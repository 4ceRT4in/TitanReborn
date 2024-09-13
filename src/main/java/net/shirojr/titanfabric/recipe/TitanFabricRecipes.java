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


public enum TitanFabricRecipes {
    MULTI_BOW_UPGRADE_SMITHING("multi_bow_upgrade", MultiBowRecipe.Serializer.INSTANCE, MultiBowRecipe.Type.INSTANCE),
    WEAPON_EFFECT_SMITHING("weapon_effect", WeaponRecipe.Serializer.INSTANCE, WeaponRecipe.Type.INSTANCE),
    ESSENCE_EFFECT_CRAFTING("essence_effect", EffectRecipe.Serializer.ESSENCE_EFFECT_INSTANCE, EffectRecipe.Type.ESSENCE_EFFECT_INSTANCE),
    ARROW_EFFECT_CRAFTING("arrow_effect", EffectRecipe.Serializer.ARROW_EFFECT_INSTANCE, EffectRecipe.Type.ARROW_EFFECT_INSTANCE);

    private final RecipeData data;

    TitanFabricRecipes(String name, RecipeSerializer<?> serializer, RecipeType<?> type) {
        this.data = new RecipeData(name, serializer, type);
    }

    public Identifier getIdentifier() {
        return this.data.identifier();
    }

    public RecipeData getRecipeData() {
        return this.data;
    }

    public static void registerModRecipes() {
        LoggerUtil.devLogger("Registering %s Mod recipes".formatted(TitanFabric.MODID));
    }

    public record RecipeData(Identifier identifier, RecipeSerializer<?> serializer, RecipeType<?> type) {
        public RecipeData(String name, RecipeSerializer<?> serializer, RecipeType<?> type) {
            this(new Identifier(TitanFabric.MODID, name), serializer, type);
            register();
        }

        public void register() {
            Registry.register(Registry.RECIPE_SERIALIZER, this.identifier, this.serializer);
            Registry.register(Registry.RECIPE_TYPE, this.identifier, this.type);

        }
    }
}
