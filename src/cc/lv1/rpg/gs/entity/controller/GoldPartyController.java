package cc.lv1.rpg.gs.entity.controller;

import vin.rabbit.net.AppMessage;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.i.Map;
import vin.rabbit.util.collection.impl.ArrayList;
import vin.rabbit.util.collection.impl.HashMap;
import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.data.DC;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.data.Exp;
import cc.lv1.rpg.gs.data.PressureTest;
import cc.lv1.rpg.gs.entity.ext.Bag;
import cc.lv1.rpg.gs.entity.ext.Storage;
import cc.lv1.rpg.gs.entity.impl.GoldParty;
import cc.lv1.rpg.gs.entity.impl.Goods;
import cc.lv1.rpg.gs.entity.impl.Mail;
import cc.lv1.rpg.gs.entity.impl.NPC;
import cc.lv1.rpg.gs.entity.impl.PartyReward;
import cc.lv1.rpg.gs.entity.impl.Player;
import cc.lv1.rpg.gs.entity.impl.RankReward;
import cc.lv1.rpg.gs.entity.impl.Reward;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsProp;
import cc.lv1.rpg.gs.gui.MainFrame;
import cc.lv1.rpg.gs.net.SMsg;
import cc.lv1.rpg.gs.net.impl.GoldPartyJob;
import cc.lv1.rpg.gs.net.impl.PartyRewardJob;
import cc.lv1.rpg.gs.net.impl.PressureJob;
import cc.lv1.rpg.gs.net.impl.SaveJob;
import cc.lv1.rpg.gs.other.ErrorCode;
import cc.lv1.rpg.gs.util.FontConver;

/**
 * 黄金斗士活动控制器
 * @author bean
 *
 */
public class GoldPartyController extends PartyController
{
	/** 展示形象的房间 */
	public static final int SHOWIMAGEROOM = 1013430101;
	/** 胜利后需要加的等待状态 */
	public static final int STATEGOODS = 1045000251;
	/** 休息时间10秒(失败胜利都是) */
	public static final int RESETTIME = 7 * 1000;
	/** 退回到上一级房间后的休息时间 */
	public static final int RETURNTIME = 20 * 1000;
	
	public static final int PARTYINITROOM = DataFactory.INITROOM;
	public static final int GOLDREWARDRANK = 50;
	public static final int SHOWIMAGERANK = 5;
	/**
	 * 战斗总次数限制
	 */
	public static final int BATTLECOUNT = 90;
	
	/**
	 * 几点开始  离24点 倒数几小时开始 4则为24-4  20点开始
	 */ 
	private final int START_TIME = 4*HOUR;
	
	/**
	 * 0星期天 1 - 6 是星期一到星期六  活动日期
	 */
	private boolean [] PARTY_AT_WEKDAY = {false,false,false,true,false,false,false};
	
	/**
	 * 活动记录相关
	 */
	private HashMap battlePoint = new HashMap(200);
	
	/** 上一届前20名 */
	private List preTopPlayers = new ArrayList(20);
	
	/**
	 * 只用来记录玩家的勇气积分(所有参加活动的)
	 */
	private Map pointMap = new HashMap();
	
	private static GoldPartyController gpc;
	
	private GoldPartyController()
	{
	}
	
	public static GoldPartyController getInstance()
	{
		if(gpc == null)
			gpc = new GoldPartyController();
		return gpc;
	}

	
	public void init()
	{
		cancelImageShow();
		
		playerList = new ArrayList();
		preTopPlayers = new ArrayList(GOLDREWARDRANK);
		int size = topPlayers.size()>GOLDREWARDRANK?GOLDREWARDRANK:topPlayers.size();
		for (int i = 0; i < size; i++) 
		{
			preTopPlayers.add(topPlayers.get(i));
		}
		topPlayers = new ArrayList(GOLDREWARDRANK); //创建排名
		//key=玩家ID value=当前总的战斗次数:当前房间胜利次数:当前房间失败次数:当前到达房间(胜利的房间)
		playerPoint = new HashMap();
		battlePoint = new HashMap();
		pointMap = new HashMap();
		
		GameServer.getInstance().getJobsObserver().
		addJob(GameServer.JOB_GAME1, new GoldPartyJob(GameServer.getInstance().getWorldManager()));
	}

	
	public void update(long timeMillis)
	{
		//用boolean值得控制当天有活动没
		if(!PARTY_AT_WEKDAY[WorldManager.weekDate])
			return;

		
		if(lessBeginTime <= 0 && !isReady)
		{
			if(WorldManager.nextDayTime < START_TIME)
			{
				//当天活动结束时候
				//小于8点后开服则当天无活动
				return;
			}
			else
			{
				//计算活动开始时间
				lessBeginTime = WorldManager.nextDayTime - START_TIME;
				lessBeginTime = lessBeginTime / 1000 *1000;		
			}
		}
		
		
		if(!isStarted)
		{
			lessBeginTime -=500;
			if(lessBeginTime <= 1800000)
			{
				if(isShowImage())
				{
					cancelImageShow();
				}
			}
			//活动开始前20分钟，系统开始公告（5分钟1次）
			if(lessBeginTime <= 1200000)
			{
//				if(lessBeginTime%10000 == 0)
				if(lessBeginTime%180000 == 0)
				{
					if(!isReady)
						init();
						
					isReady = true;  //活动进入准备阶段则玩家可以进入准备房间
	
					//离黄金斗士活动开始还有CC分钟!
					if(lessBeginTime >= 180000)
					{
						GameServer.getInstance().getWorldManager().sendEveryonePost(DC.getString(DC.PARTY_13)+(lessBeginTime/60000)+DC.getString(DC.PARTY_14));
					}
				}
			}
		}

		
		if(!isReady)
			return;
		
		if(!isStarted && lessBeginTime <= 0 && partyTime <= 0)
		{
			partyTime = HOUR; //设置黄金斗士活动时间
			isStarted = true;
			isEnded = false;
			
			//黄金斗士活动开始
			GameServer.getInstance().getWorldManager().sendEveryonePost(DC.getString(DC.PARTY_15));
		}
		else
		{
			if(!isStarted)
				return;
		}
		
		partyTime -=500;
		
		if(partyTime <= 0) 
		{
			isReady = false;
			isStarted = false;
			isEnded = true;
			
			//黄金斗士活动结束
			GameServer.getInstance().getWorldManager().sendEveryonePost(DC.getString(DC.PARTY_16));
			
			endPartyNotice();
		}
	}
	
