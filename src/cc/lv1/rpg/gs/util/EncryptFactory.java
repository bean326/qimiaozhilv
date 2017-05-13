package cc.lv1.rpg.gs.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.Utils;

public class EncryptFactory
{
	
	private final int KEY = 255;
	
	private int key = 10;
	
	private static EncryptFactory encryptFactory = null;
	
	private EncryptFactory()
	{}
	
	public static EncryptFactory getInstance()
	{
		if(encryptFactory == null)
			encryptFactory = new EncryptFactory();
		return encryptFactory;
	}
	
	/**
	 * 加密
	 */
	public String encode(final String txt)
	{
		byte b[] = toUnicode(txt).getBytes();
		
		
		int count = b.length / 4;
		int less = b.length % 4;  
		
		ByteBuffer buffer = new ByteBuffer(b);
		ByteBuffer encodebuffer = new ByteBuffer(b.length + 8);
		
		encodebuffer.writeInt(count);
		encodebuffer.writeInt(less);
		
		int a = 0;
		
		for (int i = 0; i < count; i++)
		{
			a = buffer.readInt();
			key = (int)(Math.random() * 120);
			a = a ^ key;

			encodebuffer.writeInt(a);
			encodebuffer.writeByte(key);

		}
		
		for (int i = 0; i < less; i++)
		{
			a = buffer.readByte();
			encodebuffer.writeByte(a);
		}

		StringBuffer sb = new StringBuffer();
		int available = encodebuffer.available();
		for (int i = 0; i < available; i++)
		{
			sb.append(encodebuffer.readByte()^KEY);
			
			if(i != available-1)
				sb.append(":");
		}
		
		return new String(sb.toString());
	}


	
	/**
	 * 解密
	 */
	public String decode(String s)
	{
		String ss[] =  Utils.split(s, ":");
		ByteBuffer tmpBuffer = new ByteBuffer();
		for (int i = 0; i < ss.length; i++)
		{
			tmpBuffer.writeByte(Integer.parseInt(ss[i].trim())^KEY);
		}

		ByteBuffer buffer = new ByteBuffer(tmpBuffer.getBytes());		

		int count = buffer.readInt();
		int less = buffer.readInt();
		
		
		ByteBuffer decodeBuffer = new ByteBuffer();
		
		int a = 0;
		for (int i = 0; i < count; i++)
		{
			a = buffer.readInt();
			key = buffer.readByte();
			
			a= a ^ key;
			
			decodeBuffer.writeInt(a);
		}
		
		for (int i = 0; i < less; i++)
		{
			a = buffer.readByte();
			decodeBuffer.writeByte(a);
		}
		
		
	    return toChinese(new String(decodeBuffer.getBytes()));
	}
	
	
	/**
	 * 加密文件
	 * @param fileName
	 * @return
	 */
	public boolean encodeFile(String fileName)
	{
		String fileContent = Utils.readFile2(fileName);
		fileContent = encode(fileContent);
		Utils.writeFile(fileName+".encode", fileContent.getBytes());
		return true;
	}
	
	/**
	 * 解密文件
	 * @param fileName
	 */
	public String decodeFile(String fileName)
	{
		String fileContent = Utils.readFile2(fileName);
		return decode(fileContent);
	}

	
	private String toUnicode(String str)
	{  
		  String sp="\\u";   
		  if(str.indexOf(sp) != -1)
			  return "";
		  StringBuffer result = new StringBuffer();   
		  for(int i=0;i<str.length();i++)
		  {   
			  String mid=Integer.toHexString((int)str.charAt(i));   
			  for(int j=mid.length();j<4;j++)
			  {   
				  mid="0"+mid; 
			  }   
			  result.append(sp);
			  result.append(mid);  
		  }   
		  return result.toString();
	}
	
	private String toChinese(String str)
	{
		if(str.indexOf("\\u") == -1)
			return "";
		StringBuffer result = new StringBuffer();
		str = str.replace("\\u", "");
		String[] strs = new String[str.length()/4];
		for (int i = 0; i < str.length(); i++) 
		{
			if(i%4==0)
			{
				strs[i/4] = str.substring(i,i+4);
			}
		}
		for (int i = 0; i < strs.length; i++) 
		{
			int ten = Integer.parseInt(strs[i],16);
			result.append((char)ten);
		}
		return result.toString();
	}

	
	public static void main(String[] args)
	{

		InputStreamReader reader = new InputStreamReader(System.in);
		BufferedReader buffer = new BufferedReader(reader);
		
		try
		{
			System.out.println("\u8BF7\u8F93\u5165\u6587\u4EF6\u540D\u548C\u8DEF\u5F84");
			System.out.println("\u5982:D:\\DServer\\config\\DBserver.xml");
			String fileName = buffer.readLine();
			
			File file = new File(fileName);
			if(!file.isFile())
			{
				System.out.println("\u4F60\u8F93\u5165\u7684\u6587\u4EF6\u4E0D\u5B58\u5728 :"+fileName);
				System.exit(0);
			}
			
			System.out.println("------------------------------------");
			EncryptFactory.getInstance().encodeFile(fileName);
//			System.out.println(EncryptFactory.getInstance().decodeFile(fileName));
			System.out.println(EncryptFactory.getInstance().decodeFile(fileName+".encode"));
			System.out.println("------------------------------------");
			System.out.println("1.\u52A0\u5BC6\u6210\u529F!!!");
			System.out.println("2.\u52A0\u5BC6\u6587\u672C\u6B63\u6587\u4E3A\u63A7\u5236\u53F0\u6253\u5370\u5185\u5BB9!!!");
			System.out.println("3.\u8BF7\u5C06"+file.getName()+"\u6E90\u6587\u4EF6\u4E0A\u4F20\u5230\u5B89\u5168\u7684\u5730\u65B9\u8FDB\u884C\u5907\u4EFD!!!");
			System.out.println("4.\u8BF7\u5C06"+file.getName()+".encode\u7684\u6269\u5C55\u540D\u53BB\u6389\u4EE5\u66FF\u6362\u6E90\u6587\u4EF6!!!");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(0);
		}
	}
	
}
