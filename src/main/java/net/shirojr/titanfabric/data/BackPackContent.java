package net.shirojr.titanfabric.data;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
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

public record BackPackContent(SimpleInventory inventory, BackPackItem.Type type) {
    public static final PacketCodec<RegistryByteBuf, BackPackContent> PACKET_CODEC = PacketCodec.tuple(
            ItemStack.PACKET_CODEC.collect(PacketCodecs.toList()), BackPackContent::getStacks,
            PacketCodecs.BYTE.xmap(index -> BackPackItem.Type.values()[index], backPackType -> (byte) backPackType.ordinal()), BackPackContent::type,
            BackPackContent::new
    );

    public BackPackContent(List<ItemStack> stacks, BackPackItem.Type type) {
        this(toInventory(stacks, type), type);
    }

    public static BackPackContent getDefault(BackPackItem.Type type) {
        return new BackPackContent(new ArrayList<>(), type);
    }

    public static final Codec<BackPackContent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.CODEC.listOf().fieldOf("stacks").forGetter(BackPackContent::getStacks),
            BackPackItem.Type.CODEC.fieldOf("type").forGetter(BackPackContent::type)
    ).apply(instance, BackPackContent::new));

    public ItemStack get(int index) {
        return this.inventory.getStack(index);
    }

    public DefaultedList<ItemStack> getStacks() {
        return this.inventory.getHeldStacks();
    }

    public Stream<ItemStack> stream() {
        return this.inventory.getHeldStacks().stream().map(ItemStack::copy);
    }

    public Iterator<ItemStack> iterate() {
        return this.inventory.getHeldStacks().iterator();
    }

    public Iterable<ItemStack> iterateCopy() {
        return Lists.transform(this.inventory.getHeldStacks(), ItemStack::copy);
    }

    public int size() {
        return this.inventory.size();
    }

    public boolean isEmpty() {
        return this.inventory.isEmpty();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BackPackContent component)) return false;
        if (!component.type.equals(this.type)) return false;
        for (int i = 0; i < component.size(); i++) {
            ItemStack left = this.getStacks().get(i);
            ItemStack right = component.getStacks().get(i);
            if (!ItemStack.areEqual(left, right)) return false;
        }
        return true;
    }

    public static Optional<BackPackContent> get(ItemStack stack) {
        return Optional.ofNullable(stack.get(TitanFabricDataComponents.BACKPACK_CONTENT));
    }

    public static BackPackContent getOrThrow(ItemStack stack) {
        return get(stack).orElseThrow(() -> new NullPointerException("Backpack has no valid content data"));
    }

    public void savePersistent(ItemStack bundleStack) {
        if (bundleStack.contains(TitanFabricDataComponents.BACKPACK_CONTENT)) {
            bundleStack.set(TitanFabricDataComponents.BACKPACK_CONTENT, this);
        }
    }

    // ------------------- [ util ] -------------------

    private static SimpleInventory toInventory(List<ItemStack> stacks, BackPackItem.Type type) {
        SimpleInventory inventory = new SimpleInventory(type.getSize());
        for (ItemStack stack : stacks) {
            inventory.addStack(stack);
        }
        return inventory;
    }
}
