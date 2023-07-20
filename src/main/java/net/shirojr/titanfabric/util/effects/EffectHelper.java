package net.shirojr.titanfabric.util.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.item.custom.TitanFabricArrowItem;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Helper class for TitanFabric Weapon Effects.<br><br>
 */
public class EffectHelper {
    public static final String EFFECTS_NBT_KEY = TitanFabric.MODID + ".effect";
    public static final String EFFECTS_STRENGTH_NBT_KEY = TitanFabric.MODID + ".effect.strength";

    /**
     * Returns the Weapon Effect strength. It does not translate directly to StatusEffect's amplifier!<br><br>
     * It is used for StatusEffect duration and amplifier calculation
     *
     * @param itemStack
     * @return Strength of the Weapon Effect
     */
    public static int getEffectStrength(ItemStack itemStack) {
        return itemStack.getOrCreateNbt().getInt(EFFECTS_STRENGTH_NBT_KEY);
    }

    public static ItemStack setEffectStrength(ItemStack itemStack, int strength) {
        itemStack.getOrCreateNbt().putInt(EFFECTS_STRENGTH_NBT_KEY, strength);
        return itemStack;
    }

    public static ItemStack getStackWithEffect(ItemStack itemStack, WeaponEffects effect) {
        itemStack.getOrCreateNbt().putString(EFFECTS_NBT_KEY, effect.getId());
        return itemStack;
    }

    public static boolean stackHasWeaponEffect(ItemStack itemStack) {
        return itemStack.getOrCreateNbt().contains(EFFECTS_NBT_KEY);
    }

    public static List<Text> appendToolTip(List<Text> tooltip, ItemStack stack) {
        String translation = "tooltip.titanfabric." + EffectHelper.getEffectStrength(stack);

        switch (WeaponEffects.getEffect(stack.getOrCreateNbt().getString(EffectHelper.EFFECTS_NBT_KEY))) {
            case BLIND -> translation += "Blind";
            case FIRE -> translation += "Fire";
            case POISON -> translation += "Poison";
            case WEAK -> translation += "Weak";
            case WITHER -> translation += "Wither";
        }

        tooltip.add(new TranslatableText(translation));
        return tooltip;
    }

    private static List<WeaponEffects> getSwordEffects() {
        return Arrays.stream(WeaponEffects.values()).toList();
    }

    private static List<WeaponEffects> getArrowEffects() {
        return Arrays.stream(WeaponEffects.values()).filter(effect -> effect != WeaponEffects.FIRE).toList();
    }

    public static DefaultedList<ItemStack> generateAllEffectVersionStacks(Item baseItem, DefaultedList<ItemStack> stacks) {
        List<WeaponEffects> possibleEffects = getSwordEffects();
        if (baseItem instanceof TitanFabricArrowItem) possibleEffects = getArrowEffects();

        for (WeaponEffects entry : possibleEffects) {
            ItemStack firstEffect = EffectHelper.getStackWithEffect(new ItemStack(baseItem), entry);
            stacks.add(setEffectStrength(firstEffect, 1));
            ItemStack secondEffect = EffectHelper.getStackWithEffect(new ItemStack(baseItem), entry);
            stacks.add(setEffectStrength(secondEffect, 2));
        }
        return stacks;
    }


    public static void applyWeaponEffectOnTargetFromNBT(World world, ItemStack itemStack, LivingEntity user, LivingEntity target) {
        String currentEffect = itemStack.getOrCreateNbt().getString(EFFECTS_NBT_KEY);
        if (currentEffect == null) return;

        int strength = EffectHelper.getEffectStrength(itemStack);

        applyWeaponEffectOnTarget(WeaponEffects.getEffect(currentEffect), strength, world, itemStack, user, target);
    }


    public static void applyWeaponEffectOnTarget(WeaponEffects effect, int effectStrength, World world, ItemStack itemStack, LivingEntity user, LivingEntity target) {
        if (world.isClient() || effect == null) return;


        // chance to apply effect (strength = 1 = 25%, strength = 2 = 50%)
        if(world.getRandom().nextInt(100) >= (25 * effectStrength)){
            return;
        }


        switch (effect) {
            case BLIND -> {
                target.addStatusEffect(new StatusEffectInstance(
                        effect.getStatusEffect(), effectStrength > 1 ? 4 : 10, effectStrength - 1)
                );
            }
            case FIRE -> target.setOnFireFor(effectStrength > 1 ? 10 : 5);
            case POISON -> {
                target.addStatusEffect(new StatusEffectInstance(
                        effect.getStatusEffect(), effectStrength > 1 ? 5 : 10, effectStrength - 1)
                );
            }
            case WEAK -> {
                target.addStatusEffect(new StatusEffectInstance(
                        effect.getStatusEffect(), effectStrength > 1 ? 6 : 10, effectStrength - 1)
                );
            }
            case WITHER -> {
                target.addStatusEffect(new StatusEffectInstance(
                        effect.getStatusEffect(), effectStrength > 1 ? 7 : 10, effectStrength - 1)
                );
            }
        }
    }
}