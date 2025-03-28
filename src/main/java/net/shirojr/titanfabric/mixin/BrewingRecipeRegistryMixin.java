package net.shirojr.titanfabric.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.shirojr.titanfabric.init.TitanFabricItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(BrewingRecipeRegistry.class)
public abstract class BrewingRecipeRegistryMixin {

    @Shadow
    private static void registerItemRecipe(Item input, Item ingredient, Item output) { }

    @Redirect(method = "registerDefaults",
            slice = @Slice(
                    from = @At(value = "FIELD", target = "Lnet/minecraft/item/Items;DRAGON_BREATH:Lnet/minecraft/item/Item;")
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/recipe/BrewingRecipeRegistry;registerItemRecipe(Lnet/minecraft/item/Item;Lnet/minecraft/item/Item;Lnet/minecraft/item/Item;)V"))
    private static void titanfabric$replaceDragonBreathRecipe(Item input, Item ingredient, Item output) {
        registerItemRecipe(Items.SPLASH_POTION, TitanFabricItems.LEGEND_POWDER, Items.LINGERING_POTION);
    }
}
