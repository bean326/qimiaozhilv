package cc.lv1.rpg.gs.entity.impl.story;

import vin.rabbit.comm.GameObject;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.impl.ArrayList;
import vin.rabbit.util.collection.impl.HashMap;
import vin.rabbit.util.xml.XMLNode;
import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.data.DC;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.controller.AreaController;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.controller.RoomController;
import cc.lv1.rpg.gs.entity.ext.Bag;
import cc.lv1.rpg.gs.entity.ext.StoryEvent;
import cc.lv1.rpg.gs.entity.impl.Goods;
import cc.lv1.rpg.gs.entity.impl.Task;
import cc.lv1.rpg.gs.entity.impl.answer.Question;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsEquip;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsPetEquip;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsProp;
import cc.lv1.rpg.gs.net.SMsg;

public class Event extends Task
{
	/** 1.战斗 2.答题 3.游戏 4.搜集 5.探险*/
	public int eventType;
	
	/** 是否要随机抽取(若为false则表示要按照配置的几率抽取) */
	public boolean isRandom;
	
	/** [i][0]:id [i][1]:endId [i][2]:odds [i][3]:level */
	public int[][] eventItems;
	
	/** 选择的第几个(是eventItems的范围) */
	public int chooseType = 0;
	
	public long startTime;

	public int state = StoryEvent.HIDE;
	
	/** 正在进行 */
	public boolean isDoing = false;
	
	private Story story;
	                                                                                                                                                  
	public void copyTo(GameObject go)
	{
		super.copyTo(go);
		Event event = (Event) go;
		event.eventType = eventType;
		event.isRandom = isRandom;
		event.eventItems = eventItems;
	}
	
