package com.msingleton.templecraft.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import com.msingleton.templecraft.custommobs.CustomMobAbility;
import com.msingleton.templecraft.games.Game;

public class MobSpawnProperties 
{
	protected Game game;
	protected Location loc;
	protected EntityType mob;
	protected int range;
	protected int size;
	protected int health;
	protected int dmgmulti;
	protected String abilitys;
	protected int count;
	protected long time;
	protected boolean isbossmob;
	protected List<Pair<CustomMobAbility,Integer>> abilities_random = new ArrayList<Pair<CustomMobAbility,Integer>>();
	protected List<Pair<CustomMobAbility,Integer>> abilities_rotation = new ArrayList<Pair<CustomMobAbility,Integer>>();

	public MobSpawnProperties()
	{
		game = null;
		loc = null;
		mob = null;
		size = -1;
		range = 20;
		health = 0;
		dmgmulti = 1;
		abilitys = "";
		count = 1;
		time = 0;
		isbossmob = false;
		abilities_random.clear();
		abilities_rotation.clear();
	}

	public void setGame(Game game)
	{
		this.game = game;
	}

	public Game getGame()
	{
		return game;
	}

	public void setLocation(Location loc)
	{
		this.loc = loc;
	}

	public Location getLocation()
	{
		return loc;
	}

	public void setEntityType(EntityType mob)
	{
		this.mob = mob;
	}

	public EntityType getEntityType()
	{
		return mob;
	}

	public void setSize(int size)
	{
		this.size = size;
	}

	public int getSize()
	{
		return size;
	}

	public void setRange(int range)
	{
		this.range = range;
	}

	public int getRange()
	{
		return range;
	}

	public void setHealth(int health)
	{
		this.health = health;
	}

	public int getHealth()
	{
		return health;
	}

	public void setDMGMulti(int dmgmulti)
	{
		this.dmgmulti = dmgmulti;
	}

	public int getDMGMulti()
	{
		return dmgmulti;
	}

	public void setAbilitys(String abilitys)
	{
		this.abilitys = abilitys;
	}

	public String getAbilitys()
	{
		return abilitys;
	}

	public void setCount(int count)
	{
		this.count = count;
	}

	public int getCount()
	{
		return count;
	}

	public void setTime(long time)
	{
		this.time = time;
	}

	public long getTime()
	{
		return time;
	}

	public boolean isIsbossmob() 
	{
		return isbossmob;
	}

	public void setIsbossmob(boolean isbossmob)
	{
		this.isbossmob = isbossmob;
	}

	public List<Pair<CustomMobAbility,Integer>> getAbilities_random() {
		return abilities_random;
	}

	public void setAbilities_random(List<Pair<CustomMobAbility,Integer>> abilities_random) {
		this.abilities_random = abilities_random;
	}

	public List<Pair<CustomMobAbility,Integer>> getAbilities_rotation() {
		return abilities_rotation;
	}

	public void setAbilities_rotation(List<Pair<CustomMobAbility,Integer>> abilities_rotation) {
		this.abilities_rotation = abilities_rotation;
	}
}
