package cc.lv1.rpg.gs.data;

public class ConstantData
{
//	className:GameServer.java-------------------------
	public static final int OVERTIME = 3000;
	public static final int JOB_DATABASEJOB = 0;
	public static final int JOB_GAME1 = 1;
	public static final int JOB_GAME2 = 2;
	public static final int JOB_COUNT = 3;

//	className:DataFactory.java-------------------------
	public static final int INITROOM = 1013010111;
	public static final int NOVICEAREA = 1012010100;
	public static final int KAITUOROOM = 1013060101;
	public static final int XIESHENROOM = 1013060102;
	public static final int PARTYPKROOM = 1013060103;
	public static final int MARRYROOM = 1013210101;
	public static final int HONORROOM = 1013430101;

//	className:ShopCenter.java-------------------------
	public static final int JOBSUM = 25;

//	className:MarryInvitation.java-------------------------
	public static final int MANGOODS = 1;
	public static final int WOMENGOODS = 2;
	public static final int VIPMANGOODS = 3;
	public static final int VIPWOMENGOODS = 4;
	public static final int JIEHUNZHENG = 5;

//	className:CampPartyController.java-------------------------
	public static final int CAMPPARTY_MAXLOSECOUNT = 99999999;
	public static final int CAMPPARTY_WINHONOUR = 50;
	public static final int CAMPPARTY_LOSEHONOUR = 5;
	public static final int CAMPPARTY_LEVEL = 200;

//	className:FamilyController.java-------------------------
	public static final int FAMILY_MAX_MEMBERS = 50;
	public static final int FAMILY_POINT_CONDITION= 100000;
	public static final int LEVEL = 200;
	public static final int CHANGE_LEADER_TIME = 12 * 60 * 60 * 1000;

//	className:PartyController.java-------------------------
	public static final int HONORPROP = 1045000144;
	public static final int REWARDRANK = 20;

//	className:BattleController.java-------------------------
	public static final int TEAM1 = 1;
	public static final int TEAM2 = 2;
	public static final int SKILLPROCESSOR = 1;
	public static final int PROPPROCESSOR = 2;
	public static final int RUNAWAY = 3;
	public static final int PETSKILLPROCESSOR = 4;
	public static final int RESETMONEY = 10;
	public static final int AUTOEXITTIME = 1000 * 12;
	public static final int RESETMAILGOODS = 1045000134;

//	className:TeamController.java-------------------------
	public static final int CLEAR_ALL = 0;
	public static final int CLEAR_ONE = 1;

//	className:PartyPKController.java-------------------------
	public static final int LOSESTATEGOODS = 1045000142;
	public static final int WINSTATEGOODS = 1045000251;
	public static final int PARTYEXP = 8000000;

//	className:FamilyPartyController.java-------------------------
	public static final int MAXLOSECOUNT = 99999999;
	public static final int WINHONOUR = 50;
	public static final int LOSEHONOUR = 10;

//	className:PlayerController.java-------------------------
	public static final int EXPMAXLEVEL = 7000;
	public static final int SAFETIME = 3 * 60 * 60 * 1000;
	public static final int UNSAFETIME = 5 * 60 * 60 * 1000;
	public static final int IDCARDGOODS = 1045013010;
	public static final int IDCARDGOODSCOUNT = 1;
	public static final int CAMPMONEY = 500;
	public static final int MONEYBOX = 20;
	public static final int ESCTIME = 1000*60*5;
	public static final int RANDOMVALUE = 10;
	public static final int LEADEREXP = 50;
	public static final int UPJOBLEVEL = 200;
	public static final int UPCAMPLEVEL = 200;
	public static final int MARRYLEVEL = 2000;
	public static final int TOTALCENTERGOODSCOUNT = 5;

//	className:GoldPartyController.java-------------------------
	public static final int SHOWIMAGEROOM = 1013430101;
	public static final int STATEGOODS = 1045000251;
	public static final int RESETTIME = 7 * 1000;
	public static final int RETURNTIME = 20 * 1000;
	public static final int PARTYINITROOM = 1013010111;
	public static final int GOLDREWARDRANK = 50;
	public static final int SHOWIMAGERANK = 5;
	public static final int BATTLECOUNT = 90;

//	className:AnswerParty.java-------------------------
	public static final int CHANGEEXPLEVEL = 1000;
	public static final int FREE_ANSWER = 0;
	public static final int POINT_ANSWER = 1;
	public static final int MONEY_ANSWER = 2;
	public static final int CONTINUE = 3;
	public static final int NOANSWER = 4;
	public static final int NOANSWERTIME = 5;
	public static final int ANSWER_OVER_TIME = 22;
	public static final int REWARDTIME = 23;
	public static final int ROUND = 36;//国庆时改成36
	public static final int TYPEONE = 3;//国庆时改
	public static final int TYPETWO = 16;//国庆时改
	public static final int TYPETHREE = 17;//国庆时改
	public static final int NEEDPOINT = 10000;
	public static final int NEEDMONEY = 10;
	public static final int NEEDNEXTPOINT = 100000;

//	className:FriendList.java-------------------------
	public static final int ADD_FRIEND = 0x01;
	public static final int REMOVE_FRIEND = 0x02;
	public static final int ADD_BLACK = 0x03;
	public static final int REMOVE_BLACK =0x04;
	public static final int LIST = 0x05;

//	className:PetTome.java-------------------------
	public static final int BATTLEPETMAXCOUNT = 6;

//	className:Storage.java-------------------------
	public static final int PAGE_MONEY = 10;
	public static final int MAX_SIZE = 4 * 54;

//	className:MailBox.java-------------------------
	public static final int QUERY = 4;
	public static final int QUERYMAIL = 5;
	public static final int ADD = 1;
	public static final int REMOVE = 2;
	public static final int GETATTACH = 3;
	public static final int MAX_MAILBOX_SIZE = 100;

//	className:Bag.java-------------------------
	public static final int BOSSBOXNUM = 5;
	public static final int[] bossMoney = {0,0,0,0};

//	className:TaskInfo.java-------------------------
	public static final int MAX_TASK_COUNT = 5;

//	className:Task.java-------------------------
	public static final int ADD_TASK = 0x01;
	public static final int UPDATE_TASK = 0x02;

//	className:Monster.java-------------------------
	public static final int MONSTERSKILLTYPE = -1;

//	className:Role.java-------------------------
	public static final int OVERLEVEL = 6000;
	public static final int STATE_LEISURE = 0; //空闲
	public static final int STATE_BUSY = 1; //繁忙
	public static final int STATE_TEAMLEADER = 2; //带队
	public static final int STATE_TEAMMEMBER = 4; //队员
	public static final int STATE_FIGHTING = 8; //战斗
	public static final int STATE_TRADING = 16; //贸易
	public static final int STATE_PERSONALSHOP = 32; //交易
	public static final int STATE_UPGRADEPACK = 64; //更新

//	className:Goods.java-------------------------
	public static final int COMMONSHOPBIND = 0;//永远不绑定
	public static final int MONEYSHOPBIND = 2;//拾取绑定
	public static final int BOXBINDMODE = 2;//拾取绑定
	public static final int NOTICE_QUALITY = 3;
	public static final int CHANGENAMECARD = 25;
	public static final int AUTOBATTLECARD = 26;
	public static final int EQUIPUPOTHER = 31;
	public static final int MONEYBOXOTHER = 32;
	public static final int BATTLEPETSKILLENDGOODS = 38;
	public static final int TIMESKYTICKET = 1045000291;

//	className:GoldBox.java-------------------------
	public static final int MAXCOUNT = 10;
	public static final int GOLDLEVEL = 600;
	public static final int[] notAwardGoods = {1045010101,1045010102};

//	className:MonsterBattleTmp.java-------------------------
	public static final int DEFAULTSKILL = 1031000001;

//	className:Effect.java-------------------------
	public static final int EFFECT_ADD = 0x01;
	public static final int EFFECT_SUB = 0X02;

//	className:FlashEffect.java-------------------------
	public static final int FE_TYPE_ATTACK = 1;
	public static final int FE_TYPE_CURE = 2;
	public static final int FE_TYPE_DAMAGE = 3;
	public static final int FE_TYPE_WISDOM_DAMAGE = 4;
	public static final int FE_TYPE_SPIRIT_DAMAGE= 5;
	public static final int FE_TYPE_DRUG_RELIVE = 6;
	public static final int FE_TYPE_ATTACK_DAMAGE = 7;
	public static final int FE_TYPE_SPIRIT_CURE = 8;
	public static final int FE_TYPE_CLEAR_CDTIME = 9;
	public static final int FE_TYPE_CANCEL_NEGBUFF = 10;
	public static final int FE_TYPE_NOSMITE_DAMAGE = 11;

//	className:ActiveSkill.java-------------------------
	public static final int TARGET_TYPE_TEAM_SELF = 1;
	public static final int TARGET_TYPE_TEAM_ENEMY = 2;
	public static final int TARGET_TYPE_ONESELF = 3;
	public static final int ERROR = -1;
	public static final int EFFECT_RANGE_ONE = 1;

//	className:Skill.java-------------------------
	public static final int TARGET_TYPE_PLAYERS = 1;
	public static final int TARGET_TYPE_MONSTERS = 2;

//	className:PetSkill.java-------------------------
	public static final int MAXSTUDYCOUNT = 12;
	public static final int MONEYCOUNT = 6;
	public static final int STUDYMOENY = 2;

//	className:PetBattleTmp.java-------------------------
	public static final int CDTIMER = 7000;

//	className:GoodsPetEquip.java-------------------------
	public static final int LIANHUABAOSHICOUNT = 50;
	public static final int LIANHUASHASHICOUNT = 1;

//	className:Pet.java-------------------------
	public static final int FIRST_GROW = 100;
	public static final int SECOND_GROW = 150;
	public static final int OVERMONEY = 1;
	public static final int PUTONGPET = 1;
	public static final int SHOUHUPET = 2;

//	className:Player.java-------------------------
	public static final int LIFERATE = 10;
	public static final int MAXCOPYPOINT = 100;

//	className:Shop.java-------------------------
	public static final int TYPE_MONEY_SHOP = 1000000000;
	public static final int TYPE_VIP_MONEY_SHOP = 1000000001;
	public static final int REBATE = 50;

//	className:MainFrame.java-------------------------
	public static final int WIDTH = 600;
	public static final int HEIGHT = 400;
	public static final int X = 150;
	public static final int Y = 100;

//	className:PayJob.java-------------------------
	public static final int MONEY = 1;
	public static final int GOODS = 2;

//	className:ValidateJob.java-------------------------
	public static final int EXCEPT_PLAYER = -1;
	public static final int NEW_PLAYER = 1;
	public static final int OLD_PLAYER = 2;
	public static final int CLOSE_PLAYER = 3;
}
