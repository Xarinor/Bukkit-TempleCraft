package com.bukkit.xarinor.templecraft.tasks;

import org.bukkit.entity.Player;

import com.bukkit.xarinor.templecraft.games.Game;

/**
 * EndTask.java
 * This work is dedicated to the public domain.
 * 
 * @author Xarinor
 * @author bootscreen
 * @author msingleton
 */
public class EndTask implements Runnable {
	public Game game;
	public Player player;

	/**
	 * Constructor
	 * 
	 * @param game
	 * @param p
	 */
	public EndTask(Game game, Player p) {
		this.game = game;
		this.player = p;

	}
	
	public static int taskID;

	@Override
	public void run() {
		try {
			game.hitEndBlock(player);
		}
		catch (Exception e) {
			// TODO: handle exception
		}

	}
}
