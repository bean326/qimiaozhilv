package cc.lv1.rpg.gs.entity.impl;

import vin.rabbit.comm.GameObject;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.collection.i.List;
import cc.lv1.rpg.gs.data.DC;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.controller.TeamController;
import cc.lv1.rpg.gs.entity.ext.Bag;
import cc.lv1.rpg.gs.net.SMsg;
import cc.lv1.rpg.gs.other.ErrorCode;
import cc.lv1.rpg.gs.util.FontConver;

public class BaseFlayer extends GameObject
{
	/**
	 * 等级上限
	 */
	public int overLevel;
	/**
	 * 图片id
	 */
	public int iconId;
	
	/**
	 * npcid
	 */
	public int NPCId;
	
	/**
	 * 需要roomId
	 */
	public int roomId;
	
	/**
	 * 需要等级
	 */
	public int level;
	
	/**
	 * 需要点数
	 */
	public int point;
	
	/**
	 * 需要钱
	 */
	public int money;
	
	/**
	 * 阵营 1 or 2
	 */
	public int camp;
	
	/**
	 * 家族id
	 */
	public int familyId;
	
	/**
	 * 类型 0: 无( 回比基尼海滩的)
1:初心
2:熟练
3:会心
4:达人
5:奇妙热斗场
6:特殊BOSS
7:专家
8:教授
9:大师
10:宗师
11:副本
12:天上
13:天下
14：天地
15：无双
16：时空之旅(忍者村落)
17：时空之旅(枫叶山谷)
18：黄金斗士
19：活动BOSS
20：活动副本
21：人间试练
22：地狱试练
23：天堂试练
24：青铜圣殿
25：白银圣殿
26：黄金圣殿
27：天神圣殿
	 */
	public int type;
	
	/**
	 * 荣誉值
	 */
	public int honourPoint;
	
	/**
	 * 需要的任务道具
	 */
	public int taskId;
	
	/**
	 * 需要持有物品
	 */
	public int goodsId;
	
	/**
	 * 需要消耗的行动力
	 */
	public int flyActivePoint;
	
	/**
	 * 显示类型，客户端用
	 */
	public String displayType;
	
	/**
	 * 地图说明
	 */
	public String mapshelp;
	
	public boolean checkFly(PlayerController target)
	{
		
		if(target.getPlayer().level < level)
		{
			target.sendAlert(ErrorCode.ALERT_PLAYER_LEVEL_LACK);
			return false;
		}
		
		if(target.getPlayer().level >= overLevel && overLevel > 0)
		{
			target.sendAlert(ErrorCode.ALERT_PLAYER_OVER_LEVEL_ERROR);
			return false;
		}
		
		if(target.getPlayer().flyActivePoint < flyActivePoint)
		{
			target.sendAlert(ErrorCode.ALERT_ACTIVEPOINT_NOT_FULL);
			return false;
		}
		
		if(target.getTeam() != null)
		{
			TeamController tc = target.getTeam();
			
			for (int i = 0; i < tc.getPlayerCount(); i++)
			{
				PlayerController everyone = tc.getPlayerByIndex(i);
				
				if(everyone == null)
					continue;
				
				if(everyone.getPlayer().level < level)
				{
					target.sendAlert(ErrorCode.ALERT_TEAMER_LV_CANNOT);
					return false;
				}
				
				if(everyone.getPlayer().level >= overLevel && overLevel > 0)
				{
					target.sendAlert(ErrorCode.ALERT_TEAM_PLAYER_OVERLEVEL_ERROR);
					return false;
				}
				
				if(everyone.getPlayer().flyActivePoint < flyActivePoint)
				{
					target.sendError("["+everyone.getName()+"]"+DC.getString(DC.ROOM_9));//的行动值不够，不能换房间
					return false;
				}

			}
		}
		
		Bag bag = (Bag)target.getPlayer().getExtPlayerInfo("bag");
		
		if(bag.point < point || bag.money < money)
		{
			target.sendAlert(ErrorCode.ALERT_SHOP_NO_POINT);
			return false;
		}
		
		return true;
	}

	public void sendFly(PlayerController target)
	{
		TeamController team = target.getTeam();
		
		if(team == null)
		{
			Bag bag = (Bag)target.getPlayer().getExtPlayerInfo("bag");
			bag.addMoney(target, -point,-money);
			target.getPlayer().flyActivePoint -= flyActivePoint;
			
			target.sendFlyActivePoint();
//			ByteBuffer buffer = new ByteBuffer(4);
//			buffer.writeInt(target.getPlayer().flyActivePoint);
//			target.getNetConnection().sendMessage(new SMsg(SMsg.S_PLAYER_UPDATEACTIVEPOINT_COMMAND,buffer));
		}
		else
		{
			List playList =  team.getPlayerList();
			int size = playList.size();
			for (int i = 0; i < size; i++)
			{
				PlayerController everyone = (PlayerController)playList.get(i);
				
				if(everyone == null)
					continue;
				
				Bag bag = (Bag)everyone.getPlayer().getExtPlayerInfo("bag");
				bag.addMoney(everyone, -point,-money);
				everyone.getPlayer().flyActivePoint -= flyActivePoint;
				
				everyone.sendFlyActivePoint();
				
//				ByteBuffer buffer = new ByteBuffer(4);
//				buffer.writeInt(everyone.getPlayer().flyActivePoint);
//				everyone.getNetConnection().sendMessage(new SMsg(SMsg.S_PLAYER_UPDATEACTIVEPOINT_COMMAND,buffer));
			}
		}
		
		target.moveToRoom(roomId);	
	}

}
