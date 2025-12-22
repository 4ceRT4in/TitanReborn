package net.shirojr.titanfabric.datagen;


import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.data.client.TexturedModel;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.init.TitanFabricBlocks;
import net.shirojr.titanfabric.init.TitanFabricItems;

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

        generator.registerSimpleCubeAll(TitanFabricBlocks.DEEPSLATE_LEGEND_ORE);
        generator.registerSimpleCubeAll(TitanFabricBlocks.LEGEND_BLOCK);

        generator.registerAmethyst(TitanFabricBlocks.LEGEND_CRYSTAL);
        // registerAmethystWithItem(generator, TitanFabricBlocks.LEGEND_CRYSTAL);

        generator.blockStateCollector.accept(BlockStateModelGenerator
                .createSingletonBlockState(TitanFabricBlocks.NETHERITE_ANVIL, TitanFabric.getId("block/netherite_anvil"))
                .coordinate(createSouthDefaultHorizontalRotationStates())
        );

        generator.registerCooker(TitanFabricBlocks.DIAMOND_FURNACE, TexturedModel.ORIENTABLE);
    }

    @Override
    public void generateItemModels(ItemModelGenerator generator) {
        generator.register(TitanFabricBlocks.LEGEND_CRYSTAL.asItem(), Models.GENERATED);
        generator.registerArmor(TitanFabricItems.LEGEND_HELMET);
        generator.registerArmor(TitanFabricItems.LEGEND_CHESTPLATE);
        generator.registerArmor(TitanFabricItems.LEGEND_LEGGINGS);
        generator.registerArmor(TitanFabricItems.LEGEND_BOOTS);

        generator.registerArmor(TitanFabricItems.CITRIN_HELMET);
        generator.registerArmor(TitanFabricItems.CITRIN_CHESTPLATE);
        generator.registerArmor(TitanFabricItems.CITRIN_LEGGINGS);
        generator.registerArmor(TitanFabricItems.CITRIN_BOOTS);

        generator.registerArmor(TitanFabricItems.EMBER_HELMET);
        generator.registerArmor(TitanFabricItems.EMBER_CHESTPLATE);
        generator.registerArmor(TitanFabricItems.EMBER_LEGGINGS);
        generator.registerArmor(TitanFabricItems.EMBER_BOOTS);
    }
}
