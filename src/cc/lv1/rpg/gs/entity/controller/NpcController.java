package cc.lv1.rpg.gs.entity.controller;

import vin.rabbit.net.AppMessage;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.i.Map;
import vin.rabbit.util.collection.impl.ArrayList;
import vin.rabbit.util.xml.XMLNode;
import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.data.DC;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.data.MarkChar;
import cc.lv1.rpg.gs.data.TaskManager;
import cc.lv1.rpg.gs.data.UseChar;
import cc.lv1.rpg.gs.entity.AamInvitation;
import cc.lv1.rpg.gs.entity.ConfirmJob;
import cc.lv1.rpg.gs.entity.MarryInvitation;
import cc.lv1.rpg.gs.entity.NpcDialog;
import cc.lv1.rpg.gs.entity.NpcDialog.DialogStep;
import cc.lv1.rpg.gs.entity.container.PlayerContainer;
import cc.lv1.rpg.gs.entity.ext.AnswerParty;
import cc.lv1.rpg.gs.entity.ext.Bag;
import cc.lv1.rpg.gs.entity.ext.OtherExtInfo;
import cc.lv1.rpg.gs.entity.ext.Storage;
import cc.lv1.rpg.gs.entity.ext.TaskInfo;
import cc.lv1.rpg.gs.entity.impl.BaseFlayer;
import cc.lv1.rpg.gs.entity.impl.GoldParty;
import cc.lv1.rpg.gs.entity.impl.Goods;
import cc.lv1.rpg.gs.entity.impl.Mail;
import cc.lv1.rpg.gs.entity.impl.NPC;
import cc.lv1.rpg.gs.entity.impl.Player;
import cc.lv1.rpg.gs.entity.impl.Reward;
import cc.lv1.rpg.gs.entity.impl.Shop;
import cc.lv1.rpg.gs.entity.impl.Task;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsMarry;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsProp;
import cc.lv1.rpg.gs.net.SMsg;
import cc.lv1.rpg.gs.net.impl.SaveJob;
import cc.lv1.rpg.gs.other.ErrorCode;
import cc.lv1.rpg.gs.util.FontConver;

/**
 * npc 控制器
 * @author dxw
 *
 */
public class NpcController extends SpriteController
{
	private WorldManager worldManager;
	
	private NPC npc;
	
	private NpcDialog[] npcDialogs;
	
	private Shop shop;
	
	private Shop[] shops = new Shop[0];

	private Task []tasks = new Task[0];
	
	private boolean [] isTaskAccept = new boolean[0];
	
	private boolean [] isTaskFinish = new boolean[0];
	
	public boolean isAnswerNpc = false;
	
	public void parseDialog(XMLNode xmlDialog)
	{
		if(xmlDialog != null)
		{
			List dialog = xmlDialog.getSubNodes();
			npcDialogs = new NpcDialog[dialog.size()];
			for (int i = 0; i < dialog.size(); i++) {
				XMLNode root = (XMLNode) dialog.get(i);
				npcDialogs[i] = new NpcDialog(root);
				npcDialogs[i].setParent(this);
			}
		}
	}
	
	public NpcDialog getNpcDialog(String dialog)
	{
		for (int i = 0; i < npcDialogs.length; i++) {
			NpcDialog nd = npcDialogs[i];
			if(nd.getDialogIndex().equals(dialog))
			{
				return nd;
			}
		}
		return null;
	}


	private static final int TIMESKYLEVEL = 1500;
	
