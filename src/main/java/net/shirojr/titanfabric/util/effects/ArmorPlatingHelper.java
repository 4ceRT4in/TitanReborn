package net.shirojr.titanfabric.util.effects;

import net.minecraft.item.ItemStack;
import net.shirojr.titanfabric.init.TitanFabricDataComponents;

public class ArmorPlatingHelper {

    public static boolean hasArmorPlating(ItemStack stack) {
        return stack.contains(TitanFabricDataComponents.ARMOR_PLATING);
    }

    public static void removeAllArmorPlates(ItemStack stack) {
        stack.remove(TitanFabricDataComponents.ARMOR_PLATING);
    }

    public static void applyArmorPlate(ItemStack stack, ArmorPlateType armorPlateType) {
        if (hasArmorPlating(stack)) return;
        stack.set(TitanFabricDataComponents.ARMOR_PLATING, armorPlateType);
    }

    public static ArmorPlateType getArmorPlatingType(ItemStack stack) {
        return stack.get(TitanFabricDataComponents.ARMOR_PLATING);
    }
}