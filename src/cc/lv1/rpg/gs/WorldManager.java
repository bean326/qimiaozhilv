package cc.lv1.rpg.gs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import vin.rabbit.net.AppMessage;
import vin.rabbit.net.abs.NetConnection;
import vin.rabbit.net.job.JobObserver;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.impl.ArrayList;
import vin.rabbit.util.collection.impl.HashMap;
import cc.lv1.rpg.gs.dao.DatabaseAccessor;
import cc.lv1.rpg.gs.data.DC;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.data.Exp;
import cc.lv1.rpg.gs.data.PressureTest;
import cc.lv1.rpg.gs.entity.ConfirmJob;
import cc.lv1.rpg.gs.entity.ShopCenter;
import cc.lv1.rpg.gs.entity.container.PlayerContainer;
import cc.lv1.rpg.gs.entity.controller.AreaController;
import cc.lv1.rpg.gs.entity.controller.BattleController;
import cc.lv1.rpg.gs.entity.controller.BusinessController;
import cc.lv1.rpg.gs.entity.controller.CopyController;
import cc.lv1.rpg.gs.entity.controller.CopyPVEController;
import cc.lv1.rpg.gs.entity.controller.FamilyController;
import cc.lv1.rpg.gs.entity.controller.FamilyPartyController;
import cc.lv1.rpg.gs.entity.controller.GoldPartyController;
import cc.lv1.rpg.gs.entity.controller.NpcController;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.controller.RoomController;
import cc.lv1.rpg.gs.entity.controller.TeamController;
import cc.lv1.rpg.gs.entity.ext.AnswerParty;
import cc.lv1.rpg.gs.entity.ext.Bag;
import cc.lv1.rpg.gs.entity.ext.OtherExtInfo;
import cc.lv1.rpg.gs.entity.ext.PetTome;
import cc.lv1.rpg.gs.entity.ext.Storage;
import cc.lv1.rpg.gs.entity.ext.StoryEvent;
import cc.lv1.rpg.gs.entity.impl.Goods;
import cc.lv1.rpg.gs.entity.impl.Mail;
import cc.lv1.rpg.gs.entity.impl.PartyReward;
import cc.lv1.rpg.gs.entity.impl.Player;
import cc.lv1.rpg.gs.entity.impl.RankReward;
import cc.lv1.rpg.gs.entity.impl.Reward;
import cc.lv1.rpg.gs.entity.impl.RewardSender;
import cc.lv1.rpg.gs.entity.impl.Shop;
import cc.lv1.rpg.gs.entity.impl.pet.Pet;
import cc.lv1.rpg.gs.entity.impl.story.Event;
import cc.lv1.rpg.gs.gui.MainFrame;
import cc.lv1.rpg.gs.load.impl.LocalDataLoader;
import cc.lv1.rpg.gs.net.BaseConnection;
import cc.lv1.rpg.gs.net.SMsg;
import cc.lv1.rpg.gs.net.impl.AnswerRewardJob;
import cc.lv1.rpg.gs.net.impl.CopyJob;
import cc.lv1.rpg.gs.net.impl.GetTopJob;
import cc.lv1.rpg.gs.net.impl.OnlineExpJob;
import cc.lv1.rpg.gs.net.impl.PressureJob;
import cc.lv1.rpg.gs.net.impl.SaveJob;
import cc.lv1.rpg.gs.other.ErrorCode;
/**
 * 世界管理器，维护整个游戏世界
 *
 */
public class WorldManager extends PlayerContainer
{
	public static long currentTime = 0;
	
	private static final long SAVEDATETIMER = 1000*60*3;
	
	private long saveTimer = 0;
	
	private int checkCount =0;
	
	private int areaLength ;
	
	private GameServer gameServer;
	
	private JobObserver jobObserver;
	
	private DatabaseAccessor databaseAccessor;
	
	public List postList = new ArrayList(30);
	
	private List familyList = new ArrayList(100);
	
	private List connections = new ArrayList(200);
	
	private int connectionMax = 0;
	
	private HashMap playerMap = new HashMap(1000);
	
	private List shopList = new ArrayList(200);
	
	/** 答题活动排名前10 */
	private List answerRewards = new ArrayList(10);
	
	/** 总的副本排行前20(当天的) */
	private List copyTodayTop = new ArrayList(20);
	
	/** 总的副本排行前20(昨天的) */
	private List copyYesTop = new ArrayList(20);
	
	/** 总的副本排行第一名 */
	private PartyReward bestPlayer = new PartyReward("first",1,1,WorldManager.currentTime);
	
	
	/** 上一次答题发放奖励日期 */
	public static byte sendRewardDate; 
	
	/** 上一次关闭服务器时间 */
	public static byte closeDate;
	
	/** 禁止Ip */
	private List pIps = new ArrayList(100);

	/** 区域 */
	private AreaController [] areaControllers = null;
	
	/** 当前日期 */
	public static byte date;
	
	/** 当前weekdate */
	public static byte weekDate;
	
	/** 到明天的时间 */
	public static long nextDayTime;
	
	/** 到明天的更新排行时间 */
	public static long nextDayTopTime = Long.MAX_VALUE;

	public synchronized void addPlayerConntroller(PlayerController controller)
	{
		controller.setWorldManager(this);
		controller.setOnline(true);
		playerMap.put(controller.getName(), controller);
		playerList.add(controller);
	}
	
	public synchronized void removePlayerController(PlayerController controller)
	{
		controller.setWorldManager(null);
		controller.setOnline(false);
		playerMap.remove(controller.getName());
		playerList.remove(controller);
	}
	
	public void onConnectionOpened(NetConnection conn)
	{
		if(isInProIpBox(conn))
		{
			conn.close();
			return;
		}
		
		synchronized(conn)
		{
			connections.add(conn);
		}
		connectionMax++;
	}
	
	public void onConnectionClosed(NetConnection conn)
	{
		if(conn == null)
			return;
		
		Object controller = conn.getInfo();
		
		try
		{
			if(controller != null)
			{
				if(controller instanceof PlayerController)
				{
					PlayerController target = (PlayerController) controller;
					RoomController room = target.getRoom();
					
					if(target.getParent() instanceof BattleController)
					{
						if(target.getParent() instanceof CopyPVEController)
						{
							CopyPVEController copy = (CopyPVEController) target.getParent();
							copy.setWinOrLoseInfo(false);
						}
						if(room != null)
						{
							int roomId = room.getRebirthId(target.getPlayer().camp);
							if(room.isPartyPKRoom() || room.isPartyRoom())
							{
								roomId = DataFactory.INITROOM;
							}
							if(room.isGoldPartyPKRoom || room.isGoldPartyRoom)
							{
								roomId = GoldPartyController.PARTYINITROOM;
								GoldPartyController.getInstance().clearWinAndLoseCount(target);
								GoldPartyController.getInstance().setRoomId(target, 0);
							}
							if(room.isStoryRoom || target.getEvent() != null)
							{	
								if(target.getEvent() != null && target.getEvent().getStory() != null)
								{
									if(target.getEvent().getStory().type != 4)
										roomId = DataFactory.STORYROOM;
									else
									{
										if(target.getEvent().eventType == 1)
											roomId = DataFactory.INITROOM;
									}
								}
								else
									roomId = DataFactory.STORYROOM;
							}
							
							if(room.getCopy() != null)
							{
								if(room.getCopy().type == 2)
									roomId = DataFactory.HONORROOM;
							}
							target.getPlayer().setDefaultRoom(roomId);
						}
						
					}
					
					if(room != null)
					{
						if(target.getPlayer().eventPoint >= room.getEventPoint())
						{
							if(target.getPlayer().eventPoint < 0)
							{
								target.getPlayer().eventPoint = 0;
							}
						}
					}
					
					
					removePlayerController(target);
						
					PlayerContainer container = target.getParent();

					if(container != null)
					{
						container.removePlayer(target);
					}
					if(target.getTeam() != null)
					{
						room.deleteTeam(target.getTeam());
						
						target.getTeam().playerLeaveTeam(target);
					}
					
					if(target.getAam() != null)
					{
						target.getAam().onLeave(target);
					}
					
					FamilyController family = target.getFamily();
					if(family !=null)
					{
						family.removePlayer(target);
					}						
					if(FamilyPartyController.getInstance().isPlayerIn(target))
					{
						FamilyPartyController.getInstance().removePlayer(target);
					}
					else if(GoldPartyController.getInstance().isPlayerIn(target))
					{
						GoldPartyController.getInstance().removePlayer(target);
					}

					if(target.wgCount != 0xffff) //没有封号的时候才保存
					{
						getJobObserver().addJob(GameServer.JOB_DATABASEJOB,
								new SaveJob(this,target.getPlayer(),SaveJob.CLOSE_SAVE));
					}
					conn.setInfo(null);
				}
				else if(controller instanceof Player)
				{
					Player player = (Player)controller;
					getJobObserver().addJob(GameServer.JOB_DATABASEJOB,
							new SaveJob(this,player,SaveJob.CLOSE_SAVE));
					conn.setInfo(null); 
				}
			}
		}
		catch(Exception e)
		{
			MainFrame.println("WorldManager : "+e.getMessage());
			
			if(controller != null)
			{
				if(controller instanceof PlayerController)
				{
					PlayerController target = (PlayerController) controller;
					removePlayerController(target);
					getJobObserver().addJob(GameServer.JOB_DATABASEJOB,
							new SaveJob(this,target.getPlayer(),SaveJob.CLOSE_ERROR_SAVE));
				}
			}
		}
		
		synchronized(conn)
		{
			connections.remove(conn);
		}
	}
	
