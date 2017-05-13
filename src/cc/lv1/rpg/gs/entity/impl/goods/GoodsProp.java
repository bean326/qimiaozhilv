package cc.lv1.rpg.gs.entity.impl.goods;

import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.data.DC;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.controller.FamilyPartyController;
import cc.lv1.rpg.gs.entity.controller.MonsterController;
import cc.lv1.rpg.gs.entity.controller.PVEController;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.controller.RoomController;
import cc.lv1.rpg.gs.entity.ext.Bag;
import cc.lv1.rpg.gs.entity.ext.EquipSet;
import cc.lv1.rpg.gs.entity.ext.PetTome;
import cc.lv1.rpg.gs.entity.ext.PlayerBaseInfo;
import cc.lv1.rpg.gs.entity.ext.SkillTome;
import cc.lv1.rpg.gs.entity.ext.Storage;
import cc.lv1.rpg.gs.entity.impl.BossDropProp;
import cc.lv1.rpg.gs.entity.impl.BoxDropProp;
import cc.lv1.rpg.gs.entity.impl.Goods;
import cc.lv1.rpg.gs.entity.impl.Player;
import cc.lv1.rpg.gs.entity.impl.battle.PlayerBattleTmp;
import cc.lv1.rpg.gs.entity.impl.battle.effect.Effect;
import cc.lv1.rpg.gs.entity.impl.battle.effect.FlashEffect;
import cc.lv1.rpg.gs.entity.impl.battle.effect.TimeEffect;
import cc.lv1.rpg.gs.entity.impl.battle.skill.ActiveSkill;
import cc.lv1.rpg.gs.entity.impl.battle.skill.PassiveSkill;
import cc.lv1.rpg.gs.entity.impl.battle.skill.PetActiveSkill;
import cc.lv1.rpg.gs.entity.impl.battle.skill.PetSkill;
import cc.lv1.rpg.gs.entity.impl.battle.skill.Skill;
import cc.lv1.rpg.gs.entity.impl.pet.Pet;
import cc.lv1.rpg.gs.entity.impl.pet.PetSkillStudy;
import cc.lv1.rpg.gs.net.SMsg;
import cc.lv1.rpg.gs.other.ErrorCode;
import cc.lv1.rpg.gs.util.FontConver;
import vin.rabbit.comm.GameObject;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.impl.ArrayList;


public class GoodsProp extends Goods {
	
	/** 效果 */
	public int effect;
	
	/** 使用对象(1.对自己使用 2.对敌方使用 3.不能在战斗中使用) */
	public int useObject;
	
	/** CD时间 */
	public int propCDTime;
	
	/** 技能ID */
	public int skillId;
	
	/** 持续时间(需要保存) */
	public long expTimes;
	
	/** 经验倍数(不需要保存) */
	public double expMult = 1;
	
	/** 聊天信息发送频道(不需要保存)(0:普通 1:VIP 这个主要用到道具的结婚戒指和结婚形象兑换券,和守护的技能经验包) */
	public int chatType;
	
	/** 血槽点数(需要保存) */
	public int extLife;
	
	/** 蓝槽点数(需要保存) */
	public int extMagic;
	
	/** 宝箱类型0新手 1.豪华,2.精致,3.华丽,4.豪杰,
	 * 5.女神,6.无双,7.天地,8.天下,9.天上,10.女神试练,
	 * 11.金蛋,12.超级金蛋(10%几率的物品公告),
	 * 13.BOSS之星素材宝箱,14.无双金蛋(里面所有物品都公告), 
	 * 15.新蛋,16.星星宝箱(10%几率的物品公告),17.月亮宝箱(里面所有物品都公告)
	 * */
	public int boxType;
	
	/** 物品使用次数(需要保存) */
	public int useCount;
	
	/** 记忆房间ID(需要保存) */
	public int roomId;
	
	/** 要加的经验点数(荣誉值道具时是要加的荣誉值点数) */
	public long expPoint;
	
	/** 要增加的元宝或金币 */
	public int addMoney;
	
	/** 得到宝箱时玩家的等级(决定开宝箱时要开的物品,需要保存) */
	public int boxLevel = 1;
	
	/** 亲密度,行动值,仓库格子,守护体力等其它附加值 */
	public int intPoint;
	
	/** 宠物ID */
	public int petId;
	
	/** 掉宝组ID */
	public int dropId;
	
	/** 宠物流水 */
	public long petIndex;
	
	/**
	 * 宝石默认追加属性 属性:属性值 power:10
	 * 形象兑换券时是表示  兑换的形象:有效时间(单位是天)
	 */
	public String gemAtt = "";
	
	
	public GoodsProp(){}
	
