package cc.lv1.rpg.gs.entity.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import vin.rabbit.comm.GameObject;
import vin.rabbit.util.ByteBuffer;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.RPGameObject;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsEquip;

public abstract class Goods extends RPGameObject 
{
	/** 装备由普通商店购买时的绑定状态 */
	public static final int COMMONSHOPBIND = 0;//永远不绑定
	/** 装备由商城购买时的绑定状态 */
	public static final int MONEYSHOPBIND = 2;//拾取绑定
	/** 装备宝箱生成的绑定状态 */
	public static final int BOXBINDMODE = 2;//拾取绑定
	/** 大于等于这个品质的就要在公告显示 */
	public static final int NOTICE_QUALITY = 3;
	/** 改名卡 */
	public static final int CHANGENAMECARD = 25;
	/** 自动打怪挂机卡 */
	public static final int AUTOBATTLECARD = 26;
	/** 炼化抵价券 */
	public static final int EQUIPUPOTHER = 31;
	/** 顶顶猫抵价券 */
	public static final int MONEYBOXOTHER = 32;
	/** 守护技能修炼快速完成卡 */
	public static final int BATTLEPETSKILLENDGOODS = 38;
	/** 时空旅行船票 */
	public static final int TIMESKYTICKET = 1045000291;
	/** 商品类别
	1：装备
	2：药品
	3：任务道具
	4：材料
	5：价值物
	6：宝石
	7：卷轴
	8：技能书
	9：增值服务道具
	10：宝箱
	11：经验
	12：血蓝槽
	13: 宠物食物 
	14：宠物技能书
	15：宠物
	16: 代金券
	17：行动值道具
	18：仓库扩展道具
	19：增加宠物活跃度
	20：英雄礼包
	21：休息状态道具
	22：荣誉值道具
	23：加金币的道具
	24：挂机经验卡
	25：改名卡
	26：自动打怪卡
	27：结婚道具(道具的结婚戒指,不能装备)
	28：礼券
	29：结婚形象兑换券
	30：离婚道具
	31：炼化抵价券
	32：顶顶猫抵价券
	33：其它
	34: 宠物装备(守护)
	35: 守护经验包
	36: 守护技能经验包
	37: 守护口粮
	38: 守护技能修炼快速完成卡
	39: 夺宝奇兵券
	*/
	public int type;
	
	/** 图片编号 */
	public int iconId;
	
	/** 价格(游戏币) */
	public int point;
	
	/** 价格(RMB) */
	public int money;
	
	/** 价格(代金) */
	public int token;
	
	/** 可叠加数量 */
	public int repeatNumber;

	/** 最低品质(0.白色 1.绿色 2.蓝色 3.紫色1 4.紫色2 5.紫色3) */
	public int minQuality;
	
	/** 当前品质 (0.白色 1.绿色 2.蓝色 3.紫色1 4.紫色2 5.紫色3)*/
	public int quality;
	
	/** 最高品质(0.白色 1.绿色 2.蓝色 3.紫色1 4.紫色2 5.紫色3) */
	public int maxQuality;
	
	/** 绑定模式(0.永远不绑定 1.装备绑定 2.拾取绑定 4.已绑定) */
	public int bindMode;
	
	/** 道具说明备注 */
	public String remark;

	/** 等级需求 */
	public int level;

	/** 购买时间 */
	public long createTime;
	
	/** 有效时间(物品购买后多久过期) */
	public long effectTime;
	
	/** 仇恨值 */
	public int hatred;
	
	/** 丢弃状态(true:可以丢弃 false:不能丢弃) */
	public boolean isDel = true;
	
	/** 使用状态 (true：穿在身上  false：闲置在背包)*/
	public boolean useFlag = false;
	
	public int goodsCount = 1;
	
	/** 计数 */
	public int useNumber;
	
	public Goods() {}
	
	
	/**
	 * 使用物品
	 * @param target 目标用户
	 * @return 处理结果
	 */
	public abstract boolean onUseImpl(PlayerController target);
	
