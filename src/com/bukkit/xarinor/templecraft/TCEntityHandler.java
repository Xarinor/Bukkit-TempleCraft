package com.bukkit.xarinor.templecraft;


import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;

import com.bukkit.xarinor.templecraft.custommobs.CustomMob;
import com.bukkit.xarinor.templecraft.games.Game;
import com.bukkit.xarinor.templecraft.tasks.AbilityTask;
import com.bukkit.xarinor.templecraft.tasks.SpawnTask;
import com.bukkit.xarinor.templecraft.util.MobSpawnProperties;
/**
* TCEntityHandler.java
* This work is dedicated to the public domain.
* 
* @author Xarinor
* @author bootscreen
* @author msingleton
*/

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

			//Improve
			SpawnBoss(game, loc, msp.getEntityType(), msp, msp.getHealth(), msp.getDMGMulti(), msp.getAbilitys());
				
		} else {
			if(msp.getCount() == 1 && msp.getTime() == 0) {
				try {
					Entity e = game.world.spawnEntity(loc,msp.getEntityType());

					if(e == null) {
						return;
					}

					Random r = new Random();
					if(TempleCraft.economy != null && (TempleManager.mobGoldMin + TempleManager.mobGoldRan) != 0 && r.nextInt(3) == 0) {
						game.mobGoldMap.put(e.getEntityId(), r.nextInt(TempleManager.mobGoldRan)+TempleManager.mobGoldMin);
					}

					CustomMob cmob = new CustomMob(e);
					cmob.setSpawnProperties(msp);
					
					if((e instanceof Slime || e instanceof MagmaCube) && msp.getMode() > 0) {
						Slime slime = (Slime) e;
						slime.setSize(msp.getMode());
						cmob.setMode(msp.getMode());
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
					int health = msp.getHealth();
					if(health <= 0 && e instanceof LivingEntity) {
						LivingEntity livingEntity = (LivingEntity)e;
						health = livingEntity.getMaxHealth();
					}
					cmob.setHealth(health);
					game.mobManager.AddMob(cmob);
					
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
				@SuppressWarnings("deprecation")
				int id = TempleCraft.TCScheduler.scheduleAsyncRepeatingTask(TempleCraft.TCPlugin, new SpawnTask(game, loc, msp, TempleCraft.TCPlugin), 0L, msp.getTime());
				SpawnTask.taskID = id;
				game.SpawnTaskIDs.add(id);
			}
		}
	}

	public static void SpawnBoss(Game game, Location loc, EntityType mob, MobSpawnProperties msp, int health, int dmgmulti, String abilitys) {
		try {
			Entity e = game.world.spawnEntity(loc,mob);

			if(e == null) {
				return;
			}

			Random r = new Random();
			if(TempleCraft.economy != null && (TempleManager.mobGoldMin + TempleManager.mobGoldRan) != 0 && r.nextInt(3) == 0) {
				// More Gold for a boss mob.. mimimi
				game.mobGoldMap.put(e.getEntityId(), r.nextInt((TempleManager.mobGoldRan)+TempleManager.mobGoldMin)*3);
			}

			CustomMob cmob = new CustomMob(e);
			cmob.setSpawnProperties(msp);

			if((e instanceof Slime || e instanceof MagmaCube) && msp.getMode() > 0) {
				Slime slime = (Slime) e;
				slime.setSize(msp.getMode());
				cmob.setMode(msp.getMode());
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

			if(health <= 0 && e instanceof LivingEntity) {
				LivingEntity livingEntity = (LivingEntity)e;
				health = livingEntity.getMaxHealth();
			}
			cmob.setHealth(health);
			cmob.setDMGMultiplikator(dmgmulti);

			if(abilitys.length() > 0) {
				cmob.addAbilitysfromString(abilitys);
			}

			game.mobManager.AddMob(cmob);

			if(!cmob.getAbilitys().isEmpty()) {
				@SuppressWarnings("deprecation")
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