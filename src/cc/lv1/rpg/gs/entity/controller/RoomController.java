package cc.lv1.rpg.gs.entity.controller;

import vin.rabbit.comm.GameObject;
import vin.rabbit.net.AppMessage;
import vin.rabbit.net.abs.NetConnection;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.impl.ArrayList;
import vin.rabbit.util.xml.XMLNode;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.data.DC;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.RoomExit;
import cc.lv1.rpg.gs.entity.container.PlayerContainer;
import cc.lv1.rpg.gs.entity.ext.Bag;
import cc.lv1.rpg.gs.entity.ext.BuffBox;
import cc.lv1.rpg.gs.entity.ext.PetTome;
import cc.lv1.rpg.gs.entity.impl.GoldParty;
import cc.lv1.rpg.gs.entity.impl.Goods;
import cc.lv1.rpg.gs.entity.impl.MoneyBattle;
import cc.lv1.rpg.gs.entity.impl.NPC;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsProp;
import cc.lv1.rpg.gs.entity.impl.pet.Pet;
import cc.lv1.rpg.gs.gui.MainFrame;
import cc.lv1.rpg.gs.net.SMsg;
import cc.lv1.rpg.gs.other.ErrorCode;
import cc.lv1.rpg.gs.util.Code;

/**
 * 房间控制器
 * @author dxw
 *
 */
public class RoomController extends PlayerContainer
{

	private static final int DEFUALT_NATIVITY = -1;
	
	private static final MonsterGroupController[] NULL_MONSTER_GROUP = new MonsterGroupController[0];
	
	private static final NpcController[] NULL_NPC_LIST = new NpcController[0];
	
	private WorldManager worldManager;
	
	private AreaController area;
	
	/** 副本控制器 */
	private CopyController copy;
	
	/** 是否家族活动备战厅 */
	public boolean isPartyRoom = false;
	
	/** 是否家族活动PK房间 */
	private boolean isPartyPKRoom = false;
	
	/** 是否副本房间 */
	public boolean isCopyPartyRoom = false;
	
	/** 是否黄金斗士专用房间(不包括备战厅) */
	public boolean isGoldPartyPKRoom = false;
	
	/** 是否黄金斗士备战厅 */
	public boolean isGoldPartyRoom = false;
	
	/** 是否剧情房间 */
	public boolean isStoryRoom = false;
	
	/** 房间类型 */
	public int type;
	
	/** 包含在本房间的monster */
	private MonsterGroupController[] monsterGroups = NULL_MONSTER_GROUP;
	
	/** 包含在本房间的npc */
	private NpcController[] npcList = NULL_NPC_LIST;
	
	/** 能否战斗 */
	private boolean fightAble;

	/** 能否PK */
	private boolean pkAble;
	
	/** 复活点 */
	private int [] rebirthId = new int[3];
	
	/** 0左下 1左上 2上左 3上右 4右上 5右下 6下右 7下左 */
	private RoomExit [] exits  = new RoomExit[8];
	
	private ArrayList monstersNode = null;
	
	/** 事件点数  每个房间都有事件点数，必须满足事件点数才能进行传送*/
	private int eventPoint;
	
	/** 队伍列表 */
	private List teamList = new ArrayList();
 
	public RoomController()
	{
	}
	
	/**
	 * 初始化
	 * @param node
	 */
	public void init(XMLNode node)
	{
		id = Integer.parseInt(node.getName().substring(1));
		
		name = node.getSubNode("name").getData();

		fightAble = node.getSubNode("fight").equals("1");
		
		pkAble = node.getSubNode("pk").equals("1");

		fightAble = node.getSubNode("fight").getData().equals("1");

		pkAble = node.getSubNode("pk").getData().equals("1");
	
		String rebirth = node.getSubNode("rebirth").getData();
		rebirth = rebirth.substring(1);

		String []rebirths = Utils.split(rebirth, ":");
		
		for (int i = 0; i < rebirths.length; i++)
		{
			rebirthId[i] = Integer.parseInt(rebirths[i]);
		}
		
		monstersNode = node.getSubNode("monsters").getSubNodes();
		
		if(id == DataFactory.PARTYPKROOM)
		{
			isPartyPKRoom = true;
		}
		else if(id == DataFactory.KAITUOROOM || id == DataFactory.XIESHENROOM)
		{
			isPartyRoom = true;
		}
		else
		{
			GoldParty gp = DataFactory.getInstance().getGoldPartyByRoom(id);
			if(gp != null)
			{
				if(gp.level == 0)
					isGoldPartyRoom = true;
				if(gp.level > 0)
					isGoldPartyPKRoom = true;
			}
		}
	}
	
	/**
	 * 发送自身
	 * @param target
	 * @param nativityIndex 出生的index -1代表默认 其他为0-7
	 */
	public void sendInfo(PlayerController target)
	{
		ByteBuffer buffer = new ByteBuffer(64);
		writeTo(buffer);
		SMsg msg = new SMsg(SMsg.S_ROOM_INFO_COMMAND,buffer);
		target.getNetConnection().sendMessage(msg);
	}
	
	public void copyTo(GameObject gameobject)
	{
		super.copyTo(gameobject);

		RoomController roomController = (RoomController)gameobject;
		roomController.worldManager = worldManager;
		
		roomController.type = type;
		roomController.fightAble = fightAble;
		roomController.pkAble = pkAble;
		roomController.rebirthId = rebirthId;
		roomController.monstersNode = monstersNode;
		roomController.isPartyPKRoom = isPartyPKRoom;
		roomController.isPartyRoom = isPartyRoom;
		roomController.isCopyPartyRoom = isCopyPartyRoom;
		roomController.copy = copy;
		roomController.isGoldPartyPKRoom = isGoldPartyPKRoom;
		roomController.isGoldPartyRoom = isGoldPartyRoom;
		roomController.isStoryRoom = isStoryRoom;
	}
	
	/**
	 * 当前位置是否为空
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isPositionNull(int x,int y)
	{
		for (int i = 0; i < monsterGroups.length; i++)
		{
			if(monsterGroups[i] ==null)
				continue;
			if(monsterGroups[i].x + 30 > x && monsterGroups[i].x - 30 < x
					&& monsterGroups[i].y + 10 > y && monsterGroups[i].y - 10 < y)
				return false;
		}
		for (int i = 0; i < npcList.length; i++)
		{
			if(npcList[i] ==null)
				continue;
			if(npcList[i].getNpc().x + 30 > x && npcList[i].getNpc().x - 30 < x
					&& npcList[i].getNpc().y + 10 > y && npcList[i].getNpc().y - 10 < y)
				return false;
		}
		for (int i = 0; i < playerList.size(); i++)
		{
			PlayerController player = (PlayerController) playerList.get(i);
			if(player == null || !player.isOnline())
				continue;
			if(player.getPlayer().x + 30 > x && player.getPlayer().x - 30 < x
					&& player.getPlayer().y + 10 > y && player.getPlayer().y - 10 < y)
				return false;
		}
		return true;
	}
	
	public int getActivePointType()
	{
		if(copy != null)
		{
			if(copy.type == 2)
				return 3;
			else
				return 1;
		}
		else
		{
			for (int i = 0; i < monsterGroups.length; i++)
			{
				if(monsterGroups[i] == null)
					continue;
				if(monsterGroups[i].isBodyType())
					return 2;
			}
		}
		
		if(id == DataFactory.HONORROOM)
			return 3;
		return 1; 
	}
	
	public void setRoomType()
	{
		if(isPartyPKRoom || isPartyRoom)
		{
			type = 2;
		}
		else if(isCopyPartyRoom)
		{
			if(copy != null && !copy.isTeam)
			{
				if(copy.type == 3 || copy.type == 4 || copy.type == 5)
					type = 8;
				else
					type = 3;
			}
			else
				type = 4;
		}
		else if(isGoldPartyPKRoom)
		{
			type = 6;
		}
		else if(isGoldPartyRoom)
		{
			type = 7;
		}
		else if(monsterGroups.length == 0 || monsterGroups == null)
		{
			type = 5;
		}
		else if(isStoryRoom)
		{
			type = 10;
		}
		else
		{
			MoneyBattle mb = DataFactory.getInstance().getMoneyBattle(id);
			if(mb == null)
			{
				for (int j = 0; j < monsterGroups.length; j++)
				{
					if(monsterGroups[j] != null && monsterGroups[j].isBoss())
					{
						type = 5;
						break;
					}	
				}
			}
			else
			{
				type = 9;
			}
		}
	}

	/**
	 * 发送房间信息给客户端
	 */
	public void writeTo(ByteBuffer buffer)
	{
		setRoomType();
		buffer.writeByte(type);
		buffer.writeInt(area.id);
		
		super.writeTo(buffer);

		buffer.writeBoolean(fightAble);
		buffer.writeBoolean(pkAble);
		
		int i = 0;
		for(; i < 8 ; i ++)
		{
			if(exits[i]==null)
				buffer.writeBoolean(false);
			else
				buffer.writeBoolean(true);
		}
		
		buffer.writeInt(eventPoint);
		
		writeNpcBaseInfo(buffer);
		
		int monsterLength = monsterGroups.length;
		buffer.writeInt(monsterLength);
		for (i = 0; i < monsterLength; i++) 
		{
			monsterGroups[i].writeBaseTo(buffer);
		}
		
		writePlayers(buffer);
		
		writePlayerPet(buffer);
	}
	
