package cc.lv1.rpg.gs.entity.impl.goods;

import vin.rabbit.comm.GameObject;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.Utils;
import cc.lv1.rpg.gs.data.DC;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.ext.Bag;
import cc.lv1.rpg.gs.entity.ext.EquipSet;
import cc.lv1.rpg.gs.entity.ext.PetTome;
import cc.lv1.rpg.gs.entity.ext.SkillTome;
import cc.lv1.rpg.gs.entity.impl.Goods;
import cc.lv1.rpg.gs.entity.impl.Player;
import cc.lv1.rpg.gs.entity.impl.UpRole;
import cc.lv1.rpg.gs.entity.impl.battle.skill.PassiveSkill;
import cc.lv1.rpg.gs.entity.impl.battle.skill.PetPassiveSkill;
import cc.lv1.rpg.gs.entity.impl.pet.Pet;
import cc.lv1.rpg.gs.net.SMsg;
import cc.lv1.rpg.gs.other.ErrorCode;
import cc.lv1.rpg.gs.util.FontConver;

/**
 * 宠物装备
 * @author bean
 *
 */
public class GoodsPetEquip extends Goods
{
	/** 其它属性 */
	public int otherValue;
	
	/** 装备位置 */
	public int equipLocation;
	
	/** 职业需求 (......) */
	public String job;
	
	/** 是否VIP */
	public boolean isVIP;
	
	/** 精炼系数 */
	public int growPoint;

    /** 物理攻击 */
	public int phyAtt;
	
	/** 精神攻击 */
	public int sptAtt;
	
	/** 物理防御 (万分比)*/
	public int phyDef;
	
	/** 精神防御 (万分比)*/
	public int sptDef;
	
	/** 力量 */
	public int power;
	
	/** 敏捷 */
	public int agility;
	
	/** 精神 */
	public int spirit;
	
	/** 智慧 */ 
	public int wisdom;
	
	/** 生命 */
	public int lifePoint;
	
	/** 精力 */
	public int magicPoint;
	
	/** 忽视防御的物理伤害 */
	public int noDefPhyHurt;
	
	/** 忽视防御的精神伤害 */
	public int noDefSptHurt;
	
	/** 物理免伤 */
	public int phyHurtAvoid;
	
	/** 精神免伤 */
	public int sptHurtAvoid;
	
	/** 物理爆击率 */
	public int phySmiteRate;
	
	/** 精神爆击率 */
	public int sptSmiteRate;
	
	/** 物理爆击参数 */
	public int phySmiteParm;
	
	/** 精神爆击参数 */
	public int sptSmiteParm;
	
	/** 治疗值 */
	public int curePoint;
	
	/** 抗物理暴击 */
	public int clearPhySmite;
	
	/** 抗精神暴击 */
	public int clearSptSmite;
	
	/** 抗物理暴击倍率(参数) */
	public int clearPhySmiteParm;
	
	/** 抗精神暴击倍率(参数) */
	public int clearSptSmiteParm;
	
	/** 抗忽视伤害  */
	public int clearNoDefHurt;
	
	/** 炼化属性 格式(power:10|wisdom:10)(炼化的属性)*/
	public String extAtt;
	
	/** 力量附加 */
	public StringBuffer powerExt = new StringBuffer();
	
	/** 敏捷附加 */
	public StringBuffer agilityExt = new StringBuffer();
	
	/** 精神附加 */
	public StringBuffer spiritExt = new StringBuffer();
	
	/** 智慧附加 */ 
	public StringBuffer wisdomExt = new StringBuffer();
	
	/** 生命附加 */
	public StringBuffer lifePointExt = new StringBuffer();
	
	/** 精力附加 */
	public StringBuffer magicPointExt = new StringBuffer();
	
	/** 忽视防御的物理伤害附加 */
	public StringBuffer noDefPhyHurtExt = new StringBuffer();
	
	/** 忽视防御的精神伤害附加 */
	public StringBuffer noDefSptHurtExt = new StringBuffer();
	
	/** 物理免伤附加 */
	public StringBuffer phyHurtAvoidExt = new StringBuffer();
	
	/** 精神免伤附加 */
	public StringBuffer sptHurtAvoidExt = new StringBuffer();
	
