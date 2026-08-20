package net.shirojr.titanfabric.item.custom.misc;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.*;
import net.minecraft.block.enums.DoubleBlockHalf;
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
            if (!isReplaceablePlant(world.getBlockState(firePos))) return ActionResult.FAIL;
        }

        world.playSound(player, firePos, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.BLOCKS, 1.0f, world.getRandom().nextFloat() * 0.4f + 0.8f);

        int placed = tryPlaceSoulFire(world, firePos, player);
        if (player != null && placed > 0) {
            stack.damage(placed, player, LivingEntity.getSlotForHand(context.getHand()));
        }

        if (player instanceof ServerPlayerEntity && placed > 0) {
            Criteria.PLACED_BLOCK.trigger((ServerPlayerEntity) player, firePos, stack);
        }
        return ActionResult.success(world.isClient());
    }

    private boolean isReplaceablePlant(BlockState state) {
        return state.getBlock() instanceof PlantBlock
                || state.getBlock() instanceof TallPlantBlock
                || state.isOf(Blocks.SHORT_GRASS)
                || state.isOf(Blocks.TALL_GRASS);
    }

    private void removePlantAt(World world, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof TallPlantBlock && state.contains(Properties.DOUBLE_BLOCK_HALF)) {
            DoubleBlockHalf half = state.get(Properties.DOUBLE_BLOCK_HALF);
            BlockPos other = half == DoubleBlockHalf.UPPER ? pos.down() : pos.up();
            world.breakBlock(pos, false);
            if (world.getBlockState(other).getBlock() instanceof TallPlantBlock) {
                world.breakBlock(other, false);
            }
        } else {
            world.breakBlock(pos, false);
        }
    }

    private int tryPlaceSoulFire(World world, BlockPos pos, PlayerEntity player) {
        BlockState current = world.getBlockState(pos);
        if (isReplaceablePlant(current)) {
            removePlantAt(world, pos, current);
        }
        if (world.getBlockState(pos).isAir() && AbstractFireBlock.canPlaceAt(world, pos, Direction.UP)) {
            BlockState soul = Blocks.SOUL_FIRE.getDefaultState();
            world.setBlockState(pos, soul, Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
            world.emitGameEvent(player, GameEvent.BLOCK_PLACE, pos);
            return 1;
        }
        return 0;
    }

}
