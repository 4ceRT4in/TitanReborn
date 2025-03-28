package net.shirojr.titanfabric.network.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.item.custom.armor.LegendArmorItem;

public record BowScreenPacket(ItemStack selectedStack) implements CustomPayload {
    public static final Id<BowScreenPacket> IDENTIFIER =
            new Id<>(TitanFabric.getId("armor_life"));

    public static final PacketCodec<RegistryByteBuf, BowScreenPacket> CODEC = PacketCodec.tuple(
            ItemStack.PACKET_CODEC, BowScreenPacket::selectedStack,
            BowScreenPacket::new
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
        player.sendMessage(Text.literal("Arrow Packet arrived on server side"), false);
        player.sendMessage(Text.literal("Selected Arrow: " + selectedStack), false);
    }
}
