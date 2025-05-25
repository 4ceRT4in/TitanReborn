package net.shirojr.titanfabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.shirojr.titanfabric.init.TitanFabricBlocks;
import net.shirojr.titanfabric.init.TitanFabricItems;

import java.util.concurrent.CompletableFuture;

public class TitanFabricBlockLootTableProvider extends FabricBlockLootTableProvider {
    public TitanFabricBlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        addDrop(TitanFabricBlocks.CITRIN_BLOCK);
        addDrop(TitanFabricBlocks.CITRIN_ORE, addDropWithSilkAndFortune(TitanFabricBlocks.CITRIN_ORE, 4f, 5f));
        addDrop(TitanFabricBlocks.DEEPSTALE_LEGEND_ORE, addDropWithSilkAndFortune(TitanFabricBlocks.DEEPSTALE_LEGEND_ORE, TitanFabricBlocks.LEGEND_CRYSTAL.asItem(), 4f, 5f));
        addDrop(TitanFabricBlocks.DIAMOND_FURNACE);
        addDrop(TitanFabricBlocks.EMBER_BLOCK);
        addDrop(TitanFabricBlocks.EMBER_ORE, addDropWithSilkAndFortune(TitanFabricBlocks.EMBER_ORE, TitanFabricItems.EMBER_SHARD, 4f, 5f));
        addDrop(TitanFabricBlocks.LEGEND_BLOCK);
        addDrop(TitanFabricBlocks.LEGEND_CRYSTAL, addDropWithSilkAndFortune(TitanFabricBlocks.LEGEND_CRYSTAL, 4f, 5f));
        addDrop(TitanFabricBlocks.NETHERITE_ANVIL);
    }

    public LootTable.Builder addDropWithSilkAndFortune(Block drop, Item item, float min, float max) {
        RegistryWrapper.Impl<Enchantment> impl = this.registryLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
        return this.dropsWithSilkTouch(drop, this.applyExplosionDecay(drop, ItemEntry.builder(item)
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(min, max)))
                        .apply(ApplyBonusLootFunction.uniformBonusCount(impl.getOrThrow(Enchantments.FORTUNE)))
                )
        );

    }

    public LootTable.Builder addDropWithSilkAndFortune(Block drop, float min, float max) {
        return addDropWithSilkAndFortune(drop, drop.asItem(), min, max);
    }
}
