package net.shirojr.titanfabric.mixin;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.shirojr.titanfabric.particles.GasTextureSheet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin {

    @Final
    @Mutable
    @Shadow
    private static List<ParticleTextureSheet> PARTICLE_TEXTURE_SHEETS;
    /**
     * Static initializer to add the custom GAS ParticleTextureSheet to the ParticleManager's sheets list.
     */
    static {
        List<ParticleTextureSheet> newSheets = new ArrayList<>(PARTICLE_TEXTURE_SHEETS);
        newSheets.add(GasTextureSheet.Gas);
        PARTICLE_TEXTURE_SHEETS = newSheets;
    }
}