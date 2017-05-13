package cc.lv1.rpg.gs.entity.impl.pet;

import vin.rabbit.comm.GameObject;
import cc.lv1.rpg.gs.entity.RPGameObject;

/**
 * 宠物变身规则
 * @author bean
 *
 */
public class PetUpRule extends RPGameObject
{
	/** 宠物等级 */
	public int level;
	
	/** 变身状态 */
	public int upState;
	
	/** 变身动画 */
	public String upModelId;
	
	/** 变身路线 */
	public String upWay;
	
	/** 属性选择 */
	public String upAtt;
	
	public void copyTo(GameObject go)
	{
		super.copyTo(go);
		PetUpRule pur = (PetUpRule) go;
		
		pur.level = level;
		pur.upState = upState;
		pur.upModelId = upModelId;
		pur.upWay = upWay;
		pur.upAtt = upAtt;
	}
}
