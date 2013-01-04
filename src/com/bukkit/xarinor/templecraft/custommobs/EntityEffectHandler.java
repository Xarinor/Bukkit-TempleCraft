package com.bukkit.xarinor.templecraft.custommobs;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;

public class EntityEffectHandler {

	/**
	 * Removes all PotionEffects from a living entity
	 * Ignores non-living 
	 * 
	 * @param p entity
	 */
	public static void removeAllPotionEffects(Entity entity) {
		if (entity instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity)entity;
			for (PotionEffect pot : livingEntity.getActivePotionEffects()) {
				livingEntity.removePotionEffect(pot.getType());
			}
		}
	}

}
