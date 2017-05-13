package cc.lv1.rpg.gs.entity.impl;

import vin.rabbit.comm.GameObject;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.impl.ArrayList;
import vin.rabbit.util.xml.XMLNode;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.entity.RPGameObject;
import cc.lv1.rpg.gs.entity.controller.AreaController;
import cc.lv1.rpg.gs.entity.controller.MonsterController;
import cc.lv1.rpg.gs.entity.controller.NpcController;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.controller.RoomController;
import cc.lv1.rpg.gs.entity.ext.Bag;
import cc.lv1.rpg.gs.entity.ext.TaskInfo;
import cc.lv1.rpg.gs.net.SMsg;


public class Task extends RPGameObject
{
	public static final int ADD_TASK = 0x01;
	
	public static final int UPDATE_TASK = 0x02;

	private static final int CANFINISH_TASK = 0x03;

	private static final int FINISH_TASK = 0x04;

	private static final int CANCEL_TASK = 0x05;
	
	private WorldManager world;

	
	/**
	 * 	//-1删除当前任务 1 普通任务 2 日常任务 3 称号任务 4 活动任务 5 Boss任务
	 */
	public int type;
	
	/**
	 * 人物等级
	 */
	public int level;
	
    /**
     * 1初心 2熟练 3会心 4达人
     */
    public int display;
	
	/**
	 * 触发条件 1.点击npc触发 2.使用道具触发
	 */
	public int trigger;
	
	/**
	 * 是否可以取消任务  必填 true or false
	 */
	public boolean isCanCancel;
	
	/**
	 * 完成此任务需要的时间  必填 无限制为0 有限制填入毫秒数
	 */
	public int time;
	
	/**
	 *  条件等级
	 */
	public int con_level;
	
	/**
	 *  条件物品
	 */
	public int con_itemId;
	
	/**
	 * 条件 玩家接任务的id
	 */
	public int con_bAcceptTaskId;
	
	/**
	 *  条件玩家任务旗标
	 */
	public int con_bTaskId;
	
	/**
	 *  条件角色属性
	 */
	public int con_attribute;
	
	/**
	 * 条件阵营任务
	 */
	public int con_gop;
	
	/**
	 * 奖励经验
	 */
	public long awa_exp;
	
	/**
	 * 奖励游戏币
	 */
	public long awa_money;
	
	/**
	 * 奖励人民币
	 */
	public int awa_RMB;
	
	/**
	 * 奖励属性
	 */
	public int awa_attribute;
	
	/**
	 * 奖励方式
	 */
	public boolean is_awa_item_choose;
	
	/**
	 * 奖励道具
	 */
	public int [][] awa_item = new int[5][2];;
	
	
	/**
	 * 需要打都怪
	 */
	public int [][] imp_monsters = new int[5][2];
	
	/**
	 * 需要找的道具
	 */
	public int [][] imp_items = new int[5][2];
	
	
	/**
	 * 需要打都怪名称
	 */
	public String [] imp_monstersStr = new String[5];
	
	/**
	 * 需要找的道具名称
	 */
	public String [] imp_itemsStr = new String[5];
	
	/**
	 * 实际打都怪
	 */
	public int [] real_monsters = new int[5];
	
	/**
	 * 实际找的道具
	 */
	public int [] real_items = new int[5];
	
	
	/**
	 * 满足任务条件
	 */
	public boolean isEnoughCondition = false;
	
	/**
	 * 是否可以重复接
	 */
	public boolean isCanReplay = false;
	
	/**
	 * 重复接的次数
	 */
	public int replayCount = 0;
	
	
	public int monsterCount;
	
	public int itemCount;

	private int day;
	

	
	public Task()
	{
		
	}

	public void copyTo(GameObject gameobject)
	{
		super.copyTo(gameobject);
		Task task = (Task)gameobject;
		task.world = world;
		task.trigger = trigger;
		task.type = type;
		task.isCanCancel = isCanCancel;
		task.time = time;
		task.con_level = con_level;
		task.con_itemId = con_itemId;
		task.con_bTaskId = con_bTaskId;
		task.con_bAcceptTaskId = con_bAcceptTaskId;
		task.con_attribute = con_attribute;
		task.con_gop = con_gop;
		task.awa_exp = awa_exp;
		task.awa_money = awa_money;
		task.awa_RMB = task.awa_RMB;
		task.isEnoughCondition = isEnoughCondition;
		
		task.is_awa_item_choose = is_awa_item_choose;
		task.awa_item = awa_item;
		
		task.imp_monstersStr = imp_monstersStr;
		task.imp_monsters = imp_monsters;
		task.imp_itemsStr = imp_itemsStr;
		task.imp_items = imp_items;
		
		task.isCanReplay = isCanReplay;
		task.replayCount = replayCount;
		
		task.monsterCount = monsterCount;
		task.itemCount = itemCount;
	}
	
