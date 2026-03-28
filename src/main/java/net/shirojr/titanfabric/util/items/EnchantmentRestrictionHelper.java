package net.shirojr.titanfabric.util.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.shirojr.titanfabric.init.TitanFabricItems;

import java.util.Set;

public final class EnchantmentRestrictionHelper {
    private static final Set<Item> UNBREAKING_ENCHANTING_TABLE_BANNED_ITEMS = Set.of(
            TitanFabricItems.LEGEND_SWORD,
            TitanFabricItems.LEGEND_GREATSWORD,
            TitanFabricItems.LEGEND_HELMET,
            TitanFabricItems.LEGEND_CHESTPLATE,
            TitanFabricItems.LEGEND_LEGGINGS,
            TitanFabricItems.LEGEND_BOOTS,
            TitanFabricItems.LEGEND_BOW,
            TitanFabricItems.TITAN_CROSSBOW
    );

    private EnchantmentRestrictionHelper() {
        // utility class
    }

    public static boolean shouldBanUnbreakingFromEnchantingTable(ItemStack stack) {
        return UNBREAKING_ENCHANTING_TABLE_BANNED_ITEMS.contains(stack.getItem());
    }
}