	public void update(long timeMillis)
	{
		
		WorldManager.nextDayTime -=500;
		WorldManager.nextDayTopTime -=500;

		
		if(timeMillis > saveTimer +SAVEDATETIMER)
		{
			saveTimer = timeMillis;

			for (int i = 0; i < playerList.size(); i++)
			{
				PlayerController target= (PlayerController)playerList.get(i);
				
				if(target == null)
					continue;
				
				if(target.getNetConnection() != null)
					target.getNetConnection().setReceiveMsgCount(0);
				
				
				
				if(!(target.getParent() instanceof BattleController))
				{
					if(target.getPlayer().level > 10) //玩家大于10级才有挂机经验
					{
						Exp currentExp = target.getExpByLevel(target.getPlayer().level+1,1);
						if(currentExp != null)
						{
							getJobObserver().addJob(GameServer.JOB_GAME1,
									new OnlineExpJob(currentExp,target));
						}
					}
				}


				if(!target.isOnline() || target.getNetConnection() == null)
				{
					if(!target.close())
					{
						MainFrame.println("World clear "+target.getName()+" with ing...");
						target.wgCount = 0xffff;
						
						removePlayerController(target);
						
						MainFrame.println("World clear "+target.getName()+" with success");
						
						BaseConnection nc = new BaseConnection(null);
						nc.setInfo(target);
						onConnectionClosed(nc);

						
					}
					continue;
				}
				
				if(target.isSaved)
				{
					target.isSaved = false;
					continue;
				}
				else
				{
					target.isSaved = true;
				}
				
				getJobObserver().addJob(GameServer.JOB_DATABASEJOB,
						new SaveJob(this,target.getPlayer(),SaveJob.TIME_SAVE));
			}
			
			getDatabaseAccessor().savedObjIndexs();
			
			getJobObserver().addJob(GameServer.JOB_GAME2,new PressureJob(1));
			
			if(++checkCount >= 1)
			{
				checkCount = 0;
				for (int i = 0; i < connections.size(); i++)
				{
					NetConnection  net = (NetConnection)connections.get(i);
					
					if(net == null)
						continue;
					
					if(timeMillis > net.getPingTime()+SAVEDATETIMER*2)
					{
						net.close();
					}
				}
			}
		}
		
		if(WorldManager.nextDayTopTime <= 0)
		{
			WorldManager.nextDayTopTime = 1000*60*60*24;
			getJobObserver().addJob(GameServer.JOB_GAME2, new GetTopJob());
		}
		
		if(WorldManager.nextDayTime <= 0)
		{

			int date = Utils.getCurrentDate(timeMillis);
			
			if(date != WorldManager.date) //确认一下是不是第二天
			{
				
				WorldManager.init(); //重新校对时间
				
				WorldManager.date = (byte) date;
				
				WorldManager.weekDate = (byte) WorldManager.getCurrentWeekDay();

//				ByteBuffer buffer = null;
				
				for (int i = 0; i < playerList.size(); i++)
				{
					PlayerController everyone =(PlayerController)playerList.get(i);
					
					if(everyone == null)
						continue;

					everyone.getPlayer().clearData();
					
					
					everyone.sendFlyActivePoint();

					AnswerParty ap = (AnswerParty) everyone.getPlayer().getExtPlayerInfo("answerParty");
					ap.clearAll();
					
					PetTome petTome = (PetTome) everyone.getPlayer().getExtPlayerInfo("petTome");
					petTome.clear();
					
					OtherExtInfo oei = (OtherExtInfo)everyone.getPlayer().getExtPlayerInfo("otherExtInfo");
					oei.clearAamCount();
					
					StoryEvent se = (StoryEvent) everyone.getPlayer().getExtPlayerInfo("storyEvent");
					se.clear(everyone, null);
					
					if(isZeroMorning(0))
					{
						if(everyone.isStory)
						{
							se.endStory(everyone,1);
							se.endStory(everyone,2);
							se.endStory(everyone,3);
						}
					}
				}	
				
				getJobObserver().addJob(GameServer.JOB_GAME1,new CopyJob(this));
				
				getJobObserver().addJob(GameServer.JOB_GAME2,new PressureJob(2));
			}
			else// ,如果不是第二天的话加3分钟
			{
				WorldManager.nextDayTime = 1000*60*3;
			}
		} 

		currentTime = timeMillis;
		
		for(int i = 0 ; i < areaLength ; i ++)
		{
			areaControllers[i].update(timeMillis);
		}
		
		
		int count = confirmations.size();
		for (int i = 0; i < count; i++)
		{
			ConfirmJob job = (ConfirmJob)confirmations.get(i);
			if (job.getLifeTime() == -0xffff)
			{
				confirmations.remove(i--);
				count--;
				continue;
			}
			job.setLifeTime(job.getLifeTime() - 500);
			if (!job.isAlive())
			{
				confirmations.remove(i--);
				count--;
				job.cancel();
			}
		}
		

		

		if(sendRewardDate != date)
		{
			int time = Integer.parseInt(getTypeTime("HH", timeMillis));

			if(time == AnswerParty.REWARDTIME)
			{
				sendRewardDate = date;
				getJobObserver().addJob(GameServer.JOB_GAME2, new AnswerRewardJob(this));
			}
		}
		
		
//		CampPartyController.getInstance().update(timeMillis);
		
		FamilyPartyController.getInstance().update(timeMillis);
		
		GoldPartyController.getInstance().update(timeMillis);
	}
	
