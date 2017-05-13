package cc.lv1.rpg.gs.entity.impl.pet;

import java.text.SimpleDateFormat;

import vin.rabbit.comm.GameObject;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.i.Map;
import vin.rabbit.util.collection.impl.ArrayList;
import vin.rabbit.util.collection.impl.HashMap;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.data.DC;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.data.Exp;
import cc.lv1.rpg.gs.data.PetExp;
import cc.lv1.rpg.gs.entity.controller.BattleController;
import cc.lv1.rpg.gs.entity.controller.BusinessController;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.ext.Bag;
import cc.lv1.rpg.gs.entity.ext.PetTome;
import cc.lv1.rpg.gs.entity.ext.PlayerBaseInfo;
import cc.lv1.rpg.gs.entity.ext.ReviseBaseInfo;
import cc.lv1.rpg.gs.entity.ext.TaskInfo;
import cc.lv1.rpg.gs.entity.impl.BoxDropProp;
import cc.lv1.rpg.gs.entity.impl.Goods;
import cc.lv1.rpg.gs.entity.impl.Mail;
import cc.lv1.rpg.gs.entity.impl.Player;
import cc.lv1.rpg.gs.entity.impl.Role;
import cc.lv1.rpg.gs.entity.impl.UpRole;
import cc.lv1.rpg.gs.entity.impl.battle.PetBattleTmp;
import cc.lv1.rpg.gs.entity.impl.battle.skill.PetActiveSkill;
import cc.lv1.rpg.gs.entity.impl.battle.skill.PetPassiveSkill;
import cc.lv1.rpg.gs.entity.impl.battle.skill.PetSkill;
import cc.lv1.rpg.gs.entity.impl.battle.skill.Skill;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsPetEquip;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsProp;
import cc.lv1.rpg.gs.gui.MainFrame;
import cc.lv1.rpg.gs.net.SMsg;
import cc.lv1.rpg.gs.other.ErrorCode;
import cc.lv1.rpg.gs.util.FontConver;

/**
 * 宠物实体
 * @author bean
 *
 */
public class Pet extends Role  
{
	public static final int FIRST_GROW = 100;
	public static final int SECOND_GROW = 150;
	/**用来判断是否有新技能加入(key为宠物职业,value为这个职业的技能数量)*/
	public static Map jobSkillMap = new HashMap(10);
//	public static final String FIRSTATT1 = "lifePoint";
//	public static final String FIRSTATT2 = "magicPoint";
	
	/** 强制结束宠物养成需要花费的元宝 */
	public static final int OVERMONEY = 1;
	
	public static final int PUTONGPET = 1;
	public static final int SHOUHUPET = 2;
	
//	/** 普通宠物初始经验 */
//	public static final int PETEXP = 40;
//	/** 守护宠物初始经验 */
//	public static final int BATTLEPETEXP = 5;
	
	public static final double TRAINMIN = 0.9;
	public static final double TRAINMAX = 1.1;
	
	/** 激活状态 */
	public boolean isActive;
	
	/** 需要通知变身的形象 */
	public int cmi1,cmi2,tmpLevel;
	public String att1 = "",att2 = "";
	
	
	/** 遛宠状态 */
	public boolean isStroll = false;
	
	/** 出行状态(一次只能选择一次出行方式1.嬉戏 2.锻炼 3.探险 4.学习(只有守护才能学习)) */
	public int trainState;
	
	/** 开始出行时间 */
	public long trainTime;
	
	/** 第一次出行时间 */
	public long firstTime;
	
	/** 每天可喂养的次数(配置文件读取) */
	public int maxFeedCount;
	
	/** 每天可出行的次数(配置文件读取) */
	public int maxActivePoint;
	
	/** 宠物类型(1.普通进化宠物 2.战斗守护神) */
	public int petType;
	
	/** 守护职业(一转就从101开始,二转就从201开始,三转从301开始) */
	public int job;
	
	/** 是否使用了回复体力 */
	public boolean isHuifuTili;
	
	/** 当前使用了多少战斗体力 */
	public int battlePoint;
	
	/** 最大体力 */
	public int maxBattlePoint;
	
	/** 开始喂养时间 */
	public long feedTime;
	
	/** 总喂养次数 (程序计算,喂养一次就加一次)*/
	public int feedCount;
	
	/** 宠物每次出行的时间 */
	public int gameTime;
	
	/** 活跃度(出行一次就减一次) */
	public int activePoint;
	
	/** 成长值(基础宠物的,守护的以百分比形式) */
	public int growPoint;
	
	/** 亲密度 */
	public long intimacyPoint;
	
	/** 升级需要加的属性 */
	public String growBasic = "";
	
	/**
	 * 以下 属性都是加到玩家身上
	 */
	/** 力量 */
	public int power;
	
	/** 敏捷 */
	public int nimble;
	
	/** 精神 */
	public int spirit;
	
	/** 智慧 */ 
	public int wisdom;
	
	/** 最大生命(给主人增加最大生命) */
	public int lifePoint;
	
	/** 最大精力(给主人增加最大精力) */
	public int magicPoint;
	
	/** 物理攻击 */
	public int phyAtt;
	
	/** 精神攻击 */
	public int sptAtt;
	
	/** 物理防御 (万分比)*/
	public int phyDef;
	
	/** 精神防御 (万分比)*/
	public int sptDef;
	
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
	
	/** 力量附加 */
	public StringBuffer powerExt = new StringBuffer();
	
	/** 敏捷附加 */
	public StringBuffer nimbleExt = new StringBuffer();
	
	/** 精神附加 */
	public StringBuffer spiritExt = new StringBuffer();
	
	/** 智慧附加 */ 
	public StringBuffer wisdomExt = new StringBuffer();
	
	/** 生命附加(给主人增加最大生命) */
	public StringBuffer lifePointExt = new StringBuffer();
	
	/** 精力附加(给主人增加最大精力) */
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
	
	/** 形象序列 */
	public String modelWay = "a";

	public String remark = "";
	
	/** 守护的时候表示天赋,守护的时候表示是隐藏属性格式是key:value|key:value,只记录在这个上面，不设置到属性上面 */
	public String attStr = "";
	
	public String initSkill = "";
	
	/** 主人 */
	private Player player;

	private PetExp expObj;
	
	private Map upMap = new HashMap();
	
	private PetSkill[] petSkills = new PetSkill[0];
	
	private PetActiveSkill[] pass = new PetActiveSkill[0];
	
	private PetPassiveSkill[] ppss = new PetPassiveSkill[0]; 
	
	private List equips = new ArrayList();
	
	public Map getUpMap()
	{
		return this.upMap;
	}
	
	public void fixPutongPetGrow()
	{
		if(petType == PUTONGPET)
		{
			if(growPoint <= 0)
			{
				Pet pet = (Pet) DataFactory.getInstance().getGameObject(id);
				growPoint = pet.growPoint;
			}
		}
	}
	
	public PetSkill getPetSkillByVIPType(int type)
	{
		List list = new ArrayList();
		for (int i = 0; i < petSkills.length; i++)
		{
			if(petSkills[i] != null && petSkills[i].isVIP == type)
				list.add(petSkills[i]);
		}
		return (PetSkill) list.get((int) (Math.random() * list.size()));
	}
	
	public long getInitExp()
	{
		Map expMap = null;
		if(petType == Pet.PUTONGPET)
		{
			expMap = (Map)DataFactory.getInstance().getAttachment(DataFactory.ATTACH_PET_EXP);
		}
		else if(petType == Pet.SHOUHUPET)
		{
			expMap = (Map)DataFactory.getInstance().getAttachment(DataFactory.BATTLE_PET_EXP);
		}
		return ((PetExp)expMap.get(2)).levelExp;
	}
	
	public void setStroll(boolean flag)
	{
		this.isStroll = flag;
	}
	
	public void setPlayer(Player player)
	{
		this.player = player;
	}
	
