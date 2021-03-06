package cc.lv1.rpg.gs.entity.impl;

import java.text.SimpleDateFormat;

import com.sun.xml.internal.ws.wsdl.parser.InaccessibleWSDLException;

import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.i.Map;
import vin.rabbit.util.collection.impl.ArrayList;
import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.data.DC;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.data.Exp;
import cc.lv1.rpg.gs.entity.controller.CopyController;
import cc.lv1.rpg.gs.entity.controller.GoldPartyController;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.controller.RoomController;
import cc.lv1.rpg.gs.entity.ext.Bag;

import cc.lv1.rpg.gs.entity.ext.EquipSet;

import cc.lv1.rpg.gs.entity.ext.BuffBox;


import cc.lv1.rpg.gs.entity.ext.AnswerParty;
import cc.lv1.rpg.gs.entity.ext.MailBox;
import cc.lv1.rpg.gs.entity.ext.OtherExtInfo;
import cc.lv1.rpg.gs.entity.ext.PVPInfo;
import cc.lv1.rpg.gs.entity.ext.FriendList;
import cc.lv1.rpg.gs.entity.ext.PetTome;
import cc.lv1.rpg.gs.entity.ext.PlayerBaseInfo;
import cc.lv1.rpg.gs.entity.ext.PlayerExtInfo;
import cc.lv1.rpg.gs.entity.ext.SkillTome;
import cc.lv1.rpg.gs.entity.ext.Storage;
import cc.lv1.rpg.gs.entity.ext.StoryEvent;
import cc.lv1.rpg.gs.entity.ext.TaskInfo;
//import cc.lv1.rpg.gs.entity.ext.SkillTome;
import cc.lv1.rpg.gs.entity.ext.PlayerSetting;

import cc.lv1.rpg.gs.entity.impl.battle.PlayerBattleTmp;
import cc.lv1.rpg.gs.entity.impl.battle.SpriteBattleTmp;
import cc.lv1.rpg.gs.entity.impl.battle.effect.TimeEffect;
import cc.lv1.rpg.gs.entity.impl.battle.skill.Skill;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsEquip;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsProp;
import cc.lv1.rpg.gs.entity.impl.pet.Pet;

/**
 * 玩家实体
 * @author dxw
 *
 */
public class Player extends Role
{
	/**
	 * 最后登陆时间
	 */
	public long lastTime;
	
	/** 回城复活生命回复百分比 */
	public static final int LIFERATE = 10;
	
	/** 玩家基本信息 */
	private PlayerBaseInfo baseInfo = new PlayerBaseInfo(this);
	                                                                                                     
	/** 玩家附加信息 */
	private PlayerExtInfo [] extInfos = new PlayerExtInfo[0];
	
	/** 帐户名 */
	public String accountName;
	
	/** 是否是族长 **/
	public boolean isFamilyLeader = false;

	/** 家族id */
	public int familyId;
	
	/** 家族名称 */
	public String familyName;

	/** 女神试练积分 */
	public byte copyPoint;
	public static final int MAXCOPYPOINT = 100;
	
	/** 女神试练人间难度点(第一次进入时记录,每天清0) */
	public byte copyStep1;
	
	/** 女神试练地狱难度点(第一次进入时记录,每天清0) */
	public byte copyStep2;
	
	/** 女神试练天堂难度点(第一次进入时记录,每天清0) */
	public byte copyStep3;

	/** 逃跑时间 */
	public long escTimer = 0;
	
	/** 领取黄金斗士活动签名的日期 */
	public byte goldPartyDate;
	
	/** 新手泡跑记录点 */
	public byte guideStep;
	
	/** 多倍经验开始时间 */
	public long expMultTime;
	
	/** 血槽点数 */
	public int extLife;
	
	/** 蓝槽点数 */
	public int extMagic;
	
	public static int FLYACTIVEPOINTDEFAULT = 10;
	public static byte BOSSACTIVEPOINTDEFAULT = 15;
	public static byte TIMECOPYACTIVEPOINTDEFAULT = 20;
	
	/** 每日传送行动值  当行动值用光后，第二天不用的*/
	public int flyActivePoint = 10;
	
	/** 击杀BOSS行动值 */
	public byte bossActivePoint = 15;
	
	/** 时空之旅行动值 */
	public byte timeCopyActivePoint = 20;
	
	/** 事件值 每个怪物打死了过后都把值给主角的事件值加上 */
	public int eventPoint;
	
	/** 上线日期 */
	public byte date;
	
	/** 能否聊天 */
	public boolean isChat = true;

	/** 新手指导步骤 */
	public byte newStudyProcess;
	
	/** 青铜圣殿挑战次数(每天清零) */
	public byte moneyBattleCount;
	
	/** 上一次领取活动奖励的日期 */
	public byte partyRewardMonth;
	
	public byte partyRewardDay;
	
	/** 是否封号 */
	public boolean isClosed;

	/** 所在区号 */
	public String worldNUM;
	
	public boolean isVipPlayer = true;
	
	public int chengjiu;
	
