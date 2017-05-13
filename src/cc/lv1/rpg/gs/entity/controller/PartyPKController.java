
package cc.lv1.rpg.gs.entity.controller;

import vin.rabbit.net.AppMessage;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.impl.ArrayList;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.data.DC;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.data.Exp;
import cc.lv1.rpg.gs.entity.ext.Bag;
import cc.lv1.rpg.gs.entity.ext.BuffBox;
import cc.lv1.rpg.gs.entity.ext.EquipSet;
import cc.lv1.rpg.gs.entity.ext.PetTome;
import cc.lv1.rpg.gs.entity.ext.ReviseBaseInfo;
import cc.lv1.rpg.gs.entity.ext.SkillTome;
import cc.lv1.rpg.gs.entity.impl.Goods;
import cc.lv1.rpg.gs.entity.impl.Monster;
import cc.lv1.rpg.gs.entity.impl.battle.PlayerBattleTmp;
import cc.lv1.rpg.gs.entity.impl.battle.SpriteBattleTmp;
import cc.lv1.rpg.gs.entity.impl.battle.skill.ActiveSkill;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsProp;
import cc.lv1.rpg.gs.entity.impl.pet.Pet;
import cc.lv1.rpg.gs.gui.MainFrame;
import cc.lv1.rpg.gs.net.SMsg;
import cc.lv1.rpg.gs.other.ErrorCode;
import cc.lv1.rpg.gs.util.FontConver;

public class PartyPKController extends BattleController
{
	
	/** 失败后需要加的虚弱状态 */
	public static final int LOSESTATEGOODS = 1045000142;
	
	/** 胜利后需要加的等待状态 */
	public static final int WINSTATEGOODS = 1045000251;
	
	protected List targetPlayerList = new ArrayList(5);
	
	private PlayerController[] players;
	
	private PlayerController[] targetPlayers;
	
	private int playerLength;
	
	private int targetPlayerLength;
	
	public void update(long timeMillis)
	{
		super.update(timeMillis);
		
		for (int i = 0; i < targetPlayers.length; i++) 
		{
			if(isTargetNull(targetPlayers[i]))
				continue;
			
			targetPlayers[i].update(timeMillis);
			
			PlayerBattleTmp pbt = (PlayerBattleTmp) targetPlayers[i].getAttachment();
			
			if(pbt == null)
				continue;
			
			pbt.update(timeMillis);
		}
	}
	
	public void clientMessageChain(PlayerController target, AppMessage msg)
	{
		if(isFinish)
		{
//			System.out.println("isFinish "+ isFinish);
			return;
		}
		
		int type = msg.getType();
		
		if(type == SMsg.C_BATTLE_ACTION_COMMAND)
		{
			if(!target.isOnline())
				return;
			if(target.getAttachment().isDead())
				return;
			if(!target.isSkillMaybeUse())
				return;
			
			int actionType = msg.getBuffer().readByte();
	
			if(actionType == BattleController.SKILLPROCESSOR) //释放技能
			{
				processSkill(target,msg);
			}
			else if(actionType ==2) //释放道具
			{
				processGoods(target,msg);
			}
			else if(actionType == 3) //逃跑
			{
//				removePlayer(target);
			}
			else 
				return;
		}
		else if(type == SMsg.C_BATTLE_PLAYER_BATTLEEND)
		{
			if(!isPlayerDead() && !isTargetPlayerDead())
				return;
			uninit();
		}
//		else if(type == SMsg.C_BATTLE_PLAYER_RESET_COMMAND)
//		{
//			resetChoose(target, msg.getBuffer());
//		}
//		else if(type == SMsg.C_BATTLE_REMOVE_COMMAND)
//		{
//			escBattle(target);
//		}
		else
		{
			super.clientMessageChain(target, msg);
		}
	}
	


