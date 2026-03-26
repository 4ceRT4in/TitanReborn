package net.shirojr.titanfabric.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.shirojr.titanfabric.util.effects.RecoveryProfile;

public class RecoveryStatusEffect extends StatusEffect {
    private final RecoveryProfile profile;

    public RecoveryStatusEffect(StatusEffectCategory category, int color, RecoveryProfile profile) {
        super(category, color);
        this.profile = profile;
    }

    public RecoveryProfile getProfile() {
        return profile;
    }
}
