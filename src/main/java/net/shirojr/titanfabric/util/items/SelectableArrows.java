package net.shirojr.titanfabric.util.items;

import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import net.shirojr.titanfabric.screen.custom.ArrowSelectionScreen;
import net.shirojr.titanfabric.util.TitanFabricTags;

import java.util.List;

/**
 * Items, which implement this Interface, can open the {@linkplain ArrowSelectionScreen ArrowSelectionScreen}.
 * This Screen is able to select Arrow Types.
 */
public interface SelectableArrows {
    default List<Item> supportedArrows() {
        return Registry.ITEM.stream().filter(item -> item.getDefaultStack().isIn(TitanFabricTags.Items.DEFAULT_ARROWS)).toList();
    }
}
