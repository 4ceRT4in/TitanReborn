package net.shirojr.titanfabric.util.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.cca.component.DiamondAbsorptionComponent;
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

    public static void updateDiamondAbsorptionAfterDamage(LivingEntity entity, float remainingDiamondAbsorption) {
        float remainingAmount = Math.max(0.0f, remainingDiamondAbsorption);
        DiamondAbsorptionComponent.get(entity).setDiamondAbsorptionAmount(remainingAmount, !entity.getWorld().isClient());

        if (entity.getWorld().isClient() || remainingAmount > 0.01f) return;

        entity.removeStatusEffect(TitanFabricStatusEffects.DIAMOND_ABSORPTION);
        if (entity.getAbsorptionAmount() > 0.01f) {
            applyMaxAbsorptionModifier(entity.getAttributes(), entity.getAbsorptionAmount());
        } else {
            clearMaxAbsorptionModifier(entity);
        }
    }
}
