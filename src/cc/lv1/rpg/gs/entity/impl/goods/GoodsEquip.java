package cc.lv1.rpg.gs.entity.impl.goods;



import vin.rabbit.comm.GameObject;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.Utils;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.ext.Bag;
import cc.lv1.rpg.gs.entity.ext.EquipSet;
import cc.lv1.rpg.gs.entity.ext.PetTome;
import cc.lv1.rpg.gs.entity.ext.SkillTome;
import cc.lv1.rpg.gs.entity.ext.TaskInfo;
import cc.lv1.rpg.gs.entity.impl.Goods;
import cc.lv1.rpg.gs.entity.impl.Player;
import cc.lv1.rpg.gs.entity.impl.battle.skill.PassiveSkill;
import cc.lv1.rpg.gs.entity.impl.battle.skill.PetPassiveSkill;
import cc.lv1.rpg.gs.entity.impl.battle.skill.Skill;
import cc.lv1.rpg.gs.entity.impl.pet.Pet;
import cc.lv1.rpg.gs.gui.MainFrame;
import cc.lv1.rpg.gs.net.SMsg;
import cc.lv1.rpg.gs.other.ErrorCode;

public class GoodsEquip extends Goods
{   
	/** 人物形象 */
	public int modelId; 
	
	/** 形象背景 */
	public int adornBackgroud;
	
	/**
	 * 装备星级
	 */
	public int startLevel;
	
	/** 是否是VIP装备 */
	public boolean isVIP;
	
	/** 追加了几项属性 */
	public int upNum;
	
	/**
	 * 以下是装备主属性
	 */
    /** 物理攻击 */
	public int phyAtt;
	
	/** 物理攻击成长 */
	public int phyAttRate;
	
	/** 精神攻击 */
	public int sptAtt;
	
	/** 精神攻击成长 */
	public int sptAttRate;
	
	/** 物理防御 (万分比)*/
	public int phyDef;
	
	/** 物理防御成长 */
	public int phyDefRate;
	
	/** 精神防御 (万分比)*/
	public int sptDef;
	
	/** 精神防御成长 */
	public int sptDefRate;
	
	/**
	 * 以下是装备附加属性
	 *
	 *装备位置 
	0：武器  1：头盔
	2：上衣  3：裤子
	4：鞋  5：护腕
	6：手套  7：项链
	8：戒指
	9：装饰
	10：角色形象
	11: 称号
	*/
	public int equipLocation;
	
	/** 武器类别(1.足球 2.篮球 3.手枪 4.火箭筒 5.手术刀 6.针筒 7.墨镜 8.扑克牌 9.神秘武器(通用)) */
	public int equipType;
	
	/** 装备初始等级 */
	public int equipLevel;
	
	/** 装备最大等级 */
	public int maxEquipLevel;
	
 	/** 职业需求 (1运动员,2士兵,3医护人员,4超能力者......) */
	public String job;
	
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
	
	/** 杀怪所得经验值增加 */
	public int killMonsterExp;
	
	/** 杀怪所得经验值增加% */
	public int killMonsterExpRate;
	public int skill;
	
	/** 白色装备修正值(主属性,用时要除以10000) */
	public int modValueWhite;
	
	/** 绿色装备修正值(主属性,用时要除以10000) */
	public int modValueGreen;
	
	/** 装备其它类型(0:普通装备 1结婚相关装备 2节日活动装备) */
	public int eType;
	
	/** 任务道具生成颜色 -1为不生成 */
	public int taskColor;
	
	/** 固定生成品质 */
	public int deQuality;
	
	/** 白色装备物理防御 */
	public int phyDefWhite;
	
	/** 绿色装备物理防御 */
	public int phyDefGreen;
	
	/** 蓝色装备物理防御 */
	public int phyDefBlue;
	
	/** 紫色装备物理防御 */
	public int phyDefPurple;
	
	/** 白色装备精神防御 */
	public int sptDefWhite;
	
	/** 绿色装备精神防御 */
	public int sptDefGreen;
	
	/** 蓝色装备精神防御 */
	public int sptDefBlue;
	
	/** 紫色装备精神防御 */
	public int sptDefPurple;
	
	public String attStr = "";
	
	/** 附加属性 格式(power:10|wisdom:10)(宝石追加后要保存的值，下次宝石追加时好清零)*/
	public StringBuffer extAtt = new StringBuffer();
	
