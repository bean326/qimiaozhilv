package cc.lv1.rpg.gs.entity.impl.battle;

import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.i.Map;
import vin.rabbit.util.collection.impl.ArrayList;
import vin.rabbit.util.collection.impl.HashMap;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.controller.BattleController;
import cc.lv1.rpg.gs.entity.controller.CopyPVEController;
import cc.lv1.rpg.gs.entity.controller.EventPVEController;
import cc.lv1.rpg.gs.entity.controller.GoldPVEController;
import cc.lv1.rpg.gs.entity.controller.GoldPVPController;
import cc.lv1.rpg.gs.entity.controller.MonsterController;
import cc.lv1.rpg.gs.entity.controller.PVEController;
import cc.lv1.rpg.gs.entity.controller.PVPController;
import cc.lv1.rpg.gs.entity.controller.PartyPKController;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.controller.SpriteController;
import cc.lv1.rpg.gs.entity.ext.PetTome;
import cc.lv1.rpg.gs.entity.impl.battle.effect.Effect;
import cc.lv1.rpg.gs.entity.impl.battle.effect.FlashEffect;
import cc.lv1.rpg.gs.entity.impl.battle.effect.TimeEffect;
import cc.lv1.rpg.gs.entity.impl.battle.skill.ActiveSkill;
import cc.lv1.rpg.gs.entity.impl.battle.skill.PetActiveSkill;
import cc.lv1.rpg.gs.entity.impl.pet.Pet;
import cc.lv1.rpg.gs.net.SMsg;

public class PetBattleTmp
{
	public static final int CDTIMER = 7000;
	
	private PlayerController masterController;
	
	private Pet pet;
	
	private PlayerBattleTmp master;
	
	private boolean isAction = true;
	
	private long lastPingTime;
	
	private int pCdTimer = CDTIMER;
	
	public PetBattleTmp(PlayerController player)
	{
		master = (PlayerBattleTmp) player.getAttachment();
		this.masterController = player;
		PetTome pt = (PetTome) masterController.getPlayer().getExtPlayerInfo("petTome");
		pet = pt.getActiveBattlePet();
	}
	
	public void update(long timeMillis)
	{
		if(pet == null)
			return;
		
		if(master.isDead())
			return;
		
		if(pet.isMaxBattlePoint())
		{
			return;
		}
		if(!isAction)
		{
			if(lastPingTime == 0)
			{
				lastPingTime = timeMillis;
				return;
			}
			
			if(lastPingTime + pCdTimer < timeMillis)
			{
				isAction = true;
			}
		}
		else
		{
			//宠物释放技能
			thinking();
			isAction = false;
			lastPingTime = timeMillis;
		}
	}

	
	private void thinking()
	{
		if(!(masterController.getParent() instanceof BattleController))
			return;
		if(master.getBattle().isFinish())
			return;
		if(master.isSkillUsing)
			return;
		PetActiveSkill[] pass = pet.getPetActiveSkills();
		for (int i = 0; i < pass.length; i++) 
		{
			if(pass[i] == null)
				continue;
			if(!pet.isUseSkill(pass[i]))
				continue;
			if(master.isSkillUsing)
				return;
			int random = (int) (Math.random() * 10000);
			if(random <= pass[i].skillRate)//释放技能
			{
				boolean result = setAttackTarget(pass[i]);
				if(!result)
					return;
				if(getTargetMonster() == null && getTargetPlayer() == null)
				{
					return;
				}
				if(getTargetMonster() != null && getTargetMonster().getAttachment().isDead())
					return;
				if(getTargetPlayer() != null && getTargetPlayer().getAttachment().isDead())
					return;
//				masterController.sendGetGoodsInfo(1, false, "守护技能发动成功!随机数:"+random+"几率:"+pass[i].skillRate);
				processActiveSkill(pass[i]);
				break;
			}
			else
			{
//				masterController.sendGetGoodsInfo(1, false, "守护技能发动失败!随机数:"+random+"几率:"+pass[i].skillRate);
			}
		}
	}
	
