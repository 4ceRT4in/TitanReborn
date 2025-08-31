package net.shirojr.titanfabric.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.shirojr.titanfabric.network.packet.ArrowSelectionPacket;
import net.shirojr.titanfabric.network.packet.BowScreenPacket;
import net.shirojr.titanfabric.network.packet.ExtendedInventoryOpenPacket;

public class TitanFabricC2SNetworking {
    public static void initialize() {
        ServerPlayNetworking.registerGlobalReceiver(BowScreenPacket.IDENTIFIER, BowScreenPacket::handlePacket);
        ServerPlayNetworking.registerGlobalReceiver(ExtendedInventoryOpenPacket.IDENTIFIER, ExtendedInventoryOpenPacket::handlePacket);
        ServerPlayNetworking.registerGlobalReceiver(ArrowSelectionPacket.IDENTIFIER, ArrowSelectionPacket::handlePacket);
    }
}
