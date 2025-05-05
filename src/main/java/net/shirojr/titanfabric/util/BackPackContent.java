package net.shirojr.titanfabric.util;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.shirojr.titanfabric.item.custom.misc.BackPackItem;

import java.util.List;
import java.util.stream.Stream;

public record BackPackContent(List<ItemStack> stacks, BackPackItem.Type type) implements TooltipData {
    public static final BackPackContent DEFAULT = new BackPackContent(List.of(), BackPackItem.Type.SMALL);

    public static final PacketCodec<RegistryByteBuf, BackPackContent> PACKET_CODEC = PacketCodec.tuple(
            ItemStack.PACKET_CODEC.collect(PacketCodecs.toList()), BackPackContent::stacks,
            PacketCodecs.BYTE.xmap(index -> BackPackItem.Type.values()[index], backPackType -> (byte) backPackType.ordinal()),
            BackPackContent::new
    );

    public static final Codec<BackPackContent> CODEC = RecordCodecBuilder.<BackPackContent>create(instance -> instance.group(
            ItemStack.CODEC.listOf().fieldOf("stacks").forGetter(BackPackContent::stacks),
            BackPackItem.Type.
    ));

        ItemStack.CODEC.listOf().xmap(BackPackContent::new, component -> component.stacks);


    public ItemStack get(int index) {
        return this.stacks.get(index);
    }

    public Stream<ItemStack> stream() {
        return this.stacks.stream().map(ItemStack::copy);
    }

    public Iterable<ItemStack> iterate() {
        return this.stacks;
    }

    public Iterable<ItemStack> iterateCopy() {
        return Lists.<ItemStack, ItemStack>transform(this.stacks, ItemStack::copy);
    }

    public int size() {
        return this.stacks.size();
    }

    public boolean isEmpty() {
        return this.stacks.isEmpty();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BackPackContent component)) return false;
        for (int i = 0; i < component.size(); i++) {
            ItemStack left = this.stacks.get(i);
            ItemStack right = component.stacks.get(i);
            if (!ItemStack.areEqual(left, right)) return false;
        }
        return true;
    }
}