	public boolean setVariable(String key,String value)
	{
		if(key.equals("effect"))
		{
			effect = Integer.parseInt(value);
			return true;
		}
		else if(key.equals("skillId"))
		{
			skillId = Integer.parseInt(value);
			return true;
		}
		else if(key.equals("id"))
		{
			id = Integer.parseInt(value);
			return true;
		}
		else if(key.equals("useObject"))
		{
			useObject = Integer.parseInt(value);
			return true;
		}
		else if(key.equals("propCDTime"))
		{
			propCDTime = Integer.parseInt(value);
			return true;
		}
		else if(key.equals("type"))
		{
			type = Integer.parseInt(value);
			return true;
		}
		else if(key.equals("expTimes"))
		{
			if(type != 21)
				expTimes = (long) (Double.parseDouble(value) * 60 * 60 * 1000);
			else 
				expTimes = Long.parseLong(value);
			return true;
		}
		else if(key.equals("expMult"))
		{
			expMult = Double.parseDouble(value);
			return true;
		}
		else if(key.equals("chatType"))
		{
			chatType = Integer.parseInt(value);
			boxType = Integer.parseInt(value);//特殊宝箱才有
			return true;
		}
		else if(key.equals("extLife"))
		{
			extLife = Integer.parseInt(value);
			return true;
		}
		else if(key.equals("extMagic"))
		{
			extMagic = Integer.parseInt(value);
			return true;
		}
		else if(key.equals("addMoney"))
		{
			addMoney = Integer.parseInt(value);
			return true;
		}
		else if(key.equals("expPoint"))
		{
			expPoint = Long.parseLong(value);
			return true;
		}
		else if(key.equals("intPoint"))
		{
			intPoint = Integer.parseInt(value);
			return true;
		}
		else if(key.equals("petId"))
		{
			petId = Integer.parseInt(value);
			return true;
		}
		else if(key.equals("dropId"))
		{
			dropId = Integer.parseInt(value);
			return true;
		}
		else if(key.equals("gemAtt"))
		{
			gemAtt = value;
			return true;
		}
		else
			return super.setVariable(key, value);
	}
	
	
	public void saveTo(ByteBuffer buffer)
	{
		super.saveTo(buffer);
		
//		buffer.writeByte(useCount);
//		buffer.writeInt(roomId);
		buffer.writeLong(expPoint);
//		buffer.writeInt(boxLevel);
	}
	
	public void writeSuperTo(ByteBuffer buffer)
	{
		super.writeTo(buffer);
	}
	

	
	public void writeTo(ByteBuffer buffer)
	{
		super.writeTo(buffer);
		if(type == 15)
		{
			Pet pet = (Pet) DataFactory.getInstance().getGameObject(petId);
			if(pet == null)
				buffer.writeInt(0);
			else
				buffer.writeInt(pet.modelId);
		}
		else
		{
			buffer.writeInt(effect);
			buffer.writeInt(propCDTime);
			buffer.writeInt(useObject);
			buffer.writeUTF(expPoint+"");
			if(type == 8)
			{
				buffer.writeInt(skillId);
			}
			else if(type == 7)
			{
				if(roomId != 0)
				{
					RoomController room = (RoomController) DataFactory.getInstance().getGameObject(roomId);
					buffer.writeUTF(room.name);
				}
				else
					buffer.writeUTF("");
			}
			else if(type == 6)
			{
				buffer.writeUTF(gemAtt);
			}
			else if(type == 22)
			{
				buffer.writeInt((int) expPoint);
			}
			else if(type == 10)
			{
				buffer.writeInt(boxType);
			}
		}
	}
	
	public void copyTo(GameObject go)
	{
		super.copyTo(go);
		GoodsProp goods = (GoodsProp)go;
		goods.effect = effect;
		goods.propCDTime = propCDTime;
		goods.useObject = useObject;
		goods.skillId = skillId;
		goods.boxType = boxType;
		goods.expMult = expMult;
		goods.chatType = chatType;
		goods.expPoint = expPoint;
		goods.expTimes = expTimes;
		goods.extLife = extLife;
		goods.extMagic = extMagic;
		goods.useCount = useCount;
		goods.roomId = roomId;
		goods.addMoney = addMoney;
		goods.boxLevel = boxLevel;
		goods.intPoint = intPoint;
		goods.petId = petId;
		goods.petIndex = petIndex;
		goods.dropId = dropId;
		goods.gemAtt = gemAtt;
	}

	@Override
	public void onDeleteImpl(PlayerController target)
	{
	}

	@Override
	public boolean onRemoveImpl(PlayerController target) 
	{
		boolean result = false;
	
		return result;
	}

	@Override
	public boolean onUseImpl(PlayerController target) 
	{
		switch (type)  
		{
			case 2: return eatDrug(target);
			case 3: return false;
			case 4: return false;
			case 5: return useSpeaker(target);
			case 6: return useGem(target);
			case 7: return playReel(target);
			case 8: return studySkill(target);
			case 9: return playMoney(target);
			case 10:return playBox(target);
			case 11:return playExp(target);
			case 12:return playExt(target);
			case 13:return feedPet(target);
			case 16:return playEquipMoney(target);
			case 17:return playFlyActive(target);
			case 18:return playStorageSize(target);
			case 19:return playPetActive(target);
			case 20:return presentBox(target);
			case 22:return playHonor(target);
			case 23:return playPoint(target);
			case 24:return playOnlineExp(target);
			case 25:return playChangeName(target);
			case 35:return playBattlePetExp(target);
			case 36:return playBattlePetSkillExp(target);
			case 37:return playBattlePetPoint(target);
		}
		return false;
	}
	
