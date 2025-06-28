package net.shirojr.titanfabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.init.TitanFabricItems;
import net.shirojr.titanfabric.recipe.builder.WeaponEffectRecipeJsonBuilder;

import java.util.concurrent.CompletableFuture;

public class TitanFabricRecipeProvider extends FabricRecipeProvider {
    public TitanFabricRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        for (SwordItem effectSword : TitanFabricItems.EFFECT_SWORDS) {
            Identifier id = Registries.ITEM.getId(effectSword);
            essenceUpgrade(id.getPath() + "_upgrade", effectSword, exporter);
        }
    }

    private static void essenceUpgrade(String name, Item base, RecipeExporter exporter) {
        WeaponEffectRecipeJsonBuilder.create(
                        Ingredient.ofItems(base),
                        Ingredient.ofItems(TitanFabricItems.ESSENCE),
                        RecipeCategory.COMBAT)
                .criterion(
                        FabricRecipeProvider.hasItem(base),
                        FabricRecipeProvider.conditionsFromItem(base)
                )
                .offerTo(exporter, TitanFabric.getId(name));
    }
}
