package net.shirojr.titanfabric.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.item.custom.armor.CitrinArmorItem;
import net.shirojr.titanfabric.item.custom.armor.NetherArmorItem;
import net.shirojr.titanfabric.item.custom.armor.parts.LegendArmorBootsItem;
import net.shirojr.titanfabric.item.custom.armor.parts.LegendArmorChestplateItem;
import net.shirojr.titanfabric.item.custom.armor.parts.LegendArmorHelmetItem;
import net.shirojr.titanfabric.item.custom.armor.parts.LegendArmorLeggingsItem;

public class TitanFabricItems {
    //region citrin armor
    public static final Item CITRIN_HELMET = registerItem("citrin_helmet", new CitrinArmorItem(EquipmentSlot.HEAD));
    public static final Item CITRIN_CHESTPLATE = registerItem("citrin_chestplate", new CitrinArmorItem(EquipmentSlot.CHEST));
    public static final Item CITRIN_LEGGINGS = registerItem("citrin_leggings", new CitrinArmorItem(EquipmentSlot.LEGS));
    public static final Item CITRIN_BOOTS = registerItem("citrin_boots", new CitrinArmorItem(EquipmentSlot.FEET));
    //endregion

    //region nether armor
    public static final Item NETHER_HELMET = registerItem("nether_helmet", new NetherArmorItem(EquipmentSlot.HEAD));
    public static final Item NETHER_CHESTPLATE = registerItem("nether_chestplate", new NetherArmorItem(EquipmentSlot.CHEST));
    public static final Item NETHER_LEGGINGS = registerItem("nether_leggings", new NetherArmorItem(EquipmentSlot.LEGS));
    public static final Item NETHER_BOOTS = registerItem("nether_boots", new NetherArmorItem(EquipmentSlot.FEET));
    //endregion

    //region citrin armor
    public static final Item LEGEND_HELMET = registerItem("legend_helmet", new LegendArmorHelmetItem());
    public static final Item LEGEND_CHESTPLATE = registerItem("legend_chestplate", new LegendArmorChestplateItem());
    public static final Item LEGEND_LEGGINGS = registerItem("legend_leggings", new LegendArmorLeggingsItem());
    public static final Item LEGEND_BOOTS = registerItem("legend_boots", new LegendArmorBootsItem());
    //endregion

    public static final Item CITRIN_INGOT = registerItem("citrin_ingot",
            new Item(new FabricItemSettings().group(ItemGroup.MATERIALS).maxCount(64)));
    public static final Item NETHER_INGOT = registerItem("nether_ingot",
            new Item(new FabricItemSettings().group(ItemGroup.MATERIALS).maxCount(64)));
    public static final Item LEGEND_INGOT = registerItem("legend_ingot",
            new Item(new FabricItemSettings().group(ItemGroup.MATERIALS).maxCount(64)));


    private static Item registerItem(String name, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(TitanFabric.MODID, name), item);
    }

    public static void registerModItems() {
        TitanFabric.devLogger("Registering " + TitanFabric.MODID + " Mod items");
    }

}
