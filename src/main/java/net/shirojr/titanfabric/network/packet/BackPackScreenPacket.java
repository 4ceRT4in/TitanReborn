package net.shirojr.titanfabric.network.packet;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.shirojr.titanfabric.network.NetworkingIdentifiers;

public record BackPackScreenPacket(int entityNetworkId) implements CustomPayload {
    public static final Id<BackPackScreenPacket> IDENTIFIER = new Id<>(NetworkingIdentifiers.BACKPACK_INVENTORY_OPEN);
    public static final PacketCodec<RegistryByteBuf, BackPackScreenPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, BackPackScreenPacket::entityNetworkId,
            BackPackScreenPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return IDENTIFIER;
    }
}
