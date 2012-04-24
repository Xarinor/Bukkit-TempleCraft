package com.msingleton.templecraft.custommobs;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.msingleton.templecraft.TCUtils;


public class CustomMob{

	private LivingEntity entity;
	private List<CustomMobAbility> abilitys = new ArrayList<CustomMobAbility>();
	private int health = 0;
	private int maxhealth = 0;
	private int dmgmulti = 0;
	private int size = 0;

	public CustomMob(LivingEntity entity) 
	{
		this.entity = entity;
	}

	public void setHealth(int health) 
	{
		this.health = health;
		this.maxhealth = health;
	}

	public int getHealth() 
	{
		return health;
	}

	public int getMaxHealth() 
	{
		return maxhealth;
	}
	
	public void setDMGMultiplikator(int dmgmulti) 
	{
		this.dmgmulti = dmgmulti;
	}

	public int getDMGMultiplikator() 
	{
		return dmgmulti;
	}
	
	public void setSize(int size) 
	{
		this.size = size;
	}

	public int getSize() 
	{
		return size;
	}

	public void damage(int value, Entity damager) 
	{
		if(!entity.equals(damager))
		{
			health -= value;
			if (health <= 0) {
				entity.damage(entity.getMaxHealth()+1);
				if(entity instanceof EnderDragon)
				{
					entity.setHealth(0);
				}
			}
		}
	}

	public LivingEntity getLivingEntity()
	{
		return this.entity;
	}

	public List<CustomMobAbility> getAbilitys()
	{
		return this.abilitys;
	}

	public void setAbilitys(List<CustomMobAbility> abilitys)
	{
		if(abilitys != null)
		{
			this.abilitys = abilitys;
		}
	}

	public void addAbility(CustomMobAbility ability)
	{
		if(ability != null)
		{
			this.abilitys.add(ability);
		}
	}
	
	public void addAbilitysfromString(String abilitys)
	{
		String[] ability_split = abilitys.split(",");
		for(String ability : ability_split)
		{
			addAbility(CustomMobAbility.fromShortName(ability));
		}
	}

	public LivingEntity getTarget()
	{
		return TCUtils.getTarget(this.entity);
	}
}