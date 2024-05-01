package net.shirojr.titanfabric.util;

public enum SwordType{
    DEFAULT(1.2f, 0),
    GREAT_SWORD(1.5f, 5);

    private final float critMultiplier;
    private final int cooldownTicks;

    SwordType(float critMultiplier, int cooldownTicks) {
        this.critMultiplier = critMultiplier;
        this.cooldownTicks = cooldownTicks;
    }

    public float getCritMultiplier() {
        return critMultiplier;
    }

    public int getCooldownTicks() {
        return cooldownTicks;
    }
}