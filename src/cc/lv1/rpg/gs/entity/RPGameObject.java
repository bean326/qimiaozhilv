package cc.lv1.rpg.gs.entity;

import vin.rabbit.comm.GameObject;
/**
 * rpg游戏对象
 * @author dxw
 *
 */
public class RPGameObject extends GameObject
{
	public static GameObject cloneObject(GameObject src)
	{
		try 
		{
			String clsName = src.getClass().getName();
			GameObject obj = (GameObject)Class.forName(clsName).newInstance();
			src.copyTo(obj);
			return obj;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		} 
		return null;
	}

}
