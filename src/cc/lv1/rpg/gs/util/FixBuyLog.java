package cc.lv1.rpg.gs.util;

import java.lang.reflect.Field;

import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.impl.ArrayList;
import vin.rabbit.util.collection.impl.HashMap;
import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.RPGameObject;
import cc.lv1.rpg.gs.entity.impl.Goods;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsEquip;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsProp;
import cc.lv1.rpg.gs.gui.MainFrame;

public class FixBuyLog 
{
	private String equipmentStr = "C:/Documents and Settings/bean/桌面/fix/equipment.txt";
	
	private String propStr = "C:/Documents and Settings/bean/桌面/fix/prop.txt";
	
	private String buyLogStr = "C:/Documents and Settings/bean/桌面/fix/buyLog.txt";
	
	
	private static HashMap datasMap = new HashMap(1000);
	
	private static List logs = new ArrayList(1000);
	
	/**
	 * 加载装备 
	 */
	public void loadGoods()
	{
		try 
		{
			ArrayList equips = Utils.loadFileVariables(equipmentStr, GoodsEquip.class);
			int size = equips.size();

			for (int i = 0; i < size; i++) 
			{
				GoodsEquip goods = (GoodsEquip)equips.get(i);
				if(DataFactory.getInstance().getGameObject(goods.id) != null)
				{
					System.out.println("load GoodsEquip equip exits:"+goods.id);
					continue;
				}
				datasMap.put(goods.name, goods);
			}
			
			ArrayList props = Utils.loadFileVariables(propStr, GoodsProp.class);
			size = props.size();
			for (int i = 0; i < size; i++) 
			{
				GoodsProp goods = (GoodsProp)props.get(i);
				goods.quality = (goods.minQuality == goods.maxQuality?goods.minQuality:0);
				if(DataFactory.getInstance().getGameObject(goods.id) != null)
				{
					System.out.println("load GoodsProp prop exits:"+goods.id);
					continue;
				}
				datasMap.put(goods.name, goods);
			}
			
			logs = Utils.loadFileVariables(buyLogStr, BuyLog.class);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("load equip error:"+e.getMessage());
		}
	}
	
	public static void testError()
	{
		try
		{
			Class cls = Class.forName("cc.lv1.rpg.gs.other.ErrorCode");
	
			Field []fields = cls.getFields();
			for (int i = 0; i < fields.length; i++)
			{
				System.out.println(fields[i].getInt(""));
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) 
	{
		testError();
		if(true)return;
		FixBuyLog fbl = new FixBuyLog();
		fbl.loadGoods();
		StringBuffer sb = new StringBuffer();
		sb.append("玩家帐号");
		sb.append("\t");
		sb.append("充值金额");
		sb.append("\t");
		sb.append("状态");
		sb.append("\t");
		sb.append("备注");
		sb.append(Utils.LINE_SEPARATOR);
		for (int i = 0; i < logs.size(); i++) 
		{
			BuyLog bl = (BuyLog) logs.get(i);
			String[] strs = Utils.split(bl.remark, ":");
			if(strs.length == 4)
			{
				if(strs[2].equals("经验卡生命精力礼包(150元宝)"))
					strs[2] = "经验卡生命精力礼包(中型)";
				else if(strs[2].equals("经验卡生命精力礼包(15元宝)"))
					strs[2] = "经验卡生命精力礼包(小型)";
				Goods goods = (Goods) datasMap.get(strs[2]);
System.out.println(strs[2]);
				int token = Integer.parseInt(strs[3]) * goods.token;
				if(bl.money != token)
				{
					bl.money = token;
				}
			}
			sb.append(bl.account);
			sb.append("\t");
			sb.append(bl.money);
			sb.append("\t");
			sb.append(bl.state);
			sb.append("\t");
			sb.append(bl.remark);
			if(i != logs.size()-1)
				sb.append(Utils.LINE_SEPARATOR);
		}
		Utils.writeFile("C:/Documents and Settings/bean/桌面/fix/购买记录.txt", sb.toString().getBytes());
	}
	

}