	public Player(boolean isSkill)
	{
		SkillTome st = new SkillTome();
		addExtPlayerInfo(st);
	}

	public Player()
	{
		Bag bag = new Bag();
		addExtPlayerInfo(bag);
		
		SkillTome st = new SkillTome();
		addExtPlayerInfo(st);
		
		BuffBox bb = new BuffBox();
		bb.setPlayer(this);
		addExtPlayerInfo(bb);
		
		PVPInfo pvp = new PVPInfo();
		pvp.setPlayer(this);
		addExtPlayerInfo(pvp);
		
		EquipSet es = new EquipSet();
		es.setPlayer(this);
		addExtPlayerInfo(es);
		
		addExtPlayerInfo(new FriendList());
		addExtPlayerInfo(new TaskInfo());
		addExtPlayerInfo(new PlayerSetting());
		addExtPlayerInfo(new Storage(0));//玩家个人仓库
		//addExtPlayerInfo(new Storage(1));//家族仓库
		//addExtPlayerInfo(new Storage(2));//工会仓库
		
		PetTome pet = new PetTome();
		addExtPlayerInfo(pet);
		
		MailBox mb = new MailBox();
		addExtPlayerInfo(mb);
		
		AnswerParty ap = new AnswerParty();
		addExtPlayerInfo(ap);
		
		OtherExtInfo oei = new OtherExtInfo();
		addExtPlayerInfo(oei);
		
		StoryEvent se = new StoryEvent();
		addExtPlayerInfo(se);
	}
	
	public void setSkill()
	{
		SkillTome skillTome = (SkillTome) getExtPlayerInfo("skillTome");
		PlayerSetting ps = (PlayerSetting) getExtPlayerInfo("playerSetting");
		
		Skill skill = skillTome.getSkill(1031000001);
		if(skill == null)
			return;
		Skill sk1 = (Skill) Skill.cloneObject(skill);
		ps.addPlayerBar(sk1, 0, 1);
	}

	/**
	 * 初始化角色信息
	 * type 1表示是升级 0表示是建立角色或登陆游戏时
	 */
	public void initial(int type)
	{
		setDefaultData(type);
		
		EquipSet es = (EquipSet) getExtPlayerInfo("equipSet");
		if(es != null)
		{
			int mhp = es.getTotalAtt("maxHitPoint") + maxHitPoint;
			if(hitPoint <= 1)
				hitPoint = (int) (mhp * (double)Player.LIFERATE / 100);
			
			int maxMagic = maxMagicPoint + es.getTotalAtt("maxMagicPoint");
			if(hitPoint > mhp)
				hitPoint = mhp;
			if(magicPoint > maxMagic)
				magicPoint = maxMagic;
		}
	}
	
	public void setDefaultData(int type)
	{
		/**
		 * 足球...
		    力量=ROUND(2*LV,0)
敏捷=ROUND(1.5*LV,0)
精神=ROUND(0.8*LV,0)
智慧=ROUND(1.2*LV,0)
		 */
		if(profession == 2)
		{
			baseInfo.power = Math.round(2*level);
			baseInfo.nimble = (int) Math.round(1.5*level);
			baseInfo.spirit= (int) Math.round(0.8*level);
			baseInfo.wisdom = (int) Math.round(1.2*level);
		}
		/**
		 * 军官...
			力量=ROUND(1.5*LV,0)
敏捷=ROUND(1.75*LV,0)
精神=ROUND(0.75*LV,0)
智慧=ROUND(1.15*LV,0)
		 */
		else if(profession == 1)
		{
			baseInfo.power = (int) Math.round(1.5*level);
			baseInfo.nimble = (int) Math.round(1.75*level);
			baseInfo.spirit= (int) Math.round(0.75*level);
			baseInfo.wisdom = (int) Math.round(1.5*level);
		}
		/**
		 * 护士...
			力量=ROUND(1*LV,0)
敏捷=ROUND(1.5*LV,0)
精神=ROUND(1.23*LV,0)
智慧=ROUND(1.75*LV,0)
		 */
		else if(profession == 3)
		{
			baseInfo.power = (int) Math.round(level);
			baseInfo.nimble = (int) Math.round(1.5*level);
			baseInfo.spirit= (int) Math.round(1.25*level);
			baseInfo.wisdom = (int) Math.round(1.75*level);
		}
		/**
		 * 超人...
		力量=ROUND(1*LV,0)
敏捷=ROUND(1.2*LV,0)
精神=ROUND(1.8*LV,0)
智慧=ROUND(1.5*LV,0)
		 */
		else if(profession == 4)
		{
			baseInfo.power = (int) Math.round(level);
			baseInfo.nimble = (int) Math.round(1.2*level);
			baseInfo.spirit= (int) Math.round(1.8*level);
			baseInfo.wisdom = (int) Math.round(1.5*level);
		}

		baseInfo.updateBaseValue(type);
	}
	

