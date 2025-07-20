package net.shirojr.titanfabric.init;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.UnbreakableComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.data.BackPackContent;
import net.shirojr.titanfabric.data.PotionBundleContent;
import net.shirojr.titanfabric.item.custom.*;
import net.shirojr.titanfabric.item.custom.armor.ArmorPlatingItem;
import net.shirojr.titanfabric.item.custom.armor.CitrinArmorItem;
import net.shirojr.titanfabric.item.custom.armor.EmberArmorItem;
import net.shirojr.titanfabric.item.custom.armor.LegendArmorItem;
import net.shirojr.titanfabric.item.custom.bow.LegendBowItem;
import net.shirojr.titanfabric.item.custom.bow.MultiBowItem;
import net.shirojr.titanfabric.item.custom.bow.TitanCrossBowItem;
import net.shirojr.titanfabric.item.custom.material.TitanFabricToolMaterials;
import net.shirojr.titanfabric.item.custom.misc.*;
import net.shirojr.titanfabric.item.custom.sword.CitrinSwordItem;
import net.shirojr.titanfabric.item.custom.sword.DiamondSwordItem;
import net.shirojr.titanfabric.item.custom.sword.EmberSwordItem;
import net.shirojr.titanfabric.item.custom.sword.LegendSwordItem;
import net.shirojr.titanfabric.util.SwordType;
import net.shirojr.titanfabric.util.effects.ArmorPlateType;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public interface TitanFabricItems {
    List<Item> ALL_ITEMS = new ArrayList<>();

    List<SwordItem> EFFECT_SWORDS = new ArrayList<>();
    List<CitrinArmorItem> CITRIN_ARMOR_ITEMS = new ArrayList<>();
    List<EmberArmorItem> EMBER_ARMOR_ITEMS = new ArrayList<>();
    List<LegendArmorItem> LEGEND_ARMOR_ITEMS = new ArrayList<>();

    Item CITRIN_SHARD = register("citrin_shard",
            new Item(new Item.Settings().maxCount(64)));
    Item EMBER_INGOT = register("ember_ingot",
            new Item(new Item.Settings().fireproof().maxCount(64)));
    Item LEGEND_INGOT = register("legend_ingot",
            new Item(new Item.Settings().maxCount(64)));
    Item LEGEND_POWDER = register("legend_powder",
            new Item(new Item.Settings().maxCount(64)));
    Item EMBER_SHARD = register("ember_shard",
            new Item(new Item.Settings().fireproof().maxCount(64)));

    //region armor
    CitrinArmorItem CITRIN_HELMET = registerArmor("citrin_helmet",
            new CitrinArmorItem(ArmorItem.Type.HELMET, new Item.Settings()), CITRIN_ARMOR_ITEMS);
    CitrinArmorItem CITRIN_CHESTPLATE = registerArmor("citrin_chestplate",
            new CitrinArmorItem(ArmorItem.Type.CHESTPLATE, new Item.Settings()), CITRIN_ARMOR_ITEMS);
    CitrinArmorItem CITRIN_LEGGINGS = registerArmor("citrin_leggings",
            new CitrinArmorItem(ArmorItem.Type.LEGGINGS, new Item.Settings()), CITRIN_ARMOR_ITEMS);
    CitrinArmorItem CITRIN_BOOTS = registerArmor("citrin_boots",
            new CitrinArmorItem(ArmorItem.Type.BOOTS, new Item.Settings()), CITRIN_ARMOR_ITEMS);

    EmberArmorItem EMBER_HELMET = registerArmor("ember_helmet",
            new EmberArmorItem(ArmorItem.Type.HELMET, new Item.Settings().fireproof()), EMBER_ARMOR_ITEMS);
    EmberArmorItem EMBER_CHESTPLATE = registerArmor("ember_chestplate",
            new EmberArmorItem(ArmorItem.Type.CHESTPLATE, new Item.Settings().fireproof()), EMBER_ARMOR_ITEMS);
    EmberArmorItem EMBER_LEGGINGS = registerArmor("ember_leggings",
            new EmberArmorItem(ArmorItem.Type.LEGGINGS, new Item.Settings().fireproof()), EMBER_ARMOR_ITEMS);
    EmberArmorItem EMBER_BOOTS = registerArmor("ember_boots",
            new EmberArmorItem(ArmorItem.Type.BOOTS, new Item.Settings().fireproof()), EMBER_ARMOR_ITEMS);

    LegendArmorItem LEGEND_HELMET = registerArmor("legend_helmet",
            new LegendArmorItem(ArmorItem.Type.HELMET, new Item.Settings(), 2.0f), LEGEND_ARMOR_ITEMS);
    LegendArmorItem LEGEND_CHESTPLATE = registerArmor("legend_chestplate",
            new LegendArmorItem(ArmorItem.Type.CHESTPLATE, new Item.Settings(), 4.0f), LEGEND_ARMOR_ITEMS);
    LegendArmorItem LEGEND_LEGGINGS = registerArmor("legend_leggings",
            new LegendArmorItem(ArmorItem.Type.LEGGINGS, new Item.Settings(), 3.0f), LEGEND_ARMOR_ITEMS);
    LegendArmorItem LEGEND_BOOTS = registerArmor("legend_boots",
            new LegendArmorItem(ArmorItem.Type.BOOTS, new Item.Settings(), 1.0f), LEGEND_ARMOR_ITEMS);
    //endregion

    CitrinSwordItem CITRIN_SWORD = registerEffectSword("citrin_sword",
            new CitrinSwordItem(true, TitanFabricToolMaterials.CITRIN, removeBaseDamage(6), -2.4f, SwordType.DEFAULT, new Item.Settings()));
    CitrinSwordItem CITRIN_GREATSWORD = registerEffectSword("citrin_greatsword",
            new CitrinSwordItem(true, TitanFabricToolMaterials.CITRIN_GREAT, removeBaseDamage(7), -3f, SwordType.GREAT_SWORD, new Item.Settings()));
    EmberSwordItem EMBER_SWORD = registerEffectSword("ember_sword",
            new EmberSwordItem(true, TitanFabricToolMaterials.EMBER, removeBaseDamage(7), -2.4f, SwordType.DEFAULT, new Item.Settings().fireproof()));
    EmberSwordItem EMBER_GREATSWORD = registerEffectSword("ember_greatsword",
            new EmberSwordItem(true, TitanFabricToolMaterials.EMBER_GREAT, removeBaseDamage(8), -3f, SwordType.GREAT_SWORD, new Item.Settings().fireproof()));
    LegendSwordItem LEGEND_SWORD = registerEffectSword("legend_sword",
            new LegendSwordItem(true, TitanFabricToolMaterials.LEGEND, removeBaseDamage(8), -2.4f, SwordType.DEFAULT,
                    new Item.Settings().component(DataComponentTypes.UNBREAKABLE, new UnbreakableComponent(true))));
    LegendSwordItem LEGEND_GREATSWORD = registerEffectSword("legend_greatsword",
            new LegendSwordItem(true, TitanFabricToolMaterials.LEGEND_GREAT, removeBaseDamage(9), -3f, SwordType.GREAT_SWORD,
                    new Item.Settings().component(DataComponentTypes.UNBREAKABLE, new UnbreakableComponent(true))));
    DiamondSwordItem DIAMOND_SWORD = registerEffectSword("diamond_sword",
            new DiamondSwordItem(true, TitanFabricToolMaterials.DIAMOND, 6, -2.4f, SwordType.DEFAULT, null, new Item.Settings(), false));
    DiamondSwordItem DIAMOND_GREATSWORD = registerEffectSword("diamond_greatsword",
            new DiamondSwordItem(true, TitanFabricToolMaterials.DIAMOND_GREAT, removeBaseDamage(8), -3f, SwordType.GREAT_SWORD, null, new Item.Settings(), true));
    SwordItem NETHERITE_SWORD = new SwordItem(TitanFabricToolMaterials.NETHERITE,
            new Item.Settings().fireproof().attributeModifiers(SwordItem.createAttributeModifiers(TitanFabricToolMaterials.NETHERITE, removeBaseDamage(9), -2.4f)));  // registered in ItemsMixin class
    TitanFabricSwordItem NETHERITE_GREATSWORD = register("netherite_greatsword",
            new TitanFabricSwordItem(false, TitanFabricToolMaterials.NETHERITE_GREAT, removeBaseDamage(10), -2.4f, SwordType.GREAT_SWORD, null, new Item.Settings().fireproof()));

    TitanCrossBowItem TITAN_CROSSBOW = register("legend_crossbow", new TitanCrossBowItem(new Item.Settings().maxCount(1)
            .component(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.DEFAULT)
            .component(DataComponentTypes.UNBREAKABLE, new UnbreakableComponent(true))
    ));
    LegendBowItem LEGEND_BOW = register("legend_bow", new LegendBowItem(new Item.Settings().maxCount(1)
            .component(DataComponentTypes.UNBREAKABLE, new UnbreakableComponent(true)))
    );
    MultiBowItem MULTI_BOW_1 = register("multi_bow_1", new MultiBowItem(
            new Item.Settings().maxCount(1).maxDamage(500), 1, 10));
    MultiBowItem MULTI_BOW_2 = register("multi_bow_2", new MultiBowItem(
            new Item.Settings().maxCount(1).maxDamage(1000), 2, 20));
    MultiBowItem MULTI_BOW_3 = register("multi_bow_3", new MultiBowItem(
            new Item.Settings().maxCount(1).maxDamage(1500), 3, 30));


    TitanFabricArrowItem EFFECT_ARROW = register("effect_arrow",
            new TitanFabricArrowItem(new Item.Settings().maxCount(32)));

    TitanFabricShieldItem DIAMOND_SHIELD = register("diamond_shield",
            new TitanFabricShieldItem(new Item.Settings().maxCount(1).maxDamage(1685), Items.DIAMOND));
    TitanFabricShieldItem NETHERITE_SHIELD = register("netherite_shield",
            new TitanFabricShieldItem(new Item.Settings().maxCount(1).maxDamage(3370).fireproof(), Items.NETHERITE_INGOT));
    TitanFabricShieldItem LEGEND_SHIELD = register("legend_shield",
            new TitanFabricShieldItem(new Item.Settings().maxCount(1).component(DataComponentTypes.UNBREAKABLE, new UnbreakableComponent(true)), TitanFabricItems.LEGEND_INGOT));

    ArmorPlatingItem CITRIN_ARMOR_PLATING = register("citrin_armor_plating",
            new ArmorPlatingItem(new Item.Settings().maxCount(16), ArmorPlateType.CITRIN));

    ArmorPlatingItem DIAMOND_ARMOR_PLATING = register("diamond_armor_plating",
            new ArmorPlatingItem(new Item.Settings().maxCount(16), ArmorPlateType.DIAMOND));

    ArmorPlatingItem EMBER_ARMOR_PLATING = register("ember_armor_plating",
            new ArmorPlatingItem(new Item.Settings().maxCount(16).fireproof(), ArmorPlateType.EMBER));

    ArmorPlatingItem NETHERITE_ARMOR_PLATING = register("netherite_armor_plating",
            new ArmorPlatingItem(new Item.Settings().maxCount(16).fireproof(), ArmorPlateType.NETHERITE));

    ArmorPlatingItem LEGEND_ARMOR_PLATING = register("legend_armor_plating",
            new ArmorPlatingItem(new Item.Settings().maxCount(16), ArmorPlateType.LEGEND));


    ParachuteItem PARACHUTE = register("parachute",
            new ParachuteItem(new Item.Settings().maxCount(1)/*.maxDamage(250)*/.component(TitanFabricDataComponents.ACTIVATED, false)));

    CitrinStarItem CITRIN_STAR = register("citrin_star",
            new CitrinStarItem(new Item.Settings().maxCount(4)));

    FlintAndEmberItem FLINT_AND_EMBER = register("flint_and_ember",
            new FlintAndEmberItem(new Item.Settings().fireproof().maxCount(1).maxDamage(64)));

    EssenceItem ESSENCE = register("essence", new EssenceItem(
            new Item.Settings().maxCount(64)));

    Item SWORD_HANDLE = register("sword_handle",
            new Item(new Item.Settings().maxCount(64)));
    Item DIAMOND_APPLE = register("diamond_apple",
            new Item(new Item.Settings().maxCount(16)
                    .food((new FoodComponent.Builder()).nutrition(4).saturationModifier(1.2f).alwaysEdible()
                            .statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 2), 1.0F)
                            .statusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 1200, 1), 1.0F)
                            .build())));

    PotionBundleItem POTION_BUNDLE = register("potion_bundle", new BackPackItem(new Item.Settings().maxCount(1), BackPackItem.Type.POTION));

    BackPackItem BACKPACK_SMALL = register("backpack_small", new BackPackItem(new Item.Settings().maxCount(1), BackPackItem.Type.SMALL));

    BackPackItem BACKPACK_MEDIUM = register("backpack_medium", new BackPackItem(new Item.Settings().maxCount(1), BackPackItem.Type.MEDIUM));

    BackPackItem BACKPACK_BIG = register("backpack_big", new BackPackItem(new Item.Settings().maxCount(1), BackPackItem.Type.BIG));


    private static <T extends Item> T register(String name, T item) {
        T registeredEntry = Registry.register(Registries.ITEM, TitanFabric.getId(name), item);
        ALL_ITEMS.add(registeredEntry);
        return registeredEntry;
    }

    private static <T extends SwordItem> T registerEffectSword(String name, T item) {
        register(name, item);
        EFFECT_SWORDS.add(item);
        return item;
    }

    private static <T extends ArmorItem> T registerArmor(String name, T item, List<T> list) {
        register(name, item);
        list.add(item);
        return item;
    }


    static void initialize() {
        // static initialisation
    }

    private static int removeBaseDamage(int damage) {
        return damage - 1;
    }
}
