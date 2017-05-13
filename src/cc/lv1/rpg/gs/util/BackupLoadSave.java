package cc.lv1.rpg.gs.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.dao.DatabaseAccessor;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.data.TaskManager;
import cc.lv1.rpg.gs.entity.CenterGoods;
import cc.lv1.rpg.gs.entity.NpcDialog;
import cc.lv1.rpg.gs.entity.RPGameObject;
import cc.lv1.rpg.gs.entity.ShopCenter;
import cc.lv1.rpg.gs.entity.controller.AreaController;
import cc.lv1.rpg.gs.entity.controller.FamilyPartyController;
import cc.lv1.rpg.gs.entity.controller.MonsterController;
import cc.lv1.rpg.gs.entity.controller.MonsterGroupController;
import cc.lv1.rpg.gs.entity.controller.NpcController;
import cc.lv1.rpg.gs.entity.controller.RoomController;
import cc.lv1.rpg.gs.entity.ext.PetTome;
import cc.lv1.rpg.gs.entity.impl.BossDropProp;
import cc.lv1.rpg.gs.entity.impl.BoxDropProp;
import cc.lv1.rpg.gs.entity.impl.Goods;
import cc.lv1.rpg.gs.entity.impl.Monster;
import cc.lv1.rpg.gs.entity.impl.MonsterAI;
import cc.lv1.rpg.gs.entity.impl.MonsterDropProp;
import cc.lv1.rpg.gs.entity.impl.PartyReward;
import cc.lv1.rpg.gs.entity.impl.Player;
import cc.lv1.rpg.gs.entity.impl.Reward;
import cc.lv1.rpg.gs.entity.impl.Task;
import cc.lv1.rpg.gs.entity.impl.battle.skill.Skill;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsEquip;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsProp;
import cc.lv1.rpg.gs.net.impl.SaveJob;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.i.Map;
import vin.rabbit.util.collection.impl.ArrayList;
import vin.rabbit.util.collection.impl.HashMap;
import vin.rabbit.util.xml.XMLNode;

public class BackupLoadSave implements Runnable
{

	private DatabaseAccessor sourceDA;
	
	private DatabaseAccessor backsDA;
	
	private Map accountMap = new HashMap(3000);
	
	private Map errorMap = new HashMap(500);
	

	public void run()
	{
		long time1 = System.currentTimeMillis();
		System.out.println("BackupLoadSave start with "+Utils.getCurrentTime());
//		List list ;
//		int sum = 0;
//		int i = 0;
//		Map map;
//		WorldManager world = GameServer.getInstance().getWorldManager();
		ByteBuffer buffer = new ByteBuffer(sourceDA.loadPlayerData("zwandzl"));
		Player player = new Player();
		player.loadFrom(buffer);
		PetTome pt = (PetTome) player.getExtPlayerInfo("petTome");
		for (int j = 0; j < pt.getPets().length; j++)
		{
			if(pt.getPets()[j].id == 1065000009)
			{
				pt.getPets()[j].growPoint = 280;
				break;
			}
		}
		sourceDA.savePlayer(player);
//		List answerList = world.getAnswerRewards();
//		Map topFamilys = FamilyPartyController.getInstance().getTopTeamPlayers();
//		List centerGoodss = ShopCenter.getInstance().getCenterGoods();
//		System.out.println("寄卖物品 --- " + centerGoodss.size());
//		do
//		{
//			list = sourceDA.loadPlayers(i, i+1000);
//
//			i += 1000;
//		
//			map = setAccountName(list,answerList,topFamilys,centerGoodss);
//			
//			sum += list.size();
//			
//			sourceDA.createOrUpdateWorldInfo(world);
//			
//			savePlayers(list,map); //8000条 o
//			savePlayerList(list, map); //n 
//			
//			System.out.println(list.size() +" saving .... total : "+sum);
//			System.gc();
//		}while(list.size() > 0);
		
		System.out.println("BackupLoadSave end with "+Utils.getCurrentTime());
		
		long time2 = System.currentTimeMillis();
		System.out.println("use time:::::::::::::::::"+(time2-time1)/1000+"秒");
	}
	
