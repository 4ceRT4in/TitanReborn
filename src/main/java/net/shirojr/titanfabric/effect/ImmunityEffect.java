package net.shirojr.titanfabric.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.shirojr.titanfabric.util.LoggerUtil;

import java.util.Objects;

public class ImmunityEffect extends StatusEffect {
    private boolean hasBlockedEffect = false;

    protected ImmunityEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return false;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
    }

    public static void checkAndBlockNegativeEffect(LivingEntity entity, StatusEffectInstance newEffect) {
        if(entity.getWorld() != null && !entity.getWorld().isClient) {
            StatusEffectInstance immunityEffect = entity.getStatusEffect(TitanFabricStatusEffects.IMMUNITY);

            if (immunityEffect != null) {
                ImmunityEffect immunityInstance = (ImmunityEffect) immunityEffect.getEffectType();

                if (!immunityInstance.hasBlockedEffect && newEffect.getEffectType().getCategory() == StatusEffectCategory.HARMFUL) {
                    immunityInstance.hasBlockedEffect = true;

                    entity.removeStatusEffect(newEffect.getEffectType());
                    LoggerUtil.devLogger("Immunity blocked effects: " + newEffect.getEffectType().getTranslationKey());
                }
            }
        }
    }


    @Override
    public void onRemoved(LivingEntity entity, net.minecraft.entity.attribute.AttributeContainer attributes, int amplifier) {
        super.onRemoved(entity, attributes, amplifier);
        hasBlockedEffect = false;
    }
}