	public int armSkill,armorsSkill,otherSkill,allSkill;
	
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
	
//	/** 物理防御附加 (万分比)*/
//	public StringBuffer phyDefExt = new StringBuffer();
//	
//	/** 精神防御附加 (万分比)*/
//	public StringBuffer sptDefExt = new StringBuffer();

	
	public GoodsEquip() {}
	
	
	public void saveTo(ByteBuffer buffer)
	{
		super.saveTo(buffer);
		
		buffer.writeUTF(extAtt.toString());
		buffer.writeUTF(attStr);
		if(!attStr.isEmpty())
		{
			String[] str = Utils.split(attStr, ":");
			for (int k = 0; k < str.length; k++) 
			{
				buffer.writeUTF(getVariable(str[k]));
			}
		}
	}
	
	
	public void writeSuperTo(ByteBuffer buffer)
	{
		super.writeTo(buffer);
	}
	


	
	public void writeTo(ByteBuffer buffer)
	{
		super.writeTo(buffer);
	
		buffer.writeUTF(extAtt.toString());
		buffer.writeInt(upNum);
		buffer.writeInt(startLevel);
		buffer.writeBoolean(isVIP);
		buffer.writeInt(equipLocation);
		buffer.writeInt(eType);
		if(equipLocation == 0)//武器
		{
			buffer.writeInt(phyAtt);
			buffer.writeInt(sptAtt);
		}
		else if(equipLocation <= 6)//防具
		{
			buffer.writeInt(phyDef);
			buffer.writeInt(sptDef);
		}
		else if(equipLocation <= 11)
		{
			buffer.writeInt(phyAtt);
			buffer.writeInt(sptAtt);
			buffer.writeInt(phyDef);
			buffer.writeInt(sptDef);
			if(equipLocation == 10)
				buffer.writeInt(modelId);
			else if(equipLocation == 9)
				buffer.writeInt(adornBackgroud);
		}
		buffer.writeUTF(job);

		String[] strs = new String[0];
		if(!attStr.isEmpty())
		{
			strs = Utils.split(attStr, ":");
		}

		int count = 0;
		for (int i = 0; i < strs.length; i++) 
		{
			if(strs[i].equals("createTime") || strs[i].equals("effectTime"))
				continue;
			if(strs[i].equals("upNum") || strs[i].equals("name"))
				continue;
			if(equipLocation == 0)
			{
				if(strs[i].equals("phyAtt") || strs[i].equals("sptAtt"))
					continue;
			}
			else if(equipLocation <= 6)
			{	
				if(strs[i].equals("phyDef") || strs[i].equals("sptDef"))
					continue;
			}
			else
			{
				if(strs[i].equals("phyAtt") || strs[i].equals("phyDef") || strs[i].equals("sptAtt") || strs[i].equals("sptDef"))
					continue;
			}
			if(strs[i].equals("startLevel"))
				continue;
			int value = Integer.parseInt(getVariable(strs[i]));
			if(value == 0)
				continue;
			count++;
		}
		buffer.writeInt(count);
		for (int i = 0; i < strs.length; i++) 
		{
			if(strs[i].equals("createTime") || strs[i].equals("effectTime"))
				continue;
			if(strs[i].equals("upNum") || strs[i].equals("name"))
				continue;
			if(equipLocation == 0)
			{
				if(strs[i].equals("phyAtt") || strs[i].equals("sptAtt"))
					continue;
			}
			else if(equipLocation <= 6)
			{	
				if(strs[i].equals("phyDef") || strs[i].equals("sptDef"))
					continue;
			}
			else
			{
				if(strs[i].equals("phyAtt") || strs[i].equals("phyDef") || strs[i].equals("sptAtt") || strs[i].equals("sptDef"))
					continue;
			}
			if(strs[i].equals("startLevel"))
				continue;
			int value = Integer.parseInt(getVariable(strs[i]));
			if(value == 0)
				continue;
			buffer.writeUTF(strs[i]);
			buffer.writeInt(value);
		}
	}
	
	
	public void writeAllTo(ByteBuffer buffer)
	{
		super.writeTo(buffer);
		buffer.writeInt(phyAtt);
		buffer.writeInt(phyAttRate);
		buffer.writeInt(sptAtt);
		buffer.writeInt(sptAttRate);
		buffer.writeInt(phyDef);
		buffer.writeInt(phyDefRate);
		buffer.writeInt(sptDef);
		buffer.writeInt(sptDefRate);
		buffer.writeInt(equipLocation);
		buffer.writeInt(equipType);
		buffer.writeInt(equipLevel);
		buffer.writeInt(maxEquipLevel);
		buffer.writeUTF(job);
		buffer.writeInt(agility);
		buffer.writeInt(curePoint);
		buffer.writeInt(killMonsterExp);
		buffer.writeInt(killMonsterExpRate);
		buffer.writeInt(lifePoint);
		buffer.writeInt(magicPoint);
		buffer.writeInt(noDefPhyHurt);
		buffer.writeInt(noDefSptHurt);
		buffer.writeInt(phyHurtAvoid);
		buffer.writeInt(phySmiteParm);
		buffer.writeInt(phySmiteRate);
		buffer.writeInt(power);
		buffer.writeInt(wisdom);
		buffer.writeInt(sptSmiteRate);
		buffer.writeInt(sptSmiteParm);
		buffer.writeInt(eType);
		buffer.writeInt(spirit);
		buffer.writeUTF(job);
	}
	
