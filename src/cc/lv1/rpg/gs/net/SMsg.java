package cc.lv1.rpg.gs.net;

import vin.rabbit.net.AppMessage;
import vin.rabbit.util.ByteBuffer;

public class SMsg extends AppMessage
{
	 /** 
	 * 登陆类型 
	 */
	 public static final int T_LOGIN_COMMAND = 0x00010000;  
	 /** 登陆 */
	 public static  int C_LOGIN_COMMAND = 0x00010001;       
	 public static  int S_LOGIN_COMMAND = 0x00010001; 
	 
	 /** 创建角色 */
	 public static  int C_CREATE_ROLE_COMMAND = 0x00010002;       
	 public static  int S_CREATE_ROLE_COMMAND = 0x00010002;
    
	 /** 删除角色 */
	 public static  int C_DELETE_ROLE_COMMAND = 0x00010003;
	 public static  int S_DELETE_ROLE_COMMAND = 0x00010003;
	 
	 /** 检查重名 */
	 public static  int C_ROLENAME_REPEAT_COMMAND = 0x00010004;
	 public static  int S_ROLENAME_REPEAT_COMMAND = 0x00010004;
	 
	 /** 进入游戏 */
	 public static  int C_PLAYER_ENTER_GAME_COMMAND = 0x00010005;
	 public static  int S_PLAYER_ENTER_GAME_COMMAND = 0x00010005;
	 
	 /** heart ping */
	 public static  int C_PLAYER_HEART_PING_COMMAND = 0x00010006;
	 public static  int S_PLAYER_HEART_PING_COMMAND = 0x00010006;

	 /** 临时注册 */
	 public static  int C_PLAYER_REG_COMMAND = 0x00010007;
	 public static  int S_PLAYER_REG_COMMAND = 0x00010007;
	 
	 /** 修改密码 */
	 public static  int C_PLAYER_CHANGEPWD_COMMAND = 0x00010008;
	 public static  int S_PLAYER_CHANGEPWD_COMMAND = 0x00010008;
	 
	 /** 增加邮件 */
	 public static  int C_PLAYER_INPUTMAIL_COMMAND = 0x00010009;
	 public static  int S_PLAYER_INPUTMAIL_COMMAND = 0x00010009;
	 
	 /** 正式的登录 */
	 public static  int C_PLAYER_LOGIN_COMMAND = 0x00010010;
	 public static  int S_PLAYER_LOGIN_COMMAND = 0x00010010;
	 
	 /** 合区 */
	 public static  int C_PLAYER_MERGE_COMMAND = 0x00010011;
	 public static  int S_PLAYER_MERGE_COMMAND = 0x00010011;
	 
	 /** 重新选择 */
	 public static  int C_PLAYER_RESELECTMERGE_COMMAND = 0x00010012;
	 public static  int S_PLAYER_RESELECTMERGE_COMMAND = 0x00010012;
	 
	 /** 安全退出 */
	 public static  int C_PLAYER_SAFE_ESC_COMMAND = 0x00010013;
	 public static  int S_PLAYER_SAFE_ESC_COMMAND = 0x00010013;
	 
	 /**
	  * 房间消息
	  */
	 public static final int T_ROOM_COMMAND = 0x00020000;
	 /** 返回房间信息 */
	 public static int C_ROOM_INFO_COMMAND = 0x00020001;
	 public static int S_ROOM_INFO_COMMAND = 0x00020001;
	 
	 /** 玩家进出房间通知房间内的其他人 */
	 public static  int C_ROOM_IO_COMMAND = 0x00020002;
	 public static  int S_ROOM_IO_COMMAND = 0x00020002;
	 
	 /** 玩家进入房间 */
	 public static  int C_ROOM_ADD_COMMAND = 0x00020003;
	 public static  int S_ROOM_ADD_COMMAND = 0x00020003;
	 
	 /** 玩家离开房间 */
	 public static  int C_ROOM_REMOVE_COMMAND = 0x00020004;
	 public static  int S_ROOM_REMOVE_COMMAND = 0x00020004;
	 
	 /** 玩家请求出口并返回新房间 */
	 public static  int C_ROOM_EXIT_COMMAND = 0x00020005;
	 public static  int S_ROOM_EXIT_COMMAND = 0x00020005;
	 
	 /** Player Hit Enemy 玩家在房间请求打怪 */
	 public static  int C_ROOM_PVE_COMMAND = 0x00020006;
	 public static  int S_ROOM_PVE_COMMAND = 0x00020006;
	 
	 /** Player Hit Enemy 玩家在房间请求PK(不需要对方同意的) */
	 public static  int C_ROOM_PVP_COMMAND = 0x00020007;
	 public static  int S_ROOM_PVP_COMMAND = 0x00020007;
	 
	 /** 玩家是否在战斗 进入退出时转发给非战斗本房间非战斗玩家 */
	 public static  int C_ROOM_PLAYER_ISBATTLE = 0x00020008;
	 public static  int S_ROOM_PLAYER_ISBATTLE = 0x00020008;
	 
	 /** 玩家升级 通知当前房间内的所有玩家 */
	 public static  int C_ROOM_PLAYER_LEVELUP = 0x00020009;
	 public static  int S_ROOM_PLAYER_LEVELUP = 0x00020009;
	 
	 /** 房间是否隐藏怪物组 */
	 public static  int C_ROOM_VISIBLE_MG = 0x00020010;
	 public static  int S_ROOM_VISIBLE_MG = 0x00020010;
	 
	 /** 更新战斗内玩家改变的状况
	  * 当战斗结束后发送请求获取当前玩家的变量信息
	  *  */
	 public static  int C_ROOM_PLAYER_UPDATE_PLAYER = 0x00020011;
	 public static  int S_ROOM_PLAYER_UPDATE_PLAYER = 0x00020011;
	 
