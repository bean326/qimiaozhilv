package cc.lv1.rpg.gs.gui.component.i;

import javax.swing.JFrame;

public abstract class Component
{
	
	protected JFrame frame;
	
	public abstract void setJFrame(JFrame frame);
	
	public abstract void addToFrame();
	
}
