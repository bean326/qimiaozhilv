package cc.lv1.rpg.gs.entity.impl;

import vin.rabbit.comm.GameObject;
import vin.rabbit.util.ByteBuffer;

/**
 * 活动排行
 * @author bean
 *
 */
public class PartyReward extends Reward
{
	/** 活动时存积分用honorPoint，表示荣誉值,副本里用来记录玩家在所有副本里的积分总和*/
	public long honorPoint;
	
	/** 家族排行时族长名字 */
	public String leaderName;
	
	/** 家族排行时家族人数 */
	public int playerCount;
	
	public PartyReward()
	{
		
	}
	
	public PartyReward(String name,int level,int point,long time)
	{
		this.name = name;
		this.level = level;
		this.point = point;
		this.logTime = time;
	}
	
	public void copyTo(GameObject go)
	{
		super.copyTo(go);
		PartyReward reward = (PartyReward) go;
		
		reward.honorPoint = honorPoint;
		reward.leaderName = leaderName;
		reward.playerCount = playerCount;
	}
	
	public void loadFrom(ByteBuffer buffer)
	{
		super.loadFrom(buffer);
		accountName = buffer.readUTF();
		honorPoint = buffer.readLong();
		leaderName = buffer.readUTF();
		playerCount = buffer.readInt();
	}
	
	public void saveTo(ByteBuffer buffer)
	{
		super.saveTo(buffer);
		buffer.writeUTF(accountName);
		buffer.writeLong(honorPoint);
		buffer.writeUTF(leaderName);
		buffer.writeInt(playerCount);
	}
	
	public void writeTo(ByteBuffer buffer)
	{
		super.writeTo(buffer);
		buffer.writeUTF(accountName);
		buffer.writeLong(honorPoint);
		buffer.writeUTF(leaderName);
		buffer.writeInt(playerCount);
	}
	
	public void savePartTo(ByteBuffer buffer)
	{
		buffer.writeInt(id);
		buffer.writeUTF(name);
		buffer.writeInt(rank);
		buffer.writeLong(honorPoint);
		buffer.writeLong(logTime);
	}
	
	public void loadPartFrom(ByteBuffer buffer)
	{
		id = buffer.readInt();
		name = buffer.readUTF();
		rank = buffer.readInt();
		honorPoint = buffer.readLong();
		logTime = buffer.readLong();
	}
}
