package cc.lv1.rpg.gs.net.impl;

import java.io.UnsupportedEncodingException;

import vin.rabbit.net.AppMessage;
import vin.rabbit.net.abs.NetConnection;
import vin.rabbit.net.job.i.NetJob;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.MD5;
import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.Map;
import vin.rabbit.util.collection.impl.ArrayList;
import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.dao.DatabaseAccessor;
import cc.lv1.rpg.gs.data.DC;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.data.MailRemind;
import cc.lv1.rpg.gs.data.MarkChar;
import cc.lv1.rpg.gs.data.UseChar;
import cc.lv1.rpg.gs.entity.controller.FamilyController;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.ext.Bag;
import cc.lv1.rpg.gs.entity.ext.MailBox;
import cc.lv1.rpg.gs.entity.ext.OtherExtInfo;
import cc.lv1.rpg.gs.entity.impl.Goods;
import cc.lv1.rpg.gs.entity.impl.Mail;
import cc.lv1.rpg.gs.entity.impl.Player;
import cc.lv1.rpg.gs.gui.MainFrame;
import cc.lv1.rpg.gs.load.impl.MergeLoader;
import cc.lv1.rpg.gs.net.SMsg;
import cc.lv1.rpg.gs.other.ErrorCode;
import cc.lv1.rpg.gs.util.FontConver;
import cc.lv1.rpg.gs.util.MergeStart;
/**
 * 客户端消息事物
 * @author balan
 *
 */
public class CMsgJob extends NetJob
{
	private AppMessage msg;
	
	private WorldManager worldmanager;

	private NetConnection netConnection;
	
	public CMsgJob(WorldManager worldManager, AppMessage msg)
	{
		this.msg = msg;
		this.worldmanager = worldManager;
		netConnection = (NetConnection)msg.getSource();
		
		if(netConnection.getExceptionMsg() == null)
			netConnection.setExceptionMsg(msg.getType());
		
	}

	public NetConnection getConnection()
	{
		return netConnection;
	}


	public void run()
	{

		if(netConnection.getReceiveMsgCount() > 1400) //手点击的1分钟300次，狂点
		{
			
//			MainFrame.println(netConnection.getIP()+" msg: "
//					+Integer.toHexString(msg.getType())
//					+" RMC: "+netConnection.getReceiveMsgCount());
			
			if(netConnection.getInfo() instanceof PlayerController)
			{
				PlayerController player = (PlayerController)netConnection.getInfo();

				MainFrame.println("\feng:"+player.getPlayer().accountName+":"+player.getName()+"\t"+Utils.getCurrentTime());//封
				
				player.wgCount = 0xffff; 
				
				//封号
				GameServer.getInstance().getDatabaseAccessor().changeAccountState(player.getPlayer().accountName, 1);
			}
			
			//封ip
			worldmanager.changeProIpState(netConnection.getIP(), 1);
			
			netConnection.close();
		}
		
		long bTime = System.currentTimeMillis();
		clientMessageChain();
		long aTime = System.currentTimeMillis();
		
		if(netConnection != null)
		{
			if(aTime - bTime > GameServer.OVERTIME)
			{
				MainFrame.println("msg:"+Integer.toHexString(msg.getType())+"  pt:"+(aTime - bTime));
			}
		}

	}
	
	public String toString()
	{
		return "msg type : "+msg.getType();
	}
	
