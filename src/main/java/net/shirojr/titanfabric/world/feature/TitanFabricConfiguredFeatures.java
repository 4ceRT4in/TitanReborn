package net.shirojr.titanfabric.world.feature;

import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.init.TitanFabricBlocks;

import java.util.List;

public class TitanFabricConfiguredFeatures {
    public static final RegistryKey<ConfiguredFeature<?, ?>> CITRIN_ORE = getKey("citrin_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>> CITRIN_ORE_SMALL = getKey("citrin_ore_small");
    public static final RegistryKey<ConfiguredFeature<?, ?>> EMBER_ORE = getKey("ember_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>> EMBER_ORE_BURIED = getKey("ember_ore_buried");
    public static final RegistryKey<ConfiguredFeature<?, ?>> LEGEND_ORE = getKey("legend_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>> LEGEND_ORE_SMALL = getKey("legend_ore_small");
    public static final RegistryKey<ConfiguredFeature<?, ?>> LEGEND_ORE_BURIED = getKey("legend_ore_buried");

    public static void bootstrap(Registerable<ConfiguredFeature<?, ?>> context) {
        RuleTest stoneReplacables = new TagMatchRuleTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest deepslateReplacables = new TagMatchRuleTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        RuleTest netherrackReplacables = new TagMatchRuleTest(BlockTags.BASE_STONE_NETHER);

        List<OreFeatureConfig.Target> overworldCitrinOres = List.of(
                OreFeatureConfig.createTarget(stoneReplacables, TitanFabricBlocks.CITRIN_ORE.getDefaultState())
        );
        List<OreFeatureConfig.Target> netherEmberOres = List.of(
                OreFeatureConfig.createTarget(netherrackReplacables, TitanFabricBlocks.EMBER_ORE.getDefaultState())
        );
        List<OreFeatureConfig.Target> overworldLegendOres = List.of(
                //OreFeatureConfig.createTarget(stoneReplacables, TitanFabricBlocks.LEGEND_ORE.getDefaultState()),
                OreFeatureConfig.createTarget(deepslateReplacables, TitanFabricBlocks.DEEPSLATE_LEGEND_ORE.getDefaultState())
        );

        register(context, CITRIN_ORE, Feature.ORE, new OreFeatureConfig(overworldCitrinOres, 9));
        register(context, CITRIN_ORE_SMALL, Feature.ORE, new OreFeatureConfig(overworldCitrinOres, 4));
        register(context, EMBER_ORE, Feature.ORE, new OreFeatureConfig(netherEmberOres, 9));
        register(context, EMBER_ORE_BURIED, Feature.ORE, new OreFeatureConfig(netherEmberOres, 9, 0.5f));
        register(context, LEGEND_ORE, Feature.ORE, new OreFeatureConfig(overworldLegendOres, 6, 0.7f));
        register(context, LEGEND_ORE_SMALL, Feature.ORE, new OreFeatureConfig(overworldLegendOres, 4, 0.5f));
        register(context, LEGEND_ORE_BURIED, Feature.ORE, new OreFeatureConfig(overworldLegendOres, 8, 1.0f));
    }

    private static RegistryKey<ConfiguredFeature<?, ?>> getKey(String name) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, TitanFabric.getId(name));
    }

    @SuppressWarnings("SameParameterValue")
    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(
            Registerable<ConfiguredFeature<?, ?>> context, RegistryKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
            context.register(key, new ConfiguredFeature<>(feature, configuration));
    }


    public static void initialize() {
        // static init
    }
}
