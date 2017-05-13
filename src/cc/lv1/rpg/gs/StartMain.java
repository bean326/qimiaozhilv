package cc.lv1.rpg.gs;

import cc.lv1.rpg.gs.gui.MainFrame;

public class StartMain
{
	   
	public void run()
	{
		MainFrame mainFrame = new MainFrame();
		mainFrame.showFrame();
	}

	public static void main(String[] args)
	{
		StartMain startMain = new StartMain();
		startMain.run();
	}
}
