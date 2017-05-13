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
import cc.lv1.rpg.gs.entity.ext.EquipSet;
import cc.lv1.rpg.gs.entity.ext.OtherExtInfo;
import cc.lv1.rpg.gs.entity.ext.PVPInfo;
import cc.lv1.rpg.gs.entity.ext.PetTome;
import cc.lv1.rpg.gs.entity.ext.SkillTome;
import cc.lv1.rpg.gs.entity.ext.TaskInfo;
import cc.lv1.rpg.gs.entity.impl.BaseFlayer;
import cc.lv1.rpg.gs.entity.impl.Goods;
import cc.lv1.rpg.gs.entity.impl.Mail;
import cc.lv1.rpg.gs.entity.impl.Monster;
import cc.lv1.rpg.gs.entity.impl.Player;
import cc.lv1.rpg.gs.entity.impl.battle.MonsterBattleTmp;
import cc.lv1.rpg.gs.entity.impl.battle.PlayerBattleTmp;
import cc.lv1.rpg.gs.entity.impl.battle.SpriteBattleTmp;
import cc.lv1.rpg.gs.entity.impl.battle.skill.ActiveSkill;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsNotice;
import cc.lv1.rpg.gs.entity.impl.pet.Pet;
import cc.lv1.rpg.gs.net.SMsg;
import cc.lv1.rpg.gs.net.impl.NoticeJob;
import cc.lv1.rpg.gs.net.impl.PressureJob;
import cc.lv1.rpg.gs.other.ErrorCode;
import cc.lv1.rpg.gs.util.FontConver;

/**
 * 副本战斗控制器
 * @author bean
 *
 */
public class CopyPVEController extends BattleController 
{
	private PlayerController [] players = null;

	private MonsterController [] monsters = null;
	
	private int playerLength;
	
	private int monsterLength;
	
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
		super.update(timeMillis);
		
		int monsterSize = monsters.length;
		for(int i = 0 ; i < monsterSize ; i ++)
		{
			if(monsters[i] == null)
				continue;
			monsters[i].update(timeMillis);
		}
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
				escBattle(target);
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
			if(isWin)
				return;
			
			isWin = true;
			
