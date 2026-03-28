package net.shirojr.titanfabric.network.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.shirojr.titanfabric.TitanFabric;

public record DisableSwimmingPacket(boolean enabled) implements CustomPayload {
    private static volatile boolean clientEnabled = true;

    public static final Id<DisableSwimmingPacket> IDENTIFIER =
            new Id<>(TitanFabric.getId("disable_swimming_sync"));

    public static final PacketCodec<RegistryByteBuf, DisableSwimmingPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOL, DisableSwimmingPacket::enabled,
            DisableSwimmingPacket::new
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

    public static void handlePacket(DisableSwimmingPacket packet, ClientPlayNetworking.Context context) {
        context.client().execute(() -> clientEnabled = packet.enabled());
    }
}
