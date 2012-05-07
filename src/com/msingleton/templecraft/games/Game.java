package com.msingleton.templecraft.games;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.msingleton.templecraft.TCMobHandler;
import com.msingleton.templecraft.TCUtils;
import com.msingleton.templecraft.Temple;
import com.msingleton.templecraft.TempleCraft;
import com.msingleton.templecraft.TempleManager;
import com.msingleton.templecraft.TemplePlayer;
import com.msingleton.templecraft.custommobs.CustomMob;
import com.msingleton.templecraft.custommobs.CustomMobManager;
import com.msingleton.templecraft.custommobs.CustomMobType;
import com.msingleton.templecraft.custommobs.CustomMobUtils;
import com.msingleton.templecraft.util.MobArenaClasses;
import com.msingleton.templecraft.util.MobSpawnProperties;
import com.msingleton.templecraft.util.Pair;
import com.msingleton.templecraft.util.Translation;

public class Game
{
	public World world;
	public Temple temple;
	public String gameType;
	public String gameName;
	public boolean isRunning	  = false;
	public boolean isEnding	   = false;
	public boolean isSetup		= false;
	public boolean isLoaded	   = false;
	public boolean alwaysJoinable = false;
	public boolean usingClasses   = false;
	public int rejoinCost;
	public int maxPlayers;

	// Colors
	public static ChatColor c1 = TempleCraft.c1;
	public static ChatColor c2 = TempleCraft.c2;
	public static ChatColor c3 = TempleCraft.c3;

	protected static File messageFile = null;

	// Location variables for the Temple region.
	public Location lobbyLoc = null;
	public Location startLoc = null;

	// Map for Death Message
	public Map<Integer, Entity> lastDamager = new HashMap<Integer, Entity>();
	// Contains Mob Spawnpoint Locations
	public Set<Location> mobSpawnpointSet = new HashSet<Location>();

	// Score Vars
	public List<Pair<String,Double>> standings = new ArrayList<Pair<String,Double>>();
	public int displayAmount = 5;
	public int saveAmount = 5;

	// Contains Active Mob Spawnpoints and Creature Types
	//public Map<Location,Pair<Pair<Pair<EntityType,Integer>,String>,Pair<Integer,Pair<Integer,Integer>>>> mobSpawnpointMap	 = new HashMap<Location,Pair<Pair<Pair<EntityType,Integer>,String>,Pair<Integer,Pair<Integer,Integer>>>>();
	public Map<Location,MobSpawnProperties> mobSpawnpointMap	 = new HashMap<Location,MobSpawnProperties>();
	public Map<Integer, Integer> mobGoldMap			= new HashMap<Integer, Integer>();
	public Map<Location, Integer> checkpointMap		= new HashMap<Location, Integer>();
	public Map<Location, String[]> chatMap			 = new HashMap<Location, String[]>();
	public Map<Location, List<ItemStack>> rewardLocMap = new HashMap<Location, List<ItemStack>>();
	public Map<Location, Integer> lobbyLocMap		  = new HashMap<Location, Integer>();
	public Map<Location, Integer> startLocMap		  = new HashMap<Location, Integer>();
	public Map<Chunk, Entity[]> tempMobLoc		  = new HashMap<Chunk, Entity[]>();
	//public Map<Location, Integer> LocHealthMap		  = new HashMap<Location, Integer>();

	public Set<Player> playerSet		= new HashSet<Player>();
	public Set<Player> readySet		 = new HashSet<Player>();
	public Set<Player> deadSet		  = new HashSet<Player>();
	public Set<Player> rewardSet		= new HashSet<Player>();
	public Set<LivingEntity> monsterSet = new HashSet<LivingEntity>();

	public Set<Pair<Location,Set<Integer>>> placeableLocs = new HashSet<Pair<Location,Set<Integer>>>();
	public Set<Pair<Location,Set<Integer>>> breakableLocs = new HashSet<Pair<Location,Set<Integer>>>();
	public Set<Location> coordLocSet = new HashSet<Location>();
	public Set<Block> tempBlockSet   = new HashSet<Block>();
	public Set<Location> endLocSet   = new HashSet<Location>();
	public List<ItemStack> rewards	= new ArrayList<ItemStack>();

	public Set<Integer> SpawnTaskIDs   = new HashSet<Integer>();
	public Map<CustomMob,Integer> AbilityTaskIDs   = new HashMap<CustomMob,Integer>();

	public CustomMobManager customMobManager = new CustomMobManager();

	public long startTime;
	public static int mobSpawner = 7;
	public static int diamondBlock = 57;
	public static int ironBlock = 42;
	public static int goldBlock = 41;
	public static int[] coordBlocks = {mobSpawner, diamondBlock, ironBlock, goldBlock, 63, 68};

	public Game(String name, Temple temple, World world)
	{
		TempleManager.gameSet.add(this);
		gameType	  = getClass().getSimpleName();
		gameName	  = name;
		this.world	= world;
		this.temple   = temple;
		isSetup	   = temple.isSetup;
		coordLocSet   = temple.coordLocSet;
		rejoinCost	= TempleManager.rejoinCost;
		maxPlayers	= temple.maxPlayersPerGame;
		messageFile	= TCUtils.getConfig("messages");
	} 