	public void sendRankReward()
	{
		PlayerController target = null;
		Player player = null;
		Reward rp = null;
		RankReward rr = null;
		for (int i = 0; i < answerRewards.size(); i++) 
		{
			rp = (Reward) answerRewards.get(i);
			player = rp.getPlayer();
			rr = DataFactory.getInstance().getRankReward(1, rp.rank);
			if(rr == null || rp == null)
			{
				System.out.println("rr is null or rp is null!");
				continue;
			}
			if(player == null)
			{
				player = GameServer.getInstance().getDatabaseAccessor().getPlayer(rp.id);
				if(player != null)
				{
					target = new PlayerController(player);
					sendAnswerReward(target, rr, false);
				}
				else
				{
					System.out.println("sendRankReward database player is null:"+rp.id+"  name:"+rp.name);
				}
			}
			else
			{
				target = getPlayerControllerById(player.id);
				if(target == null)
				{
					target = new PlayerController(player);
					sendAnswerReward(target, rr, false);
				}
				else
				{
					sendAnswerReward(target, rr, true);
				}	
			}
		}
		
//		PressureTest.getInstance().saveData(answerRewards,1);
		
		answerRewards = new ArrayList();
	}
	
	/**
	 * 检查当前账号的用户是否在线，在返回当前用户连接，不在则返回空
	 * @param accountName
	 * @return
	 */
	public NetConnection checkPlayerIsOnline(String accountName)
	{
		for(int i = 0 ; i < connections.size() ; i ++)
		{
			NetConnection nc = (NetConnection)connections.get(i);
			
			if(nc == null)
				continue;
			
			Object playerInfo = nc.getInfo();
			
			if(playerInfo == null)
				continue;
			
			if(playerInfo instanceof Player)
			{
				Player player = (Player)playerInfo;
				if(player.accountName.equals(accountName))
					return nc;
				continue;
			}
			
			if(playerInfo instanceof PlayerController)
			{
				PlayerController target= (PlayerController)playerInfo;
				if(target.getPlayer().accountName.equals(accountName))
					return nc;
				continue;
			}
		}
		return null;
	}

	/**
	 * 玩家进入游戏世界
	 * @param player
	 * @param conn
	 */
	public PlayerController processEnterGame(Player player,NetConnection conn)
	{
		PlayerController target = new PlayerController(player);
		target.isSaved = 0==(int)(Math.random()*2);
		target.setNetConnection(conn);
		if(player.familyId != 0)
		{
			FamilyController family = getFamilyById(player.familyId);

			if(family == null)
			{
				target.sendAlert(ErrorCode.ALERT_FAMILY_NOT_EXIST);

				player.familyId = 0;
				player.familyName = "";
				player.isFamilyLeader = false;
			}
			else
			{
				if(family.isInFamily(target.getName()))
				{
					target.setFamilyController(family);
					family.addPlayer(target);
					if(player.isFamilyLeader && !family.leaderName.equals(player.name))
						player.isFamilyLeader = false;
					if(!player.isFamilyLeader && family.leaderName.equals(player.name))
						player.isFamilyLeader = true;
				}
				else
				{
					target.sendAlert(ErrorCode.ALERT_FAMILY_OUT);
					
					target.getPlayer().familyId = 0;
					target.getPlayer().familyName = "";
					target.getPlayer().isFamilyLeader = false;
				}
			}
			
		}
		
		conn.setInfo(target);
		

		for (int i = 0; i < playerList.size(); i++)
		{
			PlayerController everyone = (PlayerController)playerList.get(i);
			
			if(everyone == null)
				continue;
			
			if(everyone.getID() == target.getID()) //检查有无相同
			{
				
				synchronized(playerList)
				{
					playerList.remove(i);
				}
				
				MainFrame.println("WorldManager: "+everyone.getPlayer().accountName +" _ "+target.getPlayer().accountName+" ... "+
						" on Enter the game "+
						getPlayerControllerByAccountName(target.getPlayer().accountName));
				
				everyone.close();
				break;
			}
		}
		
		if(!GoldPartyController.getInstance().isEnded())
		{
			if(GoldPartyController.getInstance().getPlayerPoint(target.getID()) != null)
			{
				RoomController room = getRoomWolrd(player.roomId);
				if(room != null && (room.isGoldPartyRoom || room.isGoldPartyPKRoom))
					GoldPartyController.getInstance().addPlayer(target);
			}
		}
		
		StoryEvent se = (StoryEvent) player.getExtPlayerInfo("storyEvent");
		Event event = se.getDoingEvent();

		if(event != null)
		{
			if(event.eventType == 1)
			{
				if(event.chooseType != 0)
				{
					if(event.eventItems[event.chooseType-1][1] != 0)
					{
						for (int i = event.eventItems[event.chooseType-1][0]; i < event.eventItems[event.chooseType-1][1]+1; i++) 
						{
							if(player.roomId == i)
							{
								target.setEvent(event);
								break;
							}
						}
					}
				}
				else
				{
					for (int i = 0; i < event.eventItems.length; i++) 
					{
						if(event.eventItems[i][1] != 0)
						{
							for (int j = event.eventItems[i][0]; j < event.eventItems[i][1]+1; j++) 
							{
								if(player.roomId == j)
								{
									target.setEvent(event);
									break;
								}
							}
						}
					}
				}
			}
		}
		
		addPlayerConntroller(target);	
	
		return target;
	}


