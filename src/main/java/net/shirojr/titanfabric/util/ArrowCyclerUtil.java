package net.shirojr.titanfabric.util;

public enum ArrowCyclerUtil {
    Arrow("minecraft:arrow"),
    SPECTRAL_Arrow("minecraft:spectral_arrow");

    private final String id;

    private ArrowCyclerUtil(String id) {
        this.id = id;
    }

    public String getId() { return this.id; }
}
