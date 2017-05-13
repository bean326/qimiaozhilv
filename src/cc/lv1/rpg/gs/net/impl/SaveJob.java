package cc.lv1.rpg.gs.net.impl;

import java.text.SimpleDateFormat;

import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.data.PressureTest;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.ext.Bag;
import cc.lv1.rpg.gs.entity.ext.PVPInfo;
import cc.lv1.rpg.gs.entity.ext.TaskInfo;
import cc.lv1.rpg.gs.entity.impl.Player;
import cc.lv1.rpg.gs.entity.impl.Task;
import cc.lv1.rpg.gs.gui.MainFrame;
import vin.rabbit.net.abs.NetConnection;
import vin.rabbit.net.job.i.NetJob;

/**
 * 保存
 * @author balan
 *
 */
public class SaveJob extends NetJob {

	private WorldManager world;
	
	private PlayerController target;
	
	private Player player;
	
	/** 正常保存 */
	public static final int TIME_SAVE = 1;
	/** 合区保存 */
	public static final int MERGE_SAVE = 2;
	/** 下线保存 */
	public static final int CLOSE_SAVE = 3;
	/** 下线报错再保存 */
	public static final int CLOSE_ERROR_SAVE = 4;
	/** 增加物品保存 */
	public static final int GOODS_ADD_SAVE = 5;
	/** 删除物品保存 */
	public static final int GOODS_DEL_SAVE = 6;
	/** 交易保存 */
	public static final int BUSINESS_SAVE = 7;
	/** 物品出售保存 */
	public static final int GOODS_SALE_SAVE = 8;
	/** 抽奖宝箱消费保存 */
	public static final int MONEY_EXPENSE_SAVE = 9;
	/** 族员冲值给族长分红保存 */
	public static final int FAMILY_LEADER_SAVE = 10;
	/** 家族创建保存 */
	public static final int FAMILY_CREATE_SAVE = 11;
	/** 退出家族保存 */
	public static final int FAMILY_OUT_SAVE = 12;
	/** 族长变更保存 */
	public static final int FAMILY_CHANGE_SAVE = 13;
	/** 家族解散保存 */
	public static final int FAMILY_REMOVE_SAVE = 14;
	/** 族长T人保存 */
	public static final int FAMILY_KICK_SAVE = 15;
	/** 家族添加族员保存 */
	public static final int FAMILY_ADD_SAVE = 16;
	/** 玩家升级保存 */
	public static final int PLAYER_LEVELUP_SAVE = 17;
	/** 名字修改保存 */
	public static final int NAME_MODIFY_SAVE = 18;
	/** 离婚保存 */
	public static final int MARRY_CANCEL_SAVE = 19;
	/** 寄卖中心保存 */
	public static final int SHOP_CENTER_SAVE = 20;
	/** 发送离线邮件保存 */
	public static final int MAIL_OFFLINE_SAVE = 21;
	/** 邮件缓存保存 */
	public static final int MAIL_CACHE_SAVE = 22;
	/** 安全退出保存 */
	public static final int SAFE_OUT_GAME_SAVE = 23;
	/** 角色创建保存 */
	public static final int ROLE_CREATE_SAVE = 24;
	
	private int type;
	
	public SaveJob(WorldManager world,PlayerController target,int type)
	{
		this.world = world;
		this.target = target;
		this.player = target.getPlayer();
		this.type = type;
	}
	
	public SaveJob(WorldManager world,Player player,int type)
	{
		this.world = world;
		this.player = player;
		this.type = type;
	}
	
	public NetConnection getConnection() 
	{
		if(target==null)
			return null;
		return target.getNetConnection();
	}


	public void run() 
	{
		boolean isSuccess = false;
		try
		{
			isSuccess = world.getDatabaseAccessor().savePlayer(player);
		}
		catch(Exception e)
		{
			isSuccess = world.getDatabaseAccessor().savePlayer(player);
		}
		if(!isSuccess)
			System.out.println("---------save failed:"+player.name+" account:"+player.accountName+" id:"+player.id);
		
		if(type == CLOSE_SAVE || type == CLOSE_ERROR_SAVE)
		{
			try
			{
				Player bakPlayer = world.getDatabaseAccessor().getBakPlayer(player.accountName);
				if(bakPlayer != null)
				{
					int bakZ = bakPlayer.getZhuanshengState();
					int locZ = player.getZhuanshengState();
					if(bakZ == locZ)
					{
						if(bakPlayer.experience <= player.experience)
						{
							world.getDatabaseAccessor().savePlayerToBak(player);
						}
					}	
					else if(bakZ < locZ)
					{
						world.getDatabaseAccessor().savePlayerToBak(player);
					}
				}
				else
					world.getDatabaseAccessor().savePlayerToBak(player);
			}
			catch(Exception e)
			{
				Player bakPlayer = world.getDatabaseAccessor().getBakPlayer(player.accountName);
				if(bakPlayer != null)
				{
					int bakZ = bakPlayer.getZhuanshengState();
					int locZ = player.getZhuanshengState();
					if(bakZ == locZ)
					{
						if(bakPlayer.experience <= player.experience)
						{
							world.getDatabaseAccessor().savePlayerToBak(player);
						}
					}	
					else if(bakZ < locZ)
					{
						world.getDatabaseAccessor().savePlayerToBak(player);
					}
				}
				else
					world.getDatabaseAccessor().savePlayerToBak(player);
			}
		}
	}

}
