package cc.lv1.rpg.gs.entity.impl.battle.skill;

import vin.rabbit.comm.GameObject;
import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.Map;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.data.PetExp;

public class PetSkill extends Skill 
{
	/** 技能每天最大的学习次数 */
	public static final int MAXSTUDYCOUNT = 12;
	/** 学习次数达到这个次数后就需要用元宝才能学习 */
	public static final int MONEYCOUNT = 6;
	/** 需要的元宝数量 */
	public static final int STUDYMOENY = 2;
	/**
	 * 对应宠物状态(职业)
	 * */
	public int [] petJob;
	
	/** 需要宠物类型 (0.无 1.攻击型 2.防御型) */
	public int petType;

	/** 技能学习了多少次 */
	public int studyCount;
	
	/** 技能类型(用来判断技能学习) */
	public int skillType;
	
	/** 技能经验 */
	public int exp;
	
	/** 是否正在学习中 */
	public boolean isStudying = false;
	
	/** 能否在战斗中发动 */
	public boolean isActive = false;
	
	/** VIP状态(0.普通 1.VIP) */
	public int isVIP;

	public void copyTo(GameObject gameobject)
	{
		super.copyTo(gameobject);
		PetSkill skill = (PetSkill)gameobject;
		skill.petType = petType;
		skill.studyCount = studyCount;
		skill.skillType = skillType;
		skill.exp = exp;
		skill.isActive = isActive;
		skill.isVIP = isVIP;
		
		int[] tmp = new int[petJob.length];
		System.arraycopy(petJob, 0 , tmp, 0, tmp.length);
		skill.petJob = tmp;
	}
	
	public boolean setVariable(String key,String value)
	{
		if(key.equals("petJob"))
		{
			String [] strs = Utils.split(value, ":");
			petJob = new int[strs.length];
			for(int i = 0 ; i < petJob.length ; i ++)
			{
				petJob[i] = Integer.parseInt(strs[i]);
			}
			return true;
		}
		else 
		{
			return super.setVariable(key, value);
		}
	}
	
	public int getRewardExp()
	{
		int result = 0;
		if(studyCount == 0)
			result = 0;
		if(studyCount%6 == 1)
			result = 100;
		else if(studyCount%6 == 2)
			result = 110;
		else if(studyCount%6 == 3)
			result = 120;
		else if(studyCount%6 == 4)
			result = 130;
		else if(studyCount%6 == 5)
			result = 140;
		else if(studyCount%6 == 0)
		{
			result = 150;
			if(Math.random() * 100 <= 20)
				result += 200;
		}
		return result;
	}
}
