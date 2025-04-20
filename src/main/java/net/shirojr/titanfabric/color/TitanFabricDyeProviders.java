package net.shirojr.titanfabric.color;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.shirojr.titanfabric.item.custom.TitanFabricParachuteItem;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class TitanFabricDyeProviders {

    public static final String[] COLOR_KEYS = {
            "red", "orange", "blue", "gray", "lime", "pink", "purple",
            "light_blue", "light_gray", "yellow", "magenta",
            "cyan", "brown", "green", "black", "white"
    };

    public static void applyColorTooltip(List<Text> tooltip, ItemStack stack) {
        if (stack.hasNbt() && stack.getNbt() != null) {
            if (stack.getNbt().contains("red")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorRed"));
            } else if (stack.getNbt().contains("orange")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorOrange"));
            } else if (stack.getNbt().contains("blue")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorBlue"));
            } else if (stack.getNbt().contains("gray")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorGray"));
            } else if (stack.getNbt().contains("lime")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorLime"));
            } else if (stack.getNbt().contains("pink")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorPink"));
            } else if (stack.getNbt().contains("purple")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorPurple"));
            } else if (stack.getNbt().contains("light_blue")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorLightBlue"));
            } else if (stack.getNbt().contains("light_gray")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorLightGray"));
            } else if (stack.getNbt().contains("yellow")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorYellow"));
            } else if (stack.getNbt().contains("magenta")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorMagenta"));
            } else if (stack.getNbt().contains("cyan")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorCyan"));
            } else if (stack.getNbt().contains("brown")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorBrown"));
            } else if (stack.getNbt().contains("green")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorGreen"));
            } else if (stack.getNbt().contains("black")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorBlack"));
            } else if (stack.getNbt().contains("white")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorWhite"));
            } else {
                if(stack.getItem() instanceof TitanFabricParachuteItem) {
                    tooltip.add(new TranslatableText("tooltip.titanfabric.colorWhite"));
                }
            }
        } else {
            if(stack.getItem() instanceof TitanFabricParachuteItem) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorWhite"));
            }
        }
    }

    public static String getColorFromNBT(NbtCompound nbt) {
        if(nbt != null) {
            if (nbt.contains("red")) {
                return "red";
            } else if (nbt.contains("orange")) {
                return "orange";
            } else if (nbt.contains("blue")) {
                return "blue";
            } else if (nbt.contains("gray")) {
                return "gray";
            } else if (nbt.contains("lime")) {
                return "lime";
            } else if (nbt.contains("pink")) {
                return "pink";
            } else if (nbt.contains("purple")) {
                return "purple";
            } else if (nbt.contains("light_blue")) {
                return "light_blue";
            } else if (nbt.contains("light_gray")) {
                return "light_gray";
            } else if (nbt.contains("yellow")) {
                return "yellow";
            } else if (nbt.contains("magenta")) {
                return "magenta";
            } else if (nbt.contains("cyan")) {
                return "cyan";
            } else if (nbt.contains("brown")) {
                return "brown";
            } else if (nbt.contains("green")) {
                return "green";
            } else if (nbt.contains("black")) {
                return "black";
            }
        }
        return "white";
    }
}
