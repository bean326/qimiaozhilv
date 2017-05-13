package cc.lv1.rpg.gs.util;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.impl.ArrayList;
import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.dao.DatabaseAccessor;
import cc.lv1.rpg.gs.entity.ext.Bag;
import cc.lv1.rpg.gs.entity.ext.FriendList;
import cc.lv1.rpg.gs.entity.ext.MailBox;
import cc.lv1.rpg.gs.entity.ext.OtherExtInfo;
import cc.lv1.rpg.gs.entity.ext.PetTome;
import cc.lv1.rpg.gs.entity.ext.Storage;
import cc.lv1.rpg.gs.entity.impl.Goods;
import cc.lv1.rpg.gs.entity.impl.Mail;
import cc.lv1.rpg.gs.entity.impl.Player;
import cc.lv1.rpg.gs.entity.impl.pet.Pet;
import cc.lv1.rpg.gs.gui.MainFrame;

public class MergeStart extends JFrame implements ActionListener,Runnable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int WIDTH = 600;

	public static final int HEIGHT = 400;

	private static MergeStart mergeStart = null;

	private String textFieldsTitle[] =
	{ "源数据库1", "源数据库2", "目标数据库", "用户名", "密码" };

	private JTextField textFields[] = new JTextField[5];

	private static JTextArea infoText;

	private JScrollPane infoJSP;
	
	private JScrollBar jsb;

	private String buttonsInfo[] =
	{ "合成数据","清除文本框" };

	private JButton buttons[] = new JButton[2];

	private DatabaseAccessor das[] = new DatabaseAccessor[3];
	
	private MergeStart()
	{
	}

	public static MergeStart getInstance()
	{
		if (mergeStart == null)
			mergeStart = new MergeStart();
		return mergeStart;
	}

	private void init()
	{
		setSize(WIDTH, HEIGHT);
		setLocation(300, 100);
		setResizable(false);
		setTitle("Data Move Change");
		setUndecorated(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setForeground(Color.BLACK);

		initContainer();
		initTexts();
		initButtons();
		initPrint();
	}

	private void initContainer()
	{
		Container container = getContentPane();
		container.setLayout(null);
	}

	private void initTexts()
	{

		for (int i = 0; i < textFields.length; i++)
		{
			textFields[i] = new JTextField();
			textFields[i].setBounds(10 + (i * 120), 320, 100, 40);
			textFields[i].setBorder(BorderFactory
					.createTitledBorder(textFieldsTitle[i]));
			getContentPane().add(textFields[i]);
		}

		infoText = new JTextArea();
		infoText.setBorder(null);
		infoJSP = new JScrollPane(infoText);
		infoJSP.setBounds(170, 10, MergeStart.WIDTH - 200,
				MergeStart.HEIGHT - 100);

		jsb = infoJSP.getVerticalScrollBar();
		
		getContentPane().add(infoJSP);
	}

	private void initButtons()
	{
		for (int i = 0; i < buttons.length; i++)
		{
			buttons[i] = new JButton(buttonsInfo[i]);
			buttons[i].setBounds(20, 40, 100, 200);
			getContentPane().add(buttons[i]);
			buttons[i].addActionListener(this);
		}

	}

	private void initPrint()
	{
		OutputStream textAreaStream = new OutputStream()
		{
			public void write(int b) throws IOException
			{
				infoText.append(String.valueOf((char) b));
			}

			public void write(byte b[]) throws IOException
			{
				infoText.append(new String(b));
			}

			public void write(byte b[], int off, int len) throws IOException
			{
				infoText.append(new String(b, off, len));
			}
		};
		PrintStream myOut = new PrintStream(textAreaStream);
		System.setOut(myOut);
		//System.setErr(myOut);
	}

	public void start()
	{
		setVisible(true);
	}

	private void onReadyInit()
	{
		for (int i = 0; i < textFields.length; i++)
		{
			textFields[i].setEditable(false);
		}
//		for (int i = 0; i < buttons.length; i++)
//		{
			buttons[0].setVisible(false);
//      }

		System.out.println("Ready....");
	}

	public void actionPerformed(ActionEvent e)
	{
		Object obj = e.getSource();
		if (obj instanceof JButton)
		{
			for (int i = 0; i < textFields.length; i++)
			{
				if(textFields[i].getText().isEmpty())
					return;
			}
			
			
			JButton btn = (JButton)obj;
			
			if(btn.getText().equals(buttonsInfo[0])) //合成存档
			{
				onReadyInit();
				Thread thread =new Thread(this);
				thread.start();
				
				thread =new Thread(new Flow());
				thread.start();
			}
			else if(btn.getText().equals(buttonsInfo[1])) //清除文本框
			{
				infoText.setText("");
			}

		}

	}
	
	/**
	 * 记录已经保存的账号
	 */
	private ArrayList accountNameList = new ArrayList(1200);
	
	/**
	 * 二次过滤时看名字是否已经过滤掉
	 * @return
	 */
	private boolean isSavedThisAccount(String accountName)
	{
		int size = accountNameList.size();
		for (int i = 0; i < size; i++)
		{
			if(((String)accountNameList.get(i)).equals(accountName))
			{
				return true;
			}
		}
		return false;
	}
	
	
	public void run()
	{
		DatabaseAccessor.familyObjectIndex = 10000;
		DatabaseAccessor.goodsObjectIndex = 1;
		DatabaseAccessor.roleObjectIndex = 10000;
		
		
		
		GameServer.getInstance().initialWithoutNetServer();
		GameServer.getInstance().getWorldManager().setGameServer(
				GameServer.getInstance());
		
		for (int i = 0; i < das.length; i++)
		{
			das[i] = new DatabaseAccessor("127.0.0.1","1433",textFields[i].getText().trim(),textFields[3].getText().trim(),textFields[4].getText().trim(),"sqlserver");
		}
		
		
		ArrayList newPlayerList = null;
		int total = 0,count = 0,newCount = 0,sameCount = 0,lv_1 = 0,renameCount = 0,deleteRenameCount = 0;
		List list = null;
		int i = 0;
		do
		{
			list = das[0].loadPlayers(i,i+10000);
			i += 10000;
			
			newPlayerList = new ArrayList(10000);
			
			
			if(list.size() > 0)
			{
				count += list.size();
				total += list.size();

				for (int j = 0; j < list.size(); j++)
				{
					Player player1 = (Player)list.get(j);
					
					Player player2 = das[1].getPlayer(player1.accountName);
					
					if(player2 == null)
					{
						if(das[1].checkPlayerName(player1.name))
						{
							player1.name = das[0].getDbname()+"_"+player1.name;
							
							if(player1.level > 10)
							{
								System.out.println("名字变化\t"+player1.accountName+"\t"+player1.name+"\t"+player1.level);
								renameCount++;
							}
						}
						newPlayerList.add(player1);
						
					}
					else
					{
						
						//比较后进行清除流水号
						if(player1.level > player2.level)
						{
							if(das[1].checkPlayerName(player1.name))
							{
								player1.name = das[0].getDbname()+"_"+player1.name;
								
								if(player1.level > 10)
								{
									System.out.println("名字变化\t"+player1.accountName+"\t"+player1.name+"\t"+player1.level);
									renameCount++;
								}
							}
							Bag bag1 = (Bag)player1.getExtPlayerInfo("bag");
							Bag bag2 = (Bag)player2.getExtPlayerInfo("bag");
							bag1.money += bag2.money;
							newPlayerList.add(player1);
						}
						else
						{
							if(das[0].checkPlayerName(player2.name))
							{
								player2.name = das[1].getDbname()+"_"+player2.name;
								
								if(player2.level > 10)
								{
									System.out.println("名字变化\t"+player2.accountName+"\t"+player2.name+"\t"+player2.level);
									renameCount++;
								}
							}
							Bag bag1 = (Bag)player1.getExtPlayerInfo("bag");
							Bag bag2 = (Bag)player2.getExtPlayerInfo("bag");
							bag2.money += bag1.money;
							
							newPlayerList.add(player2);
						}
						
						accountNameList.add(player1.accountName);
						sameCount ++;
					}
					
				}
				

				int size = newPlayerList.size();
				for (int j = 0; j < size; j++)
				{
					Player player = (Player)newPlayerList.get(j);
					
					if(player.level <= 10)
					{
						if(player.name.startsWith(das[0].getDbname()+"_")||player.name.startsWith(das[1].getDbname()+"_"))
						{
							deleteRenameCount++;
						}
						
						newPlayerList.remove(j--);
						size--;
						
						lv_1++;

						continue;
					}
					
					updatePlayer(player);
				}
				newCount += newPlayerList.size();
				das[2].savePlayers(newPlayerList);
			}
		}while(list.size() > 0);
		
		
System.err.println(das[0].getDbname()+" 数量 : "+count);
count = 0;

		list = null;
		i = 0;
		do
		{
			list = das[1].loadPlayers(i,i+10000);
			i += 10000;
			
			newPlayerList = new ArrayList(10000);
			
			
			if(list.size() > 0)
			{
				count += list.size();
				total += list.size();
				
				
				for (int j = 0; j < list.size(); j++)
				{
					Player player2 = (Player)list.get(j);
					
					if(isSavedThisAccount(player2.accountName))
					{
						continue;
					}
					
					if(das[0].checkPlayerName(player2.name))
					{
						player2.name = das[1].getDbname()+"_"+player2.name;
						
						if(player2.level > 10)
						{
							System.out.println("名字变化\t"+player2.accountName+"\t"+player2.name+"\t"+player2.level);
							renameCount++;
						}
					}
					newPlayerList.add(player2);
					
				}
				

				int size = newPlayerList.size();
				for (int j = 0; j < size; j++)
				{
					Player player = (Player)newPlayerList.get(j);
					
					if(player.level <= 10)
					{
						if(player.name.startsWith(das[0].getDbname()+"_")||player.name.startsWith(das[1].getDbname()+"_"))
						{
							deleteRenameCount++;
						}
						
						newPlayerList.remove(j--);
						size--;
						lv_1++;
						continue;
					}
					
					updatePlayer(player);
				}
				newCount += newPlayerList.size();
				das[2].savePlayers(newPlayerList);
			}
		}while(list.size() > 0);
		

		das[2].savedObjIndexs();
		
System.err.println(das[1].getDbname()+" 数量 : "+count);
System.err.println(das[0].getDbname()+" 和 "+das[1].getDbname()+" 总数量 "+total);
System.err.println(das[0].getDbname()+" 和 "+das[1].getDbname()+" 相同账号数量 "+sameCount);
System.err.println("10 等级以下的  "+lv_1+" 个将不写入新数据库");
System.err.println("写入新数据库的总数量 "+newCount);
System.err.println("流水号   家族: "+DatabaseAccessor.familyObjectIndex+" 道具: "+DatabaseAccessor.goodsObjectIndex+" 角色: "+DatabaseAccessor.roleObjectIndex);
System.err.println("-------------------------------------");
System.err.println(das[0].getDbname()+" 和 "+das[1].getDbname()+" 重命名数量 "+renameCount+"/删除重命名 "+deleteRenameCount);

		for (int j = 0; j < accountNameList.size(); j++)
		{
			System.out.println("两区之间都有的账号\t"+accountNameList.get(j));
		}
		System.out.println("合区完成");
	}

	/**
	 * 更新玩家流水号
	 * @param player
	 */
	public void updatePlayer(Player player)
	{
//System.out.println("取得流水号 "+DatabaseAccessor.roleObjectIndex+" : "+DatabaseAccessor.goodsObjectIndex);
		
		
		if(DatabaseAccessor.roleObjectIndex == 0)
			player.id = GameServer.getInstance().getDatabaseAccessor().getNewPlayerId();
		else	
			player.id = ++DatabaseAccessor.roleObjectIndex;
		
		/**
		    familyId = 设置为0
			familyName = 设置为””
			isFamilyLeader = false
			familyContribution = 设置0

		 */
		player.familyId = 0;
		player.familyName = "";
		player.isFamilyLeader = false;
		
		
		
		/**
		 * 物品包括   背包，仓库，邮件
			Goods
			objectIndex 重新取得流水号

		 */
		
		//背包里面所有的物品
		Bag bag = (Bag)player.getExtPlayerInfo("bag");
		Goods goods [] = bag.goodsList;
		for (int i = 0; i < goods.length; i++)
		{
			if(goods[i] == null)
				continue;
			
			if(DatabaseAccessor.goodsObjectIndex == 0)
				goods[i].objectIndex = GameServer.getInstance().getDatabaseAccessor().getGoodsObjIndex();
			else
				goods[i].objectIndex = ++DatabaseAccessor.goodsObjectIndex;
		}
		
		//0 1 2是流水号  3 4 是时间
		goods = bag.extGoods;
		for (int i = 0; i < goods.length; i++)
		{
			if(i == 3 || i == 4)
				continue;

			if(goods[i] == null)
				continue;
			
			goods[i].objectIndex = ++DatabaseAccessor.goodsObjectIndex;
		}
		
		//新手礼包
		for (int i = 0; i < bag.giftGoods.size(); i++)
		{
			Goods g = (Goods)bag.giftGoods.get(i);
			
			if(g == null)
				continue;
			
			g.objectIndex = ++DatabaseAccessor.goodsObjectIndex;
		}
		
		//取得宠物
		PetTome pt = (PetTome)player.getExtPlayerInfo("petTome");
		Pet []pet= pt.getPets();
		for (int i = 0; i < pet.length; i++)
		{
			long dan = ++DatabaseAccessor.goodsObjectIndex;
			long g = ++DatabaseAccessor.goodsObjectIndex;
			pet[i].ObjectIndex(player,dan,g);
		}
		
		//取得仓库
		Storage storage = (Storage)player.getExtPlayerInfo("storage");
		goods = storage.getGoodsList();
		for (int i = 0; i < goods.length; i++)
		{
			if(goods[i] == null)
				continue;
			
			goods[i].objectIndex = ++DatabaseAccessor.goodsObjectIndex;
		}
		
		//取得邮件流水号
		MailBox mb = (MailBox)player.getExtPlayerInfo("mailbox");
		int mailSize = mb.mailList.size();
		for (int i = 0; i < mailSize; i++)
		{
			Mail mail =  (Mail)mb.mailList.get(i);
			Goods g1 = mail.getAttach1();
			if(g1 != null)
			{
				g1.objectIndex = ++DatabaseAccessor.goodsObjectIndex;
			}
			Goods g2 =mail.getAttach2();
			if(g2 != null)
			{
				g2.objectIndex = ++DatabaseAccessor.goodsObjectIndex;
			}
			
		}

		if(!player.getMarryInfo().equals(""))
		{
			MainFrame.println(player.accountName+":"+player.name+":"+player.getMarryInfo());
		}
		
		
		//清除结婚信息
		player.removeMarryGoods(2);
		OtherExtInfo oei = (OtherExtInfo)player.getExtPlayerInfo("otherExtInfo");
		oei.clearLoveInfo();
		

		
		
		//FriendList fl = (FriendList)player.getExtPlayerInfo("friendList");
		//fl.cleanFriendList();
		//fl.cleanBlackList();
	}

	private int infoTextLength;
	
	class Flow implements Runnable
	{

		public void run()
		{
			while(true)
			{
				try
				{
					if(jsb != null)
					{
						if(infoTextLength != infoText.getDocument().getLength())
						{
							infoTextLength = infoText.getDocument().getLength();
							jsb.setValue(infoText.getDocument().getLength()); 
						}
					}
					Utils.sleep(5);
				}
				catch(Exception e)
				{
					continue;
				}
			}
		}
		
	}
	
	public static void main(String args[])
	{
		MergeStart ms = MergeStart.getInstance();
		ms.init();
		ms.start();
	}



}
