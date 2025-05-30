package net.shirojr.titanfabric.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffects;

import java.util.Objects;

public class FrostburnEffect extends StatusEffect {

    protected FrostburnEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration % 20 == 0;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        int totalDuration = Objects.requireNonNull(entity.getStatusEffect(TitanFabricStatusEffects.FROSTBURN)).getDuration();
        int maxDuration = 500;

        boolean isFirstHalf = totalDuration > maxDuration / 2;

        if (isFirstHalf) {
            entity.damage(DamageSource.MAGIC, 1.0f);
        } else {
            if (entity.getHealth() < entity.getMaxHealth()) {
                entity.heal(1.0f);
            }
        }
    }
}