	/**
	 * 与客户端通信
	 * @param conn
	 */
	public void clientMessageChain()
	{
		if(msg == null)
			return;

		int type = msg.getType();
		int markType = msg.getMarkType();

		if(markType == SMsg.T_LOGIN_COMMAND)
		{

			if(type == SMsg.C_LOGIN_COMMAND)
			{
				processPlayerLogin();
			}
			else if(type == SMsg.C_CREATE_ROLE_COMMAND)
			{
				processPlayerCreate();
			}
			else if(type == SMsg.C_DELETE_ROLE_COMMAND)
			{
//				processPlayerRoleDelete();
			}
			else if(type == SMsg.C_ROLENAME_REPEAT_COMMAND)
			{
				processCheckRoleName();
			}
			else if(type == SMsg.C_PLAYER_ENTER_GAME_COMMAND)
			{
				processPlayerEnterGame();
			}
			else if(type == SMsg.C_PLAYER_HEART_PING_COMMAND)
			{
				SMsg smsg = new SMsg(SMsg.S_PLAYER_HEART_PING_COMMAND
						,new ByteBuffer(1));
				netConnection.sendMessage(smsg);

				if(netConnection.getAttachment() != null)
				{
					if(netConnection.getAttachment() instanceof Map)
					{
						Map map = (Map)netConnection.getAttachment();
						Integer i =(Integer)map.get(SMsg.C_PLAYER_HEART_PING_COMMAND);

						if(i != null)
						{
							if(i > 5)
							{
								if(netConnection.getInfo() == null)
									netConnection.close();
								else if(!(netConnection.getInfo() instanceof PlayerController))
									netConnection.close();
							}
						}
					}
				}
	
			}
			else if(type == SMsg.C_PLAYER_REG_COMMAND)
			{
				processReg();
			}
			else if(type == SMsg.C_PLAYER_CHANGEPWD_COMMAND)
			{
				changePwd();
			}
			else if(type == SMsg.C_PLAYER_INPUTMAIL_COMMAND)
			{
				changeMail();
			}
			else if(type == SMsg.C_PLAYER_LOGIN_COMMAND)
			{
				processPlayerLoginByTrue();
			}
			else if(type == SMsg.C_PLAYER_MERGE_COMMAND)
			{
				processMerge();
			}
			else if(type == SMsg.C_PLAYER_RESELECTMERGE_COMMAND)
			{
				//重新选择合区
				//requestSelectMerge();
			}
			else if(type == SMsg.C_PLAYER_SAFE_ESC_COMMAND)
			{
				if(netConnection == null)
					return;
				
				Object playerInfo = netConnection.getInfo();

				if(playerInfo == null)
					return;
				
				if(!(playerInfo instanceof PlayerController))
					return;

				PlayerController target = (PlayerController)playerInfo;
			
				if(!target.isOnline())
					return;
				
				if(target.getNetConnection() == null)
					return;
				
				GameServer.getInstance().getWorldManager().getJobObserver().addJob(GameServer.JOB_DATABASEJOB,
						new SaveJob(GameServer.getInstance().getWorldManager(),target.getPlayer(),SaveJob.SAFE_OUT_GAME_SAVE));
			
				target.close();
			}
		}
		else
		{
			worldmanager.clientMessageChain(msg);
		}
	}
	


	private void changeMail()
	{
		String accountName = msg.getBuffer().readUTF();
		String pwd = msg.getBuffer().readUTF();
		String email = msg.getBuffer().readUTF();
		
		
		ByteBuffer buffer = new ByteBuffer();
		if(!worldmanager.getDatabaseAccessor().updateEmail(accountName, pwd, email))
		{
			buffer.writeInt(ErrorCode.ALERT_MAIL_ALTER_FAIL);
			netConnection.sendMessage(new SMsg(SMsg.S_ALERT_MESSAGE,buffer));
			return;
		}	
		buffer.writeByte(1);
		buffer.writeUTF(accountName);
		buffer.writeUTF(pwd);
		netConnection.sendMessage(new SMsg(SMsg.S_PLAYER_INPUTMAIL_COMMAND,buffer));
	}

	/**
	 * 修改密码
	 */
	private void changePwd()
	{
		String accountName = msg.getBuffer().readUTF();
		String oldPwd = msg.getBuffer().readUTF();
		String newPwd = msg.getBuffer().readUTF();

		ByteBuffer buffer = new ByteBuffer();
		if(!worldmanager.getDatabaseAccessor().updatePwd(accountName, oldPwd, newPwd))
		{
			buffer.writeInt(ErrorCode.ALERT_PWD_ALTER_FAIL);
			netConnection.sendMessage(new SMsg(SMsg.S_ALERT_MESSAGE,buffer));
			return;
		}	
		netConnection.sendMessage(new SMsg(SMsg.S_PLAYER_CHANGEPWD_COMMAND,buffer));
	}

