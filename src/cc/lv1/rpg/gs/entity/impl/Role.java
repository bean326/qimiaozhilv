package cc.lv1.rpg.gs.entity.impl;

import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.collection.i.Map;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.data.Exp;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.i.Sprite;
import cc.lv1.rpg.gs.gui.MainFrame;
/**
 * 角色实体
 * @author dxw
 *
 */
public class Role extends Sprite
{
	/** 初始等级上限 */
	public static final int OVERLEVEL = 6000;
	
	public static final int STATE_LEISURE = 0; //空闲
	public static final int STATE_BUSY = 1; //繁忙
	public static final int STATE_TEAMLEADER = 2; //带队
	public static final int STATE_TEAMMEMBER = 4; //队员
	public static final int STATE_FIGHTING = 8; //战斗
	public static final int STATE_TRADING = 16; //贸易
	public static final int STATE_PERSONALSHOP = 32; //交易
	public static final int STATE_UPGRADEPACK = 64; //更新
	
	/** 性别 1男 0女 */
	public int sex;
	
	/** 状态 */
	public int state;
	
	/** 称号 */
	public String title;
	
	/** 职业 (1军官,2足球,3护士,4超人) */
	public int profession = 1;
	
	/** 转职后的职业
	 *  1：军官
		2：特种兵
		3：足球运动员
		4：篮球运动员
		5：医生  
		6：护士
		7：超人
		8：赌圣
		9：一转军官
	   10：一转运动员
	   11：一转护士
	   12：一转超能力
	   13：二转军官
	   14：二转运动员
	   15：二转护士
	   16：二转超能力
	   17：三转军官
	   18：三转运动员
	   19：三转护士
	   20：三转超能力
		* 
		* */
	public int upProfession;
	
	/** 当前总经验 */
	public long experience = 0;
	
	/** 玩家所属阵营(200级过后必须选阵营才能继续游戏)(0.无阵营 1.猥琐阵营 2.邪神阵营) */
	public int camp;
	
	/** 上一次修改名字的月份 */
	public byte cnMonth;
	/** 上一次兑换经验的日期 */
	public byte changeExpDate;
	/**
	 * 领取每日奖励日期
	 */
	public byte dayRewardDate;
	
	/**
	 * 当天使用炼金术次数
	 */
	public byte goldBoxCount;
	
	/** 升级到下一级需要的经验 */
	public long requireExp = 5;

	/** 当前等级初始经验 */
	public long nextExp = 0;
	
	public Role()
	{	
	}
	
	public void loadFrom(ByteBuffer byteBuffer)
	{
		super.loadFrom(byteBuffer);
		sex = byteBuffer.readByte();
		title = byteBuffer.readUTF();
		profession = byteBuffer.readInt();
		upProfession = byteBuffer.readInt();
		camp = byteBuffer.readByte();
		experience = byteBuffer.readLong();
		requireExp = byteBuffer.readLong();
		cnMonth = (byte) byteBuffer.readByte();
		changeExpDate = (byte) byteBuffer.readByte();
		dayRewardDate = (byte) byteBuffer.readByte();
		goldBoxCount = (byte) byteBuffer.readByte();

		if(requireExp <= 0)
		{
			Map expMap = (Map)DataFactory.getInstance()
			.getAttachment(DataFactory.ATTACH_EXP);
			Exp exp = (Exp)expMap.get(level);
			if(exp != null)
			{
				experience = exp.total;
				requireExp = 0;
				
				int overLevel = getOverLevel();
				if(level == overLevel)
				{
					experience = exp.total;
					requireExp = 0;
				}
				else
				{
					exp = (Exp)expMap.get(level+1);
					if(exp != null)
					{
						requireExp = exp.levelExp;
					}
				}	
			}
			
			//MainFrame.println("Role fix exp --- id:"+id+" name:"+name);
		}
	
	}
	
	public void saveTo(ByteBuffer byteBuffer)
	{
		super.saveTo(byteBuffer);
		byteBuffer.writeByte(sex);
		byteBuffer.writeUTF(title);
		byteBuffer.writeInt(profession);
		byteBuffer.writeInt(upProfession);
		byteBuffer.writeByte(camp);
		byteBuffer.writeLong(experience);
		byteBuffer.writeLong(requireExp);
		byteBuffer.writeByte(cnMonth);
		byteBuffer.writeByte(changeExpDate);
		byteBuffer.writeByte(dayRewardDate);
		byteBuffer.writeByte(goldBoxCount);
	}
	
	public void readFrom(ByteBuffer byteBuffer)
	{
		super.readFrom(byteBuffer);
		sex = byteBuffer.readByte();
		state = byteBuffer.readInt();
		title = byteBuffer.readUTF();
		profession = byteBuffer.readInt();
		upProfession = byteBuffer.readInt();
		camp = byteBuffer.readByte();
		experience = byteBuffer.readLong();
		requireExp = byteBuffer.readLong();
		cnMonth = (byte) byteBuffer.readByte();
		changeExpDate = (byte) byteBuffer.readByte();
		dayRewardDate = (byte) byteBuffer.readByte();
		goldBoxCount = (byte) byteBuffer.readByte();
	}
	
	public void writeTo(ByteBuffer byteBuffer)
	{
		super.writeTo(byteBuffer);
		byteBuffer.writeByte(sex);
		byteBuffer.writeInt(state);
		byteBuffer.writeUTF(title);
		byteBuffer.writeInt(profession);
		byteBuffer.writeInt(upProfession);
		byteBuffer.writeByte(camp);
		
		if(nextExp == 0)
		{
			Map map = (Map)DataFactory.getInstance().getAttachment(DataFactory.ATTACH_EXP);
			Exp exp = (Exp)map.get(level+1);
			if(exp != null)
			{
				nextExp = exp.levelExp;
			}
		}
		
		long exp = nextExp - requireExp;
		if(exp < 0)
			exp = 0;
		byteBuffer.writeUTF(exp+"");
		byteBuffer.writeUTF(nextExp+"");
		int rate = 10000;
		if(nextExp != 0)
			rate = (int) (exp * 10000 / nextExp);
		byteBuffer.writeInt(rate);
		byteBuffer.writeInt(0);//暂时发送的0
	}
	
	
	/**
	 * 取得玩家当前的转生状态
	 * @return
	 */
	public int getZhuanshengState()
	{
		if(upProfession < 9)
			return 0;
		else if(upProfession < 13)
			return 1;
		else if(upProfession < 17)
			return 2;
		else if(upProfession < 21)
			return 3;
		return 0;
	}
	
	public int getOverLevel()
	{
		int state = getZhuanshengState();
		int overLevel = OVERLEVEL;
		if(state != 0)
		{
			UpRole ur = DataFactory.getInstance().getUpRoleByPlayer(state,profession);
			if(ur != null)
			{
				overLevel = ur.overLevel;
			}
		}
		return overLevel;
	}
	
	
}
