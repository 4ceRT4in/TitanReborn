package net.shirojr.titanfabric.item.custom;


import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.shirojr.titanfabric.init.TitanFabricItems;
import net.shirojr.titanfabric.util.items.Anvilable;

import java.util.List;

public class TitanFabricShieldItem extends ShieldItem implements Anvilable {

    private final Item[] repairItems;

    public TitanFabricShieldItem(Item.Settings settings, Item... repairItems) {
        super(settings);
        this.repairItems = repairItems;
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        if (stack.getItem() == TitanFabricItems.NETHERITE_SHIELD) {
            tooltip.add(Text.translatable("tooltip.titanfabric.netherite_shield"));
        }
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

