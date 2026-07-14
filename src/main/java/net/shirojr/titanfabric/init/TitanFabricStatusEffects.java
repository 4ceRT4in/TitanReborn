package net.shirojr.titanfabric.init;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.effect.DiamondAbsorptionStatusEffect;
import net.shirojr.titanfabric.effect.EmberBurningStatusEffect;
import net.shirojr.titanfabric.effect.FrostburnStatusEffect;
import net.shirojr.titanfabric.effect.ImmunityEffect;
import net.shirojr.titanfabric.effect.IndestructibileStatusEffect;
import net.shirojr.titanfabric.effect.RecoveryStatusEffect;
import net.shirojr.titanfabric.effect.SafeFallingStatusEffect;
import net.shirojr.titanfabric.util.effects.RecoveryProfile;

public interface TitanFabricStatusEffects {
    RegistryEntry<StatusEffect> EMBER_BURNING = registerStatusEffect(
            "ember_burning", new EmberBurningStatusEffect()
    );
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
    RegistryEntry<StatusEffect> DIAMOND_ABSORPTION = registerStatusEffect(
            "diamond_absorption",
            new DiamondAbsorptionStatusEffect(StatusEffectCategory.BENEFICIAL, 0x4ACBFF)
    );
    RegistryEntry<StatusEffect> RECOVERY = registerStatusEffect(
            "recovery",
            new RecoveryStatusEffect(StatusEffectCategory.BENEFICIAL, 0xDF7536, RecoveryProfile.BASE)
    );
    RegistryEntry<StatusEffect> STRONG_RECOVERY = registerStatusEffect(
            "strong_recovery",
            new RecoveryStatusEffect(StatusEffectCategory.BENEFICIAL, 0x9C4130, RecoveryProfile.STRONG)
    );
    RegistryEntry<StatusEffect> LONG_RECOVERY = registerStatusEffect(
            "long_recovery",
            new RecoveryStatusEffect(StatusEffectCategory.BENEFICIAL, 0xC77A5E, RecoveryProfile.LONG)
    );
    RegistryEntry<StatusEffect> STRONG_WEAKNESS = registerStatusEffect(
            "strong_weakness",
            new StatusEffect(StatusEffectCategory.HARMFUL, 0x484D48) {}
                    .addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, Identifier.of("titanfabric", "effect.strong_weakness"), -6.0, EntityAttributeModifier.Operation.ADD_VALUE)
    );

    private static RegistryEntry<StatusEffect> registerStatusEffect(String name, StatusEffect statusEffect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, TitanFabric.getId(name), statusEffect);
    }

    static void initialize() {
        // static initialisation
    }
}
