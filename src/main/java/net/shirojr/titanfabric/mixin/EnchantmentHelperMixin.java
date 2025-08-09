package net.shirojr.titanfabric.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.config.TitanConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

    @Inject(method = "getPossibleEntries", at = @At("RETURN"), cancellable = true)
    private static void getPossibleEntries(int level, ItemStack stack, Stream<RegistryEntry<Enchantment>> possibleEnchantments, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir) {
        var originalEntries = cir.getReturnValue();
        var blockedEnchants = TitanConfig.getBlockedEnchantments();
        /*
        LoggerUtil.devLogger("origin enchantment entries: " + originalEntries.size());
        LoggerUtil.devLogger("blocked enchantment entries: " + blockedEnchants);
         */
        var filteredEntries = originalEntries.stream()
                .filter(entry -> {
                    Identifier enchantId = entry.enchantment.getKey().get().getValue();
                    String enchantIdString = enchantId.toString();
                    boolean isBlocked = blockedEnchants.contains(enchantIdString);

                    /*
                    if (isBlocked) {
                        LoggerUtil.devLogger("blocked enchantment from showing up: " + enchantIdString);
                    }
                     */

                    return !isBlocked;
                })
                .collect(Collectors.toCollection(java.util.ArrayList::new));
        cir.setReturnValue(filteredEntries);
    }
}