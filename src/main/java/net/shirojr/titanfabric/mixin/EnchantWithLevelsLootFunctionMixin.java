package net.shirojr.titanfabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.EnchantWithLevelsLootFunction;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.math.random.Random;
import net.shirojr.titanfabric.util.items.FireEnchantmentBanHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Mixin(EnchantWithLevelsLootFunction.class)
public abstract class EnchantWithLevelsLootFunctionMixin extends ConditionalLootFunction {

    protected EnchantWithLevelsLootFunctionMixin(List<LootCondition> conditions) {
        super(conditions);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @WrapOperation(
            method = "process",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/enchantment/EnchantmentHelper;enchant(Lnet/minecraft/util/math/random/Random;Lnet/minecraft/item/ItemStack;ILnet/minecraft/registry/DynamicRegistryManager;Ljava/util/Optional;)Lnet/minecraft/item/ItemStack;"
            )
    )
    private ItemStack titanfabric$removeFireEnchantmentsFromLoot(
            Random random,
            ItemStack stack,
            int level,
            DynamicRegistryManager registryManager,
            Optional<RegistryEntryList<Enchantment>> options,
            Operation<ItemStack> original,
            @Local(argsOnly = true) LootContext lootContext
    ) {
        if (!FireEnchantmentBanHelper.shouldRestrictLootEnchantmentGeneration(lootContext.getWorld(), stack)) {
            return original.call(random, stack, level, registryManager, options);
        }

        Stream<net.minecraft.registry.entry.RegistryEntry<Enchantment>> availableEnchantments = options
                .map(RegistryEntryList::stream)
                .orElseGet(() -> registryManager.get(RegistryKeys.ENCHANTMENT).streamEntries().map(entry -> entry));
        ItemStack result = EnchantmentHelper.enchant(
                random,
                stack,
                level,
                availableEnchantments.filter(entry -> !FireEnchantmentBanHelper.isFireEnchantment(entry))
        );
        return FireEnchantmentBanHelper.getSanitizedLootStack(lootContext.getWorld(), result);
    }
}
