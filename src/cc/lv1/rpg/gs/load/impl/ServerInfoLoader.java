package cc.lv1.rpg.gs.load.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.Map;
import vin.rabbit.util.collection.impl.ArrayList;
import vin.rabbit.util.collection.impl.HashMap;
import vin.rabbit.util.xml.XMLFactory;
import vin.rabbit.util.xml.XMLNode;
import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.data.MarkChar;
import cc.lv1.rpg.gs.gui.MainFrame;
import cc.lv1.rpg.gs.load.i.ILoader;
import cc.lv1.rpg.gs.net.SMsg;
import cc.lv1.rpg.gs.util.BackupLoadSave;
import cc.lv1.rpg.gs.util.EncryptFactory;
/**
 * 服务器信息加载器
 * @author dxw
 *
 */
public class ServerInfoLoader implements ILoader
{

	private GameServer gameserver = null;

	private String serverStr = GameServer.getAbsolutePath()+"config/gameserver.xml";
	
	private String infoStr = GameServer.getAbsolutePath()+"config/info.txt";
	
	public void setAttachment(Object obj)
	{
		if(obj instanceof GameServer)
			gameserver = (GameServer)obj;
		else
			MainFrame.println("unknown attachment!");
	}

	public void loading()
	{
		String txt = EncryptFactory.getInstance().decodeFile(serverStr).trim();
		XMLNode node = XMLFactory.getInstance().parseXML(txt);
		
		int id = Integer.parseInt(node.getSubNode("id").getData().trim());
		String name = node.getSubNode("name").getData().trim();
		int maxConn = Integer.parseInt(node.getSubNode("maxConnection").getData().trim());
		int port = Integer.parseInt(node.getSubNode("port").getData().trim());
		String key =  node.getSubNode("key").getData().trim();
		
		if(GameServer.isBackSever)
			port=port+1;
		
		gameserver.id = id;
		gameserver.name = name;
		gameserver.port = port;
		gameserver.maxConnection = maxConn;
		gameserver.key = key;
		
		MainFrame.println("--- Server Info ---");
		MainFrame.println("Server ID "+id);
		MainFrame.println("Server Name "+name);
		MainFrame.println("Server Port "+port);
		MainFrame.println("Server MaxConnection "+maxConn);
		//MainFrame.println("login key "+key);
		
		
		loadMessage();
	}

	
	
	
	private void loadMessage()
	{
		
		try
		{
			File file = new File(infoStr);

			if(!file.exists())
				return;
			
			
			Class cls = Class.forName("cc.lv1.rpg.gs.net.SMsg");

			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			String []strs = null;
			Field field = null;
			while ( (line = br.readLine()) != null)
			{
			   if(line== null || line.equals(""))
				   continue;
				
			   strs = Utils.split(line, "\t");
			   
			   

			   if(strs == null || strs.length != 2 ||strs[0].equals("info"))
				   continue;
			   
			   
			   field = cls.getField(strs[0]);

			   if(field == null)
				   continue;

			   
			   if(field.toString().indexOf(" final ") == -1)
			   {
				   field.set(strs[0],Integer.parseInt(strs[1].substring(5),16));
				   //System.out.println(strs[0]+" "+Integer.parseInt(strs[1].substring(5),16));
			   }
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		MainFrame.println("loadMessage Success !!!");
	}

	private void changeMessage()
	{
		StringBuffer sb = new StringBuffer();
		
		try
		{
			Class cls = Class.forName("cc.lv1.rpg.gs.net.SMsg");

			Field []fields = cls.getFields();
			
			String fieldName = "";
			Integer value = null;
			int ran = 0;
			
			Map map = new HashMap();
			

			System.out.println("info\tvalue");
			sb.append("info\tvalue\r\n");
			for (int i = 0; i < fields.length; i++)
			{
				ran = (int)(Math.random()*8000);
				try
				{
					
					if(fields[i].toString().indexOf("int cc.lv1.rpg.gs.net.SMsg.T") != -1 || fields[i].toString().indexOf("int vin.rabbit.net.AppMessage.T_TYPE_MARK") != -1)
					{
						System.out.println(fields[i].getName() +"  " +"0x000"+Integer.toHexString((Integer)fields[i].get(fieldName)));
						sb.append(fields[i].getName() +"\t" +"0x000"+Integer.toHexString((Integer)fields[i].get(fieldName))+"\r\n");
					}
					
					if(fields[i].toString().indexOf(" final ") == -1)
					{
						fieldName = fields[i].getName();
						value = (Integer)fields[i].get(fieldName);
						
						while(map.get(ran+value) != null)
						{
							ran = ((int)(Math.random()*8000));
							System.out.println("已有这个随机数");
							continue;
						}				
						map.put(ran+value, "");				
		
						fields[i].set(fieldName, value+ran);
						System.out.println(fieldName +"\t" +"0x000"+Integer.toHexString((Integer)fields[i].get(fieldName))+" "+Integer.toHexString((SMsg.T_TYPE_MARK&(Integer)fields[i].get(fieldName))));

						sb.append(fieldName +"\t" +"0x000"+Integer.toHexString((Integer)fields[i].get(fieldName))+"\r\n");

					}
				} catch (IllegalArgumentException e)
				{
					e.printStackTrace();
				} catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
		} 
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}

		
		Utils.writeFile(infoStr, sb.toString().getBytes());
	}


	public static void main(String[] args)
	{
		new ServerInfoLoader().changeMessage();
		System.out.println("检查重复:");
		BackupLoadSave backup = new BackupLoadSave();
		backup.loadInfo();
		System.out.println("-------------");
	}
}
