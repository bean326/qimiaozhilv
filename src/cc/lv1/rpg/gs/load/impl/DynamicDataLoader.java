package cc.lv1.rpg.gs.load.impl;

import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.impl.ArrayList;
import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.entity.TopCenter;
import cc.lv1.rpg.gs.entity.impl.Top;
import cc.lv1.rpg.gs.load.i.ILoader;

/**
 * 动态本地加载器
 * @author dxw
 *
 */
public class DynamicDataLoader implements ILoader
{
	/** 
	 * 玩家等级 1
	 * */
	private String playerLevelStr = GameServer.getAbsolutePath()+"tops/playerLevel.txt";
	
	/**
	 * 宠物等级 2
	 */
	private String playerPetLevelStr = GameServer.getAbsolutePath()+"tops/playerPetLevel.txt";
	
	/**
	 * 玩家最大血 3
	 */
	private String playerMaxHitPointStr = GameServer.getAbsolutePath()+"tops/playerMaxHitPoint.txt";
	
	/**
	 * 玩家最大蓝 4
	 */
	private String playerMaxMagicPointStr = GameServer.getAbsolutePath()+"tops/playerMaxMagicPoint.txt";
	
	/**
	 * 玩家游戏币 5
	 */
	private String playerPointStr = GameServer.getAbsolutePath()+"tops/playerPoint.txt";
	
	/**
	 * 玩家物理攻击 6
	 */
	private String playerPhsAttStr = GameServer.getAbsolutePath()+"tops/playerPhsAtt.txt";
	
	/**
	 * 玩家精神攻击力 7
	 */
	private String playerSptAttStr = GameServer.getAbsolutePath()+"tops/playerSptAtt.txt";
	
	/**
	 * 玩家物理暴击率 8
	 */
	private String playerPhsSmiteRateStr = GameServer.getAbsolutePath()+"tops/playerPhsSmiteRate.txt";
	
	/**
	 * 玩家精神暴击率 9
	 */
	private String playerSptSmiteRateStr = GameServer.getAbsolutePath()+"tops/playerSptSmiteRate.txt";
	
	/**
	 * 玩家物理暴击伤害 10
	 */
	private String playerPhsSmiteHurtParmStr = GameServer.getAbsolutePath()+"tops/playerPhsSmiteHurtParm.txt";
	
	/**
	 * 玩家精神暴击伤害 11
	 */
	private String playerSptSmiteHurtParmStr = GameServer.getAbsolutePath()+"tops/playerSptSmiteHurtParm.txt";
	
	/**
	 * 玩家物理免伤 12
	 */
	private String playerPhsHurtAvoidStr = GameServer.getAbsolutePath()+"tops/playerPhsHurtAvoid.txt";
	
	/**
	 * 玩家精神免伤 13
	 */
	private String playerSptHurtAvoidStr = GameServer.getAbsolutePath()+"tops/playerSptHurtAvoid.txt";
	
	/**
	 * 玩家辅助值 14
	 */
	private String playerCurePointStr = GameServer.getAbsolutePath()+"tops/playerCurePoint.txt";
	
	
	public void loading()
	{
		try
		{
			TopCenter.getInstance().clear();
			
			TopCenter.getInstance().put(1, Utils.loadFileVariables(playerLevelStr, Top.class));
			TopCenter.getInstance().put(2, Utils.loadFileVariables(playerPetLevelStr, Top.class));
			TopCenter.getInstance().put(3, Utils.loadFileVariables(playerMaxHitPointStr, Top.class));
			TopCenter.getInstance().put(4, Utils.loadFileVariables(playerMaxMagicPointStr, Top.class));
			TopCenter.getInstance().put(5, Utils.loadFileVariables(playerPointStr, Top.class));
			TopCenter.getInstance().put(6, Utils.loadFileVariables(playerPhsAttStr, Top.class));
			TopCenter.getInstance().put(7, Utils.loadFileVariables(playerSptAttStr, Top.class));
			TopCenter.getInstance().put(8, Utils.loadFileVariables(playerPhsSmiteRateStr, Top.class));
			TopCenter.getInstance().put(9, Utils.loadFileVariables(playerSptSmiteRateStr, Top.class));
			TopCenter.getInstance().put(10, Utils.loadFileVariables(playerPhsSmiteHurtParmStr, Top.class));
			TopCenter.getInstance().put(11, Utils.loadFileVariables(playerSptSmiteHurtParmStr, Top.class));
			TopCenter.getInstance().put(12, Utils.loadFileVariables(playerPhsHurtAvoidStr, Top.class));
			TopCenter.getInstance().put(13, Utils.loadFileVariables(playerSptHurtAvoidStr, Top.class));
			TopCenter.getInstance().put(14, Utils.loadFileVariables(playerCurePointStr, Top.class));
		}  
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}


	public void setAttachment(Object obj)
	{
		
	}
	
	
	public static void main(String[] args)
	{
		new DynamicDataLoader().loading();
	}
}
