package net.shirojr.titanfabric.datagen;


import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.TexturedModel;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.init.TitanFabricBlocks;

import static net.minecraft.data.client.BlockStateModelGenerator.createSouthDefaultHorizontalRotationStates;

public class TitanFabricModelProvider extends FabricModelProvider {
    public TitanFabricModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator generator) {
        generator.registerSimpleCubeAll(TitanFabricBlocks.CITRIN_BLOCK);
        generator.registerSimpleCubeAll(TitanFabricBlocks.CITRIN_ORE);
        generator.registerCooker(TitanFabricBlocks.DIAMOND_FURNACE, TexturedModel.ORIENTABLE);
        generator.registerSimpleCubeAll(TitanFabricBlocks.EMBER_BLOCK);
        generator.registerSimpleCubeAll(TitanFabricBlocks.EMBER_ORE);
        generator.registerSimpleCubeAll(TitanFabricBlocks.DEEPSTALE_LEGEND_ORE);
        generator.registerSimpleCubeAll(TitanFabricBlocks.LEGEND_BLOCK);
        generator.registerAmethyst(TitanFabricBlocks.LEGEND_CRYSTAL);
        generator.blockStateCollector.accept(
                BlockStateModelGenerator.createSingletonBlockState(TitanFabricBlocks.NETHERITE_ANVIL, TitanFabric.getId("block/netherite_anvil"))
                        .coordinate(createSouthDefaultHorizontalRotationStates()));
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {

    }

}
