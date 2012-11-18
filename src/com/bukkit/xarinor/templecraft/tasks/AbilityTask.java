package com.bukkit.xarinor.templecraft.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.bukkit.xarinor.templecraft.TempleCraft;
import com.bukkit.xarinor.templecraft.custommobs.CustomMob;
import com.bukkit.xarinor.templecraft.custommobs.CustomMobAbility;
import com.bukkit.xarinor.templecraft.games.Game;

/**
 * AbilityTask.java
 * This work is dedicated to the public domain.
 * 
 * @author Xarinor
 * @author bootscreen
 * @author msingleton
 */
public class AbilityTask implements Runnable {
	public Game game;
	public CustomMob custommob;
	private List<CustomMobAbility> abilitys = new ArrayList<CustomMobAbility>();
	private CustomMobAbility ability = null;

	/**
	 * Constructor
	 * 
	 * @param game
	 * @param cm
	 */
	public AbilityTask(Game game, CustomMob cm) {
		this.game = game;
		this.custommob = cm;
		this.abilitys = cm.getAbilitys();
	}

	/**
	 * Constructor with ability set
	 * 
	 * TODO .
	 * 
	 * @param game
	 * @param cm
	 * @param ability
	 */
	public AbilityTask(Game game, CustomMob cm, CustomMobAbility ability) {
		this.game = game;
		this.custommob = cm;
		this.ability = ability;
	}


	public static int taskID;

	/**
	 * Runs the custom abilities
	 */
	@Override
	public void run() {
		try {
			if(!game.isRunning || custommob.getEntity().isDead() || custommob.getHealth() <= 0) {
				TempleCraft.TCScheduler.cancelTask(taskID);
				game.AbilityTaskIDs.remove(custommob);
			} else {
				if(!abilitys.isEmpty()) {
					Random random = new Random();
					int index = random.nextInt(abilitys.size());
					CustomMobAbility cma = abilitys.get(index);
					cma.run(game,custommob.getEntity());
				} else if(ability != null) {
					ability.run(game,custommob.getEntity());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
