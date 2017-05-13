package cc.lv1.rpg.gs.entity.controller;

import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.data.DC;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.ext.Bag;
import cc.lv1.rpg.gs.entity.ext.EquipSet;
import cc.lv1.rpg.gs.entity.ext.PVPInfo;
import cc.lv1.rpg.gs.entity.ext.ReviseBaseInfo;
import cc.lv1.rpg.gs.entity.ext.SkillTome;
import cc.lv1.rpg.gs.entity.impl.Monster;
import cc.lv1.rpg.gs.entity.impl.battle.PlayerBattleTmp;
import cc.lv1.rpg.gs.entity.impl.battle.SpriteBattleTmp;
import cc.lv1.rpg.gs.entity.impl.battle.skill.ActiveSkill;
import cc.lv1.rpg.gs.gui.MainFrame;
import cc.lv1.rpg.gs.net.SMsg;
import cc.lv1.rpg.gs.other.ErrorCode;
import cc.lv1.rpg.gs.util.FontConver;
import vin.rabbit.net.AppMessage;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.impl.ArrayList;

/**
 * 玩家 vs 玩家
 * @author dxw
 *
 */
public class PVPController extends BattleController
{

	protected List targetPlayerList = new ArrayList(4);
	
	protected PlayerController[] players;
	
	protected PlayerController[] targetPlayers;
	
	protected int playerLength;
	
	protected int targetPlayerLength;
	
