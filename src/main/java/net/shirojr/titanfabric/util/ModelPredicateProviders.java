package net.shirojr.titanfabric.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.enchantment.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.potion.PotionUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.color.TitanFabricDyeProviders;
import net.shirojr.titanfabric.item.TitanFabricItems;
import net.shirojr.titanfabric.item.custom.TitanFabricArrowItem;
import net.shirojr.titanfabric.item.custom.bow.TitanCrossBowItem;
import net.shirojr.titanfabric.item.custom.misc.BackPackItem;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffect;
import net.shirojr.titanfabric.util.handler.ArrowSelectionHandler;
import net.shirojr.titanfabric.util.items.MultiBowHelper;

import java.util.List;
import java.util.Objects;

import static net.shirojr.titanfabric.util.effects.WeaponEffectData.EFFECT_NBT_KEY;
import static net.shirojr.titanfabric.util.effects.WeaponEffectType.ADDITIONAL_EFFECT;
import static net.shirojr.titanfabric.util.effects.WeaponEffectType.INNATE_EFFECT;

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
        registerFrostburnPotionPredicate(Items.POTION);
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

    private static void registerLegendBowProviders() {
        registerBowProviders(TitanFabricItems.LEGEND_BOW);
        registerLegendBowVersionProvider(new Identifier("handle"));
    }

    private static void registerLegendBowVersionProvider(Identifier identifier) {
        ModelPredicateProviderRegistry.register(TitanFabricItems.LEGEND_BOW, identifier,
                (itemStack, clientWorld, livingEntity, seed) -> {
                    if (!(livingEntity instanceof PlayerEntity player)) return 0.0f;
                    if (!(player instanceof ArrowSelectionHandler clientPlayer)) return 0.0f;
                    if (clientPlayer.titanfabric$getSelectedArrowIndex().isEmpty()) return 0.0f;
                    ItemStack savedArrowItemStack = player.getInventory().getStack(clientPlayer.titanfabric$getSelectedArrowIndex().get());
                    if (!(savedArrowItemStack.getItem() instanceof TitanFabricArrowItem)) return 0.0f;
                    NbtCompound typeCompound = EffectHelper.getWeaponEffectDataCompound(savedArrowItemStack)
                            .getCompound(INNATE_EFFECT.getNbtKey());
                    WeaponEffect effect = WeaponEffect.getEffect(typeCompound.getString(EFFECT_NBT_KEY));
                    if (effect == null) return 0.0f;
                    return switch (effect) {
                        case BLIND -> 0.1f;
                        case FIRE -> 0.2f;
                        case POISON -> 0.3f;
                        case WEAK -> 0.4f;
                        case WITHER -> 0.5f;
                        default -> 0.0f;
                    };
                });
    }

    private static void registerPotionBundle(Item item) {
        ModelPredicateProviderRegistry.register(item, new Identifier("filled"),
                (stack, world, entity, seed) -> {
                    if (stack.getItem() instanceof BackPackItem) {
                        NbtCompound nbt = stack.getOrCreateNbt();
                        if (nbt.contains(BackPackItem.INVENTORY_NBT_KEY)) {
                            NbtCompound inventoryNbt = nbt.getCompound(BackPackItem.INVENTORY_NBT_KEY);
                            for (String key : inventoryNbt.getKeys()) {
                                NbtCompound itemNbt = inventoryNbt.getCompound(key);
                                ItemStack itemStack = ItemStack.fromNbt(itemNbt);
                                if (!itemStack.isEmpty()) {
                                    return 1.0F;
                                }
                            }
                        }
                    }
                    return 0.0F;
                });
    }

    private static void registerCrossBowProviders() {
        registerBowProviders(TitanFabricItems.TITAN_CROSSBOW);
        registerCrossBowCharge();
    }

    private static void registerCrossBowCharge() {
        ModelPredicateProviderRegistry.register(TitanFabricItems.TITAN_CROSSBOW, new Identifier("charged"),
                (itemStack, clientWorld, livingEntity, seed) -> {
                    if (!itemStack.getOrCreateNbt().contains("Charged")) return 0;
                    if (!itemStack.getOrCreateNbt().getBoolean("Charged")) return 0;
                    if (!(itemStack.getItem() instanceof TitanCrossBowItem)) return 0;
                    List<ItemStack> loadedProjectiles = TitanCrossBowItem.getProjectiles(itemStack);
                    if (loadedProjectiles.size() < 1) return 0;
                    ItemStack firstProjectileStack = loadedProjectiles.get(0);
                    if (firstProjectileStack.isOf(Items.SPECTRAL_ARROW)) return 0.1f;
                    if (firstProjectileStack.getItem() instanceof PotionItem) return 0.2f;
                    return 1.0f;
                });
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
    private static void registerColorItemProvider(Item item) {
        for (String colors : TitanFabricDyeProviders.COLOR_KEYS) {
            ModelPredicateProviderRegistry.register(item, new Identifier(colors),
                    (stack, world, entity, seed) -> stack.hasNbt() && Objects.requireNonNull(stack.getNbt()).getBoolean(colors) ? 1.0F : 0.0F);
        }
    }
    Rarity
    private static void registerFrostburnPotionPredicate(Item item) {
        ModelPredicateProviderRegistry.register(item, new Identifier("frostburn"),
                (stack, world, entity, seed) -> {
                    if (stack.getItem() instanceof PotionItem) {
                        if(stack.hasNbt() && stack.getNbt() != null && stack.getSubNbt("Potion") != null) {
                            if(Objects.requireNonNull(stack.getSubNbt("Potion")).contains("titanfabric:frostburn_potion")) {
                                return 1.0f;
                            }
                        }
                    }
                    return 0.0f;
                });
    }

    private static void registerOverpoweredEnchantedBookPredicate(Item item) {
        ModelPredicateProviderRegistry.register(item, new Identifier("overpowered"),
                (stack, world, entity, seed) -> {
                    if (stack.getItem() instanceof EnchantedBookItem) {
                        NbtCompound nbt = stack.getNbt();
                        if (nbt != null && nbt.contains("StoredEnchantments", 9)) {
                            NbtList enchantments = nbt.getList("StoredEnchantments", 10);

                            for (int i = 0; i < enchantments.size(); i++) {
                                NbtCompound enchantmentTag = enchantments.getCompound(i);
                                String enchantmentId = enchantmentTag.getString("id");
                                int level = enchantmentTag.getInt("lvl");
                                if (enchantmentId.equals("minecraft:sharpness") && level >= 6) {
                                    return 1.0f;
                                }
                                if (enchantmentId.equals("minecraft:protection") && level >= 5) {
                                    return 1.0f;
                                }
                                if (enchantmentId.equals("minecraft:power") && level >= 6) {
                                    return 1.0f;
                                }
                            }
                        }
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


