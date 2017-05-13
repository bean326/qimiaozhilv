package cc.lv1.rpg.gs.util;

import java.io.*;
import java.net.*;
import java.text.*;
import vin.rabbit.util.Utils;


/**
 * 文件备份简单服务
 * @author dxw
 *
 */
public class FileServer implements Runnable
{
	
	private static FileServer fileServer = null;;
	
	private ServerSocket serverSocket = null;
	
	private StringBuffer logs = new StringBuffer();
	
	private FileServer()
	{
		try
		{
			serverSocket = new ServerSocket(5200);
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static FileServer getInstance()
	{
		if(fileServer == null)
			fileServer = new FileServer();
		return fileServer;
	}

	
	public void go()
	{
		System.out.println("File server is started");
		logs.append("File server is started"+Utils.LINE_SEPARATOR);
		
		while(true)
		{
			Socket clientSocket = null;
			try
			{
				clientSocket =	serverSocket.accept();
				
				System.out.println(clientSocket+" : accept ");
				logs.append(clientSocket+" : accept "+Utils.LINE_SEPARATOR);
				
				FileConnection fileConnection = new FileConnection(serverSocket,clientSocket);
				Thread thread = new Thread(fileConnection);
				thread.start();
			} 
			catch (IOException e)
			{
				System.out.println("Server Received Fail : "+clientSocket);
				System.out.println(e.getMessage());
				System.out.println("---------------------------------------");
				
				logs.append("Server Received Fail : "+clientSocket+Utils.LINE_SEPARATOR);
				logs.append(e.getMessage()+Utils.LINE_SEPARATOR);
				logs.append("---------------------------------------"+Utils.LINE_SEPARATOR);
			}
		}
	}
	

	private void checkDelete()
	{
		
		File file = new File("");
		file = new File(file.getAbsolutePath().substring(0,3)+"BackAll");
		if(!file.isDirectory())
		{
			file.mkdirs();
			return;
		}
		
		File [] ipfiles = file.listFiles();
		
		if(ipfiles == null)
			return;
		
		for (int k = 0; k < ipfiles.length; k++)
		{
			File []files = ipfiles[k].listFiles();

			for (int i = 0; i < files.length; i++)
			{
				for(int j = files.length-1 ; j > i ; j --)
			    {
					String n1= ((File)files[j-1]).getName();
					String [] sns1= Utils.split(n1, ".");
					long day1 = Long.parseLong(sns1[0]+sns1[1]+sns1[2]);
					
					String n2= ((File)files[j]).getName();
					String [] sns2= Utils.split(n2, ".");
					long day2 = Long.parseLong(sns2[0]+sns2[1]+sns2[2]);
					
					  if(day1 < day2)
					  {
					  	File tmp = files[j];
					  	files[j] = files[j-1];
					  	files[j-1] = tmp;
				      }
			    }
			}
			
			
			
			for (int z = 0; z < files.length; z++)
			{
				
				if(z == 0)
				{
					logs.append("\u4FDD\u7559"+ipfiles[k].getName()+"\u76EE\u5F55\u4E0B\u7684"+files[z].getName());
					System.out.println("\u4FDD\u7559"+ipfiles[k].getName()+"\u76EE\u5F55\u4E0B\u7684"+files[z].getName());
					continue;
				}
				
				boolean b = files[z].delete();
				logs.append(b+"\u5220\u9664"+ipfiles[k].getName()+"\u76EE\u5F55\u4E0B\u7684"+files[z].getName());
				System.out.println(b+"\u5220\u9664"+ipfiles[k].getName()+"\u76EE\u5F55\u4E0B\u7684"+files[z].getName());

			}
		}
		

		
		

	}
	
	public void run()
	{
		
		while(true)
		{
			initTime();
			checkDelete();
			
			Utils.sleep((int) (nextDayTime+1000*60*60));
			saveTextByFileName(logs.toString(),"backupall");
			logs = new StringBuffer();
		}
	}
	
	public void saveTextByFileName(String data,String fileName)
	{
		File file = new File(getAbsolutePath()+"logs");
		if(!file.isDirectory())
		{
			file.mkdir();
		}
		
		String date = getTypeTime("yyyyMMdd", System.currentTimeMillis());
		Utils.writeFile(getAbsolutePath()+"logs/"+fileName+"_"+date+".txt",data.getBytes());
	}
	
	static String absolutePath = null;
	
	public static String getAbsolutePath()
	{
		if(absolutePath==null)
		{
			absolutePath = FileServer.class.getResource("/").toString();
			absolutePath = absolutePath.substring(6);
			absolutePath = "/"+absolutePath;
		}
		return absolutePath;
	}
	
	public static String getTypeTime(String type,long time)
	{
		SimpleDateFormat sdf = new SimpleDateFormat(type);
		return sdf.format(time);
	}
	
	private int day;
	
	private long nextDayTime;
	
	public void initTime()
	{
		long time = System.currentTimeMillis();
		day = (byte)Utils.getCurrentDate(time);
		nextDayTime = 0;
		for (long i = 0; i < Long.MAX_VALUE; i+=60000)
		{
			int nextDate = Utils.getCurrentDate(time+i);
			
			if(nextDate != day)
			{
				nextDayTime = time = i;
				break;
			}
		}
	}
	
	class FileConnection implements Runnable
	{
		
		private ServerSocket serverSocket;
		
		private Socket clientSocket;
		
		public FileConnection(ServerSocket serverSocket, Socket clientSocket)
		{
			this.serverSocket = serverSocket;
			this.clientSocket = clientSocket;
			
		}

		private FileOutputStream getOutputStream() throws Exception
		{

			File file = new File("");
			file = new File(file.getAbsolutePath().substring(0,3)+"BackAll\\"+clientSocket.getInetAddress());
			if(!file.isDirectory())
			{
				file.mkdirs();
			}
			file = new File(file.getAbsoluteFile()+"\\"+Utils.split(Utils.getCurrentTime().toString()," ")[0]);

			return new FileOutputStream(file);
		}
		
		
		
		
		public void run()
		{
			try
			{
				InputStream is = clientSocket.getInputStream();

				FileOutputStream fos = getOutputStream();

				byte buff [] = new byte[1024];
				int totalCount = 0;
				while(true)
				{
					int read= is.read(buff);
					
					if(read == -1)
						break;
					else
						totalCount += read;
					
					fos.write(buff,0,read);
				}
				
				is.close();
				fos.close();
				clientSocket.close();
				
				System.out.println("Total file length : "+totalCount+" Server Receive finished : "+clientSocket+" "+Utils.getCurrentTime());
				System.out.println("---------------------------------------");
				
				
				logs.append("Total file length : "+totalCount+" Server Receive finished : "+clientSocket+" "+Utils.getCurrentTime()+Utils.LINE_SEPARATOR);
				logs.append("---------------------------------------"+Utils.LINE_SEPARATOR);
			} 
			catch (Exception e)
			{
				System.out.println(e.getMessage()+" Server Receive exception : "+clientSocket+" "+Utils.getCurrentTime());
				System.out.println("---------------------------------------");
				
				logs.append(e.getMessage()+" Server Receive exception : "+clientSocket+" "+Utils.getCurrentTime()+Utils.LINE_SEPARATOR);
				logs.append("---------------------------------------"+Utils.LINE_SEPARATOR);
			}
			
		}
		
	}
	
	public static void main(String[] args)
	{
		FileServer fileServer = FileServer.getInstance();
		new Thread(fileServer).start();
		fileServer.go();
		
	}


}
