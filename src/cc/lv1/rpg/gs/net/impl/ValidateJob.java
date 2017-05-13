package cc.lv1.rpg.gs.net.impl;


import vin.rabbit.net.abs.NetConnection;
import vin.rabbit.net.job.i.NetJob;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.collection.impl.ArrayList;
import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.dao.DatabaseAccessor;
import cc.lv1.rpg.gs.entity.impl.Player;
import cc.lv1.rpg.gs.load.impl.MergeLoader;
import cc.lv1.rpg.gs.net.SMsg;
import cc.lv1.rpg.gs.other.ErrorCode;

/**
 * 验证
 * @author balan
 * @version 1.0
 */
public class ValidateJob extends NetJob 
{
	
	private WorldManager worldmanager;
	
	private NetConnection conn; 
	
	private String loginData;
	
	//private String name;
	
	
	public ValidateJob(WorldManager worldmanager ,NetConnection conn, String loginData)
	{
		this.conn = conn;
		this.loginData = loginData.trim();
		this.worldmanager = worldmanager;
	}


	public NetConnection getConnection() 
	{
		return conn;
	}


	public void run() 
	{
		if(conn == null)
			return;
		
		worldmanager.getDatabaseAccessor().loadPlayer(loginData, new PlayerLoader());
	}
	
	
	
	public class PlayerLoader
	{
		
		public static final int EXCEPT_PLAYER = -1;
		
		public static final int NEW_PLAYER = 1;
		
		public static final int OLD_PLAYER = 2;
		
		public static final int CLOSE_PLAYER = 3;
		
		public void loadPlayer(final int state , Player player)
		{
			ByteBuffer buffer = new ByteBuffer();

			switch(state)
			{
			case PlayerLoader.EXCEPT_PLAYER:
				buffer.writeInt(ErrorCode.EXCEPTION_LOGIN_PLAYINFOERROR);
				if(conn != null)
				{
					conn.sendMessage(new SMsg(SMsg.S_EXCEPTION_MESSAGE,buffer));
					conn.close();
				}
				return;
			case PlayerLoader.NEW_PLAYER: 
				buffer.writeInt(0); //null player

				if(checkMerge(conn))
					return;
				
				if(conn != null)
				{
					conn.setInfo(loginData);//临时名字
					conn.sendMessage(new SMsg(SMsg.S_LOGIN_COMMAND,buffer));//登陆成功，但是帐号不包含角色，需要新建
				}
				break;
			case PlayerLoader.OLD_PLAYER: 
				buffer.writeInt(player.id);
				buffer.writeUTF(player.name);
				buffer.writeByte(player.sex);
				buffer.writeInt(player.profession);
				buffer.writeInt(player.upProfession);
				buffer.writeInt(player.level);
				buffer.writeInt(player.modelMotionId);
				if(conn != null)
				{
					conn.setId(player.id);
					conn.setInfo(player);
					conn.sendMessage(new SMsg(SMsg.S_LOGIN_COMMAND,buffer));//登陆成功并且此帐号里已经有角色，返回给客户端信息
				}
				break;
			case PlayerLoader.CLOSE_PLAYER:
				buffer = new ByteBuffer(4);
				buffer.writeInt(ErrorCode.ALERT_ACCOUNT_CLOSED);
				if(conn != null)
				{
					conn.sendMessage(new SMsg(SMsg.S_ALERT_MESSAGE, buffer));
					conn.close();
				}
				break;
			}
		}

	
		/**
		 * 检查合区信息里面有没有玩家
		 * @param buffer
		 */
		private boolean checkMerge(NetConnection conn)
		{
			
			if(MergeLoader.mergeDAs.length == 0)
			{
				return false;
			}
			
			ByteBuffer tmpBuffer = new ByteBuffer();
			ArrayList list = new ArrayList(5);
			int count = 0;
			for (int i = 0; i < MergeLoader.mergeDAs.length; i++)
			{
				
				Player player = MergeLoader.mergeDAs[i].getPlayer(loginData);

				if(player != null)
				{
					tmpBuffer.writeUTF(MergeLoader.mergeDAs[i].getDbname());
					tmpBuffer.writeInt(player.id);
					tmpBuffer.writeUTF(player.name);
					tmpBuffer.writeByte(player.sex);
					tmpBuffer.writeInt(player.profession);
					tmpBuffer.writeInt(player.upProfession);
					tmpBuffer.writeInt(player.level);
					tmpBuffer.writeInt(player.modelMotionId);
					count++;

					player.worldNUM = MergeLoader.mergeDAs[i].getDbname();
					list.add(player);
				}
			}
			
			
			ByteBuffer buffer = new ByteBuffer();
			
			if(count > 0)
			{
				buffer.writeByte(count);
				buffer.writeBytes(tmpBuffer.getBytes());
				conn.setInfo(list);
				
				if(conn != null)
					conn.sendMessage(new SMsg(SMsg.S_PLAYER_MERGE_COMMAND,buffer));
				return true;
			}
			else
			{
				return false;
			}
		
		}
		
	}
}
