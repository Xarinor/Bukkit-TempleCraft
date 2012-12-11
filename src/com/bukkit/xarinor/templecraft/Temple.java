package com.bukkit.xarinor.templecraft;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;

import com.bukkit.xarinor.templecraft.util.Translation;

/**
* Temples.java
* This work is dedicated to the public domain.
* 
* @author Xarinor
* @author bootscreen
* @author msingleton
*/
public class Temple {
	protected File config;

	// Convenience variables.
	public String templeName;
	public boolean isSetup;
	protected boolean isEnabled;
	public int maxPlayersPerGame = -1;
	public int maxDeaths = -1;
	protected String owners;
	protected String editors;
	protected File ChunkGeneratorFile;
	protected Environment env = null;
	protected WorldType wt = null;

	// Sets and Maps for storing players and their locations.
	protected Set<String> ownerSet	= new HashSet<String>();
	protected Set<String> accessorSet = new HashSet<String>();
	protected Set<Player> editorSet   = new HashSet<Player>();

	public Location finishLocation = null;

	public static int mobSpawner = 7;
	public static int diamondBlock = 57;
	public static int ironBlock = 42;
	public static int goldBlock = 41;
	public Set<Location> coordLocSet  = new HashSet<Location>();
	//public Set<Location> placeableMatLocSet  = new HashSet<Location>();
	public static int[] coordBlocks = {mobSpawner, diamondBlock, ironBlock, goldBlock, 63, 68};

	/**
	 * Breaker
	 * TODO Change maybe
	 */
	protected Temple(){}

	/**
	 * Constructor
	 * @param name
	 */
	protected Temple(String name) {		
		config	 = TCUtils.getConfig("temples");
		templeName = name;
		//Fix isSetup and Class item things
		owners			= TCUtils.getString(config,"Temples."+name+".owners", "");
		editors		   = TCUtils.getString(config,"Temples."+name+".editors", "");
		isSetup		   = TCUtils.getBoolean(config,"Temples."+name+".isSetup", false);
		maxPlayersPerGame = TCUtils.getInt(config,"Temples."+name+".maxPlayersPerGame", -1);

		try {
			String worldName = TCUtils.getString(config, "Temples."+name+".finishLocation.world", null);
			if(worldName != null && worldName.length() > 0) {
				World world = TempleManager.server.getWorld(worldName);
				if(world != null) {
					double x = TCUtils.getDouble(config, "Temples."+name+".finishLocation.x");
					double y = TCUtils.getDouble(config, "Temples."+name+".finishLocation.y");
					double z = TCUtils.getDouble(config, "Temples."+name+".finishLocation.z");
					float pitch = (float) TCUtils.getDouble(config, "Temples."+name+".finishLocation.pitch");
					float yaw = (float) TCUtils.getDouble(config, "Temples."+name+".finishLocation.yaw");
					finishLocation = new Location(world, x, y, z, yaw, pitch);
				}
			}
		} catch (Exception e) {
			System.out.print("[TempleCraft] Could not create Finish-Location.");
			TCUtils.debugMessage("Could not create Finish-Location - " + e.getMessage());
		}
		isEnabled  = true;
		ChunkGeneratorFile = TCRestore.getChunkGenerator(this);
		env = Environment.NORMAL;
		wt = WorldType.NORMAL;
		loadEditors();
		TempleManager.templeSet.add(this);
	}

	/* LOAD/SAVE METHODS */

