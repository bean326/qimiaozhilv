package cc.lv1.rpg.gs.entity.container;

import vin.rabbit.comm.GameObject;
import vin.rabbit.net.AppMessage;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.impl.ArrayList;
import cc.lv1.rpg.gs.entity.RPGameObject;
import cc.lv1.rpg.gs.entity.controller.PlayerController;

/**
 * 主角容器   容在区域房间战斗
 * @author dxw
 *
 */
public class PlayerContainer extends RPGameObject
{
	
	protected List playerList = new ArrayList(10);
	
	protected List battleList = new ArrayList(4);
	
	public List getBattleList()
	{
		return battleList;
	}
	
	public void copyTo(GameObject gameobject)
	{
		super.copyTo(gameobject);
	}
	
	public PlayerController getPlayer(String name)
	{
		int length = playerList.size();
		for (int i = 0; i < length; i++)
		{
			PlayerController controller = (PlayerController) playerList.get(i);
			
			if(controller == null)
				continue;
			
			if (controller.getName().equals(name))
				return controller;
		}
		return null;
	}

	public PlayerController getPlayer(int id)
	{
		int length = playerList.size();
		for (int i = 0; i < length; i++)
		{
			PlayerController controller = (PlayerController) playerList.get(i);
			
			if(controller == null)
				continue;
			
			if (controller.getPlayer().id == id)
				return controller;
		}
		return null;
	}

	
	public List getPlayerList()
	{
		return playerList;
	}

	
	public PlayerController getPlayerByIndex(int index)
	{
		return (PlayerController)playerList.get(index);
	}


	public int getPlayerCount()
	{
		return playerList.size();
	}
	
	public synchronized void addPlayer(PlayerController target)
	{
		playerList.add(target);
	}
	
	public synchronized void removePlayer(PlayerController target)
	{
		playerList.remove(target);
	}
	
	/**
	 * 转发容器内所有的消息
	 * @param appMsg
	 */
	public void dispatchMsg(int sMsg,ByteBuffer buff)
	{
		try{
			ByteBuffer buffer = null;
			int size= playerList.size();
			for(int i = 0 ; i < size ; i ++)
			{
				PlayerController target = (PlayerController)playerList.get(i);
				if(target == null)
					continue;
	
				buffer = new ByteBuffer(buff.getBytes());
				
				if(target.getNetConnection() != null)
					target.getNetConnection().sendMessage(new AppMessage(sMsg,buffer));
				buffer = null;
			}
			buff = null;
		}
		catch (IllegalArgumentException e) 
		{
			System.out.println("PlayerContainer dispatchMsg message is null!");
		}
	}
	
	/**
	 * 转发范围容器内所有的消息
	 * @param appMsg
	 */
	public void dispatchScopeMsg(int sMsg,ByteBuffer buff)
	{
		int size = playerList.size();
		for(int i = 0 ; i < size ; i ++)
		{
			PlayerController target = (PlayerController)playerList.get(i);
			if(target == null)
				continue;
			ByteBuffer buffer = new ByteBuffer(buff.getBytes());
			
			if(target.getNetConnection() != null)
			target.getNetConnection().sendMessage(new AppMessage(sMsg,buffer));
		}
	}
	
	public void update(long currentMillis)
	{
		int size = playerList.size();
		for(int i = 0 ; i < size ; i ++)
		{
			PlayerController target = (PlayerController)playerList.get(i);
			
			if(target == null)
				continue;
			if(!target.isOnline())
				continue;
			
			target.update(currentMillis);
		}
	}
	
	
	
}
