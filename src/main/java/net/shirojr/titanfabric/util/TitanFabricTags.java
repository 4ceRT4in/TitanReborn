package net.shirojr.titanfabric.util;


import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.shirojr.titanfabric.TitanFabric;

@SuppressWarnings("ALL")
public class TitanFabricTags {
    private static <T> TagKey<T> createTag(String name, RegistryKey<? extends Registry<T>> registryKey) {
        return TagKey.of(registryKey, Identifier.of(TitanFabric.MODID, name));
    }

    private static <T> TagKey<T> createCommonTag(String name, RegistryKey<? extends Registry<T>> registryKey) {
        return TagKey.of(registryKey, Identifier.of("c", name));
    }

    public static class Items {
        public static final TagKey<Item> DEFAULT_ARROWS = createTag("default_selectable_arrows", Registry.ITEM_KEY);
        public static final TagKey<Item> DEFAULT_CROSSBOW_ARROWS = createTag("default_selectable_crossbow_arrows", Registry.ITEM_KEY);
        public static final TagKey<Item> BETTER_SMELTING_ITEMS = createTag("improved_diamond_furnace_smelting_items", Registry.ITEM_KEY);
        public static final TagKey<Item> HIGH_HEAT_SMELTING = createTag("high_heat_smelting_only_items", Registry.ITEM_KEY);
    }

    public static class Blocks {
        public static final TagKey<Block> HIGH_HEAT_FURNACES = createTag("high_heat_furnaces", Registry.BLOCK_KEY);
    }
}