package net.shirojr.titanfabric.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shirojr.titanfabric.TitanFabricClient;
import net.shirojr.titanfabric.access.EntityAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFireBlock.class)
public abstract class AbstractFireBlockMixin {

    @Inject(method = "onEntityCollision", at = @At("HEAD"))
    private void onEntityCollisionMixin(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        boolean soulFire = state.isOf(Blocks.SOUL_FIRE);
        boolean fire = state.isOf(Blocks.FIRE);

        if (world.isClient) {
            handleClientSide(soulFire, fire, entity);
        }

        if (!entity.isFireImmune() && !world.isClient) {
            if(soulFire) {
                ((EntityAccessor) entity).titanfabric$setSoulBurning(true);
                entity.setFireTicks(99999);
                entity.setOnFireFor(99999);
            } else {
                ((EntityAccessor) entity).titanfabric$setSoulBurning(false);
            }
        }
    }

    @Unique
    @Environment(EnvType.CLIENT)
    private void handleClientSide(boolean soulFire, boolean fire, Entity entity) {
        if (soulFire) {
            TitanFabricClient.SOUL_FIRE_ENTITIES.add(entity.getUuid());
        } else if (fire) {
            TitanFabricClient.SOUL_FIRE_ENTITIES.remove(entity.getUuid());
        }
    }
}
