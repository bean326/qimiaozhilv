package cc.lv1.rpg.gs.entity.impl.battle;

import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.impl.ArrayList;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.controller.BattleController;
import cc.lv1.rpg.gs.entity.controller.MonsterController;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.controller.SpriteController;
import cc.lv1.rpg.gs.entity.ext.BuffBox;
import cc.lv1.rpg.gs.entity.ext.EquipSet;
import cc.lv1.rpg.gs.entity.ext.OtherExtInfo;
import cc.lv1.rpg.gs.entity.ext.ReviseBaseInfo;
import cc.lv1.rpg.gs.entity.ext.PlayerBaseInfo;
import cc.lv1.rpg.gs.entity.impl.Goods;
import cc.lv1.rpg.gs.entity.impl.Player;
import cc.lv1.rpg.gs.entity.impl.battle.effect.Effect;
import cc.lv1.rpg.gs.entity.impl.battle.effect.FlashEffect;
import cc.lv1.rpg.gs.entity.impl.battle.effect.TimeEffect;
import cc.lv1.rpg.gs.entity.impl.battle.skill.ActiveSkill;
import cc.lv1.rpg.gs.net.SMsg;

/**
 * 玩家临时数据
 * @author dxw
 *
 */
public class PlayerBattleTmp extends SpriteBattleTmp
{
	/** 参与战斗的宠物 */
	private PetBattleTmp pet;
	
	/** 是否分配过物品 */
	public boolean isDisGoods = false;
	
	/** 是否正在使用技能 */
	public boolean isSkillUsing = false;
	
	/**
	 * 玩家获得的物品
	 */
	public Goods[] battleGoods = new Goods[0];
	
	/**
	 * 玩家获得的经验
	 */
	public long battleExp;
	
	/** 玩家额外获得的经验(帮忙关系加成) */
	public long maExp;
	
	/**
	 * 玩家获得的游戏币
	 */
	public long battlePoint;
	
	/**
	 * 获得的荣誉值
	 * @param room
	 */
	public int battleHonor;

	private PlayerController playerController;
	
	private Player player;

