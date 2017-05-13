package cc.lv1.rpg.gs.entity.impl.battle.effect;

import vin.rabbit.comm.GameObject;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.Utils;

/**
 * 持续效果
 * @author dxw
 *
 */
public class TimeEffect extends Effect
{
	
	/** 优先级 */
	public int priority;
	
	/** 
	 持续总类别
	 1)	常态影响：像“中毒”，不需要任何外在条件，即起作用的buff；
	 2) 适时影响：需要一定的启动条件才能起作用的buff；例如“魔法盾”，被攻击时才起作用；
	 * */
	public int influenceType;
	
	/** 正面、负面效果 */
	public int buffType;
	
	
	/** 
	1：当前最大生命力 battleMaxHP
2：当前最大精力 battleMaxMP
3：精神攻击 sptAtt
4：物理攻击 phsAtt
5：精神防御 sptDef
6：物理防御 phsDef
7：命中 hit
8：闪避 avoidance
9：公共CD speed
10：物理爆击率 phsSmiteRate
11：精神爆击率 sptSmiteRate
12：物理爆击伤害参数 phySmiteHurtParm
13：精神爆击伤害参数sptSmiteHurtParm
14：体质 physique
15：力量 power
16：敏捷 nimble
17：精神 spirit
18：智慧 wisdom

19：无法加负面buff timeEffectInvain

20：受到的X*精神伤害无效
21：受到的x*智慧伤害无效
23：眩晕  dizzy
24:攻击忽视防御 noDefAtt
25：调用智慧进行伤害
26：混乱，攻击随机目标  chaos
27：受到的伤害  damageModify
28：行为损失生命值
29：当前生命力  currentHP
30：当前精力   currentMP
	 */
	public String [] dataType = new String[3];
	
	/**
		1：数值
		2：百分比
	 */
	public int [] dataPattern = new int[3];
	
	/**
	 * 对应的效果值
	 */
	public int [] effectPoint = new int[3];
	
	/**
		0：累计，不消失
		1：累计，消失
		2：不累计，消失
	 * */
	public int [] dealMode = new int[3];
	
	
	/**
	 * 持续时间
	 */
	public long duration;
	
	/**
	 * 作用次数
	 */
	public int interval;
	
	/**
	 * 是否带出战斗 
	 */
	public boolean isOutBattle;
	
	/** buff类别(同一类别的BUFF根据等级只加一个) */
	public int type;
	
	/** 是否光环(1表示是光环) */
	public int haloType;
	
	/**
	 * 当前时间
	 */
	public long beginTime;
	
	/** 是否第一次使用 */
	public boolean isFirstUse = true;
	
	public void copyTo(GameObject gameobject)
	{
		super.copyTo(gameobject);
		TimeEffect stateEffect = (TimeEffect)gameobject;
		stateEffect.priority = priority;
		stateEffect.influenceType = influenceType;
		stateEffect.buffType = buffType;
		
		String [] stmp = new String[3];
		System.arraycopy(dataType, 0, stmp, 0, 3);
		stateEffect.dataType = stmp;
		
		int [] tmp = new int[3];
		System.arraycopy(dataPattern, 0, tmp, 0, 3);
		stateEffect.dataPattern = tmp;
		
		tmp = new int[3];
		System.arraycopy(effectPoint, 0, tmp, 0, 3);
		stateEffect.effectPoint = tmp;
		
		tmp = new int[3];
		System.arraycopy(dealMode, 0, tmp, 0, 3);
		stateEffect.dealMode = tmp;
		
		
		stateEffect.duration = duration;
		stateEffect.interval = interval;
		stateEffect.isOutBattle = isOutBattle;
		stateEffect.beginTime = beginTime;
		stateEffect.haloType = haloType;
		stateEffect.type = type;
	}
	
	public boolean setVariable(String key, String value)
	{
		if(key.equals("dataType"))
		{
			String []strs = Utils.split(value, ":");
			for(int i = 0 ; i < dataType.length ; i ++)
			{
				dataType[i] = strs[i].trim();
			}
			return true;
		}
		else if(key.equals("dataPattern"))
		{
			String []strs = Utils.split(value, ":");
			for(int i = 0 ; i < dataPattern.length ; i ++)
			{
				dataPattern[i] = Integer.parseInt(strs[i]);
			}
			return true;
		}
		else if(key.equals("effectPoint"))
		{
			String []strs = Utils.split(value, ":");
			for(int i = 0 ; i < effectPoint.length ; i ++)
			{
				effectPoint[i] = Integer.parseInt(strs[i]);
			}
			return true;
		}
		else if(key.equals("dealMode"))
		{
			String []strs = Utils.split(value, ":");
			for(int i = 0 ; i < dealMode.length ; i ++)
			{
				dealMode[i] = Integer.parseInt(strs[i]);
			}
			return true;
		}
		else if(key.equals("isOutBattle"))
		{
			isOutBattle = Integer.parseInt(value) == 1;
			return true;
		}
		else if(key.equals("duration"))
		{
			duration = Long.parseLong(value);
			return true;
		}
		else if(key.equals("type"))
		{
			type = Integer.parseInt(value);
			return true;
		}
		else
		{
			return super.setVariable(key, value);
		}
	}
	

	public void readFrom(ByteBuffer buffer)
	{

	}
	
	public void writeTo(ByteBuffer buffer)
	{

	}
	
	
	public void setBeginTime(long currentTime)
	{
		if(haloType == 1)
			setDuration(currentTime);
		beginTime = currentTime;
		
		if(interval != 0)
			avgTime = (int) (duration/interval);
	}
	
	private int avgTime;
	
	private int avgCount = 0;
	
	public void setDuration(long time)
	{
		duration = time;
	}
	
	public boolean isEndEffect(long currentTime)
	{
		return beginTime + duration <= currentTime;
	}
	
	public boolean isUseEffect(long currentTime)
	{
		if(interval == 0)
			return false;
		
		if(avgCount < interval)
		{
			if(beginTime +  avgTime * avgCount< currentTime)
			{
				avgCount++;
				return true;
			}
				
		}
		return false;
	}

	private int point[] = new int[3];
	
	public int getPoint(int index)
	{
		return point[index];
	}
	
	public void setPoint(int index,int point)
	{
		this.point[index] = point;
	}
}
