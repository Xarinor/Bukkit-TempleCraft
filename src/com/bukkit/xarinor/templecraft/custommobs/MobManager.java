package com.bukkit.xarinor.templecraft.custommobs;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Entity;

/**
* MobManager.java
* This work is dedicated to the public domain.
* 
* @author Xarinor
* @author bootscreen
* @author msingleton
*/
public class MobManager {

	private List<CustomMob> CustomMobs = new ArrayList<CustomMob>();

	/**
	 * Add custom mob
	 * 
	 * @param cm -mob
	 */
	public void AddMob(CustomMob cm) {
		this.CustomMobs.add(cm);
	}

	/**
	 * Kill custom mob
	 * 
	 * @param cm -mob
	 */
	public void KillMob(CustomMob cm) {
		cm.damage(cm.getHealth(), null);
		RemoveMob(cm);
	}

	/**
	 * Remove custom mob
	 * 
	 * @param cm -mob
	 */
	public void RemoveMob(CustomMob cm) {
		this.CustomMobs.remove(cm);
	}

	/**
	 * Clear all custom mobs
	 */
	public void clear() {
		for(CustomMob cm : CustomMobs) {
			cm.remove();
		}
		this.CustomMobs.clear();
	}

	/**
	 * Get a custom mob
	 * 
	 * @param entity -Mob
	 * @return
	 */
	public CustomMob getMob(Entity entity) {
		for (CustomMob cm : this.CustomMobs) {
			//TODO living
			if (cm.getEntity() == entity) {
				return cm;
			}
		}
		return null;
	}

	/**
	 * Get all custom mobs
	 * 
	 * @return
	 */
	public List<CustomMob> getMobs() {
		return CustomMobs;
	}
}