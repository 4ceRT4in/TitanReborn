package net.shirojr.titanfabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.shirojr.titanfabric.block.TitanFabricBlocks;
import net.shirojr.titanfabric.entity.TitanFabricEntities;
import net.shirojr.titanfabric.event.ArmorHandlingEvent;
import net.shirojr.titanfabric.event.KeyBindEvents;
import net.shirojr.titanfabric.event.ParachuteFeatureRendererEventHandler;
import net.shirojr.titanfabric.screen.TitanFabricScreenHandlers;
import net.shirojr.titanfabric.screen.screen.BackPackItemScreen;
import net.shirojr.titanfabric.screen.screen.DiamondFurnaceScreen;
import net.shirojr.titanfabric.util.ModelPredicateProviders;

@Environment(EnvType.CLIENT)
public class TitanFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModelPredicateProviders.register();
        KeyBindEvents.register();
        ArmorHandlingEvent.register();
        TitanFabricEntities.registerClient();

        HandledScreens.register(TitanFabricScreenHandlers.DIAMOND_FURNACE_SCREEN_HANDLER, DiamondFurnaceScreen::new);
        HandledScreens.register(TitanFabricScreenHandlers.BACKPACK_ITEM_SMALL_SCREEN_HANDLER, BackPackItemScreen::new);
        HandledScreens.register(TitanFabricScreenHandlers.BACKPACK_ITEM_MEDIUM_SCREEN_HANDLER, BackPackItemScreen::new);
        HandledScreens.register(TitanFabricScreenHandlers.BACKPACK_ITEM_BIG_SCREEN_HANDLER, BackPackItemScreen::new);

        ParachuteFeatureRendererEventHandler.register();
        BlockRenderLayerMap.INSTANCE.putBlock(TitanFabricBlocks.NETHERITE_ANVIL, RenderLayer.getCutout());

        TitanFabric.LOGGER.info("Initialized all " + TitanFabric.MODID + " client components");
    }
}
