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
import net.shirojr.titanfabric.screen.custom.DiamondFurnaceScreen;
import net.shirojr.titanfabric.screen.custom.ExtendedInventoryScreen;
import net.shirojr.titanfabric.util.ModelPredicateProviders;
import net.shirojr.titanfabric.util.TitanFabricKeyBinds;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class TitanFabricClient implements ClientModInitializer {

    public static Set<UUID> SOUL_FIRE_ENTITIES = new HashSet<UUID>();

    @Override
    public void onInitializeClient() {
        SOUL_FIRE_ENTITIES.clear();
        TitanFabricEvents.registerClientEvents();
        TitanFabricKeyBinds.register();
        TitanFabricColorProviders.register();

        ModelPredicateProviders.register();
        TitanFabricS2CNetworking.initialize();

        HandledScreens.register(TitanFabricScreenHandlers.BACKPACK_ITEM_SMALL_SCREEN_HANDLER, BackPackItemScreen::new);
        HandledScreens.register(TitanFabricScreenHandlers.BACKPACK_ITEM_MEDIUM_SCREEN_HANDLER, BackPackItemScreen::new);
        HandledScreens.register(TitanFabricScreenHandlers.BACKPACK_ITEM_BIG_SCREEN_HANDLER, BackPackItemScreen::new);
        HandledScreens.register(TitanFabricScreenHandlers.POTION_BUNDLE_SCREEN_HANDLER, BackPackItemScreen::new);
        HandledScreens.register(TitanFabricScreenHandlers.EXTENDED_INVENTORY, ExtendedInventoryScreen::new);
        HandledScreens.register(TitanFabricScreenHandlers.DIAMOND_FURNACE_SCREEN_HANDLER, DiamondFurnaceScreen::new);

        BlockRenderLayerMap.INSTANCE.putBlock(TitanFabricBlocks.NETHERITE_ANVIL, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(TitanFabricBlocks.LEGEND_CRYSTAL, RenderLayer.getCutout());

        EntityRendererRegistry.register(TitanFabricEntities.ARROW_ITEM, ArrowItemRenderer::new);
        EntityRendererRegistry.register(TitanFabricEntities.CITRIN_STAR, FlyingItemEntityRenderer::new);
    }
}
