package cc.lv1.rpg.gs.entity.impl.battle;

import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.List;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.controller.BattleController;
import cc.lv1.rpg.gs.entity.controller.MonsterController;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.controller.SpriteController;
import cc.lv1.rpg.gs.entity.impl.Monster;
import cc.lv1.rpg.gs.entity.impl.MonsterAI;
import cc.lv1.rpg.gs.entity.impl.battle.effect.Effect;
import cc.lv1.rpg.gs.entity.impl.battle.effect.FlashEffect;
import cc.lv1.rpg.gs.entity.impl.battle.effect.TimeEffect;
import cc.lv1.rpg.gs.entity.impl.battle.skill.ActiveSkill;
import cc.lv1.rpg.gs.entity.impl.battle.skill.Skill;
import cc.lv1.rpg.gs.net.SMsg;

/**
 * 怪物临时数据
 * @author dxw
 *
 */
public class MonsterBattleTmp extends SpriteBattleTmp
{
	/** 怪物默认技能 */
	public static final int DEFAULTSKILL = 1031000001;

	private MonsterController monsterController;
	
	private Monster monster;
	
	/** 怪物在本场战斗中的攻击次数 */
	public int attackCount;
	
	
	/** 对应战斗中最多5个玩家的仇恨值 */
	public int hatOne,hatTwo,hatThree,hatFour,hatFive;
	
	public int addHatOne,addHatTwo,addHatThree,addHatFour,addHatFive;
	
	public void setMonster(MonsterController monsterController)
	{
		super.setSpriteController(monsterController);
		super.setSprite(monsterController.getMonster());
		
		this.monsterController = monsterController;
		this.monster = monsterController.getMonster();
		
		setCDTimer(monster.speed);
//		System.out.println("---------------------------------------");
//		System.out.println("怪物 " +monster.name+" 的战斗信息");
//		System.out.println("物理攻击 ..." + monster.maxForceAttack);
//		System.out.println("精神攻击 ..." + monster.maxIFAttack);
//		System.out.println("物理防御 ... "+ monster.forceDefense);
//		System.out.println("精神防御 ... "+ monster.IFDefense);
//		System.out.println("速度 ..." + monster.speed);
//		System.out.println("---------------------------------------");
	}

	public void update(long timeMillis)
	{
		super.update(timeMillis);
	
		if(isDead())
			return;
		
		if(isAction)
		{
			//怪物释放技能
			thinking();
			isAction = false;
			lastPingTime = timeMillis;
		}
	
		if(isEmptyEffect())
			return;
		
		
		for(int j = 0 ; j < effectList.size() ; j ++)
		{
			TimeEffect effect = (TimeEffect)effectList.get(j);

			if(effect.interval != 0)
			{
				if(effect.isUseEffect(timeMillis))
				{
					processTimeEffectExt(effect);
				}
			}
			boolean flag = effect.isEndEffect(timeMillis);
			
			if(flag)
			{			
				if(effect.interval == 0)
				{
					deleteEffect(effect);
				}
				else if(effect.interval != 0)
				{
					processTimeEffectEndExt(effect);
				}
				
				monster.updateMoreValue();
				removeEffect(effect);
			}
		}

	}
	
	public void deleteEffect(TimeEffect effect)
	{
		for(int i = 0 ; i < effect.dataType.length ; i ++)
		{
			
			if(effect.dataType[i].equals("0"))
			{
				break;
			}
			
			if(effect.interval == 0)
			{
				if(effect.dataType[i].equals("noDefAtt"))
				{
					monster.setNoDefAtt(false);
				}
				else if(isOtherEffect(effect.dataType[i]))
				{
					continue;
				}
				else
				{
					int currPoint = Integer.parseInt(monster.getVariable(effect.dataType[i]));
					int p = currPoint - effect.getPoint(i);
					if(p < 0)
						p = 0;
					if(effect.dataType[i].equals("speed"))
					{
						p = getCDTimer() - effect.getPoint(i);
						if(p < 0)
							p = 0;
						setCDTimer(p);
					}
					else
					{
						monster.setVariable(effect.dataType[i],String.valueOf(p));
					}
					
				}
			}
		}
	}
	

