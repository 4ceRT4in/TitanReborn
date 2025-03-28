package net.shirojr.titanfabric.init;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.shirojr.titanfabric.network.packet.ArmorLifePacket;
import net.shirojr.titanfabric.network.packet.ArrowSelectionPacket;
import net.shirojr.titanfabric.network.packet.BowScreenPacket;
import net.shirojr.titanfabric.network.packet.ExtendedInventoryOpenPacket;

public class TitanFabricNetworkingPayloads {
    static {
        registerC2S(ArmorLifePacket.IDENTIFIER, ArmorLifePacket.CODEC);
        registerC2S(ArrowSelectionPacket.IDENTIFIER, ArrowSelectionPacket.CODEC);
        registerC2S(BowScreenPacket.IDENTIFIER, BowScreenPacket.CODEC);
        registerC2S(ExtendedInventoryOpenPacket.IDENTIFIER, ExtendedInventoryOpenPacket.CODEC);
    }

    private static <T extends CustomPayload> void registerS2C(CustomPayload.Id<T> packetIdentifier, PacketCodec<RegistryByteBuf, T> codec) {
        PayloadTypeRegistry.playS2C().register(packetIdentifier, codec);
    }

    private static <T extends CustomPayload> void registerC2S(CustomPayload.Id<T> packetIdentifier, PacketCodec<RegistryByteBuf, T> codec) {
        PayloadTypeRegistry.playC2S().register(packetIdentifier, codec);
    }


    public static void initialize() {
        // static initialisation
    }
}
