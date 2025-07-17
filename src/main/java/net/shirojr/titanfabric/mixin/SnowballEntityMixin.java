package net.shirojr.titanfabric.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SnowballEntity.class)
public abstract class SnowballEntityMixin extends ThrownItemEntity {
    public SnowballEntityMixin(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "onEntityHit", at = @At("TAIL"))
    protected void onEntityHit(EntityHitResult entityHitResult, CallbackInfo ci) {
        var entity = entityHitResult.getEntity();
        var owner = getOwner();
        if(entity == null) return;
        if(owner == null) return;


        if(entity instanceof LivingEntity livingEntity && owner instanceof PlayerEntity player) {
            DamageSource damageSource = owner.getDamageSources().playerAttack(player);
            float k = livingEntity.getKnockbackAgainst(entity, damageSource) + (0.0F);
            livingEntity.damage(damageSource, 0.0f);
            livingEntity.takeKnockback((double)(k * 0.5F), (double) MathHelper.sin(player.getYaw() * ((float)Math.PI / 180F)), (double)(-MathHelper.cos(player.getYaw() * ((float)Math.PI / 180F))));
        }
    }
}
