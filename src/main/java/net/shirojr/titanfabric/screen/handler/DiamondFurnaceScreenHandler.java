package net.shirojr.titanfabric.screen.handler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.FurnaceOutputSlot;
import net.minecraft.screen.slot.Slot;
import net.shirojr.titanfabric.block.entity.DiamondFurnaceBlockEntity;
import net.shirojr.titanfabric.screen.TitanFabricScreenHandlers;

public class DiamondFurnaceScreenHandler extends AbstractFurnaceScreenHandler {

    public DiamondFurnaceScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(4), new ArrayPropertyDelegate(4));
    }

    public DiamondFurnaceScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(TitanFabricScreenHandlers.DIAMOND_FURNACE_SCREEN_HANDLER, RecipeType.SMELTING, RecipeBookCategory.FURNACE, syncId, playerInventory, inventory, propertyDelegate);
        this.addSlot(new FurnaceOutputSlot(playerInventory.player, inventory, 3, 143, 35));
    }

    @Override
    public boolean canInsertIntoSlot(int index) {
        return index != 1 && index != 2 && index != 3;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack slotStack = slot.getStack();
            result = slotStack.copy();
            if (index == 2 || index == 3) {
                if (!this.insertItem(slotStack, 4, 40, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickTransfer(slotStack, result);
            } else if (index == 0 || index == 1) {
                if (!this.insertItem(slotStack, 4, 40, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (this.isSmeltable(slotStack)) {
                    if (!this.insertItem(slotStack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.isFuel(slotStack)) {
                    if (!this.insertItem(slotStack, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }
            if (slotStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
            if (slotStack.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTakeItem(player, slotStack);
        }
        return result;
    }
}
