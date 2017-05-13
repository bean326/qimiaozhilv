package cc.lv1.rpg.gs.entity.ext;

import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.impl.ArrayList;
import vin.rabbit.util.collection.impl.HashMap;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.impl.Player;
import cc.lv1.rpg.gs.entity.impl.battle.effect.Effect;
import cc.lv1.rpg.gs.entity.impl.battle.effect.TimeEffect;
import cc.lv1.rpg.gs.entity.impl.battle.skill.ActiveSkill;
import cc.lv1.rpg.gs.entity.impl.battle.skill.PassiveSkill;
import cc.lv1.rpg.gs.entity.impl.battle.skill.Skill;
/**
 * 技能组
 * @author dxw
 *
 */
public class SkillTome extends PlayerExtInfo
{
	/**
		1军官,2足球,3护士,4超人
	 */
	public static int professionCount[] = new int[4]; 

	/**
	 * 主动技能
	 */
	private ActiveSkill [] activeSkills = new ActiveSkill[0];
	
	/**
	 * 被动技能
	 */
	private PassiveSkill [] passiveSkills = new PassiveSkill[0];
	
	/**
	 * 当前职业可以学习的技能
	 */
	private Skill [] skills = null;
	
	/**
	 * 自动技能编排保存
	 */
	private int [] autoSkills = new int[6];
	
	public SkillTome()
	{

	}
	

	public Skill getSkill(int id)
	{
		for (int i = 0; i < skills.length; i++)
		{
			if(skills[i].id == id)
				return skills[i];
		}
		return null;
	}
	
	
	public void writeTo(ByteBuffer buffer)
	{
		String name = "";
		for (int i = 0; i < skills.length; i++)
		{
			if(name.equals(skills[i].name))
				continue;
			
			if(skills[i].isStudied)
			{
				if(isNextLv(i,skills[i].level))
					continue;
			}
			else
			{
				if(isPreLv(i,skills[i].level))
					continue;
			}

			buffer.writeInt(skills[i].id);
			buffer.writeInt(skills[i].iconId);
			buffer.writeUTF(skills[i].name);
			buffer.writeInt(skills[i].level);
			buffer.writeBoolean(skills[i].isStudied);
			buffer.writeBoolean(skills[i].isPubSkill);
			buffer.writeByte(skills[i].targetType[0]);
			buffer.writeInt(skills[i].CDTimer);
			if(skills[i] instanceof ActiveSkill)
			{
				ActiveSkill as = (ActiveSkill)skills[i];
				buffer.writeInt(as.magic);
				buffer.writeUTF(skillJob(as.profession));
				Object obj = DataFactory.getInstance().getGameObject(as.effectList[0]);
				if(obj instanceof TimeEffect && obj != null)
				{
					TimeEffect te = (TimeEffect) obj;
					if(te.haloType == 1)
						buffer.writeBoolean(true);
					else 
						buffer.writeBoolean(false);
				}
				else
					buffer.writeBoolean(false);
			}
			else if(skills[i] instanceof PassiveSkill)
			{
				PassiveSkill ps = (PassiveSkill) skills[i];
				buffer.writeInt(0);
				buffer.writeUTF(skillJob(ps.profession));
				buffer.writeBoolean(false);
			}
			buffer.writeInt(skills[i].order);
			
			
			
			if(!skills[i].isStudied)
				name = skills[i].name;
			
//			System.out.println(skills[i].name + " "+skills[i].level +" "+skills[i].isStudied);
		}
	}

	
	private String skillJob(int[] job)
	{
		String result = "";
		if(job[0] == 0)
		{
			result = "0";
		}
		else
		{
			for (int i = 0; i < job.length; i++) 
			{
				if(job[i] == 0 && i != 0)
					break;
				if(result.isEmpty())
					result += job[i];
				else
					result += ":"+job[i];
			}
		}
		return result;
	}
	
	private boolean isPreLv(int index,int currlv)
	{
		try
		{
			if(index > 0)
			{
				if(skills[index-1].level < currlv)
				return true;
			}
		}
		catch(NullPointerException e)
		{
			return false;
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			return false;
		}
		return false;
	}