	/** 物理爆击率附加 */
	public StringBuffer phySmiteRateExt = new StringBuffer();
	
	/** 精神爆击率附加 */
	public StringBuffer sptSmiteRateExt = new StringBuffer();
	
	/** 物理爆击参数附加 */
	public StringBuffer phySmiteParmExt = new StringBuffer();
	
	/** 精神爆击参数附加 */
	public StringBuffer sptSmiteParmExt = new StringBuffer();
	
	/** 治疗值附加 */
	public StringBuffer curePointExt = new StringBuffer();
	
	/** 物理攻击附加 */
	public StringBuffer phyAttExt = new StringBuffer();
	
	/** 精神攻击附加 */
	public StringBuffer sptAttExt = new StringBuffer();
	
	/** 物理防御附加 (万分比)*/
	public StringBuffer phyDefExt = new StringBuffer();
	
	/** 精神防御附加 (万分比)*/
	public StringBuffer sptDefExt = new StringBuffer();
	
	/** 抗物理暴击附加 */
	public StringBuffer clearPhySmiteExt = new StringBuffer();
	
	/** 抗精神暴击附加 */
	public StringBuffer clearSptSmiteExt = new StringBuffer();
	
	/** 抗物理暴击倍率(参数)附加 */
	public StringBuffer clearPhySmiteParmExt = new StringBuffer();
	
	/** 抗精神暴击倍率(参数)附加 */
	public StringBuffer clearSptSmiteParmExt = new StringBuffer();
	
	/** 抗忽视伤害附加 */
	public StringBuffer clearNoDefHurtExt = new StringBuffer();
	
	public void copyTo(GameObject go)
	{
		super.copyTo(go);
		GoodsPetEquip goods = (GoodsPetEquip) go;
		
		goods.equipLocation = equipLocation;
		goods.job = job;
		goods.otherValue = otherValue;
		goods.isVIP = isVIP;
		goods.growPoint = growPoint;
		
		goods.phyAtt = phyAtt;
		goods.sptAtt = sptAtt;
		goods.phyDef = phyDef;
		goods.sptDef = sptDef; 
		goods.curePoint = curePoint;
		goods.lifePoint = lifePoint;
		goods.magicPoint = magicPoint;
		goods.noDefPhyHurt = noDefPhyHurt;
		goods.noDefSptHurt = noDefSptHurt;
		goods.phyHurtAvoid = phyHurtAvoid;
		goods.sptHurtAvoid = sptHurtAvoid;
		goods.phySmiteParm = phySmiteParm;
		goods.phySmiteRate = phySmiteRate;
		goods.power = power;
		goods.wisdom = wisdom;
		goods.agility = agility;
		goods.spirit = spirit;
		goods.sptSmiteRate = sptSmiteRate;
		goods.sptSmiteParm = sptSmiteParm;
		goods.clearPhySmiteParm = clearPhySmiteParm;
		goods.clearSptSmiteParm = clearSptSmiteParm;
		goods.clearPhySmite = clearPhySmite;
		goods.clearSptSmite = clearSptSmite;
		goods.clearNoDefHurt = clearNoDefHurt;

		goods.extAtt = extAtt;
		
		goods.phyAttExt = new StringBuffer(phyAttExt);
		goods.sptAttExt = new StringBuffer(sptAttExt);
		goods.phyDefExt = new StringBuffer(phyDefExt);
		goods.sptDefExt = new StringBuffer(sptDefExt);
		goods.lifePointExt = new StringBuffer(lifePointExt);
		goods.magicPointExt = new StringBuffer(magicPointExt);
		goods.phyHurtAvoidExt = new StringBuffer(phyHurtAvoidExt);
		goods.sptHurtAvoidExt = new StringBuffer(sptHurtAvoidExt);
		goods.phySmiteParmExt = new StringBuffer(phySmiteParmExt);
		goods.sptSmiteParmExt = new StringBuffer(sptSmiteParmExt);
		goods.phySmiteRateExt = new StringBuffer(phySmiteRateExt);
		goods.sptSmiteRateExt = new StringBuffer(sptSmiteRateExt);
		goods.noDefPhyHurtExt = new StringBuffer(noDefPhyHurtExt);
		goods.noDefSptHurtExt = new StringBuffer(noDefSptHurtExt);
		goods.powerExt = new StringBuffer(powerExt);
		goods.wisdomExt = new StringBuffer(wisdomExt);
		goods.agilityExt = new StringBuffer(agilityExt);
		goods.spiritExt = new StringBuffer(spiritExt);
		goods.curePointExt = new StringBuffer(curePointExt);
		goods.clearNoDefHurtExt = new StringBuffer(clearNoDefHurtExt);
		goods.clearSptSmiteExt = new StringBuffer(clearSptSmiteExt);
		goods.clearPhySmiteExt = new StringBuffer(clearPhySmiteExt);
		goods.clearSptSmiteParmExt = new StringBuffer(clearSptSmiteParmExt);
		goods.clearPhySmiteParmExt = new StringBuffer(clearPhySmiteParmExt);
	}
	
