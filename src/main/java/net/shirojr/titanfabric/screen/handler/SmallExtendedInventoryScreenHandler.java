package net.shirojr.titanfabric.screen.handler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.shirojr.titanfabric.persistent.PersistentPlayerData;
import net.shirojr.titanfabric.persistent.PersistentWorldData;
import net.shirojr.titanfabric.screen.TitanFabricScreenHandlers;

public class SmallExtendedInventoryScreenHandler extends ScreenHandler {
    private final Inventory extendedInventory;

    public SmallExtendedInventoryScreenHandler(int syncId, Inventory extendedInventory) {
        super(TitanFabricScreenHandlers.SMALL_EXTENDED_INVENTORY_SCREEN_HANDLER, syncId);
        this.extendedInventory = extendedInventory;
        addExtendedInventorySlots(extendedInventory);
    }

    @Override
    public void close(PlayerEntity player) {
        if (player.getServer() != null) {
            PersistentPlayerData playerData = PersistentWorldData.getPersistentPlayerData(player);
            if (playerData != null) {
                playerData.extraInventory = this.extendedInventory;
            }
        }
        super.close(player);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return player.hasPermissionLevel(2);
    }

    private void addExtendedInventorySlots(Inventory extendedInventory) {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4; j++) {
                int index = j + i * 4;
                int x = j * 18;
                int y = i * 18;
                this.addSlot(new Slot(extendedInventory, index, x + 91, y + 26));
            }
        }
    }
}