	public void parseEvent(XMLNode node)
	{
		id = Integer.parseInt(node.getSubNode("id").getData());
		name = node.getSubNode("name").getData();

		level = Integer.parseInt(node.getSubNode("level").getData());
		display = Integer.parseInt(node.getSubNode("display").getData());
		
		type = Integer.parseInt(node.getSubNode("type").getData());
		
		trigger = Integer.parseInt(node.getSubNode("trigger").getData());
		isCanCancel = Boolean.parseBoolean(node.getSubNode("can_cancel").getData());
		time = Integer.parseInt(node.getSubNode("time").getData());
		
		
		XMLNode subNode = node.getSubNode("condition");
		
		con_level = Integer.parseInt(subNode.getSubNode("level").getData());
		con_itemId = Integer.parseInt(subNode.getSubNode("item").getData());
		con_bTaskId = Integer.parseInt(subNode.getSubNode("before_taskID").getData());
		con_bAcceptTaskId = Integer.parseInt(subNode.getSubNode("before_accept_taskID").getData());
		con_attribute = Integer.parseInt(subNode.getSubNode("attribute").getData());
		con_gop = Integer.parseInt(subNode.getSubNode("GOP").getData());
		
		
		subNode = node.getSubNode("impl_types");
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
		
		eventType = Integer.parseInt(node.getSubNode("event_type").getData());
		
		XMLNode eventsNode = subNode.getSubNode("story_events");
		isRandom = Boolean.parseBoolean(eventsNode.getAttributeValue("random"));
		list = eventsNode.getSubNodes();
		int[][] temp = new int[list.size()][4];
		int eventCount = 0;
		for (int i = 0; i < list.size(); i++)
		{
			XMLNode eventNode = (XMLNode)list.get(i);
			int itemsId = Integer.parseInt(eventNode.getAttributeValue("id"));
			int endId = Integer.parseInt(eventNode.getAttributeValue("endId"));
			int odds = Integer.parseInt(eventNode.getAttributeValue("odds"));
			int level = Integer.parseInt(eventNode.getAttributeValue("level"));
			if(itemsId == 0)
				continue;
			temp[i][0] = itemsId;
			temp[i][1] = endId;
			temp[i][2] = odds;
			temp[i][3] = level;
			eventCount++;
		}
		eventItems = new int[eventCount][4];
		int k = 0;
		for (int i = 0; i < temp.length; i++)
		{
			if(temp[i][0] == 0)
				continue;
			eventItems[k][0] = temp[i][0];
			eventItems[k][1] = temp[i][1];
			eventItems[k][2] = temp[i][2];
			eventItems[k][3] = temp[i][3];
			k++;
			if(!isRandom)
			{
				if(temp[i][0] == 0 || temp[i][2] == 0)
				{
					System.out.println("Event parseEvent odds is zero-event.id:"+id);
				}
			}
			if(eventType == 1)
			{
				RoomController room = (RoomController) DataFactory.getInstance().getGameObject(temp[i][0]);
				if(room == null)
				{
					System.out.println("Event paseEvent room is null-event.id:"+id+" roomId:"+temp[i][0]);
				}
				room = (RoomController) DataFactory.getInstance().getGameObject(eventItems[i][1]);
				if(room == null)
				{
					System.out.println("Event paseEvent room is null-event.id:"+id+" roomId:"+temp[i][1]);
				}	
			}
			if(eventType == 2)
			{
				if(DataFactory.getInstance().getQuestion(temp[i][0]) == null)
				{
					System.out.println("Event parseEvent question is null-event.id:"+id+" qid:"+temp[i][0]);
				}
			}
		}
		if(eventType == 1)
		{
			for (int j = 0; j < eventItems.length; j++) 
			{
				for (int i = eventItems[j][0]; i < eventItems[j][1]+1; i++)
				{
					RoomController room = (RoomController) DataFactory.getInstance().getGameObject(i);
					room.isStoryRoom = true;
				}
			}
		}
		
		subNode = node.getSubNode("awards");
		awa_exp = Long.parseLong(subNode.getSubNode("exp").getData());
		awa_money = Long.parseLong(subNode.getSubNode("money").getData());
		awa_RMB = Integer.parseInt(subNode.getSubNode("RMB").getData());
		awa_attribute = Integer.parseInt(subNode.getSubNode("attribute").getData());
		
		String str = subNode.getSubNode("items").getAttributeValue("choose");

		if(!str.isEmpty())
			is_awa_item_choose = Boolean.parseBoolean(str);

		isCanReplay = Boolean.parseBoolean(node.getSubNode("can_replay").getData());
		
		if(isCanReplay)
		{
			replayCount = Integer.parseInt(node.
					getSubNode("can_replay").getAttributeValue("count"));
		}
	
		subNode = node.getSubNode("awards");
		list = subNode.getSubNode("items").getSubNodes();
		int awaCount = 0;
		int[][] tmp = new int[list.size()][3];
		for (int i = 0; i < list.size(); i++)
		{
			XMLNode itemNode = (XMLNode)list.get(i);
			int itemId = Integer.parseInt(itemNode.getAttributeValue("id"));
			int count = Integer.parseInt(itemNode.getAttributeValue("count"));
			String os = itemNode.getAttributeValue("odds");
			int odds = 0;
			if(!"".equals(os))
				odds = Integer.parseInt(os);
			if(itemId == 0 || count == 0)
				continue;

			tmp[i][0] = itemId;
			tmp[i][1] = count;
			tmp[i][2] = odds;
			awaCount++;
		}
		int [][] tmpAwaItem = new int[awaCount][3];
		for (int i = 0; i < awaCount; i++)
		{
			tmpAwaItem[i][0] = tmp[i][0];
			tmpAwaItem[i][1] = tmp[i][1];
			tmpAwaItem[i][2] = tmp[i][2];
		}
		awa_item = tmpAwaItem;
	}
	
	public void loadFrom(ByteBuffer buffer)
	{
		objectIndex = buffer.readLong();
		state = buffer.readByte();
		chooseType = buffer.readByte();
		isDoing = buffer.readBoolean();
		buffer.readShort();//备用
		buffer.readLong();//备用
	}
	
	public void saveTo(ByteBuffer buffer)
	{
		buffer.writeLong(objectIndex);
		buffer.writeByte(state);
		buffer.writeByte(chooseType);
		buffer.writeBoolean(isDoing);
		buffer.writeShort(0);//备用
		buffer.writeLong(0);//备用
	}
	
	public void writeTo(ByteBuffer buffer)
	{
		buffer.writeInt(id);
		buffer.writeUTF(name);
		buffer.writeUTF(objectIndex+"");
		buffer.writeByte(eventType);
		buffer.writeByte(state);
	}
	
	public void setStory(Story story)
	{
		this.story = story;
	}
	
	public Story getStory()
	{
		return this.story;
	}
	
