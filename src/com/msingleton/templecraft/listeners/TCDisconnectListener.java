package com.msingleton.templecraft.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerKickEvent;

import com.msingleton.templecraft.TCUtils;
import com.msingleton.templecraft.TempleCraft;
import com.msingleton.templecraft.TempleManager;
import com.msingleton.templecraft.TemplePlayer;

/**
 * This listener acts when a player is kicked or disconnected
 * from the server. If 15 seconds pass, and the player hasn't
 * reconnected, the player is forced to leave the arena.
 */
//public class TCDisconnectListener extends PlayerListener
public class TCDisconnectListener implements Listener
{
	private TempleCraft plugin;

	public TCDisconnectListener(TempleCraft instance)
	{
		plugin = instance;
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		if(!TempleManager.isEnabled)
		{
			return;
		}

		Player p = event.getPlayer();
		if(TCUtils.hasPlayerInventory(p.getName()))
		{
			TCUtils.restorePlayerInventory(p);
		}
		if (TempleManager.playerSet.contains(p))
		{
			TempleManager.playerLeave(p);
		}
		if(TempleManager.locationMap.containsKey(p))
		{
			p.teleport(TempleManager.locationMap.get(p));
		}
	}

	@EventHandler
	public void onPlayerKick(PlayerKickEvent event)
	{
		if(!TempleManager.isEnabled)
		{
			return;
		}

		Player p = event.getPlayer();
		if(TCUtils.hasPlayerInventory(p.getName()))
		{
			TCUtils.restorePlayerInventory(p);
		}
		if (TempleManager.playerSet.contains(p))
		{
			TempleManager.playerLeave(p);
		}
		if(TempleManager.locationMap.containsKey(p))
		{
			p.teleport(TempleManager.locationMap.get(p));
		}
	}

	
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{		
		if(!TempleManager.isEnabled)
		{
			return;
		}

		final Player p = event.getPlayer();
		TempleManager.templePlayerMap.put(p, new TemplePlayer(p));

		// TODO: UpdateNotification überarbeiten
		
		if (!TempleManager.checkUpdates)
		{
			return;
		}

		if (!p.isOp())
		{
			return;
		}

        try {
        	plugin.newVersionString = TCUtils.updateCheck(plugin.currentVersionString);
            plugin.currentVersion = TCUtils.convertVersion(plugin.currentVersionString);
            plugin.newVersion = TCUtils.convertVersion(plugin.newVersionString);
            if (plugin.newVersion > plugin.currentVersion) {
                p.sendMessage(plugin.newVersionString + " is out! You are running " + plugin.currentVersionString);
                p.sendMessage("Update TempleCraft at: http://dev.bukkit.org/server-mods/templecraft-bootscreen");
            }
        } catch (Exception e) {
            // Ignore exceptions
        }
	}
}