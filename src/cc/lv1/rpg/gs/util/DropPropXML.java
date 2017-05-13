package cc.lv1.rpg.gs.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.impl.ArrayList;

public class DropPropXML 
{

	
	public static void main(String[] args) {
		String inPath = "E:\\util\\dropPropIn\\monsterDropProp.txt";
		String outPath = "E:\\util\\dropPropOut\\";
		String task = "E:\\util\\dropPropIn\\taskDropProp.txt";
		String boss = "E:\\util\\dropPropIn\\bossDropProp.txt";
		String gold = "E:\\util\\dropPropIn\\goldBox.txt";
		try {
			File file1 = new File(inPath);
			if(file1.exists())
				DropPropXML.writeXML(inPath, outPath,"monsterDropProp.txt");
			
			File file2 = new File(task);
			if(file2.exists())
				DropPropXML.writeXML(task, outPath,"taskDropProp.txt");
			
			File file3 = new File(boss);
			if(file3.exists())
				DropPropXML.writeXML(boss, outPath,"bossDropProp.txt");
			
			File file4 = new File(gold);
			if(file4.exists())
				DropPropXML.writeXML(gold, outPath,"goldBox");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void goldBoxXML(String inPath,String outPath,String newFileName) throws IOException
	{
		StringBuffer sb = new StringBuffer();
		File file = new File(inPath);
		if(!file.exists())
			return;
//		FileReader fr = new FileReader(file);
//		BufferedReader br = new BufferedReader(fr);
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GBK"));
		String line;
		String s1 = br.readLine();
		String s2 = br.readLine();
		String[] str = Utils.split(s1, "\t");
		sb.append(str[0]+"\t"+str[1]+"\t"+str[2]+"\t"+str[3]+"\t"+str[5]+Utils.LINE_SEPARATOR);
		str = Utils.split(s2, "\t");
		sb.append(str[0]+"\t"+str[1]+"\t"+str[2]+"\t"+str[3]+"\t"+str[5]+Utils.LINE_SEPARATOR);
		List list = new ArrayList();
		while((line = br.readLine()) != null)
		{
			list.add(line);
		}
		for (int i = 0; i < list.size(); i++) 
		{
			str = Utils.split(list.get(i).toString(), "\t");
			sb.append(str[0]);
			sb.append("\t");
			sb.append(str[1]);
			sb.append("\t");
			sb.append(str[2]);
			for (int j = 3; j < str.length; j++) 
			{
				str[j] = str[j].replace(" ", "");
				str[j] = str[j].replace("\t", "");
				if(j%3==0 && !str[j].isEmpty() && !"".equals(str[j]) && j > 3)
				{
					str[3] += ":" + str[j];
				}
			}
			for (int j = 5; j < str.length; j++) 
			{
				str[j] = str[j].replace(" ", "");
				str[j] = str[j].replace("\t", "");
				if((j-2)%3==0 && !str[j].isEmpty() && !"".equals(str[j]) && j > 5)
				{
					str[5] += ":" + str[j];
				}
			}
			sb.append("\t");
			sb.append(str[3]);
			sb.append("\t");
			sb.append(str[5]);
			if(i != list.size()-1)
				sb.append(Utils.LINE_SEPARATOR);
		}
		write(sb,outPath,newFileName);
	}

	
	public static void monsterDropPropXML(String inPath,String outPath,String newFileName) throws IOException
	{
		String nnn = System.getProperty("line.separator");
		StringBuffer sb = new StringBuffer();
		File file = new File(inPath);
		if(!file.exists())
			return;
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String line;
		String s1 = br.readLine();
		String s2 = br.readLine();
		String[] str = Utils.split(s1, "\t");
		sb.append(str[0]+"\t"+str[2]+"\t"+str[3]+"\t"+str[4]+"\t"+str[5]+"\t"+str[6]+"\t"+str[7]+"\t"+str[8]+"\t"+str[9]+nnn);
		str = Utils.split(s2, "\t");
		sb.append(str[0]+"\t"+str[2]+"\t"+str[3]+"\t"+str[4]+"\t"+str[5]+"\t"+str[6]+"\t"+str[7]+"\t"+str[8]+"\t"+str[9]+nnn);
		List list = new ArrayList();
		while((line = br.readLine()) != null)
		{
			list.add(line);
		}
		for (int i = 1; i < list.size(); i+=2) {
			str = Utils.split(list.get(i).toString(), "\t");
			if(str[2].isEmpty())
				str[2] = "0";
			if(str[3].isEmpty())
				str[3] = "0";
			sb.append(str[0]+"\t"+str[2]+"\t"+str[3]+"\t"+str[4]+"\t"+str[5]);
			for (int j = 5; j < str.length; j++) 
			{
				if(str[j].isEmpty())
					str[j] = "0";
			}
			for (int k = 6; k < str.length-6; k+=4)
			{
				str[6] += ":"+str[k+4];
			}
			for (int k = 7; k < str.length-5; k+=4)
			{
				str[7] += ":"+str[k+4];
			}
			for (int k = 8; k < str.length-4; k+=4)
			{
				str[8] += ":"+str[k+4];
			}
			for (int k = 9; k < str.length-3; k+=4)
			{
				str[9] += ":"+str[k+4];
			}
			sb.append("\t"+str[6]);
			sb.append("\t"+str[7]);
			sb.append("\t"+str[8]);
			sb.append("\t"+str[9]);
			if(i != list.size()-1 && i != list.size()-2)
				sb.append(nnn);
		}
//		System.out.println(sb);
		write(sb,outPath,newFileName);
	}
	
	public static void writeXML(String inPath,String outPath,String newFileName) throws IOException
	{
		if("goldBox".equals(newFileName))
		{
			goldBoxXML(inPath, outPath,newFileName);
			return;
		}
		if("monsterDropProp".equals(newFileName))
		{
			monsterDropPropXML(inPath, outPath,newFileName);
			return;
		}
		String nnn = System.getProperty("line.separator");
		StringBuffer sb = new StringBuffer();
		File file = new File(inPath);
		if(!file.exists())
			return;
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String line;
		String s1 = br.readLine();
		String s2 = br.readLine();
		String[] str = Utils.split(s1, "\t");
		sb.append(str[0]+"\t"+str[2]+"\t"+str[3]+"\t"+str[4]+"\t"+str[5]+"\t"+str[6]+"\t"+str[7]+nnn);
		str = Utils.split(s2, "\t");
		sb.append(str[0]+"\t"+str[2]+"\t"+str[3]+"\t"+str[4]+"\t"+str[5]+"\t"+str[6]+"\t"+str[7]+nnn);
		List list = new ArrayList();
		while((line = br.readLine()) != null)
		{
			list.add(line);
		}
		for (int i = 1; i < list.size(); i+=2) {
			str = Utils.split(list.get(i).toString(), "\t");
			if(str[2].isEmpty())
				str[2] = "0";
			if(str[3].isEmpty())
				str[3] = "0";
			sb.append(str[0]+"\t"+str[2]+"\t"+str[3]);
			for (int j = 5; j < str.length; j++) 
			{
				if(str[j].isEmpty())
					str[j] = "0";
			}
			for (int k = 4; k < str.length-6; k+=4)
			{
				str[4] += ":"+str[k+4];
			}
			for (int k = 5; k < str.length-5; k+=4)
			{
				str[5] += ":"+str[k+4];
			}
			for (int k = 6; k < str.length-4; k+=4)
			{
				str[6] += ":"+str[k+4];
			}
			for (int k = 7; k < str.length-3; k+=4)
			{
				str[7] += ":"+str[k+4];
			}
			sb.append("\t"+str[4]);
			sb.append("\t"+str[5]);
			sb.append("\t"+str[6]);
			sb.append("\t"+str[7]);
			if(i != list.size()-1 && i != list.size()-2)
				sb.append(nnn);
		}
//		System.out.println(sb);
		write(sb,outPath,newFileName);
	}
	
	private static void write(StringBuffer node,String outPath,String filename)
	{
		try 
		{
			File file = new File(outPath+filename);

			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(node.toString());
			bw.flush();
			bw.close();
			fw.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
}
