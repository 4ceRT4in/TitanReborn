package net.shirojr.titanfabric.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.shirojr.titanfabric.util.LoggerUtil;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ImmunityEffect extends StatusEffect {
    private static final Map<UUID, StatusEffect> entityBlockedEffect = new HashMap<>();

    protected ImmunityEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Nullable
    public static StatusEffect getBlockedEffects(UUID uuid) {
        return entityBlockedEffect.get(uuid);
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
                StatusEffect blocked = entityBlockedEffect.get(uuid);

                if (blocked != null) {
                    if (blocked == newEffect.getEffectType()) {
                        entity.removeStatusEffect(newEffect.getEffectType());
                        //LoggerUtil.devLogger("Immunity for " + uuid + " blocked known effect: " + newEffect.getEffectType().getTranslationKey());
                    }
                } else {
                    entityBlockedEffect.put(uuid, newEffect.getEffectType());
                    entity.removeStatusEffect(newEffect.getEffectType());
                    //LoggerUtil.devLogger("Immunity for " + uuid + " now protects against new effect: " + newEffect.getEffectType().getTranslationKey());
                }
            }
        }
    }

    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        UUID uuid = entity.getUuid();
        if (!entity.hasStatusEffect(TitanFabricStatusEffects.IMMUNITY)) {
            entityBlockedEffect.remove(uuid);
            //LoggerUtil.devLogger("Immunity expired for " + uuid + ". Cleared immunity effect");
        }
    }

    public static void resetImmunity(LivingEntity entity) {
        UUID uuid = entity.getUuid();
        entityBlockedEffect.remove(uuid);
        //LoggerUtil.devLogger("Immunity reset for " + uuid + ". Cleared immunity effect");
    }
}