	public void loadFrom(ByteBuffer byteBuffer)
	{
		super.loadFrom(byteBuffer);
		accountName = byteBuffer.readUTF();

		familyId = byteBuffer.readInt();
		familyName = byteBuffer.readUTF();
		isFamilyLeader = byteBuffer.readBoolean();
		copyPoint = (byte) byteBuffer.readByte();
		copyStep1 = (byte) byteBuffer.readByte();
		copyStep2 = (byte) byteBuffer.readByte();
		copyStep3 = (byte) byteBuffer.readByte();

		escTimer = byteBuffer.readLong();
		
		goldPartyDate = (byte) byteBuffer.readByte();
		guideStep = (byte) byteBuffer.readByte();
		bossActivePoint = (byte) byteBuffer.readByte();
		timeCopyActivePoint = (byte) byteBuffer.readByte();
		
		expMultTime = byteBuffer.readLong();
		extLife = byteBuffer.readInt();
		extMagic = byteBuffer.readInt();
	    flyActivePoint = byteBuffer.readInt();
	    eventPoint = byteBuffer.readInt();
	    date =(byte)byteBuffer.readByte();
	    isChat = byteBuffer.readBoolean();
	    
	    newStudyProcess = (byte) byteBuffer.readByte();
	    moneyBattleCount = (byte) byteBuffer.readByte();
	    partyRewardMonth = (byte) byteBuffer.readByte();
	    partyRewardDay = (byte) byteBuffer.readByte();

		int extLen = byteBuffer.readInt();

		for(int i = 0 ; i < extLen ; i ++)
		{
			String ext = byteBuffer.readUTF();
			getExtPlayerInfo(ext).loadFrom(byteBuffer);
		}
	
		onLoaded();
		

/*

		///-------------------------临时的打印
		//背包里面所有的物品
		Bag bag = (Bag)getExtPlayerInfo("bag");
		Goods goods [] = bag.goodsList;
		for (int i = 0; i < goods.length; i++)
		{
			if(goods[i] == null)
				continue;
			
			System.out.println("背包 "+goods[i].name+" "+ goods[i].objectIndex);
			
		}
		
		//0 1 2是流水号  3 4 是时间
		goods = bag.extGoods;
		for (int i = 0; i < goods.length; i++)
		{
			if(i == 3 || i == 4)
				continue;

			if(goods[i] == null)
				continue;
			
			
			System.out.println("附加道具 "+goods[i].name+" "+ goods[i].objectIndex);
		}
		
		//新手礼包
		for (int i = 0; i < bag.giftGoods.size(); i++)
		{
			Goods g = (Goods)bag.giftGoods.get(i);
			
			if(g == null)
				continue;
			
			System.out.println("新手礼包 "+g.name+" "+ g.objectIndex);
		}
		
		//取得宠物
		PetTome pt = (PetTome)getExtPlayerInfo("petTome");
		Pet []pet= pt.getPets();
		for (int i = 0; i < pet.length; i++)
		{
			System.out.println("宠物  "+pet[i].name+" "+pet[i].objectIndex);
			
			GoodsProp g = (GoodsProp) bag.getPetEgg(pet[i].objectIndex);

			System.out.println("宠物关联  "+pet[i].name+" petIndex  "+g.petIndex);
			System.out.println("宠物蛋的流水  "+pet[i].name+"  "+g.objectIndex);
		}
		
		//取得仓库
		Storage storage = (Storage)getExtPlayerInfo("storage");
		goods = storage.getGoodsList();
		for (int i = 0; i < goods.length; i++)
		{
			if(goods[i] == null)
				continue;
			
			System.out.println("仓库 "+goods[i].name+" "+goods[i].objectIndex);
		}
		
		//取得邮件流水号
		MailBox mb = (MailBox)getExtPlayerInfo("mailbox");
		int mailSize = mb.mailList.size();
		for (int i = 0; i < mailSize; i++)
		{
			Mail mail =  (Mail)mb.mailList.get(i);
			Goods g1 = mail.getAttach1();
			if(g1 != null)
			{
				System.out.println("邮件G1 "+g1.objectIndex);
				 
			}
			Goods g2 =mail.getAttach2();
			if(g2 != null)
			{
				System.out.println("邮件G1 "+g2.objectIndex);
			}
		}
<<<<<<< Player.java

		OtherExtInfo oei = (OtherExtInfo)getExtPlayerInfo("otherExtInfo");

		System.out.println("------------------------------------ "+oei.worldNUM);
		
		*/

	}

