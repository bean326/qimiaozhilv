package cc.lv1.rpg.gs.entity.ext;

import java.util.Date;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;

import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.i.Map;
import vin.rabbit.util.collection.i.Set;
import vin.rabbit.util.collection.impl.ArrayList;
import vin.rabbit.util.collection.impl.HashMap;
import cc.lv1.rpg.gs.data.TaskManager;
import cc.lv1.rpg.gs.entity.controller.MonsterController;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.impl.Task;
import cc.lv1.rpg.gs.gui.MainFrame;

public class TaskInfo extends PlayerExtInfo
{
	/** 最大能接的任务数量 */
	public static final int MAX_TASK_COUNT = 5; 
	
	private ArrayList tasks = new ArrayList(15);
	
	private int[] completedTasks = new int[0];

	private int[] acceptTasks = new int[0];
	
	public boolean addTask(Task task)
	{
		if(task == null)
			return false;
		
		for (int j = 0; j < tasks.size(); j++)
		{
			if(((Task)tasks.get(j)).id == task.id)
				return false;
		}

		tasks.add(task);
		return true;
	}
	
	public boolean cancelTask(int id)
	{
		Task task = getTaskById(id);
		
		if(task == null)
			return false;
		
		if(!task.isCanCancel)
			return false;
		
		tasks.remove(task);
		removeAcceptTask(task.id);
		return true;
	}

	
	public void completeTask(Task task)
	{
		tasks.remove(task);
		addCompletedTask(task);
	}
	
	public void removeTask(Task task)
	{
		tasks.remove(task);
	}
	
	public int getCurrentTaskSize()
	{
		return tasks.size();
	}
	
	private void addCompletedTask(Task t)
	{
		addCompletedTask(t.id);
	}
	
	public void addCompletedTask(int id)
	{
		if (completedTasks == null)
		{
			completedTasks = new int[1];
		}
		else
		{
			int[] tmp = new int[completedTasks.length + 1];
			for (int i = 0; i < completedTasks.length; i++)
				tmp[i] = completedTasks[i];
			completedTasks = tmp;
		}
		completedTasks[completedTasks.length - 1] = id;
	}
	
	public boolean isInAcceptTask(int id)
	{
		if(acceptTasks == null)
			return false;
		
		for (int i = 0; i < acceptTasks.length; i++)
		{
			if(acceptTasks[i] == id)
			{
				return true;
			}
		}
		return false;
	}
	
	public void addAcceptTask(int id)
	{
		if(isInAcceptTask(id))
			return;
		
		if (acceptTasks == null)
		{
			acceptTasks = new int[1];
		}
		else
		{
			int[] tmp = new int[acceptTasks.length + 1];
			for (int i = 0; i < acceptTasks.length; i++)
				tmp[i] = acceptTasks[i];
			acceptTasks = tmp;
		}
		acceptTasks[acceptTasks.length - 1] = id;
	}
	
	public void removeAcceptTask(int id)
	{
		if (acceptTasks == null)
			return;
		int length = acceptTasks.length;
		
		boolean isHasAcceptId = false;
		
		for (int i = 0; i < length; i++)
		{
			if(acceptTasks[i] == id)
			{
				isHasAcceptId = true;
				break;
			}
		}
		
		if(!isHasAcceptId)
			return;
		
		int [] tmp = new int[length-1];
		
		for (int i = 0,j = 0; i < length; i++)
		{
			if(acceptTasks[i] == id)
				continue;

			tmp[j++] = acceptTasks[i];
		}
		acceptTasks = tmp;
	}

	public int getCompletedTaskCount()
	{
		if (completedTasks == null)
			return 0;
		return completedTasks.length;
	}
	
	public int getAcceptTaskCount()
	{
		if (acceptTasks == null)
			return 0;
		return acceptTasks.length;
	}
	
	public boolean isTaskCompleted(Task task)
	{
		return isTaskCompleted(task.id);
	}
	
	public boolean isTaskCompleted(int id)
	{
		if (completedTasks == null)
			return false;
		for (int i = 0; i < completedTasks.length; i++)
		{
			if (completedTasks[i] == id)
				return true;
		}
		return false;
	}
	
