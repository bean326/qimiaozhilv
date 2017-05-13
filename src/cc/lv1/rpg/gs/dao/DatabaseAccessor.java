package cc.lv1.rpg.gs.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import vin.rabbit.net.abs.NetConnection;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.impl.ArrayList;
import vin.rabbit.util.connection.ConnectionFactory;
import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.data.DC;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.controller.FamilyController;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.ext.Bag;
import cc.lv1.rpg.gs.entity.ext.MailBox;
import cc.lv1.rpg.gs.entity.ext.OtherExtInfo;
import cc.lv1.rpg.gs.entity.ext.PetTome;
import cc.lv1.rpg.gs.entity.impl.Goods;
import cc.lv1.rpg.gs.entity.impl.Mail;
import cc.lv1.rpg.gs.entity.impl.Player;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsEquip;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsProp;
import cc.lv1.rpg.gs.gui.MainFrame;
import cc.lv1.rpg.gs.net.impl.PressureJob;
import cc.lv1.rpg.gs.net.impl.SaveJob;
import cc.lv1.rpg.gs.net.impl.ValidateJob;
import cc.lv1.rpg.gs.util.FontConver;
import cc.lv1.rpg.gs.util.MergeStart;

public class DatabaseAccessor 
{ 
 
	private ConnectionFactory cf;
	
	private String dbhost;
	
	private String dbport;
	
	private String dbname;
	
	private String user;
	
	private String pwd;
	
	private String heart;
	
	public static int roleObjectIndex = 0;
	
	public static long goodsObjectIndex = 0;
	
	public static int familyObjectIndex = 0;

	public DatabaseAccessor(String dbhost,String dbport,String dbname, String user, String pwd,String heart)
	{
		this.heart = heart;
		this.dbhost = dbhost;
		this.dbport = dbport;
		this.dbname = dbname;
		this.user = user;
		this.pwd = pwd;
		this.initial();
	}
	
	private DatabaseAccessor(String dbhost,String dbport,String dbname, String user, String pwd)
	{
		this.dbhost = dbhost;
		this.dbport = dbport;
		this.dbname = dbname;
		this.user = user;
		this.pwd = pwd;
		this.initial();
	}
	
	private void initial()
	{
		if(heart.equals("sqlserver"))
			cf = new ConnectionFactory.SqlServerFactory(dbhost,dbport,dbname,user,pwd);
		else if(heart.equals("mysql"))
			cf = new ConnectionFactory.MysqlFactory(dbhost,dbport,dbname,user,pwd);
		else
		{
			System.out.println("Database Error : heart is "+heart);
			System.exit(-1);
		}
	}
	
	/** 保存player信息 */
	public boolean savePlayer(Player player)
	{
		boolean isSaveSuccess = false;
		CallableStatement callableStatement = null;
		Connection conn = null;

		ByteBuffer buffer = new ByteBuffer(4096 * 10);
		player.saveTo(buffer);
		byte[] data = buffer.getBytes();
		
		try 
		{

			conn = cf.getConnection();
			synchronized(conn)
			{
				callableStatement = conn.prepareCall("{call PlayerSave(?,?,?,?,?)}");
				callableStatement.setBytes(1, data);
				callableStatement.setString(2, player.accountName);
				callableStatement.setInt(3, player.id);
				callableStatement.setString(4,player.name);
				callableStatement.setInt(5, 0);
				isSaveSuccess = callableStatement.execute();
				callableStatement.close();
			}
			cf.recycleConnection(conn);
		} 
		catch(Exception e) 
		{
//			e.printStackTrace();
			System.out.println("1 "+e.getMessage());
			try 
			{
			conn = cf.getConnection();
			synchronized(conn)
			{
				callableStatement = conn.prepareCall("{call PlayerSave(?,?,?,?,?)}");
				callableStatement.setBytes(1, data);
				callableStatement.setString(2, player.accountName);
				callableStatement.setInt(3, player.id);
				callableStatement.setString(4,player.name);
				callableStatement.setInt(5, 0);
				isSaveSuccess = callableStatement.execute();
				callableStatement.close();
			}
			cf.recycleConnection(conn);
			
			} 
			catch(Exception ee) 
			{
				System.out.println("2 "+ee.getMessage());
			}
		}
		return isSaveSuccess;
	}
	
	/** 保存备份player信息(下线时才保存) */
	public boolean savePlayerToBak(Player player)
	{
		boolean isSaveSuccess = false;
		CallableStatement callableStatement = null;
		Connection conn = null;

		ByteBuffer buffer = new ByteBuffer(4096 * 10);
		player.saveTo(buffer);
		byte[] data = buffer.getBytes();
	
		try 
		{

			conn = cf.getConnection();
			synchronized(conn)
			{
				callableStatement = conn.prepareCall("{call PlayerBakSave(?,?,?,?,?)}");
				callableStatement.setBytes(1, data);
				callableStatement.setString(2, player.accountName);
				callableStatement.setInt(3, player.id);
				callableStatement.setString(4,player.name);
				callableStatement.setInt(5, 0);
				isSaveSuccess = callableStatement.execute();
				callableStatement.close();
			}
			cf.recycleConnection(conn);
		} 
		catch(Exception e) 
		{
//			e.printStackTrace();
			System.out.println("bak1 "+e.getMessage());
			try 
			{
			conn = cf.getConnection();
			synchronized(conn)
			{
				callableStatement = conn.prepareCall("{call PlayerBakSave(?,?,?,?,?)}");
				callableStatement.setBytes(1, data);
				callableStatement.setString(2, player.accountName);
				callableStatement.setInt(3, player.id);
				callableStatement.setString(4,player.name);
				callableStatement.setInt(5, 0);
				isSaveSuccess = callableStatement.execute();
				callableStatement.close();
			}
			cf.recycleConnection(conn);
			
			} 
			catch(Exception ee) 
			{
				System.out.println("bak2 "+ee.getMessage());
			}
		}
		return isSaveSuccess;
	}
	
	public Player getBakPlayer(String accountName)
	{
		CallableStatement callableStatement = null;
		Connection conn = null;
		
		byte[] data = null;
		int type = 0;
		try 
		{
			conn = cf.getConnection();
			synchronized(conn)
			{
				callableStatement = conn.prepareCall("{call PlayerBakLoad(?)}");
				callableStatement.setString(1, accountName);
				ResultSet result = callableStatement.executeQuery();
				while(result.next())
				{
					data = result.getBytes("playerdata");
					type = result.getInt("playerType");
				}
				callableStatement.close();
			}
			cf.recycleConnection(conn);
		} 
		catch (Exception e) 
		{
			return null;
		} 
			
		if (data == null)
		{
			return null;
		}
		else
		{
			if(type == 1)
				return null;
			Player player = new Player();	
			ByteBuffer buffer = new ByteBuffer(data);
			player.loadFrom(buffer);
			player.initial(0);
			return player;
		}
	}
	
