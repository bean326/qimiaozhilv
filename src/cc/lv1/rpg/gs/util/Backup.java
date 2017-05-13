package cc.lv1.rpg.gs.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.impl.ArrayList;
import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.dao.DatabaseAccessor;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.ext.Bag;
import cc.lv1.rpg.gs.entity.ext.PetTome;
import cc.lv1.rpg.gs.entity.ext.Storage;
import cc.lv1.rpg.gs.entity.impl.Player;
import cc.lv1.rpg.gs.util.sort.Order;
import cc.lv1.rpg.gs.util.sort.PlayerComparator;

public class Backup implements Runnable
{

	private DatabaseAccessor sourceDA;
	
	//private DatabaseAccessor [] backsDA = new DatabaseAccessor[7];
	
	/** 到明天的时间 */
	public static long nextDayTime;
	
	public static int day;
	
	public static int[] lvCounts;
	
	public static final int TOPCOUNT = 200; 
	
	public void run()
	{

		while(true)
		{
			lvCounts = new int[10000/50+2];
			
			Calendar calendar = Calendar.getInstance();
			
			int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

			processBackup(dayOfWeek);
			
			initTime();
			Utils.sleep((int) nextDayTime);
			
			//nextDayTime = 1000 * 60 * 60 * 24;
		}
		 
	}
	
	private void processBackup(int dayOfWeek)
	{
		levelPlayer = new ArrayList(2000);
		pointPlayer = new ArrayList(2000);
		playerPetLevel = new ArrayList(2000);
		maxHitPoint = new ArrayList(2000);
		maxMagicPoint = new ArrayList(2000);
		phsAtt = new ArrayList(2000);
		sptAtt = new ArrayList(2000);
		phsSmiteRate = new ArrayList(2000);	
		sptSmiteRate = new ArrayList(2000);
		phsSmiteHurtParm = new ArrayList(2000);
		sptSmiteHurtParm = new ArrayList(2000);
		phsHurtAvoid = new ArrayList(2000);
		sptHurtAvoid = new ArrayList(2000);
		curePoint = new ArrayList(2000);
		
		System.out.println("Backup start with "+Utils.getCurrentTime());
		
		List list ;
		int sum = 0;
		int i = 0;
		do
		{
			list = sourceDA.loadPlayersWithBackup(i, i+5000);

			i += 5000;
			
			checkTopProcess(list);
			
			sum +=list.size();
			
			//backsDA[dayOfWeek-1].savePlayers(list);
			
			System.out.println(list.size() +" saving .... total : "+sum);
			System.gc();
		}while(list.size() > 0);
		
		
		saveTopProcess();
		
		int maxCount = 0;
		
		for (int k = 0; k < lvCounts.length; k++)
		{
			maxCount += lvCounts[k];
		}
		
		if(maxCount != 0)
		{
			for (int k = 0; k < lvCounts.length; k++)
			{
				System.out.println("level:"+(k*50)+"  count:"+lvCounts[k]+"   rate:"+lvCounts[k]*10000/maxCount);
			}
		}

		
		//System.out.println(sum+FontConver.simpleToCompl("\u4E2A\u6210\u529F\20\u661F\u671F")+(dayOfWeek-1)+FontConver.simpleToCompl("\u5907\u4EFD\u7ED3\u675F\20\20\u5DF2\u5B58\u5165\u6570\u636E\u5E93\20\3A")+backsDA[dayOfWeek-1].getDbname());
		System.out.println(" --- "+Utils.getCurrentTime()+" --- ");
	}


	/**
	 *  人物等级排行
	 */
	private List levelPlayer;
	
	/**
	 * 游戏币排行
	 */
	private List pointPlayer;
	
	/**
	 * 宠物等级排行
	 */
	private List playerPetLevel;
	
	/**
	 * 生命排行
	 */
	private List maxHitPoint;
	
	/**
	 * 精力排行
	 */
	private List maxMagicPoint;
	
	/**
	 * 物理攻击排行
	 */
	private static List phsAtt;
	
	/**
	 * 精神攻击排行
	 */
	private static List sptAtt;
	
	/**
	 * 物理暴击排行
	 */
	private static List phsSmiteRate;
	
	/**
	 * 精神暴击排行
	 */
	private static List sptSmiteRate;
	
	/**
	 * 物理暴击伤害排行
	 */
	private static List phsSmiteHurtParm;
	
	/**
	 * 精神暴击伤害排行
	 */
	private static List sptSmiteHurtParm;
	
	/**
	 * 物理免伤排行
	 */
	private static List phsHurtAvoid;
	
	/**
	 * 精神免伤排行
	 */
	private static List sptHurtAvoid;
	
	/**
	 * 辅助值排行
	 */
	private static List curePoint;
	
	/**
	 * 排行榜
		人物等级,游戏币,宠物等级,生命,精力,物理攻击,精神攻击,物理暴击,精神暴击,物理暴击伤害,精神暴击伤害,物理免伤,精神免伤,辅助值
	 */
	private void checkTopProcess(List list)
	{
		if(list.size() <= 0)
			return;
		
		int size = list.size();
		for (int i = 0; i < size; i++)
		{
			Player player = (Player)list.get(i);
			
			if(player.level == 1)
			{
				lvCounts[0]++; 
			}
			else
			{
				//System.out.println(player.level+" "+(player.level / 50+1));
				lvCounts[(player.level / 50+1)]++;
			}
			
			if(player.isClosed)
			{
				list.remove(i--);
				size--;
			}
		}
System.out.println("排序开始..."+list.size()+"  time:"+Utils.getCurrentTime());		
		sort(list,"level");
		sort(list,"point");
		sort(list,"playerPetLevel");
		sort(list,"maxHitPoint");
		sort(list,"maxMagicPoint");
		
		sort(list,"phsAtt");
		sort(list,"sptAtt");
		
		sort(list,"phsSmiteRate");
		sort(list,"sptSmiteRate");
		sort(list,"phsSmiteHurtParm");
		sort(list,"sptSmiteHurtParm");
		sort(list,"phsHurtAvoid");
		sort(list,"sptHurtAvoid");
		sort(list,"curePoint");
System.out.println("排序结束..."+list.size()+"  time:"+Utils.getCurrentTime());
	}
	