	public void writeTo(ByteBuffer buffer)
	{
		super.writeTo(buffer);
		buffer.writeInt(equipLocation);
		buffer.writeUTF(job);
		buffer.writeBoolean(isVIP);
		buffer.writeUTF(extAtt.toString());
		
		int count = 0;
		ByteBuffer out = new ByteBuffer();
		String[] ss = {"curePoint","lifePoint","magicPoint","noDefPhyHurt","noDefSptHurt",
				"phyHurtAvoid","sptHurtAvoid","phySmiteParm","phySmiteRate","power","wisdom",
				"agility","spirit","sptSmiteRate","sptSmiteParm","clearNoDefHurt",
				"clearPhySmiteParm","clearSptSmiteParm","clearPhySmite","clearSptSmite",
				"phyAtt","sptAtt","phyDef","sptDef"};
		for (int i = 0; i < ss.length; i++)
		{
			int value = getDefaultAtt(ss[i]);
			if(value > 0)
			{
				out.writeUTF(ss[i]);
				out.writeInt(value);
				count++;
			}
		}
		buffer.writeInt(count);
		if(count > 0)
			buffer.writeBytes(out.getBytes());
	}
	
	public void loadFrom(ByteBuffer buffer)
	{
		id = buffer.readInt();
		objectIndex = buffer.readLong();
		extAtt = buffer.readUTF();
		otherValue = buffer.readInt();
	}
	
	public void saveTo(ByteBuffer buffer)
	{
		buffer.writeInt(id);
		buffer.writeLong(objectIndex);
		buffer.writeUTF(extAtt);
		buffer.writeInt(otherValue);
	}
	
	/**
	 * 穿装备时检测职业(守护的职业)
	 * @return true or false
	 */
	public boolean isPetUseEquip(PlayerController target,Pet pet)
	{
		if(pet.level < level)//宠物等级
		{
			target.sendAlert(ErrorCode.ALERT_GOODS_LEVEL_LOW);
			return false;
		}
		String[] strs = job.split(":");

		for (int i = 0; i < strs.length; i++)
		{
			if(Integer.parseInt(strs[i]) == pet.job)
				return true;
		}
		target.sendAlert(ErrorCode.ALERT_PROFESSION_ERROR);
		return false;
	}
	