	 /**
	  * 请求当前房间可接任务的npc
	  */
	 public static  int C_ROOM_GET_ROOM_TASKS = 0x00020012;
	 public static  int S_ROOM_GET_ROOM_TASKS = 0x00020012;
	 
	 /**
	  * 客户端初始完成的消息
	  */
	 public static  int C_ROOM_INITED = 0x00020013;
	 public static  int S_ROOM_INITED = 0x00020013;
	 
	 /**
	  * 请求房间里所有队伍列表
	  */
	 public static  int C_ROOM_GET_TEAMS = 0x00020014;
	 public static  int S_ROOM_GET_TEAMS = 0x00020014;
	 
	 /**
	  * 查看房间中的某一个队伍的详细信息
	  */
	 public static  int C_ROOM_TEAM_MEMBER_INFO = 0x00020015;
	 public static  int S_ROOM_TEAM_MEMBER_INFO = 0x00020015;
	 
	 /** 发送BOSS剩余刷新时间 */
	 public static  int C_ROOM_MONSTER_FLUSH_TIME_COMMAND = 0x00020016;
	 public static  int S_ROOM_MONSTER_FLUSH_TIME_COMMAND = 0x00020016;
	 
	 /** 通知客户端放烟花 */
	 public static  int C_ROOM_MARRY_FLASH_COMMAND = 0x00020017;
	 public static  int S_ROOM_MARRY_FLASH_COMMAND = 0x00020017;
	 
	 /** 通知客户端要显示的玩家ID(针对家族战和黄金斗士活动) */
	 public static  int C_ROOM_DISPLAY_PLAYER_COMMAND = 0x00020018;
	 public static  int S_ROOM_DISPLAY_PLAYER_COMMAND = 0x00020018;
	 
	 
	 
	 /** 
	  * 玩家类型
	  */
	 public static final int T_PLAYER_COMMAND = 0x00030000;  
	 /** 返回玩家信息 */
	 public static  int C_PLAYER_INFO_COMMAND = 0x00030001;
	 public static  int S_PLAYER_INFO_COMMAND = 0x00030001;
	 
	 /** 玩家移动信息(键盘) */
	 public static  int C_PLAYER_ONKEYMOVE_COMMAND = 0x00030003;
	 public static  int S_PLAYER_ONKEYMOVE_COMMAND = 0x00030003;
	 
	 /** 玩家移动信息(鼠标) */
	 public static  int C_PLAYER_ONMOUSEMOVE_COMMAND = 0x00030004;
	 public static  int S_PLAYER_ONMOUSEMOVE_COMMAND = 0x00030004;
	 
	 /** 查看玩家装备面板 */
	 public static  int C_GET_PLAYER_EQUIPSET_COMMAND = 0x00030005;
	 public static  int S_GET_PLAYER_EQUIPSET_COMMAND = 0x00030005;

	 /** 取得tasks */
	 public static  int C_GET_TASKS_COMMAND = 0x00030006;
	 public static  int S_GET_TASKS_COMMAND = 0x00030006;
	 
	 /** 取消玩家的某个任务 */
	 public static  int C_CANCEL_TASKS_COMMAND = 0x00030007;
	 public static  int S_CANCEL_TASKS_COMMAND = 0x00030007;
	 
	 /** 玩家请求组队 */
	 public static  int C_PLAYER_REQUEST_TEAM_COMMAND = 0x00030008;
	 public static  int S_PLAYER_REQUEST_TEAM_COMMAND = 0x00030008;
	 
	 /** 加入队伍 */
	 public static  int C_ADD_TEAM_COMMAND = 0x00030009;
	 public static  int S_ADD_TEAM_COMMAND = 0x00030009;
	 
	 /** 聊天 */
	 public static  int C_CHAT_COMMAND = 0x00030010;
	 public static  int S_CHAT_COMMAND = 0x00030010;
	 
	 /** 取得技能 消息*/
	 public static  int C_GET_SKILL_COMMAND = 0x00030011;
	 public static  int S_GET_SKILL_COMMAND = 0x00030011;
	 
	 /** 玩家请求PK */
	 public static  int C_PLAYER_REQUEST_PK_COMMAND = 0x00030012;
	 public static  int S_PLAYER_REQUEST_PK_COMMAND = 0x00030012;
	 
	 /** 玩家回复PK邀请 */
	 public static  int C_PLAYER_RESPONSE_PK_COMMAND = 0x00030013;
	 public static  int S_PLAYER_RESPONSE_PK_COMMAND = 0x00030013;
	 
	 /** 玩家好友列表 */
	 public static  int C_PLAYER_FRIENDLIST_COMMAND = 0x00030014;
	 public static  int S_PLAYER_FRIENDLIST_COMMAND = 0x00030014;	 
	 
	 /** 玩家家族邀请 request */
	 public static  int C_PLAYER_FAMILYREQUEST_COMMAND = 0x00030015;
	 public static  int S_PLAYER_FAMILYREQUEST_COMMAND = 0x00030015;	 
	 
	 /** 玩家家族邀请 response */
	 public static  int C_PLAYER_FAMILYRESPONSE_COMMAND = 0x00030016;
	 public static  int S_PLAYER_FAMILYRESPONSE_COMMAND = 0x00030016;	
	 
	 /** 家族创建命令 */
	 public static  int C_PLAYER_FAMILYCREATE_COMMAND = 0x00030017;
	 public static  int S_PLAYER_FAMILYCREATE_COMMAND = 0x00030017;	
	 
	 /** 请求家族列表 */
	 public static  int C_PLAYER_FAMILYLIST_COMMAND = 0x00030018;
	 public static  int S_PLAYER_FAMILYLIST_COMMAND = 0x00030018;
	 
	 /** 族长T人 */
	 public static  int C_PLAYER_FAMILYKICK_COMMAND = 0x00030019;
	 public static  int S_PLAYER_FAMILYKICK_COMMAND = 0x00030019;
	 
