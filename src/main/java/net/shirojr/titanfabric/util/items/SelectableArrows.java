package net.shirojr.titanfabric.util.items;

import net.minecraft.item.Item;

import java.util.List;

/**
 * Items, which implement this Interface, can open the {@linkplain net.shirojr.titanfabric.screen.screen.ArrowSelectionScreen ArrowSelectionScreen}.
 * This Screen is able to select Arrow Types.
 */
public interface SelectableArrows {
    List<Item> supportedArrows();
}
