package net.shirojr.titanfabric.mixin;


import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.enchantment.ProtectionEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ProtectionEnchantment.class)
public abstract class ProtectionEnchantmentMixin {
    @ModifyConstant(
            method = "getMaxLevel()I",
            constant = @Constant(intValue = 4)
    )
    private int getMaxLevel(int original) {
        return 5;
    }
}