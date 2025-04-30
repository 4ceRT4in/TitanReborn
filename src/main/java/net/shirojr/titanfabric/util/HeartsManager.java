package net.shirojr.titanfabric.util;

public abstract class HeartsManager {
    private static boolean frozenHearts = false;

    public static boolean isFrozenHearts() {
        return frozenHearts;
    }

    public static void setFrozenHearts(boolean extendedHearts) {
        HeartsManager.frozenHearts = extendedHearts;
    }
}