	private void thinking()
	{
		//通过智能找怪
		MonsterAI ma = monster.getMonsterAI();
		List playerList = battle.getPlayerList();
//		PlayerController target = null;
		SpriteController target = null;
		int skillId = 0;
		if(ma == null)
		{
			target = getRandomTarget(playerList,1);
			skillId = DEFAULTSKILL;
		}
		else
		{//随机设置目标
			int random = ma.getRandom();
			if(ma.firstCondition.equals("0:0:0") || ma.getCondition(ma.firstCondition,random) == 0)
			{
				target = getRandomTarget(playerList, 1);
				String[] str = Utils.split(ma.firstSkill, ":");
				try 
				{
					skillId = Integer.parseInt(str[random]);
				} catch (Exception e) {
					System.out.println("MonsterBattleTmp thinking skillId first error:"+str[random]);
					return;
				}
				
			}
			else
			{
				boolean flag = false;
			
				flag = isCondition(ma.getCondition(ma.firstCondition,random), ma, playerList);
				if(flag)
				{
					ma.setDefault(ma.firstCondition, ma.firstTarget, ma.firstSkill,random);
				}
				else 
				{
					random = ma.getRandom();
					int secondCon = ma.getCondition(ma.secondCondition, random);
					if(secondCon == 0)
					{
						target = getRandomTarget(playerList, 1);
						String[] str = Utils.split(ma.secondSkill, ":");
						try 
						{
							skillId = Integer.parseInt(str[random]);
						} catch (Exception e) {
							System.out.println("MonsterBattleTmp thinking skillId second error:"+str[random]);
							return;
						}
					}
					else
					{
						flag = isCondition(secondCon, ma, playerList);
						if(flag)
						{
							ma.setDefault(ma.secondCondition,ma.secondTarget,ma.secondSkill,random);
						}
						else
						{
							random = ma.getRandom();
							int thirdCon = ma.getCondition(ma.thirdCondition,random);
							if(thirdCon == 0)
							{
								target = getRandomTarget(playerList, 1);
								String[] str = Utils.split(ma.thirdSkill, ":");
								try 
								{
									skillId = Integer.parseInt(str[random]);
								} catch (Exception e) {
									System.out.println("MonsterBattleTmp thinking skillId third error:"+str[random]);
									return;
								}
							}
							else
							{
								flag = isCondition(ma.getCondition(ma.thirdCondition,random), ma, playerList);
								if(flag)
								{
									ma.setDefault(ma.thirdCondition,ma.thirdTarget,ma.thirdSkill,random);
								}
								else
								{
									random = ma.getRandom();
									
									int fourthCon = ma.getCondition(ma.fourthCondition,random);
									if(fourthCon == 0)
									{
										target = getRandomTarget(playerList, 1);
										String[] str = Utils.split(ma.fourthSkill, ":");
										try 
										{
											skillId = Integer.parseInt(str[random]);
										} catch (Exception e) {
											System.out.println("MonsterBattleTmp thinking skillId fourth error:"+str[random]);
											return;
										}
									}
									else
									{
										flag = isCondition(ma.getCondition(ma.fourthCondition,random), ma, playerList);
										if(flag)
										{
											ma.setDefault(ma.fourthCondition,ma.fourthTarget,ma.fourthSkill,random);
										}
									}
								}
							}
						}
					}
				}
				if(flag)
				{
					target = getTarget(ma.target,ma, playerList);
					skillId = ma.skill;
				}
			}	
		}


		if(target == null || skillId == 0)
		{
			target = getRandomTarget(playerList,2);
			skillId = DEFAULTSKILL;
		}

		if(target == null)
			return;
		
		if(target.getAttachment().isDead())
			return;
		
		if(isLocked())
			return;
	
//		System.out.println("--------------------------------------------");
//		System.out.println("对第一个玩家的仇恨值:"+hatOne);
//		System.out.println("对第二个玩家的仇恨值:"+hatTwo);
//		System.out.println("对第三个玩家的仇恨值:"+hatThree);
//		System.out.println("对第四个玩家的仇恨值:"+hatFour);
//		System.out.println("对第五个玩家的仇恨值:"+hatFive);
//		int num = 0;
//		for (int i = 0; i < playerList.size(); i++)
//		{
//			PlayerController p = (PlayerController) playerList.get(i);
//			if(p.getID() == target.getID())
//			{
//				num = i;break;
//			}
//		}
//		System.out.println("攻击目标是第+num+]个玩家："+target.getName());
//		System.out.println("--------------------------------------------");
		//设置技能释放对象/**
		setSkillTarget(target);
	
		Object obj = DataFactory.getInstance().getGameObject(skillId);
		
		if(obj == null || !(obj instanceof ActiveSkill))
		{
			System.out.println("MonsterBattleTmp skillId error:"+skillId);
			return;
		}
		//获取怪物使用的技能
		ActiveSkill activeSkill = (ActiveSkill)obj;

		//释放主动技能
		processActiveSkill(activeSkill);
		
		attackCount++;
		
		//测试打印********************************************************************************
//		System.out.println("..............................");
//		List ps = battle.getPlayerList();
//		for (int i = 0; i < ps.size(); i++)
//		{
//			if(ps.get(i) == null)
//				continue;
//			PlayerController player = (PlayerController) ps.get(i);
//			System.out.println("302 MBT:"+player.getName()+" HP "+player.getPlayer().hitPoint+"  死亡:"+player.getAttachment().isDead());
//		}
//		System.out.println("..............................");
		//测试打印********************************************************************************
	}
	
