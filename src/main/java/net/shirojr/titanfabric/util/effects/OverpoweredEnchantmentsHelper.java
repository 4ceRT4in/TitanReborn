package net.shirojr.titanfabric.util.effects;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.Optional;

public class OverpoweredEnchantmentsHelper {

    public static int getNormalMaximum(RegistryEntry<Enchantment> enchantment) {
        if (enchantment.matchesKey(Enchantments.SHARPNESS)
                || enchantment.matchesKey(Enchantments.POWER)) {
            return 5;
        }
        if (enchantment.matchesKey(Enchantments.PROTECTION)) {
            return 4;
        }
        return Integer.MAX_VALUE;
    }

    /** Caps every non-anvil generation path to the ordinary vanilla maximum. */
    public static ItemStack capGeneratedLevels(ItemStack stack) {
        ItemEnchantmentsComponent enchantments = EnchantmentHelper.getEnchantments(stack);
        if (enchantments.isEmpty()) return stack;

        ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(enchantments);
        boolean changed = false;
        for (RegistryEntry<Enchantment> entry : enchantments.getEnchantments()) {
            int level = enchantments.getLevel(entry);
            int maximum = getNormalMaximum(entry);
            if (level <= maximum) continue;
            builder.set(entry, maximum);
            changed = true;
        }
        if (changed) EnchantmentHelper.set(stack, builder.build());
        return stack;
    }

    /**
     * A new overpowered level must be produced from two copies of the preceding level.
     * Existing overpowered items may still be renamed or repaired without losing the enchantment.
     */
    public static boolean isValidNetheriteAnvilCombination(ItemStack base, ItemStack sacrifice, ItemStack result) {
        ItemEnchantmentsComponent resultEnchantments = EnchantmentHelper.getEnchantments(result);
        ItemEnchantmentsComponent baseEnchantments = EnchantmentHelper.getEnchantments(base);
        ItemEnchantmentsComponent sacrificeEnchantments = EnchantmentHelper.getEnchantments(sacrifice);

        for (RegistryEntry<Enchantment> entry : resultEnchantments.getEnchantments()) {
            int normalMaximum = getNormalMaximum(entry);
            int resultLevel = resultEnchantments.getLevel(entry);
            if (normalMaximum == Integer.MAX_VALUE || resultLevel <= normalMaximum) continue;

            int baseLevel = baseEnchantments.getLevel(entry);
            if (baseLevel >= resultLevel) continue;
            if (resultLevel != normalMaximum + 1
                    || baseLevel != normalMaximum
                    || sacrificeEnchantments.getLevel(entry) != normalMaximum) {
                return false;
            }
        }
        return true;
    }

    public static boolean isOverpowered(ItemStack stack) {
        if (stack.hasEnchantments()) {
            ItemEnchantmentsComponent enchantments = stack.getEnchantments();
            for (RegistryEntry<Enchantment> enchantmentEntry : enchantments.getEnchantments()) {
                Optional<RegistryKey<Enchantment>> keyOptional = enchantmentEntry.getKey();
                if (keyOptional.isEmpty()) continue;

                RegistryKey<Enchantment> key = keyOptional.get();
                int level = enchantments.getLevel(enchantmentEntry);

                if ((key.equals(Enchantments.SHARPNESS) && level >= 6)
                        || (key.equals(Enchantments.PROTECTION) && level >= 5)
                        || (key.equals(Enchantments.POWER) && level >= 6)) {
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

                    if ((key.equals(Enchantments.SHARPNESS) && level >= 6)
                            || (key.equals(Enchantments.PROTECTION) && level >= 5)
                            || (key.equals(Enchantments.POWER) && level >= 6)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
