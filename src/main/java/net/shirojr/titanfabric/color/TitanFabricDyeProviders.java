package net.shirojr.titanfabric.color;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.shirojr.titanfabric.item.custom.misc.ParachuteItem;

import java.util.List;

public class TitanFabricDyeProviders {

    public static void applyExtendedTooltip(List<Text> tooltip, String translateKey) {
        String key = I18n.translate(translateKey);
        for (String line : key.split("\\\\n")) {
            tooltip.add(Text.of(line));
        }
    }

    public static void applyColorTooltip(List<Text> tooltip, ItemStack stack) {
        var comp = stack.get(DataComponentTypes.BASE_COLOR);
        if (comp != null) {
            for (DyeColor dyes : DyeColor.values()) {
                if (comp.getName().equals(dyes.getName())) {
                    tooltip.add(Text.translatable("tooltip.titanfabric.color." + dyes.getName()));
                    return;
                }
            }
        }

        if (stack.getItem() instanceof ParachuteItem) {
            tooltip.add(Text.translatable("tooltip.titanfabric.color.white"));
        }
    }

    public static String getColorFromNBT(ItemStack stack) {
        var comp = stack.get(DataComponentTypes.BASE_COLOR);
        if (comp != null) {
            for (DyeColor dyes : DyeColor.values()) {
                if (comp.getName().equals(dyes.getName())) {
                    return dyes.getName();
                }
            }
        }
        return "white";
    }
}
