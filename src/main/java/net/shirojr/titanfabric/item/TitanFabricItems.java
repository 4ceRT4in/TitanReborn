package net.shirojr.titanfabric.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.item.custom.*;
import net.shirojr.titanfabric.item.custom.armor.CitrinArmorItem;
import net.shirojr.titanfabric.item.custom.armor.NetherArmorItem;
import net.shirojr.titanfabric.item.custom.armor.parts.LegendArmorBootsItem;
import net.shirojr.titanfabric.item.custom.armor.parts.LegendArmorChestplateItem;
import net.shirojr.titanfabric.item.custom.armor.parts.LegendArmorHelmetItem;
import net.shirojr.titanfabric.item.custom.armor.parts.LegendArmorLeggingsItem;
import net.shirojr.titanfabric.item.custom.bow.LegendBowItem;
import net.shirojr.titanfabric.item.custom.bow.MultiBowItem;
import net.shirojr.titanfabric.item.custom.bow.TitanCrossBowItem;
import net.shirojr.titanfabric.item.custom.material.TitanFabricToolMaterials;
import net.shirojr.titanfabric.item.custom.misc.BackPackItem;
import net.shirojr.titanfabric.item.custom.misc.CitrinStarItem;
import net.shirojr.titanfabric.item.custom.sword.CitrinSwordItem;
import net.shirojr.titanfabric.item.custom.sword.LegendSwordItem;
import net.shirojr.titanfabric.item.custom.sword.NetherSwordItem;
import net.shirojr.titanfabric.util.LoggerUtil;
import net.shirojr.titanfabric.util.items.ArrowSelectionHelper;

public class TitanFabricItems {
    //region armor
    public static final Item CITRIN_HELMET = registerItem("citrin_helmet",
            new CitrinArmorItem(EquipmentSlot.HEAD));
    public static final Item CITRIN_CHESTPLATE = registerItem("citrin_chestplate",
            new CitrinArmorItem(EquipmentSlot.CHEST));
    public static final Item CITRIN_LEGGINGS = registerItem("citrin_leggings",
            new CitrinArmorItem(EquipmentSlot.LEGS));
    public static final Item CITRIN_BOOTS = registerItem("citrin_boots",
            new CitrinArmorItem(EquipmentSlot.FEET));

    public static final Item NETHER_HELMET = registerItem("nether_helmet",
            new NetherArmorItem(EquipmentSlot.HEAD));
    public static final Item NETHER_CHESTPLATE = registerItem("nether_chestplate",
            new NetherArmorItem(EquipmentSlot.CHEST));
    public static final Item NETHER_LEGGINGS = registerItem("nether_leggings",
            new NetherArmorItem(EquipmentSlot.LEGS));
    public static final Item NETHER_BOOTS = registerItem("nether_boots",
            new NetherArmorItem(EquipmentSlot.FEET));

    public static final Item LEGEND_HELMET = registerItem("legend_helmet",
            new LegendArmorHelmetItem(2.0f));
    public static final Item LEGEND_CHESTPLATE = registerItem("legend_chestplate",
            new LegendArmorChestplateItem(4.0f));
    public static final Item LEGEND_LEGGINGS = registerItem("legend_leggings",
            new LegendArmorLeggingsItem(3.0f));
    public static final Item LEGEND_BOOTS = registerItem("legend_boots",
            new LegendArmorBootsItem(1.0f));
    //endregion

    public static final Item CITRIN_SWORD = registerItem("citrin_sword",
            new CitrinSwordItem(true, TitanFabricToolMaterials.CITRIN, removeBaseDamage(6), -2.4f, new FabricItemSettings().group(TitanFabricItemGroups.TITAN)));
    public static final Item CITRIN_GREATSWORD = registerItem("citrin_greatsword",
            new CitrinSwordItem(true, TitanFabricToolMaterials.CITRIN_GREAT, removeBaseDamage(7), -2.4f, new FabricItemSettings().group(TitanFabricItemGroups.TITAN)));
    public static final Item NETHER_SWORD = registerItem("nether_sword",
            new NetherSwordItem(true, TitanFabricToolMaterials.NETHER, removeBaseDamage(7), -2.4f, new FabricItemSettings().group(TitanFabricItemGroups.TITAN)));
    public static final Item NETHER_GREATSWORD = registerItem("nether_greatsword",
            new NetherSwordItem(true, TitanFabricToolMaterials.NETHER_GREAT, removeBaseDamage(8), -2.4f, new FabricItemSettings().group(TitanFabricItemGroups.TITAN)));
    public static final Item LEGEND_SWORD = registerItem("legend_sword",
            new LegendSwordItem(true, TitanFabricToolMaterials.LEGEND, removeBaseDamage(8), -2.4f, new FabricItemSettings().group(TitanFabricItemGroups.TITAN)));
    public static final Item LEGEND_GREATSWORD = registerItem("legend_greatsword",
            new LegendSwordItem(true, TitanFabricToolMaterials.LEGEND_GREAT, removeBaseDamage(9), -2.4f, new FabricItemSettings().group(TitanFabricItemGroups.TITAN)));
    public static final SwordItem NETHERITE_SWORD = new SwordItem(TitanFabricToolMaterials.NETHERITE, removeBaseDamage(9), -2.4f,
            new FabricItemSettings().maxDamage(2031).group(ItemGroup.COMBAT).fireproof());  // registered in ItemsMixin class

