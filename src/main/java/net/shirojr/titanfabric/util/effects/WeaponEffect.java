package net.shirojr.titanfabric.util.effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

import static net.shirojr.titanfabric.util.effects.WeaponEffectData.EFFECT_NBT_KEY;

public enum WeaponEffect {
    BLIND("weapon_blind", StatusEffects.NIGHT_VISION, StatusEffects.BLINDNESS),
    FIRE("weapon_fire", StatusEffects.FIRE_RESISTANCE, null),
    POISON("weapon_poison", StatusEffects.POISON),
    WEAK("weapon_weak", StatusEffects.WEAKNESS),
    WITHER("weapon_wither", StatusEffects.INSTANT_DAMAGE, StatusEffects.WITHER);

    private final String id;
    private final StatusEffect ingredientEffect;
    private final StatusEffect outputEffect;

    WeaponEffect(String id, StatusEffect ingredientEffect, StatusEffect outputEffect) {
        this.id = id;
        this.ingredientEffect = ingredientEffect;
        this.outputEffect = outputEffect;
    }

    WeaponEffect(String id, StatusEffect effect) {
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
    public static WeaponEffect getEffect(String id) {
        for (WeaponEffect entry : WeaponEffect.values()) {
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
    public static WeaponEffect getEffect(NbtCompound nbtCompound) {
        if (EffectHelper.stackHasNoWeaponEffectData(nbtCompound)) return null;
        String id = nbtCompound.getString(EFFECT_NBT_KEY);
        for (WeaponEffect entry : WeaponEffect.values()) {
            if (entry.id.equals(id)) return entry;
        }
        return null;
    }
}
