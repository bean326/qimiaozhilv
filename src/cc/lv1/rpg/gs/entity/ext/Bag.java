package cc.lv1.rpg.gs.entity.ext;

import vin.rabbit.net.AppMessage;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.i.Map;
import vin.rabbit.util.collection.impl.ArrayList;
import vin.rabbit.util.collection.impl.HashMap;
import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.data.DC;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.RPGameObject;
import cc.lv1.rpg.gs.entity.controller.BattleController;
import cc.lv1.rpg.gs.entity.controller.BusinessController;
import cc.lv1.rpg.gs.entity.controller.PVEController;
import cc.lv1.rpg.gs.entity.controller.PVPController;
import cc.lv1.rpg.gs.entity.controller.PartyPKController;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.controller.RoomController;
import cc.lv1.rpg.gs.entity.impl.BoxDropProp;
import cc.lv1.rpg.gs.entity.impl.Goods;
import cc.lv1.rpg.gs.entity.impl.LogTemp;
import cc.lv1.rpg.gs.entity.impl.Player;
import cc.lv1.rpg.gs.entity.impl.answer.AnswerReward;
import cc.lv1.rpg.gs.entity.impl.battle.PlayerBattleTmp;
import cc.lv1.rpg.gs.entity.impl.battle.effect.Effect;
import cc.lv1.rpg.gs.entity.impl.battle.effect.FlashEffect;
import cc.lv1.rpg.gs.entity.impl.SShop;
import cc.lv1.rpg.gs.entity.impl.battle.effect.TimeEffect;
import cc.lv1.rpg.gs.entity.impl.battle.skill.ActiveSkill;
import cc.lv1.rpg.gs.entity.impl.battle.skill.PassiveSkill;
import cc.lv1.rpg.gs.entity.impl.battle.skill.Skill;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsEquip;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsNotice;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsPetEquip;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsProp;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsSynt;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsUp;
import cc.lv1.rpg.gs.gui.MainFrame;
import cc.lv1.rpg.gs.net.SMsg;
import cc.lv1.rpg.gs.net.impl.PayJob;
import cc.lv1.rpg.gs.net.impl.SaveJob;
import cc.lv1.rpg.gs.other.ErrorCode;
import cc.lv1.rpg.gs.util.FontConver;

/**
 * 背包
 * @author dxw
 *
 */
public class Bag extends PlayerExtInfo
{
	/** BOSS宝箱从第几个开始需要钱 */
	public static final int BOSSBOXNUM = 5;
	
	/** BOSS宝箱对应需要的元宝数量 */
	public static final int[] bossMoney = {0,0,0,0};
	
	/** BOSS宝箱顶的次数 */
	public int bossBoxCount = 1;
	
	/** 宝石追加装备属性需要的元宝 */
	public static final double VIPEQUIP = 0.005;
	public static final double NOVIPEQUIP = 0.5;
	
	/** 元宝(RMB) */
	public long money; 
	
	/** 装备元宝(RMB) 
	 * 卖的所有VIP商品换成此元宝，
	 * 玩家不能使用这种元宝抽奖，
	 * 玩家买商场物品时优先扣除此元宝，当此元宝不足的时候才检测money元宝 
	 * */
	public long equipMoney; 

	/** 游戏币 */
	public long point; 
	
	/** 总的背包空间(默认)  */
	private int bagSize = 60;
	
	/** 背包中的物品 0---47背包  48--59身上的装备  */
	public Goods[] goodsList = new Goods[bagSize];
	
	/** 附加小道具列表(0血槽,1蓝槽,2经验卡,3活动输了的一个暂停状态,4挂机经验卡,5求婚戒指)(暂时是6个) */
	public Goods[] extGoods = new Goods[6];
	
	/** 宠物蛋列表 */
	private List petEggs = new ArrayList();
	
	/** 临时存储宝箱物品 */
	private Goods tmp;
	
	private String boxName;
	
	/** 临时存储BOSS宝箱开启的物品 */
	private List bossGoods = new ArrayList(10);
	
