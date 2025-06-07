package net.shirojr.titanfabric.init;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.data.BackPackContent;
import net.shirojr.titanfabric.data.PotionBundleContent;
import net.shirojr.titanfabric.item.custom.*;
import net.shirojr.titanfabric.item.custom.armor.CitrinArmorItem;
import net.shirojr.titanfabric.item.custom.armor.EmberArmorItem;
import net.shirojr.titanfabric.item.custom.armor.LegendArmorItem;
import net.shirojr.titanfabric.item.custom.bow.LegendBowItem;
import net.shirojr.titanfabric.item.custom.bow.MultiBowItem;
import net.shirojr.titanfabric.item.custom.bow.TitanCrossBowItem;
import net.shirojr.titanfabric.item.custom.material.TitanFabricToolMaterials;
import net.shirojr.titanfabric.item.custom.misc.BackPackItem;
import net.shirojr.titanfabric.item.custom.misc.CitrinStarItem;
import net.shirojr.titanfabric.item.custom.misc.FlintAndEmberItem;
import net.shirojr.titanfabric.item.custom.misc.PotionBundleItem;
import net.shirojr.titanfabric.item.custom.sword.CitrinSwordItem;
import net.shirojr.titanfabric.item.custom.sword.DiamondSwordItem;
import net.shirojr.titanfabric.item.custom.sword.EmberSwordItem;
import net.shirojr.titanfabric.item.custom.sword.LegendSwordItem;
import net.shirojr.titanfabric.util.SwordType;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public interface TitanFabricItems {
    List<Item> ALL_ITEMS = new ArrayList<>();

    CitrinStarItem CITRIN_STAR = registerItem("citrin_star",
            new CitrinStarItem(new Item.Settings().maxCount(4)));

    Item CITRIN_SHARD = registerItem("citrin_shard",
            new Item(new Item.Settings().maxCount(64)));
    Item EMBER_INGOT = registerItem("ember_ingot",
            new Item(new Item.Settings().maxCount(64)));
    Item LEGEND_INGOT = registerItem("legend_ingot",
            new Item(new Item.Settings().maxCount(64)));
    Item LEGEND_POWDER = registerItem("legend_powder",
            new Item(new Item.Settings().maxCount(64)));
    Item EMBER_SHARD = registerItem("ember_shard",
            new Item(new Item.Settings().maxCount(64)));

    //region armor
    CitrinArmorItem CITRIN_HELMET = registerItem("citrin_helmet",
            new CitrinArmorItem(ArmorItem.Type.HELMET, new Item.Settings()));
    CitrinArmorItem CITRIN_CHESTPLATE = registerItem("citrin_chestplate",
            new CitrinArmorItem(ArmorItem.Type.CHESTPLATE, new Item.Settings()));
    CitrinArmorItem CITRIN_LEGGINGS = registerItem("citrin_leggings",
            new CitrinArmorItem(ArmorItem.Type.LEGGINGS, new Item.Settings()));
    CitrinArmorItem CITRIN_BOOTS = registerItem("citrin_boots",
            new CitrinArmorItem(ArmorItem.Type.BOOTS, new Item.Settings()));

    EmberArmorItem EMBER_HELMET = registerItem("ember_helmet",
            new EmberArmorItem(ArmorItem.Type.HELMET, new Item.Settings()));
    EmberArmorItem EMBER_CHESTPLATE = registerItem("ember_chestplate",
            new EmberArmorItem(ArmorItem.Type.CHESTPLATE, new Item.Settings()));
    EmberArmorItem EMBER_LEGGINGS = registerItem("ember_leggings",
            new EmberArmorItem(ArmorItem.Type.LEGGINGS, new Item.Settings()));
    EmberArmorItem EMBER_BOOTS = registerItem("ember_boots",
            new EmberArmorItem(ArmorItem.Type.BOOTS, new Item.Settings()));

    LegendArmorItem LEGEND_HELMET = registerItem("legend_helmet",
            new LegendArmorItem(ArmorItem.Type.HELMET, new Item.Settings(), 0.0f));
    LegendArmorItem LEGEND_CHESTPLATE = registerItem("legend_chestplate",
            new LegendArmorItem(ArmorItem.Type.CHESTPLATE, new Item.Settings(), 4.0f));
    LegendArmorItem LEGEND_LEGGINGS = registerItem("legend_leggings",
            new LegendArmorItem(ArmorItem.Type.LEGGINGS, new Item.Settings(), 3.0f));
    LegendArmorItem LEGEND_BOOTS = registerItem("legend_boots",
            new LegendArmorItem(ArmorItem.Type.BOOTS, new Item.Settings(), 1.0f));
    //endregion

    CitrinSwordItem CITRIN_SWORD = registerItem("citrin_sword",
            new CitrinSwordItem(true, TitanFabricToolMaterials.CITRIN, removeBaseDamage(6), -2.4f, SwordType.DEFAULT, new Item.Settings()));
    CitrinSwordItem CITRIN_GREATSWORD = registerItem("citrin_greatsword",
            new CitrinSwordItem(true, TitanFabricToolMaterials.CITRIN_GREAT, removeBaseDamage(7), -2.4f, SwordType.GREAT_SWORD, new Item.Settings()));
    EmberSwordItem EMBER_SWORD = registerItem("ember_sword",
            new EmberSwordItem(true, TitanFabricToolMaterials.EMBER, removeBaseDamage(7), -2.4f, SwordType.DEFAULT, new Item.Settings()));
    EmberSwordItem EMBER_GREATSWORD = registerItem("ember_greatsword",
            new EmberSwordItem(true, TitanFabricToolMaterials.EMBER_GREAT, removeBaseDamage(8), -2.4f, SwordType.GREAT_SWORD, new Item.Settings()));
    LegendSwordItem LEGEND_SWORD = registerItem("legend_sword",
            new LegendSwordItem(true, TitanFabricToolMaterials.LEGEND, removeBaseDamage(8), -2.4f, SwordType.DEFAULT, new Item.Settings()));
    LegendSwordItem LEGEND_GREATSWORD = registerItem("legend_greatsword",
            new LegendSwordItem(true, TitanFabricToolMaterials.LEGEND_GREAT, removeBaseDamage(9), -2.4f, SwordType.GREAT_SWORD, new Item.Settings()));
    DiamondSwordItem DIAMOND_SWORD = registerItem("diamond_sword",
            new DiamondSwordItem(true, TitanFabricToolMaterials.DIAMOND, 6, -2.4f, SwordType.DEFAULT, null, new Item.Settings(), false));
    DiamondSwordItem DIAMOND_GREATSWORD = registerItem("diamond_greatsword",
            new DiamondSwordItem(true, TitanFabricToolMaterials.DIAMOND_GREAT, removeBaseDamage(8), -2.4f, SwordType.GREAT_SWORD, null, new Item.Settings(), true));
    SwordItem NETHERITE_SWORD = new SwordItem(TitanFabricToolMaterials.NETHERITE,
            new Item.Settings().fireproof().attributeModifiers(SwordItem.createAttributeModifiers(TitanFabricToolMaterials.NETHERITE, removeBaseDamage(9), -2.4f)));  // registered in ItemsMixin class
    TitanFabricSwordItem NETHERITE_GREATSWORD = registerItem("netherite_greatsword",
            new TitanFabricSwordItem(false, TitanFabricToolMaterials.NETHERITE_GREAT, removeBaseDamage(10), -2.4f, SwordType.GREAT_SWORD, null, new Item.Settings().fireproof()));

    TitanCrossBowItem TITAN_CROSSBOW = registerItem("legend_crossbow", new TitanCrossBowItem(new Item.Settings().maxCount(1).maxDamage(-1).component(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.DEFAULT)));
    LegendBowItem LEGEND_BOW = registerItem("legend_bow", new LegendBowItem(new Item.Settings().maxCount(1).maxDamage(-1)));
    MultiBowItem MULTI_BOW_1 = registerItem("multi_bow_1", new MultiBowItem(
            new Item.Settings().maxCount(1).maxDamage(500), 1, 10));
    MultiBowItem MULTI_BOW_2 = registerItem("multi_bow_2", new MultiBowItem(
            new Item.Settings().maxCount(1).maxDamage(1000), 2, 20));
    MultiBowItem MULTI_BOW_3 = registerItem("multi_bow_3", new MultiBowItem(
            new Item.Settings().maxCount(1).maxDamage(1500), 3, 30));


    TitanFabricArrowItem ARROW = registerItem("effect_arrow",
            new TitanFabricArrowItem(new Item.Settings().maxCount(32)));

    TitanFabricShieldItem DIAMOND_SHIELD = registerItem("diamond_shield",
            new TitanFabricShieldItem(1685, 60, 14, Items.DIAMOND));
    TitanFabricShieldItem LEGEND_SHIELD = registerItem("legend_shield",
            new TitanFabricShieldItem(-1, 40, 24, TitanFabricItems.LEGEND_INGOT));

    TitanFabricParachuteItem PARACHUTE = registerItem("parachute",
            new TitanFabricParachuteItem(new Item.Settings().maxCount(1).maxDamage(250).component(TitanFabricDataComponents.ACTIVATED, false)));

    FlintAndEmberItem FLINT_AND_EMBER = registerItem("flint_and_ember",
            new FlintAndEmberItem(new Item.Settings().maxCount(1).maxDamage(64)));

    TitanFabricEssenceItem ESSENCE = registerItem("essence", new TitanFabricEssenceItem(
            new Item.Settings().maxCount(64)));

    Item SWORD_HANDLE = registerItem("sword_handle",
            new Item(new Item.Settings().maxCount(64)));
    Item DIAMOND_APPLE = registerItem("diamond_apple",
            new Item(new Item.Settings().maxCount(16)
                    .food((new FoodComponent.Builder()).nutrition(4).saturationModifier(1.2f).alwaysEdible()
                            .statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 2), 1.0F)
                            .statusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 1200, 1), 1.0F)
                            .build())));

    PotionBundleItem POTION_BUNDLE = registerItem("potion_bundle", new PotionBundleItem(new Item.Settings().maxCount(1)
            .component(TitanFabricDataComponents.POTION_BUNDLE_CONTENT, PotionBundleContent.DEFAULT)));

    BackPackItem BACKPACK_SMALL = registerItem("backpack_small", new BackPackItem(new Item.Settings().maxCount(1)
            .component(TitanFabricDataComponents.BACKPACK_CONTENT, BackPackContent.getDefault(BackPackItem.Type.SMALL))));

    BackPackItem BACKPACK_MEDIUM = registerItem("backpack_medium", new BackPackItem(new Item.Settings().maxCount(1)
            .component(TitanFabricDataComponents.BACKPACK_CONTENT, BackPackContent.getDefault(BackPackItem.Type.MEDIUM))));

    BackPackItem BACKPACK_BIG = registerItem("backpack_big", new BackPackItem(new Item.Settings().maxCount(1)
            .component(TitanFabricDataComponents.BACKPACK_CONTENT, BackPackContent.getDefault(BackPackItem.Type.BIG))));


    private static <T extends Item> T registerItem(String name, T item) {
        T registeredEntry = Registry.register(Registries.ITEM, TitanFabric.getId(name), item);
        ALL_ITEMS.add(registeredEntry);
        return registeredEntry;
    }

    static void initialize() {
        // static initialisation
    }

    private static int removeBaseDamage(int damage) {
        return damage - 1;
    }
}