	 /** 解散家族 */
	 public static  int C_PLAYER_FAMILYREMOVE_COMMAND = 0x00030020;
	 public static  int S_PLAYER_FAMILYREMOVE_COMMAND = 0x00030020;	
	 
	 /** 退出家族 */
	 public static  int C_PLAYER_FAMILYOUT_COMMAND = 0x00030021;
	 public static  int S_PLAYER_FAMILYOUT_COMMAND = 0x00030021;
	 
	 /** 族长转让 */
	 public static  int C_PLAYER_FAMILYLEADERCHANGE_COMMAND = 0x00030023;
	 public static  int S_PLAYER_FAMILYLEADERCHANGE_COMMAND = 0x00030023;
	 
	 /** 交易邀请 */
	 public static  int C_PLAYER_BUSINESS_REQUEST_COMMAND = 0x00030024;
	 public static  int S_PLAYER_BUSINESS_REQUEST_COMMAND = 0x00030024;
	 
	 /** 交易回复 */
	 public static  int C_PLAYER_BUSINESS_RESPONSE_COMMAND = 0x00030025;
	 public static  int S_PLAYER_BUSINESS_RESPONSE_COMMAND = 0x00030025;
	 
	 /** 查看宠物信息 */
	 public static  int C_PLAYER_PET_INFO_COMMAND = 0x00030026;
	 public static  int S_PLAYER_PET_INFO_COMMAND = 0x00030026;
	 
	 /** 阵营选择 */
	 public static  int C_PLAYER_CAMP_SET_COMMAND = 0x00030027;
	 public static  int S_PLAYER_CAMP_SET_COMMAND = 0x00030027;
	 
	 /** 转职 */
	 public static  int C_PLAYER_UP_PROFESSION_COMMAND = 0x00030028;
	 public static  int S_PLAYER_UP_PROFESSION_COMMAND = 0x00030028;
	 
	 /** 更新自己的属性 */
	 public static  int C_PLAYER_UPDATE_COMMAND = 0x00030029;
	 public static  int S_PLAYER_UPDATE_COMMAND = 0x00030029;
	  
	 /** 自动技能编排 */
	 public static  int C_AUTO_SKILLTOME_COMMAND = 0x00030030;
	 public static  int S_AUTO_SKILLTOME_COMMAND = 0x00030030;
	 
	 /** 更新逃跑 */
	 public static  int C_CLEAR_ESC_COMMAND = 0x00030031;
	 public static  int S_CLEAR_ESC_COMMAND = 0x00030031;
	 
	 /** 宠物信息操作 */
	 public static  int C_PLAYER_PETINFO_OPTION_COMMAND = 0x00030032;
	 public static  int S_PLAYER_PETINFO_OPTION_COMMAND = 0x00030032;
	 
	 /** 经验卡BUFF */
	 public static  int C_EXP_BUFF_COMMAND = 0x00030033;
	 public static  int S_EXP_BUFF_COMMAND = 0x00030033;
	 
	 /** 元宝开宝箱 */
	 public static  int C_MONEY_BOX_COMMMAND = 0x00030034;
	 public static  int S_MONEY_BOX_COMMMAND = 0x00030034;
	  
	 /** npc处 请求家族列表 */
	 public static  int C_PLAYER_FAMILYLISTS_COMMAND = 0x00030043;
	 public static  int S_PLAYER_FAMILYLISTS_COMMAND = 0x00030043;
	 
	 /** 任命副会长 */
	 public static  int C_PLAYER_ADD_DEPUTYLEADER_COMMAND = 0x00030044;
	 public static  int S_PLAYER_ADD_DEPUTYLEADER_COMMAND = 0x00030044;
	 
	 /** Mail */
	 public static  int C_PLAYER_MAIL_COMMAND = 0x00030046;
	 public static  int S_PLAYER_MAIL_COMMAND = 0x00030046;
	 
	 /** 通知玩家获得的具体经验 */
	 public static  int C_PLAYER_GETEXP_COMMAND = 0x00030047;
	 public static  int S_PLAYER_GETEXP_COMMAND = 0x00030047;
	 
	 /** 喂养宠物 */
	 public static  int C_PLAYER_FEED_PET_COMMAND = 0x00030048;
	 public static  int S_PLAYER_FEED_PET_COMMAND = 0x00030048;
	 
	 /** 玩家升级通知增加的属性 */
	 public static  int C_PLAYER_UP_ATT_COMMAND = 0x00030049;
	 public static  int S_PLAYER_UP_ATT_COMMAND = 0x00030049;
	 
	 /** 更新玩家角色一些信息(形象，称号) */
	 public static  int C_PLAYER_UPATE_ROLEINFO_COMMAND = 0x00030050;
	 public static  int S_PLAYER_UPATE_ROLEINFO_COMMAND = 0x00030050;
	 
	 /** 通知房间所有人自己遛宠 */
	 public static  int C_PLAYER_PET_STROLL_COMMAND = 0x00030051;
	 public static  int S_PLAYER_PET_STROLL_COMMAND = 0x00030051;
	 
	 /** 通知宠物剩余时间 */
	 public static  int C_PLAYER_PET_DOWMTIME_COMMAND = 0x00030052;
	 public static  int S_PLAYER_PET_DOWMTIME_COMMAND = 0x00030052;
	 
	 /** GM命令 */
	 public static  int C_GM_POST_COMMAND = 0x00030058;
	 public static  int S_GM_POST_COMMAND = 0x00030058;

	 /** 显示本房间的出入口，提示玩家可以向下一个房间走了 */
	 public static  int C_PLAYER_VISIABLE_COMMAND = 0x00030059;
	 public static  int S_PLAYER_VISIABLE_COMMAND = 0x00030059;
	 
