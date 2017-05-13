package cc.lv1.rpg.gs.data;

import java.util.Random;

import vin.rabbit.comm.GameObject;
import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.i.Map;
import vin.rabbit.util.collection.impl.ArrayList;
import vin.rabbit.util.collection.impl.HashMap;
import vin.rabbit.util.xml.XMLNode;
import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.entity.controller.CopyController;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.ext.Bag;
import cc.lv1.rpg.gs.entity.ext.PetTome;
import cc.lv1.rpg.gs.entity.ext.SkillTome;
//import cc.lv1.rpg.gs.entity.impl.GoldBox;
import cc.lv1.rpg.gs.entity.impl.BoxDropProp;
import cc.lv1.rpg.gs.entity.impl.GoldBox;
import cc.lv1.rpg.gs.entity.impl.GoldParty;
import cc.lv1.rpg.gs.entity.impl.Goods;
import cc.lv1.rpg.gs.entity.impl.LogTemp;
import cc.lv1.rpg.gs.entity.impl.MoneyBattle;
import cc.lv1.rpg.gs.entity.impl.Player;
import cc.lv1.rpg.gs.entity.impl.RankReward;
import cc.lv1.rpg.gs.entity.impl.UpRole;
import cc.lv1.rpg.gs.entity.impl.answer.AnswerReward;
import cc.lv1.rpg.gs.entity.impl.answer.Guide;
import cc.lv1.rpg.gs.entity.impl.answer.Question;
import cc.lv1.rpg.gs.entity.impl.battle.skill.PetSkill;
import cc.lv1.rpg.gs.entity.impl.battle.skill.Skill;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsEquip;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsMarry;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsNotice;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsPetEquip;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsProp;
import cc.lv1.rpg.gs.entity.impl.pet.Pet;
import cc.lv1.rpg.gs.entity.impl.pet.PetSkillStudy;
import cc.lv1.rpg.gs.entity.impl.pet.PetUpRule;
import cc.lv1.rpg.gs.entity.impl.pet.PetWords;
import cc.lv1.rpg.gs.other.ErrorCode;
/**
 * 数据工厂.....
 * @author dxw
 *
 */
 
public class DataFactory
{
	/** 默认回城点 */
	public static final int INITROOM = 1013010111;
	/** 主线剧情根据地 */
	public static final int STORYROOM = 1013510101;
	/** 新手区域 */
	public static final int NOVICEAREA = 1012010100;
	/** 开拓阵营活动休息房间 */
	public static final int KAITUOROOM = 1013060101;
	/** 邪神阵营活动休息房间 */
	public static final int XIESHENROOM = 1013060102;
	/** 活动PK房间 */
	public static final int PARTYPKROOM = 1013060103;
	/** 奇妙广场 */
	public static final int MARRYROOM = 1013210101;
	/** 荣誉圣殿 */
	public static final int HONORROOM = 1013430101;
	/** 天空之城 */
	public static final int SKYROOM = 1013510101;
	/** 冲关主城镇 */
	public static final int CHONGGUANROOM = INITROOM;
	/** 商城淘宝版本
	 * new:新版,商城只扣代金,淘宝只扣元宝,淘宝扣除的是money,商城扣除的是token
	 * old:旧版,商城代金不够就扣元宝*/
	public static String shopVer = "new";
	/** 是否要开沉迷系统 */

	public static boolean isIdcard = false;

	/** 战斗加密 */ 
	public static boolean isBattleCode = true;
	/** 行动值新版(true:新版 false:旧版 新版有三种行动值,旧版就只有一种) */
	public static boolean isActivePoint = true;
	/** 简体*/
	public static final String SIMPLE = "simple";
	/** 繁体 */
	public static final String COMPLEX = "complex";
	/** 英文 */
	public static final String ENGLISH = "english";
	/** 服务器的字体版本 */
	public static String fontVer = SIMPLE;
	
	private static DataFactory df = null;
	
	private static HashMap datasMap = new HashMap(1000);
	
	private static HashMap goodsMap = new HashMap(1000);
	
	private static HashMap storyMap = new HashMap(500);
	
	public static int answerNpc;

	private DataFactory()
	{
	}
	
	public static DataFactory getInstance()
	{
		if(df == null)
			df = new DataFactory();
		return df;
	}
	
	public HashMap getDatas()
	{
		return datasMap;
	}
	
	public GameObject getGameObject(int id)
	{
		return (GameObject)datasMap.get(id);
	}

	public void putGameObject(GameObject obj)
	{
		datasMap.put(obj.id, obj);
		if(obj instanceof Goods)
			goodsMap.put(obj.name, obj);
	}
	
	public Goods getGoodsByName(String name)
	{
		return (Goods) goodsMap.get(name);
	}
	
	public void setRoomObject(GameObject obj)
	{
		datasMap.remove(obj);
		datasMap.put(obj.id, obj);
	}
	
	public void putStoryObject(GameObject obj)
	{
		storyMap.put(obj.id, obj);
	}
	
	public GameObject getStoryObject(int id)
	{
		return (GameObject) storyMap.get(id);
	}
	
	public static HashMap fontMap = new HashMap(500);
	public void addFont(int key,String value)
	{
		fontMap.put(key, value);
	}
	public String getFont(int key)
	{
		return (String) fontMap.get(key);
	}
	