	/**
	 * 请求NPC对话
	 * @param target
	 * @param msg
	 */
	public void requestNpcDialog(PlayerController target,AppMessage msg)
	{
		String cmd = msg.getBuffer().readUTF();	

		if(cmd.isEmpty())
		{
			clickNpc(target);
		}
		else
		{
			String[] strs= Utils.split(cmd, ":");

			if(strs[0].equals("link"))
			{
				if(strs.length != 3)
					return;
				
				ByteBuffer buffer = new ByteBuffer(20);
				NpcDialog nDialog = getNpcDialog(strs[1]);
				if(nDialog == null)
				{
					System.out.println("NpcController "+getID()+"  "+getName());
					System.out.println("link : "+strs[1]);
					return;
				}
				DialogStep dStep = nDialog.getDialogStep(strs[2]);
				if(dStep == null)
				{
					System.out.println("NpcController "+getID()+"  "+getName());
					System.out.println("link : "+strs[1]+" : "+strs[2]);
					return;
				}
				buffer.writeByte(1);//默认的
				dStep.writeTo(buffer);
				target.getNetConnection().sendMessage(new SMsg(SMsg.S_CLICK_NPC_COMMAND,buffer));
			}
			else if(strs[0].equals("openShop") || strs[0].equals("honourExchange"))
			{
				int shopId = 0;
				try 
				{
					shopId = Integer.parseInt(strs[1]);
				} catch (Exception e) 
				{
					System.out.println("shopId error:"+strs[1]);
					return;
				}
				Shop shop = getShopById(shopId);
				if(shop == null)
					return;
				if(!target.getRoom().isNpcInRoom(shopId) && shop.isPutongShop())
					return;
				ByteBuffer buffer = new ByteBuffer(100);
				shop.writeTo(buffer);
				target.getNetConnection().sendMessage(new SMsg(SMsg.S_OPEN_SHOP_COMMAND,buffer));
			}
			else if(strs[0].equals("openPrivateStorage"))//打开玩家私人仓库
			{
//				ByteBuffer buffer = msg.getBuffer();
				List storages = target.getPlayer().getExtPlayerInfos("storage");
				for (int i = 0; i < storages.size(); i++) 
				{
					Storage storage = (Storage) storages.get(i);
					if(storage.storageType == 0)
					{
						storage.openPrivateStorage(target);
						break;
					}
				}
			}
			else if(strs[0].equals("familyCreate"))//创建家族
			{
				
				if(FamilyPartyController.getInstance().isStarted())
				{
					target.sendAlert(ErrorCode.ALERT_IS_FAMILY_START);
					return;
				}
				
				familyCreate(target,strs[1]); 
			}
			else if(strs[0].equals("openCenterShop")) //打开寄卖中心ui
			{
			}
			else if(strs[0].equals("flyer"))
			{
				if(strs[1].equals("timeSkyLog"))
				{
					int roomId = getTimeSkyCopyId(target);
					ByteBuffer buffer = new ByteBuffer();
					buffer.writeByte(2);
					buffer.writeInt(npc.id);
					RoomController room = (RoomController) DataFactory.getInstance().getGameObject(roomId);
					if(room == null)
					{	
						buffer.writeUTF("");
						buffer.writeUTF("");
					}
					else
					{
						buffer.writeUTF(room.getParent().name);
						buffer.writeUTF(room.name);
					}
					target.getNetConnection().sendMessage(new SMsg(SMsg.S_CLICK_NPC_COMMAND,buffer));
				}
				else if(strs[1].equals("timeSkyCopy"))
				{
					int roomId = getTimeSkyCopyId(target);
					if(roomId == 0)
					{
						//提示玩家没有记录
						target.sendAlert(ErrorCode.ALERT_PLAYER_NO_TIMESKY_NOTICE);
						return;
					}
					if(target.getTeam() != null)
					{
						target.sendAlert(ErrorCode.ALERT_PLAYER_TEAM_NOTNULL);
						return;
					}
					RoomController room = GameServer.getInstance().getWorldManager().getRoomWolrd(roomId);
					if(room == null)
					{
						System.out.println("NpcController copy move error:"+roomId);
						return;
					}
					CopyController copy = DataFactory.getInstance().getCopyByArea(room.getParent().id);
					if(copy == null)
						return;
					if(target.getPlayer().timeCopyActivePoint <= 0 && !copy.isHideRoom(roomId))
					{
						target.sendAlert(ErrorCode.ALERT_PLAYER_TIME_ACTIVEPOINT_ERROR);//时空之旅行动值没有了
						return;
					}
					if(target.getPlayer().level < TIMESKYLEVEL)
					{
						target.sendAlert(ErrorCode.ALERT_PLAYER_LEVEL_ERROR_TIMESKY);
						return;
					}
					AreaController area = worldManager.getAreaById(room.getParent().id);
					Map flyMap =  (Map)DataFactory.getInstance().getAttachment(DataFactory.ATTACH_NPC_FLYER);
					if(area != null && area.getRooms() != null && area.getRooms()[0] != null)
					{
						BaseFlayer bf = (BaseFlayer)flyMap.get(area.getRooms()[0].id);
						if(bf != null)
						{
							if(target.getPlayer().level >= bf.overLevel && bf.overLevel > 0)
							{
								target.sendAlert(ErrorCode.ALERT_PLAYER_OVER_LEVEL_ERROR);
								return;
							}
						}
					}
					target.moveToRoom(roomId);
				}
				else if(strs.length == 2)
				{
					flying(target, strs[1]);
				}
			}
			else if(strs[0].equals("firstGoldParty"))
			{
				if(!GoldPartyController.getInstance().isStarted())
				{
					target.sendAlert(ErrorCode.ALERT_PARTY_NO_START);
					return;
				}
				if(GoldPartyController.getInstance().getBattleCount(target) >= GoldPartyController.BATTLECOUNT)
				{
					target.sendAlert(ErrorCode.ALERT_BATTLE_COUNT_IS_MAX);
					return;
				}
				int roomId = GoldPartyController.getInstance().getRoomId(target);
				RoomController room = worldManager.getRoomWolrd(roomId);
				if(roomId == 0 || (room != null && room.isGoldPartyRoom))
				{
					GoldParty gp = DataFactory.getInstance().getGoldPartyByLevel(1);
					if(gp == null)
					{
						return;
					}
					roomId = gp.getRoomId();
				}
				target.moveToRoom(roomId);
				GoldPartyController.getInstance().setRoomId(target, roomId);
			}
			else if(strs[0].equals("taskList"))
			{
			}
			else if(strs[0].equals("answer"))//问答活动
			{
				if(!isAnswerNpc)
				{
					System.out.println("no answer npc："+getID());
					return;
				}
				
				AnswerParty ap = (AnswerParty) target.getPlayer().getExtPlayerInfo("answerParty");
				int size = ap.getQuestionSize();
				boolean isError = false;
				if(size == 0)
				{
					if(ap.getAnswerCount() % AnswerParty.ROUND != 0)
					{
						ap.clearAll();
						isError = true;
					}
					else if(ap.roundNumber != 0)
					{
						if(ap.getAnswerCount() / AnswerParty.ROUND != ap.roundNumber)
						{
							ap.clearAll();
							isError = true;
						}
					}
				}
				else
				{
					if(size != AnswerParty.ROUND)
					{
						ap.clearAll();
						isError = true;
					}
				}
				
				if(isError)
				{
					worldManager.deleteAnswerRank(target.getID());
					target.sendAlert(ErrorCode.ALERT_ANSWER_UPDATE);
					return;
				}
				
				
				Reward reward = worldManager.getAnswerRewardById(target.getID());
				if(ap.pointCount == 0)
				{
					if(reward != null)
						worldManager.deleteAnswerRank(target.getID());
				}
				
				sendAnswerQuestionState(target);
			}
			else if("facebookIndexChange".equals(strs[0]))
			{
				if(!DataFactory.fontVer.equals(DataFactory.COMPLEX))
					return;
				if(strs.length != 2)
				{
					return;
				}
				Goods goods = DataFactory.getInstance().getTmpIndexGoods(strs[1]);
				if(goods == null)
				{
					target.sendAlert(ErrorCode.ALERT_FACEBOOK_INDEX_ERROR);
					return;
				}
				Mail mail = new Mail(DC.getString(DC.BASE_1));
				mail.setTitle(DC.getString(DC.BASE_2));
				mail.setContent(DC.getString(DC.BASE_2));
				mail.addAttach(goods);
				mail.send(target);
				DataFactory.getInstance().removeTmpIndex(strs[1]);
			}
			else
			{
				taskProcess(target,strs);
			}
		}	
	}
	
	
	/**
	 * 参与家族活动
	 * @param target
	 */
	private boolean isFamilyParty(PlayerController target)
	{
		if(target.getFamily() ==  null)
		{
			target.sendAlert(ErrorCode.ALERT_PLAYER_NO_FAMILY);
			return false;
		}
		
		if(target.getTeam() != null)
		{
			target.sendAlert(ErrorCode.ALERT_MUST_EXIT_TEAM);
			return false;
		}
		
		return true;
	}
	

