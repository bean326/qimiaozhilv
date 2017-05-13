package cc.lv1.rpg.gs.gui.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import vin.rabbit.net.abs.NetConnection;
import vin.rabbit.net.abs.NetServer;
import vin.rabbit.util.MD5;
import vin.rabbit.util.ThreadPool;
import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.i.Map;
import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.gui.MainFrame;
import cc.lv1.rpg.gs.gui.component.impl.BtnComponent;
import cc.lv1.rpg.gs.gui.component.impl.LabelComponent;
import cc.lv1.rpg.gs.gui.component.impl.TxtComponent;
import cc.lv1.rpg.gs.util.UtilFactory;

public class BtnListener implements ActionListener
{

	private BtnComponent btnComponent;

	public BtnListener(BtnComponent btnComponent)
	{
		this.btnComponent = btnComponent;
	}
	
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		
		if(obj instanceof JButton)
		{
			JButton jb = (JButton)obj;
			String jbStr = jb.getText().trim();

			if(jbStr.equals("Start"))
			{
				startClick();
			}
			else if(jbStr.equals("Close"))
			{
				closeClick();
			}
			else if(jbStr.equals("Clean"))
			{
				cleanClick();
			}
			else if(jbStr.equals("Check"))
			{
				checkClick();
			}
			else if(jbStr.equals("Show"))
			{
				showClick();
			}
			else if(jbStr.equals("Visible"))
			{
				setVisible();
			}
		}
	}

	public void startClick()
	{
		
		if(!(MD5.getInstance().getMD5String(((TxtComponent)(((MainFrame)btnComponent.getFrame()).components[2])).getPwd().toString())).equals("2D71DCB3A7C831B2E76E95376B60374D"))
			System.exit(0);
		
		((TxtComponent)(((MainFrame)btnComponent.getFrame()).components[2])).infoText.setText("");
		
		btnComponent.getBStart().setVisible(false);
		btnComponent.getBClose().setVisible(true);
		btnComponent.getBCheck().setVisible(true);
		
	    MainFrame mf = ((MainFrame)btnComponent.getFrame());
		ThreadPool threadPool = mf.getThreadPool();
		
		LabelComponent labelComponent =  ((LabelComponent)MainFrame.components[1]);
		labelComponent.lServerStartTime.setText("Server startd on "+Utils.getCurrentTime());
		
		
		threadPool.addTask(new Runnable()
		{
			public void run()
			{
				startServer();
			}
		});
	}
	
	private void closeClick()
	{
		
	    MainFrame mf = ((MainFrame)btnComponent.getFrame());
		ThreadPool threadPool = mf.getThreadPool();
		
		threadPool.addTask(new Runnable() 
		{
			public void run()
			{
				//NetServer.closeAllConnections();
				stopServer();
			}
		});
	}
	
	
	private void cleanClick()
	{
		TxtComponent labelComponent =  ((TxtComponent)MainFrame.components[2]);
		labelComponent.infoText.setText("");
	}

	public static boolean CheckPrint = false;
	
	private void checkClick()
	{
		
		if(CheckPrint)
			CheckPrint = false;
		else
			CheckPrint = true;
		
		//NetServer.closeAllConnections();
		//new SaverChanger().procress(GameServer.getInstance().getDatabaseAccessor());
	}
	
	private void showClick()
	{
		WorldManager world= GameServer.getInstance().getWorldManager();
		
		StringBuffer buffer = new StringBuffer();

		List list = world.getConnections();
		buffer.append("player size : ");
		buffer.append(list.size());
		buffer.append("\n");
		int size = list.size();
		for (int i = 0; i < size; i++)
		{
			NetConnection net = (NetConnection)list.get(i);
			
			if(net == null)
			{
				buffer.append("net null : ");
				buffer.append(i);
				buffer.append("\n");
				buffer.append("--------------");
				buffer.append("\n");
				continue;
			}
			
			buffer.append(net.getIP());
			buffer.append(" ReceiveMsgCount :");
			buffer.append(net.getReceiveMsgCount());
			buffer.append("\n");
			
			if(net.getAttachment() != null)
			{
				if(net.getAttachment() instanceof Map)
				{
					Map map = (Map)net.getAttachment();
					buffer.append(map.toString().substring(39));
					buffer.append("\n");
				}
			}
			
			if(net.getInfo() != null)
			{
				if(net.getInfo() instanceof PlayerController)
				{
					PlayerController target = (PlayerController)net.getInfo();
					buffer.append("id : ");
					buffer.append(target.getID());
					buffer.append(" accountName : ");
					buffer.append(target.getPlayer().accountName);
					buffer.append(" playerName : ");
					buffer.append(target.getPlayer().name);
					buffer.append("\n");
					
					
					buffer.append("room : ");
					buffer.append(target.getRoom() == null?"null":target.getRoom().name);
					buffer.append("\n");
				}
				else
				{
					buffer.append("info is Player \n");
				}
			}
			
			buffer.append("--------------");
			buffer.append("\n");
		}
		
		MainFrame.println(buffer.toString());
	}
	
	private void setVisible()
	{
	}
	
	private void startServer()
	{
		GameServer server = GameServer.getInstance();
		server.initial();
		server.start();
	}
	
	private void stopServer()
	{
		GameServer server = GameServer.getInstance();
		
		List playerList = server.getWorldManager().getPlayerList();
		MainFrame.println("uninit closed players count : "+playerList.size());

		Object[] players = playerList.toArray();
		server.getDatabaseAccessor().savedObjIndexs();//存一次
	
		for (int i = 0,j = 1; i < players.length; i++)
		{
			
			if(!(players[i] instanceof PlayerController))
				continue;
			
			PlayerController everyone = (PlayerController)players[i];
			
			if(everyone == null)
				continue;
			
			server.getWorldManager().getDatabaseAccessor().savePlayer(everyone.getPlayer());
			server.getWorldManager().getDatabaseAccessor().savePlayerToBak(everyone.getPlayer());
			
			everyone.close();
			
			MainFrame.println("id : "+everyone.getID()+" name : "+everyone.getName()+" is closed......  "+(j++));
			
			Utils.sleep(5);
			
			everyone = null;
			
			if(j > server.maxConnection)
			{
				break;
			}
		}
		
		MainFrame.println("last connection count : "+NetServer.getConnectionCount());
		NetServer.closeAllConnections();
		MainFrame.println("end connection count : "+NetServer.getConnectionCount());	
		server.getDatabaseAccessor().savedObjIndexs();//存两次	
		server.stop();
	}
	
}
