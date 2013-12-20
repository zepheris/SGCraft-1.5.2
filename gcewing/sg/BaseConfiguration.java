//------------------------------------------------------
//
//   Greg's Mod Base - Configuration
//
//------------------------------------------------------

package gcewing.sg;

import java.io.*;
import net.minecraftforge.common.*;
import cpw.mods.fml.common.registry.*;

public class BaseConfiguration extends Configuration {

	public boolean extended = false;
	int nextVillagerID = 100;
	
	public BaseConfiguration(File file) {
		super(file);
	}
	
	public boolean getBoolean(String category, String key, boolean defaultValue) {
		return get(category, key, defaultValue).getBoolean(defaultValue);
	}
	
	public int getInteger(String category, String key, int defaultValue) {
		return get(category, key, defaultValue).getInt();
	}
	
	public int getVillager(String key, String skin) {
		Property prop = get("villagers", key, -1);
		int id = prop.getInt();
		if (id != -1) {
			VillagerRegistry reg = VillagerRegistry.instance();
			reg.registerVillagerType(id, skin);
		}
		else {
			id = registerNewVillagerType(skin);
			prop.set(id);
		}
		return id;
	}
	
	int registerNewVillagerType(String skin) {
		VillagerRegistry reg = VillagerRegistry.instance();
		int id;
		for (;;) {
			id = nextVillagerID++;
			try {
				reg.registerVillagerType(id, skin);
				return id;
			}
			catch (RuntimeException e) {
			}
		}
	}

	@Override
	public Property get(String category, String key, String defaultValue, String comment, Property.Type type) {
		if (!hasKey(category, key))
			extended = true;
		return super.get(category, key, defaultValue, comment, type);
	}

}