	public void endPartyNotice()
	{
		for (int i = 0; i < playerList.size(); i++) 
		{
			PlayerController player = (PlayerController) playerList.get(i);
			if(player.getParent() instanceof GoldPVPController)
			{
				GoldPVPController battle = (GoldPVPController) player.getParent();
				battle.setTimeOver(true);
			}
			else if(player.getParent() instanceof GoldPVEController)
			{
				GoldPVEController battle = (GoldPVEController) player.getParent();
				battle.setTimeOver(true);
			}
			else if(player.getParent() instanceof BusinessController)
			{
				BusinessController bc = (BusinessController) player.getParent();
				bc.removePlayer(player);
			}
			if(player.getParent() instanceof RoomController)
			{
				RoomController room = (RoomController) player.getParent();
				if(room.isGoldPartyRoom || room.isGoldPartyPKRoom)
					player.moveToRoom(GoldPartyController.PARTYINITROOM);
			}
		}
		
		//公告前X名玩家
		if(topPlayers.size() > 0)
		{
			int size = topPlayers.size()>GoldPartyController.SHOWIMAGERANK?GoldPartyController.SHOWIMAGERANK:topPlayers.size();
			StringBuffer sb = new StringBuffer();
			sb.append(DC.getString(DC.PARTY_17));
			sb.append(size);
			sb.append(DC.getString(DC.PARTY_18));
			sb.append("[");
			for (int i = 0; i < size; i++) 
			{
				Reward reward = (Reward) topPlayers.get(i);
				sb.append(reward.rank);
				sb.append(".|");
				if(i == topPlayers.size()-1)
				{
					sb.append(reward.name);
					sb.append("#u:");
					sb.append(reward.name);
					sb.append("|(");
					sb.append(reward.point);
					sb.append(DC.getString(DC.PARTY_19));
					sb.append(")");
				}
				else
				{
					sb.append(reward.name);
					sb.append("#u:");
					sb.append(reward.name);
					sb.append("|(");
					sb.append(reward.point);
					sb.append(DC.getString(DC.PARTY_19));
					sb.append("),");
				}
			}
			sb.append("],");
			sb.append(DC.getString(DC.PARTY_20));
			ByteBuffer buffer = new ByteBuffer(24);
			buffer.writeByte(12);
			buffer.writeInt(0);
			buffer.writeUTF("");
			buffer.writeUTF(sb.toString());
			GameServer.getInstance().getWorldManager().dispatchMsg(SMsg.S_CHAT_COMMAND, buffer);
		}
	}
	
	public Reward getReward(int playerId)
	{
		for (int i = 0; i < topPlayers.size(); i++)
		{
			Reward reward = (Reward) topPlayers.get(i);
			if(reward.id == playerId)
				return reward;
		}
		return null;
	}

	
	public void addPlayerPoint(PlayerController target,int value)
	{
		if(!isStarted)
			return;
		
		Object obj = playerPoint.get(target.getID());
		if(obj != null)
		{
			int point = getPlayerPoint(target) + value;
			playerPoint.put(target.getID(), getPlayerPoint(target)+value);

			boolean isIn = false;
			for (int i = 0; i < topPlayers.size(); i++) 
			{
				Reward reward = (Reward) topPlayers.get(i);
				if(reward.id == target.getID())
				{
					reward.point = point;
					reward.logTime = WorldManager.currentTime;
//					reward.level = target.getPlayer().level;
					putReward(reward);
					isIn = true;
					break;
				}
			}
			
			if(!isIn)
			{
				Reward reward = new Reward();
				reward.id = target.getID();
				reward.name = target.getName();
//				reward.level = target.getPlayer().level;
				reward.logTime = WorldManager.currentTime;
				reward.point = point;
				reward.setPlayer(target.getPlayer());
				addTopPlayer(reward);
			}
			
			for (int cur = 0; cur < topPlayers.size() - 1; cur++)
			{
				for (int next = cur + 1; next < topPlayers.size(); next++) 
				{
					Reward reward1 = (Reward)topPlayers.get(cur);
					Reward reward2 = (Reward)topPlayers.get(next);
					if (reward1.point < reward2.point) 
					{
						Reward temp = reward1;
						topPlayers.set(cur, reward2);
						topPlayers.set(next, temp);
					}
					else if(reward1.point == reward2.point)
					{
						if(reward1.logTime > reward2.logTime)
						{
							Reward temp = reward1;
							topPlayers.set(cur, reward2);
							topPlayers.set(next, temp);
						}
					}
				}
			}
			
//			//移除排行于第20名之后的玩家
//			if (topPlayers.size() > GOLDREWARDRANK) 
//			{
//				for (int i = GOLDREWARDRANK; i < topPlayers.size(); i++) 
//				{
//					topPlayers.remove(i);
//				}
//			}
			
			//设置玩家的名次
			for(int rank = 0;rank < topPlayers.size();rank++)
			{
				Reward reward = (Reward) topPlayers.get(rank);
				reward.rank = rank + 1;
			}
		}
	}

	
	public int getPlayerPoint(PlayerController target)
	{
		Object obj = playerPoint.get(target.getID());
		if(obj != null)
		{
			return Integer.parseInt(obj.toString());
		}
		return 0;
	}
	
