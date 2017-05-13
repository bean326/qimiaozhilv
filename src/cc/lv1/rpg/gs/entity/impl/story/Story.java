package cc.lv1.rpg.gs.entity.impl.story;

import vin.rabbit.comm.GameObject;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.impl.ArrayList;
import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.RPGameObject;
import cc.lv1.rpg.gs.entity.ext.StoryEvent;

/**
 * 剧情类
 * @author Administrator
 *
 */
public class Story extends RPGameObject
{
	public static final double CHONG_GUAN_MULT = 1.5;
	
	/** 编组(1.奇妙之旅1 2.奇妙之旅2 3.奇妙大冲关)只是用来给客户端分组显示的 */
	public int team;
	
	/** 1.主线剧情 2.冒险副本剧情 3.VIP剧情(可选) 4.奇妙大冲关 */
	public int type;
	
	/** 每天可完成次数(0表示终生只有完成一次),其他次数表示每天可以完成的次数 */
	public int time;
	
	/** 等级限制(超过多少级不能执行,0级表示无限制) */
	public int overLevel;
	
	/** 开始剧情需要消耗的行动值 */
	public int activePoint;
	
	/** 完成此剧情后将要激活下一个或几个剧情 */
	public int[] next;
	
	/** 事件组 */
	public int[] eventTeam;
	
	/** 抽取几率 */
	public int[] odds;
	
	/** 等级需求下限*/
	public int minLevel;
	
	/** 等级需求上限 */
	public int maxLevel;

	/** 事件列表 */
	private ArrayList events = new ArrayList();;
	
	/** 副本进行了几次(冒险副本才有的) */
	public int count;
	
	public int state = StoryEvent.HIDE;
	
	/** 是否开始剧情了(判断是否是第一次进,第一次进剧情要扣除相关需求) */
	public boolean isStart;
	
	public void loadFrom(ByteBuffer buffer)
	{
		buffer.readInt();//备用
		isStart = buffer.readBoolean();
		state = buffer.readByte();
		count = buffer.readByte();
		int size = buffer.readByte();
		events = new ArrayList(size);
		for (int i = 0; i < size; i++) 
		{
			int id = buffer.readInt();
			Event e = (Event) DataFactory.getInstance().getStoryObject(id);
			Event event = (Event) Event.cloneObject(e);
			
			event.loadFrom(buffer);
			event.setStory(this);
			events.add(event);
		}
	}
	
	public void saveTo(ByteBuffer buffer)
	{
		buffer.writeInt(0);//备用
		buffer.writeBoolean(isStart);
		buffer.writeByte(state);
		buffer.writeByte(count);
		int size = events.size();
		buffer.writeByte(size);
		for (int i = 0; i < size; i++)
		{
			Event event = (Event) events.get(i);
			buffer.writeInt(event.id);
			event.saveTo(buffer);
		}
	}
	
	public void copyTo(GameObject go)
	{
		super.copyTo(go);
		Story story = (Story) go;
		
		story.team = team;
		story.type = type;
		story.time = time;
		story.activePoint = activePoint;
		story.overLevel = overLevel;
		story.minLevel = minLevel;
		story.maxLevel = maxLevel;
		
		int[] temp = new int[next.length];
		System.arraycopy(next, 0, temp, 0, temp.length);
		story.next = temp;
		
		temp = new int[eventTeam.length];
		System.arraycopy(eventTeam, 0, temp, 0, temp.length);
		story.eventTeam = temp;
		
		temp = new int[odds.length];
		System.arraycopy(odds, 0, temp, 0, temp.length);
		story.odds = temp;
	}
	
	public void writeTo(ByteBuffer buffer)
	{
		super.writeTo(buffer);
		buffer.writeByte(type);
		buffer.writeByte(count);
		buffer.writeByte(state);
		buffer.writeBoolean(isStart);
	}
	
	public ArrayList getEvents()
	{
		return events;
	}
	
	public Event getEvent(long objectIndex)
	{
		for (int i = 0; i < events.size(); i++) 
		{
			Event event = (Event) events.get(i);
			if(event.objectIndex == objectIndex)
				return event;
		}
		return null;
	}
	
