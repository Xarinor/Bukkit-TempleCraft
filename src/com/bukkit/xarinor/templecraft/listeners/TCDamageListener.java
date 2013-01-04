package com.bukkit.xarinor.templecraft.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import com.bukkit.xarinor.templecraft.TCUtils;
import com.bukkit.xarinor.templecraft.TempleCraft;
import com.bukkit.xarinor.templecraft.TempleManager;
import com.bukkit.xarinor.templecraft.TemplePlayer;
import com.bukkit.xarinor.templecraft.custommobs.CustomMob;
import com.bukkit.xarinor.templecraft.custommobs.EntityEffectHandler;
import com.bukkit.xarinor.templecraft.games.Arena;
import com.bukkit.xarinor.templecraft.games.Game;

/**
 * TCDamageListener.java
 * This work is dedicated to the public domain.
 * 
 *  This listener acts as a type of death-listener.
 * When a player is sufficiently low on health, and the next
 * damaging blow will kill them, they are teleported to the
 * spectator area, they have their hearts replenished, and all
 * their items are stripped from them.
 * By the end of the arena session, the rewards are given.
 * 
 * TO-DO: Perhaps implement TeamFluff's respawn-packet-code.
 * 
 * @author Xarinor
 * @author bootscreen
 * @author msingleton
 */
public class TCDamageListener implements Listener {	
	
	/**
	 * Handles damage done in temples
	 * 
	 * @param event -Damage event
	 */
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {

		if (!TCUtils.isTCWorld(event.getEntity().getWorld())) {
			return;
		}

		if (event.getEntity() instanceof Player) {
			Player p = (Player)event.getEntity();
			Game game = TempleManager.templePlayerMap.get(p).currentGame;
			// If the player is dead or the game isn't running, the player can't take damage
			if(game != null && (game.deadSet.contains(p) || !game.isRunning)) {
				p.setFireTicks(0);
				EntityEffectHandler.removeAllPotionEffects(p);
				event.setCancelled(true);
				return;
			}

			if(game instanceof Arena) {
				// Increases the damage mobs do over time for Arena mode
				event.setDamage((int) (event.getDamage()*((Arena)game).getDamageMultiplyer()));
			}
		}

		if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent sub = (EntityDamageByEntityEvent)event;

			Entity entity = sub.getDamager();
			Entity entity2 = sub.getEntity();

			int id = entity2.getEntityId();

			if(entity instanceof Projectile) {
				entity = ((Projectile) entity).getShooter();
			}

			if(entity instanceof Player && entity2 instanceof Player) {
				TemplePlayer tp1 = TempleManager.templePlayerMap.get((Player)entity);
				TemplePlayer tp2 = TempleManager.templePlayerMap.get((Player)entity2);
				// Players on the same team can't hurt each other
				if(tp1.team != -1 && tp1.team == tp2.team) {
					event.setCancelled(true);
					return;
				}
			}

			if(entity instanceof LivingEntity) {
				Game game = TCUtils.getGame(entity);
				if(game != null) {
					CustomMob cmob = game.mobManager.getMob(entity);

					if(cmob != null && !cmob.isDead()) {
						if(cmob.getDMGMultiplikator() > 1) {
							event.setDamage(event.getDamage() * cmob.getDMGMultiplikator());
						}
					}
				}
			}

			if(entity2 instanceof LivingEntity && ((LivingEntity)entity2).getHealth() > 0) {
				Game game = TCUtils.getGame(entity);
				if(game == null) {
					game = TCUtils.getGame(entity2);
				}
				if(game == null) {
					return;
				}
				game.lastDamager.remove(id);
				game.lastDamager.put(id, entity);

				CustomMob cmob = game.mobManager.getMob(entity2);

				if(cmob != null && !cmob.isDead()) {
					cmob.damage(event.getDamage(), entity);
					event.setDamage(0);
				}
			}
		}
	}

	/**
	 * Clears all player/monster drops on death.
	 * 
	 * @param event -Dead event
	 */
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {		
		
		if (event == null) {return;}
		if (!TCUtils.isTCWorld(event.getEntity().getWorld())) {
			return;
		}

		// If a living entity dies in a TempleWorld, clear drops and such
		if (event.getEntity() instanceof LivingEntity) {
			LivingEntity e = (LivingEntity) event.getEntity();
			if(e.getKiller() != null) {
				TCUtils.debugMessage("Entity " + e.getType().getName() + "dies by \"" + e.getKiller().getName() + "\"");
			} else {
				TCUtils.debugMessage("Entity " + e.getType().getName() + "dies");
			}
			event.getDrops().clear();

			Game game;
			Entity lastDamager;
			if (e instanceof Player) {		
				Player p = (Player) e;

				if (!TempleManager.playerSet.contains(p)) {
					return;
				}

				TemplePlayer tp = TempleManager.templePlayerMap.get(p);
				game = tp.currentGame;

				lastDamager = game.lastDamager.remove(event.getEntity().getEntityId());
				game.playerDeath(p);
			} else {
				try {
					// If a monster died
					game = TCUtils.getGame(e);
					lastDamager = game.lastDamager.remove(e.getEntityId()); //-Tim
					CustomMob cmob = game.mobManager.getMob(event.getEntity());
	
					if(cmob != null && !cmob.isDead()) {
												
						if (game.AbilityTaskIDs.containsKey(cmob)) {
							TempleCraft.TCScheduler.cancelTask(game.AbilityTaskIDs.get(cmob));
						}
						game.mobSpawnpointMap.put(cmob.getSpawnProperties().getLocation(), cmob.getSpawnProperties());
						cmob.remove();
					}
				} catch (Exception ex) {
					lastDamager = null;
					game = null;
				}
			}
			if(game != null && lastDamager != null) {
				game.onEntityKilledByEntity(e,lastDamager);
			}
		}
	}
}