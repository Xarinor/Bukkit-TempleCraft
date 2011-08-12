package com.msingleton.templecraft;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

import com.iConomy.iConomy;
import com.iConomy.system.Holdings;


/**
 * This listener prevents players from sharing class-specific
 * items (read: cheating) before the arena session starts.
 */
// TO-DO: Merge with MASignListener and MAReadyListener into MALobbyListener
public class TCLobbyListener extends PlayerListener
{    
    public TCLobbyListener(TempleCraft instance)
    {
    }

    /**
     * Players can only drop items when the arena session has started.
     */
    public void onPlayerDropItem(PlayerDropItemEvent event)
    {
    }
    
    /**
     * Adds liquid blocks to the blockset when players empty their buckets.
     */
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event)
    {        	
    	
    	if (!TempleManager.playerSet.contains(event.getPlayer()))
            return;
        
        if (!TempleManager.isEnabled)
        {
            event.getBlockClicked().getFace(event.getBlockFace()).setTypeId(0);
            event.setCancelled(true);
            return;
        }

        Block liquid = event.getBlockClicked().getFace(event.getBlockFace());
        for(Temple temp : TempleManager.templeSet)
        	if(temp.playerSet.contains(event.getPlayer()))
        		temp.tempBlockSet.add(liquid);
    }
    
    /**
     * Checks if the player hits an iron block or a sign, or if the player
     * is trying to use an item.
     */
    public void onPlayerInteract(PlayerInteractEvent event)
    {        
        Player p = event.getPlayer();
        TemplePlayer tp = TempleManager.templePlayerMap.get(p);
        
        Temple temple = tp.currentTemple;
        
        if(temple == null)
        	return;
        
        // Iron block
        if (event.hasBlock() && temple.lobbyBlockSet.contains(event.getClickedBlock()))
        {
        	if(!temple.isRunning){
	            temple.tellPlayer(p, "You have been flagged as ready!");
	            temple.playerReady(p);
        	} else {
        		Holdings balance = iConomy.getAccount(p.getName()).getHoldings();
        		if(TempleCraft.iConomy == null || balance.hasEnough(temple.rejoinCost)){
    				temple.readySet.add(p);
    				if(tp.currentCheckpoint != null)
    					p.teleport(tp.currentCheckpoint);
    				else
    					p.teleport(temple.templeLoc);
    				if(TCUtils.hasPlayerInventory(p.getName()))
    					TCUtils.restorePlayerInventory(p);
    				TCUtils.keepPlayerInventory(p);
    				if(TempleCraft.iConomy != null && temple.rejoinCost > 0){
    					String msg = ChatColor.GOLD + "" + temple.rejoinCost+" gold"+ChatColor.WHITE+" has been subtracted from your account.";
    					temple.tellPlayer(p, msg);
    					balance.subtract(temple.rejoinCost);
	            	}
        		} else {
        			TempleManager.tellPlayer(p, "You do not have enough gold to rejoin.");
        		}
        	}
            return;
        }
        
        // Sign is handled by playerListener
    }
}