	 /** 更新行动值 */
	 public static  int C_PLAYER_UPDATEACTIVEPOINT_COMMAND = 0x00030061;
	 public static  int S_PLAYER_UPDATEACTIVEPOINT_COMMAND = 0x00030061;
	 
	 /** 更新当前房间的事件值 */
	 public static  int C_PLAYER_UPDATEEVENTPOINT_COMMAND = 0x00030062;
	 public static  int S_PLAYER_UPDATEEVENTPOINT_COMMAND = 0x00030062;
	 
	
	 /** 聊天动画 */
	 public static  int C_PLAYER_CHAT_CARTOON_COMMAND = 0x00030063;
	 public static  int S_PLAYER_CHAT_CARTOON_COMMAND = 0x00030063;
	 
	 /** 新手指导 */
	 public static  int C_PLAYER_NEW_COMMAND = 0x00030064;
	 public static  int S_PLAYER_NEW_COMMAND = 0x00030064;
	 
	 /** 取得排行榜 */
	 public static  int C_PLAYER_GET_TOP_COMMAND = 0x00030065;
	 public static  int S_PLAYER_GET_TOP_COMMAND = 0x00030065;
	 
	 /** 修改阵营 */
	 public static  int C_PLAYER_MODIFY_CAMP_COMMAND = 0x00030066;
	 public static  int S_PLAYER_MODIFY_CAMP_COMMAND = 0x00030066;
	 
	 /** 修改角色名字 */
	 public static  int C_PLAYER_MODIFY_NAME_COMMAND = 0x00030067;
	 public static  int S_PLAYER_MODIFY_NAME_COMMAND = 0x00030067;
	 
	 /** 设置组队状态 */
	 public static  int C_PLAYER_SET_TEAM_STATE_COMMAND = 0x00030068;
	 public static  int S_PLAYER_SET_TEAM_STATE_COMMAND = 0x00030068;
	 
	 /** 申请组队*/
	 public static  int C_PLAYER_APPLY_TEAM_COMMAND = 0x00030069;
	 public static  int S_PLAYER_APPLY_TEAM_COMMAND = 0x00030069;
	 
	 /** 领取奖励信息 */
	 public static  int C_PLAYER_REWARDINFO_COMMAND = 0x00030070;
	 public static  int S_PLAYER_REWARDINFO_COMMAND = 0x00030070;

	 /** 挂机打怪相关*/
	 public static  int C_PLAYER_AUTO_BATTLE_COMMAND = 0x00030071;
	 public static  int S_PLAYER_AUTO_BATTLE_COMMAND = 0x00030071;
	 
	 /** 刷新自动打怪挂机相关信息*/
	 public static  int C_PLAYER_AUTO_BATTLE_INFO_COMMAND = 0x00030072;
	 public static  int S_PLAYER_AUTO_BATTLE_INFO_COMMAND = 0x00030072;
	 
	 /** 验证身份证*/
	 public static  int C_PLAYER_VALIDATE_IDCARD_COMMAND = 0x00030073;
	 public static  int S_PLAYER_VALIDATE_IDCARD_COMMAND = 0x00030073;
	 
	 /** 师徒邀请*/
	 public static  int C_PLAYER_MASTER_INVI_APP_COMMAND = 0x00030074;
	 public static  int S_PLAYER_MASTER_INVI_APP_COMMAND = 0x00030074;
	 
	 /** 回复师徒邀请*/
	 public static  int C_PLAYER_APP_RESPONSE_MASTER_COMMAND = 0x00030075;
	 public static  int S_PLAYER_APP_RESPONSE_MASTER_COMMAND = 0x00030075;
	 
	 /** 师徒关系操作*/
	 public static  int C_PLAYER_OPTION_MAS_APP_COMMAND = 0x00030076;
	 public static  int S_PLAYER_OPTION_MAS_APP_COMMAND = 0x00030076;
	 
	 /** 结婚邀请*/
	 public static  int C_PLAYER_REQUEST_MARRY_COMMAND = 0x00030077;
	 public static  int S_PLAYER_REQUEST_MARRY_COMMAND = 0x00030077;
	 
	 /** 结婚回复*/
	 public static  int C_PLAYER_RESPONSE_MARRY_COMMAND = 0x00030078;
	 public static  int S_PLAYER_RESPONSE_MARRY_COMMAND = 0x00030078;
	 
	 /** 离婚请求 */
	 public static  int C_PLAYER_CANCEL_MARRY_COMMAND = 0x00030079;
	 public static  int S_PLAYER_CANCEL_MARRY_COMMAND = 0x00030079;
	 
	 /** 修改性别 */
	 public static  int C_PLAYER_CHANGE_SEX_COMMAND = 0x00030080;
	 public static  int S_PLAYER_CHANGE_SEX_COMMAND = 0x00030080;
	 
	 /** 通知客户端玩家身上或者仓库里即将过期的物品 */
	 public static  int C_PLAYER_EXPIRED_GOODS_COMMAND = 0x00030081;
	 public static  int S_PLAYER_EXPIRED_GOODS_COMMAND = 0x00030081;
	 
	 /** 宠物转换 */
	 public static  int C_PLAYER_CHANGE_PET_COMMAND = 0x00030082;
	 public static  int S_PLAYER_CHANGE_PET_COMMAND = 0x00030082;
	 
	 /** 转生 */
	 public static  int C_PLAYER_UP_ROLE_COMMAND = 0x00030083;
	 public static  int S_PLAYER_UP_ROLE_COMMAND = 0x00030083;
	 
	 /** 新手泡泡 */
	 public static  int C_PLAYER_GUIDE_STEP_COMMAND = 0x00030084;
	 public static  int S_PLAYER_GUIDE_STEP_COMMAND = 0x00030084;
	 
	 /** 炼金术 */
	 public static  int C_PLAYER_GOLD_BOX_COMMAND = 0x00030085;
	 public static  int S_PLAYER_GOLD_BOX_COMMAND = 0x00030085;
	 
