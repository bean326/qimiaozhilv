package cc.lv1.rpg.gs.entity.ext;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.data.DC;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.RPGameObject;
import cc.lv1.rpg.gs.entity.controller.NpcController;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.controller.RoomController;
import cc.lv1.rpg.gs.entity.impl.Goods;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsEquip;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsPetEquip;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsProp;
import cc.lv1.rpg.gs.net.SMsg;
import cc.lv1.rpg.gs.other.ErrorCode;
import cc.lv1.rpg.gs.util.FontConver;
import cc.lv1.rpg.gs.util.SaverChanger;
import vin.rabbit.net.AppMessage;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.impl.ArrayList;

/**
 * 玩家仓库
 * @author balan
 *
 */
public class Storage extends PlayerExtInfo 
{
	/** 括充一页仓库需要的元宝数量 */
	public static final int PAGE_MONEY = 10; 
	
	/** 仓库的最大数量 */
	public static final int MAX_SIZE = 4 * 54; 
	
	/** 仓库锁时间 */
	public static int LOCK_TIME = 24*60*60*1000;
	
	/** 仓库中的游戏币 */
	public int point;
	
	/** 仓库类型(0.玩家个人仓库 1.家族仓库 2. 工会仓库 ......) */ 
	public int storageType;
	
	/** 黄金都是活动领取签名记录*/
	public String goldSignName = "";
	
	/** 仓库状态(等于3时仓库就被锁住了) */
	public int state;
	
	/** 仓库被锁时间 */
	public long lockTime;

	/** 仓库大小(默认) */
	public int storageSize = 54;
	
	/** 仓库中物品 */
	private Goods[] goodsList = new Goods[storageSize];
	
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
	
	public Goods[] getGoodsList()
	{
		return goodsList;
	}
	
	public Storage(int storageType)
	{
		this.storageType = storageType;
	}
	
	/**
	 * 设置仓库被锁时间
	 */
	public void setLockTime(){
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		lockTime = calendar.getTimeInMillis();
	}
	
	@Override
	public String getName() 
	{
		return "storage";
	}

	@Override
	public void loadFrom(ByteBuffer buffer) 
	{
		point = buffer.readInt();
		storageType = buffer.readInt();
		goldSignName = buffer.readUTF();
		state = buffer.readInt();
		lockTime = buffer.readLong();
		storageSize = buffer.readInt();
		goodsList = new Goods[storageSize];
		loadStorageGoods(buffer);
	}

	@Override
	public void saveTo(ByteBuffer buffer) 
	{
		buffer.writeInt(point);
		buffer.writeInt(storageType);
		buffer.writeUTF(goldSignName);
		buffer.writeInt(state);
		buffer.writeLong(lockTime);
		buffer.writeInt(storageSize);
		saveStorageGoods(buffer);
	}
	
	public void writeTo(ByteBuffer buffer)
	{
		buffer.writeInt(storageSize);
		buffer.writeInt(point);
		int count = 0;
		for (int i = 0; i < goodsList.length; i++)
		{
			if(goodsList[i] != null)
				count++;
		}
		buffer.writeInt(count);
		for (int i = 0; i < goodsList.length; i++)
		{
			Goods goods = goodsList[i];
			if(goods != null)
			{
				goods.writeTo(buffer);
				buffer.writeInt(goods.goodsCount);
				buffer.writeInt(i);
			}
		}
	}


	/**
	 * 保存仓库物品
	 * @param buffer
	 */
	public void saveStorageGoods(ByteBuffer buffer)
	{
		int count = 0;
		for (int i = 0; i < goodsList.length; i++)
		{
			if(goodsList[i] != null)
				count++;
		}
		buffer.writeInt(count);
		for (int i = 0; i < goodsList.length; i++)
		{
			Goods goods = goodsList[i];
			if(goods != null)
			{
				buffer.writeInt(goods.id);
				buffer.writeUTF(goods.objectIndex+"");
				buffer.writeInt(goods.goodsCount);
				buffer.writeInt(i);
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
				else
				{
					GoodsProp prop = (GoodsProp) goods;
					buffer.writeLong(prop.expPoint);
				}
			}
		}
	}
	
