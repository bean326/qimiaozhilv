package cc.lv1.rpg.gs.entity.controller;


import vin.rabbit.net.AppMessage;
import vin.rabbit.net.abs.NetConnection;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.i.Map;
import vin.rabbit.util.collection.impl.ArrayList;
import vin.util.ext.Timer;
import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.data.DC;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.data.Exp;
import cc.lv1.rpg.gs.data.MailRemind;
import cc.lv1.rpg.gs.data.MarkChar;
import cc.lv1.rpg.gs.data.PetExp;
import cc.lv1.rpg.gs.data.Poster;
import cc.lv1.rpg.gs.data.PressureTest;
import cc.lv1.rpg.gs.data.TaskManager;
import cc.lv1.rpg.gs.data.UseChar;
import cc.lv1.rpg.gs.entity.AamInvitation;
import cc.lv1.rpg.gs.entity.BusinessInvitation;
import cc.lv1.rpg.gs.entity.CenterGoods;
import cc.lv1.rpg.gs.entity.ConfirmJob;
import cc.lv1.rpg.gs.entity.FamilyInvitation;
import cc.lv1.rpg.gs.entity.MarryInvitation;
import cc.lv1.rpg.gs.entity.PVPInvitation;
import cc.lv1.rpg.gs.entity.RoomExit;
import cc.lv1.rpg.gs.entity.ShopCenter;
import cc.lv1.rpg.gs.entity.TeamInvitation;
import cc.lv1.rpg.gs.entity.TopCenter;
import cc.lv1.rpg.gs.entity.container.PlayerContainer;
import cc.lv1.rpg.gs.entity.ext.AnswerParty;
import cc.lv1.rpg.gs.entity.ext.Bag;
import cc.lv1.rpg.gs.entity.ext.BuffBox;
import cc.lv1.rpg.gs.entity.ext.EquipSet;
import cc.lv1.rpg.gs.entity.ext.FriendList;
import cc.lv1.rpg.gs.entity.ext.MailBox;
import cc.lv1.rpg.gs.entity.ext.OtherExtInfo;
import cc.lv1.rpg.gs.entity.ext.PVPInfo;
import cc.lv1.rpg.gs.entity.ext.PetTome;
import cc.lv1.rpg.gs.entity.ext.PlayerSetting;
import cc.lv1.rpg.gs.entity.ext.ReviseBaseInfo;
import cc.lv1.rpg.gs.entity.ext.SkillTome;
import cc.lv1.rpg.gs.entity.ext.Storage;
import cc.lv1.rpg.gs.entity.ext.StoryEvent;
import cc.lv1.rpg.gs.entity.ext.TaskInfo;
import cc.lv1.rpg.gs.entity.impl.GoldBox;
import cc.lv1.rpg.gs.entity.impl.BoxDropProp;
import cc.lv1.rpg.gs.entity.impl.Goods;
import cc.lv1.rpg.gs.entity.impl.Mail;
import cc.lv1.rpg.gs.entity.impl.MoneyBattle;
import cc.lv1.rpg.gs.entity.impl.Player;
import cc.lv1.rpg.gs.entity.impl.RewardSender;
import cc.lv1.rpg.gs.entity.impl.Role;
import cc.lv1.rpg.gs.entity.impl.Shop;
import cc.lv1.rpg.gs.entity.impl.Task;
import cc.lv1.rpg.gs.entity.impl.UpRole;
import cc.lv1.rpg.gs.entity.impl.answer.Guide;
import cc.lv1.rpg.gs.entity.impl.answer.Question;
import cc.lv1.rpg.gs.entity.impl.battle.PlayerBattleTmp;
import cc.lv1.rpg.gs.entity.impl.battle.SpriteBattleTmp;
import cc.lv1.rpg.gs.entity.impl.battle.effect.Effect;
import cc.lv1.rpg.gs.entity.impl.battle.skill.ActiveSkill;
import cc.lv1.rpg.gs.entity.impl.battle.skill.PetSkill;
import cc.lv1.rpg.gs.entity.impl.battle.skill.Skill;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsEquip;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsMarry;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsNotice;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsPetEquip;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsProp;
import cc.lv1.rpg.gs.entity.impl.pet.Pet;
import cc.lv1.rpg.gs.entity.impl.pet.PetSkillStudy;
import cc.lv1.rpg.gs.entity.impl.pet.PetUpRule;
import cc.lv1.rpg.gs.entity.impl.story.Event;
import cc.lv1.rpg.gs.entity.impl.story.Story;
import cc.lv1.rpg.gs.gui.MainFrame;
import cc.lv1.rpg.gs.net.BaseConnection;
import cc.lv1.rpg.gs.net.SMsg;
import cc.lv1.rpg.gs.net.impl.GetTopJob;
import cc.lv1.rpg.gs.net.impl.LogJob;
import cc.lv1.rpg.gs.net.impl.PayJob;
import cc.lv1.rpg.gs.net.impl.SaveJob;
import cc.lv1.rpg.gs.other.ErrorCode;
import cc.lv1.rpg.gs.util.IdCardValidator;
import cc.lv1.rpg.gs.util.IdcardInfoExtractor;
/**
 * 玩家控制器
 * @author dxw
 *
 */
public class PlayerController extends SpriteController
{
	public static final int EXPMAXLEVEL = 7000;
	/** 安全在线时间 */
	public static final int SAFETIME = 3 * 60 * 60 * 1000;
	/** 警告在线时间 */
	public static final int UNSAFETIME = 5 * 60 * 60 * 1000;
	/** 成功验证身份后发放的礼品及数量 */
	public static final int IDCARDGOODS = 1045013010;
	public static final int IDCARDGOODSCOUNT = 1;
	/** 修改阵营需要的元宝 */
	public static final int CAMPMONEY = 500;
	/** 元宝宝箱一次需要花费的元宝 */
	public static final int MONEYBOX = 20;
	
	public static final int ESCTIME = 1000*60*5;
	
	public static final int RANDOMVALUE = 10;
	
	/** 队长经验加成 % */
	public static final int LEADEREXP = 50;
	/** 转职等级 */
	public static final int UPJOBLEVEL = 200;
	/** 加入阵营等级 */
	public static final int UPCAMPLEVEL = 200;
	/** 结婚需要等级 */
	public static final int MARRYLEVEL = 2000;
	
	private WorldManager worldManager;
	
	private RoomController roomController;
	
	private NetConnection netConnection;

	private Player player;
	
	private boolean online;
	
	private TeamController team;
	
	private AamController aam;
	
	private FamilyController family;

	private BusinessController business;
	
	private RoomExit roomExit = null;
	
	private PlayerController lastCharTarget = null;

	private String lastCharTargetName;
	
	/** 是否正在选择复活点 */
	public boolean isReset = false;
	
	/** 是否正在选择阵营 */
	public boolean isCamp = false;
	
	/** 是否正在转职*/
	public boolean isUpJob = false;
	
	/** 是否正在选择宠物变身 */
	public boolean isPetUp = false;
	
	/** 是否正在领取新手礼包 */
	public boolean isGift = false;
	
	/** 是否正在参加奇妙问答活动 */
	public boolean isAnswer = false;
	
	/** 是否正在开宝箱 */
	public boolean isBox = false;
	/** 开宝箱类型 0没有 1普通宝箱 2顶顶猫 */
	public int boxType;
	
	/** 是否正打开仓库*/
	public boolean isStorage = false;
	
	/** 是否是初始化登陆 */
	public boolean isInitLogin = true;
	
	/** 是否正在查看队伍列表 */
	public boolean isLookTeam = false;
	
	/** 是否自动打怪(挂机) */
	public boolean isAuto = false;
	
	/** 是否正在剧情中 */
	public boolean isStory = false;
	
	/** 剩余挂机次数 */
	public int autoCount;
	
	/** 当前总的挂机次数 */
	public int maxAutoCount;
	
	/** 挂机的怪 */
	public MonsterGroupController mgc;
	
	/** 组队状态(0.默认 1.拒绝组队 2.自动组队)*/
	public int teamState;
	
	public Exp expObj;
	
	public long currentTimeMsg = 0;
	
	public int wgCount;
	
	public boolean isSaved;
	
	/** 释放技能开始时间 */
	public long skillStartTime;
	
	/** 技能CD时间 */
	public long skillNeedTime;
	
	/**
	 *  限制寄卖数量
	 */
	public static final int TOTALCENTERGOODSCOUNT = 5;
	
	/**
	 * 寄卖数量
	 */
	public int centerGoodsCount;
	
	/** 玩家初始化房间(客户端进入房间后加载完成通知服务器) */
	public int initRoomId;
	
	private Event event;
	public Event getEvent()
	{
		return event;
	}
	public void setEvent(Event event)
	{
		if(this.event != null)
			this.event.isDoing = false;
		if(event != null)
		{
			if(!event.isDoing)
				event.isDoing = true;
			if(!isStory)
				isStory = true;
		}
		this.event = event;
	}

	public PlayerController(Player player)
	{
		this.player = player;
	}
	
	public void setIsBox(boolean result,int type)
	{
		isBox = result;
		boxType = type;
	}


	/** 获得玩家所在区域 */
	public AreaController getArea()
	{
		PlayerContainer container = getParent();
		
		if(container == null)
			return null;
		
		if(container instanceof RoomController) 
			return ((RoomController)container).getParent();
 		return worldManager.getAreaById(player.areaId);
	}
	
	/** 获得玩家所在房间 */
	public RoomController getRoom()
	{
		PlayerContainer container = getParent();
			if(container instanceof RoomController)
				return ((RoomController)container);
			
		RoomController room = null;
 		AreaController area = getArea();
		if(area != null)
		{
			room = area.getRoomById(player.roomId);	
		}

		if(room == null)
		{
			room = new RoomController();
		}
		return room;
	}
	
	/**
	 * 判断玩家当前状态，看是不是能开宝箱或者顶顶猫
	 * @return
	 */
	public boolean isPlayBox()
	{
		if(roomController == null)
			return false;
		if(roomController.isPkAble())
		{
			sendAlert(ErrorCode.ALERT_NOT_USE_IN_HERE);//"当前房间不能使用!"
			return false;
		}
		else
			return true;
	}
	
	
	/**
	 * 是否是队伍成员 队长除外
	 * @return
	 */
	public boolean isTeamMember()
	{
		if(team == null)
			return false;
		if(team.isLeader(this))
			return false;
		return true;
	}
	
	
	public boolean close()
	{
		if(netConnection != null)
		{
			netConnection.close();
			return true;
		}
		return false;
	}
	
	public void sendAlert(int alert)
	{
		ByteBuffer buffer = new ByteBuffer();
		buffer.writeInt(alert);
		if(netConnection != null)
			netConnection.sendMessage(new SMsg(SMsg.S_ALERT_MESSAGE,buffer));
	}
	
	public void sendError(String error)
	{
		ByteBuffer buffer = new ByteBuffer();
		buffer.writeUTF(error);
		if(netConnection != null)
			netConnection.sendMessage(new SMsg(SMsg.S_ERROR_MESSAGE,buffer));
	}

	public void sendException(int exception)
	{
		ByteBuffer buffer = new ByteBuffer();
		buffer.writeInt(exception);
		if(netConnection != null)
			netConnection.sendMessage(new SMsg(SMsg.S_EXCEPTION_MESSAGE,buffer));
	}
	
	public void setRoom(RoomController roomController)
	{
		this.roomController = roomController;
	}
	
	public void setBusiness(BusinessController business)
	{
		this.business = business;
	}
	
	/**
	 * 检测几种状态
	 * @return true表示正处于某种状态
	 */
	public boolean checkPlayerState()
	{
		String str = "";
		if(isBox)
		{
			if(boxType == 2)
				str = DC.getString(DC.PLAYER_1);
			else if(boxType == 1)
				str = DC.getString(DC.PLAYER_2);
		}
		if(isAnswer)
			str = DC.getString(DC.PLAYER_3);
		if(isPetUp)
			str = DC.getString(DC.PLAYER_4);
		if(isCamp)
			str = DC.getString(DC.PLAYER_5);
		if(isUpJob)
			str = DC.getString(DC.PLAYER_6);
		if(isStorage)
			str = DC.getString(DC.PLAYER_7);
		if(!str.isEmpty())
		{
			sendError(str);
			return true;
		}
		return false;
	}
	

