package net.shirojr.titanfabric.persistent;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
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
    private static final int INV_SIZE = 8;
    private final Inventory worldInventory = new SimpleInventory(INV_SIZE);
    public HashMap<UUID, PersistentPlayerData> players = new HashMap<>();
    public static final String WORLD_ITEMS_NBT_KEY = TitanFabric.MODID + ".worldInventory";
    public static final String PLAYER_ITEMS_NBT_KEY = TitanFabric.MODID + ".playerInventory";


    @Nullable
    public static PersistentPlayerData getPersistentPlayerData(LivingEntity player) {
        if (player.getWorld().getServer() == null) {
            LoggerUtil.devLogger("given player LivingEntity doesn't supply a server", true, null);
            return null;
        }
        PersistentWorldData serverState = getServerState(player.getWorld().getServer());

        return serverState.players.computeIfAbsent(player.getUuid(), uuid -> new PersistentPlayerData());
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        addInventoryToNbt(worldInventory, nbt, WORLD_ITEMS_NBT_KEY);

        NbtCompound playersNbt = new NbtCompound();
        players.forEach((uuid, persistentPlayerData) -> {
            NbtCompound playerNbt = new NbtCompound();
            addInventoryToNbt(persistentPlayerData.playerInventory, playerNbt, PLAYER_ITEMS_NBT_KEY);
            playersNbt.put(uuid.toString(), playerNbt);
        });
        nbt.put("players", playersNbt);

        return nbt;
    }

    public static PersistentWorldData createWorldDataFromNbt(NbtCompound nbt) {
        PersistentWorldData worldData = new PersistentWorldData();
        NbtList itemsNbtList = nbt.getList(WORLD_ITEMS_NBT_KEY, 10);

        for (int i = 0; i < itemsNbtList.size(); i++) {
            ItemStack stack = ItemStack.fromNbt(itemsNbtList.getCompound(i));
            worldData.worldInventory.setStack(i, stack);
        }

        NbtCompound playersNbt = nbt.getCompound("players");
        for (String playerUuid : playersNbt.getKeys()) {
            PersistentPlayerData playerData = new PersistentPlayerData();
            NbtList playerInventoryNbtList = playersNbt.getCompound(playerUuid).getList(PLAYER_ITEMS_NBT_KEY, 10); //TODO: check if problematic logic when testing
            for (int i = 0; i < playerInventoryNbtList.size(); i++) {
                ItemStack playerInventoryItemStack = ItemStack.fromNbt(playerInventoryNbtList.getCompound(i));
                playerData.playerInventory.setStack(i, playerInventoryItemStack);
            }
            worldData.players.put(UUID.fromString(playerUuid), playerData);
        }

        return worldData;
    }

    @SuppressWarnings("DataFlowIssue")
    public static PersistentWorldData getServerState(MinecraftServer server) {
        PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();
        PersistentWorldData state = persistentStateManager.getOrCreate(PersistentWorldData::createWorldDataFromNbt, PersistentWorldData::new, TitanFabric.MODID);
        state.markDirty();
        return state;
    }

    private static void addInventoryToNbt(Inventory inventory, NbtCompound nbt, String inventoryNbtKey) {
        NbtList nbtList = nbt.getList(inventoryNbtKey, 10);
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i).copy();
            NbtCompound nbtCompound = new NbtCompound();

            stack.writeNbt(nbtCompound);
            nbtList.add(nbtCompound);
        }
    }
}
