package net.shirojr.titanfabric.persistent;

import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;

public class PersistentPlayerData {
    public static final int INV_SIZE = 8;
    public Inventory extraInventory = new SimpleInventory(INV_SIZE);
    public PersistentPlayerData() {
        this.extraInventory = new SimpleInventory(INV_SIZE);
    }
    public PersistentPlayerData(Inventory inventory) {
        this.extraInventory = inventory;
    }

}
