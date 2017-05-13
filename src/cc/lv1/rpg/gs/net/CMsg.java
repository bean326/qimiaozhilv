package cc.lv1.rpg.gs.net;

import vin.rabbit.net.AppMessage;
import vin.rabbit.util.ByteBuffer;

public class CMsg extends AppMessage
{

	public CMsg(int type)
	{
		super(type);
	}
	
	public CMsg(int type, ByteBuffer buffer)
	{
		super(type, buffer);
	}

}
