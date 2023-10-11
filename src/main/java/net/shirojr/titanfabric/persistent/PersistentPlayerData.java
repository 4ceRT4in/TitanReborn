package net.shirojr.titanfabric.persistent;

import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;

public class PersistentPlayerData {
    private static final int INV_SIZE = 8;
    public final Inventory playerInventory = new SimpleInventory(INV_SIZE);

}
