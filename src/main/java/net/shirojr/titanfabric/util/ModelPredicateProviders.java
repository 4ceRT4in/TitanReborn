package net.shirojr.titanfabric.util;

import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.item.TitanFabricItems;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffects;

public class ModelPredicateProviders {

    public static void registerAll() {
        registerProviders(TitanFabricItems.CITRIN_SWORD, new Identifier("effect"), EffectHelper.EFFECTS_NBT_KEY);
        registerProviders(TitanFabricItems.CITRIN_SWORD, new Identifier("strength"), EffectHelper.EFFECTS_STRENGTH_NBT_KEY);
    }

    private static void registerProviders(Item item, Identifier identifier, String nbtKey) {
        ModelPredicateProviderRegistry.register(item, identifier, (itemStack, clientWorld, livingEntity, seed) -> {
            if (identifier.equals(new Identifier("effect"))) {
                WeaponEffects effect = WeaponEffects.getEffect(itemStack.getOrCreateNbt().getString(nbtKey));
                if (effect == null) return 0f;

                return switch (effect) {
                    //case NONE -> 0f;
                    case BLIND -> 1f;
                    case FIRE -> 2f;
                    case POISON -> 3f;
                    case WEAK -> 4f;
                    case WITHER -> 5f;
                };
            }

            if (identifier.equals(new Identifier("strength"))) {
                return EffectHelper.getEffectStrength(itemStack);
            }

            else return 0;
        });
    }
}
