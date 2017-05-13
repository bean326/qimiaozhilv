package cc.lv1.rpg.gs.entity.ext;

import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.impl.ArrayList;
import vin.rabbit.util.collection.impl.HashMap;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.entity.controller.BattleController;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.impl.Player;
import cc.lv1.rpg.gs.entity.impl.battle.PlayerBattleTmp;
import cc.lv1.rpg.gs.entity.impl.battle.effect.Effect;
import cc.lv1.rpg.gs.entity.impl.battle.effect.TimeEffect;
/**
 * 装玩家所中的正面状态
 * @author dxw
 *
 */
public class BuffBox extends PlayerExtInfo
{
	private Player player;
	
	private ArrayList effectList = new ArrayList();

	private HashMap outBattleEffect = new HashMap(20);
	
	public long pveCDtime = 0;
	
	public BuffBox()
	{
		
	}
	
	public void clearBuff()
	{
		effectList.clear();
		outBattleEffect.clear();
	}
	
	private int getEffectListIndex(int effectId)
	{
		for (int i = 0; i < effectList.size(); i++) 
		{
			if(effectList.get(i) == null)
				continue;
			TimeEffect te = (TimeEffect) effectList.get(i);
			if(te.id == effectId)
				return i;
		}
		return -1;
	}
	
	private TimeEffect getEffectByType(int type)
	{
		for (int i = 0; i < effectList.size(); i++) 
		{
			if(effectList.get(i) == null)
				continue;
			TimeEffect te = (TimeEffect) effectList.get(i);
			if(te.type == type)
				return te;
		}
		return null;
	}
	
	private List getEffects(int type)
	{
		List list = new ArrayList();
		for (int i = 0; i < effectList.size(); i++) 
		{
			if(effectList.get(i) == null)
				continue;
			TimeEffect te = (TimeEffect) effectList.get(i);
			if(te.type == type)
				list.add(te);
		}
		return list;
	}
	
	

	public Player getPlayer()
	{
		return player;
	}

	public void setPlayer(Player player)
	{
		this.player = player;
	}

	public String getName()
	{
		return "buffBox";
	}

	public boolean addEffect(TimeEffect effect,PlayerController target)
	{
		//判断同类型BUFF
//		TimeEffect te = getEffectByType(effect.type);
		List list = getEffects(effect.type);
		if(list.size() == 0)
		{
			effectList.add(effect);
			outBattleEffect.put(effect.id,effect);
			effect.setBeginTime(WorldManager.currentTime);
//			System.out.println("BuffBox 97 add buff:"+effect.name);
			return true;
		}
		else
		{
			boolean result = true;
			for (int i = 0; i < list.size(); i++) 
			{
				TimeEffect te = (TimeEffect) list.get(i);
	
				if(te.level <= effect.level)
				{
					removeEffect(te,target);
				}
				else
				{
					result = false;
				}
			}

			if(result)
			{
				effectList.add(effect);
				outBattleEffect.put(effect.id, effect);
				effect.setBeginTime(WorldManager.currentTime);
//				System.out.println("BuffBox 128 add buff:"+effect.name);
			}
			return result;
		}
	}


	
	public void removeEffect(Effect effect,PlayerController target)
	{
		effectList.remove(effect);
		outBattleEffect.remove(effect.id);
//		System.out.println("BuffBox delete buff..... "+ effect.name);
		
		
		if(target.getParent() instanceof BattleController)
		{
			PlayerBattleTmp pbt = (PlayerBattleTmp) target.getAttachment();
			pbt.fixPlayerData();
			
			if(effect instanceof TimeEffect)
			{
				if(((TimeEffect)effect).interval == 0)
					target.sendAlwaysValue();
			}
		}
	}

	public void loadFrom(ByteBuffer byteBuffer)
	{

	}

	public void saveTo(ByteBuffer byteBuffer)
	{

	}
	
	
	
	
	//更新buff 状态并发送伤害到客户端....
	public void update(long currentMillis)
	{

	}


	
	public boolean updateBattleBuffBox(PlayerController target)
	{
		int size = effectList.size();
		ArrayList removeList = new ArrayList(10);
		
		long currentTime = WorldManager.currentTime;//System.currentTimeMillis();

		for(int i = 0  ;i < size ; i ++)
		{
			Object object = effectList.get(i);
			
			if(!(object instanceof TimeEffect))
				continue;
			
			TimeEffect timeEffect =	(TimeEffect)object;

			if(!timeEffect.isOutBattle)
			{
				removeList.add(timeEffect);
				continue;
			}
			
			if(timeEffect.isEndEffect(currentTime))
			{
				removeList.add(timeEffect);
				continue;
			}
			
			if(timeEffect.haloType == 1)
			{
				removeList.add(timeEffect);
				continue;
			}
		}
		size = removeList.size();
		
		for (int i = 0; i < size ; i++)
		{
			TimeEffect timeEffect =	(TimeEffect)removeList.get(i);
		
			removeEffect(timeEffect,target);
		}
		
		return size > 0;
		
	}
	
	public List getEffectList()
	{
		return this.effectList;
	}
	
	public List getHaloList()
	{
		List list = new ArrayList();
		for (int i = 0; i < effectList.size(); i++)
		{
			Effect effect = (Effect) effectList.get(i);
			if(effect == null)
				continue;
			if(effect instanceof TimeEffect)
			{
				TimeEffect te = (TimeEffect) effect;
				if(te.haloType == 1)
					list.add(te);
			}
		}
		return list;
	}

}
