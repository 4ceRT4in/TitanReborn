package net.shirojr.titanfabric.util.effects;

import com.google.common.collect.Iterables;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.potion.Potions;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.shirojr.titanfabric.init.TitanFabricDataComponents;
import net.shirojr.titanfabric.item.custom.TitanFabricSwordItem;
import net.shirojr.titanfabric.util.LoggerUtil;
import net.shirojr.titanfabric.util.items.WeaponEffectCrafting;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Helper class for TitanFabric {@linkplain WeaponEffect}
 */
public final class EffectHelper {
    private EffectHelper() {
        // private constructor to avoid instantiating this utility class
    }

    /**
     * Returns {@linkplain WeaponEffect TitanFabric WeaponEffect} strength. It does not translate directly to StatusEffect's amplifier!<br><br>
     * Used for StatusEffect duration and amplifier calculation
     *
     * @return Strength of the Weapon Effect
     */
    public static int getEffectStrength(ItemStack itemStack, WeaponEffectType type) {
        HashSet<WeaponEffectData> effectSet = itemStack.get(TitanFabricDataComponents.WEAPON_EFFECTS);
        if (effectSet == null) return -1;
        for (WeaponEffectData entry : effectSet) {
            if (!entry.type().equals(type)) continue;
            return entry.strength();
        }
        return -1;
    }

    public static int getColor(WeaponEffect weaponEffect) {
        RegistryEntry<StatusEffect> effect = weaponEffect.getOutputEffect();
        if (effect == null) return -1;
        return effect.value().getColor();
    }

    /**
     * Sets the valid {@linkplain WeaponEffectData TitanFabric WeaponEffectData}. This will overwrite old
     * {@linkplain WeaponEffectData TitanFabric WeaponEffectData} on the stack!
     */
    public static ItemStack getStackWithEffects(ItemStack itemStack, List<WeaponEffectData> effectDataList, boolean overwrite) {
        if (itemStack.getItem() instanceof TitanFabricSwordItem titanFabricSwordItem) {
            if (!titanFabricSwordItem.canHaveWeaponEffects()) return itemStack;
        }
        if (!(itemStack.getItem() instanceof WeaponEffectCrafting weaponEffectHandler)) {
            return itemStack;
        }
        HashSet<WeaponEffectData> validEffects = new HashSet<>(effectDataList);
        if (!overwrite) {
            HashSet<WeaponEffectData> originalEffects = itemStack.get(TitanFabricDataComponents.WEAPON_EFFECTS);
            if (originalEffects != null) {
                validEffects.addAll(originalEffects);
            }
        }
        for (WeaponEffectData entry : effectDataList) {
            if (entry == null || entry.weaponEffect() == null) continue;
            if (!weaponEffectHandler.supportedEffects().contains(entry.weaponEffect())) continue;
            validEffects.add(entry);
        }
        itemStack.set(TitanFabricDataComponents.WEAPON_EFFECTS, validEffects);
        return itemStack;
    }

    public static ItemStack applyEffectToStack(ItemStack itemStack, WeaponEffectData effectData, boolean overwrite) {
        return getStackWithEffects(itemStack, List.of(effectData), overwrite);
    }

    public static void removeEffectsFromStack(ItemStack itemStack) {
        if (!itemStack.contains(TitanFabricDataComponents.WEAPON_EFFECTS)) return;
        itemStack.set(TitanFabricDataComponents.WEAPON_EFFECTS, new HashSet<>());
    }

    @Nullable
    public static WeaponEffect getWeaponEffectFromPotion(ItemStack stack) {
        PotionContentsComponent potionComponent = stack.get(DataComponentTypes.POTION_CONTENTS);
        if (potionComponent == null) return null;
        if (Iterables.size(potionComponent.getEffects()) > 1) {
            LoggerUtil.devLogger("Potion had more then one StatusEffect", true, null);
            return null;
        }
        for (var entry : WeaponEffect.values()) {
            if (entry.getIngredientEffect() == null) continue;
            if (entry.getIngredientEffect().equals(potionComponent.getEffects().iterator().next().getEffectType())) {
                return entry;
            }
        }
        LoggerUtil.devLogger("Couldn't find matching potion effect to map to WeaponEffect");
        return null;
    }

    public static ItemStack getPotionFromWeaponEffect(WeaponEffect weaponEffect) {
        if (weaponEffect.getIngredientEffect() == null) {
            LoggerUtil.devLogger("WeaponEffect has no associated IngredientEffect", true, null);
            return ItemStack.EMPTY;
        }

        if (weaponEffect.getIngredientEffect() == StatusEffects.NIGHT_VISION || weaponEffect.getIngredientEffect() == StatusEffects.BLINDNESS) {
            return PotionContentsComponent.createStack(Items.POTION, Potions.NIGHT_VISION);
        } else if (weaponEffect.getIngredientEffect() == StatusEffects.FIRE_RESISTANCE) {
            return PotionContentsComponent.createStack(Items.POTION, Potions.FIRE_RESISTANCE);
        } else if (weaponEffect.getIngredientEffect() == StatusEffects.POISON) {
            return PotionContentsComponent.createStack(Items.POTION, Potions.POISON);
        } else if (weaponEffect.getIngredientEffect() == StatusEffects.WEAKNESS) {
            return PotionContentsComponent.createStack(Items.POTION, Potions.WEAKNESS);
        } else if (weaponEffect.getIngredientEffect() == StatusEffects.INSTANT_DAMAGE || weaponEffect.getIngredientEffect() == StatusEffects.WITHER) {
            return PotionContentsComponent.createStack(Items.POTION, Potions.HARMING);
        } else {
            LoggerUtil.devLogger("No matching StatusEffect for WeaponEffect: " + weaponEffect.name());
            return ItemStack.EMPTY;
        }
    }

