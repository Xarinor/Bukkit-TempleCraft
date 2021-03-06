package com.bukkit.xarinor.templecraft.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import com.bukkit.xarinor.templecraft.custommobs.CustomMobAbility;
import com.bukkit.xarinor.templecraft.games.Game;

/**
* MobSpawnProperties.java
* This work is dedicated to the public domain.
* 
* @author Xarinor
* @author bootscreen
* @author msingleton
*/
public class MobSpawnProperties {
	protected Game game;
	protected Location loc;
	protected EntityType mob;
	protected int range;
	protected int mode;
	protected int health;
	protected int dmgmulti;
	protected String abilitys;
	protected int count;
	protected long time;
	protected boolean isbossmob;
	protected List<Pair<CustomMobAbility,Integer>> abilities_random = new ArrayList<Pair<CustomMobAbility,Integer>>();
	protected List<Pair<CustomMobAbility,Integer>> abilities_rotation = new ArrayList<Pair<CustomMobAbility,Integer>>();

	/**
	 * Constructor
	 */
	public MobSpawnProperties() {
		game = null;
		loc = null;
		mob = null;
		mode = -1;
		range = 20;
		health = 0;
		dmgmulti = 1;
		abilitys = "";
		count = 1;
		time = 0;
		isbossmob = false;
		abilities_random.clear();
		abilities_rotation.clear();
	}
	
	/**
	 * Sets a game
	 * 
	 * @param game
	 */
	public void setGame(Game game) {
		this.game = game;
	}

	/**
	 * Gets a game
	 * 
	 * @return game
	 */
	public Game getGame() {
		return game;
	}

	/**
	 * Sets a location
	 * 
	 * @param loc -location
	 */
	public void setLocation(Location loc) {
		this.loc = loc;
	}

	/**
	 * Gets a location
	 * 
	 * @return loc -location
	 */
	public Location getLocation() {
		return loc;
	}

	/**
	 * Sets an entity type
	 * 
	 * @param mob -entity type
	 */
	public void setEntityType(EntityType mob) {
		this.mob = mob;
	}

	/**
	 * Gets an entity type
	 * 
	 * @return mob -entity type
	 */
	public EntityType getEntityType() {
		return mob;
	}
	
	/**
	 * Sets an entities mode
	 * 
	 * @param mode -entity mode
	 */
	public void setMode(int i) {
		this.mode = i;
	}

	/**
	 * Gets an entities mode
	 * 
	 * @return size -entity size
	 */
	public int getMode() {
		return mode;
	}
	
	/**
	 * Sets an entities range
	 * 
	 * @param range -entity range
	 */
	public void setRange(int range) {
		this.range = range;
	}

	/**
	 * Gets an entities range
	 * 
	 * @return range -entity range
	 */
	public int getRange() {
		return range;
	}
	
	/**
	 * Sets an entities health
	 * 
	 * @param health -entity health
	 */
	public void setHealth(int health) {
		this.health = health;
	}

	/**
	 * Gets an entities health
	 * 
	 * @return health -entity health
	 */
	public int getHealth() {
		return health;
	}

	/**
	 * Sets an entities dmg multiplier
	 * 
	 * @param dmgmulti -entity dmg multiplier
	 */
	public void setDMGMulti(int dmgmulti) {
		this.dmgmulti = dmgmulti;
	}

	/**
	 * Gets an entities dmg multiplier
	 * 
	 * @return dmgmulti -entity dmg multiplier
	 */
	public int getDMGMulti() {
		return dmgmulti;
	}

	/**
	 * Sets an entities abilities
	 * 
	 * @param abilitys -entity abilities
	 */
	public void setAbilitys(String abilitys) {
		this.abilitys = abilitys;
	}

	/**
	 * Gets an entities abilities
	 * 
	 * @return abilitys -entity abilities
	 */
	public String getAbilitys() {
		return abilitys;
	}

	/**
	 * Sets the count of entities
	 * 
	 * @param count -entity count
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * Gets the count of entities
	 * 
	 * @return count -entity count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * Sets the spawn delay timer
	 * 
	 * @param time -entity count
	 */
	public void setTime(long time) {
		this.time = time;
	}

	/**
	 * Gets the spawn delay timer
	 * 
	 * @return time -entity count
	 */
	public long getTime() {
		return time;
	}
	
	/**
	 * Returns if this is a boss mob
	 * 
	 * @return boolean -isbossmob
	 */
	public boolean isIsbossmob() {
		return isbossmob;
	}

	/**
	 * Sets a mob to a boss mob
	 * 
	 * @param isbossmob -boolean
	 */
	public void setIsbossmob(boolean isbossmob) {
		this.isbossmob = isbossmob;
	}
}