	public Object getPlayerPoint(int playerId)
	{
		return playerPoint.get(playerId);
	}
	
	public void addPreTopPlayer(Reward reward)
	{
		preTopPlayers.add(reward);
	}
	
	public void addTopPlayer(Reward reward)
	{
		topPlayers.add(reward);
		putReward(reward);
	}
	
	private void putReward(Reward reward)
	{
		pointMap.put(reward.id, reward);
	}
	
	public int getPreTopCount()
	{
		return preTopPlayers.size();
	}
	
	public int getTopCount()
	{
		return topPlayers.size();
	}
	
	public List getPreTopPlayers()
	{
		return preTopPlayers;
	}
	
	public int getPartyDate()
	{
		if(topPlayers.size() == 0)
			return 0;
		Reward reward = (Reward) topPlayers.get(0);
		return Integer.parseInt(WorldManager.getTypeTime("dd", reward.logTime));
	}
	
	/**
	 * 加入到黄金斗士活动里面
	 */
	public void addPlayer(PlayerController target)
	{
		if(!isReady)
			return;
	
		if(isPlayerIn(target))
			return;
		
		Object obj = battlePoint.get(target.getID());
		if(obj == null)
		{
			//key=玩家ID value=当前总的战斗次数:当前房间胜利次数:当前房间失败次数:当前到达房间(胜利的房间)
			battlePoint.put(target.getID(), "0:0:0:0"); 
		}
		
		obj = playerPoint.get(target.getID());
		if(obj == null)
		{
			//key=玩家ID value=玩家的勇气积分
			playerPoint.put(target.getID(), "0"); 
			
			target.setGoldSignName("");
		}
		
		playerList.add(target);
	}
	
	
	
	/**
	 * 玩家下线时或黄金斗士活动结束时
	 */
	public void removePlayer(PlayerController target)
	{
		if(!isStarted)
			return;
//		target.getPlayer().setDefaultRoom(PARTYINITROOM);
	
		playerList.remove(target);
	}
	
	public int getRoomId(PlayerController target)
	{
		Object obj = battlePoint.get(target.getID());
		if(obj != null)
		{
			String[] strs = Utils.split(obj.toString(), ":");
			try{
				return Integer.parseInt(strs[3]);
			}catch(Exception e)
			{
				return 0;
			}
		}
		return 0;
	}
	
	public int getLoseCount(PlayerController target)
	{
		Object obj = battlePoint.get(target.getID());
		if(obj != null)
		{
			String[] strs = Utils.split(obj.toString(), ":");
			try{
				return Integer.parseInt(strs[2]);
			}catch(Exception e)
			{
				return 0;
			}
		}
		return 0;
	}
	
	public int getWinCount(PlayerController target)
	{
		Object obj = battlePoint.get(target.getID());
		if(obj != null)
		{
			String[] strs = Utils.split(obj.toString(), ":");
			try{
				return Integer.parseInt(strs[1]);
			}catch(Exception e)
			{
				return 0;
			}
		}
		return 0;
	}
	
	public int getBattleCount(PlayerController target)
	{
		Object obj = battlePoint.get(target.getID());
		if(obj != null)
		{
			return Integer.parseInt(obj.toString().substring(0,obj.toString().indexOf(":")));
		}
		return 0;
	}
	
	public void clearWinAndLoseCount(PlayerController target)
	{
		setWinCount(target, 0);
		setLoseCount(target, 0);
	}
	
	public void setBattleCount(PlayerController target,int value)
	{
		Object obj = battlePoint.get(target.getID());
		if(obj != null)
		{
			if(value > BATTLECOUNT)
				value = BATTLECOUNT;
			if(getBattleCount(target) == value)
				return;
			battlePoint.put(target.getID(), value+":"+getWinCount(target)+":"+getLoseCount(target)+":"+getRoomId(target));
				
		}
	}
	
	public void setWinCount(PlayerController target,int value)
	{
		Object obj = battlePoint.get(target.getID());
		if(obj != null)
		{
			if(getWinCount(target) == value)
				return;
			battlePoint.put(target.getID(), getBattleCount(target)+":"+value+":"+getLoseCount(target)+":"+getRoomId(target));	
		}
	}
	
	public void setLoseCount(PlayerController target,int value)
	{
		Object obj = battlePoint.get(target.getID());
		if(obj != null)
		{
			if(getLoseCount(target) == value)
				return;
			battlePoint.put(target.getID(), getBattleCount(target)+":"+getWinCount(target)+":"+value+":"+getRoomId(target));		
		}
	}
	
	public void setRoomId(PlayerController target,int value)
	{
		Object obj = battlePoint.get(target.getID());
		if(obj != null)
		{
			if(getRoomId(target) == value)
				return;
			battlePoint.put(target.getID(), getBattleCount(target)+":"+getWinCount(target)+":"+getLoseCount(target)+":"+value);
		}
	}
	
