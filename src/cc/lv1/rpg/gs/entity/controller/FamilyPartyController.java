package cc.lv1.rpg.gs.entity.controller;

import vin.rabbit.net.AppMessage;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.impl.ArrayList;
import vin.rabbit.util.collection.impl.HashMap;
import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.data.DC;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.data.PressureTest;
import cc.lv1.rpg.gs.entity.ext.MailBox;
import cc.lv1.rpg.gs.entity.impl.Goods;
import cc.lv1.rpg.gs.entity.impl.Mail;
import cc.lv1.rpg.gs.entity.impl.PartyReward;
import cc.lv1.rpg.gs.entity.impl.Player;
import cc.lv1.rpg.gs.entity.impl.RankReward;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsProp;
import cc.lv1.rpg.gs.gui.MainFrame;
import cc.lv1.rpg.gs.net.SMsg;
import cc.lv1.rpg.gs.net.impl.PartyRewardJob;
import cc.lv1.rpg.gs.net.impl.PressureJob;
import cc.lv1.rpg.gs.util.FontConver;
/**
 * 家族活动控制器
 * @author dxw
 *
 */
public class FamilyPartyController extends PartyController
{
	/** 最大限制次数(从休息室到PK场) */
	public static final int MAXLOSECOUNT = 99999999;
	/** 胜利方获得荣誉值(每个人) */
	public static final int WINHONOUR = 50;
	/** 失败方获得荣誉值(每个人) */
	public static final int LOSEHONOUR = 10;
	/**
	 * 几点开始  离24点 倒数几小时开始 4则为24-4  20点开始
	 */
	private final int START_TIME = 4*HOUR;
	
	/**
	 * 0星期天 1 - 6 是星期一到星期六  活动日期  增加星期三也开
	 */
	protected boolean [] PARTY_AT_WEKDAY = {false,false,false,false,false,false,true};
//	protected boolean [] PARTY_AT_WEKDAY = {true,true,true,true,true,true,true};
	/**
	 * 家族点数
	 */
	private HashMap familyPoint = new HashMap(200);
	
	/**
	 * 家族排名
	 */
	private List topFamilys  = new ArrayList(20);
	
	private static FamilyPartyController fpc = null;
	
	private FamilyPartyController()
	{
	}
	
	public static FamilyPartyController getInstance()
	{
		if(fpc == null)
			fpc= new FamilyPartyController();
		return fpc;
	}
	
	
	public void init()
	{
		familyPoint = new HashMap(200); //创建暂存名单
		topFamilys = new ArrayList(20);
		
		super.init();
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
			//活动开始前30分钟，系统开始公告（5分钟1次）
			if(lessBeginTime <= 1800000)
			{
//				if(lessBeginTime%10000 == 0)
				if(lessBeginTime%300000 == 0)
				{
					if(!isReady)
						init();
					
					isReady = true;  //活动进入准备阶段则玩家可以进入准备房间
					
					
					//家族活动即将开始
//					if(lessBeginTime >= 10000)
					if(lessBeginTime >= 300000)
					{
						GameServer.getInstance().getWorldManager().sendEveryonePost(DC.getString(DC.PARTY_1));//家族活动即将开始,大家可以通过城镇传送进准备房间哦
					}
				}
			}
		}

		
		if(!isReady)
			return;
		