	public void clientMessageChain(AppMessage msg)
	{
		if(msg == null)
			return;
		
		NetConnection netConnection = (NetConnection)msg.getSource();
		if(netConnection == null)
			return;
		
		Object playerInfo = netConnection.getInfo();

		if(playerInfo == null)
			return;
		
		int markType = msg.getMarkType();
		
		if(!(playerInfo instanceof PlayerController))
			return;

		PlayerController target = (PlayerController)playerInfo;
	
		if(!target.isOnline())
			return;
		
		if(target.getNetConnection() == null)
			return;
		
		if(target.isAuto && !isAcceptMsg(target, msg.getType()))//玩家正在挂机打怪，其它指令全部屏蔽
		{
			target.sendGetGoodsInfo(1, false, DC.getString(DC.BATTLE_5));//挂机自动打怪中，不能进行相关操作
			return;
		}
		if(!isAcceptMsgByStory(target,msg.getType()))
		{
			target.sendGetGoodsInfo(1, false, DC.getString(DC.PARTY_51));
			return;
		}

		if(markType == SMsg.T_PLAYER_COMMAND)
		{
			target.clientMessageChain(msg);
		}
		else if(markType == SMsg.T_ROOM_COMMAND)
		{
			Object parent = target.getParent();

			if(parent != null && msg.getType() != SMsg.C_ROOM_ADD_COMMAND)
			{
				if(parent instanceof RoomController)
				{
					((RoomController)parent).clientMessageChain(target,msg);
				}
			}
			else
			{
				int areaId = msg.getBuffer().readInt();
				int roomId = msg.getBuffer().readInt();
                
				
				/**
				 * 首次登陆时检测 登陆房间是否是发送的房间
				 */
				if(target.isInitLogin && msg.getType() == SMsg.C_ROOM_ADD_COMMAND)
				{
					if(target.getPlayer().areaId != areaId || target.getPlayer().roomId != roomId)
					{
						target.close();
						return;
					}
					else
					{
						target.isInitLogin = false;
					}
				}

			
				
				AreaController area = getAreaById(areaId);
				
				if(area == null)
					return;
				RoomController room = area.getRoomById(roomId);
			
				if(room == null)
					return;
	
				room.clientMessageChain(target,msg);
			}
		}
		else if(markType == SMsg.T_NPC_COMMAND)
		{
			Object parent = target.getParent();
			
			if(parent == null)
				return;
			
			if(!(parent instanceof RoomController))
				return;
			
			int npcId = msg.getBuffer().readInt();
			if(npcId == 0)
				npcId = 1021090083;
			NpcController npc = ((RoomController)parent).getNpc(npcId);

			if(npc == null)
				return;
			
			npc.clientMessageChain(target,msg);
		}
		else if(markType == SMsg.T_BAG_COMMAND)
		{
			Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
			bag.clientMessageChain(target, msg);
		}
		else if(markType == SMsg.T_SHOP_COMMAND)
		{
			int shopId = msg.getBuffer().readInt();
			Shop shop = getShop(shopId);
			if(shop == null)
				return;
			if(!target.getRoom().isNpcInRoom(shopId) && shop.isPutongShop())
			{
				return;
			}
			shop.clientMessageChain(target, msg);
		}
		else if(markType == SMsg.T_STORAGE_COMMAND)
		{
			Storage storage = (Storage) target.getPlayer().getExtPlayerInfo("storage");
			storage.clientMessageChain(target, msg);
		}
		else if(markType == SMsg.T_BATTLE_COMMAND)
		{
			Object parent = target.getParent();
		
			if(parent == null)
				return;

			if(!(parent instanceof BattleController))
				return;

			((BattleController)parent).clientMessageChain(target,msg);
		}
		else if(markType == SMsg.T_TEAM_COMMAND)
		{
			if(target.getParent() instanceof BattleController)
				return;
			if(target.getParent() instanceof BusinessController)
				return;
			TeamController team = target.getTeam();
			if(team == null)
				return;
			team.clientMessageChain(target, msg);
		}
		else if(markType == SMsg.T_BUSINESS_COMMAND)
		{
			Object parent = target.getParent();

			if(parent == null)
				return;
			
			if(!(parent instanceof BusinessController))
				return;

			((BusinessController)parent).clientMessageChain(target,msg);
		}
		else if(markType == SMsg.T_ATTACHMENT_COMMAND)
		{
			if(msg.getType() == SMsg.C_SHOPCENTER_COMMAND)
			{
				ShopCenter center = ShopCenter.getInstance();
				center.clientMessageChain(target,msg);
			}
		}
		else if(markType == SMsg.T_ANSWER_COMMAND)
		{
			AnswerParty ap = (AnswerParty) target.getPlayer().getExtPlayerInfo("answerParty");
			ap.clientMessageChain(target, msg);
		}
		else if(markType == SMsg.T_PARTY_COMMAND)
		{
			if(msg.getType() == SMsg.C_GET_COPY_PLAYER_REWARD_COMMAND)
			{
				int areaId = target.getRoom().getParent().id;
				CopyController copy = DataFactory.getInstance().getCopyByArea(areaId);
				if(copy == null)
				{
//					System.out.println("WorldManager message copy is null,areadId:"+areaId);
					return;
				}
				else
				{
					copy.clientMessageChain(target, msg);
				}
			}
			else if(msg.getType() == SMsg.C_GET_COPY_PLAYER_RANK_COMMAND)
			{
				//查看所有副本集体排行
				sendTopPlayer(target);
			}
			else
			{
				int type = msg.getBuffer().readByte();
				if(type == 2)
				{
					FamilyPartyController.getInstance().clientMessageChain(target,msg);
				}
				else if(type == 4)
				{
					GoldPartyController.getInstance().clientMessageChain(target,msg);
				}
			}
		}
		else if(markType == SMsg.T_STORY_COMMAND)
		{
			StoryEvent se = (StoryEvent) target.getPlayer().getExtPlayerInfo("storyEvent");
			se.clientMessageChain(target, msg);
		}
	}
	
	
	
	public GameServer getGameServer()
	{
		return gameServer;
	}

	public void setGameServer(GameServer gameServer)
	{
		this.gameServer = gameServer;
	}

	public JobObserver getJobObserver()
	{
		return jobObserver;
	}

	public void setJobObserver(JobObserver jobObserver)
	{
		this.jobObserver = jobObserver;
	}

	public DatabaseAccessor getDatabaseAccessor()
	{
		return databaseAccessor;
	}

	public void setDatabaseAccessor(DatabaseAccessor databaseAccessor)
	{
		this.databaseAccessor = databaseAccessor;
	}

	public void setAreaController(AreaController[] areaControllers)
	{
		this.areaControllers = areaControllers;
		areaLength = areaControllers.length;
	}
	
	public AreaController getAreaById(int areaId)
	{
		for (int i = 0; i < areaLength; i++)
		{
			if(areaControllers[i].id == areaId)
				return areaControllers[i];
		}
		return null;
	}
	
	public AreaController[] getAreaControllers()
	{
		return areaControllers;
	}

	public PlayerController getPlayerController(String name)
	{
		return (PlayerController)playerMap.get(name);
	}
	
	public PlayerController getPlayerControllerById(int id)
	{
		for (int i = 0; i < playerList.size(); i++)
		{
			PlayerController player = (PlayerController) playerList.get(i);
			if(player.getPlayer().id == id)
				return player;
		}
		return null;
	}
	
	public PlayerController getPlayerControllerByAccountName(String account)
	{
		for (int i = 0; i < playerList.size(); i++)
		{
			PlayerController player = (PlayerController) playerList.get(i);
			if(player.getPlayer().accountName.equals(account))
				return player;
		}
		return null;
	}

	public Shop getShop(int shopId) {
		for (int i = 0; i < shopList.size(); i++) 
		{
			Shop shop = (Shop) shopList.get(i);
			if(shop.id == shopId)
			{
				return shop;
			}
		}
		return null;
	}

	public void setShopList(Shop shop)
	{
		this.shopList.add(shop);
	}
	
	public List getShopList()
	{
		return this.shopList;
	}

	private List confirmations = new ArrayList();
	
	public void addConfirmJob(ConfirmJob job)
	{
		confirmations.add(job);
	}
	
	public ConfirmJob getConfirmation(String name)
	{
		int count = confirmations.size();
		for (int i = 0; i < count; i++)
		{
			ConfirmJob job = (ConfirmJob)confirmations.get(i);
			if (job.getName().equals(name))
				return job;
		}
		return null;
	}

	
	public void addFamily(FamilyController family)
	{
		familyList.add(family);
	}
	
	public void removeFamily(FamilyController family)
	{
		familyList.remove(family);
	}
	
	public List getFamilyList()
	{
		return familyList;
	}
	
	public FamilyController getFamilyById(int id)
	{
		int size = familyList.size();
		for (int i = 0; i < size; i++)
		{
			FamilyController family = (FamilyController)familyList.get(i);
			if(family.id == id)
				return family;
		}
		return null;
	}
	
	public FamilyController getFamilyByName(String name)
	{
		int size = familyList.size();
		for (int i = 0; i < size; i++)
		{
			FamilyController family = (FamilyController)familyList.get(i);
			if(family.name.equals(name))
				return family;
		}
		return null;
	}
	
	public boolean isFamilyHaved(String name)
	{
		return getFamilyByName(name) != null;
	}
	