	/** 临时序列号 */
	public final static int INDEX_LIST = 0;

	/** 玩家出生 */
	public final static int ATTACH_NATIVITY = 1;
	
	/** 玩家升级经验计算 */
	public final static int ATTACH_EXP = 2;
	
	/** 宠物升级经验计算 */
	public final static int ATTACH_PET_EXP = 3;
	
	/** NPC传送配置 */
	public final static int ATTACH_NPC_FLYER = 4;
	
	/** 邮件提醒 */
	public final static int ATTACH_MAILREMIND = 5;
	
	/** GM命令使用人员列表 */
	public final static int GMPROCESS_LIST = 6;
	
	/** 宠物出行规则 */
	public final static int PET_TRAIN_LIST = 7;
	
	/** 宠物语言 */
	public final static int PET_WORDS_LIST = 8;
	
	/** 公告显示的物品列表 */
	public final static int NOTICE_LIST = 9;
	
	/** 副本列表 */
	public final static int COPY_LIST = 10;
	
	/** 奖励列表 */
	public final static int REWARDSENDER_LIST = 11;
	
	/** 结婚规则 */
	public final static int MARRY_ROLE = 12;
	
	/** 转生规则 */
	public final static int UP_ROLE = 13;
	
	/** 房间进入的时间限制 */
	public final static int TIME_CONDITION = 14;
	
	/** 新手泡跑答题 */
	public final static int GUIDE_ANSWER = 15;
	
	/** 普通抽奖(炼金术)规则 */
	public final static int GOLD_BOX = 16;
	
	/** 简体字库 */
	public final static int SIMPLE_FONT = 17;
	
	/** 繁体字库 */
	public final static int COMPLEX_FONT = 18;
	
	/** 黄金斗士规则 */
	public final static int GOLD_PARTY = 19;
	
	/** 夺宝奇兵 */
	public final static int MONEY_BATTLE = 20;
	
	/** 宠物技学习技能规则 */
	public final static int PET_SKILL_STUDY = 21;
	
	/** 守护经验规则 */
	public final static int BATTLE_PET_EXP = 22;
	
	/** 守护领养规则 */
	public final static int GET_PET_RULE = 23;
	
	/** 剧情列表 */
	public final static int STORY_LIST = 24;
	
	/** 事件列表 */
	public final static int EVENT_LIST = 25;
	
	/** 事件组 */
	public final static int EVENT_TEAM = 26;
	
	private static HashMap attachMap = new HashMap(500);
	
	public void putAttachment(int attachType,Object obj)
	{
		attachMap.put(attachType, obj);
	}
	
	public Object getAttachment(int attachType)
	{
		return attachMap.get(attachType);
	}
	
	public PetSkillStudy getPetSkillStudyBySkill(PetSkill skill)
	{
		List list = (List) DataFactory.getInstance().getAttachment(DataFactory.PET_SKILL_STUDY);
		if(list == null)
			return null;
		for (int i = 0; i < list.size(); i++) 
		{
			PetSkillStudy pss = (PetSkillStudy) list.get(i);
			if(skill.skillType == pss.skillType && skill.level == pss.skillLevel)
				return pss;
		}
		return null;
	}
	
	public MoneyBattle getMoneyBattle(int roomId)
	{
		Map map = (Map) DataFactory.getInstance().getAttachment(DataFactory.MONEY_BATTLE);
		if(map == null)
			return null;
		return (MoneyBattle) map.get(roomId);
	}
	
	public GoldParty getGoldPartyByLevel(int level)
	{
		List list = (List) DataFactory.getInstance().getAttachment(GOLD_PARTY);
		if(list == null)
			return null;
		for (int i = 0; i < list.size(); i++)
		{
			GoldParty gp = (GoldParty) list.get(i);
			if(gp.level == level)
				return gp;
		}
		return null;
	}
	
	public GoldParty getGoldPartyByRoom(int roomId)
	{
		List list = (List) DataFactory.getInstance().getAttachment(GOLD_PARTY);
		if(list == null)
			return null;
		for (int i = 0; i < list.size(); i++)
		{
			GoldParty gp = (GoldParty) list.get(i);
			if(gp.roomIds == null)
				continue;
			for (int j = 0; j < gp.roomIds.length; j++)
			{
				if(gp.roomIds[j] == roomId)
					return gp;
			}
		}
		return null;
	}
	
	public GoldBox getAwardType()
	{
		List list = (List)DataFactory.getInstance().getAttachment(GOLD_BOX);
		if(list == null)
			return null;
		int rate = 0;
		int cr = (int) (Math.random() * 10000) + 1;
		for (int i = 0; i < list.size(); i++) 
		{
			GoldBox at = (GoldBox) list.get(i);
			rate += at.rate;
			if(cr <= rate)
			{
				return at;
			}
		}
		return null;
	}
	
	public Guide getGuideByLevel(int step)
	{
		List list = (List)DataFactory.getInstance().getAttachment(GUIDE_ANSWER);
		if(list == null)
			return null;
		for (int i = 0; i < list.size(); i++)
		{
			Guide guide = (Guide) list.get(i);
			if(guide.step == step)
				return guide;
		}
		return null;
	}
	
