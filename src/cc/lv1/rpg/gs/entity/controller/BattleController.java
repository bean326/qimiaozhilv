package cc.lv1.rpg.gs.entity.controller;

import vin.rabbit.net.AppMessage;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.impl.ArrayList;
import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.data.DC;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.container.PlayerContainer;
import cc.lv1.rpg.gs.entity.ext.Bag;
import cc.lv1.rpg.gs.entity.ext.BuffBox;
import cc.lv1.rpg.gs.entity.ext.OtherExtInfo;
import cc.lv1.rpg.gs.entity.ext.PetTome;
import cc.lv1.rpg.gs.entity.ext.PlayerBaseInfo;
import cc.lv1.rpg.gs.entity.ext.SkillTome;
import cc.lv1.rpg.gs.entity.impl.Goods;
import cc.lv1.rpg.gs.entity.impl.Mail;
import cc.lv1.rpg.gs.entity.impl.Player;
import cc.lv1.rpg.gs.entity.impl.battle.PlayerBattleTmp;
import cc.lv1.rpg.gs.entity.impl.battle.SpriteBattleTmp;
import cc.lv1.rpg.gs.entity.impl.battle.effect.TimeEffect;
import cc.lv1.rpg.gs.entity.impl.battle.skill.ActiveSkill;
import cc.lv1.rpg.gs.entity.impl.pet.Pet;
import cc.lv1.rpg.gs.net.SMsg;
import cc.lv1.rpg.gs.util.FontConver;
/**
 * 战斗控制器
 * @author dxw
 * 
 *
 */
public class BattleController extends PlayerContainer
{
	public static final int TEAM1 = 1;
	
	public static final int TEAM2 = 2;

	public static final int SKILLPROCESSOR = 1;
	
	public static final int PROPPROCESSOR = 2;
	
	public static final int RUNAWAY = 3;
	
	public static final int PETSKILLPROCESSOR = 4;
	
	public static final int RESETMONEY = 10;
	
	public static final int AUTOEXITTIME = 1000 * 12;
	
	/** 选择原地复活后送给玩家的物品 */
	public static final int RESETMAILGOODS = 1045000134;
	
	protected RoomController room;
	
	protected int fightType;
	
	protected boolean isFinish;
	
	public boolean isWin = false;
	
	public boolean isTimeOver = false;
	
	public long endTime;
	
	/** 是否检测了怪物掉宝 */
	public boolean isCheckDrop = false;
	
	/**
	 * 失败方总共所掉落的物品(PVE是怪物，PVP是失败方的玩家)
	 */
	protected Goods[] battleGoods = new Goods[0];
	
	
	/**
	 * 获得的游戏币
	 */
	protected int battlePoint;
	
	/**
	 * 获得的荣誉值
	 * @param room
	 */
	protected int battleHonor;
	
	public boolean isTimeOver()
	{
		return isTimeOver;
	}
	
	public void setTimeOver(boolean flag)
	{
		isTimeOver = flag;
	}
	
	public void setParent(RoomController room)
	{
		this.room = room;
	}
	
	public RoomController getRoom()
	{
		return room;
	}
	
