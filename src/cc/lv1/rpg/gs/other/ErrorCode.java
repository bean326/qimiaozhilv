package cc.lv1.rpg.gs.other;

/**
 * 错误指令
 * @author balan
 *
 */
public class ErrorCode {
	
	/** 玩家没在游戏中 */
	public static final int EXCEPTION_LOGIN_PLAYERNOTATGAME = 100;  
	/** 非法登陆 */
	public static final int EXCEPTION_LOGIN_ERROR = 101;   
	/** 玩家未登陆游戏 */
	public static final int EXCEPTION_LOGIN_NOTINGAME = 102;  
	/** 已经有这个名字 */
	public static final int EXCEPTION_LOGIN_SAMENAME = 103;  
	/** 读取玩家信息错误 */
	public static final int EXCEPTION_LOGIN_PLAYINFOERROR = 104;  
	/** 角色名不能为空 */
	public static final int ALERT_ROLENAME_NULL_ERROR = 105;
	/** 类型错误 */
	public static final int EXCEPTION_CLASS_ERROR = 106;
	/** 你的等级不够 */
	public static final int ALERT_PLAYER_LEVEL_LACK = 107;
	/** 没有这个阵营 */
	public static final int ALERT_NOT_THE_CAMP = 108;
	/** 你不能转这个职业 */
	public static final int ALERT_UP_PROFESSION_ERROR = 109;
	/** 你已经转过职了 */
	public static final int ALERT_PLAYER_HASBEEN_UP_PROFESSION = 110;
	/** 玩家拒绝加入您的家族 */
	public static final int ALERT_PLAYER_REFUSE_IN_FAMILY = 111;
	/** 邀请的玩家加入另外的家族了 */
	public static final int ALERT_PLAYER_IN_OTHER_FAMILY = 112;
	/** 玩家答应你加入家族了 */
	public static final int ALERT_PLAYER_AGREE_IN_FAMILY = 113;
	/** 对方取消邀请或超时 */
	public static final int ALERT_PLAYER_CANCEL = 114;
	/** 转让的人员不是本家族人员 */
	public static final int EXCEPTION_PLAYER_CHANGENOT = 115;
	/** 目标玩家不在线  */
	public static final int EXCEPTION_PLAYER_OFFLINE = 116;
	/** 您要创建的家族名已存在  */
	public static final int EXCEPTION_PLAYER_FAMILYNAME_SAME = 117;
	/** 目标玩家已有家族 */
	public static final int EXCEPTION_PLAYER_HAS_FAMILY = 118;
	/** 还没有家族 */
	public static final int EXCEPTION_PLAYER_NOHAS_FAMILY = 119;
	/** 当前家族人员已满 */
	public static final int ALERT_FAMILY_ISFULL = 120;
	/** 重复的邀请 */
	public static final int ALERT_PLAYER_SAME_INVITATION = 121;
	/** 邀请已发出 */
	public static final int ALERT_PLAYER_INVITATION_SENDED = 122;
	/** 目标玩家已在本家族中 */
	public static final int EXCEPTION_PLAYER_ISIN_FAMILY = 123;
	/** 好友已达上限 */
	public static final int ALERT_FRIENDLIST_ISFULL = 124;
	/** 玩家不在好友列表中 */
	public static final int ALERT_PLAYER_NOT_FRIENDS = 125;
	/** 玩家已在黑名单中 */
	public static final int ALERT_PLAYER_ISIN_BLACK = 126;
	/** 玩家不在黑名单中 */
	public static final int ALERT_PLAYER_NOTIN_BLACK = 127;
	/** 超过等级限制 */
	public static final int ALERT_PLAYER_OVER_LEVEL_ERROR = 128;
	/** 邀请超时 */
	public static final int ALERT_INVITE_TIME_OVERRUN = 129;
	/** 错误的邀请类型 */
	public static final int ALERT_INVITE_CLASS_ERROR = 130;
	/** 家族已不存在 */
	public static final int ALERT_FAMILY_NOT_EXIST = 131;
	/** 选择阵营不能做其它事情 */
	public static final int ALERT_CHOOSE_CAMP_ING = 132;
	/** 创建家族条件不足 */
	public static final int ALERT_FAMILY_NOT_CREATE = 133;
	/** 没有这个职业 */
	public static final int EXCEPTION_PROFESSION_NOT_EXIST = 134;
	/** 性别错误 */
	public static final int EXCEPTION_SEX_NOT_EXIST = 135;
	/** 请结束战斗或者交易 */
	public static final int ALERT_PLAYER_OVER_OTHER= 136;
	/** 5秒钟内不能连续转换场景 */
	public static final int ALERT_ROOM_FIVE_CHANGE= 137;
	/** 正在保存刷新角色信息 */
	public static final int ALERT_PLAYER_DATA_SAVE= 138;
	/** 请选择跟家族一样的阵营 */
	public static final int ALERT_FAMILY_CAMP_ERROR = 139;
	/** 不符合领取宝箱的条件 */
	public static final int ALERT_BOX_GET_ERROR = 140;
	/** 请先关闭已经开启的宝箱 */
	public static final int ALERT_BOX_OPEN_ERROR = 141;
	/** 你没有小喇叭道具 */
	public static final int ALERT_NO_SMALL_SPEAKER = 142;
	/** 你没有大喇叭道具 */
	public static final int ALERT_NO_BIG_SPEAKER = 143;
	/** 你没有超级喇叭道具 */
	public static final int ALERT_NO_SUPER_SPEAKER = 144;
	/** 本功能只对会长和副会长开放" */
	public static final int ALERT_CDR_CAN_USE = 145;
	/** 背包最少需要有4个空格 */
	public static final int ALERT_BOX_NULLPOINT_FOUR = 146;
	/** 修改族长的间隔时间还没到 */
	public static final int ALERT_CHANGE_FAMILY_LEADER_REPEAT = 147;
	/** 你没有家族，不能创建帮会 */
	public static final int ALERT_NO_FAMILY_ERROR = 148;
	/** 你今天的帮忙次数已达最大 */
	public static final int ALERT_AAM_COUNT_MAX = 149;
	/** 对方今天的帮忙次数已达最大 */
	public static final int ALERT_TARGET_AAM_COUNT_MAX = 150;
	/** 你的队友等级超过限制 */
	public static final int ALERT_TEAM_PLAYER_OVERLEVEL_ERROR = 151;
	/** 你的等级还不能加入阵营 */
	public static final int ALERT_LEVEL_LESS_CAMP_LEVEL = 152;
	/** 你已经有了阵营了 */
	public static final int ALERT_PLAYER_CAMP_NOT_NULL = 153;
	/** 任命的对象不是族长 */
	public static final int ALERT_OBJECT_NO_SHAIKH = 154;
	/** 任命的对象不是本家族成员 */
	public static final int ALERT_OBJECT_NO_SAME_FAMILY = 155;
	/** 元宝不够 */
	public static final int ALERT_BOX_MONEY_NO_ENOUGH = 156;
	/** 战斗中不能开宝箱 */
	public static final int ALERT_CANNOT_BOX_ERROR = 157;
	/** 这个不是宠物食物 */
	public static final int ALERT_GOODS_NOT_PETFOOD = 158;
	/** 今天喂养次数已达最大 */
	public static final int ALERT_FEED_PET_COUNT_OVER = 159;
	/** 为了你的号码安全，密码必须大于6位 */
	public static final int ALERT_PWD_LESS = 160;
	/** "玩家阵营和家族阵营不一样，不能加入" */
	public static final int ALERT_CAMP_FAMILY_NOT_SAME = 161;
	/** 玩家位置已记忆！ */
	public static final int ALERT_PLAYER_INDEX_SAVED = 162;
	/** 你的宠物今天出行次数已达最大 */
	public static final int ALERT_RUNNING_PET_COUNT_OVER = 163;
	/** 你的队友等级不够 */
	public static final int ALERT_TEAMER_LV_CANNOT = 164;
	/** "你的队友行动条未满!" */
	public static final int ALERT_TEAMER_ACTIVE_CANNOTs = 165;
	/** "你有队员的事件值不够，不能换房间" */
	public static final int ALERT_TEAMER_EVENTPOINT_CANNOT = 166;
	/** "你的事件值不够，不能换房间" */
	public static final int ALERT_PLAYER_EVENTPOINT_CANNOT = 167;
	/** 您领取的任务数量已达上限 */
	public static final int ALERT_PLAYER_TASK_MAX_COUNT = 168;
	/** 你使用大喇叭的等级不够 */
	public static final int ALERT_PLAYER_LEVEL_SPEAKER = 169;
	/** 你上一题还没答 */
	public static final int ALERT_PRVQUESTION_NO_ANSWER = 170;
	/** 你今天的答题机会已用完 */
	public static final int ALERT_NO_ANSWER_DAY = 171;
	/** 答题时间已过 */
	public static final int ALERT_NO_ANSWER_TIME = 172;
	/** 家族战中，此功能暂时不开放 */
	public static final int ALERT_IS_FAMILY_START = 173;
	/** 答题系统已更新，请重新答题 */
	public static final int ALERT_ANSWER_UPDATE = 174;
	/** 一个月只能修改一次 */
	public static final int ALERT_MODIFY_CAMP_OR_NAME_OVER = 175;
	/** 没有阵营不能修改 */
	public static final int ALERT_CAMP_NOT_MODIFY = 176;
	/** 请先退出家族再修改阵营 */
	public static final int ALERT_NO_FAMILY_CAMP = 177;
	/** 你没有修改名字的道具*/
	public static final int ALERT_NO_CHANGE_NAME_GOODS = 178;
	/** 角色名非法*/
	public static final int ALERT_NOT_USE_NAME = 179;
	/** 名字超过最长*/
	public static final int ALERT_NAME_LENGTH_OVER = 180;
	/** 系统正在更新，不能进行相关操作*/
	public static final int ALERT_SYSTEM_IS_UPDATE = 181;
	/** 目标玩家自动打怪挂机中*/
	public static final int ALERT_TARGET_PLAYER_AUTO_BATTLEING = 182;
	/** 战斗中不能设置挂机*/
	public static final int ALERT_BATTLEING_NOT_SETAUTO = 183;
	/** 交易中不能设置挂机*/
	public static final int ALERT_BUSINESSING_NOT_SETAUTO = 184;
	/** 请先退出家族再修改名字 */
	public static final int ALERT_FIRST_FAMILY_OUT = 185;
	/** 错误的身份证号码 */
	public static final int ALERT_IDCARD_ERROR = 186;
	/** 请输入中文姓名 */
	public static final int ALERT_NAME_ERROR = 187;
	/** 您还不足2001级，想做师傅，还要努力哦 */
	public static final int ALERT_MASTER_CON_ERROR = 188;
	/** 你已有徒弟了 */
	public static final int ALERT_APP_ISNOT_NULL = 189;
	/** 他超过2000级，不需要师傅了 */
	public static final int ALERT_TARGET_APP_CON_ERROR = 190;
	/** 目标玩家已有师徒关系 */
	public static final int ALERT_TARGET_APPMAS_ISNOT_NULL = 191;
	/** 不同阵营此处不能建立师徒关系 */
	public static final int ALERT_NOTSAME_CAMP_AM = 192;
	/** 不同家族此处不能建立师徒关系 */
	public static final int ALERT_NOTSAME_FAMI_AM = 193;
	/** 目标玩家已有了徒弟了 */
	public static final int ALERT_TARGET_APP_ISNOT_NULL = 194;
	/** 师徒关系解除，对方已下线或取消师徒关系 */
	public static final int ALERT_CLEAR_MAS_APP = 195;
	/** 你已超过2000级，不能再当徒弟了 */
	public static final int ALERT_APP_CON_ERROR = 196;
	/** 已经超过150场师徒战斗，不能再获得师徒额外奖励 */
	public static final int ALERT_MASAPP_MAX_BATTLE = 197;
	/** 你已经有师父了 */
	public static final int ALERT_MASTER_ISNOT_NULL = 198;
	/** 请先关闭顶顶猫 */
	public static final int ALERT_PLAYER_IS_MONEYBOX = 199;
	
	
	/** 交易邀请取消 */
	public static final int ALERT_BUSINESS_INVITE_CANCEL = 200;
	/**  交易已经关闭 */
	public static final int ALERT_BUSINESS_HASBEEN_OVER = 201;
	/** 玩家正在交易中 */
	public static final int ALERT_BUSINESS_ING_PLAYER = 202;
	/** 物品已绑定 不能交易 */
	public static final int ALERT_GOODS_NOT_BUSINESS = 203;
	/** 数量超出限制 */
	public static final int ALERT_GOODSCOUNT_OVERRUN = 204;
	/** 交易金钱数量超出限制 */
	public static final int ALERT_BUSINESS_MONEY_OVERRUN = 205;
	/** 战斗中不能交易 */
	public static final int ALERT_NOT_BUSINESS_IN_BATTLE = 206;
	/** 玩家拒绝和你交易 */
	public static final int ALERT_PLAYER_REJECT_BUSINESS = 207;
	
