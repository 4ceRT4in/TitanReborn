package net.shirojr.titanfabric.recipe;

import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.recipe.custom.EffectRecipe;
import net.shirojr.titanfabric.recipe.custom.MultiBowRecipe;
import net.shirojr.titanfabric.recipe.custom.WeaponRecipe;
import net.shirojr.titanfabric.util.LoggerUtil;
import net.shirojr.titanfabric.util.recipes.SlotArrangementType;

public class TitanFabricRecipes {
    public static final RecipeType<EffectRecipe> WEAPON_EFFECT_RECIPE_TYPE =
            RecipeType.register(new Identifier(TitanFabric.MODID, "weapon_effect").toString());

    public static final RecipeSerializer<EffectRecipe> ESSENCE_EFFECT_RECIPE_SERIALIZER =
            RecipeSerializer.register(new Identifier(TitanFabric.MODID, "essence_effect").toString(),
                    new EffectRecipe.Serializer(SlotArrangementType.ESSENCE));

    public static final RecipeSerializer<EffectRecipe> ARROW_EFFECT_RECIPE_SERIALIZER =
            RecipeSerializer.register(new Identifier(TitanFabric.MODID, "arrow_effect").toString(),
                    new EffectRecipe.Serializer(SlotArrangementType.ARROW));

    public static final RecipeSerializer<WeaponRecipe> WEAPON_EFFECT_RECIPE_SERIALIZER =
            RecipeSerializer.register(new Identifier(TitanFabric.MODID, "weapon_effect").toString(),
                    new WeaponRecipe.Serializer());

    public static final RecipeSerializer<MultiBowRecipe> MULTI_BOW_RECIPE_SERIALIZER =
            RecipeSerializer.register(new Identifier(TitanFabric.MODID, "multi_bow_upgrade").toString(),
                    new MultiBowRecipe.Serializer());

    public static void registerModRecipes() {
        LoggerUtil.devLogger("Registering " + TitanFabric.MODID + " Mod recipes");
    }
}