package cc.lv1.rpg.gs.entity.impl;

import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsEquip;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsPetEquip;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsProp;
import vin.rabbit.comm.GameObject;
import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.impl.ArrayList;

public class BossDropProp extends MonsterDropProp 
{
	/** 掉落物品数量 */
	public int dropCount;
	
	public void copyTo(GameObject gameObject)
	{
		BossDropProp mdp = (BossDropProp)gameObject;
		mdp.id = id;
		mdp.minMoney = minMoney;
		mdp.maxMoney = maxMoney;
		mdp.dropCount = dropCount;
		
		int [] tmp = new int[dropCount];
		System.arraycopy(propId, 0, tmp, 0, tmp.length);
		mdp.propId = tmp;
		
		tmp = new int[dropCount];
		System.arraycopy(dropRate, 0, tmp, 0, tmp.length);
		mdp.dropRate = tmp;
		
		tmp = new int[dropCount];
		System.arraycopy(minNumber, 0, tmp, 0, tmp.length);
		mdp.minNumber = tmp;
		
		tmp = new int[dropCount];
		System.arraycopy(maxNumber, 0, tmp, 0, tmp.length);
		mdp.maxNumber = tmp;
	}
	
	public boolean setVariable(String key,String value)
	{
		if(key.equals("propId"))
		{
			String strs []  = Utils.split(value, ":");
			propId = new int[strs.length];
			for(int i = 0 ; i < strs.length ; i ++)
			{
				propId[i] = Integer.parseInt(strs[i]);
			}
			dropCount = strs.length;
			return true;
		}
		else if(key.equals("dropRate"))
		{
			String strs [] = Utils.split(value, ":");
			dropRate = new int[strs.length];
			for(int i = 0 ; i < strs.length ; i ++)
			{
				dropRate[i] = Integer.parseInt(strs[i]);
			}
			return true;
		}
		else if(key.equals("minNumber"))
		{
			String strs [] = Utils.split(value, ":");
			minNumber = new int[strs.length];
			for(int i = 0 ; i < strs.length ; i ++)
			{
				minNumber[i] = Integer.parseInt(strs[i]);
			}
			return true;
		}
		else if(key.equals("maxNumber"))
		{
			String strs [] = Utils.split(value, ":");
			maxNumber = new int[strs.length];
			for(int i = 0 ; i < strs.length ; i ++)
			{
				maxNumber[i] = Integer.parseInt(strs[i]);
			}
			return true;
		}
		else
		{
			return super.setVariable(key, value);
		}
	}
	

	
	/**
	 * 获取10个物品
	 * @return
	 */
	public List getGoodsList()
	{
		List goodsList = new ArrayList(10);
		for (int i = 0; i < 10; i++) 
		{
			int num = 0;
			int rate = 0;
			int random = (int) (Math.random() * 10000) + 1;
			for (int j = 0; j < dropRate.length; j++) 
			{
				rate += dropRate[j];
				if(random <= rate)
				{
					num = j;
					break;
				}
			}
			if(propId[num] == 0)
			{
				System.out.println("BossDropProp getGoodsList propId is zero:"+id);
				continue;
			}
			Object obj = DataFactory.getInstance().getGameObject(propId[num]);
			if(obj == null)
			{
				System.out.println("BossDropProp getGoodsList goods is null:"+propId[num]);
				continue;
			}
			if(!(obj instanceof Goods))
			{
				System.out.println("BossDropProp getGoodsList obj is not Goods:"+propId[num]);
				continue;
			}
			Goods goods = (Goods) obj;
			int dCount = (int) (Math.random() * (maxNumber[num] - minNumber[num]) + minNumber[num]);
			if(goods instanceof GoodsEquip)
			{
				GoodsEquip equip = (GoodsEquip) goods;
				int q = (equip.taskColor==-1?0:equip.taskColor);
				Goods newGoods = equip.makeNewBetterEquip(q);
				newGoods.goodsCount = 1;
				newGoods.objectIndex = GameServer.getInstance().getDatabaseAccessor().getGoodsObjIndex();
				goodsList.add(newGoods);
			}
			else if(goods instanceof GoodsPetEquip)
			{
				Goods newGoods = (Goods) Goods.cloneObject(goods);
				newGoods.objectIndex = GameServer.getInstance().getDatabaseAccessor().getGoodsObjIndex();
				goodsList.add(newGoods);
			}
			else if(goods instanceof GoodsProp)
			{
				Goods newGoods = (Goods) Goods.cloneObject(goods);
				newGoods.goodsCount = dCount;
				newGoods.objectIndex = GameServer.getInstance().getDatabaseAccessor().getGoodsObjIndex();
				goodsList.add(newGoods);
			}
			else
			{
				System.err.println("BossDropProp goods id:"+propId[num]);
			}
		}
		return goodsList;
	}
	
	

	
}
