package cc.lv1.rpg.gs.entity.impl;

import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.impl.ArrayList;
import vin.rabbit.util.collection.impl.HashMap;
import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.entity.RPGameObject;
import cc.lv1.rpg.gs.entity.controller.PlayerController;

/**
 * id: id为0时表示是当天所有玩家的总次数记录
 * @author bean
 *
 */
public class LogTemp extends RPGameObject
{
	private String accountName;
	
	private int money;
	
	private int point;
	
	private int equipMoney;
	
	private int level;
	
	private String time = "";
	
	private String data = "";
	

	/**
	 * 杀怪记录时count表示是杀怪的总次数
	 * 炼化记录时count表示是炼化的总次数
	 */
	private int count = 1;
	
	private HashMap goodsMap = new HashMap();
	
	private ArrayList goodsList = new ArrayList();
	
	public LogTemp(){}
	
	public LogTemp(PlayerController target,String data)
	{
		this.name = target.getName();
		this.accountName = target.getPlayer().accountName;
		this.time = WorldManager.getTypeTime("yyyy-MM-dd-HH:mm:ss", System.currentTimeMillis());
		this.data = data;
		this.level = target.getPlayer().level;
	}
	
	
	/**
	 * @return the accountName
	 */
	public String getAccountName() {
		return accountName;
	}


	/**
	 * @param accountName the accountName to set
	 */
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}


	/**
	 * @return the money
	 */
	public int getMoney() {
		return money;
	}


	/**
	 * @param money the money to set
	 */
	public void setMoney(int money) {
		this.money = money;
	}


	/**
	 * @return the point
	 */
	public int getPoint() {
		return point;
	}
	/**
	 * @return the equipMoney
	 */
	public int getEquipMoney() {
		return equipMoney;
	}


	/**
	 * @param equipMoney the equipMoney to set
	 */
	public void setEquipMoney(int equipMoney) {
		this.equipMoney = equipMoney;
	}

	/**
	 * @param point the point to set
	 */
	public void setPoint(int point) {
		this.point = point;
	}

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}


	/**
	 * @param count the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}


	/**
	 * @return the time
	 */
	public String getTime() {
		return time;
	}


	/**
	 * @param time the time to set
	 */
	public void setTime(String time) {
		this.time = time;
	}


	/**
	 * @return the data
	 */
	public String getData() {
		return data;
	}


	/**
	 * @param data the data to set
	 */
	public void setData(String data) {
		this.data = data;
	}


	/**
	 * @return the goodsMap
	 */
	public HashMap getGoodsMap() {
		return goodsMap;
	}


	/**
	 * @param goodsMap the goodsMap to set
	 */
	public void setGoodsMap(HashMap goodsMap) {
		this.goodsMap = goodsMap;
	}


	/**
	 * @return the goodsList
	 */
	public ArrayList getGoodsList() {
		return goodsList;
	}

	/**
	 * @param goodsList the goodsList to set
	 */
	public void setGoodsList(ArrayList goodsList) {
		this.goodsList = goodsList;
	}
	
	
	
	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	public void putGoodsMap(int id,String name,int count)
	{
		if(goodsMap.get(id) != null)
		{
			UseGoods uGoods = (UseGoods) goodsMap.get(id);
			uGoods.goodsCount += count;
		}
		else
		{
			UseGoods ug = new UseGoods();
			ug.goodsId = id;
			ug.goodsName = name;
			ug.goodsCount = count;
			goodsMap.put(id, ug);
			goodsList.add(ug);
		}
	}
	
	public void updateUseGoods(ArrayList list)
	{
		for (int i = 0; i < list.size(); i++) 
		{
			UseGoods ug = (UseGoods) list.get(i);
			if(goodsMap.get(ug.goodsId) != null)
			{
				UseGoods uGoods = (UseGoods) goodsMap.get(ug.goodsId);
				uGoods.goodsCount += ug.goodsCount;
			}
			else
			{
				putGoodsMap(ug.goodsId,ug.goodsName,ug.goodsCount);
			}
		}
	}
	
	public String getGoodsStr()
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < goodsList.size(); i++) 
		{
			UseGoods ug = (UseGoods) goodsList.get(i);
			sb.append(ug.goodsName);
			sb.append("x");
			sb.append(ug.goodsCount);
			if(i != goodsList.size()-1)
				sb.append(",");
		}
		return sb.toString();
	}
	
	public void setGoods(String str)
	{
		if(str.length() <= 1)
			return;
		String[] strs = Utils.split(str, ",");
		for (int i = 0; i < strs.length; i++)
		{
			String[] gStrs = Utils.split(strs[i], "x");
			Goods goods = DataFactory.getInstance().getGoodsByName(gStrs[0]);
			if(goods == null)
			{
				System.out.println("Log temp goods name is modify:"+gStrs[0]);
				continue;
			}
			UseGoods ug = new UseGoods();
			ug.goodsId = goods.id; 
			ug.goodsName = goods.name;
			ug.goodsCount = Integer.parseInt(gStrs[1]);
			goodsMap.put(ug.goodsId, ug);
			goodsList.add(ug);
		}
	}
	

	class UseGoods
	{
		public int goodsId;
		
		public String goodsName;
		
		public int goodsCount = 1;
	}
}
