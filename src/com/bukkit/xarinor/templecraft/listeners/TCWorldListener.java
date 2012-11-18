package com.bukkit.xarinor.templecraft.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import com.bukkit.xarinor.templecraft.TCUtils;
import com.bukkit.xarinor.templecraft.TempleCraft;

/**
* TCWorldListener.java
* This work is dedicated to the public domain.
* 
* @author Xarinor
* @author bootscreen
* @author msingleton
*/
public class TCWorldListener implements Listener {
	
	/**
	 * When a world unloads
	 * 
	 * TODO Nasty stuff... knee deep in flesh-eating-bugs (other plugins :D)
	 * 
	 * @param event -World unload event
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public void onWorldUnload(WorldUnloadEvent event) {
		//debug
		//System.out.println("[TempleCraft] WorldUnloadEvent - started");
		
		if (!event.isCancelled() && TCUtils.isTCWorld(event.getWorld())) {
			//BUG: IOException Stream Closed and ConcurrentModifications errors
			//WorldManager.clearWorldReference(event.getWorld());
			
			if(TempleCraft.worldguard != null) {
				TempleCraft.worldguard.getGlobalRegionManager().unload(event.getWorld().getName());
			}
		}
	}

	/**
	 * When a world loads
	 * 
	 * @param event -World load event
	 */
	@EventHandler
	public void onWorldLoad(WorldLoadEvent event) {
		if (TCUtils.isTCWorld(event.getWorld())) {
			if(TempleCraft.worldguard != null) {
				TempleCraft.worldguard.getGlobalRegionManager().unload(event.getWorld().getName());
			}
		}
	}
}