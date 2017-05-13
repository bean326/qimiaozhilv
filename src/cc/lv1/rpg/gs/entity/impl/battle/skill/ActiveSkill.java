package cc.lv1.rpg.gs.entity.impl.battle.skill;

import java.text.SimpleDateFormat;

import vin.rabbit.comm.GameObject;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.Utils;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.impl.battle.effect.Effect;
import cc.lv1.rpg.gs.entity.impl.battle.effect.TimeEffect;
import cc.lv1.rpg.gs.gui.MainFrame;

/**
 * 主动技能
 * @author dxw
 *
 */
public class ActiveSkill extends Skill
{
	
	public static final int TARGET_TYPE_TEAM_SELF = 1;
	
	public static final int TARGET_TYPE_TEAM_ENEMY = 2;
	
	public static final int TARGET_TYPE_ONESELF = 3;
	
	public static final int ERROR = -1;
	
	public static final int EFFECT_RANGE_ONE = 1;
	
	/**
	 * 效果编号
	 */
	public int [] effectList = new int[3];
	
	/**
	 * 技能范围
		1：目标单体
		2：两个目标
		3：三个目标
		4：四个目标
		5：五个目标
		6：六个目标
		7：目标全体
	 */
	public int effectRangeList;
	
	/**
		0：通用       1：士兵
		2：运动员 3：医护人员
		4：超能力者       5：军官
		6：特种兵
		7：足球运动员
		8： 篮球运动员    9：医生  
		10：护士
		11：超心波感应者
		12：超体能感应者
		-1: 怪物技能
	 */
	public int [] profession = new int[7];
	
	/**
		0：所有武器通用
		1：足球
		2：篮球
		3：手枪
		4：火箭筒
		5：手术刀
		6：针筒
		7：墨镜
		8：扑克牌
	 */
	public int weaponType;
	
	/**
	 * 效果2几率
	 */
	public int rate2;

	/**
	 * 效果3几率
	 */
	public int rate3;
	
	/**
	 * 仇恨值
	 */
	public int hate;

	
	/**
	 * 耗蓝
	 */
	public int magic = 0;
	
	/**
	 * 技能释放开始时间(进入战斗后清零)
	 */
	public long processTime = 0;
	
	public void writeTo(ByteBuffer buffer)
	{
		buffer.writeInt(id);
		buffer.writeUTF(name);
		buffer.writeInt(level);
		buffer.writeByte(effectRangeList);
		buffer.writeInt(magic);
	}
	
/*	public void writeBattleTo(ByteBuffer buffer)
	{
		buffer.writeInt(id);
		buffer.writeUTF(name);
		buffer.writeByte(effectRangeList);
		buffer.writeInt(targetType[0]);
		buffer.writeInt(CDTimer);
		buffer.writeInt(magic);
		
	}*/
	
	public void copyTo(GameObject gameObject)
	{
		super.copyTo(gameObject);
		
		ActiveSkill aSkill = (ActiveSkill)gameObject;
		int [] tmp = new int[3];
		System.arraycopy(effectList, 0, tmp, 0, tmp.length);
		aSkill.effectList = tmp;
		aSkill.effectRangeList = effectRangeList;

		tmp = new int[7];
		System.arraycopy(profession, 0 , tmp, 0, tmp.length);
		aSkill.profession = tmp;
		
		aSkill.weaponType = weaponType;
		aSkill.rate2 = rate2;
		aSkill.rate3 = rate3;

		aSkill.magic = magic;
		
		aSkill.hate = hate;
		aSkill.processTime = processTime;
	}
	
	public boolean setVariable(String key,String value)
	{
		if(key.equals("effectList"))
		{
			String [] strs = Utils.split(value, ":");
			for(int i = 0 ; i < effectList.length ; i ++)
			{
				effectList[i] = Integer.parseInt(strs[i]);
			}
			return true;
		}
		else if(key.equals("profession"))
		{
			String [] strs = Utils.split(value, ":");
			for(int i = 0 ; i < profession.length ; i ++)
			{
				profession[i] = Integer.parseInt(strs[i]);
			}
			return true;
		}
		else 
		{
			return super.setVariable(key, value);
		}
	}
	
	public boolean checkMagicEnough(int magicPoint)
	{
		return magic <= magicPoint;
	}
	
	public boolean checkMagicEnough(PlayerController target)
	{
		return checkMagicEnough(target.getPlayer().magicPoint);
	}

	public int[] getEffectList()
	{
		return effectList;
	}

	public void setEffectList(int[] effectList)
	{
		this.effectList = effectList;
	}

	public int getEffectRangeList()
	{
		return effectRangeList;
	}

	public void setEffectRangeList(int effectRangeList)
	{
		this.effectRangeList = effectRangeList;
	}

	public int[] getProfession()
	{
		return profession;
	}

	public void setProfession(int[] profession)
	{
		this.profession = profession;
	}

	public int getWeaponType()
	{
		return weaponType;
	}

	public void setWeaponType(int weaponType)
	{
		this.weaponType = weaponType;
	}

	public int getRate2()
	{
		return rate2;
	}

	public void setRate2(int rate2)
	{
		this.rate2 = rate2;
	}

	public int getRate3()
	{
		return rate3;
	}

	public void setRate3(int rate3)
	{
		this.rate3 = rate3;
	}

	public int getMagic()
	{
		return magic;
	}

	public void setMagic(int magic)
	{
		this.magic = magic;
	}
	
	
	/**
	 * 是否是光环
	 * @return
	 */
	public boolean isHalo()
	{
		if(effectList[0] == 0)
			return false;
		Effect effect = (Effect) DataFactory.getInstance().getGameObject(effectList[0]);
		if(effect instanceof TimeEffect)
		{
			TimeEffect te = (TimeEffect) effect;
			if(te.haloType == 1)
				return true;
		}
		return false;
	}
	

	
	
	/**
	 * 技能能否释放(CD时间检测)
	 * @return
	 */
	public boolean isProcessSkill(PlayerController target)
	{
		if(processTime == 0)
			return true;
		int speed = 0;
		if(target.getAttachment() == null || target.getAttachment().getCDTimer() == 0)
			speed = target.getPlayer().getBaseInfo().speed;
		else 
			speed = target.getAttachment().getCDTimer();
		
		if(CDTimer > speed)
			speed = CDTimer;
		
//		SimpleDateFormat loTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		long time = target.getNetConnection().getLastReadTime();
		if(time < speed + processTime - 800)
		{

			if(++target.wgCount >= 1)
			{
//				MainFrame.println("ip : "+target.getNetConnection().getIP()+" accountName : "+target.getPlayer().accountName+"  playerName:"+target.getName());
//				target.close();
//				MainFrame.println(target.getNetConnection().getIP()+"  name:"+target.getName()+"  WG ActiveSkill : "+id+"  skill cdTimer:"+CDTimer);
//				MainFrame.println("name:"+target.getName()+"  Before use skill time："+loTime.format(processTime)+"  This use skill time： "+loTime.format(time));
				target.wgCount = 0;
				return false;
			}
		}
		
		return true;
	}
	
}
