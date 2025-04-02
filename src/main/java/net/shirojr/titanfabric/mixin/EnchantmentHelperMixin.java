package net.shirojr.titanfabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Enchantments.class)
public abstract class EnchantmentHelperMixin {

    @ModifyExpressionValue(method = "bootstrap",
            slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/enchantment/Enchantments;SHARPNESS:Lnet/minecraft/registry/RegistryKey;")),
            at = @At(value = "CONSTANT", args = "floatValue=0.5", ordinal = 0)
    )
    private static float modifySharpnessDamage(float original) {
        /*int level = EnchantmentHelper.getLevel(Enchantments.SHARPNESS, stack);
        if (level > 0) {
            return (level * 1.0F);
        }*/
        //FIXME: might be removable, check datapack enchants
        return original;
    }
}
