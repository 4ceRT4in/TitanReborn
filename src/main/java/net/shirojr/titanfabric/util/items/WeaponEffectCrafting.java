package net.shirojr.titanfabric.util.items;

import net.minecraft.item.ItemStack;
import net.shirojr.titanfabric.util.effects.WeaponEffect;

import java.util.List;
import java.util.Optional;

public interface WeaponEffectCrafting {
    Optional<ItemType> titanfabric$getCraftingType();

    default List<WeaponEffect> supportedEffects() {
        return List.of(WeaponEffect.values());
    }

    default boolean isNotCraftingType(ItemType type) {
        return this.titanfabric$getCraftingType().isEmpty() || !this.titanfabric$getCraftingType().get().equals(type);
    }

    /**
     * Only override if this item is an {@link ItemType#INGREDIENT INGREDIENT}
     *
     * @return {@link WeaponEffect WeaponEffect} of the {@link ItemType#INGREDIENT INGREDIENT}
     */
    default WeaponEffect ingredientEffect(ItemStack stack) {
        return null;
    }

    enum ItemType {
        PRODUCT, INGREDIENT
    }
}
