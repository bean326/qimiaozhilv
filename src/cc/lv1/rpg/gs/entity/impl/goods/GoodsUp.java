package cc.lv1.rpg.gs.entity.impl.goods;

import vin.rabbit.comm.GameObject;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.Utils;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.ext.Bag;
import cc.lv1.rpg.gs.entity.impl.Goods;
import cc.lv1.rpg.gs.entity.impl.battle.skill.ActiveSkill;
import cc.lv1.rpg.gs.other.ErrorCode;

public class GoodsUp extends Goods
{
	/** 进化源物品ID */
	public int sourceId;
	
	/** 进化目标ID */
	public int targetId;
	
	/** 需求的材料物品 */
	public int[] needGoodsId;

	/** 需求的材料物品数量 */
	public int[] needGoodsCount;
	
	/** 需求的游戏币 */
	public int needPoint;
	
	/** 需求的元宝 */
	public int needMoney;
	
	/** 需求的荣誉值 */
	public int needHonor;
	
	public void copyTo(GameObject gameObject)
	{
		super.copyTo(gameObject);
		
		GoodsUp gu = (GoodsUp)gameObject;
		int [] tmp = new int[needGoodsId.length];
		System.arraycopy(needGoodsId, 0, tmp, 0, tmp.length);
		gu.needGoodsId = tmp;

		tmp = new int[needGoodsCount.length];
		System.arraycopy(needGoodsCount, 0 , tmp, 0, tmp.length);
		gu.needGoodsCount = tmp;
		
		gu.sourceId = sourceId;
		gu.targetId = targetId;
		gu.needPoint = needPoint;
		gu.needMoney = needMoney;
		gu.needHonor = needHonor;
	}
	
	public boolean setVariable(String key,String value)
	{
		if(key.equals("needGoodsId"))
		{
			String [] strs = Utils.split(value, ":");
			needGoodsId = new int[strs.length];
			for(int i = 0 ; i < strs.length ; i ++)
			{
//				if(strs[i].equals("0"))
//				{
//					System.out.println("GoodsUp needGoodsId is zero id:"+id);
//					continue;
//				}
//				else
//				{
//					Goods goods = (Goods) DataFactory.getInstance().getGameObject(Integer.parseInt(strs[i]));
//					if(goods == null)
//					{
//						System.out.println("GoodsUp setVariable needGoodsId error:"+strs[i]+"  sourceId:"+sourceId);
//					}
//				}
				needGoodsId[i] = Integer.parseInt(strs[i]);
			}
			return true;
		}
		else if(key.equals("needGoodsCount"))
		{
			String [] strs = Utils.split(value, ":");
			needGoodsCount = new int[strs.length];
			for(int i = 0 ; i < strs.length ; i ++)
			{
//				if(strs[i].equals("0"))
//				{
//					System.out.println("GoodsUp needGoodsCount is zero id:"+id);
//					continue;
//				}
				needGoodsCount[i] = Integer.parseInt(strs[i]);
			}
			return true;
		}
		else 
		{
			return super.setVariable(key, value);
		}
	}
	
	public Goods getTargetGoods(Goods goods)
	{
		Goods newGoods = (Goods) DataFactory.getInstance().getGameObject(targetId);
		if(newGoods == null)
		{
			System.out.println("GoodsUp targetId is error:"+targetId);
			return null;
		}
		Goods targetGoods = null;
		if(newGoods instanceof GoodsEquip)
		{
			GoodsEquip equip = (GoodsEquip) newGoods;
			targetGoods = equip.makeNewBetterEquip(equip.taskColor);
		}
		else if(newGoods instanceof GoodsPetEquip)
		{
			targetGoods = (Goods) Goods.cloneObject(newGoods);
			if(goods instanceof GoodsPetEquip)
			{
				((GoodsPetEquip)targetGoods).setExtAtt(((GoodsPetEquip)goods).extAtt);
			}
		}
		else if(newGoods instanceof GoodsProp)
		{
			targetGoods = (Goods) Goods.cloneObject(newGoods);
		}
		return targetGoods;
	}
	
	public boolean isConEnough(PlayerController target)
	{
		Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
		if(bag.getGoodsCount(sourceId) < 1)
			return false;
		if(bag.point < needPoint || bag.money < needMoney || target.getHonour() < needHonor)
			return false;
		for (int i = 0; i < needGoodsId.length; i++) 
		{
			int bagCount = bag.getGoodsCount(needGoodsId[i]);
			if(bagCount < needGoodsCount[i])
				return false;
		}
		return true;
	}
	
	
	@Override
	public void onDeleteImpl(PlayerController target) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onRemoveImpl(PlayerController target) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onUseGoodsBattle(PlayerController player) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onUseImpl(PlayerController target) {
		// TODO Auto-generated method stub
		return false;
	}

}
