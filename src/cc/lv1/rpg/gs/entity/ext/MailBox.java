package cc.lv1.rpg.gs.entity.ext;

import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.impl.ArrayList;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.data.DC;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.impl.Mail;
import cc.lv1.rpg.gs.gui.MainFrame;
import cc.lv1.rpg.gs.net.SMsg;
import cc.lv1.rpg.gs.other.ErrorCode;
import cc.lv1.rpg.gs.util.FontConver;

public class MailBox extends PlayerExtInfo
{
	
	
	public static final int QUERY = 4;
	
	public static final int QUERYMAIL = 5;
	
	public static final int ADD = 1;
	
	public static final int REMOVE = 2;
	
	public static final int GETATTACH = 3;

	public static final int MAX_MAILBOX_SIZE = 100;
	
	public ArrayList mailList = new ArrayList(100);
	
	public String getName()
	{
		return "mailbox";
	}

	public void loadFrom(ByteBuffer buffer)
	{
		int size = buffer.readInt();
		
		for (int i = 0; i < size; i++)
		{
			Mail mail = new Mail();
			mail.loadFrom(buffer);
			mailList.add(mail);
		}

	}

	public void saveTo(ByteBuffer buffer)
	{
		int size = mailList.size();
		buffer.writeInt(size);
		
		for (int i = 0; i < size; i++)
		{
			Mail mail = (Mail)mailList.get(i);
			mail.saveTo(buffer);
		}
	}
	
	public boolean isFull()
	{
		return mailList.size() >= MAX_MAILBOX_SIZE;
	}
	
	public int size()
	{
		return mailList.size();
	}

	public void addMail(PlayerController target, Mail mail)
	{
		
		if(isFull())
		{
			if(target != null && target.getNetConnection() != null)
					target.sendAlert(ErrorCode.ALERT_MAILBOX_FULL_CLEARSOON);

			boolean isRemoved = false;
			
			int size = mailList.size();
			for (int i = 0; i < size; i++)
			{
				Mail rm = (Mail)mailList.get(i);
				
				if(rm == null)
					continue;
				
				if(!rm.isRead()) //优先排除未读邮件
					continue;

				if(rm.getTitle().startsWith("*")) //优先排除不能删邮件
					continue;

				if(rm.getAttach(1) != null || rm.getAttach(2) != null
						|| rm.getPoint() != 0 || rm.getMoney() != 0) //附件未提取的优先作为删除对象
					continue;

				mailList.remove(i);
				isRemoved = true;
				break;	
			}
			
			if(!isRemoved)
				mailList.remove(0);
			
			mailList.add(mail);

			sort();
		}
		else
		{
			mailList.add(mail);
			
			if(mailList.size() > MAX_MAILBOX_SIZE - 10)
			{
				if(target != null  && target.getNetConnection() != null)
					target.sendError(DC.getString(DC.PLAYER_71));
			}
		}
		
	
		//加入邮件后通知客户端
		if(target != null  && target.getNetConnection() != null)
		{
			ByteBuffer buffer = new ByteBuffer(1);
			buffer.writeByte(MailBox.ADD);
			target.getNetConnection().sendMessage(new SMsg(SMsg.S_PLAYER_MAIL_COMMAND,buffer));
		}
	}
	
	
	public void writeTo(ByteBuffer buffer)
	{
		int size = mailList.size();
		
		
		long currentTime = 0;
		
		if(size != 0)
		{
			currentTime = WorldManager.currentTime;//System.currentTimeMillis();
			if(currentTime == 0)
				currentTime = System.currentTimeMillis();
		}
		
		buffer.writeInt(size);
		
		long tempTime = 0;
		
		for (int i = 0; i < size; i++)
		{
			if(i > MAX_MAILBOX_SIZE+10)
			{
				break;
			}
			
			Mail mail = (Mail)mailList.get(i);
			
			if(mail == null)
			{
				continue;
			}
				
			if(tempTime == 0)
			{
				tempTime = mail.getCreateTime();
			}
			else
			{
				if(mail.getCreateTime() < tempTime)
				{
/*					sort();
					break;*/
					if(sort()) 
					{
						buffer.clear();
						buffer.writeByte(MailBox.QUERY);
						buffer.writeInt(size);
						i = -1;
						tempTime = 0;
						continue;
					}
				}
				else
				{
					tempTime = mail.getCreateTime();
				}
			}

			mail.id = i;
			buffer.writeInt(mail.id);
			buffer.writeUTF(mail.getSenderName());
			buffer.writeUTF(mail.getTitle());

			int day = mail.getDay(currentTime);

			if(day <= 0)
			{
				mailList.remove(i);
				size = mailList.size();
				
				buffer.clear();
				buffer.writeByte(MailBox.QUERY);
				buffer.writeInt(size);
				i = -1;
				tempTime = 0;
				continue;
			}

			buffer.writeInt(day);
			buffer.writeBoolean(mail.isRead());
			buffer.writeBoolean(mail.isHasAttach());
			

		}
		
	}
	
