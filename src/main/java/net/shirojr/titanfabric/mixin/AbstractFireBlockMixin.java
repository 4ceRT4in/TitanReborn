package net.shirojr.titanfabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shirojr.titanfabric.TitanFabricClient;
import net.shirojr.titanfabric.access.EntityAccessor;
import net.shirojr.titanfabric.util.items.ArmorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFireBlock.class)
public abstract class AbstractFireBlockMixin extends Block {

    public AbstractFireBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "onEntityCollision", at = @At("HEAD"))
    private void onEntityCollisionMixin(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        boolean soulFire = state.isOf(Blocks.SOUL_FIRE);
        boolean fire = state.isOf(Blocks.FIRE);

        if (world.isClient) {
            handleClientSide(soulFire, fire, entity);
        }

        if (!world.isClient) {
            if (soulFire) {
                ((EntityAccessor) entity).titanfabric$setSoulBurning(true);
            } else if (fire) {
                ((EntityAccessor) entity).titanfabric$setSoulBurning(false);
            }
        }
    }

    @WrapOperation(
            method = "onEntityCollision",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setFireTicks(I)V")
    )
    private void titanfabric$setSoulFireTicks(
            Entity instance,
            int fireTicks,
            Operation<Void> original,
            BlockState state,
            World world,
            BlockPos pos,
            Entity entity
    ) {
        if (state.isOf(Blocks.SOUL_FIRE)) {
            if (entity instanceof PlayerEntity player && player.getAbilities().creativeMode) {
                original.call(instance, 1);
                return;
            }
            if (fireTicks > 0) {
                fireTicks = Math.max(fireTicks, titanfabric$getSoulFireBaseTicks(entity));
            }
        }
        original.call(instance, fireTicks);
    }

    @WrapOperation(
            method = "onEntityCollision",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setOnFireFor(F)V")
    )
    private void titanfabric$skipVanillaSoulFireOnFireFor(
            Entity instance,
            float seconds,
            Operation<Void> original,
            BlockState state,
            World world,
            BlockPos pos,
            Entity entity
    ) {
        if (state.isOf(Blocks.SOUL_FIRE)) {
            return;
        }
        original.call(instance, seconds);
    }

    @Unique
    private int titanfabric$getSoulFireBaseTicks(Entity entity) {
        int baseTicks = 320;
        if (!(entity instanceof LivingEntity livingEntity)) {
            return baseTicks;
        }

        int emberArmorCount = ArmorHelper.getEmberArmorCount(livingEntity);
        if (emberArmorCount <= 0) {
            return baseTicks;
        }

        float durationMultiplier = Math.max(0.0f, 1.0f - (0.25f * emberArmorCount));
        return Math.max(0, Math.round(baseTicks * durationMultiplier));
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