	private void sendAnswerQuestionState(PlayerController target)
	{
		AnswerParty ap = (AnswerParty) target.getPlayer().getExtPlayerInfo("answerParty");
		ap.sendAnswerQuestionState(target);
	}
	
	
	private void flying(PlayerController target, String str)
	{
		if(target.getTeam() != null)
		{
			if(!target.getTeam().isLeader(target))
				return;
		}
		
		if(target.checkPlayerState())
			return;
		if(target.checkTeamState())
			return;
		
		if(str.isEmpty() || str.trim().length() == 0)
			return;
		
		int id = 0;
		
		try 
		{
			id = Integer.parseInt(str);
		} 
		catch (NumberFormatException e) 
		{
			return;
		}
		
		Map flyMap =  (Map)DataFactory.getInstance().getAttachment(DataFactory.ATTACH_NPC_FLYER);

		BaseFlayer bf = (BaseFlayer)flyMap.get(id);

		if(bf == null)
			return;

		if(bf.type == 5)
		{
			FamilyPartyController fpc = FamilyPartyController.getInstance();
			if(!fpc.isReady())
			{
				target.sendAlert(ErrorCode.ALERT_PARTY_CLOSING);
				return;
			}
			int roomId = 0;
			if(target.getPlayer().camp == 1)
				roomId = DataFactory.KAITUOROOM;
			else if(target.getPlayer().camp == 2)
				roomId = DataFactory.XIESHENROOM;
			else
			{
				if(fpc.isReady())
				{
					if(target.getFamily() == null)
					{
						target.sendAlert(ErrorCode.ALERT_PLAYER_NO_FAMILY);
						return;
					}
					if(target.getFamily().camp == 1)
					{
						roomId = DataFactory.KAITUOROOM;
					}
					else if(target.getFamily().camp == 2)
					{
						roomId = DataFactory.XIESHENROOM;
					}
					else 
						return;
				}
				else 
					return;
			}
			if(fpc.isReady())
			{
				if(!isFamilyParty(target))
					return;
				target.moveToRoom(roomId);
				FamilyPartyController.getInstance().addPlayer(target);
			}

		}
		else if(bf.type == 11 || bf.type == 20 || bf.type == 21 || bf.type == 22 || bf.type == 23)//副本传送
		{
			if(WorldManager.isZeroMorning(0))
			{
				target.sendAlert(ErrorCode.ALERT_SYSTEM_IS_UPDATE);
				return;
			}
			if(target.getTeam() != null)
			{
				target.sendAlert(ErrorCode.ALERT_PLAYER_TEAM_NOTNULL);
				return;
			}
			OtherExtInfo oei = (OtherExtInfo) target.getPlayer().getExtPlayerInfo("otherExtInfo");
			RoomController room = null;
			if(bf.type == 21 || bf.type == 22 || bf.type == 23)
			{
				if(target.getPlayer().level < 1000)
				{
					target.sendAlert(ErrorCode.ALERT_PLAYER_LEVEL_LACK);
					return;
				}
				int roomId = getHonorNvshenCopyId(target,bf.type);
				if(roomId == 0)
				{
					CopyController copy = null;
					if(bf.type == 21)
					{
						if(target.getPlayer().copyStep1 == 0)
							copy = DataFactory.getInstance().getCopyByLevel(3,target.getPlayer().level);
						else
							copy = DataFactory.getInstance().getCopyByStep(3,target.getPlayer().copyStep1);
					}
					else if(bf.type == 22)
					{
						if(target.getPlayer().copyStep2 == 0)
							copy = DataFactory.getInstance().getCopyByLevel(4,target.getPlayer().level);
						else
							copy = DataFactory.getInstance().getCopyByStep(4,target.getPlayer().copyStep2);
					}
					else if(bf.type == 23)
					{
						if(target.getPlayer().copyStep3 == 0)
							copy = DataFactory.getInstance().getCopyByLevel(5,target.getPlayer().level);
						else
							copy = DataFactory.getInstance().getCopyByStep(5,target.getPlayer().copyStep3);
					}
					if(copy == null)
						return;
					room = GameServer.getInstance().getWorldManager().getRoomWolrd(copy.startRoomId);
					if(room == null)
						return;
					if(!room.isCopyPartyRoom)
						return;
					int num = DataFactory.getInstance().getCopyNum(room.getParent().id);
					if(num == -1)
						return;
					if(oei.isAssOver[num])
					{
						target.sendAlert(ErrorCode.ALERT_PLAYER_FINISH_COPY);//你今天已经完成副本了
						return;
					}
					if(oei.assRoomId[num] != 0)
					{
						room = GameServer.getInstance().getWorldManager().getRoomWolrd(oei.assRoomId[num]);
						if(room == null)
						{
							oei.assRoomId[num] = 0;
							oei.isAssOver[num] = true;
							return;
						}
					}
					target.moveToRoom(oei.assRoomId[num]==0?copy.startRoomId:oei.assRoomId[num]);
					if(bf.type == 21)
						target.getPlayer().copyStep1 = (byte) copy.step;
					else if(bf.type == 22)
						target.getPlayer().copyStep2 = (byte) copy.step;
					else if(bf.type == 23)
						target.getPlayer().copyStep3 = (byte) copy.step;
				}
				else
				{
					if(target.getTeam() != null)
					{
						target.sendAlert(ErrorCode.ALERT_PLAYER_TEAM_NOTNULL);
						return;
					}
					room = GameServer.getInstance().getWorldManager().getRoomWolrd(roomId);
					if(room == null)
					{
						System.out.println("NpcController copy move error:"+roomId);
						return;
					}
					CopyController copy = DataFactory.getInstance().getCopyByArea(room.getParent().id);
					if(copy == null)
						return;
					AreaController area = worldManager.getAreaById(room.getParent().id);
					flyMap =  (Map)DataFactory.getInstance().getAttachment(DataFactory.ATTACH_NPC_FLYER);
					if(area != null && area.getRooms() != null && area.getRooms()[0] != null)
					{
						bf = (BaseFlayer)flyMap.get(area.getRooms()[0].id);
						if(bf != null)
						{
							if(target.getPlayer().level >= bf.overLevel && bf.overLevel > 0)
							{
								target.sendAlert(ErrorCode.ALERT_PLAYER_OVER_LEVEL_ERROR);
								return;
							}
						}
					}
					target.moveToRoom(roomId);
				}
			}
			else
			{
				room = GameServer.getInstance().getWorldManager().getRoomWolrd(bf.roomId);
				if(room == null)
				{
					System.out.println("NpcController copy move error:"+bf.roomId);
					return;
				}
				if(!room.isCopyPartyRoom)
				{
					System.out.println("NpcController room is not copyRoom:"+bf.roomId);
					return;
				}
				
				int num = DataFactory.getInstance().getCopyNum(room.getParent().id);
				if(num == -1)
					return;
				CopyController copy = DataFactory.getInstance().getCopyByArea(room.getParent().id);
				if(copy == null)
					return;
				if(copy.type == 1)
				{
					if(oei.isAssOver[num])
					{
						target.sendAlert(ErrorCode.ALERT_PLAYER_FINISH_COPY);//你今天已经完成副本了
						return;
					}
					if(oei.assRoomId[num] != 0)
					{
						room = GameServer.getInstance().getWorldManager().getRoomWolrd(oei.assRoomId[num]);
						if(room == null)
						{
							oei.assRoomId[num] = 0;
							oei.isAssOver[num] = true;
							target.sendError(DC.getString(DC.BATTLE_6));
							return;
						}
					}
					target.moveToRoom(oei.assRoomId[num]==0?bf.roomId:oei.assRoomId[num]);
				}
			}
		}
		else if(bf.type == 16 || bf.type == 17)
		{
			RoomController room = GameServer.getInstance().getWorldManager().getRoomWolrd(bf.roomId);
			if(room == null)
			{
				System.out.println("NpcController copy move error:"+bf.roomId);
				return;
			}
			CopyController copy = DataFactory.getInstance().getCopyByArea(room.getParent().id);
			if(copy == null)
				return;
			if(copy.type == 2)
			{
				if(target.getPlayer().timeCopyActivePoint <= 0)
				{
					//时空之旅行动值没有了
					target.sendAlert(ErrorCode.ALERT_PLAYER_TIME_ACTIVEPOINT_ERROR);
					return;
				}
				if(target.getPlayer().level < TIMESKYLEVEL)
				{
					target.sendAlert(ErrorCode.ALERT_PLAYER_LEVEL_ERROR_TIMESKY);
					return;
				}
				if(target.getTeam() != null)
				{
					target.sendAlert(ErrorCode.ALERT_PLAYER_TEAM_NOTNULL);
					return;
				}

				if(target.getPlayer().level >= bf.overLevel && bf.overLevel > 0)
				{
					target.sendAlert(ErrorCode.ALERT_PLAYER_OVER_LEVEL_ERROR);
					return;
				}
				OtherExtInfo oei = (OtherExtInfo) target.getPlayer().getExtPlayerInfo("otherExtInfo");
				//客户端提示玩家如果进入这个副本的话 其他的时空之旅副本的数据将被清除,增加一个变量,在这里
				//通知的时候变量加一，如果变量值为二的时候就直接清除其他时空之旅副本的数据，进入这个
				List list = (List) DataFactory.getInstance().getAttachment(DataFactory.COPY_LIST);
				for (int i = 0; i < list.size(); i++) 
				{
					CopyController cpc = (CopyController) list.get(i);
					if(cpc.type == 2)
					{
						int loc = DataFactory.getInstance().getCopyNum(cpc.areaId);
						oei.assRoomId[loc] = 0;
						oei.copyPoint[loc] = 0;
						oei.isAssOver[loc] = false;
					}
				}
				target.moveToRoom(bf.roomId);
			}
		}
		else if(bf.type == 18)
		{
			GoldPartyController gpc = GoldPartyController.getInstance();
			if(!gpc.isReady())
			{
				target.sendAlert(ErrorCode.ALERT_PARTY_CLOSING);
				return;
			}
			if(target.getTeam() != null)
			{
				target.sendAlert(ErrorCode.ALERT_PARTY_ISNOT_ACCEPT_TEAM);
				return;
			}
			if(gpc.getBattleCount(target) >= GoldPartyController.BATTLECOUNT)
			{
				target.sendAlert(ErrorCode.ALERT_BATTLE_COUNT_IS_MAX);
				return;
			}
			if(bf.checkFly(target))
			{
				gpc.addPlayer(target);
				bf.sendFly(target);
			}
		}
		else
		{
			if(bf.checkFly(target))
			{
				bf.sendFly(target);
			}
		}
	}
	
