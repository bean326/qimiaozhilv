package cc.lv1.rpg.gs.gui.component.impl;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;

import cc.lv1.rpg.gs.gui.MainFrame;
import cc.lv1.rpg.gs.gui.component.i.Component;
import cc.lv1.rpg.gs.gui.listener.BtnListener;

public class BtnComponent extends Component
{

	private Container container;
	
	public void setJFrame(JFrame frame)
	{
		super.frame = frame;
		container = frame.getContentPane();
	}

	private JButton bStart;
	
	private JButton bCheck;
	
	private JButton bClose;
	
	private JButton bClean;
	
	private JButton bShow;
	
	private JButton bVisible;
	
	public BtnComponent()
	{
		Font font = new Font("TimesRoman",Font.BOLD,10);
		
		bStart = new JButton("Start");
		bStart.setFont(font);
		bStart.setBounds(MainFrame.WIDTH-580,MainFrame.HEIGHT-60,68,20);
		
		bCheck = new JButton("Check");
		bCheck.setBounds(MainFrame.WIDTH-580,MainFrame.HEIGHT-60,68,20);
		bCheck.setBackground(Color.PINK);
		bCheck.setFont(font);
		bCheck.setVisible(false);
		
		bClose = new JButton("Close");
		bClose.setBounds(MainFrame.WIDTH-370,MainFrame.HEIGHT-60,68,20);
		bClose.setBackground(Color.PINK);
		bClose.setFont(font);
		bClose.setVisible(false);
			
		bClean = new JButton("Clean");
		bClean.setFont(font);
		bClean.setBounds(MainFrame.WIDTH-510,MainFrame.HEIGHT-60,68,20);
		
		bShow = new JButton("Show");
		bShow.setFont(font);
		bShow.setBounds(MainFrame.WIDTH-440,MainFrame.HEIGHT-60,68,20);
		
		bVisible = new JButton("Visible");
		bVisible.setName("false");
		bVisible.setFont(font);
		bVisible.setBounds(MainFrame.WIDTH-248,MainFrame.HEIGHT-60,68,16);
	}
	
	public void addToFrame()
	{
		bStart.addActionListener(new BtnListener(this));
		bCheck.addActionListener(new BtnListener(this));
		bClose.addActionListener(new BtnListener(this));
		bClean.addActionListener(new BtnListener(this));
		bShow.addActionListener(new BtnListener(this));
		bVisible.addActionListener(new BtnListener(this));
		
		container.add(bStart);
		container.add(bCheck);
		container.add(bClose);
		container.add(bClean);
		container.add(bShow);
		container.add(bVisible);
	}
	
	
	public JButton getBStart()
	{
		return bStart;
	}

	public JButton getBCheck()
	{
		return bCheck;
	}

	public JButton getBClose()
	{
		return bClose;
	}

	public JButton getBClean()
	{
		return bClean;
	}

	public JButton getBShow()
	{
		return bShow;
	}
	
	public JButton getBVisible()
	{
		return bVisible;
	}

	public JFrame getFrame()
	{
		return frame;
	}
}
