package net.shirojr.titanfabric.data;


import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.shirojr.titanfabric.TitanFabric;

import java.util.List;

public class TitanFabricModelProvider extends FabricModelProvider {
    public TitanFabricModelProvider(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        List<Block> blocks = Registry.BLOCK.streamEntries()
                .filter(blockReference -> blockReference.registryKey().getValue().getNamespace().equals(TitanFabric.MODID))
                .map(RegistryEntry.Reference::value).toList();
        for (Block entry : blocks) {
            blockStateModelGenerator.registerSimpleCubeAll(entry);
        }
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        List<Item> items = Registry.ITEM.streamEntries()
                .filter(itemReference -> itemReference.registryKey().getValue().getNamespace().equals(TitanFabric.MODID))
                .map(RegistryEntry.Reference::value).toList();
        for (Item entry : items) {
            itemModelGenerator.register(entry, Models.GENERATED);
        }
    }
}