	protected void sendEquipMoney(PlayerController target)
	{
		Object obj = DataFactory.getInstance().getGameObject(RESETMAILGOODS);
		if(obj != null || obj instanceof Goods)
		{
			Goods goods = (Goods) obj;
			Goods newGoods = (Goods) Goods.cloneObject(goods);
			newGoods.objectIndex = GameServer.getInstance().getDatabaseAccessor().getGoodsObjIndex();
			Mail mail = new Mail(DC.getString(DC.BATTLE_1));
			mail.setTitle(DC.getString(DC.BATTLE_2));//礼物赠送
			mail.setContent(DC.getString(DC.BATTLE_3));
			mail.addAttach(newGoods);
			mail.send(target);
		}
	}
	
	
	public void setFighters(PlayerController [] players,SpriteController [] sprites)
	{
		int length = players.length;
		for(int i = 0 ; i < length ; i ++)
		{
			if(players[i] == null)
				continue;
			if(!players[i].isOnline())
				continue;
			if(players[i].getParent() instanceof BusinessController)
			{
				BusinessController bc = (BusinessController) players[i].getParent();
				bc.removePlayer(players[i]);
			}
			players[i].setParent(this);
			players[i].getPlayer().state = Player.STATE_FIGHTING;
			players[i].wgCount = 0;
			players[i].currentTimeMsg = 0;
			players[i].setSkillProcessTime();
			PetTome pt = (PetTome) players[i].getPlayer().getExtPlayerInfo("petTome");
			Pet pet = pt.getActiveBattlePet();
			if(pet != null && !pet.isMaxBattlePoint())
			{
				pet.battlePoint++;
			}
		}
		
		if(sprites[0] instanceof PlayerController)
		{
			PlayerController[] targets = (PlayerController[]) sprites;
			int len = targets.length;
			for(int i = 0 ; i < len ; i ++)
			{
				if(targets[i] == null)
					continue;
				if(!targets[i].isOnline())
					continue;
				if(targets[i].getParent() instanceof BusinessController)
				{
					BusinessController bc = (BusinessController) targets[i].getParent();
					bc.removePlayer(targets[i]);
				}
				targets[i].setParent(this);
				targets[i].getPlayer().state = Player.STATE_FIGHTING;
				targets[i].wgCount = 0;
				targets[i].currentTimeMsg = 0;
				targets[i].setSkillProcessTime();
				PetTome pt = (PetTome) targets[i].getPlayer().getExtPlayerInfo("petTome");
				Pet pet = pt.getActiveBattlePet();
				if(pet != null && !pet.isMaxBattlePoint())
				{
					pet.battlePoint++;
				}
			}
		}
	}
	
	
	public void update(long currentMillis)
	{
		
		
		for (int i = 0; i < playerList.size() ; i++)
		{
			PlayerController everyOne = (PlayerController)playerList.get(i);
			
			if(everyOne == null)
				continue;
			if(!everyOne.isOnline())
				continue;
			
			PlayerBattleTmp playerTemp = (PlayerBattleTmp)everyOne.getAttachment();
			
			if(playerTemp == null)
				continue;
			
			playerTemp.update(currentMillis);
		}
	}
	
	public void init()
	{
		
	}

	
	public void dispatchMsg(int sMsg,ByteBuffer buff)
	{
		super.dispatchMsg(sMsg, buff);

	}
	
	/**
	 * 发送给本房间内的非战斗玩家 进入/退出 战斗
	 * @param isBattle 
	 * @param target
	 */
	public void dispatchIsBattleWithoutRoom(boolean isBattle,PlayerController target)
	{
		ByteBuffer buffer = new ByteBuffer(5);
		buffer.writeBoolean(isBattle);

		TeamController tc = target.getTeam();
		if(tc == null)
			buffer.writeInt(target.getID());
		else
			buffer.writeInt(tc.getLeader().getID());
		//发送消息给非战斗人员的id，客户端予以隐藏
		dispatchOutOfBattleWithThisRoom(SMsg.S_ROOM_PLAYER_ISBATTLE,buffer);
	}
	
	/**
	 * 分发消息给非战斗玩家
	 * @param sMsg
	 * @param buff
	 */
	protected void dispatchOutOfBattleWithThisRoom(int sMsg,ByteBuffer buff)
	{
		List list = room.getPlayerList();
		int size = list.size();
		ByteBuffer buffer = null;
		for(int i = 0 ; i < size; i ++)
		{
			PlayerController target = (PlayerController)list.get(i);
			
			if(isTargetNull(target))
				continue;
			
			if(target.getParent() instanceof BattleController)
				continue;
			
			buffer = new ByteBuffer(buff.getBytes());
			target.getNetConnection().sendMessage(new AppMessage(sMsg,buffer));
		}
	}
	
