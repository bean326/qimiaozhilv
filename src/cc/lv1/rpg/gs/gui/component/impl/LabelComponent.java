package cc.lv1.rpg.gs.gui.component.impl;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;

import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.gui.MainFrame;
import cc.lv1.rpg.gs.gui.component.i.Component;

public class LabelComponent extends Component
{

	private Container container;
	
	public void setJFrame(JFrame frame)
	{
		this.frame = frame;
		container = frame.getContentPane();
	}

	
	public JLabel lPlayerCount; //玩家数量
	
	public JLabel lServerStartTime;
	
	public JLabel lServerTime;
	
	public JLabel lServerName;
	
	public JLabel [] lJobs;
	
	public JLabel lMaxMem;
	
	public JLabel lCurrMem;
	
	public LabelComponent()
	{
		lServerStartTime = new JLabel("Server startd time on");
		lServerStartTime.setForeground(Color.WHITE);
		lServerStartTime.setBounds(MainFrame.WIDTH-280,MainFrame.HEIGHT-65, 300, 50);
		
		lServerTime = new JLabel("");
		lServerTime.setForeground(Color.WHITE);
		lServerTime.setBounds(MainFrame.WIDTH-160,MainFrame.HEIGHT-600, 300, 50);
		
		lPlayerCount = new JLabel();
		lPlayerCount.setForeground(Color.WHITE);
		lPlayerCount.setBounds(MainFrame.WIDTH-150,30, 200, 50);
		
		lMaxMem = new JLabel();
		lMaxMem.setForeground(Color.WHITE);
		lMaxMem.setBounds(MainFrame.WIDTH-150,270,300,50);
		
		lCurrMem = new JLabel();
		lCurrMem.setForeground(Color.WHITE);
		lCurrMem.setBounds(MainFrame.WIDTH-150,300,300,50);
		
		lJobs = new JLabel[GameServer.JOB_COUNT];
		
		for(int i = 0 ; i < GameServer.JOB_COUNT ; i ++)
		{
			lJobs[i] = new JLabel();
			lJobs[i].setForeground(Color.WHITE);
			lJobs[i].setBounds(MainFrame.WIDTH-145,50+(i*15), 200, 50);
		}
		
		lServerName = new JLabel("WT DHHL");
		lServerName.setFont(new Font("TimesRoman",Font.BOLD,20));
		lServerName.setForeground(Color.WHITE);
		lServerName.setBounds(20, 0, 300, 50);
	}
	
	
	public void addToFrame()
	{
		container.add(lServerStartTime);
		container.add(lServerTime);
		container.add(lPlayerCount);
		container.add(lMaxMem);
		container.add(lCurrMem);
		container.add(lServerName);
		
		
		for(int i = 0 ; i < GameServer.JOB_COUNT ; i ++)
		{
			container.add(lJobs[i]);
		}
	}


	

}