	/**
	 * 加载仓库中物品
	 * @param buffer
	 */
	public void loadStorageGoods(ByteBuffer buffer)
	{
		/**
		 * 仓库中的所有都是流水
		 */
		int count = buffer.readInt();
		for (int i = 0; i < count; i++) 
		{
			int id = buffer.readInt();
			long objectIndex = Long.parseLong(buffer.readUTF());
			int goodsCount = buffer.readInt();
			int location = buffer.readInt();
			int quality = buffer.readInt();
			int bindMode = buffer.readByte();
			
			Goods goods = (Goods)DataFactory.getInstance().getGameObject(id);
			if(goods == null)
			{
				System.out.println("load storage goodsId error:"+id);
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
			goodsList[location] = newGoods;
		}
	}
	
	
	/**
	 * 仓库密码操作
	 * @param target
	 * @param buffer
	 */
	public void storagePassword(PlayerController target,ByteBuffer buffer)
	{
//		String oldPwd = buffer.readUTF();
//		String newPwd = buffer.readUTF();
//		boolean result = true;
//		if(!oldPwd.equals(password))
//		{
//			result = false;
//			target.sendAlert(ErrorCode.ALERT_STORAGE_OLD_PASSWORD_ERROR);
//			return;
//		}
//		password = newPwd;
//		ByteBuffer buff = new ByteBuffer(1);
//		buff.writeBoolean(result);
//		target.getNetConnection().sendMessage(new SMsg(SMsg.S_STORAGE_PASSWORD_OPTION_COMMAND,buff));
	}
	
	
	/**
	 * 添加物品到仓库中(从背包中)
	 * @param target
	 * @param buffer
	 */
	public void addGoodsToStorage(PlayerController target,ByteBuffer buffer)
	{
		String index = buffer.readUTF();
		if(index.isEmpty())
			return;
		long objectIndex = Long.parseLong(index);
		Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
		PlayerSetting ps = (PlayerSetting) target.getPlayer().getExtPlayerInfo("playerSetting");
		Object goodsObject = bag.getGoodsByObjectIndex(objectIndex);
		if(goodsObject == null || !(goodsObject instanceof Goods))
		{
			//物品不存在
			target.sendAlert(ErrorCode.ALERT_GOODS_NOT_EXIST);
			return;
		}
		if(isStorageFull(target))
			return;

		Goods goods = (Goods)goodsObject;
		int count = goods.getGoodsCount();
		
		bag.getGoodsList()[bag.getGoodsLocation(goods.objectIndex)] = null;
		ps.updatePlayerBar(goods.id,bag.getGoodsCount(goods.id));
		ByteBuffer buff = new ByteBuffer(64);
		addGoodsToStorage(target,goods,buff);
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_ADD_GOODS_TO_STORAGE_COMMAND,buff));
		
