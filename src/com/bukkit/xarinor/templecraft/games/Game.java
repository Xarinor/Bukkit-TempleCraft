package com.bukkit.xarinor.templecraft.games;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
//TODO TESTING
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.bukkit.xarinor.templecraft.TCEntityHandler;
import com.bukkit.xarinor.templecraft.TCUtils;
import com.bukkit.xarinor.templecraft.Temple;
import com.bukkit.xarinor.templecraft.TempleCraft;
import com.bukkit.xarinor.templecraft.TempleManager;
import com.bukkit.xarinor.templecraft.TemplePlayer;
import com.bukkit.xarinor.templecraft.custommobs.CustomMob;
import com.bukkit.xarinor.templecraft.custommobs.CustomMobType;
import com.bukkit.xarinor.templecraft.custommobs.CustomMobUtils;
import com.bukkit.xarinor.templecraft.custommobs.MobManager;
import com.bukkit.xarinor.templecraft.util.MobArenaClasses;
import com.bukkit.xarinor.templecraft.util.MobSpawnProperties;
import com.bukkit.xarinor.templecraft.util.Pair;
import com.bukkit.xarinor.templecraft.util.Translation;

/**
* Game.java
* This work is dedicated to the public domain.
* 
* @author Xarinor
* @author bootscreen
* @author msingleton
*/
public class Game {
	public World world;
	public Temple temple;
	public String gameType;
	public String gameName;
	public boolean isRunning	  = false;
	public boolean isEnding	   = false;
	public boolean isSetup		= false;
	public boolean isLoaded	   = false;
	public boolean alwaysJoinable = false;
	public boolean usingClasses   = false;
	public int rejoinCost;
	public int maxPlayers;

	// Colors
	public static ChatColor c1 = TempleCraft.c1;
	public static ChatColor c2 = TempleCraft.c2;
	public static ChatColor c3 = TempleCraft.c3;

	protected static File messageFile = null;

	// Location variables for the Temple region.
	public Location lobbyLoc = null;
	public Location startLoc = null;

	// Map for Death Message
	public Map<Integer, Entity> lastDamager = new HashMap<Integer, Entity>();
	// Contains Mob Spawnpoint Locations
	public Set<Location> mobSpawnpointSet = new HashSet<Location>();

	// Score Vars
	public List<Pair<String,Double>> standings = new ArrayList<Pair<String,Double>>();
	public int displayAmount = 5;
	public int saveAmount = 5;

	// Contains Active Mob Spawnpoints and Creature Types
	public Map<Location,MobSpawnProperties> mobSpawnpointMap	 = new HashMap<Location,MobSpawnProperties>();
//	public Map<Location,MobSpawnProperties> mobSpawnpointConstantMap	 = new HashMap<Location,MobSpawnProperties>();
	public Map<Integer, Integer> mobGoldMap			= new HashMap<Integer, Integer>();
	public Map<Location, Integer> checkpointMap		= new HashMap<Location, Integer>();
	public Map<Location, String[]> chatMap			 = new HashMap<Location, String[]>();
	public Map<Location, List<ItemStack>> rewardLocMap = new HashMap<Location, List<ItemStack>>();
	public Map<Location, Integer> lobbyLocMap		  = new HashMap<Location, Integer>();
	public Map<Location, Integer> startLocMap		  = new HashMap<Location, Integer>();
	//TODO TESTING
	public Map<Chunk, Entity[]> tempMobLoc		  = new HashMap<Chunk, Entity[]>();
	public Map<Player, Integer> playerDeathMap		  = new HashMap<Player, Integer>();

	public Set<Player> playerSet		= new HashSet<Player>();
	public Set<Player> readySet		 = new HashSet<Player>();
	public Set<Player> deadSet		  = new HashSet<Player>();
	public Set<Player> endSet		  = new HashSet<Player>();
	public Set<Player> rewardSet		= new HashSet<Player>();
	public Set<LivingEntity> monsterSet = new HashSet<LivingEntity>();

	public Set<Pair<Location,Set<Integer>>> placeableLocs = new HashSet<Pair<Location,Set<Integer>>>();
	public Set<Pair<Location,Set<Integer>>> breakableLocs = new HashSet<Pair<Location,Set<Integer>>>();
	public Set<Location> coordLocSet = new HashSet<Location>();
	public Set<Block> tempBlockSet   = new HashSet<Block>();
	public Set<Location> endLocSet   = new HashSet<Location>();
	public List<ItemStack> rewards	= new ArrayList<ItemStack>();

	public Set<Integer> SpawnTaskIDs   = new HashSet<Integer>();
	public Map<CustomMob,Integer> AbilityTaskIDs   = new HashMap<CustomMob,Integer>();

	public MobManager mobManager = new MobManager();

	public int maxdeaths = -1;

	public long startTime;
	public static int mobSpawner = 7;
	public static int diamondBlock = 57;
	public static int ironBlock = 42;
	public static int goldBlock = 41;
	public static int[] coordBlocks = {mobSpawner, diamondBlock, ironBlock, goldBlock, 63, 68};

