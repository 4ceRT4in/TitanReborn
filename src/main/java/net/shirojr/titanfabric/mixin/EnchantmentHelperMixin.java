package net.shirojr.titanfabric.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityGroup;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

    @Inject(method = "getAttackDamage", at = @At("HEAD"), cancellable = true)
    private static void modifySharpnessDamage(ItemStack stack, EntityGroup group, CallbackInfoReturnable<Float> cir) {
        int level = EnchantmentHelper.getLevel(Enchantments.SHARPNESS, stack);
        if (level > 0) {
            float damage = (level * 1.25F); // 1.8 sharpness level
            cir.setReturnValue(damage);
        }
    }
}
