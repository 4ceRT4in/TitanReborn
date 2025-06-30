package net.shirojr.titanfabric.util.effects;

import com.mojang.serialization.Codec;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.potion.Potions;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public enum WeaponEffect {
    BLIND("blind", StatusEffects.NIGHT_VISION, StatusEffects.BLINDNESS, 0x23307C),
    FIRE("fire", StatusEffects.FIRE_RESISTANCE, null, -1),
    POISON("poison", StatusEffects.POISON, new PotionContentsComponent(Potions.POISON).getColor()),
    WEAK("weak", StatusEffects.WEAKNESS, new PotionContentsComponent(Potions.WEAKNESS).getColor()),
    WITHER("wither", StatusEffects.INSTANT_DAMAGE, StatusEffects.WITHER, 0x0D0D0D);

    private final String id;
    private final RegistryEntry<StatusEffect> ingredientEffect;
    private final RegistryEntry<StatusEffect> outputEffect;
    private final int color;

    private static final Map<String, WeaponEffect> BY_ID = new HashMap<>();

    static {
        for (WeaponEffect effect : values()) {
            BY_ID.put(effect.id, effect);
        }
    }

    WeaponEffect(String id, RegistryEntry<StatusEffect> ingredientEffect, RegistryEntry<StatusEffect> outputEffect, int color) {
        this.id = id;
        this.ingredientEffect = ingredientEffect;
        this.outputEffect = outputEffect;
        this.color = color;
    }

    WeaponEffect(String id, RegistryEntry<StatusEffect> effect, int color) {
        this(id, effect, effect, color);
    }

    public static final Codec<WeaponEffect> CODEC = Codec.STRING.xmap(WeaponEffect::byId, WeaponEffect::getId);
    public static final PacketCodec<RegistryByteBuf, WeaponEffect> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, WeaponEffect::getId,
            WeaponEffect::byId
    );

    /**
     * Get ID for the Weapon effect
     *
     * @return string representation of the Weapon Effect
     */
    public String getId() {
        return this.id;
    }

    public static WeaponEffect byId(String id) {
        return BY_ID.get(id);
    }

    @Nullable
    public RegistryEntry<StatusEffect> getIngredientEffect() {
        return this.ingredientEffect;
    }

    @Nullable
    public RegistryEntry<StatusEffect> getOutputEffect() {
        return this.outputEffect;
    }

    public Integer getColor() {
        return this.color;
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
}
