package cc.lv1.rpg.gs.data;

import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.net.SMsg;
import vin.rabbit.util.ByteBuffer;
import vin.util.ext.Timer;
import vin.util.ext.i.TimerListener;

/**
 * 自动发送离线公告
 * @author dxw
 *
 */
public class Poster implements TimerListener
{
	
	private Timer timer;
	
	private String msgContent;
	
	public Poster(String msgContent,Timer timer)
	{
		this.msgContent = msgContent;
		this.timer = timer;
	}
	
	public void onTimerPing()
	{
		ByteBuffer buffer = new ByteBuffer(msgContent.length()+16);
		buffer.writeByte(8);
		buffer.writeInt(100);
		buffer.writeUTF("POST");
		buffer.writeUTF(msgContent);
		GameServer.getInstance().
		getWorldManager().
		dispatchMsg(SMsg.S_CHAT_COMMAND, buffer);
	}
	
	
	public void onTimerEnd()
	{
		GameServer.getInstance().
		getWorldManager().postList.remove(timer);
	}
	
	public String getMsgContent()
	{
		return msgContent;
	}

}