	public Map setAccountName(List list,List answerList,Map topFamilys,List centerGoodss)
	{
		Map map = new HashMap(list.size());
		for (int i = 0; i < list.size(); i++) 
		{
			Player player = (Player) list.get(i);
			String newName = getNewAccountName(player.accountName);
//			newName = newName.replace("*", "");
			String oldName = player.accountName;
			if(errorMap.get(oldName) != null)
			{
				sourceDA.delPlayer(player);
				continue;
			}
			if(player.accountName.equals(newName))
				continue;
			map.put(newName, oldName);
			player.accountName = newName;
System.out.println("修改账户名:"+oldName+"-----To-----:"+newName);
			for (int j = 0; j < answerList.size(); j++) 
			{
				Reward reward = (Reward) answerList.get(j);
				if(reward.id == player.id)
				{
					reward.accountName = player.accountName;
					break;
				}
			}
			
			if(!"".equals(player.familyName))
			{
				List fList = (List) topFamilys.get(player.familyId+":"+player.familyName);
				if(fList != null)
				{
					for (int k = 0; k < fList.size(); k++) 
					{
						PartyReward pr = (PartyReward) fList.get(k);
						if(pr.id == player.id)
						{
							pr.accountName = player.accountName;
							break;
						}
					}
				}
				
			}
			
			for (int j = 0; j < centerGoodss.size(); j++) 
			{
				CenterGoods cg = (CenterGoods) centerGoodss.get(j);
				if(cg.getId() == player.id)
				{
					cg.setAccountName(player.accountName);
				}
			}

		}
		
		return map;
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
		String accountname = null;
		try 
		{
			conn = sourceDA.getConnectionFactory().getConnection();
//			synchronized(conn)
//			{
				statement = conn.prepareStatement("SELECT TOP "+(end-begin)+" accountname,playerType,playerdata FROM Playerinfo WHERE (ID NOT IN (SELECT TOP "+begin+" id FROM Playerinfo ORDER BY id))ORDER BY ID ");
				ResultSet result = statement.executeQuery();
				while(result.next())
				{
					
					accountname = result.getString("accountname");
					type = result.getInt("playerType");
					data = result.getBytes("playerdata");
					
					Player player = null;
					try
					{
						player = new Player();	
						ByteBuffer buffer = new ByteBuffer(data);
						player.loadFrom(buffer);
//						player.accountName = accountname;
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
//			}
			sourceDA.getConnectionFactory().recycleConnection(conn);
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
			conn = sourceDA.getConnectionFactory().getConnection();
//			synchronized(conn)
//			{
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
						player.accountName = accountName;
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
//			}
			 sourceDA.getConnectionFactory().recycleConnection(conn);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		} 
		return list;
	}
	
	public void savePlayerList(List list,Map map)
	{
		long time1 = System.currentTimeMillis();
		
		Object obj = null;
		String newName="",oldName="";
		Player player = null;
		byte[] data = null;
		ByteBuffer buffer = null;
		Connection conn = null;
		PreparedStatement pstmt1 = null;
		PreparedStatement pstmt2 = null;
		
		try 
		{
//			conn = sourceDA.getConnectionFactory().getConnection();
			Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver"); 
			conn = DriverManager.getConnection(
			"jdbc:microsoft:sqlserver://"+sourceDA.getDbhost()+":"+sourceDA.getDbport()+";DatabaseName="+sourceDA.getDbname()+";SelectMethod=Cursor;User="+sourceDA.getUser()+";Password="+sourceDA.getPwd());

			pstmt1 = conn.prepareStatement("update Playerinfo set accountname=?,playerData=? where accountname=?");
			pstmt2 = conn.prepareStatement("update UserInfo set name=? where name=?");
			
			conn.setAutoCommit(false);
			
			int size = list.size();
			for (int i = 0; i < size; i++)
			{
				player = (Player)list.get(i);
				obj = map.get(player.accountName);
				if(obj == null)
					continue;
				if(player.accountName.equals("") || obj.toString().equals(""))
					continue;
				newName = player.accountName;
				oldName = obj.toString();
				buffer = new ByteBuffer(4096 * 10);
				player.saveTo(buffer);
				data = buffer.getBytes();

				pstmt1.setString(1, newName);
				pstmt1.setBytes(2, data);
				pstmt1.setString(3, oldName); 
	            pstmt1.addBatch();  
	            
	            pstmt2.setString(1, newName);
				pstmt2.setString(2, oldName);
				pstmt2.addBatch();
			}
	           
	//增加到批量工作任务中   
	        pstmt1.executeBatch();   
	        pstmt2.executeBatch();  
	//提交执行   
	        conn.commit();   
	        pstmt1.close();   
	        pstmt2.close();   
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		long time2 = System.currentTimeMillis();
		
		System.out.println("use time*********************"+(time2-time1)/1000+"秒");
	}
	
	public void savePlayers(List list,Map map)
	{
		Statement statement = null;
		PreparedStatement state = null;
		Connection conn = null;
		Object obj = null;
		String newName="",oldName="";
		Player player = null;
		byte[] data = null;
		ByteBuffer buffer = null;
		
		
		int size = list.size();
		for (int i = 0; i < size; i++)
		{
			player = (Player)list.get(i);
			obj = map.get(player.accountName);
			if(obj == null)
				continue;
			if(player.accountName.equals("") || obj.toString().equals(""))
				continue;
			newName = player.accountName;
			oldName = obj.toString();
			buffer = new ByteBuffer(4096 * 10);
			player.saveTo(buffer);
			data = buffer.getBytes();
			
			try 
			{
					conn = sourceDA.getConnectionFactory().getConnection();
//					synchronized(conn)
//					{
						state = conn.prepareStatement("update UserInfo set name=? where name=?");
						state.setString(1, newName);
						state.setString(2, oldName);
						state.execute();
						state = conn.prepareStatement("update Playerinfo set accountname=?,playerData=? where accountname=?");
						state.setString(1, newName);
						state.setBytes(2, data);
						state.setString(3, oldName);
						state.execute();
						
//					}
			} 
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				try
				{
					if(statement != null)
						statement.close();
					
					sourceDA.getConnectionFactory().recycleConnection(conn);
				} 
				catch (SQLException e)
				{
					e.printStackTrace();
				}
			}
//			updatePlayer(player,map.get(player.accountName).toString());
		}
	}
	
	private void updatePlayer(Player player,String oldName)
	{
		String newName = player.accountName;
		if(oldName.equals("") || newName.equals(""))
			return;
		
		ByteBuffer buffer = new ByteBuffer(4096 * 10);
		player.saveTo(buffer);
		byte[] data = buffer.getBytes();
		
		
		Statement statement = null;
		PreparedStatement state = null;
		Connection conn = null;
		try 
		{
				conn = sourceDA.getConnectionFactory().getConnection();
//				synchronized(conn)
//				{
					state = conn.prepareStatement("update UserInfo set name=? where name=?");
					state.setString(1, newName);
					state.setString(2, oldName);
					state.execute();
					state = conn.prepareStatement("update Playerinfo set accountname=?,playerData=? where accountname=?");
					state.setString(1, newName);
					state.setBytes(2, data);
					state.setString(3, oldName);
					state.execute();
					
//				}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(statement != null)
					statement.close();
				
				sourceDA.getConnectionFactory().recycleConnection(conn);
			} 
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private void queryPlayer()
	{
		List list = sourceDA.loadPlayers();
		for (int i = 0; i < list.size(); i++) 
		{
			Player player = (Player) list.get(i);
			System.out.println("玩家数据账号："+player.accountName);
		}
		PreparedStatement statement = null;
		Connection conn = null;
		try 
		{
				conn = sourceDA.getConnectionFactory().getConnection();
//				synchronized(conn)
//				{
					conn.setAutoCommit(false);
					statement = conn.prepareStatement("select accountname from Playerinfo");
					ResultSet rs = statement.executeQuery();
					while(rs.next())
					{
						String str = rs.getString(1);
						System.out.println("Playerinfo表里账号："+str);
					}
//				}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(statement != null)
					statement.close();
				
				sourceDA.getConnectionFactory().recycleConnection(conn);
			} 
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}

		try 
		{
				conn = sourceDA.getConnectionFactory().getConnection();
//				synchronized(conn)
//				{
					conn.setAutoCommit(false);
					statement = conn.prepareStatement("select name from UserInfo");
					ResultSet rs = statement.executeQuery();
					while(rs.next())
					{
						String str = rs.getString(1);
						System.out.println("UserInfo表里账号："+str);
					}
//				}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(statement != null)
					statement.close();
				
				sourceDA.getConnectionFactory().recycleConnection(conn);
			} 
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public String getNewAccountName(String accountName)
	{
		Object obj = accountMap.get(accountName);
		return obj!=null?obj.toString():accountName;
	}
	
	
	public void init()
	{
		GameServer.getInstance().initialWithoutNetServer();
//		loadAccountList();
		sourceDA = GameServer.getInstance().getDatabaseAccessor();
		sourceDA.loadWorldInfo(GameServer.getInstance().getWorldManager());
		backsDA = new DatabaseAccessor(sourceDA.getDbhost(),sourceDA.getDbport(),sourceDA.getDbname(),sourceDA.getUser(),sourceDA.getPwd(),sourceDA.getHeart());
	}
	
	private String accountStr = GameServer.getAbsolutePath()+"config/userIds.txt";
	
	private String errorsStr = GameServer.getAbsolutePath()+"config/errors.txt";
	
	private String infoStr = GameServer.getAbsolutePath()+"config/info.txt";
	
	private Map map = new HashMap();
	public void loadInfo()
	{
		try 
		{
			infoStr = Utils.readFile2(infoStr);
			String[] strs = Utils.split(infoStr, Utils.LINE_SEPARATOR);
			for (int i = 0; i < strs.length; i++) 
			{
				if(strs[i].equals(""))
					continue;
				String[] as = Utils.split(strs[i], "\t");
				if(map.get(as[1]) != null)
					System.out.println("chongfu:"+as[1]);
				else
					map.put(as[1], as[0]);
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void loadAccountList()
	{
		try 
		{
			accountStr = Utils.readFile2(accountStr);
			String[] strs = Utils.split(accountStr, Utils.LINE_SEPARATOR);
			for (int i = 0; i < strs.length; i++) 
			{
				String[] as = Utils.split(strs[i], ",");
				if(!"".equals(as[0]))
				{
					accountMap.put(as[0], as[1]);
				}
				else
				{
					System.out.println("loadAccountList accountList error:"+strs[i]+" i="+i+" lengh:"+strs.length);
				}
			}
			
			errorsStr = Utils.readFile2(errorsStr);
			strs = Utils.split(errorsStr, Utils.LINE_SEPARATOR);
			for (int i = 0; i < strs.length; i++) 
			{
				if(!"".equals(strs[i]))
				{
					errorMap.put(strs[i], strs[i]);
				}
				else
				{
					System.out.println("loadAccountList errorList error:"+strs[i]+" i="+i+" lengh:"+strs.length);
				}
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void checkMonster()
	{
		//怪物名称,生命,经险,技能,掉落
		HashMap map = DataFactory.getInstance().getDatas();
		Object[] objs = map.values().toArray();
		StringBuffer data = new StringBuffer();
		data.append("怪物名称\t怪物生命\t怪物经验\t怪物技能\t怪物掉落");
		data.append(Utils.LINE_SEPARATOR);
		for (int i = 0; i < objs.length; i++)
		{
			if(objs[i] instanceof Monster)
			{
				StringBuffer sb = new StringBuffer();
				Monster m = (Monster) objs[i];
				sb.append(m.name);
				sb.append("\t");
				sb.append(m.maxHitPoint);
				sb.append("\t");
				sb.append(m.experience);
				sb.append("\t");
				MonsterAI ma = m.getMonsterAI();
				if(ma != null)
				{
					Map mapp = new HashMap();
					String[] strs = ma.firstSkill.split(":");
					for (int j = 0; j < strs.length; j++)
					{
						Skill skill = (Skill) DataFactory.getInstance().getGameObject(Integer.parseInt(strs[j]));
						if(skill != null && mapp.get(skill.name) == null)
							mapp.put(skill.name, "");
					}
					strs = ma.secondSkill.split(":");
					for (int j = 0; j < strs.length; j++)
					{
						Skill skill = (Skill) DataFactory.getInstance().getGameObject(Integer.parseInt(strs[j]));
						if(skill != null && mapp.get(skill.name) == null)
							mapp.put(skill.name, "");
					}
					strs = ma.thirdSkill.split(":");
					for (int j = 0; j < strs.length; j++)
					{
						Skill skill = (Skill) DataFactory.getInstance().getGameObject(Integer.parseInt(strs[j]));
						if(skill != null && mapp.get(skill.name) == null)
							mapp.put(skill.name, "");
					}
					strs = ma.fourthSkill.split(":");
					for (int j = 0; j < strs.length; j++)
					{
						Skill skill = (Skill) DataFactory.getInstance().getGameObject(Integer.parseInt(strs[j]));
						if(skill != null && mapp.get(skill.name) == null)
							mapp.put(skill.name, "");
					}
					Object[] os = mapp.keySet().toArray();
					for (int j = 0; j < os.length; j++) 
					{
						sb.append(os[j].toString());
						if(j != os.length-1)
							sb.append(":");
					}
				}
				else
					sb.append("无");
				sb.append("\t");
				boolean isNull = false;
				MonsterDropProp mdp = (MonsterDropProp) DataFactory.getInstance().getGameObject(m.normalDropRate);
				if(mdp != null)
				{
					int[] ids = mdp.propId;
					for (int j = 0; j < ids.length; j++) 
					{
						Goods goods = (Goods) DataFactory.getInstance().getGameObject(ids[j]);
						if(goods != null)
						{
							sb.append(goods.name);
							if(j != ids.length-1)
								sb.append(":");
						}
					}
				}
				else
					isNull = true;
				Goods box = (Goods) DataFactory.getInstance().getGameObject(m.boxId);
				if(box != null)
				{
					if(isNull)
					{
						sb.append(box.name);
					}
					else
					{
						sb.append(":");
						sb.append(box.name);
					}
				}
				else
				{
					if(isNull)
						sb.append("无");
				}
				data.append(sb.toString());
				data.append(Utils.LINE_SEPARATOR);
			}
		}
		Utils.writeFile("C:\\Documents and Settings\\Administrator\\桌面\\1\\monster.txt", data.toString().getBytes());
	}
	
	public void checkMonsterGroup()
	{
		HashMap map = DataFactory.getInstance().getDatas();
		Object[] objs = map.values().toArray();
		Map mapp = new HashMap();
		Map names = new HashMap();
		for (int i = 0; i < objs.length; i++)
		{
			if(objs[i] instanceof RoomController)
			{
				RoomController room = (RoomController) objs[i];
				MonsterGroupController[] mgcs = room.getMonsterGroups();
				for (int j = 0; j < mgcs.length; j++) 
				{
					if(mapp.get(mgcs[j].id) == null)
					{
						mapp.put(mgcs[j].id, room.name);
						names.put(mgcs[j].id, mgcs[j].name);
					}
					else
					{
						String str = (String) mapp.get(mgcs[j].id);
						String[] strs = str.split(":");
						boolean flag = false;
						for (int k = 0; k < strs.length; k++) 
						{
							if(strs[k].equals(room.name))
							{
								flag = true;
								break;
							}
						}
						if(!flag)
							str += ":" + room.name;
						mapp.put(mgcs[j].id, str);
					}
				}
			}
		}
		Object[] keys = mapp.keySet().toArray();
		Object[] values = mapp.values().toArray();
		StringBuffer sb = new StringBuffer();
		sb.append("ID\t怪物组\t出现的房间");
		sb.append(Utils.LINE_SEPARATOR);
		for (int i = 0; i < values.length; i++) 
		{
			sb.append(keys[i].toString());
			sb.append("\t");
			sb.append(names.get(keys[i]).toString());
			sb.append("\t");
			sb.append(values[i].toString());
			sb.append(Utils.LINE_SEPARATOR);
		}
		Utils.writeFile("C:\\Documents and Settings\\Administrator\\桌面\\1\\monsterGroup.txt", sb.toString().getBytes());
	}
	
	public void checkBox()
	{
		HashMap map = DataFactory.getInstance().getDatas();
		Object[] objs = map.values().toArray();
		StringBuffer data = new StringBuffer();
		data.append("宝箱名字\t包含物品");
		data.append(Utils.LINE_SEPARATOR);
		for (int i = 0; i < objs.length; i++) 
		{
			if(objs[i] instanceof GoodsProp)
			{
				GoodsProp prop = (GoodsProp) objs[i];
				if(prop.type == 10)
				{
					RPGameObject rpgo = (RPGameObject) DataFactory.getInstance().getGameObject(prop.dropId);
					if(rpgo != null)
					{
						data.append(prop.name);
						data.append("\t");
						int[] ids = null;
						if(rpgo instanceof BoxDropProp)
						{
							BoxDropProp bdp = (BoxDropProp) rpgo;
							ids = bdp.propId;
						}
						else if(rpgo instanceof BossDropProp)
						{
							BossDropProp bdp = (BossDropProp) rpgo;
							ids = bdp.propId;
						}
						else
							continue;
						StringBuffer sb = new StringBuffer();
						for (int j = 0; j < ids.length; j++) 
						{
							Goods goods = (Goods) DataFactory.getInstance().getGameObject(ids[j]);
							if(goods != null)
							{
								sb.append(goods.name);
								sb.append(":");
							}
						}
						if(sb.toString().endsWith(":"))
						{
							sb = new StringBuffer(sb.toString().substring(0, sb.length()-1));
						}
						data.append(sb.toString());
						data.append(Utils.LINE_SEPARATOR);
					}
				}
			}
		}
		Utils.writeFile("C:\\Documents and Settings\\Administrator\\桌面\\1\\box.txt", data.toString().getBytes());
	}
	
	public void checkEquip()
	{
		String[] ens ={"phyAtt","sptAtt","phyDef","sptDef","power","agility","nimble","spirit","wisdom","physique","lifePoint","magicPoint","growPoint","noDefPhyHurt","noDefSptHurt","phyHurtAvoid","sptHurtAvoid","phySmiteRate","sptSmiteRate","phySmiteParm","sptSmiteParm","hit","avoidance","spiritStand","curePoint","killMonsterExp","killMonsterExpRate","skillCdTimeSub","suckBloob","suckMagic","hurtToMagic","toMortalHurt","toMortalHurtRate","toBeastHurt","toBeastHurtRate","toGoblinHurt","toGoblinHurtRate","toNymphHurt","toNymphHurtRate","toDeityHurt","toDeityHurtRate","toDemonHurt","toDemonHurtRate","toBigMonsterHurt","toBigMonsterHurtRate","toMidMonsterHurt","toMidMonsterHurtRate","toSmallMonsterHurt","toSmallMonsterHurtRate","allSkill", "toGhostHurt", "toGhostHurtRate", "clearPhySmite", "clearSptSmite","clearPhySmiteParm","clearSptSmiteParm", "clearNoDefHurt", "phySmiteHurtParm", "sptSmiteHurtParm"};
		String[] chs = {"物理攻击","精神攻击","物理防御","精神防御","力量","敏捷","敏捷","精神","智慧","体质","生命","精力","成长值%","忽视防御的物理伤害","忽视防御的精神伤害","物理免伤","精神免伤","物理爆击率%","精神爆击率%","物理爆击伤害%","精神爆击伤害%","命中","闪避","精神抵抗","辅助值%","杀怪所得经验值增加","杀怪所得经验值增加%","特定技能CD时间减少","吸血——伤害转化为自己的生命值","吸蓝——伤害转化为自己的精力值","所受伤害转化为精力消耗","对凡人的伤害增加","对凡人的伤害增加%","对野兽的伤害增加","对野兽的伤害增加%","对妖的伤害增加","对妖的伤害增加%","对仙的伤害增加","对仙的伤害增加%","对神的伤害增加","对神的伤害增加%","对魔的伤害增加","对魔的伤害增加%","对大体型怪物的伤害增加","对大体型怪物的伤害增加%","对中体型的怪物伤害增加","对中体型的怪物伤害增加%","对小体型的怪物伤害增加","对小体型的怪物伤害增加%","所有学会技能等级加1", "对鬼的伤害增加", "对鬼的伤害增加%", "抗物理暴击率%", "抗精神暴击率%","抗物理暴击伤害%","抗精神暴击伤害%","抗忽视伤害", "物理爆击伤害%","精神爆击伤害%"};
		HashMap atts = new HashMap();
		for (int i = 0; i < chs.length; i++) {
			atts.put(ens[i], chs[i]);
		}
		HashMap map = DataFactory.getInstance().getDatas();
		Object[] objs = map.values().toArray();
		StringBuffer data = new StringBuffer();
		data.append("装备ID\t装备名称\t需求等级\t是否VIP\t装备星级\t装备品质\t装备属性");
		data.append(Utils.LINE_SEPARATOR);
		for (int i = 0; i < objs.length; i++) 
		{
			if(objs[i] instanceof GoodsEquip)
			{
				GoodsEquip equip = (GoodsEquip) objs[i];
				data.append(equip.id);
				data.append("\t");
				data.append(equip.name);
				data.append("\t");
				data.append(equip.level);
				data.append("\t");
				data.append(equip.isVIP?"是":"否");
				data.append("\t");
				GoodsEquip goods = equip.makeNewBetterEquip(equip.taskColor);
				data.append(goods.startLevel);
				data.append("\t");
				if(goods.quality == 0)
					data.append("白色");
				else if(goods.quality == 1)
					data.append("绿色");
				else if(goods.quality == 2)
					data.append("蓝色");
				else if(goods.quality == 3)
					data.append("紫色");
				else if(goods.quality == 4)
					data.append("紫色2");
				else if(goods.quality == 5)
					data.append("紫色3");
				data.append("\t");
				StringBuffer sb = new StringBuffer();
				if(!goods.attStr.isEmpty())
				{
					String[] strs = Utils.split(goods.attStr, ":");
					for (int j = 0; j < strs.length; j++)
					{
						int value = Integer.parseInt(goods.getVariable(strs[j]));
						if(value <= 0)
							continue;
						if(atts.get(strs[j]) == null)
							continue;
						sb.append(atts.get(strs[j]));
						sb.append(":");
						sb.append(value);
						sb.append("|");
					}
				}
				if(goods.equipLocation == 9 || goods.equipLocation == 10 || goods.equipLocation == 11)
				{
					if(goods.phyAtt > 0)
					{
						sb.append("物理攻击:");
						sb.append(goods.phyAtt);
						sb.append("|");
					}
					if(goods.sptAtt > 0)
					{
						sb.append("精神攻击:");
						sb.append(goods.sptAtt);
						sb.append("|");
					}
					if(goods.phyDef > 0)
					{
						sb.append("物理防御:");
						sb.append(goods.phyDef);
						sb.append("|");
					}
					if(goods.sptDef > 0)
					{
						sb.append("精神防御:");
						sb.append(goods.sptDef);
						sb.append("|");
					}
				}
				if(sb.toString().endsWith("|"))
					sb = new StringBuffer(sb.toString().substring(0, sb.length()-1));
				if(sb.length() == 0)
					data.append("无");
				else
					data.append(sb.toString());
				data.append(Utils.LINE_SEPARATOR);
			}
		}
		data = new StringBuffer(data.toString().substring(0, data.length()-2));
		Utils.writeFile("C:\\Documents and Settings\\Administrator\\桌面\\1\\equip.txt", data.toString().getBytes());
	}
	
	public void checkTask()
	{
		HashMap map1 = new HashMap();
		HashMap map2 = new HashMap();
		
		AreaController[] acs = GameServer.getInstance().getWorldManager().getAreaControllers();
		for (int i = 0; i < acs.length; i++) 
		{
			RoomController[] rcs = acs[i].getRooms();
			for (int j = 0; j < rcs.length; j++)
			{
				NpcController[] ncs = rcs[j].getNpcList();
				
				for (int k = 0; k < ncs.length; k++) 
				{
					NpcDialog[] nds = ncs[k].getNpcDialogs();
					for (int l = 0; l < nds.length; l++)
					{
						String str = nds[l].getDialogIndex();
						if("default".equals(str))
							continue;
						str = str.replace("t", "");
						if(map1.get(ncs[k].getID()) == null)
							map1.put(ncs[k].getID(),str);
						else
						{
							String s = map1.get(ncs[k].getID()).toString();
							map1.put(ncs[k].getID(), str+":"+s);
						}
//						System.out.println(ncs[k].getID()+"  "+ncs[k].getName()+" dialog:"+str);
					}
				}
			}
		}
		for (int i = 0; i < acs.length; i++) 
		{
			RoomController[] rcs = acs[i].getRooms();
			for (int j = 0; j < rcs.length; j++)
			{
				NpcController[] ncs = rcs[j].getNpcList();
				
				for (int k = 0; k < ncs.length; k++) 
				{
					Task[] tasks = ncs[k].getTasks();
					for (int l = 0; l < tasks.length; l++) 
					{
						String str = tasks[l].id+"";
						if(map2.get(ncs[k].getID()) == null)
							map2.put(ncs[k].getID(),str);
						else
						{
							String s = map2.get(ncs[k].getID()).toString();
							map2.put(ncs[k].getID(), str+":"+s);
						}
					}
				}
			}
		}
		for (int i = 0; i < acs.length; i++) 
		{
			RoomController[] rcs = acs[i].getRooms();
			for (int j = 0; j < rcs.length; j++)
			{
				NpcController[] ncs = rcs[j].getNpcList();
				
				for (int k = 0; k < ncs.length; k++) 
				{
					if(map1.get(ncs[k].getID()) == null && map2.get(ncs[k].getID()) != null)
					{
						System.out.println(ncs[k].getID()+"  "+ncs[k].getName()+"在对话中没有任务,在任务中有配置");
					}
					if(map1.get(ncs[k].getID()) != null && map2.get(ncs[k].getID()) == null)
					{
						System.out.println(ncs[k].getID()+"  "+ncs[k].getName()+"在对话中有任务,在任务中没有配置");
					}
					if(map1.get(ncs[k].getID()) != null && map2.get(ncs[k].getID()) != null)
					{
						if(!map1.get(ncs[k].getID()).toString().equals(map2.get(ncs[k].getID()).toString()))
						{
							System.out.println("---------------------------------");
							System.out.println("id:"+ncs[k].getID()+"  name:"+ncs[k].getName());
							String str1 = map1.get(ncs[k].getID()).toString();
							String str2 = map2.get(ncs[k].getID()).toString();
							String[] strs1 = Utils.split(str1, ":");
							String[] strs2 = Utils.split(str2, ":");
							for (int l = 0; l < strs1.length; l++) 
							{
								if(str2.indexOf(strs1[l]) == -1)
								{
									System.out.println("对话里的任务ID:"+strs1[l]+"在任务配置里没有 ");
								}
							}
							for (int l = 0; l < strs2.length; l++) 
							{
								if(str1.indexOf(strs2[l]) == -1)
								{
									System.out.println("任务配置里的任务ID:"+strs2[l]+"在对话配置里没有 ");
								}
							}
//							System.out.println("对话里面的:"+map1.get(ncs[k].getID()));
//							System.out.println("任务里面的:"+map2.get(ncs[k].getID()));
							System.out.println("---------------------------------");
						}
					}
					
				}
			}
		}
		
	}


	public static void main(String[] args) 
	{
		BackupLoadSave backup = new BackupLoadSave();
		backup.init();
//		backup.checkTask();
		Thread thread = new Thread(backup);
		thread.start();
		
//		backup.updatePlayer("bean3", "*bean3",1);
		
//		backup.queryPlayer();
		
//		String a=\u0022a\u0022;
//		System.out.print(a);
		
//		backup.loadInfo();
	}

}
