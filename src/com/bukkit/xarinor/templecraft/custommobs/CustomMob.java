package com.bukkit.xarinor.templecraft.custommobs;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Entity;

import com.bukkit.xarinor.templecraft.TCUtils;
import com.bukkit.xarinor.templecraft.util.MobSpawnProperties;
import com.bukkit.xarinor.templecraft.util.Pair;

/**
* CustomMob.java
* This work is dedicated to the public domain.
* 
* @author Xarinor
* @author bootscreen
* @author msingleton
*/
public class CustomMob{

	private Entity entity;
	private List<CustomMobAbility> abilitys = new ArrayList<CustomMobAbility>();
	private boolean dead = false;
	private int health = 0;
	private int maxhealth = 0;
	private int dmgmulti = 0;
	private int size = 0;
	private List<Pair<CustomMobAbility,Integer>> abilities_random = new ArrayList<Pair<CustomMobAbility,Integer>>();
	private List<Pair<CustomMobAbility,Integer>> abilities_rotation = new ArrayList<Pair<CustomMobAbility,Integer>>();
	private MobSpawnProperties spawnProperties = null;

	/**
	 * Constructor
	 * 
	 * @param entity -Mob
	 */
	public CustomMob(Entity entity) {
		this.entity = entity;
	}
	
	/**
	 * Remove this CustomMob
	 */
	public void remove() {
		this.entity.remove();
	}

	/**
	 * Return if living CustomMob is dead
	 * TODO Check
	 * @return
	 */
	public boolean isEntityDead() {
		return entity.isDead();
	}
	
	/**
	 * Return if CustomMob is dead
	 * TODO Check
	 * 
	 * @return
	 */
	public boolean isDead() {
		return dead;
	}

	/**
	 * Sets this CustomMob to dead
	 * 
	 * @param dead -boolean dead or not
	 */
	public void setDead(boolean dead) {
		this.dead = dead;
	}

	/**
	 * Sets this CustomMobs health pool to an amount
	 * 
	 * @param health -Health amount
	 */
	public void setHealth(int health) {
		this.health = health;
		this.maxhealth = health;
	}

	/**
	 * Get current health of this CustomMob
	 * @return
	 */
	public int getHealth() {
		return health;
	}

	/**
	 * Get maximum health of this CustomMob
	 * @return
	 */
	public int getMaxHealth() {
		return maxhealth;
	}

	/**
	 * Sets this CustomMobs dmg-multiplier
	 * @param dmgmulti
	 */
	public void setDMGMultiplikator(int dmgmulti) {
		this.dmgmulti = dmgmulti;
	}

	/**
	 * Gets the dmg multiplier back
	 * 
	 * @return
	 */
	public int getDMGMultiplikator() {
		return dmgmulti;
	}

	/**
	 * Sets the size of this CustomMob
	 * 
	 * @param size
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * Gets the size of this CustomMob
	 * 
	 * @param size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Damages this CustomMob
	 * 
	 * @param value		-damage to be done
	 * @param damager	-damage source
	 */
	public void damage(int value, Entity damager) {
		if(!entity.equals(damager)) {
			health -= value;
			if (health <= 0) {
				this.dead = true;
				//entity.damage(entity.getMaxHealth());	
				//livingEntity.damage(livingEntity.getMaxHealth());				
			}
		}
	}
	
	/**
	 * Gets this as LivingEntity
	 * 
	 * TODO Add living
	 * 
	 * @return
	 */
	public Entity getEntity() {
		return this.entity;
	}

	/**
	 * Gets the abilities of this CustomMob
	 * @return
	 */
	public List<CustomMobAbility> getAbilitys() {
		return this.abilitys;
	}

	/**
	 * Sets this CustomMobs abilities
	 * @param abilitys
	 */
	public void setAbilitys(List<CustomMobAbility> abilitys) {
		if(abilitys != null) {
			this.abilitys = abilitys;
		}
	}

	/**
	 * Adds a ability
	 * 
	 * @param ability
	 */
	public void addAbility(CustomMobAbility ability) {
		if(ability != null) {
			this.abilitys.add(ability);
		}
	}

	/**
	 * Adds abilities from a string
	 * 
	 * @param abilitys
	 */
	public void addAbilitysfromString(String abilitys) {
		String[] ability_split = abilitys.split(",");
		for(String ability : ability_split)
		{
			addAbility(CustomMobAbility.fromShortName(ability));
		}
	}
	
	/**
	 * Gets the target of this CustomMob
	 * 
	 * @return
	 */
	public Entity getTarget() {
		return TCUtils.getTarget(this.entity);
	}

	/**
	 * Gets the ability-rotation
	 * 
	 * @return
	 */
	public List<Pair<CustomMobAbility,Integer>> getAbilities_rotation() {
		return abilities_rotation;
	}

	/**
	 * Sets the ability-rotation
	 * 
	 * @param abilities_rotation
	 */
	public void setAbilities_rotation(List<Pair<CustomMobAbility,Integer>> abilities_rotation) {
		this.abilities_rotation = abilities_rotation;
	}

	/**
	 * get random
	 * 
	 * @return
	 */
	public List<Pair<CustomMobAbility,Integer>> getAbilities_random() {
		return abilities_random;
	}

	/**
	 * set random
	 * 
	 * @param abilities_random
	 */
	public void setAbilities_random(List<Pair<CustomMobAbility,Integer>> abilities_random) {
		this.abilities_random = abilities_random;
	}

	/**
	 * gets the spawn properties of this CustomMob
	 * 
	 * @return
	 */
	public MobSpawnProperties getSpawnProperties() {
		return spawnProperties;
	}
	
	/**
	 * Sets the spawn properties
	 * 
	 * @param spawnProperties
	 */
	public void setSpawnProperties(MobSpawnProperties spawnProperties) {
		this.spawnProperties = spawnProperties;
	}
}