	public Player getPlayer()
	{
		return this.player;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void setUpMap(Map map)
	{
		this.upMap = map;
	}
	
	public int getUpMapSize()
	{
		return ((HashMap) upMap).size();
	}
	
	public PetUpRule getPetUpRule(int level)
	{
		if(upMap == null)
			return null;
		return (PetUpRule) upMap.get(level);
	}
	
	public PetSkill[] getPetSkills()
	{
		return petSkills;
	}
	
	public PetActiveSkill[] getPetActiveSkills()
	{
		return pass;
	}
	
	public PetPassiveSkill[] getPetPassiveSkills()
	{
		return ppss;
	}
	
	public List getEquipList()
	{
		return equips;
	}
	
	public void addEquip(GoodsPetEquip gpe)
	{
		if(!equips.contains(gpe))
			equips.add(gpe);
	}
	
	public void removeEquip(GoodsPetEquip gpe)
	{
		equips.remove(gpe);
	}
	
	public GoodsPetEquip getEquipByObjectIndex(long objectIndex)
	{
		for (int i = 0; i < equips.size(); i++) 
		{
			GoodsPetEquip g = (GoodsPetEquip) equips.get(i);
			if(g.objectIndex == objectIndex)
				return g;
		}
		return null;
	}
	
	public GoodsPetEquip getEquipByLocation(int location)
	{
		for (int i = 0; i < equips.size(); i++) 
		{
			GoodsPetEquip g = (GoodsPetEquip) equips.get(i);
			if(g.equipLocation == location)
				return g;
		}
		return null;
	}

	
	public void copyTo(GameObject go)
	{
		super.copyTo(go);
		Pet pet = (Pet)go;

		pet.gameTime = gameTime;
		pet.activePoint = activePoint;
		pet.growPoint = growPoint;
		pet.requireExp = requireExp;
		pet.experience = experience;
		pet.feedCount = feedCount;
		pet.maxFeedCount = maxFeedCount;
		pet.maxActivePoint = maxActivePoint;
		pet.feedTime = feedTime;
		pet.petType = petType;
		pet.intimacyPoint = intimacyPoint;
		pet.remark = remark;
		pet.expObj = expObj;
		pet.upMap = upMap;
		pet.modelWay = modelWay;
		pet.trainTime = trainTime;
		pet.trainState = trainState;
		pet.isActive = isActive;
		pet.attStr = attStr;
		pet.cmi1 = cmi1;
		pet.cmi2 = cmi2;
		pet.tmpLevel = tmpLevel;
		pet.att1 = att1;
		pet.att2 = att2;
		pet.firstTime = firstTime;
		pet.job = job;
		pet.maxBattlePoint = maxBattlePoint;
		pet.battlePoint = battlePoint;
		pet.initSkill = initSkill;
		pet.growBasic = growBasic;
		
		pet.power = power;
		pet.nimble = nimble;
		pet.spirit = spirit;
		pet.wisdom = wisdom;
		pet.lifePoint = lifePoint;
		pet.magicPoint = magicPoint;
		pet.phyAtt = phyAtt;
		pet.sptAtt = sptAtt;
		pet.phyDef = phyDef;
		pet.sptDef = sptDef;
		pet.curePoint = curePoint;
		pet.noDefPhyHurt = noDefPhyHurt;
		pet.noDefSptHurt = noDefSptHurt;
		pet.phyHurtAvoid = phyHurtAvoid;
		pet.sptHurtAvoid = sptHurtAvoid;
		pet.phySmiteParm = phySmiteParm;
		pet.phySmiteRate = phySmiteRate;
		pet.sptSmiteRate = sptSmiteRate;
		pet.sptSmiteParm = sptSmiteParm;
		pet.clearPhySmiteParm = clearPhySmiteParm;
		pet.clearSptSmiteParm = clearSptSmiteParm;
		pet.clearPhySmite = clearPhySmite;
		pet.clearSptSmite = clearSptSmite;
		pet.clearNoDefHurt = clearNoDefHurt;
		
		pet.lifePointExt = new StringBuffer(lifePointExt);
		pet.magicPointExt = new StringBuffer(magicPointExt);
		pet.phyAttExt = new StringBuffer(phyAttExt);
		pet.sptAttExt = new StringBuffer(sptAttExt);
		pet.phyDefExt = new StringBuffer(phyDefExt);
		pet.sptDefExt = new StringBuffer(sptDefExt);
		pet.powerExt = new StringBuffer(powerExt);
		pet.wisdomExt = new StringBuffer(wisdomExt);
		pet.nimbleExt = new StringBuffer(nimbleExt);
		pet.spiritExt = new StringBuffer(spiritExt);
		pet.curePointExt = new StringBuffer(curePointExt);
		pet.phyHurtAvoidExt = new StringBuffer(phyHurtAvoidExt);
		pet.sptHurtAvoidExt = new StringBuffer(sptHurtAvoidExt);
		pet.phySmiteParmExt = new StringBuffer(phySmiteParmExt);
		pet.sptSmiteParmExt = new StringBuffer(sptSmiteParmExt);
		pet.phySmiteRateExt = new StringBuffer(phySmiteRateExt);
		pet.sptSmiteRateExt = new StringBuffer(sptSmiteRateExt);
		pet.noDefPhyHurtExt = new StringBuffer(noDefPhyHurtExt);
		pet.noDefSptHurtExt = new StringBuffer(noDefSptHurtExt);
		pet.clearPhySmiteExt = new StringBuffer(clearPhySmiteExt);
		pet.clearSptSmiteExt = new StringBuffer(clearSptSmiteExt);
		pet.clearNoDefHurtExt = new StringBuffer(clearNoDefHurtExt);
		pet.clearPhySmiteParmExt = new StringBuffer(clearPhySmiteParmExt);
		pet.clearSptSmiteParmExt = new StringBuffer(clearSptSmiteParmExt);
		
		pet.player = player;
	}
	

	
	@Override
	public void loadFrom(ByteBuffer byteBuffer)
	{
		id = byteBuffer.readInt();
		name = byteBuffer.readUTF();
		objectIndex = byteBuffer.readLong();
		level = byteBuffer.readInt();
		byteBuffer.readShort();
		int skillLength = byteBuffer.readShort();
		petSkills = new PetSkill[skillLength];
		for (int i = 0; i < skillLength; i++)
		{
			int id = byteBuffer.readInt();
			boolean isStudied = byteBuffer.readBoolean();
			boolean isStudying = byteBuffer.readBoolean();
			int studyCount = byteBuffer.readByte();
			int exp = byteBuffer.readInt();

			PetSkill skill = (PetSkill)DataFactory.getInstance().getGameObject(id);
			if(skill == null)
			{
				System.out.println("load petSkill skillID error:"+id);
				continue;
			}
		
			skill = (PetSkill)PetSkill.cloneObject(skill);
			skill.isStudied = isStudied;
			skill.studyCount = studyCount;
			skill.isStudying = isStudying;
			skill.exp = exp;

			if(skill.isStudied)
			{
				addPetSkill(skill);
			}
			petSkills[i] = skill;
		}
		job = byteBuffer.readByte(); 
		growPoint = byteBuffer.readShort();
		isHuifuTili = byteBuffer.readBoolean();
		experience = byteBuffer.readLong();
		requireExp = byteBuffer.readLong();
		feedTime = byteBuffer.readLong();
		feedCount = byteBuffer.readInt();
		activePoint = byteBuffer.readInt();
		battlePoint = byteBuffer.readShort();
		int size = byteBuffer.readByte();
		for (int i = 0; i < size; i++) 
		{
			int id = byteBuffer.readInt();
			GoodsPetEquip goods = (GoodsPetEquip) DataFactory.getInstance().getGameObject(id);
			GoodsPetEquip g = (GoodsPetEquip) GoodsPetEquip.cloneObject(goods);
			g.loadFrom(byteBuffer);
			g.setExtAtt(g.extAtt);
			g.useFlag = true;
			equips.add(g);
		}
		byteBuffer.readByte();
		modelId = byteBuffer.readInt();
		isActive = byteBuffer.readBoolean();
		modelWay = byteBuffer.readUTF();
		intimacyPoint = byteBuffer.readLong();
		
		attStr = byteBuffer.readUTF();
		cmi1 = byteBuffer.readInt();
		cmi2 = byteBuffer.readInt();
		tmpLevel = byteBuffer.readInt();
		trainTime = byteBuffer.readLong();
		trainState = byteBuffer.readByte();
		att1 = byteBuffer.readUTF();
		att2 = byteBuffer.readUTF();
		firstTime = byteBuffer.readLong();
		
		updateJobSkills();//要在加载完技能后才调这个方法

		PetExp exp = getExpByLevel(level+1);
		PetExp nowExp = getExpByLevel(level);
		if(exp != null)
		{
			if(requireExp > exp.levelExp)
			{
				if(nowExp != null)
				{
					if(experience >= nowExp.total)
					{
						long nExp = experience - nowExp.total;
						requireExp = exp.levelExp - nExp;
						if(requireExp >= exp.levelExp)
							requireExp = 0;
					}
				}
			}
		}
		
		if(requireExp <= 0)
		{
			if(nowExp != null)
			{
				experience = nowExp.total;
				requireExp = 0;
				if(exp != null)
				{
					requireExp = exp.levelExp;
				}
			}
		}

		PetUpRule pur = getNearUpRule();
		if(pur != null)
		{
			int mId = 0;
			String[] ways = Utils.split(pur.upWay, "|");
			String[] mIds = Utils.split(pur.upModelId, "|");
			if(ways.length == 1)
			{
				ways = Utils.split(pur.upWay, ":");
				mIds = Utils.split(pur.upModelId, ":");
				if(modelWay.equals(ways[0]))
				{
					mId = Integer.parseInt(mIds[0]);
					if(mId != 0)
						modelId = mId;
				}
				else if(modelWay.equals(ways[1]))
				{
					mId = Integer.parseInt(mIds[1]);
					if(mId != 0)
						modelId = mId;
				}
			}
			else if(ways.length > 1)
			{
				for (int i = 0; i < ways.length; i++) 
				{
					String[] upWays = Utils.split(ways[i], ":");
					String[] upMods = Utils.split(mIds[i], ":");
					for (int j = 0; j < upWays.length; j++) 
					{
						if(modelWay.equals(upWays[j]))
						{
							mId = Integer.parseInt(upMods[j]);
							if(mId != 0)
							{
								modelId = mId;
								break;
							}
						}
					}
				}
			}
		}

		if(attStr.isEmpty())
			return;
		
		if(petType == Pet.SHOUHUPET)
			return;
		
		String[] strs = Utils.split(attStr, ":");
		for (int i = 0; i < strs.length; i++) 
		{
			setVariable(strs[i], String.valueOf(byteBuffer.readInt()));
		}
		
	}

	@Override
	public void saveTo(ByteBuffer byteBuffer)
	{
		byteBuffer.writeInt(id);
		byteBuffer.writeUTF(name);
		byteBuffer.writeLong(objectIndex);
		byteBuffer.writeInt(level);
		byteBuffer.writeShort(0);
		byteBuffer.writeShort(petSkills.length);
		for (int i = 0; i < petSkills.length; i++) 
		{
			byteBuffer.writeInt(petSkills[i].id);
			byteBuffer.writeBoolean(petSkills[i].isStudied);
			byteBuffer.writeBoolean(petSkills[i].isStudying);
			byteBuffer.writeByte(petSkills[i].studyCount);
			byteBuffer.writeInt(petSkills[i].exp);
		}
		byteBuffer.writeByte(job);
		byteBuffer.writeShort(growPoint);
		byteBuffer.writeBoolean(isHuifuTili);
		byteBuffer.writeLong(experience);
		byteBuffer.writeLong(requireExp);
		byteBuffer.writeLong(feedTime);
		byteBuffer.writeInt(feedCount);
		byteBuffer.writeInt(activePoint);
		byteBuffer.writeShort(battlePoint);
		byteBuffer.writeByte(equips.size());
		for (int i = 0; i < equips.size(); i++) 
		{
			GoodsPetEquip goods = (GoodsPetEquip) equips.get(i);
			byteBuffer.writeInt(goods.id);
			goods.saveTo(byteBuffer);
		}
		byteBuffer.writeByte(0);
		byteBuffer.writeInt(modelId);
		byteBuffer.writeBoolean(isActive);
		byteBuffer.writeUTF(modelWay);
		byteBuffer.writeLong(intimacyPoint);
		
		byteBuffer.writeUTF(attStr);
		byteBuffer.writeInt(cmi1);
		byteBuffer.writeInt(cmi2);
		byteBuffer.writeInt(tmpLevel);
		byteBuffer.writeLong(trainTime);
		byteBuffer.writeByte(trainState);
		byteBuffer.writeUTF(att1);
		byteBuffer.writeUTF(att2);
		byteBuffer.writeLong(firstTime);

		if(attStr.isEmpty())
			return;
		
		if(petType == Pet.SHOUHUPET)
			return;
		
		String[] strs = Utils.split(attStr, ":");
		for (int i = 0; i < strs.length; i++) 
		{
			byteBuffer.writeInt(Integer.parseInt(getVariable(strs[i])));
		}
		
	}

	
	public void checkLevelUp()
	{
		if(expObj == null)
		{
			expObj = getExpByLevel(level+1);

			if(expObj == null)
			{
				PetExp lastExpObj = getExpByLevel(level);
				experience = lastExpObj.total;
				requireExp = 0;
				return;
			}
		
			if(requireExp == 0 && level != 1)
			{
				requireExp = expObj.levelExp;
			}
		}
		
		if(level > OVERLEVEL)
		{
			int overLevel = getOverLevel();
			if(level > overLevel)
			{
				level = overLevel;
				expObj = null;
				PetExp lastExpObj = getExpByLevel(level);
				experience = lastExpObj.total;
				requireExp = 0;
				return;
			}
		}

		if(expObj.checkIsLevelUp(this))
		{
			levelUp();
			checkLevelUp();
		}
	}
	
	public PetExp getExpByLevel(int lv)
	{
		Map expMap = null;
		if(petType == Pet.PUTONGPET)
		{
			expMap = (Map)DataFactory.getInstance().getAttachment(DataFactory.ATTACH_PET_EXP);
		}
		else if(petType == Pet.SHOUHUPET)	
		{
			expMap = (Map)DataFactory.getInstance().getAttachment(DataFactory.BATTLE_PET_EXP);
		}
		return (PetExp)expMap.get(lv);
	}
	
	private void levelUp()
	{
		level ++;
		expObj = getExpByLevel(level+1);
		
		if(expObj == null)
			return;
		
		requireExp += expObj.levelExp;
	}
	
	
	/**
	 * 宠物主人
	 * @return
	 */
	public Player getMaster()
	{
		return this.player;
	}
	
	public PetExp getExpObj()
	{
		return this.expObj;
	}
	
	public void setExpObj(PetExp obj)
	{
		this.expObj = obj;
	}
	
	
	private void setPetInfoToPlayer(String type,int point)
	{
		if(type.equals("lifePoint") || type.equals("life"))
			player.maxHitPoint += point;
		else if(type.equals("magicPoint") || type.equals("magic"))
			player.maxMagicPoint += point;
		else
		{
			PlayerBaseInfo base = player.getBaseInfo();
			int pPoint = Integer.parseInt(base.getVariable(type));
			pPoint += point;
			base.setVariable(type, String.valueOf(pPoint));
			base.updateLifeValue(point, type);
		}

		ReviseBaseInfo rbi = player.getBaseInfo().getReviseBaseInfo();
		if(rbi != null) 
		{
			player.getBaseInfo().getReviseBaseInfo().updateData(type, point,player.level);
		}
	}
	
	/**
	 * 把宠物的基本属性加到玩家身上
	 * type (1表示加，0表示减)
	 */
	public void setPlayerBaseInfo(int type)
	{
		if(attStr.isEmpty())
			return;
		PlayerBaseInfo base = player.getBaseInfo();
		if(type == 1)
		{
			String[] strs = Utils.split(attStr, ":");
			for (int i = 0; i < strs.length; i++) 
			{
				if(strs[i].equals("lifePoint") || strs[i].equals("magicPoint"))
					continue;
				int point = Integer.parseInt(getVariable(strs[i]));
				if(point > 0)
				{
					int pPoint = Integer.parseInt(base.getVariable(strs[i]));
					pPoint += point;
					base.setVariable(strs[i], String.valueOf(pPoint));
				}
			}
		}
		else if(type == 0)
		{
			String[] strs = Utils.split(attStr, ":");
			for (int i = 0; i < strs.length; i++) 
			{
				if(strs[i].equals("lifePoint") || strs[i].equals("magicPoint"))
					continue;
				int point = Integer.parseInt(getVariable(strs[i]));
				if(point > 0)
				{
					int pPoint = Integer.parseInt(base.getVariable(strs[i]));
					if(pPoint > point)
					{
						pPoint -= point;
						base.setVariable(strs[i], String.valueOf(pPoint));
					}
				}
			}
		}
	}
	
	/**
	 * 把生命和精力加到玩家身上(最大生命)
	 * @param type 1表示加 0表示减
	 */
	public void setPlayerOtherValue(int type)
	{
		if(type == 1)
		{
			if(lifePoint > 0)
			{
				player.maxHitPoint += lifePoint;
				player.maxMagicPoint += magicPoint;
			}
		}
		else if(type == 0)
		{
			if(player.maxHitPoint > lifePoint)
				player.maxHitPoint -= lifePoint;
			if(player.maxMagicPoint > magicPoint)
				player.maxMagicPoint -= magicPoint;
		}
	}
	
	/**
	 * 设置喂养次数
	 */
	public void setFeed()
	{
		if(feedTime == 0)
		{
			feedTime = WorldManager.currentTime;
			feedCount++;
			return;
		}
		SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd");
		String feedTimeStr = time.format(feedTime);
		String nowTimeStr = time.format(WorldManager.currentTime);
		if(feedTimeStr.equals(nowTimeStr))//同一天
		{
			if(feedCount < maxFeedCount)
				feedCount++;
		}
		else
		{
			feedTime = WorldManager.currentTime;
			feedCount = 1;
		}
	}
	
	/**
	 * 检测宠物今天是否还可以喂养(喂养次数是否已经到达最大)
	 * @return true表示还可以喂养，false表示已经达到最大喂养次数
	 */
	public boolean isFeed()
	{
		if(feedTime == 0)
		{
			return true;
		}
		
		SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd");
		String feedTimeStr = time.format(feedTime);
		String nowTimeStr = time.format(WorldManager.currentTime);
		if(feedTimeStr.equals(nowTimeStr))//同一天
		{
			if(feedCount >= maxFeedCount)
			{
				return false;
			}
			else
			{
				return true;
			}
		}
		else
		{
			return true;
		}
	}
	
	/**
	 * 检测宠物今天是否还可以出行(出行次数是否已经到达最大)
	 * @return true表示还可以出行，false表示已经达到最大出行次数
	 */
	public boolean isTrain()
	{
		if(firstTime == 0)
		{
			return true;
		}
		
		SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd");
		String feedTimeStr = time.format(firstTime);
		String nowTimeStr = time.format(WorldManager.currentTime);
		if(feedTimeStr.equals(nowTimeStr))//同一天
		{
			if(activePoint <= 0)
			{
				return false;
			}
			else
			{
				return true;
			}
		}
		else
		{
			return true;
		}
	}
	
	/**
	 * 宠物出行
	 * @param type 1.嬉戏 2.锻炼 3.探险
	 */
	public boolean petTrain(PlayerController target,int type)
	{
//		System.out.println("出行前：宠物出行时间："+trainTime+" 出行状态："+trainState+"  剩下活跃度:"+activePoint);
		if(trainState != 0)
		{
			return false;
		}

		if(!isTrain())
		{
			target.sendAlert(ErrorCode.ALERT_RUNNING_PET_COUNT_OVER);
			return false;
		}
		if(type != 1 && type != 2 && type != 3 && type != 4)
		{
			return false;
		}
		
		StringBuffer sb = null;
		if(type == 1)
		{
			if(isMaxInti(1))
			{
				sb = new StringBuffer();
				sb.append(DC.getString(DC.PLAYER_36));//你的宠物亲密度已达最大值
				target.sendGetGoodsInfo(1,false,sb.toString());
				return false;
			}
		}
		else if(type == 2)
		{
			if(isMaxInti(2))
			{
				sb = new StringBuffer();
				sb.append(DC.getString(DC.PLAYER_40));//你的宠物等级已达最大值
				target.sendGetGoodsInfo(1,false,sb.toString());
				return false;
			}
		}
		
		trainState = type;
		
		if(firstTime == 0 && trainTime == 0 && activePoint > 0)
		{
			trainTime = WorldManager.currentTime;
			activePoint--;
			firstTime = trainTime;
			return true;
		}
		SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd");
		String trainTimeStr = time.format(firstTime);
		String nowTimeStr = time.format(WorldManager.currentTime);

		if(trainTimeStr.equals(nowTimeStr))//同一天
		{
			if(activePoint > 0 && trainTime == 0)
			{
				activePoint--;
				trainTime = WorldManager.currentTime;
			}
		}
		else
		{
			trainTime = WorldManager.currentTime;
			firstTime = trainTime;
			activePoint = maxActivePoint - 1;
		}
		
//		System.out.println("出行后：宠物出行时间："+trainTime+" 出行状态："+trainState+"  剩下活跃度:"+activePoint);
		return true;
	}
	
	/**
	 * 出行完成
	 * type 1正常结束 2强制结束
	 */
	public void trainOver(PlayerController target,int type)
	{
		if(trainState == 0)
			return; 	
		
		if(trainTime == 0)
			return;	
		
		if(trainState == 4)
		{
			return;
		}

		PetTrain pt = getPetTrain();

		if(pt == null)
			return;

		if(type == 1)
		{
			long t = WorldManager.currentTime - trainTime;
			if( t < gameTime)
			{
				ByteBuffer buffer = new ByteBuffer();
				buffer.writeInt((int) (gameTime-t));
				target.getNetConnection().sendMessage(new SMsg(SMsg.S_PLAYER_PET_DOWMTIME_COMMAND,buffer));
				return;
			}
		}
		else if(type == 2)
		{
			Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
			if(bag.money < OVERMONEY)
			{
				target.sendAlert(ErrorCode.ALERT_SHOP_NO_MONEY);
				return;
			}
			bag.money -= OVERMONEY;
			bag.sendAddGoods(target, null);
		}
		else
			return;
	
		if(trainState <= 0 || trainState >= 4)
			return;
		
		String trainStr = "";
		if(trainState == 1)
			trainStr = DC.getString(DC.PET_1);
		else if(trainState == 2)
			trainStr = DC.getString(DC.PET_2);
		else if(trainState == 3)
			trainStr = DC.getString(DC.PET_3);
		
		long inti = intimacyPoint;
		int si = pt.strInt[trainState-1];
		int min = (int) (si * TRAINMIN);
		int max = (int) (si * TRAINMAX);
		int result = (int) (Math.random() * (max - min) + min);
		setInti(result,target);
		long nowInti = intimacyPoint - inti;
		StringBuffer sb = null;
		if(nowInti > 0)
		{
			target.sendGetGoodsInfo(1,false, DC.getPetTrainString(trainStr, DC.getString(DC.PET_6), nowInti+""));
		}
		
		long exp = experience;
		si = pt.exeExp[trainState-1];
		min = (int) (si * TRAINMIN);
		max = (int) (si * TRAINMAX);
		result = (int) (Math.random() * (max - min) + min);
		boolean flag = target.addPetExp(result);
		long nowExp = experience - exp;
		if(nowExp > 0 && flag)
		{
			target.sendGetGoodsInfo(1,false, DC.getPetTrainString(trainStr, DC.getString(DC.PET_7), nowExp+""));
		}		
		
		if(trainState == 3)
		{
			Mail mail = new Mail(DC.getString(DC.PET_8));
			mail.setTitle(trainStr);
			if(sb != null)
				mail.setContent(DataFactory.getInstance().getPetWords(trainState)+sb.toString());
			else
				mail.setContent(DataFactory.getInstance().getPetWords(trainState));
			
			int dropId = pt.dropId[trainState-1];

			if(dropId != 0)
			{
				BoxDropProp bdp = (BoxDropProp)DataFactory.getInstance().getBDP(dropId);
				if(bdp != null)
				{
					Goods goods = bdp.getGoods(true);
					if(goods != null)
					{
						mail.addAttach(goods);
					}
				}
			}
			mail.send(target);
		}
		
		trainState = 0;
		trainTime = 0;
		
//		System.out.println("掉线后经验："+(nextExp-requireExp)+"  掉线后亲密度："+intimacyPoint);
//		System.out.println("掉线：出行状态："+trainState+"  出行时间："+trainTime+"  得到经验或者亲密度："+result);

	}
	
	
	
	/**
	 * 宠物变身
	 * @param target
	 * @param beforeLevel
	 */
	public void changePetModel(PlayerController target,int beforeLevel)
	{
		if(level <= beforeLevel)
			return;
		if(upMap == null)
			return;
//		System.out.println("之前等级 ："+beforeLevel+"  现在等级："+level);
		for (int i = beforeLevel+1; i <= level; i++) 
		{
			PetUpRule pur = (PetUpRule) upMap.get(i);
//			System.out.println(i+"-----"+pur.level+"  ---  "+pur.upState);
			if(pur == null)
				continue;
			if(pur.upState == 0)
				continue;
			PetExp pe = getExpByLevel(i);
			if(pe == null)
				continue;
//			System.out.println(pe.level+"当前总经验："+experience+"  变身需要经验： "+pe.total+"  总亲密度："+intimacyPoint+"  需要亲密度："+pe.totalInt);
			if(!checkPetUp(pe,pur,i,target))
				continue;
		}
	}
	
	
	public boolean isUpById(String upId)
	{
		if(upId.equals("0"))
			return false;
		return true;
	}
	
	public boolean checkPetUp(PetExp pe,PetUpRule pur,int i,PlayerController target)
	{
		if(pur.upState == 0)
			return false;
		if(experience >= pe.total && intimacyPoint >= pe.totalInt)
		{
			if(pur.upState == 1)
			{
//				System.out.println("变身前形象："+modelId);
				String[] ways = Utils.split(pur.upWay, ":");
				String[] ums = Utils.split(pur.upModelId, ":");
				String[] atts = Utils.split(pur.upAtt, ":");
				if(ways.length == 1 && ums.length == 1 && atts.length == 1)
				{
					if(!modelWay.equals(pur.upWay))
					{
						modelId = Integer.parseInt(pur.upModelId);
						modelWay = pur.upWay;
						setAttStr(pur.upAtt);
					}
//					conState = 0;
				}
				else if(ways.length > 1 && ums.length > 1 && ways.length == ums.length)
				{
					String[] ws = Utils.split(pur.upWay, "|");
					String[] us = Utils.split(pur.upModelId, "|");
					String[] as = Utils.split(pur.upAtt, "|");
					if(ws.length == 1 && ways.length == 2 && ums.length == 2)
					{
						if(modelWay.equals(ways[0]))
						{
							if(isUpById(ums[0]))
							{
								modelId = Integer.parseInt(ums[0]);
								String[] strs = Utils.split(atts[0], ",");
								for (int k = 0; k < strs.length; k++) 
								{
									setAttStr(strs[k]);
								}
							}
						}
						else if(modelWay.equals(ways[1]))
						{
							if(isUpById(ums[1]))
							{
								modelId = Integer.parseInt(ums[1]);
								String[] strs = Utils.split(atts[1], ",");
								for (int k = 0; k < strs.length; k++) 
								{
									setAttStr(strs[k]);
								}
							}
						}
//						conState = 0;
					}
					else if(ws.length > 1 && us.length > 1 && ws.length == us.length && ws.length == 2)
					{
						for (int j = 0; j < us.length; j++) 
						{
							String[] oWays = Utils.split(us[j], ":");
							String[] oUms = Utils.split(ws[j], ":");
							String[] oAtts = Utils.split(as[j], ":");
							if(oWays.length == 2 && oUms.length == 2)
							{
								if(modelWay.equals(oWays[0]))
								{
									if(isUpById(oUms[0]))
									{
										modelId = Integer.parseInt(oUms[0]);
										String[] strs = Utils.split(oAtts[0], ",");
										for (int k = 0; k < strs.length; k++) 
										{
											setAttStr(strs[k]);
										}
									}
									break;
								}
								else if(modelWay.equals(oWays[1]))
								{
									if(isUpById(oUms[1]))
									{
										modelId = Integer.parseInt(oUms[1]);
										String[] strs = Utils.split(oAtts[1], ",");
										for (int k = 0; k < strs.length; k++) 
										{
											setAttStr(strs[k]);
										}
									}
									break;
								}
								break;
							}
							else
							{
								System.out.println("Pet 894 error:"+us[j]+"  "+ws[j]);
								return false;
							}
						}
					}
					else
					{
						System.out.println("Pet 901:pur.upWay:"+pur.upWay+"  pur.upModelId:"+pur.upModelId);
						return false;
					}
				}
				else
				{
					System.out.println("Pet 907:pur.upWay:"+pur.upWay+"  pur.upModelId:"+pur.upModelId);
					return false;
				}
//				System.out.println("++++++++++++变身后形象："+modelId+"  变身后等级："+i);
				target.sendPetModel(2,modelId,name);
			}
			else if(pur.upState == 2)
			{
//				System.out.println("//////////////////////////////////等级："+i);
				String[] ways = Utils.split(pur.upWay, ":"); 
				String[] ums = Utils.split(pur.upModelId, ":");
				String[] atts = Utils.split(pur.upAtt, ":");
				if(ways.length == 2 && ums.length == 2)
				{
					if(modelWay.length() < ways[0].length())
					{
						sendChangeModel(Integer.parseInt(ums[0]),Integer.parseInt(ums[1]),pur.level,atts[0],atts[1],target);
//						setTmpPetInfo(i);
						setDefault(Integer.parseInt(ums[0]),Integer.parseInt(ums[1]),pur.level,atts[0],atts[1]);
//						System.out.println(" 现在等级等级等级："+level);
						return false;
					}
				}
				else if(ways.length > 2 && ums.length > 2)
				{
					String[] ws = Utils.split(pur.upWay, "|");
					String[] us = Utils.split(pur.upModelId, "|");
					String[] ua = Utils.split(pur.upAtt, "|");
					if(ws.length == 2 && us.length == 2)
					{
						for (int k = 0; k < us.length; k++) 
						{
							String[] oWays = Utils.split(ws[k], ":");
							String[] oUms = Utils.split(us[k], ":");
							String[] oAtts = Utils.split(ua[k], ":");
							if(oWays.length == 2 && oUms.length == 2)
							{
								if(modelWay.equals(oWays[0].substring(0, oWays[0].length()-1)))
								{
									if(modelWay.length() < oWays[0].length())
									{
										sendChangeModel(Integer.parseInt(oUms[0]),Integer.parseInt(oUms[1]),pur.level,oAtts[0],oAtts[1], target);
//										setTmpPetInfo(i);
										setDefault(Integer.parseInt(oUms[0]),Integer.parseInt(oUms[1]),pur.level,oAtts[0],oAtts[1]);
										return false;
									}
								}
							}
							else
							{
								System.out.println("Pet error:"+ws[k]+"  "+us[k]);
								return false;
							}
						}
					}
					return false;
				}	
				else
				{
					System.out.println("Pet error:"+pur.upWay+"  "+pur.upModelId);
					return false;
				}
			}
		}
		return true;
	}
	
	
	
	
	private void setDefault(int id1,int id2,int level,String att1,String att2)
	{
		if(cmi1 == 0)
			cmi1 = id1;
		if(cmi2 == 0)
			cmi2 = id2;
		if(tmpLevel == 0)
			tmpLevel = level;
		if(!att1.isEmpty())
			this.att1 = att1;
		if(!att2.isEmpty())
			this.att2 = att2;
	}
	
	public void sendChangeModel(int id1,int id2,int level,String att1,String att2,PlayerController target)
	{
		ByteBuffer buffer = new ByteBuffer();
		buffer.writeByte(4);//通知客户端变身形象
		buffer.writeInt(id1);
		buffer.writeUTF(att1);
		buffer.writeInt(id2);
		buffer.writeUTF(att2);
		buffer.writeInt(level);
		PetExp exp = getExpByLevel(level);
		if(exp == null)
			return;
		buffer.writeUTF(exp.totalInt+"");
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_PLAYER_PETINFO_OPTION_COMMAND,buffer));
		