	public int getHonorNvshenCopyId(PlayerController target,int type)
	{
		OtherExtInfo oei = (OtherExtInfo) target.getPlayer().getExtPlayerInfo("otherExtInfo");
		List list = (List) DataFactory.getInstance().getAttachment(DataFactory.COPY_LIST);
		for (int i = 0; i < list.size(); i++) 
		{
			CopyController copy = (CopyController) list.get(i);
			if(type == 21)
			{
				if(copy.type == 3)
				{
					int num = DataFactory.getInstance().getCopyNum(copy.areaId);
					if(num != -1)
					{
						if(oei.assRoomId[num] != 0)
							return oei.assRoomId[num];
					}
				}
			}
			else if(type == 22)
			{
				if(copy.type == 4)
				{
					int num = DataFactory.getInstance().getCopyNum(copy.areaId);
					if(num != -1)
					{
						if(oei.assRoomId[num] != 0)
							return oei.assRoomId[num];
					}
				}
			}
			else if(type == 23)
			{
				if(copy.type == 5)
				{
					int num = DataFactory.getInstance().getCopyNum(copy.areaId);
					if(num != -1)
					{
						if(oei.assRoomId[num] != 0)
							return oei.assRoomId[num];
					}
				}
			}
		}
		return 0;
	}
	
