package cc.lv1.rpg.gs.entity.controller;


import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.data.DC;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.ext.Bag;
import cc.lv1.rpg.gs.entity.ext.OtherExtInfo;
import cc.lv1.rpg.gs.entity.impl.Player;
import cc.lv1.rpg.gs.entity.impl.Reward;
import cc.lv1.rpg.gs.net.SMsg;
import cc.lv1.rpg.gs.other.ErrorCode;
import cc.lv1.rpg.gs.util.FontConver;
import vin.rabbit.net.AppMessage;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.i.Map;
import vin.rabbit.util.collection.impl.ArrayList;
import vin.rabbit.util.collection.impl.HashMap;

/**
 * 副本控制器
 * @author bean
 *
 */
public class CopyController extends PartyController
{
	/** 能否组队 */
	public boolean isTeam;
	
	/** 胜利后得到积分的基础分 */
	public int winPoint;
	
	/** 失败后得到积分的基础分 */
	public int losePoint;
	
	/** 第一个房间的ID */
	public int startRoomId;
	
	/** 结束房间ID */
	public int endRoomId;
	
	/** 区域ID */
	public int areaId;
	
	/** 副本类型(1:普通副本 2:时空之旅副本 3:人间试练 4:地狱试练 5:天堂试练) */
	public int type;
	
	/** 进入隐藏房间的几率(用隐藏房间的ID作key,具体的几率是value[万分比]) */
	public String inRate;
	
	/**
	 * 1:1000-1999
	 * 2:2000-2999
	 * 3:3000-3999
	 * 4:4000-4999
	 * 5:5000-5999
	 * 6:6000-7000
	 * 
	 */
	public int step;
	
	/** 前一期玩家排行 */
	public List beforeTop = new ArrayList(20);
	
	/** 历史最强玩家 */
	public Reward bestPlayer = new Reward("none",1,1,WorldManager.currentTime);
	
	public CopyController()
	{
		
	}
	
	
	public void loadFrom(ByteBuffer buffer)//主要是取排行榜
	{
		int length = buffer.readByte();
		for (int i = 0; i < length; i++)
		{
			Reward reward = new Reward();
			reward.id = buffer.readInt();
			reward.name = buffer.readUTF();
			reward.rank = buffer.readInt();
			reward.level = buffer.readInt();
			reward.point = buffer.readInt();
			reward.logTime = buffer.readLong();
			beforeTop.add(reward);
		}
		
		length = buffer.readByte();
		for (int i = 0; i < length; i++)
		{
			Reward reward = new Reward();
			reward.id = buffer.readInt();
			reward.name = buffer.readUTF();
			reward.rank = buffer.readInt();
			reward.level = buffer.readInt();
			reward.point = buffer.readInt();
			reward.logTime = buffer.readLong();
			topPlayers.add(reward);
		}
		
		bestPlayer.id = buffer.readInt();
		bestPlayer.name = buffer.readUTF();
		bestPlayer.level = buffer.readInt();
		bestPlayer.point = buffer.readInt();
		bestPlayer.logTime = buffer.readLong();
	}
	
	public void saveTo(ByteBuffer buffer)//主要是存排行榜
	{
		int length = beforeTop.size();
		buffer.writeByte(length);
		for (int i = 0; i < length; i++) 
		{
			Reward reward = (Reward) beforeTop.get(i);
			buffer.writeInt(reward.id);
			buffer.writeUTF(reward.name);
			buffer.writeInt(reward.rank);
			buffer.writeInt(reward.level);
			buffer.writeInt(reward.point);
			buffer.writeLong(reward.logTime);
		}
		
		length = topPlayers.size();
		buffer.writeByte(length);
		for (int i = 0; i < length; i++) 
		{
			Reward reward = (Reward) topPlayers.get(i);
			buffer.writeInt(reward.id);
			buffer.writeUTF(reward.name);
			buffer.writeInt(reward.rank);
			buffer.writeInt(reward.level);
			buffer.writeInt(reward.point);
			buffer.writeLong(reward.logTime);
		}
		
		/*******************历史最强**************************/
		buffer.writeInt(bestPlayer.id);
		buffer.writeUTF(bestPlayer.name);
		buffer.writeInt(bestPlayer.level);
		buffer.writeInt(bestPlayer.point);
		buffer.writeLong(bestPlayer.logTime);
	}
	
	
	public boolean setVariable(String key,String value)
	{
		if(key.equals("isTeam"))
		{
			isTeam = value.equals("1");
			return true;
		}
		else if(key.equals("inRate"))
		{
			inRate = value;
			if("0".equals(inRate) || "".equals(inRate))
				return true;
			String[] strs = Utils.split(value, "|");
			for (int i = 0; i < strs.length; i++)
			{
				try
				{
					String[] rates = Utils.split(strs[i], ":");
					Integer.parseInt(rates[0]);
					Integer.parseInt(rates[1]);
				}
				catch(Exception e)
				{
					System.out.println("CopyController setVariable inRate error:"+value);
				}
			}
			return true;
		}
		else
			return super.setVariable(key, value);
	}
	
	
	/**
	 * 时空之旅副本完成(状态还是false,只是把记录的房间ID设回0)
	 * @param target
	 */
	public void finishCopy(PlayerController target)
	{
		int num = DataFactory.getInstance().getCopyNum(areaId);
		if(num == -1)
			return;
		OtherExtInfo oei = (OtherExtInfo) target.getPlayer().getExtPlayerInfo("otherExtInfo");
		oei.assRoomId[num] = 0;
		oei.copyPoint[num] = 0;
		if(type == 2)
			oei.isAssOver[num] = false;
		else if(type == 3 || type == 4 || type == 5)
			oei.isAssOver[num] = true;
	}
	
	
	public Reward getRewardYestoday(int id)
	{
		for (int i = 0; i < beforeTop.size(); i++) 
		{
			Reward r = (Reward) beforeTop.get(i);
			if(r.id == id)
				return r;
		}
		return null;
	}
	