		target.isPetUp = true;
	}


	
	public void setGrow()
	{
		fixPutongPetGrow();
		if(attStr.isEmpty())
		{
			return;
		}
		
		String[] strs = Utils.split(attStr, ":");
		for (int i = 0; i < strs.length; i++) 
		{
			int bPoint = Integer.parseInt(getVariable(strs[i]));
			int point = level * growPoint / 2;
			setVariable(strs[i], String.valueOf(point));
			setPetInfoToPlayer(strs[i],point-bPoint);
			
		}
	}
	

	
	public void initPetExpAndInt()
	{
		cmi1 = 0;
		cmi2 = 0;
		tmpLevel = 0;
	}
	
	public int checkLevel = 1;
	
	public void setInti(long inti,PlayerController target)
	{
		if(upMap == null)
			return;
		long maxInti = getMaxInti(1);
		if(intimacyPoint == maxInti)
			return;
		long intim = intimacyPoint;
		intimacyPoint += inti;
		
		if(intimacyPoint > maxInti)
			intimacyPoint = maxInti;
		
		for (int i = checkLevel; i < getUpMapSize(); i++) 
		{
			PetExp pe = getExpByLevel(i);
			if(upMap != null && pe != null)
			{
				if(intimacyPoint < pe.totalInt || experience < pe.total)
				{
					checkLevel = pe.level;
					break;
				}
				PetUpRule pur = (PetUpRule) upMap.get(i);
				if(pur == null)
					continue;
	
				if(intim < pe.totalInt)
					checkInti(pe,pur,target);
			}
		}
		if(inti > 0)
			target.sendPetInfo(target, target.getID());
	}
	
	
	public void checkInti(PetExp pe,PetUpRule pur,PlayerController target)
	{
		if(pur.upState == 0)
			return;
//		System.out.println(intimacyPoint+"  "+pe.totalInt+"  "+modelWay);
		if(experience >= pe.total && intimacyPoint >= pe.totalInt)
		{
			if(pur.upState == 1)
			{
//				System.out.println("变身前形象："+modelId);
				String[] ways = Utils.split(pur.upWay, ":");
				String[] ums = Utils.split(pur.upModelId, ":");
				String[] atts = Utils.split(pur.upAtt, ":");
				if(ways.length == 1 && ums.length == 1 && atts.length == 1)
				{
//					System.out.println(modelWay+"  1323  "+pur.upWay);
					if(!modelWay.equals(pur.upWay))
					{
						modelId = Integer.parseInt(pur.upModelId);
						modelWay = pur.upWay;
						setAttStr(pur.upAtt);
					}
//					conState = 0;
				}
				else if(ways.length > 1 && ums.length > 1 && ways.length == ums.length)
				{
					String[] ws = Utils.split(pur.upWay, "|");
					String[] us = Utils.split(pur.upModelId, "|");
					String[] as = Utils.split(pur.upAtt, "|");
					if(ws.length == 1 && ways.length == 2 && ums.length == 2)
					{
						if(modelWay.equals(ways[0]))
						{
							if(isUpById(ums[0]))
							{
								modelId = Integer.parseInt(ums[0]);
								String[] strs = Utils.split(atts[0], ",");
								for (int k = 0; k < strs.length; k++) 
								{
									setAttStr(strs[k]);
								}
							}
						}
						else if(modelWay.equals(ways[1]))
						{
//							System.out.println(modelId+"    1355     "+ums[1]);
							if(isUpById(ums[1]))
							{
								modelId = Integer.parseInt(ums[1]);
								String[] strs = Utils.split(atts[1], ",");
								for (int k = 0; k < strs.length; k++) 
								{
									setAttStr(strs[k]);
								}
							}
						}
					}
					else if(ws.length > 1 && us.length > 1 && ws.length == us.length && ws.length == 2)
					{
						for (int j = 0; j < us.length; j++) 
						{
							String[] oWays = Utils.split(us[j], ":");
							String[] oUms = Utils.split(ws[j], ":");
							String[] oAtts = Utils.split(as[j], ":");
							if(oWays.length == 2 && oUms.length == 2)
							{
								if(modelWay.equals(oWays[0]))
								{
//									System.out.println(modelId+"  1379 "+oUms[0]);
									if(isUpById(oUms[0]))
									{
										modelId = Integer.parseInt(oUms[0]);
										String[] strs = Utils.split(oAtts[0], ",");
										for (int k = 0; k < strs.length; k++) 
										{
											setAttStr(strs[k]);
										}
									}
									break;
								}
								else if(modelWay.equals(oWays[1]))
								{
//									System.out.println(modelId+"  1394 "+oUms[1]);
									if(isUpById(oUms[1]))
									{
										modelId = Integer.parseInt(oUms[1]);
										String[] strs = Utils.split(oAtts[1], ",");
										for (int k = 0; k < strs.length; k++) 
										{
											setAttStr(strs[k]);
										}
									}
									break;
								}
								break;
							}
							else
							{
								System.out.println("Pet 894 error:"+us[j]+"  "+ws[j]);
								return;
							}
						}
					}
					else
					{
						System.out.println("Pet 901:pur.upWay:"+pur.upWay+"  pur.upModelId:"+pur.upModelId);
						return;
					}
				}
				else
				{
					System.out.println("Pet 907:pur.upWay:"+pur.upWay+"  pur.upModelId:"+pur.upModelId);
					return;
				}
				target.checkPetExp(this);
//				System.out.println("++++++++++++变身后形象："+modelId+"  变身后等级："+i);
				target.sendPetModel(2,modelId,name);

			}
			else if(pur.upState == 2)
			{
//				System.out.println("//////////////////////////////////等级："+i);
				String[] ways = Utils.split(pur.upWay, ":"); 
				String[] ums = Utils.split(pur.upModelId, ":");
				String[] atts = Utils.split(pur.upAtt, ":");
				if(ways.length == 2 && ums.length == 2)
				{
					if(modelWay.length() < ways[0].length())
					{
						sendChangeModel(Integer.parseInt(ums[0]),Integer.parseInt(ums[1]),pur.level,atts[0],atts[1],target);
						setDefault(Integer.parseInt(ums[0]),Integer.parseInt(ums[1]),pur.level,atts[0],atts[1]);
						return;
					}
//					System.out.println(" 现在等级等级等级："+level);
				}
				else if(ways.length > 2 && ums.length > 2)
				{
					String[] ws = Utils.split(pur.upWay, "|");
					String[] us = Utils.split(pur.upModelId, "|");
					String[] ua = Utils.split(pur.upAtt, "|");
					if(ws.length == 2 && us.length == 2)
					{
						for (int k = 0; k < us.length; k++) 
						{
							String[] oWays = Utils.split(ws[k], ":");
							String[] oUms = Utils.split(us[k], ":");
							String[] oAtts = Utils.split(ua[k], ":");
							if(oWays.length == 2 && oUms.length == 2)
							{
								if(modelWay.equals(oWays[0].substring(0, oWays[0].length()-1)))
								{
									if(modelWay.length() < oWays[0].length())
									{
										sendChangeModel(Integer.parseInt(oUms[0]),Integer.parseInt(oUms[1]),pur.level,oAtts[0],oAtts[1], target);
										setDefault(Integer.parseInt(oUms[0]),Integer.parseInt(oUms[1]),pur.level,oAtts[0],oAtts[1]);
										return;
									}
								}
							}
							else
							{
								System.out.println("Pet error:"+ws[k]+"  "+us[k]);
								return;
							}
						}
					}
					return;
				}	
				else
				{
					System.out.println("Pet error:"+pur.upWay+"  "+pur.upModelId);
					return;
				}
			}
		}
	}

	public void setAttStr(String att)
	{
		if(petType == Pet.SHOUHUPET)
		{
			if(attStr.length() == 0)
				attStr += att;
			else 
				attStr += "|" + att;
		}
		else
		{
			if(attStr.isEmpty())
				attStr += att;
			else
			{
				if(attStr.indexOf(att) == -1)
					attStr += ":" + att;
			}
		}
	}
	
	private void initVariable(String att)
	{
		if(petType == Pet.SHOUHUPET)
			return;
		
		if(att.equals("power"))
		{
			if(power <= 0)
				return;
		}
		else if(att.equals("nimble"))
		{
			if(nimble <= 0)
				return;
		}
		else if(att.equals("wisdom"))
		{
			if(wisdom <= 0)
				return;
		}
		else if(att.equals("spirit"))
		{
			if(spirit <= 0)
				return;
		}
		else if(att.equals("lifePoint"))
		{
			if(lifePoint <= 0)
				return;
		}
		else if(att.equals("magicPoint"))
		{
			if(magicPoint <= 0)
				return;
		}
		
		setAttStr(att);
	}
	
	public boolean setVariable(String key,String value)
	{
		if(key.equals("growBasic"))
		{
			if("0".equals(value))
				growBasic = "";
			else
				growBasic = value;
			return true;
		}
		else if(key.equals("growPoint"))
		{
			growPoint = Integer.parseInt(value);
			return true;
		}
		else if(key.equals("power"))
		{
			power = Integer.parseInt(value);
			initVariable("power");
			return true;
		}
		else if(key.equals("agility") || key.equals("nimble"))
		{
			nimble = Integer.parseInt(value);
			initVariable("nimble");
			return true;
		}
		else if(key.equals("wisdom"))
		{
			wisdom = Integer.parseInt(value);
			initVariable("wisdom");
			return true;
		}
		else if(key.equals("spirit"))
		{
			spirit = Integer.parseInt(value);
			initVariable("spirit");
			return true;
		}
		else if(key.equals("lifePoint"))
		{
			lifePoint = Integer.parseInt(value);
			initVariable("lifePoint");
			return true;
		}
		else if(key.equals("magicPoint"))
		{
			magicPoint = Integer.parseInt(value);
			initVariable("magicPoint");
			return true;
		}
		else if(key.equals("activePoint"))
		{
			maxActivePoint = Integer.parseInt(value);
			activePoint = maxActivePoint;
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
		else if(key.equals("lifePointExt") || key.equals("lifePointExt"))
		{
			lifePointExt.append(value);
			return true;
		}
		else if(key.equals("magicExt") || key.equals("magicPointExt"))
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
		else if(key.equals("nimbleExt") || key.equals("agilityExt"))
		{
			nimbleExt.append(value);
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

	
	
	public int getNextModelOne(PetExp pe,PetUpRule pur)
	{
		int result = 0;
		String[] ways = Utils.split(pur.upWay, ":");
		String[] ums = Utils.split(pur.upModelId, ":");

		if(ways.length == 1)
			result = Integer.parseInt(pur.upModelId);
		else
		{
			String[] ws = Utils.split(pur.upWay, "|");
			String[] us = Utils.split(pur.upModelId, "|");
			if(ws.length == 1 && ways.length == 2 && ums.length == 2)
			{
				if(modelWay.equals(ways[0]))
				{
					result = Integer.parseInt(ums[0]);
				}
				else if(modelWay.equals(ways[1]))
				{
					result = Integer.parseInt(ums[1]);
				}
			}
			else if(ws.length > 1 && us.length > 1 && ws.length == us.length && ws.length == 2)
			{
				for (int j = 0; j < us.length; j++) 
				{
					String[] oWays = Utils.split(us[j], ":");
					String[] oUms = Utils.split(ws[j], ":");
					if(oWays.length == 2 && oUms.length == 2)
					{
						if(modelWay.equals(oWays[0]))
						{
							result = Integer.parseInt(oUms[0]);
							break;
						}
						else if(modelWay.equals(oWays[1]))
						{
							result = Integer.parseInt(oUms[1]);
							break;
						}
					}
				}
			}
		}
		
		return result;
	}
	
	public int[] getNextModelTwo(PetExp pe,PetUpRule pur)
	{
		int[] result = new int[2];
		String[] ways = Utils.split(pur.upWay, ":"); 
		String[] ums = Utils.split(pur.upModelId, ":");
		if(ways.length == 2 && ums.length == 2)
		{
			result[0] = Integer.parseInt(ums[0]);
			result[1] = Integer.parseInt(ums[1]);
		}
		else if(ways.length > 2 && ums.length > 2)
		{
			String[] ws = Utils.split(pur.upWay, "|");
			String[] us = Utils.split(pur.upModelId, "|");
			if(ws.length == 2 && us.length == 2)
			{
				for (int k = 0; k < us.length; k++) 
				{
					String[] oWays = Utils.split(ws[k], ":");
					String[] oUms = Utils.split(us[k], ":");
					if(oWays.length == 2 && oUms.length == 2)
					{
						if(modelWay.equals(oWays[0].substring(0, oWays[0].length()-1)))
						{
							result[0] = Integer.parseInt(oUms[0]);
							result[1] = Integer.parseInt(oUms[1]);
							break;
						}
					}
				}
			}
		}
		
		return result;
	}
	
	
	public PetTrain getPetTrain()
	{
		List list = (List) DataFactory.getInstance().getAttachment(DataFactory.PET_TRAIN_LIST);
		for (int i = 0; i < list.size(); i++) 
		{
			PetTrain pt = (PetTrain) list.get(i);
			if(pt == null)
				continue;
			if(level >= pt.minLevel && level <= pt.maxLevel)
				return pt;
		}
		return null;
	}

	
	/**
	 * 亲密度或经验值是否加到最高
	 * @param type 1表示亲密度, 2表示经验值
	 * @return true就不能再加亲密度了 false则可以
	 */
	public boolean isMaxInti(int type)
	{
		if(type == 1)
			return getMaxInti(1) <= intimacyPoint;
		else if(type == 2)
			return getMaxInti(2) <= experience;
		else
			return true;
	}

	
	/**
	 * 取得最大经验值或亲密度
	 * @param type 1表示取亲密度 2表示取经验值
	 * @return
	 */
	public long getMaxInti(int type)
	{
		HashMap expMap = (HashMap)DataFactory.getInstance().getAttachment(DataFactory.ATTACH_PET_EXP);
		PetExp pe = (PetExp) expMap.get(expMap.size());
		if(pe == null)
			return 0;
		else
		{
			if(type == 1)
			{
				if(pe.totalInt > 0)
					return pe.totalInt;
				else
					return PetExp.DEFAULTMAXINTI;
			}
			else if(type == 2)
			{
				if(pe.total > 0)
					return pe.total;
				else
					return PetExp.DEFAULTMAXEXP;
			}
		}
		return 0;
	}

	
	public PetUpRule getNearUpRule()
	{
		if(upMap == null)
			return null;
		
		for (int i = level; i > 0; i--) 
		{
			PetUpRule pur = (PetUpRule) upMap.get(i);
			if(pur == null)
				continue;
			if(pur.upState > 0 && !pur.upModelId.equals("0:0"))
			{
				PetExp pe = getExpByLevel(i);
				if(pe == null)
					continue;
				if(intimacyPoint >= pe.totalInt)
				{
					return pur;
				}
			}
		}
		return null;
	}
	
	/**
	 * 转换宠物
	 */
	public void changePet()
	{
		Pet pet = (Pet) DataFactory.getInstance().getGameObject(id);
		
		cmi1 = 0;
		cmi2 = 0;
		att1 = "";
		att2 = "";
		level = 1;
		power = 0;
		nimble = 0;
		spirit = 0;
		wisdom = 0;
		attStr = "";
		nextExp = 0;
		modelId = pet.modelId;
		feedTime = 0;
		modelWay = "a";
		tmpLevel = 0;
		trainTime = 0;
		firstTime = 0;
		feedCount = 0;
		trainState = 0;
		experience = 0;
		requireExp = getInitExp();
		activePoint = pet.activePoint;
		intimacyPoint = 0;
		
		expObj = null;
	}
	
	public void clear()
	{
		activePoint = maxActivePoint;
		feedCount = 0;
		feedTime = 0;
		isHuifuTili = false;
		for (int i = 0; i < petSkills.length; i++)
		{
			if(petSkills[i] != null)
				petSkills[i].studyCount = 0;
		}
	}
	
	
	/**
	 * 合区时候用到重新生成宠物流水号
	 * @param objectindex 宠物蛋和宠物的流水
	 * @param o 宠物蛋本身的流水
	 */
	public void ObjectIndex(Player player,long objectindex,long o)
	{	
		Bag bag = (Bag)player.getExtPlayerInfo("bag");
		GoodsProp goods = (GoodsProp) bag.getPetEgg(this.objectIndex);
		if(goods == null)
			return;
		this.objectIndex = objectindex;
		goods.petIndex = objectIndex;
		goods.objectIndex = o;
	}
	
	/*****************************************************************************************/
	/**
	 * 取得宠物的状态(是否转生的)
	 * @return
	 */
	public int getUpState()
	{
		if(job <= 100)
			return 0;
		else if(job <= 200)
			return 1;
		else if(job <= 300)
			return 2;
		else if(job <= 400)
			return 3;
		return 0;
	}
	
	/** 取宠物职业类型 */
	public int getJobType()
	{
		return 1;
	}
	
	public List getJobSkills()
	{
		List list = DataFactory.getInstance().getPetSkillList();
		List skillList = new ArrayList();
		for (int i = 0; i < list.size(); i++) 
		{
			PetSkill petSkill = (PetSkill) list.get(i);
			for (int j = 0; j < petSkill.petJob.length; j++)
			{
				if(petSkill.petJob[0] == 0)
				{
					skillList.add(petSkill);
					break;
				}
				if(petSkill.petJob[j] == 0)
					continue;
				if(petSkill.petJob[j] == job)
				{
					skillList.add(petSkill);
					break;
				}
			}
		}
		return skillList;
	}
	
	public void updateJobSkills()
	{
		Object obj = jobSkillMap.get(job);
		if(obj != null)
		{
			if(Integer.parseInt(obj.toString()) == petSkills.length)
				return;
		}
			
		HashMap stuMap = new HashMap();
		
		for(int i = 0 ; i < petSkills.length ; i ++)
		{
			stuMap.put(petSkills[i].id, petSkills[i]);
		}
		
	    
		Object[] objs = getJobSkills().toArray();
		
		PetSkill []skillsTmp = new PetSkill[objs.length];

		for (int i = 0; i < skillsTmp.length; i++)
		{
			PetSkill skill = (PetSkill)PetSkill.cloneObject((PetSkill)objs[i]);
			PetSkill petSkill = (PetSkill)stuMap.get(skill.id);
			if(petSkill == null)
				skillsTmp[i] = skill;
			else
				skillsTmp[i] = petSkill;
		}
		petSkills = skillsTmp;
	}

	public void initBaseSkill()
	{
		List skillList = getJobSkills();
		
		Object[] objs = skillList.toArray();
		
		petSkills = new PetSkill[objs.length];
		for (int i = 0; i < objs.length; i++)
		{
			petSkills[i] = (PetSkill)PetSkill.cloneObject((PetSkill)objs[i]);
		}
		
		String[] strs = Utils.split(initSkill, ":");
		
		for(int i = 0 ; i < strs.length ; i ++)
		{
			if("0".equals(strs[i]))
				continue;
			PetSkill skill = getPetSkillById(Integer.parseInt(strs[i]));
			if(skill == null)
			{
				System.out.println("Pet initBaseSkill skill is null:"+strs[i]);
				continue;
			}
			addPetSkill(skill);
		}

	}
	
	public void writeStudySkill(ByteBuffer byteBuffer)
	{
		int length = 0; 
		if(petSkills == null)
			length = 0;
		else
			length = petSkills.length;
		for (int i = 0; i < length; i++) 
		{
			if(petSkills[i].isStudied)
			{
				continue;
			}
			else
			{
				if(isPreLv(i,petSkills[i].level))
				{
					continue;
				}
			}

			byteBuffer.writeInt(petSkills[i].id);
			byteBuffer.writeInt(petSkills[i].iconId);
			byteBuffer.writeUTF(petSkills[i].name);
			byteBuffer.writeInt(petSkills[i].level);
			byteBuffer.writeBoolean(petSkills[i].isStudied);
			byteBuffer.writeBoolean(false);
			byteBuffer.writeByte(petSkills[i].targetType[0]);
			byteBuffer.writeInt(PetBattleTmp.CDTIMER);
			byteBuffer.writeByte(petSkills[i].type);
			if(petSkills[i] instanceof PetActiveSkill)
			{
				PetActiveSkill as = (PetActiveSkill)petSkills[i];
				byteBuffer.writeInt(as.magic);
				byteBuffer.writeUTF(as.petJob.toString());
				byteBuffer.writeBoolean(false);
			}
			else if(petSkills[i] instanceof PetPassiveSkill)
			{
				PetPassiveSkill ps = (PetPassiveSkill) petSkills[i];
				byteBuffer.writeInt(0);
				byteBuffer.writeUTF(ps.petJob.toString());
				byteBuffer.writeBoolean(false);
			}
			byteBuffer.writeInt(petSkills[i].order);
		}
	}
	
	public void writeSkill(ByteBuffer byteBuffer)
	{
		int length = 0; 
		if(petSkills == null)
			length = 0;
		else
			length = petSkills.length;
		int count = 0;
		for (int i = 0; i < length; i++) 
		{
			if(petSkills[i].isStudied)
			{
				if(isNextLv(i,petSkills[i].level))
				{
					continue;
				}
			}
			else
			{
				if(isPreLv(i,petSkills[i].level))
				{
					continue;
				}
			}
			count++;
		}
		byteBuffer.writeInt(count);
		for (int i = 0; i < length; i++) 
		{
			if(petSkills[i].isStudied)
			{
				if(isNextLv(i,petSkills[i].level))
				{
					continue;
				}
			}
			else
			{
				if(isPreLv(i,petSkills[i].level))
				{
					continue;
				}
			}

			byteBuffer.writeInt(petSkills[i].id);
			byteBuffer.writeInt(petSkills[i].iconId);
			byteBuffer.writeUTF(petSkills[i].name);
			byteBuffer.writeInt(petSkills[i].level);
			byteBuffer.writeBoolean(petSkills[i].isStudied);
			byteBuffer.writeByte(petSkills[i].targetType[0]);
			byteBuffer.writeInt(petSkills[i].CDTimer);
			if(petSkills[i] instanceof PetActiveSkill)
			{
				PetActiveSkill as = (PetActiveSkill)petSkills[i];
				byteBuffer.writeBoolean(false);
				byteBuffer.writeInt(as.magic);
				byteBuffer.writeUTF(as.petJob.toString());
			}
			else if(petSkills[i] instanceof PetPassiveSkill)
			{
				PetPassiveSkill ps = (PetPassiveSkill) petSkills[i];
//System.out.println(petSkills[i].id+"  name:"+petSkills[i].name+"  isJihuo:"+ps.dataType[0]+"  study:"+petSkills[i].isStudied+"  active:"+petSkills[i].isActive);
				if(ps.dataType[0] == 22)
					byteBuffer.writeBoolean(true);//能否激活
				else
					byteBuffer.writeBoolean(false);
				byteBuffer.writeInt(0);
				byteBuffer.writeUTF(ps.petJob.toString());
			}
			byteBuffer.writeByte(petSkills[i].skillType);
			byteBuffer.writeBoolean(petSkills[i].isActive);
			byteBuffer.writeInt(petSkills[i].order);
			if(!petSkills[i].isStudied)
			{
				byteBuffer.writeInt(petSkills[i].exp);
				PetSkillStudy pss = DataFactory.getInstance().getPetSkillStudyBySkill(petSkills[i]);//需要根据客户端传过来 的技能通过技能的skillType取得PetSkillStudy对象
				if(pss == null)
					byteBuffer.writeInt(0);
				else
					byteBuffer.writeInt(pss.needExp);
				byteBuffer.writeInt(petSkills[i].studyCount);
			}
			else
			{
				if(i < petSkills.length-1)
				{
					if(petSkills[i+1] != null && petSkills[i+1].skillType == petSkills[i].skillType)
					{
						byteBuffer.writeInt(petSkills[i+1].exp);
						PetSkillStudy pss = DataFactory.getInstance().getPetSkillStudyBySkill(petSkills[i+1]);//需要根据客户端传过来 的技能通过技能的skillType取得PetSkillStudy对象
						if(pss == null)
							byteBuffer.writeInt(0);
						else
							byteBuffer.writeInt(pss.needExp);
						byteBuffer.writeInt(petSkills[i+1].studyCount);
					}
					else
					{
						byteBuffer.writeInt(0);
						byteBuffer.writeInt(0);
						byteBuffer.writeInt(0);
					}
				}
				else
				{
					byteBuffer.writeInt(0);
					byteBuffer.writeInt(0);
					byteBuffer.writeInt(0);
				}
			}
		}
	}
	
	private boolean isPreLv(int index,int currlv)
	{
		try
		{
			if(index > 0)
			{
				if(petSkills[index-1].level < currlv)
					return true;
			}
		}
		catch(NullPointerException e)
		{
			return false;
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			return false;
		}
		return false;
	}

	private boolean isNextLv(int index,int currlv)
	{
		try
		{
			if(petSkills[index+1].level > currlv
					&& petSkills[index+1].isStudied)
				return true;
		}
		catch(NullPointerException e)
		{
			return false;
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			return false;
		}
		return false;
	}
	
	public PetSkill getPetStudySkillByType(int skillType)
	{ 
		PetSkill ps = null;
		for (int i = 0; i < petSkills.length; i++) 
		{
			if(petSkills[i] != null && petSkills[i].skillType == skillType)
			{
				if(petSkills[i].isStudied)
					continue;
				if(ps == null)
					ps = petSkills[i];
				else
				{
					if(ps.level > petSkills[i].level)
						ps = petSkills[i];
				}
			}
		}
		return ps;
	}
	
	public PetSkill getPetStudySkillById(int skillId)
	{ 
		PetSkill ps = null;
		for (int i = 0; i < petSkills.length; i++)
		{
			if(petSkills[i] != null && petSkills[i].id == skillId)
				ps = petSkills[i];
		}
		if(ps == null)
			return null;
		PetSkill ps1 = null;
		for (int i = 0; i < petSkills.length; i++) 
		{
			if(petSkills[i] != null && petSkills[i].skillType == ps.skillType)
			{
				if(petSkills[i].isStudied)
					continue;
				if(ps1 == null)
					ps1 = petSkills[i];
				else
				{
					if(ps1.level > petSkills[i].level)
						ps1 = petSkills[i];
				}
			}
		}
		return ps1;
	}
	
	public double getExpChangeMult()
	{
		double result = 0;
		for (int i = 0; i < ppss.length; i++) 
		{
			if(ppss[i].dataType[0] == 22)
				result += ppss[i].effectPoint[0];
		}
		return result / 10000;
	}
	
	public boolean isUseSkill(PetSkill skill)
	{
		if(skill instanceof PetActiveSkill)
		{
			for (int i = 0; i < pass.length; i++)
			{
				if(pass[i].skillType == skill.skillType && pass[i].level > skill.level)
					return false;
			}
		}
		else if(skill instanceof PetPassiveSkill)
		{
			return true;
		}
		return true;
	}
	
	public PetSkill getPetSkillById(int skillId)
	{
		for (int i = 0; i < petSkills.length; i++)
		{
			if(petSkills[i] != null && petSkills[i].id == skillId)
				return petSkills[i];
		}
		return null;
	}
	
	public PetActiveSkill getPetActiveSkillById(int skillId)
	{
		for (int i = 0; i < pass.length; i++)
		{
			if(pass[i] != null && pass[i].id == skillId)
				return pass[i];
		}
		return null;
	}
	
	public void addPetSkill(PetSkill skill)
	{
		skill.isStudied = true;
		skill.isStudying = false;
		skill.isActive = true;
		
		if(skill instanceof PetActiveSkill)
		{
			PetActiveSkill [] infos = new PetActiveSkill[pass.length+1];
			for (int i = 0; i < pass.length; i++)
				infos[i] = pass[i];
			infos[pass.length] = (PetActiveSkill)skill;
			pass = infos;
		}
		else if(skill instanceof PetPassiveSkill)
		{
			if(((PetPassiveSkill) skill).dataType[0] == 22)
				skill.isActive = false;
			
			PetPassiveSkill [] infos = new PetPassiveSkill[ppss.length+1];
			for (int i = 0; i < ppss.length; i++)
				infos[i] = ppss[i];
			infos[ppss.length] = (PetPassiveSkill)skill;
			ppss = infos;
		}
	}
	
	/**
	 * 是否是更低等级的技能没学
	 * @param skill
	 * @return
	 */
	public boolean isLowLevelNoStudy(PetSkill skill)
	{
		for (int i = 0; i < petSkills.length; i++) 
		{
			if(petSkills[i] == null)
				continue;
			if(petSkills[i].id == skill.id)
				continue;
			if(petSkills[i].skillType == skill.skillType)
			{
				if(petSkills[i].level < skill.level && !petSkills[i].isStudied)
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public PetSkill getStudyingSkill()
	{
		for (int i = 0; i < petSkills.length; i++)
		{
			if(petSkills[i] == null)
				continue;
			if(petSkills[i].isStudying)
				return petSkills[i];
		}
		return null;
	}
	
	/** 宠物学习技能 */
	public boolean studySkill(PlayerController target,PetSkill skill,PetSkillStudy pss)
	{
		if(petType == Pet.PUTONGPET)
		{
			return false;
		}
		if(trainState == 4)
		{
			target.sendAlert(ErrorCode.ALERT_PET_SKILL_IS_STUDYING);
			return false;
		}
		if(skill.isStudied)
		{
			target.sendAlert(ErrorCode.ALERT_PET_SKILL_IS_STUDIED);
			return false;
		}
		if(skill.isStudying)
		{
			target.sendAlert(ErrorCode.ALERT_PET_SKILL_IS_STUDYING);
			return false;
		}
		if(isLowLevelNoStudy(skill))
		{
			target.sendAlert(ErrorCode.ALERT_PET_HAVE_LOWERSKILL_STUDY);
			return false;
		}
		if(skill.studyCount == PetSkill.MONEYCOUNT)
		{
			Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
			if(bag.money < PetSkill.STUDYMOENY)
			{
				target.sendAlert(ErrorCode.ALERT_SHOP_NO_MONEY);
				return false;
			}
		}
		else if(skill.studyCount >= PetSkill.MAXSTUDYCOUNT)
		{
			target.sendAlert(ErrorCode.ALERT_PETSKILL_STUDYCOUNT_ERROR);
			return false;
		}
		//还需要检测上一等级技能是否学习过了，如果没有学习过则需要先学习上一级
		if(skill.exp >= pss.needExp)
		{
			skill.isStudied = true;
			skill.exp = pss.needExp;
		}
		if(!pss.isConditionEnough(target, this,skill))
		{
			return false;
		}
		pss.setSkillStudyCondition(target, this, skill);
		skill.isStudying = true;
		trainTime = WorldManager.currentTime;
		trainState = 4;
		if(skill.studyCount == PetSkill.MONEYCOUNT)
		{
			Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
			bag.money -= PetSkill.STUDYMOENY;
			bag.sendAddGoods(target, null);
		}
//System.out.println("技能："+skill.name+"lv"+skill.level+" 学习经验"+skill.exp+"/"+pss.needExp+" 此的技能学习次数:"+skill.studyCount+"/"+PetSkill.MAXSTUDYCOUNT);
		return true;
	}
	
	/**
	 * 学习能完成
	 * type 1正常结束 2强制结束
	 */
	public boolean studyEnd(PlayerController target,int type,PetSkill skill,PetSkillStudy pss)
	{	
		if(trainState <= 0 || trainState >= 5)
		{
			return false;
		}
		
		if(trainTime == 0)
		{
			return false;	
		}
		if(type == 1)
		{
			long t = WorldManager.currentTime - trainTime;
			if( t < pss.needTime)
			{
				ByteBuffer outBuffer = new ByteBuffer();
				outBuffer.writeByte(6);
				outBuffer.writeInt(id);
				outBuffer.writeInt(skill.skillType);
				outBuffer.writeInt((int) (pss.needTime-t));
				target.getNetConnection().sendMessage(new SMsg(SMsg.S_PLAYER_BATTLE_PET_BASE_COMMAND,outBuffer));
				return false;
			}
		}
		else if(type == 2)
		{
			Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
			Goods uGoods = bag.getGoodsByType(Goods.BATTLEPETSKILLENDGOODS);
			if(uGoods == null)
			{
				target.sendAlert(ErrorCode.ALERT_NOT_SKILL_FINISHI_CARD);
				return false;
			}
			else
			{
				bag.removeGoods(target, uGoods.objectIndex, 1);
			}
			
		}
		else
			return false;
		
		skill.studyCount++;
		addSkillExp(skill, skill.getRewardExp(), pss, target,1);
		trainState = 0;
		trainTime = 0;
		
		return true;
	}
	
	/**
	 * 
	 * @param skill
	 * @param exp
	 * @param pss
	 * @param target
	 * @param type 1修炼 2守护口粮
	 */
	public void addSkillExp(PetSkill skill,long exp,PetSkillStudy pss,PlayerController target,int type)
	{
		skill.exp += exp;
		skill.isStudying = false;

		/**
		 * 8、技能修炼完成以后，聊天要提示“**守护修炼**技能获得了**经验”。
		 * 如果技能升级了，要提示“**守护修炼**技能获得了**经验，技能提升至**级。”
		 */
		if(type == 1)
		{
			target.sendGetGoodsInfo(1, false, DC.getPetSkillStudyString(name, skill.name, exp, skill.level, false));
		}
		if(skill.exp >= pss.needExp)
		{
			skill.exp = pss.needExp;
			addPetSkill(skill);
			PetSkill ps1 = getPetStudySkillByType(skill.skillType);
			if(ps1 != null)
				ps1.studyCount = skill.studyCount;
			
			if(type == 1)
			{
				target.sendGetGoodsInfo(1, false, DC.getPetSkillStudyString(name, skill.name, exp, skill.level, true));
			}
		}
	}

	
	/**
	 * 洗点
	 * @param target
	 */
	public boolean flushGrowPoint(PlayerController target)
	{
		if(petType == Pet.PUTONGPET)
			return false;
		Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
		Goods goods = bag.getGoodsById(1045000343);
		if(goods == null)
		{
			target.sendAlert(ErrorCode.ALERT_NOT_FLUSHGROW_GOODS);
			return false;
		}
		bag.removeGoods(target, goods.objectIndex, 1);

		Pet pet = (Pet) DataFactory.getInstance().getGameObject(id);
	    UpRole ur = getUpRoleByPet();
	    int p = 0;
	    if(ur != null)
	    	p = ur.getPetRewardValue("growPoint");

		int pp = pet.growPoint + p;

		int[] grows = {pp,pp+10,pp+20,pp+30,pp+40,pp+50};
		growPoint = grows[(int) (Math.random()*grows.length)];
		attStr = "";
		if(growPoint > pp)//要出隐藏属性
		{
			String[] strs = {"phyAttExt","sptAttExt","powerExt","spiritExt","phyDefExt","sptDefExt",
			  "phySmiteParmExt","sptSmiteParmExt","phySmiteRateExt","sptSmiteRateExt",
			  "powerExt","nimbleExt","spiritExt","wisdomExt","lifePointExt","magicPointExt",
			  "noDefPhyHurtExt","noDefSptHurtExt","phyHurtAvoidExt","sptHurtAvoidExt",
			  "curePointExt","clearPhySmiteExt","clearSptSmiteExt","clearPhySmiteParmExt",
			  "clearSptSmiteParmExt","clearNoDefHurtExt"};
			
			List list = new ArrayList();
			for (int i = 0; i < strs.length; i++)
			{
				String pointStr = getVariable(strs[i]);
				if("".equals(pointStr) || "0".equals(pointStr))
					continue;
				list.add(strs[i]);
			}
			int leng = (int) (Math.random() * 5 + 1);
			for (int i = 0; i < leng; i++)
			{
				int random = (int) (Math.random() * list.size());
				String pointStr = getVariable(list.get(random).toString());
				String[] values = Utils.split(pointStr, ":");
				int min = Integer.parseInt(values[0]);
				int max = Integer.parseInt(values[1]);
				int point = (int) (Math.random() * (max - min) + min);
				String attStr = list.get(random).toString().substring(0,list.get(random).toString().indexOf("Ext"));
				setAttStr(attStr+":"+point);
			}
		}

		return true;
	}
	
	public int getOverLevel()
	{
		int state = getUpState();
		int overLevel = OVERLEVEL;
		if(state != 0)
		{
			UpRole ur = getUpRoleByPet();
			if(ur != null)
			{
				overLevel = ur.overLevel;
			}
		}
		return overLevel;
	}
	
	public void addBattlePetExp(PlayerController target,long exp)
	{
		if(petType == Pet.PUTONGPET)
			return;
		if(exp <= 0)
			return;
		if(isMaxBattlePoint())
			return;
		long disExp = exp;
		int jobType = getUpState();
		int playerJobType = target.getPlayer().getZhuanshengState();
		if(jobType > playerJobType)
			return;
		if(jobType == playerJobType)
		{
			if(level >= target.getPlayer().level)
				return;
		}
		int overLevel = getOverLevel();
		if(requireExp == 0 && level == overLevel)
			return;
		if(jobType == 1)
			exp *= 0.85;
		else if(jobType == 2)
			exp *= 0.7;
		
		int lev = level;

		if(requireExp == 0)
		{
			PetExp pe = getExpByLevel(level+1);
			requireExp = pe==null?0:pe.levelExp;
		}
		if(requireExp == 0)
			return;
		
		experience += exp;
		requireExp -= exp;
		
		checkLevelUp();
		//发送最新的守护信息
	
//		changePetModel(target, lev);
		target.sendGetGoodsInfo(1, false, DC.getString(DC.PET_9)+":"+disExp);
		if(level > lev)
		{
			nextExp = expObj == null ? 0 : expObj.levelExp;
			if(level > target.getPlayer().level && jobType >= playerJobType)
			{
				PetExp lastExpObj = getExpByLevel(target.getPlayer().level);
				experience = lastExpObj.total;
				requireExp = 0;
				level = target.getPlayer().level;
				lastExpObj = getExpByLevel(level+1);
				nextExp = lastExpObj == null ? 0 : lastExpObj.levelExp;
				expObj = null;
			}
			if(level > lev)
			{
				//你的守护升级了
				target.sendGetGoodsInfo(1,false, DC.getString(DC.PET_10));
			}
			target.sendAlwaysValue();
		}
		
		sendBattlePetInfo(target, 1,target.getID());
	}
	
	public void writeBattlePetTo(ByteBuffer buffer)
	{
		buffer.writeInt(id);
		buffer.writeUTF(name);
		buffer.writeInt(job);
		buffer.writeInt(modelId);
		buffer.writeInt(iconId);
		buffer.writeInt(level);
		buffer.writeBoolean(isActive);
		buffer.writeInt(getMaxBattlePoint()-battlePoint);
		buffer.writeInt(growPoint);
		buffer.writeUTF(attStr);
		buffer.writeInt(getTotalAtt("power"));
		buffer.writeInt(getTotalAtt("nimble"));
		buffer.writeInt(getTotalAtt("spirit"));
		buffer.writeInt(getTotalAtt("wisdom"));
		buffer.writeInt(getTotalAtt("lifePoint"));
		buffer.writeInt(getTotalAtt("magicPoint"));
		buffer.writeInt(getTotalAtt("phyAtt"));
		buffer.writeInt(getTotalAtt("sptAtt"));
		buffer.writeInt(getTotalAtt("phyDef"));
		buffer.writeInt(getTotalAtt("sptDef"));
		buffer.writeInt(getTotalAtt("curePoint"));
		buffer.writeInt(getTotalAtt("noDefPhyHurt"));
		buffer.writeInt(getTotalAtt("noDefSptHurt"));
		buffer.writeInt(getTotalAtt("phyHurtAvoid"));
		buffer.writeInt(getTotalAtt("sptHurtAvoid"));
		buffer.writeInt(getTotalAtt("phySmiteParm"));
		buffer.writeInt(getTotalAtt("sptSmiteParm"));
		buffer.writeInt(getTotalAtt("phySmiteRate"));
		buffer.writeInt(getTotalAtt("sptSmiteRate"));
		buffer.writeInt(getTotalAtt("clearPhySmiteParm"));
		buffer.writeInt(getTotalAtt("clearSptSmiteParm"));
		buffer.writeInt(getTotalAtt("clearPhySmite"));
		buffer.writeInt(getTotalAtt("clearSptSmite"));
		buffer.writeInt(getTotalAtt("clearNoDefHurt"));
		if(nextExp == 0)
		{
			Map map = (Map)DataFactory.getInstance().getAttachment(DataFactory.BATTLE_PET_EXP);//暂时用人物的升级经验公式
			PetExp exp = (PetExp)map.get(level+1);
			if(exp != null)
				nextExp = exp.levelExp;
		}
		long exp = nextExp - requireExp;
		if(exp < 0)
			exp = 0;
		buffer.writeUTF(exp+"");
		buffer.writeUTF(nextExp+"");
		if(nextExp <= 0)
			buffer.writeInt(10000);
		else
		{
			int rate = (int) (exp * 10000 / nextExp);
			buffer.writeInt(rate);
		}
		
		buffer.writeByte(equips.size());
		for (int i = 0; i < equips.size(); i++)
		{
			GoodsPetEquip equip = (GoodsPetEquip) equips.get(i);
			equip.writeTo(buffer);
		}
	}
	

	public boolean isMaxBattlePoint()
	{
		return battlePoint >= getMaxBattlePoint();
	}
	
	public int getMaxBattlePoint()
	{
		 UpRole ur = getUpRoleByPet();
		 if(ur != null)
		 {
			 return maxBattlePoint + ur.getPetRewardValue("maxBattlePoint");
		 }
		 else
		 {
			return maxBattlePoint;
		 }
	}
	
	/**
	 * 设置转生后的职业，形象，等级
	 */
	public void setUpRoleJob(Player player)
	{
		level = 1;
		experience = 0;
		requireExp = getInitExp();
		nextExp = 0;

		job += 100;
		
		Pet pet = (Pet) DataFactory.getInstance().getGameObject(id);
		modelId = pet.modelId;
		
		UpRole ur = getUpRoleByPet();
		if(ur != null)
			growPoint += ur.getPetRewardValue("growPoint");

		updateJobSkills();
		
		player.initial(0);
	}
	
	public void sendBattlePetInfo(PlayerController target,int type,int playerId)
	{
		ByteBuffer buffer = new ByteBuffer();
		buffer.writeByte(type);
		if(type == 1)
		{
			buffer.writeBoolean(true);
			buffer.writeInt(playerId);
			writeBattlePetTo(buffer);
		}
		else if(type == 4)
			writeSkill(buffer);
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_PLAYER_BATTLE_PET_BASE_COMMAND,buffer));
	}

	/**
	 * 是否要将玩家的经验转换到守护身上，也就是守护身上转换经验的技能激活没
	 * @return
	 */
	public boolean isChangeExpSkillActive()
	{
		for (int i = 0; i < petSkills.length; i++)
		{
			if(petSkills[i] != null)
			{
				if(petSkills[i] instanceof PetPassiveSkill)
				{
					if(((PetPassiveSkill)petSkills[i]).dataType[0] == 22 && petSkills[i].isActive)
						return true;
				}
			}
		}
		return false;
	}
	
	public boolean upRole(PlayerController target)
	{
		if(target.getParent() instanceof BattleController)
			return false;
		if(target.getParent() instanceof BusinessController)
			return false;
		if(target.getTeam() != null)
		{
			target.sendAlert(ErrorCode.ALERT_UPROLE_TEAM_ERROR);
			return false;
		}
		if(target.getAam() != null)
		{
			target.sendAlert(ErrorCode.ALERT_UPROLE_AAM_ERROR);
			return false;
		}
		if(getEquipList().size() > 0)
		{
			target.sendAlert(ErrorCode.ALERT_PET_EQUIP_MUST_TAKEOFF);
			return false;
		}
		if(getStudyingSkill() != null)
		{
			target.sendAlert(ErrorCode.ALERT_PET_IS_STUDING_SKILL);
			return false;
		}
		
		TaskInfo ti = (TaskInfo) target.getPlayer().getExtPlayerInfo("taskInfo");
		if(ti.getTasks() == null || ti.getTasks().size() > 0)
		{
			target.sendAlert(ErrorCode.ALERT_UPROLE_TASK_NOT_NULL);
			return false;
		}
		
		UpRole ur = DataFactory.getInstance().getUpRoleByPlayer(getUpState()+11,job);
		if(ur == null)
		{
			target.sendAlert(ErrorCode.ALERT_UPROLE_CONDITION_ERROR);
			return false;	
		}
		if(!ur.isGoodsEnough(target))
		{
			target.sendAlert(ErrorCode.ALERT_UPROLE_GOODS_ERROR);
			return false;
		}
		
		if(!ur.isTaskFinish(target))
		{
			target.sendAlert(ErrorCode.ALERT_UPROLE_TASK_ERROR);
			return false;
		}

		if(level < ur.needLevel)
		{
			target.sendAlert(ErrorCode.ALERT_UPROLE_LEVEL_ERROR);
			return false;
		}
		
		setUpRoleJob(target.getPlayer());

		ur.removeGoods(target);
		
		sendBattlePetInfo(target,1,target.getID());
		sendBattlePetInfo(target,4,target.getID());

		target.sendAlwaysValue();
		return true;
	}
	
	public String getVariable(String key)
	{
		if(key.equals("maxHitPoint") || key.equals("life") || key.equals("lifePoint"))
			return lifePoint+"";
	    else if(key.equals("maxMagicPoint") || key.equals("magic"))
	    	return magicPoint+"";
		else if(key.equals("power"))
			return power+"";
		else if(key.equals("agility") || key.equals("nimble"))
			return nimble+"";
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
		else if(key.equals("nimbleExt") || key.equals("agilityExt"))
			return nimbleExt.toString();
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

	public int getTotalAtt(String key)
	{
		if(petType == Pet.PUTONGPET)
			return 0;
		key = getAttStr(key);
		int result = 0;
		if(key.equals("lifePoint"))
		    result = lifePoint;
	    else if(key.equals("magicPoint"))
			result = magicPoint;
		else if(key.equals("power"))
			result = power;
		else if(key.equals("nimble"))
			result = nimble;
		else if(key.equals("spirit"))
			result = spirit;
		else if(key.equals("wisdom"))
			result = wisdom;
		else if(key.equals("curePoint"))
			result = curePoint;	
		else if(key.equals("phyAtt"))
			result = phyAtt;
		else if(key.equals("sptAtt"))
			result = sptAtt;
		else if(key.equals("phyDef"))
			result = phyDef;
		else if(key.equals("sptDef"))
			result = sptDef;
		else if(key.equals("phySmiteRate"))
			result = phySmiteRate;
		else if(key.equals("sptSmiteRate"))
			result = sptSmiteRate;
		else if(key.equals("phyHurtAvoid"))
			result = phyHurtAvoid;
		else if(key.equals("sptHurtAvoid"))
			result = sptHurtAvoid;
		else if(key.equals("noDefPhyHurt"))
			result = noDefPhyHurt;
		else if(key.equals("noDefSptHurt"))
			result = noDefSptHurt;
		else if(key.equals("phySmiteParm"))
			result = phySmiteParm;
		else if(key.equals("sptSmiteParm"))
			result = sptSmiteParm;
		else if(key.equals("clearPhySmite"))
			result = clearPhySmite;
		else if(key.equals("clearSptSmite"))
			result = clearSptSmite;
		else if(key.equals("clearPhySmiteParm"))
			result = clearPhySmiteParm;
		else if(key.equals("clearSptSmiteParm"))
			result = clearSptSmiteParm;
		else if(key.equals("clearNoDefHurt"))
			result = clearNoDefHurt;
		else if(key.equals("growPoint"))
			result = growPoint;

		 if(!attStr.isEmpty())
		 {
			 String[] strs = Utils.split(attStr, "|");
			 for (int i = 0; i < strs.length; i++) 
			 {
				 String[] atts = Utils.split(strs[i], ":");
				 if(key.equals(atts[0]))
				 {
					 int upType = getUpState();
					 if(upType == 0)
						 result += Integer.parseInt(atts[1]);
					 else if(upType == 1)
						 result += (double)Integer.parseInt(atts[1]) * (double)1.5;
					 else if(upType == 2)
						 result += (double)Integer.parseInt(atts[1]) * (double)2;
				 }
			}
		 }
		 UpRole ur = getUpRoleByPet();
		 if(ur != null)
			 result += ur.getPetRewardValue(key);
		 
		 int basicAtt = 0;
		 if(!growBasic.isEmpty())
		 {
			 String[] strs = Utils.split(growBasic, "|");
			 for (int i = 0; i < strs.length; i++)
			 {
				String[] atts = Utils.split(strs[i], ":");
				if(atts[0].equals(key))
					basicAtt += (level-1) * Double.parseDouble(atts[1]);		
			}
			 basicAtt *= (double)growPoint / 100;
		 }
		
		 result += basicAtt;
		
		 for (int i = 0; i < equips.size(); i++)
		 {
			GoodsPetEquip goods = (GoodsPetEquip) equips.get(i);
			result += goods.getTotalAtt(key);
		 }
		
		if(!isMaxBattlePoint())
		{
			int type = 0;
			if("power".equals(key))
				type = 1;
			else if("nimble".equals(key))
				type = 3;
			else if("lifePoint".equals(key))
				type = 4;
			else if("spirit".equals(key))
				type = 5;
			else if("phyAtt".equals(key))
				type = 6;
			else if("phyDef".equals(key))
				type = 7;
			else if("sptAtt".equals(key))
				type = 8;
			else if("sptDef".equals(key))
				type = 9;
			else if("phySmiteRate".equals(key))
				type = 10;
			else if("sptSmiteRate".equals(key))
				type = 11;
			else if("phySmiteParm".equals(key))
				type = 12;
			else if("sptSmiteParm".equals(key))
				type = 13;
			else if("wisdom".equals(key))
				type = 17;
			else if("clearPhySmite".equals(key))
				type = 18;
			else if("clearSptSmite".equals(key))
				type = 19;
			else if("phyHurtAvoid".equals(key))
				type = 20;
			else if("sptHurtAvoid".equals(key))
				type = 21;
			else if("curePoint".equals(key))
				type = 23;
			else if("clearPhySmiteParm".equals(key))
				type = 26;
			else if("clearSptSmiteParm".equals(key))
				type = 27;
	        for (int i = 0; i < ppss.length; i++)
	        {
				if(ppss[i] == null)
					continue;
				for (int j = 0; j < ppss[i].dataType.length; j++) 
				{
					if(ppss[i].dataType[j] == 0 || ppss[i].dataType[j] == 22)
						continue;
					if(ppss[i].dataType[j] == type)
					{
						result += ppss[i].effectPoint[j];
					}
					if(ppss[i].dataType[j] == 24 && ("phyHurtAvoid".equals(key) || "sptHurtAvoid".equals(key)))
					{
						result += ppss[i].effectPoint[j];
					}
					if(ppss[i].dataType[j] == 25 && ("clearPhySmiteParm".equals(key) || "clearSptSmiteParm".equals(key)))
					{
						result += ppss[i].effectPoint[j];
					}
				}
	        }
		}
		
		if(key.equals("lifePoint"))
		    result += 5 * getTotalAtt("power");
	    else if(key.equals("magicPoint"))
			result += 5 * getTotalAtt("spirit");
//	    else if(key.equals("curePoint"))
//			result += (int) ((getTotalAtt("nimble")+getTotalAtt("wisdom"))*10000/(Math.sqrt(level)*500));	
		else if(key.equals("phyAtt"))
			result += (int) (1.5 * getTotalAtt("power"));
		else if(key.equals("sptAtt"))
			result += (int) (1.5 * getTotalAtt("spirit"));
		else if(key.equals("phyHurtAvoid"))
			result += getTotalAtt("nimble");
		else if(key.equals("sptHurtAvoid"))
			result += getTotalAtt("wisdom");
	     return result;
	}
	
	public String getAttStr(String key)
	{
		if(key.equals("maxHitPoint") || key.equals("battleMaxHP") || key.equals("lifePoint") || key.equals("life"))
	    	return "lifePoint";
	    else if(key.equals("maxMagicPoint") || key.equals("battleMaxMP") || key.equals("magicPoint") || key.equals("magic"))
	    	return "magicPoint";
		else if(key.equals("power"))
			return "power";
		else if(key.equals("agility") || key.equals("nimble"))
			return "nimble";
		else if(key.equals("spirit"))
			return "spirit";
		else if(key.equals("wisdom"))
			return "wisdom";
		else if(key.equals("curePoint"))
			return "curePoint";
		else if(key.equals("phyAtt") || key.equals("phsAtt"))
			return "phyAtt";
		else if(key.equals("sptAtt"))
			return "sptAtt";
		else if(key.equals("phyDef") || key.equals("phsDef"))
			return "phyDef";
		else if(key.equals("sptDef"))
			return "sptDef";
		else if(key.equals("phySmiteRate") || key.equals("phsSmiteRate"))
			return "phySmiteRate";
		else if(key.equals("sptSmiteRate"))
			return "sptSmiteRate";
		else if(key.equals("phyHurtAvoid") || key.equals("phsHurtAvoid"))
			return "phyHurtAvoid";
		else if(key.equals("sptHurtAvoid"))
			return "sptHurtAvoid";
		else if(key.equals("noDefPhyHurt") || key.equals("noDefPhsHurt"))
			return "noDefPhyHurt";
		else if(key.equals("noDefSptHurt"))
			return "noDefSptHurt";
		else if(key.equals("phySmiteHurtParm") || key.equals("phsSmiteHurtParm") || key.equals("phySmiteParm") || key.equals("phsSmiteParm"))
			return "phySmiteParm";
		else if(key.equals("sptSmiteHurtParm") || key.equals("sptSmiteParm"))
			return "sptSmiteParm";
		else if(key.equals("clearPhySmite") || key.equals("clearPhsSmite"))
			return "clearPhySmite";
		else if(key.equals("clearSptSmite"))
			return "clearSptSmite";
		else if(key.equals("clearPhySmiteParm") || key.equals("clearPhsSmiteParm"))
			return "clearPhySmiteParm";
		else if(key.equals("clearSptSmiteParm"))
			return "clearSptSmiteParm";
		else if(key.equals("clearNoDefHurt"))
			return "clearNoDefHurt";
		else if(key.equals("growPoint") || key.equals("grow"))
			return "growPoint";
		return "";
	}
	
	public UpRole getUpRoleByPet()
	{
		return DataFactory.getInstance().getUpRoleByPlayer(getUpState()+10,job);
	}
	
}
