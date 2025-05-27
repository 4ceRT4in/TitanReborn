package net.shirojr.titanfabric.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.init.TitanFabricDataComponents;
import net.shirojr.titanfabric.init.TitanFabricItems;
import net.shirojr.titanfabric.item.custom.TitanFabricArrowItem;
import net.shirojr.titanfabric.item.custom.bow.TitanCrossBowItem;
import net.shirojr.titanfabric.item.custom.misc.PotionBundleItem;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffectData;
import net.shirojr.titanfabric.util.handler.ArrowSelectionHandler;
import net.shirojr.titanfabric.util.items.MultiBowHelper;

import java.util.List;
import java.util.Optional;

import static net.shirojr.titanfabric.util.effects.WeaponEffectType.ADDITIONAL_EFFECT;
import static net.shirojr.titanfabric.util.effects.WeaponEffectType.INNATE_EFFECT;

@SuppressWarnings("SameParameterValue")
@Environment(EnvType.CLIENT)
public class ModelPredicateProviders {

    public static void register() {
        registerWeaponEffects(TitanFabricItems.DIAMOND_GREATSWORD);
        registerWeaponEffects(TitanFabricItems.CITRIN_SWORD);
        registerWeaponEffects(TitanFabricItems.CITRIN_GREATSWORD);
        registerWeaponEffects(TitanFabricItems.EMBER_SWORD);
        registerWeaponEffects(TitanFabricItems.EMBER_GREATSWORD);
        registerWeaponEffects(TitanFabricItems.LEGEND_SWORD);
        registerWeaponEffects(TitanFabricItems.LEGEND_GREATSWORD);
        registerWeaponEffects(TitanFabricItems.DIAMOND_SWORD);

        registerBasicInnateItemsProvider(TitanFabricItems.ESSENCE);
        registerBasicInnateItemsProvider(TitanFabricItems.ARROW);

        registerShieldProviders(TitanFabricItems.DIAMOND_SHIELD);
        registerShieldProviders(TitanFabricItems.LEGEND_SHIELD);

        registerLegendBowProviders();
        registerBowProviders(TitanFabricItems.MULTI_BOW_1);
        registerBowArrowCount(TitanFabricItems.MULTI_BOW_1);
        registerBowProviders(TitanFabricItems.MULTI_BOW_2);
        registerBowArrowCount(TitanFabricItems.MULTI_BOW_2);
        registerBowProviders(TitanFabricItems.MULTI_BOW_3);
        registerBowArrowCount(TitanFabricItems.MULTI_BOW_3);
        registerCrossBowProviders();

        registerColorItemProvider(TitanFabricItems.BACKPACK_BIG);
        registerColorItemProvider(TitanFabricItems.BACKPACK_MEDIUM);
        registerColorItemProvider(TitanFabricItems.BACKPACK_SMALL);
        registerColorItemProvider(TitanFabricItems.PARACHUTE);

        registerBundleItemProvider(TitanFabricItems.POTION_BUNDLE);
    }

    private static void registerWeaponEffects(Item item) {
        registerEffectProvider(item, Identifier.ofVanilla("effect"));
        registerStrengthProvider(item, Identifier.ofVanilla("strength"));
    }

