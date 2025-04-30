package net.shirojr.titanfabric.mixin;

import net.fabricmc.fabric.api.item.v1.FabricItemStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import net.shirojr.titanfabric.color.TitanFabricDyeProviders;
import net.shirojr.titanfabric.effect.TitanFabricStatusEffects;
import net.shirojr.titanfabric.util.effects.ArmorPlatingHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements FabricItemStack {

    @Shadow @Nullable public abstract NbtCompound getNbt();

    @Inject(method = "damage(ILjava/util/Random;Lnet/minecraft/server/network/ServerPlayerEntity;)Z", at = @At("HEAD"), cancellable = true)
    private void titanfabric$ItemStackDamage(int amount, Random random, ServerPlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (player == null) return;
        if (player.hasStatusEffect(TitanFabricStatusEffects.INDESTRUCTIBILITY)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "hasGlint", at = @At("HEAD"), cancellable = true)
    private void titanfabric$hasGlint(CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = (ItemStack)(Object)this;
        if (ArmorPlatingHelper.hasArmorPlating(stack)) {
            cir.setReturnValue(true);
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

