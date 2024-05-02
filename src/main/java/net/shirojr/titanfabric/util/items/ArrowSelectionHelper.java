package net.shirojr.titanfabric.util.items;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ArrowSelectionHelper {
    private ArrowSelectionHelper() {
    }

    public static boolean containsArrowStack(ItemStack arrowStack, PlayerInventory inventory, SelectableArrows bowItem) {
        if (inventory.isEmpty()) return false;
        List<ItemStack> arrowsList = findAllSupportedArrowStacks(inventory, bowItem);
        return arrowsList.contains(arrowStack);
    }

    public static List<ItemStack> findAllSupportedArrowStacks(PlayerInventory inventory, SelectableArrows bowItem) {
        return inventory.main.stream().filter(itemStack ->
                bowItem.supportedArrows().contains(itemStack.getItem())).toList();
    }
}
