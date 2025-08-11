package net.shirojr.titanfabric.util.effects;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.Optional;

public class OverpoweredEnchantmentsHelper {

    public static boolean isOverpowered(ItemStack stack) {
        if (stack.hasEnchantments()) {
            ItemEnchantmentsComponent enchantments = stack.getEnchantments();
            for (RegistryEntry<Enchantment> enchantmentEntry : enchantments.getEnchantments()) {
                Optional<RegistryKey<Enchantment>> keyOptional = enchantmentEntry.getKey();
                if (keyOptional.isEmpty()) continue;

                RegistryKey<Enchantment> key = keyOptional.get();
                int level = enchantments.getLevel(enchantmentEntry);

                if ((key == Enchantments.SHARPNESS && level >= 6) || (key == Enchantments.PROTECTION && level >= 5) || (key == Enchantments.POWER && level >= 6)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isOverpoweredEnchantmentBook(ItemStack stack) {
        if (stack.getItem() instanceof EnchantedBookItem) {
            ItemEnchantmentsComponent storedEnchantments = stack.get(DataComponentTypes.STORED_ENCHANTMENTS);
            if (storedEnchantments != null) {
                for (RegistryEntry<Enchantment> enchantmentEntry : storedEnchantments.getEnchantments()) {
                    Optional<RegistryKey<Enchantment>> keyOptional = enchantmentEntry.getKey();
                    if (keyOptional.isEmpty()) continue;

                    RegistryKey<Enchantment> key = keyOptional.get();
                    int level = storedEnchantments.getLevel(enchantmentEntry);

                    if ((key == Enchantments.SHARPNESS && level >= 6)
                            || (key == Enchantments.PROTECTION && level >= 5)
                            || (key == Enchantments.POWER && level >= 6)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
