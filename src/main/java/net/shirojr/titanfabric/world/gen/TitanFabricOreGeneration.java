package net.shirojr.titanfabric.world.gen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.gen.GenerationStep;
import net.shirojr.titanfabric.world.feature.TitanFabricPlacedFeatures;

public class TitanFabricOreGeneration {
    public static void generateOres() {
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES, TitanFabricPlacedFeatures.CITRIN_ORE_UPPER);
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES, TitanFabricPlacedFeatures.CITRIN_ORE_MIDDLE);
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES, TitanFabricPlacedFeatures.CITRIN_ORE_SMALL);

        BiomeModifications.addFeature(BiomeSelectors.foundInTheNether(),
                GenerationStep.Feature.UNDERGROUND_ORES, TitanFabricPlacedFeatures.EMBER_ORE_EXTRA);
        BiomeModifications.addFeature(BiomeSelectors.foundInTheNether(),
                GenerationStep.Feature.UNDERGROUND_ORES, TitanFabricPlacedFeatures.EMBER_ORE);

        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES, TitanFabricPlacedFeatures.LEGEND_ORE);
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES, TitanFabricPlacedFeatures.LEGEND_ORE_BURIED);
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES, TitanFabricPlacedFeatures.LEGEND_ORE_LARGE);

    }
}
