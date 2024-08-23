package net.shirojr.titanfabric.screen.handler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.shirojr.titanfabric.item.custom.misc.BackPackItem;
import net.shirojr.titanfabric.screen.TitanFabricScreenHandlers;

import java.awt.*;

public class BackPackItemScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final BackPackItem.Type backPackType;
    private final ItemStack backpackStack;

    public BackPackItemScreenHandler(int syncId, PlayerInventory playerInventory, BackPackItem.Type type, PacketByteBuf buf) {
        this(syncId, playerInventory, new SimpleInventory(type.getSize()), type, buf.readItemStack());
    }

    public BackPackItemScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, BackPackItem.Type backPackType, ItemStack backpackStack) {
        super(backPackType == BackPackItem.Type.SMALL ? TitanFabricScreenHandlers.BACKPACK_ITEM_SMALL_SCREEN_HANDLER
                : backPackType == BackPackItem.Type.MEDIUM ? TitanFabricScreenHandlers.BACKPACK_ITEM_MEDIUM_SCREEN_HANDLER
                : TitanFabricScreenHandlers.BACKPACK_ITEM_BIG_SCREEN_HANDLER, syncId);
        checkSize(inventory, backPackType.getSize());

        this.inventory = inventory;
        this.backPackType = backPackType;
        this.backpackStack = backpackStack;

        inventory.onOpen(playerInventory.player);

        Point location;
        switch (backPackType) {
            case MEDIUM -> location = new Point(35, 22);
            case BIG -> location = new Point(35, 18);
            default -> location = new Point(35, 34);
        }
        addStorageSlots(backPackType, location);
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    public BackPackItem.Type getBackPackItemType() {
        return this.backPackType;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.backpackStack.getItem() instanceof BackPackItem;
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        BackPackItem.writeNbtFromInventory(backpackStack, inventory);
    }

    private void addStorageSlots(BackPackItem.Type type, Point pos) {
        int columns = 6, slotSize = 18;
        int rows = (int) (double) (type.getSize() / columns);
        Point slotPos = new Point(pos);

        int slotIndex = 0;
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                slotPos.y = pos.y + (slotSize * row);
                slotPos.x = pos.x + (slotSize * column);

                this.addSlot(new Slot(this.inventory, slotIndex, slotPos.x, slotPos.y) {
                    @Override
                    public boolean canInsert(ItemStack stack) {
                        if (stack.getItem() instanceof BackPackItem) {
                            return false;
                        }
                        return super.canInsert(stack);
                    }
                });
                slotIndex++;
            }
            slotPos.x = pos.x;
        }
    }

    @Override
    public void onSlotClick(int slotId, int button, SlotActionType actionType, PlayerEntity player) {
        Slot slot = this.getSlot(slotId);

        // Prevent moving the backpack item itself
        if (slot != null && slot.getStack() == this.backpackStack) {
            return; // Cancel the action if the backpack is clicked
        }

        super.onSlotClick(slotId, button, actionType, player);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot) this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (index < this.inventory.size() ? !this.insertItem(itemStack2, this.inventory.size(), this.slots.size(), true) : !this.insertItem(itemStack2, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return itemStack;
    }

    private void addPlayerInventory(PlayerInventory playerInventory) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
        if (stack.getItem() instanceof BackPackItem)
            return false;
        return super.canInsertIntoSlot(stack, slot);
    }

}
