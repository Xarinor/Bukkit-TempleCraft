package com.msingleton.templecraft.listeners;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;

import com.msingleton.templecraft.TempleManager;
import com.msingleton.templecraft.TemplePlayer;
import com.msingleton.templecraft.games.Game;
import com.msingleton.templecraft.util.Translation;

//public class TCInventoryListener extends InventoryListener {
public class TCInventoryListener implements Listener
{
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event)
	{
		HumanEntity he = event.getWhoClicked();
		Player p = (Player) he;
		TemplePlayer tp = TempleManager.templePlayerMap.get(p);
		Game game = tp.currentGame;

		if(game == null)
		{
			return;
		}

		if (event.getSlotType().equals(SlotType.ARMOR) && event.getCurrentItem().getType().equals(Material.WOOL))
		{
			TempleManager.tellPlayer(p, Translation.tr("playerListener.denyHelmet"));
			event.setCancelled(true);
		}
	}
}