	/** 已达到寄卖上限数量 */
	public static final int ALERT_PLAYER_SHOPCENTER_FULL = 208;
	
	/** "寄卖中心没有该物品" */
	public static final int ALERT_SHOPCENTER_GOODS_NULL = 209;
	
	/** 很遗憾,你已被族长T出家族了" */
	public static final int ALERT_FAMILY_OUT = 210;
	/** 对方背包空间不足9个,不能交易" */
	public static final int ALERT_TARGET_BAG_KONGJIAN_ERROR = 211;
	/** 你的背包空间不足9个,不能交易" */
	public static final int ALERT_PLAYER_BAG_KONGJIAN_ERROR = 212;
	/** 你有物品在寄卖中，需要下架以后才可以修改名字" */
	public static final int ALERT_MODIFY_NAME_SHOPCENTER_ERROR = 213;
	/** 已经开始答题以后，就不能使用快答功能" */
	public static final int ALERT_MONEY_ANSWER_ERROR = 214;
	/** 你今天已经使用过快答或者你今天已经没有机会使用快答" */
	public static final int ALERT_MONEY_ANSWER_NOTDO = 215;
	
	
	/** PVP邀请取消 */
	public static final int ALERT_PVP_INVITE_CANCEL = 300;
	/** 玩家已经在战斗中 */
	public static final int ALERT_PLAYER_IS_BATTLING = 301;
	/** 不同阵营的人不能切磋,请选择恶意攻击 */
	public static final int ALERT_NOT_INVITE_DIFF_CAMP = 302;
	/** 16级以前不能PK */
	public static final int ALERT_BEFORE_SIXTEEN_NOT_PK = 303;
	/** 不能向非队长发出请求 */
	public static final int ALERT_REQUEST_NOT_LEADER_ERROR = 304;
	/** 此房间不允许战斗 */
	public static final int ALERT_ROOM_ISNOT_BATTLE = 305;
	/** 怪物不存在 */
	public static final int ALERT_MONSTER_IS_NOTHING = 306;
	/** 玩家血量少于1点 */
	public static final int ALERT_PLAYER_NOT_LIFE = 307;
	/** 你不是队长 */
	public static final int ALERT_PLAYER_NOT_LEADER = 308;
	/** 你的队伍中有玩家生命小于1点 */
	public static final int ALERT_OHTER_PLAYER_NOT_LIFE = 309;
	/** 怪物正在战斗 */
	public static final int ALERT_MONSTER_IS_FIGHT = 310;
	/** 玩家不在房间中 */
	public static final int ALERT_PLAYER_ISNOT_ROOM = 311;
	/** 魔力不够 */
	public static final int ALERT_PLAYER_MAGIC_NOTENOUTH = 312;
	/** 玩家中了混乱效果被锁住 */
	public static final int ALERT_PLAYER_IS_LOCKED = 313;
	/** 初始化战斗失败 */
	public static final int ALERT_INIT_BATTLE_FAIL = 314;
	/** 目标INDEX越界 */
	public static final int ALERT_OBJECT_INDEX_OVERRUN = 315;
	/** 目标为空 */
	public static final int ALERT_OBJECT_IS_NULL = 316;
	/** 效果对象不存在 */
	public static final int ALERT_EFFECT_NOT_EXIST = 317;
	/** 同组队员不能PK */
	public static final int ALERT_SAME_TEAM_PLAYER = 318;
	/** 队伍人数已达最大 */
	public static final int ALERT_TEAM_PLAYERS_TOO_MUCH = 319;
	/** 不能强行攻击同阵营的玩家 */
	public static final int ALERT_PLAYER_SAME_CAMP = 320;
	/** 请先退出队伍再来 */
	public static final int ALERT_MUST_EXIT_TEAM = 321;
	/** 你没有家族不能参加家族活动 */
	public static final int ALERT_PLAYER_NO_FAMILY = 322;
	/** 不能攻击同家族的玩家 */
	public static final int ALERT_PLAYER_SAME_FAMILY = 323;
	/** 休息室不能PK */
	public static final int ALERT_REST_ROOM_NOPK = 324;
	/** 活动还没开始 */
	public static final int ALERT_PARTY_NO_START = 325;
	/** 虚弱状态不能进入PK */
	public static final int ALERT_NOPK_STATE = 326;
	/** 进入PK场次数超过限制 */
	public static final int ALERT_TOPK_COUNT_OVER = 327;
	/** 此副本你今天已完成 */
	public static final int ALERT_PLAYER_FINISH_COPY = 328;
	/** 组队状态不能进副本 */
	public static final int ALERT_PLAYER_TEAM_NOTNULL = 329;
	/** 此房间不能组队 */
	public static final int ALERT_ROOM_ISNOT_TEAM = 330;
	/** 你的挂机卡数量不够 */
	public static final int ALERT_AUTO_BATTLE_CARD_ERROR = 331;
	/** 队伍里有玩家的挂机卡不够 */
	public static final int ALERT_TEAM_PLAYER_CARD_ERROR = 332;
	/** 请先解除帮忙关系 */
	public static final int ALERT_UPROLE_AAM_ERROR = 333;
	/** 请先退出家族 */
	public static final int ALERT_UPROLE_FAMILY_ERROR = 334;
	/** 请先解除婚姻关系 */
	public static final int ALERT_UPROLE_MARRY_ERROR = 335;
	/** 请先脱下身上所有装备 */
	public static final int ALERT_UPROLE_EQUIP_ERROR = 336;
	/** 你不符合转生条件 */
	public static final int ALERT_UPROLE_CONDITION_ERROR = 337;
	/** 转生需要的道具不够 */
	public static final int ALERT_UPROLE_GOODS_ERROR = 338;
	/** 转生需要完成的任务没完成 */
	public static final int ALERT_UPROLE_TASK_ERROR = 339;
	/** 转生等级不够 */
	public static final int ALERT_UPROLE_LEVEL_ERROR = 340;
	/** 请先退出队伍 */
	public static final int ALERT_UPROLE_TEAM_ERROR = 341;
	/** 请先完成或放弃你接受的所有任务 */
	public static final int ALERT_UPROLE_TASK_NOT_NULL = 342;
	/** 你没有XXX道具，请去相关完成任务后领取，或上淘宝处购买！ */
	public static final int ALERT_NO_BOSSGOODS = 343;
	/** 你今天的炼金次数已达最大 */
	public static final int ALERT_AWARDTYPE_COUNT_OVER = 344;
	/** 你的BOSS行动值不够 */
	public static final int ALERT_PLAYER_BOSS_ACTIVEPOINT_ERROR = 345;
	/** 200级以上的玩家才有资格委托我炼金 */
	public static final int ALERT_GOLD_LEVEL_ERROR = 346;
	/** 你的时空之旅行动值不够  */
	public static final int ALERT_PLAYER_TIME_ACTIVEPOINT_ERROR = 347;
	/** 你没有时空之旅记录 */
	public static final int ALERT_PLAYER_NO_TIMESKY_NOTICE = 348;
	/** 1500级以上才能进行时空之旅 */
	public static final int ALERT_PLAYER_LEVEL_ERROR_TIMESKY= 349;
	/** 组队不能参加此活动 */
	public static final int ALERT_PARTY_ISNOT_ACCEPT_TEAM = 350;
	/** 战斗次数已经超过最大 */
	public static final int ALERT_BATTLE_COUNT_IS_MAX = 351;
	/** 休息状态不能进入战斗 */
	public static final int ALERT_WINNER_IS_STOP = 352;
	/** 虚弱状态不能进入战斗 */
	public static final int ALERT_XURUO_ISNO_BATTLE = 353;
	/** 对方虚弱状态不能进入战斗 */
	public static final int ALERT_TARGET_XURUO_ISNO_BATTLE = 354;
	/** 对方休息状态不能进入战斗 */
	public static final int ALERT_TARGET_XIUXI_ISNO_BATTLE = 355;
	/** 你不是前五名 */
	public static final int ALERT_PLAYER_ISNOT_SHOWIMAGE = 356;
	/** 你已经领过奖 */
	public static final int ALERT_PLAYER_IS_GET_REWARDED = 357;
	/** 活动还没结束不能领奖 */
	public static final int ALERT_GOLD_PARTY_ISNOT_END = 358;
	/** 请至少保持背包有两个空位 */
	public static final int ALERT_BAG_NULLCOUNT_ERROR = 359;
	/** 青铜挑战次数已经到5次 */
	public static final int ALERT_MONEY_BATTLE_MAX_COUNT = 360;
	/** 不能在此房间组队挂机 */
	public static final int ALERT_NOT_AUTOBATTLE_INTHISROOM = 361;
	/** 你已经领取过活动奖励 */
	public static final int ALERT_NOT_PARTY_REWARD = 362;
	/** 此冒险副本你今天已经不能再进入 */
	public static final int ALERT_STORYCOPY_CANT_IN = 363;
	/** 组队状态下，不能进入世界地图 */
	public static final int ALERT_STORY_TEAM_ISNOT_NULL = 364;
	/** 请先保持背包至少有2个空位 */
	public static final int ALERT_STORY_BAG_ISNOT_NULL = 365;
	/** 现在不是奇妙大冲关的活动时间 */
	public static final int ALERT_CHONGGUAN_TIME_ERROR = 366;
	/** 参加大冲关的条件不够 */
	public static final int ALERT_CHONGGUAN_CONDITION_ERROR = 367;
	
	
	/** 玩家不在同一个场景 */
	public static final int ALERT_PLAYER_NOTAT_ROOM = 400;
	/** 你已经在队伍中，并且你不是队长，所以你不能邀请对方！ */
	public static final int ALERT_PLAYER_INVITE_ERROR = 401;
	/** 你由于长时间没有操作，邀请已取消！ */
	public static final int ALERT_TEAM_INVITE_CANCEL = 402;
	/** 没有权限 */
	public static final int ALERT_NOT_POWER_ERROR = 403;
	/** 请选择除自己以外的队友 */
	public static final int ALERT_NOT_TARGET_SELF= 404;
	/** 玩家已经在队伍中 */
	public static final int ALERT_PLAYER_IN_TEAM = 405;
	/** 你已不在队伍中 */
	public static final int ALERT_PLAYER_NOTIN_TEAM = 406;
	/** 对方是红名不能和你组队 */
	public static final int ALERT_TARGET_IS_REDNAME = 407;
	/** 你是红名不能和对方组队 */
	public static final int ALERT_PLAYER_IS_REDNAME = 408;
	/** 不同阵营的人不能组队 */
	public static final int ALERT_DIFF_CAMP_ERROR = 409;
	/** 队伍中有玩家正在选择阵营 */
	public static final int ALERT_PLAYER_CHOOSE_CAMPING = 410;
	/** 不同家族不能组队 */
	public static final int ALERT_DIFF_FAMILY_ERROR = 411;
	/** 玩家现在还不想组队 */
	public static final int ALERT_PLAYER_REFUSE_TEAM = 412;
	/** 你的状态设置为不想组队 */
	public static final int ALERT_PLAYER_TEAM_STATE_ERROR = 413;
	/** 玩家队伍不存在 */
	public static final int ALERT_PLAYER_TEAM_NO_EXITS = 414;
	/** 玩家不是队长 */
	public static final int ALERT_PLAYER_ISNOT_TEAM_LEADER = 415;
	/** 队伍申请列表已满，请稍候申请 */
	public static final int ALERT_PLAYER_TEAM_APPLY_ISMAX = 416;
	/** 你不是队长 */
	public static final int ALERT_PLAYER_ISNOT_LEADER = 417;
	/** 玩家没有申请加入你的队伍 */
	public static final int ALERT_PLAYER_ISNOT_APPLY = 418;
	/** 你已经有队伍，不能申请 */
	public static final int ALERT_PLAYER_TEAM_ISNOT_NULL = 419;
	/** 你已经申请过 */
	public static final int ALERT_PLAYER_APPLY_IS_EXITS = 420;
	/** 你已经结婚 */
	public static final int ALERT_PLAYER_IS_MARRYED = 421;
	/** 结婚对象不在队伍里 */
	public static final int ALERT_MARYRY_OBJECT_NOTINTEAM = 422;
	/** 同性不能结婚 */
	public static final int ALERT_PLAYER_SEX_NOT_SAME = 423;
	/** 对方已经结婚 */
	public static final int ALERT_TARGET_PLAYER_IS_MARRYED = 424;
	/** 请放入求婚戒指 */
	public static final int ALERT_PLAYER_NOT_MARRY_GOODS = 425;
	/** 求婚人没有戒指 */
	public static final int ALERT_TARGET_PLAYER_NOTMARRYGOODS = 426;
	/** 请先完成爱的体验任务 */
	public static final int ALERT_MARRY_TASK_NOT_FINISH = 427;
	/** 结婚需要的道具不够 */
	public static final int ALERT_MARRY_GOODS_NOT_ENOUGH = 428;
	/** 你没有婚姻关系 */
	public static final int ALERT_PLAYER_NOT_MARRY = 429;
	/** 结婚请求已发送 */
	public static final int ALERT_MARRY_REQUEST_SUCCESS = 430;
	/** 离婚需要的道具不够 */
	public static final int ALERT_CANCEL_GOODS_ISNULL = 431;
	/** 你有婚姻关系,不能修改性别 */
	public static final int ALERT_MARRY_NOTNULL_NOT_CHANGE_SEX = 432;
	/** 你的等级不够2000级，不能结婚 */
	public static final int ALERT_MARRY_LEVEL_ERROR = 433;
	/** 对方的等级不够2000级，不能结婚 */
	public static final int ALERT_TARGET_MARRY_LEVEL_ERROR = 434;
	/** 不能用此形象兑换 */
	public static final int ALERT_NOT_MODEL_CHANGE = 435;
	/** 求婚人没有结婚需要的道具[情比金坚]*/
	public static final int ALERT_TARGET_NOT_NEEDGOODS = 436;
	/** 请先解除婚姻关系再修改名字 */
	public static final int ALERT_MARRY_ERROR_CAHNGENAME = 437;
	/** 你不需要转换宠物 */
	public static final int ALERT_NOT_CHANGE_PET = 438;
	/** 你今天已经兑换过 */
	public static final int ALERT_CHANGED_EXP_ERROR = 439;
	/** 需要的道具不够 */
	public static final int ALERT_CHANGED_EXP_NEEDGOODS_ERROR = 440;
	/** 你没有宝宝金水道具 */
	public static final int ALERT_CHANGE_PET_NEEDGOODS_ERROR = 441;
	/** 请为了地球的和平，不要使用火星文 */
	public static final int ALERT_HUOXINGWEN = 442;
	/** 你已经领养了此守护 */
	public static final int ALERT_PET_IS_EXITS = 443;
	/** 你领养的守护数量已达最大 */
	public static final int ALERT_PET_COUNT_MAX = 444;
	/** 你有守护正在学习技能中 */
	public static final int ALERT_PET_STUDYING_SKILL = 445;
	/** 你没有正在学习技能中的守护 */
	public static final int ALERT_NOTPET_STUDYING_SKILL = 446;
	/** 你的守护今天已经回复了一次体力 */
	public static final int ALERT_PET_ISHUIFUTILI_TRUE = 447;
	/** 150点以上的时候不能恢复 */
	public static final int ALERT_PET_NOT_HUIFUTILI = 448;
	/** 没有足够的精炼宝石 */
	public static final int ALERT_NOT_JINGLIANBAOSHI = 449;
	/** 请先脱下守护身上所有装备 */
	public static final int ALERT_PET_EQUIP_MUST_TAKEOFF = 450;
	/** 没有足够的魔幻药水 */
	public static final int ALERT_NOT_FLUSHGROW_GOODS = 451;
	/** 今天的学习次数已经用完 */
	public static final int ALERT_PETSKILL_STUDYCOUNT_ERROR = 452;
	/** 守护还有更低等级的技能没有学习 */
	public static final int ALERT_PET_HAVE_LOWERSKILL_STUDY = 453;
	/** 技能正在学习中 */
	public static final int ALERT_PET_SKILL_IS_STUDYING = 454;
	/** 守护已经学过此技能 */
	public static final int ALERT_PET_SKILL_IS_STUDIED = 455;
	/** 守护职业错误 */
	public static final int ALERT_PET_JOB_IS_ERROR = 456;
	/** 没有认证 */
	public static final int ALERT_NOT_RENZHENG_GOODS = 457;
	/** 道具数量不够 */
	public static final int ALERT_NOT_GOODS_STUDYSKILL = 458;
	/** 守护的等级不够 */
	public static final int ALERT_PET_LEVEL_ERROR = 459;
	/** 你不能领养该守护 */
	public static final int ALERT_NOT_GET_PET = 460;
	/** 没有足够的精炼砂石 */
	public static final int ALERT_NOT_JINGLIANSHASHI = 461;
	/** 没有足够的炼化砂石 */
	public static final int ALERT_NOT_LIANHUASHASHI = 462;
	/** 没有足够的炼化宝石 */
	public static final int ALERT_NOT_LIANHUABAOSHI = 463;
	/** 当前守护已经很饱了，不想再吃了 */
	public static final int ALERT_PET_IS_FULL = 464;
	/** 没有快速完成卡 */
	public static final int ALERT_NOT_SKILL_FINISHI_CARD = 465;
	/** 你当前没有携带守护神 */
	public static final int ALERT_NOT_BATTLE_PET = 466;
	/** 当前守护正在学习技能中 */
	public static final int ALERT_PET_IS_STUDING_SKILL = 467;
	/** 你没有守护 */
	public static final int ALERT_PET_IS_NULL = 468;
	
	
	/** 该任务不能取消 */
	public static final int ALERT_TASK_NOT_CANCEL = 500;
	
