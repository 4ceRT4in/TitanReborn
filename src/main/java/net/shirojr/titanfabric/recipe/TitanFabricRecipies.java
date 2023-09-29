package net.shirojr.titanfabric.recipe;

import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.recipe.custom.DiamondFurnaceRecipe;
import net.shirojr.titanfabric.util.LoggerUtil;

public class TitanFabricRecipies {
    public static final RecipeType<DiamondFurnaceRecipe> DIAMOND_FURNACE_RECIPE_TYPE = Registry.register(
            Registry.RECIPE_TYPE, new Identifier(TitanFabric.MODID, "diamond_furnace"),
            new RecipeType<DiamondFurnaceRecipe>() {
                @Override
                public String toString() {
                    return "diamond_furnace";
                }
            });

    public static void registerModRecipies() {
        LoggerUtil.devLogger("Registering " + TitanFabric.MODID + " Mod recipies");
    }
}
