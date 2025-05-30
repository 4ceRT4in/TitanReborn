package net.shirojr.titanfabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.shirojr.titanfabric.block.TitanFabricBlocks;
import net.shirojr.titanfabric.color.TitanFabricColorProviders;
import net.shirojr.titanfabric.entity.TitanFabricEntities;
import net.shirojr.titanfabric.event.TitanFabricEvents;
import net.shirojr.titanfabric.network.S2CNetworking;
import net.shirojr.titanfabric.particles.GasParticleFactory;
import net.shirojr.titanfabric.particles.GasTextureSheet;
import net.shirojr.titanfabric.screen.TitanFabricScreenHandlers;
import net.shirojr.titanfabric.screen.custom.BackPackItemScreen;
import net.shirojr.titanfabric.screen.custom.DiamondFurnaceScreen;
import net.shirojr.titanfabric.screen.custom.ExtendedInventoryScreen;
import net.shirojr.titanfabric.util.ImageAnimation;
import net.shirojr.titanfabric.util.ModelPredicateProviders;
import net.shirojr.titanfabric.util.TitanFabricKeyBinds;

import java.util.*;

@Environment(EnvType.CLIENT)
public class TitanFabricClient implements ClientModInitializer {

    public static Set<UUID> SOUL_FIRE_ENTITIES = new HashSet<UUID>();
    public static List<ImageAnimation> ANIMATIONS;


    @Override
    public void onInitializeClient() {
        SOUL_FIRE_ENTITIES.clear();
        ANIMATIONS = new ArrayList<>();
        TitanFabricEvents.registerClientEvents();
        TitanFabricKeyBinds.register();
        TitanFabricColorProviders.register();
        TitanFabricEntities.registerClient();

        ModelPredicateProviders.register();
        S2CNetworking.registerClientReceivers();

        HandledScreens.register(TitanFabricScreenHandlers.BACKPACK_ITEM_SMALL_SCREEN_HANDLER, BackPackItemScreen::new);
        HandledScreens.register(TitanFabricScreenHandlers.BACKPACK_ITEM_MEDIUM_SCREEN_HANDLER, BackPackItemScreen::new);
        HandledScreens.register(TitanFabricScreenHandlers.BACKPACK_ITEM_BIG_SCREEN_HANDLER, BackPackItemScreen::new);
        HandledScreens.register(TitanFabricScreenHandlers.POTION_BUNDLE_SCREEN_HANDLER, BackPackItemScreen::new);
        HandledScreens.register(TitanFabricScreenHandlers.EXTENDED_INVENTORY_SCREEN_HANDLER, ExtendedInventoryScreen::new);
        HandledScreens.register(TitanFabricScreenHandlers.DIAMOND_FURNACE_SCREEN_HANDLER, DiamondFurnaceScreen::new);

        BlockRenderLayerMap.INSTANCE.putBlock(TitanFabricBlocks.NETHERITE_ANVIL, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(TitanFabricBlocks.LEGEND_CRYSTAL, RenderLayer.getCutout());

        GasTextureSheet.initialize();
        ParticleFactoryRegistry.getInstance().register(TitanFabric.GAS_PARTICLE, GasParticleFactory::new);

        TitanFabric.LOGGER.info("Initialized all " + TitanFabric.MODID + " client components");
    }
}