	protected void processSkill(PlayerController target, AppMessage msg)
	{
		int skillId = msg.getBuffer().readInt(); //发送技能的id
		int targetIndex = msg.getBuffer().readByte(); //发送目标的index
		
		if(targetIndex < 0 || targetIndex > 4)
			return;
		
		SkillTome skillTome= (SkillTome)target.getPlayer().getExtPlayerInfo("skillTome");
		ActiveSkill skill  = skillTome.getActiveSkill(skillId);
		if(skill == null)
		{
			target.sendAlert(ErrorCode.ALERT_SKILL_NOT_EXIST);
			return;
		}
		if(skill.profession[0] != 0)
		{
			if(skill.profession[0] == Monster.MONSTERSKILLTYPE)
				return;
		}
		
		if(!skill.isProcessSkill(target))
			return;
		if(!target.isSkillUse(skill))
			return;
		
		EquipSet equipSet = (EquipSet) target.getPlayer().getExtPlayerInfo("equipSet");
		if(!equipSet.checkEquip(skill.weaponType))
		{
			target.sendGetGoodsInfo(1, false,DC.getString(DC.BATTLE_15));
			return;
		}
		
		if(!skill.checkMagicEnough(target))
		{
			target.sendGetGoodsInfo(1, false,DC.getString(DC.BATTLE_16));
			return;
		}
		
		PlayerBattleTmp sbt = (PlayerBattleTmp) target.getAttachment();
		
		if(sbt == null)
		{
			target.sendAlert(ErrorCode.ALERT_INIT_BATTLE_FAIL);
			return;
		}
		
		if(sbt.isLocked())
		{
			target.sendAlert(ErrorCode.ALERT_PLAYER_IS_LOCKED);
			return;
		}
		
		sbt.isSkillUsing = true;
		
		if(skill.targetType[0] == ActiveSkill.TARGET_TYPE_PLAYERS)//己方
		{
		    if(sbt.getTeamNo() == BattleController.TEAM2)
			{
		    	if(targetIndex >= targetPlayers.length)
				{
//		    		target.sendAlert(ErrorCode.ALERT_OBJECT_INDEX_OVERRUN);
					return;
				}
		    	if(targetPlayers[targetIndex] == null)
				{
//					target.sendAlert(ErrorCode.ALERT_OBJECT_IS_NULL);
			    	return;
				}
				sbt.setSkillTarget(targetPlayers[targetIndex]);
			}
			else
			{
				if(targetIndex >= players.length)
				{
//					target.sendAlert(ErrorCode.ALERT_OBJECT_INDEX_OVERRUN);
					return;
				}
				if(players[targetIndex] == null)
				{
//					target.sendAlert(ErrorCode.ALERT_OBJECT_IS_NULL);
			    	return;
				}
				sbt.setSkillTarget(players[targetIndex]);
			}
		}
		else if(skill.targetType[0] == ActiveSkill.TARGET_TYPE_MONSTERS) //2：敌方
		{
			if(sbt.getTeamNo() == BattleController.TEAM2)
			{
				if(targetIndex >= players.length)
				{
//					target.sendAlert(ErrorCode.ALERT_OBJECT_INDEX_OVERRUN);
					return;
				}
				if(players[targetIndex] == null)
				{
//					target.sendAlert(ErrorCode.ALERT_OBJECT_IS_NULL);
			    	return;
				}
				if(players[targetIndex].getAttachment().isDead())
				{
					for (int i = 0; i < players.length; i++) 
			    	{
						if(players[i] != null && !players[i].getAttachment().isDead())
						{
							targetIndex = i;
							break;
						}
					}
				}
				if(players[targetIndex].getAttachment().isDead())
					return;
				
				sbt.setSkillTarget(players[targetIndex]);
			}
			else
			{
				if(targetIndex >= targetPlayers.length)
				{
//					target.sendAlert(ErrorCode.ALERT_OBJECT_INDEX_OVERRUN);
					return;
				}
				if(targetPlayers[targetIndex] == null)
				{
//					target.sendAlert(ErrorCode.ALERT_OBJECT_IS_NULL);
			    	return;
				}
				if(targetPlayers[targetIndex].getAttachment().isDead())
				{
					for (int i = 0; i < targetPlayers.length; i++) 
			    	{
						if(targetPlayers[i] != null && !targetPlayers[i].getAttachment().isDead())
						{
							targetIndex = i;
							break;
						}
					}
				}
				if(targetPlayers[targetIndex].getAttachment().isDead())
					return;
				sbt.setSkillTarget(targetPlayers[targetIndex]);
			}
		}
		else if(skill.targetType[0] == ActiveSkill.TARGET_TYPE_ONESELF)//3.自己
		{
			sbt.setSkillTarget(target);
		}
		else
		{
			System.out.println("PartyPKController 240 other target Object");
			return;
		}
		if(!(sbt instanceof PlayerBattleTmp))
		{
			MainFrame.println("...............................");
			MainFrame.println("PartyPKController error:"+target.getName()+"..."+target.getAttachment().getClass());
			MainFrame.println("...............................");
		}
		boolean result = sbt.processActiveSkill(skill);
		
		if(result)
		{
			skill.processTime = target.getNetConnection().getLastReadTime();
			target.skillStartTime = skill.processTime;
			target.skillNeedTime = skill.CDTimer==0?4800:skill.CDTimer;
		}
		sbt.isSkillUsing = false;
	}

