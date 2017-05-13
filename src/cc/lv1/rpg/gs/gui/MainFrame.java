package cc.lv1.rpg.gs.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.UnsupportedEncodingException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import vin.rabbit.util.ThreadPool;
import cc.lv1.rpg.gs.gui.component.i.Component;
import cc.lv1.rpg.gs.gui.component.impl.BtnComponent;
import cc.lv1.rpg.gs.gui.component.impl.LabelComponent;
import cc.lv1.rpg.gs.gui.component.impl.TxtComponent;
import cc.lv1.rpg.gs.gui.job.UpdateFrame;

public class MainFrame extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static int TIME_CLEAR_CONSOLE = 2;
	
	public static final int WIDTH = 600;
	
	public static final int HEIGHT = 400;
	
	public static final int X = 150;
	
	public static final int Y = 100;
	
	public static Component [] components = new Component[3];
	
	private ThreadPool threadPool;
	
	public MainFrame()
	{
		setSize(WIDTH, HEIGHT);
		setLocation(300, 100);
		setResizable(false);
		
		initWindowListener();
		
		setTitle("Server Manager");
		setUndecorated(false);
		
		setForeground(Color.BLACK);

		initContainer();
		initComponent();
		initPool();
		initJob();
	}

	private void initWindowListener()
	{
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		addWindowListener(new WindowListener()
		{
			public void windowActivated(WindowEvent e)
			{

			}

			public void windowClosed(WindowEvent e)
			{

			}

			public void windowClosing(WindowEvent e)
			{
			   int res= JOptionPane.showConfirmDialog(null,"Are you sure EXIT？","EXIT",JOptionPane.OK_CANCEL_OPTION);
			   
			   if(res==JOptionPane.OK_OPTION)
			   {
				   System.exit(0);
			   }
			}

			public void windowDeactivated(WindowEvent e)
			{
				   MainFrame mf = (MainFrame)e.getSource();
				   mf.setVisible(true);
			}

			public void windowDeiconified(WindowEvent e)
			{

			}

			public void windowIconified(WindowEvent e)
			{

			}

			public void windowOpened(WindowEvent e)
			{

			}});
	}

	private void initContainer()
	{
		Container container = getContentPane();
		container.setLayout(null);
		container.setBackground(new Color(101,100,100));
	}

	private void initComponent()
	{
		BtnComponent btnComponent = new BtnComponent();
		btnComponent.setJFrame(this);
		btnComponent.addToFrame();
		
		components[0] = btnComponent;
		
		LabelComponent labelComponent = new LabelComponent();
		labelComponent.setJFrame(this);
		labelComponent.addToFrame();
		
		components[1] = labelComponent;
		
		TxtComponent txtComponent = new TxtComponent();
		txtComponent.setJFrame(this);
		txtComponent.addToFrame();
		
		components[2] = txtComponent;
	}
	
	public static void println(Object obj)
	{
		try
		{
			String line = System.getProperty("line.separator");
			
			try
			{

				((TxtComponent)components[2]).infoText.append(new String(obj.toString().getBytes(),"GBK")+line);
			}
			catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}
		}
		catch(NullPointerException e)
		{
			System.out.println(obj);
		}
	}
	
	private void initPool()
	{
		threadPool = new ThreadPool(10, 1);
		threadPool.init();
	}
	
	private void initJob()
	{
		UpdateFrame uf = new UpdateFrame(this);
		new Thread(uf,"UpdateFrame").start();
	}


	public void showFrame()
	{	
		setVisible(true);
	}
	
	public ThreadPool getThreadPool()
	{
		return threadPool;
	}
	
}
