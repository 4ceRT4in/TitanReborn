package net.shirojr.titanfabric.util.items;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.shirojr.titanfabric.init.TitanFabricDataComponents;
import net.shirojr.titanfabric.util.TitanFabricTags;
import net.shirojr.titanfabric.util.handler.ArrowSelectionHandler;

import java.util.function.Predicate;

/**
 * Utillity Class to clean up {@linkplain net.shirojr.titanfabric.item.custom.bow.MultiBowItem MultiBowItem class} a bit.
 */
public final class MultiBowHelper {
    private MultiBowHelper() {
    }

    /**
     * Set the arrow count, which will be shot at the same time.
     *
     * @param bowStack for editing the NBT information
     * @param arrows   count of concurrent arrows
     */
    public static void setFullArrowCount(ItemStack bowStack, int arrows) {
        bowStack.set(TitanFabricDataComponents.MULTI_BOW_MAX_ARROWS_COUNT, arrows);
    }

    /**
     * Get the concurrent arrow count, which is currently saved in the ItemStack's NBT information
     *
     * @param itemStack Bow ItemStack to get the necessary information
     * @return amount of arrows, which will be shot at the same time
     */
    public static int getFullArrowCount(ItemStack itemStack) {
        return itemStack.getOrDefault(TitanFabricDataComponents.MULTI_BOW_MAX_ARROWS_COUNT, 1);
    }

    public static int getArrowsLeft(ItemStack itemStack) {
        return itemStack.getOrDefault(TitanFabricDataComponents.MULTI_BOW_ARROWS_COUNT, 0);
    }

    /**
     * Set the currently loaded arrow ammount in the NBT values of the ItemStack
     *
     * @param bowStack original Bow ItemStack
     * @param arrows   new value of arrows
     */
    public static void setArrowsLeft(ItemStack bowStack, int arrows) {
        bowStack.set(TitanFabricDataComponents.MULTI_BOW_ARROWS_COUNT, arrows);
    }

    public static boolean hasArrowsLeft(ItemStack bowStack) {
        return bowStack.getOrDefault(TitanFabricDataComponents.MULTI_BOW_ARROWS_COUNT, 0) > 0;
    }

    /**
     * Handles Arrow management for the {@linkplain net.shirojr.titanfabric.item.custom.bow.MultiBowItem MultiBow}.
     * Only if the player has enough Arrow items, specified in {@linkplain TitanFabricTags} the Arrow ItemStack will be removed.
     *
     * @param player     for inventory management
     * @param bowStack   for reading concurrent arrow consumption information
     * @param arrowStack for consuming the correct arrow item
     * @return false, if not enough arrows were found in the inventory
     */
    public static boolean handleArrowConsumption(PlayerEntity player, ItemStack bowStack, ItemStack arrowStack) {
        PlayerInventory inventory = player.getInventory();
        if (player.getWorld() instanceof ServerWorld serverWorld && EnchantmentHelper.getAmmoUse(serverWorld, bowStack, arrowStack, 1) > 0)
            return true;
        if (!inventory.contains(arrowStack) || arrowStack.getCount() < 1)
            return false;

        arrowStack.decrementUnlessCreative(1, player);
        return true;
    }

    /**
     * Finds the first possible ArrowStack in an inventory, defined in {@linkplain TitanFabricTags}
     *
     * @param player used to get access to the inventory
     * @return returns either an Empty ItemStack or the first possible Arrow ItemStack
     */
    public static ItemStack searchValidArrowStack(PlayerEntity player, SelectableArrows selectableArrows) {
        Predicate<ItemStack> isSelectableArrow = itemStack -> selectableArrows.titanFabric$supportedArrows().contains(itemStack.getItem());
        ArrowSelectionHelper.cleanUpProjectileSelection(player, selectableArrows);
        ArrowSelectionHandler handler = (ArrowSelectionHandler) player;

        ItemStack outputStack = ItemStack.EMPTY;
        if (handler.titanfabric$getSelectedArrowIndex().isPresent()) {
            ItemStack stack = player.getInventory().getStack(handler.titanfabric$getSelectedArrowIndex().get());
            if (isSelectableArrow.test(stack)) outputStack = stack;
        }
        if (outputStack.isEmpty()) {
            for (ItemStack stack : player.getInventory().main) {
                if (isSelectableArrow.test(stack)) return stack;
            }
        }
        if (player.isCreative()) new ItemStack(Items.ARROW);
        return outputStack;
    }

    public static PersistentProjectileEntity prepareArrow(World world, PlayerEntity player, ItemStack arrowStack,
                                                          float pitch, float yaw, double pullProgress, ItemStack bowStack) {
        ArrowItem arrowItem = (ArrowItem) (arrowStack.getItem() instanceof ArrowItem ? arrowStack.getItem() : Items.ARROW);
        PersistentProjectileEntity persistentProjectileEntity = arrowItem.createArrow(world, arrowStack, player, bowStack);
        persistentProjectileEntity.setVelocity(player, pitch, yaw, 0.0f, (float) pullProgress * 3.0f, 1.0f);
        if (pullProgress == 1.0f) persistentProjectileEntity.setCritical(true);

        return persistentProjectileEntity;
    }
}