	private void onLoaded()
	{
		if(roomId == DataFactory.KAITUOROOM || roomId == DataFactory.XIESHENROOM || roomId == DataFactory.PARTYPKROOM)
		{
			setDefaultRoom(DataFactory.INITROOM);
		}
		
		Bag bag = (Bag)getExtPlayerInfo("bag");
		bag.fixBag();
		
		
		PetTome pt =(PetTome) getExtPlayerInfo("petTome");
		Pet pet = pt.getActivePet();
		if(pet != null)
		{
			pet.setPlayer(this);
			pet.setGrow();
			
			
			if(bag.getPetEgg(pet.objectIndex) == null)
			{
				Goods goods = (Goods) DataFactory.getInstance().getGameObject(1047000001);
				GoodsProp petGoods = (GoodsProp)Goods.cloneObject(goods);
				petGoods.objectIndex = GameServer.getInstance().getWorldManager().getDatabaseAccessor().getGoodsObjIndex();
				petGoods.petIndex = pet.objectIndex;
				bag.addPetEggs(petGoods);
			}
		}
		
		EquipSet es = (EquipSet) getExtPlayerInfo("equipSet");
		int mhp = es.getTotalAtt("maxHitPoint") + maxHitPoint;
		if(hitPoint <= 1)
			hitPoint = (int) (mhp * (double)Player.LIFERATE / 100);
		
		int maxMagic = maxMagicPoint + es.getTotalAtt("maxMagicPoint");
		if(hitPoint > mhp)
			hitPoint = mhp;
		if(magicPoint > maxMagic)
			magicPoint = maxMagic;
		
		SkillTome st = (SkillTome)getExtPlayerInfo("skillTome");
		st.updateProfessionSkills(this);
		
		Reward rp = GameServer.getInstance().getWorldManager().getAnswerRewardById(id);
		if(rp != null)
		{
			rp.setPlayer(this);
		}
		
		rp = GoldPartyController.getInstance().getReward(id);
		if(rp != null)
			rp.setPlayer(this);
		
		List copyList = (List) DataFactory.getInstance().getAttachment(DataFactory.COPY_LIST);
		for (int i = 0; i < copyList.size(); i++) 
		{
			CopyController copy = (CopyController) copyList.get(i);
			Reward r = copy.getRewardToday(id);
			if(r != null)
			{
				r.setPlayer(this);
			}
		}
	
		AnswerParty ap = (AnswerParty) getExtPlayerInfo("answerParty");

		if(ap.answerTime != 0) // 发奖励的时候服务器是关闭状态,在玩家登录时补发奖励
		{
			SimpleDateFormat sdf = new SimpleDateFormat("dd");
			byte date = Byte.parseByte(sdf.format(ap.answerTime));
			if(WorldManager.date != date)
			{
				ap.clearAll();
			}
		}
	
		OtherExtInfo oei = (OtherExtInfo) getExtPlayerInfo("otherExtInfo");
		if(oei.aamCount > OtherExtInfo.MAXAAMCOUNT)
			oei.aamCount = OtherExtInfo.MAXAAMCOUNT;
		
    	RoomController room = GameServer.getInstance().getWorldManager().getRoomWolrd(roomId);
    	if(room == null)
    	{
    		System.out.println("Player onload roomId error:"+roomId+" playerName:"+name);
    		setDefaultRoom(DataFactory.INITROOM);
    	}
    	else
    	{	
    		if(DataFactory.getInstance().isInConPartyRoom(room.id))
    			setDefaultRoom(DataFactory.INITROOM);
    		if(room.isGoldPartyPKRoom || room.isGoldPartyRoom)
    		{
    			if(GoldPartyController.getInstance().isEnded())
    				setDefaultRoom(GoldPartyController.PARTYINITROOM);
    			else
    			{
    				if(GoldPartyController.getInstance().getPlayerPoint(id) == null)
    					setDefaultRoom(GoldPartyController.PARTYINITROOM);
    			}
    		}
    	}
    	
    	StoryEvent se = (StoryEvent) getExtPlayerInfo("storyEvent");
    	se.updateStorys();
		
	    if(date != WorldManager.date) //回复 行动值 和 清除答题
	    {
	    	se.clear(null, this);
	    	
	    	pt.clear();
	    	
	    	clearData();

	    	ap.clearAll();

	    	oei.clearAss();
	    	oei.clearAamCount();
	    	
	    	if(room != null)
	    	{
	    		if(room.getCopy() != null)
				{
					if(room.getCopy().isHonorCopy())
						setDefaultRoom(DataFactory.HONORROOM);
					else
						setDefaultRoom(DataFactory.INITROOM);
				}
	    		else if(room.isCopyPartyRoom)
	    			setDefaultRoom(DataFactory.INITROOM);
	    	}
	    }
   
		if(!oei.isValiIdcard)
		{
			if(oei.leaveTime > 0)
			{
				long time = System.currentTimeMillis() - oei.leaveTime; //离线的时间
				oei.setOnlineTime(-time);
			}
		}
		
		if(Mail.offLineplayer.accountName.equals(accountName)) //清除离线邮件缓冲
		{
			Mail.offLineplayer = new Player();
			Mail.offLineplayer.accountName = "";
		}

		if(level > Exp.max_level)
		{
			Map map = (Map)DataFactory.getInstance().getAttachment(DataFactory.ATTACH_EXP);
			Exp exp = (Exp)map.get(Exp.max_level);
			
			if(exp != null)
			{
				level = exp.level;
				experience = exp.total;
				nextExp = 0;
				requireExp = 0;
			}
		}
		
		int gpDate = GoldPartyController.getInstance().getPartyDate();
		if(gpDate != 0 && gpDate != goldPartyDate)
		{
			Storage storage = (Storage) getExtPlayerInfo("storage");
			storage.goldSignName = "";
		}

		checkGoodsEffectTime();
	}
	


