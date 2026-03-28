package net.shirojr.titanfabric.recipe.builder;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.recipe.custom.ItemUpgradeRecipe;

import java.util.LinkedHashMap;
import java.util.Map;

public class ItemUpgradeRecipeJsonBuilder {
    private final RecipeCategory category;
    private final Ingredient base;
    private final Ingredient addition;
    private final ItemStack result;
    private final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap<>();

    protected ItemUpgradeRecipeJsonBuilder(Ingredient base, Ingredient addition, ItemStack result, RecipeCategory category) {
        this.category = category;
        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    public static ItemUpgradeRecipeJsonBuilder create(Ingredient base, Ingredient addition, ItemStack result, RecipeCategory category) {
        return new ItemUpgradeRecipeJsonBuilder(base, addition, result, category);
    }

    public ItemUpgradeRecipeJsonBuilder criterion(String name, AdvancementCriterion<?> criterion) {
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
        ItemUpgradeRecipe recipe = new ItemUpgradeRecipe(this.base, this.addition, this.result);
        exporter.accept(recipeId, recipe, builder.build(recipeId.withPrefixedPath("recipes/" + this.category.getName() + "/")));
    }

    private void validate(Identifier recipeId) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + recipeId);
        }
    }
}
