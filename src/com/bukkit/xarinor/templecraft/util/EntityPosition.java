package com.bukkit.xarinor.templecraft.util;

import java.io.Serializable;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * EntityPosition.java
 * This work is dedicated to the public domain.
 * 
 * @author creadri
 */
@SuppressWarnings("serial")
public class EntityPosition implements Serializable {
	private double x;
	private double y;
	private double z;
	private String world;
	private float yaw;
	private float pitch;

	/**
	 * Constructor
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param world
	 * @param yaw
	 * @param pitch
	 */
	public EntityPosition(double x, double y, double z, String world, float yaw, float pitch) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	/**
	 * Gets the position of a single entity
	 * 
	 * @param location
	 */
	public EntityPosition(Location location) {
		this.x = location.getX();
		this.y = location.getY();
		this.z = location.getZ();
		this.world = location.getWorld().getName();
		this.yaw = location.getYaw();
		this.pitch = location.getPitch();
	}

	/**
	 * Get a location in a key world
	 * 
	 * @param world
	 * @return
	 */
	public Location getLocation(World world) {
		return new Location(world, x, y, z, yaw, pitch);
	}

	/**
	 * @return
	 */
	public float getPitch() {
		return pitch;
	}

	/**
	 * @param pitch
	 */
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	/**
	 * @return
	 */
	public String getWorld() {
		return world;
	}

	/**
	 * @param world
	 */
	public void setWorld(String world) {
		this.world = world;
	}

	/**
	 * @return
	 */
	public double getX() {
		return x;
	}

	/**
	 * @param x
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * @return
	 */
	public double getY() {
		return y;
	}

	/**
	 * @param y
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * @return
	 */
	public float getYaw() {
		return yaw;
	}

	/**
	 * @param yaw
	 */
	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	/**
	 * @return
	 */
	public double getZ() {
		return z;
	}

	/**
	 * @param z
	 */
	public void setZ(double z) {
		this.z = z;
	}
}