    private static void registerBasicInnateItemsProvider(Item item) {
        ModelPredicateProviderRegistry.register(item, Identifier.ofVanilla("effect"),
                (itemStack, clientWorld, livingEntity, seed) -> {
                    Optional<WeaponEffectData> effectData = WeaponEffectData.get(itemStack, INNATE_EFFECT);
                    if (effectData.isEmpty()) return 0;
                    if (effectData.get().weaponEffect() == null) return 0.0f;
                    return switch (effectData.get().weaponEffect()) {
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
        registerBowPull(item, Identifier.ofVanilla("pull"));
        registerBowPulling(item, Identifier.ofVanilla("pulling"));
    }

    private static void registerLegendBowProviders() {
        registerBowProviders(TitanFabricItems.LEGEND_BOW);
        registerLegendBowVersionProvider(Identifier.ofVanilla("handle"));
    }

    private static void registerLegendBowVersionProvider(Identifier identifier) {
        ModelPredicateProviderRegistry.register(TitanFabricItems.LEGEND_BOW, identifier,
                (itemStack, clientWorld, livingEntity, seed) -> {
                    if (!(livingEntity instanceof PlayerEntity player)) return 0.0f;
                    if (!(player instanceof ArrowSelectionHandler clientPlayer)) return 0.0f;
                    if (clientPlayer.titanfabric$getSelectedArrowIndex().isEmpty()) return 0.0f;
                    ItemStack savedArrowItemStack = player.getInventory().getStack(clientPlayer.titanfabric$getSelectedArrowIndex().get());
                    if (!(savedArrowItemStack.getItem() instanceof TitanFabricArrowItem)) return 0.0f;
                    Optional<WeaponEffectData> effectData = WeaponEffectData.get(savedArrowItemStack, INNATE_EFFECT);
                    if (effectData.isEmpty()) return 0.0f;
                    if (effectData.get().weaponEffect() == null) return 0.0f;
                    return switch (effectData.get().weaponEffect()) {
                        case BLIND -> 0.1f;
                        case POISON -> 0.3f;
                        case WEAK -> 0.4f;
                        case WITHER -> 0.5f;
                        default -> 0.0f;
                    };
                });
    }

    private static void registerCrossBowProviders() {
        registerBowProviders(TitanFabricItems.TITAN_CROSSBOW);
        registerCrossBowCharge();
    }

    private static void registerCrossBowCharge() {
        ModelPredicateProviderRegistry.register(TitanFabricItems.TITAN_CROSSBOW, Identifier.ofVanilla("charged"),
                (itemStack, clientWorld, livingEntity, seed) -> {
                    boolean charged = Optional.ofNullable(itemStack.get(TitanFabricDataComponents.CHARGED)).orElse(false);
                    if (!charged) return 0.0f;
                    if (!(itemStack.getItem() instanceof TitanCrossBowItem)) return 0.0f;
                    List<ItemStack> loadedProjectiles = TitanCrossBowItem.getLoadedProjectiles(itemStack);
                    if (loadedProjectiles.isEmpty()) return 0;
                    ItemStack firstProjectileStack = loadedProjectiles.get(0);
                    if (firstProjectileStack.isOf(Items.SPECTRAL_ARROW)) return 0.1f;
                    if (firstProjectileStack.getItem() instanceof PotionItem) return 0.2f;
                    return 1.0f;
                });
    }

    private static void registerEffectProvider(Item item, Identifier identifier) {
        if (item == null) return;
        ModelPredicateProviderRegistry.register(item, identifier, (itemStack, clientWorld, livingEntity, seed) -> {
            Optional<WeaponEffectData> effectData = WeaponEffectData.get(itemStack, ADDITIONAL_EFFECT);
            if (effectData.isEmpty()) return 0.0f;
            if (effectData.get().weaponEffect() == null) return 0.0f;
            return switch (effectData.get().weaponEffect()) {
                case BLIND -> 0.1f;
                case FIRE -> 0.2f;
                case POISON -> 0.3f;
                case WEAK -> 0.4f;
                case WITHER -> 0.5f;
            };
        });
    }

    private static void registerColorItemProvider(Item item) {
        //TODO: add color provider impl, mby with interface too?
    }

    private static void registerBundleItemProvider(Item item) {
        ModelPredicateProviderRegistry.register(item, Identifier.ofVanilla("filled"),
                (stack, world, entity, seed) -> PotionBundleItem.getAmountFilled(stack));
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
            float maxUseTime = stack.getMaxUseTime(entity);
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
        ModelPredicateProviderRegistry.register(item, Identifier.ofVanilla("arrows"), (stack, world, entity, seed) -> {
            if (stack == null || entity == null) return 0.0f;
            return MultiBowHelper.getFullArrowCount(stack) * 0.1f;
        });
    }

    private static void registerShieldBlockingProvider(Item item) {
        ModelPredicateProviderRegistry.register(item, Identifier.ofVanilla("blocking"), (stack, world, entity, seed) -> {
            if (stack == null || entity == null) return 0.0f;
            return entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0f : 0.0f;
        });
    }
}


