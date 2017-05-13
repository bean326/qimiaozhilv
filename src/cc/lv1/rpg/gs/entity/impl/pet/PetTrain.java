package cc.lv1.rpg.gs.entity.impl.pet;

import vin.rabbit.comm.GameObject;
import vin.rabbit.util.Utils;
import cc.lv1.rpg.gs.entity.RPGameObject;

/**
 * 宠物出行相关数据
 * @author bean
 *
 */
public class PetTrain extends RPGameObject
{
	/** 宠物最小等级 */
	public int minLevel;
	
	/** 宠物最大等级 */
	public int maxLevel;
	
	/** 出行获取的经验 (1.嬉戏 2.锻炼 3.探险)*/
	public int[] exeExp	= new int[3];
	
	/** 出行获取的亲密度 (1.嬉戏 2.锻炼 3.探险)*/
	public int[] strInt	= new int[3];
	
	/** 出行获取的道具掉宝组 (1.嬉戏 2.锻炼 3.探险)*/
	public int[] dropId = new int[3];
	
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
		else if(key.equals("exeExp"))
		{
			String[] strs = Utils.split(value, ":");
			for (int i = 0; i < strs.length; i++)
			{
				exeExp[i] = Integer.parseInt(strs[i]);
			}
			return true;
		}
		else if(key.equals("strInt"))
		{
			String[] strs = Utils.split(value, ":");
			for (int i = 0; i < strs.length; i++)
			{
				strInt[i] = Integer.parseInt(strs[i]);
			}
			return true;
		}
		else if(key.equals("dropId"))
		{
			String[] strs = Utils.split(value, ":");
			for (int i = 0; i < strs.length; i++)
			{
				dropId[i] = Integer.parseInt(strs[i]);
			}
			return true;
		}
		else
			return super.setVariable(key, value);
	}
	
	public void copyTo(GameObject go)
	{
		super.copyTo(go);
		
		PetTrain pt = (PetTrain) go;
		
		pt.minLevel = minLevel;
		pt.maxLevel = maxLevel;
		
		int[] tmp = new int[3];
		System.arraycopy(exeExp, 0 , tmp, 0, tmp.length);
		pt.exeExp = tmp;
		
		tmp = new int[3];
		System.arraycopy(strInt, 0 , tmp, 0, tmp.length);
		pt.strInt = tmp;
		
		tmp = new int[3];
		System.arraycopy(dropId, 0 , tmp, 0, tmp.length);
		pt.dropId = tmp;
	}
}
