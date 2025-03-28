package net.shirojr.titanfabric.util.effects;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public enum WeaponEffectType {
    INNATE_EFFECT("innate_effect_type"),
    ADDITIONAL_EFFECT("additional_effect_type");

    private final String nbtKey;

    private static final Map<String, WeaponEffectType> BY_KEY = new HashMap<>();

    static {
        for (WeaponEffectType type : values()) {
            BY_KEY.put(type.nbtKey, type);
        }
    }

    WeaponEffectType(String nbtKey) {
        this.nbtKey = nbtKey;
    }

    public static final Codec<WeaponEffectType> CODEC = Codec.STRING.xmap(WeaponEffectType::getType, WeaponEffectType::getNbtKey);
    public static final PacketCodec<RegistryByteBuf, WeaponEffectType> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, WeaponEffectType::getNbtKey,
            WeaponEffectType::getType
    );


    public String getNbtKey() {
        return nbtKey;
    }

    @Nullable
    public static WeaponEffectType getType(String nbtKey) {
        return BY_KEY.get(nbtKey);
    }
}