	/**
	 * Starts the game.
	 */
	public void startGame()
	{		
		isRunning = true;
		startTime = System.currentTimeMillis();
		convertSpawnpoints();
		TCUtils.debugMessage("Game " + gameName + "(" + gameType + ") gestartet");
		for(Player p : playerSet)
		{
			TemplePlayer tp = TempleManager.templePlayerMap.get(p);
			if(tp.team != -1)
			{
				p.getInventory().setHelmet(new ItemStack(Material.WOOL,1,(short)0,(byte)tp.team));
			}
			getPlayerSpawnLoc(tp).getChunk().load(true);
			p.teleport(getPlayerSpawnLoc(tp));
		}
		readySet.clear();

		tellAll(Translation.tr("game.start"));
	}


	/**
	 * Ends the game.
	 */
	public void endGame()
	{
		try
		{
			TempleManager.tellAll(Translation.tr("game.finished", gameType, temple.templeName));
			TCUtils.debugMessage("Game " + gameName + "(" + gameType + ") beendet");
			for(int id : SpawnTaskIDs)
			{
				TCUtils.debugMessage("Cancel SpawnTask " + id);
				TempleCraft.TCScheduler.cancelTask(id);
			}
			SpawnTaskIDs.clear();
			for(int id : AbilityTaskIDs.values())
			{
				TCUtils.debugMessage("Cancel AbilityTask " + id);
				TempleCraft.TCScheduler.cancelTask(id);
			}
			AbilityTaskIDs.clear();
			tempMobLoc.clear();
			isRunning = false;
			isEnding = true;
			readySet.clear();
			playerSet.clear();
			rewardPlayers(rewardSet);
		}
		catch (Exception e) {
			System.out.println("[TempleCraft] Error while closing the temple.");
			e.printStackTrace();
		}
		finally
		{
			TCUtils.removePlayers(world);
			TempleManager.gameSet.remove(this);
			TCUtils.deleteTempWorld(world);
		}
	}

	private void consolidateRewards()
	{
		List<ItemStack> tempSet = new ArrayList<ItemStack>();
		for(ItemStack i: rewards)
		{
			if(i == null)
			{
				continue;
			}
			boolean found = false;
			for(ItemStack j : tempSet)
			{
				if(j.getTypeId() == i.getTypeId())
				{
					j.setAmount(j.getAmount()+i.getAmount());
					found = true;
				}
			}
			if(!found)
			{
				tempSet.add(i);
			}
		}
		rewards.clear();
		rewards.addAll(tempSet);
	}

	private void rewardPlayers(Set<Player> players)
	{
		consolidateRewards();
		for(Player p : players)
		{
			TCUtils.debugMessage("Try to reward Player " + p.getName());
			try
			{
				StringBuilder msg = new StringBuilder();
				TemplePlayer tp = TempleManager.templePlayerMap.get(p);
				List<ItemStack> tempList = new ArrayList<ItemStack>();
				for(ItemStack item : tp.rewards)
				{
					if(item != null)
					{
						TCUtils.debugMessage("Add reward" +  item.getTypeId() + ":" + item.getData().getData() + " (" + item.getAmount() + ") to TempList for Player " + p.getName());
						tempList.add(item);
					}
				}

				int size = tempList.size();
				if(size == 0)
				{
					TCUtils.debugMessage("tempList.size == 0");
					continue;
				}
				for(int i = 0; i<size; i++)
				{
					ItemStack item = tempList.get(i);
					if(item != null)
					{
						msg.append(item.getAmount()+" "+TCUtils.getMaterialName(item.getType().name()));
						if(i<size-2)
						{
							msg.append(", ");
						}
						else if(i<size-1)
						{
							msg.append(" "+Translation.tr("and")+" ");
						}
						
						if(TCUtils.isTCWorld(p.getLocation().getWorld()))
						{
							if(TCUtils.hasPlayerInventory(p.getName()))
							{
								TCUtils.restorePlayerInventory(p);
							}
						}
						
						p.getInventory().addItem(item);
						
						TCUtils.debugMessage("Player " + p.getName() + " gets " + item.getTypeId() + ":" + item.getData().getData() + " (" + item.getAmount() + ") at Location " + p.getLocation().toString());
					}
				}
				TempleManager.tellPlayer(p,Translation.tr("game.treasureReceived", msg.toString()));
			}
			catch (Exception e)
			{
				System.out.print("[TempleCraft] Error during reward distribution to player:" + p.getName());
				System.out.print("[TempleCraft] " + e.getMessage());
			}
		}
	}

	protected void convertLobby()
	{
		for(Block b: getBlockSet(Material.WALL_SIGN.getId()))
		{	 
			// Cast the block to a sign to get the text on it.
			if(!(b.getState() instanceof Sign))
			{
				continue;
			}
			Sign sign = (Sign) b.getState();
			handleSign(sign);
		}
		for(Block b: getBlockSet(Material.SIGN_POST.getId()))
		{	 
			// Cast the block to a sign to get the text on it.
			if(!(b.getState() instanceof Sign))
			{
				continue;
			}
			Sign sign = (Sign) b.getState();
			handleSign(sign);
		}

		for(Block b: getBlockSet(goldBlock))
		{
			Block rb = b.getRelative(0, -1, 0);
			if(rb.getTypeId() == ironBlock)
			{
				Block rb2 = b.getRelative(0,1,0);
				if(rb2.getType().equals(Material.WOOL))
				{
					lobbyLocMap.put(rb.getLocation(), (int)rb2.getData());
				}
				else
				{
					lobbyLocMap.put(rb.getLocation(), -1);
				}
				b.setTypeId(0);
				rb.setTypeId(ironBlock);
			}
			else
			{
				temple.coordLocSet.remove(b);
			}
		}
	}

