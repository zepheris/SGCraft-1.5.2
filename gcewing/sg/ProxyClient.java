//------------------------------------------------------------------------------------------------
//
//   SG Craft - Client Proxy
//
//------------------------------------------------------------------------------------------------

package gcewing.sg;

import net.minecraft.client.gui.*;
import net.minecraft.entity.player.*;
import net.minecraft.world.*;

public class ProxyClient extends BaseProxyClient {

	public void init(BaseMod mod) {
		super.init(mod);
	}
	
	@Override
	void registerScreens() {
		//System.out.printf("SGCraft: ProxyClient.registerScreens\n");
		addScreen(SGGui.SGBase, SGBaseScreen.class);
		addScreen(SGGui.SGController, SGControllerScreen.class);
	}

	@Override
	void registerRenderers() {
		addBlockRenderer(SGCraft.sgRingBlock, new SGRingBlockRenderer());
		addBlockRenderer(SGCraft.sgBaseBlock, new SGBaseBlockRenderer());
		addBlockRenderer(SGCraft.sgControllerBlock, new BaseBlockRenderer());
		addTileEntityRenderer(SGBaseTE.class, new SGBaseTERenderer());
	}

//	@Override
//	GuiScreen getGuiScreen(int id, EntityPlayer player, World world, int x, int y, int z) {
//		switch (id) {
//			case 1:
//				return createSGBaseScreen(world, x, y, z);
//			case 2:
//				return createSGControllerScreen(world, x, y, z);
//			default:
//				return null;
//		}
//	}
//
//	GuiScreen createSGBaseScreen(World world, int x, int y, int z) {
//		SGBaseTE te = SGBaseTE.at(world, x, y, z);
//		if (te != null)
//			return new SGBaseScreen(te);
//		return null;
//	}
//	
//	GuiScreen createSGControllerScreen(World world, int x, int y, int z) {
//		return new SGControllerScreen(null);
//	}

}
