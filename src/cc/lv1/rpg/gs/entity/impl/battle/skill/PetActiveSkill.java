package cc.lv1.rpg.gs.entity.impl.battle.skill;
import vin.rabbit.comm.GameObject;
import vin.rabbit.util.Utils;


public class PetActiveSkill extends PetSkill
{
	/** 效果编号 */
	public int [] effectList = new int[3];
	
	/**
	 * 技能范围
		1：目标单体
		2：两个目标
		3：三个目标
		4：四个目标
		5：五个目标
		6：六个目标
		7：目标全体
	 */
	public int effectRangeList;
	
	/** 效果2几率 */
	public int rate2;

	/** 效果3几率 */
	public int rate3;
	
	/** 耗蓝 */
	public int magic;
	
	/** 释放几率 */
	public int skillRate;
	
	/**
	 * 技能释放开始时间(进入战斗后清零)
	 */
	public long processTime;
	
	public void copyTo(GameObject gameObject)
	{
		super.copyTo(gameObject);
		
		PetActiveSkill aSkill = (PetActiveSkill)gameObject;
		int [] tmp = new int[effectList.length];
		System.arraycopy(effectList, 0, tmp, 0, tmp.length);
		aSkill.effectList = tmp;
		aSkill.effectRangeList = effectRangeList;
		
		aSkill.rate2 = rate2;
		aSkill.rate3 = rate3;
		aSkill.skillRate = skillRate;
		aSkill.magic = magic;
		aSkill.processTime = processTime;
	}
	
	public boolean setVariable(String key,String value)
	{
		if(key.equals("effectList"))
		{
			String [] strs = Utils.split(value, ":");
			for(int i = 0 ; i < effectList.length ; i ++)
			{
				effectList[i] = Integer.parseInt(strs[i]);
			}
			return true;
		}
		else 
		{
			return super.setVariable(key, value);
		}
	}
	
}
