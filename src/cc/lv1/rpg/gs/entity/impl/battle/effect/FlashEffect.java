package cc.lv1.rpg.gs.entity.impl.battle.effect;

import vin.rabbit.comm.GameObject;
import vin.rabbit.util.ByteBuffer;

/**
 * 瞬时效果
 * @author dxw
 *
 */
public class FlashEffect extends Effect
{
	public static final int FE_TYPE_ATTACK = 1;
	public static final int FE_TYPE_CURE = 2;
	public static final int FE_TYPE_DAMAGE = 3;
	public static final int FE_TYPE_WISDOM_DAMAGE = 4;
	public static final int FE_TYPE_SPIRIT_DAMAGE= 5;
	public static final int FE_TYPE_DRUG_RELIVE = 6;
	public static final int FE_TYPE_ATTACK_DAMAGE = 7;
	public static final int FE_TYPE_SPIRIT_CURE = 8;
	public static final int FE_TYPE_CLEAR_CDTIME = 9;
	public static final int FE_TYPE_CANCEL_NEGBUFF = 10;
	public static final int FE_TYPE_NOSMITE_DAMAGE = 11;
	
	/** 1：攻击
2：治疗
3：固定伤害
		4：调用智慧瞬时伤害
			5：调用精神瞬时伤害
			6：x%血蓝复活
			7：调用攻击瞬时伤害
			8：调用精神进行治疗
			9：清除CD
			10：取消所有负面效果
			11: 不受暴击率影响的固定伤害
	 */
	public int type;
	
	
	/**
		1：当前生命力
		2：当前精力
		3：生命力，精力
	 */
	public int dataType;
	
	
	/**
		1：数值
		2：百分比
	 */
	public int dataPattern;
	
	/**
	 *  具体值
	 */
	public int effectPoint; 
	
	
	public void copyTo(GameObject gameobject)
	{
		super.copyTo(gameobject);
		
		FlashEffect attackEffect = (FlashEffect)gameobject;
		attackEffect.type = type;
		attackEffect.dataType = dataType;
		attackEffect.dataPattern = dataPattern;
		attackEffect.effectPoint = effectPoint;
	}
	
	public void readFrom(ByteBuffer buffer)
	{
		super.readFrom(buffer);
		type =  buffer.readInt();
		dataType =  buffer.readInt();
		dataPattern = buffer.readInt();
		effectPoint = buffer.readInt();
	}
	
	public void writeTo(ByteBuffer buffer)
	{
		super.writeTo(buffer);
		buffer.writeInt(type);
		buffer.writeInt(dataType);
		buffer.writeInt(dataPattern);
		buffer.writeInt(effectPoint);
	}

	public int getType()
	{
		return type;
	}

	public void setType(int type)
	{
		this.type = type;
	}

	public int getDataType()
	{
		return dataType;
	}

	public void setDataType(int dataType)
	{
		this.dataType = dataType;
	}

	public int getDataPattern()
	{
		return dataPattern;
	}

	public void setDataPattern(int dataPattern)
	{
		this.dataPattern = dataPattern;
	}

	public int getEffectPoint()
	{
		return effectPoint;
	}

	public void setEffectPoint(int effectPoint)
	{
		this.effectPoint = effectPoint;
	}

}
