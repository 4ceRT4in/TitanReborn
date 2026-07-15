package net.shirojr.titanfabric.util.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.cca.component.DiamondAbsorptionComponent;
import net.shirojr.titanfabric.init.TitanFabricDamageTypes;
import net.shirojr.titanfabric.init.TitanFabricStatusEffects;

public final class DiamondAbsorptionHelper {
    public static final Identifier MAX_ABSORPTION_MODIFIER_ID = TitanFabric.getId("effect.diamond_absorption");

    private DiamondAbsorptionHelper() {
    }

    public static float getAbsorptionAmount(int amplifier) {
        return switch (Math.max(0, amplifier)) {
            case 0 -> 8.0f;
            case 1 -> 12.0f;
            case 2 -> 16.0f;
            default -> 20.0f;
        };
    }

    public static float getVanillaAbsorptionAmount(int amplifier) {
        return 4.0f * (Math.max(0, amplifier) + 1);
    }

    public static void applyMaxAbsorptionModifier(LivingEntity entity, int amplifier) {
        applyMaxAbsorptionModifier(entity.getAttributes(), getAbsorptionAmount(amplifier));
    }

    public static void applyMaxAbsorptionModifier(AttributeContainer attributes, float amount) {
        EntityAttributeInstance attributeInstance = attributes.getCustomInstance(EntityAttributes.GENERIC_MAX_ABSORPTION);
        if (attributeInstance == null) return;

        attributeInstance.removeModifier(MAX_ABSORPTION_MODIFIER_ID);
        attributeInstance.addTemporaryModifier(new EntityAttributeModifier(
                MAX_ABSORPTION_MODIFIER_ID,
                amount,
                EntityAttributeModifier.Operation.ADD_VALUE
        ));
    }

    public static void clearMaxAbsorptionModifier(LivingEntity entity) {
        EntityAttributeInstance attributeInstance = entity.getAttributeInstance(EntityAttributes.GENERIC_MAX_ABSORPTION);
        if (attributeInstance == null) return;
        attributeInstance.removeModifier(MAX_ABSORPTION_MODIFIER_ID);
    }

    public static boolean isFrostburn(DamageSource source) {
        return source != null && source.isOf(TitanFabricDamageTypes.FROSTBURN.get());
    }

    /** Diamond hearts do not participate in Frostburn's absorption calculation. */
    public static float getDamageableAbsorption(LivingEntity entity, DamageSource source) {
        float total = entity.getAbsorptionAmount();
        if (!isFrostburn(source)) return total;
        float diamond = Math.min(DiamondAbsorptionComponent.get(entity).getDiamondAbsorptionAmount(), total);
        return Math.max(0.0f, total - diamond);
    }

    /** Keeps component bookkeeping in lockstep with ordinary absorption damage. */
    public static void recordAbsorptionDamage(LivingEntity entity, DamageSource source, float absorptionBefore) {
        if (isFrostburn(source)) return;
        DiamondAbsorptionComponent component = DiamondAbsorptionComponent.get(entity);
        float absorbed = Math.max(0.0f, absorptionBefore - entity.getAbsorptionAmount());
        if (absorbed <= 0.01f) return;
        updateDiamondAbsorptionAfterDamage(
                entity,
                component.getDiamondAbsorptionAmount() - absorbed,
                component.getEffectAbsorptionAmount() - absorbed
        );
    }

    public static void updateDiamondAbsorptionAfterDamage(LivingEntity entity, float remainingDiamondAbsorption, float remainingEffectAbsorption) {
        float remainingAmount = Math.max(0.0f, remainingDiamondAbsorption);
        float remainingEffectAmount = Math.max(0.0f, remainingEffectAbsorption);
        DiamondAbsorptionComponent component = DiamondAbsorptionComponent.get(entity);
        component.setDiamondAbsorptionAmount(remainingAmount, !entity.getWorld().isClient());
        component.setEffectAbsorptionAmount(remainingEffectAmount, !entity.getWorld().isClient());

        if (entity.getWorld().isClient() || remainingEffectAmount > 0.01f) return;
        entity.removeStatusEffect(TitanFabricStatusEffects.DIAMOND_ABSORPTION);
        if (entity.getAbsorptionAmount() > 0.01f) {
            applyMaxAbsorptionModifier(entity.getAttributes(), entity.getAbsorptionAmount());
        } else {
            clearMaxAbsorptionModifier(entity);
        }
    }
}
