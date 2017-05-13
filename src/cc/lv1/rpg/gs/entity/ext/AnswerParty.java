package cc.lv1.rpg.gs.entity.ext;


import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.data.DC;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.controller.BattleController;
import cc.lv1.rpg.gs.entity.controller.NpcController;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.impl.Goods;
import cc.lv1.rpg.gs.entity.impl.Mail;
import cc.lv1.rpg.gs.entity.impl.RankReward;
import cc.lv1.rpg.gs.entity.impl.Reward;
import cc.lv1.rpg.gs.entity.impl.answer.AnswerReward;
import cc.lv1.rpg.gs.entity.impl.answer.Guide;
import cc.lv1.rpg.gs.entity.impl.answer.Question;
import cc.lv1.rpg.gs.net.SMsg;
import cc.lv1.rpg.gs.other.ErrorCode;
import cc.lv1.rpg.gs.util.FontConver;
import vin.rabbit.net.AppMessage;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.i.Map;
import vin.rabbit.util.collection.impl.ArrayList;
import vin.rabbit.util.collection.impl.HashMap;

/**
 * 问答活动相关信息
 * @author bean
 *
 */
public class AnswerParty extends PlayerExtInfo
{ 
	public static final int CHANGEEXPLEVEL = 1000;
	/** 免费答题 */
	public static final int FREE_ANSWER = 0;
	/** 金币答题 */
	public static final int POINT_ANSWER = 1;
	/** 元宝答题 */
	public static final int MONEY_ANSWER = 2;
	/** 继续答题 */
	public static final int CONTINUE = 3;
	/** 不能答题 */
	public static final int NOANSWER = 4;
	/** 非答题时间 */
	public static final int NOANSWERTIME = 5;
	
	/** 答题结束时间 */
	public static final int ANSWER_OVER_TIME = 22;
	/** 发放奖励时间 */
	public static final int REWARDTIME = 23;
	
//	/**  一轮的题目的数量 */
//	public static final int ROUND = 18;
//	public static final int TYPEONE = 3;
//	public static final int TYPETWO = 7;
//	public static final int TYPETHREE = 8;
	
	public static final int ROUND = 36;//国庆时改成36
	public static final int TYPEONE = 3;//国庆时改
	public static final int TYPETWO = 16;//国庆时改
	public static final int TYPETHREE = 17;//国庆时改
	
//	/** 前几次是免费 */
//	public static final int FREE_COUNT = 3 * ROUND;
//	/** 前几次是金币(除开免费的次数) */
//	public static final int POINT_COUNT = 5 * ROUND;
//	/** 前几次是元宝(除开免费和金币的次数)*/
//	public static final int MONEY_COUNT = 6 * ROUND;
	
	/** 需要游戏币 */
	public static final int NEEDPOINT = 10000;
	/** 需要元宝 */
	public static final int NEEDMONEY = 10;
	/** 需要游戏币 */
	public static final int NEEDNEXTPOINT = 100000;
	
	/** 当天累计获得经验 */
	public long expCount;
	
	/** 当天累计获得积分 */
	public int pointCount;
	
	/** 获得的图案 */
	public byte[] pics = new byte[3];
	
	/** 累计答题数量 */
	public int answerCount;
	
	/** 一轮的题目 */
	public Question[] questions;
	
	/** 当天回答到第几轮 */
	public int roundNumber;
	
	/** 第一次答题时间 */
	public long answerTime;
	
	

	@Override
	public String getName()
	{
		return "answerParty";
	}
	
