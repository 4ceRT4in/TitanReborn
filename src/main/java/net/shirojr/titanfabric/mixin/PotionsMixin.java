package net.shirojr.titanfabric.mixin;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.entry.RegistryEntry;
import net.shirojr.titanfabric.access.StatusEffectInstanceAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/** Rebalances vanilla potion definitions once, before every potion item form reads them. */
@Mixin(Potions.class)
public abstract class PotionsMixin {
    @ModifyArg(
            method = "<clinit>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/potion/Potions;register(Ljava/lang/String;Lnet/minecraft/potion/Potion;)Lnet/minecraft/registry/entry/RegistryEntry;"),
            index = 1
    )
    private static Potion titanfabric$rebalancePotionDuration(Potion potion) {
        boolean turtleMaster = potion.getEffects().stream()
                .anyMatch(effect -> effect.getEffectType() == StatusEffects.SLOWNESS)
                && potion.getEffects().stream()
                .anyMatch(effect -> effect.getEffectType() == StatusEffects.RESISTANCE);
        if (turtleMaster) {
            StatusEffectInstance slowness = potion.getEffects().stream()
                    .filter(effect -> effect.getEffectType() == StatusEffects.SLOWNESS)
                    .findFirst().orElseThrow();
            boolean strong = slowness.getAmplifier() >= 5;
            boolean extended = !strong && slowness.getDuration() > 400;
            for (StatusEffectInstance effect : potion.getEffects()) {
                int duration;
                if (effect.getEffectType() == StatusEffects.SLOWNESS) {
                    duration = strong ? 300 : extended ? 600 : 400;
                } else if (effect.getEffectType() == StatusEffects.RESISTANCE) {
                    duration = strong ? 200 : extended ? 500 : 300;
                } else {
                    duration = effect.getDuration();
                }
                ((StatusEffectInstanceAccessor) effect).titanfabric$setDuration(duration);
            }
            return potion;
        }

        for (StatusEffectInstance effect : potion.getEffects()) {
            int duration = titanfabric$duration(effect.getEffectType(), effect.getDuration(), effect.getAmplifier());
            ((StatusEffectInstanceAccessor) effect).titanfabric$setDuration(duration);
        }
        return potion;
    }

    private static int titanfabric$duration(RegistryEntry<StatusEffect> effect, int vanillaDuration, int amplifier) {
        if (effect == StatusEffects.STRENGTH || effect == StatusEffects.SPEED || effect == StatusEffects.FIRE_RESISTANCE) {
            return amplifier > 0 ? 900 : vanillaDuration > 3600 ? 7200 : 3600;
        }
        if (effect == StatusEffects.WEAKNESS || effect == StatusEffects.SLOWNESS) {
            return amplifier > 0 ? 300 : vanillaDuration > 1800 ? 3600 : 1800;
        }
        if (effect == StatusEffects.POISON || effect == StatusEffects.REGENERATION) {
            return amplifier > 0 ? 300 : vanillaDuration > 900 ? 1200 : 600;
        }
        if (effect == StatusEffects.NIGHT_VISION || effect == StatusEffects.WATER_BREATHING || effect == StatusEffects.SLOW_FALLING
                || effect == StatusEffects.JUMP_BOOST || effect == StatusEffects.INVISIBILITY) {
            return vanillaDuration > 3600 ? 1800 : 900;
        }
        if (effect == StatusEffects.RESISTANCE && vanillaDuration <= 800) {
            return amplifier > 0 ? 200 : vanillaDuration > 400 ? 600 : 300;
        }
        return vanillaDuration;
    }
}
