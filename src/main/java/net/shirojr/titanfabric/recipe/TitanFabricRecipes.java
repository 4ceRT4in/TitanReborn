package net.shirojr.titanfabric.recipe;

import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.recipe.custom.EffectRecipe;
import net.shirojr.titanfabric.recipe.custom.MultiBowRecipe;
import net.shirojr.titanfabric.recipe.custom.WeaponRecipe;
import net.shirojr.titanfabric.util.LoggerUtil;
import net.shirojr.titanfabric.util.recipes.SlotArrangementType;

public class TitanFabricRecipes {
    public static final RecipeType<EffectRecipe> WEAPON_EFFECT_RECIPE_TYPE = RecipeType.register("weapon_effect");

    public static final RecipeSerializer<EffectRecipe> ESSENCE_EFFECT_RECIPE_SERIALIZER =
            RecipeSerializer.register("essence_effect", new EffectRecipe.Serializer(SlotArrangementType.ESSENCE));
    public static final RecipeSerializer<EffectRecipe> ARROW_EFFECT_RECIPE_SERIALIZER =
            RecipeSerializer.register("arrow_effect", new EffectRecipe.Serializer(SlotArrangementType.ARROW));
    public static final RecipeSerializer<WeaponRecipe> WEAPON_EFFECT_RECIPE_SERIALIZER =
            RecipeSerializer.register("weapon_effect", new WeaponRecipe.Serializer());
    public static final RecipeSerializer<MultiBowRecipe> MULTI_BOW_RECIPE_SERIALIZER =
            RecipeSerializer.register("multi_bow_upgrade", new MultiBowRecipe.Serializer());


    public static void registerModRecipes() {
        LoggerUtil.devLogger("Registering " + TitanFabric.MODID + " Mod recipes");
    }
}
