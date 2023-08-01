package net.shirojr.titanfabric.item.custom.bow;

import net.minecraft.item.ItemStack;
import net.minecraft.tag.ItemTags;
import net.shirojr.titanfabric.item.custom.TitanFabricBowItem;
import net.shirojr.titanfabric.util.TitanFabricTags;

import java.util.function.Predicate;

public class LegendBowItem extends TitanFabricBowItem {
    public static final Predicate<ItemStack> BOW_PROJECTILES = stack -> stack.isIn(TitanFabricTags.Items.ARROWS);
    public LegendBowItem() {
        super();
    }

    @Override
    public Predicate<ItemStack> getProjectiles() {
        return BOW_PROJECTILES;
    }
}
