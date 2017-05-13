package cc.lv1.rpg.gs.entity.impl;

import vin.rabbit.comm.GameObject;
import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.impl.ArrayList;
import cc.lv1.rpg.gs.entity.controller.BattleController;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.ext.EquipSet;
import cc.lv1.rpg.gs.entity.impl.battle.MonsterBattleTmp;
import cc.lv1.rpg.gs.entity.impl.battle.PlayerBattleTmp;

public class MonsterAI extends GameObject
{
	/** 条件包1 */
	public String firstCondition;
	
	public String firstTarget;
	
	public String firstSkill;
	
	/** 条件包2 */
	public String secondCondition;
	
	public String secondTarget;
	
	public String secondSkill;
	
	/** 条件包3 */
	public String thirdCondition;
	
	public String thirdTarget;
	
	public String thirdSkill;
	
	/** 条件包4(预留) */
	public String fourthCondition;
	
	public String fourthTarget;
	
	public String fourthSkill;
	
	/** 怪物ID */
	public int monsterId;
	
	public int condition;
	public int target;
	public int skill = 1031010001;
	
	/** 对怪物释放负面BUFF的玩家 */
	private int negBuffPlayer = -1;

	/** 给其它玩家加血的玩家 */
	private int addLifeToOtherPlayer = -1;
	
	/** 恢复血量的玩家 */
	private int comebackLifePlayer = -1;
	
	
	public void copyTo(GameObject go)
	{
		super.copyTo(go);
		
		MonsterAI ma = (MonsterAI) go;
		ma.firstCondition = firstCondition;
		ma.secondCondition = secondCondition;
		ma.thirdCondition = thirdCondition;
		ma.fourthCondition = fourthCondition;
		ma.monsterId = monsterId;
		ma.negBuffPlayer = negBuffPlayer;
		ma.addLifeToOtherPlayer = addLifeToOtherPlayer;
		ma.comebackLifePlayer = comebackLifePlayer;
		ma.condition = condition;
		ma.target = target;
		ma.skill = skill;
	}
	/**
	 * 设置默认的条件，目标，技能
	 * @param conditons
	 * @param targets
	 * @param skills
	 */
	public void setDefault(String conditons,String targets,String skills,int random)
	{
		String[] con = Utils.split(conditons, ":");
		String[] tar = Utils.split(targets, ":");
		String[] ski = Utils.split(skills, ":");
		
		condition = Integer.parseInt(con[random]);
		target = Integer.parseInt(tar[random]);
		skill = Integer.parseInt(ski[random]);
	}
	
	public int getRandom()
	{
		int random = (int) (Math.random() * 3);
		return random;
	}
	
	public int getCondition(String conditions,int random)
	{
		String[] cons = Utils.split(conditions, ":");
		return Integer.parseInt(cons[random]);
	}
	
	public int getSkillId(int con)
	{
		String[] cons = Utils.split(firstCondition, ":");
		int sk = 0;
		for (int i = 0; i < cons.length; i++) 
		{
			if(cons[i].equals(String.valueOf(con)))
			{
				sk = i;
				break;
			}
		}
		String[] sks = Utils.split(firstSkill, ":");
		return Integer.parseInt(sks[sk]);
	}

	
	/**
	 * 怪物是否有仇恨值大于0
	 * @param monster
	 * @return
	 */
	public boolean isOne(MonsterBattleTmp monster)
	{
		return (monster.hatOne > 0 || monster.hatTwo > 0 || monster.hatThree > 0 
				|| monster.hatFour > 0 || monster.hatFive > 0);
	}
	
	
	/**
	 * 对某玩家的仇恨值达到某个值(500或者1000)
	 * @param monster
	 * @param playerList
	 * @return
	 */
	public boolean isTwoAndFive(int hatred,MonsterBattleTmp monster)
	{
		return (monster.hatOne >= hatred || monster.hatTwo >= hatred || monster.hatThree >= hatred
				|| monster.hatFour >= hatred || monster.hatFive >= hatred);
	}
	
