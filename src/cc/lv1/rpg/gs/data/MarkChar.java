package cc.lv1.rpg.gs.data;

import vin.rabbit.util.collection.impl.ArrayList;


public class MarkChar
{
	
	private static ArrayList mcList = new ArrayList();

	public static void putMarkChar(String markChar)
	{
		mcList.add(markChar);
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
		int size = mcList.size();
		for (int i = 0; i < size; i++)
		{
			s = (String)mcList.get(i);

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
	
}
