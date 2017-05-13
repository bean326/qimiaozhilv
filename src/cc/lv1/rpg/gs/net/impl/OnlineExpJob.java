package cc.lv1.rpg.gs.net.impl;

import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.data.DC;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.data.Exp;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.ext.Bag;
import cc.lv1.rpg.gs.entity.ext.OtherExtInfo;
import cc.lv1.rpg.gs.entity.impl.Goods;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsProp;
import cc.lv1.rpg.gs.util.FontConver;
import vin.rabbit.net.abs.NetConnection;
import vin.rabbit.net.job.i.NetJob;
/**
 * 在线经验
 * @author dxw
 *
 */
public class OnlineExpJob extends NetJob
{

	private Exp currentExp;
	
	private PlayerController target;

	public OnlineExpJob(Exp currentExp, PlayerController target)
	{
		this.currentExp = currentExp;
		this.target = target;
	}


	public NetConnection getConnection()
	{
		return null;
	}
	

	
	public void run()
	{
		long standExp = 0;
		if(target.getPlayer().level >= 5000)
		{
			//单跳经验=[当前升级经验/(480+((当前等级-4999)/2.5))*4]+250000
			if(target.getPlayer().level > PlayerController.EXPMAXLEVEL)
				standExp = (long)(currentExp.levelExp/(480+((double)(PlayerController.EXPMAXLEVEL-4999))/(double)2.5)*4+250000);
			else
				standExp = (long)(currentExp.levelExp/(480+((double)(target.getPlayer().level-4999))/(double)2.5)*4+250000);
		}
		else
		{
			standExp = currentExp.levelExp/480;
			standExp = standExp == 0? 1 : standExp;
			standExp *= 4;
		}
		
		Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
		Goods goods = (Goods) bag.getExtGoods(4);
		if(goods != null && goods instanceof GoodsProp)
		{
			GoodsProp prop = (GoodsProp) goods;
			if(WorldManager.currentTime - prop.expTimes < prop.objectIndex)//这里的流水表示 是多倍经验开始的时间
			{
				standExp *= prop.expMult;
			}
		}
		
		double mult = target.getExpMult();
		standExp *= mult;

		long disExp = target.addExp(standExp,true,false,false);
		if(disExp > 0)
			target.sendGetGoodsInfo(1, false, DC.getString(DC.PLAYER_70)+": "+disExp);//恭喜您获得在线经验××
	}

}
