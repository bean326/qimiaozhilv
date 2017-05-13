package cc.lv1.rpg.gs.entity.ext;


import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.controller.BattleController;
import cc.lv1.rpg.gs.entity.controller.EventPVEController;
import cc.lv1.rpg.gs.entity.controller.MonsterController;
import cc.lv1.rpg.gs.entity.controller.MonsterGroupController;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.controller.RoomController;
import cc.lv1.rpg.gs.entity.impl.Player;
import cc.lv1.rpg.gs.entity.impl.answer.Question;
import cc.lv1.rpg.gs.entity.impl.story.Event;
import cc.lv1.rpg.gs.entity.impl.story.Story;
import cc.lv1.rpg.gs.net.SMsg;
import cc.lv1.rpg.gs.other.ErrorCode;
import vin.rabbit.net.AppMessage;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.impl.ArrayList;
import vin.rabbit.util.collection.impl.HashMap;

public class StoryEvent extends PlayerExtInfo 
{
	public static final int HIDE = 0;//默认隐藏
	public static final int ACTIVE = 1;//激活
	public static final int FINISH = 2;//完成
	public static final int DEFAULT_STORY = 1;
	
	/** 剧情列表 */
	private ArrayList storys = new ArrayList();;

	@Override
	public String getName()
	{
		return "storyEvent";
	}
	
	public StoryEvent()
	{
		ArrayList list = (ArrayList) DataFactory.getInstance().getAttachment(DataFactory.STORY_LIST);
		if(list == null)
			return;
		for (int i = 0; i < list.size(); i++)
		{
			Story s = (Story) list.get(i);
			Story story = (Story) Story.cloneObject(s);
			addStory(story);
		}
	}
	
	public ArrayList getStorys()
	{
		return storys;
	}
	
	public void clearInit(PlayerController target,int team)
	{
		storys.clear();
		ArrayList list = (ArrayList) DataFactory.getInstance().getAttachment(DataFactory.STORY_LIST);
		if(list == null)
			return;
		for (int i = 0; i < list.size(); i++)
		{
			Story s = (Story) list.get(i);
			Story story = (Story) Story.cloneObject(s);
			addStory(story);
		}
		flushStoryMap(target,team);
	}
	
	public void addStory(Story story)
	{
		if(storys.size() == 0)
		{
			story.init();
		}
		storys.add(story);
	}
	
	public Event getDoingEvent()
	{
		for (int i = 0; i < storys.size(); i++) 
		{
			Story story = (Story) storys.get(i);
			for (int j = 0; j < story.getEvents().size(); j++)
			{
				Event event = (Event) story.getEvents().get(j);
				if(event.isDoing)
					return event;
			}
		}
		return null;
	}
	
