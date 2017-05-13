package cc.lv1.rpg.gs.entity.impl;

import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.Utils;
import cc.lv1.rpg.gs.entity.i.Sprite;
/**
 *  NPC实体
 * @author bean
 *
 */
public class NPC extends Sprite
{
	/** NPC的商店IDS(若不是商店NPC就为0) */
	public String shopId;
	
	/** NPC的脚本 */
	public String script;
	
	public int rank;//黄金斗士要用的
	/**
	 * 附加信息
	 */
	public String attach;
	
	public NPC()
	{

	}

	
	public void readFrom(ByteBuffer buffer)
	{
		super.readFrom(buffer);
	}
	
	public void writeTo(ByteBuffer buffer)
	{
		super.writeTo(buffer);
	}

	public void writeBaseTo(ByteBuffer buffer)
	{
		buffer.writeInt(id);
		buffer.writeInt(iconId);
		buffer.writeInt(modelId);
		buffer.writeUTF(name);
		buffer.writeInt(x);
		buffer.writeInt(y);
		buffer.writeUTF(script);
	}
	
	public boolean setVariable(String key,String value)
	{
		if(key.equals("script"))
		{
			String[] strs = Utils.split(value, ":");
			script = strs[0];
			if(strs.length == 2)
				rank = Integer.parseInt(strs[1]);
			return true;
		}
		else
			return super.setVariable(key, value);
	}

}
