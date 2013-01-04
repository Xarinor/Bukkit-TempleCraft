package com.bukkit.xarinor.templecraft;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.jar.JarFile;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilderFactory;

import org.bukkit.ChatColor;
//import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Bat;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.bukkit.xarinor.templecraft.games.Adventure;
import com.bukkit.xarinor.templecraft.games.Arena;
import com.bukkit.xarinor.templecraft.games.Game;
import com.bukkit.xarinor.templecraft.games.PVP;
import com.bukkit.xarinor.templecraft.games.Race;
import com.bukkit.xarinor.templecraft.games.Spleef;
import com.bukkit.xarinor.templecraft.util.InventoryStash;
import com.bukkit.xarinor.templecraft.util.Translation;
import com.herocraftonline.heroes.characters.Hero;

/**
* TCUtils.java
* This work is dedicated to the public domain.
*
* @author Xarinor
* @author bootscreen
* @author msingleton
*/
public class TCUtils {				   
	private static HashMap<String, InventoryStash> inventories = new HashMap<String, InventoryStash>();
	public static final List<Integer>  SWORDS_ID   = new LinkedList<Integer>();
	public static final List<Material> SWORDS_TYPE = new LinkedList<Material>();
	public static final int MAX_HEALTH = 20;
	public static final int MAX_FOOD = 20;
	static {
		SWORDS_TYPE.add(Material.WOOD_SWORD);
		SWORDS_TYPE.add(Material.STONE_SWORD);
		SWORDS_TYPE.add(Material.GOLD_SWORD);
		SWORDS_TYPE.add(Material.IRON_SWORD);
		SWORDS_TYPE.add(Material.DIAMOND_SWORD);
	}

	/*INVENTORY AND REWARD METHODS*/

	/**
	 * Clears the players inventory and armor slots.
	 * 
	 * @param p -Player
	 * @return
	 */
	public static PlayerInventory clearInventory(Player p) {
		PlayerInventory inv = p.getInventory();
		inv.clear();
		inv.setHelmet(null);
		inv.setChestplate(null);
		inv.setLeggings(null);
		inv.setBoots(null);
		return inv;
	}

	/**
	 * Checks if the players inventory is preserved
	 * 
	 * @param playerName
	 * @return
	 */
	public static boolean hasPlayerInventory(String playerName) {
		return inventories.containsKey(playerName);
	}

	/**
	 * Preserving a players inventory...
	 * Heroes support..
	 * 
	 * @param player - Player
	 */
	public static void keepPlayerInventory(Player player) {
		PlayerInventory inventory = player.getInventory();
		ItemStack[] contents = inventory.getContents();
		double playerHealth;
		if(TempleCraft.heroManager != null) {
			playerHealth = TempleCraft.heroManager.getHero(player).getHealth();
		} else {
			playerHealth = player.getHealth();
		}
		inventories.put(player.getName(), new InventoryStash(contents, inventory.getHelmet(), inventory.getChestplate(),inventory.getLeggings(),
				inventory.getBoots(), playerHealth, player.getFoodLevel(), player.getTotalExperience(), player.getGameMode()));	
	}

	/**
	 * gives back current items
	 * Heroes support
	 * 
	 * @param player -The happy player
	 */
	public static void restorePlayerInventory(Player player) {
		InventoryStash originalContents = inventories.remove(player.getName());
		PlayerInventory playerInv = player.getInventory();
		if(originalContents != null) {
			playerInvFromInventoryStash(playerInv, originalContents);
		}
		if(TempleCraft.heroManager != null) {
			Hero hero = TempleCraft.heroManager.getHero(player);
			hero.setHealth((int) originalContents.getHealth());
			player.setHealth((int)(originalContents.getHealth()/hero.getMaxHealth()));
		} else {
			player.setHealth((int)originalContents.getHealth());
		}
		player.setFoodLevel(originalContents.getFoodLevel());
		player.setTotalExperience(originalContents.getExperience());
		player.setGameMode(originalContents.getGameMode());
	}

	/**
	 * Add new items to a players original inventory!
	 * Lucky ones
	 * 
	 * @param player -Lucky player
	 * @param item	 -Added item
	 */
	public static void addtoPlayerInventory(Player player, ItemStack item) {
		InventoryStash Contents = inventories.get(player.getName());
		Contents.addContent(item);
		inventories.put(player.getName(), Contents);
	}

	/**
	 * /**
	 * Gives the player back his beloved diamonds
	 * 
	 * @param playerInv
	 * @param originalContents
	 */
	private static void playerInvFromInventoryStash(PlayerInventory playerInv, InventoryStash originalContents) {
		playerInv.clear();
		playerInv.clear(playerInv.getSize() + 0);
		playerInv.clear(playerInv.getSize() + 1);
		playerInv.clear(playerInv.getSize() + 2);
		playerInv.clear(playerInv.getSize() + 3);	// helmet/blockHead
		for(ItemStack item : originalContents.getContents()) {
			if(item != null && item.getTypeId() != 0) {
				playerInv.addItem(item);
			}
		}
		if(originalContents.getHelmet() != null && originalContents.getHelmet().getType() != Material.AIR) {
			playerInv.setHelmet(originalContents.getHelmet());
		}
		if(originalContents.getChest() != null && originalContents.getChest().getType() != Material.AIR) {
			playerInv.setChestplate(originalContents.getChest());
		}
		if(originalContents.getLegs() != null && originalContents.getLegs().getType() != Material.AIR) {
			playerInv.setLeggings(originalContents.getLegs());
		}
		if(originalContents.getFeet() != null && originalContents.getFeet().getType() != Material.AIR) {
			playerInv.setBoots(originalContents.getFeet());
		}
	}

	/*INITIALIZATION METHODS (CONFIGURATION)*/

	/**
	 * Creates a Configuration object from the config.yml file.
	 * 
	 * @param name -Config name
	 * @return
	 */
	public static File getConfig(String name) {
		new File("plugins/TempleCraft").mkdir();
		File configFile = new File("plugins/TempleCraft/"+name+".yml");

		try {
			if(!configFile.exists())
				configFile.createNewFile();
		} catch(Exception e) {
			System.out.println("[TempleCraft] ERROR: Config file could not be created.");
			return null;
		}


		return configFile;
	}

