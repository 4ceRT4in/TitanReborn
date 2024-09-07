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

@SuppressWarnings("unused")
public class TitanFabricRecipes {

    public static final RecipeData MULTI_BOW_UPGRADE_SMITHING = new RecipeData(new Identifier(TitanFabric.MODID, "multi_bow_upgrade"),
            MultiBowRecipe.Serializer.INSTANCE, MultiBowRecipe.Type.INSTANCE);

    public static final RecipeData WEAPON_EFFECT_SMITHING = new RecipeData(new Identifier(TitanFabric.MODID, "weapon_effect"),
            WeaponRecipe.Serializer.INSTANCE, WeaponRecipe.Type.INSTANCE);

    public static final RecipeData ESSENCE_EFFECT_CRAFTING = new RecipeData(new Identifier(TitanFabric.MODID, "essence_effect"),
            EffectRecipe.Serializer.ESSENCE_EFFECT_INSTANCE, EffectRecipe.Type.ESSENCE_EFFECT_INSTANCE);

    public static final RecipeData ARROW_EFFECT_CRAFTING = new RecipeData(new Identifier(TitanFabric.MODID, "arrow_effect"),
            EffectRecipe.Serializer.ARROW_EFFECT_INSTANCE, EffectRecipe.Type.ARROW_EFFECT_INSTANCE);


    public record RecipeData(Identifier identifier, RecipeSerializer<?> serializer, RecipeType<?> type) {

        public void register() {
            Registry.register(Registry.RECIPE_SERIALIZER, this.identifier, this.serializer);
            Registry.register(Registry.RECIPE_TYPE, this.identifier, this.type);

        }
    }

    public static void registerModRecipes() {
        LoggerUtil.devLogger("Registering %s Mod recipes".formatted(TitanFabric.MODID));
        MULTI_BOW_UPGRADE_SMITHING.register();
        WEAPON_EFFECT_SMITHING.register();
        ESSENCE_EFFECT_CRAFTING.register();
        ARROW_EFFECT_CRAFTING.register();
    }
}