	protected void convertSpawnpoints()
	{
		for(Block b: getBlockSet(Material.WALL_SIGN.getId()))
		{	 
			// Cast the block to a sign to get the text on it.
			Sign sign = (Sign) b.getState();
			handleSign(sign);
		}
		for(Block b: getBlockSet(Material.SIGN_POST.getId()))
		{	 
			// Cast the block to a sign to get the text on it.
			Sign sign = (Sign) b.getState();
			handleSign(sign);
		}
		for(Block b: getBlockSet(mobSpawner))
		{
			if(b.getLocation().getBlockY()<7)
			{
				temple.coordLocSet.remove(b);
			}
			mobSpawnpointSet.add(b.getLocation());
			MobSpawnProperties msp = new MobSpawnProperties();
			msp.setEntityType(TCMobHandler.getRandomCreature());
			mobSpawnpointMap.put(b.getLocation(),msp);
			//mobSpawnpointMap.put(b.getLocation(),new Pair<Pair<Pair<EntityType,Integer>,String>,Pair<Integer,Pair<Integer,Integer>>>(new Pair<Pair<EntityType,Integer>,String>(new Pair<EntityType,Integer>(TCMobHandler.getRandomCreature(),-1),"0:1"),new Pair<Integer,Pair<Integer,Integer>>(20,new Pair<Integer,Integer>(0,1))));
			b.setTypeId(0);
		}
		for(Block b: getBlockSet(diamondBlock))
		{
			Block rb = b.getRelative(0, -1, 0);
			if(rb.getTypeId() == ironBlock)
			{
				Block rb2 = b.getRelative(0,1,0);
				if(rb2.getType().equals(Material.WOOL))
				{
					startLocMap.put(rb.getLocation(), (int)rb2.getData());
				}
				else
				{
					startLocMap.put(rb.getLocation(), -1);
				}
				b.setTypeId(0);
				rb.setTypeId(0);
			}
			else if(rb.getTypeId() == goldBlock)
			{
				Block rb2 = b.getRelative(0, 1, 0);
				endLocSet.add(rb.getLocation());
				b.setTypeId(0);
				rb.setTypeId(goldBlock);
				// If block above is a chest
				if(rb2.getState() instanceof Chest)
				{
					rewards.addAll(Arrays.asList(((Chest)rb2.getState()).getInventory().getContents()));
					((Chest)rb2.getState()).getInventory().clear();
					rb2.setTypeId(0);
				}
			}
			else
			{
				temple.coordLocSet.remove(b);
			}
		}
		for(Block b: getBlockSet(ironBlock))
		{
			Block rb = b.getRelative(0, 1, 0);
			if(rb.getState() instanceof Chest)
			{
				Inventory inv = ((Chest)rb.getState()).getInventory();
				rewardLocMap.put(b.getLocation(),Arrays.asList(inv.getContents()));
				inv.clear();
				rb.setTypeId(0);
			}
		}
	}

	protected Set<Block> getBlockSet(int id)
	{
		Set<Block> result = new HashSet<Block>();

		if(!coordLocSet.isEmpty())
		{
			for(Location loc : coordLocSet)
			{
				Block b = world.getBlockAt(loc);
				if(b.getTypeId() == id)
					result.add(b);
			}
		}
		return result;
	}

