package net.shirojr.titanfabric.network.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.persistent.PersistentPlayerData;
import net.shirojr.titanfabric.persistent.PersistentWorldData;
import net.shirojr.titanfabric.screen.handler.ExtendedInventoryScreenHandler;

import java.util.List;

public record ExtendedInventoryOpenPacket(List<String> description) implements CustomPayload {
    public static final Id<ExtendedInventoryOpenPacket> IDENTIFIER =
            new Id<>(TitanFabric.getId("extended_inventory_open"));

    public static final PacketCodec<RegistryByteBuf, ExtendedInventoryOpenPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING.collect(PacketCodecs.toList()), ExtendedInventoryOpenPacket::description,
            ExtendedInventoryOpenPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return IDENTIFIER;
    }

    public void sendPacket() {
        ClientPlayNetworking.send(this);
    }

    public void handlePacket(ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        /*Text playerName = player.getDisplayName();
        if (playerName == null) return;
        List<String> description = List.of(playerName.getString());*/
        player.openHandledScreen(new ExtendedScreenHandlerFactory<>() {
            @Override
            public Object getScreenOpeningData(ServerPlayerEntity player) {
                return description;
            }

            @Override
            public Text getDisplayName() {
                return player.getDisplayName();
            }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                PersistentPlayerData persistentPlayerData = PersistentWorldData.getPersistentPlayerData(player);
                Inventory extendedInventory = new SimpleInventory(8);
                if (persistentPlayerData != null) {
                    extendedInventory = persistentPlayerData.extraInventory;
                }
                return new ExtendedInventoryScreenHandler(syncId, playerInventory, extendedInventory, description);
            }
        });
    }
}
