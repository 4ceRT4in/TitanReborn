package net.shirojr.titanfabric.item.custom;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.shirojr.titanfabric.item.TitanFabricItemGroups;
import net.shirojr.titanfabric.util.effects.EffectHelper;

public class TitanFabricArrowItem extends ArrowItem {
    public TitanFabricArrowItem() {
        super(new FabricItemSettings().group(TitanFabricItemGroups.TITAN));
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        super.appendStacks(group, EffectHelper.generateAllEffectVersionStacks(this, stacks));
    }
}
