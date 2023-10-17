package net.shirojr.titanfabric.screen.handler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
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
        super(backPackType == BackPackItem.Type.SMALL ? TitanFabricScreenHandlers.BACKPACK_ITEM_SMALL_SCREEN_HANDLER :
                backPackType == BackPackItem.Type.MEDIUM ? TitanFabricScreenHandlers.BACKPACK_ITEM_MEDIUM_SCREEN_HANDLER :
                        TitanFabricScreenHandlers.BACKPACK_ITEM_BIG_SCREEN_HANDLER, syncId);
        checkSize(inventory, backPackType.getSize());

        this.inventory = inventory;
        this.backPackType = backPackType;
        this.backpackStack = backpackStack;

        inventory.onOpen(playerInventory.player);

        addStorageSlots(backPackType, new Point(35, 18)); //TODO: change pos depending on type
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
        int columns = 6, margin = 1, slotSize = 18;
        int rows = (int) (double) (type.getSize() / columns);
        Point slotPos = new Point(pos);

        int slotIndex = 0;
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                slotPos.y = pos.y + (slotSize * row);
                slotPos.x = pos.x + (slotSize * column);

                this.addSlot(new Slot(this.inventory, slotIndex, slotPos.x, slotPos.y));
                slotIndex++;
            }
            slotPos.x = pos.x;
        }
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
}
