package net.shirojr.titanfabric.init;

import net.minecraft.entity.damage.DamageScaling;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.World;
import net.shirojr.titanfabric.TitanFabric;

import java.util.HashMap;
import java.util.List;

public interface TitanFabricDamageTypes {
    HashMap<RegistryKey<DamageType>, DamageTypePair> ALL_DAMAGE_TYPES = new HashMap<>();

    DamageTypePair FROSTBURN = register(new DamageType("frostburn", DamageScaling.NEVER, 0.0f),
            List.of(DamageTypeTags.BYPASSES_ARMOR, DamageTypeTags.BYPASSES_SHIELD, DamageTypeTags.BYPASSES_COOLDOWN,
                    DamageTypeTags.BYPASSES_RESISTANCE, DamageTypeTags.BYPASSES_EFFECTS, DamageTypeTags.BYPASSES_ENCHANTMENTS,
                    DamageTypeTags.IS_FREEZING, DamageTypeTags.BYPASSES_INVULNERABILITY, DamageTypeTags.NO_KNOCKBACK,
                    DamageTypeTags.NO_ANGER, DamageTypeTags.PANIC_ENVIRONMENTAL_CAUSES, DamageTypeTags.NO_IMPACT)
    );

    private static DamageTypePair register(DamageType type, List<TagKey<DamageType>> tags) {
        DamageTypePair damageTypePair = new DamageTypePair(type, tags);
        ALL_DAMAGE_TYPES.put(damageTypePair.get(), damageTypePair);
        return damageTypePair;
    }

    static DamageSource of(World world, DamageTypePair type) {
        return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(type.get()));
    }

    static void bootstrap(Registerable<DamageType> registerable) {
        for (var entry : ALL_DAMAGE_TYPES.entrySet()) {
            registerable.register(entry.getKey(), entry.getValue().instance);
        }
    }

    record DamageTypePair(DamageType instance, List<TagKey<DamageType>> tags) {
        public RegistryKey<DamageType> get() {
            return RegistryKey.of(RegistryKeys.DAMAGE_TYPE, TitanFabric.getId(instance.msgId()));
        }
    }

    static void initialize() {
        // static initialisation
    }
}