	public Guide getGuideByQuestion(int questionId)
	{
		List list = (List)DataFactory.getInstance().getAttachment(GUIDE_ANSWER);
		if(list == null)
			return null;
		for (int i = 0; i < list.size(); i++)
		{
			Guide guide = (Guide) list.get(i);
			if(guide.questionId == questionId)
				return guide;
		}
		return null;
	}
	
	public Guide getGuideById(int id)
	{
		List list = (List)DataFactory.getInstance().getAttachment(GUIDE_ANSWER);
		if(list == null)
			return null;
		for (int i = 0; i < list.size(); i++)
		{
			Guide guide = (Guide) list.get(i);
			if(guide.id == id)
				return guide;
		}
		return null;
	}
	
	public UpRole getUpRoleByPlayer(int type,int job)
	{
		if(type == 0)
			return null;
		List list = (List) getAttachment(UP_ROLE);
		for (int i = 0; i < list.size(); i++) 
		{
			UpRole ur = (UpRole)list.get(i);
			if(ur.type == type && (job+"").endsWith(ur.job+""))
				return ur;
		}
		return null;
	}
	
	public GoodsMarry getGoodsMarryByGoodsId(int goodsId)
	{
		List list = (List) getAttachment(MARRY_ROLE);
		for (int i = 0; i < list.size(); i++) 
		{
			GoodsMarry gm = (GoodsMarry)list.get(i);
			if(gm.goodsId == goodsId)
				return gm;
		}
		return null;
	}
	
	public CopyController getCopyByStep(int type,int step)
	{
		List list = (List) getAttachment(COPY_LIST);
		for (int i = 0; i < list.size(); i++) 
		{
			CopyController cpc = (CopyController) list.get(i);
			if(cpc.step == step && cpc.type == type)
				return cpc;
		}
		return null;
	}
	
	public CopyController getCopyByLevel(int type,int level)
	{
		int step = 0;
		if(level >= 1000 && level <= 1999)
			step = 1;
		else if(level >= 2000 && level <= 2999)
			step = 2;
		else if(level >= 3000 && level <= 3999)
			step = 3;
		else if(level >= 4000 && level <= 4999)
			step = 4;
		else if(level >= 5000 && level <= 5999)
			step = 5;
		else if(level >= 6000 && level <= 8500)
			step = 6;
		List list = (List) getAttachment(COPY_LIST);
		for (int i = 0; i < list.size(); i++) 
		{
			CopyController cpc = (CopyController) list.get(i);
			if(cpc.step == step && cpc.type == type)
				return cpc;
		}
		return null;
	}
	
	public CopyController getCopyByArea(int areaId)
	{
		List list = (List) getAttachment(COPY_LIST);
		for (int i = 0; i < list.size(); i++) 
		{
			CopyController cpc = (CopyController) list.get(i);
			if(cpc.areaId == areaId)
				return cpc;
		}
		return null;
	}
	
	public CopyController getCopyByNum(int num)
	{
		List list = (List) getAttachment(COPY_LIST);
		for (int i = 0; i < list.size(); i++) 
		{
			CopyController cpc = (CopyController) list.get(i);
			if(i == num)
				return cpc;
		}
		return null;
	}
	
	public CopyController getCopyById(int id)
	{
		List list = (List) getAttachment(COPY_LIST);
		for (int i = 0; i < list.size(); i++) 
		{
			CopyController cpc = (CopyController) list.get(i);
			if(cpc.id == id)
				return cpc;
		}
		return null;
	}
	
	public int getCopyNum(int areaId)
	{
		List list = (List) getAttachment(COPY_LIST);
		for (int i = 0; i < list.size(); i++) 
		{
			CopyController cpc = (CopyController) list.get(i);
			if(cpc.areaId == areaId)
				return i;
		}
		return -1;
	}
	
	private List skillList = new ArrayList(500);
	
	public void addSkill(Skill skill)
	{
		skillList.add(skill);
	}
	
	public List getSkillList()
	{
		return skillList;
	}
	
	private List petSkillList = new ArrayList(100);
	
	public void addPetSkill(Skill skill)
	{
		petSkillList.add(skill);
	}
	
	public List getPetSkillList()
	{
		return petSkillList;
	}

	
	public int getPetSkillMaxLevel(PetSkill skill)
	{
		int maxLevel = 0;
		List list = new ArrayList();
		for (int i = 0; i < petSkillList.size(); i++) 
		{
			PetSkill petSkill = (PetSkill) petSkillList.get(i);
			if(petSkill.iconId == skill.iconId)
			{
				list.add(petSkill);
			}	
		}
		for (int i = 0; i < list.size()-1; i++) 
		{
			PetSkill petSkill = (PetSkill) list.get(i);
			if(maxLevel < petSkill.level)
				maxLevel = petSkill.level;
		}
		return maxLevel;
	}
	
	private List boxDropPropList = new ArrayList();
	
	public void addBoxDropProp(BoxDropProp bdp)
	{
		boxDropPropList.add(bdp);
	}
	
	public List getBoxDropPropList()
	{
		return boxDropPropList;
	}
	
	public BoxDropProp getBDP(int id)
	{
		for (int i = 0; i < boxDropPropList.size(); i++) 
		{
			BoxDropProp bdp = (BoxDropProp) boxDropPropList.get(i);
			if(bdp == null)
				continue;
			if(bdp.id == id)
				return bdp;
		}
		return null;
	}
	
