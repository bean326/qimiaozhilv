package cc.lv1.rpg.gs.util;

import cc.lv1.rpg.gs.entity.RPGameObject;
import vin.rabbit.util.Utils;

public class MailPO extends RPGameObject
{	
	
	public int id;
	
	public String accountName;
	
	public int point;
	
	public int money;
	
	public int goodsId1;
	
	public int goodsCount1;
	
	public int goodsId2;
	
	public int goodsCount2;
	
	public boolean setVariable(String key, String value)
	{
		if(key.equals("goods1"))
		{
			String strs []  = Utils.split(value, ":");
			goodsId1 = Integer.parseInt(strs[0]);
			goodsCount1 = Integer.parseInt(strs[1]);
			return true;
		}
		else if(key.equals("goods2"))
		{
			String strs []  = Utils.split(value, ":");
			goodsId2 = Integer.parseInt(strs[0]);
			goodsCount2 = Integer.parseInt(strs[1]);
			return true;
		}
		else
		{
			return super.setVariable(key, value);
		}
	}
	
}