	private void sort(List list,String string)
	{
		java.util.List newList = new java.util.ArrayList();
		for (int i = 0; i < list.size(); i++)
		{
			Player player = (Player) list.get(i);
			newList.add(new Order(string,player));
		}
		PlayerComparator comp = new PlayerComparator();
		Collections.sort(newList,comp);
		int leng = newList.size()>TOPCOUNT?TOPCOUNT:newList.size();
		if("level".equals(string))
		{
			for (int i = 0; i < leng; i++) 
			{
				Order c = (Order) newList.get(i);
				levelPlayer.add(c.getPlayer());
			}
			if(levelPlayer.size() > TOPCOUNT)
			{
				confirmSort(levelPlayer,string);
			}
		}
		else if("point".equals(string))
		{
			for (int i = 0; i < leng; i++) 
			{
				Order c = (Order) newList.get(i);
				pointPlayer.add(c.getPlayer());
			}
			if(pointPlayer.size() > TOPCOUNT)
			{
				confirmSort(pointPlayer,string);
			}
		}
		else if("playerPetLevel".equals(string))
		{
			for (int i = 0; i < leng; i++) 
			{
				Order c = (Order) newList.get(i);
				playerPetLevel.add(c.getPlayer());
			}
			if(playerPetLevel.size() > TOPCOUNT)
			{
				confirmSort(playerPetLevel,string);
			}
		}
		else if("maxHitPoint".equals(string))
		{
			for (int i = 0; i < leng; i++) 
			{
				Order c = (Order) newList.get(i);
				maxHitPoint.add(c.getPlayer());
			}
			if(maxHitPoint.size() > TOPCOUNT)
			{
				confirmSort(maxHitPoint,string);
			}
		}
		else if("maxMagicPoint".equals(string))
		{
			for (int i = 0; i < leng; i++) 
			{
				Order c = (Order) newList.get(i);
				maxMagicPoint.add(c.getPlayer());
			}
			if(maxMagicPoint.size() > TOPCOUNT)
			{
				confirmSort(maxMagicPoint,string);
			}
		}
		else if("phsAtt".equals(string))
		{
			for (int i = 0; i < leng; i++) 
			{
				Order c = (Order) newList.get(i);
				phsAtt.add(c.getPlayer());
			}
			if(phsAtt.size() > TOPCOUNT)
			{
				confirmSort(phsAtt,string);
			}
		}
		else if("sptAtt".equals(string))
		{
			for (int i = 0; i < leng; i++) 
			{
				Order c = (Order) newList.get(i);
				sptAtt.add(c.getPlayer());
			}
			if(sptAtt.size() > TOPCOUNT)
			{
				confirmSort(sptAtt,string);
			}
		}
		else if("phsSmiteRate".equals(string))
		{
			for (int i = 0; i < leng; i++) 
			{
				Order c = (Order) newList.get(i);
				phsSmiteRate.add(c.getPlayer());
			}
			if(phsSmiteRate.size() > TOPCOUNT)
			{
				confirmSort(phsSmiteRate,string);
			}
		}
		else if("sptSmiteRate".equals(string))
		{
			for (int i = 0; i < leng; i++) 
			{
				Order c = (Order) newList.get(i);
				sptSmiteRate.add(c.getPlayer());
			}
			if(sptSmiteRate.size() > TOPCOUNT)
			{
				confirmSort(sptSmiteRate,string);
			}
		}
		else if("phsSmiteHurtParm".equals(string))
		{
			for (int i = 0; i < leng; i++) 
			{
				Order c = (Order) newList.get(i);
				phsSmiteHurtParm.add(c.getPlayer());
			}
			if(phsSmiteHurtParm.size() > TOPCOUNT)
			{
				confirmSort(phsSmiteHurtParm,string);
			}
		}
		else if("sptSmiteHurtParm".equals(string))
		{
			for (int i = 0; i < leng; i++) 
			{
				Order c = (Order) newList.get(i);
				sptSmiteHurtParm.add(c.getPlayer());
			}
			if(sptSmiteHurtParm.size() > TOPCOUNT)
			{
				confirmSort(sptSmiteHurtParm,string);
			}
		}
		else if("phsHurtAvoid".equals(string))
		{
			for (int i = 0; i < leng; i++) 
			{
				Order c = (Order) newList.get(i);
				phsHurtAvoid.add(c.getPlayer());
			}
			if(phsHurtAvoid.size() > TOPCOUNT)
			{
				confirmSort(phsHurtAvoid,string);
			}
		}
		else if("sptHurtAvoid".equals(string))
		{
			for (int i = 0; i < leng; i++) 
			{
				Order c = (Order) newList.get(i);
				sptHurtAvoid.add(c.getPlayer());
			}
			if(sptHurtAvoid.size() > TOPCOUNT)
			{
				confirmSort(sptHurtAvoid,string);
			}
		}
		else if("curePoint".equals(string))
		{
			for (int i = 0; i < leng; i++) 
			{
				Order c = (Order) newList.get(i);
				curePoint.add(c.getPlayer());
			}
			if(curePoint.size() > TOPCOUNT)
			{
				confirmSort(curePoint,string);
			}
		}
	}
	
