package net.shirojr.titanfabric.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.shirojr.titanfabric.effect.TitanFabricStatusEffects;
import net.shirojr.titanfabric.item.custom.armor.CitrinArmorItem;
import net.shirojr.titanfabric.item.custom.armor.NetherArmorItem;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

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
    private Map<StatusEffect, StatusEffectInstance> activeStatusEffects;

    @Shadow
    protected abstract void onStatusEffectApplied(StatusEffectInstance effect, @Nullable Entity source);

    @Inject(method = "addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;Lnet/minecraft/entity/Entity;)Z",
            cancellable = true, at = @At("HEAD"))
    private void titanfabric$addStatusEffect(StatusEffectInstance effect, Entity source, CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (LivingEntity) (Object) this;
        if (!(entity instanceof PlayerEntity player)) return;
        if (!canHaveStatusEffect(effect)) return;

        List<StatusEffect> blockedEffectList = List.of(StatusEffects.BLINDNESS, StatusEffects.POISON,
                StatusEffects.WEAKNESS, StatusEffects.WITHER, StatusEffects.SLOWNESS);

        for (var entry : blockedEffectList) {
            if (!blockedEffectList.contains(effect.getEffectType())) return;
        }

        List<Item> armorSet = IntStream.rangeClosed(0, 3)
                .mapToObj(player.getInventory()::getArmorStack)
                .map(ItemStack::getItem).toList();

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

        //TODO: helper class
        switch (itemCounter) {
            case 1 -> effectDuration = (int) (effect.getDuration() * 0.75);
            case 2 -> effectDuration = (int) (effect.getDuration() * 0.5);
            case 3 -> effectDuration = (int) (effect.getDuration() * 0.25);
            case 4 -> effectDuration = 0;
            default -> effectDuration = effect.getDuration();
        }

        StatusEffectInstance statusEffectInstance = activeStatusEffects.get(effect.getEffectType());
        StatusEffectInstance newStatusEffectInstance = new StatusEffectInstance(effect.getEffectType(),
                effectDuration, effect.getAmplifier(), effect.isAmbient(), effect.shouldShowParticles(),
                effect.shouldShowIcon());

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

    @Inject(method = "travel", at = @At(value = "FIELD",
            target = "Lnet/minecraft/entity/effect/StatusEffects;SLOW_FALLING:Lnet/minecraft/entity/effect/StatusEffect;",
            shift = At.Shift.BEFORE, by = 1), locals = LocalCapture.CAPTURE_FAILHARD)
    private void titanfabric$handleSafeLandingEffect(Vec3d movementInput, CallbackInfo ci, double downForce, boolean test) {
        LivingEntity entity = (LivingEntity)(Object) this;
        if (!entity.hasStatusEffect(TitanFabricStatusEffects.SAFE_FALLING) || entity.getVelocity().y > 0.0) return;
        downForce = 0.01;

        entity.onLanding();
    }
}
