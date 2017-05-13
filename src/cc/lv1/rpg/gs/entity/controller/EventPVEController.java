package cc.lv1.rpg.gs.entity.controller;

import vin.rabbit.net.AppMessage;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.impl.ArrayList;
import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.data.DC;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.ext.Bag;
import cc.lv1.rpg.gs.entity.ext.BuffBox;
import cc.lv1.rpg.gs.entity.ext.EquipSet;
import cc.lv1.rpg.gs.entity.ext.PVPInfo;
import cc.lv1.rpg.gs.entity.ext.SkillTome;
import cc.lv1.rpg.gs.entity.ext.StoryEvent;
import cc.lv1.rpg.gs.entity.ext.TaskInfo;
import cc.lv1.rpg.gs.entity.impl.Goods;
import cc.lv1.rpg.gs.entity.impl.Mail;
import cc.lv1.rpg.gs.entity.impl.Monster;
import cc.lv1.rpg.gs.entity.impl.battle.MonsterBattleTmp;
import cc.lv1.rpg.gs.entity.impl.battle.PlayerBattleTmp;
import cc.lv1.rpg.gs.entity.impl.battle.skill.ActiveSkill;
import cc.lv1.rpg.gs.entity.impl.story.Event;
import cc.lv1.rpg.gs.net.SMsg;
import cc.lv1.rpg.gs.other.ErrorCode;

public class EventPVEController extends BattleController
{
	public PlayerController [] players = null;

	public MonsterController [] monsters = null;
	
	public int playerLength;
	
	public int monsterLength;
	
	private boolean isBoss = false;
	
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
			sbt.setSkillTarget(monsters[targetIndex]);
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
		
		if(result)
		{
			skill.processTime = target.getNetConnection().getLastReadTime();
			target.skillStartTime = skill.processTime;
			target.skillNeedTime = skill.CDTimer==0?4800:skill.CDTimer;
		}
		sbt.isSkillUsing = false;

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
		if(players.length == 0 || players == null)
			return;
		if(players[0] == null)
			return;
		if(isTargetNull(players[0]))
			return;
		
		if(isMonsterDead())
		{
			isWin = true;
			MonsterGroupController mgc = room.getMonsterGroupByIndex(objectIndex);
			if(mgc != null)
			{
				mgc.setVisibled(room, 3);
			}
				
			PlayerBattleTmp pbt = (PlayerBattleTmp) players[0].getAttachment();
			removeEffect(pbt.getEffectList(),pbt);
			removeHaloEffect(pbt.getBuffBox().getHaloList(),players[0]);
			
			StringBuffer sb = null;
			
			for (int j = 0; j < monsterLength; j++) 
			{
				players[0].onPlayerKillMonster(monsters[j]);//这个方法一定要放在checkVisibleExit()前面;
			}

			if(pbt.battlePoint > 0)
			{
				Bag bag = (Bag) players[0].getPlayer().getExtPlayerInfo("bag");
				bag.point += pbt.battlePoint;
				bag.sendAddGoods(players[0], null);
				sb = new StringBuffer();
				sb.append(DC.getString(DC.BATTLE_24));
				sb.append(": ");
				sb.append(pbt.battlePoint);
				players[0].sendGetGoodsInfo(1, false, sb.toString());
			}
			
			if(pbt.battleHonor > 0)
			{
				PVPInfo pvp = (PVPInfo) players[0].getPlayer().getExtPlayerInfo("PVPInfo");
				pvp.honourPoint += pbt.battleHonor;
				sb = new StringBuffer();
				sb.append(DC.getString(DC.BATTLE_25));
				sb.append(": ");
				sb.append(pbt.battleHonor);
				players[0].sendGetGoodsInfo(1, false, sb.toString());
			}
	
			if(pbt.battleExp+pbt.maExp > 0)
			{
				long disExp = players[0].addExp(pbt.battleExp + pbt.maExp,true,true,false);
				if(disExp > 0)
				{
					sb = new StringBuffer();
					sb.append(DC.getString(DC.BATTLE_26));
					sb.append(": ");
					sb.append(pbt.battleExp+pbt.maExp);
					players[0].sendGetGoodsInfo(1,false,sb.toString());
					sb = null;
				}
				
				players[0].sendAlwaysValue();
			}
			
			players[0].checkVisibleExit();
			
			battleGoods();

			players[0].setParent(room);
			
			ByteBuffer endBattleBuff = new ByteBuffer(1);
			endBattleBuff.writeBoolean(true);
			players[0].getNetConnection().sendMessage(new SMsg(SMsg.S_BATTLE_PLAYER_BATTLEEND,endBattleBuff));
			
			checkEvent();
			
			fixPlayerBaseInfo(players,false);
			
			isFinish = true;
			
		}
		else if(isPlayerDead())
		{	
			PlayerBattleTmp pbt = (PlayerBattleTmp) players[0].getAttachment();
			removeEffect(pbt.getEffectList(),pbt);
			removeHaloEffect(pbt.getBuffBox().getEffectList(),players[0]);
			players[0].setParent(room);
			
			ByteBuffer endBattleBuff = new ByteBuffer(1);
			endBattleBuff.writeBoolean(true);
			dispatchMsg(SMsg.S_BATTLE_PLAYER_BATTLEEND, endBattleBuff);
			isFinish = true;
			
			fixPlayerBaseInfo(players,false);
		}
		
