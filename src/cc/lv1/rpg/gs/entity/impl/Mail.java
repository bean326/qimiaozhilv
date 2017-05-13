package cc.lv1.rpg.gs.entity.impl;

import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.impl.ArrayList;
import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.dao.DatabaseAccessor;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.RPGameObject;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.ext.MailBox;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsEquip;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsPetEquip;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsProp;
import cc.lv1.rpg.gs.gui.MainFrame;
import cc.lv1.rpg.gs.net.impl.SaveJob;
import cc.lv1.rpg.gs.util.FontConver;

public class Mail extends RPGameObject
{
	
	public static long ONE_DAY_TIME = 1000*60*60*24;
	
	public static long WEK_TIME = ONE_DAY_TIME*7;
	
	public static Player offLineplayer = null;
	
	private Goods attach1;
	
	private Goods attach2;
	
	private String senderName;
	
	/**
	 * 前为*则不可删
	 */
	private String title;
	
	private String content;
	
	private long createTime;
	
	private boolean isRead;
	
	private int point;
	
	private int money;
	
	static
	{
		offLineplayer = new Player();
		offLineplayer.accountName = "";
	}
	
	public Mail(String senderName,long currentTime)
	{
		this.senderName = senderName;
		this.createTime = currentTime;
	}
	
	public Mail(String senderName)
	{
		this(senderName,System.currentTimeMillis());
	}
	
	public Mail()
	{
		this("",System.currentTimeMillis());
	}

	public int getDay(long currentTime)
	{ 
		long endTime = createTime+WEK_TIME;
		if(endTime > currentTime)
		{
			return (int)((endTime - currentTime)  / ONE_DAY_TIME);
		}
		return 0;
	}
	
	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	public Goods getAttach1()
	{
		return attach1;
	}

	public Goods getAttach2()
	{
		return attach2;
	}

	public String getSenderName()
	{
		return senderName;
	}

	public void setSenderName(String senderName)
	{
		senderName = senderName;
		
		this.senderName = senderName;
	}

	public boolean isRead()
	{
		return isRead;
	}

	public void setRead(boolean isRead)
	{
		this.isRead = isRead;
	}
	
	public boolean isHasAttach()
	{
		return (attach1 != null || attach2 != null || point != 0 || money != 0);
	}
	
	public int getPoint()
	{
		return point;
	}

	public void setPoint(int point)
	{
		this.point = point;
	}

	public int getMoney()
	{
		return money;
	}

	public void setMoney(int money)
	{
		this.money = money;
	}

	public long getCreateTime()
	{
		return createTime;
	}

	public boolean addAttach(Goods goods)
	{
		if(attach1 == null)
		{
			attach1 = goods;
			return true;
		}
		else if(attach2 == null)
		{
			attach2 = goods;
			return true;
		}
		return false;
	}
	
	public Goods getAttach(int index)
	{
		if(index == 1)
		{
			return attach1;
		}
		else if(index == 2)
		{
			return attach2;
		}
		return null;
	}
	
	public void loadFrom(ByteBuffer buffer)
	{
		senderName = buffer.readUTF();
		title = buffer.readUTF();
		content = buffer.readUTF();
		createTime = buffer.readLong();
		isRead = buffer.readBoolean();
		
		point = buffer.readInt();
		money = buffer.readInt();
		
		boolean flag1 = buffer.readBoolean();
		boolean flag2 = buffer.readBoolean();
		if(flag1)
		{
			attach1 = load(buffer);
		}
		
		if(flag2)
		{
			attach2 = load(buffer);
		}
	}
	
	private Goods load(ByteBuffer buffer)
	{
		/**
		 * 邮件物品流水
		 */
		int id = buffer.readInt();
		long objectIndex = buffer.readLong();
		boolean useFlag = buffer.readBoolean();
		int goodsCount = buffer.readInt();
		int quality = buffer.readInt();
		int bindMode = buffer.readByte();
		
		Goods goods = (Goods)DataFactory.getInstance().getGameObject(id);
		if(goods == null)
		{
			System.out.println("Mail goodsId error:"+id);
		}
		Goods newGoods = (Goods) Goods.cloneObject(goods);

		newGoods.objectIndex = objectIndex;
		newGoods.useFlag = useFlag;
		newGoods.goodsCount = goodsCount;
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

		return newGoods;
	}

	public void saveTo(ByteBuffer buffer)
	{
		buffer.writeUTF(senderName);
		buffer.writeUTF(title);
		buffer.writeUTF(content);
		buffer.writeLong(createTime);
		buffer.writeBoolean(isRead);
		
		buffer.writeInt(point);
		buffer.writeInt(money);
		
		
		
		/**
		 * 	private Goods attach1;
	
	private Goods attach2;
	

		 */
		boolean flag1 = false,flag2 = false;
		if(attach1 != null)
		{
			flag1 = true;
		}
		if(attach2 != null)
		{
			flag2 = true;
		}
		
		buffer.writeBoolean(flag1);
		buffer.writeBoolean(flag2);
		if(flag1)
		{
			attach1.saveTo(buffer);
		}
		if(flag2)
		{
			attach2.saveTo(buffer);
		}
	}
	
