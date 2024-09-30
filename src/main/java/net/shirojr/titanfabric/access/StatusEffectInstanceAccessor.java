package net.shirojr.titanfabric.access;

import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;

public interface StatusEffectInstanceAccessor {

    void titanfabric$setPreviousStatusEffect(@Nullable StatusEffectInstance statusEffectInstance);

    @Nullable
    StatusEffectInstance titanfabric$getPreviousStatusEffect();

    void titanfabric$setDuration(int duration);

}
