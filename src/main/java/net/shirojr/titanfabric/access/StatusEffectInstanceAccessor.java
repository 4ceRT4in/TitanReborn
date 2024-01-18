package net.shirojr.titanfabric.access;

import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.effect.StatusEffectInstance;

public interface StatusEffectInstanceAccessor {

    public void setPreviousStatusEffect(@Nullable StatusEffectInstance statusEffectInstance);

    @Nullable
    public StatusEffectInstance getPreviousStatusEffect();

    public void setDuration(int duration);

}
