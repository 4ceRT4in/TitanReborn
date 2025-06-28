package net.shirojr.titanfabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.init.TitanFabricBlocks;
import net.shirojr.titanfabric.init.TitanFabricItems;
import net.shirojr.titanfabric.item.custom.armor.CitrinArmorItem;
import net.shirojr.titanfabric.item.custom.armor.EmberArmorItem;
import net.shirojr.titanfabric.item.custom.armor.LegendArmorItem;
import net.shirojr.titanfabric.item.custom.misc.BackPackItem;
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
            offerEssenceUpgrade(id.getPath() + "_upgrade", effectSword, exporter);
        }
        offerBackpack(TitanFabricItems.BACKPACK_BIG, Items.DIAMOND, exporter);
        offerBackpack(TitanFabricItems.BACKPACK_MEDIUM, Items.GOLD_INGOT, exporter);
        offerBackpack(TitanFabricItems.BACKPACK_SMALL, Items.IRON_INGOT, exporter);
        offerReversibleCompactingRecipes(exporter, RecipeCategory.MISC, TitanFabricItems.CITRIN_SHARD, RecipeCategory.BUILDING_BLOCKS, TitanFabricBlocks.CITRIN_BLOCK);

        for (CitrinArmorItem entry : TitanFabricItems.CITRIN_ARMOR_ITEMS) {
            offerCitrinArmor(exporter, entry);
        }
        for (EmberArmorItem entry : TitanFabricItems.EMBER_ARMOR_ITEMS) {
            offerSpecialArmor(exporter, entry, TitanFabricItems.EMBER_INGOT, Items.FIRE_CHARGE);
        }
        for (LegendArmorItem entry : TitanFabricItems.LEGEND_ARMOR_ITEMS) {
            offerSpecialArmor(exporter, entry, TitanFabricItems.LEGEND_INGOT, TitanFabricBlocks.LEGEND_CRYSTAL.asItem());
        }
    }

    private static void offerEssenceUpgrade(String name, Item base, RecipeExporter exporter) {
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

    private static void offerBackpack(BackPackItem item, Item centerPiece, RecipeExporter exporter) {
        String name = Registries.ITEM.getId(item).getPath();
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, item)
                .pattern("sws").pattern("wcw").pattern("lwl")
                .input('s', Items.STRING).input('w', ItemTags.WOOL).input('c', centerPiece).input('l', Items.LEATHER)
                .criterion(FabricRecipeProvider.hasItem(centerPiece), FabricRecipeProvider.conditionsFromItem(centerPiece))
                .offerTo(exporter, TitanFabric.getId(name));
    }

    private static void offerCitrinArmor(RecipeExporter exporter, ArmorItem output) {
        String name = Registries.ITEM.getId(output).getPath();
        ShapedRecipeJsonBuilder builder = ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, output);
        builder = builder.input('X', TitanFabricItems.CITRIN_SHARD);
        switch (output.getType()) {
            case BOOTS -> builder = builder.pattern("X X").pattern("X X");
            case CHESTPLATE -> builder = builder.pattern("X X").pattern("XXX").pattern("XXX");
            case HELMET -> builder = builder.pattern("XXX").pattern("X X");
            case LEGGINGS -> builder = builder.pattern("XXX").pattern("X X").pattern("X X");
        }
        builder.criterion(
                FabricRecipeProvider.hasItem(TitanFabricItems.CITRIN_SHARD),
                FabricRecipeProvider.conditionsFromItem(TitanFabricItems.CITRIN_SHARD)
        ).offerTo(exporter, name);
    }

    private static void offerSpecialArmor(RecipeExporter exporter, ArmorItem output, Item baseMaterial, Item extraItem) {
        String name = Registries.ITEM.getId(output).getPath();
        ShapedRecipeJsonBuilder builder = ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, output);
        builder = builder.input('X', baseMaterial).input('E', extraItem);
        switch (output.getType()) {
            case BOOTS -> builder = builder.pattern("X X").pattern("XEX");
            case CHESTPLATE -> builder = builder.pattern("X X").pattern("XEX").pattern("XXX");
            case HELMET -> builder = builder.pattern("XXX").pattern("XEX");
            case LEGGINGS -> builder = builder.pattern("XXX").pattern("XEX").pattern("X X");
        }
        builder.criterion(
                FabricRecipeProvider.hasItem(baseMaterial),
                FabricRecipeProvider.conditionsFromItem(baseMaterial)
        ).criterion(
                FabricRecipeProvider.hasItem(extraItem),
                FabricRecipeProvider.conditionsFromItem(extraItem)
        ).offerTo(exporter, name);
    }
}
