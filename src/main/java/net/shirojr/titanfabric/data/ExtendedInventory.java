package net.shirojr.titanfabric.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public record ExtendedInventory(LinkedHashSet<Slot> stacks) {
    public static final PacketCodec<RegistryByteBuf, ExtendedInventory> PACKET_CODEC =
            Slot.PACKET_CODEC.collect(PacketCodecs.toCollection(LinkedHashSet::new)).xmap(
                    ExtendedInventory::new,
                    extendedInventory -> new LinkedHashSet<>(extendedInventory.stacks)
            );

    public static final Codec<ExtendedInventory> CODEC = Codec.list(Slot.CODEC).xmap(
            slots -> new ExtendedInventory(new LinkedHashSet<>(slots)),
            extendedInventory -> new ArrayList<>(extendedInventory.stacks)
    );

    public ExtendedInventory(List<ItemStack> stacks) {
        this(listToSet(stacks));
    }

    public ExtendedInventory(SimpleInventory inventory) {
        this(inventory.getHeldStacks());
    }

    private static LinkedHashSet<Slot> listToSet(List<ItemStack> stacks) {
        LinkedHashSet<Slot> slots = new LinkedHashSet<>();
        for (int i = 0; i < stacks.size(); i++) {
            ItemStack entry = stacks.get(i);
            if (entry.isEmpty()) continue;
            slots.add(new Slot(i, entry));
        }
        return slots;
    }

    public record Slot(int index, ItemStack stack) {
        public static final PacketCodec<RegistryByteBuf, Slot> PACKET_CODEC = PacketCodec.tuple(
                PacketCodecs.VAR_INT, Slot::index,
                ItemStack.PACKET_CODEC, Slot::stack,
                Slot::new
        );
        public static final Codec<Slot> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("slot").forGetter(Slot::index),
                ItemStack.CODEC.fieldOf("stack").forGetter(Slot::stack)
        ).apply(instance, Slot::new));
    }
}
