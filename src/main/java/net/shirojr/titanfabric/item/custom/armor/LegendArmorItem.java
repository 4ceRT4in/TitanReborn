package net.shirojr.titanfabric.item.custom.armor;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;

public class LegendArmorItem extends ArmorItem {
    public LegendArmorItem(EquipmentSlot slot, Settings settings) {
        super(TitanFabricArmorMaterials.LEGEND, slot, settings);
    }
    @Override
    public boolean isDamageable() {
        return false;
    }
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }
}
