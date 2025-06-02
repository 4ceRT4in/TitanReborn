package net.shirojr.titanfabric.datagen;


import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.*;
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

        generator.registerSimpleCubeAll(TitanFabricBlocks.EMBER_BLOCK);
        generator.registerSimpleCubeAll(TitanFabricBlocks.EMBER_ORE);

        generator.registerSimpleCubeAll(TitanFabricBlocks.DEEPSTALE_LEGEND_ORE);
        generator.registerSimpleCubeAll(TitanFabricBlocks.LEGEND_BLOCK);

        registerAmethystWithItem(generator, TitanFabricBlocks.LEGEND_CRYSTAL);

        generator.blockStateCollector.accept(BlockStateModelGenerator
                .createSingletonBlockState(TitanFabricBlocks.NETHERITE_ANVIL, TitanFabric.getId("block/netherite_anvil"))
                .coordinate(createSouthDefaultHorizontalRotationStates())
        );

        generator.registerCooker(TitanFabricBlocks.DIAMOND_FURNACE, TexturedModel.ORIENTABLE);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {

    }

    private static void registerAmethystWithItem(BlockStateModelGenerator generator, Block block) {
        generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(
                        VariantSettings.MODEL, Models.CROSS.upload(block, TextureMap.cross(block), generator.modelCollector))
                ).coordinate(generator.createUpDefaultFacingVariantMap())
        );
    }
}
