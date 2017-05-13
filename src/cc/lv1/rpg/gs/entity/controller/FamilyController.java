package cc.lv1.rpg.gs.entity.controller;

import cc.lv1.rpg.gs.WorldManager;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.impl.ArrayList;
/**
 * 家族控制器
 * @author dxw
 *
 */
public class FamilyController extends GroupController
{
	/**
	 * 家族最大人数
	 */
	public static final int FAMILY_MAX_MEMBERS = 50;

	public static final int FAMILY_POINT_CONDITION= 100000;

	public static final int LEVEL = 200;
	
	
	/** 修改族长的间隔时间 */
	public static final int CHANGE_LEADER_TIME = 12 * 60 * 60 * 1000;
	
	/**
	 * 家族阵营 
	 */
	public int camp;
	
	/**
	 * 上一次族长变更时间(保存的是以秒为单位的时间,非毫秒时间)
	 */
	public int changeLeaderTime;

	/**
	 * 家族地域id
	 */
	public int []areaId = new int[0];
	
	
	public List familyNameList = new ArrayList(FAMILY_MAX_MEMBERS);
	
	
	public FamilyController()
	{
		super();
	}
	
	
	/**
	 * 当前时间段能否修改族长
	 * @return
	 */
	public boolean isChange()
	{
		long time = (long)changeLeaderTime * 1000;
		boolean result = WorldManager.currentTime - time > CHANGE_LEADER_TIME;
		return result;
	}
	
	public FamilyController(int id,String name)
	{
		super(id,name);
		this.count = 0;
		this.maxCount = FAMILY_MAX_MEMBERS;
	}

	public void saveTo(ByteBuffer buffer)
	{
		super.saveTo(buffer);
		
		buffer.writeInt(changeLeaderTime);
		buffer.writeInt(areaId.length);
		for (int i = 0; i < areaId.length; i++)
		{
			buffer.writeInt(areaId[i]);
		}
		
		buffer.writeInt(camp);
		
		int size = familyNameList.size();
		buffer.writeInt(size);
		for (int i = 0; i < size; i++)
		{
			buffer.writeUTF((String)familyNameList.get(i));
		}
	}
	
	public void loadFrom(ByteBuffer buffer)
	{
		super.loadFrom(buffer);
		
		changeLeaderTime = buffer.readInt();
		
		int tmp[] = new int[buffer.readInt()];
		for (int i = 0; i < tmp.length; i++)
		{
			tmp[i] = buffer.readInt();
		}
		areaId = tmp;
		
		camp = buffer.readInt();
		
		int size = buffer.readInt();
		for (int i = 0; i < size; i++)
		{
			familyNameList.add(buffer.readUTF());
		}
	}
	
	public void writeTo(ByteBuffer buffer)
	{

		int size = familyNameList.size();
		
		buffer.writeInt(size);
		for (int i = 0; i < size; i++)
		{
			String name = (String)familyNameList.get(i);
			
			if(name.equals(leaderName))
			{
				buffer.writeBoolean(true);
			}
			else
			{
				buffer.writeBoolean(false);
			}
			
			PlayerController players = worldmanger.getPlayerController(name);
			
			if(players == null)
			{
				buffer.writeBoolean(false);
				buffer.writeUTF(name);
			}
			else
			{
				buffer.writeBoolean(true);
				buffer.writeUTF(name);
				buffer.writeInt(players.getID());
				buffer.writeInt(players.getPlayer().level);
			}
		}
	}
	
	public void setLeader(PlayerController target)
	{
		super.setLeader(target);
		this.camp = target.getPlayer().camp;
	}

	public void addPlayer(PlayerController target)
	{
		super.addPlayer(target);
	}
	
	public void removePlayer(PlayerController target)
	{
		super.removePlayer(target);
	}

	public boolean isInFamily(String name)
	{
		return getPlayerIndexByFamily(name) != -1;
	}
	
	public int getPlayerIndexByFamily(String name)
	{
		int size = familyNameList.size();
		for (int i = 0; i < size; i++)
		{
			if(((String)familyNameList.get(i)).equals(name))
				return i;
		}
		return -1;
	}
	
	/**
	 * 玩家进入家族
	 * @param name
	 */
	public void addNameToFamily(Object name)
	{
		count++;
		familyNameList.add(name);
	}
	
	/**
	 * 删除玩家到家族
	 * @param index
	 */
	public void removeNameToFamily(int index)
	{
		count--;
		familyNameList.remove(index);
	}
	
	/**
	 * 删除玩家到家族
	 * @param index
	 */
	public void removeNameToFamily(Object name)
	{
		count--;
		familyNameList.remove(name);
	}
	
	/**
	 * 取得家族总人数
	 * @return
	 */
	public int getFamilyTotalCount()
	{
		return familyNameList.size();
	}
}
