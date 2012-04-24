package com.msingleton.templecraft;


import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Slime;

import com.msingleton.templecraft.custommobs.CustomMob;
import com.msingleton.templecraft.games.Game;
import com.msingleton.templecraft.tasks.AbilityTask;
import com.msingleton.templecraft.tasks.SpawnTask;

public class TCMobHandler 
{

	public static void SpawnMobs(Game game, Location loc, EntityType mob) 
	{
		SpawnMobs(game, loc, mob, -1, 0, 1, 0);
	}
	
	public static void SpawnMobs(Game game, Location loc, EntityType mob, int size) 
	{
		SpawnMobs(game, loc, mob, size, 0, 1, 0);
	}

	public static void SpawnMobs(Game game, Location loc, EntityType mob, int size, int health) 
	{
		SpawnMobs(game, loc, mob, size, health, 1, 0);
	}
	
	public static void SpawnMobs(Game game, Location loc, EntityType mob, int size, int health, int dmgmulti, String abilitys) 
	{
		try
		{
			LivingEntity e = game.world.spawnCreature(loc,mob);

			if(e == null)
				return;
			
			Random r = new Random();
			if(TempleCraft.economy != null && (TempleManager.mobGoldMin + TempleManager.mobGoldRan) != 0 && r.nextInt(3) == 0)
			{
				game.mobGoldMap.put(e.getEntityId(), r.nextInt(TempleManager.mobGoldRan)+TempleManager.mobGoldMin);
			}
			
			CustomMob cmob = new CustomMob(e);
			
			if(e instanceof Slime && size > 0)
			{
				Slime slime = (Slime) e;
				slime.setSize(size);
				cmob.setSize(size);
			}

			if(health <= 0)
			{
				health = e.getMaxHealth();
			}
			cmob.setHealth(health);
			
			cmob.setDMGMultiplikator(dmgmulti);
			
			if(abilitys.length() > 0)
			{
				cmob.addAbilitysfromString(abilitys);
			}
			
			game.customMobManager.AddMob(cmob);

			if(!cmob.getAbilitys().isEmpty())
			{
				int id = TempleCraft.TCScheduler.scheduleAsyncRepeatingTask(TempleCraft.TCPlugin, new AbilityTask(game, cmob), 100L, 100L);
				AbilityTask.taskID = id;
				game.AbilityTaskIDs.put(cmob,id);
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
		}
	}
	
	public static void SpawnMobs(Game game, Location loc, EntityType mob, int size, int count, long time) 
	{
		SpawnMobs(game, loc, mob, size, 0, count, time);
	}
	
	public static void SpawnMobs(Game game, Location loc, EntityType mob, int size, int health, int count, long time) 
	{
		//for (int i = 0; i < playerSet.size(); i++)
		//{
		if(count == 1 && time == 0)
		{
			try
			{
				LivingEntity e = game.world.spawnCreature(loc,mob);
				
				if(e == null)
					return;

				Random r = new Random();
				if(TempleCraft.economy != null && (TempleManager.mobGoldMin + TempleManager.mobGoldRan) != 0 && r.nextInt(3) == 0)
				{
					game.mobGoldMap.put(e.getEntityId(), r.nextInt(TempleManager.mobGoldRan)+TempleManager.mobGoldMin);
				}

				CustomMob cmob = new CustomMob(e);
				
				if(e instanceof Slime && size > 0)
				{
					Slime slime = (Slime) e;
					slime.setSize(size);
					cmob.setSize(size);
				}

				if(health > 0)
				{
					cmob.setHealth(health);
					game.customMobManager.AddMob(cmob);
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
			}
		}
		else
		{
			int id = TempleCraft.TCScheduler.scheduleAsyncRepeatingTask(TempleCraft.TCPlugin, new SpawnTask(game, loc, mob, size, health, count, TempleCraft.TCPlugin), 0L, time);
			SpawnTask.taskID = id;
			game.SpawnTaskIDs.add(id);
		}
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
