package net.shirojr.titanfabric.world.feature;

import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.feature.*;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.block.TitanFabricBlocks;
import net.shirojr.titanfabric.util.LoggerUtil;

import java.util.List;

public class TitanFabricConfiguredFeatures {
    public static final List<OreFeatureConfig.Target> OVERWORLD_CITRIN_ORES = List.of(
            OreFeatureConfig.createTarget(OreConfiguredFeatures.STONE_ORE_REPLACEABLES,
                    TitanFabricBlocks.CITRIN_ORE.getDefaultState())
    );
    public static final List<OreFeatureConfig.Target> NETHER_NETHER_ORES = List.of(
            OreFeatureConfig.createTarget(OreConfiguredFeatures.NETHERRACK,
                    TitanFabricBlocks.EMBER_ORE.getDefaultState())
    );
    public static final List<OreFeatureConfig.Target> OVERWORLD_LEGEND_ORES = List.of(
            OreFeatureConfig.createTarget(OreConfiguredFeatures.DEEPSLATE_ORE_REPLACEABLES,
                    TitanFabricBlocks.DEEPSTALE_LEGEND_ORE.getDefaultState())
    );

    public static final RegistryEntry<ConfiguredFeature<OreFeatureConfig, ?>> CITRIN_ORE =
            ConfiguredFeatures.register("citrin_ore", Feature.ORE, new OreFeatureConfig(OVERWORLD_CITRIN_ORES, 9));
    public static final RegistryEntry<ConfiguredFeature<OreFeatureConfig, ?>> CITRIN_ORE_SMALL =
            ConfiguredFeatures.register("citrin_ore_small", Feature.ORE, new OreFeatureConfig(OVERWORLD_CITRIN_ORES, 4));


    public static final RegistryEntry<ConfiguredFeature<OreFeatureConfig, ?>> NETHER_ORE =
            ConfiguredFeatures.register("nether_ore", Feature.ORE, new OreFeatureConfig(NETHER_NETHER_ORES, 9));
    public static final RegistryEntry<ConfiguredFeature<OreFeatureConfig, ?>> NETHER_ORE_BURIED =
            ConfiguredFeatures.register("nether_ore_buried", Feature.ORE, new OreFeatureConfig(NETHER_NETHER_ORES, 9, 0.5f));


    public static final RegistryEntry<ConfiguredFeature<OreFeatureConfig, ?>> LEGEND_ORE_SMALL =
            ConfiguredFeatures.register("legend_ore_small", Feature.ORE, new OreFeatureConfig(OVERWORLD_LEGEND_ORES, 4, 0.5f));
    public static final RegistryEntry<ConfiguredFeature<OreFeatureConfig, ?>> LEGEND_ORE_LARGE =
            ConfiguredFeatures.register("legend_ore_large", Feature.ORE, new OreFeatureConfig(OVERWORLD_LEGEND_ORES, 6, 0.7f));
    public static final RegistryEntry<ConfiguredFeature<OreFeatureConfig, ?>> LEGEND_ORE_BURIED =
            ConfiguredFeatures.register("legend_ore_buried", Feature.ORE, new OreFeatureConfig(OVERWORLD_LEGEND_ORES, 8, 1.0f));

    public static void registerConfiguredFeatures() {
        LoggerUtil.devLogger("Registering " + TitanFabric.MODID + " ConfiguredFeatures");
    }
}
