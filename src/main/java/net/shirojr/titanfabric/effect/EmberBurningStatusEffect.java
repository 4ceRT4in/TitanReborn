package net.shirojr.titanfabric.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

/** Effect used by the Ember barrel's lingering cloud. */
public class EmberBurningStatusEffect extends StatusEffect {
    public EmberBurningStatusEffect() {
        super(StatusEffectCategory.HARMFUL, 0xFF5A19);
    }

    @Override
    public void onApplied(LivingEntity entity, int amplifier) {
        super.onApplied(entity, amplifier);
        if (!entity.getWorld().isClient()) entity.setOnFireFor(10);
    }
}