	public int maxHitPoint;
	public int maxMagicPoint;
	public int power;
	public int nimble;
	public int spirit;
	public int wisdom;
	public int phyAtt;
	public int sptAtt;
	public int phyDef;	
	public int sptDef;	
	public int curePoint;
	public int noDefPhyHurt;
	public int noDefSptHurt;
	public int phyHurtAvoid;
	public int sptHurtAvoid;
	public int phySmiteHurtParm;
	public int sptSmiteHurtParm;
	public int clearNoDefHurt;
	/** 忽视防御攻击 */
	public boolean noDefAtt;
	/** 物理爆击率 万分之 */
	public int phySmiteRate;
	/** 精神爆击率 万分之 */
	public int sptSmiteRate;
	/** 抗物理暴击 */
	public int clearPhySmite;
	/** 抗精神暴击 */
	public int clearSptSmite;
	/** 抗物理暴击倍率(参数)万分之 */
	public int clearPhySmiteParm;
	/** 抗精神暴击倍率(参数)万分之 */
	public int clearSptSmiteParm;
	/** 精神抵抗率 万分之 */
	public int spiritStand;
	/**物理减伤比*/
	public double phyHitRate;
	/** 精神减伤比*/
	public double sptHitRate;
	
	
	public void writeTo(ByteBuffer buffer)
	{
		player.writeTo(buffer);
		buffer.writeInt(maxHitPoint);//最大生命
		buffer.writeInt(maxMagicPoint);//最大精力
		buffer.writeInt(power);//力量
		buffer.writeInt(nimble);//敏捷
		buffer.writeInt(spirit);//精神
		buffer.writeInt(wisdom);//智慧
		buffer.writeInt(0);//体质
		buffer.writeInt(curePoint);//治疗值
		buffer.writeInt(phyAtt);//物理攻击
		buffer.writeInt(sptAtt);//精神攻击
		buffer.writeInt(phyDef);//物理防御
		buffer.writeInt(sptDef);//精神防御
		if(phySmiteRate > 10000)
			phySmiteRate = 10000;
		if(sptSmiteRate > 10000)
			sptSmiteRate = 10000;
		if(spiritStand > 10000)
			spiritStand = 10000;
		buffer.writeInt(phySmiteRate);//物理爆击率
		buffer.writeInt(sptSmiteRate);//精神爆击率
		buffer.writeInt(spiritStand);//精神抵抗
		buffer.writeInt(phyHurtAvoid);//物理免伤
		buffer.writeInt(sptHurtAvoid);//精神免伤
		buffer.writeInt(noDefPhyHurt);//忽视防御的物理伤害
		buffer.writeInt(noDefSptHurt);//忽视防御的精神伤害
		buffer.writeInt((int)(SpriteBattleTmp.SMITERATE*10000)+phySmiteHurtParm);//物理暴击伤害倍率
		buffer.writeInt((int)(SpriteBattleTmp.SMITERATE*10000)+sptSmiteHurtParm);//精神暴击伤害倍率
		buffer.writeInt(clearPhySmite);//抗物理暴击
		buffer.writeInt(clearSptSmite);//抗精神暴击
		buffer.writeInt(clearPhySmiteParm);//抗物理暴击倍率
		buffer.writeInt(clearSptSmiteParm);//抗物理暴击倍率
		buffer.writeInt(clearNoDefHurt);//抗忽视伤害
		
//		System.out.println("玩家："+player.name+" 的属性!");
//		System.out.println("最大生命:"+maxHitPoint);//最大生命
//		System.out.println("最大精力:"+maxMagicPoint);//最大精力
//		System.out.println("力量:"+baseInfo.power + "---" + getTotalAtt("power"));//力量
//		System.out.println("敏捷:"+baseInfo.nimble + "---" + getTotalAtt("agility"));//敏捷
//		System.out.println("精神:"+baseInfo.spirit + "---" + getTotalAtt("spirit"));//精神
//		System.out.println("智慧:"+baseInfo.wisdom + "---" + getTotalAtt("wisdom"));//智慧
//		System.out.println("体质:"+baseInfo.physique + "---" + getTotalAtt("physique"));//体质
//		System.out.println("治疗值:"+baseInfo.curePoint + "---" + getTotalAtt("curePoint"));//治疗值
//		System.out.println("物理攻击:"+phyAtt);//物理攻击
//		System.out.println("精神攻击:"+sptAtt);//精神攻击
//		System.out.println("物理防御:"+phyDef);//物理防御
//		System.out.println("精神防御:"+sptDef);//精神防御
//		System.out.println("物理爆击率:"+baseInfo.phsSmiteRate + "---" + getTotalAtt("phySmiteRate"));//物理爆击率
//		System.out.println("精神爆击率:"+baseInfo.sptSmiteRate + "---" + getTotalAtt("sptSmiteRate"));//精神爆击率
//		System.out.println("精神抵抗:"+baseInfo.spiritStand + "---" + getTotalAtt("spiritStand"));//精神抵抗
//		System.out.println("物理免伤:"+baseInfo.phsHurtAvoid + "---" + getTotalAtt("phyHurtAvoid"));//物理免伤
//		System.out.println("精神免伤:"+baseInfo.sptHurtAvoid + "---" + getTotalAtt("sptHurtAvoid"));//精神免伤
//		System.out.println("忽视防御的物理伤害:"+baseInfo.noDefPhsHurt + "---" + getTotalAtt("noDefPhyHurt"));//忽视防御的物理伤害
//		System.out.println("忽视防御的精神伤害:"+baseInfo.noDefSptHurt + "---" + getTotalAtt("noDefSptHurt"));//忽视防御的精神伤害
	}
	
