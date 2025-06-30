package net.shirojr.titanfabric.init;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.recipe.custom.EffectRecipe;
import net.shirojr.titanfabric.recipe.custom.EffectUpgradeRecipe;
import net.shirojr.titanfabric.recipe.custom.MultiBowUpgradeRecipe;

public interface TitanFabricRecipeSerializers {
    RecipeSerializer<EffectRecipe> EFFECT = register("effect", new EffectRecipe.Serializer());
    RecipeSerializer<EffectUpgradeRecipe> EFFECT_UPGRADE = register("effect_upgrade", new EffectUpgradeRecipe.Serializer());
    RecipeSerializer<MultiBowUpgradeRecipe> MULTI_BOW_UPGRADE = register("multi_bow_upgrade", new MultiBowUpgradeRecipe.Serializer());


    private static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(String name, S serializer) {
        return Registry.register(Registries.RECIPE_SERIALIZER, TitanFabric.getId(name), serializer);
    }

    static void initialize() {
        // static initialisation
    }
}
