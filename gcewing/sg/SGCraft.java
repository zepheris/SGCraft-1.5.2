//------------------------------------------------------------------------------------------------
//
//   SG Craft - Main Class
//
//------------------------------------------------------------------------------------------------

package gcewing.sg;

import java.util.*;

import net.minecraft.block.*;
import net.minecraft.item.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraft.world.chunk.*;

import net.minecraftforge.common.*;
import net.minecraftforge.event.*;
import net.minecraftforge.event.world.*;
import net.minecraftforge.event.terraingen.*;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.*;

@Mod(modid = Info.modID, name = Info.modName, version = Info.versionNumber)

@NetworkMod(clientSideRequired = true, serverSideRequired = true, versionBounds = Info.versionBounds)

public class SGCraft extends BaseMod {

	public static SGCraft mod;

	public static SGChannel channel;
	public static BaseTEChunkManager chunkManager;
	
	public static SGBaseBlock sgBaseBlock;
	public static SGRingBlock sgRingBlock;
	public static SGControllerBlock sgControllerBlock;
	public static SGPortalBlock sgPortalBlock;
	public static Block naquadahBlock, naquadahOre;
	
	public static BaseItem naquadah, naquadahIngot, sgCoreCrystal, sgControllerCrystal;
	
	public static boolean addOresToExistingWorlds;
	public static NaquadahOreWorldGen naquadahOreGenerator;
	public static int tokraVillagerID;

	public SGCraft() {
		super("gcewing." + Info.modPackage);
		mod = this;
	}
	
	@Mod.PreInit
	public void preInit(FMLPreInitializationEvent e) {
		super.preInit(e);
	}
	
	@Mod.Init
	public void init(FMLInitializationEvent e) {
		super.init(e);
		configure();
		channel = new SGChannel(Info.modID);
		chunkManager = new BaseTEChunkManager(this);
		registerTradeHandlers();
		MinecraftForge.TERRAIN_GEN_BUS.register(this);
	}

	@Mod.PostInit
	public void postInit(FMLPostInitializationEvent e) {
		super.postInit(e);
	}
	
	void configure() {
		SGControllerTE.configure(config);
		NaquadahOreWorldGen.configure(config);
		SGBaseTE.configure(config);
		addOresToExistingWorlds = config.getBoolean("options", "addOresToExistingWorlds", false);
	}		

	@Override
	void registerBlocks() {
		sgRingBlock = newBlock("stargateRing", SGRingBlock.class, SGRingItem.class, "Stargate Ring Segment");
		sgBaseBlock = newBlock("stargateBase", SGBaseBlock.class, "Stargate Base");
		sgControllerBlock = newBlock("stargateController", SGControllerBlock.class, "Stargate Controller");
		sgPortalBlock = newBlock("stargatePortal", SGPortalBlock.class, "Stargate Portal");
		naquadahBlock = newBlock("naquadahBlock", NaquadahBlock.class, "Naquadah Alloy Block");
		naquadahOre = newBlock("naquadahOre", NaquadahOreBlock.class, "Naquadah Ore");
	}
	
	@Override
	void registerItems() {
		naquadah = newItem("naquadah", /*0x41,*/ BaseItem.class, "Naquadah");
		naquadahIngot = newItem("naquadahIngot", /*0x42,*/ BaseItem.class, "Naquadah Alloy Ingot");
		sgCoreCrystal = newItem("sgCoreCrystal", /*0x44,*/ BaseItem.class, "Stargate Core Crystal");
		sgControllerCrystal = newItem("sgControllerCrystal", /*0x45,*/ BaseItem.class, "Stargate Controller Crystal");
	}
	
	@Override
	void registerOres() {
		registerOre("oreNaquadah", naquadahOre);
		registerOre("naquadah", naquadah);
		registerOre("ingotNaquadahAlloy", naquadahIngot);
	}
	