	/**
	 * 注册
	 */
	private void processReg()
	{
		String name = msg.getBuffer().readUTF();
		String pwd = msg.getBuffer().readUTF();
		
		int result = 0;
		
		if(-1 != name.indexOf("insert") 
				|| -1 != name.indexOf("select") 
				|| -1 != name.indexOf("update"))
		{
			result = -1;
		}
		if(-1 != pwd.indexOf("insert") 
			|| -1 != pwd.indexOf("select") 
			|| -1 != pwd.indexOf("update"))
		{
			result = -1;
		}
		
		DatabaseAccessor da = null;
		

		if(name.length() <= 3)
		{
			ByteBuffer buffer = new ByteBuffer();
			buffer.writeInt(ErrorCode.EXCEPTION_LOGIN_SAMENAME);	
			netConnection.sendMessage(new SMsg(SMsg.S_EXCEPTION_MESSAGE,buffer));
			return;
		}
		
		if(pwd.equals("") || pwd.length() < 6)
		{
			ByteBuffer buffer = new ByteBuffer();
			buffer.writeInt(ErrorCode.ALERT_PWD_LESS);	
			netConnection.sendMessage(new SMsg(SMsg.S_EXCEPTION_MESSAGE,buffer));
			return;
		}
		
		
		if(result == 0)
		{
			da = worldmanager.getDatabaseAccessor();
			if(1 == da.checkUserName(name, null))
			{
				ByteBuffer buffer = new ByteBuffer();
				buffer.writeInt(ErrorCode.EXCEPTION_LOGIN_SAMENAME);	
				netConnection.sendMessage(new SMsg(SMsg.S_EXCEPTION_MESSAGE,buffer));
				return;
			}	
		}
		ByteBuffer buffer = new ByteBuffer();
		buffer.writeBoolean(result == -1?false:da.regUser(name, pwd));	
		netConnection.sendMessage(new SMsg(SMsg.S_PLAYER_REG_COMMAND,buffer));
	}

	/**
	 * 玩家进入游戏
	 * @param conn
	 */
	public void processPlayerEnterGame()
	{
		Object playerObj = netConnection.getInfo();

		if(playerObj == null)
			return;

		if(!(playerObj instanceof Player))
		{
			if(playerObj instanceof PlayerController)
				return;
			else
			{
				netConnection.close();
				return;
			}
		}
		
		netConnection.setInfo(null);
		
		Player player = (Player)playerObj;
		
		//验证玩家是否在，在则T下玩家
		NetConnection targetNc = worldmanager.checkPlayerIsOnline(player.accountName);
		if(targetNc != null)
		{
			if(!targetNc.getIP().equals(netConnection.getIP())) //检查登陆的玩家ip端口都和自己一样的话才可以进入游戏
			{
				ByteBuffer buffer = new ByteBuffer(4);
				buffer.writeInt(ErrorCode.ALERT_LOGIN_OTHERPLAYERLOGIN);
				targetNc.sendMessage(new SMsg(SMsg.S_ALERT_MESSAGE,buffer));
				targetNc.close();
				
				buffer = new ByteBuffer();
				buffer.writeUTF(DC.getString(DC.SYSTEM_1));//有相同角色在游戏中
				netConnection.sendMessage(new SMsg(SMsg.S_ERROR_MESSAGE,buffer));
				netConnection.close();
				return;
			}
		}
		
		
		worldmanager.processEnterGame(player, netConnection);
		ByteBuffer buffer = new ByteBuffer(1);
		buffer.writeBoolean(true);
		netConnection.sendMessage(new SMsg(SMsg.S_PLAYER_ENTER_GAME_COMMAND,buffer));
	}
	
	/**
	 * 检测角色名是否已存在
	 * @param conn
	 * @param roleName
	 */
	public void processCheckRoleName()
	{
		String roleName = msg.getBuffer().readUTF();
		
		ByteBuffer buffer = new ByteBuffer(2);
		boolean flag = false;
		if(worldmanager.getDatabaseAccessor().checkPlayerName(roleName))
		{
			flag = true;//alert 已有相同名字
		}
		buffer.writeBoolean(flag);
		netConnection.sendMessage(new SMsg(SMsg.S_ROLENAME_REPEAT_COMMAND, buffer));
	}
	
