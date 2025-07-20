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
import net.shirojr.titanfabric.recipe.builder.EffectRecipeJsonBuilder;
import net.shirojr.titanfabric.recipe.builder.EffectUpgradeRecipeJsonBuilder;
import net.shirojr.titanfabric.recipe.builder.MultiBowUpgradeRecipeJsonBuilder;
import net.shirojr.titanfabric.recipe.custom.EffectRecipe;
import net.shirojr.titanfabric.util.IngredientModule;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TitanFabricRecipeProvider extends FabricRecipeProvider {
    public TitanFabricRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        offerBackpack(TitanFabricItems.BACKPACK_BIG, Items.DIAMOND, exporter);
        offerBackpack(TitanFabricItems.BACKPACK_MEDIUM, Items.GOLD_INGOT, exporter);
        offerBackpack(TitanFabricItems.BACKPACK_SMALL, Items.IRON_INGOT, exporter);

        offerReversibleCompactingRecipes(exporter,
                RecipeCategory.MISC, TitanFabricItems.CITRIN_SHARD,
                RecipeCategory.BUILDING_BLOCKS, TitanFabricBlocks.CITRIN_BLOCK
        );
        offerReversibleCompactingRecipes(exporter,
                RecipeCategory.MISC, TitanFabricItems.LEGEND_INGOT,
                RecipeCategory.BUILDING_BLOCKS, TitanFabricBlocks.LEGEND_BLOCK
        );
        offerReversibleCompactingRecipes(exporter,
                RecipeCategory.MISC, TitanFabricItems.EMBER_INGOT,
                RecipeCategory.BUILDING_BLOCKS, TitanFabricBlocks.EMBER_BLOCK
        );

        offerHeatTreatment(exporter, TitanFabricBlocks.CITRIN_ORE.asItem(), TitanFabricItems.CITRIN_SHARD);
        offerHeatTreatment(exporter, TitanFabricItems.EMBER_SHARD, TitanFabricItems.EMBER_INGOT);
        offerHeatTreatment(exporter, TitanFabricBlocks.EMBER_ORE.asItem(), TitanFabricItems.EMBER_SHARD);
        offerHeatTreatment(exporter, TitanFabricBlocks.DEEPSTALE_LEGEND_ORE.asItem(), TitanFabricBlocks.LEGEND_CRYSTAL.asItem());
        offerHeatTreatment(exporter, TitanFabricItems.LEGEND_POWDER, TitanFabricItems.LEGEND_INGOT);

        for (CitrinArmorItem entry : TitanFabricItems.CITRIN_ARMOR_ITEMS) {
            offerCitrinArmor(exporter, entry);
        }
        for (EmberArmorItem entry : TitanFabricItems.EMBER_ARMOR_ITEMS) {
            offerSpecialArmor(exporter, entry, TitanFabricItems.EMBER_INGOT, Items.FIRE_CHARGE);
        }
        for (LegendArmorItem entry : TitanFabricItems.LEGEND_ARMOR_ITEMS) {
            offerSpecialArmor(exporter, entry, TitanFabricItems.LEGEND_INGOT, TitanFabricBlocks.LEGEND_CRYSTAL.asItem());
        }
        for (SwordItem effectSword : TitanFabricItems.EFFECT_SWORDS) {
            Identifier id = Registries.ITEM.getId(effectSword);
            offerEssenceUpgrade(id.getPath() + "_upgrade", effectSword, exporter);
        }

        offerSwords(exporter, TitanFabricItems.CITRIN_SWORD, TitanFabricItems.CITRIN_GREATSWORD, TitanFabricItems.CITRIN_SHARD);
        offerSwords(exporter, TitanFabricItems.EMBER_SWORD, TitanFabricItems.EMBER_GREATSWORD, TitanFabricItems.EMBER_INGOT);
        offerSwords(exporter, TitanFabricItems.LEGEND_SWORD, TitanFabricItems.LEGEND_GREATSWORD, TitanFabricItems.LEGEND_INGOT);
        offerSwords(exporter, null, TitanFabricItems.DIAMOND_GREATSWORD, Items.DIAMOND);
        offerNetheriteUpgradeRecipe(exporter, TitanFabricItems.EMBER_GREATSWORD, RecipeCategory.COMBAT, TitanFabricItems.NETHERITE_GREATSWORD);

        offerEffectItems(exporter, Items.BLAZE_POWDER, List.of(4), Items.POTION, List.of(1, 3, 5, 7), TitanFabricItems.ESSENCE, 1);
        offerEffectItems(exporter, Items.ARROW, List.of(1, 3, 5, 7), Items.POTION, List.of(4), TitanFabricItems.EFFECT_ARROW, 2);

        offerMultiBowUpgrade(exporter, TitanFabricItems.MULTI_BOW_1, TitanFabricItems.MULTI_BOW_2);
        offerMultiBowUpgrade(exporter, TitanFabricItems.MULTI_BOW_2, TitanFabricItems.MULTI_BOW_3);
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, TitanFabricItems.MULTI_BOW_1)
                .input('s', Items.STRING).input('e', TitanFabricItems.EMBER_INGOT).input('#', Items.STICK)
                .pattern(" #s")
                .pattern("e s")
                .pattern(" #s")
                .criterion(hasItem(TitanFabricItems.EMBER_INGOT), conditionsFromItem(TitanFabricItems.EMBER_INGOT))
                .offerTo(exporter);

        offerArmorPlating(exporter, TitanFabricItems.DIAMOND_ARMOR_PLATING, Items.DIAMOND);
        offerArmorPlating(exporter, TitanFabricItems.NETHERITE_ARMOR_PLATING, Items.NETHERITE_INGOT);
        offerArmorPlating(exporter, TitanFabricItems.CITRIN_ARMOR_PLATING, TitanFabricItems.CITRIN_SHARD);
        offerArmorPlating(exporter, TitanFabricItems.EMBER_ARMOR_PLATING, TitanFabricItems.EMBER_INGOT);
        offerArmorPlating(exporter, TitanFabricItems.LEGEND_ARMOR_PLATING, TitanFabricItems.LEGEND_INGOT);
    }

    private static void offerEssenceUpgrade(String name, Item base, RecipeExporter exporter) {
        EffectUpgradeRecipeJsonBuilder.create(Ingredient.ofItems(base), Ingredient.ofItems(TitanFabricItems.ESSENCE), RecipeCategory.COMBAT)
                .criterion(hasItem(base), conditionsFromItem(base)).offerTo(exporter, TitanFabric.getId(name));
    }

    private static void offerBackpack(BackPackItem item, Item centerPiece, RecipeExporter exporter) {
        String name = Registries.ITEM.getId(item).getPath();
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, item)
                .pattern("sws").pattern("wcw").pattern("lwl")
                .input('s', Items.STRING).input('w', ItemTags.WOOL).input('c', centerPiece).input('l', Items.LEATHER)
                .criterion(hasItem(centerPiece), conditionsFromItem(centerPiece))
                .offerTo(exporter, TitanFabric.getId(name));
    }

    private static void offerCitrinArmor(RecipeExporter exporter, ArmorItem output) {
        String name = Registries.ITEM.getId(output).getPath();
        ShapedRecipeJsonBuilder builder = ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, output);
        builder = builder.input('X', TitanFabricItems.CITRIN_SHARD);
        switch (output.getType()) {
            case BOOTS -> builder = builder
                    .pattern("X X")
                    .pattern("X X");
            case CHESTPLATE -> builder = builder
                    .pattern("X X")
                    .pattern("XXX")
                    .pattern("XXX");
            case HELMET -> builder = builder
                    .pattern("XXX")
                    .pattern("X X");
            case LEGGINGS -> builder = builder
                    .pattern("XXX")
                    .pattern("X X")
                    .pattern("X X");
        }
        builder.criterion(
                hasItem(TitanFabricItems.CITRIN_SHARD),
                conditionsFromItem(TitanFabricItems.CITRIN_SHARD)
        ).offerTo(exporter, name);
    }

    private static void offerSpecialArmor(RecipeExporter exporter, ArmorItem output, Item baseMaterial, Item extraItem) {
        String name = Registries.ITEM.getId(output).getPath();
        ShapedRecipeJsonBuilder builder = ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, output);
        builder = builder.input('X', baseMaterial).input('E', extraItem);
        switch (output.getType()) {
            case BOOTS -> builder = builder
                    .pattern("X X")
                    .pattern("XEX");
            case CHESTPLATE -> builder = builder
                    .pattern("X X")
                    .pattern("XEX")
                    .pattern("XXX");
            case HELMET -> builder = builder
                    .pattern("XXX")
                    .pattern("XEX");
            case LEGGINGS -> builder = builder
                    .pattern("XXX")
                    .pattern("XEX")
                    .pattern("X X");
        }
        builder.criterion(
                hasItem(baseMaterial),
                conditionsFromItem(baseMaterial)
        ).criterion(
                hasItem(extraItem),
                conditionsFromItem(extraItem)
        ).offerTo(exporter, name);
    }

    private static void offerHeatTreatment(RecipeExporter exporter, Item input, Item output) {
        String name = Registries.ITEM.getId(output).getPath();
        offerBlasting(exporter, List.of(input), RecipeCategory.MISC, output, 0.1f, 100, name);
        offerSmelting(exporter, List.of(input), RecipeCategory.MISC, output, 0.1f, 200, name);
    }

    private static void offerSwords(RecipeExporter exporter, @Nullable Item outputSword, @Nullable Item outputGreatsword, Item material) {
        if (outputGreatsword != null) {
            ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, outputGreatsword)
                    .input('X', Ingredient.ofItems(material))
                    .input('#', Ingredient.ofItems(TitanFabricItems.SWORD_HANDLE))
                    .pattern(" XX")
                    .pattern("XXX")
                    .pattern("#X ")
                    .criterion(hasItem(material), conditionsFromItem(material))
                    .offerTo(exporter, Registries.ITEM.getId(outputGreatsword).getPath());
        }
        if (outputSword != null) {
            ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, outputSword)
                    .input('#', TitanFabricItems.SWORD_HANDLE)
                    .input('X', material)
                    .pattern("X")
                    .pattern("X")
                    .pattern("#")
                    .criterion(hasItem(material), conditionsFromItem(material))
                    .offerTo(exporter, Registries.ITEM.getId(outputSword).getPath());
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static void offerEffectItems(RecipeExporter exporter, Item base, List<Integer> baseSlots, Item modifier, List<Integer> modifierSlots, Item result, int resultCount) {
        IngredientModule baseModule = new IngredientModule(Ingredient.ofItems(base), baseSlots.stream().mapToInt(Integer::intValue).toArray());
        IngredientModule modifierModule = new IngredientModule(Ingredient.ofItems(modifier), modifierSlots.stream().mapToInt(Integer::intValue).toArray());
        String name = Registries.ITEM.getId(result).getPath();

        EffectRecipeJsonBuilder.create(baseModule, modifierModule, new EffectRecipe.Result(result, resultCount))
                .criterion(hasItem(Items.BLAZE_POWDER), conditionsFromItem(Items.BLAZE_POWDER))
                .criterion(hasItem(Items.POTION), conditionsFromItem(Items.POTION))
                .offerTo(exporter, TitanFabric.getId(name));
    }

    private static void offerMultiBowUpgrade(RecipeExporter exporter, Item base, Item result) {
        String name = Registries.ITEM.getId(result).getPath();
        MultiBowUpgradeRecipeJsonBuilder.create(Ingredient.ofItems(base), result.getDefaultStack())
                .criterion(hasItem(base), conditionsFromItem(base))
                .offerTo(exporter, TitanFabric.getId(name + "_upgrade"));
    }

    private static void offerArmorPlating(RecipeExporter exporter, Item output, Item ingot) {
        String name = Registries.ITEM.getId(output).getPath();

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, output)
                .input('i', ingot)
                .pattern(" i ").pattern("iii").pattern(" i ")
                .criterion(hasItem(ingot), conditionsFromItem(ingot))
                .offerTo(exporter, TitanFabric.getId(name));

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, output, 2)
                .input('i', ingot).input('o', output)
                .pattern(" i ").pattern("ioi").pattern(" i ")
                .criterion(hasItem(ingot), conditionsFromItem(ingot))
                .offerTo(exporter, TitanFabric.getId(name + "_increased"));
    }
}
