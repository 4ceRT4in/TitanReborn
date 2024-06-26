package net.shirojr.titanfabric.item.custom.bow;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.shirojr.titanfabric.item.TitanFabricItemGroups;
import net.shirojr.titanfabric.item.TitanFabricItems;
import net.shirojr.titanfabric.item.custom.TitanFabricBowItem;
import net.shirojr.titanfabric.util.TitanFabricTags;
import net.shirojr.titanfabric.util.items.Anvilable;

import java.util.List;
import java.util.function.Predicate;

public class LegendBowItem extends TitanFabricBowItem implements Anvilable {
    public static final Predicate<ItemStack> BOW_PROJECTILES = stack -> stack.isIn(TitanFabricTags.Items.DEFAULT_ARROWS);

    public LegendBowItem() {
        super(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(1).maxDamage(-1));
    }

    @Override
    public List<Item> titanFabric$supportedArrows() {
        return List.of(Items.ARROW, Items.SPECTRAL_ARROW, TitanFabricItems.ARROW);
    }
}
