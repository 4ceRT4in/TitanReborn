package net.shirojr.titanfabric.mixin;

import net.fabricmc.fabric.api.item.v1.FabricItemStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import net.shirojr.titanfabric.init.TitanFabricStatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements FabricItemStack {
    @Inject(method = "damage(ILjava/util/Random;Lnet/minecraft/server/network/ServerPlayerEntity;)Z", at = @At("HEAD"), cancellable = true)
    private void titanfabric$ItemStackDamage(int amount, Random random, ServerPlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (player == null) return;
        if (player.hasStatusEffect(TitanFabricStatusEffects.INDESTRUCTIBILITY)) {
            cir.setReturnValue(false);
        }
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
    }
}