	public String getHideNpcInfo(int npcId)
	{
		for (int i = 0; i < topPlayers.size(); i++)
		{
			Reward reward = (Reward) topPlayers.get(i);
			if("".equals(reward.imageShow))
				continue;
			if(reward.imageShow.indexOf(npcId+"") != -1)
				return reward.imageShow;
		}
		return "";
	}

	
	public void setWinOrLoseInfo(PlayerController target,boolean result,GoldParty gp,BattleController battle)
	{
		if(target == null)
			return;
		if(gp == null)
			return;
		if(!isStarted())
			return;

		long resetTime = 0;
		GoldPartyController gpc = GoldPartyController.getInstance();
		if(battle instanceof GoldPVEController)
		{
			MonsterGroupController mgc = battle.getRoom().getMonsterGroupByIndex(battle.objectIndex);
			if(mgc != null)
			{
				if(mgc.rebirthTime != 0)
					gpc.setBattleCount(target, gpc.getBattleCount(target)+1);
			}
			else
				gpc.setBattleCount(target, gpc.getBattleCount(target)+1);
		}
		else
			gpc.setBattleCount(target, gpc.getBattleCount(target)+1);
		
		if(result)
		{
			if(battle instanceof GoldPVPController)
				gpc.setWinCount(target, gpc.getWinCount(target)+1);
			if(gpc.getBattleCount(target) >= GoldPartyController.BATTLECOUNT)
			{
				target.moveToRoom(GoldPartyController.PARTYINITROOM);
				gpc.clearWinAndLoseCount(target);
			}
			else
			{
				//System.out.println("胜利者:"+target.getName()+"  战斗总次数:"+gpc.getBattleCount(target)+"  胜利次数:"+gpc.getWinCount(target)+"  失败次数:"+gpc.getLoseCount(target)+"  到达房间:"+gpc.getRoomId(target));
				int type = getType(target, true,gp);
				if(type == 1)
				{
	//System.out.print(target.getName()+"的胜利条件达到!");
					GoldParty next = DataFactory.getInstance().getGoldPartyByLevel(gp.level+1);
					if(next != null)
					{
						int roomId = next.getRoomId();
						target.moveToRoom(roomId);
	//System.out.println("送到:"+roomId);
						gpc.clearWinAndLoseCount(target);
						gpc.setRoomId(target, roomId);
					}
					//胜利条件达到，送到下一级房间
				}
				resetTime = RESETTIME;
			}
			
			if(battle instanceof GoldPVPController)
				sendSpeReward(gp, target);
		}
		else
		{
			if(battle instanceof GoldPVPController)
				gpc.setLoseCount(target, gpc.getLoseCount(target)+1);
			if(gpc.getBattleCount(target) >= GoldPartyController.BATTLECOUNT)
			{
				target.moveToRoom(GoldPartyController.PARTYINITROOM);
				gpc.clearWinAndLoseCount(target);
			}
			else
			{
//				System.out.println("失败者:"+target.getName()+"  战斗总次数:"+gpc.getBattleCount(target)+"  失败次数:"+gpc.getLoseCount(target)+"  胜利次数:"+gpc.getWinCount(target));
				int type = getType(target, false,gp);
				if(type == -1)
				{
	//System.out.print(target.getName()+"的失败条件达到!");
					GoldParty pre = DataFactory.getInstance().getGoldPartyByLevel(gp.level-1);
					if(pre != null)
					{
						int roomId = pre.getRoomId();
						target.moveToRoom(roomId);
	//System.out.println("送到:"+roomId);
						gpc.clearWinAndLoseCount(target);
						gpc.setRoomId(target, roomId);
						resetTime = RETURNTIME;
					}	
				}
				else
				{
					resetTime = RESETTIME;
				}
			}
		}

		Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
		GoodsProp goods = (GoodsProp) DataFactory.getInstance().getGameObject(STATEGOODS);
		if(goods != null)
		{
			GoodsProp newGoods = (GoodsProp) Goods.cloneObject(goods);
			newGoods.objectIndex = WorldManager.currentTime!=0?WorldManager.currentTime:System.currentTimeMillis();//这里的流水用来计开始时间
			newGoods.expTimes = resetTime;
			bag.setExtGoods(3, newGoods);
			bag.sendExpBuff(target, newGoods.effect, true, (int)newGoods.expTimes);
		}
		
		setRewardInfo(gp, target, result, battle);
		
		sendBattleCount(target);
	}

	
	public int getType(PlayerController player,boolean result,GoldParty gp)
	{
		if(result)
		{
			int winCount = GoldPartyController.getInstance().getWinCount(player);
			if(winCount >= gp.winCon)
				return 1;//胜利条件达到
		}
		else
		{
			int loseCount = GoldPartyController.getInstance().getLoseCount(player);
			if(loseCount >= gp.loseCon)
				return -1;//胜利条件达到
		}
		return 0;
	}
	