	public void confirmBattleOver() //子类
	{
		
	}
	
	/**
	 * 目标是否为空或是否在线
	 * @param target 
	 * @return true 为空或不在线 false 在线 
	 */
	public boolean isTargetNull(PlayerController target)
	{
		if(target == null || !target.isOnline())
			return true;
		return false;
	}
	

	public void uninit(int type)
	{
		int playerCount = getPlayerCount();

		if(playerCount <= 0)
			return;
		
		for(int i = 0 ; i < playerCount ; i ++)
		{
			PlayerController everyone = (PlayerController)playerList.get(i);
			if(isTargetNull(everyone))
				continue;
			PlayerBattleTmp pbt = (PlayerBattleTmp) everyone.getAttachment();
			removeEffect(pbt.getEffectList(),pbt);
			removeHaloEffect(pbt.getBuffBox().getEffectList(),everyone);
			if(type == 1)
				everyone.setParent(room);
			everyone.setExtLifeAndMagic();
			everyone.sendAlwaysValue();
		}
		
		ByteBuffer endBattleBuff = new ByteBuffer(1);
		endBattleBuff.writeBoolean(true);
		dispatchMsg(SMsg.S_BATTLE_PLAYER_BATTLEEND, endBattleBuff);
//		System.out.println("本场战斗结束......");
	}
	
	
	public void removeHaloEffect(List list,PlayerController target)
	{
		BuffBox bb = (BuffBox)target.getPlayer().getExtPlayerInfo("buffBox");
		int size = list.size();

		for (int i = 0; i < size; i++)
		{
			Object obj = list.get(i);
			if(obj instanceof TimeEffect)
			{
				TimeEffect te = (TimeEffect) obj;
				bb.removeEffect(te,target);
			}
		}
	}
	
	
	public void removeEffect(List list,SpriteBattleTmp sbt)
	{
		if(!(sbt instanceof PlayerBattleTmp))
			return;
		PlayerBattleTmp pbt = (PlayerBattleTmp) sbt;
		
		for (int j = 0; j < list.size(); j++) 
		{
			if(list.get(j) instanceof TimeEffect)
			{
				TimeEffect effect = (TimeEffect) list.get(j);
				pbt.removeEffect(effect);
			}
		}
	}
	
	public void removePlayer(PlayerController target)
	{	
		if(target.getTeam() == null)
		{
			if(target.getParent() instanceof PVPController)
				removePVP(target);
			else if(target.getParent() instanceof PVEController)
				removePVE(target);
			
			isFinish = true;
		}
		else
		{
			if(target.getTeam().isLeader(target))
			{
				if(target.getTeam().getPlayerCount() > 2)
				{
					PlayerController nextLeader = target.getTeam().getNextLeader();
					target.getTeam().leaderToOther(target,nextLeader);
				}
			}
			
			room.deleteTeam(target.getTeam());
			
			target.getTeam().playerLeaveTeam(target);
			
			removeBP(target);
			
			checkDeadState();
			
		}
		
	}
	
	/**
	 *  逃跑
	 */
	public void escBattle(PlayerController target)
	{	
		if(target.getTeam() == null)
		{
			if(target.getParent() instanceof PVPController)
				removePVP(target);
			else if(target.getParent() instanceof PVEController)
				removePVE(target);
			
			isFinish = true;
		}
		else
		{
			if(target.getTeam().isLeader(target))
			{
				if(target.getParent() instanceof PVPController)
					removePVP(target);
				else if(target.getParent() instanceof PVEController)
					removePVE(target);
				
				isFinish = true;
			}
			else
			{
				PlayerController[] players = target.getTeam().getPlayers();
				int count = 0;
				for (int i = 0; i < players.length; i++) 
				{
					if(!players[i].getAttachment().isDead())
						count++;
				}
				int flag = 0;
				if(target.getAttachment().isDead())
					flag = 0;
				else
					flag = 1;
				if(count <= flag)
				{
					if(target.getParent() instanceof PVPController)
						removePVP(target);
					else if(target.getParent() instanceof PVEController)
						removePVE(target);
				}
				else
					removeBP(target);
			}
		}
		
	}
	
	
	
