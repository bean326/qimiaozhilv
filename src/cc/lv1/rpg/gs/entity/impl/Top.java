package cc.lv1.rpg.gs.entity.impl;

import cc.lv1.rpg.gs.entity.RPGameObject;

/**
 * 
 * @author dxw
 *
 */
public class Top extends RPGameObject
{
	public String accountName;
	
	public String playerName;
	
	public long value;
	
	public int playerJob;
	
	
	public boolean setVariable(String key,String value)
	{
		if(key.equals("value"))
		{
			this.value = Long.parseLong(value);
			return true;
		}
		else 
			return super.setVariable(key, value);
	}
	
	
}
