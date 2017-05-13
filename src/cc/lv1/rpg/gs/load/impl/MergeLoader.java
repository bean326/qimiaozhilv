package cc.lv1.rpg.gs.load.impl;

import java.io.File;
import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.impl.ArrayList;
import vin.rabbit.util.xml.XMLNode;
import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.dao.DatabaseAccessor;
import cc.lv1.rpg.gs.gui.MainFrame;
import cc.lv1.rpg.gs.load.i.ILoader;


public class MergeLoader implements ILoader
{
	
	public static String [] goodsString = new String[0];
	
	public static DatabaseAccessor []mergeDAs = new DatabaseAccessor[0];
	
	private GameServer gameserver = null;	
	
	private String meStr = GameServer.getAbsolutePath()+"config/merge.xml";

	public void setAttachment(Object obj)
	{
		if(obj instanceof GameServer)
			gameserver = (GameServer)obj;
		else
			System.out.println("unknown attachment!");
	}

	public void loading()
	{
		File file = new File(meStr);
		if(!file.exists())
			return;
		
		XMLNode node = Utils.getXMLNode(file);

		ArrayList list = node.getSubNode("dbnames").getSubNodes();
		
		mergeDAs = new DatabaseAccessor[list.size()];
		
		DatabaseAccessor source= gameserver.getDatabaseAccessor();
		
		for (int i = 0; i < list.size(); i++)
		{
			XMLNode n = (XMLNode)list.get(i);
			MainFrame.println("Merge DB name "+n.getData());
			mergeDAs[i] = new DatabaseAccessor(source.getDbhost(),source.getDbport(),n.getData().trim(),source.getUser(),source.getPwd(),source.getHeart());
		}
		
		
		list = node.getSubNode("goods").getSubNodes();
		
		goodsString = new String[list.size()];
		for (int i = 0; i < list.size(); i++)
		{
			XMLNode n = (XMLNode)list.get(i);
			MainFrame.println("Merge DB GoodsStr "+n.getData());
			goodsString[i] = n.getData();
		}
	}

}
