package cc.lv1.rpg.gs.entity.impl;

import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.RPGameObject;
import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.Map;
import vin.rabbit.util.collection.impl.ArrayList;
import vin.rabbit.util.collection.impl.HashMap;

/**
 * 发送奖励补偿
 * @author dxw
 *
 */
public class RewardSender extends RPGameObject
{
	
	public static Map sendPlayerMap = new HashMap(2000);
	
	/**
	 * 数量
	 */
	public int count;
	
	/**
	 * 发的数量
	 */
	public int canCount;
	
	/**
	 * 发的限制等级
	 */
	public int level;
	
	/**
	 * 品质
	 */
	public int quality;
	
	
	//id	name	count	quality	canCount	level
	//道具编号	道具名字	道具数量	品质限定	领取数量限制	等级领取限制
	
	public static String reCreateRewardSenderFile()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("id\tname\tcount\tquality\tcanCount\tlevel");
		sb.append(Utils.LINE_SEPARATOR);
		sb.append("id");
		sb.append("\t");
		sb.append("name");
		sb.append("\t");
		sb.append("count");
		sb.append("\t");
		sb.append("quality");
		sb.append("\t");
		sb.append("getCount");
		sb.append("\t");
		sb.append("level");
		
		Object obj = DataFactory.getInstance().getAttachment(DataFactory.REWARDSENDER_LIST);
		
		if(!(obj instanceof ArrayList))
			return sb.toString();

		ArrayList list = (ArrayList)obj;
		
		if(list.size() <= 0)
			return sb.toString();
		
		
		sb.append(Utils.LINE_SEPARATOR);
		
		for (int i = 0; i < list.size(); i++)
		{
			RewardSender rs = (RewardSender)list.get(i);
			
			if(rs==null)
				continue;
		
			
			sb.append(rs.id+"\t"+rs.name+"\t"+rs.count+"\t"+rs.quality+"\t"+rs.canCount+"\t"+rs.level);
			
			if(i != list.size()-1)
				sb.append(Utils.LINE_SEPARATOR);
		}
		
		return sb.toString();
	}
	
}
