package cc.lv1.rpg.gs.data;

import vin.rabbit.util.Utils;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.ext.Bag;
import cc.lv1.rpg.gs.entity.impl.Goods;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsPetEquip;
import cc.lv1.rpg.gs.util.FontConver;

/**
 * DC:DataContans
 * @author Administrator
 *
 */
public class DC    
{//([XX]:玩家名[FF]:家族名[GG]:物品名[MM]:怪物名[NN]:数量[TT]:类型)
	/*********************Database***********************************/
	public static final int BASE_1 = 10001;//游戏管理员
	public static final int BASE_2 = 10002;//礼包
	public static final int BASE_3 = 10003;//系统
	public static final int BASE_4 = 10004;//家族基金
	public static final int BASE_5 = 10005;//你的族员
	public static final int BASE_6 = 10006;//充值
	public static final int BASE_7 = 10007;//元宝
	public static final int BASE_8 = 10008;//您将获得一笔家族基金(百分之二十的代金卷)!!!
	public static final int BASE_9 = 10009;//登陆
	public static final int BASE_10 = 10010;//目标
	public static final int BASE_11 = 10011;//状态
	public static final int BASE_12 = 10012;//禁
	public static final int BASE_13 = 10013;//解
	public static final int BASE_14 = 10014;//封
	public static final int BASE_15 = 10015;//金
	public static final int BASE_16 = 10016;//元
	public static final int BASE_17 = 10017;//恭喜你充值成功，充值金额为[NN]元宝。你的家族为[FF]，族长是[XX]，族长获得一笔家族基金！！！//自己不是族长
	public static final int BASE_18 = 10018;//恭喜你充值成功，充值金额为[NN]元宝。你的家族为[FF]，族长是[XX]。//自己是族长
	public static final int BASE_19 = 10019;//恭喜你充值成功，充值金额为[NN]元宝。你目前没有家族。//没有家族的时候
	public static final int BASE_20 = 10020;//充值通知