	/** 相同姓名玩家在游戏内 */
	public static final int ALERT_LOGIN_SAMEPLAYERONLINE = 600;  
	/** 有个家伙把你挤下线了 */
	public static final int ALERT_LOGIN_OTHERPLAYERLOGIN = 601;  
	
	
	
	/** 两次密码不一致，请重新输入 */
	public static final int ALERT_STORAGE_TWO_PASSWORD_NOSAME = 700;
	/** 密码错误 */
	public static final int ALERT_STORAGE_PASSWORD_ERROR = 701;
	/** 仓库被锁住 */
	public static final int ALERT_STORAGE_IS_LOCK = 702;
	/** 密码错误三次,仓库将被锁24小时 */
	public static final int ALERT_STORAGE_PASSWORD_ERROR_THRICE = 703;
	/** 你输入的旧密码不对 */
	public static final int ALERT_STORAGE_OLD_PASSWORD_ERROR = 704;
	/** 仓库满了 */
	public static final int ALERT_STORAGE_IS_FULL = 705;
	/** 玩家要存入仓库的钱超过玩家自身的钱(游戏币) */
	public static final int ALERT_STORAGE_SAVE_POINT_OVERRUN = 706;
	/** 玩家要从仓库取的钱超过玩家仓库的钱(游戏币) */
	public static final int ALERT_STORAGE_TAKE_POINT_OVERRUN = 707;
	/** 你的仓库被锁的时间为 */
	public static final int ALERT_STORAGE_LOCKED_TIME = 708;
	
