package cc.lv1.rpg.gs.util.dxw;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import vin.rabbit.util.Utils;
/**
 * 扫描所有扩展名为×××的文件，放着同一目录下。
 * @author Administrator
 *
 */
public class FileScanner
{
	/**
	 * 取得源文件的扩展名类型
	 */
	private String [] fileTypes = {".jpg",".bmp",".gif",".png"};
	
	/**
	 * 文件的绝对路径
	 */
	private static String inPath = "D:\\Program Files\\Tencent\\QQ\\Users\\8053677\\Image";
	
	
	/**
	 * 文件的绝对路径
	 */
	private static String outPath = "D:\\QQpic\\";
	
	/**
	 * 取得的所有文件
	 */
	private List<File> fileList = new ArrayList<File>();
	
	/**
	 * 已经排序完成的对象
	 */
	private Object[] objects;
	
	private boolean flag;
	
	private FileScanner()
	{
	}
	
	private void inputFile(String filePath)
	{
		File file = new File(filePath);
		
		if(file.isDirectory())
		{
			File fs [] = file.listFiles();
			
			if(fs == null)
				return;
			
			for (int i = 0; i < fs.length; i++) 
				inputFile(fs[i].getAbsolutePath());
		}
		else
		{
			flag = false;
			for (int i = 0; i < fileTypes.length; i++) 
			{
				if(file.getName().toLowerCase().trim().endsWith(fileTypes[i]))
					flag = true;
			}
			
			if(!flag)
				return;

			fileList.add(file);

		}
	}
	
	
	private void sort()
	{
		Object objs[]= fileList.toArray();
		
		for(int i = 0; i < objs.length ; i++)
		{
			System.out.println("sort complete "+((int)((double)i/(double)objs.length*100))+"/100"+"    ......"+objs.length);
			for(int j = objs.length-1 ; j > i ; j --)
			{
			    if(((File)objs[j-1]).lastModified() > ((File)objs[j]).lastModified())
			    {
				     Object tmp = objs[j];
				     objs[j] = objs[j-1];
				     objs[j-1] = tmp;
			    }
			}
		}
		objects = objs;
	}
	
	
	private void outputFile(String path) throws Exception
	{
		File file = null;
		byte b[] = null;
		String str[] = null;
		
		FileInputStream fis = null;
		FileOutputStream fos = null;
		
		for (int i = 0; i < objects.length; i++) 
		{
			file = (File)objects[i];
			if(file != null)
			{
				str = Utils.split(file.getName(), ".");
				fis = new FileInputStream(file);
				fos = new FileOutputStream(path+i+"."+str[str.length-1]);
				b = new byte[2048];
				
				while(-1 != fis.read(b))
				{
					fos.write(b);
				}
				
				fos.flush();
				fis.close();
				fos.close();
				
			}
		}
		
		
	}
	
	
	
	public static void main(String[] args) throws Exception
	{
		FileScanner fs = new FileScanner();
		System.out.println("-准备提取文件");
		fs.inputFile(inPath);
		System.out.println("-准备排序");
		fs.sort();
		System.out.println("-准备输出文件");
		fs.outputFile(outPath);
		System.out.println("-完成");
		

		
		//以下是输出所有硬盘的文件
/*		File[] files = File.listRoots();
		FileScanner fs = new FileScanner();
		System.out.println("-准备提取文件");
		for (int i = 0; i < files.length; i++)
		{
			fs.inputFile(files[i].getAbsolutePath());
		}
		System.out.println("-准备排序");
		fs.sort();
		System.out.println("-准备输出文件");
		fs.outputFile(outPath);
		System.out.println("-完成");*/
		
	}
	
	
}