	public int getTimeSkyCopyId(PlayerController target)
	{
		OtherExtInfo oei = (OtherExtInfo) target.getPlayer().getExtPlayerInfo("otherExtInfo");
		List list = (List) DataFactory.getInstance().getAttachment(DataFactory.COPY_LIST);
		for (int i = 0; i < list.size(); i++) 
		{
			CopyController copy = (CopyController) list.get(i);
			if(copy.type != 2)
				continue;
			int num = DataFactory.getInstance().getCopyNum(copy.areaId);
			if(num != -1)
			{
				if(oei.assRoomId[num] != 0)
					return oei.assRoomId[num];
			}
		}
		return 0;
	}

//	/**
//	 * 设置阵营
//	 * @param msg
//	 */
//	private void setCamp(PlayerController target,String c)
//	{
//		if(target.getPlayer().level < 20)
//		{
//			target.sendAlert(ErrorCode.ALERT_LEVEL_LESS_CAMP_LEVEL);
//			return;
//		}
//		if(target.getPlayer().camp != 0)
//		{
//			target.sendAlert(ErrorCode.ALERT_PLAYER_CAMP_NOT_NULL);
//			return;
//		}
//		if(c.isEmpty() || c.trim().length() == 0)
//			return;
//		int camp = 0;
//		try 
//		{
//			camp = Integer.parseInt(c);
//		} catch (Exception e) {
//			return;
//		}
//		if(camp != 1 && camp != 2)
//		{
//			target.sendAlert(ErrorCode.ALERT_NOT_THE_CAMP);
//			return;
//		}
//		FamilyController family = target.getFamily();
//		if(family != null)
//		{
//			if(camp != family.camp && family.camp != 0)
//			{
//				target.sendAlert(ErrorCode.ALERT_FAMILY_CAMP_ERROR);
//				return;
//			}
//		}
//		target.getPlayer().setCamp(camp);
//		ByteBuffer buffer = new ByteBuffer(5);
//		buffer.writeByte(camp);
//		buffer.writeInt(target.getID());//选择了阵营才发这个ID
//		target.getRoom().dispatchMsg(SMsg.S_PLAYER_CAMP_SET_COMMAND, buffer);
//		
//		if(target.getTeam() != null)
//			target.getTeam().playerLeaveTeam(target);
//	}
	
/*	private void familyList(PlayerController target)
	{
		
		TongController targetTong = target.getTong();
		
		if(targetTong == null)
		{
			target.sendAlert(ErrorCode.ALERT_CDR_CAN_USE);
			return;
		}
		if(target.getID() != targetTong.leaderId &&
				target.getID() != targetTong.deputyLeaderId)
		{
			target.sendAlert(ErrorCode.ALERT_CDR_CAN_USE);
			return;
		}
		
		List list = worldManager.getFamilyList();
		int size = list.size();
		
		ByteBuffer buffer = new ByteBuffer(64);
		//buffer.writeInt(size);
		for (int i = 0; i < size; i++)
		{
			FamilyController family = (FamilyController)list.get(i);
			
			if(family == null)
				continue;
			
			if(target.getPlayer().camp != family.camp)
				continue;
			
			buffer.writeBoolean(family.tongId != 0);
			buffer.writeUTF(family.name);
			buffer.writeUTF(family.leaderName);
			buffer.writeInt(family.count);
		}
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_PLAYER_FAMILYLISTS_COMMAND,buffer));
	}*/

/*	private void tongCreate(PlayerController target, String name)
	{
		FamilyController family = target.getFamily();
		
		if(family == null)
		{
			target.sendAlert(ErrorCode.ALERT_NO_FAMILY_ERROR);
			return;
		}
		
		if(target.getPlayer().level < 30)
		{
			target.sendAlert(ErrorCode.ALERT_CREATE_TONG_CON_ERROR);
			return;
		}
		
		Bag bag = (Bag)target.getPlayer().getExtPlayerInfo("bag");
		
		if(bag.point < 1000000)
		{
			target.sendAlert(ErrorCode.ALERT_CREATE_TONG_MONEY_ERROR);
			return;
		}
		
		if(target.getTong() != null)
		{
			target.sendAlert(ErrorCode.ALERT_TONG_NOT_NULL);
			return;
		}
		
		if(worldManager.isTongHaved(name))
		{
			target.sendAlert(ErrorCode.ALERT_TONG_NAME_EXISTS);
			return;
		}
		
		int id = worldManager.getDatabaseAccessor().getFamilyObjIndex();
		
		TongController tong = new TongController(id,name);
		tong.setWorldManager(worldManager);
		tong.setLeader(target);
		tong.addFamily(family);
		
		family.tongId = id;
		
		for (int i = 0; i < family.getPlayerCount(); i++)
		{
			PlayerController everyone = family.getPlayerByIndex(i);
			
			if(everyone == null)
				continue;
			
			everyone.setTongController(tong);
			
			if(everyone.getID() == target.getID())
				continue;
			
			everyone.sendAlert(ErrorCode.ALERT_SHANKH_CREATE_TONG);
		}
		
		//tong.addPlayer(target);
		
		worldManager.addTong(tong);
		
		bag.point -= 1000000;
		ByteBuffer buffer = new ByteBuffer(4);
		buffer.writeInt(1000000);
		target.getNetConnection().sendMessage(new SMsg(
				SMsg.S_PLAYER_TONGCREATE_COMMAND,buffer));
	}*/
	