	@Override
	public void loadFrom(ByteBuffer byteBuffer) 
	{
		expCount = byteBuffer.readLong();
		pointCount = byteBuffer.readInt();
		pics[0] = (byte) byteBuffer.readByte();
		pics[1] = (byte) byteBuffer.readByte();
		pics[2] = (byte) byteBuffer.readByte();
		answerCount = byteBuffer.readInt();
		roundNumber = byteBuffer.readInt();
		answerTime = byteBuffer.readLong();
		int count = byteBuffer.readByte();
		if(count > 0)
			questions = new Question[count];
		for (int i = 0; i < count; i++) 
		{
			int id = byteBuffer.readInt();
			boolean isAnswer = byteBuffer.readBoolean();
			int questionCount = byteBuffer.readInt();
			
			Question q = DataFactory.getInstance().getQuestion(id);
			if(q == null)
			{
				System.out.println("question is null:"+id);
			}
			Question question = (Question) Question.cloneObject(q);
			
			question.id = id;
			question.isAnswered = isAnswer;
			question.questionCount = questionCount;
			questions[i] = question;
		}
	}

	@Override
	public void saveTo(ByteBuffer byteBuffer) 
	{
		byteBuffer.writeLong(expCount);
		byteBuffer.writeInt(pointCount);
		byteBuffer.writeByte(pics[0]);
		byteBuffer.writeByte(pics[1]);
		byteBuffer.writeByte(pics[2]);
		byteBuffer.writeInt(answerCount);
		byteBuffer.writeInt(roundNumber);
		byteBuffer.writeLong(answerTime);
		int count = (questions==null?0:questions.length);
		byteBuffer.writeByte(count);
		for (int i = 0; i < count; i++) 
		{
			byteBuffer.writeInt(questions[i].id);
			byteBuffer.writeBoolean(questions[i].isAnswered);
			byteBuffer.writeInt(questions[i].questionCount);
		}
	}
	
	public void setPic(byte pic)
	{
		for (int i = 0; i < pics.length; i++) 
		{
			if(pics[i] == 0)
			{
				pics[i] = pic;
				break;
			}
		}
	}
	
	/**
	 * 三个图片是否相同
	 * @return
	 */
	public boolean isSamePic()
	{
		return (pics[0] == pics[1] && pics[0] == pics[2] && pics[1] == pics[2] && pics[0] != 0);
	}
	
	/**
	 * 一组是否答完
	 * @return
	 */
	public boolean isTeamEnd()
	{
		return (pics[0] !=0 && pics[1] !=0 && pics[2] !=0);
	}
	
	/**
	 * 一轮是否答完
	 * @return
	 */
	public boolean isRoundEnd()
	{
		if(questions == null)
			return true;
		for (int i = 0; i < questions.length; i++) 
		{
			if(!questions[i].isAnswered)
				return false;
		}
		return true;
	}
	
	/**
	 * 获取下一题
	 * @return
	 */
	public Question getNextQuestion()
	{
		if(questions == null)
			return null;
		for (int i = 0; i < questions.length; i++) 
		{
			if(!questions[i].isAnswered)
				return questions[i];
		}
		return null;
	}
	
	private void clearPic()
	{
		pics[0] = 0;
		pics[1] = 0;
		pics[2] = 0;
	}
	
	private void clearQuestion()
	{
		questions = null;
	}
	
	private void clearTime()
	{
		if(answerTime > 0)
			answerTime = 0;
	}
	
	public void clearAll()
	{
		clear();
		clearTime();
	}
	
	/**
	 * 清零
	 */
	private void clear()
	{
		expCount = 0;
		answerCount = 0;
		pointCount = 0;
		roundNumber = 0;
		clearPic();
		clearQuestion();
	}
	
	public void addQuestion(List list)
	{
		questions = new Question[list.size()];
		for (int i = 0; i < questions.length; i++) 
		{
			questions[i] = (Question) list.get(i);
		}
	}
	
	public Question getQuestionById(int id)
	{
		if(questions == null)
			return null;
		for (int i = 0; i < questions.length; i++) 
		{
			if(questions[i] != null && questions[i].id == id)
				return questions[i];
		}
		return null;
	}
	
//	public Question getNextQuestion(int id)
//	{
//		if(questions == null)
//			return null;
//		for (int i = 0; i < questions.length; i++) 
//		{
//			if(questions[i] != null && questions[i].id == id)
//				return questions[i];
//		}
//		return null;
//	}
	
