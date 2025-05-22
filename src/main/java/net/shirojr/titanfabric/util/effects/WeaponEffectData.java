package net.shirojr.titanfabric.util.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.init.TitanFabricDataComponents;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public record WeaponEffectData(WeaponEffectType type, WeaponEffect weaponEffect, int strength) {

    public static final Codec<WeaponEffectData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            WeaponEffectType.CODEC.fieldOf("type").forGetter(WeaponEffectData::type),
            WeaponEffect.CODEC.fieldOf("effect").forGetter(WeaponEffectData::weaponEffect),
            Codec.INT.fieldOf("strength").forGetter(WeaponEffectData::strength)
    ).apply(instance, WeaponEffectData::new));

    public static final PacketCodec<RegistryByteBuf, WeaponEffectData> PACKET_CODEC = PacketCodec.tuple(
            WeaponEffectType.PACKET_CODEC, WeaponEffectData::type,
            WeaponEffect.PACKET_CODEC, WeaponEffectData::weaponEffect,
            PacketCodecs.INTEGER, WeaponEffectData::strength,
            WeaponEffectData::new
    );

    public static final String EFFECTS_COMPOUND_NBT_KEY = TitanFabric.MOD_ID + ".WeaponEffect";
    public static final String EFFECT_NBT_KEY = "effect";
    public static final String EFFECTS_STRENGTH_NBT_KEY = "EffectStrength";

    public static Optional<WeaponEffectData> get(ItemStack stack, WeaponEffectType type) {
        HashSet<WeaponEffectData> data = stack.contains(TitanFabricDataComponents.WEAPON_EFFECTS) ? stack.get(TitanFabricDataComponents.WEAPON_EFFECTS) : null;
        return get(data, type);
    }

    public static Optional<WeaponEffectData> get(HashSet<WeaponEffectData> list, WeaponEffectType type) {
        if (list == null) return Optional.empty();
        for (WeaponEffectData effectEntry : list) {
            if (!effectEntry.type().equals(type)) continue;
            return Optional.of(effectEntry);
        }
        return Optional.empty();
    }
}
