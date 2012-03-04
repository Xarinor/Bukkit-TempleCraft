package com.msingleton.templecraft;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.hero.HeroManager;
import com.msingleton.templecraft.listeners.TCBlockListener;
import com.msingleton.templecraft.listeners.TCInventoryListener;
import com.msingleton.templecraft.listeners.TCDamageListener;
import com.msingleton.templecraft.listeners.TCDisconnectListener;
import com.msingleton.templecraft.listeners.TCMonsterListener;
import com.msingleton.templecraft.listeners.TCPlayerListener;
import com.msingleton.templecraft.listeners.TCTeleportListener;
import com.msingleton.templecraft.util.MobArenaClasses;
import com.msingleton.templecraft.util.Translation;

/**
 * TempleCraft
 *
 * @author bootscreen
 * @author msingleton
 */
public class TempleCraft extends JavaPlugin
{
	/* Array of commands used to determine if a command belongs to TempleCraft
	 * or Mean Admins. */
	public Logger log;
	public List<String> ENABLED_COMMANDS;
	public double newVersion;
    public double currentVersion;
	public String newVersionString;
    public String currentVersionString;
	public static Permission permission = null;
	public static Economy economy = null;
	public static HeroManager heroManager;
	public static String language;
	public static String fileExtention = ".tcf";
	public static ChatColor c1 = ChatColor.DARK_AQUA;
	public static ChatColor c2 = ChatColor.WHITE;
	public static ChatColor c3 = ChatColor.GREEN;
	
	public void onEnable()
	{	 	
		PluginDescriptionFile pdfFile = this.getDescription();
		
		currentVersionString = pdfFile.getVersion();
        currentVersion = TCUtils.convertVersion(currentVersionString);

		log = getServer().getLogger();

		// Create event listeners.
		PluginManager pm = getServer().getPluginManager();

		// Initialize convenience variables in ArenaManager.
		TempleManager.init(this);

        // Schedule to check the version every 30 minutes for an update. This is to update the most recent 
        // version so if an admin reconnects they will be warned about newer versions.
        // Thanks Sleaker for the permission to use his updatecheck code from vault 
		this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {

            @Override
            public void run() {
                try {
                    newVersionString = TCUtils.updateCheck(currentVersionString);
                    currentVersion = TCUtils.convertVersion(currentVersionString);
                    newVersion = TCUtils.convertVersion(newVersionString);
                    if (newVersion > currentVersion) {
                        log.warning("[" + getDescription().getName() + "] TempleCraft " + newVersionString + " is out! You are running: TempleCraft " + currentVersionString);
                        log.warning("[" + getDescription().getName() + "] Update TempleCraft at: http://dev.bukkit.org/server-mods/templecraft-bootscreen");
                    }
                } catch (Exception e) {
                	e.printStackTrace();
                    // ignore exceptions
                }
            }

        }, 0, 432000);

		setupTranslations();
		setupPermissions();
		setupEconomy();
		setupHeroes();

		ENABLED_COMMANDS = TCUtils.getEnabledCommands();

		// Bind the /tc and /tcraft commands to MACommands.
		getCommand("tct").setExecutor(new TCCommands(this));

		pm.registerEvents(new TCEnabledCommands(this), this);
		pm.registerEvents(new TCPlayerListener(), this);
		pm.registerEvents(new MobArenaClasses(this), this);
		pm.registerEvents(new TCTeleportListener(), this);
		pm.registerEvents(new TCDisconnectListener(this), this);
		pm.registerEvents(new TCBlockListener(), this);
		pm.registerEvents(new TCDamageListener(), this);
		pm.registerEvents(new TCMonsterListener(this), this);
		pm.registerEvents(new TCInventoryListener(), this);
		