	public void setPlayer(PlayerController playerController)
	{
		pet = new PetBattleTmp(playerController);
	
		setSpriteController(playerController);
		setSprite(playerController.getPlayer());
		
		this.playerController = playerController;
		this.player = playerController.getPlayer();
		baseInfo = playerController.getPlayer().getBaseInfo();
		playerController.getPlayer().getBaseInfo().copyTo(baseInfo);
		buffBox = (BuffBox)player.getExtPlayerInfo("buffBox");
		boolean flag = buffBox.updateBattleBuffBox(playerController);

		if(!flag)
		{
			setBattleBaseInfo();
		}

//		//测试打印********************************************************************************
//		System.out.println("---------------------------------------");
//		System.out.println("玩家 " +player.name+" 的战斗信息");
//		System.out.println("物理攻击 ..." + phyAtt);
//		System.out.println("精神攻击 ..." + sptAtt);
//		System.out.println("物理防御 ... "+ phyDef);
//		System.out.println("精神防御 ... "+ sptDef);
//		System.out.println("物理减伤比 ... "+ phyHitRate);
//		System.out.println("精神减伤比 ... "+ sptHitRate);
//		System.out.println("速度 ..." + pCdTimer);
//		System.out.println("---------------------------------------");
//		//测试打印********************************************************************************
	}
	
	
	public void setBattleBaseInfo()
	{
		EquipSet equipSet = (EquipSet) player.getExtPlayerInfo("equipSet");
		maxHitPoint = player.maxHitPoint + equipSet.getTotalAtt("maxHitPoint");
		maxMagicPoint = player.maxMagicPoint + equipSet.getTotalAtt("maxMagicPoint");
		power = baseInfo.power + equipSet.getTotalAtt("power");
		wisdom = baseInfo.wisdom + equipSet.getTotalAtt("wisdom");
		spirit = baseInfo.spirit + equipSet.getTotalAtt("spirit");
		nimble = baseInfo.nimble + equipSet.getTotalAtt("nimble");
		phyAtt = baseInfo.phsAtt + equipSet.getTotalAtt("phyAtt");
		sptAtt = baseInfo.sptAtt + equipSet.getTotalAtt("sptAtt");
		phyDef = baseInfo.phsDef + equipSet.getTotalAtt("phyDef");
		sptDef = baseInfo.sptDef + equipSet.getTotalAtt("sptDef");
		curePoint = baseInfo.curePoint + equipSet.getTotalAtt("curePoint");
		spiritStand = baseInfo.spiritStand + equipSet.getTotalAtt("spiritStand");
		phyHurtAvoid = baseInfo.phsHurtAvoid + equipSet.getTotalAtt("phyHurtAvoid");
		sptHurtAvoid = baseInfo.sptHurtAvoid + equipSet.getTotalAtt("sptHurtAvoid");
		noDefPhyHurt = baseInfo.noDefPhsHurt + equipSet.getTotalAtt("noDefPhyHurt");
		noDefSptHurt = baseInfo.noDefSptHurt + equipSet.getTotalAtt("noDefSptHurt");
		phySmiteHurtParm = baseInfo.phySmiteHurtParm + equipSet.getTotalAtt("phySmiteHurtParm");
		sptSmiteHurtParm = baseInfo.sptSmiteHurtParm + equipSet.getTotalAtt("sptSmiteHurtParm");
		phySmiteRate = baseInfo.phsSmiteRate + equipSet.getTotalAtt("phySmiteRate");
		sptSmiteRate = baseInfo.sptSmiteRate + equipSet.getTotalAtt("sptSmiteRate");
		clearPhySmite = baseInfo.clearPhySmite + equipSet.getTotalAtt("clearPhySmite");
		clearSptSmite = baseInfo.clearSptSmite + equipSet.getTotalAtt("clearSptSmite");
		clearPhySmiteParm = baseInfo.clearPhySmiteParm + equipSet.getTotalAtt("clearPhySmiteParm");
		clearSptSmiteParm = baseInfo.clearSptSmiteParm + equipSet.getTotalAtt("clearSptSmiteParm");
		clearNoDefHurt = baseInfo.clearNoDefHurt + equipSet.getTotalAtt("clearNoDefHurt");
		
		phyHitRate = (double)phyDef / 10000;
		sptHitRate = (double)sptDef / 10000;
		noDefAtt = false;
		pCdTimer = baseInfo.speed;
		if(phySmiteRate > 10000)
			phySmiteRate = 10000;
		if(sptSmiteRate > 10000)
			sptSmiteRate = 10000;
		if(spiritStand > 10000)
			spiritStand = 10000;
	}
	
	
	/**
	 * 删除BUFF时把BUFF按照时间排序，更新一次
	 */
	public void fixPlayerData()
	{
		ReviseBaseInfo rbi = player.getBaseInfo().getReviseBaseInfo();
		
		rbi.fix(playerController);
		
		setBattleBaseInfo();
		
		List list = new ArrayList();
		for (int i = 0; i < effectList.size(); i++) 
		{
			list.add(effectList.get(i));
		}
		BuffBox buffBox = (BuffBox) player.getExtPlayerInfo("buffBox");
		for (int i = 0; i < buffBox.getEffectList().size(); i++) 
		{
			list.add(buffBox.getEffectList().get(i));
		}
		//把效果BUFF从使用时间的先后排序
		for (int cur = 0; cur < list.size() - 1; cur++)
		{
			for (int next = cur + 1; next < list.size(); next++) 
			{
				TimeEffect effect1 = (TimeEffect)list.get(cur);
				TimeEffect effect2 = (TimeEffect)list.get(next);
				if (effect1.beginTime > effect2.beginTime) 
				{
					TimeEffect temp = effect1;
					list.set(cur, effect2);
					list.set(next, temp);
				}
			}
		}
		
		
		for (int k = 0; k < list.size(); k++) 
		{
			TimeEffect effect = (TimeEffect) list.get(k);
			
			int addPoint = 0,point = 0;
			for (int j = 0; j < effect.dataType.length; j++)
			{
				if(effect.dataType[j].equals("0"))
					break;
				if(effect.dataType[j].equals("noDefAtt"))
				{
					baseInfo.setNoDefAtt(true);
				}
				else if(effect.dataType[j].equals("chaos") || effect.dataType[j].equals("damageModify")
						|| effect.dataType[j].equals("timeEffectInvain") || effect.dataType[j].equals("dizzy"))
				{
					continue;
				}
				else if(effect.interval > 0)
					continue;
				else
				{
					int currPoint = Integer.parseInt(getVariable(effect.dataType[j]));

					if(effect.dataPattern[j] == 2)
						addPoint = (int) (currPoint * (double)effect.effectPoint[j] / 100);
					else
						addPoint = effect.effectPoint[j];
	
					addPoint += (double)addPoint * (double)effect.helpPoint / 10000;

					point = addPoint+currPoint;
					if(point < 0)
						point = 0;
					setVariable(effect.dataType[j], String.valueOf(point));
			
					updateValue(addPoint, effect.dataType[j]);
				}
			}
		}
		
		
	}
	
	
	/**
	 * 加减力量等相关值的时候 更新与其相关的数据
	 * @param addPoint
	 * @param dataType
	 */
	public void updateValue(int addPoint,String dataType)
	{
		if(dataType.equals("spirit"))
		{
			maxMagicPoint += 5 * addPoint;
			sptAtt += Math.round(1.5 * addPoint);
		}
		else if(dataType.equals("wisdom"))
		{
			curePoint += (int) ((addPoint*10000)/(Math.sqrt(player.level)*500));
			sptHurtAvoid += addPoint;
		}
		else if(dataType.equals("nimble"))
		{
			curePoint += (int) ((addPoint*10000)/(Math.sqrt(player.level)*500));
			phyHurtAvoid += addPoint;
		}
		else if(dataType.equals("power"))
		{
			maxHitPoint += 5*addPoint;
			phyAtt += (int) Math.round(1.5*addPoint);
		}
	}
	
	
	/**
	 * 取暴击率
	 * @param target 被攻击玩家
	 * @return 
	 */
	public int getSmiteRate(SpriteBattleTmp target)
	{
		int result = 0;
		int type = player.getJobType();
		if(type == 1)
			result = phySmiteRate;
		else if(type == 2)
			result = sptSmiteRate;
		if(target instanceof PlayerBattleTmp)
		{
			PlayerBattleTmp pbt = (PlayerBattleTmp) target;
			if(type == 1)
			{
				result -= pbt.clearPhySmite;
			}
			else if(type == 2)
			{
				result -= pbt.clearSptSmite;
			}
		}
		else if(target instanceof MonsterBattleTmp)
		{
			MonsterBattleTmp mbt = (MonsterBattleTmp) target;
			if(type == 1)
			{
				result -= mbt.getMonster().clearPhySmite;
			}
			else if(type == 2)
			{
				result -= mbt.getMonster().clearSptSmite;
			}
		}
		if(result > 10000)
			result = 10000;
		if(result < 0)
			result = 0;
		return result;
	}
	


	

	
	/**
	 * 取得玩家伤害 物理攻击
伤害计算公式：
物理伤害=攻击者物理攻击*（1-被攻击者物理减伤比）(20100326未修改版本)
物理伤害=攻击者物理攻击*（1-被攻击者物理减伤比）- 被攻击者物理减伤值(20100326修改后版本:涂阳)
物理伤害=攻击者物理攻击*（1-被攻击者物理减伤比）(20100330修改回原来版本:蓝旭)
	 */
	private int getDamagePointByHit(PlayerController target)
	{
		double nPhsHitRate = ((PlayerBattleTmp)target.getAttachment()).phyHitRate;
		if(nPhsHitRate > 0.99)
			nPhsHitRate = 0.99;
		if(nPhsHitRate < 0)
			nPhsHitRate = 0;
//		int nPhyHurtAvoid = ((PlayerBattleTmp)target.getAttachment()).phyHurtAvoid;
//		double damage = (double)phyAtt*(double)((double)1-nPhsHitRate);
//
//		if(damage <= nPhyHurtAvoid) 
//			return 0;
//		else
//			return  (int)(damage - nPhyHurtAvoid);
		return (int) (phyAtt * (1-nPhsHitRate));
	}
	