	public static final int LIANHUABAOSHICOUNT = 50;
	public static final int LIANHUASHASHICOUNT = 1;
	/**
	 * 炼化
	 */
	public boolean extAttUp(PlayerController target)
	{		
		Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
		if(isVIP)
		{
			 Goods uGoods = bag.getGoodsByType(Goods.EQUIPUPOTHER);
			 if(uGoods == null)
			 {
				 target.sendAlert(ErrorCode.ALERT_NOT_LIANHUABAOSHI);//炼化宝石
				 return false;
			 }
			 else
			 {
				 if(uGoods.goodsCount < LIANHUABAOSHICOUNT)
				 {
					 target.sendAlert(ErrorCode.ALERT_NOT_LIANHUABAOSHI);//炼化宝石
					 return false;
				 }
				 bag.removeGoods(target, uGoods.objectIndex, LIANHUABAOSHICOUNT);
			 }
		 }
		 else
		 {
			 int needGoods = 1045000346;
			 Goods uGoods = bag.getGoodsById(needGoods);
			 if(uGoods == null)
			 {
				 target.sendAlert(ErrorCode.ALERT_NOT_LIANHUASHASHI);//炼化砂石
				 return false;
			 }
			 else
			 {
				 if(uGoods.goodsCount < LIANHUASHASHICOUNT)
				 {
					 target.sendAlert(ErrorCode.ALERT_NOT_LIANHUABAOSHI);//炼化宝石
					 return false;
				 }
				 bag.removeGoods(target, uGoods.objectIndex, LIANHUASHASHICOUNT);
			 }
		 }
		if(extAtt.isEmpty())
			return false;
		StringBuffer sb = new StringBuffer();
		String[] strs = Utils.split(extAtt, "|");
		for (int i = 0; i < strs.length; i++)
		{
			if(strs[i].indexOf("growPoint") == -1)
			{
				String[] atts = Utils.split(strs[i], ":");
				String pointStr = getVariable(atts[0]+"Ext");
				String[] values = Utils.split(pointStr, ":");
				int min = Integer.parseInt(values[0]);
				int max = Integer.parseInt(values[1]);
				int point = (int) (Math.random() * (max - min) + min);
				sb.append(atts[0]);
				sb.append(":");
				sb.append(point);
			}
			else
			{
				sb.append(strs[i]);
			}
			sb.append("|");
		}
		extAtt = sb.toString().substring(0, sb.toString().length()-1);
		return true;
	}

	private boolean isSuccessGrowUp()
	{
		double random = Math.random() * 100;
		if(growPoint == 0)
			return true;
		else if(growPoint == 1)
			return random <= 95;
		else if(growPoint == 2)
			return random <= 88;
		else if(growPoint == 3)
			return random <= 78;
		else if(growPoint == 4)
			return random <= 73;
		else if(growPoint == 5)
			return random <= 60.1;
		else if(growPoint == 6)
			return random <= 51.7;
		else if(growPoint == 7)
			return random <= 39.4;
		else if(growPoint == 8)
			return random <= 31.9;
		else if(growPoint == 9)
			return random <= 26;
		else if(growPoint == 10)
			return random <= 18.7;
		else if(growPoint == 11)
			return random <= 15.3;
		else if(growPoint == 12)
			return random <= 11.6;
		else if(growPoint == 13)
			return random <= 8.1;
		else if(growPoint == 14)
			return random <= 4.8;
		return false;
	}
	/**
	 * 精炼
	 */
	public boolean growUp(PlayerController target)
	{/*
		精练指再次根据装备的基础属性，附加一定的属性，
		普通精练：精炼一次消耗1个精炼宝石（绑定，100金币一个）1-6精炼失败减一级，从7开始失败直接变回加1的属性
		vip精练：精炼一次消耗1个VIP精炼宝石（绑定，8元宝一个），没有VIP精炼宝石再精炼的，直接扣除8元宝。*/
		if(growPoint == 15)
			return false;
		Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
		if(isVIP)
		{
			Goods uGoods = bag.getGoodsById(1045000348);
			if(uGoods == null)
			{
				target.sendAlert(ErrorCode.ALERT_NOT_JINGLIANBAOSHI);
				return false;
			}
			else
			{
				bag.removeGoods(target, uGoods.objectIndex, 1);
			}
		}
		else
		{
			Goods uGoods = bag.getGoodsById(1045000347);
			if(uGoods == null)
			{
				target.sendAlert(ErrorCode.ALERT_NOT_JINGLIANSHASHI);
				return false;
			}
			bag.removeGoods(target, uGoods.objectIndex, 1);
		}
		int bGrow = growPoint;
		if(isSuccessGrowUp())
		{
			growPoint += 1;
		}
		else
		{
			if(growPoint <= 7)
			{
				growPoint -= 1;
			}
			else
			{
				growPoint = 1;
			}
		}
		if(growPoint >= 9)
		{//恭喜***（玩家名字）把****（守护装备名字）精炼到了+？ 
			target.sendGetGoodsInfo(3, true, DC.getPetEquipUpString(target, this));
		}
		if(target.getPlayer().isVipPlayer)
		{
			DataFactory.getInstance().addVipPlayerInfo(target, DC.getPetEquipUpInfoString(name, bGrow, growPoint));
		}
		setGrowPointToExt();
		return true;
	}
	

