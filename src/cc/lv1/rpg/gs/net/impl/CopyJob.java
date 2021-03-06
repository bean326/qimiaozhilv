package cc.lv1.rpg.gs.net.impl;

import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.data.DC;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.container.PlayerContainer;
import cc.lv1.rpg.gs.entity.controller.BusinessController;
import cc.lv1.rpg.gs.entity.controller.CopyController;
import cc.lv1.rpg.gs.entity.controller.CopyPVEController;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.controller.RoomController;
import cc.lv1.rpg.gs.entity.ext.OtherExtInfo;
import cc.lv1.rpg.gs.util.FontConver;
import vin.rabbit.net.abs.NetConnection;
import vin.rabbit.net.job.i.NetJob;
import vin.rabbit.util.collection.i.List;

/**
 * 副本相关
 * @author bean
 *
 */
public class CopyJob extends NetJob
{
	
	WorldManager world;


	public NetConnection getConnection() 
	{
		return null;
	}
	
	public CopyJob(WorldManager world)
	{
		this.world = world;
	}


	public void run() 
	{
		List list = (List) DataFactory.getInstance().getAttachment(DataFactory.COPY_LIST);
		for (int j = 0; j < list.size(); j++) 
		{
			CopyController copy = (CopyController) list.get(j);
			copy.initTop();
		}
		
		world.initCopy();
		
		list = world.getPlayerList();
	
		for (int i = 0; i < list.size(); i++) 
		{
			PlayerController player = (PlayerController) list.get(i);
			if(player == null || !player.isOnline())
				continue;
			
			PlayerContainer container = player.getParent();
			
			if(container instanceof CopyPVEController)
			{
				CopyPVEController copy = (CopyPVEController) container;
				copy.setTimeOver(true);
			}
			else if(container instanceof BusinessController)
			{
				BusinessController bc = (BusinessController) container;
				bc.removePlayer(player);
			}
			if(container instanceof RoomController)
			{
				RoomController room = (RoomController) container;
				if(room.isCopyPartyRoom)
				{
					if(room.getCopy().isHonorCopy())
						player.moveToRoom(DataFactory.HONORROOM);
					else
						player.moveToRoom(DataFactory.INITROOM);
				}
				if(DataFactory.getInstance().isInConPartyRoom(room.id))
					player.moveToRoom(DataFactory.INITROOM);
			}
			
			if(player.getRoom().isCopyPartyRoom)
			{
				player.sendGetGoodsInfo(1, true, DC.getString(DC.BATTLE_4));
			}
			
			OtherExtInfo oei = (OtherExtInfo) player.getPlayer().getExtPlayerInfo("otherExtInfo");
			oei.clearAss();
		}
	}

}
