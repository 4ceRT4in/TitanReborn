package net.shirojr.titanfabric.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.render.entity.model.ShieldEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.item.custom.*;
import net.shirojr.titanfabric.item.custom.armor.ArmorPlatingItem;
import net.shirojr.titanfabric.item.custom.armor.CitrinArmorItem;
import net.shirojr.titanfabric.item.custom.armor.EmberArmorItem;
import net.shirojr.titanfabric.item.custom.armor.parts.LegendArmorBootsItem;
import net.shirojr.titanfabric.item.custom.armor.parts.LegendArmorChestplateItem;
import net.shirojr.titanfabric.item.custom.armor.parts.LegendArmorHelmetItem;
import net.shirojr.titanfabric.item.custom.armor.parts.LegendArmorLeggingsItem;
import net.shirojr.titanfabric.item.custom.bow.LegendBowItem;
import net.shirojr.titanfabric.item.custom.bow.MultiBowItem;
import net.shirojr.titanfabric.item.custom.bow.TitanCrossBowItem;
import net.shirojr.titanfabric.item.custom.material.TitanFabricToolMaterials;
import net.shirojr.titanfabric.item.custom.misc.*;
import net.shirojr.titanfabric.item.custom.sword.CitrinSwordItem;
import net.shirojr.titanfabric.item.custom.sword.DiamondSwordItem;
import net.shirojr.titanfabric.item.custom.sword.EmberSwordItem;
import net.shirojr.titanfabric.item.custom.sword.LegendSwordItem;
import net.shirojr.titanfabric.util.LoggerUtil;
import net.shirojr.titanfabric.util.SwordType;
import net.shirojr.titanfabric.util.effects.ArmorPlateType;

@SuppressWarnings("unused")
public class TitanFabricItems {
    //region armor

    public static final CitrinArmorItem CITRIN_HELMET = registerItem("citrin_helmet",
            new CitrinArmorItem(EquipmentSlot.HEAD));
    public static final CitrinArmorItem CITRIN_CHESTPLATE = registerItem("citrin_chestplate",
            new CitrinArmorItem(EquipmentSlot.CHEST));
    public static final CitrinArmorItem CITRIN_LEGGINGS = registerItem("citrin_leggings",
            new CitrinArmorItem(EquipmentSlot.LEGS));
    public static final CitrinArmorItem CITRIN_BOOTS = registerItem("citrin_boots",
            new CitrinArmorItem(EquipmentSlot.FEET));

    public static final EmberArmorItem EMBER_HELMET = registerItem("ember_helmet",
            new EmberArmorItem(EquipmentSlot.HEAD));
    public static final EmberArmorItem EMBER_CHESTPLATE = registerItem("ember_chestplate",
            new EmberArmorItem(EquipmentSlot.CHEST));
    public static final EmberArmorItem EMBER_LEGGINGS = registerItem("ember_leggings",
            new EmberArmorItem(EquipmentSlot.LEGS));
    public static final EmberArmorItem EMBER_BOOTS = registerItem("ember_boots",
            new EmberArmorItem(EquipmentSlot.FEET));

    public static final LegendArmorHelmetItem LEGEND_HELMET = registerItem("legend_helmet",
            new LegendArmorHelmetItem(2.0f));
    public static final LegendArmorChestplateItem LEGEND_CHESTPLATE = registerItem("legend_chestplate",
            new LegendArmorChestplateItem(4.0f));
    public static final LegendArmorLeggingsItem LEGEND_LEGGINGS = registerItem("legend_leggings",
            new LegendArmorLeggingsItem(3.0f));
    public static final LegendArmorBootsItem LEGEND_BOOTS = registerItem("legend_boots",
            new LegendArmorBootsItem(1.0f));
    //endregion

