package cc.lv1.rpg.gs.entity.ext;

import vin.rabbit.util.ByteBuffer;
/**
 * 玩家增值接口
 * @author dxw
 *
 */
public abstract class PlayerExtInfo
{
	public static final PlayerExtInfo getInstance(String name)
	{
		if(name.equals("bag"))
			return new Bag();

		if(name.equals("playerSetting"))
			return new PlayerSetting();
		//if(name.equals("equipSet"))
			//return new EquipSet();

		if(name.equals("skillTome"))
			return new SkillTome();
		/*		if(name.equals("home"))
			return new Home();
		if(name.equals("friendsList"))
			return new FriendsList();
		if(name.equals("battleinfo"))
			return new BattleInfo();*/

		return null;
	}
	
	public abstract String getName();
	
	public abstract void loadFrom(ByteBuffer byteBuffer);

	public abstract void saveTo(ByteBuffer byteBuffer);
	
	
}