	/**
	 * 登录时更新是否有新的剧情
	 */
	public void updateStorys()
	{
		ArrayList list = (ArrayList) DataFactory.getInstance().getAttachment(DataFactory.STORY_LIST);
		if(list == null)
			return;
		if(list.size() > storys.size())
		{
			HashMap map = new HashMap();
			for (int i = 0; i < storys.size(); i++) 
			{
				Story story = (Story) storys.get(i);
				map.put(story.id, "");
			}
			for (int i = 0; i < list.size(); i++)
			{
				Story s = (Story) list.get(i);
				Story story = (Story) Story.cloneObject(s);
				if(map.get(story.id) == null)
				{
					addStory(story);
				}
			}
		}
		for (int i = 0; i < storys.size(); i++) 
		{
			Story story = (Story) storys.get(i);
			if(story.type == 4)
				continue;
			if(story.state == FINISH)
			{
				for (int j = 0; j < story.next.length; j++) 
				{
					Story s = (Story) DataFactory.getInstance().getStoryObject(story.next[j]);
					if(s != null)
					{
						for (int k = 0; k < storys.size(); k++) 
						{
							Story tmp = (Story) storys.get(k);
							if(tmp.id == s.id && tmp.state == HIDE)
								tmp.state = ACTIVE;
						}
					}
				}
			}
			else if(story.state == ACTIVE)
			{
				if(story.getEvents().size() == 0)
				{
					story.init();
				}
			}
			else if(story.state == HIDE)
			{
				story.getEvents().clear();
			}
		}
		for (int i = 0; i < storys.size(); i++) 
		{
			Story story = (Story) storys.get(i);
			if(story.state != ACTIVE)
				continue;
			ArrayList events = story.getEvents();
			if(events.size() == 0)
				continue;
			boolean isActive = false;
			boolean isFinish = true;
			for (int j = 0; j < events.size(); j++)
			{
				Event event = (Event) events.get(j);
				if(event.state != FINISH)
					isFinish = false;
				if(event.state == ACTIVE)
					isActive = true;
			}
			if(isFinish)
				continue;
			if(!isActive)
			{
				for (int j = 0; j < events.size(); j++)
				{
					Event event = (Event) events.get(j);
					if(event.state == FINISH)
						continue;
					if(event.state == HIDE)
					{
						event.state = ACTIVE;
						break;
					}
				}
			}
		}
	}
	@Override
	public void loadFrom(ByteBuffer byteBuffer) 
	{
		int size = byteBuffer.readInt();
		storys = new ArrayList(size);
		for (int i = 0; i < size; i++)
		{
			int id = byteBuffer.readInt();
			Story s = (Story) DataFactory.getInstance().getStoryObject(id);
			Story story = (Story) Story.cloneObject(s);
			
			story.loadFrom(byteBuffer);
			storys.add(story);
		}
	}

	@Override
	public void saveTo(ByteBuffer byteBuffer) 
	{
		int size = storys.size();
		byteBuffer.writeInt(size);
		for (int i = 0; i < size; i++)
		{
			Story story = (Story) storys.get(i);
			byteBuffer.writeInt(story.id);
			story.saveTo(byteBuffer);
		}
	}
	
	/**
	 * 凌晨清除副本数据,或者玩家上线时间跟下线时间不是同一天时
	 */
	public void clear(PlayerController target,Player player)
	{
		for (int i = 0; i < storys.size(); i++)
		{
			Story story = (Story) storys.get(i);
			if(story.type == 2 && story.state != HIDE)
			{
				story.init();
				story.count = 0;
			}
			if(story.type == 4)
			{
				story.state = StoryEvent.HIDE;
				story.isStart = false;
				story.count = 0;
				story.getEvents().clear();
			}
		}
		if(target != null)
		{
			if(!target.getRoom().isStoryRoom)
				return;
			if(!(target.getParent() instanceof BattleController))
			{
				if(target.getEvent() != null && target.getEvent().getStory() != null)
				{
					if(target.getEvent().getStory().type != 4)
						target.moveToRoom(DataFactory.STORYROOM);
					else
					{
						if(target.getEvent().eventType == 1)
							target.moveToRoom(DataFactory.INITROOM);
					}
				}
				else
					target.moveToRoom(DataFactory.STORYROOM);
			}
			else
			{
				BattleController battle = (BattleController) target.getParent();
				battle.setTimeOver(true);
			}
		}
		else
		{
			RoomController room = (RoomController) DataFactory.getInstance().getGameObject(player.roomId);
			if(room.isStoryRoom)
			{
				if(getDoingEvent() != null && getDoingEvent().getStory() != null)
				{
					if(getDoingEvent().getStory().type != 4)
						player.setDefaultRoom(DataFactory.STORYROOM);
					else
					{
						if(getDoingEvent().eventType == 1)
							player.setDefaultRoom(DataFactory.INITROOM);
					}
				}
				else
					player.setDefaultRoom(DataFactory.STORYROOM);
			}
		}
	}
	
