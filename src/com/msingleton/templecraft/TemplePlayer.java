package com.msingleton.templecraft;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.msingleton.templecraft.games.Game;
import com.msingleton.templecraft.listeners.TCPlayerListener;
import com.msingleton.templecraft.util.Translation;

/**
* TemplePlayer.java
* This work is dedicated to the public domain.
* 
* @author Xarinor
* @author bootscreen
* @author msingleton
*/
public class TemplePlayer {
	
	public Set<Object> tempSet = new HashSet<Object>();
	public List<ItemStack> rewards = new ArrayList<ItemStack>();
	public int roundMobsKilled;
	public int roundPlayersKilled;
	public int roundGold;
	public int roundDeaths;
	public int team;
	public Sign sensedSign;
	public boolean canAutoTele;
	protected int ownedTemples;
	protected String name;
	protected Timer playerTimer = new Timer();
	protected TimerTask enterTempleTask;
	protected TimerTask counter;
	protected int count;
	public Location currentCheckpoint;
	public Temple currentTemple;
	public Game currentGame;
	public Set<LivingEntity> tamedMobSet = new HashSet<LivingEntity>();

	public TemplePlayer() {}

	/**
	 * Constructor
	 * 
	 * @param player
	 */
	public TemplePlayer(Player player) {
		name		 = player.getName();
		ownedTemples = getOwnedTemples();
		team		 = -1;
		canAutoTele  = false;
		resetRoundStats();
	}

	/**
	 * Get all owned temples of that TemplePlayer
	 * @return
	 */
	private int getOwnedTemples() {
		int ownedTemples = 0;
		for(Temple temple : TempleManager.templeSet) {
			if(temple.owners.contains(name.toLowerCase())) {
				ownedTemples++;
			}
		}
		return ownedTemples;
	}

	/*protected void displayStats() {
		//TO DO: this
		player.sendMessage("----TempleCraft Stats----");
		player.sendMessage(ChatColor.BLUE+"Mobs Killed: "+ChatColor.WHITE+roundMobsKilled);
		player.sendMessage(ChatColor.GOLD+"Gold Collected: "+ChatColor.WHITE+roundGold);
		player.sendMessage(ChatColor.DARK_RED+"Deaths: "+ChatColor.WHITE+roundDeaths);
		resetRoundStats();
	}*/

	/**
	 * resets Stats for this TemplePlayer
	 */
	public void resetRoundStats() {
		roundGold		   = 0;
		roundMobsKilled	 = 0;
		roundPlayersKilled  = 0;
		roundDeaths		 = 0;
	}

	/**
	 * Starts the enter timer for player
	 * @param player
	 */
	public void startEnterTimer(final Player player)
	{
		final TemplePlayer tp = TempleManager.templePlayerMap.get(player);
		
		try {
			count = Integer.parseInt(tp.sensedSign.getLine(3));
		} catch (Exception e) {
			count = 5;
		}
		
		if(count == 0) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			TCPlayerListener.handleSignClicked(player,tp.sensedSign);
			tp.sensedSign = null;
			tp.canAutoTele = false;
		} else {
			//TODO check timer task
			counter = new TimerTask()  {
				public void run() {
					if(count <= 3 && count > 0) {
						TempleManager.tellPlayer(player, Translation.tr("enteringTemple",count));
					} else if(count <= 0) {
						TCPlayerListener.handleSignClicked(player,tp.sensedSign);
						tp.sensedSign = null;
						tp.canAutoTele = false;
						cancel();
					}
					count--;
				}
			};
	
			playerTimer.scheduleAtFixedRate(counter, 0, 1000);
		}
	}

	/**
	 * Stops the enter timer
	 */
	public void stopEnterTimer() {
		if(counter != null) {
			counter.cancel();
		}
	}

	/**
	 * Resets the enter timer to full
	 * @param player
	 */
	public void resetEnterTimer(Player player) {
		stopEnterTimer();
		startEnterTimer(player);
	}
}
