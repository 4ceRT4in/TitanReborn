package net.shirojr.titanfabric.world.feature;

import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;

public class TitanFabricPlacedFeatures {
    public static final RegistryEntry<PlacedFeature> CITRIN_ORE_PLACED = PlacedFeatures.register("citrin_ore_placed",
            TitanFabricConfiguredFeatures.CITRIN_ORE, TitanFabricOreFeatures.modifiersWithCount(3,
                    HeightRangePlacementModifier.trapezoid(YOffset.aboveBottom(0), YOffset.aboveBottom(40))));
    public static final RegistryEntry<PlacedFeature> NETHER_ORE_PLACED = PlacedFeatures.register("nether_ore_placed",
            TitanFabricConfiguredFeatures.NETHER_ORE, TitanFabricOreFeatures.modifiersWithCount(9,
                    HeightRangePlacementModifier.trapezoid(YOffset.aboveBottom(-80), YOffset.aboveBottom(80))));
    public static final RegistryEntry<PlacedFeature> LEGEND_ORE_PLACED = PlacedFeatures.register("legend_ore_placed",
            TitanFabricConfiguredFeatures.LEGEND_ORE, TitanFabricOreFeatures.modifiersWithCount(1,
                    HeightRangePlacementModifier.trapezoid(YOffset.aboveBottom(-64), YOffset.aboveBottom(-30))));
}