	private boolean setAttackTarget(PetActiveSkill skill)
	{
		if(master.isSkillUsing)
			return false;
		SpriteController[] targets = null;
		BattleController bc = master.getBattle();
		if(skill.targetType[0] == ActiveSkill.TARGET_TYPE_ONESELF)//主人
		{
			master.setSkillTarget(masterController);
			return true;
		}
		else if(skill.targetType[0] == ActiveSkill.TARGET_TYPE_PLAYERS)//已方
		{
			if(bc instanceof PVEController || bc instanceof CopyPVEController
					|| bc instanceof GoldPVEController || bc instanceof EventPVEController)
			{
				targets = (SpriteController[]) bc.getPlayerList().toArray();
			}
			else
			{
				if(bc instanceof PVPController)
				{
					if(master.getTeamNo() == BattleController.TEAM1)
						targets = ((PVPController)bc).getPlayers();
					else if(master.getTeamNo() == BattleController.TEAM2)
						targets = ((PVPController)bc).getTargetPlayers();
				}
				else if(bc instanceof PartyPKController)
				{
					if(master.getTeamNo() == BattleController.TEAM1)
						targets = ((PartyPKController)bc).getPlayers();
					else if(master.getTeamNo() == BattleController.TEAM2)
						targets = ((PartyPKController)bc).getTargetPlayers();
				}
				else if(bc instanceof GoldPVPController)
				{
					if(master.getTeamNo() == BattleController.TEAM1)
						targets = ((GoldPVPController)bc).getPlayers();
					else if(master.getTeamNo() == BattleController.TEAM2)
						targets = ((GoldPVPController)bc).getTargetPlayers();
				}
			}
		}
		else if(skill.targetType[0] == ActiveSkill.TARGET_TYPE_MONSTERS)//敌方
		{
			if(bc instanceof PVEController || bc instanceof CopyPVEController
					|| bc instanceof GoldPVEController || bc instanceof EventPVEController)
			{
				if(bc instanceof PVEController)
					targets = ((PVEController)bc).getMonsters();
				else if(bc instanceof CopyPVEController)
					targets = ((CopyPVEController)bc).getMonsters();
				else if(bc instanceof GoldPVEController)
					targets = ((GoldPVEController)bc).getMonsters();
				else if(bc instanceof EventPVEController)
					targets = ((EventPVEController)bc).getMonsters();
			}
			else
			{
				if(bc instanceof PVPController)
				{
					if(master.getTeamNo() == BattleController.TEAM1)
						targets = ((PVPController)bc).getTargetPlayers();
					else if(master.getTeamNo() == BattleController.TEAM2)
						targets = ((PVPController)bc).getPlayers();
				}
				else if(bc instanceof PartyPKController)
				{
					if(master.getTeamNo() == BattleController.TEAM1)
						targets = ((PartyPKController)bc).getTargetPlayers();
					else if(master.getTeamNo() == BattleController.TEAM2)
						targets = ((PartyPKController)bc).getPlayers();
				}
				else if(bc instanceof GoldPVPController)
				{
					if(master.getTeamNo() == BattleController.TEAM1)
						targets = ((GoldPVPController)bc).getTargetPlayers();
					else if(master.getTeamNo() == BattleController.TEAM2)
						targets = ((GoldPVPController)bc).getPlayers();
				}
			}
		}
		if(targets == null)
			return false;
		
		List list = new ArrayList();
		for (int i = 0; i < targets.length; i++)
		{
			if(targets[i] == null)
				continue;
			if(targets[i].getAttachment().isDead())
				continue;
			list.add(targets[i]);
		}
		SpriteController target = (SpriteController) list.get((int) (Math.random()*list.size()));
		if(target != null)
		{
			master.setSkillTarget(target);
			return true;
		}
		else
			return false;
	}
	
	
	/**
	 * 宠物战斗释放主动技能
	 * @param skill
	 */
	public boolean processActiveSkill(PetActiveSkill skill)
	{	
		if(master.isBeforeUnusualEffect)
			master.checkUnusualEffect();
		
		ByteBuffer buffer = new ByteBuffer(64);
		buffer.writeByte(BattleController.PETSKILLPROCESSOR); //释放技能
		buffer.writeByte(master.getTeamNo());//使用技能者的组编号
		buffer.writeByte(master.getIndex()); //使用者技能者的index;
		buffer.writeInt(skill.id);

		buffer.writeByte(pet.getJobType());
		
		//测试资源
		if(skill.effectRangeList == 0)
		{
			buffer.writeByte(0);//动作
		}
		else
		{
			if(skill.targetType[0] == 1 || skill.targetType[0] == 3)
				buffer.writeByte(3);
			else if(skill.targetType[0] == 2)
				buffer.writeByte(1);
			else 
				buffer.writeByte(3);
		}
		
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
					break;
			}

