package cc.lv1.rpg.gs.entity.controller;

import vin.rabbit.net.AppMessage;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.i.Map;
import vin.rabbit.util.collection.impl.ArrayList;
import vin.rabbit.util.collection.impl.HashMap;
import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.data.DC;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.container.PlayerContainer;
import cc.lv1.rpg.gs.entity.ext.Bag;
import cc.lv1.rpg.gs.entity.ext.TaskInfo;
import cc.lv1.rpg.gs.entity.impl.Goods;
import cc.lv1.rpg.gs.entity.impl.Player;
import cc.lv1.rpg.gs.net.SMsg;
import cc.lv1.rpg.gs.net.impl.SaveJob;
import cc.lv1.rpg.gs.other.ErrorCode;
import cc.lv1.rpg.gs.util.FontConver;

/**
 * 玩家交易容器
 * @author balan
 *
 */
public class BusinessController extends PlayerContainer
{
	/** 交易面板是否锁定 */
	private boolean isActiveLocked = false;
	
	private boolean isAcceptLocked = false;
	
	private long[] activeObjectIndex = new long[9];
	
	private long[] acceptObjectIndex = new long[9];
	
	private PlayerController activePlayer;
	
	private PlayerController acceptPlayer;
	
	private int activeMoney;
	
	private int acceptMoney;
	
	private RoomController room;

	private int getSize(int type)
	{
		int count = 0;
		if(type == 1)
		{
//			for (int i = 0; i < activeGoodsId.length; i++) 
//			{
//				if(activeGoodsId[i] == 0)
//					continue;
//				count++;
//			}
			for (int i = 0; i < activeObjectIndex.length; i++) 
			{
				if(activeObjectIndex[i] == 0)
					continue;
				count++;
			}
		}
		else if(type == 2)
		{
//			for (int i = 0; i < acceptGoodsId.length; i++) 
//			{
//				if(acceptGoodsId[i] == 0)
//					continue;
//				count++;
//			}
			for (int i = 0; i < acceptObjectIndex.length; i++) 
			{
				if(acceptObjectIndex[i] == 0)
					continue;
				count++;
			}
		}
		return count;
	}
	
	public void setParent(RoomController room)
	{
		this.room = room;
	}
	
	public void setActiveLocked(boolean isLocked)
	{
		this.isActiveLocked = isLocked;
	}
	
	public void setAcceptLocked(boolean isLocked)
	{
		this.isAcceptLocked = isLocked;
	}
	
	public boolean isLocked()
	{
		return (isAcceptLocked && isActiveLocked);
	}
	
	public void disOtherUnLocked(PlayerController player)
	{
		if(player.getID() == activePlayer.getID())
		{
			if(isAcceptLocked)
				isAcceptLocked = false;
		}
		else if(player.getID() == acceptPlayer.getID())
		{
			if(isActiveLocked)
				isActiveLocked = false;
		}
	}
	

	public boolean setDefault(PlayerController target,int money,long[] indexs)
	{
		Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
		if(money > bag.point)
			return false;
		if(target.getID() == activePlayer.getID())
		{
			activeMoney = money;
			activeObjectIndex = indexs;
			isActiveLocked = true;
			return true;
		}
		else if(target.getID() == acceptPlayer.getID())
		{
			acceptMoney = money;
			acceptObjectIndex = indexs;
			isAcceptLocked = true;
			return true;
		}
		return false;
	}
	
	public void setFighters(PlayerController active,PlayerController accept)
	{
		setParent(active.getRoom());
		
		active.setParent(this);
		active.setBusiness(this);
		active.getPlayer().state = Player.STATE_PERSONALSHOP;
		activePlayer = active;

		accept.setParent(this);
		accept.setBusiness(this);
		accept.getPlayer().state = Player.STATE_PERSONALSHOP;
		acceptPlayer = accept;
		
		addPlayer(activePlayer);
		addPlayer(acceptPlayer);
	}

	
	