	/**
	 * 发送GM后台邮件查询
	 * @param buffer
	 */
	public void writeWithGm(ByteBuffer buffer)
	{
		int size = mailList.size();
		
		long currentTime = 0;
		
		if(size != 0)
			currentTime = System.currentTimeMillis();

		
		buffer.writeInt(size);
		

		for (int i = 0; i < size; i++)
		{

			Mail mail = (Mail)mailList.get(i);
			
			if(mail == null)
			{
				continue;
			}

			mail.id = i;
			buffer.writeInt(mail.id);
			buffer.writeUTF(mail.getSenderName());
			buffer.writeUTF(mail.getTitle());
			int day = mail.getDay(currentTime);
			buffer.writeInt(day);
			buffer.writeBoolean(mail.isRead());
			buffer.writeBoolean(mail.isHasAttach());
		}
	}
	

	
	private void checkIsSort()
	{
		int size = mailList.size();
		long tempTime = 0;
		
		for (int i = 0; i < size; i++)
		{
			Mail mail = (Mail)mailList.get(i);
			
			if(mail == null)
			{
				continue;
			}
				
			if(tempTime == 0)
			{
				tempTime = mail.getCreateTime();
			}
			else
			{
				if(mail.getCreateTime() < tempTime)
				{
					sort();
					break;
				}
				else
				{
					tempTime = mail.getCreateTime();
				}
			}
		}
	}
	
	private boolean sort()
	{
		
		Object [] objs = mailList.toArray();
		
		for (int i = 0; i < objs.length; i++)
		{
		   for(int j = objs.length-1 ; j > i ; j --)
		   {
			   if(((Mail)objs[j-1]).getCreateTime() > ((Mail)objs[j]).getCreateTime())
			   {
				   Object tmp = objs[j];
				   objs[j] = objs[j-1];
				   objs[j-1] = tmp;
			   }
		   }
		}

		mailList = new ArrayList(100);
		for (int i = 0; i < objs.length; i++)
		{
			mailList.add(objs[i]);
		}
		return true;
	}

	public Mail getMail(int id)
	{
		for (int i = 0; i < mailList.size(); i++)
		{
			Mail mail = (Mail)mailList.get(i);
			
			if(mail == null)
				continue;
			
			if(mail.id == id)
				return mail;
		}
		return null;
	}
	
	public void removeMails(List removeList)
	{
		int size = removeList.size();
		
		for (int i = 0; i < size; i++)
		{
			Integer id = (Integer)removeList.get(i);
			
			if(id == null)
				continue;
			
			removeMails(id);
		}
	}
	
	
	public void removeMails(int id)
	{
		int size = mailList.size();
		
		for (int i = 0; i < size; i++)
		{
			Mail mail = (Mail)mailList.get(i);
			
			if(mail == null)
				continue;
			
			if(mail.id == id)
			{
				mailList.remove(i);
				break;
			}
		}
		
	}

	public boolean isHaveNewMail()
	{
		int size = mailList.size();
		
		for (int i = 0; i < size; i++)
		{
			Mail mail = (Mail)mailList.get(i);
			
			if(mail == null)
				continue;
			
			if(!mail.isRead())
			{
				return true;
			}
		}
		
		return false;
	}
	
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		
		int size = mailList.size();
		
		sb.append("Size : ");
		sb.append(size);
		sb.append(" |");
		
		for (int i = 0; i < size; i++)
		{
			Mail mail = (Mail)mailList.get(i);
			
			if(mail == null)
				continue;
			
			
			sb.append(mail.getTitle());
			sb.append("["+mail+"]");
			
		}
		
		return sb.toString();
	}
}
