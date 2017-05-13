package cc.lv1.rpg.gs.entity.controller;

import vin.rabbit.comm.GameObject;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.impl.ArrayList;
import vin.rabbit.util.xml.XMLNode;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.RoomExit;
import cc.lv1.rpg.gs.entity.container.PlayerContainer;
import cc.lv1.rpg.gs.gui.MainFrame;
 
/**
 * 区域控制器
 * @author dxw
 *
 */
public class AreaController extends PlayerContainer
{
	
	private WorldManager worldManager;
	
	private int roomsLength;
	
	private RoomController [] rooms;
	
	private int worldId;
	
	private AreaController [] areaControllers;
	
	public void init(XMLNode node)
	{ 

		id = Integer.parseInt(node.getName().substring(1));

		name = node.getSubNode("name").getData();
		
		worldId = Integer.parseInt(node.getSubNode("worldId").getData());

		ArrayList roomList = node.getSubNode("rooms").getSubNodes();
		int roomSize = roomList.size();
		
		rooms = new RoomController[roomSize];
		roomsLength = roomSize;
		for(int i = 0 ; i < roomSize ; i ++)
		{
			XMLNode n = (XMLNode)roomList.get(i);
		
			int id = Integer.parseInt(n.getSubNode("id").getData().substring(1));
			
			GameObject gameObject = DataFactory.getInstance().getGameObject(id);
			
			if(gameObject == null || !(gameObject instanceof RoomController))
			{
				MainFrame.println("find not room by "+id);
				continue;
			}

			rooms[i] = (RoomController)RoomController.cloneObject(gameObject);
			rooms[i].setParent(this);
					
			ArrayList exitList = n.getSubNode("exits").getSubNodes();
			int exitSize = exitList == null?0:exitList.size();

			RoomExit exits []= new RoomExit[8]; 

			int eventPoint = Integer.parseInt(n.getSubNode("eventPoint").getData());
			rooms[i].setEventPoint(eventPoint);
			
			for(int j = 0 ; j < exitSize ; j ++)
			{
				XMLNode exitNode = (XMLNode)exitList.get(j);
				int position = Integer.parseInt(exitNode.getAttributeValue("position"));
				int targetArea = Integer.parseInt(exitNode.getAttributeValue("targetArea").substring(1));
				int targetRoom = Integer.parseInt(exitNode.getAttributeValue("targetRoom").substring(1));
				int targetPosition = Integer.parseInt(exitNode.getAttributeValue("targetPosition"));

				exits[position] = new RoomExit();
				exits[position].targetArea = targetArea;
				exits[position].targetRoomId = targetRoom;
				exits[position].targetPosittion = targetPosition;
				
				exits[position].sourceArea = id;
				exits[position].sourcePosittion = position;
				exits[position].sourceRoomId = rooms[i].id;
				exits[position].sourceRC = rooms[i];
			}
			
			rooms[i].setExits(exits);
			CopyController copy = DataFactory.getInstance().getCopyByArea(this.id);
			
			if(copy != null)
			{
				rooms[i].isCopyPartyRoom = true;
				rooms[i].setCopy(copy);
			}

			DataFactory.getInstance().setRoomObject(rooms[i]);//把之前数据工厂中的旧的房间对象清除，设置新的房间对象
		}
		
		CopyController copy = DataFactory.getInstance().getCopyByArea(id);
		if(copy != null)
		{
			if(!"".equals(copy.inRate) || !"0".equals(copy.inRate))
				return;
			if(copy.startRoomId != rooms[0].id || copy.endRoomId != rooms[rooms.length-1].id)
			{
				System.out.println("AreaController copy startRoomId or endRoomId error,areaId:"+id);
				System.out.println("AreaController startRoomId:"+rooms[0].id+"  endRoomId:"+rooms[rooms.length-1].id);
			}
		}
		
	}	
	

	
	public void setParent(WorldManager worldManager)
	{
		this.worldManager = worldManager;
	}
	
	public WorldManager getParent()
	{
		return worldManager;
	}

	public void update(long timeMillis)
	{
		for (int i = 0; i < roomsLength; i++)
		{
			rooms[i].update(timeMillis);
		}
	}

	public void ExitLinking()
	{
		for (int i = 0; i < rooms.length; i++)
		{
			rooms[i].ExitLinking();
		}
	}

	public RoomController getRoomById(int targetRoomId)
	{
		int length = rooms.length;
		for (int i = 0; i < length; i++)
		{
			if(rooms[i].id == targetRoomId)
				return rooms[i];
		}
		return null;
	}
	
	public int getRoomLength()
	{
		return roomsLength;
	}

	public RoomController[] getRooms()
	{
		return rooms;
	}

	public int getParentId()
	{
		return worldId;
	}

	public void setSmailWorld(AreaController[] areaControllers)
	{
		this.areaControllers = areaControllers;
	}

	public List getSmaillWorld()
	{
		List list = new ArrayList();
		for (int i = 0; i < areaControllers.length; i++)
		{
			if(areaControllers[i].id == worldId)
			{
				list.add(areaControllers[i]);
			}
		}
		return list;
	}
	
}