	/**
	 * 发送所有正在遛宠的玩家的宠物信息
	 * @param buffer
	 */
	public void writePlayerPet(ByteBuffer buffer)
	{
		int count = 0;
		int size = playerList.size();
		for(int i = 0 ; i < size; i ++)
		{
			PlayerController everyone = (PlayerController)playerList.get(i);
			
			if(everyone == null)
				continue;
			
			PetTome pt = (PetTome) everyone.getPlayer().getExtPlayerInfo("petTome");
			Pet pet = pt.getActivePet();
			if(pet == null)
				continue;
			
			if(pet.isStroll)
				count++;
		}
		
		buffer.writeInt(count);
		size = playerList.size();
		for(int i = 0 ; i < size ; i ++)
		{
			PlayerController everyone = (PlayerController)playerList.get(i);
			
			if(everyone == null)
				continue;
			
			PetTome pt = (PetTome) everyone.getPlayer().getExtPlayerInfo("petTome");
			Pet pet = pt.getActivePet();
			if(pet == null)
				continue;
			
			if(pet.isStroll)
			{
				buffer.writeInt(everyone.getID());
				buffer.writeInt(pet.modelId);
				buffer.writeUTF(pet.name);
			}
		}
		
	}
	

	/**
	 * 发送所有怪物属性
	 * @param buffer
	 */

	public void writeMonsters(ByteBuffer buffer)
	{
		int monsterLength = monsterGroups.length;
		buffer.writeInt(monsterLength);
		for (int i = 0; i < monsterLength; i++) 
		{
			monsterGroups[i].writeTo(buffer);
		}
	}
	
	/**
	 * 发送房间内所有的NPC信息
	 * @param buffer
	 */
	public void writeNpcBaseInfo(ByteBuffer buffer)
	{
		int npcLength = 0;
		
		for(int i = 0 ; i < npcList.length ; i ++)
		{
			NPC npc = npcList[i].getNpc();
			String str = GoldPartyController.getInstance().getHideNpcInfo(npc.id);
			if("hide".equals(npc.script))
			{
				if("".equals(str))
					continue;
				else
				{
					npcLength++;
				}
			}
			else
				npcLength++;
		}
		
		buffer.writeInt(npcLength);

		for(int i = 0 ; i < npcList.length ; i ++)
		{
			NPC npc = npcList[i].getNpc();
			String str = GoldPartyController.getInstance().getHideNpcInfo(npc.id);
			if("hide".equals(npc.script))
			{
				if("".equals(str))
					continue;
				else
				{
					String[] strs = Utils.split(str, ":");
					npc.modelId = Integer.parseInt(strs[1]);
					npc.name = strs[2];
					npc.writeBaseTo(buffer);
				}
			}
			else
				npc.writeBaseTo(buffer);
		}
	}
	
	/**
	 * 发送房间内所有的NPC信息
	 * @param buffer
	 */
	public void writeNpcs(ByteBuffer buffer)
	{
		int npcLength = npcList.length;
		
		buffer.writeByte(npcLength);

		for(int i = 0 ; i < npcLength ; i ++)
		{
			npcList[i].getNpc().writeTo(buffer);
		}
	}
	
	/**
	 * 发送房间内的所有玩家信息
	 * @param buffer
	 */
	public void writePlayers(ByteBuffer buffer)
	{
		int size = playerList.size();
		buffer.writeInt(size); //这个size不可信 
	
		for(int i = 0 ; i < size ; i ++)
		{
			PlayerController everyone = (PlayerController)playerList.get(i);
			
			if(everyone == null)
				continue;
			
			everyone.getPlayer().writeTo(buffer);
			buffer.writeBoolean(everyone.getParent() instanceof BattleController); //当前玩家是否在战斗
		    buffer.writeBoolean(!everyone.isTeamMember());//是否是队长(单人的算是队长)
		}
		
	}
	
	
	
	private static final long CHECKPLAYERTIMER = 1000*60;
	
	private long checkTimer = 0;
	
	public void update(long timeMillis)
	{
		super.update(timeMillis);
		int battleSize = battleList.size();
		for(int i = 0 ; i < battleSize ; i ++ )
		{
			BattleController battle = (BattleController)battleList.get(i);
			battle.update(timeMillis);
			if(battle.isFinish)
			{
				battleList.remove(i--);
				battleSize--;
				battle.dispatchIsBattleWithoutRoom(false, null);
				
				if(battle instanceof PVEController || battle instanceof GoldPVEController)
				{
					if(!battle.isWin)
						displayMonsterGroup(battle.objectIndex);
				}
				
			}
		}
		
		for (int i = 0; i < monsterGroups.length; i++)
		{
			monsterGroups[i].update(timeMillis);
		}
		
		
	
 		if(timeMillis > checkTimer +CHECKPLAYERTIMER)
		{
			checkTimer = timeMillis;
			
			for(int i = 0 ; i < playerList.size() ; i ++)
			{
				PlayerController everyone = (PlayerController)playerList.get(i);
				
				if(i >1000)
				{
					System.out.println("RoomController update i > 1000");
				}
				
				if(everyone == null)
				{
					playerList.remove(i);
					i = 0;
					System.out.println("delete null player with update RoomController");
					continue;
				}
				
				if(!everyone.isReset)
				{
					if(everyone.isOnline() && everyone.getRoom().id == id)
						continue;
					else
						removePlayer(everyone);
				}
				
				if(everyone.getNetConnection() == null)
					removePlayer(everyone);
			}
		}

	}

	
	private void displayMonsterGroup(long objectIndex)
	{
		for (int i = 0; i < monsterGroups.length; i++)
		{
			if(monsterGroups[i].objectIndex == objectIndex)
			{
				monsterGroups[i].vTimer = 0;
				monsterGroups[i].setVisibled(this, 2);
				break;
			}
		}
	}
	
	/**
	 * 玩家打怪
	 * @param target
	 * @param monsters
	 */
	public void playerHitMonster(PlayerController target,MonsterGroupController monsters)
	{
		monsterHitPlayer(monsters ,target);
	}

	/**
	 * 怪打玩家
	 * @param group
	 * @param target
	 */
	public void monsterHitPlayer(MonsterGroupController group , PlayerController target)
	{
		String fightName = group.name+"-vs-"+target.getName();

		BattleController battle = null;
		if(isCopyPartyRoom)
		{
			battle = createCopyPVE(fightName);
		}
		else if(isGoldPartyRoom || isGoldPartyPKRoom)
		{
			battle = createGoldPVE(fightName);
		}
		else if(target.getEvent() != null && target.getEvent().eventType == 1 && target.getEvent().isStoryRoom(this) && target.getTeam() == null)
		{
			battle = createEventPVE(fightName);
		}
		else
		{
			battle = createPVE(fightName);
		}

		MonsterController [] monsters = group.getMonstersInitial();
		
		for (int i = 0; i < monsters.length; i++)
		{
			if(monsters[i] == null)
				continue;
			
			if(monsters[i].getMonster().logicType == 2) //是boss
			{
				if(battle instanceof CopyPVEController)
				{
					CopyPVEController pve = (CopyPVEController)battle;
					pve.setBoss(true);
				}
				else if(battle instanceof PVEController)
				{
					PVEController pve = (PVEController)battle;
					pve.setBoss(true);
				}
				else if(battle instanceof EventPVEController)
				{
					EventPVEController pve = (EventPVEController)battle;
					pve.setBoss(true);
				}
				break;
			}
		}

		PlayerController [] players = null;
		TeamController team = target.getTeam();

		if(team==null)
		{
			players = new PlayerController[1];
			players[0] = target;
		}
		else
		{
			players = team.getPlayers();
		}
		
		if(DataFactory.isActivePoint)
		{
			if(!isCopyPartyRoom)
			{
				for (int i = 0; i < monsters.length; i++) 
				{
					if(monsters[i] == null)
						continue;
					
					if(monsters[i].getMonster().bodyType == 1)
					{
						if(!checkBattleNeedInfo(1,players,target,group))
							return;
					}
					else
						continue;
				}
				
				if(!checkBattleNeedInfo(4,players,target,group))
					return;
				
				if(target.isAuto)
				{
					if(!checkBattleNeedInfo(3,players,target,group))
						return;
					
					setCopyBattleNeedInfo(3,players, target, group,monsters);
				}
				
				setCopyBattleNeedInfo(1,players, target, group,monsters);
				
				setCopyBattleNeedInfo(4,players, target, group,monsters);
			}
			else
			{
				if(copy != null)
				{
					if(!checkBattleNeedInfo(2,players,target,group))
						return;
					
					if(!checkBattleNeedInfo(4,players,target,group))
						return;
					
					setCopyBattleNeedInfo(2,players, target, group,monsters);
					
					setCopyBattleNeedInfo(4,players, target, group,monsters);
				}
			}
		}
	
		battle.setFighters(players, monsters);
		battleList.add(battle);
	
		ByteBuffer buffer = new ByteBuffer(64);
		battle.writeTo(buffer);
		battle.dispatchMsg(SMsg.S_ROOM_PVE_COMMAND, buffer);
	
		for (int i = 0; i < players.length; i++) 
		{
			if(players[i] == null)
				continue;
			battle.dispatchIsBattleWithoutRoom(true,players[i]);
		}
		
		
		battle.objectIndex = group.objectIndex;
		group.setVisibled(this,1);

		battle.haloStart(players, battle);//启动光环
	}
	
