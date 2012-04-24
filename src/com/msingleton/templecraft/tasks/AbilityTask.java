package com.msingleton.templecraft.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.msingleton.templecraft.TempleCraft;
import com.msingleton.templecraft.custommobs.CustomMob;
import com.msingleton.templecraft.custommobs.CustomMobAbility;
import com.msingleton.templecraft.games.Game;

public class AbilityTask implements Runnable 
{
	public Game game;
	public CustomMob custommob;
	private List<CustomMobAbility> abilitys = new ArrayList<CustomMobAbility>();

	public AbilityTask(Game game, CustomMob cm)
	{
		this.game = game;
		this.custommob = cm;
		this.abilitys = cm.getAbilitys();
	}


	public static int taskID;

	@Override
	public void run() 
	{
		try
		{
			if(!game.isRunning || custommob.getLivingEntity().isDead() || custommob.getHealth() <= 0)
			{
				TempleCraft.TCScheduler.cancelTask(taskID);
				game.AbilityTaskIDs.remove(custommob);
			}
			else
			{
				Random random = new Random();
				int index = random.nextInt(abilitys.size());
				CustomMobAbility cma = abilitys.get(index);
				cma.run(game,custommob.getLivingEntity());
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}