	public void writeTo(ByteBuffer buffer)
	{
		buffer.writeInt(id);
		buffer.writeUTF(name);
		
		buffer.writeByte(monsterCount);

		for (int i = 0; i < monsterCount; i++)
		{
			buffer.writeInt(imp_monsters[i][0]);
			buffer.writeUTF(imp_monstersStr[i]);
			buffer.writeInt(real_monsters[i]);
			buffer.writeInt(imp_monsters[i][1]);
		}
		
		buffer.writeByte(itemCount);
		
		for (int i = 0; i < itemCount; i++)
		{
			buffer.writeInt(imp_items[i][0]);
			buffer.writeUTF(imp_itemsStr[i]);
			buffer.writeInt(real_items[i]);
			buffer.writeInt(imp_items[i][1]);
		}
	}
	
	public void readFrom(ByteBuffer buffer)
	{
		
	}
	
	public void loadFrom(ByteBuffer buffer)
	{
		isEnoughCondition = buffer.readBoolean();

		for (int i = 0; i < monsterCount; i++)
		{
			real_monsters[i] = buffer.readInt();
			
		}
		for (int i = 0; i < itemCount; i++)
		{
			real_items[i] = buffer.readInt();
		}
	}
	
	public void saveTo(ByteBuffer buffer)
	{
		
		buffer.writeBoolean(isEnoughCondition);
		
		for (int i = 0; i < monsterCount; i++)
		{
			buffer.writeInt(real_monsters[i]);
		}
		

		for (int i = 0; i < itemCount; i++)
		{
			buffer.writeInt(real_items[i]);
		}
	}
	
	public void initial()
	{
		//可以按等级把任务装在不同都容器里面以便每等级取得时候方便
	}


	public void parseTask(XMLNode taskNode)
	{
		id = Integer.parseInt(taskNode.getSubNode("id").getData());
		name = taskNode.getSubNode("name").getData();
		
		level = Integer.parseInt(taskNode.getSubNode("level").getData());
		display = Integer.parseInt(taskNode.getSubNode("display").getData());
		
		type = Integer.parseInt(taskNode.getSubNode("type").getData());
		
		trigger = Integer.parseInt(taskNode.getSubNode("trigger").getData());
		isCanCancel = Boolean.parseBoolean(taskNode.getSubNode("can_cancel").getData());
		time = Integer.parseInt(taskNode.getSubNode("time").getData());
		
		
		XMLNode subNode = taskNode.getSubNode("condition");
		
		con_level = Integer.parseInt(subNode.getSubNode("level").getData());
		con_itemId = Integer.parseInt(subNode.getSubNode("item").getData());
		con_bTaskId = Integer.parseInt(subNode.getSubNode("before_taskID").getData());
		con_bAcceptTaskId = Integer.parseInt(subNode.getSubNode("before_accept_taskID").getData());
		con_attribute = Integer.parseInt(subNode.getSubNode("attribute").getData());
		con_gop = Integer.parseInt(subNode.getSubNode("GOP").getData());
		
		
		subNode = taskNode.getSubNode("impl_types");
			XMLNode monstersNode = subNode.getSubNode("kill_monsters");
			ArrayList list = monstersNode.getSubNodes();
			for (int i = 0; i < list.size(); i++)
			{
				XMLNode monsterNode = (XMLNode)list.get(i);
				imp_monsters[i][0]= Integer.parseInt(monsterNode.getAttributeValue("id"));
				imp_monsters[i][1] = Integer.parseInt(monsterNode.getAttributeValue("count"));
				
				imp_monstersStr[i] = monsterNode.getAttributeValue("name");
				
				if(imp_monsters[i][0] != 0)
					monsterCount++;
			}
			
			XMLNode itemsNode = subNode.getSubNode("collect_items");
			list = itemsNode.getSubNodes();
			for (int i = 0; i < list.size(); i++)
			{
				XMLNode cItemsNode = (XMLNode)list.get(i);
				imp_items[i][0]= Integer.parseInt(cItemsNode.getAttributeValue("id"));
				imp_items[i][1] = Integer.parseInt(cItemsNode.getAttributeValue("count"));
				
				imp_itemsStr[i] = cItemsNode.getAttributeValue("name");
				
				if(imp_items[i][0] != 0)
					itemCount++;
			}
		
		subNode = taskNode.getSubNode("awards");
		awa_exp = Long.parseLong(subNode.getSubNode("exp").getData());
		awa_money = Long.parseLong(subNode.getSubNode("money").getData());
		awa_RMB = Integer.parseInt(subNode.getSubNode("RMB").getData());
		awa_attribute = Integer.parseInt(subNode.getSubNode("attribute").getData());
		
		String str = subNode.getSubNode("items").getAttributeValue("choose");

		if(!str.isEmpty())
			is_awa_item_choose = Boolean.parseBoolean(str);

		list = subNode.getSubNode("items").getSubNodes();
		int awaCount = 0;
		for (int i = 0; i < list.size(); i++)
		{
			XMLNode itemNode = (XMLNode)list.get(i);
			int itemId = Integer.parseInt(itemNode.getAttributeValue("id"));
			int count = Integer.parseInt(itemNode.getAttributeValue("count"));
			
			
			if(itemId == 0 || count == 0)
				continue;
			
			awa_item [i][0] = itemId;
			awa_item [i][1] = count;
			
			awaCount++;
		}
		
		int [][] tmpAwaItem = new int[awaCount][2];
		for (int i = 0; i < awaCount; i++)
		{
			tmpAwaItem[i][0] = awa_item[i][0];
			tmpAwaItem[i][1] = awa_item[i][1];
		}
		awa_item = tmpAwaItem;

		isCanReplay = Boolean.parseBoolean(taskNode.getSubNode("can_replay").getData());
		
		if(isCanReplay)
		{
			replayCount = Integer.parseInt(taskNode.
					getSubNode("can_replay").getAttributeValue("count"));
		}
		
		String []npcIds = Utils.split(taskNode.getSubNode("npcs").getData(),":");
		for (int i = 0; i < npcIds.length; i++)
		{
			addTaskToNPC(Integer.parseInt(npcIds[i]),i,npcIds.length);
		}
	}
	