	/*********************Invitation***********************************/
	public static final int INVITE_1 = 20001;//对方取消邀请或超时
	public static final int INVITE_2 = 20002;//拒绝了你的交易邀请!
	public static final int INVITE_3 = 20003;//拒绝了你的切磋邀请!
	public static final int INVITE_4 = 20004;//拒绝了你的结婚邀请!
	public static final int INVITE_5 = 20005;//[XX1]与[XX2]携手步入婚姻的殿堂,他们宣誓相爱一生一世,让我们为这幸福的一对人儿欢呼吧!
	public static final int INVITE_6 = 20006;//恭喜[XX1]与[XX2]结为夫妻,让我们大家一起来祝福他们吧!
	public static String getMarryString(boolean isVip,String name1,String name2)
	{
		String str = "";
		if(isVip)
			str = DC.getString(INVITE_5);
		else
			str = DC.getString(INVITE_6);
		str = str.replace("XX1", name1);
		str = str.replace("XX2", name2);
		return str;
	}
	/*********************ShopCenter***********************************/
	public static final int SHOPCENTER_1 = 30001;//取消寄卖
	public static final int SHOPCENTER_2 = 30002;//寄卖系统
	public static final int SHOPCENTER_3 = 30003;//卖出确认
	public static final int SHOPCENTER_4 = 30004;//您的
	public static final int SHOPCENTER_5 = 30005;//已经卖出成功!!!
	public static final int SHOPCENTER_6 = 30006;//购买返还
	public static final int SHOPCENTER_7 = 30007;//开始寄卖
	public static final int SHOPCENTER_8 = 30008;//寄卖返回时玩家正在登陆寄卖将增加十分钟
	public static final int SHOPCENTER_9 = 30009;//时间返还
	public static final int SHOPCENTER_10 = 30010;//寄卖中心
	public static final int SHOPCENTER_11 = 30011;//您所寄卖的物品已到期
	public static final int SHOPCENTER_12 = 30012;//交易BusinessController
	/*********************Battle***********************************/
	public static final int BATTLE_1 = 40001;//系统
	public static final int BATTLE_2 = 40002;//礼物赠送
	public static final int BATTLE_3 = 40003;//尊敬的玩家,您用自己的10元宝让全队原地复活,这种侠义的行为让我们深感佩服,特赠送您10代金券,希望您游戏愉快
	public static final int BATTLE_4 = 40004;//副本已刷新,请重新进行闯关!
	public static final int BATTLE_5 = 40005;//挂机自动打怪中
	public static final int BATTLE_6 = 40006;//该副本BOSS已全部被击杀!
	public static final int BATTLE_7 = 40007;//我的队伍
	public static final int BATTLE_8 = 40008;//带领全队原地复活!
	public static final int BATTLE_9 = 40009;//热心战结束
	public static final int BATTLE_10 = 40010;//你好,
	public static final int BATTLE_11 = 40011;//你今天的热心战斗次数已达最大,
	public static final int BATTLE_12 = 40012;//不能再获得帮忙关系的额外经验加成了!
	public static final int BATTLE_13 = 40013;//你今天的热心战次数为CC
	public static final int BATTLE_14 = 40014;//获得积分
	public static final int BATTLE_15 = 40015;//你没有技能所需武器
	public static final int BATTLE_16 = 40016;//你的魔力值不够了!
	public static final int BATTLE_17 = 40017;//你的队伍在PK(切磋)中被[XX]的队伍打败了(英文:Your team has been defeated by [XX]'s team in the battle.)
	public static final int BATTLE_18 = 40018;//你的队伍在PK(切磋)中战胜了[XX]的队伍(英文:Your team has beat [XX]'s team in the battle.)
	public static String getPKString(boolean isWin,String name)
	{
		String str = "";
		if(isWin)
			str = DC.getString(BATTLE_18);
		else
			str = DC.getString(BATTLE_17);
		StringBuffer sb = new StringBuffer();
		sb.append("|");
		sb.append(name);
		sb.append("#u:");
		sb.append(name);
		sb.append("|");
		str = str.replace("[XX]", sb.toString());
		return str;
	}
	public static final int BATTLE_19 = 40019;//恭喜[XX]在夺宝奇兵活动中打败了[MM],获得珍稀物品[GG].(英文:Congratulations [XX] has beaten [MM] in treasure hunting and got [GG])
	public static String getMoneyBattleString(PlayerController player,String monsterName,Goods goods)
	{
		String str = DC.getString(BATTLE_19);
		StringBuffer sb = new StringBuffer();
		sb.append("|");
		sb.append(player.getName());
		sb.append("#u:");
		sb.append(player.getName());
		sb.append("|");
		str = str.replace("[XX]", sb.toString());
		sb = new StringBuffer();
		sb.append("|[");
		sb.append(monsterName);
		sb.append("]#c:0xFFFF00");
		sb.append("|");
		str = str.replace("[MM]", sb.toString());
		sb = new StringBuffer();
		sb.append("|[");
		sb.append(goods.name);
		sb.append("x");
		sb.append(goods.goodsCount);
		sb.append("]#p:");
		sb.append(goods.objectIndex);
		sb.append(":");
		sb.append(goods.quality);
		sb.append(":");
		sb.append(player.getID());
		str = str.replace("[GG]", sb.toString());
		return str;
	}
	public static final int BATTLE_24 = 40024;//获得铜币
	public static final int BATTLE_25 = 40025;//获得荣誉
	public static final int BATTLE_26 = 40026;//获得经验
	public static final int BATTLE_27 = 40027;//队长加成
	public static final int BATTLE_28 = 40028;//家族加成
	public static final int BATTLE_29 = 40029;//队伍加成
	public static final int BATTLE_30 = 40030;//帮忙加成
	public static final int BATTLE_31 = 40031;//结婚加成
	public static final int BATTLE_32 = 40032;//获得
	public static final int BATTLE_33 = 40033;//[XX]很满意的收下了赎身法宝[GG],并赠送了一本<<三好BOSS守则>>给[MM],让它回去好好研读,今后不得再出来作恶!
	//(英文:[XX]accepted the gift [GG] and gave [MM] a "BOSS Rules" in return for it to study and not do evil things any more.)
	public static String getBossGoodsString(String playerName,String goodsName,String monsterName)
	{
		String str = DC.getString(BATTLE_33);
		StringBuffer sb = new StringBuffer();
		sb.append(playerName);
		sb.append("#u:");
		sb.append(playerName);
		sb.append("#c:0xFFFF00");
		sb.append("|");
		str = str.replace("[XX]", sb.toString());
		sb = new StringBuffer();
		sb.append(goodsName);
		sb.append("|");
		str = str.replace("[GG]", sb.toString());
		sb = new StringBuffer();
		sb.append("|[");
		sb.append(monsterName);
		sb.append("]#c:0xFFFF00");
		sb.append("|");
		str = str.replace("[MM]", sb.toString());
		return str;
	}
	public static final int BATTLE_36 = 40036;//系统宝贝
	public static final int BATTLE_37 = 40037;//宝箱赠送
	public static final int BATTLE_38 = 40038;//恭喜你在与(英文:Congratulate you on beating)
	public static final int BATTLE_39 = 40039;//的搏斗中胜利,并且获得(英文:in the battle.You'll get)
	public static final int BATTLE_40 = 40040;//通过邮件系统发送给你!(英文:has been sent to you through mail system)
	public static final int BATTLE_41 = 40041;//号外!!号外!!在[XX]队伍的强力攻击和完美防御的威慑下,[MM]应声倒地(英文:Breaking news! Under the attack of [XX]'s team,[MM] lost the battle.)
	public static String getBossDieString(String playerName,String monsterName)
	{
		String str = DC.getString(BATTLE_41);
		StringBuffer sb = new StringBuffer();
		sb.append("|");
		sb.append(playerName);
		sb.append("#u:");
		sb.append(playerName);
		sb.append("|");
		str = str.replace("[XX]", sb.toString());
		sb = new StringBuffer();
		sb.append("|[");
		sb.append(monsterName);
		sb.append("]#p:");
		sb.append(0);
		sb.append(":");
		sb.append(1);
		sb.append(":");
		sb.append(0);
		sb.append("|");
		str = str.replace("[MM]", sb.toString());
		return str;
	}
	public static final int BATTLE_44 = 40044;//[FF]家族的[XX]带领队伍,打败了你的队伍!(英文:Leader [XX] of [FF] family has led a team and defeated yours.)
	public static final int BATTLE_45 = 40045;//你的队伍打败了[FF]家族的[XX]带领的队伍!(英文:Your team has beat leader [XX]'s team of [FF] family.)
	public static String getPartyPKString(boolean isWin,String fName,String pName)
	{
		String str = "";
		if(isWin)
			str = DC.getString(BATTLE_45);
		else
			str = DC.getString(BATTLE_44);
		str = str.replace("[XX]", pName);
		str = str.replace("[FF]", fName);
		return str;
	}
	public static final int BATTLE_50 = 40050;//恭喜[XX]的旅行团,幸运的遇到了沉睡的[MM],他们能否击倒BOSS,携宝而归呢?让我们拭目以待!
	//Congratulations [XX]'s team on coming across the sleeping [MM],Will they beat BOSS and get the award? Let''s wait and see.
	public static String getMeetBossStirng(String playerName,String monsterName)
	{
		String str = DC.getString(BATTLE_50);
		StringBuffer sb = new StringBuffer();
		sb.append("|");
		sb.append(playerName);
		sb.append("#u:");
		sb.append(playerName);
		sb.append("|");
		str = str.replace("[XX]", sb.toString());
		sb = new StringBuffer();
		sb.append("|[");
		sb.append(monsterName);
		sb.append("]#p:");
		sb.append(0);
		sb.append(":");
		sb.append(1);
		sb.append(":");
		sb.append(0);
		str = str.replace("[MM]", sb.toString());
		return str;
	}
	public static final int BATTLE_54 = 40054;//天啦![XX]的团队太强悍了,强大的[MM]被他们彻底征服了,他们获得了至高无上的荣誉和珍贵的财宝!
	//Oh my god![XX]'s team rocks,the powerful [MM] has been defeated by them. They'll get noble honor and precious treasures!
	public static String getKillBossString(String playerName,String monsterName)
	{
		String str = DC.getString(BATTLE_54);
		StringBuffer sb = new StringBuffer();
		sb.append("|");
		sb.append(playerName);
		sb.append("#u:");
		sb.append(playerName);
		sb.append("|");
		str = str.replace("[XX]", sb.toString());
		sb = new StringBuffer();
		sb.append("|[");
		sb.append(monsterName);
		sb.append("]#p:");
		sb.append(0);
		sb.append(":");
		sb.append(1);
		sb.append(":");
		sb.append(0);
		sb.append("|");
		str = str.replace("[MM]", sb.toString());
		return str;
	}
	/*********************Room***********************************/
	public static final int ROOM_1 = 60001;//的BOSS行动值不够!
	public static final int ROOM_2 = 60002;//的时空之旅行动值不够!
	public static final int ROOM_3 = 60003;//的元宝不足!
	public static final int ROOM_4 = 60004;//的行动值不够!
	public static final int ROOM_5 = 60005;//的挂机卡不够
	public static final int ROOM_6 = 60006;//你
	public static final int ROOM_7 = 60007;//没有
	public static final int ROOM_8 = 60008;//请去完成相关任务后领取!
	public static final int ROOM_9 = 60009;//XX的行动值不够,不能换房间
	/*********************Aam***********************************/
	public static final int AAM_1 = 70001;//帮忙获得荣誉(英文:Your assisting has gained you honor points)
	public static final int AAM_2 = 70002;//你和(英文:Your assisting relation with)
	public static final int AAM_3 = 70003;//的帮忙关系已成功解除!(英文: has been removed.)
	/*********************Party***********************************/
	public static final int PARTY_1 = 80001;//家族活动即将开始,大家可以通过城镇传送进准备房间哦!!!
	public static final int PARTY_2 = 80002;//家族活动开始,今天的'MVP'是哪个家族呢?大家加油吧!!!
	public static final int PARTY_3 = 80003;//家族活动结束,欢迎大家再次参加本活动!!!
	public static final int PARTY_4 = 80004;//家族活动还剩下
	public static final int PARTY_5 = 80005;//分钟(英文:min left.)
	public static final int PARTY_6 = 80006;//家族活动官
	public static final int PARTY_7 = 80007;//家族活动荣誉奖励
	public static final int PARTY_8 = 80008;//荣誉值奖励!
	public static final int PARTY_9 = 80009;//家族活动排名奖励
	public static final int PARTY_10 = 80010;//恭喜你所在家族取得家族活动的第一名(英文:Congratulate you on the first prize in family activity.)
	public static final int PARTY_11 = 80011;//你在家族中排名第CC(英文:You're ranking)
	public static final int PARTY_12 = 80012;//特此奖励!(英文:in this activity.This is your reward!)
	public static final int PARTY_13 = 80013;//离黄金斗士活动开始还有(英文:Party time)
	public static final int PARTY_14 = 80014;//分钟(英文:min before Gold Fighter activity begins.)
	public static final int PARTY_15 = 80015;//黄金斗士活动开始
	public static final int PARTY_16 = 80016;//黄金斗士活动结束
	public static final int PARTY_17 = 80017;//黄金斗士活动前
	public static final int PARTY_18 = 80018;//名分别是
	public static final int PARTY_19 = 80019;//分
	public static final int PARTY_20 = 80020;//恭喜他们!
	public static final int PARTY_21 = 80021;//获得黄金斗士活动奖励
	public static final int PARTY_22 = 80022;//黄金斗士
	public static final int PARTY_23 = 80023;//活动随机奖励
	public static final int PARTY_24 = 80024;//恭喜你获得活动随机奖励!
	public static final int PARTY_25 = 80025;//在竞技中展现了更快,更高,更强的斗士精神,感动了女神,获得GG
	public static final int PARTY_26 = 80026;//XX正在角落玩躲猫猫,却被一颗流弹击中,低头一看,竟然是GG
	public static final int PARTY_27 = 80027;//一(英文:No.1)
	public static final int PARTY_28 = 80028;//二
	public static final int PARTY_29 = 80029;//三
	public static final int PARTY_30 = 80030;//四
	public static final int PARTY_31 = 80031;//五
	public static final int PARTY_32 = 80032;//奇妙世界的第[NN]黄金斗士! (英文:Gold fighter [NN] in Magic World.)
	public static String getGoldPartyStr(String rank)
	{
		String str = DC.getString(PARTY_32);
		str = str.replace("NN", rank);
		return str;
	}
	public static final int PARTY_33 = 80033;//获得黄金斗士XX
	public static final int PARTY_34 = 80034;//的签名写真!
	public static final int PARTY_35 = 80035;//获得黄金斗士活动签名写真!
	public static final int PARTY_36 = 80036;//恭喜你在奇妙实验中获得经验奖励
	public static final int PARTY_37 = 80037;//奇妙小博士
	public static final int PARTY_38 = 80038;//实验成果
	public static final int PARTY_39 = 80039;//你成功兑换了XX,
	public static final int PARTY_40 = 80040;//获得经验CC
	public static final int PARTY_41 = 80041;//号外!号外!
	public static final int PARTY_42 = 80042;//FF家族在本次家族战中获取胜利,果然实力非凡!
	public static final int PARTY_43 = 80043;//问答排名奖励
	public static final int PARTY_44 = 80044;//恭喜你在奇妙问答活动中获得第(英文:You have won)
	public static final int PARTY_45 = 80045;//名,特此奖励,以兹鼓励(英文:in Q&A. This is your reward!)
	public static final int PARTY_46 = 80046;//问答完成奖励
	public static final int PARTY_47 = 80047;//恭喜你完成了第(英文:You have completed)
	public static final int PARTY_48 = 80048;//轮的奇妙问答活动,特此奖励,以兹鼓励!(英文:in Q&A. This is your reward!)
	public static final int PARTY_49 = 80049;//快速答题获得经验
	public static final int PARTY_50 = 80050;//快速答题获得积分
	public static final int PARTY_51 = 80051;//玩家进行剧情旅行中,不能进行相关操作
	public static final int PARTY_52 = 80052;//你在冒险中获得物品
	public static String getEventRewardString(PlayerController target,String goodsName,int count)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(DC.getString(DC.PARTY_52));
		sb.append(": [");
		sb.append(goodsName);
		sb.append("x");
		sb.append(count);
		sb.append("]");
		return sb.toString();
	}
	public static final int PARTY_53 = 80053;//你在冒险中获得铜币
	public static final int PARTY_54 = 80054;//你在冒险中获得经验
	public static final int PARTY_55 = 80055;//恭喜[XX]在奇妙大冲关活动中,过关斩将,勇往直前,获得了[GG]的奖励
	public static String getChongGuanString(PlayerController player,String string)
	{
		String str = DC.getString(PARTY_55);
		StringBuffer sb = new StringBuffer();
		sb.append("|");
		sb.append(player.getName());
		sb.append("#u:");
		sb.append(player.getName());
		sb.append("|"); 
		str = str.replace("[XX]", sb.toString());
		String[] strs = Utils.split(string, ":");
		sb = new StringBuffer();
		sb.append("|[");
		sb.append(strs[0]);
		sb.append("]#p:");
		sb.append(strs[1]);
		sb.append(":");
		sb.append(strs[2]);
		sb.append(":");
		sb.append(player.getID());
		sb.append("|");
		str = str.replace("[GG]", sb.toString());
		return str;
	}
	/*********************PlayerController***********************************/
	public static final int PLAYER_1 = 90001;//请先关闭魔方!
	public static final int PLAYER_2 = 90002;//	开宝箱时不能做其它操作哦!
	public static final int PLAYER_3 = 90003;//	请先结束奇妙问答活动!
	public static final int PLAYER_4 = 90004;//	请先选择宠物变身!
	public static final int PLAYER_5 = 90005;//	请先选择你要加入的阵营!
	public static final int PLAYER_6 = 90006;//	请先选择你要转的职业!
	public static final int PLAYER_7 = 90007;//	请先关闭仓库!
	public static final int PLAYER_8 = 90008;//	对不起
	public static final int PLAYER_9 = 90009;//	XX正在进行转职选择!
	public static final int PLAYER_10 = 90010;//XX正在进行阵营选择!
	public static final int PLAYER_11 = 90011;//XX正在进行宠物变身选择!
	public static final int PLAYER_12 = 90012;//XX正在参加魔方活动!
	public static final int PLAYER_13 = 90013;//XX正在开启宝箱!
	public static final int PLAYER_14 = 90014;//XX正在参加奇妙问答活动!
	public static final int PLAYER_15 = 90015;//XX正在进行仓库相关操作!
	public static final int PLAYER_16 = 90016;//XX拒绝了你的组队邀请!
	public static final int PLAYER_17 = 90017;//家族
	public static final int PLAYER_18 = 90018;//族长变更信息
	public static final int PLAYER_19 = 90019;//您好:您所在的家族,当家的由[XX1]变成了[XX2],族长变更的两个相关相关玩家需要重新登录,每十二个小时可以变更一次族长!
	//Hi:The leader of your family has changed from [XX1] to [XX2],The 2 relevant players need to relog.The leader can be changed every 12 hours.
	public static String getFamilyLeaderString(String name1,String name2)
	{
		String str = DC.getString(PLAYER_19);
		str = str.replace("XX1", name1);
		str = str.replace("XX2", name2);
		return str;
	}
	public static final int PLAYER_24 = 90024;//家族解散信息
	public static final int PLAYER_25 = 90025;//您好:很不愿意告诉您这个不幸的消息,您所在的家族,已经被[XX]解散了.(英文:We're sorry to tell you the bad news that family has been dismissed by [XX].)	
	public static final int PLAYER_28 = 90028;//对不起，你被管理员禁言了!
	public static final int PLAYER_29 = 90029;//系统
	public static final int PLAYER_30 = 90030;//请先选择宠物变身
	public static final int PLAYER_31 = 90031;//恭喜你的宠物升级了
	public static final int PLAYER_32 = 90032;//获得
	public static final int PLAYER_33 = 90033;//没找到玩家!!!
	public static final int PLAYER_34 = 90034;//玩家没在线!!!
	public static final int PLAYER_35 = 90035;//正在删除中!!!
	public static final int PLAYER_36 = 90036;//你的宠物亲密度已达最大值!
	public static final int PLAYER_37 = 90037;//喂养宠物
	public static final int PLAYER_38 = 90038;//宠物获得
	public static final int PLAYER_39 = 90039;//点亲密度
	public static final int PLAYER_40 = 90040;//你的宠物等级已达最大值!
	public static final int PLAYER_41 = 90041;//点经验值
	public static final int PLAYER_42 = 90042;//申请组队成功
	public static final int PLAYER_43 = 90043;//的挂机卡不够
	public static final int PLAYER_44 = 90044;//XX请提取宝箱的物品!
	public static final int PLAYER_45 = 90045;//XX正在开宝箱!
	public static final int PLAYER_46 = 90046;//请选择要加入的阵营!
	public static final int PLAYER_47 = 90047;//XX正在选择阵营!
	public static final int PLAYER_48 = 90048;//请选择宠物进化!
	public static final int PLAYER_49 = 90049;//XX正在选择宠物变身!
	public static final int PLAYER_50 = 90050;//你的年龄未满18!
	public static final int PLAYER_51 = 90051;//身份证验证官
	public static final int PLAYER_52 = 90052;//身份验证成功礼品
	public static final int PLAYER_53 = 90053;//你已成功通过身份验证,特此发放奖励给你!
	public static final int PLAYER_54 = 90054;//XX拒绝了你的帮助邀请!
	public static final int PLAYER_55 = 90055;//XX已经成为你的被帮助人!
	public static final int PLAYER_56 = 90056;//XX已经成为你的热心人!
	public static final int PLAYER_57 = 90057;//奇妙离婚登记处
	public static final int PLAYER_58 = 90058;//离婚信息
	public static final int PLAYER_59 = 90059;//奇妙月老
	public static final int PLAYER_60 = 90060;//求婚戒指返还
	public static final int PLAYER_61 = 90061;//万能管家
	public static final int PLAYER_62 = 90062;//宠物转换
	public static final int PLAYER_63 = 90063;//宠物已恢复到初始状态!
	public static final int PLAYER_64 = 90064;//很遗憾,炼金术士意外走神了,你什么也没获得
	public static final int PLAYER_65 = 90065;//由于炼金术士今天RP意外爆发了,炼金成功获得GG
	public static final int PLAYER_66 = 90066;//好消息,炼金术士今天RP爆发了,你本次炼金成功,获得GG
	public static final int PLAYER_67 = 90067;//你打开了一个精致的礼品盒
	public static final int PLAYER_68 = 90068;//新手指导
	public static final int PLAYER_69 = 90069;//新手泡泡奖励
	public static final int PLAYER_70 = 90070;//恭喜您获得在线经验
	public static final int PLAYER_71 = 90071;//邮件将满,未免造成损失请及时清理!!
	public static final int PLAYER_72 = 90072;//恭喜[XX]在参加[NN1]元宝一次的[奇妙魔方]中得到[GG],价值[NN2]元宝.(英文:Congratulate [XX] for gaining [NN2] ingots worth [GG] in "Magic Cube"([NN1]/time))
	public static final int PLAYER_73 = 90073;//恭喜[XX]在参加[NN1]元宝一次的[奇妙魔方]中得到[GG].(英文:Congratulate [XX] for gaining [GG] in "Magic Cube"([NN1]/time))
	public static String getMoneyBoxString(PlayerController player,Goods goods,String objectIndex)
	{
		String str = "";
		if(goods.token >= 500)
			str = DC.getString(PLAYER_72);
		else
			str = DC.getString(PLAYER_73);
		StringBuffer sb = new StringBuffer();
		sb.append("|");
		sb.append(player.getName());
		sb.append("#u:");
		sb.append(player.getName());
		sb.append("|"); 
		str = str.replace("[XX]", sb.toString());
		str = str.replace("[NN1]", PlayerController.MONEYBOX+"");
		sb = new StringBuffer();
		sb.append("|[");
		sb.append(goods.name);
		sb.append("]#p:");
		sb.append(objectIndex);
		sb.append(":");
		sb.append(goods.quality);
		sb.append(":");
		sb.append(player.getID());
		if(goods.token >= 500)
		{
			sb.append("|");
			str = str.replace("[GG]", sb.toString());
			str = str.replace("[NN2]", goods.token+"");
		}
		else
		{
			str = str.replace("[GG]", sb.toString());
		}
		return str;
	}

	/*********************GOODS***********************************/
	public static final int GOODS_1 = 100001;//对不起,超过最大存钱限制!
	public static final int GOODS_2 = 100002;//丢弃
	public static final int GOODS_3 = 100003;//打造
	public static final int GOODS_4 = 100004;//消耗
	public static final int GOODS_5 = 100005;//女神试练宝箱
	public static final int GOODS_6 = 100006;//恭喜
	public static final int GOODS_7 = 100007;//开启
	public static final int GOODS_8 = 100008;//获得
	public static final int GOODS_9 = 100009;//你开启了女神试练宝箱,获得经验
	public static final int GOODS_10 = 100010;//炼化
	public static final int GOODS_11 = 100011;//请先关闭交易!
	public static final int GOODS_12 = 100012;//进化
	public static final int GOODS_13 = 100013;//出售
	public static final int GOODS_14 = 100014;//得到
	public static final int GOODS_15 = 100015;//代金券
	public static final int GOODS_16 = 100016;//因为
	public static final int GOODS_17 = 100017;//你
	public static final int GOODS_18 = 100018;//提出申请,你和
	public static final int GOODS_19 = 100019;//XX的婚姻关系结束了,希望你们以后能找到真正属于自己的幸福!
	public static final int GOODS_20 = 100020;//奇妙月老
	public static final int GOODS_21 = 100021;//结婚礼物
	public static final int GOODS_22 = 100022;//XX的老公
	public static final int GOODS_23 = 100023;//XX的老婆
	public static final int GOODS_24 = 100024;//恭喜[XX]砸开[GG1]，获得了[GG2]
	public static String getGoldEggString(PlayerController player,String eggs,Goods goods2)
	{
		String str = DC.getString(GOODS_24);
		StringBuffer sb = new StringBuffer();
		sb.append("|");
		sb.append(player.getName());
		sb.append("#u:");
		sb.append(player.getName());
		sb.append("|"); 
		str = str.replace("[XX]", sb.toString());
		str = str.replace("GG1", eggs);
		sb = new StringBuffer();
		sb.append("|[");
		sb.append(goods2.name);
		sb.append("x");
		sb.append(goods2.goodsCount);
		sb.append("]#p:");
		sb.append(goods2.objectIndex);
		sb.append(":");
		sb.append(goods2.quality);
		sb.append(":");
		sb.append(player.getID());
		str = str.replace("[GG2]", sb.toString());
		return str;
	}
	/*********************Pet***********************************/
	public static final int PET_1 = 110001;//嬉戏
	public static final int PET_2 = 110002;//锻炼
	public static final int PET_3 = 110003;//探险
	public static final int PET_4 = 110004;//恭喜你的宠物在刚才的[TT1]中获得[NN][TT2].(英文:Congratulate your pet has gained [NN] [TT2] in [TT1].)
	public static final int PET_5 = 110005;//你成功领养了守护[PP]
	public static final int PET_6 = 110006;//点亲密度!(英文:intimacy)
	public static final int PET_7 = 110007;//点经验值!(英文:experience)
	public static String getPetTrainString(String type1,String type2,String number)
	{
		String str = DC.getString(PET_4);
		str = str.replace("[TT1]", type1);
		str = str.replace("[TT2]", type2);  
		str = str.replace("[NN]", number);
		return str;
	}
	public static final int PET_8 = 110008;//宠物信息(英文:Pet Information)
	public static final int PET_9 = 110009;//你的守护获得经验
	public static final int PET_10 = 110010;//你的守护升级了
	public static final int PET_11 = 110011;//恭喜[XX]把[GG]精炼到了+[NN]
	public static String getPetEquipUpString(PlayerController player,GoodsPetEquip goods)
	{
		String str = DC.getString(PET_11);
		StringBuffer sb = new StringBuffer();
		sb.append("|");
		sb.append(player.getName());
		sb.append("#u:");
		sb.append(player.getName());
		sb.append("|"); 
		str = str.replace("[XX]", sb.toString());
		sb = new StringBuffer();
		sb.append("|[");
		sb.append(goods.name);
		sb.append("]#p:");
		sb.append(goods.objectIndex);
		sb.append(":");
		sb.append(goods.quality);
		sb.append(":");
		sb.append(player.getID());
		sb.append("|"); 
		str = str.replace("[GG]", sb.toString());
		str = str.replace("[NN]", goods.growPoint+"");
		return str;
	}
	public static final int PET_12 = 110012;//精炼守护装备[GG],装备精炼值从[NN1]变为[NN2].
	public static String getPetEquipUpInfoString(String name,int a,int b)
	{
		String str = DC.getString(PET_12);
		str = str.replace("[GG]", name);
		str = str.replace("[NN1]", a+"");
		str = str.replace("[NN2]", b+"");
		return str;
	}
	public static final int PET_13 = 110013;//你的守护[PP]修炼技能[SS],获得了[NN]点经验
	public static final int PET_14 = 110014;//你的守护[PP]修炼技能[SS],获得了[NN1]点经验,技能提升至[NN2]级.
	public static String getPetSkillStudyString(String petName,String skillName,long exp,int level,boolean isLevel)
	{
		String str = "";
		if(isLevel)	
			str = DC.getString(PET_14);
		else
			str = DC.getString(PET_13);
		str = str.replace("PP", petName);
		str = str.replace("SS", skillName);
		if(isLevel)
		{
			str = str.replace("[NN1]", exp+"");
			str = str.replace("[NN2]", level+"");
		}
		else
			str = str.replace("[NN]", exp+"");
		return str;
	}
	public static final int PET_15 = 110015;//在战斗中不能喂养宠物
	/*********************System***********************************/
	public static final int SYSTEM_1 = 120001;//有相同角色在游戏中!!!
	public static final int SYSTEM_2 = 120002;//为了地球的和平,请不要使用火星文:(
	public static final int SYSTEM_3 = 120003;//请清理缓存或升级Flash版本再试
	public static final int SYSTEM_4 = 120004;//游戏管理员
	public static final int SYSTEM_5 = 120005;//系统邮件
	
	public static String getString(int key)
	{
		String str = DataFactory.getInstance().getFont(key);
		if(str == null || str.equals(""))
			return "[key-"+key+"]";
		return str;
	}
}
