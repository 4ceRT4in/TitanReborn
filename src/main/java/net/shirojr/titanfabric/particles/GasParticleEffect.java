package net.shirojr.titanfabric.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.shirojr.titanfabric.init.TitanFabricParticles;

public record GasParticleEffect(float red, float green, float blue, float scale,
                                float alpha) implements ParticleEffect {

    public static final MapCodec<GasParticleEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            Codec.FLOAT.fieldOf("red").forGetter(particleEffect -> particleEffect.red),
                            Codec.FLOAT.fieldOf("green").forGetter(particleEffect -> particleEffect.green),
                            Codec.FLOAT.fieldOf("blue").forGetter(particleEffect -> particleEffect.blue),
                            Codec.FLOAT.fieldOf("scale").forGetter(particleEffect -> particleEffect.scale),
                            Codec.FLOAT.fieldOf("alpha").forGetter(particleEffect -> particleEffect.alpha))
                    .apply(instance, GasParticleEffect::new)
    );

    public static final PacketCodec<RegistryByteBuf, GasParticleEffect> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.FLOAT, GasParticleEffect::red,
            PacketCodecs.FLOAT, GasParticleEffect::green,
            PacketCodecs.FLOAT, GasParticleEffect::blue,
            PacketCodecs.FLOAT, GasParticleEffect::scale,
            PacketCodecs.FLOAT, GasParticleEffect::alpha,
            GasParticleEffect::new
    );

    @Override
    public ParticleType<?> getType() {
        return TitanFabricParticles.GAS_PARTICLE;
    }
}