	private void removePVE(PlayerController target)
	{
		PVEController pve = (PVEController) target.getParent();
		int len = pve.getPlayers().length;
		for (int i = 0; i < len; i++) 
		{
			PlayerController player = pve.getPlayers()[i];
			if(isTargetNull(player))
				continue;
			removeBP(player);
		}
		for (int i = 0; i < pve.getMonsters().length; i++)
		{
			pve.getMonsters()[i].setParent(room);
		}
	}
	
	private void removePVP(PlayerController target)
	{
		PVPController pvp = (PVPController) target.getParent();
		PlayerController[] players = null;
		if(target.getAttachment().getTeamNo() == 1)
		{
			players = pvp.getPlayers();
			pvp.dispathWinOrLose(pvp.getPlayerList(),1);
			pvp.dispathWinOrLose(pvp.getTargetPlayerList(),1);
			int len = pvp.getTargetPlayers().length;
			for (int i = 0; i < len; i++) 
			{
				PlayerController player = pvp.getTargetPlayers()[i];
				if(isTargetNull(player))
					continue;
				removeBP(player);
			}
		}
		else if(target.getAttachment().getTeamNo() == 2)
		{
			players = pvp.getTargetPlayers();
			pvp.dispathWinOrLose(pvp.getPlayerList(),2);
			pvp.dispathWinOrLose(pvp.getTargetPlayerList(),2);
			int len = pvp.getPlayers().length;
			for (int i = 0; i < len; i++) 
			{
				PlayerController player = pvp.getPlayers()[i];
				if(isTargetNull(player))
					continue;
				removeBP(player);
			}
		}
		for (int i = 0; i < players.length; i++) 
		{
			removeBP(players[i]);
		}
	}
	
	public void removeBP(PlayerController target)
	{
		PlayerBattleTmp pbt = (PlayerBattleTmp) target.getAttachment();
		removeEffect(pbt.getEffectList(),pbt);
		removeHaloEffect(pbt.getBuffBox().getHaloList(), target);
		removeBattlePlayer(target);
		
		target.fixPlayerBaseInfo(0);
		target.setExtLifeAndMagic();
		target.sendAlwaysValue();
	}
	
	public void removeBattlePlayer(PlayerController target)
	{   
//		target.setExtLifeAndMagic();
		
		super.removePlayer(target);
		
		ByteBuffer buffer = new ByteBuffer(1);
		buffer.writeBoolean(true);
		AppMessage msg = new AppMessage(SMsg.S_BATTLE_REMOVE_COMMAND,buffer);
		target.getNetConnection().sendMessage(msg);
		
		buffer = new ByteBuffer(5);
		int teamNo = target.getAttachment().getTeamNo();
		int index = target.getAttachment().getIndex();
		buffer.writeByte(teamNo);
		buffer.writeByte(index);
		dispatchMsg(SMsg.S_BATTLE_IO_COMMAND,buffer);
		
		target.sendAlwaysValue();
		
		dispatchIsBattleWithoutRoom(false,target);
	
		if(playerList.size() <= 0)
		{
			isFinish = true;
		}
		
		target.setParent(room);
		
		if(!target.isOnline())
			room.removePlayer(target);
	}
	

	protected void processGoods(PlayerController target, AppMessage msg)
	{
		Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
		bag.useGoods(target, msg.getBuffer());
		target.skillStartTime = target.getNetConnection().getLastReadTime();
		target.skillNeedTime = 4800;
	}
	
	protected void processSkill(PlayerController target, AppMessage msg)
	{
	}
	
	
	
	


	public void checkDeadState()//子类 
	{
	
	}
	