	/** 类型为1的题 */
	public final static int TYPE_ONE_QUESTION = 1;
	/** 类型为2的题 */
	public final static int TYPE_TWO_QUESTION = 2;
	/** 类型为3的题 */
	public final static int TYPE_THREE_QUESTION = 3;
	/** 类型为4的题 */
	public final static int TYPE_FOUR_QUESTION = 4;
	/** 类型为5的题 */
	public final static int TYPE_FIVE_QUESTION = 5;
	
	public HashMap questionMap = new HashMap();
	public void addAnswerObject(int type,Object obj)
	{
		questionMap.put(type, obj);
	}
	
	public Object getAnswerObject(int type)
	{
		return questionMap.get(type);
	}
	
	public Question getQuestionFromGuide(int id)
	{
		List questionList = (List) DataFactory.getInstance().getAnswerObject(DataFactory.TYPE_FOUR_QUESTION);
	    for (int i = 0; i < questionList.size(); i++)
	    {
	    	Question q = (Question) questionList.get(i);
			if(q.id == id)
				return q;
		}
	    return null;
	}
	
	public List questions = new ArrayList();
	public void addQuestion(Question question)
	{
		questions.add(question);
	}
	public List getQuestionList()
	{
		return questions;
	}
	public Question getQuestion(int id)
	{
		for (int i = 0; i < questions.size(); i++) 
		{
			Question q = (Question) questions.get(i);
			if(q.id == id)
				return q;
		}
		return null;
	}
	
	public List answerRewardList = new ArrayList();
	public void addARList(AnswerReward ar)
	{
		answerRewardList.add(ar);
	}
	public List getAnswerRewardList()
	{
		return answerRewardList;
	}
	public AnswerReward getAnswerReward(int level)
	{
		if(level > PlayerController.EXPMAXLEVEL)
			level = PlayerController.EXPMAXLEVEL;
		for (int i = 0; i < answerRewardList.size(); i++) 
		{
			AnswerReward ar = (AnswerReward) answerRewardList.get(i);
			if(level >= ar.minLevel && level <= ar.maxLevel)
			{
				return ar;
			}
		}
		return null;
	}
	public AnswerReward getAnswerReward(int min,int max)
	{
		for (int i = 0; i < answerRewardList.size(); i++) 
		{
			AnswerReward ar = (AnswerReward) answerRewardList.get(i);
			if(min == ar.minLevel && max == ar.maxLevel)
			{
				return ar;
			}
		}
		return null;
	}
	
	public List rankRewards = new ArrayList(13);
	public void addRankRewards(RankReward rr)
	{
		rankRewards.add(rr);
	}
	public List getRankRewards()
	{
		return rankRewards;
	}
	public RankReward getRankReward(int type,int rank)
	{
		for (int i = 0; i < rankRewards.size(); i++)
		{
			RankReward rp = (RankReward) rankRewards.get(i);
			if(rp.type == type && rp.rank == rank)
				return rp;
		}
		return null;
	}
	
	
	public Goods[] makeGoods(int goodsId,int count)
	{
		return makeGoods(goodsId,count,0);
	}
	
	public Goods[] makeGoods(int goodsId)
	{
		Object obj = DataFactory.getInstance().getGameObject(goodsId);
		if(!(obj instanceof Goods) || obj == null)
		{
			System.out.println("DataFactory error no count id:"+goodsId);
			return null;
		}
		Goods goods = (Goods) obj;
		if(goods instanceof GoodsEquip)
		{
			return makeGoods(goodsId,1,((GoodsEquip) goods).taskColor);
		}
		else
		{
			return makeGoods(goodsId,1,0);
		}
	}
	
	public Goods[] makeGoods(int goodsId,int count,int quality)
	{
		Object obj = DataFactory.getInstance().getGameObject(goodsId);
		if(!(obj instanceof Goods) || obj == null)
		{
			System.out.println("DataFactory makeGoods error id:"+goodsId);
			return null;
		}
		Goods[] goodsList = null;
		Goods goods = (Goods) obj;
		Goods newGoods = null;
		if(goods instanceof GoodsEquip)
		{
			goodsList = new Goods[count];
			for (int i = 0; i < count; i++)
			{
				newGoods = ((GoodsEquip)goods).makeNewBetterEquip(quality);
				newGoods.objectIndex = GameServer.getInstance().getDatabaseAccessor().getGoodsObjIndex();
				goodsList[i] = newGoods;
			}
		}
		else if(goods instanceof GoodsPetEquip)
		{
			goodsList = new Goods[count];
			for (int i = 0; i < count; i++)
			{
				newGoods = (Goods) Goods.cloneObject(goods);
				newGoods.objectIndex = GameServer.getInstance().getDatabaseAccessor().getGoodsObjIndex();
				goodsList[i] = newGoods;
			}
		}
		else if(goods instanceof GoodsProp)
		{
			newGoods = (Goods) Goods.cloneObject(goods);
			newGoods.objectIndex = GameServer.getInstance().getDatabaseAccessor().getGoodsObjIndex();
			int number = 0;
			if(count > newGoods.repeatNumber)
			{
				number = count / newGoods.repeatNumber;
				int moreCount = count - (number * newGoods.repeatNumber);
				if(moreCount > 0)
				{
					goodsList = new Goods[number+1];
					for (int i = 0; i < goodsList.length; i++)
					{
						newGoods = (Goods) Goods.cloneObject(goods);
						newGoods.objectIndex = GameServer.getInstance().getDatabaseAccessor().getGoodsObjIndex();
						if(i == number)
							newGoods.goodsCount = moreCount;
						else
							newGoods.goodsCount = newGoods.repeatNumber;
						goodsList[i] = newGoods;
					}
				}
				else if(moreCount == 0)
				{
					goodsList = new Goods[number];
					for (int i = 0; i < number; i++)
					{
						newGoods = (Goods) Goods.cloneObject(goods);
						newGoods.objectIndex = GameServer.getInstance().getDatabaseAccessor().getGoodsObjIndex();
						newGoods.goodsCount = newGoods.repeatNumber;
						goodsList[i] = newGoods;
					}
				}
			}
			else
			{
				goodsList = new Goods[1];
				newGoods = (Goods) Goods.cloneObject(goods);
				newGoods.objectIndex = GameServer.getInstance().getDatabaseAccessor().getGoodsObjIndex();
				newGoods.goodsCount = count;
				goodsList[0] = newGoods;
			}
		}
		return goodsList;
	}
	
