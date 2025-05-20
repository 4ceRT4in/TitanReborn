package net.shirojr.titanfabric.util.effects;

public enum ArmorPlateType {
    CITRIN("citrin_armor_plating"),
    DIAMOND("diamond_armor_plating"),
    EMBER("ember_armor_plating"),
    NETHERITE("netherite_armor_plating"),
    LEGEND("legend_armor_plating");

    private final String id;

    ArmorPlateType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
