package net.shirojr.titanfabric.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.item.TitanFabricItems;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffects;
import net.shirojr.titanfabric.util.items.MultiBowHelper;

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

        registerBowProviders(TitanFabricItems.LEGEND_BOW); // vanilla pfeile, spectral, nicht tipped, modded effect arrows
        registerBowProviders(TitanFabricItems.MULTI_BOW); // nur vanilla pfeile, spectral
        registerBowArrowCount(TitanFabricItems.MULTI_BOW);

        // crossbow: vanilla pfeile, spectral, vanilla splash potions, vanilla lingering potions, keine Raketen!
    }

    private static void registerWeaponEffects(Item item) {
        registerEffectProvider(item, new Identifier("effect"));
        registerStrengthProvider(item, new Identifier("strength"));
    }

    private static void registerBowProviders(Item item) {
        registerBowPull(item, new Identifier("pull"));
        registerBowPulling(item, new Identifier("pulling"));
    }

    private static void registerEffectProvider(Item item, Identifier identifier) {
        if (item == null) return;
        ModelPredicateProviderRegistry.register(item, identifier, (itemStack, clientWorld, livingEntity, seed) -> {
            WeaponEffects effect = WeaponEffects.getEffect(itemStack.getOrCreateNbt().getString(EffectHelper.EFFECTS_NBT_KEY));
            if (effect == null) return 0f;
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
                (itemStack, clientWorld, livingEntity, seed) -> EffectHelper.getEffectStrength(itemStack) * 0.1f);
    }

    private static void registerBowPull(Item item, Identifier identifier) {
        if (item == null) return;
        ModelPredicateProviderRegistry.register(item, identifier, (stack, world, entity, seed) -> {
            if (stack == null || entity == null || entity.getActiveItem() != stack) return 0.0f;
            float maxUseTime = stack.getMaxUseTime();
            float leftUseTime = entity.getItemUseTimeLeft();
            return (maxUseTime - leftUseTime) / 20;
        });
    }

    private static void registerBowPulling(Item item, Identifier identifier) {
        if (item == null) return;
        ModelPredicateProviderRegistry.register(item, identifier, (stack, world, entity, seed) -> {
            if (stack == null || entity == null) return 0.0f;
            return entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0f : 0.0f;
        });
    }

    private static void registerBowArrowCount(Item item) {
        if (item == null) return;
        ModelPredicateProviderRegistry.register(item, new Identifier("arrows"), (stack, world, entity, seed) -> {
            if (stack == null || entity == null) return 0.0f;
            return MultiBowHelper.getFullArrowCount(stack) * 0.1f;
        });
    }
}


