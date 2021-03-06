package org.dimdev.dimdoors.rift.targets;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.pockets.PocketGenerator;
import org.dimdev.dimdoors.world.level.DimensionalRegistry;
import org.dimdev.dimdoors.util.EntityUtils;
import org.dimdev.dimdoors.util.Location;
import org.dimdev.dimdoors.util.RGBA;
import org.dimdev.dimdoors.world.pocket.Pocket;
import org.dimdev.dimdoors.world.pocket.PrivateRegistry;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;

public class PrivatePocketTarget extends VirtualTarget implements EntityTarget {
	private static final Logger LOGGER = LogManager.getLogger();

	public static final RGBA COLOR = new RGBA(0, 1, 0, 1);

	public PrivatePocketTarget() {
	}

	@Override
	public boolean receiveEntity(Entity entity, float yawOffset) {
		// TODO: make this recursive
		UUID uuid = EntityUtils.getOwner(entity).getUuid();
		VirtualLocation virtualLocation = VirtualLocation.fromLocation(this.location);
		if (uuid != null) {
			Pocket pocket = DimensionalRegistry.getPrivateRegistry().getPrivatePocket(uuid);
			if (pocket == null) { // generate the private pocket and get its entrances
				// set to where the pocket was first created
				pocket = PocketGenerator.generatePrivatePocketV2(new VirtualLocation(virtualLocation.getWorld(), virtualLocation.getX(), virtualLocation.getZ(), -1));

				DimensionalRegistry.getPrivateRegistry().setPrivatePocketID(uuid, pocket);
				BlockEntity be = DimensionalRegistry.getRiftRegistry().getPocketEntrance(pocket).getBlockEntity();
				this.processEntity(pocket, be, entity, uuid, yawOffset);
			} else {
				Location destLoc = DimensionalRegistry.getRiftRegistry().getPrivatePocketEntrance(uuid); // get the last used entrances
				if (destLoc == null)
					destLoc = DimensionalRegistry.getRiftRegistry().getPocketEntrance(pocket); // if there's none, then set the target to the main entrances
				if (destLoc == null) { // if the pocket entrances is gone, then create a new private pocket
					LOGGER.info("All entrances are gone, creating a new private pocket!");
					pocket = PocketGenerator.generatePrivatePocketV2(new VirtualLocation(virtualLocation.getWorld(), virtualLocation.getX(), virtualLocation.getZ(), -1));

					DimensionalRegistry.getPrivateRegistry().setPrivatePocketID(uuid, pocket);
					destLoc = DimensionalRegistry.getRiftRegistry().getPocketEntrance(pocket);
				}

				this.processEntity(pocket, destLoc.getBlockEntity(), entity, uuid, yawOffset);
			}
			return true;
		} else {
			return false;
		}
	}

	private void processEntity(Pocket pocket, BlockEntity blockEntity, Entity entity, UUID uuid, float relativeYaw) {
		if (entity instanceof ItemEntity) {
			Item item = ((ItemEntity) entity).getStack().getItem();

			if (item instanceof DyeItem) {
				if(pocket.addDye(EntityUtils.getOwner(entity), ((DyeItem) item).getColor())) {
					entity.remove();
				} else {
					((EntityTarget) blockEntity).receiveEntity(entity, relativeYaw);
				}
			} else {
				((EntityTarget) blockEntity).receiveEntity(entity, relativeYaw);
			}
		} else {
			((EntityTarget) blockEntity).receiveEntity(entity, relativeYaw);
			DimensionalRegistry.getRiftRegistry().setLastPrivatePocketExit(uuid, this.location);
		}
	}

	@Override
	public VirtualTargetType<? extends VirtualTarget> getType() {
		return VirtualTargetType.PRIVATE;
	}
}