	protected int winTeam;
	
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
				removePlayer(target);
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
		else if(type == SMsg.C_BATTLE_PLAYER_RESET_COMMAND)
		{
			resetChoose(target, msg.getBuffer());
		}
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
			target.sendGetGoodsInfo(1, false, DC.getString(DC.BATTLE_16));
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
			System.out.println("PVPController 240 other target Object");
			return;
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
		if(winTeam == 0)
		{
			String pName = "",tName = "";
			for (int i = 0; i < players.length; i++) 
			{
				if(isTargetNull(players[i]))
					continue;
				if(players[i].getTeam() == null)
					pName = players[i].getName();
				else
					pName = players[i].getTeam().getLeader().getName();
			}
			for (int i = 0; i < targetPlayers.length; i++) 
			{
				if(isTargetNull(targetPlayers[i]))
					continue;
				if(targetPlayers[i].getTeam() == null)
					tName = targetPlayers[i].getName();
				else
					tName = targetPlayers[i].getTeam().getLeader().getName();
			}
			StringBuffer sb;
			if(isPlayerDead())
			{
				winTeam = 2;
				for (int i = 0; i < players.length; i++) 
				{
					if(isTargetNull(players[i]))
						continue;
					players[i].sendGetGoodsInfo(1, false, DC.getPKString(false, tName));
					sb = null;
				}
				for (int i = 0; i < targetPlayers.length; i++) 
				{
					if(isTargetNull(targetPlayers[i]))
						continue;
					targetPlayers[i].sendGetGoodsInfo(1, false, DC.getPKString(true, pName));
					sb = null;
				}
			}
			else if(isTargetPlayerDead())
			{
				winTeam = 1;
				for (int i = 0; i < targetPlayers.length; i++) 
				{
					if(isTargetNull(targetPlayers[i]))
						continue;
					targetPlayers[i].sendGetGoodsInfo(1, false, DC.getPKString(false, pName));
					sb = null;
				}
				for (int i = 0; i < players.length; i++) 
				{
					if(isTargetNull(players[i]))
						continue;
					players[i].sendGetGoodsInfo(1, false, DC.getPKString(true, tName));
					sb = null;
				}
			}
			else 
			{	
				isFinish = true;
				return;
			}
		}
		
//		if(isPlayerDead())
		if(winTeam == 2)
		{
			if(targetPlayers.length < 1)
				return;
			for (int i = 0; i < targetPlayers.length; i++) 
			{
				if(isTargetNull(targetPlayers[i]))
					continue;
				PlayerBattleTmp pbt = (PlayerBattleTmp) targetPlayers[i].getAttachment();
				if(pbt != null)
				{
					if(pbt.getEffectList() != null)
						removeEffect(pbt.getEffectList(),pbt);
					if(pbt.getBuffBox() != null && pbt.getBuffBox().getHaloList() != null)
						removeHaloEffect(pbt.getBuffBox().getHaloList(), targetPlayers[i]);
				}
				targetPlayers[i].setParent(room);

				ByteBuffer endBattleBuff = new ByteBuffer(1);
				endBattleBuff.writeBoolean(true);
				targetPlayers[i].getNetConnection().sendMessage(new SMsg(SMsg.S_BATTLE_PLAYER_BATTLEEND, endBattleBuff));
			}
			
			fixPlayerBaseInfo(targetPlayers,false);
			
			targetPlayers = new PlayerController[0];
			targetPlayerList = new ArrayList();
			targetPlayerLength = 0;
			
			PlayerController p = getAnyOnePlayer(players);
			if(p == null)
			{
				isFinish = true;
				return;
			}
	
			int roomId = room.getRebirthId(p.getPlayer().camp);
			if(roomId == 0)
			{
//				System.out.println("PVPController uninit 430 rebirth is zero:"+room.name);
				int playerCount = getPlayerCount();

				if(playerCount <= 0)
					return;
				
				for(int i = 0 ; i < playerCount ; i ++)
				{
					PlayerController everyone = (PlayerController)playerList.get(i);
					if(isTargetNull(everyone))
						continue;
					PlayerBattleTmp pbt = (PlayerBattleTmp) everyone.getAttachment();
					if(pbt != null)
					{
						if(pbt.getEffectList() != null)
							removeEffect(pbt.getEffectList(),pbt);
						if(pbt.getBuffBox() != null && pbt.getBuffBox().getEffectList() != null)
							removeHaloEffect(pbt.getBuffBox().getEffectList(),everyone);
					}
					everyone.setParent(room);

					ByteBuffer endBattleBuff = new ByteBuffer(1);
					endBattleBuff.writeBoolean(true);
					everyone.getNetConnection().sendMessage(new SMsg(SMsg.S_BATTLE_PLAYER_BATTLEEND, endBattleBuff));
				}
				isFinish = true;
				
				fixPlayerBaseInfo(players,false);
			}
			else
			{
				Object obj = DataFactory.getInstance().getGameObject(roomId);
				if(obj == null || !(obj instanceof RoomController))
					return;
				RoomController rc = (RoomController) obj;
//				if(room.id == rc.id)
//					return;
				for (int i = 0; i < players.length; i++)
				{
					rc.setParentPlayer(players[i]);
					players[i].isReset = true;
				}
				
				for (int i = 0; i < players.length; i++) 
				{
					if(isTargetNull(players[i]))
						continue;
					ByteBuffer buffer = new ByteBuffer(1);
					if(players[i].getTeam() == null)
						buffer.writeBoolean(true);
					else 
					{
						if(players[i].getTeam().isLeader(players[i]))
							buffer.writeBoolean(true);
						else
							buffer.writeBoolean(false);
					}	
					players[i].getNetConnection().sendMessage(new SMsg(SMsg.S_BATTLE_PLAYER_RESET_COMMAND, buffer));
				}
			}

		}
//		else if(isTargetPlayerDead())
		else if(winTeam == 1)
		{
			if(players.length < 1)
				return;
			for (int i = 0; i < players.length; i++) 
			{
				if(isTargetNull(players[i]))
					continue;
				PlayerBattleTmp pbt = (PlayerBattleTmp) players[i].getAttachment();
				if(pbt != null)
				{
					if(pbt.getEffectList() != null)
						removeEffect(pbt.getEffectList(),pbt);
					if(pbt.getBuffBox() != null && pbt.getBuffBox().getHaloList() != null)
						removeHaloEffect(pbt.getBuffBox().getHaloList(), players[i]);
				}
				players[i].setParent(room);
	
				ByteBuffer endBattleBuff = new ByteBuffer(1);
				endBattleBuff.writeBoolean(true);
				players[i].getNetConnection().sendMessage(new SMsg(SMsg.S_BATTLE_PLAYER_BATTLEEND, endBattleBuff));
			}
			
			fixPlayerBaseInfo(players,false);
			
			players = new PlayerController[0];
			playerList = new ArrayList();
			playerLength = 0;
			
			PlayerController p = getAnyOnePlayer(targetPlayers);
			if(p == null)
			{
				isFinish = true;
				return;
			}
		
			int roomId = room.getRebirthId(p.getPlayer().camp);
			if(roomId == 0)//
			{
//				System.out.println("PVPController 507 uninit rebirth is zero:"+room.name);

				if(targetPlayers.length <= 0)
					return;
				
				for(int i = 0 ; i < targetPlayers.length ; i ++)
				{
					PlayerController everyone = targetPlayers[i];
					if(isTargetNull(everyone))
						continue;
					PlayerBattleTmp pbt = (PlayerBattleTmp) everyone.getAttachment();
					if(pbt != null)
					{
						if(pbt.getEffectList() != null)
							removeEffect(pbt.getEffectList(),pbt);
						if(pbt.getBuffBox() != null && pbt.getBuffBox().getEffectList() != null)
							removeHaloEffect(pbt.getBuffBox().getEffectList(),everyone);
					}
					everyone.setParent(room);
		
					ByteBuffer endBattleBuff = new ByteBuffer(1);
					endBattleBuff.writeBoolean(true);
					everyone.getNetConnection().sendMessage(new SMsg(SMsg.S_BATTLE_PLAYER_BATTLEEND, endBattleBuff));
				}
				isFinish = true;
				
				fixPlayerBaseInfo(targetPlayers,false);
			}
			else
			{
				Object obj = DataFactory.getInstance().getGameObject(roomId);
				if(obj == null || !(obj instanceof RoomController))
					return;
				RoomController rc = (RoomController) obj;
//				if(room.id == rc.id)
//					return;
				for (int i = 0; i < targetPlayers.length; i++)
				{
					rc.setParentPlayer(targetPlayers[i]);
					targetPlayers[i].isReset = true;
				}
			
				for (int i = 0; i < targetPlayers.length; i++) 
				{
					if(isTargetNull(targetPlayers[i]))
						continue;
					ByteBuffer buffer = new ByteBuffer(1);
					if(targetPlayers[i].getTeam() == null)
						buffer.writeBoolean(true);
					else 
					{
						if(targetPlayers[i].getTeam().isLeader(targetPlayers[i]))
							buffer.writeBoolean(true);
						else
							buffer.writeBoolean(false);
					}	
					targetPlayers[i].getNetConnection().sendMessage(new SMsg(SMsg.S_BATTLE_PLAYER_RESET_COMMAND, buffer));
				}
			}
		}
		else
		{
			isFinish = true;
		}
		
	}
	
	/**
	 * 复活选择
	 * @param buffer
	 */
	public void resetChoose(PlayerController target,ByteBuffer buffer)
	{
		if(!(target.getParent() instanceof BattleController))
			return;
		if(!isPlayerDead() && !isTargetPlayerDead())
			return;
		
		int type = buffer.readByte();
		
		if(type == 1)
		{
			Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
			if(bag.money < RESETMONEY)
			{
				target.sendAlert(ErrorCode.ALERT_SHOP_NO_MONEY);
				return;
			}
			
			bag.money -= RESETMONEY;
			
			bag.sendAddGoods(target, null);
			
			
			if(target.getAttachment().getTeamNo() == BattleController.TEAM1)
			{
				if(players.length < 1)
					return;
				for (int i = 0; i < players.length; i++)
				{
					if(isTargetNull(players[i]))
						continue;
					PlayerBattleTmp pbt = (PlayerBattleTmp) players[i].getAttachment();
					removeEffect(pbt.getEffectList(),pbt);
//					removeHaloEffect(pbt.getBuffBox().getHaloList(), pbt);
					removeHaloEffect(pbt.getBuffBox().getEffectList(),players[i]);
					room.setParentPlayer(players[i]);
					players[i].setParent(room);
					
					ByteBuffer endBattleBuff = new ByteBuffer(1);
					endBattleBuff.writeBoolean(true);
					players[i].getNetConnection().sendMessage(new SMsg(SMsg.S_BATTLE_PLAYER_BATTLEEND, endBattleBuff));
				}
				
				fixPlayerBaseInfo(players,true);
			}
			else if(target.getAttachment().getTeamNo() == BattleController.TEAM2)
			{
				if(targetPlayers.length < 1)
					return;
				for (int i = 0; i < targetPlayers.length; i++)
				{
					if(isTargetNull(targetPlayers[i]))
						continue;
					PlayerBattleTmp pbt = (PlayerBattleTmp) targetPlayers[i].getAttachment();
					removeEffect(pbt.getEffectList(),pbt);
//					removeHaloEffect(pbt.getBuffBox().getHaloList(), pbt);
					removeHaloEffect(pbt.getBuffBox().getEffectList(),targetPlayers[i]);
					room.setParentPlayer(targetPlayers[i]);
					targetPlayers[i].setParent(room);
					
					ByteBuffer endBattleBuff = new ByteBuffer(1);
					endBattleBuff.writeBoolean(true);
					targetPlayers[i].getNetConnection().sendMessage(new SMsg(SMsg.S_BATTLE_PLAYER_BATTLEEND, endBattleBuff));
				}
				
				fixPlayerBaseInfo(targetPlayers,true);
			}
			
			sendEquipMoney(target);
			
			sendResetInfo(target);
			
		}
		else if(type == 2)
		{
			PlayerController[] ps = null;
			if(target.getAttachment().getTeamNo() == BattleController.TEAM1)
			{
				ps = players;
			}
			else if(target.getAttachment().getTeamNo() == BattleController.TEAM2)
			{
				ps = targetPlayers;
			}
			if(ps == null)
				return;
			for (int i = 0; i < ps.length; i++)
			{
				if(isTargetNull(ps[i]))
					continue;
				PlayerBattleTmp pbt = (PlayerBattleTmp) ps[i].getAttachment();
				removeEffect(pbt.getEffectList(),pbt);
//				removeHaloEffect(pbt.getBuffBox().getHaloList(), pbt);
				removeHaloEffect(pbt.getBuffBox().getEffectList(),ps[i]);
				ps[i].setParent(room);
				
				ByteBuffer endBattleBuff = new ByteBuffer(1);
				endBattleBuff.writeBoolean(true);
				ps[i].getNetConnection().sendMessage(new SMsg(SMsg.S_BATTLE_PLAYER_BATTLEEND, endBattleBuff));
			}
			
			fixPlayerBaseInfo(ps,false);
			
			PlayerController p = getAnyOnePlayer(ps);
			if(p == null)
			{
				isFinish = true;
				return;
			}
			int roomId = room.getRebirthId(p.getPlayer().camp);
			if(roomId == 0)
				return;
			Object obj = DataFactory.getInstance().getGameObject(roomId);
			if(obj == null || !(obj instanceof RoomController))
				return;
			RoomController rc = (RoomController) obj;
			if(rc.id == room.id)
			{
				return;
			}

			room.sendPlayerMove(rc, ps);
		}
		else
		{
			target.close();
			System.out.println("PVEController resetChoose() error:type=="+type);
			return;
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
			buffer.writeUTF("");//经验
			buffer.writeInt(0);//金钱
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
//			System.out.println(players[i].getName()+"  "+players[i].getPlayer().sex);
		}
	}



}