	/**
	 * 随机获取怪物的攻击目标
	 * @param playerList
	 * @return
	 */
	public PlayerController getRandomTarget(List playerList,int type)
	{
		if(type == 1)
		{
			for (int i = 0; i < 3; i++) 
			{
				int random = (int) (Math.random() * playerList.size());
				if(playerList.get(random) == null)
					return null;
				PlayerController player = (PlayerController) playerList.get(random);
				if(player.getPlayer().hitPoint > 0)
					return player;
				else
					continue;
			}
		}
		else if(type == 2)
		{
			for (int i = 0; i < playerList.size(); i++) 
			{
				if(playerList.get(i) == null)
					return null;
				PlayerController player = (PlayerController) playerList.get(i);
				if(player.getPlayer().hitPoint > 0)
					return player;
			}
		}
		return null;
	}

	
	
	/**
	 * 获取攻击目标
	 * @param target
	 * @param ma
	 * @param playerList
	 * @return
	 */
	public SpriteController getTarget(int target,MonsterAI ma,List playerList)
	{
//		PlayerController player = null;
		SpriteController sprite = null;
		switch(target)
		{
			case 0: sprite = getRandomTarget(playerList, 1);break;
			case 1: sprite = ma.getTargetOne(playerList);break;
			case 2: sprite = ma.getTargetTwo(playerList);break;
			case 3: sprite = ma.getTargetThree(this, playerList);break;
			case 4: sprite = ma.getTargetFour(this, playerList);break;
			case 5: sprite = ma.getTargetFiveAndSix(500, this, playerList);break;
			case 6: sprite = ma.getTargetFiveAndSix(1000, this, playerList);break;
			case 7: sprite = ma.getTargetSeven(this,playerList);break;
			case 8: sprite = ma.getTargetEight(playerList);break;
			case 9: sprite = ma.getTargetNine(playerList);break;
			case 10:sprite = ma.getTargetTen(this,playerList);break;
			case 11:sprite = ma.getTargetEleven(playerList);break;
			case 12:sprite = ma.getTargetTwelve(playerList);break;
			case 13:sprite = ma.getTargetThirteen(playerList);break;
//			case 14:sprite = monsterController;break;
		}
		return sprite;
	}
	
	/**
	 * 判断条件是否满足
	 * @param con
	 * @param ma
	 * @param playerList
	 * @return
	 */
	public boolean isCondition(int con,MonsterAI ma,List playerList)
	{
		boolean flag = false;
		switch(con)
		{
			case 0 : flag = true;break;
			case 1 : flag = ma.isOne(this);break;
			case 2 : flag = ma.isTwoAndFive(500, this);break;
			case 3 : flag = ma.isThree(monster, playerList);break;
			case 4 : flag = ma.isFour(playerList);break;
			case 5 : flag = ma.isTwoAndFive(1000, this);break;
			case 6 : flag = ma.isSix(monster);break;
			case 7 : flag = ma.isSeven(this,playerList);break;
			case 8 : flag = ma.isEight();break;
			case 9 : flag = ma.isNine();break;
			case 10: flag = ma.isTen(this);break;
			case 11: flag = ma.isEleven(this);break;
			case 12: flag = ma.isTwelve(this);break;
			case 13: flag = ma.isThirteen(this);break;
			case 14: flag = ma.isFourteen();break;
			case 15: flag = ma.isFifteen(this);break;
			case 16: flag = ma.isSixteen(this);break;
			case 17: flag = ma.isSeventeen(this);break;
		}
		return flag;
	}
	
	

	
	public MonsterController getMonsterController()
	{
		return monsterController;
	}

