package net.shirojr.titanfabric.screen.handler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.shirojr.titanfabric.item.custom.misc.BackPackItem;
import net.shirojr.titanfabric.network.packet.BackPackScreenPacket;
import net.shirojr.titanfabric.screen.TitanFabricScreenHandlers;
import net.shirojr.titanfabric.data.BackPackContent;

import java.awt.*;

public class BackPackItemScreenHandler extends ScreenHandler {
    private final ItemStack backpackStack;
    private final BackPackContent content;

    public BackPackItemScreenHandler(int syncId, PlayerInventory playerInventory, BackPackScreenPacket packet) {
        this(syncId, playerInventory, packet.backPackStack());
    }

    public BackPackItemScreenHandler(int syncId, PlayerInventory playerInventory, ItemStack backpackStack) {
        super(TitanFabricScreenHandlers.BACKPACK_ITEM_SCREEN_HANDLER, syncId);
        this.backpackStack = backpackStack;
        this.content = BackPackContent.getOrThrow(this.backpackStack);
        this.content.onOpen(playerInventory.player);

        Point location = switch (content.type()) {
            case MEDIUM -> new Point(35, 22);
            case BIG -> new Point(35, 18);
            default -> new Point(35, 34);
        };
        addStorageSlots(content, location);
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    public BackPackItem.Type getBackPackItemType() {
        return this.content.type();
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.backpackStack.getItem() instanceof BackPackItem;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        content.savePersistent(this.backpackStack);
    }

    private void addStorageSlots(BackPackContent content, Point pos) {
        int columns = 6, slotSize = 18;
        int rows = (int) (double) (content.type().getSize() / columns);
        Point slotPos = new Point(pos);

        int slotIndex = 0;
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                slotPos.y = pos.y + (slotSize * row);
                slotPos.x = pos.x + (slotSize * column);

                this.addSlot(new Slot(this.content.inventory(), slotIndex, slotPos.x, slotPos.y) {
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
        if (player.getMainHandStack().isEmpty()) {
            super.onSlotClick(slotId, button, actionType, player);
            return;
        }
        if (slotId >= 0) {
            Slot slot = this.slots.get(slotId);
            if (actionType == SlotActionType.SWAP && button >= 0 && button < 9) {
                ItemStack swappedItem = player.getInventory().getStack(button);
                if (swappedItem == player.getMainHandStack()) {
                    return;
                }
            }
            if (slot.hasStack()) {
                ItemStack stackInSlot = slot.getStack();
                if (stackInSlot == player.getMainHandStack() || this.getCursorStack() == player.getMainHandStack()) {
                    if (actionType == SlotActionType.THROW || actionType == SlotActionType.SWAP || actionType == SlotActionType.QUICK_MOVE ||
                            actionType == SlotActionType.PICKUP || actionType == SlotActionType.CLONE ||
                            actionType == SlotActionType.QUICK_CRAFT || actionType == SlotActionType.PICKUP_ALL) {
                        return;
                    }
                }
            }
        }
        if (this.getCursorStack() != player.getMainHandStack()) {
            super.onSlotClick(slotId, button, actionType, player);
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasStack()) {
            ItemStack toInsert = slot.getStack();
            itemStack = toInsert.copy();
            if (index < this.content.size()) {
                if (!this.insertItem(toInsert, 0, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            if (toInsert.isEmpty()) {
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
        if (stack == this.backpackStack)
            return false;
        return super.canInsertIntoSlot(stack, slot);
    }

}
