package net.shirojr.titanfabric.item.custom.bow;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.shirojr.titanfabric.init.TitanFabricDataComponents;
import net.shirojr.titanfabric.init.TitanFabricItemGroups;
import net.shirojr.titanfabric.init.TitanFabricItems;
import net.shirojr.titanfabric.item.custom.TitanFabricBowItem;
import net.shirojr.titanfabric.util.TitanFabricTags;
import net.shirojr.titanfabric.util.effects.WeaponEffectData;
import net.shirojr.titanfabric.util.items.Anvilable;

import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Predicate;

import static net.shirojr.titanfabric.util.effects.WeaponEffectType.INNATE_EFFECT;

public class LegendBowItem extends TitanFabricBowItem implements Anvilable {
    public static final Predicate<ItemStack> BOW_PROJECTILES = stack -> stack.isIn(TitanFabricTags.Items.DEFAULT_ARROWS);

    public LegendBowItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public List<Item> titanFabric$supportedArrows() {
        return List.of(Items.ARROW, Items.SPECTRAL_ARROW, TitanFabricItems.ARROW);
    }
}