	/**
	 * 是否有玩家等级比怪物低1500级
	 * @param monster
	 * @param playerList
	 * @return
	 */
	public boolean isThree(Monster monster,List playerList)
	{
		for (int i = 0; i < playerList.size(); i++) 
		{
			if(playerList.get(i) == null)
				continue;
			PlayerController player = (PlayerController) playerList.get(i);
			if(player.getPlayer().level + 1500 <= monster.level)
				return true;
		}
		return false;
	}
	
	/**
	 * 有玩家的血量少于10%
	 * @param playerList
	 * @return
	 */
	public boolean isFour(List playerList)
	{
		for (int i = 0; i < playerList.size(); i++) 
		{
			if(playerList.get(i) == null)
				continue;
			PlayerController player = (PlayerController) playerList.get(i);
			PlayerBattleTmp pbt = (PlayerBattleTmp) player.getAttachment();
			int mhp = pbt.maxHitPoint;
			if(player.getPlayer().hitPoint <= mhp * 0.1)
				return true;
		}
		return false;
	}
	
	/**
	 * 怪物的血量少于20%
	 * @param monster
	 * @return
	 */
	public boolean isSix(Monster monster)
	{
		return (monster.hitPoint <= monster.maxHitPoint * 0.2);
	}
	
	/**
	 * 怪物对某玩家的仇恨值一次性增加了100以上
	 * @param monster
	 * @return
	 */
	public boolean isSeven(MonsterBattleTmp monster,List playerList)
	{
		return (monster.addHatOne >= 100 || monster.addHatTwo >= 100 || monster.addHatThree >= 100
				|| monster.addHatFour >=100 || monster.addHatFive >= 100);
	}
	
	/**
	 * 是否遭受负面BUFF技能
	 * @return
	 */
	public boolean isEight()
	{
		return (negBuffPlayer != -1);
	}
	
	/**
	 * 是否有玩家恢复血量
	 * @return
	 */
	public boolean isNine()
	{
		return (comebackLifePlayer != -1);
	}
	
	/**
	 * 怪物在本场战斗攻击次数是否超过10次
	 * @return
	 */
	public boolean isTen(MonsterBattleTmp mbp)
	{
		return (mbp.attackCount >= 10);
	}
	
	/**
	 * 怪物在本场战斗攻击次数是否超过50次
	 * @return
	 */
	public boolean isEleven(MonsterBattleTmp mbp)
	{
		return (mbp.attackCount >= 50);
	}
	
	/**
	 * 怪物在本场战斗攻击次数是否超过100次
	 * @return
	 */
	public boolean isTwelve(MonsterBattleTmp mbp)
	{
		return (mbp.attackCount >= 100);
	}
	
	/**
	 * 怪物在本场战斗攻击次数是否超过200次
	 * @return
	 */
	public boolean isThirteen(MonsterBattleTmp mbp)
	{
		return (mbp.attackCount >= 200);
	}
	
	/**
	 * 跳过
	 * @return
	 */
	public boolean isFourteen()
	{
		return false;
	}
	
	/**
	 * 怪物在本场战斗攻击次数是否超过20次
	 * @return
	 */
	public boolean isFifteen(MonsterBattleTmp mbp)
	{
		return (mbp.attackCount >= 20);
	}
	
	/**
	 * 怪物在本场战斗攻击次数是否超过40次
	 * @return
	 */
	public boolean isSixteen(MonsterBattleTmp mbp)
	{
		return (mbp.attackCount >= 40);
	}
	
