package net.shirojr.titanfabric.access;

import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.effect.StatusEffectInstance;

public interface StatusEffectInstanceAccessor {

    void titanfabric$setPreviousStatusEffect(@Nullable StatusEffectInstance statusEffectInstance);

    @Nullable
    StatusEffectInstance titanfabric$getPreviousStatusEffect();

    void titanfabric$setDuration(int duration);

}
