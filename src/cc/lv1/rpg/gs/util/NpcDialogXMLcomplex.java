package cc.lv1.rpg.gs.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;


import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.xml.XMLFactory;
import vin.rabbit.util.xml.XMLNode;

public class NpcDialogXMLcomplex {
	
	public static void main(String[] args) {

		NpcDialogXMLcomplex ccx = new NpcDialogXMLcomplex();
//		String inPath = "E:\\workspace\\rpg_game\\bin\\data\\npc_dialog\\";
		String inPath = "D:\\npc_dialog\\in";
		String outPath = "D:\\npc_dialog\\out";
		ccx.writeXML(inPath,outPath);
	}
	
	public void writeXML(String inPath,String outPath)
	{
		String nnn = System.getProperty("line.separator");
		File file = new File(inPath);
		File[] dialog = file.listFiles();
		for (int i = 0; i < dialog.length; i++) {
			StringBuffer note = new StringBuffer("<dialog>"+nnn);
			if(dialog[i].getName().endsWith(".txt"))
			{
				String xml = inPath + dialog[i].getName();
				XMLNode node = Utils.getXMLNode(xml);
				System.out.println(dialog[i].getName());
				List nodes = node.getSubNodes();
				for (int j = 0; j < nodes.size(); j++) {
					XMLNode dia = (XMLNode) nodes.get(j);
//					if(dia.getName().equals("default"))
//					{
//						List op1 = dia.getSubNode("options").getSubNodes();
//						XMLNode words = dia.getSubNode("words");
//						XMLNode color = words.getSubNode("color");
//						XMLNode text = words.getSubNode("text");
//						String textId = text.getAttributeValue("textId");
//						String content = "    <text color=\""+color.getData()+"\" textId=\""+textId+"\">"+text.getData()+"</text>";
//						note.append(content);
//						note.append(nnn);
//						for (int k = 0; k < op1.size(); k++) {
//							XMLNode item = (XMLNode) op1.get(k);
//							String textid = item.getAttributeValue("textId");
//							String textcon =item.getAttributeValue("text");
//							String option = "    <item textId=\""+textid+"\">"+textcon+"</item>";
//							note.append(option);
//							note.append(nnn);
//						}
//					}
//					else
//					{
					
					String taskStr = null;
					List op = dia.getSubNodes();
						for (int k = 0; k < op.size(); k++) {
							XMLNode task = (XMLNode) op.get(k);
							XMLNode words = task.getSubNode("words");
							XMLNode color = words.getSubNode("color");
							XMLNode text = words.getSubNode("text");
							String textId = text.getAttributeValue("textId");
							String content = "";
							if(words != null && color != null && text != null && textId != null)
							     content = "    <text color=\""+color.getData()+"\" textId=\""+textId+"\">"+text.getData()+"</text>";
							else if(color == null && text != null && textId != null)
								 content = "    <text textId=\""+textId+"\">"+text.getData()+"</text>";
							
							if(text.getData().startsWith("任務"))
							{
								taskStr= text.getData().substring(3);
							}
							note.append(content);
							note.append(nnn);
							if(task.getSubNode("options") == null)
								continue;
							List op2 = task.getSubNode("options").getSubNodes();
							if(op2 == null)
								continue;
							for (int h = 0; h < op2.size(); h++) {
								XMLNode item = (XMLNode) op2.get(h);
								String textid = item.getAttributeValue("textId");
								String textcon =item.getAttributeValue("text");
								
								if(textcon.isEmpty())
								{
									textcon = item.getData();
								}
								String option = "    <item textId=\""+textid+"\">"+textcon+"</item>";
								note.append(option);
								note.append(nnn);
							}
							
							
						}

//					}
						if(dia.getName().startsWith("t"))
						{
							String option = "    <item textId=\""+dia.getName().substring(1, dia.getName().length())+"\">"+taskStr+"</item>";
							note.append(option);
							note.append(nnn);
						}
				}
				note.append("</dialog>");

				
				StringBuffer buffer = new StringBuffer();
	
				String strs [] = note.toString().split(nnn);
				
				boolean isDefTxt = true;
				int index = 0;
				

				for (int k = 0; k < strs.length; k++)
				{
					if(isDefTxt)
					{
						if(!(dialog[i].getName().equals("npcdefault.txt")))
						{

							try
							{
								if(strs[k+1].indexOf("任務") != -1)
								{
									if(k!=0)//临时加的.
										isDefTxt = false;
								}
							}
							catch(Exception e)
							{
								buffer.append(strs[strs.length-1]);
								buffer.append(nnn);
								break;
							}
						}
						buffer.append(strs[k]);
						buffer.append(nnn);
						continue;
					}
					else
					{

						buffer.append(strs[k+4]);
						buffer.append(nnn);
						buffer.append(strs[k+8]);
						buffer.append(nnn);
						
						k += 8;
						

					}

					if(strs.length-2 ==k)
					{
						buffer.append(strs[strs.length-1]);
						break;
					}

				}
				
				
				write(buffer,dialog[i].getName(),outPath);
			}
		}
	}
	
	

	//F:\Eclipse-3.4.1\workspace\rpg_game\bin\data\npc_dialog
	
	public void write(StringBuffer node,String filename,String outPath)
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