	/**读取player信息*/
	public void loadPlayer(String accountName ,ValidateJob.PlayerLoader loader)
	{
 		CallableStatement callableStatement = null;
		Connection conn = null;
		
		byte[] data = null;
		int type = 0;
		try 
		{
			conn = cf.getConnection();
			synchronized(conn)
			{
				callableStatement = conn.prepareCall("{call PlayerLoad(?)}");
				callableStatement.setString(1, accountName);
				ResultSet result = callableStatement.executeQuery();
				while(result.next())
				{
					data = result.getBytes("playerdata");
					type = result.getInt("playerType");
				}
				callableStatement.close();
			}
			cf.recycleConnection(conn);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			if(loader != null)
				loader.loadPlayer(ValidateJob.PlayerLoader.EXCEPT_PLAYER, null);
			return;
		} 
			
		if (data == null)
		{
			if(loader != null)
			{
				loader.loadPlayer(ValidateJob.PlayerLoader.NEW_PLAYER, null);
			}
		}
		else
		{
			Player player = new Player();	
			ByteBuffer buffer = new ByteBuffer(data);
			player.loadFrom(buffer);
			player.initial(0);
			if(type == 1)
			{
				if(loader != null)
					loader.loadPlayer(ValidateJob.PlayerLoader.CLOSE_PLAYER, player);
			}
			else
			{
				Player bakPlayer = getBakPlayer(accountName);
				if(bakPlayer != null)
				{
					boolean isChange = false;
					int bakZ = bakPlayer.getZhuanshengState();
					int locZ = player.getZhuanshengState();
					if(bakZ > locZ)
					{
						isChange = true;
					}
					else if(bakZ == locZ)
					{
						if(bakPlayer.experience > player.experience)
						{
							isChange = true;
						}
					}	
					
					if(isChange)
					{
						StringBuffer sb = new StringBuffer();
						sb.append("acountName:");
						sb.append(player.accountName);
						sb.append(" name:");
						sb.append(player.name);
						sb.append(" bakData[level:");
						sb.append(bakPlayer.level);
						sb.append(" job:");
						sb.append(bakPlayer.upProfession);
						sb.append("]localPlayerData[level:");
						sb.append(player.level);
						sb.append(" job:");
						sb.append(player.upProfession);
						sb.append("]");
						GameServer.getInstance().getWorldManager().getJobObserver()
							.addJob(GameServer.JOB_GAME2, new PressureJob(sb.toString(),"bakDataChange"));
							
						player = bakPlayer;
					}
				}
				
				checkPlayerPay(player);
				checkPlayerOtherPay(player);
				checkGoodsWithoutInGame(player);
				checkIsVipPlayer(player);

				if(loader != null)
					loader.loadPlayer(ValidateJob.PlayerLoader.OLD_PLAYER, player);
			}

		}
	}
	
	private void checkIsVipPlayer(Player player)
	{
		PreparedStatement statement = null;
		Connection conn = null;
		int money = 0;
		try 
		{
			conn = cf.getConnection();
			synchronized(conn)
			{
				statement = conn.prepareStatement("select sum(payMoney) from T_Server_Pay where accountName=? and payType=1");
				statement.setString(1, player.accountName);
				ResultSet result = statement.executeQuery();
				
				while(result.next())
				{
					money = result.getInt(1);
				}
				statement.close();
			}
			cf.recycleConnection(conn);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		} 
		
		if(money > 0)
		{
			player.isVipPlayer = true;
		}
		if(money >= 10)
		{
			player.chengjiu = 100;
		}
	}

	private void checkPlayerOtherPay(Player player)
	{
		int count = getPlayerOtherPayWithoutInGame(player.accountName);
		if(count != 0)
		{
			PreparedStatement statement = null;
			Connection conn = null;

			try 
			{
				conn = cf.getConnection();
				synchronized(conn)
				{
					for (int i = 0; i < count; i++)
					{
						Mail mail = new Mail(DC.getString(DC.BASE_1),System.currentTimeMillis());
						mail.setTitle("*"+DC.getString(DC.BASE_2));//礼包
						
						Goods [] goods = DataFactory.getInstance().makeGoods(1045013011,1);
						if(goods != null)
						{
							mail.addAttach(goods[0]);
						}
						
						mail.sendOffLine(player);
					}

				
					statement = conn.prepareStatement("update T_Server_Pay set payState=0 where accountName=? and payType=4");//type ==4 包月用户  state==1 没冲到账户
					statement.setString(1, player.accountName);
					statement.execute();

					statement.close();
				}
				cf.recycleConnection(conn);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			} 
		}
	}
	
	/**
	 * 取得其他支付奖励
	 * @param accountName
	 * @return
	 */
	private int getPlayerOtherPayWithoutInGame(String accountName)
	{
		PreparedStatement statement = null;
		Connection conn = null;
		int count = 0;
		try 
		{
			conn = cf.getConnection();
			synchronized(conn)
			{
				statement = conn.prepareStatement("select payMoney from T_Server_Pay where accountName=? and payType=4 and payState=1");//type ==4 包月奖励  state==1 没冲到账户
				statement.setString(1, accountName);
				ResultSet result = statement.executeQuery();
				
				while(result.next())
				{
					if(result.getInt(1) == 0)
					{
						count++;
					}
				}
				statement.close();
			}
			cf.recycleConnection(conn);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		} 
		return count;
	}
	
	private void checkGoodsWithoutInGame(Player player)
	{
		ArrayList goodsList = getGoodsWithoutInGame(player.accountName);
		if(goodsList != null)
		{
			PreparedStatement statement = null;
			Connection conn = null;

			try 
			{
				conn = cf.getConnection();
				synchronized(conn)
				{
					Mail mail = new Mail(DC.getString(DC.BASE_1),System.currentTimeMillis());
					mail.setTitle("*"+DC.getString(DC.BASE_2));//礼包
					
					int size = goodsList.size();
					for (int i = 0; i < size; i++)
					{
						Goods newGoods = (Goods) goodsList.get(i);
						mail.addAttach(newGoods);
						if(mail.getAttachCount() == 2 && i < size - 1)
						{	
							mail.sendOffLine(player);
							mail = new Mail(DC.getString(DC.BASE_1),System.currentTimeMillis());
							mail.setTitle("*"+DC.getString(DC.BASE_2));//礼包
						}
						if(i == size - 1)
						{
							mail.sendOffLine(player);
						}
					}
				
					statement = conn.prepareStatement("update T_Server_Pay set payState=0 where accountName=? and payType=5");
					statement.setString(1, player.accountName);
					statement.execute();
					statement.close();
				}
				cf.recycleConnection(conn);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			} 
		}
	}
	
	/**
	 * 取得物品上下线
	 * @param accountName
	 * @return
	 */
	private ArrayList getGoodsWithoutInGame(String accountName)
	{
		PreparedStatement statement = null;
		Connection conn = null;
		ArrayList goodsList = null;
		try 
		{
			conn = cf.getConnection();
			synchronized(conn)
			{
				statement = conn.prepareStatement("select payExp from T_Server_Pay where accountName=? and payType=5 and payState=1");
				statement.setString(1, accountName);
				ResultSet result = statement.executeQuery();
				String s = null;
				String []ss = null;
				while(result.next())
				{
					s = result.getString(1);
					ss = Utils.split(s, ":");
					if(ss.length != 3)
						continue;
					
					// ss[1] 是goods id
					
					try
					{
						Object obj = DataFactory.getInstance().getGameObject(Integer.parseInt(ss[1]));
						if(!(obj instanceof Goods))
							continue;
						
						 Goods [] goods = DataFactory.getInstance().makeGoods(Integer.parseInt(ss[1]),1);
	
						 if(goods != null)
						 {
							 if(goodsList == null)
								 goodsList = new ArrayList();
							 
							 goodsList.add(goods[0]);
						 }
					}
					catch(NumberFormatException e)
					{
						continue;
					}
					
				}
				statement.close();
			}
			cf.recycleConnection(conn);

			return goodsList;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		} 
		return goodsList;
	}

