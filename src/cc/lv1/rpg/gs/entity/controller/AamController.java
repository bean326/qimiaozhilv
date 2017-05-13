package cc.lv1.rpg.gs.entity.controller;

import vin.rabbit.net.AppMessage;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.collection.i.Map;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.data.DC;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.data.Exp;
import cc.lv1.rpg.gs.data.PressureTest;
import cc.lv1.rpg.gs.entity.container.PlayerContainer;
import cc.lv1.rpg.gs.entity.ext.Bag;
import cc.lv1.rpg.gs.entity.ext.OtherExtInfo;
import cc.lv1.rpg.gs.entity.impl.Goods;
import cc.lv1.rpg.gs.entity.impl.Mail;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsProp;
import cc.lv1.rpg.gs.net.SMsg;
import cc.lv1.rpg.gs.other.ErrorCode;
import cc.lv1.rpg.gs.util.FontConver;

/*
 * apprentice and master
	师徒
 */
public class AamController extends PlayerContainer
{
	
	public static int MASTER_LEVEL = 3000;//20100507从2000修改至3000
	
	public static int CLEAR = 1;
	public static int QUERY = 2;
	public static int MASTER = 1;
	public static int APPRENTICE = 2;
	
	/** 师傅 */
	private PlayerController master;
		
	/** 徒弟 */
	private PlayerController apprentice;
	
	/** 师傅名字 */
	private String masterName;
	
	/** 徒弟名字 */
	private String apprenticeName;

	
	
	public PlayerController getMaster()
	{
		return master;
	}

	public void setMaster(PlayerController master)
	{
		this.master = master;
		masterName = master.getName();
		if(playerList.contains(master))
			return;
		addPlayer(master);
	}

	public PlayerController getApprentice()
	{
		return apprentice;
	}

	public void setApprentice(PlayerController apprentice)
	{
		this.apprentice = apprentice;
		apprenticeName = apprentice.getName();
		if(playerList.contains(apprentice))
			return;
		addPlayer(apprentice);
	}
	
	public void writeTo(ByteBuffer buffer)
	{
		buffer.writeUTF(masterName);
		buffer.writeUTF(apprenticeName);
	}
	
	
	/*
	 * 1、点玩家的形象或者聊天的名字，会有“徒弟”选项。选择设置该玩家为徒弟，在同一个队伍战斗
	 * ，普通地图战斗师傅胜利一次会额外获得5荣誉值，和当前等级0.3%经验。击杀BOSS胜利一次会额外获得50荣誉值，3%经验。
	 * 徒弟普通战斗胜利额外获得当前怪物经验10%加成和1点荣誉，击杀BOSS生胜利额外获得40%经验加成
	 * 和5点荣誉，师徒每天最多只能获得150场这样的奖励。师傅或者徒弟超过100场以后，提示为，“
 * 已经超过150场师徒战斗，不能再获得师徒额外奖励。”
2、只有3000级（包括3000）以下的玩家才能作“徒弟”，师傅需要3001（包括3001）级以上.徒弟超过
3000级的时候提示“他超过3000级，不需要师傅了。”，师傅低于3001，提示“您还不足3001级，想做师傅，
还要努力哦。”
3、师徒获得经验，使用经验倍卡可以翻倍。
4、同一时间只允许设置一个人为师徒，解除关系以后可以再设置
	 */
	/**
	 * 获取师徒关系影响的经验
	 */
	public long getExpByM_A(PlayerController target,BattleController battle)
	{
		if(!(battle instanceof PVEController) && !(battle instanceof CopyPVEController))
		{
			return 0;
		}
		if(!isSameBattle())
		{
			return 0;
		}
		if(battle.getRoom().id == DataFactory.MARRYROOM || battle.getRoom().id == DataFactory.INITROOM 
		|| battle.getRoom().isGoldPartyRoom || battle.getRoom().getParent().id == DataFactory.NOVICEAREA
		|| battle.getRoom().isPartyRoom || battle.getRoom().id == DataFactory.HONORROOM
		|| battle.getRoom().id == DataFactory.SKYROOM)
		{
			return 0;
		}
		if(battle.getRoom().getParent().id == DataFactory.NOVICEAREA)
		{
			return 0;
		}
		long allExp = 0;//怪物掉落总经验
		int honor = 0;
		MonsterController[] monsters = null;
		PVEController pve = null;
		CopyPVEController cpve = null;
		if(battle instanceof PVEController)
		{
			pve = (PVEController) battle;
			monsters = pve.getMonsters();
		}
		else if(battle instanceof CopyPVEController)
		{
			cpve = (CopyPVEController) battle;
			monsters = cpve.getMonsters();
		}
		for (int i = 0; i < monsters.length; i++) 
		{
			allExp += monsters[i].getMonster().experience;
		}
		
		long exp = 0;
		boolean flag = false;
		if(pve == null && cpve != null)
			flag = cpve.isBoss();
		else
			flag = pve.isBoss();
		if(target.getID() == master.getID())
		{
			Exp ex = target.getExpByLevel(target.getPlayer().level+1,1);
			if(flag)
			{
				if(ex != null)
				{
					exp += ex.levelExp * (double)3 / 100;
				}
				honor += 50;
			}
			else
			{
				if(ex != null)
				{
					exp += ex.levelExp * (double)0.3 / 100;
				}
				honor += 5;
			}
		}
		else if(target.getID() == apprentice.getID())
		{
			if(flag)
			{
				exp += allExp * (double)40 / 100;
			}
			else
			{
				exp += allExp * (double)10 / 100;
			}
		}
		OtherExtInfo oei = (OtherExtInfo) target.getPlayer().getExtPlayerInfo("otherExtInfo");
		
		if(oei.aamCount + 1 == OtherExtInfo.MAXAAMCOUNT)
			target.sendAlert(ErrorCode.ALERT_MASAPP_MAX_BATTLE);
		if(oei.aamCount >= OtherExtInfo.MAXAAMCOUNT)
		{	
			return 0;
		}
		else
		{
			if(honor > 0)
			{
				target.setHonour(honor);
				target.sendGetGoodsInfo(1, false, DC.getString(DC.AAM_1)+": "+honor);
			}
			
			//经验卡
			if(target.getPlayer().expMultTime != 0)
			{
				Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
				Goods goods = bag.getExtGoods(2);
				if(goods != null && goods instanceof GoodsProp)
				{
					GoodsProp prop = (GoodsProp) goods;
					if(WorldManager.currentTime - prop.expTimes < target.getPlayer().expMultTime)
					{
						exp *= prop.expMult;
					}
				}
			}

			exp *= target.getExpMult();
		}

		return exp;
	}
	
