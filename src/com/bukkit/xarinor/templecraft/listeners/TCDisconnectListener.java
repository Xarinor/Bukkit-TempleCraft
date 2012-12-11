package com.bukkit.xarinor.templecraft.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.bukkit.xarinor.templecraft.TCUtils;
import com.bukkit.xarinor.templecraft.TempleCraft;
import com.bukkit.xarinor.templecraft.TempleManager;
import com.bukkit.xarinor.templecraft.TemplePlayer;

/**
* TCDisconnectListener.java
* This work is dedicated to the public domain.
* 
* This listener acts when a player is kicked or disconnected
 * from the server. If 15 seconds pass, and the player hasn't
 * reconnected, the player is forced to leave the arena.
* 
* @author Xarinor
* @author bootscreen
* @author msingleton
*/
public class TCDisconnectListener implements Listener {
	private TempleCraft plugin;

	/**
	 * Constructor
	 * 
	 * @param instance
	 */
	public TCDisconnectListener(TempleCraft instance) {
		plugin = instance;
	}
	
	/**
	 * When a player leaves the server
	 * 
	 * @param event -Player disc event
	 */
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if(!TempleManager.isEnabled) {
			return;
		}

		Player p = event.getPlayer();
		if(TCUtils.hasPlayerInventory(p.getName())) {
			TCUtils.restorePlayerInventory(p);
		}
		if (TempleManager.playerSet.contains(p)) {
			TempleManager.playerLeave(p);
		}
		if(TempleManager.locationMap.containsKey(p)) {
			TempleManager.locationMap.get(p).getChunk().load(true);
			p.teleport(TempleManager.locationMap.get(p));
		}
	}

	/**
	 * When a player gets kicked from the server
	 * 
	 * @param event -Player kick event
	 */
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event)
	{
		if(!TempleManager.isEnabled)
		{
			return;
		}

		Player p = event.getPlayer();
		if(TCUtils.hasPlayerInventory(p.getName())) {
			TCUtils.restorePlayerInventory(p);
		}
		if (TempleManager.playerSet.contains(p)) {
			TempleManager.playerLeave(p);
		}
		if(TempleManager.locationMap.containsKey(p)) {
			TempleManager.locationMap.get(p).getChunk().load(true);
			p.teleport(TempleManager.locationMap.get(p));
		}
	}

	/**
	 * When a player joins the server
	 * 
	 * @param event -Player join event
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {		
		if(!TempleManager.isEnabled) {
			return;
		}

		final Player p = event.getPlayer();
		TempleManager.templePlayerMap.put(p, new TemplePlayer(p));

		// TODO Upgrade UpdateNotification
		
		if (!TempleManager.checkUpdates) {
			return;
		}

		if (!p.isOp()) {
			return;
		}

        try {
        	plugin.newVersionString = TCUtils.updateCheck(plugin.currentVersionString);
            plugin.currentVersion = TCUtils.convertVersion(plugin.currentVersionString);
            plugin.newVersion = TCUtils.convertVersion(plugin.newVersionString);
            if (plugin.newVersion > plugin.currentVersion) {
                p.sendMessage(plugin.newVersionString + " is out! Installed: " + plugin.currentVersionString);
                p.sendMessage("Remember your admin to update TempleCraft ;)");
            }
        } catch (Exception e) {
            // Ignore exceptions
        }
	}
}