		/*
		 *  OLD Event System
		 * 
		
		PlayerListener commandListener	  = new TCEnabledCommands(this);
		PlayerListener playerListener	   = new TCPlayerListener(this);
		InventoryListener inventoryListener = new TCInventoryListener();
		PlayerListener maListener		   = new MobArenaClasses(this);
		PlayerListener teleportListener	 = new TCTeleportListener(this);
		PlayerListener discListener		 = new TCDisconnectListener(this);
		BlockListener  blockListener		= new TCBlockListener(this);
		EntityListener damageListener	   = new TCDamageListener(this);
		EntityListener monsterListener	  = new TCMonsterListener(this);

		// TO-DO: PlayerListener to check for kills/deaths.

		// Register events.

		pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, commandListener, Priority.Monitor, this);
		pm.registerEvent(Event.Type.PLAYER_BUCKET_EMPTY, playerListener,Priority.Normal,  this);
		pm.registerEvent(Event.Type.PLAYER_MOVE,	  playerListener,   Priority.Normal,  this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT,  playerListener,   Priority.Normal,  this);
		pm.registerEvent(Event.Type.CUSTOM_EVENT,	 inventoryListener,   Priority.Normal,  this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT,  maListener,	   Priority.Normal,  this);
		pm.registerEvent(Event.Type.PLAYER_TELEPORT,  teleportListener, Priority.Normal,  this);
		pm.registerEvent(Event.Type.PLAYER_QUIT,	  discListener,	 Priority.Normal,  this);
		pm.registerEvent(Event.Type.PLAYER_KICK,	  discListener,	 Priority.Normal,  this);
		pm.registerEvent(Event.Type.PLAYER_JOIN,	  discListener,	 Priority.Normal,  this);
		pm.registerEvent(Event.Type.BLOCK_BREAK,	  blockListener,	Priority.Normal,  this);
		pm.registerEvent(Event.Type.BLOCK_PLACE,	  blockListener,	Priority.Normal,  this);
		pm.registerEvent(Event.Type.SIGN_CHANGE,	  blockListener,	Priority.Normal,  this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGE,	damageListener,   Priority.Normal,  this);
		pm.registerEvent(Event.Type.ENTITY_DEATH,	 damageListener,   Priority.Normal,  this);
		pm.registerEvent(Event.Type.CREATURE_SPAWN,   monsterListener,  Priority.Normal,  this);
		pm.registerEvent(Event.Type.ENTITY_EXPLODE,   monsterListener,  Priority.Normal,  this);
		pm.registerEvent(Event.Type.ENTITY_COMBUST,   monsterListener,  Priority.Normal,  this);
		pm.registerEvent(Event.Type.ENTITY_TARGET,	monsterListener,  Priority.Normal,  this);*/

		System.out.println(Translation.tr("enableMessage", pdfFile.getName(), pdfFile.getVersion()));
	}

	private void setupTranslations()
	{			
		File configFile = TCUtils.getConfig("config");
		language = TCUtils.getString(configFile, "settings.language", "en-US");
		Translation.reload(new File(getDataFolder(), "templecraft-"+language+".csv"));

		if(Translation.getVersion()<1)
		{
			TCUtils.copyFromJarToDisk("templecraft-"+language+".csv", getDataFolder());
			log.log(Level.INFO, "[TempleCraft] copied new translation file for "+language+" to disk.");
			Translation.reload(new File(getDataFolder(), "templecraft-"+language+".csv"));
		}
	}

	public void onDisable()
	{	
		//permissionHandler = null;
		TempleManager.SBManager.save();
		TempleManager.removeAll();
		TCUtils.deleteTempWorlds();
		TCUtils.cleanConfigFiles();
	}

	private Boolean setupPermissions()
	{
		RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null)
		{
			permission = permissionProvider.getProvider();
		}
		return (permission != null);
	}

	private Boolean setupEconomy()
	{
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null)
		{
			economy = economyProvider.getProvider();
		}

		return (economy != null);
	}

	private void setupHeroes()
	{
		Plugin heroes = this.getServer().getPluginManager().getPlugin("Heroes");
		if (heroes == null)
		{
			return;
		}

		heroManager = ((Heroes) heroes).getHeroManager();
		if(heroManager != null)
		{
			System.out.println("[TempleCraft] Hooked into Heroes");
		}
	}

	public File getPluginFile()
	{
		return getFile();
	}
}