	/**
	 * 检查玩家是否有充值
	 * @param player
	 */
	private void checkPlayerPay(Player player)
	{
		int money = getPlayerPayWithoutInGame(player.accountName);

		if(money <=0)
			return;
		
		PreparedStatement statement = null;
		Connection conn = null;

		try 
		{
			conn = cf.getConnection();
			synchronized(conn)
			{
				Bag bag = (Bag)player.getExtPlayerInfo("bag");
				bag.money += money;

				statement = conn.prepareStatement("update T_Server_Pay set payState=0 where accountName=? and payType=1");//type ==1 充值  state==1 没冲到账户
				statement.setString(1, player.accountName);
				statement.execute();
				
				savePlayer(player);
				statement.close();
			}
			cf.recycleConnection(conn);
			
			Mail selfMail = new Mail(DC.getString(DC.BASE_3));
			selfMail.setTitle("*"+DC.getString(DC.BASE_20));
			//给族长分成
			if(player.familyId != 0)
			{
				FamilyController family = GameServer.getInstance().getWorldManager().getFamilyById(player.familyId);
				if(family != null)
				{
					String leaderName = "";
					//充值小于50元宝 不派发族长代金券
					if(money >= 50)
					{
						int lMoney = money / 10 * 2;
						lMoney = lMoney == 0 ? 1 :lMoney;
						//代金卷id  1045000134 
						GoodsProp goods = (GoodsProp)DataFactory.getInstance().getGameObject(1045000134);
						
						int goodsCount = lMoney/goods.addMoney;
						goodsCount = goodsCount == 0 ? 1 :goodsCount;
						
						int size =  (int) Math.ceil((double)goodsCount/(double)goods.repeatNumber);
						
						Player leader = null;
						for (int i = 0; i < size; i++)
						{
							Mail mail = new Mail(DC.getString(DC.BASE_3)); //系统
							mail.setTitle("*"+DC.getString(DC.BASE_4));//家族基金
							StringBuffer sb = new StringBuffer();
							sb.append(DC.getString(DC.BASE_5));//
							sb.append("'");
							sb.append(player.name);
							sb.append("'");
							sb.append(DC.getString(DC.BASE_6));
							sb.append(money);
							sb.append(DC.getString(DC.BASE_7));
							sb.append(",");
							sb.append(DC.getString(DC.BASE_8));
							mail.setContent(sb.toString());
							
							//代金卷id  1045000134 
							Goods[] goodss= DataFactory.getInstance().makeGoods(1045000134,goodsCount > goods.repeatNumber?goods.repeatNumber:goodsCount);	
							
							mail.addAttach(goodss[0]);
							
						
							WorldManager world = GameServer.getInstance().getWorldManager();
							PlayerController target = world.getPlayer(family.leaderId);
							
							if(target == null)
							{
								if(family.leaderId != player.id)
								{
									List conns = world.getConnections();
									
									NetConnection leaderNc = null;
									
									for (int j = 0; j < conns.size(); j++)
									{
										NetConnection nc = (NetConnection)conns.get(j);
										
										if(nc == null)
											continue;
										
										if(nc.getInfo() instanceof Player)
										{
											Player p = (Player)nc.getInfo();
											if(p.id == family.leaderId)
											{
												leaderNc = nc;
												break;
											}
										}
									}
									
									if(leaderNc == null) //说明族长完全不在线
									{
										if(leader == null)
										{
											leader = getPlayer(family.leaderId);
										}
										if(leader == null)
										{
											continue;
										}
										
										MailBox mb = (MailBox)leader.getExtPlayerInfo("mailbox");
										mb.addMail(new PlayerController(leader),mail);
										
										world.getJobObserver().addJob(GameServer.JOB_DATABASEJOB,
												new SaveJob(world,leader,SaveJob.FAMILY_LEADER_SAVE));
										
										leaderName = leader.name;
									}
									else //说明族长正在登陆状态
									{
										Player leaderPlayer = (Player)leaderNc.getInfo();
										Bag bag = (Bag)leaderPlayer.getExtPlayerInfo("bag");
										bag.equipMoney += (goodsCount > goods.repeatNumber?goods.repeatNumber:goodsCount)*10;
										
										leaderName = leaderPlayer.name;
									}
								}
							}
							else
							{//玩家现在还没进入游戏里面，所以自己不可能在游戏里
								if(family.leaderId != player.id) //族长在线，但自己不是族长
								{
									mail.send(target);//向族长发放邮件
									leaderName = target.getName();
								}
							}
							goodsCount -= goods.repeatNumber;
						}
					}//if(money>=50)
					if(family.leaderId != player.id)//自己不是族长
					{
						selfMail.setContent(DC.getString(DC.BASE_17).replace("NN", money+"").replace("FF", family.name).replace("XX", leaderName));
					}
					else//自己是族长	
					{
						selfMail.setContent(DC.getString(DC.BASE_18).replace("NN", money+"").replace("FF", family.name).replace("XX", player.name));
					}
				}//family!=null
			}//player.familyId!=0
			else//player.familyId==0没有家族
			{
				selfMail.setContent(DC.getString(DC.BASE_19).replace("NN", money+""));
			}
			
			MailBox mb = (MailBox)player.getExtPlayerInfo("mailbox");
			mb.addMail(new PlayerController(player),selfMail);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		} 
	}
	
	/**
	 * 取得玩家的充值   此充值是指玩家从平台充了后并未充游戏
	 * @param accountName
	 */
	public int getPlayerPayWithoutInGame(String accountName)
	{
		PreparedStatement statement = null;
		Connection conn = null;
		int money = 0;
		try 
		{
			conn = cf.getConnection();
			synchronized(conn)
			{
				statement = conn.prepareStatement("select sum(payMoney) from T_Server_Pay where accountName=? and payType=1 and payState=1");//type ==1 充值  state==1 没冲到账户
				statement.setString(1, accountName);
				ResultSet result = statement.executeQuery();
				
				while(result.next())
				{
					money = result.getInt(1);
				}
				statement.close();
			}
			cf.recycleConnection(conn);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		} 
		return money;
	}
	
	
	
	public Player getPlayer(String accountName)
	{
 		CallableStatement callableStatement = null;
		Connection conn = null;
		
		byte[] data = null;
		try 
		{
			conn = cf.getConnection();
			
			synchronized(conn)
			{
				callableStatement = conn.prepareCall("{call PlayerLoad(?)}");
				callableStatement.setString(1, accountName);
				ResultSet result = callableStatement.executeQuery();
				while(result.next())
				{
					data = result.getBytes("playerdata");
				}
				callableStatement.close();
			}
			cf.recycleConnection(conn);
			
			
			if (data == null)
			{
				return null;
			}
			else
			{
				Player player = new Player();	
				ByteBuffer buffer = new ByteBuffer(data);
				player.loadFrom(buffer);
				player.initial(0);
				return player;
			}
		} 
		catch (Exception e) 
		{
			System.out.println(accountName+"  " + e.getMessage());
			return null;
		}
	}
	
	public Player getPlayer(int id)
	{
 		CallableStatement callableStatement = null;
		Connection conn = null;
		
		byte[] data = null;
		try 
		{
			conn = cf.getConnection();
			
			synchronized(conn)
			{
				callableStatement = conn.prepareCall("{call PlayerLoadWithId(?)}");
				callableStatement.setInt(1, id);
				ResultSet result = callableStatement.executeQuery();
				while(result.next())
				{
					data = result.getBytes("playerdata");
				}
				callableStatement.close();
			}
			cf.recycleConnection(conn);
		} 
		catch (Exception e) 
		{
			return null;
		} 
			
		if (data == null)
		{
			return null;
		}
		else
		{
			Player player = new Player();	
			ByteBuffer buffer = new ByteBuffer(data);
			player.loadFrom(buffer);
			player.initial(0);
			return player;
		}
	}
	
	
	public byte[] loadPlayerData(String accountName)
	{
 		CallableStatement callableStatement = null;
		Connection conn = null;
		
		byte[] data = null;
		try 
		{
			conn = cf.getConnection();
			synchronized(conn)
			{
				callableStatement = conn.prepareCall("{call PlayerLoad(?)}");
				callableStatement.setString(1, accountName);
				ResultSet result = callableStatement.executeQuery();
				while(result.next())
				{
					data = result.getBytes("playerdata");
				}
				callableStatement.close();
			}
			cf.recycleConnection(conn);
		} 
		catch (Exception e) 
		{
			return null;
		}
		return data;
	}
	
	

	public void savePlayerData(int id,String accountName,byte[] data)
	{
		CallableStatement callableStatement = null;
		Connection conn = null;

		try 
		{

			conn = cf.getConnection();
			synchronized(conn)
			{
				callableStatement = conn.prepareCall("{call PlayerSave(?,?,?,?,?)}");
				callableStatement.setBytes(1, data);
				callableStatement.setString(2, accountName);
				callableStatement.setInt(3, id);
				callableStatement.setString(4,accountName);
				callableStatement.setInt(5, 0);
				callableStatement.execute();
				callableStatement.close();
			}
			cf.recycleConnection(conn);
		} 
		catch(Exception e) 
		{
			e.printStackTrace();
		}
	}


