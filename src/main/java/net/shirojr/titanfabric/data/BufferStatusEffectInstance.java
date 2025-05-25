package net.shirojr.titanfabric.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;

public record BufferStatusEffectInstance(RegistryEntry<StatusEffect> effect, int duration, int amplifier) {
    public static final Codec<BufferStatusEffectInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            StatusEffect.ENTRY_CODEC.fieldOf("buffer_effect").forGetter(BufferStatusEffectInstance::effect),
            Codec.INT.fieldOf("buffer_duration").forGetter(BufferStatusEffectInstance::duration),
            Codec.INT.fieldOf("buffer_amplifier").forGetter(BufferStatusEffectInstance::amplifier)
    ).apply(instance, BufferStatusEffectInstance::new));
}
