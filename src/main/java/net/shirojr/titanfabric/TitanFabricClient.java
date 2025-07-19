package net.shirojr.titanfabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.shirojr.titanfabric.color.TitanFabricColorProviders;
import net.shirojr.titanfabric.entity.client.ArrowItemRenderer;
import net.shirojr.titanfabric.event.TitanFabricEvents;
import net.shirojr.titanfabric.init.TitanFabricBlocks;
import net.shirojr.titanfabric.init.TitanFabricEntities;
import net.shirojr.titanfabric.network.TitanFabricS2CNetworking;
import net.shirojr.titanfabric.init.TitanFabricScreenHandlers;
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

        ModelPredicateProviders.register();
        TitanFabricS2CNetworking.initialize();

        HandledScreens.register(TitanFabricScreenHandlers.BACKPACK, BackPackItemScreen::new);
        HandledScreens.register(TitanFabricScreenHandlers.EXTENDED_INVENTORY, ExtendedInventoryScreen::new);

        BlockRenderLayerMap.INSTANCE.putBlock(TitanFabricBlocks.NETHERITE_ANVIL, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(TitanFabricBlocks.LEGEND_CRYSTAL, RenderLayer.getCutout());

        EntityRendererRegistry.register(TitanFabricEntities.ARROW_ITEM, ArrowItemRenderer::new);
        EntityRendererRegistry.register(TitanFabricEntities.CITRIN_STAR, FlyingItemEntityRenderer::new);
    }
}
