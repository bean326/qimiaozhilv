package cc.lv1.rpg.gs.util;

import vin.rabbit.util.collection.i.List;
import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.dao.DatabaseAccessor;
import cc.lv1.rpg.gs.entity.controller.FamilyController;

public class FamilyUtils
{
	
	
	
	public static void main(String[] args)
	{
		GameServer.getInstance().initialWithoutNetServer();
		DatabaseAccessor da= GameServer.getInstance().getDatabaseAccessor();
		GameServer.getInstance().getWorldManager().setGameServer(GameServer.getInstance());
		da.loadWorldInfo(GameServer.getInstance().getWorldManager());
		
		List list = GameServer.getInstance().getWorldManager().getFamilyList();

		int size = list.size();

		for (int i = 0; i < size; i++)
		{
			FamilyController f = (FamilyController)list.get(i);
			da.createOrUpdateFamilyInfo(f, true);
		}
		
	}
	
}
