package cc.lv1.rpg.gs.entity.controller;

import vin.rabbit.net.AppMessage;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.Iterator;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.i.Map;
import vin.rabbit.util.collection.impl.ArrayList;
import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.data.DC;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.data.PressureTest;
import cc.lv1.rpg.gs.entity.ext.Bag;
import cc.lv1.rpg.gs.entity.ext.BuffBox;
import cc.lv1.rpg.gs.entity.ext.EquipSet;
import cc.lv1.rpg.gs.entity.ext.OtherExtInfo;
import cc.lv1.rpg.gs.entity.ext.PVPInfo;
import cc.lv1.rpg.gs.entity.ext.PetTome;
import cc.lv1.rpg.gs.entity.ext.ReviseBaseInfo;
import cc.lv1.rpg.gs.entity.ext.SkillTome;
import cc.lv1.rpg.gs.entity.ext.TaskInfo;
import cc.lv1.rpg.gs.entity.impl.BaseFlayer;
import cc.lv1.rpg.gs.entity.impl.BoxDropProp;
import cc.lv1.rpg.gs.entity.impl.GoldParty;
import cc.lv1.rpg.gs.entity.impl.Goods;
import cc.lv1.rpg.gs.entity.impl.Mail;
import cc.lv1.rpg.gs.entity.impl.MoneyBattle;
import cc.lv1.rpg.gs.entity.impl.Monster;
import cc.lv1.rpg.gs.entity.impl.MonsterDropProp;
import cc.lv1.rpg.gs.entity.impl.battle.MonsterBattleTmp;
import cc.lv1.rpg.gs.entity.impl.battle.PlayerBattleTmp;
import cc.lv1.rpg.gs.entity.impl.battle.SpriteBattleTmp;
import cc.lv1.rpg.gs.entity.impl.battle.effect.TimeEffect;
import cc.lv1.rpg.gs.entity.impl.battle.skill.ActiveSkill;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsNotice;
import cc.lv1.rpg.gs.entity.impl.pet.Pet;
import cc.lv1.rpg.gs.gui.MainFrame;
import cc.lv1.rpg.gs.net.SMsg;
import cc.lv1.rpg.gs.net.impl.NoticeJob;
import cc.lv1.rpg.gs.net.impl.PayJob;
import cc.lv1.rpg.gs.net.impl.PressureJob;
import cc.lv1.rpg.gs.net.impl.SaveJob;
import cc.lv1.rpg.gs.other.ErrorCode;
import cc.lv1.rpg.gs.util.Code;
import cc.lv1.rpg.gs.util.FontConver;

/**
 * 玩家 vs 敌人
 * @author dxw
 *
 */
public class PVEController extends BattleController
{

	public PlayerController [] players = null;

	public MonsterController [] monsters = null;
	
	public int playerLength;
	
	public int monsterLength;
	
	private boolean isBoss = false;
	
	private boolean isNoticed = false;

	public boolean isBoss()
	{
		return isBoss;
	}

	public void setBoss(boolean isBoss)
	{
		this.isBoss = isBoss;
	}
	

	public void update(long timeMillis)
	{
		if(timeMillis - endTime > 8800 && isMonsterDead())
		{
			uninit();
		}
		if(timeMillis - endTime > 10000 && isPlayerDead())
		{
			ByteBuffer buffer = new ByteBuffer();
			buffer.writeByte(2);//10秒还没选择就自动回城
			PlayerController target = getTeamLeader();
			if(target != null && isBoss)
				resetChoose(target, buffer);
		}
		super.update(timeMillis);
		
		int monsterSize = monsters.length;
		for(int i = 0 ; i < monsterSize ; i ++)
		{
			if(monsters[i] == null)
				continue;
			monsters[i].update(timeMillis);
		}
	}
	
	public PlayerController getTeamLeader()
	{
		if(players.length == 1)
			return players[0];
		for (int i = 0; i < players.length; i++)
		{
			if(isTargetNull(players[i]))
				continue;
			else
			{
				if(players[i].getTeam() != null)
				{
					if(players[i].getTeam().isLeader(players[i]))
						return players[i];
				}
				else
					return players[i];
			}
		}
		return null;
	}
	
	public PlayerController getAnyonePlayer()
	{
		for (int i = 0; i < players.length; i++)
		{
			if(isTargetNull(players[i]))
				continue;
			else
				return players[i];
		}
		return null;
	}
	
