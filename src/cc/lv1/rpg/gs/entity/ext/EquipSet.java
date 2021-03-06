package cc.lv1.rpg.gs.entity.ext;

import vin.rabbit.util.ByteBuffer;
import cc.lv1.rpg.gs.entity.impl.Goods;
import cc.lv1.rpg.gs.entity.impl.Player;
import cc.lv1.rpg.gs.entity.impl.battle.SpriteBattleTmp;
import cc.lv1.rpg.gs.entity.impl.goods.GoodsEquip;
import cc.lv1.rpg.gs.entity.impl.pet.Pet;

/**
 * 装备面板
 * @author dxw
 *
 */
public class EquipSet extends PlayerExtInfo
{
	/**
	 * 装备不可直接修改玩家的值，例如装备一把武器，不能直接修改玩家的基础攻击
	 * 
	 * 
	 */

	private Player player;
	
	private PlayerBaseInfo baseInfo;
	
	/** 用来保存装备过的装备,用于判断紫色装备加技能 */
	private long[] equipIndex = new long[0];

	public EquipSet()
	{
	}
	
	public void addEquip(long index)
	{
		long [] infos = new long[equipIndex.length+1];
		for (int i = 0; i < equipIndex.length; i++)
			infos[i] = equipIndex[i];
		infos[equipIndex.length] = index;
		equipIndex = infos;
	}
	
	public boolean isHaveIndex(long index)
	{
		for (int i = 0; i < equipIndex.length; i++) 
		{
			if(equipIndex[i] == 0)
				continue;
			if(equipIndex[i] == index)
				return true;
		}
		return false;
	}
	
	public long[] getEquipList()
	{
		return this.equipIndex;
	}
	
	public void setPlayer(Player player)
	{
		this.player = player;
		this.baseInfo = player.getBaseInfo();
	}
	
	public String getName()
	{
		return "equipSet";
	}


	public void loadFrom(ByteBuffer buffer)
	{
		int count = buffer.readInt();
		equipIndex = new long[count];
		for (int i = 0; i < count; i++) 
		{
			long objectIndex = buffer.readLong();
			equipIndex[i] = objectIndex;
		}
	}


	public void saveTo(ByteBuffer buffer)
	{
		buffer.writeInt(equipIndex.length);
		for (int i = 0; i < equipIndex.length; i++) 
		{
			buffer.writeLong(equipIndex[i]);
		}
	}
	