		if(isTimeOver)
			players[0].moveToRoom(DataFactory.STORYROOM);
	}
	
	/**
	 * 胜利后检测事件
	 */
	private void checkEvent()
	{
		if(isTimeOver)
			return;
		Event event = players[0].getEvent();
		if(event == null)
			return;
		boolean isEndRoom = event.isEndRoom(room);
		if(isEndRoom)
		{
			if(event.getStory().team != 3)
				players[0].moveToRoom(DataFactory.STORYROOM);
			else
				players[0].moveToRoom(DataFactory.CHONGGUANROOM);
			StoryEvent se = (StoryEvent) players[0].getPlayer().getExtPlayerInfo("storyEvent");
			se.flushStoryMap(players[0],event.getStory().team);
			se.finishEvent(players[0], event);
		}
	}
	
	private void battleGoods()
	{
		if(isFinish)
			return;
		PlayerBattleTmp pbt = (PlayerBattleTmp) players[0].getAttachment();
		if(pbt.battleGoods.length == 0 || pbt.battleGoods == null)
			return;
		Bag bag = (Bag) players[0].getPlayer().getExtPlayerInfo("bag");
		
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
			sb.append(players[0].getName());
			sb.append(" ");
			sb.append(DC.getString(DC.BATTLE_32));
			sb.append(": |[");
			sb.append(goods.name);
			sb.append("]#p:");
			sb.append(objectIndex);
			sb.append(":");
			sb.append(goods.quality);
			sb.append(":");
			sb.append(players[0].getID());
			players[0].sendGetGoodsInfo(2,false,sb.toString());
			
			bag.sendBattleAddGoods(players[0], goods);
		}
		pbt.battleGoods = new Goods[0];
		
		sendBossBoxToPlayer();
	}
	
	/**
	 * 给玩家宝箱
	 */
	private void sendBossBoxToPlayer()
	{
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
			players[0].sendGetGoodsInfo(1,false,sb.toString());
		
			flag = true;
		}
		if(flag)
		{
			if(players[0].isOnline())
				mail.send(players[0]);
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
			if(isTargetNull(players[0]))
				continue;
			PlayerBattleTmp pbt = (PlayerBattleTmp) players[0].getAttachment();
			pbt = (PlayerBattleTmp) players[0].getAttachment();
			goods.objectIndex = GameServer.getInstance().getDatabaseAccessor().getGoodsObjIndex();
			Bag bag = (Bag) players[0].getPlayer().getExtPlayerInfo("bag");
			if(!bag.isCanAddGoodsToBag(players[0], goods))
			{
				players[0].sendAlert(ErrorCode.ALERT_BAG_IS_FULL);
				continue;
			}
			int count = goods.getGoodsCount();
			TaskInfo taskInfo = (TaskInfo) players[0].getPlayer().getExtPlayerInfo("taskInfo"); 

			if(taskInfo.getCurrentTaskSize() > 0)
			{
				boolean result = taskInfo.onPlayerGotItem(goods.id,count, players[0]);

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
			int levelCount = players[0].getPlayer().level;
			
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
			PlayerBattleTmp pbt = (PlayerBattleTmp) players[0].getAttachment();
			pbt.setBattlePoint(everyOnePoint);
			pbt.setBattleHonor(everyOneHonor);
			long getExp = players[0].teamExp(allExp,levelCount,monsterLevel,1);
			pbt.setBattleExp(getExp);
			
			isCheckDrop = true;
			
		}
		
		boolean result = false;
		if(isMonsterDead())
			result = true;
		else if(isPlayerDead())
			result = false;
		
		PlayerBattleTmp pbt = (PlayerBattleTmp) players[0].getAttachment();
		
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
		players[0].getNetConnection().sendMessage(new SMsg(SMsg.S_BATTLE_PLAYER_BATTLERESULT,buffer));
		
		
		
		BuffBox buffBox = (BuffBox)players[0].getPlayer().getExtPlayerInfo("buffBox");
		buffBox.pveCDtime = 3000; //设置3秒钟避免快速发消息请求
		
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
		if(!players[0].getAttachment().isDead())
				return false;
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
	 *  逃跑
	 */
	public void escBattle(PlayerController target)
	{	
		removeBP(target);
		for (int i = 0; i < getMonsters().length; i++)
		{
			getMonsters()[i].setParent(room);
		}
		
		isFinish = true;
		
		if(isTimeOver)
			players[0].moveToRoom(DataFactory.INITROOM);
	}
	
	public void removePlayer(PlayerController target)
	{
		removeBP(target);
		for (int i = 0; i < getMonsters().length; i++)
		{
			getMonsters()[i].setParent(room);
		}
		
		isFinish = true;
	}
}
