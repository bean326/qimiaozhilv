package cc.lv1.rpg.gs.entity;

import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.Utils;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.impl.Goods;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsEquip;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsPetEquip;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsProp;

public class CenterGoods implements vin.rabbit.comm.i.IDatabaseHandle
{
	
	private final int HOUR = 1000*60*60;
	
	/**
	 * 持有的主人名字
	 */
	private String name;
	
	/**
	 * 持有的主人的id
	 */
	private int id;
	
	
	/**
	 * 持有的主人的accountName
	 */
	private String accountName;
	
	/**
	 * 所卖价钱
	 */
	private int point;
	
	/**
	 * 剩余小时数
	 */
	private byte timeHour = 24;
	
	/**
	 * 计时器
	 */
	private long timer;
	
	/**
	 * 卖的装备道具饰品
	 */
	private Goods goods;
	
	/**
	 * 税收
	 */
	private int revenue;


	public int getRevenue()
	{
		return revenue;
	}

	public void setRevenue(int revenue)
	{
		this.revenue = revenue;
	}

	public void setTimeHour(byte timeHour)
	{
		this.timeHour = timeHour;
	}

	public byte getTimeHour()
	{
		return timeHour;
	}

	public long getTimer()
	{
		return timer;
	}

	public void setTimer(long timer)
	{
		this.timer = timer;
	}

	public Goods getGoods()
	{
		return goods;
	}

	public void setGoods(Goods goods)
	{
		this.goods = goods;
	}
	
	public String getName()
	{
		return name;
	}

	public int getId()
	{
		return id;
	}

	public String getAccountName()
	{
		return accountName;
	}
	
	public void setAccountName(String account)
	{
		this.accountName = account;
	}
	
	public void setPlayer(PlayerController target)
	{
		this.id = target.getID();
		this.name = target.getName();
		this.accountName = target.getPlayer().accountName;
	}

	public int getPoint()
	{
		return point;
	}

	public void setPoint(int point)
	{
		this.point = point;
	}

	public void loadFrom(ByteBuffer buffer)
	{
		id = buffer.readInt();
		accountName = buffer.readUTF();
		name = buffer.readUTF();
		point = buffer.readInt();
		timeHour = (byte) buffer.readByte();
		revenue = buffer.readInt();
		
		int id = buffer.readInt();
		long objectIndex = buffer.readLong();
		int goodsCount = buffer.readInt();
		int quality = buffer.readInt();

		Goods newGoods = (Goods)DataFactory.getInstance().getGameObject(id);
		if(newGoods == null)
		{
			System.out.println("CenterGoods goodsId error:"+id);
		}
		goods = (Goods) Goods.cloneObject(newGoods);
		
		goods.objectIndex = objectIndex;
		goods.goodsCount = goodsCount;
		goods.id = id;
		goods.quality = quality;
		if(goods instanceof GoodsEquip)
		{
			String extAtt = buffer.readUTF();
			((GoodsEquip)goods).extAtt = new StringBuffer(extAtt);
			String attStr = buffer.readUTF();
			((GoodsEquip)goods).attStr = attStr;
			if(!attStr.isEmpty())
			{
				String[] str = Utils.split(attStr, ":");
				for (int j = 0; j < str.length; j++) 
				{
					goods.setVariable(str[j],buffer.readUTF());
				}
			}
			((GoodsEquip)goods).setDefaultAtt();
			
			((GoodsEquip)goods).updateEquipChange();
		}
		else if(goods instanceof GoodsPetEquip)
		{
			((GoodsPetEquip)goods).setExtAtt(buffer.readUTF());
		}
		else if(goods instanceof GoodsProp)
		{
			((GoodsProp)goods).expPoint = buffer.readLong();
		}
		
	}

	public void saveTo(ByteBuffer buffer)
	{
		buffer.writeInt(id);
		buffer.writeUTF(accountName);
		buffer.writeUTF(name);
		buffer.writeInt(point);
		buffer.writeByte(timeHour);
		buffer.writeInt(revenue);
		
		buffer.writeInt(goods.id);
		buffer.writeLong(goods.objectIndex);
		buffer.writeInt(goods.goodsCount);
		buffer.writeInt(goods.quality);
		
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
	
	
	public void update(long timeMillis)
	{
		if(timeHour <= 0)
			return;
		
		if(timer + HOUR > timeMillis)
			return;
		
		timer = timeMillis;
		timeHour --;
		
	}

	/**
	 * 设置10分钟
	 */
	public void setTestHour()
	{
		timeHour = 1;
		timer = System.currentTimeMillis()-HOUR+(1000*60*1);
	}
}
