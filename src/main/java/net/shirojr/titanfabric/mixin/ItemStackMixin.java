package net.shirojr.titanfabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.fabric.api.item.v1.FabricItemStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import net.shirojr.titanfabric.effect.ImmunityEffect;
import net.shirojr.titanfabric.init.TitanFabricStatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements FabricItemStack {
    @ModifyExpressionValue(method = "damage(ILnet/minecraft/server/world/ServerWorld;Lnet/minecraft/server/network/ServerPlayerEntity;Ljava/util/function/Consumer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isDamageable()Z"))
    private boolean titanfabric$avoidDamage(boolean original, @Local(argsOnly = true) ServerPlayerEntity player) {
        if (player == null) return original;
        if (player.hasStatusEffect(TitanFabricStatusEffects.INDESTRUCTIBILITY)) {
            return false;
        }
        return original;
    }

    @Inject(at = @At("HEAD"), method = "finishUsing")
    private void titanfabric$finishUsing(World world, LivingEntity user, CallbackInfoReturnable<ItemStack> info) {
        if (((ItemStack) (Object) this).getItem() == Items.GOLDEN_APPLE) {
            if (user.hasStatusEffect(StatusEffects.ABSORPTION)) {
                StatusEffectInstance absorptionEffect = user.getStatusEffect(StatusEffects.ABSORPTION);
                if (absorptionEffect != null) {
                    int remainingDuration = absorptionEffect.getDuration();
                    user.removeStatusEffect(StatusEffects.ABSORPTION);
                    user.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, remainingDuration, absorptionEffect.getAmplifier()));
                }
            }
        }
        if(((ItemStack) (Object) this).getItem() == Items.MILK_BUCKET) {
            ImmunityEffect.resetImmunity(user);
        }
    }
}

