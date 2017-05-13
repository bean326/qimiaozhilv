package cc.lv1.rpg.gs.util;

import java.io.*;
import java.net.*;
import vin.rabbit.util.Utils;

public class FileClient implements Runnable
{
	
	private Socket clientSocket;
	
	private int day;
	
	private long nextDayTime;
	
	private FileClient()
	{
	}
	
	
	private void initSocket()
	{
		try
		{
			clientSocket = new Socket("222.214.218.185",5200);
			System.out.println("'SOCKET'\u306E\u30A2\u30AF\u30BB\u30B9\u306F\u6210\u529F\u3057\u307E\u3057\u305F!");
		} 
		catch (Exception e)
		{
			System.out.println(e.getMessage()+" 'SOCKET'\u306E\u30A2\u30AF\u30BB\u30B9\u306F\u5931\u6557\u3057\u307E\u3057\u305F!");
		}
	}
	
	private FileInputStream getFileInputStream() throws Exception
	{
		File file = new File("");
		file = new File(file.getAbsolutePath().substring(0,3)+"Backup");

		if(!file.isDirectory())
		{
			file.mkdir();
			//System.out.println("Create DIR '"+file.getAbsolutePath()+"' and Backup is not exits");
			return null;
		}
		
		File [] files = file.listFiles();

		if(files.length == 0)
		{
			System.out.println("\u30D0\u30C3\u30AF\u30A2\u30C3\u30D7\u306E\u66F8\u985E\u305F\u3061\u306F\u5B58\u5728\u3057\u306A\u3044\u306E\u3067\u4ECA\u65E5\u306E\u30D0\u30C3\u30AF\u30A2\u30C3\u30D7\u304C\u4E2D\u6B62\u3057\u307E\u3057\u305F");
			return null;
		}
		if(files.length == 1)
		{
			System.out.println("\u9078\u629E:"+files[0].getName());
			return new FileInputStream(files[0]);
		}
		else
		{
			for (int i = 0; i < files.length; i++)
			{
				for(int j = files.length-1 ; j > i ; j --)
			    {
					String n1= ((File)files[j-1]).getName();
					String [] sns1= Utils.split(Utils.split(n1, ".")[0], "_");
					long day1 = Long.parseLong(sns1[sns1.length -1]);
					
					String n2= ((File)files[j]).getName();
					String [] sns2= Utils.split(Utils.split(n2, ".")[0], "_");
					long day2 = Long.parseLong(sns2[sns2.length -1]);
					
					  if(day1 < day2)
					  {
					  	File tmp = files[j];
					  	files[j] = files[j-1];
					  	files[j-1] = tmp;
				      }
			    }
			}
			
			System.out.println("\u9078\u629E:"+files[0].getName());
			return new FileInputStream(files[0]);
		}
	
	}

	public void run()
	{
		InputStreamReader reader = new InputStreamReader(System.in);
		BufferedReader buffer = new BufferedReader(reader);
		int i = 0;
		try
		{
			System.out.println("\u30D0\u30C3\u30AF\u30A2\u30C3\u30D7\u306E\u6642\u9593\u3092\u8F38\u5165\u3057\u3066\u304F\u3060\u3055\u3044   \u4F8B\u3048\u3070: \u30A2\u30E9\u30D3\u30A23\u6642\u304B\u3089\u59CB\u307E\u308A\u307E\u3059");
			String str = buffer.readLine();
			i = Integer.parseInt(str.trim());
			System.out.println("\u6BCE\u65E5\u7FCC\u65E5\u306E"+i+"\u6642\u9593\u306B \u304D\u3063\u3068\u30D0\u30C3\u30AF\u30A2\u30C3\u30D7\u3057\u3066\u3044\u307E\u3059");
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage());
			System.exit(0);
		}
		
		
		while(true)
		{
			process();
			
			initTime();
			
			Utils.sleep((int) nextDayTime+(1000*60*60*i));
		}
	}
	
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
	
	private void process()
	{
		try
		{
			FileInputStream fis = getFileInputStream();

			if(fis == null)
				return;
	
			initSocket();
			OutputStream os = clientSocket.getOutputStream();
			
			byte[] buf = new byte[1024];
			
			int totalCount = 0;
			
			while (true)
            {
                int read = 0;
                if (fis != null)
                    read = fis.read(buf);
                if (read == -1)
                    break;
                else
                	totalCount += read;

                os.write(buf, 0, read);
                os.flush();
            }
			
			fis.close();
			os.close();
			clientSocket.close();
			
			System.out.println("\u66F8\u985E\u306E\u9577\u3055:"+totalCount+"\n \u305D\u306E\u307E\u307E\u5B8C\u6210\u3057\u307E\u3057\u305F \n"+Utils.getCurrentTime());
			System.out.println("------------------------------------");
		} 
		catch (Exception e)
		{
			System.out.println(e.getMessage()+"\n \u305D\u306E\u307E\u307E\u5931\u6557\u3057\u307E\u3057\u305F \n"+clientSocket +" "+Utils.getCurrentTime());
			System.out.println("------------------------------------");
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		FileClient fileClient = new FileClient();
		new Thread(fileClient).start();
	}



}
