package cc.lv1.rpg.gs.entity.impl;

import vin.rabbit.util.Utils;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.RPGameObject;

/**
 *	黄金斗士
 * @author bean
 *
 */
public class GoldParty extends RPGameObject 
{
	/** 房间ID集合 */
	public int[] roomIds;
	
	/** 胜利条件(进入下一级房间的条件) */
	public int winCon;
	
	/** 失败条件(返回上一级房间的条件) */
	public int loseCon;
	
	/** 房间等级(0.备战厅,1.青铜,2.白银3.黄金,4.4级) */
	public int level;
	
	/** 特殊奖励物品(第一轮物品ID:数量|第二轮物品ID:数量|...) */
	public String[] spRewards;
	
	/** 特殊奖励几率(万分比)(第一轮机率|第二轮机率|...) */
	public String[] rates;
	
	public int minX;
	
	public int maxX;
	
	public int minY;
	
	public int maxY;
	
	
	public int getRoomId()
	{
		if(roomIds == null)
			return 0;
		int random = (int) (Math.random() * roomIds.length); 
		return roomIds[random];
	}
	
	public boolean setVariable(String key,String value)
	{
		if(key.equals("roomId"))
		{
			if(value.equals("0"))
				return true;
			String[] strs = Utils.split(value, ":");
			roomIds = new int[strs.length];
			for (int i = 0; i < strs.length; i++)
			{
				roomIds[i] = Integer.parseInt(strs[i]);
			}
			return true;
		}
		else if(key.equals("spReward"))
		{
			if(value.equals("0"))
				return true;
			String[] strs = Utils.split(value, "|");
			for (int i = 0; i < strs.length; i++)
			{
				String[] ss = Utils.split(strs[i], ",");
				for (int j = 0; j < ss.length; j++)
				{
					String[] sss = Utils.split(ss[j], ":");
					try
					{
						Goods goods = (Goods) DataFactory.getInstance().getGameObject(Integer.parseInt(sss[0]));
						if(goods == null)
						{
							System.out.println("GoldParty setVariable goods is null:"+id+" goodsId:"+sss[0]);
						}
					}catch(Exception e)
					{
						System.out.println("GoldParty setVariable error:"+id+" goodsId:"+sss[0]);
					}
				}
			}
			spRewards = strs;
			return true;
		}
		else if(key.equals("rate"))
		{
			if(value.equals("0"))
				return true;
			String[] strs = Utils.split(value, "|");
			rates = strs;
			return true;
		}
		else if(key.equals("x"))
		{
			if(value.equals("0-0") || value.equals("0"))
				return true;
			if(value.indexOf("-") != -1)
			{
				String[] strs = Utils.split(value, "-");
				minX = Integer.parseInt(strs[0]);
				maxX = Integer.parseInt(strs[1]);
			}
			return true;
		}
		else if(key.equals("y"))
		{
			if(value.equals("0-0") || value.equals("0"))
				return true;
			if(value.indexOf("-") != -1)
			{
				String[] strs = Utils.split(value, "-");
				minY = Integer.parseInt(strs[0]);
				maxY = Integer.parseInt(strs[1]);
			}
			return true;
		}
		else
			return super.setVariable(key, value);
	}
}
