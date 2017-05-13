package cc.lv1.rpg.gs.entity;

import vin.rabbit.util.ByteBuffer;
import cc.lv1.rpg.gs.data.DC;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.controller.BusinessController;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.net.SMsg;
import cc.lv1.rpg.gs.other.ErrorCode;
import cc.lv1.rpg.gs.util.FontConver;

public class BusinessInvitation extends ConfirmJob
{
	private PlayerController inviter;
	
	public BusinessInvitation(PlayerController inviter,PlayerController target)
	{
		this.inviter = inviter;
		super.setSource(target);
	}
	
	public void confirm(boolean accepted)
	{
		if (accepted)
			business();
		else
		{
			if(inviter == null || source == null)
				return;
			inviter.sendGetGoodsInfo(1,false, source.getName()+DC.getString(DC.INVITE_2));
		}
	}

	public void cancel()
	{
		if(inviter == null)
			return;
		inviter.sendGetGoodsInfo(1, false,DC.getString(DC.INVITE_1));
	}
	
	private void business()
	{
		if(inviter == null || source == null)
			return;
		BusinessController busi = new BusinessController();
		busi.setFighters(inviter, source);
		ByteBuffer buffer = new ByteBuffer(4);
		buffer.writeUTF(source.getName());
		inviter.getNetConnection().sendMessage(new SMsg(SMsg.S_PLAYER_BUSINESS_RESPONSE_COMMAND,buffer));
		buffer = new ByteBuffer(4);
		buffer.writeUTF(inviter.getName());
		source.getNetConnection().sendMessage(new SMsg(SMsg.S_PLAYER_BUSINESS_RESPONSE_COMMAND,buffer));
	}
}
