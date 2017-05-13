package cc.lv1.rpg.gs.entity.impl.battle;



import vin.rabbit.comm.GameObject;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.impl.ArrayList;
import vin.rabbit.util.collection.impl.HashMap;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.controller.BattleController;
import cc.lv1.rpg.gs.entity.controller.CopyPVEController;
import cc.lv1.rpg.gs.entity.controller.EventPVEController;
import cc.lv1.rpg.gs.entity.controller.MonsterController;
import cc.lv1.rpg.gs.entity.controller.PVEController;
import cc.lv1.rpg.gs.entity.controller.PVPController;
import cc.lv1.rpg.gs.entity.controller.PartyPKController;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.controller.SpriteController;
import cc.lv1.rpg.gs.entity.ext.BuffBox;
import cc.lv1.rpg.gs.entity.ext.EquipSet;
import cc.lv1.rpg.gs.entity.ext.PlayerBaseInfo;
import cc.lv1.rpg.gs.entity.ext.SkillTome;
import cc.lv1.rpg.gs.entity.i.Sprite;
import cc.lv1.rpg.gs.entity.impl.Goods;
import cc.lv1.rpg.gs.entity.impl.Monster;
import cc.lv1.rpg.gs.entity.impl.MonsterDropProp;
import cc.lv1.rpg.gs.entity.impl.Player;
import cc.lv1.rpg.gs.entity.impl.battle.effect.Effect;
import cc.lv1.rpg.gs.entity.impl.battle.effect.FlashEffect;
import cc.lv1.rpg.gs.entity.impl.battle.effect.TimeEffect;
import cc.lv1.rpg.gs.entity.impl.battle.skill.ActiveSkill;
import cc.lv1.rpg.gs.entity.impl.battle.skill.Skill;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsEquip;
import cc.lv1.rpg.gs.gui.MainFrame;
import cc.lv1.rpg.gs.net.SMsg;

/**
 * 精灵临时数据
 * @author dxw
 *
 */
public abstract class SpriteBattleTmp extends GameObject
{
	/** 暴击倍数 */
	public static final double SMITERATE = 1.5;
	
	/** 浮动伤害min max */
	public static final double MINDAMAGE = 0.9;
	public static final double MAXDAMAGE = 1.1;
	
	/***
	 * 用来存带出战斗的buff
	 */
	protected BuffBox buffBox;
	
	protected int teamNo; 
	
	protected int index;
	
	protected PlayerBaseInfo baseInfo;
	
	private ByteBuffer effectBuffer = new ByteBuffer(10);
	
	protected SpriteController currentController;
	
	protected Sprite currentSprite;
	
	protected PlayerController targetPlayer = null;
	
	protected MonsterController targetMonster = null;
	
	protected BattleController battle = null;
	
	protected int effectCount = 0;
	
	protected boolean isLocked = false;
	
	protected int pCdTimer = 0;
	
	public long lastPingTime = 0;
	
	protected boolean isAction = true;
	
	public void setCDTimer(int pCdTimer)
	{
		this.pCdTimer = pCdTimer;
	}
	
	public int getCDTimer()
	{
		return this.pCdTimer;
	}
	
	public boolean isLocked()
	{
		return isLocked;
	}

	public void setLocked(boolean isLocked)
	{
		this.isLocked = isLocked;
	}

	public BattleController getBattle()
	{
		return battle;
	}

	public void setBattle(BattleController battle)
	{
		this.battle = battle;
	}

	/**
	 * 战斗临时的buff
	 */
	protected ArrayList effectList = new ArrayList();
	
	private HashMap inBattleEffect = new HashMap(20);
	
	private boolean isDead = false;

	public void setIndex(int i)
	{
		index = i;
	}
	
	public int getIndex()
	{
		return index;
	}
	
	public boolean isDead()
	{
		return isDead;
	}

	public void setDead(boolean isDead)
	{
		this.isDead = isDead;
//		System.out.println("154 setDead:"+currentSprite.name+"   死状态:"+isDead+"  当前生命:"+currentSprite.hitPoint);
		if(!isDead)
			return;
		
		removeBuff(currentController);
		
//		sendBattleSpriteInfo();
		
		ByteBuffer buffer = new ByteBuffer(2);
		buffer.writeByte(teamNo);
		buffer.writeByte(index);
		battle.dispatchMsg(SMsg.S_BATTLE_DIE, buffer);
	}
	
	public void setTeamNo(int teamNo)
	{
		this.teamNo = teamNo;
	}
	
	public int getTeamNo()
	{
		return teamNo;
	}
	
	/**
	 * 特殊技能....
	 * @param effect
	 * @param isAdd
	 */
	private void changeUnusualEffectState(TimeEffect effect,boolean isAdd)
	{
		int length = effect.dataType.length;
		for (int i = 0; i < length; i++)
		{
			if(effect.dataType[i].equals("0"))
				break;
			
			if(effect.dataType[i].equals("chaos")) //混乱
			{
				isChaos = isAdd;
				isBeforeUnusualEffect = isAdd;
				break;
			}
			else if(effect.dataType[i].equals("damageModify")) //伤害修改
			{
				if(isAdd)
					damageTimeEffect = effect;
				else
					damageTimeEffect = null;
				break;
			}
			else if(effect.dataType[i].equals("timeEffectInvain"))
			{
				isTimeEffectInvain = isAdd;
			}
		}
	}
	
	protected boolean isBeforeUnusualEffect = false;
	
	protected boolean isTimeEffectInvain = false;
	
	protected boolean isChaos = false;
	
	protected TimeEffect damageTimeEffect = null;