	/**
	 * 检测几种状态（对队伍里的人）
	 * @return true表示正处于某种状态
	 */
	public boolean checkTeamState()
	{
		if(team == null)
			return checkPlayerState();
		else
		{
			PlayerController[] pcs = team.getPlayers();
			for (int i = 0; i < pcs.length; i++) 
			{
				if(pcs[i] == null || !pcs[i].isOnline())
					continue;
				String str = pcs[i].getChoose(0);
				if(!str.isEmpty())
				{
					sendError(str);
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 检测玩家是否正在进行阵营，转职，宠物变身的选择,是的话就返回错误信息，并发送
	 * type 1表示不检测开宝箱 0表示 要检测
	 */
	public boolean isChoose(PlayerController target,int type)
	{
		if(target.getTeam() != null)
		{
			PlayerController[] pcs = target.getTeam().getPlayers();
			for (int i = 0; i < pcs.length; i++)
			{
				if(pcs[i] != null)
				{
					String msg = pcs[i].getChoose(type);
					if(!msg.isEmpty())
					{
						String str = "";
						if(pcs[i].getName().equals(getName()))
						{
							if(type == 0)
							{	
								if(isBox)
								{	
									if(boxType == 2)
										str = DC.getString(DC.PLAYER_1);
									else if(boxType == 1)
										str = DC.getString(DC.PLAYER_2);
								}
								if(isAnswer)
									str = DC.getString(DC.PLAYER_3);
							}
							if(isPetUp)
								str = DC.getString(DC.PLAYER_4);
							if(isCamp)
								str = DC.getString(DC.PLAYER_5);
							if(isUpJob)
								str = DC.getString(DC.PLAYER_6);
						}
						if(!"".equals(str))
							sendError(str);
						else
							sendError(msg);
						return true;
					}
				}
			}
		}
		else
		{
			String msg = target.getChoose(type);
			if(!msg.isEmpty())
			{
				String str = "";
				if(target.getName().equals(getName()))
				{
					if(type == 0)
					{
						if(isBox)
						{
							if(boxType == 2)
								str = DC.getString(DC.PLAYER_1);
							else if(boxType == 1)
								str = DC.getString(DC.PLAYER_2);
						}
						if(isAnswer)
							str = DC.getString(DC.PLAYER_3);
					}	
					if(isPetUp)
						str = DC.getString(DC.PLAYER_4);
					if(isCamp)
						str = DC.getString(DC.PLAYER_5);
					if(isUpJob)
						str = DC.getString(DC.PLAYER_6);
				}
				if(!"".equals(str))
					sendError(str);
				else
					sendError(msg);
				return true;
			}
		}
		return false;
	}

	
	/**
	 * 玩家是否正在进行阵营，转职，宠物变身的选择,是的话就返回错误信息
	 * type 1表示不检测开宝箱 0表示 要检测
	 */
	public String getChoose(int type)
	{
		String msg = "";
		boolean isFlag = false;
		StringBuffer sb = new StringBuffer();
		sb.append(DC.getString(DC.PLAYER_8));
		sb.append(",[");
		sb.append(getName());
		sb.append("]");
		if(isUpJob)
		{
			isFlag = true;
			msg = DC.getString(DC.PLAYER_9);
		}
		if(isCamp)
		{
			isFlag = true;
			msg = DC.getString(DC.PLAYER_10);
		}
		if(isPetUp)
		{	
			isFlag = true;
			msg = DC.getString(DC.PLAYER_11);
		}

		if(type == 0)
		{
			if(isBox)
			{
				if(boxType == 2)
				{	
					isFlag = true;
					msg = DC.getString(DC.PLAYER_12);
				}
				else if(boxType == 1)
				{
					isFlag = true;
					msg = DC.getString(DC.PLAYER_13);
				}
			}
			if(isAnswer)
			{
				isFlag = true;
				msg = DC.getString(DC.PLAYER_14);
			}
			if(isStorage)
			{
				isFlag = true;
				msg = DC.getString(DC.PLAYER_15);
			}
		}
		
		if(isFlag)
		{
			sb.append(msg);
			return sb.toString();
		}
		else
			return "";
	}
	
	
	/**
	 * PK请求
	 * @param msg
	 */
	private void requestPK(AppMessage msg)
	{
		if(roomController.isPartyRoom() || roomController.isPartyPKRoom() || roomController.isCopyPartyRoom)
			return;
		if(roomController.isGoldPartyRoom || roomController.isGoldPartyPKRoom)
			return;
		int playerId = msg.getBuffer().readInt();
		if(getID() == playerId)
			return;
		if(getParent() instanceof BattleController)
		{
			sendAlert(ErrorCode.ALERT_PLAYER_IS_BATTLING);
			return;
		}
		if(getParent() instanceof BusinessController)
		{
			sendAlert(ErrorCode.ALERT_BUSINESS_ING_PLAYER);
			return;
		}
		if(!(getParent() instanceof RoomController))
		{
			sendAlert(ErrorCode.ALERT_PLAYER_ISNOT_ROOM);
			return;
		}
		RoomController room = (RoomController)getParent();
		PlayerController target = room.getPlayer(playerId);//被邀请者
		if(target == null)
		{
			sendAlert(ErrorCode.ALERT_PLAYER_NOTAT_ROOM);
			return;
		}
		if(target.isAuto)
		{
			sendAlert(ErrorCode.ALERT_TARGET_PLAYER_AUTO_BATTLEING);
			return;
		}
		if(target.isStory)
		{
			sendGetGoodsInfo(1, false, DC.getString(DC.PARTY_51));
			return;
		}
		Object obj = target.getParent();
		if(obj == null)
		{
			sendAlert(ErrorCode.ALERT_PLAYER_NOTAT_ROOM);
			return;
		}
		if(!(obj instanceof RoomController))
		{
			if(obj instanceof BattleController)
			{
				sendAlert(ErrorCode.ALERT_PLAYER_IS_BATTLING);
				return;
			}
			else if(obj instanceof BusinessController)
			{
				sendAlert(ErrorCode.ALERT_BUSINESS_ING_PLAYER);
				return;
			}
		}
		
		if(target.getRoom().id != getRoom().id)
		{
			sendAlert(ErrorCode.ALERT_PLAYER_NOTAT_ROOM);
			return;
		}
		if(target.getPlayer().hitPoint <= 0 || player.hitPoint <= 0)
		{
			sendAlert(ErrorCode.ALERT_PLAYER_NOT_LIFE);
			return;
		}
		if(target.getTeam() != null)
		{
			if(target.getTeam().isTeamPlayer(this))
			{
				sendAlert(ErrorCode.ALERT_SAME_TEAM_PLAYER);
				return;
			}
			if(!target.getTeam().isLeader(target))
			{
				target = target.getTeam().getLeader();
			}
		}
		if(team != null)
		{
			if(!team.isLeader(this))
			{
				sendAlert(ErrorCode.ALERT_PLAYER_NOT_LEADER);
				return;
			}
		}
		if(checkPlayerState())
			return;
		if(isChoose(target,0))
			return;
			
		String pvpName = getName()+"-PVPInvitation-"+target.getName();
		
		ConfirmJob cj = worldManager.getConfirmation(pvpName);
		if(cj != null)
		{
			sendAlert(ErrorCode.ALERT_PLAYER_SAME_INVITATION);
			return;
		}
		
		cj = new PVPInvitation(this,target);
		cj.setName(pvpName);
		
		if(worldManager.getConfirmation(cj.getName()) == null)
		{
			worldManager.addConfirmJob(cj);
		}
		ByteBuffer buffer = new ByteBuffer(8);
		buffer.writeInt(getID());
		buffer.writeUTF(getName());
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_PLAYER_REQUEST_PK_COMMAND,buffer));
		
	}
	
	/**
	 * 玩家回复PK邀请
	 * @param msg
	 */
	private void responsePK(AppMessage msg)
	{
		if(roomController.isPartyRoom() || roomController.isPartyPKRoom() || roomController.isCopyPartyRoom)
		{
			return;
		}
		if(roomController.isGoldPartyRoom || roomController.isGoldPartyPKRoom)
			return;
		int inviterId = msg.getBuffer().readInt();//邀请者的ID
		boolean accept = msg.getBuffer().readBoolean();//是否同意PK
		if(getID() == inviterId)
			return;
		if(getParent() instanceof BattleController)
		{
			if(accept)
				sendAlert(ErrorCode.ALERT_PLAYER_IS_BATTLING);
			return;
		}
		if(getParent() instanceof BusinessController)
		{
			if(accept)
				sendAlert(ErrorCode.ALERT_BUSINESS_ING_PLAYER);
			return;
		}
		if(!(getParent() instanceof RoomController))
		{
			if(accept)
				sendAlert(ErrorCode.ALERT_PLAYER_ISNOT_ROOM);
			return;
		}
		RoomController room = (RoomController) getParent();
		PlayerController inviter = room.getPlayer(inviterId);
		if(inviter == null)
		{
			if(accept)
				sendAlert(ErrorCode.EXCEPTION_LOGIN_PLAYERNOTATGAME);
			return;
		}
		if(inviter.isAuto)
		{
			sendAlert(ErrorCode.ALERT_TARGET_PLAYER_AUTO_BATTLEING);
			return;
		}
		if(inviter.isStory)
		{
			sendGetGoodsInfo(1, false, DC.getString(DC.PARTY_51));
			return;
		}
		if(inviter.getParent() instanceof BattleController)
		{
			if(accept)
				sendAlert(ErrorCode.ALERT_PLAYER_IS_BATTLING);
			return;
		}
		if(inviter.getParent() instanceof BusinessController || getParent() instanceof BusinessController)
		{
			if(accept)
				sendAlert(ErrorCode.ALERT_BUSINESS_ING_PLAYER);
			return;
		}
		if(inviter.getTeam() != null && team != null && team.isTeamPlayer(inviter))
		{
			if(accept)
				sendAlert(ErrorCode.ALERT_SAME_TEAM_PLAYER);
			return;
		}
		if(checkPlayerState())
			return;
		if(isChoose(inviter,0))
			return;
		if(inviter.getRoom().id != getRoom().id)
		{
			if(accept)
				sendAlert(ErrorCode.ALERT_PLAYER_NOTAT_ROOM);
			return;
		}
		String inviteName = inviter.getName() + "-PVPInvitation-" + getName();
		Object obj = worldManager.getConfirmation(inviteName);
		if(obj == null)
		{
			if(accept)
				sendAlert(ErrorCode.ALERT_PVP_INVITE_CANCEL);
			return;
		}
		if(!(obj instanceof PVPInvitation))
		{
			if(accept)
				sendAlert(ErrorCode.ALERT_INVITE_CLASS_ERROR);
			return;
		}
		PVPInvitation pk = (PVPInvitation) obj;
		
		pk.confirm(accept);
		pk.setDefaultLifeTime();
	}
	
	
	/**
	 * 玩家向另一个玩家发出组队请求
	 * @param target
	 * @param msg
	 */
	public void requestTeam(AppMessage msg)
	{
		String playerName = msg.getBuffer().readUTF();//要邀请谁加入 队伍
		
		if(roomController.isCopyPartyRoom)
		{
			if(roomController.getCopy() != null && !roomController.getCopy().isTeam)
				return;
		}
		if(roomController.isGoldPartyRoom || roomController.isGoldPartyPKRoom)
			return;
		if(team != null && !team.isLeader(this))
		{
			sendAlert(ErrorCode.ALERT_PLAYER_INVITE_ERROR);
			return;
		}
		if(team != null && team.getPlayerCount() > 4 )
		{
			sendAlert(ErrorCode.ALERT_TEAM_PLAYERS_TOO_MUCH);
			return;
		}
		if(!(getParent() instanceof RoomController))
		{
			sendAlert(ErrorCode.ALERT_PLAYER_ISNOT_ROOM);
			return;
		}
		RoomController room = (RoomController) getParent();
		PlayerController target = room.getPlayer(playerName);
		if(getName().equals(playerName))
			return;
		if(target == null)
		{
			sendAlert(ErrorCode.ALERT_PLAYER_NOTAT_ROOM);
			return;
		}
		if(target.isAuto)
		{
			sendAlert(ErrorCode.ALERT_TARGET_PLAYER_AUTO_BATTLEING);
			return;
		}
		if(target.isStory)
		{
			sendGetGoodsInfo(1, false, DC.getString(DC.PARTY_51));
			return;
		}
		Object obj = target.getParent();
		if(!(obj instanceof RoomController))
		{
			if(obj instanceof BattleController)
			{
				sendAlert(ErrorCode.ALERT_PLAYER_IS_BATTLING);
				return;
			}
			else if(obj instanceof BusinessController)
			{
				sendAlert(ErrorCode.ALERT_BUSINESS_ING_PLAYER);
				return;
			}
		} 
		
		if(obj == null)
		{
			sendAlert(ErrorCode.ALERT_PLAYER_NOTAT_ROOM);
			return;
		}
		
		if(target.getRoom().id != getRoom().id)
		{
			sendAlert(ErrorCode.ALERT_PLAYER_NOTAT_ROOM);
			return;
		}
		if(target.getTeam() != null)
		{
			sendAlert(ErrorCode.ALERT_PLAYER_IN_TEAM);
			return;
		}
		if(target.isChoose(target,1))
			return;
		if(roomController.isPartyRoom() || roomController.isPartyPKRoom())
		{
			if(FamilyPartyController.getInstance().isReady())
			{
				if(player.familyId != target.getPlayer().familyId)
				{
					sendAlert(ErrorCode.ALERT_DIFF_FAMILY_ERROR);
					return;
				}
			}
		}
		
		if(target.teamState == 1)
		{
			sendAlert(ErrorCode.ALERT_PLAYER_REFUSE_TEAM);
			return;
		}
		else if(target.teamState == 2)
		{
			FriendList fl = (FriendList) target.getPlayer().getExtPlayerInfo("friendList");
			if(fl.isInBlack(getName()))
				return;
			autoAddTeam(target);
		}
		else if(target.teamState == 0)
		{
			String teamName = getName()+"-teamInvitation-"+target.getName();
			
			ConfirmJob cj = worldManager.getConfirmation(teamName);
			if(cj != null)
			{
				sendAlert(ErrorCode.ALERT_PLAYER_SAME_INVITATION);
				return;
			}
			
			cj = new TeamInvitation(this,target);
			cj.setName(teamName);
			
			if(worldManager.getConfirmation(cj.getName()) == null)
			{
				worldManager.addConfirmJob(cj);
			}
			ByteBuffer buffer = new ByteBuffer();
			buffer.writeInt(getID());
			buffer.writeUTF(getName());
			buffer.writeInt(player.level);
			buffer.writeByte(player.upProfession);
			target.getNetConnection().sendMessage(new SMsg(SMsg.S_PLAYER_REQUEST_TEAM_COMMAND,buffer));
		}
	}
	
	public void autoAddTeam(PlayerController target)
	{
		if(team == null)//如果邀请方的队伍中已经创建
		{
			team = new TeamController(getID());
			team.setLeader(this);//设置邀请方为队长
			player.state = Role.STATE_TEAMLEADER;//设置状态为带队
			team.addPlayer(this);//把队长加入队伍
			roomController.addTeam(team);
		}
		if(team.getPlayer(target.getPlayer().id) != null)
		{
			sendAlert(ErrorCode.ALERT_PLAYER_IN_TEAM);
			return;
		}
		target.getPlayer().state = Role.STATE_TEAMMEMBER;//设置被邀方状态为队员
		team.addPlayer(target);//把队员加入队伍
		
		ByteBuffer buffer = new ByteBuffer();
		buffer.writeUTF(target.getName());
		buffer.writeBoolean(true);//同意加入队伍
		team.writeTo(buffer);
		team.dispatchMsg(SMsg.S_ADD_TEAM_COMMAND,buffer);//转发给队伍中所有队员
		
		team.sendActivePointTo();
	}
	
	private void responseTeam(AppMessage msg)
	{
		int inviterId = msg.getBuffer().readInt();//要加入谁的队伍
		boolean accept = msg.getBuffer().readBoolean();//是否同意加入队伍
		responseTeam(inviterId,accept);
	}
	
	/**
	 * 回复组队请求
	 * @param msg
	 * @param room
	 */
	public void responseTeam(int inviterId,boolean accept)
	{
		if(roomController.isCopyPartyRoom)
		{
			if(roomController.getCopy() != null && !roomController.getCopy().isTeam)
				return;
		}
		if(roomController.isGoldPartyRoom || roomController.isGoldPartyPKRoom)
			return;
		if(getID() == inviterId)
			return;
		if(!(getParent() instanceof RoomController))
		{
			if(accept)
				sendAlert(ErrorCode.ALERT_PLAYER_NOTAT_ROOM);
			return;
		}
		RoomController room = (RoomController) getParent();
		PlayerController inviter = room.getPlayer(inviterId);
		if(inviter == null)
		{
			if(accept)
				sendAlert(ErrorCode.ALERT_PLAYER_NOTAT_ROOM);
			return;
		}
		if(inviter.isAuto)
		{
			sendAlert(ErrorCode.ALERT_TARGET_PLAYER_AUTO_BATTLEING);
			return;
		}
		if(inviter.isStory)
		{
			sendGetGoodsInfo(1, false, DC.getString(DC.PARTY_51));
			return;
		}
		if(inviter.getParent() instanceof BattleController)
		{
			if(accept)
				sendAlert(ErrorCode.ALERT_PLAYER_IS_BATTLING);
			return;
		}
		if(inviter.getRoom().id != getRoom().id)
		{
			if(accept)
				sendAlert(ErrorCode.ALERT_PLAYER_NOTAT_ROOM);
			return;
		}
		if(inviter.getTeam() != null && inviter.getTeam().getPlayer(player.id) != null)
		{
			if(accept)
				sendAlert(ErrorCode.ALERT_PLAYER_IN_TEAM);
			return;
		}
		if(inviter.getTeam() != null && inviter.getTeam().getPlayerCount() > 4)
		{
			if(accept)
				sendAlert(ErrorCode.ALERT_TEAM_PLAYERS_TOO_MUCH);
			return;
		}
		if(team != null)
		{
			if(accept)
				sendAlert(ErrorCode.ALERT_PLAYER_IN_TEAM);
			return;
		}
		if(isChoose(inviter,1))
			return;
		
		if(roomController.isPartyPKRoom() || roomController.isPartyRoom())
		{
			if(FamilyPartyController.getInstance().isReady())
			{
				if(player.familyId != inviter.getPlayer().familyId)
				{
					if(accept)
						sendAlert(ErrorCode.ALERT_DIFF_FAMILY_ERROR);
					return;
				}
			}
		}
		
		
		String inviteName = inviter.getName() + "-teamInvitation-" + getName();
		Object obj = worldManager.getConfirmation(inviteName);
		if(obj == null)
		{
			if(accept)
				sendAlert(ErrorCode.ALERT_TEAM_INVITE_CANCEL);
			return;
		}
		if(!(obj instanceof TeamInvitation))
		{
			if(accept)
				sendAlert(ErrorCode.EXCEPTION_CLASS_ERROR);
			return;
		}
		TeamInvitation ti = (TeamInvitation) worldManager.getConfirmation(inviteName);
		ByteBuffer buffer = new ByteBuffer();
		buffer.writeUTF(getName());//被邀方
		if(!accept)	
		{	
			inviter.sendGetGoodsInfo(1,false, getName()+DC.getString(DC.PLAYER_16));
		}
		else
		{
			TeamController team = ti.confirm(accept);
			if(team == null)
				return;
			buffer.writeBoolean(true);//同意加入队伍
			team.writeTo(buffer);
			team.dispatchMsg(SMsg.S_ADD_TEAM_COMMAND,buffer);//转发给队伍中所有队员
			
			team.sendActivePointTo();
		}
		ti.setDefaultLifeTime();
	}
	
	private void familyChange(ByteBuffer buffer)
	{
String name = buffer.readUTF();
		
		if(family == null)
			return;
		
		if(family.leaderId != getID())
			return;
		
		if(!family.isChange())
		{
			sendAlert(ErrorCode.ALERT_CHANGE_FAMILY_LEADER_REPEAT);
			return;
		}
		
		if(!family.isInFamily(name))
		{
			sendAlert(ErrorCode.EXCEPTION_PLAYER_CHANGENOT);
			return;
		}
		
		PlayerController target = worldManager.getPlayerController(name);
		
		if(target == null)
		{
			sendAlert(ErrorCode.EXCEPTION_PLAYER_OFFLINE);
			return;
		}
		
		if(target.getPlayer().level < FamilyController.LEVEL)
		{
			//目标玩家等级不够
			return;
		}
		
		family.setLeader(target);
		target.getPlayer().isFamilyLeader = true;
		player.isFamilyLeader = false;
		
		buffer =new ByteBuffer(1);
		buffer.writeBoolean(false);
		netConnection.sendMessage(new SMsg
				(SMsg.S_PLAYER_FAMILYLEADERCHANGE_COMMAND,buffer));
		buffer =new ByteBuffer(1);
		buffer.writeBoolean(true);
		target.getNetConnection().sendMessage(new SMsg
				(SMsg.S_PLAYER_FAMILYLEADERCHANGE_COMMAND,buffer));
		
		
		
		int count = family.getPlayerCount();
		for (int i = 0; i < count; i++)
		{
			PlayerController everyone = family.getPlayerByIndex(i);
			
			if(everyone == null)
				continue;
			//发送邮件通知转让族长
			Mail mail = new Mail(family.name+DC.getString(DC.PLAYER_17));//family.name+"家族"
			mail.setTitle("*"+DC.getString(DC.PLAYER_18));//族长变更信息
			mail.setContent(DC.getFamilyLeaderString(getName(), family.leaderName));
			
			mail.send(everyone);
		}
		
		
		target.getWorldManager().getJobObserver().addJob(GameServer.JOB_DATABASEJOB,
				new SaveJob(target.getWorldManager(),target.getPlayer(),SaveJob.FAMILY_CHANGE_SAVE));
		
		getWorldManager().getJobObserver().addJob(GameServer.JOB_DATABASEJOB,
				new SaveJob(getWorldManager(),getPlayer(),SaveJob.FAMILY_CHANGE_SAVE));
		
		/**
		 * 转让家族，更新家族列表
		 */
		GameServer.getInstance().getDatabaseAccessor().createOrUpdateFamilyInfo(family, false);
	
		family.changeLeaderTime = (int) (WorldManager.currentTime/1000);
		target.close();
		close();
	}
	


	public void familyOut()
	{
		if(family == null)
			return;
		
		if(family.leaderId == getID())
		{
			familyRmove();
			return;
		}
		
		player.familyId = 0;
		player.familyName = "";
		family.removePlayer(this);
		family.removeNameToFamily(getName());
		
		ByteBuffer buffer = new ByteBuffer(10);
		buffer.writeUTF(getName());
		netConnection.sendMessage
		(new SMsg(SMsg.S_PLAYER_FAMILYOUT_COMMAND,
				new ByteBuffer(buffer.getBytes())));
		
		PlayerController leader = family.getLeader();
		if(leader != null)
		{
			leader.getNetConnection().sendMessage
			(new SMsg(SMsg.S_PLAYER_FAMILYOUT_COMMAND,buffer));
		}
		
		
		
		/**
		 * 玩家退出家族，更新家族列表
		 */
		GameServer.getInstance().getDatabaseAccessor().createOrUpdateFamilyInfo(family, false);
		
		getWorldManager().getJobObserver().addJob(GameServer.JOB_DATABASEJOB,
				new SaveJob(getWorldManager(),getPlayer(),SaveJob.FAMILY_OUT_SAVE));
		
		setFamilyController(null);
	}

	private void familyRmove()
	{
		if(family == null)
			return;
		
		if(family.leaderId != getID())
			return;
		
		int count = family.getPlayerCount();
		for (int i = 0; i < count; i++)
		{
			PlayerController everyone = family.getPlayerByIndex(i);
			
			if(everyone == null)
				continue;
			
			if(everyone.getID() != getID())
				everyone.setFamilyController(null);
			
			everyone.getPlayer().familyId = 0;
			everyone.getPlayer().familyName = "";
			everyone.getPlayer().isFamilyLeader = false;
			everyone.getNetConnection().sendMessage
			(new SMsg(SMsg.S_PLAYER_FAMILYREMOVE_COMMAND,new ByteBuffer(1)));
			
			Mail mail = new Mail(family.name+DC.getString(DC.PLAYER_17));//family.name+"家族"
			mail.setTitle(DC.getString(DC.PLAYER_24));//家族解散信息
			mail.setContent(DC.getString(DC.PLAYER_25).replace("XX", family.leaderName));
			mail.send(everyone);
			
			
			everyone.getWorldManager().getJobObserver().addJob(GameServer.JOB_DATABASEJOB,
					new SaveJob(everyone.getWorldManager(),everyone.getPlayer(),SaveJob.FAMILY_REMOVE_SAVE));
		}
		
		worldManager.removeFamily(family);
		
		
		/**
		 * 玩家删除，更新家族列表
		 * 
		 */
		family.state = -1;
		GameServer.getInstance().getDatabaseAccessor().createOrUpdateFamilyInfo(family, false);
		
		
		setFamilyController(null);
	}

	private void familyKick(ByteBuffer buffer)
	{
		String name = buffer.readUTF();
		
		if(family == null)
			return;
		
		if(family.leaderId != getID())
			return;
		
		int index = family.getPlayerIndexByFamily(name);
		
		if(index != -1)
		{
			family.removeNameToFamily(index);
		
			PlayerController target= worldManager.getPlayerController(name);

			if(target != null)
			{
				target.getPlayer().familyId = 0;
				target.getPlayer().familyName = "";
				target.setFamilyController(null);
				
				buffer = new ByteBuffer(10);
				buffer.writeUTF(name);
				target.getNetConnection().sendMessage
				(new SMsg(SMsg.S_PLAYER_FAMILYKICK_COMMAND,buffer));
				
				
				target.getWorldManager().getJobObserver().addJob(GameServer.JOB_DATABASEJOB,
						new SaveJob(target.getWorldManager(),target.getPlayer(),SaveJob.FAMILY_KICK_SAVE));
			}
			buffer = new ByteBuffer(10);
			buffer.writeUTF(name);
			netConnection.sendMessage(new SMsg(SMsg.S_PLAYER_FAMILYKICK_COMMAND,buffer));
		}
		
		/**
		 * 玩家T出家族，更新家族列表
		 */
		GameServer.getInstance().getDatabaseAccessor().createOrUpdateFamilyInfo(family, false);
		
	}

	private void familyList()
	{
		if(family == null)
			return;
		
		ByteBuffer buffer = new ByteBuffer(32);
		family.writeTo(buffer);
		netConnection.sendMessage(new SMsg(SMsg.S_PLAYER_FAMILYLIST_COMMAND,buffer));
	}

	private void familyResponse(ByteBuffer buffer)
	{
		String confirmationName = buffer.readUTF();
		boolean result = buffer.readBoolean();

		FamilyInvitation fi = (FamilyInvitation)worldManager.getConfirmation(confirmationName);
	    
		if(fi == null)
	    {
			if(result)
				sendAlert(ErrorCode.ALERT_PLAYER_CANCEL);
	    	return;
	    }

	    fi.confirm(result);		
	    fi.setLifeTime(-0xffff);
	}

	private void familyRequest(ByteBuffer buffer)
	{
		PlayerContainer container =  getParent();
		
		if(!(container instanceof RoomController))
			return;

		if(family == null)
		{
			sendAlert(ErrorCode.EXCEPTION_PLAYER_NOHAS_FAMILY);
			return;
		}
		
		if(family.leaderId != getID())
			return;
		
		if(family.isFull())
		{
			sendAlert(ErrorCode.ALERT_FAMILY_ISFULL);
			return;
		}
		
		
		String playerName = buffer.readUTF();
		
		
		PlayerController targetPlayer = worldManager.getPlayer(playerName);//room.getPlayer(id);
		
		if(targetPlayer == null)
		{
			sendAlert(ErrorCode.EXCEPTION_PLAYER_OFFLINE);
			return;
		}
		
		if(targetPlayer.isAuto)
		{
			sendAlert(ErrorCode.ALERT_TARGET_PLAYER_AUTO_BATTLEING);
			return;
		}
		
		if(targetPlayer.getPlayer().familyId != 0)
		{
			sendAlert(ErrorCode.EXCEPTION_PLAYER_HAS_FAMILY);
			return;
		}
		
		if(targetPlayer.getPlayer().camp != family.camp)
		{
			if(targetPlayer.getPlayer().camp != 0)
			{
				sendAlert(ErrorCode.ALERT_CAMP_FAMILY_NOT_SAME);
				return;
			}
		}
		
		if(family.isInFamily(targetPlayer.getName()))
		{
			sendAlert(ErrorCode.EXCEPTION_PLAYER_ISIN_FAMILY);
			return;
		}

		String confirmationName = getName() + "-familyInvite-"
				+ targetPlayer.getName();

		ConfirmJob cj = worldManager.getConfirmation(confirmationName);
		if(cj != null)
		{
			sendAlert(ErrorCode.ALERT_PLAYER_SAME_INVITATION);
			return;
		}
		
		sendAlert(ErrorCode.ALERT_PLAYER_INVITATION_SENDED);
		
		buffer = new ByteBuffer(24);
		buffer.writeUTF(confirmationName);
		buffer.writeUTF(getName());
		targetPlayer.getNetConnection().sendMessage(new SMsg(SMsg.S_PLAYER_FAMILYREQUEST_COMMAND,buffer));
		
		cj = new FamilyInvitation(this,targetPlayer);
		cj.setName(confirmationName);
		
		worldManager.addConfirmJob(cj);
	}

	private void friendProcess(ByteBuffer buffer)
	{
		int type = buffer.readByte();
		//type 1addFriend 2removeFriend 3addFriend 4removeFriend 
		
		String name= buffer.readUTF();
		FriendList friendList = (FriendList)player.getExtPlayerInfo("friendList");
		
		if(type == FriendList.ADD_FRIEND)
		{
			if(worldManager.getPlayerController(name) == null)
			{
				sendAlert(ErrorCode.EXCEPTION_PLAYER_OFFLINE);
				return;
			}
			if(friendList.isInFriends(name))
			{
				return;
			}
			if(friendList.isInBlack(name))
			{
				return;
			}
			
			if(friendList.addFriend(name))
			{
				updateFriendList(type,name);
			}
			else
			{
				sendAlert(ErrorCode.ALERT_FRIENDLIST_ISFULL);
			}
		}
		else if(type == FriendList.REMOVE_FRIEND)
		{
			if(!friendList.isInFriends(name))
			{
				sendAlert(ErrorCode.ALERT_PLAYER_NOT_FRIENDS);
				return;
			}
			friendList.removeFromFriends(name);
			updateFriendList(type,name);
		}
		else if(type == FriendList.ADD_BLACK)
		{
			if(worldManager.getPlayerController(name) == null)
			{
				sendAlert(ErrorCode.EXCEPTION_PLAYER_OFFLINE);
				return;
			}
			if(friendList.isInBlack(name))
			{
				sendAlert(ErrorCode.ALERT_PLAYER_ISIN_BLACK);
				return;
			}
			
			if(friendList.addBlack(name))
			{
				if(friendList.isInFriends(name))
				{
					friendList.removeFromFriends(name);
				}
				updateFriendList(type,name);
			}
			else
			{
				sendAlert(ErrorCode.ALERT_FRIENDLIST_ISFULL);
			}
		}
		else if(type == FriendList.REMOVE_BLACK)
		{
			if(!friendList.isInBlack(name))
			{
				sendAlert(ErrorCode.ALERT_PLAYER_NOTIN_BLACK);
				return;
			}
			friendList.removeFromBlack(name);
			updateFriendList(type,name);
		}
		else if(type == FriendList.LIST)
		{
			updateFriendList(type,name);
		}
	}
	/**
	 * 更新好友列表
	 * @param type
	 */
	private void updateFriendList(int type,String name)
	{
		FriendList friendList = (FriendList)player.getExtPlayerInfo("friendList");
		ByteBuffer buffer = new ByteBuffer(24);
		buffer.writeByte(type);
		
		if(type == FriendList.LIST)
			friendList.writeTo(buffer, worldManager);
		else
			buffer.writeUTF(name);
		
		netConnection.sendMessage(new SMsg(SMsg.S_PLAYER_FRIENDLIST_COMMAND,buffer));
	}

	
	public long chatTime = 0;
	/**
	 * 聊天
	 * 聊天类型 0私聊  1当前房间所有玩家   2当前区域所有玩家  3当前世界所有玩家 4当前队列所有玩家 5当前家族所有玩家6帮会聊天7系统提示信息 ，得到经验，物品8公告9阵营聊天
	 * @param buffer
	 */
	public void processChat(ByteBuffer buffer)
	{
		if(buffer == null)
			return;
		// 聊天类型 0私聊  1当前房间所有玩家   2当前区域所有玩家  3当前世界所有玩家 4当前队列所有玩家 5当前家族所有玩家6帮会聊天7系统提示信息 ，得到经验，物品8公告9阵营聊天
		if(player == null || netConnection == null)
			return;
		if(!player.isChat)
		{
			sendGetGoodsInfo(1,false,DC.getString(DC.PLAYER_28));
			return;
		}
		
		long time = netConnection.getPingTime();
		
		if(time < 2000 + chatTime)
		{
			return;
		}
		chatTime = time;
		
		int chatType = buffer.readByte();
		String chatMsg = "";
		try{
			chatMsg = buffer.readUTF();
		}catch(Exception e)
		{System.out.println("PlayerController processChat acceptMsg error:"+buffer.available());}
		
		if(!isGmAccount())
		{
			Bag bag = (Bag) player.getExtPlayerInfo("bag");
			if(bag == null)
			{
				System.out.println("PlayerController processChat bag is null:"+player.name);
				return;
			}
			if(chatType == 3 || chatType == 8)
			{//当玩家发送特定范围消息时，如在世界发送消息或者在区域发送消息，要检测玩家是否用了有那种功能的喇叭，否则提示玩家不能发送
				GoodsProp prop = bag.getSpeaker(chatType);
				if(prop == null)
				{
					int msg = 0;
					if(chatType == 3)
						msg = ErrorCode.ALERT_NO_BIG_SPEAKER;
					else if(chatType == 8)
						msg = ErrorCode.ALERT_FUNCTION_NO_OPEN;
					sendAlert(msg);
					return;
				}
				else
				{
					if(player.level < prop.level)
					{
						sendAlert(ErrorCode.ALERT_PLAYER_LEVEL_SPEAKER);
						return;
					}
				}
				bag.removeGoods(this, prop.objectIndex, 1);
			}
		}
		
		if(gmCmdProcess(chatMsg))
			return;
		
//		chatMsg = MarkChar.replace(chatMsg);
		
		if(chatType == 0)
		{
			String name = "";
			try{
				name = buffer.readUTF();
			}catch(Exception e)
			{System.out.println("PlayerController processChat acceptName error:"+buffer.available());}
			
			PlayerController targetPlayer = null;
			
			if(name.equals(lastCharTargetName))
			{
				targetPlayer = lastCharTarget;
			}
			else
			{
				/*				
				targetPlayer = getTeam()==null?
						null:getTeam().getPlayer(id);
				if(targetPlayer == null)
					targetPlayer = getRoom().getPlayer(id);
					if(targetPlayer == null)
						targetPlayer = getArea().getPlayer(id);
						if(targetPlayer == null)
							targetPlayer = getWorldManager().getPlayer(id);
							if(targetPlayer == null)
								return;
				*/		
				targetPlayer = getTeam()==null?
						null:getTeam().getPlayer(name);
				
				if(targetPlayer == null)
					targetPlayer = getRoom()==null?null:getRoom().getPlayer(name);
					if(targetPlayer == null)
						targetPlayer = getArea()==null?null:getArea().getPlayer(name);
						if(targetPlayer == null)
							targetPlayer = getWorldManager()==null?null:getWorldManager().getPlayer(name);
							if(targetPlayer == null)
								return;
				
				
				lastCharTarget = targetPlayer;
				lastCharTargetName = targetPlayer.getName();
			}
			
			if(targetPlayer == null)
				return;
				
			buffer = new ByteBuffer(32);
			buffer.writeByte(chatType);
			buffer.writeInt(getID());
			buffer.writeUTF(getName());
			buffer.writeUTF(targetPlayer.getName());
			buffer.writeUTF(chatMsg);
			targetPlayer.getNetConnection()
			.sendMessage(new SMsg(SMsg.S_CHAT_COMMAND, new ByteBuffer(buffer.getBytes())));
			getNetConnection()
			.sendMessage(new SMsg(SMsg.S_CHAT_COMMAND, buffer));
			
			
			PressureTest.getInstance().putChatText(getName()+" to "+targetPlayer.getName()+" : "+chatMsg+"\n\r");
			
			return;
		}

		PlayerContainer container = null;
		
		if(chatType == 1)
		{
			container = getRoom();
		}
		else if(chatType ==2)
		{
			container = getArea();
/*			List list = getArea().getSmaillWorld();
			int size = list.size();
			for (int i = 0; i < size; i++)
			{
				AreaController area = (AreaController)list.get(i);
				buffer = new ByteBuffer(24);
				buffer.writeByte(chatType);
				buffer.writeInt(getID());
				buffer.writeUTF(getName());
				buffer.writeUTF(chatMsg);
				area.dispatchMsg(SMsg.S_CHAT_COMMAND, buffer);
			}
			return;*/
		}
		else if(chatType ==3)
		{
			container = worldManager;
		}
		else if(chatType ==4)
		{
			container = getTeam();
		}
		else if(chatType ==5)
		{
			container = getFamily();
		}
		else if(chatType ==8)
		{
			container = worldManager;
		}
		else if(chatType ==9)
		{
			List list = worldManager.getPlayerList();
			int camp = player.camp;
			
			buffer = new ByteBuffer(24);
			buffer.writeByte(chatType);
			buffer.writeInt(getID());
			buffer.writeUTF(getName());
			buffer.writeUTF(chatMsg);
			
			int size = list.size();
			for (int i = 0; i < size; i++)
			{
				PlayerController everyone = (PlayerController)list.get(i);
				
				if(everyone == null)
					continue;
				
				if(camp != everyone.getPlayer().camp)
					continue;
				
				everyone.getNetConnection().sendMessage(new SMsg(SMsg.S_CHAT_COMMAND,
						new ByteBuffer(buffer.getBytes())));
			}
			return;
		}
		
		if(container == null)
			return;
		
		buffer = new ByteBuffer(24);
		buffer.writeByte(chatType);
		buffer.writeInt(getID());
		buffer.writeUTF(getName());
		buffer.writeUTF(chatMsg);
		container.dispatchMsg(SMsg.S_CHAT_COMMAND, buffer);
		
	}

	
	private boolean gmCmdProcess(String cmd)
	{

		String [] cmds = cmd.split(":");

		boolean result = isGmAccount();
		
		if(!result)
			return false;
		
		if(!isManager())
			return false;

		if(cmds == null || cmds.length <= 1)
			return false;
		
		
/*		if(getParent() instanceof BattleController)
		{
			return false;
		}*/
		result = false;
		
		if(cmds[0].equals("addExp"))
		{
			try
			{
				long expPoint = Long.parseLong(cmds[1]);
				addExp(expPoint,false,false,false);

				result =  true;
			}
			catch(NumberFormatException e)
			{
				result =  false;
			}
		}
		else if(cmds[0].equals("qmdny"))
		{
			try
			{
				if(cmds[1].equals("all"))
				{
					AreaController[] acs = worldManager.getAreaControllers();
					for (int i = 0; i < acs.length; i++) 
					{
						RoomController[] rcs = acs[i].getRooms();
						for (int j = 0; j < rcs.length; j++) 
						{
							MonsterGroupController[] mgcs = rcs[j].getMonsterGroups();
							for (int k = 0; k < mgcs.length; k++) 
							{
								if(mgcs[k].rebirthTime > 0)
								{
									mgcs[k].vTimer = 0;
									mgcs[k].setFight(false);
									mgcs[k].notifyVisible(false);
								}
							}	
						}
					}
				}
				else if(cmds[1].equals("this"))
				{ 
					MonsterGroupController[] mgcs = roomController.getMonsterGroups();
					for (int k = 0; k < mgcs.length; k++) 
					{
						if(mgcs[k].rebirthTime > 0)
						{
							mgcs[k].vTimer = 0;
							mgcs[k].setFight(false);
							mgcs[k].notifyVisible(false);
						}
					}	
				}
				
				
				result =  true;
			}
			catch(NumberFormatException e)
			{
				result =  false;
			}
		}
		else if(cmds[0].equals("addAam"))
		{
			try
			{
				int point = Integer.parseInt(cmds[1]);
				OtherExtInfo oei = (OtherExtInfo) player.getExtPlayerInfo("otherExtInfo");
				oei.aamCount += point;
				
				result =  true;
			}
			catch(NumberFormatException e)
			{
				result =  false;
			}
		}
		else if(cmds[0].equals("clearCopy"))
		{
			try
			{
				OtherExtInfo oei = (OtherExtInfo) player.getExtPlayerInfo("otherExtInfo");
				oei.clearAss();
				
				result =  true;
			}
			catch(NumberFormatException e)
			{
				result =  false;
			}
		}
		else if(cmds[0].equals("clearStory"))
		{
			try
			{
				StoryEvent se = (StoryEvent) player.getExtPlayerInfo("storyEvent");
				int team = Integer.parseInt(cmds[1]);
				se.clearInit(this,team);
				
				result =  true;
			}
			catch(NumberFormatException e)
			{
				result =  false;
			}
		}
		else if(cmds[0].equals("finishEvent"))
		{
			try
			{
				StoryEvent se = (StoryEvent) player.getExtPlayerInfo("storyEvent");
				int id = Integer.parseInt(cmds[1]);
				Story story = se.getStoryById(id);
				if(story != null && story.state == StoryEvent.ACTIVE)
				{
					for (int j = 0; j < story.getEvents().size(); j++) 
					{
						Event event = (Event) story.getEvents().get(j);
						if(event.state == StoryEvent.ACTIVE)
						{
							se.finishEvent(this, event);
							break;
						}
					}
					result = true;
				}
				else
					result =  false;
			}
			catch(NumberFormatException e)
			{
				result =  false;
			}
		}
		else if(cmds[0].equals("addEvent"))
		{
			try
			{
				player.timeCopyActivePoint += Integer.parseInt(cmds[1]);
				player.bossActivePoint += Integer.parseInt(cmds[1]);
				player.flyActivePoint += Integer.parseInt(cmds[1]);
				if(player.timeCopyActivePoint > Player.TIMECOPYACTIVEPOINTDEFAULT)
					player.timeCopyActivePoint = Player.TIMECOPYACTIVEPOINTDEFAULT;
				if(player.bossActivePoint > Player.BOSSACTIVEPOINTDEFAULT)
					player.bossActivePoint = Player.BOSSACTIVEPOINTDEFAULT;
				if(player.flyActivePoint > Player.FLYACTIVEPOINTDEFAULT)
					player.flyActivePoint = Player.FLYACTIVEPOINTDEFAULT;
				sendActivePoint();
				result =  true;
			}
			catch(NumberFormatException e)
			{
				result =  false;
			}
		}
		else if(cmds[0].equals("addMoney"))
		{
			try
			{
				int money = Integer.parseInt(cmds[1]);	
				Bag bag = (Bag)player.getExtPlayerInfo("bag");
				bag.addMoney(this, money, money);
				
				result =  true;
			}
			catch(NumberFormatException e)
			{
				result =  false;
			}
		}
		else if(cmds[0].equals("addPoint"))
		{
			try
			{
				int money = Integer.parseInt(cmds[1]);	
				Bag bag = (Bag)player.getExtPlayerInfo("bag");
				bag.addMoney(this, money, 0);
				
				result =  true;
			}
			catch(NumberFormatException e)
			{
				result =  false;
			}
		}
		else if(cmds[0].equals("initHuangjin"))
		{
			try
			{
				if(GoldPartyController.getInstance().isStarted)
					return true;
				GoldPartyController.getInstance().cancelImageShow();
				GoldPartyController.getInstance().isReady = true;
				GoldPartyController.getInstance().init();
				GoldPartyController.getInstance().isStarted = true; 
				GoldPartyController.getInstance().isEnded = false;
				int time = Integer.parseInt(cmds[1]);	
				GoldPartyController.getInstance().partyTime = time * 60 * 1000;
				GoldPartyController.getInstance().lessBeginTime = 0;
				GameServer.getInstance().getWorldManager().sendEveryonePost("gold party start!!!!!!!!time:"+time);
				result =  true;
			}
			catch(NumberFormatException e)
			{
				result =  false;
			}
		}
		else if(cmds[0].equals("initJiazu"))
		{
			try
			{
				if(FamilyPartyController.getInstance().isStarted)
					return true;
				FamilyPartyController.getInstance().isReady = true;
				FamilyPartyController.getInstance().init();
				FamilyPartyController.getInstance().isStarted = true;
				FamilyPartyController.getInstance().isEnded = false;
				int time = Integer.parseInt(cmds[1]);	
				FamilyPartyController.getInstance().partyTime = time * 60 * 1000;
				FamilyPartyController.getInstance().lessBeginTime = 0;
				GameServer.getInstance().getWorldManager().sendEveryonePost("family party start!!!!!!!!time:"+time);
				result =  true;
			}
			catch(NumberFormatException e)
			{
				result =  false;
			}
		}
		else if(cmds[0].equals("addPartyPoint"))
		{
			try
			{
				FamilyPartyController.getInstance().addPlayer(this);
				int point = Integer.parseInt(cmds[1]);	
				FamilyPartyController.getInstance().addPoint(this, point);
				
				result =  true;
			}
			catch(NumberFormatException e)
			{
				result =  false;
			}
		}
		else if(cmds[0].equals("clearBag"))
		{
			Bag bag = (Bag)player.getExtPlayerInfo("bag");
			
			bag.clear(this);
			
			result = true;
		}
		else if(cmds[0].equals("addGoods"))
		{
			try
			{
				int goodsId = Integer.parseInt(cmds[1]);	
				Bag bag = (Bag)player.getExtPlayerInfo("bag");
				
				if(cmds.length > 3)
				{
					int num = Integer.parseInt(cmds[3]);
					for (int i = 0; i < num; i++) 
					{
						bag.addGoods(this, goodsId+i, 1);
					}
				}
				else
				{
					if(cmds.length > 2)
						bag.addGoods(this, goodsId, Integer.parseInt(cmds[2]));
					else
						bag.addGoods(this, goodsId, 1);
				}
				
				result =  true;
			}
			catch(NumberFormatException e)
			{
				result =  false;
			}
		}
		else if(cmds[0].equals("addEquip"))
		{
			try
			{
				Bag bag = (Bag)player.getExtPlayerInfo("bag");
				int goodsId = Integer.parseInt(cmds[1]);
				if(cmds.length <= 2)
				{
					bag.addGoods(this, goodsId, 1);
					result = true;
				}
				else
				{
					int quality = Integer.parseInt(cmds[2]);
					if(quality > 5 || quality < 0)
						result = false;
					else
					{
						Object obj = DataFactory.getInstance().getGameObject(goodsId);
						
						if(obj == null || !(obj instanceof GoodsEquip))
							result = false;
						else
						{
							GoodsEquip goods = (GoodsEquip) obj;

							GoodsEquip equip = (GoodsEquip) goods;
							GoodsEquip newGoods = equip.makeNewBetterEquip(quality);
							newGoods.objectIndex = GameServer.getInstance().getDatabaseAccessor().getGoodsObjIndex();
							
							bag.sendAddGoods(this,newGoods);
							result = true;
						}
					}
				}
			}
			catch(NumberFormatException e)
			{
				result =  false;
			}
		}
		else if(cmds[0].equals("addPetExp"))
		{
			try
			{
				long expPoint = Long.parseLong(cmds[1]);
				PetTome pt = (PetTome) player.getExtPlayerInfo("petTome");
				int type = Integer.parseInt(cmds[2]);
				if(type == 1)
				{
					Pet pet = pt.getActivePet();
					if(pet == null)
						return false;
//					if(cmds.length == 3)
//					{
//						int inti = Integer.parseInt(cmds[2]);
//						
//						if(!pet.isMaxInti(1))
//						{
//							pet.setInti(inti, this);
//							result =  true;
//						}
//					}
					if(!pet.isMaxInti(2))
					{
						addPetExp(expPoint);
						result =  true;
					}
				}
				else if(type == 2)
				{
					Pet pet = pt.getActiveBattlePet();
					if(pet == null)
						return false;
					pet.addBattlePetExp(this,expPoint);
					result = true;
				}
			}
			catch(NumberFormatException e)
			{
				result =  false;
			}
		}
		else if(cmds[0].equals("moveToRoom"))
		{
			try
			{
				int roomId = Integer.parseInt(cmds[1]);
				moveToRoom(roomId);
				result =  true;
			}
			catch(NumberFormatException e)
			{
				result =  false;
			}
		}
		else if(cmds[0].equals("addHonour"))
		{
			try
			{
				int honour = Integer.parseInt(cmds[1]);
				setHonour(honour);
				result =  true;
			}
			catch(NumberFormatException e)
			{
				result =  false;
			}
		}
		else if(cmds[0].equals("addMagic"))
		{
			try
			{
				int magic = Integer.parseInt(cmds[1]);
				player.setMagicPoint(magic,(getParent() instanceof BattleController)?getAttachment():null);
				result =  true;
			}
			catch(NumberFormatException e)
			{
				result =  false;
			}
		}
		else if(cmds[0].equals("addLife"))
		{
			try
			{
				int life = Integer.parseInt(cmds[1]);
				player.setHitPoint(life,(getParent() instanceof BattleController)?getAttachment():null);
				result =  true;
			}
			catch(NumberFormatException e)
			{
				result =  false;
			}
		}
		else if(cmds[0].equals("mail"))
		{
			try
			{
				String accountName = cmds[1];
				PlayerController target = getWorldManager().getPlayerControllerByAccountName(accountName);
				
				Mail mail = new Mail(DC.getString(DC.PLAYER_29));
				mail.setTitle("GM");
				mail.setPoint(Integer.parseInt(cmds[2]));
				mail.setMoney(Integer.parseInt(cmds[3]));

				if(cmds.length > 4)
				{
					Goods[] goods = DataFactory.getInstance().makeGoods(Integer.parseInt(cmds[4]), Integer.parseInt(cmds[5]));
					
					if(goods == null || goods.length == 0)
						return false;

					mail.addAttach(goods[0]);

					if(cmds.length == 8)
					{
						goods = DataFactory.getInstance().makeGoods(Integer.parseInt(cmds[6]), Integer.parseInt(cmds[7]));
						
						if(goods == null || goods.length == 0)
							return false;
						
						mail.addAttach(goods[0]);
					}
				}
				
				if(target == null)
				{
					mail.sendOffLineWithAccountName(accountName);
				}
				else
				{
					mail.send(target);
				}
				
				result =  true;
			}
			catch(Exception e)
			{
				result =  false;
			}
		}
		else if(cmds[0].equals("maill"))
		{
			try
			{
				String accountName = cmds[1];
				PlayerController target = getWorldManager().getPlayerControllerByAccountName(accountName);
				
				Mail mail = new Mail(DC.getString(DC.PLAYER_29));
				mail.setTitle("*GM");
				mail.setPoint(Integer.parseInt(cmds[2]));
				mail.setMoney(Integer.parseInt(cmds[3]));

				if(cmds.length > 4)
				{
					Goods[] goods = DataFactory.getInstance().makeGoods(Integer.parseInt(cmds[4]), Integer.parseInt(cmds[5]));
					
					if(goods == null || goods.length == 0)
						return false;

					mail.addAttach(goods[0]);

					if(cmds.length == 8)
					{
						goods = DataFactory.getInstance().makeGoods(Integer.parseInt(cmds[6]), Integer.parseInt(cmds[7]));
						
						if(goods == null || goods.length == 0)
							return false;
						
						mail.addAttach(goods[0]);
					}
				}
				
				if(target == null)
				{
					mail.sendOffLineWithAccountName(accountName);
				}
				else
				{
					mail.send(target);
				}
				
				result =  true;
			}
			catch(Exception e)
			{
				result =  false;
			}
		}
		else if(cmds[0].equals("close"))
		{
			try
			{
				String ip = cmds[1];
				String port = cmds[2];

			    NetConnection net = worldManager.getConnByIpAndPort(ip+":"+port);
			    if(net != null)
			    {
			    	net.close();
			    }
				
				result =  true;
			}
			catch(NumberFormatException e)
			{
				result =  false;
			}
		}
		else if(cmds[0].equals("clearAnswer"))
		{
			AnswerParty ap = (AnswerParty) player.getExtPlayerInfo("answerParty");
			ap.clearAll();
			result = true;
		}
		else if(cmds[0].equals("taskComplate"))
		{
			
			if(getTeam()!= null)
			{
				List list = getTeam().getPlayerList();
				for (int i = 0; i < list.size(); i++)
				{
					PlayerController everyone = (PlayerController)list.get(i);
					
					if(everyone == null)
						continue;
					
					
					TaskInfo ti = (TaskInfo)everyone.getPlayer().getExtPlayerInfo("taskInfo");
					ArrayList tasks = ti.getTasks();
					for (int j = 0; j < tasks.size(); j++)
					{
						Task task = (Task)tasks.get(j);
						
						if(task== null)
							continue;
						
						for (int k = 0; k < task.imp_monsters.length; k++)
						{
							task.real_monsters[k] = task.imp_monsters[k][1];
						}
						task.sendupdateTask(everyone);
						task.checkTaskFinish(everyone);
					}
				}
			}
			else
			{
				TaskInfo ti = (TaskInfo)player.getExtPlayerInfo("taskInfo");
				ArrayList tasks = ti.getTasks();
				for (int i = 0; i < tasks.size(); i++)
				{
					Task task = (Task)tasks.get(i);
					
					if(task== null)
						continue;
					
					
					
					for (int j = 0; j < task.imp_monsters.length; j++)
					{
						task.real_monsters[j] = task.imp_monsters[j][1];
					}
					task.sendupdateTask(this);
					task.checkTaskFinish(this);
				}
			}
			result = true;
		}
		else if(cmds[0].equals("fixExp"))
		{

			String accountName = cmds[1];
			
			PlayerController target = getWorldManager().getPlayerControllerByAccountName(accountName);
			if(target == null)
			{
				Player tp = getWorldManager().getDatabaseAccessor().getPlayer(accountName);
				if(tp != null)
				{
					Map map = (Map)DataFactory.getInstance().getAttachment(DataFactory.ATTACH_EXP);
					Exp exp = (Exp)map.get(tp.level+1);
					
					if(exp != null)
					{
						tp.nextExp = exp.levelExp;
						tp.requireExp = exp.levelExp;
						
						exp = (Exp)map.get(tp.level);
						tp.experience = exp.total;
						System.out.println("fixExp offline : name"+tp.name+"-"+"level"+tp.level+"-"+tp.nextExp+"-"+tp.experience +"-"+tp.requireExp);
					}
					getWorldManager().getDatabaseAccessor().savePlayer(tp);
				}

			}
			else
			{
				Map map = (Map)DataFactory.getInstance().getAttachment(DataFactory.ATTACH_EXP);
				Exp exp = (Exp)map.get(target.getPlayer().level+1);
				
				if(exp != null)
				{
					target.getPlayer().nextExp = exp.levelExp;
					target.getPlayer().requireExp = exp.levelExp;
					
					exp = (Exp)map.get(target.getPlayer().level);
					target.getPlayer().experience = exp.total;
					
					System.out.println("fixExp offline : name"+target.getPlayer().name+"-"+"level"+target.getPlayer().level+"-"+target.getPlayer().nextExp+"-"+target.getPlayer().experience +"-"+target.getPlayer().requireExp);
				}
			}

			
			result = true;
		}
		else if(cmds[0].equals("reloadTop"))
		{
			GameServer.getInstance().getWorldManager().
			getJobObserver().addJob(GameServer.JOB_GAME2, new GetTopJob());
			System.out.println("Reload Top Success");
			result = true;
		}
		else if(cmds[0].equals("shopCenter"))
		{
			List list = ShopCenter.getInstance().getCenterGoods();
			for (int i = 0; i < list.size(); i++)
			{
				CenterGoods cg = (CenterGoods)list.get(i);
				cg.setTestHour();
			}
			System.out.println("ShopCenter all 10 min");
			result = true;
		}
		
		
		return result;
	}


	public boolean isGmAccount()
	{
		ArrayList list= (ArrayList)DataFactory.getInstance().getAttachment(DataFactory.GMPROCESS_LIST);
		
		if(list == null)
			return false;
		
		for (int i = 0; i < list.size(); i++)
		{
			if(i == 0)
			{
				if(((String)list.get(i)).equals("*"))
					return true;
			}

			if(player.accountName.equalsIgnoreCase(((String)list.get(i))))
				return true;
		}
		return false;
	}
	
	public boolean isManager()
	{
		ArrayList list = (ArrayList)DataFactory.getInstance().getAttachment(DataFactory.GMPROCESS_LIST);
		
		if(list == null)
			return false;
		
		boolean isManger = true;
		
		for (int i = 0; i < list.size(); i++)
		{
			String name = (String)list.get(i);
			
			if(name.equals("-"))
			{
				isManger = false;
				continue;
			}
			if(player.accountName.equals(name))
				return isManger;
		}
		return isManger;
	}
	
	/**
	 * 发送自己的完整信息
	 */
	public void sendInfo()
	{	
		ByteBuffer buffer = new ByteBuffer(64);
		player.writeTo(buffer);
		netConnection.sendMessage(new SMsg(SMsg.S_PLAYER_INFO_COMMAND,buffer));
	}
	
	public void sendSkill(int type)
	{
		SkillTome skill = (SkillTome)player.getExtPlayerInfo("skillTome");
		
		ByteBuffer buffer = new ByteBuffer(64);

		if(type == 1)
		{
			buffer.writeByte(type);
			skill.writeTo(buffer);
			skill.sendAutoSkill(buffer);
		}
		else if(type == 2)
		{
			buffer.writeByte(type);
			skill.writeActiveSkill(buffer);
		}

		netConnection.sendMessage(new SMsg(SMsg.S_GET_SKILL_COMMAND,buffer));
	}
	
	public void sendSkill(AppMessage msg)
	{
		int type = msg.getBuffer().readByte();
		
		sendSkill(type);
	}
	
	private boolean isStopMoveTime = false;
	
	private int stopCount;
	
	/**
	 * 键盘移动
	 * @param msg
	 */
	public void keyMoveTo(AppMessage msg)
	{
		RoomController room  = getRoom();
		
		if(room == null)
			return;
		
		ByteBuffer readBuff = msg.getBuffer();
		int state = readBuff.readByte(); //0停 1走
		int pos = readBuff.readByte(); //1上   2下   4左  8右   5上左 6下左 9上右 10下右
		int x = readBuff.readInt(); //发送按下坐标 x
		int y = readBuff.readInt(); //发送按下坐标 x
		
		
		if(isStopMoveTime)
		{
			if(++stopCount > 5)
			{
				if(stopCount >10)
				{
					//MainFrame.println("more keyMoveTo  "+getID());
					close();
					stopCount = 0;
				}
			}
			return;
		}
		isStopMoveTime = true;
		
		
		netConnection.getLastReadTime();
		
		ByteBuffer writeBuff = new ByteBuffer(16);
		writeBuff.writeInt(player.id);
		writeBuff.writeByte(state);
		writeBuff.writeByte(pos);
		writeBuff.writeInt(x);
		writeBuff.writeInt(y);
		
		if(state == 0) //停下的时候记录玩家的坐标
		{
			getPlayer().x = x;
			getPlayer().y = y;
		}
		room.dispatchMsg(SMsg.S_PLAYER_ONKEYMOVE_COMMAND, writeBuff);
		writeBuff = null;
		msg = null;
	}
	
	
	private long lastMoveTime;
	
	/**
	 * 鼠标移动
	 * @param msg
	 */
	private void mouseMoveTo(AppMessage msg)
	{
		RoomController room  = getRoom();
		
		if(room == null)
			return;
		
		if(lastMoveTime +400 > netConnection.getLastReadTime())
			return;
		
		lastMoveTime = netConnection.getLastReadTime();
		
		ByteBuffer readBuff = msg.getBuffer();
		int x = readBuff.readInt();  //发送目标坐标 x
		int y = readBuff.readInt();  //发送目标坐标 y

		stopCount = 0;
		isStopMoveTime = false;
		
		ByteBuffer writeBuff = new ByteBuffer(12);
		writeBuff.writeInt(player.id);
		writeBuff.writeInt(x);
		writeBuff.writeInt(y);
		room.dispatchMsg(SMsg.S_PLAYER_ONMOUSEMOVE_COMMAND, writeBuff);
		player.x = x;
		player.y = y;
		writeBuff = null;
		msg = null;
	}

	/**
	 * 判断交易双方背包是不是满了
	 * @param player
	 * @param target
	 * @return true 表示没有满 false表示空格不够
	 */
	public boolean isBusiness(PlayerController player,PlayerController target)
	{
		Bag bag1 = (Bag) player.getPlayer().getExtPlayerInfo("bag");
		Bag bag2 = (Bag) target.getPlayer().getExtPlayerInfo("bag");
		if(bag1.getNullCount() < 9)
		{
			player.sendAlert(ErrorCode.ALERT_PLAYER_BAG_KONGJIAN_ERROR);
			return false;
		}
		else if(bag2.getNullCount() < 9)
		{
			player.sendAlert(ErrorCode.ALERT_TARGET_BAG_KONGJIAN_ERROR);
			return false;
		}
		else
			return true;
	}
	/**
	 * 交易邀请
	 * @param msg
	 */
	private void requestBusiness(AppMessage msg)
	{

		PlayerContainer container =  getParent();
		
		if(container == null)
		{
			sendAlert(ErrorCode.ALERT_PLAYER_NOTAT_ROOM);
			return;
		}
		
		if(!(container instanceof RoomController))
		{
			if(container instanceof BattleController)
			{
				sendAlert(ErrorCode.ALERT_NOT_BUSINESS_IN_BATTLE);
				return;
			}
			else
			{
				sendAlert(ErrorCode.ALERT_PLAYER_ISNOT_ROOM);
				return;
			}
		}
		
		

		if(getParent() instanceof BusinessController)
		{
			sendAlert(ErrorCode.ALERT_BUSINESS_ING_PLAYER);
			return;
		}
		
		RoomController room = (RoomController)container;
		
//		int playerId = msg.getBuffer().readInt();
		String playerName = msg.getBuffer().readUTF();
//		if(getID() == playerId)
		if(getName().equals(playerName))
			return;
		
		PlayerController targetPlayer = room.getPlayer(playerName);
		
		if(targetPlayer == null)
		{
			sendAlert(ErrorCode.ALERT_PLAYER_NOTAT_ROOM);
			return;
		}
		
		if(targetPlayer.isAuto)
		{
			sendAlert(ErrorCode.ALERT_TARGET_PLAYER_AUTO_BATTLEING);
			return;
		}
		
		if(targetPlayer.getParent() instanceof BusinessController)
		{
			sendAlert(ErrorCode.ALERT_BUSINESS_ING_PLAYER);
			return;
		}
		
		if(targetPlayer.getParent() instanceof BattleController)
		{
			sendAlert(ErrorCode.ALERT_NOT_BUSINESS_IN_BATTLE);
			return;
		}
		
		if(isChoose(targetPlayer,1))
			return;
		
		if(!isBusiness(this,targetPlayer))
			return;

		String confirmationName = getName() + "-businessInvitation-"
				+ targetPlayer.getName();

		ConfirmJob cj = worldManager.getConfirmation(confirmationName);
		if(cj != null)
		{
			sendAlert(ErrorCode.ALERT_PLAYER_SAME_INVITATION);
			return;
		}
		
		ByteBuffer buffer = new ByteBuffer(24);
		buffer.writeInt(getID());
		buffer.writeUTF(getName());
		targetPlayer.getNetConnection().sendMessage(new SMsg(SMsg.S_PLAYER_BUSINESS_REQUEST_COMMAND,buffer));
		
		cj = new BusinessInvitation(this,targetPlayer);
		cj.setName(confirmationName);
		
		worldManager.addConfirmJob(cj);
	}

	
	/**
	 * 交易回复
	 * @param msg
	 */
	private void responseBusiness(AppMessage msg)
	{
		int inviterId = msg.getBuffer().readInt();
		boolean result = msg.getBuffer().readBoolean();
		if(getID() == inviterId)
			return;
		if(!(getParent() instanceof RoomController))
		{
			if(getParent() instanceof BattleController)
			{
				if(result)
					sendAlert(ErrorCode.ALERT_NOT_BUSINESS_IN_BATTLE);
				return;
			}
			else if(getParent() instanceof BusinessController)
			{
				if(result)
					sendAlert(ErrorCode.ALERT_BUSINESS_ING_PLAYER);
				return;
			}
		}
		if(getParent() instanceof BusinessController)
		{
			if(result)
				sendAlert(ErrorCode.ALERT_BUSINESS_ING_PLAYER);
			return;
		}
		
		RoomController room = (RoomController) getParent();
		PlayerController inviter = room.getPlayer(inviterId);
		if(inviter == null)
		{
			if(result)
				sendAlert(ErrorCode.ALERT_PLAYER_ISNOT_ROOM);
			return;
		}
		if(inviter.isAuto)
		{
			sendAlert(ErrorCode.ALERT_TARGET_PLAYER_AUTO_BATTLEING);
			return;
		}
		if(inviter.getParent() instanceof BattleController)
		{
			if(result)
				sendAlert(ErrorCode.ALERT_NOT_BUSINESS_IN_BATTLE);
			return;
		}
		if(inviter.getParent() instanceof BusinessController)
		{
			if(result)
				sendAlert(ErrorCode.ALERT_BUSINESS_ING_PLAYER);
			return;
		}
		if(isChoose(inviter,1))
			return;
		
		if(!isBusiness(inviter,this))
			return;
		
		String confirmationName = inviter.getName() + "-businessInvitation-" + getName();
	    Object obj = worldManager.getConfirmation(confirmationName);
		if(obj == null)
	    {
			if(result)
				sendAlert(ErrorCode.ALERT_INVITE_TIME_OVERRUN);
	    	return;
	    }
		
		if(!(obj instanceof BusinessInvitation))
		{
			if(result)
				sendAlert(ErrorCode.EXCEPTION_CLASS_ERROR);
			return;
		}
		
		BusinessInvitation bi = (BusinessInvitation) obj;
		bi.confirm(result);		
		bi.setDefaultLifeTime();
	}
	

	
//	/**
//	 * 转职
//	 * @param msg
//	 */
//	public void upProfession(AppMessage msg)
//	{
//		if(!(getParent() instanceof RoomController))
//		{
//			sendAlert(ErrorCode.ALERT_PLAYER_OVER_OTHER);
//			return;
//		}
//		int up = msg.getBuffer().readByte();//要转的职业
//		if(player.level < UPJOBLEVEL)
//		{
//			sendAlert(ErrorCode.ALERT_PLAYER_LEVEL_LACK);
//			return;
//		}
//		if(player.upProfession != 0)
//		{
//			sendAlert(ErrorCode.ALERT_PLAYER_HASBEEN_UP_PROFESSION);
//			return;
//		}
//		if(player.profession == 1)
//		{
//			if(up != 1 && up != 2)
//			{
//				sendAlert(ErrorCode.ALERT_UP_PROFESSION_ERROR);
//				return;
//			}
//		}
//		else if(player.profession == 2)
//		{
//			if(up != 3 && up != 4)
//			{
//				sendAlert(ErrorCode.ALERT_UP_PROFESSION_ERROR);
//				return;
//			}
//		}
//		else if(player.profession == 3)
//		{
//			if(up != 5 && up != 6)
//			{
//				sendAlert(ErrorCode.ALERT_UP_PROFESSION_ERROR);
//				return;
//			}
//		}
//		else if(player.profession == 4)
//		{
//			if(up != 7 && up != 8)
//			{
//				sendAlert(ErrorCode.ALERT_UP_PROFESSION_ERROR);
//				return;
//			}
//		}
//		player.upProfession = up;
//		
//		Bag bag = (Bag) player.getExtPlayerInfo("bag");
//		
//		if(!bag.isRoleEquip())
//			player.setPlayerModelMotionId();
//		
//		ByteBuffer buffer = new ByteBuffer(1);
//		buffer.writeByte(1);
//		buffer.writeInt(getID());
//		buffer.writeByte(up);
//		getRoom().dispatchMsg(SMsg.S_PLAYER_UP_PROFESSION_COMMAND, buffer);
//		
//		updateRoleInfo();
//		
//		campState = 2;
//		isUpJob = false;
//		
//		checkChoose();
//	}
	
	
	public String getName()
	{
		return player.name;
	}

	public int getID()
	{
		return player.id;
	}

	public long getObjectIndex()
	{
		return 0;
	}
	
	public Player getPlayer()
	{
		return player;
	}

	public void setPlayer(Player player)
	{
		this.player = player;
	}

	public WorldManager getWorldManager()
	{
		return worldManager;
	}

	public void setWorldManager(WorldManager worldManager)
	{
		this.worldManager = worldManager;
	}

	public boolean isOnline()
	{
		return online;
	}

	public void setOnline(boolean online)
	{
		this.online = online;
	}

	public NetConnection getNetConnection()
	{
		return netConnection;
	}

	public void setNetConnection(NetConnection netConnection)
	{
		this.netConnection = netConnection;
	}

	public AamController getAam()
	{
		return aam;
	}

	public void setAam(AamController aam)
	{
		this.aam = aam;
	}

	public TeamController getTeam()
	{
		return team;
	}

	public void setTeam(TeamController team)
	{
		this.team = team;
	}

	public RoomExit getRoomExit()
	{
		return roomExit;
	}

	public void setRoomExit(RoomExit roomExit)
	{
		this.roomExit = roomExit;
	}
	
	public FamilyController getFamily()
	{
		return family;
	}
	
	public void setFamilyController(FamilyController family)
	{
		this.family = family;
	}
	
	String autoStr = "";
	
	public void update(long currentMillis)
	{
		if(DataFactory.isIdcard)
		{
			OtherExtInfo oei = (OtherExtInfo) player.getExtPlayerInfo("otherExtInfo");
			if(oei.onlineTime < UNSAFETIME && !oei.isValiIdcard)
				oei.setOnlineTime(500);
		}
		
		if(isAuto)
		{
			if(getParent() instanceof RoomController)
			{
				if((team != null && team.isLeader(this)) || team == null)
				{
					if(autoCount > 0)
					{	
						String str = checkMaybeAutoState();
						if(str.isEmpty())
						{
							autoBattle();
						}
						else
						{
							if(!str.equals(autoStr))
								sendError(str);
						}
					}
					else
						setAuto(false, 0, null);
				}
			}
		}
		
		BuffBox buffBox = (BuffBox)getPlayer().getExtPlayerInfo("buffBox");
		if(buffBox.pveCDtime > 0)
		{
			buffBox.pveCDtime -= 500;
		}
		
		
		Bag bag = (Bag) player.getExtPlayerInfo("bag");
		Goods goods = bag.getExtGoods(2);
		if(goods != null && player.expMultTime != 0 && goods instanceof GoodsProp)
		{
			GoodsProp prop = (GoodsProp) goods;
			if(currentMillis - player.expMultTime >= prop.expTimes)
			{
				player.expMultTime = 0;
				bag.setExtGoods(2, null);
				bag.sendExpBuff(this, prop.effect, false,0);
			}
		}
		
		GoodsProp gp = (GoodsProp) bag.getExtGoods(3);
		if(gp != null)
		{
			if(currentMillis - gp.objectIndex >= gp.expTimes)
			{
				bag.setExtGoods(3, null);
				bag.sendExpBuff(this, gp.effect, false, 0);
			}
		}
		 
		Goods oGoods = bag.getExtGoods(4);
		if(oGoods != null && oGoods.objectIndex != 0 && oGoods instanceof GoodsProp)
		{
			GoodsProp prop = (GoodsProp) oGoods;
			if(currentMillis - prop.objectIndex >= prop.expTimes)
			{
				prop.objectIndex = 0;
				bag.setExtGoods(4, null);
				bag.sendExpBuff(this, prop.effect, false,0);
			}
		}
		
		
		PVPInfo pvpInfo = (PVPInfo) player.getExtPlayerInfo("PVPInfo");
		if(currentMillis - pvpInfo.startCheckTime >= PVPInfo.checkTime)
		{
			pvpInfo.weakness = 0;
			pvpInfo.startCheckTime = 0;
		}
	}

	public boolean isDead()
	{
		return player.hitPoint <= 0;
	}
	
	
	/**
	 * 设置玩家凶恶值
	 */
	public void setAtrocitys()
	{
//		int at = player.atrocity;
//		if(at >= 0 && at < 40)
//			at += 100;
//		else if(at >= 40 && at <  90)
//			at += 150;
//		else if(at >= 90 && at <  200)
//			at += 200;
//		else if(at >= 200 && at <  400)
//			at += 300;
//		else if(at >= 400 && at <  600)
//			at += 450;
//		else if(at >= 600 && at <  1200)
//			at += 650;
//		else if(at > 1200)
//			at += 1200;
//		
//		player.atrocity = at;
//		if(player.atrocity > 1200)
//			player.atrocity = 1200;
	}
	

	
	
	private void mailProcess(AppMessage msg)
	{
		ByteBuffer buffer = msg.getBuffer();
		int type = buffer.readByte();
		
		MailBox mb = (MailBox)player.getExtPlayerInfo("mailbox"); 

		if(type == MailBox.ADD)
		{
			if(!mb.isHaveNewMail())
			{
				return;
			}
			buffer = new ByteBuffer(1);	
			buffer.writeByte(MailBox.ADD);
		}
		if(type == MailBox.QUERY)
		{
			buffer = new ByteBuffer(64);	
			buffer.writeByte(MailBox.QUERY);
			mb.writeTo(buffer);
		}		
		else if(type == MailBox.QUERYMAIL)
		{
			int id = buffer.readInt();
			
			Mail mail = mb.getMail(id);
			
			if(mail == null)
				return;
			
			buffer = new ByteBuffer(32);	
			buffer.writeByte(MailBox.QUERYMAIL);
			mail.writeTo(buffer);
			mail.setRead(true);
		}
		else if(type == MailBox.GETATTACH)
		{
			int id = buffer.readInt();
			
			Mail mail = mb.getMail(id);
			
			if(mail == null)
				return;
			
			Bag bag  = (Bag)player.getExtPlayerInfo("bag");
			

			if(!bag.checkEnough(mail.getAttachCount()))
			{
				sendAlert(ErrorCode.ALERT_TASK_AWA_BAG_NOT_ENOUGH);
				return;
			}
			
			Goods goods = mail.getAttach(1);
			
			if(goods != null)
			{
				bag.sendAddGoods(this, goods);
			}
			goods = mail.getAttach(2);
			if(goods != null)
			{
				bag.sendAddGoods(this, goods);
			}
			
			if(mail.getPoint() != 0 || mail.getMoney() != 0)
				bag.addMoney(this, mail.getPoint(), mail.getMoney());
			
			mail.clearAttach();	
			buffer = new ByteBuffer(1);	
			buffer.writeByte(MailBox.GETATTACH);
		}
		else if(type == MailBox.REMOVE)
		{
			int size = buffer.readInt();
			
			if(size <= 0 || size > MailBox.MAX_MAILBOX_SIZE)
				return;
			
			List removeList = new ArrayList(size+1);
			for (int i = 0; i < size; i++)
			{
				int id = buffer.readInt();
				
				Mail mail = mb.getMail(id);
				
				if(mail == null)
					continue;
				
				if(mail.getTitle().startsWith("*"))
				{
					sendAlert(ErrorCode.ALERT_MAIL_UNDEL);
					return;
				}
				
				removeList.add(id);
			}
			
			if(removeList.size() <= 0)
				return;
			
			mb.removeMails(removeList);
			
			buffer = new ByteBuffer(1);	
			buffer.writeByte(MailBox.REMOVE);
			size = removeList.size();
			buffer.writeInt(size);
			for (int i = 0; i < size ; i++)
				buffer.writeInt((Integer)removeList.get(i));
		}
		
		netConnection.sendMessage(new SMsg(SMsg.S_PLAYER_MAIL_COMMAND,buffer));
		
	}

	public void sendExpBuff()
	{
		Bag bag = (Bag) player.getExtPlayerInfo("bag");
		Goods lifeGoods = bag.getExtGoods(0);
		Goods magicGoods = bag.getExtGoods(1);
		Goods expGoods = bag.getExtGoods(2);
		GoodsProp stateGoods = (GoodsProp) bag.getExtGoods(3);
		GoodsProp olexpGoods = (GoodsProp) bag.getExtGoods(4);
		if(lifeGoods != null && player.extLife > 0 && lifeGoods instanceof GoodsProp)
			bag.sendExpBuff(this, ((GoodsProp)lifeGoods).effect, true, 0);
		if(magicGoods != null && player.extMagic > 0 && magicGoods instanceof GoodsProp)
			bag.sendExpBuff(this, ((GoodsProp)magicGoods).effect, true, 0);
		long time = 0;
		if(WorldManager.currentTime != 0)
			time = WorldManager.currentTime;
		else
			time = System.currentTimeMillis();
		if(expGoods != null && expGoods instanceof GoodsProp)
		{
			GoodsProp prop = (GoodsProp) expGoods;
			long t = time - player.expMultTime;
			if(t < prop.expTimes)
			{
				int remainTime = (int) (prop.expTimes - t);
				bag.sendExpBuff(this, ((GoodsProp)expGoods).effect, true,remainTime);
			}
			else
			{
				bag.setExtGoods(2, null);
			}
		}
		if(stateGoods != null)
		{
			long t = time - stateGoods.objectIndex;
			if(t < stateGoods.expTimes)
			{
				int remainTime = (int) (stateGoods.expTimes - t);
				bag.sendExpBuff(this, stateGoods.effect, true, remainTime);
			}
			else
			{
				bag.setExtGoods(3, null);
			}
		}
		if(olexpGoods != null)
		{
			long t = time - olexpGoods.objectIndex;
			if(t < olexpGoods.expTimes)
			{
				int remainTime = (int) (olexpGoods.expTimes - t);
				bag.sendExpBuff(this, olexpGoods.effect, true, remainTime);
			}
			else
			{
				bag.setExtGoods(4, null);
			}
		}
	}
	
	
	
	/**
	 * 查看玩家宠物信息
	 * @param msg
	 */
	public void sendPetInfo(AppMessage msg)
	{
		int playerId = msg.getBuffer().readInt();
		PlayerController target = null;
		if(playerId == getID())
			target = this;
		else
		{
			target = roomController.getPlayer(playerId);
			
			if(target == null)
				target = worldManager.getPlayerControllerById(playerId);
		
			if(target == null)
			{
				sendAlert(ErrorCode.ALERT_PLAYER_NOTAT_ROOM);
				return;
			}
		}

		sendPetInfo(target,playerId);
	}
	
	
	/**
	 * 发送宠物基本信息
	 * @param target
	 * @param playerId
	 */
	public void sendPetInfo(PlayerController target,int playerId)
	{
		PetTome pt = (PetTome) target.getPlayer().getExtPlayerInfo("petTome");
		Pet pet = pt.getActivePet();
		if(pet == null)
			return;
		ByteBuffer buffer = new ByteBuffer();
		buffer.writeInt(playerId);
		pt.writeTo(buffer);
		if(target.getID() == playerId)
		{
			long time = WorldManager.currentTime-pet.trainTime;//long time = System.currentTimeMillis()-pet.trainTime;
			buffer.writeByte(pet.trainState);
			int downTime = 0;
			if(time < pet.gameTime)
				downTime = (int) (pet.gameTime - time);
			buffer.writeInt(downTime);
		}
		else
		{
			buffer.writeByte(pet.trainState);
			buffer.writeInt(0);
		}
		
		
		netConnection.sendMessage(new SMsg(SMsg.S_PLAYER_PET_INFO_COMMAND,buffer));
	}
	
	/**
	 *宠物信息 
	 * @param msg
	 */
	private void setPetInfoOption(AppMessage msg)
	{//type 1表示修改宠物名字 2表示 宠物升级 3表示宠物学技能 
		PetTome pets = (PetTome) player.getExtPlayerInfo("petTome");
		Pet pet = pets.getActivePet();
		if(pet == null)
			return;
		int type = msg.getBuffer().readByte();
		if(type == 1)//修改名字
		{
			String name = msg.getBuffer().readUTF();
			name = MarkChar.replace(name);
			
			if(!UseChar.isCanReg(name))
			{
				ByteBuffer buffer = new ByteBuffer(4);
				buffer.writeInt(ErrorCode.ALERT_HUOXINGWEN);
				netConnection.sendMessage(new SMsg(SMsg.S_EXCEPTION_MESSAGE, buffer));
				return;
			}
			
			
			
			if(name.isEmpty() || name.length() > 7 || name.trim().length() == 0)
				return;
			pet.setName(name);
			ByteBuffer buffer = new ByteBuffer(1);
			buffer.writeByte(1);
			netConnection.sendMessage(new SMsg(SMsg.S_PLAYER_PETINFO_OPTION_COMMAND,buffer));
			
			sendPetModel(2,pet.modelId,name);
		}//3表示学会宠物技能
		else if(type == 4)//变身
		{
			if(!isPetUp)
				return;
			int modelWay = msg.getBuffer().readByte();
			int modelId = msg.getBuffer().readInt();
			String att = msg.getBuffer().readUTF();

			if(modelId == 0 || modelWay == 0)
				return;
			
			PetExp pe = pet.getExpByLevel(pet.tmpLevel);
			
			if(pe == null)
				return;
	
			pet.modelWay += modelWay;
	
			if(!att.isEmpty())
			{
				String[] strs = Utils.split(att, ",");
				for (int i = 0; i < strs.length; i++) 
				{
					pet.setAttStr(strs[i]);
				}
			}
			
			if(pet.experience >= pe.total && pet.intimacyPoint >= pe.totalInt)
			{
				pet.modelId = modelId;
			}

			pet.setGrow();
//			checkPetExp(pet);
		
			sendPetInfo(this,getID());

			pet.initPetExpAndInt();
			
			if(pet.isStroll)
				sendPetModel(2,modelId,pet.name);
			
			isPetUp = false;
			
			sendAlwaysValue();
		}
		else if(type == 5)//遛宠
		{
			boolean isStroll = msg.getBuffer().readBoolean();
			if(pet.isStroll == isStroll)
				return;
			
			if(!pet.isStroll && isStroll)
				sendPetModel(1, pet.modelId,pet.name);
			else if(pet.isStroll && !isStroll)
				sendPetModel(0, pet.modelId,pet.name);
			
			pet.setStroll(isStroll);
		}
		else if(type == 6)//宠物出行1.嬉戏 2.锻炼 3.探险
		{
			if(isPetUp)
			{
				sendGetGoodsInfo(1, false, DC.getString(DC.PLAYER_30));
				return;
			}
			if(WorldManager.isZeroMorning(0))
			{
				sendAlert(ErrorCode.ALERT_SYSTEM_IS_UPDATE);
				return;
			}
			int trainType = msg.getBuffer().readByte();//宠物出行方式
			boolean flag = pet.petTrain(this,trainType);
			if(flag)
			{
				ByteBuffer buffer = new ByteBuffer(2);
				buffer.writeByte(6);
				buffer.writeByte(trainType);
				netConnection.sendMessage(new SMsg(SMsg.S_PLAYER_PETINFO_OPTION_COMMAND,buffer));
			}
			
			sendPetInfo(this, getID());
		}
		else if(type == 7)//宠物出行结束
		{
			int overType = msg.getBuffer().readByte();//1正常结束 2.强制结束

			pet.trainOver(this,overType);

			if(pet.trainState == 0)
			{
				ByteBuffer buffer = new ByteBuffer(2);
				buffer.writeByte(6);
				buffer.writeByte(pet.trainState);
				netConnection.sendMessage(new SMsg(SMsg.S_PLAYER_PETINFO_OPTION_COMMAND,buffer));
				
				sendPetInfo(this,getID());
			}
		}
		else if(type == 8)//请求宠物下一次变身形象
		{
			int nextLevel = 0;
			int id1 = 0,id2 = 0;
			for (int i = pet.checkLevel; i < pet.getUpMapSize(); i++)
			{
				PetExp pe = pet.getExpByLevel(i);
				PetUpRule pur = pet.getPetUpRule(i);
				if(pe == null || pur == null)
					continue;
				if(pur.upState != 0)
				{
					if(pet.experience >= pe.total && pet.intimacyPoint >= pe.totalInt)
						continue;
					nextLevel = pur.level;
					if(pur.upState == 1)
					{
						id1 = pet.getNextModelOne(pe,pur);
					}
					else if(pur.upState == 2)
					{
						int[] result = pet.getNextModelTwo(pe,pur);
						id1 = result[0];
						id2 = result[1];
					}
					break;
				}
			}
			
			ByteBuffer buffer = new ByteBuffer();
			buffer.writeByte(8);
			if(id1 == 0 && id2 == 0 && nextLevel == 0)
			{
				buffer.writeInt(pet.level);
				buffer.writeInt(pet.modelId);
				buffer.writeInt(id2);
				buffer.writeUTF(pet.intimacyPoint+"");
			}
			else
			{
				PetExp pe = pet.getExpByLevel(nextLevel);
				if(pe == null)
					return;
				buffer.writeInt(nextLevel);
				buffer.writeInt(id1);
				buffer.writeInt(id2);
				buffer.writeUTF(pe.totalInt+"");
			}
			netConnection.sendMessage(new SMsg(SMsg.S_PLAYER_PETINFO_OPTION_COMMAND,buffer));
		}
	}
	
	


	
	/**
	 * 更新宠物形象
	 * @param type 0为删除 1为增加 2为更新(客户端对宠物形象的控制)
	 * @param modelId
	 */
	public void sendPetModel(int type,int modelId,String petName)
	{
		ByteBuffer buffer = new ByteBuffer(9);
		buffer.writeByte(type);
		buffer.writeInt(getID());
		buffer.writeInt(modelId);
		buffer.writeUTF(petName);
		roomController.dispatchMsg(SMsg.S_PLAYER_PET_STROLL_COMMAND, buffer);
	}

	private void responseEscTimer()
	{
		ByteBuffer buffer = new ByteBuffer(4);
		
		if(player.escTimer == 0)
		{
			buffer.writeInt(0);
		}
		else
		{
			long time =(ESCTIME + player.escTimer)-WorldManager.currentTime;
			
			if(time <= 0)
			{
				buffer.writeInt(0);
			}
			else
			{
				buffer.writeInt((int)time);
			}
		}
		getNetConnection().sendMessage
		(new SMsg(SMsg.S_CLEAR_ESC_COMMAND,buffer));
	}
	
	
	public void checkPetExp(Pet pet)
	{
		int lev = pet.level;
		
		pet.checkLevelUp();
		
		if(pet.level > lev)
		{
			pet.setGrow();
//			pet.setGrow(pet.level-lev);
			
			sendGetGoodsInfo(1,false, DC.getString(DC.PLAYER_31));
			
			pet.nextExp = pet.getExpObj() == null ? 0 : pet.getExpObj().levelExp;
			ByteBuffer buffer = new ByteBuffer(13);
			buffer.writeByte(2);
			buffer.writeInt(pet.level);
			if(pet.nextExp == 0)
			{
				buffer.writeInt(0);
				buffer.writeInt(0);
			}
			else
			{	
				long e = pet.nextExp - pet.requireExp;
				buffer.writeUTF(e+"");
				buffer.writeUTF(pet.nextExp+"");
				buffer.writeInt((int) (e * 10000 / pet.nextExp));
			}
			netConnection.sendMessage(new SMsg(SMsg.S_PLAYER_PETINFO_OPTION_COMMAND,buffer));
		}
	}
	
	
	public boolean addPetExp(long exp)
	{
		PetTome pets = (PetTome) player.getExtPlayerInfo("petTome");
		Pet pet = pets.getActivePet();
		if(pet == null)
			return false;
		
		int lev = pet.level;

		pet.experience += exp;
		pet.requireExp -= exp;
		
		pet.checkLevelUp();

		sendPetInfo(this,getID());
		
		pet.changePetModel(this, lev);
		
		if(pet.level > lev)
		{
			pet.setGrow();
			
			if(getParent() instanceof BattleController)
			{
				getAttachment().fixPlayerData();
			}
			
			sendGetGoodsInfo(1,false, DC.getString(DC.PLAYER_31));
			
			pet.nextExp = pet.getExpObj() == null ? 0 : pet.getExpObj().levelExp;
			ByteBuffer buffer = new ByteBuffer(13);
			buffer.writeByte(2);
			buffer.writeInt(pet.level);
			if(pet.nextExp == 0)
			{
				buffer.writeInt(0);
				buffer.writeInt(0);
			}
			else
			{	
				long e = pet.nextExp - pet.requireExp;
				buffer.writeUTF(e+"");
				buffer.writeUTF(pet.nextExp+"");
				buffer.writeInt((int) (e * 10000 / pet.nextExp));
			}
			netConnection.sendMessage(new SMsg(SMsg.S_PLAYER_PETINFO_OPTION_COMMAND,buffer));
		
			sendPetInfo(this,getID());
		
			sendAlwaysValue();
		}
		return true;
	}


	/**
	 * 增加经验
	 * @param experience
	 * @param isCheckUpRole
	 * @param isPetGet 是否要检测守护经验转换
	 */
	public long addExp(long experience,boolean isCheckUpRole,boolean isPetGet,boolean isChongguan)
	{
		PetTome pt = (PetTome) player.getExtPlayerInfo("petTome");
		Pet pet = pt.getActiveBattlePet();
		if(pet != null)
		{
			if(pet.isChangeExpSkillActive())//有经验吸收
			{
				double mult = pet.getExpChangeMult();
				if(isPetGet)
				{
					if(isChongguan)
						pet.addBattlePetExp(this, (long) (experience*(Story.CHONG_GUAN_MULT+mult)));
					else
						pet.addBattlePetExp(this, (long) (experience*(1+mult)));
				}
				else
					pet.addBattlePetExp(this, (long) (experience*mult));
				return 0;
			}
			else
			{
				if(isPetGet)
				{
					if(isChongguan)
						pet.addBattlePetExp(this, (long)(experience * Story.CHONG_GUAN_MULT));
					else
						pet.addBattlePetExp(this, experience);
				}
			}
		}

		long disExp = experience;
		if(isCheckUpRole)
		{
			int type = player.getZhuanshengState();
			if(type > 0)
			{
				if(type == 1)
				{
					experience *= 0.85;
				}
				else if(type == 2)
				{
					experience *= 0.7;
				}
			}
		}

		int overLevel = player.getOverLevel();
		if(player.requireExp == 0 && player.level == overLevel)
			return 0;
	
		player.setDefaultData(1);
		player.uptateBuffPoint(0);

		int level = player.level;
		int maxLife = player.maxHitPoint;
		int maxMagic = player.maxMagicPoint;
		int power = player.getBaseInfo().power;
		int spirit = player.getBaseInfo().spirit;
		int wisdom = player.getBaseInfo().wisdom;
		int nimble = player.getBaseInfo().nimble;
		
		player.experience += experience;
		
		if(player.requireExp != 0)
			player.requireExp -= experience;
		
		checkLevelUp();

		sendGetExp(disExp);
		
		//每掉增加一次经验通知
		if(player.level > level)
		{
			player.nextExp = 0;
			ByteBuffer buff = new ByteBuffer(9);
			buff.writeByte(2); //当前房间内
			buff.writeInt(getID());
			buff.writeInt(player.level);
			
			RoomController room= getRoom();
			if(room != null)
				room.dispatchMsg(SMsg.S_ROOM_PLAYER_LEVELUP, buff);
			
			buff = new ByteBuffer(25);
			buff.writeByte(1); //当前小组内
			buff.writeInt(getID());
			buff.writeInt(player.level);
			long ne = (expObj == null ? 0 : expObj.levelExp);
			long exp = ne - player.requireExp;
			if(exp < 0)
				exp = 0;
			buff.writeUTF(exp+"");
			buff.writeUTF(ne+"");
			int rate = 10000;
			if(ne != 0)	
				rate = (int) (exp * 10000 / ne);
			buff.writeInt(rate);
			
			EquipSet es = (EquipSet) player.getExtPlayerInfo("equipSet");
			buff.writeInt(player.maxHitPoint+es.getTotalAtt("maxHitPoint"));
			buff.writeInt(player.maxMagicPoint+es.getTotalAtt("maxMagicPoint"));
			
			player.nextExp = ne;

			if(team != null)
			{
				team.dispatchMsg(SMsg.S_ROOM_PLAYER_LEVELUP, buff);
			}
			else
			{
				getNetConnection().sendMessage(new SMsg
						(SMsg.S_ROOM_PLAYER_LEVELUP,buff));
			}
			
			if(roomController != null)
			{
				roomController.sendCanTasks(this);
			}
			
			sendUpAtt(maxLife,maxMagic,power,spirit,wisdom,nimble);
			
			checkGuideStep();
			
			if(player.level > 500)
			{
				GameServer.getInstance().getWorldManager().getJobObserver().addJob(GameServer.JOB_DATABASEJOB, new SaveJob(GameServer.getInstance().getWorldManager(),player,SaveJob.PLAYER_LEVELUP_SAVE));
			}
		}
		
		sendAlwaysValue();

		if(level < AamController.MASTER_LEVEL && player.level >= AamController.MASTER_LEVEL && aam != null)
		{
			aam.onLeave(this);
		}
		return disExp;
	}
	
	public void sendUpAtt(int maxLife,int maxMagic,int power,int spirit,int wisdom,int nimble)
	{
		ByteBuffer buffer = new ByteBuffer(24);
		buffer.writeInt(player.maxHitPoint-maxLife);
		buffer.writeInt(player.maxMagicPoint-maxMagic);
		buffer.writeInt(player.getBaseInfo().power-power);
		buffer.writeInt(player.getBaseInfo().spirit-spirit);
		buffer.writeInt(player.getBaseInfo().wisdom-wisdom);
		buffer.writeInt(player.getBaseInfo().nimble-nimble);
		netConnection.sendMessage(new SMsg(SMsg.S_PLAYER_UP_ATT_COMMAND,buffer));
	}
	
	private void sendGetExp(long exp)
	{
		ByteBuffer buffer = new ByteBuffer();
		buffer.writeUTF(exp+"");
		netConnection.sendMessage(new SMsg(SMsg.S_PLAYER_GETEXP_COMMAND,buffer));
	}
	
	private void checkLevelUp()
	{
		if(expObj == null)
		{
			expObj = getExpByLevel(player.level+1,0);
			
			if(expObj == null)
			{
				Exp lastExpObj = getExpByLevel(player.level,0);
				player.experience = lastExpObj.total;
				player.requireExp = 0;
				return;
			}
			
			if(player.requireExp == 0 && player.level != 1)
			{
				player.requireExp = expObj.levelExp;
			}
		}

		if(player.level > Player.OVERLEVEL)
		{
			int overLevel = player.getOverLevel();
			if(player.level > overLevel)
			{
				player.level = overLevel;
				expObj = null;
				Exp lastExpObj = getExpByLevel(player.level,0);
				player.experience = lastExpObj.total;
				player.requireExp = 0;
				return;
			}
		}
	
		
		if(expObj.checkIsLevelUp(player))
		{
			levelUp();
			checkLevelUp();
		}

	}
	
	private void levelUp()
	{
		player.level ++;
		
		MailRemind.onCheckMail(this, MailRemind.ONLEVELUP, 0);
		
		expObj = getExpByLevel(player.level+1,0);
		
		if(expObj == null)
			return;

		player.requireExp += expObj.levelExp;
		
		player.initial(1);
		player.uptateBuffPoint(0);
		
		EquipSet es = (EquipSet) player.getExtPlayerInfo("equipSet");
		player.hitPoint = player.maxHitPoint + es.getTotalAtt("maxHitPoint");
		player.magicPoint = player.maxMagicPoint + es.getTotalAtt("maxMagicPoint");
		
		checkChoose();
	}
	

	
	/**
	 * 检测宠物是否可以变身
	 */
	public void checkPetChangeModel()
	{
		PetTome pt = (PetTome) player.getExtPlayerInfo("petTome");
		Pet pet = pt.getActivePet();

		if(pet.cmi1 != 0 && pet.cmi2 != 0 && pet.tmpLevel != 0 && !pet.att1.isEmpty() && !pet.att2.isEmpty())
		{
			if(!isGift)
				pet.sendChangeModel(pet.cmi1, pet.cmi2,pet.tmpLevel,pet.att1,pet.att2, this);
		}
		else
		{
			PetExp pe = pet.getExpByLevel(pet.level+1);
			if(pet.getUpMap() != null && pe != null)
			{
				PetUpRule pur = (PetUpRule) pet.getUpMap().get(pet.level+1);
				if(pur != null)
					pet.checkInti(pe,pur,this);
			}
		}

		
//		if(player.upProfession != 0 && player.camp == 0)
//		{
//			checkChooseCamp();
//		}
//		else if(player.upProfession == 0 && player.camp != 0)
//		{
//			checkChooseJob();
//		} 


	}
	
	/**
	 * 检测玩家是否到了选择阵营的时候,宠物是否变身 
	 */
	public void checkChoose()
	{
		if(player.level >= UPCAMPLEVEL && player.camp == 0 && !isCamp)
		{
			isCamp = true;
			ByteBuffer buffer = new ByteBuffer(1);
			buffer.writeByte(0);
			netConnection.sendMessage(new SMsg(SMsg.S_PLAYER_CAMP_SET_COMMAND,buffer));
			return;
		}
		checkPetChangeModel();
	}
	
	/**
	 * 检测玩家是否到了转职的时候 
	 */
	public void checkChooseJob()
	{
		if(player.level >= UPJOBLEVEL && player.upProfession == 0 && !isUpJob)
		{
			isUpJob = true;
			ByteBuffer buffer = new ByteBuffer(1);
			buffer.writeByte(0);
			netConnection.sendMessage(new SMsg(SMsg.S_PLAYER_UP_PROFESSION_COMMAND,buffer));
			return;
		}
		checkPetChangeModel();
	}
	

	public Exp getExpByLevel(int lv,int type)
	{
		if(type == 1)
		{
			if(lv > EXPMAXLEVEL)
				lv = EXPMAXLEVEL;
		}
		Map expMap = (Map)DataFactory.
		getInstance()
		.getAttachment(DataFactory.ATTACH_EXP);
		return (Exp)expMap.get(lv);
	}
	
	/**
	 * 取得队伍中有几个人跟自己一个家族(除开自己)
	 * @param players
	 * @return
	 */
	public int getFamilyPlayers(PlayerController[] players)
	{
		int familyCount = 0;
		for (int i = 0; i < players.length; i++)
		{
			if(players[i] == null || !players[i].isOnline())
				continue;
			if(players[i].getID() == getID())
				continue;
			if(players[i].getPlayer().familyId == player.familyId && player.familyId != 0)
				familyCount++;
		}
		return familyCount;
	}
	
	public boolean isLoverInTeam(PlayerController[] players)
	{
		OtherExtInfo oei = (OtherExtInfo) player.getExtPlayerInfo("otherExtInfo");
		if("".equals(oei.loverName))
			return false;
		for (int i = 0; i < players.length; i++)
		{
			if(players[i] == null || !players[i].isOnline())
				continue;
			if(players[i].getID() == getID())
				continue;
			if(oei.loverName.equals(players[i].getName()))
				return true;
		}
		return false;
	}
	
	/**
	 * 获取战斗得到经验
	 * @param exp 怪物掉落总经验
	 * @param levelCount 玩家等级总数
	 * @param monsterLevel 怪物平均等级
	 * @param type 1表示PVE 2表示PARTYPK
	 * @return
	 */
	public long teamExp(long exp,int levelCount,int monsterLevel,int type)
	{
		long result = 0;//
		if(team == null)
			result = exp;
		else
		{
			PlayerController[] players = team.getPlayers();
			
			exp = exp * players.length;

			result = exp / players.length;//按照等级比例分配
			
			if(isLoverInTeam(players))
			{
				OtherExtInfo oei = (OtherExtInfo) player.getExtPlayerInfo("otherExtInfo");
				if(oei.marryType == 1)
					result += result * (double)5 / 100;
				else if(oei.marryType == 2)
					result += result * (double)10 / 100;
			}
			
			int familyCount = getFamilyPlayers(players);
		
			if(familyCount == 1)//加上自己就是2个人为同一家族
				result += result * (double)5/100;
			else if(familyCount == 2)//加上自己就是3个人为同一家族
				result += result * (double)10/100;
			else if(familyCount == 3)//加上自己就是4个人为同一家族
				result += result * (double)15/100;
			else if(familyCount == 4)//加上自己就是5个人为同一家族
				result += result * (double)20/100;
			
			
			if(team.isLeader(this))//是队长的话额外再获得50%
			{
				result += result * (double)LEADEREXP / 100;
			}
		}
		
		
		if(type == 1)
		{
			/**
			 * * 若lv-lm=<1500,a=a
			 * 若1500<lv-lm=<2000,a=a/2
			若2000<lv-lm=<2500,a=a/5
			若lv-lm>2500,a=a/10(20101202修改)
			 */
			int sqLevel = player.level - monsterLevel;
			if(sqLevel > 1500 && sqLevel <= 2000)
			{
				result /= 2;
			}
			else if(sqLevel > 2000 && sqLevel <= 2500)
			{
				result /= 5;
			}
			else if(sqLevel > 2500)
			{
				result /= 10;
			}
		}
		
			
		if(team != null)
		{
			int size = team.getPlayers().length;
			if(size == 2)
			{
				result += result * (double)5 / 100;
			}
			else if(size == 3)
			{
				result += result * (double)10 / 100;
			}
			else if(size == 4)
			{
				result += result * (double)15 / 100;
			}
			else if(size == 5)
			{
				result += result * (double)20 / 100;
			}
		}
		
		//经验卡
		if(player.expMultTime != 0)
		{
			Bag bag = (Bag) player.getExtPlayerInfo("bag");
			Goods goods = bag.getExtGoods(2);
			if(goods != null && goods instanceof GoodsProp)
			{
				GoodsProp prop = (GoodsProp) goods;
				if(WorldManager.currentTime - prop.expTimes < player.expMultTime)
				{
					result *= prop.expMult;
				}
			}
		}

		result *= getExpMult();
	
		if(result <= 1)
			result = 1;

		return result;
	}
	

	
	/**
	 * 发送玩家获得物品信息
	 * @param type 通知类型 1为自己 2 为组队 3为世界 4为战斗
	 * @param isNotice  是否公告
	 * @param getGoodsInfo  具体信息
	 */
	public void sendGetGoodsInfo(int type,boolean isNotice,String getGoodsInfo)
	{
		ByteBuffer buffer = new ByteBuffer();
		if(isNotice)
			buffer.writeByte(12);
		else
			buffer.writeByte(7);
		buffer.writeInt(0);
		buffer.writeUTF("");
		buffer.writeUTF(getGoodsInfo);
		if(type == 1)
		{	
			netConnection.sendMessage(new SMsg(SMsg.S_CHAT_COMMAND,buffer));
		}
		else if(type == 2)
		{
			if(team!= null)
				team.dispatchMsg(SMsg.S_CHAT_COMMAND, buffer);
			else 
				netConnection.sendMessage(new SMsg(SMsg.S_CHAT_COMMAND,buffer));
		}
		else if(type == 3)
		{
			worldManager.dispatchMsg(SMsg.S_CHAT_COMMAND, buffer);
		}
		else if(type == 4)
		{
			getParent().dispatchMsg(SMsg.S_CHAT_COMMAND, buffer);
		}
	}
	
	public void sendAlwaysValue()
	{
		ByteBuffer buffer = new ByteBuffer();
		if(getParent() instanceof BattleController)
			player.sendAlwaysValue(buffer,(PlayerBattleTmp) getAttachment());
		else
			player.sendAlwaysValue(buffer,null);
		if(team == null)
			netConnection.sendMessage(new SMsg(SMsg.S_PLAYER_UPDATE_COMMAND,buffer));
		else
			team.dispatchMsg(SMsg.S_PLAYER_UPDATE_COMMAND, buffer);
	}
	
	
/*	public void moveToRoom(int roomId)
	{
		if(roomId == 0)
			return;
		
		if(roomController == null)
			return;
		
		if(roomController.id == roomId)
			return;
		
		AreaController area = roomController.getParent();
		if(area == null)
			return;
		
		RoomController room = area.getRoomById(roomId);
		if(room == null)
		{
			AreaController [] areas = area.getParent().getAreaControllers();
			for (int i = 0; i < areas.length; i++)
			{
				if(areas[i] == null)
					continue;
				
				room = areas[i].getRoomById(roomId);
				
				if(room != null)
					break;
			}
			
			if(room == null)
			{
				return;
			}
		}
	
		if(!DataFactory.getInstance().checkInRoom(this,room.id))
			return;
	
		
		if(getTeam() == null)
			roomController.sendPlayerMove(room, new PlayerController[]{this});
		else
		{
			roomController.removeTeam(getTeam());
			roomController.sendPlayerMove(room, getTeam().getPlayers());
		}
	}*/
	
	
	public void moveToRoom(int roomId)
	{
		  if(roomId == 0)
		   return;
		  
		  if(roomController == null)
		   return;
		  
		  if(roomController.id == roomId)
		   return;
		  
		  AreaController area = roomController.getParent();
		  if(area == null)
			  return;
		  
		  RoomController room = area.getRoomById(roomId);
		  if(room == null)
		  {
			if(area.getParent()==null)
				return;
		   AreaController [] areas = area.getParent().getAreaControllers();
			if(areas == null)
				return;
		
		   for (int i = 0; i < areas.length; i++)
		   {
		    if(areas[i] == null)
		     continue;
		    
		    room = areas[i].getRoomById(roomId);
		    
		    if(room != null)
		     break;
		   }
		   
		   if(room == null)
		   {
			   return;
		   }
		  }
		 
		  if(!DataFactory.getInstance().checkInRoom(this,room.id))
			  return;
		 
		  
		  if(getTeam() == null)
		   roomController.sendPlayerMove(room, new PlayerController[]{this});
		  else
		  {
		   roomController.removeTeam(getTeam());
		   roomController.sendPlayerMove(room, getTeam().getPlayers());
		  }
	 }

	
	public void setDefaultLocation()
	{
		player.x = 0;
		player.y = 0;
		player.eventPoint = 0;
	}
	/**
	 * 检查单次释放技能的CD时间
	 * @param cdTimer
	 * @return true 表示技能可以用 false表示不可以用
	 */
	public boolean isSkillMaybeUse()
	{
		if(skillStartTime == 0)
			return true;
		
		if(WorldManager.currentTime - skillStartTime >= 4800 - 800)
			return true;
		
		return false;
	}
	
	/**
	 * 
	 * @param skill
	 * @return true 表示技能可以用 false表示不可以用
	 */
	public boolean isSkillUse(Skill skill)
	{
		if(!roomController.isGoldPartyPKRoom && !roomController.isGoldPartyRoom)
		{
			if(skill.type == 1)
				return false;
		}
	
		SkillTome st = (SkillTome) player.getExtPlayerInfo("skillTome");
		List list = st.getActiveSkillByIconId(skill.iconId);
		for (int i = 0; i < list.size(); i++) 
		{
			ActiveSkill as = (ActiveSkill) list.get(i);
			if(as.level > skill.level)
				return false;
		}
		return true;
	}
	
	public boolean isEsc()
	{
		long time = netConnection.getPingTime();

		if(player.escTimer == 0)
		{
			player.escTimer = time;
			return true;
		}
		if(ESCTIME + player.escTimer < time)
		{
			player.escTimer = time;
			return true;
		}
		return false;
	}
	
	/**
	 * 更新角色形象，称号等
	 */
	public void updateRoleInfo()
	{
		ByteBuffer buffer = new ByteBuffer();
		buffer.writeInt(getID());
		buffer.writeInt(player.modelMotionId);
		buffer.writeUTF(player.title);
		OtherExtInfo oei = (OtherExtInfo) player.getExtPlayerInfo("otherExtInfo");
		if("".equals(oei.loverName))
			buffer.writeBoolean(false);
		else
			buffer.writeBoolean(true);
		buffer.writeByte(player.upProfession);
		getRoom().dispatchMsg(SMsg.S_PLAYER_UPATE_ROLEINFO_COMMAND, buffer);
	}
	
//	/**
//	 * 替换宠物
//	 * @param pet
//	 */
//	public void insteadPet(Pets pet)
//	{
//		if(pet.getSkills() == null)
//			return;
//		player.insteadExt(pet);
//		ByteBuffer buffer = new ByteBuffer(64);
//		pet.writeTo(buffer);
//		netConnection.sendMessage(new SMsg(SMsg.S_PLAYER_PET_INFO_COMMAND,buffer));
//	}
	
	
	public void setExtLifeAndMagic()
	{
		EquipSet es = (EquipSet) player.getExtPlayerInfo("equipSet");
		int mhp = es.getTotalAtt("maxHitPoint") + player.maxHitPoint;
		int mmp = es.getTotalAtt("maxMagicPoint") + player.maxMagicPoint;
		
		if(player.extLife == 0)
		{
			if(player.hitPoint <= 1)
				player.hitPoint = (int) (mhp * (double)Player.LIFERATE / 100);
		}
		else
		{	
			player.setHitPoint(0,(getParent() instanceof BattleController)?getAttachment():null);
			
			int life = mhp - player.hitPoint;
			
			Bag bag = (Bag) player.getExtPlayerInfo("bag");
			Goods goods = bag.getExtGoods(0);
			GoodsProp prop = (GoodsProp) goods;
			if(player.extLife > life)
			{
				player.extLife -= life;
				bag.sendExpBuff(this, prop.effect, true,0);
			}
			else
			{
				life = player.extLife;
				player.extLife = 0;
				bag.sendExpBuff(this, prop.effect, false,0);
				bag.setExtGoods(0, null);
			}
			player.setHitPoint(life,(getParent() instanceof BattleController)?getAttachment():null);
		}
		
		if(player.extMagic != 0)
		{
			player.setMagicPoint(0,(getParent() instanceof BattleController)?getAttachment():null);
			int magic = mmp - player.magicPoint;
			Bag bag = (Bag) player.getExtPlayerInfo("bag");
			Goods goods = bag.getExtGoods(1);
			GoodsProp prop = (GoodsProp) goods;
			if(player.extMagic > magic)
			{
				player.extMagic -= magic;
				bag.sendExpBuff(this, prop.effect, true,0);
			}
			else
			{
				magic = player.extMagic;
				player.extMagic = 0;
				bag.sendExpBuff(this, prop.effect, false,0);
				bag.setExtGoods(1, null);
			}
			player.setMagicPoint(magic,(getParent() instanceof BattleController)?getAttachment():null);
		}
	}
	

	
	/**
	 * 元宝开宝箱
	 */
	private void moneyBox(AppMessage msg)
	{
		if(DataFactory.SIMPLE.equals(DataFactory.fontVer))
			return;
		if(getParent() instanceof BattleController)
		{
			sendAlert(ErrorCode.ALERT_CANNOT_BOX_ERROR);
			return;
		}
		
		if(!isPlayBox())
			return;

		int type = msg.getBuffer().readByte();

		if(type == 0)//关闭面板
		{
			if(!isBox)
				return;
//			isBox = false;
			setIsBox(false,0);
		}
		else if(type == 1)//开始准备抽奖,开启面板
		{
			if(isBox)
			{
				if(boxType == 1)
					sendAlert(ErrorCode.ALERT_BOX_OPEN_ERROR);
				else if(boxType == 2)
					sendAlert(ErrorCode.ALERT_PLAYER_IS_MONEYBOX);
				return;
			}
//			isBox = true;
			setIsBox(true,2);
			ByteBuffer buffer = new ByteBuffer();
			buffer.writeByte(1);
			netConnection.sendMessage(new SMsg(SMsg.S_MONEY_BOX_COMMMAND,buffer));
		}
		else
		{
			Bag bag = (Bag) player.getExtPlayerInfo("bag");
			Goods mGoods = bag.getGoodsByType(Goods.MONEYBOXOTHER);
			if(mGoods == null)
			{
				if(bag.money < MONEYBOX)
				{
					sendAlert(ErrorCode.ALERT_BOX_MONEY_NO_ENOUGH);
					return;
				}
			}
	
			List list = DataFactory.getInstance().getBoxDropPropList();
			List rList = new ArrayList(8);
			for (int i = 0; i < list.size(); i++) 
			{
				BoxDropProp bdp = (BoxDropProp) list.get(i);
				if(bdp == null)
					continue;
				if(bdp.boxType == 5)
				{
					rList.add(bdp);
				}
			}
	
			int cr = (int) (Math.random() * 10000) + 1;
			int bdpRate = 0;
			BoxDropProp cb = null;
			int loc = 0;
			//要修改的元宝抽奖
			for (int i = 0; i < rList.size(); i++) 
			{
				BoxDropProp bdp = (BoxDropProp) rList.get(i);
				bdpRate += bdp.rate;
				if(cr <= bdpRate)
				{
					cb = bdp;
					loc = i;
					break;
				}
			}
	
			if(cb == null)
			{
				return;
			}
			Goods tmp = cb.getGoodsByMoney();
			if(tmp == null)
			{
				return;
			}
			
			if(!bag.isCanAddGoodsToBag(tmp))
			{
				sendAlert(ErrorCode.ALERT_BAG_IS_FULL);
				return;
			}

			if(mGoods == null)
				bag.money -= MONEYBOX;
			else
				bag.removeGoods(this, mGoods.objectIndex, 1);
		
			ByteBuffer buffer = new ByteBuffer();
			buffer.writeByte(2);
			buffer.writeInt((int) bag.money);
			tmp.writeTo(buffer);
			buffer.writeInt(tmp.goodsCount);
			netConnection.sendMessage(new SMsg(SMsg.S_MONEY_BOX_COMMMAND,buffer));
			
			bag.sendAddGoods(this, tmp);
			
			String objectIndex = tmp.objectIndex+"";
			if(tmp.repeatNumber > 1)
			{
				Goods goods = bag.getGoodsById(tmp.id);
				if(goods != null)
					objectIndex = goods.objectIndex+"";
			}

			StringBuffer sb = new StringBuffer();
			if(tmp.quality < Goods.NOTICE_QUALITY)
			{
				sb.append(DC.getString(DC.PLAYER_32));
				sb.append(": |[");
				sb.append(tmp.name);
				sb.append("]#p:");
				sb.append(objectIndex);
				sb.append(":");
				sb.append(tmp.quality);
				sb.append(":");
				sb.append(getID());
				sendGetGoodsInfo(1,false, sb.toString());
			}
			else if(!DataFactory.isGoodsNotice(GoodsNotice.NO_NOTICE, tmp))
			{
				sb.append(DC.getMoneyBoxString(this, tmp, objectIndex));
				if(tmp.token >= 500)
				{
					//保存消费记录 类型3 抽奖大于500
					GameServer.getInstance().getWorldManager().getJobObserver().addJob(GameServer.JOB_DATABASEJOB,
							new PayJob(this,3,0,tmp.token,GameServer.getInstance().id+":"+getName()+":"+tmp.name+":"+tmp.goodsCount));
					
					//>500元宝保存一次
					getWorldManager().getJobObserver().addJob(GameServer.JOB_DATABASEJOB,
							new SaveJob(getWorldManager(),getPlayer(),SaveJob.MONEY_EXPENSE_SAVE));
					 
					sendGetGoodsInfo(3,true, sb.toString());//向世界聊天发送信息
				}
				else
				{
					sendGetGoodsInfo(1,false, sb.toString());//不公告
				}
			}
		}
	}
	
	
	
	/**
	 * 满血满蓝复活
	 */
	public void setFullHitAndMagic()
	{
		EquipSet es = (EquipSet) player.getExtPlayerInfo("equipSet");
		int hit = es.getTotalAtt("maxHitPoint") + player.maxHitPoint;
		player.setHitPoint(hit - player.hitPoint,(getParent() instanceof BattleController)?getAttachment():null);
		int magic = es.getTotalAtt("maxMagicPoint") + player.maxMagicPoint;
		player.setMagicPoint(magic - player.magicPoint,(getParent() instanceof BattleController)?getAttachment():null);
	}
	
	
	public void setWeakness()
	{
		PVPInfo pvpInfo = (PVPInfo) player.getExtPlayerInfo("PVPInfo");
		pvpInfo.setWeakness();
	}
	
	public boolean beKilled()
	{
		PVPInfo pvpInfo = (PVPInfo) player.getExtPlayerInfo("PVPInfo");
		return (WorldManager.currentTime - pvpInfo.startCheckTime < PVPInfo.checkTime) && pvpInfo.isWeakness();//return (System.currentTimeMillis() - pvpInfo.startCheckTime < pvpInfo.checkTime) && pvpInfo.isWeakness();
	}
	
	public int getHonour()
	{
		PVPInfo pvpInfo = (PVPInfo) player.getExtPlayerInfo("PVPInfo");
		return pvpInfo.honourPoint;
	}
	
	public void setHonour(int honour)
	{
		PVPInfo pvpInfo = (PVPInfo) player.getExtPlayerInfo("PVPInfo");
		pvpInfo.setHonourPoint(honour);
	}
	
	/**
	 * 身上是否有一个休息状态，有这个状态的时候不能从休息室进PK场
	 * @return
	 */
	public boolean isInPartyPk()
	{
		Bag bag = (Bag) player.getExtPlayerInfo("bag");
		GoodsProp prop = (GoodsProp) bag.getExtGoods(3);
		if(prop == null)
			return true;
		else
		{
			//这里的流水是表示一个时间的，慢慢减，减到0时状态就消失
			return prop.objectIndex <= 0;
		}
	}
	
	private void gmCmdInfo(AppMessage msg)
	{
		int type = msg.getBuffer().readInt();
		ByteBuffer buffer = new ByteBuffer();
		buffer.writeInt(type);
		
		if(!isGmAccount())
		{
			close();
			return;
		}
		
		if(type == 0) //检测是否是GM
		{
			if(isManager())
			{
				buffer.writeByte(1);
			}
			else
			{
				buffer.writeByte(2);
			}
			
			GameServer.getInstance()
			.getJobsObserver().
			addJob(GameServer.JOB_DATABASEJOB, 
			new LogJob(player.accountName, 1,(isManager()?"manager : ":"kefu : ")+ player.name+" - login.. "+netConnection.getIP()));
		}
		else if(type == 1)//查看所有在线玩家列表
		{   

			List players = worldManager.getPlayerList();
			int size = players.size();
		
			//worldManager.getDatabaseAccessor().loadPlayers(0, size*2);

			buffer.writeInt(size);
			
			for (int i = 0; i < size; i++) 
			{
				PlayerController player = (PlayerController) players.get(i);
				
				if(player == null)
					continue;
				
				buffer.writeInt(player.getID());
				buffer.writeUTF(player.getPlayer().accountName);
				buffer.writeUTF(player.getName());
				buffer.writeInt(player.getPlayer().level);
				buffer.writeUTF(player.getNetConnection() == null?"null":player.getNetConnection().getIP());
				buffer.writeBoolean(player.getPlayer().isChat);
				
				Bag bag = (Bag)player.getPlayer().getExtPlayerInfo("bag");
//				buffer.writeInt((int) bag.point);
				WorldManager.sendPoint(buffer, bag.point);
				buffer.writeInt((int) bag.money);
//				buffer.writeInt((int) bag.equipMoney);
			}
			
			
			
			
			
		}
		else if(type == 2)//要禁言的玩家ID集合
		{
			boolean isChat = msg.getBuffer().readBoolean();
			int chatType = msg.getBuffer().readByte();  //byte:0游戏里的id，1用户帐号id,2用户昵称

			if(chatType == 0)
			{
				String ids = msg.getBuffer().readUTF();
				String[] str = Utils.split(ids, ":");
				for (int i = 0; i < str.length; i++) 
				{
					int playerId = Integer.parseInt(str[i]);
					PlayerController target = worldManager.getPlayer(playerId);
					if(target == null)
						continue;

					target.getPlayer().isChat = isChat;
					
					buffer.writeInt(playerId);
					buffer.writeBoolean(target.getPlayer().isChat);
				}
				
				GameServer.getInstance()
				.getJobsObserver().
				addJob(GameServer.JOB_DATABASEJOB, 
				new LogJob(player.accountName, 3,(isManager()?"manager : ":"kefu : ")+ player.name+" - not speak "+ids+" state "+isChat));
			}
			else if(chatType == 1)
			{
				String accounts = msg.getBuffer().readUTF();
				String[] str = Utils.split(accounts, ":");
				for (int i = 0; i < str.length; i++) 
				{
					PlayerController target = worldManager.getPlayerControllerByAccountName(str[i]);
					
					if(target == null)
						continue;

					target.getPlayer().isChat = isChat;
					
					buffer.writeInt(target.getID());
					buffer.writeBoolean(target.getPlayer().isChat);
				}
				
				GameServer.getInstance()
				.getJobsObserver().
				addJob(GameServer.JOB_DATABASEJOB, 
				new LogJob(player.accountName, 3,(isManager()?"manager : ":"kefu : ")+ player.name+" - not speak "+accounts+" state "+isChat));
			}
			else if(chatType == 2)
			{
				String names = msg.getBuffer().readUTF();
				String[] str = Utils.split(names, ":");
				for (int i = 0; i < str.length; i++) 
				{
					PlayerController target = worldManager.getPlayerController(str[i]);
					
					if(target == null)
						continue;

					target.getPlayer().isChat = isChat;
					
					buffer.writeInt(target.getID());
					buffer.writeBoolean(target.getPlayer().isChat);
				}
				
				GameServer.getInstance()
				.getJobsObserver().
				addJob(GameServer.JOB_DATABASEJOB, 
				new LogJob(player.accountName, 3,(isManager()?"manager : ":"kefu : ")+ player.name+" - not speak "+names+" state "+isChat));
			}
			
			
		}
		else if(type == 3)//下面是T单个玩家下线
		{
			String account = msg.getBuffer().readUTF();
			String[] str = Utils.split(account, ":");

			for (int i = 0; i < str.length; i++) 
			{
				PlayerController target = worldManager.getPlayerControllerByAccountName(str[i]);
				
				if(target == null)
				{
					sendError(DC.getString(DC.PLAYER_33));//没找到玩家
					
					NetConnection nc = worldManager.checkPlayerIsOnline(str[i]);
					
					if(nc != null)
					{
						worldManager.onConnectionClosed(nc);
						nc.close();
					}
					continue;
				}
				
				if(!target.isOnline())
				{
					sendError(DC.getString(DC.PLAYER_34));//玩家没在线
				}
				
				if(target.getNetConnection() == null)
				{
					
					MainFrame.println("close is connection null "+str[i]);
					
					worldManager.removePlayerController(target);
					BaseConnection net = new BaseConnection(null);
					net.setInfo(target);
					worldManager.onConnectionClosed(net);
					
					
				}
				else
				{
					worldManager.removePlayerController(target);
					worldManager.onConnectionClosed(target.getNetConnection());
					
					MainFrame.println("close is connection not null "+str[i]);
				}
				
				target.close();
				
				buffer.writeInt(target.getID());
			}
			GameServer.getInstance()
			.getJobsObserver().
			addJob(GameServer.JOB_DATABASEJOB, 
			new LogJob(player.accountName, 2,(isManager()?"manager : ":"kefu : ")+ player.name+" - Tout game "+account));
		}
		else if(type == 4) //发公告
		{
			String chatMsg = msg.getBuffer().readUTF();
			ByteBuffer buff = new ByteBuffer();
			buff.writeByte(8);
			buff.writeUTF(chatMsg);
			processChat(buff);
			
			GameServer.getInstance()
			.getJobsObserver().
			addJob(GameServer.JOB_DATABASEJOB, 
			new LogJob(player.accountName, 4,(isManager()?"manager : ":"kefu : ")+ player.name+" - Tout game "+chatMsg));
			return;
		}
		else if(type == 5) //给指定玩家发放道具、金钱、元宝/欧元
		{
			if(!isGmAccount())
				return;
			
			//String mail = msg.getBuffer().readUTF();//组合成  mail:accountName:point:money:goodsId1:goodsCount1:goodsId2:goodsCount2;
			//gmCmdProcess(mail);
			
			StringBuffer sb = new StringBuffer();
			
			String accountName = msg.getBuffer().readUTF();
			String title = msg.getBuffer().readUTF();
			String content = msg.getBuffer().readUTF();
			int point = msg.getBuffer().readInt();
			int money = msg.getBuffer().readInt();
			int goodsCount =  msg.getBuffer().readByte();
			
			
			sb.append("mail:");
			sb.append(accountName+":");
			sb.append(point+":");
			sb.append(money);
			
			
			PlayerController target = getWorldManager().getPlayerControllerByAccountName(accountName);
			
			Mail mail = new Mail(DC.getString(DC.PLAYER_29));
			mail.setTitle(title.equals("")?"GM":title);
			mail.setContent(content);
			mail.setPoint(point);
			mail.setMoney(money);

			
			for (int i = 0; i < goodsCount; i++)
			{
				int id = msg.getBuffer().readInt();
				int count = msg.getBuffer().readInt();
				
				Goods[] goods = DataFactory.getInstance().makeGoods(id,count);
				
				if(goods == null || goods.length == 0)
					continue;
				
				mail.addAttach(goods[0]);
				
				sb.append(":"+id+":"+count);
			}

			if(target == null)
			{
				mail.sendOffLineWithAccountName(accountName);
			}
			else
			{
				mail.send(target);
			}
	
			GameServer.getInstance()
			.getJobsObserver().
			addJob(GameServer.JOB_DATABASEJOB, // mail:accountName:point:money:goodsId1:goodsCount1:goodsId2:goodsCount2
			new LogJob(player.accountName, 5,(isManager()?"manager : ":"kefu : ")+ player.name+" - sendGoods "+sb.toString()));
		}
		else if(type == 6) //封杀Ip
		{
			String ip = msg.getBuffer().readUTF();
			int state = msg.getBuffer().readInt();
			
			GameServer.getInstance().getWorldManager().changeProIpState(ip,state);
			
			GameServer.getInstance()
			.getJobsObserver().
			addJob(GameServer.JOB_DATABASEJOB, 
			new LogJob(player.accountName, 6,(isManager()?"manager : ":"kefu : ")+ player.name+" - limit ip "+ip+" state : "+state));
		}
		else if(type == 7) //封号
		{
			String account = msg.getBuffer().readUTF();
			int state =  msg.getBuffer().readInt()-1;//1正常 2封号   0正常 1封号
			
			PlayerController player = worldManager.getPlayerControllerByAccountName(account);
			if(player != null)
			{
				player.wgCount = 0xffff;
				player.close();
			}
			
			GameServer.getInstance().getDatabaseAccessor().changeAccountState(account, state);

			buffer.writeUTF(account);
			buffer.writeInt(state);
			
			GameServer.getInstance()
			.getJobsObserver().
			addJob(GameServer.JOB_DATABASEJOB, 
			new LogJob(this.player.accountName, 7,(isManager()?"manager : ":"kefu : ")+ this.player.name+" - fenghao "+account+" state : "+state));
		}
		else if(type == 8)//根据玩家名字查询    在线玩家信息
		{
			String name = msg.getBuffer().readUTF();
			
			if(name.isEmpty() || name.trim().length() == 0)
				return;
			
			PlayerController target = worldManager.getPlayerController(name);
			
			if(target == null)
			{
				sendError(DC.getString(DC.PLAYER_33));//没找到玩家
				return;
			}
			if(!target.isOnline())
			{
				sendError(DC.getString(DC.PLAYER_34));//玩家没在线
				return;
			}
			
			sendAll(buffer, target.getPlayer(),target);
		}
		else if(type == 9) //查询封了的IP盒子
		{
			List list = GameServer.getInstance().getWorldManager().getPIps();

			buffer.writeInt(list.size());
			
			for (int i = 0; i < list.size(); i++)
			{
				String ip = (String)list.get(i);
				buffer.writeUTF(ip);
			}
			
		}
		else if(type == 10) //离线发公告
		{
			int time =  msg.getBuffer().readInt()*1000; //间隔秒数
			int count =  msg.getBuffer().readInt(); //次数
			String chatMsg = msg.getBuffer().readUTF(); 
			
			
			try
			{
				Timer timer  = new Timer(time,count);
				timer.setName("post:"+worldManager.getDatabaseAccessor().getGoodsObjIndex());
				timer.addTimerListener(new Poster(chatMsg,timer));
				timer.start();
				worldManager.postList.add(timer);
			} 
			catch (Exception e)
			{
				System.out.println("post is fail...");
			}
		
			GameServer.getInstance()
			.getJobsObserver().
			addJob(GameServer.JOB_DATABASEJOB, 
			new LogJob(player.accountName, 4,(isManager()?"manager : ":"kefu : ")+ player.name+" - notice "+chatMsg));
		}
		else if(type == 11) //查询所有离线公告
		{
			int size = worldManager.postList.size();
			
			buffer.writeInt(size);
			
			for (int i = 0; i < size; i++)
			{
				Timer timer = (Timer)worldManager.postList.get(i);
				buffer.writeUTF(timer.getName());
				buffer.writeUTF(((Poster)timer.getTimerListener()).getMsgContent());
			}
		}
		else if(type == 12) //停止掉公告
		{
			String objectIndex = msg.getBuffer().readUTF(); 
			
			int size = worldManager.postList.size();
			
			for (int i = 0; i < size; i++)
			{
				Timer timer = (Timer)worldManager.postList.get(i);
				
				if(objectIndex.equals(timer.getName()))
				{
					if(timer.getCount() != -1)
					{
						timer.stop();
						break;
					}
					else
					{
						sendError(DC.getString(DC.PLAYER_35)); //正在删除中
						return;
					}
					
				}
			}
			buffer.writeUTF(objectIndex);
		}
		
		
		//------------------------------------------------------ 20以下的是备份服务器的
		else if(type == 20)//查看每月每日注册人数  count
		{
			if(!GameServer.isBackSever)
				return;
			
			if(!isGmAccount())
				return;
			
			String beginDay = msg.getBuffer().readUTF();//格式2009/07/09
			String endDay = msg.getBuffer().readUTF();//格式2009/07/09
			
			int count = worldManager.getDatabaseAccessor().queryRegCount(beginDay,endDay);
			buffer.writeInt(count);
		}
		else if(type == 21)// 查看每月每日在线（每天每小时取一次在线数值/24=每天在线，本月每天在线合计/本月天数=每月在线）
		{
			if(!GameServer.isBackSever)
				return;
			
			if(!isGmAccount())
				return;
			
			String beginDay = msg.getBuffer().readUTF();//格式20090709
			String endDay = msg.getBuffer().readUTF();//格式20090709
			
			int count = worldManager.getDatabaseAccessor().queryAvgCount(beginDay, endDay);
			buffer.writeInt(count);
		}
		else if(type == 22)//查看所有玩家信息， 包括离线
		{
			if(!GameServer.isBackSever)
				return;
			
			if(!isGmAccount())
				return;
	
			int begin = msg.getBuffer().readInt();
			int end = msg.getBuffer().readInt();
			
			List list = worldManager.getDatabaseAccessor().loadPlayers(begin, end);
			
			int size = list.size();
			buffer.writeInt(size);

			for (int i = 0; i < size; i++)
			{
				Player player = (Player)list.get(i);
				
				buffer.writeInt(player.id);
				buffer.writeUTF(player.accountName);
				buffer.writeUTF(player.name);
				buffer.writeInt(player.level);
				buffer.writeBoolean(player.isChat);
				
				Bag bag = (Bag)player.getExtPlayerInfo("bag");
//				buffer.writeInt((int) bag.point);
				WorldManager.sendPoint(buffer, bag.point);
				buffer.writeInt((int) bag.money);
//				buffer.writeInt((int) bag.equipMoney);
			}

		}
		else if(type == 23)//查看    离线玩家信息 通过账号
		{
			if(!GameServer.isBackSever)
				return;
			
			if(!isGmAccount())
				return;
			
			String accountName = msg.getBuffer().readUTF();
			
			Player player= worldManager.getDatabaseAccessor().getPlayer(accountName.trim());
		
			if(player != null)
			{
				sendAll(buffer, player,null);
			}
			System.out.println("Query With OffLine : "+accountName.trim()+"    Player : "+player);
		}
		else if(type == 24)//查看每月每日注册人数  list
		{
			if(!GameServer.isBackSever)
				return;
			
			if(!isGmAccount())
				return;
			
			String beginDay = msg.getBuffer().readUTF();//格式20090709
			String endDay = msg.getBuffer().readUTF();//格式20090709
			
			
			//ByteBuffer regBuff  =  worldManager.getDatabaseAccessor().queryRegList(currentDay,isDay);
			//buffer.writeBytes(regBuff.getBytes());
		}
		else if(type == 25)//查看每月每日平均在线  list
		{
			if(!GameServer.isBackSever)
				return;
			
			if(!isGmAccount())
				return;
			
			String beginDay = msg.getBuffer().readUTF();//格式20090709
			String endDay = msg.getBuffer().readUTF();//格式20090709
			
			
			ByteBuffer regBuff  =  worldManager.getDatabaseAccessor().queryAvgList(beginDay, endDay);
			buffer.writeBytes(regBuff.getBytes());
		}
		else if(type == 26)// 查询当天玩家的 最大的在线
		{
			if(!GameServer.isBackSever)
				return;
			
			if(!isGmAccount())
				return;
			
			String beginDay = msg.getBuffer().readUTF();//格式20090709
			String endDay = msg.getBuffer().readUTF();//格式20090709
			
			int count = worldManager.getDatabaseAccessor().queryMaxCount(beginDay, endDay);
			buffer.writeInt(count);
		}
		else if(type == 27)// 回删除档
		{
			if(!GameServer.isBackSever)
				return;
			
			if(!isGmAccount())
				return;
			
			String accountName = msg.getBuffer().readUTF();

			int result = worldManager.getDatabaseAccessor().returnDeleteData(accountName);
			
			buffer.writeInt(result);
			
			GameServer.getInstance()
			.getJobsObserver().
			addJob(GameServer.JOB_DATABASEJOB, 
			new LogJob(this.player.accountName, 8,(isManager()?"manager : ":"kefu : ")+ this.player.name+" - "+DC.getString(DC.BASE_14)+"ip "+accountName));
		}
		else if(type == 28)//查看每月每日最大在线  list
		{
			if(!GameServer.isBackSever)
				return;
			
			if(!isGmAccount())
				return;
			
			String beginDay = msg.getBuffer().readUTF();//格式20090709
			String endDay = msg.getBuffer().readUTF();//格式20090709
			
			ByteBuffer regBuff  =  worldManager.getDatabaseAccessor().queryMaxList(beginDay, endDay);
			buffer.writeBytes(regBuff.getBytes());
		}
		else if(type == 29)// 回备份档
		{
			if(!GameServer.isBackSever)
				return;
			
			if(!isGmAccount())
				return;
			
			String accountName = msg.getBuffer().readUTF();
			int bakIndex = msg.getBuffer().readByte()+1; // 星期天 0 星期一 1.......星期六6

			int result = worldManager.getDatabaseAccessor().returnBakData(bakIndex, accountName);
			
			buffer.writeInt(result);
			
			
			GameServer.getInstance()
			.getJobsObserver().
			addJob(GameServer.JOB_DATABASEJOB, 
			new LogJob(this.player.accountName, 8,(isManager()?"manager : ":"kefu : ")+ this.player.name+" - "+DC.getString(DC.BASE_14)+"ip "+accountName+":"+result));
		}
		else if(type == 30)//查看每月每日支付金额
		{
			if(!GameServer.isBackSever)
				return;
			
			if(!isGmAccount())
				return;
			
			String beginDay = msg.getBuffer().readUTF();//格式2009/07/09
			String endDay = msg.getBuffer().readUTF();//格式2009/07/09
			
			
			int [] res =  worldManager.getDatabaseAccessor().queryPayCount(beginDay, endDay);
			buffer.writeInt(res[0]);// 总数量
			buffer.writeInt(res[1]);//消费笔数
		}
		else if(type == 31)//查看每月每日支付金额
		{
			if(!GameServer.isBackSever)
				return;
			
			if(!isGmAccount())
				return;
			
			String beginDay = msg.getBuffer().readUTF();//格式2009/07/09
			String endDay = msg.getBuffer().readUTF();//格式2009/07/09
			int t = msg.getBuffer().readByte();// 1充值 2购物 3抽奖>500 4.4399手机包月  8.礼券兑换 9卖出大于2000
			
			ByteBuffer buff =  worldManager.getDatabaseAccessor().queryPayList(beginDay,endDay,t);
			buffer.writeBytes(buff.getBytes());
		}
		else if(type == 32)//通过昵称  查看    离线玩家信息
		{
			if(!GameServer.isBackSever)
				return;

			if(!isGmAccount())
				return;

			String playerName = msg.getBuffer().readUTF();
			
			Player player= worldManager.getDatabaseAccessor().getPlayerByPlayerName(playerName);

			if(player != null)
			{
				sendAll(buffer, player,null);
			}
			
		}
		else if(type == 33) // 查询日志类型
		{
			if(!GameServer.isBackSever)
				return;
		
			if(!isGmAccount())
				return;
			

			type = msg.getBuffer().readByte();
			String beginDay = msg.getBuffer().readUTF();//格式2009/07/09
			String endDay = msg.getBuffer().readUTF();//格式2009/07/09
		
			ByteBuffer buff =  worldManager.getDatabaseAccessor().queryServerLogList(player.accountName,type, beginDay, endDay);
			buffer.writeBytes(buff.getBytes());
		}
		else if(type == 34)
		{
			if(!isGmAccount())
				return;
			
			//String mail = msg.getBuffer().readUTF();//组合成  mail:accountName:point:money:goodsId1:goodsCount1:goodsId2:goodsCount2;
			//gmCmdProcess(mail);
			
			StringBuffer sb = new StringBuffer();
			
			String accountName = msg.getBuffer().readUTF();
			String title = msg.getBuffer().readUTF();
			String content = msg.getBuffer().readUTF();
			int point = msg.getBuffer().readInt();
			int money = msg.getBuffer().readInt();
			int goodsCount =  msg.getBuffer().readByte();
			
			
			sb.append("mail:");
			sb.append(accountName+":");
			sb.append(point+":");
			sb.append(money);
			
			
			PlayerController target = getWorldManager().getPlayerControllerByAccountName(accountName);
			
			Mail mail = new Mail(DC.getString(DC.PLAYER_29));
			mail.setTitle(title.equals("")?"GM":title);
			mail.setContent(content);
			mail.setPoint(point);
			mail.setMoney(money);

			Shop shop = worldManager.getShop(Shop.TYPE_MONEY_SHOP);
			if(shop == null)
				return;
			Shop vShop = worldManager.getShop(Shop.TYPE_VIP_MONEY_SHOP);
			
			for (int i = 0; i < goodsCount; i++)
			{
				int id = msg.getBuffer().readInt();
				int count = msg.getBuffer().readInt();
				Goods g = shop.getGoods(id);
				if(g == null)
				{
					if(vShop != null)
					{
						g = vShop.getGoods(id);
					}
				}
				if(g == null)
					return;
				Goods[] goods = DataFactory.getInstance().makeGoods(id, count, g.quality);
				
				if(goods == null || goods.length == 0)
					continue;
				
				mail.addAttach(goods[0]);
				
				sb.append(":"+id+":"+count);
			}

			if(target == null)
			{
				mail.sendOffLineWithAccountName(accountName);
			}
			else
			{
				mail.send(target);
			}
			
			GameServer.getInstance()
			.getJobsObserver().
			addJob(GameServer.JOB_DATABASEJOB, // mail:accountName:point:money:goodsId1:goodsCount1:goodsId2:goodsCount2
			new LogJob(player.accountName, 5,(isManager()?"manager : ":"kefu : ")+ player.name+" - sendGoods "+sb.toString()));
		}
		

		netConnection.sendMessage(new SMsg(SMsg.S_GM_POST_COMMAND,buffer));
	}


	private void sendAll(ByteBuffer buffer, Player player,PlayerController target)
	{
		buffer.writeUTF(player.accountName);
				
		if(target == null)
		{
			EquipSet es = (EquipSet) player.getExtPlayerInfo("equipSet");
			es.writeTo(buffer);
		}
		else
		{
			if(target.getParent() instanceof BattleController)
			{
				PlayerBattleTmp pbt = (PlayerBattleTmp) target.getAttachment();
				pbt.writeTo(buffer);
			}
			else
			{
				EquipSet es = (EquipSet) player.getExtPlayerInfo("equipSet");
				es.writeTo(buffer);
			}
		}
		
		Bag bag = (Bag)player.getExtPlayerInfo("bag");
		bag.writeTo(buffer);
		
		Storage storage = (Storage)player.getExtPlayerInfo("storage");
		storage.writeTo(buffer);
		
		PetTome pt = (PetTome)player.getExtPlayerInfo("petTome");
		buffer.writeInt(player.id);
		pt.writeTo(buffer);
		Pet pet = pt.getActivePet();
		if(player.id == getID())
		{
			long time = WorldManager.currentTime-pet.trainTime;//long time = System.currentTimeMillis()-pet.trainTime;
			buffer.writeByte(pet.trainState);
			int downTime = 0;
			if(time < pet.gameTime)
				downTime = (int) (pet.gameTime - time);
			buffer.writeInt(downTime);
		}
		else
		{
			buffer.writeByte(pet.trainState);
			buffer.writeInt(0);
		}
		
		MailBox mb = (MailBox)player.getExtPlayerInfo("mailbox");
		mb.writeWithGm(buffer);
		
		Pet[] pets = pt.getPets();
		int count = 0;
		for (int i = 0; i < pets.length; i++) 
		{
			if(pets[i] == null)
				continue;
			if(pets[i].petType != Pet.SHOUHUPET)
				continue;
			count++;
		}
		buffer.writeByte(count);
		for (int i = 0; i < pets.length; i++) 
		{
			if(pets[i] == null)
				continue;
			if(pets[i].petType != Pet.SHOUHUPET)
				continue;
			pets[i].writeBattlePetTo(buffer);
			pets[i].writeSkill(buffer);
		}
	}
	
	private void sendPlayerEquipSet(AppMessage msg)
	{
		String playerName = msg.getBuffer().readUTF();
		
		PlayerController target = null;
		if(playerName.equals(getName()))
			target = this;
		else
		{
			if(!(getParent() instanceof RoomController))
				return;
			
			target =((RoomController)getParent()).getPlayer(playerName);//.getPlayer(playerId);
			
			if(target == null)
				target = worldManager.getPlayerController(playerName);
		}
		
		if(target == null)
			return;

		Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
		
		PlayerController [] targets  = new PlayerController[2];
		targets[0] = target;
		targets[1] = this;
		bag.sendPlayerEquipSet(targets);
		
	}
	
	
	private void getTasks(AppMessage msg)
	{
		ByteBuffer buffer = new ByteBuffer(32);

		if(msg.getBuffer().readByte() == 0)//0 自己的  1可接
		{
			TaskInfo taskInfo = (TaskInfo)player.getExtPlayerInfo("taskInfo");
			buffer.writeByte(0);
			taskInfo.writeTo(buffer);
			netConnection.sendMessage(new SMsg(SMsg.S_GET_TASKS_COMMAND,buffer));
			
			taskInfo.checkAllTask(this);

		}
		else
		{
			TaskManager.getInstance().writeCandoTasks(this);
		}
	}
	
	
	private void cancelTask(AppMessage msg)
	{
		int id = msg.getBuffer().readInt();
		
		TaskInfo taskinfo = (TaskInfo)player.getExtPlayerInfo("taskInfo");
		
		if(!taskinfo.cancelTask(id))
		{
			sendAlert(ErrorCode.ALERT_TASK_NOT_CANCEL);
			return;
		}
		ByteBuffer buffer = new ByteBuffer(4);
		buffer.writeInt(id);
		netConnection.sendMessage(new SMsg(SMsg.S_CANCEL_TASKS_COMMAND,buffer));
	}
	
	
	private void autoSkillTome(AppMessage msg)
	{
		int []ids = new int[6];
		
		for (int i = 0; i < ids.length; i++)
		{
			ids[i] = msg.getBuffer().readInt();
		}
		
		SkillTome st = (SkillTome)player.getExtPlayerInfo("skillTome");
		st.setAutoSkill(ids);
	}
	
	/**
	 * 喂养宠物
	 * @param msg
	 */
	private void feedPet(AppMessage msg)
	{
		if(getParent() instanceof BattleController)
		{
			sendError(DC.getString(DC.PET_15));
			return;
		}
		if(isPetUp)
		{
			sendGetGoodsInfo(1, false, DC.getString(DC.PLAYER_30));
			return;
		}
		if(WorldManager.isZeroMorning(0))
		{
			sendAlert(ErrorCode.ALERT_SYSTEM_IS_UPDATE);
			return;
		}
		int goodsId = msg.getBuffer().readInt();
		Object obj = DataFactory.getInstance().getGameObject(goodsId);
		if(obj == null)
		{
			sendAlert(ErrorCode.ALERT_GOODS_NOT_EXIST);
			return;
		}
		if(!(obj instanceof GoodsProp))
		{
			sendAlert(ErrorCode.ALERT_GOODS_NOT_PETFOOD);
			return;
		}
		GoodsProp goods = (GoodsProp) obj;
		GoodsProp prop = (GoodsProp) Goods.cloneObject(goods);
		prop.objectIndex = GameServer.getInstance().getDatabaseAccessor().getGoodsObjIndex();
		if(prop.type != 13)
		{
			sendAlert(ErrorCode.ALERT_GOODS_NOT_PETFOOD);
			return;
		}
		
		Bag bag = (Bag) player.getExtPlayerInfo("bag");
		if(bag.point < prop.point && bag.money < prop.money)
		{
			sendAlert(ErrorCode.ALERT_SHOP_NO_POINT);
			return;
		}
		
		if(bag.point < prop.point)
		{
			sendAlert(ErrorCode.ALERT_SHOP_NO_POINT);
			return;
		}
		
		if(bag.money < prop.money)
		{
			sendAlert(ErrorCode.ALERT_SHOP_NO_MONEY);
			return;
		}
		
		PetTome pets = (PetTome) player.getExtPlayerInfo("petTome");
		Pet pet = pets.getActivePet();
		if(pet == null)
			return;

		
		if(!pet.isFeed())
		{
			sendAlert(ErrorCode.ALERT_FEED_PET_COUNT_OVER);
			return;
		}

		
		long exp = prop.expPoint;
	
		int rInt = prop.intPoint;
		
		if(exp > 0 && rInt > 0)
		{
			System.out.println("exp and inti all more than zero!");
			return;
		}

		StringBuffer sb = null;
		boolean expFlag = false;
		if(rInt > 0)
		{
			if(pet.isMaxInti(1))
			{
				sb = new StringBuffer();
				sb.append(DC.getString(DC.PLAYER_36));//你的宠物亲密度已达最大值
				sendGetGoodsInfo(1,false,sb.toString());
				return;
			}
			long bInt = pet.intimacyPoint;
			pet.setInti(rInt, this);
			long aInt = pet.intimacyPoint;
			long addInt = aInt - bInt;
			if(addInt > 0)
			{
				sb = new StringBuffer();
				sb.append(getName());
				sb.append(DC.getString(DC.PLAYER_37));
				sb.append("[");
				sb.append(prop.name);
				sb.append("],");
				sb.append(DC.getString(DC.PLAYER_38));
				sb.append(addInt);
				sb.append(DC.getString(DC.PLAYER_39));
				sendGetGoodsInfo(2,false,sb.toString());
			}
			else
				return;
		}
		else if(exp > 0)
		{
			if(pet.isMaxInti(2))
			{
				sb = new StringBuffer();
				sb.append(DC.getString(DC.PLAYER_40));//你的宠物等级已达最大值
				sendGetGoodsInfo(1,false,sb.toString());
				return;
			}
			long bExp = pet.experience;
			expFlag = addPetExp(exp);
			long aExp = pet.experience;
			long addExp = aExp - bExp;
			if(expFlag && addExp > 0)
			{
				sb = new StringBuffer();
				sb.append(getName());
				sb.append(DC.getString(DC.PLAYER_37));
				sb.append("[");
				sb.append(prop.name);
				sb.append("],");
				sb.append(DC.getString(DC.PLAYER_38));
				sb.append(addExp);
				sb.append(DC.getString(DC.PLAYER_41));
				sendGetGoodsInfo(2,false,sb.toString());
			}
			else
				return;
		}
		
		bag.point -= prop.point;
		bag.money -= prop.money;
		bag.sendAddGoods(this, null);
		
		pet.setFeed();

		sendPetInfo(this, getID());
	}

	
	/**
	 * 设置阵营
	 * @param msg
	 */
	private void setCamp(AppMessage msg)
	{
		if(player.level < UPCAMPLEVEL)
		{
			sendAlert(ErrorCode.ALERT_LEVEL_LESS_CAMP_LEVEL);
			return;
		}
		if(player.camp != 0)
		{
			sendAlert(ErrorCode.ALERT_PLAYER_CAMP_NOT_NULL);
			return;
		}
		int camp = msg.getBuffer().readByte();
		if(camp != 1 && camp != 2)
		{
			sendAlert(ErrorCode.ALERT_NOT_THE_CAMP);
			return;
		}
		FamilyController family = getFamily();
		if(family != null)
		{
			if(camp != family.camp && family.camp != 0)
			{
				sendAlert(ErrorCode.ALERT_FAMILY_CAMP_ERROR);
				return;
			}
		}
		player.setCamp(camp);
		ByteBuffer buffer = new ByteBuffer(5);
		buffer.writeByte(camp);
		buffer.writeInt(getID());//选择了阵营才发这个ID
		getRoom().dispatchMsg(SMsg.S_PLAYER_CAMP_SET_COMMAND, buffer);
		
		if(getTeam() != null)
		{
			roomController.deleteTeam(getTeam());
			
			getTeam().playerLeaveTeam(this);
		}
		
		isCamp = false;
		
		checkPetChangeModel();
	}
	

	public void onPlayerKillMonster(MonsterController monster)
	{
		TaskInfo taskInfo = (TaskInfo)getPlayer().getExtPlayerInfo("taskInfo");
		taskInfo.onPlayerKillMonster(monster,this);
		
		player.eventPoint += monster.getMonster().eventPoint;
		
		if(roomController == null)
			return;

		if(roomController.getEventPoint() == 0)
			return;
		
/*		if(player.eventPoint > (roomController.getEventPoint()*2))
		{
			return;
		}*/
		


		ByteBuffer buffer = new ByteBuffer(4);
		buffer.writeInt(player.eventPoint);
		getNetConnection().sendMessage(new SMsg(SMsg.S_PLAYER_UPDATEEVENTPOINT_COMMAND,buffer));
	}
	
	
	public void checkVisibleExit()
	{

		//通知客户端点数到了 可以显示GO....
		TeamController team = getTeam();
		
		boolean isNotify = true;

		if(team != null)
		{
			 PlayerController[] everyone = team.getPlayers();
			 for (int i = 0; i < everyone.length; i++)
			{
				 if(everyone[i] == null)
					 continue;
				 
				 if(everyone[i].getPlayer().eventPoint < roomController.getEventPoint())
					 isNotify = false;
			}
		}
		else
		{
			 if(getPlayer().eventPoint < roomController.getEventPoint())
				 isNotify = false;
		}
		
		if(isNotify && roomController.isVisiable())
		{
			if(team != null)
			{
				ByteBuffer buffer = new ByteBuffer(1);
				team.dispatchMsg(SMsg.S_PLAYER_VISIABLE_COMMAND, buffer);
			}
			else
			{
				ByteBuffer buffer = new ByteBuffer(1);
				netConnection.sendMessage(new SMsg
						(SMsg.S_PLAYER_VISIABLE_COMMAND,buffer)); 
			}
		}
	}
	
	public void onTaskFinish(Task task)
	{
		MailRemind.onCheckMail(this, MailRemind.ONTASKOVER, task.id);
	}
	
	public int getEquipPoint(String str)
	{
		EquipSet es = (EquipSet) player.getExtPlayerInfo("equipSet");
		int result = 0;
		result = Integer.parseInt(player.getBaseInfo().getVariable(str)) + es.getTotalAtt(str);
		if("phySmiteHurtParm".equals(str) || "phsSmiteHurtParm".equals(str) || "sptSmiteHurtParm".equals(str))
			result += (int)(SpriteBattleTmp.SMITERATE*10000);
		return result;
	}
	
	
	public void setSkillProcessTime()
	{
		SkillTome st = (SkillTome) player.getExtPlayerInfo("skillTome");
		st.setZeroToProcessTimer();
		skillStartTime = 0;
		skillNeedTime = 0;
	}
	
	public void sendActivePoint()
	{
		if(roomController == null)
			return;
		ByteBuffer buffer = new ByteBuffer(9);
		int type = roomController.getActivePointType();
		buffer.writeByte(type);
		buffer.writeInt(getID());
		if(type == 1)
		{
			buffer.writeInt(getPlayer().flyActivePoint);
			buffer.writeInt(Player.FLYACTIVEPOINTDEFAULT);
			buffer.writeInt(0);
		}
		else if(type == 2)
		{
			buffer.writeInt(getPlayer().bossActivePoint);
			buffer.writeInt(Player.BOSSACTIVEPOINTDEFAULT);
			buffer.writeInt(0);
		}
		else
		{
			buffer.writeInt(getPlayer().timeCopyActivePoint);
			buffer.writeInt(Player.TIMECOPYACTIVEPOINTDEFAULT);
			Bag bag = (Bag) player.getExtPlayerInfo("bag");
			buffer.writeInt(bag.getGoodsCount(Goods.TIMESKYTICKET));//船票数量
		}
		if(team != null)
			team.dispatchMsg(SMsg.S_PLAYER_SEND_ACTIVEPOINT_FROM_ROOM_COMMAND, buffer);
		else
			netConnection.sendMessage(new SMsg(SMsg.S_PLAYER_SEND_ACTIVEPOINT_FROM_ROOM_COMMAND,buffer));
	}
	
	public void sendFlyActivePoint()
	{
		ByteBuffer buffer = new ByteBuffer(4);
		buffer.writeInt(getPlayer().flyActivePoint);
		getNetConnection().sendMessage(new SMsg(SMsg.S_PLAYER_UPDATEACTIVEPOINT_COMMAND,buffer));

		sendActivePoint();
	}
	
	public double getExpMult()
	{
		if(!DataFactory.isIdcard)
			return 1;
		OtherExtInfo oei = (OtherExtInfo) player.getExtPlayerInfo("otherExtInfo");
		if(oei.isValiIdcard)
			return 1;
		if(oei.onlineTime < SAFETIME)
			return 1;
		else if(oei.onlineTime < UNSAFETIME)
			return 0.5;
		else 
			return 0; 
	}
	
	
	public void setOtherExtInfo(String key,String value,RoomController room)
	{
		OtherExtInfo oei = (OtherExtInfo) player.getExtPlayerInfo("otherExtInfo");
		int num = DataFactory.getInstance().getCopyNum(room.getParent().id);
		if(num == -1)
		{
			MainFrame.println("PlayerController setOtherExtInfo copy is not exits areaId:"+room.getParent().id+" playerName:"+getName());
			return;
		}
		oei.setVariable(key,value,num);
	}
	
	/**
	 * 取玩家副本相关的值
	 * @param key 
	 * @param flag true is query all copy point
	 * @return
	 */
	public String getOtherExtInfo(String key,boolean flag,RoomController room)
	{
		OtherExtInfo oei = (OtherExtInfo) player.getExtPlayerInfo("otherExtInfo");
		if(flag)//查所有副本中玩家的积分总和
		{
			return oei.copyPoints + "";
		}
		else
		{
			int num = DataFactory.getInstance().getCopyNum(room.getParent().id);
			if(num == -1)
			{
				MainFrame.println("PlayerController getOtherExtInfo copy is not exits areaId:"+room.getParent().id+" playerName:"+getName());
				return -1+"";
			}
			return oei.getVariable(key,num);
		}
	}
	
	
	/**
	 * 修正基础属性
	 * type 1表示是做跟装备相关操作 0表示其它
	 */
	public void fixPlayerBaseInfo(int type)
	{
		ReviseBaseInfo rbi = player.getBaseInfo().getReviseBaseInfo();
		
		rbi.fix(this);
		
		player.uptateBuffPoint(type);
		
		player.setHitPoint(0, null);
		player.setMagicPoint(0, null);

		sendAlwaysValue();
	}
	
	
	/**
	 * 修改阵营
	 * @param buffer
	 */
	public void modifyCamp(ByteBuffer inBuffer)
	{
		if(player.camp == 0)
		{
			sendAlert(ErrorCode.ALERT_CAMP_NOT_MODIFY);
			return;
		}
		int mon = Integer.parseInt(WorldManager.getTypeTime("MM", WorldManager.currentTime));
		if(player.injury != 0 && player.injury == mon)
		{
			sendAlert(ErrorCode.ALERT_MODIFY_CAMP_OR_NAME_OVER);//修改次数用完了
			return;
		}
		if(family != null)
		{
			sendAlert(ErrorCode.ALERT_NO_FAMILY_CAMP);//请选择跟家族阵营一样
			return;
		}
		
		Bag bag = (Bag) player.getExtPlayerInfo("bag");
		if(bag.money < CAMPMONEY)
		{
			sendAlert(ErrorCode.ALERT_SHOP_NO_MONEY);
			return;
		}
		bag.money -= CAMPMONEY;
		bag.sendAddGoods(this, null);
		
		int camp = player.camp==1?2:1;
		player.setCamp(camp);
		
		ByteBuffer buffer = new ByteBuffer(5);
		buffer.writeByte(camp);
		buffer.writeInt(getID());
		getRoom().dispatchMsg(SMsg.S_PLAYER_CAMP_SET_COMMAND, buffer);
		
		if(getTeam() != null)
		{	
			roomController.deleteTeam(getTeam());
			
			getTeam().playerLeaveTeam(this);
		}
		
		player.injury = mon;
	}
	
	/**
	 * 修改名字
	 * @param inBuffer
	 */
	public void modifyName(ByteBuffer inBuffer)
	{
		if(ShopCenter.getInstance().isGoodsInShopCenter(this))
		{	
			sendAlert(ErrorCode.ALERT_MODIFY_NAME_SHOPCENTER_ERROR);
			return;
		}
		int mon = Integer.parseInt(WorldManager.getTypeTime("MM", WorldManager.currentTime));
		if(player.cnMonth != 0 && player.cnMonth == mon)
		{
			sendAlert(ErrorCode.ALERT_MODIFY_CAMP_OR_NAME_OVER);//  一个月只能修改一次
			return;
		}
		
		Bag bag = (Bag) player.getExtPlayerInfo("bag");
		Goods goods = bag.getGoodsByType(Goods.CHANGENAMECARD);
		if(goods == null)
		{
			sendAlert(ErrorCode.ALERT_NO_CHANGE_NAME_GOODS);
			return;
		}
		String roleName = inBuffer.readUTF();
		roleName = MarkChar.replace(roleName);
		
		if(!UseChar.isCanReg(roleName))
		{
			sendAlert(ErrorCode.ALERT_HUOXINGWEN);
			return;
		}
		
		if(roleName.equals(getName()))
			return;
		
		if(family != null)
		{
			sendAlert(ErrorCode.ALERT_FIRST_FAMILY_OUT);
			return;
		}
		
		OtherExtInfo oei = (OtherExtInfo) player.getExtPlayerInfo("otherExtInfo");
		if(oei.loverId != 0 || !"".equals(oei.loverName))
		{	
			sendAlert(ErrorCode.ALERT_MARRY_ERROR_CAHNGENAME);
			return;
		}
		
		if(roleName.indexOf("select") !=-1 || roleName.indexOf("update") !=-1
		   || roleName.indexOf("insert") !=-1 || roleName.indexOf("into") !=-1
		   || roleName.indexOf("delete") !=-1 || roleName.indexOf("order") !=-1//by
		   || roleName.indexOf("from") !=-1 || roleName.indexOf("where") !=-1
		   || roleName.indexOf("by") !=-1 || roleName.indexOf("group") !=-1
		   || roleName.indexOf("\\") !=-1)
		{
			sendAlert(ErrorCode.ALERT_NOT_USE_NAME);
			return;
		}
		if(roleName.trim().length() == 0)
		{
			sendAlert(ErrorCode.ALERT_ROLENAME_NULL_ERROR);
			return;
		}
		if(roleName.trim().length() > 14)
		{
			sendAlert(ErrorCode.ALERT_NAME_LENGTH_OVER);
			return;
		}
		if(worldManager.getDatabaseAccessor().checkPlayerName(roleName.trim()))
		{
			sendAlert(ErrorCode.EXCEPTION_LOGIN_SAMENAME);
			return;
		}

		player.name = roleName;
		
		bag.removeGoods(this, goods.objectIndex, 1);
		
		player.cnMonth = (byte) mon;
		
		ByteBuffer buffer = new ByteBuffer(1);
		buffer.writeByte(1);//提示客户端
		getNetConnection().sendMessage(new SMsg(SMsg.S_PLAYER_MODIFY_NAME_COMMAND,buffer));
		
		worldManager.getJobObserver().addJob(GameServer.JOB_DATABASEJOB,
				new SaveJob(worldManager,player,SaveJob.NAME_MODIFY_SAVE));
		
		close();
	}
	
	/**
	 * 设置组队状态
	 * @param inBuffer
	 */
	public void setTeamState(ByteBuffer inBuffer)
	{
		if(getParent() instanceof BattleController)
			return;
		if(roomController.isCopyPartyRoom)
		{
			if(roomController.getCopy() != null && !roomController.getCopy().isTeam)
					return;
		}
		if(roomController.isGoldPartyRoom || roomController.isGoldPartyPKRoom)
			return;
		
		int state = inBuffer.readByte();
		if(teamState == state)
			return;
		teamState = state;
		
		ByteBuffer buffer = new ByteBuffer();
		buffer.writeInt(getID());
		buffer.writeByte(teamState);
		for (int i = 0; i < roomController.getPlayerList().size(); i++)
		{
			PlayerController player = (PlayerController) roomController.getPlayerList().get(i);
			if(player == null || !player.isOnline() || !player.isLookTeam)
				continue;
			player.getNetConnection().sendMessage(new SMsg(SMsg.S_PLAYER_SET_TEAM_STATE_COMMAND,buffer));
		}
	}
	
	/**
	 * 申请加入队伍
	 * @param inBuffer
	 */
	public void applyTeam(ByteBuffer inBuffer)
	{
		if(roomController.isCopyPartyRoom)
		{
			if(roomController.getCopy() != null && !roomController.getCopy().isTeam)
				return;
		}
		
		if(team != null)
		{
			sendAlert(ErrorCode.ALERT_PLAYER_TEAM_ISNOT_NULL);
			return;
		}
		if(teamState == 1)
		{
			sendAlert(ErrorCode.ALERT_PLAYER_TEAM_STATE_ERROR);
			return;
		}
		int leaderId = inBuffer.readInt();
		TeamController team = roomController.getTeamById(leaderId);
		if(team == null)
		{
			sendAlert(ErrorCode.ALERT_PLAYER_TEAM_NO_EXITS);
			return;
		}
		PlayerController leader = team.getLeader();
		if(leader == null)
		{
			sendAlert(ErrorCode.ALERT_PLAYER_NOTAT_ROOM);
			return;
		}
		if(leader.isAuto)
		{
			sendAlert(ErrorCode.ALERT_TARGET_PLAYER_AUTO_BATTLEING);
			return;
		}
		if(!team.isLeader(leader))
		{
			sendAlert(ErrorCode.ALERT_PLAYER_ISNOT_TEAM_LEADER);
			return;
		}
		if(team.isMaxApply())
		{
			sendAlert(ErrorCode.ALERT_PLAYER_TEAM_APPLY_ISMAX);
			return;
		}
		if(team.getApplyById(getID()) != null)
		{
			sendAlert(ErrorCode.ALERT_PLAYER_APPLY_IS_EXITS);
			return;
		}
		if(roomController.isPartyRoom() || roomController.isPartyPKRoom())
		{
			if(FamilyPartyController.getInstance().isReady())
			{
				if(player.familyId != leader.getPlayer().familyId)
				{
					sendAlert(ErrorCode.ALERT_DIFF_FAMILY_ERROR);
					return;
				}
			}
		}
		
		
		leader.getTeam().addApply(this);
		
		ByteBuffer buffer = new ByteBuffer();
		buffer.writeInt(getID());
		buffer.writeInt(getPlayer().level);
		buffer.writeUTF(getName());
		buffer.writeByte(getPlayer().upProfession);
		leader.getNetConnection().sendMessage(new SMsg(SMsg.S_PLAYER_APPLY_TEAM_COMMAND,buffer));
	
		sendError(DC.getString(DC.PLAYER_42));
	}
	
	
	/**
	 * 检查是否可以提取奖励
	 * @param buffer
	 */
	private void checkRewardInfo(ByteBuffer buffer)
	{
		
		int type = buffer.readByte(); // 类型1查询 2提取

		
		Object obj = DataFactory.getInstance().getAttachment(DataFactory.REWARDSENDER_LIST);
		if(obj == null)
		{
//			System.out.println("PlayerController checkRewardInfo obj is null!");
			return;
		}
		
		if(!(obj instanceof ArrayList))
			return;
		
		ArrayList list = (ArrayList)obj;
		
		if(list.size() <= 0)
			return;

		if(RewardSender.sendPlayerMap.get(player.accountName) != null)
			return;
		
		ByteBuffer sendBuffer = new ByteBuffer();
		sendBuffer.writeByte(type);
		sendBuffer.writeBoolean(true);
		
		int count = 0;
		
		for (int i = 0; i < list.size(); i++)
		{
			RewardSender rs = (RewardSender)list.get(i);
			
			if(rs == null || player.level < rs.level || rs.canCount <= 0)
				continue;
			
			if(type == 1)
			{
				sendBuffer.writeInt(rs.id);
				sendBuffer.writeUTF(rs.name);
				sendBuffer.writeInt(rs.count);
			}
			count++;
		}
		
		if(count <= 0)
			return;
		
		if(type == 2)
		{
			Bag bag = (Bag)player.getExtPlayerInfo("bag");
			if(!bag.checkEnough(count))
			{
				sendAlert(ErrorCode.ALERT_TASK_AWA_BAG_NOT_ENOUGH);
				return;
			}
			
			for (int i = 0; i < list.size(); i++)
			{
				RewardSender rs = (RewardSender)list.get(i);
				
				if(rs == null || player.level < rs.level || rs.canCount <= 0)
					continue;
				
				rs.canCount--;

				Goods goods[] = DataFactory.getInstance().makeGoods(rs.id, rs.count, rs.quality);
				if(goods == null || goods.length == 0 || goods[0] == null)
					continue;
				
				bag.sendAddGoods(this, goods[0]);
			}
			
			RewardSender.sendPlayerMap.put(player.accountName, 1);
		}
	
		netConnection.sendMessage(new SMsg(SMsg.S_PLAYER_REWARDINFO_COMMAND,sendBuffer));
	}
	
	
	/**
	 * 自动打怪挂机
	 * @param inBuffer
	 */
	private void setAutoBattle(AppMessage msg)
	{
		int type = msg.getBuffer().readByte();//0表示中断挂机  1表示开始挂机
		if(team != null && !team.isLeader(this))
		{
			sendAlert(ErrorCode.ALERT_PLAYER_ISNOT_LEADER);
			return;
		}
		if(type == 0)
		{
			if(!isAuto)
				return;
			if(team == null)
				setAuto(false, 0, null);
			else
			{
				for (int i = 0; i < team.getPlayerCount(); i++)
				{
					if(team.getPlayers()[i] != null)
					{
						team.getPlayers()[i].setAuto(false, 0, null);
					}
				}
			}
		}
		else if(type == 1)
		{
			if(isAuto)
				return;
			
			if(!(getParent() instanceof RoomController))
			{
				if(getParent() instanceof BattleController)
					sendAlert(ErrorCode.ALERT_BATTLEING_NOT_SETAUTO);
				else if(getParent() instanceof BusinessController)
					sendAlert(ErrorCode.ALERT_BUSINESSING_NOT_SETAUTO);
				return;
			}
			
			MoneyBattle mb = DataFactory.getInstance().getMoneyBattle(roomController.id);
			if(mb != null && team != null)
			{
				sendAlert(ErrorCode.ALERT_NOT_AUTOBATTLE_INTHISROOM);
				return;
			}
			
			if(checkPlayerState())
				return;
			if(isChoose(this,0))
				return;
			
			int count = msg.getBuffer().readInt();
			if(count <= 0)
				return;
			
			if(roomController.isCopyPartyRoom || roomController.isPartyPKRoom() || roomController.isPartyRoom())
			{
				return;
			}
			if(roomController.isGoldPartyRoom || roomController.isGoldPartyPKRoom)
				return;
			
			if(roomController.getMonsterGroups().length == 0 || roomController.getMonsterGroups() == null)
			{
				return;
			}
			long index = roomController.getMonsterGroups()[0].objectIndex;
			MonsterGroupController mgc = roomController.getMonsterGroupByIndex(index);
			if(mgc == null)
			{
				return;
			}
			if(mgc.isBoss())
			{
				return;
			}
			
			if(team != null)
			{
				for (int i = 0; i < team.getPlayerCount(); i++) 
				{
					PlayerController player = team.getPlayers()[i];
					Bag bag = (Bag) player.getPlayer().getExtPlayerInfo("bag");
					if(bag.getGoodsByType(Goods.AUTOBATTLECARD) == null)
					{
						if(player.getID() == getID())
							sendAlert(ErrorCode.ALERT_AUTO_BATTLE_CARD_ERROR);
						else
							sendError("["+player.getName()+"]"+DC.getString(DC.PLAYER_43));
						return;
					}
					else
					{
						if(bag.getGoodsCountByType(Goods.AUTOBATTLECARD) < count)
						{
							if(player.getID() == getID())
								sendAlert(ErrorCode.ALERT_AUTO_BATTLE_CARD_ERROR);
							else
								sendError("["+player.getName()+"]"+DC.getString(DC.PLAYER_43));
							return;
						}
					}
				}
			}
			else
			{
				Bag bag = (Bag) player.getExtPlayerInfo("bag");
				if(bag.getGoodsByType(Goods.AUTOBATTLECARD) == null)
				{
					sendAlert(ErrorCode.ALERT_AUTO_BATTLE_CARD_ERROR);
					return;
				}
				else
				{
					if(bag.getGoodsCountByType(Goods.AUTOBATTLECARD) < count)
					{
						sendAlert(ErrorCode.ALERT_AUTO_BATTLE_CARD_ERROR);
						return;
					}
				}
				
			}
			
			setAuto(true,count,mgc);
			
			ByteBuffer buffer = new ByteBuffer();
			buffer.writeBoolean(true);
			netConnection.sendMessage(new SMsg(SMsg.S_PLAYER_AUTO_BATTLE_COMMAND,buffer));
			
			autoBattle();
		}
	}
	
	
	public void autoBattle()
	{
		ByteBuffer buffer = new ByteBuffer();
		buffer.writeUTF(mgc.objectIndex+"");
		buffer.writeInt(mgc.x);
		buffer.writeInt(mgc.y);
		roomController.PvEInvitation(this, new AppMessage(SMsg.C_ROOM_PVE_COMMAND,buffer), false);
	}
	
	/**
	 * 设置挂机
	 * @param target 对象 
	 * @param count 挂机次数
	 * @param index 怪物流水
	 */
	public void setAuto(boolean isAutoBattle,int count,MonsterGroupController group)
	{
		if(team == null)
		{
			autoCount = count;
			isAuto = isAutoBattle;
			maxAutoCount = count;
			mgc = group;
			
			sendAutoData();
		}
		else
		{
			for (int i = 0; i < team.getPlayerCount(); i++) 
			{
				PlayerController player = team.getPlayers()[i];
				player.autoCount = count;
				player.isAuto = isAutoBattle;
				player.mgc = group;
				player.maxAutoCount = count;

				player.sendAutoData();
			}
		}
	}
	
	/**
	 * 刷新挂机信息
	 */
	public void sendAutoData()
	{
		ByteBuffer buffer = new ByteBuffer();
		buffer.writeBoolean(isAuto);
		buffer.writeInt(maxAutoCount);
		buffer.writeInt(autoCount);
		Bag bag = (Bag) player.getExtPlayerInfo("bag");
		buffer.writeInt(bag.getGoodsCountByType(Goods.AUTOBATTLECARD));
		netConnection.sendMessage(new SMsg(SMsg.S_PLAYER_AUTO_BATTLE_INFO_COMMAND,buffer));
	}
	
	
	/**
	 * 玩家是否没处于某个状态而不能挂机
	 * @return true 可以挂机  false不能挂机
	 */
	public String checkMaybeAutoState()
	{
		String str = "";
		PlayerController[] pcs = team==null?new PlayerController[]{this}:team.getPlayers();
		for (int i = 0; i < pcs.length; i++) 
		{
			if(pcs[i] != null)
			{
				if(isBox || isCamp || isPetUp)
				{
					if(pcs[i].isBox)
					{	
						if(pcs[i].getID() == getID())
						{
							str += DC.getString(DC.PLAYER_44);//请提取宝箱的物品
						}
						else
						{
							if(pcs[i].boxType == 1)
								str += DC.getString(DC.PLAYER_45);//正在开宝箱
							else if(pcs[i].boxType == 2)
								str += DC.getString(DC.PLAYER_12);
						}
					}
					else if(pcs[i].isCamp)
					{
						if(pcs[i].getID() == getID())
						{
							str += DC.getString(DC.PLAYER_46);//请选择要加入的阵营
						}
						else
						{
							str += DC.getString(DC.PLAYER_47);//正在选择阵营
						}
					}
					else if(pcs[i].isPetUp)
					{
						if(pcs[i].getID() == getID())
						{
							str += DC.getString(DC.PLAYER_48);//请选择宠物进化
						}
						else
						{
							str += DC.getString(DC.PLAYER_49);//正在选择宠物变身
						}
					}
					return pcs[i].getName() + str;
				}
			}
		}
		return "";
	}
	
	/**
	 * 验证身份证
	 * @param msg
	 */
	private void idcardValidate(AppMessage msg)
	{
		if(!DataFactory.isIdcard)
			return;
		OtherExtInfo oei = (OtherExtInfo) player.getExtPlayerInfo("otherExtInfo");
		if(oei.isValiIdcard)
		{
			return;
		}
		
		String name = msg.getBuffer().readUTF();
		String idcard = msg.getBuffer().readUTF();
		
		if(!IdCardValidator.isChineseName(name))
		{
			sendAlert(ErrorCode.ALERT_NAME_ERROR);
			return;
		}

		if(!IdCardValidator.isValidatedAllIdcard(idcard))
		{
			sendAlert(ErrorCode.ALERT_IDCARD_ERROR);
			return;
		}
		
		IdcardInfoExtractor idie = new IdcardInfoExtractor(idcard);
		String timeStr = WorldManager.getTypeTime("yyyy", WorldManager.currentTime);
		int age = Integer.parseInt(timeStr) - idie.getYear();
		if(age < 18)
		{
			sendError(DC.getString(DC.PLAYER_50));
			return;
		}
		if(age > 70)
		{
			sendAlert(ErrorCode.ALERT_IDCARD_ERROR);
			return;
		}

		oei.isValiIdcard = true;
		oei.onlineTime = 0;
		
		ByteBuffer buffer = new ByteBuffer();
		buffer.writeBoolean(true);
		netConnection.sendMessage(new SMsg(SMsg.S_PLAYER_VALIDATE_IDCARD_COMMAND,buffer));
		
		Goods[] goods = DataFactory.getInstance().makeGoods(IDCARDGOODS, IDCARDGOODSCOUNT);
		if(goods.length == 0 || goods == null)
		{
			System.out.println("PlayerController idcardValidate goodsId error:"+IDCARDGOODS);
		}
		else
		{
			Mail mail = new Mail(DC.getString(DC.PLAYER_51));//身份证验证官
			mail.setTitle(DC.getString(DC.PLAYER_52));//身份验证成功礼品
			mail.setContent(DC.getString(DC.PLAYER_53));//你已成功通过身份验证,特此发放奖励给你
			mail.addAttach(goods[0]);
			if(goods.length == 2)
			{
				mail.addAttach(goods[1]);
			}
			mail.send(this);
		}
	}
	
	/**
	 * 师徒邀请(邀请玩家做自己的徒弟)
	 * @param msg
	 */
	public void requestAam(AppMessage msg)
	{
		if(msg == null || msg.getBuffer() == null)
		{
			System.out.println("PlayerController requestAam msg or msg.getBuffer() is null!"+player.name);
			return;
		}
		String playerName = msg.getBuffer().readUTF();//要邀请谁做自己的徒弟
		
		if(roomController == null)
			return;
		if(roomController.isCopyPartyRoom)
		{
			if(roomController.getCopy() != null)
			{
				if(!roomController.getCopy().isTeam)
					return;
			}
		}
		if(roomController.isGoldPartyRoom || roomController.isGoldPartyPKRoom)
			return;
		
		if(player.level <= AamController.MASTER_LEVEL)
		{
			sendAlert(ErrorCode.ALERT_MASTER_CON_ERROR);
			return;
		}
		
		if(aam != null)
		{
			sendAlert(ErrorCode.ALERT_APP_ISNOT_NULL);
			return;
		}
		
		if(getParent() == null)
		{
			System.out.println("PlayerController requestAam getParent() is null!"+player.name);
			return;
		}
		
		if(!(getParent() instanceof RoomController))
		{
			if(getParent() instanceof BattleController)
			{
				sendAlert(ErrorCode.ALERT_PLAYER_IS_BATTLING);
				return;
			}
			else if(getParent() instanceof BusinessController)
			{
				sendAlert(ErrorCode.ALERT_BUSINESS_ING_PLAYER);
				return;
			}
		} 
		
		if(getName().equals(playerName)) //不能向自己发起邀请
			return;
		
		RoomController room = (RoomController) getParent();
		PlayerController target = room.getPlayer(playerName);

		if(target == null)
		{
			sendAlert(ErrorCode.ALERT_PLAYER_NOTAT_ROOM);
			return;
		}
		
		if(target.getPlayer().level >= AamController.MASTER_LEVEL)
		{
			sendAlert(ErrorCode.ALERT_TARGET_APP_CON_ERROR);
			return;
		}
		
		if(target.isAuto)
		{
			sendAlert(ErrorCode.ALERT_TARGET_PLAYER_AUTO_BATTLEING);
			return;
		}
		
		Object obj = target.getParent();
		if(obj == null)
		{
			sendAlert(ErrorCode.ALERT_PLAYER_NOTAT_ROOM);
			return;
		}
		if(!(obj instanceof RoomController))
		{
			if(obj instanceof BattleController)
			{
				sendAlert(ErrorCode.ALERT_PLAYER_IS_BATTLING);
				return;
			}
			else if(obj instanceof BusinessController)
			{
				sendAlert(ErrorCode.ALERT_BUSINESS_ING_PLAYER);
				return;
			}
		} 
		
		if(target.getRoom().id != getRoom().id)
		{
			sendAlert(ErrorCode.ALERT_PLAYER_NOTAT_ROOM);
			return;
		}
		if(target.getAam() != null)
		{
			sendAlert(ErrorCode.ALERT_TARGET_APPMAS_ISNOT_NULL);
			return;
		}
		
		if(target.isChoose(target,1))
			return;
		
		if(roomController.isPartyRoom() || roomController.isPartyPKRoom())
		{
			if(FamilyPartyController.getInstance().isReady())
			{
				if(player.familyId != target.getPlayer().familyId)
				{
					sendAlert(ErrorCode.ALERT_NOTSAME_FAMI_AM);
					return;
				}
			}
		}
		
		OtherExtInfo oei = (OtherExtInfo) player.getExtPlayerInfo("otherExtInfo");
		if(oei.aamCount >= OtherExtInfo.MAXAAMCOUNT)
		{
			sendAlert(ErrorCode.ALERT_AAM_COUNT_MAX);
			return;
		}
		oei = (OtherExtInfo) target.getPlayer().getExtPlayerInfo("otherExtInfo");
		if(oei.aamCount >= OtherExtInfo.MAXAAMCOUNT)
		{
			sendAlert(ErrorCode.ALERT_TARGET_AAM_COUNT_MAX);
			return;
		}

		String aamName = getName()+"-aamInvitation-"+target.getName();
		
		ConfirmJob cj = worldManager.getConfirmation(aamName);
		if(cj != null)
		{
			sendAlert(ErrorCode.ALERT_PLAYER_SAME_INVITATION);
			return;
		}
		
		cj = new AamInvitation(this,target);
		cj.setName(aamName);
		
		if(worldManager.getConfirmation(cj.getName()) == null)
		{
			worldManager.addConfirmJob(cj);
		}
		
		ByteBuffer buffer = new ByteBuffer(8);
		buffer.writeInt(getID());
		buffer.writeUTF(getName());
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_PLAYER_MASTER_INVI_APP_COMMAND,buffer));
	
	}
	
	
	
