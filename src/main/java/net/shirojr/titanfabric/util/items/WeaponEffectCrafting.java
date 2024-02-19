package net.shirojr.titanfabric.util.items;

import net.minecraft.item.ItemStack;
import net.shirojr.titanfabric.util.effects.WeaponEffect;

public interface WeaponEffectCrafting {
    ItemType isType();

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
