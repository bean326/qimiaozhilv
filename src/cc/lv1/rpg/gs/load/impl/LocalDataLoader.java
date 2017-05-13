package cc.lv1.rpg.gs.load.impl;

import java.io.*;

import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.i.Map;
import vin.rabbit.util.collection.impl.ArrayList;
import vin.rabbit.util.collection.impl.HashMap;
import vin.rabbit.util.xml.XMLNode;
import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.data.Exp;
import cc.lv1.rpg.gs.data.MailRemind;
import cc.lv1.rpg.gs.data.MarkChar;
import cc.lv1.rpg.gs.data.PetExp;
import cc.lv1.rpg.gs.data.TaskManager;
import cc.lv1.rpg.gs.data.UseChar;
import cc.lv1.rpg.gs.entity.controller.AreaController;
import cc.lv1.rpg.gs.entity.controller.CopyController;
import cc.lv1.rpg.gs.entity.controller.MonsterGroupController;
import cc.lv1.rpg.gs.entity.controller.NpcController;
import cc.lv1.rpg.gs.entity.controller.RoomController;
import cc.lv1.rpg.gs.entity.ext.SkillTome;
import cc.lv1.rpg.gs.entity.impl.GoldBox;
import cc.lv1.rpg.gs.entity.impl.BaseFlayer;
import cc.lv1.rpg.gs.entity.impl.BossDropProp;
import cc.lv1.rpg.gs.entity.impl.BoxDropProp;
import cc.lv1.rpg.gs.entity.impl.GoldParty;
import cc.lv1.rpg.gs.entity.impl.Goods;
import cc.lv1.rpg.gs.entity.impl.LogTemp;
import cc.lv1.rpg.gs.entity.impl.MoneyBattle;
import cc.lv1.rpg.gs.entity.impl.Monster;
import cc.lv1.rpg.gs.entity.impl.MonsterAI;
import cc.lv1.rpg.gs.entity.impl.MonsterDropProp;
import cc.lv1.rpg.gs.entity.impl.NPC;
import cc.lv1.rpg.gs.entity.impl.Player;
import cc.lv1.rpg.gs.entity.impl.RankReward;
import cc.lv1.rpg.gs.entity.impl.RewardSender;
import cc.lv1.rpg.gs.entity.impl.SShop;
import cc.lv1.rpg.gs.entity.impl.Shop;
import cc.lv1.rpg.gs.entity.impl.Task;
import cc.lv1.rpg.gs.entity.impl.UpRole;
import cc.lv1.rpg.gs.entity.impl.answer.AnswerReward;
import cc.lv1.rpg.gs.entity.impl.answer.Guide;
import cc.lv1.rpg.gs.entity.impl.answer.Question;
import cc.lv1.rpg.gs.entity.impl.battle.effect.FlashEffect;
import cc.lv1.rpg.gs.entity.impl.battle.effect.TimeEffect;
import cc.lv1.rpg.gs.entity.impl.battle.skill.ActiveSkill;
import cc.lv1.rpg.gs.entity.impl.battle.skill.PassiveSkill;
import cc.lv1.rpg.gs.entity.impl.battle.skill.PetActiveSkill;
import cc.lv1.rpg.gs.entity.impl.battle.skill.PetPassiveSkill;
import cc.lv1.rpg.gs.entity.impl.battle.skill.PetSkill;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsEquip;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsMarry;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsNotice;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsPetEquip;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsProp;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsSynt;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsUp;
import cc.lv1.rpg.gs.entity.impl.pet.Pet;
import cc.lv1.rpg.gs.entity.impl.pet.PetSkillStudy;
import cc.lv1.rpg.gs.entity.impl.pet.PetTrain;
import cc.lv1.rpg.gs.entity.impl.pet.PetUpRule;
import cc.lv1.rpg.gs.entity.impl.pet.PetWords;
import cc.lv1.rpg.gs.entity.impl.story.Event;
import cc.lv1.rpg.gs.entity.impl.story.EventTeam;
import cc.lv1.rpg.gs.entity.impl.story.Story;
import cc.lv1.rpg.gs.gui.MainFrame;
import cc.lv1.rpg.gs.load.i.ILoader;
import cc.lv1.rpg.gs.util.DropPropXML;
/**
 * 本地资源加载器
 * @author dxw
 *
 */
public class LocalDataLoader implements ILoader
{ 

	private GameServer gameserver = null;
	
	private String maskCharStr = GameServer.getAbsolutePath()+"config/maskChar.txt";
	
	private String useCharStr = GameServer.getAbsolutePath()+"config/useChar.txt";
	
	private String monsterStr = GameServer.getAbsolutePath()+"data/entity/monster.txt";
	
	private String monsterGroupStr = GameServer.getAbsolutePath()+"data/entity/monsterGroup.txt";
	
	private String npcStr = GameServer.getAbsolutePath()+"data/entity/npc.txt";
	
	private String worldStr = GameServer.getAbsolutePath()+"data/world.txt";

	private String npcDialogStr = GameServer.getAbsolutePath()+"data/npc_dialog/npc";

	private String nativityStr = GameServer.getAbsolutePath()+"data/nativity.txt";
	
	private String equipmentStr = GameServer.getAbsolutePath()+"data/entity/equipment.txt";
	
	private String petEquipmentStr = GameServer.getAbsolutePath()+"data/entity/pet/petEquipment.txt";
	
	private String propStr = GameServer.getAbsolutePath()+"data/entity/prop.txt";
	
	private String shopStr = GameServer.getAbsolutePath()+"data/entity/shop";

	private String timeEffStr = GameServer.getAbsolutePath()+"data/entity/effect1.txt";
	
	private String flashEffStr = GameServer.getAbsolutePath()+"data/entity/effect2.txt";
	
	private String aSkillStr = GameServer.getAbsolutePath()+"data/entity/skills1.txt";
	
	private String pSkillStr = GameServer.getAbsolutePath()+"data/entity/skills2.txt";
	
	private String petActiveSkillStr = GameServer.getAbsolutePath()+"data/entity/skills3.txt";
	
	private String petPassiveSkillStr = GameServer.getAbsolutePath()+"data/entity/skills4.txt";
	
	private String monsterAiStr = GameServer.getAbsolutePath()+"data/entity/monsterAI.txt";
	
	private String taskStr = GameServer.getAbsolutePath()+"data/task";
	
	private String expStr = GameServer.getAbsolutePath()+"data/exp.txt";
	
	private String mailRemindStr = GameServer.getAbsolutePath()+"data/mailRemind.txt";
	
	private String petExpStr = GameServer.getAbsolutePath()+"data/entity/pet/petExp.txt";
	
	private String battlePetExpStr = GameServer.getAbsolutePath()+"data/entity/pet/battlePetExp.txt";
	
	private String equipSShopStr = GameServer.getAbsolutePath()+"data/equipSShop.txt";
	
	private String equipDShopStr = GameServer.getAbsolutePath()+"data/equipDShop.txt";
	
	private String goodsUpStr = GameServer.getAbsolutePath()+"data/goodsUp.txt";
	
	private String goodsEoStr = GameServer.getAbsolutePath()+"data/eachOtherChange.txt";
	
	private String moneyBattleStr = GameServer.getAbsolutePath()+"data/moneyBattle.txt";
	
	private String monsterDropPropStr = GameServer.getAbsolutePath()+"data/entity/monsterDropProp.txt";
	
	private String taskDropPropStr = GameServer.getAbsolutePath()+"data/entity/taskDropProp.txt";
	
