package cc.lv1.rpg.gs.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.i.Map;
import vin.rabbit.util.collection.impl.ArrayList;
import vin.rabbit.util.collection.impl.HashMap;
import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.controller.PVEController;
import cc.lv1.rpg.gs.entity.impl.BoxDropProp;
import cc.lv1.rpg.gs.gui.MainFrame;


public class PrintFinder
{
	
	private static int linecount;
	
	private static List list = new ArrayList();

	private void find(File file,String key) throws Exception
	{
			if(file.isFile())
			{
				if(file.getName().endsWith("java"))
				{
					
					FileInputStream fis = new FileInputStream(file);
					InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
					
					char []b = new char[fis.available()];
					isr.read(b);
					String str = new String(b);
					
					String lines [] = Utils.split(str, "\n");
					
					String name = "";
					for (int i = 0; i < lines.length; i++)
					{
						if(lines[i].indexOf(key) != -1)
						{
							if(lines[i].trim().startsWith("//"))
								continue;
							linecount++;
							System.err.println(file.getName() +"  "+(i+1)+" 行      " + lines[i].trim());
							if(!file.getName().equals(name))
								list.add("className:"+file.getName()+"-------------------------"+Utils.LINE_SEPARATOR);
							list.add(file.getName() +"  "+(i+1)+" 行      " + lines[i].trim());
							name = file.getName();
						}
					}
				}
			}
			else
			{
				File fs[] = file.listFiles();
				for (int i = 0; i < fs.length; i++)
				{
					if("util".equals(file.getName()))
						continue;
					find(fs[i],key);
				}
			}
	}

	public static boolean isChinese(String strName)
	{
		char[] ch = strName.toCharArray();
		for (int i = 0; i < ch.length; i++)
		{
			char c = ch[i];
			if (isChinese(c) == true)
			{
				return true;
			}
		}
		return false;
	}

	public static boolean isChinese(char c)
	{
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
		|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
		|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
		|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
		|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
		|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS)
		{
			return true;
		}
		return false;
	}
	
	public static void test(List list)
	{
		StringBuffer sb = new StringBuffer();
		Map map = new HashMap();
		for (int i = 0; i < list.size(); i++) 
		{
			String str = list.get(i).toString();
			if(str.indexOf("className") != -1 && str.indexOf("-------") != -1)
			{
				sb.append(Utils.LINE_SEPARATOR+str);
				map = new HashMap();
			}
			else
			{
				Pattern p = Pattern.compile("(.)\"(.)u(.*)\"(.)");
			    Matcher m = p.matcher(str);  
			    StringBuffer s = new StringBuffer();
			    while(m.find())
			    {
			    	s.append(m.group());
			    }
			    String[] strs = Utils.split(s.toString(), "\"");
			    s = new StringBuffer();
			  
			    for (int j = 0; j < strs.length; j++) 
			    {
					if(strs[j].indexOf("\\u") == -1)
						continue;
					if(map.get(strs[j]) == null)
						s.append(Code.toChinese(strs[j])+Utils.LINE_SEPARATOR);
					map.put(strs[j], "");
				}
			    sb.append(s.toString());
			}
		}
	 	
	    System.out.println(sb.toString());
	    Utils.writeFile("E:/workspace/rpg_game/bin/logs/words2.txt", sb.toString().getBytes());
	}
	
	
	public void createIndex()
	{
//		String[] strs = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","6"
//				,"S","T","U","V","W","X","Y","Z","a","b","c","d","e","f","g","h","i","j","k","l","7"
//				,"m","n","o","p","q","r","s","t","u","v","w","x","y","z","0","1","2","3","4","5","8","9"};
//		HashMap map = new HashMap();
//		StringBuffer data = new StringBuffer();
//		for (int i = 0; i < 3000; i++)
//		{
//			StringBuffer sb = new StringBuffer("2011");
//			int random = (int) (Math.random() * strs.length);
//			sb.append(strs[random]);
//			random = (int) (Math.random() * strs.length);
//			sb.append(strs[random]);
//			random = (int) (Math.random() * strs.length);
//			sb.append(strs[random]);
//			random = (int) (Math.random() * strs.length);
//			sb.append(strs[random]);
//			random = (int) (Math.random() * strs.length);
//			sb.append(strs[random]);
//			random = (int) (Math.random() * strs.length);
//			sb.append(strs[random]);
//			while(map.get(sb.toString()) != null)
//			{
//				System.out.println("已经有相同的序列号:"+sb.toString());
//				sb = new StringBuffer("2011");
//				random = (int) (Math.random() * strs.length);
//				sb.append(strs[random]);
//				random = (int) (Math.random() * strs.length);
//				sb.append(strs[random]);
//				random = (int) (Math.random() * strs.length);
//				sb.append(strs[random]);
//				random = (int) (Math.random() * strs.length);
//				sb.append(strs[random]);
//				random = (int) (Math.random() * strs.length);
//				sb.append(strs[random]);
//				random = (int) (Math.random() * strs.length);
//				sb.append(strs[random]);
//				continue;
//			}
//			map.put(sb.toString(), sb.toString());
//			
//			data.append(sb.toString());
//			data.append(Utils.LINE_SEPARATOR);
//		}
//		Utils.writeFile("E:/study/java/index.txt", data.toString().getBytes());
		
		String str = Utils.readFile2("E:/study/java/index.txt");
		String strs[]= Utils.split(str, Utils.LINE_SEPARATOR);
		HashMap map = new HashMap();
		for (int i = 0; i < strs.length; i++) 
		{
			
			map.put(strs[i], strs[i]);
		}
	}

	public static void main(String[] args) throws Exception
	{
		PrintFinder pf = new PrintFinder();

//		String path = GameServer.getAbsolutePath();
//
//
//		path = path.subSequence(0, path.length() - 4) + "src";
//
//		String key = "StoryEvent";
//
//		pf.find(new File(path),key);
		
//		pf.createIndex();
		ByteBuffer buffer = new ByteBuffer();
//		buffer.writeUTF("在");
		System.out.println("DD".getBytes().length);
	}

}