	private boolean isNextLv(int index,int currlv)
	{
		try
		{
			if(skills[index+1].level > currlv
					&&
				skills[index+1].isStudied)
				return true;
		}
		catch(NullPointerException e)
		{
			return false;
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			return false;
		}
		return false;
	}
	
	public void writeActiveSkill(ByteBuffer buffer)
	{
		int skillLength = activeSkills.length;
		
		buffer.writeInt(skillLength);
		for(int i = 0 ; i < skillLength ; i ++)
		{
			buffer.writeInt(activeSkills[i].id);
		}
	}
	
	public void writePassiveSkill(ByteBuffer buffer)
	{
		int skillLength = passiveSkills.length;
		for(int i = 0 ; i < skillLength ; i ++)
		{
			passiveSkills[i].writeTo(buffer);
		}
	}
	
	public void initBaseSkill(int []skillIds,Player player)
	{
		List skillList = getProfessionSkills(player);
		
		Object[] objs = skillList.toArray();
		
		skills = new Skill[objs.length];
		for (int i = 0; i < objs.length; i++)
		{
			skills[i] = (Skill)Skill.cloneObject((Skill)objs[i]);
		}
		
		for(int i = 0 ; i < skillIds.length ; i ++)
		{
			addSkill(skillIds[i]);
		}

	}
	
	public void updateProfessionSkills(Player player)
	{
		if(SkillTome.professionCount[player.profession-1] == skills.length)
			return; //没有新技能的时候返回
			
		HashMap stuMap = new HashMap();
		
		for(int i = 0 ; i < skills.length ; i ++)
		{
			stuMap.put(skills[i].id, skills[i].isStudied);
		}
		
	    
		Object[] objs = getProfessionSkills(player).toArray();
		
		Skill []skillsTmp = new Skill[objs.length];

		for (int i = 0; i < skillsTmp.length; i++)
		{
			skillsTmp[i] = (Skill)Skill.cloneObject((Skill)objs[i]);
			
			Boolean isStuday = (Boolean)stuMap.get(skillsTmp[i].id);
			if(isStuday == null)
				continue;
			
			skillsTmp[i].isStudied = isStuday;
		}
		skills = skillsTmp;
	}


	public List getProfessionSkills(Player player)
	{
		DataFactory dataFactory = DataFactory.getInstance();

		List skillsList = dataFactory.getSkillList();
		
		List skillList = new ArrayList();

		int size = skillsList.size();
		for (int i = 0; i < size; i++)
		{
			Skill skill = (Skill)skillsList.get(i);
			
			if(skill instanceof ActiveSkill)
			{
				ActiveSkill as = ((ActiveSkill)skill);
				
				if(as.profession[0] == 15)
					continue;
				boolean isPubSkill = true;

				for (int j = 0; j < as.profession.length; j++)
				{
					if(j == 0)
					{
						if(as.profession[j] != 0 && as.profession[j+1] == 0)
							isPubSkill = false;
						if(as.profession[j] == player.profession && as.profession[j+2] == 0)
							isPubSkill = false;
						if(as.profession[j+1] == player.profession && as.profession[j+2] == 0)
							isPubSkill = false;
					}
					else
					{
						if(as.profession[j] == 0)
							break;
					}
					
					if(isPubSkill)
					{
						as.isPubSkill = isPubSkill;
						int num = player.profession+3;
						
						if(as.profession[0] == 0 || as.profession[j] == player.profession 
							|| as.profession[j] == player.profession+num
							|| as.profession[j] == player.profession+num+1)
						{
							if(!skillList.contains(as))
								skillList.add(as);
						}
						break;
					}
					else
					{
						int count = player.profession+3;
						
						if(as.profession[j] == player.profession 
							|| as.profession[j] == player.profession+count
							|| as.profession[j] == player.profession+count+1)
						{
							as.isPubSkill = isPubSkill;
							if(!skillList.contains(as))
								skillList.add(as);
						}
					}
				}
			}
			else if(skill instanceof PassiveSkill)
			{
				PassiveSkill pSkill = (PassiveSkill) skill;
				if(pSkill.profession[0] == 15)
					continue;
				
				boolean isPubSkill = true;

				for (int j = 0; j < pSkill.profession.length; j++)
				{
					if(j == 0)
					{
						if(pSkill.profession[j] != 0 && pSkill.profession[j+1] == 0)
							isPubSkill = false;
						if(pSkill.profession[j] == player.profession && pSkill.profession[j+2] == 0)
							isPubSkill = false;
						if(pSkill.profession[j+1] == player.profession && pSkill.profession[j+2] == 0)
							isPubSkill = false;
					}
					else
					{
						if(pSkill.profession[j] == 0)
							break;
					}
					
					if(isPubSkill)
					{
						pSkill.isPubSkill = isPubSkill;
						int num = player.profession+3;
						
						if(pSkill.profession[0] == 0 || pSkill.profession[j] == player.profession 
							|| pSkill.profession[j] == player.profession+num
							|| pSkill.profession[j] == player.profession+num+1)
						{
							if(!skillList.contains(pSkill))
								skillList.add(pSkill);
						}
						break;
					}
					else
					{
						int count = player.profession+3;
						
						if(pSkill.profession[j] == player.profession 
							|| pSkill.profession[j] == player.profession+count
							|| pSkill.profession[j] == player.profession+count+1)
						{
							pSkill.isPubSkill = isPubSkill;
							if(!skillList.contains(pSkill))
								skillList.add(pSkill);
						}
					}
				}
			}
		}
		return skillList;
	}
	
