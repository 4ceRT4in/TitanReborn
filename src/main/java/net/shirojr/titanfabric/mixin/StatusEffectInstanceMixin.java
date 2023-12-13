package net.shirojr.titanfabric.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

@Mixin(StatusEffectInstance.class)
public class StatusEffectInstanceMixin {

    @Shadow
    @Mutable
    @Final
    private StatusEffect type;

    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffect;canApplyUpdateEffect(II)Z"))
    private boolean updateMixin(StatusEffect effect, int duration, int amplifier, LivingEntity entity, Runnable overwriteCallback) {
        if (type.equals(StatusEffects.REGENERATION) && entity.hasStatusEffect(StatusEffects.WITHER)) {
            int i = (50 + ((entity.getStatusEffect(StatusEffects.WITHER).getAmplifier() + 1) * 10)) >> amplifier;
            if (i > 0) {
                return duration % i == 0;
            }
            return true;
        }
        return this.type.canApplyUpdateEffect(duration, amplifier);
    }

}
