package net.shirojr.titanfabric.compat.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.init.TitanFabricBlocks;
import net.shirojr.titanfabric.recipe.custom.EffectRecipe;
import net.shirojr.titanfabric.util.LoggerUtil;

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
        }

        LoggerUtil.devLogger("Done registering TitanReborn custom EMI recipes");
    }
}