			Effect effect = (Effect)dataFactory.getGameObject(skill.effectList[i]);
			
			if(effect == null)
				continue;
			
			if(i != 0)
			{
				//防止效果施加到死了的怪以外的其他对象身上
				SpriteController sc = getTargetMonster() != null ?getTargetMonster():getTargetPlayer();
				
				if(sc.getAttachment().isDead())
				{
					if(skill.effectRangeList == 1 && skill.targetType[i] == skill.targetType[0])
					{
						continue;
					}
				}
				
				if(getTargetMonster() != null
				&&(skill.targetType[i] == ActiveSkill.TARGET_TYPE_TEAM_SELF
						|| skill.targetType[i] == ActiveSkill.TARGET_TYPE_ONESELF)) //先敌后己，如果第二次的效果是
				{
					master.setSkillTarget(masterController);
				}
				else if(getTargetPlayer() != null
				&& (skill.targetType[i] == ActiveSkill.TARGET_TYPE_TEAM_SELF
						||skill.targetType[i] == ActiveSkill.TARGET_TYPE_ONESELF)
				&& skill.targetType[0] == ActiveSkill.TARGET_TYPE_TEAM_ENEMY) 
				{
					master.setSkillTarget(masterController);
				}
			}	
			
			int type = 0;

			if(effect instanceof FlashEffect)
			{
//				System.out.println(skill.name+ " PlayerBattleTmp 技能产生---瞬时效果 : "+effect.name+"  技能范围："+skill.effectRangeList);

				FlashEffect fe = (FlashEffect)effect;
				
				if(getTargetPlayer() != null)
				{
					if(fe.type == 6)
					{
						if(!getTargetPlayer().getAttachment().isDead())
							return false;
					}
					else
					{
						if(getTargetPlayer().getAttachment().isDead())
							return false;
					}
				}		
				if(!isTargetRight(skill, i))
					return false;
				
				master.onFlashEffectUse(fe);

				//打除了主目标以外的怪
				if(skill.effectRangeList != ActiveSkill.EFFECT_RANGE_ONE)
				{
		
					if(skill.targetType[i] == ActiveSkill.TARGET_TYPE_PLAYERS)
					{
						master.hitMorePlayerByEffect(effect, skill.effectRangeList);
					}
					else if(skill.targetType[i] == ActiveSkill.TARGET_TYPE_MONSTERS)
					{
						master.hitMoreMonsterByEffect(effect,skill.effectRangeList);
					}
				}
				type = 1;
			}
			else if(effect instanceof TimeEffect)
			{
				if(!isTargetRight(skill, i))
					return false;
//				System.out.println(skill.name+ " PlayerBattleTmp 技能产生---持续效果 : "+effect.name+"  目标范围:"+skill.effectRangeList);
				TimeEffect te = (TimeEffect)effect;
				
				master.onTimeEffectUse(te);
		
				//打除了主目标以外的怪
				if(skill.effectRangeList != ActiveSkill.EFFECT_RANGE_ONE)
				{
	
					if(skill.targetType[i] == ActiveSkill.TARGET_TYPE_PLAYERS)
					{
						master.hitMorePlayerByEffect(te, skill.effectRangeList);
					}
					else if(skill.targetType[i] == ActiveSkill.TARGET_TYPE_MONSTERS)
					{
						master.hitMoreMonsterByEffect(te,skill.effectRangeList);
					}
				}
				type = 2;
			}

