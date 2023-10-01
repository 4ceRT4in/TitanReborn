package net.shirojr.titanfabric.item.custom;


import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.shirojr.titanfabric.item.TitanFabricItemGroups;

public class TitanFabricShieldItem extends ShieldItem {
    public TitanFabricShieldItem(int maxDamage, int cooldownTicks, int enchantability, Item... repairItems) {
        super(new FabricItemSettings().maxDamage(maxDamage).group(TitanFabricItemGroups.TITAN));
    }



    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }
}

