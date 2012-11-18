package com.bukkit.xarinor.templecraft.listeners;

//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Set;

import org.bukkit.Location;
//import org.bukkit.Material;
//import org.bukkit.block.Block;
//import org.bukkit.entity.Creature;
//import org.bukkit.entity.Entity;
//import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
//import org.bukkit.inventory.ItemStack;

import com.bukkit.xarinor.templecraft.TCUtils;
import com.bukkit.xarinor.templecraft.TempleCraft;
import com.bukkit.xarinor.templecraft.TempleManager;
//import com.bukkit.xarinor.templecraft.games.Adventure;
import com.bukkit.xarinor.templecraft.games.Arena;
import com.bukkit.xarinor.templecraft.games.Game;
//import com.bukkit.xarinor.templecraft.util.Pair;

/**
* TCEntityListener.java
* This work is dedicated to the public domain.
* 
* Prevents Creeper explosions from damaging the blocks of the
 * arena, zombies and skeletons from burning in the sun, and
 * monsters (mostly spiders) from losing their targets.
* 
* @author Xarinor
* @author bootscreen
* @author msingleton
*/
public class TCEntityListener implements Listener {
	
	/**
	 * Constructor
	 */
	public TCEntityListener(TempleCraft instance) { }

	/**
	 * Handles all explosion events.
	 * Does nothing when the event is not in a temple.
	 * 
	 * @param event -Entity explode event
	 */
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		
		if(!TCUtils.isTCWorld(event.getLocation().getWorld())) {
			return;
		}
		Game game = TCUtils.getGameByWorld(event.getLocation().getWorld());
		if (game == null) {
			return;
		}
		//TODO Better config
		if(TempleManager.dropBlocks) {
			return;
		}
		// Removes all blocks from the list of destroyed objects.
		event.blockList().clear();
	}

	/**
	 * Zombie/skeleton combustion from the sun.
	 * 
	 * @param event -Combust event
	 */
	@EventHandler
	public void onEntityCombust(EntityCombustEvent event) {
		Game game = TCUtils.getGame(event.getEntity());
		if(game == null) {
			return;
		}
		if (game.monsterSet.contains(event.getEntity())) {
			event.setCancelled(true);
		}
	}

	/**
	 * Monsters losing their targets.
	 * 
	 * @param event -Entity target/untarget event
	 */
	@EventHandler
	public void onEntityTarget(EntityTargetEvent event) {
		Game game = TCUtils.getGame(event.getEntity());
		if(game == null) {
			return;
		}
		if (!game.isRunning) {
			return;
		}
		if (!game.monsterSet.contains(event.getEntity())) {
			return;
		}
		if (event.getReason() == TargetReason.FORGOT_TARGET) {
			event.setTarget(TCUtils.getClosestPlayer(game, event.getEntity()));
		}
		if (event.getReason() == TargetReason.TARGET_DIED) {
			event.setTarget(TCUtils.getClosestPlayer(game, event.getEntity()));
		}
		if (event.getReason() == TargetReason.CLOSEST_PLAYER) {
			event.setTarget(TCUtils.getClosestPlayer(game, event.getEntity()));
		}
	}

	/**
	 * Prevents monsters from spawning inside a temple unless
	 * it's running.
	 * 
	 * @param event -Creature spawn event
	 */
	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {		
		
		Location loc = event.getLocation();
		if(loc != null) {
			// When in TCWorld, Only Spawn Custom Monsters
			if(event.getEntity() instanceof LivingEntity && event.getEntity() != null) {
				LivingEntity e = (LivingEntity) event.getEntity();
				if(TCUtils.isTCWorld(loc.getWorld())) {
					if(event.getSpawnReason().equals(SpawnReason.CUSTOM) ||
							event.getSpawnReason().equals(SpawnReason.SPAWNER_EGG) ||
							event.getSpawnReason().equals(SpawnReason.SLIME_SPLIT) ||
							event.getSpawnReason().equals(SpawnReason.BUILD_IRONGOLEM) ) {
						Game game = TCUtils.getGameByWorld(loc.getWorld());
						if(game != null) {
							game.monsterSet.add(e);
							if(game instanceof Arena) {
								e.setHealth((int) ((Arena)game).getZombieHealth());
							}
						}
					} else {
						event.setCancelled(true);
					}
				}
			}
		}
	}
}