			setWinOrLoseInfo(true);

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
					long disExp = players[i].addExp(pbt.battleExp+pbt.maExp,true,true,false);
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
					}
				}
				
				player = players[i];
				
				players[i].sendAlwaysValue();
			}
			
			if(player != null)
				player.checkVisibleExit();
			battleGoods();
			
			isFinish = true;
			
			setM_AInfo();
			
			for(int i = 0 ; i < players.length ; i ++)
			{
				if(isTargetNull(players[i]))
					continue;
				players[i].setParent(room);
				
				ByteBuffer endBattleBuff = new ByteBuffer(1);
				endBattleBuff.writeBoolean(true);
				players[i].getNetConnection().sendMessage(new SMsg(SMsg.S_BATTLE_PLAYER_BATTLEEND,endBattleBuff));
			}
			
			fixPlayerBaseInfo(players,false);
			
			CopyController copy = DataFactory.getInstance().getCopyByArea(room.getParent().id);
			if(copy == null)
			{	
				System.out.println("CopyController uninit copy is null areaId:"+room.getParent().id);
				return;
			}

			if(room.id == copy.endRoomId)
			{
				int nextRoom = copy.inHideRoom();
				for(int i = 0 ; i < players.length ; i ++)
				{
					if(isTargetNull(players[i]))
						continue;
					if(nextRoom == 0)
						copy.finishCopy(players[i]);
				}
				for(int i = 0 ; i < players.length ; i ++)
				{
					if(isTargetNull(players[i]))
						continue;
					if(nextRoom == 0)
					{
						if(copy.isHonorCopy())
							players[i].moveToRoom(DataFactory.HONORROOM);
						else
							players[i].moveToRoom(DataFactory.INITROOM);
					}
					else
					{
						players[i].moveToRoom(nextRoom);
						/**
						 * 恭喜XX的旅行团,幸运的遇到了沉睡的****(BOSS名字),他们能否击倒BOSS，携宝而归呢?让我们拭目以待!
						 */
						String leaderName = "";
						if(players[i].getTeam() == null)
							leaderName = players[i].getName();
						else
							leaderName = players[i].getTeam().getLeader().getName();
						if(players[i].getRoom().getMonsterGroups() != null && players[i].getRoom().getMonsterGroups().length > 0
								&& players[i].getRoom().getMonsterGroups()[0] != null)
							players[i].sendGetGoodsInfo(3, true, DC.getMeetBossStirng(leaderName, players[i].getRoom().getMonsterGroups()[0].name));
					}
					break;
				}
			}
			else
			{
				for(int i = 0 ; i < players.length ; i ++)
				{
					if(isTargetNull(players[i]))
						continue;
					if(isTimeOver || room.id > copy.endRoomId)
						copy.finishCopy(players[i]);
				}
				for(int i = 0 ; i < players.length ; i ++)
				{
					if(isTargetNull(players[i]))
						continue;
					if(isTimeOver || room.id > copy.endRoomId)
					{
						if(copy.isHonorCopy())
							players[i].moveToRoom(DataFactory.HONORROOM);
						else
							players[i].moveToRoom(DataFactory.INITROOM);
					}
					else
					{
						players[i].moveToRoom(room.id+1);
					}
					break;
				}
			}
			
			GameServer.getInstance().getWorldManager().getJobObserver().addJob(GameServer.JOB_GAME1, new NoticeJob(this));
		}
		else if(isPlayerDead())
		{
			PlayerController p = getAnyOnePlayer(players);
			if(p == null)
			{
				isFinish = true;
				return;
			}
			
			CopyController copy = DataFactory.getInstance().getCopyByArea(room.getParent().id);
			if(copy == null)
			{	
				System.out.println("CopyController uninit copy is null areaId:"+room.getParent().id);
				return;
			}
			
			setWinOrLoseInfo(false);
			
			int roomId = room.getRebirthId(p.getPlayer().camp);
			
			super.uninit(0);
			
			fixPlayerBaseInfo(players,false);
			
			if(copy.isHonorCopy())
				p.moveToRoom(roomId==0?DataFactory.HONORROOM:roomId);
			else
				p.moveToRoom(roomId==0?DataFactory.INITROOM:roomId);
			isFinish = true;
		}
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
				{
					return;
				}
				
				
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
				
				//处理特殊公告物品
				if(room.getCopy() != null && room.getCopy().type == 2 && DataFactory.isGoodsNotice(GoodsNotice.TIME_TRAIN, goods))
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
		
		if(room.getCopy() != null)
		{
			for (int j = 0; j < players.length; j++) 
			{
				if(isTargetNull(players[j]))
					continue;
				if(players[j].getPlayer().copyPoint >= Player.MAXCOPYPOINT)
					continue;
				else
				{
					if(room.getCopy().type == 3 || room.getCopy().type == 4)
					{
						if(room.getCopy().isHideRoom(room.id))
							players[j].getPlayer().copyPoint += 6;
						else
							players[j].getPlayer().copyPoint += 3;
					}
					else if(room.getCopy().type == 5)
					{
						if(room.getCopy().isHideRoom(room.id))
							players[j].getPlayer().copyPoint += 6;
						else
							players[j].getPlayer().copyPoint += 4;
					}
				}
				if(players[j].getPlayer().copyPoint > Player.MAXCOPYPOINT)
				{
					players[j].getPlayer().copyPoint = Player.MAXCOPYPOINT;
				}	
				players[j].sendCopyPoint();
			}
		}
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
	
	
	
	
	
	private PlayerController getPlayerRandom()
	{
		for (int i = 0; i < players.length; i++) 
		{
			int random = (int) (Math.random() * players.length);
			PlayerController player = players[random];
			if(isTargetNull(player))
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
				buffer.writeInt((int) pbt.battleExp);
				buffer.writeUTF(pbt.battleExp+"");
				buffer.writeInt((int) pbt.battlePoint);
				/*********************************************/
			}
			else
				buffer.writeBoolean(false);
			players[i].getNetConnection().sendMessage(new SMsg(SMsg.S_BATTLE_PLAYER_BATTLERESULT,buffer));
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
	
	/**
	 * 掉线 
	 */
	public void removePlayer(PlayerController target)
	{
		if(target.getTeam() == null)
		{
			for (int i = 0; i < players.length; i++) 
			{
				if(isTargetNull(players[i]))
					continue;
				removeBP(players[i]);
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
		setWinOrLoseInfo(false);
		
		if(target.getTeam() == null || target.getTeam().isLeader(target))
		{
			for (int i = 0; i < players.length; i++) 
			{
				if(isTargetNull(players[i]))
					continue;
				removeBP(players[i]);
			}
		}
		
		if(isTimeOver)
		{
			for (int i = 0; i < players.length; i++) 
			{
				if(isTargetNull(players[i]))
					continue;
				players[i].moveToRoom(DataFactory.INITROOM);
			}
		}
		
		isFinish = true;
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

	/**
	 * 设置胜利失败信息
	 * @param result true 胜利 false 失败
	 */
	public void setWinOrLoseInfo(boolean result)
	{
		if(isTimeOver)
			return;
		if(isFinish)
			return;
		
		CopyController copy = DataFactory.getInstance().getCopyByArea(room.getParent().id);
		if(copy == null)
		{
			System.out.println("CopyPVEController setWinOrLoseInfo copy is null:"+room.getParent().id);
			return;
		}
		if(result)
		{
			for (int i = 0; i < players.length; i++)
			{
				if(isTargetNull(players[i]))
					continue;
				players[i].setOtherExtInfo("assRoomId",(room.id+1)+"",room);
				copy.addPlayerPoint(players[i], copy.winPoint,room);
			}
		}
		else
		{
			for (int i = 0; i < players.length; i++)
			{
				if(isTargetNull(players[i]))
					continue;
				players[i].setOtherExtInfo("assRoomId",room.id+"",room);
				copy.addPlayerPoint(players[i], copy.losePoint,room);
			}
		}
	}
	
	/** 掉特殊物品公告后就不公告BOSS死的消息了,默认要公告 */
	private boolean isNoticeBossDie = true;
	
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
				 * 天啦!XX的团队太强悍了,强大的xx被他们彻底征服了,他们获得了至高无上的荣誉和珍贵的财宝!
				 */
				String[] strs = Utils.split(name, "-vs-");
				leader.sendGetGoodsInfo(3, true, DC.getKillBossString(leader.getName(), strs[0]));
				
				StringBuffer sb = new StringBuffer();
				sb.append(WorldManager.getTypeTime("yyyy-MM-dd HH:mm:ss", WorldManager.currentTime));
				sb.append(" leaderName[");
				sb.append(leader.getName());
				sb.append("]kill[");
				sb.append(strs[0]);
				sb.append("]");
				GameServer.getInstance().getWorldManager().getJobObserver().addJob(GameServer.JOB_GAME1, 
						new PressureJob(sb.toString(),"killBossInfo"));
//				PressureTest.getInstance().saveTextByFileName(sb.toString(), "killBossInfo");
				break;
			}
		}
		
		isNoticed = true;
	}
	
}