	/**
	 * set battle needInfo
	 * @param type 1:bossActivePoint 2:copyInfo 3:autoBattle 4:needGoods
	 * @param players
	 * @param target
	 * @param group
	 * @param monsters
	 */
	private void setCopyBattleNeedInfo(int type,PlayerController[] players,PlayerController target,MonsterGroupController group,MonsterController[] monsters)
	{
		if(type == 1)
		{
			for (int i = 0; i < monsters.length; i++) 
			{
				if(monsters[i] == null)
					continue;
				
				if(monsters[i].getMonster().bodyType == 1)
				{
					for (int j = 0; j < players.length; j++) 
					{
						if(players[j] == null || !players[j].isOnline())
							continue;
						players[j].getPlayer().bossActivePoint--;
					}
					break;
				}
			}
		}
		else if(type == 2)
		{
			if(copy == null)
				return;
			if(copy.type == 2)
			{
				if(!copy.isHideRoom(id))
				{
					for (int j = 0; j < players.length; j++) 
					{
						if(players[j] == null || !players[j].isOnline())
							continue;
						players[j].getPlayer().timeCopyActivePoint--;
						players[j].sendActivePoint();
					}
				}
			}
			else if(copy.type == 3 || copy.type == 4)
			{
				if(copy.isHideRoom(id))//隐藏房间扣2元宝
				{
					for (int j = 0; j < players.length; j++) 
					{
						if(players[j] == null || !players[j].isOnline())
							continue;
						Bag bag = (Bag) players[j].getPlayer().getExtPlayerInfo("bag");
						bag.money -= 2;
						bag.sendAddGoods(target, null);
					}
				}
				else//非隐藏房间扣1行动值
				{
					for (int j = 0; j < players.length; j++) 
					{
						if(players[j] == null || !players[j].isOnline())
							continue;
						players[j].getPlayer().flyActivePoint -= 1;
						players[j].sendFlyActivePoint();
					}
				}
			}
			else if(copy.type == 5)//扣2元宝
			{
				for (int j = 0; j < players.length; j++) 
				{
					if(players[j] == null || !players[j].isOnline())
						continue;
					Bag bag = (Bag) players[j].getPlayer().getExtPlayerInfo("bag");
					bag.money -= 2;
					bag.sendAddGoods(target, null);
				}
			}
		}
		else if(type == 3)
		{
			for (int i = 0; i < players.length; i++) 
			{
				if(players[i] == null || !players[i].isOnline())
					continue;
				Bag bag = (Bag) players[i].getPlayer().getExtPlayerInfo("bag");
				Goods goods = bag.getGoodsByType(Goods.AUTOBATTLECARD);
				bag.removeGoods(players[i], goods.objectIndex, 1);
				players[i].autoCount--;
				
				players[i].sendAutoData();
			}
		}
		else if(type == 4)
		{
			for (int i = 0; i < players.length; i++) 
			{
				if(players[i] == null || !players[i].isOnline())
					continue;
				Bag bag = (Bag) players[i].getPlayer().getExtPlayerInfo("bag");
				if(group.needGoodsId == 0 || group.needGoodsCount == 0)
					break;
				bag.deleteGoods(players[i], group.needGoodsId, group.needGoodsCount);
			}
		}
	}
	
