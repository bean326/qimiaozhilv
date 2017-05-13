package cc.lv1.rpg.gs.entity.controller;

import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.impl.ArrayList;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.entity.container.PlayerContainer;
/**
 * 群组控制器，家族帮会继承
 * @author dxw
 *
 */
public class GroupController extends PlayerContainer
{

	protected WorldManager worldmanger;
	
	public int count;
	
	public int maxCount ;
	
	public int leaderId;
	
	public String leaderName;

	/**
	 * 称号
	 */
	public String title = "beginner";
	
	/**
	 * 状态 1正常开启 2关闭
	 */
	public int state = 1;
	
	/**
	 *  贡献度
	 */
	public int contribution;
	
	public GroupController(int id, String name)
	{
		this.id = id;
		this.name = name;
	}
	
	public GroupController()
	{
	}
	
	public void setLeader(PlayerController target)
	{
		this.leaderId = target.getID();
		this.leaderName = target.getName();
	}

	public void saveTo(ByteBuffer buffer)
	{
		super.saveTo(buffer);
		
		buffer.writeInt(count);
		buffer.writeInt(maxCount);
		buffer.writeInt(leaderId);
		buffer.writeUTF(leaderName);
		buffer.writeUTF(title);
		buffer.writeInt(state);
		buffer.writeInt(contribution);
	}
	
	
	public void loadFrom(ByteBuffer buffer)
	{
		super.loadFrom(buffer);
		
		count = buffer.readInt();
		maxCount = buffer.readInt();
		leaderId = buffer.readInt();
		leaderName = buffer.readUTF();
		title = buffer.readUTF();
		state = buffer.readInt();
		contribution = buffer.readInt();
	}
	
	public void setWorldManager(WorldManager world)
	{
		this.worldmanger = world;
	}
	
	public PlayerController getLeader()
	{
		return worldmanger.getPlayer(leaderName);
	}

	public boolean isFull()
	{
		return count >= maxCount;
	}

}