	/**
	 * 处理玩家角色删除
	 * @param conn
	 * @param id
	 * @param accountName
	 */
	public void processPlayerRoleDelete()
	{
		Object playerObj = netConnection.getInfo();
		
		if(!(playerObj instanceof Player))
		{
			return;
		}
		
		netConnection.setInfo("delete");
		//ByteBuffer buffer = new ByteBuffer(8);
		boolean flag = false;
		
		Player player = (Player)playerObj;
		if(player.familyId != 0)
		{
			FamilyController family = worldmanager.getFamilyById(player.familyId);
			if(family != null)
			{
				PlayerController target = worldmanager.processEnterGame(player, netConnection);
				target.familyOut();
				
				worldmanager.removePlayerController(target);
				netConnection.setInfo(null);
			}
		}
		
		if(worldmanager.getDatabaseAccessor().delPlayer(player))
		{
			flag = true;
		}
		//buffer.writeBoolean(flag);
		//netConnection.sendMessage(new SMsg(SMsg.S_DELETE_ROLE_COMMAND,buffer));
	}
	
	
	/**
	 * 连接运营商正式的登录
	 * 
	 */
	private void processPlayerLoginByTrue()
	{
		/**
		 *  utf 用户名
			gameid utf
			serverid  utf
			sign utf
			time utf 
		 */
		
		boolean isActive = msg.getBuffer().readBoolean(); // true 不验证 false 需要验证
		
		String accountname = "";
		
		if(isActive)
		{
			accountname = msg.getBuffer().readUTF().trim();//帐户名
			String serverid = msg.getBuffer().readUTF();
			String sign = msg.getBuffer().readUTF();
			String time = msg.getBuffer().readUTF();	
			
			try
			{
				String eSign = MD5.getInstance().getMD5String((accountname+time+GameServer.getInstance().key).getBytes("utf-8")).toLowerCase().trim();
				if(!eSign.equals(sign))
					return;
			} 
			catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			accountname = msg.getBuffer().readUTF().trim();//帐户名
			String serverid = msg.getBuffer().readUTF();
			String sign = msg.getBuffer().readUTF();
			String time = msg.getBuffer().readUTF();	
	
			try
			{
				accountname = java.net.URLDecoder.decode(accountname,"utf-8");
			} 
			catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}
			/*
			 * Key =123456
				登录
				Sign=md5("Uname=$Uname&ServerId=$ServerId&Key=$Key&Time =$Time ")
			 */
			String data ="Uname="+accountname+"&ServerId="+serverid+"&Key="+GameServer.getInstance().key+"&Time="+time;
			String m = null;
			try
			{
				
				m = MD5.getInstance().getMD5String(data.getBytes("utf-8"));
			} 
			catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}

