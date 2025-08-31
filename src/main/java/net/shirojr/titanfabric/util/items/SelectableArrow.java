package net.shirojr.titanfabric.util.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.shirojr.titanfabric.init.TitanFabricDataComponents;
import net.shirojr.titanfabric.init.TitanFabricGamerules;
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
    static ItemStack getSelectedArrowStack(ItemStack weaponStack, PlayerEntity player) {
        if (!(weaponStack.getItem() instanceof SelectableArrow arrowSelector)) return null;
        Integer selectedIndex = arrowSelector.getSelectedIndex(weaponStack);
        if (selectedIndex == null) return null;

        // gamerule doesn't seem to be synced over to the client side automatically.
        // I can't be bothered to write a client cache for that right now so we just allow it always.
        // It's only for displaying anyway... - ShiroJR
        boolean checkBagsInInventory = player.getWorld().isClient() || player.getWorld().getGameRules().getBoolean(TitanFabricGamerules.FULL_INVENTORY_POTION_BAG_SEARCH);

        Integer selectedIndexOfBag = arrowSelector.getSelectedIndexOfBag(weaponStack);
        ItemStack bagStack = null;
        DefaultedList<ItemStack> playerInventory = player.getInventory().main;

        if (selectedIndexOfBag == null) {
            if (selectedIndex >= playerInventory.size() - 1) return null;
            return playerInventory.get(selectedIndex);
        } else if (selectedIndexOfBag.equals(-1)) {
            bagStack = player.getOffHandStack();
        } else if (checkBagsInInventory) {
            bagStack = playerInventory.get(selectedIndexOfBag);
        }
        if (bagStack == null || !(bagStack.getItem() instanceof BackPackItem bagItem)) return null;
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

    /**
     * @param index      index of the ItemStack in an inventory or in a bag's inventory
     * @param indexOfBag if null, ItemStack is located in a normal inventory, if -1,
     *                   ItemStack is located in a bag which is being held in the offhand slot,
     *                   otherwise the index of the bag, which holds the ItemStack in a normal inventory
     */
    @SuppressWarnings("unused")
    record Index(int index, @Nullable Integer indexOfBag) {
        public Index(int index) {
            this(index, null);
        }

        public boolean isInBag() {
            return indexOfBag != null;
        }

        public boolean isInOffhandBag() {
            return indexOfBag != null && indexOfBag == -1;
        }

        public static Optional<Index> get(PlayerEntity player, ItemStack searchStack) {
            Index result = null;
            boolean checkBagsInInventory = player.getWorld().getGameRules().getBoolean(TitanFabricGamerules.FULL_INVENTORY_POTION_BAG_SEARCH);

            if (!checkBagsInInventory) {
                ItemStack offhandStack = player.getOffHandStack();
                if (offhandStack.getItem() instanceof BackPackItem bagItem && bagItem.getBackpackType().equals(BackPackItem.Type.POTION)) {
                    Inventory bagInventory = BackPackItem.getInventoryFromComponents(offhandStack, BackPackItem.Type.POTION);
                    for (int indexInBag = 0; indexInBag < bagInventory.size(); indexInBag++) {
                        ItemStack stackInBag = bagInventory.getStack(indexInBag);
                        if (stackInBag.equals(searchStack)) {
                            return Optional.of(new Index(indexInBag, -1));
                        }
                    }
                }
            }

            DefaultedList<ItemStack> playerInventory = player.getInventory().main;
            for (int inventoryIndex = 0; inventoryIndex < playerInventory.size(); inventoryIndex++) {
                ItemStack stack = playerInventory.get(inventoryIndex);
                if (stack.equals(searchStack)) {
                    result = new Index(inventoryIndex);
                    break;
                }
                if (!checkBagsInInventory) continue;
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