	public void copyTo(GameObject go)
	{
		super.copyTo(go);
		GoodsEquip goods = (GoodsEquip)go;

		goods.phyAtt = phyAtt;
		goods.phyAttRate = phyAttRate;
		goods.sptAtt = sptAtt;
		goods.sptAttRate = sptAttRate;
		goods.phyDef = phyDef;
		goods.phyDefRate = phyDefRate;
		goods.sptDef = sptDef; 
		goods.equipLocation = equipLocation;
		goods.maxEquipLevel = maxEquipLevel;
		goods.equipLevel = equipLevel;
		goods.job = job;
		goods.equipType = equipType;
		goods.curePoint = curePoint;
		goods.killMonsterExp = killMonsterExp;
		goods.killMonsterExpRate = killMonsterExpRate;
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
		
		goods.eType = eType;
		goods.modValueWhite = modValueWhite;
		goods.modValueGreen = modValueGreen;
		goods.attStr = attStr;
		goods.phyDefWhite = phyDefWhite;
		goods.phyDefGreen = phyDefGreen;
		goods.phyDefBlue = phyDefBlue;
		goods.phyDefPurple = phyDefPurple;
		goods.sptDefWhite = sptDefWhite;
		goods.sptDefGreen = sptDefGreen;
		goods.sptDefBlue = sptDefBlue;
		goods.sptDefPurple = sptDefPurple;
		goods.taskColor = taskColor;
		goods.modelId = modelId;
		goods.adornBackgroud = adornBackgroud;
		goods.startLevel = startLevel;
		goods.isVIP = isVIP;
		goods.deQuality = deQuality;
		goods.upNum = upNum;

		goods.extAtt = new StringBuffer(extAtt);
		goods.phyAttExt = new StringBuffer(phyAttExt);
		goods.sptAttExt = new StringBuffer(sptAttExt);
		goods.lifePointExt = new StringBuffer(lifePointExt);
		goods.magicPointExt = new StringBuffer(magicPointExt);
		goods.phyHurtAvoidExt = new StringBuffer(phyHurtAvoidExt);
		goods.sptHurtAvoidExt = new StringBuffer(sptHurtAvoidExt);
		goods.phySmiteParmExt = new StringBuffer(phySmiteParmExt);
		goods.sptSmiteParmExt = new StringBuffer(sptSmiteParmExt);
		goods.phySmiteRateExt = new StringBuffer(phySmiteRateExt);
		goods.sptSmiteRateExt = new StringBuffer(sptSmiteRateExt);
		goods.powerExt = new StringBuffer(powerExt);
		goods.wisdomExt = new StringBuffer(wisdomExt);
		goods.agilityExt = new StringBuffer(agilityExt);
		goods.spiritExt = new StringBuffer(spiritExt);
		goods.curePointExt = new StringBuffer(curePointExt);
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
	}
	
	@Override
	public void onDeleteImpl(PlayerController target) 
	{
	}

	@Override
	public boolean onRemoveImpl(PlayerController target)
	{
		Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");	
		Goods[] goodsList = bag.getGoodsList();

		if(useFlag)
		{
			int location = bag.getGoodsLocation(this.objectIndex);
			int nullLocation = bag.getNullLocation();

			if(nullLocation == -1)
				return false;
			
			setEquipValue(target,0);
			
			goodsList[nullLocation] = this;
			goodsList[location] = null;
			useFlag = false;
			target.getPlayer().setHitPoint(0,null);
			target.getPlayer().setMagicPoint(0,null);
			
			setEquipValue(target,1);
			
			if(equipLocation == 10)
			{
				//更换角色形象
				target.getPlayer().setPlayerModelMotionId();
			}
			else if(equipLocation == 11)
			{
				//更换角色称号
				target.getPlayer().title = "";
			}
			
//			System.out.println("取下后的形象ID："+target.getPlayer().modelMotionId);
			
			target.updateRoleInfo();
			
	
			target.fixPlayerBaseInfo(1);
			
			return true;
		}
		return false;
	}

	@Override
	public boolean onUseImpl(PlayerController target) 
	{
		if(useFlag)
			return false;
		boolean result = false;
		Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");	
		Goods[] goodsList = bag.getGoodsList();
		//装备上此装备
		result = isPlayerUseEquip(target);
		if(result)
		{
			ByteBuffer buffer = new ByteBuffer();
			equipBind();
			
			setEquipValue(target,0);
			int thisLocation = bag.getGoodsLocation(this.objectIndex);
			goodsList[thisLocation] = null;
			Goods equip = bag.getSameLocationGoods(equipLocation);
			if(equip != null)
			{
				int equipLocation = bag.getGoodsLocation(equip.objectIndex);
				int nullLocation = bag.getNullLocation();
				if(nullLocation == -1)
					return false;
				goodsList[equipLocation] = this;
				goodsList[nullLocation] = equip;
				equip.useFlag = false;
				this.useFlag = true;
				buffer.writeBoolean(true);
				buffer.writeUTF(equip.objectIndex+"");
				buffer.writeInt(nullLocation);
				target.getPlayer().setHitPoint(0,null);
				target.getPlayer().setMagicPoint(0,null);
				
				TaskInfo info = (TaskInfo)target.getPlayer().getExtPlayerInfo("taskInfo");
				info.onPlayerGotItem(equip.id,1, target);
			}
			else
			{
				goodsList[goodsList.length-1-this.equipLocation] = this;
				useFlag = true;
				buffer.writeBoolean(false);
			}
			buffer.writeUTF(this.objectIndex+"");
			buffer.writeInt(this.equipLocation);
			
			EquipSet equipSet = (EquipSet) target.getPlayer().getExtPlayerInfo("equipSet");
			equipSet.writeTo(buffer);
			target.getNetConnection().sendMessage(new SMsg(SMsg.S_PUT_ON_EQUIP_COMMAND,buffer));
			
			setEquipValue(target,1);
			
			if(equipLocation == 10)
			{
				//更换角色形象
				if(modelId != 0)
					target.getPlayer().modelMotionId = modelId;
			}
			else if(equipLocation == 11)
			{
				//更换角色称号
				target.getPlayer().title = name;
			}
//				System.out.println("穿上后的形象ID："+target.getPlayer().modelMotionId);
			target.updateRoleInfo();
			
			ByteBuffer buff = new ByteBuffer(128);
			writeTo(buff);
			target.getNetConnection().sendMessage(new SMsg(SMsg.S_GET_GOODS_INFO_COMMAND,buff));
	
			target.fixPlayerBaseInfo(1);
			
		}
		
		return result;
	}
	
