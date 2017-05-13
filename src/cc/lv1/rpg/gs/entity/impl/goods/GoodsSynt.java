package cc.lv1.rpg.gs.entity.impl.goods;

import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.Utils;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.impl.Goods;

/**
 * 合成物品
 * @author dxw
 *
 */
public class GoodsSynt extends Goods
{

	public int profession[] = new int[3];
	
	public int []materialsIds = new int[5];
	
	public String []materialsName = new String[4];
	
	public int []materialsCount= new int[5];
	
	public int []modValue = new int[3];
	
	public int []rates = new int[6];
	
	/** 
	 * 合成序列号
	 * */
	public int gsId;
	
	/**
	 * 客户端用图片Id
	 */
	public int iconId;
	
	/**
	 * 客户端用难度
	 */
	public int rank;
	
	/**
	 * 客户端用组别
	 */
	public int groups;
	
	/**
	 * 客户端用序列
	 */
	public int sequence;
	
	
	public boolean setVariable(String key,String value)
	{
		if(key.equals("materialsIds"))
		{
			String [] strs = Utils.split(value, ":");
			for(int i = 0 ; i < materialsIds.length ; i ++)
			{
				materialsIds[i] = Integer.parseInt(strs[i]);
			}
			return true;
		}
		else if(key.equals("materialsCount"))
		{
			String [] strs = Utils.split(value, ":");
			for(int i = 0 ; i < materialsCount.length ; i ++)
			{
				materialsCount[i] = Integer.parseInt(strs[i]);
			}
			return true;
		}
		else if(key.equals("materialsName"))
		{
			String [] strs = Utils.split(value, ":");
			for(int i = 0 ; i < materialsName.length ; i ++)
			{
				materialsName[i] = strs[i];
			}
			return true;
		}
		else if(key.equals("profession"))
		{
			String [] strs = Utils.split(value, ":");
			for(int i = 0 ; i < profession.length ; i ++)
			{
				profession[i] = Integer.parseInt(strs[i]);
			}
			return true;
		}
		else if(key.equals("modValue"))
		{
			String [] strs = Utils.split(value, ":");
			for(int i = 0 ; i < modValue.length ; i ++)
			{
				modValue[i] = Integer.parseInt(strs[i]);
			}
			return true;
		}
		else if(key.equals("rates"))
		{
			String [] strs = Utils.split(value, ":");
			for(int i = 0 ; i < rates.length ; i ++)
			{
				rates[i] = Integer.parseInt(strs[i]);
			}
			return true;
		}
		else 
		{
			return super.setVariable(key, value);
		}
	}
	
	
	
	public void onDeleteImpl(PlayerController target)
	{
	}

	public boolean onRemoveImpl(PlayerController target)
	{
		return false;
	}

	public boolean onUseImpl(PlayerController target)
	{
		return false;
	}



	@Override
	public boolean onUseGoodsBattle(PlayerController player) {
		// TODO Auto-generated method stub
		return false;
	}

}
