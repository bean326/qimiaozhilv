package cc.lv1.rpg.gs.entity.ext;

import vin.rabbit.comm.GameObject;
import vin.rabbit.comm.i.IClientHandle;
import vin.rabbit.util.ByteBuffer;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.RPGameObject;
import cc.lv1.rpg.gs.entity.impl.Player;
import cc.lv1.rpg.gs.entity.impl.UpRole;
import cc.lv1.rpg.gs.entity.impl.battle.skill.PassiveSkill;
import cc.lv1.rpg.gs.entity.impl.battle.skill.PetActiveSkill;
import cc.lv1.rpg.gs.entity.impl.battle.skill.PetPassiveSkill;
import cc.lv1.rpg.gs.entity.impl.pet.Pet;

/**
 * 玩家基本资料
 * @author dxw
 *
 */
public class PlayerBaseInfo extends RPGameObject implements IClientHandle
{	
	
	/** 力量 */
	public int power;

	/** 敏捷 */
	public int nimble;
	
	/** 精神 */
	public int spirit;
	
	/** 智慧 */
	public int wisdom;
	

	/** 速度 公共cd时间 */
	public int speed = 4800;
	
	
	/** 物理攻击 */
	public int phsAtt;
	
	/** 精神攻击 */
	public int sptAtt;
	
	/** 物理防御 */
	public int phsDef;	
	
	/** 精神防御 */
	public int sptDef;	

	
	/** 治疗值(辅助值万分比) */
	public int curePoint;
	
	
	/** 命中 */
	public int hit;
	
	/** 闪避 */
	public int avoidance;
	
	
	
	/** 物理爆击率 万分之 */
	public int phsSmiteRate = 500;
	
	/** 精神爆击率 万分之 */
	public int sptSmiteRate = 500;
	
	/** 精神抵抗率 万分之 */
	public int spiritStand;
	
	/** 抗物理暴击 */
	public int clearPhySmite;
	
	/** 抗精神暴击 */
	public int clearSptSmite;
	
	/** 抗物理暴击倍率(参数) */
	public int clearPhySmiteParm;
	
	/** 抗精神暴击倍率(参数) */
	public int clearSptSmiteParm;
	
	
	/** 忽视防御物伤 */
	public int noDefPhsHurt;
	
	/** 忽视防御精伤 */
	public int noDefSptHurt;
	
	/** 物理免伤 */
	public int phsHurtAvoid;
	
	/** 精神免伤 */
	public int sptHurtAvoid;
	
	/** 物理爆击伤害参数 */
	public int phySmiteHurtParm;
	
	/** 精神爆击伤害参数 */
	public int sptSmiteHurtParm;
	
	/** 抗忽视伤害 */
	public int clearNoDefHurt;

	/** 攻击忽视防御() */
	public boolean noDefAtt = false;

	private Player player;
	
	
	public int battleMaxHP = 0;
	
	public int battleMaxMP = 0;
	/** 增加精力的倍数 */
	public double mult = 1;
	/** buff技能持续时间 */
	public int buffTime;
	/** 异能旋涡、体能摄取、异体爆破技能伤害 */
	public int skillDamage;
	/** 所有行为增加怪物对自己的仇恨 */
//	public int hatred;
	/** 增加合成材料成功率 */
	public int syntSuccesRate;
	/** 得到经验加成的几率 */
	public int expAddRate;
	/** 具体经验加成比率 */
	public int addExp;
	
	
	public PlayerBaseInfo(Player player)
	{
		this.player = player;
	}
	
	public Player getPlayer()
	{
		return this.player;
	}
	
	public void setNoDefAtt(boolean flag)
	{
		noDefAtt = flag;
	}
	
