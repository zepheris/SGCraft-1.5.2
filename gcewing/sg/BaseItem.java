//------------------------------------------------------------------------------------------------
//
//   Greg's Mod Base - Generic Textured Item
//
//------------------------------------------------------------------------------------------------

package gcewing.sg;

import net.minecraft.client.renderer.texture.*;
import net.minecraft.creativetab.*;
import net.minecraft.item.*;

public class BaseItem extends Item {

	String textureName;

	public BaseItem(int id, String texture) {
		super(id);
		textureName = texture;
		setCreativeTab(CreativeTabs.tabMisc);
	}
	
	public void updateIcons(IconRegister reg) {
		System.out.printf("BaseItem.updateIcons: %s\n", textureName);
		itemIcon = reg.registerIcon(textureName);
	}

//	@Override
//	public String getTextureFile() {
//		return textureFile;
//	}

}
