package net.shirojr.titanfabric.persistent;

import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.shirojr.titanfabric.data.ExtendedInventory;

public class PersistentPlayerData {
    public static final int INV_SIZE = 8;
    public ExtendedInventory extraInventory = new ExtendedInventory(INV_SIZE);
    public PersistentPlayerData() {
        this.extraInventory = new ExtendedInventory(INV_SIZE);
    }
    public PersistentPlayerData(ExtendedInventory inventory) {
        this.extraInventory = inventory;
    }

}
