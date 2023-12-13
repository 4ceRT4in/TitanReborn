package net.shirojr.titanfabric.mixin;

import net.minecraft.entity.effect.StatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(StatusEffects.class)
public abstract class StatusEffectsMixin {

    @ModifyConstant(method = "<clinit>", constant = @Constant(doubleValue = -4.0))
    private static double titanfabric$weaknessBalancingArgsMod(double original) {
        return -2.0;
    }

    @ModifyConstant(method = "<clinit>", constant = @Constant(doubleValue = 3.0))
    private static double titanfabric$strengthBalancingArgsMod(double original) {
        return 1.5;
    }
}