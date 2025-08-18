package net.shirojr.titanfabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.World;
import net.shirojr.titanfabric.TitanFabricClient;
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
    private boolean titan$soulBurning;

    @Override
    public boolean titanfabric$isSoulBurning() {
        return this.titan$soulBurning;
    }

    @Override
    public void titanfabric$setSoulBurning(boolean value) {
        this.titan$soulBurning = value;
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
        return this.titan$soulBurning ? 2.0F : amount;
    }

    @Inject(method = "baseTick", at = @At("TAIL"))
    private void baseTick(CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (self.getFireTicks() <= 0) {
            this.titan$soulBurning = false;
        }
    }

    @ModifyExpressionValue(method = "doesRenderOnFire", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isOnFire()Z"))
    private boolean isOnFire(boolean original) {
        if (!((Entity) (Object) this instanceof LivingEntity livingEntity)) return original;
        return original && ArmorHelper.getEmberArmorCount(livingEntity) < 4;
    }

    @Inject(method = "updateMovementInFluid", at = @At("HEAD"))
    private void updateMovementInFluid(TagKey<Fluid> tag, double speed, CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity)(Object)this;
        World world = entity.getWorld();
        if(!world.isClient()) return;
        if (!(world.getFluidState(entity.getBlockPos()).getFluid() == Fluids.LAVA || world.getFluidState(entity.getBlockPos()).getFluid() == Fluids.FLOWING_LAVA)) return;
        if (world.isClient()) {
            handleClientSide(entity);
        }
    }

    @Unique
    @Environment(EnvType.CLIENT)
    private void handleClientSide(Entity entity) {
        TitanFabricClient.SOUL_FIRE_ENTITIES.remove(entity.getUuid());
    }
}
