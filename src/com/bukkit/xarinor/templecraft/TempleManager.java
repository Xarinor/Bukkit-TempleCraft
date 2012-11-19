package com.bukkit.xarinor.templecraft;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;


import com.bukkit.xarinor.templecraft.custommobs.CustomMobType;
import com.bukkit.xarinor.templecraft.custommobs.CustomMobUtils;
import com.bukkit.xarinor.templecraft.games.Game;
import com.bukkit.xarinor.templecraft.scoreboards.ScoreBoardManager;
import com.bukkit.xarinor.templecraft.util.MobArenaClasses;
import com.bukkit.xarinor.templecraft.util.Translation;

/**
* TempleManager.java
* This work is dedicated to the public domain.
* 
* @author Xarinor
* @author bootscreen
* @author msingleton
*/
public class TempleManager {
	public static Map<Player,Location> locationMap = new HashMap<Player,Location>();

	// Convenience variables.
	public static TempleCraft plugin = null;
	public static Server   server = null;
	public static ScoreBoardManager SBManager;
	public static boolean isEnabled = true;
	public static boolean checkUpdates;
	public static boolean dropBlocks;
	public static boolean constantWorldNames;
	public static boolean manageInventory;
	public static boolean allowunsafeEnchantments;

	// Colors
	public static ChatColor c1 = TempleCraft.c1;
	public static ChatColor c2 = TempleCraft.c2;
	public static ChatColor c3 = TempleCraft.c3;

	// Configuration
	public static File config = null;

	// A Map of which temples are being edited in which World
	public static Map<String,World> templeEditMap = new HashMap<String,World>();
	public static Map<Player,TemplePlayer> templePlayerMap = new HashMap<Player,TemplePlayer>();
	public static Set<Temple> templeSet = new HashSet<Temple>();
	public static Set<Game> gameSet = new HashSet<Game>();
	public static Set<Player> playerSet = new HashSet<Player>();
	public static Set<CustomMobType> BossTypeSet = new HashSet<CustomMobType>();
	public static Set<Integer> breakable = new HashSet<Integer>();	
	public static Set<Integer> placeable = new HashSet<Integer>();	
	public static String breakableMats;
	public static String placeableMats;
	public static String goldPerMob;

	// Adventure is the basic mode. race, spleef and pvp are not 100% supported.
	final public static Set<String> modes = new HashSet<String>(Arrays.asList("adventure","race","spleef","pvp"));

	// Flatland Configs
	public static int[] landLevels	= {0,60,64,65,128};
	public static byte[] landMats	= {7,1,3,2,0};

	public static int repairDelay;
	public static int maxEditWorlds;
	public static int maxTemplesPerPerson;
	public static int rejoinCost;
	public static int mobGoldMin = 0;
	public static int mobGoldRan = 0;
	// This is an attempt to prevent worlds not being unloaded properly on slower servers.
	public static int hitEndwaitingtime = 5;



	/** 
	* Initiates the temple
	* 
	* @param Templecraft -instance
    */
	public static void init(TempleCraft instance) {
		// If instance == null, simply update location variables.
		if (instance != null) {
			// General variables.
			plugin	= instance;
			config	= TCUtils.getConfig("config");
			server	= plugin.getServer();
			
			// Configuration
			hitEndwaitingtime		= TCUtils.getInt(config, "settings.hitendwaitingtime", 5);
			repairDelay				= TCUtils.getInt(config, "settings.repairdelay", 5);
			maxEditWorlds			= TCUtils.getInt(config, "settings.maxeditworlds", 2);
			maxTemplesPerPerson		= TCUtils.getInt(config, "settings.maxtemplesperperson", 1);
			rejoinCost				= TCUtils.getInt(config, "settings.rejoincost", 0);
			breakableMats			= TCUtils.getString(config, "settings.breakablemats", "31,37,38,39,40,46,82");
			placeableMats			= TCUtils.getString(config, "settings.placeablemats", "31,37,38,39,40,46,82");
			goldPerMob				= TCUtils.getString(config, "settings.goldpermob", "50-100");
			dropBlocks				= TCUtils.getBoolean(config, "settings.dropblocks", false);
			constantWorldNames		= TCUtils.getBoolean(config, "settings.constantworldnames", false);
			manageInventory			= TCUtils.getBoolean(config, "settings.manageinventory", true);
			allowunsafeEnchantments	= TCUtils.getBoolean(config, "settings.allowunsafeenchantments", false);
			checkUpdates			= TCUtils.getBoolean(config, "settings.updatenotification", true);
			
			loadMisc();
			loadTemplePlayers();
			loadCustomTemples();
			SBManager = new ScoreBoardManager();
			
			CustomMobUtils.loadBosses();
		}
	}

	/**
	 * loads temples for usage
	 */
	public static void loadCustomTemples() {
		File folder = new File("plugins/TempleCraft/SavedTemples/");
		if(!folder.exists()) {
			folder.mkdir();
		}
		// Get temples based on filenames
		for(File f : folder.listFiles()) {
			if(f.isDirectory()) {
				String templeName = f.getName();
				Temple temple;
				temple = TCUtils.getTempleByName(templeName);
				if(temple == null) {
					temple = new Temple(templeName);
				}
			}
			else if(f.getName().endsWith(".tcf")) {
				String templeName = f.getName().replace(".tcf", "");
				Temple temple;
				temple = TCUtils.getTempleByName(templeName);
				if(temple == null) {
					temple = new Temple(templeName);
				}
			}
		}	
	}

