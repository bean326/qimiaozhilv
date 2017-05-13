package cc.lv1.rpg.gs;

import java.awt.event.ActionListener;
import java.util.EventListener;

import cc.lv1.rpg.gs.gui.MainFrame;
import cc.lv1.rpg.gs.gui.component.impl.BtnComponent;
import cc.lv1.rpg.gs.gui.listener.BtnListener;

public class AutoStartMain
{
	   
	public void run()
	{
		MainFrame mainFrame = new MainFrame();
		mainFrame.showFrame();
		
		GameServer.isAutoStart = true;
		
		BtnComponent btnComponent = (BtnComponent)MainFrame.components[0];
		EventListener el[] = (EventListener[])btnComponent.getBStart().getListeners(ActionListener.class);
		BtnListener btnel = (BtnListener)el[0];
		btnel.startClick();
	}
	
	public static void main(String[] args)
	{
		AutoStartMain startMain = new AutoStartMain();
		startMain.run();
	}
}
