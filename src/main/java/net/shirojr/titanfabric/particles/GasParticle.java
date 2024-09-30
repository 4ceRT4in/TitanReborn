package net.shirojr.titanfabric.particles;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;

public class GasParticle extends SpriteBillboardParticle {
    protected GasParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, float scale) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.scale = scale;
        this.maxAge = 100 + this.random.nextInt(50);
        this.collidesWithWorld = false;
        this.setAlpha(0.9F); // Start fully opaque, will fade over time
    }

    @Override
    public ParticleTextureSheet getType() {
        return GasTextureSheet.Gas;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.age++ >= this.maxAge) {
            this.markDead();
        } else {
            // Increase upward motion over time
            this.velocityY += 0.001D;
            // Slight horizontal drift
            this.velocityX += (this.random.nextDouble() - 0.5) * 0.001D;
            this.velocityZ += (this.random.nextDouble() - 0.5) * 0.001D;

            // Limit velocities
            this.velocityY = Math.min(this.velocityY, 0.02D);
            this.velocityX = MathHelper.clamp(this.velocityX, -0.02D, 0.02D);
            this.velocityZ = MathHelper.clamp(this.velocityZ, -0.02D, 0.02D);

            // Gradually fade out
            this.alpha *= 0.98F;
        }
    }
}

