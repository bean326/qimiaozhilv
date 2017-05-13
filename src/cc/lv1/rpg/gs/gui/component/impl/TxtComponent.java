package cc.lv1.rpg.gs.gui.component.impl;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.EventListener;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import vin.rabbit.net.abs.NetServer;

import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.gui.MainFrame;
import cc.lv1.rpg.gs.gui.component.i.Component;
import cc.lv1.rpg.gs.gui.listener.BtnListener;

public class TxtComponent extends Component implements KeyListener
{

	private Container container;
	
	private StringBuffer pwd = new StringBuffer();
	
	public void setJFrame(JFrame frame)
	{
		super.frame = frame;
		container = frame.getContentPane();
	}
	
	public JTextArea infoText;
	
	public JScrollPane infoJSP;
	
	public TxtComponent()
	{
		infoText = new JTextArea();
		infoText.setForeground(Color.WHITE);
		infoText.setBorder(null);
		infoText.setBackground(new Color(90,90,90));
		infoJSP = new JScrollPane(infoText);
		infoJSP.setBounds(20, 40,MainFrame.WIDTH-200,MainFrame.HEIGHT-100);
		infoText.addKeyListener(this);
		NetServer.setJTextArea(infoText);
	}

	public void addToFrame()
	{
		container.add(infoJSP);
	}


	public void keyPressed(KeyEvent e)
	{
	}


	public void keyReleased(KeyEvent e)
	{		
		if(!GameServer.isRunning())
		{
			if(e.getKeyCode() == 0x08)
			{
				infoText.setText("");
				pwd = new StringBuffer();
			}
			else if(e.getKeyCode() == 0x0a)
			{
				BtnComponent btnComponent = (BtnComponent)MainFrame.components[0];
				EventListener el[] = (EventListener[])btnComponent.getBStart().getListeners(ActionListener.class);
				BtnListener btnel = (BtnListener)el[0];
				btnel.startClick();
			}
			else
			{
				if(e.getKeyCode() != 0x10)
				{
					pwd.append(e.getKeyChar());
					infoText.setText(infoText.getText().replace(e.getKeyChar(), '*'));
				}

			}
		}
	}

	public void keyTyped(KeyEvent e)
	{

	}

	public StringBuffer getPwd()
	{
		return pwd;
	}

	
	
}