	public String getPetWords(int type)
	{
		int t = type + 6;
		List words = new ArrayList();
		List list = (List) getAttachment(PET_WORDS_LIST);
		for (int i = 0; i < list.size(); i++) 
		{
			PetWords pw = (PetWords) list.get(i);
			if(pw == null)
				continue;
			if(pw.type == t)
				words.add(pw.words);
		}
		int random = (int) (Math.random() * words.size());
		return words.get(random).toString();
	}

	

	
	/**
	 * 设置出生点
	 * @param player
	 * @return
	 */
	public boolean setNativity(Player player)
	{
		ArrayList nativitys = (ArrayList)attachMap.get(ATTACH_NATIVITY);
		Random random = new Random();
		XMLNode points = (XMLNode) nativitys.get(0);
		ArrayList pointss = points.getSubNodes();
		int index = random.nextInt(pointss.size());
		XMLNode point = (XMLNode) pointss.get(index);
		if(point == null)
			return false;
		player.worldId = Integer.parseInt(point.getAttributeValue("worldId"));
		player.areaId= Integer.parseInt(point.getAttributeValue("areaId"));
		player.roomId= Integer.parseInt(point.getAttributeValue("roomId"));
		player.x = Integer.parseInt(point.getAttributeValue("x"));
		player.y = Integer.parseInt(point.getAttributeValue("y"));

		int nativitySize = nativitys.size();
		
		for(int i = 0 ; i < nativitySize ; i ++)
		{
			XMLNode anyNode = (XMLNode)nativitys.get(i);
			
			if(anyNode.getName().equals("points"))
			{
				ArrayList pointList = anyNode.getSubNodes();
				Random ran = new Random();
				int indexx = ran.nextInt(pointList.size());
				XMLNode pointO = (XMLNode)pointList.get(indexx);
				
				if(pointO != null)
				{
					player.worldId = Integer.parseInt(pointO.getAttributeValue("worldId"));
					player.areaId= Integer.parseInt(pointO.getAttributeValue("areaId"));
					player.roomId= Integer.parseInt(pointO.getAttributeValue("roomId"));
					player.x = Integer.parseInt(pointO.getAttributeValue("x"));
					player.y = Integer.parseInt(pointO.getAttributeValue("y"));
				}
				
//				System.out.println("创建角色世界Id:"+player.worldId);
//				System.out.println("创建区域世界Id:"+player.areaId);
//				System.out.println("创建房间世界Id:"+player.roomId);
//				System.out.println("创建玩家出生点-- x:"+player.x+" y:"+player.y);
			}
			else if(anyNode.getName().equals("skills"))
			{
				ArrayList skills = anyNode.getSubNodes();
				int size = skills.size();
				int [] ids = new int[size];
				for(int j = 0 ; j < size ; j ++)
				{
					XMLNode skill = (XMLNode)skills.get(j);
					ids[j] = Integer.parseInt(skill.getAttributeValue("id"));
				}
				//初始化技能组
				SkillTome skillTome = (SkillTome)player.getExtPlayerInfo("skillTome");
				skillTome.initBaseSkill(ids,player);
			}
			else if(anyNode.getName().equals("goods"))
			{
				ArrayList list = anyNode.getSubNodes();
				int size = list.size();

				Bag bag = (Bag)player.getExtPlayerInfo("bag");
				for(int j = 0 ; j < size ; j ++)
				{
					XMLNode goodsNode = (XMLNode)list.get(j);
					int goodsId = Integer.parseInt(goodsNode.getAttributeValue("id"));
					int goodsCount = Integer.parseInt(goodsNode.getAttributeValue("count"));
					int bindMode = Integer.parseInt(goodsNode.getAttributeValue("bind"));
		
					Goods goods = (Goods)DataFactory.getInstance().getGameObject(goodsId);

					if(goods == null)
						continue;

					Goods newGoods = null;
					if(goods instanceof GoodsEquip)
					{
						GoodsEquip equip = (GoodsEquip) goods;
						newGoods = equip.makeNewBetterEquip(equip.quality);
					}
					else if(goods instanceof GoodsProp)
					{
						newGoods = (Goods) Goods.cloneObject(goods);
						newGoods.goodsCount = goodsCount;
					}
					
					newGoods.bindMode = bindMode;
					newGoods.pickUpBind();
					newGoods.objectIndex = GameServer.getInstance().getWorldManager().getDatabaseAccessor().getGoodsObjIndex();
					
					//System.out.println(j+"  加入背包  "+newGoods.name +"  流水号 "+newGoods.objectIndex);
					bag.getGoodsList()[j] = newGoods;

				}
			}
			else if(anyNode.getName().equals("petSkills"))
			{
//				ArrayList skills = anyNode.getSubNodes();
//				if(skills != null)
//				{
//					int size = skills.size();
//					int [] ids = new int[size];
//					for(int j = 0 ; j < size ; j ++)
//					{
//						XMLNode skill = (XMLNode)skills.get(j);
//						ids[j] = Integer.parseInt(skill.getAttributeValue("id"));
//					}
//					//初始化宠物技能组
//					Pet pet = (Pet)player.getExtPlayerInfo("pet");
//					pet.initPetSkill(ids);
//				}
			}
			else if(anyNode.getName().equals("pet"))
			{
				ArrayList skills = anyNode.getSubNodes();
				int size = skills.size();
				for(int j = 0 ; j < size ; j ++)
				{
					XMLNode skill = (XMLNode)skills.get(j);
					int id = Integer.parseInt(skill.getAttributeValue("id"));
					GoodsProp goods = (GoodsProp) DataFactory.getInstance().getGameObject(id);
					GoodsProp prop = (GoodsProp) Goods.cloneObject(goods);
					Pet pet = (Pet) DataFactory.getInstance().getGameObject(prop.petId);
					Pet newPet = (Pet) Pet.cloneObject(pet);
					prop.petIndex = GameServer.getInstance().getWorldManager().getDatabaseAccessor().getGoodsObjIndex();
					prop.objectIndex = GameServer.getInstance().getWorldManager().getDatabaseAccessor().getGoodsObjIndex();
					newPet.objectIndex = prop.petIndex;
					PetTome pets = (PetTome) player.getExtPlayerInfo("petTome");
					newPet.setPlayer(player);
					newPet.isActive = true;
					newPet.requireExp = newPet.getInitExp();

					pets.addPet(newPet);
					Bag bag = (Bag) player.getExtPlayerInfo("bag");
					bag.addPetEggs(prop);
					break;
				}
				
			}
			else if(anyNode.getName().equals("giftGoods"))
			{
				ArrayList list = anyNode.getSubNodes();
				int size = list.size();

				Bag bag = (Bag)player.getExtPlayerInfo("bag");
				for(int j = 0 ; j < size ; j ++)
				{
					XMLNode goodsNode = (XMLNode)list.get(j);
					int goodsId = Integer.parseInt(goodsNode.getAttributeValue("id"));
					int goodsCount = Integer.parseInt(goodsNode.getAttributeValue("count"));
		
					Goods[] goodsList = makeGoods(goodsId, goodsCount);
					
					//System.out.println(j+"  加入背包  "+newGoods.name +"  流水号 "+newGoods.objectIndex);
					for (int k = 0; k < goodsList.length; k++)
					{
						if(goodsList[k] == null)
							continue;
						bag.addGiftGoods(goodsList[k]);
					}
					
					goodsList = null;
				}
			}
		}

		return true;
	}
	
