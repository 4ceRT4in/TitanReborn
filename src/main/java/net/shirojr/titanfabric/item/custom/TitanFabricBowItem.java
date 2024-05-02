package net.shirojr.titanfabric.item.custom;

import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.ItemTags;
import net.shirojr.titanfabric.util.items.SelectableArrows;

import java.util.function.Predicate;

public abstract class TitanFabricBowItem extends BowItem implements SelectableArrows {

    public TitanFabricBowItem(Settings settings) {
        super(settings);
    }

    @Override
    public Predicate<ItemStack> getProjectiles() {
        return stack -> {
            for (Item supportedArrowItem : this.supportedArrows()) {
                if (stack.getItem().equals(supportedArrowItem)) {
                    return true;
                }
            }
            return stack.isIn(ItemTags.ARROWS);
        };
    }
}
