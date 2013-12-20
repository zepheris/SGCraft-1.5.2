//------------------------------------------------------------------------------------------------
//
//   SG Craft - Stargate Controller Tile Entity
//
//------------------------------------------------------------------------------------------------

package gcewing.sg;

import net.minecraft.nbt.*;
import net.minecraft.tileentity.*;
import net.minecraft.world.*;

public class SGControllerTE extends BaseTileEntity {

	public static int linkRangeX = 5; // either side
	public static int linkRangeY = 1; // up or down
	public static int linkRangeZ = 6; // in front

	public boolean isLinkedToStargate;
	public int linkedX, linkedY, linkedZ;
	
	public static void configure(BaseConfiguration cfg) {
		linkRangeX = cfg.getInteger("dhd", "linkRangeX", linkRangeX);
		linkRangeY = cfg.getInteger("dhd", "linkRangeY", linkRangeY);
		linkRangeZ = cfg.getInteger("dhd", "linkRangeZ", linkRangeZ);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		isLinkedToStargate = nbt.getBoolean("isLinkedToStargate");
		linkedX = nbt.getInteger("linkedX");
		linkedY = nbt.getInteger("linkedY");
		linkedZ = nbt.getInteger("linkedZ");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setBoolean("isLinkedToStargate", isLinkedToStargate);
		nbt.setInteger("linkedX", linkedX);
		nbt.setInteger("linkedY", linkedY);
		nbt.setInteger("linkedZ", linkedZ);
	}

	SGBaseTE getLinkedStargateTE() {
		if (isLinkedToStargate) {
			TileEntity gte = worldObj.getBlockTileEntity(linkedX, linkedY, linkedZ);
			if (gte instanceof SGBaseTE)
				return (SGBaseTE)gte;
		}
		return null;
	}

	void checkForLink() {
		//System.out.printf("SGControllerTE.checkForLink at (%d,%d,%d): %s\n", xCoord, yCoord, zCoord, this);
		//System.out.printf("SGControllerTE.checkForLink: isLinkedToStargate = %s\n", isLinkedToStargate);
		if (!isLinkedToStargate) {
			Trans3 t = localToGlobalTransformation();
			for (int i = -linkRangeX; i <= linkRangeX; i++)
				for (int j = -linkRangeY; j <= linkRangeY; j++)
					for (int k = 1; k <= linkRangeZ; k++) {
					//for (int k = -linkRangeZ; k <= linkRangeZ; k++) {
						Vector3 p = t.p(i, j, -k);
						//System.out.printf("SGControllerTE: Looking for stargate at (%d,%d,%d)\n",
						//	p.floorX(), p.floorY(), p.floorZ());
						TileEntity te = worldObj.getBlockTileEntity(p.floorX(), p.floorY(), p.floorZ());
						if (te instanceof SGBaseTE) {
							//System.out.printf("SGControllerTE: Found stargate at (%d,%d,%d)\n",
							//	te.xCoord, te.yCoord, te.zCoord);
							if (linkToStargate((SGBaseTE)te))
								return;
						}
					}
		}
	}
	
	boolean linkToStargate(SGBaseTE gte) {
		if (!isLinkedToStargate && !gte.isLinkedToController && gte.isMerged) {
			System.out.printf(
				"SGControllerTE: Linking controller at (%d, %d, %d) with stargate at (%d, %d, %d)\n",
				xCoord, yCoord, zCoord, gte.xCoord, gte.yCoord, gte.zCoord);
			linkedX = gte.xCoord;
			linkedY = gte.yCoord;
			linkedZ = gte.zCoord;
			isLinkedToStargate = true;
			markBlockForUpdate();
			gte.linkedX = xCoord;
			gte.linkedY = yCoord;
			gte.linkedZ = zCoord;
			gte.isLinkedToController = true;
			gte.markBlockForUpdate();
			return true;
		}
		return false;
	}
	
	public void clearLinkToStargate() {
		System.out.printf("SGControllerTE: Unlinking controller at (%d, %d, %d) from stargate\n",
			xCoord, yCoord, zCoord);
		isLinkedToStargate = false;
		markBlockForUpdate();
	}

}
