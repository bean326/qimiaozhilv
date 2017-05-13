package cc.lv1.rpg.gs.entity.ext;

import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.entity.impl.Player;
import vin.rabbit.util.ByteBuffer;

public class PVPInfo extends PlayerExtInfo 
{

	private Player player;
	
	public static long checkTime = 1000 * 60 * 5;//5分钟
	
	/** 被杀开始时间 */
	public long startCheckTime;
	
	/** 虚弱状态 */
	public int weakness;
	
	/** 荣誉值 */
	public int honourPoint;
	
	public PVPInfo()
	{
	}
	
	public void setPlayer(Player player)
	{
		this.player = player;
	}
	
	@Override
	public String getName() 
	{
		return "PVPInfo";
	}

	@Override
	public void loadFrom(ByteBuffer buffer) 
	{
		startCheckTime = buffer.readLong();
		weakness = buffer.readInt();
		honourPoint = buffer.readInt();

	}

	@Override
	public void saveTo(ByteBuffer buffer) 
	{
		buffer.writeLong(startCheckTime);
		buffer.writeInt(weakness);
		buffer.writeInt(honourPoint);

	}
	
	public void writeTo(ByteBuffer buffer)
	{
		buffer.writeInt(honourPoint);
	}
	
	public void setHonourPoint(int point)
	{
		long hp = honourPoint + point;
		if(hp > 99999999)
			honourPoint = 99999999;
		else if(hp < 0)
			honourPoint = 0;
		else
			honourPoint = (int) hp;
	}
	
	public void setWeakness()
	{
		if(weakness >= 2)
			return;
		weakness += 1;
		if(startCheckTime == 0 || weakness >= 2)
		{
			startCheckTime = WorldManager.currentTime;//System.currentTimeMillis();
		}
	}
	
	public boolean isWeakness()
	{
		return weakness >= 2;
	}
	
//	public void print()
//	{
//		System.out.println("-------------------------------------");
//		System.out.println("荣誉值："+honourPoint);
//		System.out.println("-------------------------------------");
//	}
}
