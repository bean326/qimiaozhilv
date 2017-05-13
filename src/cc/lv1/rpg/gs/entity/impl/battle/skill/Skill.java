package cc.lv1.rpg.gs.entity.impl.battle.skill;

import vin.rabbit.comm.GameObject;
import vin.rabbit.util.Utils;
import cc.lv1.rpg.gs.entity.RPGameObject;

/**
 * 怪物和玩家的技能...
 * @author dxw
 *
 */
public class Skill extends RPGameObject
{
	
	public int level;
	
	public int iconId;
	
	public static final int TARGET_TYPE_PLAYERS = 1;
	
	public static final int TARGET_TYPE_MONSTERS = 2;
	
	public boolean isStudied = false;
	
	public boolean isPubSkill = false;
	
	/** 排序ID */
	public int order;
	
	/** 需要等级 */
	public int needLevel;
	
	/** 优先级(暂时只有宠物技能用到) */
	public int priority;
	
	/** 类型(1.黄金斗士活动专用技能) */
	public int type;
	
	/**
	1：己方
	2：敌方
	3：自己
	 */
	public int [] targetType = new int[3];
	
	
	/**
	 * CD时间
	 */
	public int CDTimer;
	
	public Skill()
	{
	}

	public void copyTo(GameObject gameobject)
	{
		super.copyTo(gameobject);
		Skill skill = (Skill)gameobject;
		skill.level = level;
		skill.iconId = iconId;
		skill.isPubSkill = isPubSkill;
		skill.CDTimer = CDTimer;
		skill.order = order;
		skill.type = type;
		int[] tmp = new int[3];
		System.arraycopy(targetType, 0 , tmp, 0, tmp.length);
		skill.targetType = tmp;
	}

	public int getLevel()
	{
		return level;
	}

	public void setLevel(int level)
	{
		this.level = level;
	}
	
	public boolean setVariable(String key,String value)
	{
		if(key.equals("targetType"))
		{
			String [] strs = Utils.split(value, ":");
			for(int i = 0 ; i < targetType.length ; i ++)
			{
				targetType[i] = Integer.parseInt(strs[i]);
			}
			return true;
		}
		else 
		{
			return super.setVariable(key, value);
		}
	}
	

	protected int getValue(int att,int dataPattern,int effectPoint)
	{
		int result = att;
		if(dataPattern == 1)//数值
		{
			result = effectPoint; 
		}
		else if(dataPattern == 2)
		{
			result = (int) (result * ((double)effectPoint/10000));
		}
		return result;		
	}
	protected double getValue(double att,int dataPattern,int effectPoint)
	{
		double result = att;
		if(dataPattern == 1)//数值
		{
			result = effectPoint; 
		}
		else if(dataPattern == 2)
		{
			result = result * ((double)effectPoint/10000);
		}
		return result;		
	}

}
