package cc.lv1.rpg.gs.entity.impl;

import vin.rabbit.util.Utils;
import cc.lv1.rpg.gs.entity.RPGameObject;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.ext.Bag;
import cc.lv1.rpg.gs.entity.ext.SkillTome;
import cc.lv1.rpg.gs.entity.ext.TaskInfo;

public class UpRole extends RPGameObject
{
	/** 1.一转 2.二转 3.三转  11.宠物一转  12.宠物二转  13.宠物三转*/
	public int type;
	
	/** 转生需要等级 */
	public int needLevel;
	
	/** 职业
	 * (人物转生时:1.军官 2.运动员 3.护士 4.超能力) 
	 * (宠物转生时:1.2.3.4.)*/
	public int job;
	
	/** 需要道具 */
	public int[] needGoodsId;
	
	/** 需要道具数量 */
	public int[] needGoodsCount;
	
	/** 转生需要完成任务 */
	public int[] needTaskId;
	
	/** 转生后的等级上限 */
	public int overLevel;
	
	/** 奖励属性 */
	public String[] rewardType;
	
	/** 奖励属性值 */
	public int[] rewardValue;
	
	
	public int getPetRewardValue(String key)
	{
		if(type < 11)
			return 0;
		for (int i = 0; i < rewardType.length; i++) 
		{
			if(key.equals(rewardType[i]))
			{
				return rewardValue[i];
			}
		}
		return 0;
	}
	
	/**
	 * 设置转生的属性到玩家身上
	 * @param player
	 * @param type 1表示只设置几个基本属性(力量,精神,智慧,敏捷) 2表示设置其它属性
	 */
	public void setPlayerInfo(Player player,int type)
	{
		
		if(rewardType == null)
		{	
			System.out.println("UpRole setPlayerInfo rewardType or rewardValue is null!");
			return;
		}
		
		if(type == 1)
		{
			for (int i = 0; i < rewardType.length; i++) 
			{
				if(rewardType[i].equals("power") || rewardType[i].equals("nimble")
					  || rewardType[i].equals("spirit") || rewardType[i].equals("wisdom"))
				{
					String point = player.getBaseInfo().getVariable(rewardType[i]);
					int total = Integer.parseInt(point) + rewardValue[i];
					player.getBaseInfo().setVariable(rewardType[i], total+"");
				}
				else
					continue;
			}	
		}
		else if(type == 2)
		{
			for (int i = 0; i < rewardType.length; i++) 
			{
				if(rewardType[i].equals("power") || rewardType[i].equals("nimble")
						  || rewardType[i].equals("spirit") || rewardType[i].equals("wisdom"))
				{
					continue;
				}
				else
				{
					String point = player.getBaseInfo().getVariable(rewardType[i]);
					int total = Integer.parseInt(point) + rewardValue[i];
					player.getBaseInfo().setVariable(rewardType[i], total+"");
				}
			}	
		}
		
	}
	
	/**
	 * 检测玩家是否完成了需要完成的任务
	 * @param target
	 * @return
	 */
	public boolean isTaskFinish(PlayerController target)
	{
		if(needTaskId == null)
			return true;
		TaskInfo ti = (TaskInfo) target.getPlayer().getExtPlayerInfo("taskInfo");
		
		boolean result = true;
		for (int i = 0; i < needTaskId.length; i++)
		{
			if(needTaskId[i] == 0)
				continue;
			if(!ti.isTaskCompleted(needTaskId[i]))
			{
				result = false;
				break;
			}
		}
		return result;
	}
	
	public boolean isGoodsEnough(PlayerController target)
	{
		if(needGoodsId == null)
			return true;
		Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
		
		for (int i = 0; i < needGoodsId.length; i++)
		{
			if(needGoodsId[i] == 0)
				continue;
			int count = bag.getGoodsCount(needGoodsId[i]);
			if(count < needGoodsCount[i])
				return false;
		}
		return true;
	}
	
	public void removeGoods(PlayerController target)
	{
		if(needGoodsId == null)
			return;
		Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
		
		for (int i = 0; i < needGoodsId.length; i++)
		{
			if(needGoodsId[i] == 0)
				continue;
			bag.deleteGoods(target, needGoodsId[i], needGoodsCount[i]);
		}
	}
	
	
	public boolean setVariable(String key,String value)
	{
		if(key.equals("needGoodsId"))
		{
			String[] strs = Utils.split(value, ":");
			needGoodsId = new int[strs.length];
			for (int i = 0; i < strs.length; i++)
			{
				needGoodsId[i] = Integer.parseInt(strs[i]);
			}
			return true;
		}
		else if(key.equals("needGoodsCount"))
		{
			String[] strs = Utils.split(value, ":");
			needGoodsCount = new int[strs.length];
			for (int i = 0; i < strs.length; i++)
			{
				needGoodsCount[i] = Integer.parseInt(strs[i]);
			}
			return true;
		}
		else if(key.equals("needTaskId"))
		{
			String[] strs = Utils.split(value, ":");
			needTaskId = new int[strs.length];
			for (int i = 0; i < strs.length; i++)
			{
				needTaskId[i] = Integer.parseInt(strs[i]);
			}
			return true;
		}
	    else if(key.equals("rewardType"))
		{
			String[] strs = Utils.split(value, ":");
			rewardType = new String[strs.length];
			for (int i = 0; i < strs.length; i++)
			{
				rewardType[i] = strs[i];
				if(rewardType[i].equals("0"))
				{
					System.out.println("UpRole setVariable rewardType error!");
				}
			}
			return true;
		}
		else if(key.equals("rewardValue"))
		{
			String[] strs = Utils.split(value, ":");
			rewardValue = new int[strs.length];
			for (int i = 0; i < strs.length; i++)
			{
				rewardValue[i] = Integer.parseInt(strs[i]);
			}
			return true;
		}
		else
			return super.setVariable(key, value);
	}
	
}
