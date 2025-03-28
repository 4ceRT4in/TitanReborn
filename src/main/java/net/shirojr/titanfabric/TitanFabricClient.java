package net.shirojr.titanfabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.shirojr.titanfabric.init.TitanFabricBlocks;
import net.shirojr.titanfabric.color.TitanFabricColorProviders;
import net.shirojr.titanfabric.entity.TitanFabricEntities;
import net.shirojr.titanfabric.event.TitanFabricEvents;
import net.shirojr.titanfabric.init.TitanFabricParticles;
import net.shirojr.titanfabric.particles.GasParticleFactory;
import net.shirojr.titanfabric.particles.GasTextureSheet;
import net.shirojr.titanfabric.screen.TitanFabricScreenHandlers;
import net.shirojr.titanfabric.screen.custom.BackPackItemScreen;
import net.shirojr.titanfabric.screen.custom.ExtendedInventoryScreen;
import net.shirojr.titanfabric.util.ModelPredicateProviders;
import net.shirojr.titanfabric.util.TitanFabricKeyBinds;

@Environment(EnvType.CLIENT)
public class TitanFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        TitanFabricEvents.registerClientEvents();
        TitanFabricKeyBinds.register();
        TitanFabricColorProviders.register();
        TitanFabricEntities.registerClient();
        TitanFabricParticles.initialize();

        ModelPredicateProviders.register();
        TitanFabricS2CNetworking.registerClientReceivers();

        HandledScreens.register(TitanFabricScreenHandlers.BACKPACK_ITEM_SMALL_SCREEN_HANDLER, BackPackItemScreen::new);
        HandledScreens.register(TitanFabricScreenHandlers.BACKPACK_ITEM_MEDIUM_SCREEN_HANDLER, BackPackItemScreen::new);
        HandledScreens.register(TitanFabricScreenHandlers.BACKPACK_ITEM_BIG_SCREEN_HANDLER, BackPackItemScreen::new);
        HandledScreens.register(TitanFabricScreenHandlers.EXTENDED_INVENTORY_SCREEN_HANDLER, ExtendedInventoryScreen::new);

        BlockRenderLayerMap.INSTANCE.putBlock(TitanFabricBlocks.NETHERITE_ANVIL, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(TitanFabricBlocks.LEGEND_CRYSTAL, RenderLayer.getCutout());

        GasTextureSheet.initialize();
        ParticleFactoryRegistry.getInstance().register(TitanFabricParticles.GAS_PARTICLE, GasParticleFactory::new);
    }
}
