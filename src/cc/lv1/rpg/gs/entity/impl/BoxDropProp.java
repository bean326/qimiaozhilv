package cc.lv1.rpg.gs.entity.impl;

import vin.rabbit.comm.GameObject;
import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.impl.ArrayList;
import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.RPGameObject;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsEquip;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsPetEquip;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsProp;
import cc.lv1.rpg.gs.gui.MainFrame;

/**
 * 宝箱掉宝组
 * @author balan
 *
 */
public class BoxDropProp extends RPGameObject
{
	/** 最低等级 */
	public int minLevel;
	
	/** 最高等级 */
	public int maxLevel;
	
	/** 掉宝组物品类型(1.Box1 2.Box2 3.Box3 4.Box4 5.Box5 6.Box6 7.Box7) */
	public int type;
	
	/** 宝箱类型(1.金宝箱 2.银宝箱 3.武器宝箱 4.装备宝箱 
	 * 5.元宝宝箱 6.试练宝箱 7.青铜圣殿 8.白银圣殿 
	 * 9.黄金圣殿 10.天神圣殿 13.BOSS之星素材宝箱 14.无双金蛋 
	 * 15.新蛋 16.星星宝箱 17.月亮宝箱) 
	 * */
	public int boxType;
	
	/** 选到这个掉宝组里的东西的几率(0表示没有几率，随机) */
	public int rate;
	
	/** 生成品质的几率(白:绿:蓝:紫1:紫2:紫3:预留) */
	public int[] qualityRate = new int[7];
	
	public int[] propId = null;
	
	public int[] counts = null;
	
	
	public void copyTo(GameObject go)
	{
		super.copyTo(go);
		BoxDropProp bdp = (BoxDropProp) go;
		
		bdp.minLevel = minLevel;
		bdp.maxLevel = maxLevel;
		bdp.type = type;
		bdp.boxType = boxType;
		bdp.rate = rate;
		
		int [] tmp = new int[propId.length];
		System.arraycopy(propId, 0, tmp, 0, tmp.length);
		bdp.propId = tmp;
		
		tmp = new int[counts.length];
		System.arraycopy(counts, 0, tmp, 0, tmp.length);
		bdp.counts = tmp;
		
		tmp = new int[qualityRate.length];
		System.arraycopy(qualityRate, 0, tmp, 0, tmp.length);
		bdp.qualityRate = tmp;
	}
	
	public void setDefault(List objs,List cs)
	{
		propId = new int[objs.size()];
		for (int i = 0; i < propId.length; i++) 
		{
			try
			{
				propId[i] = Integer.parseInt(objs.get(i).toString());
			}
			catch(Exception e)
			{
				System.out.println("BoxDropProp 42:"+objs.get(i).toString());
			}
		}
		
		counts = new int[cs.size()];
		for (int i = 0; i < counts.length; i++) 
		{
			try
			{
				counts[i] = Integer.parseInt(cs.get(i).toString());
			}
			catch(Exception e)
			{
				System.out.println("BoxDropProp 66:"+cs.get(i).toString()+"  type:"+type+"  "+objs.get(i).toString());
			}
		}
	}
	
	/**
	 * 元宝宝箱取物品
	 * @return
	 */
	public Goods getGoodsByMoney()
	{
		if(propId == null)
			return null;
		if(isZero())
		{
			return null;
		}
		int random = (int) (Math.random() * propId.length);
		if(propId[random] == 0)
		{
			System.out.println("BoxDropProp getGoodsByMoney prodId is zero:"+id);
			return null;
		}
		Object obj = DataFactory.getInstance().getGameObject(propId[random]);
		if(obj == null)
		{
			System.out.println("BoxDropProp getGoodsByMoney goods is null propId error:"+propId[random]);
			return null;
		}
		if(!(obj instanceof Goods))
		{
			System.out.println("BoxDropProp getGoodsByMoney obj is not Goods:"+propId[random]);
			return null;
		}
		Goods newGoods = null;
		if(obj instanceof GoodsEquip)
		{
			GoodsEquip equip = (GoodsEquip) obj;
			if(equip.taskColor == -1)
				newGoods = equip.makeNewBetterEquip(0);
			else
				newGoods = equip.makeNewBetterEquip(equip.taskColor);
			newGoods.setBindMode(Goods.BOXBINDMODE);
		}
		else if(obj instanceof GoodsPetEquip)
		{
			GoodsPetEquip equip = (GoodsPetEquip) obj;
			newGoods = (Goods) Goods.cloneObject(equip);
			newGoods.setBindMode(Goods.BOXBINDMODE);
		}
		else if(obj instanceof GoodsProp)
		{
			GoodsProp prop = (GoodsProp) obj;
			newGoods = (Goods) Goods.cloneObject(prop);
			newGoods.goodsCount = counts[random];
		}
		else
		{
			System.out.println("BoxDropProp getGoodsByMoney:"+obj.getClass());
		}
		if(newGoods == null)
		{
			System.out.println("BoxDropProp getGoodsByMoney:"+propId);
			return null;
		}
		newGoods.objectIndex = GameServer.getInstance().getDatabaseAccessor().getGoodsObjIndex();
		return newGoods;
	}
	
