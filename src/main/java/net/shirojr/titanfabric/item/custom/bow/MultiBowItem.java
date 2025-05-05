package net.shirojr.titanfabric.item.custom.bow;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.shirojr.titanfabric.init.TitanFabricDataComponents;
import net.shirojr.titanfabric.item.custom.TitanFabricBowItem;
import net.shirojr.titanfabric.util.handler.ArrowShootingHandler;
import net.shirojr.titanfabric.util.items.MultiBowHelper;
import net.shirojr.titanfabric.util.items.SelectableArrows;

import java.util.List;

public class MultiBowItem extends TitanFabricBowItem implements SelectableArrows {
    private final int coolDownTicks;
    private float pullProgress;

    public MultiBowItem(Item.Settings settings, int maxArrowCount, int tickCooldown) {
        super(settings
                .component(TitanFabricDataComponents.MULTI_BOW_MAX_ARROWS_COUNT, maxArrowCount)
                .component(TitanFabricDataComponents.MULTI_BOW_ARROWS_COUNT, 0)
        );
        this.coolDownTicks = tickCooldown;
    }

    public int getFullArrowCount(ItemStack stack) {
        return stack.getOrDefault(TitanFabricDataComponents.MULTI_BOW_MAX_ARROWS_COUNT, 0);
    }

    @Override
    public List<Item> titanFabric$supportedArrows() {
        return List.of(Items.ARROW, Items.SPECTRAL_ARROW);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player) || world.isClient()) return;

        int maxUseTime = this.getMaxUseTime(stack, user);
        int useTimeLeft = player.getItemUseTimeLeft();
        this.pullProgress = BowItem.getPullProgress(maxUseTime - useTimeLeft);
        if (pullProgress < 0.2f) return;

        player.getItemCooldownManager().set(stack.getItem(), this.coolDownTicks);
        int possibleArrowCount = Math.min(MultiBowHelper.searchValidArrowStack(player, this).getCount(), MultiBowHelper.getFullArrowCount(stack));
        if (player.isCreative()) possibleArrowCount = MultiBowHelper.getFullArrowCount(stack);

        MultiBowHelper.setArrowsLeft(stack, possibleArrowCount);
        ((ArrowShootingHandler) player).titanfabric$shootsArrows(true);
        stack.set(TitanFabricDataComponents.MULTI_BOW_PROJECTILE_TICK, 10 * possibleArrowCount);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (world.isClient() || !(entity instanceof PlayerEntity player)) return;
        boolean isInHand = player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof MultiBowItem ||
                player.getStackInHand(Hand.OFF_HAND).getItem() instanceof MultiBowItem;
        if (!isInHand) {
            ((ArrowShootingHandler) player).titanfabric$shootsArrows(false);
            return;
        }
        int projectileTick = stack.getOrDefault(TitanFabricDataComponents.MULTI_BOW_PROJECTILE_TICK, 0);
        if (projectileTick < 1) return;
        stack.set(TitanFabricDataComponents.MULTI_BOW_PROJECTILE_TICK, projectileTick - 1);
        if (!validTick(projectileTick + 1)) return;
        ((ArrowShootingHandler) player).titanfabric$shootsArrows(true);
        handleArrowShots(player, MultiBowHelper.searchValidArrowStack(player, this), this.pullProgress);
        handleAfterShotValues(stack, player);
    }

    private static boolean validTick(int tick) {
        return tick % 10 == 0;
    }

    private static void handleAfterShotValues(ItemStack bowStack, PlayerEntity player) {
        if (MultiBowHelper.hasArrowsLeft(bowStack)) {
            int newArrowCount = MultiBowHelper.getArrowsLeft(bowStack) - 1;
            MultiBowHelper.setArrowsLeft(bowStack, newArrowCount);
            if (newArrowCount <= 0) {
                ((ArrowShootingHandler) player).titanfabric$shootsArrows(false);
            }
        }
        if (player instanceof ServerPlayerEntity serverPlayer) {
            bowStack.damage(1, serverPlayer.getServerWorld(), serverPlayer, item -> {
            });
        }
    }

    private static void handleArrowShots(PlayerEntity player, ItemStack arrowStack, double pullProgress) {
        World world = player.getWorld();
        ItemStack bowStack = player.getStackInHand(Hand.MAIN_HAND);
        if (!MultiBowHelper.handleArrowConsumption(player, bowStack, arrowStack)) return;

        world.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS,
                1.0f, 1.0f / (world.getRandom().nextFloat() * 0.4f + 1.2f) + (float) pullProgress * 0.5f);
        PersistentProjectileEntity projectile = MultiBowHelper.prepareArrow(world, player, arrowStack,
                player.getPitch(), player.getYaw(), pullProgress, bowStack);
        world.spawnEntity(projectile);
    }
}
