package com.msingleton.templecraft;


import java.util.Random;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import com.msingleton.templecraft.games.Game;

public class TCMobHandler 
{
	
	public static void SpawnMobs(Game game, Location loc, EntityType mob) 
	{
		SpawnMobs(game, loc, mob, 0);
	}
	
	public static void SpawnMobs(Game game, Location loc, EntityType mob, int health) 
	{
		//for (int i = 0; i < playerSet.size(); i++)
		//{
			try
			{
				LivingEntity e = game.world.spawnCreature(loc,mob);
				
				if(e == null)
					return;

				//TODO: implement setMaxHealth() when bukkit support it.
				/*if(health > 0)
				{
					e.setHealth(health);
				}*/
				
				Random r = new Random();
				if(TempleCraft.economy != null && (TempleManager.mobGoldMin + TempleManager.mobGoldRan) != 0 && r.nextInt(3) == 0)
				{
					game.mobGoldMap.put(e.getEntityId(), r.nextInt(TempleManager.mobGoldRan)+TempleManager.mobGoldMin);
				}
				
				if(!(e instanceof Creature))
				{
					return;
				}
				
				// Grab a random target.
				Creature c = (Creature) e;
				c.setTarget(TCUtils.getClosestPlayer(game, e));
			}
			catch(Exception e)
			{
				System.out.println("[TempleCraft] Could not spawn "+mob.getName());
			};
		//}
	}
	
	public static EntityType getRandomCreature()
	{
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
