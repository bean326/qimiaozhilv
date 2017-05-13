package cc.lv1.rpg.gs.entity.impl;

import vin.rabbit.comm.GameObject;
import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.impl.ArrayList;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.RPGameObject;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsEquip;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsPetEquip;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsProp;

public class MonsterDropProp extends RPGameObject
{
	/** 最小钱数量 */
	public int minMoney;
	
	/** 最大钱数量 */
	public int maxMoney;
	
	/** 最小荣誉量 */
	public int minHonor;
	
	/** 最大荣誉量 */
	public int maxHonor;
	
	/** 道具ID */
	public int[] propId = null;
	
	/** 道具掉落几率(万分之) */
	public int[] dropRate = null;
	
	/** 最小掉落数量 */
	public int[] minNumber = null;
	
	/** 最大掉落数量 */
	public int[] maxNumber = null;
	
	public void copyTo(GameObject gameObject)
	{
		super.copyTo(gameObject);
		
		MonsterDropProp mdp = (MonsterDropProp)gameObject;
		mdp.id = id;
		mdp.minMoney = minMoney;
		mdp.maxMoney = maxMoney;
		
		int [] tmp = new int[propId.length];
		System.arraycopy(propId, 0, tmp, 0, tmp.length);
		mdp.propId = tmp;
		
		tmp = new int[dropRate.length];
		System.arraycopy(dropRate, 0, tmp, 0, tmp.length);
		mdp.dropRate = tmp;
		
		tmp = new int[minNumber.length];
		System.arraycopy(minNumber, 0, tmp, 0, tmp.length);
		mdp.minNumber = tmp;
		
		tmp = new int[maxNumber.length];
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
	 * 获取怪物掉落的荣誉
	 * @return
	 */
	public int getRandomHonor()
	{
		if(maxHonor == 0 && minHonor == 0)
			return 0;
		int random = (int) (Math.random() * (maxHonor - minHonor) + minHonor);
		return random;
	}
	
	/**
	 * 获取怪物掉落的钱
	 * @return
	 */
	public int getRandomMoney()
	{
		if(maxMoney == 0 && minMoney == 0)
			return 0;
		int random = (int) (Math.random() * (maxMoney - minMoney) + minMoney);
//		int result = 0;
//		if(pVSeLevel <= 5)
//			result = random;
//		else if(pVSeLevel <= 10)
//			result = (int) (random * 0.8);
//		else if(pVSeLevel <= 15)
//			result = (int) (random * 0.5);
//		else if(pVSeLevel < 20)
//			result = (int) (random * 0.2);
//		System.out.println(result);
		return random;
	}
	

	
	/**
	 *   获取怪物掉落的物品
	 * @param pVSeLevel
	 * @param monster
	 * @param type 1为普通掉宝，2为任务掉宝
	 * @return
	 */
	public List getDropProp(int pVSeLevel,Monster monster,int type)
	{
		double rate = 1;
//		if(type == 1)
//		{
//			if(pVSeLevel <= 50)
//				rate = 1;
//			else if(pVSeLevel <= 100)
//				rate = 0.8;
//			else if(pVSeLevel <= 150)
//				rate = 0.5;
//			else if(pVSeLevel < 200)
//				rate = 0.2;
//		}
//		else if(type == 2)
//		{
//			rate = 1;
//		}
		
		List list = new ArrayList();
		
		for (int i = 0; i < propId.length; i++) 
		{
			if(propId[i] == 0)
				continue;
			Object obj = DataFactory.getInstance().getGameObject(propId[i]);
			if(obj == null)
			{
				System.out.println("MonsterDropProp getDropProp goods is null propId error:"+propId[i]);
				continue;
			}
			if(!(obj instanceof Goods))
			{
				System.out.println("MonsterDropProp getDropProp obj is not Goods:"+propId[i]);
				continue;
			}
			Goods goods = (Goods) obj;
			int dCount = (int) (Math.random() * (maxNumber[i] - minNumber[i]) + minNumber[i]);
			if(goods instanceof GoodsEquip)
			{
				GoodsEquip ge = (GoodsEquip) goods;
				double dRate = 0;
				int quality = -1;
				
				if(ge.taskColor != -1)
				{
					dRate = dropRate[i];
					quality = ge.taskColor;
				}
				else
				{
					dRate = rate * dropRate[i];//万分之
					quality = getQualityByMonster(monster, dRate);
				}
				int random = (int) (Math.random() * 10000);
				if(random <= dRate)
				{
					for (int j = 0; j < dCount; j++) 
					{
						Goods newGoods = ge.makeNewBetterEquip(quality);
						list.add(newGoods);
					}
				}
			}
			else if(goods instanceof GoodsPetEquip)
			{
				double dRate = rate * dropRate[i];//万分之
				int random = (int) (Math.random() * 10000) + 1;
				if(random <= dRate)
				{
					for (int j = 0; j < dCount; j++) 
					{
						Goods newGoods = (Goods) Goods.cloneObject(goods);
						list.add(newGoods);
					}
				}
			}
			else if(goods instanceof GoodsProp)
			{
				double dRate = rate * dropRate[i];//万分之
				int random = (int) (Math.random() * 10000) + 1;
				if(random <= dRate)
				{
					for (int j = 0; j < dCount; j++) 
					{
						Goods newGoods = (Goods) Goods.cloneObject(goods);
						list.add(newGoods);
					}
				}
			}
			else
			{
				System.err.println("MonsterDropProp goods id:"+propId[i]);
			}
		}
		return list;
	}
	

	
	/**
	 * 根据怪物类型得到掉宝的品质
	 * PS：x是指根据掉宝规则计算出的掉落几率
a)	普通怪掉落白色装备的几率=x*90%，绿装=x*10%
b)	特殊怪掉落白装的几率=x*50%，绿装掉落几率=x*30%，蓝装掉落几率=x*20%
c)	BOSS掉落白装的几率=x*35%，绿装掉落几率=x*30%，蓝装掉落几率=x*20%，紫1掉落几率=x*15%
d)	世界BOSS掉落白装的几率=x*30%，绿装掉落几率=x*25%，蓝装掉落几率=x*20%，紫1掉落几率=x*15%，紫2掉落几率=x*10%

	 * @param monster
	 * @return
	 */
	public int getQualityByMonster(Monster monster,double dropRate)
	{
		int result = 0,whiteRate = 0,greenRate = 0,blueRate = 0,purpleRate = 0;//0：普通怪 1：特殊怪物 2：BOSS 3：世界BOSS 
//		System.out.println("总的几率："+dropRate+"  随机数："+random+"  怪物类型："+monster.logicType);
		int random = (int) (Math.random() * dropRate);
		if(monster.logicType ==1)//普通怪
		{
			whiteRate = (int) (dropRate * 0.9);
//			System.out.println("白色几率："+whiteRate);
//			System.out.println("绿色几率："+(dropRate-whiteRate));
			if(random <= whiteRate)
				result = 0;//白色
			else
				result = 1;
		}
		else if(monster.logicType == 2)//特殊怪物
		{
			whiteRate = (int) (dropRate * 0.5);
			greenRate = (int) (dropRate * 0.8);
//			System.out.println("白色几率："+whiteRate);
//			System.out.println("绿色几率："+(greenRate-whiteRate));
//			System.out.println("蓝色几率："+(dropRate-greenRate));
			if(random <= whiteRate)
				result = 0;
			else if(random <= greenRate)
				result = 1;
			else
				result = 2;
		}
		else if(monster.logicType == 3)//BOSS
		{
			whiteRate = (int) (dropRate * 0.35);
			greenRate = (int) (dropRate * 0.65);
			blueRate = (int) (dropRate * 0.85);
//			System.out.println("白色几率："+whiteRate);
//			System.out.println("绿色几率："+(greenRate-whiteRate));
//			System.out.println("蓝色几率："+(blueRate-greenRate));
//			 System.out.println("紫1色几率："+(dropRate-blueRate));
			if(random <= whiteRate)
				result = 0;
			else if(random <= greenRate)
				result = 1;
			else if(random <= blueRate)
				result = 2;
			else
				result = 3;
		}
		else if(monster.logicType == 4)//世界BOSS
		{
			whiteRate = (int) (dropRate * 0.3);
			greenRate = (int) (dropRate * 0.55);
			blueRate = (int) (dropRate * 0.75);
			purpleRate = (int) (dropRate * 0.9);
//			System.out.println("白色几率："+whiteRate);
//			System.out.println("绿色几率："+(greenRate-whiteRate));
//			System.out.println("蓝色几率："+(blueRate-greenRate));
//			System.out.println("紫1色几率："+(purpleRate-blueRate));
//			System.out.println("紫2色几率："+(dropRate-purpleRate));
			if(random <= whiteRate)
				result = 0;
			else if(random <= greenRate)
				result = 1;
			else if(random <= blueRate)
				result = 2;
			else if(random <= purpleRate)
				result = 3;
			else
				result = 4;
		}	
		return result;
	}
	
	
	
	
}
