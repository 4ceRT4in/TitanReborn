package net.shirojr.titanfabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.shirojr.titanfabric.init.TitanFabricBlocks;
import net.shirojr.titanfabric.init.TitanFabricDamageTypes;
import net.shirojr.titanfabric.init.TitanFabricItems;
import net.shirojr.titanfabric.init.TitanFabricTags;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
                            TitanFabricBlocks.DEEPSLATE_LEGEND_ORE.asItem(), TitanFabricItems.EMBER_SHARD,
                            TitanFabricItems.LEGEND_POWDER)
                    .setReplace(false);
            getOrCreateTagBuilder(TitanFabricTags.Items.HIGH_HEAT_SMELTING)
                    .add(TitanFabricBlocks.CITRIN_ORE.asItem(), TitanFabricBlocks.EMBER_ORE.asItem(), TitanFabricBlocks.DEEPSLATE_LEGEND_ORE.asItem())
                    .add(TitanFabricItems.EMBER_SHARD, TitanFabricItems.LEGEND_POWDER)
                    .setReplace(false);
            getOrCreateTagBuilder(TitanFabricTags.Items.DEFAULT_CROSSBOW_ARROWS)
                    .add(Items.ARROW, Items.SPECTRAL_ARROW, Items.FIREWORK_ROCKET)
                    .setReplace(false);
            getOrCreateTagBuilder(TitanFabricTags.Items.DEFAULT_ARROWS)
                    .add(Items.ARROW, Items.SPECTRAL_ARROW)
                    .setReplace(false);
            getOrCreateTagBuilder(TitanFabricTags.Items.ARMOR_PLATING)
                    .add(TitanFabricItems.LEGEND_ARMOR_PLATING, TitanFabricItems.EMBER_ARMOR_PLATING, TitanFabricItems.CITRIN_ARMOR_PLATING,
                            TitanFabricItems.DIAMOND_ARMOR_PLATING, TitanFabricItems.NETHERITE_ARMOR_PLATING)
                    .setReplace(false);
            getOrCreateTagBuilder(TitanFabricTags.Items.PLATEABLE_ARMOR)
                    .add(TitanFabricItems.CITRIN_HELMET, TitanFabricItems.CITRIN_CHESTPLATE, TitanFabricItems.CITRIN_LEGGINGS,
                            TitanFabricItems.CITRIN_BOOTS, TitanFabricItems.EMBER_HELMET, TitanFabricItems.EMBER_CHESTPLATE,
                            TitanFabricItems.EMBER_LEGGINGS, TitanFabricItems.EMBER_BOOTS, TitanFabricItems.LEGEND_HELMET,
                            TitanFabricItems.LEGEND_CHESTPLATE, TitanFabricItems.LEGEND_LEGGINGS, TitanFabricItems.LEGEND_BOOTS,
                            Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS,
                            Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS)
                    .setReplace(false);
            getOrCreateTagBuilder(ItemTags.TRIMMABLE_ARMOR)
                    .add(TitanFabricItems.CITRIN_HELMET, TitanFabricItems.CITRIN_CHESTPLATE, TitanFabricItems.CITRIN_LEGGINGS,
                            TitanFabricItems.CITRIN_BOOTS, TitanFabricItems.EMBER_HELMET, TitanFabricItems.EMBER_CHESTPLATE,
                            TitanFabricItems.EMBER_LEGGINGS, TitanFabricItems.EMBER_BOOTS, TitanFabricItems.LEGEND_HELMET,
                            TitanFabricItems.LEGEND_CHESTPLATE, TitanFabricItems.LEGEND_LEGGINGS, TitanFabricItems.LEGEND_BOOTS)
                    .setReplace(false);
            getOrCreateTagBuilder(TitanFabricTags.Items.DYES)
                    .add(Items.RED_DYE, Items.LIME_DYE, Items.BLACK_DYE, Items.BROWN_DYE, Items.BLUE_DYE, Items.CYAN_DYE, Items.GRAY_DYE,
                            Items.GREEN_DYE, Items.LIGHT_BLUE_DYE, Items.LIGHT_GRAY_DYE, Items.MAGENTA_DYE, Items.ORANGE_DYE, Items.PINK_DYE,
                            Items.PURPLE_DYE, Items.WHITE_DYE, Items.YELLOW_DYE)
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
                    .add(TitanFabricBlocks.DEEPSLATE_LEGEND_ORE, TitanFabricBlocks.LEGEND_CRYSTAL, TitanFabricBlocks.LEGEND_BLOCK)
                    .setReplace(false);
            getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                    .add(TitanFabricBlocks.CITRIN_ORE, TitanFabricBlocks.CITRIN_BLOCK)
                    .add(TitanFabricBlocks.EMBER_ORE, TitanFabricBlocks.EMBER_BLOCK)
                    .add(TitanFabricBlocks.DEEPSLATE_LEGEND_ORE, TitanFabricBlocks.LEGEND_CRYSTAL, TitanFabricBlocks.LEGEND_BLOCK)
                    .add(TitanFabricBlocks.DIAMOND_FURNACE, TitanFabricBlocks.NETHERITE_ANVIL)
                    .setReplace(false);

            // no campfires, furnaces, ... They should be handled over their LIT BlockState Property
            getOrCreateTagBuilder(TitanFabricTags.Blocks.HOT_BLOCKS)
                    .add(Blocks.LAVA, Blocks.LAVA_CAULDRON, Blocks.MAGMA_BLOCK, Blocks.FIRE, Blocks.SOUL_FIRE);
        }
    }

    public static class DamageTypeTagsProvider extends FabricTagProvider<DamageType> {
        public DamageTypeTagsProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, RegistryKeys.DAMAGE_TYPE, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
            Map<TagKey<DamageType>, HashSet<TitanFabricDamageTypes.DamageTypePair>> invertedMap = new HashMap<>();
            for (var entry : TitanFabricDamageTypes.ALL_DAMAGE_TYPES.entrySet()) {
                for (TagKey<DamageType> tag : entry.getValue().tags()) {
                    invertedMap.computeIfAbsent(tag, damageTypeTagKey -> new HashSet<>()).add(entry.getValue());
                }
            }
            for (var entry : invertedMap.entrySet()) {
                FabricTagProvider<DamageType>.FabricTagBuilder builder = getOrCreateTagBuilder(entry.getKey()).setReplace(false);
                for (TitanFabricDamageTypes.DamageTypePair damageTypePair : entry.getValue()) {
                    builder.addOptional(damageTypePair.get());
                }
            }
        }
    }


    public static void addProviders(FabricDataGenerator.Pack pack) {
        pack.addProvider(ItemTagsProvider::new);
        pack.addProvider(BlockTagsProvider::new);
        pack.addProvider(DamageTypeTagsProvider::new);
    }
}
