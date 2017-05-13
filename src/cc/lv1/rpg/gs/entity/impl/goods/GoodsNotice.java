package cc.lv1.rpg.gs.entity.impl.goods;

import vin.rabbit.util.ByteBuffer;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import cc.lv1.rpg.gs.entity.impl.Goods;


public class GoodsNotice extends Goods 
{
	public static final String NO_NOTICE = "no";
	public static final String PARTY_BOSS = "partyBoss";
	public static final String TIME_TRAIN = "timeTrain";
	public static final String SPECIAL_BOSS = "specialBoss";
	public static final String COPY_BOX = "copyBox";
	public static final String MONEY_BATTLE = "moneyBattle";
	public static final String CHONG_GUAN = "chongGuan";
	public static final String STAR_BOX = "starBox";
	/**
	 * no:不公告
	 * partyBoss:活动BOSS公告
	 * timeTrain:时空之旅公告
	 * specialBoss:特殊BOSS
	 * copyBox:试练宝箱
	 * moneyBattle:夺宝奇兵
	 * chongGuan:奇妙大冲关
	 */
	public String noticeType;
	
	/**
	 * 物品的数量等于这个数量才公告,暂时只针对于copyBox和夺宝奇兵
	 */
	public int noticeCount;

	@Override
	public void onDeleteImpl(PlayerController target) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onRemoveImpl(PlayerController target) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onUseGoodsBattle(PlayerController player) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onUseImpl(PlayerController target) {
		// TODO Auto-generated method stub
		return false;
	}

}
