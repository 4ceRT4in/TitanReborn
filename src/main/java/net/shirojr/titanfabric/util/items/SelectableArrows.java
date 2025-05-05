package net.shirojr.titanfabric.util.items;

import net.minecraft.item.Item;
import net.shirojr.titanfabric.screen.custom.ArrowSelectionScreen;

import java.util.List;

/**
 * Items, which implement this Interface, can open the {@linkplain ArrowSelectionScreen ArrowSelectionScreen}.
 * This Screen is able to select Arrow Types.
 */
public interface SelectableArrows {
    List<Item> titanFabric$supportedArrows();

    //FIXME: check getProjectiles in RangedWeapon class
}