	public void setFighters(PlayerController[] players, SpriteController[] sprite)
	{
		PlayerController [] targetPlayers = (PlayerController[])sprite;
		
		super.setFighters(players, targetPlayers);
		
		this.players = players;
		this.targetPlayers = targetPlayers;
		this.playerLength = players.length;
		this.targetPlayerLength = targetPlayers.length;
		
		
		 PlayerBattleTmp pt = null;
		 
		 int length = players.length;
		 int i = 0;
		 for( ; i < length ; i ++)
		 {
			 if(isTargetNull(players[i]))
				continue;
			 
			 pt = new PlayerBattleTmp();
			 players[i].setAttachment(pt);
			 pt.setBattle(this);
			 pt.setTeamNo(BattleController.TEAM1);
			 pt.setIndex(i);
			 pt.setPlayer(players[i]);
			 playerList.add(players[i]); 
		 }
		 
		 length = targetPlayers.length;
		 for(i = 0; i < length ; i ++)
		 {
			 if(isTargetNull(targetPlayers[i]))
				continue;
			 
			 pt = new PlayerBattleTmp();
			 targetPlayers[i].setAttachment(pt);
			 pt.setBattle(this);
			 pt.setTeamNo(BattleController.TEAM2);
			 pt.setIndex(i); 
			 pt.setPlayer(targetPlayers[i]);
			 targetPlayerList.add(targetPlayers[i]); 
		 }
		 
	}
	
	
	public void writeTo(ByteBuffer buffer)
	{
		int i = 0;
		buffer.writeByte(BattleController.TEAM1);
		buffer.writeByte(playerLength);//玩家数量
		for (i = 0; i < playerLength; i++)
		{
			if(isTargetNull(players[i]))
				continue;
			buffer.writeByte(i);
			buffer.writeInt(players[i].getID());
			players[i].writeBattlePetInfo(buffer);
			players[i].sendAlwaysValue();
		}
		buffer.writeByte(BattleController.TEAM2);
		buffer.writeByte(targetPlayerLength);
		for (i = 0; i < targetPlayerLength; i++) 
		{
			if(isTargetNull(targetPlayers[i]))
				continue;
			buffer.writeByte(i);
			EquipSet es = (EquipSet) targetPlayers[i].getPlayer().getExtPlayerInfo("equipSet");
			buffer.writeInt(targetPlayers[i].getID());
			buffer.writeUTF(targetPlayers[i].getName());
			buffer.writeInt(targetPlayers[i].getPlayer().iconId);
			buffer.writeInt(targetPlayers[i].getPlayer().modelId);
			buffer.writeInt(targetPlayers[i].getPlayer().modelMotionId);
			buffer.writeInt(targetPlayers[i].getPlayer().level);
			buffer.writeInt(targetPlayers[i].getPlayer().hitPoint);
			buffer.writeInt(es.getTotalAtt("maxHitPoint")+targetPlayers[i].getPlayer().maxHitPoint);
			buffer.writeInt(targetPlayers[i].getPlayer().magicPoint);
			buffer.writeInt(es.getTotalAtt("maxMagicPoint")+targetPlayers[i].getPlayer().maxMagicPoint);
			buffer.writeByte(targetPlayers[i].getPlayer().upProfession);
			buffer.writeByte(targetPlayers[i].getPlayer().sex);
			targetPlayers[i].writeBattlePetInfo(buffer);
		}
	}
	

	
	/**
	 * 通知战斗结束
	 */
	public void uninit()
	{
		boolean familyEnd = FamilyPartyController.getInstance().isEnded();
//		System.out.println("PartyPKController uninit():战斗是否结束:家族:"+familyEnd+"  阵营："+campEnd);
		
		int loseTeam = 0;
		if(isPlayerDead())
			loseTeam = 1;
		else if(isTargetPlayerDead())
			loseTeam = 2;
			
		if(targetPlayers == null || players == null)
			return;
		
//			if(targetPlayers.length < 1)
//				return;
			for (int i = 0; i < targetPlayers.length; i++) 
			{
				if(isTargetNull(targetPlayers[i]))
					continue;
				PlayerBattleTmp pbt = (PlayerBattleTmp) targetPlayers[i].getAttachment();
				if(pbt != null)
				{
					if(pbt.getEffectList() != null)
						removeEffect(pbt.getEffectList(),pbt);
//					removeHaloEffect(pbt.getBuffBox().getHaloList(), pbt);
					if(loseTeam == pbt.getTeamNo())
					{
						if(pbt.getBuffBox() != null)
						{
							if(pbt.getBuffBox().getEffectList() != null)
								removeHaloEffect(pbt.getBuffBox().getEffectList(), targetPlayers[i]);
						}
					}
					else
					{
						if(pbt.getBuffBox() != null)
						{
							if(pbt.getBuffBox().getHaloList() != null)
								removeHaloEffect(pbt.getBuffBox().getHaloList(), targetPlayers[i]);
						}
					}
				}
				

//				targetPlayers[i].setExtLifeAndMagic();
//				targetPlayers[i].sendAlwaysValue();
				ByteBuffer endBattleBuff = new ByteBuffer(1);
				endBattleBuff.writeBoolean(true);
				targetPlayers[i].getNetConnection().sendMessage(new SMsg(SMsg.S_BATTLE_PLAYER_BATTLEEND, endBattleBuff));
			}
		
//			if(players.length < 1)
//				return;
			for (int i = 0; i < players.length; i++) 
			{
				if(isTargetNull(players[i]))
					continue;
				PlayerBattleTmp pbt = (PlayerBattleTmp) players[i].getAttachment();
				if(pbt != null)
				{
					if(pbt.getEffectList() != null)
						removeEffect(pbt.getEffectList(),pbt);
					if(loseTeam == pbt.getTeamNo())
					{
						if(pbt.getBuffBox() != null)
						{
							if(pbt.getBuffBox().getEffectList() != null)
								removeHaloEffect(pbt.getBuffBox().getEffectList(), players[i]);
						}
					}
					else
					{
						if(pbt.getBuffBox() != null)
						{
							if(pbt.getBuffBox().getHaloList() != null)
								removeHaloEffect(pbt.getBuffBox().getHaloList(), players[i]);
						}
					}		
				}	

				ByteBuffer endBattleBuff = new ByteBuffer(1);
				endBattleBuff.writeBoolean(true);
				players[i].getNetConnection().sendMessage(new SMsg(SMsg.S_BATTLE_PLAYER_BATTLEEND, endBattleBuff));
			}
		
		
		sendBattleResultText();
		
		if(!familyEnd)
		{
			if(isPlayerDead())
			{
				setWinOrLoseInfo(players,false,targetPlayers);
				setWinOrLoseInfo(targetPlayers,true,players);
			}
			else if(isTargetPlayerDead())
			{
				setWinOrLoseInfo(players,true,targetPlayers);
				setWinOrLoseInfo(targetPlayers,false,players);
			}
		}
		
		fixPlayerBaseInfo(players,false);
		
		fixPlayerBaseInfo(targetPlayers,false);
		

		if(familyEnd)
		{
			PlayerController p1 = getAnyOnePlayer(targetPlayers);
			if(p1 == null)
			{
				isFinish = true;
				return;
			}
			p1.moveToRoom(DataFactory.INITROOM);
			
			PlayerController p2 = getAnyOnePlayer(players);
			if(p2 == null)
			{
				isFinish = true;
				return;
			}
			p2.moveToRoom(DataFactory.INITROOM);
		}
		else
		{
			if(isTargetPlayerDead())
			{
				for (int i = 0; i < players.length; i++) 
				{
					if(isTargetNull(players[i]))
						continue;
					players[i].setParent(room);
				}
				PlayerController p1 = getAnyOnePlayer(targetPlayers);
				if(p1 == null)
				{
					isFinish = true;
					return;
				}
				int roomId = 0,camp = 0;
				if(!familyEnd)
				{
					camp = p1.getFamily().camp;
				}
				roomId = room.getRebirthId(camp);
				if(roomId == 0)
				{
					roomId = PartyController.getRoomByCamp(camp);
				}
				p1.moveToRoom(roomId);
			}
			else if(isPlayerDead())
			{
				for (int i = 0; i < targetPlayers.length; i++) 
				{
					if(isTargetNull(targetPlayers[i]))
						continue;
					targetPlayers[i].setParent(room);
				}
				PlayerController p2 = getAnyOnePlayer(players);
				if(p2 == null)
				{
					isFinish = true;
					return;
				}
				int roomId = 0,camp = 0;
				if(!familyEnd)
				{
					camp = p2.getFamily().camp;
				}
				roomId = room.getRebirthId(camp);
				if(roomId == 0)
				{
					roomId = PartyController.getRoomByCamp(camp);
				}
				p2.moveToRoom(roomId);
			}
		}
		
		
		isFinish = true;
	}
	


