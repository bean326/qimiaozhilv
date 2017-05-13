package cc.lv1.rpg.gs.net.impl;

import cc.lv1.rpg.gs.WorldManager;
import vin.rabbit.net.abs.NetConnection;
import vin.rabbit.net.job.i.NetJob;

public class UpdateJob extends NetJob
{

	private WorldManager worldManger;
	
	private long timeMillis;
	
	public UpdateJob(WorldManager worldManger,long timeMillis)
	{
		this.worldManger = worldManger;
		this.timeMillis = timeMillis;
	}
	
	public NetConnection getConnection()
	{
		return null;
	}

	public void run()
	{
		worldManger.update(timeMillis);
	}
	
}