	protected void handleSign(Sign sign)
	{
		String[] Lines = sign.getLines();
		Block b = sign.getBlock();


		if(Lines[0].equals("[TCB]"))
		{
			if(CustomMobUtils.bossTypeExists(Lines[1]))
			{
				CustomMobType cmt = CustomMobUtils.getBoss(Lines[1]);
				Location loc = new Location(b.getWorld(),b.getX()+.5,b.getY(),b.getZ()+.5);
				mobSpawnpointSet.add(loc);
				MobSpawnProperties msp = new MobSpawnProperties();
				msp.setEntityType(TCMobHandler.getRandomCreature());
				msp.setDMGMulti(cmt.getDmgmulti());
				msp.setEntityType(cmt.getMobtype());
				msp.setRange(cmt.getRange());
				msp.setHealth(cmt.getMaxhealth());
				msp.setSize(cmt.getSize());
				msp.setIsbossmob(true);
				msp.setAbilities_random(cmt.getAbilities_random());
				msp.setAbilities_rotation(cmt.getAbilities_rotation());
				mobSpawnpointMap.put(b.getLocation(),msp);
				b.setTypeId(0);
			}
			else
			{
				int size = -1;
				EntityType ct = null;
				if(Lines[1].contains(":"))
				{
					String[] split = Lines[1].split(":");
					ct = EntityType.fromName(split[0]);

					if(ct == null)
					{
						System.out.println("[TempleCraft] Could not find EntityType \"" + split[0] + "\"");
						return;
					}
					if(split.length == 2)
					{
						if(ct == EntityType.SLIME)
						{
							try
							{
								size = Integer.parseInt(split[1]);
							}
							catch (Exception e) 
							{
								size = -1;
							}
						}
					}
				}
				else
				{
					ct = EntityType.fromName(Lines[1]);
				}
				if(ct == null)
				{
					System.out.println("[TempleCraft] Could not find EntityType \"" + Lines[1] + "\"");
					return;
				}
				int range = 20;
				int health = 0;
				int dmgmulti = 0;
				try
				{
					String[] split = Lines[2].split(":");
					if(split[0].contains("-"))
					{
						health = 0;
					}
					else
					{
						health = Integer.parseInt(split[0]);
					}

					if(split.length > 1)
					{
						if(split[1].contains("-"))
						{
							range = 20;
						}
						else
						{
							range = Integer.parseInt(split[1]);
						}
					}

					if(split.length > 2)
					{
						if(split[2].contains("-"))
						{
							dmgmulti = 1;
						}
						else
						{
							dmgmulti = Integer.parseInt(split[2]);
						}
					}
				}
				catch(Exception e)
				{
					health = 0;
					range = 20;
					System.out.println("[TempleCraft] Could not use this line \"" + Lines[2] + "\" for health:range:dmgmultiplicator");
				}
				Location loc = new Location(b.getWorld(),b.getX()+.5,b.getY(),b.getZ()+.5);
				mobSpawnpointSet.add(loc);
				//mobSpawnpointMap.put(loc,new Pair<EntityType,Integer>(ct,range));

				MobSpawnProperties msp = new MobSpawnProperties();
				msp.setEntityType(TCMobHandler.getRandomCreature());
				msp.setAbilitys(Lines[3]);
				msp.setDMGMulti(dmgmulti);
				msp.setEntityType(ct);
				msp.setRange(range);
				msp.setHealth(health);
				msp.setSize(size);
				msp.setIsbossmob(true);
				mobSpawnpointMap.put(b.getLocation(),msp);
				//mobSpawnpointMap.put(loc,new Pair<Pair<Pair<EntityType,Integer>,String>,Pair<Integer,Pair<Integer,Integer>>>(new Pair<Pair<EntityType,Integer>,String>(new Pair<EntityType,Integer>(ct,size),Lines[3]),new Pair<Integer,Pair<Integer,Integer>>(range,new Pair<Integer,Integer>(health,dmgmulti))));
				//LocHealthMap.put(loc, health);
				b.setTypeId(0);
			}

			return;	
		}

		if(!Lines[0].equals("[TempleCraft]") && !Lines[0].equals("[TC]"))
		{
			if(Lines[0].equals("[TempleCraftM]") || Lines[0].equals("[TCM]"))
			{
				String[] newLines = {Lines[1]+Lines[2],Lines[3]};
				chatMap.put(b.getLocation(), newLines);
				b.setTypeId(0);
			}
			else if(Lines[0].equals("[TempleCraftML]") || Lines[0].equals("[TCML]"))
			{
				String[] newLines = {"",Lines[3]};
				newLines[0] = getMessageFromFile(Lines[1]);
				chatMap.put(b.getLocation(), newLines);
				b.setTypeId(0);
			}
			else
			{
				temple.coordLocSet.remove(b);
			}
			return;			
		}

		if(Lines[1].toLowerCase().equals("lobby"))
		{
			lobbyLoc = b.getLocation();
			b.setTypeId(0);
		}
		else if(Lines[1].toLowerCase().equals("classes"))
		{
			//sign.getBlock().setTypeId(0);
		}
		else if(Lines[1].toLowerCase().equals("place") || Lines[1].toLowerCase().equals("break"))
		{
			int radius = 0;
			try
			{
				radius = Integer.parseInt(Lines[2]);
			}
			catch(Exception e)
			{
				radius = 0;
			}
			for(int x=-radius;x<=radius;x++)
			{
				for(int y=-radius;y<=radius;y++)
				{
					for(int z=-radius;z<=radius;z++)
					{
						Location loc = new Location(world,b.getX() + x, b.getY() + y, b.getZ() + z);
						String[] split = Lines[3].split(",");
						Set<Integer> blocks = new HashSet<Integer>();
						for (String str : split)
						{
							try
							{
								blocks.add(Integer.parseInt(str));
							}
							catch (Exception e) {}
						}
						if(Lines[1].toLowerCase().equals("place"))
						{
							placeableLocs.add(new Pair<Location,Set<Integer>>(loc,blocks));
						} else {
							breakableLocs.add(new Pair<Location,Set<Integer>>(loc,blocks));
						}
						b.setTypeId(0);
					}
				}
			}
			//sign.getBlock().setTypeId(0);
		}
		else if(Lines[1].toLowerCase().equals("checkpoint"))
		{
			try
			{
				checkpointMap.put(sign.getBlock().getLocation(), Integer.parseInt(Lines[3]));
				String[] newLines = {Translation.tr("game.checkpoint"),Lines[3]};
				chatMap.put(b.getLocation(), newLines);
			}
			catch(Exception e)
			{
				checkpointMap.put(sign.getBlock().getLocation(), 5);
				String[] newLines = {Translation.tr("game.checkpoint"),"5"};
				chatMap.put(b.getLocation(), newLines);
			}
			b.setTypeId(0);
		}
		else if(Lines[1].toLowerCase().equals("spawnarea"))
		{
			int radius;
			try
			{
				radius = Math.abs(Integer.parseInt(Lines[3]));
			}
			catch(Exception e)
			{
				radius = 5;
			}

			//Get a square area of blocks and then keep the ones that are a distance radius away or less
			int y = b.getY();
			for(int i=-radius;i<=radius;i++)
			{
				for(int k=-radius;k<=radius;k++)
				{
					int x = b.getX()+i;
					int z = b.getZ()+k;
					Location loc = new Location(world,x, y, z);
					if(TCUtils.distance(b.getLocation(), loc) <= radius)
					{
						startLocMap.put(loc, -1);
					}
				}
			}

			b.setTypeId(0);	
		}
		else
		{
			int size = -1;
			EntityType ct = null;
			if(Lines[1].contains(":"))
			{
				String[] split = Lines[1].split(":");
				ct = EntityType.fromName(split[0]);
				if(split.length == 2)
				{
					if(ct == EntityType.SLIME)
					{
						try
						{
							size = Integer.parseInt(split[1]);
						}
						catch (Exception e) 
						{
							size = -1;
						}
					}
				}
			}
			else
			{
				ct = EntityType.fromName(Lines[1]);
			}

			if(ct == null)
			{
				return;
			}
			int range = 20;
			long time = 0;
			int count = 1;
			try
			{
				String[] split = Lines[3].split(":");
				if(split[0].contains("-"))
				{
					range = 20;
				}
				else
				{
					range = Integer.parseInt(split[0]);
				}

				if(split.length > 1)
				{
					if(split[1].contains("-"))
					{
						time = 20;
					}
					else
					{
						time = Integer.parseInt(split[1]);
					}
				}

				if(split.length > 2)
				{
					if(split[2].contains("-"))
					{
						count = 1;
					}
					else
					{
						count = Integer.parseInt(split[2]);
					}
				}
			}
			catch(Exception e)
			{
				range = 20;
				time = 0;
				count = 1;
			}

			int health;
			try
			{
				health = Integer.parseInt(Lines[2]);
			}
			catch(Exception e)
			{
				health = 0;
			}
			int dmgmulti = 1;
			Location loc = new Location(b.getWorld(),b.getX()+.5,b.getY(),b.getZ()+.5);
			mobSpawnpointSet.add(loc);
			MobSpawnProperties msp = new MobSpawnProperties();
			msp.setEntityType(TCMobHandler.getRandomCreature());
			msp.setDMGMulti(dmgmulti);
			msp.setEntityType(ct);
			msp.setRange(range);
			msp.setHealth(health);
			msp.setSize(size);
			msp.setTime(time);
			msp.setCount(count);
			mobSpawnpointMap.put(b.getLocation(),msp);
			//mobSpawnpointMap.put(loc,new Pair<EntityType,Integer>(ct,range));
			//mobSpawnpointMap.put(loc,new Pair<Pair<Pair<EntityType,Integer>,String>,Pair<Integer,Pair<Integer,Integer>>>(new Pair<Pair<EntityType,Integer>,String>(new Pair<EntityType,Integer>(ct,size),time_count),new Pair<Integer,Pair<Integer,Integer>>(range,new Pair<Integer,Integer>(health,dmgmulti))));
			//LocHealthMap.put(loc, health);
			b.setTypeId(0);
		}
	}

