package net.shirojr.titanfabric.util.effects;

import net.minecraft.nbt.NbtCompound;
import net.shirojr.titanfabric.TitanFabric;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record WeaponEffectData(WeaponEffectType type, WeaponEffect weaponEffect, int strength) {
    public static final String EFFECTS_COMPOUND_NBT_KEY = TitanFabric.MODID + ".WeaponEffect";
    public static final String EFFECT_NBT_KEY = "effect";
    public static final String EFFECTS_STRENGTH_NBT_KEY = "EffectStrength";

    public NbtCompound toNbt() {
        NbtCompound effectCompound = new NbtCompound();
        effectCompound.putString(EFFECT_NBT_KEY, weaponEffect().getId());
        effectCompound.putInt(EFFECTS_STRENGTH_NBT_KEY, strength);

        NbtCompound typeCompound = new NbtCompound();
        typeCompound.put(type().getNbtKey(), effectCompound);
        return typeCompound;
    }

    public static Optional<WeaponEffectData> fromNbt(NbtCompound compound, WeaponEffectType type) {
        WeaponEffect effect = WeaponEffect.getEffect(compound.getString(EFFECT_NBT_KEY));
        int strength = compound.getInt(EFFECTS_STRENGTH_NBT_KEY);
        if (effect == null) return Optional.empty();
        return Optional.of(new WeaponEffectData(type, effect, strength));
    }

    public static List<WeaponEffectData> allDataFromNbt(NbtCompound compound) {
        List<WeaponEffectData> dataList = new ArrayList<>();
        for (String typeNbtKey : compound.getKeys()) {
            WeaponEffectType type = WeaponEffectType.getType(typeNbtKey);
            if (type == null) continue;
            WeaponEffect effect = WeaponEffect.getEffect(compound.getCompound(type.getNbtKey()).getString(EFFECT_NBT_KEY));
            int strength = compound.getCompound(type.getNbtKey()).getInt(EFFECTS_STRENGTH_NBT_KEY);
            dataList.add(new WeaponEffectData(type, effect, strength));
        }
        return dataList;
    }

}
