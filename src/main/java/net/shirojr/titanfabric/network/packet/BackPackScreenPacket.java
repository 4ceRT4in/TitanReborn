package net.shirojr.titanfabric.network.packet;

import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.shirojr.titanfabric.network.NetworkingIdentifiers;

public record BackPackScreenPacket(ItemStack backPackStack) implements CustomPayload {
    public static final Id<BackPackScreenPacket> IDENTIFIER = new Id<>(NetworkingIdentifiers.BACKPACK_INVENTORY_OPEN);
    public static final PacketCodec<RegistryByteBuf, BackPackScreenPacket> CODEC = PacketCodec.tuple(
            ItemStack.PACKET_CODEC, BackPackScreenPacket::backPackStack,
            BackPackScreenPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return IDENTIFIER;
    }
}
