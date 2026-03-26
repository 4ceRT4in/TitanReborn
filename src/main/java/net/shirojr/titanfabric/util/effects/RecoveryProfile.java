package net.shirojr.titanfabric.util.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.shirojr.titanfabric.effect.RecoveryStatusEffect;

public enum RecoveryProfile {
    BASE("base", 6.0f, 1.0f, 100, 1.0f, 10, 12.0f),
    STRONG("strong", 12.0f, 1.0f, 100, 1.0f, 10, 6.0f),
    LONG("long", 6.0f, 1.0f, 100, 1.0f, 20, 12.0f);

    private final String id;
    private final float maxBufferAmount;
    private final float refillAmount;
    private final int refillIntervalTicks;
    private final float healAmount;
    private final int healIntervalTicks;
    private final float triggerThresholdHealth;

    RecoveryProfile(String id, float maxBufferAmount, float refillAmount, int refillIntervalTicks,
                    float healAmount, int healIntervalTicks, float triggerThresholdHealth) {
        this.id = id;
        this.maxBufferAmount = maxBufferAmount;
        this.refillAmount = refillAmount;
        this.refillIntervalTicks = refillIntervalTicks;
        this.healAmount = healAmount;
        this.healIntervalTicks = healIntervalTicks;
        this.triggerThresholdHealth = triggerThresholdHealth;
    }

    public String getId() {
        return id;
    }

    public float getMaxBufferAmount() {
        return maxBufferAmount;
    }

    public float getRefillAmount() {
        return refillAmount;
    }

    public int getRefillIntervalTicks() {
        return refillIntervalTicks;
    }

    public float getHealAmount() {
        return healAmount;
    }

    public int getHealIntervalTicks() {
        return healIntervalTicks;
    }

    public float getTriggerThresholdHealth() {
        return triggerThresholdHealth;
    }

    public static RecoveryProfile byId(String id) {
        for (RecoveryProfile profile : values()) {
            if (profile.id.equals(id)) return profile;
        }
        return BASE;
    }

    public static ActiveRecovery getActive(LivingEntity entity) {
        ActiveRecovery selectedRecovery = null;
        for (StatusEffectInstance instance : entity.getStatusEffects()) {
            if (instance.getEffectType().value() instanceof RecoveryStatusEffect recoveryStatusEffect) {
                ActiveRecovery candidate = new ActiveRecovery(recoveryStatusEffect.getProfile(), instance.getDuration(), instance.getEffectType());
                if (selectedRecovery == null
                        || candidate.duration() > selectedRecovery.duration()
                        || (candidate.duration() == selectedRecovery.duration()
                        && candidate.profile().getMaxBufferAmount() > selectedRecovery.profile().getMaxBufferAmount())) {
                    selectedRecovery = candidate;
                }
            }
        }
        return selectedRecovery;
    }

    public record ActiveRecovery(RecoveryProfile profile, int duration, RegistryEntry<StatusEffect> effectType) {
    }
}
