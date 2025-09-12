package net.shirojr.titanfabric.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.shirojr.titanfabric.cca.component.FrostburnComponent;

public class FrostburnStatusEffect extends StatusEffect {

    public FrostburnStatusEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void onApplied(LivingEntity entity, int amplifier) {
        super.onApplied(entity, amplifier);
        FrostburnComponent frostburnComponent = FrostburnComponent.get(entity);
        frostburnComponent.setFrostburnLimit(amplifier, true);
    }
}