	private boolean playBattlePetPoint(PlayerController target)
	{
		PetTome pt = (PetTome) target.getPlayer().getExtPlayerInfo("petTome");
		Pet pet = pt.getActiveBattlePet();
		if(pet == null)
		{
			target.sendAlert(ErrorCode.ALERT_NOT_BATTLE_PET);
			return false;
		}
		if(pet.battlePoint <= 0 ||pet.battlePoint - intPoint < 0)
		{
			target.sendAlert(ErrorCode.ALERT_PET_IS_FULL);
			return false;
		}
		pet.battlePoint -= intPoint;
		if(pet.battlePoint < 0)
			pet.battlePoint = 0;
		ByteBuffer buffer = new ByteBuffer();
		buffer.writeByte(11);
		buffer.writeUTF(pet.name);
		buffer.writeInt(intPoint);
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_PLAYER_BATTLE_PET_BASE_COMMAND,buffer));
		return true;
	}
	
	private boolean playBattlePetSkillExp(PlayerController target)
	{
		if(expPoint <= 0)
			return false;
		PetTome pt = (PetTome) target.getPlayer().getExtPlayerInfo("petTome");
		Pet pet = pt.getActiveBattlePet();
		if(pet == null)
			return false;
		PetSkill skill = pet.getPetSkillByVIPType(chatType);
		if(skill == null)
			return false;
		skill = pet.getPetStudySkillById(skill.id);
		if(skill == null)
			return false;
		PetSkillStudy pss = DataFactory.getInstance().getPetSkillStudyBySkill(skill);//需要根据客户端传过来 的技能通过技能的skillType取得PetSkillStudy对象
		if(pss == null)
			return false;
		pet.addSkillExp(skill, expPoint, pss, target,2);
		pet.sendBattlePetInfo(target, 4,target.getID());
		return true;
	}
	
	private boolean playBattlePetExp(PlayerController target)
	{
		if(expPoint > 0)
		{
			PetTome pt = (PetTome) target.getPlayer().getExtPlayerInfo("petTome");
			Pet pet = pt.getActiveBattlePet();
			if(pet == null)
				return false;
			pet.addBattlePetExp(target, expPoint);
			return true;
		}
		return false;
	}
	
	private boolean playChangeName(PlayerController target)
	{
		ByteBuffer buffer = new ByteBuffer(1);
		buffer.writeByte(0);
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_PLAYER_MODIFY_NAME_COMMAND,buffer));
		return false;
	}
	
	private boolean playOnlineExp(PlayerController target)
	{
		Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
		Goods goods = bag.getExtGoods(4);
		if(goods == null)
		{
			Goods prop = (Goods) Goods.cloneObject(this);
			prop.objectIndex = WorldManager.currentTime;
			bag.setExtGoods(4, prop);
			return true;
		}
		else
		{
			GoodsProp newGoods = (GoodsProp) Goods.cloneObject(this);
			GoodsProp old = (GoodsProp) goods;
			bag.sendExpBuff(target, old.effect, false, 0);
			newGoods.objectIndex = WorldManager.currentTime;
			bag.setExtGoods(4, newGoods);
//			target.sendAlert(ErrorCode.ALERT_ONLINE_EXP_EXITS);
			return true;
		}
	}
	
	private boolean playHonor(PlayerController target)
	{
		if(expPoint <= 0)
			return false;
		target.setHonour((int) expPoint);
		return true;
	}
	
	
	/**
	 * 礼包
	 * @param target
	 * @return
	 */
	private boolean presentBox(PlayerController target)
	{
		if(gemAtt.isEmpty())
			return false;
		Bag bag = (Bag)target.getPlayer().getExtPlayerInfo("bag");
		
		String[] strs = Utils.split(gemAtt, "|");
		int bagNullSize = bag.getNullCount();
		
		if(strs.length > bagNullSize)
		{
			target.sendAlert(ErrorCode.ALERT_BAG_IS_FULL);
			return false;
		}
		
		for (int i = 0; i < strs.length; i++) 
		{
			String[] gStr = Utils.split(strs[i], ":");
			try{
				Goods[] goods = DataFactory.getInstance().makeGoods(Integer.parseInt(gStr[0]), 
					Integer.parseInt(gStr[1]), Integer.parseInt(gStr[2]));
				if(goods == null)
				{
					System.out.println("GoodsProp presentBox error name:"+name+" strs[i]:"+strs[i]);
					return false;
				}
				for (int j = 0; j < goods.length; j++)
				{
					if(goods[j] != null)
						bag.sendAddGoods(target, goods[j]);
				}
			}catch(Exception e)
			{
				System.out.println("GoodsProp presentBox error name:"+name+" gemAtt:"+gemAtt);
				return false;
			}
		}
		
		return true;
	}

	/**
	 * 宠物活跃度道具
	 * @param target
	 * @return
	 */
	private boolean playPetActive(PlayerController target)
	{
		if(intPoint <= 0)
			return false;
		PetTome petTome = (PetTome) target.getPlayer().getExtPlayerInfo("petTome");
		Pet pet = petTome.getActivePet();
		if(pet == null)
			return false;
		
		if(pet.activePoint + intPoint > pet.maxActivePoint)
		{
			target.sendAlert(ErrorCode.ALERT_PETACTIVEPOINT_OVER);
			return false;
		}
		pet.activePoint += intPoint;
		return true;
	}
	
	/**
	 * 行动值道具
	 * @param target
	 * @return
	 */
	private boolean playFlyActive(PlayerController target)
	{
		if(intPoint <= 0)
			return false;
		if(target.getPlayer().flyActivePoint + intPoint > Player.FLYACTIVEPOINTDEFAULT)
		{
			target.sendAlert(ErrorCode.ALERT_FLYACTIVEPOINT_OVER);
			return false;
		}
		target.getPlayer().flyActivePoint += intPoint;
		return true;
	}
	
	/**
	 * 仓库扩展道具
	 * @param target
	 * @return
	 */
	private boolean playStorageSize(PlayerController target)
	{
		if(intPoint <= 0)
			return false;
		Storage storage = (Storage) target.getPlayer().getExtPlayerInfo("storage");
		if(storage.storageSize + intPoint > Storage.MAX_SIZE)
		{
			target.sendAlert(ErrorCode.ALERT_STORAGE_MAX_SIZE);
			return false;
		}
		storage.addStorageSize(intPoint);
		return true;
	}
	
	
	/**
	 * 代金券
	 * @param target
	 * @return
	 */
	private boolean playEquipMoney(PlayerController target)
	{
		if(addMoney == 0)
			return false;
		Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
		bag.equipMoney += addMoney;
		return true;
	}
	
	/**
	 * 更换宠物
	 * @param target
	 * @return
	 */
	public boolean activePet(PlayerController target)
	{
		if(petId == 0)
			return false;
		Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
		if(bag.getGoodsByObjectIndex(objectIndex) == null)
			return false;
		PetTome pt = (PetTome) target.getPlayer().getExtPlayerInfo("petTome");
		Pet newPet = null;
		Pet p = pt.getActivePet();
		if(p != null)
		{
			p.isActive = false;
			p.setPlayerBaseInfo(0);
			p.setPlayerOtherValue(0);
		}

		if(petIndex == 0)
		{
			Pet pet = (Pet) DataFactory.getInstance().getGameObject(petId);
			newPet = (Pet) Pet.cloneObject(pet);
			petIndex = GameServer.getInstance().getDatabaseAccessor().getGoodsObjIndex();
			newPet.objectIndex = petIndex;
			newPet.requireExp = newPet.getInitExp();
			newPet.isActive = true;
			newPet.setPlayer(target.getPlayer());
			pt.addPet(newPet);
			bag.addPetEggs(this);
		}
		else
		{
			newPet = pt.getPet(petIndex);
			if(newPet != null)
			{
				newPet.isActive = true;
			}
		}

		if(newPet != null)
		{
			newPet.setPlayerBaseInfo(1);
			newPet.setPlayerOtherValue(1);
			if(p.isStroll)
				target.sendPetModel(2, newPet.modelId,newPet.name);
		}
		
		bag.getGoodsList()[bag.getGoodsLocation(objectIndex)] = null;

		if(p != null)
		{
			Goods goods = bag.getPetEgg(p.objectIndex);
			if(goods != null)
			{
				bag.sendAddGoods(target, goods);
			}
		}
		return true;
	}


	/**
	 * 宠物食物
	 * @param target
	 * @return
	 */
	private boolean feedPet(PlayerController target)
	{
		if(target.isPetUp)
		{
			target.sendGetGoodsInfo(1, false, DC.getString(DC.PLAYER_30));
			return false;
		}
		PetTome pt = (PetTome) target.getPlayer().getExtPlayerInfo("petTome");
		Pet pet = pt.getActivePet();
		if(pet == null)
			return false;
		if(intPoint > 0 && expPoint > 0)
		{
			System.out.println("Goods pet feed not same time add exp and inti!");
			return false;
		}
		StringBuffer sb = null;
		if(intPoint > 0)
		{
			if(pet.isMaxInti(1))
			{
				sb = new StringBuffer();
				sb.append(DC.getString(DC.PLAYER_36));//你的宠物亲密度已达最大值
				target.sendGetGoodsInfo(1,false,sb.toString());
				return false;
			}
			pet.setInti(intPoint,target);
			return true;
		}
		if(expPoint > 0)
		{
			if(pet.isMaxInti(2))
			{
				sb = new StringBuffer();
				sb.append(DC.getString(DC.PLAYER_40));//你的宠物等级已达最大值
				target.sendGetGoodsInfo(1,false,sb.toString());
				return false;
			}
			boolean expFlag = target.addPetExp(expPoint);
			return expFlag;
		}
		return false;
	}
	
	
	/**
	 * @param target
	 * @return
	 */
	private boolean playPoint(PlayerController target)
	{
		if(addMoney == 0)
			return false;
		Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
		bag.point += addMoney;
		return true;
	}
	
	/**
	 * 加元宝的
	 * @param target
	 * @return
	 */
	private boolean playMoney(PlayerController target)
	{
		if(addMoney == 0)
			return false;
		Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
		bag.money += addMoney;
		return true;
	}
	
	/**
	 * 喇叭等
	 * @param target
	 * @return
	 */
	private boolean useSpeaker(PlayerController target)
	{
		if(chatType == 0)
			return false;
		else
		{
			target.sendAlert(ErrorCode.ALERT_GOODS_NOT_USE);
			return false;
		}
	}
	
	
	
	/**
	 * 卷轴
	 * @param target
	 * @return
	 */
	private boolean playReel(PlayerController target)
	{
		if(!(target.getParent() instanceof RoomController))
			return false;
		int reelId = 0;
		if(id == 1041000001)//回城卷
		{
			if(target.isStory)
			{
				if(target.getEvent() == null)
					return false;
				else
				{
					if(target.getEvent().eventType != 1)
						return false;
				}
			}
			reelId = DataFactory.INITROOM;
			if(FamilyPartyController.getInstance().isStarted())
				FamilyPartyController.getInstance().removePlayerWithout(target);
		}
		else if(id == 1045000041)//记忆宝石
		{
			if(useCount >= 1)
			{
				if(roomId == 0)
					return false;
				reelId = roomId;
			}
			else
			{
				roomId = target.getRoom().id;
				useCount++;
				target.sendAlert(ErrorCode.ALERT_PLAYER_INDEX_SAVED);
				Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
				bag.sendMemoryStone(target, objectIndex+"", target.getRoom().name);
				return false;
			}
		}
		if(reelId == 0)
			return false;
		Object obj = DataFactory.getInstance().getGameObject(reelId);
		if(obj == null || !(obj instanceof RoomController))
			return false;
		if(target.getTeam() != null)
		{
			target.sendAlert(ErrorCode.ALERT_TEAM_STATE_USEERROR);
			return false;
		}
		if(reelId == target.getRoom().id)
			return false;
		target.moveToRoom(reelId);
		useCount = 0;
		return true;
	}
	

	
	/**
	 * 血 蓝槽
	 * @param target
	 * @return
	 */
	private boolean playExt(PlayerController target)
	{
		Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
		if(extLife != 0)
		{
			GoodsProp goods = (GoodsProp) bag.getExtGoods(0);
			if(goods != null)
			{
				bag.sendExpBuff(target, goods.effect, false, 0);
			}
			target.getPlayer().extLife = extLife;
			bag.setExtGoods(0, this);
		}
		else if(extMagic != 0)
		{
			GoodsProp goods = (GoodsProp) bag.getExtGoods(1);
			if(goods != null)
			{
				bag.sendExpBuff(target, goods.effect, false, 0);
			}
			target.getPlayer().extMagic = extMagic;
			bag.setExtGoods(1, this);
		}
		return true;
	}
	
	/**
	 * 宝石
	 * @param target
	 * @return
	 */
	private boolean useGem(PlayerController target)
	{
		return false;
	}
	
	/**
	 * 经验道具
	 * @param target
	 * @return
	 */
	private boolean playExp(PlayerController target)
	{
		if(expPoint == 0)
		{
			Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
			GoodsProp goods = (GoodsProp) bag.getExtGoods(2);
			if(goods != null)
			{
				bag.sendExpBuff(target, goods.effect, false, 0);
			}
			target.getPlayer().expMultTime = WorldManager.currentTime;//System.currentTimeMillis();
			bag.setExtGoods(2, this);
		}
		else
		{
			target.addExp(expPoint,true,false,false);
		}
		return true;
	}
	
	private boolean isSpeBox(boolean check)
	{
		if(check)//判断是特殊宝箱
			return boxType == 11 || boxType == 12 || boxType == 14 || boxType == 15 || boxType == 16 || boxType == 17;
		else//判断不是特殊宝箱
			return boxType != 11 && boxType != 12 && boxType != 14 && boxType != 15 && boxType != 16 && boxType != 17;
	}
	
	/**
	 * 宝箱
	 * @param target
	 * @param buffer
	 * @return
	 */
	private boolean playBox(PlayerController target)
	{
		if(dropId == 0 && isSpeBox(false))
			return false;
		if(!target.isPlayBox())
			return false;
		
		Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
		if(!target.isOnline())
		{
			System.out.println("Player is false online!");
			return false;
		}
		ByteBuffer buffer = new ByteBuffer();
		if(isSpeBox(true))
		{
			List list = DataFactory.getInstance().getBoxDropPropList();
			List rList = new ArrayList(8);
			for (int i = 0; i < list.size(); i++) 
			{
				BoxDropProp bdp = (BoxDropProp) list.get(i);
				if(bdp == null)
					continue;
				if(bdp.boxType == boxType)
				{
					rList.add(bdp);
				}
			}
	
			int cr = (int) (Math.random() * 10000) + 1;
			int bdpRate = 0;
			BoxDropProp cb = null;
			boolean isNotice = false;
			//要修改的元宝抽奖
			for (int i = 0; i < rList.size(); i++) 
			{
				BoxDropProp bdp = (BoxDropProp) rList.get(i);
				bdpRate += bdp.rate;
				if(cr <= bdpRate)
				{
					cb = bdp;
					if(bdp.rate <= 1000)
						isNotice = true;
					break;
				}
			}
	
			if(cb == null)
				return false;
			Goods tmp = cb.getGoodsByMoney();
			if(tmp == null)
				return false;
			
			if(!bag.isCanAddGoodsToBag(tmp))
			{
				target.sendAlert(ErrorCode.ALERT_BAG_IS_FULL);
				return false;
			}
			buffer.writeByte(boxType);
			tmp.writeTo(buffer);
			buffer.writeInt(tmp.goodsCount);
		
			if((isNotice && boxType == 12) || boxType == 14)
			{
				target.sendGetGoodsInfo(3, true, DC.getGoldEggString(target,name,tmp));
			}
			if(boxType == 16)
			{
				if(DataFactory.isGoodsNotice(GoodsNotice.STAR_BOX, tmp))
					target.sendGetGoodsInfo(3, true, DC.getGoldEggString(target,name,tmp));
			}
			else if(boxType == 17)
			{
				target.sendGetGoodsInfo(3, true, DC.getGoldEggString(target,name,tmp));
			}

			bag.sendAddGoods(target, tmp);
		}
		else
		{
			if(String.valueOf(dropId).startsWith("8"))
			{
				BossDropProp bossDP = (BossDropProp) DataFactory.getInstance().getGameObject(dropId);
				if(bossDP == null)
				{
					System.out.println("GoodsProp playBox bossDPId:"+dropId);
					return false;
				}
				List goodsList = bossDP.getGoodsList();
				if(goodsList.size() != 10)
					return false;
				buffer.writeByte(1);//BOSS宝箱
				for (int i = 0; i < 10; i++) 
				{
					Goods goods = (Goods) goodsList.get(i);
					goods.writeTo(buffer);
					buffer.writeInt(goods.goodsCount);
					bag.addBossGoods(goods);
				}
			}
			else
			{
				List goodsList = new ArrayList(8);
				BoxDropProp bdp = (BoxDropProp)DataFactory.getInstance().getBDP(dropId);
				if(bdp == null)
				{
					System.out.println("GoodsProp playBox bdpId:"+dropId);
					return false;
				}
				for (int i = 0; i < 8; i++)
				{
					Goods goods = bdp.getGoods(false);
					if(goods == null)
						return false;
					goodsList.add(goods);
					
				}	

				if(goodsList.size() == 0 || goodsList == null || goodsList.size() != 8)
					return false;
				int random = (int) (Math.random()*goodsList.size());
				buffer.writeByte(0);//普通宝箱
				for (int i = 0; i < 8; i++) 
				{
					Goods goods = (Goods) goodsList.get(i);
					goods.writeTo(buffer);
					buffer.writeInt(goods.goodsCount);
					if(i == random)
					{
						buffer.writeBoolean(true);//取这个物品
						bag.setTmp(goods,name);
					}
					else
						buffer.writeBoolean(false);
				}
				buffer.writeByte(boxType);
			}
			target.setIsBox(true, 1);
		}
		
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_OPEN_BOX_COMMAND,buffer));
		return true;
	}
	

	
	/**
	 * 战斗中使用物品
	 */
	public boolean onUseGoodsBattle(PlayerController player)
	{
		if(player.getAttachment().isDead())
			return false;
		int mhp = ((PlayerBattleTmp)player.getAttachment()).maxHitPoint;
		int mmp = ((PlayerBattleTmp)player.getAttachment()).maxMagicPoint;
		double mult = player.getPlayer().getBaseInfo().mult;

		if(type == 2)
		{
			Object obj = DataFactory.getInstance().getGameObject(effect);
			if(obj == null)
			{
				player.sendAlert(ErrorCode.ALERT_EFFECT_NOT_EXIST);
				return false;
			}
			else if(obj instanceof TimeEffect)
			{
				TimeEffect te = (TimeEffect) obj;
				TimeEffect effect = (TimeEffect) Effect.cloneObject(te);
				
				setBattleMonsterAI(effect,player);
				player.getAttachment().setAddLifePlayer();
			
				player.getAttachment().setSkillTarget(player);
				boolean flag = false;
				if(effect.dataType[0].equals("currentMP"))
				{
					if(player.getPlayer().magicPoint >= mmp)
						return false;
					effect.effectPoint[0] = (int) (effect.effectPoint[0] * mult);
					flag = true;
				}
				else if(effect.dataType[0].equals("currentHP"))
				{
					if(player.getPlayer().hitPoint >= mhp)
						return false;
				}
				if(flag)
				{
					TimeEffect e = (TimeEffect) Effect.cloneObject(effect);
					e.effectPoint[0] = (int) (e.effectPoint[0] * mult);
					player.getAttachment().onTimeEffectUse(e);
				}
				else
					player.getAttachment().onTimeEffectUse(effect);
			}
			else if(obj instanceof FlashEffect)
			{
				FlashEffect effect = (FlashEffect) obj;
				
				setBattleMonsterAI(effect,player);
				player.getAttachment().setAddLifePlayer();
				
				player.getAttachment().setSkillTarget(player);
		
				boolean flag = false;
				if(effect.dataType == 2 && effect.type == 2)
				{
					if(player.getPlayer().magicPoint >= mmp)
						return false;
					flag = true;
				}	
				else if(effect.dataType == 1 && effect.type == 2)
				{
					if(player.getPlayer().hitPoint >= mhp)
						return false;
				}
				else if(effect.dataType == 3 && effect.type == 2)
				{
					if(player.getPlayer().hitPoint >= mhp && player.getPlayer().magicPoint >= mmp)
						return false;
				}
	
				if(flag)
				{
					FlashEffect e = (FlashEffect) Effect.cloneObject(effect);
					e.effectPoint = (int) (e.effectPoint * mult);
					player.getAttachment().onFlashEffectUse(e);
				}
				else
					player.getAttachment().onFlashEffectUse(effect);
			}
		}
		return true;
	}
	
	/**
	 * 学习宠物技能
	 * @param target
	 * @return
	 */
