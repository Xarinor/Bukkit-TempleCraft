package com.msingleton.templecraft.listeners;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

import com.msingleton.templecraft.TCPermissionHandler;
import com.msingleton.templecraft.TCUtils;
import com.msingleton.templecraft.Temple;
import com.msingleton.templecraft.TempleManager;
import com.msingleton.templecraft.TemplePlayer;
import com.msingleton.templecraft.games.Game;
import com.msingleton.templecraft.scoreboards.ScoreBoard;
import com.msingleton.templecraft.util.Pair;
import com.msingleton.templecraft.util.Translation;


/**
 * This listener serves as a protection class. Blocks within
 * the game world cannot be destroyed, and blocks can only
 * be placed by a participant in the current arena session.
 */
//public class TCBlockListener extends BlockListener
public class TCBlockListener implements Listener
{

	/**
	 * Prevents blocks from breaking if block protection is on.
	 */	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event)
	{	
		Player p = event.getPlayer();
		TemplePlayer tp = TempleManager.templePlayerMap.get(p);
		Block b = event.getBlock();

		Temple temple = tp.currentTemple;

		if(b.getState() instanceof Sign)
		{
			Sign sign = (Sign) b.getState();
			ScoreBoard sb = TempleManager.SBManager.getScoreBoardBySign(sign);
			if(sb != null)
			{
				if(TCPermissionHandler.hasPermission(p, "templecraft.placesigns"))
				{
					TempleManager.SBManager.deleteScoreBoard(sb);
					p.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(323,1));
					return;
				}
				else
				{
					TempleManager.tellPlayer(p, Translation.tr("blockListener.cantBreakSB"));
					event.setCancelled(true);
					return;
				}
			}
		}

		for(ScoreBoard sb : TempleManager.SBManager.scoreBoards)
		{
			if(sb.inRegion(b.getLocation()))
			{
				TempleManager.tellPlayer(p, Translation.tr("blockListener.cantBreakSBB", sb.id));
				event.setCancelled(true);
				return;
			}
		}

		if(temple == null)
		{
			return;
		}

		boolean cancel = true;

		Game game = tp.currentGame;

		if(game == null)
		{
			return;
		}

		if (TempleManager.breakable.contains(b.getTypeId()))
		{
			cancel = false;
		}

		if (game.isRunning && TempleManager.playerSet.contains(event.getPlayer()))
		{

			if(!game.breakableLocs.isEmpty())
			{
				for (Pair<Location, Set<Integer>> pr : game.breakableLocs)
				{
					if(pr.a.equals(b.getLocation()))
					{
						if(pr.b.contains(-1) || pr.b.contains(b.getTypeId()))
						{
							game.tempBlockSet.add(b);
							Material type = b.getType();

							// Make sure to add the top parts of doors.
							if (type == Material.WOODEN_DOOR || type == Material.IRON_DOOR_BLOCK)
							{
								game.tempBlockSet.add(b.getRelative(0,1,0));
							}

							cancel = false;
						}
					}
				}
			}

			if(!TempleManager.breakable.isEmpty())
			{
				if(TempleManager.breakable.contains(b.getTypeId()))
				{
					game.tempBlockSet.add(b);
					Material type = b.getType();

					// Make sure to add the top parts of doors.
					if (type == Material.WOODEN_DOOR || type == Material.IRON_DOOR_BLOCK)
					{
						game.tempBlockSet.add(b.getRelative(0,1,0));
					}

					cancel = false;
				}
			}
			else
			{			
				game.tempBlockSet.add(b);
				Material type = b.getType();

				// Make sure to add the top parts of doors.
				if (type == Material.WOODEN_DOOR || type == Material.IRON_DOOR_BLOCK)
				{
					game.tempBlockSet.add(b.getRelative(0,1,0));
				}

				return;
			}
		}

		if (game.tempBlockSet.remove(b))
		{
			return;
		}

		if(TempleManager.dropBlocks && !cancel)
		{
			return;
		}

		if(!cancel)
		{
			b.setTypeId(0);
		}

		event.setCancelled(true);
	}

	/**
	 * Adds player-placed blocks to a set for removal and item
	 * drop purposes. If the block is placed within the arena
	 * region, cancel the event if protection is on.
	 */
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event)
	{		
		Player p = event.getPlayer();
		TemplePlayer tp = TempleManager.templePlayerMap.get(p);
		Block b = event.getBlock();

		Temple temple = tp.currentTemple;

		if(temple == null)
		{
			return;
		}

		// if player places significant block while editing, record it
		if(TCUtils.isTCEditWorld(p.getWorld()))
		{
			for(int i : Temple.coordBlocks)
			{
				if(b.getTypeId() == i)
				{
					temple.coordLocSet.add(b.getLocation());
				}
			}
			return;
		}

		Game game = tp.currentGame;

		if(game == null)
		{
			return;
		}

		if (!game.isRunning)
		{
			return;
		}

		if (game.isRunning && TempleManager.playerSet.contains(event.getPlayer()))
		{

			if(!game.placeableLocs.isEmpty())
			{
				for (Pair<Location, Set<Integer>> pr : game.placeableLocs)
				{
					if(pr.a.equals(b.getLocation()))
					{
						if(pr.b.contains(-1) || pr.b.contains(b.getTypeId()))
						{
							game.tempBlockSet.add(b);
							Material type = b.getType();

							// Make sure to add the top parts of doors.
							if (type == Material.WOODEN_DOOR || type == Material.IRON_DOOR_BLOCK)
							{
								game.tempBlockSet.add(b.getRelative(0,1,0));
							}

							return;
						}
					}
				}
			}

			if(!TempleManager.placeable.isEmpty())
			{
				if(TempleManager.placeable.contains(b.getTypeId()))
				{
					game.tempBlockSet.add(b);
					Material type = b.getType();

					// Make sure to add the top parts of doors.
					if (type == Material.WOODEN_DOOR || type == Material.IRON_DOOR_BLOCK)
					{
						game.tempBlockSet.add(b.getRelative(0,1,0));
					}

					return;
				}
			}
			else
			{			
				game.tempBlockSet.add(b);
				Material type = b.getType();

				// Make sure to add the top parts of doors.
				if (type == Material.WOODEN_DOOR || type == Material.IRON_DOOR_BLOCK)
				{
					game.tempBlockSet.add(b.getRelative(0,1,0));
				}

				return;
			}
		}

		event.setCancelled(true);
	}

	@EventHandler
	public void onSignChange(SignChangeEvent event)
	{
		Player p = event.getPlayer();
		TemplePlayer tp = TempleManager.templePlayerMap.get(p);

		Temple temple = tp.currentTemple;

		if(temple == null)
		{
			if(event.getLine(0).equals("[TCSB]") || event.getLine(0).equals("[TCS]") || event.getLine(0).equals("[TC]") || event.getLine(0).equals("[TCB]") || event.getLine(0).equals("[TempleCraft]"))
			{
				if(!TCPermissionHandler.hasPermission(p, "templecraft.placesigns"))
				{
					TempleManager.tellPlayer(p, Translation.tr("blockListener.cantPlaceEntrance"));
					event.setCancelled(true);
					return;
				}
			}
			if(event.getLine(0).equals("[TCSB]"))
			{
				Location loc	  = event.getBlock().getLocation();
				String templeName = event.getLine(1).toLowerCase();
				String gameMode   = event.getLine(2).toLowerCase();
				String type	   = event.getLine(3).toLowerCase();
				TempleManager.SBManager.newScoreBoard(loc, loc, templeName, gameMode, type);
			}
		}
	}
}