	public void clientMessageChain(PlayerController target, AppMessage msg)
	{
		if(isFinish)
		{
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
			if(!isPlayerDead() && !isMonsterDead())
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

		if(targetIndex < 0 || targetIndex > 9)
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
		
		MonsterBattleTmp mbt = null;
		int monsterBeforeHP = 0,monsterAfterHP = 0;
		
		if(skill.targetType[0] == ActiveSkill.TARGET_TYPE_PLAYERS) //己方
		{
		    if(targetIndex >= players.length)
		    {
				return;
		    }
		    if(players[targetIndex] == null)
		    {
		    	return;
		    }
		    sbt.setSkillTarget(players[targetIndex]);
		   
		}
		else if(skill.targetType[0] == ActiveSkill.TARGET_TYPE_MONSTERS) //2：敌方
		{
		
			if(targetIndex >= monsterLength)
			{
				return;
			}
		    if(monsters[targetIndex] == null)
		    {
		    	return;
		    }
		    if(monsters[targetIndex].getAttachment().isDead())
		    {
		    	for (int i = 0; i < monsters.length; i++)
		    	{
		    		if(monsters[i] != null && !monsters[i].getAttachment().isDead())
		    		{
		    			targetIndex = i;
		    			break;
		    		}
				}
		    }
		    if(monsters[targetIndex].getAttachment().isDead())
		    	return;
		    mbt = (MonsterBattleTmp) monsters[targetIndex].getAttachment();
			sbt.setSkillTarget(monsters[targetIndex]);
			
			monsterBeforeHP = mbt.getMonster().hitPoint;
		}	
		else if(skill.targetType[0] == ActiveSkill.TARGET_TYPE_ONESELF)//3.自己
		{
			sbt.setSkillTarget(target);
		}
		else
		{
			System.out.println("PVEController 240 other target Object");
			return;
		}

		boolean result = sbt.processActiveSkill(skill);

		if(mbt != null)
		{
			monsterAfterHP = mbt.getMonster().hitPoint;
			//设置怪物仇恨值
			if(mbt.isDead() || target.isDead() || !target.isOnline())
				mbt.initHatred(getPlayerIndex(target));
			else
			{
				mbt.setHatred(monsterBeforeHP - monsterAfterHP, skill, getPlayerIndex(target), 0, 0);
			}
		}
		
		if(result)
		{
			skill.processTime = target.getNetConnection().getLastReadTime();
			target.skillStartTime = skill.processTime;
			target.skillNeedTime = skill.CDTimer==0?4800:skill.CDTimer;
		}
		sbt.isSkillUsing = false;

	}
	
	public int getPlayerIndex(PlayerController target)
	{
		for (int i = 0; i < players.length; i++)
		{
			if(players[i] == null)
				continue;
			if(players[i].getName().equals(target.getName()))
			{
				return i;
			}
		}
		return -1;
	}
	

	public void setFighters(PlayerController[] players, SpriteController [] sprite)
	{
		 MonsterController [] monsters = (MonsterController[])sprite;
		 super.setFighters(players, monsters);
		 
		 this.players = players;
		 this.monsters = monsters;
		 playerLength = players.length;
		 monsterLength = monsters.length;
		 
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
			 addPlayer(players[i]);
		 }
		 
		 
		 MonsterBattleTmp mt = null;
		 
		 length = monsters.length;
		 for( i = 0 ; i < length ; i ++)
		 {
			 if(monsters[i] == null)
				 continue;
			 
			 mt = new MonsterBattleTmp();
			 monsters[i].setAttachment(mt);
			 mt.setBattle(this);
			 mt.setMonster(monsters[i]);
			 mt.setTeamNo(BattleController.TEAM2);
			 mt.setIndex(i);
			 monsters[i].setParent(this);
		 }
	}
	
	
	public void writeTo(ByteBuffer buffer)
	{
		buffer.writeByte(BattleController.TEAM1);

		int playerLength = players.length;

		buffer.writeByte(playerLength);
		
		for(int i = 0 ; i < playerLength ; i ++)
		{
			if(isTargetNull(players[i]))
				continue;
			
			buffer.writeByte(i);
			buffer.writeInt(players[i].getID());
			players[i].writeBattlePetInfo(buffer);
			players[i].sendAlwaysValue();
		}
		
		buffer.writeByte(BattleController.TEAM2);

		int monsterLength = monsters.length;
		
		buffer.writeByte(monsterLength);

		for(int i = 0 ; i < monsterLength ; i ++)
		{
			if(monsters[i] == null)
				continue;
			
			buffer.writeByte(i);
			monsters[i].getMonster().writeBattleTo(buffer);

		}
	}
	
	public MonsterController[] getMonsters()
	{
		return monsters;
	}