	/**
	 * 基础经验
	 * @param target
1-999：=1+33831.917*(当前等级-1)
1000-1999：=33831919.5+147350.3826*(当前等级-999) 
2000-2999：=181182302.1+223462.33305*(当前等级-1999)  
3000-3999：=404644635.15+175550.88612*(当前等级-2999)
4000-4999:=580195521.27+198421.95804*(当前等级-3999)  
5000-5999:=778617479.31+177545.657746*(当前等级-4999) 
6000-6499:=956163137.056+169590.17242*(当前等级-5999)
6500-6999:1040958223.266+375351.973388*(当前等级-6499)
	 * @return
	 */
	public long baseExp(PlayerController target)
	{
		int level = target.getPlayer().level;
		long exp = 0;
		if(level <= 999)
		{
			exp = 1 + 33831 * (level - 1);
		}
		else if(level <= 1999)
		{
			exp = 33831919 + (long)147350 * (level - 999);
		}
		else if(level <= 2999)
		{
			exp = 181182302 + (long)223462 * (level - 1999);
		}
		else if(level <= 3999)
		{
			exp = 404644635 + (long)175550 * (level - 2999);
		}
		else if(level <= 4999)
		{
			exp = 580195521 + (long)198421 * (level - 3999); 
		}
		else if(level <= 5999)
		{
			exp = 778617479 + (long)177545 * (level - 4999); 
		}
		else if(level <= 6499)
		{
			exp = 956163137 + (long)169590 * (level - 5999); 
		}
		else
		{
			exp = 1040958223 + (long)375351 * (level - 6499); 
		}
		return exp;
	}
	
	/**
	 * 获取经验
	 * @param target
	 * @param result true为胜利 false为失败
	1级房间获得奖励：
	经验：胜利100%，失败50%
	金钱：胜利5金币，失败2金币
	荣誉：胜利70--68点，失败50--48点
	2级房间获得经验：
	经验： 胜利150%，失败110%
	金钱：胜利10金币，失败6金币
	荣誉：胜利100--98点，失败80--78点
	3级房间获得经验：
	经验： 胜利200%，失败160%
	金钱：胜利20金币，失败12金币
	荣誉：胜利130--128点，失败110--108点
	4级房间获得经验：
	经验： 胜利250%，失败210%
	金钱：胜利26金币，失败22金币
	荣誉：胜利160--158点，失败140--138点
	
	打房间的NPC怪物，胜利以后获得胜利奖励的90%，失败以后获得失败奖励90%
	 * @return
	 */
	public void setRewardInfo(GoldParty gp,PlayerController target,boolean isWin,BattleController battle)
	{
		long exp = baseExp(target);
		long point = 0;
		int yongqi = 0;
		if(gp.level == 1)
		{
			if(isWin)
			{
				point = 50000;
				yongqi = 70;
			}
			else
			{
				point = 20000;
				yongqi = 50;
				exp *= 0.5;
			}
		}
		else if(gp.level == 2)
		{
			if(isWin)
			{
				point = 100000;
				yongqi = 100;
				exp *= 1.5;
			}
			else
			{
				point = 60000;
				yongqi = 80;
				exp *= 1.1;
			}
		}
		else if(gp.level == 3)
		{
			if(isWin)
			{
				point = 200000;
				yongqi = 130;
				exp *= 2;
			}
			else
			{
				point = 120000;
				yongqi = 110;
				exp *= 1.6;
			}
		}
		else if(gp.level == 4)
		{
			if(isWin)
			{
				point = 260000;
				yongqi = 160;
				exp *= 2.5;
			}
			else
			{
				point = 220000;
				yongqi = 140;
				exp *= 2.1;
			}
		}
//		exp *= 1.5;//20100819增加20100826删除
		//经验卡
		if(target.getPlayer().expMultTime != 0)
		{
			Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
			Goods goods = bag.getExtGoods(2);
			if(goods != null && goods instanceof GoodsProp)
			{
				GoodsProp prop = (GoodsProp) goods;
				if(WorldManager.currentTime - prop.expTimes < target.getPlayer().expMultTime)
				{
					exp *= prop.expMult;
				}
			}
		}//20100826增加
		
		yongqi -= (int)(Math.random() * 3);
		if(battle instanceof GoldPVEController)
		{
			point *= 0.9;
			yongqi *= 0.9;
			exp *= 0.9;
			
			MonsterGroupController mgc = battle.getRoom().getMonsterGroupByIndex(battle.objectIndex);
			if(mgc != null)
			{
				if(mgc.rebirthTime == 0)
				{
					point *= 0;
					yongqi *= 0;
					exp *= 0;
				}
			}
		}
		if(exp > 0)
		{
			long disExp = target.addExp(exp, true,true,false);
			if(disExp > 0)
				target.sendGetGoodsInfo(1, false, DC.getString(DC.BATTLE_26)+": "+disExp);
		}
		if(point > 0)
		{
			target.sendGetGoodsInfo(1, false, DC.getString(DC.BATTLE_24)+": "+point);
			Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
			bag.point += point;
			bag.sendAddGoods(target, null);
		}
		if(yongqi > 0)
		{
			target.sendGetGoodsInfo(1, false, DC.getString(DC.BATTLE_25)+": "+yongqi);
			target.setHonour(yongqi);
			addPlayerPoint(target, yongqi);
		}
	}

