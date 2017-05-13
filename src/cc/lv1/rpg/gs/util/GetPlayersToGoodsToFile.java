package cc.lv1.rpg.gs.util;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.impl.ArrayList;
import vin.rabbit.util.connection.ConnectionFactory;
import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.dao.DatabaseAccessor;
import cc.lv1.rpg.gs.entity.ext.Bag;
import cc.lv1.rpg.gs.entity.ext.Storage;
import cc.lv1.rpg.gs.entity.impl.Player;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsEquip;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsProp;

/**
 * 取得拥有某物品的玩家到文件
 * 
 */
public class GetPlayersToGoodsToFile
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

			list = sourceDA.loadPlayers(ii, ii + 20000,WorldManager.getTypeTime("yyyy/MM/dd", System.currentTimeMillis()-((long)1000*(long)60*(long)60*(long)24*(long)90)),WorldManager.getTypeTime("yyyy/MM/dd", System.currentTimeMillis()+(1000*60*60*24)));

			ii += 20000;

			int size = list.size();
			Excel excel = null;

			if (size != 0)
			{
				excel = new Excel("player_info" + (++page) + ".xls");
				excel.addHead(new String[]
				{ "id", "\u8D26\u6237\u540D", "\u73A9\u5BB6\u540D",
						"\u7B49\u7EA7", "\u5145\u503C", "\u4EF7\u503C\u7269" ,"goodsId"});
			}

			for (int i = 0; i < size; i++)
			{
				Player player = (Player) list.get(i);

				if (player.isClosed)
					continue;

//				//两个月没登陆就不查了
//				if(player.lastTime > lasttime)
//					continue;
				
				int price = 0;

				Bag bag = (Bag) player.getExtPlayerInfo("bag");

				for (int j = 0; j < bag.goodsList.length; j++)
				{
					if (bag.goodsList[j] == null)
						continue;

					if (bag.goodsList[j] instanceof GoodsEquip)
					{
						GoodsEquip ge = (GoodsEquip) bag.goodsList[j];

						if (ge.isVIP)
						{
							price += ge.money;
						}
					} else if (bag.goodsList[j] instanceof GoodsProp)
					{
						if (bag.goodsList[j].id == 1045000135)
						{
							GoodsProp gp = (GoodsProp) bag.goodsList[j];
							price += gp.addMoney * gp.goodsCount;
						} else if (bag.goodsList[j].id == 1045000134)
						{
							GoodsProp gp = (GoodsProp) bag.goodsList[j];
							price += gp.addMoney * gp.goodsCount;
						} else if (bag.goodsList[j].id == 1045000136)
						{
							GoodsProp gp = (GoodsProp) bag.goodsList[j];
							price += gp.addMoney * gp.goodsCount;
						}

					}

				}

				price += bag.equipMoney;
				price += bag.money;

				Storage storage = (Storage) player.getExtPlayerInfo("storage");

				for (int j = 0; j < storage.getGoodsList().length; j++)
				{
					if (storage.getGoodsList()[j] == null)
						continue;

					if (storage.getGoodsList()[j] instanceof GoodsEquip)
					{
						GoodsEquip ge = (GoodsEquip) storage.getGoodsList()[j];

						if (ge.isVIP)
						{
							price += ge.money;
						}
					}
				}

				int payMoney = getMoney(player.accountName);

				List linelist = new ArrayList();

				linelist.add((i + 1));

				String name = player.accountName.replace("\n", "Enter");
				name = name.replace("\t", "Table");

				linelist.add(name);

				name = player.name.replace("\n", "Enter");
				name = name.replace("\t", "Table");

				linelist.add(name);
				linelist.add(player.level);
				linelist.add(payMoney);
				linelist.add(price);

				/*
				 * 真时之沙 1045000294 时之沙 1045000290 进化小元宝 1045010423 无双 1042200401
				 * 72变宝石 1045010406 王国之心 1042010105 影大黄蜂 1051010025 奇大黄蜂
				 * 1051030025 影十番队长 1051010026 奇十番队长 1051030026
				 * 
				 * 以上物品数量有2个以上的账号
				 * 
				 * 
				 * 
				 * 进化原石 1045010412 以上物品数量有10个以上的账号
				 * 
				 * 
				 * 紫药 1041030001 以上物品数量有50个以上的账号
				 */
				
				
				
				boolean flag = false;

				int[] idds = new int[]{ 1045000294, 1045000290, 1045010423, 1042200401, 1045010406,1045010406, 1042010105, 1051010025, 1051030025,1051010026, 1051030026 }; 
				int countt = 5; 

				flag = checkGoodsIn(idds,countt,bag,storage);
				
				idds = new int[]{ 1045010412 }; 
				countt = 50; 
				
				if(!flag)
				flag = checkGoodsIn(idds,countt,bag,storage);
				
				idds = new int[]{ 1041030001 }; // 填入过滤ID 有这个ID并且数量大于一定时才会进入表内
				countt = 200; 
				
				if(!flag)
				flag = checkGoodsIn(idds,countt,bag,storage);
				
				if(flag)
				{
					if(goodsnameMap == null)
						initGoodsName();
					
					
					StringBuffer sb = new StringBuffer();
					
					Set s = goodsnameMap.keySet();
					Iterator<Integer> iterator = s.iterator();
					while(iterator.hasNext())
					{
						Integer goodsIds = iterator.next();
						if(goodsIds != null)
						{
							int cccount = 0;
							cccount += bag.getGoodsCountById(goodsIds);
							cccount += storage.getGoodsCount(goodsIds);
							
							if(cccount != 0)
							{
								sb.append(goodsnameMap.get(goodsIds));
								sb.append("*");
								sb.append(cccount);	
							}

						}
					}
					
					linelist.add(sb.toString());
					
					excel.addLine(linelist);
					System.out.println("in..... "+(count++)+" "+player.name);
				}
				count++;
			}

			if (excel != null)
			{
				excel.close();
			}

		} while (list.size() > 0);

	}
	
	
	private boolean checkGoodsIn(int[] ids,int count,Bag bag,Storage storage)
	{
		for (int j = 0; j < ids.length; j++)
		{
			if (bag.getGoodsCountById(ids[j]) > count || storage.getGoodsCount(ids[j]) > count)
				return true;
		}
		return false;
	}
	

	private int getMoney(String accountName)
	{
		ConnectionFactory cf = sourceDA.getConnectionFactory();

		PreparedStatement statement = null;
		Connection conn = null;

		int money = 0;

		try
		{
			conn = cf.getConnection();
			synchronized (conn)
			{
				statement = conn
						.prepareStatement("select sum(payMoney) from T_Server_PAY where payType=1 and accountName=?");
				statement.setString(1, accountName);
				ResultSet rs = statement.executeQuery();
				while (rs.next())
				{
					money = rs.getInt(1);
				}
				statement.close();
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (statement != null)
					statement.close();

				cf.recycleConnection(conn);
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return money;
	}
	
	/*
	 * 真时之沙 1045000294 时之沙 1045000290 进化小元宝 1045010423 无双 1042200401
	 * 72变宝石 1045010406 王国之心 1042010105 影大黄蜂 1051010025 奇大黄蜂
	 * 1051030025 影十番队长 1051010026 奇十番队长 1051030026
	 * 
	 * 以上物品数量有2个以上的账号
	 * 
	 * 
	 * 
	 * 进化原石 1045010412 以上物品数量有10个以上的账号
	 * 
	 * 
	 * 紫药 1041030001 以上物品数量有50个以上的账号
	 */
	
	private HashMap<Integer, String> goodsnameMap;
	
	private void initGoodsName()
	{
		goodsnameMap = new HashMap<Integer, String>();
		goodsnameMap.put(1045000294, "\u771f\u65f6\u4e4b\u6c99");
		goodsnameMap.put(1045000290, "\u65f6\u4e4b\u6c99");
		goodsnameMap.put(1045010423, "\u8fdb\u5316\u5c0f\u5143\u5b9d");
		goodsnameMap.put(1042200401, "\u65e0\u53cc");
		goodsnameMap.put(1045010406, "72\u53d8\u5b9d\u77f3");
		goodsnameMap.put(1042010105, "\u738b\u56fd\u4e4b\u5fc3");
		goodsnameMap.put(1051010025, "\u5f71\u5927\u9ec4\u8702");
		goodsnameMap.put(1051030025, "\u5947\u5927\u9ec4\u8702");
		goodsnameMap.put(1051010026, "\u5f71\u5341\u756a\u961f\u957f");
		goodsnameMap.put(1051030026, "\u5947\u5341\u756a\u961f\u957f");
		goodsnameMap.put(1045010412, "\u8fdb\u5316\u539f\u77f3");
		goodsnameMap.put(1041030001, "\u7d2b\u836f");
	}


	public static void main(String[] args) throws Exception
	{
		GetPlayersToGoodsToFile gtf = new GetPlayersToGoodsToFile();
		gtf.init();
		gtf.run();
	}
}
