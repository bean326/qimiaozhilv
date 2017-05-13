package cc.lv1.rpg.gs.net.impl;

import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.WorldManager;
import vin.rabbit.net.abs.NetConnection;
import vin.rabbit.net.job.i.NetJob;

public class GetTopJob extends NetJob
{

	public GetTopJob()
	{
	}
	
	public NetConnection getConnection()
	{
		return null;
	}

	public void run()
	{
		GameServer.getInstance().initDynamicData();
	}

}
