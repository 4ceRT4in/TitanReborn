package net.shirojr.titanfabric.world.feature;

import net.minecraft.world.gen.placementmodifier.*;

import java.util.List;

public class TitanFabricOrePlacement {
    public static List<PlacementModifier> modifiers(PlacementModifier countModifier, PlacementModifier heightModifier) {
        return List.of(countModifier, SquarePlacementModifier.of(), heightModifier, BiomePlacementModifier.of());
    }

    public static List<PlacementModifier> modifiersWithCount(int count, PlacementModifier heightModifier) {
        return List.of(CountPlacementModifier.of(count), heightModifier);
    }

    public static List<PlacementModifier> modifiersWithRarity(int chance, PlacementModifier heightModifier) {
        return List.of(RarityFilterPlacementModifier.of(chance), heightModifier);
    }
}
