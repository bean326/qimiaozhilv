package cc.lv1.rpg.gs.entity.ext;

import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.impl.Goods;
import cc.lv1.rpg.gs.entity.impl.battle.skill.Skill;
import vin.rabbit.comm.GameObject;
import vin.rabbit.util.ByteBuffer;
/**
 * 玩家的设置，包括声音效果的调整，快捷栏等等
 * @author dxw
 *
 */
public class PlayerSetting extends PlayerExtInfo
{
	private GameObject[] objects = new GameObject[10];//快捷栏中物品，从1到9的位置,0没有用
	
	public PlayerSetting()
	{
		
	}
	
	@Override
	public String getName()
	{
		return "playerSetting";
	}

	@Override
	public void loadFrom(ByteBuffer buffer)
	{
		int len = buffer.readInt();
		for (int i = 0; i < len; i++) 
		{
			int location = buffer.readInt();
			int id = buffer.readInt();
			int count = buffer.readInt();
			
			Object obj = DataFactory.getInstance().getGameObject(id);
			if(obj instanceof Goods)
			{
				Goods goods = (Goods) obj;
				goods = (Goods) Goods.cloneObject(goods);
				goods.goodsCount = count;
				objects[location] = goods;
			}
			else if(obj instanceof Skill)
			{
				Skill skill = (Skill) obj;
				skill = (Skill) Skill.cloneObject(skill);
				objects[location] = skill;
			}
		}

	}

	@Override
	public void saveTo(ByteBuffer buffer)
	{
		int count = 0,i = 0;
		for (i = 0; i < objects.length; i++)
		{
			if(objects[i] != null)
				count++;
		}
		buffer.writeInt(count);
		for (i = 0; i < objects.length; i++)
		{
			if(objects[i] != null)
			{
				buffer.writeInt(i);
				buffer.writeInt(objects[i].id);
				if(objects[i] instanceof Goods)
					buffer.writeInt(((Goods)objects[i]).goodsCount);
				else if(objects[i] instanceof Skill)
					buffer.writeInt(0);
			}
		}
	}
	
	public void writeTo(ByteBuffer buffer)
	{
		int count = 0,i = 0;
		for (i = 0; i < objects.length; i++)
		{
			if(objects[i] != null)
			{
				count++;
			}
		}
		buffer.writeInt(count);
		for (i = 0; i < objects.length; i++)
		{
			if(objects[i] != null)
			{
				buffer.writeInt(i);
				buffer.writeInt(objects[i].id);
				if(objects[i] instanceof Goods)
				{	
					buffer.writeInt(1);
					buffer.writeInt(((Goods)objects[i]).goodsCount);
				}
				else if(objects[i] instanceof Skill)
				{
					buffer.writeInt(0);
					buffer.writeInt(0);
				}
			}
		}
		
	}
	
	
	
	
	/**
	 * 删除快捷方式 
	 * @param location
	 */
	public void deletePlayerBar(int location)
	{
		objects[location] = null;
	}
	
	/**
	 * 添加快捷方式 
	 * @param location
	 * @param goods
	 */
	public void addPlayerBar(Object obj,int count,int location)
	{
		if(obj instanceof Goods)
		{
			Goods goods = (Goods) obj;
			goods = (Goods) Goods.cloneObject(goods);
			goods.goodsCount = count;
			objects[location] = goods;
		}
		else if(obj instanceof Skill)
		{
			Skill skill = (Skill) obj;
			skill = (Skill) Skill.cloneObject(skill);
			objects[location] = skill;
		}
	}
	
	
	public void addPlayerBar(int location,int id,int count)
	{
		Object obj = DataFactory.getInstance().getGameObject(id);
		if(obj instanceof Goods)
		{
			Goods goods = (Goods) obj;
			goods = (Goods) Goods.cloneObject(goods);
			goods.goodsCount = count;
			objects[location] = goods;
		}
		else if(obj instanceof Skill)
		{
			Skill skill = (Skill) obj;
			skill = (Skill) Skill.cloneObject(skill);
			objects[location] = skill;
		}
	}

	/**
	 * 更新快捷栏
	 * @param goods
	 */
	public void updatePlayerBar(int id,int count)
	{
		for (int i = 0; i < objects.length; i++) 
		{
			if(objects[i] != null)
			{
				if(objects[i].id == id)
				{
					if(objects[i] instanceof Goods)
					{
						if(count <= 0)
							objects[i] = null;
						else
							((Goods)objects[i]).goodsCount = count;
					}	
					break;
				}
			}
		}
	}
	
	/**
	 * 更新快捷栏(技能) 
	 * @param iconId
	 */
	public void updatePlayerBar(Skill sk)
	{
		for (int i = 0; i < objects.length; i++) 
		{
			if(objects[i] != null)
			{
				if(objects[i] instanceof Skill)
				{
					Skill skill = (Skill) objects[i];
					if(skill.iconId == sk.iconId)
					{
						objects[i] = sk;
					}
				}
			}
		}
	}

	
//	public void removeSkill()
//	{
//		for (int i = 0; i < objects.length; i++) 
//		{
//			if(objects[i] != null)
//			{
//				if(objects[i] instanceof Skill)
//				{
//					objects[i] = null;
//				}
//			}
//		}
//	}
	
	public Object[] getObjects()
	{
		return objects;
	}
}
