package cc.lv1.rpg.gs.data;

import cc.lv1.rpg.gs.entity.RPGameObject;
import cc.lv1.rpg.gs.entity.impl.Player;

public class Exp extends RPGameObject
{
	
	public static int max_level = 1;
	
	public int level;
	
	public long levelExp;
	
	public long total;
	
	
	public String toString()
	{
		return "level "+level+" " +
				":  levelExp "+levelExp+" " +
						":  total "+total;
	}
	
	public boolean setVariable(String key,String value)
	{
		if(key.equals("level"))
		{
			level = Integer.parseInt(value);
			return true;
		}
		else if(key.equals("levelExp"))
		{
			levelExp = Long.parseLong(value);
			return true;
		}
		else if(key.equals("total"))
		{
			total = Long.parseLong(value);
			return true;
		}
		else
			return super.setVariable(key, value);
	}


	public boolean checkIsLevelUp(Player player)
	{
		return player.experience >= total;
	}
	

}
