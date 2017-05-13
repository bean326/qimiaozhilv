package cc.lv1.rpg.gs.entity;

import cc.lv1.rpg.gs.data.DC;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.controller.AamController;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.util.FontConver;

/**
 * 师徒邀请
 * @author dxw
 *
 */
public class AamInvitation extends ConfirmJob
{

	private PlayerController inviter;
	
	
	public AamInvitation(PlayerController inviter,PlayerController target)
	{
		this.inviter = inviter;
		super.setSource(target);
	}
	
	public AamController confirm(boolean accepted)
	{
		if (accepted)
			return makeAam();
		return null;
	}
	
	
	public void cancel()
	{
		if(inviter == null)
			return;
		inviter.sendGetGoodsInfo(1,false, DC.getString(DC.INVITE_1));
	}
	
	private AamController makeAam()
	{
		if(inviter == null || source == null)
			return null;
		
		if(inviter.getAam() != null || source.getAam() != null)
			return null;

		AamController aam = new AamController();
		aam.setMaster(inviter);
		aam.setApprentice(source);
		inviter.setAam(aam);
		source.setAam(aam);
		return aam;
	}
	
}
