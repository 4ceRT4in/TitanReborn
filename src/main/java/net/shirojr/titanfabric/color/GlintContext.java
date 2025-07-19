package net.shirojr.titanfabric.color;

import net.minecraft.util.DyeColor;

public class GlintContext {
    private static final ThreadLocal<DyeColor> COLOR = ThreadLocal.withInitial(() -> null); //saves for current Thread a DyeColor, which is being set for the Item which needs to be rendered

    public static void setColor(DyeColor color) { COLOR.set(color); }
    public static DyeColor getColor() { return COLOR.get(); }
    public static void clear() { COLOR.remove(); }
}