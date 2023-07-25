package net.shirojr.titanfabric.data;


import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.item.TitanFabricItems;

import java.util.ArrayList;
import java.util.List;

public class TitanFabricModelProvider extends FabricModelProvider {
    public TitanFabricModelProvider(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(TitanFabricItems.ARROW, Models.GENERATED);
        List<Item> items = Registry.ITEM.stream().filter(item -> item.getTranslationKey().contains(TitanFabric.MODID)).toList();
        for (Item entry : items) {
            itemModelGenerator.register(entry, Models.GENERATED);
        }
    }
}