	/**
	 * 检测能否进入房间 房间是否有时间限制  true为可以进  false为不能进
	 * @param id
	 * @return
	 */
	public boolean checkInRoom(PlayerController target,int id)
	{
		Map map = (Map)getAttachment(DataFactory.TIME_CONDITION);
		
		if(map == null)
			return true;
		
		Object obj = map.get(id+"");
		
		if(obj == null)
			return true;
		
		String[] strs = Utils.split((String)obj, "|");
		String[] ts = null;
		if(strs.length == 1)
			ts = Utils.split((String)obj, ":");
		else if(strs.length > 1)
			ts = Utils.split(strs[0], ":");
		else
			return true;
			
		String currTime = WorldManager.getTypeTime("yyyy-MM-dd", WorldManager.currentTime);
		
		for (int i = 0; i < ts.length; i++)
		{
			if(ts[i].trim().equals(currTime))
			{	
				return true;
			}
		}
	
		target.sendAlert(ErrorCode.ALERT_NOT_PARTY_TIME);
		
		return false;
	}
	

	
	/**
	 * 是否需要把玩家设置回城(在特殊时间特殊房间)
	 * @param roomId
	 * @return
	 */
	public boolean isInConPartyRoom(int roomId)
	{
		Map map = (Map)getAttachment(DataFactory.TIME_CONDITION);
		
		if(map == null)
			return false;
		
		Object obj = map.get(roomId+"");
		
		if(obj == null)
			return false;
		String[] strs = Utils.split((String)obj, "|");
		String[] ts = null;
		if(strs.length == 1)
			ts = Utils.split((String)obj, ":");
		else if(strs.length > 1)
			ts = Utils.split(strs[0], ":");
		else
			return true;
		String currTime = WorldManager.getTypeTime("yyyy-MM-dd", WorldManager.currentTime);
		
		for (int i = 0; i < ts.length; i++)
		{
			if(ts[i].trim().equals(currTime))
			{	
				return false;
			}
		}
	
		return true;
	
	}
	
	
	public static boolean isGoodsNotice(String type,Goods goods)
	{
		List list = (List) DataFactory.getInstance().getAttachment(NOTICE_LIST);
		if(GoodsNotice.COPY_BOX.equals(type))
		{
			for (int i = 0; i < list.size(); i++) 
			{
				GoodsNotice gn = (GoodsNotice) list.get(i);
				if(GoodsNotice.COPY_BOX.equals(gn.noticeType) && gn.id == goods.id && gn.noticeCount == goods.goodsCount && gn.noticeCount > 0)
					return true;
			}
		}
		else if(GoodsNotice.MONEY_BATTLE.equals(type))
		{
			for (int i = 0; i < list.size(); i++) 
			{
				GoodsNotice gn = (GoodsNotice) list.get(i);
				if(GoodsNotice.MONEY_BATTLE.equals(gn.noticeType) && gn.id == goods.id && gn.noticeCount == goods.goodsCount && gn.noticeCount > 0)
					return true;
			}
		}
		else if(GoodsNotice.NO_NOTICE.equals(type))
		{
			for (int i = 0; i < list.size(); i++) 
			{
				GoodsNotice gn = (GoodsNotice) list.get(i);
				if(GoodsNotice.NO_NOTICE.equals(gn.noticeType) && gn.id == goods.id)
					return true;
			}
		}
		else if(GoodsNotice.PARTY_BOSS.equals(type))
		{
			for (int i = 0; i < list.size(); i++) 
			{
				GoodsNotice gn = (GoodsNotice) list.get(i);
				if(GoodsNotice.PARTY_BOSS.equals(gn.noticeType) && gn.id == goods.id)
					return true;
			}
		}
		else if(GoodsNotice.SPECIAL_BOSS.equals(type))
		{
			for (int i = 0; i < list.size(); i++) 
			{
				GoodsNotice gn = (GoodsNotice) list.get(i);
				if(GoodsNotice.SPECIAL_BOSS.equals(gn.noticeType) && gn.id == goods.id)
					return true;
			}
		}
		else if(GoodsNotice.TIME_TRAIN.equals(type))
		{
			for (int i = 0; i < list.size(); i++) 
			{
				GoodsNotice gn = (GoodsNotice) list.get(i);
				if(GoodsNotice.TIME_TRAIN.equals(gn.noticeType) && gn.id == goods.id)
					return true;
			}
		}
		else if(GoodsNotice.CHONG_GUAN.equals(type))
		{
			for (int i = 0; i < list.size(); i++) 
			{
				GoodsNotice gn = (GoodsNotice) list.get(i);
				if(GoodsNotice.CHONG_GUAN.equals(gn.noticeType) && gn.id == goods.id)
					return true;
			}
		}
		else if(GoodsNotice.STAR_BOX.equals(type))
		{
			for (int i = 0; i < list.size(); i++) 
			{
				GoodsNotice gn = (GoodsNotice) list.get(i);
				if(GoodsNotice.STAR_BOX.equals(gn.noticeType) && gn.id == goods.id)
					return true;
			}
		}
		return false;
	}
	
