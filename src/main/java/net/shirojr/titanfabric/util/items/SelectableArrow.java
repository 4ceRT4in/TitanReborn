package net.shirojr.titanfabric.util.items;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.shirojr.titanfabric.init.TitanFabricDataComponents;
import net.shirojr.titanfabric.item.custom.misc.BackPackItem;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public interface SelectableArrow {
    List<Item> titanFabric$supportedArrows();


    /**
     * If {@link #getSelectedIndexOfBag(ItemStack)} is not null this will use the index of the stack, which is in
     * the container item's inventory
     *
     * @param weaponStack stack, which holds the selected arrow information
     * @return index of the selected arrow stack
     */
    @Nullable
    default Integer getSelectedIndex(ItemStack weaponStack) {
        return weaponStack.get(TitanFabricDataComponents.SELECTED_PROJECTILE);
    }

    default void setSelectedIndex(ItemStack weaponStack, @Nullable Integer index) {
        if (index == null) {
            weaponStack.remove(TitanFabricDataComponents.SELECTED_PROJECTILE);
            return;
        }
        weaponStack.set(TitanFabricDataComponents.SELECTED_PROJECTILE, index);
    }

    /**
     * @param weaponStack stack, which holds the selected arrow information
     * @return If not null, points to a container item, which holds the selected arrow
     */
    @Nullable
    default Integer getSelectedIndexOfBag(ItemStack weaponStack) {
        return weaponStack.get(TitanFabricDataComponents.CONTAINER_FOR_SELECTED_PROJECTILE);
    }

    default void setSelectedIndexOfBag(ItemStack weaponStack, @Nullable Integer indexOfBag) {
        if (indexOfBag == null) {
            weaponStack.remove(TitanFabricDataComponents.CONTAINER_FOR_SELECTED_PROJECTILE);
            return;
        }
        weaponStack.set(TitanFabricDataComponents.CONTAINER_FOR_SELECTED_PROJECTILE, indexOfBag);
    }

    @Nullable
    static ItemStack getSelectedArrowStack(ItemStack weaponStack, List<ItemStack> inventory) {
        if (!(weaponStack.getItem() instanceof SelectableArrow arrowSelector)) return null;
        Integer selectedIndex = arrowSelector.getSelectedIndex(weaponStack);
        if (selectedIndex == null) return null;
        Integer selectedIndexOfBag = arrowSelector.getSelectedIndexOfBag(weaponStack);
        if (selectedIndexOfBag == null) {
            if (selectedIndex >= inventory.size() - 1) return null;
            return inventory.get(selectedIndex);
        }
        ItemStack bagStack = inventory.get(selectedIndexOfBag);
        if (!(bagStack.getItem() instanceof BackPackItem bagItem)) return null;
        if (!bagItem.getBackpackType().equals(BackPackItem.Type.POTION)) return null;
        Inventory bagInventory = BackPackItem.getInventoryFromComponents(bagStack, BackPackItem.Type.POTION);
        if (selectedIndex >= bagInventory.size()) return null;
        return bagInventory.getStack(selectedIndex);
    }

    static boolean applySelectedArrowStack(ItemStack weaponStack, @Nullable Index index) {
        if (!(weaponStack.getItem() instanceof SelectableArrow arrowSelector)) return false;
        arrowSelector.setSelectedIndex(weaponStack, Optional.ofNullable(index).map(entry -> entry.index).orElse(null));
        arrowSelector.setSelectedIndexOfBag(weaponStack, Optional.ofNullable(index).map(entry -> entry.indexOfBag).orElse(null));
        return true;
    }

    record Index(int index, @Nullable Integer indexOfBag) {
        public Index(int index) {
            this(index, null);
        }

        public static Optional<Index> get(List<ItemStack> inventory, ItemStack searchStack) {
            Index result = null;
            for (int inventoryIndex = 0; inventoryIndex < inventory.size(); inventoryIndex++) {
                ItemStack stack = inventory.get(inventoryIndex);
                if (result == null && stack.equals(searchStack)) {
                    result = new Index(inventoryIndex);
                }
                if (!(stack.getItem() instanceof BackPackItem bagItem)) continue;
                if (!bagItem.getBackpackType().equals(BackPackItem.Type.POTION)) continue;
                Inventory bagInventory = BackPackItem.getInventoryFromComponents(stack, BackPackItem.Type.POTION);
                for (int indexInBag = 0; indexInBag < bagInventory.size(); indexInBag++) {
                    ItemStack stackInBag = bagInventory.getStack(indexInBag);
                    if (stackInBag.equals(searchStack)) {
                        return Optional.of(new Index(indexInBag, inventoryIndex));
                    }
                }
            }
            return Optional.ofNullable(result);
        }
    }
}
