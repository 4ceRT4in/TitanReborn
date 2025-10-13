package net.shirojr.titanfabric.item.custom.misc;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class FlintAndEmberItem extends FlintAndSteelItem {
    public FlintAndEmberItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        ItemStack stack = context.getStack();

        if (CampfireBlock.canBeLit(state) || CandleBlock.canBeLit(state) || CandleCakeBlock.canBeLit(state)) {
            world.playSound(player, pos, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.BLOCKS, 1.0f, world.getRandom().nextFloat() * 0.4f + 0.8f);
            world.setBlockState(pos, state.with(Properties.LIT, true), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
            world.emitGameEvent(player, GameEvent.BLOCK_PLACE, pos);
            if (player != null) {
                stack.damage(1, player, LivingEntity.getSlotForHand(context.getHand()));
            }
            return ActionResult.success(world.isClient());
        }

        BlockPos firePos = pos.offset(context.getSide());
        if (!AbstractFireBlock.canPlaceAt(world, firePos, Direction.UP)) {
            return ActionResult.FAIL;
        }

        world.playSound(player, firePos, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.BLOCKS, 1.0f, world.getRandom().nextFloat() * 0.4f + 0.8f);

        int damage = stack.getDamage();
        int maxDamage = stack.getMaxDamage();

        if (damage < maxDamage - 28) {
            placeSoulFireArea(world, firePos, player, 3, 3);
            if (player != null) {
                player.getItemCooldownManager().set(stack.getItem(), 30);
                stack.damage(9, player, LivingEntity.getSlotForHand(context.getHand()));
            }
        } else if (damage < maxDamage - 8) {
            placeSoulFireCross(world, firePos, player);
            if (player != null) {
                player.getItemCooldownManager().set(stack.getItem(), 20);
                stack.damage(5, player, LivingEntity.getSlotForHand(context.getHand()));
            }
        } else {
            BlockState soul = Blocks.SOUL_FIRE.getDefaultState();
            world.setBlockState(firePos, soul, Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
            world.emitGameEvent(player, GameEvent.BLOCK_PLACE, firePos);
            if (player != null) {
                stack.damage(1, player, LivingEntity.getSlotForHand(context.getHand()));
            }
        }

        if (player instanceof ServerPlayerEntity) {
            Criteria.PLACED_BLOCK.trigger((ServerPlayerEntity) player, firePos, stack);
        }
        return ActionResult.success(world.isClient());
    }

    private void placeSoulFireArea(World world, BlockPos center, PlayerEntity player, int width, int height) {
        int offset = width / 2;
        for (int x = -offset; x <= offset; x++) {
            for (int z = -offset; z <= offset; z++) {
                BlockPos p = center.add(x, 0, z);
                if (world.getBlockState(p).isAir() && AbstractFireBlock.canPlaceAt(world, p, Direction.UP)) {
                    BlockState soul = Blocks.SOUL_FIRE.getDefaultState();
                    world.setBlockState(p, soul, Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
                    world.emitGameEvent(player, GameEvent.BLOCK_PLACE, p);
                }
            }
        }
    }

    private void placeSoulFireCross(World world, BlockPos center, PlayerEntity player) {
        BlockPos[] positions = {center, center.north(), center.south(), center.east(), center.west()};
        for (BlockPos p : positions) {
            if (world.getBlockState(p).isAir() && AbstractFireBlock.canPlaceAt(world, p, Direction.UP)) {
                BlockState soul = Blocks.SOUL_FIRE.getDefaultState();
                world.setBlockState(p, soul, Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
                world.emitGameEvent(player, GameEvent.BLOCK_PLACE, p);
            }
        }
    }
}