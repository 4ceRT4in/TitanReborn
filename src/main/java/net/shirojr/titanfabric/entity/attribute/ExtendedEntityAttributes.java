package net.shirojr.titanfabric.entity.attribute;

import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.shirojr.titanfabric.TitanFabric;

public class ExtendedEntityAttributes {
    public static final RegistryEntry<EntityAttribute> GENERIC_CRIT_MODIFIER = register(
            "generic.crit_modifier", new ClampedEntityAttribute("tooltip.titanfabric.attribute.critModifier", 1.2, 0.0, 2048.0)
    );

    private static RegistryEntry<EntityAttribute> register(String id, EntityAttribute attribute) {
        return Registry.registerReference(Registries.ATTRIBUTE, TitanFabric.getId(id), attribute);
    }
}
