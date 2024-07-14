package net.shirojr.titanfabric.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.nbt.NbtCompound;
import net.shirojr.titanfabric.access.StatusEffectInstanceAccessor;

@Mixin(StatusEffectInstance.class)
public class StatusEffectInstanceMixin implements StatusEffectInstanceAccessor {

    @Shadow
    @Mutable
    @Final
    private StatusEffect type;
    @Shadow
    private int duration;
    @Unique
    private StatusEffectInstance previousStatusEffectInstance = null;
    @Unique
    private boolean appliedPreviousStatusEffectInstance = false;

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

    @Inject(method = "update", at = @At("RETURN"))
    private void updateMixin(LivingEntity entity, Runnable overwriteCallback, CallbackInfoReturnable<Boolean> info) {
        if (this.previousStatusEffectInstance != null && !info.getReturnValue() && !this.appliedPreviousStatusEffectInstance && this.previousStatusEffectInstance.getDuration() > 200) {
            ((StatusEffectInstanceAccessor) this.previousStatusEffectInstance).titanfabric$setDuration(this.previousStatusEffectInstance.getDuration() - 200);
            entity.setStatusEffect(this.previousStatusEffectInstance, null);
            this.appliedPreviousStatusEffectInstance = true;
        }
    }

    @Inject(method = "writeNbt", at = @At("HEAD"))
    private void writeNbtMixin(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> info) {
        if (this.previousStatusEffectInstance != null) {
            nbt.putInt("PreviousStatusEffect", StatusEffect.getRawId(this.previousStatusEffectInstance.getEffectType()));
            nbt.putInt("PreviousStatusEffectDuration", this.previousStatusEffectInstance.getDuration());
            nbt.putInt("PreviousStatusEffectAmplifier", this.previousStatusEffectInstance.getAmplifier());
        }
    }

    @Inject(method = "fromNbt*", at = @At("RETURN"))
    private static void fromNbtMixin(NbtCompound nbt, CallbackInfoReturnable<StatusEffectInstance> info) {
        if (info.getReturnValue() != null && nbt.contains("PreviousStatusEffect")) {
            ((StatusEffectInstanceAccessor) info.getReturnValue()).titanfabric$setPreviousStatusEffect(
                    new StatusEffectInstance(StatusEffect.byRawId(nbt.getInt("PreviousStatusEffect")), nbt.getInt("PreviousStatusEffectDuration"), nbt.getInt("PreviousStatusEffectAmplifier")));
        }
    }

    @Override
    public void titanfabric$setPreviousStatusEffect(@Nullable StatusEffectInstance statusEffectInstance) {
        this.previousStatusEffectInstance = statusEffectInstance;
    }

    @Override
    public @Nullable StatusEffectInstance titanfabric$getPreviousStatusEffect() {
        return this.previousStatusEffectInstance;
    }

    @Override
    public void titanfabric$setDuration(int duration) {
        this.duration = duration;
    }

}
