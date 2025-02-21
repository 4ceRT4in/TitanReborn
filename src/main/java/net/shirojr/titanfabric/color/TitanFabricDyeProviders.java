package net.shirojr.titanfabric.color;

import net.minecraft.nbt.NbtCompound;

public class TitanFabricDyeProviders {

    public static final String[] COLOR_KEYS = {
            "red", "orange", "blue", "gray", "lime", "pink", "purple",
            "light_blue", "light_gray", "yellow", "magenta",
            "cyan", "brown", "green", "black", "white"
    };

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