		TaskInfo info = (TaskInfo)target.getPlayer().getExtPlayerInfo("taskInfo");
		info.onPlayerLostItem(goods.id,count, target);
		
	}
	
	
	/**
	 * 从仓库中取出物品
	 * @param target
	 * @param buffer
	 */
	public void takeOutGoodsFromStorage(PlayerController target,ByteBuffer buffer)
	{
		String index = buffer.readUTF();
		if(index.isEmpty())
			return;
		long objectIndex = Long.parseLong(index);
		Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
		if(bag.isBagFull(target))
			return;
		Object goodsObject = getGoodsByObjectIndex(objectIndex);
		if(goodsObject == null || !(goodsObject instanceof Goods))
		{
			//物品不存在
			target.sendAlert(ErrorCode.ALERT_GOODS_NOT_EXIST);
			return;
		}
		Goods goods = (Goods)goodsObject;
		goodsList[getGoodsLocation(objectIndex)] = null;
		ByteBuffer buff = new ByteBuffer(128);
		buff.writeUTF(goods.objectIndex+"");
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_TAKEOUT_GOODS_FROM_STORAGE_COMMAND,buff));
		
		bag.sendAddGoods(target, goods);
	}
	
	/**
	 * 改变物品在仓库中位置
	 * @param target
	 * @param buffer
	 */
	public void changeStorageGoodsLocation(PlayerController target,ByteBuffer buffer)
	{
		String index = buffer.readUTF();
		if(index.isEmpty())
			return;
		long objectIndex = Long.parseLong(index);
		int newLocation = buffer.readInt();
		if(newLocation < 0)
			return;
		Object goodsObject = getGoodsByObjectIndex(objectIndex);
		if(!(goodsObject instanceof Goods) || goodsObject == null)
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
		ByteBuffer buff = new ByteBuffer(20);
		if(goodsList[newLocation] != null)
		{
			Goods reGoods = goodsList[newLocation];
			if(reGoods.id != goods.id)
			{
				target.sendAlert(ErrorCode.ALERT_GOODS_NOT_SUPERPOSE);
				return;
			}
			else
			{
				if(goods.repeatNumber <= 1)
				{
					target.sendAlert(ErrorCode.ALERT_GOODS_NOT_SUPERPOSE);
					return;
				}
				int count = goods.goodsCount + reGoods.goodsCount;
				if(count <= reGoods.repeatNumber)
				{
					reGoods.goodsCount = count;
					goods.goodsCount = 0;
				}
				else 
				{
					reGoods.goodsCount = reGoods.repeatNumber;
					goods.goodsCount = count - goods.repeatNumber;	
				}
				buff.writeInt(1);
				buff.writeUTF(goods.objectIndex+"");
				buff.writeInt(getGoodsLocation(goods.objectIndex));
				buff.writeInt(goods.goodsCount);
				buff.writeUTF(reGoods.objectIndex+"");
				buff.writeInt(getGoodsLocation(reGoods.objectIndex));
				buff.writeInt(reGoods.goodsCount);
				updateGoodsList(goods);
				updateGoodsList(reGoods);
			}
		}
		else
		{
			goodsList[getGoodsLocation(objectIndex)] = null;
			goodsList[newLocation] = goods;
			buff.writeInt(0);
			buff.writeUTF(objectIndex+"");
			buff.writeInt(newLocation);
		}
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_CHANGE_STORAGE_GOODS_LOCATION_COMMAND,buff));
	}
	

	/**
	 * 打开玩家个人仓库
	 * @param target
	 * @param buffer
	 */
	public void openPrivateStorage(PlayerController target)
	{
		ByteBuffer buffer = new ByteBuffer();
		writeTo(buffer);
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_OPEN_STORAGE_COMMAND,buffer));
		
		if(!target.isStorage)
			target.isStorage = true;
	}

	
	/**
	 * 仓库中金钱(游戏币)(取出，存入)操作
	 * @param target
	 * @param buffer
	 */
	public void moneyOption(PlayerController target,ByteBuffer buffer)
	{
		int type = buffer.readInt();
		int pointCount = 0;
		String inPoint = buffer.readUTF();
		
		try
		{
			pointCount = Integer.parseInt(inPoint);
		}
		catch (Exception e) 
		{
			return;
		}
		if(pointCount < 0)
			return;
		Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
		if(type == 0)//存钱
		{
			if(pointCount > Integer.MAX_VALUE)
			{
				return;
			}
			long a = (long)point + (long)pointCount;
			if(a > Integer.MAX_VALUE)
			{
				target.sendError(DC.getString(DC.GOODS_1));
				return;
			}
			if(pointCount > bag.point)
			{
				target.sendAlert(ErrorCode.ALERT_STORAGE_SAVE_POINT_OVERRUN);
				return;
			}
			point += pointCount;
			bag.point -= pointCount;
		}
		else if(type == 1)
		{
			if(pointCount > point)
			{
				target.sendAlert(ErrorCode.ALERT_STORAGE_TAKE_POINT_OVERRUN);
				return;
			}
			point -= pointCount;
			bag.point += pointCount;
		}
		ByteBuffer buff = new ByteBuffer();
		buff.writeInt(pointCount);
		buff.writeInt(type);
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_MONEY_OPTION_COMMAND,buff));
	}
	
	
	
	/**
	 * 添加物品到玩家仓库中
	 * @param target
	 * @param goods
	 */
	public void addGoodsToStorage(PlayerController target,Goods goods,ByteBuffer buffer)
	{
		if(!target.isOnline())
			return;
		buffer.writeUTF(goods.objectIndex+"");
		if(goods.repeatNumber <= 1)
		{
			if(isStorageFull(target))
				return;
			int location = getNullLocation();
			if(location == -1)
				return;
			goodsList[location] = goods;
			buffer.writeInt(2);
			goods.writeTo(buffer);
			buffer.writeInt(goods.goodsCount);
			buffer.writeInt(location);
		}
		else
		{
			Goods go = getGoods(goods);
			if(go != null)
			{
				int total = goods.goodsCount + go.goodsCount;
				if(total <= goods.repeatNumber)
				{			
					go.goodsCount = total;//更新物品数量 
					buffer.writeInt(0);
					buffer.writeUTF(go.objectIndex+"");
					buffer.writeInt(go.goodsCount);
					buffer.writeInt(getGoodsLocation(go.objectIndex));
				}
				else
				{
					go.goodsCount = go.repeatNumber;
					buffer.writeInt(1);
					buffer.writeUTF(go.objectIndex+"");
					buffer.writeInt(go.goodsCount);
					buffer.writeInt(getGoodsLocation(go.objectIndex));
					int count = total - go.repeatNumber;
					if(count > 0)
					{
						Goods newGoods = (Goods) Goods.cloneObject(go);
						newGoods.objectIndex = GameServer.getInstance().getDatabaseAccessor().getGoodsObjIndex();
						newGoods.goodsCount = total - newGoods.repeatNumber;
						if(isStorageFull(target))
							return;
						int nullLocation = getNullLocation();
						if(nullLocation == -1)
							return;
						goodsList[nullLocation] = newGoods;
						buffer.writeUTF(newGoods.objectIndex+"");
						buffer.writeInt(newGoods.goodsCount);
						buffer.writeInt(getGoodsLocation(newGoods.objectIndex));
					}
				}	
				updateGoodsList(go);
				Goods goo = (Goods) Goods.cloneObject(go);
				goo.goodsCount = getGoodsCount(goo.id);
			}
			else
			{
				if(isStorageFull(target))
					return;
				int location = getNullLocation();
				if(location == -1)
					return;
				goodsList[location] = goods;
				buffer.writeInt(2);
				goods.writeTo(buffer);
				buffer.writeInt(goods.goodsCount);
				buffer.writeInt(location);
			}
		}
		
	}

		
		
		
	
	/**
	 * 判断仓库是否满了
	 * @param target
	 * @return
	 */
	public boolean isStorageFull(PlayerController target)
	{
		if(getNullLocation() == -1)
		{
			target.sendAlert(ErrorCode.ALERT_STORAGE_IS_FULL);
			return true;
		}
		return false;
	}
	
	/**
	 * 获取一个空位置
	 * @return
	 */
	private int getNullLocation()
	{
		for (int i = 0; i < goodsList.length; i++)
		{
			if(goodsList[i] == null)
				return i;
		}
		return -1;
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
	 * 获取仓库中物品数量没有到达最大迭加数量的物品
	 * @param id
	 * @return
	 */
	public Goods getGoods(Goods goods)
	{
		for (int i = 0; i < goodsList.length; i++) 
		{
			if(goodsList[i] != null)
			{
				if(goodsList[i].id == goods.id && goodsList[i].goodsCount < goodsList[i].repeatNumber
						&& goodsList[i].bindMode == goods.bindMode)
				{
					return goodsList[i];
				}
			}
		}
		return null;
	}
	
	
	public int getNullCount()
	{
		int count = 0;
		
		for (int i = 0; i < goodsList.length; i++)
		{
			if(goodsList[i] == null)
			{
				count++;
			}
		}
		return count;
	}
	
	/**
	 * 更新仓库
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
					if(goods.goodsCount <= 0)
						goodsList[i] = null;
					else
						goodsList[i] = goods;
					break;
				}
			}
		}
	}
	
	
	/**
	 * 获取同类物品的总数量
	 * @param goods
	 * @return
	 */
	public int getGoodsCount(int id)
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
	 * 仓库容量升级
	 * @param target
	 * @param buffer
	 */
	public void upStorageSize(PlayerController target,ByteBuffer buffer)
	{
		int upPage = buffer.readByte();
		Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
		int needMoney = upPage * PAGE_MONEY;
		if(bag.money < upPage * needMoney)
		{
			target.sendAlert(ErrorCode.ALERT_SHOP_NO_MONEY);
			return;
		}
		bag.money -= needMoney;
		
		addStorageSize(upPage*60);

		bag.sendAddGoods(target, null);
	}
	
	public void sendStorageSize(PlayerController target)
	{
		ByteBuffer buffer = new ByteBuffer(4);
		buffer.writeInt(storageSize);
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_UP_STORAGE_SIZE_COMMAND,buffer));
	}
	
	/**
	 * 增加仓库容量 按照格子数量增加
	 * 
	 */
	public void addStorageSize(int size)
	{
		int newSize = storageSize + size;
		Goods[] goods = new Goods[newSize];
		for (int i = 0; i < storageSize; i++)
		{
			goods[i] = goodsList[i];
		}
		goodsList = goods;
		storageSize = newSize;
	}
	
	
	public void sortStorage(PlayerController target)
	{

		int count = 0;
		
		for (int j = storageSize-1; j >= 0; j--)
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

		openPrivateStorage(target);
	}
	
	
	public void removeGoodsByIndex(long index)
	{
		for (int i = 0; i < goodsList.length; i++)
		{
			if(goodsList[i] == null)
				continue;
			if(goodsList[i].objectIndex == index)
			{	
				goodsList[i] = null;
				break;
			}
		}
	}
	
	
	/**
	 * (仓库)客户端消息通道
	 * @param target
	 * @param msg
	 */
	public void clientMessageChain(PlayerController target, AppMessage msg)
	{
		int type = msg.getType();
		ByteBuffer buffer = msg.getBuffer();
		if(type == SMsg.C_STORAGE_PASSWORD_OPTION_COMMAND)
		{
//			storagePassword(target,buffer);
		}
		else if(type == SMsg.C_ADD_GOODS_TO_STORAGE_COMMAND)
		{
			addGoodsToStorage(target,buffer);
		}
		else if(type == SMsg.C_TAKEOUT_GOODS_FROM_STORAGE_COMMAND)
		{
			takeOutGoodsFromStorage(target,buffer);
		}
		else if(type == SMsg.C_CHANGE_STORAGE_GOODS_LOCATION_COMMAND)
		{
			changeStorageGoodsLocation(target,buffer);
		}
		else if(type == SMsg.C_OPEN_STORAGE_COMMAND)
		{
			openPrivateStorage(target);
		}
		else if(type == SMsg.C_MONEY_OPTION_COMMAND)
		{
			moneyOption(target,buffer);
		}
		else if(type == SMsg.C_UP_STORAGE_SIZE_COMMAND)
		{
			upStorageSize(target,buffer);
		}
		else if(type == SMsg.C_SORT_STORAGE_COMMAND)
		{
			sortStorage(target);
		}
		else if(type == SMsg.C_CLOSE_STORAGE_COMMAND)
		{
			if(target.isStorage)
				target.isStorage = false;
		}
	}

	public int getStorageSize() {
		return storageSize;
	}

	public void setStorageSize(int storageSize) {
		this.storageSize = storageSize;
	}
	
	public int getGoodsCount()
	{
		return goodsList.length;
	}


}
