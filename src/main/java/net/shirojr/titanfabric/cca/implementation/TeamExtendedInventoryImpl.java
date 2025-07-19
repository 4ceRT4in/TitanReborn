package net.shirojr.titanfabric.cca.implementation;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.shirojr.titanfabric.TitanFabricComponents;
import org.jetbrains.annotations.Nullable;

public class TeamExtendedInventoryImpl extends AbstractExtendedInventoryComponentImpl {
    private final Team provider;
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final Scoreboard scoreboard;
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    @Nullable
    private final MinecraftServer server;

    public TeamExtendedInventoryImpl(Team provider, Scoreboard scoreboard, @Nullable MinecraftServer server) {
        super();
        this.provider = provider;
        this.scoreboard = scoreboard;
        this.server = server;
    }

    @Override
    public Text getHeaderText() {
        return provider.getDisplayName();
    }

    @Override
    public InventoryType getType() {
        return InventoryType.TEAM;
    }

    @Override
    public void sync() {
        TitanFabricComponents.EXTENDED_INVENTORY_TEAM.sync(this.provider);
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        if (super.shouldSyncWith(player)) return true;
        if (player.getScoreboardTeam() == null) return false;
        return player.getScoreboardTeam().equals(this.provider);
    }
}
