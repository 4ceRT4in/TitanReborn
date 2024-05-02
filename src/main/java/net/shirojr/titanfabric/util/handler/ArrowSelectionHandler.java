package net.shirojr.titanfabric.util.handler;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface ArrowSelectionHandler {
    Optional<ItemStack> titanfabric$getSelectedArrow();
    void titanfabric$setSelectedArrow(@Nullable ItemStack selectedArrowStack);
}
