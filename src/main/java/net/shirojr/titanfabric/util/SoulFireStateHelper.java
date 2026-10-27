package net.shirojr.titanfabric.util;

import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.block.SoulFireBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;

/** Builds soul-fire states with the same directional properties as normal fire. */
public final class SoulFireStateHelper {
    private SoulFireStateHelper() {
    }

    public static boolean canPlaceAt(WorldView world, BlockPos pos) {
        return SoulFireBlock.isSoulBase(world.getBlockState(pos.down()))
                || Blocks.FIRE.getDefaultState().canPlaceAt(world, pos);
    }

    public static BlockState getStateForPosition(BlockView world, BlockPos pos) {
        BlockState soulFire = Blocks.SOUL_FIRE.getDefaultState();
        BlockState normalFire = AbstractFireBlock.getState(world, pos);
        if (!normalFire.isOf(Blocks.FIRE)) {
            return soulFire;
        }

        return soulFire
                .with(FireBlock.AGE, normalFire.get(FireBlock.AGE))
                .with(FireBlock.NORTH, normalFire.get(FireBlock.NORTH))
                .with(FireBlock.EAST, normalFire.get(FireBlock.EAST))
                .with(FireBlock.SOUTH, normalFire.get(FireBlock.SOUTH))
                .with(FireBlock.WEST, normalFire.get(FireBlock.WEST))
                .with(FireBlock.UP, normalFire.get(FireBlock.UP));
    }
}
