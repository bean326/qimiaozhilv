package cc.lv1.rpg.gs.entity.impl.story;

import vin.rabbit.comm.GameObject;
import vin.rabbit.util.Utils;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.RPGameObject;

/**
 * 事件组
 * @author Administrator
 *
 */
public class EventTeam extends RPGameObject
{
	/** 0.综合 1.战斗 2.探险 3.游戏 4.寻宝 5.答题 */
	public int type;
	
	/** 事件列表 */
	public int[] events;
	
	public void copyTo(GameObject go)
	{
		super.copyTo(go);
		EventTeam tt = (EventTeam) go;
		
		tt.type = type;
		
		int[] temp = new int[events.length];
		System.arraycopy(events, 0, temp, 0, temp.length);
		tt.events = temp;
	}
	
	public boolean setVariable(String key,String value)
	{
		if("events".equals(key))
		{
			String[] strs = Utils.split(value, ":");
			events = new int[strs.length];
			for (int i = 0; i < strs.length; i++) 
			{
				events[i] = Integer.parseInt(strs[i]);
				Event event = (Event) DataFactory.getInstance().getStoryObject(events[i]);
				if(event == null)
				{
					System.out.println("EventTeam setVarible event is null:"+events[i]);
				}
			}
			return true;
		}
		else
			return super.setVariable(key, value);
	}
}
