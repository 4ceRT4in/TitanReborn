package net.shirojr.titanfabric.recipe.builder;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.recipe.custom.EffectUpgradeRecipe;

import java.util.LinkedHashMap;
import java.util.Map;

public class EffectUpgradeRecipeJsonBuilder {
    private final RecipeCategory category;
    private final Ingredient base;
    private final Ingredient addition;
    private final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap<>();

    protected EffectUpgradeRecipeJsonBuilder(RecipeCategory category, Ingredient base, Ingredient addition) {
        this.category = category;
        this.base = base;
        this.addition = addition;
    }

    public static EffectUpgradeRecipeJsonBuilder create(Ingredient base, Ingredient addition, RecipeCategory category) {
        return new EffectUpgradeRecipeJsonBuilder(category, base, addition);
    }

    public EffectUpgradeRecipeJsonBuilder criterion(String name, AdvancementCriterion<?> criterion) {
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
        EffectUpgradeRecipe effectUpgradeRecipe = new EffectUpgradeRecipe(this.base, this.addition);
        exporter.accept(recipeId, effectUpgradeRecipe, builder.build(recipeId.withPrefixedPath("recipes/" + this.category.getName() + "/")));
    }

    private void validate(Identifier recipeId) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + recipeId);
        }
    }
}
