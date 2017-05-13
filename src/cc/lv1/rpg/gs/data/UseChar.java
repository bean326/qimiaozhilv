package cc.lv1.rpg.gs.data;

import java.io.UnsupportedEncodingException;

import vin.rabbit.util.collection.impl.ArrayList;

public class UseChar
{
	private static ArrayList useList = new ArrayList();

	public static void putUseChar(String markChar)
	{
		useList.add(markChar);
	}
	
	public static int size()
	{
		return useList.size();
	}
	
	
	/**
	 * 能否注册
	 * @param str
	 * @return
	 */
	public static boolean isCanReg(String str)
	{
		if(str == null || str.length() <= 0)
			return false;
		
		if(DataFactory.COMPLEX.equals(DataFactory.fontVer))
			return true;
		
		for (int i = 0; i < str.length(); i++)
		{
			if(!isInCharBox(str.charAt(i)+""))
			{
				return false;
			}
		}
		return true;
	}
	
	private static boolean isInCharBox(String str)
	{
		int size = useList.size();
		try{
		for (int i = 0; i < size; i++)
		{
			String s1 = new String(useList.get(i).toString().getBytes(), "UTF-8");   
			String s2 = new String(str.trim().getBytes(), "UTF-8");   
			if(s1.equals(s2))
				return true;
		}
		}catch(Exception e){}
		
		return false;
	}
	
	
	
	public static String replace(String source)
	{
		if(source== null || source.equals(""))
			return "";
		return check(source);
	}
	private static String check(String source)
	{
		String newSource = source;
		String s = "";
		int size = useList.size();
		for (int i = 0; i < size; i++)
		{
			s = (String)useList.get(i);
			
			if(source.indexOf(s) != -1)
			{
				StringBuffer sb = new StringBuffer();
				for (int j = 0; j < s.length(); j++)
				{
					sb.append("*");
				}
				newSource = newSource.replace(s,sb.toString());
			}
		}
		return newSource;
	}
	
	/**
	 * 取得不能使用的字符
	 * @param source
	 * @return
	 */
	public static String getFixString(String source)
	{
		String newSource = source;
		String s = "";
		int size = useList.size();
		for (int i = 0; i < size; i++)
		{
			s = (String)useList.get(i);
			
			if(source.indexOf(s) != -1)
			{
				StringBuffer sb = new StringBuffer();
				for (int j = 0; j < s.length(); j++)
				{
					sb.append("");
				}
				newSource = newSource.replace(s,sb.toString());
			}
		}
		return newSource;
	}
	
}
