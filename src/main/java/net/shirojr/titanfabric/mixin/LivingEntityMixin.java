package net.shirojr.titanfabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.shirojr.titanfabric.access.StatusEffectInstanceAccessor;
import net.shirojr.titanfabric.cca.component.ExtendedInventoryComponent;
import net.shirojr.titanfabric.effect.ImmunityEffect;
import net.shirojr.titanfabric.init.TitanFabricGamerules;
import net.shirojr.titanfabric.init.TitanFabricItems;
import net.shirojr.titanfabric.item.custom.TitanFabricSwordItem;
import net.shirojr.titanfabric.item.custom.armor.CitrinArmorItem;
import net.shirojr.titanfabric.item.custom.misc.ParachuteItem;
import net.shirojr.titanfabric.util.items.ArmorHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow
    public abstract boolean canHaveStatusEffect(StatusEffectInstance effect);

    @Shadow
    protected abstract void onStatusEffectUpgraded(StatusEffectInstance effect, boolean reapplyEffect, @Nullable Entity source);

    @Shadow
    @Final
    private Map<RegistryEntry<StatusEffect>, StatusEffectInstance> activeStatusEffects;

    @Shadow
    protected abstract void onStatusEffectApplied(StatusEffectInstance effect, @Nullable Entity source);

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

    @Inject(method = "damage", at = @At("HEAD"))
    public void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source != null && source.getAttacker() instanceof LivingEntity attacker && !source.isIn(DamageTypeTags.IS_PROJECTILE)) {
            LivingEntity self = (LivingEntity) (Object) this;

            if (self.isBlocking() && self.getActiveItem().getItem() == TitanFabricItems.NETHERITE_SHIELD) {
                DamageSource damageSource = self.getDamageSources().generic();
                //LoggerUtil.devLogger("works");
                float k = attacker.getKnockbackAgainst(self, damageSource) + (1.25F);
                attacker.takeKnockback(k * 0.5F, MathHelper.sin(self.getYaw() * ((float) Math.PI / 180F)), -MathHelper.cos(self.getYaw() * ((float) Math.PI / 180F)));
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

    @WrapOperation(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getFinalGravity()D"))
    private double modifyGravityForParachute(LivingEntity instance, Operation<Double> original) {
        if (instance.getVelocity().getY() >= 0 || !ParachuteItem.isParachuteActivated(instance)) {
            return original.call(instance);
        }
        instance.onLanding();
        return 0.002;
    }

    @ModifyVariable(method = "damage", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float modifyDamageAmount(float amount, DamageSource source) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.getWorld() != null && !entity.getWorld().isClient() && source != null) {
            if (source.isIn(DamageTypeTags.IS_FIRE)) {
                int totalArmor = 0;
                for (ItemStack armorStack : entity.getArmorItems()) {
                    if (!armorStack.isEmpty() && armorStack.getItem() instanceof ArmorItem armorItem) {
                        totalArmor += armorItem.getProtection();
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

    @Inject(method = "blockedByShield", at = @At("HEAD"), cancellable = true)
    private void blockedByShield(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (self.getWorld().getGameRules().getBoolean(TitanFabricGamerules.LEGACY_COMBAT)) {
            cir.setReturnValue(false);
        }

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
                        float f = 0.25F;
                        if (target.getRandom().nextFloat() < f) {
                            target.getItemCooldownManager().set(target.getActiveItem().getItem(), 100);
                            target.clearActiveItem();
                            target.getWorld().sendEntityStatus(target, (byte) 30);
                        }
                    }
                }

            }
        }
    }

    @ModifyVariable(method = "setOnFireForTicks", at = @At("HEAD"), argsOnly = true)
    private int handleFireTicksForArmor(int ticks) {
        if (ticks > 0) {
            int netherArmorCount = ArmorHelper.getEmberArmorCount((LivingEntity) (Object) this);
            if (netherArmorCount > 0) {
                float seconds = ticks * 0.05f;
                ticks = netherArmorCount == 4 ? 0 : (int) (seconds * netherArmorCount * 0.25f);
            }
        }
        return ticks;
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
        return 1;
    }

    @ModifyConstant(method = "modifyAppliedDamage", constant = @Constant(intValue = 25))
    private int modifyAppliedDamageInt25(int original) {
        return 10;
    }

    @ModifyConstant(method = "modifyAppliedDamage", constant = @Constant(floatValue = 25.0F))
    private float modifyAppliedDamageFloat25(float original) {
        return 10.0F;
    }
}
