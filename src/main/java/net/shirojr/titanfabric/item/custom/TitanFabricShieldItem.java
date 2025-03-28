package net.shirojr.titanfabric.item.custom;


import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.shirojr.titanfabric.init.TitanFabricItemGroups;
import net.shirojr.titanfabric.util.items.Anvilable;

public class TitanFabricShieldItem extends ShieldItem implements Anvilable {

    private Item[] repairItems;

    public TitanFabricShieldItem(int maxDamage, int cooldownTicks, int enchantability, Item... repairItems) {
        super(new FabricItemSettings().maxDamage(maxDamage).group(TitanFabricItemGroups.TITAN));
        this.repairItems = repairItems;
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        for (Item repairItem : repairItems) {
            if (ingredient.getItem() == repairItem) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }
}

