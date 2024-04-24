package net.shirojr.titanfabric.item.custom.bow;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.shirojr.titanfabric.item.TitanFabricItemGroups;
import net.shirojr.titanfabric.item.custom.TitanFabricBowItem;
import net.shirojr.titanfabric.util.items.MultiBowHelper;
import net.shirojr.titanfabric.util.items.SelectableArrows;

import java.util.List;

public class MultiBowItem extends TitanFabricBowItem implements SelectableArrows {
    private final int coolDownTicks;
    private final int fullArrowCount;
    private float pullProgress;

    public MultiBowItem(int arrowCount, int tickCooldown, int durability) {
        super(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(1).maxDamage(durability));
        this.fullArrowCount = arrowCount;
        this.coolDownTicks = tickCooldown;
    }

    public int getFullArrowCount() {
        return this.fullArrowCount;
    }

    @Override
    public List<Item> supportedArrows() {
        return List.of(Items.ARROW, Items.SPECTRAL_ARROW);
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack itemStack = new ItemStack(this);
        MultiBowHelper.setFullArrowCount(itemStack, 1);
        return itemStack;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity playerEntity) || world.isClient()) return;

        int maxUseTime = this.getMaxUseTime(stack);
        int useTimeLeft = playerEntity.getItemUseTimeLeft();
        this.pullProgress = BowItem.getPullProgress(maxUseTime - useTimeLeft);
        if (pullProgress < 0.2f) return;

        playerEntity.getItemCooldownManager().set(stack.getItem(), this.coolDownTicks);
        int possibleArrowCount = Math.min(MultiBowHelper.searchFirstValidArrowStack(playerEntity, this).getCount(), MultiBowHelper.getFullArrowCount(stack));
        if (playerEntity.isCreative()) possibleArrowCount = MultiBowHelper.getFullArrowCount(stack);

        MultiBowHelper.setArrowsLeftNbt(stack, possibleArrowCount);
        stack.getOrCreateNbt().putInt(MultiBowHelper.PROJECTILE_TICK_NBT_KEY, 10 * possibleArrowCount);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (world.isClient() || !(entity instanceof PlayerEntity player)) return;
        if (!stack.getOrCreateNbt().contains(MultiBowHelper.FULL_ARROW_COUNT_NBT_KEY))
            MultiBowHelper.setFullArrowCount(stack, this.fullArrowCount);

        boolean isInHand = player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof MultiBowItem ||
                player.getStackInHand(Hand.OFF_HAND).getItem() instanceof MultiBowItem;
        if (!isInHand) return;
        int projectileTick = stack.getOrCreateNbt().getInt(MultiBowHelper.PROJECTILE_TICK_NBT_KEY);
        if (projectileTick < 1) return;
        stack.getOrCreateNbt().putInt(MultiBowHelper.PROJECTILE_TICK_NBT_KEY, projectileTick - 1);
        if (!validTick(projectileTick + 1)) return;
        handleArrowShots(player, MultiBowHelper.searchFirstValidArrowStack(player, this), this.pullProgress);
        handleAfterShotValues(stack, player);
    }

    private static boolean validTick(int tick) {
        return tick % 10 == 0;
    }

    private static void handleAfterShotValues(ItemStack bowStack, PlayerEntity player) {
        if (bowStack.getOrCreateNbt().contains(MultiBowHelper.ARROWS_LEFT_NBT_KEY)) {
            int arrows = bowStack.getOrCreateNbt().getInt(MultiBowHelper.ARROWS_LEFT_NBT_KEY) - 1;
            if (arrows > 0) bowStack.getOrCreateNbt().putInt(MultiBowHelper.ARROWS_LEFT_NBT_KEY, arrows);
            else bowStack.removeSubNbt(MultiBowHelper.ARROWS_LEFT_NBT_KEY);
        }
        bowStack.damage(1, player, p -> p.sendToolBreakStatus(p.getActiveHand()));
    }

    private static void handleArrowShots(PlayerEntity player, ItemStack arrowStack, double pullProgress) {
        World world = player.getWorld();
        ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);
        int powerEnchantLevel = EnchantmentHelper.getLevel(Enchantments.POWER, stack);
        int punchEnchantLevel = EnchantmentHelper.getLevel(Enchantments.PUNCH, stack);
        int flameEnchantLevel = EnchantmentHelper.getLevel(Enchantments.FLAME, stack);

        if (!MultiBowHelper.handleArrowConsumption(player, stack, arrowStack)) return;

        world.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS,
                1.0f, 1.0f / (world.getRandom().nextFloat() * 0.4f + 1.2f) + (float) pullProgress * 0.5f);

        PersistentProjectileEntity projectile = MultiBowHelper.prepareArrow(world, player, arrowStack,
                player.getPitch(), player.getYaw(), pullProgress, powerEnchantLevel, punchEnchantLevel, flameEnchantLevel);

        world.spawnEntity(projectile);

        MultiBowHelper.setArrowsLeftNbt(stack, MultiBowHelper.getArrowsLeftNbt(stack) - 1);
    }
}