	public Story getStoryById(int storyId)
	{
		for (int i = 0; i < storys.size(); i++) 
		{
			Story story = (Story) storys.get(i);
			if(story.id == storyId)
				return story;
		}
		return null;
	}
	
	public boolean isChongguan()
	{
		boolean[] chongguan = {true,false,false,false,true,false,false};
		if(!chongguan[WorldManager.weekDate])
			return false;
		int hour = Integer.parseInt(WorldManager.getTypeTime("HH", WorldManager.currentTime));
		if(hour < 10 || hour > 21)
			return false;
		return true;
	}
	
	/**
	 * 点开剧情面板
	 * @param target
	 */
	public void startStory(PlayerController target,AppMessage msg)
	{
		if(WorldManager.isZeroMorning(0))
		{
			target.sendAlert(ErrorCode.ALERT_SYSTEM_IS_UPDATE);
			return;
		}
		if(target.getTeam() != null)
		{
			target.sendAlert(ErrorCode.ALERT_STORY_TEAM_ISNOT_NULL);
			return;
		}
		if(target.isAnswer)
			return;
		if(target.isAuto)
			return;
		if(target.isBox)
			return;
		if(target.isCamp)
			return;
		if(target.isGift)
			return;
		if(target.isLookTeam)
			return;
		if(target.isPetUp)
			return;
		if(target.isReset)
			return;
		if(target.isStorage)
			return;
		if(!(target.getParent() instanceof RoomController))
			return;
		
		int team = msg.getBuffer().readByte();
		if(team == 3 && !isChongguan())
		{
			target.sendAlert(ErrorCode.ALERT_CHONGGUAN_TIME_ERROR);
			return;
		}
		
		flushStoryMap(target,team);
	}
	