	//lasttime > 
	/**
	 * 取得所有玩家 分页  备份专用
	 * 数量 开始 --- 结束
	 * 0--10
	 * 10--20
	 * 20--30
	 */
	public List loadPlayersWithBackup(int begin,int end)
	{
		long currTime = System.currentTimeMillis();
		currTime -= (1000l*60l*60l*24l*30l);
		String beforeOneMonthTime = WorldManager.getTypeTime("yyyy/MM/dd", currTime);
		
		PreparedStatement statement = null;
		Connection conn = null;
		List list = new ArrayList(end-begin);
		byte[] data = null;
		int type = 0;
		String playerName = null;

		try 
		{
			conn = cf.getConnection();
			synchronized(conn)
			{
				statement = conn.prepareStatement("SELECT playerName,playerType,playerdata " +
						"FROM Playerinfo" +
						" WHERE ID in" +
						"(" +
						"SELECT TOP "+(end-begin)+" ID " +
						"FROM Playerinfo " +
						"WHERE (ID NOT IN (SELECT TOP "+begin+" id FROM Playerinfo ORDER BY id))ORDER BY ID" +
						")" +
						" and " +
						"lasttime > "+"'"+beforeOneMonthTime+"'");
				ResultSet result = statement.executeQuery();
				while(result.next())
				{
					
					playerName = result.getString("playerName");
					type = result.getInt("playerType");
					data = result.getBytes("playerdata");
				
					Player player = null;
					try
					{
						player = new Player();	
						ByteBuffer buffer = new ByteBuffer(data);
						player.loadFrom(buffer);
						player.initial(0);
						
						player.isClosed = type == 1? true : false; //是否封号
					}
					catch(Exception e)
					{
						System.out.println("error player data....."+player.name+"  "+player.id);
						continue;
					}

					list.add(player);
				}
				statement.close();
			}
			cf.recycleConnection(conn);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		} 
		return list;
	}
	
	
	/**
	 * 取得所有玩家 分页
	 * 数量 开始 --- 结束
	 * 0--10
	 * 10--20
	 * 20--30
	 */
	public List loadPlayers(int begin,int end)
	{
		PreparedStatement statement = null;
		Connection conn = null;
		List list = new ArrayList(end-begin);
		byte[] data = null;
		int type = 0;
		long lastTime = 0;
		String playerName = null;
		try 
		{
			conn = cf.getConnection();
			synchronized(conn)
			{
				statement = conn.prepareStatement("SELECT TOP "+(end-begin)+" playerName,playerType,playerdata,lastTime FROM Playerinfo WHERE (ID NOT IN (SELECT TOP "+begin+" id FROM Playerinfo ORDER BY id))ORDER BY ID ");
				ResultSet result = statement.executeQuery();
				while(result.next())
				{
					
					playerName = result.getString("playerName");
					type = result.getInt("playerType");
					data = result.getBytes("playerdata");
					lastTime = result.getDate("lastTime").getTime();

					
					Player player = null;
					try
					{
						player = new Player();	
						ByteBuffer buffer = new ByteBuffer(data);
						player.loadFrom(buffer);
						player.initial(0);

						player.lastTime = lastTime;
						
						player.isClosed = type == 1? true : false; //是否封号
					}
					catch(Exception e)
					{
						//e.printStackTrace();
						System.out.println("error player data....."+player.name+"  "+player.id);
						continue;
					}

					list.add(player);
				}
				statement.close();
			}
			cf.recycleConnection(conn);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		} 
		return list;
	}
	
	
	/**
	 * 取得所有玩家 分页
	 * 数量 开始 --- 结束
	 * 0--10
	 * 10--20
	 * 20--30
	 */
	public List loadPlayers(int begin,int end,String startTime,String endTime)
	{
		
		System.out.println("SELECT TOP "+(end-begin)+" playerName,playerType,playerdata,lastTime FROM Playerinfo WHERE (ID NOT IN (SELECT TOP "+begin+" id FROM Playerinfo where lasttime between '"+startTime+"' and '"+endTime+"' ORDER BY id)) and lasttime between '"+startTime+"' and '"+endTime+"' ORDER BY ID ");
		
		PreparedStatement statement = null;
		Connection conn = null;
		List list = new ArrayList(end-begin);
		byte[] data = null;
		int type = 0;
		long lastTime = 0;
		String playerName = null;
		try 
		{
			conn = cf.getConnection();
			synchronized(conn)
			{
				statement = conn.prepareStatement("SELECT TOP "+(end-begin)+" playerName,playerType,playerdata,lastTime FROM Playerinfo WHERE (ID NOT IN (SELECT TOP "+begin+" id FROM Playerinfo where lasttime between '"+startTime+"' and '"+endTime+"' ORDER BY id)) and lasttime between '"+startTime+"' and '"+endTime+"' ORDER BY ID ");
				ResultSet result = statement.executeQuery();
				while(result.next())
				{
					
					playerName = result.getString("playerName");
					type = result.getInt("playerType");
					data = result.getBytes("playerdata");
					lastTime = result.getDate("lastTime").getTime();

					
					Player player = null;
					try
					{
						player = new Player();	
						ByteBuffer buffer = new ByteBuffer(data);
						player.loadFrom(buffer);
						player.initial(0);

						player.lastTime = lastTime;
						
						player.isClosed = type == 1? true : false; //是否封号
					}
					catch(Exception e)
					{
						//e.printStackTrace();
						System.out.println("error player data....."+player.name+"  "+player.id);
						continue;
					}

					list.add(player);
				}
				statement.close();
			}
			cf.recycleConnection(conn);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		} 
		return list;
	}
	
	/**
	 * 取得所有玩家
	 * @return
	 */
	public List loadPlayers()
	{
		Statement statement = null;
		Connection conn = null;
		List list = new ArrayList(2000);
		byte[] data = null;
		String accountName = null;
		try 
		{
			conn = cf.getConnection();
			synchronized(conn)
			{
				statement = conn.createStatement();
				ResultSet result = statement.executeQuery("select accountName,playerData from playerInfo");
				while(result.next())
				{
					accountName = result.getString("accountName");
					data = result.getBytes("playerdata");
					
					Player player = null;
					try
					{
						player = new Player();	
						ByteBuffer buffer = new ByteBuffer(data);
						player.loadFrom(buffer);
						player.initial(0);
					}
					catch(Exception e)
					{
						System.out.println("error player data..... accountName : "+accountName);
						continue;
					}

					list.add(player);
				}
				statement.close();
			}
			cf.recycleConnection(conn);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		} 
		return list;
	}
	
	/**
	 * 保存玩家
	 * @param list
	 */
	public void savePlayers(List list)
	{
		int size = list.size();
		for (int i = 0; i < size; i++)
		{
			Player player = (Player)list.get(i);
			
			savePlayer(player);
		}
	}
	
