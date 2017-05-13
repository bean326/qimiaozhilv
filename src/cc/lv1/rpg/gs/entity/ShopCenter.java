package cc.lv1.rpg.gs.entity;

import vin.rabbit.net.AppMessage;
import vin.rabbit.net.abs.NetConnection;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.impl.ArrayList;
import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.data.DC;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.data.PressureTest;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.ext.Bag;
import cc.lv1.rpg.gs.entity.ext.MailBox;
import cc.lv1.rpg.gs.entity.impl.Goods;
import cc.lv1.rpg.gs.entity.impl.Mail;
import cc.lv1.rpg.gs.entity.impl.Player;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsEquip;
import cc.lv1.rpg.gs.gui.MainFrame;
import cc.lv1.rpg.gs.net.SMsg;
import cc.lv1.rpg.gs.net.impl.SaveJob;
import cc.lv1.rpg.gs.net.impl.ShopCenterInfoJob;
import cc.lv1.rpg.gs.net.impl.ShopCenterUpdateJob;
import cc.lv1.rpg.gs.other.ErrorCode;
import cc.lv1.rpg.gs.util.FontConver;

/**
 * 交易中心
 * @author dxw
 *
 */
public class ShopCenter implements vin.rabbit.comm.i.IDatabaseHandle
{
	
	private static int MAX = 2000;
	
	private static final int PAGECOUNT = 0x0A;
	
	private static final int BASEQUERY = 0x01;
	
	private static final int ADD = 0x02;

	private static final int GET = 0x03;
	
	private static final int CANCEL = 0x04;
	
	private static final int MYLIST = 0x05;
	
	private static final int GETINFO = 0x06;
	
	private static final int GOODS_NOHAS = 0x07;
	
	private static ShopCenter sc;
	
	/** 0：通用1：士兵 2：运动员 3：医护人员 4：超能力者5：军官 6：特种兵 7：足球运动员 8： 篮球运动员9：医生10：护士11：超人12：赌圣
	 * 13：一转军官
	   14：一转运动员
	   15：一转护士
	   16：一转超能力
	   17：二转军官
	   18：二转运动员
	   19：二转护士
	   20：二转超能力
	   21：三转军官
	   22：三转运动员
	   23：三转护士
	   24：三转超能力
	 *  武器0，头盔1，上衣2，裤子3，鞋4，护腕5，手套6，饰品7（项链，戒指，装饰）equipCount
	 */
	private int [][] jobsCount = new int[JOBSUM][8];
	public static final int JOBSUM = 25;
	/**
	 * 饰品数量
	 */
	private int adornmentCount;
	
	/**
	 * 道具数量
	 */
	private int otherCount;
	
	/**
	 * 中心正在销售的道具
	 */
	private ArrayList centerGoods = new ArrayList(500);
	
/*	*//**
	 * 中心已卖出的道具
	 *//*
	private ArrayList saledGoods = new ArrayList(500);
	
	*//**
	 * 中心已过期的道具
	 *//*
	private ArrayList overdueGoods = new ArrayList(1000);*/
	
	private ShopCenter()
	{
	}
	
	public static ShopCenter getInstance()
	{
		if(sc == null)
			sc = new ShopCenter();
		return sc;
	}
	
	
	public boolean isShopFull()
	{
		return centerGoods.size() > MAX;
	}

	
	public void loadFrom(ByteBuffer buffer)
	{
		for (int i = 0; i < 13; i++)
		{
			for (int j = 0; j < 8; j++)
			{
				buffer.readInt();
			}
		}

		//otherCount = buffer.readInt();

		buffer.readInt();

		long timeMillis = System.currentTimeMillis();
		int count = buffer.readInt();
		for (int i = 0; i < count; i++)
		{
			CenterGoods cg = new CenterGoods();
			cg.setTimer(timeMillis);
			cg.loadFrom(buffer);
			centerGoods.add(cg);
			
			onAddOrRemove(cg, true);
		}

	}

	public List getCenterGoods()
	{
		return centerGoods;
	}
	
