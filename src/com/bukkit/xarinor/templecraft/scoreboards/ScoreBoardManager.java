package com.bukkit.xarinor.templecraft.scoreboards;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.bukkit.xarinor.templecraft.TCUtils;
import com.bukkit.xarinor.templecraft.TempleManager;
import com.bukkit.xarinor.templecraft.games.Game;

/**
* ScoreBoardManager.java
* This work is dedicated to the public domain.
* 
* @author Xarinor
* @author bootscreen
* @author msingleton
*/
public class ScoreBoardManager {
	public List<ScoreBoard> scoreBoards	= new ArrayList<ScoreBoard>();

	// Configuration
	public static File configFile = null;

	/**
	 * Constructor
	 */
	public ScoreBoardManager() {
		configFile = TCUtils.getConfig("scoreboards");
		loadScoreBoards();
	}

	/**
	 * Saves all scoreboards from the config
	 */
	public void save() {
		for(ScoreBoard sb : scoreBoards) {
			sb.save();
		}
	}

	/**
	 * Loads all scoreboards from the config
	 */
	private void loadScoreBoards() {
		YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		if(!config.isConfigurationSection("ScoreBoards")) {
			config.createSection("ScoreBoards");
			saveConfig(config);
			return;
		}
		ConfigurationSection scoreBoards = config.getConfigurationSection("ScoreBoards");
		for(String id : scoreBoards.getKeys(false)) {
			ConfigurationSection board = scoreBoards.getConfigurationSection(id);
			Location p1	   = getLoc(board,"p1");
			Location p2	   = getLoc(board,"p2");
			String templeName = board.getString("temple", "");
			String gameMode   = board.getString("gamemode", "");
			String type	   = board.getString("type","");
			if(p1 == null || p2 == null || templeName.equals("") || gameMode.equals("") || type.equals("")) {
				scoreBoards.set(id, null);
				continue;
			}
			removeDuplicates(p1);
			newScoreBoard(id,p1,p2, templeName, gameMode, type);
		}
		saveConfig(config);
	}

	/**
	 * Gets a scoreboard by a key sign
	 * 
	 * @param sign -Needle
	 * @return
	 */
	public ScoreBoard getScoreBoardBySign(Sign sign) {
		Location loc = sign.getBlock().getLocation();
		for(ScoreBoard sb : scoreBoards) {
			if(sb.inRegion(loc)) {
				return sb;
			}
		}
		return null;
	}

	/**
	 * Deletes a scoreboard
	 * 
	 * @param sb -ScoreBoard
	 */
	public void deleteScoreBoard(ScoreBoard sb) {
		if(sb == null) {
			return;
		}
		deleteScoreBoardConfig(sb.id);
		for(int i = 0;i<sb.signs.size();i++) {
			sb.signs.get(i).getBlock().setTypeId(0);
		}
		this.scoreBoards.remove(sb);
	}

	/**
	 * Removes dublicates
	 * 
	 * @param loc
	 */
	private void removeDuplicates(Location loc) {
		for(ScoreBoard sb : scoreBoards) {
			if(sb.p1.equals(loc)) {
				deleteScoreBoard(sb);
			}
		}
	}

	/**
	 * Saves the config file for scoreboards
	 * 
	 * @param config
	 */
	private void saveConfig(YamlConfiguration config) {
		try {
			config.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds a new scorebaord to the config
	 * 
	 * @param p1
	 * @param p2
	 * @param templeName
	 * @param gameMode
	 * @param type
	 */
	public void newScoreBoard(Location p1, Location p2, String templeName, String gameMode, String type) {
		newScoreBoard(getUniqueID(),p1,p2,templeName,gameMode,type);
	}

	/**
	 * Adds a new scorebaord with board id to the config
	 * 
	 * @param id -Scoreboard id
	 * @param p1
	 * @param p2
	 * @param templeName
	 * @param gameMode
	 * @param type
	 */
	public void newScoreBoard(String id, Location p1, Location p2, String templeName, String gameMode, String type) {
		if(type.equals("recent")) {
			scoreBoards.add(new RecentScoreBoard(id,p1,p2,templeName,gameMode));
		}
		//else if(type.equals("highscore"))
		//scoreBoards.add(new HighScoreBoard(id,p1,p2,templeName,gameMode));
		else {
			deleteScoreBoardConfig(id);
		}
	}

	/**
	 * Gets an new unique sign id
	 * 
	 * @return
	 */
	private String getUniqueID() {
		Set<String> ids = new HashSet<String>();
		for(ScoreBoard sb : scoreBoards) {
			ids.add(sb.id);
		}
		int i = 1;
		while(ids.contains(i+"")) {
			i++;
		}
		return i+"";
	}

	/**
	 * Deletes the scoreboard with the key id
	 * 
	 * @param id
	 */
	private void deleteScoreBoardConfig(String id) {
		YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		if(!config.isConfigurationSection("ScoreBoards")) {
			config.createSection("ScoreBoards");
			saveConfig(config);
			return;
		}
		ConfigurationSection scoreBoards = config.getConfigurationSection("ScoreBoards");
		scoreBoards.set(id, null);
		saveConfig(config);
	}

	/**
	 * @param board
	 * @param path
	 * @return
	 */
	private Location getLoc(ConfigurationSection board, String path) {
		if(!board.isConfigurationSection(path)) {
			return null;
		}
		ConfigurationSection location = board.getConfigurationSection(path);
		World world = TempleManager.server.getWorld(location.getString("world"));
		double x = location.getDouble("x",0);
		double y = location.getDouble("y",0);
		double z = location.getDouble("z",0);
		if(world == null || y == 0) {
			return null;
		}
		return new Location(world,x,y,z);
	}

	/**
	 * Updates all scoreboards
	 * 
	 * @param game
	 * @param scores
	 */
	public void updateScoreBoards(Game game, List<String> scores) {
		String templeName = game.temple.templeName;
		String gameMode = game.getClass().getSimpleName().toLowerCase();
		for(ScoreBoard sb : scoreBoards) {
			if(sb.templeName.equals(templeName) && sb.gameMode.equals(gameMode)) {
				sb.updateScores(scores);
			}
		}
	}
}
