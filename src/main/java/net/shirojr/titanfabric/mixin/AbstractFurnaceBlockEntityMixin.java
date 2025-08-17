package net.shirojr.titanfabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.shirojr.titanfabric.block.entity.DiamondFurnaceBlockEntity;
import net.shirojr.titanfabric.init.TitanFabricTags;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin implements SidedInventory {

    @Shadow
    protected DefaultedList<ItemStack> inventory;

    @Unique
    private static final int[] EXTENDED_BOTTOM_SLOTS = new int[]{2, 1, 3};

    @Inject(method = "<init>*", at = @At("RETURN"))
    private void titanfabric$initializeExtendedInventory(BlockEntityType blockEntityType, BlockPos pos, BlockState state, RecipeType recipeType, CallbackInfo ci) {
        if ((Object) this instanceof DiamondFurnaceBlockEntity) {
            this.inventory = DefaultedList.ofSize(4, ItemStack.EMPTY);
        }
    }

    @Inject(method = "getAvailableSlots", at = @At("HEAD"), cancellable = true)
    private void titanfabric$getModifiedAvailableSlots(Direction side, CallbackInfoReturnable<int[]> cir) {
        if (!((AbstractFurnaceBlockEntity) (Object) this instanceof DiamondFurnaceBlockEntity)) return;
        if (side == Direction.DOWN) {
            cir.setReturnValue(EXTENDED_BOTTOM_SLOTS);
        }
    }

    @Inject(method = "isValid", at = @At("HEAD"), cancellable = true)
    private void titanfabric$isValidForNewSlot(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!((AbstractFurnaceBlockEntity) (Object) this instanceof DiamondFurnaceBlockEntity)) return;
        if (slot == 3) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "readNbt", at = @At("HEAD"))
    private void titanfabric$readNbtWithExtraSlot(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup, CallbackInfo ci) {
        if ((Object) this instanceof DiamondFurnaceBlockEntity) {
            this.inventory = DefaultedList.ofSize(4, ItemStack.EMPTY);
        }
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/AbstractFurnaceBlockEntity;getCookTime(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/AbstractFurnaceBlockEntity;)I"))
    private static int titanfabric$getDiamondFurnaceCookTime(World world, AbstractFurnaceBlockEntity furnace, Operation<Integer> original, @Local(argsOnly = true) AbstractFurnaceBlockEntity blockEntity) {
        int originalEvaluation = original.call(world, furnace);
        return blockEntity instanceof DiamondFurnaceBlockEntity ? originalEvaluation / 2 : originalEvaluation;
    }

    @WrapOperation(method = "setStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/AbstractFurnaceBlockEntity;getCookTime(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/AbstractFurnaceBlockEntity;)I"))
    private int titanfabric$getDiamondFurnaceCookTimeOnStack(World world, AbstractFurnaceBlockEntity furnace, Operation<Integer> original) {
        int originalEvaluation = original.call(world, furnace);
        BlockEntity blockEntity = (AbstractFurnaceBlockEntity) (Object) this;
        return blockEntity instanceof DiamondFurnaceBlockEntity ? originalEvaluation / 2 : originalEvaluation;
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/AbstractFurnaceBlockEntity;canAcceptRecipeOutput(Lnet/minecraft/registry/DynamicRegistryManager;Lnet/minecraft/recipe/RecipeEntry;Lnet/minecraft/util/collection/DefaultedList;I)Z"))
    private static boolean titanFabric$blockLowHeatSmelting(DynamicRegistryManager registryManager, @Nullable RecipeEntry<?> recipe, DefaultedList<ItemStack> slots, int count, Operation<Boolean> original, @Local(argsOnly = true) AbstractFurnaceBlockEntity blockEntity) {
        ItemStack smeltMaterialStack = slots.get(0);
        if (smeltMaterialStack.isIn(TitanFabricTags.Items.HIGH_HEAT_SMELTING)) {
            if (!blockEntity.getCachedState().isIn(TitanFabricTags.Blocks.HIGH_HEAT_FURNACES)) return false;
        }
        return original.call(registryManager, recipe, slots, count);
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/AbstractFurnaceBlockEntity;canAcceptRecipeOutput(Lnet/minecraft/registry/DynamicRegistryManager;Lnet/minecraft/recipe/RecipeEntry;Lnet/minecraft/util/collection/DefaultedList;I)Z"))
    private static boolean titanfabric$canAcceptRecipeOutputWithTwoSlots(DynamicRegistryManager registryManager, @Nullable RecipeEntry<?> recipe, DefaultedList<ItemStack> slots, int count, Operation<Boolean> original, @Local(argsOnly = true) AbstractFurnaceBlockEntity blockEntity) {
        if (!(blockEntity instanceof DiamondFurnaceBlockEntity)) {
            return original.call(registryManager, recipe, slots, count);
        }
        if (blockEntity.size() < 4) {
            return original.call(registryManager, recipe, slots, count);
        }
        if (slots.get(0).isEmpty() || recipe == null) {
            return false;
        }
        ItemStack output = recipe.value().getResult(registryManager);
        if (output.isEmpty()) {
            return false;
        }
        ItemStack firstOutput = slots.get(2);
        if (firstOutput.isEmpty()) {
            return true;
        }
        if (ItemStack.areItemsAndComponentsEqual(firstOutput, output)) {
            if (firstOutput.getCount() < count && firstOutput.getCount() < firstOutput.getMaxCount()) {
                return true;
            }
        } else {
            ItemStack secondOutput = slots.get(3);
            if (secondOutput.isEmpty()) {
                return true;
            }
            if (ItemStack.areItemsAndComponentsEqual(secondOutput, output)) {
                if (secondOutput.getCount() < count && secondOutput.getCount() < secondOutput.getMaxCount()) {
                    return true;
                }
                return secondOutput.getCount() < output.getMaxCount();
            }
            return false;
        }
        ItemStack secondOutput = slots.get(3);
        if (secondOutput.isEmpty()) {
            return true;
        }
        if (!ItemStack.areItemsAndComponentsEqual(secondOutput, output)) {
            return false;
        }
        if (secondOutput.getCount() < count && secondOutput.getCount() < secondOutput.getMaxCount()) {
            return true;
        }
        return secondOutput.getCount() < output.getMaxCount();
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/AbstractFurnaceBlockEntity;craftRecipe(Lnet/minecraft/registry/DynamicRegistryManager;Lnet/minecraft/recipe/RecipeEntry;Lnet/minecraft/util/collection/DefaultedList;I)Z"))
    private static boolean titanFabric$craftDiamondFurnaceRecipe(DynamicRegistryManager registryManager, @Nullable RecipeEntry<?> recipe, DefaultedList<ItemStack> slots, int count, Operation<Boolean> original, @Local(argsOnly = true) AbstractFurnaceBlockEntity furnaceBlockEntity) {
        if (!(furnaceBlockEntity instanceof DiamondFurnaceBlockEntity)) {
            return original.call(registryManager, recipe, slots, count);
        }
        ItemStack inputSlotStack = slots.get(0);
        ItemStack fuelSlotStack = slots.get(1);
        ItemStack outputSlotStack = slots.get(2);
        ItemStack outputSlotStack2 = slots.get(3);
        int changeAmount = inputSlotStack.isIn(TitanFabricTags.Items.BETTER_SMELTING_ITEMS) ? 2 : 1;
        if (recipe == null || !hasValidOutput(registryManager, recipe.value(), slots, count, changeAmount)) {
            return false;
        }
        ItemStack recipeOutputStack = recipe.value().getResult(registryManager);
        ItemStack output = recipeOutputStack.copy();
        if (outputSlotStack.isEmpty()) {
            output.setCount(changeAmount);
            slots.set(2, output);
        } else if (outputSlotStack.isOf(recipeOutputStack.getItem()) && outputSlotStack.getCount() + changeAmount <= outputSlotStack.getMaxCount()) {
            outputSlotStack.increment(changeAmount);
        } else if (outputSlotStack2.isEmpty()) {
            output.setCount(changeAmount);
            slots.set(3, output);
        } else if (outputSlotStack2.isOf(recipeOutputStack.getItem()) && outputSlotStack2.getCount() + changeAmount <= outputSlotStack2.getMaxCount()) {
            outputSlotStack2.increment(changeAmount);
        } else {
            return false;
        }
        if (inputSlotStack.isOf(Blocks.WET_SPONGE.asItem()) && !fuelSlotStack.isEmpty() && fuelSlotStack.isOf(Items.BUCKET)) {
            slots.set(1, new ItemStack(Items.WATER_BUCKET));
        }
        inputSlotStack.decrement(1);
        return true;
    }

    @Unique
    private static boolean hasValidOutput(DynamicRegistryManager registryManager, Recipe<?> recipe, DefaultedList<ItemStack> slots, int inputSlotCount, int changeAmount) {
        ItemStack smeltingMaterial = slots.get(0);
        ItemStack outputSlotStack = slots.get(2);
        ItemStack outputSlotStack2 = slots.get(3);
        ItemStack recipeOutputStack = recipe.getResult(registryManager);

        if (smeltingMaterial.isEmpty() || recipeOutputStack.isEmpty()) return false;
        if (outputSlotStack.isEmpty()) return true;

        if (ItemStack.areItemsAndComponentsEqual(outputSlotStack, recipeOutputStack)) {
            int outputSlotCount = outputSlotStack.getCount();
            if (outputSlotCount < inputSlotCount && outputSlotCount + changeAmount <= outputSlotStack.getMaxCount()) {
                return true;
            }
        }
        if (outputSlotStack2.isEmpty()) return true;

        if (ItemStack.areItemsAndComponentsEqual(outputSlotStack2, recipeOutputStack)) {
            int outputSlot2Count = outputSlotStack2.getCount();
            return outputSlot2Count < inputSlotCount && outputSlot2Count + changeAmount <= outputSlotStack2.getMaxCount();
        }
        return false;
    }
}