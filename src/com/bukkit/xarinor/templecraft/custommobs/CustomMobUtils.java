package com.bukkit.xarinor.templecraft.custommobs;

import java.io.File;
//import java.util.HashMap;
import java.util.HashSet;
//import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import com.bukkit.xarinor.templecraft.TCUtils;

/**
* CustomMobUtils.java
* This work is dedicated to the public domain.
* 
* TODO !
* 
* @author Xarinor
* @author bootscreen
* @author msingleton
*/
public class CustomMobUtils {
	protected static File configFile				= null;
	public static Set<CustomMobType> custommobs	= new HashSet<CustomMobType>();
	public static Set<String> custommobnames		= new HashSet<String>();

	/**
	 * load bosses from boss config file
	 */
	public static void loadBosses() {
		configFile	= TCUtils.getConfig("bosses");
		if(configFile.exists()) {
			custommobnames = getBossNames();
			if(custommobnames != null) {
				custommobs  = getBosses(configFile, "bosses.");
			}
		}
	}

	/**
	 * Get boss names
	 * Not fully functional
	 * 
	 * @return
	 */
	private static Set<String> getBossNames()
	{
		YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

		if(config.isSet("bosses")) {
			return config.getConfigurationSection("bosses").getKeys(false);
		} else {
			return null;
		}
	}

	/**
	 * Get bosses from config
	 * 
	 * TODO Looks interesting :)
	 * 
	 * @param configFile -File name
	 * @param path		 -file path
	 * @return
	 */
	private static Set<CustomMobType> getBosses(File configFile, String path) {
		YamlConfiguration c = YamlConfiguration.loadConfiguration(configFile);

		Set<CustomMobType> result = new HashSet<CustomMobType>();
		for (String s : custommobnames) {
			CustomMobType cmt = new CustomMobType();
			try {
				cmt.setName(s);
				cmt.setMobtype(EntityType.fromName(c.getString(path + s + ".Spawning.MobType", null)));
				cmt.setDmgmulti(c.getInt(path + s + ".Spawning.DMGMultiplicator", 0));
				cmt.setMaxhealth(c.getInt(path + s + ".Spawning.MaxHealth", 0));
				cmt.setMode(c.getInt(path + s + ".Spawning.Mode", -1));
				cmt.setCount(c.getInt(path + s + ".Spawning.Count", 1));
				cmt.setRange(c.getInt(path + s + ".Spawning.SpawnRange", 1));
				cmt.setAbilitys(c.getString(path + s + ".Spawning.Abilities", null));
				result.add(cmt);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * Get all custom mobs
	 * 
	 * @return
	 */
	public Set<CustomMobType> getBosses() {
		return custommobs;
	}

	/**
	 * Get a boss per name
	 * 
	 * @param name
	 * @return
	 */
	public static CustomMobType getBoss(String name) {
		for(CustomMobType cmt : custommobs) {
			if(cmt.getName().equals(name)) {
				return cmt;
			}
		}
		return null;
	}

	/**
	 * Check if a custom mob boss exists
	 * 
	 * @param name
	 * @return
	 */
	public static boolean bossTypeExists(String name) {
		for(CustomMobType cmt : custommobs) {
			if(cmt.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}
}
