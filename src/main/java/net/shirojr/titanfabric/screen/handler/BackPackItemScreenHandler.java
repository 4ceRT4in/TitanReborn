package net.shirojr.titanfabric.screen.handler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.shirojr.titanfabric.data.BackPackContent;
import net.shirojr.titanfabric.init.TitanFabricDataComponents;
import net.shirojr.titanfabric.item.custom.misc.BackPackItem;
import net.shirojr.titanfabric.network.packet.BackPackScreenPacket;
import net.shirojr.titanfabric.init.TitanFabricScreenHandlers;
import net.shirojr.titanfabric.util.items.BackPackSlot;

import java.awt.*;

public class BackPackItemScreenHandler extends ScreenHandler {
    private final ItemStack backpackStack;
    private final BackPackContent content;

    public BackPackItemScreenHandler(int syncId, PlayerInventory playerInventory, BackPackScreenPacket packet) {
        this(syncId, playerInventory, packet.backPackStack());
    }

    public BackPackItemScreenHandler(int syncId, PlayerInventory playerInventory, ItemStack backpackStack) {
        super(TitanFabricScreenHandlers.BACKPACK, syncId);
        this.backpackStack = backpackStack;
        this.content = BackPackContent.getOrThrow(this.backpackStack);

        Point location = switch (content.type()) {
            case MEDIUM -> new Point(35, 22);
            case BIG -> new Point(35, 18);
            default -> new Point(35, 34);
        };
        addStorageSlots(content, location);
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
        this.backpackStack.set(TitanFabricDataComponents.BACKPACK_CONTENT, this.content);
    }

    public BackPackItem.Type getBackPackItemType() {
        return this.content.type();
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.content.inventory().canPlayerUse(player) && this.backpackStack.getItem() instanceof BackPackItem;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        content.savePersistent(this.backpackStack);
        super.onClosed(player);
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
                this.addSlot(new BackPackSlot(backpackStack, this.content.inventory(), slotIndex, slotPos.x, slotPos.y));
                slotIndex++;
            }
            slotPos.x = pos.x;
        }
    }

    @Override
    public void onSlotClick(int slotId, int button, SlotActionType actionType, PlayerEntity player) {
        /*if (slotId >= 0 && slotId < this.slots.size()) {
            Slot slot = this.slots.get(slotId);
            ItemStack stackInSlot = slot.getStack();
            if (stackInSlot.equals(this.backpackStack) || this.getCursorStack().equals(this.backpackStack)) {
                if (slot.inventory.equals(this.content.inventory())) {
                    return;
                }
            }
        }*/
        super.onSlotClick(slotId, button, actionType, player);
        this.content.inventory().markDirty();
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (!slot.hasStack()) return newStack;

        ItemStack originalStack = slot.getStack();
        newStack = originalStack.copy();
        if (invSlot < this.content.size()) {
            if (!this.insertItem(originalStack, this.content.size(), this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (!this.insertItem(originalStack, 0, this.content.size(), false)) {
            return ItemStack.EMPTY;
        }

        if (originalStack.isEmpty()) {
            slot.setStack(ItemStack.EMPTY);
        } else {
            slot.markDirty();
        }
        return newStack;
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
        if (stack.equals(this.backpackStack)) return false;
        return super.canInsertIntoSlot(stack, slot);
    }
}
