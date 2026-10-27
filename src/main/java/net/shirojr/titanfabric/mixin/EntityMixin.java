package net.shirojr.titanfabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.block.Blocks;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.World;
import net.shirojr.titanfabric.access.EntityAccessor;
import net.shirojr.titanfabric.util.items.ArmorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin implements EntityAccessor {

    @Unique
    private static final TrackedData<Boolean> TITANFABRIC_SOUL_BURNING =
            DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BOOLEAN);

    @ModifyArg(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;initDataTracker(Lnet/minecraft/entity/data/DataTracker$Builder;)V"
            ),
            index = 0
    )
    private DataTracker.Builder titanfabric$trackSoulBurning(DataTracker.Builder builder) {
        builder.add(TITANFABRIC_SOUL_BURNING, false);
        return builder;
    }

    @Override
    public boolean titanfabric$isSoulBurning() {
        return ((Entity) (Object) this).getDataTracker().get(TITANFABRIC_SOUL_BURNING);
    }

    @Override
    public void titanfabric$setSoulBurning(boolean value) {
        ((Entity) (Object) this).getDataTracker().set(TITANFABRIC_SOUL_BURNING, value);
    }

    @ModifyArg(
            method = "baseTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
            ),
            index = 1
    )
    private float baseTick(float amount) {
        return titanfabric$isSoulBurning() ? 2.0F : amount;
    }

    @Inject(method = "baseTick", at = @At("HEAD"))
    private void baseTick(CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (self.getWorld().isClient()) return;

        boolean touchingSoulFire = self.getWorld().getBlockState(self.getBlockPos()).isOf(Blocks.SOUL_FIRE);
        if (touchingSoulFire) {
            titanfabric$setSoulBurning(true);
        } else if (!self.isOnFire()) {
            titanfabric$setSoulBurning(false);
        }
    }

    @ModifyExpressionValue(method = "doesRenderOnFire", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isOnFire()Z"))
    private boolean isOnFire(boolean original) {
        Entity self = (Entity) (Object) this;
        boolean soulFire = titanfabric$isSoulBurning();
        if (!(self instanceof LivingEntity livingEntity)) return original || soulFire;
        return (original || soulFire) && ArmorHelper.getEmberArmorCount(livingEntity) < 4;
    }

    @Inject(method = "updateMovementInFluid", at = @At("HEAD"))
    private void updateMovementInFluid(TagKey<Fluid> tag, double speed, CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity)(Object)this;
        World world = entity.getWorld();
        if (world.isClient()) return;
        if (!(world.getFluidState(entity.getBlockPos()).getFluid() == Fluids.LAVA || world.getFluidState(entity.getBlockPos()).getFluid() == Fluids.FLOWING_LAVA)) return;
        titanfabric$setSoulBurning(false);
    }
}