	/**
	 * Constructor, the bringer of Doom
	 * 
	 * @param name		-Game-name
	 * @param temple	-active temple
	 * @param world		-temple world
	 */
	public Game(String name, Temple temple, World world) {
		TempleManager.gameSet.add(this);
		gameType	  = getClass().getSimpleName();
		gameName	  = name;
		this.world	= world;
		this.temple   = temple;
		isSetup	   = temple.isSetup;
		coordLocSet   = temple.coordLocSet;
		rejoinCost	= TempleManager.rejoinCost;
		maxPlayers	= temple.maxPlayersPerGame;
		messageFile	= TCUtils.getConfig("messages");
		maxdeaths = temple.maxDeaths;
	} 

	/**
	 * Starts the game.
	 */
	public void startGame() {		
		isRunning = true;
		startTime = System.currentTimeMillis();
		convertSpawnpoints();
		TCUtils.debugMessage("Game " + gameName + "(" + gameType + ") gestartet");
		for(Player player : playerSet) {
			TemplePlayer tp = TempleManager.templePlayerMap.get(player);
			if(tp.team != -1) {
				player.getInventory().setHelmet(new ItemStack(Material.WOOL,1,(short)0,(byte)tp.team));
			}
			getPlayerSpawnLoc(tp).getChunk().load(true);
			player.teleport(getPlayerSpawnLoc(tp));
		}
		readySet.clear();

		tellAll(Translation.tr("game.start"));
	}


	/**
	 * Ends the game.
	 */
	public void endGame() {
		try {
			TempleManager.tellAll(Translation.tr("game.finished", gameType, temple.templeName));
			TCUtils.debugMessage("Game " + gameName + "(" + gameType + ") beendet");
			if(!SpawnTaskIDs.isEmpty()) {
				for(int id : SpawnTaskIDs) {
					TCUtils.debugMessage("Cancel SpawnTask " + id);
					TempleCraft.TCScheduler.cancelTask(id);
				}
				SpawnTaskIDs.clear();
			}
			if(!AbilityTaskIDs.isEmpty()) {
				for(int id : AbilityTaskIDs.values()) {
					TCUtils.debugMessage("Cancel AbilityTask " + id);
					TempleCraft.TCScheduler.cancelTask(id);
				}
				AbilityTaskIDs.clear();
			}
			mobSpawnpointMap.clear();
			mobSpawnpointSet.clear();
			mobManager.clear();
			//TODO TESTING
			//tempMobLoc.clear();
			isRunning = false;
			isEnding = true;
			readySet.clear();
			playerSet.clear();
			playerDeathMap.clear();
			rewardPlayers(rewardSet);
		} catch (Exception e) {
			System.out.println("[TempleCraft] Error while closing the temple.");
			e.printStackTrace();		
		} finally {
			TCUtils.removePlayers(world);
			while(!world.getPlayers().isEmpty()) {
				TempleCraft.TCScheduler.scheduleSyncDelayedTask(TempleCraft.TCPlugin, new Runnable() {	
					@Override
					public void run() { }
				}, 20L);
				TCUtils.removePlayers(world);
			}
			TempleManager.gameSet.remove(this);		
			TCUtils.deleteTempWorld(world);		
		}
	}

	/**
	 * Players get their rewards! hopefully.. :)
	 * 
	 * @param players
	 */
	private void rewardPlayers(Set<Player> players) {
		
		
		for(Player p : players) {
			TCUtils.debugMessage("Try to reward Player " + p.getName());
			try {
				for(ItemStack item : rewards) {
					if(TCUtils.hasPlayerInventory(p.getName())) {
						TCUtils.addtoPlayerInventory(p, item);
					} else {
						p.getInventory().addItem(item);
					}
				}
				TempleManager.tellPlayer(p,Translation.tr("game.treasureReceived"));
			} catch (Exception e) {
				System.out.print("[TempleCraft] Error during reward distribution to player:" + p.getName());
				System.out.print("[TempleCraft] " + e.getMessage());
			}
		}
	}

	/**
	 * Convert all lobby elements
	 */
	protected void convertLobby()
	{
		// Convert lobby sign
		for(Block b: getBlockSet(Material.WALL_SIGN.getId())) {	 
			if(!(b.getState() instanceof Sign)) {
				continue;
			}
			Sign sign = (Sign) b.getState();
			handleSign(sign);
		}
		for(Block b: getBlockSet(Material.SIGN_POST.getId())) {	 
			if(!(b.getState() instanceof Sign)) {
				continue;
			}
			Sign sign = (Sign) b.getState();
			handleSign(sign);
		}

		// Convert lobby starting block
		for(Block b: getBlockSet(goldBlock)) {
			Block rb = b.getRelative(0, -1, 0);
			if(rb.getTypeId() == ironBlock) {
				Block rb2 = b.getRelative(0,1,0);
				if(rb2.getType().equals(Material.WOOL)) {
					lobbyLocMap.put(rb.getLocation(), (int)rb2.getData());
				} else {
					lobbyLocMap.put(rb.getLocation(), -1);
				}
				b.setTypeId(0);
				rb.setTypeId(ironBlock);
			} else {
				temple.coordLocSet.remove(b);
			}
		}
	}

