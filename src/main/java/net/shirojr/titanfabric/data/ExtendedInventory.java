package net.shirojr.titanfabric.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.server.network.ServerPlayerEntity;
import net.shirojr.titanfabric.persistent.PersistentPlayerData;
import net.shirojr.titanfabric.persistent.PersistentWorldData;

import java.util.LinkedHashSet;
import java.util.List;

public record ExtendedInventory(int size, LinkedHashSet<Slot> stacks) {
    public static final PacketCodec<RegistryByteBuf, ExtendedInventory> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, ExtendedInventory::size,
            Slot.PACKET_CODEC.collect(PacketCodecs.toCollection(LinkedHashSet::new)), ExtendedInventory::stacks,
            ExtendedInventory::new
    );


    public static final Codec<ExtendedInventory> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.fieldOf("size").forGetter(ExtendedInventory::size),
                    Codec.list(Slot.CODEC).fieldOf("stacks").forGetter(inventory -> inventory.stacks.stream().toList()))
            .apply(instance, (integer, slots) -> new ExtendedInventory(integer, slots.stream().map(slot -> slot.stack).toList())));

    public ExtendedInventory(int size, List<ItemStack> stacks) {
        this(size, listToSet(stacks));
    }

    public ExtendedInventory(SimpleInventory inventory) {
        this(inventory.size(), inventory.getHeldStacks());
    }

    public ExtendedInventory(int size) {
        this(size, new LinkedHashSet<>(size));
    }

    public Inventory asInventory() {
        SimpleInventory inventory = new SimpleInventory(this.stacks.size());
        for (int i = 0; i < this.stacks.size(); i++) {
            ItemStack stack = ItemStack.EMPTY;
            for (Slot slot : this.stacks) {
                if (slot.index != i) continue;
                stack = slot.stack;
            }
            inventory.addStack(stack);
        }
        return inventory;
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

    public void savePersistent(ServerPlayerEntity player) {
        if (player == null || player.getServer() == null) return;
        PersistentPlayerData persistentPlayerData = PersistentWorldData.getPersistentPlayerData(player);
        if (persistentPlayerData == null) {
            PersistentWorldData.getServerState(player.getServer()).players.put(player.getUuid(), new PersistentPlayerData());
        }
        if (persistentPlayerData != null) {
            persistentPlayerData.extraInventory = this;
        }
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