	public PlayerController[] getPlayers()
	{
		return players;
	}
	
	
	/**
	 * 复活选择
	 * @param buffer
	 */
	public void resetChoose(PlayerController target,ByteBuffer buffer)
	{
		if(!(target.getParent() instanceof BattleController))
			return;
		if(!isPlayerDead() && !isMonsterDead())
			return;
		
		int type = buffer.readByte();
		
		if(isPlayerDead())
		{
			int playerCount = getPlayerCount();

			if(playerCount <= 0)
				return;
			
			if(type == 1)
			{
				Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
				if(bag.money < RESETMONEY)
				{
					target.sendAlert(ErrorCode.ALERT_SHOP_NO_MONEY);
					return;
				}
				
				bag.money -= RESETMONEY;

				for(int i = 0 ; i < playerCount ; i ++)
				{
					PlayerController everyone = (PlayerController)playerList.get(i);
					if(isTargetNull(everyone))
						continue;
					PlayerBattleTmp pbt = (PlayerBattleTmp) everyone.getAttachment();
					removeEffect(pbt.getEffectList(),pbt);
//					removeHaloEffect(pbt.getBuffBox().getHaloList(), pbt);
					List list = pbt.getBuffBox().getEffectList();
					removeHaloEffect(list, everyone);
					room.setParentPlayer(everyone);
					everyone.setParent(room);
				}
				
				ByteBuffer endBattleBuff = new ByteBuffer(1);
				endBattleBuff.writeBoolean(true);
				dispatchMsg(SMsg.S_BATTLE_PLAYER_BATTLEEND, endBattleBuff);
				
				sendEquipMoney(target);
				
				sendResetInfo(target);
				
				fixPlayerBaseInfo(players,true);
			}
			else if(type == 2)
			{
				super.uninit(1);
				
				fixPlayerBaseInfo(players,false);
				
				for (int i = 0; i < players.length; i++) 
				{
					if(isTargetNull(players[i]))
						continue;

					if(players[i].isAuto)
						players[i].setAuto(false, 0, null);
					break;
				}
				
				if(players.length < 1)
					return;
				int roomId = room.getRebirthId(players[0].getPlayer().camp);
				if(roomId == 0)
					return;
				Object obj = DataFactory.getInstance().getGameObject(roomId);
				if(obj == null || !(obj instanceof RoomController))
					return;
				RoomController rc = (RoomController) obj;
				if(rc.id == room.id)
					return;

				room.sendPlayerMove(rc, players);
			}
			else
			{
				System.out.println("PVEController 481 error:type=="+type);
				return;
			}
			
			isFinish = true;
		}
	}

	
	private void checkMoneyBattle()
	{
		if(players.length > 1)
			return;
		if(players[0] == null)
			return;
		if(!players[0].isOnline())
			return;
		MoneyBattle mb = DataFactory.getInstance().getMoneyBattle(room.id);
		if(mb == null)
			return;
		
		List list = DataFactory.getInstance().getBoxDropPropList();
		List rList = new ArrayList();
		for (int i = 0; i < list.size(); i++) 
		{
			BoxDropProp bdp = (BoxDropProp) list.get(i);
			if(bdp == null)
				continue;
			if(bdp.boxType == mb.type + 6)
			{
				rList.add(bdp);
			}
		}
		int cr = (int) (Math.random() * 10000) + 1;
		int bdpRate = 0;
		BoxDropProp cb = null;
		for (int i = 0; i < rList.size(); i++) 
		{
			BoxDropProp bdp = (BoxDropProp) rList.get(i);
			bdpRate += bdp.rate;
			if(cr <= bdpRate)
			{
				cb = bdp;
				break;
			}
		}
		if(cb == null)
			return;
		
		Goods tmp = cb.getGoodsByMoney();
		if(tmp == null)
			return;
		
		Bag bag = (Bag) players[0].getPlayer().getExtPlayerInfo("bag");
		if(!bag.isCanAddGoodsToBag(tmp))
		{
			players[0].sendAlert(ErrorCode.ALERT_BAG_IS_FULL);
			return;
		}

		String msg = DC.getMoneyBattleString(players[0], Utils.split(name, "-vs-")[0], tmp);
		if(isBossNotice(tmp))
		{
			players[0].sendGetGoodsInfo(3,true, msg);//向世界聊天发送信息
		}
		else
		{
			players[0].sendGetGoodsInfo(1,false, msg);//向自己聊天发送信息
		}
		
		if(tmp.token >= 500)
		{
			String str = "";
			if(mb.type == 1)
				str = "qt";
			else if(mb.type == 2)
				str = "by";
			else if(mb.type == 3)
				str = "hj";
			else if(mb.type == 4)
				str = "ts";
			//保存消费记录 类型3 抽奖大于500
			GameServer.getInstance().getWorldManager().getJobObserver().addJob(GameServer.JOB_DATABASEJOB,
					new PayJob(players[0],3,0,tmp.token,GameServer.getInstance().id+":"+str+"  "+players[0].getName()+":"+tmp.name+":"+tmp.goodsCount));
			
			//>500元宝保存一次
			GameServer.getInstance().getWorldManager().getJobObserver().addJob(GameServer.JOB_DATABASEJOB,
					new SaveJob(GameServer.getInstance().getWorldManager(),players[0].getPlayer(),SaveJob.MONEY_EXPENSE_SAVE));
		}
		
		mb.setMoneyBattleInfo(players[0]);
		bag.sendAddGoods(players[0], tmp);
	}

