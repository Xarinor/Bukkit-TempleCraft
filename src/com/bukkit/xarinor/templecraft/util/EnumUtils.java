package com.bukkit.xarinor.templecraft.util;

/**
 * EnumUtils.java
 * This work is dedicated to the public domain.
 * 
 * @author Boorscreen
 * @author msingleton
 */
public class EnumUtils {
	
	/**
	 * A common method for all enums since they can't have another base class
	 * 
	 * @param <T> Enum type
	 * @param c enum type. All enums must be all caps.
	 * @param string case insensitive
	 * @return corresponding enum, or null
	 */
	public static <T extends Enum<T>> T getEnumFromString(Class<T> c, String string) {
		if( c != null && string != null ) {
			try {
				return Enum.valueOf(c, string.trim().toUpperCase());
			} catch(IllegalArgumentException ex)  { }
		}
		return null;
	}
	
	/**
	 * A common method for all enums since they can't have another base class
	 * 
	 * @param <T> Enum type
	 * @param c enum type. All enums must be all caps.
	 * @param string case insensitive
	 * @param <T> Enum type Default
	 * @return corresponding enum, or Default
	 */
	public static <T extends Enum<T>> T getEnumFromString(Class<T> c, String string, T def) {
	    if( c != null && string != null ) {
	        try {
	            return Enum.valueOf(c, string.trim().toUpperCase());
	        } catch(IllegalArgumentException ex) { }
	    }
	    return def;
	}
}