    public static final CitrinSwordItem CITRIN_SWORD = registerItem("citrin_sword",
            new CitrinSwordItem(true, TitanFabricToolMaterials.CITRIN, removeBaseDamage(6), -2.4f, SwordType.DEFAULT, new FabricItemSettings().group(TitanFabricItemGroups.TITAN)));
    public static final CitrinSwordItem CITRIN_GREATSWORD = registerItem("citrin_greatsword",
            new CitrinSwordItem(true, TitanFabricToolMaterials.CITRIN_GREAT, removeBaseDamage(7), -2.4f, SwordType.GREAT_SWORD, new FabricItemSettings().group(TitanFabricItemGroups.TITAN)));
    public static final EmberSwordItem EMBER_SWORD = registerItem("ember_sword",
            new EmberSwordItem(true, TitanFabricToolMaterials.EMBER, removeBaseDamage(7), -2.4f, SwordType.DEFAULT, new FabricItemSettings().group(TitanFabricItemGroups.TITAN)));
    public static final EmberSwordItem EMBER_GREATSWORD = registerItem("ember_greatsword",
            new EmberSwordItem(true, TitanFabricToolMaterials.EMBER_GREAT, removeBaseDamage(8), -2.4f, SwordType.GREAT_SWORD, new FabricItemSettings().group(TitanFabricItemGroups.TITAN)));
    public static final LegendSwordItem LEGEND_SWORD = registerItem("legend_sword",
            new LegendSwordItem(true, TitanFabricToolMaterials.LEGEND, removeBaseDamage(8), -2.4f, SwordType.DEFAULT, new FabricItemSettings().group(TitanFabricItemGroups.TITAN)));
    public static final LegendSwordItem LEGEND_GREATSWORD = registerItem("legend_greatsword",
            new LegendSwordItem(true, TitanFabricToolMaterials.LEGEND_GREAT, removeBaseDamage(9), -2.4f, SwordType.GREAT_SWORD, new FabricItemSettings().group(TitanFabricItemGroups.TITAN)));
    public static final DiamondSwordItem DIAMOND_SWORD = registerItem("diamond_sword",
            new DiamondSwordItem(true, TitanFabricToolMaterials.DIAMOND, 6, -2.4f, SwordType.DEFAULT, null, new FabricItemSettings().group(TitanFabricItemGroups.TITAN)));
    public static final TitanFabricSwordItem DIAMOND_GREATSWORD = registerItem("diamond_greatsword",
            new TitanFabricSwordItem(true, TitanFabricToolMaterials.DIAMOND_GREAT, removeBaseDamage(8), -2.4f, SwordType.GREAT_SWORD, null, new FabricItemSettings().group(TitanFabricItemGroups.TITAN)));
    public static final SwordItem NETHERITE_SWORD = new SwordItem(TitanFabricToolMaterials.NETHERITE, removeBaseDamage(9), -2.4f,
            new FabricItemSettings().maxDamage(2031).group(ItemGroup.COMBAT).fireproof());  // registered in ItemsMixin class
    public static final TitanFabricSwordItem NETHERITE_GREATSWORD = registerItem("netherite_greatsword",
            new TitanFabricSwordItem(false, TitanFabricToolMaterials.NETHERITE_GREAT, removeBaseDamage(10), -2.4f, SwordType.GREAT_SWORD, null, new FabricItemSettings().group(TitanFabricItemGroups.TITAN).fireproof()));

    public static final TitanCrossBowItem TITAN_CROSSBOW = registerItem("legend_crossbow", new TitanCrossBowItem(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(1).maxDamage(-1)));
    public static final LegendBowItem LEGEND_BOW = registerItem("legend_bow", new LegendBowItem());
    public static final MultiBowItem MULTI_BOW_1 = registerItem("multi_bow_1", new MultiBowItem(1, 10, 500));
    public static final MultiBowItem MULTI_BOW_2 = registerItem("multi_bow_2", new MultiBowItem(2, 20, 1000));
    public static final MultiBowItem MULTI_BOW_3 = registerItem("multi_bow_3", new MultiBowItem(3, 30, 1500));

    public static final TitanFabricArrowItem ARROW = registerItem("effect_arrow",
            new TitanFabricArrowItem(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(32)));

