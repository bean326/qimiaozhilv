package cc.lv1.rpg.gs.entity.controller;

import vin.rabbit.comm.GameObject;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.impl.ArrayList;
import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.RPGameObject;
import cc.lv1.rpg.gs.entity.container.PlayerContainer;
import cc.lv1.rpg.gs.entity.ext.Bag;
import cc.lv1.rpg.gs.entity.ext.BuffBox;
import cc.lv1.rpg.gs.entity.impl.MoneyBattle;
import cc.lv1.rpg.gs.entity.impl.Monster;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsProp;
import cc.lv1.rpg.gs.net.SMsg;
import cc.lv1.rpg.gs.other.ErrorCode;
import cc.lv1.rpg.gs.util.Code;

/**
 * 怪物组控制器
 * @author dxw
 *
 */
public class MonsterGroupController extends RPGameObject
{
	
	public static int publicObjectIndex = 0xFF;
	
	/** 怪物组容在什么地方 */
	private PlayerContainer playerContainer;
	
	private boolean isFight = false;
	
	public long vTimer = 0;
	
	/** 区域id */
	public int areaId;
	
	/** 房间id */
	public int roomId;
	
	/** 坐标x */
	public int x;
	
	/** 坐标y */
	public int y;
	
	public int minX,maxX,minY,maxY;

	/** 怪物组字符串(ID列表) */
	public String monsters;
	
	/** 重生时间 */
	public int rebirthTime;
	
	/** 选取攻击对象的时间间隔(毫秒) */
	public int searchDelay;
	
	/** 玩家需要什么道具才能与之战斗 */
	public int needGoodsId;
	
	/** 道具数量 */
	public int needGoodsCount;
	
	/** 怪物控制器 */
	public MonsterController monsterControllers [];
	
	/** 怪物名称组 */
	public String monsterNames;
	
	/** 是否开始计时 */
	public boolean isTime;
	
	/** BOSS间隔攻击玩家时间(部分特殊BOSS) */
	public int JIANGETIME = 10 * 1000;
	public int jiangeTime = JIANGETIME;
	
	/**
	 * 随机浮动2分钟时间
	 */
	private static final int MINUTES = 1000*60*2;
	
	public MonsterGroupController()
	{
		
	}
	
	public void copyTo(GameObject gameObject)
	{
		super.copyTo(gameObject);
		
		MonsterGroupController mc = (MonsterGroupController)gameObject;
		mc.monsters = monsters;
		mc.rebirthTime = rebirthTime;
		mc.searchDelay = searchDelay;
		mc.needGoodsId = needGoodsId;
		mc.needGoodsCount = needGoodsCount;
		mc.minX = minX;
		mc.maxX = maxX;
		mc.minY = minY;
		mc.maxY = maxY;
		mc.isTime = isTime;
		
		String [] monsterIds = Utils.split(monsters, ",");
		
		DataFactory df = DataFactory.getInstance();
		
		mc.monsterControllers = new MonsterController[monsterIds.length];

		for(int i = 0 ; i < monsterIds.length ; i++)
		{
			int monsterId = Integer.parseInt(monsterIds[i].trim());
			Monster monster = (Monster)df.getGameObject(monsterId);
			if(monster == null)
			{
				System.out.println("monsterGroup : "+id+" can not find "+monsterId+" entity");
				continue;
			}
			Monster nMonster = new Monster();
			monster.copyTo(nMonster);
			nMonster.areaId = mc.areaId;
			nMonster.roomId = mc.roomId;
			nMonster.x = mc.x;
			nMonster.y = mc.y;
			MonsterController nMonsterController = new MonsterController();
			nMonsterController.setMonster(nMonster);
			nMonsterController.setParent(playerContainer);
			mc.monsterControllers[i] = nMonsterController;
		}
	}
	
	public void writeTo(ByteBuffer buffer)
	{
		super.writeTo(buffer);
		buffer.writeInt(x);
		buffer.writeInt(y);
		buffer.writeInt(rebirthTime);
		buffer.writeInt(searchDelay);
		buffer.writeInt(needGoodsId);
		buffer.writeInt(needGoodsCount);
		
		int monsterLength = monsterControllers.length;
		buffer.writeByte(monsterLength);
		for(int i = 0 ; i < monsterLength ; i++)
		{
			monsterControllers[i].getMonster().writeTo(buffer);
		}
		
	}

	public void writeBaseTo(ByteBuffer buffer)
	{
		super.writeTo(buffer);
		int monsterId = Integer.parseInt(Utils.split(monsters, ",")[0]);
		Monster monster = (Monster) DataFactory.getInstance().getGameObject(monsterId);
		buffer.writeInt(monster.modelMotionId);
		buffer.writeInt(x);
		buffer.writeInt(y);
		buffer.writeBoolean(!isFight);
		buffer.writeBoolean(rebirthTime>0);
	}

	public void setParent(PlayerContainer playerContainer)
	{
		this.playerContainer = playerContainer;
	}
	
	public PlayerContainer getParent()
	{
		return playerContainer;
	}

	public boolean isFight()
	{
		return isFight;
	}

	public void setFight(boolean isFight)
	{
		this.isFight = isFight;
	}

	public MonsterController[] getMonstersInitial()
	{
		int length = monsterControllers.length;
		MonsterController [] mcs = new MonsterController[length];
		for(int i = 0 ; i < length ; i ++)
		{
			mcs[i] = new MonsterController();
			Monster monster = new Monster();
			monsterControllers[i].getMonster().copyTo(monster);
			mcs[i].setMonster(monster);
		}
		return mcs;
	}

