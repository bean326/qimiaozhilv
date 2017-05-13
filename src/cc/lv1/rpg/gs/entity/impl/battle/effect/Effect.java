package cc.lv1.rpg.gs.entity.impl.battle.effect;

import vin.rabbit.comm.GameObject;
import vin.rabbit.util.ByteBuffer;
import cc.lv1.rpg.gs.entity.RPGameObject;
/**
 * 效果
 * @author dxw
 *
 */
public abstract class Effect extends RPGameObject
{
	/** 效果等级 */
	public int level;

	/** 目标类型 */
	transient public int targetType;
	
	/** 效果几率 */
	transient public int range;
	
	/** 跟辅助值相关(1表示相关 2是否跟暴击相关) */
	public int cureType;
	
	/** 效果释放者释放效果时的辅助值，在重新计算BUFF值的时候要加成 */
	public int helpPoint;
	
	/** 加法 */
	public static final int EFFECT_ADD = 0x01;
	
	/** 减法 */
	public static final int EFFECT_SUB = 0X02;
	
	public void copyTo(GameObject gameobject)
	{
		super.copyTo(gameobject);
		Effect effect = (Effect)gameobject;
		effect.level = level;
		effect.cureType = cureType;
		effect.helpPoint = helpPoint;
	}
	
	public void readFrom(ByteBuffer buffer)
	{
		super.readFrom(buffer);
		level = buffer.readByte();
	}
	
	public void writeTo(ByteBuffer buffer)
	{
		super.writeTo(buffer);
		buffer.writeByte(level);
	}

	public int getLevel()
	{
		return level;
	}

	public void setLevel(int level)
	{
		this.level = level;
	}

	public int getTargetType()
	{
		return targetType;
	}

	public void setTargetType(int targetType)
	{
		this.targetType = targetType;
	}

	public int getRange()
	{
		return range;
	}

	public void setRange(int range)
	{
		this.range = range;
	}
	
}
