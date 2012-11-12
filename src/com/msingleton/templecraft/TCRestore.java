package com.msingleton.templecraft;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Slime;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;

import com.msingleton.templecraft.util.EntityPosition;

public class TCRestore 
{
	//Blocks
	private static Set<Integer> blockSet = new HashSet<Integer>(Arrays.asList(0,1,2,3,4,5,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,35,41,42,43,44,45,46,47,48,49,52,54,56,57,58,60,61,62,67,73,74,79,80,81,82,84,85,86,87,88,89,90,91,92,98,99,100,101,102,103,106,107,108,109,110,112,113,114,121,123,124));
	private static String c = ":";
	private static String s = " ";

	public static void saveTemple(World w, Temple temple)
	{
		if(w == null || temple == null)
		{
			return;
		}

		w.save();
		File folder = new File("plugins/TempleCraft/SavedTemples/"+temple.templeName);
		try 
		{
			copyFiles(new File(w.getName()),folder);
		} 
		catch (IOException e) 
		{
			TCUtils.debugMessage("could not save Worldname " + w.getName() + ", Temple" + temple.templeName + "\n" + e.getMessage());
			e.printStackTrace();
		}
		saveSignificantLocs(temple);
	}

	// Copies all files under srcDir to dstDir.
	// If dstDir does not exist, it will be created.
	/*	public static void copyDirectory(File srcDir, File dstDir)
	{
		if(srcDir.getName().equals("uid.dat") || srcDir.getName().equals("session.lock"))
		{
			if(dstDir.exists())
			{
				dstDir.delete();
			}
			TCUtils.debugMessage(srcDir.getName() + " ## srcDir.getName().equals(\"uid.dat\") || srcDir.getName().equals(\"session.lock\")");
			return;
		}
		if (srcDir.isDirectory())
		{
			TCUtils.debugMessage(srcDir.getName() + " isDirectory");
			if (!dstDir.exists())
			{
				dstDir.mkdir();
			}

			String[] children = srcDir.list();
			for (int i=0; i<children.length; i++)
			{
				TCUtils.debugMessage(srcDir.getName() + " - " + children[i]);
				copyDirectory(new File(srcDir, children[i]), new File(dstDir, children[i]));
			}
		}
		else
		{
			TCUtils.debugMessage(srcDir.getName() + " isFile");
			// This method is implemented in Copying a File
			copyFile(srcDir, dstDir);
		}
	}

	// Copies src file to dst file.
	// If the dst file does not exist, it is created
	public static void copyFile(File src, File dst)
	{
		try
		{
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dst);

			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0)
			{
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}
		catch(Exception e)
		{
			System.out.println("Could not copy file "+src);
		}
	}
	 */

