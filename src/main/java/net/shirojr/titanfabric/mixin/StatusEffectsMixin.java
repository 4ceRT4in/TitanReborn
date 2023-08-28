package net.shirojr.titanfabric.mixin;

import net.minecraft.entity.effect.StatusEffects;
import net.shirojr.titanfabric.init.ConfigInit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(StatusEffects.class)
public abstract class StatusEffectsMixin {
    /*@Redirect(
            method = "<clinit>",
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=weakness")),
            at = @At(value = "NEW", target = "Lnet/minecraft/entity/effect/DamageModifierStatusEffect;<init>(Lnet/minecraft/entity/effect/StatusEffectCategory;ID)V",
                    ordinal = 0)
    )
    private static DamageModifierStatusEffect titanfabric$weaknessBalancing(StatusEffectCategory category, int color, double modifier) {
        return new BalancedDamageModifierStatusEffect(category, color);
    }*/

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/effect/DamageModifierStatusEffect;<init>(Lnet/minecraft/entity/effect/StatusEffectCategory;ID)V"),
            index = 2)
    private static double titanfabric$weaknessBalancingArgsMod(double modifier) {
        return ConfigInit.CONFIG.weaknessStatusEffectModifier;
    }
}