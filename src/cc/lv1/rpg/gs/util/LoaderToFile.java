package cc.lv1.rpg.gs.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import vin.rabbit.util.Utils;
import vin.rabbit.util.xml.XMLFactory;
import vin.rabbit.util.xml.XMLNode;

import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.dao.DatabaseAccessor;

public class LoaderToFile
{
//	private static String dbStr = "D:/DServer/config/DBserver.xml";
	
//	private static String dbStr = "E:/workspace/rpg_game/bin/config/DBserver.xml";
	
	
	private static String dbStr = "";
	
	public static void main(String[] args)
	{
		InputStreamReader reader = new InputStreamReader(System.in);
		BufferedReader buffer = new BufferedReader(reader);

		try
		{
			System.out.println("input path");
			dbStr = buffer.readLine();
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		reader = new InputStreamReader(System.in);
		buffer = new BufferedReader(reader);
		
		String inputStr = "";
		
		try
		{
			System.out.println("accountname:dbname");
			inputStr = buffer.readLine();
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		String []str = Utils.split(inputStr, ":");
		
		String txt = EncryptFactory.getInstance().decodeFile(dbStr).trim();
		XMLNode node = XMLFactory.getInstance().parseXML(txt);
		String host = node.getSubNode("host").getData().trim();
		String port = node.getSubNode("port").getData().trim();
		String user = node.getSubNode("user").getData().trim();
		String pwd = node.getSubNode("pwd").getData().trim();
		
	
		DatabaseAccessor da = new DatabaseAccessor(host,port,str[1],user,pwd,"sqlserver");
		
		byte data [] = da.loadPlayerData(str[0]);
		System.out.println(str[0]+ " load data size "+(data==null?"null":data.length));
		Utils.writeFile(str[0]+".qmdata", data);
		
	}
}
