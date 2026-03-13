package net.shirojr.titanfabric.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.shirojr.titanfabric.network.packet.ArmorDamageTiltFixPacket;
import net.shirojr.titanfabric.network.packet.ArmorHudOverlayPacket;
import net.shirojr.titanfabric.network.packet.BackPackScreenPacket;
import net.shirojr.titanfabric.network.packet.DisableSwimmingPacket;
import net.shirojr.titanfabric.network.packet.ImmunityBlockedEffectPacket;

public class TitanFabricS2CNetworking {
    public static void initialize() {
        ClientPlayNetworking.registerGlobalReceiver(BackPackScreenPacket.IDENTIFIER, BackPackScreenPacket::handlePacket);
        ClientPlayNetworking.registerGlobalReceiver(ArmorDamageTiltFixPacket.IDENTIFIER, ArmorDamageTiltFixPacket::handlePacket);
        ClientPlayNetworking.registerGlobalReceiver(ArmorHudOverlayPacket.IDENTIFIER, ArmorHudOverlayPacket::handlePacket);
        ClientPlayNetworking.registerGlobalReceiver(DisableSwimmingPacket.IDENTIFIER, DisableSwimmingPacket::handlePacket);
        ClientPlayNetworking.registerGlobalReceiver(ImmunityBlockedEffectPacket.IDENTIFIER, ImmunityBlockedEffectPacket::handlePacket);
    }
}
