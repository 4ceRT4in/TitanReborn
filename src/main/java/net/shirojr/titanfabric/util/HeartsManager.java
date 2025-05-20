package net.shirojr.titanfabric.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class HeartsManager {
    private static boolean frozenHearts = false;

    public static boolean isFrozenHearts() {
        return frozenHearts;
    }

    public static void setFrozenHearts(boolean extendedHearts) {
        HeartsManager.frozenHearts = extendedHearts;
    }
}
