package net.shirojr.titanfabric.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.ArrayList;
import java.util.List;

public record BackPackContent(List<ItemStack> items) {

    public static final Codec<BackPackContent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.CODEC.listOf().fieldOf("items").forGetter(content ->
                    content.items().stream()
                            .filter(stack -> !stack.isEmpty())
                            .toList()
            )
    ).apply(instance, BackPackContent::new));

    public static final PacketCodec<RegistryByteBuf, BackPackContent> PACKET_CODEC =
            PacketCodec.tuple(
                    ItemStack.PACKET_CODEC.collect(PacketCodecs.toList()),
                    content -> content.items().stream()
                            .filter(stack -> !stack.isEmpty())
                            .toList(),
                    BackPackContent::new
            );

    public BackPackContent() {
        this(new ArrayList<>());
    }

    public BackPackContent(List<ItemStack> items) {
        this.items = new ArrayList<>(items);
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public ItemStack getItem(int index) {
        if (index >= 0 && index < items.size()) {
            return items.get(index);
        }
        return ItemStack.EMPTY;
    }

    public static BackPackContent empty() {
        return new BackPackContent();
    }
}