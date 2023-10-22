package net.shirojr.titanfabric.util.items;

import net.shirojr.titanfabric.util.effects.WeaponEffects;

public interface EssenceCrafting {
    ItemType isType();

    /**
     * Only override if this item is an {@link ItemType#INGREDIENT INGREDIENT}
     *
     * @return {@link WeaponEffects WeaponEffect} of the {@link ItemType#INGREDIENT INGREDIENT}
     */
    default WeaponEffects ingredientEffect() {
        return null;
    }

    enum ItemType {
        PRODUCT, INGREDIENT
    }
}