	/**
	 * 怪物在本场战斗攻击次数是否超过60次
	 * @return
	 */
	public boolean isSeventeen(MonsterBattleTmp mbp)
	{
		return (mbp.attackCount >= 60);
	}
	
	
	public List getPlayerList(List list)
	{
		List playerList = new ArrayList();
		for (int i = 0; i < list.size(); i++) 
		{
			PlayerController target = (PlayerController) list.get(i);
			if(target == null)
				continue;
			if(target.getPlayer().hitPoint <= 0)
				continue;
			PlayerBattleTmp pbt = (PlayerBattleTmp) target.getAttachment();
			if(pbt == null)
				continue;
			if(pbt.isDead())
				continue;
			playerList.add(target);
		}
		return playerList;
	}
	/**
	 * 获取怪物攻击目标(生命最少的玩家)
	 * @param playerList
	 * @return
	 */
	public PlayerController getTargetOne(List list)
	{
		List playerList = getPlayerList(list);
		PlayerController player = (PlayerController) playerList.get(0);
		for (int i = 0; i < playerList.size(); i++) 
		{
			PlayerController target = (PlayerController) playerList.get(i);
			if(target.getPlayer().hitPoint < player.getPlayer().hitPoint)
				player = target;
		}
//		if(player.getPlayer().hitPoint > 0)
//			return player;
		return player;
	}
	
	
	/**
	 * 获取怪物攻击目标(等级最低的玩家)
	 * @param playerList
	 * @return
	 */
	public PlayerController getTargetTwo(List list)
	{
		List playerList = getPlayerList(list);
		PlayerController player = (PlayerController) playerList.get(0);
		if(player == null)
			return null;
		for (int i = 0; i < playerList.size(); i++) 
		{
			PlayerController target = (PlayerController) playerList.get(i);
			if(player.getPlayer().level > target.getPlayer().level)
			{
				player = target;
			}
		}
//		if(player.getPlayer().hitPoint > 0)
//			return player;
		return player;
	}
	

	
	/**
	 * 获取怪物攻击目标(仇恨值最高的玩家)
	 * @param monster
	 * @param playerList
	 * @return
	 */
	public PlayerController getTargetThree(MonsterBattleTmp monster,List list)
	{
		List playerList = getPlayerList(list);
		int max = getMax(getMax(getMax(getMax(monster.hatOne, monster.hatTwo),
				          monster.hatThree), monster.hatFour), monster.hatFive);
		Object target = null;
		if(max == 0)
			return null;
		if(max == monster.hatOne)
			target = playerList.get(0);
		else if(max == monster.hatTwo)
			target = playerList.get(1);
		else if(max == monster.hatThree)
			target = playerList.get(2);
		else if(max == monster.hatFour)
			target = playerList.get(3);
		else if(max == monster.hatFive)
			target = playerList.get(4);
		if(target == null)
			return null;
		PlayerController player = (PlayerController) target;
//		if(player.getPlayer().hitPoint > 0 && player != null)
//			return player;
//		else 
//			return player;
		return player;
			
	}
	
	/**
	 * 获取怪物攻击目标(仇恨值最低的玩家)
	 * @param monster
	 * @param playerList
	 * @return
	 */
	public PlayerController getTargetFour(MonsterBattleTmp monster,List list)
	{
		List playerList = getPlayerList(list);
		int min = getMin(getMin(getMin(getMin(monster.hatOne, monster.hatTwo),
				          monster.hatThree), monster.hatFour), monster.hatFive);
		Object target = null;
		if(min == 0)
			return null;
		if(min == monster.hatOne)
			target = playerList.get(0);
		else if(min == monster.hatTwo)
			target = playerList.get(1);
		else if(min == monster.hatThree)
			target = playerList.get(2);
		else if(min == monster.hatFour)
			target = playerList.get(3);
		else if(min == monster.hatFive)
			target = playerList.get(4);
		if(target == null)
			return null;
		PlayerController player = (PlayerController) target;
//		if(player.getPlayer().hitPoint > 0 && player != null)
//			return player;
//		else 
//			return null;
		return player;
			
	}
	