	/**
	 * loads all Players inside temples
	 */
	private static void loadTemplePlayers() {
		for(Player p : server.getOnlinePlayers()) {
			if(!TempleManager.templePlayerMap.containsKey(p)) {
				TempleManager.templePlayerMap.put(p, new TemplePlayer(p));
			}
		}
	}

	/*PLAYER METHODS*/

	/**
	 * Tells user list of all players in temples
	 * 
	 * @param p -User
	 */
	public static void playerList(Player p) {
		if (playerSet.isEmpty()) {
			tellPlayer(p, Translation.tr("templesEmpty"));
			return;
		}
		for(Game game : gameSet) {
			game.playerList(p);
		}
	}

	/**
	 * Lists players that are not yet ready
	 * 
	 * @param p -User
	 */
	public static void notReadyList(Player p) {
		if (playerSet.isEmpty()) {
			tellPlayer(p, Translation.tr("templesEmpty"));
			return;
		}
		for(Game game : gameSet) {
			game.notReadyList(p);
		}
	}

	/**
	 * Removes all players from temples
	 */
	public static void removeAll() {
		// Attempts to make all players leave whatever they are doing
		for(Player p : server.getOnlinePlayers()) {
			playerLeave(p);
		}
	}

	/**
	 * Removes all nonplayer entities
	 * 
	 * @param world -Templeworld
	 */
	public static void clearWorld(World world) {
		for(Chunk c : world.getLoadedChunks()) {
			world.regenerateChunk(c.getX(), c.getZ());
		}
		for(Entity e : world.getEntities()) {
			// do not remove players!
			if(!(e instanceof Player)) {
				e.remove();
			}
		}
	}

	/**
	 * Attempts to remove a player from the Temple session.
	 * The player is teleported back to his previous location, and
	 * is removed from all the sets and maps.
	 * 
	 * @param p -User leaving
	 * TODO tp finishloc only if endblock
	 */
	public static void playerLeave(Player p) {		
		TemplePlayer tp = templePlayerMap.get(p);
		if(tp == null) {
			return;
		}
		Temple temple = tp.currentTemple;
		Game game = tp.currentGame;
		if(temple == null && game == null) {
			return;
		}
		p.setFireTicks(0);
		tp.currentTemple = null;
		tp.currentGame = null;
		tp.currentCheckpoint = null;
		tp.resetRoundStats();
		if(game != null) {
			game.readySet.remove(p);
			game.playerSet.remove(p);
		}
		playerSet.remove(p);
		MobArenaClasses.classMap.remove(p);

		if(TCUtils.hasPlayerInventory(p.getName())) {
			TCUtils.restorePlayerInventory(p);
		}
		String msg;
		if(game.endSet.contains(p)) {
			
			if(temple.finishLocation == null) {
				p.teleport(locationMap.get(p));
				msg = Translation.tr("game.tpsavedloc");
			} else {
				p.teleport(temple.finishLocation);
				msg = Translation.tr("game.tpfinishloc");
			}
		} else {
				p.teleport(locationMap.get(p));
				msg = Translation.tr("game.tpsavedloc");
		}
		TempleManager.tellPlayer(p, msg);

		locationMap.remove(p);

		if(temple.editorSet.remove(p)) {
			if(temple.editorSet.isEmpty() && templeEditMap.containsKey(temple.templeName)) {
				TCUtils.deleteTempWorld(templeEditMap.remove(temple.templeName));
			}
		}
		
		for( LivingEntity tamedMob : tp.tamedMobSet) {
			tamedMob.damage(tamedMob.getMaxHealth());
			tp.tamedMobSet.remove(tamedMob);
		}

		if(game != null && !game.isEnding) {
			if(game.readySet.equals(game.playerSet)) {
				if(!game.isRunning && playerSet.size() > 0) {
					game.startGame();
				} else {
					game.endGame();
				}
			}
		}
	}

	/* MISC METHODS */

	/**
	 * Load terrain generators
	 * Handles breakable and placeable blocks
	 */
	private static void loadMisc() {
		// Handles Chunk Generator Folder
		File cgFolder = new File("plugins/TempleCraft/ChunkGenerators");
		if(!cgFolder.exists()) {
			cgFolder.mkdir();
			//TCUtils.copyFromJarToDisk("Flat1.jar", cgFolder);
		}

		// Handles getting mob gold drop amount from config
		String[] g = goldPerMob.split("-");
		try {
			if(g[0] != null) {
				mobGoldMin = Integer.parseInt(g[0]);
				if(g[1] != null) {
					mobGoldRan = Integer.parseInt(g[1])-Integer.parseInt(g[0]);
				}
			}
		} catch(Exception e) {
			mobGoldMin = 0;
			mobGoldRan = 0;
		}

		// Handles what is breakable
		for(String s : breakableMats.split(",")) {
			s = s.trim();
			if(!s.isEmpty()) {
				breakable.add(Integer.parseInt(s));
			}
		}
		
		// Handles what is placeable
		for(String s : placeableMats.split(",")) {
			s = s.trim();
			if(!s.isEmpty()) {
				placeable.add(Integer.parseInt(s));
			}
		}
	}

	/**
	 * Sends a message to a player.
	 * 
	 * @param p   -User
	 * @param msg -Message
	 */
	public static void tellPlayer(Player p, String msg) {
		if (p == null) {
			return;
		}
		p.sendMessage(c3 + "[TC] " + c2 + msg);
	}

	/**
	 * Sends a message to all players in the Temple.
	 * 
	 * @param msg -Message
	 */
	public static void tellAll(String msg) {
		Player[] players = server.getOnlinePlayers();
		for(Player p: players) {
			tellPlayer((Player)p, msg);
		}
	}
}