	public PlayerController[] getPlayers()
	{
		return players;
	}

	public PlayerController[] getTargetPlayers()
	{
		return targetPlayers;
	}
	
	public List getTargetPlayerList()
	{
		return targetPlayerList;
	}
	
	/**
	 * 检测状态
	 */
	public void checkDeadState()
	{
		if(!isPlayerDead() && !isTargetPlayerDead())
			return;

		int loseTeam = 0;
		if(isTargetPlayerDead())
		{
			loseTeam = targetPlayers[0].getAttachment().getTeamNo();
		}
		else if(isPlayerDead())
		{
			loseTeam =  players[0].getAttachment().getTeamNo();
		}
		
		dispathWinOrLose(playerList,loseTeam);
		dispathWinOrLose(targetPlayerList,loseTeam);
		
		
		endTime = WorldManager.currentTime;//System.currentTimeMillis();
	}
	

	public void dispathWinOrLose(List players,int loseTeam)
	{
		for(int i = 0 ; i < players.size() ; i ++)
		{
			PlayerController everyone = (PlayerController)players.get(i);
			if(isTargetNull(everyone))
				continue;
			ByteBuffer buffer = new ByteBuffer(1);
			
			if(everyone.getAttachment().getTeamNo() == loseTeam)
			{
				buffer.writeBoolean(false);
			}
			else
			{
				buffer.writeBoolean(true);
			}
			buffer.writeByte(0);
			/************以下数据暂时用不到，PVE时用来发送获得的经验，金钱，物品名，是否有BOSS宝箱，客户端好播放动画**************************/
			buffer.writeBoolean(false);//是否有BOSS宝箱
			buffer.writeUTF("");//物品名
			buffer.writeUTF("");
			buffer.writeInt(0);
			/**************************************/
			SMsg appmsg = new SMsg(SMsg.S_BATTLE_PLAYER_BATTLERESULT,buffer);
			everyone.getNetConnection().sendMessage(appmsg);
		}
	}
	