	public void writeTo(ByteBuffer buffer)
	{
		player.writeTo(buffer);
		buffer.writeInt(player.maxHitPoint + getTotalAtt("maxHitPoint"));//最大生命
		buffer.writeInt(player.maxMagicPoint + getTotalAtt("maxMagicPoint"));//最大精力
		buffer.writeInt(baseInfo.power + getTotalAtt("power"));//力量
		buffer.writeInt(baseInfo.nimble + getTotalAtt("agility"));//敏捷
		buffer.writeInt(baseInfo.spirit + getTotalAtt("spirit"));//精神
		buffer.writeInt(baseInfo.wisdom + getTotalAtt("wisdom"));//智慧
		buffer.writeInt(0);//体质
		buffer.writeInt(baseInfo.curePoint + getTotalAtt("curePoint"));//治疗值
		buffer.writeInt(baseInfo.phsAtt + getTotalAtt("phyAtt"));//物理攻击
		buffer.writeInt(baseInfo.sptAtt + getTotalAtt("sptAtt"));//精神攻击
		buffer.writeInt(baseInfo.phsDef + getTotalAtt("phyDef"));//物理防御
		buffer.writeInt(baseInfo.sptDef + getTotalAtt("sptDef"));//精神防御
		int psr = baseInfo.phsSmiteRate + getTotalAtt("phySmiteRate");
		int ssr = baseInfo.sptSmiteRate + getTotalAtt("sptSmiteRate");
		int ss = baseInfo.spiritStand + getTotalAtt("spiritStand");
		if(psr > 10000)
			psr = 10000;
		if(ssr > 10000)
			ssr = 10000;
		if(ss > 10000)
			ss = 10000;
		buffer.writeInt(psr);//物理爆击率
		buffer.writeInt(ssr);//精神爆击率
		buffer.writeInt(ss);//精神抵抗
		buffer.writeInt(baseInfo.phsHurtAvoid + getTotalAtt("phyHurtAvoid"));//物理免伤
		buffer.writeInt(baseInfo.sptHurtAvoid + getTotalAtt("sptHurtAvoid"));//精神免伤
		buffer.writeInt(baseInfo.noDefPhsHurt + getTotalAtt("noDefPhyHurt"));//忽视防御的物理伤害
		buffer.writeInt(baseInfo.noDefSptHurt + getTotalAtt("noDefSptHurt"));//忽视防御的精神伤害
		buffer.writeInt((int)(SpriteBattleTmp.SMITERATE*10000)+baseInfo.phySmiteHurtParm + getTotalAtt("phySmiteHurtParm"));//物理暴击伤害倍率
		buffer.writeInt((int)(SpriteBattleTmp.SMITERATE*10000)+baseInfo.sptSmiteHurtParm + getTotalAtt("sptSmiteHurtParm"));//精神暴击伤害倍率
		buffer.writeInt(baseInfo.clearPhySmite + getTotalAtt("clearPhySmite"));//抗物理暴击
		buffer.writeInt(baseInfo.clearSptSmite + getTotalAtt("clearSptSmite"));//抗精神暴击
		buffer.writeInt(baseInfo.clearPhySmiteParm + getTotalAtt("clearPhySmiteParm"));//抗物理暴击倍率
		buffer.writeInt(baseInfo.clearSptSmiteParm + getTotalAtt("clearSptSmiteParm"));//抗物理暴击倍率
		buffer.writeInt(baseInfo.clearNoDefHurt + getTotalAtt("clearNoDefHurt"));
//		System.out.println("玩家："+player.name+" 的属性!");
//		System.out.println("抗忽视伤害:"+baseInfo.clearNoDefHurt+"---"+getTotalAtt("clearNoDefHurt"));
//		System.out.println("最大生命:"+player.maxHitPoint + "---" + getTotalAtt("maxHitPoint"));//最大生命
//		System.out.println("最大精力:"+player.maxMagicPoint + "---" + getTotalAtt("maxMagicPoint"));//最大精力
//		System.out.println("力量:"+baseInfo.power + "---" + getTotalAtt("power"));//力量
//		System.out.println("敏捷:"+baseInfo.nimble + "---" + getTotalAtt("agility"));//敏捷
//		System.out.println("精神:"+baseInfo.spirit + "---" + getTotalAtt("spirit"));//精神
//		System.out.println("智慧:"+baseInfo.wisdom + "---" + getTotalAtt("wisdom"));//智慧
//		System.out.println("体质:"+baseInfo.physique + "---" + getTotalAtt("physique"));//体质
//		System.out.println("治疗值:"+baseInfo.curePoint + "---" + getTotalAtt("curePoint"));//治疗值
//		System.out.println("物理攻击:"+baseInfo.phsAtt + "---" + getTotalAtt("phyAtt"));//物理攻击
//		System.out.println("精神攻击:"+baseInfo.sptAtt + "---" + getTotalAtt("sptAtt"));//精神攻击
//		System.out.println("物理防御:"+baseInfo.phsDef + "---" + getTotalAtt("phyDef"));//物理防御
//		System.out.println("精神防御:"+baseInfo.sptDef + "---" + getTotalAtt("sptDef"));//精神防御
//		System.out.println("物理爆击率:"+baseInfo.phsSmiteRate + "---" + getTotalAtt("phySmiteRate"));//物理爆击率
//		System.out.println("精神爆击率:"+baseInfo.sptSmiteRate + "---" + getTotalAtt("sptSmiteRate"));//精神爆击率
//		System.out.println("精神抵抗:"+baseInfo.spiritStand + "---" + getTotalAtt("spiritStand"));//精神抵抗
//		System.out.println("物理免伤:"+baseInfo.phsHurtAvoid + "---" + getTotalAtt("phyHurtAvoid"));//物理免伤
//		System.out.println("精神免伤:"+baseInfo.sptHurtAvoid + "---" + getTotalAtt("sptHurtAvoid"));//精神免伤
//		System.out.println("忽视防御的物理伤害:"+baseInfo.noDefPhsHurt + "---" + getTotalAtt("noDefPhyHurt"));//忽视防御的物理伤害
//		System.out.println("忽视防御的精神伤害:"+baseInfo.noDefSptHurt + "---" + getTotalAtt("noDefSptHurt"));//忽视防御的精神伤害
//		System.out.println("抗物理爆击:"+baseInfo.clearPhySmite + "---" + getTotalAtt("clearPhySmite"));//物理免伤
//		System.out.println("抗精神爆击:"+baseInfo.clearSptSmite + "---" + getTotalAtt("clearSptSmite"));//精神免伤
//		System.out.println("抗物理爆击伤害:"+baseInfo.clearPhySmiteParm + "---" + getTotalAtt("clearPhySmiteParm"));//忽视防御的物理伤害
//		System.out.println("抗精神爆击伤害:"+baseInfo.clearSptSmiteParm + "---" + getTotalAtt("clearSptSmiteParm"));//忽视防御的精神伤害
	}