	/**
	 * 回复师徒请求
	 * @param msg
	 * @param room
	 */
	public void responseAam(AppMessage msg)
	{
		int inviterId = msg.getBuffer().readInt();//回复谁的师徒邀请
		boolean accept = msg.getBuffer().readBoolean();//是否同意当徒弟
		
		if(roomController == null)
			return;
		if(roomController.isCopyPartyRoom)
		{
			if(roomController.getCopy() != null)
			{	
				if(!roomController.getCopy().isTeam)
					return;
			}
		}
		if(roomController.isGoldPartyRoom || roomController.isGoldPartyPKRoom)
			return;
		
		if(getID() == inviterId)
			return;
		
		if(aam != null)
		{
			if(accept)
				sendAlert(ErrorCode.ALERT_MASTER_ISNOT_NULL);
			return;
		}
		
		if(player.level >= AamController.MASTER_LEVEL)
		{
			if(accept)
				sendAlert(ErrorCode.ALERT_APP_CON_ERROR);
			return;
		}
		
		if(!(getParent() instanceof RoomController))
		{
			if(accept)
				sendAlert(ErrorCode.ALERT_PLAYER_NOTAT_ROOM);
			return;
		}
		
		RoomController room = (RoomController) getParent();
		PlayerController inviter = room.getPlayer(inviterId);
		
		if(inviter == null)
		{
			if(accept)
				sendAlert(ErrorCode.ALERT_PLAYER_NOTAT_ROOM);
			return;
		}
		if(inviter.isAuto)
		{
			sendAlert(ErrorCode.ALERT_TARGET_PLAYER_AUTO_BATTLEING);
			return;
		}
		if(inviter.getParent() instanceof BattleController)
		{
			if(accept)
				sendAlert(ErrorCode.ALERT_PLAYER_IS_BATTLING);
			return;
		}
		if(inviter.getRoom().id != getRoom().id)
		{
			if(accept)
				sendAlert(ErrorCode.ALERT_PLAYER_NOTAT_ROOM);
			return;
		}
		
		if(inviter.getAam() != null)
		{
			if(accept)
				sendAlert(ErrorCode.ALERT_TARGET_APP_ISNOT_NULL);
			return;
		}

		
		if(isChoose(inviter,1))
			return;
		
		if(roomController.isPartyPKRoom() || roomController.isPartyRoom())
		{
			if(FamilyPartyController.getInstance().isReady())
			{
				if(player.familyId != inviter.getPlayer().familyId)
				{
					if(accept)
					    sendAlert(ErrorCode.ALERT_NOTSAME_FAMI_AM);
					return;
				}
			}
		}
		
		OtherExtInfo oei = (OtherExtInfo) player.getExtPlayerInfo("otherExtInfo");
		if(oei.aamCount >= OtherExtInfo.MAXAAMCOUNT)
		{
			sendAlert(ErrorCode.ALERT_AAM_COUNT_MAX);
			return;
		}
		oei = (OtherExtInfo) inviter.getPlayer().getExtPlayerInfo("otherExtInfo");
		if(oei.aamCount >= OtherExtInfo.MAXAAMCOUNT)
		{
			sendAlert(ErrorCode.ALERT_TARGET_AAM_COUNT_MAX);
			return;
		}
		
		
		String inviteName = inviter.getName() + "-aamInvitation-" + getName();
		Object obj = worldManager.getConfirmation(inviteName);
		if(obj == null)
		{
			if(accept)
				sendAlert(ErrorCode.ALERT_TEAM_INVITE_CANCEL);
			return;
		}
		if(!(obj instanceof AamInvitation))
		{
			if(accept)
				sendAlert(ErrorCode.EXCEPTION_CLASS_ERROR);
			return;
		}
		AamInvitation ai = (AamInvitation) worldManager.getConfirmation(inviteName);
		if(ai == null)
			return; 
		
		if(!accept)	
		{	
			inviter.sendGetGoodsInfo(1,false, getName()+DC.getString(DC.PLAYER_54));
		}
		else
		{
			AamController ac = ai.confirm(accept);
			if(ac == null)
				return;
			inviter.sendError("["+player.name+"]"+DC.getString(DC.PLAYER_55));
			sendError("["+inviter.getName()+"]"+DC.getString(DC.PLAYER_56));
		
			ac.writeBaseTo(this);
			ac.writeBaseTo(inviter);
		
		}
		ai.setDefaultLifeTime();
	}
	