	public void activeNextEvent(Event event)
	{
		for (int i = 0; i < events.size(); i++) 
		{
			Event e = (Event) events.get(i);
			if(e.objectIndex == event.objectIndex)
			{
				if(i != events.size()-1)
				{
					((Event) events.get(i+1)).state = StoryEvent.ACTIVE;
					break;
				}
			}
		}
	}
	
	public void init()
	{
		state = StoryEvent.ACTIVE;
		isStart = false;
		events = new ArrayList();
		if(type == 1 || type == 3)
		{
			EventTeam et = (EventTeam) DataFactory.getInstance().getStoryObject(eventTeam[0]);
			if(et == null)
			{	
				System.out.println("Story init et is null:"+eventTeam[0]);
				return;
			}
			for (int i = 0; i < et.events.length; i++)
			{
				Event e = (Event) DataFactory.getInstance().getStoryObject(et.events[i]);
				Event event = (Event) Event.cloneObject(e); 
				event.objectIndex = GameServer.getInstance().getDatabaseAccessor().getGoodsObjIndex();
				event.setStory(this);
				if(i == 0)
					event.state = StoryEvent.ACTIVE;
				events.add(event);
			}
		}
		else if(type == 2 || type == 4)
		{
			for (int i = 0; i < 10; i++) 
			{
				int random = (int) (Math.random() * 10000 + 1);
				int rate = 0;
				for (int j = 0; j < eventTeam.length; j++)
				{
					EventTeam et = (EventTeam) DataFactory.getInstance().getStoryObject(eventTeam[j]);
					rate += odds[j];
					if(random <= rate)
					{
						int loc = (int) (Math.random() * et.events.length);
						Event e = (Event) DataFactory.getInstance().getStoryObject(et.events[loc]);
						Event event = (Event) Event.cloneObject(e); 
						event.objectIndex = GameServer.getInstance().getDatabaseAccessor().getGoodsObjIndex();
						event.setStory(this);
						if(i == 0)
							event.state = StoryEvent.ACTIVE;
						events.add(event);
						break;
					}
				}
			}
		}
	}
	
	public boolean isEnd(Event event)
	{
		if(events.size() == 0)
			return false;
		Event last = (Event) events.get(events.size()-1);
		if(event.objectIndex == last.objectIndex)
			return true;
		else 
			return false;
	}
	
	public double getExpMult(Event event)
	{
		for (int i = 0; i < events.size(); i++)
		{
			Event e = (Event) events.get(i);
			if(e.objectIndex == event.objectIndex)
			{
				return 0.6 + i * 0.1;
			}
		}
		return 0;
	}
	
	public boolean setVariable(String key,String value)
	{
		if("next".equals(key))
		{
			String[] strs = Utils.split(value, ":");
			next = new int[strs.length];
			for (int i = 0; i < strs.length; i++) 
			{
				next[i] = Integer.parseInt(strs[i]);
			}
			return true;
		}
		else if("eventTeam".equals(key))
		{
			String[] strs = Utils.split(value, ":");
			eventTeam = new int[strs.length];
			for (int i = 0; i < strs.length; i++) 
			{
				eventTeam[i] = Integer.parseInt(strs[i]);
				EventTeam et = (EventTeam) DataFactory.getInstance().getStoryObject(eventTeam[i]);
				if(et == null)
				{
					System.out.println("Story setVariable et is null:"+eventTeam[i]);
				}
			}
			return true;
		}
		else if("odds".equals(key))
		{
			String[] strs = Utils.split(value, ":");
			odds = new int[strs.length];
			for (int i = 0; i < strs.length; i++) 
			{
				odds[i] = Integer.parseInt(strs[i]);
			}
			return true;
		}
		else if("levels".equals(key))
		{
			if("0".equals(value))
				return true;
			String[] strs = Utils.split(value, "-");
			for (int i = 0; i < strs.length; i++) 
			{
				minLevel = Integer.parseInt(strs[0]);
				maxLevel = Integer.parseInt(strs[1]);
			}
			return true;
		}
		else
			return super.setVariable(key, value);
	}
}
