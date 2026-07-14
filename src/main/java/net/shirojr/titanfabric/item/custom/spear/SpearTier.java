package net.shirojr.titanfabric.item.custom.spear;

import net.minecraft.item.ToolMaterial;
import net.shirojr.titanfabric.item.custom.material.TitanFabricToolMaterials;
import net.shirojr.titanfabric.util.effects.WeaponEffect;

import java.util.List;

/** Central, authoritative balancing data for every TitanFabric spear. */
public enum SpearTier {
    CITRIN(TitanFabricToolMaterials.CITRIN, 5, 8 * 20, 25, 1.25f, WeaponEffect.POISON, true),
    EMBER(TitanFabricToolMaterials.EMBER, 6, 7 * 20, 30, 1.50f, WeaponEffect.FIRE, true),
    DIAMOND(TitanFabricToolMaterials.DIAMOND, 6, 7 * 20, 30, 1.50f, null, true),
    TITAN(TitanFabricToolMaterials.LEGEND, 7, 6 * 20, 35, 1.75f, null, true),
    NETHERITE(TitanFabricToolMaterials.NETHERITE, 8, 5 * 20, 40, 2.00f, null, false);

    private final ToolMaterial material;
    private final int damage;
    private final int throwCooldown;
    private final int range;
    private final float rangeModifier;
    private final WeaponEffect innateEffect;
    private final boolean weaponEffects;

    SpearTier(ToolMaterial material, int damage, int throwCooldown, int range, float rangeModifier, WeaponEffect innateEffect, boolean weaponEffects) {
        this.material = material;
        this.damage = damage;
        this.throwCooldown = throwCooldown;
        this.range = range;
        this.rangeModifier = rangeModifier;
        this.innateEffect = innateEffect;
        this.weaponEffects = weaponEffects;
    }

    public ToolMaterial material() { return material; }
    public int damage() { return damage; }
    public int throwCooldown() { return throwCooldown; }
    public int range() { return range; }
    public float rangeModifier() { return rangeModifier; }
    public WeaponEffect innateEffect() { return innateEffect; }
    public boolean weaponEffects() { return weaponEffects; }
    public List<WeaponEffect> supportedEffects() { return weaponEffects ? List.of(WeaponEffect.values()) : List.of(); }
}
