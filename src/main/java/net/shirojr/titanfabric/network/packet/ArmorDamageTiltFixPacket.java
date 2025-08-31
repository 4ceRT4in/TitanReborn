package net.shirojr.titanfabric.network.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.util.DamageTiltAvoider;

import java.util.Optional;

public record ArmorDamageTiltFixPacket(Optional<ItemStack> bufferStack) implements CustomPayload {
    public ArmorDamageTiltFixPacket(ItemStack bufferStack) {
        this(Optional.ofNullable(bufferStack.isEmpty() ? null : bufferStack));
    }

    public static final Id<ArmorDamageTiltFixPacket> IDENTIFIER =
            new Id<>(TitanFabric.getId("armor_damage_tilt"));

    public static final PacketCodec<RegistryByteBuf, ArmorDamageTiltFixPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.optional(ItemStack.PACKET_CODEC), ArmorDamageTiltFixPacket::bufferStack,
            ArmorDamageTiltFixPacket::new
    );

    public void sendPacket(ServerPlayerEntity target) {
        ServerPlayNetworking.send(target, this);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return IDENTIFIER;
    }

    public static void handlePacket(ArmorDamageTiltFixPacket packet, ClientPlayNetworking.Context context) {
        if (!(context.player() instanceof DamageTiltAvoider tilter)) return;
        tilter.titanReborn$setArmorStackBuffer(packet.bufferStack.orElse(null));
    }
}
