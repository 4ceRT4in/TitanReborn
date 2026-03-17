package net.shirojr.titanfabric.util.items;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.BowItem;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;
import net.shirojr.titanfabric.init.TitanFabricGamerules;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class FireEnchantmentBanHelper {

    private FireEnchantmentBanHelper() {
        // utility class
    }

    public static boolean isFireEnchantmentBanEnabled(@Nullable World world) {
        return world != null && world.getGameRules().getBoolean(TitanFabricGamerules.DISABLE_FIRE_ENCHANTMENTS);
    }

    public static boolean isRestrictedCombatItem(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.getItem() instanceof SwordItem || stack.getItem() instanceof BowItem;
    }

    public static boolean isFireEnchantment(RegistryEntry<Enchantment> enchantment) {
        return enchantment.matchesKey(Enchantments.FLAME) || enchantment.matchesKey(Enchantments.FIRE_ASPECT);
    }

    public static boolean shouldRestrictGeneration(@Nullable World world, ItemStack stack) {
        return isFireEnchantmentBanEnabled(world) && isRestrictedCombatItem(stack);
    }

    public static List<EnchantmentLevelEntry> filterFireEnchantments(List<EnchantmentLevelEntry> entries) {
        return entries.stream()
                .filter(entry -> !isFireEnchantment(entry.enchantment))
                .toList();
    }

    public static boolean hasBlockedFireEnchantment(ItemStack stack) {
        ItemEnchantmentsComponent enchantments = stack.get(DataComponentTypes.ENCHANTMENTS);
        if (hasBlockedFireEnchantment(enchantments)) return true;
        ItemEnchantmentsComponent storedEnchantments = stack.get(DataComponentTypes.STORED_ENCHANTMENTS);
        return hasBlockedFireEnchantment(storedEnchantments);
    }

    public static ItemStack getSanitizedCombatStackForEffects(@Nullable World world, ItemStack original) {
        if (!isFireEnchantmentBanEnabled(world)) return original;
        if (!isRestrictedCombatItem(original)) return original;
        if (!hasBlockedFireEnchantment(original.get(DataComponentTypes.ENCHANTMENTS))) return original;

        ItemStack sanitized = original.copy();
        stripFireEnchantments(sanitized);
        return sanitized;
    }

    public static boolean stripFireEnchantments(ItemStack stack) {
        if (!shouldSanitizeStack(stack)) return false;

        boolean changed = false;
        changed |= stripEnchantmentsComponent(stack, DataComponentTypes.ENCHANTMENTS);
        changed |= stripEnchantmentsComponent(stack, DataComponentTypes.STORED_ENCHANTMENTS);
        return changed;
    }

    private static boolean shouldSanitizeStack(ItemStack stack) {
        return stack.getItem() instanceof EnchantedBookItem || isRestrictedCombatItem(stack);
    }

    private static boolean hasBlockedFireEnchantment(@Nullable ItemEnchantmentsComponent component) {
        if (component == null || component.isEmpty()) return false;
        for (RegistryEntry<Enchantment> enchantment : component.getEnchantments()) {
            if (isFireEnchantment(enchantment)) return true;
        }
        return false;
    }

    private static boolean stripEnchantmentsComponent(
            ItemStack stack,
            net.minecraft.component.ComponentType<ItemEnchantmentsComponent> componentType
    ) {
        ItemEnchantmentsComponent component = stack.get(componentType);
        if (component == null || component.isEmpty()) return false;

        ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(component);
        int beforeSize = component.getSize();
        builder.remove(FireEnchantmentBanHelper::isFireEnchantment);
        ItemEnchantmentsComponent updated = builder.build();
        if (updated.getSize() == beforeSize) return false;

        if (updated.isEmpty()) {
            stack.remove(componentType);
        } else {
            stack.set(componentType, updated);
        }
        return true;
    }
}
