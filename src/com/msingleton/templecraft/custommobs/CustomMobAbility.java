package com.msingleton.templecraft.custommobs;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;
//import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
//import org.bukkit.entity.Fireball;
//import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.msingleton.templecraft.TCUtils;
import com.msingleton.templecraft.games.Game;
import com.msingleton.templecraft.util.EnumUtils;

public enum CustomMobAbility 
{
	ARROW("A")
	{
		public void run(Game game, Entity customMob)
		{
			//customMob.launchProjectile(Arrow.class);
		}
	},
	FIREAURA("FA")
	{
		public void run(Game game, Entity customMob)
		{
			for (Player p : getNearbyPlayers(game, customMob, 5))
			{
				p.setFireTicks(100);
			}
		}
	},
	FIREBALL("FB")
	{
		public void run(Game game, Entity customMob)
		{
			Entity target = TCUtils.getTarget(customMob);

			while(target == null || (target != null && target.equals(customMob)))
			{
				target = TCUtils.getTarget(customMob);
			}
			
			if(target != null && !target.equals(customMob))
			{
				//customMob.launchProjectile(Fireball.class);
				
				/*
				Fireball fb = customMob.launchProjectile(Fireball.class);
				fb.setBounce(false);
				fb.setYield(2);
				fb.setShooter(customMob);
				fb.setDirection(target.getVelocity().add(target.getLocation().toVector().subtract(customMob.getLocation().toVector()).normalize().multiply(Integer.MAX_VALUE)));
				*/
			}
		}
	},
	TELEPORTTOPLAYER("TTP")
	{
		public void run(Game game, Entity customMob)
		{
			Player p = TCUtils.getNearbyRandomPlayer(customMob);
			//Player p = TCUtils.getRandomPlayer(game);
			if(p != null)
			{
	            while(game.deadSet.contains(p))
	            {
	            	p = TCUtils.getRandomPlayer(game);
	            }
				customMob.teleport(p);
			}
		}
	},
	THROWTARGET("TT")
    {
        public void run(Game game, Entity customMob)
        {
            Entity target = TCUtils.getTarget(customMob);
            if (target == null) 
            {
            	return;
            }
            
            if(target instanceof Player)
            {
            	Player p = (Player) target;
            	if(game.deadSet.contains(p))
            	{
            		target = TCUtils.getRandomPlayer(game);
            	}
            }
            
            Location bLoc       = customMob.getLocation();
            Location loc        = target.getLocation();
            if(bLoc.distance(loc) < 10)
            {
	            Vector v            = new Vector(loc.getX() - bLoc.getX(), 0, loc.getZ() - bLoc.getZ());
	            target.setVelocity(v.normalize().setY(0.8));
            }
        }
    };

	private String name;

	private CustomMobAbility(String name)
	{
		this.name = name;
	}

	public static CustomMobAbility fromShortName(String shortname)
	{    	
		if(shortname.equalsIgnoreCase(CustomMobAbility.ARROW.name))
		{
			return CustomMobAbility.ARROW;
		}
		if(shortname.equalsIgnoreCase(CustomMobAbility.FIREAURA.name))
		{
			return CustomMobAbility.FIREAURA;
		}
		if(shortname.equalsIgnoreCase(CustomMobAbility.FIREBALL.name))
		{
			return CustomMobAbility.FIREBALL;
		}
		if(shortname.equalsIgnoreCase(CustomMobAbility.TELEPORTTOPLAYER.name))
		{
			return CustomMobAbility.TELEPORTTOPLAYER;
		}
		if(shortname.equalsIgnoreCase(CustomMobAbility.THROWTARGET.name))
		{
			return CustomMobAbility.THROWTARGET;
		}

		System.out.print("[TempleCraft] No Ability with this Shortname (" + shortname + ") found.");
		return null;
	}

	/**
	 * The run-method that all boss abilities must define.
	 * The method is called in the ability cycle for the given boss.
	 * @param game The game the boss is in
	 * @param boss The boss entity
	 */
	public abstract void run(Game game, Entity customMob);

	/**
	 * Get a list of nearby players
	 * @param arena The arena
	 * @param mob The Mob
	 * @param x The 'radius' in which to grab players
	 * @return A list of nearby players
	 */
	protected List<Player> getNearbyPlayers(Game game, Entity mob, int x)
	{
		List<Player> result = new LinkedList<Player>();
		for (Entity e : mob.getNearbyEntities(x, x, x))
		{
			if (game.playerSet.contains(e))
			{
				result.add((Player) e);
			}
		}
		return result;
	}
	    
	public static CustomMobAbility fromString(String string)
	{
		return EnumUtils.getEnumFromString(CustomMobAbility.class, string.replaceAll("[-_\\. ]", ""));
	}

	public String toString()
	{
		return name;
	}
}