	public boolean isPlayerDead()
	{
		for (int i = 0; i < players.length; i++)
		{
			if(isTargetNull(players[i]))
				continue;
			if(!players[i].getAttachment().isDead())
				return false;
		}
		return true;
	}
	public boolean isTargetPlayerDead()
	{
		for (int i = 0; i < targetPlayers.length; i++)
		{
			if(isTargetNull(targetPlayers[i]))
				continue;
			if(!targetPlayers[i].getAttachment().isDead())
				return false;
		}
		return true;
	}
	
	public void dispatchIsBattleWithoutRoom(boolean isBattle,PlayerController target)
	{
		if(target == null)
		{
			PlayerController player = null;
			int length = players.length;
			for (int i = 0; i < length; i++)
			{
				if(players[i] != null)
				{
					player = players[i];
					break;
				}
			}
			
			if(player!= null)
				super.dispatchIsBattleWithoutRoom(isBattle,player);
			
			player = null;
			length = targetPlayers.length;
			for (int i = 0; i < length; i++)
			{
				if(targetPlayers[i] != null)
				{
					player = targetPlayers[i];
					break;
				}
			}
			
			if(player== null)
				return;
			
			super.dispatchIsBattleWithoutRoom(isBattle,player);
		}
		else
		{
			super.dispatchIsBattleWithoutRoom(isBattle, target);
		}
	}
	