	public abstract boolean onUseGoodsBattle(PlayerController player);
	
	/**
	 * 取下物品
	 * @param target 目标用户
	 * @return 处理结果
	 */
	public abstract boolean onRemoveImpl(PlayerController target);

	/**
	 * 删除物品
	 * @param target 目标用户
	 */
	public abstract void onDeleteImpl(PlayerController target);


	/**
	 * 战斗中使用物品的处理
	 * @param battleController 战斗控制器
	 * @return 处理结果
	 */
	//public abstract boolean onBattleUseImpl(BattleController battleController);
	
	
	public void writeSuperTo(ByteBuffer buffer)
	{
		
	}
	
	/**
	 * 
	 */
	public void writeTo(ByteBuffer buffer)
	{
		super.writeTo(buffer);
		buffer.writeInt(type);
		buffer.writeInt(iconId);
		buffer.writeInt(point);
		buffer.writeInt(money);
		buffer.writeInt(token);
		buffer.writeInt(repeatNumber);
		buffer.writeInt(minQuality);
		buffer.writeInt(quality);
		buffer.writeInt(maxQuality);
		buffer.writeInt(bindMode);
		buffer.writeInt(level);
	}
	
	

	
	
	public void saveTo(ByteBuffer buffer)
	{
		buffer.writeInt(id);
		buffer.writeLong(objectIndex);
//		buffer.writeInt(type);
		buffer.writeBoolean(useFlag);
//		buffer.writeInt(repeatNumber);
		buffer.writeInt(goodsCount);
		buffer.writeInt(quality);
		buffer.writeByte(bindMode);
	}

	public void copyTo(GameObject go)
	{
		super.copyTo(go);
		Goods goods = (Goods)go;
		goods.iconId = iconId;
		goods.type = type;
		goods.point = point;
		goods.money = money;
		goods.token = token;
		goods.level = level;
		goods.repeatNumber = repeatNumber;
		goods.remark = remark;
		goods.maxQuality = maxQuality;
		goods.quality = quality;
		goods.minQuality = minQuality;
		goods.useFlag = useFlag;
		goods.bindMode = bindMode;
		goods.hatred = hatred;
		goods.isDel = isDel;
		goods.createTime = createTime;
		goods.effectTime = goods.effectTime;
	}
	
	protected boolean isJob(PlayerController target,int job)
	{
		if(job == 0)
			return true;
		if(target.getPlayer().upProfession != 0)
		{
			int pro = job - 4;
//			switch (job) 
//			{
//				case 5: pro = 1;break;
//				case 6: pro = 2;break;
//				case 7: pro = 3;break;
//				case 8: pro = 4;break;
//				case 9: pro = 5;break;
//				case 10: pro = 6;break;
//				case 11: pro = 7;break;
//				case 12: pro = 8;break;
//			}
			if(target.getPlayer().upProfession != pro)
			{
				return false;
			}
		}
		else
		{
			if(job !=0 && job != target.getPlayer().profession)
			{
				return false;
			}	
		}
		return true;
	}
	
	public void setBindMode(int mode)
	{
		bindMode = mode;
	}
	
	public void pickUpBind()
	{
		if(bindMode == 2)
			bindMode = 4;
	}
	
	public void equipBind()
	{
		if(bindMode == 1)
			bindMode = 4;
	}

	public String toString()
	{
		return name;
	}
	
	
	public void setCreateTime()
	{
		createTime = WorldManager.currentTime;
	}
	
	
	public int getGoodsCount()
	{
		if(goodsCount > 0)
			return goodsCount;
		else 
			return 1;
	}
	
	public boolean setVariable(String key,String value)
	{
		if(key.equals("isDel"))
		{
			isDel = (value.equals("1"));
			return true;
		}
		else if(key.equals("createTime"))
		{
			createTime = Long.parseLong(value);
			return true;
		}
		else if(key.equals("effectTime"))
		{
			effectTime = Long.parseLong(value);
			return true;
		}
		else
			return super.setVariable(key, value);
	}
	
	public void copyAllTo(Goods equip){}
}