	public boolean isFinish()
	{
		return this.isFinish;
	}
	
	
	/**
	 * 光环启动
	 * @param player
	 * @param sprite
	 */
	public void haloStart(PlayerController[] players,BattleController battle)
	{
		List officers = getOfficer(players);//只有军官才有光环
		//////////////测试时用///////////////////////////////////////////////////////////////
//		List officers = new ArrayList(players.length);
//		for (int i = 0; i < players.length; i++) {
//			officers.add(players[i]);
//		}
		/////////////////////////////////////////////////////////////////////////////
		for (int i = 0; i < officers.size(); i++) 
		{
			PlayerController player = (PlayerController) officers.get(i);
			if(isTargetNull(player))
				continue;
			SpriteBattleTmp sbt = player.getAttachment();
			SkillTome skillTome = (SkillTome) player.getPlayer().getExtPlayerInfo("skillTome");
			List haloList = skillTome.getHaloSkills();//获取光环 

			for (int j = 0; j < haloList.size(); j++) 
			{
				ActiveSkill aSkill = (ActiveSkill) haloList.get(j);
				ActiveSkill maxLevelSkill = getSameHaloMaxLevel(getHalo(officers),aSkill.name);
				if(maxLevelSkill.id != aSkill.id)
					continue;

				if(battle instanceof PVEController || battle instanceof CopyPVEController || battle instanceof EventPVEController)
				{
					PlayerController[] teamPlayers = null;
					MonsterController[] monsters = null;
					if(battle instanceof PVEController)
					{
						teamPlayers = ((PVEController) battle).getPlayers();
						monsters = ((PVEController) battle).getMonsters();
					}
					else if(battle instanceof CopyPVEController)
					{
						teamPlayers = ((CopyPVEController) battle).getPlayers();
						monsters = ((CopyPVEController) battle).getMonsters();
					}
					else if(battle instanceof EventPVEController)
					{
						teamPlayers = ((EventPVEController) battle).getPlayers();
						monsters = ((EventPVEController) battle).getMonsters();
					}
					
					if(aSkill.targetType[0] == ActiveSkill.TARGET_TYPE_PLAYERS)
					{
						for (int k = 0; k < teamPlayers.length; k++) 
						{
							PlayerController target = teamPlayers[k];
							if(isTargetNull(target))
								continue;
							sbt.setSkillTarget(target);
							sbt.processActiveSkill(aSkill);
						}
					}
					else if(aSkill.targetType[0] == ActiveSkill.TARGET_TYPE_MONSTERS)
					{
						for (int k = 0; k < monsters.length; k++) 
						{
							sbt.setSkillTarget(monsters[k]);
							sbt.processActiveSkill(aSkill);
						}
					}
				}
				else if(battle instanceof PVPController || battle instanceof PartyPKController)
				{
					PlayerController[] targets = null;
					if(aSkill.targetType[0] == ActiveSkill.TARGET_TYPE_PLAYERS)
					{
						if(sbt.getTeamNo() == BattleController.TEAM1)
						{
							if(battle instanceof PVPController)
								targets = ((PVPController) battle).getPlayers();
							else if(battle instanceof PartyPKController)
								targets = ((PartyPKController) battle).getPlayers();
						}
						else if(sbt.getTeamNo() == BattleController.TEAM2)
						{
							if(battle instanceof PVPController)
								targets = ((PVPController) battle).getTargetPlayers();
							else if(battle instanceof PartyPKController)
								targets = ((PartyPKController) battle).getTargetPlayers();
						}
					}
					else if(aSkill.targetType[0] == ActiveSkill.TARGET_TYPE_MONSTERS)
					{
						if(sbt.getTeamNo() == BattleController.TEAM1)
						{
							if(battle instanceof PVPController)
								targets = ((PVPController) battle).getTargetPlayers();
							else if(battle instanceof PartyPKController)
								targets = ((PartyPKController) battle).getTargetPlayers();
						}
						else if(sbt.getTeamNo() == BattleController.TEAM2)
						{
							if(battle instanceof PVPController)
								targets = ((PVPController) battle).getPlayers();
							else if(battle instanceof PartyPKController)
								targets = ((PartyPKController) battle).getPlayers();
						}
					}
					int len = targets.length;
					for (int k = 0; k < len; k++) 
					{
						if(isTargetNull(targets[k]))
							continue;
						sbt.setSkillTarget(targets[k]);
						sbt.processActiveSkill(aSkill);
					}
				}
			}
		}
	}
	
	
	/**
	 * 获取队伍中所有军官的光环技能集合
	 * @param officers
	 * @return
	 */
	private List getHalo(List officers)
	{
		List haloList = new ArrayList();
		for (int i = 0; i < officers.size(); i++) 
		{
			PlayerController player = (PlayerController) officers.get(i);
			if(isTargetNull(player))
				continue;
			SkillTome skillTome = (SkillTome) player.getPlayer().getExtPlayerInfo("skillTome");
			for (int j = 0; j < skillTome.getHaloSkills().size(); j++)
			{
				haloList.add(skillTome.getHaloSkills().get(j));//队伍中所有军官的光环技能
			}
		}
		return haloList;
	}
	

	
	/**
	 * 获取同一队伍中所有军官的光环技能中等级最高的
	 * @param haloList
	 * @param id
	 * @return
	 */
	private ActiveSkill getSameHaloMaxLevel(List haloList,String name)
	{
		List list = new ArrayList();
		for (int i = 0; i < haloList.size(); i++) 
		{
			ActiveSkill aSkill = (ActiveSkill) haloList.get(i);
			if(aSkill.name.equals(name))
				list.add(aSkill);
		}
		ActiveSkill aSkill = (ActiveSkill) list.get(0);
		for (int i = 0; i < list.size(); i++)
		{
			ActiveSkill as = (ActiveSkill) list.get(i);
			if(aSkill.level < as.level)
				aSkill = as;
		}
		return aSkill;
	}
	
