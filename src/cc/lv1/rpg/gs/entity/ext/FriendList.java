package cc.lv1.rpg.gs.entity.ext;

import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.impl.ArrayList;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.ext.PlayerExtInfo;

public class FriendList extends PlayerExtInfo
{

	public static final int ADD_FRIEND = 0x01;
	
	public static final int REMOVE_FRIEND = 0x02;
	
	public static final int ADD_BLACK = 0x03;
	
	public static final int REMOVE_BLACK =0x04;
	
	public static final int LIST = 0x05;
	
	private final int MAX_LIST = 100;
	
	private List friendList = new ArrayList();
	
	private List blackList = new ArrayList();
	

	public String getName()
	{
		return "friendList";
	}
	
	public boolean isFriend(PlayerController target)
	{
		for (int i = 0; i < friendList.size(); i++) 
		{
			if(friendList.get(i).toString().equals(target.getName()))
				return true;
		}
		return false;
	}

	public boolean addFriend(String name)
	{
		if(friendList.size() < MAX_LIST)
		{
			friendList.add(name);
			return true;
		}
		return false;
	}
	
	public boolean addBlack(String name)
	{
		if(blackList.size() < MAX_LIST)
		{
			blackList.add(name);
			return true;
		}
		return false;
	}
	
	public boolean removeFromFriends(String name)
	{
		int size = friendList.size();
		for(int i = 0;i < size ;i ++)
		{
			if(name.equals((String)friendList.get(i)))
			{
				friendList.remove(i);
				return true;
			}
		}
		return false;
	}
	
	public boolean removeFromBlack(String name)
	{
		int size = blackList.size();
		for(int i = 0;i < size ;i ++)
		{
			if(name.equals((String)blackList.get(i)))
			{
				blackList.remove(i);
				return true;
			}
		}
		return false;
	}
	
	public boolean isInFriends(String name)
	{ 
		int size = friendList.size();
		for(int i = 0;i < size ;i ++)
		{
			if(name.equals((String)friendList.get(i)))
				return true;
		}
		return false;
	}
	
	public boolean isInBlack(String name)
	{
		int size = blackList.size();
		for(int i = 0;i < size ;i ++)
		{
			if(name.equals((String)blackList.get(i)))
				return true;
		}
		return false;
	}
	
	
	public void loadFrom(ByteBuffer buffer)
	{
		int size = buffer.readByte();
		for(int i = 0;i < size;i ++)
		{
			friendList.add(buffer.readUTF());
		}
		size = buffer.readByte();
		for(int i = 0;i < size;i ++)
		{
			blackList.add(buffer.readUTF());
		}
	}

	
	public void saveTo(ByteBuffer buffer)
	{
		int size = friendList.size();
		
		buffer.writeByte(size);
		
		String name = "";
		for (int i = 0; i < size; i++)
		{
			name = (String)friendList.get(i);
			buffer.writeUTF(name);
		}
		
		size = blackList.size();
		
		buffer.writeByte(size);
		for (int i = 0; i < size; i++) 
		{
			name = (String)blackList.get(i);
			buffer.writeUTF(name);
		}
	}

	public void writeTo(ByteBuffer buffer,WorldManager world)
	{
		int size = friendList.size();
		String name = null;
		PlayerController player = null;
		buffer.writeByte(size);
		for(int i = 0;i < size;i ++)
		{
			name = (String)friendList.get(i);
			
			player = world.getPlayerController(name);
			
			buffer.writeUTF(name);
			if(player != null)
			{
				buffer.writeBoolean(true);
				buffer.writeInt(player.getID());
				buffer.writeInt(player.getPlayer().level);
			}
			else
			{
				buffer.writeBoolean(false);
			}
				
		}
		size = blackList.size();
		buffer.writeByte(size);
		for(int i = 0;i < size;i ++)
		{
			name = (String)blackList.get(i);

			player = world.getPlayerController(name);
			
			buffer.writeUTF(name);
			if(player != null)
			{
				buffer.writeBoolean(true);
				buffer.writeInt(player.getID());
				buffer.writeInt(player.getPlayer().level);
			}
			else
			{
				buffer.writeBoolean(false);
			}
		} 
	}
	
	public void cleanFriendList()
	{
		friendList = new ArrayList();
	}
	
	public void cleanBlackList()
	{
		blackList = new ArrayList();
	}
}


