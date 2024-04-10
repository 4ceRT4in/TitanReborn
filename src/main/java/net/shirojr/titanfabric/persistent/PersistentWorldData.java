package net.shirojr.titanfabric.persistent;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
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
    public static final String WORLD_ITEMS_NBT_KEY = TitanFabric.MODID + ".worldInventory";
    public static final String PLAYER_ITEMS_NBT_KEY = TitanFabric.MODID + ".playerInventory";


    @Nullable
    public static PersistentPlayerData getPersistentPlayerData(LivingEntity entity) {
        if (entity.getWorld().getServer() == null) {
            LoggerUtil.devLogger("given LivingEntity doesn't supply a server", true, null);
            return null;
        }
        PersistentWorldData serverState = getServerState(entity.getWorld().getServer());
        return serverState.players.computeIfAbsent(entity.getUuid(), uuid -> new PersistentPlayerData());
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        addInventoryToNbt(worldInventory, nbt, WORLD_ITEMS_NBT_KEY);
        NbtCompound playersNbt = new NbtCompound();
        players.forEach((uuid, persistentPlayerData) ->
                addInventoryToNbt(persistentPlayerData.extraInventory, playersNbt, uuid.toString()));
        nbt.put(PLAYER_ITEMS_NBT_KEY, playersNbt);
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
        return worldData;
    }

    @SuppressWarnings("DataFlowIssue")
    public static PersistentWorldData getServerState(MinecraftServer server) {
        PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();
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
