package cc.lv1.rpg.gs.entity.controller;

import cc.lv1.rpg.gs.entity.impl.Monster;
import cc.lv1.rpg.gs.entity.impl.battle.MonsterBattleTmp;

/**
 * 怪物控制器
 * @author dxw
 *
 */
public class MonsterController extends SpriteController
{

	private Monster monster;

	public Monster getMonster()
	{
		return monster;
	}

	public void setMonster(Monster monster)
	{
		this.monster = monster;
	}

	public int getID()
	{
		return monster.id;
	}

	public String getName()
	{
		return monster.name;
	}

	public long getObjectIndex()
	{
		return monster.objectIndex;
	}
	

	
	public void update(long timeMillis)
	{
		
		MonsterBattleTmp monsterTemp = (MonsterBattleTmp)getAttachment();
		monsterTemp.update(timeMillis);
	}

}