	/**
	 *  结婚
	 * @param target
	 * @param msg
	 */
	private void requestMarry(AppMessage msg)
	{
		if(!(getParent() instanceof RoomController))
			return;
		if(getRoom().id != DataFactory.MARRYROOM)
			return;
		OtherExtInfo oei = (OtherExtInfo) getPlayer().getExtPlayerInfo("otherExtInfo");
		if(!"".equals(oei.loverName) && oei.loverId != 0)
		{
			sendAlert(ErrorCode.ALERT_PLAYER_IS_MARRYED);
			return;
		}
		if(getTeam() == null)
		{
			sendAlert(ErrorCode.ALERT_MARYRY_OBJECT_NOTINTEAM);
			return;
		}
		if(player.level < MARRYLEVEL)
		{
			sendAlert(ErrorCode.ALERT_MARRY_LEVEL_ERROR);
			return;
		}
		String name = msg.getBuffer().readUTF();
		String index = msg.getBuffer().readUTF();//结婚戒指流水]
		long objectIndex = 0;
		try{objectIndex = Long.parseLong(index);}catch(Exception e){return;}
		PlayerController lover = getTeam().getPlayer(name);
		if(lover == null)
		{
			sendAlert(ErrorCode.ALERT_MARYRY_OBJECT_NOTINTEAM);
			return;
		}
		if(lover.getPlayer().level < MARRYLEVEL)
		{
			sendAlert(ErrorCode.ALERT_TARGET_MARRY_LEVEL_ERROR);
			return;
		}
		Bag bag = (Bag) getPlayer().getExtPlayerInfo("bag");
		Goods goods = bag.getGoodsByObjectIndex(objectIndex);
		if(goods == null)
		{
			sendAlert(ErrorCode.ALERT_GOODS_NOT_EXIST);//没有戒指还想结婚？
			return;
		}
		GoodsMarry gm = DataFactory.getInstance().getGoodsMarryByGoodsId(goods.id);
		if(gm == null)
		{
			System.out.println("NpcController marry goodsMarry is null:"+goods.id);
			return;
		}
//		if(!gm.isTaskFinish(this))
//		{
//			sendAlert(ErrorCode.ALERT_MARRY_TASK_NOT_FINISH);
//			return;
//		}
		if(!gm.isConEnough(this))
		{
			sendAlert(ErrorCode.ALERT_MARRY_GOODS_NOT_ENOUGH);
			return;
		}
		if(goods.type != 27)
		{
			sendAlert(ErrorCode.ALERT_PLAYER_NOT_MARRY_GOODS);
			return;
		}
		if(goods instanceof GoodsProp)
		{
			GoodsProp prop = (GoodsProp) goods;
			if(prop.chatType != 0 && prop.chatType != 1)//判断物品是不是结婚戒指
			{
				sendAlert(ErrorCode.ALERT_PLAYER_NOT_MARRY_GOODS);
				return;
			}
		}
		else
		{
			System.out.println("is not goods:"+goods.name+"   "+goods.type);
			return;
		}
		if(!(lover.getParent() instanceof RoomController))
		{
			System.out.println("not in room");
			return;
		}
		if(lover.getPlayer().sex == getPlayer().sex)
		{
			sendAlert(ErrorCode.ALERT_PLAYER_SEX_NOT_SAME);
			return;
		}
		OtherExtInfo love = (OtherExtInfo) lover.getPlayer().getExtPlayerInfo("otherExtInfo");
		if(!"".equals(love.loverName) || love.loverId != 0)
		{
			sendAlert(ErrorCode.ALERT_TARGET_PLAYER_IS_MARRYED);
			return;
		}
		
		String marryName = getName()+"-marryInvitation-"+lover.getName();
		
		ConfirmJob cj = worldManager.getConfirmation(marryName);
		if(cj != null)
		{
			sendAlert(ErrorCode.ALERT_PLAYER_SAME_INVITATION);
			return;
		}
		
		cj = new MarryInvitation(this,lover,objectIndex,2);
		cj.setName(marryName);
		
		if(worldManager.getConfirmation(cj.getName()) == null)
		{
			worldManager.addConfirmJob(cj);
		}
		
		ByteBuffer buffer = new ByteBuffer(8);
		buffer.writeInt(getID());
		buffer.writeUTF(getName());
		lover.getNetConnection().sendMessage(new SMsg(SMsg.S_PLAYER_REQUEST_MARRY_COMMAND,buffer));
	
		sendAlert(ErrorCode.ALERT_MARRY_REQUEST_SUCCESS);
	
	}
	
	
	/**
	 * 结婚回复
	 * @param msg
	 */
	private void responseMarry(AppMessage msg)
	{
		String inviterName = msg.getBuffer().readUTF();
		boolean result = msg.getBuffer().readBoolean();

		String confirmationName = inviterName + "-marryInvitation-" + getName();

		MarryInvitation mi = (MarryInvitation)worldManager.getConfirmation(confirmationName);
	    
		if(mi == null)
	    {
			if(result)
				sendAlert(ErrorCode.ALERT_PLAYER_CANCEL);
	    	return;
	    }

		mi.confirm(result);		
		mi.setLifeTime(-0xffff);
	}
	

