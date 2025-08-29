package net.shirojr.titanfabric.util.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.shirojr.titanfabric.init.TitanFabricGamerules;
import net.shirojr.titanfabric.item.custom.misc.BackPackItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class ArrowSelectionHelper {
    private ArrowSelectionHelper() {
    }

    public static List<ItemStack> findAllSupportedArrowStacks(PlayerEntity player, SelectableArrow bowItem) {
        List<ItemStack> supported = new ArrayList<>();
        Predicate<ItemStack> isSupportedArrowStack = stack -> bowItem.titanFabric$supportedArrows().contains(stack.getItem());

        boolean checkBagsInInventory = player.getWorld().getGameRules().getBoolean(TitanFabricGamerules.FULL_INVENTORY_POTION_BAG_SEARCH);
        for (ItemStack stack : player.getInventory().main) {
            if (isSupportedArrowStack.test(stack)) {
                supported.add(stack);
                continue;
            }
            if (!checkBagsInInventory) continue;
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

        ItemStack offhandStack = player.getOffHandStack();
        if (!checkBagsInInventory && offhandStack.getItem() instanceof BackPackItem bagItem && bagItem.getBackpackType().equals(BackPackItem.Type.POTION)) {
            Inventory bagItemInventory = BackPackItem.getInventoryFromComponents(offhandStack, BackPackItem.Type.POTION);
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
        ItemStack selectedArrowStack = SelectableArrow.getSelectedArrowStack(weaponStack, user);
        if (selectedArrowStack == null || selectionHandler.titanFabric$supportedArrows().contains(selectedArrowStack.getItem())) {
            return;
        }
        if (user instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.sendMessage(Text.translatable("actionbar.titanfabric.not_compatible"), true);
        }

        List<ItemStack> selectableArrows = findAllSupportedArrowStacks(user, selectionHandler);
        ItemStack newSelectedStack = null;
        if (!selectableArrows.isEmpty()) newSelectedStack = selectableArrows.get(0);
        Optional<SelectableArrow.Index> index = SelectableArrow.Index.get(user, newSelectedStack);
        SelectableArrow.applySelectedArrowStack(weaponStack, index.orElse(null));
    }
}
