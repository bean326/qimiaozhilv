package cc.lv1.rpg.gs.util;

import java.io.FileOutputStream;
import java.io.IOException;

import vin.rabbit.util.collection.i.List;


public class Excel
{

	private int row = 0;
	
	private FileOutputStream fos;

	public Excel(String strPath) throws IOException
	{
		fos = new FileOutputStream(strPath);
		_writeFile(new short[]{ 0x809, 8, 0, 0x10, 0, 0 });
	}
	
	public void addHead(List list) throws Exception
	{
		Object []obj = list.toArray();
		String str []= new String[obj.length];
		for (int i = 0; i < str.length; i++)
			str[i] = (String)obj[i];
		addHead(str);
	}
	
	public void addHead(String head[]) throws Exception
	{
		addLine(0, head);
	}
	
	public void addLine(int rows,List list)throws Exception
	{
		if(list == null)
			throw new Exception("row "+rows+" is null");
		if(rows < 0)
			throw new Exception("row < 0");
		
		Object []objs = list.toArray();
		String str[] = new String[list.size()];
		for (int i = 0; i < str.length; i++)
			str[i] = String.valueOf(objs[i]);
		addLine(rows,str);
	}
	
	
	public void addLine(int rows, String head[]) throws Exception
	{
		if(head == null)
			throw new Exception("row "+rows+" is null");
		if(rows < 0)
			throw new Exception("row < 0");
		for (int i = 0;i < head.length; i++)
			addString(rows, i, head[i]);
		row = rows + 1;
	}

	public void addLine(List list) throws Exception
	{
		addLine(row, list);
	}
	
	private void _writeFile(short[] values) throws IOException
	{
		for (short v : values)
		{
			byte[] b = getBytes(v);
			fos.write(b, 0, b.length);
		}
	}


	public void close() throws IOException
	{
		_writeFile(new short[]{ 0xa, 0});
		fos.close();
	}
	
	public void addString(int rows, int columns, String value) throws IOException
	{
		byte[] b = getBytes(value);
		_writeFile(new short[]
		{ 0x204, (short) (b.length + 8), (short) rows, (short) columns, 0, (short) b.length });
		fos.write(b, 0, b.length);
	}

	private byte[] getBytes(String s)
	{
		return s.getBytes();
	}
	
	private byte[] getBytes(short n)
	{
		byte[] b = new byte[2];
		b[0] = (byte) (n & 0xff);
		b[1] = (byte) (n >> 8 & 0xff);
		return b;
	}
}