	private void confirmSort(List list,String string)
	{
		java.util.List newList = new java.util.ArrayList();
		for (int i = 0; i < list.size(); i++)
		{
			Player player = (Player) list.get(i);
			newList.add(new Order(string,player));
		}
		PlayerComparator comp = new PlayerComparator();
		Collections.sort(newList,comp);
		List temp = new ArrayList(TOPCOUNT);
		for (int i = 0; i < TOPCOUNT; i++) 
		{
			Order c = (Order) newList.get(i);
			temp.add(c.getPlayer());
		}
		if("level".equals(string))
		{
			levelPlayer = temp;
		}
		else if("point".equals(string))
		{
			pointPlayer = temp;
		}
		else if("playerPetLevel".equals(string))
		{
			playerPetLevel = temp;
		}
		else if("maxHitPoint".equals(string))
		{
			maxHitPoint = temp;
		}
		else if("maxMagicPoint".equals(string))
		{
			maxMagicPoint = temp;
		}
		else if("phsAtt".equals(string))
		{
			phsAtt = temp;
		}
		else if("sptAtt".equals(string))
		{
			sptAtt = temp;
		}
		else if("phsSmiteRate".equals(string))
		{
			phsSmiteRate = temp;
		}
		else if("sptSmiteRate".equals(string))
		{
			sptSmiteRate = temp;
		}
		else if("phsSmiteHurtParm".equals(string))
		{
			phsSmiteHurtParm = temp;
		}
		else if("sptSmiteHurtParm".equals(string))
		{
			sptSmiteHurtParm = temp;
		}
		else if("phsHurtAvoid".equals(string))
		{
			phsHurtAvoid = temp;
		}
		else if("sptHurtAvoid".equals(string))
		{
			sptHurtAvoid = temp;
		}
		else if("curePoint".equals(string))
		{
			curePoint = temp;
		}
	}
	
//	private void sorts(List list, String string)
//	{
//
//		Object [] objs = list.toArray();
//		
//		for (int i = 0; i < objs.length; i++)
//		{
//			for(int j = objs.length-1 ; j > i ; j --)
//		    {
//			  if(string.equals("level"))
//			  {
//				  Player p1 = (Player)objs[j-1];
//				  Player p2 = (Player)objs[j];
//				  int type1 = p1.getZhuanshengState();
//				  int type2 = p2.getZhuanshengState();
//				  if(type1 == type2)
//				  {
//					  if(p1.level < p2.level)
//					  {
//					  	Object tmp = objs[j];
//					  	objs[j] = objs[j-1];
//					  	objs[j-1] = tmp;
//				      }
//				  }
//				  else
//				  {
//					 if(type1 < type2)
//					 {
//						 Object tmp = objs[j];
//					  	 objs[j] = objs[j-1];
//					  	 objs[j-1] = tmp;
//					 }
//				  }
//			  }
//			  else if(string.equals("point"))
//			  {
//				  long a = ((Bag)(((Player)objs[j-1]).getExtPlayerInfo("bag"))).point;
//				  long b = ((Bag)(((Player)objs[j]).getExtPlayerInfo("bag"))).point;
//				  
//				  a += ((Storage)(((Player)objs[j-1]).getExtPlayerInfo("storage"))).point;
//				  b += ((Storage)(((Player)objs[j]).getExtPlayerInfo("storage"))).point;
//				  
//				  if(a < b)
//				  {
//				  	Object tmp = objs[j];
//				  	objs[j] = objs[j-1];
//				  	objs[j-1] = tmp;
//			      }
//			  }
//			  else if(string.equals("playerPetLevel"))
//			  {
//				  if(((PetTome)(((Player)objs[j-1]).getExtPlayerInfo("petTome"))).getActivePet().level<((PetTome)(((Player)objs[j]).getExtPlayerInfo("petTome"))).getActivePet().level)
//				  {
//				  	Object tmp = objs[j];
//				  	objs[j] = objs[j-1];
//				  	objs[j-1] = tmp;
//			      }
//			  }
//			  else if(string.equals("maxHitPoint"))
//			  {
//				  if((new PlayerController(((Player)objs[j-1])).getEquipPoint("maxHitPoint"))<(new PlayerController(((Player)objs[j])).getEquipPoint("maxHitPoint")))
//				  {
//				  	Object tmp = objs[j];
//				  	objs[j] = objs[j-1];
//				  	objs[j-1] = tmp;
//			      }
//			  }
//			  else if(string.equals("maxMagicPoint"))
//			  {
//				  if((new PlayerController(((Player)objs[j-1])).getEquipPoint("maxMagicPoint"))<(new PlayerController(((Player)objs[j])).getEquipPoint("maxMagicPoint")))
//				  {
//				  	Object tmp = objs[j];
//				  	objs[j] = objs[j-1];
//				  	objs[j-1] = tmp;
//			      }
//			  }
//			  else if(string.equals("phsAtt"))
//			  {
//				  if((new PlayerController(((Player)objs[j-1])).getEquipPoint("phsAtt"))<(new PlayerController(((Player)objs[j])).getEquipPoint("phsAtt")))
//				  {
//				  	Object tmp = objs[j];
//				  	objs[j] = objs[j-1];
//				  	objs[j-1] = tmp;
//			      }
//			  }
//			  else if(string.equals("sptAtt"))
//			  {
//				  if((new PlayerController(((Player)objs[j-1])).getEquipPoint("sptAtt"))<(new PlayerController(((Player)objs[j])).getEquipPoint("sptAtt")))
//				  {
//				  	Object tmp = objs[j];
//				  	objs[j] = objs[j-1];
//				  	objs[j-1] = tmp;
//			      }
//			  }
//			  else if(string.equals("phsSmiteRate"))
//			  {
//				  if((new PlayerController(((Player)objs[j-1])).getEquipPoint("phsSmiteRate"))<(new PlayerController(((Player)objs[j])).getEquipPoint("phsSmiteRate")))
//				  {
//				  	Object tmp = objs[j];
//				  	objs[j] = objs[j-1];
//				  	objs[j-1] = tmp;
//			      }
//			  }
//			  else if(string.equals("sptSmiteRate"))
//			  {
//				  if((new PlayerController(((Player)objs[j-1])).getEquipPoint("sptSmiteRate"))<(new PlayerController(((Player)objs[j])).getEquipPoint("sptSmiteRate")))
//				  {
//				  	Object tmp = objs[j];
//				  	objs[j] = objs[j-1];
//				  	objs[j-1] = tmp;
//			      }
//			  }
//			  else if(string.equals("phsSmiteHurtParm"))
//			  {
//				  if((new PlayerController(((Player)objs[j-1])).getEquipPoint("phsSmiteHurtParm"))<(new PlayerController(((Player)objs[j])).getEquipPoint("phsSmiteHurtParm")))
//				  {
//				  	Object tmp = objs[j];
//				  	objs[j] = objs[j-1];
//				  	objs[j-1] = tmp;
//			      }
//			  }
//			  else if(string.equals("sptSmiteHurtParm"))
//			  {
//				  if((new PlayerController(((Player)objs[j-1])).getEquipPoint("sptSmiteHurtParm"))<(new PlayerController(((Player)objs[j])).getEquipPoint("sptSmiteHurtParm")))
//				  {
//				  	Object tmp = objs[j];
//				  	objs[j] = objs[j-1];
//				  	objs[j-1] = tmp;
//			      }
//			  }
//			  else if(string.equals("phsHurtAvoid"))
//			  {
//				  if((new PlayerController(((Player)objs[j-1])).getEquipPoint("phsHurtAvoid"))<(new PlayerController(((Player)objs[j])).getEquipPoint("phsHurtAvoid")))
//				  {
//				  	Object tmp = objs[j];
//				  	objs[j] = objs[j-1];
//				  	objs[j-1] = tmp;
//			      }
//			  }
//			  else if(string.equals("sptHurtAvoid"))
//			  {
//				  if((new PlayerController(((Player)objs[j-1])).getEquipPoint("sptHurtAvoid"))<(new PlayerController(((Player)objs[j])).getEquipPoint("sptHurtAvoid")))
//				  {
//				  	Object tmp = objs[j];
//				  	objs[j] = objs[j-1];
//				  	objs[j-1] = tmp;
//			      }
//			  }
//			  else if(string.equals("curePoint"))
//			  {
//				  if((new PlayerController(((Player)objs[j-1])).getEquipPoint("curePoint"))<(new PlayerController(((Player)objs[j])).getEquipPoint("curePoint")))
//				  {
//				  	Object tmp = objs[j];
//				  	objs[j] = objs[j-1];
//				  	objs[j-1] = tmp;
//			      }
//			  }
//			  
//		    }
//		}
//		
//		
//		if(string.equals("level"))
//		{
//			int length = objs.length >= TOPCOUNT ? TOPCOUNT:objs.length;
//			for (int i = 0; i < length; i++)
//			{
//				levelPlayer.add(objs[i]);
//			}
//			
//			if(levelPlayer.size() > TOPCOUNT)
//			{
//				cutSort(levelPlayer,"level");
//			}
//		}
//		else if(string.equals("point"))
//		{
//			int length = objs.length >= TOPCOUNT ? TOPCOUNT:objs.length;
//			for (int i = 0; i < length; i++)
//			{
//				pointPlayer.add(objs[i]);
//			}
//			
//			if(pointPlayer.size() > TOPCOUNT)
//			{
//				cutSort(pointPlayer,"point");
//			}
//		}
//		else if(string.equals("playerPetLevel"))
//		{
//			int length = objs.length >= TOPCOUNT ? TOPCOUNT:objs.length;
//			for (int i = 0; i < length; i++)
//			{
//				playerPetLevel.add(objs[i]);
//			}
//			
//			if(playerPetLevel.size() > TOPCOUNT)
//			{
//				cutSort(playerPetLevel,"playerPetLevel");
//			}
//		}
//		else if(string.equals("maxHitPoint"))
//		{
//			int length = objs.length >= TOPCOUNT ? TOPCOUNT:objs.length;
//			for (int i = 0; i < length; i++)
//			{
//				maxHitPoint.add(objs[i]);
//			}
//			
//			if(maxHitPoint.size() > TOPCOUNT)
//			{
//				cutSort(maxHitPoint,"maxHitPoint");
//			}
//		}
//		else if(string.equals("maxMagicPoint"))
//		{
//			int length = objs.length >= TOPCOUNT ? TOPCOUNT:objs.length;
//			for (int i = 0; i < length; i++)
//			{
//				maxMagicPoint.add(objs[i]);
//			}
//			
//			if(maxMagicPoint.size() > TOPCOUNT)
//			{
//				cutSort(maxMagicPoint,"maxMagicPoint");
//			}
//		}
//		
//		else if(string.equals("phsAtt"))
//		{
//			int length = objs.length >= TOPCOUNT ? TOPCOUNT:objs.length;
//			for (int i = 0; i < length; i++)
//			{
//				phsAtt.add(objs[i]);
//			}
//			
//			if(phsAtt.size() > TOPCOUNT)
//			{
//				cutSort(phsAtt,"phsAtt");
//			}
//		}
//		else if(string.equals("sptAtt"))
//		{
//			int length = objs.length >= TOPCOUNT ? TOPCOUNT:objs.length;
//			for (int i = 0; i < length; i++)
//			{
//				sptAtt.add(objs[i]);
//			}
//			
//			if(sptAtt.size() > TOPCOUNT)
//			{
//				cutSort(sptAtt,"sptAtt");
//			}
//		}
//		else if(string.equals("phsSmiteRate"))
//		{
//			int length = objs.length >= TOPCOUNT ? TOPCOUNT:objs.length;
//			for (int i = 0; i < length; i++)
//			{
//				phsSmiteRate.add(objs[i]);
//			}
//			
//			if(phsSmiteRate.size() > TOPCOUNT)
//			{
//				cutSort(phsSmiteRate,"phsSmiteRate");
//			}
//		}
//		else if(string.equals("sptSmiteRate"))
//		{
//			int length = objs.length >= TOPCOUNT ? TOPCOUNT:objs.length;
//			for (int i = 0; i < length; i++)
//			{
//				sptSmiteRate.add(objs[i]);
//			}
//			
//			if(sptSmiteRate.size() > TOPCOUNT)
//			{
//				cutSort(sptSmiteRate,"sptSmiteRate");
//			}
//		}
//		else if(string.equals("phsSmiteHurtParm"))
//		{
//			int length = objs.length >= TOPCOUNT ? TOPCOUNT:objs.length;
//			for (int i = 0; i < length; i++)
//			{
//				phsSmiteHurtParm.add(objs[i]);
//			}
//			
//			if(phsSmiteHurtParm.size() > TOPCOUNT)
//			{
//				cutSort(phsSmiteHurtParm,"phsSmiteHurtParm");
//			}
//		}
//		else if(string.equals("sptSmiteHurtParm"))
//		{
//			int length = objs.length >= TOPCOUNT ? TOPCOUNT:objs.length;
//			for (int i = 0; i < length; i++)
//			{
//				sptSmiteHurtParm.add(objs[i]);
//			}
//			
//			if(sptSmiteHurtParm.size() > TOPCOUNT)
//			{
//				cutSort(sptSmiteHurtParm,"sptSmiteHurtParm");
//			}
//		}
//		else if(string.equals("phsHurtAvoid"))
//		{
//			int length = objs.length >= TOPCOUNT ? TOPCOUNT:objs.length;
//			for (int i = 0; i < length; i++)
//			{
//				phsHurtAvoid.add(objs[i]);
//			}
//			
//			if(phsHurtAvoid.size() > TOPCOUNT)
//			{
//				cutSort(phsHurtAvoid,"phsHurtAvoid");
//			}
//		}
//		else if(string.equals("sptHurtAvoid"))
//		{
//			int length = objs.length >= TOPCOUNT ? TOPCOUNT:objs.length;
//			for (int i = 0; i < length; i++)
//			{
//				sptHurtAvoid.add(objs[i]);
//			}
//			
//			if(sptHurtAvoid.size() > TOPCOUNT)
//			{
//				cutSort(sptHurtAvoid,"sptHurtAvoid");
//			}
//		}
//		else if(string.equals("curePoint"))
//		{
//			int length = objs.length >= TOPCOUNT ? TOPCOUNT:objs.length;
//			for (int i = 0; i < length; i++)
//			{
//				curePoint.add(objs[i]);
//			}
//			
//			if(curePoint.size() > TOPCOUNT)
//			{
//				cutSort(curePoint,"curePoint");
//			}
//		}
//		
//	}
//	
//	private void cutSort(List list,String string)
//	{
//
//		Object [] objs = list.toArray();
//		
//		for (int i = 0; i < objs.length; i++)
//		{
//			for(int j = objs.length-1 ; j > i ; j --)
//		    {
//				  if(string.equals("level"))
//				  {
//					  Player p1 = (Player)objs[j-1];
//					  Player p2 = (Player)objs[j];
//					  int type1 = p1.getZhuanshengState();
//					  int type2 = p2.getZhuanshengState();
//					  if(type1 == type2)
//					  {
//						  if(p1.level < p2.level)
//						  {
//						  	Object tmp = objs[j];
//						  	objs[j] = objs[j-1];
//						  	objs[j-1] = tmp;
//					      }
//					  }
//					  else
//					  {
//						 if(type1 < type2)
//						 {
//							 Object tmp = objs[j];
//						  	 objs[j] = objs[j-1];
//						  	 objs[j-1] = tmp;
//						 }
//					  }
//				  }
//				  else if(string.equals("point"))
//				  {
//					  long a = ((Bag)(((Player)objs[j-1]).getExtPlayerInfo("bag"))).point;
//					  long b = ((Bag)(((Player)objs[j]).getExtPlayerInfo("bag"))).point;
//					  
//					  a += ((Storage)(((Player)objs[j-1]).getExtPlayerInfo("storage"))).point;
//					  b += ((Storage)(((Player)objs[j]).getExtPlayerInfo("storage"))).point;
//					
//					  if(a < b)
//					  {
//					  	Object tmp = objs[j];
//					  	objs[j] = objs[j-1];
//					  	objs[j-1] = tmp;
//				      }
//				  }
//				  else if(string.equals("playerPetLevel"))
//				  {
//					  if(((PetTome)(((Player)objs[j-1]).getExtPlayerInfo("petTome"))).getActivePet().level<((PetTome)(((Player)objs[j]).getExtPlayerInfo("petTome"))).getActivePet().level)
//					  {
//					  	Object tmp = objs[j];
//					  	objs[j] = objs[j-1];
//					  	objs[j-1] = tmp;
//				      }
//				  }
//				  else if(string.equals("maxHitPoint"))
//				  {
//					  if((new PlayerController(((Player)objs[j-1])).getEquipPoint("maxHitPoint"))<(new PlayerController(((Player)objs[j])).getEquipPoint("maxHitPoint")))
//					  {
//					  	Object tmp = objs[j];
//					  	objs[j] = objs[j-1];
//					  	objs[j-1] = tmp;
//				      }
//				  }
//				  else if(string.equals("maxMagicPoint"))
//				  {
//					  if((new PlayerController(((Player)objs[j-1])).getEquipPoint("maxMagicPoint"))<(new PlayerController(((Player)objs[j])).getEquipPoint("maxMagicPoint")))
//					  {
//					  	Object tmp = objs[j];
//					  	objs[j] = objs[j-1];
//					  	objs[j-1] = tmp;
//				      }
//				  }
//				  else if(string.equals("phsAtt"))
//				  {
//					  if((new PlayerController(((Player)objs[j-1])).getEquipPoint("phsAtt"))<(new PlayerController(((Player)objs[j])).getEquipPoint("phsAtt")))
//					  {
//					  	Object tmp = objs[j];
//					  	objs[j] = objs[j-1];
//					  	objs[j-1] = tmp;
//				      }
//				  }
//				  else if(string.equals("sptAtt"))
//				  {
//					  if((new PlayerController(((Player)objs[j-1])).getEquipPoint("sptAtt"))<(new PlayerController(((Player)objs[j])).getEquipPoint("sptAtt")))
//					  {
//					  	Object tmp = objs[j];
//					  	objs[j] = objs[j-1];
//					  	objs[j-1] = tmp;
//				      }
//				  }
//				  else if(string.equals("phsSmiteRate"))
//				  {
//					  if((new PlayerController(((Player)objs[j-1])).getEquipPoint("phsSmiteRate"))<(new PlayerController(((Player)objs[j])).getEquipPoint("phsSmiteRate")))
//					  {
//					  	Object tmp = objs[j];
//					  	objs[j] = objs[j-1];
//					  	objs[j-1] = tmp;
//				      }
//				  }
//				  else if(string.equals("sptSmiteRate"))
//				  {
//					  if((new PlayerController(((Player)objs[j-1])).getEquipPoint("sptSmiteRate"))<(new PlayerController(((Player)objs[j])).getEquipPoint("sptSmiteRate")))
//					  {
//					  	Object tmp = objs[j];
//					  	objs[j] = objs[j-1];
//					  	objs[j-1] = tmp;
//				      }
//				  }
//				  else if(string.equals("phsSmiteHurtParm"))
//				  {
//					  if((new PlayerController(((Player)objs[j-1])).getEquipPoint("phsSmiteHurtParm"))<(new PlayerController(((Player)objs[j])).getEquipPoint("phsSmiteHurtParm")))
//					  {
//					  	Object tmp = objs[j];
//					  	objs[j] = objs[j-1];
//					  	objs[j-1] = tmp;
//				      }
//				  }
//				  else if(string.equals("sptSmiteHurtParm"))
//				  {
//					  if((new PlayerController(((Player)objs[j-1])).getEquipPoint("sptSmiteHurtParm"))<(new PlayerController(((Player)objs[j])).getEquipPoint("sptSmiteHurtParm")))
//					  {
//					  	Object tmp = objs[j];
//					  	objs[j] = objs[j-1];
//					  	objs[j-1] = tmp;
//				      }
//				  }
//				  else if(string.equals("phsHurtAvoid"))
//				  {
//					  if((new PlayerController(((Player)objs[j-1])).getEquipPoint("phsHurtAvoid"))<(new PlayerController(((Player)objs[j])).getEquipPoint("phsHurtAvoid")))
//					  {
//					  	Object tmp = objs[j];
//					  	objs[j] = objs[j-1];
//					  	objs[j-1] = tmp;
//				      }
//				  }
//				  else if(string.equals("sptHurtAvoid"))
//				  {
//					  if((new PlayerController(((Player)objs[j-1])).getEquipPoint("sptHurtAvoid"))<(new PlayerController(((Player)objs[j])).getEquipPoint("sptHurtAvoid")))
//					  {
//					  	Object tmp = objs[j];
//					  	objs[j] = objs[j-1];
//					  	objs[j-1] = tmp;
//				      }
//				  }
//				  else if(string.equals("curePoint"))
//				  {
//					  if((new PlayerController(((Player)objs[j-1])).getEquipPoint("curePoint"))<(new PlayerController(((Player)objs[j])).getEquipPoint("curePoint")))
//					  {
//					  	Object tmp = objs[j];
//					  	objs[j] = objs[j-1];
//					  	objs[j-1] = tmp;
//				      }
//				  }
//		    }
//		}
//		
//		List tlist = new ArrayList(TOPCOUNT);
//		for (int i = 0; i < TOPCOUNT; i++)
//		{
//			tlist.add(objs[i]);
//		}
//		
//		if(string.equals("level"))
//		{
//			levelPlayer = tlist;
//		}
//		else if(string.equals("point"))
//		{
//			pointPlayer = tlist;
//		}
//		else if(string.equals("playerPetLevel"))
//		{
//			playerPetLevel = tlist;
//		}
//		else if(string.equals("maxHitPoint"))
//		{
//			maxHitPoint = tlist;
//		}
//		else if(string.equals("maxMagicPoint"))
//		{
//			maxMagicPoint = tlist;
//		}
//		else if(string.equals("phsAtt"))
//		{
//			phsAtt = tlist;
//		}
//		else if(string.equals("sptAtt"))
//		{
//			sptAtt = tlist;
//		}
//		else if(string.equals("phsSmiteRate"))
//		{
//			phsSmiteRate = tlist;
//		}
//		else if(string.equals("sptSmiteRate"))
//		{
//			sptSmiteRate = tlist;
//		}
//		else if(string.equals("phsSmiteHurtParm"))
//		{
//			phsSmiteHurtParm = tlist;
//		}
//		else if(string.equals("sptSmiteHurtParm"))
//		{
//			sptSmiteHurtParm = tlist;
//		}
//		else if(string.equals("phsHurtAvoid"))
//		{
//			phsHurtAvoid = tlist;
//		}
//		else if(string.equals("sptHurtAvoid"))
//		{
//			sptHurtAvoid = tlist;
//		}
//		else if(string.equals("curePoint"))
//		{
//			curePoint = tlist;
//		}
//
//	}
	
