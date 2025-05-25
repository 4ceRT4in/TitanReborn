package net.shirojr.titanfabric.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.entry.RegistryEntry;
import net.shirojr.titanfabric.access.StatusEffectInstanceAccessor;
import net.shirojr.titanfabric.data.BufferStatusEffectInstance;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StatusEffectInstance.class)
public class StatusEffectInstanceMixin implements StatusEffectInstanceAccessor {

    @Shadow
    private int duration;

    @Shadow
    @Final
    private RegistryEntry<StatusEffect> type;
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
        if (effect.equals(StatusEffects.REGENERATION) && entity.hasStatusEffect(StatusEffects.WITHER)) {
            StatusEffectInstance witherEffect = entity.getStatusEffect(StatusEffects.WITHER);
            if (witherEffect != null) {
                int i = (50 + ((witherEffect.getAmplifier() + 1) * 10)) >> amplifier;
                return i <= 0 || duration % i == 0;
            }
        }
        return this.type.value().canApplyUpdateEffect(duration, amplifier);
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
    private void writeNbtMixin(CallbackInfoReturnable<NbtElement> cir) {
        if (this.previousStatusEffectInstance == null) return;
        BufferStatusEffectInstance buffer = new BufferStatusEffectInstance(
                this.previousStatusEffectInstance.getEffectType(),
                this.previousStatusEffectInstance.getDuration(),
                this.previousStatusEffectInstance.getAmplifier()
        );
        BufferStatusEffectInstance.CODEC.encodeStart(NbtOps.INSTANCE, buffer).getPartialOrThrow();
    }

    @Inject(method = "fromNbt*", at = @At("RETURN"))
    private static void fromNbtMixin(NbtCompound nbt, CallbackInfoReturnable<StatusEffectInstance> info) {
        StatusEffectInstance result = info.getReturnValue();
        BufferStatusEffectInstance.CODEC.parse(NbtOps.INSTANCE, nbt).result().ifPresent(bufferInstance -> {
            if (!(result instanceof StatusEffectInstanceAccessor resultAccessor)) return;
            resultAccessor.titanfabric$setPreviousStatusEffect(
                    new StatusEffectInstance(bufferInstance.effect(), bufferInstance.duration(), bufferInstance.amplifier())
            );
        });
    }

    @Override
    public void titanfabric$setPreviousStatusEffect(@Nullable StatusEffectInstance statusEffectInstance) {
        this.previousStatusEffectInstance = statusEffectInstance != null ? new StatusEffectInstance(statusEffectInstance) : null;
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