	public void saveTo(ByteBuffer byteBuffer)
	{
		super.saveTo(byteBuffer);
		
		byteBuffer.writeUTF(accountName);

		byteBuffer.writeInt(familyId);
		byteBuffer.writeUTF(familyName);
		byteBuffer.writeBoolean(isFamilyLeader);
		byteBuffer.writeByte(copyPoint);
		byteBuffer.writeByte(copyStep1);
		byteBuffer.writeByte(copyStep2);
		byteBuffer.writeByte(copyStep3);
	    byteBuffer.writeLong(escTimer);
	    
	    byteBuffer.writeByte(goldPartyDate);
	    byteBuffer.writeByte(guideStep);
	    byteBuffer.writeByte(bossActivePoint);
	    byteBuffer.writeByte(timeCopyActivePoint);
	    
	    byteBuffer.writeLong(expMultTime);
	    byteBuffer.writeInt(extLife);
	    byteBuffer.writeInt(extMagic);
	    byteBuffer.writeInt(flyActivePoint);
	    byteBuffer.writeInt(eventPoint);
	    byteBuffer.writeByte(date);
	    byteBuffer.writeBoolean(isChat);
	    
	    byteBuffer.writeByte(newStudyProcess);
	    byteBuffer.writeByte(moneyBattleCount);
	    byteBuffer.writeByte(partyRewardMonth);
	    byteBuffer.writeByte(partyRewardDay);
	    
		int extLen = extInfos.length;
		byteBuffer.writeInt(extLen);
		
		for(int i = 0 ; i < extLen ; i ++)
		{
			String extName = extInfos[i].getName();

			byteBuffer.writeUTF(extName);
			extInfos[i].saveTo(byteBuffer);
		}
	}
	
	public void readFrom(ByteBuffer byteBuffer)
	{
		super.readFrom(byteBuffer);
	}
	
	public void writeTo(ByteBuffer byteBuffer)
	{
		super.writeTo(byteBuffer);
		byteBuffer.writeInt(familyId);
		byteBuffer.writeUTF(familyName);
		byteBuffer.writeBoolean(isFamilyLeader);
		byteBuffer.writeInt(flyActivePoint);
		byteBuffer.writeInt(eventPoint);

		EquipSet es = (EquipSet) getExtPlayerInfo("equipSet");
		int mhp = es.getTotalAtt("maxHitPoint") + maxHitPoint;
		int mmp = es.getTotalAtt("maxMagicPoint") + maxMagicPoint;
		byteBuffer.writeInt(mhp);
		byteBuffer.writeInt(mmp);
		byteBuffer.writeInt(baseInfo.speed);
		
		PVPInfo pvpInfo = (PVPInfo) getExtPlayerInfo("PVPInfo");
		byteBuffer.writeInt(pvpInfo.honourPoint);
		
		OtherExtInfo oei = (OtherExtInfo) getExtPlayerInfo("otherExtInfo");
		oei.writeValidateTo(byteBuffer);
	}
	
	public void writeBaseTo(ByteBuffer buffer)
	{
		
	}
	
	public PlayerExtInfo getExtPlayerInfo(String name)
	{
		for (int i = 0; i < extInfos.length; i++)
		{	
			if (extInfos[i].getName().equals(name))
				return extInfos[i];
		}
		return null;
	}
	
	public void addExtPlayerInfo(PlayerExtInfo extInfo)
	{
		PlayerExtInfo[] infos = new PlayerExtInfo[extInfos.length+1];
		for (int i = 0; i < extInfos.length; i++)
			infos[i] = extInfos[i];
		infos[extInfos.length] = extInfo;
		extInfos = infos;
	}
	
	public List getExtPlayerInfos(String name)
	{
		List list = new ArrayList();
		for (int i = 0; i < extInfos.length; i++)
		{	
			if (extInfos[i].getName().equals(name))
				list.add(extInfos[i]);
		}
		return list;
	}
	
	public boolean insteadExt(PlayerExtInfo ext)
	{

		return false;
	}

	public PlayerBaseInfo getBaseInfo()
	{
		return baseInfo;
	}

	public void setCamp(int camp)
	{
		this.camp = camp;
	}