	public boolean isNoDefAtt()
	{
		return noDefAtt;
	}

	

	
	/**
	 * 升级和进入游戏的时候更新一下值
	 * type 1表示是升级 0表示是建立角色或则登陆游戏时
	 */
	public void updateBaseValue(int type)
	{
		UpRole ur = DataFactory.getInstance().getUpRoleByPlayer(player.getZhuanshengState(),player.profession);
		if(ur != null)
		{
			ur.setPlayerInfo(player, 1);
		}
		
		PetTome pt = (PetTome) player.getExtPlayerInfo("petTome");
		Pet pet = pt.getActivePet();
		if(pet != null)
		{
			pet.setPlayerBaseInfo(1);
		}
		
//		if(player.upProfession == 1)
		if(player.profession == 1)
		{
			/**
			 * 
			生命上限=5*力量+20*LV+150
			精力上限=5*精神+6*LV+90
			物理攻击=ROUND(1.5*力量,0)
			物理防御=ROUND(0.8*敏捷,0)+300
			精神攻击=ROUND(1.5*精神,0)
			精神防御=ROUND(4*智慧,0)
			辅助值=(敏捷+智慧)/(SQRT(LV)*500)
			 */
			player.maxHitPoint = 5*power + 20*player.level + 150;
			player.maxMagicPoint = 5*spirit + 6*player.level + 90;
			phsAtt = (int) Math.round(1.5*power)+80;
//			phsDef = (int) (Math.round(0.8*nimble)+300);
			sptAtt = (int) Math.round(1.5*spirit);
//			sptDef = (int) (Math.round(0.8*wisdom)+300);
			curePoint = (int) ((nimble+wisdom)*10000/(Math.sqrt(player.level)*500));
		}
//		else if(player.upProfession == 3)
		else if(player.profession == 2)
		{
			/**
			 * 生命上限=5*力量+11*LV+120
				精力上限=5*精神+6*LV+75
				物理攻击=ROUND(1.5*力量,0)
				物理防御=ROUND(0.8*敏捷,0)+300
				精神攻击=ROUND(1.5*精神,0)
				精神防御=ROUND(4*智慧,0)
				辅助值=(敏捷+智慧)/(SQRT(LV)*500)
			 */
			player.maxHitPoint = 5*power + 11*player.level + 120;
			player.maxMagicPoint = 5*spirit + 6*player.level + 75;
			phsAtt = (int) Math.round(1.5*power) + 100;
//			phsDef = (int) (Math.round(0.8*nimble)+300);
			sptAtt = (int) Math.round(1.5*spirit);
//			sptDef = (int) (Math.round(0.8*wisdom)+300);
			curePoint = (int) ((nimble+wisdom)*10000/(Math.sqrt(player.level)*500));
		}
//		else if(player.upProfession == 6)
		else if(player.profession == 3)
		{
			/**
			 * 生命上限=5*力量+13*LV+90
				精力上限=5*精神+6*LV+120
				物理攻击=ROUND(1.5*力量,0)
				物理防御=ROUND(0.8*敏捷,0)+300
				精神攻击=ROUND(1.5*精神,0)
				精神防御=ROUND(4*智慧,0)
				辅助值=(敏捷+智慧)/(SQRT(LV)*500)
			 */
			player.maxHitPoint = 5*power + 13*player.level + 90;
			player.maxMagicPoint = 5*spirit + 6*player.level + 120;
			phsAtt = (int) Math.round(1.5*power);
//			phsDef = (int) (Math.round(0.8*nimble)+300);
			sptAtt = (int) Math.round(1.5*spirit)+60;
//			sptDef = (int) (Math.round(0.8*wisdom)+300);
			curePoint = (int) ((nimble+wisdom)*10000/(Math.sqrt(player.level)*500));
		}
//		else if(player.upProfession == 7)
		else if(player.profession == 4)
		{
			/**
			 * 生命上限=ROUND(5*力量+9.5*LV,0)+70
				精力上限=5*精神+6*LV+150
				物理攻击=ROUND(1.5*力量,0)
				物理防御=ROUND(0.8*敏捷,0)+300
				精神攻击=ROUND(1.5*精神,0)
				精神防御=ROUND(4*智慧,0)
				辅助值=(敏捷+智慧)/(SQRT(LV)*500)
			 */
			player.maxHitPoint = (int) (Math.round(5*power + 9.5*player.level) + 70);
			player.maxMagicPoint = 5*spirit + 6*player.level + 150;
			phsAtt = (int) Math.round(1.5*power);
//			phsDef = (int) (Math.round(0.8*nimble)+300);
			sptAtt = (int) Math.round(1.5*spirit)+100;
//			sptDef = (int) (Math.round(0.8*wisdom)+300);
			curePoint = (int) ((nimble+wisdom)*10000/(Math.sqrt(player.level)*500));
		}
		
		if(pet != null)
		{
			pet.setPlayerOtherValue(1);
		}


		mult = 1;
		phsSmiteRate = 500;
		sptSmiteRate = 500;
		spiritStand = 0;
		noDefPhsHurt = 0;
		noDefSptHurt = 0;
		phsHurtAvoid = nimble;
		sptHurtAvoid = wisdom;
		phySmiteHurtParm = 0;
		sptSmiteHurtParm = 0;
		clearNoDefHurt = 0;
		battleMaxHP = 0;
		battleMaxMP = 0;
		hit = 0;
		avoidance = 0;
		clearPhySmiteParm = 0;
		clearSptSmiteParm = 0;
		phsDef = 0;
		sptDef = 0;
		
		if(player.profession == 1)
		{
			clearPhySmite = 300;
			clearSptSmite = 300;
		}
		else if(player.profession == 2)
		{
			clearPhySmite = 0;
			clearSptSmite = 0;
		}
		else if(player.profession == 3)
		{
			clearPhySmite = 300;
			clearSptSmite = 300;
		}
		else if(player.profession == 4)
		{
			clearPhySmite = 300;
			clearSptSmite = 300;
		}

		if(ur != null)
		{
			ur.setPlayerInfo(player, 2);
		}

		SkillTome st = (SkillTome) player.getExtPlayerInfo("skillTome");
		PassiveSkill[] ps = st.getPassiveSkills();
		for (int i = 0; i < ps.length; i++) 
		{
			if(ps[i] == null)
				continue;
			ps[i].setValue(player);
		}

//		pet = pt.getActiveBattlePet();
//		if(pet != null)
//		{
//			PetPassiveSkill[] pps = pet.getPetPassiveSkills();
//			for (int i = 0; i < pet.getPetPassiveSkills().length; i++)
//			{
//				if(pps[i] == null)
//					continue;
//				pps[i].setValue(pet, player);
//			}
//		}
		
		
		rbi = new ReviseBaseInfo();
		rbi.copyTo(this);
	}
	
