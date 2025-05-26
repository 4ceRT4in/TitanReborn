package net.shirojr.titanfabric.persistent;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.data.ExtendedInventory;
import net.shirojr.titanfabric.util.LoggerUtil;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class PersistentWorldData extends PersistentState {
    private ExtendedInventory worldInventory = new ExtendedInventory(PersistentPlayerData.INV_SIZE);
    public HashMap<UUID, PersistentPlayerData> players = new HashMap<>();
    public HashMap<String, PersistentPlayerData> teamData = new HashMap<>();
    public static final String WORLD_ITEMS_NBT_KEY = TitanFabric.MOD_ID + ".worldInventory";
    public static final String PLAYER_ITEMS_NBT_KEY = TitanFabric.MOD_ID + ".playerInventory";
    public static final String TEAM_ITEMS_NBT_KEY = TitanFabric.MOD_ID + ".teamInventory";

    private static final Type<PersistentWorldData> type = new Type<>(
            PersistentWorldData::new,
            PersistentWorldData::fromNbt,
            null
    );

    @Nullable
    public static PersistentPlayerData getPersistentPlayerData(LivingEntity entity) {
        if (entity.getWorld().getServer() == null) {
            LoggerUtil.devLogger("given LivingEntity doesn't supply a server", true, null);
            return null;
        }
        PersistentWorldData serverState = getServerState(entity.getWorld().getServer());

        if (entity instanceof ServerPlayerEntity player) {
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
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound worldNbt = new NbtCompound();
        addInventoryToNbt(worldInventory, worldNbt);
        nbt.put(WORLD_ITEMS_NBT_KEY, worldNbt);
        NbtCompound playersNbt = new NbtCompound();
        players.forEach((uuid, persistentPlayerData) ->
                addInventoryToNbt(persistentPlayerData.extraInventory, playersNbt));
        nbt.put(PLAYER_ITEMS_NBT_KEY, playersNbt);

        NbtCompound teamsNbt = new NbtCompound();
        teamData.forEach((teamName, persistentPlayerData) ->
                addInventoryToNbt(persistentPlayerData.extraInventory, teamsNbt));
        nbt.put(TEAM_ITEMS_NBT_KEY, teamsNbt);

        return nbt;
    }

    public static PersistentWorldData fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        PersistentWorldData worldData = new PersistentWorldData();

        NbtCompound worldInventoryCompound = nbt.getCompound(WORLD_ITEMS_NBT_KEY);
        worldData.worldInventory = getInventoryFromNbt(worldInventoryCompound, lookup)
                .orElse(new ExtendedInventory(PersistentPlayerData.INV_SIZE));

        NbtCompound playersNbt = nbt.getCompound(PLAYER_ITEMS_NBT_KEY);
        for (String playerUuid : playersNbt.getKeys()) {
            PersistentPlayerData playerData = new PersistentPlayerData();
            NbtCompound playerInventoryCompound = playersNbt.getCompound(playerUuid);
            playerData.extraInventory = getInventoryFromNbt(playerInventoryCompound, lookup)
                    .orElse(new ExtendedInventory(PersistentPlayerData.INV_SIZE));
            worldData.players.put(UUID.fromString(playerUuid), playerData);
        }

        NbtCompound teamsNbt = nbt.getCompound(TEAM_ITEMS_NBT_KEY);
        for (String teamName : teamsNbt.getKeys()) {
            NbtCompound teamInventoryCompound = teamsNbt.getCompound(teamName);
            PersistentPlayerData teamData = new PersistentPlayerData(getInventoryFromNbt(teamInventoryCompound, lookup)
                    .orElse(new ExtendedInventory(PersistentPlayerData.INV_SIZE)));
            worldData.teamData.put(teamName, teamData);
        }

        return worldData;
    }

    public static PersistentWorldData getServerState(MinecraftServer server) {
        PersistentStateManager manager = server.getOverworld().getPersistentStateManager();
        PersistentWorldData state = manager.getOrCreate(type, TitanFabric.MOD_ID);
        state.markDirty();
        return state;
    }

    private static void addInventoryToNbt(ExtendedInventory inventory, NbtCompound nbt) {
        ExtendedInventory.CODEC.encodeStart(NbtOps.INSTANCE, inventory).result()
                .ifPresent(nbtElement -> nbt.put("extended_inventory", nbtElement));
    }

    private static Optional<ExtendedInventory> getInventoryFromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        return ExtendedInventory.CODEC.parse(NbtOps.INSTANCE, nbt).result();
    }
}