	/**
	 * 通知战斗结束
	 */
	public void uninit()
	{
		if(isFinish)
			return;
		
		for (int i = 0; i < monsterLength; i++)
		{
			monsters[i].setParent(room);
		}
		if(isMonsterDead())
		{
			isWin = true;
			MonsterGroupController mgc = room.getMonsterGroupByIndex(objectIndex);
			if(mgc != null)
			{
				mgc.setVisibled(room, 3);
			}
				
			for(int i = 0 ; i < players.length ; i ++)
			{
				if(isTargetNull(players[i]))
					continue;
				PlayerBattleTmp pbt = (PlayerBattleTmp) players[i].getAttachment();
				removeEffect(pbt.getEffectList(),pbt);
				removeHaloEffect(pbt.getBuffBox().getHaloList(),players[i]);
			}

			
			PlayerController player = null;
			StringBuffer sb = null;
			
			for (int i = 0; i < players.length; i++) 
			{
				if(isTargetNull(players[i]))
					continue;
				PlayerBattleTmp pbt = (PlayerBattleTmp) players[i].getAttachment();
				if(players[i].getAam() != null)
				{
					pbt.maExp = players[i].getAam().getExpByM_A(players[i], this);
				}
			}
			
			for (int i = 0; i < players.length; i++) 
			{
				if(isTargetNull(players[i]))
					continue;
				PlayerBattleTmp pbt = (PlayerBattleTmp) players[i].getAttachment();
//				if(pbt.isDead())
//					continue;
				
				for (int j = 0; j < monsterLength; j++) 
				{
					players[i].onPlayerKillMonster(monsters[j]);//这个方法一定要放在checkVisibleExit()前面;
				}

				if(pbt.battlePoint > 0)
				{
					Bag bag = (Bag) players[i].getPlayer().getExtPlayerInfo("bag");
					bag.point += pbt.battlePoint;
					bag.sendAddGoods(players[i], null);
					sb = new StringBuffer();
					sb.append(DC.getString(DC.BATTLE_24));
					sb.append(": ");
					sb.append(pbt.battlePoint);
					players[i].sendGetGoodsInfo(1, false, sb.toString());
				}
				
				if(pbt.battleHonor > 0)
				{
					PVPInfo pvp = (PVPInfo) players[i].getPlayer().getExtPlayerInfo("PVPInfo");
					pvp.honourPoint += pbt.battleHonor;
					sb = new StringBuffer();
					sb.append(DC.getString(DC.BATTLE_25));
					sb.append(": ");
					sb.append(pbt.battleHonor);
					players[i].sendGetGoodsInfo(1, false, sb.toString());
				}
				
				if(pbt.battleExp+pbt.maExp > 0)
				{
					long disExp = players[i].addExp(pbt.battleExp + pbt.maExp,true,true,false);
					if(disExp > 0)
					{
						boolean flag = false;
						if(players[i].getTeam() != null)
						{
							if(players[i].getTeam().isLeader(players[i]))
								flag = true;
						}
						
						sb = new StringBuffer();
						sb.append(DC.getString(DC.BATTLE_26));
						sb.append(": ");
						sb.append(pbt.battleExp+pbt.maExp);
						
						if(flag)
						{
							sb.append("(");
							sb.append(DC.getString(DC.BATTLE_27));//队长加成
							sb.append(")");
						}
						if(players[i].getFamilyPlayers(players) > 0)
						{
							sb.append("(");
							sb.append(DC.getString(DC.BATTLE_28));//家族加成
							sb.append(")");
						}
						if(players[i].getTeam() != null)
						{
							sb.append("(");
							sb.append(DC.getString(DC.BATTLE_29));//队伍加成
							sb.append(")");
						}
						if(pbt.maExp > 0)
						{
							sb.append("(");
							sb.append(DC.getString(DC.BATTLE_30));//帮忙加成
							sb.append(")");
						}
						if(players[i].isLoverInTeam(players))
						{
							sb.append("(");
							sb.append(DC.getString(DC.BATTLE_31));//结婚加成
							sb.append(")");
						}
						players[i].sendGetGoodsInfo(1,false,sb.toString());
						sb = null;
					}
				}
				
				player = players[i];
				
				players[i].sendAlwaysValue();
			}
			
			if(player != null)
				player.checkVisibleExit();
			
			battleGoods();
			checkMoneyBattle();

			isFinish = true;
			
			setM_AInfo();
			
			if(isBoss())
			{
				for(int i = 0 ; i < players.length ; i ++)
				{
					if(isTargetNull(players[i]))
						continue;
					
					ByteBuffer endBattleBuff = new ByteBuffer(1);
					endBattleBuff.writeBoolean(true);
					players[i].getNetConnection().sendMessage(new SMsg(SMsg.S_BATTLE_PLAYER_BATTLEEND,endBattleBuff));
				}
				
				if(isMoveRoom())
				{
					for(int i = 0 ; i < players.length ; i ++)
					{
						if(isTargetNull(players[i]))
							continue;
						players[i].moveToRoom(DataFactory.INITROOM);
						break;
					}
				}
				else
				{
					for(int i = 0 ; i < players.length ; i ++)
					{
						if(isTargetNull(players[i]))
							continue;
						players[i].setParent(room);
					}
				}
			}
			else
			{
				for(int i = 0 ; i < players.length ; i ++)
				{
					if(isTargetNull(players[i]))
						continue;
					players[i].setParent(room);
					
					ByteBuffer endBattleBuff = new ByteBuffer(1);
					endBattleBuff.writeBoolean(true);
					players[i].getNetConnection().sendMessage(new SMsg(SMsg.S_BATTLE_PLAYER_BATTLEEND,endBattleBuff));
				}
			}
			
			GameServer.getInstance().getWorldManager().getJobObserver().addJob(GameServer.JOB_GAME1, new NoticeJob(this));
			
			fixPlayerBaseInfo(players,false);
		}
		else if(isPlayerDead())
		{	
			PlayerController p = getAnyOnePlayer(players);
			if(p == null)
			{
				isFinish = true;
				return;
			}
			int roomId = room.getRebirthId(p.getPlayer().camp);
			
			if(isReturn())
			{
				super.uninit(0);
				
				fixPlayerBaseInfo(players,false);
				
				if(isMoveRoom())
					p.moveToRoom(roomId==0?DataFactory.INITROOM:roomId);
				else
				{
					for(int i = 0 ; i < players.length ; i ++)
					{
						if(isTargetNull(players[i]))
							continue;
						players[i].setParent(room);
					}
				}
				isFinish = true;
			}
			else
			{
				if(roomId == 0)
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
						everyone.setParent(room);
					}
					
					ByteBuffer endBattleBuff = new ByteBuffer(1);
					endBattleBuff.writeBoolean(true);
					dispatchMsg(SMsg.S_BATTLE_PLAYER_BATTLEEND, endBattleBuff);
					isFinish = true;
					
					fixPlayerBaseInfo(players,false);

				}
				else  //弹出回城复活还是原地复活
				{
					Object obj = DataFactory.getInstance().getGameObject(roomId);
					if(obj == null || !(obj instanceof RoomController))
					{
						MainFrame.println("PVEController uninit roomId error:"+roomId);
						return;
					}
					RoomController rc = (RoomController) obj;
//					if(room.id == rc.id)
//						return;
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
		}
	}
	/** 掉特殊物品公告后就不公告BOSS死的消息了,默认要公告 */
	private boolean isNoticeBossDie = true;
	/**
	 * 打死怪物后是否要送玩家回城
	 * @return
	 */
	private boolean isReturn()
	{
		for (int i = 0; i < monsters.length; i++) 
		{
			if(monsters[i].getMonster().bodyType == 1)
				return true;
		}
		return false;
	}
	
