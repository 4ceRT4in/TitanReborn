package net.shirojr.titanfabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.Vec3d;
import net.shirojr.titanfabric.access.HealthAccessor;
import net.shirojr.titanfabric.access.StatusEffectInstanceAccessor;
import net.shirojr.titanfabric.cca.component.DiamondAbsorptionComponent;
import net.shirojr.titanfabric.cca.component.ExtendedInventoryComponent;
import net.shirojr.titanfabric.effect.ImmunityEffect;
import net.shirojr.titanfabric.init.TitanFabricDamageTypes;
import net.shirojr.titanfabric.init.TitanFabricGamerules;
import net.shirojr.titanfabric.init.TitanFabricStatusEffects;
import net.shirojr.titanfabric.item.custom.TitanFabricSwordItem;
import net.shirojr.titanfabric.item.custom.misc.ParachuteItem;
import net.shirojr.titanfabric.util.effects.DiamondAbsorptionHelper;
import net.shirojr.titanfabric.util.items.ArmorHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements HealthAccessor {
    @Shadow
    public abstract boolean canHaveStatusEffect(StatusEffectInstance effect);

    @Shadow
    protected abstract void onStatusEffectUpgraded(StatusEffectInstance effect, boolean reapplyEffect, @Nullable Entity source);

    @Shadow
    @Final
    private Map<RegistryEntry<StatusEffect>, StatusEffectInstance> activeStatusEffects;

    @Shadow
    protected abstract void onStatusEffectApplied(StatusEffectInstance effect, @Nullable Entity source);

    @Shadow
    public abstract void setHealth(float health);

    @Shadow
    public abstract float getHealth();

    @Shadow
    public abstract float getMaxHealth();

    @Inject(method = "onStatusEffectApplied", at = @At("TAIL"), cancellable = true)
    private void onStatusEffectApplied(StatusEffectInstance effect, Entity source, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        StatusEffect statusEffect = effect.getEffectType().value();
        if (statusEffect.getCategory() != StatusEffectCategory.HARMFUL) return;
        ImmunityEffect.checkAndBlockNegativeEffect(self, effect);
        if (ImmunityEffect.getBlockedEffects(self.getUuid()) == statusEffect) {
            self.removeStatusEffect(effect.getEffectType());
            ci.cancel();
        }
    }

    @Unique
    @Nullable
    private Float titanfabric$restorePoint = null;

    @Override
    public void titanfabric$setRestorePoint(Float restorePoint) {
        this.titanfabric$restorePoint = restorePoint;
    }

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void titanfabric$tick(CallbackInfo callback) {
        if (this.titanfabric$restorePoint != null) {
            if (this.titanfabric$restorePoint > 0 && this.titanfabric$restorePoint > this.getHealth()) {
                this.setHealth(this.titanfabric$restorePoint);
            }
            this.titanfabric$setRestorePoint(null);
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    private void titanfabric$readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("Health", NbtElement.NUMBER_TYPE)) {
            final float nbtHealth = nbt.getFloat("Health");

            if (nbtHealth > this.getMaxHealth() && nbtHealth > 0) {
                titanfabric$restorePoint = nbtHealth;
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

        List<RegistryEntry<StatusEffect>> blockedEffectList = List.of(StatusEffects.BLINDNESS, StatusEffects.POISON, StatusEffects.WEAKNESS, StatusEffects.WITHER, StatusEffects.SLOWNESS);

        if (!blockedEffectList.contains(effect.getEffectType())) return;


        int effectDuration = switch (ArmorHelper.getCitrinArmorCount(player)) {
            case 1 -> (int) (effect.getDuration() * 0.75);
            case 2 -> (int) (effect.getDuration() * 0.5);
            case 3 -> (int) (effect.getDuration() * 0.25);
            case 4 -> 0;
            default -> effect.getDuration();
        };

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
        if (newStatusEffectInstance != null && statusEffectInstance.upgrade(newStatusEffectInstance)) {
            onStatusEffectUpgraded(statusEffectInstance, true, source);
            cir.setReturnValue(true);
        } else if (statusEffectInstance.upgrade(effect)) {
            onStatusEffectUpgraded(statusEffectInstance, true, source);
            cir.setReturnValue(true);
        }
        cir.setReturnValue(false);
    }

    @WrapOperation(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getFinalGravity()D"))
    private double modifyGravityForParachute(LivingEntity instance, Operation<Double> original) {
        if (instance.getVelocity().getY() >= 0 || !ParachuteItem.isParachuteActivated(instance)) {
            return original.call(instance);
        }
        instance.onLanding();
        return 0.002;
    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void titanfabric$reduceEmberDamageTickChance(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.getWorld() == null || entity.getWorld().isClient()) return;
        if (amount <= 0) return;

        int emberArmorCount = ArmorHelper.getEmberArmorCount(entity);
        if (emberArmorCount <= 0) return;
        if (!titanfabric$isEmberTickAffectedDamage(source)) return;

        float avoidChance = Math.min(1.0f, 0.25f * emberArmorCount);
        if (entity.getRandom().nextFloat() < avoidChance) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "onStatusEffectRemoved", at = @At("TAIL"))
    private void titanfabric$onStatusEffectRemoved(StatusEffectInstance effect, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.getWorld().isClient()) return;
        if (effect.getEffectType().equals(TitanFabricStatusEffects.DIAMOND_ABSORPTION)) {
            DiamondAbsorptionComponent.get(self).setDiamondAbsorptionAmount(0.0f, true);
            if (self.getAbsorptionAmount() <= 0.01f) {
                DiamondAbsorptionHelper.clearMaxAbsorptionModifier(self);
            }
            return;
        }
        if (!effect.getEffectType().equals(TitanFabricStatusEffects.IMMUNITY)) return;
        ImmunityEffect.resetImmunity(self);
    }

    @ModifyVariable(method = "damage", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float modifyDamageAmount(float amount, DamageSource source) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.getWorld() == null || entity.getWorld().isClient() || source == null) return amount;

        int emberArmorCount = ArmorHelper.getEmberArmorCount(entity);
        if (emberArmorCount <= 0) return amount;
        if (!source.isOf(net.minecraft.entity.damage.DamageTypes.LAVA)) return amount;

        float damageMultiplier = Math.max(0.0f, 1.0f - (0.25f * emberArmorCount));
        return amount * damageMultiplier;
    }

    @Unique
    private boolean titanfabric$isEmberTickAffectedDamage(@Nullable DamageSource source) {
        if (source == null) return false;
        return source.isOf(net.minecraft.entity.damage.DamageTypes.LAVA)
                || source.isOf(net.minecraft.entity.damage.DamageTypes.HOT_FLOOR)
                || source.isIn(DamageTypeTags.IS_FIRE);
    }

    @ModifyVariable(method = "applyMovementInput", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private Vec3d titanfabric$applyMovementInputMixin(Vec3d original) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.getVelocity().getY() < 0 && ParachuteItem.isParachuteActivated(entity)) {
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
        if (entity instanceof PlayerEntity player) {
            if (!player.getWorld().isClient() && player.getWorld().getGameRules().getBoolean(TitanFabricGamerules.LEGACY_COMBAT)) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "blockedByShield", at = @At("HEAD"))
    private void blockedByShield(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (source.getAttacker() instanceof PlayerEntity attacker) {
            if (self instanceof PlayerEntity target) {
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
                        target.disableShield();
                    }
                }

            }
        }
    }

    @ModifyVariable(method = "setOnFireForTicks", at = @At("HEAD"), argsOnly = true)
    private int handleFireTicksForArmor(int ticks) {
        if (ticks <= 0) return ticks;

        int emberArmorCount = ArmorHelper.getEmberArmorCount((LivingEntity) (Object) this);
        if (emberArmorCount <= 0) return ticks;

        float durationMultiplier = Math.max(0.0f, 1.0f - (0.25f * emberArmorCount));
        return Math.max(0, Math.round(ticks * durationMultiplier));
    }

    @Inject(method = "dropInventory", at = @At("HEAD"))
    private void dropExtendedInventories(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        ExtendedInventoryComponent entityExtendedInventory = ExtendedInventoryComponent.getEntity(entity);
        if (entityExtendedInventory.shouldDropInventory()) {
            ItemScatterer.spawn(entity.getWorld(), entity, entityExtendedInventory.getInventory());
            entityExtendedInventory.getInventory().clear();
            entityExtendedInventory.sync();
        }

        Team team = entity.getScoreboardTeam();
        if (team == null) return;
        ExtendedInventoryComponent teamExtendedInventory = ExtendedInventoryComponent.getTeam(team);
        if (teamExtendedInventory.shouldDropInventory()) {
            ItemScatterer.spawn(entity.getWorld(), entity, teamExtendedInventory.getInventory());
            teamExtendedInventory.getInventory().clear();
            teamExtendedInventory.sync();
        }
    }

    @ModifyConstant(method = "modifyAppliedDamage", constant = @Constant(intValue = 5))
    private int modifyAppliedDamageInt5(int original) {
        return 3;
    }

    @ModifyConstant(method = "modifyAppliedDamage", constant = @Constant(intValue = 25))
    private int modifyAppliedDamageInt25(int original) {
        return 20;
    }

    @ModifyConstant(method = "modifyAppliedDamage", constant = @Constant(floatValue = 25.0F))
    private float modifyAppliedDamageFloat25(float original) {
        return 20.0F;
    }

    @WrapOperation(method = "applyDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setAbsorptionAmount(F)V"))
    private void absorptionFrostburnBypass(LivingEntity instance, float absorptionAmount, Operation<Void> original, @Local(argsOnly = true) DamageSource source) {
        if (source.isOf(TitanFabricDamageTypes.FROSTBURN.get())) return;

        float totalAbsorptionBefore = instance.getAbsorptionAmount();
        float diamondAbsorptionBefore = Math.min(DiamondAbsorptionComponent.get(instance).getDiamondAbsorptionAmount(), totalAbsorptionBefore);
        if (diamondAbsorptionBefore <= 0.01f) {
            original.call(instance, absorptionAmount);
            return;
        }

        float yellowAbsorptionBefore = Math.max(0.0f, totalAbsorptionBefore - diamondAbsorptionBefore);
        float absorbedAmount = Math.max(0.0f, totalAbsorptionBefore - absorptionAmount);
        float convertedDiamondAmount = Math.max(0.0f, Math.min(diamondAbsorptionBefore, absorbedAmount - yellowAbsorptionBefore));

        original.call(instance, absorptionAmount + convertedDiamondAmount);
        DiamondAbsorptionHelper.updateDiamondAbsorptionAfterDamage(instance, diamondAbsorptionBefore - convertedDiamondAmount);
    }

    @Debug(export = true)
    @Inject(method = "getEquipmentChanges",
            at = @At(
                    value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;applyAttributeModifiers(Lnet/minecraft/entity/EquipmentSlot;Ljava/util/function/BiConsumer;)V",
                    ordinal = 0,
                    shift = At.Shift.AFTER
            )
    )
    private void afterEquipmentChanges(CallbackInfoReturnable<Map<EquipmentSlot, ItemStack>> cir) {
        /*LivingEntity livingEntity = (LivingEntity) (Object) this;
        FrostburnComponent frostburnComponent = FrostburnComponent.get(livingEntity);
        frostburnComponent.setPhase(FrostburnComponent.Phase.INCREASE);*/
    }
}
