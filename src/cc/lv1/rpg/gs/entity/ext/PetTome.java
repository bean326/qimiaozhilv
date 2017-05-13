package cc.lv1.rpg.gs.entity.ext;

import java.text.SimpleDateFormat;

import javax.swing.text.StyledEditorKit.ForegroundAction;

import cc.lv1.rpg.gs.WorldManager;
import cc.lv1.rpg.gs.data.DataFactory;
import cc.lv1.rpg.gs.data.Exp;
import cc.lv1.rpg.gs.data.PetExp;
import cc.lv1.rpg.gs.entity.impl.Goods;
import cc.lv1.rpg.gs.entity.impl.Player;
import cc.lv1.rpg.gs.entity.impl.battle.skill.PetSkill;
import cc.lv1.rpg.gs.entity.impl.pet.Pet;
import vin.rabbit.util.ByteBuffer;
import vin.rabbit.util.Utils;
import vin.rabbit.util.collection.i.Map;

public class PetTome extends PlayerExtInfo
{
	/** 宠物喂养周期(24小时) */
	public static final long FEEDPETTIME = 1000*60*60*24;
	
	/** */
	public static final int BATTLEPETMAXCOUNT = 6;
	
	/** 玩家宠物列表 */
	private Pet[] pets = new Pet[0];
	 
	public void clear()
	{
		for (int i = 0; i < pets.length; i++)
		{
			if(pets[i] != null)
				pets[i].clear();
		}
	}
	
	public int getBattlePetCount()
	{
		int count = 0;
		for (int i = 0; i < pets.length; i++) 
		{
			if(pets[i] == null)
				continue;
			if(pets[i].petType == Pet.SHOUHUPET)
				count++;
		}
		return count;
	}
	
	public Pet getPetById(int petId)
	{
		for (int i = 0; i < pets.length; i++) 
		{
			if(pets[i] == null)
				continue;
			if(pets[i].id == petId)
				return pets[i];
		}
		return null;
	}
	
	public Pet getPet(long objectIndex)
	{
		for (int i = 0; i < pets.length; i++) 
		{
			if(pets[i] == null)
				continue;
			if(pets[i].objectIndex == objectIndex)
				return pets[i];
		}
		return null;
	}
	
	public Pet[] getPets()
	{
		return pets;
	}
	
	public void addPet(Pet pet)
	{
		Pet [] infos = new Pet[pets.length+1];
		for (int i = 0; i < pets.length; i++)
			infos[i] = pets[i];
		infos[pets.length] = pet;
		pets = infos;
	}
	
	public Pet getActivePet()
	{
		for (int i = 0; i < pets.length; i++) 
		{
			if(pets[i] == null)
				continue;
			if(pets[i].petType == Pet.PUTONGPET && pets[i].isActive)
				return pets[i];
		}
		return null;
	}
	
	public Pet getStudyingSkillBattlePet()
	{
		for (int i = 0; i < pets.length; i++) 
		{
			if(pets[i] == null)
				continue;
			PetSkill[] pss = pets[i].getPetSkills();
			for (int j = 0; j < pss.length; j++) 
			{
				if(pss[j] != null && pss[j].isStudying)
					return pets[i];
			}
		}
		return null;
	}
	
	public Pet getBattlePet(int petId)
	{
		for (int i = 0; i < pets.length; i++) 
		{
			if(pets[i] == null)
				continue;
			if(pets[i].id == petId && pets[i].petType == Pet.SHOUHUPET)
				return pets[i];
		}
		return null;
	}
	
	public Pet getActiveBattlePet()
	{
		for (int i = 0; i < pets.length; i++) 
		{
			if(pets[i] == null)
				continue;
			if(pets[i].petType == Pet.SHOUHUPET && pets[i].isActive)
				return pets[i];
		}
		return null;
	}
	
	public boolean isBattlePetMaxCount()
	{
		int count = 0;
		for (int i = 0; i < pets.length; i++) 
		{
			if(pets[i] == null)
				continue;
			if(pets[i].petType == Pet.SHOUHUPET)
				count++;
		}
		if(count < BATTLEPETMAXCOUNT)
			return false;
		return true;
	}
	
