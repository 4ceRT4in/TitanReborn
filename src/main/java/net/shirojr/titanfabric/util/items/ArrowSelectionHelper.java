package net.shirojr.titanfabric.util.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.shirojr.titanfabric.util.handler.ArrowSelectionHandler;

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
                bowItem.titanFabric$supportedArrows().contains(itemStack.getItem())).toList();
    }

    public static void cleanUpProjectileSelection(PlayerEntity user, SelectableArrows bowItem) {
        ArrowSelectionHandler arrowSelection = (ArrowSelectionHandler) user;
        if (arrowSelection.titanfabric$getSelectedArrowIndex().isEmpty()) return;
        ItemStack selectedArrowStack = user.getInventory().getStack(arrowSelection.titanfabric$getSelectedArrowIndex().get());
        boolean isArrowSupported = bowItem.titanFabric$supportedArrows().contains(selectedArrowStack.getItem());
        boolean inventoryContainsSelectedStack = ArrowSelectionHelper.containsArrowStack(selectedArrowStack, user.getInventory(), bowItem);
        if (isArrowSupported && inventoryContainsSelectedStack) return;

        if (user instanceof ServerPlayerEntity serverPlayer)
            serverPlayer.sendMessage(new TranslatableText("actionbar.titanfabric.not_compatible"), true);
        List<ItemStack> selectableArrows = findAllSupportedArrowStacks(user.getInventory(), bowItem);
        ItemStack newSelectedStack = null;
        if (selectableArrows.size() > 0) newSelectedStack = selectableArrows.get(0);
        arrowSelection.titanfabric$setSelectedArrowIndex(newSelectedStack);
    }
}