	/** 新手礼包 */
	public List giftGoods = new ArrayList();
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < goodsList.length; i++) 
		{
			if(goodsList[i] == null)
				continue;
			sb.append(goodsList[i].name);
			sb.append(":");
			sb.append(goodsList[i].goodsCount);
			sb.append("|");
		}
		if(sb.length() == 0)
			return "";
		return sb.substring(0, sb.length()-1);
	}
	
	public Goods getBossGoods(long objectIndex)
	{
		for (int i = 0; i < bossGoods.size(); i++)
		{
			Goods goods = (Goods) bossGoods.get(i);
			if(goods.objectIndex == objectIndex)
				return goods;
		}
		return null;
	}
	
	public void addGiftGoods(Goods goods)
	{
		if(!giftGoods.contains(goods))
			giftGoods.add(goods);
	}
	
	public List getGiftGoods()
	{
		return this.giftGoods;
	}
	
	public void clearGift()
	{
		giftGoods.clear();
	}
	
	public void clear(PlayerController target)
	{
		for (int i = 0; i < goodsList.length; i++) 
		{
			goodsList[i] = null;
		}
		
		writeToNotUseGoods(target);
	}
	
	/**
	 * 是否有新手礼包可以领取
	 * @return true 有 false 没有
	 */
	public boolean isGift()
	{
		return (giftGoods.size() > 0);
	}
	
	
	public void addBossGoods(Goods goods)
	{
		bossGoods.add(goods);
	}
	
	
	public void addPetEggs(Goods goods)
	{
		if(!petEggs.contains(goods))
		{
			petEggs.add(goods);
		}
	}

	
	public Goods getPetEgg(long objectIndex)
	{
		for (int i = 0; i < petEggs.size(); i++)
		{
			GoodsProp goods = (GoodsProp) petEggs.get(i);
			if(goods == null)
				continue;
			if(goods.petIndex == objectIndex)
			{
				return goods;
			}
		}
		return null;
	}
	
	public List getPetEgg()
	{
		return petEggs;
	}
	
	public GoodsProp getSpeaker(int chatType)
	{
		for (int i = 0; i < goodsList.length; i++)
		{
			if(goodsList[i] == null)
				continue;
			if(goodsList[i] instanceof GoodsProp)
			{
				GoodsProp prop = (GoodsProp) goodsList[i];
				if(prop.chatType == chatType)
					return prop;
			}
		}
		return null;
	}
	
	public void setExtGoods(int location,Goods goods)
	{
		if(location >= extGoods.length || location < 0)
			return;
		extGoods[location] = goods;
	}
	
	public Goods getExtGoods(int location)
	{
		if(location < extGoods.length)
		{
			return extGoods[location];
		}
		return null;
	}
	
	public void setTmp(Goods goods,String gn)
	{
		this.tmp = goods;
		this.boxName = gn;
	}

	public String getName()
	{
		return "bag";
	}


	public void loadFrom(ByteBuffer buffer)
	{
		bagSize = buffer.readInt();
		money = buffer.readLong();
		point = buffer.readLong();
		equipMoney = buffer.readLong();
		loadBagGoods(buffer); 
	}


	public void saveTo(ByteBuffer buffer)
	{
		buffer.writeInt(bagSize);
		buffer.writeLong(money);
		buffer.writeLong(point);
		buffer.writeLong(equipMoney);
		saveBagGoods(buffer);//存背包
	}
	
	/**
	 * 发送背包所有信息
	 * @param buffer
	 */
	public void writeToNotUseGoods(PlayerController target)
	{
		ByteBuffer buffer = new ByteBuffer();
		writeTo(buffer);
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_GET_PLAYER_BAG_COMMAND,buffer));
	}
	
	public void writeTo(ByteBuffer buffer)
	{
		int count = 0;
		
		buffer.writeInt(bagSize);
		buffer.writeInt((int) money);
//		buffer.writeInt((int) point);
		WorldManager.sendPoint(buffer, point);
		buffer.writeInt((int) equipMoney);
		for (int i = 0; i < goodsList.length; i++) 
		{
			if(goodsList[i] != null)
			{
				count++;	
			}
		}
		buffer.writeInt(count);
		for (int j = 0; j < goodsList.length; j++) 
		{
			if(goodsList[j] != null)
			{
				Goods goods = goodsList[j];
				buffer.writeInt(j);
				buffer.writeInt(goods.goodsCount);
				buffer.writeBoolean(goods.useFlag);
				goods.writeTo(buffer);
			}
		}
	}
	
	/** 
	 * 发送角色身上装备信息
	 * @param buffer
	 */
	public void sendPlayerEquipSet(PlayerController target[])
	{
		int count = 0;
		List list = new ArrayList();
		for (int i = 0; i < goodsList.length; i++) {
			Goods goods = (Goods) goodsList[i];
			if(goods == null)
				continue;
			if(goods.useFlag && goods.type == 1)
			{
				count++;
				list.add(goods);
			}
		}
		
		ByteBuffer buffer = new ByteBuffer();
		if(target[0].getParent() instanceof BattleController)
		{
			PlayerBattleTmp pbt = (PlayerBattleTmp) target[0].getAttachment();
			pbt.writeTo(buffer);
		}
		else
		{
			EquipSet es = (EquipSet) target[0].getPlayer().getExtPlayerInfo("equipSet");
			es.writeTo(buffer);
		}
		
		buffer.writeByte(count);
		for (int i = 0; i < count; i++) {
			Goods goods = (Goods) list.get(i);
			goods.writeTo(buffer);
			buffer.writeInt(((GoodsEquip)goods).equipLocation);
		}
		if(target.length == 1)
			target[0].getNetConnection().sendMessage(new SMsg(SMsg.S_GET_PLAYER_EQUIPSET_COMMAND,buffer));
		else
			target[1].getNetConnection().sendMessage(new SMsg(SMsg.S_GET_PLAYER_EQUIPSET_COMMAND,buffer));
	}
	
	/**
	 * 保存背包
	 * @param buffer
	 */
	public void saveBagGoods(ByteBuffer buffer)
	{
		int number = 0;
		//记录物品数量
		for (int i = 0; i < goodsList.length; i++) 
		{
			if(goodsList[i] != null)
				number++;
		}
		buffer.writeInt(number);
		//记录物品信息
		for(int j=0; j<goodsList.length; j++)
		{
			if(goodsList[j] != null)
			{
				Goods goods = goodsList[j];
				buffer.writeInt(goods.id);
				buffer.writeLong(goods.objectIndex);
				buffer.writeBoolean(goods.useFlag);
				buffer.writeInt(goods.goodsCount);
				buffer.writeInt(goods.quality);
				buffer.writeByte(goods.bindMode);
				buffer.writeInt(j);
				if(goods instanceof GoodsEquip)
				{
					GoodsEquip equip = (GoodsEquip) goods;
					buffer.writeUTF(equip.extAtt.toString());
					buffer.writeUTF(equip.attStr);
					if(!equip.attStr.isEmpty())
					{
						String[] str = Utils.split(equip.attStr, ":");
						for (int k = 0; k < str.length; k++) 
						{
							buffer.writeUTF(equip.getVariable(str[k]));
						}
					}
				}
				else if(goods instanceof GoodsPetEquip)
				{
					GoodsPetEquip equip = (GoodsPetEquip) goods;
					buffer.writeUTF(equip.extAtt.toString());
				}
				else if(goods instanceof GoodsProp)
				{
					GoodsProp prop = (GoodsProp) goods;
					buffer.writeLong(prop.expPoint);
				}
			}
		}
		
		int num = 0;
		//记录物品数量
		for (int i = 0; i < extGoods.length; i++) 
		{
			if(extGoods[i] != null)
				num++;
		}
		
		buffer.writeByte(num);
		
		for (int i = 0; i < extGoods.length; i++) 
		{
			if(extGoods[i] == null)
				continue;
			GoodsProp prop = (GoodsProp) extGoods[i];
			buffer.writeInt(prop.id);
			buffer.writeLong(prop.objectIndex);
			buffer.writeByte(i);
		}
		
		buffer.writeInt(petEggs.size());
		for (int i = 0; i < petEggs.size(); i++)
		{
			GoodsProp prop = (GoodsProp) petEggs.get(i);
			buffer.writeInt(prop.id);
			buffer.writeLong(prop.objectIndex);
			buffer.writeLong(prop.petIndex);
		}
		
		buffer.writeInt(giftGoods.size());
		for (int i = 0; i < giftGoods.size(); i++)
		{
			Goods goods = (Goods) giftGoods.get(i);
			buffer.writeInt(goods.id);
			buffer.writeLong(goods.objectIndex);
			buffer.writeInt(goods.goodsCount);
			buffer.writeInt(goods.quality);
			buffer.writeByte(goods.bindMode);
			if(goods instanceof GoodsEquip)
			{
				GoodsEquip equip = (GoodsEquip) goods;
				buffer.writeUTF(equip.extAtt.toString());
				buffer.writeUTF(equip.attStr);
				if(!equip.attStr.isEmpty())
				{
					String[] str = Utils.split(equip.attStr, ":");
					for (int k = 0; k < str.length; k++) 
					{
						buffer.writeUTF(equip.getVariable(str[k]));
					}
				}
			}
			else if(goods instanceof GoodsPetEquip)
			{
				GoodsPetEquip equip = (GoodsPetEquip) goods;
				buffer.writeUTF(equip.extAtt.toString());
			}
			else if(goods instanceof GoodsProp)
			{
				GoodsProp prop = (GoodsProp) goods;
				buffer.writeLong(prop.expPoint);
			}
		}
	}
	

	
	/**
	 * 取背包
	 * @param buffer
	 */
	public void loadBagGoods(ByteBuffer buffer)
	{
		/**
		 * 背包加上身上的
		 */
		int len = buffer.readInt();
		for(int i=0; i<len; i++)
		{
			int id = buffer.readInt();
			long objectIndex = buffer.readLong();
			boolean useFlag = buffer.readBoolean();
			int goodsCount = buffer.readInt();
			int quality = buffer.readInt();
			int bindMode = buffer.readByte();
			int location = buffer.readInt();
			
			Goods goods = null;
			try{
				goods = (Goods)DataFactory.getInstance().getGameObject(id);
			}catch(Exception e){System.out.println("Bag classError:"+id);}
			if(goods == null)
			{
				System.out.println("load bag goodsId error:"+id);
			}
			Goods newGoods = (Goods) Goods.cloneObject(goods);

			newGoods.objectIndex = objectIndex;
			newGoods.useFlag = useFlag;
			newGoods.goodsCount = (goodsCount<=0?1:goodsCount);
			newGoods.id = id;
			newGoods.quality = quality;
			newGoods.bindMode = bindMode;
			if(newGoods instanceof GoodsEquip)
			{
				String extAtt = buffer.readUTF();
				((GoodsEquip)newGoods).extAtt = new StringBuffer(extAtt);
				String attStr = buffer.readUTF();
				((GoodsEquip)newGoods).attStr = attStr;
				if(!attStr.isEmpty())
				{
					String[] str = Utils.split(attStr, ":");
					for (int j = 0; j < str.length; j++) 
					{
						newGoods.setVariable(str[j], buffer.readUTF());
					}
				}
				((GoodsEquip)newGoods).setDefaultAtt();

				((GoodsEquip)newGoods).updateEquipChange();

			}
			else if(newGoods instanceof GoodsPetEquip)
			{
				newGoods.useFlag = false;
				((GoodsPetEquip)newGoods).setExtAtt(buffer.readUTF());
			}
			else if(newGoods instanceof GoodsProp)
			{
				((GoodsProp)newGoods).expPoint = buffer.readLong();
			}

			goodsList[location] = newGoods;
		}
		
		/**
		 * 附件道具，经验卡等 0 1 2是流水号  3 4 是时间
		 */
		int length = buffer.readByte();
		for (int i = 0; i < length; i++) 
		{
			int id = buffer.readInt();
			long objectIndex = buffer.readLong();
			int location = buffer.readByte();
			
			Goods goods = (Goods) DataFactory.getInstance().getGameObject(id);
			if(goods == null)
			{
				System.out.println("load bag goodsId error:"+id);
			}
			Goods newGoods = (Goods) Goods.cloneObject(goods);
			
			newGoods.objectIndex = objectIndex;
			newGoods.id = id;
			
			extGoods[location] = newGoods;
		}
		
		/**
		 * 宠物蛋
		 */
		int leng = buffer.readInt();
		for (int i = 0; i < leng; i++) 
		{
			int id = buffer.readInt();
			long objectIndex = buffer.readLong();//宠物蛋流水
			long petIndex = buffer.readLong();//关联到宠物的流水 与pet宠物流水号相同
			
			Goods goods = (Goods) DataFactory.getInstance().getGameObject(id);
			if(goods == null)
			{
				System.out.println("load bag goodsId error:"+id);
			}
			Goods newGoods = (Goods) Goods.cloneObject(goods);

			newGoods.objectIndex = objectIndex;
			((GoodsProp)newGoods).petIndex = petIndex;
			newGoods.id = id;

			petEggs.add(newGoods);
		}
		
		
		
		/**
		 * 新手礼包
		 */
		int le = buffer.readInt();
		for (int i = 0; i < le; i++) 
		{
			int id = buffer.readInt();
			long objectIndex = buffer.readLong();
			int goodsCount = buffer.readInt();
			int quality = buffer.readInt();
			int bindMode = buffer.readByte();

			Goods goods = (Goods)DataFactory.getInstance().getGameObject(id);
			if(goods == null)
			{
				System.out.println("load bag goodsId error:"+id);
			}
			Goods newGoods = (Goods) Goods.cloneObject(goods);

			newGoods.objectIndex = objectIndex;
			newGoods.goodsCount = (goodsCount<=0?1:goodsCount);
			newGoods.id = id;
			newGoods.quality = quality;
			newGoods.bindMode = bindMode;
			if(newGoods instanceof GoodsEquip)
			{
				String extAtt = buffer.readUTF();
				((GoodsEquip)newGoods).extAtt = new StringBuffer(extAtt);
				String attStr = buffer.readUTF();
				((GoodsEquip)newGoods).attStr = attStr;
				if(!attStr.isEmpty())
				{
					String[] str = Utils.split(attStr, ":");
					for (int j = 0; j < str.length; j++) {
						newGoods.setVariable(str[j], buffer.readUTF());
					}
				}
				((GoodsEquip)newGoods).setDefaultAtt();
				
				((GoodsEquip)newGoods).updateEquipChange();
			}
			else if(newGoods instanceof GoodsPetEquip)
			{
				((GoodsPetEquip)newGoods).setExtAtt(buffer.readUTF());
			}
			else if(newGoods instanceof GoodsProp)
			{
				((GoodsProp)newGoods).expPoint = buffer.readLong();
			}
			
			giftGoods.add(newGoods);
		}
		
	}

	

	
	/**
	 * 打造装备添加到背包
	 * @param target
	 * @param goodsId
	 * @param goodsCount
	 */
	public void addGoods(PlayerController target,int goodsId , int goodsCount)
	{
		Object obj = DataFactory.getInstance().getGameObject(goodsId);

		if(obj == null)
			return;
		if(!(obj instanceof Goods))
			return;
		Goods goods = (Goods) obj;

		Goods newGoods = null;
		if(goodsCount > goods.repeatNumber)
		{
			target.sendAlert(ErrorCode.ALERT_GOODSCOUNT_OVERRUN);
			return;
		}
		if(goods instanceof GoodsEquip)
		{
			GoodsEquip equip = (GoodsEquip) goods;
			newGoods = equip.makeNewBetterEquip(equip.quality);
		}
		else if(goods instanceof GoodsPetEquip)
		{
			newGoods = (Goods) Goods.cloneObject(goods);
		}
		else
		{
			newGoods = (Goods) Goods.cloneObject(goods);
			newGoods.goodsCount = goodsCount;
		}

		newGoods.objectIndex = GameServer.getInstance().getDatabaseAccessor().getGoodsObjIndex();
		
		sendAddGoods(target,newGoods);
	}
	
	
	public boolean isCanAddGoodsList(List list)
	{
		int nullCount = getNullCount();
		for (int i = 0; i < list.size(); i++)
		{
			Goods goods = (Goods) list.get(i);
			int count = goods.goodsCount;
			if(getCanAddCount(goods.id) >= count)
				continue;
			else
			{
				count -= getCanAddCount(goods.id);
				int num = count / goods.repeatNumber;
				if(count % goods.repeatNumber == 0)
					nullCount -= num;
				else
					nullCount -= num + 1;
			}
		}

		if(nullCount < 0)
			return false;
		
		return true;
	}
	
	private int getCanAddCount(int id)
	{
		int count = 0;
		for (int i = 0; i < goodsList.length; i++) 
		{
			if(goodsList[i] == null)
				continue;
			if(goodsList[i].repeatNumber == 1)
				continue;
			if(goodsList[i].id == id)
			{
				count += goodsList[i].repeatNumber - goodsList[i].goodsCount;
			}
		}
		return count;
	}
	
	
	/**
	 * 是否还能往背包添加物品(true还可以添加，false不可以添加了)
	 * @param target
	 * @param goods
	 * @return
	 */
	public boolean isCanAddGoodsToBag(Goods goods)
	{
		if(goods.repeatNumber <= 1)
		{
			if(getNullCount() < goods.goodsCount)
				return false;
			if(isBagFull())
				return false;
		}
		else
		{
			List sameGoodsList = getSameGoods(goods);
			if(sameGoodsList.size() == 0)
			{
				if(isBagFull())
					return false;
			}
			else
			{
				int count = 0;
				for (int i = 0; i < sameGoodsList.size(); i++)
				{
					Goods sGoods = (Goods) sameGoodsList.get(i);
					count += sGoods.repeatNumber - sGoods.goodsCount;
				}
				if(goods.goodsCount > count)
				{
					if(isBagFull())
						return false;
				}
			}
		}
		return true;
	}
	
	

	/**
	 * 是否还能往背包添加物品(true还可以添加，false不可以添加了)
	 * @param target
	 * @param goods
	 * @return
	 */
	public boolean isCanAddGoodsToBag(PlayerController target,Goods goods)
	{
		if(goods.repeatNumber <= 1)
		{
			if(getNullCount() < goods.goodsCount)
				return false;
			if(isBagFull(target))
				return false;
		}
		else
		{
			List sameGoodsList = getSameGoods(goods);
			if(sameGoodsList.size() == 0)
			{
				if(isBagFull(target))
					return false;
			}
			else
			{
				int count = 0;
				for (int i = 0; i < sameGoodsList.size(); i++)
				{
					Goods sGoods = (Goods) sameGoodsList.get(i);
					count += sGoods.repeatNumber - sGoods.goodsCount;
					
				}
				if(goods.goodsCount > count)
				{
					if(isBagFull(target))
						return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * 添加物品到玩家背包中
	 * @param target
	 * @param goods
	 */
	public void addGoodsToBag(PlayerController target,Goods goods,ByteBuffer buffer)
	{
		if(goods == null)
			return;
		if(goods.goodsCount <= 0)
			goods.goodsCount = 1;
		if(goods.objectIndex == 0)
			goods.objectIndex = GameServer.getInstance().getDatabaseAccessor().getGoodsObjIndex();
//		System.out.println(target.getName()+"   "+goods.name+"   "+goods.bindMode);
		goods.pickUpBind();
//		System.out.println(target.getName()+" 444  "+goods.name+"   "+goods.bindMode);
		PlayerSetting ps = (PlayerSetting) target.getPlayer().getExtPlayerInfo("playerSetting");
		buffer.writeInt(goods.goodsCount);
		if(goods.repeatNumber <= 1)
		{
			if(getNullCount() < goods.goodsCount)
				return;
			if(isBagFull(target))
				return;
			if(goods.goodsCount == 1)
			{
				int location = getNullLocation();
				if(location == -1)
					return;
				goodsList[location] = goods;
				sendGoodsInfo(0,goods,location,buffer);
			}
			else if(goods.goodsCount > 1)
			{
				for (int i = 0; i < goods.goodsCount; i++)
				{
					int location = getNullLocation();
					if(location == -1)
						return;
					Goods newGoods = (Goods) Goods.cloneObject(goods);
					goodsList[location] = newGoods;
					sendGoodsInfo(0,newGoods,location,buffer);
				}
			}
		}
		else
		{
			List sameGoodsList = getSameGoods(goods);
			if(sameGoodsList.size() == 0)
			{
				if(isBagFull(target))
					return;
				int location = getNullLocation();
				if(location == -1)
					return;
				goodsList[location] = goods;
				sendGoodsInfo(0,goods,location,buffer);
			}
			else
			{
				buffer.writeByte(1);
				int count = 0;
				for (int i = 0; i < sameGoodsList.size(); i++)
				{
					Goods sGoods = (Goods) sameGoodsList.get(i);
					count += sGoods.repeatNumber - sGoods.goodsCount;
				}
				if(goods.goodsCount > count)
				{
					if(isBagFull(target))
						return;
					buffer.writeByte(1);
					buffer.writeByte(sameGoodsList.size());
					for (int i = 0; i < sameGoodsList.size(); i++)
					{
						Goods sGoods = (Goods) sameGoodsList.get(i);
						sGoods.goodsCount = sGoods.repeatNumber;
						sendGoodsInfo(1,sGoods,-1,buffer);
					}
					count = goods.goodsCount - count;
					Goods newGoods = (Goods) Goods.cloneObject(goods);
					newGoods.objectIndex = GameServer.getInstance().getDatabaseAccessor().getGoodsObjIndex();
					newGoods.goodsCount = count;
					int location = getNullLocation();
					if(location == -1)
						return;
					goodsList[location] = newGoods;
					
					sendGoodsInfo(1,newGoods,-1,buffer);
				}
				else
				{
					buffer.writeByte(0);	
					buffer.writeByte(sameGoodsList.size());
					for (int i = 0; i < sameGoodsList.size(); i++)
					{
						Goods sGoods = (Goods) sameGoodsList.get(i);
						if(goods.goodsCount <= sGoods.repeatNumber - sGoods.goodsCount)
						{
							sGoods.goodsCount += goods.goodsCount;
							goods.goodsCount = 0;
						}
						else
						{
							goods.goodsCount -= sGoods.repeatNumber - sGoods.goodsCount;
							sGoods.goodsCount = sGoods.repeatNumber;
						}
						sendGoodsInfo(1,sGoods,-1,buffer);
					}	
				}
			}
			ps.updatePlayerBar(goods.id, getGoodsCount(goods.id));
		}
		
		target.sendActivePoint();

	}

	
	public void addMoney(PlayerController target,int point,int money)
	{
		
		this.point += point;
		this.money += money;
		sendAddGoods(target, null);
	}
	
	
	private void sendGoodsInfo(int type,Goods goods,int location,ByteBuffer buffer)
	{
		if(type == 0)
		{
			buffer.writeByte(0);
			goods.writeTo(buffer);
			buffer.writeInt(goods.goodsCount);
			buffer.writeInt(location);
		}
		else if(type == 1)
		{
			buffer.writeUTF(goods.objectIndex+"");
			buffer.writeInt(goods.goodsCount);
			buffer.writeInt(getGoodsLocation(goods.objectIndex));
			buffer.writeInt(goods.point);
		}
	}
	

	
	
	/**
	 * 从背包中删除物品(丢弃物品)
	 * @param target 目标玩家
	 * @param buffer 参数
	 */
	public void deleteGoodsFromBag(PlayerController target, ByteBuffer buffer) 
	{
		long objectIndex = Long.parseLong(buffer.readUTF());
		Object goodsObject = getGoodsByObjectIndex(objectIndex);
		if(goodsObject == null)
		{
			//物品不存在
			target.sendAlert(ErrorCode.ALERT_GOODS_NOT_EXIST);
			return;
		}
		Goods goods = (Goods) goodsObject;
		if(goods.useFlag)
			return;
		if(!goods.isDel)
		{
			//物品不能丢弃
			target.sendAlert(ErrorCode.ALERT_GOODS_ISNOT_DEL);
			return;
		}
		if(goods instanceof GoodsPetEquip)
		{
			if(((GoodsPetEquip)goods).growPoint > 0)
			{
				//物品不能丢弃
				target.sendAlert(ErrorCode.ALERT_GOODS_ISNOT_DEL);
				return;
			}
		}
		
		if(goods.goodsCount <= 0)
			goods.goodsCount = 1;

		if(target.getPlayer().isVipPlayer)
		{
			StringBuffer data = new StringBuffer();
			data.append(DC.getString(DC.GOODS_2));
			data.append(goods.name);
			data.append("x");
			data.append(goods.goodsCount);
			DataFactory.getInstance().addVipPlayerInfo(target, data.toString());
		}
		
		removeGoods(target, objectIndex, goods.goodsCount);
	}
	

	/**
	 * 改变物品在背包中的位置
	 * @param target
	 * @param buffer
	 */
	public void changeGoodsLocation(PlayerController target,ByteBuffer buffer)
	{
		String str = buffer.readUTF();
		long objectIndex = 0;
		try 
		{
			objectIndex = Long.parseLong(str);
		} catch (Exception e)
		{
			return;
		}
		
		int newLocation = buffer.readInt();
		if(newLocation < 0)
			return;
		Object goodsObject = getGoodsByObjectIndex(objectIndex);
		if(goodsObject == null)
		{
			//物品不存在
			target.sendAlert(ErrorCode.ALERT_GOODS_NOT_EXIST);
			return;
		}
		Goods goods = (Goods) goodsObject;
		if(newLocation > goodsList.length-1)
		{
			target.sendAlert(ErrorCode.ALERT_GOODS_LOCATION_ERROR);
			return;
		}
		if(goods.useFlag)
			return;
		ByteBuffer buff = new ByteBuffer(16);
		if(goodsList[newLocation] != null)
		{
			int oldLocation = getGoodsLocation(objectIndex);
			Goods g = goodsList[newLocation];
			goodsList[newLocation] = goods;
			goodsList[oldLocation] = g;
			buff.writeByte(1);
			buff.writeUTF(g.objectIndex+"");
			buff.writeInt(oldLocation);
		}
		else
		{
			buff.writeByte(2);
			goodsList[getGoodsLocation(objectIndex)] = null;
			goodsList[newLocation] = goods;
		}
		buff.writeUTF(objectIndex+"");
		buff.writeInt(newLocation);
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_CHANGE_BAG_GOODS_LOCATION_COMMAND,buff));
	}
	
	
//	/**
//	 * 战斗中喝药
//	 * @param player
//	 * @param targetIndex
//	 * @return
//	 */
//	public PlayerController getTargetBattle(PlayerController player,int targetIndex)
//	{
//		BattleController battle = (BattleController) player.getParent();
//		PlayerController target = null;
//		if(battle instanceof PVEController)
//		{
//			PVEController pve = (PVEController) battle;
//			if(targetIndex > pve.getPlayerCount())
//			{
//				player.sendAlert(ErrorCode.ALERT_OBJECT_INDEX_OVERRUN);
//				return null;
//			}
//			target = pve.getPlayerByIndex(targetIndex);
//		}
//		else if(battle instanceof PVPController)
//		{
//			PVPController pvp = (PVPController) battle;
//			if(player.getAttachment().getTeamNo() == BattleController.TEAM1)
//			{
//				if(targetIndex > pvp.getPlayers().length)
//				{
//					player.sendAlert(ErrorCode.ALERT_OBJECT_INDEX_OVERRUN);
//					return null;
//				}
//				target = pvp.getPlayers()[targetIndex];
//			}
//			if(player.getAttachment().getTeamNo() == BattleController.TEAM2)
//			{
//				if(targetIndex > pvp.getTargetPlayers().length)
//				{
//					player.sendAlert(ErrorCode.ALERT_OBJECT_INDEX_OVERRUN);
//					return null;
//				}
//				target = pvp.getTargetPlayers()[targetIndex];
//			}
//		}
//		else if(battle instanceof PartyPKController)
//		{
//			PartyPKController ppk = (PartyPKController) battle;
//			if(player.getAttachment().getTeamNo() == BattleController.TEAM1)
//			{
//				if(targetIndex > ppk.getPlayers().length)
//				{
//					player.sendAlert(ErrorCode.ALERT_OBJECT_INDEX_OVERRUN);
//					return null;
//				}
//				target = ppk.getPlayers()[targetIndex];
//			}
//			if(player.getAttachment().getTeamNo() == BattleController.TEAM2)
//			{
//				if(targetIndex > ppk.getTargetPlayers().length)
//				{
//					player.sendAlert(ErrorCode.ALERT_OBJECT_INDEX_OVERRUN);
//					return null;
//				}
//				target = ppk.getTargetPlayers()[targetIndex];
//			}
//		}
//		return target;
//	}
	
	/**
	 * 使用物品
	 * @param target
	 * @param buffer
	 */
	public void useGoods(PlayerController target,ByteBuffer inBuffer)
	{
		String objectIndex = "";
		int goodsId = 0,targetIndex = -1;
		boolean isUse = true;
		Goods goods = null;
		long index = 0;
		boolean result = false;
		if(target.getParent() instanceof BattleController)
		{
			goodsId = inBuffer.readInt();
			targetIndex = inBuffer.readByte();
		}
		else
		{
			objectIndex = inBuffer.readUTF();
			goodsId = inBuffer.readInt();
			if(inBuffer.available() > 0)
				isUse = inBuffer.readBoolean();
			if(objectIndex.isEmpty() && goodsId <= 0)
			{
				return;
			}
		}

		if(objectIndex.isEmpty())
			goods = getGoodsById(goodsId);
		else
		{
			try
			{
				index = Long.parseLong(objectIndex);
			}
			catch(Exception e)
			{
				return;
			}
			goods = getGoodsByObjectIndex(index);
		}

		if(goods == null)
		{
			//物品不存在
			target.sendAlert(ErrorCode.ALERT_GOODS_NOT_EXIST);
			return;
		}	
		if(target.isAuto && goods.type != 2)
		{
			return;
		}
		
		if(target.isBox && goods.type == 10)
		{
			if(target.boxType == 1)
				target.sendAlert(ErrorCode.ALERT_BOX_OPEN_ERROR);
			else if(target.boxType == 2)
				target.sendAlert(ErrorCode.ALERT_PLAYER_IS_MONEYBOX);
			return;
		}
	
		if(tmp != null && goods.type == 10)
		{
			target.sendAlert(ErrorCode.ALERT_BOX_OPEN_ERROR);
			return;
		}
		
		if(goods.type == 10 && getNullCount() < 4)
		{
			target.sendAlert(ErrorCode.ALERT_BOX_NULLPOINT_FOUR);
			return;
		}
	
		if(target.getParent() instanceof BattleController)//表示是在战斗中用物品
		{
			if(goods.type != 2)
			{
				target.sendAlert(ErrorCode.ALERT_NOT_USE_GOODS_INBATTLE);
				return;
			}

			result = goods.onUseGoodsBattle(target);

			if(!result)//使用物品失败
				return;
			if(goods instanceof GoodsProp)
			{ 
				GoodsProp prop = (GoodsProp) goods;
				sendBattlePropInfo(prop,target);
				removeGoods(target,prop.objectIndex,1);
			}
		}
		else//战斗外
		{
			if(isUse)
				goods.useNumber++;
			else
			{	
				goods.useNumber = 0;
				return;
			}
			if(goods.useNumber == 1)
			{
				boolean isCheck = false;
				if(goods instanceof GoodsProp)
				{
					GoodsProp prop = (GoodsProp) goods;
					if(prop.type == 11 && getExtGoods(2) != null && prop.expPoint == 0 && prop.expTimes > 0)
						isCheck = true;
					if(prop.extLife > 0 && getExtGoods(0) != null)
						isCheck = true;
					if(prop.extMagic > 0 && getExtGoods(1) != null)
						isCheck = true;
					if(prop.type == 24 && getExtGoods(4) != null)
						isCheck = true;
				}
				if(isCheck)
				{
					//通知客户端之前已经用了宝石或经验卡，是否需要替换
					ByteBuffer buffer = new ByteBuffer();
					buffer.writeUTF(goods.objectIndex+"");
					target.getNetConnection().sendMessage(new SMsg(SMsg.S_USE_GOODS_COMMAND,buffer));
					return;
				}
			}
			result = goods.onUseImpl(target);
			if(!result)//使用物品失败
				return;

			if(goods instanceof GoodsEquip)
			{
				TaskInfo info = (TaskInfo)target.getPlayer().getExtPlayerInfo("taskInfo");
				info.onPlayerLostItem(goods.id,1, target);
			}
			else if(goods instanceof GoodsPetEquip)
			{//暂定
				TaskInfo info = (TaskInfo)target.getPlayer().getExtPlayerInfo("taskInfo");
				info.onPlayerLostItem(goods.id,1, target);
			}
			else if(goods instanceof GoodsProp)
			{
				sendOutBattlePropInfo(goods,target);
				if(goods.type != 15)
					removeGoods(target,goods.objectIndex,1);
				goods.useNumber = 0;
			}
		}
	}
	
	
	/**
	 * 脱下装备
	 * @param target
	 * @param buffer
	 */
	public void takeOffEquip(PlayerController target,ByteBuffer inBuffer)
	{
		if(target.getParent() instanceof BattleController)
		{
			return;
		}
		
		long objectIndex = Long.parseLong(inBuffer.readUTF());
		Object goodsObject = getGoodsByObjectIndex(objectIndex);
		if(goodsObject == null)
		{
			//物品不存在
			target.sendAlert(ErrorCode.ALERT_GOODS_NOT_EXIST);
			return;
		}	
		if(!(goodsObject instanceof GoodsEquip))
		{
			target.sendAlert(ErrorCode.ALERT_GOODS_TYPE_ERROR);
			return;
		}
		Goods equip = (GoodsEquip) goodsObject;
		if(!equip.useFlag)
			return;
		boolean result = equip.onRemoveImpl(target);
		if(!result)
		{
			target.sendAlert(ErrorCode.ALERT_GOODSEQUIP_TAKEOFF_ERROR);
			return;
		}
		ByteBuffer buffer = new ByteBuffer();
		if(target.getParent() instanceof BattleController)
		{
			PlayerBattleTmp pbt = (PlayerBattleTmp) target.getAttachment();
			pbt.writeTo(buffer);
		}
		else
		{
			EquipSet es = (EquipSet) target.getPlayer().getExtPlayerInfo("equipSet");
			es.writeTo(buffer);
		}
		buffer.writeUTF(objectIndex+"");
		buffer.writeInt(getGoodsLocation(objectIndex));
		buffer.writeInt(((GoodsEquip)equip).equipLocation);
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_TAKE_OFF_EQUIP_COMMAND,buffer));
	
	
		TaskInfo info = (TaskInfo)target.getPlayer().getExtPlayerInfo("taskInfo");
		info.onPlayerGotItem(equip.id,1, target);

	}
	
	/**
	 * 取得玩家快捷栏列表
	 * @param target
	 */
	public void getPlayerShotcutBar(PlayerController target)
	{
/*		if(BtnListener.CheckPrint)
		{
			if(target.getPlayer().accountName.equals("dxw"))
			System.out.println(target.getName()+"---- 收到客户端马上发送快捷栏请求  ----");
		}*/
		PlayerSetting ps = (PlayerSetting) target.getPlayer().getExtPlayerInfo("playerSetting");
		ByteBuffer buffer = new ByteBuffer();
		ps.writeTo(buffer);
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_GET_PLAYER_SHORTCUT_BAR_COMMAND,buffer));
	}
	
	/**
	 * 设置玩家快捷栏列表
	 * @param target
	 * @param buffer
	 */
	public void setPlayerShortcutBar(PlayerController target,ByteBuffer buffer)
	{	
		int location = buffer.readInt();
		int id = buffer.readInt();
		int type = buffer.readInt();//1为物品，0为技能
		if(location < 0)
			return;
		int count = 0;
		PlayerSetting ps = (PlayerSetting) target.getPlayer().getExtPlayerInfo("playerSetting"); 
		if(type == 1)
		{
			Object goodsObject = getGoodsById(id);
			if(goodsObject == null)
			{
				//物品不存在
				target.sendAlert(ErrorCode.ALERT_GOODS_NOT_EXIST);
				return;
			}	
			Goods goods = (Goods) goodsObject;
			count = getGoodsCount(goods.id);
			ps.addPlayerBar(goodsObject, count, location);
		}
		else if(type == 0)
		{
			SkillTome st = (SkillTome) target.getPlayer().getExtPlayerInfo("skillTome");
			Object obj = st.getSkill(id);
			if(obj == null || !(obj instanceof Skill))
			{
				//物品不存在
				target.sendAlert(ErrorCode.ALERT_GOODS_NOT_EXIST);
				return;
			}	
			count = 0;
			ps.addPlayerBar(obj, count, location);
		}
		
		ByteBuffer buff = new ByteBuffer(16);
		buff.writeInt(location);
		buff.writeInt(id);
		buff.writeInt(type);
		buff.writeInt(count);
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_SET_PLAYER_SHORTCUT_BAR_COMMAND,buff));
	}
	

	
	/**
	 * 取消物品快捷方式 
	 * @param target
	 * @param buffer
	 */
	public void cancelGoodsShortcut(PlayerController target,ByteBuffer buffer)
	{
		int location = buffer.readInt();
		if(location < 0)
			return;
		PlayerSetting ps = (PlayerSetting) target.getPlayer().getExtPlayerInfo("playerSetting"); 
		ps.deletePlayerBar(location);
		ByteBuffer buff = new ByteBuffer(4);
		buff.writeInt(location);
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_CANCEL_GOODS_SHORTCUT_COMMAND,buff));
	}
	
	/**
	 * 取得物品具体信息
	 * @param target
	 * @param buffer
	 */
	public void getGoodsInfo(PlayerController target,ByteBuffer buffer)
	{
		int playerId = buffer.readInt();
		String objectIndex = buffer.readUTF();
		if(objectIndex.isEmpty())
			return;
		Goods goods = null;
		if(target.getID() == playerId)
			 goods = getGoodsByObjectIndex(Long.parseLong(objectIndex));
		else
		{
			PlayerController player = target.getRoom().getPlayer(playerId);
			if(player == null)
				player = target.getWorldManager().getPlayer(playerId);
			if(player == null)
				return;
			Bag bag = (Bag) player.getPlayer().getExtPlayerInfo("bag");
			goods = bag.getGoodsByObjectIndex(Long.parseLong(objectIndex));
		}
		if(goods == null)
		{
			//物品不存在
			target.sendAlert(ErrorCode.ALERT_GOODS_SALED);
			return;
		}
		sendGoodsInfo(target,goods);
	}
	
	public void sendGoodsInfo(PlayerController target,Goods goods)
	{
		ByteBuffer buffer = new ByteBuffer();
		goods.writeTo(buffer);
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_GET_GOODS_INFO_COMMAND,buffer));
	}

	/**
	 * 拆分物品
	 * @param target
	 * @param buffer
	 */
	public void splitGoods(PlayerController target,ByteBuffer buffer)
	{
		long objectIndex = Long.parseLong(buffer.readUTF());
		int count = buffer.readInt();
		int location = buffer.readInt();
		if(count <= 0 || location < 0)
		{
			System.out.println("Bag splitGoods count is zero:"+count+" location:"+location+" playerName:"+target.getName());
			return;
		}
		Object goodsObject = getGoodsByObjectIndex(objectIndex);
		if(goodsObject == null)
		{
			//物品不存在
			target.sendAlert(ErrorCode.ALERT_GOODS_NOT_EXIST);
			return;
		}
		Goods goods = (Goods) goodsObject;
		if(goods.repeatNumber > 1)
		{
			if(count < goods.goodsCount)
			{
				goods.goodsCount -= count; 
				updateGoodsList(goods);
			}
			else
			{
				target.sendAlert(ErrorCode.ALERT_GOODS_COUNT_OVERRUN);
				return;
			}
		}
		else
		{
			target.sendAlert(ErrorCode.ALERT_GOODS_NOT_SPLIT);
			return;
		}
		if(isBagFull(target))
			return;
		ByteBuffer buff = new ByteBuffer(20);
		if(goodsList[location] == null)
		{
			Goods newGoods = (Goods) RPGameObject.cloneObject(goods);
			newGoods.objectIndex = target.getWorldManager().getDatabaseAccessor().getGoodsObjIndex();
			newGoods.goodsCount = count;
			goodsList[location] = newGoods;
			buff.writeUTF(goods.objectIndex+"");
			buff.writeInt(goods.goodsCount);
			buff.writeInt(getGoodsLocation(goods.objectIndex));
			buff.writeUTF(newGoods.objectIndex+"");
			buff.writeInt(newGoods.goodsCount);
			buff.writeInt(location);
		}
		else if(goodsList[location].id == goods.id)
		{
			if(count+goodsList[location].goodsCount <= goodsList[location].repeatNumber)
				goodsList[location].goodsCount += count;
			else
			{
				if(!isCanAddGoodsToBag(target, goods))
					return;
				count -= goods.repeatNumber - goodsList[location].goodsCount;
				goodsList[location].goodsCount = goodsList[location].repeatNumber;
				Goods newGoods = (Goods) RPGameObject.cloneObject(goods);
				newGoods.objectIndex = target.getWorldManager().getDatabaseAccessor().getGoodsObjIndex();
				newGoods.goodsCount = count;
				sendAddGoods(target, newGoods);
			}
			buff.writeUTF(goods.objectIndex+"");
			buff.writeInt(goods.goodsCount);
			buff.writeInt(getGoodsLocation(goods.objectIndex));
			buff.writeUTF(goodsList[location].objectIndex+"");
			buff.writeInt(goodsList[location].goodsCount);
			buff.writeInt(location);
		}
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_SPLIT_GOODS_COMMAND,buff));
	}
	
	/**
	 * 叠加物品
	 * @param target
	 * @param buffer
	 */
	public void superposeGoods(PlayerController target,ByteBuffer buffer)
	{
		long objectIndex1 = Long.parseLong(buffer.readUTF());
		long objectIndex2 = Long.parseLong(buffer.readUTF());
		Object goodsObject1 = getGoodsByObjectIndex(objectIndex1);
		Object goodsObject2 = getGoodsByObjectIndex(objectIndex2);
		if(goodsObject1 == null || goodsObject2 == null)
		{
			//物品不存在
			target.sendAlert(ErrorCode.ALERT_GOODS_NOT_EXIST);
			return;
		}
		Goods goods1 = (Goods) goodsObject1;
		Goods goods2 = (Goods) goodsObject2;
		ByteBuffer buff = new ByteBuffer(20);
		if(goods1.id == goods2.id && goods1.repeatNumber > 1)
		{
			int count = goods1.goodsCount + goods2.goodsCount;
			if(count <= goods2.repeatNumber)
			{
				goods2.goodsCount = count;
				goods1.goodsCount = 0;
			}
			else 
			{
				goods2.goodsCount = goods2.repeatNumber;
				goods1.goodsCount = count - goods1.repeatNumber;	
			}
			buff.writeUTF(goods1.objectIndex+"");
			buff.writeInt(getGoodsLocation(goods1.objectIndex));
			buff.writeInt(goods1.goodsCount);
			buff.writeUTF(goods2.objectIndex+"");
			buff.writeInt(getGoodsLocation(goods2.objectIndex));
			buff.writeInt(goods2.goodsCount);
			updateGoodsList(goods1);
			updateGoodsList(goods2);
		}
		else
		{
			//不能叠加
			target.sendAlert(ErrorCode.ALERT_GOODS_NOT_SUPERPOSE);
			return;
		}
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_SUPERPOSE_GOODS_COMMAND,buff));
	}
	

	
	
	public Goods[] getGoodsList() {
		return goodsList;
	}
	
	
	/**
	 * 判断玩家背包是否满了(true为满了，false为还有空位置)
	 * @return
	 */
	public boolean isBagFull()
	{
		if(getNullLocation() == -1)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * 判断玩家背包是否满了(true为满了，false为还有空位置)
	 * @return
	 */
	public boolean isBagFull(PlayerController target)
	{
		if(getNullLocation() == -1)
		{
			target.sendAlert(ErrorCode.ALERT_BAG_IS_FULL);
			return true;
		}
		return false;
	}
	
	/**
	 * 根据流水获取物品在背包的位置
	 * @param objectIndex
	 * @return
	 */
	public int getGoodsLocation(long objectIndex)
	{
		for (int i = 0; i < goodsList.length; i++) 
		{
			if(goodsList[i] != null)
			{
				if(goodsList[i].objectIndex == objectIndex)
				{
					return i;
				}
			}
		}
		return 0;
	}
	
	/**
	 * 获取一个空位
	 * @return
	 */
	public int getNullLocation()
	{
		for (int i = 0; i < goodsList.length; i++) 
		{
			if(goodsList[i] == null && i <= 47)//第47顺位后的位置空间是属于穿在身上的装备的，是隐藏的
			{
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * 根据类型获取物品
	 * @param type
	 * @return
	 */
	public Goods getGoodsByType(int type)
	{
		for (int i = 0; i < goodsList.length; i++) 
		{
			if(goodsList[i] == null || goodsList[i].useFlag)
				continue;
			if(goodsList[i].type == type)
				return goodsList[i];
		}
		return null;
	}
	
	/**
	 * 获取背包中物品数量没有到达最大迭加数量的物品
	 * @param id
	 * @return
	 */
	public Goods getGoods(Goods goods)
	{
		for (int i = 0; i < goodsList.length; i++) 
		{
			if(goodsList[i] != null)
			{
				if(goodsList[i].id == goods.id && goodsList[i].goodsCount < goodsList[i].repeatNumber)
				{
					return goodsList[i];
				}
			}
		}
		return null;
	}
	
	/**
	 * 获取所有同类物品(数量小于可堆叠数量的)
	 * @return
	 */
	public List getSameGoods(Goods	goods)
	{
		List list = new ArrayList();
		for (int i = 0; i < goodsList.length; i++) 
		{
			if(goodsList[i] == null)
				continue;
			if(goodsList[i].id == goods.id && goodsList[i].goodsCount < goodsList[i].repeatNumber
					&& goodsList[i].bindMode == goods.bindMode)
				list.add(goodsList[i]);
		}
		return list;
	}
	
	/**
	 * 获取装备中未绑定的物品
	 * @param id
	 * @return
	 */
	public Goods getGoodsByBindmode(int id)
	{
		for (int i = 0; i < goodsList.length; i++) 
		{
			if(goodsList[i] != null)
			{
				if(goodsList[i].id == id && goodsList[i].bindMode != 4)
				{
					return goodsList[i];
				}
			}
		}
		return null;
	}

	/**
	 * 根据ID获取背包中物品
	 * @param id
	 * @return
	 */
	public Goods getGoodsById(int id)
	{
		for (int i = 0; i < goodsList.length; i++) 
		{
			if(goodsList[i] != null)
			{
				if(goodsList[i].id == id)
				{
					return goodsList[i];
				}
			}
		}
		return null;
	}
	
	/**
	 * 根据流水号获取物品
	 * @param objectIndex
	 * @return
	 */
	public Goods getGoodsByObjectIndex(long objectIndex)
	{
		for (int i = 0; i < goodsList.length; i++) 
		{
			if(goodsList[i] == null)
				continue;
			Goods goods = goodsList[i];
			if(goods.objectIndex == objectIndex)
			{
				return goods;
			}
		}
		return null;
	}
	
	/**
	 * 获取同类物品的总数量(绑定不绑定一起算)
	 * @param type
	 * @return
	 */
	public int getGoodsCountByType(int type)
	{
		int count = 0;
		for (int i = 0; i < goodsList.length; i++) 
		{
			if(goodsList[i] != null)
			{
				if(goodsList[i].type == type)
				{
					count += goodsList[i].goodsCount;
				}
			}
		}
		return count;
	}
	
	/**
	 * 获取同类物品的总数量
	 * @param goods
	 * @return
	 */
	public int getGoodsCountByBindMode(int id)
	{
		int count = 0;
		for (int i = 0; i < goodsList.length; i++) 
		{
			if(goodsList[i] != null)
			{
				if(goodsList[i].id == id && goodsList[i].bindMode != 4 && !goodsList[i].useFlag)
				{
					count += goodsList[i].goodsCount;
				}
			}
		}
		return count;
	}
	
	
	public int getGoodsCount(int id)
	{
		int count = 0;
		for (int i = 0; i < goodsList.length; i++) 
		{
			if(goodsList[i] != null)
			{
				if(goodsList[i].id == id && !goodsList[i].useFlag)
				{
					count += goodsList[i].goodsCount;
				}
			}
		}
		return count;
	}
	
	public int getGoodsCountById(int id)
	{
		int count = 0;
		for (int i = 0; i < goodsList.length; i++) 
		{
			if(goodsList[i] != null)
			{
				if(goodsList[i].id == id)
				{
					count += goodsList[i].goodsCount;
				}
			}
		}
		return count;
	}
	
	
	/**
	 * 更新背包
	 * @param goods
	 */
	public void updateGoodsList(Goods goods)
	{
		for (int i = 0; i < goodsList.length; i++) 
		{
			if(goodsList[i] != null)
			{
				if(goodsList[i].objectIndex == goods.objectIndex)
				{
					if(goodsList[i].goodsCount <= 0)
						goodsList[i] = null;
					else
						goodsList[i] = goods;
					break;
				}
			}
		}
	}
	
	/**
	 * 获取背包中同类物品
	 * @param id
	 * @return
	 */
	public List getGoodsSameId(Goods goods)
	{
		List list = new ArrayList();
		for (int i = 0; i < goodsList.length; i++) 
		{
			if(goodsList[i] != null)
			{
				if(goodsList[i].id == goods.id)
				{
					list.add(goodsList[i]);
				}
			}
		}
		return list;
	}
	
	/**
	 * 获取装备面板上是否有同类装备已经装备
	 * @param equipList
	 * @param location
	 * @return  有同类装备则返回已经装备的装备，没有则返回NULL
	 */
	public Goods getSameLocationGoods(int location)
	{
		for (int i = 0; i < goodsList.length; i++) 
		{
			if(goodsList[i] != null && goodsList[i].type == 1 && goodsList[i].useFlag)
			{
				GoodsEquip equip = (GoodsEquip) goodsList[i];
				if(equip.equipLocation == location)
					return equip;
			}
		}
		return null;
	}
	
	
	/**
	 * 拆解
	 * @param target
	 * @param buffer
	 */
	private void dShopProcess(PlayerController target, ByteBuffer buffer)
	{
		
		long equipObjectIndex = Long.parseLong(buffer.readUTF().trim());
		
		Goods goods = getGoodsByObjectIndex(equipObjectIndex);
		
		if(goods == null)
		{
			target.sendAlert(ErrorCode.ALERT_DS_PROCESS_FAIL);
			//System.out.println("dShopProcess goods is null : "+equipObjectIndex);
			return;
		}
		
		if(!(goods instanceof GoodsEquip))
		{
			//target.sendError("该物品不是装备不能合成");
			System.out.println("this item not equip dont ds id: "+goods.id);
			return;
		}
		
		GoodsEquip equip = (GoodsEquip)goods;
		
		if(equip.quality <= 0)
		{
			target.sendAlert(ErrorCode.ALERT_GREENDOWN_NOT_DS);
			//target.sendError("绿色以下的装备不能拆解");
			return;
		}
		
		if(point < (equip.point/8))
		{
			target.sendAlert(ErrorCode.ALERT_SHOP_NO_POINT);
			//target.sendError("金钱不够，不能拆解");
			return;
		}
		
		int p = SShop.getShop().addRandomGoods(target,this,equip);
		if(p == -1)
		{
			target.sendAlert(ErrorCode.ALERT_DS_GOODS_NOT);
			//System.out.println("dShop fail "+goods.id);
			return;
		}
		
		point -= p;
		buffer = new ByteBuffer(1);
//		buffer.writeInt((int) point);
		WorldManager.sendPoint(buffer, point);
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_BAG_DSHOP_COMMAND,buffer));
	}
	

	
	/**
	 * 合成
	 * @param target
	 * @param buffer
	 */
	private void sShopProcess(PlayerController target, ByteBuffer buffer)
	{
		if(isBagFull())
		{
			target.sendAlert(ErrorCode.ALERT_BAG_IS_FULL);
			return;
		}
		SShop sshop = SShop.getShop();
		
		int gsId = buffer.readInt();
		
		GoodsSynt gs = sshop.getEquipSynt(gsId);
		
		if(gs == null)
		{
			//target.sendError("你要打造的物品没有");
			System.out.println("Bag sShopProcess "+gsId + " null");
			return;
		}
		
		if(point < gs.point || money < gs.money)
		{
			target.sendAlert(ErrorCode.ALERT_SHOP_NO_POINT);
			return;
		}
		
		
		int goodsCount = 0;
		
		for (int i = 0; i < gs.materialsIds.length; i++)
		{
			if(gs.materialsIds[i] != 0)
				goodsCount++;
			else
				break;
		}
		
		if(goodsCount == 0)
		{
			System.out.println("Bag sShopProcess "+gs.id + " materialsIds is Error");
			return;
		}
		

		HashMap map = new HashMap();

		for (int i = 0; i < goodsCount; i++)
		{
			if(!checkGoodsEnough(gs.materialsIds[i], gs.materialsCount[i]))
			{
				target.sendAlert(ErrorCode.ALERT_GOODS_ENOUGH);
				return;
			}
			
			map.put(gs.materialsIds[i], gs.materialsCount[i]);
		}
		
		
		if(!(sshop.check(gs.gsId,map)))
		{
			System.out.println("GoodsSynt diffents of config file with id "+gs.id);
			//System.out.println("打造的和配置文件的不符合");
			return;
		}

//		GoodsEquip equip = sshop.getEquip(gs.id);
		Goods goods = sshop.getGoods(gs.id,gs.rates);
		
		if(goods == null)
		{
			System.out.println("Bag sShop fail by id "+gs.id);
			return;
		}
		
		for (int i = 0; i < goodsCount; i++)
		{
			deleteGoods(target, gs.materialsIds[i], gs.materialsCount[i]);
		}
		
		point -= gs.point;
		money -= gs.money;

		sendAddGoods(target, goods);
		
		ByteBuffer buff = new ByteBuffer(1);
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_BAG_SSHOP_COMMAND,buff));
	
		if(target.getPlayer().isVipPlayer)
		{
			StringBuffer data = new StringBuffer(DC.getString(DC.GOODS_3));
			data.append(goods.name);
			data.append(",");
			data.append(DC.getString(DC.GOODS_4));
			for (int i = 0; i < goodsCount; i++) 
			{
				Goods g = (Goods) DataFactory.getInstance().getGameObject(gs.materialsIds[i]);
				if(g == null)
					continue;
				data.append(g.name);
				data.append("x");
				data.append(gs.materialsCount[i]);
				data.append(",");
			}
			data.append("money:");
			data.append(gs.money);
			data.append(",point:");
			data.append(gs.point);
			DataFactory.getInstance().addVipPlayerInfo(target, data.toString());
		}
	}


	/**
	 * 材料合成
	 * @param target
	 * @param buffer
	 */
	private void synthetizeProcess(PlayerController target, ByteBuffer buffer)
	{
		int rate = buffer.readByte();
		int id = buffer.readInt();
		int count = buffer.readInt();
		if(count < 0)
			return;
		if(rate < 3)
			return;
		
		if(!checkGoodsEnough(id,count))
		{
			target.sendAlert(ErrorCode.ALERT_GOODS_ENOUGH);
			return;
		}
		
		
		Goods goods = getGoodsById(id);
		
		if(goods.type != 4)
		{
//			System.out.println("synthetizeProcess not cl~s");
			return;
		}
		
		if(String.valueOf(goods.id).endsWith("5"))
		{
			return;
		}
		
		int nextLvCount = count/rate;
		int lessCount = count%rate;
		int delCount = count - lessCount;

		delCount = deleteGoods(target, id, delCount);
		
		
		int nextLvFailCount = 0;
		
		int attaRate = target.getPlayer().getBaseInfo().syntSuccesRate;
		
		for (int i = 0, j = nextLvCount; i < j; i++)
		{
			int ran = (int)(Math.random()*100);
			
			if(rate == 3 && ran >= (50+attaRate))
			{
				nextLvCount--;
				nextLvFailCount++;
			}
			else if(rate == 4 && ran >= (75+attaRate))
			{
				nextLvCount--;
				nextLvFailCount++;
			}
		}

		
		buffer = new ByteBuffer(8);
		
		if(nextLvCount != 0)
		{
			addGoods(target, ++id, nextLvCount);
			
		}
		buffer.writeInt(nextLvCount);
		buffer.writeInt(nextLvFailCount);
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_BAG_SYNTHETIZE_COMMAND,buffer));
	}
	
	/**
	 * 删除未绑定的物品
	 * @param target
	 * @param id
	 * @param delCount
	 * @return
	 */
	public int deleteGoodsByBindmode(PlayerController target,int id,int delCount)
	{
//		int count = delCount;
		
		Goods goods = null;
		
		while(delCount > 0)
		{
			goods = getGoodsByBindmode(id);
		
			if(goods != null)
			{	

				if(goods.goodsCount == 0)
					break;

				if(goods.goodsCount <= 0)
				{
					System.out.println("Bag goods count <= 0" +target.getName()+"  "+ id);
					goods = null;
					continue;
				}

				if(delCount >= goods.goodsCount)
				{
					delCount -= goods.goodsCount;
					removeGoods(target, goods.objectIndex, goods.goodsCount);
				}
				else
				{
					removeGoods(target, goods.objectIndex, delCount);
					delCount = 0;
				}
			}
			else
			{
				System.out.println("Bag deleteGoods id="+id);
				delCount = 0;
				break;
			}
		}
		
//		TaskInfo info = (TaskInfo)target.getPlayer().getExtPlayerInfo("taskInfo");
//		info.onPlayerLostItem(id,count, target);
		
		return delCount;
	}

	/**
	 * 删除指定数量的goods
	 * @param target
	 * @param id
	 * @param delCount
	 * @return
	 */
	public int deleteGoods(PlayerController target, int id, int delCount)
	{
//		int count = delCount;
		
		Goods goods = null;
		
		while(delCount > 0)
		{
			goods = getGoodsById(id);
		
			if(goods != null)
			{	

				if(goods.goodsCount == 0)
					break;

				if(goods.goodsCount <= 0)
				{
					System.out.println("Bag goods count <= 0" +target.getName()+"  "+ id);
					goods = null;
					continue;
				}

				if(delCount >= goods.goodsCount)
				{
					delCount -= goods.goodsCount;
					removeGoods(target, goods.objectIndex, goods.goodsCount);
				}
				else
				{
					removeGoods(target, goods.objectIndex, delCount);
					delCount = 0;
				}
			}
			else
			{
				System.out.println("Bag deleteGoods id="+id);
				delCount = 0;
				break;
			}
		}
		
//		TaskInfo info = (TaskInfo)target.getPlayer().getExtPlayerInfo("taskInfo");
//		info.onPlayerLostItem(id,count, target);
		
		return delCount;
	}
	

	public int getBagSize() {
		return bagSize;
	}


	public void setBagSize(int bagSize) {
		this.bagSize = bagSize;
	}
	
	


	public boolean checkGoodsEnough(int goodId, int goodCount)
	{
		int count = 0;
		
		for (int i = 0; i < goodsList.length; i++)
		{
			if(i > 47)
				break;;
			
			if(goodsList[i] == null)
				continue;
			
			if(goodsList[i].id == goodId)
			{
				if(goodsList[i].goodsCount >= goodCount) 
					return true;
				else
				{
					count += goodsList[i].goodsCount;
					
					if(count >= goodCount)
						return true;
				}
			}
		}
		return false;
	}
	
	public void removeBusinessGoods(PlayerController target,long objectIndex)
	{
		for (int i = 0; i < bagSize; i++)
		{
			if(goodsList[i] != null && goodsList[i].objectIndex == objectIndex)
			{	
				TaskInfo info = (TaskInfo)target.getPlayer().getExtPlayerInfo("taskInfo");
				info.onPlayerLostItem(goodsList[i].id,goodsList[i].goodsCount, target);
				
				sendDeleteGoods(target,goodsList[i].objectIndex+"",goodsList[i].goodsCount);
				goodsList[i] = null;
				break;
			}
		}
	}

	
	public boolean removeGoods(PlayerController target,long objectIndex , int goodCount)
	{
		int delCount = goodCount;

		int count = goodCount;
		
		int id = 0;
		
		for (int i = 0; i < goodsList.length; i++)
		{
			if(goodsList[i] == null)
				continue;
			
			if(goodsList[i].objectIndex == objectIndex)
			{
				id = goodsList[i].id;
				if(count <= 0)
					break;
				if(goodsList[i].repeatNumber > 1) //可叠加
				{
					if(count >= goodsList[i].goodsCount)
					{
						sendDeleteGoods(target,goodsList[i].objectIndex+"",goodsList[i].goodsCount);
						count -= goodsList[i].goodsCount;
						goodsList[i].goodsCount = 0;
					}
					else
					{
						sendDeleteGoods(target,goodsList[i].objectIndex+"",count);
						goodsList[i].goodsCount -= count;
					}
				}
				else
				{
					sendDeleteGoods(target,goodsList[i].objectIndex+"",goodsList[i].goodsCount);
					goodsList[i].goodsCount = 0;
					count--;
				}
				PlayerSetting ps = (PlayerSetting) target.getPlayer().getExtPlayerInfo("playerSetting");
				ps.updatePlayerBar(goodsList[i].id, goodsList[i].goodsCount);
				updateGoodsList(goodsList[i]);
			}
		}
		
		if(id != 0)
		{
			TaskInfo info = (TaskInfo)target.getPlayer().getExtPlayerInfo("taskInfo");
			info.onPlayerLostItem(id,delCount, target);
			
			target.sendActivePoint();
		}
		
		GameServer.getInstance().getWorldManager().getJobObserver().addJob(GameServer.JOB_DATABASEJOB,
				new SaveJob(GameServer.getInstance().getWorldManager(),target.getPlayer(),SaveJob.GOODS_DEL_SAVE));

		return true;
	}
	
	public void sendDeleteGoods(PlayerController target,String objectIndex,int goodsCount)
	{
		ByteBuffer buff = new ByteBuffer(6);
		buff.writeUTF(objectIndex);
		buff.writeInt(goodsCount);
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_DELETE_GOODS_FROM_BAG_COMMAND, buff));
	}
	
	/**
	 * 战斗加物品到背包
	 * @param target
	 * @param goods
	 */
	public void sendBattleAddGoods(PlayerController target,Goods goods)
	{
		ByteBuffer buffer = new ByteBuffer();
		if(goods != null)
		{
			buffer.writeByte(1);
			addGoodsToBag(target,goods,buffer);
		}
		else
			buffer.writeByte(0);
//		buffer.writeInt((int) point);
		WorldManager.sendPoint(buffer, point);
		buffer.writeInt((int) money);
		buffer.writeInt((int) equipMoney);
		buffer.writeInt(target.getHonour());//荣誉值
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_ADD_GOODS_COMMAND,buffer));
	
		GameServer.getInstance().getWorldManager().getJobObserver().addJob(GameServer.JOB_DATABASEJOB,
				new SaveJob(GameServer.getInstance().getWorldManager(),target.getPlayer(),SaveJob.GOODS_ADD_SAVE));
	}
	
	
	/**
	 * 非战斗加物品到背包
	 * @param target
	 * @param goods
	 */
	public void sendAddGoods(PlayerController target,Goods goods)
	{
		ByteBuffer buffer = new ByteBuffer();
		if(goods != null)
		{
			if(goods.goodsCount > goods.repeatNumber)
			{
				return;
			}
			goods.pickUpBind();
			if(!isCanAddGoodsToBag(target, goods))
			{
				return;
			}
			int count = goods.getGoodsCount();
			
			buffer.writeByte(1);
			addGoodsToBag(target,goods,buffer);
			
			TaskInfo info = (TaskInfo)target.getPlayer().getExtPlayerInfo("taskInfo");
			info.onPlayerGotItem(goods.id,count, target);
		}
		else
		{
			buffer.writeByte(0);
		}
//		buffer.writeInt((int) point);
		WorldManager.sendPoint(buffer, point);
		buffer.writeInt((int) money);
		buffer.writeInt((int) equipMoney);
		buffer.writeInt(target.getHonour());//荣誉值
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_ADD_GOODS_COMMAND,buffer));
	
		GameServer.getInstance().getWorldManager().getJobObserver().addJob(GameServer.JOB_DATABASEJOB,
				new SaveJob(GameServer.getInstance().getWorldManager(),target.getPlayer(),SaveJob.GOODS_ADD_SAVE));
	}
	
	
	/**
	 * 发送战斗中使用物品信息
	 * @param goods
	 * @param target
	 * @param buffer
	 */
	public void sendBattlePropInfo(Goods goods,PlayerController target)
	{
		GoodsProp prop = (GoodsProp) goods;
		ByteBuffer buffer = new ByteBuffer();
		Effect ef = (Effect) DataFactory.getInstance().getGameObject(prop.effect);
		if(ef == null)
			return;
		Effect effect = (Effect) Effect.cloneObject(ef);
		buffer.writeByte(BattleController.PROPPROCESSOR); //释放道具
		buffer.writeByte(target.getAttachment().getTeamNo());//使用道具者的组编号
		buffer.writeByte(target.getAttachment().getIndex()); //使用者道具者的index;
		buffer.writeInt(prop.id);
		buffer.writeByte(2);
		
		//测试资源
		buffer.writeByte(3);
		

		buffer.writeInt(prop.effect);
		if(effect instanceof FlashEffect)
			buffer.writeByte(1);
		if(effect instanceof TimeEffect)
			buffer.writeByte(2);
		
		target.getAttachment().pack(buffer);
		
		buffer.writeInt(target.getPlayer().magicPoint);
		
		target.getParent().dispatchMsg(SMsg.S_BATTLE_ACTION_COMMAND, buffer);
	}

	
	/**
	 * 发送战斗外使用道具
	 * @param goods
	 * @param buff
	 * @param target
	 */
	public void sendOutBattlePropInfo(Goods goods,PlayerController target)
	{
		ByteBuffer buffer = new ByteBuffer();
		GoodsProp prop = (GoodsProp) goods;
		if(prop.type == 8)//技能书
		{
			buffer.writeInt(prop.skillId);
			SkillTome st = (SkillTome) target.getPlayer().getExtPlayerInfo("skillTome");
			Skill skill = st.getSkill(prop.skillId);
			if(skill == null)
			{
				target.sendAlert(ErrorCode.ALERT_SKILL_NOT_EXIST);
				return;
			}
			buffer.writeInt(skill.iconId);
			buffer.writeInt(skill.level);
			if(skill instanceof ActiveSkill)
				buffer.writeInt(((ActiveSkill)skill).magic);
			else
				buffer.writeInt(0);
			buffer.writeBoolean(skill.isPubSkill);
			target.getNetConnection().sendMessage(new SMsg(SMsg.S_STUDY_SKILL_COMMAND,buffer));
			
			if(skill instanceof PassiveSkill)
			{
				sendPlayerEquipSet(new PlayerController[]{target});
				
				target.sendAlwaysValue();
			}
			
			PlayerSetting ps = (PlayerSetting) target.getPlayer().getExtPlayerInfo("playerSetting");
			ps.updatePlayerBar(skill);
			
			getPlayerShotcutBar(target);
		}
		else if(prop.type == 2)
		{
			target.sendAlwaysValue();
		}
		else if(prop.type == 9 || prop.type == 16 || prop.type == 23)
		{
			sendAddGoods(target, null);
		}
		else if(prop.type == 11 || prop.type == 24)
		{
			target.sendAlwaysValue();

			if(prop.expMult > 0 && prop.expTimes > 0)
			{
				sendExpBuff(target,prop.effect,true,(int) prop.expTimes);
			}
		}
		else if(prop.type == 12)
		{
			sendExpBuff(target,prop.effect,true,0);
		}
		else if(prop.type == 14)
		{
//			//学会宠物技能
//			buffer.writeByte(3);
//			buffer.writeInt(prop.skillId);
//			Pet pet = (Pet) target.getPlayer().getExtPlayerInfo("pet");
//			Skill skill = pet.getSkill(prop.skillId);
//			if(skill == null)
//			{
////				target.sendAlert(ErrorCode.ALERT_SKILL_NOT_EXIST);
//				return;
//			}
//			buffer.writeInt(skill.iconId);
//			buffer.writeInt(skill.level);
//			target.getNetConnection().sendMessage(new SMsg(SMsg.S_PLAYER_PETINFO_OPTION_COMMAND,buffer));
		}
		else if(prop.type == 15)
		{
			target.sendPetInfo(target, target.getID());
		}
		else if(prop.type == 17)
		{
			target.sendFlyActivePoint();
		}
		else if(prop.type == 18)
		{
			Storage storage = (Storage) target.getPlayer().getExtPlayerInfo("storage");
			storage.sendStorageSize(target);
		}
		else if(prop.type == 19)
		{
			target.sendPetInfo(target, target.getID());
		}
	}
	
	/**
	 * 
	 * @param id 
	 * @param flag true表示添加BUFF，false表示删除BUFF
	 *  @param remainTime 多倍经验卡剩余时间
	 */
	public void sendExpBuff(PlayerController target,int id,boolean flag,int remainTime)
	{
		ByteBuffer buffer = new ByteBuffer();
		buffer.writeInt(target.getID());
		buffer.writeInt(id);
		buffer.writeBoolean(flag);//true表示添加BUFF，false表示删除BUFF
		if(flag)
		{
			buffer.writeByte(2);//不消失
			buffer.writeByte(1);//正面BUFF
			buffer.writeInt(target.getPlayer().extLife);
			buffer.writeInt(target.getPlayer().extMagic);
			buffer.writeInt(remainTime);
		}
		if(target.getTeam() == null)
			target.getNetConnection().sendMessage(new SMsg(SMsg.S_EXP_BUFF_COMMAND,buffer));
		else
			target.getTeam().dispatchMsg(SMsg.S_EXP_BUFF_COMMAND,buffer);
	}
	
	/**
	 * 物品绑定
	 * @param target
	 * @param buffer
	 */
	private void goodsBind(PlayerController target,ByteBuffer buffer)
	{
		String objectIndex = buffer.readUTF();
		int bindMode = buffer.readByte();
		long index = Long.parseLong(objectIndex);
		if(bindMode <= 0)
			return;
		Goods goods = getGoodsByObjectIndex(index);
		if(goods == null)
		{
			target.sendAlert(ErrorCode.ALERT_GOODS_NOT_EXIST);
			return;
		}
		goods.bindMode = bindMode;
		ByteBuffer buff = new ByteBuffer();
		buff.writeUTF(objectIndex);
		buff.writeByte(bindMode);
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_BAG_GOODS_BIND,buff));
	}
	
	
	private void boxSuccess(PlayerController target,ByteBuffer buffer)
	{
		int type = buffer.readByte();
		if(type == -2)//试练宝箱开始转动
		{
			int boxType = buffer.readByte();//10表示是试练宝箱
			if(boxType != 10)
				return;
			target.getPlayer().copyPoint = 0;//防止重复领取
			target.sendCopyPoint();
		}
		else if(type == -1)//开启试练宝箱
		{
			if(target.getPlayer().copyPoint != Player.MAXCOPYPOINT)
				return;
			if(target.isBox)
				return;
			if(target.getParent() instanceof BattleController)
				return;
			List list = DataFactory.getInstance().getBoxDropPropList();
			List rList = new ArrayList(8);
			for (int i = 0; i < list.size(); i++) 
			{
				BoxDropProp bdp = (BoxDropProp) list.get(i);
				if(bdp == null)
					continue;
				if(bdp.boxType == 6)
				{
					rList.add(bdp);
				}
			}
	
			int cr = (int) (Math.random() * 10000) + 1;
			int bdpRate = 0;
			BoxDropProp cb = null;
			//要修改的元宝抽奖
			for (int i = 0; i < rList.size(); i++) 
			{
				BoxDropProp bdp = (BoxDropProp) rList.get(i);
				bdpRate += bdp.rate;
				if(cr <= bdpRate)
				{
					cb = bdp;
					break;
				}
			}
	
			if(cb == null)
			{
				return;
			}
			list = new ArrayList();
			for (int i = 0; i < 8; i++)
			{
				Goods goods = cb.getGoods(false);
				if(goods == null)
					return;
				list.add(goods);
				
			}	

			if(list.size() == 0 || list == null || list.size() != 8)
				return;
			int random = (int) (Math.random()*list.size());
			buffer.writeByte(0);//普通宝箱
			for (int i = 0; i < 8; i++) 
			{
				Goods goods = (Goods) list.get(i);
				goods.writeTo(buffer);
				buffer.writeInt(goods.goodsCount);
				if(i == random)
				{
					buffer.writeBoolean(true);//取这个物品
					setTmp(goods,DC.getString(DC.GOODS_5));
				}
				else
					buffer.writeBoolean(false);
			}
			buffer.writeByte(10);
			target.getNetConnection().sendMessage(new SMsg(SMsg.S_OPEN_BOX_COMMAND,buffer));
			target.setIsBox(true, 1);
		}
		else if(type == 0)//提取
		{
			if(tmp == null)
				return;
			int boxType = buffer.readByte();//10表示是试练宝箱
		
			tmp.pickUpBind();
			
			if(!isCanAddGoodsToBag(tmp))
			{
				ByteBuffer buff = new ByteBuffer(1);
				buff.writeByte(0);//失败
				target.getNetConnection().sendMessage(new SMsg(SMsg.S_BOX_RESULT_COMMAND,buff));
				return;
			}
			
			String objectIndex = tmp.objectIndex+"";
			if(tmp.repeatNumber > 1)
			{
				Goods goods = getGoodsById(tmp.id);
				if(goods != null)
					objectIndex = goods.objectIndex+"";
			}
			
			StringBuffer sb = new StringBuffer();
			if(!DataFactory.isGoodsNotice(GoodsNotice.NO_NOTICE,tmp) && boxType != 10 && boxType != 13)
			{
				sb.append(DC.getString(DC.GOODS_6));
				sb.append("|");
				sb.append(target.getName());
				sb.append("#u:");
				sb.append(target.getName());
				sb.append("|,");
				sb.append(DC.getString(DC.GOODS_7));
				sb.append("[");
				sb.append(boxName);
				sb.append("]");
				sb.append(DC.getString(DC.GOODS_8));
				sb.append("|[");
				sb.append(tmp.name);
				sb.append("x");
				sb.append(tmp.goodsCount);
				sb.append("]#p:");
				sb.append(objectIndex);
				sb.append(":");
				sb.append(tmp.quality);
				sb.append(":");
				sb.append(target.getID());
				if(boxType >= 5 && boxType != 11)
				{
					target.sendGetGoodsInfo(3,true, sb.toString());//向世界聊天发送信息
				}
				else
				{
					if(tmp.token >= 500)
					{
						//保存消费记录 类型3 抽奖大于500
						GameServer.getInstance().getWorldManager().getJobObserver().addJob(GameServer.JOB_DATABASEJOB,
								new PayJob(target,3,0,tmp.token,GameServer.getInstance().id+":"+target.getName()+":"+tmp.name+":"+tmp.goodsCount));
						
						//>500元宝保存一次
						target.getWorldManager().getJobObserver().addJob(GameServer.JOB_DATABASEJOB,
								new SaveJob(target.getWorldManager(),target.getPlayer(),SaveJob.MONEY_EXPENSE_SAVE));
						 
						target.sendGetGoodsInfo(3,true, sb.toString());//向世界聊天发送信息
					}
					else
					{
						target.sendGetGoodsInfo(1,false, sb.toString());//不公告
					}
				}
			}
			else if(tmp.quality < Goods.NOTICE_QUALITY)
			{
				sb.append(DC.getString(DC.GOODS_8));
				sb.append(": |[");
				sb.append(tmp.name);
				sb.append("]#p:");
				sb.append(objectIndex);
				sb.append(":");
				sb.append(tmp.quality);
				sb.append(":");
				sb.append(target.getID());
				target.sendGetGoodsInfo(1,false, sb.toString());
			}
			
			if(boxType == 10)
			{
				if(DataFactory.isGoodsNotice(GoodsNotice.COPY_BOX, tmp))
				{
					sb = new StringBuffer();
					sb.append(DC.getString(DC.GOODS_6));
					sb.append("|");
					sb.append(target.getName());
					sb.append("#u:");
					sb.append(target.getName());
					sb.append("|,");
					sb.append(DC.getString(DC.GOODS_7));
					sb.append("[");
					sb.append(boxName);
					sb.append("]");
					sb.append(DC.getString(DC.GOODS_8));
					sb.append("|[");
					sb.append(tmp.name);
					sb.append("x");
					sb.append(tmp.goodsCount);
					sb.append("]#p:");
					sb.append(objectIndex);
					sb.append(":");
					sb.append(tmp.quality);
					sb.append(":");
					sb.append(target.getID());
					target.sendGetGoodsInfo(3, true, sb.toString());
				}
			}
						
			sendAddGoods(target, tmp);

			target.setIsBox(false, 0);
			setTmp(null,"");
			
			ByteBuffer buff = new ByteBuffer(1);
			buff.writeByte(1);//成功
			target.getNetConnection().sendMessage(new SMsg(SMsg.S_BOX_RESULT_COMMAND,buff));
			
			if(boxType == 10)//女神宝箱
			{
				AnswerReward ar = DataFactory.getInstance().getAnswerReward(target.getPlayer().level);
				if(ar != null)
				{
					long rewardExp = ar.expReward * 30 + 50 * 10000 * 10000;
					long disExp = target.addExp(rewardExp, true,true,false);
					if(disExp > 0)
						target.sendGetGoodsInfo(1, false, DC.getString(DC.GOODS_9)+": "+disExp);
				}
			}
		}
		else if(type == 1)//关闭
		{
//			target.isBox = false;
			target.setIsBox(false, 0);
			setTmp(null,"");
			
//			System.out.println(target.getName()+" 关闭... "+target.isBox);
		}
		else if(type == 2) //抽奖后的关闭 保存
		{
			target.getWorldManager().getJobObserver().addJob(GameServer.JOB_DATABASEJOB,
					new SaveJob(target.getWorldManager(),target.getPlayer(),SaveJob.MONEY_EXPENSE_SAVE));
		}
			
	}
	
	/**
	 * 生成任务道具
	 * @param target
	 * @param goodsId
	 * @param goodsCount
	 */
	public String addStoryGoods(PlayerController target,int goodsId, int goodsCount)
	{
		Object obj = DataFactory.getInstance().getGameObject(goodsId);

		if(obj == null)
			return "";
		if(!(obj instanceof Goods))
			return "";
		if(!target.isOnline())
			return "";
		Goods goods = (Goods) obj;
		Goods newGoods = null;
		if(goods instanceof GoodsEquip)
		{
			GoodsEquip equip = (GoodsEquip) goods;
			newGoods = equip.makeNewBetterEquip
			(equip.taskColor==-1?equip.quality :equip.taskColor);
		}
		else if(goods instanceof GoodsPetEquip)
		{
			newGoods = (Goods) Goods.cloneObject(goods);
		}
		else
		{
			newGoods = (Goods) Goods.cloneObject(goods);
		}
		if(newGoods == null)
		{
			System.out.println("Bag getTaskGoods goodsId:"+goodsId);
			return "";
		}
		newGoods.objectIndex = GameServer.getInstance().getDatabaseAccessor().getGoodsObjIndex();
		StringBuffer returnStr = new StringBuffer();
		returnStr.append(newGoods.name);
		returnStr.append(":");
		returnStr.append(newGoods.objectIndex);
		returnStr.append(":");
		returnStr.append(newGoods.quality);
		if(newGoods.repeatNumber > 1)
		{
			newGoods.goodsCount = goodsCount;
			sendAddGoods(target, newGoods);
		}
		else
		{
			if(getNullCount() < goodsCount)
			{
				target.sendAlert(ErrorCode.ALERT_BAG_IS_FULL);
				return "";
			}
			target.sendFacebookInfo(1, newGoods, "", 0);
			sendAddGoods(target, newGoods);
			if(goodsCount > 1)
			{
				for (int i = 1; i < goodsCount; i++)
				{
					Goods ng = (Goods) Goods.cloneObject(newGoods);
					ng.objectIndex = target.getWorldManager().getDatabaseAccessor().getGoodsObjIndex();
					
					target.sendFacebookInfo(1, ng, "", 0);
					sendAddGoods(target, ng);
				}
			}
		}
		return returnStr.toString();
	}
	
	/**
	 * 生成任务道具
	 * @param target
	 * @param goodsId
	 * @param goodsCount
	 */
	public boolean addTaskGoods(PlayerController target,int goodsId , int goodsCount)
	{
		Object obj = DataFactory.getInstance().getGameObject(goodsId);

		if(obj == null)
			return false;
		if(!(obj instanceof Goods))
			return false;
		if(!target.isOnline())
			return false;
		Goods goods = (Goods) obj;
		Goods newGoods = null;
		if(goods instanceof GoodsEquip)
		{
			GoodsEquip equip = (GoodsEquip) goods;
			newGoods = equip.makeNewBetterEquip
			(equip.taskColor==-1?equip.quality :equip.taskColor);
		}
		else if(goods instanceof GoodsPetEquip)
		{
			newGoods = (Goods) Goods.cloneObject(goods);
		}
		else
		{
			newGoods = (Goods) Goods.cloneObject(goods);
		}
		if(newGoods == null)
		{
			System.out.println("Bag getTaskGoods goodsId:"+goodsId);
			return false;
		}
		newGoods.objectIndex = GameServer.getInstance().getDatabaseAccessor().getGoodsObjIndex();

		if(newGoods.repeatNumber > 1)
		{
			newGoods.goodsCount = goodsCount;
			sendAddGoods(target, newGoods);
		}
		else
		{
			if(getNullCount() < goodsCount)
			{
				target.sendAlert(ErrorCode.ALERT_BAG_IS_FULL);
				return false;
			}
			target.sendFacebookInfo(1, newGoods, "", 0);
			sendAddGoods(target, newGoods);
			if(goodsCount > 1)
			{
				for (int i = 1; i < goodsCount; i++)
				{
					Goods ng = (Goods) Goods.cloneObject(newGoods);
					ng.objectIndex = target.getWorldManager().getDatabaseAccessor().getGoodsObjIndex();
					
					target.sendFacebookInfo(1, ng, "", 0);
					sendAddGoods(target, ng);
				}
			}
		}
		return true;
	}
	
	private void getTaskGoods(PlayerController target,ByteBuffer inBuffer)
	{
		int goodsId = inBuffer.readInt();
	
		Object obj = DataFactory.getInstance().getGameObject(goodsId);

		if(obj == null)
			return;
		if(!(obj instanceof Goods))
			return;
		if(!target.isOnline())
			return;
		Goods goods = (Goods) obj;
		Goods newGoods = null;

		ByteBuffer outBuffer = new ByteBuffer();
		if(goods instanceof GoodsEquip)
		{
			newGoods = ((GoodsEquip) goods).makeNewBetterEquip(((GoodsEquip) goods).taskColor);
		}
		else if(goods instanceof GoodsPetEquip)
		{
			newGoods = (Goods) Goods.cloneObject(goods);
		}
		else if(goods instanceof GoodsProp)
		{
			newGoods = (Goods) Goods.cloneObject(goods);
		}
		if(newGoods == null)
			return;
		newGoods.writeTo(outBuffer);
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_GET_TASK_GOODS_COMMAND,outBuffer));
	}
	
	
	public void sendMemoryStone(PlayerController target,String objectIndex,String roomName)
	{
		ByteBuffer outBuffer = new ByteBuffer();
		outBuffer.writeUTF(objectIndex+"");
		outBuffer.writeUTF(roomName);
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_MEMORY__STONE_COMMAND,outBuffer));

	}
	
	
	private void playBossBox(PlayerController target,ByteBuffer buffer)
	{
		String objectIndex = buffer.readUTF();
		int type = buffer.readByte();

		if(type != -1)
		{
			long index = 0;
			try {
				index = Long.parseLong(objectIndex);
			} catch (Exception e) {
//				System.out.println("Bag playBossBox error objectIndex:"+objectIndex);
				return;
			}
			Goods goods = getBossGoods(index);
			if(goods == null)
			{
//				System.out.println("Bag playBossBox error objectIndex:"+index);
				return;
			}

			if(bossBoxCount > bossMoney.length)
			{
				return;
			}
			
			int needMoney = bossMoney[bossBoxCount-1];
			
			if(bossBoxCount >= BOSSBOXNUM)
			{
				if(money < needMoney)
				{
					target.sendAlert(ErrorCode.ALERT_SHOP_NO_MONEY);
					return;
				}
			}
			
			if(!isCanAddGoodsToBag(goods))
			{
				target.sendAlert(ErrorCode.ALERT_BAG_IS_FULL);
				return;
			}
			
			money -= needMoney;
			
			ByteBuffer buff = new ByteBuffer(1);
			buff.writeByte(type);
			target.getNetConnection().sendMessage(new SMsg(SMsg.S_BOSS_BOX_PLAY_COMMAND,buff));
			
			sendAddGoods(target, goods);
			
			bossGoods.remove(goods);
			
			bossBoxCount++;
		}
		else
		{
			bossBoxCount = 1;
			bossGoods = new ArrayList(10);
			
//			target.isBox = false;
			target.setIsBox(false, 0);
		}
	}
	
	private void getGift(PlayerController target)
	{
		if(isGift())
		{
			for (int i = 0; i < giftGoods.size(); i++) 
			{
				Goods goods = (Goods) giftGoods.get(i);
				if(goods == null)
					continue;
				sendAddGoods(target, goods);
				target.sendGetGoodsInfo(1,false, DC.getString(DC.GOODS_8)+":"+goods.name);
			}
			
			clearGift();
		}
		target.isGift = false;
	}
	
	private void sortBag(PlayerController target)
	{

		int count = 0;
		
		for (int j = 47; j >= 0; j--)
		{
			if(goodsList[j] != null)
			{
				for (int i = 0; i <= j; i++)
				{
					if(goodsList[i] == null)
					{
						goodsList[i] = goodsList[j];
						goodsList[j] = null;
						count--;
						break;
					}
				}
				count++;
			}
		}
		
		Goods goods = null;
		
		for(int i = 0; i < count ; i++)
		{
		   for(int j = count-1 ; j > i ; j --)
		   {
			    if(goodsList[j-1].type > goodsList[j].type)
			    {
			    	goods = goodsList[j-1];
			    	goodsList[j-1] = goodsList[j];
			    	goodsList[j] = goods;
			    }
		   }
		}
		
		
		for(int i = 0; i < count ; i++)
		{
		   for(int j = count-1 ; j > i ; j --)
		   {
			    if(goodsList[j-1].id > goodsList[j].id)
			    {
			    	if(goodsList[j-1].type == goodsList[j].type)
			    	{
				    	goods = goodsList[j-1];
				    	goodsList[j-1] = goodsList[j];
				    	goodsList[j] = goods;
			    	}
			    }
		   }
		}
		
		fixBag();

		writeToNotUseGoods(target);
	}
	
	
	public void fixBag()
	{
		for (int i = 0; i < goodsList.length; i++)
		{
			if(goodsList[i] != null)
			{
				if(goodsList[i].useFlag && goodsList[i] instanceof GoodsEquip)
				{
					GoodsEquip equip = (GoodsEquip) goodsList[i];
					if(i < 48)
					{
						goodsList[goodsList.length-1-equip.equipLocation] = equip;
						goodsList[i] = null;
					}
				}
			}
		}
		
	}


	private void buyActivePoint(PlayerController target, ByteBuffer buffer)
	{
		int flyPoint = buffer.readInt(); //飞行行动点数
		
		if(flyPoint <= 0)
			return;
		
		if(flyPoint * 10 > equipMoney && flyPoint * 10 > money)
		{
			target.sendAlert(ErrorCode.ALERT_SHOP_NO_MONEY);
			return;
		}
		
		if(target.getPlayer().flyActivePoint + flyPoint > Player.FLYACTIVEPOINTDEFAULT)
		{
			target.sendAlert(ErrorCode.ALERT_FLYACTIVEPOINT_OVER);
			return;
		}
		
		
		if(flyPoint * 10 <= equipMoney)
		{
			equipMoney -=(flyPoint*10);
			addMoney(target, 0, 0);
		}
		else if(flyPoint * 10 <= money) //你的元宝数量不足
		{
			addMoney(target, 0, -(flyPoint*10));
		}
	
		target.getPlayer().flyActivePoint += flyPoint;
		
		target.sendFlyActivePoint();
//		buffer = new ByteBuffer(4);
//		buffer.writeInt(target.getPlayer().flyActivePoint);
//		target.getNetConnection().sendMessage(new SMsg(SMsg.S_PLAYER_UPDATEACTIVEPOINT_COMMAND,buffer));
	}

	public boolean checkEnough(int enoughCount)
	{
		int count = 0;
		
		for (int i = 0; i < goodsList.length; i++)
		{
			if(goodsList[i] == null && i <= 47)
			{
				if(++count >= enoughCount)
				{	
					return true;
				}
			}
		}
		return false;
	}
	
	public int getNullCount()
	{
		int count = 0;
		
		for (int i = 0; i < goodsList.length; i++)
		{
			if(goodsList[i] == null && i <= 47)
			{
				count++;
			}
		}
		return count;
	}
	
	public boolean isRoleEquip()
	{
		for (int i = 0; i < goodsList.length; i++) 
		{
			if(goodsList[i] != null && goodsList[i].useFlag && goodsList[i] instanceof GoodsEquip)
			{
				GoodsEquip equip = (GoodsEquip) goodsList[i];
				if(equip.equipLocation == 10)
					return true;
			}
		}
		return false;
	}
	
	/**
	 * 宝石追加到装备(炼化)
	 * @param target
	 * @param buffer
	 */
	private void synGemEquip(PlayerController target,ByteBuffer buffer)
	{
		if(!(target.getParent() instanceof RoomController))
			return;
		if(WorldManager.isZeroMorning(1))
		{
			target.sendAlert(ErrorCode.ALERT_SYSTEM_IS_UPDATE);
			return;
		}
		String equipIndex = buffer.readUTF();
		String gemIndex = buffer.readUTF();
		if(equipIndex.equals(gemIndex))
		{
//			System.out.println("Bag synGemEquip index same equipIndex:"+equipIndex+"  gemIndex:"+gemIndex);
			return;
		}
		long gem = 0,equips = 0;
		try 
		{
			gem = Long.parseLong(gemIndex);
			equips = Long.parseLong(equipIndex);
		} catch (Exception e)
		{
//			System.out.println("Bag synGemEquip error index gem:"+gemIndex+"  equip:"+equipIndex);
			return;
		}
		Goods gemGoods = getGoodsByObjectIndex(gem);
		if(gemGoods == null)
		{
//			System.out.println("Bag synGemEquip gemGoods is null:"+gem);
			return;
		}
		if(gemGoods.type != 6)
		{
			MainFrame.println("Bag synGemEquip gemGoods:"+gemGoods.name+"  type:"+gemGoods.type);
			return;
		}
		Goods equipGoods = getGoodsByObjectIndex(equips);
		if(equipGoods == null)
		{
//			System.out.println("Bag synGemEquip equipGoods is null:"+equips);
			return;
		}
		if(equipGoods.type != 1)
		{
			MainFrame.println("Bag synGemEquip equipGoods:"+equipGoods.name+"  type:"+equipGoods.type);
			return;
		}
		if(equipGoods.useFlag)
			return;
		GoodsProp prop = (GoodsProp) gemGoods;
		GoodsEquip equip = (GoodsEquip) equipGoods;
		if(!equip.isGemEquip())
			return;
		int needMoney = 0;
		if(equip.isVIP)
		{
			LogTemp log = new LogTemp();
			log.id = target.getID();
			log.name = target.getName();
			log.setLevel(target.getPlayer().level);
			
			double nm = equip.token * VIPEQUIP;
			if(nm < 1)
				needMoney = 1;
			else
				needMoney = (int)nm;
			
			Goods uGoods = getGoodsByType(Goods.EQUIPUPOTHER);
			if(uGoods == null)
			{
				if(money+equipMoney < needMoney)
				{
					target.sendAlert(ErrorCode.ALERT_SHOP_NO_MONEY);
					return;
				}
				if(equipMoney >= needMoney)
				{
					equipMoney -= needMoney;
					log.setEquipMoney(needMoney);
				}
				else
				{
					needMoney -= equipMoney;
					equipMoney = 0;
					money -= needMoney;
					log.setMoney(needMoney);
				}
			}
			else
			{
				int uGoodsCount = getGoodsCount(uGoods.id);
				int otherCount = needMoney - uGoodsCount;
				if(otherCount <= 0)
				{
					log.putGoodsMap(uGoods.id, uGoods.name, needMoney);
					
					deleteGoods(target, uGoods.id, needMoney);
				}
				else
				{
					if(money+equipMoney < otherCount)
					{
						target.sendAlert(ErrorCode.ALERT_SHOP_NO_MONEY);
						return;
					}
					
					log.putGoodsMap(uGoods.id, uGoods.name, uGoodsCount);
					
					deleteGoods(target, uGoods.id, uGoodsCount);
					if(equipMoney >= otherCount)
					{
						equipMoney -= otherCount;
						log.setEquipMoney(otherCount);
					}
					else
					{
						otherCount -= equipMoney;
						equipMoney = 0;
						money -= otherCount;
						log.setMoney(otherCount);
					}
				}
			}
			
			log.setAccountName(target.getPlayer().accountName);
			DataFactory.getInstance().putLogMap(0,log);
			
			if(target.getPlayer().isVipPlayer)
			{
				StringBuffer data = new StringBuffer();
				data.append(DC.getString(DC.GOODS_10));
				data.append(equip.name);
				data.append(",");
				data.append(DC.getString(DC.GOODS_4));
				data.append("[");
				data.append(prop.name);
				data.append("x");
				data.append(1);
				data.append(log.getGoodsStr());
				if(log.getMoney() > 0)
				{
					data.append("money:");
					data.append(log.getMoney());
				}
				if(log.getEquipMoney() > 0)
				{
					data.append("daijin:");
					data.append(log.getEquipMoney());
				}
				data.append("]");
				DataFactory.getInstance().addVipPlayerInfo(target, data.toString());
			}
		}
		else
		{
			double nm = equip.point * NOVIPEQUIP;
			if(nm < 1)
				needMoney = 1;
			else
				needMoney = (int)nm;
			if(point < needMoney)
			{
				target.sendAlert(ErrorCode.ALERT_SHOP_NO_POINT);
				return;
			}
			point -= needMoney;
		}

		equip.clearExtAtt();
		equip.setDefaultAtt(prop.gemAtt);
		equip.setGemExt(prop.gemAtt);
		
		if(equip.bindMode != 4 && prop.bindMode == 4)
			equip.bindMode = 4;
		
		removeGoods(target, prop.objectIndex, 1);
		sendAddGoods(target, null);
		sendGoodsInfo(target, equip); 
	
		ByteBuffer buff = new ByteBuffer();
		buff.writeUTF(equip.extAtt.toString());
		buff.writeUTF(equip.objectIndex+"");
		equip.writeTo(buff);
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_SYN_GEM_EQUIP_COMMAND,buff));
		
		
	}
	

	/**
	 * 物品进化
	 * @param target
	 * @param inBuffer v
	 */
	private void goodsUp(PlayerController target,ByteBuffer inBuffer)
	{
		int type = inBuffer.readByte();
		String objectIndex = inBuffer.readUTF();
		long index = 0;
		try {
			index = Long.parseLong(objectIndex);
		} catch (Exception e) {
			System.out.println("Bag goodsUp accept index error:"+objectIndex);
			return;
		}
		if(target.getParent() instanceof BattleController)
			return;
		if(target.getParent() instanceof BusinessController)
		{
			target.sendGetGoodsInfo(1, false, DC.getString(DC.GOODS_11));//请先关闭交易
			return;
		}
		Goods goods = getGoodsByObjectIndex(index);
		if(goods == null)	
		{
			target.sendAlert(ErrorCode.ALERT_GOODS_NOT_EXIST);
			return;
		}
		if(goods.useFlag)
		{
			System.out.println("Bag goodsUp goods is use error:"+goods.name+" playerName:"+target.getName());
			return;
		}
		GoodsUp gu = SShop.getShop().getGoodsUpBySourceId(goods.id);
		if(gu == null)
		{
			System.out.println("Bag goodsUp goods is not up error:"+goods.id);
			return;
		}
		Goods targetGoods = gu.getTargetGoods(goods);//这里取出是没有no objectIndex
		if(targetGoods == null)
		{
			target.sendAlert(ErrorCode.ALERT_TARGET_GOODS_ISNULL);
			return;
		}
		ByteBuffer buffer = new ByteBuffer();
		if(type == 1)//query
		{
			buffer.writeByte(1);
			targetGoods.writeTo(buffer);
		}
		else if(type == 2)//up
		{
			if(!gu.isConEnough(target))
			{
				System.out.println("Bag goodsUp goods is condition error:"+gu.sourceId+" playerName:"+target.getName());
				return;
			}
			Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
			if(!bag.isCanAddGoodsToBag(targetGoods))
			{	
				target.sendAlert(ErrorCode.ALERT_BAG_IS_FULL);
				return;
			}
			targetGoods.objectIndex = GameServer.getInstance().getDatabaseAccessor().getGoodsObjIndex();
			boolean isSelf = false;
			for (int i = 0; i < gu.needGoodsId.length; i++)
			{
				if(gu.needGoodsId[i] == goods.id && !isSelf)
					isSelf = true;
				bag.deleteGoods(target, gu.needGoodsId[i], gu.needGoodsCount[i]);
			}
			if(!isSelf)
				bag.removeGoods(target, index, 1);
			bag.point -= gu.needPoint;
			bag.money -= gu.needMoney;
			target.setHonour(-gu.needHonor);
			
			bag.sendAddGoods(target, targetGoods);
			
			buffer.writeByte(2);
			buffer.writeBoolean(true);
			
			if(target.getPlayer().isVipPlayer)
			{
				StringBuffer data = new StringBuffer();
				data.append(DC.getString(DC.GOODS_12));
				data.append(goods.name);
				data.append(",");
				data.append(DC.getString(DC.GOODS_4));
				data.append("[");
				data.append(goods.name);
				data.append("x");
				data.append(1);
				for (int i = 0; i < gu.needGoodsId.length; i++)
				{
					Goods g = (Goods) DataFactory.getInstance().getGameObject(gu.needGoodsId[i]);
					if(g == null)
						continue;
					data.append(g.name);
					data.append("x");
					data.append(gu.needGoodsCount[i]);
				}
				if(gu.needMoney > 0)
				{
					data.append("money:");
					data.append(gu.needMoney);
				}	
				if(gu.needPoint > 0)
				{
					data.append("point:");
					data.append(gu.needPoint);
				}	
				if(gu.needHonor > 0)
				{
					data.append("honor:");
					data.append(gu.needHonor);
				}	
				data.append("],");
				data.append(DC.getString(DC.GOODS_8));
				data.append(targetGoods.name);
				DataFactory.getInstance().addVipPlayerInfo(target, data.toString());
			}
		}
		else
		{
			System.out.println("Bag goodsUp error type:"+type);
			return;
		}
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_GOODS_UP_COMMAND,buffer));
	}

	/**
	 * 装备从另外一个装备上COPY属性过来,每COPY一次要把上次COPY的属性清掉
	 * @param target  消耗一个兑换券
	 * @param buffer 一个兑换券,一个形象,兑换成功给玩家返一个结婚形象
	 */
	private void equipCopyUp(PlayerController target,ByteBuffer inBuffer)
	{
		if(!(target.getParent() instanceof RoomController))
			return;
		String changeIndex = "";
		String modelIndex = "";
		try 
		{
			changeIndex = inBuffer.readUTF();
			modelIndex = inBuffer.readUTF();
		} catch (Exception e)
		{
			System.out.println("Bag equipCopyUp error inBufferLength:"+inBuffer.available());
			return;
		}
		if(changeIndex.equals(modelIndex))
			return;
		long changer = 0,modeler = 0;
		try 
		{
			changer = Long.parseLong(changeIndex);
			modeler = Long.parseLong(modelIndex);
		} catch (Exception e)
		{
			return;
		}
		Goods prop = getGoodsByObjectIndex(changer);
		if(prop == null)
			return;
		if(!(prop instanceof GoodsProp))
			return;
		if(prop.type != 29)
			return;
		GoodsProp changeGoods = (GoodsProp) prop;
		Goods equip = getGoodsByObjectIndex(modeler);
		if(equip == null)
			return;
		if(!(equip instanceof GoodsEquip))
			return;
		GoodsEquip modelGoods = (GoodsEquip) equip;
		if(modelGoods.equipLocation != 10)
			return;
		if(modelGoods.eType == 1)
		{
			target.sendAlert(ErrorCode.ALERT_NOT_MODEL_CHANGE);
			return;
		}
		if(changeGoods.useFlag || modelGoods.useFlag)
			return;
		GoodsEquip returnGoods = null;
		int goodsId = 0;
		long effectTime = 0;
		String[] strs = Utils.split(changeGoods.gemAtt, ":");
		try{
			goodsId = Integer.parseInt(strs[0]);
			effectTime = Long.parseLong(strs[1]) * 24 * 60 * 60 * 1000;
		}catch(Exception e)
		{
			System.out.println("Bag equipCopyUp changeGoods error gemAtt:"+changeGoods.gemAtt);
			return;
		}
		if(goodsId == 0 || effectTime == 0)
		{
			System.out.println("Bag equipCopyUp changeGoods id or time error:"+changeGoods.gemAtt);
			return;
		}
		
		Goods[] goodsList = DataFactory.getInstance().makeGoods(goodsId);
		if(goodsList != null)
		{
			if(!(goodsList[0] instanceof GoodsEquip))
			{
				System.out.println("Bag equipCopyUp error id:"+goodsId);
				return;
			}
			returnGoods = (GoodsEquip) goodsList[0];
		}
		
		if(returnGoods != null)
		{
			if(returnGoods.eType == 1)
			{
				OtherExtInfo oei = (OtherExtInfo) target.getPlayer().getExtPlayerInfo("otherExtInfo");
				if("".equals(oei.loverName))
				{
					target.sendAlert(ErrorCode.ALERT_PLAYER_NOT_MARRY);
					return;
				}
			}
			if(!(isCanAddGoodsToBag(returnGoods)))
			{
				target.sendAlert(ErrorCode.ALERT_BAG_IS_FULL);
				return;
			}
			returnGoods.objectIndex = GameServer.getInstance().getDatabaseAccessor().getGoodsObjIndex();
			returnGoods.setCreateTime();
			returnGoods.setAttStr("createTime");
			returnGoods.effectTime = effectTime;
			returnGoods.setAttStr("effectTime");
		
			removeGoods(target, changeGoods.objectIndex, 1);
			modelGoods.copyAllTo(returnGoods);	
			sendAddGoods(target, returnGoods);
			
			ByteBuffer buffer = new ByteBuffer();
			buffer.writeBoolean(true);
			target.getNetConnection().sendMessage(new SMsg(SMsg.S_EQUIP_COPY_UP_COMMAND,buffer));
		}
	}
	
	/**
	 * 生命精力宝石同值进化
	 * @param target
	 * @param inBuffer v
	 */
	private void gemLifeAnsMagicUp(PlayerController target,ByteBuffer inBuffer)
	{
		int type = inBuffer.readByte();
		String objectIndex = inBuffer.readUTF();
		long index = 0;
		try {
			index = Long.parseLong(objectIndex);
		} catch (Exception e) {
			System.out.println("Bag goodsUp accept index error:"+objectIndex);
			return;
		}
		if(target.getParent() instanceof BattleController)
			return;
		if(target.getParent() instanceof BusinessController)
		{
			target.sendGetGoodsInfo(1, false, DC.getString(DC.GOODS_11));//请先关闭交易
			return;
		}
		Goods goods = getGoodsByObjectIndex(index);
		if(goods == null)	
		{
			target.sendAlert(ErrorCode.ALERT_GOODS_NOT_EXIST);
			return;
		}
		if(goods.useFlag)
		{
			System.out.println("Bag goodsUp goods is use error:"+goods.name+" playerName:"+target.getName());
			return;
		}
		GoodsUp gu = SShop.getShop().getGoodsEoBySourceId(goods.id);
		if(gu == null)
		{
			System.out.println("Bag gemLifeAnsMagicUp error goodsUp is null:"+goods.id);
			return;
		}
		
		Goods targetGoods = gu.getTargetGoods(goods);//这里取出是没有no objectIndex
		if(targetGoods == null)
		{
			target.sendAlert(ErrorCode.ALERT_TARGET_GOODS_ISNULL);
			return;
		}
		ByteBuffer buffer = new ByteBuffer();
		if(type == 1)//query
		{
			buffer.writeByte(1);
			targetGoods.writeTo(buffer);
		}
		else if(type == 2)//up
		{
			if(!gu.isConEnough(target))
			{
				System.out.println("Bag goodsUp goods is condition error:"+gu.sourceId+" playerName:"+target.getName());
				return;
			}
			Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
			if(!bag.isCanAddGoodsToBag(targetGoods))
			{	
				target.sendAlert(ErrorCode.ALERT_BAG_IS_FULL);
				return;
			}
			targetGoods.objectIndex = GameServer.getInstance().getDatabaseAccessor().getGoodsObjIndex();
			boolean isSelf = false;
			for (int i = 0; i < gu.needGoodsId.length; i++)
			{
				if(gu.needGoodsId[i] == goods.id && !isSelf)
					isSelf = true;
				bag.deleteGoods(target, gu.needGoodsId[i], gu.needGoodsCount[i]);
			}
			if(!isSelf)
				bag.removeGoods(target, index, 1);
			bag.point -= gu.needPoint;
			bag.money -= gu.needMoney;
			target.setHonour(-gu.needHonor);
			
			bag.sendAddGoods(target, targetGoods);
			
			buffer.writeByte(2);
			buffer.writeBoolean(true);
		}
		else
		{
			System.out.println("Bag goodsUp error type:"+type);
			return;
		}
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_LIFE_MAGIC_UP_COMMAND,buffer));
	}
	
	
	
	/**
	 * (背包)客户端消息通道
	 * @param target
	 * @param msg
	 */
	public void clientMessageChain(PlayerController target,AppMessage msg)
	{
		int type = msg.getType();
		ByteBuffer buffer = msg.getBuffer();
		
		if(type == SMsg.C_DELETE_GOODS_FROM_BAG_COMMAND)
		{
			deleteGoodsFromBag(target,buffer);
		}
		else if(type == SMsg.C_TAKE_OFF_EQUIP_COMMAND)
		{
			takeOffEquip(target,buffer);
		}
		else if(type == SMsg.C_USE_GOODS_COMMAND)
		{
			useGoods(target,buffer);
		}
		else if(type == SMsg.C_CHANGE_BAG_GOODS_LOCATION_COMMAND)
		{
			changeGoodsLocation(target,buffer);
		}
		else if(type == SMsg.C_GET_PLAYER_BAG_COMMAND)
		{
			writeToNotUseGoods(target);
		}
		else if(type == SMsg.C_GET_PLAYER_SHORTCUT_BAR_COMMAND)
		{
			getPlayerShotcutBar(target);
		}
		else if(type == SMsg.C_SET_PLAYER_SHORTCUT_BAR_COMMAND)
		{
			setPlayerShortcutBar(target,buffer);
		}
		else if(type == SMsg.C_CANCEL_GOODS_SHORTCUT_COMMAND)
		{
			cancelGoodsShortcut(target,buffer);
		}
		else if(type == SMsg.C_GET_GOODS_INFO_COMMAND)
		{
			getGoodsInfo(target,buffer);
		}
		else if(type == SMsg.C_SPLIT_GOODS_COMMAND)
		{
			splitGoods(target,buffer);
		}
		else if(type == SMsg.C_SUPERPOSE_GOODS_COMMAND)
		{
			superposeGoods(target,buffer);
		}
		else if(type == SMsg.C_BAG_SYNTHETIZE_COMMAND)
		{
			synthetizeProcess(target,buffer);
		}
		else if(type == SMsg.C_BAG_SSHOP_COMMAND)
		{
			sShopProcess(target,buffer);
		}
		else if(type == SMsg.C_BAG_DSHOP_COMMAND)
		{
			dShopProcess(target,buffer);
		}
		else if(type == SMsg.C_BAG_GOODS_BIND)
		{
			goodsBind(target,buffer);
		}
		else if(type == SMsg.C_ADD_GOODS_COMMAND)
		{
			boxSuccess(target,buffer);
		}
		else if(type == SMsg.C_GET_TASK_GOODS_COMMAND)
		{
			getTaskGoods(target, buffer);
		}
		else if(type == SMsg.C_BAG_BUYACTIVE_COMMAND)
		{
			buyActivePoint(target, buffer);
		}
		else if(type == SMsg.C_BOSS_BOX_PLAY_COMMAND)
		{
			playBossBox(target,buffer);
		}
		else if(type == SMsg.C_NEW_PLAYER_GIFTGOODS_COMMAND)
		{
			getGift(target);
		}
		else if(type == SMsg.C_SORT_GOODS_COMMAND)
		{
			sortBag(target);
		}
		else if(type == SMsg.C_SYN_GEM_EQUIP_COMMAND)
		{
			synGemEquip(target,buffer);
		}
		else if(type == SMsg.C_GOODS_UP_COMMAND)
		{
			goodsUp(target,buffer); 
		}
		else if(type == SMsg.C_EQUIP_COPY_UP_COMMAND)
		{
			equipCopyUp(target,buffer);
		}
		else if(type == SMsg.C_LIFE_MAGIC_UP_COMMAND)
		{
			gemLifeAnsMagicUp(target,buffer);
		}
		
	}


	
}
