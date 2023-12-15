package net.shirojr.titanfabric.block.custom;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.shirojr.titanfabric.block.TitanFabricBlockEntities;
import net.shirojr.titanfabric.block.entity.DiamondFurnaceBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class DiamondFurnaceBlock extends AbstractFurnaceBlock {
    public DiamondFurnaceBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void openScreen(World world, BlockPos pos, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof DiamondFurnaceBlockEntity)) return;

        NamedScreenHandlerFactory screenHandlerFactory = world.getBlockState(pos).createScreenHandlerFactory(world, pos);
        player.openHandledScreen(screenHandlerFactory);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, TitanFabricBlockEntities.DIAMOND_FURNACE, DiamondFurnaceBlockEntity::tick);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DiamondFurnaceBlockEntity(pos, state);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!state.get(LIT)) return;

        double xOrigin = (double) pos.getX() + 0.5;
        double yOrigin = pos.getY();
        double zOrigin = (double) pos.getZ() + 0.5;
        if (random.nextDouble() < 0.1) {
            world.playSound(xOrigin, yOrigin, zOrigin, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS,
                    1.0f, 1.0f, false);
        }
        Direction direction = state.get(FACING);
        Direction.Axis axis = direction.getAxis();
        double g = 0.52;
        double h = random.nextDouble() * 0.6 - 0.3;
        double i = axis == Direction.Axis.X ? (double) direction.getOffsetX() * g : h;
        double j = random.nextDouble() * 6.0 / 16.0;
        double k = axis == Direction.Axis.Z ? (double) direction.getOffsetZ() * g : h;
        world.addParticle(ParticleTypes.SMOKE, xOrigin + i, yOrigin + j, zOrigin + k, 0.0, 0.0, 0.0);
        world.addParticle(ParticleTypes.FLAME, xOrigin + i, yOrigin + j, zOrigin + k, 0.0, 0.0, 0.0);
    }
}