	public boolean isTaskAccept(int id)
	{
		for (int i = 0; i < acceptTasks.length; i++)
		{
			if (acceptTasks[i] == id)
				return true;
		}
		return false;
	}
	
	public String getName()
	{
		return "taskInfo";
	}

	public void writeTo(ByteBuffer buffer)
	{
		int size = tasks.size();
		buffer.writeInt(size);
		
		for (int i = 0; i < size; i++)
		{
			Task task = (Task)tasks.get(i);
			task.writeTo(buffer);
		}
		
	}

	public void loadFrom(ByteBuffer buffer)
	{
		int count = buffer.readShort();
//		int count = buffer.readInt();
		Task task = null;
		int id = 0;

		int [] deleteTask = new int[0]; 
		
		for (int i = 0; i < count; i++)
		{
			id = buffer.readInt();
			task = (Task) TaskManager.getInstance().getTask(id);
			
			if(task.type != -1) //删除掉的任务
			{
				task = (Task) Task.cloneObject(task);
				task.loadFrom(buffer);
				tasks.add(task);
			}
			else
			{
				task = (Task) Task.cloneObject(task);
				task.loadFrom(buffer);	
				
				int[] infos = new int[deleteTask.length+1];
				for (int j = 0; j < deleteTask.length; j++)
					infos[j] = deleteTask[j];
				infos[deleteTask.length] = id;
				deleteTask = infos;
			}

		}

		Map map = new HashMap();
		int tId;
		count = buffer.readShort();
//		count = buffer.readInt();
		for (int i = 0; i < count; i++)
		{
			tId = buffer.readInt();		

			if(tId == 0)
				continue;
		
			if(((Task) TaskManager.getInstance().getTask(tId)).type == -1)
				continue;

			map.put(tId, 1);	
		}
		
		Object objs []= map.keySet().toArray();
		completedTasks = new int[objs.length];
		for (int i = 0; i < objs.length; i++)
		{
			completedTasks[i]= (Integer)objs[i];
		}
		
		map = new HashMap();
		count = buffer.readShort();
//		count = buffer.readInt();
		for (int i = 0; i < count; i++)
		{
			tId = buffer.readInt();
			
			if(tId == 0)
				continue;
			
			map.put(tId, 1);	
		}

		count = buffer.readInt();
		
		
		objs = map.keySet().toArray();
		acceptTasks = new int[objs.length];
		for (int i = 0; i < objs.length; i++)
		{
			acceptTasks[i]= (Integer)objs[i];	
		}
		map = null;
		objs = null;

		for (int i = 0; i < count; i++)
		{
			int tid = buffer.readInt();
			int replayCount = buffer.readInt();
			Task oldTask = new Task();
			oldTask.id = tid;
			oldTask.replayCount = replayCount;
			oldTask.setTimer(buffer.readInt());
			replayMap.put(tid, oldTask);			
		}
		

		if(deleteTask.length != 0)
		{
			
			List list = new ArrayList();
			boolean isSame = false;
			
			for (int i = 0; i < completedTasks.length; i++)
			{
				for (int j = 0; j < deleteTask.length; j++)
				{
					if(completedTasks[i] == deleteTask[j])
					{
						isSame=true;
						break;
					}
				}
				
				if(isSame)
					isSame=false;
				else
					list.add(completedTasks[i]);
			}
			
			completedTasks = new int[list.size()];
			for (int i = 0; i < completedTasks.length; i++)
			{
				completedTasks[i]= (Integer)list.get(i);
			}
			
			list = new ArrayList();
			isSame = false;
			
			for (int i = 0; i < acceptTasks.length; i++)
			{
				for (int j = 0; j < deleteTask.length; j++)
				{
					if(acceptTasks[i] == deleteTask[j])
					{
						isSame=true;
						break;
					}
				}
				
				if(isSame)
					isSame=false;
				else
					list.add(acceptTasks[i]);
			}
			
			
			acceptTasks = new int[list.size()];
			for (int i = 0; i < acceptTasks.length; i++)
			{
				acceptTasks[i]= (Integer)list.get(i);
			}
			
			for (int i = 0; i < deleteTask.length; i++)
			{
				replayMap.remove(deleteTask[i]);
			}
		}
	}

//	private int[] loadOne(int count,ByteBuffer buffer)
//	{
//		Task task = null;
//		int id = 0;
//		
//		int [] deleteTask = new int[0]; 
//System.out.println("task size:"+count);
//		for (int i = 0; i < count; i++)
//		{
//			id = buffer.readInt();
//System.out.println("taskId:"+id);
//			task = (Task) TaskManager.getInstance().getTask(id);
//System.out.println("taskName:"+task.name);
//			if(task.type != -1) //删除掉的任务
//			{
//				task = (Task) Task.cloneObject(task);
//				task.loadFrom(buffer);
//				tasks.add(task);
//			}
//			else
//			{
//				task = (Task) Task.cloneObject(task);
//				task.loadFrom(buffer);	
//				
//				int[] infos = new int[deleteTask.length+1];
//				for (int j = 0; j < deleteTask.length; j++)
//					infos[j] = deleteTask[j];
//				infos[deleteTask.length] = id;
//				deleteTask = infos;
//			}
//		}
//		return deleteTask;
//	}
//	private void loadTwo(int count,ByteBuffer buffer)
//	{
//		Map map = new HashMap();
//		int tId;
//		for (int i = 0; i < count; i++)
//		{
//			tId = buffer.readInt();		
//
//			if(tId == 0)
//				continue;
//		
//			if(((Task) TaskManager.getInstance().getTask(tId)).type == -1)
//				continue;
//
//			map.put(tId, 1);	
//		}
//		Object objs []= map.keySet().toArray();
//		completedTasks = new int[objs.length];
//		for (int i = 0; i < objs.length; i++)
//		{
//			completedTasks[i]= (Integer)objs[i];
//		}
//	}
//	private void loadThree(int size,ByteBuffer buffer,int[] deleteTask)
//	{
//		int count = size;
//		Map map = new HashMap();
//		int tId = 0;
//		for (int i = 0; i < count; i++)
//		{
//			tId = buffer.readInt();	
//			if(tId == 0)
//				continue;
//			map.put(tId, 1);	
//		}
//		count = buffer.readInt();
//		Object[] objs = map.keySet().toArray();
//		acceptTasks = new int[objs.length];
//		for (int i = 0; i < objs.length; i++)
//		{
//			acceptTasks[i]= (Integer)objs[i];	
//		}
//		map = null;
//		objs = null;
//		for (int i = 0; i < count; i++)
//		{
//			int tid = buffer.readInt();
//			int replayCount = buffer.readInt();
//			Task oldTask = new Task();
//			oldTask.id = tid;
//			oldTask.replayCount = replayCount;
//			oldTask.setTimer(buffer.readInt());
//			replayMap.put(tid, oldTask);			
//		}
//		if(deleteTask.length != 0)
//		{
//			List list = new ArrayList();
//			boolean isSame = false;
//			for (int i = 0; i < completedTasks.length; i++)
//			{
//				for (int j = 0; j < deleteTask.length; j++)
//				{
//					if(completedTasks[i] == deleteTask[j])
//					{
//						isSame=true;
//						break;
//					}
//				}
//				if(isSame)
//					isSame=false;
//				else
//					list.add(completedTasks[i]);
//			}
//			completedTasks = new int[list.size()];
//			for (int i = 0; i < completedTasks.length; i++)
//			{
//				completedTasks[i]= (Integer)list.get(i);
//			}
//			list = new ArrayList();
//			isSame = false;
//			for (int i = 0; i < acceptTasks.length; i++)
//			{
//				for (int j = 0; j < deleteTask.length; j++)
//				{
//					if(acceptTasks[i] == deleteTask[j])
//					{
//						isSame=true;
//						break;
//					}
//				}
//				if(isSame)
//					isSame=false;
//				else
//					list.add(acceptTasks[i]);
//			}
//			acceptTasks = new int[list.size()];
//			for (int i = 0; i < acceptTasks.length; i++)
//			{
//				acceptTasks[i]= (Integer)list.get(i);
//			}
//			for (int i = 0; i < deleteTask.length; i++)
//			{
//				replayMap.remove(deleteTask[i]);
//			}
//		}
//
//	}
//	
//
//	public void loadFrom(ByteBuffer buffer)
//	{System.out.println(buffer.available()+"-------");
//		int[] deleteTask = null;
//		ByteBuffer test1 = new ByteBuffer(buffer.getBytes(),buffer.position(),buffer.available());
//		ByteBuffer test2 = new ByteBuffer(buffer.getBytes(),buffer.position(),buffer.available());
//System.out.println("buffer:"+buffer.available()+"  test2:"+test2.available()+" test1:"+test1.available());
//		int size1 = test1.readInt();
//		int size2 = test2.readShort();
//		if(size1 == size2 && size1 > 0)
//		{
//			buffer.readInt();
//System.out.println("count1:"+size1);
//			deleteTask = loadOne(size1,buffer);
//			
//			ByteBuffer test3 = new ByteBuffer(buffer.getBytes(),buffer.position(),buffer.available());
//			ByteBuffer test4 = new ByteBuffer(buffer.getBytes(),buffer.position(),buffer.available());
//System.out.println("1shengxia:"+buffer.available());
//			int size4 = test3.readInt();
//			int size5 = test4.readShort();
//			if(size4 == size5 && size4 > 0)
//			{
//			    buffer.readInt();
//System.out.println("count2:"+size4);
//				loadTwo(size4,buffer);
//				
//				ByteBuffer test5 = new ByteBuffer(buffer.getBytes(),buffer.position(),buffer.available());
//				ByteBuffer test6 = new ByteBuffer(buffer.getBytes(),buffer.position(),buffer.available());
//System.out.println("2shengxia:"+buffer.available());
//				int size6 = test5.readInt();
//				int size7 = test6.readShort();
//System.out.println("size6:"+size6+"  size7:"+size7);
//				if(size6 == size7 && size6 > 0)
//				{
//					buffer.readInt();
//System.out.println("count3:"+size6);
//					loadThree(size6,buffer,deleteTask);
//				}
//				else
//				{
//					int count = buffer.readShort();
//					loadThree(count,buffer,deleteTask);
//				}
//			}
//			else
//			{
//				int count = buffer.readShort();
//				loadTwo(count,buffer);
//				count = buffer.readShort();
//				loadThree(count,buffer,deleteTask);
//			}
//		}
//		else
//		{
//			int count = buffer.readShort();
//			deleteTask = loadOne(count,buffer);
//			
//			count = buffer.readShort();
//			loadTwo(count,buffer);
//			
//			count = buffer.readShort();
//			loadThree(count,buffer,deleteTask);
//		}
//	}

