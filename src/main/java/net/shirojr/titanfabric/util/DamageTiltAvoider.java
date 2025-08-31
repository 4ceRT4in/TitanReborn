package net.shirojr.titanfabric.util;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface DamageTiltAvoider {
    @Nullable ItemStack titanReborn$getArmorStackBuffer();

    void titanReborn$setArmorStackBuffer(@Nullable ItemStack stack);
}
