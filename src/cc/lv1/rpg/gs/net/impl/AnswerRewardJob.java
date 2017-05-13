package cc.lv1.rpg.gs.net.impl;

import vin.rabbit.net.abs.NetConnection;
import vin.rabbit.net.job.i.NetJob;
import vin.rabbit.util.collection.i.List;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.ext.AnswerParty;

public class AnswerRewardJob extends NetJob 
{

	WorldManager world;
	
	public AnswerRewardJob(WorldManager world)
	{
		this.world = world;
	}

	public NetConnection getConnection()
	{
		return null;
	}

	public void run()
	{
		world.sendRankReward();
	}


}