	public Set<Location> getPossibleSpawnLocs(int team)
	{
		if(team == -1)
		{
			return startLocMap.keySet();
		}

		Set<Location> tempSet = new HashSet<Location>();
		for(Location l : startLocMap.keySet())
		{
			if(startLocMap.get(l) == team)
			{
				tempSet.add(l);
			}
		}

		if(tempSet.isEmpty())
		{
			return startLocMap.keySet();
		}
		else
		{
			return tempSet;
		}
	}

	public Location getPlayerSpawnLoc(TemplePlayer tp)
	{
		Random r = new Random();
		Location loc = null;
		Set<Location> locSet = new HashSet<Location>();

		locSet = getPossibleSpawnLocs(tp.team);

		for(Location l : locSet)
		{
			if(loc == null)
			{
				loc = l;
			}
			else if(r.nextInt(locSet.size()) == 0)
			{
				loc = l;
			}
		}
		return loc;
	}

	/**
	 * Attempts to let a player join the Temple session.
	 * Players must have an empty inventory to join the Temple. Their
	 * location will be stored for when they leave.
	 */
	public void playerJoin(Player p)
	{		
		if (!TempleManager.isEnabled)
		{
			tellPlayer(p, Translation.tr("notEnabled"));
			return;
		}
		if (playerSet.isEmpty() && !temple.trySetup(world))
		{
			tellPlayer(p, Translation.tr("templeNotSetup", temple.templeName));
			TCUtils.deleteTempWorld(world);
			return;
		}
		if (playerSet.contains(p))
		{
			tellPlayer(p, Translation.tr("game.alreadyPlayingSame"));
			return;
		}
		if (TempleManager.playerSet.contains(p))
		{
			tellPlayer(p, Translation.tr("game.alreadyPlaying"));
			return;
		}
		TemplePlayer tp = TempleManager.templePlayerMap.get(p);
		if(tp.currentGame != null)
		{
			return;
		}
		if (isRunning && !alwaysJoinable)
		{
			tellPlayer(p, Translation.tr("game.inProgress", gameName));
			return;
		}
		if(isFull())
		{
			tellPlayer(p, Translation.tr("game.isFull", gameName));
			return;
		}

		tp.currentTemple = temple;
		tp.currentGame = this;
		tp.currentCheckpoint = null;
		TempleManager.playerSet.add(p);
		playerSet.add(p);
		TCUtils.debugMessage("Player " + p.getName() + " joined Temple " + temple.templeName + ", Game " + gameName + "(" + gameType + ").");

		if(world.getPlayers().isEmpty())
		{
			convertLobby();
			world.setTime(8000);
			world.setStorm(false);
		}

		if (!TempleManager.locationMap.containsKey(p))
		{
			TempleManager.locationMap.put(p, p.getLocation());
		}
		if(TempleManager.manageInventory)
		{
			if(!TCUtils.hasPlayerInventory(p.getName()))
			{
				TCUtils.keepPlayerInventory(p);
			}
			TCUtils.clearInventory(p);
			p.setFoodLevel(TCUtils.MAX_FOOD);
			p.setTotalExperience(0);
		}
		TCUtils.restoreHealth(p);

		lobbyLoc.getChunk().load(true);
		p.teleport(lobbyLoc);
		tellPlayer(p, Translation.tr("game.join", gameName));
		p.setGameMode(GameMode.SURVIVAL);
	}

