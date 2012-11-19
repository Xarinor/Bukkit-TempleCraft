package com.bukkit.xarinor.templecraft;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
* TCPermissionHandler.java
* This work is dedicated to the public domain.
* 
* @author Xarinor
* @author bootscreen
* @author msingleton
*/
public class TCPermissionHandler {
	public static String[] allPermissions = {
		"tc","newgame","join","leave","ready","templelist","gamelist","playerlist","forcestart","forceend","checkupdates","newtemple",
		"edittemple","deletetemple","savetemple","renametemple","worldtotemple","addplayer","removeplayer","setmaxplayers","findsigblocks"
	};
	public static ChatColor c1 = TempleCraft.c1;
	public static ChatColor c2 = TempleCraft.c2;
	public static String[] descriptions = {
		c1+"/tct (pageNumber)"+c2+"	   - Templecraft Help",
		c1+"/tct newgame <temple> <mode>"+c2+" - Create a new game.",
		c1+"/tct join (game)"+c2+" - Join <game>.",
		c1+"/tct leave"+c2+"		  - Leave current Temple.",
		c1+"/tct ready"+c2+"		 - List players who aren't ready.",
		c1+"/tct tlist"+c2+"			- List Temples.",
		c1+"/tct glist"+c2+"			- List Games.",
		c1+"/tct plist"+c2+"			- List players in Games.",
		c1+"/tct forcestart <game>"+c2+" - Manually start a Game.",
		c1+"/tct forceend <game>"+c2+"   - Manually end a Game.",
		c1+"/tct closeall"+c2+"   - close all running games.",
		c1+"/tct checkupdates"+c2+"		 - Check for new updates.",
		c1+"/tct new <temple> (generator)"+c2+" - Creates a new Temple.",
		c1+"/tct edit <temple>"+c2+"	 - Edit an existing temple.",
		c1+"/tct delete <temple>"+c2+"  - Delete an existing temple.",
		c1+"/tct save"+c2+"			   - Save the current temple.",
		c1+"/tct rename <newName>"+c2+" - Renames the temple to <newName>.",
		c1+"/tct worldtotemple <temple>"+c2+" - Save current World as a temple.",
		c1+"/tct textadd <shortname> <text>"+c2+"	 - Add text for a [TCML] sign.",
		c1+"/tct textdel <shortname>"+c2+"	 - Remove long text of a [TCML] sign.",
		c1+"/tct finishloc <temple> [del]"+c2+"	 - Set the finish-location to your current position (must be out of temple), or delete the location with [del].",
		c1+"/tct add <player>"+c2+"	 - Allow a player to edit your temple.",
		c1+"/tct remove <player>"+c2+" - Disallow a player to edit your temple.",
		c1+"/tct setmaxplayers <integer>"+c2+" - Set max players per game.",
		c1+"/tct setmaxdeath <integer>"+c2+" - Set max deaths per player per game.",
		c1+"/tct findsigblocks <radius>"+c2+" - Find all important blocks near you in the temple."
	};
	public static int entsPerPage = 7;
	public static String[] editBasics = {
		"newtemple","edittemple","savetemple","addplayer","removeplayer"
	};
	
	/**
	 * Checks a player for permissions
	 * @param player -Player
	 * @param permission -Permission
	 * @return
	 */
	public static boolean hasPermission(Player player, String permission) {
		if(!permission.contains("templecraft.")) {
			permission = "templecraft."+permission;
		}
		if(TempleCraft.permission != null) {
			for(String command : editBasics) {
				if(permission.contains(command)) {
					if(TempleCraft.permission.has(player, "templecraft.editbasics")) {
						return true;
					}
				}
			}
			return TempleCraft.permission.has(player, permission);
		} else {
			return player.hasPermission(permission);
		}
	}

	/**
	 * Sends a respond to a player
	 * 
	 * @param player
	 * @param page
	 */
	public static void sendResponse(Player player, int page) {
		List<String> personalPerms = getPerms(player);
		int totalPages = (int)Math.ceil(personalPerms.size()/(double)entsPerPage);
		if(page<0 || page>totalPages) {
			player.sendMessage("Page "+page+" not found");
			return;
		}
		for(int i = 0;i<3;i++) {
			player.sendMessage("");
		}
		player.sendMessage(c1+"-----------"+c2+" TempleCraft Help ("+page+"/"+totalPages+") "+c1+"-----------");
		int start = (page-1)*entsPerPage;
		int end = page*entsPerPage;
		for(int i = start; i < end; i++) {
			if(personalPerms.size() > i) {
				player.sendMessage(personalPerms.get(i));
			} else {
				player.sendMessage("");
			}
		}
	}

	/**
	 * gets the permissions of a player
	 * 
	 * @param player
	 * @return
	 */
	private static List<String> getPerms(Player player) {
		List<String> result = new ArrayList<String>(); 
		for(int i = 0; i < allPermissions.length;i++) {
			if(hasPermission(player,allPermissions[i])) {
				result.add(descriptions[i]);
			}
		}
		return result;
	}
}
