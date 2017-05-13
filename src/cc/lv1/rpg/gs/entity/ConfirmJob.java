package cc.lv1.rpg.gs.entity;

import cc.lv1.rpg.gs.entity.controller.PlayerController;

public class ConfirmJob
{
	protected int lifeTime = 30 * 1000;
	
	protected String name;
	
	protected PlayerController source;
	
	public boolean isAlive()
	{
		return lifeTime > 0;
	}
	
	public void setLifeTime(int lifeTime)
	{
		this.lifeTime = lifeTime;
	}
	
	public void setDefaultLifeTime()
	{
		this.lifeTime = -0xffff;
	}
	
	public int getLifeTime()
	{
		return lifeTime;
	}
	
	public void cancel()
	{
		lifeTime = 0;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public PlayerController getSource()
	{
		return source;
	}

	public void setSource(PlayerController source)
	{
		this.source = source;
	}


	
	
	
}