	/**
	 * Convert all spawnpoints Player and Mob
	 */
	protected void convertSpawnpoints() {
		// Cast the block to a sign to get the text on it.
		for(Block b: getBlockSet(Material.WALL_SIGN.getId())) {	 
			Sign sign = (Sign) b.getState();
			handleSign(sign);
		}
		for(Block b: getBlockSet(Material.SIGN_POST.getId())) {	 
			Sign sign = (Sign) b.getState();
			handleSign(sign);
		}
		// Mob spawnpoint
		for(Block b: getBlockSet(mobSpawner)) {
			if(b.getLocation().getBlockY()<7) {
				temple.coordLocSet.remove(b);
			}
			mobSpawnpointSet.add(b.getLocation());
			MobSpawnProperties msp = new MobSpawnProperties();
			msp.setEntityType(TCEntityHandler.getRandomCreature());
			msp.setLocation(b.getLocation());
			mobSpawnpointMap.put(b.getLocation(),msp);
//			mobSpawnpointConstantMap.put(b.getLocation(),msp);
			b.setTypeId(0);
		}
		
		for(Block b: getBlockSet(diamondBlock)) {
			Block rb = b.getRelative(0, -1, 0);
			// Player spawnpoint
			if(rb.getTypeId() == ironBlock) {
				Block rb2 = b.getRelative(0,1,0);
				if(rb2.getType().equals(Material.WOOL)) {
					startLocMap.put(rb.getLocation(), (int)rb2.getData());
				} else {
					startLocMap.put(rb.getLocation(), -1);
				}
				b.setTypeId(0);
				rb.setTypeId(0);
			}
			// Temple end block
			else if(rb.getTypeId() == goldBlock) {
				Block rb2 = b.getRelative(0, 1, 0);
				endLocSet.add(rb.getLocation());
				b.setTypeId(0);
				rb.setTypeId(goldBlock);
				// End reward (Tri-block)
				if(rb2.getState() instanceof Chest) {
					rewards.addAll(Arrays.asList(((Chest)rb2.getState()).getInventory().getContents()));
					((Chest)rb2.getState()).getInventory().clear();
					rb2.setTypeId(0);
				}
			} else {
				temple.coordLocSet.remove(b);
			}
		}
		// Reward block
		for(Block b: getBlockSet(ironBlock)) {
			Block rb = b.getRelative(0, 1, 0);
			if(rb.getState() instanceof Chest) {
				Inventory inv = ((Chest)rb.getState()).getInventory();
				rewardLocMap.put(b.getLocation(),Arrays.asList(inv.getContents()));
				inv.clear();
				rb.setTypeId(0);
			}
		}
	}

	/**
	 * Get a block set
	 * 
	 * @param id -Set id
	 * @return
	 */
	protected Set<Block> getBlockSet(int id) {
		Set<Block> result = new HashSet<Block>();

		if(!coordLocSet.isEmpty()) {
			for(Location loc : coordLocSet) {
				Block b = world.getBlockAt(loc);
				if(b.getTypeId() == id)
					result.add(b);
			}
		}
		return result;
	}