	/**
	 * 取得怪物伤害 物理攻击
	 */
	private int getDamagePointByHit(MonsterController monster)
	{
		double nPhsHitRate = monster.getMonster().phsHitRate;
		if(nPhsHitRate > 0.99)
			nPhsHitRate = 0.99;
		if(nPhsHitRate < 0)
			nPhsHitRate = 0;
		return getDamagePointByHit(nPhsHitRate,0);
	}
	
	/**
	 * 取得伤害 物理攻击
	 */
	private int getDamagePointByHit(double nPhsHitRate,int nPhsHurtAvoid)
	{
		double damage = (((double)phyAtt*(double)(((double)1)-nPhsHitRate))+ noDefPhyHurt);
		if(damage <= nPhsHurtAvoid)
			return 0;
		else 
			return (int) (damage - nPhsHurtAvoid);
	}
	/**
	 * 取得伤害 魔法攻击
	 * 精神伤害=攻击者精神攻击*（1-被攻击者精神减伤比）(20100326未修改版本)
	 * 精神伤害=攻击者精神攻击*（1-被攻击者精神减伤比）- 被攻击者精神减伤值(20100326修改后版本:涂阳)
	 * 精神伤害=攻击者精神攻击*（1-被攻击者精神减伤比）(20100330修改回原来版本:蓝旭)
	 */
	public int getDamagePointByMagic(PlayerController target)
	{
		double nSptHitRate = ((PlayerBattleTmp)target.getAttachment()).sptHitRate;
		if(nSptHitRate > 0.99)
			nSptHitRate = 0.99;
		if(nSptHitRate < 0)
			nSptHitRate = 0;
//		int nSptHurtAvoid = ((PlayerBattleTmp)target.getAttachment()).sptHurtAvoid;
//		double damage = (double)sptAtt*(double)((double)1-nSptHitRate);
//		if(damage <= nSptHurtAvoid)
//			return 0;
//		else
//			return (int)(damage  - nSptHurtAvoid);
		return (int) (sptAtt * (1-nSptHitRate));
	}
	/**
	 * 取得伤害 魔法攻击
	 */
	public int getDamagePointByMagic(MonsterController monster)
	{
		double nSptHitRate = monster.getMonster().sptHitRate;
		if(nSptHitRate > 0.8)
			nSptHitRate = 0.8;
		if(nSptHitRate < 0)
			nSptHitRate = 0;
		return getDamagePointByMagic(nSptHitRate,0);
	}
	/**
	 * 取得伤害 魔法攻击
	 */
	private int getDamagePointByMagic(double nSptHitRate,int nSptHurtAvoid)
	{
		double damage = sptAtt*(1-nSptHitRate) + noDefSptHurt;
		if(damage <= nSptHurtAvoid)
			return 0;
		else
			return (int)(damage - nSptHurtAvoid);
	}