	public void clearActive(Pet pet)
	{
		for (int i = 0; i < pets.length; i++) 
		{
			if(pets[i] == null)
				continue;
			if(pets[i].id == pet.id)
				continue;
			if(pets[i].petType == pet.petType)
			{
				pets[i].isActive = false;
			}
		}
	}
	
	@Override
	public String getName() 
	{
		return "petTome";
	}

	@Override
	public void loadFrom(ByteBuffer buffer) 
	{
		int length = buffer.readInt();
		for (int i = 0; i < length; i++) 
		{
			int id = buffer.readInt();
			Pet pet = (Pet) DataFactory.getInstance().getGameObject(id);
			Pet newPet = (Pet) Pet.cloneObject(pet);
 
			newPet.loadFrom(buffer);
			newPet.fixPutongPetGrow();
			addPet(newPet);
		}
	}

	@Override
	public void saveTo(ByteBuffer buffer)
	{
		int number = 0;
		//记录物品数量
		for (int i = 0; i < pets.length; i++) 
		{
			if(pets[i] != null)
				number++;
		}
		buffer.writeInt(number);
		for (int i = 0; i < pets.length; i++)
		{
			if(pets[i] != null)
			{
				buffer.writeInt(pets[i].id);
				pets[i].saveTo(buffer);
			}
		}
	}
	
	public void writeTo(ByteBuffer buffer)
	{
		Pet pet = getActivePet();
		buffer.writeInt(pet.id);
		buffer.writeUTF(pet.name);
		buffer.writeInt(pet.modelId);
		buffer.writeInt(pet.level);
		buffer.writeUTF(pet.intimacyPoint+"");
		buffer.writeInt(pet.activePoint);
		buffer.writeInt(pet.growPoint);
		buffer.writeInt(pet.power);
		buffer.writeInt(pet.nimble);
		buffer.writeInt(pet.wisdom);
		buffer.writeInt(pet.spirit);
		buffer.writeInt(pet.lifePoint);
		buffer.writeInt(pet.magicPoint);
		buffer.writeInt(pet.gameTime);
		if(pet.nextExp == 0)
		{
			Map map = (Map)DataFactory.getInstance().getAttachment(DataFactory.ATTACH_PET_EXP);
			PetExp exp = (PetExp)map.get(pet.level+1);
			if(exp != null)
				pet.nextExp = exp.levelExp;
		}
		long exp = pet.nextExp - pet.requireExp;
		if(exp < 0)
			exp = 0;
		buffer.writeUTF(exp+"");
		buffer.writeUTF(pet.nextExp+"");

		if(pet.nextExp <= 0)
			buffer.writeInt(10000);
		else
		{
			int rate = (int) (exp * 10000 / pet.nextExp);
			buffer.writeInt(rate);
		}
		
		buffer.writeInt(pet.feedCount);
		buffer.writeInt(pet.maxFeedCount);
	}
	
	
	public void readFrom(ByteBuffer byteBuffer)
	{
		Pet pet = getActivePet();
		if(pet == null)
			return;
		pet.name = byteBuffer.readUTF();
		pet.activePoint = byteBuffer.readInt();
		Pet tmp = (Pet) DataFactory.getInstance().getGameObject(pet.id);
		if(pet.activePoint > tmp.activePoint)
			pet.activePoint = tmp.activePoint;
		pet.feedCount = byteBuffer.readInt();
		if(pet.feedCount > pet.maxFeedCount)
			pet.feedCount = pet.maxFeedCount;
		pet.growPoint = byteBuffer.readInt();
		pet.modelId = byteBuffer.readInt();

		pet.power = byteBuffer.readInt();
		pet.spirit = byteBuffer.readInt();
		pet.wisdom = byteBuffer.readInt();
		pet.nimble = byteBuffer.readInt();
	}
	
	

}
