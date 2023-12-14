package net.shirojr.titanfabric.util.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.potion.PotionUtil;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.item.custom.TitanFabricArrowItem;
import net.shirojr.titanfabric.item.custom.TitanFabricEssenceItem;
import net.shirojr.titanfabric.util.LoggerUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Helper class for TitanFabric {@linkplain WeaponEffects}
 */
public final class EffectHelper {
    public static final String EFFECTS_NBT_KEY = TitanFabric.MODID + ".effect";
    public static final String EFFECTS_STRENGTH_NBT_KEY = TitanFabric.MODID + ".effect.strength";

    private EffectHelper() {
        // private ctor to avoid instantiating this utility class
    }

    /**
     * Returns {@linkplain WeaponEffects TitanFabric WeaponEffect} strength. It does not translate directly to StatusEffect's amplifier!<br><br>
     * Used for StatusEffect duration and amplifier calculation
     *
     * @param itemStack
     * @return Strength of the Weapon Effect
     */
    public static int getEffectStrength(ItemStack itemStack) {
        return itemStack.getOrCreateNbt().getInt(EFFECTS_STRENGTH_NBT_KEY);
    }

    /**
     * Sets the {@linkplain WeaponEffects TitanFabric WeaponEffect} strength. It does not translate directly to StatusEffect's amplifier!<br><br>
     * Used to write custom NBT information to the ItemStack
     *
     * @param itemStack
     * @param strength
     * @return
     */
    public static ItemStack setEffectStrength(ItemStack itemStack, int strength) {
        itemStack.getOrCreateNbt().putInt(EFFECTS_STRENGTH_NBT_KEY, strength);
        return itemStack;
    }

    /**
     * Sets the {@linkplain WeaponEffects TitanFabric WeaponEffect}.<br><br>
     * Used to write custom NBT information to the ItemStack
     *
     * @param itemStack
     * @param effect
     * @return
     */
    public static ItemStack getStackWithEffect(ItemStack itemStack, WeaponEffects effect) {
        if (effect == null) {
            LoggerUtil.devLogger("WeaponEffect not valid. Applied no new WeaponEffect to ItemStack", true, null);
            return itemStack;
        }
        itemStack.getOrCreateNbt().putString(EFFECTS_NBT_KEY, effect.getId());
        return itemStack;
    }

    @Nullable
    public static WeaponEffects getWeaponEffectFromPotion(ItemStack stack) {
        if (!stack.isOf(Items.POTION)) return null;
        List<StatusEffectInstance> statusEffects = PotionUtil.getPotionEffects(stack);
        if (statusEffects.size() > 1) {
            LoggerUtil.devLogger("Potion had more then one StatusEffect", true, null);
            return null;
        }

        for (var entry : WeaponEffects.values()) {
            if (entry.getStatusEffect().equals(statusEffects.get(0).getEffectType())) return entry;
        }
        LoggerUtil.devLogger("Couldn't find matching potion effect to map to WeaponEffects");
        return null;
    }

    /**
     * @param nbtCompound
     * @return True, if the ItemStack contains any {@linkplain WeaponEffects TitanFabric WeaponEffect}
     */
    public static boolean stackHasWeaponEffect(NbtCompound nbtCompound) {
        return nbtCompound.contains(EFFECTS_NBT_KEY);
    }

    public static boolean stackHasWeaponEffect(ItemStack itemStack, WeaponEffects effect) {
        if (!itemStack.getOrCreateNbt().contains(EFFECTS_NBT_KEY)) return false;
        return WeaponEffects.getEffect(itemStack.getOrCreateNbt().getString(EFFECTS_NBT_KEY)).equals(effect);
    }

    /**
     * Builds the ToolTip TranslationKey for the Sword ItemStack which has a TitanFabric Weapon Effect
     *
     * @param tooltip original tooltip of the ItemStack
     * @param stack   original ItemStack
     * @return ItemStack with description for current {@linkplain WeaponEffects TitanFabric WeaponEffect}
     */
    public static List<Text> appendSwordToolTip(List<Text> tooltip, ItemStack stack) {
        WeaponEffects effect = WeaponEffects.getEffect(stack.getOrCreateNbt().getString(EffectHelper.EFFECTS_NBT_KEY));
        if (effect == null) return tooltip;
        String translation = "tooltip.titanfabric." + EffectHelper.getEffectStrength(stack);
        switch (effect) {
            case BLIND -> translation += "Blind";
            case FIRE -> translation += "Fire";
            case POISON -> translation += "Poison";
            case WEAK -> translation += "Weak";
            case WITHER -> translation += "Wither";
        }
        tooltip.add(new TranslatableText(translation));
        return tooltip;
    }

