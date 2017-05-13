package cc.lv1.rpg.gs.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vin.rabbit.util.ByteBuffer;

public class Code 
{
	/**
	 * 加密
	 * @param buffer
	 * @param key
	 * @return
	 */
	public static ByteBuffer encode(ByteBuffer buffer,int key)
	{
		ByteBuffer buff = new ByteBuffer();
		if(buffer.length() > 0)
		{
			int _key = buffer.readInt();
			buff.writeInt(_key^key);
			if(buffer.length() > 0)
			{
				ByteBuffer _buff = new ByteBuffer();
				_buff.writeBytes(buffer.getBytes());
				buff.writeBytes(encodeTwo(_buff, _key).getBytes());
			}
		}
		return buff;
	}
	
	private static ByteBuffer encodeTwo(ByteBuffer buffer, int key)
	{
		if(buffer.length() > 0)
		{
			ByteBuffer buff = new ByteBuffer();
			while(buffer.length() > 4)
			{
				buff.writeInt(buffer.readInt() ^ key);
			}
			while(buffer.length() > 0)
			{
				buff.writeInt(buffer.readByte() ^ (key & 0xFF));
			}
			return buff;
		}
		return null;
	}
	
	/**
	 * 解密
	 * @param buffer
	 * @param key
	 * @return
	 */
	public static void decode(ByteBuffer buffer, int key)
	{
		ByteBuffer buff = new ByteBuffer();
		if(buffer.length() > 0)
		{
			int _key = buffer.readInt() ^ key;
			buff.writeInt(_key);
			if(buffer.length() > 0)
			{
				ByteBuffer _buff = new ByteBuffer();
				_buff.writeBytes(buffer.getBytes());
				buff.writeBytes(decodeTwo(_buff, _key).getBytes());
			}
		}
		buffer.clear();
		buffer.writeBytes(buff.getBytes());
	}
	
	private static ByteBuffer decodeTwo(ByteBuffer buffer,int key)
	{
		if(buffer.length() > 0)
		{
			ByteBuffer buff = new ByteBuffer();
			while(buffer.length() > 4)
			{
				buff.writeInt(buffer.readInt() ^ key);
			}
			while(buffer.length() > 0)
			{
				buff.writeByte(buffer.readByte() ^ (key & 0xFF));
			}
			return buff;
		}
		return null;
	}
	
	public static String toUnicode(String str)
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
	
	public static String toChinese(String str)
	{		
		 Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");    
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) 
        {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch + "");    
        }
        return str;

	}
	
	public static void main(String[] args) {
		System.out.println(toUnicode("紫药"));
	}
	

    public static ByteBuffer byteDecrypt(ByteBuffer inBuffer)
	{
		if(inBuffer.available() > 0)
		{
			ByteBuffer buffer = new ByteBuffer();
			int key = inBuffer.readInt();
			if(inBuffer.available() > 0)
			{
				ByteBuffer b = new ByteBuffer(inBuffer.readBytes(inBuffer.available()));
				b = encrypt(b, key);
				buffer.writeBytes(b.getBytes());
			}
			return buffer;
		}
		return null;
	}
    
    
 

    public static ByteBuffer encrypt(ByteBuffer buffer,int key)
	{
    	ByteBuffer out = new ByteBuffer();
		if(buffer.available() > 0)
		{
			while(buffer.available() > 4)
			{	
				int _dec32 = buffer.readInt() ^ key;
				out.writeInt(_dec32);
			}
			while(buffer.available() > 0)
			{
				int _dec8 = buffer.readByte() ^ (key & 0xFF);
				out.writeByte(_dec8);
			}
		}
		return out;
	}


}
