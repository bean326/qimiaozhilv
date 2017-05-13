package cc.lv1.rpg.gs.entity.impl.goods;

import vin.rabbit.comm.GameObject;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.impl.ArrayList;
import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.data.DC;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.ext.Bag;
import cc.lv1.rpg.gs.entity.ext.OtherExtInfo;
import cc.lv1.rpg.gs.entity.ext.TaskInfo;
import cc.lv1.rpg.gs.entity.impl.Goods;
import cc.lv1.rpg.gs.entity.impl.Mail;
import cc.lv1.rpg.gs.entity.impl.battle.skill.ActiveSkill;
import cc.lv1.rpg.gs.other.ErrorCode;
import cc.lv1.rpg.gs.util.FontConver;

public class GoodsMarry extends Goods 
{
	/** 道具ID(0:离婚规则 其它的表示需要的结婚道具ID) */
	public int goodsId;

	public int[] needTaskId;
	
	public int[] needGoodsId;
	
	public int[] needGoodsCount;
	
	public int needPoint;
	
	public int needMoney;
	
	public int[] manGoodsId;
	
	public int[] manGoodsCount;

	public int[] womenGoodsId;
	
	public int[] womenGoodsCount;
	
	public void copyTo(GameObject gameObject)
	{
		super.copyTo(gameObject);
		
		GoodsMarry marry = (GoodsMarry)gameObject;
		int[] tmp = new int[needTaskId.length];
		System.arraycopy(needTaskId, 0, tmp, 0, tmp.length);
		marry.needTaskId = tmp;
		
		tmp = new int[needGoodsId.length];
		System.arraycopy(needGoodsId, 0, tmp, 0, tmp.length);
		marry.needGoodsId = tmp;
		
		tmp = new int[needGoodsCount.length];
		System.arraycopy(needGoodsCount, 0, tmp, 0, tmp.length);
		marry.needGoodsCount = tmp;
		
		tmp = new int[manGoodsId.length];
		System.arraycopy(manGoodsId, 0, tmp, 0, tmp.length);
		marry.manGoodsId = tmp;
		
		tmp = new int[manGoodsCount.length];
		System.arraycopy(manGoodsCount, 0, tmp, 0, tmp.length);
		marry.manGoodsCount = tmp;
		
		tmp = new int[womenGoodsId.length];
		System.arraycopy(womenGoodsId, 0, tmp, 0, tmp.length);
		marry.womenGoodsId = tmp;
		
		tmp = new int[womenGoodsCount.length];
		System.arraycopy(womenGoodsCount, 0, tmp, 0, tmp.length);
		marry.womenGoodsCount = tmp;
		
		marry.goodsId = goodsId;
		marry.needPoint = needPoint;
		marry.needMoney = needMoney;
	}
	
	/**
	 * 检测玩家是否完成了需要完成的任务
	 * @param target
	 * @return
	 */
	public boolean isTaskFinish(PlayerController target)
	{
		TaskInfo ti = (TaskInfo) target.getPlayer().getExtPlayerInfo("taskInfo");
		
		boolean result = true;
		for (int i = 0; i < needTaskId.length; i++)
		{
			if(needTaskId[i] == 0)
				continue;
			if(!ti.isTaskCompleted(needTaskId[i]))
			{
				result = false;
				break;
			}
		}
		return result;
	}
	
	/**
	 * 检测结婚需要的道具是否都足够
	 * @return true:足够 false:不够
	 */
	public boolean isConEnough(PlayerController target)
	{
		Bag bag = (Bag)target.getPlayer().getExtPlayerInfo("bag");
		for (int i = 0; i < needGoodsId.length; i++)
		{
			if(needGoodsId[i] == 0)
				continue;
			if(bag.getGoodsCount(needGoodsId[i]) < needGoodsCount[i])
				return false;
		}
		return true;
	}
	
