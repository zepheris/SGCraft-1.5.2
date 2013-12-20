//------------------------------------------------------------------------------------------------
//
//   Mod Base - 4-way rotatable block
//
//------------------------------------------------------------------------------------------------

package gcewing.sg;

import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.creativetab.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.world.*;

public class Base4WayBlock<TE extends TileEntity> extends BaseBlock<TE> {

	static int rotationShift = 0;
	static int rotationMask = 0x3;
	
	public Base4WayBlock(int id, Material material) {
		super(id, material, null);
	}
	
	public Base4WayBlock(int id, Material material, Class<TE> teClass) {
		super(id, material, teClass);
	}
	
	public void setRotation(World world, int x, int y, int z, int rotation, boolean notify) {
		int data = world.getBlockMetadata(x, y, z);
		data = insertRotation(data, rotation);
		setMetadata(world, x, y, z, data, notify);
	}
	
	@Override
	public int rotationInWorld(int data, TE te) {
		return extractRotation(data);
	}

	public int extractRotation(int data) {
		return (data & rotationMask) >> rotationShift;
	}
	
	public int insertRotation(int data, int rotation) {
		return (data & ~rotationMask) | (rotation << rotationShift);
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving player, ItemStack stack) {
		int rotation = Math.round((180 - player.rotationYaw) / 90) & 3;
		setRotation(world, x, y, z, rotation, true);
	}

	@Override
	public Icon getBlockTextureFromSideAndMetadata(int side, int data) {
		int rotation = extractRotation(data);
		int localSide = Directions.globalToLocalSide(side, rotation);
		return getBlockTextureFromLocalSideAndMetadata(localSide, data);
	}
	
//	int getBlockTextureFromLocalSideAndMetadata(int side, int data) {
//		return blockIndexInTexture + side;
//	}

}
