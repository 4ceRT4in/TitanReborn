package net.shirojr.titanfabric.cca.implementation;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.shirojr.titanfabric.TitanFabricComponents;
import org.jetbrains.annotations.Nullable;

public class GlobalExtendedInventoryImpl extends AbstractExtendedInventoryComponentImpl {
    private final Scoreboard provider;
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    @Nullable
    private final MinecraftServer server;

    public GlobalExtendedInventoryImpl(Scoreboard scoreboard, @Nullable MinecraftServer server) {
        super();
        this.provider = scoreboard;
        this.server = server;
    }

    @Override
    public Text getHeaderText() {
        return Text.literal("Global Inventory");
    }

    @Override
    public InventoryType getType() {
        return InventoryType.GLOBAL;
    }

    @Override
    public boolean shouldDropInventory() {
        return false;
    }

    @Override
    public void setDropInventory(boolean shouldDropInventory, boolean shouldSync) {
        if (shouldDropInventory) {
            throw new UnsupportedOperationException("Global Extended Inventories can't be dropped");
        }
    }

    @Override
    public void sync() {
        TitanFabricComponents.EXTENDED_INVENTORY_GLOBAL.sync(this.provider);
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return super.shouldSyncWith(player);
    }
}
