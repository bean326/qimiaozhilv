package cc.lv1.rpg.gs.entity.i;

import vin.rabbit.comm.GameObject;
import vin.rabbit.util.ByteBuffer;
import cc.lv1.rpg.gs.entity.RPGameObject;

/**
 * 游戏世界对象
 * @author dxw
 *
 */
public class WorldObject extends RPGameObject
{
	/**世界ID */
	public int worldId; 
	
	public void copyTo(GameObject dest)
	{
		super.copyTo(dest);
		
		WorldObject sprite = (WorldObject)dest;
		sprite.worldId = worldId;
	}
	
	public void loadFrom(ByteBuffer buffer)
	{
		super.loadFrom(buffer);
		worldId = buffer.readInt();
	}
	
	public void saveTo(ByteBuffer buffer)
	{
		super.saveTo(buffer);
		buffer.writeInt(worldId);
	}
	
	public void readFrom(ByteBuffer buffer)
	{
		super.readFrom(buffer);
		worldId = buffer.readInt();
	}
	
	public void writeTo(ByteBuffer buffer)
	{
		super.writeTo(buffer);
		buffer.writeInt(worldId);
	}
	
}