	/**
	 * 获取玩家装备属性(装上装备后不是修改玩家的基础属性，只是在基础属性上增加)
	 * @return 玩家的装备属性
	 */
	public int getTotalAtt(String att)
	{
		int result = 0;
		Bag bag = (Bag) player.getExtPlayerInfo("bag");
		for (int i = 0; i < bag.getGoodsList().length; i++) 
		{
			if(bag.getGoodsList()[i] != null && bag.getGoodsList()[i] instanceof GoodsEquip)
			{
				GoodsEquip goods = (GoodsEquip) bag.getGoodsList()[i];
				if(goods.useFlag)
				{
				    if(att.equals("maxHitPoint") || att.equals("battleMaxHP") || att.equals("lifePoint"))
				    	result += goods.lifePoint + 5*goods.power;
				    else if(att.equals("maxMagicPoint") || att.equals("battleMaxMP") || att.equals("magicPoint"))
						result += goods.magicPoint + 5 * goods.spirit;
					else if(att.equals("power"))
						result += goods.power;
					else if(att.equals("agility") || att.equals("nimble"))
						result += goods.agility;
					else if(att.equals("spirit"))
						result += goods.spirit;
					else if(att.equals("wisdom"))
						result += goods.wisdom;
					else if(att.equals("curePoint"))
						result += goods.curePoint+(goods.agility+goods.wisdom)*10000/(Math.sqrt(player.level)*500);	
					else if(att.equals("phyAtt") || att.equals("phsAtt"))
						result += goods.phyAtt + 1.5 * goods.power;
					else if(att.equals("sptAtt"))
						result += goods.sptAtt + 1.5 * goods.spirit;
					else if(att.equals("phyDef") || att.equals("phsDef"))
						result += goods.phyDef;
					else if(att.equals("sptDef"))
						result += goods.sptDef;
					else if(att.equals("phySmiteRate") || att.equals("phsSmiteRate"))
						result += goods.phySmiteRate;
					else if(att.equals("sptSmiteRate"))
						result += goods.sptSmiteRate;
					else if(att.equals("phyHurtAvoid") || att.equals("phsHurtAvoid"))
						result += goods.phyHurtAvoid + goods.agility;
					else if(att.equals("sptHurtAvoid"))
						result += goods.sptHurtAvoid + goods.wisdom;
					else if(att.equals("noDefPhyHurt") || att.equals("noDefPhsHurt"))
						result += goods.noDefPhyHurt;
					else if(att.equals("noDefSptHurt"))
						result += goods.noDefSptHurt;
					else if(att.equals("phySmiteHurtParm") || att.equals("phsSmiteHurtParm") || att.equals("phsSmiteParm") || att.equals("phySmiteParm"))
						result += goods.phySmiteParm;
					else if(att.equals("sptSmiteHurtParm") || att.equals("sptSmiteParm"))
						result += goods.sptSmiteParm;
					else if(att.equals("clearPhySmite") || att.equals("clearPhsSmite"))
						result += goods.clearPhySmite;
					else if(att.equals("clearSptSmite"))
						result += goods.clearSptSmite;
					else if(att.equals("clearPhySmiteParm") || att.equals("clearPhsSmiteParm"))
						result += goods.clearPhySmiteParm;
					else if(att.equals("clearSptSmiteParm"))
						result += goods.clearSptSmiteParm;
					else if(att.equals("clearNoDefHurt"))
						result += goods.clearNoDefHurt;
				}
			}
			else 
				continue;
		}
		
		PetTome pt = (PetTome) player.getExtPlayerInfo("petTome");
		Pet p = pt.getActiveBattlePet();
		if(p != null && !p.isMaxBattlePoint())
		{
			result += p.getTotalAtt(att);
		}
		return result;
	}
	
	public void writeBaseTo(ByteBuffer buffer)
	{
		buffer.writeInt(player.maxHitPoint + getTotalAtt("maxHitPoint"));
		buffer.writeInt(player.maxMagicPoint + getTotalAtt("maxMagicPoint"));
	}
	
	
	/**
	 * 使用技能时检测是否有所属武器
	 * @param type
	 * @return
	 */
	public boolean checkEquip(int type)
	{
		if(type == 0)
			return true;
		boolean result = false;
		Bag bag = (Bag) player.getExtPlayerInfo("bag");
		for (int i = 0; i < bag.getGoodsList().length; i++) 
		{
			Goods goods = bag.getGoodsList()[i];
			if(!(goods instanceof GoodsEquip))
				continue;
			GoodsEquip equip = (GoodsEquip) goods;
			if(equip.useFlag && equip.equipLocation == 0)
			{
				if(equip.equipType == type || equip.equipType == 9)
				{
					result = true;
					break;
				}
			}
		}
		return result;
	}
	
}
