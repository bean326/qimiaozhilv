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
 * 取得所有玩家的信息到文件
 * @author dxw
 *
 */
public class GetPlayersToFile
{
	private DatabaseAccessor sourceDA;
	
	public void init()
	{
		GameServer.getInstance().initialWithoutNetServer();
		sourceDA = GameServer.getInstance().getDatabaseAccessor();
	}
	
	public void run() throws Exception
	{
		
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
			excel = new Excel("player_info"+(++page)+".xls");
			excel.addHead(new String[]{"id","\u8D26\u6237\u540D","\u73A9\u5BB6\u540D","\u7B49\u7EA7","\u5145\u503C","\u4EF7\u503C\u7269"});
		}

		
		for (int i = 0; i < size; i++)
		{
			Player player = (Player)list.get(i);
			
			if(player.isClosed)
				continue;
			
			int price = 0;
			
			Bag bag =(Bag)player.getExtPlayerInfo("bag");
			
			for (int j = 0; j < bag.goodsList.length; j++)
			{
				if(bag.goodsList[j] == null)
					continue;
				             
				
				if(bag.goodsList[j] instanceof GoodsEquip)
				{
					GoodsEquip ge = (GoodsEquip)bag.goodsList[j];
					
					if(ge.isVIP)
					{
						price += ge.money;
					}
				}
				else if(bag.goodsList[j] instanceof GoodsProp)
				{
					if(bag.goodsList[j].id == 1045000135)
					{
						GoodsProp gp = (GoodsProp)bag.goodsList[j];
						price += gp.addMoney*gp.goodsCount;
					}
					else if(bag.goodsList[j].id == 1045000134)
					{
						GoodsProp gp = (GoodsProp)bag.goodsList[j];
						price += gp.addMoney*gp.goodsCount;
					}
					else if(bag.goodsList[j].id == 1045000136)
					{
						GoodsProp gp = (GoodsProp)bag.goodsList[j];
						price += gp.addMoney*gp.goodsCount;
					}
					
					
				}

			}
			
			price += bag.equipMoney;
			price += bag.money;
			
			
			Storage storage =(Storage)player.getExtPlayerInfo("storage");
			
			
			for (int j = 0; j < storage.getGoodsList().length; j++)
			{
				if(storage.getGoodsList()[j] == null)
					continue;
				
				if(storage.getGoodsList()[j] instanceof GoodsEquip)
				{
					GoodsEquip ge = (GoodsEquip)storage.getGoodsList()[j];
					
					if(ge.isVIP)
					{
						price += ge.money;
					}
				}
			}
			
			int payMoney = getMoney(player.accountName);
		    
			List linelist = new ArrayList();
			
			linelist.add((i+1));
			
			String name =  player.accountName.replace("\n", "Enter");
			name = name.replace("\t", "Table");
			
			linelist.add(name);

			name =  player.name.replace("\n", "Enter");
			name = name.replace("\t", "Table");
			
			linelist.add(name);
			linelist.add(player.level);
			linelist.add(payMoney);
			linelist.add(price);
			
			excel.addLine(linelist);
			System.out.println((count++)+" "+player.name+" "+price);
		}
		
			if(excel != null)
				excel.close();
		
		}
		while(list.size() > 0);
		
		
		
		
	}
	
	
	private int getMoney(String accountName)
	{
		ConnectionFactory cf = sourceDA.getConnectionFactory();
		
		PreparedStatement statement = null;
		Connection conn = null;
		
		int money= 0;

		try 
		{
			conn = cf.getConnection();
			synchronized(conn)
			{	
				statement = conn.prepareStatement("select sum(payMoney) from T_Server_PAY where payType=1 and accountName=?");
				statement.setString(1, accountName);
				ResultSet rs = statement.executeQuery();
				while(rs.next())
				{
					money = rs.getInt(1);
				}
				statement.close();
			}	
		} 
		catch(Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(statement != null)
					statement.close();
				
				cf.recycleConnection(conn);
			} 
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return money;
	}
	
	public static void main(String[] args) throws Exception
	{
		GetPlayersToFile gtf = new GetPlayersToFile();
		gtf.init();
		gtf.run();
	}
	
}
