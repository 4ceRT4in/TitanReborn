package net.shirojr.titanfabric.screen.handler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.shirojr.titanfabric.item.custom.misc.BackPackItem;
import net.shirojr.titanfabric.screen.TitanFabricScreenHandlers;

import java.awt.*;

public class BackPackItemScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final BackPackItem.Type backPackType;

    public BackPackItemScreenHandler(int syncId, PlayerInventory playerInventory, BackPackItem.Type type) {
        this(syncId, playerInventory, new SimpleInventory(type.getSize()), type);
    }

    public BackPackItemScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, BackPackItem.Type backPackType) {
        super(TitanFabricScreenHandlers.BACKPACK_ITEM_SMALL_SCREEN_HANDLER, syncId);
        checkSize(inventory, backPackType.getSize());

        this.inventory = inventory;
        this.backPackType = backPackType;

        inventory.onOpen(playerInventory.player);

        addStorageSlots(backPackType, new Point(20, 20));
    }

    public BackPackItem.Type getBackPackitemType() {
        return this.backPackType;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return false;
    }

    private void addStorageSlots(BackPackItem.Type type, Point pos) {
        int columns = 6, margin = 5, slotSize = 16;
        int rows = (int) (double) (type.getSize() / columns);
        
        int slotIndex = 0;
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                pos.y = pos.y + ((slotSize + margin) * row);
                pos.x = pos.x + ((slotSize + margin) * column);

                this.addSlot(new Slot(this.inventory, slotIndex, pos.x, pos.y));
                slotIndex++;
            }
        }
    }
}