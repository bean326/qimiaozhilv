package cc.lv1.rpg.gs.entity.impl;


import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.data.DC;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.data.PressureTest;
import cc.lv1.rpg.gs.entity.RPGameObject;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.ext.Bag;
import cc.lv1.rpg.gs.entity.ext.PetTome;
import cc.lv1.rpg.gs.entity.ext.PVPInfo;
import cc.lv1.rpg.gs.entity.ext.TaskInfo;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsEquip;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsPetEquip;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsProp;
import cc.lv1.rpg.gs.entity.impl.pet.Pet;
import cc.lv1.rpg.gs.gui.MainFrame;
import cc.lv1.rpg.gs.net.SMsg;
import cc.lv1.rpg.gs.net.impl.PayJob;
import cc.lv1.rpg.gs.net.impl.PressureJob;
import cc.lv1.rpg.gs.net.impl.SaveJob;
import cc.lv1.rpg.gs.other.ErrorCode;
import cc.lv1.rpg.gs.util.FontConver;
import vin.rabbit.net.AppMessage;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.i.Map;
import vin.rabbit.util.collection.impl.ArrayList;
import vin.rabbit.util.collection.impl.HashMap;


public class Shop extends RPGameObject{
	
	/** 商城(可用代金券的商店) */
	public static final int TYPE_MONEY_SHOP = 1000000000;
	/** 淘宝(只能用元宝的商店) */
	public static final int TYPE_VIP_MONEY_SHOP = 1000000001;
	
	public static final int REBATE = 50;
	/** 物品列表 */
	private List goodsList = new ArrayList(60);
	
	private HashMap saleRate; 
	
	private HashMap buyRate;
	
	private HashMap honourMap;
	
	private HashMap sourceIds;
	
	/** 商店类型
	 * 0:普通商店(只能用游戏币购买,只能出售部分物品)
	 * 1:代金商城(只能用代金券购买,可以出售物品)
	 * 2:荣誉商城(只能用荣誉值购买)
	 * 3:元宝商城(只能用元宝购买,不能在这里出售物品) 
	 * 4:礼券商城(只能用礼券兑换,不能在这里出售物品)
	 * 5:宠物商城(守护,暂时是用元宝购买)
	*/
	public int shopType;

	public Shop(int shopId,List list,HashMap sr,HashMap br,HashMap honour,HashMap sis)
	{
		id = shopId;
		goodsList = list;
		saleRate = sr;
		buyRate = br;
		honourMap = honour;
		sourceIds = sis;
		setShopType();
	}
	
	public int goodsCount()
	{
		return goodsList.size();
	}
	
	public List getGoodsList()
	{
		return goodsList;
	}
	
	public Goods getGoods(int id)
	{
		for (int i = 0; i < goodsList.size(); i++)
		{
			Goods goods = (Goods) goodsList.get(i);
			if(goods != null && goods.id == id)
				return goods;
		}
		return null;
	}
	
	public int getBuyRebate(int goodsId)
	{
		String str = buyRate.get(goodsId).toString();
		return Integer.parseInt(str);
	}
	
	public void setBuyRate(int goodsId,int rate)
	{
		buyRate.put(goodsId, rate);
	}
	
	public void writeTo(ByteBuffer buffer)
	{
		buffer.writeInt(id);
		buffer.writeByte(shopType);
		int length = goodsList.size();
		int count = 0;
		for (int i = 0; i < length; i++)
		{
			Goods goods = (Goods) goodsList.get(i);
			if(goods.type == 15)
				continue;
			count++;
		}
		buffer.writeInt(count);
		for (int i = 0; i < length; i++)
		{
			Goods goods = (Goods) goodsList.get(i);
			if(goods.type == 15)
				continue;
			goods.writeTo(buffer);
			if(shopType == 4)
			{
				buffer.writeInt(100);
				buffer.writeInt(0);
			}
			else
			{
				if(buyRate.get(goods.id) == null)
					buffer.writeInt(100);
				else
				{
					int br = Integer.parseInt(buyRate.get(goods.id).toString()) ;
					buffer.writeInt(br);
				}
				if(honourMap.get(goods.id) == null)
					buffer.writeInt(0);
				else
				{
					int honour = Integer.parseInt(honourMap.get(goods.id).toString()) ;
					buffer.writeInt(honour);
				}
			}
		}
	}
	
	
	
	
	
