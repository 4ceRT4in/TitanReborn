package net.shirojr.titanfabric.world.feature;

import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.feature.*;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.block.TitanFabricBlocks;

import java.util.List;

public class TitanFabricConfiguredFeatures {
    public static final List<OreFeatureConfig.Target> OVERWORLD_CITRIN_ORES = List.of(
            OreFeatureConfig.createTarget(OreConfiguredFeatures.STONE_ORE_REPLACEABLES,
                    TitanFabricBlocks.CITRIN_ORE.getDefaultState())
    );
    public static final List<OreFeatureConfig.Target> NETHER_NETHER_ORES = List.of(
            OreFeatureConfig.createTarget(OreConfiguredFeatures.NETHERRACK,
                    TitanFabricBlocks.NETHER_ORE.getDefaultState())
    );
    public static final List<OreFeatureConfig.Target> OVERWORLD_LEGEND_ORES = List.of(
            OreFeatureConfig.createTarget(OreConfiguredFeatures.STONE_ORE_REPLACEABLES,
                    TitanFabricBlocks.LEGEND_ORE.getDefaultState())
    );

    public static final RegistryEntry<ConfiguredFeature<OreFeatureConfig, ?>> CITRIN_ORE =
            ConfiguredFeatures.register("citrin_ore", Feature.ORE, new OreFeatureConfig(OVERWORLD_CITRIN_ORES, 5));
    public static final RegistryEntry<ConfiguredFeature<OreFeatureConfig, ?>> NETHER_ORE =
            ConfiguredFeatures.register("nether_ore", Feature.ORE, new OreFeatureConfig(NETHER_NETHER_ORES, 7));
    public static final RegistryEntry<ConfiguredFeature<OreFeatureConfig, ?>> LEGEND_ORE =
            ConfiguredFeatures.register("legend_ore", Feature.ORE, new OreFeatureConfig(OVERWORLD_LEGEND_ORES, 3));

    public static void registerConfiguredFeatures() {
        TitanFabric.LOGGER.info("Registering " + TitanFabric.MODID + " ConfiguredFeatures");
    }
}
