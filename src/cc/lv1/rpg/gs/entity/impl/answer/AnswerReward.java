package cc.lv1.rpg.gs.entity.impl.answer;

import cc.lv1.rpg.gs.entity.RPGameObject;

/**
 * 答题奖励
 * @author bean
 *
 */
public class AnswerReward extends RPGameObject
{
	public int minLevel;
	
	public int maxLevel;
	
	/** 经验奖励 */
	public long expReward;
	
	public boolean setVariable(String key,String value)
	{
		if(key.equals("expReward"))
		{
			expReward = Long.parseLong(value);
			return true;
		}
		else
			return super.setVariable(key, value);
	}
}