	public void saveTo(ByteBuffer buffer)
	{
		for (int i = 0; i < 13; i++)
		{
			for (int j = 0; j < 8; j++)
			{
				buffer.writeInt(jobsCount[i][j]);
			}
		}
		buffer.writeInt(otherCount);
		
		int count = centerGoods.size();
		int len = 0;
		ByteBuffer nBuff = new ByteBuffer();
		for (int i = 0; i < count; i++)
		{
			CenterGoods cg = (CenterGoods)centerGoods.get(i);
			
			if(cg == null)
				continue;
			
			if(cg.getGoods() == null)
				continue;
			
			len++;
			cg.saveTo(nBuff);
		}
		buffer.writeInt(len);
		buffer.writeBytes(nBuff.getBytes());
	}

	public void clientMessageChain(PlayerController target, AppMessage msg)
	{
		ByteBuffer buffer = msg.getBuffer();
		int type = buffer.readByte();	//1 查询 , 2 增加  //3 取得
		
		if(type == BASEQUERY)
		{
			centerQuery(target,buffer);
		}
		else if(type == ADD)
		{
			addGoodsToCenter(target,buffer);
		}
		else if(type == GET)
		{
			getGoodsFromCenter(target,buffer);
		}
		else if(type == CANCEL)
		{
			cancelGoodsFromCenter(target,buffer);
		}
		else if(type == MYLIST)
		{
			queryMyList(target,buffer);
		}
		else if(type == GETINFO)
		{
			getGoodsInfo(target,buffer);
		}
	}

