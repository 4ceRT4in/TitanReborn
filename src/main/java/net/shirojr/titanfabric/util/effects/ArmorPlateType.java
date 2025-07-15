package net.shirojr.titanfabric.util.effects;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;

public enum ArmorPlateType implements StringIdentifiable {
    CITRIN("citrin_armor_plating"),
    DIAMOND("diamond_armor_plating"),
    EMBER("ember_armor_plating"),
    NETHERITE("netherite_armor_plating"),
    LEGEND("legend_armor_plating");

    private final String id;

    ArmorPlateType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String asString() {
        return getId();
    }

    public static final Codec<ArmorPlateType> CODEC = StringIdentifiable.createCodec(ArmorPlateType::values);

    public static final PacketCodec<ByteBuf, ArmorPlateType> PACKET_CODEC = PacketCodecs.codec(CODEC);
}