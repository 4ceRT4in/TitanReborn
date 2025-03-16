package net.shirojr.titanfabric.mixin;


import net.minecraft.enchantment.DamageEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(DamageEnchantment.class)
public abstract class DamageEnchantmentMixin {
    @ModifyConstant(
            method = "getMaxLevel()I",
            constant = @Constant(intValue = 5)
    )
    private int getMaxLevel(int original) {
        return 6;
    }
}