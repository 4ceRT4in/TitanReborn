package net.shirojr.titanfabric.item.custom.sword;

import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterial;
import net.shirojr.titanfabric.item.custom.TitanFabricSwordItem;
import net.shirojr.titanfabric.util.effects.WeaponEffect;

public class EmberSwordItem extends TitanFabricSwordItem {
    public EmberSwordItem(boolean hasWeaponEffects, ToolMaterial toolMaterial, int attackDamage, float attackSpeed,
                          float critMultiplier, Item.Settings settings) {
        super(hasWeaponEffects, toolMaterial, attackDamage, attackSpeed, critMultiplier, WeaponEffect.FIRE, settings);
    }
}