	/**
	 * 发送活动奖励
	 */
	public void sendPartyReward(PlayerController target,RankReward rr,Reward reward)
	{
		List goodsList = new ArrayList();
		Goods[] goods = null;
		if(rr.goods1[0] != 0)
		{
			goods = DataFactory.getInstance().makeGoods(rr.goods1[0], rr.goods1[1], rr.goods1[2]);
			if(goods != null)
			{
				for (int i = 0; i < goods.length; i++) 
				{
					if(goods[i] == null)
						continue;
					goodsList.add(goods[i]);
				}
			}
		}
		if(rr.goods2[0] != 0)
		{
			goods = DataFactory.getInstance().makeGoods(rr.goods2[0], rr.goods2[1], rr.goods2[2]);
			if(goods != null)
			{
				for (int i = 0; i < goods.length; i++) 
				{
					if(goods[i] == null)
						continue;
					goodsList.add(goods[i]);
				}
			}
		}
		if(rr.goods3[0] != 0)
		{
			goods = DataFactory.getInstance().makeGoods(rr.goods3[0], rr.goods3[1], rr.goods3[2]);
			if(goods != null)
			{
				for (int i = 0; i < goods.length; i++) 
				{
					if(goods[i] == null)
						continue;
					goodsList.add(goods[i]);
				}
			}
		}
		if(rr.goods4[0] != 0)
		{
			goods = DataFactory.getInstance().makeGoods(rr.goods4[0], rr.goods4[1], rr.goods4[2]);
			if(goods != null)
			{
				for (int i = 0; i < goods.length; i++) 
				{
					if(goods[i] == null)
						continue;
					goodsList.add(goods[i]);
				}
			}
		}
		
		Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
		if(!bag.isCanAddGoodsList(goodsList))
		{
			target.sendAlert(ErrorCode.ALERT_BAG_IS_FULL);
			return;
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append(WorldManager.getTypeTime("yyyy-MM-dd HH:mm:ss", WorldManager.currentTime));
		sb.append("    player:");
		sb.append(target.getName());
		sb.append("    reward:{");
		sb.append("money[");
		sb.append(rr.money);
		sb.append("]  point:[");
		sb.append(rr.point);
		sb.append("]");
		bag.money += rr.money;
		bag.point += rr.point;
		
		sb.append("  goods:");
		StringBuffer sb1 = new StringBuffer();
		for (int i = 0; i < goodsList.size(); i++)
		{
			Goods g = (Goods) goodsList.get(i);
			sb1.append("[");
			sb1.append(g.name);
			sb1.append("x");
			sb1.append(g.goodsCount);
			sb1.append("]");
			
			bag.sendAddGoods(target, g);
		}
		sb.append(sb1);
		target.sendGetGoodsInfo(1, false, DC.getString(DC.PARTY_21)+": "+sb1.toString());
		reward.isGetReward = true;
//		PressureTest.getInstance().saveTextByFileName(sb.toString(), "goldParty");
		GameServer.getInstance().getJobsObserver().addJob(GameServer.JOB_GAME2, new PressureJob(sb.toString(),"goldParty"));
		sb = null;
	}
	
	/**
	 * 发送战斗特殊奖励 胜利才能获得
	 * @param gp
	 */
	public void sendSpeReward(GoldParty gp,PlayerController target)
	{
		if(target != null)
		{
			for (int i = 0; i < gp.spRewards.length; i++)
			{
				int rate = Integer.parseInt(gp.rates[i]);
				String[] strs  = Utils.split(gp.spRewards[i], ",");
				int[] goodsIds = new int[strs.length];
				int[] goodsCounts = new int[strs.length];
				for (int j = 0; j < goodsIds.length; j++)
				{
					String[] ss = Utils.split(strs[j], ":");
					goodsIds[j] = Integer.parseInt(ss[0]);
					goodsCounts[j] = Integer.parseInt(ss[1]);
				}
				int random = (int) (Math.random() * 10000);
				if(random <= rate)
				{
					List list = new ArrayList();
					for (int j = 0; j < goodsIds.length; j++)
					{
						if(goodsIds[j] == 0)
							continue;
						Goods[] goodsList = DataFactory.getInstance().makeGoods(goodsIds[j],goodsCounts[j]);
						for (int k = 0; k < goodsList.length; k++)
						{
							if(goodsList[k] == null)
								continue;
							list.add(goodsList[k]);
						}
					}
					if(list.size() > 0)
					{
						int r = (int) (Math.random() * list.size());
						Goods goods = (Goods) list.get(r);
						Mail mail = new Mail(DC.getString(DC.PARTY_22));
						mail.setTitle(DC.getString(DC.PARTY_23));
						mail.setContent(DC.getString(DC.PARTY_24));
						mail.addAttach(goods);
						mail.send(target);
						
						StringBuffer sb = new StringBuffer();
						if(i == 0)
						{//XXX在竞技中展现了更快、更高、更强的斗士精神，感动了女神，获得***奖励！
//							sb.append("|");
							sb.append(target.getName());
							sb.append("#u:");
							sb.append(target.getName());
							sb.append("|");
							sb.append(DC.getString(DC.PARTY_25));
							sb.append("|[");
							sb.append(goods.name);
							sb.append("]#p:");
							sb.append(objectIndex);
							sb.append(":");
							sb.append(goods.quality);
							sb.append(":");
							sb.append(target.getID());
						}
						else if(i == 1)
						{//XXX正在角落玩躲猫猫，却被一颗流弹击中，低头一看，竟然是****
							sb.append(target.getName());
							sb.append("#u:");
							sb.append(target.getName());
							sb.append("|");
							sb.append(DC.getString(DC.PARTY_26));
							sb.append("|[");
							sb.append(goods.name);
							sb.append("]#p:");
							sb.append(objectIndex);
							sb.append(":");
							sb.append(goods.quality);
							sb.append(":");
							sb.append(target.getID());
						}
						target.sendGetGoodsInfo(3, true, sb.toString());
						sb = null;
						break;
					}
				}
			}
		}
	}
	
	public void showRewardAndImage(PlayerController target,ByteBuffer inBuffer)
	{
		if(isReady)
		{
			target.sendAlert(ErrorCode.ALERT_GOLD_PARTY_ISNOT_END);
			return;
		}
		Reward reward = getReward(target.getID());
		if(reward == null)
		{
			target.sendAlert(ErrorCode.ALERT_PLAYER_ISNOT_SHOWIMAGE);
			return;
		}
		if(reward.rank > SHOWIMAGERANK)
		{	
			target.sendAlert(ErrorCode.ALERT_PLAYER_ISNOT_SHOWIMAGE);
			return;
		}
		if(reward.isGetReward)
		{	
			target.sendAlert(ErrorCode.ALERT_PLAYER_IS_GET_REWARDED);
			return;
		}
		RankReward rr = DataFactory.getInstance().getRankReward(4, reward.rank);
		if(rr == null)
		{
			System.out.println("GoldPartyController showRewardAndImage rankReward is null:"+target.getName());
			return;
		}
		int type = inBuffer.readByte();
		if(type == 1)//查询状态,需要发送将获得的奖品给客户端显示
		{
			ByteBuffer buffer = new ByteBuffer();
			buffer.writeByte(1);
			Goods goods = (Goods) DataFactory.getInstance().getGameObject(rr.goods1[0]);
			if(goods != null)
			{
				buffer.writeInt(rr.goods1[0]);
				buffer.writeUTF(goods.name);
				buffer.writeInt(rr.goods1[1]);
			}
			if(rr.goods2[0] != 0)
			{
				goods = (Goods) DataFactory.getInstance().getGameObject(rr.goods2[0]);
				if(goods != null)
				{
					buffer.writeInt(rr.goods2[0]);
					buffer.writeUTF(goods.name);
					buffer.writeInt(rr.goods2[1]);
				}
			}
			if(rr.goods3[0] != 0)
			{
				goods = (Goods) DataFactory.getInstance().getGameObject(rr.goods3[0]);
				if(goods != null)
				{
					buffer.writeInt(rr.goods3[0]);
					buffer.writeUTF(goods.name);
					buffer.writeInt(rr.goods3[1]);
				}
			}
			if(rr.goods4[0] != 0)
			{
				goods = (Goods) DataFactory.getInstance().getGameObject(rr.goods4[0]);
				if(goods != null)
				{
					buffer.writeInt(rr.goods4[0]);
					buffer.writeUTF(goods.name);
					buffer.writeInt(rr.goods4[1]);
				}
			}
			target.getNetConnection().sendMessage(new SMsg(SMsg.S_GOLD_PARTY_REWARD_SHOW_COMMAND,buffer));
		}
		else if(type == 2)//领取并展示形象
		{
			sendPartyReward(target,rr,reward);
			
			int image = target.getPlayer().modelMotionId;
			NpcController[] npcs = target.getRoom().getNpcList();
			NPC npc = null;
			for (int i = 0; i < npcs.length; i++) 
			{
				String str = getHideNpcInfo(npcs[i].getID());
				if("".equals(str) && "hide".equals(npcs[i].getNpc().script) && npcs[i].getNpc().rank == reward.rank)
				{
					reward.imageShow = npcs[i].getID()+":"+image+":"+target.getName();
					npcs[i].getNpc().name = target.getName();
					npcs[i].getNpc().modelId = image;
					npc = npcs[i].getNpc();
					break;
				}
			}
			if(npc == null)
				return;
			ByteBuffer buffer = new ByteBuffer();
			buffer.writeByte(2);
			buffer.writeBoolean(true);//需要显示的
			npc.writeBaseTo(buffer);
			target.getRoom().dispatchMsg(SMsg.S_GOLD_PARTY_REWARD_SHOW_COMMAND, buffer);
		}
	}
	
	public boolean isShowImage()
	{
		for (int i = 0; i < topPlayers.size(); i++)
		{
			Reward reward = (Reward) topPlayers.get(i);
			if(!"0:0:0".equals(reward.imageShow))
				return true;
		}
		return false;
	}
	
	
	public void cancelImageShow()
	{
		if(topPlayers.size() == 0)
			return;
		RoomController room = GameServer.getInstance().getWorldManager().getRoomWolrd(SHOWIMAGEROOM);
		if(room == null)
			return;
		for (int i = 0; i < topPlayers.size(); i++)
		{
			Reward reward = (Reward) topPlayers.get(i);
			reward.imageShow = "0:0:0";
		}
		NpcController[] npcs = room.getNpcList();
		ByteBuffer buffer = new ByteBuffer();
		int leng = 0;
		for (int i = 0; i < npcs.length; i++) 
		{
			if("hide".equals(npcs[i].getNpc().script))
				leng++;
		}
		buffer.writeByte(2);
		buffer.writeBoolean(false);//需要隐藏的
		buffer.writeInt(leng);
		for (int i = 0; i < npcs.length; i++) 
		{
			if("hide".equals(npcs[i].getNpc().script))
			{
				buffer.writeInt(npcs[i].getID());
			}
		}
		room.dispatchMsg(SMsg.S_GOLD_PARTY_REWARD_SHOW_COMMAND, buffer);
	}
	
	public void sendGoldRankInfo(PlayerController target)
	{
		ByteBuffer buffer = new ByteBuffer();
		/********************yestoday********************************/
		int size = preTopPlayers.size()>GOLDREWARDRANK?GOLDREWARDRANK:preTopPlayers.size();
		buffer.writeByte(size);
		String str = "";
		for (int i = 0; i < size; i++)
		{
			if(i == GOLDREWARDRANK)
				break;
			Reward reward = (Reward) preTopPlayers.get(i);
			buffer.writeInt(reward.rank);
			buffer.writeUTF(reward.name);
			Player player = reward.getPlayer();
			if(player == null)
				player = GameServer.getInstance().getDatabaseAccessor().getPlayer(reward.id);
			if(player == null)
			{
				buffer.writeInt(0);
				buffer.writeByte(0);
			}
			else
			{
				buffer.writeInt(player.level);
				buffer.writeByte(player.upProfession);
			}
			buffer.writeInt(reward.point);
			buffer.writeUTF(WorldManager.getTypeTime("MM.dd HH:mm", reward.logTime));
		}
		
		/*******************today**************************/
		size = topPlayers.size()>GOLDREWARDRANK?GOLDREWARDRANK:topPlayers.size();
		buffer.writeByte(size);
		str = "";
		for (int i = 0; i < size; i++)
		{
			if(i == GOLDREWARDRANK)
				break;
			Reward reward = (Reward) topPlayers.get(i);
			buffer.writeInt(reward.rank);
			buffer.writeUTF(reward.name);
			Player player = reward.getPlayer();
			if(player == null)
				player = GameServer.getInstance().getDatabaseAccessor().getPlayer(reward.id);
			if(player == null)
			{
				buffer.writeInt(0);
				buffer.writeByte(0);
			}
			else
			{
				buffer.writeInt(player.level);
				buffer.writeByte(player.upProfession);
			}
			buffer.writeInt(reward.point);
			buffer.writeUTF(WorldManager.getTypeTime("MM.dd HH:mm", reward.logTime));
			if(str.isEmpty())
				str = WorldManager.getTypeTime("yyyy-MM-dd", reward.logTime);
		}
		
		if(str.isEmpty())
			str = WorldManager.getTypeTime("yyyy-MM-dd", WorldManager.currentTime);
		
		/*******************myself**************************/
		Reward self = (Reward) pointMap.get(target.getID());
		if(self == null)
		{
			buffer.writeInt(0);
			buffer.writeInt(0);
		}
		else
		{
			buffer.writeInt(self.rank);
			buffer.writeInt(self.point);
		}
		buffer.writeUTF(str);
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_GOLD_PARTY_BATTLE_RANK_COMMAND,buffer));
	}
	
	private void signName(PlayerController target,ByteBuffer inBuffer)
	{
		if(isReady)
		{
			return;
		}
		int npcId = inBuffer.readInt();
		NpcController npc = target.getRoom().getNpc(npcId);
		if(npc == null)
			return;
		if(!"hide".equals(npc.getNpc().script))
		{
			return;
		}
		Reward reward = getReward(target.getID());
		if((reward != null && reward.rank <= 5) || target.isGoldSignName(npc.getID()))
		{
			String rank = "";
			if(npc.getNpc().rank == 1)
				rank = DC.getString(DC.PARTY_27);
			else if(npc.getNpc().rank == 2)
				rank = DC.getString(DC.PARTY_28);
			else if(npc.getNpc().rank == 3)
				rank = DC.getString(DC.PARTY_29);
			else if(npc.getNpc().rank == 4)
				rank = DC.getString(DC.PARTY_30);
			else if(npc.getNpc().rank == 5)																																																																											
				rank = DC.getString(DC.PARTY_31);
			target.sendError(DC.getGoldPartyStr(rank));
			return;
		}
		if(npc.getNpc().rank > 5)
			return;
		
		int[] signGoods = {0,1045000300,1045000301,1045000302,1045000303,1045000304};
		Goods[] goods = DataFactory.getInstance().makeGoods(signGoods[npc.getNpc().rank]);
		if(goods == null || goods[0] == null)
		{
			System.out.println("GoldPartyController signName goodsId error:"+signGoods[npc.getNpc().rank]);
			return;
		}
		Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
		if(!bag.isCanAddGoodsToBag(goods[0]))
		{
			target.sendAlert(ErrorCode.ALERT_BAG_IS_FULL);
			return;
		}
		target.sendError(DC.getString(DC.PARTY_33)+"["+npc.getName()+"]"+DC.getString(DC.PARTY_34));
		
		bag.sendAddGoods(target, goods[0]);
		target.sendGetGoodsInfo(1, false, DC.getString(DC.PARTY_35));
		
		ByteBuffer buffer = new ByteBuffer(1);
		buffer.writeBoolean(true);
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_GOLD_PARTY_SIGN_NAME_COMMAND,buffer));

		target.setGoldSignName(npc.getID()+"");
	}
	
	public void sendBattleCount(PlayerController target)
	{
		ByteBuffer buffer = new ByteBuffer();
		buffer.writeInt(getWinCount(target));
		buffer.writeInt(getLoseCount(target));
		buffer.writeInt(getBattleCount(target));
		buffer.writeInt(BATTLECOUNT);
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_GOLD_PARTY_BATTLE_COUNT_COMMAND,buffer));
	}
	
	/**
	 * 接收家族活动内消息
	 * 
	 * @param msg
	 */
	public void clientMessageChain(PlayerController target,AppMessage msg)
	{
		int type = msg.getType();
		
		if(type == SMsg.C_GOLD_PARTY_REWARD_SHOW_COMMAND)
		{
			showRewardAndImage(target,msg.getBuffer());
		}
		else if(type == SMsg.C_GOLD_PARTY_BATTLE_RANK_COMMAND)
		{
			sendGoldRankInfo(target);
		}
		else if(type == SMsg.C_GOLD_PARTY_SIGN_NAME_COMMAND)
		{
			signName(target,msg.getBuffer());
		}
		else
		{
			super.clientMessageChain(target,msg);
		}

	}
}
