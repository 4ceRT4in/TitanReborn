package net.shirojr.titanfabric.world.feature;

import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;

public class TitanFabricPlacedFeatures {
    public static final RegistryEntry<PlacedFeature> CITRIN_ORE_UPPER = PlacedFeatures.register("citrin_ore_upper",
            TitanFabricConfiguredFeatures.CITRIN_ORE, TitanFabricOreFeatures.modifiersWithCount(7,
                    HeightRangePlacementModifier.trapezoid(YOffset.aboveBottom(80), YOffset.aboveBottom(384))));
    public static final RegistryEntry<PlacedFeature> CITRIN_ORE_MIDDLE = PlacedFeatures.register("citrin_ore_middle",
            TitanFabricConfiguredFeatures.CITRIN_ORE, TitanFabricOreFeatures.modifiersWithCount(6,
                    HeightRangePlacementModifier.trapezoid(YOffset.aboveBottom(0), YOffset.aboveBottom(56))));
    public static final RegistryEntry<PlacedFeature> CITRIN_ORE_SMALL = PlacedFeatures.register("citrin_ore_small",
            TitanFabricConfiguredFeatures.CITRIN_ORE_SMALL, TitanFabricOreFeatures.modifiersWithCount(6,
                    HeightRangePlacementModifier.trapezoid(YOffset.fixed(0), YOffset.fixed(72))));

    //------------------------------------------------------------------------------------------------------------------

    public static final RegistryEntry<PlacedFeature> NETHER_ORE_EXTRA = PlacedFeatures.register("nether_ore_extra",
            TitanFabricConfiguredFeatures.NETHER_ORE, TitanFabricOreFeatures.modifiersWithCount(30,
                    HeightRangePlacementModifier.uniform(YOffset.fixed(32), YOffset.fixed(256))));
    public static final RegistryEntry<PlacedFeature> NETHER_ORE = PlacedFeatures.register("nether_ore",
            TitanFabricConfiguredFeatures.NETHER_ORE_BURIED, TitanFabricOreFeatures.modifiersWithCount(2,
                    HeightRangePlacementModifier.trapezoid(YOffset.fixed(10), YOffset.fixed(90))));

    //------------------------------------------------------------------------------------------------------------------
    public static final RegistryEntry<PlacedFeature> LEGEND_ORE = PlacedFeatures.register("legend_ore",
            TitanFabricConfiguredFeatures.LEGEND_ORE_SMALL, TitanFabricOreFeatures.modifiersWithCount(3,
                    HeightRangePlacementModifier.trapezoid(YOffset.aboveBottom(-80), YOffset.fixed(0))));
    public static final RegistryEntry<PlacedFeature> LEGEND_ORE_LARGE = PlacedFeatures.register("legend_ore_large",
            TitanFabricConfiguredFeatures.LEGEND_ORE_LARGE, TitanFabricOreFeatures.modifiersWithCount(5,
                    HeightRangePlacementModifier.trapezoid(YOffset.aboveBottom(-80), YOffset.fixed(0))));
    public static final RegistryEntry<PlacedFeature> LEGEND_ORE_BURIED = PlacedFeatures.register("legend_ore_buried",
            TitanFabricConfiguredFeatures.LEGEND_ORE_BURIED, TitanFabricOreFeatures.modifiersWithCount(2,
                    HeightRangePlacementModifier.trapezoid(YOffset.aboveBottom(-80), YOffset.fixed(0))));

    
}
