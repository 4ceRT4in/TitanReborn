package net.shirojr.titanfabric.api;

import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ItemScatterer;
import net.shirojr.titanfabric.persistent.PersistentPlayerData;
import net.shirojr.titanfabric.persistent.PersistentWorldData;

import java.util.Optional;

/**
 * Utility class to interact with the extended Inventory of TitanFabric. <br><br>
 * <I>Keep in mind that the data is only available on the logical server side.
 * If the inventories are needed on the client side,
 * custom C2S networking and maybe even caching may be necessary.</I>
 */
@SuppressWarnings("unused")
public class PlayerInventoryHandler {
    private PlayerInventoryHandler() {
    }

    /**
     * Get an extended Inventory of a player.<br>
     * <I>The inventory is only available on the logical server side.</I>
     *
     * @param world  World instance
     * @param player Player instance for the Inventory which should be dropped
     * @return Will be empty if the player doesn't have an inventory yet.
     */
    public static Optional<Inventory> getExtendedInventory(ServerWorld world, ServerPlayerEntity player) {
        if (world.isClient()) return Optional.empty();
        PersistentPlayerData playerData = PersistentWorldData.getPersistentPlayerData(player);
        if (playerData == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(playerData.extraInventory);
    }

    /**
     * Sets the extended Inventory of a specific player. The inventory needs to be of the same size as specified in
     * {@link PersistentPlayerData}. Otherwise, the inventory will be filled up with empty ItemStacks or will be reduced
     * to the size of the extended inventory space where left over items will be ignored. <br><br>
     * If the player doesn't have an extended inventory entry in the files yet, it will create a new inventory for that.
     *
     * @param serverPlayer target player for changing the inventory
     * @param inventory    inventory which the player will get.
     */
    public static void setExtendedInventory(ServerPlayerEntity serverPlayer, Inventory inventory) {
        ItemStack[] inventoryStacks = new ItemStack[PersistentPlayerData.INV_SIZE];
        for (int i = 0; i < PersistentPlayerData.INV_SIZE; i++) {
            ItemStack stack = ItemStack.EMPTY;
            if (inventory.size() > i) {
                stack = inventory.getStack(i);
            }
            inventoryStacks[i] = (stack);
        }
        Inventory newInventory = new SimpleInventory(inventoryStacks);
        PersistentPlayerData playerData = PersistentWorldData.getPersistentPlayerData(serverPlayer);
        if (playerData == null) return;
        playerData.extraInventory = newInventory;
    }

    /**
     * Drops the extended inventory items of a player.
     *
     * @param serverPlayer player which should drop its extended Inventory
     * @return true, if items have been dropped successfully. Can be ignored if this information is not needed.
     */
    public static boolean dropExtendedInventory(ServerPlayerEntity serverPlayer) {
        Optional<Inventory> inventory = getExtendedInventory(serverPlayer.getWorld(), serverPlayer);
        if (inventory.isEmpty()) return false;
        ItemScatterer.spawn(serverPlayer.getWorld(), serverPlayer.getBlockPos(), inventory.get());
        setExtendedInventory(serverPlayer, new SimpleInventory(PersistentPlayerData.INV_SIZE));
        return true;
    }
}
