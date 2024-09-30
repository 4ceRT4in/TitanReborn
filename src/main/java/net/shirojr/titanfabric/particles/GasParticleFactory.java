package net.shirojr.titanfabric.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class GasParticleFactory implements ParticleFactory<GasParticleEffect> {
    private final SpriteProvider spriteProvider;

    public GasParticleFactory(SpriteProvider spriteProvider) {
        this.spriteProvider = spriteProvider;
    }

    @Override
    public Particle createParticle(GasParticleEffect effect, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        GasParticle particle = new GasParticle(world, x, y, z, velocityX, velocityY, velocityZ, effect.getScale());
        particle.setSprite(this.spriteProvider);
        particle.setColor(effect.getRed(), effect.getGreen(), effect.getBlue());
        return particle;
    }
}
