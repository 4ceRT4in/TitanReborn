package net.shirojr.titanfabric.network.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.shirojr.titanfabric.TitanFabric;

public record BackPackScreenPacket(ItemStack backPackStack) implements CustomPayload {
    public static final Id<BackPackScreenPacket> IDENTIFIER = new Id<>(TitanFabric.getId("backpack_inventory_open"));
    public static final PacketCodec<RegistryByteBuf, BackPackScreenPacket> CODEC = PacketCodec.tuple(
            ItemStack.PACKET_CODEC, BackPackScreenPacket::backPackStack,
            BackPackScreenPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return IDENTIFIER;
    }

    @SuppressWarnings("unused")
    public void handlePacket(ClientPlayNetworking.Context context) {
        TitanFabric.LOGGER.info("backpack screen packet arrived on client");
    }
}
