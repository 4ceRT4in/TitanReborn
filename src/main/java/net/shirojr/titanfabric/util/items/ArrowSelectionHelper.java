package net.shirojr.titanfabric.util.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;

public class ArrowSelectionHelper {
    private ArrowSelectionHelper() {
    }

    public static boolean containsArrowStack(ItemStack arrowStack, PlayerInventory inventory, SelectableArrow bowItem) {
        if (inventory.isEmpty()) return false;
        List<ItemStack> arrowsList = findAllSupportedArrowStacks(inventory, bowItem);
        return arrowsList.contains(arrowStack);
    }

    public static List<ItemStack> findAllSupportedArrowStacks(PlayerInventory inventory, SelectableArrow bowItem) {
        return inventory.main.stream().filter(itemStack ->
                bowItem.titanFabric$supportedArrows().contains(itemStack.getItem())).toList();
    }

    public static void cleanUpProjectileSelection(PlayerEntity user, ItemStack weaponStack) {
        if (!(weaponStack.getItem() instanceof SelectableArrow selectionHandler)) return;
        Integer selectedIndex = selectionHandler.getSelectedIndex(weaponStack);
        if (selectedIndex == null) return;

        ItemStack selectedArrowStack = user.getInventory().getStack(selectedIndex);
        boolean isArrowSupported = selectionHandler.titanFabric$supportedArrows().contains(selectedArrowStack.getItem());
        boolean inventoryContainsSelectedStack = ArrowSelectionHelper.containsArrowStack(selectedArrowStack, user.getInventory(), selectionHandler);
        if (isArrowSupported && inventoryContainsSelectedStack) return;

        if (user instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.sendMessage(Text.translatable("actionbar.titanfabric.not_compatible"), true);
        }
        List<ItemStack> selectableArrows = findAllSupportedArrowStacks(user.getInventory(), selectionHandler);
        ItemStack newSelectedStack = null;
        if (!selectableArrows.isEmpty()) newSelectedStack = selectableArrows.get(0);
        selectionHandler.setSelectedIndex(user.getInventory(), weaponStack, user.getInventory().indexOf(newSelectedStack));
    }
}
