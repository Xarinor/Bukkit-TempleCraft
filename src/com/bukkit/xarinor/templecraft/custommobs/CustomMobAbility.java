package com.bukkit.xarinor.templecraft.custommobs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.bukkit.Effect;

import com.bukkit.xarinor.templecraft.TCUtils;
import com.bukkit.xarinor.templecraft.games.Game;
import com.bukkit.xarinor.templecraft.util.EnumUtils;

/**
* CustomMobAbility.java
* This work is dedicated to the public domain.
* 
* @author Xarinor
* @author bootscreen
* @author msingleton
* @author garbagemule
*/

public enum CustomMobAbility {
	ARROW("A") {
		public void run(Game game, Entity customMob) {
			Location loc = (customMob.getLocation());
			World world = loc.getWorld();
			Arrow a = ((LivingEntity) customMob).launchProjectile(Arrow.class);
			a.setShooter((LivingEntity) customMob);
			world.playEffect(loc, Effect.BOW_FIRE, 1);
		}
	},
	FIREAURA("FA") {
		public void run(Game game, Entity customMob) {
			for (Player p : getNearbyPlayers(game, customMob, 2)) {
				Location loc = (customMob.getLocation());
				World world = loc.getWorld();
				for ( int i=0; i<10; i++ ) {
					world.playEffect(loc, Effect.MOBSPAWNER_FLAMES, 1);
				}
				world.playEffect(loc, Effect.BLAZE_SHOOT, 1);
				p.setFireTicks(60);
			}
		}
	},
	FIREBALL("FB") {
		public void run(Game game, Entity customMob) {
			Entity target = TCUtils.getTarget(customMob);
			if (target == null) {
				return;
			}
			if(target instanceof Player) {
				Player p = (Player) target;
				if(game.deadSet.contains(p)) {
					return;
				}
				Location loc = (customMob.getLocation());
				World world = loc.getWorld();
				world.playEffect(loc, Effect.GHAST_SHOOT, 1);
				Fireball fb = ((LivingEntity) customMob).launchProjectile(Fireball.class);
				fb.setShooter((LivingEntity) customMob);
				fb.setDirection(target.getVelocity().add(target.getLocation().toVector().subtract(customMob.getLocation().toVector()).normalize().multiply(Integer.MAX_VALUE)));
				fb.setBounce(false);
			}
		}
	},
	TELEPORTTOPLAYER("TTP") {
		public void run(Game game, Entity customMob) {
			Player p = TCUtils.getNearbyRandomPlayer(customMob);
			if(p != null && !game.deadSet.contains(p)) {
				customMob.teleport(p);
				Location loc = customMob.getLocation();
				World world = loc.getWorld();
				for ( int i=0; i<10; i++ ) {
					world.playEffect(loc, Effect.ENDER_SIGNAL, 1);
				}
				world.playEffect(loc, Effect.GHAST_SHRIEK, 1);
			}
		}
	},
	THROWTARGET("TT") {	
		public void run(Game game, Entity customMob) {
			Entity target = TCUtils.getTarget(customMob);
			if (target != null && target instanceof Player) {
				Player p = (Player) target;
				if(!game.deadSet.contains(p)) {
					Location bLoc	= customMob.getLocation();
					Location loc	= target.getLocation();
					if(bLoc.distance(loc) < 8) {
						Vector v	= new Vector(loc.getX() - bLoc.getX(), 0, loc.getZ() - bLoc.getZ());
						target.setVelocity(v.normalize().setY(0.8));
						World world = loc.getWorld();
						world.playEffect(loc, Effect.ZOMBIE_CHEW_IRON_DOOR, 1);
					}
				}
			}
		}
	},
	LIVINGBOMB("LB") {
		public void run(Game game, Entity customMob) {
			Player p = TCUtils.getNearbyRandomPlayer(customMob);
			if (p != null && !game.deadSet.contains(p)) {
				int count = 4;
				while(count > 0) {
					if (game.deadSet.contains(p)) {
						return;
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) { }
					
					// Display the time counting down
					Location loc = (p.getEyeLocation());
					World world = loc.getWorld();
					world.playEffect(loc, Effect.CLICK1, 1);
					world.playEffect(loc, Effect.SMOKE, 1);
					p.setFireTicks(20);
					count--;
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) { }
				// Only visual explosion (-1 does no block damage)
				if (game.deadSet.contains(p)) {
					return;
				}
				p.getWorld().createExplosion(p.getLocation().getX(),p.getLocation().getY(),p.getLocation().getZ(),2,true,false);
				boolean spread = false;
				// Punish further players
				for (Player nearby : TCUtils.getNearbyPlayers(p,4)) {
					if (!game.deadSet.contains(nearby)) {
						nearby.setFireTicks(40);
						nearby.damage(6);
						spread = true;
						System.out.println("Mid range - "+nearby);
					}
				}
				// Punish near players hard
				for (Player nearby : TCUtils.getNearbyPlayers(p,2)) {
					if (!game.deadSet.contains(nearby)) {
						nearby.setFireTicks(120);
						nearby.damage(16);
						spread = true;
						System.out.println("Close range - "+nearby);
					}
				}
				// Little damage when nobody hit
				if (spread == false) {
					p.damage(2);
				}
			}
		}
	},
	IGNITE("I") {
		public void run(Game game, Entity customMob) {
			Player p = TCUtils.getNearbyRandomPlayer(customMob);
			if (p != null && !game.deadSet.contains(p)) {
					Location loc = (p.getLocation());
					World world = loc.getWorld();
					world.playEffect(loc, Effect.SMOKE, 1);
					p.setFireTicks(100);
			}
		}
	},
	FORCEWAVE("FW") {
		public void run(Game game, Entity customMob) {
			List<Player> players = TCUtils.getNearbyPlayers(customMob, 4);
			Location bLoc	= customMob.getLocation();
			for (Player p : players) {
				if(!game.deadSet.contains(p)) {
					Location loc	= p.getLocation();
					Vector v	= new Vector(loc.getX() - bLoc.getX(), 0, loc.getZ() - bLoc.getZ());
					p.setVelocity(v.normalize().setY(0.8));
					World world = loc.getWorld();
					world.playEffect(loc, Effect.ZOMBIE_CHEW_IRON_DOOR, 1);
				}
			}
		}
	},
	VORTEX("V") {
		public void run(Game game, Entity customMob) {
			List<Player> players = TCUtils.getNearbyPlayers(customMob, 12);
			Location bLoc	= customMob.getLocation();
			for (Player p : players) {
				if(!game.deadSet.contains(p)) {
					Location loc	= p.getLocation();
					Vector v = new Vector(bLoc.getX() - loc.getX(), 0, bLoc.getZ() - loc.getZ());
					double a = Math.abs(bLoc.getX() - loc.getX());
					double b = Math.abs(bLoc.getZ() - loc.getZ());
					double c = Math.sqrt((a*a + b*b));
	
					World world = loc.getWorld();
					world.playEffect(loc, Effect.ENDER_SIGNAL, 1);
	
					p.setVelocity(v.normalize().multiply(c*0.2).setY(0.8));
				}
			}
		}
	},
	SHUFFLEPOSITION("SP") {
		public void run(Game game, Entity customMob) {
			Location loc = (customMob.getLocation());
			World world = loc.getWorld();
			// Grab the players and add the boss
			List<LivingEntity> entities = new ArrayList<LivingEntity>();
			for (Player p: game.playerSet) {
				if (!game.deadSet.contains(p)) {
					entities.add(p);
				}
			}
			if (customMob instanceof LivingEntity) {
				entities.add((LivingEntity) customMob);
			}
			// Grab the locations
			List<Location> locations = new LinkedList<Location>();
			for (LivingEntity e : entities) {
				locations.add(e.getLocation());
			}
			// Shuffle the entities list.
			Collections.shuffle(entities);
			/* The entities are shuffled, but the locations are not, so if
			 * we remove the first element of each list, chances are they
			 * will not match, i.e. shuffle achieved! */
			while (!entities.isEmpty() && !locations.isEmpty()) {
				entities.remove(0).teleport(locations.remove(0));
				world.playEffect(loc, Effect.GHAST_SHRIEK, 1);
			}
		}
	},
	PULLTARGET("PT") {
		public void run(Game game, Entity customMob) {
			Entity target = TCUtils.getTarget(customMob);
			if (target != null && target instanceof Player) {
				Player p = (Player) target;
				if(!game.deadSet.contains(p)) {
					Location loc = target.getLocation();
					Location bLoc = customMob.getLocation();

					Vector v = new Vector(bLoc.getX() - loc.getX(), 0, bLoc.getZ() - loc.getZ());
					double a = Math.abs(bLoc.getX() - loc.getX());
					double b = Math.abs(bLoc.getZ() - loc.getZ());
					double c = Math.sqrt((a*a + b*b));

					World world = loc.getWorld();
					world.playEffect(loc, Effect.ENDER_SIGNAL, 1);

					target.setVelocity(v.normalize().multiply(c*0.2).setY(0.8));
				}
			}
		}
	},
	CHAINLIGHTNING("CL") {
		public void run(Game game, Entity customMob) {
			Player p = TCUtils.getNearbyRandomPlayer(customMob);
			if (p != null && !game.deadSet.contains(p)) {
				customMob.getLocation().getWorld().strikeLightning(p.getLocation());
				customMob.getLocation().getWorld().setThundering(true);
				for(Player player: TCUtils.getNearbyPlayers(p)) {
						customMob.getLocation().getWorld().strikeLightning(player.getLocation());
				}
			}
		}
	},
	DISORIENTTEARGET("DT") {
		public void run(Game game, Entity customMob) {
			Player p = TCUtils.getNearbyRandomPlayer(customMob);
			if (p != null && !game.deadSet.contains(p)) {
				PotionEffect confusion = new PotionEffect(PotionEffectType.CONFUSION, 210, 1);
				PotionEffect blindness = new PotionEffect(PotionEffectType.BLINDNESS, 150, 1);
				ArrayList<PotionEffect> effects = new ArrayList<PotionEffect>(Arrays.asList(confusion, blindness));
				p.addPotionEffects(effects);
				Location loc = (customMob.getLocation());
				World world = loc.getWorld();
				world.playEffect(loc, Effect.POTION_BREAK, 1);
			}
		}
	},
	FLAMING("F") {
		public void run(Game game, Entity customMob) {
//			PotionEffect potion = new PotionEffect(PotionEffectType.SPEED 200, 1);
//			p.addPotionEffect(potion, true);
			PotionEffect fireprot = new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 600, 1);
			PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, 450, 1);
			ArrayList<PotionEffect> effects = new ArrayList<PotionEffect>(Arrays.asList(fireprot, speed));
			((LivingEntity) customMob).addPotionEffects(effects);
			customMob.setFireTicks(580);
			Location loc = (customMob.getLocation());
			World world = loc.getWorld();
			world.playEffect(loc, Effect.SMOKE, 1);
			world.playEffect(loc, Effect.BLAZE_SHOOT, 1);
		}
	};

	private String name;

	/**
	 * Constructor
	 * 
	 * @param name -Ability name
	 */
	private CustomMobAbility(String name) {
		this.name = name;
	}

	/**
	 * Get the ability with its shortname
	 * 
	 * @param shortname -Shortname of the ability
	 * @return
	 */
	public static CustomMobAbility fromShortName(String shortname) {    	
		if(shortname.equalsIgnoreCase(CustomMobAbility.ARROW.name)) {
			return CustomMobAbility.ARROW;
		}
		if(shortname.equalsIgnoreCase(CustomMobAbility.FIREAURA.name)) {
			return CustomMobAbility.FIREAURA;
		}
		if(shortname.equalsIgnoreCase(CustomMobAbility.FIREBALL.name)) {
			return CustomMobAbility.FIREBALL;
		}
		if(shortname.equalsIgnoreCase(CustomMobAbility.TELEPORTTOPLAYER.name)) {
			return CustomMobAbility.TELEPORTTOPLAYER;
		}
		if(shortname.equalsIgnoreCase(CustomMobAbility.THROWTARGET.name)) {
			return CustomMobAbility.THROWTARGET;
		}
		if(shortname.equalsIgnoreCase(CustomMobAbility.LIVINGBOMB.name)) {
			return CustomMobAbility.LIVINGBOMB;
		}
		if(shortname.equalsIgnoreCase(CustomMobAbility.IGNITE.name)) {
			return CustomMobAbility.IGNITE;
		}
		if(shortname.equalsIgnoreCase(CustomMobAbility.FORCEWAVE.name)) {
			return CustomMobAbility.FORCEWAVE;
		}
		if(shortname.equalsIgnoreCase(CustomMobAbility.VORTEX.name)) {
			return CustomMobAbility.VORTEX;
		}
		if(shortname.equalsIgnoreCase(CustomMobAbility.SHUFFLEPOSITION.name)) {
			return CustomMobAbility.SHUFFLEPOSITION;
		}
		if(shortname.equalsIgnoreCase(CustomMobAbility.PULLTARGET.name)) {
			return CustomMobAbility.PULLTARGET;
		}
		if(shortname.equalsIgnoreCase(CustomMobAbility.CHAINLIGHTNING.name)) {
			return CustomMobAbility.CHAINLIGHTNING;
		}
		if(shortname.equalsIgnoreCase(CustomMobAbility.DISORIENTTEARGET.name)) {
			return CustomMobAbility.DISORIENTTEARGET;
		}
		if(shortname.equalsIgnoreCase(CustomMobAbility.FLAMING.name)) {
			return CustomMobAbility.FLAMING;
		}
		System.out.print("[TempleCraft] No Ability with this Shortname (" + shortname + ") found.");
		return null;
	}

	/**
	 * The run-method that all boss abilities must define.
	 * The method is called in the ability cycle for the given boss.
	 * 
	 * @param game		-The game the boss is in
	 * @param customMob	-The boss entity
	 */
	public abstract void run(Game game, Entity customMob);
	
	/**
	 * Get a list of nearby players
	 * 
	 * @param arena	-The arena
	 * @param mob	-The Mob
	 * @param x		-The 'radius' in which to grab players
	 * @return		-A list of nearby players
	 */
	protected List<Player> getNearbyPlayers(Game game, Entity mob, int x) {
		List<Player> result = new LinkedList<Player>();
		for (Entity e : mob.getNearbyEntities(x, x, x)) {
			if (game.playerSet.contains(e)) {
				result.add((Player) e);
			}
		}
		return result;
	}
	
	/**
	 *Gets the ability from a string
	 * 
	 * @param string -Ability string
	 * @return
	 */
	public static CustomMobAbility fromString(String string) {
		return EnumUtils.getEnumFromString(CustomMobAbility.class, string.replaceAll("[-_\\. ]", ""));
	}

	/**
	 * This to string
	 */
	public String toString() {
		return name;
	}
}