package net.shirojr.titanfabric.util.handler;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface ArrowSelectionHandler {
    Optional<Integer> titanfabric$getSelectedArrowIndex();
    void titanfabric$setSelectedArrowIndex(@Nullable ItemStack selectedArrowStack);
}
