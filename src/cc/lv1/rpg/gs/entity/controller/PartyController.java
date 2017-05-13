package cc.lv1.rpg.gs.entity.controller;

import vin.rabbit.net.AppMessage;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.impl.ArrayList;
import vin.rabbit.util.collection.impl.HashMap;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.container.PlayerContainer;
import cc.lv1.rpg.gs.entity.impl.PartyReward;
import cc.lv1.rpg.gs.entity.impl.Player;
import cc.lv1.rpg.gs.entity.impl.Reward;
import cc.lv1.rpg.gs.net.SMsg;
/**
 * 活动控制器
 * @author dxw
 *
 */
public class PartyController extends PlayerContainer
{
	/** 荣誉值道具 */
	public static final int HONORPROP = 1045000144;
	/** 排行榜要显示的名次 */
	public static final int REWARDRANK = 20;
	/**
	 * 每小时
	 */
	protected static final int HOUR = 1000*60*60;

	/**
	 * 剩余开始时间   系统自动初始化
	 */
	protected long lessBeginTime = 0;
	
	/**
	 * 举行活动的时间   系统自动初始化
	 */
	protected long partyTime = 0;
	
	
	/**
	 * 是否进入准备阶段   系统自动初始化
	 */
	protected boolean isReady = false;
	
	/**
	 * 活动是否开始了   系统自动初始化
	 */
	protected boolean isStarted = false;
	
	/**
	 * 活动是否结束了   系统自动初始化
	 */
	protected boolean isEnded = true;
	
	
	/**
	 * 玩家点数
	 */
	protected HashMap playerPoint = new HashMap(200);
	
	/**
	 * 排行玩家
	 */
	protected List topPlayers = new ArrayList(20);
	
	
	/**
	 * 成员排行 
	 * 家族战时key值是家族ID:名字 value是一个List，存的是这个家族中成员获得的荣誉值的排行
	 * 阵营战时key值是
	 */
	protected HashMap topTeamPlayers = new HashMap(50);
	
	
	protected WorldManager worldManager;
	
	
	public void setWorld(WorldManager worldManager)
	{
		this.worldManager = worldManager;
	}
	
	public void init()
	{
		topPlayers = new ArrayList(20); //创建排名
		playerPoint = new HashMap(200); //创建暂存名单
		playerList = new ArrayList(100);
		topTeamPlayers = new HashMap(50);
		
	}
	
	
	