	private void addTaskToNPC(int npcId,int index,int length)
	{
		AreaController []areas = world.getAreaControllers();
		for (int i = 0; i < areas.length; i++)
		{
			RoomController []rooms = areas[i].getRooms();
			for (int j = 0; j < rooms.length; j++)
			{
				NpcController []npcs = rooms[j].getNpcList();
				for (int k = 0; k < npcs.length; k++)
				{
					if(npcs[k].getID() == npcId)
						npcs[k].addTask(this,index,length);
				}
			}
		}
	}
	

	public void setWorld(WorldManager worldManager)
	{
		world = worldManager;
	}

	
	public boolean isEnoughCondition()
	{
		return isEnoughCondition;
	}
	
	public void setEnoughCondition(boolean b)
	{
		isEnoughCondition = b;
	}

	public void sendAddTask(PlayerController target)
	{
		ByteBuffer buffer = new ByteBuffer(5);
		buffer.writeByte(Task.ADD_TASK);
		writeTo(buffer);
		sendTaskMsg(target,buffer);
	}

	  
	  
	public void sendupdateTask(PlayerController target)
	{
		ByteBuffer buffer = new ByteBuffer(16);
		buffer.writeByte(Task.UPDATE_TASK);
		sendUpdateTask(buffer);
		sendTaskMsg(target,buffer);
	}
	
	
	private void sendUpdateTask(ByteBuffer buffer)
	{
		
		buffer.writeInt(id);
		buffer.writeBoolean(isEnoughCondition);
		buffer.writeUTF(name);
		buffer.writeByte(monsterCount);

		for (int i = 0; i < monsterCount; i++)
		{
			buffer.writeInt(imp_monsters[i][0]);
			buffer.writeInt(real_monsters[i]);
		}
		
		buffer.writeByte(itemCount);
		
		for (int i = 0; i < itemCount; i++)
		{
			buffer.writeInt(imp_items[i][0]);
			buffer.writeInt(real_items[i]);
		}

	}

	public int getMonsterIndex(MonsterController monster)
	{
		for (int i = 0; i < monsterCount; i++)
		{
			if(imp_monsters[i][0] == monster.getID())
				return i;
		}
		return -1;
	}
	
	public int getItemIndex(Goods goods)
	{
		return getItemIndex(goods.id);
	}
	
	public int getItemIndex(int goodsId)
	{
		for (int i = 0; i < itemCount; i++)
		{
			if(imp_items[i][0] == goodsId)
				return i;
		}
		return -1;
	}

