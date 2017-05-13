package cc.lv1.rpg.gs.entity.impl.answer;

import vin.rabbit.comm.GameObject;
import cc.lv1.rpg.gs.entity.RPGameObject;

public class Question extends RPGameObject
{
	/** 1.洗脑题(游戏规范题) 2.游戏常识 3.其它 4.泡泡答题 5.剧情题目*/
	public int type;
	
	/** 答案 */
	public int answer;
	
	/** 是否回答过(不COPY) */
	public boolean isAnswered = false;
	
	/** 答题次数(这道题答了多少次，不copy) */
	public int questionCount;
	
	/**
	 * 回答是否正确
	 * @param ans
	 * @return
	 */
	public boolean isRight(int ans)
	{
		return answer == ans;
	}
	
	public void setQuestionCount(int count)
	{
		this.questionCount = count;
	}
	
	public int getQuestionCount()
	{
		return this.questionCount;
	}
	
	public void setAnswered(boolean flag)
	{
		this.isAnswered = flag;
	}
	
	public void copyTo(GameObject go)
	{
		super.copyTo(go);
		Question q = (Question) go;
		
		q.type = type;
		q.answer = answer;
	}
}
