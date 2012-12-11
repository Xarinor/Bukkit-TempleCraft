package com.bukkit.xarinor.templecraft.listeners;

//TODO TESTING
//import org.bukkit.entity.Entity;
//import org.bukkit.entity.LivingEntity;
//import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

//import com.bukkit.xarinor.templecraft.TCUtils;
//import com.bukkit.xarinor.templecraft.TempleCraft;
//import com.bukkit.xarinor.templecraft.custommobs.CustomMob;
//import com.bukkit.xarinor.templecraft.games.Game;
//import com.bukkit.xarinor.templecraft.tasks.AbilityTask;
//TODO TESTING
//import com.bukkit.xarinor.templecraft.util.MobSpawnProperties;

/**
* TCChunkListener.java
* This work is dedicated to the public domain.
* 
* @author Xarinor
* @author bootscreen
* @author msingleton
*/
public class TCChunkListener implements Listener {	
	/**
	 * Records chunk loading in a templecraft world
	 * 
	 * @param event
	 */
	@EventHandler
	public void onChunkLoad(ChunkLoadEvent event) {
		//TODO HUGE TESTING
//		if(TCUtils.isTCWorld(event.getWorld())) {
//			Game game = TCUtils.getGameByWorld(event.getWorld());
//			
//			if(game != null && !game.tempMobLoc.isEmpty()) {
//				for(Entity e : game.tempMobLoc.get(event.getChunk())) {
//					if(e instanceof LivingEntity) {
//						Entity le = event.getWorld().spawnEntity(e.getLocation(), e.getType());
//						CustomMob cmold = game.mobManager.getMob(e);
//						
//						if(cmold != null) {
//							CustomMob cmnew = new CustomMob(le);
//							cmnew.setHealth(cmold.getMaxHealth());
//							cmnew.setAbilitys(cmold.getAbilitys());
//							cmnew.setDMGMultiplikator(cmold.getDMGMultiplikator());
//							cmnew.setSize(cmold.getSize());
//							cmnew.setSpawnProperties(cmold.getSpawnProperties());
//
//							int id = TempleCraft.TCScheduler.scheduleAsyncRepeatingTask(TempleCraft.TCPlugin, new AbilityTask(game, cmnew), 100L, 100L);
//							AbilityTask.taskID = id;
//							game.AbilityTaskIDs.put(cmnew,id);
//							
//							if(le instanceof Slime) {
//								Slime slime = (Slime) le;
//								slime.setSize(cmold.getSize());
//							}
//							game.mobManager.RemoveMob(cmold);
//						}
//					}
//				}
//				game.tempMobLoc.remove(event.getChunk());			
//			}
//		}
	}

	/**
	 * Records chunk unloadings
	 * 
	 * @param event
	 */
	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent event) {
//		if(TCUtils.isTCWorld(event.getWorld())) {
//			Game game = TCUtils.getGameByWorld(event.getWorld());
//			
//			if(game != null) {
//				if(!game.isEnding) {
//					game.tempMobLoc.put(event.getChunk(), event.getChunk().getEntities());	
//					TODO HUGE TESTING
//						CustomMob cmob = game.mobManager.getMob(e);
//						Assume this keeps skills
//						if(cmob != null) {
//							TempleCraft.TCScheduler.cancelTask(game.AbilityTaskIDs.get(cmob));
//							game.AbilityTaskIDs.remove(cmob);
//						}
//					}
//				}
//			}
//		}
	}
}
