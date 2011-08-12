package com.msingleton.templecraft;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Creature;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerInventoryEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;


import com.sk89q.worldedit.EditSession;

public class TCPlayerListener  extends PlayerListener{

	public TCPlayerListener(TempleCraft templeCraft) {		
	}
	
	public void onPlayerMove(PlayerMoveEvent event)
    {
		if (!event.getPlayer().getWorld().equals(TempleManager.world))
            return;
		
		if(!TempleManager.isEnabled || TempleManager.templeSet.isEmpty())
			return;
		
		Player p = event.getPlayer();
		TemplePlayer tp = TempleManager.templePlayerMap.get(p);
		Temple temple = tp.currentTemple;
		
		if(temple == null)
			return;

		if(!temple.world.equals(TempleManager.world))
			return;
		
		if(!temple.isRunning)
			return;
		
		for(Location loc : temple.checkpointMap.keySet()){
			if(tp.currentCheckpoint != loc && TCUtils.distance(loc, p.getLocation()) < temple.checkpointMap.get(loc)){
				tp.currentCheckpoint = loc;
			}
		}
		
		Set<Location> tempLocs = new HashSet<Location>();
		for(Location loc : temple.mobSpawnpointMap.keySet()){
			if(TCUtils.distance(loc, p.getLocation()) < 20){
				tempLocs.add(loc);
			}
		}
		for(Location loc : tempLocs)
			temple.SpawnMobs(loc, temple.mobSpawnpointMap.remove(loc));
    }

	 public void onPlayerInteract(PlayerInteractEvent event){    
		 
		 if(!TempleManager.isEnabled)
				return;
		 
        Player p = event.getPlayer();
        TemplePlayer tp = TempleManager.templePlayerMap.get(p);
        Action a = event.getAction();
			
        // Signs
        if (event.hasBlock() && event.getClickedBlock().getState() instanceof Sign){        
	        // Cast the block to a sign to get the text on it.
	        Sign sign = (Sign) event.getClickedBlock().getState();
	        handleSign(p, a, sign);
		 }
        
        if (!TempleManager.playerSet.contains(p))
            return;
        
        Temple temple = tp.currentTemple;
    	
        if(temple == null || !temple.isRunning)
        	return;
        
        // Gold block
        if (event.hasBlock() && temple.endBlockSet.contains(event.getClickedBlock()) && event.getClickedBlock().getTypeId() == 41)
        {
            if (temple.playerSet.contains(p))
            {
            	temple.readySet.add(p);
            	if(temple.readySet.equals(temple.playerSet)){
            		temple.endTemple();
            	} else {
            		temple.tellPlayer(p, "You are ready to leave!");
            	}
            }
            else
            {
                temple.tellPlayer(p, "WTF!? Get out of here!");
            }
            return;
        }
	}
	 
	private void handleSign(Player p, Action a, Sign sign) {
		String Line1 = sign.getLine(0);
        String Line2 = sign.getLine(1);
    	if(!Line1.equals("[TempleCraft]") && !Line1.equals("[TC]"))
    		return;
    	Temple temple = TCUtils.getTempleByName(Line2.toLowerCase());
    	if(temple != null){
    		temple.playerJoin(p);
    	}
    }
}
