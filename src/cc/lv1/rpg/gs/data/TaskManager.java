package cc.lv1.rpg.gs.data;

import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.collection.impl.ArrayList;
import vin.rabbit.util.collection.impl.HashMap;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.ext.TaskInfo;
import cc.lv1.rpg.gs.entity.impl.Task;
import cc.lv1.rpg.gs.net.SMsg;

public class TaskManager
{
	private static TaskManager taskManager = null;

	private HashMap taskMap = new HashMap(500);
	
	private HashMap lvTaskMap = new HashMap(500);
	
	private TaskManager()
	{
	}
	
	public static TaskManager getInstance()
	{
		if(taskManager == null)
			taskManager =  new TaskManager();
		return taskManager;
	}
	
	public HashMap getTasks()
	{
		return taskMap;
	}

	public Task getTask(int id)
	{
		return (Task)taskMap.get(id);
	}

	public void putTask(Task task)
	{
		taskMap.put(task.id, task);
		putLvTask(task);
	}
	
	private void putLvTask(Task task)
	{
		ArrayList arrayList = (ArrayList)lvTaskMap.get(task.con_level);
		
		if(arrayList == null)
		{
			arrayList = new ArrayList();
		}
		
		arrayList.add(task);
		lvTaskMap.put(task.con_level, arrayList);
	}
	
	public ArrayList getLvTasks(int playerLevel)
	{
		return (ArrayList)lvTaskMap.get(playerLevel);
	}
	
	public void writeCandoTasks(PlayerController target)
	{
		
		int playerLevel = target.getPlayer().level;
		
		TaskInfo taskInfo =(TaskInfo)target.getPlayer().getExtPlayerInfo("taskInfo");
		
		ArrayList taskBox = new ArrayList();
		
		for (int i = 1; i <= playerLevel; i++)
		{
			ArrayList list = getLvTasks(i);
			
			if(list == null)
				continue;
			
			
			int taskSize = list.size();
			for (int j = 0; j < taskSize; j++)
			{
				Task task = (Task)list.get(j);
				
				if(taskInfo.isTaskCompleted(task))
					continue;

				if(task.con_bAcceptTaskId != 0)
					continue;
				
				if(task.con_bTaskId != 0)
				{
					if(!taskInfo.isTaskCompleted(task.con_bTaskId))
						continue;
				}
				
				if(task.con_gop != 0)
				{
					if(target.getPlayer().camp != task.con_gop)
						continue;
				}
				
				taskBox.add(task);
			}
		}
		writeCandoTasks(taskBox,target);
	}
	
	private void writeCandoTasks(ArrayList taskBox,PlayerController target)
	{
		int size = taskBox.size();
		
		ByteBuffer buffer = new ByteBuffer(32);
		buffer.writeByte(1);
		buffer.writeInt(size);
		
		for (int i = 0; i < size; i++)
		{
			Task task = (Task)taskBox.get(i);
			task.writeTo(buffer);
		}
		
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_GET_TASKS_COMMAND,buffer));
	}
	
	
}
