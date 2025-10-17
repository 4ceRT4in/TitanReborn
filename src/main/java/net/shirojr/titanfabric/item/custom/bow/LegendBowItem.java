package net.shirojr.titanfabric.item.custom.bow;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.shirojr.titanfabric.color.TitanFabricDyeProviders;
import net.shirojr.titanfabric.init.TitanFabricItems;
import net.shirojr.titanfabric.init.TitanFabricTags;
import net.shirojr.titanfabric.item.custom.TitanFabricBowItem;
import net.shirojr.titanfabric.util.items.Anvilable;

import java.util.List;
import java.util.function.Predicate;

public class LegendBowItem extends TitanFabricBowItem implements Anvilable {
    public static final Predicate<ItemStack> BOW_PROJECTILES = stack -> stack.isIn(TitanFabricTags.Items.DEFAULT_ARROWS);

    public LegendBowItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public Predicate<ItemStack> getProjectiles() {
        return stack -> {
            for (Item supportedArrowItem : this.titanFabric$supportedArrows()) {
                if (stack.getItem().equals(supportedArrowItem)) {
                    return true;
                }
            }
            return false;
        };
    }

    @Override
    public List<Item> titanFabric$supportedArrows() {
        return List.of(Items.ARROW, Items.SPECTRAL_ARROW, TitanFabricItems.EFFECT_ARROW);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        TitanFabricDyeProviders.applyExtendedTooltip(tooltip, "tooltip.titanfabric.legendBowKeybind");
    }

}