	/**
	 * Adds a joined Temple player to the set of ready players.
	 */
	public void playerReady(Player p)
	{
		readySet.add(p);

		TCUtils.debugMessage("Player " + p.getName() + " is ready. Temple " + temple.templeName + ", Game " + gameName + "(" + gameType + ").");

		if (readySet.equals(playerSet) && !isRunning)
		{
			startGame();
		}
	}

	/**
	 * Prints the list of players currently in the Temple session.
	 */
	public void playerList(Player p)
	{
		if (playerSet.isEmpty())
		{
			return;
		}

		StringBuffer list = new StringBuffer();
		final String SEPARATOR = ", ";
		for (Player player : playerSet)
		{
			list.append(player.getName());
			list.append(SEPARATOR);
		}

		tellPlayer(p, Translation.tr("game.plist", gameName, list.substring(0, list.length() - 2)));
	}

	/**
	 * Resets players Class and Stats
	 */
	public void playerDeath(Player p)
	{
		TemplePlayer tp = TempleManager.templePlayerMap.get(p);

		deadSet.add(p);
		MobArenaClasses.classMap.remove(p);
		tp.tempSet.clear();
		tp.roundDeaths++;


		for( LivingEntity tamedMob : tp.tamedMobSet)
		{
			if(!tamedMob.isDead())
			{
				tamedMob.damage(tamedMob.getMaxHealth());
			}
			tp.tamedMobSet.remove(tamedMob);
		}

		TCUtils.restoreHealth(p);
		p.setFoodLevel(20);
		p.setTotalExperience(0);
		p.setFireTicks(0);
	}

	/**
	 * Prints the list of players who aren't ready.
	 */
	public void notReadyList(Player p)
	{
		if(isRunning)
		{
			tellPlayer(p, Translation.tr("game.inProgress", gameName));
			return;
		}
		if (playerSet.isEmpty())
		{
			return;
		}

		Set<Player> notReadySet = new HashSet<Player>(playerSet);
		notReadySet.removeAll(readySet);

		if (notReadySet.isEmpty())
		{
			tellPlayer(p, Translation.tr("game.everyoneReady", gameName));
			return;
		}

		StringBuffer list = new StringBuffer();
		final String SEPARATOR = ", ";
		for (Player player : notReadySet)
		{
			list.append(player.getName());
			list.append(SEPARATOR);
		}

		tellPlayer(p, Translation.tr("game.rlist", gameName, list.substring(0, list.length() - 2)));
	}

	/**
	 * Forcefully starts the Temple, causing all players in the
	 * playerSet who aren't ready to leave, and starting the
	 * Temple for everyone else.
	 */
	public void forceStart(Player p)
	{
		if (isRunning)
		{
			tellPlayer(p, Translation.tr("game.inProgress", gameName));
			return;
		}
		if (readySet.isEmpty())
		{
			tellPlayer(p, Translation.tr("game.forceStartFail"));
			return;
		}

		Iterator<Player> iterator = playerSet.iterator();
		while (iterator.hasNext())
		{
			Player player = iterator.next();
			if (!readySet.contains(player))
			{
				TempleManager.playerLeave(player);
			}
		}

		if(p != null)
		{
			tellPlayer(p, Translation.tr("game.forcedStart"));
		}
	}

	/**
	 * Forcefully ends the Temple, causing all players to leave and
	 * all relevant sets and maps to be cleared.
	 */
	public void forceEnd(Player p)
	{		
		endGame();

		if(p != null)
		{
			tellPlayer(p, Translation.tr("game.forcedEnd"));
		}
	}

	/* ///////////////////////////////////////////////////////////////////// //

		CLEANUP METHODS

	// ///////////////////////////////////////////////////////////////////// */