			if(!(m.equalsIgnoreCase(sign)))
			{
				ByteBuffer buffer = new ByteBuffer(4);
				buffer.writeInt(ErrorCode.EXCEPTION_LOGIN_ERROR);
				netConnection.sendMessage(new SMsg(SMsg.S_ALERT_MESSAGE,buffer));
				netConnection.close();
				return;
			}
		
		}
		
		if(!GMcheckProcess(accountname))
			return;

		if(netConnection.getInfo()!=null)
		{ 
			ByteBuffer buffer = new ByteBuffer(4);
			buffer.writeInt(ErrorCode.EXCEPTION_LOGIN_PLAYERNOTATGAME);
			netConnection.sendMessage(new SMsg(SMsg.S_EXCEPTION_MESSAGE, buffer));
			netConnection.close();
			return;
		}
		
		//验证玩家是否在，在则T下玩家
		NetConnection targetNc = worldmanager.checkPlayerIsOnline(accountname);
		if(targetNc != null)
		{
			ByteBuffer buffer = new ByteBuffer(4);
			buffer.writeInt(ErrorCode.ALERT_LOGIN_OTHERPLAYERLOGIN);
			targetNc.sendMessage(new SMsg(SMsg.S_ALERT_MESSAGE,buffer));
			worldmanager.onConnectionClosed(targetNc);
			targetNc.close();

			
			buffer = new ByteBuffer();
			buffer.writeUTF(DC.getString(DC.SYSTEM_1));//有相同角色在游戏中
			netConnection.sendMessage(new SMsg(SMsg.S_ERROR_MESSAGE,buffer));
			netConnection.close();
			return;
		}
		
		netConnection.setInfo("validate");

		worldmanager.getJobObserver().addJob(GameServer.JOB_DATABASEJOB, new ValidateJob(worldmanager,netConnection,accountname));
	}
	
	
	/**
	 * 处理玩家登陆
	 * @param conn
	 * @param acountname
	 */
	private void processPlayerLogin()
	{
		
		String accountname = msg.getBuffer().readUTF().trim();//帐户名
		String password = msg.getBuffer().readUTF();//密码(暂时不用)
		
		if(!GMcheckProcess(accountname))
			return;
		
		if(-1 != accountname.indexOf("insert") 
				|| -1 != accountname.indexOf("select") 
				|| -1 != accountname.indexOf("update"))
		{
			return;
		}
		if(-1 != password.indexOf("insert") 
			|| -1 != password.indexOf("select") 
			|| -1 != password.indexOf("update"))
		{
			return;
		}

		int result = worldmanager.getDatabaseAccessor().checkUserName(accountname, password);
		
		if(result == -1)
		{
			ByteBuffer buffer = new ByteBuffer(4);
			buffer.writeInt(ErrorCode.ALERT_LOGIN_FAIL);
			netConnection.sendMessage(new SMsg(SMsg.S_ALERT_MESSAGE, buffer));
			return;
		}
		
		if(result == -3)
		{
			ByteBuffer buffer = new ByteBuffer();
			buffer.writeByte(-1);
			buffer.writeUTF(accountname);
			buffer.writeUTF(password);
			netConnection.sendMessage(new SMsg(SMsg.S_PLAYER_INPUTMAIL_COMMAND, buffer));
			return;
		}
		
		if(netConnection.getInfo()!=null)
		{ 
			ByteBuffer buffer = new ByteBuffer(4);
			buffer.writeInt(ErrorCode.EXCEPTION_LOGIN_PLAYERNOTATGAME);
			netConnection.sendMessage(new SMsg(SMsg.S_EXCEPTION_MESSAGE, buffer));
			netConnection.close();
			return;
		}
		//验证玩家是否在，在则T下玩家
		NetConnection targetNc = worldmanager.checkPlayerIsOnline(accountname);
		
		if(targetNc != null)
		{
			ByteBuffer buffer = new ByteBuffer(4);
			buffer.writeInt(ErrorCode.ALERT_LOGIN_OTHERPLAYERLOGIN);
			targetNc.sendMessage(new SMsg(SMsg.S_ALERT_MESSAGE,buffer));
			worldmanager.onConnectionClosed(targetNc);
			targetNc.close();
			
			buffer = new ByteBuffer();
			buffer.writeUTF(DC.getString(DC.SYSTEM_1));//有相同角色在游戏中
			netConnection.sendMessage(new SMsg(SMsg.S_ERROR_MESSAGE,buffer));
			netConnection.close();
			return;
		}
		
		netConnection.setInfo("validate");

		worldmanager.getJobObserver().addJob(GameServer.JOB_DATABASEJOB, new ValidateJob(worldmanager,netConnection,accountname));
	}

	private boolean GMcheckProcess(String accountname)
	{
		if(GameServer.isBackSever)
		{
			Player player = new Player();
			player.accountName = accountname;
			PlayerController target = new PlayerController(player);
			
			if(!target.isGmAccount())
			{
				System.out.println(accountname+" not GM"+netConnection.getIP());
				
				if(netConnection != null)
					netConnection.close();
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 处理玩家角色创建
	 */
	private void processPlayerCreate()
	{
		ByteBuffer readBuffer = msg.getBuffer();
		
		String accountName = readBuffer.readUTF().trim();//帐户名
		String roleName = readBuffer.readUTF().trim();//角色名
		int roleSex = readBuffer.readInt(); //1 nan 0 nv性别
		int profession = readBuffer.readInt();//职业（有4种）

		if(accountName == null || accountName.equals("") ||roleName == null || roleName.equals("") )
			return;
		
		if(netConnection.getInfo() instanceof Player)
			return;
		
		String nroleName = MarkChar.replace(roleName);
		
		if(!nroleName.equals(roleName))
		{
			ByteBuffer buffer = new ByteBuffer(4);
			buffer.writeInt(ErrorCode.ALERT_NOT_USE_NAME);
			netConnection.sendMessage(new SMsg(SMsg.S_EXCEPTION_MESSAGE, buffer));
			return;
		}
		roleName = nroleName;
		
		
		if(!UseChar.isCanReg(roleName))
		{
			String fixStr = UseChar.getFixString(roleName);
			
			ByteBuffer buffer = new ByteBuffer();
			buffer.writeUTF(DC.getString(DC.SYSTEM_2)+":"+fixStr);
			netConnection.sendMessage(new SMsg(SMsg.S_ERROR_MESSAGE, buffer));
			return;
		}
		
		if(roleName.indexOf("select") !=-1 
				||roleName.indexOf("update") !=-1
				||roleName.indexOf("insert") !=-1
				||roleName.indexOf("into") !=-1
				||roleName.indexOf("delete") !=-1
				||roleName.indexOf("order") !=-1//by
				||roleName.indexOf("from") !=-1
				||roleName.indexOf("where") !=-1
				||roleName.indexOf("by") !=-1
				||roleName.indexOf("group") !=-1
				||roleName.indexOf("\\") !=-1
				||roleName.indexOf("*") !=-1
				)
		{
			ByteBuffer buffer = new ByteBuffer(1);
			buffer.writeBoolean(true);
			netConnection.sendMessage(new SMsg(SMsg.S_ROLENAME_REPEAT_COMMAND, buffer));
			return;
		}

		if(worldmanager.getDatabaseAccessor().getPlayer(accountName.trim()) !=  null) //说明已经创建过角色，不能重复创建
		{
			ByteBuffer buffer = new ByteBuffer(4);
			buffer.writeUTF(DC.getString(DC.SYSTEM_3));//请清理缓存或升级flash版本再试
			netConnection.sendMessage(new SMsg(SMsg.S_ERROR_MESSAGE, buffer));
			return;
		}
		
		if(worldmanager.getDatabaseAccessor().checkPlayerName(roleName.trim()))
		{
			ByteBuffer buffer = new ByteBuffer(1);
			buffer.writeBoolean(true);
			netConnection.sendMessage(new SMsg(SMsg.S_ROLENAME_REPEAT_COMMAND, buffer));
			return;
		}
		
		if(profession == 0)
		{
			ByteBuffer buffer = new ByteBuffer(4);
			buffer.writeInt(ErrorCode.EXCEPTION_PROFESSION_NOT_EXIST);//alert 没有这个职业
			netConnection.sendMessage(new SMsg(SMsg.S_EXCEPTION_MESSAGE, buffer));
			return;
		}
		if(roleSex != 1 && roleSex != 0)
		{
			ByteBuffer buffer = new ByteBuffer(4);
			buffer.writeInt(ErrorCode.EXCEPTION_SEX_NOT_EXIST);//alert 性别错误
			netConnection.sendMessage(new SMsg(SMsg.S_EXCEPTION_MESSAGE, buffer));
			return;
		}
		
		if(roleName.trim().length() == 0)
		{
			ByteBuffer buffer = new ByteBuffer(4);
			buffer.writeInt(ErrorCode.ALERT_ROLENAME_NULL_ERROR);//alert 角色名不能为空
			netConnection.sendMessage(new SMsg(SMsg.S_EXCEPTION_MESSAGE, buffer));
			return;
		}
		
		int roldId = worldmanager.getDatabaseAccessor().getNewPlayerId();//获取一个新的角色ID
		
		
		Player player = new Player();

		player.id = roldId;
		player.accountName = accountName;
		player.name = roleName;
		player.sex = roleSex;
		player.profession = profession;
		player.date = WorldManager.date;
		
		if(profession == 1)
			player.upProfession = 1;
		else if(profession == 2)
			player.upProfession = 3;
		else if(profession == 3)
			player.upProfession = 6;
		else if(profession == 4)
			player.upProfession = 7;
		
		player.initial(0);
		
		player.hitPoint = player.maxHitPoint;
		player.magicPoint = player.maxMagicPoint;
		
		
		
		player.setPlayerModelMotionId();

		DataFactory.getInstance().setNativity(player);
		
		player.setSkill();
		
		MailRemind.onCheckMail(new PlayerController(player), MailRemind.ONLEVELUP, 0);
		
		netConnection.setId(roldId);
		netConnection.setInfo(player);
		
		worldmanager.getJobObserver().addJob(GameServer.JOB_DATABASEJOB, new SaveJob(worldmanager,player,SaveJob.ROLE_CREATE_SAVE));
			
		//把创建好的角色信息发送给客户端
		ByteBuffer buffer = new ByteBuffer(32);
		buffer.writeInt(roldId);
		buffer.writeUTF(player.name);
		buffer.writeByte(player.sex);
		buffer.writeInt(player.upProfession);
		buffer.writeInt(player.level);
		netConnection.sendMessage(new SMsg(SMsg.S_CREATE_ROLE_COMMAND, buffer));
	}

	/**
	 * 执行合区
	 */
	private void processMerge()
	{
		if(MergeLoader.mergeDAs.length <= 0)
			return;

		if(!(netConnection.getInfo() instanceof ArrayList))
			return;

		ArrayList list = (ArrayList)netConnection.getInfo();
		
		if(list.size() <= 0)
			return;
	
		String name = msg.getBuffer().readUTF();
		String worldNUM = msg.getBuffer().readUTF().trim();
		
		
		boolean flag = false;
		
		Player player = null;
		
		for (int i = 0; i < list.size(); i++)
		{
			player = (Player)list.get(i);
			
			if(player == null)
				continue;
			
			if(name.equals(player.name))
			{
				if(player.worldNUM.isEmpty())
				{
					netConnection.close();
					return;
				}
				
				if(!(player.worldNUM.equals(worldNUM)))
					continue;
				
				flag = true;
				break;
			}
		}
		
		if(!flag)
			return;


		
		if(worldmanager.getDatabaseAccessor().getPlayer(player.accountName) !=  null) //说明已经创建过角色，不能重复创建
		{
			ByteBuffer buffer = new ByteBuffer(4);
			buffer.writeUTF(DC.getString(DC.SYSTEM_3));//请清理缓存或升级flash版本再试
			netConnection.sendMessage(new SMsg(SMsg.S_ERROR_MESSAGE, buffer));
			return;
		}
		
	
		if(worldmanager.getDatabaseAccessor().checkPlayerName(player.name))
		{
			//本区已有相同名字
			//加上区名字
			player.name = player.worldNUM+"_"+player.name;
			
			int count = 0;
			
			while(worldmanager.getDatabaseAccessor().checkPlayerName(player.name))
			{
				player.name = player.worldNUM+"_"+player.name;
				
				if(++count == 4)
					break;
			}
			
			if(count >= 4)
			{
				netConnection.close();
				return;
			}

		}
		
		MergeStart.getInstance().updatePlayer(player);

		for (int i = 0; i < list.size(); i++)
		{
			Player everyPlayer = (Player)list.get(i);
			
			if(everyPlayer == null)
				continue;
			
			
			for (int j = 0; j < MergeLoader.mergeDAs.length; j++)
			{
				if(MergeLoader.mergeDAs[j].getDbname().equals(everyPlayer.worldNUM))
				{
					Bag bag = (Bag)everyPlayer.getExtPlayerInfo("bag");
					long money = bag.money;
					bag.money = 0;
					GameServer.getInstance().getJobsObserver().
					addJob(GameServer.JOB_DATABASEJOB,new MergeSaveJob(MergeLoader.mergeDAs[j],everyPlayer));
					bag.money = money;
					break;
				}
			}
			
			if(everyPlayer.worldNUM.equals(player.worldNUM))
				continue;
			
			Bag bag = (Bag)player.getExtPlayerInfo("bag");
			Bag bag2 = (Bag)everyPlayer.getExtPlayerInfo("bag");
			
			if(bag2.money>0)
			{
				bag.money += bag2.money;
				MainFrame.println("Target "+player.name+" Money "+bag.money+" - Other "+everyPlayer.name+" Money "+bag2.money);
			}
			
		}		
		
		MailBox mb = (MailBox)player.getExtPlayerInfo("mailbox");
		
		for (int i = 0; i < MergeLoader.goodsString.length; i++)
		{
			 String []str = Utils.split(MergeLoader.goodsString[i], ":");
			 
			 if(str.length != 2)
				 continue;
			 
			 Mail mail = new Mail(DC.getString(DC.SYSTEM_4)); //游戏管理员
			 mail.setTitle(DC.getString(DC.SYSTEM_5));//系统邮件
	
		   	 Goods [] goods = DataFactory.getInstance().makeGoods(Integer.parseInt(str[0]),Integer.parseInt(str[1]));
			 if(goods != null)
			 {
				mail.addAttach(goods[0]);
			 }
			
			 mb.addMail(new PlayerController(player), mail);
		}
		
		try
		{
			OtherExtInfo oei = (OtherExtInfo)player.getExtPlayerInfo("otherExtInfo");
			oei.worldNUM = Integer.parseInt(player.worldNUM.trim());
		}
		catch(Exception e)
		{
			MainFrame.println("player.worldNUM : "+player.worldNUM+" "+e.getMessage());
		}
		
		worldmanager.getJobObserver().addJob(GameServer.JOB_DATABASEJOB,
		new SaveJob(worldmanager,player,SaveJob.MERGE_SAVE));

		ByteBuffer buffer = new ByteBuffer();
		
		buffer.writeInt(player.id);
		buffer.writeUTF(player.name);
		buffer.writeByte(player.sex);
		buffer.writeInt(player.profession);
		buffer.writeInt(player.upProfession);
		buffer.writeInt(player.level);
		buffer.writeInt(player.modelMotionId);
		if(netConnection != null)
		{
			netConnection.setId(player.id);
			netConnection.setInfo(player);
			netConnection.sendMessage(new SMsg(SMsg.S_LOGIN_COMMAND,buffer));//登陆成功并且此帐号里已经有角色，返回给客户端信息
		}

	}
	
	
	private void requestSelectMerge()
	{
		if(!(netConnection.getInfo() instanceof Player))
			return;
		
		Player player = (Player)netConnection.getInfo();
		OtherExtInfo oei = (OtherExtInfo)player.getExtPlayerInfo("otherExtInfo");
		
		if(oei.worldNUM == 0)
		{
			System.out.println("其他区没有号");
			return;
		}
		
		if(MergeLoader.mergeDAs.length == 0)
		{
			System.out.println("本区没有合区资料");
			return;
		}
		
		DatabaseAccessor sourceDA = null;
		try
		{
			for (int i = 0; i < MergeLoader.mergeDAs.length; i++)
			{
				if(Integer.parseInt(MergeLoader.mergeDAs[i].getDbname()) == oei.worldNUM)
				{
					sourceDA = MergeLoader.mergeDAs[i];
					break;
				}
			}
		}
		catch(Exception e)
		{
			for (int i = 0; i < MergeLoader.mergeDAs.length; i++)
				MainFrame.println("MergeLoader.mergeDAs[i].getDbname() "+MergeLoader.mergeDAs[i].getDbname()+" "+e.getMessage());
			return;
		}
		
		if(sourceDA == null)
			return;
		
		processPlayerRoleDelete();
		
		Player sourcePlayer = sourceDA.getPlayer(player.accountName);
		player.id = sourcePlayer.id;
		sourceDA.savePlayer(player);
		
		
		netConnection.sendMessage(new SMsg( SMsg.S_PLAYER_RESELECTMERGE_COMMAND,new ByteBuffer(1)));
		
		netConnection.setInfo("validate");
		worldmanager.getJobObserver().addJob(GameServer.JOB_DATABASEJOB, new ValidateJob(worldmanager,netConnection,player.accountName));
	}

}
