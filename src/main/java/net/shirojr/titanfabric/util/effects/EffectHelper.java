package net.shirojr.titanfabric.util.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.shirojr.titanfabric.item.custom.TitanFabricArrowItem;
import net.shirojr.titanfabric.item.custom.TitanFabricEssenceItem;
import net.shirojr.titanfabric.item.custom.TitanFabricSwordItem;
import net.shirojr.titanfabric.util.LoggerUtil;
import net.shirojr.titanfabric.util.items.WeaponEffectCrafting;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;

import static net.shirojr.titanfabric.util.effects.WeaponEffectData.*;

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
        NbtCompound compound = getWeaponEffectDataCompound(itemStack);
        WeaponEffectType weaponEffectType = WeaponEffectType.getType(type.getNbtKey());
        if (!compound.getKeys().contains(type.getNbtKey())) return -1;
        if (weaponEffectType == null) return -1;
        Optional<WeaponEffectData> data = WeaponEffectData.fromNbt(compound, weaponEffectType);
        return data.map(WeaponEffectData::strength).orElse(-1);
    }

    /**
     * Sets the {@linkplain WeaponEffect TitanFabric WeaponEffect} strength. It does not translate directly to StatusEffect's amplifier!<br><br>
     * Used to write custom NBT information to the ItemStack
     */
    @SuppressWarnings("unused")
    public static ItemStack setEffectStrength(ItemStack itemStack, WeaponEffectType type, int strength) {
        NbtCompound originalNbt = getWeaponEffectDataCompound(itemStack);
        if (!originalNbt.contains(type.getNbtKey())) return itemStack;
        if (!originalNbt.getCompound(type.getNbtKey()).contains(EFFECTS_STRENGTH_NBT_KEY)) return itemStack;
        originalNbt.getCompound(type.getNbtKey()).putInt(EFFECTS_STRENGTH_NBT_KEY, strength);
        return itemStack;
    }

    public static NbtCompound getWeaponEffectDataCompound(ItemStack stack) {
        return stack.getOrCreateNbt().getCompound(EFFECTS_COMPOUND_NBT_KEY);
    }

    public static int getColor(WeaponEffect weaponEffect) {
        StatusEffect effect = weaponEffect.getOutputEffect();
        if (weaponEffect == WeaponEffect.FIRE) return PotionUtil.getColor(Potions.FIRE_RESISTANCE);
        if(effect == null) return -1;
        return effect.getColor();
    }

    /**
     * Sets the {@linkplain WeaponEffectData TitanFabric WeaponEffectData}.<br><br>
     * Used to write custom NBT information to the ItemStack
     */
    public static ItemStack getStackWithEffects(ItemStack itemStack, List<WeaponEffectData> effectDataList) {
        if (itemStack.getItem() instanceof TitanFabricSwordItem titanFabricSwordItem) {
            if (!titanFabricSwordItem.canHaveWeaponEffects()) return itemStack;
        }
        NbtCompound stackNbt = itemStack.getOrCreateNbt();
        for (WeaponEffectData entry : effectDataList) {
            NbtCompound newTypeCompound = new NbtCompound();
            newTypeCompound.putString(EFFECT_NBT_KEY, entry.weaponEffect().getId());
            newTypeCompound.putInt(EFFECTS_STRENGTH_NBT_KEY, entry.strength());
            if (!stackNbt.contains(EFFECTS_COMPOUND_NBT_KEY)) {
                NbtCompound freshCompound = new NbtCompound();
                freshCompound.put(entry.type().getNbtKey(), newTypeCompound);
                stackNbt.put(EFFECTS_COMPOUND_NBT_KEY, freshCompound);
            } else {
                NbtCompound compound = stackNbt.getCompound(EFFECTS_COMPOUND_NBT_KEY);
                compound.put(entry.type().getNbtKey(), newTypeCompound);
            }
        }
        return itemStack;
    }

    public static ItemStack applyEffectToStack(ItemStack itemStack, WeaponEffectData effectData) {
        return getStackWithEffects(itemStack, List.of(effectData));
    }

    public static ItemStack removeEffectsFromStack(ItemStack itemStack) {
        ItemStack output = itemStack.copy();
        output.getOrCreateNbt().remove(EFFECTS_COMPOUND_NBT_KEY);
        return output;
    }

    @Nullable
    public static WeaponEffect getWeaponEffectFromPotion(ItemStack stack) {
        if (!stack.isOf(Items.POTION)) return null;
        List<StatusEffectInstance> statusEffects = PotionUtil.getPotionEffects(stack);
        if (statusEffects.size() > 1) {
            LoggerUtil.devLogger("Potion had more then one StatusEffect", true, null);
            return null;
        }
        for (var entry : WeaponEffect.values()) {
            if (entry.getIngredientEffect() == null) continue;
            if (entry.getIngredientEffect().equals(statusEffects.get(0).getEffectType())) return entry;
        }
        LoggerUtil.devLogger("Couldn't find matching potion effect to map to WeaponEffect");
        return null;
    }

    public static ItemStack getPotionFromWeaponEffect(WeaponEffect weaponEffect) {
        // Check if the weaponEffect has a valid ingredient effect
        if (weaponEffect.getIngredientEffect() == null) {
            LoggerUtil.devLogger("WeaponEffect has no associated IngredientEffect", true, null);
            return ItemStack.EMPTY;
        }

        // Create a potion item stack
        ItemStack potionStack = new ItemStack(Items.POTION);

        // Set a base potion type using a switch-like structure
        if (weaponEffect.getIngredientEffect() == StatusEffects.NIGHT_VISION || weaponEffect.getIngredientEffect() == StatusEffects.BLINDNESS) {
            // BLIND effect
            PotionUtil.setPotion(potionStack, Potions.NIGHT_VISION); // Assuming NIGHT_VISION for BLIND effect

        } else if (weaponEffect.getIngredientEffect() == StatusEffects.FIRE_RESISTANCE) {
            // FIRE effect
            PotionUtil.setPotion(potionStack, Potions.FIRE_RESISTANCE);
        } else if (weaponEffect.getIngredientEffect() == StatusEffects.POISON) {
            // POISON effect
            PotionUtil.setPotion(potionStack, Potions.POISON);
        } else if (weaponEffect.getIngredientEffect() == StatusEffects.WEAKNESS) {
            // WEAK effect
            PotionUtil.setPotion(potionStack, Potions.WEAKNESS);
        } else if (weaponEffect.getIngredientEffect() == StatusEffects.INSTANT_DAMAGE || weaponEffect.getIngredientEffect() == StatusEffects.WITHER) {
            // WITHER effect
            PotionUtil.setPotion(potionStack, Potions.HARMING); // Assuming INSTANT_DAMAGE for WITHER
        } else {
            LoggerUtil.devLogger("No matching StatusEffect for WeaponEffect: " + weaponEffect.name());
            return ItemStack.EMPTY;
        }

        // Log if needed
        LoggerUtil.devLogger("Potion created for WeaponEffect: " + weaponEffect.name());

        return potionStack;
    }


    /**
     * @return True, if the ItemStack contains any {@linkplain WeaponEffect TitanFabric WeaponEffect}
     */
    public static boolean stackHasNoWeaponEffectData(NbtCompound nbtCompound) {
        return !nbtCompound.contains(EFFECTS_COMPOUND_NBT_KEY);
    }

    public static boolean stackHasWeaponEffect(ItemStack itemStack) {
        if (!itemStack.hasNbt()) return false;
        NbtCompound nbt = itemStack.getNbt();
        if (nbt == null || !nbt.contains(EFFECTS_COMPOUND_NBT_KEY)) return false;
        NbtCompound compound = nbt.getCompound(EFFECTS_COMPOUND_NBT_KEY);
        for (String nbtKey : compound.getKeys()) {
            WeaponEffectType type = WeaponEffectType.getType(nbtKey);
            if (type == null) continue;
            WeaponEffect weaponEffectCompound = WeaponEffect.getEffect(compound.getCompound(type.getNbtKey()).getString(EFFECT_NBT_KEY));
            if (weaponEffectCompound == null) continue;
            return true;
        }
        return false;
    }

    public static boolean shouldEffectApply(Random random, int strength) {
        return random.nextInt(100) <= (25 * strength);
    }

    /**
     * Generates a List of ItemStacks from a single base ItemStack. The List contains all possible variants of the
     * base ItemStack in combination with the {@linkplain WeaponEffect TitanFabric WeaponEffect}
     *
     * @param baseItem original ItemStack
     * @param stacks   list of all registered ItemStacks.
     */
    public static void generateAllEffectVersionStacks(Item baseItem, DefaultedList<ItemStack> stacks, boolean addBaseItem) {
        if (!(baseItem instanceof WeaponEffectCrafting baseItemEffects)) return;
        List<WeaponEffect> possibleEffects = baseItemEffects.supportedEffects();
        if (baseItem instanceof TitanFabricArrowItem) {
            for (WeaponEffect entry : possibleEffects) {
                WeaponEffectData data = new WeaponEffectData(WeaponEffectType.INNATE_EFFECT, entry, 2);
                ItemStack effectStack = EffectHelper.applyEffectToStack(new ItemStack(baseItem), data);
                stacks.add(effectStack);
            }
        } else if (baseItem instanceof TitanFabricEssenceItem) {
            for (WeaponEffect entry : possibleEffects) {
                WeaponEffectData data = new WeaponEffectData(WeaponEffectType.INNATE_EFFECT, entry, 1);
                ItemStack effectStack = EffectHelper.applyEffectToStack(new ItemStack(baseItem), data);
                stacks.add(effectStack);
            }
        } else if (baseItem instanceof TitanFabricSwordItem swordItem) {
            ItemStack onlyInnateItemStack = new ItemStack(baseItem);
            if (addBaseItem) {
                if (swordItem.getBaseEffect() != null) {
                    WeaponEffectData innateEffectData = new WeaponEffectData(WeaponEffectType.INNATE_EFFECT, swordItem.getBaseEffect(), 1);
                    EffectHelper.applyEffectToStack(onlyInnateItemStack, innateEffectData);
                }
                stacks.add(onlyInnateItemStack);
            }
            for (WeaponEffect entry : possibleEffects) {
                for (int effectStrengthVersion = 1; effectStrengthVersion < 3; effectStrengthVersion++) {
                    WeaponEffectData additionalEffectData = new WeaponEffectData(WeaponEffectType.ADDITIONAL_EFFECT, entry, effectStrengthVersion);
                    ItemStack effectItemStack = EffectHelper.applyEffectToStack(new ItemStack(baseItem), additionalEffectData);
                    if (swordItem.getBaseEffect() != null) {
                        WeaponEffectData innateData = new WeaponEffectData(WeaponEffectType.INNATE_EFFECT, swordItem.getBaseEffect(), 1);
                        EffectHelper.applyEffectToStack(effectItemStack, innateData);
                    }
                    stacks.add(effectItemStack);
                }
            }
        } else {
            stacks.add(new ItemStack(baseItem));
            for (WeaponEffect entry : possibleEffects) {
                WeaponEffectData firstData = new WeaponEffectData(WeaponEffectType.ADDITIONAL_EFFECT, entry, 0);
                WeaponEffectData secondData = new WeaponEffectData(WeaponEffectType.ADDITIONAL_EFFECT, entry, 1);
                ItemStack firstEffectStack = EffectHelper.applyEffectToStack(new ItemStack(baseItem), firstData);
                ItemStack secondEffectStack = EffectHelper.applyEffectToStack(new ItemStack(baseItem), secondData);
                stacks.add(firstEffectStack);
                stacks.add(secondEffectStack);
            }
        }
    }

    public static void applyWeaponEffectsOnTarget(World world, ItemStack itemStack, LivingEntity target) {
        NbtCompound compound = itemStack.getOrCreateNbt().getCompound(EFFECTS_COMPOUND_NBT_KEY);
        for (String nbtKey : compound.getKeys()) {
            WeaponEffectType type = WeaponEffectType.getType(nbtKey);
            if (type == null) continue;
            String currentEffect = compound.getCompound(nbtKey).getString(WeaponEffectData.EFFECT_NBT_KEY);
            int strength = EffectHelper.getEffectStrength(itemStack, type);
            WeaponEffectData data = new WeaponEffectData(type, WeaponEffect.getEffect(currentEffect), strength);
            applyWeaponEffectOnTarget(data, world, target, itemStack.getItem() instanceof ArrowItem, itemStack.getItem() instanceof ArrowItem);
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

    public static boolean haveSameEffects(ItemStack stack1, ItemStack stack2) {
        if (!stackHasWeaponEffect(stack1) || !stackHasWeaponEffect(stack2)) return false;
        return getWeaponEffectDataCompound(stack1).equals(getWeaponEffectDataCompound(stack2));
    }

    public static List<WeaponEffect> getAllPossibleEffects(Item baseItem) {
        if (!(baseItem instanceof WeaponEffectCrafting baseItemEffects)) return List.of();
        return baseItemEffects.supportedEffects();
    }
}