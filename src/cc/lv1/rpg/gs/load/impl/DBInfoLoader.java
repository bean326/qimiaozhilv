package cc.lv1.rpg.gs.load.impl;

import vin.rabbit.util.xml.XMLFactory;
import vin.rabbit.util.xml.XMLNode;
import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.dao.DatabaseAccessor;
import cc.lv1.rpg.gs.gui.MainFrame;
import cc.lv1.rpg.gs.load.i.ILoader;
import cc.lv1.rpg.gs.util.EncryptFactory;
/**
 * 数据库加载器
 * @author dxw
 *
 */
public class DBInfoLoader implements ILoader
{

	private GameServer gameserver = null;

	private String dbStr = GameServer.getAbsolutePath()+"config/DBserver.xml";
	
	public void setAttachment(Object obj)
	{
		if(obj instanceof GameServer)
			gameserver = (GameServer)obj;
		else
			System.out.println("unknown attachment!");
	}
	
	
	
	public void loading()
	{
		String txt = EncryptFactory.getInstance().decodeFile(dbStr).trim();
		XMLNode node = XMLFactory.getInstance().parseXML(txt);
		
		String host = node.getSubNode("host").getData().trim();
		String port = node.getSubNode("port").getData().trim();
		String name = node.getSubNode("name").getData().trim();
		String user = node.getSubNode("user").getData().trim();
		String pwd = node.getSubNode("pwd").getData().trim();
		
		XMLNode heartNode = node.getSubNode("heart");
		String heart = null;
		if(heartNode == null)
			heart = "sqlserver";
		else
			heart = heartNode.getData().trim();
		
		DatabaseAccessor databaseAccessor = new DatabaseAccessor(host,port,name,user,pwd,heart);
		
		gameserver.setDatabaseAccessor(databaseAccessor);
		
		MainFrame.println("--- Database Info ---");
		MainFrame.println("Database host "+host);
		MainFrame.println("Database port "+port);
		MainFrame.println("Database name "+name);
//		MainFrame.println("Database user "+user);
//		MainFrame.println("Database pwd "+pwd);
	}

}