	/**
	 * Kills all monsters currently on the Temple floor.
	 */
	public void killMonsters()
	{		
		// Remove all monsters, then clear the Set.
		for (LivingEntity e : monsterSet)
		{
			if (!e.isDead())
			{
				e.remove();
			}
		}
		monsterSet.clear();
	}

	/**
	 * Removes all the blocks on the Temple floor.
	 */
	public void clearTempBlocks()
	{
		// Remove all blocks, then clear the Set.
		for (Block b : tempBlockSet)
		{
			b.setType(Material.AIR);
		}

		tempBlockSet.clear();
	}

	/**
	 * Removes all items and slimes in the Temple region.
	 */
	public void clearEntities()
	{	
		for (Entity e : world.getEntities())
		{
			if(!(e instanceof Player))
			{
				e.remove();
			}
		}
	}

	/* ///////////////////////////////////////////////////////////////////// //

	HIGHSCORE METHODS

	// ///////////////////////////////////////////////////////////////////// */

	protected boolean isPersonalHighScore(Player p, double totalTime)
	{
		for(Pair<String,Double> pair : standings)
		{
			if(pair.a.equals(p.getDisplayName()))
			{	
				if(totalTime < pair.b)
				{
					return true;
				}
				else
				{
					return false;
				}
			}
		}
		return false;
	}

	protected boolean isHighScore(Player p, double totalTime)
	{
		Pair<String,Double> pair = standings.get(0);
		if(pair != null && totalTime < pair.b)
		{
			return true;
		}
		return false;
	}

	protected void sortStandings()
	{
		List<Pair<String,Double>> tempList = new ArrayList<Pair<String,Double>>();
		while(!standings.isEmpty())
		{
			Pair<String,Double> min = null;
			for(int j = 0; j < standings.size(); j++)
			{
				if(min == null)
				{
					min = standings.get(j);
				}
				if (standings.get(j).b < min.b)
				{
					min = standings.get(j);
				}
			}
			standings.remove(min);
			tempList.add(min);
		}
		standings = tempList;
	}