	public void saveTo(ByteBuffer buffer)
	{
		getJobObserver().addJob(GameServer.JOB_GAME2,new PressureJob(1));
		
		DataFactory.getInstance().saveTmpIndex();
		
		int size = answerRewards.size();
		buffer.writeByte(sendRewardDate);
		byte today = (byte)Utils.getCurrentDate(System.currentTimeMillis());
		buffer.writeByte(date==0?today:date);
		
		int partySize = FamilyPartyController.getInstance().getTopFamilys().size();
		buffer.writeByte(partySize);
		
		for (int i = 0; i < partySize; i++)
		{
			PartyReward rd = (PartyReward)FamilyPartyController.getInstance().getTopFamilys().get(i);
			rd.saveTo(buffer);
		}
		
		HashMap topTeamPlayers= FamilyPartyController.getInstance().getTopTeamPlayers();
		Object []keyObj= topTeamPlayers.keySet().toArray();
		buffer.writeInt(keyObj.length);
		for (int i = 0; i < keyObj.length; i++)
		{
			String key = (String)keyObj[i];
			
			buffer.writeUTF(key);
			
			ArrayList topList = (ArrayList)topTeamPlayers.get(key);
			int topSize = topList.size();
			buffer.writeInt(topSize);
			for (int j = 0; j <topSize; j++)
			{
				PartyReward rd = (PartyReward)topList.get(j);
				rd.saveTo(buffer);
			}
			
		}
		
		List list = (List) DataFactory.getInstance().getAttachment(DataFactory.COPY_LIST);
		int leng = list.size();
		buffer.writeByte(leng);
		for (int i = 0; i < leng; i++) 
		{
			CopyController copy = (CopyController) list.get(i);
			buffer.writeInt(copy.id);
			copy.saveTo(buffer);
		}
		
		int copyTodaySize = copyTodayTop.size();
		buffer.writeByte(copyTodaySize);
		for (int i = 0; i < copyTodaySize; i++)
		{
			PartyReward rp = (PartyReward) copyTodayTop.get(i);
			rp.savePartTo(buffer);
		}
		
		int copyYesSize = copyYesTop.size();
		buffer.writeByte(copyYesSize);
		for (int i = 0; i < copyYesSize; i++)
		{
			PartyReward rp = (PartyReward) copyYesTop.get(i);
			rp.savePartTo(buffer);
		}
		
		bestPlayer.savePartTo(buffer);
		
		int goldTopCount = GoldPartyController.getInstance().getTopCount();
		buffer.writeInt(goldTopCount);
		for (int i = 0; i < goldTopCount; i++) 
		{
			Reward reward = (Reward) GoldPartyController.getInstance().getTopPlayers().get(i);
			reward.saveReward(buffer);
		}
		
		int preGoldTopCount = GoldPartyController.getInstance().getPreTopCount();
		buffer.writeByte(preGoldTopCount);
		for (int i = 0; i < preGoldTopCount; i++) 
		{
			Reward reward = (Reward) GoldPartyController.getInstance().getPreTopPlayers().get(i);
			reward.saveReward(buffer);
		}
		
		buffer.writeByte(0);//备用 
		
		
		buffer.writeByte(size);
		for (int i = 0; i < size; i++)
		{
			Reward rp = (Reward) answerRewards.get(i);
			rp.saveTo(buffer);
		}

		ShopCenter.getInstance().saveTo(buffer);

		
		
		if(RewardSender.sendPlayerMap.keySet().size() != 0)
		{
			StringBuffer sb = new StringBuffer();
			Object objs[] = RewardSender.sendPlayerMap.keySet().toArray();
			for (int i = 0; i < objs.length; i++)
			{
				sb.append(objs[i]);
				
				if(i != objs.length)
					sb.append(Utils.LINE_SEPARATOR);
			}
			
			Utils.writeFile(GameServer.getAbsolutePath()+"RewardSenderList.txt", sb.toString().getBytes());
			
			
			Object obj = DataFactory.getInstance().getAttachment(DataFactory.REWARDSENDER_LIST);
			if(obj instanceof ArrayList)
			{
				ArrayList list1 = (ArrayList)obj;
			
				for (int i = 0; i < list1.size(); i++)
				{
					RewardSender rs = (RewardSender)list1.get(i);
					
					if(rs == null)
						continue;
					
					//System.out.println("id:"+rs.id+"\tname:"+rs.name+"\tcount:"+rs.count+"\tcanCount:"+rs.canCount);
					
					
				}
			}
			
			String str = RewardSender.reCreateRewardSenderFile();
			Utils.writeFile(LocalDataLoader.rewardSenderStr, str.getBytes());
		}

	}

	
	public void loadFrom(ByteBuffer buffer)
	{		
		sendRewardDate = (byte) buffer.readByte();
		closeDate = (byte) buffer.readByte();

		int fptSize = buffer.readByte();
		//待排名保存一次后则可以删除这个判断
		if(fptSize != 0)
		{
			for (int i = 0; i < fptSize; i++)
			{
				PartyReward rp = new PartyReward();
				rp.loadFrom(buffer);
				FamilyPartyController.getInstance().getTopFamilys().add(rp);
			}

			int keySize = buffer.readInt();

			for (int i = 0; i < keySize; i++)
			{
				String key = buffer.readUTF();

				ArrayList topList = new ArrayList();
				int topSize = buffer.readInt();
				for (int j = 0; j < topSize; j++)
				{
					PartyReward rp = new PartyReward();
					rp.loadFrom(buffer);
					topList.add(rp);
				}
				FamilyPartyController.getInstance().getTopTeamPlayers().put(key, topList);
			}
			
			int leng = buffer.readByte();
//			if(leng > 0)
//			{
				for (int i = 0; i < leng; i++) 
				{
					int id = buffer.readInt();
					CopyController copy = DataFactory.getInstance().getCopyById(id);
					if(copy == null)
					{
						System.out.println("WorldManager load copy error:"+id);
					}
					copy.loadFrom(buffer);
				}
				
				int copyTodaySize = buffer.readByte();
//				if(copyTodaySize > 0)
//				{
					for (int i = 0; i < copyTodaySize; i++)
					{
						PartyReward rp = new PartyReward();
						rp.loadPartFrom(buffer);
						copyTodayTop.add(rp);
					}
					
					int copyYesSize = buffer.readByte();
					for (int i = 0; i < copyYesSize; i++)
					{
			 			PartyReward rp = new PartyReward();
						rp.loadPartFrom(buffer);
						copyYesTop.add(rp);
					}
					
					bestPlayer.loadPartFrom(buffer);
			
					int goldSize = buffer.readInt(); 
//					if(goldSize > 0)
//					{
						for (int i = 0; i < goldSize; i++)
						{
							Reward rp = new Reward();
							rp.loadReward(buffer);
							GoldPartyController.getInstance().addTopPlayer(rp);
						}
						
						int preGoldSize = buffer.readByte();
//						if(preGoldSize > 0)
//						{
				 			for (int i = 0; i < preGoldSize; i++)
							{
								Reward rp = new Reward();
								rp.loadReward(buffer);
								GoldPartyController.getInstance().addPreTopPlayer(rp);
							}
							
							buffer.readByte();
//						}//preGoldSize
//					}//goldSize
//				}//copyTodaySize
//			}//leng  
		}//fptSize
		
		
		int size = buffer.readByte();
		for (int i = 0; i < size; i++)
		{
			Reward rp = new Reward();
			rp.loadFrom(buffer);
			answerRewards.add(rp);
		}
	
	
		List flTmp = getDatabaseAccessor().getFamilys(); 
		
//		System.out.println("load family oldSize : "+size);
//		System.out.println("load family newSize : "+flTmp.size());
		
		
		
		if(flTmp.size() != 0)
			familyList = flTmp;
		
		try
		{
			if(buffer.available() <= 0)
			{
				System.out.println("ShopCenter is null");
				return;
			}
			ShopCenter.getInstance().loadFrom(buffer);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			MainFrame.println("shopCenter goods is error!");//寄卖中心东西掉了......
		}
		
		
		
		/**
		 * 读取已经发过的人
		 */
		if(new File(GameServer.getAbsolutePath()+"RewardSenderList.txt").exists())
		{
			String str = Utils.readFile2(GameServer.getAbsolutePath()+"RewardSenderList.txt");
			String  accountStr []= Utils.split(str.trim(), Utils.LINE_SEPARATOR);
	
			for (int i = 0; i < accountStr.length; i++)
			{
				RewardSender.sendPlayerMap.put(accountStr[i].trim(),1);
			}
			
		}
		
		byte today = (byte)Utils.getCurrentDate(System.currentTimeMillis());
		if(today != closeDate)
		{
			sendRankReward();
		}
	}
	
