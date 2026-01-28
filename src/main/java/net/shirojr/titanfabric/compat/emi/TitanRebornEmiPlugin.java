package net.shirojr.titanfabric.compat.emi;

import com.google.common.collect.Lists;
import dev.emi.emi.EmiPort;
import dev.emi.emi.EmiUtil;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.recipe.EmiSmithingRecipe;
import dev.emi.emi.runtime.EmiReloadLog;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.*;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.init.TitanFabricBlocks;
import net.shirojr.titanfabric.init.TitanFabricDataComponents;
import net.shirojr.titanfabric.init.TitanFabricItems;
import net.shirojr.titanfabric.item.custom.sword.CitrinSwordItem;
import net.shirojr.titanfabric.item.custom.sword.EmberSwordItem;
import net.shirojr.titanfabric.recipe.custom.EffectRecipe;
import net.shirojr.titanfabric.util.LoggerUtil;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffect;
import net.shirojr.titanfabric.util.effects.WeaponEffectData;
import net.shirojr.titanfabric.util.effects.WeaponEffectType;

import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

public class TitanRebornEmiPlugin implements EmiPlugin {
    public static final Identifier TEXTURE = TitanFabric.getId("textures/gui/emi_recipes.png");

    public static final EmiStack DIAMOND_FURNACE_WORKSTATION = EmiStack.of(TitanFabricBlocks.DIAMOND_FURNACE);
    public static final EmiStack NETHERITE_ANVIL_WORKSTATION = EmiStack.of(TitanFabricBlocks.NETHERITE_ANVIL);

    public static final EmiRecipeCategory DIAMOND_FURNACE_CATEGORY = new EmiRecipeCategory(
            TitanFabric.getId("diamond_furnace_recipes"),
            DIAMOND_FURNACE_WORKSTATION,
            new EmiTexture(TEXTURE, 0, 0, 16, 16)
    );

    public static final EmiRecipeCategory NETHERITE_ANVIL_CATEGORY = new EmiRecipeCategory(
            TitanFabric.getId("netherite_anvil_recipes"),
            NETHERITE_ANVIL_WORKSTATION,
            new EmiTexture(TEXTURE, 0, 0, 16, 16)
    );

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(DIAMOND_FURNACE_CATEGORY);
        registry.addCategory(NETHERITE_ANVIL_CATEGORY);
        registry.addWorkstation(DIAMOND_FURNACE_CATEGORY, DIAMOND_FURNACE_WORKSTATION);
        registry.addWorkstation(NETHERITE_ANVIL_CATEGORY, NETHERITE_ANVIL_WORKSTATION);

        RecipeManager recipeManager = registry.getRecipeManager();

        LoggerUtil.devLogger("Registering TitanReborn custom EMI recipes");
        var recipes = recipeManager.values();
        for (RecipeEntry<?> entry : recipes) {
            Recipe<?> recipe = entry.value();
            if (recipe instanceof EffectRecipe effectRecipe) {
                LoggerUtil.devLogger("Registered EMI recipe: " + entry.id());
            }
            if (recipe instanceof SmeltingRecipe) {
            }
            if (recipe instanceof RepairItemRecipe repairItemRecipe) {

            }
        }