	private void battleGoods()
	{
		if(isFinish)
			return;
		for (int i = 0; i < players.length; i++) 
		{
			if(isTargetNull(players[i]))
				continue;
			PlayerBattleTmp pbt = (PlayerBattleTmp) players[i].getAttachment();
//			if(pbt.isDead())
//				continue;
			if(pbt.battleGoods.length == 0 || pbt.battleGoods == null)
				continue;
			Bag bag = (Bag) players[i].getPlayer().getExtPlayerInfo("bag");
			
			StringBuffer buffer = new StringBuffer();
			for (int j = 0; j < pbt.battleGoods.length; j++) 
			{
				Goods goods = pbt.battleGoods[j];
				if(goods == null)
					continue;
				if(!bag.isCanAddGoodsToBag(goods))
					return;
				
				String objectIndex = goods.objectIndex+"";
				if(goods.repeatNumber > 1)
				{
					Goods g = bag.getGoodsById(goods.id);
					if(g != null)
						objectIndex = g.objectIndex+"";
				}
				
				StringBuffer sb = new StringBuffer();
				sb.append(players[i].getName());
				sb.append(" ");
				sb.append(DC.getString(DC.BATTLE_32));
				sb.append(": |[");
				sb.append(goods.name);
				sb.append("]#p:");
				sb.append(objectIndex);
				sb.append(":");
				sb.append(goods.quality);
				sb.append(":");
				sb.append(players[i].getID());
				players[i].sendGetGoodsInfo(2,false,sb.toString());
				
				if(isBossNotice(goods))
				{
					buffer.append("|[");
					buffer.append(goods.name);
					buffer.append("]");
					buffer.append("#p:");
					buffer.append(objectIndex);
					buffer.append(":");
					buffer.append(goods.quality);
					buffer.append(":");
					buffer.append(players[i].getID());
				}
				
				bag.sendBattleAddGoods(players[i], goods);
			}
			
			if(buffer.length() > 0)
			{
				isNoticeBossDie = false;
				//XX很满意的收下了赎身法宝XX,并赠送了一本"三好BOSS守则"给XX,让它回去好好研读,今后不得再出来作恶
				players[i].sendGetGoodsInfo(3, true, DC.getBossGoodsString(players[i].getName(), buffer.toString(), Utils.split(name, "-vs-")[0]));
			}
			
			pbt.battleGoods = new Goods[0];
		}
		
		sendBossBoxToPlayer();
	}
	
