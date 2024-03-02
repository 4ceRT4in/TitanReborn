package net.shirojr.titanfabric.recipe;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.recipe.custom.DiamondFurnaceRecipe;
import net.shirojr.titanfabric.recipe.custom.EssenceRecipe;
import net.shirojr.titanfabric.recipe.custom.WeaponRecipe;
import net.shirojr.titanfabric.util.LoggerUtil;

public class TitanFabricRecipes {
    public static final RecipeType<DiamondFurnaceRecipe> DIAMOND_FURNACE_RECIPE_TYPE = registerRecipe("diamond_furnace");
    public static final RecipeType<EssenceRecipe> WEAPON_EFFECT_RECIPE_TYPE = RecipeType.register("weapon_effect");

    public static final RecipeSerializer<EssenceRecipe> ESSENCE_EFFECT_RECIPE_SERIALIZER =
            RecipeSerializer.register("essence_effect", new EssenceRecipe.Serializer());
    public static final RecipeSerializer<WeaponRecipe> WEAPON_EFFECT_RECIPE_SERIALIZER =
            RecipeSerializer.register("weapon_effect", new WeaponRecipe.Serializer());

    private static <T extends Recipe<?>> RecipeType<T> registerRecipe(String id) {
        return Registry.register(Registry.RECIPE_TYPE, new Identifier(TitanFabric.MODID, id),
                new RecipeType<T>() {
                    @Override
                    public String toString() {
                        return id;
                    }
                });
    }

    public static void registerModRecipes() {
        LoggerUtil.devLogger("Registering " + TitanFabric.MODID + " Mod recipes");
    }
}