	public void saveTo(ByteBuffer buffer)
	{
		Object objs[] = tasks.toArray();
		
StringBuffer str = new StringBuffer();
str.append(" taskSize1:");
str.append(tasks.size());
str.append(" objsLength1:");
str.append(objs.length);
		
		int count = objs.length;
		
str.append(" count1:");
str.append(count);
		
		buffer.writeShort(count);
		
str.append(" taskSize2:");
str.append(tasks.size());
str.append(" objsLength2:");
str.append(objs.length);
str.append(" count2:");
str.append(count);
		
		for (int i = 0; i < count; i++)
		{
			Task t = (Task) objs[i];
			
			if(t == null)
			{
str.append(" taskSize3:");
str.append(tasks.size());
str.append(" objsLength3:");
str.append(objs.length);
str.append(" count3:");
str.append(count);
str.append(" i:");
str.append(i);
			}
			buffer.writeInt(t.id);
			t.saveTo(buffer);
		}
if(str.toString().indexOf("taskSize3") != -1)
	System.out.println(str.toString());
		count = completedTasks.length;
		buffer.writeShort(completedTasks.length);
//		buffer.writeInt(count);
		for (int i = 0; i < completedTasks.length; i++)
		{
			buffer.writeInt(completedTasks[i]);
		}
		
		count = acceptTasks.length;
		buffer.writeShort(acceptTasks.length);
//		buffer.writeInt(count);
		for (int i = 0; i < acceptTasks.length; i++)
		{
			buffer.writeInt(acceptTasks[i]);
		}
		
		
		count = replayMap.size();
		buffer.writeInt(count);
		
		Object []keys = replayMap.keySet().toArray();
		Object []values = replayMap.values().toArray();
		
		for (int i = 0; i < count; i++)
		{
			buffer.writeInt(Integer.parseInt(keys[i]+""));
			buffer.writeInt(((Task)values[i]).replayCount);
			buffer.writeInt(((Task)values[i]).getTimer());
		}
		
	}
	