	/**
	 * Gets you all the enabled commands
	 * 
	 * @return Commands
	 */
	public static List<String> getEnabledCommands() {
		YamlConfiguration c = YamlConfiguration.loadConfiguration(TempleManager.config);

		String commands = c.getString("settings.enabledcommands", "/tct");
		c.set("settings.enabledcommands", commands);
		try {
			c.save(TempleManager.config);
		} catch (IOException e) {
			e.printStackTrace();
		}

		List<String> result = new LinkedList<String>();
		for (String s : commands.split(",")) {
			result.add(s.trim());
		}
		return result;
	}

	/**
	 * Returns the name of the next TempleCraft world available for usage
	 * 
	 * @param type	-Edit or Play-world
	 * @return Name	-Availeable name / next number
	 */
	public static String getNextAvailableTempWorldName(String type) {
		String name;

		if(type.equals("Edit")) {
			int size = TempleManager.templeEditMap.size();
			if(size >= TempleManager.maxEditWorlds) {
				return null;
			} else {
				name = "TCEditWorld_"+size;
			}
		} else {
			Set<String> worldNames = new HashSet<String>();
			for(World w : TempleManager.server.getWorlds()) {
				worldNames.add(w.getName());
			}
			int i = 0;
			do {
				name = "TC"+type+"World_"+i;
				i++;
			}
			while(worldNames.contains(name));
			File worldfolder =  new File(name);
			while(worldfolder.exists()) {
				if(!deleteFolder(worldfolder)) {
					name = "TC"+type+"World_"+i;
					worldfolder =  new File(name);
					i++;
				}
			}
		}
		return name;
	}

	/**
	 * Grabs an double from the config-file.
	 * 
	 * @param configFile -The file
	 * @param path		 -File path
	 * @return
	 */
	public static double getDouble(File configFile, String path) {
		YamlConfiguration c = YamlConfiguration.loadConfiguration(configFile);

		return c.getDouble(path);
	}
	