	public NetConnection getConnByIpAndPort(String ipAPort)
	{
		for (int i = 0; i < connections.size(); i++)
		{
			NetConnection net =(NetConnection)connections.get(i);
			
			if(net == null)
				continue;
			
			if(net.getIP().equals(ipAPort))
			{
				return net;
			}
		}
		return null;
	}
	
	
	private void checkSameIp(NetConnection conn)
	{
		ArrayList list = getSameConnectionsByIp(conn.getIP().split(":")[0]);
		
		if(list != null)
		{
			conn.setInfo(list);
		}
	}
	
	
	public ArrayList getSameConnectionsByIp(String ip)
	{
		ArrayList list = new ArrayList();
		
		for (int i = 0; i < connections.size(); i++)
		{
			NetConnection net =(NetConnection)connections.get(i);
			
			if(net == null)
				continue;
			
			if(net.getIP().split(":")[0].equals(ip))
			{
				list.add(net);
			}
		}
		if(list.size() <= 0)
			return null;
		return list;
	}

	public static void init()
	{
		long time = System.currentTimeMillis();
		WorldManager.date = (byte)Utils.getCurrentDate(time);
		nextDayTime = 0;
		for (long i = 0; i < Long.MAX_VALUE; i+=60000)
		{
			int nextDate = Utils.getCurrentDate(time+i);
			
			if(nextDate != WorldManager.date)
			{
				nextDayTime = time = i;
				break;
			}
		}
		
		WorldManager.weekDate = (byte) WorldManager.getCurrentWeekDay();

		WorldManager.nextDayTopTime = nextDayTime-(1000*60*60*18);

	}
	
	public int getConnectionSize()
	{
		return connections.size();
	}
	
	public List getConnections()
	{
		return connections;
	}
	
	/**
	 * 增加删除封杀的IP
	 * @param ip
	 * @param state 1添加 2删除
	 */
	public void changeProIpState(String ip,int state)
	{
		String str[] = Utils.split(ip,":");
		ip = str[0];
		
		if(state == 1)
		{
			if(!pIps.contains(ip))
			{
				pIps.add(ip);
			}
		}
		else if(state == 2)
		{
			pIps.remove(ip);
		}
	}
	
