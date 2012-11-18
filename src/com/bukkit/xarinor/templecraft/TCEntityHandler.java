package com.bukkit.xarinor.templecraft;


import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
//import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Wolf;

import com.bukkit.xarinor.templecraft.custommobs.CustomMob;
import com.bukkit.xarinor.templecraft.custommobs.CustomMobAbility;
import com.bukkit.xarinor.templecraft.games.Game;
import com.bukkit.xarinor.templecraft.tasks.AbilityTask;
import com.bukkit.xarinor.templecraft.tasks.SpawnTask;
import com.bukkit.xarinor.templecraft.util.MobSpawnProperties;
import com.bukkit.xarinor.templecraft.util.Pair;
/**
* TCEntityHandler.java
* This work is dedicated to the public domain.
* 
* @author Xarinor
* @author bootscreen
* @author msingleton
*/

//TODO Living Entities!!
public class TCEntityHandler {
	
	/**
	 * Spawn Mob with entity type
	 * 
	 * @param game
	 * @param loc
	 * @param mob
	 */
	public static void SpawnMobs(Game game, Location loc, EntityType mob) {
		MobSpawnProperties msp = new MobSpawnProperties();
		msp.setEntityType(mob);
		msp.setLocation(loc);
		SpawnMobs(game, loc, msp);
	}

	/**
	 * Spawn mob with spawn properties
	 * 
	 * @param game
	 * @param loc
	 * @param msp
	 */
	public static void SpawnMobs(Game game, Location loc, MobSpawnProperties msp) {
		if(msp.isIsbossmob()) {
			if(msp.getAbilities_random().size() > 0 || msp.getAbilities_rotation().size() > 0) {
				SpawnMobs(game, loc, msp.getEntityType(), msp, msp.getSize(), msp.getHealth(), msp.getDMGMulti(), msp.getAbilities_random(), msp.getAbilities_rotation());
			} else {
				SpawnMobs(game, loc, msp.getEntityType(), msp, msp.getSize(), msp.getHealth(), msp.getDMGMulti(), msp.getAbilitys());
			}
		} else {
			if(msp.getCount() == 1 && msp.getTime() == 0) {
				try {
					//LivingEntity e = game.world.spawnCreature(loc,msp.getEntityType());
					Entity e = game.world.spawnEntity(loc,msp.getEntityType());

					if(e == null) {
						return;
					}

					Random r = new Random();
					if(TempleCraft.economy != null && (TempleManager.mobGoldMin + TempleManager.mobGoldRan) != 0 && r.nextInt(3) == 0) {
						game.mobGoldMap.put(e.getEntityId(), r.nextInt(TempleManager.mobGoldRan)+TempleManager.mobGoldMin);
					}

					//TODO Living
					CustomMob cmob = new CustomMob(e);
					cmob.setSpawnProperties(msp);
					//TODO MagmaSlime ok with this? Lagg?
					if(e instanceof Slime && msp.getSize() > 0) {
						Slime slime = (Slime) e;
						slime.setSize(msp.getSize());
						cmob.setSize(msp.getSize());
					} if(e instanceof Wolf) {
						Wolf wolf = (Wolf) e;
						//TODO Still not aggressive?
						wolf.setAngry(true);
					}

					if(msp.getHealth() > 0) {
						cmob.setHealth(msp.getHealth());
					}
					
					game.customMobManager.AddMob(cmob);
					
					if(!(e instanceof Creature)) {
						return;
					}
					// Grab a random target.
					Creature c = (Creature) e;
					c.setTarget(TCUtils.getClosestPlayer(game, e));
				} catch(Exception e) {
					System.out.println("[TempleCraft] Could not spawn "+msp.getEntityType().getName());
				}
			} else {
				int id = TempleCraft.TCScheduler.scheduleAsyncRepeatingTask(TempleCraft.TCPlugin, new SpawnTask(game, loc, msp, TempleCraft.TCPlugin), 0L, msp.getTime());
				SpawnTask.taskID = id;
				game.SpawnTaskIDs.add(id);
			}
		}
	}

