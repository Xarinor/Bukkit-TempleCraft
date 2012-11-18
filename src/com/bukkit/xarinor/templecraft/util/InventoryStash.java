package com.bukkit.xarinor.templecraft.util;

import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;

/**
 * EnumUtils.java
 * This work is dedicated to the public domain.
 * 
 * Stash for all items, exp, gamemode, healt- and foodlevel
 * 
 * @author tommytony
 * @author Xarinor
 */
public class InventoryStash {
	private ItemStack[] contents;
	private ItemStack helmet;
	private ItemStack chest;
	private ItemStack legs;
	private ItemStack feet;
	private double health;
	private int foodLevel;
	private int experience;
	private GameMode gameMode;

	public InventoryStash(ItemStack[] contents) {
		this.setContents(contents);
	}

	/**
	 * Constructor
	 * 
	 * @param contents	 -Items
	 * @param helmet	 -Helmet
	 * @param chest		 -Chest
	 * @param legs		 -Legs
	 * @param feet		 -Feet
	 * @param health	 -Health amount
	 * @param foodLevel	 -Food level
	 * @param experience -Experience amount
	 * @param gameMode	 -gamemode
	 */
	public InventoryStash(ItemStack[] contents, ItemStack helmet, ItemStack chest, ItemStack legs, ItemStack feet, double health, int foodLevel, int experience, GameMode gameMode) {
		this.setContents(contents);
		this.setHelmet(helmet);
		this.setChest(chest);
		this.setLegs(legs);
		this.setFeet(feet);
		this.setHealth(health);
		this.setFoodLevel(foodLevel);
		this.setExperience(experience);
		this.setGameMode(gameMode);
	}

	/**
	 * Sets all submitted items and data as contents
	 * 
	 * @param contents -Items/ExpCount/Health/FoodLevel
	 */
	public void setContents(ItemStack[] contents) {
		this.contents = contents;
	}

	/**
	 * Gets all set items and data from the contents
	 * 
	 * @return -Contents
	 */
	public ItemStack[] getContents() {
		return contents;
	}

	/**
	 * Adds additional items to the contents
	 * 
	 * @param item	-New item
	 */
	public void addContent(ItemStack item) {
		ItemStack[] result = new ItemStack[this.contents.length + 1];
		System.arraycopy(contents, 0, result, 0, contents.length);
		result[result.length-1] = item;
		this.contents = result;
	}
	
	/**
	 * Sets the helmet in contents
	 * 
	 * @param helmet
	 */
	public void setHelmet(ItemStack helmet) {
		this.helmet = helmet;
	}

	/**
	 * Gets the helmet from contents
	 * 
	 * @return
	 */
	public ItemStack getHelmet() {
		return helmet;
	}

	/**
	 * Sets the chest in contents
	 * 
	 * @param chest
	 */
	public void setChest(ItemStack chest) {
		this.chest = chest;
	}

	/**
	 * Gets the chest from contents
	 * 
	 * @return
	 */
	public ItemStack getChest() {
		return chest;
	}

	/**
	 * Sets the legs in contents
	 * 
	 * @param legs
	 */
	public void setLegs(ItemStack legs) {
		this.legs = legs;
	}

	/**
	 * gets the Legs from contents
	 * 
	 * @return
	 */
	public ItemStack getLegs() {
		return legs;
	}

	/**
	 * Sets the feet in contents
	 * 
	 * @param feet
	 */
	public void setFeet(ItemStack feet) {
		this.feet = feet;
	}

	/**
	 * Gets the feet from contents
	 * 
	 * @return
	 */
	public ItemStack getFeet() {
		return feet;
	}

	/**
	 * Sets the health in contents
	 * 
	 * @param health
	 */
	public void setHealth(double health) {
		this.health = health;
	}

	/**
	 * Gets the health from contents
	 * 
	 * @return
	 */
	public double getHealth() {
		return health;
	}

	/**
	 * Sets the food level in contents
	 * 
	 * @param foodLevel
	 */
	public void setFoodLevel(int foodLevel) {
		this.foodLevel = foodLevel;
	}

	/**
	 * Gets the food level from contents
	 * 
	 * @return
	 */
	public int getFoodLevel() {
		return foodLevel;
	}

	/**
	 * Sets the experience in contents
	 * 
	 * @param experience
	 */
	public void setExperience(int experience) {
		this.experience = experience;
	}

	/**
	 * Gets the experience from contents
	 * 
	 * @return
	 */
	public int getExperience() {
		return experience;
	}

	/**
	 * Sets the game mode in contents
	 * 
	 * @param gameMode
	 */
	public void setGameMode(GameMode gameMode) {
		this.gameMode = gameMode;
	}

	/**
	 * Gets the game mode from contents
	 * 
	 * @return
	 */
	public GameMode getGameMode() {
		return gameMode;
	}
}
