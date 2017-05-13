package cc.lv1.rpg.gs.util;

import cc.lv1.rpg.gs.GameServer;
/**
 * 后台服务器
 * @author dxw
 *
 */
public class BackServer
{

	public void init()
	{
		
		GameServer.isBackSever = true;
		GameServer.getInstance().initialOnBackServer();
		GameServer.getInstance().start();
	}
	
	
	public static void main(String[] args)
	{
		BackServer bs = new BackServer();
		bs.init();
	}
	
}