	/**
	 * 离婚
	 */
	private void cancelMarry()
	{
		if(!(getParent() instanceof RoomController))
			return;
		if(getRoom().id != DataFactory.MARRYROOM)
			return;
		OtherExtInfo oei = (OtherExtInfo) getPlayer().getExtPlayerInfo("otherExtInfo");
		if("".equals(oei.loverName) && oei.loverId == 0)
		{
			sendAlert(ErrorCode.ALERT_PLAYER_NOT_MARRY);
			return;
		}
		GoodsMarry gm = DataFactory.getInstance().getGoodsMarryByGoodsId(0);
		if(gm == null)
		{
			System.out.println("NpcController cancelMarry goodsMarry is null:"+0);
			return;
		}
//		if(!gm.isTaskFinish(this))
//		{
//			sendAlert(ErrorCode.ALERT_NEED_TASK_ISNOT_FINISH);
//			return;
//		}
		if(!gm.isConEnough(this))
		{
			sendAlert(ErrorCode.ALERT_CANCEL_GOODS_ISNULL);
			return;
		}
		
		OtherExtInfo loverOei = null;
		PlayerController lover = roomController.getPlayer(oei.loverId);
		if(lover == null)
			lover = roomController.getParent().getPlayer(oei.loverId);
		if(lover == null)
			lover = worldManager.getPlayer(oei.loverId);
		Player player = null;
		if(lover == null)
		{
			player = worldManager.getDatabaseAccessor().getPlayer(oei.loverId);
			if(player == null)
			{
				sendAlert(ErrorCode.EXCEPTION_PLAYER_OFFLINE);
				return;
			}
			loverOei = (OtherExtInfo) player.getExtPlayerInfo("otherExtInfo");
			Mail mail = new Mail(DC.getString(DC.PLAYER_57));//奇妙离婚登记处
			mail.setTitle(DC.getString(DC.PLAYER_58));//"离婚信息
			mail.setContent(gm.getCancelMarryMailContent(getName(), getName(), player.name));
			mail.send(this);
			
			mail = new Mail(DC.getString(DC.PLAYER_57));
			mail.setTitle(DC.getString(DC.PLAYER_58));
			mail.setContent(gm.getCancelMarryMailContent(player.name, getName(), player.name));
			mail.sendOffLine(player);
		}
		else
		{
			loverOei = (OtherExtInfo) lover.getPlayer().getExtPlayerInfo("otherExtInfo");
			
			Mail mail = new Mail(DC.getString(DC.PLAYER_57));
			mail.setTitle(DC.getString(DC.PLAYER_58));
			mail.setContent(gm.getCancelMarryMailContent(getName(), getName(), lover.getName()));
			mail.send(this);
			
			mail = new Mail(DC.getString(DC.PLAYER_57));
			mail.setTitle(DC.getString(DC.PLAYER_58));
			mail.setContent(gm.getCancelMarryMailContent(lover.getName(), getName(), lover.getName()));
			mail.send(lover);
		}

		loverOei.clearLoveInfo();
		oei.clearLoveInfo();
		
		gm.removeMarryGoods(this);
		
		ByteBuffer buffer = new ByteBuffer();
		buffer.writeBoolean(true);
		netConnection.sendMessage(new SMsg(SMsg.S_PLAYER_CANCEL_MARRY_COMMAND,buffer));
		
		removeMarryGoods(1);
		updateRoleInfo();
		if(lover == null)
		{
			player.removeMarryGoods(1);
			
			worldManager.getJobObserver().addJob(GameServer.JOB_DATABASEJOB,
					new SaveJob(worldManager,player,SaveJob.MARRY_CANCEL_SAVE));
		}
		else
		{
			lover.removeMarryGoods(1);
			lover.updateRoleInfo();
		}
	}
	
