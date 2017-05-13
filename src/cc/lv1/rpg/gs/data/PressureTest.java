package cc.lv1.rpg.gs.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.entity.impl.PartyReward;
import cc.lv1.rpg.gs.entity.impl.Reward;
import cc.lv1.rpg.gs.gui.MainFrame;
import vin.rabbit.net.abs.NetConnection;
import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.impl.HashMap;

public class PressureTest
{
	
	private static PressureTest pt;
	
	private PressureTest()
	{

	}
	
	private FileOutputStream pchatOutputStream;
	
	public void putChatText(String line)
	{
		File logPath = new File(GameServer.getAbsolutePath()+"logs/pchats");
		if(!logPath.isDirectory())
		{
			logPath.mkdirs();
		}
		
		String date = WorldManager.getTypeTime("yyyyMMdd", System.currentTimeMillis());
		String fileName = GameServer.getAbsolutePath()+"logs/pchats/pchats_"+date+".txt";
		
		String context = "";
		File file  = new File(fileName);
		if(file.exists())
		{
			context = Utils.readFile2(file);
		}
		else
		{
			try
			{
				file.createNewFile();
			} 
		    catch (IOException e1)
			{
				e1.printStackTrace();
			}
		    
		    
		    if(pchatOutputStream != null)
		    {
		    	try
				{
		    		pchatOutputStream.close();
				} 
		    	catch (IOException e)
				{
					e.printStackTrace();
				}
		    }
		    pchatOutputStream =null;
		}
		
		if(pchatOutputStream == null) 
		{
			try
			{
				pchatOutputStream = new FileOutputStream(file);
				pchatOutputStream.write(context.getBytes());
				pchatOutputStream.flush();
			} 
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		try
		{
			pchatOutputStream.write((line).getBytes());
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}
	
	public static PressureTest getInstance()
	{
		if(pt == null)
			pt = new PressureTest();
		return pt;
	}
	
	public void saveData(List list,String str,int[] isActive,String fileName,HashMap map,String[] isSend)
	{
		StringBuffer sb = null;
		if(str.equals("familyMemberRank"))
		{
			sb = new StringBuffer();
			sb.append("rank");
			sb.append("\t");
			sb.append("name");
			sb.append("\t");
			sb.append("honorPoint");
			sb.append("\t");
			sb.append("logTime");
			sb.append("\t");
			sb.append("isSend");
			sb.append(Utils.LINE_SEPARATOR);
			
			for (int i = 0; i < list.size(); i++) 
			{
				PartyReward reward = (PartyReward) list.get(i);
				sb.append(reward.rank);
				sb.append("\t");
				sb.append(reward.name);
				sb.append("\t");
				sb.append(reward.honorPoint);
				sb.append("\t");
				sb.append(WorldManager.getTypeTime("yyyyMMdd-HH:mm:ss", reward.logTime));
				sb.append("\t");
				if(isSend != null)
					sb.append(isSend[i]);
				else
					sb.append(-1);
				if(i < list.size()-1)
					sb.append(Utils.LINE_SEPARATOR);
			}
		}
		else if(str.equals("familyRank"))
		{
			sb = new StringBuffer();
			sb.append("rank");
			sb.append("\t");
			sb.append("name");
			sb.append("\t");
			sb.append("leaderName");
			sb.append("\t");
			sb.append("playerCount");
			sb.append("\t");
			sb.append("honorPoint");
			sb.append("\t");
			sb.append("logTime");
			sb.append("\t");
			sb.append("isSend");
			sb.append(Utils.LINE_SEPARATOR);
			
			for (int i = 0; i < list.size(); i++) 
			{
				PartyReward reward = (PartyReward) list.get(i);
				sb.append(reward.rank);
				sb.append("\t");
				sb.append(reward.name);
				sb.append("\t");
				sb.append(reward.leaderName);
				sb.append("\t");
				sb.append(reward.playerCount);
				sb.append("\t");
				sb.append(reward.honorPoint);
				sb.append("\t");
				sb.append(WorldManager.getTypeTime("yyyyMMdd-hh:mm:ss", reward.logTime));
				sb.append("\t");
				List sendList = (List) map.get(reward.name);
				if(sendList == null)
				{
					if(isActive != null)
						sb.append(isActive[i]);
					else
						sb.append(-1);
				}
				else
				{
					StringBuffer sBuffer = new StringBuffer();
					for (int j = 0; j < sendList.size(); j++) 
					{
						String[] strs = Utils.split(sendList.get(j).toString(), ":");
						if(!strs[1].equals("1"))//0不在线，1在线,2数据库也没有, 3荣誉为0,4家族里没有
						{
							if(sBuffer.length() > 0)
							{
								sBuffer.append("|");
							}
							sBuffer.append(sendList.get(j).toString());
						}
					}
					sb.append(sBuffer.toString());
				}
				if(i < list.size()-1)
					sb.append(Utils.LINE_SEPARATOR);
			}
		}
		else if(str.equals("marryInfo") || str.equals("saleGoodsErrorInfo") || str.equals("killBossInfo"))
		{
			sb = new StringBuffer();
			sb.append(str);
			sb.append(Utils.LINE_SEPARATOR);
		}
		
		if(sb != null)
			saveTextByFileFolderName(sb.toString(),fileName,"familyParty");
		else
		{
			MainFrame.println("********"+WorldManager.getTypeTime("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis())+"************");
			MainFrame.println("PressureTest saveData error:[fileName:"+fileName+"][str:"+str+"]");
			MainFrame.println("********************");
		}
	}

	/**
	 * 
	 * @param obj
	 * @param type obj为list时1表示是问答活动排名 2是家族活动家族排名 3是家族活动第一名家族内的族员排名
	 */
	public void saveData(Object obj,int type)
	{
		if(obj instanceof List)
		{
			StringBuffer sb = new StringBuffer();
			if(type == 1)
			{
				sb.append("rank");
				sb.append("\t");
				sb.append("account");
				sb.append("\t");
				sb.append("name");
				sb.append("\t");
				sb.append("level");
				sb.append("\t");
				sb.append("point");
				sb.append("\t");
				sb.append("logTime");
				sb.append(Utils.LINE_SEPARATOR);
				
				List list = (List) obj;
				for (int i = 0; i < list.size(); i++) 
				{
					Reward reward = (Reward) list.get(i);
					sb.append(reward.rank);
					sb.append("\t");
					sb.append(reward.accountName);
					sb.append("\t");
					sb.append(reward.name);
					sb.append("\t");
					sb.append(reward.level);
					sb.append("\t");
					sb.append(reward.point);
					sb.append("\t");
					sb.append(WorldManager.getTypeTime("yyyyMMdd-hh:mm:ss", reward.logTime));
					if(i < list.size()-1)
						sb.append(Utils.LINE_SEPARATOR);
				}
			}

			saveTestText(sb.toString());
		}
		else if(obj instanceof String)
		{
			saveTestText(obj.toString());
		}

	}
	
	public void saveMoneyLog(String data,String fileName)
	{
		File file = new File(GameServer.getAbsolutePath()+"logs");
		if(!file.isDirectory())
		{
			file.mkdir();
		}
		file = new File(GameServer.getAbsolutePath()+"logs/moneyLog");
		if(!file.isDirectory())
		{
			file.mkdir();
		}
		
		String date = WorldManager.getTypeTime("yyyyMMdd", System.currentTimeMillis());
		
		String str = GameServer.getAbsolutePath()+"logs/moneyLog/"+fileName+"_"+date+".txt";
		
		Utils.writeFile(str,data.getBytes());
	}
	
	public void saveTextByFileFolderName(String data,String fileName,String folder)
	{
		File file = new File(GameServer.getAbsolutePath()+"logs");
		if(!file.isDirectory())
		{
			file.mkdir();
		}
		file = new File(GameServer.getAbsolutePath()+"logs/"+folder);
		if(!file.isDirectory())
		{
			file.mkdir();
		}
		
		String date = WorldManager.getTypeTime("yyyyMMdd", System.currentTimeMillis());
		
		String str = GameServer.getAbsolutePath()+"logs/"+folder+"/"+fileName+"_"+date+".txt";

		if("vipPlayerInfos".equals(fileName))
		{
			File f = new File(str);
			String info = "";
			if(f.exists())
			{
				try {
					FileReader fr = new FileReader(f);
					BufferedReader br = new BufferedReader(fr);
					String tempString = "";
					br.readLine();
		            while ((tempString = br.readLine()) != null)
		            {
		            	info += Utils.LINE_SEPARATOR + tempString;
		            }
		            br.close();
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
			data += info;
		}
		Utils.writeFile(str,data.getBytes());
	}
	
	
	public void saveTextByFileName(String data,String fileName)
	{
		File file = new File(GameServer.getAbsolutePath()+"logs");
		if(!file.isDirectory())
		{
			file.mkdir();
		}
		if(fileName.equals("familyPartyRewardSendInfo"))
		{
			file = new File(GameServer.getAbsolutePath()+"logs/familyParty");
		}
		else
		{
			file = new File(GameServer.getAbsolutePath()+"logs/"+fileName);
		}
		if(!file.isDirectory())
		{
			file.mkdir();
		}
		
		String date = WorldManager.getTypeTime("yyyyMMdd", System.currentTimeMillis());
		
		String str = "";
	
		if(fileName.equals("familyPartyRewardSendInfo"))
			str = GameServer.getAbsolutePath()+"logs/familyParty/"+fileName+"_"+date+".txt";
		else
			str = GameServer.getAbsolutePath()+"logs/"+fileName+"/"+fileName+"_"+date+".txt";

		if(fileName.equals("goldParty") || fileName.equals("familyPartyRewardSendInfo")
				|| fileName.equals("vipPlayerInfo") || fileName.equals("bakDataChange"))
		{
			File f = new File(str);
			String info = "";
			if(f.exists())
			{
				try {
					FileReader fr = new FileReader(f);
					BufferedReader br = new BufferedReader(fr);
					String tempString = "";
		            while ((tempString = br.readLine()) != null)
		            {
		            	info += Utils.LINE_SEPARATOR + tempString;
		            }
		            br.close();
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
			data += info;
		}
		
		Utils.writeFile(str,data.getBytes());
	}

	private void saveTestText(String data)
	{
		File file = new File(GameServer.getAbsolutePath()+"logs");
		if(!file.isDirectory())
		{
			file.mkdir();
		}
		file = new File(GameServer.getAbsolutePath()+"logs/answerRank");
		if(!file.isDirectory())
		{
			file.mkdir();
		}
		
		String date = WorldManager.getTypeTime("yyyyMMdd", System.currentTimeMillis());
		Utils.writeFile(GameServer.getAbsolutePath()+"logs/answerRank/answerRank_"+date+".txt",data.getBytes());
	}
	
	
	
	private FileOutputStream shopCenterOut;
	
	
	public void saveShopCenterText(String line)
	{
		File logPath = new File(GameServer.getAbsolutePath()+"logs/ShopCenter");
		if(!logPath.isDirectory())
		{
			logPath.mkdirs();
		}
		
		String date = WorldManager.getTypeTime("yyyyMMdd", System.currentTimeMillis());
		String fileName = GameServer.getAbsolutePath()+"logs/ShopCenter/shopCenter_"+date+".txt";
		
		String context = "";
		File file  = new File(fileName);
		if(file.exists())
		{
			context = Utils.readFile2(file);
		}
		else
		{
			try
			{
				file.createNewFile();
			} 
		    catch (IOException e1)
			{
				e1.printStackTrace();
			}
		    
		    
		    if(shopCenterOut != null)
		    {
		    	try
				{
					shopCenterOut.close();
				} 
		    	catch (IOException e)
				{
					e.printStackTrace();
				}
		    }
		    shopCenterOut =null;
		}
		
		if(shopCenterOut == null) 
		{
			try
			{
				shopCenterOut = new FileOutputStream(file);
				shopCenterOut.write(context.getBytes());
				shopCenterOut.flush();
			} 
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		try
		{
			shopCenterOut.write((line).getBytes());
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}
	
	
	public void saveSavePlayerInfoError(String data)
	{
		String str = GameServer.getAbsolutePath()+"logs/closeError";
		File file = new File(str);
		if(!file.isDirectory())
			file.mkdir();
		
		str = str+"/save_"+WorldManager.getTypeTime("yyyyMMdd", System.currentTimeMillis())+".txt";
		File f = new File(str);
		String info = "";
		if(f.exists())
		{
			try {
				FileReader fr = new FileReader(f);
				BufferedReader br = new BufferedReader(fr);
				String tempString = "";
	            while ((tempString = br.readLine()) != null)
	            {
	            	info += Utils.LINE_SEPARATOR + tempString;
	            }
	            br.close();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		data += info;
		Utils.writeFile(str, data.getBytes());
	}


	public static void main(String[] args)
	{
		while(true)
		{
			Utils.sleep(5000);
			PressureTest.getInstance().saveShopCenterText(System.currentTimeMillis()+"");
		}
	}

}
