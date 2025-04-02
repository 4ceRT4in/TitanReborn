package net.shirojr.titanfabric.util.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.ItemStack;
import net.shirojr.titanfabric.item.custom.armor.CitrinArmorItem;
import net.shirojr.titanfabric.item.custom.armor.EmberArmorItem;

public class ArmorHelper {
    public static int getEmberArmorCount(LivingEntity entity) {
        int i = 0;
        for (ItemStack armorStack : entity.getArmorItems()) {
            if (armorStack.getItem() instanceof EmberArmorItem) i++;
        }
        return i;
    }

    public static int getCitrinArmorCount(LivingEntity entity) {
        int i = 0;
        for (ItemStack armorStack : entity.getArmorItems()) {
            if (armorStack.getItem() instanceof CitrinArmorItem) i++;
        }
        return i;
    }

    public static int getNetheriteArmorCount(LivingEntity entity) {
        int i = 0;
        for (ItemStack armorStack : entity.getArmorItems()) {
            if (armorStack.getItem() instanceof ArmorItem armorItem && armorItem.getMaterial().value().equals(ArmorMaterials.NETHERITE.value())) {
                i++;
            }
        }
        return i;
    }
}