    public static final TitanFabricShieldItem DIAMOND_SHIELD = registerItem("diamond_shield",
            new TitanFabricShieldItem(1685, 60, 1,false, Items.DIAMOND));
    public static final TitanFabricShieldItem NETHERITE_SHIELD = registerItem("netherite_shield",
            new TitanFabricShieldItem(1685*2, 60, 16,true, Items.NETHERITE_INGOT));
    public static final TitanFabricShieldItem LEGEND_SHIELD = registerItem("legend_shield",
            new TitanFabricShieldItem(-1, 40, 24,false, TitanFabricItems.LEGEND_INGOT));

    public static final CitrinStarItem CITRIN_STAR = registerItem("citrin_star",
            new CitrinStarItem(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(4)));

    public static final Item CITRIN_SHARD = registerItem("citrin_shard",
            new Item(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(64)));
    public static final Item EMBER_INGOT = registerItem("ember_ingot",
            new Item(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(64)));
    public static final Item LEGEND_INGOT = registerItem("legend_ingot",
            new Item(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(64)));
    public static final Item LEGEND_POWDER = registerItem("legend_powder",
            new Item(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(64)));
    public static final Item EMBER_SHARD = registerItem("ember_shard",
            new Item(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(64)));

    public static final TitanFabricParachuteItem PARACHUTE = registerItem("parachute",
            new TitanFabricParachuteItem(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(1)));

    public static final FlintAndEmberItem FLINT_AND_EMBER = registerItem("flint_and_ember",
            new FlintAndEmberItem(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(1).maxDamage(64)));

    public static final ArmorPlatingItem CITRIN_ARMOR_PLATING = registerItem("citrin_armor_plating",
            new ArmorPlatingItem(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(16), ArmorPlateType.CITRIN));

    public static final ArmorPlatingItem DIAMOND_ARMOR_PLATING = registerItem("diamond_armor_plating",
            new ArmorPlatingItem(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(16), ArmorPlateType.DIAMOND));

    public static final ArmorPlatingItem EMBER_ARMOR_PLATING = registerItem("ember_armor_plating",
            new ArmorPlatingItem(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(16), ArmorPlateType.EMBER));

    public static final ArmorPlatingItem NETHERITE_ARMOR_PLATING = registerItem("netherite_armor_plating",
            new ArmorPlatingItem(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(16).fireproof(), ArmorPlateType.NETHERITE));

    public static final ArmorPlatingItem LEGEND_ARMOR_PLATING = registerItem("legend_armor_plating",
            new ArmorPlatingItem(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(16), ArmorPlateType.LEGEND));

    public static final TitanFabricEssenceItem ESSENCE = registerItem("essence", new TitanFabricEssenceItem());
    public static final CutPotion CUT_POTION = registerItem("cut_potion", new CutPotion(new FabricItemSettings()));

    public static final Item SWORD_HANDLE = registerItem("sword_handle",
            new Item(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(64)));
    public static final Item DIAMOND_APPLE = registerItem("diamond_apple",
            new Item(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(16)
                    .food((new FoodComponent.Builder()).hunger(4).saturationModifier(1.2f).alwaysEdible()
                            .statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 2), 1.0F)
                            .statusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 1200, 1), 1.0F)
                            .build())));

    public static final BackPackItem POTION_BUNDLE  = registerItem("potion_bundle",
            new BackPackItem(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(1), BackPackItem.Type.POTION));

    public static final BackPackItem BACKPACK_SMALL = registerItem("backpack_small",
            new BackPackItem(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(1), BackPackItem.Type.SMALL));

    public static final BackPackItem BACKPACK_MEDIUM = registerItem("backpack_medium",
            new BackPackItem(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(1), BackPackItem.Type.MEDIUM));

    public static final BackPackItem BACKPACK_BIG = registerItem("backpack_big",
            new BackPackItem(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(1), BackPackItem.Type.BIG));

    private static <T extends Item> T registerItem(String name, T item) {
        return Registry.register(Registry.ITEM, new Identifier(TitanFabric.MODID, name), item);
    }

    public static void registerModItems() {
        LoggerUtil.devLogger("Registering " + TitanFabric.MODID + " Mod items");
    }

    private static int removeBaseDamage(int damage) {
        return damage - 1;
    }
}
