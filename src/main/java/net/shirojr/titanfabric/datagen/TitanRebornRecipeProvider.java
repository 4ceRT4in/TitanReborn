package net.shirojr.titanfabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.shirojr.titanfabric.init.TitanFabricItems;
import net.shirojr.titanfabric.recipe.builder.WeaponEffectRecipeJsonBuilder;

import java.util.concurrent.CompletableFuture;

public class TitanRebornRecipeProvider extends FabricRecipeProvider {
    public TitanRebornRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        WeaponEffectRecipeJsonBuilder.create(
                Ingredient.ofItems(TitanFabricItems.CITRIN_SWORD),
                Ingredient.ofStacks(TitanFabricItems.ESSENCE.getDefaultStack()),
                RecipeCategory.COMBAT
        );
    }
}
