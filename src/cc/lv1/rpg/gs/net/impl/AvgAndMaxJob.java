package cc.lv1.rpg.gs.net.impl;

import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.data.DataFactory;
import vin.rabbit.net.abs.NetConnection;
import vin.rabbit.net.job.i.NetJob;

public class AvgAndMaxJob extends NetJob {

	private WorldManager worldmanager;
	
	private int account;
	
	public AvgAndMaxJob(WorldManager worldmanager)
	{
		this.worldmanager = worldmanager;
		this.account = worldmanager.getPlayerList().size();
	}

	public NetConnection getConnection() 
	{
		return null;
	}

	public void run() 
	{
		if(DataFactory.fontVer.equals(DataFactory.SIMPLE))
		{
			if(account > 30)
				account= (int) (account + account * 1.2);
		}

		worldmanager.getDatabaseAccessor().avgAndMaxOnServer(account);
	}

}
