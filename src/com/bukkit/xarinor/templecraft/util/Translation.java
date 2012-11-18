package com.bukkit.xarinor.templecraft.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;

/**
* Translation.java
* This work is dedicated to the public domain.
* 
* @author narrowtux
*/
public class Translation {
	private static Map<String, String> translations = new HashMap<String, String>();
	private static File file;
	private static int version;

	public static void reload(File file) {
		Translation.file = file;
		translations.clear();
		load();
	}

	/**
	 * load translation
	 */
	private static void load() {
		FlatFileReader reader = new FlatFileReader(file, true);
		for(String key:reader.keys()) {
			if(!key.equals("version")) {
				String trans = parseColors(reader.getString(key, "No translation for '"+key+"' found!"));
				trans = trans.replaceAll("\\\\n", "\n");
				translations.put(key, trans);
			}
		}
		version = reader.getInteger("version", 0);
	}

	/**
	 * Find string in translations
	 * With additional arguments
	 * 
	 * @param key
	 * @param args
	 * @return
	 */
	public static String tr(String key, Object... args) {
		if(!translations.containsKey(key)) {
			return "Can't find key "+key+" for translation!";
		} else {
			String trans = translations.get(key);
			return String.format(trans, args);
		}
	}

	/**
	 * Find string in translations
	 * 
	 * @param key
	 * @return
	 */
	public static String tr(String key) {
		if(!translations.containsKey(key)) {
			return "Can't find key "+key+" for translation!";
		} else {
			String trans = translations.get(key);
			return trans;
		}
	}

	/**
	 * Parse colors by string
	 * 
	 * @param str -String
	 * @return
	 */
	public static String parseColors(String str) {
		//Method written by Afforess
		for (ChatColor color : ChatColor.values()) {
			String name = "\\[" + color.name().toUpperCase() + "]";
			str = str.replaceAll(name, color.toString());
		}
		return str;
	}

	/**
	 * Gets the version
	 * 
	 * @return
	 */
	public static int getVersion() {
		return version;
	}
}
