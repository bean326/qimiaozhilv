package cc.lv1.rpg.gs.net.impl;

import cc.lv1.rpg.gs.entity.ShopCenter;
import vin.rabbit.net.abs.NetConnection;
import vin.rabbit.net.job.i.NetJob;

public class ShopCenterUpdateJob extends NetJob
{


	private long timeMillis;

	public ShopCenterUpdateJob(long timeMillis)
	{
		this.timeMillis = timeMillis;
	}
	
	public NetConnection getConnection()
	{
		return null;
	}

	public void run()
	{
		ShopCenter.getInstance().update(timeMillis);
	}

}