	public Task getTaskById(int id)
	{
		int count = tasks.size();
		for (int i = 0; i < count; i++)
		{
			Task task = (Task)tasks.get(i);
			
			if(task.id == id)
				return task;
		}
		return null;
	}
	
	
	public void onPlayerKillMonster(MonsterController monster,PlayerController player)
	{
		int taskSize = tasks.size();
		for (int i = 0; i < taskSize; i++)
		{
			Task task = (Task)tasks.get(i);
			
			if(task.isEnoughCondition())
				continue;
			
			if(task.monsterCount == 0)
				continue;
			
			int index = task.getMonsterIndex(monster);
			
			if(index == -1)
				continue;
			
			task.real_monsters[index]++;
			
			if(task.real_monsters[index] >= task.imp_monsters[index][1])
			{
				task.real_monsters[index] = task.imp_monsters[index][1];
			}
			
			task.sendupdateTask(player);
			
			if(task.real_monsters[index] >= task.imp_monsters[index][1])
			{
				task.checkTaskFinish(player);
			}
		}
		
	}
//	/**
//	 * 
//	 *	返回值用来确认任务道具 
//	 *	假设传来一个任务道具，只有当返回值true的时候才能往背包里加道具.
//	 * 	而当是非任务道具传来的时候就算返回值为false也照样加到背包里面
//	 * 
//	 */
//	public boolean onPlayerGotItems(Goods goods,PlayerController target)
//	{
//		int taskSize = tasks.size();
//		
//		boolean isTaskGoodsAdd = false;
//		
//		for (int i = 0; i < taskSize; i++)
//		{
//			Task task = (Task)tasks.get(i);
//			
//			if(task.isEnoughCondition())
//				continue;
//			
//			if(task.itemCount == 0)
//				continue;
//			
//			int index = task.getItemIndex(goods);
//			
//			if(index == -1)
//				continue;
//			
//			task.real_items[index] += goods.goodsCount;
//		
//			if(task.real_items[index] >= task.imp_items[index][1])
//			{
//				task.real_items[index] = task.imp_items[index][1];
//			}
//			
//			task.sendupdateTask(target);
//			
//			if(task.real_items[index] >= task.imp_items[index][1])
//			{
//				task.checkTaskFinish(target);
//			}
//			
//			isTaskGoodsAdd = true;	
//		}
//		return isTaskGoodsAdd;
//	}
	
	
	/**
	 * 
	 *	返回值用来确认任务道具 
	 *	假设传来一个任务道具，只有当返回值true的时候才能往背包里加道具.
	 * 	而当是非任务道具传来的时候就算返回值为false也照样加到背包里面
	 * 
	 */
	public boolean onPlayerGotItem(int goodsId,int goodsCount,PlayerController target)
	{
		int taskSize = tasks.size();
		
		boolean isTaskGoodsAdd = false;
		
		for (int i = 0; i < taskSize; i++)
		{
			Task task = (Task)tasks.get(i);
		
			if(task.isEnoughCondition())
				continue;
			
			if(task.itemCount == 0)
				continue;
			
			int index = task.getItemIndex(goodsId);
			
			if(index == -1)
				continue;
			
			task.real_items[index] += goodsCount;
		
			if(task.real_items[index] >= task.imp_items[index][1])
			{
				task.real_items[index] = task.imp_items[index][1];
			}
			
			task.sendupdateTask(target);
			
			if(task.real_items[index] >= task.imp_items[index][1])
			{
				task.checkTaskFinish(target);
			}

			isTaskGoodsAdd = true;	
		}
		return isTaskGoodsAdd;
	}
	
	
	public void onPlayerLostItem(int goodsId,int goodsCount,PlayerController target)
	{
		int taskSize = tasks.size();
		for (int i = 0; i < taskSize; i++)
		{
			Task task = (Task)tasks.get(i);			

			int index = task.getItemIndex(goodsId);
			
			if(index == -1)
				continue;
			
			task.real_items[index] -= goodsCount;
			
			task.updateTask(target);
			
			if(task.real_items[index] <= 0)
			{
				task.real_items[index] = 0;
			}
			
			task.setEnoughCondition(false);
			
			task.checkTaskFinish(target);
			
			task.sendupdateTask(target);
		}
	}
	
//	public void onPlayerLostItem(Goods goods,PlayerController target)
//	{
//		int taskSize = tasks.size();
//		for (int i = 0; i < taskSize; i++)
//		{
//			Task task = (Task)tasks.get(i);			
//			
//			int index = task.getItemIndex(goods);
//			
//			if(index == -1)
//				continue;
//
//			task.real_items[index] -= goods.goodsCount;
//			
//			task.updateTask(target);
//			
//		
//			if(task.real_items[index] <= 0)
//			{
//				task.real_items[index] = 0;
//			}
//			
//			task.sendupdateTask(target);
//
//			task.setEnoughCondition(false);
//		}
//	}