	private void familyCreate(PlayerController target, String name)
	{
		name = MarkChar.replace(name);
		
		if(!UseChar.isCanReg(name))
		{
			target.sendAlert(ErrorCode.ALERT_HUOXINGWEN);
			return;
		}
		
		if(target.getPlayer().level < FamilyController.LEVEL)
		{
			target.sendAlert(ErrorCode.ALERT_FAMILY_NOT_CREATE);
			return;
		}
		
		if(target.getPlayer().camp == 0)
		{
			target.sendAlert(ErrorCode.ALERT_FAMILY_NOT_CREATE);
			return;
		}
		
		Bag bag = (Bag)target.getPlayer().getExtPlayerInfo("bag");
		
		if(bag.point < FamilyController.FAMILY_POINT_CONDITION)
		{
			target.sendAlert(ErrorCode.ALERT_FAMILY_NOT_CREATE);
			return;
		}
		
		if(target.getFamily() != null)
		{
			target.sendAlert(ErrorCode.EXCEPTION_PLAYER_HAS_FAMILY);
			return;
		}
		
		if(worldManager.isFamilyHaved(name))
		{
			target.sendAlert(ErrorCode.EXCEPTION_PLAYER_FAMILYNAME_SAME);
			return;
		}
		
		int id = worldManager.getDatabaseAccessor().getFamilyObjIndex();
		FamilyController family = new FamilyController(id,name);
		family.setWorldManager(worldManager);
		target.getPlayer().familyId = id;
		target.getPlayer().familyName = name;
		target.getPlayer().isFamilyLeader = true;
		target.setFamilyController(family);
		family.setLeader(target);
		family.addNameToFamily(target.getName());
		family.addPlayer(target);
		
		worldManager.addFamily(family);
		
		bag.point -= 100000;
		ByteBuffer buffer = new ByteBuffer(4);
		buffer.writeInt(100000);
		buffer.writeUTF(family.name);
		target.getNetConnection().sendMessage(new SMsg(
				SMsg.S_PLAYER_FAMILYCREATE_COMMAND,buffer));
		
		
		/**
		 * 玩家创建家族，更新家族列表
		 */
		GameServer.getInstance().getDatabaseAccessor().createOrUpdateFamilyInfo(family, true);
		target.getWorldManager().getJobObserver().addJob(GameServer.JOB_DATABASEJOB,
				new SaveJob(target.getWorldManager(),target.getPlayer(),SaveJob.FAMILY_CREATE_SAVE));
		
		GameServer.getInstance().getDatabaseAccessor().savedObjIndexs();
	}
	
	