	public void update(long timeMillis)
	{
		if(isFight && isTime)
		{
			if(timeMillis < vTimer)
				return;
		
			vTimer = 0;
			isFight = false;
			
			notifyVisible(false);
		}
		if(jiangeTime  > 0)
		{
			if(!isFight)
				jiangeTime -= 500;
		}
		else
		{
			if(!isFight && vTimer == 0 && isBodyType() && rebirthTime > 30 * 60 * 1000)
			{ 
				RoomController room = (RoomController)playerContainer;
				List list = room.getPlayerList();
				if(list == null || list.size() == 0)
					return;
				List tmp = new ArrayList();
				for (int i = 0; i < list.size(); i++) 
				{
					PlayerController player = (PlayerController) list.get(i);
					if(!player.isOnline())
						continue;
					if(player.getParent() instanceof BattleController)
						continue;
					if(player.initRoomId != room.id)
						continue;
					if(player.checkPlayerState())
						continue;
					if(player.isChoose(player,0))
						continue;
					if(player.getPlayer().hitPoint <= 0)
						continue;
					if(player.getPlayer().bossActivePoint <= 0)
						continue;
					if (player.getTeam() != null)
					{
						if(!player.getTeam().isLeader(player))
							continue;
						boolean isError = false;
						PlayerController[] pcs = player.getTeam().getPlayers();
						for (int j = 0; j < pcs.length; j++)
						{
							if(pcs[j].getPlayer().hitPoint <= 0 || pcs[j].initRoomId != room.id
									|| pcs[j].getPlayer().bossActivePoint <= 0 || !pcs[j].isOnline()
									|| pcs[j].checkPlayerState() || pcs[j].isChoose(pcs[j],0))
							{
								isError = true;
								break;
							}
						}
						if(isError)
							continue;
					}
					BuffBox buffBox = (BuffBox)player.getPlayer().getExtPlayerInfo("buffBox");
					if(buffBox.pveCDtime > 0)
					{
						buffBox.pveCDtime = 0;
					}
					tmp.add(player);
				}
				if(tmp.size() == 0)
					return;
				int random = (int) (Math.random() * tmp.size());
				PlayerController target = (PlayerController) tmp.get(random);
				if(target.initRoomId != room.id)
					return;
				room.monsterHitPlayer(this, target);
				
				jiangeTime = JIANGETIME;
			}
		}
	}
	public void setVisibled(){};
	/**
	 * 
	 * @param room
	 * @param type 1.隐藏,不计时 2.显示,不计时 3.隐藏,计时
	 */
	public void setVisibled(RoomController room,int type)
	{
		if(rebirthTime == 0)
			return;
		
		if(type == 1)//隐藏,不计时,hide,no time
		{
			isTime = false;
			isFight = true;
			notifyVisible(true);
		}
		else if(type == 2)//显示,不计时,display,no time
		{
			isTime = false;
			isFight = false;
			notifyVisible(false);
		}
		else if(type == 3)//隐藏,计时,hide,time
		{
			isTime = true;
			if(room.isGoldPartyRoom || room.isGoldPartyPKRoom)
			{
				vTimer = System.currentTimeMillis()+rebirthTime;
			}
			else
			{
				//修改时间为正负5分钟随机
				int temp = (int)(Math.random()*MINUTES);
				vTimer = System.currentTimeMillis()+rebirthTime+temp;
			}
			isFight = true;
			notifyVisible(true);
		}
	}
	
	
	public void notifyVisible(boolean isV)
	{
		RoomController room = (RoomController)playerContainer;
		ByteBuffer buffer = new ByteBuffer(10);
		buffer.writeUTF(objectIndex+"");
		buffer.writeBoolean(isV);
		if(!isV)
		{
			jiangeTime = JIANGETIME;
			setXYByCount(room,3);
			buffer.writeInt(x);
			buffer.writeInt(y);
		}
		room.dispatchMsg(SMsg.S_ROOM_VISIBLE_MG, buffer);
	}

	public boolean isBodyType()
	{
		for (int i = 0; i < monsterControllers.length; i++)
		{
			if(monsterControllers[i] == null)
				continue;
			if(monsterControllers[i].getMonster() == null)
				continue;
			if(monsterControllers[i].getMonster().bodyType == 1)
				return true;
		}
		return false;
	}
	
	public boolean isBoss()
	{
		for (int i = 0; i < monsterControllers.length; i++)
		{
			if(monsterControllers[i].getMonster().bodyType == 1 || 
					monsterControllers[i].getMonster().boxId > 0
				|| monsterControllers[i].getMonster().logicType == 2)
				return true;
		}
		return false;
	}
	
	public boolean setVariable(String key,String value)
	{
		if(key.equals("maxMorality") || key.equals("needGoodsId"))
		{
			needGoodsId = Integer.parseInt(value);
			return true;
		}
		else if(key.equals("lowerGrade") || key.equals("needGoodsCount"))
		{
			needGoodsCount= Integer.parseInt(value);
			return true;
		}
		else
			return super.setVariable(key, value);
	}
	
	private void setXY()
	{
		if(maxX > 0)
			x = (int) (Math.random() * (maxX - minX) + minX);
		if(maxY > 0)
			y = (int) (Math.random() * (maxY - minY) + minY);
	}

	public void setXYByCount(RoomController room,int count)
	{
		if(maxX > 0 || maxY > 0)
		{
			setXY();
			for (int i = 0; i < count; i++) 
			{
				if(room.isPositionNull(x, y))
					break;
			}
		}
	}
	
}