	/** "行动条未满!" */
	public static final int ALERT_ACTIVEPOINT_NOT_FULL = 709;
	/** 仓库容量已达最大 */
	public static final int ALERT_STORAGE_MAX_SIZE = 710;
	
	/** 物品不能丢弃 */
	public static final int ALERT_GOODS_NOT_DELETE = 801;
	/** 物品暂时无法使用 */
	public static final int ALERT_GOODS_CANNET_USE = 802; 
	/** 物品暂时无法取下 */
	public static final int ALERT_GOODS_CANNET_REMOVE = 803;
	/** 玩家等级太低 */
	public static final int ALERT_GOODS_LEVEL_LOW = 804;
	/** 玩家性别不符 */
	public static final int ALERT_GOODS_SEX_ERROR = 805;
	/** 此物品不存在 */
	public static final int ALERT_GOODS_NOT_EXIST = 806;
	/** 数量超出限制 */
	public static final int ALERT_GOODS_COUNT_OVERRUN = 807;
	/** 物品不能拆分 */
	public static final int ALERT_GOODS_NOT_SPLIT = 808;
	/** 物品不能叠加 */
	public static final int ALERT_GOODS_NOT_SUPERPOSE = 809;
	/** 装备不属于玩家这个职业 */
	public static final int ALERT_GOODS_JOB_ERROR = 810;
	/** 装备取下失败 */
	public static final int ALERT_GOODSEQUIP_TAKEOFF_ERROR = 811;
	/** 不能装此物品 */
	public static final int ALERT_GOODS_TYPE_ERROR = 812;
	/** 使用物品失败 */
	public static final int ALERT_GOODS_USE_ERROR = 813;
	/** 背包已满 */
	public static final int ALERT_BAG_IS_FULL = 814; 
	/** 位置错误 */
	public static final int ALERT_GOODS_LOCATION_ERROR = 815;
	/** 技能不存在 */
	public static final int ALERT_SKILL_NOT_EXIST = 816;
	/** 这个不是技能 */
	public static final int ALERT_GOODS_NOT_SKILL = 817;
	/** 职业错误 */
	public static final int ALERT_PROFESSION_ERROR = 818;
	/** 已经学习过该技能 */
	public static final int ALERT_SKILL_IS_EXIST = 819;
	/** 材料不足 */
	public static final int ALERT_GOODS_ENOUGH = 820;
	/** 绿色以下的装备不能拆解 */
	public static final int ALERT_GREENDOWN_NOT_DS = 821;
	/** 这里不能出售物品 */
	public static final int ALERT_THIS_NOT_SALE_GOODS = 822;
	/** 你还有更低等级的技能没学 */
	public static final int ALERT_SKILL_LEVEL_ERROR = 823;
	/** 不能学习该技能 */
	public static final int ALERT_NOT_STUDY_SKILL = 824;
	/** 你已经学习了更高等级的技能 */
	public static final int ALERT_HASBEEN_HIGHER_SKILL = 825;
	/** 你没有技能所需武器 */
	public static final int ALERT_NO_SKILL_NEEDARM = 826;
	/** 你还没学习这个装备所需的技能 */
	public static final int ALERT_NO_EQUIP_NEEDSKILL = 827;
	/** 战斗中不能使用此物品 */
	public static final int ALERT_NOT_USE_GOODS_INBATTLE = 828;
	/** 背包空间不足不能领取奖品 */
	public static final int ALERT_TASK_AWA_BAG_NOT_ENOUGH = 829;
	/** 组队状态不能使用传送 */
	public static final int ALERT_TEAM_STATE_USEERROR  = 830;
	/** 不能同时使用两个生命宝石或精力宝石 */
	public static final int ALERT_NOT_USE_SAME_GOODS = 831;
	/** 此操作将覆盖你已经使用的多倍经验卡 */
	public static final int ALERT_EXP_ANOTHER_START = 832;
	/** 你上一次多倍经验时间还未到 */
	public static final int ALERT_MULTEXP_TIME_NOTEND = 833;
	/** 此物品不能直接使用 */
	public static final int ALERT_GOODS_NOT_USE = 834;
	/** 玩家物品已经出售 */
	public static final int ALERT_GOODS_SALED = 835;
	/** 宠物等级太低 */
	public static final int ALERT_PET_LEVEL_LOW = 836;
	/** 你的宠物已学会改技能 */
	public static final int ALERT_PET_STUDIED_SKILL = 837;
	/** 宠物已学会更高等级的技能 */
	public static final int ALERT_PET_STUDIED_HIGHER_SKILL = 838;
	/** 宠物还有更低等级的技能没学 */
	public static final int ALERT_PET_NOT_STUDY_LOW_SKILL = 839;
	/** 同一层次的技能只能学习一个 */
	public static final int ALERT_PET_SKILL_NO_STUDY = 840;
	/** 行动值已满或者加超了 */
	public static final int ALERT_FLYACTIVEPOINT_OVER = 841;
	/** 活跃度已满或者加超了 */
	public static final int ALERT_PETACTIVEPOINT_OVER = 842;
	/** 你已经使用了挂机经验卡 */
	public static final int ALERT_ONLINE_EXP_EXITS = 843;
	/** 目标物品不存在 */
	public static final int ALERT_TARGET_GOODS_ISNULL = 844;
	/** 物品不能丢弃 */
	public static final int ALERT_GOODS_ISNOT_DEL = 845;
	/** 不能在当前房间使用 */
	public static final int ALERT_NOT_USE_IN_HERE = 846;
	/** 序列号错误或过期 */
	public static final int ALERT_FACEBOOK_INDEX_ERROR = 847;
	
	
	/** 你的金钱不够 */
	public static final int ALERT_SHOP_NO_POINT = 901;
	/** 你的荣誉值不够 */
	public static final int ALERT_SHOP_NO_HONOUR = 902;
	/** 物品不能出售 */
	public static final int ALERT_SHOP_NOT_SALE = 904;
	/** 物品已出售完 */
	public static final int ALERT_SHOP_GOODS_NOT_HAVE = 905;
	/** 请到恶人村药店买虚伪散消除你的红名 */
	public static final int ALERT_PLAYER_ERR = 906;
	/** 你的元宝不足 */
	public static final int ALERT_SHOP_NO_MONEY = 907;
	/** 该功能正在完善 */
	public static final int ALERT_FUNCTION_NO_OPEN = 908;
	
