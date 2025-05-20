package net.shirojr.titanfabric.color;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.shirojr.titanfabric.item.custom.TitanFabricParachuteItem;

import java.util.List;
import java.util.Map;

public class TitanFabricDyeProviders {

    public static void applyColorTooltip(List<Text> tooltip, ItemStack stack) {
        if (stack.hasNbt()) {
            NbtCompound nbt = stack.getNbt();

            for (DyeColor dyes : DyeColor.values()) {
                assert nbt != null;
                if (nbt.contains(dyes.getName())) {
                    tooltip.add(new TranslatableText("tooltip.titanfabric.color." + dyes.getName()));
                    return;
                }
            }
        }

        if (stack.getItem() instanceof TitanFabricParachuteItem) {
            tooltip.add(new TranslatableText("tooltip.titanfabric.color.white"));
        }
    }

    public static String getColorFromNBT(NbtCompound nbt) {
        if (nbt != null) {
            for (DyeColor dyes : DyeColor.values()) {
                if (nbt.contains(dyes.getName())) {
                    return dyes.getName();
                }
            }
        }
        return "white";
    }
}
