package net.shirojr.titanfabric.item.custom.sword;

import net.minecraft.item.ToolMaterial;
import net.shirojr.titanfabric.item.custom.TitanFabricSwordItem;
import net.shirojr.titanfabric.util.SwordType;
import net.shirojr.titanfabric.util.effects.WeaponEffect;

public class DiamondSwordItem extends TitanFabricSwordItem {
    public DiamondSwordItem(boolean hasWeaponEffects, ToolMaterial toolMaterial, int attackDamage, float attackSpeed,
                            SwordType swordType, WeaponEffect baseEffect, Settings settings) {
        super(hasWeaponEffects, toolMaterial, attackDamage, attackSpeed, swordType, baseEffect, settings);
    }
}
