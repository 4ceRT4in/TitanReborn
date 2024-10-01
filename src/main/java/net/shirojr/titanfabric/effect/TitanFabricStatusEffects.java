package net.shirojr.titanfabric.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;
import net.shirojr.titanfabric.TitanFabric;

public class TitanFabricStatusEffects {
    public static StatusEffect INDESTRUCTIBILITY;
    public static StatusEffect SAFE_FALLING;

    private static StatusEffect registerStatusEffect(String name, StatusEffect statusEffect) {
        return Registry.register(Registries.STATUS_EFFECT, Identifier.of(TitanFabric.MODID, name), statusEffect);
    }

    public static void registerStatusEffects() {
        INDESTRUCTIBILITY = registerStatusEffect("indestructibility", new IndestructibileStatusEffect(StatusEffectCategory.BENEFICIAL, 0x8379E0));
        SAFE_FALLING = registerStatusEffect("safe_falling", new SafeFallingStatusEffect(StatusEffectCategory.BENEFICIAL, 0x34ABEB));
    }
}
