package net.shirojr.titanfabric.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.init.ConfigInit;
import net.shirojr.titanfabric.item.custom.TitanFabricArrowItem;
import net.shirojr.titanfabric.item.custom.TitanFabricEssenceItem;
import net.shirojr.titanfabric.item.custom.TitanFabricShootableItem;
import net.shirojr.titanfabric.item.custom.armor.CitrinArmorItem;
import net.shirojr.titanfabric.item.custom.armor.NetherArmorItem;
import net.shirojr.titanfabric.item.custom.armor.parts.LegendArmorBootsItem;
import net.shirojr.titanfabric.item.custom.armor.parts.LegendArmorChestplateItem;
import net.shirojr.titanfabric.item.custom.armor.parts.LegendArmorHelmetItem;
import net.shirojr.titanfabric.item.custom.armor.parts.LegendArmorLeggingsItem;
import net.shirojr.titanfabric.item.custom.bow.LegendBowItem;
import net.shirojr.titanfabric.item.custom.bow.MultiBowItem;
import net.shirojr.titanfabric.item.custom.material.TitanFabricToolMaterials;
import net.shirojr.titanfabric.item.custom.misc.CitrinStarItem;
import net.shirojr.titanfabric.item.custom.sword.*;

public class TitanFabricItems {
    //region armor
    public static final Item CITRIN_HELMET = registerItem("citrin_helmet", new CitrinArmorItem(EquipmentSlot.HEAD));
    public static final Item CITRIN_CHESTPLATE = registerItem("citrin_chestplate", new CitrinArmorItem(EquipmentSlot.CHEST));
    public static final Item CITRIN_LEGGINGS = registerItem("citrin_leggings", new CitrinArmorItem(EquipmentSlot.LEGS));
    public static final Item CITRIN_BOOTS = registerItem("citrin_boots", new CitrinArmorItem(EquipmentSlot.FEET));

    public static final Item NETHER_HELMET = registerItem("nether_helmet", new NetherArmorItem(EquipmentSlot.HEAD));
    public static final Item NETHER_CHESTPLATE = registerItem("nether_chestplate", new NetherArmorItem(EquipmentSlot.CHEST));
    public static final Item NETHER_LEGGINGS = registerItem("nether_leggings", new NetherArmorItem(EquipmentSlot.LEGS));
    public static final Item NETHER_BOOTS = registerItem("nether_boots", new NetherArmorItem(EquipmentSlot.FEET));

    public static final Item LEGEND_HELMET = registerItem("legend_helmet", new LegendArmorHelmetItem(ConfigInit.CONFIG.TitanArmorHelmetHealth));
    public static final Item LEGEND_CHESTPLATE = registerItem("legend_chestplate", new LegendArmorChestplateItem(ConfigInit.CONFIG.TitanArmorChestplateHealth));
    public static final Item LEGEND_LEGGINGS = registerItem("legend_leggings", new LegendArmorLeggingsItem(ConfigInit.CONFIG.TitanArmorLeggingsHealth));
    public static final Item LEGEND_BOOTS = registerItem("legend_boots", new LegendArmorBootsItem(ConfigInit.CONFIG.TitanArmorBootsHealth));
    //endregion

    public static final Item CITRIN_SWORD = registerItem("citrin_sword", new CitrinSwordItem(false, TitanFabricToolMaterials.CITRIN, 5, -2.4f));
    public static final Item CITRIN_GREATSWORD = registerItem("citrin_greatsword", new CitrinSwordItem(true, TitanFabricToolMaterials.CITRIN_GREAT, 5, -2.4f));
    public static final Item NETHER_SWORD = registerItem("nether_sword", new NetherSwordItem(false, TitanFabricToolMaterials.NETHER, 6, -2.4f));
    public static final Item NETHER_GREATSWORD = registerItem("nether_greatsword", new NetherSwordItem(true, TitanFabricToolMaterials.NETHER_GREAT, 6, -2.4f));
    public static final Item LEGEND_SWORD = registerItem("legend_sword", new LegendSwordItem(false, TitanFabricToolMaterials.LEGEND, 7, -2.4f));
    public static final Item LEGEND_GREATSWORD = registerItem("legend_greatsword", new LegendSwordItem(true, TitanFabricToolMaterials.LEGEND_GREAT, 7, -2.4f));

    public static final Item DIAMOND_GREATSWORD = registerItem("diamond_greatsword", new DiamondSwordItem(true, TitanFabricToolMaterials.LEGEND_GREAT, 7, -2.4f));
    public static final Item NETHERITE_GREATSWORD = registerItem("netherite_greatsword", new NetheriteSwordItem(true, TitanFabricToolMaterials.LEGEND_GREAT, 7, -2.4f));

    public static final Item LEGEND_SLINGSGOT = registerItem("legend_slingshot", new TitanFabricShootableItem());
    public static final Item LEGEND_BOW = registerItem("legend_bow", new LegendBowItem());
    public static final Item MULTI_BOW = registerItem("multi_bow", new MultiBowItem());
    public static final Item ARROW = registerItem("arrow", new TitanFabricArrowItem());

    //TODO: implement fabric shield lib
    //public static final Item DIAMOND_SHIELD = registerItem("diamond_shield", new DiamondShieldItem());
    //public static final Item LEGEND_SHIELD = registerItem("legend_shield", new LegendShieldItem());

    public static final Item CITRIN_STAR = registerItem("citrin_star", new CitrinStarItem());

    public static final Item CITRIN_INGOT = registerItem("citrin_ingot",
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
            new Item(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(64)));

    public static final Item BLINDNESS_ESSENCE = registerItem("blindness_essence",
            new TitanFabricEssenceItem("tooltip.titanfabric.blindnessEssenceItem"));
    public static final Item FIRE_ESSENCE = registerItem("fire_essence",
            new TitanFabricEssenceItem("tooltip.titanfabric.fireEssenceItem"));
    public static final Item POISON_ESSENCE = registerItem("poison_essence",
            new TitanFabricEssenceItem("tooltip.titanfabric.poisonEssenceItem"));
    public static final Item WEAKNESS_ESSENCE = registerItem("weakness_essence",
            new TitanFabricEssenceItem("tooltip.titanfabric.weaknessEssenceItem"));
    public static final Item WITHER_ESSENCE = registerItem("wither_essence",
            new TitanFabricEssenceItem("tooltip.titanfabric.witherEssenceItem"));

    public static final Item SWORD_HANDLE = registerItem("sword_handle",
            new Item(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(1)));
    public static final Item DIAMOND_APPLE = registerItem("diamond_apple",
            new Item(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(64).food((new net.minecraft.item.FoodComponent.Builder()).hunger(4).saturationModifier(10.0F).alwaysEdible().statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 2), 1.0F).statusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 1200, 1), 1.0F).build())));


    private static Item registerItem(String name, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(TitanFabric.MODID, name), item);
    }

    public static void registerModItems() {
        TitanFabric.devLogger("Registering " + TitanFabric.MODID + " Mod items");
    }

}
