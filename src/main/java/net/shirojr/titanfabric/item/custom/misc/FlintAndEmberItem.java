package net.shirojr.titanfabric.item.custom.misc;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
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
        BlockPos blockPos;
        PlayerEntity playerEntity = context.getPlayer();
        World world = context.getWorld();
        BlockState blockState = world.getBlockState(blockPos = context.getBlockPos());
        ItemStack itemStack = context.getStack();

        if (CampfireBlock.canBeLit(blockState) || CandleBlock.canBeLit(blockState) || CandleCakeBlock.canBeLit(blockState)) {
            world.playSound(playerEntity, blockPos, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.BLOCKS, 1.0f, (world.getRandom().nextFloat() * 0.4f + 0.8f));
            world.setBlockState(blockPos, blockState.with(Properties.LIT, true), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
            world.emitGameEvent(playerEntity, GameEvent.BLOCK_PLACE, blockPos);
            if (playerEntity != null) {
                itemStack.damage(1, playerEntity, p -> p.sendToolBreakStatus(context.getHand()));
            }
            return ActionResult.success(world.isClient());
        }

        BlockPos blockPos2 = blockPos.offset(context.getSide());
        if (AbstractFireBlock.canPlaceAt(world, blockPos2, context.getPlayerFacing())) {
            world.playSound(playerEntity, blockPos2, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.BLOCKS, 1.0f, (world.getRandom().nextFloat() * 0.4f + 0.8f));
            if (itemStack.getDamage() < itemStack.getMaxDamage() - 28) {
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        BlockPos firePos = blockPos2.add(x, 0, z);
                        if (world.getBlockState(firePos).isAir() && AbstractFireBlock.canPlaceAt(world, firePos, context.getPlayerFacing())) {
                            world.setBlockState(firePos, Blocks.SOUL_FIRE.getDefaultState(), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
                            world.emitGameEvent(playerEntity, GameEvent.BLOCK_PLACE, firePos);
                        }
                    }
                }
                playerEntity.getItemCooldownManager().set(itemStack.getItem(), 30);
                itemStack.damage(9, playerEntity, p -> p.sendToolBreakStatus(context.getHand()));
            } else if (itemStack.getDamage() < itemStack.getMaxDamage() - 8) {
                BlockPos[] firePositions = {
                        blockPos2,
                        blockPos2.north(),
                        blockPos2.south(),
                        blockPos2.east(),
                        blockPos2.west()
                };
                for (BlockPos firePos : firePositions) {
                    if (world.getBlockState(firePos).isAir() && AbstractFireBlock.canPlaceAt(world, firePos, Direction.UP)) {
                        world.setBlockState(firePos, Blocks.SOUL_FIRE.getDefaultState(), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
                        world.emitGameEvent(playerEntity, GameEvent.BLOCK_PLACE, firePos);
                    }
                }
                playerEntity.getItemCooldownManager().set(itemStack.getItem(), 20);
                itemStack.damage(5, playerEntity, p -> p.sendToolBreakStatus(context.getHand()));
            } else {
                world.setBlockState(blockPos2, Blocks.SOUL_FIRE.getDefaultState(), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
                world.emitGameEvent(playerEntity, GameEvent.BLOCK_PLACE, blockPos2);
                playerEntity.getItemCooldownManager().set(itemStack.getItem(), 10);
                itemStack.damage(1, playerEntity, p -> p.sendToolBreakStatus(context.getHand()));
            }

            if (playerEntity instanceof ServerPlayerEntity) {
                Criteria.PLACED_BLOCK.trigger((ServerPlayerEntity) playerEntity, blockPos2, itemStack);
            }
            return ActionResult.success(world.isClient());
        }
        return ActionResult.FAIL;
    }
}
