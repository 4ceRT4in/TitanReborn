package net.shirojr.titanfabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.world.World;
import net.shirojr.titanfabric.block.entity.DiamondFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractFurnaceBlockEntity.class)
public class AbstractFurnaceBlockEntityMixin {
    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/AbstractFurnaceBlockEntity;getCookTime(Lnet/minecraft/world/World;Lnet/minecraft/recipe/RecipeType;Lnet/minecraft/inventory/Inventory;)I"))
    private static int titanfabric$getDiamondFurnaceCookTime(World world, RecipeType<? extends AbstractCookingRecipe> recipeType, Inventory inventory, Operation<Integer> original, @Local(argsOnly = true) AbstractFurnaceBlockEntity blockEntity) {
        int originalEvaluation = original.call(world, recipeType, inventory);
        return blockEntity instanceof DiamondFurnaceBlockEntity ? originalEvaluation / 2 : originalEvaluation;
    }

    @WrapOperation(method = "setStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/AbstractFurnaceBlockEntity;getCookTime(Lnet/minecraft/world/World;Lnet/minecraft/recipe/RecipeType;Lnet/minecraft/inventory/Inventory;)I"))
    private int titanfabric$getDiamondFurnaceCookTimeOnStack(World world, RecipeType<? extends AbstractCookingRecipe> recipeType, Inventory inventory, Operation<Integer> original) {
        int originalEvaluation = original.call(world, recipeType, inventory);
        BlockEntity blockEntity = (AbstractFurnaceBlockEntity) (Object) this;
        return blockEntity instanceof DiamondFurnaceBlockEntity ? originalEvaluation / 2 : originalEvaluation;
    }
}