			buffer.writeInt(effect.id);
			buffer.writeByte(type);
			master.pack(buffer);

		}
		
		masterController.getPlayer().setMagicPoint(-skill.magic,master);
		
		buffer.writeInt(masterController.getPlayer().magicPoint);

		master.getBattle().dispatchMsg(SMsg.S_BATTLE_ACTION_COMMAND, buffer);
		
		master.getBattle().checkDeadState();	
		
		return true;
	}
	
	private MonsterController getTargetMonster()
	{
		return master.targetMonster;
	}
	
	private PlayerController getTargetPlayer()
	{
		return master.targetPlayer;
	}
	
	private boolean isTargetRight(PetActiveSkill skill,int i)
	{
		SpriteController[] targets = null;
		BattleController bc = master.getBattle();
		if(skill.targetType[i] == ActiveSkill.TARGET_TYPE_PLAYERS)//已方
		{
			if(bc instanceof PVEController || bc instanceof CopyPVEController
					|| bc instanceof GoldPVEController || bc instanceof EventPVEController)
			{
				targets = (SpriteController[]) bc.getPlayerList().toArray();
			}
			else
			{
				if(bc instanceof PVPController)
				{
					if(master.getTeamNo() == BattleController.TEAM1)
						targets = ((PVPController)bc).getPlayers();
					else if(master.getTeamNo() == BattleController.TEAM2)
						targets = ((PVPController)bc).getTargetPlayers();
				}
				else if(bc instanceof PartyPKController)
				{
					if(master.getTeamNo() == BattleController.TEAM1)
						targets = ((PartyPKController)bc).getPlayers();
					else if(master.getTeamNo() == BattleController.TEAM2)
						targets = ((PartyPKController)bc).getTargetPlayers();
				}
				else if(bc instanceof GoldPVPController)
				{
					if(master.getTeamNo() == BattleController.TEAM1)
						targets = ((GoldPVPController)bc).getPlayers();
					else if(master.getTeamNo() == BattleController.TEAM2)
						targets = ((GoldPVPController)bc).getTargetPlayers();
				}
			}
		}
		else if(skill.targetType[i] == ActiveSkill.TARGET_TYPE_MONSTERS)//敌方
		{
			if(bc instanceof PVEController || bc instanceof CopyPVEController
					|| bc instanceof GoldPVEController || bc instanceof EventPVEController)
			{
				if(bc instanceof PVEController)
					targets = ((PVEController)bc).getMonsters();
				else if(bc instanceof CopyPVEController)
					targets = ((CopyPVEController)bc).getMonsters();
				else if(bc instanceof GoldPVEController)
					targets = ((GoldPVEController)bc).getMonsters();
				else if(bc instanceof EventPVEController)
					targets = ((EventPVEController)bc).getMonsters();
			}
			else
			{
				if(bc instanceof PVPController)
				{
					if(master.getTeamNo() == BattleController.TEAM1)
						targets = ((PVPController)bc).getTargetPlayers();
					else if(master.getTeamNo() == BattleController.TEAM2)
						targets = ((PVPController)bc).getPlayers();
				}
				else if(bc instanceof PartyPKController)
				{
					if(master.getTeamNo() == BattleController.TEAM1)
						targets = ((PartyPKController)bc).getTargetPlayers();
					else if(master.getTeamNo() == BattleController.TEAM2)
						targets = ((PartyPKController)bc).getPlayers();
				}
				else if(bc instanceof GoldPVPController)
				{
					if(master.getTeamNo() == BattleController.TEAM1)
						targets = ((GoldPVPController)bc).getTargetPlayers();
					else if(master.getTeamNo() == BattleController.TEAM2)
						targets = ((GoldPVPController)bc).getPlayers();
				}
			}
		}
		Map map = new HashMap();
		for (int j = 0; j < targets.length; j++)
		{
			if(targets[j] == null || targets[j].getAttachment().isDead())
				continue;
			map.put(targets[j].getID(), targets[j]);
		}
		if(skill.targetType[i] == ActiveSkill.TARGET_TYPE_ONESELF)
		{
			if(getTargetPlayer() == null)
				return false;
			if(getTargetPlayer().getID() != masterController.getID())
				return false;
			if(getTargetMonster() != null)
				return false;
		}
		else if(skill.targetType[i] == ActiveSkill.TARGET_TYPE_PLAYERS)
		{
			if(getTargetPlayer() == null)
				return false;
			if(map.get(getTargetPlayer().getID()) == null)
				return false;
		}
		else if(skill.targetType[i] == ActiveSkill.TARGET_TYPE_MONSTERS)//2：敌方
		{
			if(bc instanceof PVEController || bc instanceof CopyPVEController
					|| bc instanceof GoldPVEController || bc instanceof EventPVEController)
			{
				if(getTargetMonster() == null)
					return false;
				if(map.get(getTargetMonster().getID()) == null)
					return false;
				if(getTargetPlayer() != null)
					return false;
			}
			else
			{
				if(getTargetPlayer() == null)
					return false;
				if(map.get(getTargetPlayer().getID()) == null)
					return false;
				if(getTargetMonster() != null)
					return false;
			}
		}
		return true;
	}
}
