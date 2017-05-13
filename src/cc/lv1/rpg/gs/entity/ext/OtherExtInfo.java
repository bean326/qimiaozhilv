package cc.lv1.rpg.gs.entity.ext;

import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.controller.CopyController;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.controller.RoomController;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.collection.i.List;

public class OtherExtInfo extends PlayerExtInfo 
{
	/** 副本到哪个房间了,数组长度为整个游戏中副本的数量,每个值分别对应在每个副本中 */
	public int[] assRoomId;
	
	/** 当天副本是否已刷完,数组长度为整个游戏中副本的数量,每个值分别对应在每个副本中 */
	public boolean[] isAssOver;
	
	/** 副本中得到积分 */
	public int[] copyPoint;
	
	/** 所有副本总的积分 */
	public long copyPoints;
	
	/** 所在区号 */
	public int worldNUM = 0;
	
	/** 下线时间 */
	public long leaveTime;
	
	/** 累计在线时间(有上限的，暂时最高是5小时) */
	public int onlineTime;
	
	/** 是否验证了身份证 */
	public boolean isValiIdcard = false;
	
	
	public static final short MAXAAMCOUNT = 100;
	/**
	 * 师徒每天最多只能获得100场这样的奖励。
	 */
	public short aamCount;

	/** 婚姻类型 1:普通 2:VIP */
	public byte marryType;
	
	/** 爱人的ID */
	public int loverId;
	
	/** 爱人名字 */
	public String loverName = "";
	

	public OtherExtInfo()
	{
		List list = (List) DataFactory.getInstance().getAttachment(DataFactory.COPY_LIST);
		int num = list.size();
		assRoomId = new int[num];
		isAssOver = new boolean[num];
		copyPoint = new int[num];
	}
	
	@Override
	public String getName() 
	{
		return "otherExtInfo";
	}

	@Override
	public void loadFrom(ByteBuffer byteBuffer)
	{
		copyPoints = byteBuffer.readLong();
		
		worldNUM = byteBuffer.readInt();
		leaveTime = byteBuffer.readLong();
		onlineTime = byteBuffer.readInt();
		isValiIdcard = byteBuffer.readBoolean();
		aamCount = (short) byteBuffer.readShort();
		if(aamCount < 0)
			aamCount = 0;
		marryType = (byte) byteBuffer.readByte();
		loverId = byteBuffer.readInt();
		loverName = byteBuffer.readUTF();

		int len = byteBuffer.readByte();
		for (int i = 0; i < len; i++) 
		{
			assRoomId[i] = byteBuffer.readInt();
		}
		len = byteBuffer.readByte();
		for (int i = 0; i < len; i++) 
		{
			isAssOver[i] = byteBuffer.readBoolean();
		}
		len = byteBuffer.readByte();
		for (int i = 0; i < len; i++) 
		{
			copyPoint[i] = byteBuffer.readInt();
		}
	}

	@Override
	public void saveTo(ByteBuffer byteBuffer)
	{
		byteBuffer.writeLong(copyPoints);
		
		byteBuffer.writeInt(worldNUM);
		byteBuffer.writeLong(WorldManager.currentTime);
		byteBuffer.writeInt(onlineTime);
		byteBuffer.writeBoolean(isValiIdcard);
		byteBuffer.writeShort(aamCount);
		byteBuffer.writeByte(marryType);
		byteBuffer.writeInt(loverId);
		byteBuffer.writeUTF(loverName);

		byteBuffer.writeByte(assRoomId.length);
		for (int i = 0; i < assRoomId.length; i++) 
		{
			byteBuffer.writeInt(assRoomId[i]);
		}
		byteBuffer.writeByte(isAssOver.length);
		for (int i = 0; i < isAssOver.length; i++) 
		{
			byteBuffer.writeBoolean(isAssOver[i]);
		}
		byteBuffer.writeByte(copyPoint.length);
		for (int i = 0; i < copyPoint.length; i++) 
		{
			byteBuffer.writeInt(copyPoint[i]);
		}

	}
	
	public boolean setVariable(String key,String value,int num)
	{
		if(key.equals("assRoomId"))
		{
			assRoomId[num] = Integer.parseInt(value);
			CopyController copy = DataFactory.getInstance().getCopyByNum(num);
			if(copy.type == 1)
			{
				if(assRoomId[num]-1 == copy.endRoomId)
				{
					assRoomId[num] = 0;
					isAssOver[num] = true;
				}
			}
			return true;
		}
		else if(key.equals("copyPoint"))
		{
			copyPoint[num] += Integer.parseInt(value);
			if(copyPoint[num] < 0)
				copyPoint[num] = 0;
			
			copyPoints += Long.parseLong(value);
			if(copyPoints < 0)
				copyPoints = 0;
			
			return true;
		}
		else
			return false;
	}
	
	public void setOnlineTime(long oTime)
	{
		if(!DataFactory.isIdcard)
			return;
		if(isValiIdcard)
			return;
		onlineTime += oTime;
		if(onlineTime > PlayerController.UNSAFETIME)
			onlineTime = PlayerController.UNSAFETIME;
		if(onlineTime < 0)
			onlineTime = 0;
	} 
	

	
	public String getVariable(String key,int num)
	{
		if(key.equals("copyPoint"))
		{
			return copyPoint[num]+"";
		}
		else if(key.equals("assRoomId"))
		{
			return assRoomId[num]+"";
		}
		else if(key.equals("isAssOver"))
		{
			return isAssOver[num]+"";
		}
		else
			return "";
	}
	
	
	/**
	 * 清除个人打怪副本信息
	 */
	public void clearAss()
	{
		for (int i = 0; i < assRoomId.length; i++) 
		{
			assRoomId[i] = 0;
		}
		for (int i = 0; i < isAssOver.length; i++) 
		{
			isAssOver[i] = false;
		}
		for (int i = 0; i < copyPoint.length; i++) 
		{
			copyPoint[i] = 0;
		}
		
		copyPoints = 0;
	}
	
	
	public void writeValidateTo(ByteBuffer buffer)
	{
		buffer.writeBoolean(isValiIdcard);
		buffer.writeInt(onlineTime);
		if("".equals(loverName))
			buffer.writeBoolean(false);
		else
			buffer.writeBoolean(true);
	}
	
	
	public void clearAamCount()
	{
		aamCount = 0;
	}
	
	public void clearLoveInfo()
	{
		loverId = 0;
		loverName = "";
		marryType = 0;
	}

}