	/**
	 * 玩家自身的条件够不够穿上装备
	 * @return true or false
	 */
	public boolean isPlayerUseEquip(PlayerController target)
	{
		Player player = target.getPlayer();
		if(player.level < level)
		{
			target.sendAlert(ErrorCode.ALERT_GOODS_LEVEL_LOW);
			return false;
		}
		String[] strs = job.split(":");
		for (int i = 0; i < strs.length; i++) 
		{
			if(isJob(target, Integer.parseInt(strs[i])))
					return true;
		}
		
//		int goodsJob1 = -1,goodsJob2 = -1,goodsJob3 = -1,goodsJob4;
//		if(strs.length == 1)
//		{
//			if(!isJob(target, Integer.parseInt(job)))
//			{
//				target.sendAlert(ErrorCode.ALERT_PROFESSION_ERROR);
//				return false;
//			}
//		}
//		else if(strs.length == 2)
//		{
//			goodsJob1 = Integer.parseInt(strs[0]);
//			goodsJob2 = Integer.parseInt(strs[1]);
//			if(!isJob(target, goodsJob1) && !isJob(target, goodsJob2))
//			{
//				target.sendAlert(ErrorCode.ALERT_PROFESSION_ERROR);
//				return false;
//			}
//		}
//		else if(strs.length == 3)
//		{
//			goodsJob1 = Integer.parseInt(strs[0]);
//			goodsJob2 = Integer.parseInt(strs[1]);
//			goodsJob3 = Integer.parseInt(strs[2]);
//			if(!isJob(target, goodsJob1) && !isJob(target, goodsJob2) && !isJob(target, goodsJob3))
//			{
//				target.sendAlert(ErrorCode.ALERT_PROFESSION_ERROR);
//				return false;
//			}
//		}
//		else if(strs.length == 4)
//		{
//			goodsJob1 = Integer.parseInt(strs[0]);
//			goodsJob2 = Integer.parseInt(strs[1]);
//			goodsJob3 = Integer.parseInt(strs[2]);
//			goodsJob4 = Integer.parseInt(strs[3]);
//			if(!isJob(target, goodsJob1) && !isJob(target, goodsJob2) && !isJob(target, goodsJob3)
//					&& !isJob(target,goodsJob4))
//			{
//				target.sendAlert(ErrorCode.ALERT_PROFESSION_ERROR);
//				return false;
//			}
//		}	
//		return true;
		target.sendAlert(ErrorCode.ALERT_PROFESSION_ERROR);
		return false;
	}

	
	private static final double WHIT_MULT = 1.0;
	private static final double GREE_MULT = 1.1;
	private static final double BLUE_MULT = 1.2;
	private static final double PONE_MULT = 1.75;
	private static final double PTWO_MULT = 1.75;
	private static final double PTHR_MULT = 1.75;
	

	/**
	 * 生成更好的装备
	 * @param equipQuality  要生成装备的品质(白-绿-蓝-紫-紫1-紫2)
	 * @return 生成后的新装备
	 */
	public GoodsEquip makeNewBetterEquip(int equipQuality)
	{
		if(equipQuality < 0)
			equipQuality = 0;
		
		GoodsEquip goods = (GoodsEquip) Goods.cloneObject(this);
		goods.quality = equipQuality;
		if(goods.equipLocation != 10 && goods.equipLocation != 11 && goods.equipLocation != 9)
		{
			goods.disMainGoodsAtt(this);
//			if(!goods.isVIP)
//			{
				switch (equipQuality) 
				{
					case 0:goods.setEquipByQuality(WHIT_MULT);break;
					case 1:goods.setEquipByQuality(GREE_MULT);break;
					case 2:goods.setEquipByQuality(BLUE_MULT);break;
					case 3:goods.setEquipByQuality(PONE_MULT);break;
					case 4:goods.setEquipByQuality(PTWO_MULT);break;
					case 5:goods.setEquipByQuality(PTHR_MULT);break;
				}
//			}
//			else
//			{
//				goods.setEquipByQuality(WHIT_MULT);
//			}
		}
		else
		{
			goods.setEquipByQuality(WHIT_MULT);
		}
		goods.setDefaultAtt();
		
		return goods;
	}

	
	
	
	/**
	 * 生成装备属性
	 * @param mult
	 */
	private void setEquipByQuality(double mult)
	{
//		System.out.println("isVIP:"+isVIP+  "  name:"+name);
		String[] attStr = {"power","agility","spirit","wisdom","lifePoint","magicPoint","noDefPhyHurt","noDefSptHurt",
				"phyHurtAvoid","sptHurtAvoid","phySmiteRate","sptSmiteRate","phySmiteParm","sptSmiteParm","curePoint",
				"killMonsterExp","killMonsterExpRate","clearPhySmite","clearSptSmite","clearPhySmiteParm","clearSptSmiteParm"};

		for (int i = 0; i < attStr.length; i++) 
		{
			String point = getVariable(attStr[i]);
			int value = Integer.parseInt(point);
//			if(!point.equals(String.valueOf(value)))
//			{
//				MainFrame.println("point budengyu value："+point+"   "+value);
//			}
//			System.out.println(name+"  att："+attStr[i]+"  value："+value+"  point："+point);
			if(value <= 0)
			{
				continue;
			}
			int modValue = (int) (value * mult);
			setVariable(attStr[i], String.valueOf(modValue));
			setAttStr(attStr[i]);
		}
	}
	
	
	
