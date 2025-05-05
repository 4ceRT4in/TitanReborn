package net.shirojr.titanfabric.util.items;

import net.shirojr.titanfabric.util.effects.WeaponEffect;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface WeaponEffectCrafting {
    default List<WeaponEffect> supportedEffects() {
        return List.of(WeaponEffect.values());
    }

    @Nullable
    default WeaponEffect getBaseEffect() {
        return null;
    }

}
