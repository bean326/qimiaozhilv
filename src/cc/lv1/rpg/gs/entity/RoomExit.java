package cc.lv1.rpg.gs.entity;

import cc.lv1.rpg.gs.entity.controller.RoomController;

/**
 * 房间出口实体
 * @author dxw
 *
 */
public class RoomExit
{
	public int sourceArea;
	
	public int sourcePosittion;
	
	public int sourceRoomId;
	
	public RoomController sourceRC;
	
	public int targetArea;
	
	public int targetPosittion;
	
	public int targetRoomId;
	
	public RoomController targetRC;

}