	public void update(long timeMillis)
	{
		super.update(timeMillis);

		if(isDead())
			return;

		List list = buffBox.getEffectList();
		for(int j = 0 ; j < list.size() ; j ++)
		{
			TimeEffect effect = (TimeEffect)list.get(j);
			
			if(effect.interval != 0)
			{
				if(effect.isUseEffect(timeMillis))
				{
					processTimeEffectExt(effect);
				}
			}
			boolean flag = effect.isEndEffect(timeMillis);
	
			if(flag)
			{				
				if(effect.interval != 0)
				{
					processTimeEffectEndExt(effect);
				}
			}
		}

		for(int j = 0 ; j < list.size() ; j ++)
		{
			TimeEffect effect = (TimeEffect)list.get(j);

			boolean flag = effect.isEndEffect(timeMillis);
			
			if(flag)
			{				
				buffBox.removeEffect(effect,playerController);
			}
		}

		for(int j = 0 ; j < effectList.size() ; j ++)
		{
			TimeEffect effect = (TimeEffect)effectList.get(j);
			
			if(effect.interval != 0)
			{
				if(effect.isUseEffect(timeMillis))
				{
					processTimeEffectExt(effect);
				}
			}
			boolean flag = effect.isEndEffect(timeMillis);
			
			if(flag)
			{				
				if(effect.interval != 0)
				{
					processTimeEffectEndExt(effect);
				}
			}
		}

		for(int j = 0 ; j < effectList.size() ; j ++)
		{
			TimeEffect effect = (TimeEffect)effectList.get(j);

			boolean flag = effect.isEndEffect(timeMillis);
			
			if(flag)
			{			
				removeEffect(effect);
			}
		}	

		pet.update(timeMillis);
	}
	