	public ByteBuffer sendReward(PlayerController target,boolean isExpMult)
	{
		ByteBuffer buffer = new ByteBuffer();
		
		ByteBuffer tmp = new ByteBuffer();
		
		int count = 0;
		Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
		int random = (int) (Math.random() * 10000 + 1);
		int odds = 0;
		for (int i = 0; i < awa_item.length; i++)
		{
			if(awa_item[i][0] == 0)
				break;
			if(awa_item[i][2] == 0)
			{
				String result = bag.addStoryGoods(target, awa_item[i][0], awa_item[i][1]);
				
				if("".equals(result) || result.length() == 0)
					return null;
				
				Goods goods = getGoods(awa_item[i][0]);
				if(goods != null)
				{
					goods.writeTo(tmp);
					tmp.writeInt(awa_item[i][1]);
					
					if(DataFactory.isGoodsNotice("chongGuan", goods) && story != null && story.type == 4)
						target.sendGetGoodsInfo(3, true, DC.getChongGuanString(target, result));
					else
						target.sendGetGoodsInfo(1, false, DC.getEventRewardString(target,goods.name,awa_item[i][1]));
				}
				count++;
				
			}
			else
			{
				odds += awa_item[i][2];
				if(random <= odds)
				{
					String result = bag.addStoryGoods(target, awa_item[i][0], awa_item[i][1]);
					
					if("".equals(result) || result.length() == 0)
						return null;
					else
					{
						Goods goods = getGoods(awa_item[i][0]);
						if(goods != null)
						{
							goods.writeTo(tmp);
							tmp.writeInt(awa_item[i][1]);
							
							if(DataFactory.isGoodsNotice("chongGuan", goods) && story != null && story.type == 4)
								target.sendGetGoodsInfo(3, true, DC.getChongGuanString(target, result));
							else
								target.sendGetGoodsInfo(1, false, DC.getEventRewardString(target, goods.name, awa_item[i][1]));
						}
						count = 1;
						break;
					}
				}
			}
		}
		if(awa_money > 0)
		{
			bag.point += awa_money;
			bag.sendAddGoods(target, null);
			target.sendGetGoodsInfo(1, false, DC.getString(DC.PARTY_53)+": "+awa_money);
			
			buffer.writeUTF(awa_money+"");
		}
		else
			buffer.writeUTF(0+"");
		
		if(awa_exp > 0)
		{
			long exp = awa_exp;
			if(target.getPlayer().expMultTime != 0)
			{
				Goods goods = bag.getExtGoods(2);
				if(goods != null && goods instanceof GoodsProp)
				{
					GoodsProp prop = (GoodsProp) goods;
					if(WorldManager.currentTime - prop.expTimes < target.getPlayer().expMultTime)
					{
						exp *= prop.expMult;
					}
				}
			}

			if(isExpMult)
				exp *= story.getExpMult(this);
	
			long result = 0;
			if(exp > 0)
				result = target.addExp(exp, true, true,story.type==4);
			
			if(result > 0)
				target.sendGetGoodsInfo(1, false, DC.getString(DC.PARTY_54)+": "+result);
			
			buffer.writeUTF(exp+"");
		}
		else
			buffer.writeUTF(0+"");

		
		buffer.writeByte(count);
		buffer.writeBytes(tmp.getBytes());
		return buffer;
	}
	
	private Goods getGoods(int id)
	{
		Object obj = DataFactory.getInstance().getGameObject(id);
		if(obj == null)
			return null;
		if(!(obj instanceof Goods))
			return null;
		Goods goods = (Goods) obj;
		Goods newGoods = null;
		if(goods instanceof GoodsEquip)
		{
			newGoods = ((GoodsEquip) goods).makeNewBetterEquip(((GoodsEquip) goods).taskColor);
		}
		else if(goods instanceof GoodsPetEquip)
		{
			newGoods = (Goods) Goods.cloneObject(goods);
		}
		else if(goods instanceof GoodsProp)
		{
			newGoods = (Goods) Goods.cloneObject(goods);
		}
		return newGoods;
	}
	
	
	public void writeNeedGoods(ByteBuffer buffer)
	{
		buffer.writeByte(itemCount);
		for (int i = 0; i < itemCount; i++)
		{
			if(imp_items[i][0] == 0)
				continue;
			buffer.writeInt(imp_items[i][0]);
			buffer.writeInt(imp_items[i][1]);
		}
	}
	
	public boolean isEndRoom(RoomController room)
	{
		if(eventType != 1)
			return false;
		if(chooseType != 0)
		{
			if(room.id == eventItems[chooseType-1][1])
				return true;
		}
		return false;
	}
	
	public boolean isStoryRoom(RoomController room)
	{
		if(eventType != 1)
			return false;
		for (int i = 0; i < eventItems.length; i++) 
		{
			for (int j = eventItems[i][0]; j < eventItems[i][1]+1; j++)
			{
				if(j == room.id)
					return true;
			}
		}
		return false;
	}
}
