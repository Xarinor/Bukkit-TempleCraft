package com.msingleton.templecraft.tasks;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Slime;

import com.msingleton.templecraft.TCUtils;
import com.msingleton.templecraft.TempleCraft;
import com.msingleton.templecraft.TempleManager;
import com.msingleton.templecraft.custommobs.CustomMob;
import com.msingleton.templecraft.games.Game;

public class SpawnTask implements Runnable 
{
	public TempleCraft plugin;
	public EntityType mob;
	public int health;
	public int count;
	public Game game;
	public Location loc;
	public int size;

	public SpawnTask(Game game, Location loc, EntityType mob, int size, int health, int maxCount, TempleCraft plugin){
		this.plugin = plugin;   
		this.mob = mob;
		this.game = game;
		this.loc = loc;
		this.health = health;
		this.count = maxCount;
		this.size = size;
	}

	public static int taskID;

	@Override
	public void run() 
	{
		try
		{
			//System.out.print(taskID + ":" + count);
			count--;

			if(count <= 0  || !game.isRunning || game.isEnding || TempleManager.server.getWorld(loc.getWorld().getUID()) == null)
			{
				game.SpawnTaskIDs.remove(taskID);
				TempleCraft.TCScheduler.cancelTask(taskID);
			}
			else
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
		}
		catch(Exception e)
		{
			System.out.println("[TempleCraft] Could not spawn "+mob.getName());
		}
	}
}   