	public void removeEffect(TimeEffect effect)
	{
		super.removeEffect(effect);

		fixPlayerData();

		if(effect.interval == 0)
			playerController.sendAlwaysValue();
	}
	
	

	/**
	 * 玩家战斗释放主动技能
	 * @param skill
	 */
	public boolean processActiveSkill(ActiveSkill skill)
	{	
		if(isBeforeUnusualEffect)
			checkUnusualEffect();
		
		ByteBuffer buffer = new ByteBuffer(64);
		buffer.writeByte(BattleController.SKILLPROCESSOR); //释放技能
		buffer.writeByte(getTeamNo());//使用技能者的组编号
		buffer.writeByte(getIndex()); //使用者技能者的index;
		buffer.writeInt(skill.id);
	
		boolean isLoverInTeam = false;
		if(playerController.getTeam() != null)
		{
			PlayerController[] players = playerController.getTeam().getPlayers();
			isLoverInTeam = playerController.isLoverInTeam(players);
		}

		int jobType = player.getJobType();
		
		if(isLoverInTeam)
		{
			OtherExtInfo oei = (OtherExtInfo) player.getExtPlayerInfo("otherExtInfo");
			if(oei.marryType == 1)
				buffer.writeByte(3);
			else if(oei.marryType == 2)
				buffer.writeByte(4);
			else
			{
				if(jobType == 1)
					buffer.writeByte(1);
				else
					buffer.writeByte(2);
			}
		}
		else
		{
			if(jobType == 1)
				buffer.writeByte(1);
			else
				buffer.writeByte(2);
		}
		
		//测试资源
		if(skill.effectRangeList == 0)
		{
			buffer.writeByte(0);//动作
		}
		else
		{
			if(skill.targetType[0] == 1 || skill.targetType[0] == 3)
				buffer.writeByte(3);
			else if(skill.targetType[0] == 2)
				buffer.writeByte(1);
			else 
				buffer.writeByte(3);
		}
		
		DataFactory dataFactory = DataFactory.getInstance();

		int skillLength = skill.effectList.length;
		
		for(int i = 0 ; i < skillLength ; i ++)
		{
			if(skill.effectList[i] == 0)
				continue;
			
			if(i == 1)
			{
				int random = (int) (Math.random() * 10000);
				if(skill.rate2 < random)
					continue;
			}
			if(i == 2)
			{
				int random = (int) (Math.random() * 10000);
				if(skill.rate3 < random)
					break;
			}

			Effect effect = (Effect)dataFactory.getGameObject(skill.effectList[i]);
			
			if(effect == null)
				continue;
			
			if(i != 0)
			{
				//防止效果施加到死了的怪以外的其他对象身上
				SpriteController sc = targetMonster != null ?targetMonster:targetPlayer;
				
				if(sc.getAttachment().isDead())
				{
					if(skill.effectRangeList == 1 && skill.targetType[i] == skill.targetType[0])
					{
						continue;
					}
				}
				
				if(targetMonster != null
				&&(skill.targetType[i] == ActiveSkill.TARGET_TYPE_TEAM_SELF
						|| skill.targetType[i] == ActiveSkill.TARGET_TYPE_ONESELF)) //先敌后己，如果第二次的效果是
				{
					setSkillTarget(playerController);
				}
				else if(targetPlayer != null
				&& (skill.targetType[i] == ActiveSkill.TARGET_TYPE_TEAM_SELF
						||skill.targetType[i] == ActiveSkill.TARGET_TYPE_ONESELF)
				&& skill.targetType[0] == ActiveSkill.TARGET_TYPE_TEAM_ENEMY) 
				{
					setSkillTarget(playerController);
				}
			}	
			
			int type = 0;

			if(effect instanceof FlashEffect)
			{
//				System.out.println(skill.name+ " PlayerBattleTmp 技能产生---瞬时效果 : "+effect.name+"  技能范围："+skill.effectRangeList);

				FlashEffect fe = (FlashEffect)effect;
				
				if(targetPlayer != null)
				{
					if(fe.type == 6)
					{
						if(!targetPlayer.getAttachment().isDead())
							return false;
					}
					else
					{
						if(targetPlayer.getAttachment().isDead())
							return false;
					}
				}

				onFlashEffectUse(fe);

				//打除了主目标以外的怪
				if(skill.effectRangeList != ActiveSkill.EFFECT_RANGE_ONE)
				{
		
					if(skill.targetType[i] == ActiveSkill.TARGET_TYPE_PLAYERS)
					{
						hitMorePlayerByEffect(effect, skill.effectRangeList);
					}
					else if(skill.targetType[i] == ActiveSkill.TARGET_TYPE_MONSTERS)
					{
						hitMoreMonsterByEffect(effect,skill.effectRangeList);
					}
				}
				type = 1;
			}
			else if(effect instanceof TimeEffect)
			{
//				System.out.println(skill.name+ " PlayerBattleTmp 技能产生---持续效果 : "+effect.name+"  目标范围:"+skill.effectRangeList);
				TimeEffect te = (TimeEffect)effect;
				
				onTimeEffectUse(te);
		
				//打除了主目标以外的怪
				if(skill.effectRangeList != ActiveSkill.EFFECT_RANGE_ONE)
				{
	
					if(skill.targetType[i] == ActiveSkill.TARGET_TYPE_PLAYERS)
					{
						hitMorePlayerByEffect(te, skill.effectRangeList);
					}
					else if(skill.targetType[i] == ActiveSkill.TARGET_TYPE_MONSTERS)
					{
						hitMoreMonsterByEffect(te,skill.effectRangeList);
					}
				}
				type = 2;
			}

			buffer.writeInt(effect.id);
			buffer.writeByte(type);
			pack(buffer);

		}
		
		player.setMagicPoint(-skill.magic,this);
		
		buffer.writeInt(player.magicPoint);

		battle.dispatchMsg(SMsg.S_BATTLE_ACTION_COMMAND, buffer);
		
		battle.checkDeadState();	
		
//		sendBattlePlayerInfo();
		
		return true;
	}
	
	
	/**
	  effect 百分比计算
	  (基本伤害+装备<值>)*装备<百分>*效果<百分>
	  
	  effect 值计算
	  (基本伤害+装备<值>+效果<值>)*装备<百分>
	  
	 */
	protected int getTotalDamageByMonster(FlashEffect effect)
	{
		int baseDamage = 0;
		if(player.profession == 1 || player.profession == 2)
		{
			baseDamage = getDamagePointByHit(targetMonster);
		}
		else if(player.profession == 3 || player.profession == 4)
		{
			baseDamage = getDamagePointByMagic(targetMonster);
		}
	
		int rdp = 0;
		if(effect.dataPattern == 1) //数值
		{
			rdp = -baseDamage + effect.effectPoint;
		}
		else if(effect.dataPattern == 2) //百分比
		{
			rdp = (int) (Math.abs(baseDamage) * (double)effect.effectPoint / 100);
		}
		
		if(rdp >= 0)
			rdp = -1;
		return rdp;
	}
	


