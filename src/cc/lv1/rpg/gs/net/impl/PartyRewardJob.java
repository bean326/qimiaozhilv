package cc.lv1.rpg.gs.net.impl;

import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.data.DC;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.controller.PartyController;
import cc.lv1.rpg.gs.entity.impl.PartyReward;
import cc.lv1.rpg.gs.util.FontConver;
import vin.rabbit.net.abs.NetConnection;
import vin.rabbit.net.job.i.NetJob;
import vin.rabbit.util.Utils;



public class PartyRewardJob extends NetJob 
{
	PartyController partyController;
	
	PartyReward first;
	
	public PartyRewardJob(PartyController partyController,PartyReward pr)
	{
		this.partyController = partyController;
		this.first = pr;
	}

	public NetConnection getConnection() 
	{
		return null;
	}

	public void run()
	{
		partyController.sendPartyReward();
		
		Utils.sleep(30 * 1000);
		
		//號外！號外！（**）家族在本次家族戰中獲取勝利，果然實力非凡！
		if(first != null)
		{
			StringBuffer sb = new StringBuffer();
			sb.append(DC.getString(DC.PARTY_41));
			sb.append("(");
			sb.append(first.name);
			sb.append(")");
			sb.append(DC.getString(DC.PARTY_42));
			GameServer.getInstance().getWorldManager().sendEveryonePost(sb.toString());
			sb = null;
		}
	}

}