	/**
	 * 获取队伍中所有军官
	 * @param players
	 * @return
	 */
	private List getOfficer(PlayerController[] players)
	{
		List officers = new ArrayList(players.length);
		for (int i = 0; i < players.length; i++) 
		{
//			if(players[i].getPlayer().upProfession == 1)//军官才有光环
			if(players[i].getPlayer().profession == 1)//军官才有光环
			{
				officers.add(players[i]);
			}
		}
		return officers;
	}
	
	public void setBattleGoods(Goods goods)
	{
		Goods[] sGoods = new Goods[battleGoods.length+1];
		for (int i = 0; i < battleGoods.length; i++) 
			sGoods[i] = battleGoods[i];
		sGoods[battleGoods.length] = goods;
		battleGoods = sGoods;
	}
	
	public Goods[] getGoodsList()
	{
		return this.battleGoods;
	}

	public void setBattleHonor(int honor)
	{
		this.battleHonor += honor;
	}
	
	public void setBattlePoint(int point)
	{
		this.battlePoint += point;
	}
	
	public int getBattlePoint()
	{
		return this.battlePoint;
	}
	
	
	protected void sendResetInfo(PlayerController target) 
	{
		if(target.getTeam() != null)
		{
			StringBuffer sb = new StringBuffer();
			sb.append(target.getName());
			sb.append(DC.getString(DC.BATTLE_8));
			target.sendGetGoodsInfo(2, false, sb.toString());
		}
	}
	/**
	 * 修正玩家数据
	 * @param players
	 * @param isReset  是否是战斗结束后选择复活
	 */
	protected void fixPlayerBaseInfo(PlayerController[] players,boolean isReset)
	{
		for (int i = 0; i < players.length; i++)
		{
			if(isTargetNull(players[i]))
				continue;

			players[i].fixPlayerBaseInfo(0);
		}
		
		if(isReset)
		{
			for (int i = 0; i < players.length; i++) 
			{
				if(isTargetNull(players[i]))
					continue;
				players[i].setFullHitAndMagic();
				players[i].sendAlwaysValue();
			}
		}
		else
		{
			for (int i = 0; i < players.length; i++)
			{
				if(isTargetNull(players[i]))
					continue;
				players[i].setExtLifeAndMagic();
				players[i].sendAlwaysValue();
			}
		}
	}
	
	
	
	
	/**
	 * 消息通道
	 * @param target
	 * @param msg
	 */
	public void clientMessageChain(PlayerController target, AppMessage msg)
	{
		int type = msg.getType();
		
		if(type == SMsg.C_BATTLE_REMOVE_COMMAND)
		{
			if(target.isEsc())
			{
				if(target.getAttachment().isDead()) //如果队长死亡则不能逃跑
					return;
				if(endTime != 0)
					return;
				TeamController team = target.getTeam();
				if(team != null)
				{
					if(!team.isLeader(target))
						return;
					PlayerController [] players = team.getPlayers();
					for (int i = 0; i < players.length; i++)
					{
						if(isTargetNull(players[i]))
							continue;
						if(players[i] != null)
						{
							players[i].getPlayer().escTimer = 
								target.getNetConnection().getPingTime();
							ByteBuffer buffer = new ByteBuffer(4);
							buffer.writeInt(PlayerController.ESCTIME);
							players[i].getNetConnection().sendMessage
							(new SMsg(SMsg.S_CLEAR_ESC_COMMAND,buffer));
						}
					}
				}
				else
				{
					ByteBuffer buffer = new ByteBuffer(4);
					buffer.writeInt(PlayerController.ESCTIME);
					target.getNetConnection().sendMessage
					(new SMsg(SMsg.S_CLEAR_ESC_COMMAND,buffer));
				}
				escBattle(target);
				
				
				if(target.getTeam() == null)
					target.sendActivePoint();
				else
					target.getTeam().sendActivePointTo();
			}
			else
			{
//				System.out.println("BattleController Esc fail");
			}
		}
	}
	
