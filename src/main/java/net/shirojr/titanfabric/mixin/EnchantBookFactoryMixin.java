package net.shirojr.titanfabric.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.shirojr.titanfabric.config.TitanConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(TradeOffers.EnchantBookFactory.class)
public abstract class EnchantBookFactoryMixin implements TradeOffers.Factory {

    @Inject(method = "create", at = @At("RETURN"), cancellable = true)
    public void create(Entity entity, Random random, CallbackInfoReturnable<TradeOffer> cir) {
        TradeOffer offer = cir.getReturnValue();
        if (offer == null) return;
        ItemStack sell = offer.getSellItem();
        if (!sell.isOf(Items.ENCHANTED_BOOK)) return;

        var blocked = TitanConfig.getBlockedEnchantments();
        var enchantments = EnchantmentHelper.getEnchantments(sell);

        for (RegistryEntry<Enchantment> enchantmentEntry : enchantments.getEnchantments()) {
            Optional<RegistryKey<Enchantment>> keyOptional = enchantmentEntry.getKey();
            if (keyOptional.isEmpty()) continue;
            Identifier id = keyOptional.get().getValue();
            if (blocked.contains(id.toString())) {
                cir.setReturnValue(null);
                return;
            }
        }
    }
}