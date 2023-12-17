package net.shirojr.titanfabric.util.effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.stat.Stat;
import org.jetbrains.annotations.Nullable;

public enum WeaponEffects {
    BLIND("weapon_blind", StatusEffects.BLINDNESS),
    FIRE("weapon_fire", StatusEffects.FIRE_RESISTANCE, null),
    POISON("weapon_poison", StatusEffects.POISON),
    WEAK("weapon_weak", StatusEffects.WEAKNESS),
    WITHER("weapon_wither", StatusEffects.INSTANT_DAMAGE, StatusEffects.WITHER);

    private final String id;
    private final StatusEffect ingredientEffect;
    private final StatusEffect outputEffect;

    WeaponEffects(String id, StatusEffect ingredientEffect, StatusEffect outputEffect) {
        this.id = id;
        this.ingredientEffect = ingredientEffect;
        this.outputEffect = outputEffect;
    }

    WeaponEffects(String id, StatusEffect effect) {
        this(id, effect, effect);
    }

    /**
     * Get ID for the Weapon effect
     *
     * @return string representation of the Weapon Effect
     */
    public String getId() {
        return this.id;
    }

    @Nullable
    public StatusEffect getIngredientEffect() {
        return this.ingredientEffect;
    }

    @Nullable
    public StatusEffect getOutputEffect() {
        return this.outputEffect;
    }

    /**
     * Get WeaponEffect from an ID
     *
     * @param id string representation of the Weapon Effect
     * @return Weapon Effect
     */
    @Nullable
    public static WeaponEffects getEffect(String id) {
        for (WeaponEffects entry : WeaponEffects.values()) {
            if (entry.id.equals(id)) return entry;
        }
        return null;
    }

    /**
     * Get WeaponEffect from an ItemStack
     *
     * @param nbtCompound nbt containing the Weapon Effect
     * @return Weapon Effect
     */
    @Nullable
    public static WeaponEffects getEffect(NbtCompound nbtCompound) {
        if (!EffectHelper.stackHasWeaponEffect(nbtCompound)) return null;
        String id = nbtCompound.getString(EffectHelper.EFFECTS_NBT_KEY);
        for (WeaponEffects entry : WeaponEffects.values()) {
            if (entry.id.equals(id)) return entry;
        }
        return null;
    }
}
