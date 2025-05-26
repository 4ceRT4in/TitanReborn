package net.shirojr.titanfabric.api;

import net.minecraft.inventory.Inventory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ItemScatterer;
import net.shirojr.titanfabric.data.ExtendedInventory;
import net.shirojr.titanfabric.persistent.PersistentPlayerData;
import net.shirojr.titanfabric.persistent.PersistentWorldData;

import java.util.Optional;

/**
 * Utility class to interact with the extended Inventory of TitanFabric. <br><br>
 * <I>Keep in mind that the data is only available on the logical server side.
 * If the inventories are needed on the client side,
 * custom S2C networking and maybe even caching may be necessary.</I>
 */
@SuppressWarnings("unused")
public class PlayerInventoryHandler {
    private PlayerInventoryHandler() {
    }

    /**
     * Get an extended Inventory of a player.<br>
     *
     * @param world  World instance
     * @param player Player instance for the Inventory which should be dropped
     * @return Will be empty if the player doesn't have an inventory yet.
     */
    public static Optional<ExtendedInventory> getExtendedInventory(ServerWorld world, ServerPlayerEntity player) {
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
     * If the player doesn't have an extended inventory in the files yet, it will create a new inventory entry for that.
     *
     * @param serverPlayer target player for changing the inventory
     * @param inventory    inventory which the player will get.
     */
    public static void setExtendedInventory(ServerPlayerEntity serverPlayer, ExtendedInventory inventory) {
        PersistentPlayerData playerData = PersistentWorldData.getPersistentPlayerData(serverPlayer);
        if (playerData == null) return;
        playerData.extraInventory = inventory;
    }


    /**
     * Drops the extended inventory items of a player.<br>
     * Items are spawned as entities and the inventory is cleaned up.
     *
     * @param serverPlayer player which should drop their extended Inventory
     * @return True, if items have been dropped successfully and the inventory wasn't empty to begin with.<br>
     * The return value can be ignored if this information is not needed.
     */
    public static boolean dropExtendedInventory(ServerPlayerEntity serverPlayer) {
        Optional<ExtendedInventory> inventory = getExtendedInventory(serverPlayer.getServerWorld(), serverPlayer);
        if (inventory.isEmpty()) return false;
        ItemScatterer.spawn(serverPlayer.getWorld(), serverPlayer.getBlockPos(), inventory.get().asInventory());
        setExtendedInventory(serverPlayer, new ExtendedInventory(PersistentPlayerData.INV_SIZE));
        return true;
    }

    /**
     * Get the extended Inventory of a team.<br>
     *
     * @param teamName Name of the team
     * @param server   Minecraft server instance
     * @return Will be empty if the team doesn't have an inventory yet.
     */
    public static Optional<ExtendedInventory> getTeamInventory(String teamName, MinecraftServer server) {
        PersistentPlayerData teamData = PersistentWorldData.getPersistentPlayerDataForTeam(teamName, server);
        if (teamData == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(teamData.extraInventory);
    }

    /**
     * Sets the extended Inventory of a specific team. The inventory needs to be of the same size as specified in
     * {@link PersistentPlayerData}. Otherwise, the inventory will be filled up with empty ItemStacks or will be reduced
     * to the size of the extended inventory space where left over items will be ignored. <br><br>
     * If the team doesn't have an extended inventory in the files yet, it will create a new inventory entry for that.
     *
     * @param teamName  target team for changing the inventory
     * @param inventory inventory which the team will get.
     * @param server    Minecraft server instance
     */
    public static void setTeamInventory(String teamName, ExtendedInventory inventory, MinecraftServer server) {
        PersistentPlayerData teamData = PersistentWorldData.getPersistentPlayerDataForTeam(teamName, server);
        if (teamData == null) return;
        teamData.extraInventory = inventory;
    }

    /**
     * Drops the extended inventory items of a team.<br>
     * Items are spawned as entities and the inventory is cleaned up.
     *
     * @param teamName Name of the team whose inventory should be dropped
     * @param server   Minecraft server instance
     * @return True, if items have been dropped successfully and the inventory wasn't empty to begin with.<br>
     * The return value can be ignored if this information is not needed.
     */
    public static boolean dropTeamInventory(String teamName, MinecraftServer server) {
        Optional<ExtendedInventory> inventory = getTeamInventory(teamName, server);
        if (inventory.isEmpty()) return false;
        // Assuming you want to drop items at the position of each team member or a specific position
        // For simplicity, let's drop it at the first player in the team's location
        server.getPlayerManager().getPlayerList().stream()
                .filter(p -> teamName.equals(p.getScoreboardTeam() != null ? p.getScoreboardTeam().getName() : null))
                .findFirst().ifPresent(player -> ItemScatterer.spawn(player.getWorld(), player.getBlockPos(), inventory.get().asInventory()));
        setTeamInventory(teamName, new ExtendedInventory(PersistentPlayerData.INV_SIZE), server);
        return true;
    }

}
