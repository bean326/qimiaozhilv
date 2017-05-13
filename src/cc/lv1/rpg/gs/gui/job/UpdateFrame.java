package cc.lv1.rpg.gs.gui.job;

import javax.swing.JLabel;

import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.gui.MainFrame;
import cc.lv1.rpg.gs.gui.component.impl.LabelComponent;
import vin.rabbit.net.abs.NetServer;
import vin.rabbit.net.job.JobObserver;
import vin.rabbit.net.job.i.NetJob;
import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.impl.LinkedList;

public class UpdateFrame implements Runnable
{
	
	private MainFrame mainFrame;
	
	private LabelComponent labelComponent;
	
	private int maxPlayerAccount;
	
	public UpdateFrame(MainFrame mainFrame)
	{
		this.mainFrame = mainFrame;
		labelComponent = (LabelComponent)mainFrame.components[1];
	}

	public void run()
	{
		int hourTimer = 0;
		int hour = 0;

		//boolean isPrint = false;
		
		while(true)
		{
			Utils.sleep(50);
			
			if(!GameServer.isRunning())
				continue;
			
			maxPlayerAccount = maxPlayerAccount>NetServer.getConnectionCount()?maxPlayerAccount:NetServer.getConnectionCount();
			
			StringBuffer conn1 = new StringBuffer(25);
			conn1.append("Connection:   ");
			conn1.append(GameServer.getInstance().getWorldManager().getConnectionSize());
			conn1.append(" / ");
			conn1.append(maxPlayerAccount);

			labelComponent.lPlayerCount.setText(conn1.toString());
			
			GameServer gameserver = GameServer.getInstance();
			
			for(int i = 0 ; i < GameServer.JOB_COUNT; i ++)
			{
				JLabel label = labelComponent.lJobs[i];
				JobObserver jobsObserver = gameserver.getJobsObserver();
				
				label.setText(jobsObserver.getName(i)+" : "+jobsObserver.getJobSizeByType(i)+"");
				
/*				if(!isPrint && jobsObserver.getJobSizeByType(i) > 50)
				{
					LinkedList list = jobsObserver.getJobManager(i).getJobs();
					
					for (int j = 0; j < list.size(); j++)
					{
						NetJob job = (NetJob)list.get(i);
						System.out.println(job.toString());
					}
					isPrint = true;
					System.out.println("-----------------------");
				}
				else if(isPrint)
				{
					if(jobsObserver.getJobSizeByType(i) == 0)
						isPrint = false;
				}*/
				
				
			}
			
			labelComponent.lMaxMem.setText("Memory: "+ Runtime.getRuntime().totalMemory() / 1024 /1024+" mb / " +
					Runtime.getRuntime().maxMemory() / 1024 /1024+" mb");

			labelComponent.lCurrMem.setText("Quick Memory: " + Runtime.getRuntime().freeMemory() / 1024 /1024+" mb");
			
			
			hourTimer++;
			if(hourTimer>=3600)
			{
				hour++;
				labelComponent.lServerTime.setText("Timer:  "+(hour/24)+" day "+(hour%24)+" hour");
				hourTimer = 0;
			}
			
/*			if(showMessage)
			{

			}*/
		}
	}

	
	public int getMaxPlayerAccount()
	{
		return maxPlayerAccount;
	}
}
