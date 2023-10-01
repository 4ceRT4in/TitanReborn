package net.shirojr.titanfabric.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shirojr.titanfabric.TitanFabric;

public class TitanFabricStatusEffects {
    public static StatusEffect INDESTRUCTIBILITY;

    private static StatusEffect registerStatusEffect(String name, StatusEffect statusEffect) {
        return Registry.register(Registry.STATUS_EFFECT, new Identifier(TitanFabric.MODID, name), statusEffect);
    }

    public static void registerStatusEffects() {
        INDESTRUCTIBILITY = registerStatusEffect("indestructibility", new IndestructibileStatusEffect(StatusEffectCategory.BENEFICIAL, 0x8379E0));
    }
}
