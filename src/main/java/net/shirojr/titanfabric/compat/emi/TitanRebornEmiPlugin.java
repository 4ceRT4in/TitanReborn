package net.shirojr.titanfabric.compat.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.block.TitanFabricBlocks;
import net.shirojr.titanfabric.compat.emi.recipe.DiamondFurnaceEmiRecipe;
import net.shirojr.titanfabric.compat.emi.recipe.EffectEmiRecipe;
import net.shirojr.titanfabric.compat.emi.recipe.WeaponSmithingEmiRecipe;
import net.shirojr.titanfabric.item.TitanFabricItems;
import net.shirojr.titanfabric.recipe.custom.EffectRecipe;
import net.shirojr.titanfabric.recipe.custom.WeaponRecipe;
import net.shirojr.titanfabric.util.LoggerUtil;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffect;
import net.shirojr.titanfabric.util.effects.WeaponEffectData;
import net.shirojr.titanfabric.util.effects.WeaponEffectType;

public class TitanRebornEmiPlugin implements EmiPlugin {
    public static final Identifier TEXTURE = new Identifier("titanfabric", "textures/gui/emi_recipes.png");

    public static final EmiStack DIAMOND_FURNACE_WORKSTATION = EmiStack.of(TitanFabricBlocks.DIAMOND_FURNACE);
    public static final EmiStack NETHERITE_ANVIL_WORKSTATION = EmiStack.of(TitanFabricBlocks.NETHERITE_ANVIL);

    public static final EmiRecipeCategory DIAMOND_FURNACE_CATEGORY = new EmiRecipeCategory(
            new Identifier("titanfabric", "diamond_furnace_recipes"),
            DIAMOND_FURNACE_WORKSTATION,
            new EmiTexture(TEXTURE, 0, 0, 16, 16)
    );

    public static final EmiRecipeCategory NETHERITE_ANVIL_CATEGORY = new EmiRecipeCategory(
            new Identifier("titanfabric", "netherite_anvil_recipes"),
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
        for (Recipe<?> recipe : recipes) {
            if (recipe instanceof EffectRecipe effectRecipe) {
                registry.addRecipe(new EffectEmiRecipe(effectRecipe, VanillaEmiRecipeCategories.CRAFTING, WeaponEffect.BLIND, "blind_upgrade"));
                registry.addRecipe(new EffectEmiRecipe(effectRecipe, VanillaEmiRecipeCategories.CRAFTING, WeaponEffect.POISON, "poison_upgrade"));
                registry.addRecipe(new EffectEmiRecipe(effectRecipe, VanillaEmiRecipeCategories.CRAFTING, WeaponEffect.WITHER, "wither_upgrade"));
                registry.addRecipe(new EffectEmiRecipe(effectRecipe, VanillaEmiRecipeCategories.CRAFTING, WeaponEffect.WEAK, "weak_upgrade"));
                if (!effectRecipe.base.ingredient().test(new ItemStack(Items.ARROW))) {
                    registry.addRecipe(new EffectEmiRecipe(effectRecipe, VanillaEmiRecipeCategories.CRAFTING, WeaponEffect.FIRE, "fire_upgrade"));
                }

                LoggerUtil.devLogger("Registered EMI recipe: " + recipe.getId());
            }
            if (recipe instanceof WeaponRecipe weaponRecipe) {
                for (WeaponEffect entry : EffectHelper.getAllPossibleEffects(weaponRecipe.getOutput().getItem())) {
                    WeaponEffectData data = new WeaponEffectData(WeaponEffectType.INNATE_EFFECT, entry, 1);
                    ItemStack effectStack = EffectHelper.applyEffectToStack(new ItemStack(TitanFabricItems.ESSENCE), data);

                    WeaponSmithingEmiRecipe baseEmiSwordRecipe = new WeaponSmithingEmiRecipe(weaponRecipe, effectStack, data.weaponEffect().getId());
                    WeaponSmithingEmiRecipe upgradeEmiSwordRecipe = new WeaponSmithingEmiRecipe(weaponRecipe,
                            baseEmiSwordRecipe.getOutputs().stream().findAny().get().getItemStack(), effectStack,
                            data.weaponEffect().getId() + "/second");

                    registry.addRecipe(baseEmiSwordRecipe);
                    registry.addRecipe(upgradeEmiSwordRecipe);
                }
            }
            if (recipe instanceof SmeltingRecipe) {
                registry.addRecipe(new DiamondFurnaceEmiRecipe((AbstractCookingRecipe) recipe, DIAMOND_FURNACE_CATEGORY, 2, false));
            }
        }

        LoggerUtil.devLogger("Done registering TitanReborn custom EMI recipes");
    }
}

