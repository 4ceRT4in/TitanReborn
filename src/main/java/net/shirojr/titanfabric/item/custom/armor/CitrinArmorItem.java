package net.shirojr.titanfabric.item.custom.armor;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemGroup;

public class CitrinArmorItem extends ArmorItem {
    public CitrinArmorItem(EquipmentSlot slot) {
        super(TitanFabricArmorMaterials.CITRIN, slot, new FabricItemSettings().group(ItemGroup.COMBAT));
    }
}
