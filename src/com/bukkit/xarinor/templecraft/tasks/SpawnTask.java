package com.bukkit.xarinor.templecraft.tasks;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
//import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Slime;

import com.bukkit.xarinor.templecraft.TCUtils;
import com.bukkit.xarinor.templecraft.TempleCraft;
import com.bukkit.xarinor.templecraft.TempleManager;
import com.bukkit.xarinor.templecraft.custommobs.CustomMob;
import com.bukkit.xarinor.templecraft.games.Game;
import com.bukkit.xarinor.templecraft.util.MobSpawnProperties;

/**
 * SpawnTask.java
 * This work is dedicated to the public domain.
 * 
 * @author Xarinor
 * @author bootscreen
 * @author msingleton
 */
public class SpawnTask implements Runnable {
	public TempleCraft plugin;
	public EntityType mob;
	public MobSpawnProperties msp;
	public int health;
	public int count;
	public Game game;
	public Location loc;
	public int size;

	/**
	 * Constructor
	 * 
	 * @param game
	 * @param loc
	 * @param msp
	 * @param plugin
	 */
	public SpawnTask(Game game, Location loc, MobSpawnProperties msp, TempleCraft plugin){
		this.plugin = plugin;   
		this.mob = msp.getEntityType();
		this.game = game;
		this.msp = msp;
		this.loc = loc;
		this.health = msp.getHealth();
		this.count = msp.getCount();
		this.size = msp.getSize();
	}

	public static int taskID;

	/**
	 * Execute spawn task
	 */
	@Override
	public void run() {
		try {
			//System.out.print(taskID + ":" + count);
			count--;

			if(count <= 0  || !game.isRunning || game.isEnding || TempleManager.server.getWorld(loc.getWorld().getUID()) == null) {
				game.SpawnTaskIDs.remove(taskID);
				TempleCraft.TCScheduler.cancelTask(taskID);
			} else {
				Entity e = game.world.spawnEntity(loc,mob);
				
				if(e == null) {
					return;
				}
				
				Random r = new Random();
				if(TempleCraft.economy != null && (TempleManager.mobGoldMin + TempleManager.mobGoldRan) != 0 && r.nextInt(3) == 0) {
					game.mobGoldMap.put(e.getEntityId(), r.nextInt(TempleManager.mobGoldRan)+TempleManager.mobGoldMin);
				}
				
				CustomMob cmob = new CustomMob(e);

				cmob.setSpawnProperties(msp);
				
				if(e instanceof Slime && size > 0) {
					Slime slime = (Slime) e;
					slime.setSize(size);
					cmob.setSize(size);
				}
				
				if(health > 0) {
					cmob.setHealth(health);
					game.customMobManager.AddMob(cmob);
				}
				
				if(!(e instanceof Creature)) {
					return;
				}
				
				// Grab a random target.
				Creature c = (Creature) e;
				c.setTarget(TCUtils.getClosestPlayer(game, e));
			}
		} catch(Exception e) {
			System.out.println("[TempleCraft] Could not spawn "+mob.getName());
		}
	}
}   
