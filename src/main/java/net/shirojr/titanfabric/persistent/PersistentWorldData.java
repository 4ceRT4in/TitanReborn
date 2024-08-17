package net.shirojr.titanfabric.persistent;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.util.LoggerUtil;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

public class PersistentWorldData extends PersistentState {
    private Inventory worldInventory = new SimpleInventory(PersistentPlayerData.INV_SIZE);
    public HashMap<UUID, PersistentPlayerData> players = new HashMap<>();
    public HashMap<String, PersistentPlayerData> teamData = new HashMap<>();
    public static final String WORLD_ITEMS_NBT_KEY = TitanFabric.MODID + ".worldInventory";
    public static final String PLAYER_ITEMS_NBT_KEY = TitanFabric.MODID + ".playerInventory";
    public static final String TEAM_ITEMS_NBT_KEY = TitanFabric.MODID + ".teamInventory";

    @Nullable
    public static PersistentPlayerData getPersistentPlayerData(LivingEntity entity) {
        if (entity.getWorld().getServer() == null) {
            LoggerUtil.devLogger("given LivingEntity doesn't supply a server", true, null);
            return null;
        }
        PersistentWorldData serverState = getServerState(entity.getWorld().getServer());

        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) entity;
            String teamName = getPlayerTeamName(player);
            if (teamName != null) {
                // If player is in a team, return or create a PersistentPlayerData for the team
                return serverState.teamData.computeIfAbsent(teamName, k -> new PersistentPlayerData());
            }
        }

        // If not a player or not in a team, use individual inventory
        return serverState.players.computeIfAbsent(entity.getUuid(), uuid -> new PersistentPlayerData());
    }

    public static PersistentPlayerData getPersistentPlayerDataForTeam(String teamName, MinecraftServer server) {

                PersistentWorldData serverState = getServerState(server);
                // If player is in a team, return or create a PersistentPlayerData for the team
                return serverState.teamData.computeIfAbsent(teamName, k -> new PersistentPlayerData());

    }

    @Nullable
    public static PersistentPlayerData getPersistentPlayerData(ServerWorld world, UUID uuid) {
        PersistentWorldData serverState = getServerState(world.getServer());
        ServerPlayerEntity player = world.getServer().getPlayerManager().getPlayer(uuid);
        if (player != null) {
            String teamName = getPlayerTeamName(player);
            if (teamName != null) {
                // If player is in a team, return or create a PersistentPlayerData for the team
                return serverState.teamData.computeIfAbsent(teamName, k -> new PersistentPlayerData());
            }
        }
        return serverState.players.getOrDefault(uuid, null);
    }

    private static String getPlayerTeamName(ServerPlayerEntity player) {
        return player.getScoreboardTeam() != null ? player.getScoreboardTeam().getName() : null;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        addInventoryToNbt(worldInventory, nbt, WORLD_ITEMS_NBT_KEY);
        NbtCompound playersNbt = new NbtCompound();
        players.forEach((uuid, persistentPlayerData) ->
                addInventoryToNbt(persistentPlayerData.extraInventory, playersNbt, uuid.toString()));
        nbt.put(PLAYER_ITEMS_NBT_KEY, playersNbt);

        NbtCompound teamsNbt = new NbtCompound();
        teamData.forEach((teamName, persistentPlayerData) ->
                addInventoryToNbt(persistentPlayerData.extraInventory, teamsNbt, teamName));
        nbt.put(TEAM_ITEMS_NBT_KEY, teamsNbt);

        return nbt;
    }

    public static PersistentWorldData createWorldDataFromNbt(NbtCompound nbt) {
        PersistentWorldData worldData = new PersistentWorldData();

        NbtCompound worldInventoryCompound = nbt.getCompound(WORLD_ITEMS_NBT_KEY);
        worldData.worldInventory = getInventoryFromNbt(worldInventoryCompound);

        NbtCompound playersNbt = nbt.getCompound(PLAYER_ITEMS_NBT_KEY);
        for (String playerUuid : playersNbt.getKeys()) {
            PersistentPlayerData playerData = new PersistentPlayerData();
            NbtCompound playerInventoryCompound = playersNbt.getCompound(playerUuid);
            playerData.extraInventory = getInventoryFromNbt(playerInventoryCompound);
            worldData.players.put(UUID.fromString(playerUuid), playerData);
        }

        NbtCompound teamsNbt = nbt.getCompound(TEAM_ITEMS_NBT_KEY);
        for (String teamName : teamsNbt.getKeys()) {
            NbtCompound teamInventoryCompound = teamsNbt.getCompound(teamName);
            PersistentPlayerData teamData = new PersistentPlayerData(getInventoryFromNbt(teamInventoryCompound));
            worldData.teamData.put(teamName, teamData);
        }

        return worldData;
    }

    public static PersistentWorldData getServerState(MinecraftServer server) {
        PersistentStateManager persistentStateManager = server.getOverworld().getPersistentStateManager();
        PersistentWorldData state = persistentStateManager.getOrCreate(PersistentWorldData::createWorldDataFromNbt,
                PersistentWorldData::new, TitanFabric.MODID);
        state.markDirty();
        return state;
    }

    private static void addInventoryToNbt(Inventory inventory, NbtCompound nbt, String inventoryNbtKey) {
        NbtCompound inventoryCompound = new NbtCompound();
        for (int i = 0; i < inventory.size(); i++) {
            NbtCompound stackCompound = new NbtCompound();
            ItemStack stack = inventory.getStack(i).copy();
            stack.writeNbt(stackCompound);
            inventoryCompound.put("slot:" + i, stackCompound);
        }
        nbt.put(inventoryNbtKey, inventoryCompound);
    }

    private static Inventory getInventoryFromNbt(NbtCompound nbt) {
        Inventory inventory = new SimpleInventory(PersistentPlayerData.INV_SIZE);
        for (int i = 0; i < inventory.size(); i++) {
            NbtCompound stackCompound = nbt.getCompound("slot:" + i);
            ItemStack stack = ItemStack.fromNbt(stackCompound);
            inventory.setStack(i, stack);
        }
        return inventory;
    }
}
