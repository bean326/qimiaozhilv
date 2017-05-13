package cc.lv1.rpg.gs.net.impl;

import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.data.PressureTest;
import cc.lv1.rpg.gs.entity.impl.LogTemp;
import cc.lv1.rpg.gs.other.ErrorCode;
import vin.rabbit.net.abs.NetConnection;
import vin.rabbit.net.job.i.NetJob;
import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.impl.ArrayList;
import vin.rabbit.util.collection.impl.HashMap;


public class PressureJob extends NetJob {
	
	/** 0.默认是直接保存  
	 * 1.更新全部玩家炼化日志,夺宝奇兵青铜圣殿战斗记录,白银,黄金,天神,保存到日志文件 */
	private int type;
	
	private String data;
	
	private String fileName;

	public NetConnection getConnection() {
		return null; 
	}
	
	public PressureJob(String data,String fileName)
	{
		this.data = data;
		this.fileName = fileName;
	}
	
	/** type 0.默认是直接保存  
	 * 1.更新全部玩家炼化日志,夺宝奇兵青铜圣殿战斗记录,白银,黄金,天神,保存到日志文件 */
	public PressureJob(int type)
	{
		this.type = type;
	}


	public void run() 
	{
		if(type == 0)//除开moneyLog之外的
			PressureTest.getInstance().saveTextByFileName(data, fileName);
		else if(type == 1)//日常保存
		{
			if(!WorldManager.isZeroMorning(1))
			{
				write();
			}
		}
		else if(type == 2)//从第一天过渡到第二天时保存,保存后要清空当天的数据,开始记录第二天的数据
		{
			write();
			DataFactory.getInstance().clearLogs();
		}
	}
	
	
	public void write()
	{
		ArrayList list = DataFactory.getInstance().getLogList(0);
		LogTemp all = DataFactory.getInstance().getLogTemp(0,DataFactory.ALL);//取当天所有玩家炼化总次数的记录
		if(all != null && list != null)
			PressureTest.getInstance().saveMoneyLog(getStr(all,list), "lianhuaLog");
		
		list = DataFactory.getInstance().getLogList(1);
		all = DataFactory.getInstance().getLogTemp(1,DataFactory.ALL);
		if(all != null && list != null)
			PressureTest.getInstance().saveMoneyLog(getStr(all,list), "qingtongLog");
		
		list = DataFactory.getInstance().getLogList(2);
		all = DataFactory.getInstance().getLogTemp(2,DataFactory.ALL);
		if(all != null && list != null)
			PressureTest.getInstance().saveMoneyLog(getStr(all,list), "baiyinLog");
		
		list = DataFactory.getInstance().getLogList(3);
		all = DataFactory.getInstance().getLogTemp(3,DataFactory.ALL);
		if(all != null && list != null)
			PressureTest.getInstance().saveMoneyLog(getStr(all,list), "huangjinLog");
		
		list = DataFactory.getInstance().getLogList(4);
		all = DataFactory.getInstance().getLogTemp(4,DataFactory.ALL);
		if(all != null && list != null)
			PressureTest.getInstance().saveMoneyLog(getStr(all,list), "tianshenLog");
		
		list = DataFactory.getInstance().getLogList(5);
		if(list != null)
		{
			PressureTest.getInstance().saveTextByFileFolderName(getVipPlayerInfos(list), "vipPlayerInfos","moneyLog");
			DataFactory.getInstance().getLogList(5).clear();
		}
	}
	
	private String getVipPlayerInfos(ArrayList list)
	{
		StringBuffer data = new StringBuffer();
		data.append("time\taccount\tname\tlevel\tdata");
		for (int i = 0; i < list.size(); i++)
		{
			LogTemp log = (LogTemp) list.get(i);
			if(i == 0)
				data.append(Utils.LINE_SEPARATOR);
			data.append(log.getTime());
			data.append("\t");
			data.append(log.getAccountName());
			data.append("\t");
			data.append(log.name);
			data.append("\t");
			data.append(log.getLevel());
			data.append("\t");
			data.append(log.getData());
			if(i != list.size()-1)
				data.append(Utils.LINE_SEPARATOR);
		}
		return data.toString();
	}
	
	private String getStr(LogTemp all,ArrayList list)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("account\tname\tlevel\tcount\tpoint\tmoney\tdaijin\tgoods");
		sb.append(Utils.LINE_SEPARATOR);
		sb.append("all\t");
		sb.append("all\t");
		sb.append("0\t");
		sb.append(all.getCount());
		sb.append("\t");
		sb.append(all.getPoint());
		sb.append("\t");
		sb.append(all.getMoney());
		sb.append("\t");
		sb.append(all.getEquipMoney());
		sb.append("\t");
		sb.append(all.getGoodsStr().length()==0?"无":all.getGoodsStr());
		sb.append(Utils.LINE_SEPARATOR);
		for (int i = 0; i < list.size(); i++)
		{
			LogTemp log = (LogTemp) list.get(i);
			sb.append(log.getAccountName());
			sb.append("\t");
			sb.append(log.name);
			sb.append("\t");
			sb.append(log.getLevel());
			sb.append("\t");
			sb.append(log.getCount());
			sb.append("\t");
			sb.append(log.getPoint());
			sb.append("\t");
			sb.append(log.getMoney());
			sb.append("\t");
			sb.append(log.getEquipMoney());
			sb.append("\t");
			sb.append(log.getGoodsStr().length()==0?"无":log.getGoodsStr());
			if(i != list.size()-1)
				sb.append(Utils.LINE_SEPARATOR);
		}
		return sb.toString();
	}

}
