package net.shirojr.titanfabric.util;


import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.shirojr.titanfabric.TitanFabric;

@SuppressWarnings("ALL")
public class TitanFabricTags {
    private static <T> TagKey<T> createTag(String name, RegistryKey<? extends Registry<T>> registryKey) {
        return TagKey.of(registryKey, new Identifier(TitanFabric.MODID, name));
    }

    private static <T> TagKey<T> createCommonTag(String name, RegistryKey<? extends Registry<T>> registryKey) {
        return TagKey.of(registryKey, new Identifier("c", name));
    }

    public static class Items {
        public static final TagKey<Item> DEFAULT_ARROWS = createTag("default_selectable_arrows", Registry.ITEM_KEY);
        public static final TagKey<Item> DEFAULT_CROSSBOW_ARROWS = createTag("default_selectable_crossbow_arrows", Registry.ITEM_KEY);
        public static final TagKey<Item> BETTER_SMELTING_ITEMS = createTag("improved_diamond_furnace_smelting_items", Registry.ITEM_KEY);
        public static final TagKey<Item> HIGH_HEAT_SMELTING = createTag("high_heat_smelting_only_items", Registry.ITEM_KEY);
        public static final TagKey<Item> PLATABLE_ARMOR = createTag("platable_armor", Registry.ITEM_KEY);
        public static final TagKey<Item> DYE = createTag("dye", Registry.ITEM_KEY);
        public static final TagKey<Item> ARMOR_PLATING = createTag("armor_plating", Registry.ITEM_KEY);
    }

    public static class Blocks {
        public static final TagKey<Block> HIGH_HEAT_FURNACES = createTag("high_heat_furnaces", Registry.BLOCK_KEY);
    }
}