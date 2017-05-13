package cc.lv1.rpg.gs.entity;

import vin.rabbit.util.ByteBuffer;
import cc.lv1.rpg.gs.data.DC;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.controller.BattleController;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.controller.RoomController;
import cc.lv1.rpg.gs.net.SMsg;
import cc.lv1.rpg.gs.other.ErrorCode;
import cc.lv1.rpg.gs.util.FontConver;

public class PVPInvitation extends ConfirmJob 
{
	private PlayerController inviter;
	
	public PVPInvitation(PlayerController inviter,PlayerController target)
	{
		this.inviter = inviter;
		super.setSource(target);
	}
	
	public void confirm(boolean accepted)
	{
		if (accepted)
			startPK();
		else
		{
			if(inviter == null || source == null)
				return;
			inviter.sendGetGoodsInfo(1,false, source.getName()+DC.getString(DC.INVITE_3));
		}
	}

	public void cancel()
	{
		if(inviter == null)
			return;
		inviter.sendGetGoodsInfo(1,false, DC.getString(DC.INVITE_1));
	}
	
	private void startPK()
	{
		if(inviter == null || source == null)
			return;
		if(inviter.getParent() instanceof RoomController && source.getParent() instanceof RoomController)
		{
			RoomController room = (RoomController) inviter.getParent();
			room.playerHitPlayer(inviter, source);
		}
		else if(source.getParent() instanceof BattleController)
		{
			inviter.sendAlert(ErrorCode.ALERT_PLAYER_IS_BATTLING);
			return;
		}
	}
}