    public static boolean shouldEffectApply(Random random, int strength) {
        return random.nextInt(100) <= (25 * strength);
    }

    public static <T extends SwordItem> List<ItemStack> generateSwordsStacks(T swordItem, boolean addBaseItem) {
        if (!(swordItem instanceof WeaponEffectCrafting weaponEffectHandler)) {
            return List.of(swordItem.getDefaultStack());
        }
        List<ItemStack> stacks = new ArrayList<>();
        if (addBaseItem) {
            stacks.add(swordItem.getDefaultStack());
        }
        for (WeaponEffect weaponEffect : weaponEffectHandler.supportedEffects()) {
            for (int effectStrength = 1; effectStrength < 3; effectStrength++) {
                ItemStack swordStack = swordItem.getDefaultStack();
                if (weaponEffectHandler.getBaseEffect() != null) {
                    EffectHelper.applyEffectToStack(swordStack, weaponEffectHandler.getBaseEffect(), false);
                }
                WeaponEffectData additionalEffectData = new WeaponEffectData(WeaponEffectType.ADDITIONAL_EFFECT, weaponEffect, effectStrength);
                EffectHelper.applyEffectToStack(swordStack, additionalEffectData, false);
                stacks.add(swordStack);
            }
        }
        return stacks;
    }

    public static <T extends ArrowItem> List<ItemStack> generateArrowStacks(T arrowItem, boolean addBaseItem) {
        if (!(arrowItem instanceof WeaponEffectCrafting weaponEffectHandler)) {
            return List.of(arrowItem.getDefaultStack());
        }
        ItemStack defaultStack = arrowItem.getDefaultStack();
        List<ItemStack> stacks = new ArrayList<>();
        if (addBaseItem) {
            stacks.add(defaultStack);
        }

        for (WeaponEffect weaponEffect : weaponEffectHandler.supportedEffects()) {
            WeaponEffectData additionalEffectData = new WeaponEffectData(WeaponEffectType.INNATE_EFFECT, weaponEffect, 2);
            ItemStack effectItemStack = EffectHelper.applyEffectToStack(arrowItem.getDefaultStack(), additionalEffectData, false);
            stacks.add(effectItemStack);
        }
        return stacks;
    }

    public static <T extends Item> List<ItemStack> generateEssenceStacks(T essenceStack, boolean addBaseItem) {
        if (!(essenceStack instanceof WeaponEffectCrafting weaponEffectHandler)) {
            return List.of(essenceStack.getDefaultStack());
        }
        ItemStack defaultStack = essenceStack.getDefaultStack();
        List<ItemStack> stacks = new ArrayList<>();
        if (addBaseItem) {
            stacks.add(defaultStack);
        }

        for (WeaponEffect weaponEffect : weaponEffectHandler.supportedEffects()) {
            WeaponEffectData additionalEffectData = new WeaponEffectData(WeaponEffectType.INNATE_EFFECT, weaponEffect, 1);
            ItemStack effectItemStack = EffectHelper.applyEffectToStack(essenceStack.getDefaultStack(), additionalEffectData, false);
            stacks.add(effectItemStack);
        }
        return stacks;
    }

    public static void applyWeaponEffectsOnTarget(World world, ItemStack itemStack, LivingEntity target) {
        var weaponEffects = itemStack.get(TitanFabricDataComponents.WEAPON_EFFECTS);
        if (weaponEffects == null) return;
        for (WeaponEffectData entry : weaponEffects) {
            applyWeaponEffectOnTarget(entry, world, target, itemStack.getItem() instanceof ArrowItem, itemStack.getItem() instanceof ArrowItem);
        }
    }

    private static void applyWeaponEffectOnTarget(WeaponEffectData data, World world, LivingEntity target, boolean ignoreChance, boolean isFromArrow) {
        if (world.isClient() || data == null) return;
        WeaponEffect effect = data.weaponEffect();
        int effectStrength = data.strength();
        if (!ignoreChance) {
            if (world.getRandom().nextInt(100) >= (25 * effectStrength)) return;
        }
        //maximum effect strength of swords is 0, and of arrows is 1
        final int potionEffectStrength = isFromArrow ? 1 : 0;
        switch (effect) {
            case BLIND -> {
                if (target.hasStatusEffect(StatusEffects.BLINDNESS)) return;
                target.addStatusEffect(new StatusEffectInstance(
                        effect.getOutputEffect(), 100, potionEffectStrength)
                );
            }
            case FIRE -> {
                if (target.isOnFire()) return;
                target.setOnFireFor(5);
            }
            case POISON -> {
                if (target.hasStatusEffect(StatusEffects.POISON)) return;
                target.addStatusEffect(new StatusEffectInstance(
                        effect.getOutputEffect(), 100, potionEffectStrength)
                );
            }
            case WEAK -> {
                if (target.hasStatusEffect(StatusEffects.WEAKNESS)) return;
                target.addStatusEffect(new StatusEffectInstance(
                        effect.getOutputEffect(), 100, potionEffectStrength)
                );
            }
            case WITHER -> {
                if (target.hasStatusEffect(StatusEffects.WITHER)) return;
                target.addStatusEffect(new StatusEffectInstance(
                        effect.getOutputEffect(), 100, potionEffectStrength)
                );
            }
        }
    }

    public static List<WeaponEffect> getAllPossibleEffects(Item baseItem) {
        if (!(baseItem instanceof WeaponEffectCrafting baseItemEffects)) return List.of();
        return baseItemEffects.supportedEffects();
    }
}