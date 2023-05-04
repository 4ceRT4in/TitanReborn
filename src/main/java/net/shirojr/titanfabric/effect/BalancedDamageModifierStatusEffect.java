package net.shirojr.titanfabric.effect;

import net.minecraft.entity.effect.DamageModifierStatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.shirojr.titanfabric.init.ConfigInit;

public class BalancedDamageModifierStatusEffect extends DamageModifierStatusEffect {
    public BalancedDamageModifierStatusEffect(StatusEffectCategory category, int color) {
        super(category, color, ConfigInit.CONFIG.weaknessStatusEffectModifier);
    }
}