	/**
	 * Handle Templecraft signs
	 * 
	 * @param sign
	 */
	protected void handleSign(Sign sign) {
		String[] Lines = sign.getLines();
		Block b = sign.getBlock();


		if(Lines[0].equals("[TCB]")) {
			// Predefined custom boss
			if(CustomMobUtils.bossTypeExists(Lines[1]))
			{
				CustomMobType cmt = CustomMobUtils.getBoss(Lines[1]);
				Location loc = new Location(b.getWorld(),b.getX()+.5,b.getY(),b.getZ()+.5);
				mobSpawnpointSet.add(loc);

				MobSpawnProperties msp = new MobSpawnProperties();
				msp.setDMGMulti(cmt.getDmgmulti());
				msp.setEntityType(cmt.getMobtype());
				msp.setAbilitys(cmt.getAbilitys());
				msp.setRange(cmt.getRange());
				msp.setHealth(cmt.getMaxhealth());
				msp.setMode(cmt.getMode());
				msp.setIsbossmob(true);
				
				msp.setLocation(b.getLocation());
				mobSpawnpointMap.put(b.getLocation(),msp);
//				mobSpawnpointConstantMap.put(b.getLocation(),msp);
				b.setTypeId(0);			
			}
			// Normal custom boss
			else {
				int mode = -1;
				EntityType ct = null;
				if(Lines[1].contains(":")) {
					String[] split = Lines[1].split(":");
					ct = EntityType.fromName(split[0]);

					if(ct == null) {
						System.out.println("[TempleCraft] Could not find EntityType \"" + split[0] + "\"");
						return;
					}
					if(split.length == 2) {
						try {
							mode = Integer.parseInt(split[1]);
						} catch (Exception e) {
							mode = -1;
						}
					}
				} else {
					ct = EntityType.fromName(Lines[1]);
				}
				if(ct == null) {
					System.out.println("[TempleCraft] Could not find EntityType \"" + Lines[1] + "\"");
					return;
				}
				int range = 20;
				int health = 20;
				int dmgmulti = 1;
				try {
					String[] split = Lines[2].split(":");
					if(split[0].contains("-")) {
						health = 0;
					} else {
						health = Integer.parseInt(split[0]);
					}
					if(split.length > 1) {
						if(split[1].contains("-")) {
							range = 20;
						} else {
							range = Integer.parseInt(split[1]);
						}
					}
					if(split.length > 2) {
						if(split[2].contains("-")) {
							dmgmulti = 1;
						} else {
							dmgmulti = Integer.parseInt(split[2]);
						}
					}
				} catch(Exception e) {
					health = 0;
					range = 20;
					dmgmulti = 1;
					System.out.println("[TempleCraft] Could not use this line \"" + Lines[2] + "\" for health:range:dmgmultiplicator");
				}
				Location loc = new Location(b.getWorld(),b.getX()+.5,b.getY(),b.getZ()+.5);
				mobSpawnpointSet.add(loc);

				MobSpawnProperties msp = new MobSpawnProperties();
				//msp.setEntityType(TCEntityHandler.getRandomCreature());
				msp.setAbilitys(Lines[3]);
				msp.setDMGMulti(dmgmulti);
				msp.setEntityType(ct);
				msp.setRange(range);
				msp.setHealth(health);
				msp.setMode(mode);
				msp.setIsbossmob(true);
				msp.setLocation(b.getLocation());
				mobSpawnpointMap.put(b.getLocation(),msp);
//				mobSpawnpointConstantMap.put(b.getLocation(),msp);
				b.setTypeId(0);
			}
			return;	
		}

		if(!Lines[0].equals("[TempleCraft]") && !Lines[0].equals("[TC]")) {
			// Normal message
			if(Lines[0].equals("[TempleCraftM]") || Lines[0].equals("[TCM]")) {
				String[] newLines = {Lines[1]+Lines[2],Lines[3]};
				chatMap.put(b.getLocation(), newLines);
				b.setTypeId(0);
			}
			// Long message
			else if(Lines[0].equals("[TempleCraftML]") || Lines[0].equals("[TCML]")) {
				String[] newLines = {"",Lines[3]};
				newLines[0] = getMessageFromFile(Lines[1]);
				chatMap.put(b.getLocation(), newLines);
				b.setTypeId(0);
			} else {
				temple.coordLocSet.remove(b);
			}
			return;			
		}

		// Lobby
		if(Lines[1].toLowerCase().equals("lobby")) {
			lobbyLoc = b.getLocation();
			b.setTypeId(0);
		}
		//classes
		else if(Lines[1].toLowerCase().equals("classes")) {
			//sign.getBlock().setTypeId(0);
		}
		// Placeable or breakable
		else if(Lines[1].toLowerCase().equals("place") || Lines[1].toLowerCase().equals("break")) {
			int radius = 0;
			try {
				radius = Integer.parseInt(Lines[2]);
			} catch(Exception e) {
				radius = 0;
			}
			for(int x=-radius;x<=radius;x++) {
				for(int y=-radius;y<=radius;y++) {
					for(int z=-radius;z<=radius;z++) {
						Location loc = new Location(world,b.getX() + x, b.getY() + y, b.getZ() + z);
						String[] split = Lines[3].split(",");
						Set<Integer> blocks = new HashSet<Integer>();
						for (String str : split) {
							try {
								blocks.add(Integer.parseInt(str));
							} catch (Exception e) {
							}
						}
						// Place
						if(Lines[1].toLowerCase().equals("place")) {
							placeableLocs.add(new Pair<Location,Set<Integer>>(loc,blocks));
						}
						// Break
						else {
							breakableLocs.add(new Pair<Location,Set<Integer>>(loc,blocks));
						}
						b.setTypeId(0);
					}
				}
			}
		}
		// Checkpoint
		else if(Lines[1].toLowerCase().equals("checkpoint")) {
			try {
				checkpointMap.put(sign.getBlock().getLocation(), Integer.parseInt(Lines[3]));
				String[] newLines = {Translation.tr("game.checkpoint"),Lines[3]};
				chatMap.put(b.getLocation(), newLines);
			} catch(Exception e) {
				checkpointMap.put(sign.getBlock().getLocation(), 5);
				String[] newLines = {Translation.tr("game.checkpoint"),"5"};
				chatMap.put(b.getLocation(), newLines);
			}
			b.setTypeId(0);
		}
		// Player spawnarea
		else if(Lines[1].toLowerCase().equals("spawnarea")) {
			int radius;
			try {
				radius = Math.abs(Integer.parseInt(Lines[3]));
			} catch(Exception e) {
				radius = 5;
			}

			//Get a square area of blocks and then keep the ones that are a distance radius away or less
			int y = b.getY();
			for(int i=-radius;i<=radius;i++) {
				for(int k=-radius;k<=radius;k++) {
					int x = b.getX()+i;
					int z = b.getZ()+k;
					Location loc = new Location(world,x, y, z);
					if(TCUtils.distance(b.getLocation(), loc) <= radius) {
						startLocMap.put(loc, -1);
					}
				}
			}
			b.setTypeId(0);	
		}
		// Normal mob spawnpoint
		else {
			int mode = -1;
			EntityType ct = null;
			// custom properties
			if(Lines[1].contains(":")) {
				String[] split = Lines[1].split(":");
				ct = EntityType.fromName(split[0]);
				if(split.length == 2) {
					try {
						mode = Integer.parseInt(split[1]);
					} catch (Exception e) {
						mode = -1;
					}
				}
			}
			// Absolutely normal
			else {
				ct = EntityType.fromName(Lines[1]);
			}

			if(ct == null) {
				return;
			}
			
			/* Mob Spawnpoint (range:time:count)  */
			int range = 20;
			long time = 0;
			int count = 1;
			try {
				String[] split = Lines[3].split(":");
				if(split[0].contains("-")) {
					range = 20;
				} else {
					range = Integer.parseInt(split[0]);
				}

				if(split.length > 1) {
					if(split[1].contains("-")) {
						time = 20;
					} else {
						time = Long.parseLong(split[1]);
					}
				}

				if(split.length > 2) {
					if(split[2].contains("-")) {
						count = 1;
					} else {
						count = Integer.parseInt(split[2]);
					}
				}
			} catch(Exception e) {
				range = 20;
				time = 0;
				count = 1;
			}

			int health;
			try {
				health = Integer.parseInt(Lines[2]);
			} catch(Exception e) {
				health = 0;
			}
			int dmgmulti = 1;
			Location loc = new Location(b.getWorld(),b.getX()+.5,b.getY(),b.getZ()+.5);
			mobSpawnpointSet.add(loc);
			MobSpawnProperties msp = new MobSpawnProperties();
			msp.setEntityType(TCEntityHandler.getRandomCreature());
			msp.setDMGMulti(dmgmulti);
			msp.setEntityType(ct);
			msp.setRange(range);
			msp.setHealth(health);
			msp.setMode(mode);
			msp.setTime(time * 20); //time is now set to server ticks (1 sec = 20 ticks) -Tim
			msp.setCount(count);
			mobSpawnpointMap.put(b.getLocation(),msp);
//			mobSpawnpointConstantMap.put(b.getLocation(),msp);
			b.setTypeId(0);	
		}
	}

