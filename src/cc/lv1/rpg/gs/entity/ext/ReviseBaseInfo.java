package cc.lv1.rpg.gs.entity.ext;

import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.impl.battle.skill.PassiveSkill;
import cc.lv1.rpg.gs.entity.impl.pet.Pet;

public class ReviseBaseInfo
{
	public int maxHitPoint;
	
	public int maxMagicPoint;
	
	/** 力量 */
	public int power;

	/** 敏捷 */
	public int nimble;
	
	/** 精神 */
	public int spirit;
	
	/** 智慧 */
	public int wisdom;
	

	/** 速度 公共cd时间 */
	public int speed;
	
	
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
	public int phsSmiteRate;
	
	/** 精神爆击率 万分之 */
	public int sptSmiteRate;
	
	/** 精神抵抗率 万分之 */
	public int spiritStand;
	
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

	/** 攻击忽视防御() */
	private boolean noDefAtt = false;
	
	public int battleMaxHP = 0;
	
	public int battleMaxMP = 0;
	
	/** 增加精力的倍数 */
	public double mult;
	
	/** buff技能持续时间 */
	public int buffTime;

	/** 异能旋涡、体能摄取、异体爆破技能伤害 */
	public int skillDamage;

	/** 增加合成材料成功率 */
	public int syntSuccesRate;
	/** 得到经验加成的几率 */
	public int expAddRate;
	/** 具体经验加成比率 */
	public int addExp;

	public void copyTo(PlayerBaseInfo p)
	{
		this.power = p.power;
		this.wisdom = p.wisdom;
		this.spirit = p.spirit;
		this.nimble = p.nimble;
		this.phsAtt = p.phsAtt;
		this.phsDef = p.phsDef;
		this.sptAtt = p.sptAtt;
		this.sptDef = p.sptDef;
		this.curePoint = p.curePoint;
		
		this.phsSmiteRate = p.phsSmiteRate;
		this.sptSmiteRate = p.sptSmiteRate;
		this.noDefPhsHurt = p.noDefPhsHurt;
		this.noDefSptHurt = p.noDefSptHurt;
		this.phsHurtAvoid = p.phsHurtAvoid;
		this.sptHurtAvoid = p.sptHurtAvoid;
		this.phySmiteHurtParm = p.phySmiteHurtParm;
		this.sptSmiteHurtParm = p.sptSmiteHurtParm;
		
		
		this.spiritStand = p.spiritStand;
		this.hit = p.hit;
		this.avoidance = p.avoidance;
		this.noDefAtt = p.noDefAtt;
		this.battleMaxHP = p.battleMaxHP;
		this.battleMaxMP = p.battleMaxMP;
		this.mult = p.mult;
		this.buffTime = p.buffTime;
		this.skillDamage = p.skillDamage;
		this.syntSuccesRate = p.syntSuccesRate;
		this.expAddRate = p.expAddRate;
		this.addExp = p.addExp;
		
		this.maxHitPoint = p.getPlayer().maxHitPoint;
		this.maxMagicPoint = p.getPlayer().maxMagicPoint;
	}

	public void fix(PlayerController pc)
	{
		PlayerBaseInfo bi = pc.getPlayer().getBaseInfo();
		
		bi.power = this.power;
		bi.wisdom = this.wisdom;
		bi.spirit = this.spirit;
		bi.nimble = this.nimble;
		bi.phsAtt = this.phsAtt;
		bi.phsDef = this.phsDef;
		bi.sptAtt = this.sptAtt;
		bi.sptDef = this.sptDef;
		bi.curePoint = this.curePoint;
		
		bi.phsSmiteRate = this.phsSmiteRate;
		bi.sptSmiteRate = this.sptSmiteRate;
		bi.noDefPhsHurt = this.noDefPhsHurt;
		bi.noDefSptHurt = this.noDefSptHurt;
		bi.phsHurtAvoid = this.phsHurtAvoid;
		bi.sptHurtAvoid = this.sptHurtAvoid;
		bi.phySmiteHurtParm = this.phySmiteHurtParm;
		bi.sptSmiteHurtParm = this.sptSmiteHurtParm;
		
		
		bi.spiritStand = this.spiritStand;
		bi.hit = this.hit;
		bi.avoidance = this.avoidance;
		bi.noDefAtt = this.noDefAtt;
		bi.battleMaxHP = this.battleMaxHP;
		bi.battleMaxMP = this.battleMaxMP;
		bi.mult = this.mult;
		bi.buffTime = this.buffTime;
		bi.skillDamage = this.skillDamage;
		bi.syntSuccesRate = this.syntSuccesRate;
		bi.expAddRate = this.expAddRate;
		bi.addExp = this.addExp;
		
		bi.getPlayer().maxHitPoint = this.maxHitPoint;
		bi.getPlayer().maxMagicPoint = this.maxMagicPoint;
	}
	
	public void updateData(String key,int value,int level)
	{
		if(key.equals("life") || key.equals("maxHitPoint") || key.equals("lifePoint"))
		{
			maxHitPoint += value;
		}
		else if(key.equals("magic") || key.equals("maxMagicPoint") || key.equals("magicPoint"))
		{
			maxMagicPoint += value;
		}
		else if(key.equals("power"))
		{
			power += value;
			
			maxHitPoint += 5*value;
			phsAtt += (int) Math.round(1.5*value);
		}
		else if(key.equals("spirit"))
		{
			spirit += value;
			
			maxMagicPoint += 5 * value;
			sptAtt += Math.round(1.5 * value);
		}
		else if(key.equals("nimble"))
		{
			nimble += value;
			
			curePoint += (int) ((value*10000)/(Math.sqrt(level)*500));
		}
		else if(key.equals("wisdom"))
		{
			wisdom += value;
			
			curePoint += (int) ((value*10000)/(Math.sqrt(level)*500));
		}
		else if(key.equals("phsAtt") || key.equals("phyAtt"))
		{
			phsAtt += value;
		}
		else if(key.equals("sptAtt"))
		{
			sptAtt += value;
		}
		else if(key.equals("phsDef") || key.equals("phyDef"))
		{
			phsDef += value;
		}
		else if(key.equals("sptDef"))
		{
			sptDef += value;
		}
		else if(key.equals("phsSmiteRate") || key.equals("phySmiteRate"))
		{
			phsSmiteRate += value;
		}
		else if(key.equals("sptSmiteRate"))
		{
			sptSmiteRate += value;
		}
		else if(key.equals("clearPhySmite") || key.equals("clearPhsSmite"))
		{
			
		}
		else if(key.equals("clearSptSmite"))
		{
			
		}
		else if(key.equals("phySmiteHurtParm") || key.equals("phsSmiteHurtParm"))
		{
			phySmiteHurtParm += value;
		}
		else if(key.equals("sptSmiteHurtParm"))
		{
			sptSmiteHurtParm += value;
		}
	}

}
