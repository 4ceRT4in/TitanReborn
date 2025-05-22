package net.shirojr.titanfabric.world.feature;

import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.gen.feature.*;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.init.TitanFabricBlocks;

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


    public static void bootstrap(Registerable<ConfiguredFeature<?, ?>> context) {

    }

    private static RegistryKey<ConfiguredFeature<?, ?>> getKey(String name) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, TitanFabric.getId(name));
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<ConfiguredFeature<?, ?>> context,
                                                                                   RegistryKey<ConfiguredFeature<?, ?>> key,
                                                                                   F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }


    public static void initialize() {
        // static init
    }
}
