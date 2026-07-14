package net.shirojr.titanfabric.mixin;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.potion.Potions;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Rebalances vanilla potion definitions once, before every potion item form reads them. */
@Mixin(Potions.class)
public abstract class PotionsMixin {
    @Redirect(method = "<clinit>", at = @At(value = "NEW", target = "net/minecraft/entity/effect/StatusEffectInstance"))
    private static StatusEffectInstance titanfabric$rebalancePotionDuration(RegistryEntry<StatusEffect> effect, int duration, int amplifier) {
        return new StatusEffectInstance(effect, titanfabric$duration(effect, duration, amplifier), amplifier);
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
            return vanillaDuration > 1800 ? 1800 : 900;
        }
        if (effect == StatusEffects.RESISTANCE && vanillaDuration <= 800) {
            return amplifier > 0 ? 200 : vanillaDuration > 400 ? 600 : 300;
        }
        return vanillaDuration;
    }
}
