package com.bukkit.xarinor.templecraft;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.bukkit.xarinor.templecraft.util.Translation;

/**
* TCEnabledCommands.java
* This work is dedicated to the public domain.
* 
* @author Xarinor
* @author bootscreen
* @author msingleton
*/
public class TCEnabledCommands implements Listener {
	private TempleCraft plugin;
	
	/**
	 * @param instance TempleCraft
	 */
	public TCEnabledCommands(TempleCraft instance) {
		plugin = instance;
	}

	/**
	 * Commands cannot be used inside of temples
	 * Exceptions are OP 
	 * 
	 * @param event -Player-command
	 */
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		Player p = event.getPlayer();
		TemplePlayer tp = TempleManager.templePlayerMap.get(p);
		
		if(p.isOp()) {
			return;
		}
		
		if (!TempleManager.playerSet.contains(p)) {
			return;
		}
		
		String msg = event.getMessage();
		String[] args = msg.split(" ");
		
		if(!tp.tempSet.isEmpty() && tp.tempSet.contains(msg)) {
			tp.tempSet.remove(msg);
			return;
		}
		
		if (plugin.ENABLED_COMMANDS.contains(msg.trim()) || plugin.ENABLED_COMMANDS.contains(args[0])) {
			return;
		}
		
		event.setCancelled(true);
		TempleManager.tellPlayer(p, Translation.tr("commandDisabled"));
	}
}