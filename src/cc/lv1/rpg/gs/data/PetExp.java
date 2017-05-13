package cc.lv1.rpg.gs.data;

import cc.lv1.rpg.gs.entity.RPGameObject;
import cc.lv1.rpg.gs.entity.impl.pet.Pet;

public class PetExp extends RPGameObject
{
	/** 默认亲密度最高 */
	public static long DEFAULTMAXINTI = 5162500;
	
	/** 默认经验值最高 */
	public static long DEFAULTMAXEXP = 7018000;
	
	public int level;
	
	public long levelExp;
	
	public long total;
	
	/** 总亲密度(用来判断是否能变身) */
	public long totalInt;
	
	public String toString()
	{
		return "level "+level+" " +
				":  levelExp "+levelExp+" " +
						":  total "+total;
	}
	
	public boolean setVariable(String key,String value)
	{
		if(key.equals("total"))
		{
			total = Long.parseLong(value);
			return true;
		}
		else if(key.equals("levelExp"))
		{
			levelExp = Long.parseLong(value);
			return true;
		}
		else if(key.equals("totalInt"))
		{
			totalInt = Long.parseLong(value);
			return true;
		}
		else
			return super.setVariable(key, value);
	}
	
	public boolean checkIsLevelUp(Pet pet)
	{
		return pet.experience >= total;
	}
	
}
