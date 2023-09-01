package net.shirojr.titanfabric.item.custom.bow;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tag.ItemTags;
import net.shirojr.titanfabric.item.custom.TitanFabricBowItem;
import net.shirojr.titanfabric.util.TitanFabricTags;
import net.shirojr.titanfabric.util.items.SelectableArrows;

import java.util.List;
import java.util.function.Predicate;

public class LegendBowItem extends TitanFabricBowItem implements SelectableArrows {
    public static final Predicate<ItemStack> BOW_PROJECTILES = stack -> stack.isIn(TitanFabricTags.Items.ARROWS);
    public LegendBowItem() {
        super();
    }

    @Override
    public List<Item> supportedArrows() {
        return List.of(Items.ARROW, Items.SPECTRAL_ARROW);
    }
}