	private String boxDropPropStr = GameServer.getAbsolutePath()+"data/entity/boxDropProp.txt";
	
	private String bossDropPropStr = GameServer.getAbsolutePath()+"data/entity/bossDropProp.txt";
	
	private String boxStr = GameServer.getAbsolutePath()+"data/entity/box";
	
	private String baseFlyerStr = GameServer.getAbsolutePath()+"data/baseflyer.txt";
	
	private String petStr = GameServer.getAbsolutePath()+"data/entity/pet/pet.txt";
	
	private String petGetRuleStr = GameServer.getAbsolutePath()+"data/entity/pet/petGetRule.txt";
	
	private String petUpStr = GameServer.getAbsolutePath()+"data/entity/pet/petUp/pet";

	private String petSkillStudyStr = GameServer.getAbsolutePath()+"data/entity/pet/petSkillStudy.txt";
	
	private String gmProcessStr = GameServer.getAbsolutePath()+"data/gm_process.txt";
	
	private String languageStr = GameServer.getAbsolutePath()+"data/language.txt";
	
	private String serverControllerStr = GameServer.getAbsolutePath()+"data/serverController.txt";
	
	private String petTrainStr = GameServer.getAbsolutePath()+"data/entity/pet/petTrain.txt";
	
	private String petWordsStr = GameServer.getAbsolutePath()+"data/entity/pet/petWords.txt";
	
	private String noNoticeStr = GameServer.getAbsolutePath()+"data/notice.txt";
	
	private String timeRoomConditionStr = GameServer.getAbsolutePath()+"data/timeRoomCondition.txt";
	
	private String rankRewardStr = GameServer.getAbsolutePath()+"data/entity/answer/rankReward.txt";
	
	private String answerRewardStr = GameServer.getAbsolutePath()+"data/entity/answer/answerReward.txt";
	
	private String questionStr = GameServer.getAbsolutePath()+"data/entity/answer/question.txt";
	
	private String copyStr = GameServer.getAbsolutePath()+"data/copy.txt";
	
	private String marryStr = GameServer.getAbsolutePath()+"data/goodsMarry.txt";
	
	private String upRoleStr = GameServer.getAbsolutePath()+"data/upRole.txt";
	
	private String guideStepStr = GameServer.getAbsolutePath()+"data/guideStep.txt";
	
	private String goldBoxStr = GameServer.getAbsolutePath()+"data/entity/goldBox.txt";
	
	public static String rewardSenderStr = GameServer.getAbsolutePath()+"data/rewardSender.txt";
	
	private String goldPartyStr = GameServer.getAbsolutePath()+"data/goldParty.txt";
	
	private String storyStr = GameServer.getAbsolutePath()+"data/story/story.txt";
	
	private String eventTeamStr = GameServer.getAbsolutePath()+"data/story/eventTeam.txt";
	
	private String eventStr = GameServer.getAbsolutePath()+"data/story/event";
	
	private String logStr = GameServer.getAbsolutePath()+"logs/moneyLog";
	
	private String indexStr = GameServer.getAbsolutePath()+"data/index.txt";
	
	public void setAttachment(Object obj)
	{
		if(obj instanceof GameServer)
			gameserver = (GameServer)obj;
		else
			MainFrame.println("unknown attachment!");
	}

	/**
	 * 加载.........
	 * 
	 */
	public void loading()
	{
		loadMaskCharStr();
		MainFrame.println("load MaskCharStr success!!");
		
		loadUseCharStr();
		MainFrame.println("load UseCharStr success!!");
		
		loadCopy();
		MainFrame.println("load copy success!!");
		
		loadEffect();
		MainFrame.println("load Effect success!!");
		
		loadSkill();
		MainFrame.println("load Skill success!!");
		
		loadGoodsProp();
		MainFrame.println("load prop success!!");
		
		loadGoodsEquip();
		MainFrame.println("load equipment success!!");
		
		loadGoldParty();
		MainFrame.println("load goldParty success!!");
		
		loadWorldMap();
		MainFrame.println("load WorldMap success!!");
		
		loadShop();
		MainFrame.println("load shop success!!");

		loadExtShop();
		MainFrame.println("load ext shop success!!");
		
		loadNpc();
		MainFrame.println("load Npc success!!");
		
		loadDropProp();
		MainFrame.println("load dropProp success!!");
		
		loadMonster();
		MainFrame.println("load monster success!!");
		
		loadMonsterGroup();
		MainFrame.println("load monsterGroup success!!");
		
		loadNativity();
		MainFrame.println("load nativity success!!");
		
		loadTask();
		MainFrame.println("load task success!!");
		
		loadExp();
		MainFrame.println("load exp success!!");
		
		loadBaseFlyer();
		MainFrame.println("load BaseFlyer success!!");
		
		loadPet();
		MainFrame.println("load Pet succes!!");
		
		loadMailRemind();
		MainFrame.println("load MailRemind success!!");
		
		loadGMProcess();
		MainFrame.println("load GMProcess success");
		
		loadPetTrain();
		MainFrame.println("load PetTrain success!!");
		
		loadNoNotice();
		MainFrame.println("load noNotice success!!");
		
		loadQuestion();
		MainFrame.println("load question success!!");
		
		if(loadRewardSender())
		{
			MainFrame.println("load RewardSender success!!");
		}
		
		loadMarry();
		MainFrame.println("load marry success!!");
		
		loadUpRole();
		MainFrame.println("load upRole success!!");
		
		loadTimeRoomCondition();
		MainFrame.println("load TimeRoomCondition success!!");
		
		loadGoldBox();
		MainFrame.println("load goldBox success!!");
		
		loadGuideStep();
		MainFrame.println("load guideStep success!!");
		
		loadFontStorage();
		MainFrame.println("load fontStorage success!!");
		
		loadMoneyBattle();
		MainFrame.println("load moneyBattle success!!");
		
		loadStory();
		MainFrame.println("load story success!!");
		
		loadLog();
		MainFrame.println("load log success!!");	
	}
	
	private void loadIndex()
	{
		if(!new File(indexStr).exists())
			return;
		indexStr = Utils.readFile2(indexStr);
		String[] indexes = Utils.split(indexStr, Utils.LINE_SEPARATOR);
		if(indexes.length == 0)
			return;
		HashMap map = new HashMap();
		for (int i = 0; i < indexes.length; i++) 
		{
			if(indexes[i].equals("") || indexes[i].length()==0)
				continue;
			String[] strs = Utils.split(indexes[i], "\t");
			if(strs.length!=2)
				continue;
			if(map.get(strs[0]) != null)
			{
				System.out.println("index is exits:"+strs[0]);
				continue;
			}
			map.put(strs[0], strs[1]);
		}
		DataFactory.getInstance().putAttachment(DataFactory.INDEX_LIST, map);
	}
	
