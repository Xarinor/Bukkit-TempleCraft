package com.bukkit.xarinor.templecraft.games;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.bukkit.xarinor.templecraft.TCUtils;
import com.bukkit.xarinor.templecraft.Temple;
import com.bukkit.xarinor.templecraft.util.Classes;

//TODO JavaDoc
public class Spleef extends Game
{
	public Map<Location,String> brokenBlockMap = new HashMap<Location,String>();
	public Set<Player> aliveSet				= new HashSet<Player>();
	private Timer gameTimer					= new Timer();
	public Player winner;
	public int roundNum = 0;
	public int roundLim = 3;

	public Spleef(String name, Temple temple, World world)
	{
		super(name, temple, world);
		world.setPVP(false);
	}

	public void playerJoin(Player p)
	{	
		super.playerJoin(p);
		Classes.clearInventory(p);
		TCUtils.debugMessage("Player " + p.getName() + " joined Temple " + temple.templeName + ", Game " + gameName + "(" + gameType + ").");
	}

	public void startGame()
	{
		startRound();
		super.startGame();
	}

	public void endGame()
	{
		try
		{
			TCUtils.debugMessage("end game");
			gameTimer.cancel();
			super.endGame();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startRound()
	{
		isRunning = true;
		roundNum++;
		restorePlayingField();
		//for(Location loc : lobbyLocMap.keySet()) {
		//TODO Testing
		Iterator<Entry<Location, Integer>> entries = lobbyLocMap.entrySet().iterator();
		while (entries.hasNext()) {
			Entry<Location, Integer> entry = entries.next();
			Location loc = entry.getKey();
			
			loc.getBlock().setTypeId(0);
		}
		deadSet.clear();
		aliveSet.addAll(playerSet);
		tellAll("Round "+roundNum);
		TCUtils.debugMessage("Started Round " + roundNum);
	}

	public void endRound()
	{
		isRunning = false;
		for(Player p : aliveSet)
		{
			lobbyLoc.getChunk().load(true);
			p.teleport(lobbyLoc);
		}
		tellAll(winner.getDisplayName()+" won round "+roundNum);
		if(roundNum >= roundLim)
		{
			tellAll("Good game! Ending Spleef...");
			TCUtils.debugMessage("Game end start waiting timer 2000");
			TimerTask task = new TimerTask()
			{
				public void run()
				{
					endGame();
				}
			};
			gameTimer.schedule(task, 2000);
		}
		else
		{
			//for(Location loc : lobbyLocMap.keySet()) {
			//TODO Testing
			Iterator<Entry<Location, Integer>> entries = lobbyLocMap.entrySet().iterator();
			while (entries.hasNext()) {
				Entry<Location, Integer> entry = entries.next();
				Location loc = entry.getKey();
					
				loc.getBlock().setTypeId(42);
			}
		}
	}

	private void restorePlayingField()
	{
		TCUtils.debugMessage("restorePlayingField");
		//for(Location loc : brokenBlockMap.keySet()) {
		//TODO Testing
		Iterator<Entry<Location, String>> entries = brokenBlockMap.entrySet().iterator();
		while (entries.hasNext()) {
			Entry<Location, String> entry = entries.next();
			Location loc = entry.getKey();
				
			String[] s = brokenBlockMap.get(loc).split(":");
			int id;
			byte data;
			try
			{
				id = Integer.parseInt(s[0]);
				data = Byte.parseByte(s[1]);
			}
			catch(Exception e)
			{
				id = Integer.parseInt(s[0]);
				data = 0;
			}
			world.getBlockAt(loc).setTypeIdAndData(id, data, true);
		}
	}

	public void playerDeath(Player p)
	{
		lobbyLoc.getChunk().load(true);
		p.teleport(lobbyLoc);
		super.playerDeath(p);
		aliveSet.remove(p);
		if(aliveSet.size() == 1)
		{
			winner = (Player)aliveSet.toArray()[0];
			endRound();
		}
		else if(aliveSet.isEmpty())
		{
			winner = p;
			endRound();
		}
	}
}