	/**
	 * 检测玩家背包
	 * @param target
	 */
	public boolean checkPlayerBag(PlayerController target)
	{
		Bag bag = (Bag)target.getPlayer().getExtPlayerInfo("bag");
		List list = new ArrayList();
		for (int i = 0; i < needGoodsId.length; i++)
		{
			if(needGoodsId[i] == 0)
				continue;
			Goods goods = (Goods) DataFactory.getInstance().getGameObject(needGoodsId[i]);
			Goods newGoods = (Goods) Goods.cloneObject(goods);
			list.add(newGoods);
		}
		if(!(bag.isCanAddGoodsList(list)))
		{
			target.sendAlert(ErrorCode.ALERT_BAG_IS_FULL);
			return false;
		}
		return true;
	}
	
	public String getCancelMarryMailContent(String self,String active,String accept)
	{/**
	因为XXXX提出申请，XXX和XXX的婚姻关系结束了，希望他们以后能找到真正属于自己的幸福！*/
		StringBuffer sb = new StringBuffer();
		sb.append(DC.getString(DC.GOODS_16));//因为
		if(self.equals(active))
			sb.append(DC.getString(DC.GOODS_17));//你
		else if(self.equals(accept))
		{
			sb.append("[");
			sb.append(active);
			sb.append("]");
		}
		sb.append(DC.getString(DC.GOODS_18));//提出申请,你和
		sb.append("[");
		if(self.equals(active))
			sb.append(accept);
		else if(self.equals(accept))
			sb.append(active);
		sb.append("]");
		sb.append(DC.getString(DC.GOODS_19));
		return sb.toString();
	}
	
	public void removeMarryGoods(PlayerController target)
	{
		Bag bag = (Bag) target.getPlayer().getExtPlayerInfo("bag");
		for (int i = 0; i < needGoodsId.length; i++)
		{
			if(needGoodsId[i] == 0)
				continue;
			Goods goods = (Goods) DataFactory.getInstance().getGameObject(needGoodsId[i]);
			if(goods == null)
			{
				System.out.println("GoodsMarry removeMarryGoods goods is null:"+needGoodsId[i]);
				continue;
			}
			if(goods instanceof GoodsProp)
			{
				GoodsProp prop = (GoodsProp) goods;
				if(prop.type == 27)
					continue;
			}
			bag.deleteGoods(target, needGoodsId[i], needGoodsCount[i]);
		}
	}
	
	
	/**
	 * 发送婚姻道具给玩家
	 * @param target
	 * @param list
	 */
	public void sendMarryGoodsToPlayer(PlayerController target)
	{
		OtherExtInfo oei = (OtherExtInfo) target.getPlayer().getExtPlayerInfo("otherExtInfo");
		Mail mail = new Mail(DC.getString(DC.GOODS_20));
		mail.setTitle(DC.getString(DC.GOODS_21));
			
		int[] goodsList = null,goodsListCount = null;
		if(target.getPlayer().sex == 1)
		{
			goodsList = manGoodsId;
			goodsListCount = manGoodsCount;
		}
		else if(target.getPlayer().sex == 0)
		{
			goodsList = womenGoodsId;
			goodsListCount = womenGoodsCount;
		}
		else
			return;
		for (int i = 0; i < goodsList.length; i++)
		{
			if(goodsList[i] == 0)
				continue;
			Goods goods = (Goods) DataFactory.getInstance().getGameObject(goodsList[i]);
			if(goods == null)
			{
				System.out.println("GoodsMarry sendMarryGoodsToPlayer goods is null:"+goodsList[i]);
				continue;
			}
			Goods newGoods = null;
			if(goods instanceof GoodsEquip)
			{
				newGoods = ((GoodsEquip)goods).makeNewBetterEquip(((GoodsEquip) goods).taskColor);
			}
			else
			{
				newGoods = (Goods) Goods.cloneObject(goods);
				newGoods.goodsCount = goodsListCount[i]==0?1:goodsListCount[i];
			}
			newGoods.objectIndex = GameServer.getInstance().getDatabaseAccessor().getGoodsObjIndex();
			if(newGoods instanceof GoodsEquip)
			{
				GoodsEquip equip = (GoodsEquip) newGoods;
				if(equip.eType == 1 && equip.equipLocation == 11)
				{
					if(target.getPlayer().sex == 1)
						equip.name = oei.loverName+DC.getString(DC.GOODS_22);//的老公
					else
						equip.name = oei.loverName+DC.getString(DC.GOODS_23);//的老婆
					equip.setAttStr("name");
				}
			}
			mail.addAttach(newGoods);
			if(mail.getAttachCount() == 2 && i < goodsList.length - 1)
			{	
				mail.send(target);
				mail = new Mail(DC.getString(DC.GOODS_20));
				mail.setTitle(DC.getString(DC.GOODS_21));
			}
			if(i == goodsList.length - 1)
			{
				mail.send(target);
			}
		}
	}
	
