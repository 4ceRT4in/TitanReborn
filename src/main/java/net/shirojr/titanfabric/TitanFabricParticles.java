package net.shirojr.titanfabric;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.shirojr.titanfabric.particles.GasParticleEffect;

public class TitanFabricParticles {
    public static final ParticleType<GasParticleEffect> GAS_PARTICLE = Registry.register(
            Registries.PARTICLE_TYPE, "gas_particle", FabricParticleTypes.complex(GasParticleEffect.CODEC, GasParticleEffect.PACKET_CODEC)
    );

    private static <T extends ParticleType<?>> T register(String name, T entry) {
        return Registry.register(Registries.PARTICLE_TYPE, TitanFabric.getId(name), entry);
    }


    public static void initialize() {
        // static initialisation
    }
}