	/**
	 * 活动BOSS才公告
	 * partyBoss notice
	1045010418	女武神之魂
	1045010419	魔女之魂
	1045010420	光之战士之魂
	1045010421	暗之战士之魂
	1045000275	五百点荣誉值兑换道具
	1045010423	进化小元宝
	1045010406	72变宝石
	*/
	private boolean isBossNotice(Goods	goods)
	{
		Map map = (Map) DataFactory.getInstance().getAttachment(DataFactory.ATTACH_NPC_FLYER);
		BaseFlayer bf = null;
		for(Iterator it = map.keySet().iterator();it.hasNext();)
		{
			bf = (BaseFlayer) map.get(it.next());
			if(bf.roomId == room.id)
				break;
		}
		if(bf == null)
		{
			return false;
		}
		
		if(bf.type == 19)
		{
			if(!isBoss())
				return false;
			return DataFactory.isGoodsNotice(GoodsNotice.PARTY_BOSS, goods);
		}
		else if(bf.type == 6)
		{
			if(!isBoss())
				return false;
			return DataFactory.isGoodsNotice(GoodsNotice.SPECIAL_BOSS, goods);
		}
		else if(bf.type == 24 || bf.type == 25 || bf.type == 26 || bf.type == 27)
		{
			if(isBoss())
				return false;
			return DataFactory.isGoodsNotice(GoodsNotice.MONEY_BATTLE, goods);
		}
		return false;
	}
	