	public Monster getMonster()
	{
		return monster;
	}


	public boolean processActiveSkill(ActiveSkill skill)
	{
		if(isBeforeUnusualEffect)
			checkUnusualEffect();

		ByteBuffer buffer = new ByteBuffer(64);
		buffer.writeByte(BattleController.SKILLPROCESSOR); //释放技能
		buffer.writeByte(getTeamNo());//使用技能者的组编号
		buffer.writeByte(getIndex()); //使用者技能者的index;
		buffer.writeInt(skill.id);
		if(targetPlayer != null)
		{
			if(monster.minForceAttack > 0 || monster.maxForceAttack > 0)
			{
				buffer.writeByte(1);
			}
			else
			{
				buffer.writeByte(2);
			}
		}
		else
			buffer.writeByte(1);
		
		//测试资源
		buffer.writeByte(1);//动作

		DataFactory dataFactory = DataFactory.getInstance();
		
		int skillLength = skill.effectList.length;
		
		for(int i = 0 ; i < skillLength ; i ++)
		{
			if(skill.effectList[i] == 0)
				continue;
			
			if(i == 1)
			{
				int random = (int) (Math.random() * 10000);
				if(skill.rate2 < random)
					continue;
			}
			if(i == 2)
			{
				int random = (int) (Math.random() * 10000);
				if(skill.rate3 < random)
					continue;
			}
			
			Effect effect = (Effect)dataFactory.getGameObject(skill.effectList[i]);
			
			if(effect == null)
				continue;
			
			if(i != 0)
			{
				
				//防止效果施加到死了的怪以外的其他对象身上
				SpriteController sc = targetMonster != null ?targetMonster:targetPlayer;
				
				if(sc.getAttachment().isDead())
				{
					if(skill.effectRangeList == 1)
					{
						continue;
					}
				}
				
				if(targetMonster != null
				&&skill.targetType[i] == ActiveSkill.TARGET_TYPE_TEAM_SELF) //先敌后己，如果第二次的效果是
				{
					setSkillTarget(monsterController);
				}
				else if(targetPlayer != null
				&& skill.targetType[i] == ActiveSkill.TARGET_TYPE_TEAM_SELF
				&& skill.targetType[0] == ActiveSkill.TARGET_TYPE_TEAM_ENEMY) 
				{
					setSkillTarget(monsterController);
				}
			}
			
			
			//1 FlashEffect   2 TimeEffect
			int type = 0;

			if(effect instanceof FlashEffect)
			{
//				System.out.println("怪物"+monster.name+"  技能："+skill.name+ " 技能产生---瞬时效果 : "+effect.name);
				//打指定目标，如果目标已是死了则转向另外一只活着的，如果都死了则宣布胜利跳出该方法
				onFlashEffectUse((FlashEffect)effect);
				//打除了主目标以外的怪
				if(skill.effectRangeList != ActiveSkill.EFFECT_RANGE_ONE)
				{
					int targetType = getSkillTargeType();
					
					if(targetType == ActiveSkill.TARGET_TYPE_PLAYERS)
					{
						hitMorePlayerByEffect(effect, skill.effectRangeList);
					}
					else if(targetType == ActiveSkill.TARGET_TYPE_MONSTERS)
					{
						hitMoreMonsterByEffect(effect,skill.effectRangeList);
					}
				}
				type = 1;
			}
			else if(effect instanceof TimeEffect)
			{
				TimeEffect te = (TimeEffect)effect;
				onTimeEffectUse(te);
				
				//打除了主目标以外的怪
				if(skill.effectRangeList != ActiveSkill.EFFECT_RANGE_ONE)
				{
					int targetType = getSkillTargeType();
					
					if(targetType == ActiveSkill.TARGET_TYPE_PLAYERS)
					{
						hitMorePlayerByEffect(te, skill.effectRangeList);
					}
					else if(targetType == ActiveSkill.TARGET_TYPE_MONSTERS)
					{
						hitMoreMonsterByEffect(te,skill.effectRangeList);
					}
				}
				
				type = 2;
			}
			
			
			buffer.writeInt(effect.id);
			buffer.writeByte(type);
			pack(buffer);
		}
		
		monster.setMagicPoint(-skill.magic,this);
		
		buffer.writeInt(monster.magicPoint);

		battle.dispatchMsg(SMsg.S_BATTLE_ACTION_COMMAND, buffer);
		battle.checkDeadState();
		
		return true;

	}


