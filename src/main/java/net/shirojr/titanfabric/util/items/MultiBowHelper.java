package net.shirojr.titanfabric.util.items;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.util.TitanFabricTags;
import net.shirojr.titanfabric.util.handler.ArrowSelectionHandler;

import java.util.function.Predicate;

/**
 * Utillity Class to clean up {@linkplain net.shirojr.titanfabric.item.custom.bow.MultiBowItem MultiBowItem class} a bit.
 */
public final class MultiBowHelper {
    public static final String FULL_ARROW_COUNT_NBT_KEY = TitanFabric.MODID + ".concurrent_arrows";
    public static final String ARROWS_LEFT_NBT_KEY = TitanFabric.MODID + ".arrows_left";
    public static final String PROJECTILE_TICK_NBT_KEY = TitanFabric.MODID + ".projectile_tick";

    private MultiBowHelper() {
        // private ctor to avoid instantiating this utility class
    }

    /**
     * Set the arrow count, which will be shot at the same time.
     *
     * @param bowStack for editing the NBT information
     * @param arrows   count of concurrent arrows
     */
    public static void setFullArrowCount(ItemStack bowStack, int arrows) {
        bowStack.getOrCreateNbt().putInt(FULL_ARROW_COUNT_NBT_KEY, arrows);
        if (arrows < 1) bowStack.removeSubNbt(FULL_ARROW_COUNT_NBT_KEY);
    }

    /**
     * Get the concurrent arrow count, which is currently saved in the ItemStack's NBT information
     *
     * @param itemStack Bow ItemStack to get the necessary information
     * @return amount of arrows, which will be shot at the same time
     */
    public static int getFullArrowCount(ItemStack itemStack) {
        if (!itemStack.getOrCreateNbt().contains(FULL_ARROW_COUNT_NBT_KEY)) return 0;
        return itemStack.getOrCreateNbt().getInt(FULL_ARROW_COUNT_NBT_KEY);
    }

    public static int getArrowsLeftNbt(ItemStack itemStack) {
        if (!itemStack.getOrCreateNbt().contains(ARROWS_LEFT_NBT_KEY)) return 0;
        return itemStack.getOrCreateNbt().getInt(ARROWS_LEFT_NBT_KEY);
    }

    /**
     * Set the currently loaded arrow ammount in the NBT values of the ItemStack
     *
     * @param bowStack original Bow ItemStack
     * @param arrows   new value of arrows
     */
    public static void setArrowsLeftNbt(ItemStack bowStack, int arrows) {
        bowStack.getOrCreateNbt().putInt(ARROWS_LEFT_NBT_KEY, arrows);
        if (arrows < 1) bowStack.removeSubNbt(ARROWS_LEFT_NBT_KEY);
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
        if (player.getAbilities().creativeMode || EnchantmentHelper.getLevel(Enchantments.INFINITY, bowStack) > 0)
            return true;
        if (!inventory.contains(arrowStack) || arrowStack.getCount() < 1)
            return false;

        //arrowStack.decrement(1);
        inventory.removeStack(inventory.getSlotWithStack(arrowStack), 1);
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
                                                          float pitch, float yaw, double pullProgress,
                                                          int powerLevel, int punchLevel, int flameLevel) {

        ArrowItem arrowItem = (ArrowItem) (arrowStack.getItem() instanceof ArrowItem ? arrowStack.getItem() : Items.ARROW);
        PersistentProjectileEntity persistentProjectileEntity = arrowItem.createArrow(world, arrowStack, player);
        persistentProjectileEntity.setVelocity(player, pitch, yaw, 0.0f, (float) pullProgress * 3.0f, 1.0f);

        double powerDamage = persistentProjectileEntity.getDamage() + powerLevel * 0.5 + 0.5;
        if (powerLevel > 0) persistentProjectileEntity.setDamage(powerDamage);
        if (punchLevel > 0) persistentProjectileEntity.setPunch(punchLevel);
        if (flameLevel > 0) persistentProjectileEntity.setOnFireFor(100);
        if (pullProgress == 1.0f) persistentProjectileEntity.setCritical(true);

        return persistentProjectileEntity;
    }
}
