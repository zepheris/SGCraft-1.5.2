//------------------------------------------------------------------------------------------------
//
//   SG Craft - Common Proxy
//
//------------------------------------------------------------------------------------------------

package gcewing.sg;

import net.minecraft.entity.player.*;
import net.minecraft.client.gui.*;
import net.minecraft.inventory.*;
import net.minecraft.world.*;

public class Proxy extends BaseProxy {

	public void init(BaseMod mod) {
		super.init(mod);
	}
	
	@Override
	void registerContainers() {
		//System.out.printf("SGCraft: Proxy.registerContainers\n");
		addContainer(SGGui.SGBase, SGBaseContainer.class);
	}

//	@Override
//	Container getGuiContainer(int id, EntityPlayer player, World world, int x, int y, int z) {
//		switch (id) {
//			//case 1:
//			//	return createSGBaseContainer(world, x, y, z);
//			default:
//				return null;
//		}
//	}

//	Container createSGBaseContainer(World world, int x, int y, int z) {
//		TileEntity te = world.getBlockTileEntity(x, y, z);
//		if (te instanceof SGBaseTE)
//			return new SGBaseContainer((SGBaseTE)te);
//		else
//			return null;
//	}

}
