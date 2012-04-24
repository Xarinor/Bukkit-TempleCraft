package com.msingleton.templecraft.games;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.msingleton.templecraft.TCMobHandler;
import com.msingleton.templecraft.TCUtils;
import com.msingleton.templecraft.Temple;
import com.msingleton.templecraft.TempleCraft;
import com.msingleton.templecraft.TempleManager;
import com.msingleton.templecraft.TemplePlayer;
import com.msingleton.templecraft.util.MobArenaClasses;
import com.msingleton.templecraft.util.Translation;

public class Adventure extends Game
{
	public Adventure(String name, Temple temple, World world)
	{
		super(name, temple, world);
		world.setPVP(false);
	}

	public void endGame()
	{
		super.endGame();
	}

	public void playerDeath(Player p)
	{
		if(TempleCraft.economy != null)
		{
			String s = TempleCraft.economy.format(2.0);
			String currencyName = s.substring(s.indexOf(" ") + 1);

			if(TempleCraft.economy.has(p.getName(),rejoinCost))
			{		
				if(TempleCraft.economy != null && rejoinCost > 0)
				{
					TempleManager.tellPlayer(p, Translation.tr("adventure.rejoin1", rejoinCost, currencyName));
					TempleManager.tellPlayer(p, Translation.tr("adventure.rejoin2"));
				}
			}
			else
			{
				TempleManager.tellPlayer(p, Translation.tr("adventure.rejoinFail1", currencyName));
				TempleManager.tellPlayer(p, Translation.tr("adventure.rejoinFail2"));
			}
		}
		lobbyLoc.getChunk().load(true);
		p.teleport(lobbyLoc);
		super.playerDeath(p);
	}

	protected void handleSign(Sign sign)
	{
		String[] Lines = sign.getLines();

		if(!Lines[0].equals("[TempleCraft]") && !Lines[0].equals("[TC]") && !Lines[0].equals("[TCB]") && !Lines[0].equals("[TempleCraftM]") && !Lines[0].equals("[TCM]"))
		{
			return;
		}

		if(Lines[1].toLowerCase().equals("classes"))
		{
			if(MobArenaClasses.enabled)
			{
				usingClasses = true;
				MobArenaClasses.generateClassSigns(sign);
			}
		}
		super.handleSign(sign);
	}

	public void hitEndBlock(Player p)
	{

		TemplePlayer tp = TempleManager.templePlayerMap.get(p);
		if (playerSet.contains(p))
		{				
			readySet.add(p);
			rewardSet.add(p);
			tp.rewards = rewards;
			int totalTime = (int)(System.currentTimeMillis()-startTime)/1000;
			tellPlayer(p, Translation.tr("game.finishTime", ""+totalTime));

			// Update ScoreBoards
			List<String> scores = new ArrayList<String>();
			scores.add(p.getName());
			scores.add(tp.roundMobsKilled + "");
			scores.add(tp.roundGold + "");
			scores.add(tp.roundDeaths + "");
			scores.add(totalTime + "");
			TempleManager.SBManager.updateScoreBoards(this, scores);

			if(readySet.equals(playerSet))
			{
				endGame();
			}
			else
			{
				tellPlayer(p, Translation.tr("game.readyToLeave"));
				tp.currentCheckpoint = null;
			}
		}
	}

	public void onPlayerMove(PlayerMoveEvent event)
	{
		super.onPlayerMove(event);		
		Player p = event.getPlayer();

		if(!isRunning)
		{
			return;
		}

		Set<Location> tempLocs = new HashSet<Location>();
		for(Location loc : mobSpawnpointMap.keySet())
		{
			if(p.getLocation().distance(loc) < mobSpawnpointMap.get(loc).b.a)
			{
				tempLocs.add(loc);
			}
		}

		for(Location loc : tempLocs)
		{
			try
			{
				if(mobSpawnpointMap.get(loc).a.b.contains(":"))
				{
					String[] split = mobSpawnpointMap.get(loc).a.b.split(":");
					if(split.length > 1)
					{
						//spawn continoues mobs
						int count = Integer.parseInt(split[1]);
						long time = Integer.parseInt(split[0]) * 20;

						TCMobHandler.SpawnMobs(this, loc, mobSpawnpointMap.get(loc).a.a.a, mobSpawnpointMap.get(loc).a.a.b, mobSpawnpointMap.get(loc).b.b.a, count, time);
						mobSpawnpointMap.remove(loc);
					}
					else
					{
						TCMobHandler.SpawnMobs(this, loc, mobSpawnpointMap.get(loc).a.a.a, mobSpawnpointMap.get(loc).a.a.b, mobSpawnpointMap.get(loc).b.b.a, mobSpawnpointMap.get(loc).b.b.b, mobSpawnpointMap.get(loc).a.b);
						mobSpawnpointMap.remove(loc);
					}
				}
				else
				{
					TCMobHandler.SpawnMobs(this, loc, mobSpawnpointMap.get(loc).a.a.a, mobSpawnpointMap.get(loc).a.a.b, mobSpawnpointMap.get(loc).b.b.a, mobSpawnpointMap.get(loc).b.b.b, mobSpawnpointMap.get(loc).a.b);
					mobSpawnpointMap.remove(loc);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				TCMobHandler.SpawnMobs(this, loc, mobSpawnpointMap.get(loc).a.a.a, mobSpawnpointMap.get(loc).a.a.b, mobSpawnpointMap.get(loc).b.b.a);
				mobSpawnpointMap.remove(loc);
			}
		}
	}

	public void onEntityKilledByEntity(LivingEntity killed, Entity killer)
	{

		super.onEntityKilledByEntity(killed, killer);
		TCUtils.sendDeathMessage(this, killed, killer);

		if(killer instanceof Player)
		{
			if(mobGoldMap != null && mobGoldMap.containsKey(killed.getEntityId()))
			{
				for(Player p : playerSet)
				{
					int gold = mobGoldMap.get(killed.getEntityId())/playerSet.size();
					TempleManager.templePlayerMap.get(p).roundGold += gold;
					if(TempleCraft.economy != null)
					{
						TempleCraft.economy.depositPlayer(p.getName(), gold);
					}
				}
			}
		}
	}
}
