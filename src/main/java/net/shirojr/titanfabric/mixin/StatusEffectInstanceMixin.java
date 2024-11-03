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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.nbt.NbtCompound;
import net.shirojr.titanfabric.access.StatusEffectInstanceAccessor;

@Mixin(StatusEffectInstance.class)
public class StatusEffectInstanceMixin implements StatusEffectInstanceAccessor {
    @Shadow @Mutable @Final
    private StatusEffect type;

    @Shadow
    private int duration;

    @Unique
    private StatusEffectInstance previousStatusEffectInstance = null;

    @Unique
    private boolean appliedPreviousStatusEffectInstance = false;

    @Inject(method = "<init>(Lnet/minecraft/entity/effect/StatusEffectInstance;)V", at = @At("RETURN"))
    private void copyConstructorMixin(StatusEffectInstance instance, CallbackInfo ci) {
        if (instance != null) {
            StatusEffectInstanceAccessor accessor = (StatusEffectInstanceAccessor) instance;
            StatusEffectInstance previousEffect = accessor.titanfabric$getPreviousStatusEffect();
            if (previousEffect != null) {
                this.previousStatusEffectInstance = new StatusEffectInstance(previousEffect);
               // this.appliedPreviousStatusEffectInstance = ((StatusEffectInstanceAccessor) instance).titanfabric$getPreviousStatusEffect();
            }
        }
    }

    // Add method to preserve state during serialization
    @Inject(method = "copyFrom", at = @At("RETURN"))
    private void copyFromMixin(StatusEffectInstance that, CallbackInfo ci) {
        if (that != null) {
            StatusEffectInstanceAccessor accessor = (StatusEffectInstanceAccessor) that;
            StatusEffectInstance previousEffect = accessor.titanfabric$getPreviousStatusEffect();
            if (previousEffect != null) {
                this.previousStatusEffectInstance = new StatusEffectInstance(previousEffect);
                //this.appliedPreviousStatusEffectInstance = ((StatusEffectInstanceAccessor) that).titanfabric$getAppliedPreviousStatusEffect();
            }
        }
    }

    @Redirect(method = "update", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/effect/StatusEffect;canApplyUpdateEffect(II)Z"))
    private boolean updateMixin(StatusEffect effect, int duration, int amplifier,
                                LivingEntity entity, Runnable overwriteCallback) {
        // Special case for Regeneration + Wither interaction
        if (type.equals(StatusEffects.REGENERATION) && entity.hasStatusEffect(StatusEffects.WITHER)) {
            StatusEffectInstance witherEffect = entity.getStatusEffect(StatusEffects.WITHER);
            if (witherEffect != null) {
                int i = (50 + ((witherEffect.getAmplifier() + 1) * 10)) >> amplifier;
                return i > 0 ? duration % i == 0 : true;
            }
        }
        return this.type.canApplyUpdateEffect(duration, amplifier);
    }

    @Inject(method = "update", at = @At("RETURN"))
    private void updateMixin(LivingEntity entity, Runnable overwriteCallback,
                             CallbackInfoReturnable<Boolean> info) {
        if (this.previousStatusEffectInstance != null &&
                !info.getReturnValue() &&
                !this.appliedPreviousStatusEffectInstance &&
                this.previousStatusEffectInstance.getDuration() > 200) {

            // Create a copy of the previous effect to avoid modification issues
            StatusEffectInstance updatedPrevEffect = new StatusEffectInstance(
                    this.previousStatusEffectInstance.getEffectType(),
                    this.previousStatusEffectInstance.getDuration() - 200,
                    this.previousStatusEffectInstance.getAmplifier()
            );

            entity.setStatusEffect(updatedPrevEffect, null);
            this.appliedPreviousStatusEffectInstance = true;
        }
    }

    @Inject(method = "writeNbt", at = @At("HEAD"))
    private void writeNbtMixin(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> info) {
        if (this.previousStatusEffectInstance != null) {
            NbtCompound previousEffect = new NbtCompound();
            previousEffect.putInt("Id", StatusEffect.getRawId(this.previousStatusEffectInstance.getEffectType()));
            previousEffect.putInt("Duration", this.previousStatusEffectInstance.getDuration());
            previousEffect.putInt("Amplifier", this.previousStatusEffectInstance.getAmplifier());
            nbt.put("PreviousStatusEffect", previousEffect);
        }
    }

    @Inject(method = "fromNbt*", at = @At("RETURN"))
    private static void fromNbtMixin(NbtCompound nbt, CallbackInfoReturnable<StatusEffectInstance> info) {
        StatusEffectInstance result = info.getReturnValue();
        if (result != null && nbt.contains("PreviousStatusEffect", 10)) {  // 10 is the ID for compound tags
            NbtCompound previousEffect = nbt.getCompound("PreviousStatusEffect");
            ((StatusEffectInstanceAccessor) result).titanfabric$setPreviousStatusEffect(
                    new StatusEffectInstance(
                            StatusEffect.byRawId(previousEffect.getInt("Id")),
                            previousEffect.getInt("Duration"),
                            previousEffect.getInt("Amplifier")
                    )
            );
        }
    }

    @Override
    public void titanfabric$setPreviousStatusEffect(@Nullable StatusEffectInstance statusEffectInstance) {
        this.previousStatusEffectInstance = statusEffectInstance != null ?
                new StatusEffectInstance(statusEffectInstance) : null;
        this.appliedPreviousStatusEffectInstance = false;
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