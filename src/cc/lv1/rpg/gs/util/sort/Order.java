package cc.lv1.rpg.gs.util.sort;

import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.ext.Bag;
import cc.lv1.rpg.gs.entity.ext.PetTome;
import cc.lv1.rpg.gs.entity.ext.Storage;
import cc.lv1.rpg.gs.entity.impl.Player;

public class Order
{
	private long value;
	private String key;
	private Player player;
	public Order(String key,Player player) 
	{
		this.key = key;
		this.player = player;
		if("level".equals(key))
		{
			value = player.level;
		}
		else if("point".equals(key))
		{
			value = ((Bag)player.getExtPlayerInfo("bag")).point;
			value += ((Storage)(player.getExtPlayerInfo("storage"))).point;
		}
		else if("playerPetLevel".endsWith(key))
		{
			value = ((PetTome)player.getExtPlayerInfo("petTome")).getActivePet().level;
		}
		else
		{
			value = new PlayerController(player).getEquipPoint(key);
		}
	}
	public String getKey() 
	{
		return key;
	}
	public void setKey(String key) 
	{
		this.key = key;
	}
	public long getValue()
	{
		return value;
	}
	public void setValue(long value)
	{
		this.value = value;
	}
	public Player getPlayer() 
	{
		return player;
	}
	public void setPlayer(Player player) 
	{
		this.player = player;
	}
}


