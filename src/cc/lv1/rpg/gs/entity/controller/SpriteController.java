package cc.lv1.rpg.gs.entity.controller;

import cc.lv1.rpg.gs.entity.container.PlayerContainer;
import cc.lv1.rpg.gs.entity.impl.battle.SpriteBattleTmp;

/**
 * 精灵控制器
 * @author dxw
 *
 */
public abstract class SpriteController
{

	private PlayerContainer playerContainer;

	private SpriteBattleTmp attachment;
	
	public abstract int getID();
	public abstract String getName();
	public abstract long getObjectIndex();
	
	public void setParent(PlayerContainer playerContainer)
	{
		this.playerContainer = playerContainer;
	}
	
	public SpriteBattleTmp getAttachment()
	{
		return attachment;
	}
	public void setAttachment(SpriteBattleTmp attachment)
	{
		this.attachment = attachment;
	}
	public PlayerContainer getParent()
	{
		return playerContainer;
	}
	
	public void update(long timeMillis)
	{
		
	}
}