	/**
	 * 查看是否在封IP的盒子里面
	 * @param conn
	 * @return
	 */
	private boolean isInProIpBox(NetConnection conn)
	{
		if(pIps.size() == 0)
			return false;
		
		String str[] = Utils.split(conn.getIP(), ":");
		
		for (int i = 0; i < pIps.size(); i++)
		{
			String ip = (String)pIps.get(i);
			
			if(str[0].equals(ip))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 取得封锁IP的盒子
	 * @return
	 */
	public List getPIps()
	{
		return pIps;
	}

	private static int getCurrentWeekDay()
	{
	   Calendar cal = Calendar.getInstance();
	   cal.setTime(new Date());
	   return cal.get(Calendar.DAY_OF_WEEK) - 1;
	}
	
	public void sendEveryonePost(String chatMsg)
	{
		ByteBuffer buffer = new ByteBuffer(24);
		buffer.writeByte(8);
		buffer.writeInt(1);
		buffer.writeUTF(DC.getString(DC.BASE_3));
		buffer.writeUTF(chatMsg);
		GameServer.getInstance().getWorldManager().dispatchMsg(SMsg.S_CHAT_COMMAND, buffer);
	}
	
	
	/**
	 * 游戏中所有副本总排行
	 * @param answerRewards
	 * @param rPlayer
	 */
	public void addCopyAllRank(PlayerController target,RoomController room)
	{
		OtherExtInfo oei = (OtherExtInfo) target.getPlayer().getExtPlayerInfo("otherExtInfo");
		long point = oei.copyPoints;//取得玩家的总的副本积分
		boolean isInRank = false;
		//如果玩家已经在前二十，则更新其积分
		for(int has = 0;has < copyTodayTop.size();has++)
		{
			PartyReward pr = (PartyReward) copyTodayTop.get(has);
			if(pr.id == target.getID())
			{
				pr.honorPoint = point;
				pr.logTime = currentTime;
				isInRank = true;
				break;
			}
		}
		//如果玩家没有在排行榜中，则加入到排行榜
		if(!isInRank)
		{
			PartyReward pr2 = new PartyReward();
			pr2.id = target.getID();
			pr2.name = target.getName();
			pr2.honorPoint = point;
			pr2.setPlayer(target.getPlayer());
			pr2.logTime = currentTime;
			copyTodayTop.add(pr2);
		}
		
		
		if (copyTodayTop.size() > 1) 
		{
		//对排行榜中的20名玩家进行排序
			for (int i = 0; i < copyTodayTop.size()-1; i++) 
			{
				for (int j = i + 1; j < copyTodayTop.size(); j++)
				{
					PartyReward rp4 = (PartyReward) copyTodayTop.get(i);
					PartyReward rp5 = (PartyReward) copyTodayTop.get(j);
					if (rp4.honorPoint < rp5.honorPoint) 
					{
						PartyReward temp = rp4;
						copyTodayTop.set(i, rp5);
						copyTodayTop.set(j, temp);
					}
					else if(rp4.honorPoint == rp5.honorPoint && rp4.logTime > rp5.logTime)
					{
						PartyReward temp = rp4;
						copyTodayTop.set(i, rp5);
						copyTodayTop.set(j, temp);
					}
				}
			}
			//移除排行于第20名之后的玩家
			if (copyTodayTop.size() > 20) 
			{
				for (int i = 20; i < copyTodayTop.size(); i++)
				{
					copyTodayTop.remove(i);
				}
			}
		}
		
		for (int i = 0; i < copyTodayTop.size(); i++)
		{
			PartyReward rp = (PartyReward) copyTodayTop.get(i);
			rp.rank = i+1;
			if(i == 0 && rp.honorPoint > bestPlayer.honorPoint)
			{
				rp.copyTo(bestPlayer);
			}
		}
	}
	
	
	
	/**
	 * 添加到排名榜中并排序
	 * @param answerRewards
	 * @param rPlayer
	 */
	public void addAnswerReward(Player player,int pointCount)
	{
		if(answerRewards.size() == 0)
		{
			Reward rp1 = new Reward();
			rp1.id = player.id;
			rp1.accountName = player.accountName;
			rp1.name = player.name;
			rp1.level = player.level;
			rp1.point = pointCount;
			rp1.logTime = currentTime;
			rp1.rank = 1;
			rp1.setPlayer(player);
			answerRewards.add(rp1);
		}
		else
		{
			boolean isInRank = false;
			//如果玩家已经在前十，则更新其积分
			for(int has = 0;has < answerRewards.size();has++)
			{
				Reward rp2 = (Reward) answerRewards.get(has);
				if(rp2.id == player.id)
				{
					rp2.point = pointCount;
					rp2.level = player.level;
					rp2.logTime = currentTime;
					isInRank = true;
					break;
				}
			}
			//如果玩家没有在排行榜中，则加入到排行榜
			if(!isInRank)
			{
				Reward rp3 = new Reward();
				rp3.id = player.id;
				rp3.accountName = player.accountName;
				rp3.name = player.name;
				rp3.level = player.level;
				rp3.point = pointCount;
				rp3.setPlayer(player);
				rp3.logTime = currentTime;
				answerRewards.add(rp3);
			}
			if (answerRewards.size() > 1) 
			{
			//对排行榜中的10名玩家进行排序
				for (int i = 0; i < answerRewards.size()-1; i++) 
				{
					for (int j = i + 1; j < answerRewards.size(); j++)
					{
						Reward rp4 = (Reward) answerRewards.get(i);
						Reward rp5 = (Reward) answerRewards.get(j);
						if (rp4.point < rp5.point) 
						{
							Reward temp = rp4;
							answerRewards.set(i, rp5);
							answerRewards.set(j, temp);
						}
						else if(rp4.point == rp5.point && rp4.logTime > rp5.logTime)
						{
							Reward temp = rp4;
							answerRewards.set(i, rp5);
							answerRewards.set(j, temp);
						}
					}
				}
			
			}
			//移除排行于第十名之后的玩家
			if (answerRewards.size() > 10) 
			{
				for (int i = 10; i < answerRewards.size(); i++)
				{
					answerRewards.remove(i);
				}
			}
			
			for (int i = 0; i < answerRewards.size(); i++)
			{
				Reward rp = (Reward) answerRewards.get(i);
				rp.rank = i+1;
			}
		}
	}
	

	public void deleteAnswerRank(int id)
	{
		for (int i = 0; i < answerRewards.size(); i++) 
		{
			Reward reward = (Reward) answerRewards.get(i);
			if(reward.id == id)
			{
				answerRewards.remove(i);
				break;
			}
		}
	}
	
	
	public List getAnswerRewards()
	{
		return answerRewards;
	}
	
	public boolean isMoreThanReward(int point)
	{
		if(answerRewards.size() == 0)
			return true;
		Reward rp = (Reward) answerRewards.get(answerRewards.size()-1);
		return (point > rp.point);
	}
	
	public Reward getAnswerRewardById(int id)
	{
		for (int i = 0; i < answerRewards.size(); i++) 
		{
			Reward rp = (Reward) answerRewards.get(i);
			if(rp.id == id)
				return rp;
		}
		return null;
	}
	
	private Mail createAnswerMail(RankReward rr)
	{
		Mail mail = null;
		if(currentTime == 0)
			mail = new Mail(DC.getString(DC.PARTY_37),System.currentTimeMillis());
		else
			mail = new Mail(DC.getString(DC.PARTY_37));
		StringBuffer sb = null;
		if(rr.type == 1)
		{
			sb = new StringBuffer();
			mail.setTitle(DC.getString(DC.PARTY_43));
			sb.append(DC.getString(DC.PARTY_44));
			sb.append("[");
			sb.append(rr.rank);
			sb.append("]");
			sb.append(DC.getString(DC.PARTY_45));
			mail.setContent(sb.toString());//恭喜你在奇妙问答活动中获得第多少 名，特此奖励，以兹鼓励
		}
		else
		{
			mail.setTitle(DC.getString(DC.PARTY_46));
			sb = new StringBuffer();
			sb.append(DC.getString(DC.PARTY_47));
			sb.append("[");
			sb.append(rr.rank);
			sb.append("]");
			sb.append(DC.getString(DC.PARTY_48));
			mail.setContent(sb.toString());//恭喜你完成了第["+rr.rank+"]轮的奇妙问答活动,特此奖励，以兹鼓励
		}
		return mail;
	}
	
	/**
	 * 发送答题奖励
	 * @param target
	 * @param rr
	 */
	public void sendAnswerReward(PlayerController target,RankReward rr, boolean isOnline)
	{
		if(rr == null || target == null)
			return;
		Mail mail = createAnswerMail(rr);
		if(rr.money > 0)
			mail.setMoney(rr.money);
		if(rr.point > 0)
			mail.setPoint(rr.point);
		Goods[] goods1 = null,goods2 = null,goods3 = null,goods4 = null;
		if(rr.goods1[0] != 0)
		{
			goods1 = DataFactory.getInstance().makeGoods(rr.goods1[0], rr.goods1[1], rr.goods1[2]);
		}
		if(rr.goods2[0] != 0)
		{
			goods2 = DataFactory.getInstance().makeGoods(rr.goods2[0], rr.goods2[1], rr.goods2[2]);
		}
		if(rr.goods3[0] != 0)
		{
			goods3 = DataFactory.getInstance().makeGoods(rr.goods3[0], rr.goods3[1], rr.goods3[2]);
		}
		if(rr.goods4[0] != 0)
		{
			goods4 = DataFactory.getInstance().makeGoods(rr.goods4[0], rr.goods4[1], rr.goods4[2]);
		}
		if(goods1 != null && goods1[0] != null)
			mail.addAttach(goods1[0]);
		if(goods2 != null && goods2[0] != null)
			mail.addAttach(goods2[0]);
		if(mail.getAttachCount() == 0)
		{
			if(goods3 != null && goods3[0] != null)
				mail.addAttach(goods3[0]);
			if(goods4 != null && goods4[0] != null)
				mail.addAttach(goods4[0]);
			if(mail.getAttachCount() > 0)
			{
				if(isOnline)
					mail.send(target);
				else
					mail.sendOffLine(target.getPlayer());
			}
		}
		else if(mail.getAttachCount() == 1)
		{
			if(goods3 != null && goods3[0] != null)
				mail.addAttach(goods3[0]);
			if(mail.getAttachCount() == 1)
			{
				if(goods4 != null && goods4[0] != null)
					mail.addAttach(goods4[0]);
				if(isOnline)
					mail.send(target);
				else
					mail.sendOffLine(target.getPlayer());
			}
			else
			{
				if(isOnline)
					mail.send(target);
				else
					mail.sendOffLine(target.getPlayer());
				mail = null;
				
				if(goods4 != null && goods4[0] != null)
				{
					mail = createAnswerMail(rr);
					mail.addAttach(goods4[0]);
					if(isOnline)
						mail.send(target);
					else
						mail.sendOffLine(target.getPlayer());
				}
			}
			
		}
		else
		{
			if(isOnline)
				mail.send(target);
			else
				mail.sendOffLine(target.getPlayer());
			mail = null;
			
			if(goods3 != null && goods3[0] != null)
			{
				mail = createAnswerMail(rr);
				mail.addAttach(goods3[0]);
				if(goods4 != null && goods4[0] != null)
					mail.addAttach(goods4[0]);
				if(mail.getAttachCount() > 0)
				{
					if(isOnline)
						mail.send(target);
					else
						mail.sendOffLine(target.getPlayer());
				}
			}
		}
	}
	
	
	public static String getTypeTime(String type,long time)
	{
		SimpleDateFormat sdf = new SimpleDateFormat(type);
		return sdf.format(time);
	}
	
	
	public RoomController getRoomWolrd(int roomId)
	{
		AreaController areas[]= getAreaControllers();
		for (int i = 0; i < areas.length; i++)
		{
			if(areas[i] == null)
				continue;
			RoomController targetRoom = areas[i].getRoomById(roomId);
			if(targetRoom != null)
			{
				return targetRoom;
			}
		}
		return null;
	}
	
	
	/**
	 *  all copy rank
	 * @param target
	 */
	public void sendTopPlayer(PlayerController target)
	{
		ByteBuffer buffer = new ByteBuffer();
		/********************yestoday********************************/
		int size = copyYesTop==null?0:copyYesTop.size();
		buffer.writeByte(size);
		String str = "";
		for (int i = 0; i < size; i++)
		{
			PartyReward reward = (PartyReward) copyYesTop.get(i);
			buffer.writeInt(reward.rank);
			buffer.writeUTF(reward.name);
			Player player = reward.getPlayer();
			if(player == null)
				player = GameServer.getInstance().getDatabaseAccessor().getPlayer(reward.id);
			if(player == null)
			{
				buffer.writeUTF("");
				buffer.writeByte(0);
			}
			else
			{
				buffer.writeUTF(player.familyName);
				buffer.writeByte(player.upProfession);
			}
			buffer.writeUTF(reward.honorPoint+"");
			if(str.isEmpty())
				str = WorldManager.getTypeTime("yyyy-MM-dd", reward.logTime);
		}
		if(str.isEmpty())
			str = WorldManager.getTypeTime("yyyy-MM-dd", WorldManager.currentTime);
		buffer.writeUTF(str);
		
		/*******************today**************************/
		size = copyTodayTop==null?0:copyTodayTop.size();
		buffer.writeByte(size);
		str = "";
		boolean isInList = false;
		long lastPoint = 0;
		for (int i = 0; i < size; i++)
		{
			PartyReward reward = (PartyReward) copyTodayTop.get(i);
			buffer.writeInt(reward.rank);
			buffer.writeUTF(reward.name);
			Player player = reward.getPlayer();
			if(player == null)
				player = GameServer.getInstance().getDatabaseAccessor().getPlayer(reward.id);
			if(player == null)
			{
				buffer.writeUTF("");
				buffer.writeByte(0);
			}
			else
			{
				buffer.writeUTF(player.familyName);
				buffer.writeByte(player.upProfession);
			}
			buffer.writeUTF(reward.honorPoint+"");
			if(str.isEmpty())
				str = WorldManager.getTypeTime("yyyy-MM-dd", reward.logTime);
			
			if(reward.name.equals(target.getName()))
				isInList = true;
			if(i == size - 1)
				lastPoint = reward.honorPoint;
		}
		if(str.isEmpty())
			str = WorldManager.getTypeTime("yyyy-MM-dd", WorldManager.currentTime);
		buffer.writeUTF(str);
		
		/*******************the best**************************/
		buffer.writeUTF(bestPlayer.name);
		buffer.writeUTF(bestPlayer.honorPoint+"");
		buffer.writeUTF(WorldManager.getTypeTime("yyyy-MM-dd HH:mm:ss", bestPlayer.logTime));
		
		/*******************myself**************************/
		String selfPoints = target.getOtherExtInfo("copyPoint",true,target.getRoom());
		buffer.writeUTF(selfPoints);
		long selfPoint = Long.parseLong(selfPoints);
		if(!isInList && selfPoint > lastPoint)
		{
			MainFrame.println("WorldManager copyPlayer:"+target.getName()+":point:"+selfPoints+"--lastPoint:"+lastPoint);
		}
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_GET_COPY_PLAYER_RANK_COMMAND,buffer));
	}
	
	
	/**
	 * CopyJob里调用
	 */
	public void initCopy()
	{
		copyYesTop = new ArrayList(copyTodayTop.size());
		for (int i = 0; i < copyTodayTop.size(); i++) 
		{
			copyYesTop.add(copyTodayTop.get(i));
		}
		copyTodayTop = new ArrayList(20);
	}
	
	
	/**
	 * 23点55分以后,0点5分以前，不能答题，不能喂养宠物，宠物不能出行
	 * 22点到0点不能答题
	 * @param type 0:default 1:lianhua,duobaoqibing
	 * @return
	 */
	public static boolean isZeroMorning(int type)
	{
		int time = Integer.parseInt(getTypeTime("HH",currentTime));
	    int minute = Integer.parseInt(getTypeTime("mm", currentTime));
	    if(type == 0)//默认的
	    	return ((time == 23 && minute >= 55) || (time == 0 && minute < 10));
	    else//炼化,夺宝奇兵,在凌晨需要空出5分钟来保存当天的记录(主要是元宝消费记录)
	    	return ((time == 23 && minute >= 59) || (time == 0 && minute < 4));
	} 
	