	/**
	 * This function will copy files or directories from one location to another.
	 * note that the source and the destination must be mutually exclusive. This 
	 * function can not be used to copy a directory to a sub directory of itself.
	 * The function will also have problems if the destination files already exist.
	 * @param src -- A File object that represents the source for the copy
	 * @param dest -- A File object that represnts the destination for the copy.
	 * @throws IOException if unable to copy.
	 * 
	 * copied from http://www.dreamincode.net/code/snippet1443.htm
	 */
	public static void copyFiles(File src, File dest) throws IOException 
	{	
		TCUtils.debugMessage("try copying: " + src.getAbsolutePath() + " > " + dest.getAbsolutePath());
		if (!src.exists()) //Check to ensure that the source is valid...
		{
			TCUtils.debugMessage("Can not find source: " + src.getAbsolutePath()+".",Level.SEVERE);
			throw new IOException("copyFiles: Can not find source: " + src.getAbsolutePath()+".");
		}
		else if (!src.canRead()) //check to ensure we have rights to the source...
		{ 
			TCUtils.debugMessage("No right to source: " + src.getAbsolutePath()+".",Level.SEVERE);
			throw new IOException("copyFiles: No right to source: " + src.getAbsolutePath()+".");
		}

		if (src.isDirectory()) 	//is this a directory copy?
		{
			TCUtils.debugMessage(src.getName() + " isDirectory");
			if (!dest.exists()) //does the destination already exist?
			{ 
				//if not we need to make it exist if possible (note this is mkdirs not mkdir)
				if (!dest.mkdirs()) 
				{
					TCUtils.debugMessage("Could not create direcotry: " + dest.getAbsolutePath() + ".",Level.SEVERE);
					throw new IOException("copyFiles: Could not create direcotry: " + dest.getAbsolutePath() + ".");
				}
			}
			//get a listing of files...
			String list[] = src.list();
			//copy all the files in the list.
			for (int i = 0; i < list.length; i++)
			{
				File dest1 = new File(dest, list[i]);
				File src1 = new File(src, list[i]);
				TCUtils.debugMessage("copyChild: " + src1.getAbsolutePath() + " > " + dest1.getAbsolutePath());
				copyFiles(src1 , dest1);
			}
		} 
		else 
		{ 
			TCUtils.debugMessage(src.getName() + " isFile");
			//This was not a directory, so lets just copy the file
			FileInputStream fin = null;
			FileOutputStream fout = null;
			byte[] buffer = new byte[4096]; //Buffer 4K at a time (you can change this).
			int bytesRead;
			try
			{
				if(dest.exists() && !dest.canWrite())
				{
					throw new IOException("copyFiles: " + dest.getAbsolutePath() + " already exists.");
				}
				//open the files for input and output
				fin =  new FileInputStream(src);
				fout = new FileOutputStream(dest);
				//while bytesRead indicates a successful read, lets write...
				while ((bytesRead = fin.read(buffer)) >= 0)
				{
					fout.write(buffer,0,bytesRead);
				}
			} 
			catch (Exception e) //Error copying file... 
			{ 
				TCUtils.debugMessage("Unable to copy file: " + 
						src.getAbsolutePath() + " > " + dest.getAbsolutePath()+".",Level.SEVERE);
				IOException wrapper = new IOException("copyFiles: Unable to copy file: " + 
						src.getAbsolutePath() + " > " + dest.getAbsolutePath()+".");
				wrapper.initCause(e);
				wrapper.setStackTrace(e.getStackTrace());
				throw wrapper;
			} 
			finally //Ensure that the files are closed (if they were open).
			{ 
				if (fin != null) { fin.close(); }
				if (fout != null) { fout.close(); }
			}
		}
	}