	/**
	 * 获取怪物攻击目标(仇恨值高于500或者1000)
	 * @param monster
	 * @param playerList
	 * @return
	 */
	public PlayerController getTargetFiveAndSix(int hatred,MonsterBattleTmp monster,List list)
	{
		List playerList = getPlayerList(list);
		String str = "";
		if(monster.hatOne >= hatred)
			str += "0";
		if(monster.hatTwo >= hatred)
		{
			if(str.isEmpty())
				str += "1";
			else 
				str += ":1";
		}
		if(monster.hatThree >= hatred)
		{
			if(str.isEmpty())
				str += "2";
			else 
				str += ":2";
		}
		if(monster.hatFour >= hatred)
		{
			if(str.isEmpty())
				str += "3";
			else 
				str += ":3";
		}
		if(monster.hatFive >= hatred)
		{
			if(str.isEmpty())
				str += "4";
			else 
				str += ":4";
		}
		if(str.isEmpty())
			return null;
		String[] con = Utils.split(str, ":");
		int random = (int) (Math.random() * con.length);
		int cond = Integer.parseInt(con[random]);
		if(playerList.get(cond) == null)
			return null;
		PlayerController player = (PlayerController) playerList.get(cond);
//		if(player.getPlayer().hitPoint > 0 && player != null)
//			return player;
//		else 
//			return null;
		return player;
	}

	/**
	 * 获取怪物攻击目标(上次仇恨值增加超过100的)
	 * @return
	 */
	public PlayerController getTargetSeven(MonsterBattleTmp monster,List list)
	{
		List playerList = getPlayerList(list);
		List targetList = new ArrayList(playerList.size());
		if(monster.addHatOne >= 100)
			targetList.add(playerList.get(0));
		if(monster.addHatTwo >= 100)
			targetList.add(playerList.get(1));
		if(monster.addHatThree >= 100)
			targetList.add(playerList.get(2));
		if(monster.addHatFour >= 100)
			targetList.add(playerList.get(3));
		if(monster.addHatFive >= 100)
			targetList.add(playerList.get(4));
		int random = (int) (Math.random() * targetList.size());
		if(targetList.get(random) == null)
			return null;
		PlayerController player = (PlayerController) targetList.get(random);
//		if(player.getPlayer().hitPoint > 0 && player != null)
//			return player;
//		else 
//			return null;
		return player;
	}
	
	/**
	 * 获取怪物攻击目标(随机选取医生，护士职业)
	 * @param playerList
	 * @return
	 */
	public PlayerController getTargetEight(List list)
	{
		List playerList = getPlayerList(list);
		List targetList = new ArrayList(playerList.size());
		for (int i = 0; i < playerList.size(); i++) 
		{
			if(playerList.get(i) == null)
				continue;
			PlayerController player = (PlayerController) playerList.get(i);
//			if(player.getPlayer().upProfession == 6)
			if(player.getPlayer().profession == 3)
				targetList.add(player);
		}
		int random = (int) (Math.random() * targetList.size());
		if(targetList.get(random) == null)
			return null;
		PlayerController player = (PlayerController) targetList.get(random);
//		if(player.getPlayer().hitPoint > 0 && player != null)
//			return player;
//		else 
//			return null;
		return player;
	}
	
	/**
	 * 获取怪物攻击目标(随机选取有恢复血量的玩家)
	 * @return
	 */
	public PlayerController getTargetNine(List list)
	{
		List playerList = getPlayerList(list);
		if(comebackLifePlayer != -1)
		{
			PlayerController target = (PlayerController) playerList.get(comebackLifePlayer);
			if(target != null && target.getAttachment() != null && !target.getAttachment().isDead())
				return target;
		}
		return null;	
	}
	