	/**
	 * get all availeable spawn locations for a team
	 * 
	 * @param team -Team
	 * @return
	 */
	public Set<Location> getPossibleSpawnLocs(int team) {
		if(team == -1) {
			return startLocMap.keySet();
		}

		Set<Location> tempSet = new HashSet<Location>();
		//for(Location l : startLocMap.keySet()) {
		//TODO Testing
		Iterator<Entry<Location, Integer>> entries = startLocMap.entrySet().iterator();
		while (entries.hasNext()) {
			Entry<Location, Integer> entry = entries.next();
			Location l = entry.getKey();
			
			if(startLocMap.get(l) == team) {
				tempSet.add(l);
			}
		}

		if(tempSet.isEmpty()) {
			return startLocMap.keySet();
		} else {
			return tempSet;
		}
	}

	/**
	 * Get the spawn location for player
	 * 
	 * @param templeplayer -Player
	 * @return
	 */
	public Location getPlayerSpawnLoc(TemplePlayer templeplayer)
	{
		Random r = new Random();
		Location loc = null;
		Set<Location> locSet = new HashSet<Location>();

		locSet = getPossibleSpawnLocs(templeplayer.team);

		for(Location l : locSet) {
			if(loc == null) {
				loc = l;
			} else if(r.nextInt(locSet.size()) == 0) {
				loc = l;
			}
		} return loc;
	}

	/**
	 * Attempts to let a player join the Temple session.
	 * Players must have an empty inventory to join the Temple. Their
	 * location will be stored for when they leave.
	 * 
	 * TODO Endloc Patch!
	 * 
	 * @param player
	 */
	public void playerJoin(Player player) {		
		if (!TempleManager.isEnabled) {
			tellPlayer(player, Translation.tr("notEnabled"));
			return;
		}
		if (playerSet.isEmpty() && !temple.trySetup(world)) {
			tellPlayer(player, Translation.tr("templeNotSetup", temple.templeName));
			TCUtils.deleteTempWorld(world);
			return;
		}
		if (playerSet.contains(player)) {
			tellPlayer(player, Translation.tr("game.alreadyPlayingSame"));
			return;
		}
		if (TempleManager.playerSet.contains(player)) {
			tellPlayer(player, Translation.tr("game.alreadyPlaying"));
			return;
		}
		TemplePlayer tp = TempleManager.templePlayerMap.get(player);
		if(tp.currentGame != null) {
			return;
		}
		if (isRunning && !alwaysJoinable) {
			tellPlayer(player, Translation.tr("game.inProgress", gameName));
			return;
		}
		if(isFull()) {
			tellPlayer(player, Translation.tr("game.isFull", gameName));
			return;
		}

		tp.currentTemple = temple;
		tp.currentGame = this;
		tp.currentCheckpoint = null;
		TempleManager.playerSet.add(player);
		playerSet.add(player);
		TCUtils.debugMessage("Player " + player.getName() + " joined Temple " + temple.templeName + ", Game " + gameName + "(" + gameType + ").");

		if(world.getPlayers().isEmpty()) {
			convertLobby();
			world.setTime(8000);
			world.setStorm(false);
		}

		if (!TempleManager.locationMap.containsKey(player)) {
			TempleManager.locationMap.put(player, player.getLocation());
		}
		if(TempleManager.manageInventory) {
			if(!TCUtils.hasPlayerInventory(player.getName())) {
				TCUtils.keepPlayerInventory(player);
			}
			TCUtils.clearInventory(player);
			player.setFoodLevel(TCUtils.MAX_FOOD);
			player.setTotalExperience(0);
		}
		TCUtils.restoreHealth(player);

		lobbyLoc.getChunk().load(true);
		player.teleport(lobbyLoc);
		tellPlayer(player, Translation.tr("game.join", gameName));
		player.setGameMode(GameMode.SURVIVAL);
	}