	private void saveTopProcess()
	{
		/**levelPlayer*******************************************************************/
		int size = levelPlayer.size();
		
		StringBuffer sb = new StringBuffer();
		sb.append("id");
		sb.append("\t");
		sb.append("accountName");
		sb.append("\t");
		sb.append("playerName");
		sb.append("\t");
		sb.append("playerJob");
		sb.append("\t");
		sb.append("value");
		sb.append("\n");
		sb.append("\n");
		int count = 1;
		
		for (int j = 0; j < size; j++)
		{
			Player player = (Player)levelPlayer.get(j);

			if(player == null)
				continue;
			
			if(player.isClosed)
				continue;
			
			sb.append(count++);
			sb.append("\t");
			sb.append(Utils.split(Utils.split(Utils.split(player.accountName,"\n")[0], "\r")[0], "\t")[0]);
			sb.append("\t");
			sb.append(Utils.split(Utils.split(Utils.split(player.name,"\n")[0],"\r")[0], "\t")[0]);
			sb.append("\t");
			sb.append(player.upProfession);
			sb.append("\t");
			sb.append(player.level);
			sb.append("\n");
			

			
			if(count > TOPCOUNT)
				break;
		}
		sb.delete(sb.length()-1, sb.length());
		writeFile(GameServer.getAbsolutePath()+"tops/playerLevel.txt",sb.toString());
		
		
		/**pointPlayer*******************************************************************/
		size = pointPlayer.size();
		
		sb = new StringBuffer();
		sb.append("id");
		sb.append("\t");
		sb.append("accountName");
		sb.append("\t");
		sb.append("playerName");
		sb.append("\t");
		sb.append("value");
		sb.append("\n");
		sb.append("\n");
		count = 1;
		
		for (int j = 0; j < size; j++)
		{
			Player player = (Player)pointPlayer.get(j);

			if(player == null)
				continue;
			
			if(player.isClosed)
				continue;
			
			sb.append(count++);
			sb.append("\t");
			sb.append(Utils.split(Utils.split(Utils.split(player.accountName,"\n")[0], "\r")[0], "\t")[0]);
			sb.append("\t");
			sb.append(Utils.split(Utils.split(Utils.split(player.name,"\n")[0],"\r")[0], "\t")[0]);
			sb.append("\t");
			sb.append(((Bag)(player.getExtPlayerInfo("bag"))).point+((Storage)(player.getExtPlayerInfo("storage"))).point);
			sb.append("\n");
			
			if(count > TOPCOUNT)
				break;
		}
		sb.delete(sb.length()-1, sb.length());
		writeFile(GameServer.getAbsolutePath()+"tops/playerPoint.txt",sb.toString());
		
		/**playerPetLevel*******************************************************************/
		size = playerPetLevel.size();
		
		sb = new StringBuffer();
		sb.append("id");
		sb.append("\t");
		sb.append("accountName");
		sb.append("\t");
		sb.append("playerName");
		sb.append("\t");
		sb.append("value");
		sb.append("\n");
		sb.append("\n");
		count = 1;
		
		for (int j = 0; j < size; j++)
		{
			Player player = (Player)playerPetLevel.get(j);

			if(player == null)
				continue;
			
			if(player.isClosed)
				continue;
			
			sb.append(count++);
			sb.append("\t");
			sb.append(Utils.split(Utils.split(Utils.split(player.accountName,"\n")[0], "\r")[0], "\t")[0]);
			sb.append("\t");
			sb.append(Utils.split(Utils.split(Utils.split(player.name,"\n")[0],"\r")[0], "\t")[0]);
			sb.append("\t");
			sb.append((((PetTome)(player.getExtPlayerInfo("petTome")))).getActivePet().level);
			sb.append("\n");
			
			if(count > TOPCOUNT)
				break;
		}
		sb.delete(sb.length()-1, sb.length());
		writeFile(GameServer.getAbsolutePath()+"tops/playerPetLevel.txt",sb.toString());
		
		/**maxHitPoint*******************************************************************/
		size = maxHitPoint.size();
		
		sb = new StringBuffer();
		sb.append("id");
		sb.append("\t");
		sb.append("accountName");
		sb.append("\t");
		sb.append("playerName");
		sb.append("\t");
		sb.append("value");
		sb.append("\n");
		sb.append("\n");
		count = 1;
		
		for (int j = 0; j < size; j++)
		{
			Player player = (Player)maxHitPoint.get(j);

			if(player == null)
				continue;
			
			if(player.isClosed)
				continue;
			
			sb.append(count++);
			sb.append("\t");
			sb.append(Utils.split(Utils.split(Utils.split(player.accountName,"\n")[0], "\r")[0], "\t")[0]);
			sb.append("\t");
			sb.append(Utils.split(Utils.split(Utils.split(player.name,"\n")[0],"\r")[0], "\t")[0]);
			sb.append("\t");
			sb.append((new PlayerController(player).getEquipPoint("maxHitPoint")));
			sb.append("\n");
			
			if(count > TOPCOUNT)
				break;
		}
		sb.delete(sb.length()-1, sb.length());
		writeFile(GameServer.getAbsolutePath()+"tops/playerMaxHitPoint.txt",sb.toString());
		
		
		/**maxMagicPoint*******************************************************************/
		size = maxMagicPoint.size();
		
		sb = new StringBuffer();
		sb.append("id");
		sb.append("\t");
		sb.append("accountName");
		sb.append("\t");
		sb.append("playerName");
		sb.append("\t");
		sb.append("value");
		sb.append("\n");
		sb.append("\n");
		count = 1;
		
		for (int j = 0; j < size; j++)
		{
			Player player = (Player)maxMagicPoint.get(j);

			if(player == null)
				continue;
			
			if(player.isClosed)
				continue;
			
			sb.append(count++);
			sb.append("\t");
			sb.append(Utils.split(Utils.split(Utils.split(player.accountName,"\n")[0], "\r")[0], "\t")[0]);
			sb.append("\t");
			sb.append(Utils.split(Utils.split(Utils.split(player.name,"\n")[0],"\r")[0], "\t")[0]);
			sb.append("\t");
			sb.append((new PlayerController(player).getEquipPoint("maxMagicPoint")));
			sb.append("\n");
			
			if(count > TOPCOUNT)
				break;
		}
		sb.delete(sb.length()-1, sb.length());
		writeFile(GameServer.getAbsolutePath()+"tops/playerMaxMagicPoint.txt",sb.toString());
		
		
		/**phsAtt*******************************************************************/
		size = phsAtt.size();
		
		sb = new StringBuffer();
		sb.append("id");
		sb.append("\t");
		sb.append("accountName");
		sb.append("\t");
		sb.append("playerName");
		sb.append("\t");
		sb.append("value");
		sb.append("\n");
		sb.append("\n");
		count = 1;
		
		for (int j = 0; j < size; j++)
		{
			Player player = (Player)phsAtt.get(j);

			if(player == null)
				continue;
			
			if(player.isClosed)
				continue;
			
			sb.append(count++);
			sb.append("\t");
			sb.append(Utils.split(Utils.split(Utils.split(player.accountName,"\n")[0], "\r")[0], "\t")[0]);
			sb.append("\t");
			sb.append(Utils.split(Utils.split(Utils.split(player.name,"\n")[0],"\r")[0], "\t")[0]);
			sb.append("\t");
			sb.append((new PlayerController(player).getEquipPoint("phsAtt")));
			sb.append("\n");
			
			if(count > TOPCOUNT)
				break;
		}
		sb.delete(sb.length()-1, sb.length());
		writeFile(GameServer.getAbsolutePath()+"tops/playerPhsAtt.txt",sb.toString());	
		
		/**sptAtt*******************************************************************/
		size = sptAtt.size();
		
		sb = new StringBuffer();
		sb.append("id");
		sb.append("\t");
		sb.append("accountName");
		sb.append("\t");
		sb.append("playerName");
		sb.append("\t");
		sb.append("value");
		sb.append("\n");
		sb.append("\n");
		count = 1;
		
		for (int j = 0; j < size; j++)
		{
			Player player = (Player)sptAtt.get(j);

			if(player == null)
				continue;
			
			if(player.isClosed)
				continue;
			
			sb.append(count++);
			sb.append("\t");
			sb.append(Utils.split(Utils.split(Utils.split(player.accountName,"\n")[0], "\r")[0], "\t")[0]);
			sb.append("\t");
			sb.append(Utils.split(Utils.split(Utils.split(player.name,"\n")[0],"\r")[0], "\t")[0]);
			sb.append("\t");
			sb.append((new PlayerController(player).getEquipPoint("sptAtt")));
			sb.append("\n");
			
			if(count > TOPCOUNT)
				break;
		}
		sb.delete(sb.length()-1, sb.length());
		writeFile(GameServer.getAbsolutePath()+"tops/playerSptAtt.txt",sb.toString());
		
		/**phsSmiteRate*******************************************************************/
		size = phsSmiteRate.size();
		
		sb = new StringBuffer();
		sb.append("id");
		sb.append("\t");
		sb.append("accountName");
		sb.append("\t");
		sb.append("playerName");
		sb.append("\t");
		sb.append("value");
		sb.append("\n");
		sb.append("\n");
		count = 1;
		
		for (int j = 0; j < size; j++)
		{
			Player player = (Player)phsSmiteRate.get(j);

			if(player == null)
				continue;
			
			if(player.isClosed)
				continue;
			
			sb.append(count++);
			sb.append("\t");
			sb.append(Utils.split(Utils.split(Utils.split(player.accountName,"\n")[0], "\r")[0], "\t")[0]);
			sb.append("\t");
			sb.append(Utils.split(Utils.split(Utils.split(player.name,"\n")[0],"\r")[0], "\t")[0]);
			sb.append("\t");
			sb.append((new PlayerController(player).getEquipPoint("phsSmiteRate")));
			sb.append("\n");
			
			if(count > TOPCOUNT)
				break;
		}
		sb.delete(sb.length()-1, sb.length());
		writeFile(GameServer.getAbsolutePath()+"tops/playerPhsSmiteRate.txt",sb.toString());
		
		/**sptSmiteRate*******************************************************************/
		size = sptSmiteRate.size();
		
		sb = new StringBuffer();
		sb.append("id");
		sb.append("\t");
		sb.append("accountName");
		sb.append("\t");
		sb.append("playerName");
		sb.append("\t");
		sb.append("value");
		sb.append("\n");
		sb.append("\n");
		count = 1;
		
		for (int j = 0; j < size; j++)
		{
			Player player = (Player)sptSmiteRate.get(j);

			if(player == null)
				continue;
			
			if(player.isClosed)
				continue;
			
			sb.append(count++);
			sb.append("\t");
			sb.append(Utils.split(Utils.split(Utils.split(player.accountName,"\n")[0], "\r")[0], "\t")[0]);
			sb.append("\t");
			sb.append(Utils.split(Utils.split(Utils.split(player.name,"\n")[0],"\r")[0], "\t")[0]);
			sb.append("\t");
			sb.append((new PlayerController(player).getEquipPoint("sptSmiteRate")));
			sb.append("\n");
			
			if(count > TOPCOUNT)
				break;
		}
		sb.delete(sb.length()-1, sb.length());
		writeFile(GameServer.getAbsolutePath()+"tops/playerSptSmiteRate.txt",sb.toString());
		
		/**phsSmiteHurtParm*****************************************************/
		size = phsSmiteHurtParm.size();
		
		sb = new StringBuffer();
		sb.append("id");
		sb.append("\t");
		sb.append("accountName");
		sb.append("\t");
		sb.append("playerName");
		sb.append("\t");
		sb.append("value");
		sb.append("\n");
		sb.append("\n");
		count = 1;
		
		for (int j = 0; j < size; j++)
		{
			Player player = (Player)phsSmiteHurtParm.get(j);

			if(player == null)
				continue;
			
			if(player.isClosed)
				continue;
			
			sb.append(count++);
			sb.append("\t");
			sb.append(Utils.split(Utils.split(Utils.split(player.accountName,"\n")[0], "\r")[0], "\t")[0]);
			sb.append("\t");
			sb.append(Utils.split(Utils.split(Utils.split(player.name,"\n")[0],"\r")[0], "\t")[0]);
			sb.append("\t");
			sb.append((new PlayerController(player).getEquipPoint("phsSmiteHurtParm")));
			sb.append("\n");
			
			if(count > TOPCOUNT)
				break;
		}
		sb.delete(sb.length()-1, sb.length());
		writeFile(GameServer.getAbsolutePath()+"tops/playerPhsSmiteHurtParm.txt",sb.toString());
		
		/**sptSmiteHurtParm*****************************************************/
		size = sptSmiteHurtParm.size();
		
		sb = new StringBuffer();
		sb.append("id");
		sb.append("\t");
		sb.append("accountName");
		sb.append("\t");
		sb.append("playerName");
		sb.append("\t");
		sb.append("value");
		sb.append("\n");
		sb.append("\n");
		count = 1;
		
		for (int j = 0; j < size; j++)
		{
			Player player = (Player)sptSmiteHurtParm.get(j);

			if(player == null)
				continue;
			
			if(player.isClosed)
				continue;
			
			sb.append(count++);
			sb.append("\t");
			sb.append(Utils.split(Utils.split(Utils.split(player.accountName,"\n")[0], "\r")[0], "\t")[0]);
			sb.append("\t");
			sb.append(Utils.split(Utils.split(Utils.split(player.name,"\n")[0],"\r")[0], "\t")[0]);
			sb.append("\t");
			sb.append((new PlayerController(player).getEquipPoint("sptSmiteHurtParm")));
			sb.append("\n");
			
			if(count > TOPCOUNT)
				break;
		}
		sb.delete(sb.length()-1, sb.length());
		writeFile(GameServer.getAbsolutePath()+"tops/playerSptSmiteHurtParm.txt",sb.toString());
		
		/**phsHurtAvoid*****************************************************/
		size = phsHurtAvoid.size();
		
		sb = new StringBuffer();
		sb.append("id");
		sb.append("\t");
		sb.append("accountName");
		sb.append("\t");
		sb.append("playerName");
		sb.append("\t");
		sb.append("value");
		sb.append("\n");
		sb.append("\n");
		count = 1;
		
		for (int j = 0; j < size; j++)
		{
			Player player = (Player)phsHurtAvoid.get(j);

			if(player == null)
				continue;
			
			if(player.isClosed)
				continue;
			
			sb.append(count++);
			sb.append("\t");
			sb.append(Utils.split(Utils.split(Utils.split(player.accountName,"\n")[0], "\r")[0], "\t")[0]);
			sb.append("\t");
			sb.append(Utils.split(Utils.split(Utils.split(player.name,"\n")[0],"\r")[0], "\t")[0]);
			sb.append("\t");
			sb.append((new PlayerController(player).getEquipPoint("phsHurtAvoid")));
			sb.append("\n");
			
			if(count > TOPCOUNT)
				break;
		}
		sb.delete(sb.length()-1, sb.length());
		writeFile(GameServer.getAbsolutePath()+"tops/playerPhsHurtAvoid.txt",sb.toString());
		
		/**sptHurtAvoid*****************************************************/
		size = sptHurtAvoid.size();
		
		sb = new StringBuffer();
		sb.append("id");
		sb.append("\t");
		sb.append("accountName");
		sb.append("\t");
		sb.append("playerName");
		sb.append("\t");
		sb.append("value");
		sb.append("\n");
		sb.append("\n");
		count = 1;
		
		for (int j = 0; j < size; j++)
		{
			Player player = (Player)sptHurtAvoid.get(j);

			if(player == null)
				continue;
			
			if(player.isClosed)
				continue;
			
			sb.append(count++);
			sb.append("\t");
			sb.append(Utils.split(Utils.split(Utils.split(player.accountName,"\n")[0], "\r")[0], "\t")[0]);
			sb.append("\t");
			sb.append(Utils.split(Utils.split(Utils.split(player.name,"\n")[0],"\r")[0], "\t")[0]);
			sb.append("\t");
			sb.append((new PlayerController(player).getEquipPoint("sptHurtAvoid")));
			sb.append("\n");
			
			if(count > TOPCOUNT)
				break;
		}
		sb.delete(sb.length()-1, sb.length());
		writeFile(GameServer.getAbsolutePath()+"tops/playerSptHurtAvoid.txt",sb.toString());
		
		/**curePoint*****************************************************/
		size = curePoint.size();
		
		sb = new StringBuffer();
		sb.append("id");
		sb.append("\t");
		sb.append("accountName");
		sb.append("\t");
		sb.append("playerName");
		sb.append("\t");
		sb.append("value");
		sb.append("\n");
		sb.append("\n");
		count = 1;
		
		for (int j = 0; j < size; j++)
		{
			Player player = (Player)curePoint.get(j);

			if(player == null)
				continue;
			
			if(player.isClosed)
				continue;
			
			sb.append(count++);
			sb.append("\t");
			sb.append(Utils.split(Utils.split(Utils.split(player.accountName,"\n")[0], "\r")[0], "\t")[0]);
			sb.append("\t");
			sb.append(Utils.split(Utils.split(Utils.split(player.name,"\n")[0],"\r")[0], "\t")[0]);
			sb.append("\t");
			sb.append((new PlayerController(player).getEquipPoint("curePoint")));
			sb.append("\n");
			
			if(count > TOPCOUNT)
				break;
		}
		sb.delete(sb.length()-1, sb.length());
		writeFile(GameServer.getAbsolutePath()+"tops/playerCurePoint.txt",sb.toString());

	}
	
