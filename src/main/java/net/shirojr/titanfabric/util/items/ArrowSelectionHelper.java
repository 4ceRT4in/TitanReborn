package net.shirojr.titanfabric.util.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.shirojr.titanfabric.item.custom.misc.BackPackItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class ArrowSelectionHelper {
    private ArrowSelectionHelper() {
    }

    public static List<ItemStack> findAllSupportedArrowStacks(List<ItemStack> inventory, SelectableArrow bowItem) {
        List<ItemStack> supported = new ArrayList<>();
        Predicate<ItemStack> isSupportedArrowStack = stack -> bowItem.titanFabric$supportedArrows().contains(stack.getItem());
        for (ItemStack stack : inventory) {
            if (isSupportedArrowStack.test(stack)) {
                supported.add(stack);
                continue;
            }
            if (!(stack.getItem() instanceof BackPackItem bagItem)) continue;
            if (!bagItem.getBackpackType().equals(BackPackItem.Type.POTION)) continue;
            Inventory bagItemInventory = BackPackItem.getInventoryFromComponents(stack, BackPackItem.Type.POTION);
            for (int i = 0; i < bagItemInventory.size(); i++) {
                ItemStack stackInBag = bagItemInventory.getStack(i);
                if (isSupportedArrowStack.test(stackInBag)) {
                    supported.add(stackInBag);
                }
            }
        }
        return supported;
    }

    public static void cleanUpProjectileSelection(PlayerEntity user, ItemStack weaponStack) {
        if (!(weaponStack.getItem() instanceof SelectableArrow selectionHandler)) return;
        ItemStack selectedArrowStack = SelectableArrow.getSelectedArrowStack(weaponStack, user.getInventory().main);
        if (selectedArrowStack == null || selectionHandler.titanFabric$supportedArrows().contains(selectedArrowStack.getItem())) {
            return;
        }
        if (user instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.sendMessage(Text.translatable("actionbar.titanfabric.not_compatible"), true);
        }
        List<ItemStack> selectableArrows = findAllSupportedArrowStacks(user.getInventory().main, selectionHandler);
        ItemStack newSelectedStack = null;
        if (!selectableArrows.isEmpty()) newSelectedStack = selectableArrows.get(0);
        Optional<SelectableArrow.Index> index = SelectableArrow.Index.get(user.getInventory().main, newSelectedStack);
        SelectableArrow.applySelectedArrowStack(weaponStack, index.orElse(null));
    }
}