	protected int getTotalDamageByPlayer(FlashEffect effect)
	{
		
		int baseDamage = 0;

		if(player.profession == 1 || player.profession == 2)
		{
			baseDamage = getDamagePointByHit(targetPlayer);
		}
		else if(player.profession == 3 || player.profession == 4)
		{
			baseDamage = getDamagePointByMagic(targetPlayer);
		}

		int rdp = 0;
		if(effect.dataPattern == 1) //数值
		{
			rdp = -baseDamage + effect.effectPoint;
		}
		else if(effect.dataPattern == 2) //百分比
		{
			rdp = (int) (Math.abs(baseDamage) * (double)effect.effectPoint / 100);
		}
		
		if(rdp >= 0)
			rdp = -1;
		
		return rdp;
	}
	
	
	
	
	
	public Player getPlayer()
	{
		return player;
	}
	
	public PlayerController getPlayerController()
	{
		return playerController;
	}
	
	
	
	public PlayerBaseInfo getBaseInfo()
	{
		return this.baseInfo;
	}
	
	
	public void setBattleGoods(Goods goods)
	{
		Goods[] sGoods = new Goods[battleGoods.length+1];
		for (int i = 0; i < battleGoods.length; i++) 
			sGoods[i] = battleGoods[i];
		sGoods[battleGoods.length] = goods;
		battleGoods = sGoods;
	}
	
	public void setBattleHonor(int honor)
	{
		battleHonor = honor;
	}
	
	public void setBattlePoint(long point)
	{
		battlePoint = point;
	}
	