	/**
	 * Load this temple as type
	 * (Game, edit, etc.)
	 * 
	 * @param type
	 * @return
	 */
	public World loadTemple(String type) {
		World result = null;
		String worldName;
		if(TempleManager.constantWorldNames) {
			worldName = "TCTempleWorld_"+templeName;
		} else {
			worldName = TCUtils.getNextAvailableTempWorldName(type);
		}
		if(worldName == null) {
			return null;
		}

		// if the world already exists and it can not be deleted (i.e. it contains players) return null
		World world = TempleManager.server.getWorld(worldName);
		if(world != null && TempleManager.constantWorldNames) {
			return null;
		}
		//TODO Try harder?
		if(world != null && !TCUtils.deleteTempWorld(world)) {
			return null;
		}

		File worldFolder = new File(worldName);
		if(worldFolder.exists()) {
			if(!TCUtils.deleteFolder(worldFolder)) {
				TCUtils.debugMessage("error while deleting " + worldFolder.getAbsolutePath());
			} else {
				TCUtils.debugMessage(worldFolder.getAbsolutePath() + " deleted.");
				System.out.println("[TempleCraft] World " + worldName + " deleted!");
			}
		}

		if(worldFolder.exists()) {
			return null;
		}

		File tcffile = new File("plugins/TempleCraft/SavedTemples/"+templeName+TempleCraft.fileExtention);

		WorldCreator wc = new WorldCreator(worldName);
		if(env != null) {
			wc.environment(env);
			TCUtils.setInt(TCUtils.getConfig("temples"),"Temples."+this.templeName+".environment", env.getId());
		} else {
			wc.environment(Environment.getEnvironment(TCUtils.getInt(TCUtils.getConfig("temples"),"Temples."+this.templeName+".environment", Environment.NORMAL.getId())));
		}

		if(wt != null) {
			wc.type(wt);
			TCUtils.getString(TCUtils.getConfig("temples"),"Temples."+this.templeName+".worldtype", wt.getName());
		} else {
			wc.type(WorldType.getByName(TCUtils.getString(TCUtils.getConfig("temples"),"Temples."+this.templeName+".worldtype", WorldType.NORMAL.getName())));
		}
		if(ChunkGeneratorFile != null) {
			ChunkGenerator cg = TCRestore.getChunkGenerator(ChunkGeneratorFile);
			if(cg != null) {
				wc.generator(cg);
			}
		}

		// if the tcf file doesn't exist
		if(TCRestore.loadTemple(worldName, this) || !tcffile.exists()) {
			try {
				result = TempleManager.server.createWorld(wc);
				
				if(TempleCraft.MVWM != null) {
					TempleCraft.MVWM.addWorld(result.getName(), result.getEnvironment(), Long.toString(result.getSeed()), result.getWorldType(), false, null, true);
				}
				//TODO Check if default Cat works
				/*if(TempleCraft.catacombs != null)
				{
					TempleCraft.catacombs.loadWorld(result.getName());
				}*/
				System.out.println("[TempleCraft] World \""+worldName+"\" Loaded!");
			} catch (Exception e) {
				System.out.println("[TempleCraft] World \""+worldName+"\" could not be loaded!");
				e.printStackTrace();
			}
		} else if(type.equals("Edit") || type.equals("Convert")) {
			//TODO Check
			/*File file = new File("plugins/TempleCraft/SavedTemples/"+templeName);
			file.mkdir();
			TCUtils.copyFromJarToDisk("Flat1.jar", file);
			ChunkGeneratorFile = new File("plugins/TempleCraft/SavedTemples/"+templeName+"/Flat1.jar");
			ChunkGenerator cg = TCRestore.getChunkGenerator(ChunkGeneratorFile);
			wc.generator(cg);*/
			result = TempleManager.server.createWorld(wc);
			if(TempleCraft.MVWM != null) {
				TempleCraft.MVWM.addWorld(result.getName(), result.getEnvironment(), Long.toString(result.getSeed()), result.getWorldType(), false, null, true);
			}
			//TODO Check
			/*if(TempleCraft.catacombs != null)
			{
				TempleCraft.catacombs.loadWorld(result.getName());
			}*/
			TCRestore.loadTemple(new Location(result,0,0,0), this);
		} else {
			return null;
		}

		if(result == null) {
			return null;
		}

		if(type.equals("Edit")) {
			TempleManager.templeEditMap.put(templeName, result);
		} else {
			result.setAutoSave(false);
		}

		result.setKeepSpawnInMemory(false);
		coordLocSet.addAll(TCRestore.getSignificantLocs(this, result));
		return result;
	}