	public static boolean loadTemple(String worldName, Temple temple)
	{
		TCUtils.debugMessage("loadtemple Worldname " + worldName + ", Temple" + temple.templeName);
		File file = new File("plugins/TempleCraft/SavedTemples/"+temple.templeName);

		if(!file.exists())
		{
			return false;
		}
		try 
		{
			copyFiles(file,new File(worldName));
		} 
		catch (IOException e) 
		{
			TCUtils.debugMessage("could not load Worldname " + worldName + ", Temple" + temple.templeName + "\n" + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static File getChunkGenerator(Temple temple)
	{
		File folder = new File("plugins/TempleCraft/SavedTemples/"+temple.templeName);
		for(File f : getFilesByExtention(folder, ".jar"))
		{
			ChunkGenerator cg = getChunkGenerator(f);
			if(cg != null)
			{
				return f;
			}
		}
		return null;
	}

	private static Set<File> getFilesByExtention(File folder, String extention)
	{
		Set<File> files = new HashSet<File>();
		if(folder.isDirectory())
		{
			for(File f : folder.listFiles())
			{
				if(f.isDirectory())
				{
					files.addAll(getFilesByExtention(f,extention));
				}
				else if(f.getName().endsWith(extention))
				{
					files.add(f);
				}
			}
		}
		return files;
	}

	@SuppressWarnings("rawtypes")
	public static ChunkGenerator getChunkGenerator(File file)
	{
		try
		{
			JarFile jar = new JarFile(file);
			URL url=new URL("jar:file:"+file.getPath()+"!/");
			URL[] urls = new URL[]{url};
			ClassLoader cl = new URLClassLoader(urls);
			Enumeration<JarEntry> e = jar.entries();
			while(e.hasMoreElements())
			{
				JarEntry entry = e.nextElement();
				String name = entry.getName();
				if(name.endsWith(".class"))
				{
					if(name.contains("/"))
					{
						name = name.replace("/", ".");
					}
					Class cls = cl.loadClass(name.replace(".class", ""));
					Object instance = cls.newInstance();
					if(instance instanceof ChunkGenerator)
					{
						return (ChunkGenerator)instance;
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static void saveSignificantLocs(Temple temple)
	{
		if(temple == null || temple.coordLocSet.isEmpty())
		{
			return;
		}

		HashSet<EntityPosition> significantLocs = new HashSet<EntityPosition>();

		for(Location loc : temple.coordLocSet)
		{
			significantLocs.add(new EntityPosition(loc));
		}

		try
		{	
			File folder = new File("plugins/TempleCraft/SavedTemples/");
			if(!folder.exists())
			{
				folder.mkdir();
			}
			File tmpfile = new File("plugins/TempleCraft/SavedTemples/"+temple.templeName+"/"+"TCLocs.tmp");
			FileOutputStream fos = new FileOutputStream(tmpfile);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(significantLocs);
			oos.close();
			fos.close();
			File file = new File("plugins/TempleCraft/SavedTemples/"+temple.templeName+"/"+"TCLocs"+ TempleCraft.fileExtention);
			if(file.exists())
			{
				file.delete();
			}
			tmpfile.renameTo(file);
		}
		catch (Exception e)
		{
			System.out.println("[TempleCraft] Couldn't create backup file. Aborting...");
			e.printStackTrace();
			return;
		}
	}

	@SuppressWarnings("unchecked")
	public static HashSet<EntityPosition> getSignificantEPs(Temple temple)
	{		
		String fileName = "TCLocs"+TempleCraft.fileExtention;

		HashSet<EntityPosition> significantEPs = new HashSet<EntityPosition>();		
		try
		{
			File file = new File("plugins/TempleCraft/SavedTemples/"+temple.templeName+"/"+fileName);
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			significantEPs = (HashSet<EntityPosition>) ois.readObject();
			ois.close();
			fis.close();
		}
		catch (Exception e)
		{
			System.out.println("[TempleCraft] TempleCraft file not found for this temple.");
			return significantEPs;
		}

		return significantEPs;
	}

	public static HashSet<Location> getSignificantLocs(Temple temple, World world)
	{					
		HashSet<Location> significantLocs = new HashSet<Location>();

		for(EntityPosition ep : getSignificantEPs(temple))
		{
			significantLocs.add(ep.getLocation(world));
		}

		return significantLocs;
	}


	// Old Loading Method
	@SuppressWarnings("unchecked")
	public static void loadTemple(Location startLoc, Temple temple)
	{	
		if(startLoc == null)
		{
			return;
		}

		String fileName = temple.templeName + TempleCraft.fileExtention;

		HashMap<EntityPosition,String> preciousPatch;
		try
		{
			File file = new File("plugins/TempleCraft/SavedTemples/"+fileName);
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			preciousPatch = (HashMap<EntityPosition,String>) ois.readObject();
			ois.close();
		}
		catch (Exception e)
		{
			System.out.println("[TempleCraft] TempleCraft file not found for this temple.");
			return;
		}

		Set<EntityPosition> ppKeySet = new HashSet<EntityPosition>();
		ppKeySet.addAll(preciousPatch.keySet());

		Map<EntityPosition, String> blockMap = new HashMap<EntityPosition, String>();
		Map<EntityPosition, String> nonBlockMap = new HashMap<EntityPosition, String>();
		Map<EntityPosition, String> pistonMap = new HashMap<EntityPosition, String>();
		for (EntityPosition ep : ppKeySet)
		{
			int id = Integer.parseInt(preciousPatch.get(ep).split(c)[0]);
			int y = (int)(startLoc.getY()+ep.getY());
			if(id == getDefaultBlock(y))
			{
				preciousPatch.remove(ep);
			}
			else if(!blockSet.contains(id) && id != 29 && id!=33 && id != 34)
			{
				nonBlockMap.put(ep, preciousPatch.remove(ep));
			}
			else if(blockSet.contains(id))
			{
				blockMap.put(ep, preciousPatch.remove(ep));
			}
			else
			{
				pistonMap.put(ep, preciousPatch.remove(ep));
			}
		}

		loadBlockMap(startLoc, blockMap, temple, true);
		loadBlockMap(startLoc, nonBlockMap, temple, true);
		loadBlockMap(startLoc, pistonMap, temple, true);
	}

	private static void contentsFromString(Chest cb, String string)
	{
		String[] items = string.split(s);
		for(int i = 0; i < items.length; i+=3)
		{
			if(items[i] == null || items[i].isEmpty())
			{
				continue;
			}
			ItemStack item = new ItemStack(Integer.parseInt(items[i]));
			item.setAmount(Integer.parseInt(items[i+1]));
			item.setDurability(Short.parseShort(items[i+2]));
			cb.getInventory().setItem(i/3, item);
		}
	}

	private static void loadBlockMap(Location startLoc, Map<EntityPosition, String> blockMap, Temple temple, Boolean physics)
	{
		World world = startLoc.getWorld();
		for (EntityPosition ep : blockMap.keySet())
		{			
			String[] s = blockMap.get(ep).split(c);

			int id = Integer.parseInt(s[0]);
			byte data = Byte.parseByte(s[1]);

			double x = ep.getX()+startLoc.getX();
			double y = ep.getY()+startLoc.getBlockY();
			double z = ep.getZ()+startLoc.getZ();
			Location loc = new Location(world, x, y, z);
			Block b = world.getBlockAt(loc);
			b.setTypeIdAndData(id, data, physics);

			// If it's a door, add the upper half
			if(id == 71 || id == 64)
			{
				b.getRelative(0,1,0).setTypeIdAndData(id,(byte) (data+8), true);
				// If it's a sign, add the text
			}
			else if(b.getState() instanceof Sign)
			{
				temple.coordLocSet.add(loc);
				if(s.length > 2)
				{
					for(int i = 2; i<s.length;i++)
					{
						if((i-2)>3 || (i-2)<0 || s[i] == null)
						{
							continue;
						}
						Sign sign = (Sign) b.getState();
						sign.setLine((i-2), s[i]);
						sign.update(true);
					}
				}
				// If it's a container, add it's contents
			}
			else if(b.getState() instanceof Chest)
			{
				if(s.length > 2)
				{
					contentsFromString((Chest)b.getState(), s[2]);
				}
				// If it's diamond,gold or iron or bedrock, add it to coordBlockSet
			}
			else if(b.getTypeId() == Temple.goldBlock || b.getTypeId() == Temple.diamondBlock || b.getTypeId() == Temple.ironBlock || b.getType() == Material.WALL_SIGN || b.getType() == Material.SIGN_POST || b.getTypeId() == Temple.mobSpawner)
			{
				temple.coordLocSet.add(loc);
			}
		}
	}

	public static int getDefaultBlock(int y)
	{
		int[] levels = TempleManager.landLevels;
		byte[] mats = TempleManager.landMats;
		for(int i = 0; i<levels.length; i++)
		{
			int bottom, top;
			if(i == 0)
			{
				bottom = 0;
			}
			else
			{
				bottom = levels[i-1];
			}
			top = levels[i];
			if(y >= bottom && y <= top)
			{
				return mats[i];
			}
		}
		return 0;
	}

	public static void clearEntities(Location p1, Location p2)
	{
		World world = p1.getWorld();

		Chunk c1 = world.getChunkAt(p1);
		Chunk c2 = world.getChunkAt(p2);

		/* Yes, ugly nesting, but it's necessary. This bit
		 * removes all the entities in the Temple region without
		 * bloatfully iterating through all entities in the
		 * world. Much faster on large servers especially. */ 
		for (int i = c1.getX(); i <= c2.getX(); i++)
		{
			for (int j = c1.getZ(); j <= c2.getZ(); j++)
			{
				for (Entity e : world.getChunkAt(i,j).getEntities())
				{
					if ((e instanceof Item) || (e instanceof Slime))
					{
						e.remove();
					}
				}
			}
		}
	}
}
