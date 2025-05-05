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
import net.shirojr.titanfabric.recipe.custom.WeaponEffectRecipe;

import java.util.LinkedHashMap;
import java.util.Map;

public class WeaponEffectRecipeJsonBuilder {
    private final RecipeCategory category;
    private final Ingredient base;
    private final Ingredient addition;
    private final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap<>();

    public WeaponEffectRecipeJsonBuilder(RecipeCategory category, Ingredient base, Ingredient addition) {
        this.category = category;
        this.base = base;
        this.addition = addition;
    }

    public static WeaponEffectRecipeJsonBuilder create(Ingredient base, Ingredient addition, RecipeCategory category) {
        return new WeaponEffectRecipeJsonBuilder(category, base, addition);
    }

    public WeaponEffectRecipeJsonBuilder criterion(String name, AdvancementCriterion<?> criterion) {
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
        WeaponEffectRecipe weaponEffectRecipe = new WeaponEffectRecipe(this.base, this.addition);
        exporter.accept(recipeId, weaponEffectRecipe, builder.build(recipeId.withPrefixedPath("recipes/" + this.category.getName() + "/")));
    }

    private void validate(Identifier recipeId) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + recipeId);
        }
    }
}
