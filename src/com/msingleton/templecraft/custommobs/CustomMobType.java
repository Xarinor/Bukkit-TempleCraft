package com.msingleton.templecraft.custommobs;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.EntityType;

import com.msingleton.templecraft.util.Pair;

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
	private int size = 0;
	private String name = "";
	private String oldabilitys = "";
	private List<Pair<CustomMobAbility,Integer>> abilities_random = new ArrayList<Pair<CustomMobAbility,Integer>>();
	private List<Pair<CustomMobAbility,Integer>> abilities_rotation = new ArrayList<Pair<CustomMobAbility,Integer>>();

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
	public int getSize() {
		return size;
	}

	/**
	 * Set mob size
	 * 
	 * @param size
	 */
	public void setSize(int size) {
		this.size = size;
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
	 * Get old abilities
	 * 
	 * @return
	 */
	public String getOldabilitys() {
		return oldabilitys;
	}

	/**
	 * Set old abilities
	 * 
	 * @param oldabilitys
	 */
	public void setOldabilitys(String oldabilitys) {
		this.oldabilitys = oldabilitys;
	}

	/**
	 * Get ability roatation
	 * 
	 * @return
	 */
	public List<Pair<CustomMobAbility,Integer>> getAbilities_rotation() {
		return abilities_rotation;
	}

	/**
	 * Set random abilities
	 * 
	 * @return
	 */
	public List<Pair<CustomMobAbility,Integer>> getAbilities_random() {
		return abilities_random;
	}

	/**
	 * Set random abilities
	 * 
	 * @param abilities_random
	 */
	public void setAbilities_random(List<Pair<CustomMobAbility,Integer>> abilities_random) {
		this.abilities_random = abilities_random;
	}
	
	/**
	 * Add random abilities
	 * 
	 * @param cma	-Ability
	 * @param i		-no.
	 */
	public void addAbilities_random(CustomMobAbility cma, int i) {
		this.abilities_random.add(new Pair<CustomMobAbility,Integer>(cma, i));
	}

	/**
	 * Set ability rotation
	 * 
	 * @param abilities_rotation
	 */
	public void setAbilities_rotation(List<Pair<CustomMobAbility,Integer>> abilities_rotation) {
		this.abilities_rotation = abilities_rotation;
	}

	/**
	 * add ability to rotation
	 * 
	 * @param cma	-Ability
	 * @param i		-no.
	 */
	public void addAbilities_rotation(CustomMobAbility cma, int i) {
		this.abilities_rotation.add(new Pair<CustomMobAbility,Integer>(cma, i));
	}
}