	public int getPlayerPoint(PlayerController target)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(target.getPlayer().name);
		if(playerPoint == null)
			return 0;
		Object obj = playerPoint.get(sb.toString());
		if(obj == null)
			return 0;
		String[] strs = Utils.split(obj.toString(), ":");
		return  Integer.parseInt(strs[0]);
	}
	
	public void addPlayerPoint(PlayerController target,int point)
	{
		if(playerPoint != null)
		{
			StringBuffer sb = new StringBuffer();
			sb.append(target.getName());
			Object obj = playerPoint.get(sb.toString());
			if(obj != null)
			{
				String[] strs = Utils.split(obj.toString(), ":");
				long value = Long.parseLong(strs[0]) + point;
				if(value > 99999999)
					value = 99999999;
				String str = strs[2].equals("0")?String.valueOf(WorldManager.currentTime):strs[2];
				playerPoint.put(sb.toString(), value+":"+strs[1]+":"+str);//value:count:time
//				System.out.println("加入个人排行！"+target.getName());
				setRank(target, topPlayers, 1, value);
			
				if(target.getFamily() != null)
				{
					sb = new StringBuffer();
					sb.append(target.getFamily().id);
					sb.append(":");
					sb.append(target.getFamily().name);
					
					obj = topTeamPlayers.get(sb.toString());
					if(obj != null && obj instanceof ArrayList)
					{
						ArrayList list = (ArrayList) obj;
						boolean isIn = false;
						for (int i = 0; i < list.size(); i++) 
						{
							PartyReward reward = (PartyReward) list.get(i);
							if(reward.id == target.getID())
							{
								reward.id = target.getID();
								reward.name = target.getName();
								reward.accountName = target.getPlayer().accountName;
								reward.level = target.getPlayer().level;
								reward.honorPoint = value;
								reward.logTime = WorldManager.currentTime;
								isIn = true;
								break;
							}
						}
						if(!isIn)
						{
							PartyReward reward = new PartyReward();
							reward.id = target.getID();
							reward.name = target.getName();
							reward.accountName = target.getPlayer().accountName;
							reward.level = target.getPlayer().level;
							reward.honorPoint = value;
							reward.logTime = WorldManager.currentTime;
							list.add(reward);
						}
						
						quickSortReward(list);
						
						
						for (int i = 0; i < list.size(); i++) 
						{
							PartyReward reward = (PartyReward) list.get(i);
							reward.rank = i+1;
						}
					}
				}
			}
		}
	}
	
	
	public void addPlayer(PlayerController target)
	{
		if(isPlayerIn(target))
			return;
		
		Object obj = playerPoint.get(target.getName());
		if(obj == null)
		{
			//value:count:time
			//荣誉值:次数限制(从休息室到PK场，次数到达最大时就不能再进了)
			playerPoint.put(target.getName(), "0:0:0"); 
		}
		
		super.addPlayer(target);
	}
	
	
	protected boolean isMoveRoom(PlayerController target)
	{
		FamilyPartyController fpc = FamilyPartyController.getInstance();
		if(!fpc.isReady())
		{
//			System.out.println("party is not ready!");
			 return false;
		}
		if(playerPoint == null)
		{
//			System.out.println("playerPoint is null!");
			return false;
		}
//		Object obj = playerPoint.get(target.getPlayer().accountName+":"+target.getName());
	
		Object obj = playerPoint.get(target.getName());
		if(obj == null)
		{
//			System.out.println("obj is null!");
			return false;
		}
		else
		{
			String[] strs = Utils.split(obj.toString(), ":");
			if(fpc.isReady())
			{
				return Integer.parseInt(strs[1]) < FamilyPartyController.MAXLOSECOUNT;
			}
		}
		return true;
	}
	
	protected void setInRoomCount(PlayerController target)
	{
		if(target.getTeam() == null)
		{
			Object obj = playerPoint.get(target.getName());
			if(obj != null)
			{
				String[] strs = Utils.split(obj.toString(), ":");
				int count = Integer.parseInt(strs[1]) + 1;
				playerPoint.put(target.getName(), strs[0]+":"+count+":"+strs[2]);
			}
		}
		else
		{
			if(target.getTeam().isLeader(target))
			{
				PlayerController[] pcs = target.getTeam().getPlayers();
				for (int i = 0; i < pcs.length; i++)
				{
					if(pcs[i] == null || !pcs[i].isOnline())
						continue;
					Object obj = playerPoint.get(pcs[i].getName());
					if(obj != null)
					{
						String[] strs = Utils.split(obj.toString(), ":");
						int count = Integer.parseInt(strs[1]) + 1;
						playerPoint.put(pcs[i].getName(), strs[0]+":"+count+":"+strs[2]);
					}
				}
			}
		}
	}
	
	/**
	 * 使用回城卷时
	 */
	public void removePlayerWithout(PlayerController target)
	{
		//玩家下线后不清楚map内容
		super.removePlayer(target);
	}
	
	
	/**
	 * 玩家下线时或活动结束时
	 */
	public void removePlayer(PlayerController target)
	{
		target.getPlayer().setDefaultRoom(DataFactory.INITROOM);
		
		//玩家下线后不清楚map内容
		super.removePlayer(target);
	}
	
	
	public boolean isPlayerInParty(PlayerController target)
	{
		return playerPoint==null?false:playerPoint.get(target.getName()) != null;
	}
	/**
	 * 玩家在活动里面
	 * @param target
	 * @return
	 */
	public boolean isPlayerIn(PlayerController target)
	{
		return super.getPlayer(target.getID()) != null;
	}
	
	public boolean isReady()
	{
		return isReady;
	}

	public boolean isStarted()
	{
		return isStarted;
	}

	public boolean isEnded()
	{
		return isEnded;
	}
	
	/**
	 * 发送活动奖励
	 */
	public void sendPartyReward()
	{
		
	}
	
	
	/**
	 * 设置荣誉排行榜
	 */
	public void setRank(PlayerController target,List topList,int type,long honorPoint)
	{
		String name = "";
		int id = 0;
		if(type == 1)//玩家个人排行
		{
			id = target.getID();
			name = target.getName();
		}
		else if(type == 2)//家族排行
		{
			if(target.getFamily() == null)
				return;
			id = target.getFamily().id;
			name = target.getFamily().name;
		}
		else 
			return;
		if(topList.size() == 0)
		{
			PartyReward reward = new PartyReward();//活动时存积分用honorPoint，表示荣誉值，答题时存积分用point
			reward.id = id;
			reward.name = name;
			reward.honorPoint = honorPoint;
			reward.rank = 1;
			reward.logTime = WorldManager.currentTime;
			if(type == 2)
			{
				reward.leaderName = target.getFamily().leaderName;
				//reward.playerCount = target.getFamily().getPlayerCount();
				reward.playerCount = target.getFamily().getFamilyTotalCount();
			}
			topList.add(reward);
		}else
		{
			boolean isInRank = false;
			//如果玩家已经在前10，则更新玩家的荣誉点数
			for(int has = 0;has < topList.size();has++)
			{
				PartyReward reward = (PartyReward) topList.get(has);
				if(reward.id == id)
				{
					reward.honorPoint = honorPoint;
					if(type == 2)
					{
						reward.leaderName = target.getFamily().leaderName;
						reward.playerCount = target.getFamily().getFamilyTotalCount();
					}
					isInRank = true;
					break;
				}
			}
			
			//如果玩家没有在排行榜中，则加入到排行榜
			if(isInRank == false)
			{
				PartyReward reward = new PartyReward();
				reward.id = id;
				reward.name = name;
				reward.honorPoint = honorPoint;
				reward.logTime = WorldManager.currentTime;
				if(type == 2)
				{
					reward.leaderName = target.getFamily().leaderName;
					reward.playerCount = target.getFamily().getFamilyTotalCount();
				}
				topList.add(reward);
			}
			
			if (topList.size() > 1) 
			{
			//对排行榜中的玩家进行排序	
				for (int cur = 0; cur < topList.size() - 1; cur++)
				{
					
					for (int next = cur + 1; next < topList.size(); next++) 
					{
						PartyReward reward1 = (PartyReward)topList.get(cur);
						PartyReward reward2 = (PartyReward)topList.get(next);
						if (reward1.honorPoint < reward2.honorPoint) 
						{
							PartyReward temp = reward1;
							topList.set(cur, reward2);
							topList.set(next, temp);
						}
						else if(reward1.honorPoint == reward2.honorPoint)
						{
							if(reward1.logTime > reward2.logTime)
							{
								PartyReward temp = reward1;
								topList.set(cur, reward2);
								topList.set(next, temp);
							}
						}
					}
				}	
			}
			//移除排行于第10名之后的玩家
			if (topList.size() > REWARDRANK) 
			{
				for (int i = REWARDRANK; i < topList.size(); i++) 
				{
					topList.remove(i);
				}
			}
			
			//设置玩家的名次
			for(int rank = 0;rank < topList.size();rank++)
			{
				PartyReward reward = (PartyReward) topList.get(rank);
				reward.rank = rank + 1;
			}
		}
	}
	
	
	public void sendTopPlayer(PlayerController target)
	{
		ByteBuffer buffer = new ByteBuffer();
		int size = topPlayers==null?0:topPlayers.size();
		buffer.writeByte(size);
//		System.out.println("玩家排行："+size);
		String str = "";
		for (int i = 0; i < size; i++)
		{
			PartyReward reward = (PartyReward) topPlayers.get(i);
			buffer.writeInt(reward.rank);
			buffer.writeUTF(reward.name);
			buffer.writeUTF(reward.honorPoint+"");
			if(str.isEmpty())
				str = WorldManager.getTypeTime("yyyy-MM-dd", reward.logTime);
		}
		if(str.isEmpty())
			str = WorldManager.getTypeTime("yyyy-MM-dd", WorldManager.currentTime);
		
		buffer.writeUTF(target.getName());
		if(playerPoint == null)
		{
			buffer.writeInt(0);
		}
		else
		{
			Object obj = playerPoint.get(target.getPlayer().name);
			if(obj == null)
			{
				buffer.writeInt(0);
			}
			else
			{
				String[] strs = Utils.split(obj.toString(), ":");
				buffer.writeInt(Integer.parseInt(strs[0]));
			}
		}
		buffer.writeUTF(str);
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_GET_PARTY_PLAYER_REWARD_COMMAND,buffer));
	}
	
	
	/**
	 * 排序 list中的对象 是PartyReward对象
	 * @param list 
	 */
	public void quickSortReward(List list)
	{
		for (int cur = 0; cur < list.size() - 1; cur++)
		{
			for (int next = cur + 1; next < list.size(); next++) 
			{
				PartyReward reward1 = (PartyReward)list.get(cur);
				PartyReward reward2 = (PartyReward)list.get(next);
				if (reward1.honorPoint < reward2.honorPoint) 
				{
					PartyReward temp = reward1;
					list.set(cur, reward2);
					list.set(next, temp);
				}
				else if(reward1.honorPoint == reward2.honorPoint)
				{
					if(reward1.logTime > reward2.logTime)
					{
						PartyReward temp = reward1;
						list.set(cur, reward2);
						list.set(next, temp);
					}
				}
			}
		}	
	}
	
	
	/**
	 * 排序list里的对象是字符串数组,格式是name:poing:count:time
	 */
	public void quickSort(List list)
	{
		for (int cur = 0; cur < list.size() - 1; cur++)
		{
			for (int next = cur + 1; next < list.size(); next++) 
			{
				String str1 = list.get(cur).toString();
				String[] strs1 = Utils.split(str1, ":");
				String str2 = list.get(next).toString();
				String[] strs2 = Utils.split(str2, ":");
				long point1 = Long.parseLong(strs1[1]);
				long point2 = Long.parseLong(strs2[1]);
				long time1 = Long.parseLong(strs1[3]);
				long time2 = Long.parseLong(strs2[3]);
				if (point1 < point2) 
				{
					String temp = str1;
					list.set(cur, str2);
					list.set(next, temp);
				}
				else if(point1 == point2)
				{
					if(time1 > time2)
					{
						String temp = str1;
						list.set(cur, str2);
						list.set(next, temp);
					}
				}
			}
		}	
	}
	
	
	
	
	/**
	 * 获取玩家的休息房间
	 * @param target
	 * @return
	 */
	public static int getRoomByCamp(int camp)
	{
		int result = 0;
		if(camp == 1)
			result = DataFactory.KAITUOROOM;
		else if(camp == 2)
			result = DataFactory.XIESHENROOM;
		return result;	
	}
	
	
	public HashMap getTopTeamPlayers()
	{
		return topTeamPlayers;
	}

	public List getTopPlayers()
	{
		return topPlayers;
	}


	public void setTopTeamPlayers(HashMap topTeamPlayers)
	{
		this.topTeamPlayers = topTeamPlayers;
	}
	
	
	/**
	 * 接收活动内消息
	 * 
	 * @param msg
	 */
	public void clientMessageChain(PlayerController target,AppMessage msg)
	{
		int type = msg.getType();
		
		if(type == SMsg.C_GET_PARTY_PLAYER_REWARD_COMMAND)
		{
			sendTopPlayer(target);
		}

	}
	
	
	
}