	/**
	 * 普通宝箱
	 * flag 是否读取配置文件中的绑定状态
	 * @return
	 */
	public Goods getGoods(boolean flag)
	{
		if(propId == null)
		{
			return null;
		}
		if(isZero())
		{
			return null;
		}
		int random = (int) (Math.random() * propId.length);
		if(propId[random] == 0)
		{
			System.out.println("BoxDropProp getGoods prodId is zero:"+id);
			return null;
		}
		Object obj = DataFactory.getInstance().getGameObject(propId[random]);
		if(obj == null)
		{
			System.out.println("BoxDropProp getGoods goods is null propId error:"+propId[random]);
			return null;
		}
		if(!(obj instanceof Goods))
		{
			System.out.println("BoxDropProp getGoods obj is not Goods:"+propId[random]);
			return null;
		}
		Goods newGoods = null;
		if(obj instanceof GoodsEquip)
		{
			GoodsEquip equip = (GoodsEquip) obj;
			newGoods = equip.makeNewBetterEquip(getQuality());
			if(!flag)
				newGoods.setBindMode(Goods.BOXBINDMODE);
		}
		else if(obj instanceof GoodsPetEquip)
		{
			GoodsPetEquip equip = (GoodsPetEquip) obj;
			newGoods = (Goods) Goods.cloneObject(equip);
			if(!flag)
				newGoods.setBindMode(Goods.BOXBINDMODE);
		}
		else if(obj instanceof GoodsProp)
		{
			GoodsProp prop = (GoodsProp) obj;
			newGoods = (Goods) Goods.cloneObject(prop);
			newGoods.goodsCount = counts[random];
		}
		else
		{
			System.out.println("BoxDropProp getGoods:"+obj.getClass());
			return null;
		}
		if(newGoods == null)
		{
			System.out.println("BoxDropProp getGoods:"+propId);
			return null;
		}
		newGoods.objectIndex = GameServer.getInstance().getDatabaseAccessor().getGoodsObjIndex();
		return newGoods;
	}
	
	
//	/**
//	 * 宠物出行抽物品
//	 * @return
//	 */
//	public Goods getGoodsByPet()
//	{
//		if(propId == null)
//			return null;
//		if(isZero())
//		{
//			return null;
//		}
//		int random = (int) (Math.random() * propId.length);
//		if(propId[random] == 0)
//			return null;
//		Object obj = DataFactory.getInstance().getGameObject(propId[random]);
//		if(obj == null)
//		{
//			System.out.println("BoxDropProp goods is null propId error:"+propId[random]);
//			return null;
//		}
//		if(!(obj instanceof Goods))
//		{
//			return null;
//		}
//		Goods newGoods = null;
//		if(obj instanceof GoodsEquip)
//		{
//			GoodsEquip equip = (GoodsEquip) obj;
//			newGoods = equip.makeNewBetterEquip(getQuality());
//			newGoods.setBindMode(2);
//		}
//		else if(obj instanceof GoodsProp)
//		{
//			GoodsProp prop = (GoodsProp) obj;
//			newGoods = (Goods) Goods.cloneObject(prop);
//			newGoods.goodsCount = counts[random];
//		}
//		else
//		{
//			System.out.println("BoxDropProp getGoods:"+obj.getClass());
//		}
//		if(newGoods == null)
//		{
//			System.out.println("BoxDropProp getGoods:"+propId);
//		}
//		newGoods.objectIndex = GameServer.getInstance().getDatabaseAccessor().getGoodsObjIndex();
//		return newGoods;
//	}
	
	private int getQuality()
	{
		int result = 0;
		int random = (int) (Math.random() * 10000) + 1;
		int wRate = qualityRate[0];
		int rRate = qualityRate[0] + qualityRate[1];
		int bRate = qualityRate[0] + qualityRate[1] + qualityRate[2];
		int prOne = qualityRate[0] + qualityRate[1] + qualityRate[2] + qualityRate[3];
		int prTwo = qualityRate[0] + qualityRate[1] + qualityRate[2] + qualityRate[3] + qualityRate[4];
		int prThr = qualityRate[0] + qualityRate[1] + qualityRate[2] + qualityRate[3] + qualityRate[4] + qualityRate[5];
		if(random <= wRate)
			result = 0;
		else if(random <= rRate)
			result = 1;
		else if(random <= bRate)
			result = 2;
		else if(random <= prOne)
			result = 3;
		else if(random <= prTwo)
			result = 4;
		else if(random <= prThr)
			result = 5;
		return result;
	}
	
	public boolean isZero()
	{
		for (int i = 0; i < propId.length; i++) 
		{
			if(propId[i] != 0)
				return false;
		}
		return true;
	}
	
	public boolean setVariable(String key,String value)
	{
		if(key.equals("minLevel"))
		{
			minLevel = Integer.parseInt(value);
			return true;
		}
		else if(key.equals("maxLevel"))
		{
			maxLevel = Integer.parseInt(value);
			return true;
		}
		else if(key.equals("type"))
		{
			type = Integer.parseInt(value);
			return true;
		}
		else if(key.equals("boxType"))
		{
			boxType = Integer.parseInt(value);
			return true;
		}
		else if(key.equals("rate"))
		{
			rate = Integer.parseInt(value);
			return true;
		}
		else if(key.equals("qualityRate"))
		{
			String[] strs = Utils.split(value, ":");
			for (int i = 0; i < strs.length; i++)
			{
				qualityRate[i] = Integer.parseInt(strs[i]);
			}
			return true;
		}
		else
		{
			return super.setVariable(key, value);
		}
	}

}
