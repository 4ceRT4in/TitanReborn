package net.shirojr.titanfabric;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.block.stats.TitanFabricStats;
import net.shirojr.titanfabric.config.TitanConfig;
import net.shirojr.titanfabric.event.TitanFabricEvents;
import net.shirojr.titanfabric.init.TitanFabricGamerules;
import net.shirojr.titanfabric.init.*;
import net.shirojr.titanfabric.network.TitanFabricC2SNetworking;
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
        TitanConfig.loadConfig();
        TitanFabricConfiguredFeatures.initialize();
        TitanFabricItems.initialize();
        TitanFabricArmorMaterials.initialize();
        TitanFabricRecipeSerializers.initialize();
        TitanFabricBlocks.initialize();
        TitanFabricItemGroups.initialize();
        TitanFabricBlockEntities.initialize();
        TitanFabricEntities.register();
        TitanFabricNetworkingPayloads.initialize();
        TitanFabricScreenHandlers.initialize();
        TitanFabricStatusEffects.initialize();
        TitanFabricPotions.initialize();
        TitanFabricWorldGen.generateWorldGen();
        TitanFabricC2SNetworking.initialize();
        TitanFabricGamerules.initialize();
        TitanFabricDamageTypes.initialize();
        TitanFabricStats.register();
        TitanFabricEvents.registerEvents();

        LOGGER.info("Titans incoming!");
        LoggerUtil.devLogger("Initialized Instance in development environment!");
    }

    public static Identifier getId(String path) {
        return Identifier.of(MOD_ID, path);
    }
}