	public String getName()
	{
		return "skillTome";
	}


	public void loadFrom(ByteBuffer buffer)
	{

		DataFactory dataFactory = DataFactory.getInstance();
		
		int length = buffer.readInt();
		
		skills = new Skill[length];
		
		for (int i = 0; i < length; i++)
		{
			int id = buffer.readInt();
			boolean isStudied = buffer.readBoolean();
			boolean isPubSkill = buffer.readBoolean();

			Skill skill = (Skill)dataFactory.getGameObject(id);
			if(skill == null)
			{
				System.out.println("load skillTome skillID error:"+id);
				continue;
			}
			skill = (Skill)Skill.cloneObject(skill);
			skill.isStudied = isStudied;
			skill.isPubSkill = isPubSkill;
			
			if(skill.isStudied)
			{
				addSkill(skill);
			}
			skills[i] = skill;
		}
		
		for (int i = 0; i < autoSkills.length; i++)
		{
			autoSkills[i] = buffer.readInt();
		}
	}


	public void saveTo(ByteBuffer buffer)
	{

		buffer.writeInt(skills.length);
		for (int i = 0; i < skills.length; i++)
		{
//			if(skills[i].isStudied)
//				System.out.println("已经学过："+skills[i].name);
			buffer.writeInt(skills[i].id);
			buffer.writeBoolean(skills[i].isStudied);
			buffer.writeBoolean(skills[i].isPubSkill);
		}
		
		for (int i = 0; i < autoSkills.length; i++)
		{
			buffer.writeInt(autoSkills[i]);
		}
	}

	public void addSkill(int skillId)
	{
		Skill skill = getSkill(skillId);
		if(skill == null)
			return;
		addSkill(skill);
	}
	
	private void addSkill(Skill extInfo)
	{
		extInfo.isStudied = true;
		
		if(extInfo instanceof ActiveSkill)
		{
			ActiveSkill [] infos = new ActiveSkill[activeSkills.length+1];
			for (int i = 0; i < activeSkills.length; i++)
				infos[i] = activeSkills[i];
			infos[activeSkills.length] = (ActiveSkill)extInfo;
			activeSkills = infos;
		}
		else if(extInfo instanceof PassiveSkill)
		{
			PassiveSkill [] infos = new PassiveSkill[passiveSkills.length+1];
			for (int i = 0; i < passiveSkills.length; i++)
				infos[i] = passiveSkills[i];
			infos[passiveSkills.length] = (PassiveSkill)extInfo;
			passiveSkills = infos;
		}
	}
	