	public void onPlayerLevelUp()
	{
		
	}


	private HashMap replayMap = new HashMap(100);
	
	public void completeReplayTask(Task task)
	{
		Task oldTask = (Task)replayMap.get(task.id);
		
		if(oldTask == null)
		{
			oldTask = new Task();
			oldTask.id = task.id;
			oldTask.replayCount = task.replayCount;
		}
		oldTask.replayCount--;
		
		//增加每次循环任务都更新时间
		oldTask.setTimer(new Date().getDay());
		
		if(oldTask.replayCount <= 0)
		{
			completeTask(task);
			
			//oldTask.setTimer(new Date().getDay());
			
			if(task.type == 2 || task.type == 5)
				replayMap.put(oldTask.id,oldTask);
			else
				replayMap.remove(oldTask.id);
			
			return;
		}
		
		
		replayMap.put(oldTask.id,oldTask);
	}


	public void checkReplayTask(Task task)
	{
		Task oldTask = (Task)replayMap.get(task.id);
	
		if(oldTask != null)
		{
			
			//屏蔽掉剩余数量判断
			//if(oldTask.replayCount <= 0)
			//{
				if(new Date().getDay() != oldTask.getTimer())
				{
					clearTaskTag(task);
					replayMap.remove(oldTask.id);
				}
			//}

		}
	}
	
