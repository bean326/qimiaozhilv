package cc.lv1.rpg.gs.net;

import org.apache.mina.core.session.IoSession;

import vin.rabbit.net.AppMessage;
import vin.rabbit.net.SMConnection;

public class BaseConnection extends SMConnection
{

	public BaseConnection(IoSession arg0)
	{
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public void close()
	{
	}

	public void sendMessage(AppMessage s)
	{
	}

	
}
