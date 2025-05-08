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
import net.shirojr.titanfabric.init.TitanFabricDataComponents;
import net.shirojr.titanfabric.item.custom.misc.BackPackItem;
import net.shirojr.titanfabric.network.packet.BackPackScreenPacket;
import net.shirojr.titanfabric.screen.TitanFabricScreenHandlers;
import net.shirojr.titanfabric.util.BackPackContent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BackPackItemScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final ItemStack backpackStack;

    public BackPackItemScreenHandler(int syncId, PlayerInventory playerInventory, BackPackScreenPacket packet) {
        this(syncId, playerInventory, new SimpleInventory(packet.getSize()), type, buf.readItemStack());
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
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        List<ItemStack> storedStacks = new ArrayList<>();
        for (int i = 0; i < inventory.size(); i++) {
            storedStacks.add(i, inventory.getStack(i));
        }
        BackPackContent content = new BackPackContent(storedStacks, backPackType);
        backpackStack.set(TitanFabricDataComponents.BACKPACK_CONTENT, content);
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
            if (index < backPackType.getSize()) {
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
