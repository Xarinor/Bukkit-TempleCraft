package com.bukkit.xarinor.templecraft.listeners;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;

import com.bukkit.xarinor.templecraft.TempleManager;
import com.bukkit.xarinor.templecraft.TemplePlayer;
import com.bukkit.xarinor.templecraft.games.Game;
import com.bukkit.xarinor.templecraft.util.Translation;

/**
* TCInventoryListener.java
* This work is dedicated to the public domain.
* 
* @author Xarinor
* @author bootscreen
* @author msingleton
*/
public class TCInventoryListener implements Listener {

	/**
	 * When a player clicks on an inventory slot
	 * 
	 * @param event - Inventory click event
	 */
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		//TODO Method?
		HumanEntity he = event.getWhoClicked();
		Player p = (Player) he;
		TemplePlayer tp = TempleManager.templePlayerMap.get(p);
		Game game = tp.currentGame;

		if(game == null) {
			return;
		}

		// TODO Buggy? PVP.. xD
		if (event.getSlotType().equals(SlotType.ARMOR) && event.getCurrentItem().getType().equals(Material.WOOL)) {
			TempleManager.tellPlayer(p, Translation.tr("playerListener.denyHelmet"));
			event.setCancelled(true);
		}
	}
}
