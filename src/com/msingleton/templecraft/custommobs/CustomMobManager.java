package com.msingleton.templecraft.custommobs;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Entity;

public class CustomMobManager {

	private List<CustomMob> CustomMobs = new ArrayList<CustomMob>();

	public void AddMob(CustomMob cm) 
	{
		this.CustomMobs.add(cm);
	}

	public void KillMob(CustomMob cm) 
	{
		RemoveMob(cm);
	}

	public void RemoveMob(CustomMob cm) 
	{
		this.CustomMobs.remove(cm);
	}

	public void Clear()
	{
		this.CustomMobs.clear();
	}

	public CustomMob getMob(Entity entity) 
	{
		for (CustomMob cm : this.CustomMobs) {
			if (cm.getLivingEntity() == entity) {
				return cm;
			}
		}
		return null;
	}
}