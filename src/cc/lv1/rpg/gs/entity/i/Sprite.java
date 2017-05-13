package cc.lv1.rpg.gs.entity.i;

import cc.lv1.rpg.gs.entity.impl.battle.PlayerBattleTmp;
import cc.lv1.rpg.gs.entity.impl.battle.SpriteBattleTmp;
import vin.rabbit.comm.GameObject;
import vin.rabbit.util.ByteBuffer;
/**
 * 精灵
 * @author dxw
 *
 */
public class Sprite extends WorldObject
{
	/** X坐标 */
	public int x;
	
	/** Y坐标 */
	public int y;
	
	/** 区域ID */
	public int areaId;
	
	/** 房间ID */
	public int roomId;
	
	/** 头像id */
	public int iconId;
	
	/** 模型id */
	public int modelId;
	
	/** 模型动画id */
	public int modelMotionId ;
	
	/** 等级 */
	public int level = 1;
	
	/** 生命值 */
	public int hitPoint;
	
	/** 最大生命值 */
	public int maxHitPoint;
	
	/** 当前精力 */
	public int magicPoint;
	
	/** 最大精力 */
	public int maxMagicPoint;
	
	/** 修改阵营的月份(一个月只能修改一次) */
	public int injury;
	
	public void copyTo(GameObject dest)
	{
		super.copyTo(dest);
		
		Sprite sprite = (Sprite)dest;
		sprite.x = x;
		sprite.y = y;
		sprite.areaId = areaId;
		sprite.roomId = roomId;
		sprite.iconId = iconId;
		sprite.modelId = modelId;
		sprite.modelMotionId = modelMotionId;
		sprite.level = level;
		sprite.hitPoint = hitPoint;
		sprite.maxHitPoint = maxHitPoint;
		sprite.magicPoint = magicPoint;
		sprite.maxMagicPoint = maxMagicPoint;
		sprite.injury = injury;
	}
	
	public void loadFrom(ByteBuffer buffer)
	{
		super.loadFrom(buffer);
		x = buffer.readInt();
		y = buffer.readInt();
		areaId = buffer.readInt();
		roomId = buffer.readInt();
		iconId = buffer.readInt();
		modelId = buffer.readInt();
		modelMotionId = buffer.readInt();
		level = buffer.readInt();
		hitPoint = buffer.readInt();
		maxHitPoint = buffer.readInt();
		magicPoint = buffer.readInt();
		maxMagicPoint = buffer.readInt();
		injury = buffer.readInt();
	}
	
	public void saveTo(ByteBuffer buffer)
	{
		super.saveTo(buffer);
		buffer.writeInt(x);
		buffer.writeInt(y);
		buffer.writeInt(areaId);
		buffer.writeInt(roomId);
		buffer.writeInt(iconId);
		buffer.writeInt(modelId);
		buffer.writeInt(modelMotionId);
		buffer.writeInt(level);
		buffer.writeInt(hitPoint);
		buffer.writeInt(maxHitPoint);
		buffer.writeInt(magicPoint);
		buffer.writeInt(maxMagicPoint);
		buffer.writeInt(injury);
	}
	
	public void readFrom(ByteBuffer buffer)
	{
		super.readFrom(buffer);
		x = buffer.readInt();
		y = buffer.readInt();
		areaId = buffer.readInt();
		roomId = buffer.readInt();
		iconId = buffer.readInt();
		modelId = buffer.readInt();
		modelMotionId = buffer.readInt();
		level = buffer.readInt();
		hitPoint = buffer.readInt();
		maxHitPoint = buffer.readInt();
		magicPoint = buffer.readInt();
		maxMagicPoint = buffer.readInt();
		injury = buffer.readInt();
	}
	
	public void writeTo(ByteBuffer buffer)
	{
		super.writeTo(buffer);
		buffer.writeInt(x);
		buffer.writeInt(y);
		buffer.writeInt(areaId);
		buffer.writeInt(roomId);
		buffer.writeInt(iconId);
		buffer.writeInt(modelId);
		buffer.writeInt(modelMotionId);
		buffer.writeInt(level);
		buffer.writeInt(hitPoint);
		buffer.writeInt(maxHitPoint);
		buffer.writeInt(magicPoint);
		buffer.writeInt(maxMagicPoint);
		buffer.writeInt(injury);
	}

	public boolean setHitPoint(int point,SpriteBattleTmp pbt)
	{
		hitPoint += point;
		if(hitPoint <= 0)
		{
			hitPoint = 0;
			return false;
		}
	
		if(hitPoint > maxHitPoint)
			hitPoint = maxHitPoint;
		return true;
	}
	
	public boolean setMagicPoint(int point,SpriteBattleTmp pbt)
	{
		magicPoint += point;
		
		if(magicPoint <= 0)
		{
			magicPoint = 0;
			return false;
		}
		if(magicPoint > maxMagicPoint)
			magicPoint = maxMagicPoint;
		return true;
	}
	
	
	public boolean checkMagicEnough(int point)
	{
		return magicPoint >= point;
	}
	
}
