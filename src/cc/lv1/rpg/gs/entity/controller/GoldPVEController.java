package cc.lv1.rpg.gs.entity.controller;

import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.impl.ArrayList;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.impl.GoldParty;
import cc.lv1.rpg.gs.entity.impl.Goods;
import cc.lv1.rpg.gs.entity.impl.Mail;
import cc.lv1.rpg.gs.entity.impl.battle.PlayerBattleTmp;
import cc.lv1.rpg.gs.net.SMsg;
import cc.lv1.rpg.gs.util.FontConver;

public class GoldPVEController extends PVEController 
{
	/** 失败后需要加的虚弱状态 */
//	public static final int LOSESTATEGOODS = 1045000142;
	
	/** 胜利后需要加的等待状态 */
//	public static final int WINSTATEGOODS = 1045000251;
	/**
	 * 通知战斗结束
	 */
	public void uninit()
	{
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
		}
		
		if(players.length <= 0)
			return;
		if(players[0] != null)
		{
			PlayerBattleTmp pbt = (PlayerBattleTmp) players[0].getAttachment();
			if(pbt != null)
			{
				if(pbt.getEffectList() != null)
					removeEffect(pbt.getEffectList(),pbt);
				if(!isWin)
				{
					if(pbt.getBuffBox() != null && pbt.getBuffBox().getEffectList() != null)
						removeHaloEffect(pbt.getBuffBox().getEffectList(),players[0]);
				}
			}
			players[0].setParent(room);

			ByteBuffer endBattleBuff = new ByteBuffer(1);
			endBattleBuff.writeBoolean(true);
			players[0].getNetConnection().sendMessage(new SMsg(SMsg.S_BATTLE_PLAYER_BATTLEEND, endBattleBuff));
		}

		fixPlayerBaseInfo(players,false);
		
		if(room.isGoldPartyPKRoom || room.isGoldPartyRoom)
		{
			GoldPartyController gpc = GoldPartyController.getInstance();
			
			if(isTimeOver)
			{
				if(players[0] != null)
				{
					players[0].moveToRoom(GoldPartyController.PARTYINITROOM);
				}
			}
			else
			{
				if(room.isGoldPartyPKRoom)
				{
					GoldParty gp = DataFactory.getInstance().getGoldPartyByRoom(room.id);
					if(isPlayerDead())
					{
						gpc.setWinOrLoseInfo(players[0],false,gp,this);
					}
					else if(isMonsterDead())
					{
						gpc.setWinOrLoseInfo(players[0],true,gp,this);
					}
				}
			}
		}
	
		isFinish = true;
	}
	

	/**
	 *  逃跑
	 */
	public void escBattle(PlayerController target)
	{
		if(players[0] != null)
			removeBP(players[0]);
		
		if(GoldPartyController.getInstance().isEnded() && !GoldPartyController.getInstance().isReady())
		{
			players[0].moveToRoom(GoldPartyController.PARTYINITROOM);
		}
		
		if(monsters[0] != null)
			monsters[0].setParent(room);
		
		isFinish = true;
		
	}
	
	
	


}
