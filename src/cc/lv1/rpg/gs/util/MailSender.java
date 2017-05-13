package cc.lv1.rpg.gs.util;

import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.impl.ArrayList;
import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.impl.Goods;
import cc.lv1.rpg.gs.entity.impl.Mail;


public class MailSender
{

	private StringBuffer buffer = new StringBuffer(102400);
	
	public MailSender()
	{
		GameServer.getInstance().initialWithoutNetServer();
		
		buffer.append("id");
		buffer.append("\t");
		buffer.append("accountName");
		buffer.append("\t");
		buffer.append("point");
		buffer.append("\t");
		buffer.append("money");
		buffer.append("\t");
		buffer.append("goods1");
		buffer.append("\t");
		buffer.append("goods2");
		buffer.append(Utils.LINE_SEPARATOR);
		
		buffer.append("\u5E8F\u5217\u53F7");//序列号
		buffer.append("\t");
		buffer.append("\u8D26\u6237\u540D");//账户名
		buffer.append("\t");
		buffer.append("\u6E38\u620F\u5E01");//游戏币
		buffer.append("\t");
		buffer.append("\u5143\u5B9D");//元宝
		buffer.append("\t");
		buffer.append("\u9053\u51771");//道具1
		buffer.append("\t");
		buffer.append("\u9053\u51772");//道具2
		buffer.append(Utils.LINE_SEPARATOR);
	}
	
	public void run()
	{
		String path = GameServer.getAbsolutePath()+"MailSender.txt";
		try
		{
			ArrayList mailPos = Utils.loadFileVariables(path, MailPO.class);
			
			System.out.println("mail list_size : "+mailPos.size());
			long time = System.currentTimeMillis();
			int count = 0;
			int nCount = 0;
			for (int i = 0; i < mailPos.size(); i++)
			{
				MailPO po  = (MailPO)mailPos.get(i);
				
				
				
				Mail mail = new Mail("\u6E38\u620F\u7BA1\u7406\u5458",time); //游戏管理员
				mail.setTitle("\u7CFB\u7EDF\u90AE\u4EF6");//系统邮件
				
				if(po.goodsId1 != 0 && po.goodsCount1 != 0)
				{
					Goods [] goods = DataFactory.getInstance().makeGoods(po.goodsId1, po.goodsCount1);
					if(goods != null)
					{
						mail.addAttach(goods[0]);
					}
				}
				if(po.goodsId2 != 0 && po.goodsCount2 != 0)
				{
					Goods [] goods = DataFactory.getInstance().makeGoods(po.goodsId2, po.goodsCount2);
					if(goods != null)
					{
						mail.addAttach(goods[0]);
					}
				}
				mail.setPoint(po.point);
				mail.setMoney(po.money);
				
				if(!mail.sendOffLineWithAccountName(po.accountName))
				{
					
					buffer.append(po.id+"");
					buffer.append("\t");
					buffer.append(po.accountName);
					buffer.append("\t");
					buffer.append(po.point+"");
					buffer.append("\t");
					buffer.append(po.money+"");
					buffer.append("\t");
					buffer.append(po.goodsId1+":"+po.goodsCount1);
					buffer.append("\t");
					buffer.append(po.goodsId2+":"+po.goodsCount2);
					buffer.append(Utils.LINE_SEPARATOR);
					nCount ++;
					continue;
				}
				
				System.out.println("send id : "+po.id+" accountName : " +po.accountName +" money : "+po.money);
				Utils.sleep(5);
				count++;
			}
			System.out.println("all total : "+mailPos.size()+"send total :"+count +"  --  no send total  :"+nCount);
			Utils.writeFile("MailSender_Last.txt", buffer.toString().getBytes());
		} 
		catch (InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		
		
	}
	
	public static void main(String[] args)
	{
		MailSender sender = new MailSender();
		sender.run();
	}
}