	/**
	 * 设置角色模型ID
	 */
	public void setPlayerModelMotionId()
	{
		if(upProfession == 0)
		{
			if(sex == 1)
				modelMotionId = 1190000101;
			else if(sex == 0)
				modelMotionId = 1190000001;
		}
		else
		{
			if(sex == 1)
			{
				if(upProfession == 1 || upProfession == 9 || upProfession == 13 || upProfession == 17)
					modelMotionId = 1190106101;
				else if(upProfession == 2)
					modelMotionId = 1190106101;
				else if(upProfession == 3 || upProfession == 10 || upProfession == 14 || upProfession == 18)
					modelMotionId = 1190108101;
				else if(upProfession == 4)
					modelMotionId = 1190108101;
				else if(upProfession == 5)
					modelMotionId = 1190109101;
				else if(upProfession == 6 || upProfession == 11 || upProfession == 15 || upProfession == 19)
					modelMotionId = 1190110101;
				else if(upProfession == 7 || upProfession == 12 || upProfession == 16 || upProfession == 20)
					modelMotionId = 1190111101;
				else if(upProfession == 8)
					modelMotionId = 1190112101;
			}
			else if(sex == 0)
			{
				if(upProfession == 1 || upProfession == 9 || upProfession == 13 || upProfession == 17)
					modelMotionId = 1190106001;
				else if(upProfession == 2)
					modelMotionId = 1190106001;
				else if(upProfession == 3 || upProfession == 10 || upProfession == 14 || upProfession == 18)
					modelMotionId = 1190107001;
				else if(upProfession == 4)
					modelMotionId = 1190108001;
				else if(upProfession == 5)
					modelMotionId = 1190109001;
				else if(upProfession == 6 || upProfession == 11 || upProfession == 15 || upProfession == 19)
					modelMotionId = 1190109001;
				else if(upProfession == 7 || upProfession == 12 || upProfession == 16 || upProfession == 20)
					modelMotionId = 1190112001;
				else if(upProfession == 8)
					modelMotionId = 1190112001;
			}
		}
	}
	

	
	public boolean setHitPoint(int point,SpriteBattleTmp pbt)
	{
		hitPoint += point;
		if(hitPoint <= 0)
		{
			hitPoint = 0;
			return false;
		}
		
		int maxPoint = 0;
		if(pbt == null)
		{
			EquipSet equipSet = (EquipSet) getExtPlayerInfo("equipSet");
			maxPoint = maxHitPoint + equipSet.getTotalAtt("maxHitPoint");
		}
		else
		{
			if(pbt instanceof PlayerBattleTmp)
				maxPoint = ((PlayerBattleTmp)pbt).maxHitPoint;
			else
			{
				EquipSet equipSet = (EquipSet) getExtPlayerInfo("equipSet");
				maxPoint = maxHitPoint + equipSet.getTotalAtt("maxHitPoint");
			}
		}
		
		if(hitPoint > maxPoint)
			hitPoint = maxPoint;
		return true;
	}
	
	public boolean setMagicPoint(int point,SpriteBattleTmp pbt)
	{
		magicPoint += point;
		
		if(magicPoint <= 0)
		{
			magicPoint = 0;
			return false;
		}
		int maxPoint = 0;
		if(pbt == null)
		{
			EquipSet equipSet = (EquipSet) getExtPlayerInfo("equipSet");
			maxPoint = maxMagicPoint + equipSet.getTotalAtt("maxMagicPoint");
		}
		else
		{
			if(pbt instanceof PlayerBattleTmp)
				maxPoint = ((PlayerBattleTmp)pbt).maxMagicPoint;
			else
			{
				EquipSet equipSet = (EquipSet) getExtPlayerInfo("equipSet");
				maxPoint = maxMagicPoint + equipSet.getTotalAtt("maxMagicPoint");
			}
		}
		
		if(magicPoint > maxPoint)
			magicPoint = maxPoint;
		return true;
	}

	public void sendAlwaysValue(ByteBuffer buffer,PlayerBattleTmp pbt)
	{
		buffer.writeInt(id);
		buffer.writeInt(hitPoint);
		buffer.writeInt(magicPoint);
		buffer.writeInt(level);
		if(nextExp == 0)
		{
			Map map = (Map)DataFactory.getInstance().getAttachment(DataFactory.ATTACH_EXP);
			Exp exp = (Exp)map.get(level+1);
			if(exp != null)
				nextExp = exp.levelExp;
		}
		long exp = nextExp - requireExp;
		if(exp < 0)
			exp = 0;
		buffer.writeUTF(exp+"");
		if(pbt == null)
		{
			EquipSet es = (EquipSet) getExtPlayerInfo("equipSet");
			es.writeBaseTo(buffer);
		}
		else
		{
			buffer.writeInt(pbt.maxHitPoint);
			buffer.writeInt(pbt.maxMagicPoint);
		}
		buffer.writeUTF(nextExp+"");
		int rate = 10000;
		if(nextExp != 0)
		    rate = (int) (exp * 10000 / nextExp);
		buffer.writeInt(rate);
	}
   
	public void sendLifeInfo(ByteBuffer buffer)
	{
		buffer.writeInt(id);
		buffer.writeInt(hitPoint);
		buffer.writeInt(magicPoint);
	}
	


	
	public void sendPlayerBattleInfo(ByteBuffer buffer)
	{
		buffer.writeInt(magicPoint);
		EquipSet es = (EquipSet) getExtPlayerInfo("equipSet");
		es.writeBaseTo(buffer);
	}
	
	
	