	public void setBattleExp(long exp)
	{
		battleExp = exp;
	}
	
	public void sendBattleBaseInfo(ByteBuffer buffer)
	{
		buffer.writeInt(player.magicPoint);
		buffer.writeInt(maxHitPoint);
		buffer.writeInt(maxMagicPoint);
	}
	
	/**
	 * 发送中了效果后的值
	 * @param buffer
	 */
	public void sendBaseInfo(ByteBuffer buffer)
	{
		buffer.writeInt(player.hitPoint);
		buffer.writeInt(player.magicPoint);
		buffer.writeInt(maxHitPoint);
		buffer.writeInt(maxMagicPoint);
	}
	
	
	public String getVariable(String key)
	{
	    if(key.equals("noDefPhyHurt") || key.equals("noDefPhsHurt"))
		{
			return noDefPhyHurt + "";
		}
		else if(key.equals("phyHurtAvoid") || key.equals("phsHurtAvoid"))
		{
			return phyHurtAvoid + "";
		}
		else if(key.equals("phySmiteHurtParm") || key.equals("phsSmiteHurtParm"))
		{
			return phySmiteHurtParm + "";
		}
		else if(key.equals("phySmiteRate") || key.equals("phsSmiteRate"))
		{
			return phySmiteRate + "";
		}
		else if(key.equals("phsHitRate") || key.equals("phyHitRate"))
		{
			return phyHitRate + "";
		}
		else if(key.equals("phyDef") || key.equals("phsDef"))
		{
			return phyDef + "";
		}
		else if(key.equals("maxMagicPoint") || key.equals("battleMaxMP"))
		{
			return maxMagicPoint + "";
		}
		else if(key.equals("phsAtt") || key.equals("phyAtt"))
		{
			return phyAtt + "";
		}
		else if(key.equals("maxHitPoint") || key.equals("battleMaxHP"))
		{
			return maxHitPoint + "";
		}
		else if(key.equals("currentHP") || key.equals("hitPoint"))
		{
			return player.hitPoint + "";
		}
		else if(key.equals("currentMP") || key.equals("magicPoint"))
		{
			return player.magicPoint + "";
		}
		else
			return super.getVariable(key);
	}
	
	
	public boolean setVariable(String key,String value)
	{
	    if(key.equals("noDefPhyHurt") || key.equals("noDefPhsHurt"))
		{
	    	noDefPhyHurt = Integer.parseInt(value);
			return true;
		}
		else if(key.equals("phyHurtAvoid") || key.equals("phsHurtAvoid"))
		{
			phyHurtAvoid = Integer.parseInt(value);
			return true;
		}
		else if(key.equals("phySmiteHurtParm") || key.equals("phsSmiteHurtParm"))
		{
			phySmiteHurtParm = Integer.parseInt(value);
			return true;
		}
		else if(key.equals("phySmiteRate") || key.equals("phsSmiteRate"))
		{
			phySmiteRate = Integer.parseInt(value);
			return true;
		}
		else if(key.equals("phsHitRate") || key.equals("phyHitRate"))
		{
			phyHitRate = Integer.parseInt(value);
			return true;
		}
		else if(key.equals("phyDef") || key.equals("phsDef"))
		{
			phyDef = Integer.parseInt(value);
			return true;
		}
		else if(key.equals("maxMagicPoint") || key.equals("battleMaxMP"))
		{
			maxMagicPoint = Integer.parseInt(value);
			if(maxMagicPoint < player.magicPoint)
				player.magicPoint = maxMagicPoint;
			return true;
		}
		else if(key.equals("phsAtt") || key.equals("phyAtt"))
		{
			phyAtt = Integer.parseInt(value);
			return true;
		}
		else if(key.equals("maxHitPoint") || key.equals("battleMaxHP"))
		{
			maxHitPoint = Integer.parseInt(value);
			if(maxHitPoint < player.hitPoint)
				player.hitPoint = maxHitPoint;
			return true;
		}
		else if(key.equals("currentHP") || key.equals("hitPoint"))
		{
			player.hitPoint = Integer.parseInt(value);
			if(player.hitPoint > maxHitPoint)
				player.hitPoint = maxHitPoint;
			return true;
		}
		else if(key.equals("currentMP") || key.equals("magicPoint"))
		{
			player.magicPoint = Integer.parseInt(value);
			if(player.magicPoint > maxMagicPoint)
				player.magicPoint = maxMagicPoint;
			return true;
		}
		else
			return super.setVariable(key,value);
	}
	

}
