package net.shirojr.titanfabric.world.gen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.OrePlacedFeatures;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.world.feature.TitanFabricPlacedFeatures;

public class TitanFabricOreGeneration {
    public static void generateOres() {
        BiomeModifications.create(TitanFabric.getId("diamond_ore_rebalance"))
                .add(ModificationPhase.REPLACEMENTS, BiomeSelectors.foundInOverworld(), context -> {
                    context.getGenerationSettings().removeFeature(GenerationStep.Feature.UNDERGROUND_ORES, OrePlacedFeatures.ORE_DIAMOND);
                    context.getGenerationSettings().removeFeature(GenerationStep.Feature.UNDERGROUND_ORES, OrePlacedFeatures.ORE_DIAMOND_MEDIUM);
                    context.getGenerationSettings().removeFeature(GenerationStep.Feature.UNDERGROUND_ORES, OrePlacedFeatures.ORE_DIAMOND_LARGE);
                    context.getGenerationSettings().removeFeature(GenerationStep.Feature.UNDERGROUND_ORES, OrePlacedFeatures.ORE_DIAMOND_BURIED);

                    context.getGenerationSettings().addFeature(GenerationStep.Feature.UNDERGROUND_ORES, TitanFabricPlacedFeatures.DIAMOND_ORE);
                    context.getGenerationSettings().addFeature(GenerationStep.Feature.UNDERGROUND_ORES, TitanFabricPlacedFeatures.DIAMOND_ORE_MEDIUM);
                    context.getGenerationSettings().addFeature(GenerationStep.Feature.UNDERGROUND_ORES, TitanFabricPlacedFeatures.DIAMOND_ORE_LARGE);
                    context.getGenerationSettings().addFeature(GenerationStep.Feature.UNDERGROUND_ORES, TitanFabricPlacedFeatures.DIAMOND_ORE_BURIED);
                });

        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES, TitanFabricPlacedFeatures.CITRIN_ORE_UPPER);
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES, TitanFabricPlacedFeatures.CITRIN_ORE_MIDDLE);
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES, TitanFabricPlacedFeatures.CITRIN_ORE_SMALL);

        BiomeModifications.addFeature(BiomeSelectors.foundInTheNether(),
                GenerationStep.Feature.UNDERGROUND_ORES, TitanFabricPlacedFeatures.EMBER_ORE);

        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES, TitanFabricPlacedFeatures.LEGEND_ORE);
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES, TitanFabricPlacedFeatures.LEGEND_ORE_MEDIUM);
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES, TitanFabricPlacedFeatures.LEGEND_ORE_BURIED);
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES, TitanFabricPlacedFeatures.LEGEND_ORE_LARGE);

    }
}
