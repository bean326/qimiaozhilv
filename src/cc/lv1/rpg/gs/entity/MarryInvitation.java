package cc.lv1.rpg.gs.entity;

import vin.rabbit.util.ByteBuffer;
import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.data.DC;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.data.PressureTest;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.controller.RoomController;
import cc.lv1.rpg.gs.entity.ext.Bag;
import cc.lv1.rpg.gs.entity.ext.OtherExtInfo;
import cc.lv1.rpg.gs.entity.impl.Goods;
import cc.lv1.rpg.gs.entity.impl.Mail;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsMarry;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsProp;
import cc.lv1.rpg.gs.net.SMsg;
import cc.lv1.rpg.gs.other.ErrorCode;
import cc.lv1.rpg.gs.util.FontConver;

public class MarryInvitation extends ConfirmJob 
{
	public static final int MANGOODS = 1;
	public static final int WOMENGOODS = 2;
	public static final int VIPMANGOODS = 3;
	public static final int VIPWOMENGOODS = 4;
	public static final int JIEHUNZHENG = 5;
	
	private PlayerController inviter;
	
	private long objectIndex;//结婚戒指流水
	
	/** 1:离婚邀请 2:结婚邀请 */
	private int type;
	
	public MarryInvitation(PlayerController inviter,PlayerController target,long index,int type)
	{
		this.inviter = inviter;
		super.setSource(target);
		objectIndex = index;
		this.type = type;
	}
	
	public void confirm(boolean accepted)
	{
		if (accepted)
		{	
			if(type == 2)
				marry();
			else if(type == 1)
				cancelMarry();
			else
				return;
		}
		else 
		{
			if(inviter == null || source == null)
				return;
			inviter.sendGetGoodsInfo(1,false, source.getName()+DC.getString(DC.INVITE_4));
		}
	}
	
	
	public void cancel()
	{
		if(inviter == null)
			return;
		inviter.sendGetGoodsInfo(1,false, DC.getString(DC.INVITE_1));
	}
	
	public void cancelMarry()
	{
		
	}
	
	public void marry()
	{
		if(inviter == null || source == null)
			return;
		if(!(inviter.getParent() instanceof RoomController))
			return;
		if(!(source.getParent() instanceof RoomController))
			return;
		if(inviter.getRoom().id != DataFactory.MARRYROOM)
			return;
		if(source.getRoom().id != DataFactory.MARRYROOM)
			return;
		if(source.getPlayer().level < PlayerController.MARRYLEVEL)
		{
			source.sendAlert(ErrorCode.ALERT_MARRY_LEVEL_ERROR);
			return;
		}
		if(inviter.getPlayer().level < PlayerController.MARRYLEVEL)
		{
			source.sendAlert(ErrorCode.ALERT_TARGET_MARRY_LEVEL_ERROR);
			return;
		}
		OtherExtInfo beiqiuhun = (OtherExtInfo) source.getPlayer().getExtPlayerInfo("otherExtInfo");
		if(!"".equals(beiqiuhun.loverName) || beiqiuhun.loverId != 0)
		{
			source.sendAlert(ErrorCode.ALERT_PLAYER_IS_MARRYED);
			return;
		}
		if(source.getTeam() == null)
		{
			source.sendAlert(ErrorCode.ALERT_MARYRY_OBJECT_NOTINTEAM);
			return;
		}
		PlayerController lover = source.getTeam().getPlayer(inviter.getName());
		if(lover == null)
		{
			source.sendAlert(ErrorCode.ALERT_MARYRY_OBJECT_NOTINTEAM);
			return;
		}
		if(source.getPlayer().sex == inviter.getPlayer().sex)
		{
			source.sendAlert(ErrorCode.ALERT_PLAYER_SEX_NOT_SAME);
			return;
		}
		OtherExtInfo qiuhunren = (OtherExtInfo) inviter.getPlayer().getExtPlayerInfo("otherExtInfo");
		if(!"".equals(qiuhunren.loverName) || qiuhunren.loverId != 0)
		{
			source.sendAlert(ErrorCode.ALERT_TARGET_PLAYER_IS_MARRYED);
			return;
		}
		Bag inviterBag = (Bag) inviter.getPlayer().getExtPlayerInfo("bag");
		Goods goods = inviterBag.getGoodsByObjectIndex(objectIndex);
		if(goods == null)
		{
			source.sendAlert(ErrorCode.ALERT_TARGET_PLAYER_NOTMARRYGOODS);
			return;
		}
		if(goods.type != 27)
		{
			source.sendAlert(ErrorCode.ALERT_TARGET_PLAYER_NOTMARRYGOODS);
			return;
		}
		GoodsProp prop = null;
		if(goods instanceof GoodsProp)
		{
			prop = (GoodsProp) goods;
			if(prop.chatType != 0 && prop.chatType != 1)//判断物品是不是结婚戒指
			{
				source.sendAlert(ErrorCode.ALERT_TARGET_PLAYER_NOTMARRYGOODS);
				return;
			}
		}
		else
			return;
		
		GoodsMarry gm = DataFactory.getInstance().getGoodsMarryByGoodsId(goods.id);
		if(gm == null)
		{
			System.out.println("MarryInvitation GoodsMarry is null:"+goods.id);
			return;
		}
		if(!gm.isConEnough(inviter))
		{
			source.sendAlert(ErrorCode.ALERT_TARGET_NOT_NEEDGOODS);
			return;
		}
		
		qiuhunren.loverId = source.getID();
		qiuhunren.loverName = source.getName();
		qiuhunren.marryType = (byte) (((GoodsProp)goods).chatType + 1);
		
		beiqiuhun.loverId = inviter.getID();
		beiqiuhun.loverName = inviter.getName();
		beiqiuhun.marryType = (byte) (((GoodsProp)goods).chatType + 1);
		
		gm.sendMarryGoodsToPlayer(inviter);
		gm.sendMarryGoodsToPlayer(source);
		
		inviterBag.setExtGoods(5, goods);//再附加道具列表中加一个位置
		inviterBag.removeGoods(inviter, objectIndex, 1);
		gm.removeMarryGoods(inviter);
		
		boolean isVip = ((GoodsProp)goods).chatType == 1;
		/**
		 * 系统发放全服公告，以红色字体显示，普通结婚显示内容为：“恭喜XXX与XXX结为夫妻！
		 * 让我们大家一起来祝福他们吧！”VIP结婚显示内容为：“XXX与XXX携手步入婚姻的殿堂，
		 * 他们宣誓相爱一生一世，让我们为这幸福的一对人儿欢呼吧！”
		 */
		GameServer.getInstance().getWorldManager().sendEveryonePost(DC.getMarryString(isVip, inviter.getName(), source.getName()));
		
		ByteBuffer buffer = new ByteBuffer();
		buffer.writeBoolean(isVip);
		source.getRoom().dispatchMsg(SMsg.S_ROOM_MARRY_FLASH_COMMAND,buffer);
		
		source.sendFacebookInfo(2, null, inviter.getName(), 0);
		inviter.sendFacebookInfo(2, null, source.getName(), 0);
		
		source.updateRoleInfo();
		inviter.updateRoleInfo();
		
	}
}
