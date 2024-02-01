package net.shirojr.titanfabric.util.effects;

import org.jetbrains.annotations.Nullable;

public enum WeaponEffectType {
    INNATE_EFFECT("innate_effect_type"),
    ADDITIONAL_EFFECT("additional_effect_type");

    private final String nbtKey;

    WeaponEffectType(String nbtKey) {
        this.nbtKey = nbtKey;
    }

    public String getNbtKey() {
        return nbtKey;
    }

    @Nullable
    public static WeaponEffectType getType(String nbtKey) {
        for (WeaponEffectType type : WeaponEffectType.values()) {
            if (nbtKey.equals(type.getNbtKey())) return type;
        }
        return null;
    }
}