	/**
	 * 物品放入或者拿出交易面板
	 * @param target
	 * @param msg
	 */
	public void goodsIO(PlayerController target,AppMessage msg)
	{
		if(!target.isOnline())
			return;
		if(isLocked())
			return;
		if((target.getID() == acceptPlayer.getID() && isAcceptLocked) 
				|| (target.getID() == activePlayer.getID() && isActiveLocked))
		{
			removePlayer(target);
			return;
		}
		
		int goodsType = msg.getBuffer().readInt();//表示是放物品还是钱
		Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
		ByteBuffer buffer = new ByteBuffer(64);
		buffer.writeByte(goodsType);
		if(goodsType == 1)//物品
		{
			int type = msg.getBuffer().readInt();//表示是放入还是拿出 1表示放入 2表示拿出 3表示锁定
			int goodsId = msg.getBuffer().readInt();
			int location = msg.getBuffer().readInt();
			int count = msg.getBuffer().readInt();//要放入或者拿出多少数量
			String objectIndex = msg.getBuffer().readUTF();

			if(location < 0 || count <= 0)
				return;
			buffer.writeByte(type);
			if(type == 1)//放入
			{
				Goods goods = null;
				if(objectIndex.isEmpty())
					goods = bag.getGoodsByBindmode(goodsId);
				else
				    goods = bag.getGoodsByObjectIndex(Long.parseLong(objectIndex));
				if(goods == null)
				{
					target.sendAlert(ErrorCode.ALERT_GOODS_NOT_EXIST);
					return;
				}
				if(goods.bindMode == 4)
				{
					target.sendAlert(ErrorCode.ALERT_GOODS_NOT_BUSINESS);
					return;
				}
				if(count > goods.repeatNumber)
				{
					target.sendAlert(ErrorCode.ALERT_GOODSCOUNT_OVERRUN);
					return;
				}
				if(count > bag.getGoodsCountByBindMode(goods.id))
				{
					target.sendAlert(ErrorCode.ALERT_GOODSCOUNT_OVERRUN);
					return;
				}
				goods.writeTo(buffer);
				buffer.writeByte(location);
				buffer.writeInt(count);
			}
			else if(type == 2)//取出
			{
				buffer.writeInt(goodsId);
				buffer.writeByte(location);
				buffer.writeInt(count);
			}
			
		}
		else if(goodsType == 2)
		{
			int money = msg.getBuffer().readInt();
			if(money > bag.point)
			{
				target.sendAlert(ErrorCode.ALERT_BUSINESS_MONEY_OVERRUN);
				return;
			}
			buffer.writeInt(money);
		}
		
		isAcceptLocked = false;
		isActiveLocked = false;
	
		if(target.getID() == activePlayer.getID())
		{	
			acceptPlayer.getNetConnection().sendMessage(new SMsg(SMsg.S_BUSINESS_GOODS_IO_COMMAND,buffer));
		}
		else if(target.getID() == acceptPlayer.getID())
		{
			activePlayer.getNetConnection().sendMessage(new SMsg(SMsg.S_BUSINESS_GOODS_IO_COMMAND,buffer));
		}
	}
	