	private ReviseBaseInfo rbi;
	
	/**
	 * 修正玩家角色
	 * @return
	 */
	public ReviseBaseInfo getReviseBaseInfo()
	{
		return rbi;
	}


	
	public void updateLifeValue(int addPoint,String dataType)
	{
		if(dataType.equals("spirit"))
		{
			player.maxMagicPoint += 5 * addPoint;
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
			phsHurtAvoid += addPoint;
		}
		else if(dataType.equals("power"))
		{
			player.maxHitPoint += 5*addPoint;
			phsAtt += (int) Math.round(1.5*addPoint);
		}
	}
	
	public void copyTo(GameObject gameobject)
	{
		PlayerBaseInfo pbi = (PlayerBaseInfo)gameobject;
		pbi.power = power;
		pbi.nimble = nimble;
		pbi.spirit = spirit;
		pbi.wisdom = wisdom;
		pbi.speed = speed;
		pbi.phsAtt = phsAtt;
		pbi.sptAtt = sptAtt;
		pbi.phsDef = phsDef;
		pbi.sptDef = sptDef;
		pbi.curePoint = curePoint;
		pbi.hit = hit;
		pbi.avoidance = avoidance;
		pbi.phsSmiteRate = phsSmiteRate;
		pbi.sptSmiteRate = sptSmiteRate;
		pbi.spiritStand = spiritStand;
		pbi.noDefPhsHurt = noDefPhsHurt;
		pbi.noDefSptHurt = noDefSptHurt;
		pbi.phsHurtAvoid = phsHurtAvoid;
		pbi.sptHurtAvoid = sptHurtAvoid;
		pbi.phySmiteHurtParm = phySmiteHurtParm;
		pbi.sptSmiteHurtParm = sptSmiteHurtParm;
		pbi.clearNoDefHurt = clearNoDefHurt;
		pbi.clearPhySmite = clearPhySmite;
		pbi.clearSptSmite = clearSptSmite;
		pbi.clearPhySmiteParm = clearPhySmiteParm;
		pbi.clearSptSmiteParm = clearSptSmiteParm;
		pbi.mult = mult;
		pbi.buffTime = buffTime;
//		pbi.hatred = hatred;
		pbi.skillDamage = skillDamage;
	}
	
	public void readFrom(ByteBuffer buffer)
	{

	}
	



