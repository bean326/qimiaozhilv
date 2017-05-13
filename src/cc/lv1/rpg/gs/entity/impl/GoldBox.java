package cc.lv1.rpg.gs.entity.impl;

import vin.rabbit.util.Utils;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.RPGameObject;
import cc.lv1.rpg.gs.entity.controller.PlayerController;

/**
 * 普通抽奖
 * @author bean
 *
 */
public class GoldBox extends RPGameObject
{
	/** 每天最多能抽多少次奖(炼金) */
	public static final int MAXCOUNT = 10;
	
	/** 炼金需要的等级 */
	public static final int GOLDLEVEL = 600;
	
	public static final int[] notAwardGoods = {1045010101,1045010102};
	
	public int rate;
	
	public boolean isNotice;
	
	public int[] goodsIds;
	
	public int[] goodsCounts;
	
	
	/**
	 * 随机取得奖品
	 * @return
	 */
	public Goods getGoods()
	{
		if(goodsIds == null || goodsCounts == null)
			return null;
		int random = (int) (Math.random() * goodsIds.length);
		if(goodsIds[random] == 0 || goodsCounts[random] == 0)
			return null;
		Goods[] gos = DataFactory.getInstance().makeGoods(goodsIds[random], goodsCounts[random]);
		return gos==null?null:gos[0];
	}
	
	/**
	 * 首先判断炼金是否成功
	 * @param goods
	 * 价格	<=3500 60%失败，30%成功。
		3500-7500 40%失败，60%成功。
		>=7500 15%失败，85%成功
	 * @return
	 */
	public static boolean isSuccess(PlayerController target,Goods goods)
	{
		int success = 0;
		if(goods.point <= 3500)
			success = 3000;
		else if(goods.point < 7500)
			success = 6000; 
		else
			success = 8500; 
		int random = (int) (Math.random() * 10000);
		if(random <= success)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * 判断一个物品能否用来炼金术
	 * @param goodsId
		VIP宝石不能炼金
	 * @return
	 */
	public static boolean isAward(Goods goods)
	{
		if(goods.type != 6)
			return false;
		if(goods.token > 0)
			return false;
		for (int i = 0; i < notAwardGoods.length; i++) 
		{
			if(goods.id == notAwardGoods[i])
				return false;
		}
		return true;
	}
	
	public boolean setVariable(String key,String value)
	{
		if(key.equals("isNotice"))
		{
			isNotice = Integer.parseInt(value) == 1;
			return true;
		}
		else if(key.equals("goodsIds"))
		{
			String[] strs = Utils.split(value, ":");
			goodsIds = new int[strs.length];
			for (int i = 0; i < strs.length; i++)
			{
				goodsIds[i] = Integer.parseInt(strs[i]);
			}
			return true;
		}
		else if(key.equals("goodsCounts"))
		{
			String[] strs = Utils.split(value, ":");
			goodsCounts = new int[strs.length];
			for (int i = 0; i < strs.length; i++)
			{
				goodsCounts[i] = Integer.parseInt(strs[i]);
			}
			return true;
		}
		else
		{
			return super.setVariable(key, value);
		}
	}
}