	protected List<Pair<String, Double>> getStandings(Temple temple, String path)
	{
		File configFile = TCUtils.getConfig("temples");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		if(!config.isConfigurationSection("Temples."+temple.templeName+".HighScores."+path))
		{
			return new ArrayList<Pair<String,Double>>();
		}

		ConfigurationSection selection = config.getConfigurationSection("Temples."+temple.templeName+".HighScores."+path);
		List<Pair<String,Double>> standings = new ArrayList<Pair<String,Double>>();

		for(String id : selection.getKeys(false))
		{
			String s = selection.getString(id);
			try
			{
				String[] data = s.split(",");
				standings.add(new Pair<String,Double>(data[0],Double.parseDouble(data[1])));
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return standings;
	}

	protected void saveStandings(Temple temple, String path)
	{
		File configFile = TCUtils.getConfig("temples");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		ConfigurationSection selection;
		if(!config.isConfigurationSection("Temples."+temple.templeName+".HighScores."+path))
		{
			selection = config.createSection("Temples."+temple.templeName+".HighScores."+path);
		}
		else
		{
			selection = config.getConfigurationSection("Temples."+temple.templeName+".HighScores."+path);
		}

		for(int i = 0; i<standings.size();i++)
		{
			if(i >= saveAmount)
			{
				break;
			}
			if(!selection.contains((i+1)+""))
			{
				selection.createSection((i+1)+"");
			}
			Pair<String,Double> pair = standings.get(i);
			selection.set((i+1)+"", pair.a+","+pair.b);
		}

		for(String s : selection.getKeys(false))
		{
			try
			{
				if(Integer.parseInt(s) > saveAmount)
					selection.set(s, null);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		try
		{
			config.save(configFile);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/* ///////////////////////////////////////////////////////////////////// //

	MISC METHODS

	// ///////////////////////////////////////////////////////////////////// */

	/**
	 * Sends a message to a player.
	 */
	public void tellPlayer(Player p, String msg)
	{
		TempleManager.tellPlayer(p, msg);
	}

	/**
	 * Sends a message to all players in the Temple.
	 */
	public void tellAll(String msg)
	{
		for(Player p: playerSet)
		{
			tellPlayer((Player)p, msg);		
		}
	}

	public void hitStartBlock(Player p, int team)
	{
		if(isRunning && !deadSet.contains(p))
		{
			return;
		}

		TemplePlayer tp = TempleManager.templePlayerMap.get(p);
		if(!usingClasses || MobArenaClasses.classMap.containsKey(p))
		{
			if(!isRunning)
			{
				tellPlayer(p, Translation.tr("game.ready"));
				tp.team = team;
				if(tp.team != -1)
				{
					p.getInventory().setHelmet(new ItemStack(Material.WOOL,1,(short)0,(byte)team));
					tellPlayer(p, Translation.tr("game.team", TCUtils.getWoolColor(team)));
				}
				playerReady(p);
				// If a method is installed
			}
			else if(TempleCraft.economy != null)
			{				
				String s = TempleCraft.economy.format(2.0);
				String currencyName = s.substring(s.indexOf(" ") + 1);

				// if player has enough money subtract money from account

				if(TempleCraft.economy.has(p.getName(),rejoinCost))
				{					
					if(rejoinCost > 0)
					{
						tellPlayer(p, Translation.tr("game.transaction", rejoinCost, currencyName));
						TempleCraft.economy.withdrawPlayer(p.getName(),rejoinCost);
					}

					deadSet.remove(p);
					if(tp.currentCheckpoint != null)
					{
						tp.currentCheckpoint.getChunk().load(true);
						p.teleport(tp.currentCheckpoint);
					}
					else
					{
						getPlayerSpawnLoc(tp).getChunk().load(true);
						p.teleport(getPlayerSpawnLoc(tp));
					}

					if(tp.team != -1)
					{
						p.getInventory().setHelmet(new ItemStack(Material.WOOL,1,(short)0,(byte)team));
					}
				}
				else
				{
					TempleManager.tellPlayer(p, Translation.tr("adventure.rejoinFail1"));
					TempleManager.tellPlayer(p, Translation.tr("adventure.rejoinFail2"));
				}
			}
			else
			{
				deadSet.remove(p);
				if(tp.currentCheckpoint != null)
				{
					tp.currentCheckpoint.getChunk().load(true);
					p.teleport(tp.currentCheckpoint);
				}
				else
				{
					getPlayerSpawnLoc(tp).getChunk().load(true);
					p.teleport(getPlayerSpawnLoc(tp));
				}

				if(!usingClasses)
				{
					if(TCUtils.hasPlayerInventory(p.getName()))
					{
						TCUtils.restorePlayerInventory(p);
					}
					TCUtils.keepPlayerInventory(p);
					TCUtils.restoreHealth(p);
				}

				if(tp.team != -1)
				{
					p.getInventory().setHelmet(new ItemStack(Material.WOOL,1,(short)0,(byte)team));
				}
			}
		}
		else
		{
			tellPlayer(p, Translation.tr("game.pickAClass"));
		}
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

	public void hitRewardBlock(Player p, List<ItemStack> itemList)
	{
		rewards.addAll(itemList);

		List<ItemStack> tempList = new ArrayList<ItemStack>();
		for(ItemStack item : itemList)
		{
			if(item != null)
			{
				tempList.add(item);
			}
		}
		int size = tempList.size();
		StringBuilder msg = new StringBuilder();
		if(size == 0)
		{
			tellAll(Translation.tr("game.emptyTreasureFound", p.getDisplayName()));
		}
		else
		{
			tellAll(Translation.tr("game.treasureFound", p.getDisplayName()));
			for(int i = 0; i<size; i++)
			{
				ItemStack item = tempList.get(i);
				if(item != null)
				{
					msg.append(item.getAmount()+" "+TCUtils.getMaterialName(item.getType().name()));
					if(i<size-2)
					{
						msg.append(", ");
					}
					else if(i<size-1)
					{
						msg.append(" "+Translation.tr("and")+" ");
					}
				}
			}
			tellAll(Translation.tr("game.treasureMessage", msg.toString()));
		}
	}

	public void onPlayerMove(PlayerMoveEvent event)
	{
		Player p = event.getPlayer();
		TemplePlayer tp = TempleManager.templePlayerMap.get(p);

		for(Location loc : chatMap.keySet())
		{
			if(tp.tempSet.contains(loc))
			{
				continue;
			}

			String[] msg = chatMap.get(loc);
			int range;
			String s;
			try
			{
				range = Integer.parseInt(msg[1]);
				s = msg[0];
			}
			catch(Exception e)
			{
				range = 5;
				s = msg[0]+msg[1];
			}

			if(s.length() > 0)
			{
				if(TCUtils.distance(loc, p.getLocation()) < range)
				{
					if(msg[0].startsWith("/"))
					{
						tp.tempSet.add(s);
						p.chat(s);
					}
					else
					{
						p.sendMessage(c1+Translation.tr("game.message")+": "+c2+s);
					}
					tp.tempSet.add(loc);
				}
			}
		}

		if(!isRunning)
		{
			return;
		}

		for(Location loc : checkpointMap.keySet())
		{
			if(tp.currentCheckpoint != loc && TCUtils.distance(loc, p.getLocation()) < checkpointMap.get(loc))
			{
				tp.currentCheckpoint = loc;
			}
		}
	}

	public boolean isFull()
	{
		return maxPlayers != -1 && playerSet.size() >= maxPlayers;
	}

	public void onEntityKilledByEntity(LivingEntity killed, Entity killer)
	{

		if (killed instanceof Player)
		{		
			Player p = (Player) killed;

			if (!playerSet.contains(p))
			{
				return;
			}

			if(killer instanceof Player)
			{
				TemplePlayer tp2 = TempleManager.templePlayerMap.get((Player)killer);
				tp2.roundPlayersKilled++;
			}
		}
		else
		{			
			if(killer instanceof Player)
			{
				TempleManager.templePlayerMap.get(killer).roundMobsKilled++;
			}

			monsterSet.remove(killed);
		}
	}

	public String getMessageFromFile(String key)
	{
		YamlConfiguration c = YamlConfiguration.loadConfiguration(messageFile);

		if(c.isSet(key))
		{
			return c.getString(key,"");
		}

		return "";
		//return null;
	}
}
