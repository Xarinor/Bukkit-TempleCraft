package com.bukkit.xarinor.templecraft.games;

import java.util.ArrayList;
import java.util.HashSet;
//import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
//import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.bukkit.xarinor.templecraft.TCEntityHandler;
import com.bukkit.xarinor.templecraft.TCUtils;
import com.bukkit.xarinor.templecraft.Temple;
import com.bukkit.xarinor.templecraft.TempleCraft;
import com.bukkit.xarinor.templecraft.TempleManager;
import com.bukkit.xarinor.templecraft.TemplePlayer;
import com.bukkit.xarinor.templecraft.custommobs.CustomMob;
import com.bukkit.xarinor.templecraft.util.Classes;
import com.bukkit.xarinor.templecraft.util.MobSpawnProperties;
import com.bukkit.xarinor.templecraft.util.Translation;
//import com.bukkit.xarinor.templecraft.custommobs.CustomMob;

/**
* Adventure.java
* This work is dedicated to the public domain.
* 
* @author Xarinor
* @author bootscreen
* @author msingleton
*/
public class Adventure extends Game {
	/**
	 * Constructor
	 * 
	 * @param name	 -Adventure name
	 * @param temple -Temple name
	 * @param world	 -Temple world
	 */
	public Adventure(String name, Temple temple, World world) {
		super(name, temple, world);
		world.setPVP(false);
	}

	/**
	 * End this Adventure
	 */
	public void endGame() {
		super.endGame();
	}

	/**
	 * Player dies to the adventure
	 * 
	 * @param player
	 */
	public void playerDeath(Player player) {
		if(TempleCraft.economy != null) {
			String s = TempleCraft.economy.format(2.0);
			String currencyName = s.substring(s.indexOf(" ") + 1);

			if(TempleCraft.economy.has(player.getName(),rejoinCost)) {		
				if(TempleCraft.economy != null && rejoinCost > 0) {
					TempleManager.tellPlayer(player, Translation.tr("adventure.rejoin1", rejoinCost, currencyName));
					TempleManager.tellPlayer(player, Translation.tr("adventure.rejoin2"));
				}
			} else {
				TempleManager.tellPlayer(player, Translation.tr("adventure.rejoinFail1", currencyName));
				TempleManager.tellPlayer(player, Translation.tr("adventure.rejoinFail2"));
			}
		}
		lobbyLoc.getChunk().load(true);
		player.teleport(lobbyLoc);
		super.playerDeath(player);
	}

	/**
	 * Handle Templecraft signs in this Adventure
	 * 
	 * @param sign
	 */
	protected void handleSign(Sign sign)
	{
		String[] Lines = sign.getLines();

		if(!Lines[0].equals("[TempleCraft]") && !Lines[0].equals("[TC]") && !Lines[0].equals("[TCB]") &&!Lines[0].equals("[TempleCraftM]") &&
				!Lines[0].equals("[TCM]") && !Lines[0].equals("[TempleCraftML]") && !Lines[0].equals("[TCML]")) {
			return;
		}

		if(Lines[1].toLowerCase().equals("classes")) {
			if(Classes.enabled) {
				usingClasses = true;
				Classes.generateClassSigns(sign);
			}
		}
		super.handleSign(sign);
	}

	/**
	 * End block hit
	 * 
	 * @param player
	 */
	public void hitEndBlock(Player player) {
		TCUtils.debugMessage("Player " + player.getName() + " hit EndBlock in Game " + gameName + "(" + gameType + ")");
		TemplePlayer tp = TempleManager.templePlayerMap.get(player);
		if (playerSet.contains(player)) {				
			TCUtils.debugMessage("Player " + player.getName() + " contains PlayerSet.");
			readySet.add(player);
			rewardSet.add(player);
			tp.rewards = rewards;
			int totalTime = (int)(System.currentTimeMillis()-startTime)/1000;
			tellPlayer(player, Translation.tr("game.finishTime", ""+totalTime));

			// Update ScoreBoards
			List<String> scores = new ArrayList<String>();
			scores.add(player.getName());
			scores.add(tp.roundMobsKilled + "");
			scores.add(tp.roundGold + "");
			scores.add(tp.roundDeaths + "");
			scores.add(totalTime + "");
			TempleManager.SBManager.updateScoreBoards(this, scores);

			if(readySet.equals(playerSet)) {
				endGame();
			} else {
				tellPlayer(player, Translation.tr("game.readyToLeave"));
				tp.currentCheckpoint = null;
			}
		} else {
			TCUtils.debugMessage("Player " + player.getName() + " not contains PlayerSet but hit end block.", Level.WARNING);
		}
	}

	/**
	 * Player moving
	 * 
	 * @param event
	 */
	public void onPlayerMove(PlayerMoveEvent event) {
		super.onPlayerMove(event);		
		Player p = event.getPlayer();

		if(!isRunning) {
			return;
		}

//		// TODO
//		// Huge testing as unexpected .remove() could crash servers.
		Iterator<Map.Entry<Location,MobSpawnProperties>> entries = mobSpawnpointMap.entrySet().iterator();
		Set<Location> tempLocs = new HashSet<Location>();
		while (entries.hasNext()) {
			Map.Entry<Location, MobSpawnProperties> entry = entries.next();
			Location loc = entry.getKey();
			if (loc != null) {
				if(p.getLocation().distance(loc) < mobSpawnpointMap.get(loc).getRange()) {
					tempLocs.add(loc);
				}
			}
		}
		
		// Haha got it finally ...  fail-fast iterator -Xari
		for(Location loc : tempLocs) {
			try {
				TCEntityHandler.SpawnMobs(this, loc, mobSpawnpointMap.get(loc));
				mobSpawnpointMap.remove(loc);
			} catch (Exception e) {
				e.printStackTrace();
				mobSpawnpointMap.remove(loc);
			}
		}
	}

	/**
	 * Entity killer kills entity killed
	 *
	 * @param killed
	 * @param killer
	 */
	public void onEntityKilledByEntity(LivingEntity killed, Entity killer)
	{
		super.onEntityKilledByEntity(killed, killer);
		
		TCUtils.sendDeathMessage(this, killed, killer);
		
		if(killer instanceof Player) {
			if(mobGoldMap != null && mobGoldMap.containsKey(killed.getEntityId())) {
				for(Player p : playerSet) {
					int gold = mobGoldMap.get(killed.getEntityId())/playerSet.size();
					TempleManager.templePlayerMap.get(p).roundGold += gold;
					if(TempleCraft.economy != null) {
						TempleCraft.economy.depositPlayer(p.getName(), gold);
					}
				}
			}
		}
		if (!(killed instanceof Player)) {
			CustomMob cmob = mobManager.getMob(killed);
			// First implementation of Boss-Redstone impulse for testing.
			if (cmob != null && cmob.getSpawnProperties().isIsbossmob()){
				Block spawnBlock = world.getBlockAt(cmob.getSpawnProperties().getLocation());
				Block impulseBlock = spawnBlock.getRelative(0, -2, 0);
				Block baseBlock = spawnBlock.getRelative(0, -3, 0);
				if (impulseBlock.getType().equals(Material.AIR) && !baseBlock.getType().equals(Material.AIR)) {
					impulseBlock.setType(Material.REDSTONE_TORCH_ON);
				}
			}
		}
	}
}