	public boolean isPrvAnswered(int id)
	{
		if(questions == null)
			return true;
		if(questions[0].id == id)
			return true;
		for (int i = 1; i < questions.length; i++) 
		{
			if(questions[i].id == id && questions[i-1].isAnswered)
			{
				return true;
			}
			else
			{
				continue;
			}
		}
		return false;
	}
	


	
	public int getAnswerType()
	{
		int result = 0;
		int time = Integer.parseInt(WorldManager.getTypeTime("HH",WorldManager.currentTime));
		if((time == ANSWER_OVER_TIME || time == REWARDTIME)) 
		{
			result = NOANSWERTIME;
			if(time == REWARDTIME)
			{
				clearAll();
			}
		}
		else
		{
			if(questions == null)
			{
				if(answerCount == 0)
				{
					result = FREE_ANSWER;//当天还没有开始答题
				}
				else if(answerCount == ROUND)
				{
					result = POINT_ANSWER;//免费机会 没有了，要用游戏币才能答
				}
				else if(answerCount == 2 * ROUND)
				{
					result = MONEY_ANSWER;//游戏币答题机会也没有了，要用元宝才能答
				}
				else
				{
					result = NOANSWER;//当天所有答题机会都没有了
					clearTime();
				}
			}
			else
			{
				result = CONTINUE;
			}
		}
		return result;
	}
	

	
	public int getQuestionCount()
	{
		if(questions == null)
			return 0;
		int result = 0;
		for (int i = 0; i < questions.length; i++) 
		{
			if(questions[i] != null && questions[i].isAnswered)
				result++;
		}
		return result;
	}
	
	
	