    public static final Item DIAMOND_GREATSWORD = registerItem("diamond_greatsword",
            new TitanFabricSwordItem(true, TitanFabricToolMaterials.DIAMOND, removeBaseDamage(8), -2.4f, null, new FabricItemSettings().group(TitanFabricItemGroups.TITAN)));
    public static final Item NETHERITE_GREATSWORD = registerItem("netherite_greatsword",
            new TitanFabricSwordItem(false, TitanFabricToolMaterials.NETHERITE, removeBaseDamage(10), -2.4f, null, new FabricItemSettings().group(TitanFabricItemGroups.TITAN).fireproof()));

    public static final Item TITAN_CROSSBOW = registerItem("legend_crossbow", new TitanCrossBowItem(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(1).maxDamage(-1)));
    public static final Item LEGEND_BOW = registerItem("legend_bow", new LegendBowItem());
    public static final Item MULTI_BOW_1 = registerItem("multi_bow_1", new MultiBowItem(1, 20, 500));
    public static final Item MULTI_BOW_2 = registerItem("multi_bow_2", new MultiBowItem(2, 40, 1000));
    public static final Item MULTI_BOW_3 = registerItem("multi_bow_3", new MultiBowItem(3, 60, 1500));

    public static final Item ARROW = registerItem("effect_arrow", new TitanFabricArrowItem(ArrowSelectionHelper.ArrowType.POTION_PROJECTILE));
    //  public static final Item ARROW = registerItem("effect_arrow", new TitanFabricArrowItem(ArrowSelectionHelper.ArrowType.));

    public static final Item DIAMOND_SHIELD = registerItem("diamond_shield",
            new TitanFabricShieldItem(1685, 60, 14, Items.DIAMOND));
    public static final Item LEGEND_SHIELD = registerItem("legend_shield",
            new TitanFabricShieldItem(-1, 40, 24, TitanFabricItems.LEGEND_INGOT));

    public static final Item CITRIN_STAR = registerItem("citrin_star", new CitrinStarItem());

    public static final Item CITRIN_SHARD = registerItem("citrin_shard",
            new Item(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(64)));
    public static final Item NETHER_INGOT = registerItem("nether_ingot",
            new Item(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(64)));
    public static final Item LEGEND_INGOT = registerItem("legend_ingot",
            new Item(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(64)));
    public static final Item LEGEND_POWDER = registerItem("legend_powder",
            new Item(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(64)));
    public static final Item NETHER_SHARD = registerItem("nether_shard",
            new Item(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(64)));
    public static final Item LEGEND_CRYSTAL = registerItem("legend_crystal",
            new Item(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(64)));

    public static final Item PARACHUTE = registerItem("parachute",
            new TitanFabricParachuteItem(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(64)));

    public static final Item ESSENCE = registerItem("essence", new TitanFabricEssenceItem());

    public static final Item SWORD_HANDLE = registerItem("sword_handle",
            new Item(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(1)));
    public static final Item DIAMOND_APPLE = registerItem("diamond_apple",
            new Item(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(64)
                    .food((new FoodComponent.Builder()).hunger(4).saturationModifier(1.2f).alwaysEdible()
                            .statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 2), 1.0F)
                            .statusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 1200, 1), 1.0F)
                            .build())));

    public static final Item BACKPACK_SMALL = registerItem("backpack_small",
            new BackPackItem(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(1), BackPackItem.Type.SMALL));

    public static final Item BACKPACK_MEDIUM = registerItem("backpack_medium",
            new BackPackItem(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(1), BackPackItem.Type.MEDIUM));

    public static final Item BACKPACK_BIG = registerItem("backpack_big",
            new BackPackItem(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(1), BackPackItem.Type.BIG));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(TitanFabric.MODID, name), item);
    }

    public static void registerModItems() {
        LoggerUtil.devLogger("Registering " + TitanFabric.MODID + " Mod items");
    }

    private static int removeBaseDamage(int damage) {
        return damage - 1;
    }
}