	/**
	 * Save this temple to SavedTemples
	 * @param world	 -Temple world
	 * @param player -User
	 */
	protected void saveTemple(World world, Player player) {
		TempleManager.tellPlayer(player, Translation.tr("templeSave"));
		TCRestore.saveTemple(world, this);

		isSetup = trySetup(world);
		if(TCUtils.getBoolean(config,"Temples."+templeName+".isSetup", isSetup) != isSetup) {
			TCUtils.setBoolean(config,"Temples."+templeName+".isSetup", isSetup);
			if(isSetup) {
				TempleManager.tellPlayer(player, Translation.tr("templeNowSetup", templeName));
			} else {
				TempleManager.tellPlayer(player, Translation.tr("templeNoLongerSetup", templeName));
			}
		} else if(!isSetup) {
			TempleManager.tellPlayer(player, Translation.tr("templeNotSetup", templeName));
		}

		// ChunkGenerator
		if(ChunkGeneratorFile != null) {
			File destination = new File("plugins/TempleCraft/SavedTemples/"+templeName+"/"+ChunkGeneratorFile.getName());
			if(!destination.exists()) {
				File folder = new File("plugins/TempleCraft/SavedTemples/"+templeName);
				folder.mkdir();
				//TCRestore.copyFile(ChunkGeneratorFile, destination);
				try {
					TCRestore.copyFiles(ChunkGeneratorFile, destination);
				} catch (IOException e) {
					TCUtils.debugMessage("could not copy chunkgenerator " + ChunkGeneratorFile.getAbsolutePath() + " to " + destination.getAbsolutePath() + "\n" + e.getMessage());
					e.printStackTrace();
				}
			}
		}
		TempleManager.tellPlayer(player, Translation.tr("templeSaved"));
	}

	/**
	 * Load the editors allowed in this Temple
	 */
	private void loadEditors() {
		for(String s : owners.split(",")) {
			s = s.trim();
			ownerSet.add(s);
		}

		for(String s : editors.split(",")) {
			s = s.trim();
			accessorSet.add(s);
		}
	}

	/**
	 * Remove editors from this Temple
	 */
	public void removeEditors() {
		for(Player p: editorSet) {
			TemplePlayer tp = TempleManager.templePlayerMap.get(p);
			if(tp == null) {
				return;
			}
			if(tp.currentTemple == this) {
				TempleManager.playerLeave(p);
			}
		}
	}

	/**
	 * Adds a new owner to this Temple
	 * Also update the editors
	 * 
	 * @param playerName -New owner name
	 * @return
	 */
	protected boolean addOwner(String playerName) {
		if(ownerSet.contains(playerName)) {
			return false;
		} else {
			ownerSet.add(playerName);
		}
		updateEditors();
		return true;
	}

	/**
	 * Adds a new editor to this Temple
	 * 
	 * @param playerName -New editor name
	 * @return
	 */
	protected boolean addEditor(String playerName) {
		if(accessorSet.contains(playerName)) {
			return false;
		} else {
			accessorSet.add(playerName);
		}
		updateEditors();
		return true;
	}

	/**
	 * Removes an editor from this Temple
	 * 
	 * @param playerName -Editor name
	 * @return
	 */
	protected boolean removeEditor(String playerName) {
		boolean result;
		result = (ownerSet.remove(playerName) || accessorSet.remove(playerName));
		updateEditors();
		return result;
	}

