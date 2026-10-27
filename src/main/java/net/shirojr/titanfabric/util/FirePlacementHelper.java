package net.shirojr.titanfabric.util;

import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/** Lets ignition items replace the small plants that water and lava buckets can displace. */
public final class FirePlacementHelper {
    private FirePlacementHelper() {
    }

    public static BlockPos getPlacementPos(World world, BlockPos clickedPos, Direction side) {
        return isIgnitionReplaceable(world.getBlockState(clickedPos))
                ? clickedPos
                : clickedPos.offset(side);
    }

    public static boolean prepareNormalFirePlacement(World world, BlockPos pos) {
        return preparePlacement(world, pos, AbstractFireBlock.getState(world, pos).canPlaceAt(world, pos));
    }

    public static boolean prepareSoulFirePlacement(World world, BlockPos pos) {
        return preparePlacement(world, pos, SoulFireStateHelper.canPlaceAt(world, pos));
    }

    private static boolean preparePlacement(World world, BlockPos pos, boolean canPlaceFire) {
        BlockState state = world.getBlockState(pos);
        if (state.isAir()) {
            return canPlaceFire;
        }
        if (!canPlaceFire || !isIgnitionReplaceable(state)) {
            return false;
        }

        if (world.isClient()) {
            return world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
        }
        return world.breakBlock(pos, true);
    }

    public static boolean isIgnitionReplaceable(BlockState state) {
        return state.isOf(Blocks.SHORT_GRASS)
                || state.isOf(Blocks.FERN)
                || state.isOf(Blocks.DEAD_BUSH);
    }
}
