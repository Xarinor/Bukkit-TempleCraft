package com.bukkit.xarinor.templecraft.custommobs;

import org.bukkit.entity.EntityType;

/**
* CustomMobType.java
* This work is dedicated to the public domain.
* 
* @author Xarinor
* @author bootscreen
* @author msingleton
*/
public class CustomMobType {

	private EntityType mobtype = null;
	private int maxhealth = 0;
	private int dmgmulti = 0;
	private int count = 1;
	private int range = 20;
	private int mode = -1;
	private String name = "";
	private String abilitys;

	/**
	 * Get the base mob type
	 * 
	 * @return
	 */
	public EntityType getMobtype() {
		return mobtype;
	}

	/**
	 * Set base mob type
	 * 
	 * @param mobtype
	 */
	public void setMobtype(EntityType mobtype) {
		this.mobtype = mobtype;
	}

	/**
	 * Get maximum health
	 * 
	 * @return
	 */
	public int getMaxhealth() {
		return maxhealth;
	}

	/**
	 * Set maximum health
	 * @param maxhealth
	 */
	public void setMaxhealth(int maxhealth) {
		this.maxhealth = maxhealth;
	}

	/**
	 * Get dmg multiplier
	 * 
	 * @return
	 */
	public int getDmgmulti() {
		return dmgmulti;
	}

	/**
	 * Set dmg multiplier
	 * 
	 * @param dmgmulti
	 */
	public void setDmgmulti(int dmgmulti) {
		this.dmgmulti = dmgmulti;
	}
	
	/**
	 * Get number of mobs
	 * 
	 * @return
	 */
	public int getCount() {
		return count;
	}

	/**
	 * Set number of mobs
	 * 
	 * @param count
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * Get radius for the mob to spawn
	 * 
	 * @return
	 */
	public int getRange() {
		return range;
	}

	/**
	 * Set radius for the mob to spawn
	 * 
	 * @param range
	 */
	public void setRange(int range) {
		this.range = range;
	}

	/**
	 * Get mob size
	 * 
	 * @return
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * Set mob size
	 * 
	 * @param size
	 */
	public void setMode(int i) {
		this.mode = i;
	}
	
	/**
	 * Get mob name
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set mob name
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get mob abilities
	 * @return 
	 */
	public String getAbilitys() {
		return abilitys;
	}
	
	/**
	 * Set mob abilities
	 */
	public void setAbilitys(String a) {
		this.abilitys = a;
	}
}