	/**
	 * 刷新题目 
	 * @param target
	 * @param questionId 题目ID
	 */
	public void sendNextQuestion(PlayerController target,int questionId)
	{
		ByteBuffer buffer = new ByteBuffer(5);
		if(answerCount % ROUND != getQuestionCount())
		{
			answerCount = ROUND * (answerCount / ROUND) + getQuestionCount();
			questionId = getNextQuestion().id;
		}
		buffer.writeByte(answerCount%ROUND+1);//第几道题
		buffer.writeInt(questionId);
		buffer.writeByte(pics[0]);
		buffer.writeByte(pics[1]);
		buffer.writeByte(pics[2]);
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_QUESTION_FLUSH_COMMAND,buffer));
	}
	
	/**
	 * 发送答题状态
	 * @param target
	 */
	public void sendAnswerQuestionState(PlayerController target)
	{
		checkClear();
		
		closeAnswer(target);
		
		ByteBuffer buffer = new ByteBuffer();
		buffer.writeByte(getAnswerType());
		buffer.writeInt(DataFactory.answerNpc);
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_ANSWER_QUESTION_STATE_COMMAND,buffer));
	}
	
	
	/** 一组题目答完后获得的额外经验和积分奖励 */
	private long otherExp = 0;
	private int otherPoint = 0;
	/**
	 * 发送答题结果
	 * @param target
	 */
	public void sendAnswerResult(PlayerController target,boolean result,int answer,long exp,int point,int pic)
	{
		ByteBuffer buffer = new ByteBuffer();
		buffer.writeBoolean(result);
		buffer.writeBoolean(isRoundEnd());
		buffer.writeByte(answer);
		buffer.writeUTF(exp+"");
		buffer.writeInt(point);
		buffer.writeByte(pic);
		buffer.writeUTF(otherExp+"");
		buffer.writeInt(otherPoint);
//		buffer.writeBoolean(getAnswerType()==NOANSWER);
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_ANSWER_QUESTION_RESULT_COMMAND,buffer));
		
		otherExp = 0;
		otherPoint = 0;
		
		if(exp == 0 && result)
		{
			System.out.println("AnswerParty sendAnswerResult exp is zero!player:"+target.getName()+"  level:"+target.getPlayer().level);
		}
	}
	
	/**
	 * 发送累计获得奖励
	 * @param target
	 */
	public void sendTotalReward(PlayerController target)
	{
		ByteBuffer buffer = new ByteBuffer();
		buffer.writeUTF(expCount+"");
		buffer.writeInt(pointCount);
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_TOTAL_REWARD_COMMAND,buffer));
	}
	
	public void processAnswerResult(PlayerController target,ByteBuffer buffer)
	{
		if(WorldManager.isZeroMorning(0))
		{
			target.sendAlert(ErrorCode.ALERT_SYSTEM_IS_UPDATE);
			return;
		}
		
		if(!isMayBeAnswer(target))
			return;
		
		int questionId = buffer.readInt();
		int answer = buffer.readByte();
		Question q = getQuestionById(questionId);
		if(q == null)
		{
			return;
		}
		if(q.isAnswered)
		{
//			System.out.println("question isAnswered!");
			return;
		}
		if(!isPrvAnswered(questionId))
		{
			target.sendAlert(ErrorCode.ALERT_PRVQUESTION_NO_ANSWER);//上一题还没答
			return;
		}
		q.setQuestionCount(q.getQuestionCount()+1);
		boolean result = q.isRight(answer);
		if(result)
		{
			q.setAnswered(true);

			long exp = expCount;
			int point = pointCount;
			int pic = setReward(target, q.getQuestionCount());
			if(isTeamEnd())
			{
				clearPic();
			}
			if(isRoundEnd())
			{
				roundNumber++;
				clearQuestion();
				closeAnswer(target);
			}

			sendAnswerResult(target, true,answer,expCount-exp,pointCount-point,pic);
			
			sendTotalReward(target);
			
			answerCount++;
		}
		else
		{
			sendAnswerResult(target, false,answer,0,0,0);
		}
	}
	
	
	public void checkClear()
	{
		if(WorldManager.sendRewardDate != WorldManager.date && answerCount == 0 && WorldManager.sendRewardDate != 0)
		{
			if(answerTime == 0)
				clear();
		}
	}
	
	
	public void flushQuestion(PlayerController target)
	{
		if(WorldManager.isZeroMorning(0))
		{
			target.sendAlert(ErrorCode.ALERT_SYSTEM_IS_UPDATE);
			return;
		}
		
		if(!isMayBeAnswer(target))
			return;
		
		checkClear();
		
		int qType = getAnswerType();

		if(qType == CONTINUE)
		{
			if(answerCount == 0)
			{
				sendNextQuestion(target,questions[0].id);
			}
			else
				sendNextQuestion(target,questions[answerCount%ROUND].id);
		}
		else 
		{
			if(qType == POINT_ANSWER)
			{
				Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
				if(bag.point < NEEDPOINT)
				{
					target.sendAlert(ErrorCode.ALERT_SHOP_NO_POINT);
					return;
				}
				else
				{
					bag.point -= NEEDPOINT;
					bag.sendAddGoods(target, null);
				}
			}
			else if(qType == MONEY_ANSWER)
			{
				Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
				if(bag.point < NEEDNEXTPOINT)
				{
					target.sendAlert(ErrorCode.ALERT_SHOP_NO_POINT);
					return;
				}
				else
				{
					bag.point -= NEEDNEXTPOINT;
					bag.sendAddGoods(target, null);
				}
			}
			
			addQuestion(getQuestions());
			sendNextQuestion(target,questions[0].id);
		}
		
		target.isAnswer = true;
		
		answerTime = System.currentTimeMillis();
		
		sendTotalReward(target);
	}
	
	/**
	 * 设置奖励
	 * @param type 单题奖励还是一组完后奖励或者是一轮奖励
	 * @param count 玩家一共答了多少次才把这题答对
	 * @return 获得的图片
	 * 一道题可以有无限次答题机会，即第一次回答错误，还可以继续，直到答对为止。如果一次性答对获得20-30分，第二次或更多次才答对获得15-20分，
	每组结束后如果获得的三个图案不相同获得额外经验奖励一次,再加一次单题奖励，不加积分，如果相同获得经验再加单题奖励5倍，积分加150
	经验奖励：一次性答对获得完全单题经验，第二次或以后答对获得一半单题经验奖励，
	 */
	public int setReward(PlayerController target,int count)
	{
		int pic = (int) (Math.random() * 3 + 1);
		AnswerReward ar = DataFactory.getInstance().getAnswerReward(target.getPlayer().level);
		if(ar == null)
			return pic;
		long soloExp;//获得经验
		int point = 0;//获得积分

		if(count == 1)// 一次性答对
		{
			point = (int) (Math.random() * 20 + 40);
			soloExp = ar.expReward;
		}
		else
		{
			point = (int) (Math.random() * 20 + 10);
			soloExp = ar.expReward / 2;
		}
	
		setPic((byte) pic);
		
		if(isTeamEnd())
		{
			if(isSamePic())
			{
				otherExp = 5 * ar.expReward;
				otherPoint = 200;
			}
			else
			{
				otherExp = ar.expReward;
			}
			soloExp += otherExp;
			point += otherPoint;
		}

		if(isRoundEnd())
		{
			//一轮后奖励
			RankReward rr = DataFactory.getInstance().getRankReward(2, (answerCount/ROUND)+1);
			GameServer.getInstance().getWorldManager().sendAnswerReward(target, rr, true);
		}

		target.addExp(soloExp,true,false,false);
		pointCount += point;
		expCount += soloExp;
		GameServer.getInstance().getWorldManager().addAnswerReward(target.getPlayer(),pointCount);
		return pic;
	}

	
	private List getQuestions()
	{
		List result = new ArrayList(ROUND);
		List questionList = (List) DataFactory.getInstance().getAnswerObject(DataFactory.TYPE_ONE_QUESTION);
		List temp = new ArrayList();
		for (int i = 0; i < questionList.size(); i++) 
		{
			Question q = (Question) questionList.get(i);
			Question question = (Question) Question.cloneObject(q);
			temp.add(question);
		}
		for (int i = 0; i < TYPEONE; i++) 
		{
			int random = (int) (Math.random() * temp.size());
			Question q = (Question) temp.get(random);
			Question question = (Question) Question.cloneObject(q);
			result.add(question);
			temp.remove(random);
		}
		
		temp.clear();
		
		List tempList = new ArrayList(TYPETWO+TYPETHREE);
		questionList = (List) DataFactory.getInstance().getAnswerObject(DataFactory.TYPE_TWO_QUESTION);
		for (int i = 0; i < questionList.size(); i++) 
		{
			Question q = (Question) questionList.get(i);
			Question question = (Question) Question.cloneObject(q);
			temp.add(question);
		}
		for (int i = 0; i < TYPETWO; i++) 
		{
			int random = (int) (Math.random() * temp.size());
			Question q = (Question) temp.get(random);
			tempList.add(q);
			temp.remove(random);
		}
		
		temp.clear();
		
		questionList = (List) DataFactory.getInstance().getAnswerObject(DataFactory.TYPE_THREE_QUESTION);
		for (int i = 0; i < questionList.size(); i++) 
		{
			Question q = (Question) questionList.get(i);
			Question question = (Question) Question.cloneObject(q);
			temp.add(question);
		}
		for (int i = 0; i < TYPETHREE; i++) 
		{
			int random = (int) (Math.random() * temp.size());
			Question q = (Question) temp.get(random);
			tempList.add(q);
			temp.remove(random);
		}
		
		//打乱
		for (int i = 0; i < TYPETWO+TYPETHREE; i++)
		{
			int random = (int) (Math.random() * tempList.size());
			Question q = (Question) tempList.get(random);
			Question question = (Question) Question.cloneObject(q);
			result.add(question);
			tempList.remove(random);
		}
		
		tempList.clear();
		
		return result;
	}
	
	
	/**
	 * 发送排名
	 * @param target
	 */
	private void sendAnswerReward(PlayerController target)
	{
		List list = GameServer.getInstance().getWorldManager().getAnswerRewards();
		int size = list.size();
		ByteBuffer buffer = new ByteBuffer();
		buffer.writeByte(size);
		if(size == 0)
			clear();
		for (int i = 0; i < size; i++) 
		{
			Reward rp = (Reward) list.get(i);
			buffer.writeByte(rp.rank);
			buffer.writeUTF(rp.name);
			buffer.writeInt(rp.level);
			buffer.writeInt(rp.point);
			buffer.writeUTF(WorldManager.getTypeTime("HH:mm:ss", rp.logTime));
		}
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_GET_ANSWER_REWARD_COMMAND,buffer));

		sendTotalReward(target);
	}
	
	/**
	 * 关闭答题
	 * @param target
	 */
	private void closeAnswer(PlayerController target)
	{
		target.isAnswer = false;
	}
	
	/**
	 * 检测是否在答题时间内或者答题机会是否用完
	 * @param target
	 * @return
	 */
	public boolean isMayBeAnswer(PlayerController target)
	{
		int state = getAnswerType();
		if(state == AnswerParty.NOANSWERTIME)
		{
			target.sendAlert(ErrorCode.ALERT_NO_ANSWER_TIME);
			return false;
		}
		
		if(state == AnswerParty.NOANSWER)
		{
			target.sendAlert(ErrorCode.ALERT_NO_ANSWER_DAY);
			return false;
		}
		
		return true;
	}
	
	public int getAnswerCount()
	{
		return answerCount;
	}
	
	public int getQuestionSize()
	{
		return questions==null?0:questions.length;
	}
	
	private static int CEPOINT = 50 * 10000;
	private static int CEMONEY = 3;
	private static int[] conGoodsId = {1045012001,1045010408};
	private static int[] conGoodsCount = {5,1};
	private static int[] rewardGoodsId = {1045010409,1045010409,1045010409,1045000276,1045000507,1045000608,1045012001,1045000031,1045000135};
	private static int[] rewardGoodsCount = {60,80,100,2,1,1,10,3,1};
	private static int[] rewardGoodsRate = {10,10,10,20,10,10,10,10,10};
	
	/**
	 * 用道具直接兑换经验奇妙试验室
	 * 1、每天最多兑换1次
2、1000级以上可以进行
3、条件：行动值礼包*5、元宝*3、奇妙之花*1、50金币
4、物品奖励从以下抽取：炼化宝石60个、80个、100个（各10%）、2000荣（20%）、4000万生命宝石（10%）、4000万精力宝石（10%）、行动值小礼包*10（10%）、1小时4倍卡*3（10%）、100代金券（10%）
5、经验奖励：直接加上答对一道题的35倍经验+100亿
	 */
	public void changeExp(PlayerController target,ByteBuffer inBuffer)
	{
		if(target.getParent() instanceof BattleController)
			return;
		int type = inBuffer.readByte();
		if(type == 1)//请求当天是否已经兑换过
		{
			boolean isChanged = target.getPlayer().changeExpDate == WorldManager.date;
			ByteBuffer outBuffer = new ByteBuffer();
			outBuffer.writeByte(type);
			outBuffer.writeBoolean(isChanged);
			target.getNetConnection().sendMessage(new SMsg(SMsg.S_PLAYER_CHANGE_EXP_COMMAND,outBuffer));
		}
		else if (type == 2)
		{
			if(target.getPlayer().changeExpDate == WorldManager.date)
			{
				target.sendAlert(ErrorCode.ALERT_CHANGED_EXP_ERROR);
				return;
			}
			if(target.getPlayer().level < CHANGEEXPLEVEL)
			{
				target.sendAlert(ErrorCode.ALERT_PLAYER_LEVEL_LACK);
				return;
			}
			
			Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
			if(bag.point < CEPOINT)
			{
				target.sendAlert(ErrorCode.ALERT_SHOP_NO_POINT);
				return;
			}
			if(bag.money < CEMONEY)
			{
				target.sendAlert(ErrorCode.ALERT_SHOP_NO_MONEY);
				return;
			}
			for (int i = 0; i < conGoodsId.length; i++)
			{
				if(conGoodsId[i] == 0)
					continue;
				int count = bag.getGoodsCount(conGoodsId[i]);
				if(count < conGoodsCount[i])
				{
					target.sendAlert(ErrorCode.ALERT_CHANGED_EXP_NEEDGOODS_ERROR);
					return;
				}
			}
			
			bag.money -= CEMONEY;
			bag.point -= CEPOINT;
			bag.sendAddGoods(target, null);
			for (int i = 0; i < conGoodsId.length; i++) 
			{
				if(conGoodsId[i] == 0)
					continue;
				bag.deleteGoods(target, conGoodsId[i], conGoodsCount[i]);
			}
		
			AnswerReward ar = DataFactory.getInstance().getAnswerReward(target.getPlayer().level);
			long rewardExp = 0;
			if(ar != null)
				rewardExp = ar.expReward * 35 + 100 * 10000 * 10000;
			
			Goods newGoods = null;
			int rate = 0;
			int random = (int) (Math.random() * 100) + 1;
			for (int i = 0; i < rewardGoodsRate.length; i++) 
			{
				rate += rewardGoodsRate[i];
				if(random <= rate)
				{
					Goods[] goods = DataFactory.getInstance().makeGoods(rewardGoodsId[i], rewardGoodsCount[i]);
					if(goods != null && goods[0] != null)
					{
						newGoods = goods[0];
					}	
					break;
				}
			}

			long disExp = target.addExp(rewardExp,true,true,false);
			if(disExp > 0)
				target.sendGetGoodsInfo(1, false, DC.getString(DC.PARTY_36)+":"+disExp);
			
			if(newGoods != null)
			{
				Mail mail = new Mail(DC.getString(DC.PARTY_37));
				mail.setTitle(DC.getString(DC.PARTY_38));
				StringBuffer sb = new StringBuffer();
				sb.append(DC.getString(DC.PARTY_39));
				sb.append(newGoods.name);
				sb.append("x");
				sb.append(newGoods.goodsCount);
				sb.append(",");
				sb.append(DC.getString(DC.PARTY_40));
				sb.append(rewardExp);
				mail.setContent(sb.toString());
				mail.addAttach(newGoods);
				mail.send(target);
			}
			target.getPlayer().changeExpDate = WorldManager.date;
			
			ByteBuffer outBuffer = new ByteBuffer();
			outBuffer.writeByte(type);
			outBuffer.writeUTF(rewardExp+"");//经验
			outBuffer.writeUTF(newGoods == null?"":newGoods.name);
			outBuffer.writeInt(newGoods.goodsCount);
			target.getNetConnection().sendMessage(new SMsg(SMsg.S_PLAYER_CHANGE_EXP_COMMAND,outBuffer));
		}
	}
	
	/**
	 * 1、在返回按钮的旁边增加一个“快答”按钮。
2、点击“快答”按钮以后会弹出提示“缴纳3元宝可以帮您快速回答108道题，确定要快答吗？”
3、确定使用以后，玩家会获得144--180道题的经验，会获得7200--5500之间的积分
4、会获得对应的三封邮件奖励
5、如果已经回答过1题的，就不能使用快答功能，会提示“已经开始答题以后，就不能使用快答功能了。”
	 */
	public void moneyAnswer(PlayerController target)
	{
		if(target.getParent() instanceof BattleController)
			return;
		if(WorldManager.isZeroMorning(0))
		{
			target.sendAlert(ErrorCode.ALERT_SYSTEM_IS_UPDATE);
			return;
		}
		if(getAnswerType() != FREE_ANSWER)
		{
			target.sendAlert(ErrorCode.ALERT_MONEY_ANSWER_NOTDO);
			return;
		}
		Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
		if(bag.money < 2)
		{
			target.sendAlert(ErrorCode.ALERT_SHOP_NO_MONEY);
			return;
		}
		if(answerCount > 0)
		{
			target.sendAlert(ErrorCode.ALERT_MONEY_ANSWER_ERROR);
			return;
		}

		AnswerReward ar = DataFactory.getInstance().getAnswerReward(target.getPlayer().level);
		if(ar == null)
			return;
		long exp = (int) (Math.random() * 36 + 144) * ar.expReward;
		target.addExp(exp, true, false,false);
		
		expCount = exp;
		pointCount = (int) (Math.random() * 1700 + 5500);
		
		//获得奖励
		//一轮后奖励
		RankReward rr = DataFactory.getInstance().getRankReward(2, 1);
		GameServer.getInstance().getWorldManager().sendAnswerReward(target, rr, true);
		//一轮后奖励
		rr = DataFactory.getInstance().getRankReward(2, 2);
		GameServer.getInstance().getWorldManager().sendAnswerReward(target, rr, true);
		//一轮后奖励
		rr = DataFactory.getInstance().getRankReward(2, 3);
		GameServer.getInstance().getWorldManager().sendAnswerReward(target, rr, true);
		
		answerCount = ROUND * 3;
		roundNumber = 3;
		
		bag.money -= 2;
		bag.sendAddGoods(target, null);
		
		GameServer.getInstance().getWorldManager().addAnswerReward(target.getPlayer(),pointCount);
		
		ByteBuffer buffer = new ByteBuffer();
		buffer.writeByte(4);
		buffer.writeInt(DataFactory.answerNpc);
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_ANSWER_QUESTION_STATE_COMMAND,buffer));
		
		StringBuffer sb = new StringBuffer();
		sb.append(DC.getString(DC.PARTY_49));
		sb.append(":");
		sb.append(expCount);
		target.sendGetGoodsInfo(1, false, sb.toString());
		sb = new StringBuffer();
		sb.append(DC.getString(DC.PARTY_50));
		sb.append(":");
		sb.append(pointCount);
		target.sendGetGoodsInfo(1, false, sb.toString());
	}
	
	
	/** 接收玩家和的互动 */
	public void clientMessageChain(PlayerController target, AppMessage msg)
	{
		int type = msg.getType();
		
		if(type == SMsg.C_QUESTION_FLUSH_COMMAND)
		{
			flushQuestion(target);
		}
		else if(type == SMsg.C_ANSWER_QUESTION_RESULT_COMMAND)
		{
			processAnswerResult(target, msg.getBuffer());
		}
		else if(type == SMsg.C_ANSWER_QUESTION_STATE_COMMAND)
		{
			sendAnswerQuestionState(target);
		}
		else if(type == SMsg.C_GET_ANSWER_REWARD_COMMAND)
		{
			sendAnswerReward(target);
		}
		else if(type == SMsg.C_CLOSE_ANSWER_COMMAND)
		{
			closeAnswer(target);
		}
		else if(type == SMsg.C_PLAYER_CHANGE_EXP_COMMAND)
		{
			changeExp(target,msg.getBuffer());
		}
		else if(type == SMsg.C_PLAYER_MONEY_ANSWER_COMMAND)
		{
			moneyAnswer(target);
		}
	}
	

}