        List<Enchantment> targetedEnchantments = Lists.newArrayList();
        List<Enchantment> universalEnchantments = Lists.newArrayList();
        for (Enchantment enchantment : EmiPort.getEnchantmentRegistry().stream().toList()) {
            try {
                if (enchantment.isAcceptableItem(ItemStack.EMPTY)) {
                    universalEnchantments.add(enchantment);
                    continue;
                }
            } catch (Throwable ignored) {
            }
            targetedEnchantments.add(enchantment);
        }
        for (Item i : EmiPort.getItemRegistry()) {
            try {
                if (i.getComponents().getOrDefault(DataComponentTypes.MAX_DAMAGE, 0) > 0) {
                    if (i instanceof ArmorItem ai && ai.getMaterial() != null && ai.getMaterial().value().repairIngredient().get() != null && !ai.getMaterial().value().repairIngredient().get().isEmpty()) {
                        registry.addRecipe(new NetheriteAnvilRecipe(EmiStack.of(i), EmiIngredient.of(ai.getMaterial().value().repairIngredient().get()), TitanFabric.getId("/netherite_anvil/repairing/material" + EmiUtil.subId(i) + "/" + EmiUtil.subId(ai.getMaterial().value().repairIngredient().get().getMatchingStacks()[0].getItem()))));
                    } else if (i instanceof ToolItem ti && ti.getMaterial().getRepairIngredient() != null && !ti.getMaterial().getRepairIngredient().isEmpty()) {
                        registry.addRecipe(new NetheriteAnvilRecipe(EmiStack.of(i), EmiIngredient.of(ti.getMaterial().getRepairIngredient()), TitanFabric.getId("/netherite_anvil/repairing/material" + EmiUtil.subId(i) + "/" + EmiUtil.subId(ti.getMaterial().getRepairIngredient().getMatchingStacks()[0].getItem()))));

                    }
                }
            } catch (Throwable ignored) {
            }

            try {
                ItemStack defaultStack = i.getDefaultStack();
                Consumer<Enchantment> consumer = e -> {
                    int max = e.getMaxLevel();
                    registry.addRecipe(new NetheriteAnvilEnchantRecipe(i, e, max, TitanFabric.getId("/netherite_anvil/enchanting" + EmiUtil.subId(i) + "/" + EmiUtil.subId(EmiPort.getEnchantmentRegistry().getId(e)) + "/" + max)));
                };
                for (Enchantment e : targetedEnchantments) {
                    if (e.isAcceptableItem(defaultStack)) {
                        consumer.accept(e);
                    }
                }
            } catch (Throwable t) {
                EmiReloadLog.warn("Exception thrown registering enchantment recipes", t);
            }
        }

        addEssenceRecipe(registry, Potions.POISON, WeaponEffect.POISON, "poison_essence");
        addEssenceRecipe(registry, Potions.NIGHT_VISION, WeaponEffect.BLIND, "blindness_essence");
        addEssenceRecipe(registry, Potions.FIRE_RESISTANCE, WeaponEffect.FIRE, "fire_resistance_essence");
        addEssenceRecipe(registry, Potions.HARMING, WeaponEffect.WITHER, "wither_essence");
        addEssenceRecipe(registry, Potions.WEAKNESS, WeaponEffect.WEAK, "weakness_essence");

        addArrowRecipe(registry, Potions.POISON, WeaponEffect.POISON, "poison_arrow");
        addArrowRecipe(registry, Potions.NIGHT_VISION, WeaponEffect.BLIND, "blindness_arrow");
        addArrowRecipe(registry, Potions.FIRE_RESISTANCE, WeaponEffect.FIRE, "fire_resistance_arrow");
        addArrowRecipe(registry, Potions.HARMING, WeaponEffect.WITHER, "wither_arrow");
        addArrowRecipe(registry, Potions.WEAKNESS, WeaponEffect.WEAK, "weakness_arrow");

        for (SwordItem effectSword : TitanFabricItems.EFFECT_SWORDS) {
            Identifier id = Registries.ITEM.getId(effectSword);

            for (WeaponEffect weaponEffect : WeaponEffect.values()) {
                for (int i = 1; i <= 2; i++) {

                    ItemStack output = new ItemStack(effectSword);
                    HashSet<WeaponEffectData> outputWeaponEffects = new HashSet<>(output.getOrDefault(TitanFabricDataComponents.WEAPON_EFFECTS, new HashSet<>()));

                    if (output.getItem() instanceof CitrinSwordItem) {
                        outputWeaponEffects.add(new WeaponEffectData(WeaponEffectType.INNATE_EFFECT, WeaponEffect.POISON, 1));
                    } else if (output.getItem() instanceof EmberSwordItem) {
                        outputWeaponEffects.add(new WeaponEffectData(WeaponEffectType.INNATE_EFFECT, WeaponEffect.FIRE, 1));
                    }
                    outputWeaponEffects.add(new WeaponEffectData(WeaponEffectType.ADDITIONAL_EFFECT, weaponEffect, i));

                    output.set(TitanFabricDataComponents.WEAPON_EFFECTS, outputWeaponEffects);

                    ItemStack input = new ItemStack(effectSword);
                    if (i == 2) {
                        HashSet<WeaponEffectData> inputWeaponEffects = new HashSet<>(input.getOrDefault(TitanFabricDataComponents.WEAPON_EFFECTS, new HashSet<>()));

                        if (output.getItem() instanceof CitrinSwordItem) {
                            inputWeaponEffects.add(new WeaponEffectData(WeaponEffectType.INNATE_EFFECT, WeaponEffect.POISON, 1));
                        } else if (output.getItem() instanceof EmberSwordItem) {
                            inputWeaponEffects.add(new WeaponEffectData(WeaponEffectType.INNATE_EFFECT, WeaponEffect.FIRE, 1));
                        }
                        inputWeaponEffects.add(new WeaponEffectData(WeaponEffectType.ADDITIONAL_EFFECT, weaponEffect, 1));
                        input.set(TitanFabricDataComponents.WEAPON_EFFECTS, inputWeaponEffects);
                    }
                    registry.addRecipe(new EmiSmithingRecipe(EmiStack.EMPTY, EmiStack.of(input), EmiStack.of(TitanFabricItems.ESSENCE.withEffect(weaponEffect)), EmiStack.of(output), TitanFabric.getId("/" + id.getPath() + "_" + weaponEffect.getId() + "_" + i + "_upgrade")));

                }
            }
        }