	public boolean setVariable(String key,String value)
	{
		if(key.equals("needTaskId"))
		{
			String[] strs = Utils.split(value, ":");
			needTaskId = new int[strs.length];
			for (int i = 0; i < strs.length; i++) 
			{
				needTaskId[i] = Integer.parseInt(strs[i]);
			}
			return true;
		}
		else if(key.equals("needGoodsId"))
		{
			String[] strs = Utils.split(value, ":");
			needGoodsId = new int[strs.length];
			for (int i = 0; i < strs.length; i++) 
			{
				needGoodsId[i] = Integer.parseInt(strs[i]);
				Goods goods = (Goods) DataFactory.getInstance().getGameObject(needGoodsId[i]);
				if(goods == null)
				{
					System.out.println("GoodsMarry needGoodsId is error:"+needGoodsId[i]);
				}
			}
			return true;
		}
		else if(key.equals("needGoodsCount"))
		{
			String[] strs = Utils.split(value, ":");
			needGoodsCount = new int[strs.length];
			for (int i = 0; i < strs.length; i++) 
			{
				needGoodsCount[i] = Integer.parseInt(strs[i]);
			}
			return true;
		}
		else if(key.equals("manGoodsId"))
		{
			String[] strs = Utils.split(value, ":");
			manGoodsId = new int[strs.length];
			for (int i = 0; i < strs.length; i++) 
			{
				manGoodsId[i] = Integer.parseInt(strs[i]);
			}
			return true;
		}
		else if(key.equals("manGoodsCount"))
		{
			String[] strs = Utils.split(value, ":");
			manGoodsCount = new int[strs.length];
			for (int i = 0; i < strs.length; i++) 
			{
				manGoodsCount[i] = Integer.parseInt(strs[i]);
				Goods goods = (Goods) DataFactory.getInstance().getGameObject(manGoodsId[i]);
				if(goods == null)
					continue;
				if(goods.repeatNumber == 1 && manGoodsCount[i] > 1)
				{
					System.out.println("GoodsMarry setVariable 1count error:"+goods.id+"  "+goods.name);
				}
			}
			return true;
		}
		else if(key.equals("womenGoodsId"))
		{
			String[] strs = Utils.split(value, ":");
			womenGoodsId = new int[strs.length];
			for (int i = 0; i < strs.length; i++) 
			{
				womenGoodsId[i] = Integer.parseInt(strs[i]);
			}
			return true;
		}
		else if(key.equals("womenGoodsCount"))
		{
			String[] strs = Utils.split(value, ":");
			womenGoodsCount = new int[strs.length];
			for (int i = 0; i < strs.length; i++) 
			{
				womenGoodsCount[i] = Integer.parseInt(strs[i]);
				Goods goods = (Goods) DataFactory.getInstance().getGameObject(womenGoodsId[i]);
				if(goods == null)
					continue;
				if(goods.repeatNumber == 1 && womenGoodsCount[i] > 1)
				{
					System.out.println("GoodsMarry setVariable 2count error:"+goods.id+"  "+goods.name);
				}
			}
			return true;
		}
		else 
			return super.setVariable(key, value);
	}
	
	@Override
	public void onDeleteImpl(PlayerController target) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onRemoveImpl(PlayerController target) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onUseGoodsBattle(PlayerController player) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onUseImpl(PlayerController target) {
		// TODO Auto-generated method stub
		return false;
	}

}
