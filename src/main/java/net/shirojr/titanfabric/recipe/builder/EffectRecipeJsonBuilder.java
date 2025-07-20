package net.shirojr.titanfabric.recipe.builder;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.recipe.custom.EffectRecipe;
import net.shirojr.titanfabric.util.IngredientModule;

import java.util.LinkedHashMap;
import java.util.Map;

public class EffectRecipeJsonBuilder {
    private final IngredientModule base;
    private final IngredientModule modifier;
    private final EffectRecipe.Result result;
    private final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap<>();

    protected EffectRecipeJsonBuilder(IngredientModule base, IngredientModule modifier, EffectRecipe.Result result) {
        this.base = base;
        this.modifier = modifier;
        this.result = result;
    }

    public static EffectRecipeJsonBuilder create(IngredientModule base, IngredientModule modifier, EffectRecipe.Result result) {
        return new EffectRecipeJsonBuilder(base, modifier, result);
    }

    public EffectRecipeJsonBuilder criterion(String name, AdvancementCriterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    public void offerTo(RecipeExporter exporter, Identifier recipeId) {
        this.validate(recipeId);
        Advancement.Builder builder = exporter.getAdvancementBuilder()
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
        this.criteria.forEach(builder::criterion);
        EffectRecipe effectRecipe = new EffectRecipe(this.base, this.modifier, this.result);
        exporter.accept(recipeId, effectRecipe, builder.build(recipeId.withPrefixedPath("recipes/" + EffectRecipe.CATEGORY.name().toLowerCase() + "/")));
    }

    private void validate(Identifier recipeId) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + recipeId);
        }
    }
}