	public boolean checkTaskFinish(PlayerController target)
	{
		if(isEnoughCondition)
			return true;
		
		int count = 0;
		if(monsterCount > 0)
		{
			count = monsterCount;
			
			for (int i = 0; i < monsterCount; i++)
			{
				if(real_monsters[i] >= imp_monsters[i][1]) 
				{
					count--;
				}
			}
			
			if(count > 0)
			{
				return false;
			}	
		}

		
		if(itemCount > 0)
		{
			count = itemCount;
			
			for (int i = 0; i < itemCount; i++)
			{
				if(real_items[i] >= imp_items[i][1]) 
				{
					count -- ;
				}
			}
			
			if(count > 0)
			{
				return false;
			}	
		}
		
		sendTaskCanFinish(target);
		return true;
	}
	
	public void sendTaskCanFinish(PlayerController target)
	{
		isEnoughCondition = true;
		
		ByteBuffer buffer = new ByteBuffer(5);
		buffer.writeByte(Task.CANFINISH_TASK);
		buffer.writeInt(id);
		sendTaskMsg(target,buffer);
	}
	
	public void sendTaskFinish(PlayerController target)
	{
		ByteBuffer buffer = new ByteBuffer(5);
		buffer.writeByte(Task.FINISH_TASK);
		buffer.writeInt(id);
		sendTaskMsg(target,buffer);
	}
	
	public void sendTaskCancel(PlayerController target)
	{
		ByteBuffer buffer = new ByteBuffer(5);
		buffer.writeByte(Task.CANCEL_TASK);
		buffer.writeInt(id);
		sendTaskMsg(target,buffer);
	} 
	
	private void sendTaskMsg(PlayerController target,ByteBuffer buffer)
	{
		SMsg msg = new SMsg(SMsg.S_TASK_NPC_COMMAND,buffer);
		target.getNetConnection().sendMessage(msg);
	}

	public boolean recovery(PlayerController target)
	{
		Bag bag = (Bag)target.getPlayer().getExtPlayerInfo("bag");
		
		for (int i = 0; i < itemCount; i++)
		{
			if(!bag.checkGoodsEnough(imp_items[i][0],imp_items[i][1]))
				return false;
		}
		
//		TaskInfo info = (TaskInfo)target.getPlayer().getExtPlayerInfo("taskInfo");
		
		for (int i = 0; i < itemCount; i++)
		{
//			int id = imp_items[i][0];
//			int count = imp_items[i][1];
			bag.deleteGoods(target, imp_items[i][0], imp_items[i][1]);
			//bag.removeGoods(target,imp_items[i][0],imp_items[i][1]);
			
//			info.onPlayerLostItem(id,count, target);
		}
		return true;
	}
	

	
	
	public boolean sendAwards(PlayerController target,int index)
	{
		Bag bag = (Bag)target.getPlayer().getExtPlayerInfo("bag");
		
		
		if(index == -1)
		{
			for (int i = 0; i < awa_item.length; i++)
			{
				if(awa_item[i][0] == 0)
					break;

				boolean result = bag.addTaskGoods(target, awa_item[i][0], awa_item[i][1]);
				
				if(!result)
					return false;
			}
			if(awa_money != 0 || awa_RMB != 0)
			{
				bag.point += awa_money;
				bag.money += awa_RMB;
				bag.sendAddGoods(target, null);
			}
		}
		else
		{
			try
			{
				
				boolean result = bag.addTaskGoods(target, awa_item[index][0], awa_item[index][1]);
				

				if(!result)
					return false;
				if(awa_money != 0 || awa_RMB != 0)
				{
					bag.point += awa_money;
					bag.money += awa_RMB;
					bag.sendAddGoods(target, null);
				}
			}
			catch(ArrayIndexOutOfBoundsException e)
			{
				return false;
			}
		}
		
		if(awa_exp != 0)
		{
			target.addExp(awa_exp,true,false,false);
		}
		target.sendAlwaysValue();
		return true;
	}

	public void updateTask(PlayerController target)
	{
		if(itemCount <= 0)
			return;
		
		Bag bag = (Bag)target.getPlayer().getExtPlayerInfo("bag");
		

		real_items = new int[5];

		
		for (int i = 0; i < itemCount; i++)
		{
			int count = bag.getGoodsCount(imp_items[i][0]);
			real_items[i] = count > imp_items[i][1] ? imp_items[i][1] :count;
		}
	}

	public boolean equals(Object object)
	{
		if(object instanceof Task)
		{
			return ((Task)object).id == id;
		}
		return false;
	}
	
	public String toString()
	{
		return id+"";
	}

	public void setTimer(int day)
	{
		this.day = day;
	}
	
	public int getTimer()
	{
		return day;
	}



}
