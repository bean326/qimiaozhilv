package cc.lv1.rpg.gs.entity;

import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.impl.Top;
import cc.lv1.rpg.gs.net.SMsg;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.collection.impl.ArrayList;
import vin.rabbit.util.collection.impl.HashMap;

/**
 * 排行中心
 * @author dxw
 *
 */
public class TopCenter
{
	private static TopCenter tc;
	
	private HashMap topMap = new HashMap(10);
	
	private TopCenter()
	{}
	
	public static TopCenter getInstance()
	{
		if(tc == null)
			tc = new TopCenter();
		return tc;
	}
	
	
	/** 
	 * 0取得所有的
	 * 玩家等级 1
	 * 宠物等级 2
	 * 玩家最大血 3
	 * 玩家最大蓝 4
	 * 玩家游戏币 5
	 * 玩家物理攻击力 6
	 * 玩家精神攻击力 7
	 * 玩家物理暴击率 8
	 * 玩家精神暴击率 9
	 * 玩家物理暴击伤害 10
	 * 玩家精神暴击伤害 11
	 * 玩家物理免伤 12
	 * 玩家精神免伤 13
	 * 玩家辅助值 14
	 * */
	public void getTop(PlayerController target,int index)
	{
		Object [] keys = topMap.keySet().toArray();
		
		ByteBuffer buffer = new ByteBuffer(1024*3);
		buffer.writeByte(index);
		if(index == 0)
		{
			ArrayList list = null;
			buffer.writeInt(keys.length);
			for (int i = 0; i < keys.length; i++)
			{
				list = (ArrayList)topMap.get(keys[i]);
				int size = list.size();
				buffer.writeInt(size);
				for (int j = 0; j< size; j++)
				{
					Top top = (Top)list.get(j);
					buffer.writeInt(top.id);
					buffer.writeUTF(top.accountName);
					buffer.writeUTF(top.playerName);
					buffer.writeInt((int)top.value);
				}
			}
		}
		else
		{
			ArrayList list = (ArrayList)topMap.get(keys[index-1]);
			int size = list.size();
			buffer.writeInt(size);

			for (int i = 0; i < size; i++)
			{
				Top top = (Top)list.get(i);
				buffer.writeInt(top.id);
				buffer.writeUTF(top.accountName);
				buffer.writeUTF(top.playerName);
				if(index == 1)
					buffer.writeByte(top.playerJob);
				
				if(index == 5)
					WorldManager.sendPoint(buffer, top.value);
				else
					buffer.writeInt((int)top.value);
			}
			
		}
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_PLAYER_GET_TOP_COMMAND,buffer));	
	}
	
	
	public synchronized void put(int id,ArrayList list)
	{
		topMap.put(id, list);
	}

	public void clear()
	{
		topMap = new HashMap(10);
	}

	
}
