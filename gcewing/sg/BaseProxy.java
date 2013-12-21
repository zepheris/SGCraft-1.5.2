//------------------------------------------------------------------------------------------------
//
//   Greg's Mod Base - Generic Common Proxy
//
//------------------------------------------------------------------------------------------------

package gcewing.sg;

import java.util.*;
import java.lang.reflect.*;

import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.world.*;

import net.minecraftforge.common.*;
import cpw.mods.fml.common.network.*;

public class BaseProxy implements IGuiHandler {

	BaseMod base;

	Map<Integer, Class<? extends Container>> containerClasses =
		new HashMap<Integer, Class<? extends Container>>();

	public void init(BaseMod mod) {
		base = mod;
		registerContainers();
	}
	
	void registerContainers() {
	}
	
	public void addContainer(Enum id, Class<? extends Container> cls) {
		addContainer(id.ordinal(), cls);
	}

	public void addContainer(int id, Class<? extends Container> cls) {
		containerClasses.put(id, cls);
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
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		//System.out.printf("%s: BaseProxy.getServerGuiElement: %d\n", base, id);
		Class cls = containerClasses.get(id);
		if (cls != null)
			return createGuiElement(cls, player, world, x, y, z);
		else
			return getGuiContainer(id, player, world, x, y, z);
	}
	
	Object createGuiElement(Class cls, EntityPlayer player, World world, int x, int y, int z) {
		try {
			try {
				//System.out.printf("BaseProxy.createGuiElement: Invoking create method of %s for %s in %s\n",
				//	cls, player, world);
				return cls.getMethod("create", EntityPlayer.class, World.class, int.class, int.class, int.class)
					.invoke(null, player, world, x, y, z);
			}
			catch (NoSuchMethodException e) {
				//System.out.printf("BaseProxy.createGuiElement: Invoking constructor of %s\n", cls);
				return cls.getConstructor(EntityPlayer.class, World.class, int.class, int.class, int.class)
					.newInstance(player, world, x, y, z);
			}
		}
		catch (Exception e) {
			Throwable cause = e.getCause();
			//System.out.printf("BaseProxy.createGuiElement: %s: %s\n", e, cause);
			if (cause != null)
				cause.printStackTrace();
			else
				e.printStackTrace();
			//throw new RuntimeException(e);
			return null;
		}
	}
	
	Container getGuiContainer(int id, EntityPlayer player, World world, int x, int y, int z) {
		//System.out.printf("%s: No Container registered for gui id %d\n", this, id);
		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}
	
//	public void printChatMessage(String s) {
//	}

}