	/** 总次数的记录ID */
	public static final String ALL = "all";
	private HashMap lianhuaMap = new HashMap(100);//总记录存在这里面
	private ArrayList lianhuaList = new ArrayList(100);//这里面不包括总的记录
	
	private HashMap qingtongMap = new HashMap(100);
	private ArrayList qingtongList = new ArrayList(100);
	private HashMap baiyinMap = new HashMap(100);
	private ArrayList baiyinList = new ArrayList(100);
	private HashMap huangjinMap = new HashMap(100);
	private ArrayList huangjinList = new ArrayList(100);
	private HashMap tianshenMap = new HashMap(100);
	private ArrayList tianshenList = new ArrayList(100);
	
	private ArrayList vipPlayerInfos = new ArrayList(100);
	public void putLogMap(int type,LogTemp log)
	{
		HashMap map = null;
		ArrayList list = null;
		if(type == 0)
		{
			map = lianhuaMap;
			list = lianhuaList;
		}
		else if(type == 1)
		{
			map = qingtongMap;
			list = qingtongList;
		}
		else if(type == 2)
		{
			map = baiyinMap;
			list = baiyinList;
		}
		else if(type == 3)
		{
			map = huangjinMap;
			list = huangjinList;
		}
		else if(type == 4)
		{
			map = tianshenMap;
			list = tianshenList;
		}
		if(map.get(log.getAccountName()) == null)
		{
			map.put(log.getAccountName(), log);
			list.add(log);
			if(map.get(ALL) == null)
			{
				LogTemp all = new LogTemp();
				all.setPoint(log.getPoint());
				all.setMoney(log.getMoney());
				all.setEquipMoney(log.getEquipMoney());
				all.updateUseGoods(log.getGoodsList());
				map.put(ALL, all);
			}
			else
			{
				LogTemp all = (LogTemp) map.get(ALL);
				all.name = log.name;
				all.setPoint(all.getPoint()+log.getPoint());
				all.setMoney(all.getMoney()+log.getMoney());
				all.setEquipMoney(all.getEquipMoney()+log.getEquipMoney());
				all.setCount(all.getCount()+1);
				all.updateUseGoods(log.getGoodsList());
			}
		}
		else
		{
			LogTemp tmp = (LogTemp) map.get(log.getAccountName());
			tmp.name = log.name;
			tmp.setLevel(log.getLevel());
			tmp.setPoint(tmp.getPoint()+log.getPoint());
			tmp.setMoney(tmp.getMoney()+log.getMoney());
			tmp.setEquipMoney(tmp.getEquipMoney()+log.getEquipMoney());
			tmp.setCount(tmp.getCount()+1);
			tmp.updateUseGoods(log.getGoodsList());
			
			LogTemp all = (LogTemp) map.get(ALL);
			all.name = log.name;
			all.setPoint(all.getPoint()+log.getPoint());
			all.setMoney(all.getMoney()+log.getMoney());
			all.setEquipMoney(all.getEquipMoney()+log.getEquipMoney());
			all.setCount(all.getCount()+1);
			all.updateUseGoods(log.getGoodsList());
		}
	}
	
