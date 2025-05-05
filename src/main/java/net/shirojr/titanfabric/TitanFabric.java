package net.shirojr.titanfabric;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.init.*;
import net.shirojr.titanfabric.block.stats.TitanFabricStats;
import net.shirojr.titanfabric.init.TitanFabricStatusEffects;
import net.shirojr.titanfabric.init.TitanFabricEntities;
import net.shirojr.titanfabric.event.TitanFabricEvents;
import net.shirojr.titanfabric.gamerule.TitanFabricGamerules;
import net.shirojr.titanfabric.network.TitanFabricC2SNetworking;
import net.shirojr.titanfabric.potion.TitanFabricPotions;
import net.shirojr.titanfabric.init.TitanFabricRecipeSerializers;
import net.shirojr.titanfabric.screen.TitanFabricScreenHandlers;
import net.shirojr.titanfabric.util.LoggerUtil;
import net.shirojr.titanfabric.world.feature.TitanFabricConfiguredFeatures;
import net.shirojr.titanfabric.world.gen.TitanFabricWorldGen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TitanFabric implements ModInitializer {

    public static final String MOD_ID = "titanfabric";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        TitanFabricConfiguredFeatures.registerConfiguredFeatures();
        TitanFabricItems.initialize();
        TitanFabricArmorMaterials.initialize();
        TitanFabricRecipeSerializers.initialize();
        TitanFabricBlocks.initialize();
        TitanFabricItemGroups.initialize();
        TitanFabricBlockEntities.initialize();
        TitanFabricEntities.register();
        TitanFabricNetworkingPayloads.initialize();
        TitanFabricScreenHandlers.register();
        TitanFabricStatusEffects.initialize();
        TitanFabricPotions.register();
        TitanFabricWorldGen.generateWorldGen();
        TitanFabricC2SNetworking.initialize();
        TitanFabricGamerules.register();
        TitanFabricStats.register();
        TitanFabricEvents.registerEvents();

        LOGGER.info("Titans incoming!");
        LoggerUtil.devLogger("Initialized Instance in development environment!");
    }

    public static Identifier getId(String path) {
        return Identifier.of(MOD_ID, path);
    }
}