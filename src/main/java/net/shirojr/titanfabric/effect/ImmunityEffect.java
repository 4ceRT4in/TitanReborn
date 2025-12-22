package net.shirojr.titanfabric.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.shirojr.titanfabric.init.TitanFabricStatusEffects;
import net.shirojr.titanfabric.util.LoggerUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ImmunityEffect extends StatusEffect {
    private static final Map<UUID, StatusEffect> entityBlockedEffect = new ConcurrentHashMap<>();

    public ImmunityEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Nullable
    public static StatusEffect getBlockedEffects(UUID uuid) {
        return entityBlockedEffect.get(uuid);
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity.getWorld().isClient()) return false;

        StatusEffectInstance immunityEffect = entity.getStatusEffect(TitanFabricStatusEffects.IMMUNITY);
        if (immunityEffect != null && immunityEffect.getDuration() <= 1) {
            UUID uuid = entity.getUuid();
            resetImmunity(entity);
            LoggerUtil.devLogger("Immunity expired for " + uuid + ". Cleared immunity effect");
        }

        return false;
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration <= 1;
    }


    public static void checkAndBlockNegativeEffect(LivingEntity entity, StatusEffectInstance newEffect) {
        if (entity.getWorld() == null || entity.getWorld().isClient) return;
        StatusEffectInstance immunityEffect = entity.getStatusEffect(TitanFabricStatusEffects.IMMUNITY);

        if (immunityEffect != null && newEffect.getEffectType().value().getCategory() == StatusEffectCategory.HARMFUL) {
            UUID uuid = entity.getUuid();
            StatusEffect blocked = entityBlockedEffect.get(uuid);

            if (blocked != null) {
                if (blocked == newEffect.getEffectType().value()) {
                    entity.removeStatusEffect(newEffect.getEffectType());
                    LoggerUtil.devLogger("Immunity for " + uuid + " blocked known effect: " + newEffect.getEffectType().value().getTranslationKey());
                }
            } else {
                entityBlockedEffect.put(uuid, newEffect.getEffectType().value());
                entity.removeStatusEffect(newEffect.getEffectType());
                LoggerUtil.devLogger("Immunity for " + uuid + " now protects against new effect: " + newEffect.getEffectType().value().getTranslationKey());
            }
        }
    }

    public static void resetImmunity(LivingEntity entity) {
        UUID uuid = entity.getUuid();
        entityBlockedEffect.remove(uuid);
        LoggerUtil.devLogger("Immunity reset for " + uuid + ". Cleared immunity effect");
    }

    @Override
    public void onRemoved(AttributeContainer attributeContainer) {
        super.onRemoved(attributeContainer);
    }
}