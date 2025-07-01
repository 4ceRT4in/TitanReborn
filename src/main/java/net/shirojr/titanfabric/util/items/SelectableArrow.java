package net.shirojr.titanfabric.util.items;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.shirojr.titanfabric.init.TitanFabricDataComponents;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface SelectableArrow {
    List<Item> titanFabric$supportedArrows();

    /**
     * @param weaponStack ItemStack which holds the data
     * @param index       New Selected Projectile ItemStack index. If <code>null</code> or <code>-1</code> current entry will be removed.
     * @return true, if any changes have been made to the stack
     */
    default boolean setSelectedIndex(Inventory inventory, ItemStack weaponStack, @Nullable Integer index) {
        if (index == null || index == -1) {
            weaponStack.remove(TitanFabricDataComponents.SELECTED_PROJECTILE);
            return true;
        }
        ItemStack stack = inventory.getStack(index);
        if (stack == null || !titanFabric$supportedArrows().contains(stack.getItem())) {
            return false;
        }
        weaponStack.set(TitanFabricDataComponents.SELECTED_PROJECTILE, index);
        return true;
    }

    @Nullable
    default Integer getSelectedIndex(ItemStack weaponStack) {
        return weaponStack.get(TitanFabricDataComponents.SELECTED_PROJECTILE);
    }
}