	private void loadStory()
	{
		try 
		{
			ArrayList list = new ArrayList();
			File file = new File(eventStr);
			File [] eventfiles = file.listFiles();
			for(int i = 0 ; i < eventfiles.length ; i ++)
			{
				if(eventfiles[i] == null)
					continue;
				
				if(!eventfiles[i].toString().endsWith("txt"))
					continue;
				
				XMLNode eventNode = Utils.getXMLNode(eventfiles[i]);
				Event event = new Event();
				event.setWorld(gameserver.getWorldManager());
				event.parseEvent(eventNode);

				if(DataFactory.getInstance().getStoryObject(event.id) != null)
				{
					System.out.println("load event exists:"+event.id);
				}
				DataFactory.getInstance().putStoryObject(event);
				
				list.add(event);
			}
			DataFactory.getInstance().putAttachment(DataFactory.EVENT_LIST, list);
			
			list = Utils.loadFileVariables(eventTeamStr, EventTeam.class);
			for (int i = 0; i < list.size(); i++)
			{
				EventTeam eventTeam = (EventTeam) list.get(i);
				if(DataFactory.getInstance().getStoryObject(eventTeam.id) != null)
				{
					System.out.println("load eventTeam exists:"+eventTeam.id);
				}
				DataFactory.getInstance().putStoryObject(eventTeam);
			}
			DataFactory.getInstance().putAttachment(DataFactory.EVENT_TEAM, list);
			
			list = Utils.loadFileVariables(storyStr, Story.class);
			for (int i = 0; i < list.size(); i++)
			{
				Story story = (Story) list.get(i);
				if(DataFactory.getInstance().getStoryObject(story.id) != null)
				{
					System.out.println("load story exists:"+story.id);
				}
				DataFactory.getInstance().putStoryObject(story);
			}
			DataFactory.getInstance().putAttachment(DataFactory.STORY_LIST, list);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	private void loadLog()
	{
		try 
		{
			File file = new File(logStr);
			
			if(!file.isDirectory())
			{
				file.mkdir();
			}
			
			File[] files = file.listFiles();
			if(files == null)
				return;
			String date = WorldManager.getTypeTime("yyyyMMdd", System.currentTimeMillis());
			for (int j = 0; j < files.length; j++) 
			{
				if(!files[j].getName().endsWith("txt"))
					continue;
				if(files[j].getName().startsWith("vip"))
					continue;
				String[] ss = files[j].getName().replace(".txt", "").split("_");
				if(!date.equals(ss[1]))
					continue;
	
				BufferedReader br = new BufferedReader(new FileReader(new File(files[j].getPath())));
				br.readLine();
				String line;
				while((line = br.readLine()) != null)
				{
					if(line.length() == 0)
						continue;
					String[] strs = Utils.split(line, "\t");
					LogTemp all = new LogTemp();
					all.setAccountName(strs[0]);
					all.name = strs[1];
					if(strs.length == 7)
					{
						all.setCount(Integer.parseInt(strs[2]));
						all.setPoint(Integer.parseInt(strs[3]));
						all.setMoney(Integer.parseInt(strs[4]));
						all.setEquipMoney(Integer.parseInt(strs[5]));
						all.setGoods(strs[6]);
					}
					else if(strs.length == 8)
					{
						all.setLevel(Integer.parseInt(strs[2]));
						all.setCount(Integer.parseInt(strs[3]));
						all.setPoint(Integer.parseInt(strs[4]));
						all.setMoney(Integer.parseInt(strs[5]));
						all.setEquipMoney(Integer.parseInt(strs[6]));
						all.setGoods(strs[7]);
					}
					DataFactory.getInstance().addLog(files[j].getName(), all);
				}
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	private void loadMoneyBattle()
	{
		try
		{
			ArrayList list = Utils.loadFileVariables(moneyBattleStr, MoneyBattle.class);
			Map map = new HashMap();
			for (int i = 0; i < list.size(); i++)
			{
				MoneyBattle mb = (MoneyBattle) list.get(i);
				map.put(mb.roomId, mb);
			}
			DataFactory.getInstance().putAttachment(DataFactory.MONEY_BATTLE, map);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			System.out.println("load moneyBattle error!");
		}
	}
	
	private void loadGuideStep()
	{
		try
		{
			File file = new File(guideStepStr);
			if(!file.exists())
				return;
			ArrayList list = Utils.loadFileVariables(guideStepStr, Guide.class);
			DataFactory.getInstance().putAttachment(DataFactory.GUIDE_ANSWER, list);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			System.out.println("load guideStep error!");
		}
	}
	
	private void loadGoldParty()
	{
		try
		{
			ArrayList list = Utils.loadFileVariables(goldPartyStr, GoldParty.class);
			DataFactory.getInstance().putAttachment(DataFactory.GOLD_PARTY, list);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			System.out.println("load goldParty error!");
		}
	}
	
	private void loadFontStorage()
	{
		languageStr = Utils.readFile2(languageStr);
		String[] fonts = Utils.split(languageStr, Utils.LINE_SEPARATOR);
		for (int i = 0; i < fonts.length; i++) 
		{
			if(fonts[i].isEmpty())
				continue;
			String[] strs = Utils.split(fonts[i], "\t");
			DataFactory.getInstance().addFont(Integer.parseInt(strs[0]), strs[1]);
		}
		
		serverControllerStr = Utils.readFile2(serverControllerStr);
		String[] scs = Utils.split(serverControllerStr, Utils.LINE_SEPARATOR);
		for (int i = 0; i < scs.length; i++) 
		{
			String[] strs = Utils.split(scs[i], "=");
			if("fontVer".equals(strs[0]))
				DataFactory.fontVer = strs[1];
			else if("isIdcard".equals(strs[0]))
				DataFactory.isIdcard = strs[1].equals("true");
			else if("shopVer".equals(strs[0]))
				DataFactory.shopVer = strs[1];
		}
	}
	
	private void loadGoldBox()
	{
		try
		{
			File file = new File(goldBoxStr);
			if(file.isFile() && file.exists())
			{
				String out = goldBoxStr.substring(1, goldBoxStr.length()-11);
				DropPropXML.writeXML(goldBoxStr,out, "goldBox");
				ArrayList list = Utils.loadFileVariables(out+"goldBox", GoldBox.class);
				DataFactory.getInstance().putAttachment(DataFactory.GOLD_BOX, list);
			}
			else
			{
				System.out.println("Load awardType error null!");
			}
			
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			System.out.println("load awardType error!");
		}
	}
	
	private void loadUpRole()
	{
		try
		{
			ArrayList list = Utils.loadFileVariables(upRoleStr, UpRole.class);
			DataFactory.getInstance().putAttachment(DataFactory.UP_ROLE, list);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			System.out.println("load marry error!");
		}
	}
	
	private void loadMarry()
	{
		try
		{
			ArrayList list = Utils.loadFileVariables(marryStr, GoodsMarry.class);
			DataFactory.getInstance().putAttachment(DataFactory.MARRY_ROLE, list);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			System.out.println("load marry error!");
		}
	}
	
	public void loadMaskCharStr()
	{
		try
		{
			
			File file = new File(maskCharStr);
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ( (line = br.readLine()) != null)
			{
			   if(line== null || line.equals(""))
				   continue;
				
			   MarkChar.putMarkChar(line);
			}
			
		}
		catch(Exception e)
		{
			System.out.println("loadMask CharStr error!");
		}
	}
	
	
	public void loadUseCharStr()
	{
		try
		{
			
			File file = new File(useCharStr);
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ( (line = br.readLine()) != null)
			{
			   if(line== null || line.equals(""))
				   continue;
				
			   UseChar.putUseChar(line);
			}
//			System.out.println(UseChar.size());
		}
		catch(Exception e)
		{
			System.out.println("loadMask CharStr error!");
		}
	}
	
	
	private boolean loadRewardSender()
	{
		if(!new File(rewardSenderStr).exists())
			return false;
		
		try
		{
			ArrayList list = Utils.loadFileVariables(rewardSenderStr, RewardSender.class);
			DataFactory.getInstance().putAttachment(DataFactory.REWARDSENDER_LIST, list);
			return true;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			System.out.println("load RewardSender error!");
		}
		return false;
	}

	private void loadCopy()
	{
		try
		{
			ArrayList list = Utils.loadFileVariables(copyStr, CopyController.class);
			DataFactory.getInstance().putAttachment(DataFactory.COPY_LIST, list);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			System.out.println("load copy error!");
		}
	}
	
	private void loadQuestion()
	{
		try 
		{
			DataFactory df = DataFactory.getInstance();
			
			ArrayList questions = Utils.loadFileVariables(questionStr, Question.class);
			
			List list1 = new ArrayList();
			List list2 = new ArrayList();
			List list3 = new ArrayList();
			List list4 = new ArrayList();
			List list5 = new ArrayList();
			
			for (int i = 0; i < questions.size(); i++)
			{
				Question question = (Question) questions.get(i);
				if(df.getQuestion(question.id) != null)
				{
					System.out.println("loadQuestion question is exist:"+question.id);
					continue;
				}
				
				if(question.type == 1)
					list1.add(question);
				else if(question.type == 2)
					list2.add(question);
				else if(question.type == 3)
					list3.add(question);
				else if(question.type == 4)
					list4.add(question);
				else if(question.type == 5)
					list4.add(question);
				
				df.addQuestion(question);
			}
			
			df.addAnswerObject(DataFactory.TYPE_ONE_QUESTION, list1);
			df.addAnswerObject(DataFactory.TYPE_TWO_QUESTION, list2);
			df.addAnswerObject(DataFactory.TYPE_THREE_QUESTION, list3);
			df.addAnswerObject(DataFactory.TYPE_FOUR_QUESTION, list4);
			df.addAnswerObject(DataFactory.TYPE_FIVE_QUESTION, list5);
			
			ArrayList rrs = Utils.loadFileVariables(rankRewardStr, RankReward.class);
			for (int i = 0; i < rrs.size(); i++)
			{
				RankReward rr = (RankReward) rrs.get(i);
				df.addRankRewards(rr);
			}
			
			ArrayList ars = Utils.loadFileVariables(answerRewardStr, AnswerReward.class);
			for (int i = 0; i < ars.size(); i++)
			{
				AnswerReward ar = (AnswerReward) ars.get(i);
				if(df.getAnswerReward(ar.minLevel,ar.maxLevel) != null)
				{
					System.out.println("loadQuestion answerReward is exist:"+ar.id);
					continue;
				}
				df.addARList(ar);
			}
		}
		catch (Exception e) 
		{
			//e.printStackTrace();
			System.out.println("load question error!");
		}
	}
	
	private void loadNoNotice()
	{
		try {
			ArrayList notices = Utils.loadFileVariables(noNoticeStr, GoodsNotice.class);
			ArrayList list = new ArrayList();
			
			for (int i = 0; i < notices.size(); i++)
			{
				list.add(notices.get(i));
			}
			
			DataFactory.getInstance().putAttachment(DataFactory.NOTICE_LIST, list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadPetTrain()
	{
		try 
		{
			ArrayList petTrain = Utils.loadFileVariables(petTrainStr, PetTrain.class);

			DataFactory.getInstance().putAttachment(DataFactory.PET_TRAIN_LIST, petTrain);
			
			
			ArrayList petWords = Utils.loadFileVariables(petWordsStr, PetWords.class);
			
			DataFactory.getInstance().putAttachment(DataFactory.PET_WORDS_LIST, petWords);
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			System.out.println("load petTrain error!");
		} 
		
	}

	private void loadGMProcess()
	{
		gmProcessStr = Utils.readFile2(gmProcessStr);
		String gmUsers[]= Utils.split(gmProcessStr, Utils.LINE_SEPARATOR);
		
		ArrayList list = new ArrayList();
		
		for (int i = 0; i < gmUsers.length; i++)
		{
			if(gmUsers.length -1 == i)
				continue;
			MainFrame.println("GM User :"+gmUsers[i]);
			list.add(gmUsers[i]);
		}
		
		DataFactory.getInstance().putAttachment(DataFactory.GMPROCESS_LIST, list);
		
		
		GoodsEquip ges= (GoodsEquip)DataFactory.getInstance().getGameObject(1050605139);
		
		GoodsEquip ge = new GoodsEquip();
		ges.copyTo(ge);

		ge.id = 19820520;
		ge.name = "G.M.GOODS1";
		ge.level = 1;
		ge.type = 1;
		ge.iconId = 1141010001;
		ge.minQuality = 3;
		ge.maxQuality = 3;
		ge.repeatNumber = 1;
		ge.job = "0";
		ge.equipType = 9;
		ge.equipLevel = 1;
		ge.maxEquipLevel = 1;
		ge.phyAtt = 100000;
		ge.sptAtt = 100000;
		ge.modValueWhite = 1000000;
		ge.modValueGreen = 1200000;
		ge.curePoint = 60000;
		ge.isVIP = true;
		ge.startLevel = 10;

		ge.phyHurtAvoid = 10000;
		ge.sptHurtAvoid = 10000;
		
		ge.lifePoint = 3000000;
		ge.magicPoint = 300000000;
		ge.phyDefWhite = 300000;
		ge.phyDefGreen = 300000;
		ge.phyDefBlue = 300000;
		ge.phyDefPurple = 300000;
		ge.sptDefWhite = 300000;
		ge.sptDefGreen = 300000;
		ge.sptDefBlue = 300000;
		ge.sptDefPurple = 300000;
	
		DataFactory.getInstance().putGameObject(ge);

		GoodsEquip n = (GoodsEquip)DataFactory.getInstance().getGameObject(1051040027);
		
		GoodsEquip ng = new GoodsEquip();
		n.copyTo(ng);
		ng.id = 19860326;
		ng.name = "G.M.GOODS2";
		ng.level = 1;
		ng.type = 1;
		ng.iconId = 1141010001;
		ng.minQuality = 3;
		ng.maxQuality = 3;
		ng.repeatNumber = 1;
		ng.job = "0";
		ng.equipType = 9;
		ng.equipLevel = 1;
		ng.maxEquipLevel = 1;
		ng.phyAtt = 99999999;
		ng.sptAtt = 99999999;
		ge.phyHurtAvoid = 99999999;
		ge.sptHurtAvoid = 99999999;
		ng.modValueWhite = 1000000;
		ng.modValueGreen = 1200000;
		ng.curePoint = 60000;
		ng.isVIP = true;
		ng.startLevel = 0;
		DataFactory.getInstance().putGameObject(ng);
		
		loadIndex();
	}

	private void loadPet()
	{
		try
		{
			ArrayList list = Utils.loadFileVariables(petStr, Pet.class);
		
			for(int i = 0 ; i < list.size() ; i ++)
			{
				Pet pet = (Pet)list.get(i);
				Map upMap = new HashMap();
				File file = new File(petUpStr+pet.id+".txt");
				ArrayList upList = null;
				if(file.exists())
				{
					upList = Utils.loadFileVariables(file, PetUpRule.class);
				}
				if(upList != null)
				{
					for (int j = 0; j < upList.size(); j++) 
					{
						PetUpRule pur = (PetUpRule) upList.get(j);
						upMap.put(pur.level, pur);
					}
				}
				pet.setUpMap(upMap);
				if(DataFactory.getInstance().getGameObject(pet.id) != null)
				{
					System.out.println("load Pet error pet is exits:"+pet.id);
					continue;
				}
				DataFactory.getInstance().putGameObject(pet);
			}
			
			BufferedReader br = new BufferedReader(new FileReader(new File(petGetRuleStr)));
			br.readLine();
			br.readLine();
			String line;
			Map map = new HashMap();
			while((line = br.readLine()) != null)
			{
				String[] strs = Utils.split(line, "\t");
				String[] pets = Utils.split(strs[1], ":");
				for (int i = 0; i < pets.length; i++) 
				{
					GoodsProp prop = (GoodsProp) DataFactory.getInstance().getGameObject(Integer.parseInt(pets[i]));
					if(prop == null || prop.type != 15)
					{
						System.out.println("load petGetRule error:"+pets[i]);
					}
					Pet pet = (Pet) DataFactory.getInstance().getGameObject(prop.petId);
					if(pet == null)
					{
						System.out.println("load petGetRule error petId:"+prop.petId);
					}
				}
				map.put(Integer.parseInt(strs[0]), strs[1]);
			}
			DataFactory.getInstance().putAttachment(DataFactory.GET_PET_RULE, map);
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}



	private void loadBaseFlyer()
	{
		try
		{
			ArrayList list = Utils.loadFileVariables(baseFlyerStr, BaseFlayer.class);
		
			Map bfMap = new HashMap(10);
			for(int i = 0 ; i < list.size() ; i ++)
			{
				BaseFlayer bf = (BaseFlayer)list.get(i);
				bfMap.put(bf.id, bf);
			}
			DataFactory.getInstance().putAttachment(DataFactory.ATTACH_NPC_FLYER, bfMap);

		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	private void loadMailRemind()
	{
		
		try
		{
			ArrayList list = Utils.loadFileVariables(mailRemindStr, MailRemind.class);
			DataFactory.getInstance().putAttachment(DataFactory.ATTACH_MAILREMIND, list);
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		} 
	}

	private void loadExp()
	{
		
		try
		{
			ArrayList list = Utils.loadFileVariables(expStr, Exp.class);
			
			Map expMap = new HashMap();
			
			for(int i = 0 ; i < list.size() ; i ++)
			{
				Exp exp = (Exp)list.get(i);
				expMap.put(exp.level, exp);
				
				if(exp.level > Exp.max_level)
				{
					Exp.max_level = exp.level;
				}
			}
			
			DataFactory.getInstance().putAttachment(DataFactory.ATTACH_EXP, expMap);
			
			ArrayList petExp = Utils.loadFileVariables(petExpStr, PetExp.class);
			Map petExpMap = new HashMap();
			
			for(int i = 0 ; i < petExp.size() ; i ++)
			{
				PetExp exp = (PetExp)petExp.get(i);
				petExpMap.put(exp.level, exp);
				if(i + 1 == petExp.size())
				{
					PetExp.DEFAULTMAXEXP = exp.total;
					PetExp.DEFAULTMAXINTI = exp.totalInt;
				}	
			}
			DataFactory.getInstance().putAttachment(DataFactory.ATTACH_PET_EXP, petExpMap);
			
			ArrayList battlePets = Utils.loadFileVariables(battlePetExpStr, PetExp.class);
			petExpMap = new HashMap();
			for(int i = 0 ; i < battlePets.size() ; i ++)
			{
				PetExp exp = (PetExp)battlePets.get(i);
				petExpMap.put(exp.level, exp);
			}
			DataFactory.getInstance().putAttachment(DataFactory.BATTLE_PET_EXP, petExpMap);
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		} 
	}

	/**
	 * 加载效果
	 */

	private void loadEffect()
	{
		DataFactory dataFactory = DataFactory.getInstance();
		try
		{
			ArrayList timeEffects = Utils.loadFileVariables(timeEffStr, TimeEffect.class);
			
			for(int i = 0 ; i < timeEffects.size() ; i ++)
			{
				TimeEffect timeEffect = (TimeEffect)timeEffects.get(i);
				if(DataFactory.getInstance().getGameObject(timeEffect.id) != null)
				{
					System.out.println("load timeEffect error timeEffect is exits:"+timeEffect.id);
					continue;
				}
				dataFactory.putGameObject(timeEffect);
			}
			
			
			ArrayList flashEffects = Utils.loadFileVariables(flashEffStr, FlashEffect.class);
			
			for(int i = 0 ; i < flashEffects.size() ; i ++)
			{
				FlashEffect flashEffect = (FlashEffect)flashEffects.get(i);
				if(DataFactory.getInstance().getGameObject(flashEffect.id) != null)
				{
					System.out.println("load flashEffect error flashEffect is exits:"+flashEffect.id);
					continue;
				}
				dataFactory.putGameObject(flashEffect);
			}
			
		} 
		catch (InstantiationException e)
		{
			e.printStackTrace();
		} 
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}
	
	private void loadSkill()
	{
		DataFactory dataFactory = DataFactory.getInstance();
		try
		{
			ArrayList askills =  Utils.loadFileVariables(aSkillStr,ActiveSkill.class);
			for(int i = 0 ; i < askills.size() ; i ++)
			{
				ActiveSkill aSkill = (ActiveSkill)askills.get(i);
				if(DataFactory.getInstance().getGameObject(aSkill.id) != null)
				{
					System.out.println("load aSkill error aSkill is exits:"+aSkill.id);
					continue;
				}
				dataFactory.putGameObject(aSkill);
				dataFactory.addSkill(aSkill);
			}
			
			ArrayList pkills =  Utils.loadFileVariables(pSkillStr,PassiveSkill.class);
			for(int i = 0 ; i < pkills.size() ; i ++)
			{
				PassiveSkill pSkill = (PassiveSkill)pkills.get(i);
				if(DataFactory.getInstance().getGameObject(pSkill.id) != null)
				{
					System.out.println("load pSkill error pSkill is exits:"+pSkill.id);
					continue;
				}
				dataFactory.putGameObject(pSkill);
				dataFactory.addSkill(pSkill);
			}
			
			Player player = new Player(true);//初始化技能数量
			for (int i = 0; i < SkillTome.professionCount.length; i++)
			{
				player.profession = i+1;
				
				SkillTome st = (SkillTome)player.getExtPlayerInfo("skillTome");
				List list = st.getProfessionSkills(player);
				
				SkillTome.professionCount[i] = list.size();
			}
			
			
			ArrayList pass =  Utils.loadFileVariables(petActiveSkillStr,PetActiveSkill.class);
			for(int i = 0 ; i < pass.size() ; i ++)
			{
				PetActiveSkill petSkill = (PetActiveSkill)pass.get(i);
				DataFactory.getInstance().putGameObject(petSkill);
				DataFactory.getInstance().addPetSkill(petSkill);
			}
			
			ArrayList ppss =  Utils.loadFileVariables(petPassiveSkillStr,PetPassiveSkill.class);
			for(int i = 0 ; i < ppss.size() ; i ++)
			{
				PetPassiveSkill petSkill = (PetPassiveSkill)ppss.get(i);
				DataFactory.getInstance().putGameObject(petSkill);
				DataFactory.getInstance().addPetSkill(petSkill);
			}
			
			ArrayList psss =  Utils.loadFileVariables(petSkillStudyStr,PetSkillStudy.class);
			DataFactory.getInstance().putAttachment(DataFactory.PET_SKILL_STUDY, psss);
			
			Pet pet = new Pet();
			for (int i = 0; i < 100; i++)//暂定宠物有10种职业
			{
				pet.job = i+1;

				List list = pet.getJobSkills();
				
				Pet.jobSkillMap.put(pet.job, list.size());
			}
		} 
		catch (InstantiationException e)
		{
			e.printStackTrace();
		} 
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 加载世界（区域，房间）
	 */
	private void loadWorldMap()
	{
		XMLNode worldNode = Utils.getXMLNode(worldStr);
        
		XMLNode roomsNode = worldNode.getSubNode("rooms");
		ArrayList roomsList = roomsNode.getSubNodes();
		int roomsSize = roomsList.size();
		
		RoomController [] roomControllers = new RoomController[roomsSize];

		for(int i = 0 ; i < roomsSize ; i ++) 
		{
			XMLNode node = (XMLNode)roomsList.get(i);
			
			roomControllers[i] = new RoomController();
			roomControllers[i].init(node);
			roomControllers[i].setWorld(gameserver.getWorldManager());
			if(DataFactory.getInstance().getGameObject(roomControllers[i].id) != null)
			{
				System.out.println("load roomControllers[i] error roomControllers[i] is exits:"+roomControllers[i].id);
				continue;
			}
			DataFactory.getInstance().putGameObject(roomControllers[i]);
		}
		
		
		XMLNode areaNode = worldNode.getSubNode("areas");
		
		ArrayList areaList = areaNode.getSubNodes();
		int areaSize = areaList.size();
		
		AreaController [] areaControllers = new AreaController[areaSize];
		
		for(int i = 0 ; i < areaSize ;i ++) 
		{
			XMLNode node = (XMLNode)areaList.get(i);
			areaControllers[i] = new AreaController();
			areaControllers[i].setParent(gameserver.getWorldManager());
			areaControllers[i].init(node);
			areaControllers[i].setSmailWorld(areaControllers);
		}
		gameserver.getWorldManager().setAreaController(areaControllers);
	
		for (int i = 0; i < areaControllers.length; i++)
		{
			areaControllers[i].ExitLinking();
		}
		
		
		
	}
	
	/**
	 * 加载NPC到服务器
	 */
	private void loadNpc()
	{
		try
		{
			WorldManager world = gameserver.getWorldManager();
			ArrayList npcs = Utils.loadFileVariables(npcStr, NPC.class);
			int npcSize = npcs.size();
 
			NpcController[] npcControllers = new NpcController[npcSize];
			
			for(int i = 0 ; i < npcSize ; i ++)
			{
				NPC npc = (NPC)npcs.get(i);
				npcControllers[i] = new NpcController();
				npcControllers[i].setWorld(world);
				npcControllers[i].setNpc(npc);
				if(npc.script.equals("answer"))
				{
					npcControllers[i].isAnswerNpc = true;
					DataFactory.answerNpc = npc.id;
				}
				if(!npc.shopId.equals("0") && !npc.shopId.trim().equals("0"))
				{
					String[] strs = Utils.split(npc.shopId, ":");
					for (int j = 0; j < strs.length; j++) 
					{
						int id = Integer.parseInt(strs[j]);
						Shop shop = (Shop) DataFactory.getInstance().getGameObject(id);
						npcControllers[i].setShop(shop);
					}
				}
				
		        AreaController area = (AreaController)world.getAreaById(npc.areaId);
				if(area != null)
				{
					RoomController room = area.getRoomById(npc.roomId);
					if(room != null)
					{
						room.addNpcList(npcControllers[i]);
						npcControllers[i].setParent(room);
					}
				}	
				File file = new File(npcDialogStr+npc.id+".txt");
				XMLNode dialog;
				if(file.exists())
				{
					dialog = Utils.getXMLNode(file);	
				}
				else
				{
				    dialog = Utils.getXMLNode(npcDialogStr+"default.txt");	
				}		
				npcControllers[i].parseDialog(dialog);
				
				
				npcControllers[i].runAttach();
			}	
		} catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("load npc error:"+e.getMessage());
		}

	}

	public void loadDropProp()
	{
		try
		{
			DataFactory datafactory = DataFactory.getInstance(); 
			
			
			File file = new File(monsterDropPropStr);
			if(file.isFile() && file.exists())
			{
				String out = monsterDropPropStr.substring(1, monsterDropPropStr.length()-19);
				DropPropXML.writeXML(monsterDropPropStr,out, "monsterDropProp");
				ArrayList list1 = Utils.loadFileVariables(out+"monsterDropProp", MonsterDropProp.class);
				for (int i = 0; i < list1.size(); i++) 
				{
					MonsterDropProp mdp = (MonsterDropProp) list1.get(i);
					if(DataFactory.getInstance().getGameObject(mdp.id) != null)
					{
						System.out.println("load mdp error mdp is exits:"+mdp.id);
						continue;
					}
					datafactory.putGameObject(mdp);
				}
			}

			
			file = new File(taskDropPropStr);
			if(file.isFile() && file.exists())
			{
				String out = taskDropPropStr.substring(1, taskDropPropStr.length()-16);
				DropPropXML.writeXML(taskDropPropStr, out, "taskDropProp");

				ArrayList list2 = Utils.loadFileVariables(out+"taskDropProp", MonsterDropProp.class);
				for (int i = 0; i < list2.size(); i++) 
				{
					MonsterDropProp mdp = (MonsterDropProp) list2.get(i);
					if(DataFactory.getInstance().getGameObject(mdp.id) != null)
					{
						System.out.println("load mdp error mdp is exits:"+mdp.id);
						continue;
					}
					datafactory.putGameObject(mdp);
				}
			}

			
			ArrayList list3 = Utils.loadFileVariables(boxDropPropStr,BoxDropProp.class);
			for (int i = 0; i < list3.size(); i++) 
			{
				BoxDropProp bdp = (BoxDropProp) list3.get(i);
				file = new File(boxStr);
				File[] files = file.listFiles();
				List ids = new ArrayList();
				List counts = new ArrayList();
				for (int j = 0; j < files.length; j++) 
				{
					if(!files[j].getName().startsWith("box"))
						continue;
					BufferedReader br = new BufferedReader(new FileReader(new File(files[j].getPath())));
					br.readLine();
					br.readLine();
					String line;
					while((line = br.readLine()) != null)
					{
						String[] strs = Utils.split(line, "\t");
						int dropId = Integer.parseInt(strs[0]);
						if(bdp.id != dropId)
							continue;
						if(strs[1].equals("0"))
							continue;
						ids.add(strs[1]);
						try
						{
							counts.add(strs[3]);
						}
						catch(Exception e)
						{
							counts.add(1);
						}
					}
				}
				bdp.setDefault(ids,counts);
				datafactory.addBoxDropProp(bdp);
			}
			
			
			file = new File(bossDropPropStr);
			if(file.isFile() && file.exists())
			{
				String out = bossDropPropStr.substring(1, bossDropPropStr.length()-16);
				DropPropXML.writeXML(bossDropPropStr,out , "bossDropProp");
				ArrayList list4 = Utils.loadFileVariables(out+"bossDropProp", BossDropProp.class);
				for (int i = 0; i < list4.size(); i++) 
				{
					BossDropProp mdp = (BossDropProp) list4.get(i);
					for (int j = 0; j < mdp.propId.length; j++)
					{
						if(mdp.propId[j] == 0)
							continue;
						Goods goods = (Goods) DataFactory.getInstance().getGameObject(mdp.propId[j]);
						if(goods == null)
						{
							System.out.println("Load BossDropProp goods is null:"+mdp.propId[j]+" mdpId:"+mdp.id);
							continue;
						}
					}
					datafactory.putGameObject(mdp);
				}
			}
			

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("load dropProp error:"+e.getMessage());
		} 
	}
	
	
	/**
	 * 加载怪物
	 */
	public void loadMonster()
	{
		try
		{
			ArrayList list = Utils.loadFileVariables(monsterStr, Monster.class);
			ArrayList listAI = Utils.loadFileVariables(monsterAiStr, MonsterAI.class);
			
			DataFactory datafactory = DataFactory.getInstance(); 
			int monsterSize = list.size();
			
			for(int i = 0 ; i < monsterSize ; i ++)
			{
				Monster monster = (Monster)list.get(i);
				monster.updateMoreValue();
				for (int j = 0; j < listAI.size(); j++) 
				{
					MonsterAI monsterAI = (MonsterAI)listAI.get(j);
					if(monsterAI.monsterId == monster.id)
					{
						monster.setMonsterAI(monsterAI);
						break;
					}
				}
				MonsterDropProp mdp = null;
				if(monster.normalDropRate != 0)
				{
					mdp = (MonsterDropProp) datafactory.getGameObject(monster.normalDropRate);
					monster.setMonsterDropProp(mdp);
				}
				if(monster.questDropRate != 0)
				{
					mdp = (MonsterDropProp) datafactory.getGameObject(monster.questDropRate);
					monster.setTaskDropProp(mdp);
				}
				if(DataFactory.getInstance().getGameObject(monster.id) != null)
				{
					System.out.println("load monster error monster is exits:"+monster.id);
					continue;
				}
				datafactory.putGameObject(monster);
			}
			
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
			System.out.println("load monster error:"+e.getMessage());
		} 
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
			System.out.println("load monster error:"+e.getMessage());
		}
	}
	
	/**
	 * 加载怪物组
	 */
	private void loadMonsterGroup()
	{
		try
		{
			ArrayList list = Utils.loadFileVariables(monsterGroupStr, MonsterGroupController.class);			
			int size = list.size();
			DataFactory datafactory = DataFactory.getInstance();
			for(int i = 0 ; i < size ; i++)
			{
				MonsterGroupController monsterGroupController	= (MonsterGroupController)list.get(i);
				datafactory.putGameObject(monsterGroupController);
			}
			
			WorldManager world = gameserver.getWorldManager();
			
			AreaController [] areas = world.getAreaControllers();
			int areaLength = areas.length;
			for(int i = 0 ; i < areaLength ; i++)
			{
				RoomController [] rooms = areas[i].getRooms();
				int roomLength = rooms.length;
				
				for(int j = 0 ; j < roomLength ; j ++)
				{
					ArrayList nodeList= rooms[j].getMonstersNode();
					
					if(nodeList == null)
						continue;
					
					int groupSize = nodeList.size();
					for(int k = 0 ; k < groupSize ; k++)
					{
						XMLNode xmlnode = (XMLNode)nodeList.get(k);
						int groupId = Integer.parseInt(xmlnode.getAttributeValue("id"));
						String x = xmlnode.getAttributeValue("x");
						String y = xmlnode.getAttributeValue("y");
						int minX=0,maxX=0,minY=0,maxY=0,groupX=0,groupY=0;
						try{groupX = Integer.parseInt(xmlnode.getAttributeValue("x"));}
						catch(Exception e)
						{
							String[] strs = Utils.split(x, "-");
							minX = Integer.parseInt(strs[0]);
							maxX = Integer.parseInt(strs[1]);
							if(minX > maxX)
								System.out.println("loadMonsterGroup error minX>maxX!"+groupId+"  "+rooms[j].id);
						}
						try{groupY = Integer.parseInt(xmlnode.getAttributeValue("y"));}
						catch(Exception e)
						{
							String[] strs = Utils.split(y, "-");
							minY = Integer.parseInt(strs[0]);
							maxY = Integer.parseInt(strs[1]);
							if(minY > maxY)
								System.out.println("loadMonsterGroup error minY>maxY!"+groupId+"  "+rooms[j].id);
						}
						
						MonsterGroupController mc = (MonsterGroupController)datafactory.getGameObject(groupId);
						if(mc == null)
						{
							System.out.println("MonsterGroupController "+groupId+" can not find");
							continue;
						}
						mc.minX = minX;
						mc.maxX = maxX;
						mc.minY = minY;
						mc.maxY = maxY;
						if(groupX == 0)
							groupX = (int) (Math.random() * (maxX - minX) + minX);
						if(groupY == 0)
							groupY = (int) (Math.random() * (maxY - minY) + minY);
						MonsterGroupController nMc = new MonsterGroupController();
						nMc.areaId = areas[i].id;
						nMc.roomId = rooms[j].id;
						nMc.x = groupX;
						nMc.y = groupY;
						nMc.setParent(rooms[j]);
						mc.copyTo(nMc);
						nMc.objectIndex = MonsterGroupController.publicObjectIndex++;
						rooms[j].addMonsterGroupList(nMc);

						//服务器启动就隐藏掉时间怪
			 			nMc.setVisibled(rooms[j],3);

					}
					
				}
			}
		} 
		catch (InstantiationException e)
		{
			e.printStackTrace();
			System.out.println("load monsterGroup error:"+e.getMessage());
		} 
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
			System.out.println("load monsterGroup error:"+e.getMessage());
		}
	}
	
	/**
	 * 加载装备 
	 */
	public void loadGoodsEquip()
	{
		try 
		{
			ArrayList equips = Utils.loadFileVariables(equipmentStr, GoodsEquip.class);
			int size = equips.size();

			for (int i = 0; i < size; i++) 
			{
				GoodsEquip goods = (GoodsEquip)equips.get(i);
				if(DataFactory.getInstance().getGameObject(goods.id) != null)
				{
					System.out.println("load GoodsEquip equip exits:"+goods.id);
					continue;
				}
				DataFactory.getInstance().putGameObject(goods);
			}
			
			equips = Utils.loadFileVariables(petEquipmentStr, GoodsPetEquip.class);
			size = equips.size();

			for (int i = 0; i < size; i++) 
			{
				GoodsPetEquip goods = (GoodsPetEquip)equips.get(i);
				if(DataFactory.getInstance().getGameObject(goods.id) != null)
				{
					System.out.println("load GoodsEquip equip exits:"+goods.id);
					continue;
				}
				DataFactory.getInstance().putGameObject(goods);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("load equip error:"+e.getMessage());
		}
	}
	
	/**
	 * 加载道具
	 */
	public void loadGoodsProp()
	{
		try {
//			StringBuffer sb1 = new StringBuffer();
//			StringBuffer sb2 = new StringBuffer();
			ArrayList props = Utils.loadFileVariables(propStr, GoodsProp.class);
			int size = props.size();
			for (int i = 0; i < size; i++) 
			{
				GoodsProp goods = (GoodsProp)props.get(i);
				goods.quality = (goods.minQuality == goods.maxQuality?goods.minQuality:0);
				if(DataFactory.getInstance().getGameObject(goods.id) != null)
				{
					System.out.println("load GoodsProp prop exits:"+goods.id);
					continue;
				}
//				if(goods.isDel)
//				{	
//					if(sb1.length() == 0)
//						sb1.append(goods.id+"\t"+goods.name);
//					else
//						sb1.append("\n"+goods.id+"\t"+goods.name);
//				}
//				else
//				{	
//					if(sb2.length() == 0)
//						sb2.append(goods.id+"\t"+goods.name);
//					else
//						sb2.append("\n"+goods.id+"\t"+goods.name);
//				}
				DataFactory.getInstance().putGameObject(goods);
			}
//			Utils.writeFile("E:\\可以丢弃的道具.txt", sb1.toString().getBytes());
//			Utils.writeFile("E:\\不可以丢弃的道具.txt", sb2.toString().getBytes());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("load props error:"+e.getMessage());
		}
	}
	
	private void loadTask()
	{
		File file = new File(taskStr);
		File [] taskfiles = file.listFiles();
		
		for(int i = 0 ; i < taskfiles.length ; i ++)
		{
			if(taskfiles[i] == null)
				continue;
			
			if(!taskfiles[i].toString().endsWith("txt"))
				continue;
			
			XMLNode taskNode = Utils.getXMLNode(taskfiles[i]);
			Task task = new Task();
			task.setWorld(gameserver.getWorldManager());
			task.parseTask(taskNode);
			task.initial();

			TaskManager.getInstance().putTask(task);
		}
	}
	
	/**
	 * 加载商店
	 */
	public void loadShop()
	{
		try
		{
			String MATCH = "shop\\d++\\.txt";
			File file = new File(shopStr);
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) 
			{
				if(!files[i].getName().matches(MATCH))
					continue;
				List goodsList = new ArrayList(200);
				BufferedReader br = new BufferedReader(new FileReader(new File(files[i].getPath())));
				br.readLine();
				br.readLine();
				String line;
				HashMap buyRate = new HashMap();
				HashMap saleRate = new HashMap();
				HashMap honourPoint = new HashMap();
				HashMap sourceIds = new HashMap();
				
				while((line = br.readLine()) != null)
				{
					String[] gos = Utils.split(line, "\t");
					int goodsId = Integer.parseInt(gos[0]);
					int honour = 0;
					if(gos.length == 5)
					{
						honour = Integer.parseInt(gos[4]);
					}
					Goods goods = (Goods) DataFactory.getInstance().getGameObject(goodsId);
					Goods newGoods = null;
					if(goods instanceof GoodsEquip)
					{
						GoodsEquip equip = (GoodsEquip) Goods.cloneObject(goods);
						if(files[i].getName().equals("shop1000000000.txt") || files[i].getName().equals("shop2000000000.txt")
						|| files[i].getName().equals("shop1000000001.txt") || files[i].getName().equals("shop2000000002.txt"))
						{
							newGoods = equip.makeNewBetterEquip(equip.taskColor==-1?0:equip.taskColor);
						}
						else
						{
							newGoods = equip.makeNewBetterEquip(0);
						}
					}
					else if(goods instanceof GoodsPetEquip)
						newGoods = (Goods) Goods.cloneObject(goods);
					else if(goods instanceof GoodsProp)
						newGoods = (Goods) Goods.cloneObject(goods);
					if(newGoods == null)
					{
						System.out.println("LocalDataLoader shop goods is null:"+goodsId+"  "+files[i].getName());
						continue;
					}
					goodsList.add(newGoods);
					if(files[i].getName().equals("shop1000000002.txt"))//礼券商店
					{
						sourceIds.put(goodsId, gos[1]);
						saleRate.put(goodsId, 100);
						buyRate.put(goodsId, 100);
					}
					else
					{
						saleRate.put(goodsId, gos[2]);
						buyRate.put(goodsId, gos[3]);
					}
					honourPoint.put(goodsId, honour);
				}
				
				String str = files[i].getName().substring(4);
				int shopId = Integer.parseInt(str.substring(0, str.indexOf(".")));
				Shop shop = new Shop(shopId,goodsList,saleRate,buyRate,honourPoint,sourceIds);
				if(DataFactory.getInstance().getGameObject(shop.id) != null)
				{
					System.out.println("load shop error shop is exits:"+shop.id);
					continue;
				}
				DataFactory.getInstance().putGameObject(shop);
				GameServer.getInstance().getWorldManager().setShopList(shop);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("load shop error:"+e.getMessage());
		}
	}

	private void loadExtShop()
	{
		try
		{
			ArrayList list1 = Utils.loadFileVariables(equipSShopStr, GoodsSynt.class);
			ArrayList list2 = Utils.loadFileVariables(equipDShopStr, GoodsSynt.class);
			ArrayList list3 = Utils.loadFileVariables(goodsUpStr, GoodsUp.class);
			ArrayList list4 = Utils.loadFileVariables(goodsEoStr, GoodsUp.class);
			
			SShop ss = SShop.getShop();
			
			for(int i = 0 ; i < list1.size() ; i ++)
			{
				GoodsSynt gs = (GoodsSynt)list1.get(i);
				if(ss.getEquipSynt(gs.gsId) != null)
				{
					System.out.println("load extShop error goodsSynt is exits:"+gs.gsId);
					continue;
				}
				ss.addEquipSynt(gs);
			}
			
			for (int i = 0; i < list2.size(); i++)
			{
				GoodsSynt gs = (GoodsSynt)list2.get(i);
				if(ss.getEquipDisa(gs.id) != null)
				{
					System.out.println("load extShop error goodsSynt is exits:"+gs.id);
					continue;
				}
				ss.addEquipDisa(gs);
			}
			
			for (int i = 0; i < list3.size(); i++)
			{
				GoodsUp gu = (GoodsUp)list3.get(i);
				if(ss.getGoodsUpBySourceId(gu.sourceId) != null)
				{
					System.out.println("load extShop error goodsUp sourceId is exits:"+gu.sourceId);
					continue;
				}
				ss.addGoodsUp(gu);
			}
			
			for (int i = 0; i < list4.size(); i++)
			{
				GoodsUp gu = (GoodsUp)list4.get(i);
				if(ss.getGoodsEoBySourceId(gu.sourceId) != null)
				{
					System.out.println("load extShop error goodsEo sourceId is exits:"+gu.sourceId);
					continue;
				}
				ss.addGoodsGold(gu);
			}

			SShop.getShop().setWorld(gameserver.getWorldManager());
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
	
	/**
	 * 读取出生初始化配置文件信息
	 */
	private void loadNativity()
	{
		XMLNode nativityNode = Utils.getXMLNode(nativityStr);
		ArrayList nativitys = nativityNode.getSubNodes();
		DataFactory dataFactory = DataFactory.getInstance();
		dataFactory.putAttachment(DataFactory.ATTACH_NATIVITY, nativitys);
	}
	
	/**
	 * 读取房间进入的时间限制 
	 */
	public void loadTimeRoomCondition()
	{
		if(!new File(timeRoomConditionStr).isFile())
			return;
		
		Map map = new HashMap();
		
		timeRoomConditionStr = Utils.readFile2(timeRoomConditionStr);
		String lines1 [] = Utils.split(timeRoomConditionStr,Utils.LINE_SEPARATOR);
		
		String lines2 [] = null;
	
		for (int i = 0; i < lines1.length; i++)
		{
			if(lines1[i].equals(""))
				continue;
			lines2 = Utils.split(lines1[i],"\t");
			int roomId = Integer.parseInt(lines2[0]);
			if(roomId == DataFactory.INITROOM)
			{
				System.out.println("loadTimeRoomCondition error:"+roomId);
			}
			map.put(lines2[0], lines2[1]);
		}

		DataFactory.getInstance().putAttachment(DataFactory.TIME_CONDITION,map);
	}

	
}