	/**
	 * Adds a joined Temple player to the set of ready players.
	 * 
	 * @param player
	 */
	public void playerReady(Player player) {
		readySet.add(player);
		TCUtils.debugMessage("Player " + player.getName() + " is ready. Temple " + temple.templeName + ", Game " + gameName + "(" + gameType + ").");
		if (readySet.equals(playerSet) && !isRunning) {
			startGame();
		}
	}

	/**
	 * Prints the list of players currently in the Temple session.
	 * 
	 * @param p -PLAYER
	 */
	public void playerList(Player p) {
		if (playerSet.isEmpty()) {
			return;
		}

		StringBuffer list = new StringBuffer();
		final String SEPARATOR = ", ";
		for (Player player : playerSet) {
			list.append(player.getName());
			list.append(SEPARATOR);
		}

		tellPlayer(p, Translation.tr("game.plist", gameName, list.substring(0, list.length() - 2)));
	}

	/**
	 * Resets players Class and Stats
	 * 
	 * @param player
	 */
	public void playerDeath(Player player) {
		TemplePlayer tp = TempleManager.templePlayerMap.get(player);

		deadSet.add(player);
		MobArenaClasses.classMap.remove(player);
		tp.tempSet.clear();
		tp.roundDeaths++;

		playerDeathMap.put(player, tp.roundDeaths);

		for( LivingEntity tamedMob : tp.tamedMobSet) {
			if(!tamedMob.isDead()) {
				tamedMob.damage(tamedMob.getMaxHealth());
			}
			tp.tamedMobSet.remove(tamedMob);
		}

		TCUtils.restoreHealth(player);
		player.setFoodLevel(20);
		player.setTotalExperience(0);
		player.setFireTicks(0);

		if(maxdeaths > -1 && tp.roundDeaths > maxdeaths) {
			TempleManager.playerLeave(player);
		}
	}

	/**
	 * Prints the list of players who aren't ready.
	 * 
	 * @param p -PLAYER
	 */
	public void notReadyList(Player p) {
		if(isRunning) {
			tellPlayer(p, Translation.tr("game.inProgress", gameName));
			return;
		}
		if (playerSet.isEmpty()) {
			return;
		}

		Set<Player> notReadySet = new HashSet<Player>(playerSet);
		notReadySet.removeAll(readySet);

		if (notReadySet.isEmpty()) {
			tellPlayer(p, Translation.tr("game.everyoneReady", gameName));
			return;
		}

		StringBuffer list = new StringBuffer();
		final String SEPARATOR = ", ";
		for (Player player : notReadySet) {
			list.append(player.getName());
			list.append(SEPARATOR);
		}
		tellPlayer(p, Translation.tr("game.rlist", gameName, list.substring(0, list.length() - 2)));
	}

	/**
	 * Forcefully starts the Temple, causing all players in the
	 * playerSet who aren't ready to leave, and starting the
	 * Temple for everyone else.
	 * 
	 * @param p -Player
	 */
	public void forceStart(Player p) {
		if (isRunning) {
			tellPlayer(p, Translation.tr("game.inProgress", gameName));
			return;
		}
		if (readySet.isEmpty()) {
			tellPlayer(p, Translation.tr("game.forceStartFail"));
			return;
		}

		Iterator<Player> iterator = playerSet.iterator();
		while (iterator.hasNext()) {
			Player player = iterator.next();
			if (!readySet.contains(player)) {
				TempleManager.playerLeave(player);
			}
		}

		if(p != null) {
			tellPlayer(p, Translation.tr("game.forcedStart"));
		}
	}

	/**
	 * Forcefully ends the Temple, causing all players to leave and
	 * all relevant sets and maps to be cleared.
	 * 
	 * @param player -Players
	 */
	public void forceEnd(Player player) {		
		endGame();
		if(player != null) {
			tellPlayer(player, Translation.tr("game.forcedEnd"));
		}
	}

	/* CLEANUP METHODS */

	/**
	 * Kills all monsters currently on the Temple floor.
	 */
	public void killMonsters() {		
		// Remove all monsters, then clear the Set.
		for (LivingEntity e : monsterSet) {
			if (!e.isDead()) {
				e.remove();
			}
		}
		monsterSet.clear();
	}

	/**
	 * Removes all the blocks on the Temple floor.
	 */
	public void clearTempBlocks() {
		// Remove all blocks, then clear the Set.
		for (Block b : tempBlockSet) {
			b.setType(Material.AIR);
		}
		tempBlockSet.clear();
	}

	/**
	 * Removes all items and slimes in the Temple region.
	 */
	public void clearEntities() {	
		for (Entity e : world.getEntities()) {
			if(!(e instanceof Player)) {
				e.remove();
			}
		}
	}

	/* HIGHSCORE METHODS */