	/** 删除角色 ***/
	public boolean delPlayer(Player player)
	{
 		CallableStatement callableStatement = null;
		Connection conn = null;
		
		boolean result = false;
		try 
		{
			conn = cf.getConnection();
			synchronized(conn)
			{
				callableStatement = conn.prepareCall("{?=call PlayerRemove(?,?)}");
				callableStatement.setInt(2, player.id);
				callableStatement.setString(3, player.accountName);
				callableStatement.registerOutParameter(1, Types.INTEGER);
				callableStatement.execute();

				result = callableStatement.getInt(1)==1;
				callableStatement.close();
			}
			cf.recycleConnection(conn);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return result;
	}
	
	
	/**
	 * 取得一个新的角色id
	 * @return
	 */
	public int getNewPlayerId() 
	{
		if(DatabaseAccessor.roleObjectIndex == 0)
		{
			int roleIndex = (int) indexProcess("RoleIndex", 1);
			
			if(GameServer.isAutoStart)
			{ //如果读取角色发生错误，则角色流水号像右移动500个
				DatabaseAccessor.roleObjectIndex = roleIndex+500;
				roleIndex = DatabaseAccessor.roleObjectIndex;
			}
			
			return roleIndex;
		}
		return ++DatabaseAccessor.roleObjectIndex;
	}
	
	/**
	 * 取得物品流水号
	 * @return
	 */
	public long getGoodsObjIndex()
	{
		if(DatabaseAccessor.goodsObjectIndex == 0)
		{
			long goodsIndex = indexProcess("GoodsIndex", 1);
			
			if(GameServer.isAutoStart)
			{ //如果读取角色发生错误，则物品流水号像右移动50000个
				DatabaseAccessor.goodsObjectIndex = goodsIndex +50000;
				goodsIndex = DatabaseAccessor.goodsObjectIndex;
			}
			
			return goodsIndex;
		}
		return ++DatabaseAccessor.goodsObjectIndex;
	}
	
	/**
	 * 取得家族流水号
	 * @return
	 */
	public int getFamilyObjIndex()
	{
		if(DatabaseAccessor.familyObjectIndex == 0)
		{
			int familyIndex = (int) indexProcess("FamilyIndex", 1);
			
			if(GameServer.isAutoStart)
			{//如果读取家族发生错误，则家族流水号像右移动50个
				DatabaseAccessor.familyObjectIndex = familyIndex+50;
				familyIndex = DatabaseAccessor.familyObjectIndex;
			}
			return familyIndex;
		}
		return ++DatabaseAccessor.familyObjectIndex;
	}
	
	/**
	 * 保存下标
	 */
	public void savedObjIndexs()
	{
		
		if(roleObjectIndex != 0)
		{
			indexProcess("RoleIndex", 2);
		}
		
		if(goodsObjectIndex != 0)
		{
			indexProcess("GoodsIndex", 2);
		}
		
		if(familyObjectIndex != 0)
		{
			indexProcess("FamilyIndex", 2);
		}
	}
	
	
	/**
	 * 处理
	 * @param index RoleIndex	GoodsIndex	FamilyIndex
	 * @param type 1 读取 ， 2保存
	 */
	private long indexProcess(String index,int type)
	{
		long t1 = System.currentTimeMillis();
		PreparedStatement statement = null;

		Connection conn = null;
		try 
		{
			conn = cf.getConnection();
			synchronized(conn)
			{	
				if(type == 1)
				{
					statement = conn.prepareStatement("select "+index+" from GameIndex");
					ResultSet rs = statement.executeQuery();
					while(rs.next())
					{
						if(index.equals("RoleIndex"))
						{
							return DatabaseAccessor.roleObjectIndex = rs.getInt(1);
						}
						else if(index.equals("GoodsIndex"))
						{
							long t2 = System.currentTimeMillis();
							if(t2 - t1 > 3000)
								MainFrame.println("DatabaseAccessor getGoodsIndex time out:"+(t2-t1));
							return  (DatabaseAccessor.goodsObjectIndex = rs.getLong(1));
						}
						else if(index.equals("FamilyIndex"))
						{
							return DatabaseAccessor.familyObjectIndex = rs.getInt(1);
						}
					}
				}
				else if(type == 2)
				{
					if(index.equals("RoleIndex"))
					{
						statement = conn.prepareStatement("update GameIndex set "+index+"="+(DatabaseAccessor.roleObjectIndex+1));
					}
					else if(index.equals("GoodsIndex"))
					{
						statement = conn.prepareStatement("update GameIndex set "+index+"="+(DatabaseAccessor.goodsObjectIndex+1));
					}
					else if(index.equals("FamilyIndex"))
					{
						statement = conn.prepareStatement("update GameIndex set "+index+"="+(DatabaseAccessor.familyObjectIndex+1));
					}
					statement.execute();
				}
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
		return 0 ;

	}
	
	

	
	/**
	 * 检查有无相同的名字
	 * @param playerName
	 * @return
	 */
	public boolean checkPlayerName(String playerName)
	{
		CallableStatement callableStatement = null;
		Connection conn = null;
		int result = 0;
		try 
		{
			conn = cf.getConnection();
			synchronized(conn)
			{
				
				if(heart.equals("sqlserver"))
				{
					callableStatement = conn.prepareCall("{?=call GameRoleName(?)}");
					callableStatement.registerOutParameter(1, Types.INTEGER);
					callableStatement.setString(2, playerName);
					callableStatement.execute();
					result = callableStatement.getInt(1);
					callableStatement.close();
				}
				else if(heart.equals("mysql"))
				{
					callableStatement = conn.prepareCall("{call GameRoleName(?,?)}");
					callableStatement.registerOutParameter(1, Types.INTEGER);
					callableStatement.setString(2, playerName);
					callableStatement.execute();
					result = callableStatement.getInt(1);
					callableStatement.close();
				}
			}	
			cf.recycleConnection(conn);
		} 
		catch(Exception e) 
		{
			System.out.println(e.getMessage());
			return true;
		}
		return result == 1;
	}
	
	

	
	
	public boolean createOrUpdateWorldInfo(WorldManager world)
	{
		CallableStatement callableStatement = null;
		Connection conn = null;
		
		
		ByteBuffer buffer = new ByteBuffer(4096 * 10);
		world.saveTo(buffer);
		byte[] data = buffer.getBytes();
		
		try 
		{
			conn = cf.getConnection();
			synchronized(conn)
			{
				callableStatement = conn.prepareCall("{call createOrUpdateServerInfo(?,?,?,?,?)}");	
				callableStatement.setInt(1, world.getGameServer().id);
				callableStatement.setString(2, world.getGameServer().name);
				callableStatement.setString(3, dbhost);
				callableStatement.setInt(4,0);
				callableStatement.setBytes(5, data);
				callableStatement.execute();
				callableStatement.close();
			}
			cf.recycleConnection(conn);
		}
		catch(Exception e) 
		{
			e.printStackTrace();
		}
		return true;
	}
	

	public void loadWorldInfo(WorldManager world)
	{
 		CallableStatement callableStatement = null;
		Connection conn = null;
		
		byte[] data = null;
		try 
		{
			conn = cf.getConnection();
			synchronized(conn)
			{
				callableStatement = conn.prepareCall("{call LoadServerInfo(?)}");
				callableStatement.setInt(1, world.getGameServer().id);
				ResultSet result = callableStatement.executeQuery();
				while(result.next())
				{
					data = result.getBytes("S_DATA");
				}
				callableStatement.close();
			}
			cf.recycleConnection(conn);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		} 
		
		if(data == null)
		{
			System.out.println("load server data is null");
			data = new byte[4];
		}
		world.loadFrom(new ByteBuffer(data));
	}
	
	
	/**
	 * 添加更新家族列表，删除家族改变家族状态state为-1
	 * @param family
	 * @param isCreate 
	 * @return
	 */
	public boolean createOrUpdateFamilyInfo(FamilyController family,boolean isCreate)
	{
		CallableStatement callableStatement = null;
		Connection conn = null;
		
		try 
		{
			conn = cf.getConnection();
			synchronized(conn)
			{
				if(isCreate)
					callableStatement = conn.prepareCall("{call createFamily(?,?,?,?,?,?,?,?,?,?,?,?,?)}");	
				else
					callableStatement = conn.prepareCall("{call updateFamily(?,?,?,?,?,?,?,?,?,?,?,?,?)}");
				
				callableStatement.setInt(1,family.id);
				callableStatement.setString(2,family.name);
				callableStatement.setInt(3,family.count);
				callableStatement.setInt(4,family.maxCount);
				callableStatement.setInt(5,family.leaderId);
				callableStatement.setString(6, family.leaderName);
				callableStatement.setString(7, family.title);
				callableStatement.setInt(8,family.state);
				callableStatement.setInt(9,family.contribution);
				callableStatement.setInt(10,family.changeLeaderTime);
				callableStatement.setInt(11,family.camp);
				
				ByteBuffer buffer = new ByteBuffer();
				buffer.writeInt(family.areaId.length);
				for (int i = 0; i < family.areaId.length; i++)
				{
					buffer.writeInt(family.areaId[i]);
				}
				callableStatement.setBytes(12,buffer.getBytes());
				
				buffer = new ByteBuffer();
				buffer.writeInt(family.familyNameList.size());
				for (int i = 0; i < family.familyNameList.size(); i++)
				{
					buffer.writeUTF((String) family.familyNameList.get(i));
				}
				callableStatement.setBytes(13,buffer.getBytes());
				callableStatement.execute();
				callableStatement.close();

			}
			cf.recycleConnection(conn);
		}
		catch(Exception e) 
		{
			e.printStackTrace();
		}
		return true;
	}
	

	public List getFamilys()
	{
		List list = new ArrayList();
		
		PreparedStatement statement = null;
		Connection conn = null;
		
		try 
		{
			conn = cf.getConnection();
			synchronized(conn)
			{	
				/**
				 *  @F_ID int,
					@F_NAME varchar(50),
					@F_COUNT int,
					@F_MAX_COUNT int,
					@F_LEADER_ID int,
					@F_LEADER_NAME varchar(50),
					@F_TITLE varchar(50),
					@F_STATE int,
					@F_CONTRIBUTION int,
					@F_PRESTIGE int,
					@F_CAMP int,
					@F_AREAS image,
					@F_MEMBER_NAMES image
				 */
				statement = conn.prepareStatement("select F_ID,F_NAME,F_COUNT,F_MAX_COUNT,F_LEADER_ID,F_LEADER_NAME,F_TITLE,F_STATE,F_CONTRIBUTION,F_PRESTIGE,F_CAMP,F_AREAS,F_MEMBER_NAMES from familyInfo where F_STATE=1");
				ResultSet rs = statement.executeQuery();
				while(rs.next())
				{
					FamilyController family = new FamilyController();
					family.id = rs.getInt(1);
					family.name = rs.getString(2);
					family.count = rs.getInt(3);
					family.maxCount = rs.getInt(4);
					family.leaderId = rs.getInt(5);
					family.leaderName = rs.getString(6);
					family.title = rs.getString(7);
					family.state = rs.getInt(8);
					family.contribution = rs.getInt(9);
					family.changeLeaderTime = rs.getInt(10);
					family.camp = rs.getInt(11);
					
					ByteBuffer buffer = new ByteBuffer(rs.getBytes(12));
					int count = buffer.readInt();
					if(count != 0)
					{
						family.areaId = new int[count];
						for (int i = 0; i < count; i++)
						{
							family.areaId[i] = buffer.readInt();
						}
					}
					
					buffer = new ByteBuffer(rs.getBytes(13));
					count = buffer.readInt();
					
					for (int i = 0; i < count; i++)
					{
						family.familyNameList.add(buffer.readUTF());
					}
					family.setWorldManager(GameServer.getInstance().getWorldManager());
					list.add(family);
				}
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
		return list;
	}

	
	
	public ConnectionFactory getConnectionFactory()
	{
		return cf;
	}
	

	public String getDbhost()
	{
		return dbhost;
	}

	public String getDbport()
	{
		return dbport;
	}

	public String getDbname()
	{
		return dbname;
	}

	public String getUser()
	{
		return user;
	}

	public String getPwd()
	{
		return pwd;
	}

//------------------------------------------------------------------------------------------------------------------------------------------

	
	/**
	 * 临时，检查是否有相同名字
	 * @param name
	 */
	public int checkUserName(String name,String pwd)
	{
		PreparedStatement statement = null;
		Connection conn = null;
		try 
		{
			conn = cf.getConnection();
			synchronized(conn)
			{	
				statement = conn.prepareStatement("select password,state,email from UserInfo where name=?");
				statement.setString(1, name);
				ResultSet rs = statement.executeQuery();
				while(rs.next())
				{
					int state = rs.getInt(2);
					
					if(state == 1 && pwd == null) 
						return 1;
					
					if(rs.getString(1).equals(pwd))
					{
						if(rs.getString(3) == null) //email为空的状态
							return -3;
						return 1;
					}
				}
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
		return -1;
	}
	
	/**
	 * 临时，注册名字
	 * @param name
	 * @param pwd
	 * @return
	 */
	public boolean regUser(String name,String pwd)
	{
		PreparedStatement statement = null;
		Connection conn = null;
	
		try 
		{
			conn = cf.getConnection();
			synchronized(conn)
			{	
				statement = conn.prepareStatement("insert into UserInfo(name,password,state,lasttime) values(?,?,?,?)");

				statement.setString(1, name);
				statement.setString(2, pwd);
				statement.setInt(3, 1);
				statement.setDate(4, new Date(System.currentTimeMillis()));
				statement.execute();
				return true;
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
				statement.close();
				cf.recycleConnection(conn);
			} 
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return false;
	}
	
	/**
	 * 修改密码
	 * @param name
	 * @param pwd
	 * @return
	 */
	public boolean updatePwd(String accountName,String oldPwd,String newPwd)
	{
		PreparedStatement statement = null;
		Connection conn = null;
	
		boolean isCanChange = false;
		
		try 
		{
			conn = cf.getConnection();
			synchronized(conn)
			{	
				
				statement = conn.prepareStatement("select password from UserInfo where name=?");
				statement.setString(1, accountName);
				ResultSet rs = statement.executeQuery();
				while(rs.next())
				{
					if(rs.getString(1).equals(oldPwd))
					{
						isCanChange = true;
					}

				}
				
				if(isCanChange)
				{
					statement = conn.prepareStatement("update UserInfo set password=? where name=?");
					statement.setString(1, newPwd);
					statement.setString(2, accountName);
					statement.execute();
				}
				
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
				statement.close();
				cf.recycleConnection(conn);
			} 
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return isCanChange;
	}
	
	
	/**
	 * 修改邮件
	 * @param name
	 * @param pwd
	 * @return
	 */
	public boolean updateEmail(String accountName,String oldPwd,String email)
	{
		PreparedStatement statement = null;
		Connection conn = null;
	
		boolean isCanChange = false;
		
		try 
		{
			conn = cf.getConnection();
			synchronized(conn)
			{	
				
				statement = conn.prepareStatement("select password from UserInfo where name=?");
				statement.setString(1, accountName);
				ResultSet rs = statement.executeQuery();
				while(rs.next())
				{
					if(rs.getString(1).equals(oldPwd))
					{
						isCanChange = true;
					}
				}
				
				if(isCanChange)
				{
					statement = conn.prepareStatement("update UserInfo set email=? where name=?");
					statement.setString(1, email);
					statement.setString(2, accountName);
					statement.execute();
				}
				
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
				statement.close();
				cf.recycleConnection(conn);
			} 
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return isCanChange;
	}
	
	
	/**
	 * 保存服务器平均和最大15分钟一次
	 */
	public void avgAndMaxOnServer(int accounts)
	{
		CallableStatement callableStatement = null;
		Connection conn = null;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		int loginTime = Integer.parseInt(sdf.format(new java.util.Date()));

		try 
		{
			conn = cf.getConnection();
			synchronized(conn)
			{
				
				callableStatement = conn.prepareCall("{call avgAndMaxOnServer(?,?,?)}");
				callableStatement.setInt(1, loginTime);
				callableStatement.setInt(2, GameServer.getInstance().id);//
				callableStatement.setInt(3, accounts);
				callableStatement.execute();
				callableStatement.close();
			}
			cf.recycleConnection(conn);
		}  
		catch(Exception e) 
		{
			e.printStackTrace();
		}
	}

	/**
	 * 查询 当天/当月 注册量  格式2009-07-09
	 * @param currentDay
	 * boolean isDay    true为天   false为月
	 */
	public int queryRegCount(String beginDay,String endDay)
	{
		PreparedStatement statement = null;
		Connection conn = null;
		
		int count = 0;
		
		try 
		{
			conn = cf.getConnection();
			synchronized(conn)
			{	
				statement = conn.prepareStatement("select count(*) from PlayerInfo where createTime between '"+beginDay+"' and '"+endDay+"'");
				ResultSet rs = statement.executeQuery();

				while(rs.next())
				{
					count = rs.getInt(1);
				}
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
		return count;
	}
	
/*	*//**
	 * 查询 当天/当月 注册量  格式2009-07-09 list
	 * @param currentDay
	 * boolean isDay    true为天   false为月
	 *//*
	public ByteBuffer queryRegList(String currentDay,boolean isDay)
	{
		PreparedStatement statement = null;
		Connection conn = null;
		
		ByteBuffer buffer = new ByteBuffer(64);
		try 
		{
			conn = cf.getConnection();
			synchronized(conn)
			{	
				statement = conn.prepareStatement("select lastTime,name,state from UserInfo");
				ResultSet rs = statement.executeQuery();
				
				if(isDay)
				{ 
					String name = null;
					Date da = null;
					int state = 0;
					
					while(rs.next())
					{
						 da = rs.getDate(1);
						 name= rs.getString(2);
						 state = rs.getInt(3);
						
						if(da.toString().equals(currentDay))
						{
							buffer.writeUTF(name);
							buffer.writeInt(state);
							buffer.writeUTF(da.toString());
						}
					}
				}
				else
				{
					currentDay = currentDay.substring(0,7);
					
					String name = null;
					Date da = null;
					int state = 0;
					
					while(rs.next())
					{
						da = rs.getDate(1);
						
						if(da.toString().substring(0,7).equals(currentDay))
						{
							buffer.writeUTF(name);
							buffer.writeInt(state);
							buffer.writeUTF(da.toString());
						}
					}
				}
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
		return buffer;
	}*/
	
	
	/**
	 * 查询当天玩家的平均数
	 * @param currentDay 格式2009-07-09
	 * @param isDay true为天   false为月
	 * @return
	 */
	public int queryAvgCount(String beginDay,String endDay)
	{
		PreparedStatement statement = null;
		Connection conn = null;
		
		int count = 0;
		
		try 
		{
			conn = cf.getConnection();
			synchronized(conn)
			{	
				statement = conn.prepareStatement("select sum(avgAccount),sum(avgCount) from T_Server_AVG where serverId=? and avgTimeId between '"+beginDay+"' and '"+endDay+"'");
				statement.setInt(1, GameServer.getInstance().id);
				ResultSet rs = statement.executeQuery();

				while(rs.next())
				{
					int avgAccount = rs.getInt(1);
					int avgCount = rs.getInt(2);
					count = avgAccount/avgCount;
				}
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
		return count;
	}
	
	
	/**
	 * 查询区间玩家的平均数
	 * @param beginDay
	 * @param endDay
	 * @return
	 */
	public ByteBuffer queryAvgList(String beginDay,String endDay)
	{
		PreparedStatement statement = null;
		Connection conn = null;
		
		ByteBuffer buffer = new ByteBuffer(64);
		
		try 
		{
			conn = cf.getConnection();
			synchronized(conn)
			{	
				statement = conn.prepareStatement("select avgTimeId,avgAccount,avgCount from T_Server_AVG where serverId=? and avgTimeId between '"+beginDay+"' and '"+endDay+"'");
				statement.setInt(1, GameServer.getInstance().id);
				ResultSet rs = statement.executeQuery();

				while(rs.next())
				{
					int avgTimeId = rs.getInt(1);
					int avgAccount = rs.getInt(2);
					int avgCount = rs.getInt(3);
					
					buffer.writeUTF(avgTimeId+"");
					buffer.writeInt(avgAccount/avgCount);
				}
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
		return buffer;
	}
	
	/**
	 * 查询区间内玩家的总数  最大在线  count 
	 * @param beginDay
	 * @param endDay
	 * @return
	 */
	public int queryMaxCount(String beginDay,String endDay)
	{
		PreparedStatement statement = null;
		Connection conn = null;
		
		int count = 0;
		
		try 
		{
			conn = cf.getConnection();
			synchronized(conn)
			{	
				statement = conn.prepareStatement("select sum(maxAccount) from T_Server_MAX where serverId=? and maxTimeId between '"+beginDay+"' and '"+endDay+"'");
				statement.setInt(1, GameServer.getInstance().id);
				ResultSet rs = statement.executeQuery();
				
				while(rs.next())
				{
					count = rs.getInt(1);
				}
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
		return count;
	}
	
	
	/**
	 * 查询当天玩家的最大数
	 * @param beginDay
	 * @param endDay
	 * @return
	 */
	public ByteBuffer queryMaxList(String beginDay,String endDay)
	{
		PreparedStatement statement = null;
		Connection conn = null;
		
		ByteBuffer buffer = new ByteBuffer(1024);
		
		try 
		{
			conn = cf.getConnection();
			synchronized(conn)
			{	
				statement = conn.prepareStatement("select maxTimeId,maxAccount from T_Server_MAX where serverId=? and maxTimeId between '"+beginDay+"' and '"+endDay+"'");
				statement.setInt(1, GameServer.getInstance().id);
				ResultSet rs = statement.executeQuery();

				while(rs.next())
				{
					buffer.writeUTF(rs.getInt(1)+"");
					buffer.writeInt(rs.getInt(2));
				}
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
		return buffer;
	}
	
	/**
	 *	回被玩家删除的档
	 */
	public int returnDeleteData(String account)
	{
		PreparedStatement statement = null;
		Connection conn = null;
		
		try 
		{
			conn = cf.getConnection();
			synchronized(conn)
			{	
				statement = conn.prepareStatement("select playerData from bak_playerinfo where accountName=?");
				statement.setString(1, account);
				
				ResultSet rs = statement.executeQuery();

				while(rs.next())
				{
					byte []data = rs.getBytes("playerData");
					
					
					Player player = getPlayer(account);
					
					if(player == null) //正式的没有档不能回
						return -2;
					
					if(player.familyId != 0) //正式的有家族的不能回档
						return -3;
					
					Player oldPlayer = new Player();	
					ByteBuffer buffer = new ByteBuffer(data);
					oldPlayer.loadFrom(buffer);
					oldPlayer.initial(0);
					
					if(oldPlayer.familyId != 0)
						return -3;
					
					int id = player.id;
					
					player = oldPlayer;
					player.id = id;
					
					savePlayer(player);
					
					return 1;
				}
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
		return -1;
	}
	
	
	/**
	 *	回被玩家备份档
	 *  0----7 bakIndex
	 */
	public int returnBakData(int bakIndex,String accountName)
	{

		Player player = getPlayer(accountName);
		
		if(player == null) //正式的没有档不能回
			return -2;
		
		if(player.familyId != 0|| 
			!player.familyName.equals("")) //正式的有家族的不能回档
			return -3;
		
		OtherExtInfo oei = (OtherExtInfo)player.getExtPlayerInfo("otherExtInfo");
		
		if(oei.loverId != 0 ||
				!oei.loverName.equals(""))
			return -4;

		
		
		DatabaseAccessor backDA = new DatabaseAccessor(getDbhost(),getDbport(),"qmb"+bakIndex,getUser(),getPwd(),"sqlserver");
		
		Player bakPlayer = backDA.getPlayer(accountName);

		if(bakPlayer == null) //此档案没有备份用户
			return -1;
	

		int id = player.id;
		
		player = bakPlayer;
		
		System.out.println(player.id);
		
		MergeStart.getInstance().updatePlayer(player);
		System.out.println(player.id);
		player.id = id;
		System.out.println(player.id);
		savePlayer(player);

		return 1;
	}
	
	
	/**
	 * 封号改变UserInfo状态
	 * @param accountName
	 * @param state 0正常 1 封号
	 * @return
	 */
	public int changeAccountState(String accountName,int state)
	{
		PreparedStatement statement = null;
		Connection conn = null;
		
		int count = 0;

		try 
		{
			conn = cf.getConnection();
			synchronized(conn)
			{	
				statement = conn.prepareStatement("update PlayerInfo set playerType=? where accountName=?");
				statement.setInt(1, state);
				statement.setString(2, accountName);
				statement.execute();
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
		return count;
	}
	
	
	//payType 1充值 2购物
	public void addPaymentInfo(int roleId,String accountName,int payType,int payState,int payMoney,String payExp)
	{
		CallableStatement callableStatement = null;
		Connection conn = null;
		
		try
		{
			conn = cf.getConnection();
			synchronized(conn)
			{
				callableStatement = conn.prepareCall("{call addPay(?,?,?,?,?,?)}");
				callableStatement.setInt(1, roleId);
				callableStatement.setString(2, accountName);
				callableStatement.setByte(3, (byte)payType);//1充值 2购物
				callableStatement.setByte(4, (byte)payState);//0已充  1未充
				callableStatement.setInt(5, payMoney);
				callableStatement.setString(6, payExp);
				callableStatement.execute();
				callableStatement.close();
			}
			cf.recycleConnection(conn);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 查询时间段里充值的总数 
	 * @param beginDay
	 * @param endDay
	 * @return
	 */
	public int[] queryPayCount(String beginDay,String endDay)
	{
		PreparedStatement statement = null;
		Connection conn = null;
		
		int moneyTotal = 0;
		int count = 0;
		try 
		{
			conn = cf.getConnection();
			synchronized(conn)
			{	
				statement = conn.prepareStatement("select sum(payMoney),count(payMoney) from T_Server_PAY where payType=1 and payTime between '"+beginDay+"' and '"+endDay+"'");
				ResultSet rs = statement.executeQuery();
				while(rs.next())
				{
					moneyTotal = rs.getInt(1);
					count = rs.getInt(2);
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
		return new int[]{moneyTotal,count};
	}
	
	
	/**
	 * 查询时间段内充值的总数列表
	 * @param beginDay
	 * @param endDay
	 * @param isDay
	 * @return
	 */
	public ByteBuffer queryPayList(String beginDay,String endDay,int type)
	{
		PreparedStatement statement = null;
		Connection conn = null;
		
		ByteBuffer buffer = new ByteBuffer(102400);
		
		try 
		{
			conn = cf.getConnection();
			synchronized(conn)
			{	
				statement = conn.prepareStatement("select accountName,payMoney,payState,payExp,payTime from T_Server_PAY where payType="+type+" and payTime between '"+beginDay+"' and '"+endDay+"'");
				
				ResultSet rs = statement.executeQuery();
				
				String accountName;
				int payMoney;
				int payState;
				String payExp;

				while(rs.next())
				{
					accountName = rs.getString(1);
					payMoney = rs.getInt(2);
					payState = rs.getInt(3);
					payExp = rs.getString(4);
	
					buffer.writeUTF(accountName);
					buffer.writeInt(payMoney);
					buffer.writeByte(payState);
					buffer.writeUTF(payExp);
				}
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
		return buffer;
	}
	
	
/*	*//**
	 * 查询所有PAY相关
	 * @return
	 *//*
	public ByteBuffer queryPayList()
	{
		PreparedStatement statement = null;
		Connection conn = null;
		
		ByteBuffer buffer = new ByteBuffer(102400);
		
		try 
		{
			conn = cf.getConnection();
			synchronized(conn)
			{	
				statement = conn.prepareStatement("select roleId,accountName,payType,payState,payMoney,payExp,payTime from T_Server_PAY");
				
				ResultSet rs = statement.executeQuery();
				
				String accountName;
				int payMoney;
				int payState;
				int roleId;
				int payType;
				String payTime;
				String payExp;

				while(rs.next())
				{
					roleId = rs.getInt(1);
					accountName = rs.getString(2);
					payType = rs.getInt(3);
					payState = rs.getInt(4);
					payMoney = rs.getInt(5);
					payExp = rs.getString(6);
					payTime = rs.getString(7);
	
					
					
					buffer.writeInt(roleId);
					buffer.writeUTF(accountName);
					buffer.writeByte(payType);
					buffer.writeByte(payState);
					buffer.writeInt(payMoney);
					buffer.writeUTF(payExp);
					buffer.writeUTF(payTime);
				}
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
		return buffer;
	}*/
	
	/**
	 *  通过昵称查询用户
	 * @param accountName
	 * @return
	 */
	public Player getPlayerByPlayerName(String playerName)
	{
		PreparedStatement statement = null;
		Connection conn = null;
		
		byte[] data = null;
		try 
		{
			conn = cf.getConnection();
			synchronized(conn)
			{	
				statement = conn.prepareStatement("select playerData from playerInfo where playerName=?");
				statement.setString(1, playerName);
				ResultSet rs = statement.executeQuery();
				while(rs.next())
				{
					data = rs.getBytes(1);
				}
			}	
			cf.recycleConnection(conn);
		} 
		catch (Exception e) 
		{
			return null;
		} 
			
		if (data == null)
		{
			return null;
		}
		else
		{
			Player player = new Player();	
			ByteBuffer buffer = new ByteBuffer(data);
			player.loadFrom(buffer);
			player.initial(0);
			return player;
		}
	}
	
	
	/**
	 * 添加日志
	 * @param accountName
	 * @param type
	 * @param event
	 * @return
	 */
	public void addToLog(String accountName,int type,String event)
	{
		PreparedStatement statement = null;
		Connection conn = null;
		
		SimpleDateFormat fmt=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try 
		{
			conn = cf.getConnection();
			synchronized(conn)
			{	
				statement = conn.prepareStatement("insert into t_server_log(type,accountName,event,eventTime) values(?,?,?,?)");
				statement.setInt(1, type);
				statement.setString(2, accountName);
				statement.setString(3, event);
				statement.setString(4,fmt.format(new java.util.Date()));
				statement.execute();
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

	}
	
	
	/**
	 * 
	 * @param type type  1登陆 2T下线 3禁言 4发公告 5发奖励 6封ip 7封号 8回档
	 * @param beginDay
	 * @param endDay
	 * @return
	 */
	public ByteBuffer queryServerLogList(String targetAccountName,int type,String beginDay,String endDay)
	{
		PreparedStatement statement = null;
		Connection conn = null;
		
		ByteBuffer buffer = new ByteBuffer(102400);
		buffer.writeByte(type);
		try 
		{
			conn = cf.getConnection();
			synchronized(conn)
			{	
				statement = conn.prepareStatement("select accountName,event,eventTime from T_Server_Log where type="+type+" and eventTime between '"+beginDay+"' and '"+endDay+"'");
				
				ResultSet rs = statement.executeQuery();
				
				String accountName,event,eventTime;
				

				
				while(rs.next())
				{
					accountName = rs.getString(1);
					event = rs.getString(2);
					eventTime = rs.getString(3);
	

					if(!targetAccountName.equals(accountName))
						continue;
					
					if(type == 1)
					{
						String str[]= Utils.split(event, "/");
						event = DC.getString(DC.BASE_9)+"IP:"+str[1];
					}
					else if(type == 2)
					{
						String str[]= Utils.split(event, " ");
						event = DC.getString(DC.BASE_10)+":"+str[str.length -1];
					}
					else if(type == 3 )
					{
						String str[]= Utils.split(event, " ");
						event = DC.getString(DC.BASE_10)+":"+str[str.length -3]+" "+DC.getString(DC.BASE_11)+":"+(str[str.length -1].equals("false")?DC.getString(DC.BASE_12):DC.getString(DC.BASE_13));
					}
					else if(type == 7 || type == 6)
					{
						String str[]= Utils.split(event, " ");
						event = DC.getString(DC.BASE_10)+":"+str[str.length -4]+" "+DC.getString(DC.BASE_11)+":"+(str[str.length -1].equals("1")?DC.getString(DC.BASE_14):DC.getString(DC.BASE_13));
					}
					else if(type == 4)
					{
						String str[]= Utils.split(event, " ");
						event = str[str.length -1];
					}
					else if(type == 5)
					{
						String str[]= Utils.split(event, "mail:");
						
						if(str.length == 2)
						{
							str = Utils.split(str[1], ":");
							if(str.length != 0)
							{
								StringBuffer sb = new StringBuffer();
								sb.append(DC.getString(DC.BASE_10));
								sb.append(":");
								sb.append(str[0]+"|");
								sb.append(DC.getString(DC.BASE_15));
								sb.append(str[1]+"|");
								sb.append(DC.getString(DC.BASE_16));
								sb.append(str[2]+"|");
								
								if(str.length > 3)
								{
									sb.append(((Goods)DataFactory.getInstance().getGameObject(Integer.parseInt(str[3]))).name+str[4]+"|");
									if(str.length > 5)
									{
										if(Integer.parseInt(str[5]) < 10)
										{
											if(str.length > 6)
												sb.append(((Goods)DataFactory.getInstance().getGameObject(Integer.parseInt(str[6]))).name+str[7]);
										}
										else
											sb.append(((Goods)DataFactory.getInstance().getGameObject(Integer.parseInt(str[5]))).name+str[6]);
									}
								}
								event = sb.toString();
							}

						}
						
					}
					
					
					buffer.writeUTF(accountName);
					buffer.writeUTF(event);
					buffer.writeUTF(eventTime);
				}
				
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
		return buffer;
	}
	


	public static void main(String[] args) throws ParseException
	{
		//DatabaseAccessor da= new DatabaseAccessor("localhost","3306","qimiao_db","root","root","mysql");
//		DatabaseAccessor da= new DatabaseAccessor("localhost","1433","rpg2008","sa","dengxianwen","sqlserver");
//
//		System.out.println(da.loadPlayers(1,5));
		
/*		long currTime = System.currentTimeMillis();
		currTime -= (1000l*60l*60l*24l*30l);
		String beforeOneMonthTime = WorldManager.getTypeTime("yyyy/MM/dd", currTime);*/
	}

	public String getHeart()
	{
		return heart;
	}


	

}
