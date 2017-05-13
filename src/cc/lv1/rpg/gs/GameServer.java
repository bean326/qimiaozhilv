package cc.lv1.rpg.gs;

import vin.rabbit.net.AppMessage;
import vin.rabbit.net.Flash843NetServer;
import vin.rabbit.net.SMNetServer;
import vin.rabbit.net.abs.NetConnection;
import vin.rabbit.net.abs.NetServer;
import vin.rabbit.net.i.IConntionIOListener;
import vin.rabbit.net.i.IServerListener;
import vin.rabbit.net.job.JobObserver;
import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.Map;
import vin.rabbit.util.collection.impl.HashMap;
import cc.lv1.rpg.gs.dao.DatabaseAccessor;
import cc.lv1.rpg.gs.entity.RPGameObject;
import cc.lv1.rpg.gs.gui.MainFrame;
import cc.lv1.rpg.gs.load.i.ILoader;
import cc.lv1.rpg.gs.load.impl.DBInfoLoader;
import cc.lv1.rpg.gs.load.impl.DynamicDataLoader;
import cc.lv1.rpg.gs.load.impl.LocalDataLoader;
import cc.lv1.rpg.gs.load.impl.MergeLoader;
import cc.lv1.rpg.gs.load.impl.ServerInfoLoader;
import cc.lv1.rpg.gs.net.impl.AvgAndMaxJob;
import cc.lv1.rpg.gs.net.impl.CMsgJob;
import cc.lv1.rpg.gs.net.impl.SaveWorldJob;
import cc.lv1.rpg.gs.net.impl.ShopCenterUpdateJob;
import cc.lv1.rpg.gs.net.impl.UpdateJob;

public class GameServer extends RPGameObject implements IConntionIOListener,IServerListener,Runnable
{  
	/**
	 * 是否自动启服务器
	 */
	public static boolean isAutoStart = false;
	
	public static final int OVERTIME = 3000;
	
	public String key;
	
	private static GameServer gameServer = null;
	
	private static String absolutePath = null;

	private static boolean running = false; 
	
	public static boolean isBackSever;
	
	public static final int JOB_DATABASEJOB = 0;
	
	public static final int JOB_GAME1 = 1;

	public static final int JOB_GAME2 = 2;
	
	public static final int JOB_COUNT = 3;
	
	
	private ILoader loader;
	
	private NetServer netServer;
	
	private JobObserver jobsObserver;
	
	private DatabaseAccessor databaseAccessor;
	
	private WorldManager worldManager = new WorldManager();

	
	public int maxConnection;
	
	public int port;
	
	

	private GameServer()
	{
	}
	
	public static boolean isRunning()
	{
		return running;
	}
	
	public static GameServer getInstance()
	{
		if(gameServer == null)
			gameServer = new GameServer();
		return gameServer;
	}
	
	public static String getAbsolutePath()
	{
		if(absolutePath==null)
		{
			absolutePath = GameServer.class.getResource("/").toString();
			absolutePath = absolutePath.substring(6);
			absolutePath = "/"+absolutePath;
		}
		return absolutePath;
	}
	
	private void initServerInfo()
	{
		loader = new ServerInfoLoader();
		loader.setAttachment(this);
		loader.loading();
	}
	
	private void initDBInfo()
	{
		loader = new DBInfoLoader();
		loader.setAttachment(this);
		loader.loading();
	}
	
	private void initLocalData()
	{
		loader = new LocalDataLoader();
		loader.setAttachment(this);
		loader.loading();
	}
	
	private void initMerge()
	{
		loader = new MergeLoader();
		loader.setAttachment(this);
		loader.loading();
	}
	
	public void initDynamicData()
	{
		loader = new DynamicDataLoader();
		loader.setAttachment(this);
		loader.loading();
	}
	
	private void initAttachment()
	{
		NetServer flashServer = new Flash843NetServer();
		flashServer.start();
	}
	
	private void initNetServer()
	{
		netServer = new SMNetServer();
		netServer.setGameServer(this);
		netServer.addServerListener(this);
		netServer.setPort(port);
		netServer.setMaxConnection(maxConnection);
		netServer.setSameIpConnectCount(12);
	}
	
	private void initJobServer()
	{
		jobsObserver = new JobObserver(GameServer.JOB_COUNT);
		jobsObserver.setQueueTimeOut(GameServer.JOB_DATABASEJOB, 1500); 
		jobsObserver.setName(GameServer.JOB_DATABASEJOB, "DB1");
		jobsObserver.setQueueTimeOut(GameServer.JOB_GAME1, 600);
		jobsObserver.setName(GameServer.JOB_GAME1, "GS1");
		jobsObserver.setQueueTimeOut(GameServer.JOB_GAME2, 1000);
		jobsObserver.setName(GameServer.JOB_GAME2, "GS2");
		jobsObserver.setNetServer(netServer);
	}
	