	/**
	 * 离婚,合区时删除婚姻相关道具
	 * @param 1离婚 2合区
	 */
	public void removeMarryGoods(int type)
	{
//		StringBuffer sb = new StringBuffer("online remove marryGoods---");
		
		Bag bag = (Bag) player.getExtPlayerInfo("bag");
		
		Goods[] goodsList = bag.getGoodsList();
		for (int i = 0; i < goodsList.length; i++) 
		{
			if(goodsList[i] == null)
				continue;
			if(type == 1)
			{
				if(goodsList[i] instanceof GoodsEquip)
				{
					GoodsEquip equip = (GoodsEquip) goodsList[i];
					if(equip.equipLocation == 11 && equip.eType == 1)
					{
						if(equip.useFlag)
						{
							 player.title = "";
						}
						bag.removeGoods(this, goodsList[i].objectIndex, 1);
					}  
				}
			}
			else if(type == 2)
			{
				if(goodsList[i] instanceof GoodsEquip)
				{
					GoodsEquip equip = (GoodsEquip) goodsList[i];
					if(equip.eType == 1)
					{
						if(equip.useFlag)
						{
							if(equip.equipLocation == 11)
								player.title = "";
							if(equip.equipLocation == 10)
								player.setPlayerModelMotionId();
						}
						bag.removeGoods(this, goodsList[i].objectIndex, 1);
					}
				}
			}
			else
				continue;
		}
		
		Storage storage = (Storage) player.getExtPlayerInfo("storage");
		goodsList = storage.getGoodsList();
		for (int i = 0; i < goodsList.length; i++) 
		{
			if(goodsList[i] == null)
				continue;
			if(type == 1)
			{
				if(goodsList[i] instanceof GoodsEquip)
				{
					GoodsEquip equip = (GoodsEquip) goodsList[i];
					if(equip.eType == 1 && equip.equipLocation == 11 )
					{	
						goodsList[i] = null;
					}
				}
			}
			else if(type == 2)
			{
				if(goodsList[i] instanceof GoodsEquip)
				{
					GoodsEquip equip = (GoodsEquip) goodsList[i];
					if(equip.eType == 1)
						goodsList[i] = null;
				}
			}
			else
				continue;
		
		}
		
		MailBox mb = (MailBox)player.getExtPlayerInfo("mailbox");
		int mailSize = mb.mailList.size();
		for (int i = 0; i < mailSize; i++)
		{
			Mail mail =  (Mail)mb.mailList.get(i);
			Goods g1 = mail.getAttach1();
			if(g1 != null)
			{
				if(g1 instanceof GoodsEquip)
				{
					GoodsEquip equip = (GoodsEquip) g1;
					if(equip.eType == 1)
					{	
						mail.clearAttach();
					}
				}
			}
			Goods g2 =mail.getAttach2();
			if(g2 != null)
			{
				if(g2 instanceof GoodsEquip)
				{
					GoodsEquip equip = (GoodsEquip) g2;
					if(equip.eType == 1)
					{
						mail.clearAttach();
					}
				}
			}
			
		}

		if(type == 1)
			bag.setExtGoods(5, null);
		else
		{
			Goods goods = bag.getExtGoods(5);
			if(goods != null)
			{
				Mail mail = new Mail(DC.getString(DC.PLAYER_59));
				mail.setTitle(DC.getString(DC.PLAYER_60));
				mail.addAttach(goods);
				mail.send(this);
				
				bag.setExtGoods(5, null);
			}
		}
		
	}
	
	
	/**
	 * 修改性别
	 * @param buffer
	 */
	public void changeSex()
	{
		OtherExtInfo oei = (OtherExtInfo) player.getExtPlayerInfo("otherExtInfo");
		if(!"".equals(oei.loverName) || oei.loverId != 0)
		{
			sendAlert(ErrorCode.ALERT_MARRY_NOTNULL_NOT_CHANGE_SEX);
			return;
		}
		
		Bag bag = (Bag) player.getExtPlayerInfo("bag");
		if(bag.money < CAMPMONEY)
		{
			sendAlert(ErrorCode.ALERT_SHOP_NO_MONEY);
			return;
		}
		bag.money -= CAMPMONEY;
		bag.sendAddGoods(this, null);
		
		player.sex = player.sex==1?0:1;
		
		ByteBuffer buffer = new ByteBuffer();
		buffer.writeBoolean(true);
		netConnection.sendMessage(new SMsg(SMsg.S_PLAYER_CHANGE_SEX_COMMAND,buffer));
		
		if(bag.goodsList[49] == null)
		{	
			player.setPlayerModelMotionId();
		    updateRoleInfo();
		}
	}
	