	/**
	 * 转发容器内所有的消息
	 * @param appMsg
	 */
	public void dispatchMsg(int sMsg,ByteBuffer buff)
	{
		for(int i = 0 ; i < targetPlayerList.size() ; i ++)
		{
			PlayerController target = (PlayerController)targetPlayerList.get(i);
			if(isTargetNull(target))
				continue;
			ByteBuffer buffer;
			if(sMsg == SMsg.S_ROOM_PVP_COMMAND)
			{
				buffer = new ByteBuffer(64);
				write(buffer);
			}
			else
			{
				buffer = new ByteBuffer(buff.getBytes());
			}
			target.getNetConnection().sendMessage(new AppMessage(sMsg,buffer));
		}
		super.dispatchMsg(sMsg, buff);
	}
	
	public void write(ByteBuffer buffer)
	{
		int i = 0;
		buffer.writeByte(BattleController.TEAM2);
		buffer.writeByte(targetPlayerLength);//玩家数量
		for (i = 0; i < targetPlayerLength; i++)
		{
			if(isTargetNull(targetPlayers[i]))
				continue;
			buffer.writeByte(i);
			buffer.writeInt(targetPlayers[i].getID());
			targetPlayers[i].writeBattlePetInfo(buffer);
			targetPlayers[i].sendAlwaysValue();
		}
		buffer.writeByte(BattleController.TEAM1);
		buffer.writeByte(playerLength);
		for (i = 0; i < playerLength; i++) 
		{
			if(isTargetNull(players[i]))
				continue;
			buffer.writeByte(i);
			EquipSet es = (EquipSet) players[i].getPlayer().getExtPlayerInfo("equipSet");
			buffer.writeInt(players[i].getID());
			buffer.writeUTF(players[i].getName());
			buffer.writeInt(players[i].getPlayer().iconId);
			buffer.writeInt(players[i].getPlayer().modelId);
			buffer.writeInt(players[i].getPlayer().modelMotionId);
			buffer.writeInt(players[i].getPlayer().level);
			buffer.writeInt(players[i].getPlayer().hitPoint);
			buffer.writeInt(es.getTotalAtt("maxHitPoint")+players[i].getPlayer().maxHitPoint);
			buffer.writeInt(players[i].getPlayer().magicPoint);
			buffer.writeInt(es.getTotalAtt("maxMagicPoint")+players[i].getPlayer().maxMagicPoint);
			buffer.writeByte(players[i].getPlayer().upProfession);
			buffer.writeByte(players[i].getPlayer().sex);
			players[i].writeBattlePetInfo(buffer);
		}
	}
	
	
	
	
	
