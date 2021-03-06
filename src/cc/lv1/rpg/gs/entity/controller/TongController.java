/*package cc.lv1.rpg.gs.entity.controller;

import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.impl.ArrayList;

*//**
 * 帮会控制器
 * @author dxw
 *
 *//*
public class TongController extends GroupController
{

	*//**
	 *  帮会行动值
	 *//*
	public int activeNum = 0;
	
	*//**
	 * 副会长id
	 *//*
	public int deputyLeaderId;
	
	*//**
	 * 副会长name
	 *//*
	public String deputyLeaderName;
	
	*//**
	 * 帮会公告
	 *//*
	public String remark;
	
	*//**
	 * 等级
	 *//*
	public int level = 1;
	
	public TongController()
	{
	}
	
	public TongController(int id , String name)
	{
		super(id,name);
		this.count = 0;
		this.maxCount = 3;
	}
	
	public void saveTo(ByteBuffer buffer)
	{
		super.saveTo(buffer);
		buffer.writeInt(level);
		buffer.writeUTF(remark);
		buffer.writeInt(deputyLeaderId);
		buffer.writeUTF(deputyLeaderName);
		buffer.writeInt(activeNum);
	}
	
	public void loadFrom(ByteBuffer buffer)
	{
		super.loadFrom(buffer);
		level = buffer.readInt();
		remark = buffer.readUTF();
		deputyLeaderId = buffer.readInt();
		deputyLeaderName = buffer.readUTF();
		activeNum = buffer.readInt();

	}

	public void writeTo(ByteBuffer buffer)
	{
		buffer.writeUTF(name);
		buffer.writeUTF(remark);
		buffer.writeInt(level);
		buffer.writeInt(activeNum);
		buffer.writeInt(contribution);
		buffer.writeUTF(leaderName);
		buffer.writeUTF(deputyLeaderName);
		
		int size = familyList.size();
		buffer.writeInt(size);
		for (int i = 0; i < size; i++)
		{
			FamilyController family = (FamilyController)familyList.get(i);
			
			if(family == null)
				continue;
			
			buffer.writeUTF(family.title);
			buffer.writeUTF(family.name);
			family.writeTo(buffer);
		}

	}
	
	public void setDeputyLeader(PlayerController target)
	{
		deputyLeaderId = target.getID();
		deputyLeaderName = target.getName();
	}
	
	public void clearDeputyLeader()
	{
		deputyLeaderId = 0;
		deputyLeaderName = "";
	}
	
	public void addPlayer(PlayerController player)
	{
			//把族长的成员加到帮会里面
	}

	
	public boolean isInTong(FamilyController family)
	{
		return getFamilyIndexByTong(family) != -1;
	}
	
	
	public int getFamilyIndexByTong(FamilyController family)
	{
		int size = familyList.size();
		for (int i = 0; i < size; i++)
		{
			if(((FamilyController)familyList.get(i)).name.equals(family.name))
				return i;
		}
		return -1;
	}
	
	public String toString()
	{
		return id+"";
	}
	
	public boolean equals(Object obj)
	{
		if(obj instanceof TongController)
		{
			TongController tc = (TongController)obj;
			if(tc.id == id)
				return true;
		}
		return false;
	}
}	
*/

