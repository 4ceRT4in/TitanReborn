package net.shirojr.titanfabric.init;


import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.shirojr.titanfabric.TitanFabric;


public class TitanFabricTags {
    private static <T> TagKey<T> createTag(String name, RegistryKey<? extends Registry<T>> registryKey) {
        return TagKey.of(registryKey, TitanFabric.getId(name));
    }

    public static class Items {
        public static final TagKey<Item> DEFAULT_ARROWS = createTag("default_selectable_arrows", RegistryKeys.ITEM);
        public static final TagKey<Item> DEFAULT_CROSSBOW_ARROWS = createTag("default_selectable_crossbow_arrows", RegistryKeys.ITEM);
        public static final TagKey<Item> BETTER_SMELTING_ITEMS = createTag("improved_diamond_furnace_smelting_items", RegistryKeys.ITEM);
        public static final TagKey<Item> HIGH_HEAT_SMELTING = createTag("high_heat_smelting_only_items", RegistryKeys.ITEM);
        public static final TagKey<Item> PLATEABLE_ARMOR = createTag("plateable_armor", RegistryKeys.ITEM);
        public static final TagKey<Item> DYES = createTag("dye", RegistryKeys.ITEM);
        public static final TagKey<Item> ARMOR_PLATING = createTag("armor_plating", RegistryKeys.ITEM);
    }

    public static class Blocks {
        public static final TagKey<Block> HIGH_HEAT_FURNACES = createTag("high_heat_furnaces", RegistryKeys.BLOCK);
        public static final TagKey<Block> HOT_BLOCKS = createTag("hot", RegistryKeys.BLOCK);
    }
}