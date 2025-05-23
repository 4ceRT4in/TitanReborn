package net.shirojr.titanfabric.network.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.persistent.PersistentPlayerData;
import net.shirojr.titanfabric.persistent.PersistentWorldData;
import net.shirojr.titanfabric.screen.handler.ExtendedInventoryScreenHandler;

import java.util.List;

public record ExtendedInventoryOpenPacket(List<String> description, int targetEntityId) implements CustomPayload {
    public static final Id<ExtendedInventoryOpenPacket> IDENTIFIER =
            new Id<>(TitanFabric.getId("extended_inventory_open"));

    public static final PacketCodec<RegistryByteBuf, ExtendedInventoryOpenPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING.collect(PacketCodecs.toList()), ExtendedInventoryOpenPacket::description,
            PacketCodecs.VAR_INT, ExtendedInventoryOpenPacket::targetEntityId,
            ExtendedInventoryOpenPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return IDENTIFIER;
    }

    public void savePersistent(ServerPlayerEntity player) {
        PersistentPlayerData persistentPlayerData = PersistentWorldData.getPersistentPlayerData(player);
        if (persistentPlayerData != null) {
            persistentPlayerData.extraInventory = this.getInventory();
        }
    }

    public void sendPacket() {
        ClientPlayNetworking.send(this);
    }

    public void handlePacket(ServerPlayNetworking.Context context) {
        ServerWorld world = context.player().getServerWorld();
        if (!(world.getEntityById(this.targetEntityId) instanceof ServerPlayerEntity player)) return;
        Text playerName = player.getDisplayName();
        if (playerName == null) return;
        List<String> description = List.of(playerName.getString());
        PersistentPlayerData playerData = PersistentWorldData.getPersistentPlayerData(player);
        if (playerData == null) {
            PersistentWorldData serverState = PersistentWorldData.getServerState(context.server());
            serverState.players.put(player.getUuid(), new PersistentPlayerData());
        }

        player.openHandledScreen(new ExtendedScreenHandlerFactory<ExtendedInventoryOpenPacket>() {
            @Override
            public ExtendedInventoryOpenPacket getScreenOpeningData(ServerPlayerEntity player) {
                PersistentPlayerData playerData = PersistentWorldData.getPersistentPlayerData(player);
                if (playerData == null) return null;
                return new ExtendedInventoryOpenPacket(description, ExtendedInventoryOpenPacket.this.targetEntityId);
            }

            @Override
            public Text getDisplayName() {
                return player.getDisplayName();
            }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                PersistentPlayerData persistentPlayerData = PersistentWorldData.getPersistentPlayerData(player);
                if (persistentPlayerData == null) return null;
                ExtendedInventoryOpenPacket packet = new ExtendedInventoryOpenPacket(List.of(), ExtendedInventoryOpenPacket.this.targetEntityId);
                return new ExtendedInventoryScreenHandler(syncId, playerInventory, packet);
            }
        });
    }
}