	public static void SpawnMobs(Game game, Location loc, EntityType mob, MobSpawnProperties msp, int size, int health, int dmgmulti, String abilitys) {
		try {
			//LivingEntity e = game.world.spawnCreature(loc,mob);
			Entity e = game.world.spawnEntity(loc,mob);

			if(e == null) {
				return;
			}

			Random r = new Random();
			if(TempleCraft.economy != null && (TempleManager.mobGoldMin + TempleManager.mobGoldRan) != 0 && r.nextInt(3) == 0) {
				game.mobGoldMap.put(e.getEntityId(), r.nextInt(TempleManager.mobGoldRan)+TempleManager.mobGoldMin);
			}

			//TODO living
			CustomMob cmob = new CustomMob(e);

			cmob.setSpawnProperties(msp);

			if(e instanceof Slime && size > 0) {
				Slime slime = (Slime) e;
				slime.setSize(size);
				cmob.setSize(size);
			} if(e instanceof Wolf) {
				Wolf wolf = (Wolf) e;
				wolf.setAngry(true);
			}

			if(health <= 0) {
				health = cmob.getMaxHealth();
			}
			cmob.setHealth(health);
			cmob.setDMGMultiplikator(dmgmulti);

			if(abilitys.length() > 0) {
				cmob.addAbilitysfromString(abilitys);
			}

			game.customMobManager.AddMob(cmob);

			if(!cmob.getAbilitys().isEmpty()) {
				int id = TempleCraft.TCScheduler.scheduleAsyncRepeatingTask(TempleCraft.TCPlugin, new AbilityTask(game, cmob), 100L, 100L);
				AbilityTask.taskID = id;
				game.AbilityTaskIDs.put(cmob,id);
			}

			if(!(e instanceof Creature)) {
				return;
			}
			// Grab a random target.
			Creature c = (Creature) e;
			c.setTarget(TCUtils.getClosestPlayer(game, e));
		} catch(Exception e) {
			System.out.println("[TempleCraft] Could not spawn "+mob.getName());
		}
	}

	/**
	 * Spawn mob BOSS
	 * 
	 * @param game
	 * @param loc
	 * @param mob
	 * @param msp
	 * @param size
	 * @param health
	 * @param dmgmulti
	 * @param abilities_random
	 * @param abilities_rotation
	 */
	public static void SpawnMobs(Game game, Location loc, EntityType mob, MobSpawnProperties msp, int size, int health, int dmgmulti, List<Pair<CustomMobAbility, Integer>> abilities_random, List<Pair<CustomMobAbility, Integer>> abilities_rotation) 
	{
		try
		{
			//LivingEntity e = game.world.spawnCreature(loc,mob);
			Entity e = game.world.spawnEntity(loc,mob);

			if(e == null)
				return;

			Random r = new Random();
			if(TempleCraft.economy != null && (TempleManager.mobGoldMin + TempleManager.mobGoldRan) != 0 && r.nextInt(3) == 0) {
				game.mobGoldMap.put(e.getEntityId(), r.nextInt(TempleManager.mobGoldRan)+TempleManager.mobGoldMin);
			}

			//TODO living
			CustomMob cmob = new CustomMob(e);
			cmob.setSpawnProperties(msp);

			if(e instanceof Slime && size > 0) {
				Slime slime = (Slime) e;
				slime.setSize(size);
				cmob.setSize(size);
			}
			if(e instanceof Wolf) {
				Wolf wolf = (Wolf) e;
				//TODO Still passive?
				wolf.setAngry(true);
			}

			if(health <= 0) {
				health = cmob.getMaxHealth();
			}
			cmob.setHealth(health);
			cmob.setDMGMultiplikator(dmgmulti);
			game.customMobManager.AddMob(cmob);

			if(!(e instanceof Creature)) {
				return;
			}
			// Grab a random target.
			Creature c = (Creature) e;
			c.setTarget(TCUtils.getClosestPlayer(game, e));
		} catch(Exception e) {
			System.out.println("[TempleCraft] Could not spawn "+mob.getName());
		}
	}

	/**
	 * Pick a random creature
	 * 
	 * TODO Add new creatures maybe
	 * 
	 * @return
	 */
	public static EntityType getRandomCreature() {
		int dZombies, dSkeletons, dSpiders, dCreepers, dWolves, dCaveSpiders;
		dZombies = 5;
		dSkeletons = dZombies + 5;
		dSpiders = dSkeletons + 5;
		dCreepers = dSpiders + 5;
		dWolves = dCreepers + 5;
		dCaveSpiders = dWolves + 5;

		EntityType mob;

		int ran = new Random().nextInt(dCaveSpiders);
		if	  (ran < dZombies)	 mob = EntityType.ZOMBIE;
		else if (ran < dSkeletons)   mob = EntityType.SKELETON;
		else if (ran < dSpiders)	 mob = EntityType.SPIDER;
		else if (ran < dCreepers)	mob = EntityType.CREEPER;
		else if (ran < dWolves)	  mob = EntityType.WOLF;
		else if (ran < dCaveSpiders) mob = EntityType.CAVE_SPIDER;
		else return null;

		return mob;
	}
}