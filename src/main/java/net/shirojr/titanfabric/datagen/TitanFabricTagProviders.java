package net.shirojr.titanfabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.shirojr.titanfabric.init.TitanFabricBlocks;
import net.shirojr.titanfabric.init.TitanFabricItems;
import net.shirojr.titanfabric.util.TitanFabricTags;

import java.util.concurrent.CompletableFuture;

public class TitanFabricTagProviders {
    public static class ItemTagsProvider extends FabricTagProvider<Item> {
        public ItemTagsProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, RegistryKeys.ITEM, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
            getOrCreateTagBuilder(TitanFabricTags.Items.BETTER_SMELTING_ITEMS)
                    .addOptionalTag(ItemTags.COAL_ORES)
                    .addOptionalTag(ItemTags.COPPER_ORES)
                    .addOptionalTag(ItemTags.IRON_ORES)
                    .addOptionalTag(ItemTags.GOLD_ORES)
                    .addOptionalTag(ItemTags.REDSTONE_ORES)
                    .addOptionalTag(ItemTags.LAPIS_ORES)
                    .addOptionalTag(ItemTags.EMERALD_ORES)
                    .addOptionalTag(ItemTags.DIAMOND_ORES)
                    .addOptionalTag(ConventionalItemTags.RAW_MATERIALS)
                    .addOptionalTag(ConventionalItemTags.ORES_IN_GROUND_NETHERRACK)
                    .add(Items.ANCIENT_DEBRIS)
                    .add(TitanFabricBlocks.CITRIN_ORE.asItem(), TitanFabricBlocks.EMBER_ORE.asItem(),
                            TitanFabricBlocks.DEEPSTALE_LEGEND_ORE.asItem(), TitanFabricItems.EMBER_SHARD,
                            TitanFabricItems.LEGEND_POWDER)
                    .setReplace(false);
            getOrCreateTagBuilder(TitanFabricTags.Items.HIGH_HEAT_SMELTING)
                    .add(TitanFabricBlocks.CITRIN_ORE.asItem(), TitanFabricBlocks.EMBER_ORE.asItem(), TitanFabricBlocks.DEEPSTALE_LEGEND_ORE.asItem())
                    .add(TitanFabricItems.EMBER_SHARD, TitanFabricItems.LEGEND_POWDER)
                    .setReplace(false);
            getOrCreateTagBuilder(TitanFabricTags.Items.DEFAULT_CROSSBOW_ARROWS)
                    .add(Items.ARROW, Items.SPECTRAL_ARROW, Items.FIREWORK_ROCKET)
                    .setReplace(false);
            getOrCreateTagBuilder(TitanFabricTags.Items.DEFAULT_ARROWS)
                    .add(Items.ARROW, Items.SPECTRAL_ARROW)
                    .setReplace(false);
        }
    }

    public static class BlockTagsProvider extends FabricTagProvider<Block> {
        public BlockTagsProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, RegistryKeys.BLOCK, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
            getOrCreateTagBuilder(TitanFabricTags.Blocks.HIGH_HEAT_FURNACES)
                    .add(Blocks.BLAST_FURNACE, TitanFabricBlocks.DIAMOND_FURNACE)
                    .setReplace(false);
            getOrCreateTagBuilder(BlockTags.ANVIL)
                    .add(TitanFabricBlocks.NETHERITE_ANVIL)
                    .setReplace(false);
            getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL)
                    .add(TitanFabricBlocks.CITRIN_ORE, TitanFabricBlocks.CITRIN_BLOCK)
                    .add(TitanFabricBlocks.EMBER_ORE, TitanFabricBlocks.EMBER_BLOCK)
                    .add(TitanFabricBlocks.DEEPSTALE_LEGEND_ORE, TitanFabricBlocks.LEGEND_CRYSTAL, TitanFabricBlocks.LEGEND_BLOCK)
                    .setReplace(false);
            getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                    .add(TitanFabricBlocks.CITRIN_ORE, TitanFabricBlocks.CITRIN_BLOCK)
                    .add(TitanFabricBlocks.EMBER_ORE, TitanFabricBlocks.EMBER_BLOCK)
                    .add(TitanFabricBlocks.DEEPSTALE_LEGEND_ORE, TitanFabricBlocks.LEGEND_CRYSTAL, TitanFabricBlocks.LEGEND_BLOCK)
                    .add(TitanFabricBlocks.DIAMOND_FURNACE, TitanFabricBlocks.NETHERITE_ANVIL)
                    .setReplace(false);
        }
    }


    public static void addProviders(FabricDataGenerator.Pack pack) {
        pack.addProvider(ItemTagsProvider::new);
        pack.addProvider(BlockTagsProvider::new);
    }
}
