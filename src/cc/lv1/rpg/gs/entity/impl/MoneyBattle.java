package cc.lv1.rpg.gs.entity.impl;

import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.RPGameObject;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.ext.Bag;
import cc.lv1.rpg.gs.other.ErrorCode;

public class MoneyBattle extends RPGameObject 
{
	public static final int BAIYIN_TIKET = 1045028037;
	public static final int HUANGJIN_TIKET = 1045028038;
	public static final int TIANSHEN_TIKET = 1045028039;
	public int roomId;
	
	/** 1.青铜圣殿 2.白银圣殿 3.黄金圣殿 4.天神圣殿 */
	public int type;
	
	public int point;
	
	public int money;
	
	/** 每天最多战斗几次 0表示无限制 */
	public int maxCount;
	
	public boolean isConditionEnough(PlayerController target)
	{
		Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
		if(bag.point < point)
		{
			target.sendAlert(ErrorCode.ALERT_SHOP_NO_POINT);
			return false;
		}
		Goods goods = null;
		if(type == 2)
			goods = bag.getGoodsById(BAIYIN_TIKET);
		else if(type == 3)
			goods = bag.getGoodsById(HUANGJIN_TIKET);
		else if(type == 4)
			goods = bag.getGoodsById(TIANSHEN_TIKET);
		if(goods == null)
		{
			if(bag.money < money)
			{
				target.sendAlert(ErrorCode.ALERT_SHOP_NO_MONEY);
				return false;
			}
		}
		if(type == 1)
		{
			if(target.getPlayer().moneyBattleCount >= maxCount && maxCount > 0)
			{
				target.sendAlert(ErrorCode.ALERT_MONEY_BATTLE_MAX_COUNT);
				return false;
			}
		}
		return true;
	}
	
	public void setMoneyBattleInfo(PlayerController target)
	{
		Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
		Goods goods = null;
		if(type == 2)
			goods = bag.getGoodsById(BAIYIN_TIKET);
		else if(type == 3)
			goods = bag.getGoodsById(HUANGJIN_TIKET);
		else if(type == 4)
			goods = bag.getGoodsById(TIANSHEN_TIKET);
		if(goods == null)
			bag.addMoney(target, -point, -money);
		else
			bag.removeGoods(target, goods.objectIndex, 1);
		if(type == 1)
			target.getPlayer().moneyBattleCount++; 
		
		LogTemp log = new LogTemp();
		log.id = target.getID();
		log.name = target.getName();
		log.setLevel(target.getPlayer().level);
		log.setPoint(point);
		log.setMoney(money);
		log.setAccountName(target.getPlayer().accountName);
		DataFactory.getInstance().putLogMap(type,log);
	}
}
