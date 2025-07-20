package net.shirojr.titanfabric.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.SoulFireBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoulFireBlock.class)
public abstract class SoulFireBlockMixin {

    @Inject(method = "canPlaceAt", at = @At("HEAD"), cancellable = true)
    private void allowPlacementAnywhere(BlockState state, WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }
}