	public ArrayList getLogList(int type)
	{
		if(type == 0)
			return lianhuaList;
		else if(type == 1)
			return qingtongList;
		else if(type == 2)
			return baiyinList;
		else if(type == 3)
			return huangjinList;
		else if(type == 4)
			return tianshenList;
		else if(type == 5)
			return vipPlayerInfos;
		else 
			return null;
	}
	
	public LogTemp getLogTemp(int type,String key)
	{
		if(type == 0)
			return (LogTemp) lianhuaMap.get(key);
		else if(type == 1)
			return (LogTemp) qingtongMap.get(key);
		else if(type == 2)
			return (LogTemp) baiyinMap.get(key);
		else if(type == 3)
			return (LogTemp) huangjinMap.get(key);
		else if(type == 4)
			return (LogTemp) tianshenMap.get(key);
		else 
			return null;
	}
	
	public void addLog(String fileName,LogTemp log)
	{
		String date = WorldManager.getTypeTime("yyyyMMdd", System.currentTimeMillis());
		fileName = fileName.replace(".txt", "");
		String[] strs = Utils.split(fileName, "_");
		if("lianhuaLog".equals(strs[0]) && date.equals(strs[1]))
		{
			lianhuaMap.put(log.getAccountName(), log);
			if(!"all".equals(log.getAccountName()))
				lianhuaList.add(log);
		}
		else if("qingtongLog".equals(strs[0]) && date.equals(strs[1]))
		{
			qingtongMap.put(log.getAccountName(), log);
			if(!"all".equals(log.getAccountName()))
				qingtongList.add(log);
		}
		else if("baiyinLog".equals(strs[0]) && date.equals(strs[1]))
		{
			baiyinMap.put(log.getAccountName(), log);
			if(!"all".equals(log.getAccountName()))
				baiyinList.add(log);
		}
		else if("huangjinLog".equals(strs[0]) && date.equals(strs[1]))
		{
			huangjinMap.put(log.getAccountName(), log);
			if(!"all".equals(log.getAccountName()))
				huangjinList.add(log);
		}
		else if("tianshenLog".equals(strs[0]) && date.equals(strs[1]))
		{
			tianshenMap.put(log.getAccountName(), log);
			if(!"all".equals(log.getAccountName()))
				tianshenList.add(log);
		}
	}
	
	public void clearLogs()
	{
		lianhuaMap = new HashMap(100);
		lianhuaList = new ArrayList(100);
		qingtongMap = new HashMap(100);
		qingtongList = new ArrayList(100);
		baiyinMap = new HashMap(100);
		baiyinList = new ArrayList(100);
		huangjinMap = new HashMap(100);
		huangjinList = new ArrayList(100);
		tianshenMap = new HashMap(100);
		tianshenList = new ArrayList(100);
		vipPlayerInfos = new ArrayList(100);
	}
	
	/**
	 * 添加VIP玩家记录
	 * @param target
	 * @param data
	 */
	public void addVipPlayerInfo(PlayerController target,String data)
	{
		LogTemp log = new LogTemp(target,data);
		if(!vipPlayerInfos.contains(log))
		{
			vipPlayerInfos.add(log);
		}
	}
	
	/**
	 * 临时兑换序列号保存到文件
	 */
	public void saveTmpIndex()
	{
		HashMap map = (HashMap) getAttachment(INDEX_LIST);
		if(map == null)
			return;
		if(map.size() == 0)
			return;
		StringBuffer data = new StringBuffer();
		Object[] objs = map.keySet().toArray();
		Object[] values = map.values().toArray();
		for (int i = 0; i < objs.length; i++) 
		{
			data.append(objs[i]);
			data.append("\t");
			data.append(values[i]);
			if(i != objs.length-1)
				data.append(Utils.LINE_SEPARATOR);
		}
		Utils.writeFile(GameServer.getAbsolutePath()+"data/index.txt", data.toString().getBytes());
	}
	
	public void removeTmpIndex(String index)
	{
		HashMap map = (HashMap) getAttachment(INDEX_LIST);
		if(map == null)
			return;
		if(map.size() == 0)
			return;
		map.remove(index);
	}
	
	public Goods getTmpIndexGoods(String index)
	{
		HashMap map = (HashMap) getAttachment(INDEX_LIST);
		if(map == null)
			return null;
		if(map.size() == 0)
			return null;
		Object obj = map.get(index);
		if(obj == null)
			return null;
		Goods[] goods = makeGoods(Integer.parseInt(obj.toString()));
		
		return goods!=null&&goods.length>0?goods[0]:null;
	}
}
