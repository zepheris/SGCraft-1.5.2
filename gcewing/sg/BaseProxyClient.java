//------------------------------------------------------------------------------------------------
//
//   Greg's Mod Base - Generic Client Proxy
//
//------------------------------------------------------------------------------------------------

package gcewing.sg;

import java.net.*;
import java.util.*;

import net.minecraft.block.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.audio.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.tileentity.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.network.*;
import net.minecraft.network.packet.*;
import net.minecraft.src.*;
import net.minecraft.tileentity.*;
import net.minecraft.world.*;

import net.minecraftforge.common.*;
import net.minecraftforge.client.*;
import cpw.mods.fml.common.network.*;
import cpw.mods.fml.client.registry.*;

public class BaseProxyClient extends Proxy {

	public Minecraft mc;

	Map<Integer, Class<? extends GuiScreen>> screenClasses =
		new HashMap<Integer, Class<? extends GuiScreen>>();

	public void init(BaseMod mod) {
		super.init(mod);
		mc = ModLoader.getMinecraftInstance();
		registerScreens();
		loadTextures(mod);
		registerRenderers();
		registerSounds();
	}
	
	void registerScreens() {
	}

	public void addScreen(Enum id, Class<? extends GuiScreen> cls) {
		addScreen(id.ordinal(), cls);
	}

	public void addScreen(int id, Class<? extends GuiScreen> cls) {
		screenClasses.put(id, cls);
	}
	
	void loadTextures(BaseMod mod) {
		MinecraftForgeClient.preloadTexture(mod.textureFile);
		//System.out.printf("BaseProxyCLient.loadTextures:" + "mod.textureFile");
	}
	
	void registerRenderers() {
	}

	void addBlockRenderer(BaseIRenderType block, BaseBlockRenderer renderer) {
		int renderID = RenderingRegistry.getNextAvailableRenderId();
		block.setRenderType(renderID);
		renderer.renderID = renderID;
		RenderingRegistry.registerBlockHandler(renderID, renderer);
	}
	
	void addItemRenderer(Item item, IItemRenderer renderer) {
		MinecraftForgeClient.registerItemRenderer(item.itemID, renderer);
	}
	
	void addItemRenderer(Block block, IItemRenderer renderer) {
		MinecraftForgeClient.registerItemRenderer(block.idDropped(0, null, 0), renderer);
	}
	
	void addTileEntityRenderer(Class <? extends TileEntity> teClass, TileEntitySpecialRenderer renderer) {
		ClientRegistry.bindTileEntitySpecialRenderer(teClass, renderer);
	}
	
	void registerSounds() {
		try {
			//System.out.printf("BaseProxyClient.registerSounds\n");
			SoundPool pool = mc.sndManager.soundPoolSounds;
			String namePrefix = base.modPackage.replace(".", "/") + "/";
			String subdir = "sounds";
			Set<String> items = base.listResources(subdir);
			for (String item : items) {
				//System.out.printf("BaseProxyClient.registerSounds: item = %s\n", item);
				String soundName = namePrefix + item;
				URL soundURL = new URL(base.resourceURL, subdir + "/" + item);
				//System.out.printf("BaseProxyClient.registerSounds: name = %s url = %s\n", soundName, soundURL);
				pool.addSound(soundName, soundURL);
			}
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Returns a Container to be displayed to the user. 
	 * On the client side, this needs to return a instance of GuiScreen
	 * On the server side, this needs to return a instance of Container
	 *
	 * @param ID The Gui ID Number
	 * @param player The player viewing the Gui
	 * @param world The current world
	 * @param x X Position
	 * @param y Y Position
	 * @param z Z Position
	 * @return A GuiScreen/Container to be displayed to the user, null if none.
	 */
	
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		Class cls = screenClasses.get(id);
		if (cls != null) {
			//System.out.printf("%s: BaseProxyClient.getClientGuiElement: %d is %s\n",
			//	base, id, cls);
			return createGuiElement(cls, player, world, x, y, z);
		}
		else
			return getGuiScreen(id, player, world, x, y, z);
	}
	
	GuiScreen getGuiScreen(int id, EntityPlayer player, World world, int x, int y, int z) {
		//System.out.printf("%s: No GuiScreen registered for gui id %d\n", this, id);
		return null;
	}

//	@Override
//	public void printChatMessage(String s) {
//		mc.ingameGUI.getChatGUI().printChatMessage(s);
//	}

}