	 /** 根据房间类型发送相对的行动值 */
	 public static int C_PLAYER_SEND_ACTIVEPOINT_FROM_ROOM_COMMAND = 0x00030086;
	 public static int S_PLAYER_SEND_ACTIVEPOINT_FROM_ROOM_COMMAND = 0x00030086;
	 
	 /** 每日奖励 */
	 public static int C_PLAYER_EVERYDAY_REWARD_COMMAND = 0x00030087;
	 public static int S_PLAYER_EVERYDAY_REWARD_COMMAND = 0x00030087;
	 
	 /** 通知facebook相关信息 */
	 public static int C_PLAYER_FACEBOOK_INFO_COMMAND = 0x00030088;
	 public static int S_PLAYER_FACEBOOK_INFO_COMMAND = 0x00030088;
	 
	 /** 守护神(宠物)基础操作 */
	 public static int C_PLAYER_BATTLE_PET_BASE_COMMAND = 0x00030089;
	 public static int S_PLAYER_BATTLE_PET_BASE_COMMAND = 0x00030089;
	 
	 
	 
	 /** 
	  * NPC消息类型
	  */
	 public static final int T_NPC_COMMAND = 0x00040000;  
	
	 /** 请求NPC对话 */
	 public static  int C_CLICK_NPC_COMMAND = 0x00040001;
	 public static  int S_CLICK_NPC_COMMAND = 0x00040001;
	 
	 /** npc任务消息头 */
	 public static  int C_TASK_NPC_COMMAND  = 0x00040002;
	 public static  int S_TASK_NPC_COMMAND = 0x00040002;
	 
	 
	 
	 /** 
	  * 商店消息
	  */
	 public static final int T_SHOP_COMMAND = 0x00050000;
	 /** 打开商店 */
	 public static  int C_OPEN_SHOP_COMMAND = 0x00050001;
	 public static  int S_OPEN_SHOP_COMMAND = 0x00050001;
	 
	 /** 购买物品 */
	 public static  int C_BUY_GOODS_COMMAND = 0x00050002;
	 public static  int S_BUY_GOODS_COMMAND = 0x00050002;
	 
	 /** 出售物品 */
	 public static  int C_SALE_GOODS_COMMAND = 0x00050003;
	 public static  int S_SALE_GOODS_COMMAND = 0x00050003;
	 
	 /** 用荣誉值购买商品 */
	 public static  int C_BUY_GOODS_FROM_HONOUR_COMMAND = 0x00050004;
	 public static  int S_BUY_GOODS_FROM_HONOUR_COMMAND = 0x00050004;
	 
	 
	 /**
	  *  背包信息
	  */
	 public static final int T_BAG_COMMAND = 0x00060000;
	 
	 /** 删除背包中的物品 */
	 public static  int C_DELETE_GOODS_FROM_BAG_COMMAND = 0x00060001;
	 public static  int S_DELETE_GOODS_FROM_BAG_COMMAND = 0x00060001;
	 
	 /** 取下装备 */
	 public static  int C_TAKE_OFF_EQUIP_COMMAND = 0x00060003;
	 public static  int S_TAKE_OFF_EQUIP_COMMAND = 0x00060003;
	 
	 /** 穿上装备 */
	 public static  int C_PUT_ON_EQUIP_COMMAND = 0x00060004;
	 public static  int S_PUT_ON_EQUIP_COMMAND = 0x00060004;
	 
	 /** 使用物品 */
	 public static  int C_USE_GOODS_COMMAND = 0x00060005;
	 public static  int S_USE_GOODS_COMMAND = 0x00060005;
	 
	 /** 改变物品在背包中的位置 */
	 public static  int C_CHANGE_BAG_GOODS_LOCATION_COMMAND = 0x00060006;
	 public static  int S_CHANGE_BAG_GOODS_LOCATION_COMMAND = 0x00060006;
	 
	 /** 查看玩家背包 */
	 public static  int C_GET_PLAYER_BAG_COMMAND = 0x00060007;
	 public static  int S_GET_PLAYER_BAG_COMMAND = 0x00060007;
	 
	 /** 请求玩家快捷栏列表 */
	 public static  int C_GET_PLAYER_SHORTCUT_BAR_COMMAND = 0x00060009;
	 public static  int S_GET_PLAYER_SHORTCUT_BAR_COMMAND = 0x00060009;
	 
	 /** 设置玩家玩家快捷栏列表 */
	 public static  int C_SET_PLAYER_SHORTCUT_BAR_COMMAND = 0x00060010;
	 public static  int S_SET_PLAYER_SHORTCUT_BAR_COMMAND = 0x00060010;
	 
	 /** 取消物品快捷方式 */
	 public static  int C_CANCEL_GOODS_SHORTCUT_COMMAND = 0x00060011;
	 public static  int S_CANCEL_GOODS_SHORTCUT_COMMAND = 0x00060011;
	 
	 /**  获取物品对象 */
	 public static  int C_GET_GOODS_INFO_COMMAND = 0x00060012;
	 public static  int S_GET_GOODS_INFO_COMMAND = 0x00060012;
	 
	 /** 拆分物品 */
	 public static  int C_SPLIT_GOODS_COMMAND = 0x00060013;
	 public static  int S_SPLIT_GOODS_COMMAND = 0x00060013;
	 
	 /** 叠加物品 */
	 public static  int C_SUPERPOSE_GOODS_COMMAND = 0x00060014;
	 public static  int S_SUPERPOSE_GOODS_COMMAND = 0x00060014;
	 
	 /** 增加物品 */
	 public static  int C_ADD_GOODS_COMMAND = 0x00060015;
	 public static  int S_ADD_GOODS_COMMAND = 0x00060015;
	 
