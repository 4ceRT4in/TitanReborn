package net.shirojr.titanfabric;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.shirojr.titanfabric.data.TitanFabricModelProvider;

public class TitanFabricDatagen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        fabricDataGenerator.addProvider(new TitanFabricModelProvider(fabricDataGenerator));

        TitanFabric.LOGGER.info("Running Datagen task...");
    }
}