	/** "邮件已满，未免造成损失请及时清理！" */
	public static final int ALERT_MAILBOX_FULL_CLEARSOON = 909;
	
	/** "拆解失败" */
	public static final int ALERT_DS_PROCESS_FAIL = 910;
	
	/** 修改邮件失败! */
	public static final int ALERT_MAIL_ALTER_FAIL = 911;
	
	/** "修改密码失败!" */
	public static final int ALERT_PWD_ALTER_FAIL = 912;
	
	/** 该用户已被封号 */
	public static final int ALERT_ACCOUNT_CLOSED = 913;
	
	/** 账号密码错误，登陆失败 */
	public static final int ALERT_LOGIN_FAIL = 914;
	
	/** 对不起，该装备不能进行拆解。 */
	public static final int ALERT_DS_GOODS_NOT = 915;
	
	/** 封号先T玩家下线 */
	public static final int ALERT_ACCOUNT_CLOSE_BEFORE = 916;
	
	/** 现在没有活动举行!*/
	public static final int ALERT_PARTY_CLOSING = 917;
	
	/** 家族胜利方冷却10秒 */
	public static final int ALERT_PARTY_CDTIMING = 918;
	
	/** "该邮件不能删除" */
	public static final int ALERT_MAIL_UNDEL = 919;
	
	/** 此物品只能在商城出售 */
	public static final int ALERT_GOODS_NOSALE_THERE = 920;
	/** 对不起,你没有相同价值的礼券 */
	public static final int ALERT_SHOP_NO_LIQUAN = 921;
	/** 物品不能在此出售 */
	public static final int ALERT_GOODS_NO_SALE_THERE = 922;
	/** 非活动时间不能进入此房间 */
	public static final int ALERT_NOT_PARTY_TIME = 923;
	/** 你的代金不足 */
	public static final int ALERT_SHOP_NO_TOKEN = 924;
	/** 你的行动值不足 */
	public static final int ALERT_NO_FLYACTIVEPOINT = 925;

}