	/**
	 * 给玩家宝箱
	 */
	private void sendBossBoxToPlayer()
	{
		for (int i = 0; i < players.length; i++)
		{
			if(isTargetNull(players[i]))
				continue;
			
			int count = 0;
			Mail mail = new Mail(DC.getString(DC.BATTLE_36));
			mail.setTitle("BOSS"+DC.getString(DC.BATTLE_37));
			boolean flag = false;
			for (int j = 0; j < monsterLength; j++) 
			{
				if(monsters[j] == null)
					continue;
				int boxId = monsters[j].getMonster().boxId;
				if(boxId <= 0)
					continue;
				Object obj = DataFactory.getInstance().getGameObject(boxId);
				if(obj == null || !(obj instanceof Goods))
				{
					System.out.println("PVEController sendBox error:boxId:"+boxId);
					continue;
				}
				count++;
				Goods box = (Goods) obj;
				Goods newGoods = (Goods) Goods.cloneObject(box);
				if(count > 2)
					break;
				newGoods.objectIndex = GameServer.getInstance().getDatabaseAccessor().getGoodsObjIndex();
				
				StringBuffer sb = new StringBuffer();
				sb.append(DC.getString(DC.BATTLE_38));
				sb.append(monsters[j].getName());
				sb.append(",");
				sb.append(DC.getString(DC.BATTLE_39));
				sb.append("[");
				sb.append(newGoods.name);
				sb.append("]");
				mail.setContent(sb.toString());
				mail.addAttach(newGoods);
				
				sb = new StringBuffer();
				sb.append("[");
				sb.append(newGoods.name);
				sb.append("]");
				sb.append(DC.getString(DC.BATTLE_40));
				players[i].sendGetGoodsInfo(1,false,sb.toString());
			
				flag = true;
			}
			if(flag)
			{
				if(players[i].isOnline())
					mail.send(players[i]);
			}
		}
		
	}
	
	
	/**
	 * 战斗结束分配战利品(暂时还不加到背包中)
	 * @param goodsList
	 */
	private void disBattleGoods(List goodsList)
	{
		for (int j = 0; j < goodsList.size(); j++) 
		{
			Goods goods = (Goods) goodsList.get(j);
			if(goods == null)
				continue;
			PlayerController player = getPlayerRandom();
			if(isTargetNull(player))
				continue;
			PlayerBattleTmp pbt = (PlayerBattleTmp) player.getAttachment();
			if(pbt.isDisGoods)
			{
				player = getPlayerByDisGoods();
			}
			if(player == null)
			{
				setPlayerDisGoods();
				player = getPlayerRandom();
			}
			if(player == null)
				continue;
			if(!player.isOnline())
				continue;
			pbt = (PlayerBattleTmp) player.getAttachment();
//			if(pbt.isDead())
//				continue;
			goods.objectIndex = GameServer.getInstance().getDatabaseAccessor().getGoodsObjIndex();
			Bag bag = (Bag) player.getPlayer().getExtPlayerInfo("bag");
			if(!bag.isCanAddGoodsToBag(player, goods))
			{
				player.sendAlert(ErrorCode.ALERT_BAG_IS_FULL);
				continue;
			}
			int count = goods.getGoodsCount();
			TaskInfo taskInfo = (TaskInfo) player.getPlayer().getExtPlayerInfo("taskInfo"); 

			if(taskInfo.getCurrentTaskSize() > 0)
			{
				boolean result = taskInfo.onPlayerGotItem(goods.id,count, player);

				if(!result && goods.type == 3)
					continue;
			}	
			else
			{
				if(goods.type == 3)
					continue;
			}	
			pbt.setBattleGoods(goods);

			pbt.isDisGoods = true;
		}
		goodsList.clear();
	}
	
	private PlayerController getPlayerRandom()
	{
		for (int i = 0; i < players.length; i++) 
		{
			int random = (int) (Math.random() * players.length);
			PlayerController player = players[random];
			if(isTargetNull(players[i]))
				continue;
			PlayerBattleTmp pbt = (PlayerBattleTmp) player.getAttachment();
			if(pbt.isDead())
				continue;
			return player;
		}
		return null;
	}
	
	private void setPlayerDisGoods()
	{
		for (int i = 0; i < players.length; i++)
		{
			if(isTargetNull(players[i]))
				continue;
			PlayerBattleTmp pbt = (PlayerBattleTmp) players[i].getAttachment();
			pbt.isDisGoods = false;
		}
	}
	
