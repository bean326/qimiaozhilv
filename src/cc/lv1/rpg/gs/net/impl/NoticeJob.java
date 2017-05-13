package cc.lv1.rpg.gs.net.impl;

import cc.lv1.rpg.gs.entity.controller.BattleController;
import vin.rabbit.net.abs.NetConnection;
import vin.rabbit.net.job.i.NetJob;

public class NoticeJob extends NetJob 
{
	
	BattleController battle;
	
	public NoticeJob(BattleController battle)
	{
		this.battle = battle;
	}

	public NetConnection getConnection() 
	{
		return null;
	}

	public void run() 
	{
		battle.sendNoticeInfo();
	}

}