	/**
	  effect 百分比计算
	  (基本伤害+装备<值>)*装备<百分>*效果<百分>
	  
	  effect 值计算
	  (基本伤害+装备<值>+效果<值>)*装备<百分>
	  
	 */
	protected int getTotalDamageByMonster(FlashEffect effect)
	{
		int baseDamage = getDamagePointByHit(targetMonster);
		if(baseDamage == 0)
			baseDamage = getDamagePointByMagic(targetMonster);

		int rdp = 0;
		if(effect.dataPattern == 1) //数值
		{
			rdp = -baseDamage + effect.effectPoint;
		}
		else if(effect.dataPattern == 2) //百分比
		{
			rdp = (int) (Math.abs(baseDamage) * (double)effect.effectPoint / 100);
		}

		if(rdp >= 0)
			rdp = -1;
		
		return rdp;
	}
	
	

	protected int getTotalDamageByPlayer(FlashEffect effect)
	{
	
		int baseDamage = getDamagePointByHit(targetPlayer);
	
		if(baseDamage == 0)
			baseDamage = getDamagePointByMagic(targetPlayer);

		int rdp = 0;
		if(effect.dataPattern == 1) //数值
		{
			rdp = -baseDamage + effect.effectPoint;
		}
		else if(effect.dataPattern == 2) //百分比
		{
			rdp = (int) (Math.abs(baseDamage) * (double)effect.effectPoint / 100);
		}
	
		if(rdp >= 0)
			rdp = -1;
		return rdp;
	}
	
	
	/**
	 * 取得伤害 物理攻击
	 * 
	 */
	public int getDamagePointByHit(PlayerController target)
	{
		double nPhsHitRate = ((PlayerBattleTmp)target.getAttachment()).phyHitRate;

		if(nPhsHitRate > 0.99)
			nPhsHitRate = 0.99;
		if(nPhsHitRate < 0)
			nPhsHitRate = 0;
		
		int nPhsHurtAvoid = ((PlayerBattleTmp)target.getAttachment()).phyHurtAvoid;
	
		int result = getDamagePointByHit(nPhsHitRate,nPhsHurtAvoid);

		return result;
	}
	
	/**
	 * 取得伤害 物理攻击
	 */
	public int getDamagePointByHit(MonsterController monster)
	{
		double nPhsHitRate = monster.getMonster().phsHitRate;
		return getDamagePointByHit(nPhsHitRate,0);
	}
	
	/**
	 * 取得伤害 物理攻击
	 */
	public int getDamagePointByHit(double nPhsHitRate,int nPhsHurtAvoid)
	{
		int phyAtt = 0;
		if(monster.maxForceAttack == 0)
			phyAtt = monster.minForceAttack;
		else
			phyAtt = (int) (Math.random() * (monster.maxForceAttack - monster.minForceAttack) + monster.minForceAttack);

		double damage = (double)phyAtt*(double)((double)1-nPhsHitRate);

		if(damage <= nPhsHurtAvoid) 
			return 0;
		else
			return  (int)(damage - nPhsHurtAvoid);
		
	}
	
