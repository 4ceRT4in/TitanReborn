package net.shirojr.titanfabric.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.SmithingTransformRecipe;
import net.minecraft.recipe.input.SmithingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.shirojr.titanfabric.init.TitanFabricItems;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Netherite spears deliberately lose both innate and added weapon-effect data. */
@Mixin(SmithingTransformRecipe.class)
public class SmithingTransformRecipeMixin {
    @Inject(method = "craft", at = @At("RETURN"))
    private void titanfabric$clearNetheriteSpearEffects(
            SmithingRecipeInput input,
            RegistryWrapper.WrapperLookup lookup,
            CallbackInfoReturnable<ItemStack> cir
    ) {
        ItemStack result = cir.getReturnValue();
        if (result.isOf(TitanFabricItems.NETHERITE_SPEAR)) {
            EffectHelper.removeEffectsFromStack(result);
        }
    }
}