	private void writeFile(String fileName,String fileContent)
	{
		try
		{
			FileOutputStream fos = new FileOutputStream(new File(fileName));
			OutputStreamWriter osw = new OutputStreamWriter(fos, "GBK");
			osw.write(new String(fileContent.getBytes()));
			osw.close();
			fos.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 到开启时候12点
	 */
	public void initTime()
	{
		long time = System.currentTimeMillis();
		day = (byte)Utils.getCurrentDate(time);
		nextDayTime = 0;
		for (long i = 0; i < Long.MAX_VALUE; i+=60000)
		{
			int nextDate = Utils.getCurrentDate(time+i);
			
			if(nextDate != day)
			{
				nextDayTime = time = i;
				break;
			}
		}
	}
	
	public void init()
	{
		GameServer.getInstance().initialWithoutNetServer();
		sourceDA = GameServer.getInstance().getDatabaseAccessor();
		
		//for (int i = 0; i < backsDA.length; i++)
		//{
		//	backsDA[i] = new DatabaseAccessor(sourceDA.getDbhost(),sourceDA.getDbport(),"qmb"+(i+1),sourceDA.getUser(),sourceDA.getPwd(),sourceDA.getHeart());
		//}
	}
	
	
	
	

	public static void main(String[] args)
	{

		Backup backup = new Backup();
		backup.init();
		
		Thread thread = new Thread(backup);
		thread.start();
		
	}

	
}
