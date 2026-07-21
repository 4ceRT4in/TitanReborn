package net.shirojr.titanfabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import net.shirojr.titanfabric.util.items.FireEnchantmentBanHelper;
import net.shirojr.titanfabric.util.effects.OverpoweredEnchantmentsHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.stream.Stream;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

    @Inject(
            method = "enchant(Lnet/minecraft/util/math/random/Random;Lnet/minecraft/item/ItemStack;ILjava/util/stream/Stream;)Lnet/minecraft/item/ItemStack;",
            at = @At("RETURN")
    )
    private static void titanfabric$capGeneratedEnchantments(
            Random random, ItemStack stack, int level, Stream<RegistryEntry<Enchantment>> possibleEnchantments,
            CallbackInfoReturnable<ItemStack> cir
    ) {
        OverpoweredEnchantmentsHelper.capGeneratedLevels(cir.getReturnValue());
    }

    @WrapOperation(
            method = "onTargetDamaged(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/damage/DamageSource;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/enchantment/EnchantmentHelper;onTargetDamaged(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/damage/DamageSource;Lnet/minecraft/item/ItemStack;)V"
            )
    )
    private static void titanfabric$removeFireEnchantmentDamageEffects(
            ServerWorld world,
            Entity target,
            DamageSource source,
            ItemStack stack,
            Operation<Void> original
    ) {
        ItemStack sanitized = FireEnchantmentBanHelper.getSanitizedCombatStackForEffects(world, stack);
        original.call(world, target, source, sanitized);
    }
}
