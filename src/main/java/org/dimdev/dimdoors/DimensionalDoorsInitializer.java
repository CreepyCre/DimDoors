package org.dimdev.dimdoors;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import me.sargunvohra.mcmods.autoconfig1u.ConfigHolder;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.command.ModCommands;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.dimdev.dimdoors.entity.stat.ModStats;
import org.dimdev.dimdoors.fluid.ModFluids;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.item.RiftConfigurationToolItem;
import org.dimdev.dimdoors.network.c2s.HitBlockS2CPacket;
import org.dimdev.dimdoors.particle.ModParticleTypes;
import org.dimdev.dimdoors.pockets.SchematicHandler;
import org.dimdev.dimdoors.pockets.SchematicV2Handler;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.dimdev.dimdoors.pockets.virtual.VirtualSingularPocket;
import org.dimdev.dimdoors.pockets.modifier.Modifier;
import org.dimdev.dimdoors.rift.targets.Targets;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.sound.ModSoundEvents;
import org.dimdev.dimdoors.world.ModBiomes;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.feature.ModFeatures;
import org.jetbrains.annotations.NotNull;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class DimensionalDoorsInitializer implements ModInitializer {
    public static final Identifier MONOLITH_PARTICLE_PACKET = new Identifier("dimdoors", "monolith_particle_packet");
	public static ConfigHolder<ModConfig> CONFIG_MANAGER;
	public static ModConfig CONFIG;

	private static Map<UUID, ServerPlayNetworkHandler> UUID_SERVER_PLAY_NETWORK_HANDLER_MAP = new HashMap<>();
	private static MinecraftServer server;

    @NotNull
    public static MinecraftServer getServer() {
        if (server != null) {
            return server;
        }
        throw new UnsupportedOperationException("Accessed server too early!");
    }

    public static ServerWorld getWorld(RegistryKey<World> key) {
        return getServer().getWorld(key);
    }

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register((minecraftServer) -> {
            server = minecraftServer;
        });

        ModBlocks.init();
        ModItems.init();
        ModFeatures.init();
        ModBiomes.init();
        ModDimensions.init();
        ModEntityTypes.init();
		ModStats.init();
        ModBlockEntityTypes.init();
        ModCommands.init();
		ModFluids.init();
        ModSoundEvents.init();
		ModParticleTypes.init();
        ModConfig.init();

        Targets.registerDefaultTargets();
		VirtualTarget.VirtualTargetType.register();

		VirtualSingularPocket.VirtualSingularPocketType.register();

		Modifier.ModifierType.register();

		PocketGenerator.PocketGeneratorType.register();

        SchematicV2Handler.getInstance().load();
        SchematicHandler.INSTANCE.loadSchematics();


		AttackBlockCallback.EVENT.register(RiftConfigurationToolItem::onAttackBlockCallback);

		ServerPlayNetworking.registerGlobalReceiver(HitBlockS2CPacket.ID, RiftConfigurationToolItem::receiveHitBlock);
    }
}