	/**
	 * 通知客户端玩家身上即将过期的物品
	 */
	public void sendExpiredGoods()
	{
		Goods goods = null;
		long overTime = 0;
		int type = 0;
		Bag bag = (Bag) player.getExtPlayerInfo("bag");
		Goods[] goodsList = bag.getGoodsList();
		for (int i = 0; i < goodsList.length; i++) 
		{
			if(goodsList[i] == null)
				continue;
			if(goodsList[i].createTime == 0 || goodsList[i].effectTime == 0)
				continue;
			long time = goodsList[i].effectTime - (System.currentTimeMillis() - goodsList[i].createTime);
			if(time > 0)
			{
				if(overTime != 0)
				{
					if(overTime > time)
					{	
						overTime = time;
						goods = goodsList[i];
						if(goodsList[i].useFlag)
							type = 1;
					}
				}
				else
				{	
					overTime = time;
					goods = goodsList[i];
					if(goodsList[i].useFlag)
						type = 1;
				}
			}
		}
		
		Storage storage = (Storage) player.getExtPlayerInfo("storage");
		goodsList = storage.getGoodsList();
		for (int i = 0; i < goodsList.length; i++) 
		{
			if(goodsList[i] == null)
				continue;
			if(goodsList[i].createTime == 0 || goodsList[i].effectTime == 0)
				continue;
			long time = goodsList[i].effectTime - (System.currentTimeMillis() - goodsList[i].createTime);
			if(time > 0)
			{
				if(overTime != 0)
				{
					if(overTime > time)
					{	
						overTime = time;
						goods = goodsList[i];
						type = 2;
					}
				}
				else
				{	
					overTime = time;
					goods = goodsList[i];
					type = 2;
				}
			}
		}

		if(goods == null)
			return;
		ByteBuffer buffer = new ByteBuffer();
		buffer.writeInt(goods.id);
		buffer.writeUTF(goods.name);
		buffer.writeInt((int)overTime);
		netConnection.sendMessage(new SMsg(SMsg.S_PLAYER_EXPIRED_GOODS_COMMAND,buffer));
	}
	
	private static final int CEGOODS = 1045010411;
	private static final int ZSGOODS1 = 1045000178;
	private static final int ZSGOODSCOUNT1 = 10;
	private static final int ZSGOODS2 = 1045000180;
	private static final int ZSGOODSCOUNT2 = 10;
	/**
	 * 转换宠物
	 * 转换后等级为1级，赠送10个1000经验包，1000亲密包
	 */
	public void changePet()
	{
		if(getParent() instanceof BattleController)
			return;
		Bag bag = (Bag) player.getExtPlayerInfo("bag");
//		if(bag.money < 500)
//		{
//			sendAlert(ErrorCode.ALERT_SHOP_NO_MONEY);
//			return;
//		}
		Goods goods = bag.getGoodsById(CEGOODS);
		if(goods == null)
		{
			sendAlert(ErrorCode.ALERT_CHANGE_PET_NEEDGOODS_ERROR);
			return;
		}
		
		PetTome pt = (PetTome) player.getExtPlayerInfo("petTome");
		Pet pet = pt.getActivePet();
		if(pet == null)
			return;
		if(pet.level < 25 || isPetUp)
		{
			sendAlert(ErrorCode.ALERT_NOT_CHANGE_PET);
			return;
		}
		pet.changePet();
		sendPetInfo(this,player.id);
		
		bag.removeGoods(this, goods.objectIndex, 1);
		
		Goods[] g1 = DataFactory.getInstance().makeGoods(ZSGOODS1,ZSGOODSCOUNT1);
		Goods[] g2 = DataFactory.getInstance().makeGoods(ZSGOODS2,ZSGOODSCOUNT2);
		if(g1 != null && g1[0] != null && g2 != null && g2[0] != null)
		{
			Mail mail = new Mail(DC.getString(DC.PLAYER_61));
			mail.setTitle(DC.getString(DC.PLAYER_62));
			mail.addAttach(g1[0]);
			mail.addAttach(g2[0]);
			mail.send(this);
		}
		
		sendError(DC.getString(DC.PLAYER_63));
	}
	
	
	/**
	 * 转生
	 */
	public void upRole()
	{
		if(getParent() instanceof BattleController)
			return;
		if(getParent() instanceof BusinessController)
			return;
		if(team != null)
		{
			sendAlert(ErrorCode.ALERT_UPROLE_TEAM_ERROR);
			return;
		}
		if(getAam() != null)
		{
			sendAlert(ErrorCode.ALERT_UPROLE_AAM_ERROR);
			return;
		}
		Bag bag = (Bag) player.getExtPlayerInfo("bag");
		for (int i = 48; i < bag.goodsList.length; i++) 
		{
			if(bag.goodsList[i] != null)
			{
				sendAlert(ErrorCode.ALERT_UPROLE_EQUIP_ERROR);
				return;
			}
		}
		
		TaskInfo ti = (TaskInfo) player.getExtPlayerInfo("taskInfo");
		if(ti.getTasks() == null || ti.getTasks().size() > 0)
		{
			sendAlert(ErrorCode.ALERT_UPROLE_TASK_NOT_NULL);
			return;
		}
		
		UpRole ur = DataFactory.getInstance().getUpRoleByPlayer(player.getZhuanshengState()+1,player.profession);
		if(ur == null)
		{
			sendAlert(ErrorCode.ALERT_UPROLE_CONDITION_ERROR);
			return;	
		}
		if(!ur.isGoodsEnough(this))
		{
			sendAlert(ErrorCode.ALERT_UPROLE_GOODS_ERROR);
			return;
		}
		
		if(!ur.isTaskFinish(this))
		{
			sendAlert(ErrorCode.ALERT_UPROLE_TASK_ERROR);
			return;
		}

		if(player.level < ur.needLevel)
		{
			sendAlert(ErrorCode.ALERT_UPROLE_LEVEL_ERROR);
			return;
		}
		
		
		BuffBox buff = (BuffBox) player.getExtPlayerInfo("buffBox");
		for (int i = 0; i < buff.getEffectList().size(); i++)
		{
			Effect effect = (Effect) buff.getEffectList().get(i);
			buff.removeEffect(effect, this);
			bag.sendExpBuff(this, effect.id, false, 0);
		}
		
		player.setUpRoleJob();
		expObj = null;
		
		ur.removeGoods(this);
		
		ByteBuffer buffer = new ByteBuffer();
		player.sendAlwaysValue(buffer, null);
		getRoom().dispatchMsg(SMsg.S_PLAYER_UP_ROLE_COMMAND, buffer);
		
	    buffer = new ByteBuffer(64);
		SkillTome skill = (SkillTome) player.getExtPlayerInfo("skillTome");
		buffer.writeByte(1);
		skill.writeTo(buffer);
		skill.sendAutoSkill(buffer);
		netConnection.sendMessage(new SMsg(SMsg.S_GET_SKILL_COMMAND,buffer));
		
		PlayerSetting ps = (PlayerSetting) player.getExtPlayerInfo("playerSetting");
		Object[] os = ps.getObjects();
		for (int i = 0; i < os.length; i++) 
		{
			if(os[i] != null)
			{
				if(os[i] instanceof Skill)
				{
					Skill s = (Skill) os[i];
					if(s.id == 1031000001)
						continue;
					os[i] = null;
					buffer = new ByteBuffer();
					buffer.writeInt(i);
					netConnection.sendMessage(new SMsg(SMsg.S_CANCEL_GOODS_SHORTCUT_COMMAND,buffer));
				}
			}
		}
		
		updateRoleInfo();
	}
	
	/**
	 * 炼金术
	 */
	public void processGoldBox(ByteBuffer inBuffer)
	{
		if(getParent() instanceof BattleController)
			return;
		if(player.level < GoldBox.GOLDLEVEL)
		{
			sendAlert(ErrorCode.ALERT_GOLD_LEVEL_ERROR);
			return;
		}
		if(player.goldBoxCount >= GoldBox.MAXCOUNT)
		{
			sendAlert(ErrorCode.ALERT_AWARDTYPE_COUNT_OVER);
			return;
		}
		String str = inBuffer.readUTF();
		long objectIndex = 0;
		try {
			objectIndex = Long.parseLong(str);
		} catch (Exception e) {
			System.out.println("PlayerController awardTypeCommon error:"+str);return;
		}
		
		Bag bag = (Bag) player.getExtPlayerInfo("bag");
		Goods goods = bag.getGoodsByObjectIndex(objectIndex);
		if(goods == null)
		{
			sendAlert(ErrorCode.ALERT_GOODS_NOT_EXIST);
			return;
		}
		if(!GoldBox.isAward(goods))
		{
//			System.out.println("PlayerController isAward is false!");
			return;
		}
		if(!GoldBox.isSuccess(this,goods))
		{
			//炼金失败
			sendGetGoodsInfo(1, false, DC.getString(DC.PLAYER_64));
			bag.removeGoods(this, objectIndex, 1);
			ByteBuffer buffer = new ByteBuffer();
			buffer.writeBoolean(false);
			netConnection.sendMessage(new SMsg(SMsg.S_PLAYER_GOLD_BOX_COMMAND,buffer));
		}
		else
		{
			GoldBox at = DataFactory.getInstance().getAwardType();
			if(at == null)
			{
				System.out.println("PlayerController processGoldBox at is null!");
				return;
			}
			Goods awardGoods = at.getGoods();
			if(awardGoods != null)
			{
				if(!bag.isCanAddGoodsToBag(awardGoods))
				{
					sendAlert(ErrorCode.ALERT_BAG_IS_FULL);
					return;
				}
				bag.removeGoods(this, objectIndex, 1);
				
				bag.sendAddGoods(this, awardGoods);
				
				ByteBuffer buffer = new ByteBuffer();
				buffer.writeBoolean(true);
				awardGoods.writeTo(buffer);
				buffer.writeInt(awardGoods.goodsCount);
				netConnection.sendMessage(new SMsg(SMsg.S_PLAYER_GOLD_BOX_COMMAND,buffer));
				
				StringBuffer sb = new StringBuffer();
				if(at.isNotice)
				{
					sb.append(DC.getString(DC.GOODS_6));
					sb.append("|");
					sb.append(getName());
					sb.append("#u:");
					sb.append(getName());
					sb.append("|");
					sb.append(DC.getString(DC.PLAYER_65));
					sb.append("|[");
					sb.append(awardGoods.name);
					sb.append("]#p:");
					sb.append(awardGoods.objectIndex);
					sb.append(":");
					sb.append(awardGoods.quality);
					sb.append(":");
					sb.append(getID());
					sendGetGoodsInfo(3, true, sb.toString());
				}
				else
				{
					sb.append(DC.getString(DC.PLAYER_66));
					sb.append("|[");
					sb.append(awardGoods.name);
					sb.append("]#p:");
					sb.append(awardGoods.objectIndex);
					sb.append(":");
					sb.append(awardGoods.quality);
					sb.append(":");
					sb.append(getID());
					sendGetGoodsInfo(1, false, sb.toString());
				}
			}
			else
			{
				/**
				 * 恭喜XXX，由于炼金术士今天RP意外爆发了，炼金成功获得XXX
	很遗憾，炼金术士意外走神了，你什么也没得到！
	好消息，炼金术士今天RP爆发了，你本次炼金成功获得XXX！
				 */
				sendGetGoodsInfo(1, false, DC.getString(DC.PLAYER_64));
			
				bag.removeGoods(this, objectIndex, 1);
				
				ByteBuffer buffer = new ByteBuffer();
				buffer.writeBoolean(false);
				netConnection.sendMessage(new SMsg(SMsg.S_PLAYER_GOLD_BOX_COMMAND,buffer));
			}
		}
		
		player.goldBoxCount++;
	}
	
	
	private int[] dayRewardGoodsIds = {1045000031,1045012001};
	private int[] dayrewardGoodsCounts = {1,2};
	private List drGoodsList = null;
	
