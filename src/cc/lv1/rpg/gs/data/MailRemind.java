package cc.lv1.rpg.gs.data;

import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.impl.ArrayList;
import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.entity.RPGameObject;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.impl.Goods;
import cc.lv1.rpg.gs.entity.impl.Mail;
import cc.lv1.rpg.gs.entity.impl.Player;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsEquip;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsPetEquip;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsProp;

/**
 * 邮件提醒
 * @author dxw
 *
 */
public class MailRemind  extends RPGameObject
{
	
	/**
	 * 类型	
		1.玩家等级
		2.宠物等级
		3.任务ID
	 */
	public int type;
	
	/**
	 * 玩家等级/宠物等级/任务id
	 */
	public int condition;
	
	/**
		0无职业限制
		1士兵,
		2运动员
		3医护人员
		4超能力者
		5：军官
	  	6：特种兵
	  	7：足球运动员
	  	8：篮球运动员
	  	9：医生  
	  	10：护士
	  	11：超人
	  	12：赌圣
	 * 
	 */
	public int profession;
	
	/**
	 * 0是无阵营
	 */
	public int camp;
	
	/**
	 * 寄信人
	 */
	public String sender;
	
	/**
	 * 邮件标题
	 */
	public String mailTitle;

	/**
	 * 邮件正文
	 */
	public String mailContent;
	
	/**
	 * 附件物品id
	 */
	public int []attachGoodsId = new int[2];
	
	/**
	 * 附件物品数量
	 */
	public int []attachGoodsCount = new int[2];
	
	/**
	 * 附件金钱 point/money
	 */
	public int []attachPM = new int[2];
	
	public boolean setVariable(String key,String value)
	{
		if(key.equals("attachGoodsId"))
		{
			String []strs = Utils.split(value, ":");
			for (int i = 0; i < strs.length; i++)
			{
				attachGoodsId [i] =  Integer.parseInt(strs[i]);
			}
			return true;
		}
		else if(key.equals("attachGoodsCount"))
		{
			String []strs = Utils.split(value, ":");
			for (int i = 0; i < strs.length; i++)
			{
				attachGoodsCount [i] =  Integer.parseInt(strs[i]);
			}
			return true;
		}
		else if(key.equals("attachPM"))
		{
			String []strs = Utils.split(value, ":");
			for (int i = 0; i < strs.length; i++)
			{
				attachPM [i] =  Integer.parseInt(strs[i]);
			}
			return true;
		}
		else
			return super.setVariable(key, value);
	}
	
	
	public static int ONLEVELUP = 1;
	
	public static int ONPETLEVELUP = 2;
	
	public static int ONTASKOVER = 3;
	
	/**
	 * @param player
	 * @param type 		
	 *  1.玩家等级
		2.宠物等级
		3.任务ID
	 */
	public static void onCheckMail(PlayerController target,int type,int taskId)
	{
		ArrayList list = (ArrayList)DataFactory.getInstance().getAttachment(DataFactory.ATTACH_MAILREMIND);
		
		int size = list.size();
		
		Player player = target.getPlayer();
		
		for (int i = 0; i < size; i++)
		{
			MailRemind me = (MailRemind)list.get(i);
			
			if(type != me.type)
				continue;
			
			if(me.type == ONLEVELUP)
			{
				if(me.condition != player.level)
					continue;
			}
			else if(me.type == ONPETLEVELUP)
			{
/*				PetTome pt = player.getExtPlayerInfo("petTome");
				Pet pet = pt.getActivePet();
				
				if(me.condition != pet.level)
					continue;*/
			}
			else if(me.type == ONTASKOVER)
			{
				if(taskId != me.condition)
					continue;
			}
			
			if(player.camp != me.camp)
				continue;
			
			if(me.profession != 0)
			{
				if(player.upProfession != 0)
				{
					if(player.upProfession != me.profession-4)
						continue;
				}
				else
				{
					if(player.profession != me.profession)
						continue;
				}
			}
			
			

			
			
			Mail mail = new Mail(me.sender);
			mail.setTitle(me.mailTitle);
			mail.setContent(me.mailContent);
			mail.setPoint(me.attachPM[0]);
			mail.setMoney(me.attachPM[1]);
			if(me.attachGoodsId[0]!= 0)
			{
				for (int j = 0; j < me.attachGoodsId.length; j++)
				{
					int id = me.attachGoodsId[j];
					
					if(id == 0)
						continue;

					Goods goods = (Goods)DataFactory.getInstance().getGameObject(id);
					
					if(goods == null)
						continue;

					Goods newGoods = null;
					if(goods instanceof GoodsEquip)
					{
						GoodsEquip equip = (GoodsEquip) goods;
						newGoods = equip.makeNewBetterEquip(equip.quality);
					}
					else if(goods instanceof GoodsProp)
					{
						newGoods = (Goods) Goods.cloneObject(goods);
						newGoods.goodsCount = me.attachGoodsCount[j];
					}
					else if(goods instanceof GoodsPetEquip)
					{
						newGoods = (Goods) Goods.cloneObject(goods);
					}
					else
					{
						System.out.println("MailRemind goodsId error:"+id);
						continue;
					}
					newGoods.objectIndex = GameServer.getInstance().getWorldManager().getDatabaseAccessor().getGoodsObjIndex(); 
					
					mail.addAttach(newGoods);

				}
			}
			mail.send(target);
			
		}
		
	}
	
}