//	private  boolean studyPetSkill(PlayerController target)
//	{
//		GoodsPet pet = (GoodsPet) target.getPlayer().getExtPlayerInfo("pet");
//		if(pet== null)
//			return false;
//		if(pet.level < level)
//		{
//			target.sendAlert(ErrorCode.ALERT_PET_LEVEL_LOW);
//			return false;
//		}
//		Skill skill = pet.getSkill(skillId);
//		if(skill == null)
//		{
//			target.sendAlert(ErrorCode.ALERT_SKILL_NOT_EXIST);
//			return false;
//		}
//		if(skill.isStudied)
//		{
//			target.sendAlert(ErrorCode.ALERT_PET_STUDIED_SKILL);
//			return false;
//		}
//		if(pet.isHighLevelStudy(skill))
//		{
//			target.sendAlert(ErrorCode.ALERT_PET_STUDIED_HIGHER_SKILL);
//			return false;
//		}
//		if(pet.isLowLevelNoStudy(skill))
//		{
//			target.sendAlert(ErrorCode.ALERT_PET_NOT_STUDY_LOW_SKILL);
//			return false;
//		}
//		if(pet.checkSameSkill(skill))
//		{
//			target.sendAlert(ErrorCode.ALERT_PET_SKILL_NO_STUDY);
//			return false;
//		}
//		pet.addSkill(skillId);
//		return true;
//	}
	
	
	/**
	 * 学习主角技能
	 * @param target
	 * @return
	 */
	private boolean studySkill(PlayerController target)
	{
		Object obj = DataFactory.getInstance().getGameObject(skillId);
		if(obj == null)
		{
			target.sendAlert(ErrorCode.ALERT_SKILL_NOT_EXIST);
			return false;
		}
		if(!(obj instanceof Skill))
		{
			target.sendAlert(ErrorCode.ALERT_GOODS_NOT_SKILL);
			return false;
		}
		if(target.getPlayer().level < level)
		{
			target.sendAlert(ErrorCode.ALERT_GOODS_LEVEL_LOW);
			return false;
		}
		SkillTome skillTome = (SkillTome) target.getPlayer().getExtPlayerInfo("skillTome");
		Skill skill = skillTome.getSkill(skillId);//玩家可以学习的技能列表
		if(skill == null)
		{
//			System.out.println("GoodsProp skillId:"+skillId+"  player name:"+target.getName()+
//					"  goodsId:"+id+" player job:"+target.getPlayer().profession+" player upJob:"+target.getPlayer().upProfession);
			target.sendAlert(ErrorCode.ALERT_PROFESSION_ERROR);
			return false;
		}
		if(skill.isStudied)
		{
			target.sendAlert(ErrorCode.ALERT_SKILL_IS_EXIST);
			return false;
		}
		if(skillTome.isHighLevelStudy(skill))
		{
			target.sendAlert(ErrorCode.ALERT_HASBEEN_HIGHER_SKILL);
			return false;
		}
		if(skillTome.isLowLevelNoStudy(skill))
		{
			target.sendAlert(ErrorCode.ALERT_SKILL_LEVEL_ERROR);
			return false;
		}
		Skill sk = (Skill) obj;
//		System.out.println("技能类型："+sk.getClass());
		if(sk instanceof PassiveSkill)//被动技能学习直接加属性
		{   
			PassiveSkill pSkill = (PassiveSkill) sk;
			boolean isCanStudy = false;
			for (int i = 0; i < pSkill.profession.length; i++) 
			{
				if(pSkill.profession[i] == 0)
					continue;
				if(isJob(target, pSkill.profession[i]))
				{
					isCanStudy = true;
					break;
				}
			}

//			System.out.println(pSkill.name+"能否学习："+isCanStudy+"   "+skillId);
			if(!isCanStudy)	
			{
				target.sendAlert(ErrorCode.ALERT_PROFESSION_ERROR);
				return false;
			}
			pSkill.setValue(target.getPlayer());
		}
		else if(sk instanceof ActiveSkill)
		{
			ActiveSkill aSkill = (ActiveSkill) sk;
			boolean isCanStudy = false;
			for (int i = 0; i < aSkill.profession.length; i++) 
			{
				if(aSkill.profession[i] == 0)
					continue;
				if(isJob(target, aSkill.profession[i]))
				{
					isCanStudy = true;
					break;
				}
			}
//			System.out.println(aSkill.name+"能否学习："+isCanStudy+"   "+skillId);
			if(!isCanStudy)	
			{
				target.sendAlert(ErrorCode.ALERT_PROFESSION_ERROR);
				return false;
			}
		}
		skillTome.addSkill(skillId);
		return true;
	}
	
	/**
	 * 喝药(战斗外)
	 * @param target
	 * @return
	 */
	public boolean eatDrug(PlayerController target)
	{
		EquipSet es = (EquipSet) target.getPlayer().getExtPlayerInfo("equipSet");
		int mhp = target.getPlayer().maxHitPoint + es.getTotalAtt("maxHitPoint");
		int mmp = target.getPlayer().maxMagicPoint + es.getTotalAtt("maxMagicPoint");
		boolean result = false;
		Object obj = DataFactory.getInstance().getGameObject(effect);

		PlayerBaseInfo base = target.getPlayer().getBaseInfo();
		
		if(obj == null)
		{
			target.sendAlert(ErrorCode.ALERT_EFFECT_NOT_EXIST);
			result = false;
		}
		else if(obj instanceof TimeEffect)
		{
			TimeEffect te = (TimeEffect) obj;
			TimeEffect effect = (TimeEffect) Effect.cloneObject(te);
			
			for(int i = 0 ; i < effect.dataType.length ; i ++)
			{
				if(effect.dataType[i].equals("0"))
					break;
				TimeEffect nte = new TimeEffect();
				effect.copyTo(nte);
				int point = 0,currPoint = 0;
				if(effect.dataType[i].equals("currentHP"))
				{
					currPoint = target.getPlayer().hitPoint;
					if(currPoint >= mhp)
						return false;
				}
				else if(effect.dataType[i].equals("currentMP"))
				{
					currPoint = target.getPlayer().magicPoint;
					if(currPoint >= mmp)
						return false;
				}
				if(effect.dataType[i].equals("battleMaxHP"))
					currPoint = target.getPlayer().maxHitPoint;
				else if(effect.dataType[i].equals("battleMaxMP"))
					currPoint = target.getPlayer().maxMagicPoint;
				
//				System.out.println("效果："+effect.dataType[i]+"  当前玩家身上的这个值为："+currPoint);
				if(effect.dataPattern[i] == 1)
					point = (int) (effect.effectPoint[i]);
				else if(effect.dataPattern[i] == 2)
					point = (int) (currPoint*(double)effect.effectPoint[i]/100);
				
//				System.out.println("加了后的值为："+point);
				if(effect.dataType[i].equals("currentHP"))
					target.getPlayer().setHitPoint(point,null);
				else if(effect.dataType[i].equals("currentMP"))
					target.getPlayer().setMagicPoint((int)(point * base.mult),null);
				if(effect.dataType[i].equals("battleMaxHP"))
					target.getPlayer().maxHitPoint += point;
				else if(effect.dataType[i].equals("battleMaxMP"))
					target.getPlayer().maxMagicPoint += point;
			}
			result = true;
//			System.out.println("玩家的血为："+target.getPlayer().hitPoint);
		}
		else if(obj instanceof FlashEffect)
		{
			FlashEffect effect = (FlashEffect) obj;
			
			FlashEffect fEffect = new FlashEffect();
			effect.copyTo(fEffect);
			
			if(fEffect.type == 2)
			{
				int resultPoint = 0;
				if(fEffect.dataPattern == 1) //数值
				{
					if(fEffect.dataType == 1) //当前生命力
					{
						if(target.getPlayer().hitPoint >= mhp)
							return false;
						resultPoint = fEffect.effectPoint;
						target.getPlayer().setHitPoint(resultPoint,null);
					}
					else if(fEffect.dataType == 2)  //当前精力
					{
						if(target.getPlayer().magicPoint >= mmp)
							return false;
						resultPoint = (int) (fEffect.effectPoint * base.mult);
						target.getPlayer().setMagicPoint(resultPoint,null);
					}
					else if(fEffect.dataType == 3)
					{
						if(target.getPlayer().hitPoint >= mhp && target.getPlayer().magicPoint >= mmp)
							return false;
						target.getPlayer().setHitPoint(effect.effectPoint,null);
						target.getPlayer().setMagicPoint(effect.effectPoint,null);
					}
				}
				else if(fEffect.dataPattern == 2)//百分比
				{
					if(fEffect.dataType == 1) //当前生命力
					{
						if(target.getPlayer().hitPoint >= mhp)
							return false;
						resultPoint = target.getPlayer().maxHitPoint * fEffect.effectPoint;
						target.getPlayer().setHitPoint(resultPoint,null);
					}
					else if(fEffect.dataType == 2)  //当前精力
					{
						if(target.getPlayer().magicPoint >= mmp)
							return false;
						resultPoint = (int) (target.getPlayer().maxMagicPoint * fEffect.effectPoint * base.mult);
						target.getPlayer().setMagicPoint(resultPoint,null);
					}
					else if(effect.dataType == 3)
					{
						if(target.getPlayer().hitPoint >= mhp && target.getPlayer().magicPoint >= mmp)
							return false;
						int hitPoint = (int) (mhp * (double)effect.effectPoint / 100);
						int magicPoint = (int) (mmp * (double)effect.effectPoint / 100);
						target.getPlayer().setHitPoint(hitPoint,null);
						target.getPlayer().setMagicPoint(magicPoint,null);
					}
				}
				result = true;
			}
		}
		return result;
	}
	
	/**
	 * 喝血的时候，给怪物设置AI
	 * @param effect
	 * @param target
	 */
	private void setBattleMonsterAI(Effect effect,PlayerController target)
	{
		if(effect instanceof TimeEffect)
		{
			TimeEffect tEffect = (TimeEffect) effect;
			if(tEffect.buffType == 1 && tEffect.dataType[0].equals("currentHP"))
			{
				if(target.getParent() instanceof PVEController)
				{
					PVEController pve = (PVEController) target.getParent();
					MonsterController[] monsters = pve.getMonsters();
					for (int i = 0; i < monsters.length; i++) 
					{
						if(monsters[i].getMonster().getMonsterAI() != null)
							monsters[i].getMonster().getMonsterAI().setComebackLifePlayer(pve.getPlayerIndex(target));
					}
				}
			}
		}
		else if(effect instanceof FlashEffect)
		{
			FlashEffect fEffect = (FlashEffect) effect;
			if(fEffect.type == 2 && fEffect.dataType == 1)
			{
				if(target.getParent() instanceof PVEController)
				{
					PVEController pve = (PVEController) target.getParent();
					MonsterController[] monsters = pve.getMonsters();
					for (int i = 0; i < monsters.length; i++) 
					{
						if(monsters[i].getMonster().getMonsterAI() != null)
							monsters[i].getMonster().getMonsterAI().setComebackLifePlayer(pve.getPlayerIndex(target));
					}
				}
			}
		}
	}
}