	/**
	 * 领取每日奖励
	 * 1045000031 1小时4倍经验卡  1个
	1045012001 行动值小礼包  2个
	 */
	public void getEverydayReward(ByteBuffer inBuffer)
	{
		ByteBuffer outBuffer = null;
		
		int type = inBuffer.readByte();
		if(type == 1)//查询状态
		{
			outBuffer = new ByteBuffer();
			outBuffer.writeByte(1);
			if(player.dayRewardDate == WorldManager.date)
			{
				outBuffer.writeBoolean(false);
			}
			else
			{	
				outBuffer.writeBoolean(true);
			}
		}
		else if(type == 2)
		{
			if(player.dayRewardDate == WorldManager.date)
				return;
			drGoodsList = new ArrayList();
			for (int i = 0; i < dayRewardGoodsIds.length; i++) 
			{
				Goods goods = (Goods) DataFactory.getInstance().getGameObject(dayRewardGoodsIds[i]);
				if(goods == null || dayrewardGoodsCounts[i] == 0)
				{
					System.out.println("PlayerController getEverydayReward error:"+dayRewardGoodsIds[i]+"  count is zero:"+i);
					return;
				}
			}
			for (int i = 0; i < dayRewardGoodsIds.length; i++) 
			{
				Goods[] gs = DataFactory.getInstance().makeGoods(dayRewardGoodsIds[i], dayrewardGoodsCounts[i]);
				for (int j = 0; j < gs.length; j++) 
				{
					drGoodsList.add(gs[j]);
				}
			}
			outBuffer = new ByteBuffer();
			outBuffer.writeByte(2);
			outBuffer.writeBoolean(true);
			outBuffer.writeByte(drGoodsList.size());
			for (int i = 0; i < drGoodsList.size(); i++) 
			{
				Goods goods = (Goods) drGoodsList.get(i);
				outBuffer.writeInt(goods.id);
				outBuffer.writeUTF(goods.name);
				outBuffer.writeInt(goods.goodsCount);
			}
		}
		else if(type == 3)//领取
		{
			if(drGoodsList == null)
				return;
			Bag bag = (Bag) player.getExtPlayerInfo("bag");
			if(!bag.isCanAddGoodsList(drGoodsList))
			{
				sendAlert(ErrorCode.ALERT_BAG_IS_FULL);
				return;
			}

			StringBuffer sb = new StringBuffer();
			sb.append(DC.getString(DC.PLAYER_67));
			sb.append(":  ");
			for (int i = 0; i < drGoodsList.size(); i++) 
			{
				Goods goods = (Goods) drGoodsList.get(i);
				
				sb.append(goods.name);
				sb.append(" x");
				sb.append(goods.goodsCount);
				sb.append("  ");

				bag.sendAddGoods(this, goods);
			}
			
			sendGetGoodsInfo(1, false, sb.toString());
			
			outBuffer = new ByteBuffer();
			outBuffer.writeByte(3);
			outBuffer.writeBoolean(true);
			
			drGoodsList = null;
			player.dayRewardDate = WorldManager.date;
		} 
		else if(type == 4 || type == 5)//发送要获得的特殊活动奖励物品(如中秋活动)
		{
			Map map = (Map)DataFactory.getInstance().getAttachment(DataFactory.TIME_CONDITION);
			if(map == null)
				return;	
			Object obj = map.get(getRoom().id+"");
			if(obj == null)
				return;	
			String[] ts = Utils.split((String)obj, "|");
			if(ts.length <= 1)
				return;	
			String[] dStr = Utils.split(ts[0], ":");
			for (int i = 0; i < dStr.length; i++)
			{
				String[] dateStr = Utils.split(dStr[i], "-");
				if(dateStr.length != 3)
					continue;
				if(getPlayer().partyRewardMonth == Integer.parseInt(dateStr[1])
						&& getPlayer().partyRewardDay == Integer.parseInt(dateStr[2]))
				{
					sendAlert(ErrorCode.ALERT_NOT_PARTY_REWARD);
					return;
				}
			}
			if(type == 4)
			{
				outBuffer = new ByteBuffer();
				outBuffer.writeByte(4);
				outBuffer.writeBoolean(true);
				outBuffer.writeByte(ts.length-1);
				for (int i = 1; i < ts.length; i++)
				{
					String[] goodsStr = Utils.split(ts[i], ":");
					int id = Integer.parseInt(goodsStr[0]);
					int count = Integer.parseInt(goodsStr[1]);
					Goods goods = (Goods) DataFactory.getInstance().getGameObject(id);
					outBuffer.writeInt(id);
					outBuffer.writeUTF(goods.name);
					outBuffer.writeInt(count);
				}
			}
			else if(type == 5)//领取
			{
				List list = new ArrayList();
				for (int i = 1; i < ts.length; i++)//第一个是日期,从第二个开始才是物品 
				{
					String[] goodsStr = Utils.split(ts[i], ":");
					Goods[] gs = DataFactory.getInstance().makeGoods(Integer.parseInt(goodsStr[0]), Integer.parseInt(goodsStr[1]));
					for (int j = 0; j < gs.length; j++)
					{
						if(gs[j] == null)
							continue;
						list.add(gs[j]);
					}
				}
				Bag bag = (Bag) getPlayer().getExtPlayerInfo("bag");
				if(!bag.isCanAddGoodsList(list))
				{
					sendAlert(ErrorCode.ALERT_BAG_IS_FULL);
					return;
				}
				StringBuffer sb = new StringBuffer();
				sb.append(DC.getString(DC.PLAYER_67));
				sb.append(":  ");
				for (int i = 0; i < list.size(); i++) 
				{
					Goods goods = (Goods)list.get(i);
					sb.append(goods.name);
					sb.append(" x");
					sb.append(goods.goodsCount);
					sb.append("  ");
					
					bag.sendAddGoods(this, goods);
				}
				
				sendGetGoodsInfo(1, false, sb.toString());
				
				outBuffer = new ByteBuffer();
				outBuffer.writeByte(5);
				outBuffer.writeBoolean(true);
				
				getPlayer().partyRewardMonth = Byte.parseByte(WorldManager.getTypeTime("MM", WorldManager.currentTime));
				getPlayer().partyRewardDay = WorldManager.date;
			}
		}
		netConnection.sendMessage(new SMsg(SMsg.S_PLAYER_EVERYDAY_REWARD_COMMAND,outBuffer));
	}
	
	
	/**
	 * 通知需要更新到facebook的
	 * @param type 1:获得称号 2:结婚信息 3:帮忙战
	 */
	public void sendFacebookInfo(int type,Goods goods,String otherName,int ampoint)
	{
		if(ampoint != 100 && ampoint != 50 && type == 3)
			return;
		ByteBuffer buffer = new ByteBuffer();
		buffer.writeByte(type);
		buffer.writeUTF(player.accountName);
		if(type == 1)
		{
			if(!(goods instanceof GoodsEquip))
				return;
			GoodsEquip equip = (GoodsEquip) goods;
			if(equip.equipLocation != 11)
				return;
			buffer.writeUTF(goods.name);
		}
		else if(type == 2)
			buffer.writeUTF(otherName);
		else if(type == 3)
		{
			buffer.writeInt(ampoint);
		}
		netConnection.sendMessage(new SMsg(SMsg.S_PLAYER_FACEBOOK_INFO_COMMAND,buffer));
	}
	/**
	 * 发送成就
	 * @param game
	 * @param point
	 */
	public void sendChengjiu(ByteBuffer inBuffer)
	{
		int type = inBuffer.readByte();
		ByteBuffer buffer = new ByteBuffer();
		buffer.writeByte(type);
		if(type == 4)
		{
			buffer.writeInt(GameServer.getInstance().id);
			if(player.chengjiu == 0)
			{
				int money = worldManager.getDatabaseAccessor().getPlayerPayWithoutInGame(player.accountName);
				if(money >= 10)
					player.chengjiu = 100;
			}
			int point = player.level / 10 + player.chengjiu;
			buffer.writeInt(point);
		}
		else if(type == 5)
		{
			buffer.writeInt(GameServer.getInstance().id);
		}
		
		netConnection.sendMessage(new SMsg(SMsg.S_PLAYER_FACEBOOK_INFO_COMMAND,buffer));
	}
	
	public void setGoldSignName(String str)
	{
		Storage storage = (Storage) player.getExtPlayerInfo("storage");
		if("".equals(str))
			storage.goldSignName = str;
		else
		{
			if(storage.goldSignName.indexOf(str) == -1)
			{
				if("".equals(storage.goldSignName))
					storage.goldSignName += str;
				else
					storage.goldSignName += ":" + str;
				
				player.goldPartyDate = (byte) GoldPartyController.getInstance().getPartyDate();
			}
		}
	}

	public boolean isGoldSignName(int npcId)
	{
		Storage storage = (Storage) player.getExtPlayerInfo("storage");
		if("".equals(storage.goldSignName))
			return false;
		if(storage.goldSignName.indexOf(npcId+"") == -1)
			return false;
		return true;
	}
	
	private void guideAnswerStep(ByteBuffer inBuffer)
	{
		int type = inBuffer.readByte();
		if(type == 2)//回答新手泡泡问题
		{
			int questionId = inBuffer.readInt();
			int answer = inBuffer.readByte();
			Guide guide = DataFactory.getInstance().getGuideByLevel(player.guideStep+1);
			if(guide == null)
				return;
			if(guide.questionId != questionId)
			{
				return;
			}
			if(player.level < guide.level)
				return;
			NpcController npc = roomController.getNpc(guide.npcId);
			if(npc == null)
				return;
			Question q = DataFactory.getInstance().getQuestionFromGuide(questionId);
			if(q == null)
			{
				System.out.println("PlayerController guideAnswerStep quesiton is null:"+questionId);
				return;
			}
			boolean result = q.isRight(answer);
			ByteBuffer buffer = new ByteBuffer();
			buffer.writeByte(type);
			buffer.writeInt(npc.getID());
			buffer.writeBoolean(result);
			buffer.writeByte(answer);
			netConnection.sendMessage(new SMsg(SMsg.S_PLAYER_GUIDE_STEP_COMMAND,buffer));
			
			if(result)
			{
				player.guideStep = (byte) guide.step;
				guide.sendReward(this);
				
				checkGuideStep();
			}
		}
	}
	
	public void sendCopyPoint()
	{		
		if(player.copyPoint > Player.MAXCOPYPOINT)
			player.copyPoint = Player.MAXCOPYPOINT;
		ByteBuffer buffer = new ByteBuffer();
		buffer.writeByte(player.copyPoint);
		buffer.writeByte(Player.MAXCOPYPOINT);
		getNetConnection().sendMessage(new SMsg(SMsg.S_COPY_POINT_SHOW_COMMAND,buffer));
	}
	
	/**
	 * 检测泡泡
	 * @param type
	 */
	public void checkGuideStep()
	{
		Guide guide = DataFactory.getInstance().getGuideByLevel(player.guideStep+1);
		if(guide == null)
			return;
		if(player.level < guide.level)
			return;
		if(roomController == null)
			return;
		NpcController npc = roomController.getNpc(guide.npcId);
		if(npc == null)
			return;
		ByteBuffer buffer = new ByteBuffer();
		buffer.writeByte(1);
		buffer.writeInt(guide.npcId);
		netConnection.sendMessage(new SMsg(SMsg.S_PLAYER_GUIDE_STEP_COMMAND,buffer));
	}
	
	public void writeBattlePetInfo(ByteBuffer buffer)
	{
		PetTome pt = (PetTome) player.getExtPlayerInfo("petTome");
		Pet pet = pt.getActiveBattlePet();
		if(pet == null)
			buffer.writeBoolean(false);
		else
		{
			if(pet.isMaxBattlePoint())
				buffer.writeBoolean(false);
			else
			{
				buffer.writeBoolean(true);
				buffer.writeUTF(pet.name);
				buffer.writeInt(pet.job);
				buffer.writeInt(pet.level);
				buffer.writeInt(pet.modelId);
			}
		}
	}
	
	/**
	 * 激守护相关操作
	 * @param inBuffer
	 */
	private void battlePetBaseInfo(ByteBuffer inBuffer)
	{
		int type = inBuffer.readByte();
		ByteBuffer buffer = new ByteBuffer();
		buffer.writeByte(type);
		PetTome pt = (PetTome) player.getExtPlayerInfo("petTome");
		if(type == 1)//查看守护信息
		{
			int playerId = inBuffer.readInt();
			Pet pet = null;
			if(playerId == getID())
			{
				pet = pt.getActiveBattlePet();
			}
			else
			{
				PlayerController target = roomController.getPlayer(playerId);
				if(target == null)
					target = worldManager.getPlayer(playerId);
				if(target == null)
				{
					sendAlert(ErrorCode.EXCEPTION_LOGIN_PLAYERNOTATGAME);
					return;
				}
				PetTome pts = (PetTome) target.getPlayer().getExtPlayerInfo("petTome");
				pet = pts.getActiveBattlePet();
			}
			buffer.writeBoolean(pet!=null);
			buffer.writeInt(playerId);
			if(pet != null)
			{
				pet.writeBattlePetTo(buffer);
				
				pet.sendBattlePetInfo(this, 4,playerId);
			}
		}
		else if(type == 2)//查看守护信息列表
		{
			Pet[] pets = pt.getPets();
			int count = 0;
			for (int i = 0; i < pets.length; i++) 
			{
				if(pets[i] == null)
					continue;
				if(pets[i].petType != Pet.SHOUHUPET)
					continue;
				count++;
			}
			buffer.writeByte(count);
			for (int i = 0; i < pets.length; i++) 
			{
				if(pets[i] == null)
					continue;
				if(pets[i].petType != Pet.SHOUHUPET)
					continue;
				pets[i].writeBattlePetTo(buffer);
				if(pets[i].isActive)
					pets[i].sendBattlePetInfo(this, 4,getID());
			}
		}
		else if(type == 3)//激活或者收回守护
		{
			if(getParent() instanceof BattleController)
				return;
			int petId = inBuffer.readInt();
			boolean isActive = inBuffer.readBoolean();
			Pet pet = pt.getPetById(petId);
			if(pet == null)
			{
				return;
			}
			if(pet.isActive == isActive)
			{
				return;
			}
			if(isActive)
			{
				pt.clearActive(pet);
			}
			pet.isActive = isActive;

			player.setDefaultData(1);
			player.uptateBuffPoint(0);
			
			pet.sendBattlePetInfo(this,1,getID());
			if(isActive)
				pet.sendBattlePetInfo(this,4,getID());
			return;
		}
		else if(type == 4)//设置技能状态
		{
			if(getParent() instanceof BattleController)
				return;
			Pet pet = pt.getActiveBattlePet();
			if(pet == null)
				return; 
			int skillId = inBuffer.readInt();
			boolean isActive = inBuffer.readBoolean();
			PetSkill skill = pet.getPetSkillById(skillId);
			if(skill == null)
				return;
			if(!skill.isStudied)
				return;
			skill.isActive = isActive;
//			pet.writeSkill(buffer);
			return;
		}
		else if(type == 6)//守护技能开始学习
		{
			if(getParent() instanceof BattleController)
				return;
			int skillId = inBuffer.readInt();
			int petId = inBuffer.readInt();
			if(pt.getStudyingSkillBattlePet() != null)
			{
				sendAlert(ErrorCode.ALERT_PET_STUDYING_SKILL);
				return;
			}
			Pet pet = pt.getBattlePet(petId);
			if(pet == null)
				return;

			PetSkill skill = pet.getPetStudySkillById(skillId);
			if(skill == null)
				return;

			PetSkillStudy pss = DataFactory.getInstance().getPetSkillStudyBySkill(skill);//需要根据客户端传过来 的技能通过技能的iconId取得PetSkillStudy对象
			if(pss == null)
				return;

			boolean result = pet.studySkill(this, skill,pss);
			
			if(!result)
				return;

			buffer.writeInt(petId);
			buffer.writeInt(skill.skillType);
			buffer.writeInt(pss.needTime);
		}
		else if(type == 7)//守护完成技能学习
		{
			int sType = inBuffer.readByte();
			int skillType = inBuffer.readInt();
			int petId = inBuffer.readInt();
			if(pt.getStudyingSkillBattlePet() == null)
			{
				sendAlert(ErrorCode.ALERT_NOTPET_STUDYING_SKILL);
				return;
			}
			Pet pet = pt.getBattlePet(petId);
			if(pet == null)
				return;
			PetSkill skill = pet.getPetStudySkillByType(skillType);
			if(skill == null)
			{	
				return;			
			}
			if(!skill.isStudying)
				return;
			PetSkillStudy pss = DataFactory.getInstance().getPetSkillStudyBySkill(skill);//需要根据客户端传过来 的技能通过技能的skillType取得PetSkillStudy对象
			if(pss == null)
				return;
		
			boolean result = pet.studyEnd(this, sType, skill,pss);
	
			if(!result)
				return;
			
			pet.sendBattlePetInfo(this,4,getID());
		}
		else if(type == 8)//守护洗点
		{
			if(getParent() instanceof BattleController)
				return;
			Pet pet = pt.getActiveBattlePet();
			if(pet == null)
				return;
		
			boolean result = pet.flushGrowPoint(this);
			
			if(!result)
				return;
			pet.writeBattlePetTo(buffer);
		}
		else if(type == 10)//脱下装备
		{
			if(getParent() instanceof BattleController)
				return;
			Pet pet = pt.getActiveBattlePet();
			if(pet == null)
				return;
			long objectIndex = Long.parseLong(inBuffer.readUTF());
			GoodsPetEquip equip = pet.getEquipByObjectIndex(objectIndex);
			if(equip == null)
				return;
			boolean result = equip.onRemoveImpl(this);
			if(!result)
			{
				sendAlert(ErrorCode.ALERT_GOODSEQUIP_TAKEOFF_ERROR);
				return;
			}
			buffer.writeUTF(equip.objectIndex+"");
			buffer.writeInt(equip.equipLocation);
			if(getParent() instanceof BattleController)
			{
				PlayerBattleTmp pbt = (PlayerBattleTmp) getAttachment();
				pbt.writeTo(buffer);
			}
			else
			{
				EquipSet es = (EquipSet) getPlayer().getExtPlayerInfo("equipSet");
				es.writeTo(buffer);
			}
			
			TaskInfo info = (TaskInfo)getPlayer().getExtPlayerInfo("taskInfo");
			info.onPlayerGotItem(equip.id,1, this);
		}
		else if(type == 11)//给守护恢复体力
		{
			if(getParent() instanceof BattleController)
				return;
			Pet pet = pt.getActiveBattlePet();
			if(pet == null)
			{
				sendAlert(ErrorCode.ALERT_PET_IS_NULL);
				return;
			}
			if(pet.isHuifuTili)
			{
				sendAlert(ErrorCode.ALERT_PET_ISHUIFUTILI_TRUE);
				return;
			}
			if(pet.getMaxBattlePoint()-pet.battlePoint > 100)
			{
				sendAlert(ErrorCode.ALERT_PET_NOT_HUIFUTILI);
				return;
			}
			pet.battlePoint -= 100;
			if(pet.battlePoint < 0)
				pet.battlePoint = 0;
			pet.isHuifuTili = true;
			pet.sendBattlePetInfo(this, 1,getID());
			
			buffer.writeUTF(pet.name);
			buffer.writeInt(100);
		}
		else if(type == 12)//炼化守护装备
		{
			if(getParent() instanceof BattleController)
				return;
			long objectIndex = Long.parseLong(inBuffer.readUTF());
			Bag bag = (Bag) player.getExtPlayerInfo("bag");
			Goods goods = bag.getGoodsByObjectIndex(objectIndex);
			if(goods == null)
			{
				sendAlert(ErrorCode.ALERT_GOODS_NOT_EXIST);
				return;
			}
			if(!(goods instanceof GoodsPetEquip))
			{
				return;
			}
			boolean result = ((GoodsPetEquip)goods).extAttUp(this);
			
			if(!result)
				return;
			
			buffer.writeUTF(objectIndex+"");
			goods.writeTo(buffer);
		}
		else if(type == 13)//装备精炼
		{
			if(getParent() instanceof BattleController)
				return;
			long objectIndex = Long.parseLong(inBuffer.readUTF());
			Bag bag = (Bag) player.getExtPlayerInfo("bag");
			Goods goods = bag.getGoodsByObjectIndex(objectIndex);
			if(goods == null)
			{
				sendAlert(ErrorCode.ALERT_GOODS_NOT_EXIST);
				return;
			}
			if(!(goods instanceof GoodsPetEquip))
			{
				return;
			} 
			int grow1 = ((GoodsPetEquip)goods).growPoint;
			boolean result = ((GoodsPetEquip)goods).growUp(this);
			
			if(!result)
				return;
			
			int grow2 = ((GoodsPetEquip)goods).growPoint;
			if(grow2 > grow1)
				buffer.writeBoolean(true);
			else
				buffer.writeBoolean(false);
			buffer.writeInt(grow2);
			buffer.writeUTF(objectIndex+"");
			goods.writeTo(buffer);
		}
		else if(type == 14)//守护转生
		{
			if(getParent() instanceof BattleController)
				return;
			Pet pet = pt.getActiveBattlePet();
			if(pet == null)
				return;
			boolean result = pet.upRole(this);
			if(!result)
				return;
		}
		else if(type == 15)//查看某个守护的技能
		{
			int petId = inBuffer.readInt();
			Pet pet = pt.getBattlePet(petId);
			if(pet == null)
				return;
			pet.writeSkill(buffer);
		}
		else if(type == 16)//请求玩家可以领养的守护
		{
			if(getParent() instanceof BattleController)
				return;
			Map map = (Map) DataFactory.getInstance().getAttachment(DataFactory.GET_PET_RULE);
			if(map == null)
				return;
			Object obj = map.get(getPlayer().upProfession+4);
			if(obj == null)
				return;
			String pets = obj.toString();
			if(pets.length() == 0)
				return;
			String[] strs = Utils.split(pets, ":");
			buffer.writeByte(strs.length);
			for (int i = 0; i < strs.length; i++) 
			{
				GoodsProp prop = (GoodsProp) DataFactory.getInstance().getGameObject(Integer.parseInt(strs[i]));
				Pet pet = (Pet) DataFactory.getInstance().getGameObject(prop.petId);
				buffer.writeInt(prop.id);
				buffer.writeInt(prop.point);
				buffer.writeInt(prop.token);
				buffer.writeInt(prop.money);
				buffer.writeBoolean(pt.getBattlePet(pet.id)!=null);
				pet.writeBattlePetTo(buffer);
			}
		}
		else if(type == 17)
		{
			if(getParent() instanceof BattleController)
				return;
			Pet pet = pt.getActiveBattlePet();
			if(pet == null)
				return;
			String name = inBuffer.readUTF();
			name = MarkChar.replace(name);
			
			if(!UseChar.isCanReg(name))
			{
				ByteBuffer out = new ByteBuffer(4);
				out.writeInt(ErrorCode.ALERT_HUOXINGWEN);
				netConnection.sendMessage(new SMsg(SMsg.S_EXCEPTION_MESSAGE, out));
				return;
			}
			
			if(name.isEmpty() || name.length() > 7 || name.trim().length() == 0)
				return;
			pet.setName(name);
			buffer.writeUTF(name);
		}
		netConnection.sendMessage(new SMsg(SMsg.S_PLAYER_BATTLE_PET_BASE_COMMAND,buffer));
		
		sendAlwaysValue();
	}
	
	private void sendBattlePetStudySkillInfo()
	{
		PetTome pt = (PetTome) getPlayer().getExtPlayerInfo("petTome");
		Pet pet = pt.getStudyingSkillBattlePet();
		if(pet == null)
			return;
		for (int i = 0; i < pet.getPetSkills().length; i++)
		{
			PetSkill ps = pet.getPetSkills()[i];
			if(ps != null && ps.isStudying)
			{
				PetSkillStudy pss = DataFactory.getInstance().getPetSkillStudyBySkill(ps);//需要根据客户端传过来 的技能通过技能的iconId取得PetSkillStudy对象
				if(pss == null)
					return;
				long t = WorldManager.currentTime - pet.trainTime;
				if(t < pss.needTime)
				{
					ByteBuffer buffer = new ByteBuffer();
					buffer.writeByte(6);
					buffer.writeInt(pet.id);
					buffer.writeInt(ps.skillType);
					buffer.writeInt((int) (pss.needTime-t));
					getNetConnection().sendMessage(new SMsg(SMsg.S_PLAYER_BATTLE_PET_BASE_COMMAND,buffer));
					return;
				}
				else
				{
					pet.studyEnd(this, 1, ps, pss);
					return;
				}
			}
		}
		
	}
	
	
	/**
     * 与客户端通信
     * @param msg
     */
	public void clientMessageChain(AppMessage msg)
	{
		int type = msg.getType();
		
		if(type == SMsg.C_PLAYER_INFO_COMMAND)
		{
			sendInfo();
			sendExpiredGoods();
			checkGuideStep();
			sendBattlePetStudySkillInfo();
		}
		else if(type == SMsg.C_PLAYER_ONKEYMOVE_COMMAND)
		{
			keyMoveTo(msg);
		}
		else if(type == SMsg.C_PLAYER_ONMOUSEMOVE_COMMAND)
		{
			mouseMoveTo(msg);
		}
		else if(type == SMsg.C_GET_PLAYER_EQUIPSET_COMMAND)
		{
			sendPlayerEquipSet(msg);
		}	
		else if(type == SMsg.C_GET_TASKS_COMMAND)
		{
			getTasks(msg);
		}
		else if(type == SMsg.C_CANCEL_TASKS_COMMAND)
		{
			cancelTask(msg);
		}
		else if(type == SMsg.C_PLAYER_REQUEST_TEAM_COMMAND)
		{
			requestTeam(msg);
		}
		else if(type == SMsg.C_ADD_TEAM_COMMAND)
		{
			responseTeam(msg);
		}
		else if(type == SMsg.C_CHAT_COMMAND)
		{
			processChat(msg.getBuffer());
		}
		else if(type == SMsg.C_GET_SKILL_COMMAND)
		{
			sendSkill(msg);
		}
		else if(type == SMsg.C_PLAYER_REQUEST_PK_COMMAND)
		{
			requestPK(msg);
		}
		else if(type == SMsg.C_PLAYER_RESPONSE_PK_COMMAND)
		{
			responsePK(msg);
		}
		else if(type == SMsg.C_PLAYER_FRIENDLIST_COMMAND)
		{
			friendProcess(msg.getBuffer());
		}
		else if(type == SMsg.C_PLAYER_BUSINESS_REQUEST_COMMAND)
		{
			requestBusiness(msg);
		}
		else if(type == SMsg.C_PLAYER_BUSINESS_RESPONSE_COMMAND)
		{
			responseBusiness(msg);
		}
		else if(type == SMsg.C_PLAYER_CAMP_SET_COMMAND)
		{
			setCamp(msg);
		}
		else if(type == SMsg.C_AUTO_SKILLTOME_COMMAND)
		{
			autoSkillTome(msg);
		}
		else if(type == SMsg.C_CLEAR_ESC_COMMAND)
		{
			responseEscTimer();
		}
		else if(type == SMsg.C_PLAYER_PET_INFO_COMMAND)
		{
			sendPetInfo(msg);
		}
		else if(type == SMsg.C_PLAYER_PETINFO_OPTION_COMMAND)
		{
			setPetInfoOption(msg);
		}
		else if(type == SMsg.C_EXP_BUFF_COMMAND)
		{
			sendExpBuff();
		}
		else if(type == SMsg.C_MONEY_BOX_COMMMAND)
		{
			moneyBox(msg);
		}
		else if(type == SMsg.C_PLAYER_MAIL_COMMAND)
		{
			mailProcess(msg);
		}
		else if(type == SMsg.C_PLAYER_FEED_PET_COMMAND)
		{
			feedPet(msg);
		}
		else if(type == SMsg.C_PLAYER_UPDATEACTIVEPOINT_COMMAND)
		{
			sendFlyActivePoint();
		}
		else if(type == SMsg.C_PLAYER_UPDATEEVENTPOINT_COMMAND)
		{
			ByteBuffer buffer = new ByteBuffer(4);
			buffer.writeInt(player.eventPoint);
			getNetConnection().sendMessage(new SMsg(SMsg.S_PLAYER_UPDATEEVENTPOINT_COMMAND,buffer));
		}
		else if(type == SMsg.C_GM_POST_COMMAND)
		{
			gmCmdInfo(msg);
		}
		else if(type == SMsg.C_PLAYER_FAMILYREQUEST_COMMAND)
		{
			if(getParent() instanceof BattleController)
				return;
			if(FamilyPartyController.getInstance().isReady())
			{
				if(roomController.isPartyRoom() || roomController.isPartyPKRoom())
				{
					sendAlert(ErrorCode.ALERT_IS_FAMILY_START);
					return;
				}
			}
			if(FamilyPartyController.getInstance().isStarted())
			{
				sendAlert(ErrorCode.ALERT_IS_FAMILY_START);
				return;
			}
			familyRequest(msg.getBuffer());//family
		}
		else if(type == SMsg.C_PLAYER_FAMILYRESPONSE_COMMAND)
		{
			if(getParent() instanceof BattleController)
				return;
			if(FamilyPartyController.getInstance().isReady())
			{
				if(roomController.isPartyRoom() || roomController.isPartyPKRoom())
				{
					sendAlert(ErrorCode.ALERT_IS_FAMILY_START);
					return;
				}
			}
			if(FamilyPartyController.getInstance().isStarted())
			{
				sendAlert(ErrorCode.ALERT_IS_FAMILY_START);
				return;
			}
			familyResponse(msg.getBuffer());//family
		}
		else if(type == SMsg.C_PLAYER_FAMILYLIST_COMMAND)
		{
			familyList();//family
		}
		else if(type == SMsg.C_PLAYER_FAMILYKICK_COMMAND)
		{
			if(getParent() instanceof BattleController)
				return;
			if(FamilyPartyController.getInstance().isReady())
			{
				if(roomController.isPartyRoom() || roomController.isPartyPKRoom())
				{
					sendAlert(ErrorCode.ALERT_IS_FAMILY_START);
					return;
				}
			}
			if(FamilyPartyController.getInstance().isStarted())
			{
				sendAlert(ErrorCode.ALERT_IS_FAMILY_START);
				return;
			}
			familyKick(msg.getBuffer());//family
		}
		else if(type == SMsg.C_PLAYER_FAMILYREMOVE_COMMAND)
		{
			if(getParent() instanceof BattleController)
				return;
			if(FamilyPartyController.getInstance().isReady())
			{
				if(roomController.isPartyRoom() || roomController.isPartyPKRoom())
				{
					sendAlert(ErrorCode.ALERT_IS_FAMILY_START);
					return;
				}
			}
			if(FamilyPartyController.getInstance().isStarted())
			{
				sendAlert(ErrorCode.ALERT_IS_FAMILY_START);
				return;
			}
			familyRmove();//family
		}
		else if(type == SMsg.C_PLAYER_FAMILYOUT_COMMAND)
		{
			if(getParent() instanceof BattleController)
				return;
			if(FamilyPartyController.getInstance().isReady())
			{
				if(roomController.isPartyRoom() || roomController.isPartyPKRoom())
				{
					sendAlert(ErrorCode.ALERT_IS_FAMILY_START);
					return;
				}
			}
			if(FamilyPartyController.getInstance().isStarted())
			{
				sendAlert(ErrorCode.ALERT_IS_FAMILY_START);
				return;
			}
			familyOut();//family
		}
		else if(type == SMsg.C_PLAYER_FAMILYLEADERCHANGE_COMMAND)
		{
			if(getParent() instanceof BattleController)
				return;
			if(FamilyPartyController.getInstance().isReady())
			{
				if(roomController.isPartyRoom() || roomController.isPartyPKRoom())
				{
					sendAlert(ErrorCode.ALERT_IS_FAMILY_START);
					return;
				}
			}
			if(FamilyPartyController.getInstance().isStarted())
			{
				sendAlert(ErrorCode.ALERT_IS_FAMILY_START);
				return;
			}
			familyChange(msg.getBuffer());//family
		}
		else if(type == SMsg.C_PLAYER_CHAT_CARTOON_COMMAND)
		{
			String chatMsg = msg.getBuffer().readUTF();
			ByteBuffer buffer = new ByteBuffer();
			buffer.writeInt(getID());
			buffer.writeUTF(chatMsg);
			roomController.dispatchMsg(SMsg.S_PLAYER_CHAT_CARTOON_COMMAND, buffer);
		}
		else if(type == SMsg.C_PLAYER_NEW_COMMAND)
		{
			int newStudyProcess = msg.getBuffer().readInt();
			if(newStudyProcess != -1)
			{
				player.newStudyProcess = (byte) newStudyProcess;
			}

			ByteBuffer buffer = new ByteBuffer(4);
			buffer.writeInt(player.newStudyProcess);
			netConnection.sendMessage(new SMsg(SMsg.S_PLAYER_NEW_COMMAND,buffer));
		}
		else if(type == SMsg.C_PLAYER_GET_TOP_COMMAND)
		{
			int index = msg.getBuffer().readByte();
			TopCenter.getInstance().getTop(this,index);
		}
		else if(type == SMsg.C_PLAYER_MODIFY_CAMP_COMMAND)
		{
			modifyCamp(msg.getBuffer());
		}
		else if(type == SMsg.C_PLAYER_MODIFY_NAME_COMMAND)
		{
			modifyName(msg.getBuffer());
		}
		else if(type == SMsg.C_PLAYER_SET_TEAM_STATE_COMMAND)
		{
			setTeamState(msg.getBuffer());
		}
		else if(type == SMsg.C_PLAYER_APPLY_TEAM_COMMAND)
		{
			applyTeam(msg.getBuffer());
		}
		else if(type == SMsg.C_PLAYER_REWARDINFO_COMMAND)
		{
			checkRewardInfo(msg.getBuffer());
		}
		else if(type == SMsg.C_PLAYER_AUTO_BATTLE_COMMAND)
		{
			setAutoBattle(msg);
		}
		else if(type == SMsg.C_PLAYER_AUTO_BATTLE_INFO_COMMAND)
		{
			sendAutoData();
		}
		else if(type == SMsg.C_PLAYER_VALIDATE_IDCARD_COMMAND)
		{
			idcardValidate(msg);
		}
		else if(type == SMsg.C_PLAYER_MASTER_INVI_APP_COMMAND)
		{
			requestAam(msg);
		}
		else if(type == SMsg.C_PLAYER_APP_RESPONSE_MASTER_COMMAND)
		{
			responseAam(msg);
		}
		else if(type == SMsg.C_PLAYER_OPTION_MAS_APP_COMMAND)
		{
			if(aam == null)
				return;
			if(getParent() instanceof BattleController)
				return;
			aam.optionM_A(this, msg.getBuffer().readByte());
		}
		else if(type == SMsg.C_PLAYER_REQUEST_MARRY_COMMAND)
		{
			requestMarry(msg);
		}
		else if(type == SMsg.C_PLAYER_RESPONSE_MARRY_COMMAND)
		{
			responseMarry(msg);
		}
		else if(type == SMsg.C_PLAYER_CANCEL_MARRY_COMMAND)
		{
			cancelMarry();
		}
		else if(type == SMsg.C_PLAYER_CHANGE_SEX_COMMAND)
		{
			changeSex();
		}
		else if(type == SMsg.C_PLAYER_CHANGE_PET_COMMAND)
		{
			changePet();
		}
		else if(type == SMsg.C_PLAYER_UP_ROLE_COMMAND)
		{
			upRole();
		}
		else if(type == SMsg.C_PLAYER_GUIDE_STEP_COMMAND)
		{
			guideAnswerStep(msg.getBuffer()); 
		}
		else if(type == SMsg.C_PLAYER_GOLD_BOX_COMMAND)
		{
			processGoldBox(msg.getBuffer());
		}
		else if(type == SMsg.C_PLAYER_EVERYDAY_REWARD_COMMAND)
		{
			getEverydayReward(msg.getBuffer());
		}
		else if(type == SMsg.C_PLAYER_FACEBOOK_INFO_COMMAND)
		{
			sendChengjiu(msg.getBuffer());
		}
		else if(type == SMsg.C_PLAYER_BATTLE_PET_BASE_COMMAND)
		{
			battlePetBaseInfo(msg.getBuffer());
		}
	}

	
	
	
}
