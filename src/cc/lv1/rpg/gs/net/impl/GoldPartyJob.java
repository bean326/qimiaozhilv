package cc.lv1.rpg.gs.net.impl;

import vin.rabbit.net.abs.NetConnection;
import vin.rabbit.net.job.i.NetJob;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.List;
import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.entity.controller.BattleController;
import cc.lv1.rpg.gs.entity.controller.BusinessController;
import cc.lv1.rpg.gs.entity.controller.GoldPVEController;
import cc.lv1.rpg.gs.entity.controller.GoldPVPController;
import cc.lv1.rpg.gs.entity.controller.GoldPartyController;
import cc.lv1.rpg.gs.entity.controller.PVEController;
import cc.lv1.rpg.gs.entity.controller.PartyController;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.controller.RoomController;
import cc.lv1.rpg.gs.entity.impl.PartyReward;
import cc.lv1.rpg.gs.entity.impl.Reward;
import cc.lv1.rpg.gs.net.SMsg;
import cc.lv1.rpg.gs.util.FontConver;

public class GoldPartyJob extends NetJob
{
	private WorldManager world;
	
	public GoldPartyJob(WorldManager world)
	{
		this.world = world;
	}

	public NetConnection getConnection() 
	{
		return null;
	}

	public void run()
	{
		List list = world.getPlayerList();
		for (int i = 0; i < list.size(); i++)
		{
			PlayerController player = (PlayerController) list.get(i);
			player.setGoldSignName("");
		}
	}

}
