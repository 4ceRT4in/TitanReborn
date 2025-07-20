package net.shirojr.titanfabric.util.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.shirojr.titanfabric.data.BackPackContent;

public class BackPackSlot extends Slot {
    private final ItemStack containerHolder;

    public BackPackSlot(ItemStack containerHolder, Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
        this.containerHolder = containerHolder;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        if (ItemStack.areEqual(stack, containerHolder)) return false;
        return super.canInsert(stack);
    }

    @Override
    public boolean canTakeItems(PlayerEntity playerEntity) {
        return super.canTakeItems(playerEntity);
    }

    @Override
    public ItemStack insertStack(ItemStack stack) {
        return super.insertStack(stack);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        BackPackContent.get(containerHolder).ifPresent(content -> content.savePersistent(containerHolder));
    }
}
