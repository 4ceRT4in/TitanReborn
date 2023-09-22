package net.shirojr.titanfabric.mixin;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(method = "setFireTicks", at = @At("HEAD"))
    private void reducedTicks(int fireTicks, CallbackInfo ci) {
        //TODO: test for nether amror
    }
}