	public void writeTo(ByteBuffer buffer)
	{
		buffer.writeInt(id);
		buffer.writeUTF(senderName);
		buffer.writeUTF(title);
		buffer.writeUTF(content);
		buffer.writeUTF(WorldManager.getTypeTime("MM.dd HH:mm", createTime));
		
		buffer.writeInt(point);
		buffer.writeInt(money);
		
		if(attach1 == null)
		{
			buffer.writeInt(0);
		}
		else
		{
			buffer.writeInt(attach1.id);
			buffer.writeInt(attach1.goodsCount);
			attach1.writeTo(buffer);
		}
		if(attach2 == null)
		{
			buffer.writeInt(0);
		}
		else
		{
			buffer.writeInt(attach2.id);
			buffer.writeInt(attach2.goodsCount);
			attach2.writeTo(buffer);
		}
	}
	
	/**
	 * 给在线玩家发送
	 * @param target
	 */
	public void send(PlayerController target)
	{
		if(target != null)
		{
			MailBox mail = (MailBox)target.getPlayer().getExtPlayerInfo("mailbox");
			mail.addMail(target,this);
		}
	}
	
	public boolean sendOffLineWithAccountId(int id)
	{
		Player player = null;
		
		if(offLineplayer.id == id)
		{
			player = offLineplayer;
		}
		else
		{
			DatabaseAccessor da = GameServer.getInstance().getDatabaseAccessor();
			player = da.getPlayer(id);
			
			if(player != null)
				offLineplayer = player;
		}
		
		if(player == null)
			return false;
		
		return sendOffLine(player);
	}
	
	public boolean sendOffLineWithAccountName(String account)
	{
		Player player = null;
		
		if(offLineplayer.accountName.equals(account))
		{
			player = offLineplayer;
		}
		else
		{
			DatabaseAccessor da = GameServer.getInstance().getDatabaseAccessor();
			player = da.getPlayer(account);
			
			if(player != null)
				offLineplayer = player;
		}
		
		if(player == null)
		{
			MainFrame.println("send off mail fail"+": "+account);//发送离线邮件失败
			return false;
		}
		
		return sendOffLine(player);
	}
	

	public static void clearCachePlayer()
	{
		offLineplayer = new Player();
		offLineplayer.accountName = "";
	}
	
	public boolean sendOffLine(Player player)
	{
/*		PlayerController target = new PlayerController(player);
		
		MailBox mail = (MailBox)target.getPlayer().getExtPlayerInfo("mailbox");

		mail.addMail(target,this);
		
		WorldManager world = GameServer.getInstance().getWorldManager();

		world.getJobObserver().addJob(GameServer.JOB_DATABASEJOB,
				new SaveJob(world,target.getPlayer()));*/
		

		MailBox mail = (MailBox)player.getExtPlayerInfo("mailbox");
		mail.addMail(null,this);
		
		WorldManager world = GameServer.getInstance().getWorldManager();
		world.getJobObserver().addJob(GameServer.JOB_DATABASEJOB,
				new SaveJob(world,player,SaveJob.MAIL_OFFLINE_SAVE));
		
		MainFrame.println(player.accountName+":"+player.name+" / "+mail+"  "+Utils.getCurrentTime());
		return true;
	}

	public void clearAttach()
	{
		attach1 = null;
		attach2 = null;
		point = 0;
		money = 0;
	}

	public int getAttachCount()
	{
		int count = 0;
		
		if(attach1 != null)
			count++;
		
		if(attach2 != null)
			count++;	
			
		return count;
	}
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		if(attach1 != null)
		{
			sb.append(attach1.name+":"+attach1.goodsCount);
		}
		
		if(attach2 != null)
		{
			sb.append(attach2.name+":"+attach2.goodsCount);
		}
		return sb.toString();
	}
	
	
	private static List cachePlayerList = null;
	
	
	public void sendOffLineCacheWithAccountName(String account)
	{
		if(cachePlayerList == null)
			cachePlayerList = new ArrayList();
		
		Player player = null;
		Player currentPlayer = null;
		
		for (int i = 0; i < cachePlayerList.size(); i++)
		{
			player = (Player)cachePlayerList.get(i);
			
			if(player.accountName.equals(account))
			{
				currentPlayer = player;
				break;
			}
		}

		if(currentPlayer == null)
		{
			DatabaseAccessor da = GameServer.getInstance().getDatabaseAccessor();
			currentPlayer = da.getPlayer(account);

			if(currentPlayer == null)
			{
				MainFrame.println(" can not find this player "+account);
				return;
			}
			cachePlayerList.add(currentPlayer);
		}

		MailBox mail = (MailBox)currentPlayer.getExtPlayerInfo("mailbox");
		mail.addMail(null,this);
	}
	
	public static void sendCacheMails()
	{
		if(cachePlayerList == null)
			return;
		
		Player player = null;
		WorldManager world = GameServer.getInstance().getWorldManager();
		
		for (int i = 0; i < cachePlayerList.size(); i++)
		{
			player = (Player)cachePlayerList.get(i);
			
			
			world.getJobObserver().addJob(GameServer.JOB_DATABASEJOB,
					new SaveJob(world,player,SaveJob.MAIL_CACHE_SAVE));
			
			
			MainFrame.println(player.accountName+":"+player.name+" / "+(MailBox)player.getExtPlayerInfo("mailbox")+"  "+Utils.getCurrentTime());
		}
		
		cachePlayerList = null;
	}
	
}