	private void sendGoodsIO(PlayerController target,int goodsId,int location)
	{
		ByteBuffer buffer = new ByteBuffer();
		buffer.writeByte(1);
		buffer.writeByte(2);
		buffer.writeInt(goodsId);
		buffer.writeByte(location);
		buffer.writeInt(1);
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_BUSINESS_GOODS_IO_COMMAND,buffer));
	}
	
	
	/**
	 * 关闭交易
	 * @param target
	 */
	public void removePlayer(PlayerController target)
	{
		if(!(target.getParent() instanceof BusinessController))
		{
			target.sendAlert(ErrorCode.ALERT_BUSINESS_HASBEEN_OVER);
			return;
		}
		
		ByteBuffer buffer = new ByteBuffer(1);
		buffer.writeBoolean(true);
		dispatchMsg(SMsg.S_BUSINESS_OVER_COMMAND, buffer);//交易结束
		
		over();

		if(!target.isOnline())
			room.removePlayer(target);
	}
	
	
	private void businessOK(PlayerController target,AppMessage msg)
	{
		if(!target.isOnline())
			return;
		
		if(activePlayer == null || acceptPlayer == null)
		{
			return;
		}
		
		if(target.getID() == activePlayer.getID())
		{
			if(!target.isBusiness(target, acceptPlayer))
				return;
		}
		else if(target.getID() == acceptPlayer.getID())
		{
			if(!target.isBusiness(target, activePlayer))
				return;
		}

		Bag activeBag = (Bag) activePlayer.getPlayer().getExtPlayerInfo("bag");
		Bag acceptBag = (Bag) acceptPlayer.getPlayer().getExtPlayerInfo("bag");
		Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
		int money = msg.getBuffer().readInt();
		int listSize = msg.getBuffer().readInt();
		if(listSize < 0 || money < 0 || listSize > 9)
			return;
		long[] indexs = new long[listSize];
		if(money > bag.point)
		{
			target.sendAlert(ErrorCode.ALERT_BUSINESS_MONEY_OVERRUN);
			return;
		}
		Map map = new HashMap();
		for (int i = 0; i < listSize; i++) 
		{
			msg.getBuffer().readInt();
			msg.getBuffer().readInt();
			String objectIndex = msg.getBuffer().readUTF();
			if(map.get(objectIndex) != null)
				continue;
			if(objectIndex.isEmpty() || objectIndex.length() == 0 || "0".equals(objectIndex))
				return;
			Goods bagGoods = null;
			if(target.getID() == activePlayer.getID())
			{
				bagGoods = activeBag.getGoodsByObjectIndex(Long.parseLong(objectIndex));
				if(bagGoods == null)
				{
					target.sendAlert(ErrorCode.ALERT_GOODS_NOT_EXIST);
					return;
				}
				if(bagGoods.bindMode == 4)
				{
					target.sendAlert(ErrorCode.ALERT_GOODS_NOT_BUSINESS);
					return;
				}
			}
			else if(target.getID() == acceptPlayer.getID())
			{
				bagGoods = acceptBag.getGoodsByObjectIndex(Long.parseLong(objectIndex));
				if(bagGoods == null)
				{
					target.sendAlert(ErrorCode.ALERT_GOODS_NOT_EXIST);
					return;
				}
				if(bagGoods.bindMode == 4)
				{
					target.sendAlert(ErrorCode.ALERT_GOODS_NOT_BUSINESS);
					return;
				}
			}

			if(bagGoods == null)
				return;
			indexs[i] = Long.parseLong(objectIndex);
			map.put(objectIndex, "");
		}

		boolean flag = false;
		flag = setDefault(target,money,indexs);
		if(!flag)
		{
			target.sendAlert(ErrorCode.ALERT_BUSINESS_MONEY_OVERRUN);
			return;
		}

		ByteBuffer buffer = new ByteBuffer(1);
		buffer.writeByte(3);
		if(target.getID() == activePlayer.getID())
			acceptPlayer.getNetConnection().sendMessage(new SMsg(SMsg.S_BUSINESS_GOODS_IO_COMMAND,buffer));
		else if(target.getID() == acceptPlayer.getID())
			activePlayer.getNetConnection().sendMessage(new SMsg(SMsg.S_BUSINESS_GOODS_IO_COMMAND,buffer));

		if(isLocked())//双方都准备好了，添加到背包
		{
			if(activeBag.point < activeMoney)
				return;
			if(acceptBag.point < acceptMoney)
				return;
			
			boolean result = true;
			List activeList = new ArrayList();
			List acceptList = new ArrayList();
			for (int i = 0; i < activeObjectIndex.length; i++) 
			{
				if(activeObjectIndex[i] == 0)
					continue;
				Goods goods = activeBag.getGoodsByObjectIndex(activeObjectIndex[i]);
				if(goods == null || goods.useFlag)
				{
					return;
				}
				if(goods.bindMode == 4)
				{
					activePlayer.sendAlert(ErrorCode.ALERT_GOODS_NOT_BUSINESS);
					return;
				}
				activeList.add(goods);
			}
			if(!result)
				return;
			if(acceptBag.getNullCount() < activeList.size())
				return;
			if(!acceptBag.isCanAddGoodsList(activeList))
				return;
				
			for (int i = 0; i < acceptObjectIndex.length; i++) 
			{
				if(acceptObjectIndex[i] == 0)
					continue;
				Goods goods = acceptBag.getGoodsByObjectIndex(acceptObjectIndex[i]);
				if(goods == null || goods.useFlag)
				{
					return;
				}
				if(goods.bindMode == 4)
				{
					acceptPlayer.sendAlert(ErrorCode.ALERT_GOODS_NOT_BUSINESS);
					return;
				}
				acceptList.add(goods);
			}
			if(!result)
				return;
			if(activeBag.getNullCount() < acceptList.size())
				return;
			if(!activeBag.isCanAddGoodsList(acceptList))
				return;
			
			
			if(result)
			{
				if(!activePlayer.isOnline() || !acceptPlayer.isOnline() || !target.isOnline())
					return;
				
				/***************infos************************************/
				if(acceptPlayer.getPlayer().isVipPlayer || activePlayer.getPlayer().isVipPlayer)
				{
					StringBuffer data = new StringBuffer();
					data.append(DC.getString(DC.SHOPCENTER_12));
					data.append(":");
					data.append(activePlayer.getName());
					data.append("[");
					for (int i = 0; i < activeList.size(); i++) 
					{
						Goods goods = (Goods) activeList.get(i);
						data.append(goods.name);
						data.append("x");
						data.append(goods.goodsCount);
					}
					if(activeMoney > 0)
					{
						data.append("point:");
						data.append(activeMoney);
					}
					
					data.append("]-to-");
					data.append(acceptPlayer.getName());
					data.append("[");
					for (int i = 0; i < acceptList.size(); i++) 
					{
						Goods goods = (Goods) acceptList.get(i);
						data.append(goods.name);
						data.append("x");
						data.append(goods.goodsCount);
					}
					if(acceptMoney > 0)
					{
						data.append("point:");
						data.append(acceptMoney);
					}
					data.append("]");
					DataFactory.getInstance().addVipPlayerInfo(target, data.toString());
				}
				/***************************************************/
				
				for (int i = 0; i < activeList.size(); i++) 
				{
					Goods goods = (Goods) activeList.get(i);
					activeBag.removeBusinessGoods(activePlayer,goods.objectIndex);
					acceptBag.sendAddGoods(acceptPlayer, goods);
				}
				for (int i = 0; i < acceptList.size(); i++) 
				{
					Goods goods = (Goods) acceptList.get(i);
					acceptBag.removeBusinessGoods(acceptPlayer,goods.objectIndex);
					activeBag.sendAddGoods(activePlayer, goods);
				}

				acceptBag.point += activeMoney;
				activeBag.point -= activeMoney;
				activeBag.point += acceptMoney;
				acceptBag.point -= acceptMoney;
			}

			buffer = new ByteBuffer(5);
			buffer.writeBoolean(result);//交易成功或者失败
			WorldManager.sendPoint(buffer,activeBag.point);
			activePlayer.getNetConnection().sendMessage(new SMsg(SMsg.S_BUSINESS_OK_COMMAND,buffer));
			buffer = new ByteBuffer(5);
			buffer.writeBoolean(result);//交易成功或者失败
			WorldManager.sendPoint(buffer,acceptBag.point);
			acceptPlayer.getNetConnection().sendMessage(new SMsg(SMsg.S_BUSINESS_OK_COMMAND,buffer));
			
			if(!result)
				removePlayer(activePlayer);
			else
				over();
		}
	}

	
	private Goods getGoods(List list,int id)
	{
		for (int i = 0; i < list.size(); i++) 
		{
			if(list.get(i) == null || !(list.get(i) instanceof Goods))
				continue;
			Goods goods = (Goods) list.get(i);
			if(goods.id == id)
				return goods;
		}
		return null;
	}
	
	