		if(!isStarted && lessBeginTime <= 0 && partyTime <= 0)
		{
			partyTime = HOUR;
			
			isStarted = true;
			isEnded = false;
			
			//家族活动开始
			GameServer.getInstance().getWorldManager().sendEveryonePost(DC.getString(DC.PARTY_2));
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
			
			//家族活动结束
			GameServer.getInstance().getWorldManager().sendEveryonePost(DC.getString(DC.PARTY_3));
			
			//通知家族中的玩家活动结束
			//胜利方获得该阵营所有人员的荣誉增加50%。 用一个job 
			GameServer.getInstance().getJobsObserver().addJob(GameServer.JOB_GAME2, new PartyRewardJob(this,(PartyReward) topFamilys.get(0)));	
		}
		else if(isStarted)
		{
			if(partyTime%300000 == 0)
			{
				//GameServer.getInstance().getWorldManager().sendEveryonePost(("家族活动还剩下"+partyTime/(1000*60))+"分钟,"+"家族活动正在火热进行中 !!!!!");
				GameServer.getInstance().getWorldManager().sendEveryonePost((DC.getString(DC.PARTY_4)+partyTime/(1000*60))+DC.getString(DC.PARTY_5));
			}
		}
	}
	

	
	/**
	 * 加点数
	 * @param target
	 * @param point
	 * @return
	 */
	public boolean addPoint(PlayerController target,int point)
	{
		if(!isStarted)
			return false;
		
		
		if(target.getFamily() == null)
			return false;
		
		StringBuffer sb = null;
		
		if(familyPoint != null)
		{
			sb = new StringBuffer();
			sb.append(target.getFamily().id);
			sb.append(":");
			sb.append(target.getFamily().name);
			
			Object obj = familyPoint.get(sb.toString());
			if(obj != null)
			{
				long value = Long.parseLong(obj.toString()) + point;
				familyPoint.put(sb.toString(), value);
				
//				System.out.println("加入排行！"+target.getName());
				setRank(target, topFamilys, 2, value);
			}
		}
		
		//加入到家族排行
		
		super.addPlayerPoint(target, point);
		
		return true;
	}
	
	
	/**
	 * 加入到家族活动里面
	 */
	public void addPlayer(PlayerController target)
	{
		if(target.getFamily() == null)
			return;
		
		if(!isReady)
			return;

		if(familyPoint == null)
			return;

		StringBuffer sb = new StringBuffer();
		sb.append(target.getFamily().id);
		sb.append(":");
		sb.append(target.getFamily().name);
		
		//家族名字没在活动中是时，创建
		Object obj = familyPoint.get(sb.toString());
		if(obj == null)
		{
			familyPoint.put(sb.toString(), 0);
			
			topTeamPlayers.put(sb.toString(), new ArrayList());
		}
			
		super.addPlayer(target);
	}
	
	
	/**
	 * 玩家下线时 家族活动结束时
	 */
	public void removePlayer(PlayerController target)
	{
		if(target.getFamily() == null)
			return;

		if(!isStarted)
			return;
		
		super.removePlayer(target);
	}
	
	
	public String[] getRewardList(FamilyController family)
	{
		List list = new ArrayList();
		String[] strs;
		for (int i = 0; i < family.familyNameList.size(); i++) 
		{
			String name = family.familyNameList.get(i).toString();
			if(playerPoint.get(name) != null)
			{
				list.add(name+":"+playerPoint.get(name));
			}
		}

		//排序
		quickSort(list);
		
		strs = new String[list.size()];
		for (int i = 0; i < list.size(); i++)
		{
			String[] string = Utils.split(list.get(i).toString(), ":");
			strs[i] = string[0];
		}
		
		return strs;
	}
	

	/**
	 * 发送活动奖励
	 */
	public void sendPartyReward()
	{
		StringBuffer buffer = new StringBuffer();
		//家族奖励是否发放
		int[] isFamilyReward = new int[topFamilys.size()];
		HashMap map = new HashMap();//记录是否都发了荣誉值兑换卡
		for (int k = 0; k < topFamilys.size(); k++) 
		{
			double honorMult = 0;
			if(k == 0)
			{
				honorMult = 1;
			}
			else if(k == 1)
			{
				honorMult = 0.5;
			}
			else if(k == 2)
			{
				honorMult = 0.2;
			}
			else
				break;
			PartyReward reward = (PartyReward) topFamilys.get(k);
			FamilyController family = GameServer.getInstance().getWorldManager().getFamilyByName(reward.name);
			if(family == null)
			{
				MainFrame.println("FamilyPartyController family is null:"+reward.name);
				continue;
			}
			
			List list = new ArrayList();
			for (int i = 0; i < family.familyNameList.size(); i++)
			{
				String name = family.familyNameList.get(i).toString();
				if(playerPoint.get(name) != null)
				{
					Mail mail = new Mail(DC.getString(DC.PARTY_6));//家族活动官
					mail.setTitle("*"+DC.getString(DC.PARTY_7));//家族活动荣誉奖励
					mail.setContent(DC.getString(DC.PARTY_8));//荣誉值奖励
					Goods g = (Goods) DataFactory.getInstance().getGameObject(HONORPROP);//荣誉值道具
					if(g == null)
					{
						MainFrame.println("FamilyPartyController goods is null:"+HONORPROP);
						continue;
					}
					Goods newGoods = (Goods) Goods.cloneObject(g);
					newGoods.objectIndex = GameServer.getInstance().getDatabaseAccessor().getGoodsObjIndex();
					String[] pPoints = Utils.split(playerPoint.get(name).toString(), ":");
					if(pPoints[0].equals("0"))
					{
						list.add(name+":"+3);//玩家的荣誉值为0
						continue;
					}
					((GoodsProp)newGoods).expPoint = (int) (Integer.parseInt(pPoints[0]) * honorMult);
					mail.addAttach(newGoods);
					PlayerController target = family.getPlayer(name);
					if(target == null)
					{
						Player player = GameServer.getInstance().getDatabaseAccessor().getPlayerByPlayerName(name);
						if(player != null)
						{
							mail.sendOffLine(player);
							list.add(name+":"+0);//不在线发的
							buffer.append("["+player.name+"] honorProp_offLine_send_success!"+Utils.LINE_SEPARATOR);
						}
						else
						{
							MainFrame.println("FamilyPartyController player is null:"+name);
							list.add(name+":"+2);//数据库也取不到玩家
						}
					}
					else
					{
						mail.send(target);
						list.add(name+":"+1);//发奖时玩家在线
						buffer.append("["+target.getName()+"] honorProp_onLine_send_success!"+Utils.LINE_SEPARATOR);
					}
					
					if(isFamilyReward[k] != 1)
						isFamilyReward[k] = 1;
				}
				else
				{
					list.add(name+":"+4);
				}
			}
			map.put(family.name, list);//记录是否都发了荣誉值兑换卡
			
			if(k == 0)
			{
				ArrayList firstList = (ArrayList) topTeamPlayers.get(family.id+":"+family.name);
					//第一名家族成员的奖励是否发放 1表示发放了 2表示有问题
//				int[] isSendReward = new int[firstList.size()];
				String[] isSendReward = new String[firstList.size()];
				for (int i = 0; i < firstList.size(); i++) 
				{
					PartyReward pr = (PartyReward) firstList.get(i);
					RankReward rr = DataFactory.getInstance().getRankReward(3, pr.rank<5?pr.rank:4);
					if(rr == null)
					{
						System.out.println("familyReward rr is null:"+pr.name);
						continue;
					}
					Mail mail = new Mail(DC.getString(DC.PARTY_6));//家族活动官
					mail.setTitle("*"+DC.getString(DC.PARTY_9));//家族活动排名奖励
					StringBuffer sb = new StringBuffer();
					sb.append(DC.getString(DC.PARTY_10));
					sb.append(",");
					sb.append(DC.getString(DC.PARTY_11));
					sb.append("[");
					sb.append(i+1);
					sb.append("]");
					sb.append(DC.getString(DC.PARTY_12));
					mail.setContent(sb.toString());////恭喜你所在家族取得家族活动的第一名,你在家族中排名第["+(i+1)+"],特此奖励!
					if(rr.point > 0)
						mail.setPoint(rr.point);
					if(rr.money > 0)
						mail.setMoney(rr.money);
					if(rr.goods1[0] != 0)
					{
						Goods[] goods = DataFactory.getInstance().makeGoods(rr.goods1[0], rr.goods1[1], rr.goods1[2]);
						if(goods[0] != null)
						{
							mail.addAttach(goods[0]);
						}
					}
					if(rr.goods2[1] != 0)
					{
						Goods[] goods = DataFactory.getInstance().makeGoods(rr.goods2[0], rr.goods2[1], rr.goods2[2]);
						if(goods[0] != null)
						{
							mail.addAttach(goods[0]);
						}
					}

					if(mail.getAttachCount() > 0)
					{
						PlayerController target = GameServer.getInstance().getWorldManager().getPlayer(pr.name);
						if(target == null)
						{
							Player player = GameServer.getInstance().getDatabaseAccessor().getPlayerByPlayerName(pr.name);
							if(player != null)
							{
								mail.sendOffLineCacheWithAccountName(player.accountName);
								isSendReward[i] = "offLine_send_success!";
								buffer.append("["+player.name+"]  rank:["+rr.rank+"] firstFamilyReward_offLine_send_success!"+Utils.LINE_SEPARATOR);
							}
							else
							{
								MainFrame.println("FamilyPartyController firstFamily player is null:"+pr.name);
								isSendReward[i] = "offLine_send_error!";
							}
						}
						else
						{
							mail.send(target);
							isSendReward[i] = "online_send_success!";
							buffer.append("["+target.getName()+"]  rank:["+rr.rank+"] firstFamilyReward_onLine_send_success!"+Utils.LINE_SEPARATOR);
						}	
					}
					else
					{
						isSendReward[i] = "goods_is_null!";
					}
				}
				
				if(firstList != null && firstList.size() == isSendReward.length)
				{
					PressureTest.getInstance().saveData(firstList,"familyMemberRank",null,family.name,map,isSendReward);
				}
				else
				{
					MainFrame.println("********"+WorldManager.getTypeTime("yyyy-MM-dd HH:mm:ss", WorldManager.currentTime)+"************");
					MainFrame.println("FamilyPartyController sendPartyReward error!");
					if(firstList != null)
						MainFrame.println("list size:"+firstList.size()+"  isSendReward length:"+isSendReward.length);
					MainFrame.println("********************");
				}
			}
		}
	
		////奖励发完后把在活动中的玩家全部送回城

		for (int i = 0; i < playerList.size(); i++)
		{
			PlayerController player = (PlayerController) playerList.get(i);
			if(player == null || !player.isOnline())
				continue;
			if(player.getParent() instanceof BattleController)
				continue;
			if(player.getRoom().isPartyPKRoom() || player.getRoom().isPartyRoom())
				player.moveToRoom(DataFactory.INITROOM);
		}
		 
		PressureTest.getInstance().saveData(topFamilys,"familyRank",isFamilyReward,"familyRank",map,null);
		GameServer.getInstance().getJobsObserver().addJob(GameServer.JOB_GAME2, new PressureJob(buffer.toString(),"familyPartyRewardSendInfo"));
//		PressureTest.getInstance().saveTextByFileName(buffer.toString(), "familyPartyRewardSendInfo");
		
		Mail.sendCacheMails();
		
//		MainFrame.println("end.........."+WorldManager.getTypeTime("yyyy-MM-dd HH:mm:ss", WorldManager.currentTime)+".........");
	}
	
	private void sendTopFamily(PlayerController target)
	{
		ByteBuffer buffer = new ByteBuffer();
		int size = topFamilys==null?0:topFamilys.size();
		buffer.writeByte(size);
//		System.out.println("家族排行："+size);
		String str = "";
		for (int i = 0; i < size; i++)
		{
			PartyReward reward = (PartyReward) topFamilys.get(i);
			buffer.writeInt(reward.rank);
			buffer.writeInt(reward.id);
			buffer.writeUTF(reward.name);
			buffer.writeUTF(reward.leaderName);
			buffer.writeInt(reward.playerCount);
			buffer.writeUTF(reward.honorPoint+"");
			if(str.isEmpty())
				str = WorldManager.getTypeTime("yyyy-MM-dd", reward.logTime);
		}
		if(str.isEmpty())
			str = WorldManager.getTypeTime("yyyy-MM-dd", WorldManager.currentTime);
		buffer.writeUTF(str);
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_GET_PARTY_FAMILY_REWARD_COMMAND,buffer));
	}
	
	
	private void sendFamilyTopPlayer(PlayerController target,AppMessage msg)
	{
		if(topTeamPlayers == null)
			return;
		int type = msg.getBuffer().readByte();
		if(type == 1)
		{
			if(target.getFamily() == null)
				return;
			sendFamilyPlayerTop(target,target.getFamily().id,target.getFamily().name);
		}
		else if(type == 2)
		{
			int familyId = msg.getBuffer().readInt();
			String familyName = msg.getBuffer().readUTF();
			sendFamilyPlayerTop(target,familyId,familyName);
		}
	}
	
	
	/**
	 * 根据家族查询家族内所有成员的排行
	 * @param family
	 */
	private void sendFamilyPlayerTop(PlayerController target,int familyId,String familyName)
	{
		ArrayList list = (ArrayList) topTeamPlayers.get(familyId+":"+familyName);
	
		if(list == null)
			return;
		
		ByteBuffer buffer = new ByteBuffer();
		int size = list.size();
		buffer.writeInt(size);
//		System.out.println("家族排行："+size);
		long honor = 0;
		String str = "";
		for (int i = 0; i < size; i++)
		{
			PartyReward reward = (PartyReward) list.get(i);
			buffer.writeInt(reward.rank);
			buffer.writeUTF(reward.name);
			buffer.writeUTF(reward.honorPoint+"");
			honor += reward.honorPoint;
			if(str.isEmpty())
				str = WorldManager.getTypeTime("yyyy-MM-dd", reward.logTime);
		}
		if(str.isEmpty())
			str = WorldManager.getTypeTime("yyyy-MM-dd", WorldManager.currentTime);
		
		buffer.writeUTF(str); 
		buffer.writeUTF(familyName);
		buffer.writeUTF(honor+"");
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_GET_PLAYER_FAMILY_HONOUR_COMMAND,buffer));
	}

	
	/**
	 * 接收家族活动内消息
	 * 
	 * @param msg
	 */
	public void clientMessageChain(PlayerController target,AppMessage msg)
	{
		int type = msg.getType();
		
		if(type == SMsg.C_GET_PARTY_FAMILY_REWARD_COMMAND)
		{
			sendTopFamily(target);
		}
		else if(type == SMsg.C_GET_PLAYER_FAMILY_HONOUR_COMMAND)
		{
			sendFamilyTopPlayer(target,msg);
		}
		else
		{
			super.clientMessageChain(target,msg);
		}

	}

	public List getTopFamilys()
	{
		return topFamilys;
	}

	public void setTopFamilys(List topFamilys)
	{
		this.topFamilys = topFamilys;
	}
	
}
