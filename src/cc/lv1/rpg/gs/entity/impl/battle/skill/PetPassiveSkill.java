package cc.lv1.rpg.gs.entity.impl.battle.skill;

import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.ext.EquipSet;
import cc.lv1.rpg.gs.entity.ext.PlayerBaseInfo;
import cc.lv1.rpg.gs.entity.ext.ReviseBaseInfo;
import cc.lv1.rpg.gs.entity.impl.Player;
import cc.lv1.rpg.gs.entity.impl.pet.Pet;
import vin.rabbit.comm.GameObject;
import vin.rabbit.util.Utils;

public class PetPassiveSkill extends PetSkill 
{
	/**
	 * 操作类型
	    1：力量
		2：体质
		3：敏捷
		4：生命
		5：精神
		6：物理攻击
		7：物理防御
		8：精神攻击
		9：精神防御
		10：物理暴击率
		11：精神暴击率
		12：物理暴击参数
		13：精神暴击参数
		14：公共CD时间
		15：蓝药
		16：所有buff技能持续时间
		17：智慧
		18：抗物理暴击
		19：抗精神暴击
		20: 物理免伤
		21: 精神免伤
		22: 经验转换
		23: 辅助值
        24: 免伤(物理，精神同时加)
        25: 抗暴击伤害(物理，精神同时加)
		26: 抗物理暴击伤害
		27: 抗精神暴击伤害
	 */
	public int [] dataType = new int[3];
	
	/**
	 * 数据类型
	 * 1：数值
		2：百分比
	 */
	public int [] dataPattern = new int[3];
	
	/**
	 * 效果值(守护不出加百分比的技能)
	 */
	public int [] effectPoint = new int[3];
	
	/**
	 * 效果目标
		1：自己
		2：己方全体
		3：敌方全体
	 */
	public int effectTarget;
	
	public void copyTo(GameObject gameObject)
	{
		super.copyTo(gameObject);
		
		PetPassiveSkill pSkill = (PetPassiveSkill)gameObject;
		
		int [] tmp = new int[3];
		System.arraycopy(dataType, 0, tmp, 0, tmp.length);
		pSkill.dataType = tmp;
		
		tmp = new int[3];
		System.arraycopy(dataPattern, 0, tmp, 0, tmp.length);
		pSkill.dataPattern = tmp;
		
		tmp = new int[3];
		System.arraycopy(effectPoint, 0, tmp, 0, tmp.length);
		pSkill.effectPoint = tmp;
	
		pSkill.effectTarget = effectTarget;
	}
	
