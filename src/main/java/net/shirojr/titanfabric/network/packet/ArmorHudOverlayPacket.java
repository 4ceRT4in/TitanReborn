package net.shirojr.titanfabric.network.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.shirojr.titanfabric.TitanFabric;

public record ArmorHudOverlayPacket(boolean enabled) implements CustomPayload {
    private static volatile boolean clientEnabled = true;

    public static final Id<ArmorHudOverlayPacket> IDENTIFIER =
            new Id<>(TitanFabric.getId("armor_hud_overlay_sync"));

    public static final PacketCodec<RegistryByteBuf, ArmorHudOverlayPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOL, ArmorHudOverlayPacket::enabled,
            ArmorHudOverlayPacket::new
    );

    public void sendPacket(ServerPlayerEntity target) {
        ServerPlayNetworking.send(target, this);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return IDENTIFIER;
    }

    public static boolean isClientEnabled() {
        return clientEnabled;
    }

    public static void handlePacket(ArmorHudOverlayPacket packet, ClientPlayNetworking.Context context) {
        context.client().execute(() -> clientEnabled = packet.enabled());
    }
}