	/**
	 * 是否在相同的战斗中
	 * @return
	 */
	public String isSame()
	{
		if(master == null || apprentice == null)
		{
			return "master==null||apprentice==null";
		}
		
		if(master.getTeam() == null || apprentice.getTeam() == null)
		{
			return "master.getTeam()==null||apprentice.getTeam()==null";
		}
		
		if(master.getTeam().getPlayer(apprentice.getID()) == null)
		{
			StringBuffer sb = new StringBuffer("mtpId:(");
			PlayerController[] ps = master.getTeam().getPlayers();
			for (int i = 0; i < ps.length; i++)
			{
				if(ps[i] != null)
				{
				    sb.append(ps[i].getID()+" ");
				}
			}
			sb.append("apprenticeId:");
			sb.append(apprentice.getID());
			sb.append(")");
			return sb.toString();
		}
		
		if(apprentice.getTeam().getPlayer(master.getID()) == null)
		{
			StringBuffer sb = new StringBuffer("atpId:(");
			PlayerController[] ps = apprentice.getTeam().getPlayers();
			for (int i = 0; i < ps.length; i++)
			{
				if(ps[i] != null)
				{
				    sb.append(ps[i].getID()+" ");
				}
			}
			sb.append("masterId:");
			sb.append(master.getID());
			sb.append(")");
			return sb.toString();
		}

		return "true";
	}
	
	
	/**
	 * 是否在相同的战斗中
	 * @return
	 */
	public boolean isSameBattle()
	{
		if(master == null || apprentice == null)
		{
			return false;
		}
		
		if(master.getTeam() == null || apprentice.getTeam() == null)
		{
			return false;
		}
		
		if(master.getTeam().getPlayer(apprentice.getID()) == null)
		{
			return false;
		}
		
		if(apprentice.getTeam().getPlayer(master.getID()) == null)
		{
			return false;
		}

		return true;
	}

	/**
	 * 当有一个离开的时候通知另外一个
	 * @param target
	 */
	public void onLeave(PlayerController target)
	{
		
		if(master == null || apprentice == null)
			return;
		
		PlayerController otherNotify = null;
		
		if(target.getName().equals(masterName))
		{
			otherNotify = apprentice;
		}
		else if(target.getName().equals(apprenticeName))
		{
			otherNotify = master;
		}
		

		if(otherNotify == null)
			return;
		
		otherNotify.sendAlert(ErrorCode.ALERT_CLEAR_MAS_APP);
		StringBuffer sb = new StringBuffer();
		sb.append(DC.getString(DC.AAM_2));
		sb.append("[");
		sb.append(otherNotify.getName());
		sb.append("]");
		sb.append(DC.getString(DC.AAM_3));
		target.sendError(sb.toString());
		ByteBuffer buffer = new ByteBuffer();
		buffer.writeByte(CLEAR);
		dispatchMsg(SMsg.S_PLAYER_OPTION_MAS_APP_COMMAND,buffer);
		
		target.setAam(null);
		otherNotify.setAam(null);
		master = null;
		apprentice = null;
		masterName = null;
		apprenticeName = null;
	}
	
	public void optionM_A(PlayerController target,int type)
	{
		if(type == CLEAR)
		{
			onLeave(target);
		}
		else if(type == QUERY)
		{
			writeBaseTo(target);
		}
	}
	
	public void writeBaseTo(PlayerController target)
	{
		ByteBuffer buffer = new ByteBuffer();
		buffer.writeByte(QUERY);
		if(target.getName().equals(masterName))
		{
			buffer.writeByte(MASTER);
			buffer.writeUTF(apprenticeName);
		}
		else if(target.getName().equals(apprenticeName))
		{
			buffer.writeByte(APPRENTICE);
			buffer.writeUTF(masterName);
		}
		else
			return;
		OtherExtInfo oei = (OtherExtInfo) target.getPlayer().getExtPlayerInfo("otherExtInfo");
		buffer.writeInt(OtherExtInfo.MAXAAMCOUNT-oei.aamCount<0?0:OtherExtInfo.MAXAAMCOUNT-oei.aamCount);
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_PLAYER_OPTION_MAS_APP_COMMAND,buffer));
	}

	public String toString()
	{
		return masterName + "  " + apprenticeName;
	}
	
}
