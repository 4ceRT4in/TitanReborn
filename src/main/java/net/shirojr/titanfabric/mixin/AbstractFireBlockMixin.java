package net.shirojr.titanfabric.mixin;

import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoulFireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFireBlock.class)
public abstract class AbstractFireBlockMixin {

    @Inject(method = "onEntityCollision", at = @At("HEAD"))
    private void onEntityCollisionMixin(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (state.isOf(Blocks.SOUL_FIRE) && !entity.isFireImmune()) {
            entity.setFireTicks(99999);
            entity.setOnFireFor(99999);
        }
    }
}
