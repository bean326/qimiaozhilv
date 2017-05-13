package cc.lv1.rpg.gs.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.impl.ArrayList;
import vin.rabbit.util.connection.ConnectionFactory;
import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.dao.DatabaseAccessor;
import cc.lv1.rpg.gs.entity.ext.Bag;
import cc.lv1.rpg.gs.entity.ext.Storage;
import cc.lv1.rpg.gs.entity.impl.Player;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsEquip;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsProp;

/**
 * 取得所有玩家的剩余元宝
 * @author dxw
 *
 */
public class GetPlayersMoneyToFile
{
	private DatabaseAccessor sourceDA;
	
	public void init()
	{
		GameServer.getInstance().initialWithoutNetServer();
		sourceDA = GameServer.getInstance().getDatabaseAccessor();
	}
	
	public void run() throws Exception
	{
		long totalcount = 0;
		List list = null;
		
		int count = 0;
		int ii = 0;
		int page = 0;
		do
		{
		

		list = sourceDA.loadPlayers(ii, ii+20000);
		
		ii += 20000;
		
		int size = list.size();
		Excel excel = null;
		
		if(size != 0)
		{
			excel = new Excel("player_info_money"+(++page)+".xls");
			excel.addHead(new String[]{"id","\u8D26\u6237\u540D","\u73A9\u5BB6\u540D","\u7B49\u7EA7","less_yuanbao"});
		}

		
		for (int i = 0; i < size; i++)
		{
			Player player = (Player)list.get(i);
			
			if(player.isClosed)
				continue;
			
			
			Bag bag =(Bag)player.getExtPlayerInfo("bag");
			
			if(bag.money <= 0)
			{
				continue;
			}

			List linelist = new ArrayList();
			
			linelist.add((i+1));
			
			String name =  player.accountName.replace("\n", "Enter");
			name = name.replace("\t", "Table");
			
			linelist.add(name);

			name =  player.name.replace("\n", "Enter");
			name = name.replace("\t", "Table");
			
			linelist.add(name);
			linelist.add(player.level);

			linelist.add(bag.money);

			excel.addLine(linelist);
			System.out.println((count++)+" "+player.name+" "+bag.money);
			
			totalcount += bag.money;
		}
		
			if(excel != null)
				excel.close();
		
		}
		while(list.size() > 0);
		
		
		System.out.println("Total less money = "+totalcount);
		
	}
	
	
	public static void main(String[] args) throws Exception
	{
		GetPlayersMoneyToFile gtf = new GetPlayersMoneyToFile();
		gtf.init();
		gtf.run();
	}
	
}