	/**
	 * sets a double for a config file
	 * 
	 * @param configFile -The file
	 * @param path		 -file path
	 * @param dbl		 -Input
	 * @return
	 */
	public static boolean setDouble(File configFile, String path, double dbl) {
		YamlConfiguration c = YamlConfiguration.loadConfiguration(configFile);
		c.set(path, dbl);
		try {
			c.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Grabs an int from the config-file.
	 * 
	 * @param configFile -The file
	 * @param path		 -File path
	 * @param def
	 * @return
	 */
	public static int getInt(File configFile, String path, int def) {
		YamlConfiguration c = YamlConfiguration.loadConfiguration(configFile);
		int result = c.getInt(path, def);
		c.set(path, result);
		
		try {
			c.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * sets a double for a config file
	 * 
	 * @param configFile -The file
	 * @param path		 -file path
	 * @param value		 -Input
	 * @return
	 */
	public static void setInt(File configFile, String path, int value) {
		YamlConfiguration c = YamlConfiguration.loadConfiguration(configFile);
		c.set(path, value);
		try {
			c.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Grabs an string from the config-file.
	 * 
	 * @param configFile -The file
	 * @param path		 -File path
	 * @param def
	 * @return
	 */
	public static String getString(File configFile, String path, String def) {
		YamlConfiguration c = YamlConfiguration.loadConfiguration(configFile);

		String result = c.getString(path, def);
		c.set(path, result);

		try {
			c.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Sets a string for a config file
	 * 
	 * @param configFile -The file
	 * @param path		 -file path
	 * @param def
	 * @return
	 */
	public static void setString(File configFile, String path, String def) {
		YamlConfiguration c = YamlConfiguration.loadConfiguration(configFile);
		c.set(path, def);

		try {
			c.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Grabs a boolean from the config-file.
	 * 
	 * @param configFile -The file
	 * @param path		 -File path
	 * @param def
	 * @return
	 */
	public static boolean getBoolean(File configFile, String path, boolean def) {
		YamlConfiguration c = YamlConfiguration.loadConfiguration(configFile);

		boolean result = c.getBoolean(path, def);
		c.set(path, result);

		try {
			c.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Sets a boolean for a config-file.
	 * 
	 * @param configFile -The file
	 * @param path		 -File path
	 * @param key		 -Input
	 */
	public static void setBoolean(File configFile, String path, boolean key)
	{
		YamlConfiguration c = YamlConfiguration.loadConfiguration(configFile);
		c.set(path, key);

		try {
			c.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Removes an entry from the config file
	 * 
	 * @param configFile -The file
	 * @param path		 -File path
	 */
	public static void removeValue(File configFile, String path) {
		YamlConfiguration c = YamlConfiguration.loadConfiguration(configFile);
		c.set(path, null);
		try {
			c.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a Configuration object from the config.yml file.
	 */
	public static void cleanConfigFiles() {
		cleanTempleConfig();
	}

	/**
	 * Creates a clean temple configuration
	 */
	private static void cleanTempleConfig() {
		YamlConfiguration c = YamlConfiguration.loadConfiguration(getConfig("temples"));

		if(!c.getKeys(false).contains("Temples")) {
			return;
		}

		Set<String> list = c.getConfigurationSection("Temples").getKeys(false);
		for(String s : list) {
			Temple temple = getTempleByName(s);
			if(temple == null) {
				c.getConfigurationSection("Temples").set(s, null);
			}
		}
		try {
			c.save(getConfig("temples"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* PLAYER METHODS */

	/**
	 * Returns a searched player
	 * 
	 * @param playerName
	 * @return
	 */
	public static Player getPlayerByName(String playerName) {
		for(Player p : TempleManager.server.getOnlinePlayers()) {
			if(p.getName().equals(playerName)) {
				return p;
			}
		}
		return null;
	}

	/**
	 * Turns the current set of players into an array, and grabs a random
	 * element out of it.
	 */
	public static Player getRandomPlayer() {
		Random random = new Random();
		Object[] array = TempleManager.server.getOnlinePlayers();
		return (Player) array[random.nextInt(array.length)];
	}

	/**
	 * Turns the current set of players in a selected game into an array, and grabs a random
	 * element out of it.
	 * 
	 * @param game -Game the player should be picked from
	 */
	public static Player getRandomPlayer(Game game) {
		Random random = new Random();
		Object[] array = game.playerSet.toArray();
		return (Player) array[random.nextInt(array.length)];
	}
	
	/**
	 * Turns the current set of players near an entity into an array
	 * 
	 * @param entity - The entity where nearby players get picked
	 * @return -Players
	 */
	public static List<Player> getNearbyPlayers(Entity entity) {
		
		List<Player> result = new ArrayList<Player>();
		List<Entity> entity_list = entity.getNearbyEntities(20, 20, 20);
		for(Entity e : entity_list) {
			if(e instanceof Player) {
				result.add((Player) e);
			}
		}
		return result;
	}
	/**
	 * Turns the current set of players near an entity into an array
	 * This is with added range to the process
	 * 
	 * @param entity - The entity where nearby players get picked
	 * @param radius - The radius to get players in
	 * @return -Players
	 */
	public static List<Player> getNearbyPlayers(Entity entity, int range) {
		
		List<Player> result = new ArrayList<Player>();
		List<Entity> entity_list = entity.getNearbyEntities(range, range, range);
		for(Entity e : entity_list) {
			if(e instanceof Player) {
				result.add((Player) e);
			}
		}
		return result;
	}
	
	/**
	 * Turns the current set of players near an entity into an array, and grabs a random
	 * element out of it.
	 * 
	 * @param entity -The entity a near player should be picked from
	 * @return -Player
	 */
	public static Player getNearbyRandomPlayer(Entity entity) {
		Random random = new Random();
		List<Entity> entity_list = entity.getNearbyEntities(20, 20, 20);
		List<Entity> nearby_players = new ArrayList<Entity>();
		for(Entity e : entity_list) {
			if(e instanceof Player) {
				nearby_players.add(e);
			}
		}
		Object[] array = nearby_players.toArray();

		Player p = null;
		if(array.length > 0) {
			p = (Player) array[random.nextInt(array.length)];
		}
		return p;
	}
	
	/* TEMPLE EDIT METHODS */

	/**
	 * Gets you a temple by name
	 * 
	 * @param templeName
	 * @return
	 */
	public static Temple getTempleByName(String templeName) {
		templeName = templeName.toLowerCase();
		for(Temple temple : TempleManager.templeSet) {
			if(temple.templeName.equals(templeName)) {
				return temple;
			}
		}
		return null;
	} 

	/**
	 * Gets a temple by worldname
	 * 
	 * @param w
	 * @return
	 */
	public static Temple getTempleByWorld(World w) {
		for(Temple temple : TempleManager.templeSet) {
			World world = TempleManager.templeEditMap.get(temple.templeName);
			if(world != null && world.equals(w)) {
				return temple;
			}
		}
		return null;
	}

	/**
	 * Creates a new temple file
	 * 
	 * @param player	 -User
	 * @param templeName -name of the new temple
	 * @param ChunkGen	 -chunk generator
	 * @param edit		 -Editing?
	 * @return
	 */
	public static boolean newTemple(Player player, String templeName, String ChunkGen, boolean edit) {		
		Temple temple = TCUtils.getTempleByName(templeName);
		TemplePlayer tp = TempleManager.templePlayerMap.get(player);

		if(tp.ownedTemples >= TempleManager.maxTemplesPerPerson && !TCPermissionHandler.hasPermission(player,"templecraft.editall")) {
			TempleManager.tellPlayer(player, Translation.tr("maxTemplesOwned"));
			return false;
		}

		if(temple != null) {
			TempleManager.tellPlayer(player, Translation.tr("templeAE",temple.templeName));
			return false;
		}

		if(tp.currentTemple != null) {
			TempleManager.tellPlayer(player, Translation.tr("mustLeaveTemple"));
			return false;
		}

		for(char c : templeName.toCharArray()) {
			if(!Character.isLetterOrDigit(c)) {
				TempleManager.tellPlayer(player, Translation.tr("nameFail"));
				return false;
			}
		}
		temple = new Temple(templeName);
		tp.ownedTemples++;
		temple.addOwner(player.getName());

		if(ChunkGen != null) {
			if(ChunkGen.equalsIgnoreCase("nether")) {
				temple.env = Environment.NETHER;
				temple.wt = null;
				temple.ChunkGeneratorFile = null;
			} else if(ChunkGen.equalsIgnoreCase("the_end")) {
				temple.env = Environment.THE_END;
				temple.wt = null;
				temple.ChunkGeneratorFile = null;
			} else if(ChunkGen.equalsIgnoreCase("flat")) {
				temple.wt = WorldType.FLAT;
				temple.env = null;
				temple.ChunkGeneratorFile = null;
			} else {
				temple.ChunkGeneratorFile = getChunkGeneratorByName(ChunkGen);
				temple.wt = null;
				temple.env = null;
			}
		}

		if(!edit) {
			return true;
		}
		editTemple(player, temple);

		return true;
	}

	/**
	 * Gets a custom chunk generator
	 * 
	 * @param ChunkGen - Name of the generator
	 * @return
	 */
	private static File getChunkGeneratorByName(String ChunkGen) {
		File cgFolder = new File("plugins/TempleCraft/ChunkGenerators");
		File temp = null;
		ChunkGen = ChunkGen.toLowerCase();
		if(cgFolder.isDirectory()) {
			for(File f : cgFolder.listFiles()) {
				String name = f.getName().toLowerCase();
				if(name.replace(".jar", "").equals(ChunkGen.replace(".jar", ""))) {
					return f;
				} else if(name.startsWith(ChunkGen)) {
					temp = f;
				}
			}
		}
		return temp;
	}

	/**
	 * Editing a temple by name
	 * 
	 * @param p		 -User
	 * @param temple -Temple name
	 */
	public static void editTemple(Player p, Temple temple) {	
		TemplePlayer tp = TempleManager.templePlayerMap.get(p);

		if(!temple.accessorSet.contains(p.getName()) && !temple.ownerSet.contains(p.getName()) && !TCPermissionHandler.hasPermission(p,"templecraft.editall")){
			TempleManager.tellPlayer(p, Translation.tr("cantEdit"));
			return;
		}

		if(tp.currentTemple != null && tp.currentTemple != temple) {
			TempleManager.tellPlayer(p, Translation.tr("mustLeaveTemple"));
			return;
		}

		World EditWorld;
		if(TempleManager.templeEditMap.containsKey(temple.templeName)) {
			EditWorld = TempleManager.templeEditMap.get(temple.templeName);
		} else {
			EditWorld = temple.loadTemple("Edit");
		}

		if(EditWorld == null) {
			if(TempleManager.constantWorldNames) {
				TempleManager.tellPlayer(p, Translation.tr("templeInUse", temple.templeName));
			} else {
				TempleManager.tellPlayer(p, Translation.tr("editTempleFail"));
			}
			return;
		}

		// Only clears and loads Temple if no one is already editing
		if(temple.editorSet.isEmpty()) {
			TempleManager.templeEditMap.put(temple.templeName,EditWorld);
			EditWorld.setTime(8000);
			EditWorld.setStorm(false);
		}
		temple.editorSet.add(p);
		tp.currentTemple = temple;
		if(!TCUtils.hasPlayerInventory(p.getName())) {
			TCUtils.keepPlayerInventory(p);
			TCUtils.clearInventory(p);
		}
		if(!TempleManager.locationMap.containsKey(p)) {
			TempleManager.locationMap.put(p, p.getLocation());
		}
		Location lobbyLoc = temple.getLobbyLoc(EditWorld);
		if(lobbyLoc == null) {
			lobbyLoc = new Location(EditWorld,-1, EditWorld.getHighestBlockYAt(-1, -1)+2, -1);
		}
		lobbyLoc.getChunk().load(true);
		p.teleport(lobbyLoc);
		p.setGameMode(GameMode.CREATIVE);
		if(TempleCraft.MVWM != null) {
			TempleCraft.MVWM.getMVWorld(EditWorld).setGameMode("creative");
		}
	}

	/**
	 * Converts a temple
	 * 
	 * @param player -User
	 * @param temple -Temple
	 */
	public static void convertTemple(Player player, Temple temple) {
		File tcffile = new File("plugins/TempleCraft/SavedTemples/"+temple.templeName+TempleCraft.fileExtention);
		File file = new File("plugins/TempleCraft/SavedTemples/"+temple.templeName);

		if(!tcffile.exists() && !file.exists()) {
			return;
		}

		World ConvertWorld = temple.loadTemple("Convert");

		if(ConvertWorld == null) {
			return;
		}

		ConvertWorld.setAutoSave(false);
		ConvertWorld.setKeepSpawnInMemory(false);
		ConvertWorld.setTime(8000);
		ConvertWorld.setStorm(false);
		ConvertWorld.setSpawnLocation(-1, ConvertWorld.getHighestBlockYAt(-1, -1)+2, -1);

		temple.saveTemple(ConvertWorld, player);
		deleteTempWorld(ConvertWorld);
	}

	/**
	 * Removes a temple from the plugin
	 * 
	 * @param temple
	 */
	public static void removeTemple(Temple temple) {		
		String fileName = "plugins/TempleCraft/SavedTemples/"+temple.templeName;
		//Remove all files associated with the temple
		try {
			File folder = new File(fileName);
			deleteFolder(folder);
		} catch (SecurityException e) {
			System.err.println("Unable to delete " + fileName + "("+ e.getMessage() + ")");
		}

		TempleManager.templeSet.remove(temple);
	}

	/**
	 * Rename a temple 
	 * 
	 * @param temple-Temple name
	 * @param arg	-New name
	 */
	public static void renameTemple(Temple temple, String arg) {
		String folder = "plugins/TempleCraft/SavedTemples/";
		File current = new File(folder+temple.templeName);
		File result  = new File(folder+arg);
		if(result.exists()) {
			return;
		}
		current.renameTo(result);
		File configFile = TCUtils.getConfig("temples");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		if(config.isConfigurationSection("Temples."+temple.templeName)) {
			ConfigurationSection src = config.getConfigurationSection("Temples."+temple.templeName);
			ConfigurationSection dst = config.createSection("Temples."+arg);
			copyConfigurationSection(src,dst);
			config.getConfigurationSection("Temples").set(temple.templeName, null);
		}
		temple.templeName = arg;
		try {
			config.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Copys selected config
	 * 
	 * @param src -Config section
	 * @param dst -Target for the copy
	 */
	public static void copyConfigurationSection(ConfigurationSection src, ConfigurationSection dst) {
		for(String s : src.getKeys(false)) {
			if(src.isConfigurationSection(src.getCurrentPath()+s)) {
				dst.createSection(s);
				copyConfigurationSection(src.getConfigurationSection(s),dst.getConfigurationSection(s));
			} else {
				dst.set(s, src.get(s));
			}
		}
	}

	/**
	 * Sets the maximum amount of players able to join a certain temple
	 * 
	 * @param temple -Temple
	 * @param value	 -Player count
	 */
	public static void setTempleMaxPlayers(Temple temple, int value) {
		TCUtils.setInt(getConfig("temples"),"Temples."+temple.templeName+".maxPlayersPerGame", value);
		temple.maxPlayersPerGame = value;
	}

	/**
	 * Sets the maximum amount of deaths until the temple gets closed / is lost
	 * 
	 * @param temple -Temple
	 * @param value  -Death count
	 */
	public static void setTempleMaxDeaths(Temple temple, int value) {
		TCUtils.setInt(getConfig("temples"),"Temples."+temple.templeName+".maxDeaths", value);
		temple.maxDeaths = value;
	}

	/**
	 * Set's the location players should get ported to after temple completion
	 *
	 * @param temple
	 * @param loc
	 */
	public static void setFinishLocation(Temple temple, Location loc) {
		if(loc != null) {
			TCUtils.setString(getConfig("temples"),"Temples."+temple.templeName+".finishLocation.world", loc.getWorld().getName());
			TCUtils.setDouble(getConfig("temples"),"Temples."+temple.templeName+".finishLocation.x", loc.getX());
			TCUtils.setDouble(getConfig("temples"),"Temples."+temple.templeName+".finishLocation.y", loc.getY());
			TCUtils.setDouble(getConfig("temples"),"Temples."+temple.templeName+".finishLocation.z", loc.getZ());
			TCUtils.setDouble(getConfig("temples"),"Temples."+temple.templeName+".finishLocation.pitch", loc.getPitch());
			TCUtils.setDouble(getConfig("temples"),"Temples."+temple.templeName+".finishLocation.yaw", loc.getYaw());
		} else {
			TCUtils.removeValue(getConfig("temples"),"Temples."+temple.templeName+".finishLocation.world");
			TCUtils.removeValue(getConfig("temples"),"Temples."+temple.templeName+".finishLocation.x");
			TCUtils.removeValue(getConfig("temples"),"Temples."+temple.templeName+".finishLocation.y");
			TCUtils.removeValue(getConfig("temples"),"Temples."+temple.templeName+".finishLocation.z");
			TCUtils.removeValue(getConfig("temples"),"Temples."+temple.templeName+".finishLocation.pitch");
			TCUtils.removeValue(getConfig("temples"),"Temples."+temple.templeName+".finishLocation.yaw");			
		}
		temple.finishLocation = loc;
	}

	/**
	 * Removes players from an active temple world
	 * 
	 * @param world -Temple world
	 */
	public static void removePlayers(World world) {
		Set<Player> tempSet = new HashSet<Player>();
		for(Player p: world.getPlayers()) {
			tempSet.add(p);
		}
		World w;
		for(Player p : tempSet) {
			TemplePlayer tp = TempleManager.templePlayerMap.get(p);
			if(tp == null) {
				w = getNonTempWorld();
				if(w == null) {
					p.kickPlayer("Could not find a non temporary world to teleport you to.");
				}
				(new Location(w,0,0,0)).getChunk().load(true);
				p.teleport(new Location(w,0,0,0));
			} else {
				TempleManager.playerLeave(p);
			}
		}
	}

	/**
	 * Gets a non-Temple-world
	 * @return
	 */
	private static World getNonTempWorld() {
		World ntw = TempleManager.server.getWorld("world");
		if(ntw == null) {
			for(World w : TempleManager.server.getWorlds()) {
				if(!TCUtils.isTCWorld(w)) {
					ntw = w;
				}
			}
		}
		return ntw;
	}

	/**
	 * Delete a temple world
	 * 
	 * @param w -Temple world
	 * @return
	 */
	public static boolean deleteTempWorld(World world) {
		String worldname = world.getName();
		//TODO: optimze -.-
		System.out.println("[TempleCraft] Attempting to delete World " + world.getName());
		world.save();	
		removePlayers(world);

		/* Multiverse */
		if(TempleCraft.MVWM != null) {
			if(TempleCraft.MVWM.isMVWorld(worldname)) {
				TempleCraft.MVWM.removeWorldFromConfig(world.getName());		
				try {
					if (TempleCraft.MVWM.deleteWorld(worldname, true) == true) {
						System.out.println("[TempleCraft] " + worldname + " was deleted via MVWM");
					} else {
						System.out.println("[TempleCraft] " + worldname + " was NOT deleted via MVWM");
					}
				} catch (Exception ex) {
					System.out.println("[TempleCraft] MVWM delete Execption: " + ex.getMessage());
				}
				
			} else {
				try {
					if(TempleManager.server.unloadWorld(world, true) == true) {
						System.out.println("[TempleCraft] World \""+worldname+"\" unloaded!");
					} else {
						System.out.println("[TempleCraft] World \""+worldname+"\" NOT unloaded!");
					}
				} catch (Exception ex) {
					System.out.println("[TempleCraft] MVWM delete Execption: " + ex.getMessage());
				}
			}			
		} else {
			try {
				if(TempleManager.server.unloadWorld(world, true) == true) {
					System.out.println("[TempleCraft] World \""+worldname+"\" unloaded!");
				} else {
					System.out.println("[TempleCraft] World \""+worldname+"\" NOT unloaded!");
				}
			} catch (Exception ex) {
				System.out.println("[TempleCraft] MVWM delete Execption: " + ex.getMessage());
			}
		}			
		
		File folder = new File(worldname);
		if(folder.exists()) {
			if(!deleteFolder(folder)) {
				if(!deleteFolder(folder)) {
					System.out.print("[TempleCraft] Error while deleting " + folder.getAbsolutePath());
				} else {
					debugMessage(folder.getAbsolutePath() + " deleted.");
					System.out.println("[TempleCraft] World \""+worldname+"\" deleted!");
				}
			} else {
				debugMessage(folder.getAbsolutePath() + " deleted.");
				System.out.println("[TempleCraft] World \""+worldname+"\" deleted!");
			}
		}
		if(folder.exists()) {
			return false;
		}
		return true;
	}

	/**
	 * Delete all loaded temporary worlds
	 */
	public static void deleteTempWorlds() {
		for(World w : TempleManager.server.getWorlds()) {
			if(TCUtils.isTCWorld(w)) {
				System.out.print(w.getName() + " is a TempleCraft World. Trying to delete it!");
				deleteTempWorld(w);
			}
		}
	}
	
	/**
	 * Delete all loaded temporary worlds with no players
	 * (Unused temples)
	 */
	public static void deleteUnusedTempWorlds() {
		for(World w : TempleManager.server.getWorlds()) {
			if(TCUtils.isTCWorld(w)) {
				if (w.getPlayers().size() < 1) {
					System.out.print(w.getName() + " is a a unused TempleCraft World. Trying to delete it!");
					deleteTempWorld(w);
				}
			}
		}
	}

	/**
	 * Delete the folders of unloaded temp worlds
	 */
	public static void deleteTempWorldFolders() {
		for(File f : TempleManager.server.getWorldContainer().listFiles()) {
			if(TCUtils.isTCWorldFolder(f)) {
				System.out.print(f.getAbsolutePath() + " is TC World Folder, try to delete it.");
				deleteFolder(f);
			}
		}
	}

	/**
	 * Deletes the folder
	 * 
	 * @param folder
	 * @return
	 */
	public static boolean deleteFolder(File folder) {
		boolean result = true;
		try {
			if (folder.exists()) {
				if(folder.isDirectory()) {
					for(File f : folder.listFiles()) {
						result = result && deleteFolder(f);
					}
				}
				result = result && folder.delete();
				return result;
			} else {
				return false;
			}
		} catch(Exception e) {
			System.out.print("Error while deleting " + folder.getAbsolutePath() + " - " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	/* GAME METHODS */

	/**
	 * Check sent command for new game
	 * 
	 * @param p		-User
	 * @param args	-aditional param
	 */
	public static void newGameCommand(Player p, String[] args) {
		Temple temple;
		String gameName;
		String mode;

		TempleManager.tellPlayer(p, Translation.tr("newGame"));
		if(args.length < 2) {
			TempleManager.tellPlayer(p, Translation.tr("incorrectArguments"));
			return;
		} else if(args.length < 3) {
			temple = getTempleByName(args[1]);
			mode = "adventure";
			gameName = getUniqueGameName(args[1], mode);
		} else {
			temple = getTempleByName(args[1]);
			mode = args[2].toLowerCase();
			gameName = getUniqueGameName(args[1], mode);
		}

		// Check the validity of the arguements
		if(temple == null) {
			TempleManager.tellPlayer(p, Translation.tr("templeDNE", args[1]));
			return;
		}

		if(!temple.isSetup) {
			TempleManager.tellPlayer(p, Translation.tr("templeNotSetup", temple.templeName));
			return;
		}

		if(!TempleManager.modes.contains(mode)) {
			TempleManager.tellPlayer(p, Translation.tr("modeDNE", mode));
			return;
		}

		if(temple.maxPlayersPerGame < 1 && temple.maxPlayersPerGame != -1) {
			TempleManager.tellPlayer(p, Translation.tr("templeFull", temple.templeName));
			return;
		}

		if(newGame(gameName,temple,mode) != null) {
			TempleManager.tellPlayer(p, Translation.tr("game.created",gameName));
		} else if(TempleManager.constantWorldNames) {
			TempleManager.tellPlayer(p, Translation.tr("templeInUse",temple.templeName));
		} else {
			TempleManager.tellPlayer(p, Translation.tr("newGameFail"));
		}
	}

	/**
	 * Open a new Templecraft game (world)
	 * 
	 * @param name	-Game name
	 * @param temple-Temple name
	 * @param mode	-Gamemode
	 * @return
	 */
	public static Game newGame(String name, Temple temple, String mode) {
		if(temple == null) {
			return null;
		}

		Game game;
		World world = temple.loadTemple("Temple");
		// Checks to make sure the world is not null
		if(world == null) {
			return null;
		}
		
		//Handle the gamemodes differently
		if(mode.equals("adventure")) {
			game = new Adventure(name, temple, world);
		} else if(mode.equals("race")) {
			game = new Race(name, temple, world);
		} else if(mode.equals("spleef")) {
			game = new Spleef(name, temple, world);
		} else if(mode.equals("pvp")) {
			game = new PVP(name, temple, world);
		} else if(mode.equals("arena")) {
			game = new Arena(name, temple, world);
		} else {
			game = new Adventure(name, temple, world);
		}
		return game;
	}

	/**
	 * Gets a game in progress with an searched entity
	 * 
	 * @param entity
	 * @return
	 */
	public static Game getGame(Entity entity) {
		for(Game game : TempleManager.gameSet) {	
			if(game.monsterSet.contains(entity)) {
				return game;
			}
		}
		return null;
	}

	/**
	 * Gets a game in progress by the game name
	 * 
	 * @param gameName
	 * @return
	 */
	public static Game getGameByName(String gameName) {
		for(Game game : TempleManager.gameSet) {
			if(game.gameName.startsWith(gameName)) {
				return game;
			}
		}
		return null;
	}

	/**
	 * Gets a game in progress by the temple world
	 * 
	 * @param world -Temple world
	 * @return
	 */
	public static Game getGameByWorld(World world) {
		for(Game game : TempleManager.gameSet) {
			if(game.world.equals(world)) {
				return game;
			}
		}
		return null;
	} 

	/**
	 * Gets a game in progress by name and mode
	 * 
	 * @param templeName-Temple Name
	 * @param mode		-Gamemode
	 * @return
	 */
	public static String getUniqueGameName(String templeName, String mode) {
		String gameName = "";
		int i = 1;
		do {
			gameName = templeName+mode.substring(0,3)+i;
			i++;
		}
		while(getGameByName(gameName) != null);
		return gameName;
	}

	/**
	 * Gets the player closest to the input entity. ArrayList implementation
	 * means a complexity of O(n).
	 * 
	 * @param game
	 * @param e
	 * @return
	 */
	public static Player getClosestPlayer(Game game, Entity e) {

		// Set up the comparison variable and the result.
		double current = Double.POSITIVE_INFINITY;
		Player result = null;

		/* Iterate through the ArrayList, and update current and result every
		 * time a squared distance smaller than current is found. */
		for (Player p : game.playerSet) {
			double dist = distance(p.getLocation(), e.getLocation());
			if (dist < current) {
				current = dist;
				result = p;
			}
		}
		return result;
	}


	/**
	 * Calculates the squared distance between locations.
	 * 
	 * @param loc	-Location 1
	 * @param loc2	-Location 2
	 * @return
	 */
	public static double distance(Location loc, Location loc2) {
		double d4 = loc.getX() - loc2.getX();
		double d5 = loc.getY() - loc2.getY();
		double d6 = loc.getZ() - loc2.getZ();

		return Math.sqrt(d4*d4 + d5*d5 + d6*d6);
	}

	/* MISC METHODS */

	/**
	 * Checks if there is a new update of TempleCraft and notifies the responsible person
	 * 
	 * Thanks Sleaker for the permission to use his updatecheck code from vault
	 * 
	 * @param currentVersion
	 * @return
	 * @throws Exception
	 */
	public static String updateCheck(String currentVersion) throws Exception {
		String pluginUrlString = "http://dev.bukkit.org/server-mods/templecraft-bootscreen/files.rss";
		try {
			URL url = new URL(pluginUrlString);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			if(con.getResponseCode() == 200) {
				Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url.openConnection().getInputStream());
				doc.getDocumentElement().normalize();
				NodeList nodes = doc.getElementsByTagName("item");
				Node firstNode = nodes.item(0);
				if (firstNode.getNodeType() == 1) {
					Element firstElement = (Element)firstNode;
					NodeList firstElementTagName = firstElement.getElementsByTagName("title");
					Element firstNameElement = (Element) firstElementTagName.item(0);
					NodeList firstNodes = firstNameElement.getChildNodes();
					return firstNodes.item(0).getNodeValue().replace("Templecraft v", "");
				}
			} else {
				System.err.println("[TempleCraft] Can't check http://dev.bukkit.org/server-mods/templecraft-bootscreen/files.rss for updates");
			}
		} catch (SocketException localException) {
			System.err.println("[TempleCraft] Can't check http://dev.bukkit.org/server-mods/templecraft-bootscreen/files.rss for updates");
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return currentVersion;
	}

	/**
	 * Converts the version of templecraft into whats needed
	 * 
	 * @param Version
	 * @return
	 */
	public static double convertVersion(String Version) {
		try {
			String[] version = Version.split("\\.", 2);
			return Double.valueOf(version[0] + "." + version[1].replace(".", ""));
		} catch(Exception e) {}
		return 0;
	}

	/**
	 * Sends a death message to players
	 * 
	 * @param game		-Active game
	 * @param entity	-Killed entity
	 * @param entity2	-Mean killer
	 */
	public static void sendDeathMessage(Game game, Entity entity, Entity entity2) {

		String msg = "";
		
		String killed = getDisplayName(entity);
		String killer = getDisplayName(entity2); 	

		if(killer.equals("")) {
			String s = entity.getLastDamageCause().getCause().name().toLowerCase();
			killer = s.substring(0,1).toUpperCase().concat(s.substring(1, s.length()));
		}
		for(Player p: game.playerSet) {
			msg = Translation.tr("killMessage", killer, killed);

			if(TempleCraft.economy != null) {
				String s = TempleCraft.economy.format(2.0);
				String currencyName = s.substring(s.indexOf(" ") + 1);

				if(game.mobGoldMap.containsKey(entity.getEntityId()) && entity2 instanceof Player) {
					msg += ChatColor.GOLD + " (+" + game.mobGoldMap.get(entity.getEntityId())/game.playerSet.size() + " "+currencyName+")";
				}
			}
			game.tellPlayer(p, msg);
		}
	}

	/**
	 * Get the display name of a entity
	 * For entities with strange, buggy or missing names there is a small name-fix
	 * 
	 * TODO Boss Names? !
	 * 
	 * @param entity
	 * @return
	 */
	private static String getDisplayName(Entity entity) {
		if(entity instanceof Player) {
			return ((Player)entity).getDisplayName();
		}
		if(entity instanceof Creature) {
			String name = ((Creature)entity).getClass().getSimpleName().replace("Craft", "");

			StringBuilder formatted = new StringBuilder();
			for(int i = 0; i < name.length(); i++) {
				if(i != 0 && Character.isUpperCase(name.charAt(i))) {
					formatted.append(" ");
				}
				formatted.append(name.charAt(i));
			}
			return formatted.toString();
		}
		if(entity instanceof EnderDragon) {
			return "Ender Dragon";
		}
		if(entity instanceof Ghast) {
			return "Ghast";
		}
		if(entity instanceof MagmaCube) {
			return "Magma Cube";
		}
		if(entity instanceof Slime) {
			return "Slime";
		}
		if(entity instanceof Bat) {
			return "Bat";
		}
		if(entity instanceof Skeleton) {
			Skeleton e = (Skeleton)entity;
			if (e.getSkeletonType() == SkeletonType.WITHER) {
				return "Wither Skeleton";
			}
		}
		return "";
	}

	/**
	 * Copy Entry from char to disk
	 * 
	 * @param entry
	 * @param folder
	 */
	public static void copyFromJarToDisk(String entry, File folder) {
		try {
			@SuppressWarnings("resource")
			JarFile jar = new JarFile(TempleManager.plugin.getPluginFile());
			InputStream is = jar.getInputStream(jar.getJarEntry(entry));
			OutputStream os = new FileOutputStream(new File(folder, entry));
			byte[] buffer = new byte[4096];
			int length;
			while (is!=null&&(length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
			os.close();
			is.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Check if a world is from Templecraft
	 * 
	 * @param world
	 * @return
	 */
	public static boolean isTCWorld(World world) {
		String name = world.getName();
		if(name.startsWith("TCTempleWorld_") || name.startsWith("TCEditWorld_") || name.startsWith("TCConvertWorld_")) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if a world folder is from Templecraft
	 * 
	 * @param worldFolder
	 * @return
	 */
	public static boolean isTCWorldFolder(File worldFolder) {
		String name = worldFolder.getName();
		if(name.startsWith("TCTempleWorld_") || name.startsWith("TCEditWorld_") || name.startsWith("TCConvertWorld_")) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if a world is in editmode from Templecraft
	 * @param world
	 * @return
	 */
	public static boolean isTCEditWorld(World world) {
		if(world.getName().startsWith("TCEditWorld_") || TempleManager.templeEditMap.containsValue(world)) {
			return true;
		}
		return false;
	}

	/**
	 * Gets all significant blocks near the player in a temple world
	 * 
	 * @param player
	 * @param radius
	 */
	public static void getSignificantBlocks(Player player, int radius) {
		TemplePlayer tp = TempleManager.templePlayerMap.get(player);
		Temple temple = tp.currentTemple;
		Location ploc = player.getLocation();
		int x1 = ploc.getBlockX()-radius;
		int x2 = ploc.getBlockX()+radius;
		int y1 = ploc.getBlockY()-radius;
		int y2 = ploc.getBlockY()+radius;
		int z1 = ploc.getBlockZ()-radius;
		int z2 = ploc.getBlockZ()+radius;
		if(y1<=0) {
			y1 = 1;
		}
		if(y2>player.getWorld().getMaxHeight()) {
			y2 = player.getWorld().getMaxHeight();
		}

		TempleManager.tellPlayer(player, Translation.tr("findingSigBlocks","("+x1+","+y1+","+z1+")","("+x2+","+y2+","+z2+")"));

		Map<Integer, Integer> foundBlocks = new HashMap<Integer, Integer>();

		for(int i = x1;i<=x2;i++) {
			for(int j = y1;j<=y2;j++) {
				for(int k = z1;k<=z2;k++) {
					int id = player.getWorld().getBlockTypeIdAt(i, j, k);
					//Significant blocks
					if(id == Temple.goldBlock || id == Temple.diamondBlock || id == Temple.ironBlock || id == 63 || id == 68 || (id == Temple.mobSpawner && j>5)) {
						Location loc = new Location(player.getWorld(),i,j,k);
						if(!temple.coordLocSet.contains(loc)) {
							temple.coordLocSet.add(loc);
							if(foundBlocks.containsKey(id)) {
								foundBlocks.put(id, foundBlocks.remove(id)+1);
							} else {
								foundBlocks.put(id, 1);
							}
						}
					}
				}
			}
		}

		// Print Results
		//for(Integer id : foundBlocks.keySet()) {
		//TODO Testing
		Iterator<Entry<Integer, Integer>> entries = foundBlocks.entrySet().iterator();
		while (entries.hasNext()) {
			Entry<Integer, Integer> entry = entries.next();
			Integer id= entry.getKey();
				
			TempleManager.tellPlayer(player, Translation.tr("foundSigBlocks",foundBlocks.get(id),getMaterialName(Material.getMaterial(id).name())));
		}
		TempleManager.tellPlayer(player, Translation.tr("done"));
	}

	/**
	 * Gets the name of a material
	 * 
	 * @param material
	 * @return
	 */
	public static String getMaterialName(String material) {
		StringBuilder sb = new StringBuilder();
		String[] words = material.split("_");
		for(int i = 0; i<words.length;i++) {
			sb.append(Character.toUpperCase(words[i].charAt(0))+words[i].toLowerCase().substring(1));
			if((i+1)<words.length) {
				sb.append(" ");
			}
		}
		return sb.toString();
	}

	/**
	 * Sort function by values
	 * 
	 * @param standings
	 * @return
	 */
	public static Map<String,Integer> sortMapByValues(Map<String, Integer> standings) {
		List<String> keyList = new ArrayList<String>();
		List<Integer> valueList = new ArrayList<Integer>();
		for(String s : standings.keySet()) {
			keyList.add(s);
			valueList.add(standings.get(s));
		}
		Set<Integer> sortedSet = new TreeSet<Integer>(valueList);

		Object[] sortedArray = sortedSet.toArray();
		int size = sortedArray.length;

		Map<String, Integer> result = new LinkedHashMap<String, Integer>();
		for (int i = 0; i < size; i++) {
			result.put(keyList.get(valueList.indexOf(sortedArray[i])), (Integer) sortedArray[i]);
		}

		return result;
	}

	/**
	 * Get the teamcolor based on whool blocks
	 * 
	 * @param team
	 * @return
	 */
	public static String getWoolColor(int team) {
		switch(team) {
		case 0:
			return ChatColor.WHITE+"White";
		case 1:
			return ChatColor.GOLD+"Orange";
		case 2:
			return ChatColor.LIGHT_PURPLE+"Magenta";
		case 3:
			return ChatColor.AQUA+"Light Blue";
		case 4:
			return ChatColor.YELLOW+"Yellow";
		case 5:
			return ChatColor.GREEN+"Lime";
		case 6:
			return ChatColor.LIGHT_PURPLE+"Pink";
		case 7:
			return ChatColor.DARK_GRAY+"Gray";
		case 8:
			return ChatColor.GRAY+"Light Gray";
		case 9:
			return ChatColor.DARK_AQUA+"Cyan";
		case 10:
			return ChatColor.DARK_PURPLE+"Purple";
		case 11:
			return ChatColor.BLUE+"Blue";
		case 12:
			return ChatColor.GOLD+"Brown";
		case 13:
			return ChatColor.DARK_GREEN+"Green";
		case 14:
			return ChatColor.RED+"Red";
		case 15:
			return ChatColor.BLACK+"Black";
		default:
			return "";
		}
	}

	/**
	 * Restore a players health pool
	 * Heroes support
	 * 
	 * @param player
	 */
	public static void restoreHealth(Player player)
	{
		if(TempleCraft.heroManager != null) {
			Hero hero = TempleCraft.heroManager.getHero(player);
			hero.setHealth(hero.getMaxHealth());
			hero.setMana(0);
		}
		player.setHealth(MAX_HEALTH);
	}

	/**
	 * Get the target player of the LivingEntity if possible.
	 * @param entity The entity whose target to get
	 * @return The target player, or null
	 * 
	 * 
	 */
	public static Entity getTarget(Entity entity) {
		if (entity instanceof Creature) {
			//LivingEntity target = null;
			Entity target = null;
			try
			{
				target = ((Creature) entity).getTarget();
			}
			catch (Exception e) {}

			if (target instanceof Player) {
				return target;
			}
		}
		return null;
	}

	/**
	 * Send a debug message
	 * 
	 * @param message -Message to be sent
	 */
	public static void debugMessage(String message) {
		if(Thread.currentThread().getStackTrace().length == 2) {
			debugMessage(Thread.currentThread().getStackTrace()[2].getClassName() + "########" +  Thread.currentThread().getStackTrace()[2].getMethodName() + "########" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "########" + message, Level.INFO);
		} else if(Thread.currentThread().getStackTrace().length == 1) {
			debugMessage(Thread.currentThread().getStackTrace()[1].getClassName() + "########" + Thread.currentThread().getStackTrace()[1].getMethodName() + "########" + Thread.currentThread().getStackTrace()[1].getLineNumber() + "########" + message, Level.INFO);
		} else if(Thread.currentThread().getStackTrace().length == 0) {
			debugMessage(Thread.currentThread().getStackTrace()[0].getClassName() + "########" + Thread.currentThread().getStackTrace()[0].getMethodName() + "########" + Thread.currentThread().getStackTrace()[0].getLineNumber() + "########" + message, Level.INFO);
		}
	}

	/**
	 * Send a debug message with loglevel
	 * 
	 * TODO Used anywhere?
	 * 
	 * @param message  -Message to be sent
	 * @param loglevel -The loglevel
	 */
	public static void debugMessage(String message, Level loglevel) {
		if (TempleCraft.debugMode) {
			if(loglevel.equals(Level.INFO)) {
				String split[] = message.split("########", 4);
				if(split.length == 4) {
					TempleCraft.debuglog.log(loglevel, message);
					return;
				}
			}
			if(Thread.currentThread().getStackTrace().length == 2) {
				TempleCraft.debuglog.log(loglevel, Thread.currentThread().getStackTrace()[2].getClassName() + "########" + Thread.currentThread().getStackTrace()[2].getMethodName() + "########" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "########" + message);
			} else if(Thread.currentThread().getStackTrace().length == 1) {
				TempleCraft.debuglog.log(loglevel, Thread.currentThread().getStackTrace()[1].getClassName() + "########" + Thread.currentThread().getStackTrace()[1].getMethodName() + "########" + Thread.currentThread().getStackTrace()[1].getLineNumber() + "########" + message);
			} else if(Thread.currentThread().getStackTrace().length == 0) {
				TempleCraft.debuglog.log(loglevel, Thread.currentThread().getStackTrace()[0].getClassName() + "########" + Thread.currentThread().getStackTrace()[0].getMethodName() + "########" + Thread.currentThread().getStackTrace()[0].getLineNumber() + "########" + message);
			}
		}
	}
}