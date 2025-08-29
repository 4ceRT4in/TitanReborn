package net.shirojr.titanfabric.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.init.TitanFabricDataComponents;
import net.shirojr.titanfabric.init.TitanFabricItems;
import net.shirojr.titanfabric.init.TitanFabricStatusEffects;
import net.shirojr.titanfabric.item.custom.TitanFabricArrowItem;
import net.shirojr.titanfabric.item.custom.bow.TitanCrossBowItem;
import net.shirojr.titanfabric.item.custom.misc.BackPackItem;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.OverpoweredEnchantmentsHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffectData;
import net.shirojr.titanfabric.util.items.MultiBowHelper;
import net.shirojr.titanfabric.util.items.SelectableArrow;

import java.util.List;
import java.util.Objects;
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
        registerBasicInnateItemsProvider(TitanFabricItems.EFFECT_ARROW);

        registerShieldProviders(TitanFabricItems.DIAMOND_SHIELD);
        registerShieldProviders(TitanFabricItems.NETHERITE_SHIELD);
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

        registerPotionBundle(TitanFabricItems.POTION_BUNDLE);

        registerOverpoweredEnchantedBookPredicate(Items.ENCHANTED_BOOK);
        registerCustomPotionPredicate(Items.POTION);
        registerCustomPotionPredicate(Items.SPLASH_POTION);
        registerCustomPotionPredicate(Items.LINGERING_POTION);
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
                    if (!(itemStack.getItem() instanceof SelectableArrow)) return 0.0f;
                    ItemStack selectedArrowStack = SelectableArrow.getSelectedArrowStack(itemStack, player);
                    if (selectedArrowStack == null) return 0.0f;
                    if (!(selectedArrowStack.getItem() instanceof TitanFabricArrowItem)) return 0.0f;
                    Optional<WeaponEffectData> effectData = WeaponEffectData.get(selectedArrowStack, INNATE_EFFECT);
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

    private static void registerCrossBowProviders() {
        registerBowProviders(TitanFabricItems.TITAN_CROSSBOW);
        registerCrossBowCharge();
    }

    private static void registerPotionBundle(Item item) {
        ModelPredicateProviderRegistry.register(item, Identifier.ofVanilla("filled"),
                (stack, world, entity, seed) -> {
                    if (stack.getItem() instanceof BackPackItem) {
                        var nbt = stack.get(TitanFabricDataComponents.BACKPACK_CONTENT);
                        if (nbt != null) {
                            for (ItemStack key : nbt.getItems()) {
                                if (!key.isEmpty() && key.getItem() != TitanFabricItems.BACKPACK_BIG) {
                                    return 1.0F;
                                }
                            }
                        }
                    }
                    return 0.0F;
                });
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
        if (item == null) return;
        for (DyeColor colors : DyeColor.values()) {
            ModelPredicateProviderRegistry.register(item, Identifier.ofVanilla(colors.getName()),
                    (stack, world, entity, seed) ->
                            stack.get(DataComponentTypes.BASE_COLOR) != null && Objects.requireNonNull(stack.get(DataComponentTypes.BASE_COLOR)).getName().equals(colors.getName()) ? 1.0F : 0.0F);
        }
    }

    private static void registerCustomPotionPredicate(Item item) {
        if (item == null) return;
        ModelPredicateProviderRegistry.register(item, Identifier.ofVanilla("frostburn"),
                (stack, world, entity, seed) -> {
                    if (stack.getItem() instanceof PotionItem || stack.getItem() instanceof SplashPotionItem || stack.getItem() instanceof LingeringPotionItem) {
                        PotionContentsComponent contents = stack.get(DataComponentTypes.POTION_CONTENTS);
                        if (contents != null) {
                            for (StatusEffectInstance effect : contents.getEffects()) {
                                if (effect.getEffectType() == TitanFabricStatusEffects.FROSTBURN) {
                                    return 1.0f;
                                }
                            }
                        }
                    }
                    return 0.0f;
                });
        ModelPredicateProviderRegistry.register(item, Identifier.ofVanilla("immunity"),
                (stack, world, entity, seed) -> {
                    if (stack.getItem() instanceof PotionItem || stack.getItem() instanceof SplashPotionItem || stack.getItem() instanceof LingeringPotionItem) {
                        PotionContentsComponent contents = stack.get(DataComponentTypes.POTION_CONTENTS);
                        if (contents != null) {
                            for (StatusEffectInstance effect : contents.getEffects()) {
                                if (effect.getEffectType() == TitanFabricStatusEffects.IMMUNITY) {
                                    return 1.0f;
                                }
                            }
                        }
                    }
                    return 0.0f;
                });
        ModelPredicateProviderRegistry.register(item, Identifier.ofVanilla("indestructible"),
                (stack, world, entity, seed) -> {
                    if (stack.getItem() instanceof PotionItem || stack.getItem() instanceof SplashPotionItem || stack.getItem() instanceof LingeringPotionItem) {
                        PotionContentsComponent contents = stack.get(DataComponentTypes.POTION_CONTENTS);
                        if (contents != null) {
                            for (StatusEffectInstance effect : contents.getEffects()) {
                                if (effect.getEffectType() == TitanFabricStatusEffects.INDESTRUCTIBILITY) {
                                    return 1.0f;
                                }
                            }
                        }
                    }
                    return 0.0f;
                });
    }

    private static void registerOverpoweredEnchantedBookPredicate(Item item) {
        ModelPredicateProviderRegistry.register(item, Identifier.ofVanilla("overpowered"),
                (stack, world, entity, seed) -> {
                    if (OverpoweredEnchantmentsHelper.isOverpoweredEnchantmentBook(stack)) {
                        return 1.0f;
                    }
                    return 0.0f;
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


