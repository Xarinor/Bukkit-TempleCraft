package com.msingleton.templecraft.custommobs;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import com.msingleton.templecraft.TCUtils;

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
			if(custommobnames != null && !custommobnames.isEmpty()) {
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

		if(!config.isSet("bosses")) {
			try {
				config.set("bosses.testBoss.Spawning.MobType", "Zombie");
				config.set("bosses.testBoss.Spawning.DMGMultiplicator", 3);
				config.set("bosses.testBoss.Spawning.MaxHealth", 200);
				config.set("bosses.testBoss.Spawning.Size", 0);
				config.set("bosses.testBoss.Spawning.Count", 1);
				config.set("bosses.testBoss.Spawning.SpawnRange", 5);
				config.set("bosses.testBoss.Spawning.Abilities", "");
				config.save(configFile);
			} 
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return config.getConfigurationSection("bosses").getKeys(false);
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
		//List<String> abilities_random_string			  = new ArrayList<String>();
		//List<String> abilities_rotation_string			  = new ArrayList<String>();
		Map<String, Object> abilities_random_set			  = new HashMap<String, Object>();
		Map<String, Object> abilities_rotation_set			  = new HashMap<String, Object>();
		for (String s : custommobnames) {
			CustomMobType cmt = new CustomMobType();
			try {
				cmt.setName(s);
				cmt.setMobtype(EntityType.fromName(c.getString(path + s + ".Spawning.MobType", null)));
				cmt.setDmgmulti(c.getInt(path + s + ".Spawning.DMGMultiplicator", 0));
				cmt.setMaxhealth(c.getInt(path + s + ".Spawning.MaxHealth", 0));
				cmt.setSize(c.getInt(path + s + ".Spawning.Size", 0));
				cmt.setCount(c.getInt(path + s + ".Spawning.Count", 1));
				cmt.setRange(c.getInt(path + s + ".Spawning.SpawnRange", 1));
				cmt.setOldabilitys(c.getString(path + s + ".Abilities", null));
				if(c.isSet(path + s + ".Rotation")) {
					abilities_rotation_set = c.getConfigurationSection(path + s + ".Rotation").getValues(false);
					for(String ability : abilities_rotation_set.keySet()) {
						if(CustomMobAbility.fromString(ability) != null) {
							cmt.addAbilities_rotation(CustomMobAbility.fromString(ability), Integer.parseInt(abilities_rotation_set.get(ability).toString()));
						}
					}
				}
				if(c.isSet(path + s + ".Random")) {
					abilities_random_set = c.getConfigurationSection(path + s +".Random").getValues(false);
					for(String ability : abilities_random_set.keySet()) {
						if(CustomMobAbility.fromString(ability) != null) {
							cmt.addAbilities_random(CustomMobAbility.fromString(ability), Integer.parseInt(abilities_random_set.get(ability).toString()));
						}
					}
				}
				result.add(cmt);
			} catch (Exception e) {
				e.printStackTrace();
			}
			//result.put(s, c.getString(path + s + "." + type, null));
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
