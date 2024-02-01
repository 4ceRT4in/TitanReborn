package net.shirojr.titanfabric.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import net.shirojr.titanfabric.recipe.TitanFabricRecipes;
import net.shirojr.titanfabric.recipe.custom.EssenceRecipe;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(CraftingScreenHandler.class)
public abstract class CraftingScreenHandlerMixin {
/*    @Unique private ItemStack recipeItemStack;

    @Inject(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/CraftingResultInventory;setStack(ILnet/minecraft/item/ItemStack;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void titanfabric$injectCustomRecipes(ScreenHandler handler, World world, PlayerEntity player,
                                                        CraftingInventory craftingInventory, CraftingResultInventory resultInventory,
                                                        CallbackInfo ci, ServerPlayerEntity serverPlayerEntity, ItemStack itemStack) {
        //LoggerUtil.devLogger("mixin for itemStack: " + itemStack.getName());
        if (!itemStack.isEmpty() || world.getServer() == null) return;
        Optional<EssenceRecipe> optionalRecipe = world.getServer().getRecipeManager().getFirstMatch(TitanFabricRecipes.WEAPON_EFFECT_RECIPE_TYPE, craftingInventory, world);
        if (optionalRecipe.isEmpty()) return;
        CraftingRecipe craftingRecipe = optionalRecipe.get();
        if (!resultInventory.shouldCraftRecipe(world, serverPlayerEntity, craftingRecipe)) return;

        itemStack = craftingRecipe.craft(craftingInventory);
        //LoggerUtil.devLogger("mixin for itemStack: " + itemStack.getName());
    }*/
}
