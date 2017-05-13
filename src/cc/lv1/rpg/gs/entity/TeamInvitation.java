package cc.lv1.rpg.gs.entity;

import cc.lv1.rpg.gs.data.DC;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.controller.TeamController;
import cc.lv1.rpg.gs.entity.impl.Role;
import cc.lv1.rpg.gs.other.ErrorCode;
import cc.lv1.rpg.gs.util.FontConver;


/**
 * 组队邀请
 * @author dxw
 *
 */
public class TeamInvitation extends ConfirmJob
{
	
	private PlayerController inviter;
	
	
	public TeamInvitation(PlayerController inviter,PlayerController target)
	{
		this.inviter = inviter;
		super.setSource(target);
	}
	
	public TeamController confirm(boolean accepted)
	{
		if (accepted)
			return makeTeam();
		return null;
	}

	public void cancel()
	{
		if(inviter == null)
			return;
		inviter.sendGetGoodsInfo(1,false, DC.getString(DC.INVITE_1));
	}
	
	private TeamController makeTeam()
	{
		if(inviter == null || source == null)
			return null;
		TeamController team = null;
		if(inviter.getTeam() != null)//如果邀请方的队伍中已经创建
		{
			team = inviter.getTeam();
		}
		else
		{//邀请方的队伍还没有创建
			team = new TeamController(inviter.getID());
			team.setLeader(inviter);//设置邀请方为队长
			inviter.getPlayer().state = Role.STATE_TEAMLEADER;//设置状态为带队
			team.addPlayer(inviter);//把队长加入队伍
			inviter.getRoom().addTeam(team);
		}
		if(team.getPlayer(source.getPlayer().id) != null)
		{
			source.sendAlert(ErrorCode.ALERT_PLAYER_IN_TEAM);
			return null;
		}
		source.getPlayer().state = Role.STATE_TEAMMEMBER;//设置被邀方状态为队员
		team.addPlayer(source);//把队员加入队伍
		return team;
		//关键看inviter的team是不是null,如果已经有队了就把被邀请的玩家加入到队里面。如果没有对，创建一个队列
	}
}