	/**
	 * 分配新装备的主属性
	 * @param goods
	 * @param equipQuality
	 */
	public void disMainGoodsAtt(GoodsEquip goods)
	{
//		System.out.println("name:"+name+"   quality:"+quality+"   equipType:"+equipType+"   modValueWhite:"+modValueWhite+"   modValueGreen:"+modValueGreen);
		if(quality == 0)//白色装备
		{
			if(equipType == 0)
			{
				phyAtt = (int) (goods.phyAtt * ((double)goods.modValueWhite/10000));
				phyDef = phyDefWhite;
				sptAtt = (int) (goods.sptAtt * ((double)goods.modValueWhite/10000));
				sptDef = sptDefWhite;
			}
			else if(equipType <= 4)
			{
				phyAtt = (int) (goods.phyAtt * ((double)goods.modValueWhite/10000));
				phyDef = phyDefWhite;
			}
			else if(equipType <= 8)
			{
				sptAtt = (int) (goods.sptAtt * ((double)goods.modValueWhite/10000));
				sptDef = sptDefWhite;
			}
			else
			{
				phyAtt = (int) (goods.phyAtt * ((double)goods.modValueWhite/10000));
				phyDef = phyDefWhite;
				sptAtt = (int) (goods.sptAtt * ((double)goods.modValueWhite/10000));
				sptDef = sptDefWhite;
			}
		}
		else if(quality == 1)//绿色装备
		{
			if(equipType == 0)
			{
				phyAtt = (int) (goods.phyAtt * ((double)goods.modValueGreen/10000));
				phyDef = phyDefGreen;
				sptAtt = (int) (goods.sptAtt * ((double)goods.modValueGreen/10000));
				sptDef = sptDefGreen;
			}
			else if(equipType <= 4)
			{
				phyAtt = (int) (goods.phyAtt * ((double)goods.modValueGreen/10000));;
				phyDef = phyDefGreen;
			}
			else if(equipType <= 8)
			{
				sptAtt = (int) (goods.sptAtt * ((double)goods.modValueGreen/10000));
				sptDef = sptDefGreen;
			}
			else
			{
				phyAtt = (int) (goods.phyAtt * ((double)goods.modValueGreen/10000));;
				phyDef = phyDefGreen;
				sptAtt = (int) (goods.sptAtt * ((double)goods.modValueGreen/10000));
				sptDef = sptDefGreen;
			}
		}
		else if(quality == 2)//蓝色装备
		{
			if(equipType == 0)
			{
				phyAtt = (int) (goods.phyAtt * ((double)goods.modValueGreen/10000) + goods.phyAtt * 0.2);
				phyDef = phyDefBlue;
				sptAtt = (int) (goods.sptAtt * ((double)goods.modValueGreen/10000) + goods.sptAtt * 0.2);
				sptDef = sptDefBlue;
			}
			else if(equipType <= 4)
			{
				phyAtt = (int) (goods.phyAtt * ((double)goods.modValueGreen/10000) + goods.phyAtt * 0.2); 
				phyDef = phyDefBlue;
			}
			else if(equipType <= 8)
			{
				sptAtt = (int) (goods.sptAtt * ((double)goods.modValueGreen/10000) + goods.sptAtt * 0.2); 
				sptDef = sptDefBlue;
			}
			else
			{
				phyAtt = (int) (goods.phyAtt * ((double)goods.modValueGreen/10000) + goods.phyAtt * 0.2); 
				phyDef = phyDefBlue;
				sptAtt = (int) (goods.sptAtt * ((double)goods.modValueGreen/10000) + goods.sptAtt * 0.2); 
				sptDef = sptDefBlue;
			}
		}
		else if(quality >= 3)//紫色1装备
		{
			if(equipType == 0)
			{
				phyAtt = (int) (goods.phyAtt * ((double)goods.modValueGreen/10000) + 2 * goods.phyAtt * 0.2);
				phyDef = phyDefPurple;
				sptAtt = (int) (goods.sptAtt * ((double)goods.modValueGreen/10000) + 2 * goods.sptAtt * 0.2);
				sptDef = sptDefPurple;
			}
			else if(equipType <= 4)
			{
				phyAtt = (int) (goods.phyAtt * ((double)goods.modValueGreen/10000) + 2 * goods.phyAtt * 0.2); 
				phyDef = phyDefPurple;
			}
			else if(equipType <= 8)
			{
				sptAtt = (int) (goods.sptAtt * ((double)goods.modValueGreen/10000) + 2 * goods.sptAtt * 0.2); 
				sptDef = sptDefPurple;
			}
			else
			{
				phyAtt = (int) (goods.phyAtt * ((double)goods.modValueGreen/10000) + 2 * goods.phyAtt * 0.2); 
				phyDef = phyDefPurple;
				sptAtt = (int) (goods.sptAtt * ((double)goods.modValueGreen/10000) + 2 * goods.sptAtt * 0.2); 
				sptDef = sptDefPurple;
			}
		}
		setAttStr("phyAtt");
		setAttStr("phyDef");
		setAttStr("sptAtt");
		setAttStr("sptDef");
	}
		
	