	private void getGoodsInfo(PlayerController target, ByteBuffer buffer)
	{

		long objectIndex = 0;
		
		try
		{
			objectIndex = Long.parseLong(buffer.readUTF());//流水号
		}
		catch(NumberFormatException e)
		{
			System.out.println("NumberFormatException ShopCenter getGoodsInfo");
			return;
		}
		
		CenterGoods cg = getCenterGoodsByObjectIndex(objectIndex);
		
		if(cg == null)
		{
			buffer = new ByteBuffer(16);
			buffer.writeByte(GOODS_NOHAS);
			buffer.writeUTF(objectIndex+"");
			target.getNetConnection().sendMessage(new SMsg(SMsg.S_SHOPCENTER_COMMAND,buffer));
			return;
		}
		

		buffer = new ByteBuffer(64);
		buffer.writeByte(GETINFO);
		cg.getGoods().writeTo(buffer);
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_SHOPCENTER_COMMAND,buffer));
		buffer = null;
	}

	private void queryMyList(PlayerController target, ByteBuffer buffer)
	{
		
		buffer = new ByteBuffer(256);
		
		int size = centerGoods.size();
		int count = 0;
		
		for (int i = 0; i < size; i++)
		{
			CenterGoods cg = (CenterGoods)centerGoods.get(i);
			
			if(cg.getAccountName().equals(target.getPlayer().accountName))
			{
				Goods g = cg.getGoods();
				buffer.writeInt(g.id);
				buffer.writeInt(g.iconId);
				buffer.writeUTF(g.name);
				buffer.writeUTF(g.objectIndex+"");
				buffer.writeInt(g.level);
				buffer.writeInt(g.goodsCount);
				buffer.writeUTF(cg.getName());
				buffer.writeInt(cg.getPoint());
				buffer.writeByte(cg.getTimeHour());	
				count++;
			}
		}
		target.centerGoodsCount = count;
		
		ByteBuffer sendBuffer = new ByteBuffer(buffer.available()+5);
		sendBuffer.writeByte(MYLIST);
		sendBuffer.writeInt(count);
		sendBuffer.writeBytes(buffer.getBytes());
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_SHOPCENTER_COMMAND,sendBuffer));
		buffer = null;
	}

	private void cancelGoodsFromCenter(PlayerController target,
			ByteBuffer buffer)
	{
		long objectIndex = 0;
		
		try
		{
			objectIndex = Long.parseLong(buffer.readUTF());//流水号
		}
		catch(NumberFormatException e)
		{
			System.out.println("NumberFormatException ShopCenter getGoodsFromCenter");
			return;
		}
		
		CenterGoods cg = getCenterGoodsByObjectIndex(objectIndex);
		
		if(cg == null)
		{
			target.sendAlert(ErrorCode.ALERT_SHOPCENTER_GOODS_NULL);
			return;
		}
		
		if(!(target.getPlayer().accountName.equals(cg.getAccountName())))
		{
			//target.sendError("这件物品不是你的");
			return;
		}
		
		Bag bag = (Bag)target.getPlayer().getExtPlayerInfo("bag");
		
		if(bag.isBagFull())
		{
			target.sendAlert(ErrorCode.ALERT_BAG_IS_FULL);
			return;
		}
		bag.sendAddGoods(target, cg.getGoods());

		removeToCenter(cg);
		onAddOrRemove(cg, false);
		
		
		buffer = new ByteBuffer(16);
		buffer.writeByte(CANCEL);
		buffer.writeUTF(objectIndex+"");
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_SHOPCENTER_COMMAND,buffer));
		
		target.centerGoodsCount--;
		
		
		
		
		StringBuffer lineBuffer = new StringBuffer();
		
		lineBuffer.append(DC.getString(DC.SHOPCENTER_1));//取消寄卖
		lineBuffer.append("\t");
		lineBuffer.append(cg.getAccountName()+":"+cg.getName());
		lineBuffer.append("\t");
		lineBuffer.append(cg.getGoods() != null?cg.getGoods().name+":"+cg.getGoods().goodsCount+":"+cg.getPoint():null);
		lineBuffer.append("\t");
		lineBuffer.append(Utils.getCurrentTime() +" on");
		lineBuffer.append("\n");
		
		
		//记录寄卖中心信息
		GameServer.getInstance().getJobsObserver().addJob(GameServer.JOB_GAME2, new ShopCenterInfoJob(lineBuffer.toString()));
	}

	private void getGoodsFromCenter(PlayerController target, ByteBuffer buffer)
	{
		long objectIndex = 0;
		
		try
		{
			objectIndex = Long.parseLong(buffer.readUTF());//流水号
		}
		catch(NumberFormatException e)
		{
			System.out.println("NumberFormatException ShopCenter getGoodsFromCenter");
			return;
		}
		
		
		CenterGoods cg = getCenterGoodsByObjectIndex(objectIndex);
		
		if(cg == null)
		{
			buffer = new ByteBuffer(16);
			buffer.writeByte(GOODS_NOHAS);
			buffer.writeUTF(objectIndex+"");
			target.getNetConnection().sendMessage(new SMsg(SMsg.S_SHOPCENTER_COMMAND,buffer));
			return;
		}
		
		if(cg.getGoods() == null)
		{
			System.out.println("cg.getGoods() == null");
			return;
		}
		
		Bag bag = (Bag)target.getPlayer().getExtPlayerInfo("bag");
		
		if(bag.point < cg.getPoint())
		{
			target.sendAlert(ErrorCode.ALERT_SHOP_NO_POINT);
			return;
		}
		
		if(bag.isBagFull())
		{
			target.sendAlert(ErrorCode.ALERT_BAG_IS_FULL);
			return;
		}
		
		bag.sendAddGoods(target, cg.getGoods());
		bag.addMoney(target, -cg.getPoint(),0);
		
		onAddOrRemove(cg, false);
		
		removeToCenter(cg);
		
		
		buffer = new ByteBuffer(16);
		buffer.writeByte(GET);
		buffer.writeUTF(objectIndex+"");
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_SHOPCENTER_COMMAND,buffer));
		
		
		WorldManager world = target.getWorldManager();

		Player player = world.getPlayerByLoginState(cg.getAccountName());
		
		int r = cg.getRevenue()/2;
		r = r == 0 ? 1 : r;
		
		Mail mail = new Mail(DC.getString(DC.SHOPCENTER_2));//寄卖系统
		mail.setTitle(DC.getString(DC.SHOPCENTER_3));//卖出确认
		StringBuffer sb = new StringBuffer();
		sb.append(DC.getString(DC.SHOPCENTER_4));
		sb.append(" ");
		sb.append(cg.getGoods().name);
		sb.append(" ");
		sb.append(DC.getString(DC.SHOPCENTER_5));
		mail.setContent(sb.toString());
		mail.setPoint(cg.getPoint()+r);
		
		
		PlayerController seller = null;
		if(player == null)
		{
			seller = world.getPlayer(cg.getName());
			
			if(seller == null)
			{
				mail.sendOffLineWithAccountName(cg.getAccountName());
			}
			else
			{
				mail.send(seller);
			}
		}
		else
		{
			MailBox mb = (MailBox)player.getExtPlayerInfo("mailbox");
			mb.addMail(new PlayerController(null), mail);
		}

		StringBuffer lineBuffer = new StringBuffer();
		
		lineBuffer.append(DC.getString(DC.SHOPCENTER_6));//购买返还
		lineBuffer.append("\t");
		lineBuffer.append(cg.getAccountName()+":"+cg.getName());
		lineBuffer.append("\t");
		lineBuffer.append(cg.getGoods() != null?cg.getGoods().name+":"+cg.getGoods().goodsCount+":"+cg.getPoint():null);
		lineBuffer.append("\t");
		lineBuffer.append(Utils.getCurrentTime() +(seller==null?" off":" on"));		
		lineBuffer.append("\n");
		
		
		//记录寄卖中心信息
		GameServer.getInstance().getJobsObserver().addJob(GameServer.JOB_GAME2, new ShopCenterInfoJob(lineBuffer.toString()));
	
		GameServer.getInstance().getWorldManager().getJobObserver().addJob(GameServer.JOB_DATABASEJOB,
				new SaveJob(GameServer.getInstance().getWorldManager(),target.getPlayer(),SaveJob.SHOP_CENTER_SAVE));
	}

	private void addGoodsToCenter(PlayerController target, ByteBuffer buffer)
	{
		
		if(target.centerGoodsCount >= PlayerController.TOTALCENTERGOODSCOUNT)
		{
			target.sendAlert(ErrorCode.ALERT_PLAYER_SHOPCENTER_FULL);
			return;
		}
		target.centerGoodsCount++;

		long objectIndex = 0;
		
		try
		{
			objectIndex = Long.parseLong(buffer.readUTF());//流水号
		}
		catch(NumberFormatException e)
		{
			System.out.println("NumberFormatException ShopCenter addGoodsToCenter");
			return;
		}
		
		int inputPoint = buffer.readInt(); //金钱
		int timeHour = buffer.readByte(); //小时 24 48
		
		if(timeHour != 12 || inputPoint < 0)
		{
			if(timeHour != 24)
				return;
		}

		Bag bag = (Bag)target.getPlayer().getExtPlayerInfo("bag");

		Goods goods= bag.getGoodsByObjectIndex(objectIndex);

		if(goods == null)
		{
			return;
		}
		
		int count = goods.goodsCount;
		
		double l = timeHour == 12 ? 0.05:0.08;
		

		int p3 = (int) (goods.point * 0.3);
		int revenue = (int) (p3* 3 * l*(double)count); 
		revenue = revenue == 0 ? 1 : revenue;
		
		//System.out.println(p3 +"  " +revenue);

		if(bag.point < revenue)
		{
			target.sendAlert(ErrorCode.ALERT_SHOP_NO_POINT);
			return;
		}
		
		if(!bag.removeGoods(target, objectIndex, count))
			return;
		
		goods.goodsCount = count;//重新设定数量
		
		bag.addMoney(target, -revenue, 0);
		
		CenterGoods cg = new CenterGoods();
		cg.setGoods(goods);
		cg.setPlayer(target);
		cg.setPoint(inputPoint);
		cg.setTimeHour((byte)(timeHour-1));
		cg.setTimer(WorldManager.currentTime);
		cg.setRevenue(revenue);
		 
		addToCenter(cg);
		
		onAddOrRemove(cg,true);

		buffer = new ByteBuffer(1);
		buffer.writeByte(ADD);
		target.getNetConnection().sendMessage(new SMsg(SMsg.S_SHOPCENTER_COMMAND,buffer));
		
		
		
		StringBuffer lineBuffer = new StringBuffer();
		
		lineBuffer.append(DC.getString(DC.SHOPCENTER_7));//开始寄卖
		lineBuffer.append("\t");
		lineBuffer.append(cg.getAccountName()+":"+cg.getName());
		lineBuffer.append("\t");
		lineBuffer.append(cg.getGoods() != null?cg.getGoods().name+":"+cg.getGoods().goodsCount+":"+cg.getPoint():null);
		lineBuffer.append("\t");
		lineBuffer.append(Utils.getCurrentTime() +" on");
		lineBuffer.append("\n");
		
		
		//记录寄卖中心信息
		GameServer.getInstance().getJobsObserver().addJob(GameServer.JOB_GAME2, new ShopCenterInfoJob(lineBuffer.toString()));
	
		GameServer.getInstance().getWorldManager().getJobObserver().addJob(GameServer.JOB_DATABASEJOB,
				new SaveJob(GameServer.getInstance().getWorldManager(),target.getPlayer(),SaveJob.SHOP_CENTER_SAVE));
	}

	public void addToCenter(CenterGoods cg)
	{
		synchronized(centerGoods)
		{
			centerGoods.add(cg);
		}
	}
	
	public void removeToCenter(CenterGoods cg)
	{
		synchronized(centerGoods)
		{
			centerGoods.remove(cg);
		}
	}
	

	/**
	 * 增加或者减少单个的总数量
	 * @param goods
	 * @param b
	 */
	private void onAddOrRemove(CenterGoods cg,boolean b)
	{
		Goods goods = cg.getGoods();

		if(goods.type == 1)
		{
			if(goods instanceof GoodsEquip)
			{
				GoodsEquip ge = (GoodsEquip)goods;
				
				/*0：通用1：士兵 2：运动员 3：医护人员 4：超能力者5：军官 6：特种兵 7：足球运动员 8： 篮球运动员9：医生10：护士11：超人12：赌圣*/
				String jobs [] = Utils.split(ge.job, ":");
			
				//武器0，头盔1，上衣2，裤子3，鞋4，护腕5，手套6，饰品7（项链，戒指，装饰）equipCount
				int equipIndex = 0;
				if(ge.equipLocation >= 7)
					equipIndex = 7;
				else 
					equipIndex = ge.equipLocation;
				
				
				for (int i = 0; i < jobs.length; i++)
				{
					int jobIndex = Integer.parseInt(jobs[i]);
					
					if(b)
						jobsCount[jobIndex][equipIndex]++;
					else
						jobsCount[jobIndex][equipIndex]--;

				}
			}
		}
		else 
		{
			if(b)
				otherCount ++;
			else
				otherCount --;
		}
	}

	/**
	 * 查询客户端
	 * @param target
	 * @param buffer
	 */
	private void centerQuery(PlayerController target, ByteBuffer buffer)
	{
		
		int currentPage = buffer.readInt();//客户端发送页码
		int type = buffer.readByte(); //按类型 -1 则是无类型-2搜索类型 0武器 1防具 2 饰品 3道具

		int size = centerGoods.size();

		if(size <= 0)
			return;
		
		if(type == -1)  // -1
		{
			//System.out.println("收到  "+type+"  页码 "+currentPage);
			
			int totalPage = size/PAGECOUNT;
			totalPage += (size%PAGECOUNT != 0 ?1:0);

			if(currentPage <= 0 || currentPage > totalPage)
				return;
			
			buffer = new ByteBuffer(256);
			buffer.writeByte(BASEQUERY);
			buffer.writeByte(type);
			buffer.writeInt(currentPage);
			buffer.writeInt(totalPage);
			
			if(currentPage == totalPage)  
				buffer.writeByte(size%PAGECOUNT);
			else
				buffer.writeByte(PAGECOUNT);
			
			for (int i = (currentPage-1)*PAGECOUNT,j = i+PAGECOUNT; i < size; i++)
			{
				if(i >= j)
					break;
				
				CenterGoods cg = (CenterGoods)centerGoods.get(i);
				Goods g = cg.getGoods();
				buffer.writeInt(g.id);
				buffer.writeInt(g.iconId);
				buffer.writeUTF(g.name);
				buffer.writeUTF(g.objectIndex+"");
				buffer.writeInt(g.level);
				buffer.writeInt(g.goodsCount);
				buffer.writeUTF(cg.getName());
				buffer.writeInt(cg.getPoint());
				buffer.writeByte(cg.getTimeHour());
			}
		}
		else if(type == -2) //关键字搜索
		{
			int index = buffer.readInt();
			
			if(index < 0 || index >= size)
			{
				index = 0;
				currentPage = 1;
			}
			String keyword = buffer.readUTF().trim();
			
			
			buffer = new ByteBuffer(256);
			
			int i = index;
			int count = 0;
			for ( ; i < size; i++)
			{
				CenterGoods cg = (CenterGoods)centerGoods.get(i);
				Goods g = cg.getGoods();
				
				if(g.name.indexOf(keyword) == -1)
					continue;
				
				buffer.writeInt(g.id);
				buffer.writeInt(g.iconId);
				buffer.writeUTF(g.name);
				buffer.writeUTF(g.objectIndex+"");
				buffer.writeInt(g.level);
				buffer.writeInt(g.goodsCount);
				buffer.writeUTF(cg.getName());
				buffer.writeInt(cg.getPoint());
				buffer.writeByte(cg.getTimeHour());
				
				if(++count >= PAGECOUNT)
				{
					break;
				}
			}
			
			ByteBuffer sendBuffer = new ByteBuffer(buffer.available()+8);
			sendBuffer.writeByte(BASEQUERY);
			sendBuffer.writeByte(type);
			sendBuffer.writeInt(currentPage);
			sendBuffer.writeInt(i+1);  //返回服务器用的index
			sendBuffer.writeByte(count);
			sendBuffer.writeBytes(buffer.getBytes());
			buffer = sendBuffer;
			
		}
		else if(type == 3) // 3道具
		{
			int index = buffer.readInt(); //发送index过来，下次就接着从index开始循环
			
			if(index >= size)
				return;
			
			int blevel = buffer.readInt(); //起始物品等级
			int elevel = buffer.readInt();
			
			
//			System.out.println("收到  "+type+"  页码 "+currentPage+"  ---- 等级区间 " +blevel+"!"+elevel);
			
			int totalPage = otherCount/PAGECOUNT;
			totalPage += (otherCount%PAGECOUNT != 0 ?1:0);
	
			buffer = new ByteBuffer(256);
			
			int count = 0;
			int i = index;
			for ( ; i < size; i++)
			{
				
				CenterGoods cg = (CenterGoods)centerGoods.get(i);
				Goods g = cg.getGoods();
					
				if(g.type == 1)
					continue;
			
				if(g.level == 0 || g.level >= blevel && g.level <= elevel)
				{
					buffer.writeInt(g.id);
					buffer.writeInt(g.iconId);
					buffer.writeUTF(g.name);
					buffer.writeUTF(g.objectIndex+"");
					buffer.writeInt(g.level);
					buffer.writeInt(g.goodsCount);
					buffer.writeUTF(cg.getName());
					buffer.writeInt(cg.getPoint());
					buffer.writeByte(cg.getTimeHour());
					
					if(++count >= PAGECOUNT)
					{
						break;
					}
				}
			} 
			
			ByteBuffer sendBuffer = new ByteBuffer();
			sendBuffer.writeByte(BASEQUERY);
			sendBuffer.writeByte(type);
			sendBuffer.writeInt(currentPage);
			sendBuffer.writeInt(totalPage);
			
			sendBuffer.writeInt(i+1);  //返回服务器用的index

			sendBuffer.writeByte(count);
			sendBuffer.writeBytes(buffer.getBytes());
			buffer = sendBuffer;
		}
		else  // 0武器 1防具 2 饰品 
		{

			int index = buffer.readInt(); //发送index过来，下次就接着从index开始循环

			if(index >= size)
				return;
			
			int blevel = buffer.readInt(); //起始物品等级
			int elevel = buffer.readInt();
			int jobIndex = buffer.readByte();	//0：通用1：士兵 2：运动员 3：医护人员 4：超能力者5：军官 6：特种兵 7：足球运动员 8： 篮球运动员9：医生10：护士11：超人12：赌圣
			int equipIndex = buffer.readByte();  //武器0，头盔1，上衣2，裤子3，鞋4，护腕5，手套6，饰品7（项链，戒指，装饰)
			boolean isIgnoreJob = buffer.readBoolean();// 是否忽略职业
			//System.out.println("收到  "+type+"  页码 "+currentPage+"  ---- 等级区间 " +blevel+"!"+elevel+"  jobIndex "+jobIndex +"  equipIndex "+equipIndex);
			
			if(jobIndex < 0 || jobIndex > JOBSUM-1 || equipIndex < 0 || equipIndex > 7)
			{
				System.out.println(jobIndex + " -- out array -- "+equipIndex);
				return;
			}

			int totalPage = 0;
			if(isIgnoreJob)
			{
				int lessCount = 0;
				for (int j = 0; j < jobsCount.length; j++)
				{
					totalPage += jobsCount[j][equipIndex]/PAGECOUNT;
					lessCount += jobsCount[j][equipIndex]%PAGECOUNT;
				}
				
				totalPage+= lessCount/PAGECOUNT;
				totalPage += (lessCount%PAGECOUNT != 0 ?1:0);
			}
			else
			{
				totalPage = jobsCount[jobIndex][equipIndex]/PAGECOUNT;
				totalPage += (jobsCount[jobIndex][equipIndex]%PAGECOUNT != 0 ?1:0);
			}
			
			
			//System.out.println("查询类型 "+jobIndex+" " +equipIndex +" ----------- "+jobsCount[jobIndex][equipIndex]+" "+totalPage);
			
			buffer = new ByteBuffer(256);
			
			int count = 0;
			int i = index;
			for ( ; i < size; i++)
			{
				
				CenterGoods cg = (CenterGoods)centerGoods.get(i);
				Goods g = cg.getGoods();
				
				if(g.type != 1)
					continue;
				
				
				//g.level//多少级之间的物品
				if(g.level >= blevel && g.level <= elevel)
				{
					if(!(g instanceof GoodsEquip))
						continue;
					
					GoodsEquip ge = (GoodsEquip)g;
					
					String [] jobs = Utils.split(ge.job, ":");
					
					boolean flag = false;
					
					for (int j = 0; j < jobs.length; j++)
					{
						int eJobIndex= Integer.parseInt(jobs[j]);
						
						if(isIgnoreJob)
						{
							int eEquipIndex = 0;
							
							if(ge.equipLocation >= 7)
								eEquipIndex = 7;
							else 
								eEquipIndex = ge.equipLocation;
							
							if(eEquipIndex == equipIndex)
							{
								flag = true;
								break;
							}
						}
						else
						{
							if(eJobIndex == jobIndex)
							{
								int eEquipIndex = 0;
								
								if(ge.equipLocation >= 7)
									eEquipIndex = 7;
								else 
									eEquipIndex = ge.equipLocation;
								
								if(eEquipIndex == equipIndex)
								{
									flag = true;
									break;
								}
							}
						}

					}
					
					if(!flag)
						continue;
					
					buffer.writeInt(g.id);
					buffer.writeInt(g.iconId);
					buffer.writeUTF(g.name);
					buffer.writeUTF(g.objectIndex+"");
					buffer.writeInt(g.level);
					buffer.writeInt(g.goodsCount);
					buffer.writeUTF(cg.getName());
					buffer.writeInt(cg.getPoint());
					buffer.writeByte(cg.getTimeHour());

					if(++count >= PAGECOUNT)
					{
						break;
					}
				}
			}
			
			
			ByteBuffer sendBuffer = new ByteBuffer();
			sendBuffer.writeByte(BASEQUERY);
			sendBuffer.writeByte(type);
			sendBuffer.writeInt(currentPage);
			sendBuffer.writeInt(totalPage);
			
			sendBuffer.writeInt(i+1);  //返回服务器用的index
			
			sendBuffer.writeByte(count);
			sendBuffer.writeBytes(buffer.getBytes());
			buffer = sendBuffer;
		}

		target.getNetConnection().sendMessage(new SMsg(SMsg.S_SHOPCENTER_COMMAND,buffer));
	}

	
	public CenterGoods getCenterGoodsByObjectIndex(long objectIndex)
	{
		for (int i = 0; i < centerGoods.size(); i++)
		{
			CenterGoods cg = (CenterGoods)centerGoods.get(i);
			Goods g = cg.getGoods();
			
			if(g.objectIndex == objectIndex)
				return cg;
		}
		return null;
	}
	
	/**
	 * 是否有物品在寄卖中心
	 * @param target
	 * @return
	 */
	public boolean isGoodsInShopCenter(PlayerController target)
	{
		for (int i = 0; i < centerGoods.size(); i++)
		{
			CenterGoods cg = (CenterGoods)centerGoods.get(i);
			if(cg.getAccountName().equals(target.getPlayer().accountName))
				return true;
		}
		return false;
	}
	
	
	private String cacheName = "";
	
	public void update(long timeMillis)
	{
		cacheName = "";
		Mail.clearCachePlayer();
		StringBuffer lineBuffer = new StringBuffer();
		
		synchronized(centerGoods)
		{
			int size = centerGoods.size();
			
			for (int i = 0; i < size; i++)
			{
				CenterGoods cg = ((CenterGoods)centerGoods.get(i));
				cg.update(timeMillis);
				
				
				if(cg.getTimeHour() <= 0)
				{
					WorldManager world = GameServer.getInstance().getWorldManager();
					
					
					
					//缓存名字不为""检查这个物品是否是缓存名字的
					if(!cacheName.equals(""))
					{
						if(!cacheName.equals(cg.getAccountName()))
						{
							cacheName = "";
						}
					}

					
					Player player = world.getPlayerByLoginState(cg.getAccountName());
					if(player != null)
					{
						cacheName = player.accountName;//寄卖返回时玩家正在登陆寄卖将增加十分钟
						MainFrame.println("SOS:"+DC.getString(DC.SHOPCENTER_8)+": "+player.accountName+" "+cg.getGoods());
					}
					
					if(!cacheName.equals(""))
					{
						//临时增加10分钟
						cg.setTestHour();
						continue;
					}
						
					

					
					
					removeToCenter(cg);
					onAddOrRemove(cg, false);
					i--;
					//centerGoods.remove(i--);
					size--;
					
					
					
					PlayerController seller = world.getPlayer(cg.getName());
					
					
					

					
					//Utils.sleep(20);
					lineBuffer.append(DC.getString(DC.SHOPCENTER_9));//时间返还
					lineBuffer.append("\t");
					lineBuffer.append(cg.getAccountName()+":"+cg.getName());
					lineBuffer.append("\t");
					lineBuffer.append(cg.getGoods() != null?cg.getGoods().name+":"+cg.getGoods().goodsCount+":"+cg.getPoint():null);
					lineBuffer.append("\t");
					

					
					Mail mail = new Mail(DC.getString(DC.SHOPCENTER_2),timeMillis);//寄卖系统
					mail.setTitle(DC.getString(DC.SHOPCENTER_10));//寄卖中心
					mail.setContent(DC.getString(DC.SHOPCENTER_11));//您所寄卖的物品已到期
					mail.addAttach(cg.getGoods());
					//mail.setPoint(cg.getRevenue()); //返回玩家的税收
					
					if(seller == null)
					{
						//mail.sendOffLineWithAccountName(cg.getAccountName());
						mail.sendOffLineCacheWithAccountName(cg.getAccountName());
						lineBuffer.append(Utils.getCurrentTime()+" off");
					}
					else
					{
						mail.send(seller);
						lineBuffer.append(Utils.getCurrentTime()+" on");
					}
					
					lineBuffer.append("\n");
				}
			}
		}
		
		Mail.sendCacheMails();
		
		//记录寄卖中心信息
		GameServer.getInstance().getJobsObserver().addJob(GameServer.JOB_GAME2, new ShopCenterInfoJob(lineBuffer.toString()));
		
	}
}
