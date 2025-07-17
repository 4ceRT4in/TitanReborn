package net.shirojr.titanfabric.data;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.collection.DefaultedList;
import net.shirojr.titanfabric.init.TitanFabricDataComponents;
import net.shirojr.titanfabric.item.custom.misc.BackPackItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public record BackPackContent(List<ItemStack> items) {

    public static final Codec<BackPackContent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.CODEC.listOf().fieldOf("items").forGetter(BackPackContent::items)
    ).apply(instance, BackPackContent::new));

    public static final PacketCodec<RegistryByteBuf, BackPackContent> PACKET_CODEC =
            PacketCodec.tuple(
                    PacketCodecs.optional(ItemStack.PACKET_CODEC).collect(PacketCodecs.toList()),
                    items -> items.items().stream().map(Optional::of).toList(),
                    optionals -> new BackPackContent(
                            optionals.stream()
                                    .map(opt -> opt.orElse(ItemStack.EMPTY))
                                    .toList()
                    )
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

    public void setItem(int index, ItemStack stack) {
        while (items.size() <= index) {
            items.add(ItemStack.EMPTY);
        }
        items.set(index, stack);
    }

    public static BackPackContent empty() {
        return new BackPackContent();
    }
}