	/**
	 * 获取怪物攻击目标(上次仇恨值增加最多的玩家)
	 * @return
	 */
	public PlayerController getTargetTen(MonsterBattleTmp monster,List list)
	{
		List playerList = getPlayerList(list);
		Object target = null;
		int max = getMax(getMax(getMax(getMax(monster.addHatOne, monster.addHatTwo), 
				monster.addHatThree), monster.addHatFour), monster.addHatFive);
		if(max != 0)
		{
			if(max == monster.addHatOne)
				target = playerList.get(0);
			else if(max == monster.addHatTwo)
				target = playerList.get(1);
			else if(max == monster.addHatThree)
				target = playerList.get(2);
			else if(max == monster.addHatFour)
				target = playerList.get(3);
			else if(max == monster.addHatFive)
				target = playerList.get(4);
		}
		if(target == null)
			return null;
		PlayerController player = (PlayerController) target;
//		if(player.getPlayer().hitPoint > 0 && player != null)
//			return player;
//		else 
//			return null;
		return player;
	}
	
	/**
	 * 获取怪物攻击目标(有给其它玩家加血的玩家)
	 * @return
	 */
	public PlayerController getTargetEleven(List list)
	{
		List playerList = getPlayerList(list);
		if(addLifeToOtherPlayer != -1)
		{
			PlayerController target = (PlayerController) playerList.get(addLifeToOtherPlayer);
			if(target != null && target.getAttachment() != null && !target.getAttachment().isDead())
				return target;
		}
		return null;
	}
	
	/**
	 * 获取怪物攻击目标(有对怪物释放负面BUFF的玩家)
	 * @return
	 */
	public PlayerController getTargetTwelve(List list)
	{
		List playerList = getPlayerList(list);
		if(negBuffPlayer != -1)
		{
			PlayerController target = (PlayerController) playerList.get(negBuffPlayer);
			if(target != null && target.getAttachment() != null && !target.getAttachment().isDead())
				return target;
		}
		return null;
	}

	
	/**
	 * 获取怪物攻击目标(随机选取血量低于2%的玩家)
	 * @param playerList
	 * @return
	 */
	public PlayerController getTargetThirteen(List list)
	{
		List playerList = getPlayerList(list);
		for (int i = 0; i < playerList.size(); i++) 
		{
			if(playerList.get(i) == null)
				continue;
			PlayerController player = (PlayerController) playerList.get(i);
			PlayerBattleTmp pbt = (PlayerBattleTmp) player.getAttachment();
			int mhp = pbt.maxHitPoint;
			if(player.getPlayer().hitPoint <= mhp * 0.1 
					&& player.getPlayer().hitPoint > 0 && player != null && player.getAttachment() != null && !player.getAttachment().isDead())
					return player;
		}
		return null;
	}


	/**
	 * 随机获取怪物的攻击目标
	 * @param playerList
	 * @return
	 */
	public PlayerController getRandomTarget(List list)
	{
		List playerList = getPlayerList(list);
		for (int i = 0; i < playerList.size(); i++) 
		{
			PlayerController player = (PlayerController) playerList.get(i);
			if(player.getPlayer().hitPoint > 0  && player != null && player.getAttachment() != null && !player.getAttachment().isDead())
				return player;
		}
		return null;
	}
	 
	
	public int getMax(int one,int two)
	{
		return (one > two ? one : two);
	}
	
	public int getMin(int one,int two)
	{
		return (one < two ? one : two);
	}

	public int getNegBuffPlayer() {
		return negBuffPlayer;
	}

	public void setNegBuffPlayer(int negBuffPlayer) {
		this.negBuffPlayer = negBuffPlayer;
	}

	public int getAddLifeToOtherPlayer() {
		return addLifeToOtherPlayer;
	}

	public void setAddLifeToOtherPlayer(int addLifeToOtherPlayer) {
		this.addLifeToOtherPlayer = addLifeToOtherPlayer;
	}

	public int getComebackLifePlayer() {
		return comebackLifePlayer;
	}

	public void setComebackLifePlayer(int comebackLifePlayer) {
		this.comebackLifePlayer = comebackLifePlayer;
	}
	
	public void setComebackLifePlayer(PlayerController player)
	{
		
	}
	

}