	public void flushStoryMap(PlayerController target,int team)
	{
		if(team == 3)
		{
			if(target.getRoom().id != DataFactory.CHONGGUANROOM)
				target.moveToRoom(DataFactory.CHONGGUANROOM);
		}
		else
		{
			if(target.getRoom().id != DataFactory.STORYROOM)
				target.moveToRoom(DataFactory.STORYROOM);
		}
		ByteBuffer tmp = new ByteBuffer();
		int len = 0;
		for (int i = 0; i < storys.size(); i++) 
		{
			Story story = (Story) storys.get(i);
			if(story.team != team)
				continue;
			if(story.type == 4)
			{	
				if(story.minLevel == 0 && story.maxLevel == 0)
					continue;
				if(target.getPlayer().level >= story.minLevel && target.getPlayer().level <= story.maxLevel)
				{
					if(!story.isStart)
					{
						story.init();
					}
					len++;
					story.writeTo(tmp);
				}
			}
			else
			{
				len++;
				story.writeTo(tmp);
			}
		}
		
		if(len == 0)
		{
			target.sendAlert(ErrorCode.ALERT_CHONGGUAN_CONDITION_ERROR);
			return;
		}
		ByteBuffer buffer = new ByteBuffer();
		buffer.writeByte(team);
		buffer.writeInt(len);
		buffer.writeBytes(tmp.getBytes());
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_STORY_START_INFO_COMMAND,buffer));
		
		target.isStory = true;
	}
	
	/**
	 * 关闭剧情面板
	 * @param target
	 */
	public void endStory(PlayerController target,int team)
	{
		target.isStory = false;
		if(team == 3)
		{
			if(target.getRoom().id != DataFactory.CHONGGUANROOM)
				target.moveToRoom(DataFactory.CHONGGUANROOM);
		}
		else
		{
			if(target.getRoom().id != DataFactory.STORYROOM)
				target.moveToRoom(DataFactory.STORYROOM);
		}
		Event event = target.getEvent();
		if(event != null)
		{
			event.startTime = 0;
		}
		
		ByteBuffer buffer = new ByteBuffer();
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_STORY_END_INFO_COMMAND,buffer));
	}
	
	/**
	 * 点开单个剧情
	 * @param target
	 * @param msg
	 */
	public void sendStoryInfo(PlayerController target,AppMessage msg)
	{
		if(WorldManager.isZeroMorning(0))
		{
			target.sendAlert(ErrorCode.ALERT_SYSTEM_IS_UPDATE);
			return;
		}
		if(!target.isStory)
			return;
		int storyId = msg.getBuffer().readInt();
		Story story = getStoryById(storyId);
		if(story == null)
		{
			return;
		}
		if(story.type == 4 && !isChongguan())
		{
			target.sendAlert(ErrorCode.ALERT_CHONGGUAN_TIME_ERROR);
			return;
		}
		if(target.getPlayer().level > story.overLevel && story.overLevel > 0)
		{
			target.sendAlert(ErrorCode.ALERT_PLAYER_OVER_LEVEL_ERROR);
			return;
		}
		if(story.type == 2)
		{
			if(story.count >= story.time)
			{
				target.sendAlert(ErrorCode.ALERT_STORYCOPY_CANT_IN);
				return;
			}
		}
		if(story.type == 4)
		{
			if(story.count >= story.time)
			{
				target.sendAlert(ErrorCode.ALERT_STORYCOPY_CANT_IN);//不能再进大冲关了
				return;
			}
		}
		if(target.getPlayer().flyActivePoint < story.activePoint && !story.isStart)
		{
			target.sendAlert(ErrorCode.ALERT_NO_FLYACTIVEPOINT);
			return;
		}
		if(!story.isStart)
		{
			target.getPlayer().flyActivePoint -= story.activePoint;
			target.sendFlyActivePoint();
			story.isStart = true;
			flushStoryMap(target,story.team);
		}
		flushStory(story,target);
	}
	
	public void flushStory(Story story,PlayerController target)
	{
		ByteBuffer buffer = new ByteBuffer();
		story.writeTo(buffer);
		int len = story.getEvents().size();
		buffer.writeByte(len);
		for (int i = 0; i < len; i++)
		{
			Event event = (Event) story.getEvents().get(i);
			event.writeTo(buffer);
		}
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_STORY_INFO_COMMAND,buffer));
	}

	/**
	 * 点开单个事件,开始事件
	 * @param target
	 * @param msg
	 */
	public void startEvent(PlayerController target,AppMessage msg)
	{
		if(WorldManager.isZeroMorning(0))
		{
			target.sendAlert(ErrorCode.ALERT_SYSTEM_IS_UPDATE);
			return;
		}
		int storyId = msg.getBuffer().readInt();
		String objectIndex = msg.getBuffer().readUTF();
		Story story = getStoryById(storyId);
		if(story == null)
		{
			System.out.println("StoryEvent startEvent story is error:"+storyId+" "+target.getName());
			return;
		}
		if(!target.isStory)
			return;
		if(story.type == 4 && !isChongguan())
		{
			target.sendAlert(ErrorCode.ALERT_CHONGGUAN_TIME_ERROR);
			return;
		}
		if(story.state != ACTIVE)
		{
//			System.out.println("StoryEvent startEvent story is not active:"+storyId+" "+target.getName());
			return;
		}
		if(target.getPlayer().level > story.overLevel && story.overLevel > 0)
		{
			target.sendAlert(ErrorCode.ALERT_PLAYER_OVER_LEVEL_ERROR);
			return;
		}
		long eventIndex = 0;
		try{eventIndex = Long.parseLong(objectIndex);}catch(Exception e)
		{
			System.out.println("StoryEvent startEvent objectIndex is error:"+objectIndex);
			return;
		}
		Event event = story.getEvent(eventIndex);
		if(event == null)
		{
			System.out.println("StoryEvent startEvent event is error:"+eventIndex);
			return;
		}
		if(event.state != ACTIVE)
		{
//			System.out.println("StoryEvent startEvent event is not active:"+eventIndex+" "+event.id+" "+event.name);
			return;
		}
		Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
		if(bag.getNullCount() < 2)
		{
			target.sendAlert(ErrorCode.ALERT_STORY_BAG_ISNOT_NULL);
			return;
		}

		ByteBuffer buffer = new ByteBuffer();
		buffer.writeByte(event.eventType);
		buffer.writeInt(storyId);
		buffer.writeUTF(objectIndex);
		if(event.eventType == 1 || event.eventType == 2 || event.eventType == 3)
		{
			int location = -1;
			if(event.isRandom)
			{
				location = (int) (Math.random() * event.eventItems.length);
			}
			else
			{
				int rate = 0;
				int random = (int) (Math.random() * 10000 + 1);
				for (int i = 0; i < event.eventItems.length; i++) 
				{
					rate += event.eventItems[i][2];
					if(random <= rate)
					{
						location = i;
						break;
					}
				}
			}
			if(event.eventType == 1)
			{
				RoomController room = null;
				if(event.chooseType != 0)
				{
					room = target.getWorldManager().getRoomWolrd(event.eventItems[event.chooseType-1][0]);
				}
				else
				{
					room = target.getWorldManager().getRoomWolrd(event.eventItems[location][0]);
					event.chooseType = location+1;
				}
				if(room == null)
				{
					System.out.println("Even checkEventStart room is null:"+event.id+" loc:"+location);
					return;
				}
				target.moveToRoom(room.id);
				target.isStory = false;
			}
			else if(event.eventType == 2)
			{
				buffer.writeInt(event.eventItems[location][0]);
			}
			else if(event.eventType == 3)
			{
				buffer.writeInt(event.eventItems[location][0]);
				buffer.writeInt(event.eventItems[location][3]);
			}
		}
		else if(event.eventType == 4)
		{
			event.writeNeedGoods(buffer);
		}
		else if(event.eventType == 5)
		{
			if(event.startTime == 0)
			{
				event.startTime = WorldManager.currentTime;
				buffer.writeInt(event.time);
			}
			else
			{
				int t = (int) (WorldManager.currentTime - event.startTime);
				if(t < event.time)
				{
					buffer.writeInt(event.time-t);
				}
				else
				{
					buffer.writeInt(0);
				}
			}
		}
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_STORY_START_EVENT_COMMAND,buffer));
		
		target.setEvent(event);
	}
	
	/**
	 * 事件结果,客户端通知过来的
	 * @param target
	 * @param msg
	 */
	public void endEvent(PlayerController target,AppMessage msg)
	{
		if(WorldManager.isZeroMorning(0))
		{
			target.sendAlert(ErrorCode.ALERT_SYSTEM_IS_UPDATE);
			return;
		}
		int storyId = msg.getBuffer().readInt();
		String objectIndex = msg.getBuffer().readUTF();
		Story story = getStoryById(storyId);
		if(story == null)
		{
			System.out.println("StoryEvent endEvent story is error:"+storyId);
			return;
		}
		if(!target.isStory)
			return;
		if(story.state != ACTIVE)
		{
//			System.out.println("StoryEvent endEvent story is not active:"+storyId);
			return;
		}
		long eventIndex = 0;
		try{eventIndex = Long.parseLong(objectIndex);}catch(Exception e)
		{
			System.out.println("StoryEvent startEvent objectIndex is error:"+objectIndex);
		}
		Event event = story.getEvent(eventIndex);
		if(event == null)
		{
			System.out.println("StoryEvent startEvent event is error:"+eventIndex);
			return;
		}
		if(event.state != ACTIVE)
		{
//			System.out.println("StoryEvent startEvent event is not active:"+eventIndex);
			return;
		}

		if(event.eventType == 1)
			return;
		endEvent(target,event,msg);
	}
	
	private void endEvent(PlayerController target, Event event,AppMessage msg)
	{
		ByteBuffer buffer = new ByteBuffer();
		buffer.writeByte(event.eventType);
		if(event.eventType == 2)
		{
			int question = msg.getBuffer().readInt();
			int answer = msg.getBuffer().readByte();
			Question q = DataFactory.getInstance().getQuestion(question);
			if(q == null)
			{
				return;
			}
			if(q.isRight(answer))
			{
				buffer.writeBoolean(true);
				
				finishEvent(target, event);
			}
			else
			{
				buffer.writeBoolean(false);
			}
			buffer.writeByte(answer);
		}
		else if(event.eventType == 3)
		{
			finishEvent(target, event);
		}
		else if(event.eventType == 4)
		{
			Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
			for (int i = 0; i < event.imp_items.length; i++) 
			{
				if(event.imp_items[i][0] == 0)
					continue;
				if(event.imp_items[i][1] > bag.getGoodsCount(event.imp_items[i][0]))
				{
					target.sendAlert(ErrorCode.ALERT_CHANGED_EXP_NEEDGOODS_ERROR);
					return;
				}
			}
			
			for (int i = 0; i < event.imp_items.length; i++) 
			{
				if(event.imp_items[i][0] == 0)
					continue;
				bag.deleteGoods(target, event.imp_items[i][0], event.imp_items[i][1]);
			}
			
			finishEvent(target, event);
		}
		else if(event.eventType == 5)
		{
			if(event.startTime == 0)
				return;
			int t = (int) (WorldManager.currentTime - event.startTime);
			if(t < event.time)
			{
				buffer.writeBoolean(false);
				buffer.writeInt(event.time-t);
			}
			else
			{
				buffer.writeBoolean(true);
				
				ByteBuffer tmp = finishEvent(target, event);
				buffer.writeBytes(tmp.getBytes());
			}
		}
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_STORY_END_EVENT_COMMAND,buffer));
	}
	
	public ByteBuffer finishEvent(PlayerController target,Event event)
	{
		event.state = FINISH;
		event.isDoing = false;
		
		Story story = event.getStory();
		ByteBuffer buffer = event.sendReward(target,story.type==2||story.type==4);
		
		if(story.isEnd(event))
		{
			if(story.count == story.time)
			{
				story.state = FINISH;
				for (int i = 0; i < story.next.length; i++) 
				{
					Story s = (Story) DataFactory.getInstance().getStoryObject(story.next[i]);
					if(s != null)
					{
						for (int j = 0; j < storys.size(); j++) 
						{
							Story tmp = (Story) storys.get(j);
							if(tmp.id == s.id)
							{
								tmp.init();
							}
						}
					}
				}
			}
			else
			{
				if(story.type == 2)
					story.init();
				if(story.type == 4)
				{
					story.state = HIDE;
					story.isStart = false;
					story.getEvents().clear();
				}
			}
			if((story.type == 2 || story.type == 4) && story.count < story.time)
				story.count++;
			
			flushStoryMap(target,story.team);
		}
		else
		{
			story.activeNextEvent(event);
			flushStory(story,target);
		}
		target.setEvent(null);
		
		return buffer;
	}
	
	/** 接收玩家和的互动 */
	public void clientMessageChain(PlayerController target, AppMessage msg)
	{
		int type = msg.getType();
		
		if(type == SMsg.C_STORY_START_INFO_COMMAND)
		{
			startStory(target,msg);//点开剧情面板
		}
		else if(type == SMsg.C_STORY_END_INFO_COMMAND)
		{
			endStory(target,msg.getBuffer().readByte());//关闭剧情面板
		}
		else if(type == SMsg.C_STORY_INFO_COMMAND)
		{
			sendStoryInfo(target,msg);//点开单个剧情
		}
		else if(type == SMsg.C_STORY_START_EVENT_COMMAND)
		{
			startEvent(target,msg);//点开单个事件,开始事件
		}
		else if(type == SMsg.C_STORY_END_EVENT_COMMAND)
		{
			endEvent(target,msg);//事件结果
		}
	}

}
