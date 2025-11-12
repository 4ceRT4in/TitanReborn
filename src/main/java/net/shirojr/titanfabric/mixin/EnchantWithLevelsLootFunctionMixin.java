package net.shirojr.titanfabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.EnchantWithLevelsLootFunction;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.math.random.Random;
import net.shirojr.titanfabric.config.TitanConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Optional;

@Mixin(EnchantWithLevelsLootFunction.class)
public abstract class EnchantWithLevelsLootFunctionMixin extends ConditionalLootFunction {

    protected EnchantWithLevelsLootFunctionMixin(List<LootCondition> conditions) {
        super(conditions);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @WrapOperation(method="process", at=@At(value="INVOKE", target="Lnet/minecraft/enchantment/EnchantmentHelper;enchant(Lnet/minecraft/util/math/random/Random;Lnet/minecraft/item/ItemStack;ILnet/minecraft/registry/DynamicRegistryManager;Ljava/util/Optional;)Lnet/minecraft/item/ItemStack;"))
    private ItemStack titan$removeEnchantsFromConfig(Random r, ItemStack s, int lvl, DynamicRegistryManager drm, Optional<RegistryEntryList<Enchantment>> opts, Operation<ItemStack> op) {
        List<String> blocked = TitanConfig.getBlockedEnchantments();
        Optional<RegistryEntryList.Direct<Enchantment>> filtered = opts.flatMap(list -> {
            List<RegistryEntry<Enchantment>> kept = list.stream().filter(e -> e.getKey().isPresent() && !blocked.contains(e.getKey().get().getValue().toString())).toList();
            return kept.isEmpty() ? Optional.empty() : Optional.of(RegistryEntryList.of(kept));
        });
        return op.call(r, s, lvl, drm, filtered);
    }
}