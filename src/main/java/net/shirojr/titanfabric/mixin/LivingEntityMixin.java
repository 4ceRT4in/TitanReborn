package net.shirojr.titanfabric.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.shirojr.titanfabric.item.custom.armor.CitrinArmorItem;
import net.shirojr.titanfabric.item.custom.armor.NetherArmorItem;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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
    private Map<StatusEffect, StatusEffectInstance> activeStatusEffects;

    @Shadow
    protected abstract void onStatusEffectApplied(StatusEffectInstance effect, @Nullable Entity source);

    @Inject(at = @At("HEAD"), method = "damage", cancellable = true)
    private void titanfabric$damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        List<DamageSource> handledDamageTypes = List.of(DamageSource.IN_FIRE,
                DamageSource.ON_FIRE,
                DamageSource.HOT_FLOOR,
                DamageSource.MAGIC, // POISON & INSTANT DAMAGE
                DamageSource.WITHER);

        if (!source.getName().equals("lava") && !handledDamageTypes.contains(source)) return;
        if (!(((LivingEntity) (Object) this) instanceof PlayerEntity player)) return;

        List<Item> armorSet = IntStream.rangeClosed(0, 3)
                .mapToObj(player.getInventory()::getArmorStack)
                .map(ItemStack::getItem).toList();


        for (var item : armorSet) {
            if (item instanceof NetherArmorItem) {
                if (player.getWorld().getRandom().nextInt(4) < 1) {
                    if (source.getName().equals("lava") || source == DamageSource.ON_FIRE || source == DamageSource.IN_FIRE) {
                        //player.extinguish();
                        cir.setReturnValue(false);
                    }
                }
            }
            if (item instanceof CitrinArmorItem) {
                if (player.getRandom().nextInt(4) < 1) return;

                if (source.equals(DamageSource.WITHER) || source.equals(DamageSource.MAGIC)) {
                    cir.setReturnValue(false);
                    return;
                }
            }
        }
    }

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

        int effectDuration = effect.getDuration();
        for (Item entry : armorSet) {
            if (entry instanceof CitrinArmorItem) {
                effectDuration /= 4;
            }
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
}
