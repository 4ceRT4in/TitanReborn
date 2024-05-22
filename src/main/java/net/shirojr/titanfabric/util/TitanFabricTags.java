package net.shirojr.titanfabric.util;


import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shirojr.titanfabric.TitanFabric;

@SuppressWarnings("ALL")
public class TitanFabricTags {
    public static class Items {
        public static final TagKey<Item> ARROWS = createTag("selectable_arrows");
        public static final TagKey<Item> BETTER_SMELTING_ITEMS = createTag("improved_diamond_furnace_smelting");
        public static final TagKey<Item> HIGH_HEAT_SMELTING = createTag("needs_high_heat_smelting");
        public static final TagKey<Item> ORES = createCommonTag("diamond_furnace_ores");

        private static TagKey<Item> createTag(String name) {
            return TagKey.of(Registry.ITEM_KEY, new Identifier(TitanFabric.MODID, name));
        }

        private static TagKey<Item> createCommonTag(String name) {
            return TagKey.of(Registry.ITEM_KEY, new Identifier("c", name));
        }
    }
}
