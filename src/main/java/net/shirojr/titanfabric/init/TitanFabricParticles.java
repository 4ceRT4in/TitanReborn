package net.shirojr.titanfabric.init;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.particles.GasParticleEffect;

public interface TitanFabricParticles {
    ParticleType<GasParticleEffect> GAS_PARTICLE = register(
            "gas",
            FabricParticleTypes.complex(GasParticleEffect.CODEC, GasParticleEffect.PACKET_CODEC)
    );

    @SuppressWarnings("SameParameterValue")
    private static <T extends ParticleType<?>> T register(String name, T entry) {
        return Registry.register(Registries.PARTICLE_TYPE, TitanFabric.getId(name), entry);
    }


    static void initialize() {
        // static initialisation
    }
}