	/**
	 * 掉线 
	 */
	public void removePlayer(PlayerController target)
	{
		if(target.getTeam() == null)
		{
			if(target.getAttachment().getTeamNo() == 1)
			{
				dispathWinOrLose(playerList,1);
				dispathWinOrLose(targetPlayerList,1);
				for (int i = 0; i < targetPlayers.length; i++) 
				{
					if(isTargetNull(targetPlayers[i]))
						continue;
					removeBP(targetPlayers[i]);
				}
			}
			else if(target.getAttachment().getTeamNo() == 2)
			{
				dispathWinOrLose(playerList,2);
				dispathWinOrLose(targetPlayerList,2);
				for (int i = 0; i < players.length; i++) 
				{
					if(isTargetNull(players[i]))
						continue;
					removeBP(players[i]);
				}
			}

			removeBP(target);
			
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
	 * 逃跑
	 */
	public void escBattle(PlayerController target)
	{
		if(target.getTeam() == null || target.getTeam().isLeader(target))
		{
			PlayerController pc = null;
			if(target.getAttachment().getTeamNo() == 1)
			{
				dispathWinOrLose(getPlayerList(),1);
				dispathWinOrLose(getTargetPlayerList(),1);
			    pc = getAnyOnePlayer(players);
			    
			    setWinOrLoseInfo(players, false,targetPlayers);
			    setWinOrLoseInfo(targetPlayers, true,players);
			}
			else if(target.getAttachment().getTeamNo() == 2)
			{
				dispathWinOrLose(getPlayerList(),2);
				dispathWinOrLose(getTargetPlayerList(),2);
				pc = getAnyOnePlayer(targetPlayers);
				
				setWinOrLoseInfo(targetPlayers, false,players);
			    setWinOrLoseInfo(players, true,targetPlayers);
			}
			for (int i = 0; i < players.length; i++) 
			{
				if(isTargetNull(players[i]))
					continue;
				removeBP(players[i]);
			}
			for (int i = 0; i < targetPlayers.length; i++) 
			{
				if(isTargetNull(targetPlayers[i]))
					continue;
				removeBP(targetPlayers[i]);
			}
			if(FamilyPartyController.getInstance().isEnded())
			{
				for (int i = 0; i < players.length; i++) 
				{
					if(isTargetNull(players[i]))
						continue;
					players[i].moveToRoom(DataFactory.INITROOM);
					break;
				}
				for (int i = 0; i < targetPlayers.length; i++) 
				{
					if(isTargetNull(targetPlayers[i]))
						continue;
					targetPlayers[i].moveToRoom(DataFactory.INITROOM);
					break;
				}
			}
			else
			{
				if(pc != null)
				{
					int roomId = 0,camp = 0;
					if(FamilyPartyController.getInstance().isStarted())
					{
						camp = pc.getFamily().camp;
					}
					roomId = room.getRebirthId(camp);
					if(roomId == 0)
					{
						roomId = PartyController.getRoomByCamp(camp);
					}
					pc.moveToRoom(roomId);
				}
			}
			isFinish = true;
		}
	}
	
	
	public static final int PARTYEXP = 8000000;
	/**
	 * 获取经验
	 * @param target
	 * @param result true为胜利 false为失败
	 * 胜利的一方：（800万经验+当前等级升级所需经验除以480）*2
	 * 失败的一方：（800万经验+当前等级升级所需经验除以480）*1
	 * @return
	 */
	private long getExpWinOrLose(PlayerController target,boolean isWin)
	{
		long result = 0;
		if(FamilyPartyController.getInstance().isStarted())
		{
			Exp currentExp = target.getExpByLevel(target.getPlayer().level+1,1);
			long value = 0;
			if(currentExp != null)
				value = currentExp.levelExp;
			if(isWin)
			{
				result = (PARTYEXP + value/480) * 2;
			}
			else
			{
				result = PARTYEXP + value/480;
			}
		}
		return result;
	}
	
	/**
	 * 设置胜利或者失败信息
	 * @param targets
	 * @param result true为胜利 flase 为失败
	 * others 对方玩家
	  * 游戏币奖励:失败是胜利奖励的50%。
		胜利奖励20000+自身等级*5
		单位是铜币
	 */
	public void setWinOrLoseInfo(PlayerController[] targets,boolean result,PlayerController[] others)
	{
		int count = 0,allLevel = 0;
		for (int i = 0; i < others.length; i++) 
		{
			if(isTargetNull(others[i]))
				continue;
			count ++;
		}
		for (int i = 0; i < others.length; i++) 
		{
			if(isTargetNull(others[i]))
				continue;
			allLevel += others[i].getPlayer().level;
		}
		
		int aveLevel = allLevel / count;
		
		int teamAllLevel = 0;
		for (int i = 0; i < targets.length; i++) 
		{
			if(isTargetNull(targets[i]))
				continue;
			teamAllLevel += targets[i].getPlayer().level;
		}
		int point = 0;
		FamilyPartyController fpc = FamilyPartyController.getInstance();
		if(fpc.isEnded())
			return;
		if(result)
		{
			if(fpc.isStarted())
				point = FamilyPartyController.WINHONOUR;
		}
		else
		{
			if(fpc.isStarted())
				point = FamilyPartyController.LOSEHONOUR;
		}
		
		for (int i = 0; i < targets.length; i++) 
		{
			if(isTargetNull(targets[i]))
				continue;

			int honor = FamilyPartyController.WINHONOUR + targets[i].getPlayer().level / 100 + (int) (Math.random() * 3 + 1);
			honor = result?honor:honor/2;
			targets[i].setHonour(honor);
			targets[i].sendGetGoodsInfo(2, false, targets[i].getName()+DC.getString(DC.BATTLE_25)+": "+honor);//获得荣誉值
			
			long resultExp = targets[i].teamExp(getExpWinOrLose(targets[i],result), teamAllLevel, aveLevel,2);
			long disExp = targets[i].addExp(resultExp,true,true,false);
			if(disExp > 0)
				targets[i].sendGetGoodsInfo(1, false, DC.getString(DC.BATTLE_26)+": "+disExp);
			
			if(fpc.isStarted())
			{
				fpc.addPoint(targets[i], honor);
			}
			int goodsId = 0;
			if(!result)
			{
				goodsId = LOSESTATEGOODS;
			}
			else
			{
				goodsId = WINSTATEGOODS;
			}
			Bag bag = (Bag) targets[i].getPlayer().getExtPlayerInfo("bag");
			GoodsProp goods = (GoodsProp) DataFactory.getInstance().getGameObject(goodsId);
			if(goods != null)
			{
				GoodsProp newGoods = (GoodsProp) Goods.cloneObject(goods);
				newGoods.objectIndex = WorldManager.currentTime!=0?WorldManager.currentTime:System.currentTimeMillis();//这里的流水用来计开始时间
				bag.setExtGoods(3, newGoods);
				bag.sendExpBuff(targets[i], newGoods.effect, true, (int)newGoods.expTimes);
			}
			
			long rPoint = 20000 + targets[i].getPlayer().level * 5;
			long rp = result?rPoint:rPoint/2;
			bag.point += rp;
			targets[i].sendGetGoodsInfo(1, false, DC.getString(DC.BATTLE_24)+": "+rp);
			bag.sendAddGoods(targets[i], null);
			
			honor = 0;
		}
	}
	

	
	
	public void sendBattleResultText()
	{
		String winFamily = "",loseFamily = "",winTeam = "",loseTeam = "";
		PlayerController[] winPlayers = null,losePlayers = null;
		StringBuffer sb = null;
		if(isPlayerDead())
		{
			winFamily = getTypeName(1, targetPlayers);
			loseFamily = getTypeName(1, players);
			winTeam = getTypeName(2, targetPlayers);
			loseTeam = getTypeName(2, players);
			winPlayers = targetPlayers;
			losePlayers = players;
		}
		else if(isTargetPlayerDead())
		{
			winFamily = getTypeName(1, players);
			loseFamily = getTypeName(1, targetPlayers);
			winTeam = getTypeName(2, players);
			loseTeam = getTypeName(2, targetPlayers);
			winPlayers = players;
			losePlayers = targetPlayers;
		}

		for (int i = 0; i < losePlayers.length; i++)
		{
			if(isTargetNull(losePlayers[i]))
				continue;
			losePlayers[i].sendGetGoodsInfo(1, false, DC.getPartyPKString(false, winFamily, winTeam));
		}
		
		for (int i = 0; i < winPlayers.length; i++)
		{
			if(isTargetNull(winPlayers[i]))
				continue;
			winPlayers[i].sendGetGoodsInfo(1, false, DC.getPartyPKString(true, loseFamily, loseTeam));
		}
	}
	
	
	public String getTypeName(int type,PlayerController[] players)
	{
		for (int i = 0; i < players.length; i++) 
		{//输家
			if(isTargetNull(players[i]))
				continue;
			if(type == 1)
			{
				if(players[i].getFamily() == null)
					continue;
				return players[i].getFamily().name;
			}
			else if(type == 2)
			{
				if(players[i].getTeam() == null)
				{
					return players[i].getName();
				}
				else
				{
					return players[i].getTeam().getLeader().getName();
				}
			}
		}
		return "";
	}
}
