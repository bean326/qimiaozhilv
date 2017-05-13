package cc.lv1.rpg.gs.entity;

import vin.rabbit.util.ByteBuffer;
import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.data.DC;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.controller.FamilyController;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.net.SMsg;
import cc.lv1.rpg.gs.net.impl.SaveJob;
import cc.lv1.rpg.gs.other.ErrorCode;
import cc.lv1.rpg.gs.util.FontConver;

/**
 * 家族邀请
 * @author dxw
 *
 */
public class FamilyInvitation extends ConfirmJob
{
	private PlayerController inviter;
	
	
	public FamilyInvitation(PlayerController inviter,PlayerController target)
	{
		this.inviter = inviter;
		super.setSource(target);
	}
	
	public void confirm(boolean accepted)
	{
		if (accepted)
		{
			addFamily();
		}
		else
		{
			if(inviter == null)
				return;
			inviter.sendAlert(ErrorCode.ALERT_PLAYER_REFUSE_IN_FAMILY);
		}
	}

	private void addFamily()
	{
		if(inviter == null || source == null)
			return;
		FamilyController family = inviter.getFamily();
		
		if(inviter.getPlayer().familyId == 0 || family == null)
			return;
		
		
		if(source.getPlayer().familyId != 0 || source.getFamily() != null)
		{
			inviter.sendAlert(ErrorCode.ALERT_PLAYER_IN_OTHER_FAMILY);
			return;
		}
		
		source.getPlayer().familyId = family.id;
		source.getPlayer().familyName = family.name;
		family.addNameToFamily(source.getName());
		family.addPlayer(source);
		source.setFamilyController(family);
		
		inviter.sendAlert(ErrorCode.ALERT_PLAYER_AGREE_IN_FAMILY);
		
		ByteBuffer buffer = new ByteBuffer();
		buffer.writeUTF(family.name);
		source.getNetConnection().sendMessage(new SMsg(SMsg.S_PLAYER_FAMILYRESPONSE_COMMAND,buffer));
		
		/**
		 * 玩家加入家族，更新家族列表
		 */
		GameServer.getInstance().getDatabaseAccessor().createOrUpdateFamilyInfo(family, false);
		
		source.getWorldManager().getJobObserver().addJob(GameServer.JOB_DATABASEJOB,
				new SaveJob(source.getWorldManager(),source.getPlayer(),SaveJob.FAMILY_ADD_SAVE));
		inviter.getWorldManager().getJobObserver().addJob(GameServer.JOB_DATABASEJOB,
				new SaveJob(inviter.getWorldManager(),inviter.getPlayer(),SaveJob.FAMILY_ADD_SAVE));
	}
	
	public void cancel()
	{
		inviter.sendGetGoodsInfo(1,false, DC.getString(DC.INVITE_1));
	}
	
}