	/**
	 * 检测点击npc
	 */
	private void clickNpc(PlayerController target)
	{
		if(tasks.length > 0)
		{
			TaskInfo taskInfo = (TaskInfo)target.getPlayer().getExtPlayerInfo("taskInfo");
			
			StringBuffer strBuff1,strBuff2;
			
			for (int i = 0; i < tasks.length; i++)
			{
				taskInfo.checkReplayTask(tasks[i]);
				
				if(taskInfo.isTaskCompleted(tasks[i].id))
					continue;
				
				if(target.getPlayer().level < tasks[i].con_level)
					continue;
		
				if(target.getPlayer().getZhuanshengState() < tasks[i].con_attribute)
					continue;
				
				if(tasks[i].con_bTaskId != 0)
				{
					if(!taskInfo.isTaskCompleted(tasks[i].con_bTaskId))
						continue;
				}
		
			
				if(tasks[i].con_bAcceptTaskId != 0)
				{
					if(taskInfo.isTaskAccept(tasks[i].con_bAcceptTaskId))
						continue;
				}
				
				if(tasks[i].con_gop != 0)
				{
					if(target.getPlayer().camp != tasks[i].con_gop)
						continue;
				}
	
				Task task = taskInfo.getTaskById(tasks[i].id);

				strBuff1 = new StringBuffer(24);
				strBuff2 = new StringBuffer(24);
				if(task == null)
				{
					
					if(!isTaskAccept[i]) 
						continue;
					
					strBuff1.append(tasks[i].id);
					strBuff1.append(":");
					strBuff1.append(tasks[i].type);
					strBuff1.append(":");
					strBuff1.append(tasks[i].level);
					strBuff1.append(":");
					strBuff1.append(tasks[i].display);
					
					strBuff2.append("link:t");
					strBuff2.append(tasks[i].id);
					strBuff2.append(":01");

					npcDialogs[0].getDialogStep()[0].addTaskOption(strBuff1.toString(),strBuff2.toString());
				}
				else
				{	
					if(task.isEnoughCondition())
					{	
						if(!isTaskFinish[i])
							continue;
						
						
						strBuff1.append(tasks[i].id);
						strBuff1.append(":");
						strBuff1.append(tasks[i].type);
						strBuff1.append(":");
						strBuff1.append(tasks[i].level);
						strBuff1.append(":");
						strBuff1.append(tasks[i].display);
						
						strBuff2.append("link:t");
						strBuff2.append(tasks[i].id);
						strBuff2.append(":03");
			
						npcDialogs[0].getDialogStep()[0].addTaskOption(strBuff1.toString(),
								strBuff2.toString());
					}
					else
					{
						
						strBuff1.append(tasks[i].id);
						strBuff1.append(":");
						strBuff1.append(tasks[i].type);
						strBuff1.append(":");
						strBuff1.append(tasks[i].level);
						strBuff1.append(":");
						strBuff1.append(tasks[i].display);
						
						strBuff2.append("link:t");
						strBuff2.append(tasks[i].id);
						strBuff2.append(":02");
					
						npcDialogs[0].getDialogStep()[0].addTaskOption(strBuff1.toString(),
								strBuff2.toString());
					}
				}
				
			}	
		}
		
		ByteBuffer buffer = new ByteBuffer(20);
		buffer.writeByte(1);//默认的
		npcDialogs[0].getDialogStep()[0].writeTo(buffer);
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_CLICK_NPC_COMMAND,buffer));
	}

	/**
	 * 任务相关命令
	 * @param target
	 * @param com
	 * @return
	 */
	public void taskProcess(PlayerController target,String [] strs)
	{
		TaskInfo taskInfo = (TaskInfo)target.getPlayer().getExtPlayerInfo("taskInfo");

		if(strs[0].equals("accept"))
		{
			if(taskInfo.getCurrentTaskSize() >= TaskInfo.MAX_TASK_COUNT)
			{
				target.sendAlert(ErrorCode.ALERT_PLAYER_TASK_MAX_COUNT);
				return;
			}
			
			Task task = getTaskById(Integer.parseInt(strs[1]));

			if(task == null)
				return;

			if(taskInfo.isTaskCompleted(task.id))
				return;
	
			if(target.getPlayer().level < task.con_level)
				return;

			if(target.getPlayer().getZhuanshengState() < task.con_attribute)
				return;

			if(task.con_bTaskId != 0)
			{
				if(!taskInfo.isTaskCompleted(task.con_bTaskId))
					return;
			}
		

			if(task.con_bAcceptTaskId != 0)
			{
				if(taskInfo.isTaskAccept(task.con_bAcceptTaskId))
					return;
			}
	
			if(task.con_gop != 0)
			{
				if(task.con_gop != target.getPlayer().camp)
				{
					return;
				}
			}
	
			
			Task nTask = new Task();
			task.copyTo(nTask);
	
			if(!taskInfo.addTask(nTask))
				return;
		
			nTask.updateTask(target);
			nTask.sendAddTask(target);
			taskInfo.addAcceptTask(nTask.id);
			
			nTask.checkTaskFinish(target);
		}
		else if(strs[0].equals("finish"))
		{
			Task task = taskInfo.getTaskById(Integer.parseInt(strs[1]));

			if(task == null)
				return;

			if(taskInfo.isTaskCompleted(task.id))
				return;
		
			if(!task.checkTaskFinish(target))
				return;

			
			if(task.awa_item.length != 0)
			{
				Bag bag =(Bag)target.getPlayer().getExtPlayerInfo("bag");
				if(!bag.checkEnough(task.is_awa_item_choose?1:task.awa_item.length))
				{
					target.sendAlert(ErrorCode.ALERT_TASK_AWA_BAG_NOT_ENOUGH);
					return;
				}
			}

			if(task.itemCount > 0)
			{
				if(!task.recovery(target))
				{
					return;
				}
			}
			
			if(strs.length > 2)
			{
				if(task.is_awa_item_choose)
				{
					//发送选择奖励
					boolean result = task.sendAwards(target,Integer.parseInt(strs[2]));

					if(!result)
						return;
				}
			}
			else
			{
				boolean result = task.sendAwards(target,-1);
	
				if(!result)
					return;
			}
			
			if(task.isCanReplay)
			{
				taskInfo.completeReplayTask(task);
				taskInfo.removeTask(task);
			}
			else
			{
				taskInfo.completeTask(task);
			}
		
			task.sendTaskFinish(target);
			target.onTaskFinish(task);
		}
		
	}

	
	public Task getTaskById(int id)
	{
		for (int i = 0; i < tasks.length; i++)
		{
			if(tasks[i].id == id)
				return tasks[i];
		}
		return null;
	}

	public void setNpc(NPC npc)
	{
		this.npc = npc;
	}
	
	public int getID()
	{
		return npc.id;
	}

	public String getName()
	{
		return npc.name;
	}

	public long getObjectIndex() 
	{
		return npc.objectIndex;
	}
	
	public NPC getNpc()
	{
		return npc;
	}
	
	public void setWorld(WorldManager worldManager)
	{
		this.worldManager = worldManager;
	}
	
	public NpcDialog[] getNpcDialogs()
	{
		return npcDialogs;
	}
	
	public Shop getShop()
	{
		return shop;
	}
	
	public Shop getShopById(int shopId)
	{
		for (int i = 0; i < shops.length; i++)
		{
			if(shops[i] == null)
				continue;
			if(shops[i].id == shopId)
				return shops[i];
		}
		return null;
	}

	public void setShop(Shop shop) 
	{
		Shop[] ss = new Shop[shops.length+1];
		for (int i = 0; i < shops.length; i++)
		{
			ss[i] = shops[i];
		}
		ss[shops.length] = shop;
		shops = ss;
	}

	public void addTask(Task task,int index,int length)
	{
		if(!isTaskExist(task))
		{
			Task []taskTmp = new Task[tasks.length+1];
			for (int i = 0; i < tasks.length; i++)
			{
				taskTmp[i] = tasks[i];
			}
			taskTmp[taskTmp.length-1] = task;
			tasks = taskTmp;
		}
		

		if(length == 1)
		{
			addAcceptBoolean(true);
			addFinishBoolean(true);
		}
		else
		{
			if(index == 0)
			{
				addAcceptBoolean(true);
				addFinishBoolean(false);
			}
			else
			{
				addAcceptBoolean(false);
				addFinishBoolean(true);
			}
		}	

	}

	public boolean isTaskExist(int taskId)
	{
		for (int i = 0; i < tasks.length; i++)
		{
			if(tasks[i].id == taskId)
				return true;
		}
		return false;
	}
	
	public Task[] getTasks()
	{
		return tasks;
	}
	
	public boolean isTaskExist(Task task)
	{
		return isTaskExist(task.id);
	}

	
	private void addAcceptBoolean(boolean b)
	{
		boolean []isTaskAcceptTmp = new boolean[isTaskAccept.length+1];
		for (int i = 0; i < isTaskAccept.length; i++)
		{
			isTaskAcceptTmp[i] = isTaskAccept[i];
		}
		isTaskAcceptTmp[isTaskAcceptTmp.length-1] = b;
		isTaskAccept = isTaskAcceptTmp;
	}
	
	private void addFinishBoolean(boolean b)
	{
		boolean []isTaskFinishTmp = new boolean[isTaskFinish.length+1];
		for (int i = 0; i < isTaskFinish.length; i++)
		{
			isTaskFinishTmp[i] = isTaskFinish[i];
		}
		isTaskFinishTmp[isTaskFinishTmp.length-1] = b;
		isTaskFinish = isTaskFinishTmp;
	}
	
	public void anyTask(PlayerController target,ByteBuffer buffer)
	{
		TaskInfo taskInfo = (TaskInfo)target.getPlayer().getExtPlayerInfo("taskInfo");
		
		ByteBuffer buff = new ByteBuffer(16);

		for (int i = 0; i < tasks.length; i++)
		{
			taskInfo.checkReplayTask(tasks[i]);
			
			if(taskInfo.isTaskCompleted(tasks[i].id))
				continue;
			
			if(target.getPlayer().level < tasks[i].con_level)
				continue;
			
			if(target.getPlayer().getZhuanshengState() < tasks[i].con_attribute)
				continue;

			if(tasks[i].con_bTaskId != 0)
			{
				if(!taskInfo.isTaskCompleted(tasks[i].con_bTaskId))
					continue;
			}
			

			if(tasks[i].con_bAcceptTaskId != 0)
			{
				if(taskInfo.isTaskAccept(tasks[i].con_bAcceptTaskId))
					continue;
			}
			
			if(tasks[i].con_gop != 0)
			{
				if(tasks[i].con_gop != target.getPlayer().camp)
				{
					continue;
				}
			}
			
			
			Task task = taskInfo.getTaskById(tasks[i].id);
			

			if(task == null)
			{
				
				if(!isTaskAccept[i]) 
					continue;

				//1 未接任务
				Task t = TaskManager.getInstance().getTask(tasks[i].id);
				buff.writeBoolean(t.isCanReplay);
				buff.writeByte(1);
			}
			else
			{
				if(task.isEnoughCondition())
				{
					if(!isTaskFinish[i])
						continue;
					
					//3满足条件
					buff.writeBoolean(task.isCanReplay);
					buff.writeByte(3);
				}
				else
				{
					//2未满足条件
					buff.writeBoolean(task.isCanReplay);
					buff.writeByte(2);
				}
			}
			
			

		}
		buffer.writeInt(buff.available()/2);
		buffer.writeBytes(buff.getBytes());
	}
	
	public void setParent(PlayerContainer playerContainer)
	{
		super.setParent(playerContainer);
	}

	public void runAttach()
	{
		String attachCmd = npc.attach;
		
		if(attachCmd.startsWith("otherWhere"))
		{
			String [] strs = Utils.split(attachCmd, ":");
			
			for (int i = 0; i < strs.length; )
			{
				if(i == 0)
				{
					i++;
					continue;
				}
				
				int areaId = Integer.parseInt(strs[i++]);
				
				AreaController area = worldManager.getAreaById(areaId);
				
				if(area == null)
				{
					System.out.println("npc.txt otherWhere area not has "+areaId);
					break;
				}
				
				int roomId = Integer.parseInt(strs[i++]);
				
				RoomController room = area.getRoomById(roomId);
					
				if(room == null)
				{
					System.out.println("npc.txt otherWhere room not has "+roomId);
					break;
				}
				
				room.addNpcList(this);
			}

		}
	}
	
	
	

	
	/** 接收玩家和npc的互动 */
	public void clientMessageChain(PlayerController target, AppMessage msg)
	{
		int type = msg.getType();
		
		if(type == SMsg.C_CLICK_NPC_COMMAND)
		{
			requestNpcDialog(target,msg);
		}
		else if(type == SMsg.C_BUY_GOODS_COMMAND)
		{
			shop.clientMessageChain(target, msg);
		}
	}
}