	/**
	 * checkBattleNeedInfo
	 * @param type 1:bossActivePoint 2:copyInfo 3:autoBattle 4:needGoods
	 * @param players
	 * @param target
	 * @param group
	 * @return
	 */
	private boolean checkBattleNeedInfo(int type,PlayerController[] players,PlayerController target,MonsterGroupController group)
	{
		for (int j = 0; j < players.length; j++) 
		{
			if(players[j] == null || !players[j].isOnline())
				continue;
			if(type == 1)
			{
				if(players[j].getPlayer().bossActivePoint <= 0)
				{
					if(players[j].getID() == target.getID())
						target.sendAlert(ErrorCode.ALERT_PLAYER_BOSS_ACTIVEPOINT_ERROR);//击杀BOSS行动值不够
					else
						target.sendError(players[j].getName()+DC.getString(DC.ROOM_1));
					return false;
				}
			}
			else if(type == 2)
			{
				if(copy == null)
					return false;
				if(copy.type == 2)
				{
					if(!copy.isHideRoom(id))
					{
						if(players[j].getPlayer().timeCopyActivePoint <= 0)
						{
							if(players[j].getID() == target.getID())
								target.sendAlert(ErrorCode.ALERT_PLAYER_TIME_ACTIVEPOINT_ERROR);//
							else
								target.sendError(players[j].getName()+DC.getString(DC.ROOM_2));
							return false;
						}
					}
				}
				else if(copy.type == 3 || copy.type == 4)
				{
					if(copy.isHideRoom(id))//隐藏房间扣2元宝
					{
						Bag bag = (Bag) players[j].getPlayer().getExtPlayerInfo("bag");
						if(bag.money < 2)
						{
							if(players[j].getID() == target.getID())
								target.sendAlert(ErrorCode.ALERT_SHOP_NO_MONEY);
							else
								target.sendError(players[j].getName()+DC.getString(DC.ROOM_3));
							return false;
						}
					}
					else//非隐藏房间扣1行动值
					{
						if(players[j].getPlayer().flyActivePoint < 1)
						{
							if(players[j].getID() == target.getID())
								target.sendAlert(ErrorCode.ALERT_NO_FLYACTIVEPOINT);
							else
								target.sendError(players[j].getName()+DC.getString(DC.ROOM_4));
							return false;
						}
					}
				}
				else if(copy.type == 5)//扣2元宝
				{
					Bag bag = (Bag) players[j].getPlayer().getExtPlayerInfo("bag");
					if(bag.money < 2)
					{
						if(players[j].getID() == target.getID())
							target.sendAlert(ErrorCode.ALERT_SHOP_NO_MONEY);
						else
							target.sendError(players[j].getName()+DC.getString(DC.ROOM_3));
						return false;
					}
				}
			}
			else if(type == 3)
			{
				Bag bag = (Bag) players[j].getPlayer().getExtPlayerInfo("bag");
				Goods goods = bag.getGoodsByType(Goods.AUTOBATTLECARD);
				if(goods == null)
				{	
					if(players[j].getID() == target.getID())
						target.sendAlert(ErrorCode.ALERT_AUTO_BATTLE_CARD_ERROR);
					else
						target.sendError("["+players[j].getName()+"]"+DC.getString(DC.ROOM_5));
					players[j].setAuto(false, 0, null);
					return false;
				}
				else
				{
					if(bag.getGoodsCountByType(Goods.AUTOBATTLECARD) < target.autoCount)
					{
						if(players[j].getID() == target.getID())
							target.sendAlert(ErrorCode.ALERT_AUTO_BATTLE_CARD_ERROR);
						else
							target.sendError("["+players[j].getName()+"]"+DC.getString(DC.ROOM_5));
						players[j].setAuto(false, 0, null);
						return false;
					}
				}
			}
			else if(type == 4)
			{
				Bag bag = (Bag) players[j].getPlayer().getExtPlayerInfo("bag");
				if(group.needGoodsId == 0 || group.needGoodsCount == 0)
					break;
				Goods goods = (Goods) DataFactory.getInstance().getGameObject(group.needGoodsId);
				if(goods == null)
				{
					System.out.println("RoomController playerHitMonster goods is null:"+group.needGoodsId);
					return false;
				}
				if(bag.getGoodsCount(group.needGoodsId) < group.needGoodsCount)
				{//你没有XXX道具，请去完成相关任务后领取
					StringBuffer sb = new StringBuffer();
					if(players[j].getID() == target.getID())
						sb.append(DC.getString(DC.ROOM_6));
					else
						sb.append(players[j].getName());
					sb.append(DC.getString(DC.ROOM_7));
					sb.append(goods.name);
					sb.append(",");
					sb.append(DC.getString(DC.ROOM_8));
					target.sendError(sb.toString());
					sb = null;
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * 玩家打玩家
	 * @param source
	 * @param target
	 */
	public void playerHitPlayer(PlayerController inviter,PlayerController target)
	{
		if(!inviter.isOnline() || inviter.getParent()!=this)
			return;
		
		if(!target.isOnline() || target.getParent()!=this)
			return;
		
		String fightName = inviter.getName()+"-vs-"+target.getName();
		
		BattleController battle = null;
		
		if(isPartyPKRoom)
		{		
			if(inviter.getTeam() == null)
			{
				Bag bag = (Bag)inviter.getPlayer().getExtPlayerInfo("bag");
				if(bag.getExtGoods(3) != null)
				{
					inviter.sendAlert(ErrorCode.ALERT_PARTY_CDTIMING);
					return;
				}
			}
			else
			{
				PlayerController[] pcs = inviter.getTeam().getPlayers();
				for (int i = 0; i < pcs.length; i++)
				{
					if(pcs[i] == null || !pcs[i].isOnline())
						continue;
					Bag bag = (Bag) pcs[i].getPlayer().getExtPlayerInfo("bag");
					if(bag.getExtGoods(3) != null)
					{
						inviter.sendAlert(ErrorCode.ALERT_PARTY_CDTIMING);
						return;
					}
				}
			}
			
			if(target.getTeam() == null)
			{
				Bag bag = (Bag)target.getPlayer().getExtPlayerInfo("bag");
				if(bag.getExtGoods(3) != null)
				{
					inviter.sendAlert(ErrorCode.ALERT_PARTY_CDTIMING);
					return;
				}
			}
			else
			{
				PlayerController[] pcs = target.getTeam().getPlayers();
				for (int i = 0; i < pcs.length; i++)
				{
					if(pcs[i] == null || !pcs[i].isOnline())
						continue;
					Bag bag = (Bag) pcs[i].getPlayer().getExtPlayerInfo("bag");
					if(bag.getExtGoods(3) != null)
					{
						inviter.sendAlert(ErrorCode.ALERT_PARTY_CDTIMING);
						return;
					}
				}
			}
			
			battle = createPartyPK(fightName);
		}
		else if(isGoldPartyPKRoom)
		{
			Bag bag = (Bag)inviter.getPlayer().getExtPlayerInfo("bag");
			if(bag.getExtGoods(3) != null)
			{
				GoodsProp prop = (GoodsProp) bag.getExtGoods(3);
				if(prop.expTimes == 10*1000)
					inviter.sendAlert(ErrorCode.ALERT_WINNER_IS_STOP);
				else
					inviter.sendAlert(ErrorCode.ALERT_XURUO_ISNO_BATTLE);
				return;
			}
			bag = (Bag)target.getPlayer().getExtPlayerInfo("bag");
			if(bag.getExtGoods(3) != null)
			{
				GoodsProp prop = (GoodsProp) bag.getExtGoods(3);
				if(prop.expTimes == 10*1000)
					inviter.sendAlert(ErrorCode.ALERT_TARGET_XIUXI_ISNO_BATTLE);
				else
					inviter.sendAlert(ErrorCode.ALERT_TARGET_XURUO_ISNO_BATTLE);
				return;
			}
			
			battle = createGoldPVP(fightName);
		}
		else
		{
			battle = createPVP(fightName);
		}
		
		PlayerController [] sources = null;
		PlayerController [] players = null;
		
		TeamController team = inviter.getTeam();
		if(team==null)
		{
			sources = new PlayerController[1];
			sources[0] = inviter;
		}
		else
		{
			sources = team.getPlayers();
		}
		team = target.getTeam();
		if(team==null)
		{
			players = new PlayerController[1];
			players[0] = target;
		}
		else
		{
			players = team.getPlayers();
		}
		battle.setFighters(sources, players);
		battleList.add(battle);
		
		ByteBuffer buffer = new ByteBuffer(64);
		battle.writeTo(buffer);
		battle.dispatchMsg(SMsg.S_ROOM_PVP_COMMAND, buffer);
		
		for (int i = 0; i < players.length; i++) 
		{
			if(players[i] == null)
				continue;
			battle.dispatchIsBattleWithoutRoom(true,players[i]);
		}
		for (int i = 0; i < sources.length; i++) 
		{
			if(sources[i] == null)
				continue;
			battle.dispatchIsBattleWithoutRoom(true,sources[i]);
		}
		
		battle.haloStart(sources, battle);//启动光环
		battle.haloStart(players, battle);//启动光环
	}

	private BattleController createPVE(String fightName)
	{
		BattleController battle = new PVEController();
		battle.id = fightName.hashCode();
		battle.name = fightName;
		battle.setParent(this);
		return battle;
	}
	
	private BattleController createPVP(String fightName)
	{
		BattleController battle = new PVPController();
		battle.id = fightName.hashCode();
		battle.name = fightName;
		battle.setParent(this);
		return battle;
	}
	
	private BattleController createGoldPVP(String fightName)
	{
		BattleController battle = new GoldPVPController();
		battle.id = fightName.hashCode();
		battle.name = fightName;
		battle.setParent(this);
		return battle;
	}
	
	
	private BattleController createPartyPK(String fightName)
	{
		BattleController battle = new PartyPKController();
		battle.id = fightName.hashCode();
		battle.name = fightName;
		battle.setParent(this);
		return battle;
	}
	
	private BattleController createCopyPVE(String fightName)
	{
		BattleController battle = new CopyPVEController();
		battle.id = fightName.hashCode();
		battle.name = fightName;
		battle.setParent(this);
		return battle;
	}
	
	private BattleController createGoldPVE(String fightName)
	{
		BattleController battle = new GoldPVEController();
		battle.id = fightName.hashCode();
		battle.name = fightName;
		battle.setParent(this);
		return battle;
	}
	
	private BattleController createEventPVE(String fightName)
	{
		BattleController battle = new EventPVEController();
		battle.id = fightName.hashCode();
		battle.name = fightName;
		battle.setParent(this);
		return battle;
	}
	
	
	public void ExitLinking()
	{
		for (int i = 0; i < exits.length; i++)
		{
			if(exits[i] == null)
				continue;

			AreaController areaTarget = worldManager.getAreaById(exits[i].targetArea);
			
			if(areaTarget == null)
			{
				MainFrame.println("Exit : no exit area "+exits[i].targetArea);
				continue;
			}
			RoomController roomTarget = areaTarget.getRoomById(exits[i].targetRoomId);
			
			if(roomTarget == null)
			{
				MainFrame.println("Exit : no exit room "+exits[i].targetRoomId+" by "+exits[i].targetArea);
				continue;
			}
			
			exits[i].targetRC = roomTarget;
		}
	}
	
	/**
	 * 把NPC添加到房间中
	 * @param npc
	 */
	public void addNpcList(NpcController npc)
	{
		NpcController[] npcs = new NpcController[npcList.length+1];
		for (int i = 0; i < npcList.length; i++)
		{
			npcs[i] = npcList[i];
		}
		npcs[npcList.length] = npc;
		npcList = npcs;
	}
	
	/**
	 * 把monsterGroup添加到房间中
	 * @param monsterGroup
	 */
	public void addMonsterGroupList(MonsterGroupController monsterGroup)
	{
		MonsterGroupController[] nMonsterGroup = new MonsterGroupController[monsterGroups.length+1];
		for (int i = 0; i < monsterGroups.length; i++)
		{
			nMonsterGroup[i] = monsterGroups[i];
		}
		nMonsterGroup[monsterGroups.length] = monsterGroup;
		monsterGroups = nMonsterGroup;
	}
	
	
	
	/**
	 * pvp邀请(不需要对方同意的邀请)
	 * @param target
	 * @param msg
	 */
	private void PvPInvitation(PlayerController source, AppMessage msg)
	{
		if(isCopyPartyRoom)
		{
			return;
		}
		
		
		int playerId = msg.getBuffer().readInt();
		
		if (source.getTeam() != null)
		{
			if(!source.getTeam().isLeader(source))
			{
				source.sendAlert(ErrorCode.ALERT_PLAYER_NOT_LEADER);
				return;
			}
			for (int i = 0; i < source.getTeam().getPlayers().length; i++)
			{
				if(source.getTeam().getPlayers()[i].getPlayer().hitPoint <= 0)
				{
					source.sendAlert(ErrorCode.ALERT_OHTER_PLAYER_NOT_LIFE);
					return;
				}
			}
		}
		else
		{
			if(source.getPlayer().hitPoint <= 0)
			{
				source.sendAlert(ErrorCode.ALERT_PLAYER_NOT_LIFE);
				return;
			}
		}
		
		
		PlayerController target = getPlayer(playerId);
		if(target == null || source.getParent() != this)
		{
			source.sendAlert(ErrorCode.ALERT_PLAYER_NOTAT_ROOM);
			return;
		}
		if(source.getID() == target.getID())
			return;
		if(target.isAuto)
		{
			source.sendAlert(ErrorCode.ALERT_TARGET_PLAYER_AUTO_BATTLEING);
			return;
		}
		if(source.getTeam() != null)
		{
			if(source.getTeam().isTeamPlayer(target))
			{
				source.sendAlert(ErrorCode.ALERT_SAME_TEAM_PLAYER);
				return;
			}
		}
		if(source.checkPlayerState())
			return;
		if(source.isChoose(source,0) || source.isChoose(target,0))
			return;
		if(isPartyPKRoom)
		{
			if(!FamilyPartyController.getInstance().isStarted())
			{
				source.sendAlert(ErrorCode.ALERT_PARTY_NO_START);
				return;
			}
			Bag bag = (Bag) source.getPlayer().getExtPlayerInfo("bag");
			if(bag.getExtGoods(3) != null)
			{
				source.sendAlert(ErrorCode.ALERT_NOPK_STATE);
				return;
			}
			if(FamilyPartyController.getInstance().isStarted())
			{
				if(source.getFamily() == null || target.getFamily() == null)
					return;
	
				if(source.getFamily().id == target.getFamily().id)
				{
					//同家族的不能PK
					source.sendAlert(ErrorCode.ALERT_PLAYER_SAME_FAMILY);
					return;
				}
			}
		}
		else if(isPartyRoom)
		{
			source.sendAlert(ErrorCode.ALERT_REST_ROOM_NOPK);
			return;
		}
		else if(isGoldPartyRoom)
		{
			source.sendAlert(ErrorCode.ALERT_REST_ROOM_NOPK);
			return;
		}

		if(!pkAble)	
		{
			source.sendAlert(ErrorCode.ALERT_ROOM_ISNOT_BATTLE);
			return;
		}
		if(source.getParent() instanceof BattleController || target.getParent() instanceof BattleController)
		{
			source.sendAlert(ErrorCode.ALERT_PLAYER_IS_BATTLING);
			return;
		}	
		if(target.getTeam() != null)
		{
			for (int i = 0; i < target.getTeam().getPlayers().length; i++)
			{
				if(target.getTeam().getPlayers()[i].getPlayer().hitPoint <= 0)
				{
					source.sendAlert(ErrorCode.ALERT_PLAYER_NOT_LIFE);
					return;
				}
			}
		}
		else
		{
			if(target.getPlayer().hitPoint <= 0)
			{
				source.sendAlert(ErrorCode.ALERT_PLAYER_NOT_LIFE);
				return;
			}
		}
			
		playerHitPlayer(source,target);	
	}

	/**
	 * pve邀请 战斗要去
	 * 
	 */
	public void PvEInvitation(PlayerController target, AppMessage msg,boolean flag)
	{
		BuffBox buffBox = (BuffBox)target.getPlayer().getExtPlayerInfo("buffBox");

		if(buffBox.pveCDtime > 0)
			return;
		
		MoneyBattle mb = DataFactory.getInstance().getMoneyBattle(id);
		if(mb != null)
		{
			if(target.getTeam() != null)
			{
				target.sendAlert(ErrorCode.ALERT_PARTY_ISNOT_ACCEPT_TEAM);
				if(target.isAuto)
					target.setAuto(false, 0, null);
				return;
			}
			else
			{
				Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
				if(bag.getNullCount() < 2)
				{
					target.sendAlert(ErrorCode.ALERT_BAG_NULLCOUNT_ERROR);
					if(target.isAuto)
						target.setAuto(false, 0, null);
					return;
				}
				if(!mb.isConditionEnough(target))
				{
					if(target.isAuto)
						target.setAuto(false, 0, null);
					return;
				}
			}
		}
		
		if(isCopyPartyRoom)
		{
			if(WorldManager.isZeroMorning(0))
			{
				target.sendAlert(ErrorCode.ALERT_SYSTEM_IS_UPDATE);
				return;
			}
		}
		if(isGoldPartyPKRoom || isGoldPartyRoom)
		{
			Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
			if(bag.getExtGoods(3) != null)
			{
				GoodsProp prop = (GoodsProp) bag.getExtGoods(3);
				if(prop.expTimes == 10*1000)
					target.sendAlert(ErrorCode.ALERT_WINNER_IS_STOP);
				else
					target.sendAlert(ErrorCode.ALERT_XURUO_ISNO_BATTLE);
				return;
			}
			int battleCount = GoldPartyController.getInstance().getBattleCount(target);
			if(battleCount >= GoldPartyController.BATTLECOUNT)
			{
				target.sendAlert(ErrorCode.ALERT_BATTLE_COUNT_IS_MAX);
				return;
			}
		}

		ByteBuffer newBuffer = new ByteBuffer(msg.getBuffer().getBytes());
		long groupIndex = 0;
		String strIndex = "";
		int x,y;
		try{
			if(!flag)
			{
				strIndex = msg.getBuffer().readUTF();
				x = msg.getBuffer().readInt();
				y = msg.getBuffer().readInt();
			}
			else
			{
				if(DataFactory.isBattleCode)
				{
					ByteBuffer buffer = Code.byteDecrypt(msg.getBuffer());
					strIndex = buffer.readUTF();
					x = buffer.readInt();
					y = buffer.readInt();
				}
				else
				{
					strIndex = msg.getBuffer().readUTF();
					x = msg.getBuffer().readInt();
					y = msg.getBuffer().readInt();
				}
			}
		}catch(Exception e)
		{
//			System.out.println("RoomController PvEInvitation error newBufferLength:"+msg.getBuffer().available());
			System.out.println("RoomController PvEInvitation error newBufferLength:"+newBuffer.available());
			return;
		}
		try{
			groupIndex = Long.parseLong(strIndex);
		}catch(Exception e)
		{
			System.out.println("RoomController PvEInvitation error strIndex:"+strIndex+"  room:"+name+" player:"+target.getName()+" newBufferLength:"+newBuffer.available());
			return;
		}

		if(!fightAble)	
		{
			target.sendAlert(ErrorCode.ALERT_ROOM_ISNOT_BATTLE);
			return;
		}
		if(!target.isOnline())
		{
			target.sendAlert(ErrorCode.EXCEPTION_LOGIN_PLAYERNOTATGAME);
			target.close();
			return;
		}
	
		if(target.getPlayer().hitPoint <= 0)
		{	
			target.sendAlert(ErrorCode.ALERT_PLAYER_NOT_LIFE);
			return;
		}
		
		if (target.getTeam() != null)
		{
			if(!target.getTeam().isLeader(target))
			{
				target.sendAlert(ErrorCode.ALERT_PLAYER_NOT_LEADER);
				return;
			}
			for (int i = 0; i < target.getTeam().getPlayers().length; i++)
			{
				if(target.getTeam().getPlayers()[i].getPlayer().hitPoint <= 0)
				{
					target.sendAlert(ErrorCode.ALERT_OHTER_PLAYER_NOT_LIFE);
					return;
				}
			}
		}
		if(target.getParent() != this)
		{
			target.sendAlert(ErrorCode.ALERT_PLAYER_NOTAT_ROOM);
			return;
		}
		if(target.getParent() instanceof BattleController)
		{
			target.sendAlert(ErrorCode.ALERT_PLAYER_IS_BATTLING);
			return;
		}
		
		MonsterGroupController mgc = getMonsterGroupByIndex(groupIndex);
		
		if(mgc == null || mgc.getParent() != this)
		{
			return;
		}
		if(mgc.isFight())
		{
			return;
		}
		if(x != mgc.x || y != mgc.y)
		{
			System.out.println("location error~~~~"+target.getName());
			System.out.println("x:"+x+"  y:"+y+"  monsterX:"+mgc.x+"   monsterY:"+mgc.y);
			return;
		}
		if(target.checkPlayerState())
			return;
		if(target.isChoose(target,0))
			return;
		
		if(mgc.isBodyType() && mgc.rebirthTime > 30 * 60 * 1000)
			return;

		playerHitMonster(target, mgc);
	}

	private void moveToNextRoom(PlayerController target,AppMessage msg)
	{
		NetConnection conn = target.getNetConnection();
		long time = conn.getPingTime();
		
		if(time < 2000 + target.currentTimeMsg)
		{
			//target.sendAlert(ErrorCode.ALERT_ROOM_FIVE_CHANGE);
			return;
		}
		target.currentTimeMsg = time;

		if(target.checkPlayerState())
			return;
		if(target.checkTeamState())
			return;
		
		if(target.getTeam() != null)
		{
			TeamController team = target.getTeam();
			
			if(team.getLeader() == null)
				return;
			
			if(!team.isLeader(target))
				return;
			
			PlayerController []everyone = team.getPlayers();
			for (int i = 0; i < everyone.length; i++)
			{
				if(everyone[i] == null)
					continue;
				
				if(everyone[i].getPlayer().eventPoint < eventPoint)
				{
					target.sendAlert(ErrorCode.ALERT_TEAMER_EVENTPOINT_CANNOT);
					return;
				}
			}
			
			for (int i = 0; i < everyone.length; i++)
			{
				if(everyone[i] == null)
					continue;
				
				everyone[i].getPlayer().eventPoint = 0;
			}	
		}
		else
		{
			
			if(target.getPlayer().eventPoint < eventPoint)
			{
				target.sendAlert(ErrorCode.ALERT_PLAYER_EVENTPOINT_CANNOT);
				return;
			}
			target.getPlayer().eventPoint = 0;
		}

		int exitIndex = msg.getBuffer().readByte();
		
		if(exitIndex > 7 || exitIndex < 0)
			return;
		
		if(exits[exitIndex] == null)
			return;
		RoomController roomTarget = exits[exitIndex].targetRC;

		if(roomTarget == null)
		{
			//target.sendError("该出入口没有房间");
			return;
		}
		
		
		FamilyPartyController fpc = FamilyPartyController.getInstance();

		if(roomTarget.isPartyPKRoom || roomTarget.isPartyRoom)
		{
			if(fpc.isEnded())
			{
				target.sendAlert(ErrorCode.ALERT_PARTY_NO_START);
				return;
			}
			if(roomTarget.isPartyPKRoom)
			{
				if(target.getTeam() == null)
				{
					Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
					if(bag.getExtGoods(3) != null)
					{
						target.sendAlert(ErrorCode.ALERT_NOPK_STATE);
						return;
					}
				}
				else
				{
					PlayerController[] pcs = target.getTeam().getPlayers();
					for (int i = 0; i < pcs.length; i++)
					{
						if(pcs[i] == null || !pcs[i].isOnline())
							continue;
						Bag bag = (Bag) pcs[i].getPlayer().getExtPlayerInfo("bag");
						if(bag.getExtGoods(3) != null)
						{
							target.sendAlert(ErrorCode.ALERT_NOPK_STATE);
							return;
						}
					}
				}
			}
		}
		else if(roomTarget.isGoldPartyPKRoom || roomTarget.isGoldPartyRoom)
		{
			if(GoldPartyController.getInstance().isEnded())
			{
				target.sendAlert(ErrorCode.ALERT_PARTY_NO_START);
				return;
			}
			if(roomTarget.isGoldPartyPKRoom)
			{
				Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
				if(bag.getExtGoods(3) != null)
				{
					target.sendAlert(ErrorCode.ALERT_NOPK_STATE);
					return;
				}
			}
		}
		
		if(target.getTeam() == null)
		{
			target.setDefaultLocation();
			removePlayer(target);
			target.setRoomExit(exits[exitIndex]);
			target.setParent(null);
			roomTarget.sendInfo(target);
		}
		else
		{
			removeTeam(target.getTeam());
			
			PlayerController[] players = target.getTeam().getPlayers();
			for (int i = 0; i < players.length; i++)
			{
				if(roomTarget.getPlayer(players[i].getID()) == null && roomTarget.area.getPlayer(players[i].getID()) == null)
				{	
					roomTarget.getPlayerList().add(players[i]);
					roomTarget.area.addPlayer(players[i]);
				}
			}
			if(target.getTeam().isLeader(target))
			{
				for (int i = 0; i < players.length; i++) 
				{
					players[i].setDefaultLocation();
					removePlayer(players[i]);
					players[i].setRoomExit(exits[exitIndex]);
					players[i].setParent(null);
					roomTarget.sendInfo(players[i]);
				}
			}
		}
	}
	

	/**
	 * 在复活选择的时候，把玩家的房间ID设为目标房间ID，在复活选择完后又设置回去，这样是防止玩家到故意掉线或者F5再上线到原地
	 * @param target
	 */
	public void setParentPlayer(PlayerController target)
	{
		target.getPlayer().worldId = area.getParentId();
		target.getPlayer().areaId = area.id;
		target.getPlayer().roomId = id;
	}
	

	public void addPlayer(PlayerController target)
	{
		if(target.getParent() != null && target.getParent() instanceof RoomController)
		{
//System.out.println(id+"  .............  "+name);
//System.out.println(((RoomController)target.getParent()).id+" "+((RoomController)target.getParent()).name);		
			if(((RoomController)target.getParent()).id != this.id)
			{//防止直接发add消息
				target.close();
				return;
			}
		}
		
		int nativityIndex = 0;
		
		RoomExit exit = target.getRoomExit();
	   
		if(exit == null)
		{
			nativityIndex = DEFUALT_NATIVITY;
		}
		else
		{
			nativityIndex = exit.targetPosittion;
			target.setRoomExit(null);
		}
		
		target.sendAlwaysValue();

		target.getPlayer().setXYByCount(this, 3);
		
		ByteBuffer buffer = new ByteBuffer(64);

		buffer.writeBoolean(true);
		target.getPlayer().writeTo(buffer);
		buffer.writeByte(isPartyPKRoom||isGoldPartyPKRoom?DEFUALT_NATIVITY:nativityIndex);
		buffer.writeBoolean(!target.isTeamMember());//是否是队长(单人的算是队长)
		if(target.getTeam() != null)
		{
			target.getTeam().dispatchMsg(SMsg.S_ROOM_IO_COMMAND,buffer);
		}
		else
		{
			target.getNetConnection().sendMessage(new SMsg(SMsg.S_ROOM_IO_COMMAND,buffer));
		}
		
		dispatchMsg(SMsg.S_ROOM_IO_COMMAND,buffer);
		
		PlayerController tempPlayer =  getPlayer(target.getID());
		if(tempPlayer != null)
		{
			super.removePlayer(tempPlayer);
			area.removePlayer(tempPlayer);
		}
		
		super.addPlayer(target);
		area.addPlayer(target);
		
		target.getPlayer().worldId = area.getParentId();
		target.getPlayer().areaId = area.id;
		target.getPlayer().roomId = id;
		target.setParent(this);
		target.setRoom(this);
		
		target.sendAlwaysValue();
		
		buffer = new ByteBuffer(15);
		buffer.writeInt(target.getPlayer().worldId);
		buffer.writeInt(area.id);
		buffer.writeInt(id);
		buffer.writeByte(isPartyPKRoom||isGoldPartyPKRoom?DEFUALT_NATIVITY:nativityIndex);
		buffer.writeInt(target.getPlayer().x);
		buffer.writeInt(target.getPlayer().y);
		AppMessage msg = new AppMessage(SMsg.S_ROOM_ADD_COMMAND,buffer);
		target.getNetConnection().sendMessage(msg);
		
		PetTome pt = (PetTome) target.getPlayer().getExtPlayerInfo("petTome");
		Pet pet = pt.getActivePet();
		if(pet != null)
		{
			if(pet.isStroll)
				target.sendPetModel(1,pet.modelId,pet.name);
			pet.trainOver(target,1);
			
			target.sendPetInfo(target,target.getID());
		}

		if(target.getTeam() != null && target.getTeam().isLeader(target))
			addTeam(target.getTeam());
		
		target.sendActivePoint();
		
		if(isGoldPartyPKRoom || isGoldPartyRoom)
			GoldPartyController.getInstance().sendBattleCount(target);
		
		target.checkGuideStep();
		target.sendCopyPoint();
	}
	
	public void removePlayer(PlayerController target)
	{
		super.removePlayer(target);
		area.removePlayer(target);
		
		ByteBuffer buffer = new ByteBuffer(8);
		buffer.writeInt(area.id);
		buffer.writeInt(id);
		AppMessage msg = new AppMessage(SMsg.S_ROOM_REMOVE_COMMAND,buffer);
		NetConnection conn = target.getNetConnection();
		if(conn != null)
			conn.sendMessage(msg);
		
		buffer = new ByteBuffer(5);
		buffer.writeBoolean(false);
		buffer.writeInt(target.getID());
		dispatchMsg(SMsg.S_ROOM_IO_COMMAND,buffer);
		
		target.isLookTeam = false;
		
//		flushRoomTeamInfo();
	}
	
	
	public MonsterGroupController getMonsterGroupById(int groupId)
	{
		for(int i = monsterGroups.length-1 ; i >= 0 ; i --)
		{
			if(monsterGroups[i].id == groupId)
				return monsterGroups[i];
		}
		return null;
	}
	
	public MonsterGroupController getMonsterGroupByIndex(long objectIndex)
	{
		for(int i = monsterGroups.length-1 ; i >= 0 ; i --)
		{
			if(monsterGroups[i].objectIndex == objectIndex)
				return monsterGroups[i];
		}
		return null;
	}
	
	public void setCopy(CopyController copy)
	{
		this.copy = copy;
	}
	
	public CopyController getCopy()
	{
		return this.copy;
	}
	
	public void setWorld(WorldManager worldManager)
	{
		this.worldManager = worldManager;
	}
	
	public void setParent(AreaController area)
	{
		this.area = area;
	}
	
	public AreaController getParent()
	{
		return area;
	}

	public void setExits(RoomExit[] exits)
	{
		this.exits = exits;
	}

	public RoomExit[] getExits()
	{
		return exits;
	}
	
	public NpcController[] getNpcList()
	{
		return npcList;
	}
	
	public ArrayList getMonstersNode()
	{
		return monstersNode;
	}

	public MonsterGroupController[] getMonsterGroups()
	{
		return monsterGroups;
	}
	
	public List getPlayerList()
	{
		return playerList;
	}

	public String toString()
	{
		return " player "+playerList.size()+
		" npc : "+
		npcList.length+
		" monster : "+
		monsterGroups.length;
	}
	
	public NpcController getNpc(int npcId)
	{
		int length = npcList.length;
		for(int i = 0 ; i < length ; i ++)
		{
			if(npcList[i].getID() == npcId)
				return npcList[i];
		}
		return null;
	}
	
	public boolean isFightAble()
	{
		return fightAble;
	}
	
	public boolean isPkAble()
	{
		return pkAble;
	}
	
	public boolean isPartyRoom()
	{
		return isPartyRoom;
	}
	
	public boolean isPartyPKRoom()
	{
		return isPartyPKRoom;
	}
	
	public int getEventPoint()
	{
		return eventPoint;
	}

	public void setEventPoint(int eventPoint)
	{
		this.eventPoint = eventPoint;
	}

	public void sendCanTasks(PlayerController target)
	{
		ByteBuffer buffer = new ByteBuffer(32);
		buffer.writeInt(npcList.length);
		for (int i = 0; i < npcList.length; i++)
		{
			buffer.writeInt(npcList[i].getID());
			npcList[i].anyTask(target,buffer);
		}
		target.getNetConnection().sendMessage
		(new SMsg(SMsg.S_ROOM_GET_ROOM_TASKS,buffer));
		
//		PetTome pt = (PetTome) target.getPlayer().getExtPlayerInfo("petTome");
//		Pet pet = pt.getActivePet();
//		if(pet == null)
//			return;
//
//		pet.trainOver(target,1);
		
		Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
		if(bag.isGift())
		{
			target.isGift = true;
			buffer = new ByteBuffer();
			target.getNetConnection().sendMessage(new SMsg(SMsg.S_NEW_PLAYER_GIFTGOODS_COMMAND,buffer));
		}
		
		target.checkChoose();
	}
	
	/**
	 * 传入阵营值，返回当前场景传回的重生点ID
	 * @param i
	 * @return
	 */
	public int getRebirthId(int camp)
	{
		return rebirthId[camp];
	}
	
	public void sendPlayerMove(RoomController targetRoom,PlayerController[] players)
	{
		
		int length = players.length;
		for (int i = 0; i < length; i++) 
		{
			if(players[i] == null || !players[i].isOnline())
				continue;
			
			players[i].setDefaultLocation();
			
			players[i].setParent(null);
			
			removePlayer(players[i]);
		}
		for (int i = 0; i < length; i++)
		{
			if(players[i] == null || !players[i].isOnline())
				continue;
			targetRoom.sendInfo(players[i]);
		}
		for (int i = 0; i < length; i++)
		{
			if(players[i] == null || !players[i].isOnline())
				continue;
			targetRoom.addPlayer(players[i]);
		}
		if(!targetRoom.isStoryRoom)
		{
			for (int i = 0; i < length; i++)
			{
				if(players[i] == null || !players[i].isOnline())
					continue;
				if(players[i].isStory)
					players[i].isStory = false;
			}
		}
	}
	
	/**
	 * 判断买东西时是否在正确房间
	 * @param shopId
	 * @return
	 */
	public boolean isNpcInRoom(int shopId)
	{
		for (int i = 0; i < npcList.length; i++) 
		{
			if(npcList[i] != null)
			{	
				String[] strs = Utils.split(npcList[i].getNpc().shopId, ":");
				for (int j = 0; j < strs.length; j++) 
				{
					int id = Integer.parseInt(strs[j]);
					if(id == shopId)
						return true;
				}
			}
		}
		return false;
	}
	
	public List getTeamList()
	{
		return teamList;
	}
	
	public void addTeam(TeamController team)
	{
		if(isCopyPartyRoom)
		{
			if(copy != null && !copy.isTeam)
				return;
		}
		if(isGoldPartyRoom || isGoldPartyPKRoom)
			return;
		
		if(getTeamById(team.id) == null)
		{
			teamList.add(team);
			
//			flushRoomTeamInfo();
		}
	}
	
	/**
	 * 调用玩家离开组的方法之前
	 * 玩家离开组的时候删除队伍，要检测玩家离开后队伍是否只有一人了，这时才删除
	 * @param team
	 */
	public void deleteTeam(TeamController team)
	{
		if(team.getPlayerCount() == 2)
			removeTeam(team);
	}
	
	public void removeTeam(TeamController team)
	{
		if(isCopyPartyRoom)
		{	
			if(copy != null && !copy.isTeam)
				return;
		}
		
		for (int i = 0; i < team.getPlayerCount(); i++) 
		{
			PlayerController player = team.getPlayers()[i];
			if(player == null || !player.isOnline())
				continue;
			player.isLookTeam = false;
		}
		
		teamList.remove(team);
		
//		flushRoomTeamInfo();
	}
	
	public TeamController getTeamById(int id)
	{
		for (int i = 0; i < teamList.size(); i++)
		{
			TeamController team = (TeamController)teamList.get(i);
			if(team.id == id)
				return team;
		}
		return null;
	}
	
	
	/**
	 * 给房间里所有正在查看队伍列表的玩家刷新队伍变化信息
	 */
	public void flushRoomTeamInfo()
	{
//		for (int i = 0; i < playerList.size(); i++) 
//		{
//			PlayerController player = (PlayerController) playerList.get(i);
//			if(!player.isOnline())
//				continue;
//			if(!player.isLookTeam)
//				continue;
//			sendRoomTeamInfo(player,1);
//		}
	}
	
	
	/**
	 * all teams
	 * type 0:close  1:sendTeamList
	 */
	public void sendRoomTeamInfo(PlayerController target,int type)
	{
		if(isCopyPartyRoom)
		{
			if(copy != null && !copy.isTeam)
				return;
		}
		if(isGoldPartyRoom || isGoldPartyPKRoom)
			return;
		
		if(type == 0)
		{
			target.isLookTeam = false;
			return;
		}
		
		if(target.getTeam() != null && target.getTeam().isLeader(target))
		{
			target.getTeam().sendApplyList(target);
		}
		else
		{
			ByteBuffer buffer = new ByteBuffer();
			buffer.writeByte(type);
			int size = 0,i = 0;
			for (i = 0; i < teamList.size(); i++) 
			{	
				TeamController team = (TeamController) teamList.get(i);
				int len = team.getPlayerList().size();
				if(team == null || len > 4)
					continue;
				if(team.getLeader() == null)
				{
					removeTeam(team);
					continue;
				}
				size++;
			}
			buffer.writeUTF(target.getTeam()==null?"":target.getTeam().name);
			buffer.writeByte(target.teamState);
			
			buffer.writeInt(size);
			for (i = 0; i < teamList.size(); i++) 
			{	
				TeamController team = (TeamController) teamList.get(i);
				int len = team.getPlayerList().size();
				if(team == null || len > 4)
					continue;
				if(team.getLeader() == null)
				{
					removeTeam(team);
					continue;
				}
				buffer.writeInt(team.id);
				buffer.writeUTF(team.name);
				buffer.writeUTF(team.getLeader().getName());
				buffer.writeByte(len);
				buffer.writeInt(team.getLeader().getPlayer().level);
			}
			
			size = 0;
			for (i = 0; i < playerList.size(); i++) 
			{
				PlayerController player = (PlayerController) playerList.get(i);
				if(player.getTeam() != null)
					continue;
				if(player.getID() == target.getID())
					continue;
				size++;
			}
			buffer.writeInt(size);
			for (i = 0; i < playerList.size(); i++) 
			{
				PlayerController player = (PlayerController) playerList.get(i);
				if(player.getTeam() != null)
					continue;
				if(player.getID() == target.getID())
					continue;
				buffer.writeInt(player.getID());
				buffer.writeInt(player.getPlayer().level);
				buffer.writeUTF(player.getName());
				buffer.writeByte(player.getPlayer().upProfession);
				buffer.writeByte(player.teamState);
			}
			
			target.getNetConnection().sendMessage(new SMsg(SMsg.S_ROOM_GET_TEAMS,buffer));
		}
		
		target.isLookTeam = true;
	}
	
	/**
	 * 查看某个队伍的详细信息
	 * @param target
	 * @param inBuffer
	 */
	public void sendTeamMemberInfo(PlayerController target,ByteBuffer inBuffer)
	{
		int teamId = inBuffer.readInt();
		TeamController team = getTeamById(teamId);
		if(team == null)
			return;
		ByteBuffer buffer = new ByteBuffer();
		int size = team.getPlayerCount();
		buffer.writeInt(team.id);
		buffer.writeUTF(team.name);
		
		buffer.writeByte(size);
		for (int i = 0; i < size; i++) 
		{
			PlayerController player = team.getPlayers()[i];
			buffer.writeInt(player.getPlayer().level);
			buffer.writeUTF(player.getName());
			buffer.writeByte(player.getPlayer().upProfession);
			buffer.writeByte(player.teamState);
			buffer.writeBoolean(team.isLeader(player));
		}
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_ROOM_TEAM_MEMBER_INFO,buffer));
	}
	
	
//	public void sendRoomMonsters(PlayerController target)
//	{
//		ByteBuffer buffer = new ByteBuffer();
//		buffer.writeByte(monsterGroups.length);
//		for (int i = 0; i < monsterGroups.length; i++) 
//		{
//			buffer.writeInt(monsterGroups[i].id);
//			buffer.writeUTF(monsterGroups[i].objectIndex+"");
//			buffer.writeUTF(monsterGroups[i].name);
//			buffer.writeByte(monsterGroups[i].monsterControllers.length);
//			for (int j = 0; j < monsterGroups[i].monsterControllers.length; j++) 
//			{
//				MonsterController mc = monsterGroups[i].monsterControllers[j];
//				buffer.writeInt(mc.getMonster().level);
//				buffer.writeUTF(mc.getName());
//			}
//		}
//		target.getNetConnection().sendMessage(new SMsg(SMsg.S_ROOM_MONSTERS_COMMAND,buffer));
//	}
	
	/**
	 * 发送需要显示的玩家的ID(暂时只在家族活动里用)
	 */
	private void displayPlayer(PlayerController target)
	{
		if(!isPartyPKRoom && !isPartyRoom && !isGoldPartyPKRoom && !isGoldPartyRoom)
			return;
		List list = new ArrayList();
		for (int i = 0; i < playerList.size(); i++)
		{
			if(playerList.get(i) == null)
				continue;
			PlayerController player = (PlayerController) playerList.get(i);
			if(!player.isOnline())
				continue;
			if(player.getTeam() == null)
			{
				if(player.getID() == target.getID())
					continue;
				list.add(player.getID());
//System.out.println(player.getName());
			}
			else
			{
				if(player.getTeam().isTeamPlayer(target) && player.getID() != target.getID())
					continue;
				if(player.getID() == target.getID())
				{
					PlayerController[] pcs = player.getTeam().getPlayers();
					for (int j = 0; j < pcs.length; j++) 
					{
						if(pcs[j].getID() == target.getID())
							continue;
						if(pcs[j].getTeam().isLeader(pcs[j]))
							list.add(pcs[j].getID());
					}
				}
				else if(player.getTeam().isLeader(player))
					list.add(player.getID());
			}
		}
		
		ByteBuffer buffer = new ByteBuffer();
		int size = list.size();
		buffer.writeInt(size);
		for (int i = 0; i < size; i++) 
		{
//			System.out.println("....."+list.get(i).toString());
			buffer.writeInt(Integer.parseInt(list.get(i).toString()));
		}
//		System.out.println("自己的ID："+target.getID());
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_ROOM_DISPLAY_PLAYER_COMMAND,buffer));
	}
	