    private static List<WeaponEffects> getWeaponEffects() {
        return Arrays.stream(WeaponEffects.values()).toList();
    }

    private static List<WeaponEffects> getArrowEffects() {
        return Arrays.stream(WeaponEffects.values()).filter(effect -> effect != WeaponEffects.FIRE).toList();
    }

    public static boolean shouldEffectApply(Random random, int strength) {
        return random.nextInt(100) <= (25 * strength);
    }

    /**
     * Generates a List of ItemStacks from a single base ItemStack. The List contains all possible variants of the
     * base ItemStack in combination with the {@linkplain WeaponEffects TitanFabric WeaponEffects}
     *
     * @param baseItem original ItemStack
     * @param stacks   list of all registered ItemStacks.
     */
    public static void generateAllEffectVersionStacks(Item baseItem, DefaultedList<ItemStack> stacks) {
        List<WeaponEffects> possibleEffects = getWeaponEffects();
        if (baseItem instanceof TitanFabricArrowItem) possibleEffects = getArrowEffects();

        if (baseItem instanceof TitanFabricArrowItem || baseItem instanceof TitanFabricEssenceItem) {
            for (WeaponEffects entry : possibleEffects) {
                ItemStack effectStack = EffectHelper.getStackWithEffect(new ItemStack(baseItem), entry);
                stacks.add(effectStack);
            }
        } else {
            stacks.add(new ItemStack(baseItem));
            for (WeaponEffects entry : possibleEffects) {
                ItemStack firstEffectStack = EffectHelper.getStackWithEffect(new ItemStack(baseItem), entry);
                ItemStack secondEffectStack = EffectHelper.getStackWithEffect(new ItemStack(baseItem), entry);
                stacks.add(setEffectStrength(firstEffectStack, 1));
                stacks.add(setEffectStrength(secondEffectStack, 2));
            }
        }
    }

    public static void applyWeaponEffectOnTargetFromNBT(World world, ItemStack itemStack, LivingEntity user, LivingEntity target) {
        String currentEffect = itemStack.getOrCreateNbt().getString(EFFECTS_NBT_KEY);
        if (currentEffect == null) return;

        int strength = EffectHelper.getEffectStrength(itemStack);

        applyWeaponEffectOnTarget(WeaponEffects.getEffect(currentEffect), strength, world, itemStack, user, target);
    }

    public static void applyWeaponEffectOnTarget(WeaponEffects effect, int effectStrength, World world, ItemStack itemStack, LivingEntity user, LivingEntity target) {
        if (world.isClient() || effect == null) return;
        if (world.getRandom().nextInt(100) >= (25 * effectStrength)) return;

        //TODO: change values for balancing as needed
        switch (effect) {
            case BLIND -> {
                if (target.hasStatusEffect(StatusEffects.BLINDNESS)) return;
                target.addStatusEffect(new StatusEffectInstance(
                        effect.getStatusEffect(), secondsToTicks(5), effectStrength - 1)
                );
            }
            case FIRE -> {
                if (target.isOnFire()) return;
                target.setOnFireFor(5);
            }
            case POISON -> {
                if (target.hasStatusEffect(StatusEffects.POISON)) return;
                target.addStatusEffect(new StatusEffectInstance(
                        effect.getStatusEffect(), secondsToTicks(5), effectStrength - 1)
                );
            }
            case WEAK -> {
                if (target.hasStatusEffect(StatusEffects.WEAKNESS)) return;
                target.addStatusEffect(new StatusEffectInstance(
                        effect.getStatusEffect(), secondsToTicks(5), effectStrength - 1)
                );
            }
            case WITHER -> {
                if (target.hasStatusEffect(StatusEffects.WITHER)) return;
                target.addStatusEffect(new StatusEffectInstance(
                        effect.getStatusEffect(), secondsToTicks(5), effectStrength - 1)
                );
            }
        }
    }

    /**
     * Translates from seconds to ticks
     *
     * @param seconds
     * @return calculated ticks
     */
    public static int secondsToTicks(int seconds) {
        return seconds * 20;
    }
}