	public void setAttStr(String str)
	{
		String[] strs = Utils.split(attStr, ":");
		boolean flag = false;
		for (int i = 0; i < strs.length; i++)
		{
			if(strs[i].equals(str))
			{
				flag = true;
				break;
			}
		}
		if(!flag)
		{
			if(attStr.isEmpty())
				attStr += str;
			else
				attStr += ":" + str;
		}
	}
	

	
	/**
	 * 装备生成时没有的属性设置为0
	 */
	public void setDefaultAtt()
	{
		String[] aStr = {"power","agility","spirit","wisdom","clearPhySmite","clearSptSmite","clearPhySmiteParm","clearSptSmiteParm",
		           "lifePoint","magicPoint","noDefPhyHurt","noDefSptHurt","phyHurtAvoid","sptHurtAvoid","phySmiteRate","sptSmiteRate",
		           "phySmiteParm","sptSmiteParm","curePoint","killMonsterExp","killMonsterExpRate"};
		
		if(attStr.isEmpty())
		{
			for (int i = 0; i < aStr.length; i++)
			{
				setVariable(aStr[i], "0");
			}
		}
		else
		{
			String[] strs = Utils.split(attStr, ":");
			
			for (int i = 0; i < aStr.length; i++)
			{
				boolean flag = false;
				for (int j = 0; j < strs.length; j++)
				{
					if(aStr[i].equals(strs[j]))
					{
						flag = true;
						break;
					}
				}
				if(!flag)
				{
					setVariable(aStr[i], "0");
				}
			}
		}

		if(!isVIP)
		{
			if(quality > 1)
			{
				if(startLevel < 10)
				{
					startLevel += 1;
					setAttStr("startLevel");
				}
			}
		}
	}
	

	
	@Override
	public boolean onUseGoodsBattle(PlayerController player) {
		
		return false;
	}
	
	
	public String getVariable(String key)
	{
		if(key.equals("maxHitPoint") || key.equals("battleMaxHP"))
			return ""+ lifePoint;
	    else if(key.equals("maxMagicPoint") || key.equals("battleMaxMP"))
			return ""+ magicPoint;
		else if(key.equals("power"))
			return ""+ power;
		else if(key.equals("nimble") || key.equals("agility"))
			return ""+ agility;
		else if(key.equals("spirit"))
			return ""+ spirit;
		else if(key.equals("wisdom"))
			return ""+ wisdom;
		else if(key.equals("curePoint"))
			return ""+ curePoint;	
		else if(key.equals("phyAtt") || key.equals("phsAtt"))
			return ""+ phyAtt;
		else if(key.equals("sptAtt"))
			return ""+ sptAtt;
		else if(key.equals("phyDef") || key.equals("phsDef"))
			return ""+ phyDef;
		else if(key.equals("sptDef"))
			return ""+ sptDef;
		else if(key.equals("phySmiteRate") || key.equals("phsSmiteRate"))
			return ""+ phySmiteRate;
		else if(key.equals("sptSmiteRate"))
			return ""+ sptSmiteRate;
		else if(key.equals("phyHurtAvoid") || key.equals("phsHurtAvoid"))
			return ""+ phyHurtAvoid;
		else if(key.equals("sptHurtAvoid"))
			return ""+ sptHurtAvoid;
		else if(key.equals("noDefPhyHurt") || key.equals("noDefPhsHurt"))
			return ""+ noDefPhyHurt;
		else if(key.equals("noDefSptHurt"))
			return ""+ noDefSptHurt;
		else if(key.equals("phySmiteHurtParm") || key.equals("phsSmiteHurtParm") || key.equals("phsSmiteParm") || key.equals("phySmiteParm"))
			return ""+ phySmiteParm;
		else if(key.equals("sptSmiteHurtParm") || key.equals("sptSmiteParm"))
			return ""+ sptSmiteParm;
		else if(key.equals("clearNoDefHurt"))
			return ""+ clearNoDefHurt;
		else if(key.equals("phyAttExt"))
			return phyAttExt.toString();
		else if(key.equals("sptAttExt"))
			return sptAttExt.toString();
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
		else if(key.equals("agilityExt"))
			return agilityExt.toString();
		else if(key.equals("spiritExt"))
			return spiritExt.toString();
		else if(key.equals("curePointExt"))
			return curePointExt.toString();
		else if(key.equals("modelId"))
			return ""+modelId;
		else if(key.equals("adornBackgroud"))
			return ""+adornBackgroud;
		else if(key.equals("clearPhySmite") || key.equals("clearPhsSmite"))
			return ""+clearPhySmite;
		else if(key.equals("createTime"))
			return ""+createTime;
		else if(key.equals("effectTime"))
			return ""+effectTime;
		else
			return super.getVariable(key);
	}
	
	
	public boolean setVariable(String key,String value)
	{
		if(key.equals("isVIP"))
		{
			isVIP = (value.equals("1"));
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
		else if(key.equals("phsSmiteHurtParm") || key.equals("phySmiteHurtParm") || key.equals("phsSmiteParm") || key.equals("phySmiteParm"))
		{
			phySmiteParm = Integer.parseInt(value);
			return true;
		}
		else if(key.equals("sptSmiteHurtParm") || key.equals("sptSmiteParm"))
		{
			sptSmiteParm = Integer.parseInt(value);
			return true;
		}
		else
		{
			return super.setVariable(key, value);
		}
	}

	
	public void setExtAtt(String str)
	{
		if(this.extAtt.length() == 0)
			this.extAtt.append(str);
		else 
			this.extAtt.append("|"+str);
	}
	
	public int getAddAttCount()
	{
		int count = 0;
		switch (startLevel)
		{
			case 1: count = 1; break;
			case 2: count = 2; break;
			case 3: count = 3; break;
			case 4: count = 4; break;
			case 5: count = 5; break;
			case 6: count = 6; break;
			case 7: count = 7; break;
			case 8: count = 8; break;
			case 9: count = 9; break;
			case 10:count = 10;break;
		}
		
		int result = (int) (Math.random() * count) + 1;
		
		return result;
	}
	
	/**
	 * 附加属性加
	 */
	public void setGemExt(String gemAttStr)
	{
		String[] strs = {"phyAttExt","sptAttExt","lifePointExt","magicPointExt","phyHurtAvoidExt","sptHurtAvoidExt",
							"phySmiteParmExt","sptSmiteParmExt","phySmiteRateExt","sptSmiteRateExt",
							"powerExt","wisdomExt","agilityExt","spiritExt","curePointExt"};
		
		int leng = getAddAttCount();
		for (int i = 0; i < leng; i++)
		{
			int random = (int) (Math.random() * strs.length);
			String pointStr = getVariable(strs[random]);
			String[] values = Utils.split(pointStr, ":");
			int min = Integer.parseInt(values[0]);
			int max = Integer.parseInt(values[1]);
			int point = (int) (Math.random() * (max - min) + min);
			if(point <= 0)
				continue;
			String attStr = strs[random].substring(0,strs[random].indexOf("Ext"));
			int bPoint = Integer.parseInt(getVariable(attStr));
			setVariable(attStr, String.valueOf(bPoint+point));
			setExtAtt(attStr+":"+point);
			
			setAttStr(attStr);
			upNum++;
			setAttStr("upNum");
		}
		
		if(extAtt.toString().equals(gemAttStr))
		{
			setNoZeroExt(gemAttStr);
		}
	}
	
	
	/**
	 * 把宝石默认的值加上
	 * @param attStr
	 */
	public void setDefaultAtt(String attStr)
	{
		if(attStr.isEmpty())
			return;
		String[] strs = Utils.split(attStr, "|");
		for (int i = 0; i < strs.length; i++)
		{
			if(strs[i].equals("0:0"))
				continue;
			String[] gemExt = Utils.split(strs[i], ":"); 
			int point = Integer.parseInt(getVariable(gemExt[0]));
			int addPoint = Integer.parseInt(gemExt[1]);
			setVariable(gemExt[0], String.valueOf(point+addPoint));
			setAttStr(gemExt[0]);
			setExtAtt(strs[i]);
		}
	}
	
	/**
	 * 追加前把前一个宝石加的值清零
	 */
	public void clearExtAtt()
	{
		if(extAtt.length() == 0)
			return;
		String[] strs = Utils.split(extAtt.toString(), "|");
		for (int i = 0; i < strs.length; i++) 
		{
			String[] atts = Utils.split(strs[i], ":");
			int bPoint = Integer.parseInt(getVariable(atts[0]));
			int point = Integer.parseInt(atts[1]);
			setVariable(atts[0], String.valueOf(bPoint-point));
		}
		extAtt.delete(0, extAtt.length());
		upNum = 0;
	}
	
	
	private void setNoZeroExt(String gemAttStr)
	{
		String[] strs = {"phyAttExt","sptAttExt","lifePointExt","magicPointExt","phyHurtAvoidExt","sptHurtAvoidExt",
				"phySmiteParmExt","sptSmiteParmExt","phySmiteRateExt","sptSmiteRateExt",
				"powerExt","wisdomExt","agilityExt","spiritExt","curePointExt"};
		
		for (int i = 0; i < strs.length; i++)
		{
			int random = (int) (Math.random() * strs.length);
			String pointStr = getVariable(strs[random]);
			if(pointStr.equals("0:0") || pointStr.startsWith("0"))
				continue;
			String[] values = Utils.split(pointStr, ":");
			int min = Integer.parseInt(values[0]);
			int max = Integer.parseInt(values[1]);
			int point = (int) (Math.random() * (max - min) + min);
			if(point <= 0)
				continue;
			String attStr = strs[random].substring(0,strs[random].indexOf("Ext"));
			int bPoint = Integer.parseInt(getVariable(attStr));
			setVariable(attStr, String.valueOf(bPoint+point));
			setExtAtt(attStr+":"+point);
			setAttStr(attStr);
			
			upNum++;
			setAttStr("upNum");
			break;
		}
		
		if(extAtt.toString().equals(gemAttStr))
		{
			for (int i = 0; i < strs.length; i++)
			{
				String pointStr = getVariable(strs[i]);
				if(pointStr.equals("0:0") || pointStr.startsWith("0"))
					continue;
				String[] values = Utils.split(pointStr, ":");
				int min = Integer.parseInt(values[0]);
				int max = Integer.parseInt(values[1]);
				int point = (int) (Math.random() * (max - min) + min);
				if(point <= 0)
					continue;
				String attStr = strs[i].substring(0,strs[i].indexOf("Ext"));
				int bPoint = Integer.parseInt(getVariable(attStr));
				setVariable(attStr, String.valueOf(bPoint+point));
				setExtAtt(attStr+":"+point);
				setAttStr(attStr);
				
				upNum++;
				setAttStr("upNum");
				break;
			}
		}
	}
	
	
	/**
	 * 是否能用宝石追加
	 * @return
	 */
	public boolean isGemEquip()
	{
		if(startLevel == 0 || (eType != 0 && equipLocation == 10))
			return false;
		return true;
	}
	
	
	/**
	 * 改了基本属性后，重新生成一次
	 */
	public void updateEquipChange()
	{
		if(eType != 0 && equipLocation == 10)
			return;

		GoodsEquip newEquip = (GoodsEquip) DataFactory.getInstance().getGameObject(id);
		GoodsEquip equip = newEquip.makeNewBetterEquip(quality);
		equip.objectIndex = objectIndex;
		equip.goodsCount = goodsCount;
		equip.bindMode = bindMode;
		equip.useFlag = useFlag;
		equip.upNum = upNum;
		equip.createTime = createTime;
		equip.effectTime = effectTime;
		if(eType == 1 && equipLocation == 11)	
		{
			equip.name = name;
			equip.setAttStr("name");
		}

		if(extAtt.length() > 0)
		{
			equip.extAtt = new StringBuffer(extAtt);
			
			String[] strs = Utils.split(extAtt.toString(), "|");
			
			for (int i = 0; i < strs.length; i++) 
			{
				String[] atts = Utils.split(strs[i], ":");
				int point = 0,addPoint = 0;
				try
				{
					point = Integer.parseInt(equip.getVariable(atts[0]));
					addPoint = Integer.parseInt(atts[1]);
					equip.setVariable(atts[0], (point+addPoint)+"");
					equip.setAttStr(atts[0]);
				}
				catch (Exception e) 
				{
					MainFrame.println("GoodsEquip updateEquipChange() error:"+e.getMessage());
				}	
			}
		}
		
		equip.copyTo(this);
		objectIndex = equip.objectIndex;
		if(upNum > 0)
		{
			setAttStr("upNum");
		}

		equip = null;
		newEquip = null;
	}

	
	/**
	 * 把战斗属性全部COPY到另外一件装备上
	 * @param equip
	 */
	public void copyAllTo(Goods goods)
	{
		if(goods instanceof GoodsEquip)
		{
			GoodsEquip equip = (GoodsEquip) goods;
			equip.startLevel = startLevel;
			if(equip.startLevel > 0)
			{
				equip.setAttStr("startLevel");
			}
			equip.upNum = upNum;
			if(equip.upNum > 0)
			{
				equip.setAttStr("upNum");
			}
			equip.extAtt = new StringBuffer(extAtt);
			String[] aStr = {"power","agility","spirit","wisdom","clearPhySmite","clearSptSmite","clearPhySmiteParm","clearSptSmiteParm",
			           "lifePoint","magicPoint","noDefPhyHurt","noDefSptHurt","phyHurtAvoid","sptHurtAvoid","phySmiteRate","sptSmiteRate",
			           "phySmiteParm","sptSmiteParm","curePoint","phyAtt","sptAtt","phyDef","sptDef"};
			for (int i = 0; i < aStr.length; i++) 
			{
				int point = Integer.parseInt(equip.getVariable(aStr[i]));
				int addPoint = Integer.parseInt(getVariable(aStr[i]));
				equip.setVariable(aStr[i], (point+addPoint)+"");
				equip.setAttStr(aStr[i]);
			}	
		}
	}
	
	
}