	/**
	 * 把金币拆分成金银铜发送给客户端
	 * @param buffer
	 * @param point
	 */
	public static void sendPoint(ByteBuffer buffer,long point)
	{
		int jin = (int) (point / 10000);
		int yin = (int) ((point % 10000) / 100);
		int tong = (int) (point % 100);
		buffer.writeInt(jin);
		buffer.writeByte(yin);
		buffer.writeByte(tong);
	}
	
	public boolean isAcceptMsgByStory(PlayerController target,int msg)
	{
		if(target.isStory && (msg == SMsg.C_PLAYER_REQUEST_TEAM_COMMAND || msg == SMsg.C_ADD_TEAM_COMMAND
		  || msg == SMsg.C_PLAYER_REQUEST_PK_COMMAND || msg == SMsg.C_PLAYER_RESPONSE_PK_COMMAND
		  || msg == SMsg.C_PLAYER_APPLY_TEAM_COMMAND || msg == SMsg.C_PLAYER_AUTO_BATTLE_COMMAND
		  || msg == SMsg.C_LEADER_TRANSFER_COMMAND || msg == SMsg.C_TEAM_SET_INFO_COMMAND
		  || msg == SMsg.C_LEADER_T_MEMBER_COMMAND || msg == SMsg.C_LEADER_DISBAND_TEAM_COAMMAND
		  || msg == SMsg.C_MEMBER_LEAVE_TEAM_COMMAND || msg == SMsg.C_TEAM_APPLY_LIST_COMMAND
		  || msg == SMsg.C_TEAM_AGREE_APPLY_COMMAND || msg == SMsg.C_TEAM_CLEAR_APPLY_COMMAND))
		{
			return false;
		}
		else
			return true;
	}
	
	public boolean isAcceptMsg(PlayerController target,int message)
	{
		if(!target.isAuto)
			return true; 
		if(message == SMsg.C_PLAYER_INFO_COMMAND || message == SMsg.C_GET_PLAYER_EQUIPSET_COMMAND
		  || message == SMsg.C_CHAT_COMMAND || message == SMsg.C_GET_SKILL_COMMAND
		  || message == SMsg.C_PLAYER_PET_INFO_COMMAND || message == SMsg.C_PLAYER_UPDATE_COMMAND
		  || message == SMsg.C_PLAYER_CHAT_CARTOON_COMMAND || message == SMsg.C_PLAYER_NEW_COMMAND
		  || message == SMsg.C_PLAYER_GET_TOP_COMMAND || message == SMsg.C_PLAYER_AUTO_BATTLE_COMMAND
		  || message == SMsg.C_GET_PLAYER_BAG_COMMAND || message == SMsg.C_GET_PLAYER_SHORTCUT_BAR_COMMAND
		  || message == SMsg.C_GET_GOODS_INFO_COMMAND || message == SMsg.C_OPEN_STORAGE_COMMAND
		  || message == SMsg.C_BATTLE_IO_COMMAND || message == SMsg.C_BATTLE_ACTION_COMMAND
		  || message == SMsg.C_BATTLE_PLAYER_BATTLEEND || message == SMsg.C_BATTLE_PLAYER_BATTLERESULT
		  || message == SMsg.C_BATTLE_DIE || message == SMsg.C_BATTLE_PLAYER_UPDATE_COMMAND
		  || message == SMsg.C_BATTLE_PLAYER_RESET_COMMAND || message == SMsg.C_GET_PARTY_PLAYER_REWARD_COMMAND
		  || message == SMsg.C_GET_PARTY_FAMILY_REWARD_COMMAND || message == SMsg.C_GET_PLAYER_FAMILY_HONOUR_COMMAND
		  || message == SMsg.C_GET_COPY_PLAYER_RANK_COMMAND || message == SMsg.C_ROOM_PLAYER_UPDATE_PLAYER
		  || message == SMsg.C_PLAYER_CAMP_SET_COMMAND || message == SMsg.C_PLAYER_UPDATEEVENTPOINT_COMMAND
		  || message == SMsg.C_PLAYER_UPDATEACTIVEPOINT_COMMAND || message == SMsg.C_CLOSE_STORAGE_COMMAND
		  || message == SMsg.C_ADD_GOODS_COMMAND || message == SMsg.C_PLAYER_PETINFO_OPTION_COMMAND
		  || message == SMsg.C_USE_GOODS_COMMAND || message == SMsg.C_AUTO_SKILLTOME_COMMAND
		  || message == SMsg.C_PLAYER_AUTO_BATTLE_INFO_COMMAND)
		{
			return true;
		}
		else
			return false;
	}
	
	
	/**
	 * 取得在登陆阶段的玩家
	 * @return
	 */
	public Player getPlayerByLoginState(String accountName)
	{
		List listConection = getConnections();
		int listConectionSize = listConection.size();
		 
		for (int j = 0; j < listConectionSize; j++)
		{
			NetConnection conn = (NetConnection)listConection.get(j);
			
			if(conn.getInfo() instanceof Player)
			{
				Player player = (Player)conn.getInfo();
				if(player.accountName.equals(accountName))
					return player;
			}
		}
		
		return null;
	}
}