	/**
	 * 获取主动技能
	 * @param id
	 * @return
	 */
	public ActiveSkill getActiveSkill(int id)
	{
		int length = activeSkills.length;
		for(int i = 0 ; i < length ; i ++)
		{
			if(id == activeSkills[i].id)
				return activeSkills[i];
		}
		return null;
	}
	
	/**
	 * 获取被动技能
	 * @param id
	 * @return
	 */
	public PassiveSkill getPassiveSkill(int id)
	{
		int length = passiveSkills.length;
		for(int i = 0 ; i < length ; i ++)
		{
			if(id == passiveSkills[i].id)
				return passiveSkills[i];
		}
		return null;
	}

	public List getHaloSkills()
	{
		List aSkill = new ArrayList(6);
		for (int i = 0; i < activeSkills.length; i++)
		{
			if(activeSkills[i].isHalo())
			{
				aSkill.add(activeSkills[i]);//光环
			}
		}
		return aSkill;
	}
	
	public PassiveSkill[] getPassiveSkills()
	{
		return this.passiveSkills;
	}
	
	public ActiveSkill[] getActiveSkills()
	{
		return this.activeSkills;
	}
	
	public Skill[] getSkills()
	{
		return this.skills;
	}
		
	public void updateSkill(Skill skill,int type)
	{
		for (int i = 0; i < skills.length; i++)
		{
			if(skills[i].iconId == skill.iconId)
			{
				skills[i] = skill;
				break;
			}
		}
		Skill[] sks = null;
		if(type == 1)
			sks = activeSkills;
		else if(type == 2)
			sks = passiveSkills;
		for (int i = 0; i < sks.length; i++)
		{
			if(sks[i].iconId == skill.iconId)
			{
				skill.isStudied = true;
				sks[i] = skill;
				break;
			}
		}
	}
	
	/**
	 * 是否是更低等级的技能没学
	 * @param skill
	 * @return
	 */
	public boolean isLowLevelNoStudy(Skill skill)
	{
		for (int i = 0; i < skills.length; i++) 
		{
			if(skills[i] == null)
				continue;
			if(skills[i].id == skill.id)
				continue;
			if(skills[i].iconId == skill.iconId)
			{
//				System.out.println(skills[i].name+"//////"+skills[i].level+"///////"+skills[i].isStudied);
				if(skills[i].level < skill.level && !skills[i].isStudied)
				{
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 是否有更高等级的技能已经学习
	 * @param skill
	 * @return
	 */
	public boolean isHighLevelStudy(Skill skill)
	{
		for (int i = 0; i < skills.length; i++) 
		{
			if(skills[i] == null)
				continue;
			if(skills[i].id == skill.id)
				continue;
			if(skills[i].iconId == skill.iconId)
			{
				if(skills[i].level > skill.level && skills[i].isStudied)
				{
					return true;
				}
			}
		}
		return false;
	}

	public void setAutoSkill(int[] ids)
	{
		autoSkills = ids;
	}
	
	public void sendAutoSkill(ByteBuffer buffer)
	{
		for (int i = 0; i < autoSkills.length; i++)
		{
			buffer.writeInt(autoSkills[i]);
		}
	}
	
	/**
	 * 进入战斗后把主动技能的开始CD时间清零
	 */
	public void setZeroToProcessTimer()
	{
		for (int i = 0; i < activeSkills.length; i++)
		{
			if(activeSkills[i] == null)
				continue;
			activeSkills[i].processTime = 0;
		}
	}
	
	
	public List getActiveSkillByIconId(int iconId)
	{
		List list = new ArrayList(activeSkills.length);
		for (int i = 0; i < activeSkills.length; i++)
		{
			if(activeSkills[i] == null)
				continue;
			if(activeSkills[i].iconId == iconId)
				list.add(activeSkills[i]);
		}
		return list;
	}
}