	 /** 学习技能 */
	 public static  int C_STUDY_SKILL_COMMAND = 0x00060016;
	 public static  int S_STUDY_SKILL_COMMAND = 0x00060016;
	 
	 /** 合成材料 */
	 public static  int C_BAG_SYNTHETIZE_COMMAND = 0x00060017;
	 public static  int S_BAG_SYNTHETIZE_COMMAND = 0x00060017;
	 	 
	 /** 合成装备 */
	 public static  int C_BAG_SSHOP_COMMAND = 0x00060018;
	 public static  int S_BAG_SSHOP_COMMAND = 0x00060018;
	 
	 /** 拆解装备 */
	 public static  int C_BAG_DSHOP_COMMAND = 0x00060019;
	 public static  int S_BAG_DSHOP_COMMAND = 0x00060019;
	 
	 /** 绑定物品 */
	 public static  int C_BAG_GOODS_BIND = 0x00060020;
	 public static  int S_BAG_GOODS_BIND = 0x00060020;
	 
	 /** 宝箱开启 */
	 public static  int C_OPEN_BOX_COMMAND = 0x00060021;
	 public static  int S_OPEN_BOX_COMMAND = 0x00060021;
	 
	 /**  获取任务物品对象 */
	 public static  int C_GET_TASK_GOODS_COMMAND = 0x00060022;
	 public static  int S_GET_TASK_GOODS_COMMAND = 0x00060022;
	 
	 /** 开宝箱提取结果 */
	 public static  int C_BOX_RESULT_COMMAND = 0x00060023;
	 public static  int S_BOX_RESULT_COMMAND = 0x00060023;
	 
	 /** 记忆宝石 */
	 public static  int C_MEMORY__STONE_COMMAND = 0x00060024;
	 public static  int S_MEMORY__STONE_COMMAND = 0x00060024;
	
	 /** 兑换行动值点数 */
	 public static  int C_BAG_BUYACTIVE_COMMAND = 0x00060025;
	 public static  int S_BAG_BUYACTIVE_COMMAND = 0x00060025;
	 
	 /** BOSS宝箱 */
	 public static  int C_BOSS_BOX_PLAY_COMMAND = 0x00060026;
	 public static  int S_BOSS_BOX_PLAY_COMMAND = 0x00060026;
	 
	 /** 新手大礼包 */
	 public static  int C_NEW_PLAYER_GIFTGOODS_COMMAND = 0x00060027;
	 public static  int S_NEW_PLAYER_GIFTGOODS_COMMAND = 0x00060027;

	 /** 整理背包 */
	 public static  int C_SORT_GOODS_COMMAND = 0x00060028;
	 public static  int S_SORT_GOODS_COMMAND = 0x00060028;
	 
	 /** 宝石追加装备属性(炼化) */
	 public static  int C_SYN_GEM_EQUIP_COMMAND = 0x00060029;
	 public static  int S_SYN_GEM_EQUIP_COMMAND = 0x00060029;
	 
	 /** 物品进化 */
	 public static  int C_GOODS_UP_COMMAND = 0x00060030;
	 public static  int S_GOODS_UP_COMMAND = 0x00060030;
	 
	 /** 结婚形象COPY进化*/
	 public static  int C_EQUIP_COPY_UP_COMMAND = 0x00060031;
	 public static  int S_EQUIP_COPY_UP_COMMAND = 0x00060031;
	 
	 /** 生命精力宝石同值进化*/
	 public static  int C_LIFE_MAGIC_UP_COMMAND = 0x00060032;
	 public static  int S_LIFE_MAGIC_UP_COMMAND = 0x00060032;
	 
	 
	 /** 
	  * 
	  * 玩家仓库指令 
	  * 
	  * */
	 public static final int T_STORAGE_COMMAND = 0x00070000;
	 
	 /** 仓库密码(设置密码，修改密码) */
	 public static  int C_STORAGE_PASSWORD_OPTION_COMMAND = 0x00070001;
	 public static  int S_STORAGE_PASSWORD_OPTION_COMMAND = 0x00070001;
	 
	 /** 打开仓库 */
	 public static  int C_OPEN_STORAGE_COMMAND = 0x00070002;
	 public static  int S_OPEN_STORAGE_COMMAND = 0x00070002;
	 
	 /** 添加物品到仓库 */
	 public static  int C_ADD_GOODS_TO_STORAGE_COMMAND = 0x00070003;
	 public static  int S_ADD_GOODS_TO_STORAGE_COMMAND = 0x00070003;
	 
	 /** 从仓库中取出物品 */
	 public static  int C_TAKEOUT_GOODS_FROM_STORAGE_COMMAND = 0x00070004;
	 public static  int S_TAKEOUT_GOODS_FROM_STORAGE_COMMAND = 0x00070004;
	 
	 /** 改变物品在仓库中位置 */
	 public static  int C_CHANGE_STORAGE_GOODS_LOCATION_COMMAND = 0x00070005;
	 public static  int S_CHANGE_STORAGE_GOODS_LOCATION_COMMAND = 0x00070005;
	 
	 /** 仓库中金钱的操作(存钱，取钱)(游戏币) */
	 public static  int C_MONEY_OPTION_COMMAND = 0x00070006;
	 public static  int S_MONEY_OPTION_COMMAND = 0x00070006;
	 
	 /** 验证仓库是否有密码 */
	 public static  int C_VALIDATE_STORAGE_ISPASSWORD_COMMAND = 0x00070007;
	 public static  int S_VALIDATE_STORAGE_ISPASSWORD_COMMAND = 0x00070007;
	 
	 /** 扩充仓库容量 */
	 public static  int C_UP_STORAGE_SIZE_COMMAND = 0x00070008;
	 public static  int S_UP_STORAGE_SIZE_COMMAND = 0x00070008;
	 
	 /** 整理仓库 */
	 public static  int C_SORT_STORAGE_COMMAND = 0x00070009;
	 public static  int S_SORT_STORAGE_COMMAND = 0x00070009;
	 