	private void initGameWorld()
	{
		worldManager.setGameServer(this);
		worldManager.setDatabaseAccessor(databaseAccessor);
		worldManager.setJobObserver(jobsObserver);
	}
	
	public void initial()
	{
		MainFrame.println("initial GameServer...");
		
		initServerInfo();
		initDBInfo();
		
		initAttachment();
		
		initLocalData();
		initDynamicData();
		
		initNetServer();
		initJobServer();
		
		initGameWorld();
		
		initMerge();
	}
	
	public void initialOnBackServer()
	{
		MainFrame.println("initial GameServer...");
		
		initServerInfo();
		initDBInfo();

		initLocalData();
		initDynamicData();
		
		initNetServer();
		initJobServer();
		
		initGameWorld();
		
		initMerge();
	}
	
	public void initialWithoutNetServer()
	{
		MainFrame.println("initial GameServer...");
		
		initServerInfo();
		initDBInfo();

		initLocalData();
		
		initJobServer();
		initGameWorld();
		
		worldManager.setGameServer(this);
		worldManager.setJobObserver(jobsObserver);
		worldManager.setDatabaseAccessor(databaseAccessor);
		
		initMerge();
	}



	public void start()
	{
		databaseAccessor.loadWorldInfo(worldManager);
		 
		running = true;
		netServer.start();
		new Thread(jobsObserver,"JobTimeOut").start();
		
		WorldManager.init();
		
		if(!GameServer.isBackSever)
			new Thread(this,"GameWorld").start();
		
		MainFrame.println("Server is started");
		
	}
	
	public void stop()
	{
		if (!running)
			return;
		
		databaseAccessor.createOrUpdateWorldInfo(worldManager);
		
		netServer.stop();
		jobsObserver.stop();
		running = false;
		
		
		MainFrame.println("Server is stoped");
	}
	
	public static long totalMsgCount;
	
	public void run()
	{

		long checkCenterTime = 1000*60*60;
		long centerTime = 0;
		long timeMillis = 0;
		
		Utils.sleep(3000);
		
		while(running)
		{
			timeMillis = System.currentTimeMillis();
			
			Utils.sleep(500);	
			jobsObserver.addJob(GameServer.JOB_GAME1, new UpdateJob(worldManager,timeMillis));
			
			if(timeMillis > centerTime +checkCenterTime)
			{
				if(centerTime != 0)
				{
					//刷新寄卖中心
					jobsObserver.addJob(GameServer.JOB_GAME1, new ShopCenterUpdateJob(timeMillis));

					//保存游戏世界
					jobsObserver.addJob(GameServer.JOB_DATABASEJOB, new SaveWorldJob(worldManager));
					
					//保存当天玩家数量信息
					jobsObserver.addJob(GameServer.JOB_DATABASEJOB, new AvgAndMaxJob(worldManager));
				}
				
				centerTime = timeMillis;
			}
			
		}

	}
	
	public void onConnectionOpened(NetConnection conn)
	{
		conn.setLastReadTime(System.currentTimeMillis());
		worldManager.onConnectionOpened(conn);
	}
	
	public void onConnectionClosed(NetConnection conn)
	{
		worldManager.onConnectionClosed(conn);
	}


	public void onMessageArrived(AppMessage msg)
	{
		if(msg == null)
			return;


//		System.out.println("收到客户端消息类型："+Integer.toHexString(msg.getType()));




		GameServer.totalMsgCount++;
		
		
		NetConnection netConnection = (NetConnection)msg.getSource();
		
		if(netConnection != null)
		{
			Object obj = netConnection.getAttachment();
			if(obj == null)
			{
				Map map = new HashMap();
				map.put(msg.getType(), 1);
				netConnection.setAttachment(map);
			}
			else
			{
				if(obj instanceof Map)
				{
					Map map = (Map)obj;
					Integer count = (Integer)map.get(msg.getType());
					if(count == null)
					{
						map.put(msg.getType(), 1);
					}
					else
					{
						map.put(msg.getType(), count+1);
					}
				}
			}
		}

		jobsObserver.addJob(GameServer.JOB_GAME1, new CMsgJob(worldManager,msg));
	}

	public void setDatabaseAccessor(DatabaseAccessor databaseAccessor)
	{
		this.databaseAccessor = databaseAccessor;
	}
	
	public DatabaseAccessor getDatabaseAccessor()
	{
		return databaseAccessor; 
	}

	public JobObserver getJobsObserver()
	{
		return jobsObserver;
	}

	public WorldManager getWorldManager()
	{
		return worldManager;
	}

}
