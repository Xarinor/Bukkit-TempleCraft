package com.bukkit.xarinor.templecraft.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.bukkit.xarinor.templecraft.TCPermissionHandler;
import com.bukkit.xarinor.templecraft.TCUtils;
import com.bukkit.xarinor.templecraft.Temple;
import com.bukkit.xarinor.templecraft.TempleManager;
import com.bukkit.xarinor.templecraft.TemplePlayer;
import com.bukkit.xarinor.templecraft.games.Game;
import com.bukkit.xarinor.templecraft.util.Translation;

/**
* TCTeleportListener.java
* This work is dedicated to the public domain.
* 
* This listener prevents players from warping out of the arena, if
* they are in the arena session.
* 
* TODO Fix the bug that causes the message when people get stuck in walls.
* 
* @author Xarinor
* @author bootscreen
* @author msingleton
*/
public class TCTeleportListener implements Listener {
	
	/**
	 * When a player teleports
	 * 
	 * @param event -Player tp event
	 */
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		Player p = event.getPlayer();
		TemplePlayer tp = TempleManager.templePlayerMap.get(p);
		Location to = event.getTo();
		Location from = event.getFrom();
		
		if(event.getCause().equals(TeleportCause.COMMAND)) {
			if(TCUtils.isTCWorld(to.getWorld())) {
				Game game = TCUtils.getGameByWorld(to.getWorld());
				if(game != null) {
					if(TCPermissionHandler.hasPermission(p, "templecraft.teleport")) {
						TempleManager.tellPlayer(p, Translation.tr("teleportListener.deny2"));
						event.setCancelled(true);
					}
				}
			}
		}

		if (!TempleManager.playerSet.contains(p)) {
			return;
		}

		Temple temple = tp.currentTemple;

		if(temple == null) {
			return;
		}

		if ((!TCUtils.isTCWorld(from.getWorld()) && TCUtils.isTCWorld(to.getWorld())) || to.getWorld().equals(p.getWorld())) {
			return;
		}

		TempleManager.tellPlayer(p, Translation.tr("teleportListener.deny"));
		event.setCancelled(true);
	}
}