	public boolean setVariable(String key,String value)
	{
		if(key.equals("dataType"))
		{
			String strs []  = Utils.split(value, ":");
			for(int i = 0 ; i < strs.length ; i ++)
			{
				dataType[i] = Integer.parseInt(strs[i]);
			}
			return true;
		}
		else if(key.equals("dataPattern"))
		{
			String strs [] = Utils.split(value, ":");
			for(int i = 0 ; i < strs.length ; i ++)
			{
				dataPattern[i] = Integer.parseInt(strs[i]);
			}
			return true;
		}
		else if(key.equals("effectPoint"))
		{
			String strs [] = Utils.split(value, ":");
			for(int i = 0 ; i < strs.length ; i ++)
			{
				effectPoint[i] = Integer.parseInt(strs[i]);
			}
			return true;
		}
		else
		{
			return super.setVariable(key, value);
		}
	}
	
	
//	/**
//	 * 学习被动技能后要设置属性
//	 * @param target 要学习的玩家
//	 */
//	public void setValue(Pet pet,Player player)
//	{
//		if(pet.isMaxBattlePoint())
//			return;
//		if(effectTarget != 1)
//			return;
//		if(dataType[0] == 22 && !isActive)
//			return;
//		for (int i = 0; i < dataType.length; i++) 
//		{
//			if(dataType[i] == 0)
//				break;
//			int beforeValue = getValueByType(player,dataType[i]);
//
//			setValue(player,dataPattern[i],effectPoint[i],dataType[i]);
//			
//			int afterValue = getValueByType(player,dataType[i]);
//			
//			ReviseBaseInfo rbi = player.getBaseInfo().getReviseBaseInfo();
//			if(rbi != null) 
//				rbi.updateData(getType(dataType[i]), afterValue-beforeValue,player.level);
//		}
//		setEquipValue(pet,player,1);
//	}
//	
//	/**
//	 * 
//	 * @param player
//	 * @param type  1为正，0为负
//	 */
//	public void setEquipValue(Pet pet,Player player,int type)
//	{
//		if(type == 1)
//		{
//			if(pet.isMaxBattlePoint())
//				return;
//		}
//		for (int i = 0; i < dataType.length; i++) 
//		{
//			if(dataType[i] == 0)
//				break;
//			if(effectTarget != 1)
//				break;
//			
//			int beforeValue = getValueByType(player,dataType[i]);
//			
//			setEquipValue(player,dataPattern[i],effectPoint[i],dataType[i],type);
//			
//			int afterValue = getValueByType(player,dataType[i]);
//			
//			ReviseBaseInfo rbi = player.getBaseInfo().getReviseBaseInfo();
//			if(rbi != null) 
//				rbi.updateData(getType(dataType[i]), afterValue-beforeValue,player.level);
//		}
//	}
//	
//	
//	private void setEquipValue(Player player,int dataPattern,int effectPoint,int dataType,int type)
//	{
//		if(dataPattern != 2 || dataType == 0)
//			return;
//		if(type == 0)
//			effectPoint = -effectPoint;
//		EquipSet es = (EquipSet) player.getExtPlayerInfo("equipSet");
//		PlayerBaseInfo base = player.getBaseInfo();
//		int addPoint = 0;
//		switch(dataType)
//		{
//			case 1: addPoint = (int) (es.getTotalAtt("power") * (double)effectPoint / 10000);
//					base.power += addPoint;
//					base.updateLifeValue(addPoint, "power");break;
//			case 2:	break;
//			case 3: addPoint = (int) (es.getTotalAtt("nimble") * (double)effectPoint / 10000);
//					base.nimble += addPoint;
//					base.updateLifeValue(addPoint, "nimble");break;
//			case 4: addPoint = (int) (es.getTotalAtt("maxHitPoint") * (double)effectPoint / 10000);
//					player.maxHitPoint += addPoint;break;
//			case 5: addPoint = (int) (es.getTotalAtt("spirit") * (double)effectPoint / 10000);
//					base.spirit += addPoint;
//					base.updateLifeValue(addPoint, "spirit");break;
//			case 6: base.phsAtt += es.getTotalAtt("phyAtt") * (double)effectPoint / 10000;break;
//			case 7: base.phsDef += es.getTotalAtt("phyDef") * (double)effectPoint / 10000;break;
//			case 8: base.sptAtt += es.getTotalAtt("sptAtt") * (double)effectPoint / 10000;break;
//			case 9: base.sptDef += es.getTotalAtt("sptDef") * (double)effectPoint / 10000;break;
//			case 10:base.phsSmiteRate += es.getTotalAtt("phySmiteRate") * (double)effectPoint / 10000;break;
//			case 11:base.sptSmiteRate += es.getTotalAtt("sptSmiteRate") * (double)effectPoint / 10000;break;
//			case 12:base.phySmiteHurtParm += es.getTotalAtt("phySmiteHurtParm") * (double)effectPoint / 10000;break;
//			case 13:base.sptSmiteHurtParm += es.getTotalAtt("spySmiteHurtParm") * (double)effectPoint / 10000;break;
//			case 14:break;
//			case 15:break;
//			case 16:break;
//			case 17:addPoint = (int) (es.getTotalAtt("wisdom") * (double)effectPoint / 10000);
//					base.wisdom += addPoint;
//					base.updateLifeValue(addPoint, "wisdom");break;
//			case 18:base.clearPhySmite += es.getTotalAtt("clearPhySmite") * (double)effectPoint / 10000;break;
//			case 19:base.clearSptSmite += es.getTotalAtt("clearSptSmite") * (double)effectPoint / 10000;break;
//			case 20:base.phsHurtAvoid += es.getTotalAtt("phsHurtAvoid") * (double)effectPoint / 10000;break;
//			case 21:base.sptHurtAvoid += es.getTotalAtt("sptHurtAvoid") * (double)effectPoint / 10000;break;
//			
//		}
//	}
//	
//	
//	private void setValue(Player player,int dataPattern,int effectPoint,int dataType)
//	{
//		PlayerBaseInfo base = player.getBaseInfo();
//		switch(dataType)
//		{
//			case 1: base.power += getValue(base.power,dataPattern,effectPoint);
//					base.updateLifeValue(getValue(base.power,dataPattern,effectPoint), "power");break;
//			case 2: break;
//			case 3: base.nimble += getValue(base.nimble,dataPattern,effectPoint);
//					base.updateLifeValue(getValue(base.nimble,dataPattern,effectPoint), "nimble");break;
//			case 4: player.maxHitPoint += getValue(player.maxHitPoint,dataPattern,effectPoint);break;
//			case 5: base.spirit += getValue(base.spirit,dataPattern,effectPoint);
//					base.updateLifeValue(getValue(base.spirit,dataPattern,effectPoint), "spirit");break;
//			case 6: base.phsAtt += getValue(base.phsAtt,dataPattern,effectPoint);break;
//			case 7: base.phsDef += getValue(base.phsDef,dataPattern,effectPoint);break;
//			case 8: base.sptAtt += getValue(base.sptAtt,dataPattern,effectPoint);break;
//			case 9: base.sptDef += getValue(base.sptDef,dataPattern,effectPoint);break;
//			case 10:base.phsSmiteRate += getValue(base.phsSmiteRate,dataPattern,effectPoint);break;
//			case 11:base.sptSmiteRate += getValue(base.sptSmiteRate,dataPattern,effectPoint);break;
//			case 12:base.phySmiteHurtParm += getValue(base.phySmiteHurtParm,dataPattern,effectPoint);break;
//			case 13:base.sptSmiteHurtParm += getValue(base.sptSmiteHurtParm,dataPattern,effectPoint);break;
//			case 14:base.speed += getValue(base.speed,dataPattern,effectPoint);break;
//			case 15:base.mult += getValue(base.mult,dataPattern,effectPoint);break;
//			case 16:base.buffTime += getValue(base.buffTime,dataPattern,effectPoint);break;
//			case 17:base.wisdom += getValue(base.wisdom,dataPattern,effectPoint);
//					base.updateLifeValue(getValue(base.wisdom,dataPattern,effectPoint), "wisdom");break;
//			case 18:base.clearPhySmite += getValue(base.clearPhySmite, dataPattern, effectPoint);break;
//			case 19:base.clearSptSmite += getValue(base.clearSptSmite, dataPattern, effectPoint);break;
//			case 20:base.phsHurtAvoid += getValue(base.phsHurtAvoid, dataPattern, effectPoint);break;
//			case 21:base.sptHurtAvoid += getValue(base.sptHurtAvoid, dataPattern, effectPoint);break;
//		}
//	}
//
//	private int getValueByType(Player player,int type)
//	{
//		PlayerBaseInfo base = player.getBaseInfo();
//		switch (type) 
//		{
//			case 1: return base.power;
//			case 3: return base.nimble;
//			case 4: return player.maxHitPoint;
//			case 5: return base.spirit; 
//			case 6: return base.phsAtt;
//			case 7: return base.phsDef;
//			case 8: return base.sptAtt;
//			case 9: return base.sptDef;
//			case 10:return base.phsSmiteRate;
//			case 11:return base.sptSmiteRate;
//			case 12:return base.phySmiteHurtParm;
//			case 13:return base.sptSmiteHurtParm;
//			case 17:return base.wisdom;
//			case 18:return base.clearPhySmite;
//			case 19:return base.clearSptSmite;
//			case 20:return base.phsHurtAvoid;
//			case 21:return base.sptHurtAvoid;
//			default:return 0;
//		}
//	}
//	
//	private String getType(int type)
//	{
//		switch (type) 
//		{
//			case 1: return "power";
//			case 3: return "nimble";
//			case 4: return "maxHitPoint";
//			case 5: return "spirit"; 
//			case 6: return "phsAtt";
//			case 7: return "phsDef";
//			case 8: return "sptAtt";
//			case 9: return "sptDef";
//			case 10:return "phsSmiteRate";
//			case 11:return "sptSmiteRate";
//			case 12:return "phySmiteHurtParm";
//			case 13:return "sptSmiteHurtParm";
//			case 17:return "wisdom";
//			case 18:return "clearPhySmite";
//			case 19:return "clearSptSmite";
//			case 20:return "phsHurtAvoid";
//			case 21:return "sptHurtAvoid";
//			default:return "0";
//		}
//	}
	
}