	/**
	 * Update the editors of the Temple
	 */
	private void updateEditors() {
		StringBuilder owners = new StringBuilder();
		for(String s : ownerSet) {
			if(owners.length() == 0) {
				owners.append(s);
			} else {
				owners.append(","+s);
			}
		}
		StringBuilder editors = new StringBuilder();
		for(String s : accessorSet) {
			if(editors.length() == 0) {
				editors.append(s);
			} else {
				editors.append(","+s);
			}
		}
		this.owners = owners.toString();
		this.editors = editors.toString();

		try {
			saveConfig();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Saves the config preferences for this Temple
	 * 
	 * @throws IOException
	 */
	protected void saveConfig() throws IOException {
		YamlConfiguration c = YamlConfiguration.loadConfiguration(config);
		c.set("Temples."+templeName+".owners", owners);		
		c.set("Temples."+templeName+".editors", editors);
		c.save(config);
	}

	/* SETUP METHODS */

	/**
	 * Trying to set up the new instance Temple
	 * 
	 * TODO Improve!!
	 * 
	 * @param world -World name
	 * @return
	 */
	public boolean trySetup(World world) {		
		boolean foundLobbyLoc = false;
		boolean foundTempleLoc = false;

		for(Block b: getBlockSet(world,Material.WALL_SIGN.getId())) {
			if(foundLobbyLoc && foundTempleLoc) {
				break;
			}

			Sign sign = (Sign) b.getState();
			if(!foundLobbyLoc) {
				foundLobbyLoc = checkSign("lobby", sign);
			}
			if(!foundTempleLoc) {
				foundTempleLoc = checkSign("spawnarea", sign);
			}
		}
		for(Block b: getBlockSet(world,Material.SIGN_POST.getId())) {	 
			if(foundLobbyLoc && foundTempleLoc) {
				break;
			}
			Sign sign = (Sign) b.getState();
			if(!foundLobbyLoc) {
				foundLobbyLoc = checkSign("lobby", sign);
			}
			if(!foundTempleLoc) {
				foundTempleLoc = checkSign("spawnarea", sign);
			}
		}
		for(Block b: getBlockSet(world,diamondBlock)) {
			if(foundTempleLoc) {
				break;
			}
			Block rb = b.getRelative(0, -1, 0);
			if(rb.getTypeId() == ironBlock) {
				foundTempleLoc = true;
			}
		}
		isSetup = foundLobbyLoc && foundTempleLoc;
		return isSetup;
	}

	/**
	 * Gets the location of the new lobby
	 * defined by the lobby sign
	 * 
	 * @param world -World name
	 * @return
	 */
	public Location getLobbyLoc(World world) {		
		for(Block b: getBlockSet(world,Material.WALL_SIGN.getId())) {			
			Sign sign = (Sign) b.getState();
			String[] Lines = sign.getLines();
			if(Lines[0].equals("[TC]") || Lines[0].equals("[TempleCraft]")) {
				if(Lines[1].toLowerCase().equals("lobby")) {
					return b.getLocation();
				}
			}
		}
		for(Block b: getBlockSet(world,Material.SIGN_POST.getId())) {	 
			Sign sign = (Sign) b.getState();
			String[] Lines = sign.getLines();
			if(Lines[0].equals("[TC]") || Lines[0].equals("[TempleCraft]")) {
				if(Lines[1].toLowerCase().equals("lobby")) {
					return b.getLocation();
				}
			}
		}
		return null;
	}

	/**
	 * Check a sign for TempleCraft reference [TC] or [TempleCraft]
	 * 
	 * TODO Improve?
	 * 
	 * @param key	-The reference key
	 * @param sign	-Sign to check
	 * @return
	 */
	private boolean checkSign(String key, Sign sign) {
		if(sign == null) {
			return false;
		}
		String[] Lines = sign.getLines();
		if(!Lines[0].equals("[TC]") && !Lines[0].equals("[TempleCraft]")) {
			return false;
		}

		if(Lines[1].toLowerCase().equals(key)) {
			return true;
		}
		return false;
	}

	/**
	 *TODO Good description
	 *
	 *@param world	-World name
	 *@param id		-Block set id
	 */
	private Set<Block> getBlockSet(World world, int id) {
		Set<Block> result = new HashSet<Block>();

		if(!coordLocSet.isEmpty()) {
			for(Location loc : coordLocSet) {
				Block b = world.getBlockAt(loc);
				if(b.getTypeId() == id) {
					result.add(b);
				}
			}
		}
		return result;
	}

	/* MISC METHODS */

	/**
	 * Sends a message to a player.
	 * 
	 * @param player-Player to contact
	 * @param msg	-message to send
	 */
	protected void tellPlayer(Player player, String msg) {
		if (player == null) {
			return;
		}
		player.sendMessage(ChatColor.GREEN + "[TC] " + ChatColor.WHITE + msg);
	}
}