	private void setGrowPointToExt()
	{
		if(extAtt.isEmpty())
			extAtt = "growPoint:"+growPoint;
		else
		{
			if(extAtt.indexOf("growPoint") != -1)
			{
				String[] strs = Utils.split(extAtt, "|");
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < strs.length; i++) 
				{
					String[] atts = Utils.split(strs[i], ":");
					if("growPoint".equals(atts[0]))
					{
						strs[i] = "growPoint:"+growPoint;
					}
					sb.append(strs[i]);
					sb.append("|");
				}
				extAtt = sb.substring(0, sb.length()-1);
			}
			else
				extAtt += "|growPoint:"+growPoint;
		}
	}
	
	@Override
	public void onDeleteImpl(PlayerController target) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onRemoveImpl(PlayerController target)
	{
		PetTome pt = (PetTome) target.getPlayer().getExtPlayerInfo("petTome");
		Pet pet = pt.getActiveBattlePet();
		if(pet == null)
			return false;
		
		Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
		if(bag.isBagFull())
			return false;
		
		setEquipValue(target,0);
		
		useFlag = false;
		pet.removeEquip(this);
		bag.sendAddGoods(target, this);
		
		setEquipValue(target,1);
		target.fixPlayerBaseInfo(1);
		
		pet.sendBattlePetInfo(target, 1,target.getID());
		return true;
	}

	@Override
	public boolean onUseGoodsBattle(PlayerController player) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onUseImpl(PlayerController target) 
	{
		PetTome pt = (PetTome) target.getPlayer().getExtPlayerInfo("petTome");
		Pet pet = pt.getActiveBattlePet();
		if(pet == null)
			return false;
		if(!isPetUseEquip(target,pet))
			return false;
		
		setEquipValue(target,0);
		
		Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
		GoodsPetEquip goods = pet.getEquipByLocation(equipLocation);
		bag.removeGoods(target, objectIndex, 1);
		if(goods != null)
		{
			bag.sendAddGoods(target, goods);
			pet.removeEquip(goods);
		}
		useFlag = true;
		pet.addEquip(this);
		
		setEquipValue(target,1);
		
		pet.sendBattlePetInfo(target, 1,target.getID());
		target.fixPlayerBaseInfo(1);
		
		ByteBuffer buffer = new ByteBuffer();
		buffer.writeByte(9);
		writeTo(buffer);
		EquipSet equipSet = (EquipSet) target.getPlayer().getExtPlayerInfo("equipSet");
		equipSet.writeTo(buffer);
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_PLAYER_BATTLE_PET_BASE_COMMAND,buffer));

		return true;
	}
	
	private void setEquipValue(PlayerController target, int type)
	{
		SkillTome st = (SkillTome) target.getPlayer().getExtPlayerInfo("skillTome");
		PassiveSkill[] ps = st.getPassiveSkills();
		for (int i = 0; i < ps.length; i++) 
		{
			if(ps[i] == null)
				continue;
			ps[i].setEquipValue(target.getPlayer(),type);
		}
//		PetTome pt = (PetTome) target.getPlayer().getExtPlayerInfo("petTome");
//		Pet pet = pt.getActiveBattlePet();
//		if(pet == null)
//			return;
//		PetPassiveSkill[] pps = pet.getPetPassiveSkills();
//		for (int i = 0; i < pet.getPetPassiveSkills().length; i++)
//		{
//			if(pps[i] == null)
//				continue;
//			pps[i].setEquipValue(pet, target.getPlayer(), type);
//		}
	}
	
	public int getTotalAtt(String key)
	{
		int result = getDefaultAtt(key) + getExtAttPoint(key);
		if(key.equals("lifePoint"))
		    result += 5 * getDefaultAtt("power") + 5 * getExtAttPoint("power");
	    else if(key.equals("magicPoint"))
			result += 5 * getDefaultAtt("spirit") + 5 * getExtAttPoint("spirit");
//	    else if(key.equals("curePoint"))
//			result += (int) ((getDefaultAtt("nimble")+getExtAttPoint("nimble")+getExtAttPoint("wisdom")+getDefaultAtt("wisdom"))*10000/(Math.sqrt(level)*500));	
		else if(key.equals("phyAtt"))
			result += (int) (1.5 * getDefaultAtt("power") + 1.5 * getExtAttPoint("power"));
		else if(key.equals("sptAtt"))
			result += (int) (1.5 * getDefaultAtt("spirit") + 1.5 * getExtAttPoint("spirit"));
		else if(key.equals("phyHurtAvoid"))
			result += getDefaultAtt("nimble") + getExtAttPoint("nimble");
		else if(key.equals("sptHurtAvoid"))
			result += getDefaultAtt("wisdom") + getExtAttPoint("wisdom");
		 return result;
	}
	
	private int getDefaultAtt(String key)
	{
		int result = 0;
		if(key.equals("maxHitPoint") || key.equals("battleMaxHP") || key.equals("lifePoint") || key.equals("life"))
		    	result = lifePoint;
	    else if(key.equals("maxMagicPoint") || key.equals("battleMaxMP") || key.equals("magicPoint") || key.equals("magic"))
			result = magicPoint;
		else if(key.equals("power"))
			result = power;
		else if(key.equals("agility") || key.equals("nimble"))
			result = agility;
		else if(key.equals("spirit"))
			result = spirit;
		else if(key.equals("wisdom"))
			result = wisdom;
		else if(key.equals("curePoint"))
			result = curePoint;	
		else if(key.equals("phyAtt") || key.equals("phsAtt"))
			result = phyAtt;
		else if(key.equals("sptAtt"))
			result = sptAtt;
		else if(key.equals("phyDef") || key.equals("phsDef"))
			result = phyDef;
		else if(key.equals("sptDef"))
			result = sptDef;
		else if(key.equals("phySmiteRate") || key.equals("phsSmiteRate"))
			result = phySmiteRate;
		else if(key.equals("sptSmiteRate"))
			result = sptSmiteRate;
		else if(key.equals("phyHurtAvoid") || key.equals("phsHurtAvoid"))
			result = phyHurtAvoid;
		else if(key.equals("sptHurtAvoid"))
			result = sptHurtAvoid;
		else if(key.equals("noDefPhyHurt") || key.equals("noDefPhsHurt"))
			result = noDefPhyHurt;
		else if(key.equals("noDefSptHurt"))
			result = noDefSptHurt;
		else if(key.equals("phySmiteHurtParm") || key.equals("phsSmiteHurtParm") || key.equals("phySmiteParm") || key.equals("phsSmiteParm"))
			result = phySmiteParm;
		else if(key.equals("sptSmiteHurtParm") || key.equals("sptSmiteParm"))
			result = sptSmiteParm;
		else if(key.equals("clearPhySmite") || key.equals("clearPhsSmite"))
			result = clearPhySmite;
		else if(key.equals("clearSptSmite"))
			result = clearSptSmite;
		else if(key.equals("clearPhySmiteParm") || key.equals("clearPhsSmiteParm"))
			result = clearPhySmiteParm;
		else if(key.equals("clearSptSmiteParm"))
			result = clearSptSmiteParm;
		else if(key.equals("clearNoDefHurt"))
			result = clearNoDefHurt;
		return result;
	}
	
	private int getExtAttPoint(String key)
	{
		 int result = 0;
		 if(!extAtt.toString().isEmpty())
		 {
			 String[] strs = Utils.split(extAtt.toString(), "|");
			 for (int i = 0; i < strs.length; i++) 
			 {
				 String[] atts = Utils.split(strs[i], ":");
				 if(key.equals(atts[0]))
					 result += Integer.parseInt(atts[1]);
			 }
			 result *=  getMultByGrow();
		 }
		 return result;
	}


	public void setExtAtt(String str)
	{
		this.extAtt = str;
		if(extAtt.indexOf("growPoint") != -1)
		{
			String[] strs = Utils.split(extAtt, "|");
			for (int i = 0; i < strs.length; i++) 
			{
				if(strs[i].indexOf("growPoint") != -1)
				{
					growPoint = Integer.parseInt(Utils.split(strs[i], ":")[1]);
					break;
				}
			}
		}
	}
	
	/**
	 * 根据精炼系数获取倍数
	 * @return
	 */
	public double getMultByGrow()
	{
		if(growPoint == 0)
			return 1.0;
		else if(growPoint == 1)
			return 1.5;
		else if(growPoint == 2)
			return 2.0;
		else if(growPoint == 3)
			return 2.5;
		else if(growPoint == 4)
			return 3.5;
		else if(growPoint == 5)
			return 5.0;
		else if(growPoint == 6)
			return 7.5;
		else if(growPoint == 7)
			return 13.0;
		else if(growPoint == 8)
			return 21.0;
		else if(growPoint == 9)
			return 35.0;
		else if(growPoint == 10)
			return 56.0;
		else if(growPoint == 11)
			return 89.0;
		else if(growPoint == 12)
			return 140.0;
		else if(growPoint == 13)
			return 220.0;
		else if(growPoint == 14)
			return 310.0;
		else if(growPoint == 15)
			return 440.0;
		else 
			return 1.0;
	}
	
	public boolean setVariable(String key,String value)
	{
		if(key.equals("isVIP"))
		{
			isVIP = (value.equals("1"));
			return true;
		}
		else if(key.equals("extAtt"))
		{
			if("0".equals(value))
				extAtt = "";
			else
				extAtt = value;
			return true;
		}
		else if(key.equals("phyAttExt"))
		{
			phyAttExt.append(value);
			return true;
		}
		else if(key.equals("sptAttExt"))
		{
			sptAttExt.append(value);
			return true;
		}
		else if(key.equals("phyDefExt"))
		{
			phyDefExt.append(value);
			return true;
		}
		else if(key.equals("sptDefExt"))
		{
			sptDefExt.append(value);
			return true;
		}
		else if(key.equals("lifePointExt"))
		{
			lifePointExt.append(value);
			return true;
		}
		else if(key.equals("magicPointExt"))
		{
			magicPointExt.append(value);
			return true;
		}
		else if(key.equals("phyHurtAvoidExt"))
		{
			phyHurtAvoidExt.append(value);
			return true;
		}
		else if(key.equals("sptHurtAvoidExt"))
		{
			sptHurtAvoidExt.append(value);
			return true;
		}
		else if(key.equals("phySmiteParmExt"))
		{
			phySmiteParmExt.append(value);
			return true;
		}
		else if(key.equals("sptSmiteParmExt"))
		{
			sptSmiteParmExt.append(value);
			return true;
		}
		else if(key.equals("phySmiteRateExt"))
		{
			phySmiteRateExt.append(value);
			return true;
		}
		else if(key.equals("sptSmiteRateExt"))
		{
			sptSmiteRateExt.append(value);
			return true;
		}
		else if(key.equals("powerExt"))
		{
			powerExt.append(value);
			return true;
		}
		else if(key.equals("wisdomExt"))
		{
			wisdomExt.append(value);
			return true;
		}
		else if(key.equals("agilityExt"))
		{
			agilityExt.append(value);
			return true;
		}
		else if(key.equals("spiritExt"))
		{
			spiritExt.append(value);
			return true;
		}
		else if(key.equals("curePointExt"))
		{
			curePointExt.append(value);
			return true;
		}
		else if(key.equals("noDefPhyHurtExt"))
		{
			noDefPhyHurtExt.append(value);
			return true;
		}	
		else if(key.equals("noDefSptHurtExt"))
		{
			noDefSptHurtExt.append(value);
			return true;
		}	
		else if(key.equals("clearPhySmiteExt"))
		{
			clearPhySmiteExt.append(value);
			return true;
		}	
		else if(key.equals("clearSptSmiteExt"))
		{
			clearSptSmiteExt.append(value);
			return true;
		}	
		else if(key.equals("clearPhySmiteParmExt"))
		{
			clearPhySmiteParmExt.append(value);
			return true;
		}	
		else if(key.equals("clearSptSmiteParmExt"))
		{
			clearSptSmiteParmExt.append(value);
			return true;
		}	
		else if(key.equals("clearNoDefHurtExt"))
		{
			clearNoDefHurtExt.append(value);
			return true;
		}	
		else 
		{
			return super.setVariable(key, value);
		}
	}

	public String getVariable(String key)
	{
		if(key.equals("maxHitPoint") || key.equals("lifePoint") || key.equals("life"))
			return lifePoint+"";
	    else if(key.equals("maxMagicPoint") || key.equals("magicPoint") || key.equals("magic"))
	    	return magicPoint+"";
		else if(key.equals("power"))
			return power+"";
		else if(key.equals("agility") || key.equals("nimble"))
			return agility+"";
		else if(key.equals("spirit"))
			return spirit+"";
		else if(key.equals("wisdom"))
			return wisdom+"";
		else if(key.equals("curePoint"))
			return curePoint+"";	
		else if(key.equals("phyAtt") || key.equals("phsAtt"))
			return phyAtt+"";
		else if(key.equals("sptAtt"))
			return sptAtt+"";
		else if(key.equals("phyDef") || key.equals("phsDef"))
			return phyDef+"";
		else if(key.equals("sptDef"))
			return sptDef+"";
		else if(key.equals("phySmiteRate") || key.equals("phsSmiteRate"))
			return phySmiteRate+"";
		else if(key.equals("sptSmiteRate"))
			return sptSmiteRate+"";
		else if(key.equals("phyHurtAvoid") || key.equals("phsHurtAvoid"))
			return phyHurtAvoid+"";
		else if(key.equals("sptHurtAvoid"))
			return sptHurtAvoid+"";
		else if(key.equals("noDefPhyHurt") || key.equals("noDefPhsHurt"))
			return noDefPhyHurt+"";
		else if(key.equals("noDefSptHurt"))
			return noDefSptHurt+"";
		else if(key.equals("phySmiteHurtParm") || key.equals("phsSmiteHurtParm") || key.equals("phySmiteParm") || key.equals("phsSmiteParm"))
			return phySmiteParm+"";
		else if(key.equals("sptSmiteHurtParm") || key.equals("sptSmiteParm"))
			return sptSmiteParm+"";
		else if(key.equals("clearPhySmite") || key.equals("clearPhsSmite"))
			return clearPhySmite+"";
		else if(key.equals("clearSptSmite"))
			return clearSptSmite+"";
		else if(key.equals("clearPhySmiteParm") || key.equals("clearPhsSmiteParm"))
			return clearPhySmiteParm+"";
		else if(key.equals("clearSptSmiteParm"))
			return clearSptSmiteParm+"";
		else if(key.equals("clearNoDefHurt"))
			return clearNoDefHurt+"";
		else if(key.equals("phyAttExt"))
			return phyAttExt.toString();
		else if(key.equals("sptAttExt"))
			return sptAttExt.toString();
		else if(key.equals("phyDefExt"))
			return phyDefExt.toString();
		else if(key.equals("sptDefExt"))
			return sptDefExt.toString();
		else if(key.equals("lifePointExt"))
			return lifePointExt.toString();
		else if(key.equals("magicPointExt"))
			return magicPointExt.toString();
		else if(key.equals("phyHurtAvoidExt"))
			return phyHurtAvoidExt.toString();
		else if(key.equals("sptHurtAvoidExt"))
			return sptHurtAvoidExt.toString();
		else if(key.equals("phySmiteParmExt"))
			return phySmiteParmExt.toString();
		else if(key.equals("sptSmiteParmExt"))
			return sptSmiteParmExt.toString();
		else if(key.equals("phySmiteRateExt"))
			return phySmiteRateExt.toString();
		else if(key.equals("sptSmiteRateExt"))
			return sptSmiteRateExt.toString();
		else if(key.equals("powerExt"))
			return powerExt.toString();
		else if(key.equals("wisdomExt"))
			return wisdomExt.toString();
		else if(key.equals("agilityExt") || key.equals("nimbleExt"))
			return agilityExt.toString();
		else if(key.equals("spiritExt"))
			return spiritExt.toString();
		else if(key.equals("curePointExt"))
			return curePointExt.toString();
		else if(key.equals("noDefPhyHurtExt"))
			return noDefPhyHurtExt.toString();
		else if(key.equals("noDefSptHurtExt"))
			return noDefSptHurtExt.toString();
		else if(key.equals("clearPhySmiteExt"))
			return clearPhySmiteExt.toString();
		else if(key.equals("clearSptSmiteExt"))
			return clearSptSmiteExt.toString();
		else if(key.equals("clearPhySmiteParmExt"))
			return clearPhySmiteParmExt.toString();
		else if(key.equals("clearSptSmiteParmExt"))
			return clearSptSmiteParmExt.toString();
		else if(key.equals("clearNoDefHurtExt"))
			return clearNoDefHurtExt.toString();
		else 
			return super.getVariable(key);
	}
}
