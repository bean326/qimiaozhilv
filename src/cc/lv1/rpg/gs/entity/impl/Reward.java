package cc.lv1.rpg.gs.entity.impl;

import vin.rabbit.comm.GameObject;
import vin.rabbit.util.ByteBuffer;
import cc.lv1.rpg.gs.entity.RPGameObject;

/**
 * 问答排行
 * @author bean
 *
 */
public class Reward extends RPGameObject
{
	public int rank;
	
	public String accountName;
	
	public int level;
	
	/** 答题时存积分用point */
	public int point;
	
	public long logTime;
	
	public boolean isGetReward = false;
	
	/** npcId:npcModelId:playerName:playerSignName 
	 * NpcId:NPC形象ID:玩家名字:玩家领取签名的情况(1表示领取了第一名的签名,1|2表示领取了第一名，第二名的签名,以此类推,0表示没领取)
	 * */
	public String imageShow = "0:0:0";
	
	private Player player;
	
	public Reward()
	{
		
	}
	
	
	public Reward(String name,int level,int point,long time)
	{
		this.name = name;
		this.level = level;
		this.point = point;
		this.logTime = time;
	}
	
	public void copyTo(GameObject go)
	{
		super.copyTo(go);
		Reward reward = (Reward) go;
		
		reward.level = level;
		reward.point = point;
		reward.logTime = logTime;
		reward.isGetReward = isGetReward;
		reward.imageShow = imageShow;
		reward.setPlayer(player);
	}
	
	public Player getPlayer()
	{
		return this.player;
	}
	
	public int getPoint()
	{
		return point;
	}
	
	public void setPlayer(Player player)
	{
		this.player = player;
	}
	
	public void loadFrom(ByteBuffer buffer)
	{
		super.loadFrom(buffer);
		level = buffer.readInt();
		point = buffer.readInt();
		rank = buffer.readByte();
		logTime = buffer.readLong();
	}
	
	public void loadReward(ByteBuffer buffer)
	{
		super.loadFrom(buffer);
		level = buffer.readShort();
		rank = buffer.readShort();
		point = buffer.readInt();
		buffer.readByte();
		logTime = buffer.readLong();
		isGetReward = buffer.readBoolean();
		imageShow = buffer.readUTF();
	}
	
	public void saveTo(ByteBuffer buffer)
	{
		super.saveTo(buffer);
		buffer.writeInt(level);
		buffer.writeInt(point);
		buffer.writeByte(rank);
		buffer.writeLong(logTime);
	}
	
	public void saveReward(ByteBuffer buffer)
	{
		super.saveTo(buffer);
		buffer.writeShort(level);
		buffer.writeShort(rank);
		buffer.writeInt(point);
		buffer.writeByte(0);
		buffer.writeLong(logTime);
		buffer.writeBoolean(isGetReward);
		buffer.writeUTF(imageShow);
	}
	
	public void writeTo(ByteBuffer buffer)
	{
		super.writeTo(buffer);
		buffer.writeInt(level);
		buffer.writeInt(point);
		buffer.writeByte(rank);
		buffer.writeLong(logTime);
	}
}
