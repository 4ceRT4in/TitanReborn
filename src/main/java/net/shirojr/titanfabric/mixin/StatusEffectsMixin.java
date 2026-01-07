package net.shirojr.titanfabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(StatusEffects.class)
public abstract class StatusEffectsMixin {

    @ModifyConstant(method = "<clinit>", constant = @Constant(doubleValue = -4.0))
    private static double titanfabric$weaknessBalancingArgsMod(double original) {
        return -1.5;
    }

    @ModifyConstant(method = "<clinit>", constant = @Constant(doubleValue = 3.0))
    private static double titanfabric$strengthBalancingArgsMod(double original) { return 1.0; }

    @WrapOperation(
            method = "<clinit>",
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=blindness")),
            at = @At(
                    value = "NEW",
                    target = "(Lnet/minecraft/entity/effect/StatusEffectCategory;I)Lnet/minecraft/entity/effect/StatusEffect;",
                    ordinal = 0
            )
    )
    private static StatusEffect adjustBlindnessAttributes(StatusEffectCategory category, int color, Operation<StatusEffect> original) {
        StatusEffect originalEffect = original.call(category, color);
        return originalEffect.addAttributeModifier(
                EntityAttributes.GENERIC_MOVEMENT_SPEED, Identifier.ofVanilla("effect.slowness"), -0.10F, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
    }
}