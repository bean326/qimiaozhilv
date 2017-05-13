package cc.lv1.rpg.gs.net.impl;

import vin.rabbit.net.abs.NetConnection;
import vin.rabbit.net.job.i.NetJob;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.dao.DatabaseAccessor;

public class SaveWorldJob extends NetJob
{
	
	private WorldManager worldManager;

	public SaveWorldJob(WorldManager worldManager)
	{
		this.worldManager = worldManager;
	}

	public NetConnection getConnection()
	{
		return null;
	}

	public void run()
	{
		DatabaseAccessor databaseAccessor =  worldManager.getDatabaseAccessor();
		databaseAccessor.createOrUpdateWorldInfo(worldManager);
	}

}