	public Reward getRewardToday(int id)
	{
		for (int i = 0; i < topPlayers.size(); i++) 
		{
			Reward r = (Reward) topPlayers.get(i);
			if(r.id == id)
				return r;
		}
		return null;
	}
	
	
	
	
	public void addPlayerPoint(PlayerController target,int point,RoomController room)
	{
			if(point == 0)
				return;
			OtherExtInfo oei = (OtherExtInfo) target.getPlayer().getExtPlayerInfo("otherExtInfo");
			oei.copyPoints += point;
			if(oei.copyPoints < 0)
				oei.copyPoints = 0;
			//此方法一定要在前面的setOtherExtInfo方法后面调用
			GameServer.getInstance().getWorldManager().addCopyAllRank(target,room);
			
			target.sendGetGoodsInfo(1, false, DC.getString(DC.BATTLE_14)+": "+point);
	}
	
	/**
	 * CopyJob里调用
	 */
	public void initTop()
	{
		beforeTop = new ArrayList(topPlayers.size());
		for (int i = 0; i < topPlayers.size(); i++) 
		{
			beforeTop.add(topPlayers.get(i));
		}
		topPlayers = new ArrayList(20);
	}
	
	
	public void sendTopPlayer(PlayerController target)
	{
		ByteBuffer buffer = new ByteBuffer();
		/********************昨天的排行********************************/
		int size = beforeTop==null?0:beforeTop.size();
		buffer.writeByte(size);
		String str = "";
		for (int i = 0; i < size; i++)
		{
			Reward reward = (Reward) beforeTop.get(i);
			buffer.writeInt(reward.rank);
			buffer.writeUTF(reward.name);
			Player player = reward.getPlayer();
			if(player == null)
				player = GameServer.getInstance().getDatabaseAccessor().getPlayer(reward.id);
			if(player == null)
			{
				buffer.writeUTF("");
				buffer.writeByte(0);
			}
			else
			{
				buffer.writeUTF(player.familyName);
				buffer.writeByte(player.upProfession);
			}
			buffer.writeInt(reward.point);
			if(str.isEmpty())
				str = WorldManager.getTypeTime("yyyy-MM-dd", reward.logTime);
		}
		if(str.isEmpty())
			str = WorldManager.getTypeTime("yyyy-MM-dd", WorldManager.currentTime);
		buffer.writeUTF(str);
		
		/*******************今天的排行**************************/
		size = topPlayers==null?0:topPlayers.size();
		buffer.writeByte(size);
		str = "";
		for (int i = 0; i < size; i++)
		{
			Reward reward = (Reward) topPlayers.get(i);
			buffer.writeInt(reward.rank);
			buffer.writeUTF(reward.name);
			Player player = reward.getPlayer();
			if(player == null)
				player = GameServer.getInstance().getDatabaseAccessor().getPlayer(reward.id);
			if(player == null)
			{
				buffer.writeUTF("");
				buffer.writeByte(0);
			}
			else
			{
				buffer.writeUTF(player.familyName);
				buffer.writeByte(player.upProfession);
			}
			buffer.writeInt(reward.point);
			if(str.isEmpty())
				str = WorldManager.getTypeTime("yyyy-MM-dd", reward.logTime);
		}
		if(str.isEmpty())
			str = WorldManager.getTypeTime("yyyy-MM-dd", WorldManager.currentTime);
		buffer.writeUTF(str);
		
		/*******************历史最强**************************/
		buffer.writeUTF(bestPlayer.name);
		buffer.writeInt(bestPlayer.point);
		buffer.writeUTF(WorldManager.getTypeTime("yyyy-MM-dd HH:mm:ss", bestPlayer.logTime));
		
		/*******************自己的**************************/
		buffer.writeUTF(target.getOtherExtInfo("copyPoint",false,target.getRoom()));
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_GET_COPY_PLAYER_REWARD_COMMAND,buffer));
	}
	
	
	public int inHideRoom()
	{
		if("".equals(inRate) || "0".equals(inRate))
			return 0;
		String[] strs = Utils.split(inRate, "|");
		for (int i = 0; i < strs.length; i++)
		{
			String[] rates = Utils.split(strs[i], ":");
			int random = (int) (Math.random() * 10000);
			if(random <= Integer.parseInt(rates[1]))
				return Integer.parseInt(rates[0]);
		}
		return 0;
	}
	
	
	public boolean isHideRoom(int roomId)
	{
		if("".equals(inRate) || "0".equals(inRate))
			return false;
		String[] strs = Utils.split(inRate, "|");
		for (int i = 0; i < strs.length; i++)
		{
			String[] rates = Utils.split(strs[i], ":");
			if(rates[0].equals(roomId+""))
				return true;
		}
		return false;
	}
	
	
	public boolean isHonorCopy()
	{
		return (type == 2 || type == 3 || type == 4 || type == 5);
	}

	
	/**
	 * 接收副本内消息
	 * 
	 * @param msg
	 */
	public void clientMessageChain(PlayerController target,AppMessage msg)
	{
		int type = msg.getType();
		if(type == SMsg.C_GET_COPY_PLAYER_REWARD_COMMAND)
		{
//			sendTopPlayer(target);
		}
		
	}
}