	 /** 关闭仓库 */
	 public static  int C_CLOSE_STORAGE_COMMAND = 0x00070010;
	 public static  int S_CLOSE_STORAGE_COMMAND = 0x00070010;
	 
	 
	 /**
	  * 战斗消息
	  */
	 public static final int T_BATTLE_COMMAND = 0x00080000;
	 
	 /** 玩家退出战斗,可能是短线可能是逃跑... */
	 public static  int C_BATTLE_REMOVE_COMMAND = 0x00080001;
	 public static  int S_BATTLE_REMOVE_COMMAND = 0x00080001;
	 
	 /** 玩家进出战斗，通知战斗内的其他玩家 */
	 public static  int C_BATTLE_IO_COMMAND = 0x00080002;
	 public static  int S_BATTLE_IO_COMMAND = 0x00080002;
	 
	 /** 接收玩家动作... */
	 public static  int C_BATTLE_ACTION_COMMAND = 0x00080003;
	 public static  int S_BATTLE_ACTION_COMMAND = 0x00080003;
	 
	 /** 通知本场战斗中战斗结束 */
	 public static  int C_BATTLE_PLAYER_BATTLEEND = 0x00080004;
	 public static  int S_BATTLE_PLAYER_BATTLEEND = 0x00080004;
	 
	 /** 通知本场战斗中战斗结果 true 胜利，false 失败 */
	 public static  int C_BATTLE_PLAYER_BATTLERESULT = 0x00080005;
	 public static  int S_BATTLE_PLAYER_BATTLERESULT = 0x00080005;
	 
	 /** 战斗中死亡通知 */
	 public static  int C_BATTLE_DIE = 0x00080006;
	 public static  int S_BATTLE_DIE = 0x00080006;
	 
	 /** 战斗中更新玩家信息 */
	 public static  int C_BATTLE_PLAYER_UPDATE_COMMAND = 0x00080007;
	 public static  int S_BATTLE_PLAYER_UPDATE_COMMAND = 0x00080007;
	 
	 /** 花钱复活 */
	 public static  int C_BATTLE_PLAYER_RESET_COMMAND = 0x00080008;
	 public static  int S_BATTLE_PLAYER_RESET_COMMAND = 0x00080008;
	 
	 
	 /**
	  * 组队消息
	  */
	 public static final int T_TEAM_COMMAND = 0x00100000;
	 
	 /** 队长转让 */
	 public static  int C_LEADER_TRANSFER_COMMAND = 0x00100001;
	 public static  int S_LEADER_TRANSFER_COMMAND = 0x00100001;
	 
	 /** 队长T人 */
	 public static  int C_LEADER_T_MEMBER_COMMAND = 0x00100002;
	 public static  int S_LEADER_T_MEMBER_COMMAND = 0x00100002;
	 
	 /** 解散组 */
	 public static  int C_LEADER_DISBAND_TEAM_COAMMAND = 0x00100003;
	 public static  int S_LEADER_DISBAND_TEAM_COAMMAND = 0x00100003;
	 
	 /** 玩家离开组 */
	 public static  int C_MEMBER_LEAVE_TEAM_COMMAND = 0x00100004;
	 public static  int S_MEMBER_LEAVE_TEAM_COMMAND = 0x00100004;
	 
	 /** 队长查看申请列表 */
	 public static  int C_TEAM_APPLY_LIST_COMMAND = 0x00100005;
	 public static  int S_TEAM_APPLY_LIST_COMMAND = 0x00100005;
	 
	 /** 队长批准玩家加入队伍 */
	 public static  int C_TEAM_AGREE_APPLY_COMMAND = 0x00100006;
	 public static  int S_TEAM_AGREE_APPLY_COMMAND = 0x00100006;
	 
	 /** 队长清空申请列表 */
	 public static  int C_TEAM_CLEAR_APPLY_COMMAND = 0x00100007;
	 public static  int S_TEAM_CLEAR_APPLY_COMMAND = 0x00100007;
	 
	 /** 队长设置队伍信息(现在只能修改队伍名字) */
	 public static  int C_TEAM_SET_INFO_COMMAND = 0x00100008;
	 public static  int S_TEAM_SET_INFO_COMMAND = 0x00100008;
	
	 
	 
	 /**
	  * 交易消息
	  */
	 public static final int T_BUSINESS_COMMAND = 0x00110000;
	 
	 /** 物品放入拿出，通知交易双方 */
	 public static  int C_BUSINESS_GOODS_IO_COMMAND = 0x00110001;
	 public static  int S_BUSINESS_GOODS_IO_COMMAND = 0x00110001;
	 
	 /** 交易结束 */
	 public static  int C_BUSINESS_OVER_COMMAND = 0x00110002;
	 public static  int S_BUSINESS_OVER_COMMAND = 0x00110002;
	 
	 /** 交易完成(成功或者失败) */
	 public static  int C_BUSINESS_OK_COMMAND = 0x00110003;
	 public static  int S_BUSINESS_OK_COMMAND = 0x00110003;
	 
	 
	 /**
	  * 答题消息
	  */
	 public static final int T_ANSWER_COMMAND = 0x00130000;
	 
	 /** 答题状态  */
	 public static  int C_ANSWER_QUESTION_STATE_COMMAND = 0x00130001;
	 public static  int S_ANSWER_QUESTION_STATE_COMMAND = 0x00130001;
	 
	 /** 刷新题目  */
	 public static  int C_QUESTION_FLUSH_COMMAND = 0x00130002;
	 public static  int S_QUESTION_FLUSH_COMMAND = 0x00130002;
	 
	 /** 答题结果  */
	 public static  int C_ANSWER_QUESTION_RESULT_COMMAND = 0x00130003;
	 public static  int S_ANSWER_QUESTION_RESULT_COMMAND = 0x00130003;
	 
