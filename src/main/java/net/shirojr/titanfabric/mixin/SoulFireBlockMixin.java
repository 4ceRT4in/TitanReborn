package net.shirojr.titanfabric.mixin;

import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.block.SoulFireBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.shirojr.titanfabric.util.SoulFireStateHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoulFireBlock.class)
public abstract class SoulFireBlockMixin extends AbstractFireBlock {

    public SoulFireBlockMixin(Settings settings, float damage) {
        super(settings, damage);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(
                FireBlock.AGE,
                FireBlock.NORTH,
                FireBlock.EAST,
                FireBlock.SOUTH,
                FireBlock.WEST,
                FireBlock.UP
        );
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return SoulFireStateHelper.getStateForPosition(context.getWorld(), context.getBlockPos());
    }

    @Inject(method = "canPlaceAt", at = @At("HEAD"), cancellable = true)
    private void canPlaceAt(BlockState state, WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(SoulFireStateHelper.canPlaceAt(world, pos));
    }

    @Inject(method = "getStateForNeighborUpdate", at = @At("HEAD"), cancellable = true)
    private void titanfabric$updateDirectionalState(
            BlockState state,
            net.minecraft.util.math.Direction direction,
            BlockState neighborState,
            WorldAccess world,
            BlockPos pos,
            BlockPos neighborPos,
            CallbackInfoReturnable<BlockState> cir
    ) {
        if (!SoulFireStateHelper.canPlaceAt(world, pos)) {
            cir.setReturnValue(Blocks.AIR.getDefaultState());
            return;
        }

        BlockState updatedState = SoulFireStateHelper.getStateForPosition(world, pos);
        cir.setReturnValue(updatedState.with(FireBlock.AGE, state.get(FireBlock.AGE)));
    }
}
