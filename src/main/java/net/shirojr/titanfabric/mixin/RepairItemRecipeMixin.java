package net.shirojr.titanfabric.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RepairItemRecipe;
import net.shirojr.titanfabric.init.TitanFabricItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** The Enchanted Diamond Apple is intentionally repaired only at the smithing table. */
@Mixin(RepairItemRecipe.class)
public abstract class RepairItemRecipeMixin {
    @Inject(method = "canCombineStacks", at = @At("HEAD"), cancellable = true)
    private static void titanfabric$excludeEnchantedDiamondApple(ItemStack first, ItemStack second,
                                                                 CallbackInfoReturnable<Boolean> cir) {
        if (first.isOf(TitanFabricItems.ENCHANTED_DIAMOND_APPLE)
                || second.isOf(TitanFabricItems.ENCHANTED_DIAMOND_APPLE)) {
            cir.setReturnValue(false);
        }
    }
}
