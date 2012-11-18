package com.bukkit.xarinor.templecraft.scoreboards;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

/**
* RecentScoreBoard.java
* This work is dedicated to the public domain.
* 
* @author Xarinor
* @author bootscreen
* @author msingleton
*/
public class RecentScoreBoard extends ScoreBoard {

	/**
	 * Constructor
	 * 
	 * @param id		-Score board id
	 * @param p1		-Location 1
	 * @param p2		-Location 2
	 * @param templeName-Temple name
	 * @param gameMode	-Game mode (Adventure,..)
	 */
	public RecentScoreBoard(String id, Location p1, Location p2, String templeName, String gameMode) {
		super(id,p1,p2,templeName,gameMode);
		type = "recent";
		save();
	}

	/**
	 * Updates the scores for this ScoreBoard
	 * 
	 * TODO Do better
	 * 
	 * @param strings
	 */
	public void updateScores(List<String> strings) {
		for(int i = 0; i<signs.size();i++) {
			if(signs.get(i).getY() == p1.getY()) {
				shiftDown(signs.get(i),1);
				if(strings.size() > i) {
					signs.get(i).setLine(1, strings.get(i));
					signs.get(i).update(true);
				}
			}
		}
	}

	/**
	 * @param sign
	 * @param start
	 */
	private void shiftDown(Sign sign, int start)
	{
		Block b = sign.getBlock();
		if(b.getRelative(0, -1, 0).getState() instanceof Sign) {
			Sign newSign = (Sign)b.getRelative(0, -1, 0).getState();
			if(inRegion(b.getRelative(0, -1, 0).getLocation())) {
				shiftDown(newSign,0);
				newSign.setLine(0, sign.getLine(3));
				newSign.update(true);
			}
		}
		for(int i = 3;i > start;i--) {
			sign.setLine(i, sign.getLine(i-1));
			sign.update(true);
		}
	}
}