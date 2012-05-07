package com.msingleton.templecraft;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.msingleton.templecraft.games.Game;
import com.msingleton.templecraft.util.Translation;

public class TCCommands implements CommandExecutor
{
	private TempleCraft plugin;

	public TCCommands(TempleCraft instance)
	{
		plugin = instance;
	}
	/**
	 * Handles all command parsing.
	 * Unrecognized commands return false, giving the sender a list of
	 * valid commands (from plugin.yml).
	 */
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args)
	{	   
		if (args.length == 1 && args[0].equals("checkupdates"))
		{
			try {
				if ((sender == null) || !(sender instanceof Player))
				{
					plugin.newVersionString = TCUtils.updateCheck(plugin.currentVersionString);
					plugin.currentVersion = TCUtils.convertVersion(plugin.currentVersionString);
					plugin.newVersion = TCUtils.convertVersion(plugin.newVersionString);
					if (plugin.newVersion > plugin.currentVersion) {
						plugin.log.warning("[" + plugin.getDescription().getName() + "] Version " + plugin.newVersionString + " is out! You are running " + plugin.currentVersionString);
						plugin.log.warning("[" + plugin.getDescription().getName() + "] Update TempleCraft at: http://dev.bukkit.org/server-mods/templecraft-bootscreen");
					}
					else
					{
						plugin.log.info("[" + plugin.getDescription().getName() + "] You have the newest Version.");
					}
				}
				else
				{
					Player p = (Player) sender;
					if(TCPermissionHandler.hasPermission(p, "templecraft.checkupdates"))
					{
						plugin.newVersionString = TCUtils.updateCheck(plugin.currentVersionString);
						plugin.currentVersion = TCUtils.convertVersion(plugin.currentVersionString);
						plugin.newVersion = TCUtils.convertVersion(plugin.newVersionString);
						if (plugin.newVersion > plugin.currentVersion) {
							p.sendMessage("[" + plugin.getDescription().getName() + "] Version " + plugin.newVersionString + " is out! You are running " + plugin.currentVersionString);
							p.sendMessage("[" + plugin.getDescription().getName() + "] Update TempleCraft at: http://dev.bukkit.org/server-mods/templecraft-bootscreen");
						}
						else
						{
							p.sendMessage("[" + plugin.getDescription().getName() + "] You have the newest Version.");
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				// Ignore exceptions
			}
			return true;
		}

		// Only accept commands from players.
		if ((sender == null) || !(sender instanceof Player))
		{
			plugin.log.warning("[TempleCraft] Only players can use these commands, silly.");
			return true;
		}

		// Cast the sender to a Player object.
		Player p = (Player) sender;

		/* If more than one argument, must be an advanced command.
		 * Only allow operators to access these commands. */
		if (args.length > 1)
		{
			if(advancedCommands(p, args))
			{
				return true;
			}
		}

		// If not exactly one argument, must be an invalid command.
		if (args.length == 1)
		{
			if(basicCommands(p, args[0].toLowerCase()))
			{
				return true;
			}
			else
			{
				try
				{
					TCPermissionHandler.sendResponse(p,Integer.parseInt(args[0]));
					return true;
				}
				catch(Exception e)
				{};
			}
		}

		TCPermissionHandler.sendResponse(p,1);
		return true;
	}

	/**
	 * Handles basic commands.
	 */
	private boolean basicCommands(Player p, String cmd)
	{   

		if(cmd.equals("version"))
		{
			TempleManager.tellPlayer(p,"[TempleCraft] Version " + plugin.getDescription().getVersion());
			
			return true;
		}
		
		TemplePlayer tp = TempleManager.templePlayerMap.get(p);

		if((cmd.equals("join") || cmd.equals("j")) && TCPermissionHandler.hasPermission(p, "templecraft.join"))
		{
			for(Game game : TempleManager.gameSet)
			{
				if(!game.isRunning)
				{
					game.playerJoin(p);
					return true;
				}
			}
			TempleManager.tellPlayer(p, Translation.tr("joinFail"));
			return true;
		}

		if ((cmd.equals("leave") || cmd.equals("l")) && TCPermissionHandler.hasPermission(p, "templecraft.leave"))
		{
			TempleManager.playerLeave(p);
			return true;
		}
		
		if ((cmd.equals("closeall") || cmd.equals("ca")) && TCPermissionHandler.hasPermission(p, "templecraft.forceend"))
		{
			TempleManager.removeAll();
			TempleCraft.TCScheduler.cancelTasks(plugin);
			for(Game game : TempleManager.gameSet)
			{
				game.AbilityTaskIDs.clear();
				game.SpawnTaskIDs.clear();
				game.endGame();
			}
			TCUtils.deleteTempWorlds();
		}

		if (cmd.equals("save"))
		{
			Temple temple = tp.currentTemple;

			if(temple == null || !TCUtils.isTCEditWorld(p.getWorld()))
			{
				TempleManager.tellPlayer(p, Translation.tr("saveFail"));
				return true;
			}

			if(TCPermissionHandler.hasPermission(p, "templecraft.savetemple"))
			{
				if(TCUtils.isTCEditWorld(p.getWorld()))
				{
					temple.saveTemple(p.getWorld(), p);
				}
			}
			return true;
		}

		if ((cmd.equals("playerlist") || cmd.equals("plist")) && TCPermissionHandler.hasPermission(p, "templecraft.playerlist"))
		{
			if(TempleManager.playerSet.contains(p))
			{
				tp.currentGame.playerList(p);
			}
			else
			{
				TempleManager.playerList(p);
			}
			return true;
		}

		if ((cmd.equals("gamelist") || cmd.equals("glist")) && TCPermissionHandler.hasPermission(p, "templecraft.playerlist"))
		{
			if(TempleManager.gameSet.isEmpty())
			{
				TempleManager.tellPlayer(p,Translation.tr("noGamesAvailable"));
				return true;
			}
			p.sendMessage(ChatColor.GREEN+"Game List:");
			for(Game game : TempleManager.gameSet)
			{
				if(game != null)
				{
					if(game.isRunning)
					{
						p.sendMessage(Translation.tr("gameInProgress",game.gameName));
					}
					else
					{
						p.sendMessage(Translation.tr("gameInLobby",game.gameName));
					}
				}
			}
			return true;
		}

		if ((cmd.equals("templelist") || cmd.equals("tlist")) && TCPermissionHandler.hasPermission(p, "templecraft.templelist"))
		{
			if(TempleManager.templeSet.isEmpty())
			{
				TempleManager.tellPlayer(p,Translation.tr("noTemplesAvailable"));
				return true;
			}
			p.sendMessage(Translation.tr("templeList"));
			ArrayList<String> list = new ArrayList<String>();

			Iterator<Temple> it = TempleManager.templeSet.iterator();
			while(it.hasNext())
			{
				Temple temple = it.next();
				StringBuilder line = new StringBuilder();
				if(temple != null)
				{
					if(temple.isSetup)
					{
						line.append(Translation.tr("templeListSetup",temple.templeName));
					} 
					else 
					{
						line.append(Translation.tr("templeListNotSetup",temple.templeName));
					}
					int startLeng = line.length();
					while(line.length()<55-startLeng)
					{
						line.append(" ");
					}
				}
				if(it.hasNext())
				{
					temple = it.next();
					if(temple != null)
					{
						if(temple.isSetup)
						{
							line.append(Translation.tr("templeListSetup",temple.templeName));
						} 
						else 
						{
							line.append(Translation.tr("templeListNotSetup",temple.templeName));
						}
					}
				}
				list.add(line.toString());
			}

			for(String s : list)
			{
				p.sendMessage(s);
			}
			return true;
		}

		if ((cmd.equals("ready") || cmd.equals("notready"))  && TCPermissionHandler.hasPermission(p, "templecraft.ready"))
		{
			Game game = tp.currentGame;
			if(game != null && game.playerSet.contains(p))
			{
				game.notReadyList(p);
			}
			else
			{
				TempleManager.notReadyList(p);
			}
			return true;
		}

		if (cmd.equals("converttemples") && TCPermissionHandler.hasPermission(p, "templecraft.converttemples"))
		{
			for(Temple temple : TempleManager.templeSet)
			{
				TempleManager.tellPlayer(p, Translation.tr("convertingTemple", temple.templeName));
				TCUtils.convertTemple(p, temple);
			}

			TempleManager.tellPlayer(p, Translation.tr("convertingComplete"));
			return true;
		}

		return false;
	}

	private boolean advancedCommands(Player p, String[] args)
	{		
		//Room commands
		String cmd = args[0].toLowerCase();
		String arg = args[1].toLowerCase();	

		TemplePlayer tp = TempleManager.templePlayerMap.get(p);

		if (cmd.equals("new") && TCPermissionHandler.hasPermission(p, "templecraft.newtemple"))
		{
			TempleManager.tellPlayer(p, Translation.tr("newTemple", arg));
			if(args.length == 2)
			{
				TCUtils.newTemple(p, arg, null, true);
			}
			else if(args.length == 3)
			{
				TCUtils.newTemple(p, arg, args[2].toLowerCase(), true);
			}
			return true;
		}

		if (cmd.equals("newgame") && TCPermissionHandler.hasPermission(p, "templecraft.newgame"))
		{
			TCUtils.newGameCommand(p, args);
			return true;
		}

		if (cmd.equals("delete") && TCPermissionHandler.hasPermission(p, "templecraft.deletetemple"))
		{
			Temple temple = TCUtils.getTempleByName(arg);

			if(temple == null)
			{
				TempleManager.tellPlayer(p, Translation.tr("templeDNE", arg));
				return true;
			}

			TCUtils.removeTemple(temple);
			TempleManager.tellPlayer(p, Translation.tr("templeDeleted", arg));
			return true;
		}

		if (cmd.equals("rename") && TCPermissionHandler.hasPermission(p, "templecraft.renametemple"))
		{
			Temple temple;
			String result;
			if(args.length == 2)
			{
				temple = tp.currentTemple;
				result = arg;
				if(temple == null)
				{
					TempleManager.tellPlayer(p, Translation.tr("mustBeEditing"));
					return true;
				}
			} 
			else if(args.length == 3)
			{
				temple = TCUtils.getTempleByName(arg);
				result = args[2];
				if(temple == null)
				{
					TempleManager.tellPlayer(p, Translation.tr("templeDNE", arg));
					return true;
				}
			} 
			else 
			{
				return true;
			}
			Temple newtemple = TCUtils.getTempleByName(result);
			if(newtemple == null)
			{
				TCUtils.renameTemple(temple, arg);
			}
			else
			{
				TempleManager.tellPlayer(p, Translation.tr("templeAE", result));
				return true;
			}
			TempleManager.tellPlayer(p, Translation.tr("templeRenamed", temple.templeName, result));
			return true;
		}

		if (cmd.equals("setmaxplayers") && TCPermissionHandler.hasPermission(p, "templecraft.setmaxplayers"))
		{
			Temple temple;
			String number;
			if(args.length == 2)
			{
				temple = tp.currentTemple;
				number = arg;
			} 
			else if(args.length == 3)
			{
				temple = TCUtils.getTempleByName(arg);
				number = args[2];
			} 
			else 
			{
				return true;
			}
			if(temple == null)
			{
				TempleManager.tellPlayer(p, Translation.tr("templeDNE", arg));
				return true;
			}
			try
			{
				int value = Integer.parseInt(number);
				TCUtils.setTempleMaxPlayers(temple, value);
				TempleManager.tellPlayer(p, Translation.tr("templeMaxPlayersSet",temple.templeName, value));
			}
			catch(Exception e)
			{
				TempleManager.tellPlayer(p, Translation.tr("invalidInteger"));
			}
			return true;
		}

		if (cmd.equals("worldtotemple") && TCPermissionHandler.hasPermission(p, "templecraft.worldtotemple"))
		{
			if(TCUtils.getTempleByName(arg) != null)
			{
				TempleManager.tellPlayer(p, Translation.tr("templeAE"));
				return true;
			}

			if(args.length == 2)
			{
				TCUtils.newTemple(p, arg, null, false);
			}
			else if(args.length == 3)
			{
				TCUtils.newTemple(p, arg, args[2].toLowerCase(), false);
			}

			Temple temple = TCUtils.getTempleByName(arg);
			TCRestore.saveTemple(p.getWorld(), temple);

			TempleManager.tellPlayer(p, Translation.tr("worldConverted",arg));
			return true;
		}
		
		if ((cmd.equals("finishloc") || cmd.equals("fl")) && TCPermissionHandler.hasPermission(p, "templecraft.edittemple"))
		{
			Temple temple = TCUtils.getTempleByName(arg);

			if(temple == null)
			{
				TempleManager.tellPlayer(p, Translation.tr("templeDNE", arg));
				return true;
			}
			
			TCUtils.setFinishLocation(temple, p.getLocation());
			TempleManager.tellPlayer(p, Translation.tr("templeFinishLocSet",arg));
			return true;
		}

		if ((cmd.equals("textadd") || cmd.equals("ta")) && TCPermissionHandler.hasPermission(p, "templecraft.edittemple"))
		{
			try
			{
				File messageFile = TCUtils.getConfig("messages");
				YamlConfiguration c = YamlConfiguration.loadConfiguration(messageFile);
				String text = "";
				for(int i = 2; i < args.length; i++)
				{
					text +=args[i] + " ";
				}
				c.set(arg, text);
				try
				{
					c.save(messageFile);
					TempleManager.tellPlayer(p, "Text added.");
				}
				catch (IOException e)
				{
					TempleManager.tellPlayer(p, "Could not perform command.");
					e.printStackTrace();
				}
			}
			catch (Exception e) 
			{
				TempleManager.tellPlayer(p, "Could not perform command.");
			}
			return true;
		}

		if (cmd.equals("edit") && TCPermissionHandler.hasPermission(p, "templecraft.edittemple"))
		{			
			Temple temple = TCUtils.getTempleByName(arg);

			if(temple == null)
			{
				TempleManager.tellPlayer(p, Translation.tr("templeDNE", arg));
				return true;
			}

			TempleManager.tellPlayer(p, Translation.tr("preparingTemple", temple.templeName));
			TCUtils.editTemple(p, temple);
			return true;
		}

		if (cmd.equals("add") && TCPermissionHandler.hasPermission(p, "templecraft.addplayer"))
		{			
			Temple temple = tp.currentTemple;

			if(temple == null || !temple.editorSet.contains(p))
			{
				TempleManager.tellPlayer(p, Translation.tr("mustBeEditing"));
				return true;
			}

			if(!temple.ownerSet.contains(p.getName()) && !TCPermissionHandler.hasPermission(p, "templecraft.editall"))
			{
				TempleManager.tellPlayer(p, Translation.tr("mustBeOwner"));
				return true;
			}

			String playerName = null;
			for(Player player : TempleManager.server.getOnlinePlayers())
			{
				if(player.getName().toLowerCase().startsWith(arg))
				{
					playerName = player.getName();
					break;
				}
			}

			if(playerName == null)
			{
				TempleManager.tellPlayer(p, Translation.tr("playerNotFound"));
			} 
			else
			{
				if(temple.addEditor(playerName))
				{
					TempleManager.tellPlayer(p, Translation.tr("playerAdded", playerName, temple.templeName));
				}
				else
				{
					TempleManager.tellPlayer(p, Translation.tr("playerAlreadyAdded", playerName));
				}
			}
			return true;
		}

		if (cmd.equals("remove") && TCPermissionHandler.hasPermission(p, "templecraft.removeplayer"))
		{			
			Temple temple = tp.currentTemple;

			if(temple == null || !temple.editorSet.contains(p))
			{
				TempleManager.tellPlayer(p, Translation.tr("mustBeEditing"));
				return true;
			}

			if(!temple.ownerSet.contains(p.getName()) && !TCPermissionHandler.hasPermission(p, "templecraft.editall"))
			{
				TempleManager.tellPlayer(p, Translation.tr("mustBeOwner"));
				return true;
			}

			String playerName = null;
			for(Player player : TempleManager.server.getOnlinePlayers())
			{
				if(player.getName().toLowerCase().startsWith(arg))
				{
					playerName = player.getName();
					break;
				}
			}

			if(playerName == null)
			{
				TempleManager.tellPlayer(p, Translation.tr("playerNotFound"));
			} 
			else 
			{
				if(temple.removeEditor(playerName))
				{
					TempleManager.tellPlayer(p, Translation.tr("playerRemoved", playerName, temple.templeName));
				}
				else
				{
					TempleManager.tellPlayer(p, Translation.tr("playerAlreadyRemoved", playerName, temple.templeName));
				}
			}
			return true;
		}

		if (cmd.equals("findsigblocks") && TCPermissionHandler.hasPermission(p, "templecraft.findsigblocks"))
		{
			try
			{
				int radius = Integer.parseInt(arg);
				Temple temple = tp.currentTemple;

				if(temple == null)
				{
					TempleManager.tellPlayer(p, Translation.tr("mustBeEditing"));
					return true;
				}

				TCUtils.getSignificantBlocks(p, radius);
				return true;
			} 
			catch(Exception e)
			{
				TempleManager.tellPlayer(p, Translation.tr("invalidInteger"));
				return true;
			}
		}

		if(!(cmd.equals("join") || cmd.equals("j") || cmd.equals("forcestart") || cmd.equals("forceend")))
			return false;

		//Game commands
		String gamename = args[1].toLowerCase();
		Game game = TCUtils.getGameByName(gamename);

		if(game == null)
		{
			TempleManager.tellPlayer(p, Translation.tr("gameDNE", gamename));
			return true;
		}

		if ((cmd.equals("join") || cmd.equals("j")) && TCPermissionHandler.hasPermission(p, "templecraft.join"))
		{
			game.playerJoin(p);
			return true;
		}

		// tc forcestart <game>
		if (cmd.equals("forcestart") && TCPermissionHandler.hasPermission(p, "templecraft.forcestart"))
		{
			game.forceStart(p);
			return true;
		}		

		if (cmd.equals("forceend") && TCPermissionHandler.hasPermission(p, "templecraft.forceend"))
		{
			game.forceEnd(p);
			return true;
		}

		return false;
	}
}