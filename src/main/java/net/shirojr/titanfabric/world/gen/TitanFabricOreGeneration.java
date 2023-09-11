package net.shirojr.titanfabric.world.gen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.gen.GenerationStep;
import net.shirojr.titanfabric.world.feature.TitanFabricPlacedFeatures;

public class TitanFabricOreGeneration {
    public static void generateOres() {
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES, TitanFabricPlacedFeatures.CITRIN_ORE_UPPER.getKey().get());
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES, TitanFabricPlacedFeatures.CITRIN_ORE_MIDDLE.getKey().get());
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES, TitanFabricPlacedFeatures.CITRIN_ORE_SMALL.getKey().get());

        BiomeModifications.addFeature(BiomeSelectors.foundInTheNether(),
                GenerationStep.Feature.UNDERGROUND_ORES, TitanFabricPlacedFeatures.NETHER_ORE_EXTRA.getKey().get());
        BiomeModifications.addFeature(BiomeSelectors.foundInTheNether(),
                GenerationStep.Feature.UNDERGROUND_ORES, TitanFabricPlacedFeatures.NETHER_ORE.getKey().get());

        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES, TitanFabricPlacedFeatures.LEGEND_ORE.getKey().get());
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES, TitanFabricPlacedFeatures.LEGEND_ORE_BURIED.getKey().get());
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES, TitanFabricPlacedFeatures.LEGEND_ORE_LARGE.getKey().get());

    }
}
