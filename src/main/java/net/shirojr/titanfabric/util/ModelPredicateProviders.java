package net.shirojr.titanfabric.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.item.TitanFabricItems;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffect;
import net.shirojr.titanfabric.util.items.MultiBowHelper;

import static net.shirojr.titanfabric.util.effects.WeaponEffectData.EFFECT_NBT_KEY;
import static net.shirojr.titanfabric.util.effects.WeaponEffectType.ADDITIONAL_EFFECT;
import static net.shirojr.titanfabric.util.effects.WeaponEffectType.INNATE_EFFECT;

@Environment(EnvType.CLIENT)
public class ModelPredicateProviders {
    public static void register() {
        registerWeaponEffects(TitanFabricItems.DIAMOND_GREATSWORD);
        registerWeaponEffects(TitanFabricItems.CITRIN_SWORD);
        registerWeaponEffects(TitanFabricItems.CITRIN_GREATSWORD);
        registerWeaponEffects(TitanFabricItems.NETHER_SWORD);
        registerWeaponEffects(TitanFabricItems.NETHER_GREATSWORD);
        registerWeaponEffects(TitanFabricItems.LEGEND_SWORD);
        registerWeaponEffects(TitanFabricItems.LEGEND_GREATSWORD);

        registerBasicInnateItemsProvider(TitanFabricItems.ESSENCE);
        registerBasicInnateItemsProvider(TitanFabricItems.ARROW);

        registerShieldProviders(TitanFabricItems.DIAMOND_SHIELD);
        registerShieldProviders(TitanFabricItems.LEGEND_SHIELD);

        registerBowProviders(TitanFabricItems.LEGEND_BOW);
        registerBowProviders(TitanFabricItems.MULTI_BOW_1);
        registerBowArrowCount(TitanFabricItems.MULTI_BOW_1);
        registerBowProviders(TitanFabricItems.MULTI_BOW_2);
        registerBowArrowCount(TitanFabricItems.MULTI_BOW_2);
        registerBowProviders(TitanFabricItems.MULTI_BOW_3);
        registerBowArrowCount(TitanFabricItems.MULTI_BOW_3);
        registerCrossBowProviders();
    }

    private static void registerWeaponEffects(Item item) {
        registerEffectProvider(item, new Identifier("effect"));
        registerStrengthProvider(item, new Identifier("strength"));
    }

    private static void registerBasicInnateItemsProvider(Item item) {
        ModelPredicateProviderRegistry.register(item, new Identifier("effect"),
                (itemStack, clientWorld, livingEntity, seed) -> {
                    if (!EffectHelper.getWeaponEffectDataCompound(itemStack).contains(INNATE_EFFECT.getNbtKey()))
                        return 0.0f;
                    NbtCompound typeCompound = EffectHelper.getWeaponEffectDataCompound(itemStack).getCompound(INNATE_EFFECT.getNbtKey());
                    WeaponEffect effect = WeaponEffect.getEffect(typeCompound.getString(EFFECT_NBT_KEY));
                    if (effect == null) return 0.0f;
                    return switch (effect) {
                        case BLIND -> 0.1f;
                        case FIRE -> 0.2f;
                        case POISON -> 0.3f;
                        case WEAK -> 0.4f;
                        case WITHER -> 0.5f;
                    };
                });
    }

    private static void registerShieldProviders(Item item) {
        registerShieldBlockingProvider(item);
    }

    private static void registerBowProviders(Item item) {
        registerBowPull(item, new Identifier("pull"));
        registerBowPulling(item, new Identifier("pulling"));
    }

    private static void registerCrossBowProviders() {
        registerBowProviders(TitanFabricItems.TITAN_CROSSBOW);
        //registerCrossBow(item);
    }

    private static void registerEffectProvider(Item item, Identifier identifier) {
        if (item == null) return;
        ModelPredicateProviderRegistry.register(item, identifier, (itemStack, clientWorld, livingEntity, seed) -> {
            if (!EffectHelper.getWeaponEffectDataCompound(itemStack).contains(ADDITIONAL_EFFECT.getNbtKey()))
                return 0.0f;
            NbtCompound typeCompound = EffectHelper.getWeaponEffectDataCompound(itemStack).getCompound(ADDITIONAL_EFFECT.getNbtKey());
            WeaponEffect effect = WeaponEffect.getEffect(typeCompound.getString(EFFECT_NBT_KEY));
            if (effect == null) return 0.0f;
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
        if (item == null) return;
        ModelPredicateProviderRegistry.register(item, identifier,
                (itemStack, clientWorld, livingEntity, seed) ->
                        EffectHelper.getEffectStrength(itemStack, ADDITIONAL_EFFECT) * 0.1f);
    }

    private static void registerBowPull(Item item, Identifier identifier) {
        if (item == null) return;
        ModelPredicateProviderRegistry.register(item, identifier, (stack, world, entity, seed) -> {
            if (stack == null || entity == null || entity.getActiveItem() != stack) return 0.0f;
            float maxUseTime = stack.getMaxUseTime();
            float leftUseTime = entity.getItemUseTimeLeft();
            return (maxUseTime - leftUseTime) / 20.0f;
            //return 0.65f;
        });
    }

    private static void registerBowPulling(Item item, Identifier identifier) {
        if (item == null) return;
        ModelPredicateProviderRegistry.register(item, identifier, (stack, world, entity, seed) -> {
            if (stack == null || entity == null) return 0.0f;
            if (!entity.isUsingItem()) return 0.0f;
            if (entity.getActiveItem() != stack) return 0.0f;
            return 1.0f;
        });
    }

    private static void registerBowArrowCount(Item item) {
        if (item == null) return;
        ModelPredicateProviderRegistry.register(item, new Identifier("arrows"), (stack, world, entity, seed) -> {
            if (stack == null || entity == null) return 0.0f;
            return MultiBowHelper.getFullArrowCount(stack) * 0.1f;
        });
    }

    private static void registerShieldBlockingProvider(Item item) {
        ModelPredicateProviderRegistry.register(item, new Identifier("blocking"), (stack, world, entity, seed) -> {
            if (stack == null || entity == null) return 0.0f;
            return entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0f : 0.0f;
        });
    }
}


