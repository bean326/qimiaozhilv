package cc.lv1.rpg.gs.net.impl;

import vin.rabbit.net.abs.NetConnection;
import vin.rabbit.net.job.i.NetJob;
import vin.rabbit.util.ByteBuffer;
import cc.lv1.rpg.gs.dao.DatabaseAccessor;
import cc.lv1.rpg.gs.entity.ext.Bag;
import cc.lv1.rpg.gs.entity.impl.Player;
/**
 * 合区保存玩家对象
 * @author dxw
 *
 */
public class MergeSaveJob extends NetJob
{

	private DatabaseAccessor da;
	
	private Player player;
	
	
	public MergeSaveJob(DatabaseAccessor da,Player player)
	{
		this.da = da;
		this.player = player;
	}
	
	public NetConnection getConnection()
	{
		return null;
	}

	public void run()
	{
		ByteBuffer buffer = new ByteBuffer();
		player.saveTo(buffer);
		Player nplayer = new Player();	
		nplayer.loadFrom(buffer);
		nplayer.initial(0);
		
		Bag bag = (Bag)nplayer.getExtPlayerInfo("bag");
		bag.money = 0;
		da.savePlayer(nplayer);
	}

}
