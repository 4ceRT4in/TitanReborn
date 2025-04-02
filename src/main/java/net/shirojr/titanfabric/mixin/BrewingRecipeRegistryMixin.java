package net.shirojr.titanfabric.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.shirojr.titanfabric.init.TitanFabricItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(BrewingRecipeRegistry.class)
public abstract class BrewingRecipeRegistryMixin {
    @Redirect(method = "registerDefaults",
            slice = @Slice(
                    from = @At(value = "FIELD", target = "Lnet/minecraft/item/Items;DRAGON_BREATH:Lnet/minecraft/item/Item;")
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/recipe/BrewingRecipeRegistry$Builder;registerItemRecipe(Lnet/minecraft/item/Item;Lnet/minecraft/item/Item;Lnet/minecraft/item/Item;)V"))
    private static void titanfabric$replaceDragonBreathRecipe(BrewingRecipeRegistry.Builder instance, Item input, Item ingredient, Item output) {
        instance.registerItemRecipe(Items.SPLASH_POTION, TitanFabricItems.LEGEND_POWDER, Items.LINGERING_POTION);
    }
}
