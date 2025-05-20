package net.shirojr.titanfabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.math.Vec3d;
import net.shirojr.titanfabric.access.StatusEffectInstanceAccessor;
import net.shirojr.titanfabric.effect.ImmunityEffect;
import net.shirojr.titanfabric.item.TitanFabricItems;
import net.shirojr.titanfabric.item.custom.TitanFabricParachuteItem;
import net.shirojr.titanfabric.item.custom.TitanFabricSwordItem;
import net.shirojr.titanfabric.item.custom.armor.CitrinArmorItem;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;
import java.util.stream.IntStream;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow
    public abstract boolean canHaveStatusEffect(StatusEffectInstance effect);

    @Shadow
    protected abstract void onStatusEffectUpgraded(StatusEffectInstance effect, boolean reapplyEffect, @Nullable Entity source);

    @Shadow
    @Final
    private Map<StatusEffect, StatusEffectInstance> activeStatusEffects;

    @Shadow
    protected abstract void onStatusEffectApplied(StatusEffectInstance effect, @Nullable Entity source);

    @Inject(method = "tickStatusEffects", at = @At("HEAD"))
    private void onTickStatusEffects(CallbackInfo ci) {
        LivingEntity self = (LivingEntity)(Object)this;
        for (StatusEffectInstance effectInstance : new ArrayList<>(self.getStatusEffects())) {
            StatusEffect effect = effectInstance.getEffectType();

            if (effect.getCategory() == StatusEffectCategory.HARMFUL) {
                ImmunityEffect.checkAndBlockNegativeEffect(self, effectInstance);
            }
        }
    }

    @Inject(method = "damage", at = @At("HEAD"))
    public void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if(source != null && source.getAttacker() != null && !source.isProjectile()) {
            Entity attacker = source.getAttacker();
            LivingEntity self = (LivingEntity) (Object) this;
            if (!(attacker instanceof LivingEntity attackingEntity)) return;

            if (self.isBlocking() && self.getActiveItem().getItem() == TitanFabricItems.NETHERITE_SHIELD) {
                Vec3d vec1 = attacker.getPos().subtract(self.getPos()).normalize();
                Vec3d vec2 = self.getRotationVec(1.0F).normalize();

                double d = vec2.dotProduct(vec1);

                if (d > 0.5) {
                    attackingEntity.addVelocity(vec1.x , 0.5, vec1.z);
                    attackingEntity.velocityDirty = true;
                }
            }
        }
    }

    @Inject(method = "addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;Lnet/minecraft/entity/Entity;)Z", cancellable = true, at = @At("HEAD"))
    private void titanfabric$addStatusEffect(StatusEffectInstance effect, Entity source, CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (LivingEntity) (Object) this;
        if (!(entity instanceof PlayerEntity player))
            return;
        if (!canHaveStatusEffect(effect))
            return;

        List<StatusEffect> blockedEffectList = List.of(StatusEffects.BLINDNESS, StatusEffects.POISON, StatusEffects.WEAKNESS, StatusEffects.WITHER, StatusEffects.SLOWNESS);

        for (var entry : blockedEffectList) {
            if (!blockedEffectList.contains(effect.getEffectType()))
                return;
        }

        List<Item> armorSet = IntStream.rangeClosed(0, 3).mapToObj(player.getInventory()::getArmorStack).map(ItemStack::getItem).toList();

        if (armorSet.stream().allMatch(item -> item instanceof CitrinArmorItem)) {
            cir.setReturnValue(false);
            return;
        }

        int itemCounter = 0;
        for (Item entry : armorSet) {
            if (entry instanceof CitrinArmorItem) {
                itemCounter++;
            }
        }

        int effectDuration;

        // TODO: helper class
        switch (itemCounter) {
            case 1 -> effectDuration = (int) (effect.getDuration() * 0.75);
            case 2 -> effectDuration = (int) (effect.getDuration() * 0.5);
            case 3 -> effectDuration = (int) (effect.getDuration() * 0.25);
            case 4 -> effectDuration = 0;
            default -> effectDuration = effect.getDuration();
        }

        StatusEffectInstance newStatusEffectInstance = new StatusEffectInstance(
                effect.getEffectType(),
                effectDuration,
                effect.getAmplifier(),
                effect.isAmbient(),
                effect.shouldShowParticles(),
                effect.shouldShowIcon()
        );

        // Copy over the previous effect data if it exists
        StatusEffectInstanceAccessor originalAccessor = (StatusEffectInstanceAccessor) effect;
        StatusEffectInstanceAccessor newAccessor = (StatusEffectInstanceAccessor) newStatusEffectInstance;
        if (originalAccessor.titanfabric$getPreviousStatusEffect() != null) {
            newAccessor.titanfabric$setPreviousStatusEffect(
                    new StatusEffectInstance(originalAccessor.titanfabric$getPreviousStatusEffect())
            );
        }

        StatusEffectInstance statusEffectInstance = activeStatusEffects.get(effect.getEffectType());

        if (statusEffectInstance == null) {
            activeStatusEffects.put(newStatusEffectInstance.getEffectType(), newStatusEffectInstance);
            onStatusEffectApplied(newStatusEffectInstance, source);
            cir.setReturnValue(true);
            return;
        }
        if (statusEffectInstance.upgrade(effect)) {
            onStatusEffectUpgraded(statusEffectInstance, true, source);
            cir.setReturnValue(true);
        }
        cir.setReturnValue(false);
    }
    private boolean titanFabric$renderCrossBowFix(ItemStack instance, Item item, Operation<Boolean> original) {
        boolean originalEvaluation = original.call(instance, item);
        if (!item.equals(Items.CROSSBOW)) return originalEvaluation;
        return originalEvaluation || instance.getItem() instanceof CrossbowItem;
    }
    @ModifyVariable(method = "travel", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/effect/StatusEffects;SLOW_FALLING:Lnet/minecraft/entity/effect/StatusEffect;", shift = At.Shift.BEFORE, by = 1), ordinal = 0)
    private double titanfabric$handleSafeLandingEffect(double original) {
        LivingEntity entity = ((LivingEntity) (Object) this);
        if (entity.getVelocity().getY() < 0 && TitanFabricParachuteItem.isParachuteActivated(entity)) {
            original = 0.005D;
            entity.onLanding();
        }
        return original;
    }

    @ModifyVariable(method = "damage", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float modifyDamageAmount(float amount, DamageSource source) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.getWorld() != null && !entity.getWorld().isClient() && source != null) {
            if (source == DamageSource.IN_FIRE) {
                int totalArmor = 0;
                for (ItemStack armorStack : entity.getArmorItems()) {
                    if (!armorStack.isEmpty() && armorStack.getItem() instanceof ArmorItem armorItem) {
                        totalArmor += armorItem.getMaterial().getProtectionAmount(armorItem.getSlotType());
                    }
                }
                if (totalArmor > 0) {
                    float multiplier = 1.0F - (totalArmor * 0.04F);
                    multiplier = Math.max(0.0F, multiplier);
                    return (amount * multiplier) + 0.5F;
                }
            }
        }
        return amount;
    }


    @ModifyVariable(method = "applyMovementInput", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private Vec3d titanfabric$applyMovementInputMixin(Vec3d original) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.getVelocity().getY() < 0 && TitanFabricParachuteItem.isParachuteActivated(entity)) {
            return new Vec3d(original.getX(), original.getY(), 0.98D);
        }
        return original;
    }

    @Inject(method = "getHandSwingDuration", at = @At("RETURN"), cancellable = true)
    private void titanfabric$matchHandSwingWithItemCooldown(CallbackInfoReturnable<Integer> cir) {
        if (!((LivingEntity) (Object) this instanceof PlayerEntity player)) return;
        ItemStack stack = player.getMainHandStack();
        if (!(stack.getItem() instanceof TitanFabricSwordItem titanFabricSwordItem)) return;
        int defaultCooldown = cir.getReturnValue();
        cir.setReturnValue(defaultCooldown + titanFabricSwordItem.getCooldownTicks());
    }

    @Inject(method = "isPushable", at = @At("HEAD"), cancellable = true)
    private void isPushable(CallbackInfoReturnable<Boolean> cir) {
        if (((LivingEntity) (Object) this) instanceof PlayerEntity) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "pushAwayFrom", at = @At("HEAD"), cancellable = true)
    private void pushAwayFrom(Entity entity, CallbackInfo ci) {
        if (entity instanceof PlayerEntity) {
            ci.cancel();
        }
    }

    @Inject(method = "blockedByShield", at = @At("HEAD"), cancellable = true)
    private void blockedByShield(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if (source.getAttacker() instanceof PlayerEntity attacker) {
            if (((LivingEntity) (Object) this) instanceof PlayerEntity target) {
                // If the target is blocking and holding a shield
                boolean isCrit = attacker.fallDistance > 0.0F
                        && !attacker.isOnGround()
                        && !attacker.isClimbing()
                        && !attacker.isTouchingWater()
                        && !attacker.hasStatusEffect(StatusEffects.BLINDNESS)
                        && !attacker.hasVehicle()
                        && !attacker.isSprinting();
                if (isCrit) {
                    if (target.isBlocking()) {
                        float f = 0.25F;
                        if (target.getRandom().nextFloat() < f) {
                            target.getItemCooldownManager().set(target.getActiveItem().getItem(), 100);
                            target.clearActiveItem();
                            target.world.sendEntityStatus(target, (byte) 30);
                        }
                    }
                }

            }

        }
    }

    @ModifyVariable(method = "applyEnchantmentsToDamage", at = @At(value = "STORE", ordinal = 0), index = 2, argsOnly = true)
    private float applyEnchantmentsToDamage(float amount, DamageSource source, float originalAmount) {
        LivingEntity self = (LivingEntity)(Object)this;
        if (self.hasStatusEffect(StatusEffects.RESISTANCE) && source != DamageSource.OUT_OF_WORLD && !source.isUnblockable()) {
            StatusEffectInstance status = self.getStatusEffect(StatusEffects.RESISTANCE);
            int i = (status.getAmplifier() + 1) * 10;
            int j = 100 - i;
            float f = originalAmount * (float)j / 100.0F;
            amount = Math.max(f, 0.0F);
        }
        return amount;
    }
}