	private PlayerController getPlayerByDisGoods()
	{
		for (int i = 0; i < players.length; i++)
		{
			if(isTargetNull(players[i]))
				continue;
			PlayerBattleTmp pbt = (PlayerBattleTmp) players[i].getAttachment();
			if(pbt.isDead())
				continue;
			if(!pbt.isDisGoods)
				return players[i];
		}
		return null;
	}
	

	
	public void checkDeadState()
	{
		if(!isMonsterDead() && !isPlayerDead())
			return;
		if(isMonsterDead())
		{
			if(isCheckDrop)
				return;
			for (int j = 0; j < monsters.length; j++)
			{
				monsters[j].getAttachment().loserMercy(monsters[j].getAttachment());
			}
			if(battleGoods.length > 0)
			{
				List goodsList = new ArrayList(battleGoods.length);
				for (int j = 0; j < battleGoods.length; j++) 
				{
					if(battleGoods[j] != null)
					{
						goodsList.add(battleGoods[j]);
					}
				}
				disBattleGoods(goodsList);
				
				battleGoods = new Goods[0];
			}
			int levelCount = 0 ;
			for (int i = 0; i < players.length; i++) 
			{
				if(players[i] != null && players[i].isOnline())
					levelCount += players[i].getPlayer().level;
			}
			
			long allExp = 0;//怪物掉落总经验
			int monsterLevelCount = 0;//怪物总等级
			for (int i = 0; i < monsters.length; i++) 
			{
				allExp += monsters[i].getMonster().experience;
				monsterLevelCount += monsters[i].getMonster().level;
			}
			int monsterLevel = monsterLevelCount / monsters.length;//怪物平均等级
			
			int everyOnePoint = battlePoint;
			int everyOneHonor = battleHonor;
			for (int i = 0; i < players.length; i++)
			{
				if(isTargetNull(players[i]))
					continue;
				PlayerBattleTmp pbt = (PlayerBattleTmp) players[i].getAttachment();
//				if(pbt.isDead())
//					continue;
				pbt.setBattlePoint(everyOnePoint);
				pbt.setBattleHonor(everyOneHonor);
				long getExp = players[i].teamExp(allExp,levelCount,monsterLevel,1);
				pbt.setBattleExp(getExp);
			}
			
			isCheckDrop = true;
			
		}
		
		boolean result = false;
		if(isMonsterDead())
			result = true;
		else if(isPlayerDead())
			result = false;
		
		for (int i = 0; i < players.length; i++) 
		{
			if(isTargetNull(players[i]))
				continue;
			PlayerBattleTmp pbt = (PlayerBattleTmp) players[i].getAttachment();
			
			ByteBuffer buffer = new ByteBuffer();
			buffer.writeBoolean(result);
			buffer.writeByte(getCatType(pbt));
			String name = getBossBox();
			if(!name.isEmpty() && result)
			{
				/************以下数据暂时用不到，PVE时用来发送获得的经验，金钱，物品名，是否有BOSS宝箱，客户端好播放动画**************************/
				buffer.writeBoolean(true);
				buffer.writeUTF(name);
				buffer.writeUTF(pbt.battleExp+"");
				buffer.writeInt((int)pbt.battlePoint);
				/*********************************************/
			}
			else
				buffer.writeBoolean(false);
			players[i].getNetConnection().sendMessage(new SMsg(SMsg.S_BATTLE_PLAYER_BATTLERESULT,buffer));
			
			
			
			BuffBox buffBox = (BuffBox)players[i].getPlayer().getExtPlayerInfo("buffBox");
			buffBox.pveCDtime = 3000; //设置3秒钟避免快速发消息请求
		}
		
		endTime = System.currentTimeMillis();
	}
	
	private String getBossBox()
	{
		for (int i = 0; i < monsters.length; i++)
		{
			int boxId = monsters[i].getMonster().boxId;
			if(boxId == 0)
				continue;
			Object obj = DataFactory.getInstance().getGameObject(boxId);
			if(obj == null || !(obj instanceof Goods))
				continue;
			return ((Goods)obj).name;
		}
		return "";
	}
	
	public byte getCatType(PlayerBattleTmp pbt)
	{
		if(pbt.battleGoods.length == 0 || pbt.battleGoods == null)
			return 1;
		else
		{
			for (int i = 0; i < pbt.battleGoods.length; i++) 
			{
				if(pbt.battleGoods[i] == null)
					continue;
				if(pbt.battleGoods[i].type == 6)
					return 3;
			}
		}
		return 2;
	}
	
	
	public boolean isPlayerDead()
	{
		int playerLength = players.length;
		for (int i = 0; i < playerLength; i++)
		{
			if(isTargetNull(players[i]))
				continue;
			if(!players[i].getAttachment().isDead())
				return false;
		}
		return true;
	}
	public boolean isMonsterDead()
	{
		int monsterLength = monsters.length;
		for (int i = 0; i < monsterLength; i++)
		{
			if(!monsters[i].getAttachment().isDead() || monsters[i].getMonster().hitPoint > 0)
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
			for(int i = 0 ; i < length ; i ++)
			{
				if(players[i] != null)
				{
					player = players[i];
					break;
				}
			}
			
			if(player == null)
				return;
			
			super.dispatchIsBattleWithoutRoom(isBattle,player);
		}
		else
		{
			super.dispatchIsBattleWithoutRoom(isBattle, target);
		}
	}

	
	public void sendNoticeInfo()
	{
		if(isNoticed)
			return;
		
		if(!isNoticeBossDie)
			return;

		for (int i = 0; i < monsters.length; i++)
		{
			if(monsters[i].getMonster().bodyType == 1)
			{
				PlayerController leader = null;
				PlayerController pc = getAnyonePlayer();
				if(pc == null)
					return;
				if(pc.getTeam() != null)
				{
					leader = pc.getTeam().getLeader();
				}
				else 
					leader = pc;
				/**
				 * 号外!!号外!!在XXXXXXX队伍的强力攻击和完美防御的威慑下,YYYYYYY应声倒地。
				 */
				String[] strs = Utils.split(name, "-vs-");	
				leader.sendGetGoodsInfo(3, true, DC.getBossDieString(leader.getName(),strs[0]));
				break;
			}
		}
		
		isNoticed = true;
	}
	
	private boolean isMoveRoom()
	{
		MoneyBattle mb = DataFactory.getInstance().getMoneyBattle(room.id);
		if(mb == null)
			return true;
		return false;
	}
	
	
}