	public void clearTaskTag(Task task)
	{
		
		if(isTaskCompleted(task.id))
		{
			int [] tmpCompletedTasks = new int[completedTasks.length-1];
			
			if(tmpCompletedTasks.length != 0)
			{
				for (int i = 0,j = 0; i < completedTasks.length; i++)
				{
					if(completedTasks[i] == task.id)
						continue;
					
					tmpCompletedTasks[j] = completedTasks[i];
					j++;
				}
			}
			completedTasks = tmpCompletedTasks;
		}


		if(isTaskAccept(task.id))
		{
			int [] tmpAcceptTasks = new int[acceptTasks.length-1];
			
			if(tmpAcceptTasks.length != 0)
			{
				for (int i = 0,j = 0; i < acceptTasks.length; i++)
				{
					if(acceptTasks[i] == task.id)
						continue;
					
					tmpAcceptTasks[j] = acceptTasks[i];
					j++;
				}	
			}
			acceptTasks = tmpAcceptTasks;
		}
		
	}

	public void checkAllTask(PlayerController target)
	{
		int size = tasks.size();
		for (int i = 0; i < size; i++)
		{
			Task task = (Task)tasks.get(i);
			if(task.checkTaskFinish(target))
				task.sendTaskCanFinish(target);
		}
	}

	public ArrayList getTasks()
	{
		return tasks;
	}

	
	
	
}
