package net.shirojr.titanfabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.EnchantRandomlyLootFunction;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.random.Random;
import net.shirojr.titanfabric.util.items.FireEnchantmentBanHelper;
import net.shirojr.titanfabric.util.effects.OverpoweredEnchantmentsHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(EnchantRandomlyLootFunction.class)
public abstract class EnchantRandomlyLootFunctionMixin extends ConditionalLootFunction {

    protected EnchantRandomlyLootFunctionMixin(List<LootCondition> conditions) {
        super(conditions);
    }

    @Inject(method = "process", at = @At("RETURN"))
    private void titanfabric$capGeneratedLevels(
            ItemStack stack, LootContext context, CallbackInfoReturnable<ItemStack> cir
    ) {
        OverpoweredEnchantmentsHelper.capGeneratedLevels(cir.getReturnValue());
    }

    @WrapOperation(
            method = "process",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/Util;getRandomOrEmpty(Ljava/util/List;Lnet/minecraft/util/math/random/Random;)Ljava/util/Optional;"
            )
    )
    private Optional<RegistryEntry<Enchantment>> titanfabric$removeFireEnchantmentsFromRandomLoot(
            List<RegistryEntry<Enchantment>> enchantments,
            Random random,
            Operation<Optional<RegistryEntry<Enchantment>>> original,
            @Local(argsOnly = true) ItemStack stack,
            @Local(argsOnly = true) LootContext lootContext
    ) {
        if (!FireEnchantmentBanHelper.shouldRestrictLootEnchantmentGeneration(lootContext.getWorld(), stack)) {
            return original.call(enchantments, random);
        }

        List<RegistryEntry<Enchantment>> filteredEnchantments = enchantments.stream()
                .filter(entry -> !FireEnchantmentBanHelper.isFireEnchantment(entry))
                .toList();
        return original.call(filteredEnchantments, random);
    }
}