	protected boolean checkUnusualEffect()
	{
		PVEController pve = null;
		PVPController pvp = null;
		PartyPKController ppk = null;
		CopyPVEController copy = null;
		EventPVEController event = null;
		
		if(battle instanceof PVEController)
		{
			pve = (PVEController)battle;
		}
		else if(battle instanceof CopyPVEController)
		{
			copy = (CopyPVEController)battle;
		}
		else if(battle instanceof EventPVEController)
		{
			event = (EventPVEController)battle;
		}
		else if(battle instanceof PVPController)
		{
			pvp = (PVPController)battle;
		}
		else if(battle instanceof PartyPKController)
		{
			ppk = (PartyPKController)battle;
		}

		if(isChaos && isBeforeUnusualEffect) //混乱
		{
			if(pve != null)
			{
				MonsterController [] monsters = pve.getMonsters();
				
				for (int i = 0; i < monsters.length; i++)
				{
					if(monsters[i].getAttachment().isDead)
						continue;
					
					if(((int)(Math.random()*4)) == 3)
					{
						setSkillTarget(monsters[i]);
						return true;
					}	
				}
				
				PlayerController []players = pve.getPlayers();
				
				for (int i = 0; i < players.length; i++)
				{
					if(players[i].getAttachment().isDead)
						continue;
					
					if(((int)(Math.random()*4)) == 3)
					{
						setSkillTarget(players[i]);
						return true;
					}	
				}
			}
			else if(copy != null)
			{
				MonsterController [] monsters = copy.getMonsters();
				
				for (int i = 0; i < monsters.length; i++)
				{
					if(monsters[i].getAttachment().isDead)
						continue;
					
					if(((int)(Math.random()*4)) == 3)
					{
						setSkillTarget(monsters[i]);
						return true;
					}	
				}
				
				PlayerController []players = copy.getPlayers();
				
				for (int i = 0; i < players.length; i++)
				{
					if(players[i].getAttachment().isDead)
						continue;
					
					if(((int)(Math.random()*4)) == 3)
					{
						setSkillTarget(players[i]);
						return true;
					}	
				}
			}
			else if(event != null)
			{
				MonsterController [] monsters = event.getMonsters();
				
				for (int i = 0; i < monsters.length; i++)
				{
					if(monsters[i].getAttachment().isDead)
						continue;
					
					if(((int)(Math.random()*4)) == 3)
					{
						setSkillTarget(monsters[i]);
						return true;
					}	
				}
				
				PlayerController []players = event.getPlayers();
				
				for (int i = 0; i < players.length; i++)
				{
					if(players[i].getAttachment().isDead)
						continue;
					
					if(((int)(Math.random()*4)) == 3)
					{
						setSkillTarget(players[i]);
						return true;
					}	
				}
			}
			else if(pvp != null)
			{
				PlayerController [] targetPlayers = pvp.getTargetPlayers();
				
				for (int i = 0; i < targetPlayers.length; i++)
				{
					if(targetPlayers[i].getAttachment().isDead)
						continue;
					
					if(((int)(Math.random()*4)) == 3)
					{
						setSkillTarget(targetPlayers[i]);
						return true;
					}	
				}
				
				PlayerController [] players = pvp.getPlayers();
				
				for (int i = 0; i < players.length; i++)
				{
					if(players[i].getAttachment().isDead)
						continue;
					
					if(((int)(Math.random()*4)) == 3)
					{
						setSkillTarget(players[i]);
						return true;
					}	
				}
			}
			else if(ppk != null)
			{
				PlayerController [] targetPlayers = ppk.getTargetPlayers();
				
				for (int i = 0; i < targetPlayers.length; i++)
				{
					if(targetPlayers[i].getAttachment().isDead)
						continue;
					
					if(((int)(Math.random()*4)) == 3)
					{
						setSkillTarget(targetPlayers[i]);
						return true;
					}	
				}
				
				PlayerController [] players = ppk.getPlayers();
				
				for (int i = 0; i < players.length; i++)
				{
					if(players[i].getAttachment().isDead)
						continue;
					
					if(((int)(Math.random()*4)) == 3)
					{
						setSkillTarget(players[i]);
						return true;
					}	
				}
			}
		}
		return false;
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
	
	
	public boolean addEffect(TimeEffect effect)
	{
		List list = getEffects(effect.type);
		if(list.size() == 0)
		{
			effectList.add(effect);
			inBattleEffect.put(effect.id,effect);
			effect.setBeginTime(WorldManager.currentTime);
			changeUnusualEffectState(effect,true);
//			System.out.println("SpriteBattleTmp 315 buff..... "+ effect.name+"  玩家："+currentSprite.name);
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
					removeEffect(te);
				}
				else
				{
					result = false;
				}
			}
			if(result)
			{
				effectList.add(effect);
				inBattleEffect.put(effect.id, effect);
				effect.setBeginTime(WorldManager.currentTime);
				changeUnusualEffectState(effect,true);
//				System.out.println("SpriteBattleTmp 340 buff..... "+ effect.name);
			}
			return result;
		}
	}
	
	public void deleteEffect(TimeEffect effect)
	{
		
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
	
	/**
	 * 删除效果  子类掉用,PlayerBattleTmp重写了的
	 * @param effect
	 */
	public void removeEffect(TimeEffect effect)
	{
		effectList.remove(effect);
		inBattleEffect.remove(effect.id);
		changeUnusualEffectState(effect,false);
		if(currentController instanceof PlayerController)
		{
			if(effect.interval == 0)
				((PlayerController)currentController).sendAlwaysValue();
		}
		
		
//		System.out.println("SpriteBattleTmp 371 删除战斗内 buff..... "+ effect.name+"   "+currentSprite.name);
	}
	
	/**
	 * 修正数据
	 */
	public void fixPlayerData()
	{
	}
	
	public int EffectSize()
	{
		return effectList.size();
	}
	
	public boolean isEmptyEffect()
	{
		return EffectSize() <= 0;
	}

	public ByteBuffer getEffectBuffer()
	{
		return effectBuffer;
	}
	
	public void addEffectBuffer(ByteBuffer effectBuff)
	{
		if(effectBuff != null)
		{
			effectBuffer.writeBytes(effectBuff.getBytes());
			effectCount++;
		}
		else
		{
			effectBuffer = new ByteBuffer(10);
			effectCount = 0;
		}
	}
	
	public int getEffectCount()
	{
		return effectCount;
	}

	public void update(long timeMillis)
	{
		if(isAction)
			return;
		
		if(lastPingTime == 0)
		{
			lastPingTime = timeMillis;
			return;
		}
		
		if(lastPingTime + pCdTimer < timeMillis)
			isAction = true;
	}
	
	public abstract boolean processActiveSkill(ActiveSkill skill);
	
	
	/**
	 * 取得单纯伤害
	 * @param effect
	 * @return
	 */
	public int getDamageOnly(FlashEffect effect)
	{
		int result = 0;
		if(effect.dataPattern == 1) //数值
		{
			if(effect.dataType == 1) //当前生命力
			{
				result = effect.effectPoint;
				result = setDamage(result);
			}
			else if(effect.dataType == 2)  //当前精力
			{
				result = effect.effectPoint;
				currentSprite.setMagicPoint(result,this);
			}
		}
		else if(effect.dataPattern == 2)//百分比
		{
			if(effect.dataType == 1) //当前生命力
			{
				result = (int) (currentSprite.hitPoint * (double)effect.effectPoint / 100);
				result = setDamage(result);
			}
			else if(effect.dataType == 2)  //当前精力
			{
				int mmp = 0;
				if(currentSprite instanceof Player)
				{
					PlayerBattleTmp pbt = (PlayerBattleTmp) currentController.getAttachment();
					mmp = pbt.maxMagicPoint;
				}
				else if(currentSprite instanceof Monster)
				{
					mmp = currentSprite.maxMagicPoint;
				}
				result = (int) (mmp * (double)effect.effectPoint / 100);
				currentSprite.setMagicPoint(result,this);
			}
		}
		
		if(result >= 0)
			result = -1;
		return result;
	}

	public int getDamage(int damage)
	{
		if(damageTimeEffect != null)
		{
			int length = damageTimeEffect.dataType.length;
			for (int i = 0; i < length; i++)
			{
				if(damageTimeEffect.dataType[i].equals("0"))
					break;
				
				if(damageTimeEffect.dataType[i].equals("damageModify")) //伤害修改
				{
					if(damageTimeEffect.dataPattern[i] == 1) //数值
					{
						damage += damageTimeEffect.effectPoint[i]; 
					}
					else if(damageTimeEffect.dataPattern[i] == 2)//百分比
					{
						damage += Math.abs(damage) * (double)damageTimeEffect.effectPoint[i] /100;
					}
					break;
				}
			}
		}
		return damage;
	}
	
	
	public int setDamage(int damage)
	{
		if(damageTimeEffect != null)
		{
			int length = damageTimeEffect.dataType.length;
			for (int i = 0; i < length; i++)
			{
				if(damageTimeEffect.dataType[i].equals("0"))
					break;
				
				if(damageTimeEffect.dataType[i].equals("damageModify")) //伤害修改
				{
					if(damageTimeEffect.dataPattern[i] == 1) //数值
					{
						damage += damageTimeEffect.effectPoint[i]; 
					}
					else if(damageTimeEffect.dataPattern[i] == 2)//百分比
					{
						damage += Math.abs(damage) * (double)damageTimeEffect.effectPoint[i] /100;
					}
					break;
				}
			}
		}

		setDead(!currentSprite.setHitPoint(damage,this));
	
		return damage;
	}
	

	
	protected void processTimeEffectExt(TimeEffect effect)
	{
		for (int i = 0; i < effect.dataType.length; i++) 
		{
			if(effect.dataType[i].equals("currentHP")) //加减血
			{
				int damage = 0;
				if(effect.dataPattern[i] == 1)
					damage = getDamage(effect.effectPoint[i]);
				else if(effect.dataPattern[i] == 2)
				{
					if(currentController instanceof PlayerController)
					{
						PlayerController target = (PlayerController) currentController;
						damage = (int) (target.getEquipPoint("maxHitPoint") * (double)effect.effectPoint[i]/100);
					}
				}
//PlayerBattleTmp pbt = (PlayerBattleTmp) currentController.getAttachment();
//System.out.println(currentSprite.name+"1processTimeEffectExt:当前生命:"+currentSprite.hitPoint+"  死亡："+isDead+" 伤害:"+damage+" 辅助值:"+pbt.curePoint);
				if(damage > 0 && isDead())
					continue;
				
				setDamage(damage/effect.interval);
//System.out.println(currentSprite.name+"2processTimeEffectExt:当前生命:"+currentSprite.hitPoint+"  死亡："+isDead+"  i:"+i);
				
				sendBattleSpriteInfo();

				battle.checkDeadState();
			}
			else if(effect.dataType[i].equals("currentMP")) //加减魔力
			{
				int point = 0;
				if(effect.dataPattern[i] == 1)
					point = effect.effectPoint[i];
				else if(effect.dataPattern[i] == 2)
				{
					if(currentController instanceof PlayerController)
					{
						PlayerController target = (PlayerController) currentController;
						point = (int) (target.getEquipPoint("maxMagicPoint") * (double)effect.effectPoint[i]/100);
					}
				}
				currentSprite.setMagicPoint(point/effect.interval,this);
				
				sendBattleSpriteInfo();
			}
			else if(effect.dataType[i].equals("dizzy")) //眩晕
			{
				setLocked(true);
			}
		}
	}

	public void processTimeEffectEndExt(TimeEffect effect)
	{
		for (int i = 0; i < effect.dataType.length; i++) 
		{
//			if(effect.dataType[i].equals("currentHP")) //加减血
//			{
//				if(effect.isFirstUse)
//				{
//					effect.isFirstUse = false;
//					continue;
//				}
//				setDamage(effect.effectPoint[i]/effect.interval);
//			}
//			else if(effect.dataType[i].equals("currentMP")) //加减魔力
//			{
//				if(effect.isFirstUse)
//				{
//					effect.isFirstUse = false;
//					continue;
//				}
//				currentSprite.setMagicPoint(effect.effectPoint[i]/effect.interval);
//			}
//			else 
			if(effect.dataType[i].equals("dizzy")) //眩晕
			{
				setLocked(false);
			}
		}
	}
	
	/**
	 * 加血效果
	 * @param targetSprite
	 * @param flashEffect
	 * @return
	 */
	public int pointUp(FlashEffect flashEffect)
	{
		int resultPoint = 0;

		if(flashEffect.dataPattern == 1) //数值
		{
			if(flashEffect.dataType == 1) //当前生命力
			{
				resultPoint += flashEffect.effectPoint;
				currentSprite.setHitPoint(resultPoint,this);
			}
			else if(flashEffect.dataType == 2)  //当前精力
			{
				resultPoint += flashEffect.effectPoint;
				currentSprite.setMagicPoint(resultPoint,this);
			}
			else if(flashEffect.dataType == 3)//生命力，精力
			{
				resultPoint += flashEffect.effectPoint;
				currentSprite.setHitPoint(resultPoint,this);
				currentSprite.setMagicPoint(resultPoint,this);
			}
		}
		else if(flashEffect.dataPattern == 2)//百分比
		{
			if(flashEffect.dataType == 1) //当前生命力
			{
				int mhp = 0;
				if(currentSprite instanceof Player)
				{
					PlayerBattleTmp pbt = (PlayerBattleTmp) currentController.getAttachment();
					mhp = pbt.maxHitPoint;
				}
				else if(currentSprite instanceof Monster)
				{
					mhp = currentSprite.maxHitPoint;
				}
				resultPoint += mhp * (double)flashEffect.effectPoint / 100;
				currentSprite.setHitPoint(resultPoint,this);
			}
			else if(flashEffect.dataType == 2)  //当前精力
			{
				int mmp = 0;
				if(currentSprite instanceof Player)
				{
					PlayerBattleTmp pbt = (PlayerBattleTmp) currentController.getAttachment();
					mmp = pbt.maxMagicPoint;
				}
				else if(currentSprite instanceof Monster)
				{
					mmp = currentSprite.maxMagicPoint;
				}
				resultPoint += mmp * (double)flashEffect.effectPoint / 100;
				currentSprite.setMagicPoint(resultPoint,this);
			}
			else if(flashEffect.dataType == 3)//生命力，精力
			{
				int mmp = 0,mhp = 0;
				if(currentSprite instanceof Player)
				{
					PlayerBattleTmp pbt = (PlayerBattleTmp) currentController.getAttachment();
					mmp = pbt.maxMagicPoint;
					mhp = pbt.maxHitPoint;
				}
				else if(currentSprite instanceof Monster)
				{
					mmp = currentSprite.maxMagicPoint;
					mhp = currentSprite.maxHitPoint;
				}
				resultPoint = (int) (mmp * (double)flashEffect.effectPoint / 100);
				currentSprite.setMagicPoint(resultPoint,this);
				
				resultPoint = (int) (mhp * (double)flashEffect.effectPoint / 100);
				currentSprite.setHitPoint(resultPoint,this);
			}
		}
		return resultPoint;	
	}
	
	/**
	 * 设置技能释放对象
	 */
	public void setSkillTarget(SpriteController target)
	{
		targetPlayer = null;
		targetMonster = null;
		if(target instanceof PlayerController)
			targetPlayer = (PlayerController)target;
		else if(target instanceof MonsterController)
			targetMonster = (MonsterController)target;
	}

	
	public int getSkillTargeType()
	{
		if(targetPlayer != null)
			return ActiveSkill.TARGET_TYPE_PLAYERS;
		else if(targetMonster != null)
			return ActiveSkill.TARGET_TYPE_MONSTERS;
		else 
			return ActiveSkill.ERROR;
	}

	public void setSpriteController(SpriteController currentController)
	{
		this.currentController = currentController;	
	}
	
	public void setSprite(Sprite currentSprite)
	{
		this.currentSprite = currentSprite;
	}
	

	
	//给予百分之30的随机浮动伤害
	public int getRamdomDamage(int damage)
	{
		int min = (int) ((double)damage * MINDAMAGE);
		int max = (int) ((double)damage * MAXDAMAGE);
		int result = (int) (Math.random() * (max - min) + min);
		return result;
//		int t = damage / 3;
//		return (int)(Math.random()*(t*2))+(damage-t);
	}
	

	
	
	
	
	
	
	
	

	
	
	
	
	
	
	//-----------------------------------------------------------------------------------------
	
	/**
	 * 当瞬时效果对怪物和玩家使用
	 * @return
	 */
	public void onFlashEffectUse(FlashEffect effect)
	{
		int targetType = getSkillTargeType();
		
		boolean isSmite = false;

		if(targetType == Skill.TARGET_TYPE_PLAYERS) //目标是玩家
		{
			PlayerBattleTmp pbt = (PlayerBattleTmp)targetPlayer.getAttachment();
			
			ByteBuffer buffer = new ByteBuffer(10);
			buffer.writeByte(effect.type);

			if(effect.type == FlashEffect.FE_TYPE_ATTACK) //攻击
			{
				int rdp = 0;
				if(currentController instanceof PlayerController)
				{
					if(baseInfo.isNoDefAtt())
					{
//						int job = ((PlayerController)currentController).getPlayer().upProfession;
						int job = ((Player)currentSprite).getJobType();
						PlayerBattleTmp thisTmp = (PlayerBattleTmp) currentController.getAttachment();
//						if(job == 1 || job == 3)
						if(job == 1)
							rdp = thisTmp.phyAtt;
//						else if(job == 6 || job == 7)
						else if(job == 2)
							rdp = thisTmp.sptAtt;
						rdp = pbt.setDamage(-rdp);
					}
					else
					{
						rdp = getTotalDamageByPlayer(effect);
						if(rdp != -1)
							rdp = getRamdomDamage(rdp);
						
//						System.out.println("没加辅助值的伤害："+rdp);
						/***********************************************************/
						rdp += getHelpPoint(effect, rdp);
						/***********************************************************/

						isSmite = isSmite(currentController.getAttachment(),pbt);
						if(isSmite)
							rdp *= getSmiteMult();
//							System.out.println("对玩家造成固定伤害："+rdp);

						rdp = pbt.setDamage(rdp);
//							System.out.println("对玩家造成伤害："+rdp);
					}
				}
				else if(currentController instanceof MonsterController)
				{
					MonsterController mc = (MonsterController) currentController;

					if(mc.getMonster().isNoDefAtt())
					{
						rdp = ((MonsterBattleTmp)currentController.getAttachment()).getDamagePointByHit(0, 0);
						rdp = pbt.setDamage(rdp);
					}
					else
					{
						rdp = getTotalDamageByPlayer(effect);
//							System.out.println("对玩家造成固定伤害："+rdp);
						rdp = pbt.setDamage(rdp);
//							System.out.println("对玩家造成伤害："+rdp);
					}
				}

				buffer.writeInt(rdp);
			}
			else if(effect.type == FlashEffect.FE_TYPE_CURE) //治疗
			{
				setAddLifePlayer();
		
				int curePoint = pbt.pointUp(effect);
		
				int helpPoint = getHelpPoint(effect, curePoint);
			
				if(effect.dataType == 1)
				{
					targetPlayer.getPlayer().setHitPoint(helpPoint,pbt);
					buffer.writeByte(1);
					buffer.writeInt(curePoint+helpPoint);
				}
				else if(effect.dataType == 2)
				{
					targetPlayer.getPlayer().setMagicPoint(helpPoint,pbt);
					buffer.writeByte(2);
					buffer.writeInt(curePoint+helpPoint);
				}
				else if(effect.dataType == 3)
				{
					int mPoint = 0;
					buffer.writeByte(3);
					if(effect.dataPattern == 1)
					{
						mPoint = effect.effectPoint;
					}
					else if(effect.dataPattern == 2)
					{
						int mmp = (int) (pbt.maxMagicPoint * (double)effect.effectPoint / 100);
						mPoint = mmp + getHelpPoint(effect, mmp);
					}
		
					targetPlayer.getPlayer().setHitPoint(helpPoint,pbt);
					targetPlayer.getPlayer().setMagicPoint(helpPoint,pbt);
			
					buffer.writeInt(curePoint+helpPoint);
					buffer.writeInt(mPoint);
				}
//				System.out.println("加血or加蓝："+curePoint);
//				System.out.println("治疗值："+p);
//				buffer.writeInt(curePoint);
			}
			else if(effect.type == FlashEffect.FE_TYPE_DAMAGE) //伤害
			{
				int damage = pbt.getDamageOnly(effect);
				
				/***********************************************************/
				int helpPoint = getHelpPoint(effect, damage);
				pbt.setDead(!pbt.getPlayer().setHitPoint(helpPoint,pbt));
				damage += helpPoint;
				/***********************************************************/	
				
				isSmite = isSmite(currentController.getAttachment(),pbt);
				if(isSmite)
				{
					int bDamage = damage;
					damage *= getSmiteMult();
					if(Math.abs(damage) > Math.abs(bDamage))
					{
						//bDamage是之前已经对目标设置了的伤害，这里要减去
						pbt.setDead(!pbt.getPlayer().setHitPoint(damage-bDamage,pbt));
					}
				}
				
				damage += pbt.clearNoDefHurt;//因为damage是负数,被攻击方的抗忽视伤害是正数,所以这里算抗忽视伤害需要用加
				if(damage >= 0)
					damage = -1;
				
				buffer.writeInt(damage);
			}
			else if(effect.type == FlashEffect.FE_TYPE_WISDOM_DAMAGE)//调用智慧瞬时伤害
			{
				if(currentController instanceof PlayerController)
				{
					PlayerBattleTmp thisTmp = (PlayerBattleTmp) currentController.getAttachment();
					int wisdom = thisTmp.wisdom;
					int damage = 0;
					if(effect.dataPattern == 1)
					{
						damage = -wisdom;
					}
					else if(effect.dataPattern == 2)
					{
						damage = (int) ((double)wisdom * (double)effect.effectPoint/100);
					}
					
					/***********************************************************/	
					damage += getHelpPoint(effect, damage);
					/***********************************************************/	
					
					isSmite = isSmite(currentController.getAttachment(),pbt);
					if(isSmite)
						damage *= getSmiteMult();
					
					damage = pbt.setDamage(damage);
					buffer.writeInt(damage);
				}
				else
					buffer.writeInt(0);
			}
			else if(effect.type == FlashEffect.FE_TYPE_SPIRIT_DAMAGE)//调用精神瞬时伤害
			{
				if(currentController instanceof PlayerController)
				{
					PlayerBattleTmp thisTmp = (PlayerBattleTmp) currentController.getAttachment();
					int spirit = thisTmp.spirit;
					int damage = 0;
					if(effect.dataPattern == 1)
					{
						damage = -spirit;
					}
					else if(effect.dataPattern == 2)
					{
						damage = (int) ((double)spirit * (double)effect.effectPoint/100);
					}
					
					/***********************************************************/	
					damage += getHelpPoint(effect, damage);
					/***********************************************************/	
					
					isSmite = isSmite(currentController.getAttachment(),pbt);
					if(isSmite)
						damage *= getSmiteMult();
					
					damage = pbt.setDamage(damage);
					buffer.writeInt(damage);
				}
				else
					buffer.writeInt(0);
			}
			else if(effect.type == FlashEffect.FE_TYPE_DRUG_RELIVE)//x%血蓝复活
			{
				int hitPoint = (int) (pbt.maxHitPoint * (double)effect.effectPoint / 100);
				int magicPoint = 0;
				/***********************************************************/	
				hitPoint += getHelpPoint(effect, hitPoint);
				/***********************************************************/	
				pbt.setDead(!pbt.getPlayer().setHitPoint(hitPoint,pbt));
				int m = (int) (pbt.maxMagicPoint * (double)effect.effectPoint / 100);
				if(pbt.getPlayer().magicPoint < m)
				{
					magicPoint = m;
					/***********************************************************/	
					magicPoint += getHelpPoint(effect, magicPoint);
					/***********************************************************/
					pbt.getPlayer().magicPoint = magicPoint;
				}
				buffer.writeInt(hitPoint);
				buffer.writeInt(magicPoint);
			}
			else if(effect.type == FlashEffect.FE_TYPE_ATTACK_DAMAGE)//调用攻击瞬时伤害
			{
				if(currentController instanceof PlayerController)
				{
//					int job = ((PlayerController) currentController).getPlayer().profession;
					int job = ((Player) currentSprite).getJobType();
					PlayerBattleTmp thisTmp = (PlayerBattleTmp) currentController.getAttachment();
					int attach = 0;
//					if(job == 1 || job == 2)
					if(job == 1)
						attach = thisTmp.phyAtt;
//					else if(job == 3 || job == 4)
					else if(job == 2)
						attach = thisTmp.sptAtt;
					
					int damage = 0;
					if(effect.dataPattern == 1)
					{
						damage = -attach;
					}
					else  if(effect.dataPattern == 2)
					{
						damage = (int) ((double)attach * (double)effect.effectPoint/100);
					}
					
					/***********************************************************/	
					damage += getHelpPoint(effect, damage);
					/***********************************************************/	
					
					isSmite = isSmite(currentController.getAttachment(),pbt);
					if(isSmite)
						damage *= getSmiteMult();
					
					damage = pbt.setDamage(damage);
					buffer.writeInt(damage);
				}
				else if(currentController instanceof MonsterController)
				{
					MonsterController mc = (MonsterController) currentController;
					int damage = 0;
					if(mc.getMonster().maxForceAttack > 0 || mc.getMonster().minForceAttack > 0)
					{
						damage = -(int) (Math.random() * (mc.getMonster().maxForceAttack - mc.getMonster().minForceAttack) + mc.getMonster().minForceAttack);
					}
					else
					{
						damage = -(int) (Math.random() * (mc.getMonster().maxIFAttack - mc.getMonster().minIFAttack) + mc.getMonster().minIFAttack);
					}
					damage = pbt.setDamage(damage);
					buffer.writeInt(damage);
				}
			}
			else if(effect.type == FlashEffect.FE_TYPE_SPIRIT_CURE)//调用精神进行治疗
			{
				if(currentController instanceof PlayerController)
				{
					PlayerBattleTmp thisTmp = (PlayerBattleTmp) currentController.getAttachment();
					int spirit = thisTmp.spirit;
					int damage = (int) (spirit * (double)effect.effectPoint/100);
					/***********************************************************/	
					damage += getHelpPoint(effect, damage);
					/***********************************************************/	
					pbt.getPlayer().setHitPoint(damage,pbt);
					buffer.writeInt(damage);
				}
				else
					buffer.writeInt(0);
			}
			else if(effect.type == FlashEffect.FE_TYPE_CLEAR_CDTIME)//清除CD
			{
//				System.out.println("清除目标玩家所有技能的CD时间："+targetPlayer.getName());
				SkillTome skillTome = (SkillTome) targetPlayer.getPlayer().getExtPlayerInfo("skillTome");
				ActiveSkill[] aSkills = skillTome.getActiveSkills();
				for (int i = 0; i < aSkills.length; i++)
				{
					aSkills[i].processTime = 0;
				}
			}
			else if(effect.type == FlashEffect.FE_TYPE_CANCEL_NEGBUFF)//取消所有负面效果
			{
				List list = pbt.getEffectList();
				List negBuff = new ArrayList(list.size());
				for (int i = 0; i < list.size(); i++)
				{
					if(list.get(i) instanceof TimeEffect)
					{
						TimeEffect te = (TimeEffect) list.get(i);
						if(te.buffType == 2)
							negBuff.add(te);
					}
				}
				if(negBuff.size() > 0)
					battle.removeEffect(negBuff, pbt);
				
				List list2 = pbt.getBuffBox().getEffectList();
				List negBuff2 = new ArrayList(list.size());
				for (int i = 0; i < list2.size(); i++)
				{
					if(list2.get(i) instanceof TimeEffect)
					{
						TimeEffect te = (TimeEffect) list2.get(i);
						if(te.buffType == 2)
							negBuff2.add(te);
					}
				}
				if(negBuff2.size() > 0)
					battle.removeEffect(negBuff2, pbt);
			}
			else if(effect.type == FlashEffect.FE_TYPE_NOSMITE_DAMAGE) //不受暴击率影响的伤害
			{
				if(currentController instanceof PlayerController)
				{
					int damage = pbt.getDamageOnly(effect);
					
					/***********************************************************/
					int helpPoint = getHelpPoint(effect, damage);
					pbt.setDead(!pbt.getPlayer().setHitPoint(helpPoint,pbt));
					damage += helpPoint;
					/***********************************************************/
					damage += pbt.clearNoDefHurt;//因为damage是负数,被攻击方的抗忽视伤害是正数,所以这里算抗忽视伤害需要用加
					if(damage >= 0)
						damage = -1;
	//				System.out.println("damage:"+damage);
					buffer.writeInt(damage);
				}
				else
				{
					int damage = pbt.getDamageOnly(effect);
					damage += pbt.clearNoDefHurt;//因为damage是负数,被攻击方的抗忽视伤害是正数,所以这里算抗忽视伤害需要用加
					if(damage >= 0)
						damage = -1;
	//				System.out.println("damage:"+damage);
					buffer.writeInt(damage);
				}
			}
			
			buffer.writeBoolean(isSmite);
			buffer.writeByte(effect.dataType);
			buffer.writeByte(pbt.getTeamNo());
			buffer.writeByte(pbt.getIndex());
			
			pbt.sendBaseInfo(buffer);
			
			addEffectBuffer(buffer);
		}
		else if(targetType == Skill.TARGET_TYPE_MONSTERS) //目标是怪物
		{
			MonsterBattleTmp mbt = (MonsterBattleTmp)targetMonster.getAttachment();
			
			//负面BUFF，设置怪物AI
			setNetBuffPlayer(effect,mbt);
			
			ByteBuffer buffer = new ByteBuffer(10);
			buffer.writeByte(effect.type);
			if(effect.type == FlashEffect.FE_TYPE_ATTACK)
			{ 
				int rdp = 0;
				if(currentController instanceof PlayerController)
				{
					if(baseInfo.isNoDefAtt())
					{
//						int job = ((PlayerController)currentController).getPlayer().upProfession;
						int job = ((Player) currentSprite).getJobType();
						PlayerBattleTmp thisTmp = (PlayerBattleTmp) currentController.getAttachment();
//						if(job == 1 || job == 3)
						if(job == 1)
							rdp = thisTmp.phyAtt;
//						else if(job == 6 || job == 7)
						else if(job == 2)
							rdp = thisTmp.sptAtt;
						rdp = mbt.setDamage(-rdp);
					}
					else
					{
						rdp = getTotalDamageByMonster(effect);		
//						System.out.println("对怪物造成伤害："+rdp);
						rdp = getRamdomDamage(rdp);
//						System.out.println("对怪物造成伤害："+rdp);
						
						/***********************************************************/
						rdp += getHelpPoint(effect, rdp);
						/***********************************************************/
						
						isSmite = isSmite(currentController.getAttachment(),mbt);
						if(isSmite)
							rdp *= getSmiteMult();
//						System.out.println("对怪物造成伤害："+rdp);
						rdp = mbt.setDamage(rdp);
//						System.out.println("对怪物造成伤害："+rdp);
					}
				}
				buffer.writeInt(rdp); 
			}
			else if(effect.type == FlashEffect.FE_TYPE_DAMAGE) //伤害
			{
				int damage = mbt.getDamageOnly(effect);
				
				/***********************************************************/
				int helpPoint = getHelpPoint(effect, damage);
				mbt.setDead(!mbt.getMonster().setHitPoint(helpPoint,mbt));
				damage += helpPoint;
				/***********************************************************/
				
				isSmite = isSmite(currentController.getAttachment(),mbt);
				if(isSmite)
				{
					int bDamage = damage;
					damage *= getSmiteMult();
					if(Math.abs(damage) > Math.abs(bDamage))
					{
						//bDamage是之前已经对目标设置了的伤害，这里要减去
						mbt.setDead(!mbt.getMonster().setHitPoint(damage-bDamage,mbt));
					}
				}

				damage += mbt.getMonster().clearNoDefHurt;//因为damage是负数,被攻击方的抗忽视伤害是正数,所以这里算抗忽视伤害需要用加
				if(damage >= 0)
					damage = -1;
				
//				System.out.println("damage2:"+damage);
				buffer.writeInt(damage);
			}
			else if(effect.type == FlashEffect.FE_TYPE_WISDOM_DAMAGE)//调用智慧瞬时伤害
			{
				if(currentController instanceof PlayerController)
				{
					PlayerBattleTmp thisTmp = (PlayerBattleTmp) currentController.getAttachment();
					int wisdom = thisTmp.wisdom;
					int damage = 0;
					if(effect.dataPattern == 1)
					{
						damage = -wisdom;
					}
					else if(effect.dataPattern == 2)
					{
						damage = (int) ((double)wisdom * (double)effect.effectPoint/100);
					}

					/***********************************************************/	
					damage += getHelpPoint(effect, damage);
					/***********************************************************/	
					
					isSmite = isSmite(currentController.getAttachment(),mbt);
					if(isSmite)
						damage *= getSmiteMult();
					
					damage = mbt.setDamage(damage);
					buffer.writeInt(damage);
				}
				else
					buffer.writeInt(0);
			}
			else if(effect.type == FlashEffect.FE_TYPE_SPIRIT_DAMAGE)//调用精神瞬时伤害
			{
				if(currentController instanceof PlayerController)
				{
					PlayerBattleTmp thisTmp = (PlayerBattleTmp) currentController.getAttachment();
					int spirit = thisTmp.spirit;
					int damage = 0;
					if(effect.dataPattern == 1)
					{
						damage = -spirit;
					}
					else if(effect.dataPattern == 2)
					{
						damage = (int) ((double)spirit * (double)effect.effectPoint/100);
					}
					
					
					/***********************************************************/	
					damage += getHelpPoint(effect, damage);
					/***********************************************************/	
					
					isSmite = isSmite(currentController.getAttachment(),mbt);
					if(isSmite)
						damage *= getSmiteMult();
					
					damage = mbt.setDamage(damage);
					buffer.writeInt(damage);
				}
				else
					buffer.writeInt(0);
			}
			else if(effect.type == FlashEffect.FE_TYPE_ATTACK_DAMAGE)//调用攻击瞬时伤害
			{
				if(currentController instanceof PlayerController)
				{
//					int job = ((PlayerController) currentController).getPlayer().upProfession;
					int job = ((Player) currentSprite).getJobType();
					PlayerBattleTmp thisTmp = (PlayerBattleTmp) currentController.getAttachment();
					int attach = 0;
//					if(job == 1 || job == 3)
					if(job == 1)
						attach = thisTmp.phyAtt;
//					else if(job == 6 || job == 7)
					else if(job == 2)
						attach = thisTmp.sptAtt;
					int damage = 0;
					if(effect.dataPattern == 1)
					{
						damage = -attach;
					}
					else if(effect.dataPattern == 2)
					{
						damage = (int) ((double)attach * (double)effect.effectPoint/100);
					}
					
					/***********************************************************/	
					damage += getHelpPoint(effect, damage);
					/***********************************************************/	
			
					isSmite = isSmite(currentController.getAttachment(),mbt);
					if(isSmite)
						damage *= getSmiteMult();
					
					damage = mbt.setDamage(damage);
					buffer.writeInt(damage);
				}
				else
					buffer.writeInt(0);
			}
			else if(effect.type == FlashEffect.FE_TYPE_NOSMITE_DAMAGE) //不受暴击率影响的伤害
			{
				if(currentController instanceof PlayerController)
				{
					int damage = mbt.getDamageOnly(effect);
//					System.out.println("damage:"+damage);
					/***********************************************************/
					int helpPoint = getHelpPoint(effect, damage);
					mbt.setDead(!mbt.getMonster().setHitPoint(helpPoint,mbt));
					damage += helpPoint;
//					System.out.println("damage:"+damage);
					/***********************************************************/
					damage += mbt.getMonster().clearNoDefHurt;//因为damage是负数,被攻击方的抗忽视伤害是正数,所以这里算抗忽视伤害需要用加
//					System.out.println("damage:"+damage);
					if(damage >= 0)
						damage = -1;
//					System.out.println("damage:"+damage);
					buffer.writeInt(damage);
				}
				else
					buffer.writeInt(0);
			}
			buffer.writeBoolean(isSmite);
			buffer.writeByte(effect.dataType);
			buffer.writeByte(mbt.getTeamNo());
			buffer.writeByte(mbt.getIndex()); 
			
			mbt.sendBaseInfo(buffer);

			addEffectBuffer(buffer);
		}
	}
	
	
	
	protected abstract int getTotalDamageByMonster(FlashEffect effect);
	
	protected abstract int getTotalDamageByPlayer(FlashEffect effect);
	
	
	
	/**
	 * 设定其他目标
	 */
	protected boolean setNextPlayer()
	{
		PlayerController [] everyone = null;
		
		if(battle instanceof PVEController)
		{
			PVEController pve = (PVEController)battle;
			everyone = pve.getPlayers();
		}
		else if(battle instanceof CopyPVEController)
		{
			CopyPVEController copy = (CopyPVEController)battle;
			everyone = copy.getPlayers();
		}
		else if(battle instanceof EventPVEController)
		{
			EventPVEController event = (EventPVEController)battle;
			everyone = event.getPlayers();
		}
		else if(battle instanceof PVPController)
		{
			PVPController pvp = (PVPController)battle;
			if(teamNo == BattleController.TEAM1)
				everyone = pvp.getTargetPlayers();
			else if(teamNo == BattleController.TEAM2)
				everyone = pvp.getPlayers();
		}
		else if(battle instanceof PartyPKController)
		{
			PartyPKController ppk = (PartyPKController)battle;
			if(teamNo == BattleController.TEAM1)
				everyone = ppk.getTargetPlayers();
			else if(teamNo == BattleController.TEAM2)
				everyone = ppk.getPlayers();
		}
		if(everyone == null)
			return false;
		int length = everyone.length;
		for(int i = 0 ; i < length ; i ++)
		{
			if(everyone[i] == null)
				continue;
			
			if(everyone[i].getAttachment().isDead())
				continue;
			
			setTargetPlayer(everyone[i]);
			return true;
		}
		return false;
	}
	
	
	/**
	 * 设定其他怪物目标
	 */
	protected boolean setNextMonster()
	{
		MonsterController [] monsters = null;
		if(battle instanceof PVEController)
		{
			monsters = ((PVEController)battle).getMonsters();
		}
		else if(battle instanceof CopyPVEController)
		{
			monsters = ((CopyPVEController)battle).getMonsters();
		}
		else if(battle instanceof EventPVEController)
		{
			monsters = ((EventPVEController)battle).getMonsters();
		}
		if(monsters == null)
			return false;
	
		int length = monsters.length;
		
		for(int i = 0 ; i < length ; i ++)
		{
			if(monsters[i] == null)
				continue;
			
			if(monsters[i].getAttachment().isDead())
				continue;
			
			setTargetMonster(monsters[i]);
			return true;
		}
		return false;
	}
	
	
	public void setTargetPlayer(PlayerController targetPlayer)
	{
		this.targetPlayer = targetPlayer;
	}
	
	public void setTargetMonster(MonsterController targetMonster)
	{
		this.targetMonster = targetMonster;
	}
	
	/**
	 * 除了主打人以外的其他人
	 * @param effect
	 * @param effectRangeList
	 */
	protected void hitMorePlayerByEffect(Effect effect, int effectRangeList)
	{
		
		PlayerController [] playerArray  = null; 

		if(battle instanceof PVEController)
		{
			playerArray =  ((PVEController)battle).getPlayers();
		}
		else if(battle instanceof CopyPVEController)
		{
			playerArray =  ((CopyPVEController)battle).getPlayers();
		}
		else if(battle instanceof EventPVEController)
		{
			playerArray =  ((EventPVEController)battle).getPlayers();
		}
		else if(battle instanceof PVPController)
		{
			if(teamNo == BattleController.TEAM1)
				playerArray = ((PVPController)battle).getPlayers();
			else
				playerArray = ((PVPController)battle).getTargetPlayers();
		}
		else if(battle instanceof PartyPKController)
		{
			if(teamNo == BattleController.TEAM1)
				playerArray = ((PartyPKController)battle).getPlayers();
			else
				playerArray = ((PartyPKController)battle).getTargetPlayers();
		}
		
		if(playerArray == null)
			return;
	
		int length = playerArray.length;
		
		if(targetPlayer == null)
			return;
		
		PlayerBattleTmp pbt = (PlayerBattleTmp)targetPlayer.getAttachment();
		
		for (int i = 0; i < length; i++)
		{
			if(pbt.getIndex() == i)
				continue;
			
			if(playerArray[i].getAttachment().isDead())
				continue;
			
			if(--effectRangeList <= 0)
				break;
			
			setTargetPlayer(playerArray[i]);
			if(effect instanceof FlashEffect)
				onFlashEffectUse((FlashEffect)effect);
			else if(effect instanceof TimeEffect)
				onTimeEffectUse((TimeEffect)effect);
		}

	}
	
	/**
	 * 把效果使用到除了主打怪以外的怪上
	 * @param effect
	 * @param effectRangeList
	 */
	protected void hitMoreMonsterByEffect(Effect effect, int effectRangeList)
	{
		if(battle instanceof PVPController)
		{
			PlayerController [] playerArray  = null; 
			
			if(teamNo == BattleController.TEAM1)
				playerArray = ((PVPController)battle).getTargetPlayers();
			else
				playerArray = ((PVPController)battle).getPlayers();
			
			if(playerArray == null)
				return;
		
			int length = playerArray.length;
			
			if(targetPlayer == null)
				return;
			
			PlayerBattleTmp pbt = (PlayerBattleTmp)targetPlayer.getAttachment();
			
			for (int i = 0; i < length; i++)
			{
				if(pbt.getIndex() == i)
					continue;
				
				if(playerArray[i].getAttachment().isDead())
					continue;
				
				if(--effectRangeList <= 0)
					break;
				
				setTargetPlayer(playerArray[i]);
				if(effect instanceof FlashEffect)
					onFlashEffectUse((FlashEffect)effect);
				else if(effect instanceof TimeEffect)
					onTimeEffectUse((TimeEffect)effect);
			}
		}
		else if(battle instanceof PVEController || battle instanceof CopyPVEController 
				|| battle instanceof EventPVEController)
		{
			MonsterController [] monsterArray = null;
			if(battle instanceof PVEController)
			{
				monsterArray = ((PVEController)battle).getMonsters();
			}
			else if(battle instanceof CopyPVEController)
			{
				monsterArray = ((CopyPVEController)battle).getMonsters();
			}
			else if(battle instanceof EventPVEController)
			{
				monsterArray =  ((EventPVEController)battle).getMonsters();
			}
			
			int length = monsterArray.length;
			
			if(targetMonster == null)
				return;
			
			MonsterBattleTmp mbt = (MonsterBattleTmp)targetMonster.getAttachment();
			
			for(int i = 0 ; i < length ; i ++)
			{

				if(mbt.getIndex() == i)
					continue;
				
				if(monsterArray[i].getAttachment().isDead())
					continue;
				
				
				if(--effectRangeList <= 0)
					break;
				
				
				setTargetMonster(monsterArray[i]);
				if(effect instanceof FlashEffect)
					onFlashEffectUse((FlashEffect)effect);
				else if(effect instanceof TimeEffect)
					onTimeEffectUse((TimeEffect)effect);
			}
		}
		else if(battle instanceof PartyPKController)
		{
			PlayerController [] playerArray  = null; 
			
			if(teamNo == BattleController.TEAM1)
				playerArray = ((PartyPKController)battle).getTargetPlayers();
			else
				playerArray = ((PartyPKController)battle).getPlayers();
			
			if(playerArray == null)
				return;
		
			int length = playerArray.length;
			
			if(targetPlayer == null)
				return;
			
			PlayerBattleTmp pbt = (PlayerBattleTmp)targetPlayer.getAttachment();
			
			for (int i = 0; i < length; i++)
			{
				if(pbt.getIndex() == i)
					continue;
				
				if(playerArray[i].getAttachment().isDead())
					continue;
				
				if(--effectRangeList <= 0)
					break;
				
				setTargetPlayer(playerArray[i]);
				if(effect instanceof FlashEffect)
					onFlashEffectUse((FlashEffect)effect);
				else if(effect instanceof TimeEffect)
					onTimeEffectUse((TimeEffect)effect);
			}
		}
	}
	
	
	/**
	 * 使用持续效果
	 * @param effect
	 * @return
	 */
	public boolean onTimeEffectUse(TimeEffect effect)
	{
		int targetType = getSkillTargeType();

		if(targetType == Skill.TARGET_TYPE_PLAYERS) //目标是玩家
		{
			PlayerBattleTmp pbt = (PlayerBattleTmp)targetPlayer.getAttachment();
		
			if(pbt.isDead())
				return false;
			
			ByteBuffer buffer = new ByteBuffer();
			
			buffer.writeUTF(effect.name);
			buffer.writeInt((int)effect.duration);//效果时间
			buffer.writeInt(effect.interval);//次数
			buffer.writeByte(pbt.getTeamNo());
			buffer.writeByte(pbt.getIndex());
			buffer.writeBoolean(effect.isOutBattle);
			buffer.writeByte(effect.buffType);
			
			if(pbt.isTimeEffectInvain)
			{
				if(effect.buffType == 2)//负面buff失效
				{
					buffer.writeByte(1);
					buffer.writeUTF(effect.dataType[0]); 
					buffer.writeInt(-1); 
					addEffectBuffer(buffer);
					return false;
				}
			}

			if(effect.isOutBattle)
			{

				int count = 0;
				
				for (int i = 0; i < effect.dataType.length; i++)
				{
					if(effect.dataType[i].equals("0"))
					{
						break;
					}
					else if(isOtherEffect(effect.dataType[i]))
					{
						continue;
					}
					count++;
				}
				buffer.writeByte(count);
				
				TimeEffect nte = new TimeEffect();
				effect.copyTo(nte);
				
				boolean flag = pbt.getBuffBox().addEffect(nte,targetPlayer);

				for(int i = 0 ; i < nte.dataType.length ; i ++)
				{
					
					if(nte.dataType[i].equals("0"))
					{
						break;
					}
					
					int point = 0;
					int addPoint = 0;
					
//					System.out.println("1633"+flag);
					if(flag)
					{
						if(nte.dataType[i].equals("noDefAtt"))
						{
							pbt.getBaseInfo().setNoDefAtt(true);
						}
						else if(isOtherEffect(nte.dataType[i]))
						{
							continue;
						}
						else
						{
							int currPoint = Integer.parseInt(pbt.getVariable(nte.dataType[i]));
//							System.out.println("带出战斗SpriteBattleTmp:"+pbt.getPlayer().name+"--效果类型:"+nte.dataType[i]+"  使用前的值:"+currPoint);
							if(nte.dataPattern[i] == 1)
							{
								addPoint = nte.effectPoint[i];
							}
							else if(nte.dataPattern[i] == 2)
							{
								addPoint = (int) (currPoint*(double)nte.effectPoint[i]/100);
							}
							
							
							if(nte.dataType[i].equals("speed"))
							{
								point = addPoint + pbt.getCDTimer();
								if(point < 0)
									point = 0;
								pbt.setCDTimer(point);
							}
							else if(nte.dataType[i].equals("currentHP"))
							{
								if(nte.interval == 0)
								{
									addPoint = pbt.setDamage(addPoint);
								}
								else
								{
									addPoint = pbt.getDamage(addPoint);
								}
							}
							else if(nte.dataType[i].equals("currentMP"))
							{
								if(nte.dataPattern[i] == 2)
									addPoint = (int) (currPoint*(double)nte.effectPoint[i]/100);
							}
							else
							{
								/***************************************************/
								addPoint += getHelpPoint(nte, addPoint);
								/***************************************************/
								point = addPoint + currPoint;
								if(point < 0)
									point = 0;
								pbt.setVariable(nte.dataType[i], String.valueOf(point));
							}
								
							
							if(nte.interval == 0)
								nte.setPoint(i,addPoint);
							
							
							pbt.updateValue(addPoint, nte.dataType[i]);
							
//							System.out.println("带出战斗SpriteBattleTmp:"+pbt.getPlayer().name+"--效果类型:"+nte.dataType[i]+"  效果值:"+addPoint+"  使用后的值:"+getVariable(nte.dataType[i]));
						}
					}
				
					buffer.writeUTF(nte.dataType[i]); // 上升效果
					buffer.writeInt(addPoint); // 上升点数
				}
			}
			else
			{
				//在战斗中的buff
				int count = 0;
				
				for (int i = 0; i < effect.dataType.length; i++)
				{
					if(effect.dataType[i].equals("0"))
						break;
					
					else if(isOtherEffect(effect.dataType[i]))
					{
						continue;
					}
					count++;
				}
				buffer.writeByte(count);
				
				TimeEffect nte = new TimeEffect();
				effect.copyTo(nte);
				
				boolean isAdd = pbt.addEffect(nte);
			
				for(int i = 0 ; i < nte.dataType.length ; i ++)
				{
					if(nte.dataType[i].equals("0"))
						break;
					int point = 0;
					int addPoint = 0;
					if(isAdd)
					{
						if(nte.dataType[i].equals("noDefAtt"))
						{
							pbt.getBaseInfo().setNoDefAtt(true);
						}
						else if(isOtherEffect(nte.dataType[i]))
						{
							continue;
						}
						else
						{
							int currPoint = Integer.parseInt(pbt.getVariable(nte.dataType[i]));
//							System.out.println("不带出战斗SpriteBattleTmp:"+pbt.getPlayer().name+"--效果类型:"+nte.dataType[i]+"  使用前的值:"+currPoint);
							if(nte.dataPattern[i] == 1)
							{
								addPoint = nte.effectPoint[i];
							}
							else if(nte.dataPattern[i] == 2)
							{
								addPoint = (int) (currPoint*(double)nte.effectPoint[i]/100);
							}

							if(nte.dataType[i].equals("speed"))
							{
								point = addPoint + pbt.getCDTimer();
								if(point < 0)
									point = 0;
								pbt.setCDTimer(point);
							}
							else if(nte.dataType[i].equals("currentHP"))
							{
								if(nte.interval == 0)
								{
									addPoint = pbt.setDamage(addPoint);
								}
								else
								{
									addPoint = pbt.getDamage(addPoint);
								}
							}
							else if(nte.dataType[i].equals("currentMP"))
							{
								if(nte.dataPattern[i] == 2)
									addPoint = (int) (currPoint*(double)nte.effectPoint[i]/100);
								if(nte.interval == 0)
								{
									pbt.getPlayer().setMagicPoint(addPoint,pbt);
								}
							}
							else
							{
//								MainFrame.println("addPoint:"+addPoint+" ... currPoint:"+currPoint);
								/***************************************************/
								addPoint += getHelpPoint(nte, addPoint);
								/***************************************************/
//								MainFrame.println("addPoint:"+addPoint+" +++ currPoint:"+currPoint);
								point = addPoint+currPoint;
//								MainFrame.println("point:"+point);
								if(point < 0)
									point = 0;
								pbt.setVariable(nte.dataType[i], String.valueOf(point));
							}
							
							if(nte.interval == 0)
								nte.setPoint(i,addPoint);
							
							pbt.updateValue(addPoint, nte.dataType[i]);
//							System.out.println("不带出战斗SpriteBattleTmp:"+pbt.getPlayer().name+"--效果类型:"+nte.dataType[i]+"  效果值:"+addPoint+"  使用后的值:"+pbt.getVariable(nte.dataType[i]));
						}
					}
						
					buffer.writeUTF(nte.dataType[i]); // 上升效果
					buffer.writeInt(addPoint); // 上升点数	
//					System.out.println("不带出战斗的BUFF/////"+effect.dataType[i]+"   点数："+addPoint);
				}
			}
			
			pbt.sendBaseInfo(buffer);
			
			addEffectBuffer(buffer);
		}
		else if(targetType == Skill.TARGET_TYPE_MONSTERS) //目标是怪物
		{
			MonsterBattleTmp mbt = (MonsterBattleTmp)targetMonster.getAttachment();
			
			if(mbt.isDead())
				return false;

			//负面BUFF，设置怪物AI
			setNetBuffPlayer(effect,mbt);
			
			ByteBuffer buffer = new ByteBuffer(10);
			
			buffer.writeUTF(effect.name);
			buffer.writeInt((int)effect.duration);//效果时间
			buffer.writeInt(effect.interval);//次数
			buffer.writeByte(mbt.getTeamNo());
			buffer.writeByte(mbt.getIndex());
			buffer.writeBoolean(effect.isOutBattle);
			buffer.writeByte(effect.buffType);
			
			if(mbt.isTimeEffectInvain)
			{
				if(effect.buffType == 2)//负面buff失效
				{
					buffer.writeByte(1);
					buffer.writeUTF(effect.dataType[0]); 
					buffer.writeInt(-1); 
					addEffectBuffer(buffer);
					return false;
				}
			}
		
			//在战斗中的buff
			int count = 0;
			
			for (int i = 0; i < effect.dataType.length; i++)
			{
				if(effect.dataType[i].equals("0"))
					break;
				else if(isOtherEffect(effect.dataType[i]))
				{
					continue;
				}
				count++;
			}
			buffer.writeByte(count);


			TimeEffect nte = new TimeEffect();
			effect.copyTo(nte);

			boolean  isAdd = mbt.addEffect(nte);

			for(int i = 0 ; i < nte.dataType.length ; i ++)
			{
				if(nte.dataType[i].equals("0"))
					break;
				
				int point = 0;
				int addPoint = 0;
				if(isAdd)
				{
					if(nte.dataType[i].equals("noDefAtt"))
					{
						mbt.getMonster().setNoDefAtt(true);
					}
					else if(isOtherEffect(nte.dataType[i]))
					{
						continue;
					}
					else 
					{
						int currPoint = Integer.parseInt(targetMonster.getMonster().getVariable(nte.dataType[i]));
	
						if(nte.dataPattern[i] == 1)
						{
							addPoint = nte.effectPoint[i];
						}
						else if(nte.dataPattern[i] == 2)
						{
							addPoint = (int) (currPoint*(double)nte.effectPoint[i]/100);
						}
						if(nte.dataType[i].equals("speed"))
						{
							point = addPoint + mbt.getCDTimer();
							if(point < 0)
								point = 0;
							mbt.setCDTimer(point);
						}
						else if(nte.dataType[i].equals("currentHP"))
						{
							if(nte.interval == 0)
							{
								addPoint = mbt.setDamage(addPoint);
							}
							else
							{
								addPoint = mbt.getDamage(addPoint);
							}
						}
						else if(nte.dataType[i].equals("currentMP"))
						{
							
						}
						else
						{
							/***************************************************/
							addPoint += getHelpPoint(nte, addPoint);
							/***************************************************/
							point = currPoint+addPoint;
							if(point < 0)
								point = 0;
							targetMonster
							.getMonster().setVariable
							(nte.dataType[i], String.valueOf(point));
						}
						
						if(nte.interval == 0)
						{
							targetMonster.getMonster().updateMoreValue();
							nte.setPoint(i,addPoint);
						}
	
					}
				}

				buffer.writeUTF(nte.dataType[i]); // 上升效果
				buffer.writeInt(addPoint); // 上升点数
			}
			
			mbt.sendBaseInfo(buffer);
			
			addEffectBuffer(buffer);
		}
		return true;
	}
	
	
	
	public void pack(ByteBuffer buffer)
	{
		ByteBuffer buff = getEffectBuffer();
		
		if(buff == null)
			return;
		
		buffer.writeByte(effectCount);
		buffer.writeBytes(buff.getBytes());
		addEffectBuffer(null);
	}
	
	
	protected boolean changeTarget()
	{
		int targetType = getSkillTargeType();
		
		if(targetType == ActiveSkill.TARGET_TYPE_PLAYERS)
		{
			if(!setNextPlayer())
			{
				battle.checkDeadState();
				return false;
			}
		}
		else if(targetType == ActiveSkill.TARGET_TYPE_MONSTERS)
		{
			if(!setNextMonster())
			{
				battle.checkDeadState();
				return false;
			}
		}
		return true;
	}

	
	/**
	 * 如果有对怪物释放负面BUFF，则把这个玩家设置为怪物的下一个目标
	 * @param effect
	 * @param mbt
	 */
	public void setNetBuffPlayer(Effect effect,MonsterBattleTmp mbt)
	{
		if(effect instanceof FlashEffect)
		{
			FlashEffect fe = (FlashEffect) effect;
			if(fe.effectPoint < 0)
			{
				if(battle instanceof PVEController)
				{
					PVEController pve = (PVEController) battle;
					if(currentController instanceof PlayerController)
					{
						PlayerController target = (PlayerController) currentController;
						if(mbt.getMonster().getMonsterAI() != null)
							mbt.getMonster().getMonsterAI().setNegBuffPlayer(pve.getPlayerIndex(target));
					}
				}
			}
		}
		else if(effect instanceof TimeEffect)
		{
			TimeEffect te = (TimeEffect) effect;
			//负面BUFF，设置怪物AI
			if(te.buffType == 2)
			{
				if(battle instanceof PVEController)
				{
					PVEController pve = (PVEController) battle;
					if(currentController instanceof PlayerController)
					{
						PlayerController target = (PlayerController) currentController;
						if(mbt.getMonster().getMonsterAI() != null)
							mbt.getMonster().getMonsterAI().setNegBuffPlayer(pve.getPlayerIndex(target));
					}
				}
			}
		}
	}
	
	/**
	 * 如果有玩家恢复血或则给别的玩家加血，则设置为怪物的下一个目标
	 * @param battle
	 */
	public void setAddLifePlayer()
	{
		if(battle instanceof PVEController)
		{
			PVEController pve = (PVEController) battle;
			MonsterController[] monsters = pve.getMonsters();
			for (int i = 0; i < monsters.length; i++) 
			{
				if(currentController instanceof PlayerController)
				{
					PlayerController target = (PlayerController) currentController;
					if(monsters[i].getMonster().getMonsterAI() != null)
					{
						monsters[i].getMonster().getMonsterAI().setAddLifeToOtherPlayer(pve.getPlayerIndex(target));
						monsters[i].getMonster().getMonsterAI().setComebackLifePlayer(pve.getPlayerIndex(target));
					}
				}
			}
		}
	}
	
	public BuffBox getBuffBox()
	{
		return this.buffBox;
	}
	
	public List getEffectList()
	{
		return effectList;
	}
	
	
	/**
	 * 失败方掉宝
	 * @param sbt
	 */
	public void loserMercy(SpriteBattleTmp sbt)
	{
		if(sbt instanceof MonsterBattleTmp)
		{
			MonsterBattleTmp mbt = (MonsterBattleTmp) sbt;
			Monster monster = mbt.getMonster();
			MonsterDropProp taskDP = mbt.getMonster().getTaskDropProp();
			MonsterDropProp commDP = mbt.getMonster().getMonsterDropProp();
			monsterMercy(taskDP,monster,2);
			monsterMercy(commDP,monster,1);
		}
	}
	
	/**
	 * 怪物慈悲(掉宝)
	 */
	private void monsterMercy(MonsterDropProp mdp,Monster monster,int type)
	{
		if(mdp == null)
		{
//			System.out.println("此怪物没有物品掉落！");
			return;
		}
		int count = 0;
		for (int i = 0; i < battle.getPlayerCount(); i++) 
		{
			if( battle.getPlayerList().get(i) == null)
				continue;
			PlayerController player = (PlayerController) battle.getPlayerList().get(i);
			count += player.getPlayer().level;
		}
		if(count != 0)
		{
			int pVeLevel = 0;//怪物与玩家平均等级之差
			int avrLevel = count/battle.getPlayerCount();
			if(avrLevel > monster.level)
				pVeLevel = avrLevel - monster.level;
			else
				pVeLevel = monster.level - avrLevel;
			int dropMoney = mdp.getRandomMoney();
			battle.setBattlePoint(dropMoney);
			
			int dropHonor = mdp.getRandomHonor();
			battle.setBattleHonor(dropHonor);
			
			List goodsList = mdp.getDropProp(pVeLevel, monster,type);
			for (int i = 0; i < goodsList.size(); i++) 
			{
				Goods goods = (Goods) goodsList.get(i);
				battle.setBattleGoods(goods);
			}
		}
	} 
	
	
	public boolean isOtherEffect(String dataType)
	{
		boolean result = false;
		if(dataType.equals("chaos") || dataType.equals("damageModify")
				|| dataType.equals("timeEffectInvain") || dataType.equals("dizzy"))
		{
			result = true;
		}
		return result;
	}
	
	
	/**
	 * 玩家死后删除BUFF
	 */
	public void removeBuff(SpriteController sprite)
	{
		if(!(sprite instanceof PlayerController))
			return;
		PlayerController target = (PlayerController) sprite;
		if(target.getAttachment().isDead() || target.isDead())
		{
			if(target.getAttachment().effectList != null)
			{
				battle.removeEffect(target.getAttachment().effectList, target.getAttachment());
			}
			BuffBox buffBox = (BuffBox) target.getPlayer().getExtPlayerInfo("buffBox");
			List list = buffBox.getEffectList();
			for (int j = 0; j < list.size(); j++) 
			{
				TimeEffect te = (TimeEffect) list.get(j);
				buffBox.removeEffect(te,target);
			}
		}
	}
	
	
	/**
	 * 是否打出暴击
	 * @param sbt 攻击方
	 * @param target 目标玩家
	 * @return
	 */
	public boolean isSmite(SpriteBattleTmp sbt,SpriteBattleTmp target)
	{
		boolean result = false;
		if(sbt instanceof PlayerBattleTmp)
		{
			PlayerBattleTmp active = (PlayerBattleTmp) sbt;
			int random = (int) (Math.random() * 10000) + 1;
			if(random <= active.getSmiteRate(target))
			{
				result = true;
			}
		}
		return result;
	}

	
	/**
	 * 获取加上辅助值后的值
	 * @param effect
	 * @param point
	 * @param curePoint
	 * @return
	 */
	public int getHelpPoint(Effect effect,int point)
	{
		int result = 0;
		if(currentController instanceof PlayerController)
		{
			PlayerController player = (PlayerController) currentController;
			int curePoint = ((PlayerBattleTmp)player.getAttachment()).curePoint;
			if(effect.cureType == 1)
			{
				result = (int) ((double)point * (double)curePoint / 10000);
				effect.helpPoint = curePoint;
			}
		}
		return result;
	}
	
	/**
	 * 获取暴击倍数
	 * @return
	 */
	public double getSmiteMult()
	{
		double result = SMITERATE;
		if(currentController instanceof PlayerController)
		{
			PlayerBattleTmp pbt = (PlayerBattleTmp) currentController.getAttachment();
			if(pbt.getPlayer().getJobType() == 1)
			{
				result += (double)pbt.phySmiteHurtParm / 10000;
				if(targetPlayer != null)
				{
					PlayerBattleTmp targetPbt = (PlayerBattleTmp) targetPlayer.getAttachment();
					result -= (double)targetPbt.clearPhySmiteParm / 10000;
				}
				else if(targetMonster != null)
				{
					MonsterBattleTmp monster = (MonsterBattleTmp) targetMonster.getAttachment();
					result -= (double)monster.getMonster().clearPhySmiteParm / 10000;
				}
			}
			else if(pbt.getPlayer().getJobType() == 2)
			{
				result += (double)pbt.sptSmiteHurtParm / 10000;
				if(targetPlayer != null)
				{
					PlayerBattleTmp targetPbt = (PlayerBattleTmp) targetPlayer.getAttachment();
					result -= (double)targetPbt.clearSptSmiteParm / 10000;
				}
				else if(targetMonster != null)
				{
					MonsterBattleTmp monster = (MonsterBattleTmp) targetMonster.getAttachment();
					result -= (double)monster.getMonster().clearSptSmiteParm / 10000;
				}
			}
		}
		
		if(result < 1.2)
			result = 1.2;
		return result;
	}
	
	
	/**
	 * 发送战斗中各玩家的数据
	 */
	public void sendBattleSpriteInfo()
	{
		ByteBuffer buffer = new ByteBuffer();
		
		List players = battle.getPlayerList();
		List targetPlayers = null;
		MonsterController[] monsters = null;

		if(battle instanceof PVPController)
		{
			PVPController pvp = (PVPController) battle;
			targetPlayers = pvp.getTargetPlayerList();
		}
		else if(battle instanceof PartyPKController)
		{
			PartyPKController ppk = (PartyPKController) battle;
			targetPlayers = ppk.getTargetPlayerList();
		}
		else if(battle instanceof PVEController)
		{
			monsters = ((PVEController)battle).getMonsters();
		}
		else if(battle instanceof CopyPVEController)
		{
			monsters = ((CopyPVEController)battle).getMonsters();
		}
		else if(battle instanceof EventPVEController)
		{
			monsters = ((EventPVEController)battle).getMonsters();
		}
		
		for (int i = 0; i < players.size(); i++)
		{
			PlayerController pc = (PlayerController) players.get(i);
			if(battle.isTargetNull(pc))
				continue;
			PlayerBattleTmp pbt = (PlayerBattleTmp) pc.getAttachment();
			buffer = new ByteBuffer();
			buffer.writeByte(pbt.getTeamNo());
			buffer.writeByte(pbt.getIndex());
			pbt.sendBaseInfo(buffer);
			battle.dispatchMsg(SMsg.S_BATTLE_PLAYER_UPDATE_COMMAND, buffer);
		}
		
		if(targetPlayers != null)
		{
			for (int i = 0; i < targetPlayers.size(); i++)
			{
				PlayerController pc = (PlayerController) targetPlayers.get(i);
				if(battle.isTargetNull(pc))
					continue;
				PlayerBattleTmp pbt = (PlayerBattleTmp) pc.getAttachment();
				buffer = new ByteBuffer();
				buffer.writeByte(pbt.getTeamNo());
				buffer.writeByte(pbt.getIndex());
				pbt.sendBaseInfo(buffer);
				battle.dispatchMsg(SMsg.S_BATTLE_PLAYER_UPDATE_COMMAND, buffer);
			}
		}
		
		if(monsters != null)
		{
			for (int i = 0; i < monsters.length; i++)
			{
				if(monsters[i] == null)
					continue;
				MonsterBattleTmp mbt = (MonsterBattleTmp) monsters[i].getAttachment();
				buffer = new ByteBuffer();
				buffer.writeByte(mbt.getTeamNo());
				buffer.writeByte(mbt.getIndex());
				mbt.sendBaseInfo(buffer);
				battle.dispatchMsg(SMsg.S_BATTLE_PLAYER_UPDATE_COMMAND, buffer);
			}
		}
	}

	
}
