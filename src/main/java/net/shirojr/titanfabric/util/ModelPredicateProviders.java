package net.shirojr.titanfabric.util;

import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.item.TitanFabricItems;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffects;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ModelPredicateProviders {

    public static void registerAll() {
        registerEffectProvider(TitanFabricItems.CITRIN_SWORD, new Identifier("effect"));
        registerStrengthProvider(TitanFabricItems.CITRIN_SWORD, new Identifier("strength"));
    }

    private static void registerEffectProvider(Item item, Identifier identifier) {
        ModelPredicateProviderRegistry.register(item, identifier, (itemStack, clientWorld, livingEntity, seed) -> {
            WeaponEffects effect = WeaponEffects.getEffect(itemStack.getOrCreateNbt().getString(EffectHelper.EFFECTS_NBT_KEY));
            if (effect == null) return 0f;
            TitanFabric.LOGGER.info(String.valueOf(effect));
            return switch (effect) {
                case BLIND -> 0.1f;
                case FIRE -> 0.2f;
                case POISON -> 0.3f;
                case WEAK -> 0.4f;
                case WITHER -> 0.5f;
            };
        });
    }

    private static void registerStrengthProvider(Item item, Identifier identifier) {
        ModelPredicateProviderRegistry.register(item, identifier, (itemStack, clientWorld, livingEntity, seed) -> EffectHelper.getEffectStrength(itemStack) * 0.1f);
    }
}
