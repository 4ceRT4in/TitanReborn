package net.shirojr.titanfabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.CookingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.VanillaRecipeProvider;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.init.TitanFabricBlocks;
import net.shirojr.titanfabric.init.TitanFabricItems;
import net.shirojr.titanfabric.recipe.builder.WeaponEffectRecipeJsonBuilder;

import java.util.concurrent.CompletableFuture;

public class TitanFabricRecipeProvider extends FabricRecipeProvider {
    public TitanFabricRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        generateWeaponEffectRecipes(exporter);
        generateDiamondFurnaceRecipes(exporter);
    }


    private void generateDiamondFurnaceRecipes(RecipeExporter exporter) {
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(TitanFabricItems.LEGEND_POWDER), RecipeCategory.MISC,
                TitanFabricItems.LEGEND_INGOT, 0.7F, 200)
                .criterion("has_legend_powder", conditionsFromItem(TitanFabricItems.LEGEND_POWDER)).offerTo(exporter); // for what even are the criterions xD?
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(TitanFabricItems.EMBER_SHARD), RecipeCategory.MISC,
                        TitanFabricItems.EMBER_INGOT, 0.7F, 200)
                .criterion("has_ember_shard", conditionsFromItem(TitanFabricItems.EMBER_SHARD)).offerTo(exporter);
    }

    private void generateWeaponEffectRecipes(RecipeExporter exporter) {
        WeaponEffectRecipeJsonBuilder.create(
                        Ingredient.ofItems(TitanFabricItems.CITRIN_SWORD),
                        Ingredient.ofStacks(TitanFabricItems.ESSENCE.getDefaultStack()), RecipeCategory.COMBAT)
                .criterion(
                        FabricRecipeProvider.hasItem(TitanFabricItems.CITRIN_SHARD),
                        FabricRecipeProvider.conditionsFromItem(TitanFabricItems.CITRIN_SHARD)
                )
                .offerTo(exporter, TitanFabric.getId("citrin_sword"));
        WeaponEffectRecipeJsonBuilder.create(
                        Ingredient.ofItems(TitanFabricItems.CITRIN_GREATSWORD),
                        Ingredient.ofStacks(TitanFabricItems.ESSENCE.getDefaultStack()), RecipeCategory.COMBAT)
                .criterion(
                        FabricRecipeProvider.hasItem(TitanFabricItems.CITRIN_SHARD),
                        FabricRecipeProvider.conditionsFromItem(TitanFabricItems.CITRIN_SHARD)
                )
                .offerTo(exporter, TitanFabric.getId("citrin_greatsword"));
        WeaponEffectRecipeJsonBuilder.create(
                        Ingredient.ofItems(TitanFabricItems.EMBER_SWORD),
                        Ingredient.ofStacks(TitanFabricItems.ESSENCE.getDefaultStack()), RecipeCategory.COMBAT)
                .criterion(
                        FabricRecipeProvider.hasItem(TitanFabricItems.EMBER_SHARD),
                        FabricRecipeProvider.conditionsFromItem(TitanFabricItems.EMBER_SHARD)
                )
                .offerTo(exporter, TitanFabric.getId("ember_sword"));
        WeaponEffectRecipeJsonBuilder.create(
                        Ingredient.ofItems(TitanFabricItems.EMBER_GREATSWORD),
                        Ingredient.ofStacks(TitanFabricItems.ESSENCE.getDefaultStack()), RecipeCategory.COMBAT)
                .criterion(
                        FabricRecipeProvider.hasItem(TitanFabricItems.EMBER_SHARD),
                        FabricRecipeProvider.conditionsFromItem(TitanFabricItems.EMBER_SHARD)
                )
                .offerTo(exporter, TitanFabric.getId("ember_greatsword"));
        WeaponEffectRecipeJsonBuilder.create(
                        Ingredient.ofItems(TitanFabricItems.LEGEND_SWORD),
                        Ingredient.ofStacks(TitanFabricItems.ESSENCE.getDefaultStack()), RecipeCategory.COMBAT)
                .criterion(
                        FabricRecipeProvider.hasItem(TitanFabricItems.LEGEND_INGOT),
                        FabricRecipeProvider.conditionsFromItem(TitanFabricItems.LEGEND_INGOT)
                )
                .offerTo(exporter, TitanFabric.getId("legend_sword"));
        WeaponEffectRecipeJsonBuilder.create(
                        Ingredient.ofItems(TitanFabricItems.LEGEND_GREATSWORD),
                        Ingredient.ofStacks(TitanFabricItems.ESSENCE.getDefaultStack()), RecipeCategory.COMBAT)
                .criterion(
                        FabricRecipeProvider.hasItem(TitanFabricItems.LEGEND_INGOT),
                        FabricRecipeProvider.conditionsFromItem(TitanFabricItems.LEGEND_INGOT)
                )
                .offerTo(exporter, TitanFabric.getId("legend_greatsword"));
        WeaponEffectRecipeJsonBuilder.create(
                        Ingredient.ofItems(Items.DIAMOND_SWORD),
                        Ingredient.ofStacks(TitanFabricItems.ESSENCE.getDefaultStack()), RecipeCategory.COMBAT)
                .criterion(
                        FabricRecipeProvider.hasItem(Items.DIAMOND),
                        FabricRecipeProvider.conditionsFromItem(Items.DIAMOND)
                )
                .offerTo(exporter, TitanFabric.getId("diamond_sword"));
        WeaponEffectRecipeJsonBuilder.create(
                        Ingredient.ofItems(TitanFabricItems.DIAMOND_GREATSWORD),
                        Ingredient.ofStacks(TitanFabricItems.ESSENCE.getDefaultStack()), RecipeCategory.COMBAT)
                .criterion(
                        FabricRecipeProvider.hasItem(Items.DIAMOND),
                        FabricRecipeProvider.conditionsFromItem(Items.DIAMOND)
                )
                .offerTo(exporter, TitanFabric.getId("diamond_greatsword"));
    }
}
