package cc.lv1.rpg.gs.entity.impl;

import vin.rabbit.util.Utils;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.RPGameObject;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.util.FontConver;

/**
 * 排名奖励和回合奖励
 * @author bean
 *
 */
public class RankReward extends RPGameObject
{
	/** 名次 */
	public byte rank;
	
	/** 类型奖励 1.排名奖励 2.回合奖励(一轮完后的奖励,这里rank就没有用了，填0) 3.家族活动成员奖励 4.黄金斗士奖励*/
	public byte type;
	
	public int point;
	
	public int money;
	
	public int honor;
	
	/**
	 * goods1[0]:ID
	 * goods1[1]:数量
	 * goods1[2]:品质
	 */
	public int[] goods1 = new int[3];
	
	/**
	 * goods2[0]:ID
	 * goods2[1]:数量
	 * goods2[2]:品质
	 */
	public int[] goods2 = new int[3];
	
	/**
	 * goods3[0]:ID
	 * goods3[1]:数量
	 * goods3[2]:品质
	 */
	public int[] goods3 = new int[3];
	
	/**
	 * goods4[0]:ID
	 * goods4[1]:数量
	 * goods4[2]:品质
	 */
	public int[] goods4 = new int[3];
	
	
	
	public boolean setVariable(String key,String value)
	{
		if(key.equals("goods1"))
		{
			String[] strs = Utils.split(value, ":");
			for (int i = 0; i < strs.length; i++)
			{
				goods1[i] = Integer.parseInt(strs[i]);
			}
			return true;
		}
		else if(key.equals("goods2"))
		{
			String[] strs = Utils.split(value, ":");
			for (int i = 0; i < strs.length; i++)
			{
				goods2[i] = Integer.parseInt(strs[i]);
			}
			return true;
		}
		else if(key.equals("goods3"))
		{
			String[] strs = Utils.split(value, ":");
			for (int i = 0; i < strs.length; i++)
			{
				goods3[i] = Integer.parseInt(strs[i]);
			}
			return true;
		}
		else if(key.equals("goods4"))
		{
			String[] strs = Utils.split(value, ":");
			for (int i = 0; i < strs.length; i++)
			{
				goods4[i] = Integer.parseInt(strs[i]);
			}
			return true;
		}
		else if(key.equals("rank"))
		{
			rank = Byte.parseByte(value);
			return true;
		}
		else if(key.equals("type"))
		{
			type = Byte.parseByte(value);
			return true;
		}
		else
		{
			return super.setVariable(key, value);
		}
	}
	
	
}
