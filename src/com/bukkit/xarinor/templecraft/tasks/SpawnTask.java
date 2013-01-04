package com.bukkit.xarinor.templecraft.tasks;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.inventory.ItemStack;

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
	public int mode;

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
		this.mode = msp.getMode();
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
				
				if((e instanceof Slime || e instanceof MagmaCube) && mode > 0) {
					Slime slime = (Slime) e;
					slime.setSize(mode);
					cmob.setMode(mode);
				} if(e instanceof IronGolem) {
					IronGolem golem = (IronGolem) e;
					golem.setPlayerCreated(false);
				} if(e instanceof Skeleton) {
					Skeleton skeleton = (Skeleton) e;
					if (msp.getMode() == 1) {
						skeleton.setSkeletonType(SkeletonType.WITHER);
						((Skeleton) e).getEquipment().setItemInHand(new ItemStack(Material.STONE_SWORD,1));
					} else {
						skeleton.setSkeletonType(SkeletonType.NORMAL);
						((Skeleton) e).getEquipment().setItemInHand(new ItemStack(Material.BOW,1));
					}
				} if(e instanceof PigZombie) {
					((PigZombie) e).getEquipment().setItemInHand(new ItemStack(Material.GOLD_SWORD,1));
				} if (e instanceof Zombie){
					Zombie zombie = (Zombie) e;
					if (msp.getMode() == 1) {
						zombie.setBaby(true);
					} else if (msp.getMode() == 2) {
						zombie.setVillager(true);
					} else if (msp.getMode() == 3) {
						zombie.setBaby(true);
						zombie.setVillager(true);
					}
				} if(e instanceof Wolf) {
					Wolf wolf = (Wolf) e;
					wolf.setAngry(true);
					if (msp.getMode() == 1) {
						wolf.setBaby();
					}
				} if(e instanceof Cow) {
					Cow cow = (Cow) e;
					if (msp.getMode() == 1) {
						cow.setBaby();
					}
				} if(e instanceof Chicken) {
					Chicken chicken = (Chicken) e;
					if (msp.getMode() == 1) {
						chicken.setBaby();
					}
				} if(e instanceof Pig) {
					Pig pig = (Pig) e;
					if (msp.getMode() == 1) {
						pig.setBaby();
					}
				} if(e instanceof Sheep) {
					Sheep sheep = (Sheep) e;
					if (msp.getMode() == 1) {
						sheep.setBaby();
					}
				}
				
				if(health > 0) {
					cmob.setHealth(health);
					game.mobManager.AddMob(cmob);
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