	public void setDefaultRoom(int id)
	{
		RoomController room = (RoomController) DataFactory.getInstance().getGameObject(id);
		if(room != null)
		{
			worldId = room.getParent().getParentId();
			areaId = room.getParent().id;
			roomId = room.id;
			x = 0;
			y = 0;
		}
	}
	
	
	/**
	 * 升级时更新BUFF的值到玩家身上
	 * @param type 1表示是做跟装备相关操作 0表示其它
	 * 	 */
	public void uptateBuffPoint(int type)
	{
		if(type != 1)
			type = 0;
		BuffBox buffBox = (BuffBox) getExtPlayerInfo("buffBox");
		List buffList = buffBox.getEffectList();
	
		for (int k = 0; k < buffList.size(); k++) 
		{
			TimeEffect effect = (TimeEffect) buffList.get(k);
	
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
				else
				{
					int currPoint = Integer.parseInt(baseInfo.getVariable(effect.dataType[j]));

					if(type == 1)
					{
						if(effect.dataPattern[j] == 2)
						{
							EquipSet es = (EquipSet) getExtPlayerInfo("equipSet");
							addPoint = (int) ((es.getTotalAtt(effect.dataType[j]) + currPoint) * (double)effect.effectPoint[j] / 100);
							
							addPoint += (double)addPoint * (double)effect.helpPoint / 10000;
							
							effect.setPoint(j, addPoint);
						}
						else
							addPoint = effect.getPoint(j);
					}
					else if(type == 0)
						addPoint = effect.getPoint(j);

					point = addPoint+currPoint;
					if(point < 0)
						point = 0;
					baseInfo.setVariable(effect.dataType[j], String.valueOf(point));
			
					baseInfo.updateLifeValue(addPoint, effect.dataType[j]);
				}
			}
		}
	}
	
	/**
	 * 检测玩家的道具有效期
	 */
	public void checkGoodsEffectTime()
	{
		Bag bag = (Bag) getExtPlayerInfo("bag");
		Goods[] goodsList = bag.getGoodsList();
		for (int i = 0; i < goodsList.length; i++) 
		{
			if(goodsList[i] == null)
				continue;
			if(goodsList[i].createTime == 0 || goodsList[i].effectTime == 0)
			{
				if(goodsList[i] instanceof GoodsEquip)
				{
					GoodsEquip equip = (GoodsEquip) goodsList[i];
					if(equip.eType == 2)
					{
						if(equip.useFlag)
						{
							if(equip.equipLocation == 10)
								setPlayerModelMotionId();
						}
						goodsList[i] = null;
					}
				}
				continue;
			}
			long time = goodsList[i].effectTime - (System.currentTimeMillis() - goodsList[i].createTime);
			if(time <= 0)
			{
				if(goodsList[i].useFlag && goodsList[i] instanceof GoodsEquip)
				{
					GoodsEquip equip = (GoodsEquip) goodsList[i];
					if(equip.equipLocation == 10 && equip.eType != 0)
						setPlayerModelMotionId();
				}
				goodsList[i] = null;
			}
		}
		
		Storage storage = (Storage) getExtPlayerInfo("storage");
		goodsList = storage.getGoodsList();
		for (int i = 0; i < goodsList.length; i++) 
		{
			if(goodsList[i] == null)
				continue;
			if(goodsList[i].createTime == 0 || goodsList[i].effectTime == 0)
			{
				if(goodsList[i] instanceof GoodsEquip)
				{
					GoodsEquip equip = (GoodsEquip) goodsList[i];
					if(equip.eType == 2)
					{
						goodsList[i] = null;
					}
				}
				continue;
			}
			long time = goodsList[i].effectTime - (System.currentTimeMillis() - goodsList[i].createTime);
			if(time <= 0)
			{
				//到期了,删除,并通知玩家
				goodsList[i] = null;
			}
		}
	}
	
	
	/**
	 * 离婚,合区时删除婚姻相关道具
	 * @param 1离婚 2合区
	 */
	public void removeMarryGoods(int type)
	{
		
		Bag bag = (Bag) getExtPlayerInfo("bag");

		Goods[] goodsList = bag.getGoodsList();
		for (int i = 0; i < goodsList.length; i++) 
		{
			if(goodsList[i] == null)
				continue;
			if(type == 1)
			{
				if(goodsList[i] instanceof GoodsEquip)
				{
					GoodsEquip equip = (GoodsEquip) goodsList[i];
					if(equip.equipLocation == 11 && equip.eType == 1)
					{
						if(equip.useFlag)
						{
							title = "";
						}
						goodsList[i] = null;
					}  
				}
			}
			else if(type == 2)
			{
				if(goodsList[i] instanceof GoodsEquip)
				{
					GoodsEquip equip = (GoodsEquip) goodsList[i];
					if(equip.eType == 1)
					{
						if(equip.useFlag)
						{
							if(equip.equipLocation == 11)
								title = "";
							if(equip.equipLocation == 10)
								setPlayerModelMotionId();
						}
						goodsList[i] = null;
					}
				}
			}
			else
				continue;
		}
		
		Storage storage = (Storage) getExtPlayerInfo("storage");
		goodsList = storage.getGoodsList();
		for (int i = 0; i < goodsList.length; i++) 
		{
			if(goodsList[i] == null)
				continue;
			if(type == 1)
			{
				if(goodsList[i] instanceof GoodsEquip)
				{
					GoodsEquip equip = (GoodsEquip) goodsList[i];
					if(equip.eType == 1 && equip.equipLocation == 11 )
					{
						goodsList[i] = null;
					}
				}
			}
			else if(type == 2)
			{
				if(goodsList[i] instanceof GoodsEquip)
				{
					GoodsEquip equip = (GoodsEquip) goodsList[i];
					if(equip.eType == 1)
						goodsList[i] = null;
				}
			}
			else
				continue;
		
		}
		
		MailBox mb = (MailBox) getExtPlayerInfo("mailbox");
		int mailSize = mb.mailList.size();
		for (int i = 0; i < mailSize; i++)
		{
			Mail mail =  (Mail)mb.mailList.get(i);
			Goods g1 = mail.getAttach1();
			if(g1 != null)
			{
				if(g1 instanceof GoodsEquip)
				{
					GoodsEquip equip = (GoodsEquip) g1;
					if(equip.eType == 1)
					{
						mail.clearAttach();
					}
				}
			}
			Goods g2 =mail.getAttach2();
			if(g2 != null)
			{
				if(g2 instanceof GoodsEquip)
				{
					GoodsEquip equip = (GoodsEquip) g2;
					if(equip.eType == 1)
					{
						mail.clearAttach();
					}
				}
			}
			
		}
		
		if(type == 1)
			bag.setExtGoods(5, null);
		else
		{
			Goods goods = bag.getExtGoods(5);
			if(goods != null)
			{
				Mail mail = new Mail(DC.getString(DC.PLAYER_59));
				mail.setTitle(DC.getString(DC.PLAYER_60));
				mail.addAttach(goods);
				Goods[] gs = DataFactory.getInstance().makeGoods(1045027007);
				if(gs!=null&&gs[0]!=null)
					mail.addAttach(gs[0]);
				mail.sendOffLine(this);
				
//				sb.append(" return qiuhunjiezhi:");
//				sb.append(goods.name);
				
				bag.setExtGoods(5, null);
			}
		}
		
		if(type == 2)
		{
			if(cnMonth != 0)
				cnMonth = 0;
		}
		
//		return sb.toString();
	}
	
	public String getMarryInfo()
	{
		OtherExtInfo oei = (OtherExtInfo) getExtPlayerInfo("otherExtInfo");
		if("".equals(oei.loverName) || oei.loverId == 0)
			return "";
		StringBuffer sb = new StringBuffer();
		sb.append("[marryType:");
		sb.append(oei.marryType==1?"common":"VIP");
		sb.append("][loverName:");
		sb.append(oei.loverName);
		sb.append("]");
		return sb.toString();
	}
	
	
	/**
	 * 取得玩家的属性类型(1.物理 2.精神)
	 * @return
	 */
	public int getJobType()
	{
		if(profession == 1 || profession == 2)
			return 1;
		else 
			return 2;
	}
	
	/**
	 * 转生后清空技能列表,重新初始化技能组
	 */
	public void upInitSkills()
	{
		SkillTome st = (SkillTome) getExtPlayerInfo("skillTome");
		st.clearSkill();
		
		int [] ids = new int[1];
		ids[0] = 1031000001;
		st.initBaseSkill(ids,this);
		
		st.updateProfessionSkills(this);
		
	}
	
	/**
	 * 设置转生后的职业，形象，等级
	 */
	public void setUpRoleJob()
	{
		level = 1;
		experience = 0;
		requireExp = 5;
		nextExp = 0;

		switch (upProfession) 
		{
			case 1: upProfession =  9;break;
			case 3: upProfession = 10;break;
			case 6: upProfession = 11;break;
			case 7: upProfession = 12;break;
			default:upProfession += 4;break;
		}
		
		setPlayerModelMotionId();
		
		upInitSkills();
		
		initial(0);
	}
	
	public void clearData()
	{
		date = WorldManager.date;
		goldBoxCount = 0;
		flyActivePoint = Player.FLYACTIVEPOINTDEFAULT;
		bossActivePoint = Player.BOSSACTIVEPOINTDEFAULT;
		timeCopyActivePoint = Player.TIMECOPYACTIVEPOINTDEFAULT;
		copyStep1 = 0;
		copyStep2 = 0;
		copyStep3 = 0;
		moneyBattleCount = 0;
	}
	
	
	private void setXY(GoldParty gp)
	{
		if(gp.maxX > 0)
			x = (int) (Math.random() * (gp.maxX - gp.minX) + gp.minX);
		if(gp.maxY > 0)
			y = (int) (Math.random() * (gp.maxY - gp.minY) + gp.minY);
	}
	
	public void setXYByCount(RoomController room,int count)
	{
		if(room.isPartyPKRoom())
		{
			//x：410-1500
			//y:360-490
			x = (int) (Math.random() * (1500 - 410) + 410);
			y = (int) (Math.random() * (490 - 360) + 360);
		}
		else
		{
			GoldParty gp = DataFactory.getInstance().getGoldPartyByRoom(room.id);
			if(gp == null)
				return;
			if(gp.maxX > 0 || gp.maxY > 0)
			{
				setXY(gp);
				for (int i = 0; i < count; i++) 
				{
					if(room.isPositionNull(x, y))
						break;
					setXY(gp);
				}
			}
		}
	}

}
