package net.shirojr.titanfabric.util.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Items, which implement this Interface, can open the {@linkplain net.shirojr.titanfabric.screen.custom.ArrowSelectionScreen ArrowSelectionScreen}.
 * This Screen is able to select Arrow Types.
 */
public interface SelectableArrows {
    List<Item> supportedArrows();
}