	@Override
	void registerRecipes() {
		ItemStack chiselledSandstone = new ItemStack(Block.sandStone, 1, 1);
		ItemStack smoothSandstone = new ItemStack(Block.sandStone, 1, 2);
		ItemStack sgChevronBlock = new ItemStack(sgRingBlock, 1, 1);
		ItemStack blueDye = new ItemStack(Item.dyePowder, 1, 4);
		ItemStack orangeDye = new ItemStack(Item.dyePowder, 1, 14);
		if (config.getBoolean("options", "allowCraftingNaquadah", false))
			newShapelessRecipe(naquadah, 1, Item.coal, Item.slimeBall, Item.blazePowder);
		newRecipe(sgRingBlock, 1, "CCC", "NNN", "SSS",
			'S', smoothSandstone, 'N', "ingotNaquadahAlloy", 'C', chiselledSandstone);
		newRecipe(sgChevronBlock, "CgC", "NpN", "SrS",
			'S', smoothSandstone, 'N', "ingotNaquadahAlloy", 'C', chiselledSandstone,
			'g', Item.lightStoneDust, 'r', Item.redstone, 'p', Item.enderPearl);
		newRecipe(sgBaseBlock, 1, "CrC", "NeN", "ScS",
			'S', smoothSandstone, 'N', "ingotNaquadahAlloy", 'C', chiselledSandstone,
			'r', Item.redstone, 'e', Item.eyeOfEnder, 'c', sgCoreCrystal);
		newRecipe(sgControllerBlock, 1, "bbb", "OpO", "OcO",
			'b', Block.stoneButton, 'O', Block.obsidian, 'p', Item.enderPearl,
			'r', Item.redstone, 'c', sgControllerCrystal);
		newShapelessRecipe(naquadahIngot, 1, "naquadah", Item.ingotIron);
		newRecipe(naquadahBlock, 1, "NNN", "NNN", "NNN", 'N', "ingotNaquadahAlloy");
		newRecipe(naquadahIngot, 9, "B", 'B', naquadahBlock);
		if (config.getBoolean("options", "allowCraftingCrystals", false)) {
			newRecipe(sgCoreCrystal, 1, "bbr", "rdb", "brb",
				'b', blueDye, 'r', Item.redstone, 'd', Item.diamond);
			newRecipe(sgControllerCrystal, 1, "roo", "odr", "oor",
				'o', orangeDye, 'r', Item.redstone, 'd', Item.diamond);
		}
	}
	
	@Override
	void registerRandomItems() {
		String[] categories = {ChestGenHooks.MINESHAFT_CORRIDOR,
			ChestGenHooks.PYRAMID_DESERT_CHEST, ChestGenHooks.PYRAMID_JUNGLE_CHEST,
			ChestGenHooks.STRONGHOLD_LIBRARY, ChestGenHooks.VILLAGE_BLACKSMITH};
		addRandomChestItem(new ItemStack(sgBaseBlock), 1, 1, 2, categories);
		addRandomChestItem(new ItemStack(sgControllerBlock), 1, 1, 1, categories);
		addRandomChestItem(new ItemStack(sgRingBlock, 1, 0), 1, 3, 8, categories);
		addRandomChestItem(new ItemStack(sgRingBlock, 1, 1), 1, 3, 7, categories);
		addRandomChestItem(new ItemStack(sgCoreCrystal, 1, 1), 1, 1, 2, categories);
		addRandomChestItem(new ItemStack(sgControllerCrystal, 1, 1), 1, 1, 1, categories);
	}
	
	@Override
	void registerWorldGenerators() {
		if (config.getBoolean("options", "enableNaquadahOre", true)) {
			naquadahOreGenerator = new NaquadahOreWorldGen();
			GameRegistry.registerWorldGenerator(naquadahOreGenerator);
		}
	}
	
	void registerTradeHandlers() {
		VillagerRegistry reg = VillagerRegistry.instance();
		tokraVillagerID = config.getVillager("tokra", resourcePath("tokra.png"));
		SGTradeHandler handler = new SGTradeHandler();
		reg.registerVillageTradeHandler(tokraVillagerID, handler);
	}
	
	@ForgeSubscribe
	public void onChunkLoad(ChunkDataEvent.Load e) {
		Chunk chunk = e.getChunk();
		//System.out.printf("SGCraft.onChunkLoad: (%d, %d)\n", chunk.xPosition, chunk.zPosition);
		SGChunkData.onChunkLoad(e);
	}

	@ForgeSubscribe
	public void onChunkSave(ChunkDataEvent.Save e) {
		Chunk chunk = e.getChunk();
		//System.out.printf("SGCraft.onChunkSave: (%d, %d)\n", chunk.xPosition, chunk.zPosition);
		SGChunkData.onChunkSave(e);
	}
	
	@ForgeSubscribe
	public void onInitMapGen(InitMapGenEvent e) {
		FeatureGeneration.onInitMapGen(e);
	}

//	@ForgeSubscribe
//	void onWorldLoad(WorldEvent.Load e) {
//		System.out.printf("SGCraft: World loaded: %s\n", e.world);
//	}
//	
//	@ForgeSubscribe
//	void onWorldUnload(WorldEvent.Unload e) {
//		System.out.printf("SGCraft: World unloaded: %s\n", e.world);
//	}

}
