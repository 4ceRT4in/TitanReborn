package net.shirojr.titanfabric.mixin;


import net.minecraft.enchantment.PowerEnchantment;
import net.minecraft.enchantment.ProtectionEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(PowerEnchantment.class)
public abstract class PowerEnchantmentMixin {
    @ModifyConstant(
            method = "getMaxLevel()I",
            constant = @Constant(intValue = 5)
    )
    private int getMaxLevel(int original) {
        return 6;
    }
}