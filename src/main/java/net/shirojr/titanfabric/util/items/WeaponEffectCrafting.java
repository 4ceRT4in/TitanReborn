package net.shirojr.titanfabric.util.items;

import net.minecraft.item.ItemStack;
import net.shirojr.titanfabric.util.effects.WeaponEffect;

import java.util.Optional;

public interface WeaponEffectCrafting {
    Optional<ItemType> titanfabric$getType();
    default boolean isType(ItemType type) {
        return this.titanfabric$getType().isPresent() && this.titanfabric$getType().get().equals(type);
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
