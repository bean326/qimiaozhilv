package cc.lv1.rpg.gs.entity.impl.pet;

import vin.rabbit.util.Utils;
import cc.lv1.rpg.gs.entity.RPGameObject;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.ext.Bag;
import cc.lv1.rpg.gs.entity.impl.battle.skill.PetSkill;
import cc.lv1.rpg.gs.other.ErrorCode;
import cc.lv1.rpg.gs.util.FontConver;

public class PetSkillStudy extends RPGameObject
{
	/** 对应PetSkill里的order */
	public int skillType;
	public int skillLevel;
	/** 需要道具(从第二次开始学习都需要扣除的) */
	public int needPoint;
	public int needMoney;
	public int[] needGoodsIds;
	public int[] needGoodsCounts;
	public int needTime;
	public int needPetLevel;
	/** 认证道具(第一次学习的时候需要扣除) */
	public int[] proofGoodsIds;
	public int[] proofGoodsCounts;
	public int proofPoint;
	/** 学习这一级技能需要多少经验才能学会 */
	public int needExp;
	/** 需要宠物职业 */
	public int[] needPetJob;
	
	
	public boolean isConditionEnough(PlayerController target,Pet pet,PetSkill skill)
	{
		Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
		boolean isRight = false;
		for (int i = 0; i < needPetJob.length; i++) 
		{
			if(needPetJob[i] == pet.job)
				isRight = true;
		}
		if(!isRight)
		{
			target.sendAlert(ErrorCode.ALERT_PET_JOB_IS_ERROR);
			return false;
		}
		if(skill.exp == 0)
		{
			for (int i = 0; i < proofGoodsIds.length; i++)
			{
				if(bag.getGoodsCount(proofGoodsIds[i]) < proofGoodsCounts[i])
				{
					target.sendAlert(ErrorCode.ALERT_NOT_RENZHENG_GOODS);
					return false;
				}
			}
			if(bag.point < proofPoint)
			{
				target.sendAlert(ErrorCode.ALERT_SHOP_NO_POINT);
				return false;
			}
		}
		else
		{
			if(bag.point < needPoint)
			{
				target.sendAlert(ErrorCode.ALERT_SHOP_NO_POINT);
				return false;
			}
			if(bag.money < needMoney)
			{
				target.sendAlert(ErrorCode.ALERT_SHOP_NO_MONEY);
				return false;
			}
			for (int i = 0; i < needGoodsIds.length; i++)
			{
				if(bag.getGoodsCount(needGoodsIds[i]) < needGoodsCounts[i])
				{
					target.sendAlert(ErrorCode.ALERT_NOT_GOODS_STUDYSKILL);
					return false;
				}
			}
		}
		boolean isJob = false;
		for (int i = 0; i < needPetJob.length; i++)
		{
			if(needPetJob[i] != 0 && needPetJob[i] == pet.job)
			{
				isJob = true;
				break;
			}
		}
		if(!isJob)
		{
			return false;
		}
		if(needPetJob[0] == pet.job)
		{
			if(pet.level < needPetLevel)
			{
				target.sendAlert(ErrorCode.ALERT_PET_LEVEL_ERROR);
				return false;
			}
		}
		return true;
	}

	public void setSkillStudyCondition(PlayerController target,Pet pet,PetSkill skill)
	{
		Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
		if(skill.exp == 0)
		{
			for (int i = 0; i < proofGoodsIds.length; i++)
			{
				bag.deleteGoods(target, proofGoodsIds[i], proofGoodsCounts[i]);
			}
			bag.point -= proofPoint;
		}
		else
		{
			for (int i = 0; i < needGoodsIds.length; i++)
			{
				bag.deleteGoods(target, needGoodsIds[i], needGoodsCounts[i]);
			}
			bag.point -= needPoint;
			bag.money -= needMoney;
		}
		bag.sendAddGoods(target, null);
	}
	
	public boolean setVariable(String key,String value)
	{
		if(key.equals("skillLevel"))
		{
			skillLevel = Integer.parseInt(value);
			return true;
		}
		else if(key.equals("needPetJob"))
		{
			String[] strs = Utils.split(value, ":");
			needPetJob = new int[strs.length];
			for (int i = 0; i < strs.length; i++) 
			{
				needPetJob[i] = Integer.parseInt(strs[i]);
			}
			return true;
		}
		else if(key.equals("needGoods"))
		{
			if("0".equals(value))
			{
				needGoodsIds = new int[0];
				needGoodsCounts = new int[0];
				return true;
			}
			String[] strs = Utils.split(value, "|");
			needGoodsIds = new int[strs.length];
			needGoodsCounts = new int[strs.length];
			for (int i = 0; i < strs.length; i++) 
			{
				String[] goods = Utils.split(strs[i], ":");
				needGoodsIds[i] = Integer.parseInt(goods[0]);
				needGoodsCounts[i] = Integer.parseInt(goods[1]);
			}
			return true;
		}
		else if(key.equals("proofGoods"))
		{
			if("0".equals(value))
			{
				proofGoodsIds = new int[0];
				proofGoodsCounts = new int[0];
				return true;
			}
			String[] strs = Utils.split(value, "|");
			proofGoodsIds = new int[strs.length];
			proofGoodsCounts = new int[strs.length];
			for (int i = 0; i < strs.length; i++) 
			{
				String[] goods = Utils.split(strs[i], ":");
				proofGoodsIds[i] = Integer.parseInt(goods[0]);
				proofGoodsCounts[i] = Integer.parseInt(goods[1]);
			}
			return true;
		}
		else
			return super.setVariable(key, value);
	}
}