/*	private void over()
	{
		activePlayer.setParent(room);
		activePlayer.setBusiness(null);
		acceptPlayer.setBusiness(null);
		acceptPlayer.setParent(room);
		super.removePlayer(activePlayer);
		super.removePlayer(acceptPlayer);
		
		activePlayer = null;
		acceptPlayer = null;
		
	}*/
	
	private void over()
	{
		if(activePlayer != null)
			GameServer.getInstance().getWorldManager().getJobObserver().addJob(GameServer.JOB_DATABASEJOB,
					new SaveJob(GameServer.getInstance().getWorldManager(),activePlayer.getPlayer(),SaveJob.BUSINESS_SAVE));
		if(acceptPlayer != null)
			GameServer.getInstance().getWorldManager().getJobObserver().addJob(GameServer.JOB_DATABASEJOB,
					new SaveJob(GameServer.getInstance().getWorldManager(),acceptPlayer.getPlayer(),SaveJob.BUSINESS_SAVE));
		  activePlayer.setParent(room);
		  activePlayer.setBusiness(null);
		  acceptPlayer.setBusiness(null);
		  acceptPlayer.setParent(room);
		  super.removePlayer(activePlayer);
		  super.removePlayer(acceptPlayer);
		  
		  activePlayer = null;
		  acceptPlayer = null;
	 }
	

	
	
	public void clientMessageChain(PlayerController target, AppMessage msg)
	{
		int type = msg.getType();
		
		if(type == SMsg.C_BUSINESS_GOODS_IO_COMMAND)
		{
			goodsIO(target,msg);
		}
		else if(type == SMsg.C_BUSINESS_OVER_COMMAND)
		{
			removePlayer(target);
		}
		else if(type == SMsg.C_BUSINESS_OK_COMMAND)
		{
			businessOK(target,msg);
		}
	}
	
}