	public PlayerController getAnyOnePlayer(PlayerController[] players)
	{
		for (int i = 0; i < players.length; i++)
		{
			if(isTargetNull(players[i]))
				continue;
			return players[i];
		}
		return null;
	}
	
	public void setM_AInfo()
	{
		if(room.id == DataFactory.MARRYROOM || room.id == DataFactory.INITROOM 
				|| room.isGoldPartyRoom || room.getParent().id == DataFactory.NOVICEAREA
				|| room.isPartyRoom || room.id == DataFactory.HONORROOM)
			return;
		for (int i = 0; i < playerList.size(); i++) 
		{
			PlayerController player = (PlayerController) playerList.get(i);
			if(player == null || !player.isOnline())
				continue;
			if(player.getAam() != null)
			{
				if(player.getAam().isSameBattle())
				{
					OtherExtInfo oei = (OtherExtInfo) player.getPlayer().getExtPlayerInfo("otherExtInfo");
					if(oei.aamCount+1 == OtherExtInfo.MAXAAMCOUNT)
					{
						Mail mail = new Mail(DC.getString(DC.BATTLE_1));
						mail.setTitle(DC.getString(DC.BATTLE_9));
						StringBuffer sb = new StringBuffer();
						sb.append(DC.getString(DC.BATTLE_10));
						sb.append(":\n");
						sb.append(DC.getString(DC.BATTLE_11));
						sb.append(",");
						sb.append(DC.getString(DC.BATTLE_12));
						mail.setContent(player.getName()+sb.toString());
						mail.send(player);
						sb = null;
					}
					if(oei.aamCount < OtherExtInfo.MAXAAMCOUNT)
					{	
						oei.aamCount++;
						player.sendFacebookInfo(3, null, "", oei.aamCount);
						player.sendGetGoodsInfo(1, false, DC.getString(DC.BATTLE_13)+": "+oei.aamCount);
						player.getAam().writeBaseTo(player);
					}
				}
			}
		}
		
		
	}
	
	/**
	 * 子类
	 */
	public void sendNoticeInfo()
	{
		
	}
}
