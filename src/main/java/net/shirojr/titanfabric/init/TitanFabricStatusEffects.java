package net.shirojr.titanfabric.init;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.effect.FrostburnStatusEffect;
import net.shirojr.titanfabric.effect.ImmunityEffect;
import net.shirojr.titanfabric.effect.IndestructibileStatusEffect;
import net.shirojr.titanfabric.effect.SafeFallingStatusEffect;

public interface TitanFabricStatusEffects {
    RegistryEntry<StatusEffect> INDESTRUCTIBILITY = registerStatusEffect(
            "indestructibility",
            new IndestructibileStatusEffect(StatusEffectCategory.BENEFICIAL, 0x8379E0)
    );
    RegistryEntry<StatusEffect> FROSTBURN = registerStatusEffect(
            "frostburn",
            new FrostburnStatusEffect(StatusEffectCategory.HARMFUL, 0x9DBFE8)
    );
    RegistryEntry<StatusEffect> IMMUNITY = registerStatusEffect(
            "immunity",
            new ImmunityEffect(StatusEffectCategory.BENEFICIAL, 0xC3FF00)
    );
    RegistryEntry<StatusEffect> SAFE_FALLING = registerStatusEffect(
            "safe_falling",
            new SafeFallingStatusEffect(StatusEffectCategory.BENEFICIAL, 0x34ABEB)
    );

    private static RegistryEntry<StatusEffect> registerStatusEffect(String name, StatusEffect statusEffect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, TitanFabric.getId(name), statusEffect);
    }

    static void initialize() {
        // static initialisation
    }
}
