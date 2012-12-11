package com.bukkit.xarinor.templecraft.custommobs;

import org.bukkit.entity.LivingEntity;
import net.minecraft.server.v1_4_5.EntityLiving;

import org.bukkit.craftbukkit.v1_4_5.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_4_5.inventory.CraftItemStack;

public class CustomMobEquipment
{
	private static org.bukkit.inventory.ItemStack toCraftBukkit(org.bukkit.inventory.ItemStack stack)
	{
		if (!(stack instanceof CraftItemStack))
			return new CraftItemStack(stack);
		else
			return stack;
	}
	
	public static void setWeapon(LivingEntity mob, org.bukkit.inventory.ItemStack item)
	{
		EntityLiving ent = ((CraftLivingEntity) mob).getHandle();
		net.minecraft.server.v1_4_5.ItemStack itemStack = ((CraftItemStack) toCraftBukkit(item)).getHandle();
		ent.setEquipment(0, itemStack);
	}

	public static void setHelmet(LivingEntity mob, org.bukkit.inventory.ItemStack item)
	{
		EntityLiving ent = ((CraftLivingEntity) mob).getHandle();
		net.minecraft.server.v1_4_5.ItemStack itemStack = ((CraftItemStack) toCraftBukkit(item)).getHandle();
		ent.setEquipment(4, itemStack);
	}

	public static void setChestplate(LivingEntity mob, org.bukkit.inventory.ItemStack item)
	{
		EntityLiving ent = ((CraftLivingEntity) mob).getHandle();
		net.minecraft.server.v1_4_5.ItemStack itemStack = ((CraftItemStack) toCraftBukkit(item)).getHandle();
		ent.setEquipment(3, itemStack);
	}

	public static void setPants(LivingEntity mob, org.bukkit.inventory.ItemStack item)
	{
		EntityLiving ent = ((CraftLivingEntity) mob).getHandle();
		net.minecraft.server.v1_4_5.ItemStack itemStack = ((CraftItemStack) toCraftBukkit(item)).getHandle();
		ent.setEquipment(2, itemStack);
	}

	public static void setBoots(LivingEntity mob, org.bukkit.inventory.ItemStack item)
	{
		EntityLiving ent = ((CraftLivingEntity) mob).getHandle();
		net.minecraft.server.v1_4_5.ItemStack itemStack = ((CraftItemStack) toCraftBukkit(item)).getHandle();
		ent.setEquipment(1, itemStack);
	}

	public static org.bukkit.inventory.ItemStack getWeapon(LivingEntity mob)
	{
		EntityLiving ent = ((CraftLivingEntity) mob).getHandle();
		return new CraftItemStack(ent.getEquipment(0));
	}

	public static org.bukkit.inventory.ItemStack getHelmet(LivingEntity mob)
	{
		EntityLiving ent = ((CraftLivingEntity) mob).getHandle();
		return new CraftItemStack(ent.getEquipment(1));
	}

	public static org.bukkit.inventory.ItemStack getChestplate(LivingEntity mob)
	{
		EntityLiving ent = ((CraftLivingEntity) mob).getHandle();
		return new CraftItemStack(ent.getEquipment(2));
	}

	public static org.bukkit.inventory.ItemStack getPants(LivingEntity mob)
	{
		EntityLiving ent = ((CraftLivingEntity) mob).getHandle();
		return new CraftItemStack(ent.getEquipment(3));
	}

	public static org.bukkit.inventory.ItemStack getBoots(LivingEntity mob)
	{
		EntityLiving ent = ((CraftLivingEntity) mob).getHandle();
		return new CraftItemStack(ent.getEquipment(4));
	}
}
