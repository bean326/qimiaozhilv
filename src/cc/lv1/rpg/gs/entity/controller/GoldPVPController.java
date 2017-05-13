package cc.lv1.rpg.gs.entity.controller;

import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.impl.ArrayList;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.ext.Bag;
import cc.lv1.rpg.gs.entity.impl.GoldParty;
import cc.lv1.rpg.gs.entity.impl.Goods;
import cc.lv1.rpg.gs.entity.impl.Mail;
import cc.lv1.rpg.gs.entity.impl.battle.PlayerBattleTmp;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsProp;
import cc.lv1.rpg.gs.net.SMsg;
import cc.lv1.rpg.gs.other.ErrorCode;
import cc.lv1.rpg.gs.util.FontConver;

/**
 * 黄金斗士(不能组队)
 * @author bean
 *
 */
public class GoldPVPController extends PVPController 
{
	/**
	 * 通知战斗结束
	 */
	public void uninit()
	{
		if(targetPlayers.length < 1)
			return;
		if(targetPlayers[0] != null)
		{
			PlayerBattleTmp pbt = (PlayerBattleTmp) targetPlayers[0].getAttachment();
			if(pbt != null)
			{
				if(pbt.getEffectList() != null)
					removeEffect(pbt.getEffectList(),pbt);
				if(isTargetPlayerDead())
				{
					if(pbt.getBuffBox() != null && pbt.getBuffBox().getHaloList() != null)
						removeHaloEffect(pbt.getBuffBox().getHaloList(), targetPlayers[0]);
				}
			}
			targetPlayers[0].setParent(room);

			ByteBuffer endBattleBuff = new ByteBuffer(1);
			endBattleBuff.writeBoolean(true);
			targetPlayers[0].getNetConnection().sendMessage(new SMsg(SMsg.S_BATTLE_PLAYER_BATTLEEND, endBattleBuff));
		}
		
		fixPlayerBaseInfo(targetPlayers,false);
		
		
		if(players.length <= 0)
			return;
		if(players[0] != null)
		{
			PlayerBattleTmp pbt = (PlayerBattleTmp) players[0].getAttachment();
			if(pbt != null)
			{
				if(pbt.getEffectList() != null)
					removeEffect(pbt.getEffectList(),pbt);
				if(isPlayerDead())
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
		
		
		GoldPartyController gpc = GoldPartyController.getInstance();
		
		if(isTimeOver)
		{
			if(players[0] != null)
			{
				players[0].moveToRoom(GoldPartyController.PARTYINITROOM);
			}
			
			if(targetPlayers[0] != null)
			{
				targetPlayers[0].moveToRoom(GoldPartyController.PARTYINITROOM);
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
					gpc.setWinOrLoseInfo(targetPlayers[0],true,gp,this);
				}
				else if(isTargetPlayerDead())
				{
					gpc.setWinOrLoseInfo(targetPlayers[0],false,gp,this);
					gpc.setWinOrLoseInfo(players[0],true,gp,this);
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
		if(targetPlayers[0] != null)
			removeBP(targetPlayers[0]);
		
		if(GoldPartyController.getInstance().isEnded() && !GoldPartyController.getInstance().isReady())
		{
			players[0].moveToRoom(GoldPartyController.PARTYINITROOM);
			targetPlayers[0].moveToRoom(GoldPartyController.PARTYINITROOM);
		}
		
		isFinish = true;
		
	}
	
}
