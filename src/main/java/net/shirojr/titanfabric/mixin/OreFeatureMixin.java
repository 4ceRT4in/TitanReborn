package net.shirojr.titanfabric.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.gen.feature.OreFeature;
import net.shirojr.titanfabric.block.TitanFabricBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;

@Mixin(OreFeature.class)
public abstract class OreFeatureMixin {

    private static final Random random = new Random();

    @Redirect(method = "generateVeinPart", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/chunk/ChunkSection;setBlockState(IIILnet/minecraft/block/BlockState;Z)Lnet/minecraft/block/BlockState;"
    ))
    private BlockState redirectSetBlockState(ChunkSection instance, int x, int y, int z, BlockState state, boolean lock) {
        BlockState previousState = instance.setBlockState(x, y, z, state);

        if (state.getBlock() == TitanFabricBlocks.DEEPSTALE_LEGEND_ORE) {
            if (Math.random() <= 0.4) { // 40% chance (bumped cause 20 was too low, in order to match with the checks)
                int crystalCount = MathHelper.nextInt(random, 1, 3);

                for (int i = 0; i < crystalCount; i++) {
                    int range = 1;
                    int nx = MathHelper.clamp(x + MathHelper.nextInt(random, -range, range), 0, 15);
                    int ny = MathHelper.clamp(y + MathHelper.nextInt(random, -range, range), 0, 15);
                    int nz = MathHelper.clamp(z + MathHelper.nextInt(random, -range, range), 0, 15);

                    if (isValidChunkCoordinate(nx, ny, nz) && isValidCrystalPlacement(instance, nx, ny, nz)) {
                        Direction facing = getCrystalFacing(instance, nx, ny, nz);
                        BlockState crystalState = TitanFabricBlocks.LEGEND_CRYSTAL.getDefaultState().with(Properties.FACING, facing);
                        instance.setBlockState(nx, ny, nz, crystalState);
                    }
                }
            }
        }
        return previousState;
    }

    @Unique
    private boolean isValidChunkCoordinate(int x, int y, int z) {
        return x >= 0 && x < 16 && y >= 0 && y < 16 && z >= 0 && z < 16;
    }

    @Unique
    private boolean isValidCrystalPlacement(ChunkSection instance, int x, int y, int z) {
        if (!isValidChunkCoordinate(x, y, z) || !isValidChunkCoordinate(x, y - 1, z)) return false;

        BlockState belowState = instance.getBlockState(x, y - 1, z);
        return (belowState.getBlock() == net.minecraft.block.Blocks.DEEPSLATE || belowState.getBlock() == TitanFabricBlocks.DEEPSTALE_LEGEND_ORE)
                && instance.getBlockState(x, y, z).isAir();
    }

    @Unique
    private Direction getCrystalFacing(ChunkSection instance, int x, int y, int z) {
        for (Direction direction : Direction.values()) {
            int nx = x + direction.getOffsetX();
            int ny = y + direction.getOffsetY();
            int nz = z + direction.getOffsetZ();
            if (isValidChunkCoordinate(nx, ny, nz)) {
                BlockState adjacentState = instance.getBlockState(nx, ny, nz);
                if (!adjacentState.isAir() && adjacentState.getBlock() != TitanFabricBlocks.LEGEND_CRYSTAL && adjacentState.getBlock() != net.minecraft.block.Blocks.LAVA && adjacentState.getBlock() != Blocks.WATER) {
                    return direction.getOpposite();
                }
            }
        }
        return Direction.UP;
    }
}
