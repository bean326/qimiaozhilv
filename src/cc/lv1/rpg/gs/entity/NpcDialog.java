package cc.lv1.rpg.gs.entity;


import cc.lv1.rpg.gs.entity.controller.NpcController;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.List;
import vin.rabbit.util.collection.impl.ArrayList;
import vin.rabbit.util.xml.XMLNode;
/**
 * npc对话信息
 * @author bean
 *
 */
public class NpcDialog
{	
    private String dialogIndex;
    
    private DialogStep[] dialogSteps;
    
    private NpcController npcController;
    
    /**
     * 构造方法
     * @param xmlDialog
     */
	public NpcDialog(XMLNode root)
	{
		this.dialogIndex = root.getName();
		List steps = root.getSubNodes();
		dialogSteps = new DialogStep[steps.size()];
		for (int i = 0; i < steps.size(); i++) 
		{
			XMLNode step = (XMLNode) steps.get(i);
			dialogSteps[i] = new DialogStep(step);
		}
	}
	
	public void setParent(NpcController npc)
	{
		this.npcController = npc;
	}
	
	/**
	 * 通过指令获取DialogStep对象
	 * @param command
	 * @return
	 */
	public DialogStep getDialogStep(String command)
	{
		int com = 0;
		
			try
			{
				com = Integer.parseInt(command);
			}
			catch(NumberFormatException e)
			{
				return null;
			}
			
		for (int i = 0; i < dialogSteps.length; i++) {
			if(dialogSteps[i].index == com)
			{
				return dialogSteps[i];
			}
		}
		return null;
	}
	
	public String getDialogIndex()
	{
		return this.dialogIndex;
	}

	public DialogStep[] getDialogStep()
	{
		return this.dialogSteps;
	}

	public NpcController getNpcController()
	{
		return this.npcController;
	}
	
	/**
	 * -----------------
	 * 内部类(任务对话)
	 * -----------------
	 * @author bean
	 *
	 */
    public class DialogStep
    {
    	public int index;
    	
    	public String dialogColor;
    	
    	public int dialogId;
    	
    	public List optionList;
    	
    	public DialogStep(XMLNode step)
    	{
    		XMLNode words = step.getSubNode("words");
    		XMLNode options = step.getSubNode("options");
    		XMLNode color = words.getSubNode("color");
    	    XMLNode text = words.getSubNode("text");
    	    this.dialogColor = color.getData();
    	    this.dialogId = Integer.parseInt(text.getAttributeValue("textId"));
    		this.optionList = options.getSubNodes();
    		if(this.optionList == null)
    		{
    			this.optionList = new ArrayList(5);
    		}
    		this.index = Integer.parseInt(step.getName().replaceAll("s", ""));
    	}
  
    	public void writeTo(ByteBuffer buffer)
    	{
    		int size = optionList.size();
    		buffer.writeInt(npcController.getID());
    		buffer.writeInt(dialogId);
    		buffer.writeInt(size);

    		for (int i = 0; i < size; i++)
    		{
    			XMLNode item = (XMLNode) optionList.get(i);
    			String textId = item.getAttributeValue("textId");
    			String command = item.getAttributeValue("command");

    			String [] strIds = Utils.split(textId, ":");
    			if(strIds.length == 1)
    			{
    				buffer.writeInt(Integer.parseInt(textId));
    				buffer.writeByte(-1);
    				buffer.writeInt(-1);
    				buffer.writeByte(-1);
    			}
    			else if(strIds.length == 4)
    			{
    				buffer.writeInt(Integer.parseInt(strIds[0]));
    				buffer.writeByte(Integer.parseInt(strIds[1]));
    				buffer.writeInt(Integer.parseInt(strIds[2]));
    				buffer.writeByte(Integer.parseInt(strIds[3]));
    			}
    		
    			buffer.writeUTF(command);
    			if(item.getName().equals("taskItem"))
    			{
    				optionList.remove(i--);
    				size--;
    			}
    		}
    	}
    	
    	
    	public void addTaskOption(String taskId,String cmd)
    	{
    		XMLNode itemNode = new XMLNode("taskItem");
    		itemNode.addAttribute("textId", taskId);
    		itemNode.addAttribute("command", cmd);  		
    		optionList.add(itemNode);
    	}
    	
    }
	
}
