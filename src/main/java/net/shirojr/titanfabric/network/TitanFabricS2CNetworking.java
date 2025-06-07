package net.shirojr.titanfabric.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.shirojr.titanfabric.network.packet.BackPackScreenPacket;

public class TitanFabricS2CNetworking {
    public static void initialize() {
         ClientPlayNetworking.registerGlobalReceiver(BackPackScreenPacket.IDENTIFIER, BackPackScreenPacket::handlePacket);
    }
}