	/**
	 * 取得伤害 魔法攻击
	 */
	public int getDamagePointByMagic(PlayerController target)
	{
		double nSptHitRate = ((PlayerBattleTmp)target.getAttachment()).sptHitRate;
		
		if(nSptHitRate > 0.99)
			nSptHitRate = 0.99;
		if(nSptHitRate < 0)
			nSptHitRate = 0;
		
		int nSptHurtAvoid = ((PlayerBattleTmp)target.getAttachment()).sptHurtAvoid;
		int result = getDamagePointByMagic(nSptHitRate,nSptHurtAvoid);
		return result;
	}
	/**
	 * 取得伤害 魔法攻击
	 */
	public int getDamagePointByMagic(MonsterController monster)
	{
		double nSptHitRate = monster.getMonster().sptHitRate;
		return getDamagePointByMagic(nSptHitRate,0);
	}
	/**
	 * 取得伤害 魔法攻击
	 */
	public int getDamagePointByMagic(double nSptHitRate,int nSptHurtAvoid)
	{
		int sptAtt = 0;
		if(monster.maxIFAttack == 0)
			sptAtt = monster.minIFAttack;
		else
			sptAtt = (int) (Math.random() * (monster.maxIFAttack - monster.minIFAttack) + monster.minIFAttack);
		
		double damage = (double)sptAtt*(double)((double)1-nSptHitRate);

		if(damage <= nSptHurtAvoid)
			return 0;
		else
			return (int)(damage  - nSptHurtAvoid);
	}
	
	
	/**
	 * 把怪物的仇恨值归0
	 * @param playerIndex
	 */
	public void initHatred(int playerIndex)
	{
		if(playerIndex == 0)
			hatOne = 0;
		else if(playerIndex == 1)
			hatTwo = 0;
		else if(playerIndex == 2)
			hatThree = 0;
		else if(playerIndex == 3)
			hatFour = 0;
		else if(playerIndex == 4)
			hatFive = 0;
		
	}
	
//	/**
//	 * 增加所有怪物对某玩家的仇恨值
//	 * 最后杀死怪物的玩家，将额外增加其他所有怪物对他的仇恨，仇恨加量为当前仇恨值*0.1。
//	 */
//	public void setMonstersToPlayerHatred(int playerIndex,float point)
//	{
//		//战斗中，循环的加上还没死的怪物对该玩家的仇恨
//		if(playerIndex == 0)
//			hatOne += hatOne * point;
//		else if(playerIndex == 1)
//			hatTwo += hatTwo * point;
//		else if(playerIndex == 2)
//			hatThree += hatThree * point;
//		else if(playerIndex == 3)
//			hatFour += hatFour * point;
//		else if(playerIndex == 4)
//			hatFive += hatFive * point;
//	}
	
	
	/**
	 * 1、玩家的每次行为所造成的仇恨增量=伤害仇恨增量+特殊技能增量+装备影响+其他（道具）
	2、伤害仇恨运算规则：当伤害小于等于700时，仇恨值增量=0.08*伤害值；
	当伤害大于700小于等于1200时，仇恨值增量=56+（伤害值-700）*0.07；
	当伤害值大于1200时，仇恨值增量=91+（伤害值-1200）*0.06
//	3、最后杀死怪物的玩家，将额外增加其他所有怪物对他的仇恨，仇恨加量为当前仇恨值*0.1。
	4、特殊技能增量指BUFF技能、加血、复活、眩晕等技能所造成的仇恨，（仇恨值由策划填）
//	5、每个光环技能将增加自己每次行为所造成的仇恨增量1%，
//	6、特种兵的挑衅技能：被动，所有行为造成怪物对自己的仇恨值增加N%
	7、在战斗中使用药品将增加所有怪物对自己的仇恨，（具体值由策划填）
	
	 * @param damage 伤害仇恨
	 * @param skill 技能仇恨
	 * @param playerIndex 玩家标识
	 * @param equipHat 装备影响
	 * @param other 其它
	 */
	public void setHatred(int damage,ActiveSkill skill,int playerIndex,int equipHat,int other)
	{
		int hat = 0;
		if(damage <= 700)
			hat = (int) (0.08 * damage);
		else if(damage > 700 && damage <= 1200)
			hat = (int) (56 + (damage - 700) * 0.07);
		else if(damage > 1200)
			hat = (int) (91 + (damage - 1200) * 0.06);
		
		if(skill != null)
			hat += skill.hate;//技能仇恨
		
		hat += equipHat;//装备影响
		hat += other;//其它

		setHatred(hat,playerIndex);
	}

	/**
	 * 设置仇恨值
	 * @param hatred
	 */
	public void setHatred(int hatred,int playerIndex)
	{
		if(playerIndex == 0)
		{
			addHatOne = hatred;
			hatOne += addHatOne;
		}
		else if(playerIndex == 1)
		{
			addHatTwo = hatred;
			hatTwo += addHatTwo;
		}
		else if(playerIndex == 2)
		{
			addHatThree = hatred;
			hatThree += addHatThree;
		}
		else if(playerIndex == 3)
		{
			addHatFour = hatred;
			hatFour += addHatFour;
		}
		else if(playerIndex == 4)
		{
			addHatFive = hatred;
			hatFive += addHatFive;
		}
	}
	
	/**
	 * 发送中了效果后的值
	 * @param buffer
	 */
	public void sendBaseInfo(ByteBuffer buffer)
	{
		buffer.writeInt(monster.hitPoint);
		buffer.writeInt(monster.magicPoint);
		buffer.writeInt(monster.maxHitPoint);
		buffer.writeInt(monster.maxMagicPoint);
	}
	
}
