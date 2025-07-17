package net.shirojr.titanfabric.init;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.recipe.custom.ArmorPlatingRecipe;
import net.shirojr.titanfabric.recipe.custom.WeaponEffectRecipe;

public interface TitanFabricRecipeSerializers {
    RecipeSerializer<WeaponEffectRecipe> WEAPON_EFFECT = register("weapon_effect", new WeaponEffectRecipe.Serializer());
    RecipeSerializer<ArmorPlatingRecipe> ARMOR_PLATING = register("armor_plating", new ArmorPlatingRecipe.Serializer());


    private static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(String name, S serializer) {
        return Registry.register(Registries.RECIPE_SERIALIZER, TitanFabric.getId(name), serializer);
    }

    static void initialize() {
        // static initialisation
    }
}
