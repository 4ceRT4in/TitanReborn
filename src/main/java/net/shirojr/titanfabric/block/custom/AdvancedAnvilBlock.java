package net.shirojr.titanfabric.block.custom;

import net.minecraft.block.*;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.shirojr.titanfabric.block.stats.TitanFabricStats;
import org.jetbrains.annotations.Nullable;

public class AdvancedAnvilBlock extends FallingBlock {
    private static final Text TITLE = new TranslatableText("block.titanfabric.netherite_anvil");
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

    public AdvancedAnvilBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }


    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().rotateYClockwise());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public DamageSource getDamageSource() {
        return DamageSource.ANVIL;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;
        player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
        player.incrementStat(TitanFabricStats.USED_ADVANCED_ANVIL);
        return ActionResult.CONSUME;
    }

    @Override
    @Nullable
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return new SimpleNamedScreenHandlerFactory((syncId, inventory, player) ->
                new AnvilScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos)), TITLE);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(FACING)) {
            case EAST -> VoxelShapes.union(AdvancedAnvilBlock.SHAPE_E, AdvancedAnvilBlock.SHAPE_BASE);
            case SOUTH -> VoxelShapes.union(AdvancedAnvilBlock.SHAPE_S, AdvancedAnvilBlock.SHAPE_BASE);
            case WEST -> VoxelShapes.union(AdvancedAnvilBlock.SHAPE_W, AdvancedAnvilBlock.SHAPE_BASE);
            default -> VoxelShapes.union(AdvancedAnvilBlock.SHAPE_N, AdvancedAnvilBlock.SHAPE_BASE);
        };
    }

    @Override
    protected void configureFallingBlockEntity(FallingBlockEntity entity) {
        entity.setHurtEntities(2.0f, 40);
    }

    @Override
    public void onLanding(World world, BlockPos pos, BlockState fallingBlockState, BlockState currentStateInPos, FallingBlockEntity fallingBlockEntity) {
        super.onLanding(world, pos, fallingBlockState, currentStateInPos, fallingBlockEntity);
        if (world instanceof ServerWorld serverWorld) {
            serverWorld.playSound(null, pos, SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 3f, 0.65f);
        }
    }

    @Override
    public void onDestroyedOnLanding(World world, BlockPos pos, FallingBlockEntity fallingBlockEntity) {
        super.onDestroyedOnLanding(world, pos, fallingBlockEntity);
        if (world instanceof ServerWorld serverWorld) {
            serverWorld.playSound(null, pos, SoundEvents.BLOCK_ANVIL_DESTROY, SoundCategory.BLOCKS, 3f, 1.2f);
        }
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    private static final VoxelShape SHAPE_BASE = Block.createCuboidShape(2, 0, 2, 14, 10, 14);
    private static final VoxelShape SHAPE_N = Block.createCuboidShape(2, 10, 0, 14, 14, 16);
    private static final VoxelShape SHAPE_E = Block.createCuboidShape(0, 10, 2, 16, 14, 14);
    private static final VoxelShape SHAPE_S = Block.createCuboidShape(2, 10, 0, 14, 14, 16);
    private static final VoxelShape SHAPE_W = Block.createCuboidShape(0, 10, 2, 16, 14, 14);
}
