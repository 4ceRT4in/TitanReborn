package net.shirojr.titanfabric;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;
import net.shirojr.titanfabric.datagen.*;
import net.shirojr.titanfabric.world.feature.TitanFabricConfiguredFeatures;
import net.shirojr.titanfabric.world.feature.TitanFabricPlacedFeatures;

public class TitanFabricDatagen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(TitanFabricModelProvider::new);
        pack.addProvider(TitanFabricRecipeProvider::new);
        pack.addProvider(TitanFabricBlockLootTableProvider::new);
        pack.addProvider(TitanFabricRegistryDataGenerator::new);
        TitanFabricTagProviders.addProviders(pack);
    }

    @Override
    public void buildRegistry(RegistryBuilder registryBuilder) {
        DataGeneratorEntrypoint.super.buildRegistry(registryBuilder);

        registryBuilder.addRegistry(RegistryKeys.CONFIGURED_FEATURE, TitanFabricConfiguredFeatures::bootstrap);
        registryBuilder.addRegistry(RegistryKeys.PLACED_FEATURE, TitanFabricPlacedFeatures::bootstrap);
    }
}