	/**
	 * 是否要显示GO
	 * @return
	 */
	public boolean isVisiable()
	{//試煉之地 時空旅行 黃金鬥士 城鎮 奇妙廣場 熱斗場 荣誉圣殿
		if(id == DataFactory.MARRYROOM || id == DataFactory.INITROOM || id == DataFactory.STORYROOM
				|| isCopyPartyRoom || isGoldPartyPKRoom || isGoldPartyRoom
				|| isPartyPKRoom || isPartyRoom || id == DataFactory.HONORROOM)
			return false;
		MoneyBattle mb = DataFactory.getInstance().getMoneyBattle(id);
		if(mb != null)
			return false;
		return true;
	}
	
	
	private void sendMonsterFlushTime(PlayerController target)
	{
		AreaController[] acs = worldManager.getAreaControllers();
		if(acs == null)
			return;
		ByteBuffer buffer = new ByteBuffer();
		for (int i = 0; i < acs.length; i++)
		{
			if(acs[i] == null)
				continue;
			RoomController[] rcs = acs[i].getRooms();
			if(rcs == null)
				continue;
			for (int j = 0; j < rcs.length; j++) 
			{
				if(rcs[j] == null)
					continue;
				MonsterGroupController[] mgcs = rcs[j].getMonsterGroups();
				if(mgcs == null)
					continue;
				for (int k = 0; k < mgcs.length; k++) 
				{
					if(mgcs[k] == null)
						continue;
					if(mgcs[k].rebirthTime > 5 * 60 * 1000)
					{
						buffer.writeInt(rcs[j].id);
						if(mgcs[k].vTimer == 0)
							buffer.writeInt(0);
						else
							buffer.writeInt((int) (mgcs[k].vTimer-WorldManager.currentTime));
					}
				}
			}
		}
		
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_ROOM_MONSTER_FLUSH_TIME_COMMAND,buffer));
	}
	

    /** 与客户端通信 */
	public void clientMessageChain(PlayerController target, AppMessage msg)
	{
		int type = msg.getType();

		if(type == SMsg.C_ROOM_INFO_COMMAND)
		{
			if(target.getParent() == null || target.getParent() instanceof RoomController)
			{
				if(!DataFactory.getInstance().checkInRoom(target,id))
				{
					return;
				}
				sendInfo(target);
			}
		}
		else if(type == SMsg.C_ROOM_ADD_COMMAND)
		{
			if(target.getParent() == null || target.getParent() instanceof RoomController)
			{
				if(!DataFactory.getInstance().checkInRoom(target,id))
				{
					return;
				}
				addPlayer(target);
			}
		}
/*		else if(type == SMsg.C_ROOM_REMOVE_COMMAND)
		{
			removePlayer(target);
		}*/
		else if(type == SMsg.C_ROOM_EXIT_COMMAND)
		{
			ByteBuffer buffer = new ByteBuffer(msg.getBuffer().getBytes());
			int exitIndex = buffer.readByte();
			if(exitIndex > 7 || exitIndex < 0)
				return;
			if(exits[exitIndex] == null)
				return;
			RoomController roomTarget = exits[exitIndex].targetRC;
			if(roomTarget == null)
				return;
			
			if(target.getParent() == null || target.getParent() instanceof RoomController)
			{	
				if(!DataFactory.getInstance().checkInRoom(target,roomTarget.id))
				{
					return;
				}
				moveToNextRoom(target,msg);
			}
		}
		else if(type == SMsg.C_ROOM_PVE_COMMAND)
		{
			PvEInvitation(target,msg,DataFactory.isBattleCode);
		}
		else if(type == SMsg.C_ROOM_PLAYER_UPDATE_PLAYER)
		{
			target.sendAlwaysValue();
		}
		else if(type == SMsg.C_ROOM_GET_ROOM_TASKS)
		{
			sendCanTasks(target);
		}
		else if(type == SMsg.C_ROOM_PVP_COMMAND)
		{
			PvPInvitation(target,msg);
		}
		else if(type == SMsg.C_ROOM_INITED)//客户端初始化房间完成
		{
			if(getEventPoint() != 0)
			{
				if(target.getPlayer().eventPoint >= getEventPoint() && isVisiable())
				{
					ByteBuffer buffer = new ByteBuffer(1);
					target.getNetConnection().sendMessage(new SMsg
							(SMsg.S_PLAYER_VISIABLE_COMMAND,buffer)); 
				}
			}
			
			target.initRoomId = id;
			
			target.sendAlwaysValue();
		}
		else if(type == SMsg.C_ROOM_GET_TEAMS)
		{
			sendRoomTeamInfo(target,msg.getBuffer().readByte());
		}
		else if(type == SMsg.C_ROOM_TEAM_MEMBER_INFO)
		{
			sendTeamMemberInfo(target,msg.getBuffer());
		}
		else if(type == SMsg.C_ROOM_MONSTER_FLUSH_TIME_COMMAND)
		{
			sendMonsterFlushTime(target);
		}
		else if(type == SMsg.C_ROOM_DISPLAY_PLAYER_COMMAND)
		{
			displayPlayer(target);
		}
	}
}
