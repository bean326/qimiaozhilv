package cc.lv1.rpg.gs.net.impl;

import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.dao.DatabaseAccessor;
import cc.lv1.rpg.gs.entity.controller.PlayerController;
import vin.rabbit.net.abs.NetConnection;
import vin.rabbit.net.job.i.NetJob;
/**
 * 买东西事务
 * @author andrew
 *
 */
public class PayJob extends NetJob
{

	public static final int MONEY = 1;
	
	public static final int GOODS = 2;
	
	private DatabaseAccessor dao;
	
	private PlayerController target;
	
	private int roleId;
	
	private int payType;
	
	private int payState;
	
	private int payMoney;
	
	private String payExp;
	
	private boolean isInit = false;
	
	/**
	 * 充值购物
	 * @param target 充值目标
	 * @param payType 充值类型  1充值 2购物 3抽奖>500 4.4399手机包月 5礼包接口上下线兑换 8.礼券兑换 9卖出大于500
	 * @param payState 0已充  1未充
	 * @param payMoney 消费金额/充值金额
	 * @param payExp 消费备注/充值串号
	 */
	public PayJob(PlayerController target,int payType,int payState,int payMoney,String payExp)
	{
		this.payType = payType;
		this.payState = payState;
		this.payMoney = payMoney;
		this.payExp = payExp;
		
		if(target==null)
			return;
		
		this.target = target;
		
		roleId = target.getID();
		
		WorldManager world = target.getWorldManager();
		
		if(world == null)
		{
			target.close();
			return;
		}
		
		dao = world.getDatabaseAccessor();
		
		isInit = true;
	}

	public NetConnection getConnection()
	{
		return target.getNetConnection();
	}


	public void run()
	{
		if(!isInit)
			return;
		
			dao.addPaymentInfo(roleId,target.getPlayer().accountName, payType,payState, payMoney, payExp);
	}

}
