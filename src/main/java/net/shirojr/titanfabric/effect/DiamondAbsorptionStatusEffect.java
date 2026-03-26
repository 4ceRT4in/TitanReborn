package net.shirojr.titanfabric.effect;

import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.shirojr.titanfabric.util.effects.DiamondAbsorptionHelper;

public class DiamondAbsorptionStatusEffect extends StatusEffect {
    public DiamondAbsorptionStatusEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void onApplied(AttributeContainer attributeContainer, int amplifier) {
        super.onApplied(attributeContainer, amplifier);
        DiamondAbsorptionHelper.applyMaxAbsorptionModifier(attributeContainer, DiamondAbsorptionHelper.getAbsorptionAmount(amplifier));
    }
}
