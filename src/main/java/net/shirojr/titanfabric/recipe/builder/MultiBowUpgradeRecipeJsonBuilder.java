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
import net.shirojr.titanfabric.recipe.custom.MultiBowUpgradeRecipe;

import java.util.LinkedHashMap;
import java.util.Map;

public class MultiBowUpgradeRecipeJsonBuilder {
    private final Ingredient base;
    private final ItemStack result;
    private final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap<>();

    protected MultiBowUpgradeRecipeJsonBuilder(Ingredient base, ItemStack result) {
        this.base = base;
        this.result = result;
    }

    public static MultiBowUpgradeRecipeJsonBuilder create(Ingredient base, ItemStack result) {
        return new MultiBowUpgradeRecipeJsonBuilder(base, result);
    }

    public MultiBowUpgradeRecipeJsonBuilder criterion(String name, AdvancementCriterion<?> criterion) {
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
        MultiBowUpgradeRecipe recipe = new MultiBowUpgradeRecipe(this.base, this.base, this.result);
        exporter.accept(recipeId, recipe, builder.build(recipeId.withPrefixedPath("recipes/" + RecipeCategory.COMBAT.name().toLowerCase() + "/")));
    }

    private void validate(Identifier recipeId) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + recipeId);
        }
    }
}
