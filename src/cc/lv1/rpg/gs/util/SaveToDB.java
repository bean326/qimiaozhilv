package cc.lv1.rpg.gs.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import vin.rabbit.util.Utils;
import vin.rabbit.util.xml.XMLFactory;
import vin.rabbit.util.xml.XMLNode;
import cc.lv1.rpg.gs.dao.DatabaseAccessor;

public class SaveToDB
{
	public static void main(String[] args)
	{
		
		InputStreamReader reader = new InputStreamReader(System.in);
		BufferedReader buffer = new BufferedReader(reader);
		
		String strs [] = null;
		try
		{
			System.out.println("dbserver|dbFile|dbname|playerId|accountName");
			String str = buffer.readLine();
			strs = Utils.split(str, "|");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		String txt = EncryptFactory.getInstance().decodeFile(strs[0]).trim();
		XMLNode node = XMLFactory.getInstance().parseXML(txt);
		String host = node.getSubNode("host").getData().trim();
		String port = node.getSubNode("port").getData().trim();
		String user = node.getSubNode("user").getData().trim();
		String pwd = node.getSubNode("pwd").getData().trim();
		
		DatabaseAccessor da = new DatabaseAccessor(host,port,strs[2],user,pwd,"sqlserver");
		
		InputStream is;
		byte data[] = null;
		try
		{
			is = new FileInputStream(strs[1]);
			data = new byte[is.available()];
			is.read(data);
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		da.savePlayerData(Integer.parseInt(strs[3]), strs[4], data);

	}
}
