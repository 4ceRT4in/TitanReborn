package net.shirojr.titanfabric.network.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.Entity;
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
import net.shirojr.titanfabric.data.ExtendedInventory;
import net.shirojr.titanfabric.persistent.PersistentPlayerData;
import net.shirojr.titanfabric.persistent.PersistentWorldData;
import net.shirojr.titanfabric.screen.handler.ExtendedInventoryScreenHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record ExtendedInventoryOpenPacket(int entityId/*, ExtendedInventory inventory*/) implements CustomPayload {
    public static final Id<ExtendedInventoryOpenPacket> IDENTIFIER =
            new Id<>(TitanFabric.getId("extended_inventory_open"));

    public static final PacketCodec<RegistryByteBuf, ExtendedInventoryOpenPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, ExtendedInventoryOpenPacket::entityId,
            /*ExtendedInventory.PACKET_CODEC, ExtendedInventoryOpenPacket::inventory,*/
            ExtendedInventoryOpenPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return IDENTIFIER;
    }

    public void savePersistent(ServerPlayerEntity player, ExtendedInventory inventory) {
        PersistentPlayerData persistentPlayerData = PersistentWorldData.getPersistentPlayerData(player);
        if (persistentPlayerData != null) {
            persistentPlayerData.extraInventory = inventory;
        }
    }

    public void sendPacket() {
        ClientPlayNetworking.send(this);
    }

    @Nullable
    public ExtendedInventory getExtendedInventory(ServerWorld world) {
        if (!(world.getEntityById(this.entityId) instanceof ServerPlayerEntity player)) return null;
        PersistentPlayerData playerData = PersistentWorldData.getPersistentPlayerData(player);
        if (playerData == null) {
            PersistentWorldData serverState = PersistentWorldData.getServerState(world.getServer());
            serverState.players.put(player.getUuid(), new PersistentPlayerData());
        }
        return Optional.ofNullable(playerData).map(data -> data.extraInventory).orElse(null);
    }

    public void handlePacket(ServerPlayNetworking.Context context) {
        ServerWorld world = context.player().getServerWorld();
        Entity entity = world.getEntityById(this.entityId);
        if (!(entity instanceof ServerPlayerEntity player)) return;
        PersistentPlayerData playerData = PersistentWorldData.getPersistentPlayerData(entity);
        if (playerData == null) {
            PersistentWorldData serverState = PersistentWorldData.getServerState(context.server());
            serverState.players.put(entity.getUuid(), new PersistentPlayerData());
        }
        ExtendedInventoryOpenPacket packet = new ExtendedInventoryOpenPacket(player.getId());
        ExtendedInventory extendedInventory = packet.getExtendedInventory(world);
        if (extendedInventory == null) return;

        player.openHandledScreen(new ExtendedScreenHandlerFactory<ExtendedInventoryOpenPacket>() {
            @Override
            public ExtendedInventoryOpenPacket getScreenOpeningData(ServerPlayerEntity player) {
                PersistentPlayerData playerData = PersistentWorldData.getPersistentPlayerData(player);
                if (playerData == null) return null;
                return new ExtendedInventoryOpenPacket(player.getId());
            }

            @Override
            public Text getDisplayName() {
                return player.getDisplayName();
            }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                return new ExtendedInventoryScreenHandler(syncId, playerInventory, extendedInventory);
            }
        });
    }
}