        for (SmeltingRecipe recipe : getRecipes(registry, RecipeType.SMELTING)) {
            registry.addRecipe(new DiamondFurnaceRecipe(recipe));
        }

        // Tried to remove sharpness lvl 6 enchantment recipes but those are not loaded at this point so can not be removed here
        // Used EmiAnvilEnchantRecipeMixin instead
        //        CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture = CompletableFuture.supplyAsync(
        //                BuiltinRegistries::createWrapperLookup, Util.getMainWorkerExecutor()
        //        );
        //        try {
        //            RegistryWrapper.Impl<Enchantment> wrapper = completableFuture.get().getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
        //            RegistryEntry<Enchantment> registryEntry = (RegistryEntry<Enchantment>) wrapper.getOptional(Enchantments.POWER).get();
        //            for (Item item : EmiPort.getItemRegistry()) {
        //                if (item.isEnchantable(item.getDefaultStack())) {
        //                    if (registryEntry.value().isAcceptableItem(item.getDefaultStack())) {
        //                        registry.removeRecipes(Identifier.of("emi", "/anvil/enchanting/" + EmiUtil.subId(item) + "/power/6"));
        //                        if (item.getDefaultStack().isOf(Items.BOW)) {
        //                        }
        //                    }
        //                }
        //            }
        //        } catch (InterruptedException | ExecutionException ignored) {
        //        }

        LoggerUtil.devLogger("Done registering TitanReborn custom EMI recipes");
    }

    private static <C extends RecipeInput, T extends Recipe<C>> Iterable<T> getRecipes(EmiRegistry registry, RecipeType<T> type) {
        return registry.getRecipeManager().listAllOfType(type).stream().map(e -> e.value())::iterator;
    }

    private void addEssenceRecipe(EmiRegistry registry, RegistryEntry<Potion> potionType, WeaponEffect effect, String id) {
        EmiStack potion = EmiStack.of(PotionContentsComponent.createStack(Items.POTION, potionType));
        EmiStack blaze = EmiStack.of(Items.BLAZE_POWDER);
        EmiStack empty = EmiStack.EMPTY;

        List<EmiIngredient> inputs = List.of(
                empty, potion, empty,
                potion, blaze, potion,
                empty, potion, empty
        );

        registry.addRecipe(new EmiCraftingRecipe(inputs, EmiStack.of(TitanFabricItems.ESSENCE.withEffect(effect)), TitanFabric.getId("/" + id), false));
    }

    private void addArrowRecipe(EmiRegistry registry, RegistryEntry<Potion> potionType, WeaponEffect effect, String id) {
        EmiStack potion = EmiStack.of(PotionContentsComponent.createStack(Items.POTION, potionType));
        EmiStack arrow = EmiStack.of(Items.ARROW);
        EmiStack empty = EmiStack.EMPTY;

        List<EmiIngredient> inputs = List.of(
                empty, arrow, empty,
                arrow, potion, arrow,
                empty, arrow, empty
        );
        WeaponEffectData additionalEffectData = new WeaponEffectData(WeaponEffectType.INNATE_EFFECT, effect, 2);
        ItemStack output = EffectHelper.applyEffectToStack(new ItemStack(TitanFabricItems.EFFECT_ARROW), additionalEffectData, false);

        registry.addRecipe(new EmiCraftingRecipe(inputs, EmiStack.of(output, 2), TitanFabric.getId("/" + id), false));
    }
}

