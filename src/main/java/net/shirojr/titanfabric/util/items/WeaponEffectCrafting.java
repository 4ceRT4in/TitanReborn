package net.shirojr.titanfabric.util.items;

import net.shirojr.titanfabric.util.effects.WeaponEffect;
import net.shirojr.titanfabric.util.effects.WeaponEffectData;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface WeaponEffectCrafting {
    default List<WeaponEffect> supportedEffects() {
        return List.of(WeaponEffect.values());
    }

    @Nullable
    default WeaponEffectData getBaseEffect() {
        return null;
    }

}