	 /** 累计获得奖励  */
	 public static  int C_TOTAL_REWARD_COMMAND = 0x00130004;
	 public static  int S_TOTAL_REWARD_COMMAND = 0x00130004;
	 
	 /** 取得排行榜  */
	 public static  int C_GET_ANSWER_REWARD_COMMAND = 0x00130005;
	 public static  int S_GET_ANSWER_REWARD_COMMAND = 0x00130005;
	
	 /** 关闭答题  */
	 public static  int C_CLOSE_ANSWER_COMMAND = 0x00130006;
	 public static  int S_CLOSE_ANSWER_COMMAND = 0x00130006;
	 
	 /** 经验兑换 */
	 public static  int C_PLAYER_CHANGE_EXP_COMMAND = 0x00130007;
	 public static  int S_PLAYER_CHANGE_EXP_COMMAND = 0x00130007;
	 
	 /** 快速答题 */
	 public static  int C_PLAYER_MONEY_ANSWER_COMMAND = 0x00130008;
	 public static  int S_PLAYER_MONEY_ANSWER_COMMAND = 0x00130008;
	 
	 

	 /**
	  * 活动消息
	  */
	 public static final int T_PARTY_COMMAND = 0x00140000;
	 
	 /** 查看活动玩家排行榜 */
	 public static  int C_GET_PARTY_PLAYER_REWARD_COMMAND = 0x00140001;
	 public static  int S_GET_PARTY_PLAYER_REWARD_COMMAND = 0x00140001;
	 
	 /** 查看活动家族排行榜 */
	 public static  int C_GET_PARTY_FAMILY_REWARD_COMMAND = 0x00140002;
	 public static  int S_GET_PARTY_FAMILY_REWARD_COMMAND = 0x00140002;
	 
	 /** 查看自己家族所有成员荣誉 */
	 public static  int C_GET_PLAYER_FAMILY_HONOUR_COMMAND = 0x00140003;
	 public static  int S_GET_PLAYER_FAMILY_HONOUR_COMMAND = 0x00140003;
	 
	 /** 查看所有副本集体排行 */
	 public static  int C_GET_COPY_PLAYER_RANK_COMMAND = 0x00140004;
	 public static  int S_GET_COPY_PLAYER_RANK_COMMAND = 0x00140004;
	 
	 /** 查看副本中玩家排行 */
	 public static  int C_GET_COPY_PLAYER_REWARD_COMMAND = 0x00140005;
	 public static  int S_GET_COPY_PLAYER_REWARD_COMMAND = 0x00140005;
	 
	 /** 黄金斗士活动玩家领取奖励并展示形象 */
	 public static  int C_GOLD_PARTY_REWARD_SHOW_COMMAND = 0x00140006;
	 public static  int S_GOLD_PARTY_REWARD_SHOW_COMMAND = 0x00140006;
	 
	 /** 查看黄金斗士活动排行 */
	 public static  int C_GOLD_PARTY_BATTLE_RANK_COMMAND = 0x00140007;
	 public static  int S_GOLD_PARTY_BATTLE_RANK_COMMAND = 0x00140007;
	 
	 /** 黄金斗士领取签名 */
	 public static  int C_GOLD_PARTY_SIGN_NAME_COMMAND = 0x00140008;
	 public static  int S_GOLD_PARTY_SIGN_NAME_COMMAND = 0x00140008;
	 
	 /** 黄金斗士战斗次数显示 */
	 public static  int C_GOLD_PARTY_BATTLE_COUNT_COMMAND = 0x00140009;
	 public static  int S_GOLD_PARTY_BATTLE_COUNT_COMMAND = 0x00140009;
	 
	 /** 女神试练积分通知 */
	 public static  int C_COPY_POINT_SHOW_COMMAND = 0x00140010;
	 public static  int S_COPY_POINT_SHOW_COMMAND = 0x00140010;
	 
	 
	 /**
	  * 剧情消息
	  */
	 public static final int T_STORY_COMMAND = 0x00150000;
	 
	 /** 玩家开始剧情 */
	 public static int C_STORY_START_INFO_COMMAND = 0x00150001;
	 public static int S_STORY_START_INFO_COMMAND = 0x00150001;
	 
	 /** 玩家结束剧情 */
	 public static int C_STORY_END_INFO_COMMAND = 0x00150002;
	 public static int S_STORY_END_INFO_COMMAND = 0x00150002;
	 
	 /** 单个剧情信息 */
	 public static int C_STORY_INFO_COMMAND = 0x00150003;
	 public static int S_STORY_INFO_COMMAND = 0x00150003;
	 
	 /** 开始进行事件 */
	 public static int C_STORY_START_EVENT_COMMAND = 0x00150004;
	 public static int S_STORY_START_EVENT_COMMAND = 0x00150004;
	 
	 /** 结束当前事件 */
	 public static int C_STORY_END_EVENT_COMMAND = 0x00150005;
	 public static int S_STORY_END_EVENT_COMMAND = 0x00150005;
	 
	 
	 
	 /**
	  * 服务器附加
	  */
	 public static final int T_ATTACHMENT_COMMAND = 0x00120000;
	 
	 public static  int C_SHOPCENTER_COMMAND = 0x00120001;
	 public static  int S_SHOPCENTER_COMMAND = 0x00120001;
	 
	 

	 /** 错误提示类型*/

	 public static final int T_ERROR_COMMAND = 0x00090000;
	 
	 public static  int S_ERROR_MESSAGE = 0x00090001;
	 public static  int S_ALERT_MESSAGE = 0x00090002;
	 public static  int S_EXCEPTION_MESSAGE = 0x00090003;
	
	 
	 

	 

	public SMsg(int type)
	{
		super(type, 256);
	}

	public SMsg(int type, ByteBuffer buffer)
	{
		super(type, buffer, null);
	}

}
