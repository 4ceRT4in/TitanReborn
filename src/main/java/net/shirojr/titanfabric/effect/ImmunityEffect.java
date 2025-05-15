package net.shirojr.titanfabric.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.shirojr.titanfabric.util.LoggerUtil;

import java.util.*;

public class ImmunityEffect extends StatusEffect {
    private static final Map<UUID, Set<StatusEffect>> entityBlockedEffects = new HashMap<>();
    private static final Map<UUID, Integer> entityProtectionSlots = new HashMap<>();

    protected ImmunityEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    public static Set<StatusEffect> getBlockedEffects(UUID uuid) {
        return entityBlockedEffects.getOrDefault(uuid, Collections.emptySet());
    }


    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return false;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
    }

    public static void checkAndBlockNegativeEffect(LivingEntity entity, StatusEffectInstance newEffect) {
        if (entity.getWorld() != null && !entity.getWorld().isClient) {
            StatusEffectInstance immunityEffect = entity.getStatusEffect(TitanFabricStatusEffects.IMMUNITY);

            if (immunityEffect != null && newEffect.getEffectType().getCategory() == StatusEffectCategory.HARMFUL) {
                UUID uuid = entity.getUuid();

                entityBlockedEffects.putIfAbsent(uuid, new HashSet<>());
                entityProtectionSlots.putIfAbsent(uuid, 0);

                Set<StatusEffect> blocked = entityBlockedEffects.get(uuid);
                int availableSlots = entityProtectionSlots.get(uuid);

                if (blocked.contains(newEffect.getEffectType())) {
                    entity.removeStatusEffect(newEffect.getEffectType());
                    LoggerUtil.devLogger("Immunity for " + uuid + " blocked known effect: " + newEffect.getEffectType().getTranslationKey());
                } else if (availableSlots > 0) {
                    blocked.add(newEffect.getEffectType());
                    entityProtectionSlots.put(uuid, availableSlots - 1);
                    entity.removeStatusEffect(newEffect.getEffectType());
                    LoggerUtil.devLogger("Immunity for " + uuid + " now protects against new effect: " + newEffect.getEffectType().getTranslationKey());
                }
            }
        }
    }

    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        UUID uuid = entity.getUuid();

        entityBlockedEffects.putIfAbsent(uuid, new HashSet<>());
        entityProtectionSlots.put(uuid, entityProtectionSlots.getOrDefault(uuid, 0) + 1);
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        UUID uuid = entity.getUuid();
        if (!entity.hasStatusEffect(TitanFabricStatusEffects.IMMUNITY)) {
            entityBlockedEffects.remove(uuid);
            entityProtectionSlots.remove(uuid);
            LoggerUtil.devLogger("Immunity expired for " + uuid + ". Cleared all effects the player was immune to");
        }
    }

    public static void resetImmunity(LivingEntity entity, boolean isDurationExtended) {
        UUID uuid = entity.getUuid();
        entityBlockedEffects.remove(uuid);
        entityProtectionSlots.remove(uuid);
        LoggerUtil.devLogger("Immunity expired for " + uuid + ". Cleared all effects the player was immune to");
    }
}