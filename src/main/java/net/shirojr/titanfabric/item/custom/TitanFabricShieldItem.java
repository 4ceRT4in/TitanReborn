package net.shirojr.titanfabric.item.custom;


import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.shirojr.titanfabric.util.items.Anvilable;

public class TitanFabricShieldItem extends ShieldItem implements Anvilable {

    private final Item[] repairItems;

    public TitanFabricShieldItem(Item.Settings settings, Item... repairItems) {
        super(settings);
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

