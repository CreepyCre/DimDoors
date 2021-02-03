package org.dimdev.dimdoors.pockets.virtual;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.dimdev.dimdoors.pockets.PocketGroup;
import org.dimdev.dimdoors.util.PocketGenerationParameters;
import org.dimdev.dimdoors.util.Weighted;
import org.dimdev.dimdoors.world.pocket.Pocket;

public interface VirtualPocket extends Weighted<PocketGenerationParameters> {

	static VirtualPocket deserialize(Tag tag) {
		if (tag.getType() == 9) { // ListTag
			return VirtualPocketList.deserialize((ListTag) tag);
		}
		return VirtualSingularPocket.deserialize((CompoundTag) tag); // should be CompoundTag
	}

	static Tag serialize(VirtualPocket virtualPocket) {
		if (virtualPocket instanceof VirtualPocketList) {
			return VirtualPocketList.serialize((VirtualPocketList) virtualPocket);
		}
		return VirtualSingularPocket.serialize((VirtualSingularPocket) virtualPocket);
	}


	Pocket prepareAndPlacePocket(PocketGenerationParameters parameters);
}