	public void writeTo(ByteBuffer buffer)
	{
		buffer.writeInt(power);
		buffer.writeInt(nimble);
		buffer.writeInt(spirit);
		buffer.writeInt(wisdom);
		buffer.writeInt(0);//原来 是体质 ， 现在没有这个属性
		buffer.writeInt(speed);
		buffer.writeInt(phsAtt);
		buffer.writeInt(sptAtt);
		buffer.writeInt(phsDef);
		buffer.writeInt(sptDef);
		buffer.writeInt(curePoint);
		buffer.writeInt(hit);
		buffer.writeInt(avoidance);
		buffer.writeInt(phsSmiteRate);
		buffer.writeInt(sptSmiteRate);
		buffer.writeInt(spiritStand);
		buffer.writeInt(noDefPhsHurt);
		buffer.writeInt(noDefSptHurt);
		buffer.writeInt(phsHurtAvoid);
		buffer.writeInt(sptHurtAvoid);
		buffer.writeInt(phySmiteHurtParm);
		buffer.writeInt(sptSmiteHurtParm);
	}

	public String getVariable(String key)
	{
		if(key.equals("currentHP") || key.equals("hitPoint"))
		{
			return player.hitPoint+"";
		}
		else if(key.equals("currentMP") || key.equals("magicPoint"))
		{
			return player.magicPoint+"";
		}
		else if(key.equals("battleMaxHP") || key.equals("maxHitPoint"))
		{
			return player.maxHitPoint+"";
		}
		else if(key.equals("battleMaxMP") || key.equals("maxMagicPoint"))
		{
			return player.maxMagicPoint+"";
		}
		else if(key.equals("speed"))
		{
			return 0+"";
		}
		else if(key.equals("phyAtt") || key.equals("phsAtt"))
		{
			return phsAtt+"";
		}
		else if(key.equals("phyDef") || key.equals("phsDef"))
		{
			return phsDef+"";
		}
		else if(key.equals("phsHurtAvoid") || key.equals("phyHurtAvoid"))
		{
			return phsHurtAvoid+"";
		}
		else if(key.equals("noDefPhsHurt") || key.equals("noDefPhyHurt"))
		{
			return noDefPhsHurt+"";
		}
		else if(key.equals("phsSmiteRate") || key.equals("phySmiteRate"))
		{
			return phsSmiteRate+"";
		}
		else if(key.equals("phsSmiteHurtParm") || key.equals("phySmiteHurtParm"))
		{
			return phySmiteHurtParm+"";
		}
		else
		{
			return super.getVariable(key);
		}
	}
	
	public boolean setVariable(String key, String value)
	{
		if(key.equals("currentHP"))
		{
			return true;
		}
		else if(key.equals("currentMP"))
		{
			return true;
		}
		else if(key.equals("phsSmiteRate") || key.equals("phySmiteRate"))
		{
			phsSmiteRate = Integer.parseInt(value);
			if(phsSmiteRate > 10000)
				phsSmiteRate = 10000;
			return true;
		}
		else if(key.equals("spiritStand"))
		{
			spiritStand = Integer.parseInt(value);
			if(spiritStand > 10000)
				spiritStand = 10000;
			return true;
		}
		else if(key.equals("sptSmiteRate"))
		{
			sptSmiteRate = Integer.parseInt(value);
			if(sptSmiteRate > 10000)
				sptSmiteRate = 10000;
			return true;
		}
		else if(key.equals("battleMaxHP") || key.equals("maxHitPoint"))
		{
			player.maxHitPoint = Integer.parseInt(value);
			return true;
		}
		else if(key.equals("battleMaxMP") || key.equals("maxMagicPoint"))
		{
			player.maxMagicPoint = Integer.parseInt(value);
			return true;
		}
		else if(key.equals("speed"))
		{
			return true;
		}
		else if(key.equals("phyAtt") || key.equals("phsAtt"))
		{
			phsAtt = Integer.parseInt(value);
			return true;
		}
		else if(key.equals("phyDef") || key.equals("phsDef"))
		{
			phsDef = Integer.parseInt(value);
			return true;
		}
		else if(key.equals("phsHurtAvoid") || key.equals("phyHurtAvoid"))
		{
			phsHurtAvoid = Integer.parseInt(value);
			return true;
		}
		else if(key.equals("noDefPhsHurt") || key.equals("noDefPhyHurt"))
		{
			noDefPhsHurt = Integer.parseInt(value);
			return true;
		}
		else if(key.equals("phsSmiteRate") || key.equals("phySmiteRate"))
		{
			phsSmiteRate = Integer.parseInt(value);
			return true;
		}
		else if(key.equals("phsSmiteHurtParm") || key.equals("phySmiteHurtParm"))
		{
			phySmiteHurtParm = Integer.parseInt(value);
			return true;
		}
		else
		{
			return super.setVariable(key, value);
		}
	}
	

}
