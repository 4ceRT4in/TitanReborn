package net.shirojr.titanfabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.shirojr.titanfabric.block.entity.DiamondFurnaceBlockEntity;
import net.shirojr.titanfabric.util.TitanFabricTags;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin {

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

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/AbstractFurnaceBlockEntity;canAcceptRecipeOutput(Lnet/minecraft/recipe/Recipe;Lnet/minecraft/util/collection/DefaultedList;I)Z"))
    private static boolean titanFabric$blockLowHeatSmelting(@Nullable Recipe<?> recipe, DefaultedList<ItemStack> slots, int count, Operation<Boolean> original, @Local(argsOnly = true) AbstractFurnaceBlockEntity blockEntity) {
        ItemStack smeltMaterialStack = slots.get(0);
        if (smeltMaterialStack.isIn(TitanFabricTags.Items.HIGH_HEAT_SMELTING)) {
            if (!blockEntity.getCachedState().isIn(TitanFabricTags.Blocks.HIGH_HEAT_FURNACES)) return false;
        }
        return original.call(recipe, slots, count);
    }


    // this boolean target method has the side effect of crafting instantly!
    // only access the original call when actually necessary...
    @WrapOperation(method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/entity/AbstractFurnaceBlockEntity;craftRecipe(Lnet/minecraft/recipe/Recipe;Lnet/minecraft/util/collection/DefaultedList;I)Z"
            )
    )
    private static boolean titanFabric$craftDiamondFurnaceRecipe(@Nullable Recipe<?> recipe, DefaultedList<ItemStack> slots,
                                                                 int count, Operation<Boolean> original,
                                                                 @Local(argsOnly = true) AbstractFurnaceBlockEntity furnaceBlockEntity) {
        if (!(furnaceBlockEntity instanceof DiamondFurnaceBlockEntity)) return original.call(recipe, slots, count);
        ItemStack inputSlotStack = slots.get(0);
        ItemStack fuelSlotStack = slots.get(1);
        ItemStack outputSlotStack = slots.get(2);
        int changeAmount = inputSlotStack.isIn(TitanFabricTags.Items.BETTER_SMELTING_ITEMS) ? 2 : 1;
        if (recipe == null || !hasValidOutput(recipe, slots, count, changeAmount)) return false;
        ItemStack recipeOutputStack = recipe.getOutput();

        if (outputSlotStack.isEmpty()) {
            ItemStack output = recipeOutputStack.copy();
            output.setCount(changeAmount);
            slots.set(2, output);
        } else if (outputSlotStack.isOf(recipeOutputStack.getItem())) {
            outputSlotStack.increment(changeAmount);
        }
        if (inputSlotStack.isOf(Blocks.WET_SPONGE.asItem()) && !fuelSlotStack.isEmpty() && fuelSlotStack.isOf(Items.BUCKET)) {
            slots.set(1, new ItemStack(Items.WATER_BUCKET));
        }
        inputSlotStack.decrement(1);
        return true;
    }

    @Unique
    private static boolean hasValidOutput(Recipe<?> recipe, DefaultedList<ItemStack> slots, int inputSlotCount, int changeAmount) {
        ItemStack smeltingMaterial = slots.get(0);
        ItemStack outputSlotStack = slots.get(2);
        ItemStack recipeOutputStack = recipe.getOutput();
        int outputSlotCount = outputSlotStack.getCount();

        if (smeltingMaterial.isEmpty() || recipeOutputStack.isEmpty()) return false;
        if (outputSlotStack.isEmpty()) return true;
        if (!outputSlotStack.isItemEqualIgnoreDamage(recipeOutputStack)) return false;
        if (outputSlotCount < inputSlotCount / changeAmount && outputSlotCount < outputSlotStack.getMaxCount())
            return true;
        return outputSlotStack.getCount() < smeltingMaterial.getMaxCount();
    }
}
