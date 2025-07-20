package net.shirojr.titanfabric.cca.implementation;

import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.shirojr.titanfabric.cca.component.ExtendedInventoryComponent;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

import java.util.Locale;
import java.util.function.Consumer;

public abstract class AbstractExtendedInventoryComponentImpl implements ExtendedInventoryComponent, AutoSyncedComponent {
    private final SimpleInventory inventory;
    private boolean dropInventory;

    public AbstractExtendedInventoryComponentImpl() {
        this.inventory = new SimpleInventory(getType().getSize());
        this.dropInventory = false;
    }

    @Override
    public SimpleInventory getInventory() {
        return inventory;
    }

    @Override
    public void modifyInventory(Consumer<SimpleInventory> consumer, boolean shouldSync) {
        consumer.accept(this.inventory);
        if (!shouldSync) return;
        this.sync();
    }

    @Override
    public boolean shouldDropInventory() {
        return this.dropInventory;
    }

    @Override
    public void setDropInventory(boolean shouldDropInventory, boolean shouldSync) {
        this.dropInventory = shouldDropInventory;
        if (!shouldSync) return;
        this.sync();
    }

    @SuppressWarnings("unused")
    public boolean hasAccess(@Nullable ServerPlayerEntity player) {
        if (player == null) return false;
        return this.shouldSyncWith(player);
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return player.hasPermissionLevel(2);
    }

    @Override
    public void readFromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound inventoryNbt = nbt.getCompound("inventory" + StringUtils.upperCase(getType().name().toLowerCase(Locale.ROOT)));
        Inventories.readNbt(inventoryNbt, inventory.getHeldStacks(), registryLookup);
        if (inventoryNbt.contains("dropItemsOnDeath")) {
            setDropInventory(inventoryNbt.getBoolean("dropItemsOnDeath"), true);
        }
    }

    @Override
    public void writeToNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound inventoryNbt = new NbtCompound();
        Inventories.writeNbt(inventoryNbt, this.inventory.getHeldStacks(), registryLookup);
        inventoryNbt.putBoolean("dropItemsOnDeath", shouldDropInventory());
        nbt.put("inventory" + StringUtils.upperCase(getType().name().toLowerCase(Locale.ROOT)), inventoryNbt);
    }
}
