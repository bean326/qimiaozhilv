package cc.lv1.rpg.gs.entity.impl.answer;

import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.impl.ArrayList;
import cc.lv1.rpg.gs.data.DC;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.RPGameObject;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.impl.Goods;
import cc.lv1.rpg.gs.entity.impl.Mail;

public class Guide extends RPGameObject
{
	public int step;
	
	public int level;
	
	/** 对应NPC的ID */
	public int npcId;
	
	/** 问题ID */
	public int questionId;

	/** 奖励物品(军官物品1:军官物品2|运动员物品1:运动员物品2|护士物品1:护士物品2|超能力物品1:超能力物品2) */
	public String rewardIds;
	
	/** 奖励物品数量(军官数量1:军官数量2|运动员数量1:运动员数量2|护士数量1:护士数量2|超能力数量1:超能力数量2)  */
	public String rewardCounts;
	
	/** 对话ID */
	public int dialogId;
	
	public void sendReward(PlayerController target)
	{
		if(rewardIds == null || rewardCounts == null)
			return;
		String[] goodsStr = Utils.split(rewardIds, "|");
		String[] countStr = Utils.split(rewardCounts, "|");
		if(goodsStr.length != countStr.length)
		{
			System.out.println("Guide id error:"+rewardIds+" count:"+rewardCounts);
			return;
		}
		String[] rewards = null;
		String[] counts = null;
		if(goodsStr.length == 1)
		{
			rewards = Utils.split(goodsStr[0],":");
			counts = Utils.split(countStr[0],":");
		}
		else
		{
			rewards = Utils.split(goodsStr[target.getPlayer().profession-1],":");
			counts = Utils.split(countStr[target.getPlayer().profession-1],":");
		}
		List list = new ArrayList();
		for (int i = 0; i < rewards.length; i++)
		{
			try
			{
				Goods[] goodsList = DataFactory.getInstance().makeGoods(Integer.parseInt(rewards[i]), Integer.parseInt(counts[i]));
				for (int j = 0; j < goodsList.length; j++)
				{
					if(goodsList[j] != null)
						list.add(goodsList[j]);
				}
			}catch (Exception e) 
			{
				System.out.println("Guide sendReward goodsId error:"+rewards[i]);
				continue;
			}
		}
		Mail mail = new Mail(DC.getString(DC.PLAYER_68));
		mail.setTitle(DC.getString(DC.PLAYER_69));
		mail.setContent(DC.getString(DC.PLAYER_69));
		int size = list.size();
		for (int i = 0; i < size; i++)
		{
			Goods goods = (Goods) list.get(i);
			mail.addAttach(goods);
			if(mail.getAttachCount() == 2)
			{
				mail.send(target);
				if(i != size-1)
				{
					mail = new Mail(DC.getString(DC.PLAYER_68));
					mail.setTitle(DC.getString(DC.PLAYER_69));
					mail.setContent(DC.getString(DC.PLAYER_69));
				}
			}
			else
			{
				if(i == size-1)
					mail.send(target);
			}
		}
	}
}