	/**
	 * 打开商城
	 * @param target
	 */
	private void openShop(PlayerController target)
	{
		ByteBuffer buffer = new ByteBuffer();
		writeTo(buffer);
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_OPEN_SHOP_COMMAND,buffer));
	}
	
	
	/**
	 * 购买商品
	 * @param target 玩家
	 * @param buffer 接收参数
	 */
	private void buyGoods(PlayerController target, ByteBuffer buffer)
	{
		int goodsId = buffer.readInt();//物品ID
		int count = buffer.readInt();//要购买的物品数量
		if(shopType == 4 && count > 1)
		{
			System.out.println("Shop buyGoods count more than 1!"+target.getName());
			return;
		}
		if(count <=0 || goodsId <= 0)
			return;
		//判断商店是否有这个物品
		Object goodsObject = getGoodsById(goodsId);
		if(goodsObject == null || !(goodsObject instanceof Goods))
		{
			MainFrame.println("---shop not the goods:"+goodsId+"  goodsCount:"+count+"---");
			//物品不存在
			target.sendAlert(ErrorCode.ALERT_GOODS_NOT_EXIST);
			return;
		}
		Goods goods = (Goods) goodsObject;
		if(count > goods.repeatNumber)
		{
			//数量超出限制 
			target.sendAlert(ErrorCode.ALERT_GOODS_COUNT_OVERRUN);
			return;
		}
		
		//判断玩家的钱够不够买商品
		int br = Integer.parseInt(buyRate.get(goods.id).toString());
		int needPoint = (int) (goods.point * ((double)br/100) * count);
		int needMoney = (int) (goods.money * ((double)br/100) * count);
		int needToken = (int) (goods.token * ((double)br/100) * count);
		int honour = Integer.parseInt(honourMap.get(goods.id).toString()) ;
		int needHonour = (int) (honour * ((double)br/100) * count);
		
		Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
		
		Goods liquan = null;
		
		if(shopType == 1)
		{
			if("new".equals(DataFactory.shopVer))
			{
				if(bag.equipMoney < needToken)
				{
					//玩家的钱不够了
					target.sendAlert(ErrorCode.ALERT_SHOP_NO_TOKEN);
					return;
				}
			}
			else
			{
				if(bag.money + bag.equipMoney < needToken)
				{
					target.sendAlert(ErrorCode.ALERT_SHOP_NO_TOKEN);
					return;
				}
			}
		}
		else if(shopType == 2)
		{
			if(target.getHonour() < needHonour)
			{
				//玩家的荣誉值不够了
				target.sendAlert(ErrorCode.ALERT_SHOP_NO_HONOUR);
				return;
			}
		}
		else if(shopType == 3)
		{
			if("new".equals(DataFactory.shopVer))
			{
				if(bag.money < needMoney)
				{
					//玩家的钱不够了
					target.sendAlert(ErrorCode.ALERT_SHOP_NO_MONEY);
					return;
				}
			}
			else
			{
				if(bag.money < needToken)
				{
					//玩家的钱不够了
					target.sendAlert(ErrorCode.ALERT_SHOP_NO_MONEY);
					return;
				}
			}
		}
		else if(shopType == 4)
		{
			//取背包里的礼券判断，只能一换一,相同价值的换相同价值的
			Object obj = sourceIds.get(goods.id);
			if(obj == null)
			{
				System.out.println("Shop shopType==4 obj is null!");
				return;
			}
			int needId = Integer.parseInt(obj.toString());
			liquan = bag.getGoodsById(needId);
			if(liquan == null)
			{
				target.sendAlert(ErrorCode.ALERT_SHOP_NO_LIQUAN);
				return;
			}
		}
		else if(shopType == 5)
		{
			if(bag.point < needPoint)
			{
				//玩家的钱不够了
				target.sendAlert(ErrorCode.ALERT_SHOP_NO_POINT);
				return;
			}
			if(bag.money < needMoney)
			{
				//玩家的钱不够了
				target.sendAlert(ErrorCode.ALERT_SHOP_NO_MONEY);
				return;
			}
		}
		else
		{
			if(bag.point < needPoint || bag.money < needMoney)
			{
				//玩家的钱不够了
				target.sendAlert(ErrorCode.ALERT_SHOP_NO_POINT);
				return;
			}
		}
		
		Goods newGoods = getNewGoods(goods,count);
		if(newGoods.repeatNumber == 1 && bag.getNullCount() == 0)
		{
			target.sendAlert(ErrorCode.ALERT_BAG_IS_FULL);
			return;
		}
		if(!bag.isCanAddGoodsToBag(target, newGoods))
		{
			return;
		}
		
		if(shopType == 1)
		{
			if("new".equals(DataFactory.shopVer))
			{
				bag.equipMoney -= needToken;
			}
			else
			{
				if(bag.equipMoney >= needToken)
					bag.equipMoney -= needToken;
				else
				{
					needToken -= bag.equipMoney;
					bag.equipMoney = 0;
					bag.money -= needToken;
				}
			}

			GameServer.getInstance().getWorldManager().getJobObserver().addJob(GameServer.JOB_DATABASEJOB,
					new PayJob(target,2,0,needToken,GameServer.getInstance().id+":"+target.getName()+":"+newGoods.name+":"+newGoods.goodsCount));
			
		}
		else if(shopType == 2)
		{
			GameServer.getInstance().getWorldManager().getJobObserver().addJob(GameServer.JOB_DATABASEJOB,
					new PayJob(target,2,0,needHonour,GameServer.getInstance().id+":honor:"+target.getName()+":"+newGoods.name+":"+newGoods.goodsCount));
			
			target.setHonour(-needHonour);
		}
		else if(shopType == 3)
		{
			if("new".equals(DataFactory.shopVer))
			{
				bag.money -= needMoney;
				GameServer.getInstance().getWorldManager().getJobObserver().addJob(GameServer.JOB_DATABASEJOB,
						new PayJob(target,2,0,needMoney,GameServer.getInstance().id+":money:"+target.getName()+":"+newGoods.name+":"+newGoods.goodsCount));
			}
			else
			{
				bag.money -= needToken;
				GameServer.getInstance().getWorldManager().getJobObserver().addJob(GameServer.JOB_DATABASEJOB,
						new PayJob(target,2,0,needToken,GameServer.getInstance().id+":money:"+target.getName()+":"+newGoods.name+":"+newGoods.goodsCount));
			}
		}
		else if(shopType == 4)
		{
			bag.removeGoods(target, liquan.objectIndex, 1);
			
			GameServer.getInstance().getWorldManager().getJobObserver().addJob(GameServer.JOB_DATABASEJOB,
					new PayJob(target,8,0,needMoney,GameServer.getInstance().id+":duihuan:"+target.getName()+":from:"+liquan.name+":to:"+newGoods.name));
		}
		else if(shopType == 5)
		{
			if(newGoods.type == 15)
			{
				PetTome pt = (PetTome) target.getPlayer().getExtPlayerInfo("petTome");
				if(pt.getBattlePet(((GoodsProp)newGoods).petId) != null)
				{
					target.sendAlert(ErrorCode.ALERT_PET_IS_EXITS);
					return;
				}
				if(pt.isBattlePetMaxCount())
				{
					target.sendAlert(ErrorCode.ALERT_PET_COUNT_MAX);
					return;
				}
				if(!isBuyPet(target, newGoods.id))
				{
					target.sendAlert(ErrorCode.ALERT_NOT_GET_PET);
					return;
				}
				addPet(target,((GoodsProp)newGoods).petId);
			}
			
			bag.point -= needPoint;
			bag.money -= needMoney;
			bag.sendAddGoods(target, null);
		}
		else
		{
			bag.point -= needPoint;
		}	
		
		if(newGoods.type != 15)
			bag.sendAddGoods(target, newGoods);
	}
	
	private boolean isBuyPet(PlayerController target,int petGoods)
	{
		Map map = (Map) DataFactory.getInstance().getAttachment(DataFactory.GET_PET_RULE);
		if(map == null)
			return false;
		int job = target.getPlayer().upProfession+4;
		Object obj = map.get(job);
		if(obj == null)
			return false;
		String pets = obj.toString();
		if(pets.indexOf(petGoods+"") == -1)
		{
			return false;
		}
		return true;
	}
	
	private void addPet(PlayerController target,int petId)
	{
		Pet pet = (Pet) DataFactory.getInstance().getGameObject(petId);
		Pet newPet = (Pet) Pet.cloneObject(pet);
		newPet.objectIndex = GameServer.getInstance().getDatabaseAccessor().getGoodsObjIndex();
		newPet.requireExp = newPet.getInitExp();
		newPet.setPlayer(target.getPlayer());
		PetTome pt = (PetTome) target.getPlayer().getExtPlayerInfo("petTome");
		newPet.initBaseSkill();
		if(pt.getBattlePetCount() == 0)
			newPet.isActive = true;
		pt.addPet(newPet);
		
		target.sendError(DC.getString(DC.PET_5).replace("[PP]", newPet.name));

		ByteBuffer buffer = new ByteBuffer();
		buffer.writeByte(2);
		Pet[] pets = pt.getPets();
		int count = 0;
		for (int i = 0; i < pets.length; i++) 
		{
			if(pets[i] == null)
				continue;
			if(pets[i].petType != Pet.SHOUHUPET)
				continue;
			count++;
		}
		buffer.writeByte(count);
		for (int i = 0; i < pets.length; i++) 
		{
			if(pets[i] == null)
				continue;
			if(pets[i].petType != Pet.SHOUHUPET)
				continue;
			pets[i].writeBattlePetTo(buffer);
		}
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_PLAYER_BATTLE_PET_BASE_COMMAND,buffer));
	}
	
	/**
	 * 玩家出售商品
	 * @param target
	 * @param buffer
	 */
	private void saleGoods(PlayerController target,ByteBuffer buffer)
	{
		if(shopType == 4 || shopType == 3)//元宝商城和礼券商城不能出售物品
		{
			target.sendAlert(ErrorCode.ALERT_GOODS_NO_SALE_THERE);
			return;
		}
		int goodsId = buffer.readInt();
		int count = buffer.readInt();
		String objectIndex = buffer.readUTF();
		
		Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
		Object goodsObject = null;
		if(objectIndex.isEmpty() && goodsId != 0)
			goodsObject = bag.getGoodsById(goodsId);
		else
		    goodsObject = bag.getGoodsByObjectIndex(Long.parseLong(objectIndex));
		if(goodsObject == null || !(goodsObject instanceof Goods))
		{
			//物品不存在
			target.sendAlert(ErrorCode.ALERT_GOODS_NOT_EXIST);
			return;
		}
		Goods goods = (Goods) goodsObject;
		int bagGoodsCount = bag.getGoodsCount(goods.id);
		if(count <= 0)
		{
			return;
		}
		if(count > bagGoodsCount)
		{
			target.sendAlert(ErrorCode.ALERT_GOODS_COUNT_OVERRUN);
			return;
		}
		
		if(goods instanceof GoodsPetEquip)
		{
			if(shopType != 5)
			{
				target.sendAlert(ErrorCode.ALERT_GOODS_NO_SALE_THERE);
				return;
			}
			if(((GoodsPetEquip)goods).growPoint > 0)
			{
				target.sendAlert(ErrorCode.ALERT_SHOP_NOT_SALE);
				return;
			}
		}
		
		boolean result = false;
		long bPoint = bag.point;
		long bDaijin = bag.equipMoney;
		PVPInfo oei = (PVPInfo) target.getPlayer().getExtPlayerInfo("PVPInfo");
		int bHonor = oei.honourPoint;
		if(shopType == 1)
		{
			if(goods.type == 4 || goods.type == 6)
			{
				Goods shopGoods = getGoodsById(goods.id);
				if(shopGoods == null)
				{
					target.sendAlert(ErrorCode.ALERT_SHOP_NOT_SALE);
					return;
				}
			}
			else
			{
				if(goods.type != 1)
				{
					target.sendAlert(ErrorCode.ALERT_SHOP_NOT_SALE);
					return;
				}
			}
			if(goods.token == 0)
			{
				target.sendAlert(ErrorCode.ALERT_SHOP_NOT_SALE);
				return;
			}
			if(goods.token > 0)
			{
				int saleMoney = (int) (goods.token * ((double)REBATE/100) * count);
				bag.equipMoney += saleMoney;
				StringBuffer sb = new StringBuffer();
				sb.append(DC.getString(DC.GOODS_13));
				sb.append(goods.name);
				sb.append(",");
				sb.append(DC.getString(DC.GOODS_14));
				sb.append(saleMoney);
				sb.append(DC.getString(DC.GOODS_15));
				target.sendGetGoodsInfo(1, false, sb.toString());
				result = true;
			}
		}
		else
		{
			if(shopType == 2 && getGoods(goods.id) != null && goods instanceof GoodsEquip)
			{
				int honour = Integer.parseInt(honourMap.get(goods.id).toString()) ;
				int saleHonor = (int) (honour * ((double)50/100) * count);
				oei.setHonourPoint(saleHonor);
				result = true;
			}
			else
			{
				if(goods.token > 0)
				{
					if(goods.type == 1 ||goods.type == 6)
						target.sendAlert(ErrorCode.ALERT_GOODS_NOSALE_THERE);
					else
						target.sendAlert(ErrorCode.ALERT_SHOP_NOT_SALE);
					return;
				}
				if(goods.bindMode == 4)
				{
					target.sendAlert(ErrorCode.ALERT_SHOP_NOT_SALE);
					return;
				}
				int sr = 30;
				if(saleRate.get(goods.id) != null)
					sr = Integer.parseInt(saleRate.get(goods.id).toString());
				int salePoint = (int) (goods.point * ((double)sr/100) * count);
				bag.point += salePoint;
				result = true;
			}
		}
		
		if(result)
		{
			
			//保存消费出售记录 类型 9  大于500元宝
			if(goods.token >= 500)
			{
				GameServer.getInstance().getWorldManager().getJobObserver().addJob(GameServer.JOB_DATABASEJOB,
						new PayJob(target,9,0,goods.token,GameServer.getInstance().id+":"+target.getName()+":"+goods.name+":"+goods.goodsCount));
				
				//保存一次
				target.getWorldManager().getJobObserver().addJob(GameServer.JOB_DATABASEJOB,
						new SaveJob(target.getWorldManager(),target.getPlayer(),SaveJob.GOODS_SALE_SAVE));
			}
			
			
			if(goods.repeatNumber <= 1)
				bag.removeGoods(target, goods.objectIndex, count);
			else
				bag.deleteGoods(target, goods.id, count);

		    ByteBuffer buff = new ByteBuffer(16);
			buff.writeInt(goods.id);
			buff.writeInt(count);
			WorldManager.sendPoint(buff, bag.point);
			buff.writeInt((int) bag.money);
			buff.writeInt((int) bag.equipMoney);
			target.getNetConnection().sendMessage(new SMsg(SMsg.S_SALE_GOODS_COMMAND,buff));

			if(target.getPlayer().isVipPlayer)
			{
				StringBuffer data = new StringBuffer();
				data.append(DC.getString(DC.GOODS_13));
				data.append(goods.name);
				data.append("x");
				data.append(count);
				data.append(",");
				data.append(DC.getString(DC.GOODS_14));
				data.append("[");
				long getPoint = bag.point - bPoint;
				long getDaijin = bag.equipMoney - bDaijin;
				int getHonor = oei.honourPoint - bHonor;
				if(getPoint > 0)
				{
					data.append("point:");
					data.append(getPoint);
				}
				if(getDaijin > 0)
				{
					data.append("daijin:");
					data.append(getDaijin);
				}
				if(getHonor > 0)
				{
					data.append("honor:");
					data.append(getHonor);
				}
				data.append("]");
				DataFactory.getInstance().addVipPlayerInfo(target, data.toString());
			}
		}
	}
	
	
	
	 /**
	  * 根据ID获取物品
	  * @param id
	  * @return
	  */
	private Goods getGoodsById(int id)
	{
		for (int i = 0; i < goodsList.size(); i++) 
		{
			Goods goods = (Goods) goodsList.get(i);
			if(goods.id == id)
				return goods;
		}
		return null;
	}
	
	
	public boolean isPutongShop()
	{
		return shopType == 0;
	}
	
	public void setShopType()
	{
		if(id == 1000000000)
			shopType = 1;
		else if(id == 2000000000)
			shopType = 2;
		else if(id == 1000000001)
			shopType = 3;
		else if(id == 1000000002)
			shopType = 4;
		else if(id == 1000000003)
			shopType = 5;
		else
			shopType = 0;
	}
	
	/**
	 * 生成购买的物品
	 * @param goods
	 * @param count
	 * @return
	 */
	private Goods getNewGoods(Goods goods,int count)
	{
		Goods newGoods = null;
		if(goods instanceof GoodsEquip)
		{
			GoodsEquip n = (GoodsEquip) DataFactory.getInstance().getGameObject(goods.id);
			GoodsEquip newEquip = (GoodsEquip) Goods.cloneObject(n);
			if(shopType == 0)
			{
				newGoods = newEquip.makeNewBetterEquip(0);
			}
			else
			{
				newGoods = newEquip.makeNewBetterEquip(newEquip.taskColor==-1?0:newEquip.taskColor);
			}
			if(shopType == 1)
				newGoods.setBindMode(Goods.MONEYSHOPBIND);
			else if(shopType == 0)
				newGoods.setBindMode(Goods.COMMONSHOPBIND);
		}
		else if(goods instanceof GoodsPetEquip)
		{
			newGoods = (Goods) Goods.cloneObject(goods);
		}
		else
		{
			newGoods = (Goods) Goods.cloneObject(goods);
			newGoods.id = goods.id;
			newGoods.name = goods.name;
			newGoods.type = goods.type;
			newGoods.repeatNumber = goods.repeatNumber;
			newGoods.iconId = goods.iconId;
			newGoods.goodsCount = count;
		}
		newGoods.objectIndex = GameServer.getInstance().getDatabaseAccessor().getGoodsObjIndex();
		return newGoods;
	}

	public Goods getGoodsByPetId(int petId)
	{
		for (int i = 0; i < goodsList.size(); i++)
		{
			Goods goods = (Goods) goodsList.get(i);
			if(goods.type != 15)
				continue;
			GoodsProp prop = (GoodsProp) goods;
			if(prop.petId == petId)
				return prop;
		}
		return null;
	}
	
	
	/**
	 * 与商店有关的客户端消息通道
	 * @param target
	 * @param msg
	 */
	public void clientMessageChain(PlayerController target, AppMessage msg)
	{
		int type = msg.getType();
		ByteBuffer buffer = msg.getBuffer();
	
		if(type == SMsg.C_BUY_GOODS_COMMAND)
		{
			buyGoods(target,buffer);
		}
		else if(type == SMsg.C_SALE_GOODS_COMMAND)
		{
			saleGoods(target,buffer);
		}
		else if(type == SMsg.C_OPEN_SHOP_COMMAND)
		{
			openShop(target);
		}
	}
	
	
	

	
}
