package net.shirojr.titanfabric.world.feature;

import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.OreConfiguredFeatures;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.shirojr.titanfabric.TitanFabric;

import java.util.List;

public class TitanFabricPlacedFeatures {
    public static final RegistryKey<PlacedFeature> DIAMOND_ORE = getKey("diamond_ore_reduced_placed");
    public static final RegistryKey<PlacedFeature> DIAMOND_ORE_MEDIUM = getKey("diamond_ore_medium_reduced_placed");
    public static final RegistryKey<PlacedFeature> DIAMOND_ORE_LARGE = getKey("diamond_ore_large_reduced_placed");
    public static final RegistryKey<PlacedFeature> DIAMOND_ORE_BURIED = getKey("diamond_ore_buried_reduced_placed");

    public static final RegistryKey<PlacedFeature> CITRIN_ORE_UPPER = getKey("citrin_ore_upper_placed");
    public static final RegistryKey<PlacedFeature> CITRIN_ORE_MIDDLE = getKey("citrin_ore_middle_placed");
    public static final RegistryKey<PlacedFeature> CITRIN_ORE_SMALL = getKey("citrin_ore_small_placed");

    public static final RegistryKey<PlacedFeature> EMBER_ORE = getKey("ember_ore_placed");
    public static final RegistryKey<PlacedFeature> EMBER_ORE_EXTRA = getKey("ember_ore_extra_placed");

    public static final RegistryKey<PlacedFeature> LEGEND_ORE = getKey("legend_ore_placed");
    public static final RegistryKey<PlacedFeature> LEGEND_ORE_MEDIUM = getKey("legend_ore_medium_placed");
    public static final RegistryKey<PlacedFeature> LEGEND_ORE_LARGE = getKey("legend_ore_large_placed");
    public static final RegistryKey<PlacedFeature> LEGEND_ORE_BURIED = getKey("legend_ore_buried_placed");


    public static void bootstrap(Registerable<PlacedFeature> context) {
        var configuredFeatures = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);

        register(context, DIAMOND_ORE, configuredFeatures.getOrThrow(OreConfiguredFeatures.ORE_DIAMOND_SMALL),
                TitanFabricOrePlacement.modifiersWithCount(UniformIntProvider.create(5, 6),
                        HeightRangePlacementModifier.trapezoid(
                                YOffset.aboveBottom(-80),
                                YOffset.aboveBottom(80)
                        )
                )
        );
        register(context, DIAMOND_ORE_MEDIUM, configuredFeatures.getOrThrow(OreConfiguredFeatures.ORE_DIAMOND_MEDIUM),
                TitanFabricOrePlacement.modifiersWithCount(UniformIntProvider.create(1, 2),
                        HeightRangePlacementModifier.uniform(
                                YOffset.fixed(-64),
                                YOffset.fixed(-4)
                        )
                )
        );
        register(context, DIAMOND_ORE_LARGE, configuredFeatures.getOrThrow(OreConfiguredFeatures.ORE_DIAMOND_LARGE),
                TitanFabricOrePlacement.modifiersWithRarity(11,
                        HeightRangePlacementModifier.trapezoid(
                                YOffset.aboveBottom(-80),
                                YOffset.aboveBottom(80)
                        )
                )
        );
        register(context, DIAMOND_ORE_BURIED, configuredFeatures.getOrThrow(OreConfiguredFeatures.ORE_DIAMOND_BURIED),
                TitanFabricOrePlacement.modifiersWithCount(UniformIntProvider.create(3, 4),
                        HeightRangePlacementModifier.trapezoid(
                                YOffset.aboveBottom(-80),
                                YOffset.aboveBottom(80)
                        )
                )
        );

        register(context, CITRIN_ORE_UPPER, configuredFeatures.getOrThrow(TitanFabricConfiguredFeatures.CITRIN_ORE),
                TitanFabricOrePlacement.modifiersWithCount(72,
                        HeightRangePlacementModifier.trapezoid(
                                YOffset.fixed(80),
                                YOffset.fixed(384)
                        )
                )
        );
        register(context, CITRIN_ORE_MIDDLE, configuredFeatures.getOrThrow(TitanFabricConfiguredFeatures.CITRIN_ORE),
                TitanFabricOrePlacement.modifiersWithCount(8,
                        HeightRangePlacementModifier.trapezoid(
                                YOffset.fixed(0),
                                YOffset.fixed(56)
                        )
                )
        );
        register(context, CITRIN_ORE_SMALL, configuredFeatures.getOrThrow(TitanFabricConfiguredFeatures.CITRIN_ORE_SMALL),
                TitanFabricOrePlacement.modifiersWithCount(8,
                        HeightRangePlacementModifier.uniform(
                                YOffset.fixed(0),
                                YOffset.fixed(72)
                        )
                )
        );

        register(context, EMBER_ORE, configuredFeatures.getOrThrow(TitanFabricConfiguredFeatures.EMBER_ORE),
                TitanFabricOrePlacement.modifiersWithCount(8,
                        HeightRangePlacementModifier.uniform(
                                YOffset.aboveBottom(10),
                                YOffset.belowTop(10)
                        )
                )
        );
        register(context, EMBER_ORE_EXTRA, configuredFeatures.getOrThrow(TitanFabricConfiguredFeatures.EMBER_ORE),
                TitanFabricOrePlacement.modifiersWithCount(8,
                        HeightRangePlacementModifier.uniform(
                                YOffset.aboveBottom(10),
                                YOffset.belowTop(10)
                        )
                )
        );

        register(context, LEGEND_ORE, configuredFeatures.getOrThrow(TitanFabricConfiguredFeatures.LEGEND_ORE_SMALL),
                TitanFabricOrePlacement.modifiersWithCount(UniformIntProvider.create(4, 5),
                        HeightRangePlacementModifier.trapezoid(
                                YOffset.aboveBottom(-80),
                                YOffset.aboveBottom(80)
                        )
                )
        );
        register(context, LEGEND_ORE_MEDIUM, configuredFeatures.getOrThrow(TitanFabricConfiguredFeatures.LEGEND_ORE_MEDIUM),
                TitanFabricOrePlacement.modifiersWithCount(1,
                        HeightRangePlacementModifier.uniform(
                                YOffset.fixed(-64),
                                YOffset.fixed(-4)
                        )
                )
        );
        register(context, LEGEND_ORE_LARGE, configuredFeatures.getOrThrow(TitanFabricConfiguredFeatures.LEGEND_ORE),
                TitanFabricOrePlacement.modifiersWithRarity(14,
                        HeightRangePlacementModifier.trapezoid(
                                YOffset.aboveBottom(-80),
                                YOffset.aboveBottom(80)
                        )
                )
        );
        register(context, LEGEND_ORE_BURIED, configuredFeatures.getOrThrow(TitanFabricConfiguredFeatures.LEGEND_ORE_BURIED),
                TitanFabricOrePlacement.modifiersWithCount(UniformIntProvider.create(2, 3),
                        HeightRangePlacementModifier.trapezoid(
                                YOffset.aboveBottom(-80),
                                YOffset.aboveBottom(80)
                        )
                )
        );
    }

    private static RegistryKey<PlacedFeature> getKey(String name) {
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE, TitanFabric.getId(name));
    }

    private static void register(Registerable<PlacedFeature> context, RegistryKey<PlacedFeature> key,
                                 RegistryEntry<ConfiguredFeature<?, ?>> configuredFeature, List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuredFeature, List.copyOf(modifiers)));
    }
}
