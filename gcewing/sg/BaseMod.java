//------------------------------------------------------------------------------------------------
//
//   Greg's Mod Base - Generic Mod
//
//------------------------------------------------------------------------------------------------

package gcewing.sg;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;

import net.minecraft.block.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.world.*;

import net.minecraftforge.common.*;
import net.minecraftforge.client.*;
import net.minecraftforge.oredict.*;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.*;
import cpw.mods.fml.common.registry.*;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class BaseMod {

	public static BaseConfiguration config;
	public static String modPackage;
	public static String resourceDir; // path to resources directory with leading and trailing slashes
	public static String textureFile; // path to default texture file with leading slash
	public static URL resourceURL; // URL to the resources directory
	public static BaseMod base;
	public static Proxy proxy;
	public static String channel;

	static File cfgFile;

	static String resourcePath(String fileName) {
		return resourceDir + fileName;
	}

	public BaseMod(String pkg) {
		base = this;
		modPackage = pkg;
		String resourceRelDir = modPackage.replace(".", "/") + "/resources/";
		resourceDir = "/" + resourceRelDir;
		textureFile = resourceDir + "textures.png";
		resourceURL = getClass().getClassLoader().getResource(resourceRelDir);
		//System.out.printf("BaseMod: resourceURL = %s\n", resourceURL);
	}

	//@Mod.PreInit
	public void preInit(FMLPreInitializationEvent e) {
		cfgFile = e.getSuggestedConfigurationFile();
		loadConfig();
		boolean[] configMarkers = ReflectionHelper.getPrivateValue(Configuration.class, config, "configMarkers");
		preallocateBlockIDs(configMarkers);
		preallocateItemIDs(configMarkers);
		//System.out.printf("BaseMod: Registering packet handlers for channel '%s'\n", channel);
	}
	
	void preallocateBlockIDs(boolean[] configMarkers) {
		ConfigCategory items = config.getCategory(config.CATEGORY_BLOCK);
		for (Property prop : items.getValues().values()) {
			int id = prop.getInt();
			if (id != -1) {
				//System.out.printf("BaseMod.preallocateItemIDs: Marking block id %d as in use\n", id);
				configMarkers[id] = true;
			}
		}
	}
	
	void preallocateItemIDs(boolean[] configMarkers) {
		ConfigCategory items = config.getCategory(config.CATEGORY_ITEM);
		for (Property prop : items.getValues().values()) {
			int id = prop.getInt();
			if (id != -1) {
				//System.out.printf("BaseMod.preallocateItemIDs: Marking item id %d as in use\n", id);
				configMarkers[id + 256] = true;
			}
		}
	}

	//@Mod.Init
	public void init(FMLInitializationEvent e) {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	//@Mod.PostInit
	public void postInit(FMLPostInitializationEvent e) {
		registerBlocks();
		registerItems();
		registerOres();
		registerRecipes();
		registerTileEntities();
		registerRandomItems();
		registerWorldGenerators();
		if (e.getSide().isClient())
			proxy = new ProxyClient();
		else
			proxy = new Proxy();
		proxy.init(this);
		System.out.printf("%s: BaseMod.postInit: Registering gui handler %s\n", this, proxy);
		NetworkRegistry.instance().registerGuiHandler(this, proxy);
		saveConfig();
	}

	void loadConfig() {
		config = new BaseConfiguration(cfgFile);
	}

	void saveConfig() {
		if (config.extended)
			config.save();
	}

	String qualifiedName(String name) {
		return modPackage + "." + name;
	}

	BaseItem newItem(String name, String title) {
		return newItem(name, BaseItem.class, title);
	}

	<ITEM extends BaseItem> ITEM newItem(String name, Class<ITEM> cls, String title) {
		try {
			int id = config.getItem(name, 1).getInt();
			Constructor<ITEM> ctor = cls.getConstructor(int.class, String.class);
			ITEM item = ctor.newInstance(id, modPackage + ":" + name);
			item.setUnlocalizedName(qualifiedName(name));
			//item.setItemName(qualifiedName(name));
			//item.setIconIndex(icon);
			LanguageRegistry.addName(item, title);
			return item;
		}
		catch (Exception e) {
			throw new RuntimeException(e.toString());
		}
	}
	
	Block newBlock(String name, String title) {
		return newBlock(name, Block.class, title);
	}
	
	<BLOCK extends Block> BLOCK newBlock(String name, Class<BLOCK> cls, String title) {
		return newBlock(name, cls, ItemBlock.class, title);
	}
	
	<BLOCK extends Block> BLOCK newBlock(String name, Class<BLOCK> cls, Class itemClass, String title) {
		try {
			int id = config.getBlock(name, 1).getInt();
			Constructor<BLOCK> ctor = cls.getConstructor(int.class);
			BLOCK block = ctor.newInstance(id);
			if (block instanceof BaseBlock) {
				((BaseBlock)block).modName = modPackage;
				((BaseBlock)block).textureName = name;
			}
			//block.setUnlocalizedName(qualifiedName(name));
			block.setUnlocalizedName(modPackage + ":" + name);
			GameRegistry.registerBlock(block, itemClass);
			LanguageRegistry.addName(block, title);
			return block;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	void registerOre(String name, Block block) {
		OreDictionary.registerOre(name, new ItemStack(block));
	}
	
	void registerOre(String name, Item item) {
		OreDictionary.registerOre(name, new ItemStack(item));
	}

	void newRecipe(Item product, int qty, Object... params) {
		//GameRegistry.addRecipe(new ItemStack(product, qty), params);
		newRecipe(new ItemStack(product, qty), params);
	}
	
	void newRecipe(Block product, int qty, Object... params) {
		//GameRegistry.addRecipe(new ItemStack(product, qty), params);
		newRecipe(new ItemStack(product, qty), params);
	}

	void newRecipe(ItemStack product, Object... params) {
		GameRegistry.addRecipe(new ShapedOreRecipe(product, params));
	}

	void newShapelessRecipe(Block product, int qty, Object... params) {
		newShapelessRecipe(new ItemStack(product, qty), params);
	}
	
	void newShapelessRecipe(Item product, int qty, Object... params) {
		newShapelessRecipe(new ItemStack(product, qty), params);
	}
	
	void newShapelessRecipe(ItemStack product, Object... params) {
		GameRegistry.addRecipe(new ShapelessOreRecipe(product, params));
	}

	void newSmeltingRecipe(Item product, int qty, Item input) {
		GameRegistry.addSmelting(input.itemID, new ItemStack(product, qty), 0);
	}
	
	void newSmeltingRecipe(Item product, int qty, Block input) {
		GameRegistry.addSmelting(input.blockID, new ItemStack(product, qty), 0);
	}
	
	void addRandomChestItem(ItemStack stack, int minQty, int maxQty, int weight, String... category) {
		WeightedRandomChestContent item = new WeightedRandomChestContent(stack, minQty, maxQty, weight);
		for (int i = 0; i < category.length; i++)
			ChestGenHooks.addItem(category[i], item);
	}

	void registerBlocks() {}
	void registerItems() {}
	void registerOres() {}
	void registerRecipes() {}
	void registerTileEntities() {}
	void registerRandomItems() {}
	void registerWorldGenerators() {}
	
	public Set<String> listResources(String subdir) {
		try {
			Set<String>result = new HashSet<String>();
			String protocol = resourceURL.getProtocol();
			if (protocol.equals("jar")) {
				String resPath = resourceURL.getPath();
				int pling = resPath.indexOf("!");
				URL jarURL = new URL(resPath.substring(0, pling));
				String resDirInJar = resPath.substring(pling + 2);
				String prefix = resDirInJar + subdir + "/";
				//System.out.printf("BaseMod.listResources: looking for names starting with %s\n", prefix);
				JarFile jar = new JarFile(new File(jarURL.toURI()));
				Enumeration<JarEntry> entries = jar.entries();
				while (entries.hasMoreElements()) {
					String name = entries.nextElement().getName();
					if (name.startsWith(prefix) && !name.endsWith("/") && !name.contains("/.")) {
						//System.out.printf("BaseMod.listResources: name = %s\n", name);
						result.add(name.substring(prefix.length()));
					}
				}
			}
			else
				throw new RuntimeException("Resource URL protocol " + protocol + " not supported");
			return result;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	static void openGui(EntityPlayer player, Enum id, World world, int x, int y, int z) {
		openGui(player, id.ordinal(), world, x, y, z);
	}

	static void openGui(EntityPlayer player, int id, World world, int x, int y, int z) {
		//System.out.printf("%s: BaseMod.openGui: %d for %s\n", base, id, player);
		player.openGui(base, id, world, x, y, z);
	}
	
//	public void printChatMessage(String s) {
//		proxy.printChatMessage(s);
//	}

}
