package cc.lv1.rpg.gs.net.impl;

import cc.lv1.rpg.gs.GameServer;
import vin.rabbit.net.abs.NetConnection;
import vin.rabbit.net.job.i.NetJob;

public class LogJob  extends NetJob
{
	
	private String accountName;
	
	private int type;
	
	private String event;
	
	public NetConnection getConnection()
	{
		return null;
	}

	/**
	 * 
	 * @param accountName
	 * @param type  1登陆 2T下线 3禁言 4发公告 5发奖励 6封ip 7封号 8回档
	 * @param event
	 */
	public LogJob(String accountName,int type,String event)
	{
		this.accountName = accountName;
		this.type = type;
		this.event = event;
	}

	public void run()
	{
		GameServer.getInstance().
		getDatabaseAccessor().
		addToLog(accountName,type,event);
	}

}
