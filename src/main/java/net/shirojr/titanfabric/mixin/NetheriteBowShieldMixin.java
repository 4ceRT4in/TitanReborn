package net.shirojr.titanfabric.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.shirojr.titanfabric.item.custom.bow.NetheriteBowItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class NetheriteBowShieldMixin {
    @ModifyArg(method = "damage", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/LivingEntity;damageShield(F)V"), index = 0)
    private float titanfabric$damageShieldForBlockedHalf(float amount, @Local(argsOnly = true) DamageSource source) {
        return NetheriteBowItem.isNetheriteBowArrow(source) ? amount * 0.5F : amount;
    }

    // The first STORE to the amount argument is vanilla's `amount = 0` after a successful
    // shield block. The third float local holds the damage blocked by the shield.
    // Keep that block (direction checks, animation and statistics), but let half of the
    // actual incoming damage proceed through the original, single applyDamage call.
    @ModifyVariable(method = "damage", at = @At(value = "STORE", ordinal = 0), ordinal = 0, argsOnly = true)
    private float titanfabric$passHalfThroughShield(float amount, @Local(argsOnly = true) DamageSource source,
                                                   @Local(ordinal = 2) LocalFloatRef blockedDamage) {
        if (!NetheriteBowItem.isNetheriteBowArrow(source)) return amount;
        float half = blockedDamage.get() * 0.5F;
        blockedDamage.set(half);
        return half;
    }
}
