package net.shirojr.titanfabric.util.effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import org.jetbrains.annotations.Nullable;

public enum WeaponEffects {
    /*NONE("weapon_none", null),*/
    BLIND("weapon_blind", StatusEffects.BLINDNESS),
    FIRE("weapon_fire", null),
    POISON("weapon_poison", StatusEffects.POISON),
    WEAK("weapon_weak", StatusEffects.WEAKNESS),
    WITHER("weapon_wither", StatusEffects.WITHER);

    private final String id;
    private final StatusEffect statusEffect;

    WeaponEffects(String id, StatusEffect statusEffect) {
        this.id = id;
        this.statusEffect = statusEffect;
    }

    /**
     * Get ID for the Weapon effect
     *
     * @return string representation of the Weapon Effect
     */
    public String getId() {
        return this.id;
    }

    public StatusEffect getStatusEffect() {
        return this.statusEffect;
    }

    /**
     * Get WeaponEffect from an ID
     *
     * @param id string representation of the Weapon Effect
     * @return Weapon Effect
     */
    @Nullable
    public static WeaponEffects getEffect(String id) {
        for (var entry : WeaponEffects.values()) {
            if (entry.id.equals(id)) return entry;
        }
        return null;
    }
}
