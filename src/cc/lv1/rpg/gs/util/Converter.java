package cc.lv1.rpg.gs.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import cc.lv1.rpg.gs.GameServer;

import vin.rabbit.util.Utils;

public class Converter
{
	private String outPath = "F:\\MyEclipse5\\workspace\\rpg_game\\src";
	
	private void find(File file) throws Exception
	{
		if (file.isFile())
		{
			if (file.getName().endsWith("java"))
			{
				String path = outPath+file.getAbsolutePath().substring(file.getAbsolutePath().indexOf("src")+3);
				
				FileInputStream fis = new FileInputStream(file);
				InputStreamReader isr = new InputStreamReader(fis, "UTF-8");

				char[] b = new char[fis.available()];
				isr.read(b);
				String str = new String(b);
				String []lines = Utils.split(str, "\n");
				StringBuffer buffer = new StringBuffer();
				for (int i = 0; i < lines.length; i++)
				{
					if(i == lines.length-1)
						continue;
					if(PrintFinder.isChinese(lines[i]))
					{
						StringBuffer cBuffer= new StringBuffer();
						for (int j = 0; j < lines[i].length(); j++)
						{
							char c = lines[i].charAt(j);
							if(PrintFinder.isChinese(c))
							{
								continue;
							}
							cBuffer.append(c);	
						}
						
						//System.out.println(lines[i] +"   "+cBuffer.toString());
						buffer.append(cBuffer.toString());
					}
					else
						buffer.append(lines[i]);
				}


				Utils.writeFile(path, buffer.toString().getBytes());


			}
		} 
		else
		{
			if(file.getName().equals("CVS"))
			{
				//System.out.println(file.getAbsolutePath());
			}
			else
			{
				File fs[] = file.listFiles();
				for (int i = 0; i < fs.length; i++)
				{
					find(fs[i]);
				}
			}
		}
	}
	
	
	public static void main(String[] args) throws Exception
	{
		Converter con = new Converter();

		String path = GameServer.getAbsolutePath();
		path = path.subSequence(0, path.length() - 4) + "src";

		con.find(new File(path));
		System.out.println("转换完成");
	}
	
}
