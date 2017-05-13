package cc.lv1.rpg.gs.net.impl;

import cc.lv1.rpg.gs.data.PressureTest;
import vin.rabbit.net.abs.NetConnection;
import vin.rabbit.net.job.i.NetJob;

public class ShopCenterInfoJob extends NetJob
{

	private String line;
	
	public ShopCenterInfoJob(String line)
	{
		this.line = line;
	}
	
	
	public NetConnection getConnection()
	{
		return null;
	}

	public void run()
	{
		
		PressureTest.getInstance().saveShopCenterText(line);
	}

}