	/**
	 * Check if highscore got busted
	 * 
	 * @param player	-Player
	 * @param totalTime	-His completion time
	 * @return
	 */
	protected boolean isPersonalHighScore(Player player, double totalTime) {
		for(Pair<String,Double> pair : standings) {
			if(pair.a.equals(player.getDisplayName())) {	
				if(totalTime < pair.b) {
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}

	/**
	 * Is this the best time?
	 * 
	 * TODO Maybe not only time?
	 * TODO player needed?
	 * 
	 * @param player	-Player
	 * @param totalTime	-His time
	 * @return
	 */
	protected boolean isHighScore(Player player, double totalTime) {
		Pair<String,Double> pair = standings.get(0);
		if(pair != null && totalTime < pair.b) {
			return true;
		}
		return false;
	}

	/**
	 * Sort the standings on the scoreboards
	 * 
	 * TODO Overhaul
	 */
	protected void sortStandings() {
		List<Pair<String,Double>> tempList = new ArrayList<Pair<String,Double>>();
		while(!standings.isEmpty()) {
			Pair<String,Double> min = null;
			for(int j = 0; j < standings.size(); j++) {
				if(min == null) {
					min = standings.get(j);
				} if (standings.get(j).b < min.b) {
					min = standings.get(j);
				}
			}
			standings.remove(min);
			tempList.add(min);
		}
		standings = tempList;
	}

	/**
	 * Get the scores for a temple
	 * 
	 * TODO Overhaul
	 * 
	 * @param temple -Temple
	 * @param path	 -Temple file path
	 * @return
	 */
	protected List<Pair<String, Double>> getStandings(Temple temple, String path) {
		File configFile = TCUtils.getConfig("temples");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		if(!config.isConfigurationSection("Temples."+temple.templeName+".HighScores."+path)) {
			return new ArrayList<Pair<String,Double>>();
		}

		ConfigurationSection selection = config.getConfigurationSection("Temples."+temple.templeName+".HighScores."+path);
		List<Pair<String,Double>> standings = new ArrayList<Pair<String,Double>>();

		for(String id : selection.getKeys(false)) {
			String s = selection.getString(id);
			try {
				String[] data = s.split(",");
				standings.add(new Pair<String,Double>(data[0],Double.parseDouble(data[1])));
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return standings;
	}

	/**
	 * Save the scores
	 * 
	 * @param temple -Temple
	 * @param path	 -Temple file path
	 */
	protected void saveStandings(Temple temple, String path) {
		File configFile = TCUtils.getConfig("temples");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		ConfigurationSection selection;
		if(!config.isConfigurationSection("Temples."+temple.templeName+".HighScores."+path)) {
			selection = config.createSection("Temples."+temple.templeName+".HighScores."+path);
		} else {
			selection = config.getConfigurationSection("Temples."+temple.templeName+".HighScores."+path);
		}

		for(int i = 0; i<standings.size();i++) {
			if(i >= saveAmount) {
				break;
			}
			if(!selection.contains((i+1)+"")) {
				selection.createSection((i+1)+"");
			}
			Pair<String,Double> pair = standings.get(i);
			selection.set((i+1)+"", pair.a+","+pair.b);
		}

		for(String s : selection.getKeys(false)) {
			try {
				if(Integer.parseInt(s) > saveAmount)
					selection.set(s, null);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		try {
			config.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* MISC METHODS */

	/**
	 * Sends a message to a player.
	 * 
	 * @param player
	 * @param message
	 */
	public void tellPlayer(Player player, String message) {
		TempleManager.tellPlayer(player, message);
	}

	/**
	 * Sends a message to all players in the Temple.
	 * 
	 *  @param message
	 */
	public void tellAll(String message) {
		for(Player p: playerSet) {
			tellPlayer((Player)p, message);		
		}
	}

	/**
	 * Startblock stroke!
	 * 
	 * @param player
	 * @param team
	 */
	public void hitStartBlock(Player player, int team) {
		if(isRunning && !deadSet.contains(player)) {
			return;
		}

		if(maxdeaths > -1 && playerDeathMap.containsKey(player) && playerDeathMap.get(player) > maxdeaths) {
			tellPlayer(player, Translation.tr("game.tooMuchDeaths"));
			return;
		}

		TemplePlayer tp = TempleManager.templePlayerMap.get(player);
		if(!usingClasses || MobArenaClasses.classMap.containsKey(player)) {
			if(!isRunning) {
				tellPlayer(player, Translation.tr("game.ready"));
				tp.team = team;
				if(tp.team != -1) {
					player.getInventory().setHelmet(new ItemStack(Material.WOOL,1,(short)0,(byte)team));
					tellPlayer(player, Translation.tr("game.team", TCUtils.getWoolColor(team)));
				}
				playerReady(player);
				// If a method is installed
			} else if(TempleCraft.economy != null) {				
				String s = TempleCraft.economy.format(2.0);
				String currencyName = s.substring(s.indexOf(" ") + 1);

				// if player has enough money subtract money from account

				if(TempleCraft.economy.has(player.getName(),rejoinCost)) {					
					if(rejoinCost > 0) {
						tellPlayer(player, Translation.tr("game.transaction", rejoinCost, currencyName));
						TempleCraft.economy.withdrawPlayer(player.getName(),rejoinCost);
					}

					deadSet.remove(player);
					if(tp.currentCheckpoint != null) {
						tp.currentCheckpoint.getChunk().load(true);
						player.teleport(tp.currentCheckpoint);
					} else {
						getPlayerSpawnLoc(tp).getChunk().load(true);
						player.teleport(getPlayerSpawnLoc(tp));
					}

					if(tp.team != -1) {
						player.getInventory().setHelmet(new ItemStack(Material.WOOL,1,(short)0,(byte)team));
					}
				} else {
					TempleManager.tellPlayer(player, Translation.tr("adventure.rejoinFail1"));
					TempleManager.tellPlayer(player, Translation.tr("adventure.rejoinFail2"));
				}
			} else {
				deadSet.remove(player);
				if(tp.currentCheckpoint != null) {
					tp.currentCheckpoint.getChunk().load(true);
					player.teleport(tp.currentCheckpoint);
				} else {
					getPlayerSpawnLoc(tp).getChunk().load(true);
					player.teleport(getPlayerSpawnLoc(tp));
				}

				if(!usingClasses) {
					TCUtils.restoreHealth(player);
				}

				if(tp.team != -1) {
					player.getInventory().setHelmet(new ItemStack(Material.WOOL,1,(short)0,(byte)team));
				}
			}
		} else {
			tellPlayer(player, Translation.tr("game.pickAClass"));
		}
	}

	/**
	 * Endblock stroke!
	 * 
	 * @param player
	 */
	public void hitEndBlock(Player player) {
		TCUtils.debugMessage("Player " + player.getName() + "hit EndBlock");
		TemplePlayer tp = TempleManager.templePlayerMap.get(player);
		
		if (playerSet.contains(player)) {				
			readySet.add(player);
			rewardSet.add(player);
			tp.rewards = rewards;
			int totalTime = (int)(System.currentTimeMillis()-startTime)/1000;
			tellPlayer(player, Translation.tr("game.finishTime", ""+totalTime));
			if(readySet.equals(playerSet)) {
				endGame();
			} else {
				tellPlayer(player, Translation.tr("game.readyToLeave"));
				tp.currentCheckpoint = null;
			}
		}
	}

	/**
	 * Rewardblock found!
	 * 
	 * @param player
	 * @param itemList
	 */
	public void hitRewardBlock(Player player, List<ItemStack> itemList) {
		
		rewards.addAll(itemList);

		int size = itemList.size();
		StringBuilder msg = new StringBuilder();
		if(size == 0) {
			tellAll(Translation.tr("game.emptyTreasureFound", player.getDisplayName()));
		} else {
			tellAll(Translation.tr("game.treasureFound", player.getDisplayName()));
			for(int i = 0; i<size; i++) {
				ItemStack item = itemList.get(i);
				
				if(item != null) {
					msg.append(item.getAmount()+" "+TCUtils.getMaterialName(item.getType().name()));
					if(i<size-2) {
						msg.append(", ");
					} else if(i<size-1) {
						msg.append(" "+Translation.tr("and")+" ");
					}
				}
			}
			tellAll(Translation.tr("game.treasureMessage", msg.toString()));
		}
	}

	/**
	 * Player moving
	 * 
	 * @param event -Movement
	 */
	public void onPlayerMove(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		TemplePlayer tp = TempleManager.templePlayerMap.get(p);

		//for(Location loc : chatMap.keySet()) {
		//TODO Testing
		Iterator<Entry<Location, String[]>> entries = chatMap.entrySet().iterator();
		while (entries.hasNext()) {
			Entry<Location, String[]> entry = entries.next();
			Location loc = entry.getKey();
			
			if(tp.tempSet.contains(loc)) {
				continue;
			}

			String[] msg = chatMap.get(loc);
			int range;
			String s;
			try {
				range = Integer.parseInt(msg[1]);
				s = msg[0];
			} catch(Exception e) {
				range = 5;
				s = msg[0]+msg[1];
			}

			if(s.length() > 0) {
				if(TCUtils.distance(loc, p.getLocation()) < range) {
					if(msg[0].startsWith("/")) {
						tp.tempSet.add(s);
						p.chat(s);
					} else {
						p.sendMessage(c1+Translation.tr("game.message")+": "+c2+s);
					}
					tp.tempSet.add(loc);
				}
			}
		}

		if(!isRunning) {
			return;
		}

		//for(Location loc : checkpointMap.keySet()) {
		//TODO Testing
		Iterator<Entry<Location, Integer>> entries1 = checkpointMap.entrySet().iterator();
		while (entries1.hasNext()) {
			Entry<Location, Integer> entry = entries1.next();
			Location loc = entry.getKey();
			
			if(tp.currentCheckpoint != loc && TCUtils.distance(loc, p.getLocation()) < checkpointMap.get(loc)) {
				tp.currentCheckpoint = loc;
			}
		}
	}

	/**
	 * game full?
	 * 
	 * @return
	 */
	public boolean isFull() {
		return maxPlayers != -1 && playerSet.size() >= maxPlayers;
	}

	/**
	 * Entity killer kills entity killed
	 * 
	 * @param killed
	 * @param killer
	 */
	public void onEntityKilledByEntity(LivingEntity killed, Entity killer) {

		if (killed instanceof Player) {		
			Player p = (Player) killed;

			if (!playerSet.contains(p)) {
				return;
			}

			if(killer instanceof Player) {
				TemplePlayer tp2 = TempleManager.templePlayerMap.get((Player)killer);
				tp2.roundPlayersKilled++;
			}
		} else {			
			if(killer instanceof Player) {
				TempleManager.templePlayerMap.get(killer).roundMobsKilled++;
			}
			monsterSet.remove(killed);
		}
	}

	/**
	 * Gets a message from the message file per search key
	 * 
	 * @param key
	 * @return
	 */
	public String getMessageFromFile(String key) {
		YamlConfiguration c = YamlConfiguration.loadConfiguration(messageFile);

		if(c.isSet(key)) {
			return c.getString(key,"");
		}
		return "";
	}
}
